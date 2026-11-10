package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.*;
import kh.edu.istad.platform.iam.domain.service.EffectivePermissionCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class GetUserAuthoritiesUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public GetUserAuthoritiesUseCase(UserRepository userRepository,
                                     RoleRepository roleRepository,
                                     PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public UserAuthoritiesResult execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + userUuid));

        Instant now = Instant.now();

        // 1. Find active user roles
        List<UserRole> userRoles = userRepository.findRolesByUserId(user.getId());
        List<UserAuthoritiesResult.RoleAuthority> roleAuthorities = new ArrayList<>();
        Set<String> rolePermissionNames = new LinkedHashSet<>();

        for (UserRole ur : userRoles) {
            if (ur.isActive(now)) {
                Optional<Role> roleOpt = roleRepository.findById(ur.getRoleId())
                        .filter(r -> !r.isDeleted() && r.isActive());
                if (roleOpt.isPresent()) {
                    Role role = roleOpt.get();
                    roleAuthorities.add(new UserAuthoritiesResult.RoleAuthority(role.getUuid(), role.getName(), ur.getExpiresAt()));

                    // Role permissions
                    List<RolePermission> rolePerms = roleRepository.findPermissionsByRoleId(role.getId());
                    for (RolePermission rp : rolePerms) {
                        if (rp.isActive(now)) {
                            permissionRepository.findById(rp.getPermissionId())
                                    .filter(p -> !p.isDeleted() && p.isActive())
                                    .ifPresent(p -> rolePermissionNames.add(p.getName()));
                        }
                    }
                }
            }
        }

        // 2. Find direct user permissions
        List<UserPermission> userPermissions = userRepository.findPermissionsByUserId(user.getId());
        List<UserAuthoritiesResult.DirectPermissionAuthority> directAuthorities = new ArrayList<>();
        Map<String, PermissionEffect> directMap = new LinkedHashMap<>();

        for (UserPermission up : userPermissions) {
            if (up.isActive(now)) {
                Optional<Permission> permOpt = permissionRepository.findById(up.getPermissionId())
                        .filter(p -> !p.isDeleted() && p.isActive());
                if (permOpt.isPresent()) {
                    Permission perm = permOpt.get();
                    directAuthorities.add(new UserAuthoritiesResult.DirectPermissionAuthority(perm.getName(), up.getEffect()));
                    directMap.put(perm.getName(), up.getEffect());
                }
            }
        }

        // 3. Calculate effective permissions
        Set<String> effectivePermissions = EffectivePermissionCalculator.calculate(rolePermissionNames, directMap);

        return new UserAuthoritiesResult(roleAuthorities, directAuthorities, effectivePermissions);
    }
}
