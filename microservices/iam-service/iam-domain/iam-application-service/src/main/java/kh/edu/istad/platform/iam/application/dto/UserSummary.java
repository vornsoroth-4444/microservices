package kh.edu.istad.platform.iam.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserSummary {
    private UUID uuid;
    private String username;
    private String email;
    private String name;
    private String locale;
    private boolean enabled;
    private List<RoleSummary> roles;
    private Instant createdAt;
    private Instant updatedAt;

    public UserSummary() {}

    public UserSummary(UUID uuid, String username, String email, String name, String locale,
                       boolean enabled, List<RoleSummary> roles, Instant createdAt, Instant updatedAt) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public List<RoleSummary> getRoles() { return roles; }
    public void setRoles(List<RoleSummary> roles) { this.roles = roles; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
