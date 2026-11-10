package kh.edu.istad.platform.iam.application.dto;

public class CreatePermissionCommand {
    private String resource;
    private String action;
    private String description;

    public CreatePermissionCommand() {}

    public CreatePermissionCommand(String resource, String action, String description) {
        this.resource = resource;
        this.action = action;
        this.description = description;
    }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
