package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record TrackOrderResult(
        UUID orderTrackingId,
        OrderStatus orderStatus,
        List<String> failureMessages
) {
}
