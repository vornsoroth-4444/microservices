package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Permission;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserPermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RevokePermissionFromUserUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public RevokePermissionFromUserUseCase(UserRepository userRepository,
                                          PermissionRepository permissionRepository,
                                          CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public void execute(UUID userUuid, UUID permissionUuid, String reason) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + userUuid));

        Permission perm = permissionRepository.findByUuid(permissionUuid)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with uuid: " + permissionUuid));

        Instant now = Instant.now();
        List<UserPermission> assignments = userRepository.findPermissionsByUserId(user.getId());
        String actor = currentActorProvider.getCurrentActor();

        boolean found = false;
        for (UserPermission up : assignments) {
            if (up.getPermissionId().equals(perm.getId()) && up.isActive(now)) {
                up.revoke(actor, reason);
                userRepository.saveUserPermission(up);
                found = true;
            }
        }

        if (!found) {
            throw new ResourceNotFoundException("Active permission assignment not found for permission: " + perm.getName());
        }
    }
}
