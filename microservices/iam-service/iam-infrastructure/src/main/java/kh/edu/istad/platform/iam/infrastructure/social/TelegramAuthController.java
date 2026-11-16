package kh.edu.istad.platform.iam.infrastructure.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.edu.istad.platform.iam.application.dto.AuthUserResult;
import kh.edu.istad.platform.iam.application.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.application.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iam.application.usecase.identity.LinkIdentityUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.domain.model.IdentityProvider;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import kh.edu.istad.platform.iam.infrastructure.security.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

@Controller
public class TelegramAuthController {

    private static final Logger log = LoggerFactory.getLogger(TelegramAuthController.class);

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;
    private final LinkIdentityUseCase linkIdentityUseCase;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final JwtTokenService jwtTokenService;
    private final JdbcOperations jdbc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Value("${telegram.bot-token:}")
    private String botToken;

    public TelegramAuthController(FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase,
                                  LinkIdentityUseCase linkIdentityUseCase,
                                  UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  GetUserAuthoritiesUseCase getUserAuthoritiesUseCase,
                                  JwtTokenService jwtTokenService,
                                  JdbcOperations jdbc) {
        this.findOrProvisionFederatedUserUseCase = findOrProvisionFederatedUserUseCase;
        this.linkIdentityUseCase = linkIdentityUseCase;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.getUserAuthoritiesUseCase = getUserAuthoritiesUseCase;
        this.jwtTokenService = jwtTokenService;
        this.jdbc = jdbc;
    }

    @GetMapping("/login/telegram")
    public void handleTelegramLogin(
            @RequestParam("id") String id,
            @RequestParam(value = "first_name", required = false) String firstName,
            @RequestParam(value = "last_name", required = false) String lastName,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "photo_url", required = false) String photoUrl,
            @RequestParam(value = "auth_date", required = false) String authDate,
            @RequestParam(value = "hash", required = false) String hash,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if (!socialEnabled()) {
            response.sendRedirect("/login?error=social_disabled");
            return;
        }

        Map<String, String> telegramAttributes = new LinkedHashMap<>();
        telegramAttributes.put("id", id);
        if (firstName != null && !firstName.isBlank()) telegramAttributes.put("first_name", firstName);
        if (lastName != null && !lastName.isBlank()) telegramAttributes.put("last_name", lastName);
        if (username != null && !username.isBlank()) telegramAttributes.put("username", username);
        if (photoUrl != null && !photoUrl.isBlank()) telegramAttributes.put("photo_url", photoUrl);
        if (authDate != null && !authDate.isBlank()) telegramAttributes.put("auth_date", authDate);
        if (hash != null && !hash.isBlank()) telegramAttributes.put("hash", hash);

        // Verify hash if bot token configured and hash provided
        if (botToken != null && !botToken.isBlank() && hash != null && !hash.isBlank()) {
            if (!verifyTelegramHash(telegramAttributes, hash)) {
                log.warn("Telegram hash verification failed for user id: {}", id);
                response.sendRedirect("/login?error=telegram_verification_failed");
                return;
            }
        }

        String displayName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
        if (displayName.isEmpty()) {
            displayName = username != null ? username : "Telegram User";
        }

        String safeUsername = username != null ? username.replaceAll("\\s+", "").toLowerCase() : "tg_" + id;

        FederatedIdentityCommand cmd = FederatedIdentityCommand.builder()
                .provider(IdentityProvider.TELEGRAM)
                .providerSubject(id)
                .email(null)
                .emailVerified(false)
                .displayName(displayName)
                .username(safeUsername)
                .pictureUrl(photoUrl)
                .attributes(objectMapper.writeValueAsString(telegramAttributes))
                .build();

        // Check if linking to existing user
        String linkTarget = takeLinkTarget(request);
        if (linkTarget != null) {
            try {
                linkIdentityUseCase.execute(UUID.fromString(linkTarget), cmd);
            } catch (Exception e) {
                log.warn("Failed to link Telegram identity to user {}: {}", linkTarget, e.getMessage());
            }
        }

        AuthUserResult authResult = findOrProvisionFederatedUserUseCase.execute(cmd);
        User user = authResult.getUser();

        // Guarantee USER role is present
        ensureUserRole(user);

        // Fetch authorities and permissions
        UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(user.getUuid());
        List<String> roleNames = authoritiesResult.getRoles() != null
                ? authoritiesResult.getRoles().stream().map(UserAuthoritiesResult.RoleAuthority::getName).toList()
                : List.of("USER");
        List<String> permissions = authoritiesResult.getEffectivePermissions() != null
                ? List.copyOf(authoritiesResult.getEffectivePermissions())
                : List.of("user:read");

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (String auth : permissions) {
            authorities.add(new SimpleGrantedAuthority(auth));
        }
        for (String r : roleNames) {
            authorities.add(new SimpleGrantedAuthority(r.startsWith("ROLE_") ? r : "ROLE_" + r));
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        String fullName = ((user.getGivenName() != null ? user.getGivenName() : "") + " "
                + (user.getFamilyName() != null ? user.getFamilyName() : "")).trim();
        if (fullName.isEmpty()) fullName = user.getUsername();

        String token = jwtTokenService.generateToken(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                fullName,
                roleNames,
                permissions
        );

        String origin = determineOrigin(request);
        String redirectUrl = origin + "/?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) + "&social=true";

        // Clear iam_auth_origin cookie
        Cookie clearCookie = new Cookie("iam_auth_origin", "");
        clearCookie.setPath("/");
        clearCookie.setMaxAge(0);
        response.addCookie(clearCookie);

        log.info("Telegram login success for user: {}, role: {}, redirecting to: {}", user.getUsername(), roleNames, redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/api/auth/telegram")
    @ResponseBody
    public ResponseEntity<?> handleTelegramAuthApi(@RequestBody Map<String, String> payload,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        String id = payload.get("id");
        if (id == null || id.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Telegram user ID is required"));
        }

        if (!socialEnabled()) {
            return ResponseEntity.status(403).body(Map.of("message", "Social sign-in is disabled by IAM policy"));
        }

        String firstName = payload.get("first_name");
        String lastName = payload.get("last_name");
        String username = payload.get("username");
        String photoUrl = payload.get("photo_url");
        String authDate = payload.get("auth_date");
        String hash = payload.get("hash");

        Map<String, String> telegramAttributes = new LinkedHashMap<>();
        telegramAttributes.put("id", id);
        if (firstName != null && !firstName.isBlank()) telegramAttributes.put("first_name", firstName);
        if (lastName != null && !lastName.isBlank()) telegramAttributes.put("last_name", lastName);
        if (username != null && !username.isBlank()) telegramAttributes.put("username", username);
        if (photoUrl != null && !photoUrl.isBlank()) telegramAttributes.put("photo_url", photoUrl);
        if (authDate != null && !authDate.isBlank()) telegramAttributes.put("auth_date", authDate);
        if (hash != null && !hash.isBlank()) telegramAttributes.put("hash", hash);

        if (botToken != null && !botToken.isBlank() && hash != null && !hash.isBlank()) {
            if (!verifyTelegramHash(telegramAttributes, hash)) {
                return ResponseEntity.status(401).body(Map.of("message", "Telegram signature verification failed"));
            }
        }

        String displayName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
        if (displayName.isEmpty()) {
            displayName = username != null ? username : "Telegram User";
        }

        String safeUsername = username != null ? username.replaceAll("\\s+", "").toLowerCase() : "tg_" + id;

        try {
            FederatedIdentityCommand cmd = FederatedIdentityCommand.builder()
                    .provider(IdentityProvider.TELEGRAM)
                    .providerSubject(id)
                    .email(null)
                    .emailVerified(false)
                    .displayName(displayName)
                    .username(safeUsername)
                    .pictureUrl(photoUrl)
                    .attributes(objectMapper.writeValueAsString(telegramAttributes))
                    .build();

            AuthUserResult authResult = findOrProvisionFederatedUserUseCase.execute(cmd);
            User user = authResult.getUser();
            ensureUserRole(user);

            UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(user.getUuid());
            List<String> roleNames = authoritiesResult.getRoles() != null
                    ? authoritiesResult.getRoles().stream().map(UserAuthoritiesResult.RoleAuthority::getName).toList()
                    : List.of("USER");
            List<String> permissions = authoritiesResult.getEffectivePermissions() != null
                    ? List.copyOf(authoritiesResult.getEffectivePermissions())
                    : List.of("user:read");

            String fullName = ((user.getGivenName() != null ? user.getGivenName() : "") + " "
                    + (user.getFamilyName() != null ? user.getFamilyName() : "")).trim();
            if (fullName.isEmpty()) fullName = user.getUsername();

            String token = jwtTokenService.generateToken(
                    user.getUuid(),
                    user.getUsername(),
                    user.getEmail(),
                    fullName,
                    roleNames,
                    permissions
            );

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("accessToken", token);
            resp.put("tokenType", "Bearer");
            resp.put("user", Map.of(
                    "uuid", user.getUuid().toString(),
                    "username", user.getUsername(),
                    "name", fullName,
                    "roles", roleNames,
                    "authorities", permissions
            ));
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            log.error("Telegram API auth error", ex);
            return ResponseEntity.internalServerError().body(Map.of("message", ex.getMessage()));
        }
    }

    private void ensureUserRole(User user) {
        Optional<Role> userRoleOpt = roleRepository.findByName("USER");
        if (userRoleOpt.isEmpty()) {
            userRoleOpt = roleRepository.findByName("ROLE_USER");
        }
        if (userRoleOpt.isPresent()) {
            Role r = userRoleOpt.get();
            var existingRoles = userRepository.findRolesByUserId(user.getId());
            boolean hasRole = existingRoles != null && existingRoles.stream().anyMatch(ur -> ur.getRoleId().equals(r.getId()));
            if (!hasRole) {
                UserRole ur = UserRole.builder()
                        .userId(user.getId())
                        .roleId(r.getId())
                        .assignedAt(Instant.now())
                        .assignedBy("SYSTEM")
                        .reason("Telegram sign-in default role")
                        .build();
                userRepository.saveUserRole(ur);
            }
        }
    }

    private boolean verifyTelegramHash(Map<String, String> params, String expectedHash) {
        if (botToken == null || botToken.isBlank() || expectedHash == null || expectedHash.isBlank()) {
            return true;
        }
        try {
            List<String> keys = new ArrayList<>(params.keySet());
            keys.remove("hash");
            Collections.sort(keys);

            StringBuilder dataCheck = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                String k = keys.get(i);
                dataCheck.append(k).append("=").append(params.get(k));
                if (i < keys.size() - 1) {
                    dataCheck.append("\n");
                }
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = digest.digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] hmac = mac.doFinal(dataCheck.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hmac) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().equalsIgnoreCase(expectedHash);
        } catch (Exception e) {
            log.warn("Telegram hash verification error: {}", e.getMessage());
            return false;
        }
    }

    private String determineOrigin(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("iam_auth_origin".equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    String val = c.getValue().trim();
                    if (val.startsWith("http://") || val.startsWith("https://")) {
                        return val;
                    }
                }
            }
        }
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains(":3000")) {
            return "http://localhost:3000";
        }
        return "";
    }

    private String takeLinkTarget(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session == null) return null;
        Object val = session.getAttribute("iam-link-user-uuid");
        session.removeAttribute("iam-link-user-uuid");
        return val == null ? null : val.toString();
    }

    private boolean socialEnabled() {
        return jdbc.query("SELECT config_value FROM iam_system_config WHERE config_key='social.enabled'", rs -> !rs.next() || Boolean.parseBoolean(rs.getString(1)));
    }
}
