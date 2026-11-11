package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.model.Identity;
import kh.edu.istad.platform.iam.domain.model.IdentityProvider;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class WebUserResolver {

    private final UserRepository userRepository;

    public WebUserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> resolveCurrentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();

        // 1. If principal is OidcUser (e.g. Google)
        if (principal instanceof OidcUser oidcUser) {
            String email = oidcUser.getEmail();
            if (email != null && !email.isBlank()) {
                Optional<User> byEmail = userRepository.findByEmail(email);
                if (byEmail.isPresent()) return byEmail;
            }
            String sub = oidcUser.getSubject();
            if (sub != null && !sub.isBlank()) {
                Optional<Identity> identity = userRepository.findIdentityByProviderAndSubject(IdentityProvider.GOOGLE, sub);
                if (identity.isPresent()) {
                    return userRepository.findById(identity.get().getUserId());
                }
            }
        }

        // 2. If principal is OAuth2User (e.g. GitHub, Facebook)
        if (principal instanceof OAuth2User oauth2User) {
            Object uuidAttr = oauth2User.getAttribute("userUuid");
            if (uuidAttr != null) {
                try {
                    Optional<User> byUuid = userRepository.findByUuid(UUID.fromString(uuidAttr.toString()));
                    if (byUuid.isPresent()) return byUuid;
                } catch (Exception ignored) {}
            }
            Object usernameAttr = oauth2User.getAttribute("username");
            if (usernameAttr != null) {
                Optional<User> byUsername = userRepository.findByUsername(usernameAttr.toString());
                if (byUsername.isPresent()) return byUsername;
            }
            Object emailAttr = oauth2User.getAttribute("email");
            if (emailAttr != null) {
                Optional<User> byEmail = userRepository.findByEmail(emailAttr.toString());
                if (byEmail.isPresent()) return byEmail;
            }
            String name = oauth2User.getName();
            if (name != null && !name.isBlank()) {
                for (IdentityProvider provider : IdentityProvider.values()) {
                    Optional<Identity> identity = userRepository.findIdentityByProviderAndSubject(provider, name);
                    if (identity.isPresent()) {
                        return userRepository.findById(identity.get().getUserId());
                    }
                }
            }
        }

        // 3. Fallback by auth.getName() (Form login, Telegram, or UUID)
        String name = auth.getName();
        if (name != null && !name.isBlank()) {
            try {
                UUID uuid = UUID.fromString(name);
                Optional<User> byUuid = userRepository.findByUuid(uuid);
                if (byUuid.isPresent()) return byUuid;
            } catch (IllegalArgumentException ignored) {}

            Optional<User> byUsername = userRepository.findByUsername(name);
            if (byUsername.isPresent()) return byUsername;

            Optional<User> byEmail = userRepository.findByEmail(name);
            if (byEmail.isPresent()) return byEmail;

            for (IdentityProvider provider : IdentityProvider.values()) {
                Optional<Identity> identity = userRepository.findIdentityByProviderAndSubject(provider, name);
                if (identity.isPresent()) {
                    return userRepository.findById(identity.get().getUserId());
                }
            }
        }

        return Optional.empty();
    }
}
