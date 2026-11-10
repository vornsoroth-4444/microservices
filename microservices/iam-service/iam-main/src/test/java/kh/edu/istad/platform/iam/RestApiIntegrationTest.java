package kh.edu.istad.platform.iam;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/auth/register registers new user")
    void testRegisterUserApi() throws Exception {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        String registerJson = """
                {
                    "username": "testuser_%s",
                    "email": "testuser_%s@istad.co",
                    "password": "Password123!",
                    "confirmPassword": "Password123!",
                    "givenName": "Test",
                    "familyName": "User",
                    "locale": "en"
                }
                """.formatted(randomSuffix, randomSuffix);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser_" + randomSuffix))
                .andExpect(jsonPath("$.email").value("testuser_" + randomSuffix + "@istad.co"));
    }

    @Test
    @DisplayName("GET /api/users without token returns 401 Unauthorized")
    void testGetUsersUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/users with valid bearer token returns paginated user list")
    void testGetUsersAuthorized() throws Exception {
        // 1. Obtain token with user:read scope
        MvcResult tokenResult = mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic("iam-client", "iam-secret"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("scope", "user:read role:read permission:read"))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = JsonPath.read(tokenResult.getResponse().getContentAsString(), "$.access_token");

        // 2. Query users list
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page.totalElements").isNumber());

        // 3. Query roles list
        mockMvc.perform(get("/api/roles")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        // 4. Query permissions list
        mockMvc.perform(get("/api/permissions")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
