package kh.edu.istad.platform.iam.rest.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserResponse {
    private UUID uuid;
    private String username;
    private String email;
    private boolean emailVerified;
    private String name;
    private String locale;
    private boolean enabled;
    private List<RoleItemResponse> roles;
    private Instant createdAt;
    private Instant updatedAt;

    public UserResponse() {}

    public UserResponse(UUID uuid, String username, String email, boolean emailVerified,
                        String name, String locale, boolean enabled, List<RoleItemResponse> roles,
                        Instant createdAt, Instant updatedAt) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.emailVerified = emailVerified;
        this.name = name;
        this.locale = locale;
        this.enabled = enabled;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public List<RoleItemResponse> getRoles() { return roles; }
    public void setRoles(List<RoleItemResponse> roles) { this.roles = roles; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
