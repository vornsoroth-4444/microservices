package kh.edu.istad.platform.iam;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class IamServiceApplicationTests {

    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
        assertTrue(true);
    }
}
