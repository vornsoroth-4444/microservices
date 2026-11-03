package kh.edu.istad.platform.springauthorizationserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthorizationServerIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private final JsonMapper jsonMapper = new JsonMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("1. Public Home endpoint should return UP status")
    void testPublicHomeEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Spring Authorization Server is running"));
    }

    @Test
    @DisplayName("2. OIDC discovery configuration endpoint should return valid JSON")
    void testOpenIdConfigurationEndpoint() throws Exception {
        mockMvc.perform(get("/.well-known/openid-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuer").exists())
                .andExpect(jsonPath("$.token_endpoint").exists())
                .andExpect(jsonPath("$.jwks_uri").exists());
    }

    @Test
    @DisplayName("3. JWKS endpoint should return RSA public key")
    void testJwksEndpoint() throws Exception {
        mockMvc.perform(get("/oauth2/jwks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keys").isArray())
                .andExpect(jsonPath("$.keys[0].kty").value("RSA"));
    }

    @Test
    @DisplayName("4. Calling protected /api/customers without token should return 401 Unauthorized")
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("5. Obtain OAuth2 token using client_credentials grant and access /api/customers")
    void testClientCredentialsTokenAndAccessProtectedApi() throws Exception {
        // Step A: Request Token via OAuth2 Client Credentials grant
        MvcResult tokenResult = mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic("oidc-client", "secret"))
                        .param("grant_type", "client_credentials")
                        .param("scope", "read write"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andReturn();

        JsonNode responseNode = jsonMapper.readTree(tokenResult.getResponse().getContentAsString());
        String accessToken = responseNode.get("access_token").asText();

        // Step B: Call Protected GET /api/customers with Bearer Token
        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Step C: Call Protected POST /api/customers with Bearer Token
        String customerPayload = """
                {
                    "name": "Integration Test User",
                    "email": "test_%s@example.com",
                    "phoneNumber": "012345678"
                }
                """.formatted(System.currentTimeMillis());

        mockMvc.perform(post("/api/customers")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Customer created successfully!"))
                .andExpect(jsonPath("$.authenticated_identity").value("oidc-client"));
    }
}
