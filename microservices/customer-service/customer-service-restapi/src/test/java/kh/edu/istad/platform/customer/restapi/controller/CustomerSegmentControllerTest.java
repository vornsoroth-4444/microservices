package kh.edu.istad.platform.customer.restapi.controller;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.usecase.CreateCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.DeleteCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.usecase.UpdateCustomerSegmentUseCase;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import kh.edu.istad.platform.customer.restapi.dto.CreateCustomerSegmentRequest;
import kh.edu.istad.platform.customer.restapi.dto.UpdateCustomerSegmentRequest;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerSegmentWebMapper;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerSegmentWebMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerSegmentControllerTest {

    @Mock
    private CreateCustomerSegmentUseCase createCustomerSegmentUseCase;
    @Mock
    private QueryCustomerSegmentUseCase queryCustomerSegmentUseCase;
    @Mock
    private UpdateCustomerSegmentUseCase updateCustomerSegmentUseCase;
    @Mock
    private DeleteCustomerSegmentUseCase deleteCustomerSegmentUseCase;

    private final CustomerSegmentWebMapper mapper = new CustomerSegmentWebMapperImpl();

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        CustomerSegmentController controller = new CustomerSegmentController(
                createCustomerSegmentUseCase,
                queryCustomerSegmentUseCase,
                updateCustomerSegmentUseCase,
                deleteCustomerSegmentUseCase,
                mapper
        );

        webTestClient = WebTestClient.bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/customer-segments should create segment and return 201 Created")
    void shouldCreateSegment() {
        CustomerSegment segment = CustomerSegment.builder()
                .id("seg-1")
                .name("VIP")
                .type(CustomerSegmentType.PREMIUM)
                .description("VIP customers")
                .build();

        when(createCustomerSegmentUseCase.create(any(CustomerSegment.class))).thenReturn(Mono.just(segment));

        CreateCustomerSegmentRequest request = CreateCustomerSegmentRequest.builder()
                .name("VIP")
                .type(CustomerSegmentType.PREMIUM)
                .description("VIP customers")
                .build();

        webTestClient.post()
                .uri("/api/customer-segments")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("seg-1")
                .jsonPath("$.name").isEqualTo("VIP")
                .jsonPath("$.type").isEqualTo("PREMIUM");
    }

    @Test
    @DisplayName("GET /api/customer-segments should return all segments")
    void shouldFindAllSegments() {
        CustomerSegment seg1 = CustomerSegment.builder().id("1").name("Silver").type(CustomerSegmentType.SILVER).build();
        CustomerSegment seg2 = CustomerSegment.builder().id("2").name("Gold").type(CustomerSegmentType.GOLD).build();

        when(queryCustomerSegmentUseCase.findAll()).thenReturn(Flux.just(seg1, seg2));

        webTestClient.get()
                .uri("/api/customer-segments")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Silver")
                .jsonPath("$[1].name").isEqualTo("Gold");
    }

    @Test
    @DisplayName("GET /api/customer-segments/{segmentId} should return segment")
    void shouldFindById() {
        CustomerSegment seg = CustomerSegment.builder().id("seg-1").name("Silver").type(CustomerSegmentType.SILVER).build();

        when(queryCustomerSegmentUseCase.findById("seg-1")).thenReturn(Mono.just(seg));

        webTestClient.get()
                .uri("/api/customer-segments/{segmentId}", "seg-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Silver");
    }

    @Test
    @DisplayName("PUT /api/customer-segments/{segmentId} should update segment")
    void shouldUpdateSegment() {
        CustomerSegment updated = CustomerSegment.builder().id("seg-1").name("Gold").type(CustomerSegmentType.GOLD).build();

        when(updateCustomerSegmentUseCase.update(eq("seg-1"), eq("Gold"), eq(CustomerSegmentType.GOLD), eq("New desc")))
                .thenReturn(Mono.just(updated));

        UpdateCustomerSegmentRequest request = UpdateCustomerSegmentRequest.builder()
                .name("Gold")
                .type(CustomerSegmentType.GOLD)
                .description("New desc")
                .build();

        webTestClient.put()
                .uri("/api/customer-segments/{segmentId}", "seg-1")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Gold");
    }

    @Test
    @DisplayName("DELETE /api/customer-segments/{segmentId} should return 204 No Content")
    void shouldDeleteSegment() {
        when(deleteCustomerSegmentUseCase.delete("seg-1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/customer-segments/{segmentId}", "seg-1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
