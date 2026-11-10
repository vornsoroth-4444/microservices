package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.security.core.Authentication;
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
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        String name = auth.getName();
        try {
            UUID uuid = UUID.fromString(name);
            Optional<User> byUuid = userRepository.findByUuid(uuid);
            if (byUuid.isPresent()) return byUuid;
        } catch (IllegalArgumentException ignored) {}

        return userRepository.findByUsername(name)
                .or(() -> userRepository.findByEmail(name));
    }
}
