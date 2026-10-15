package kh.edu.istad.platform.order.domain.mapper;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderItemCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.dto.TrackOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDataMapper {

    public Order createOrderCommandToOrder(CreateOrderCommand command) {
        return Order.builder()
                .customerId(command.customerId())
                .businessId(command.businessId())
                .deliveryAddress(command.deliveryAddress())
                .price(new Money(command.price()))
                .items(orderItemsToOrderItemEntities(command.items()))
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntities(List<CreateOrderItemCommand> orderItemCommands) {
        return orderItemCommands.stream()
                .map(orderItemCommand -> OrderItem.builder()
                        .productId(orderItemCommand.productId())
                        .price(new Money(orderItemCommand.price()))
                        .quantity(orderItemCommand.quantity())
                        .subTotal(new Money(orderItemCommand.subTotal()))
                        .build())
                .toList();
    }

    public CreateOrderResult orderToCreateOrderResult(Order order, String message) {
        return CreateOrderResult.builder()
                .orderTrackingId(order.getTrackingId().value())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResult orderToTrackOrderResult(Order order) {
        return TrackOrderResult.builder()
                .orderTrackingId(order.getTrackingId().value())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }
}
