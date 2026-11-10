package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.RoleSummary;
import kh.edu.istad.platform.iam.application.dto.UserResult;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
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
public class GetUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public GetUserUseCase(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public UserResult execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + userUuid));

        Instant now = Instant.now();
        List<UserRole> userRoles = userRepository.findRolesByUserId(user.getId());
        List<RoleSummary> roles = new ArrayList<>();
        for (UserRole ur : userRoles) {
            if (ur.isActive(now)) {
                roleRepository.findById(ur.getRoleId())
                        .filter(r -> !r.isDeleted())
                        .ifPresent(r -> roles.add(new RoleSummary(r.getUuid(), r.getName(), r.isActive())));
            }
        }

        return UserDtoMapper.toResult(user, roles);
    }
}
