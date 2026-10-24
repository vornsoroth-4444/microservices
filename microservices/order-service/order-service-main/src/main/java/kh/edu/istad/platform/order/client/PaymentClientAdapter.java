package kh.edu.istad.platform.order.client;

import kh.edu.istad.platform.order.domain.dto.client.PaymentClientModel;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.port.out.PaymentClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class PaymentClientAdapter implements PaymentClientPort {

    private static final Logger log = LoggerFactory.getLogger(PaymentClientAdapter.class);

    private final RestTemplate directRestTemplate;
    private final RestTemplate loadBalancedRestTemplate;

    @Value("${ecommerce.communication.mode:eureka}")
    private String communicationMode;

    @Value("${ecommerce.services.payment-url:http://localhost:9094}")
    private String paymentHardcodedUrl;

    public PaymentClientAdapter(
            @Qualifier("directRestTemplate") RestTemplate directRestTemplate,
            @Qualifier("loadBalancedRestTemplate") RestTemplate loadBalancedRestTemplate
    ) {
        this.directRestTemplate = directRestTemplate;
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    @Override
    public PaymentClientModel processPayment(UUID customerId, UUID orderId, BigDecimal amount) {
        String url;
        RestTemplate restTemplate;

        if ("hardcoded".equalsIgnoreCase(communicationMode)) {
            // Part 1: Hard-Coded REST Address
            url = paymentHardcodedUrl + "/api/payments";
            restTemplate = directRestTemplate;
            log.info("[HARD-CODED MODE] Calling Payment Service at: {}", url);
        } else {
            // Part 3 & 4: Eureka Service Discovery & Load Balancing
            url = "http://payment-service/api/payments";
            restTemplate = loadBalancedRestTemplate;
            log.info("[EUREKA DISCOVERY MODE] Calling Payment Service at: {}", url);
        }

        Map<String, Object> request = new HashMap<>();
        request.put("customerId", customerId);
        request.put("orderId", orderId);
        request.put("price", amount);
        request.put("paymentMethod", "CREDIT_CARD");

        try {
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, request, Map.class);
            Map body = responseEntity.getBody();
            if (body != null) {
                String paymentIdStr = (String) body.get("paymentId");
                return PaymentClientModel.builder()
                        .paymentId(paymentIdStr != null ? UUID.fromString(paymentIdStr) : UUID.randomUUID())
                        .orderId(orderId)
                        .customerId(customerId)
                        .price(amount)
                        .status("COMPLETED")
                        .message("Payment processed successfully")
                        .build();
            }
        } catch (Exception e) {
            log.error("Payment processing failed at {}: {}", url, e.getMessage());
            throw new OrderDomainException("Payment processing failed: " + e.getMessage());
        }

        throw new OrderDomainException("Failed to receive payment response from Payment Service");
    }
}
