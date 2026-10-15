package kh.edu.istad.platform.payment.domain.mapper;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.payment.domain.dto.GetPaymentResult;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentResult;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataMapper {

    public Payment processPaymentCommandToPayment(ProcessPaymentCommand command) {
        return Payment.builder()
                .customerId(command.customerId())
                .orderId(command.orderId())
                .price(new Money(command.price()))
                .paymentMethod(command.paymentMethod())
                .build();
    }

    public ProcessPaymentResult paymentToProcessPaymentResult(Payment payment, String message) {
        return ProcessPaymentResult.builder()
                .paymentId(payment.getId().value())
                .transactionId(payment.getTransactionId() != null ? payment.getTransactionId().value() : null)
                .status(payment.getStatus())
                .message(message)
                .build();
    }

    public GetPaymentResult paymentToGetPaymentResult(Payment payment) {
        return GetPaymentResult.builder()
                .paymentId(payment.getId().value())
                .customerId(payment.getCustomerId().value())
                .orderId(payment.getOrderId().value())
                .price(payment.getPrice().amount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId() != null ? payment.getTransactionId().value() : null)
                .build();
    }
}
