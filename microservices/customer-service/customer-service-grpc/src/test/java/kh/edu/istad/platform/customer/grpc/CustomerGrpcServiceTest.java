package kh.edu.istad.platform.customer.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilterQuery;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.usecase.CreateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerUseCase;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import build.buf.gen.istad.customer.v1.GetCustomerRequest;
import build.buf.gen.istad.customer.v1.GetCustomerResponse;
import build.buf.gen.istad.customer.v1.ListCustomersRequest;
import build.buf.gen.istad.customer.v1.ListCustomersResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerGrpcServiceTest {

    @Mock
    private QueryCustomerUseCase queryCustomerUseCase;

    @Mock
    private CreateCustomerUseCase createCustomerUseCase;

    private CustomerGrpcService grpcService;

    @BeforeEach
    void setUp() {
        grpcService = new CustomerGrpcService(queryCustomerUseCase, createCustomerUseCase);
    }

    @Test
    void getCustomer_withValidId_returnsCustomer() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(new CustomerId(id))
                .username("testuser")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .status(CustomerStatus.ACTIVE)
                .build();

        when(queryCustomerUseCase.findById(id)).thenReturn(Mono.just(customer));

        List<GetCustomerResponse> responses = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        boolean[] completed = {false};

        grpcService.getCustomer(
                GetCustomerRequest.newBuilder().setCustomerId(id.toString()).build(),
                new StreamObserver<>() {
                    @Override public void onNext(GetCustomerResponse value) { responses.add(value); }
                    @Override public void onError(Throwable t) { errors.add(t); }
                    @Override public void onCompleted() { completed[0] = true; }
                }
        );

        assertTrue(errors.isEmpty());
        assertTrue(completed[0]);
        assertEquals(1, responses.size());
        assertEquals("testuser", responses.get(0).getCustomer().getUsername());
        assertEquals(id.toString(), responses.get(0).getCustomer().getCustomerId());
        assertEquals(build.buf.gen.istad.customer.v1.CustomerStatus.CUSTOMER_STATUS_ACTIVE, responses.get(0).getCustomer().getStatus());
    }

    @Test
    void getCustomer_withInvalidUUID_returnsInvalidArgument() {
        List<Throwable> errors = new ArrayList<>();

        grpcService.getCustomer(
                GetCustomerRequest.newBuilder().setCustomerId("not-a-uuid").build(),
                new StreamObserver<>() {
                    @Override public void onNext(GetCustomerResponse value) { }
                    @Override public void onError(Throwable t) { errors.add(t); }
                    @Override public void onCompleted() { }
                }
        );

        assertEquals(1, errors.size());
        assertTrue(errors.get(0) instanceof StatusRuntimeException);
        assertEquals(Status.Code.INVALID_ARGUMENT, ((StatusRuntimeException) errors.get(0)).getStatus().getCode());
    }

    @Test
    void getCustomer_whenNotFound_returnsNotFound() {
        UUID id = UUID.randomUUID();
        when(queryCustomerUseCase.findById(id)).thenReturn(Mono.empty());

        List<Throwable> errors = new ArrayList<>();

        grpcService.getCustomer(
                GetCustomerRequest.newBuilder().setCustomerId(id.toString()).build(),
                new StreamObserver<>() {
                    @Override public void onNext(GetCustomerResponse value) { }
                    @Override public void onError(Throwable t) { errors.add(t); }
                    @Override public void onCompleted() { }
                }
        );

        assertEquals(1, errors.size());
        assertTrue(errors.get(0) instanceof StatusRuntimeException);
        assertEquals(Status.Code.NOT_FOUND, ((StatusRuntimeException) errors.get(0)).getStatus().getCode());
    }

    @Test
    void listCustomers_streamsResponses() {
        Customer c1 = Customer.builder()
                .id(new CustomerId(UUID.randomUUID()))
                .username("user1")
                .status(CustomerStatus.ACTIVE)
                .build();
        Customer c2 = Customer.builder()
                .id(new CustomerId(UUID.randomUUID()))
                .username("user2")
                .status(CustomerStatus.INACTIVE)
                .build();

        when(queryCustomerUseCase.query(any(CustomerFilterQuery.class))).thenReturn(Flux.just(c1, c2));

        List<ListCustomersResponse> responses = new ArrayList<>();
        boolean[] completed = {false};

        grpcService.listCustomers(
                ListCustomersRequest.newBuilder().setKeyword("user").build(),
                new StreamObserver<>() {
                    @Override public void onNext(ListCustomersResponse value) { responses.add(value); }
                    @Override public void onError(Throwable t) { fail(t); }
                    @Override public void onCompleted() { completed[0] = true; }
                }
        );

        assertTrue(completed[0]);
        assertEquals(2, responses.size());
        assertEquals("user1", responses.get(0).getCustomer().getUsername());
        assertEquals("user2", responses.get(1).getCustomer().getUsername());
    }
}
