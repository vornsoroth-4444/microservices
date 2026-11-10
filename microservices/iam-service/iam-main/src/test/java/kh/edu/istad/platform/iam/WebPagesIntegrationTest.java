package kh.edu.istad.platform.iam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebPagesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
