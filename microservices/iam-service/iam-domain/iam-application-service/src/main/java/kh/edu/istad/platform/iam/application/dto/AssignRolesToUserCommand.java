package kh.edu.istad.platform.iam.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AssignRolesToUserCommand {
    private UUID userUuid;
    private List<UUID> roleUuids;
    private Instant expiresAt;
    private String reason;

    public AssignRolesToUserCommand() {}

    public AssignRolesToUserCommand(UUID userUuid, List<UUID> roleUuids, Instant expiresAt, String reason) {
        this.userUuid = userUuid;
        this.roleUuids = roleUuids;
        this.expiresAt = expiresAt;
        this.reason = reason;
    }

    public UUID getUserUuid() { return userUuid; }
    public void setUserUuid(UUID userUuid) { this.userUuid = userUuid; }
    public List<UUID> getRoleUuids() { return roleUuids; }
    public void setRoleUuids(List<UUID> roleUuids) { this.roleUuids = roleUuids; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
