package kh.edu.istad.platform.iam.application.dto;

import java.time.Instant;
import java.util.UUID;

public class PermissionAssignmentResult {
    private UUID permissionUuid;
    private String permissionName;
    private Instant assignedAt;
    private Instant expiresAt;
    private String reason;

    public PermissionAssignmentResult() {}

    public PermissionAssignmentResult(UUID permissionUuid, String permissionName, Instant assignedAt, Instant expiresAt, String reason) {
        this.permissionUuid = permissionUuid;
        this.permissionName = permissionName;
        this.assignedAt = assignedAt;
        this.expiresAt = expiresAt;
        this.reason = reason;
    }

    public UUID getPermissionUuid() { return permissionUuid; }
    public void setPermissionUuid(UUID permissionUuid) { this.permissionUuid = permissionUuid; }
    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
