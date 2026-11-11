package kh.edu.istad.platform.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EntityScan(basePackages = "kh.edu.istad.platform.iam.persistence.entity")
@EnableJpaRepositories(basePackages = "kh.edu.istad.platform.iam.persistence.repository")
public class IamServiceApplication {

    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(IamServiceApplication.class, args);
    }

    private static void loadEnv() {
        Path current = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 5 && current != null; i++) {
            Path envFile = current.resolve(".env");
            if (Files.isRegularFile(envFile)) {
                loadEnvFile(envFile);
            }
            Path subEnv = current.resolve("microservices/iam-service/.env");
            if (Files.isRegularFile(subEnv)) {
                loadEnvFile(subEnv);
            }
            current = current.getParent();
        }
    }

    private static void loadEnvFile(Path envFile) {
        try (BufferedReader reader = Files.newBufferedReader(envFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int equalsIdx = line.indexOf('=');
                if (equalsIdx > 0) {
                    String key = line.substring(0, equalsIdx).trim();
                    String value = line.substring(equalsIdx + 1).trim();
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (System.getProperty(key) == null && System.getenv(key) == null) {
                        System.setProperty(key, value);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
