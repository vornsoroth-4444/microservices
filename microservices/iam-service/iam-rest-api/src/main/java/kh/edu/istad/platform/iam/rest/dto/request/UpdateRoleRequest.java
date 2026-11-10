package kh.edu.istad.platform.iam.rest.dto.request;

public class UpdateRoleRequest {
    private String description;

    public UpdateRoleRequest() {}

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
