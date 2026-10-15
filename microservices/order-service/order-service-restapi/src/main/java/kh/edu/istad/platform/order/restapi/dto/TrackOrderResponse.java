package kh.edu.istad.platform.order.restapi.dto;

import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackOrderResponse {
    private UUID orderTrackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;
}
