package kh.edu.istad.platform.customer.domain.exception;

public class CustomerSegmentNotFoundException extends CustomerDomainException {
    public CustomerSegmentNotFoundException(String message) {
        super(message);
    }

    public CustomerSegmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
