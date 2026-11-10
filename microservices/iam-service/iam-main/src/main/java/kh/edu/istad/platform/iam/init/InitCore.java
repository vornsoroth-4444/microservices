package kh.edu.istad.platform.iam.init;

import kh.edu.istad.platform.iam.application.port.out.PasswordHasher;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Component
public class InitCore implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(InitCore.class);

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final RegisteredClientRepository registeredClientRepository;

    public InitCore(PermissionRepository permissionRepository,
                    RoleRepository roleRepository,
                    UserRepository userRepository,
                    PasswordHasher passwordHasher,
                    RegisteredClientRepository registeredClientRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Starting database initialization and seeding...");

        // 1. Seed Permissions
        Map<String, Permission> seededPerms = seedPermissions();

        // 2. Seed Roles and assign permissions
        Role adminRole = seedRole("ADMIN", "System Administrator with full access", true, seededPerms.values());
        Role userRole = seedRole("USER", "Standard application user", true, List.of(seededPerms.get("user:read")));

        // 3. Seed Users
        seedAdminUser(adminRole, userRole);
        seedRegularUser(userRole);

        // 4. Seed OAuth2 Clients
        seedDefaultClient();

        log.info("Database initialization and seeding completed successfully.");
    }

    private Map<String, Permission> seedPermissions() {
        Map<String, Permission> perms = new HashMap<>();
        String[] resources = {"user", "role", "permission"};
        String[] actions = {"create", "read", "update", "delete", "assign"};

        Instant now = Instant.now();
        for (String res : resources) {
            for (String act : actions) {
                String name = res + ":" + act;
                Permission perm = permissionRepository.findByName(name).orElseGet(() -> {
                    Permission newPerm = Permission.builder()
                            .uuid(UUID.randomUUID())
                            .name(name)
                            .resource(res)
                            .action(act)
                            .description("Permission to " + act + " " + res)
                            .system(true)
                            .active(true)
                            .createdAt(now)
                            .createdBy("system")
                            .updatedAt(now)
                            .updatedBy("system")
                            .version(0L)
                            .build();
                    log.info("Seeding permission: {}", name);
                    return permissionRepository.save(newPerm);
                });
                perms.put(name, perm);
            }
        }
        return perms;
    }

    private Role seedRole(String name, String description, boolean system, Collection<Permission> permissions) {
        Instant now = Instant.now();
        Role role = roleRepository.findByName(name).orElseGet(() -> {
            Role newRole = Role.builder()
                    .uuid(UUID.randomUUID())
                    .name(name)
                    .description(description)
                    .system(system)
                    .active(true)
                    .createdAt(now)
                    .createdBy("system")
                    .updatedAt(now)
                    .updatedBy("system")
                    .version(0L)
                    .build();
            log.info("Seeding role: {}", name);
            return roleRepository.save(newRole);
        });

        // Ensure permissions are assigned
        List<RolePermission> existingAssignments = roleRepository.findPermissionsByRoleId(role.getId());
        Set<Long> assignedPermIds = new HashSet<>();
        for (RolePermission rp : existingAssignments) {
            if (rp.isActive(now)) {
                assignedPermIds.add(rp.getPermissionId());
            }
        }

        for (Permission perm : permissions) {
            if (perm != null && !assignedPermIds.contains(perm.getId())) {
                RolePermission rp = RolePermission.builder()
                        .roleId(role.getId())
                        .permissionId(perm.getId())
                        .assignedAt(now)
                        .assignedBy("system")
                        .reason("Seed assignment")
                        .build();
                roleRepository.saveRolePermission(rp);
            }
        }

        return role;
    }

    private void seedAdminUser(Role adminRole, Role userRole) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Instant now = Instant.now();
            User admin = User.builder()
                    .uuid(UUID.randomUUID())
                    .username("admin")
                    .email("admin@istad.co")
                    .emailVerified(true)
                    .passwordHash(passwordHasher.hash("AdminPassword123!"))
                    .givenName("System")
                    .familyName("Admin")
                    .locale("en")
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .createdAt(now)
                    .createdBy("system")
                    .updatedAt(now)
                    .updatedBy("system")
                    .version(0L)
                    .build();

            User saved = userRepository.save(admin);
            log.info("Seeded admin user: {}", saved.getUsername());

            userRepository.saveUserRole(UserRole.builder()
                    .userId(saved.getId())
                    .roleId(adminRole.getId())
                    .assignedAt(now)
                    .assignedBy("system")
                    .reason("Seed admin role")
                    .build());

            userRepository.saveUserRole(UserRole.builder()
                    .userId(saved.getId())
                    .roleId(userRole.getId())
                    .assignedAt(now)
                    .assignedBy("system")
                    .reason("Seed user role")
                    .build());
        }
    }

    private void seedRegularUser(Role userRole) {
        if (userRepository.findByUsername("user").isEmpty()) {
            Instant now = Instant.now();
            User regular = User.builder()
                    .uuid(UUID.randomUUID())
                    .username("user")
                    .email("user@istad.co")
                    .emailVerified(true)
                    .passwordHash(passwordHasher.hash("UserPassword123!"))
                    .givenName("Regular")
                    .familyName("User")
                    .locale("en")
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .createdAt(now)
                    .createdBy("system")
                    .updatedAt(now)
                    .updatedBy("system")
                    .version(0L)
                    .build();

            User saved = userRepository.save(regular);
            log.info("Seeded regular user: {}", saved.getUsername());

            userRepository.saveUserRole(UserRole.builder()
                    .userId(saved.getId())
                    .roleId(userRole.getId())
                    .assignedAt(now)
                    .assignedBy("system")
                    .reason("Seed user role")
                    .build());
        }
    }

    private void seedDefaultClient() {
        if (registeredClientRepository.findByClientId("iam-client") == null) {
            RegisteredClient defaultClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId("iam-client")
                    .clientSecret(passwordHasher.hash("iam-secret"))
                    .clientName("IAM Client Application")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .redirectUri("http://localhost:8080/login/oauth2/code/iam-client")
                    .redirectUri("https://oauth.pstmn.io/v1/callback")
                    .postLogoutRedirectUri("http://localhost:8080/")
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope(OidcScopes.EMAIL)
                    .scope(OidcScopes.PHONE)
                    .scope(OidcScopes.ADDRESS)
                    .scope("client.create")
                    .scope("user:read")
                    .scope("user:create")
                    .scope("user:update")
                    .scope("role:read")
                    .scope("permission:read")
                    .clientSettings(ClientSettings.builder()
                            .requireAuthorizationConsent(true)
                            .build())
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofHours(1))
                            .refreshTokenTimeToLive(Duration.ofDays(30))
                            .reuseRefreshTokens(false)
                            .build())
                    .build();

            registeredClientRepository.save(defaultClient);
            log.info("Seeded default OAuth2 client: iam-client");
        }
    }
}
