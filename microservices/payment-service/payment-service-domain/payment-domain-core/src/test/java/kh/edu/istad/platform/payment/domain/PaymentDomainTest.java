package kh.edu.istad.platform.payment.domain;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.entity.CreditEntry;
import kh.edu.istad.platform.payment.domain.entity.CreditHistory;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentInitiatedEvent;
import kh.edu.istad.platform.payment.domain.exception.PaymentDomainException;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainService;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainServiceImpl;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentMethod;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import kh.edu.istad.platform.payment.domain.valueobject.CreditTransactionType;
import kh.edu.istad.platform.payment.domain.valueobject.TransactionId;
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

    @Test
    void testCreditEntry_AddsAndSubtractsBalanceSafely() {
        CreditEntry creditEntry = CreditEntry.builder()
                .customerId(customerId)
                .totalCreditAmount(new Money(new BigDecimal("100.00")))
                .build();
        creditEntry.initializeCreditEntry();

        creditEntry.addCreditAmount(new Money(new BigDecimal("20.00")));
        creditEntry.subtractCreditAmount(new Money(new BigDecimal("35.00")));

        assertEquals(new Money(new BigDecimal("85.00")), creditEntry.getTotalCreditAmount());
        assertThrows(PaymentDomainException.class,
                () -> creditEntry.subtractCreditAmount(new Money(new BigDecimal("90.00"))));
    }

    @Test
    void testCreditHistory_InitializesTransactionRecord() {
        CreditHistory history = CreditHistory.builder()
                .customerId(customerId)
                .transactionId(new TransactionId(UUID.randomUUID()))
                .amount(new Money(new BigDecimal("25.00")))
                .transactionType(CreditTransactionType.CREDIT)
                .build();

        history.initializeTransaction();

        assertNotNull(history.getId());
        assertEquals(customerId, history.getCustomerId());
        assertEquals(CreditTransactionType.CREDIT, history.getTransactionType());
    }
}
