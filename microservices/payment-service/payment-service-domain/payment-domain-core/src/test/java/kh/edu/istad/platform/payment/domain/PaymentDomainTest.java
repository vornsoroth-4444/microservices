package kh.edu.istad.platform.payment.domain;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentInitiatedEvent;
import kh.edu.istad.platform.payment.domain.exception.PaymentDomainException;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainService;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainServiceImpl;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentMethod;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDomainTest {

    private PaymentDomainService paymentDomainService;
    private CustomerId customerId;
    private OrderId orderId;

    @BeforeEach
    void setUp() {
        paymentDomainService = new PaymentDomainServiceImpl();
        customerId = new CustomerId(UUID.randomUUID());
        orderId = new OrderId(UUID.randomUUID());
    }

    @Test
    void testValidateAndInitiatePayment_Success() {
        Payment payment = Payment.builder()
                .customerId(customerId)
                .orderId(orderId)
                .price(new Money(new BigDecimal("75.00")))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        PaymentInitiatedEvent event = paymentDomainService.validateAndInitiatePayment(payment);

        assertNotNull(event);
        assertNotNull(payment.getId());
        assertNotNull(payment.getTransactionId());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
    }

    @Test
    void testValidateAndInitiatePayment_ThrowsException_WhenPriceZero() {
        Payment payment = Payment.builder()
                .customerId(customerId)
                .orderId(orderId)
                .price(new Money(BigDecimal.ZERO))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        assertThrows(PaymentDomainException.class, () -> paymentDomainService.validateAndInitiatePayment(payment));
    }

    @Test
    void testPaymentLifecycle_CompleteAndFail() {
        Payment payment = Payment.builder()
                .customerId(customerId)
                .orderId(orderId)
                .price(new Money(new BigDecimal("75.00")))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();

        paymentDomainService.validateAndInitiatePayment(payment);
        assertEquals(PaymentStatus.PENDING, payment.getStatus());

        paymentDomainService.completePayment(payment);
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());

        // Completing again should throw exception
        assertThrows(PaymentDomainException.class, () -> paymentDomainService.completePayment(payment));
    }
}
