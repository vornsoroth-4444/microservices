package kh.edu.istad.platform.iam.application.dto;

import kh.edu.istad.platform.iam.domain.model.PermissionEffect;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class GrantPermissionsToUserCommand {
    private UUID userUuid;
    private List<UUID> permissionUuids;
    private PermissionEffect effect;
    private Instant expiresAt;
    private String reason;

    public GrantPermissionsToUserCommand() {}

    public GrantPermissionsToUserCommand(UUID userUuid, List<UUID> permissionUuids, PermissionEffect effect,
                                        Instant expiresAt, String reason) {
        this.userUuid = userUuid;
        this.permissionUuids = permissionUuids;
        this.effect = effect;
        this.expiresAt = expiresAt;
        this.reason = reason;
    }

    public UUID getUserUuid() { return userUuid; }
    public void setUserUuid(UUID userUuid) { this.userUuid = userUuid; }
    public List<UUID> getPermissionUuids() { return permissionUuids; }
    public void setPermissionUuids(List<UUID> permissionUuids) { this.permissionUuids = permissionUuids; }
    public PermissionEffect getEffect() { return effect; }
    public void setEffect(PermissionEffect effect) { this.effect = effect; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
