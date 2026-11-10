package kh.edu.istad.platform.iam.application.dto;

import java.util.UUID;

public class ResetUserPasswordCommand {
    private UUID userUuid;
    private String newPassword;

    public ResetUserPasswordCommand() {}

    public ResetUserPasswordCommand(UUID userUuid, String newPassword) {
        this.userUuid = userUuid;
        this.newPassword = newPassword;
    }

    public UUID getUserUuid() { return userUuid; }
    public void setUserUuid(UUID userUuid) { this.userUuid = userUuid; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
