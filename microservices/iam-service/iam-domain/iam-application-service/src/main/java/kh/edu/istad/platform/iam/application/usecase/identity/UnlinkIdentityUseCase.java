package kh.edu.istad.platform.iam.application.usecase.identity;

import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.exception.UnlinkLastMethodException;
import kh.edu.istad.platform.iam.domain.model.Identity;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UnlinkIdentityUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;

    public UnlinkIdentityUseCase(UserRepository userRepository, CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public void execute(UUID userUuid, UUID identityUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + userUuid));

        Identity targetIdentity = userRepository.findIdentityByUuid(identityUuid)
                .filter(i -> !i.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Identity not found with uuid: " + identityUuid));

        if (!targetIdentity.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("Identity does not belong to user");
        }

        // Domain rule: a user cannot unlink their last sign-in method
        List<Identity> activeIdentities = userRepository.findIdentitiesByUserId(user.getId())
                .stream()
                .filter(i -> !i.isDeleted())
                .toList();

        boolean hasPassword = user.hasPassword();
        if (!hasPassword && activeIdentities.size() <= 1) {
            throw new UnlinkLastMethodException("Cannot unlink last sign-in method: user has no password configured");
        }

        String actor = currentActorProvider.getCurrentActor();
        targetIdentity.softDelete(actor);
        userRepository.saveIdentity(targetIdentity);
    }
}
