package kh.edu.istad.platform.order.client.adapter;

import kh.edu.istad.platform.order.domain.dto.GetCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerServiceClientAdapter implements CustomerServiceClient {
    private final WebClient.Builder webClientBuilder;
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
}
