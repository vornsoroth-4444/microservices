package kh.edu.istad.platform.iam.rest.dto.response;

import java.util.UUID;

public class RoleResponse {
    private UUID uuid;
    private String name;
    private boolean active;

    public RoleResponse() {}

    public RoleResponse(UUID uuid, String name, boolean active) {
        this.uuid = uuid;
        this.name = name;
        this.active = active;
    }

    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
