package kh.edu.istad.platform.iam;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Shared base class for all integration tests.
 * Starts a single PostgreSQL container (reused across all tests in the same JVM run).
 * Uses the Singleton Container pattern so the container is not stopped between test classes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("db_oauth2")
                .withUsername("iteuser")
                .withPassword("qwerqwer");
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        // Provide dummy social-login credentials so context starts without real OAuth2 providers
        registry.add("spring.security.oauth2.client.registration.google.client-id", () -> "test-google-id");
        registry.add("spring.security.oauth2.client.registration.google.client-secret", () -> "test-google-secret");
        registry.add("spring.security.oauth2.client.registration.github.client-id", () -> "test-github-id");
        registry.add("spring.security.oauth2.client.registration.github.client-secret", () -> "test-github-secret");
        registry.add("spring.security.oauth2.client.registration.facebook.client-id", () -> "test-facebook-id");
        registry.add("spring.security.oauth2.client.registration.facebook.client-secret", () -> "test-facebook-secret");
    }
}
