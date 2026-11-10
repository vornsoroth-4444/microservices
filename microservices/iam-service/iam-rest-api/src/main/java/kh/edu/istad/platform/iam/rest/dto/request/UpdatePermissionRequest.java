package kh.edu.istad.platform.iam.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdatePermissionRequest {

    @NotBlank(message = "resource is required")
    @Size(min = 2, max = 50, message = "resource must be between 2 and 50 characters")
    private String resource;

    @NotBlank(message = "action is required")
    @Size(min = 2, max = 50, message = "action must be between 2 and 50 characters")
    private String action;

    private String description;

    public UpdatePermissionRequest() {}

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
