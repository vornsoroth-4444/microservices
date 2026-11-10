package kh.edu.istad.platform.iam.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

@Configuration
@EnableMethodSecurity
public class ApiSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Public auth endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()

                        // /api/me endpoints
                        .requestMatchers("/api/me/**").authenticated()

                        // User endpoints
                        .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("user:create")
                        .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/*").hasAuthority("user:read")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*").hasAuthority("user:update")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasAuthority("user:delete")
                        .requestMatchers(HttpMethod.POST, "/api/users/*/enable").hasAuthority("user:update")
                        .requestMatchers(HttpMethod.POST, "/api/users/*/disable").hasAuthority("user:update")
                        .requestMatchers(HttpMethod.POST, "/api/users/*/lock").hasAuthority("user:update")
                        .requestMatchers(HttpMethod.POST, "/api/users/*/unlock").hasAuthority("user:update")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/password", "/api/users/*/reset-password").hasAuthority("user:update")
                        .requestMatchers(HttpMethod.POST, "/api/users/*/reset-password").hasAuthority("user:update")
                        .requestMatchers(HttpMethod.GET, "/api/users/*/authorities").hasAuthority("user:read")
                        .requestMatchers(HttpMethod.POST, "/api/users/*/roles").hasAnyAuthority("user:assign", "user:update")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*/roles/*").hasAnyAuthority("user:assign", "user:update")
                        .requestMatchers(HttpMethod.POST, "/api/users/*/permissions").hasAnyAuthority("user:assign", "user:update")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*/permissions/*").hasAnyAuthority("user:assign", "user:update")
                        .requestMatchers(HttpMethod.GET, "/api/users/*/identities").hasAuthority("user:read")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*/identities/*").hasAuthority("user:update")

                        // Role endpoints
                        .requestMatchers(HttpMethod.POST, "/api/roles").hasAuthority("role:create")
                        .requestMatchers(HttpMethod.GET, "/api/roles", "/api/roles/*").hasAuthority("role:read")
                        .requestMatchers(HttpMethod.PATCH, "/api/roles/*").hasAuthority("role:update")
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/*").hasAuthority("role:delete")
                        .requestMatchers(HttpMethod.POST, "/api/roles/*/activate").hasAuthority("role:update")
                        .requestMatchers(HttpMethod.POST, "/api/roles/*/deactivate").hasAuthority("role:update")
                        .requestMatchers(HttpMethod.POST, "/api/roles/*/permissions").hasAnyAuthority("role:assign", "role:update")
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/*/permissions/*").hasAnyAuthority("role:assign", "role:update")

                        // Permission endpoints
                        .requestMatchers(HttpMethod.POST, "/api/permissions").hasAuthority("permission:create")
                        .requestMatchers(HttpMethod.GET, "/api/permissions", "/api/permissions/*").hasAuthority("permission:read")
                        .requestMatchers(HttpMethod.PATCH, "/api/permissions/*").hasAuthority("permission:update")
                        .requestMatchers(HttpMethod.DELETE, "/api/permissions/*").hasAuthority("permission:delete")
                        .requestMatchers(HttpMethod.POST, "/api/permissions/*/activate").hasAuthority("permission:update")
                        .requestMatchers(HttpMethod.POST, "/api/permissions/*/deactivate").hasAuthority("permission:update")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Permissions
            List<String> permissions = jwt.getClaimAsStringList("permissions");
            if (permissions != null) {
                permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
            }

            // Roles
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                roles.forEach(r -> authorities.add(new SimpleGrantedAuthority(r.startsWith("ROLE_") ? r : "ROLE_" + r)));
            }

            // Scope / scp fallback
            List<String> scopes = jwt.getClaimAsStringList("scope");
            if (scopes != null) {
                scopes.forEach(s -> {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + s));
                    authorities.add(new SimpleGrantedAuthority(s));
                });
            } else {
                String scopeStr = jwt.getClaimAsString("scope");
                if (scopeStr != null) {
                    for (String s : scopeStr.split(" ")) {
                        if (!s.isBlank()) {
                            authorities.add(new SimpleGrantedAuthority("SCOPE_" + s));
                            authorities.add(new SimpleGrantedAuthority(s));
                        }
                    }
                }
            }

            return authorities;
        });
        return converter;
    }
}
