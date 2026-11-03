package kh.edu.istad.platform.order.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import kh.edu.istad.platform.order.client.adapter.CustomerServiceClientAdapter;
import kh.edu.istad.platform.order.client.config.ClientConfig;
import kh.edu.istad.platform.order.client.config.OAuth2ClientConfig;
import kh.edu.istad.platform.order.client.config.RestClientConfig;
import kh.edu.istad.platform.order.client.httpClient.CustomerHttpClient;
import kh.edu.istad.platform.order.domain.dto.CustomerResponse;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RealHttpEndToEndOAuth2Test.EndToEndConfig.class)
class RealHttpEndToEndOAuth2Test {

    private static HttpServer authServer;
    private static HttpServer customerServiceServer;
    private static int authServerPort;
    private static int customerServicePort;

    private static final AtomicInteger tokenRequestCount = new AtomicInteger(0);
    private static final AtomicInteger customerRequestCount = new AtomicInteger(0);
    private static volatile String lastReceivedBearerToken = null;
    private static volatile String lastReceivedBasicAuth = null;

    @BeforeAll
    static void startServers() throws IOException {
        // 1. Start Mock Authorization Server on an open ephemeral port
        authServer = HttpServer.create(new InetSocketAddress(0), 0);
        authServerPort = authServer.getAddress().getPort();

        authServer.createContext("/oauth2/token", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    tokenRequestCount.incrementAndGet();
                    lastReceivedBasicAuth = exchange.getRequestHeaders().getFirst("Authorization");

                    byte[] requestBody = exchange.getRequestBody().readAllBytes();
                    String bodyString = new String(requestBody, StandardCharsets.UTF_8);

                    String responseJson = """
                            {
                                "access_token": "live-generated-access-token-9999",
                                "token_type": "Bearer",
                                "expires_in": 3600,
                                "scope": "read write"
                            }
                            """;

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }
        });
        authServer.start();

        // 2. Start Mock Customer Service on an open ephemeral port
        customerServiceServer = HttpServer.create(new InetSocketAddress(0), 0);
        customerServicePort = customerServiceServer.getAddress().getPort();

        customerServiceServer.createContext("/api/customers/cust-999", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                customerRequestCount.incrementAndGet();
                lastReceivedBearerToken = exchange.getRequestHeaders().getFirst("Authorization");

                String responseJson = """
                        {
                            "customerId": "cust-999",
                            "username": "live_user",
                            "familyName": "Sok",
                            "givenName": "Dara",
                            "status": "ACTIVE"
                        }
                        """;

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
        });
        customerServiceServer.start();
    }

    @AfterAll
    static void stopServers() {
        if (authServer != null) {
            authServer.stop(0);
        }
        if (customerServiceServer != null) {
            customerServiceServer.stop(0);
        }
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        // Point LoadBalancer service "customer-service" to ephemeral localhost port
        registry.add("spring.cloud.discovery.client.simple.instances.customer-service[0].uri",
                () -> "http://localhost:" + customerServicePort);
        registry.add("service.customer.base-url", () -> "http://customer-service");
        registry.add("service.business.base-url", () -> "http://customer-service");

        // Real application.yaml properties for OAuth2 client
        registry.add("spring.security.oauth2.client.registration.customer-client.client-id", () -> "customer-client");
        registry.add("spring.security.oauth2.client.registration.customer-client.client-secret", () -> "secret");
        registry.add("spring.security.oauth2.client.registration.customer-client.authorization-grant-type", () -> "client_credentials");
        registry.add("spring.security.oauth2.client.registration.customer-client.client-authentication-method", () -> "client_secret_basic");
        registry.add("spring.security.oauth2.client.registration.customer-client.scope", () -> "read,write");
        registry.add("spring.security.oauth2.client.provider.customer-client.token-uri",
                () -> "http://localhost:" + authServerPort + "/oauth2/token");
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({ClientConfig.class, OAuth2ClientConfig.class, RestClientConfig.class, CustomerServiceClientAdapter.class})
    static class EndToEndConfig {
    }

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Autowired
    private CustomerHttpClient customerHttpClient;

    @Test
    @DisplayName("End-to-end 100% real HTTP flow: Client credentials requested, token cached & reused, Bearer attached")
    void testFullEndToEndLiveFlow() {
        // --- CALL 1: First request (Token not in cache) ---
        CustomerResponse customer1 = customerServiceClient.getCustomer("cust-999");

        assertThat(customer1).isNotNull();
        assertThat(customer1.customerId()).isEqualTo("cust-999");
        assertThat(customer1.username()).isEqualTo("live_user");
        assertThat(customer1.familyName()).isEqualTo("Sok");
        assertThat(customer1.givenName()).isEqualTo("Dara");
        assertThat(customer1.status()).isEqualTo("ACTIVE");

        // 1. Verify Authorization server received the token request via Basic Auth
        assertThat(tokenRequestCount.get()).isEqualTo(1);
        assertThat(lastReceivedBasicAuth).isNotNull();
        assertThat(lastReceivedBasicAuth).startsWith("Basic ");

        // 2. Verify Customer Service received the Bearer token issued by Authorization server
        assertThat(customerRequestCount.get()).isEqualTo(1);
        assertThat(lastReceivedBearerToken).isEqualTo("Bearer live-generated-access-token-9999");

        // --- CALL 2: Second request (Token IS in cache and still valid) ---
        CustomerResponse customer2 = customerServiceClient.getCustomer("cust-999");

        assertThat(customer2).isNotNull();
        assertThat(customer2.customerId()).isEqualTo("cust-999");

        // 3. Verify token was REUSED from cache: tokenRequestCount MUST STILL BE 1!
        assertThat(tokenRequestCount.get()).isEqualTo(1);

        // 4. Customer service was called a 2nd time
        assertThat(customerRequestCount.get()).isEqualTo(2);
        assertThat(lastReceivedBearerToken).isEqualTo("Bearer live-generated-access-token-9999");
    }
}
