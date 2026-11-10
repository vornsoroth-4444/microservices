package kh.edu.istad.platform.iam.rest.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AssignRolesToUserRequest {

    @NotEmpty(message = "roleUuids cannot be empty")
    private List<UUID> roleUuids;

    private Instant expiresAt;
    private String reason;

    public AssignRolesToUserRequest() {}

    public List<UUID> getRoleUuids() { return roleUuids; }
    public void setRoleUuids(List<UUID> roleUuids) { this.roleUuids = roleUuids; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
