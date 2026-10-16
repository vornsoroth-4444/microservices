package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;

import java.util.UUID;

public class Product extends AggregateRoot<ProductId> {
    private final BusinessId businessId;
    private String name;
    private Money price;
    private boolean available;

    public void initializeProduct() {
        if (getId() != null) {
            throw new BusinessDomainException("Product id must be null before initialization!");
        }
        validateProduct();
        setId(new ProductId(UUID.randomUUID()));
        available = true;
    }

    public void validateProduct() {
        if (businessId == null) {
            throw new BusinessDomainException("BusinessId must not be null!");
        }
        if (name == null || name.isBlank()) {
            throw new BusinessDomainException("Product name must not be blank!");
        }
        if (price == null || !price.isGreaterThanZero()) {
            throw new BusinessDomainException("Product price must be greater than zero!");
        }
    }

    public void update(String name, Money price) {
        if (name == null || name.isBlank()) {
            throw new BusinessDomainException("Product name must not be blank!");
        }
        if (price == null || !price.isGreaterThanZero()) {
            throw new BusinessDomainException("Product price must be greater than zero!");
        }
        this.name = name;
        this.price = price;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public BusinessId getBusinessId() {
        return businessId;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    private Product(Builder builder) {
        setId(builder.productId);
        businessId = builder.businessId;
        name = builder.name;
        price = builder.price;
        available = builder.available;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ProductId productId;
        private BusinessId businessId;
        private String name;
        private Money price;
        private boolean available = true;

        private Builder() {
        }

        public Builder productId(ProductId val) {
            productId = val;
            return this;
        }

        public Builder businessId(BusinessId val) {
            businessId = val;
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

        public Builder available(boolean val) {
            available = val;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
