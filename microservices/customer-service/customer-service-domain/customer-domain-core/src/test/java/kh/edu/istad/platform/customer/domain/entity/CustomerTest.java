package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    @Test
    @DisplayName("Should successfully initiate customer with generated UUID and ACTIVE status")
    void shouldInitiateCustomerSuccessfully() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+123456789"))
                .build();

        customer.initiateCustomer();

        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getId().value()).isNotNull();
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throw exception when initiating customer with pre-existing ID")
    void shouldThrowExceptionWhenInitiatingWithExistingId() {
        Customer customer = Customer.builder()
                .id(new CustomerId(UUID.randomUUID()))
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .build();

        assertThatThrownBy(customer::initiateCustomer)
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("Customer id must be null prior to initiation");
    }

    @Test
    @DisplayName("Should throw exception when initiating customer with blank username")
    void shouldThrowExceptionWhenInitiatingWithBlankUsername() {
        Customer customer = Customer.builder()
                .username("   ")
                .familyName("Doe")
                .givenName("John")
                .build();

        assertThatThrownBy(customer::initiateCustomer)
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("username must not be blank");
    }

    @Test
    @DisplayName("Should successfully update customer details")
    void shouldUpdateCustomerDetails() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+123456789"))
                .build();
        customer.initiateCustomer();

        CustomerSegment segment = CustomerSegment.builder()
                .id("seg-1")
                .name("Gold")
                .type(CustomerSegmentType.GOLD)
                .build();

        customer.updateDetails(
                "Smith",
                "Jane",
                new Email("jane.smith@example.com"),
                new PhoneNumber("+987654321"),
                segment
        );

        assertThat(customer.getFamilyName()).isEqualTo("Smith");
        assertThat(customer.getGivenName()).isEqualTo("Jane");
        assertThat(customer.getEmail().value()).isEqualTo("jane.smith@example.com");
        assertThat(customer.getPhoneNumber().value()).isEqualTo("+987654321");
        assertThat(customer.getSegment()).isEqualTo(segment);
    }

    @Test
    @DisplayName("Should deactivate ACTIVE customer and throw if already INACTIVE")
    void shouldDeactivateCustomer() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .build();
        customer.initiateCustomer();
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);

        customer.deactivateCustomer();
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.INACTIVE);

        assertThatThrownBy(customer::deactivateCustomer)
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("Could not deactivate because customer status is not active");
    }

    @Test
    @DisplayName("Should activate INACTIVE customer and throw if already ACTIVE")
    void shouldActivateCustomer() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .build();
        customer.initiateCustomer();
        customer.deactivateCustomer();

        customer.activateCustomer();
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);

        assertThatThrownBy(customer::activateCustomer)
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("Customer is already active");
    }
}
