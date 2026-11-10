package kh.edu.istad.platform.iam.web.config;

import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import gg.jte.resolve.ResourceCodeResolver;
import gg.jte.springframework.boot.autoconfigure.JteProperties;
import gg.jte.springframework.boot.autoconfigure.JteViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(JteProperties.class)
public class JteConfig {

    private static final Logger log = LoggerFactory.getLogger(JteConfig.class);

    @Bean
    @Primary
    public TemplateEngine jteTemplateEngine(JteProperties jteProperties) {
        ClassLoader classLoader = getClass().getClassLoader();
        List<CodeResolver> resolvers = new ArrayList<>();

        // 1. Gather all potential directory locations on disk (for development & live reload)
        Set<Path> candidates = new LinkedHashSet<>();
        if (jteProperties.getTemplateLocation() != null && !jteProperties.getTemplateLocation().isBlank()) {
            candidates.add(Paths.get(jteProperties.getTemplateLocation()).toAbsolutePath().normalize());
        }

        String userDir = System.getProperty("user.dir", ".");
        String[] relativeSearchPaths = {
                "src/main/jte",
                "iam-web/src/main/jte",
                "iam-main/src/main/jte",
                "microservices/iam-service/iam-web/src/main/jte",
                "microservices/iam-service/iam-main/src/main/jte"
        };

        for (String rel : relativeSearchPaths) {
            candidates.add(Paths.get(rel).toAbsolutePath().normalize());
            candidates.add(Paths.get(userDir, rel).toAbsolutePath().normalize());
        }

        for (Path path : candidates) {
            if (Files.isDirectory(path)) {
                log.info("Found JTE template directory: {}", path);
                resolvers.add(new DirectoryCodeResolver(path));
            }
        }

        // 2. Classpath resource resolvers (for packaged JAR runtime and fallback)
        resolvers.add(new ResourceCodeResolver("", classLoader));
        resolvers.add(new ResourceCodeResolver("jte", classLoader));

        CompositeCodeResolver compositeResolver = new CompositeCodeResolver(resolvers);

        Path classDir = Paths.get("jte-classes");
        try {
            Files.createDirectories(classDir);
        } catch (IOException e) {
            log.warn("Could not create jte-classes directory: {}", e.getMessage());
        }

        TemplateEngine templateEngine = TemplateEngine.create(
                compositeResolver,
                classDir,
                ContentType.Html,
                classLoader
        );
        templateEngine.setTrimControlStructures(jteProperties.isTrimControlStructures());
        return templateEngine;
    }

    @Bean
    @Primary
    public JteViewResolver jteViewResolver(TemplateEngine templateEngine, JteProperties jteProperties) {
        JteViewResolver resolver = new JteViewResolver(templateEngine, jteProperties);
        resolver.setOrder(Ordered.HIGHEST_PRECEDENCE + 30);
        return resolver;
    }
}
