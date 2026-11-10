package kh.edu.istad.platform.iam.rest.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AssignPermissionsToRoleRequest {

    @NotEmpty(message = "permissionUuids cannot be empty")
    private List<UUID> permissionUuids;

    private Instant expiresAt;
    private String reason;

    public AssignPermissionsToRoleRequest() {}

    public List<UUID> getPermissionUuids() { return permissionUuids; }
    public void setPermissionUuids(List<UUID> permissionUuids) { this.permissionUuids = permissionUuids; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
