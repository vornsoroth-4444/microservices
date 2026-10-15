package kh.edu.istad.platform.order.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.usecase.CreateOrderUseCase;
import kh.edu.istad.platform.order.domain.usecase.TrackOrderUseCase;
import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import kh.edu.istad.platform.order.restapi.controller.OrderController;
import kh.edu.istad.platform.order.restapi.dto.CreateOrderRequest;
import kh.edu.istad.platform.order.restapi.dto.OrderItemRequest;
import kh.edu.istad.platform.order.restapi.dto.StreetAddressRequest;
import kh.edu.istad.platform.order.restapi.handler.OrderGlobalExceptionHandler;
import kh.edu.istad.platform.order.restapi.mapper.OrderWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private TrackOrderUseCase trackOrderUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        OrderWebMapper orderWebMapper = new OrderWebMapper();
        OrderController orderController = new OrderController(createOrderUseCase, trackOrderUseCase, orderWebMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new OrderGlobalExceptionHandler())
                .build();
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        UUID trackingId = UUID.randomUUID();
        when(createOrderUseCase.execute(any(CreateOrderCommand.class)))
                .thenReturn(CreateOrderResult.builder()
                        .orderTrackingId(trackingId)
                        .orderStatus(OrderStatus.PENDING)
                        .message("Order created successfully")
                        .build());

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(UUID.randomUUID())
                .businessId(UUID.randomUUID())
                .price(new BigDecimal("45.00"))
                .deliveryAddress(StreetAddressRequest.builder()
                        .street("St 271")
                        .postalCode("12300")
                        .city("Phnom Penh")
                        .build())
                .items(List.of(
                        OrderItemRequest.builder()
                                .productId(UUID.randomUUID())
                                .quantity(3)
                                .price(new BigDecimal("15.00"))
                                .subTotal(new BigDecimal("45.00"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderTrackingId").value(trackingId.toString()))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Order created successfully"));
    }

    @Test
    void testCreateOrder_ValidationFailure_WhenCustomerIdNull() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(null) // invalid
                .businessId(UUID.randomUUID())
                .price(new BigDecimal("45.00"))
                .deliveryAddress(StreetAddressRequest.builder()
                        .street("St 271")
                        .postalCode("12300")
                        .city("Phnom Penh")
                        .build())
                .items(List.of(
                        OrderItemRequest.builder()
                                .productId(UUID.randomUUID())
                                .quantity(1)
                                .price(new BigDecimal("45.00"))
                                .subTotal(new BigDecimal("45.00"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
