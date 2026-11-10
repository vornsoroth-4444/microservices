package kh.edu.istad.platform.iam.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RoleResult {
    private UUID uuid;
    private String name;
    private String description;
    private boolean system;
    private boolean active;
    private List<PermissionResult> permissions;
    private Instant createdAt;
    private Instant updatedAt;

    public RoleResult() {}

    public static Builder builder() {
        return new Builder();
    }

    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isSystem() { return system; }
    public void setSystem(boolean system) { this.system = system; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<PermissionResult> getPermissions() { return permissions; }
    public void setPermissions(List<PermissionResult> permissions) { this.permissions = permissions; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private final RoleResult target = new RoleResult();

        public Builder uuid(UUID uuid) { target.setUuid(uuid); return this; }
        public Builder name(String name) { target.setName(name); return this; }
        public Builder description(String desc) { target.setDescription(desc); return this; }
        public Builder system(boolean sys) { target.setSystem(sys); return this; }
        public Builder active(boolean act) { target.setActive(act); return this; }
        public Builder permissions(List<PermissionResult> perms) { target.setPermissions(perms); return this; }
        public Builder createdAt(Instant created) { target.setCreatedAt(created); return this; }
        public Builder updatedAt(Instant updated) { target.setUpdatedAt(updated); return this; }

        public RoleResult build() { return target; }
    }
}
