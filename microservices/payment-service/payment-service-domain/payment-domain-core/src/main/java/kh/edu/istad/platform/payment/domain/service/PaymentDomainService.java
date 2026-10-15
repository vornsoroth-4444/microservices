package kh.edu.istad.platform.payment.domain.service;

import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentFailedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentInitiatedEvent;

import java.util.List;

public interface PaymentDomainService {
    PaymentInitiatedEvent validateAndInitiatePayment(Payment payment);

    PaymentCompletedEvent completePayment(Payment payment);

    PaymentFailedEvent failPayment(Payment payment, List<String> failureMessages);
}
