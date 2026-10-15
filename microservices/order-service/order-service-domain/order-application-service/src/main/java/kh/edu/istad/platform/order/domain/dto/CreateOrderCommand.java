package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CreateOrderCommand(
        CustomerId customerId,
        BusinessId businessId,
        BigDecimal price,
        List<CreateOrderItemCommand> items,
        StreetAddress deliveryAddress
) {
}
