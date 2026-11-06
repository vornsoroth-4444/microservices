package kh.edu.istad.platform.order.client.adapter;

import kh.edu.istad.platform.order.client.grpc.CustomerGrpcClient;
import kh.edu.istad.platform.order.client.httpClient.CustomerHttpClient;
import kh.edu.istad.platform.order.domain.dto.GetCustomerResult;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class CustomerServiceClientAdapter implements CustomerServiceClient {
    private final WebClient.Builder webClientBuilder;
    private final CustomerHttpClient customerHttpClient;
    private final CustomerGrpcClient customerGrpcClient;
    public record GetCustomer(
            String customerId,
            String username,
            String familyName,
            String givenName,
            String status
    ){

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
        return customerHttpClient.queryCustomer();
    }

    @Override
    public Flux<GetCustomerResult> listCustomersGrpc() {
        return customerGrpcClient.listCustomers();
    }

    @Override
    public Mono<GetCustomerResult> getCustomerGrpc(String id) {
        return customerGrpcClient.getCustomer(id);
    }
}
