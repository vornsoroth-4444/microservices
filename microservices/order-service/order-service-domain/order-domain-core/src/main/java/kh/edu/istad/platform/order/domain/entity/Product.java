package kh.edu.istad.platform.order.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;

public class Product extends AggregateRoot<ProductId> {
    private final String name;
    private final Money price;

    public void validateProduct() {
        if (getId() == null) {
            throw new OrderDomainException("ProductId must not be null!");
        }
        if (name == null || name.isBlank()) {
            throw new OrderDomainException("Product name must not be blank!");
        }
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Product price must be greater than zero!");
        }
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    private Product(Builder builder) {
        setId(builder.productId);
        name = builder.name;
        price = builder.price;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ProductId productId;
        private String name;
        private Money price;

        private Builder() {
        }

        public Builder productId(ProductId val) {
            productId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
