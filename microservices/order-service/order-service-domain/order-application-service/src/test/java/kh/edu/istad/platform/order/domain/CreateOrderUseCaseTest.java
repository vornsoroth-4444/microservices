package kh.edu.istad.platform.order.domain;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderItemCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.dto.client.BusinessClientModel;
import kh.edu.istad.platform.order.domain.dto.client.CustomerClientModel;
import kh.edu.istad.platform.order.domain.dto.client.PaymentClientModel;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.exception.CustomerInactiveException;
import kh.edu.istad.platform.order.domain.mapper.OrderDataMapper;
import kh.edu.istad.platform.order.domain.port.out.BusinessClientPort;
import kh.edu.istad.platform.order.domain.port.out.CustomerClientPort;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.port.out.PaymentClientPort;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerClientPort customerClientPort;

    @Mock
    private BusinessClientPort businessClientPort;

    @Mock
    private PaymentClientPort paymentClientPort;

    private CreateOrderUseCase createOrderUseCase;

    @BeforeEach
    void setUp() {
        OrderDomainService orderDomainService = new OrderDomainServiceImpl();
        OrderDataMapper orderDataMapper = new OrderDataMapper();
        createOrderUseCase = new CreateOrderUseCase(
                orderDomainService,
                orderRepository,
                orderDataMapper,
                customerClientPort,
                businessClientPort,
                paymentClientPort
        );
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

        when(customerClientPort.getCustomer(customerId)).thenReturn(Optional.of(
                CustomerClientModel.builder()
                        .customerId(customerId)
                        .username("johndoe")
                        .givenName("John")
                        .familyName("Doe")
                        .status("ACTIVE")
                        .build()
        ));
        when(businessClientPort.getBusiness(businessId)).thenReturn(Optional.of(
                BusinessClientModel.builder()
                        .businessId(businessId)
                        .name("Test Business")
                        .build()
        ));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentClientPort.processPayment(any(), any(), any())).thenReturn(
                PaymentClientModel.builder()
                        .paymentId(UUID.randomUUID())
                        .status("COMPLETED")
                        .build()
        );

        CreateOrderResult result = createOrderUseCase.execute(command);

        assertNotNull(result);
        assertNotNull(result.orderTrackingId());
        assertEquals(OrderStatus.PENDING, result.orderStatus());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testExecuteCreateOrder_CustomerInactive_ThrowsCustomerInactiveException() {
        UUID customerId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();

        CreateOrderCommand command = CreateOrderCommand.builder()
                .customerId(new CustomerId(customerId))
                .businessId(new BusinessId(businessId))
                .price(new BigDecimal("50.00"))
                .deliveryAddress(new StreetAddress(UUID.randomUUID(), "Monivong Blvd", "12000", "Phnom Penh"))
                .items(List.of(
                        CreateOrderItemCommand.builder()
                                .productId(new ProductId(UUID.randomUUID()))
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build()
                ))
                .build();

        when(customerClientPort.getCustomer(customerId)).thenReturn(Optional.of(
                CustomerClientModel.builder()
                        .customerId(customerId)
                        .username("inactiveuser")
                        .givenName("Inactive")
                        .familyName("User")
                        .status("INACTIVE")
                        .build()
        ));

        CustomerInactiveException ex = assertThrows(CustomerInactiveException.class, () ->
                createOrderUseCase.execute(command)
        );

        assertEquals("Customer " + customerId + " is inactive!", ex.getMessage());
    }
}
