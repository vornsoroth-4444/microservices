package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.RoleSummary;
import kh.edu.istad.platform.iam.application.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.application.dto.UserResult;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateUserProfileUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;

    public UpdateUserProfileUseCase(UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public UserResult execute(UpdateUserProfileCommand cmd) {
        User user = userRepository.findByUuid(cmd.getUserUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + cmd.getUserUuid()));

        String actor = currentActorProvider.getCurrentActor();
        user.updateProfile(
                cmd.getGivenName(),
                cmd.getFamilyName(),
                cmd.getMiddleName(),
                cmd.getNickname(),
                cmd.getGender(),
                cmd.getBirthdate(),
                cmd.getProfileUrl(),
                cmd.getPictureUrl(),
                cmd.getWebsiteUrl(),
                cmd.getZoneinfo(),
                cmd.getLocale(),
                cmd.getPhoneNumber(),
                cmd.getAddress(),
                actor
        );

        User savedUser = userRepository.save(user);

        Instant now = Instant.now();
        List<UserRole> userRoles = userRepository.findRolesByUserId(savedUser.getId());
        List<RoleSummary> roles = new ArrayList<>();
        for (UserRole ur : userRoles) {
            if (ur.isActive(now)) {
                roleRepository.findById(ur.getRoleId())
                        .filter(r -> !r.isDeleted())
                        .ifPresent(r -> roles.add(new RoleSummary(r.getUuid(), r.getName(), r.isActive())));
            }
        }

        return UserDtoMapper.toResult(savedUser, roles);
    }
}
