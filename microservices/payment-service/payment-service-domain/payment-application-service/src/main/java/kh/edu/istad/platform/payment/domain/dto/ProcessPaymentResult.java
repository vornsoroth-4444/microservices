package kh.edu.istad.platform.payment.domain.dto;

import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ProcessPaymentResult(
        UUID paymentId,
        UUID transactionId,
        PaymentStatus status,
        String message
) {
}
