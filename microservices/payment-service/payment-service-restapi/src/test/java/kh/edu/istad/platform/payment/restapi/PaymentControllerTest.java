package kh.edu.istad.platform.payment.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.istad.platform.payment.domain.dto.GetPaymentResult;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentResult;
import kh.edu.istad.platform.payment.domain.usecase.GetPaymentUseCase;
import kh.edu.istad.platform.payment.domain.usecase.ProcessPaymentUseCase;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentMethod;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import kh.edu.istad.platform.payment.restapi.controller.PaymentController;
import kh.edu.istad.platform.payment.restapi.dto.ProcessPaymentRequest;
import kh.edu.istad.platform.payment.restapi.handler.PaymentGlobalExceptionHandler;
import kh.edu.istad.platform.payment.restapi.mapper.PaymentWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProcessPaymentUseCase processPaymentUseCase;

    @Mock
    private GetPaymentUseCase getPaymentUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PaymentWebMapper paymentWebMapper = new PaymentWebMapper();
        PaymentController paymentController = new PaymentController(processPaymentUseCase, getPaymentUseCase, paymentWebMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new PaymentGlobalExceptionHandler())
                .build();
    }

    @Test
    void testProcessPayment_Success() throws Exception {
        UUID paymentId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        when(processPaymentUseCase.execute(any(ProcessPaymentCommand.class)))
                .thenReturn(ProcessPaymentResult.builder()
                        .paymentId(paymentId)
                        .transactionId(transactionId)
                        .status(PaymentStatus.COMPLETED)
                        .message("Payment processed successfully")
                        .build());

        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .customerId(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .price(new BigDecimal("99.99"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value(paymentId.toString()))
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.message").value("Payment processed successfully"));
    }

    @Test
    void testProcessPayment_ValidationFailure_WhenPriceNull() throws Exception {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .customerId(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .price(null) // invalid
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPayment_Success() throws Exception {
        UUID paymentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        when(getPaymentUseCase.execute(any()))
                .thenReturn(GetPaymentResult.builder()
                        .paymentId(paymentId)
                        .customerId(customerId)
                        .orderId(orderId)
                        .price(new BigDecimal("99.99"))
                        .paymentMethod(PaymentMethod.BANK_TRANSFER)
                        .status(PaymentStatus.COMPLETED)
                        .transactionId(transactionId)
                        .build());

        mockMvc.perform(get("/api/payments/" + paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId.toString()))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
