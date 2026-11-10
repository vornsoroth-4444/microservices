package kh.edu.istad.platform.iam.rest.dto.response;

import kh.edu.istad.platform.iam.domain.model.PermissionEffect;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserAuthoritiesResponse {
    private List<RoleAuthorityResponse> roles;
    private List<DirectPermissionResponse> directPermissions;
    private Set<String> effectivePermissions;

    public UserAuthoritiesResponse() {}

    public UserAuthoritiesResponse(List<RoleAuthorityResponse> roles,
                                   List<DirectPermissionResponse> directPermissions,
                                   Set<String> effectivePermissions) {
        this.roles = roles;
        this.directPermissions = directPermissions;
        this.effectivePermissions = effectivePermissions;
    }

    public List<RoleAuthorityResponse> getRoles() { return roles; }
    public void setRoles(List<RoleAuthorityResponse> roles) { this.roles = roles; }
    public List<DirectPermissionResponse> getDirectPermissions() { return directPermissions; }
    public void setDirectPermissions(List<DirectPermissionResponse> directPermissions) { this.directPermissions = directPermissions; }
    public Set<String> getEffectivePermissions() { return effectivePermissions; }
    public void setEffectivePermissions(Set<String> effectivePermissions) { this.effectivePermissions = effectivePermissions; }

    public static class RoleAuthorityResponse {
        private UUID uuid;
        private String name;
        private Instant expiresAt;

        public RoleAuthorityResponse() {}

        public RoleAuthorityResponse(UUID uuid, String name, Instant expiresAt) {
            this.uuid = uuid;
            this.name = name;
            this.expiresAt = expiresAt;
        }

        public UUID getUuid() { return uuid; }
        public void setUuid(UUID uuid) { this.uuid = uuid; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Instant getExpiresAt() { return expiresAt; }
        public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    }

    public static class DirectPermissionResponse {
        private String name;
        private PermissionEffect effect;

        public DirectPermissionResponse() {}

        public DirectPermissionResponse(String name, PermissionEffect effect) {
            this.name = name;
            this.effect = effect;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public PermissionEffect getEffect() { return effect; }
        public void setEffect(PermissionEffect effect) { this.effect = effect; }
    }
}
