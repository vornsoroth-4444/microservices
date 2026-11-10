package kh.edu.istad.platform.iam.application.usecase.identity;

import kh.edu.istad.platform.iam.application.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.application.dto.IdentityResult;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ConflictException;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Identity;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LinkIdentityUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;

    public LinkIdentityUseCase(UserRepository userRepository, CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public IdentityResult execute(UUID userUuid, FederatedIdentityCommand cmd) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + userUuid));

        // Domain rule: One identity per provider per user
        List<Identity> userIdentities = userRepository.findIdentitiesByUserId(user.getId());
        for (Identity id : userIdentities) {
            if (!id.isDeleted() && id.getProvider() == cmd.getProvider()) {
                throw new ConflictException("User already has an identity linked with provider: " + cmd.getProvider());
            }
        }

        // Check if provider + providerSubject is already taken by another user
        Optional<Identity> existingGlobal = userRepository.findIdentityByProviderAndSubject(cmd.getProvider(), cmd.getProviderSubject());
        if (existingGlobal.isPresent() && !existingGlobal.get().isDeleted()) {
            throw new ConflictException("Social account is already linked to another user");
        }

        Instant now = Instant.now();
        String actor = currentActorProvider.getCurrentActor();

        Identity identity = Identity.builder()
                .uuid(UUID.randomUUID())
                .userId(user.getId())
                .provider(cmd.getProvider())
                .providerSubject(cmd.getProviderSubject())
                .email(cmd.getEmail())
                .emailVerified(cmd.isEmailVerified())
                .displayName(cmd.getDisplayName())
                .username(cmd.getUsername())
                .pictureUrl(cmd.getPictureUrl())
                .attributes(cmd.getAttributes())
                .linkedAt(now)
                .lastLoginAt(now)
                .deleted(false)
                .createdAt(now)
                .createdBy(actor)
                .updatedAt(now)
                .updatedBy(actor)
                .version(0L)
                .build();

        Identity saved = userRepository.saveIdentity(identity);
        return IdentityDtoMapper.toResult(saved);
    }
}
