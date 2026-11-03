package kh.edu.istad.platform.order.client;

import kh.edu.istad.platform.order.client.adapter.CustomerServiceClientAdapter;
import kh.edu.istad.platform.order.client.httpClient.CustomerHttpClient;
import kh.edu.istad.platform.order.domain.dto.CustomerResponse;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.ClientRegistrationIdProcessor;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OAuth2RestClientFlowTest {

    private OAuth2AuthorizedClientManager authorizedClientManager;
    private CustomerHttpClient customerHttpClient;
    private CustomerServiceClient customerServiceClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        authorizedClientManager = mock(OAuth2AuthorizedClientManager.class);

        ClientRegistration registration = ClientRegistration.withRegistrationId("customer-client")
                .clientId("customer-client")
                .clientSecret("secret")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri("http://localhost:9000/oauth2/token")
                .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "mock-access-token-xyz",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(registration, "anonymousUser", token);

        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
                .thenReturn(authorizedClient);

        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl("http://customer-service")
                .requestInterceptor(new OAuth2ClientHttpRequestInterceptor(authorizedClientManager));

        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClientBuilder.build()))
                .httpRequestValuesProcessor(ClientRegistrationIdProcessor.DEFAULT_INSTANCE)
                .build();

        customerHttpClient = factory.createClient(CustomerHttpClient.class);
        customerServiceClient = new CustomerServiceClientAdapter(customerHttpClient, WebClient.builder());
    }

    @Test
    @DisplayName("1. Verify @ClientRegistrationId is present on CustomerHttpClient interface and methods")
    void testClientRegistrationIdAnnotationPresent() throws NoSuchMethodException {
        assertThat(CustomerHttpClient.class.isAnnotationPresent(ClientRegistrationId.class)).isTrue();
        assertThat(CustomerHttpClient.class.getAnnotation(ClientRegistrationId.class).value())
                .isEqualTo("customer-client");

        Method getCustomerMethod = CustomerHttpClient.class.getMethod("getCustomer", String.class);
        assertThat(getCustomerMethod.isAnnotationPresent(ClientRegistrationId.class)).isTrue();
        assertThat(getCustomerMethod.getAnnotation(ClientRegistrationId.class).value())
                .isEqualTo("customer-client");
    }

    @Test
    @DisplayName("2. Complete OAuth2 RestClient flow: Token acquired, Bearer attached, response returned")
    void testGetCustomerFlowWithOAuth2Token() {
        String customerId = "c1234567";

        mockServer.expect(requestTo("http://customer-service/api/customers/" + customerId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer mock-access-token-xyz"))
                .andRespond(withSuccess("""
                        {
                            "customerId": "c1234567",
                            "username": "john_doe",
                            "familyName": "Doe",
                            "givenName": "John",
                            "status": "ACTIVE"
                        }
                        """, MediaType.APPLICATION_JSON));

        CustomerResponse response = customerServiceClient.getCustomer(customerId);

        assertThat(response).isNotNull();
        assertThat(response.customerId()).isEqualTo("c1234567");
        assertThat(response.username()).isEqualTo("john_doe");
        assertThat(response.familyName()).isEqualTo("Doe");
        assertThat(response.givenName()).isEqualTo("John");
        assertThat(response.status()).isEqualTo("ACTIVE");

        mockServer.verify();

        // Verify that OAuth2AuthorizedClientManager was queried for "customer-client" via ClientRegistrationIdProcessor
        verify(authorizedClientManager, atLeastOnce()).authorize(argThat(request ->
                "customer-client".equals(request.getClientRegistrationId())
        ));
    }

    @Test
    @DisplayName("3. Verify AuthorizedClientServiceOAuth2AuthorizedClientManager token caching vs new token request")
    void testTokenCachingAndExpiration() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("customer-client")
                .clientId("customer-client")
                .clientSecret("secret")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri("http://localhost:9000/oauth2/token")
                .build();

        ClientRegistrationRepository clientRegistrationRepository =
                new InMemoryClientRegistrationRepository(registration);
        InMemoryOAuth2AuthorizedClientService clientService =
                new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);

        AtomicInteger tokenFetchCounter = new AtomicInteger(0);

        OAuth2AuthorizedClientProvider mockProvider = context -> {
            OAuth2AuthorizedClient existing = context.getAuthorizedClient();
            if (existing != null && existing.getAccessToken().getExpiresAt() != null
                    && existing.getAccessToken().getExpiresAt().isAfter(Instant.now())) {
                // Token is still valid in cache -> reuse it, no new token requested
                return null;
            }
            tokenFetchCounter.incrementAndGet();
            OAuth2AccessToken token = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    "token-number-" + tokenFetchCounter.get(),
                    Instant.now(),
                    Instant.now().plusSeconds(300)
            );
            return new OAuth2AuthorizedClient(context.getClientRegistration(), context.getPrincipal().getName(), token);
        };

        AuthorizedClientServiceOAuth2AuthorizedClientManager realManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, clientService);
        realManager.setAuthorizedClientProvider(mockProvider);

        Authentication principal = new AnonymousAuthenticationToken(
                "anonymousKey", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("customer-client")
                .principal(principal)
                .build();

        // 1st call: Cache empty -> calls provider to fetch token
        OAuth2AuthorizedClient client1 = realManager.authorize(authorizeRequest);
        assertThat(client1).isNotNull();
        assertThat(client1.getAccessToken().getTokenValue()).isEqualTo("token-number-1");
        assertThat(tokenFetchCounter.get()).isEqualTo(1);

        // 2nd call: Token in cache and still valid -> reuse it (counter stays 1)
        OAuth2AuthorizedClient client2 = realManager.authorize(authorizeRequest);
        assertThat(client2).isNotNull();
        assertThat(client2.getAccessToken().getTokenValue()).isEqualTo("token-number-1");
        assertThat(tokenFetchCounter.get()).isEqualTo(1); // Provider NOT called again!

        // Expire the token in cache
        OAuth2AccessToken expiredToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "token-expired",
                Instant.now().minusSeconds(600),
                Instant.now().minusSeconds(300)
        );
        clientService.saveAuthorizedClient(
                new OAuth2AuthorizedClient(registration, principal.getName(), expiredToken),
                principal
        );

        // 3rd call: Token expired -> re-requests token from provider (counter increments to 2)
        OAuth2AuthorizedClient client3 = realManager.authorize(authorizeRequest);
        assertThat(client3).isNotNull();
        assertThat(client3.getAccessToken().getTokenValue()).isEqualTo("token-number-2");
        assertThat(tokenFetchCounter.get()).isEqualTo(2); // Provider called to get fresh token!
    }
}
