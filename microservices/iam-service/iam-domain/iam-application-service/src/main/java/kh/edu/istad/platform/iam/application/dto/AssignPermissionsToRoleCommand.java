package kh.edu.istad.platform.iam.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AssignPermissionsToRoleCommand {
    private UUID roleUuid;
    private List<UUID> permissionUuids;
    private Instant expiresAt;
    private String reason;

    public AssignPermissionsToRoleCommand() {}

    public AssignPermissionsToRoleCommand(UUID roleUuid, List<UUID> permissionUuids, Instant expiresAt, String reason) {
        this.roleUuid = roleUuid;
        this.permissionUuids = permissionUuids;
        this.expiresAt = expiresAt;
        this.reason = reason;
    }

    public UUID getRoleUuid() { return roleUuid; }
    public void setRoleUuid(UUID roleUuid) { this.roleUuid = roleUuid; }
    public List<UUID> getPermissionUuids() { return permissionUuids; }
    public void setPermissionUuids(List<UUID> permissionUuids) { this.permissionUuids = permissionUuids; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
