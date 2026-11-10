package kh.edu.istad.platform.iam.application.dto;

import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.User;

import java.util.List;
import java.util.Set;

public class AuthUserResult {
    private User user;
    private List<Role> roles;
    private Set<String> permissions;
    private Set<String> authorities;

    public AuthUserResult() {}

    public AuthUserResult(User user, List<Role> roles, Set<String> permissions, Set<String> authorities) {
        this.user = user;
        this.roles = roles;
        this.permissions = permissions;
        this.authorities = authorities;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    public Set<String> getAuthorities() { return authorities; }
    public void setAuthorities(Set<String> authorities) { this.authorities = authorities; }
}
