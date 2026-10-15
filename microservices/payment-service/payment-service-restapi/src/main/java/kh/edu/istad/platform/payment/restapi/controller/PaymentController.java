package kh.edu.istad.platform.payment.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.payment.domain.dto.GetPaymentQuery;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentResult;
import kh.edu.istad.platform.payment.domain.usecase.GetPaymentUseCase;
import kh.edu.istad.platform.payment.domain.usecase.ProcessPaymentUseCase;
import kh.edu.istad.platform.payment.restapi.dto.PaymentResponse;
import kh.edu.istad.platform.payment.restapi.dto.ProcessPaymentRequest;
import kh.edu.istad.platform.payment.restapi.dto.ProcessPaymentResponse;
import kh.edu.istad.platform.payment.restapi.mapper.PaymentWebMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;
    private final PaymentWebMapper paymentWebMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ProcessPaymentResponse processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        log.info("Received request to process payment for order: {}", request.getOrderId());
        ProcessPaymentResult result = processPaymentUseCase.execute(paymentWebMapper.toCommand(request));
        return paymentWebMapper.toResponse(result);
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable("paymentId") UUID paymentId) {
        log.info("Received request to get payment with id: {}", paymentId);
        return paymentWebMapper.toPaymentResponse(getPaymentUseCase.execute(new GetPaymentQuery(paymentId)));
    }
}
