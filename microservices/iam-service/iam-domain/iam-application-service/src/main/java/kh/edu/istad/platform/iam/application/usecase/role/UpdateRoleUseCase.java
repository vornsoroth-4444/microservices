package kh.edu.istad.platform.iam.application.usecase.role;

import kh.edu.istad.platform.iam.application.dto.PermissionResult;
import kh.edu.istad.platform.iam.application.dto.RoleResult;
import kh.edu.istad.platform.iam.application.dto.UpdateRoleCommand;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Permission;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.RolePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UpdateRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public UpdateRoleUseCase(RoleRepository roleRepository,
                            PermissionRepository permissionRepository,
                            CurrentActorProvider currentActorProvider) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public RoleResult execute(UpdateRoleCommand cmd) {
        Role role = roleRepository.findByUuid(cmd.getRoleUuid())
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with uuid: " + cmd.getRoleUuid()));

        String actor = currentActorProvider.getCurrentActor();
        role.update(cmd.getDescription(), actor);
        Role savedRole = roleRepository.save(role);

        Instant now = Instant.now();
        List<RolePermission> rolePerms = roleRepository.findPermissionsByRoleId(savedRole.getId());
        List<PermissionResult> permissions = new ArrayList<>();
        for (RolePermission rp : rolePerms) {
            if (rp.isActive(now)) {
                Optional<Permission> permOpt = permissionRepository.findById(rp.getPermissionId())
                        .filter(p -> !p.isDeleted());
                permOpt.ifPresent(p -> permissions.add(PermissionResult.builder()
                        .uuid(p.getUuid())
                        .name(p.getName())
                        .resource(p.getResource())
                        .action(p.getAction())
                        .description(p.getDescription())
                        .system(p.isSystem())
                        .active(p.isActive())
                        .createdAt(p.getCreatedAt())
                        .updatedAt(p.getUpdatedAt())
                        .build()));
            }
        }

        return RoleDtoMapper.toResult(savedRole, permissions);
    }
}
