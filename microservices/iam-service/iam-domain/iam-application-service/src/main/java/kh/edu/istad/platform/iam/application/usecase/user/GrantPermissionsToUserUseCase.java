package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.GrantPermissionsToUserCommand;
import kh.edu.istad.platform.iam.application.dto.PermissionGrantResult;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.InactiveResourceException;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Permission;
import kh.edu.istad.platform.iam.domain.model.PermissionEffect;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserPermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GrantPermissionsToUserUseCase {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public GrantPermissionsToUserUseCase(UserRepository userRepository,
                                        PermissionRepository permissionRepository,
                                        CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public List<PermissionGrantResult> execute(GrantPermissionsToUserCommand cmd) {
        User user = userRepository.findByUuid(cmd.getUserUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + cmd.getUserUuid()));

        String actor = currentActorProvider.getCurrentActor();
        Instant now = Instant.now();
        PermissionEffect effect = cmd.getEffect() != null ? cmd.getEffect() : PermissionEffect.GRANT;
        List<PermissionGrantResult> results = new ArrayList<>();

        if (cmd.getPermissionUuids() != null) {
            for (UUID permUuid : cmd.getPermissionUuids()) {
                Permission perm = permissionRepository.findByUuid(permUuid)
                        .filter(p -> !p.isDeleted())
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with uuid: " + permUuid));

                if (!perm.isActive()) {
                    throw new InactiveResourceException("Inactive permission cannot be assigned: " + perm.getName());
                }

                UserPermission up = UserPermission.builder()
                        .userId(user.getId())
                        .permissionId(perm.getId())
                        .effect(effect)
                        .assignedAt(now)
                        .assignedBy(actor)
                        .expiresAt(cmd.getExpiresAt())
                        .reason(cmd.getReason())
                        .build();

                userRepository.saveUserPermission(up);
                results.add(new PermissionGrantResult(perm.getUuid(), perm.getName(), up.getEffect(), up.getAssignedAt(), up.getExpiresAt(), up.getReason()));
            }
        }

        return results;
    }
}
