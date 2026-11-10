package kh.edu.istad.platform.iam.domain.exception;

public class AccountLockedException extends DomainException {
    public AccountLockedException(String message) {
        super(message);
    }
}
