package kh.edu.istad.platform.order.domain.dto.client;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerClientModel(
        UUID customerId,
        String username,
        String givenName,
        String familyName,
        String status
) {
    public boolean isActive() {
        return status == null || "ACTIVE".equalsIgnoreCase(status);
    }
}
