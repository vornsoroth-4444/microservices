package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.exception.DuplicateResourceException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerSegmentRepository customerSegmentRepository;

    private CreateCustomerUseCase createCustomerUseCase;

    @BeforeEach
    void setUp() {
        createCustomerUseCase = new CreateCustomerUseCase(customerRepository, customerSegmentRepository);
    }

    @Test
    @DisplayName("Should successfully create customer when inputs are valid and unique")
    void shouldCreateCustomerSuccessfully() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+123456789"))
                .build();

        when(customerRepository.existsByUsername("john_doe")).thenReturn(Mono.just(false));
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(Mono.just(false));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createCustomerUseCase.create(customer))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
                    assertThat(saved.getUsername()).isEqualTo("john_doe");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail when username is duplicate")
    void shouldFailWhenUsernameIsDuplicate() {
        Customer customer = Customer.builder()
                .username("existing_user")
                .familyName("Doe")
                .givenName("John")
                .build();

        when(customerRepository.existsByUsername("existing_user")).thenReturn(Mono.just(true));

        StepVerifier.create(createCustomerUseCase.create(customer))
                .expectErrorMatches(throwable -> throwable instanceof DuplicateResourceException
                        && throwable.getMessage().contains("already exists"))
                .verify();
    }

    @Test
    @DisplayName("Should fail when email is duplicate")
    void shouldFailWhenEmailIsDuplicate() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("duplicate@example.com"))
                .build();

        when(customerRepository.existsByUsername("john_doe")).thenReturn(Mono.just(false));
        when(customerRepository.existsByEmail("duplicate@example.com")).thenReturn(Mono.just(true));

        StepVerifier.create(createCustomerUseCase.create(customer))
                .expectErrorMatches(throwable -> throwable instanceof DuplicateResourceException
                        && throwable.getMessage().contains("duplicate@example.com"))
                .verify();
    }

    @Test
    @DisplayName("Should associate segment when segmentId is provided and valid")
    void shouldAssociateSegmentWhenValid() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+123456789"))
                .build();

        CustomerSegment segment = CustomerSegment.builder()
                .id("seg-123")
                .name("Gold")
                .type(CustomerSegmentType.GOLD)
                .build();

        when(customerRepository.existsByUsername("john_doe")).thenReturn(Mono.just(false));
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(Mono.just(false));
        when(customerSegmentRepository.findById("seg-123")).thenReturn(Mono.just(segment));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createCustomerUseCase.create(customer, "seg-123"))
                .assertNext(saved -> {
                    assertThat(saved.getSegment()).isNotNull();
                    assertThat(saved.getSegment().getName()).isEqualTo("Gold");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail when segmentId is provided but not found")
    void shouldFailWhenSegmentNotFound() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .build();

        when(customerRepository.existsByUsername("john_doe")).thenReturn(Mono.just(false));
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(Mono.just(false));
        when(customerSegmentRepository.findById("non-existent")).thenReturn(Mono.empty());

        StepVerifier.create(createCustomerUseCase.create(customer, "non-existent"))
                .expectError(CustomerSegmentNotFoundException.class)
                .verify();
    }
}
