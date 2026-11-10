package kh.edu.istad.platform.iam.rest.dto.response;

import kh.edu.istad.platform.iam.domain.model.IdentityProvider;

import java.time.Instant;
import java.util.UUID;

public class IdentityResponse {
    private UUID uuid;
    private IdentityProvider provider;
    private String email;
    private String displayName;
    private Instant linkedAt;

    public IdentityResponse() {}

    public IdentityResponse(UUID uuid, IdentityProvider provider, String email, String displayName, Instant linkedAt) {
        this.uuid = uuid;
        this.provider = provider;
        this.email = email;
        this.displayName = displayName;
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
    public Instant getLinkedAt() { return linkedAt; }
    public void setLinkedAt(Instant linkedAt) { this.linkedAt = linkedAt; }
}
