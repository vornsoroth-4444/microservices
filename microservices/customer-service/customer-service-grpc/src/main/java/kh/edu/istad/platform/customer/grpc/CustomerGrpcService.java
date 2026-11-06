package kh.edu.istad.platform.customer.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilterQuery;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.usecase.CreateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.QueryCustomerUseCase;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import build.buf.gen.istad.customer.v1.CreateCustomerRequest;
import build.buf.gen.istad.customer.v1.CreateCustomerResponse;
import build.buf.gen.istad.customer.v1.CustomerServiceGrpc;
import build.buf.gen.istad.customer.v1.GetCustomerRequest;
import build.buf.gen.istad.customer.v1.GetCustomerResponse;
import build.buf.gen.istad.customer.v1.ListCustomersRequest;
import build.buf.gen.istad.customer.v1.ListCustomersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final QueryCustomerUseCase queryCustomerUseCase;
    private final CreateCustomerUseCase createCustomerUseCase;

    @Override
    public void getCustomer(GetCustomerRequest request, StreamObserver<GetCustomerResponse> observer) {
        UUID id;
        try {
            id = UUID.fromString(request.getCustomerId());
        } catch (IllegalArgumentException e) {
            observer.onError(Status.INVALID_ARGUMENT.withDescription("customer_id must be a UUID").asRuntimeException());
            return;
        }
        queryCustomerUseCase.findById(id)
                .switchIfEmpty(Mono.error(
                        Status.NOT_FOUND.withDescription("customer not found: " + id).asRuntimeException()))
                .subscribe(
                        customer -> {
                            observer.onNext(GetCustomerResponse.newBuilder().setCustomer(toProto(customer)).build());
                            observer.onCompleted();
                        },
                        err -> observer.onError(err instanceof StatusRuntimeException
                                ? err
                                : Status.INTERNAL.withDescription(err.getMessage()).asRuntimeException()));
    }

    @Override
    public void listCustomers(ListCustomersRequest request, StreamObserver<ListCustomersResponse> observer) {
        CustomerFilterQuery.CustomerFilterQueryBuilder filter = CustomerFilterQuery.builder();
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            filter.keyword(request.getKeyword());
        }
        if (request.getStatus() != null && request.getStatus() != build.buf.gen.istad.customer.v1.CustomerStatus.CUSTOMER_STATUS_UNSPECIFIED) {
            if (request.getStatus() == build.buf.gen.istad.customer.v1.CustomerStatus.CUSTOMER_STATUS_ACTIVE) {
                filter.status(CustomerStatus.ACTIVE);
            } else if (request.getStatus() == build.buf.gen.istad.customer.v1.CustomerStatus.CUSTOMER_STATUS_INACTIVE) {
                filter.status(CustomerStatus.INACTIVE);
            }
        }
        if (request.getPage() > 0) {
            filter.page(request.getPage());
        }
        if (request.getSize() > 0) {
            filter.size(request.getSize());
        }

        queryCustomerUseCase.query(filter.build()).subscribe(
                c -> observer.onNext(ListCustomersResponse.newBuilder().setCustomer(toProto(c)).build()),
                err -> observer.onError(Status.INTERNAL.withDescription(err.getMessage()).asRuntimeException()),
                observer::onCompleted);
    }

    @Override
    public void createCustomer(CreateCustomerRequest request, StreamObserver<CreateCustomerResponse> observer) {
        try {
            Customer domainCustomer = Customer.builder()
                    .username(request.getUsername())
                    .familyName(request.getFamilyName())
                    .givenName(request.getGivenName())
                    .email(new Email(request.getEmail()))
                    .phoneNumber(new PhoneNumber(request.getPhoneNumber()))
                    .build();

            createCustomerUseCase.create(domainCustomer).subscribe(
                    saved -> {
                        observer.onNext(CreateCustomerResponse.newBuilder().setCustomer(toProto(saved)).build());
                        observer.onCompleted();
                    },
                    err -> observer.onError(err instanceof StatusRuntimeException
                            ? err
                            : Status.INTERNAL.withDescription(err.getMessage()).asRuntimeException())
            );
        } catch (Exception e) {
            observer.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private build.buf.gen.istad.customer.v1.Customer toProto(Customer customer) {
        build.buf.gen.istad.customer.v1.Customer.Builder builder = build.buf.gen.istad.customer.v1.Customer.newBuilder();
        if (customer.getId() != null && customer.getId().value() != null) {
            builder.setCustomerId(customer.getId().value().toString());
        }
        if (customer.getUsername() != null) {
            builder.setUsername(customer.getUsername());
        }
        if (customer.getFamilyName() != null) {
            builder.setFamilyName(customer.getFamilyName());
        }
        if (customer.getGivenName() != null) {
            builder.setGivenName(customer.getGivenName());
        }
        if (customer.getEmail() != null && customer.getEmail().value() != null) {
            builder.setEmail(customer.getEmail().value());
        }
        if (customer.getPhoneNumber() != null && customer.getPhoneNumber().value() != null) {
            builder.setPhoneNumber(customer.getPhoneNumber().value());
        }
        if (customer.getStatus() != null) {
            switch (customer.getStatus()) {
                case ACTIVE -> builder.setStatus(build.buf.gen.istad.customer.v1.CustomerStatus.CUSTOMER_STATUS_ACTIVE);
                case INACTIVE -> builder.setStatus(build.buf.gen.istad.customer.v1.CustomerStatus.CUSTOMER_STATUS_INACTIVE);
            }
        }
        return builder.build();
    }
}
