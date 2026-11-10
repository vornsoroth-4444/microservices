package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RevokeRoleFromUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;

    public RevokeRoleFromUserUseCase(UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public void execute(UUID userUuid, UUID roleUuid, String reason) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + userUuid));

        Role role = roleRepository.findByUuid(roleUuid)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with uuid: " + roleUuid));

        Instant now = Instant.now();
        List<UserRole> assignments = userRepository.findRolesByUserId(user.getId());
        String actor = currentActorProvider.getCurrentActor();

        boolean found = false;
        for (UserRole ur : assignments) {
            if (ur.getRoleId().equals(role.getId()) && ur.isActive(now)) {
                ur.revoke(actor, reason);
                userRepository.saveUserRole(ur);
                found = true;
            }
        }

        if (!found) {
            throw new ResourceNotFoundException("Active role assignment not found for role: " + role.getName());
        }
    }
}
