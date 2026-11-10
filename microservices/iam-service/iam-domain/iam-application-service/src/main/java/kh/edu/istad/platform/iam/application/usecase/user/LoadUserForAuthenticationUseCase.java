package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.AuthUserResult;
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
public class LoadUserForAuthenticationUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public LoadUserForAuthenticationUseCase(UserRepository userRepository,
                                           RoleRepository roleRepository,
                                           PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public AuthUserResult execute(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail.trim())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + usernameOrEmail));

        Instant now = Instant.now();
        List<UserRole> userRoles = userRepository.findRolesByUserId(user.getId());
        List<Role> roles = new ArrayList<>();
        Set<String> rolePermissionNames = new LinkedHashSet<>();
        Set<String> authorities = new LinkedHashSet<>();

        for (UserRole ur : userRoles) {
            if (ur.isActive(now)) {
                Optional<Role> roleOpt = roleRepository.findById(ur.getRoleId())
                        .filter(r -> !r.isDeleted() && r.isActive());
                if (roleOpt.isPresent()) {
                    Role role = roleOpt.get();
                    roles.add(role);
                    authorities.add("ROLE_" + role.getName());

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

        List<UserPermission> userPermissions = userRepository.findPermissionsByUserId(user.getId());
        Map<String, PermissionEffect> directMap = new LinkedHashMap<>();
        for (UserPermission up : userPermissions) {
            if (up.isActive(now)) {
                permissionRepository.findById(up.getPermissionId())
                        .filter(p -> !p.isDeleted() && p.isActive())
                        .ifPresent(p -> directMap.put(p.getName(), up.getEffect()));
            }
        }

        Set<String> effectivePermissions = EffectivePermissionCalculator.calculate(rolePermissionNames, directMap);
        authorities.addAll(effectivePermissions);

        return new AuthUserResult(user, roles, effectivePermissions, authorities);
    }
}
