package kh.edu.istad.platform.iam;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DynamicClientRegistrationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("RFC 7591 Dynamic Client Registration full lifecycle")
    void testDynamicClientRegistrationLifecycle() throws Exception {
        // 1. Obtain token with client.create scope
        MvcResult tokenResult = mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic("iam-client", "iam-secret"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("scope", "client.create"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = tokenResult.getResponse().getContentAsString();
        String accessToken = JsonPath.read(responseBody, "$.access_token");

        // 2. Register new OAuth2 client
        String clientPayload = """
                {
                    "client_name": "Integration Test App",
                    "redirect_uris": ["https://oauth.pstmn.io/v1/callback"],
                    "grant_types": ["authorization_code", "refresh_token"],
                    "response_types": ["code"],
                    "scope": "openid profile email"
                }
                """;

        MvcResult regResult = mockMvc.perform(post("/connect/register")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.client_id").isNotEmpty())
                .andExpect(jsonPath("$.client_secret").isNotEmpty())
                .andExpect(jsonPath("$.registration_access_token").isNotEmpty())
                .andReturn();

        String regResponseBody = regResult.getResponse().getContentAsString();
        String clientId = JsonPath.read(regResponseBody, "$.client_id");
        String registrationAccessToken = JsonPath.read(regResponseBody, "$.registration_access_token");

        // 3. Read registered client metadata
        mockMvc.perform(get("/connect/register")
                        .param("client_id", clientId)
                        .header("Authorization", "Bearer " + registrationAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client_id").value(clientId))
                .andExpect(jsonPath("$.client_name").value("Integration Test App"))
                .andExpect(jsonPath("$.redirect_uris[0]").value("https://oauth.pstmn.io/v1/callback"));
    }
}
