package kh.edu.istad.platform.payment.restapi.dto;

import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPaymentResponse {
    private UUID paymentId;
    private UUID transactionId;
    private PaymentStatus status;
    private String message;
}
