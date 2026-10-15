package kh.edu.istad.platform.payment.domain.dto;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProcessPaymentCommand(
        CustomerId customerId,
        OrderId orderId,
        BigDecimal price,
        PaymentMethod paymentMethod
) {
}
