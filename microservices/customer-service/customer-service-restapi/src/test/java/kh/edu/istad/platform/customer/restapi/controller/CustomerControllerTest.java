package kh.edu.istad.platform.customer.restapi.controller;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilterQuery;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistic;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.usecase.*;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import kh.edu.istad.platform.customer.restapi.dto.CreateCustomerRequest;
import kh.edu.istad.platform.customer.restapi.dto.UpdateCustomerRequest;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CreateCustomerUseCase createCustomerUseCase;
    @Mock
    private QueryCustomerUseCase queryCustomerUseCase;
    @Mock
    private UpdateCustomerUseCase updateCustomerUseCase;
    @Mock
    private PatchCustomerUseCase patchCustomerUseCase;
    @Mock
    private DeactivateCustomerUseCase deactivateCustomerUseCase;
    @Mock
    private ActivateCustomerUseCase activateCustomerUseCase;
    @Mock
    private DeleteCustomerUseCase deleteCustomerUseCase;

    private final CustomerWebMapper customerWebMapper = new CustomerWebMapperImpl();

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        CustomerController customerController = new CustomerController(
                createCustomerUseCase,
                queryCustomerUseCase,
                updateCustomerUseCase,
                patchCustomerUseCase,
                deactivateCustomerUseCase,
                activateCustomerUseCase,
                deleteCustomerUseCase,
                customerWebMapper
        );

        webTestClient = WebTestClient.bindToController(customerController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/customers should create customer and return 201 Created")
    void shouldCreateCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(new CustomerId(customerId))
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+123456789"))
                .status(CustomerStatus.ACTIVE)
                .build();

        when(createCustomerUseCase.create(any(Customer.class), eq("seg-1"))).thenReturn(Mono.just(customer));

        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email("john@example.com")
                .phoneNumber("+123456789")
                .segmentId("seg-1")
                .build();

        webTestClient.post()
                .uri("/api/customers")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.username").isEqualTo("john_doe")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("GET /api/customers/{customerId} should return customer")
    void shouldGetCustomerById() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(new CustomerId(customerId))
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .status(CustomerStatus.ACTIVE)
                .build();

        when(queryCustomerUseCase.findById(customerId)).thenReturn(Mono.just(customer));

        webTestClient.get()
                .uri("/api/customers/{customerId}", customerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("john_doe");
    }

    @Test
    @DisplayName("PUT /api/customers/{customerId} should update customer")
    void shouldUpdateCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer updated = Customer.builder()
                .id(new CustomerId(customerId))
                .username("john_doe")
                .familyName("Smith")
                .givenName("Jane")
                .status(CustomerStatus.ACTIVE)
                .build();

        when(updateCustomerUseCase.update(eq(customerId), eq("Smith"), eq("Jane"), eq("jane@example.com"), eq("+987654321"), eq("seg-2")))
                .thenReturn(Mono.just(updated));

        UpdateCustomerRequest request = UpdateCustomerRequest.builder()
                .familyName("Smith")
                .givenName("Jane")
                .email("jane@example.com")
                .phoneNumber("+987654321")
                .segmentId("seg-2")
                .build();

        webTestClient.put()
                .uri("/api/customers/{customerId}", customerId)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.familyName").isEqualTo("Smith")
                .jsonPath("$.givenName").isEqualTo("Jane");
    }

    @Test
    @DisplayName("PATCH /api/customers/{customerId}/deactivate should deactivate customer")
    void shouldDeactivateCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer deactivated = Customer.builder()
                .id(new CustomerId(customerId))
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .status(CustomerStatus.INACTIVE)
                .build();

        when(deactivateCustomerUseCase.deactivate(customerId)).thenReturn(Mono.just(deactivated));

        webTestClient.patch()
                .uri("/api/customers/{customerId}/deactivate", customerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("DELETE /api/customers/{customerId} should delete customer and return 204 No Content")
    void shouldDeleteCustomer() {
        UUID customerId = UUID.randomUUID();
        when(deleteCustomerUseCase.delete(customerId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/customers/{customerId}", customerId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("GET /api/customers/statistics should report total customers by status and segment")
    void shouldReportStatisticsByStatusAndSegment() {
        CustomerStatistic stat1 = CustomerStatistic.builder()
                .status(CustomerStatus.ACTIVE)
                .segmentName("Gold")
                .total(15L)
                .build();
        CustomerStatistic stat2 = CustomerStatistic.builder()
                .status(CustomerStatus.ACTIVE)
                .segmentName("Silver")
                .total(25L)
                .build();

        when(queryCustomerUseCase.getStatistic(any(CustomerFilterQuery.class)))
                .thenReturn(Flux.just(stat1, stat2));

        webTestClient.get()
                .uri("/api/customers/statistics")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].status").isEqualTo("ACTIVE")
                .jsonPath("$[0].segmentName").isEqualTo("Gold")
                .jsonPath("$[0].total").isEqualTo(15)
                .jsonPath("$[1].status").isEqualTo("ACTIVE")
                .jsonPath("$[1].segmentName").isEqualTo("Silver")
                .jsonPath("$[1].total").isEqualTo(25);
    }
}
