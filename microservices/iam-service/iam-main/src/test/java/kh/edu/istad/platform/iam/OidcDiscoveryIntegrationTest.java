package kh.edu.istad.platform.iam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OidcDiscoveryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /.well-known/openid-configuration returns standard OIDC metadata")
    void testOpenIdConfigurationEndpoint() throws Exception {
        mockMvc.perform(get("/.well-known/openid-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuer").value("http://localhost:9000"))
                .andExpect(jsonPath("$.authorization_endpoint").value("http://localhost:9000/oauth2/authorize"))
                .andExpect(jsonPath("$.token_endpoint").value("http://localhost:9000/oauth2/token"))
                .andExpect(jsonPath("$.jwks_uri").value("http://localhost:9000/oauth2/jwks"))
                .andExpect(jsonPath("$.response_types_supported").isArray())
                .andExpect(jsonPath("$.subject_types_supported").isArray())
                .andExpect(jsonPath("$.id_token_signing_alg_values_supported").isArray());
    }

    @Test
    @DisplayName("GET /oauth2/jwks returns RSA JWK keys")
    void testJwksEndpoint() throws Exception {
        mockMvc.perform(get("/oauth2/jwks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keys").isArray())
                .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
                .andExpect(jsonPath("$.keys[0].kid").isNotEmpty());
    }
}
