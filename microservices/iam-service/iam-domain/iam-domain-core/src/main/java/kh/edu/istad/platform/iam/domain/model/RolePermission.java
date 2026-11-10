package kh.edu.istad.platform.iam.domain.model;

import java.time.Instant;
import java.util.Objects;

public class RolePermission {
    private Long id;
    private Long roleId;
    private Long permissionId;
    private Instant assignedAt;
    private String assignedBy;
    private Instant expiresAt;
    private Instant revokedAt;
    private String revokedBy;
    private String reason;

    public RolePermission() {
    }

    public RolePermission(Long id, Long roleId, Long permissionId, Instant assignedAt,
                          String assignedBy, Instant expiresAt, Instant revokedAt,
                          String revokedBy, String reason) {
        this.id = id;
        this.roleId = roleId;
        this.permissionId = permissionId;
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
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }
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
        if (!(o instanceof RolePermission that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private Long id;
        private Long roleId;
        private Long permissionId;
        private Instant assignedAt;
        private String assignedBy;
        private Instant expiresAt;
        private Instant revokedAt;
        private String revokedBy;
        private String reason;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder roleId(Long roleId) { this.roleId = roleId; return this; }
        public Builder permissionId(Long permissionId) { this.permissionId = permissionId; return this; }
        public Builder assignedAt(Instant assignedAt) { this.assignedAt = assignedAt; return this; }
        public Builder assignedBy(String assignedBy) { this.assignedBy = assignedBy; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder revokedAt(Instant revokedAt) { this.revokedAt = revokedAt; return this; }
        public Builder revokedBy(String revokedBy) { this.revokedBy = revokedBy; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }

        public RolePermission build() {
            return new RolePermission(id, roleId, permissionId, assignedAt, assignedBy, expiresAt, revokedAt, revokedBy, reason);
        }
    }
}
