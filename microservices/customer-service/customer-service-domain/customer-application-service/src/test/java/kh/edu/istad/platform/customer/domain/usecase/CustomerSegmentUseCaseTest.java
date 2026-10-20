package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.exception.DuplicateResourceException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerSegmentUseCaseTest {

    @Mock
    private CustomerSegmentRepository customerSegmentRepository;

    @Test
    @DisplayName("CreateCustomerSegmentUseCase should save when name is unique")
    void shouldCreateCustomerSegment() {
        CreateCustomerSegmentUseCase useCase = new CreateCustomerSegmentUseCase(customerSegmentRepository);

        CustomerSegment segment = CustomerSegment.builder()
                .name("Platinum")
                .type(CustomerSegmentType.PREMIUM)
                .description("VIP")
                .build();

        when(customerSegmentRepository.existsByName("Platinum")).thenReturn(Mono.just(false));
        when(customerSegmentRepository.save(any(CustomerSegment.class))).thenReturn(Mono.just(segment));

        StepVerifier.create(useCase.create(segment))
                .assertNext(result -> assertThat(result.getName()).isEqualTo("Platinum"))
                .verifyComplete();
    }

    @Test
    @DisplayName("CreateCustomerSegmentUseCase should fail when name already exists")
    void shouldFailWhenSegmentNameExists() {
        CreateCustomerSegmentUseCase useCase = new CreateCustomerSegmentUseCase(customerSegmentRepository);

        CustomerSegment segment = CustomerSegment.builder()
                .name("Gold")
                .type(CustomerSegmentType.GOLD)
                .build();

        when(customerSegmentRepository.existsByName("Gold")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.create(segment))
                .expectError(DuplicateResourceException.class)
                .verify();
    }

    @Test
    @DisplayName("DeleteCustomerSegmentUseCase should fail if customers are assigned to the segment")
    void shouldPreventDeleteWhenCustomersAssigned() {
        DeleteCustomerSegmentUseCase useCase = new DeleteCustomerSegmentUseCase(customerSegmentRepository);

        CustomerSegment segment = CustomerSegment.builder()
                .id("seg-1")
                .name("Gold")
                .type(CustomerSegmentType.GOLD)
                .build();

        when(customerSegmentRepository.findById("seg-1")).thenReturn(Mono.just(segment));
        when(customerSegmentRepository.countCustomersBySegmentId("seg-1")).thenReturn(Mono.just(5L));

        StepVerifier.create(useCase.delete("seg-1"))
                .expectErrorMatches(throwable -> throwable instanceof CustomerDomainException
                        && throwable.getMessage().contains("5 customer(s) are currently assigned"))
                .verify();
    }

    @Test
    @DisplayName("DeleteCustomerSegmentUseCase should succeed when no customers are assigned")
    void shouldDeleteWhenNoCustomersAssigned() {
        DeleteCustomerSegmentUseCase useCase = new DeleteCustomerSegmentUseCase(customerSegmentRepository);

        CustomerSegment segment = CustomerSegment.builder()
                .id("seg-1")
                .name("Gold")
                .type(CustomerSegmentType.GOLD)
                .build();

        when(customerSegmentRepository.findById("seg-1")).thenReturn(Mono.just(segment));
        when(customerSegmentRepository.countCustomersBySegmentId("seg-1")).thenReturn(Mono.just(0L));
        when(customerSegmentRepository.deleteById("seg-1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.delete("seg-1"))
                .verifyComplete();
    }
}
