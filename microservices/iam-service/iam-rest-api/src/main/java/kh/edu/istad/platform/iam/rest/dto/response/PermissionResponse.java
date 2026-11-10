package kh.edu.istad.platform.iam.rest.dto.response;

import java.util.UUID;

public class PermissionResponse {
    private UUID uuid;
    private String name;
    private String resource;
    private String action;
    private String description;
    private boolean system;
    private boolean active;

    public PermissionResponse() {}

    public PermissionResponse(UUID uuid, String name, String resource, String action, String description, boolean system, boolean active) {
        this.uuid = uuid;
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
        this.system = system;
        this.active = active;
    }

    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isSystem() { return system; }
    public void setSystem(boolean system) { this.system = system; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
