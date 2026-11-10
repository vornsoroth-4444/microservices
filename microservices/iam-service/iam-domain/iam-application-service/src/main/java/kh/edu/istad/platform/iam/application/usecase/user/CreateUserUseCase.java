package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.CreateUserCommand;
import kh.edu.istad.platform.iam.application.dto.RoleSummary;
import kh.edu.istad.platform.iam.application.dto.UserResult;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PasswordHasher;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ConflictException;
import kh.edu.istad.platform.iam.domain.exception.InactiveResourceException;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;

    public CreateUserUseCase(UserRepository userRepository,
                             RoleRepository roleRepository,
                             PasswordHasher passwordHasher,
                             CurrentActorProvider currentActorProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public UserResult execute(CreateUserCommand cmd) {
        String normalizedEmail = cmd.getEmail().trim().toLowerCase(Locale.ROOT);
        String username = cmd.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username is already taken: " + username);
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email is already registered: " + normalizedEmail);
        }
        if (cmd.getPhoneNumber() != null && !cmd.getPhoneNumber().isBlank()) {
            if (userRepository.existsByPhoneNumber(cmd.getPhoneNumber().trim())) {
                throw new ConflictException("Phone number is already in use: " + cmd.getPhoneNumber());
            }
        }

        String actor = currentActorProvider.getCurrentActor();
        Instant now = Instant.now();

        String passwordHash = (cmd.getPassword() != null && !cmd.getPassword().isBlank())
                ? passwordHasher.hash(cmd.getPassword())
                : null;

        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username(username)
                .email(normalizedEmail)
                .emailVerified(false)
                .passwordHash(passwordHash)
                .givenName(cmd.getGivenName() != null ? cmd.getGivenName().trim() : "")
                .familyName(cmd.getFamilyName() != null ? cmd.getFamilyName().trim() : "")
                .gender(cmd.getGender())
                .birthdate(cmd.getBirthdate())
                .phoneNumber(cmd.getPhoneNumber())
                .phoneNumberVerified(false)
                .zoneinfo(cmd.getZoneinfo() != null ? cmd.getZoneinfo() : "Asia/Phnom_Penh")
                .locale(cmd.getLocale() != null ? cmd.getLocale() : "en")
                .address(cmd.getAddress())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .deleted(false)
                .createdAt(now)
                .createdBy(actor)
                .updatedAt(now)
                .updatedBy(actor)
                .version(0L)
                .build();

        User savedUser = userRepository.save(user);

        List<RoleSummary> roles = new ArrayList<>();
        if (cmd.getRoleUuids() != null && !cmd.getRoleUuids().isEmpty()) {
            for (UUID roleUuid : cmd.getRoleUuids()) {
                Role role = roleRepository.findByUuid(roleUuid)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with uuid: " + roleUuid));
                if (!role.isActive()) {
                    throw new InactiveResourceException("Inactive role cannot be assigned: " + role.getName());
                }
                UserRole ur = UserRole.builder()
                        .userId(savedUser.getId())
                        .roleId(role.getId())
                        .assignedAt(now)
                        .assignedBy(actor)
                        .reason("Assigned during user creation")
                        .build();
                userRepository.saveUserRole(ur);
                roles.add(new RoleSummary(role.getUuid(), role.getName(), role.isActive()));
            }
        }

        return UserDtoMapper.toResult(savedUser, roles);
    }
}
