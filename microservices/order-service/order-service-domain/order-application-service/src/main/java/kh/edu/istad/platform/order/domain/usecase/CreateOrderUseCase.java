package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.dto.client.BusinessClientModel;
import kh.edu.istad.platform.order.domain.dto.client.CustomerClientModel;
import kh.edu.istad.platform.order.domain.dto.client.PaymentClientModel;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.mapper.OrderDataMapper;
import kh.edu.istad.platform.order.domain.port.out.BusinessClientPort;
import kh.edu.istad.platform.order.domain.port.out.CustomerClientPort;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.port.out.PaymentClientPort;
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
    private final CustomerClientPort customerClientPort;
    private final BusinessClientPort businessClientPort;
    private final PaymentClientPort paymentClientPort;

    @Transactional
    public CreateOrderResult execute(CreateOrderCommand command) {
        log.info("Initiating Order creation for customer: {} and business: {}",
                command.customerId().value(), command.businessId().value());

        // 1. Inter-service call: Validate customer
        log.info("[ORDER-SERVICE] Step 1: Validating customer {} via Customer Service", command.customerId().value());
        CustomerClientModel customer = customerClientPort.getCustomer(command.customerId().value())
                .orElseThrow(() -> new OrderDomainException("Customer not found with id: " + command.customerId().value()));
        if (!customer.isActive()) {
            throw new OrderDomainException("Customer " + command.customerId().value() + " is inactive!");
        }
        log.info("[ORDER-SERVICE] Customer {} validated successfully (name: {} {})",
                customer.customerId(), customer.givenName(), customer.familyName());

        // 2. Inter-service call: Validate business
        log.info("[ORDER-SERVICE] Step 2: Validating business {} via Business Service", command.businessId().value());
        BusinessClientModel business = businessClientPort.getBusiness(command.businessId().value())
                .orElseThrow(() -> new OrderDomainException("Business not found with id: " + command.businessId().value()));
        log.info("[ORDER-SERVICE] Business {} validated successfully (name: {})", business.businessId(), business.name());

        // 3. Domain Logic & Persistence
        Order order = orderDataMapper.createOrderCommandToOrder(command);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order);

        Order savedOrder = orderRepository.save(order);
        log.info("[ORDER-SERVICE] Order created successfully in database with id: {} and tracking id: {}",
                savedOrder.getId().value(), savedOrder.getTrackingId().value());

        // 4. Inter-service call: Process payment
        log.info("[ORDER-SERVICE] Step 3: Processing payment of ${} for Order {} via Payment Service",
                command.price(), savedOrder.getId().value());
        PaymentClientModel payment = paymentClientPort.processPayment(
                command.customerId().value(),
                savedOrder.getId().value(),
                command.price()
        );
        log.info("[ORDER-SERVICE] Payment processed successfully with payment id: {} (status: {})",
                payment.paymentId(), payment.status());

        return orderDataMapper.orderToCreateOrderResult(
                savedOrder,
                "Order created and paid successfully (Payment ID: " + payment.paymentId() + ")"
        );
    }
}
