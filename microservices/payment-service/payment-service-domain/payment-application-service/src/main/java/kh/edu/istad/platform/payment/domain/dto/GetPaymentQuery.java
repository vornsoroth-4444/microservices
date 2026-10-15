package kh.edu.istad.platform.payment.domain.dto;

import java.util.UUID;

public record GetPaymentQuery(
        UUID paymentId
) {
}
