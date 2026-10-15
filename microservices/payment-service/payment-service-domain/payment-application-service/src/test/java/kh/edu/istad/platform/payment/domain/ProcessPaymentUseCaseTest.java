package kh.edu.istad.platform.payment.domain;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentResult;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.mapper.PaymentDataMapper;
import kh.edu.istad.platform.payment.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainService;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainServiceImpl;
import kh.edu.istad.platform.payment.domain.usecase.ProcessPaymentUseCase;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentMethod;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentUseCaseTest {

    @Mock
    private PaymentRepository paymentRepository;

    private ProcessPaymentUseCase processPaymentUseCase;

    @BeforeEach
    void setUp() {
        PaymentDomainService paymentDomainService = new PaymentDomainServiceImpl();
        PaymentDataMapper paymentDataMapper = new PaymentDataMapper();
        processPaymentUseCase = new ProcessPaymentUseCase(paymentDomainService, paymentRepository, paymentDataMapper);
    }

    @Test
    void testExecuteProcessPayment_Success() {
        ProcessPaymentCommand command = ProcessPaymentCommand.builder()
                .customerId(new CustomerId(UUID.randomUUID()))
                .orderId(new OrderId(UUID.randomUUID()))
                .price(new BigDecimal("120.00"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProcessPaymentResult result = processPaymentUseCase.execute(command);

        assertNotNull(result);
        assertNotNull(result.paymentId());
        assertNotNull(result.transactionId());
        assertEquals(PaymentStatus.COMPLETED, result.status());
        assertEquals("Payment processed successfully", result.message());

        verify(paymentRepository).save(any(Payment.class));
    }
}
