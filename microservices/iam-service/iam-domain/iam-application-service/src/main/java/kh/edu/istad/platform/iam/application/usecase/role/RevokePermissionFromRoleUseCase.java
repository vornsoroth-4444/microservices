package kh.edu.istad.platform.iam.application.usecase.role;

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
import java.util.List;
import java.util.UUID;

@Service
public class RevokePermissionFromRoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public RevokePermissionFromRoleUseCase(RoleRepository roleRepository,
                                          PermissionRepository permissionRepository,
                                          CurrentActorProvider currentActorProvider) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public void execute(UUID roleUuid, UUID permissionUuid, String reason) {
        Role role = roleRepository.findByUuid(roleUuid)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with uuid: " + roleUuid));

        Permission perm = permissionRepository.findByUuid(permissionUuid)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with uuid: " + permissionUuid));

        Instant now = Instant.now();
        List<RolePermission> assignments = roleRepository.findPermissionsByRoleId(role.getId());
        String actor = currentActorProvider.getCurrentActor();

        boolean found = false;
        for (RolePermission rp : assignments) {
            if (rp.getPermissionId().equals(perm.getId()) && rp.isActive(now)) {
                rp.revoke(actor, reason);
                roleRepository.saveRolePermission(rp);
                found = true;
            }
        }

        if (!found) {
            throw new ResourceNotFoundException("Active permission assignment not found on role: " + role.getName());
        }
    }
}
