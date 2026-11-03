package kh.edu.istad.platform.order.client.config;

import kh.edu.istad.platform.order.client.exception.DownStreamServiceException;
import kh.edu.istad.platform.order.client.httpClient.BusinessHttpClient;
import kh.edu.istad.platform.order.client.httpClient.CustomerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.HttpServiceGroup;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(
        group = "customer-service",
        types = {CustomerHttpClient.class},
        clientType = HttpServiceGroup.ClientType.REST_CLIENT
)
@ImportHttpServices(
        group = "business-service",
        types = {BusinessHttpClient.class},
        clientType = HttpServiceGroup.ClientType.REST_CLIENT
)
public class ClientConfig {

    @Value("${service.customer.base-url:http://customer-service}")
    private String customerServiceBaseUrl;

    @Value("${service.business.base-url:http://business-service}")
    private String businessServiceBaseUrl;

    @Bean
    public RestClientHttpServiceGroupConfigurer restClientHttpServiceGroupConfigurer() {
        return groups -> {
            groups.filterByName("customer-service")
                    .forEachClient((group, clientBuilder) ->
                            clientBuilder
                                    .baseUrl(customerServiceBaseUrl)
                                    .defaultHeader("User-Agent", "OrderService-RestClient")
                                    .defaultStatusHandler(
                                            HttpStatusCode::isError,
                                            (request, response) -> {
                                                throw new DownStreamServiceException(
                                                        "Customer Service",
                                                        response.getStatusCode() + ": " + response.getStatusText()
                                                );
                                            }
                                    )
                    );

            groups.filterByName("business-service")
                    .forEachClient((group, clientBuilder) ->
                            clientBuilder
                                    .baseUrl(businessServiceBaseUrl)
                                    .defaultHeader("User-Agent", "OrderService-RestClient")
                    );
        };
    }
}
