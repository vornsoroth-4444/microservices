package kh.edu.istad.platform.order.domain.port.out.client;

import kh.edu.istad.platform.order.domain.dto.GetCustomerResult;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerServiceClient {
    Flux<GetCustomerResult> getCustomerNames();
    Flux<QueryCustomerResult> queryCustomers();
    Flux<GetCustomerResult> listCustomersGrpc();
    Mono<GetCustomerResult> getCustomerGrpc(String id);
}
