package kh.edu.istad.platform.iam.web.config;

import gg.jte.CodeResolver;
import gg.jte.TemplateNotFoundException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CompositeCodeResolver implements CodeResolver {

    private final List<CodeResolver> resolvers;

    public CompositeCodeResolver(List<CodeResolver> resolvers) {
        this.resolvers = resolvers != null ? List.copyOf(resolvers) : List.of();
    }

    @Override
    public String resolve(String name) {
        String targetName = normalize(name);
        for (CodeResolver resolver : resolvers) {
            try {
                if (resolver.exists(targetName)) {
                    String code = resolver.resolve(targetName);
                    if (code != null) {
                        return code;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @Override
    public String resolveRequired(String name) throws TemplateNotFoundException {
        String code = resolve(name);
        if (code == null) {
            throw new TemplateNotFoundException(name + " not found in any registered code resolver");
        }
        return code;
    }

    @Override
    public boolean exists(String name) {
        String targetName = normalize(name);
        for (CodeResolver resolver : resolvers) {
            try {
                if (resolver.exists(targetName)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @Override
    public long getLastModified(String name) {
        String targetName = normalize(name);
        for (CodeResolver resolver : resolvers) {
            try {
                if (resolver.exists(targetName)) {
                    return resolver.getLastModified(targetName);
                }
            } catch (Exception ignored) {
            }
        }
        return 0L;
    }

    @Override
    public List<String> resolveAllTemplateNames() {
        Set<String> all = new LinkedHashSet<>();
        for (CodeResolver resolver : resolvers) {
            try {
                List<String> names = resolver.resolveAllTemplateNames();
                if (names != null) {
                    all.addAll(names);
                }
            } catch (Exception ignored) {
            }
        }
        return new ArrayList<>(all);
    }

    private String normalize(String name) {
        if (name == null) {
            return null;
        }
        return name.startsWith("/") ? name.substring(1) : name;
    }
}
