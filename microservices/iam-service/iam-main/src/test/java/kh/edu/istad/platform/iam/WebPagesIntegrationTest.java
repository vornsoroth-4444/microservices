package kh.edu.istad.platform.iam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebPagesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / unauthenticated redirects to login")
    void testHomeUnauthenticatedRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("GET /profile unauthenticated redirects to login")
    void testProfileUnauthenticatedRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("GET / authenticated with admin renders profile overview")
    void testHomeAuthenticatedWithAdmin() throws Exception {
        mockMvc.perform(get("/").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("admin")))
                .andExpect(content().string(containsString("Overview")));
    }

    @Test
    @DisplayName("GET /profile authenticated with admin renders profile overview")
    void testProfileAuthenticatedWithAdmin() throws Exception {
        mockMvc.perform(get("/profile").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("admin")))
                .andExpect(content().string(containsString("Overview")));
    }

    @Test
    @DisplayName("GET /login in English renders English sign in page")
    void testLoginPageEnglish() throws Exception {
        mockMvc.perform(get("/login").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sign in")));
    }

    @Test
    @DisplayName("GET /login in Khmer renders Khmer sign in page")
    void testLoginPageKhmer() throws Exception {
        mockMvc.perform(get("/login").param("lang", "km"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ចូលគណនី")));
    }

    @Test
    @DisplayName("GET /register in English renders English register page")
    void testRegisterPageEnglish() throws Exception {
        mockMvc.perform(get("/register").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create an account")));
    }

    @Test
    @DisplayName("GET /register in Khmer renders Khmer register page")
    void testRegisterPageKhmer() throws Exception {
        mockMvc.perform(get("/register").param("lang", "km"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("បង្កើតគណនី")));
    }
}
