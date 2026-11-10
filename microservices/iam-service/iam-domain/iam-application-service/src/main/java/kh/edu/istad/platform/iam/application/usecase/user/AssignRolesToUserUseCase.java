package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.AssignRolesToUserCommand;
import kh.edu.istad.platform.iam.application.dto.RoleAssignmentResult;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.InactiveResourceException;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssignRolesToUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;

    public AssignRolesToUserUseCase(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public List<RoleAssignmentResult> execute(AssignRolesToUserCommand cmd) {
        User user = userRepository.findByUuid(cmd.getUserUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + cmd.getUserUuid()));

        String actor = currentActorProvider.getCurrentActor();
        Instant now = Instant.now();
        List<RoleAssignmentResult> results = new ArrayList<>();

        if (cmd.getRoleUuids() != null) {
            for (UUID roleUuid : cmd.getRoleUuids()) {
                Role role = roleRepository.findByUuid(roleUuid)
                        .filter(r -> !r.isDeleted())
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with uuid: " + roleUuid));

                if (!role.isActive()) {
                    throw new InactiveResourceException("Inactive role cannot be assigned: " + role.getName());
                }

                UserRole ur = UserRole.builder()
                        .userId(user.getId())
                        .roleId(role.getId())
                        .assignedAt(now)
                        .assignedBy(actor)
                        .expiresAt(cmd.getExpiresAt())
                        .reason(cmd.getReason())
                        .build();

                userRepository.saveUserRole(ur);
                results.add(new RoleAssignmentResult(role.getUuid(), role.getName(), ur.getAssignedAt(), ur.getExpiresAt(), ur.getReason()));
            }
        }

        return results;
    }
}
