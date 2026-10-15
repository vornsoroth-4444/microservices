package kh.edu.istad.platform.order.domain;

import kh.edu.istad.common.domain.valueobject.*;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import kh.edu.istad.platform.order.domain.service.OrderDomainServiceImpl;
import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderDomainTest {

    private OrderDomainService orderDomainService;
    private CustomerId customerId;
    private BusinessId businessId;
    private ProductId productId;
    private StreetAddress deliveryAddress;

    @BeforeEach
    void setUp() {
        orderDomainService = new OrderDomainServiceImpl();
        customerId = new CustomerId(UUID.randomUUID());
        businessId = new BusinessId(UUID.randomUUID());
        productId = new ProductId(UUID.randomUUID());
        deliveryAddress = new StreetAddress(UUID.randomUUID(), "Main St", "12000", "Phnom Penh");
    }

    @Test
    void testValidateAndInitiateOrder_Success() {
        OrderItem item = OrderItem.builder()
                .productId(productId)
                .quantity(2)
                .price(new Money(new BigDecimal("25.00")))
                .subTotal(new Money(new BigDecimal("50.00")))
                .build();

        Order order = Order.builder()
                .customerId(customerId)
                .businessId(businessId)
                .deliveryAddress(deliveryAddress)
                .price(new Money(new BigDecimal("50.00")))
                .items(List.of(item))
                .build();

        OrderCreatedEvent event = orderDomainService.validateAndInitiateOrder(order);

        assertNotNull(event);
        assertNotNull(order.getId());
        assertNotNull(order.getTrackingId());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertEquals(1, order.getItems().size());
        assertNotNull(order.getItems().get(0).getId());
    }

    @Test
    void testValidateAndInitiateOrder_ThrowsException_WhenPriceMismatch() {
        OrderItem item = OrderItem.builder()
                .productId(productId)
                .quantity(2)
                .price(new Money(new BigDecimal("25.00")))
                .subTotal(new Money(new BigDecimal("50.00")))
                .build();

        Order order = Order.builder()
                .customerId(customerId)
                .businessId(businessId)
                .deliveryAddress(deliveryAddress)
                .price(new Money(new BigDecimal("100.00"))) // Mismatched price
                .items(List.of(item))
                .build();

        assertThrows(OrderDomainException.class, () -> orderDomainService.validateAndInitiateOrder(order));
    }

    @Test
    void testValidateAndInitiateOrder_ThrowsException_WhenEmptyItems() {
        Order order = Order.builder()
                .customerId(customerId)
                .businessId(businessId)
                .deliveryAddress(deliveryAddress)
                .price(new Money(new BigDecimal("50.00")))
                .items(List.of())
                .build();

        assertThrows(OrderDomainException.class, () -> orderDomainService.validateAndInitiateOrder(order));
    }

    @Test
    void testOrderLifecycle_Transitions() {
        OrderItem item = OrderItem.builder()
                .productId(productId)
                .quantity(1)
                .price(new Money(new BigDecimal("30.00")))
                .subTotal(new Money(new BigDecimal("30.00")))
                .build();

        Order order = Order.builder()
                .customerId(customerId)
                .businessId(businessId)
                .deliveryAddress(deliveryAddress)
                .price(new Money(new BigDecimal("30.00")))
                .items(List.of(item))
                .build();

        orderDomainService.validateAndInitiateOrder(order);
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());

        orderDomainService.payOrder(order);
        assertEquals(OrderStatus.PAID, order.getOrderStatus());

        orderDomainService.approveOrder(order);
        assertEquals(OrderStatus.APPROVED, order.getOrderStatus());
    }
}
