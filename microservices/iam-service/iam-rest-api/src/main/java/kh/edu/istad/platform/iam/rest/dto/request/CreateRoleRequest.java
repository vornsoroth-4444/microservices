package kh.edu.istad.platform.iam.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public class CreateRoleRequest {

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
    private String name;

    private String description;
    private List<UUID> permissionUuids;

    public CreateRoleRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<UUID> getPermissionUuids() { return permissionUuids; }
    public void setPermissionUuids(List<UUID> permissionUuids) { this.permissionUuids = permissionUuids; }
}
