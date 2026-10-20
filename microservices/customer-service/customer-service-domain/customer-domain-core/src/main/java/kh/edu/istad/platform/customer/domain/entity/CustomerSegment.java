package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;

public class CustomerSegment {
    private String id;
    private String name;
    private CustomerSegmentType type;
    private String description;

    public CustomerSegment() {
    }

    public CustomerSegment(String id, String name, CustomerSegmentType type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new CustomerDomainException("Customer segment name must not be blank");
        }
        if (type == null) {
            throw new CustomerDomainException("Customer segment type must not be null");
        }
    }

    public void update(String name, CustomerSegmentType type, String description) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        if (type != null) {
            this.type = type;
        }
        if (description != null) {
            this.description = description;
        }
        validate();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomerSegmentType getType() {
        return type;
    }

    public void setType(CustomerSegmentType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String name;
        private CustomerSegmentType type;
        private String description;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(CustomerSegmentType type) {
            this.type = type;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public CustomerSegment build() {
            return new CustomerSegment(id, name, type, description);
        }
    }
}
