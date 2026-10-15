package kh.edu.istad.platform.business.restapi;

import kh.edu.istad.platform.business.domain.dto.GetBusinessResult;
import kh.edu.istad.platform.business.domain.dto.RegisterBusinessCommand;
import kh.edu.istad.platform.business.domain.dto.RegisterBusinessResult;
import kh.edu.istad.platform.business.domain.usecase.GetBusinessUseCase;
import kh.edu.istad.platform.business.domain.usecase.RegisterBusinessUseCase;
import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import kh.edu.istad.platform.business.restapi.controller.BusinessController;
import kh.edu.istad.platform.business.restapi.dto.BusinessAddressRequest;
import kh.edu.istad.platform.business.restapi.dto.BusinessContactRequest;
import kh.edu.istad.platform.business.restapi.dto.RegisterBusinessRequest;
import kh.edu.istad.platform.business.restapi.handler.BusinessGlobalExceptionHandler;
import kh.edu.istad.platform.business.restapi.mapper.BusinessWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private RegisterBusinessUseCase registerBusinessUseCase;

    @Mock
    private GetBusinessUseCase getBusinessUseCase;

    @BeforeEach
    void setUp() {
        BusinessWebMapper businessWebMapper = new BusinessWebMapper();
        BusinessController businessController = new BusinessController(registerBusinessUseCase, getBusinessUseCase, businessWebMapper);
        webTestClient = WebTestClient.bindToController(businessController)
                .controllerAdvice(new BusinessGlobalExceptionHandler())
                .build();
    }

    @Test
    void testRegisterBusiness_Success() {
        UUID businessId = UUID.randomUUID();
        when(registerBusinessUseCase.execute(any(RegisterBusinessCommand.class)))
                .thenReturn(Mono.just(RegisterBusinessResult.builder()
                        .businessId(businessId)
                        .status(BusinessStatus.ACTIVE)
                        .message("Business registered successfully")
                        .build()));

        RegisterBusinessRequest request = RegisterBusinessRequest.builder()
                .name("Bakery Bliss")
                .address(BusinessAddressRequest.builder()
                        .street("Street 110")
                        .city("Phnom Penh")
                        .postalCode("12200")
                        .build())
                .contact(BusinessContactRequest.builder()
                        .email("info@bakerybliss.com")
                        .phone("+85512345678")
                        .build())
                .build();

        webTestClient.post()
                .uri("/api/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.businessId").isEqualTo(businessId.toString())
                .jsonPath("$.status").isEqualTo("ACTIVE")
                .jsonPath("$.message").isEqualTo("Business registered successfully");
    }

    @Test
    void testRegisterBusiness_ValidationFailure_WhenNameBlank() {
        RegisterBusinessRequest request = RegisterBusinessRequest.builder()
                .name(" ") // blank name
                .address(BusinessAddressRequest.builder()
                        .street("Street 110")
                        .city("Phnom Penh")
                        .postalCode("12200")
                        .build())
                .contact(BusinessContactRequest.builder()
                        .email("info@bakerybliss.com")
                        .phone("+85512345678")
                        .build())
                .build();

        webTestClient.post()
                .uri("/api/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetBusiness_Success() {
        UUID businessId = UUID.randomUUID();
        when(getBusinessUseCase.execute(any()))
                .thenReturn(Mono.just(GetBusinessResult.builder()
                        .businessId(businessId)
                        .name("Bakery Bliss")
                        .status(BusinessStatus.ACTIVE)
                        .address(new BusinessAddress("Street 110", "Phnom Penh", "12200"))
                        .contact(new BusinessContact("info@bakerybliss.com", "+85512345678"))
                        .build()));

        webTestClient.get()
                .uri("/api/businesses/" + businessId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.businessId").isEqualTo(businessId.toString())
                .jsonPath("$.name").isEqualTo("Bakery Bliss")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }
}
