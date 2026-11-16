package kh.edu.istad.platform.iam.rest.controller;

import kh.edu.istad.platform.iam.application.usecase.user.DisableUserUseCase;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class SecurityActivityController {
    private final JdbcOperations jdbc;
    private final DisableUserUseCase disableUser;
    private final OAuth2AuthorizationService authorizations;

    public SecurityActivityController(JdbcOperations jdbc, DisableUserUseCase disableUser, OAuth2AuthorizationService authorizations) {
        this.jdbc = jdbc;
        this.disableUser = disableUser;
        this.authorizations = authorizations;
    }

    @GetMapping("/api/me/login-history")
    public List<Map<String, Object>> myLoginHistory(@AuthenticationPrincipal Jwt jwt,
                                                     @RequestParam(name = "size", defaultValue = "100") int size) {
        String username = jdbc.query("SELECT username FROM users WHERE uuid = ?", rs -> rs.next() ? rs.getString(1) : jwt.getSubject(), UUID.fromString(jwt.getSubject()));
        return loginHistory(username, size);
    }

    @GetMapping("/api/users/{uuid}/login-history")
    @PreAuthorize("hasAuthority('user:read')")
    public List<Map<String, Object>> userLoginHistory(@PathVariable("uuid") UUID uuid,
                                                       @RequestParam(name = "size", defaultValue = "100") int size) {
        String username = jdbc.query("SELECT username FROM users WHERE uuid = ?", rs -> rs.next() ? rs.getString(1) : null, uuid);
        if (username == null) return List.of();
        return loginHistory(username, size);
    }

    private List<Map<String, Object>> loginHistory(String principal, int size) {
        int limit = Math.max(1, Math.min(size, 500));
        return jdbc.queryForList("SELECT id, event_type AS type, actor, detail, ip_address AS \"ipAddress\", user_agent AS \"userAgent\", succeeded, created_at AS \"createdAt\" FROM iam_security_event WHERE actor = ? AND event_type IN ('LOGIN_SUCCESS','LOGIN_FAILURE') ORDER BY created_at DESC LIMIT ?", principal, limit);
    }

    @GetMapping("/api/me/activity")
    public List<Map<String, Object>> myActivity(@AuthenticationPrincipal Jwt jwt,
                                                 @RequestParam(name = "size", defaultValue = "100") int size) {
        int limit = Math.max(1, Math.min(size, 500));
        String username = jdbc.query("SELECT username FROM users WHERE uuid = ?", rs -> rs.next() ? rs.getString(1) : jwt.getSubject(), UUID.fromString(jwt.getSubject()));
        return jdbc.queryForList("SELECT id, event_type AS type, actor, target, detail, ip_address AS \"ipAddress\", user_agent AS \"userAgent\", succeeded, created_at AS \"createdAt\" FROM iam_security_event WHERE actor IN (?, ?) OR target IN (?, ?) ORDER BY created_at DESC LIMIT ?", jwt.getSubject(), username, jwt.getSubject(), username, limit);
    }

    @GetMapping("/api/audit-logs")
    @PreAuthorize("hasAuthority('user:read')")
    public List<Map<String, Object>> auditLogs(@RequestParam(name = "search", required = false) String search,
                                                @RequestParam(name = "type", required = false) String type,
                                                @RequestParam(name = "size", defaultValue = "100") int size) {
        int limit = Math.max(1, Math.min(size, 500));
        String text = search == null ? "" : "%" + search.trim() + "%";
        String eventType = type == null || type.isBlank() ? "%" : type;
        return jdbc.queryForList("SELECT id, event_type AS type, actor, target, detail, ip_address AS \"ipAddress\", user_agent AS \"userAgent\", succeeded, created_at AS \"createdAt\" FROM iam_security_event WHERE (CAST(? AS varchar) = '%' OR event_type = ?) AND (CAST(? AS varchar) = '' OR coalesce(actor,'') ILIKE ? OR coalesce(target,'') ILIKE ? OR coalesce(detail,'') ILIKE ?) ORDER BY created_at DESC LIMIT ?", eventType, eventType, search == null ? "" : search.trim(), text, text, text, limit);
    }

    @GetMapping("/api/me/sessions")
    public List<Map<String, Object>> mySessions(@AuthenticationPrincipal Jwt jwt) {
        return sessions(jwt.getSubject());
    }

    @DeleteMapping("/api/me/sessions")
    public Map<String, Integer> revokeMySessions(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("terminated", revokeSessions("principal_name IN (SELECT username FROM users WHERE uuid = ? UNION SELECT email FROM users WHERE uuid = ? UNION SELECT uuid::text FROM users WHERE uuid = ?)", UUID.fromString(jwt.getSubject()), UUID.fromString(jwt.getSubject()), UUID.fromString(jwt.getSubject())));
    }

    @DeleteMapping("/api/me/sessions/{sessionId}")
    public Map<String, Integer> revokeMySession(@AuthenticationPrincipal Jwt jwt, @PathVariable("sessionId") String sessionId) {
        return Map.of("terminated", revokeOne("id = ? AND principal_name IN (SELECT username FROM users WHERE uuid = ? UNION SELECT email FROM users WHERE uuid = ? UNION SELECT uuid::text FROM users WHERE uuid = ?)", sessionId, UUID.fromString(jwt.getSubject()), UUID.fromString(jwt.getSubject()), UUID.fromString(jwt.getSubject())));
    }

    @GetMapping("/api/users/{uuid}/sessions")
    @PreAuthorize("hasAuthority('user:read')")
    public List<Map<String, Object>> userSessions(@PathVariable("uuid") UUID uuid) {
        return sessionsForUuid(uuid);
    }

    @DeleteMapping("/api/users/{uuid}/sessions")
    @PreAuthorize("hasAuthority('user:update')")
    public Map<String, Integer> revokeUserSessions(@PathVariable("uuid") UUID uuid) {
        return Map.of("terminated", revokeSessions("principal_name IN (SELECT username FROM users WHERE uuid = ? UNION SELECT email FROM users WHERE uuid = ? UNION SELECT uuid::text FROM users WHERE uuid = ?)", uuid, uuid, uuid));
    }

    @DeleteMapping("/api/users/{uuid}/sessions/{sessionId}")
    @PreAuthorize("hasAuthority('user:update')")
    public Map<String, Integer> revokeUserSession(@PathVariable("uuid") UUID uuid, @PathVariable("sessionId") String sessionId) {
        return Map.of("terminated", revokeOne("id = ? AND principal_name IN (SELECT username FROM users WHERE uuid = ? UNION SELECT email FROM users WHERE uuid = ? UNION SELECT uuid::text FROM users WHERE uuid = ?)", sessionId, uuid, uuid, uuid));
    }

    private List<Map<String, Object>> sessions(String subject) {
        try {
            return sessionsForUuid(UUID.fromString(subject));
        } catch (IllegalArgumentException ex) {
            return jdbc.queryForList("SELECT a.id, a.principal_name AS \"principalName\", c.client_name AS \"clientName\", a.access_token_issued_at AS \"lastActivity\", a.access_token_expires_at AS \"expiresAt\", a.access_token_scopes AS scopes, e.ip_address AS \"ipAddress\", e.user_agent AS \"userAgent\" FROM oauth2_authorization a LEFT JOIN oauth2_registered_client c ON c.id = a.registered_client_id LEFT JOIN LATERAL (SELECT ip_address,user_agent FROM iam_security_event WHERE actor=a.principal_name AND event_type='LOGIN_SUCCESS' ORDER BY created_at DESC LIMIT 1) e ON true WHERE a.principal_name = ? AND a.access_token_expires_at > CURRENT_TIMESTAMP ORDER BY a.access_token_issued_at DESC", subject);
        }
    }

    private List<Map<String, Object>> sessionsForUuid(UUID uuid) {
        return jdbc.queryForList("SELECT a.id, a.principal_name AS \"principalName\", c.client_name AS \"clientName\", a.access_token_issued_at AS \"lastActivity\", a.access_token_expires_at AS \"expiresAt\", a.access_token_scopes AS scopes, e.ip_address AS \"ipAddress\", e.user_agent AS \"userAgent\" FROM oauth2_authorization a JOIN users u ON a.principal_name IN (u.username, u.email, u.uuid::text) LEFT JOIN oauth2_registered_client c ON c.id = a.registered_client_id LEFT JOIN LATERAL (SELECT ip_address,user_agent FROM iam_security_event WHERE actor=a.principal_name AND event_type='LOGIN_SUCCESS' ORDER BY created_at DESC LIMIT 1) e ON true WHERE u.uuid = ? AND a.access_token_expires_at > CURRENT_TIMESTAMP ORDER BY a.access_token_issued_at DESC", uuid);
    }

    private int revokeSessions(String predicate, Object... parameters) {
        List<String> ids = jdbc.queryForList("SELECT id FROM oauth2_authorization WHERE " + predicate, String.class, parameters);
        return ids.stream().mapToInt(this::revokeAuthorization).sum();
    }

    private int revokeOne(String predicate, Object... parameters) {
        List<String> ids = jdbc.queryForList("SELECT id FROM oauth2_authorization WHERE " + predicate, String.class, parameters);
        return ids.stream().mapToInt(this::revokeAuthorization).sum();
    }

    private int revokeAuthorization(String id) {
        OAuth2Authorization authorization = authorizations.findById(id);
        if (authorization == null) return 0;
        if (authorization.getAccessToken() != null && authorization.getAccessToken().getClaims() != null) {
            Object jti = authorization.getAccessToken().getClaims().get("jti");
            var expiresAt = authorization.getAccessToken().getToken().getExpiresAt();
            if (jti != null && expiresAt != null) jdbc.update("INSERT INTO iam_revoked_token(jti,expires_at) VALUES (?, ?) ON CONFLICT (jti) DO NOTHING", jti.toString(), expiresAt);
        }
        authorizations.remove(authorization);
        return 1;
    }

    @PostMapping("/api/me/deactivation-request")
    public void requestDeactivation(@AuthenticationPrincipal Jwt jwt) {
        UUID uuid = UUID.fromString(jwt.getSubject());
        disableUser.execute(uuid);
        revokeSessions("principal_name IN (SELECT username FROM users WHERE uuid = ? UNION SELECT email FROM users WHERE uuid = ? UNION SELECT uuid::text FROM users WHERE uuid = ?)", uuid, uuid, uuid);
    }
}
