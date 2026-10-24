package kh.edu.istad.platform.order.client;

import kh.edu.istad.platform.order.domain.dto.client.CustomerClientModel;
import kh.edu.istad.platform.order.domain.port.out.CustomerClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerClientAdapter implements CustomerClientPort {

    private static final Logger log = LoggerFactory.getLogger(CustomerClientAdapter.class);

    private final RestTemplate directRestTemplate;
    private final RestTemplate loadBalancedRestTemplate;

    @Value("${ecommerce.communication.mode:eureka}")
    private String communicationMode;

    @Value("${ecommerce.services.customer-url:http://localhost:9091}")
    private String customerHardcodedUrl;

    public CustomerClientAdapter(
            @Qualifier("directRestTemplate") RestTemplate directRestTemplate,
            @Qualifier("loadBalancedRestTemplate") RestTemplate loadBalancedRestTemplate
    ) {
        this.directRestTemplate = directRestTemplate;
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    @Override
    public Optional<CustomerClientModel> getCustomer(UUID customerId) {
        String url;
        RestTemplate restTemplate;

        if ("hardcoded".equalsIgnoreCase(communicationMode)) {
            // Part 1: Hard-Coded REST Address
            url = customerHardcodedUrl + "/api/customers/" + customerId;
            restTemplate = directRestTemplate;
            log.info("[HARD-CODED MODE] Calling Customer Service at: {}", url);
        } else {
            // Part 3 & 4: Eureka Service Discovery & Load Balancing
            url = "http://customer-service/api/customers/" + customerId;
            restTemplate = loadBalancedRestTemplate;
            log.info("[EUREKA DISCOVERY MODE] Calling Customer Service at: {}", url);
        }

        try {
            Map response = restTemplate.getForObject(url, Map.class);
            if (response != null) {
                return Optional.of(CustomerClientModel.builder()
                        .customerId(customerId)
                        .username((String) response.get("username"))
                        .givenName((String) response.get("givenName"))
                        .familyName((String) response.get("familyName"))
                        .status((String) response.get("status"))
                        .build());
            }
        } catch (Exception e) {
            log.error("Failed to query customer from {}: {}", url, e.getMessage());
        }
        return Optional.empty();
    }
}
