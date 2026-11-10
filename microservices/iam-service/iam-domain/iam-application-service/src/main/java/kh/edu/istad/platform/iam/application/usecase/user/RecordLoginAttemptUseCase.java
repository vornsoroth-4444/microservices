package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RecordLoginAttemptUseCase {

    private final UserRepository userRepository;

    public RecordLoginAttemptUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void execute(String usernameOrEmail, boolean success) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) return;

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isDeleted()) return;

            if (success) {
                user.recordSuccessfulLogin();
            } else {
                user.recordFailedLogin(5); // lock after 5 consecutive failed attempts
            }
            userRepository.save(user);
        }
    }
}
