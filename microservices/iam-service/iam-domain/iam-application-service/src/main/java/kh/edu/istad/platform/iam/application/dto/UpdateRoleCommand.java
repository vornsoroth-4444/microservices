package kh.edu.istad.platform.iam.application.dto;

import java.util.UUID;

public class UpdateRoleCommand {
    private UUID roleUuid;
    private String description;

    public UpdateRoleCommand() {}

    public UpdateRoleCommand(UUID roleUuid, String description) {
        this.roleUuid = roleUuid;
        this.description = description;
    }

    public UUID getRoleUuid() { return roleUuid; }
    public void setRoleUuid(UUID roleUuid) { this.roleUuid = roleUuid; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
