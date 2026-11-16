package kh.edu.istad.platform.iam.application.port.out;

public interface PasswordPolicy {
    void validate(String password);
}
