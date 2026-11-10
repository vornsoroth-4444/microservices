package kh.edu.istad.platform.iam.application.usecase.identity;

import kh.edu.istad.platform.iam.application.dto.AuthUserResult;
import kh.edu.istad.platform.iam.application.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.platform.iam.domain.model.Identity;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class FindOrProvisionFederatedUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase;

    public FindOrProvisionFederatedUserUseCase(UserRepository userRepository,
                                             RoleRepository roleRepository,
                                             LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.loadUserForAuthenticationUseCase = loadUserForAuthenticationUseCase;
    }

    @Transactional
    public AuthUserResult execute(FederatedIdentityCommand cmd) {
        Instant now = Instant.now();

        // 1. Check if identity exists by provider and providerSubject
        Optional<Identity> existingIdentity = userRepository.findIdentityByProviderAndSubject(cmd.getProvider(), cmd.getProviderSubject());

        User user;
        if (existingIdentity.isPresent()) {
            Identity identity = existingIdentity.get();
            identity.recordLogin();
            userRepository.saveIdentity(identity);

            user = userRepository.findById(identity.getUserId())
                    .orElseThrow(() -> new IllegalStateException("User missing for identity: " + identity.getId()));
            user.recordSuccessfulLogin();
            userRepository.save(user);
        } else {
            // 2. Check if a user with matching email already exists
            String email = (cmd.getEmail() != null && !cmd.getEmail().isBlank())
                    ? cmd.getEmail().trim().toLowerCase(Locale.ROOT)
                    : null;

            Optional<User> userByEmail = email != null ? userRepository.findByEmail(email) : Optional.empty();

            if (userByEmail.isPresent()) {
                user = userByEmail.get();
            } else {
                // Provision a brand new user
                String baseUsername = (cmd.getUsername() != null && !cmd.getUsername().isBlank())
                        ? cmd.getUsername().trim()
                        : (email != null ? email.split("@")[0] : cmd.getProvider().name().toLowerCase() + "_" + cmd.getProviderSubject());

                String username = baseUsername;
                int suffix = 1;
                while (userRepository.existsByUsername(username)) {
                    username = baseUsername + (suffix++);
                }

                String givenName = cmd.getDisplayName() != null ? cmd.getDisplayName() : username;
                String familyName = "";
                if (givenName.contains(" ")) {
                    int lastSpace = givenName.lastIndexOf(" ");
                    familyName = givenName.substring(lastSpace + 1);
                    givenName = givenName.substring(0, lastSpace);
                }

                user = User.builder()
                        .uuid(UUID.randomUUID())
                        .username(username)
                        .email(email != null ? email : username + "@" + cmd.getProvider().name().toLowerCase() + ".local")
                        .emailVerified(cmd.isEmailVerified())
                        .passwordHash(null) // Social-only users have null password hash
                        .givenName(givenName)
                        .familyName(familyName)
                        .pictureUrl(cmd.getPictureUrl())
                        .locale("en")
                        .enabled(true)
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .failedLoginAttempts(0)
                        .lastLoginAt(now)
                        .deleted(false)
                        .createdAt(now)
                        .createdBy("FEDERATION_" + cmd.getProvider())
                        .updatedAt(now)
                        .updatedBy("FEDERATION_" + cmd.getProvider())
                        .version(0L)
                        .build();

                user = userRepository.save(user);

                // Assign default USER role
                Optional<Role> defaultRole = roleRepository.findByName("USER");
                if (defaultRole.isPresent()) {
                    Role role = defaultRole.get();
                    UserRole ur = UserRole.builder()
                            .userId(user.getId())
                            .roleId(role.getId())
                            .assignedAt(now)
                            .assignedBy("SYSTEM")
                            .reason("Default registration role")
                            .build();
                    userRepository.saveUserRole(ur);
                }
            }

            // Create and link the new identity
            Identity newIdentity = Identity.builder()
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
                    .createdBy("FEDERATION_" + cmd.getProvider())
                    .updatedAt(now)
                    .updatedBy("FEDERATION_" + cmd.getProvider())
                    .version(0L)
                    .build();

            userRepository.saveIdentity(newIdentity);
        }

        return loadUserForAuthenticationUseCase.execute(user.getUsername());
    }
}
