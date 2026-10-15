package kh.edu.istad.platform.business.domain;

import kh.edu.istad.platform.business.domain.dto.RegisterBusinessCommand;
import kh.edu.istad.platform.business.domain.entity.Business;
import kh.edu.istad.platform.business.domain.exception.BusinessDomainException;
import kh.edu.istad.platform.business.domain.mapper.BusinessDataMapper;
import kh.edu.istad.platform.business.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.business.domain.service.BusinessDomainService;
import kh.edu.istad.platform.business.domain.service.BusinessDomainServiceImpl;
import kh.edu.istad.platform.business.domain.usecase.RegisterBusinessUseCase;
import kh.edu.istad.platform.business.domain.valueobject.BusinessAddress;
import kh.edu.istad.platform.business.domain.valueobject.BusinessContact;
import kh.edu.istad.platform.business.domain.valueobject.BusinessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterBusinessUseCaseTest {

    @Mock
    private BusinessRepository businessRepository;

    private RegisterBusinessUseCase registerBusinessUseCase;

    @BeforeEach
    void setUp() {
        BusinessDomainService businessDomainService = new BusinessDomainServiceImpl();
        BusinessDataMapper businessDataMapper = new BusinessDataMapper();
        registerBusinessUseCase = new RegisterBusinessUseCase(businessDomainService, businessRepository, businessDataMapper);
    }

    @Test
    void testExecuteRegisterBusiness_Success() {
        RegisterBusinessCommand command = RegisterBusinessCommand.builder()
                .name("Super Mart")
                .address(new BusinessAddress("St 1986", "Phnom Penh", "12000"))
                .contact(new BusinessContact("info@supermart.com", "+85599887766"))
                .build();

        when(businessRepository.existsByName("Super Mart")).thenReturn(Mono.just(false));
        when(businessRepository.save(any(Business.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(registerBusinessUseCase.execute(command))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertNotNull(result.businessId());
                    assertEquals(BusinessStatus.ACTIVE, result.status());
                    assertEquals("Business registered successfully", result.message());
                })
                .verifyComplete();
    }

    @Test
    void testExecuteRegisterBusiness_ThrowsException_WhenDuplicateName() {
        RegisterBusinessCommand command = RegisterBusinessCommand.builder()
                .name("Super Mart")
                .address(new BusinessAddress("St 1986", "Phnom Penh", "12000"))
                .contact(new BusinessContact("info@supermart.com", "+85599887766"))
                .build();

        when(businessRepository.existsByName("Super Mart")).thenReturn(Mono.just(true));

        StepVerifier.create(registerBusinessUseCase.execute(command))
                .expectError(BusinessDomainException.class)
                .verify();
    }
}
