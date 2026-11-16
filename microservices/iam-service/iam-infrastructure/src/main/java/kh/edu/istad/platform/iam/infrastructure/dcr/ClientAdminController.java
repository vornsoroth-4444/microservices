package kh.edu.istad.platform.iam.infrastructure.dcr;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api/clients")
@PreAuthorize("hasAuthority('user:update')")
public class ClientAdminController {
    private final RegisteredClientRepository clients;
    private final JdbcOperations jdbc;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public ClientAdminController(RegisteredClientRepository clients, JdbcOperations jdbc, PasswordEncoder passwordEncoder) {
        this.clients = clients;
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return jdbc.queryForList("SELECT id FROM oauth2_registered_client ORDER BY client_name").stream()
                .map(row -> clients.findById(String.valueOf(row.get("id"))))
                .filter(Objects::nonNull)
                .map(this::toMap)
                .toList();
    }

    @GetMapping("/{clientId}")
    public Map<String, Object> get(@PathVariable String clientId) {
        return toMap(require(clientId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestBody ClientRequest request) {
        validate(request);
        String id = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString().replace("-", "");
        String secret = secret();
        int accessMinutes = configInt("token.accessMinutes", 60);
        int refreshDays = configInt("token.refreshDays", 30);
        RegisteredClient.Builder builder = RegisteredClient.withId(id).clientId(clientId)
                .clientName(request.clientName().trim())
                .clientIdIssuedAt(java.time.Instant.now())
                .clientSecret(passwordEncoder.encode(secret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(accessMinutes)).refreshTokenTimeToLive(Duration.ofDays(refreshDays)).build());
        addGrants(builder, request.grantTypes());
        addScopes(builder, request.scopes());
        addRedirects(builder, request.redirectUris());
        RegisteredClient saved = builder.build();
        clients.save(saved);
        Map<String, Object> result = toMap(saved);
        result.put("clientSecret", secret);
        return result;
    }

    @PutMapping("/{clientId}")
    public Map<String, Object> update(@PathVariable String clientId, @RequestBody ClientRequest request) {
        validate(request);
        RegisteredClient current = require(clientId);
        RegisteredClient.Builder builder = RegisteredClient.from(current).clientName(request.clientName().trim());
        builder.authorizationGrantTypes(types -> { types.clear(); types.addAll(grantTypes(request.grantTypes())); });
        builder.scopes(scopes -> { scopes.clear(); scopes.addAll(clean(request.scopes())); });
        builder.redirectUris(uris -> { uris.clear(); uris.addAll(clean(request.redirectUris())); });
        RegisteredClient saved = builder.build();
        clients.save(saved);
        return toMap(saved);
    }

    @PostMapping("/{clientId}/rotate-secret")
    public Map<String, Object> rotateSecret(@PathVariable String clientId) {
        RegisteredClient current = require(clientId);
        String raw = secret();
        RegisteredClient saved = RegisteredClient.from(current).clientSecret(passwordEncoder.encode(raw)).build();
        clients.save(saved);
        Map<String, Object> result = toMap(saved);
        result.put("clientSecret", raw);
        return result;
    }

    @DeleteMapping("/{clientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String clientId) {
        RegisteredClient current = require(clientId);
        jdbc.update("DELETE FROM oauth2_authorization_consent WHERE registered_client_id = ?", current.getId());
        jdbc.update("DELETE FROM oauth2_authorization WHERE registered_client_id = ?", current.getId());
        jdbc.update("DELETE FROM oauth2_registered_client WHERE id = ?", current.getId());
    }

    private RegisteredClient require(String clientId) {
        RegisteredClient client = clients.findByClientId(clientId);
        if (client == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "OAuth client not found");
        return client;
    }

    private void validate(ClientRequest request) {
        if (request.clientName() == null || request.clientName().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientName is required");
        if (request.grantTypes() == null || request.grantTypes().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one grant type is required");
        Set<String> allowed = Set.of("authorization_code", "refresh_token", "client_credentials", "urn:ietf:params:oauth:grant-type:device_code");
        if (!allowed.containsAll(request.grantTypes())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported OAuth grant type");
        for (String uri : clean(request.redirectUris())) {
            if (!uri.startsWith("https://") && !uri.startsWith("http://localhost")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Redirect URIs must use HTTPS (HTTP is allowed for localhost)");
        }
    }

    private Set<AuthorizationGrantType> grantTypes(List<String> values) {
        Set<AuthorizationGrantType> result = new LinkedHashSet<>();
        for (String value : clean(values)) result.add(new AuthorizationGrantType(value));
        return result;
    }

    private void addGrants(RegisteredClient.Builder builder, List<String> values) { builder.authorizationGrantTypes(types -> types.addAll(grantTypes(values))); }
    private void addScopes(RegisteredClient.Builder builder, List<String> values) { builder.scopes(scopes -> scopes.addAll(clean(values))); }
    private void addRedirects(RegisteredClient.Builder builder, List<String> values) { builder.redirectUris(uris -> uris.addAll(clean(values))); }
    private Set<String> clean(List<String> values) { if (values == null) return Set.of(); Set<String> result = new LinkedHashSet<>(); for (String value : values) if (value != null && !value.isBlank()) result.add(value.trim()); return result; }

    private String secret() { byte[] bytes = new byte[32]; random.nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }

    private int configInt(String key, int fallback) {
        return jdbc.query("SELECT config_value FROM iam_system_config WHERE config_key = ?", rs -> rs.next() ? Integer.parseInt(rs.getString(1)) : fallback, key);
    }

    private Map<String, Object> toMap(RegisteredClient client) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", client.getId()); data.put("clientId", client.getClientId()); data.put("clientName", client.getClientName());
        data.put("createdAt", client.getClientIdIssuedAt()); data.put("authenticationMethods", client.getClientAuthenticationMethods().stream().map(ClientAuthenticationMethod::getValue).toList());
        data.put("grantTypes", client.getAuthorizationGrantTypes().stream().map(AuthorizationGrantType::getValue).toList());
        data.put("redirectUris", client.getRedirectUris()); data.put("scopes", client.getScopes());
        return data;
    }

    public record ClientRequest(String clientName, List<String> redirectUris, List<String> grantTypes, List<String> scopes) { }
}
