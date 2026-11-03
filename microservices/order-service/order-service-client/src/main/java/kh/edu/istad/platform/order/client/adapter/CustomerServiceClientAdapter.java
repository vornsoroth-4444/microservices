package kh.edu.istad.platform.order.client.adapter;

import kh.edu.istad.platform.order.client.httpClient.CustomerHttpClient;
import kh.edu.istad.platform.order.domain.dto.CustomerResponse;
import kh.edu.istad.platform.order.domain.dto.GetCustomerResult;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class CustomerServiceClientAdapter implements CustomerServiceClient {
    private final CustomerHttpClient customerHttpClient;
    private final WebClient.Builder webClientBuilder;

    public record GetCustomer(
            String customerId,
            String username,
            String familyName,
            String givenName,
            String status
    ) {
    }

    @Override
    public Flux<GetCustomerResult> getCustomerNames() {
        WebClient webClient = webClientBuilder
                .baseUrl("http://customer-service")
                .build();

        var response = webClient
                .get()
                .uri("/api/customers")
                .retrieve()
                .bodyToFlux(GetCustomer.class);
        return response.map(cus -> new GetCustomerResult(
                cus.customerId,
                cus.username,
                cus.familyName,
                cus.givenName,
                cus.status
        ));
    }

    @Override
    public Flux<QueryCustomerResult> queryCustomers() {
        return Flux.defer(() -> Flux.fromIterable(customerHttpClient.queryCustomer()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public CustomerResponse getCustomer(String customerId) {
        CustomerResponse response = customerHttpClient.getCustomer(customerId);
        if (response != null && response.customerId() == null) {
            return new CustomerResponse(
                    customerId,
                    response.username(),
                    response.familyName(),
                    response.givenName(),
                    response.status()
            );
        }
        return response;
    }

    @Override
    public Mono<CustomerResponse> getCustomerById(String customerId) {
        return Mono.fromCallable(() -> getCustomer(customerId))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
