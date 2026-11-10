package kh.edu.istad.platform.iam.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Identity {
    private Long id;
    private UUID uuid;
    private Long userId;
    private IdentityProvider provider;
    private String providerSubject;
    private String email;
    private boolean emailVerified;
    private String displayName;
    private String username;
    private String pictureUrl;
    private String attributes;
    private Instant linkedAt;
    private Instant lastLoginAt;
    private boolean deleted;
    private Instant deletedAt;
    private String deletedBy;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private Long version;

    public Identity() {
    }

    public Identity(Long id, UUID uuid, Long userId, IdentityProvider provider, String providerSubject,
                    String email, boolean emailVerified, String displayName, String username,
                    String pictureUrl, String attributes, Instant linkedAt, Instant lastLoginAt,
                    boolean deleted, Instant deletedAt, String deletedBy, Instant createdAt,
                    String createdBy, Instant updatedAt, String updatedBy, Long version) {
        this.id = id;
        this.uuid = uuid;
        this.userId = userId;
        this.provider = provider;
        this.providerSubject = providerSubject;
        this.email = email;
        this.emailVerified = emailVerified;
        this.displayName = displayName;
        this.username = username;
        this.pictureUrl = pictureUrl;
        this.attributes = attributes;
        this.linkedAt = linkedAt;
        this.lastLoginAt = lastLoginAt;
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

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
        this.updatedAt = this.lastLoginAt;
    }

    public void softDelete(String actor) {
        this.deleted = true;
        this.deletedAt = Instant.now();
        this.deletedBy = actor != null ? actor : "SYSTEM";
        this.updatedAt = this.deletedAt;
        this.updatedBy = this.deletedBy;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public IdentityProvider getProvider() { return provider; }
    public void setProvider(IdentityProvider provider) { this.provider = provider; }
    public String getProviderSubject() { return providerSubject; }
    public void setProviderSubject(String providerSubject) { this.providerSubject = providerSubject; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
    public String getAttributes() { return attributes; }
    public void setAttributes(String attributes) { this.attributes = attributes; }
    public Instant getLinkedAt() { return linkedAt; }
    public void setLinkedAt(Instant linkedAt) { this.linkedAt = linkedAt; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
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
        if (!(o instanceof Identity identity)) return false;
        return Objects.equals(uuid, identity.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public static class Builder {
        private Long id;
        private UUID uuid;
        private Long userId;
        private IdentityProvider provider;
        private String providerSubject;
        private String email;
        private boolean emailVerified = false;
        private String displayName;
        private String username;
        private String pictureUrl;
        private String attributes;
        private Instant linkedAt;
        private Instant lastLoginAt;
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
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder provider(IdentityProvider provider) { this.provider = provider; return this; }
        public Builder providerSubject(String providerSubject) { this.providerSubject = providerSubject; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder emailVerified(boolean emailVerified) { this.emailVerified = emailVerified; return this; }
        public Builder displayName(String displayName) { this.displayName = displayName; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder pictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; return this; }
        public Builder attributes(String attributes) { this.attributes = attributes; return this; }
        public Builder linkedAt(Instant linkedAt) { this.linkedAt = linkedAt; return this; }
        public Builder lastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; return this; }
        public Builder deleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder deletedAt(Instant deletedAt) { this.deletedAt = deletedAt; return this; }
        public Builder deletedBy(String deletedBy) { this.deletedBy = deletedBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        public Identity build() {
            return new Identity(id, uuid, userId, provider, providerSubject, email, emailVerified,
                    displayName, username, pictureUrl, attributes, linkedAt, lastLoginAt,
                    deleted, deletedAt, deletedBy, createdAt, createdBy, updatedAt, updatedBy, version);
        }
    }
}
