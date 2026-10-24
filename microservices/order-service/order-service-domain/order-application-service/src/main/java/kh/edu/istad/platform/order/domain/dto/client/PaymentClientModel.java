package kh.edu.istad.platform.order.domain.dto.client;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentClientModel(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        BigDecimal price,
        String status,
        String message
) {
}
