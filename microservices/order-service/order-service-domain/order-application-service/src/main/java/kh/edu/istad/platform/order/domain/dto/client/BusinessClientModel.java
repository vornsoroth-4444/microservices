package kh.edu.istad.platform.order.domain.dto.client;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BusinessClientModel(
        UUID businessId,
        String name,
        String status
) {
    public boolean isActive() {
        return status == null || "ACTIVE".equalsIgnoreCase(status);
    }
}
