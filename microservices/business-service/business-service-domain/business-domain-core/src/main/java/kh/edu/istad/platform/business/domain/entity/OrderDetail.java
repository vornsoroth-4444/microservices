package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.valueobject.BusinessOrderStatus;

import java.util.List;

public class OrderDetail extends BaseEntity<OrderId> {
    private final BusinessOrderStatus status;
    private final Money price;
    private final List<Product> products;

    public void validate() {
        if (getId() == null) {
            throw new BusinessDomainException("OrderId must not be null!");
        }
        if (status == null) {
            throw new BusinessDomainException("Order status must not be null!");
        }
        if (price == null || !price.isGreaterThanZero()) {
            throw new BusinessDomainException("Order price must be greater than zero!");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessDomainException("Order must contain at least one product!");
        }
    }

    public BusinessOrderStatus getStatus() {
        return status;
    }

    public Money getPrice() {
        return price;
    }

    public List<Product> getProducts() {
        return products;
    }

    private OrderDetail(Builder builder) {
        setId(builder.orderId);
        status = builder.status;
        price = builder.price;
        products = builder.products == null ? null : List.copyOf(builder.products);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderId orderId;
        private BusinessOrderStatus status;
        private Money price;
        private List<Product> products;

        private Builder() {
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder status(BusinessOrderStatus val) {
            status = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder products(List<Product> val) {
            products = val;
            return this;
        }

        public OrderDetail build() {
            return new OrderDetail(this);
        }
    }
}
