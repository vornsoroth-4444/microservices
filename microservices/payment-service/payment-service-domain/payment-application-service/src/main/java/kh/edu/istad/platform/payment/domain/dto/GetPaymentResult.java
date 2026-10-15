package kh.edu.istad.platform.payment.domain.dto;

import kh.edu.istad.platform.payment.domain.valueobject.PaymentMethod;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record GetPaymentResult(
        UUID paymentId,
        UUID customerId,
        UUID orderId,
        BigDecimal price,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        UUID transactionId
) {
}
