package kh.edu.istad.platform.order.client.grpc;

import io.grpc.stub.StreamObserver;
import kh.edu.istad.platform.order.domain.dto.GetCustomerResult;
import build.buf.gen.istad.customer.v1.Customer;
import build.buf.gen.istad.customer.v1.CustomerServiceGrpc;
import build.buf.gen.istad.customer.v1.GetCustomerRequest;
import build.buf.gen.istad.customer.v1.GetCustomerResponse;
import build.buf.gen.istad.customer.v1.ListCustomersRequest;
import build.buf.gen.istad.customer.v1.ListCustomersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerGrpcClient {
    private final CustomerServiceGrpc.CustomerServiceStub stub;

    public Mono<GetCustomerResult> getCustomer(String customerId) {
        return Mono.create(sink -> stub.getCustomer(
                GetCustomerRequest.newBuilder().setCustomerId(customerId).build(),
                new StreamObserver<GetCustomerResponse>() {
                    public void onNext(GetCustomerResponse r) { sink.success(toResult(r.getCustomer())); }
                    public void onError(Throwable t) { sink.error(t); }
                    public void onCompleted() { }
                }));
    }

    public Flux<GetCustomerResult> listCustomers() {
        return Flux.create(sink -> stub.listCustomers(
                ListCustomersRequest.getDefaultInstance(),
                new StreamObserver<ListCustomersResponse>() {
                    public void onNext(ListCustomersResponse r) { sink.next(toResult(r.getCustomer())); }
                    public void onError(Throwable t) { sink.error(t); }
                    public void onCompleted() { sink.complete(); }
                }));
    }

    private GetCustomerResult toResult(Customer customer) {
        return new GetCustomerResult(
                customer.getCustomerId(),
                customer.getUsername(),
                customer.getFamilyName(),
                customer.getGivenName(),
                customer.getStatus().name()
        );
    }
}
