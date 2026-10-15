package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.common.domain.valueobject.ProductId;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateOrderItemCommand(
        ProductId productId,
        int quantity,
        BigDecimal price,
        BigDecimal subTotal
) {
}
