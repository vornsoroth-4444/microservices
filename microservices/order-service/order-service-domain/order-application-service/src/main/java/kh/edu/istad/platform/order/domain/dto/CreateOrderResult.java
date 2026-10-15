package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateOrderResult(
        UUID orderTrackingId,
        OrderStatus orderStatus,
        String message
) {
}
