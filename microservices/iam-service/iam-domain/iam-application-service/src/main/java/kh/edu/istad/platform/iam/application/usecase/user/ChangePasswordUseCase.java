package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.ChangePasswordCommand;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PasswordHasher;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.InvalidCredentialException;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;

    public ChangePasswordUseCase(UserRepository userRepository,
                                 PasswordHasher passwordHasher,
                                 CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public void execute(ChangePasswordCommand cmd) {
        if (cmd.getNewPassword() == null || !cmd.getNewPassword().equals(cmd.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        User user = userRepository.findByUuid(cmd.getUserUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + cmd.getUserUuid()));

        // If user has existing password, verify it
        if (user.hasPassword()) {
            if (cmd.getCurrentPassword() == null || !passwordHasher.matches(cmd.getCurrentPassword(), user.getPasswordHash())) {
                throw new InvalidCredentialException("Current password is incorrect");
            }
        }

        String actor = currentActorProvider.getCurrentActor();
        user.changePassword(passwordHasher.hash(cmd.getNewPassword()), actor);
        userRepository.save(user);
    }
}
