package kh.edu.istad.platform.iam.application.dto;

import java.util.UUID;

public class ChangePasswordCommand {
    private UUID userUuid;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public ChangePasswordCommand() {}

    public ChangePasswordCommand(UUID userUuid, String currentPassword, String newPassword, String confirmPassword) {
        this.userUuid = userUuid;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public UUID getUserUuid() { return userUuid; }
    public void setUserUuid(UUID userUuid) { this.userUuid = userUuid; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
