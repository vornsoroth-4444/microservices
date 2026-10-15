package kh.edu.istad.platform.business.domain.exception;

import kh.edu.istad.common.domain.exception.DomainException;

public class BusinessNotFoundException extends DomainException {
    public BusinessNotFoundException(String message) {
        super(message);
    }

    public BusinessNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
