package kh.edu.istad.platform.order.client;

import kh.edu.istad.platform.order.domain.dto.client.BusinessClientModel;
import kh.edu.istad.platform.order.domain.port.out.BusinessClientPort;
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
public class BusinessClientAdapter implements BusinessClientPort {

    private static final Logger log = LoggerFactory.getLogger(BusinessClientAdapter.class);

    private final RestTemplate directRestTemplate;
    private final RestTemplate loadBalancedRestTemplate;

    @Value("${ecommerce.communication.mode:eureka}")
    private String communicationMode;

    @Value("${ecommerce.services.business-url:http://localhost:9093}")
    private String businessHardcodedUrl;

    public BusinessClientAdapter(
            @Qualifier("directRestTemplate") RestTemplate directRestTemplate,
            @Qualifier("loadBalancedRestTemplate") RestTemplate loadBalancedRestTemplate
    ) {
        this.directRestTemplate = directRestTemplate;
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    @Override
    public Optional<BusinessClientModel> getBusiness(UUID businessId) {
        String url;
        RestTemplate restTemplate;

        if ("hardcoded".equalsIgnoreCase(communicationMode)) {
            // Part 1: Hard-Coded REST Address
            url = businessHardcodedUrl + "/api/businesses/" + businessId;
            restTemplate = directRestTemplate;
            log.info("[HARD-CODED MODE] Calling Business Service at: {}", url);
        } else {
            // Part 3 & 4: Eureka Service Discovery & Load Balancing
            url = "http://business-service/api/businesses/" + businessId;
            restTemplate = loadBalancedRestTemplate;
            log.info("[EUREKA DISCOVERY MODE] Calling Business Service at: {}", url);
        }

        try {
            Map response = restTemplate.getForObject(url, Map.class);
            if (response != null) {
                return Optional.of(BusinessClientModel.builder()
                        .businessId(businessId)
                        .name((String) response.get("name"))
                        .status("ACTIVE")
                        .build());
            }
        } catch (Exception e) {
            log.error("Failed to query business from {}: {}", url, e.getMessage());
        }
        return Optional.empty();
    }
}
