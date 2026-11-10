package kh.edu.istad.platform.iam.application.dto;

import java.util.List;
import java.util.UUID;

public class CreateRoleCommand {
    private String name;
    private String description;
    private List<UUID> permissionUuids;

    public CreateRoleCommand() {}

    public CreateRoleCommand(String name, String description, List<UUID> permissionUuids) {
        this.name = name;
        this.description = description;
        this.permissionUuids = permissionUuids;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<UUID> getPermissionUuids() { return permissionUuids; }
    public void setPermissionUuids(List<UUID> permissionUuids) { this.permissionUuids = permissionUuids; }
}
