package kh.edu.istad.platform.iam.infrastructure.security;

import kh.edu.istad.platform.iam.application.dto.AuthUserResult;
import kh.edu.istad.platform.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.platform.iam.domain.model.Address;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.*;

@Configuration
public class TokenCustomizerConfig {

    private final LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase;

    public TokenCustomizerConfig(LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase) {
        this.loadUserForAuthenticationUseCase = loadUserForAuthenticationUseCase;
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            Authentication principal = context.getPrincipal();
            if (principal == null) return;

            User user = null;
            List<String> roleNames = new ArrayList<>();
            Set<String> effectivePermissions = new LinkedHashSet<>();

            if (principal.getPrincipal() instanceof SecurityUser securityUser) {
                user = securityUser.getUser();
                roleNames = securityUser.getRoles().stream().map(Role::getName).toList();
                effectivePermissions = securityUser.getPermissions();
            } else if (principal.getName() != null) {
                try {
                    AuthUserResult authResult = loadUserForAuthenticationUseCase.execute(principal.getName());
                    user = authResult.getUser();
                    roleNames = authResult.getRoles().stream().map(Role::getName).toList();
                    effectivePermissions = authResult.getPermissions();
                } catch (Exception ignored) {
                }
            }

            if (user == null) return;

            // Customize Access Token
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getClaims().claim("sub", user.getUuid().toString());
                context.getClaims().claim("preferred_username", user.getUsername());
                context.getClaims().claim("roles", roleNames);
                context.getClaims().claim("permissions", effectivePermissions);
            }

            // Customize ID Token
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                context.getClaims().claim("sub", user.getUuid().toString());

                Set<String> scopes = context.getAuthorizedScopes();

                if (scopes.contains(OidcScopes.PROFILE)) {
                    context.getClaims().claim("name", user.getFullName());
                    if (user.getGivenName() != null) context.getClaims().claim("given_name", user.getGivenName());
                    if (user.getFamilyName() != null) context.getClaims().claim("family_name", user.getFamilyName());
                    if (user.getMiddleName() != null) context.getClaims().claim("middle_name", user.getMiddleName());
                    if (user.getNickname() != null) context.getClaims().claim("nickname", user.getNickname());
                    context.getClaims().claim("preferred_username", user.getUsername());
                    if (user.getProfileUrl() != null) context.getClaims().claim("profile", user.getProfileUrl());
                    if (user.getPictureUrl() != null) context.getClaims().claim("picture", user.getPictureUrl());
                    if (user.getWebsiteUrl() != null) context.getClaims().claim("website", user.getWebsiteUrl());
                    if (user.getGender() != null) context.getClaims().claim("gender", user.getGender());
                    if (user.getBirthdate() != null) context.getClaims().claim("birthdate", user.getBirthdate().toString());
                    if (user.getZoneinfo() != null) context.getClaims().claim("zoneinfo", user.getZoneinfo());
                    if (user.getLocale() != null) context.getClaims().claim("locale", user.getLocale());
                    if (user.getUpdatedAt() != null) context.getClaims().claim("updated_at", user.getUpdatedAt().getEpochSecond());
                }

                if (scopes.contains(OidcScopes.EMAIL)) {
                    context.getClaims().claim("email", user.getEmail());
                    context.getClaims().claim("email_verified", user.isEmailVerified());
                }

                if (scopes.contains(OidcScopes.PHONE)) {
                    if (user.getPhoneNumber() != null) {
                        context.getClaims().claim("phone_number", user.getPhoneNumber());
                        context.getClaims().claim("phone_number_verified", user.isPhoneNumberVerified());
                    }
                }

                if (scopes.contains(OidcScopes.ADDRESS)) {
                    Address addr = user.getAddress();
                    if (addr != null) {
                        Map<String, String> addressClaim = new LinkedHashMap<>();
                        if (addr.getStreetAddress() != null) addressClaim.put("street_address", addr.getStreetAddress());
                        if (addr.getLocality() != null) addressClaim.put("locality", addr.getLocality());
                        if (addr.getRegion() != null) addressClaim.put("region", addr.getRegion());
                        if (addr.getPostalCode() != null) addressClaim.put("postal_code", addr.getPostalCode());
                        if (addr.getCountry() != null) addressClaim.put("country", addr.getCountry());
                        context.getClaims().claim("address", addressClaim);
                    }
                }
            }
        };
    }
}
