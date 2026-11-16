package kh.edu.istad.platform.iam.infrastructure.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.istad.platform.iam.application.dto.AuthUserResult;
import kh.edu.istad.platform.iam.application.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iam.application.usecase.identity.LinkIdentityUseCase;
import kh.edu.istad.platform.iam.domain.model.IdentityProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;
    private final LinkIdentityUseCase linkIdentityUseCase;
    private final JdbcOperations jdbc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomOidcUserService(FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase, LinkIdentityUseCase linkIdentityUseCase, JdbcOperations jdbc) {
        this.findOrProvisionFederatedUserUseCase = findOrProvisionFederatedUserUseCase;
        this.linkIdentityUseCase = linkIdentityUseCase;
        this.jdbc = jdbc;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        if (!socialEnabled()) throw new OAuth2AuthenticationException(new org.springframework.security.oauth2.core.OAuth2Error("access_denied"), "Social sign-in is disabled by IAM policy");
        OidcUser oidcUser = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toLowerCase();

        IdentityProvider provider = IdentityProvider.GOOGLE;
        if ("google".equals(registrationId)) {
            provider = IdentityProvider.GOOGLE;
        }

        String providerSubject = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        boolean emailVerified = Boolean.TRUE.equals(oidcUser.getEmailVerified());
        String displayName = oidcUser.getFullName();
        String pictureUrl = oidcUser.getPicture();

        String attributesJson = "{}";
        try {
            attributesJson = objectMapper.writeValueAsString(oidcUser.getClaims());
        } catch (Exception ignored) {}

        FederatedIdentityCommand cmd = FederatedIdentityCommand.builder()
                .provider(provider)
                .providerSubject(providerSubject)
                .email(email)
                .emailVerified(emailVerified)
                .displayName(displayName)
                .username(email != null && email.contains("@") ? email.split("@")[0] : null)
                .pictureUrl(pictureUrl)
                .attributes(attributesJson)
                .build();

        String linkTarget = takeLinkTarget();
        if (linkTarget != null) {
            linkIdentityUseCase.execute(java.util.UUID.fromString(linkTarget), cmd);
        }

        AuthUserResult authResult = findOrProvisionFederatedUserUseCase.execute(cmd);

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (String auth : authResult.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(auth));
        }

        Map<String, Object> claims = new HashMap<>(oidcUser.getClaims());
        if (authResult.getUser() != null) {
            claims.put("userUuid", authResult.getUser().getUuid().toString());
            claims.put("username", authResult.getUser().getUsername());
            if (authResult.getUser().getEmail() != null) {
                claims.putIfAbsent("email", authResult.getUser().getEmail());
            }
        }
        org.springframework.security.oauth2.core.oidc.OidcUserInfo userInfo =
                new org.springframework.security.oauth2.core.oidc.OidcUserInfo(claims);

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), userInfo);
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
