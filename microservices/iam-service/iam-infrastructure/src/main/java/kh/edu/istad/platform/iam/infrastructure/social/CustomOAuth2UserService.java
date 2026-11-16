package kh.edu.istad.platform.iam.infrastructure.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.istad.platform.iam.application.dto.AuthUserResult;
import kh.edu.istad.platform.iam.application.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iam.application.usecase.identity.LinkIdentityUseCase;
import kh.edu.istad.platform.iam.domain.model.IdentityProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;
    private final LinkIdentityUseCase linkIdentityUseCase;
    private final JdbcOperations jdbc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomOAuth2UserService(FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase, LinkIdentityUseCase linkIdentityUseCase, JdbcOperations jdbc) {
        this.findOrProvisionFederatedUserUseCase = findOrProvisionFederatedUserUseCase;
        this.linkIdentityUseCase = linkIdentityUseCase;
        this.jdbc = jdbc;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        if (!socialEnabled()) throw new OAuth2AuthenticationException(new org.springframework.security.oauth2.core.OAuth2Error("access_denied"), "Social sign-in is disabled by IAM policy");
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toLowerCase();

        IdentityProvider provider;
        String providerSubject;
        String email = null;
        boolean emailVerified = false;
        String displayName = null;
        String username = null;
        String pictureUrl = null;

        Map<String, Object> attributes = oauth2User.getAttributes();
        String attributesJson = "{}";
        try {
            attributesJson = objectMapper.writeValueAsString(attributes);
        } catch (Exception ignored) {}

        if ("github".equals(registrationId)) {
            provider = IdentityProvider.GITHUB;
            providerSubject = String.valueOf(attributes.get("id"));
            email = (String) attributes.get("email");
            emailVerified = email != null;
            displayName = (String) attributes.get("name");
            username = (String) attributes.get("login");
            pictureUrl = (String) attributes.get("avatar_url");
        } else if ("facebook".equals(registrationId)) {
            provider = IdentityProvider.FACEBOOK;
            providerSubject = String.valueOf(attributes.get("id"));
            email = (String) attributes.get("email");
            emailVerified = email != null;
            displayName = (String) attributes.get("name");
            username = (String) attributes.get("name");
            pictureUrl = null;
        } else {
            // Default Google or generic
            provider = IdentityProvider.GOOGLE;
            providerSubject = (String) attributes.get("sub");
            if (providerSubject == null) {
                providerSubject = String.valueOf(attributes.get("id"));
            }
            email = (String) attributes.get("email");
            Boolean ev = (Boolean) attributes.get("email_verified");
            emailVerified = Boolean.TRUE.equals(ev);
            displayName = (String) attributes.get("name");
            pictureUrl = (String) attributes.get("picture");
        }

        FederatedIdentityCommand cmd = FederatedIdentityCommand.builder()
                .provider(provider)
                .providerSubject(providerSubject)
                .email(email)
                .emailVerified(emailVerified)
                .displayName(displayName != null ? displayName : username)
                .username(username)
                .pictureUrl(pictureUrl)
                .attributes(attributesJson)
                .build();

        String linkTarget = takeLinkTarget();
        if (linkTarget != null) {
            linkIdentityUseCase.execute(UUID.fromString(linkTarget), cmd);
        }

        AuthUserResult authResult = findOrProvisionFederatedUserUseCase.execute(cmd);

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (String auth : authResult.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(auth));
        }

        Map<String, Object> enhancedAttributes = new HashMap<>(attributes);
        if (authResult.getUser() != null) {
            enhancedAttributes.put("userUuid", authResult.getUser().getUuid().toString());
            enhancedAttributes.put("username", authResult.getUser().getUsername());
            if (authResult.getUser().getEmail() != null) {
                enhancedAttributes.putIfAbsent("email", authResult.getUser().getEmail());
            }
        }

        String nameAttributeKey = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        if (nameAttributeKey == null || !enhancedAttributes.containsKey(nameAttributeKey)) {
            nameAttributeKey = "id";
            if (!enhancedAttributes.containsKey(nameAttributeKey)) {
                nameAttributeKey = "sub";
            }
        }

        return new DefaultOAuth2User(authorities, enhancedAttributes, nameAttributeKey);
    }

    private String takeLinkTarget() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) return null;
        var session = attributes.getRequest().getSession(false);
        if (session == null) return null;
        Object value = session.getAttribute("iam-link-user-uuid");
        session.removeAttribute("iam-link-user-uuid");
        return value == null ? null : value.toString();
    }

    private boolean socialEnabled() {
        return jdbc.query("SELECT config_value FROM iam_system_config WHERE config_key='social.enabled'", rs -> !rs.next() || Boolean.parseBoolean(rs.getString(1)));
    }
}
