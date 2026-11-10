package kh.edu.istad.platform.iam.application;

import kh.edu.istad.platform.iam.application.dto.*;
import kh.edu.istad.platform.iam.application.port.out.*;
import kh.edu.istad.platform.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iam.application.usecase.role.AssignPermissionsToRoleUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.AssignRolesToUserUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.platform.iam.domain.exception.InactiveResourceException;
import kh.edu.istad.platform.iam.domain.exception.UnlinkLastMethodException;
import kh.edu.istad.platform.iam.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PermissionRepository permissionRepository;
    @Mock private PasswordHasher passwordHasher;
    @Mock private CurrentActorProvider currentActorProvider;

    @Test
    @DisplayName("RegisterUserUseCase registers user with hashed password and default USER role")
    void testRegisterUserUseCase() {
        when(userRepository.existsByUsername("sokdara")).thenReturn(false);
        when(userRepository.existsByEmail("dara.sok@istad.co")).thenReturn(false);
        when(passwordHasher.hash("Str0ng!Pass2026")).thenReturn("hashed_pass");

        Role userRole = Role.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name("USER")
                .active(true)
                .build();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(100L);
            return u;
        });

        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository, roleRepository, passwordHasher);
        RegisterUserCommand cmd = RegisterUserCommand.builder()
                .username("sokdara")
                .email("dara.sok@istad.co")
                .password("Str0ng!Pass2026")
                .confirmPassword("Str0ng!Pass2026")
                .givenName("Dara")
                .familyName("Sok")
                .locale("km")
                .build();

        UserResult result = useCase.execute(cmd);

        assertNotNull(result);
        assertEquals("sokdara", result.getUsername());
        assertEquals("dara.sok@istad.co", result.getEmail());
        assertEquals("Dara Sok", result.getName());
        assertEquals("km", result.getLocale());
        assertEquals(1, result.getRoles().size());
        assertEquals("USER", result.getRoles().get(0).getName());
        verify(userRepository).saveUserRole(any(UserRole.class));
    }

    @Test
    @DisplayName("UnlinkIdentityUseCase prevents unlinking the last sign-in method when user has no password")
    void testUnlinkIdentityPreventsLastMethod() {
        UUID userUuid = UUID.randomUUID();
        UUID identityUuid = UUID.randomUUID();

        User user = User.builder()
                .id(10L)
                .uuid(userUuid)
                .username("socialuser")
                .passwordHash(null) // no password
                .build();

        Identity identity = Identity.builder()
                .id(50L)
                .uuid(identityUuid)
                .userId(10L)
                .provider(IdentityProvider.GOOGLE)
                .deleted(false)
                .build();

        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(userRepository.findIdentityByUuid(identityUuid)).thenReturn(Optional.of(identity));
        when(userRepository.findIdentitiesByUserId(10L)).thenReturn(List.of(identity));

        UnlinkIdentityUseCase useCase = new UnlinkIdentityUseCase(userRepository, currentActorProvider);

        assertThrows(UnlinkLastMethodException.class, () -> useCase.execute(userUuid, identityUuid));
    }

    @Test
    @DisplayName("AssignRolesToUserUseCase throws InactiveResourceException when assigning inactive role")
    void testAssignInactiveRole() {
        UUID userUuid = UUID.randomUUID();
        UUID roleUuid = UUID.randomUUID();

        User user = User.builder()
                .id(10L)
                .uuid(userUuid)
                .build();

        Role inactiveRole = Role.builder()
                .id(20L)
                .uuid(roleUuid)
                .name("MANAGER")
                .active(false) // Inactive!
                .build();

        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(roleRepository.findByUuid(roleUuid)).thenReturn(Optional.of(inactiveRole));

        AssignRolesToUserUseCase useCase = new AssignRolesToUserUseCase(userRepository, roleRepository, currentActorProvider);
        AssignRolesToUserCommand cmd = new AssignRolesToUserCommand(userUuid, List.of(roleUuid), null, "test");

        assertThrows(InactiveResourceException.class, () -> useCase.execute(cmd));
    }

    @Test
    @DisplayName("GetUserAuthoritiesUseCase computes roles, direct permissions and effective permissions correctly")
    void testGetUserAuthoritiesUseCase() {
        UUID userUuid = UUID.randomUUID();
        User user = User.builder().id(10L).uuid(userUuid).build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));

        Instant now = Instant.now();
        Role adminRole = Role.builder().id(1L).uuid(UUID.randomUUID()).name("ADMIN").active(true).build();
        UserRole userRole = UserRole.builder().userId(10L).roleId(1L).assignedAt(now).build();
        when(userRepository.findRolesByUserId(10L)).thenReturn(List.of(userRole));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        Permission permUserRead = Permission.builder().id(101L).name("user:read").active(true).build();
        Permission permUserUpdate = Permission.builder().id(102L).name("user:update").active(true).build();
        RolePermission rp1 = RolePermission.builder().roleId(1L).permissionId(101L).assignedAt(now).build();
        RolePermission rp2 = RolePermission.builder().roleId(1L).permissionId(102L).assignedAt(now).build();
        when(roleRepository.findPermissionsByRoleId(1L)).thenReturn(List.of(rp1, rp2));
        when(permissionRepository.findById(101L)).thenReturn(Optional.of(permUserRead));
        when(permissionRepository.findById(102L)).thenReturn(Optional.of(permUserUpdate));

        // Direct DENY on user:update and direct GRANT on report:read
        Permission permReportRead = Permission.builder().id(103L).name("report:read").active(true).build();
        UserPermission upDeny = UserPermission.builder().userId(10L).permissionId(102L).effect(PermissionEffect.DENY).assignedAt(now).build();
        UserPermission upGrant = UserPermission.builder().userId(10L).permissionId(103L).effect(PermissionEffect.GRANT).assignedAt(now).build();
        when(userRepository.findPermissionsByUserId(10L)).thenReturn(List.of(upDeny, upGrant));
        when(permissionRepository.findById(103L)).thenReturn(Optional.of(permReportRead));

        GetUserAuthoritiesUseCase useCase = new GetUserAuthoritiesUseCase(userRepository, roleRepository, permissionRepository);
        UserAuthoritiesResult result = useCase.execute(userUuid);

        assertNotNull(result);
        assertEquals(1, result.getRoles().size());
        assertEquals("ADMIN", result.getRoles().get(0).getName());
        assertEquals(2, result.getDirectPermissions().size());

        Set<String> effective = result.getEffectivePermissions();
        assertTrue(effective.contains("user:read"));
        assertFalse(effective.contains("user:update"), "user:update was DENIED directly");
        assertTrue(effective.contains("report:read"), "report:read was GRANTED directly");
    }
}
