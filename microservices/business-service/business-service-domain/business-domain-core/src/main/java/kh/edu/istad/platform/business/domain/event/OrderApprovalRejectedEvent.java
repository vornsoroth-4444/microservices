package kh.edu.istad.platform.business.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.business.domain.entity.OrderApproval;

import java.time.ZonedDateTime;

public class OrderApprovalRejectedEvent implements DomainEvent<OrderApproval> {
    private final OrderApproval orderApproval;
    private final ZonedDateTime createdAt;

    public OrderApprovalRejectedEvent(OrderApproval orderApproval, ZonedDateTime createdAt) {
        this.orderApproval = orderApproval;
        this.createdAt = createdAt;
    }

    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
