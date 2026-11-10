package kh.edu.istad.platform.iam.application.dto;

import java.time.Instant;
import java.util.UUID;

public class RoleAssignmentResult {
    private UUID roleUuid;
    private String roleName;
    private Instant assignedAt;
    private Instant expiresAt;
    private String reason;

    public RoleAssignmentResult() {}

    public RoleAssignmentResult(UUID roleUuid, String roleName, Instant assignedAt, Instant expiresAt, String reason) {
        this.roleUuid = roleUuid;
        this.roleName = roleName;
        this.assignedAt = assignedAt;
        this.expiresAt = expiresAt;
        this.reason = reason;
    }

    public UUID getRoleUuid() { return roleUuid; }
    public void setRoleUuid(UUID roleUuid) { this.roleUuid = roleUuid; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
