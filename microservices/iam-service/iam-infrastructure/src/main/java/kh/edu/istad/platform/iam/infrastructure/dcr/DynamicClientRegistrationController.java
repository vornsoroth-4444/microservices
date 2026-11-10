package kh.edu.istad.platform.iam.infrastructure.dcr;

import kh.edu.istad.platform.iam.infrastructure.adapter.PasswordHasherImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/connect/register")
public class DynamicClientRegistrationController {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordHasherImpl passwordHasher;
    private final JwtDecoder jwtDecoder;

    @Value("${spring.security.oauth2.authorizationserver.issuer:http://localhost:9000}")
    private String issuer;

    private final SecureRandom secureRandom = new SecureRandom();

    public DynamicClientRegistrationController(RegisteredClientRepository registeredClientRepository,
                                               PasswordHasherImpl passwordHasher,
                                               JwtDecoder jwtDecoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordHasher = passwordHasher;
        this.jwtDecoder = jwtDecoder;
    }

    @PostMapping
    public ResponseEntity<OidcClientRegistrationResponse> registerClient(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody OidcClientRegistrationRequest request) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing initial access token");
        }

        String token = authHeader.substring(7).trim();
        validateInitialAccessToken(token);

        String id = UUID.randomUUID().toString();
        byte[] idBytes = ("Client-" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
        String clientId = Base64.getUrlEncoder().withoutPadding().encodeToString(idBytes);

        byte[] secretBytes = new byte[32];
        secureRandom.nextBytes(secretBytes);
        String rawSecret = Base64.getUrlEncoder().withoutPadding().encodeToString(secretBytes);
        String encodedSecret = passwordHasher.hash(rawSecret);

        RegisteredClient.Builder builder = RegisteredClient.withId(id)
                .clientId(clientId)
                .clientIdIssuedAt(Instant.now())
                .clientSecret(encodedSecret)
                .clientName(request.getClientName() != null ? request.getClientName() : "Dynamic Client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);

        if (request.getRedirectUris() != null) {
            request.getRedirectUris().forEach(builder::redirectUri);
        }

        if (request.getGrantTypes() != null) {
            for (String gt : request.getGrantTypes()) {
                if ("authorization_code".equalsIgnoreCase(gt)) {
                    builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
                } else if ("refresh_token".equalsIgnoreCase(gt)) {
                    builder.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
                } else if ("client_credentials".equalsIgnoreCase(gt)) {
                    builder.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
                }
            }
        } else {
            builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
            builder.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
        }

        if (request.getScope() != null && !request.getScope().isBlank()) {
            String[] scopes = request.getScope().split(" ");
            for (String s : scopes) {
                if (!s.isBlank()) builder.scope(s.trim());
            }
        } else {
            builder.scope(OidcScopes.OPENID);
            builder.scope(OidcScopes.PROFILE);
            builder.scope(OidcScopes.EMAIL);
        }

        builder.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build());
        builder.tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(1))
                .refreshTokenTimeToLive(Duration.ofDays(30))
                .build());

        RegisteredClient registeredClient = builder.build();
        registeredClientRepository.save(registeredClient);

        String registrationClientUri = issuer + "/connect/register?client_id=" + clientId;
        String registrationAccessToken = "reg_" + clientId + "_" + rawSecret.substring(0, Math.min(16, rawSecret.length()));

        OidcClientRegistrationResponse response = new OidcClientRegistrationResponse(
                clientId,
                rawSecret,
                registeredClient.getClientName(),
                new ArrayList<>(registeredClient.getRedirectUris()),
                registrationAccessToken,
                registrationClientUri
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<OidcClientRegistrationResponse> getClient(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("client_id") String clientId) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing registration access token");
        }

        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found: " + clientId);
        }

        String registrationClientUri = issuer + "/connect/register?client_id=" + clientId;
        String regToken = authHeader.substring(7).trim();

        OidcClientRegistrationResponse response = new OidcClientRegistrationResponse(
                client.getClientId(),
                null, // do not re-expose client secret on read
                client.getClientName(),
                new ArrayList<>(client.getRedirectUris()),
                regToken,
                registrationClientUri
        );

        return ResponseEntity.ok(response);
    }

    private void validateInitialAccessToken(String token) {
        // Allow bootstrap token or verify JWT has client.create scope
        if ("bootstrap-initial-token".equals(token)) {
            return;
        }
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Object scopeObj = jwt.getClaims().get("scope");
            if (scopeObj != null && scopeObj.toString().contains("client.create")) {
                return;
            }
            Object scpObj = jwt.getClaims().get("scp");
            if (scpObj != null && scpObj.toString().contains("client.create")) {
                return;
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient scope: client.create required");
        } catch (Exception ex) {
            if (ex instanceof ResponseStatusException) throw ex;
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid initial access token: " + ex.getMessage());
        }
    }
}
