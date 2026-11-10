package kh.edu.istad.platform.iam.application.dto;

public class RegisterUserCommand {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String givenName;
    private String familyName;
    private String locale;

    public RegisterUserCommand() {}

    public RegisterUserCommand(String username, String email, String password, String confirmPassword,
                               String givenName, String familyName, String locale) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.givenName = givenName;
        this.familyName = familyName;
        this.locale = locale;
    }

    public static Builder builder() { return new Builder(); }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }
    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public static class Builder {
        private final RegisterUserCommand target = new RegisterUserCommand();
        public Builder username(String username) { target.setUsername(username); return this; }
        public Builder email(String email) { target.setEmail(email); return this; }
        public Builder password(String password) { target.setPassword(password); return this; }
        public Builder confirmPassword(String confirmPassword) { target.setConfirmPassword(confirmPassword); return this; }
        public Builder givenName(String givenName) { target.setGivenName(givenName); return this; }
        public Builder familyName(String familyName) { target.setFamilyName(familyName); return this; }
        public Builder locale(String locale) { target.setLocale(locale); return this; }
        public RegisterUserCommand build() { return target; }
    }
}
