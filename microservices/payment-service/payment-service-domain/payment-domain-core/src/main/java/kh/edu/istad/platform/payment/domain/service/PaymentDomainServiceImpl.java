package kh.edu.istad.platform.payment.domain.service;

import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentFailedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentInitiatedEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class PaymentDomainServiceImpl implements PaymentDomainService {

    private static final String UTC = "UTC";

    @Override
    public PaymentInitiatedEvent validateAndInitiatePayment(Payment payment) {
        payment.initializePayment();
        return new PaymentInitiatedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public PaymentCompletedEvent completePayment(Payment payment) {
        payment.complete();
        return new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public PaymentFailedEvent failPayment(Payment payment, List<String> failureMessages) {
        payment.fail(failureMessages);
        return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)));
    }
}
