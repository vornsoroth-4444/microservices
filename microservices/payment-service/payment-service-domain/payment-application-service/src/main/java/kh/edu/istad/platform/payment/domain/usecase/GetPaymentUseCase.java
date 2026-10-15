package kh.edu.istad.platform.payment.domain.usecase;

import kh.edu.istad.common.domain.valueobject.PaymentId;
import kh.edu.istad.platform.payment.domain.dto.GetPaymentQuery;
import kh.edu.istad.platform.payment.domain.dto.GetPaymentResult;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.exception.PaymentNotFoundException;
import kh.edu.istad.platform.payment.domain.mapper.PaymentDataMapper;
import kh.edu.istad.platform.payment.domain.port.out.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentDataMapper paymentDataMapper;

    @Transactional(readOnly = true)
    public GetPaymentResult execute(GetPaymentQuery query) {
        log.info("Fetching payment with id: {}", query.paymentId());

        Payment payment = paymentRepository.findById(new PaymentId(query.paymentId()))
                .orElseThrow(() -> {
                    log.warn("Payment not found with id: {}", query.paymentId());
                    return new PaymentNotFoundException("Payment not found with id: " + query.paymentId());
                });

        return paymentDataMapper.paymentToGetPaymentResult(payment);
    }
}
