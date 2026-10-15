package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.mapper.OrderDataMapper;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderDataMapper orderDataMapper;

    @Transactional
    public CreateOrderResult execute(CreateOrderCommand command) {
        log.info("Creating order for customer: {} and business: {}",
                command.customerId().value(), command.businessId().value());

        Order order = orderDataMapper.createOrderCommandToOrder(command);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {} and tracking id: {}",
                savedOrder.getId().value(), savedOrder.getTrackingId().value());

        return orderDataMapper.orderToCreateOrderResult(savedOrder, "Order created successfully");
    }
}
