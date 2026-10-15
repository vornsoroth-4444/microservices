package kh.edu.istad.platform.payment.domain.usecase;

import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentResult;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentInitiatedEvent;
import kh.edu.istad.platform.payment.domain.mapper.PaymentDataMapper;
import kh.edu.istad.platform.payment.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPaymentUseCase {

    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;
    private final PaymentDataMapper paymentDataMapper;

    @Transactional
    public ProcessPaymentResult execute(ProcessPaymentCommand command) {
        log.info("Processing payment for order: {} with amount: {}",
                command.orderId().value(), command.price());

        Payment payment = paymentDataMapper.processPaymentCommandToPayment(command);
        PaymentInitiatedEvent event = paymentDomainService.validateAndInitiatePayment(payment);

        // Simulate payment completion
        paymentDomainService.completePayment(payment);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully with id: {} and transaction id: {}",
                savedPayment.getId().value(), savedPayment.getTransactionId().value());

        return paymentDataMapper.paymentToProcessPaymentResult(savedPayment, "Payment processed successfully");
    }
}
