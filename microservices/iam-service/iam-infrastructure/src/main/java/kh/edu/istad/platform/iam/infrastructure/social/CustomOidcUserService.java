package kh.edu.istad.platform.iam.infrastructure.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.istad.platform.iam.application.dto.AuthUserResult;
import kh.edu.istad.platform.iam.application.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iam.domain.model.IdentityProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomOidcUserService(FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase) {
        this.findOrProvisionFederatedUserUseCase = findOrProvisionFederatedUserUseCase;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
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

        AuthUserResult authResult = findOrProvisionFederatedUserUseCase.execute(cmd);

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (String auth : authResult.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(auth));
        }

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
