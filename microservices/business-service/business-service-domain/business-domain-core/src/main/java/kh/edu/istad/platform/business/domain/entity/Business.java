package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;

import java.util.UUID;

public class Business extends AggregateRoot<BusinessId> {
    private String name;
    private BusinessAddress address;
    private BusinessContact contact;
    private BusinessStatus status;

    public void initiateBusiness() {
        validateInitiateBusiness();
        super.setId(new BusinessId(UUID.randomUUID()));
        this.status = BusinessStatus.ACTIVE;
    }

    private void validateInitiateBusiness() {
        if (super.getId() != null) {
            throw new BusinessDomainException("Business id must be null before initiation!");
        }
        if (status != null) {
            throw new BusinessDomainException("Business status must be null before initiation!");
        }
        validateBusiness();
    }

    public void validateBusiness() {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessDomainException("Business name must not be blank!");
        }
        if (address == null) {
            throw new BusinessDomainException("Business address must not be null!");
        }
        if (contact == null) {
            throw new BusinessDomainException("Business contact must not be null!");
        }
    }

    public void updateDetails(String name, BusinessAddress address, BusinessContact contact) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessDomainException("Business name must not be blank!");
        }
        this.name = name;
        if (address != null) this.address = address;
        if (contact != null) this.contact = contact;
    }

    public void deactivate() {
        if (status != BusinessStatus.ACTIVE) {
            throw new BusinessDomainException("Only active business can be deactivated! Current status: " + status);
        }
        this.status = BusinessStatus.INACTIVE;
    }

    public void activate() {
        if (status != BusinessStatus.INACTIVE && status != BusinessStatus.PENDING) {
            throw new BusinessDomainException("Business cannot be activated from status: " + status);
        }
        this.status = BusinessStatus.ACTIVE;
    }

    public String getName() {
        return name;
    }

    public BusinessAddress getAddress() {
        return address;
    }

    public BusinessContact getContact() {
        return contact;
    }

    public BusinessStatus getStatus() {
        return status;
    }

    private Business(Builder builder) {
        super.setId(builder.businessId);
        name = builder.name;
        address = builder.address;
        contact = builder.contact;
        status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BusinessId businessId;
        private String name;
        private BusinessAddress address;
        private BusinessContact contact;
        private BusinessStatus status;

        private Builder() {
        }

        public Builder businessId(BusinessId val) {
            businessId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder address(BusinessAddress val) {
            address = val;
            return this;
        }

        public Builder contact(BusinessContact val) {
            contact = val;
            return this;
        }

        public Builder status(BusinessStatus val) {
            status = val;
            return this;
        }

        public Business build() {
            return new Business(this);
        }
    }
}
