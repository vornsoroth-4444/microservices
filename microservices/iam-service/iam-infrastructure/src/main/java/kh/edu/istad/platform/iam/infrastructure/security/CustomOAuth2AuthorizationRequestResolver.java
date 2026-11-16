package kh.edu.istad.platform.iam.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                "/oauth2/authorization"
        );
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        return customize(request, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return customize(request, authorizationRequest);
    }

    private OAuth2AuthorizationRequest customize(HttpServletRequest request, OAuth2AuthorizationRequest req) {
        if (req == null) return null;

        Map<String, Object> additionalParameters = new HashMap<>(req.getAdditionalParameters());
        String authUri = req.getAuthorizationUri();

        // 1. For Google: force prompt=select_account so the Google Account Chooser screen always displays
        if (authUri != null && authUri.contains("accounts.google.com")) {
            additionalParameters.put("prompt", "select_account");
            additionalParameters.put("access_type", "offline");
        } else if (authUri != null && authUri.contains("github.com")) {
            // For GitHub: prompt=consent forces re-consent
            additionalParameters.put("prompt", "consent");
        }

        // Allow explicit query parameter override (?prompt=...)
        String queryPrompt = request.getParameter("prompt");
        if (queryPrompt != null && !queryPrompt.isBlank()) {
            additionalParameters.put("prompt", queryPrompt);
        }

        return OAuth2AuthorizationRequest.from(req)
                .additionalParameters(additionalParameters)
                .build();
    }
}
