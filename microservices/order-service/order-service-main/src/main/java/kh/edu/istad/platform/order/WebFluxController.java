package kh.edu.istad.platform.order;

import kh.edu.istad.platform.order.config.ServicePropConfig;
import kh.edu.istad.platform.order.domain.dto.CustomerResponse;
import kh.edu.istad.platform.order.domain.dto.GetCustomerResult;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import kh.edu.istad.platform.order.domain.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Map;

@RefreshScope
@RestController
public class WebFluxController {
    private final CustomerServiceClient customerServiceClient;
    private final OrderService orderService;
    private final ServicePropConfig servicePropConfig;

    public WebFluxController(CustomerServiceClient customerServiceClient,
                             OrderService orderService,
                             ServicePropConfig servicePropConfig) {
        this.customerServiceClient = customerServiceClient;
        this.orderService = orderService;
        this.servicePropConfig = servicePropConfig;
    }

    @GetMapping("/customers")
    public Flux<GetCustomerResult> getCustomer() {
        return customerServiceClient.getCustomerNames();
    }

    @GetMapping("/customers/query")
    public Flux<QueryCustomerResult> queryCustomer() {
        return customerServiceClient.queryCustomers();
    }

    @GetMapping("/customers/{customerId}")
    public Mono<CustomerResponse> getCustomerById(@PathVariable String customerId) {
        return Mono.fromCallable(() -> orderService.getCustomer(customerId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/Flux")
    public Flux<String> getFluxData() {
        return Flux.just("A", "B", "C", "D", "E", "F")
                .log()
                .delayElements(Duration.ofSeconds(1));
    }

    @Value("${global.version}")
    private String globalVersion;

    @GetMapping("/consul/config")
    public Mono<Map<String, Object>> getConsulConfig() {
        return Mono.just(
                Map.of(
                        "global", globalVersion,
                        "message", servicePropConfig.getMessage(),
                        "version", servicePropConfig.getVersion(),
                        "env", servicePropConfig.getEnv(),
                        "baseUrl", servicePropConfig.getBaseUrl()
                )
        );
    }
}
