package kh.edu.istad.platform.iam.web.config;

import gg.jte.TemplateEngine;
import gg.jte.springframework.boot.autoconfigure.JteProperties;
import gg.jte.springframework.boot.autoconfigure.JteView;
import gg.jte.springframework.boot.autoconfigure.JteViewResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.servlet.View;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JteConfigTest {

    @Test
    @DisplayName("JTE template engine finds and precompiles all required templates")
    void testTemplateEngineResolvesTemplates() {
        JteConfig config = new JteConfig();
        JteProperties props = new JteProperties();
        props.setTrimControlStructures(true);

        TemplateEngine engine = config.jteTemplateEngine(props);

        assertTrue(engine.hasTemplate("login.jte"), "login.jte should be resolvable");
        assertTrue(engine.hasTemplate("register.jte"), "register.jte should be resolvable");
        assertTrue(engine.hasTemplate("consent.jte"), "consent.jte should be resolvable");
        assertTrue(engine.hasTemplate("layout.jte"), "layout.jte should be resolvable");
        assertTrue(engine.hasTemplate("index.jte"), "index.jte should be resolvable");
        assertTrue(engine.hasTemplate("change-password.jte"), "change-password.jte should be resolvable");
        assertTrue(engine.hasTemplate("connected-apps.jte"), "connected-apps.jte should be resolvable");
        assertTrue(engine.hasTemplate("linked-accounts.jte"), "linked-accounts.jte should be resolvable");
        assertTrue(engine.hasTemplate("profile-edit.jte"), "profile-edit.jte should be resolvable");
    }

    @Test
    @DisplayName("JTE view resolver resolves login view to JteView rather than null")
    void testViewResolverResolvesLogin() throws Exception {
        JteConfig config = new JteConfig();
        JteProperties props = new JteProperties();
        props.setTemplateSuffix(".jte");

        TemplateEngine engine = config.jteTemplateEngine(props);
        JteViewResolver resolver = config.jteViewResolver(engine, props);

        GenericApplicationContext context = new GenericApplicationContext();
        context.refresh();
        resolver.setApplicationContext(context);

        View view = resolver.resolveViewName("login", Locale.ENGLISH);
        assertNotNull(view, "ViewResolver must resolve 'login' to a valid View");
        assertTrue(view instanceof JteView, "Resolved view should be an instance of JteView");
    }
}
