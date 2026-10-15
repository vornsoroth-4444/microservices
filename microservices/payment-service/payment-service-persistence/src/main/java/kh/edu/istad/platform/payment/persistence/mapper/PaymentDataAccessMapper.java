package kh.edu.istad.platform.payment.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.common.domain.valueobject.PaymentId;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.valueobject.TransactionId;
import kh.edu.istad.platform.payment.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
public class PaymentDataAccessMapper {

    private static final String FAILURE_MESSAGE_DELIMITER = ",";

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().value())
                .customerId(payment.getCustomerId().value())
                .orderId(payment.getOrderId().value())
                .price(payment.getPrice().amount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId() != null ? payment.getTransactionId().value() : null)
                .failureMessages(payment.getFailureMessages() != null ?
                        String.join(FAILURE_MESSAGE_DELIMITER, payment.getFailureMessages()) : "")
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .paymentId(new PaymentId(paymentEntity.getId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .price(new Money(paymentEntity.getPrice()))
                .paymentMethod(paymentEntity.getPaymentMethod())
                .status(paymentEntity.getStatus())
                .transactionId(new TransactionId(paymentEntity.getTransactionId()))
                .failureMessages(paymentEntity.getFailureMessages() != null && !paymentEntity.getFailureMessages().isEmpty() ?
                        new ArrayList<>(Arrays.asList(paymentEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))) :
                        new ArrayList<>())
                .build();
    }
}
