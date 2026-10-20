package kh.edu.istad.platform.customer.restapi.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record PatchCustomerRequest(
        String familyName,
        String givenName,
        @Email(message = "Invalid email format")
        String email,
        String phoneNumber,
        String segmentId
) {
}
