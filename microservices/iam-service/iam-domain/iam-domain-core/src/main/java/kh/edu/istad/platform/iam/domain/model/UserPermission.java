package kh.edu.istad.platform.iam.domain.model;

import java.time.Instant;
import java.util.Objects;

public class UserPermission {
    private Long id;
    private Long userId;
    private Long permissionId;
    private PermissionEffect effect;
    private Instant assignedAt;
    private String assignedBy;
    private Instant expiresAt;
    private Instant revokedAt;
    private String revokedBy;
    private String reason;

    public UserPermission() {
    }

    public UserPermission(Long id, Long userId, Long permissionId, PermissionEffect effect,
                          Instant assignedAt, String assignedBy, Instant expiresAt,
                          Instant revokedAt, String revokedBy, String reason) {
        this.id = id;
        this.userId = userId;
        this.permissionId = permissionId;
        this.effect = effect;
        this.assignedAt = assignedAt;
        this.assignedBy = assignedBy;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.revokedBy = revokedBy;
        this.reason = reason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isActive(Instant now) {
        return revokedAt == null && (expiresAt == null || expiresAt.isAfter(now));
    }

    public void revoke(String revokedBy, String reason) {
        this.revokedAt = Instant.now();
        this.revokedBy = revokedBy != null ? revokedBy : "SYSTEM";
        this.reason = reason;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }
    public PermissionEffect getEffect() { return effect; }
    public void setEffect(PermissionEffect effect) { this.effect = effect; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
    public String getRevokedBy() { return revokedBy; }
    public void setRevokedBy(String revokedBy) { this.revokedBy = revokedBy; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPermission that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long permissionId;
        private PermissionEffect effect;
        private Instant assignedAt;
        private String assignedBy;
        private Instant expiresAt;
        private Instant revokedAt;
        private String revokedBy;
        private String reason;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder permissionId(Long permissionId) { this.permissionId = permissionId; return this; }
        public Builder effect(PermissionEffect effect) { this.effect = effect; return this; }
        public Builder assignedAt(Instant assignedAt) { this.assignedAt = assignedAt; return this; }
        public Builder assignedBy(String assignedBy) { this.assignedBy = assignedBy; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder revokedAt(Instant revokedAt) { this.revokedAt = revokedAt; return this; }
        public Builder revokedBy(String revokedBy) { this.revokedBy = revokedBy; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }

        public UserPermission build() {
            return new UserPermission(id, userId, permissionId, effect, assignedAt, assignedBy, expiresAt, revokedAt, revokedBy, reason);
        }
    }
}
