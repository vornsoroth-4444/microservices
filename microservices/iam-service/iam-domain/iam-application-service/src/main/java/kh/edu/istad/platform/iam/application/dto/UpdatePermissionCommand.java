package kh.edu.istad.platform.iam.application.dto;

import java.util.UUID;

public class UpdatePermissionCommand {
    private UUID permissionUuid;
    private String resource;
    private String action;
    private String description;

    public UpdatePermissionCommand() {}

    public UpdatePermissionCommand(UUID permissionUuid, String resource, String action, String description) {
        this.permissionUuid = permissionUuid;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }

    public UUID getPermissionUuid() { return permissionUuid; }
    public void setPermissionUuid(UUID permissionUuid) { this.permissionUuid = permissionUuid; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
