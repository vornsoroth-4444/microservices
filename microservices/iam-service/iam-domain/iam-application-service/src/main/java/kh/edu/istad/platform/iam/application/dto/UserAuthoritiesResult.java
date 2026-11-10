package kh.edu.istad.platform.iam.application.dto;

import kh.edu.istad.platform.iam.domain.model.PermissionEffect;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserAuthoritiesResult {
    private List<RoleAuthority> roles;
    private List<DirectPermissionAuthority> directPermissions;
    private Set<String> effectivePermissions;

    public UserAuthoritiesResult() {}

    public UserAuthoritiesResult(List<RoleAuthority> roles,
                                 List<DirectPermissionAuthority> directPermissions,
                                 Set<String> effectivePermissions) {
        this.roles = roles;
        this.directPermissions = directPermissions;
        this.effectivePermissions = effectivePermissions;
    }

    public List<RoleAuthority> getRoles() { return roles; }
    public void setRoles(List<RoleAuthority> roles) { this.roles = roles; }
    public List<DirectPermissionAuthority> getDirectPermissions() { return directPermissions; }
    public void setDirectPermissions(List<DirectPermissionAuthority> directPermissions) { this.directPermissions = directPermissions; }
    public Set<String> getEffectivePermissions() { return effectivePermissions; }
    public void setEffectivePermissions(Set<String> effectivePermissions) { this.effectivePermissions = effectivePermissions; }

    public static class RoleAuthority {
        private UUID uuid;
        private String name;
        private Instant expiresAt;

        public RoleAuthority() {}

        public RoleAuthority(UUID uuid, String name, Instant expiresAt) {
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

    public static class DirectPermissionAuthority {
        private String name;
        private PermissionEffect effect;

        public DirectPermissionAuthority() {}

        public DirectPermissionAuthority(String name, PermissionEffect effect) {
            this.name = name;
            this.effect = effect;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public PermissionEffect getEffect() { return effect; }
        public void setEffect(PermissionEffect effect) { this.effect = effect; }
    }
}
