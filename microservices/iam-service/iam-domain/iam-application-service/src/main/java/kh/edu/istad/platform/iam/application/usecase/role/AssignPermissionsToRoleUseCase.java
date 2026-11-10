package kh.edu.istad.platform.iam.application.usecase.role;

import kh.edu.istad.platform.iam.application.dto.AssignPermissionsToRoleCommand;
import kh.edu.istad.platform.iam.application.dto.PermissionAssignmentResult;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
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
public class AssignPermissionsToRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public AssignPermissionsToRoleUseCase(RoleRepository roleRepository,
                                         PermissionRepository permissionRepository,
                                         CurrentActorProvider currentActorProvider) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public List<PermissionAssignmentResult> execute(AssignPermissionsToRoleCommand cmd) {
        Role role = roleRepository.findByUuid(cmd.getRoleUuid())
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with uuid: " + cmd.getRoleUuid()));

        String actor = currentActorProvider.getCurrentActor();
        Instant now = Instant.now();
        List<PermissionAssignmentResult> results = new ArrayList<>();

        if (cmd.getPermissionUuids() != null) {
            for (UUID permUuid : cmd.getPermissionUuids()) {
                Permission perm = permissionRepository.findByUuid(permUuid)
                        .filter(p -> !p.isDeleted())
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with uuid: " + permUuid));

                if (!perm.isActive()) {
                    throw new InactiveResourceException("Inactive permission cannot be assigned: " + perm.getName());
                }

                RolePermission rp = RolePermission.builder()
                        .roleId(role.getId())
                        .permissionId(perm.getId())
                        .assignedAt(now)
                        .assignedBy(actor)
                        .expiresAt(cmd.getExpiresAt())
                        .reason(cmd.getReason())
                        .build();

                roleRepository.saveRolePermission(rp);
                results.add(new PermissionAssignmentResult(perm.getUuid(), perm.getName(), rp.getAssignedAt(), rp.getExpiresAt(), rp.getReason()));
            }
        }

        return results;
    }
}
