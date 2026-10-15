package kh.edu.istad.platform.order.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.*;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.valueobject.OrderItemId;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;
import kh.edu.istad.platform.order.persistence.entity.OrderEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntity;
import kh.edu.istad.platform.order.persistence.entity.StreetAddressEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataAccessMapper {

    private static final String FAILURE_MESSAGE_DELIMITER = ",";

    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().value())
                .customerId(order.getCustomerId().value())
                .businessId(order.getBusinessId().value())
                .trackingId(order.getTrackingId().value())
                .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
                .price(order.getPrice().amount())
                .items(new ArrayList<>())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages() != null ?
                        String.join(FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()) : "")
                .build();

        if (order.getItems() != null) {
            List<OrderItemEntity> orderItemEntities = order.getItems().stream()
                    .map(orderItem -> OrderItemEntity.builder()
                            .id(null) // ID is null for database IDENTITY generation
                            .order(orderEntity)
                            .productId(orderItem.getProductId().value())
                            .price(orderItem.getPrice().amount())
                            .quantity(orderItem.getQuantity())
                            .subTotal(orderItem.getSubTotal().amount())
                            .build())
                    .collect(Collectors.toCollection(ArrayList::new));
            orderEntity.setItems(orderItemEntities);
        }
        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .businessId(new BusinessId(orderEntity.getBusinessId()))
                .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
                .price(new Money(orderEntity.getPrice()))
                .items(orderItemEntitiesToOrderItems(orderEntity.getItems()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessages(orderEntity.getFailureMessages() != null && !orderEntity.getFailureMessages().isEmpty() ?
                        new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))) :
                        new ArrayList<>())
                .build();
    }

    private StreetAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress) {
        if (deliveryAddress == null) {
            return null;
        }
        return StreetAddressEntity.builder()
                .street(deliveryAddress.getStreet())
                .postalCode(deliveryAddress.getPostalCode())
                .city(deliveryAddress.getCity())
                .build();
    }

    private StreetAddress addressEntityToDeliveryAddress(StreetAddressEntity addressEntity) {
        if (addressEntity == null) {
            return null;
        }
        return new StreetAddress(
                UUID.randomUUID(),
                addressEntity.getStreet(),
                addressEntity.getPostalCode(),
                addressEntity.getCity()
        );
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(orderItemEntity -> OrderItem.builder()
                        .orderItemId(new OrderItemId(orderItemEntity.getId()))
                        .productId(new ProductId(orderItemEntity.getProductId()))
                        .price(new Money(orderItemEntity.getPrice()))
                        .quantity(orderItemEntity.getQuantity())
                        .subTotal(new Money(orderItemEntity.getSubTotal()))
                        .build())
                .toList();
    }
}
