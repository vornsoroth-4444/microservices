package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.platform.order.domain.dto.client.PaymentClientModel;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentClientPort {
    PaymentClientModel processPayment(UUID customerId, UUID orderId, BigDecimal amount);
}
