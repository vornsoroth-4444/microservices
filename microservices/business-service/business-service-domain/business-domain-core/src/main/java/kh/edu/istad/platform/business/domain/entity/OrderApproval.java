package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalId;
import kh.edu.istad.platform.business.domain.valueobject.OrderApprovalStatus;

import java.util.UUID;

public class OrderApproval extends AggregateRoot<OrderApprovalId> {
    private final BusinessId businessId;
    private final OrderId orderId;
    private OrderApprovalStatus status;

    public void initializeApproval() {
        if (getId() != null || status != null) {
            throw new BusinessDomainException("Order approval must be uninitialized before creation!");
        }
        validate();
        setId(new OrderApprovalId(UUID.randomUUID()));
        status = OrderApprovalStatus.PENDING;
    }

    private void validate() {
        if (businessId == null || orderId == null) {
            throw new BusinessDomainException("BusinessId and OrderId must not be null!");
        }
    }

    public void approve() {
        transitionTo(OrderApprovalStatus.APPROVED);
    }

    public void reject() {
        transitionTo(OrderApprovalStatus.REJECTED);
    }

    private void transitionTo(OrderApprovalStatus nextStatus) {
        if (status != OrderApprovalStatus.PENDING) {
            throw new BusinessDomainException("Only pending order approvals can be decided!");
        }
        status = nextStatus;
    }

    public BusinessId getBusinessId() {
        return businessId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderApprovalStatus getStatus() {
        return status;
    }

    private OrderApproval(Builder builder) {
        setId(builder.orderApprovalId);
        businessId = builder.businessId;
        orderId = builder.orderId;
        status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderApprovalId orderApprovalId;
        private BusinessId businessId;
        private OrderId orderId;
        private OrderApprovalStatus status;

        private Builder() {
        }

        public Builder orderApprovalId(OrderApprovalId val) {
            orderApprovalId = val;
            return this;
        }

        public Builder businessId(BusinessId val) {
            businessId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder status(OrderApprovalStatus val) {
            status = val;
            return this;
        }

        public OrderApproval build() {
            return new OrderApproval(this);
        }
    }
}
