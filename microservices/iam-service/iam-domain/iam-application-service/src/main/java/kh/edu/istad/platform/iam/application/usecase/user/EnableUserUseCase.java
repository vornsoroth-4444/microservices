package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EnableUserUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;

    public EnableUserUseCase(UserRepository userRepository, CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public void execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + userUuid));

        user.enable(currentActorProvider.getCurrentActor());
        userRepository.save(user);
    }
}
