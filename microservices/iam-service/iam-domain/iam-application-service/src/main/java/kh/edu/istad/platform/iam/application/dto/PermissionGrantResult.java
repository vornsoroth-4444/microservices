package kh.edu.istad.platform.iam.application.dto;

import kh.edu.istad.platform.iam.domain.model.PermissionEffect;

import java.time.Instant;
import java.util.UUID;

public class PermissionGrantResult {
    private UUID permissionUuid;
    private String permissionName;
    private PermissionEffect effect;
    private Instant assignedAt;
    private Instant expiresAt;
    private String reason;

    public PermissionGrantResult() {}

    public PermissionGrantResult(UUID permissionUuid, String permissionName, PermissionEffect effect,
                                 Instant assignedAt, Instant expiresAt, String reason) {
        this.permissionUuid = permissionUuid;
        this.permissionName = permissionName;
        this.effect = effect;
        this.assignedAt = assignedAt;
        this.expiresAt = expiresAt;
        this.reason = reason;
    }

    public UUID getPermissionUuid() { return permissionUuid; }
    public void setPermissionUuid(UUID permissionUuid) { this.permissionUuid = permissionUuid; }
    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }
    public PermissionEffect getEffect() { return effect; }
    public void setEffect(PermissionEffect effect) { this.effect = effect; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
