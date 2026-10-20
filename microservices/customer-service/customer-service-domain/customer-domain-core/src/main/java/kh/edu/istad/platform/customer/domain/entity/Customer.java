package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;

import java.util.UUID;

public class Customer extends AggregateRoot<CustomerId> {
    private final String username;
    private String familyName;
    private String givenName;
    private Email email;
    private PhoneNumber phoneNumber;
    private CustomerStatus status;
    private CustomerSegment segment;

    // Domain Critical Logic
    public void initiateCustomer() {
        validateCustomerForInitiation();
        super.setId(new CustomerId(UUID.randomUUID()));
        status = CustomerStatus.ACTIVE;
    }

    private void validateCustomerForInitiation() {
        if (super.getId() != null) {
            throw new CustomerDomainException("Customer id must be null prior to initiation");
        }
        if (status != null) {
            throw new CustomerDomainException("Customer status must be null prior to initiation");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new CustomerDomainException("Customer username must not be blank");
        }
        if (familyName == null || familyName.trim().isEmpty()) {
            throw new CustomerDomainException("Customer familyName must not be blank");
        }
        if (givenName == null || givenName.trim().isEmpty()) {
            throw new CustomerDomainException("Customer givenName must not be blank");
        }
    }

    public void updateCustomer(String familyName, String givenName) {
        if (familyName == null || familyName.trim().isEmpty() || givenName == null || givenName.trim().isEmpty()) {
            throw new CustomerDomainException("familyName and givenName must not be null or blank");
        }
        this.familyName = familyName.trim();
        this.givenName = givenName.trim();
    }

    public void updateDetails(String familyName, String givenName, Email email, PhoneNumber phoneNumber, CustomerSegment segment) {
        updateCustomer(familyName, givenName);
        if (email != null) {
            this.email = email;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if (segment != null) {
            this.segment = segment;
        }
    }

    public void assignSegment(CustomerSegment segment) {
        this.segment = segment;
    }

    public void deactivateCustomer() {
        if (status != CustomerStatus.ACTIVE) {
            throw new CustomerDomainException("Could not deactivate because customer status is not active");
        }
        status = CustomerStatus.INACTIVE;
    }

    public void activateCustomer() {
        if (status == CustomerStatus.ACTIVE) {
            throw new CustomerDomainException("Customer is already active");
        }
        status = CustomerStatus.ACTIVE;
    }

    public String getUsername() {
        return username;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public Email getEmail() {
        return email;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public CustomerSegment getSegment() {
        return segment;
    }

    private Customer(Builder builder) {
        super.setId(builder.id);
        this.username = builder.username;
        this.familyName = builder.familyName;
        this.givenName = builder.givenName;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.status = builder.status;
        this.segment = builder.segment;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CustomerId id;
        private String username;
        private String familyName;
        private String givenName;
        private Email email;
        private PhoneNumber phoneNumber;
        private CustomerStatus status;
        private CustomerSegment segment;

        private Builder() {
        }

        public Builder id(CustomerId val) {
            this.id = val;
            return this;
        }

        public Builder username(String val) {
            this.username = val;
            return this;
        }

        public Builder familyName(String val) {
            this.familyName = val;
            return this;
        }

        public Builder givenName(String val) {
            this.givenName = val;
            return this;
        }

        public Builder email(Email val) {
            this.email = val;
            return this;
        }

        public Builder phoneNumber(PhoneNumber val) {
            this.phoneNumber = val;
            return this;
        }

        public Builder status(CustomerStatus val) {
            this.status = val;
            return this;
        }

        public Builder segment(CustomerSegment val) {
            this.segment = val;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
