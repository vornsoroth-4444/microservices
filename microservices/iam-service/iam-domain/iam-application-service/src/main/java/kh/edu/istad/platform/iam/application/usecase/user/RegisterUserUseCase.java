package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.RegisterUserCommand;
import kh.edu.istad.platform.iam.application.dto.RoleSummary;
import kh.edu.istad.platform.iam.application.dto.UserResult;
import kh.edu.istad.platform.iam.application.port.out.PasswordHasher;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ConflictException;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public UserResult execute(RegisterUserCommand cmd) {
        if (cmd.getPassword() == null || !cmd.getPassword().equals(cmd.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String normalizedEmail = cmd.getEmail().trim().toLowerCase(Locale.ROOT);
        String username = cmd.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username is already taken: " + username);
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email is already registered: " + normalizedEmail);
        }

        Instant now = Instant.now();
        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username(username)
                .email(normalizedEmail)
                .emailVerified(false)
                .passwordHash(passwordHasher.hash(cmd.getPassword()))
                .givenName(cmd.getGivenName() != null ? cmd.getGivenName().trim() : "")
                .familyName(cmd.getFamilyName() != null ? cmd.getFamilyName().trim() : "")
                .locale(cmd.getLocale() != null ? cmd.getLocale() : "en")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .deleted(false)
                .createdAt(now)
                .createdBy("SELF")
                .updatedAt(now)
                .updatedBy("SELF")
                .version(0L)
                .build();

        User savedUser = userRepository.save(user);

        // Assign default USER role if available
        List<RoleSummary> roles = new ArrayList<>();
        Optional<Role> defaultRole = roleRepository.findByName("USER");
        if (defaultRole.isPresent()) {
            Role role = defaultRole.get();
            UserRole userRole = UserRole.builder()
                    .userId(savedUser.getId())
                    .roleId(role.getId())
                    .assignedAt(now)
                    .assignedBy("SYSTEM")
                    .reason("Default registration role")
                    .build();
            userRepository.saveUserRole(userRole);
            roles.add(new RoleSummary(role.getUuid(), role.getName(), role.isActive()));
        }

        return UserDtoMapper.toResult(savedUser, roles);
    }
}
