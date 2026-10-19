package kh.edu.istad.platform.customer.restapi.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;

public record CreateCustomerRequest(
        String username,
        String familyName,
        String givenName,
        String email,
        String phoneNumber,
        String status,
        String segmentId
) {
}
