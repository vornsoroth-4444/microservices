package kh.edu.istad.platform.order.restapi.dto;

import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderResponse {
    private UUID orderTrackingId;
    private OrderStatus orderStatus;
    private String message;
}
