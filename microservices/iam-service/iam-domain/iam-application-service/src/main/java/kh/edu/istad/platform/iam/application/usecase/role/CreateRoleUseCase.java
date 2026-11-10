package kh.edu.istad.platform.iam.application.usecase.role;

import kh.edu.istad.platform.iam.application.dto.CreateRoleCommand;
import kh.edu.istad.platform.iam.application.dto.PermissionResult;
import kh.edu.istad.platform.iam.application.dto.RoleResult;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.exception.ConflictException;
import kh.edu.istad.platform.iam.domain.exception.InactiveResourceException;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Permission;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.RolePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreateRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public CreateRoleUseCase(RoleRepository roleRepository,
                             PermissionRepository permissionRepository,
                             CurrentActorProvider currentActorProvider) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public RoleResult execute(CreateRoleCommand cmd) {
        String roleName = cmd.getName().trim().toUpperCase();
        if (roleRepository.existsByName(roleName)) {
            throw new ConflictException("Role already exists with name: " + roleName);
        }

        String actor = currentActorProvider.getCurrentActor();
        Instant now = Instant.now();

        Role role = Role.builder()
                .uuid(UUID.randomUUID())
                .name(roleName)
                .description(cmd.getDescription())
                .system(false)
                .active(true)
                .deleted(false)
                .createdAt(now)
                .createdBy(actor)
                .updatedAt(now)
                .updatedBy(actor)
                .version(0L)
                .build();

        Role savedRole = roleRepository.save(role);

        List<PermissionResult> permissions = new ArrayList<>();
        if (cmd.getPermissionUuids() != null) {
            for (UUID permUuid : cmd.getPermissionUuids()) {
                Permission perm = permissionRepository.findByUuid(permUuid)
                        .filter(p -> !p.isDeleted())
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with uuid: " + permUuid));

                if (!perm.isActive()) {
                    throw new InactiveResourceException("Inactive permission cannot be assigned: " + perm.getName());
                }

                RolePermission rp = RolePermission.builder()
                        .roleId(savedRole.getId())
                        .permissionId(perm.getId())
                        .assignedAt(now)
                        .assignedBy(actor)
                        .reason("Assigned during role creation")
                        .build();

                roleRepository.saveRolePermission(rp);
                permissions.add(PermissionResult.builder()
                        .uuid(perm.getUuid())
                        .name(perm.getName())
                        .resource(perm.getResource())
                        .action(perm.getAction())
                        .description(perm.getDescription())
                        .system(perm.isSystem())
                        .active(perm.isActive())
                        .createdAt(perm.getCreatedAt())
                        .updatedAt(perm.getUpdatedAt())
                        .build());
            }
        }

        return RoleDtoMapper.toResult(savedRole, permissions);
    }
}
