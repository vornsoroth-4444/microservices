package kh.edu.istad.platform.iam.rest.dto.request;

import jakarta.validation.constraints.Size;

public class UpdateRoleRequest {
    @Size(min = 2, max = 50)
    private String name;
    private String description;

    public UpdateRoleRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
