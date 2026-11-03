package kh.edu.istad.platform.order.domain.port.out.client;

import kh.edu.istad.platform.order.domain.dto.CustomerResponse;
import kh.edu.istad.platform.order.domain.dto.GetCustomerResult;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerServiceClient {
    Flux<GetCustomerResult> getCustomerNames();
    Flux<QueryCustomerResult> queryCustomers();

    @ClientRegistrationId("customer-client")
    CustomerResponse getCustomer(String customerId);

    Mono<CustomerResponse> getCustomerById(String customerId);
}
