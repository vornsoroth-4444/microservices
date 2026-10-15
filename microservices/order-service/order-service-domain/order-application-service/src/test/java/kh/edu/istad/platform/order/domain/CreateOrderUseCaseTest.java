package kh.edu.istad.platform.order.domain;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderItemCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.mapper.OrderDataMapper;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import kh.edu.istad.platform.order.domain.service.OrderDomainServiceImpl;
import kh.edu.istad.platform.order.domain.usecase.CreateOrderUseCase;
import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    private CreateOrderUseCase createOrderUseCase;

    @BeforeEach
    void setUp() {
        OrderDomainService orderDomainService = new OrderDomainServiceImpl();
        OrderDataMapper orderDataMapper = new OrderDataMapper();
        createOrderUseCase = new CreateOrderUseCase(orderDomainService, orderRepository, orderDataMapper);
    }

    @Test
    void testExecuteCreateOrder_Success() {
        UUID customerId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = CreateOrderCommand.builder()
                .customerId(new CustomerId(customerId))
                .businessId(new BusinessId(businessId))
                .price(new BigDecimal("50.00"))
                .deliveryAddress(new StreetAddress(UUID.randomUUID(), "Monivong Blvd", "12000", "Phnom Penh"))
                .items(List.of(
                        CreateOrderItemCommand.builder()
                                .productId(new ProductId(productId))
                                .quantity(2)
                                .price(new BigDecimal("25.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build()
                ))
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderResult result = createOrderUseCase.execute(command);

        assertNotNull(result);
        assertNotNull(result.orderTrackingId());
        assertEquals(OrderStatus.PENDING, result.orderStatus());
        assertEquals("Order created successfully", result.message());

        verify(orderRepository).save(any(Order.class));
    }
}
