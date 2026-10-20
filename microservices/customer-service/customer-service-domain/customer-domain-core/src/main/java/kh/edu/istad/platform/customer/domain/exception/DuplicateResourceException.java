package kh.edu.istad.platform.customer.domain.exception;

public class DuplicateResourceException extends CustomerDomainException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
