package kh.edu.istad.platform.order.domain.exception;

public class CustomerInactiveException extends OrderDomainException {

    public CustomerInactiveException(String message) {
        super(message);
    }

    public CustomerInactiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
