package kh.edu.istad.platform.iam.application.dto;

import kh.edu.istad.platform.iam.domain.model.IdentityProvider;

public class FederatedIdentityCommand {
    private IdentityProvider provider;
    private String providerSubject;
    private String email;
    private boolean emailVerified;
    private String displayName;
    private String username;
    private String pictureUrl;
    private String attributes;

    public FederatedIdentityCommand() {}

    public static Builder builder() { return new Builder(); }

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

    public static class Builder {
        private final FederatedIdentityCommand target = new FederatedIdentityCommand();
        public Builder provider(IdentityProvider p) { target.setProvider(p); return this; }
        public Builder providerSubject(String s) { target.setProviderSubject(s); return this; }
        public Builder email(String e) { target.setEmail(e); return this; }
        public Builder emailVerified(boolean ev) { target.setEmailVerified(ev); return this; }
        public Builder displayName(String dn) { target.setDisplayName(dn); return this; }
        public Builder username(String u) { target.setUsername(u); return this; }
        public Builder pictureUrl(String pic) { target.setPictureUrl(pic); return this; }
        public Builder attributes(String a) { target.setAttributes(a); return this; }
        public FederatedIdentityCommand build() { return target; }
    }
}
