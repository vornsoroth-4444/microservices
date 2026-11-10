package kh.edu.istad.platform.iam.application.dto;

import kh.edu.istad.platform.iam.domain.model.IdentityProvider;

import java.time.Instant;
import java.util.UUID;

public class IdentityResult {
    private UUID uuid;
    private IdentityProvider provider;
    private String email;
    private String displayName;
    private String username;
    private String pictureUrl;
    private Instant linkedAt;

    public IdentityResult() {}

    public IdentityResult(UUID uuid, IdentityProvider provider, String email, String displayName,
                          String username, String pictureUrl, Instant linkedAt) {
        this.uuid = uuid;
        this.provider = provider;
        this.email = email;
        this.displayName = displayName;
        this.username = username;
        this.pictureUrl = pictureUrl;
        this.linkedAt = linkedAt;
    }

    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public IdentityProvider getProvider() { return provider; }
    public void setProvider(IdentityProvider provider) { this.provider = provider; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
    public Instant getLinkedAt() { return linkedAt; }
    public void setLinkedAt(Instant linkedAt) { this.linkedAt = linkedAt; }
}
