package kh.edu.istad.platform.iam.domain;

import kh.edu.istad.platform.iam.domain.exception.SystemResourceProtectedException;
import kh.edu.istad.platform.iam.domain.model.*;
import kh.edu.istad.platform.iam.domain.service.EffectivePermissionCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DomainCoreTest {

    @Test
    @DisplayName("User should correctly calculate full name, lock after consecutive failed logins, and update profile")
    void testUserBehavior() {
        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username("sokdara")
                .email("dara.sok@istad.co")
                .givenName("Dara")
                .familyName("Sok")
                .enabled(true)
                .accountNonLocked(true)
                .build();

        assertEquals("Dara Sok", user.getFullName());
        assertTrue(user.isAccountNonLocked());

        // Test failed logins
        user.recordFailedLogin(3);
        assertEquals(1, user.getFailedLoginAttempts());
        assertTrue(user.isAccountNonLocked());

        user.recordFailedLogin(3);
        user.recordFailedLogin(3);
        assertEquals(3, user.getFailedLoginAttempts());
        assertFalse(user.isAccountNonLocked(), "Account should be locked after 3 failed attempts");

        // Unlock
        user.unlock("ADMIN");
        assertTrue(user.isAccountNonLocked());
        assertEquals(0, user.getFailedLoginAttempts());

        // Password change
        user.changePassword("hashed_pass", "sokdara");
        assertEquals("hashed_pass", user.getPasswordHash());
        assertNotNull(user.getPasswordChangedAt());

        // Profile update
        user.updateProfile("Dara2", "Sok2", null, "Dar", "male",
                LocalDate.of(2003, 5, 20), null, null, null,
                "Asia/Phnom_Penh", "km", "+85512345678", null, "sokdara");

        assertEquals("Dara2 Sok2", user.getFullName());
        assertEquals("km", user.getLocale());
        assertEquals("+85512345678", user.getPhoneNumber());
    }

    @Test
    @DisplayName("System roles and permissions cannot be updated or deleted")
    void testSystemProtection() {
        Role adminRole = Role.builder()
                .uuid(UUID.randomUUID())
                .name("ADMIN")
                .system(true)
                .active(true)
                .build();

        assertThrows(SystemResourceProtectedException.class, () -> adminRole.update("New desc", "ADMIN"));
        assertThrows(SystemResourceProtectedException.class, () -> adminRole.softDelete("ADMIN"));
        assertThrows(SystemResourceProtectedException.class, () -> adminRole.deactivate("ADMIN"));

        Permission userRead = Permission.builder()
                .uuid(UUID.randomUUID())
                .name("user:read")
                .resource("user")
                .action("read")
                .system(true)
                .active(true)
                .build();

        assertThrows(SystemResourceProtectedException.class, () -> userRead.update("user", "write", "desc", "ADMIN"));
        assertThrows(SystemResourceProtectedException.class, () -> userRead.softDelete("ADMIN"));
        assertThrows(SystemResourceProtectedException.class, () -> userRead.deactivate("ADMIN"));
    }

    @Test
    @DisplayName("Audited assignments should be revocable and reflect active status with expiration")
    void testAuditedAssignments() {
        Instant now = Instant.now();
        Instant future = now.plus(1, ChronoUnit.HOURS);
        Instant past = now.minus(1, ChronoUnit.HOURS);

        UserRole activeRole = UserRole.builder()
                .id(1L)
                .userId(10L)
                .roleId(20L)
                .assignedAt(now)
                .expiresAt(future)
                .build();
        assertTrue(activeRole.isActive(now));

        UserRole expiredRole = UserRole.builder()
                .id(2L)
                .userId(10L)
                .roleId(20L)
                .assignedAt(past.minus(1, ChronoUnit.HOURS))
                .expiresAt(past)
                .build();
        assertFalse(expiredRole.isActive(now));

        activeRole.revoke("ADMIN", "No longer required");
        assertFalse(activeRole.isActive(now));
        assertNotNull(activeRole.getRevokedAt());
        assertEquals("No longer required", activeRole.getReason());
    }

    @Test
    @DisplayName("Effective permissions = active role permissions + direct GRANT - direct DENY")
    void testEffectivePermissions() {
        List<String> rolePermissions = List.of("user:read", "user:update", "order:read");

        Map<String, PermissionEffect> direct = new LinkedHashMap<>();
        direct.put("report:read", PermissionEffect.GRANT);
        direct.put("user:update", PermissionEffect.DENY);

        Set<String> effective = EffectivePermissionCalculator.calculate(rolePermissions, direct);

        assertTrue(effective.contains("user:read"));
        assertFalse(effective.contains("user:update"), "user:update should be revoked by direct DENY");
        assertTrue(effective.contains("order:read"));
        assertTrue(effective.contains("report:read"), "report:read should be added by direct GRANT");
        assertEquals(Set.of("user:read", "order:read", "report:read"), effective);
    }
}
