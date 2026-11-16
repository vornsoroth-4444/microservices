package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.ResetUserPasswordCommand;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PasswordHasher;
import kh.edu.istad.platform.iam.application.port.out.PasswordPolicy;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResetUserPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;
    private final PasswordPolicy passwordPolicy;

    public ResetUserPasswordUseCase(UserRepository userRepository,
                                    PasswordHasher passwordHasher,
                                    CurrentActorProvider currentActorProvider,
                                    PasswordPolicy passwordPolicy) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.currentActorProvider = currentActorProvider;
        this.passwordPolicy = passwordPolicy;
    }

    @Transactional
    public void execute(ResetUserPasswordCommand cmd) {
        passwordPolicy.validate(cmd.getNewPassword());
        User user = userRepository.findByUuid(cmd.getUserUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + cmd.getUserUuid()));

        String actor = currentActorProvider.getCurrentActor();
        user.changePassword(passwordHasher.hash(cmd.getNewPassword()), actor);
        userRepository.save(user);
    }
}
