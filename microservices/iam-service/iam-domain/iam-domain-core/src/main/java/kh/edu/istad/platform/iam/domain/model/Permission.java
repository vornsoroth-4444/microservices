package kh.edu.istad.platform.iam.domain.model;

import kh.edu.istad.platform.iam.domain.exception.SystemResourceProtectedException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Permission {
    private Long id;
    private UUID uuid;
    private String name;
    private String resource;
    private String action;
    private String description;
    private boolean system;
    private boolean active;
    private boolean deleted;
    private Instant deletedAt;
    private String deletedBy;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private Long version;

    public Permission() {
    }

    public Permission(Long id, UUID uuid, String name, String resource, String action, String description,
                      boolean system, boolean active, boolean deleted, Instant deletedAt, String deletedBy,
                      Instant createdAt, String createdBy, Instant updatedAt, String updatedBy, Long version) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
        this.system = system;
        this.active = active;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void update(String resource, String action, String description, String actor) {
        if (this.system) {
            throw new SystemResourceProtectedException("System permission cannot be updated: " + this.name);
        }
        this.resource = resource;
        this.action = action;
        this.name = resource + ":" + action;
        this.description = description;
        this.updatedAt = Instant.now();
        this.updatedBy = actor != null ? actor : "SYSTEM";
    }

    public void softDelete(String actor) {
        if (this.system) {
            throw new SystemResourceProtectedException("System permission cannot be deleted: " + this.name);
        }
        this.deleted = true;
        this.deletedAt = Instant.now();
        this.deletedBy = actor != null ? actor : "SYSTEM";
        this.updatedAt = this.deletedAt;
        this.updatedBy = this.deletedBy;
    }

    public void activate(String actor) {
        this.active = true;
        this.updatedAt = Instant.now();
        this.updatedBy = actor != null ? actor : "SYSTEM";
    }

    public void deactivate(String actor) {
        if (this.system) {
            throw new SystemResourceProtectedException("System permission cannot be deactivated: " + this.name);
        }
        this.active = false;
        this.updatedAt = Instant.now();
        this.updatedBy = actor != null ? actor : "SYSTEM";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission that)) return false;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public static class Builder {
        private Long id;
        private UUID uuid;
        private String name;
        private String resource;
        private String action;
        private String description;
        private boolean system;
        private boolean active = true;
        private boolean deleted = false;
        private Instant deletedAt;
        private String deletedBy;
        private Instant createdAt;
        private String createdBy;
        private Instant updatedAt;
        private String updatedBy;
        private Long version = 0L;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder uuid(UUID uuid) { this.uuid = uuid; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder resource(String resource) { this.resource = resource; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder system(boolean system) { this.system = system; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder deleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder deletedAt(Instant deletedAt) { this.deletedAt = deletedAt; return this; }
        public Builder deletedBy(String deletedBy) { this.deletedBy = deletedBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        public Permission build() {
            return new Permission(id, uuid, name, resource, action, description, system, active,
                    deleted, deletedAt, deletedBy, createdAt, createdBy, updatedAt, updatedBy, version);
        }
    }
}
