package kh.edu.istad.platform.payment.restapi.dto;

import kh.edu.istad.platform.payment.domain.valueobject.PaymentMethod;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private UUID paymentId;
    private UUID customerId;
    private UUID orderId;
    private BigDecimal price;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private UUID transactionId;
}
