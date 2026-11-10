package kh.edu.istad.platform.iam.application.dto;

import java.util.UUID;

public class RoleSummary {
    private UUID uuid;
    private String name;
    private boolean active;

    public RoleSummary() {}

    public RoleSummary(UUID uuid, String name, boolean active) {
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
