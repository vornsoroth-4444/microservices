package kh.edu.istad.platform.order.domain.dto;

public record CustomerResponse(
        String customerId,
        String username,
        String familyName,
        String givenName,
        String status
) {
}
