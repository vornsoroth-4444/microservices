package kh.edu.istad.platform.order.restapi.mapper;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderItemCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.dto.TrackOrderResult;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import kh.edu.istad.platform.order.restapi.dto.CreateOrderRequest;
import kh.edu.istad.platform.order.restapi.dto.CreateOrderResponse;
import kh.edu.istad.platform.order.restapi.dto.TrackOrderResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderWebMapper {

    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        return CreateOrderCommand.builder()
                .customerId(new CustomerId(request.getCustomerId()))
                .businessId(new BusinessId(request.getBusinessId()))
                .price(request.getPrice())
                .deliveryAddress(new StreetAddress(
                        UUID.randomUUID(),
                        request.getDeliveryAddress().getStreet(),
                        request.getDeliveryAddress().getPostalCode(),
                        request.getDeliveryAddress().getCity()))
                .items(request.getItems().stream()
                        .map(item -> CreateOrderItemCommand.builder()
                                .productId(new ProductId(item.getProductId()))
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .subTotal(item.getSubTotal())
                                .build())
                        .toList())
                .build();
    }

    public CreateOrderResponse toResponse(CreateOrderResult result) {
        return CreateOrderResponse.builder()
                .orderTrackingId(result.orderTrackingId())
                .orderStatus(result.orderStatus())
                .message(result.message())
                .build();
    }

    public TrackOrderResponse toTrackResponse(TrackOrderResult result) {
        return TrackOrderResponse.builder()
                .orderTrackingId(result.orderTrackingId())
                .orderStatus(result.orderStatus())
                .failureMessages(result.failureMessages())
                .build();
    }
}
