package kh.edu.istad.platform.payment.restapi.mapper;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.dto.GetPaymentResult;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentResult;
import kh.edu.istad.platform.payment.restapi.dto.PaymentResponse;
import kh.edu.istad.platform.payment.restapi.dto.ProcessPaymentRequest;
import kh.edu.istad.platform.payment.restapi.dto.ProcessPaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentWebMapper {

    public ProcessPaymentCommand toCommand(ProcessPaymentRequest request) {
        return ProcessPaymentCommand.builder()
                .customerId(new CustomerId(request.getCustomerId()))
                .orderId(new OrderId(request.getOrderId()))
                .price(request.getPrice())
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    public ProcessPaymentResponse toResponse(ProcessPaymentResult result) {
        return ProcessPaymentResponse.builder()
                .paymentId(result.paymentId())
                .transactionId(result.transactionId())
                .status(result.status())
                .message(result.message())
                .build();
    }

    public PaymentResponse toPaymentResponse(GetPaymentResult result) {
        return PaymentResponse.builder()
                .paymentId(result.paymentId())
                .customerId(result.customerId())
                .orderId(result.orderId())
                .price(result.price())
                .paymentMethod(result.paymentMethod())
                .status(result.status())
                .transactionId(result.transactionId())
                .build();
    }
}
