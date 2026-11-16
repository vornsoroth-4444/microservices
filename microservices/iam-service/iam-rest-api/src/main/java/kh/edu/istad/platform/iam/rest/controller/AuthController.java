package kh.edu.istad.platform.iam.rest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.application.dto.RegisterUserCommand;
import kh.edu.istad.platform.iam.application.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.application.dto.UserResult;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.application.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.rest.dto.RestDtoMapper;
import kh.edu.istad.platform.iam.rest.dto.request.LoginRequest;
import kh.edu.istad.platform.iam.rest.dto.request.RegisterUserRequest;
import kh.edu.istad.platform.iam.rest.dto.response.LoginResponse;
import kh.edu.istad.platform.iam.rest.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JdbcOperations jdbc;

    @Value("${spring.security.oauth2.authorizationserver.issuer:http://localhost:9000}")
    private String issuer;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          UserRepository userRepository,
                          GetUserAuthoritiesUseCase getUserAuthoritiesUseCase,
                          PasswordEncoder passwordEncoder,
                          JwtEncoder jwtEncoder,
                          JdbcOperations jdbc) {
        this.registerUserUseCase = registerUserUseCase;
        this.userRepository = userRepository;
        this.getUserAuthoritiesUseCase = getUserAuthoritiesUseCase;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jdbc = jdbc;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(request.username().trim());
        if (userOpt.isEmpty() || userOpt.get().isDeleted()) {
            recordSecurityEvent("LOGIN_FAILURE", request.username(), "auth", "User not found", clientIp, userAgent, false);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        User user = userOpt.get();

        if (!user.isEnabled()) {
            recordSecurityEvent("LOGIN_FAILURE", user.getUsername(), user.getUsername(), "Account is disabled", clientIp, userAgent, false);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled");
        }

        if (!user.isAccountNonLocked()) {
            recordSecurityEvent("LOGIN_FAILURE", user.getUsername(), user.getUsername(), "Account is locked", clientIp, userAgent, false);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is locked");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            jdbc.update("UPDATE users SET failed_login_attempts = failed_login_attempts + 1 WHERE id = ?", user.getId());
            recordSecurityEvent("LOGIN_FAILURE", user.getUsername(), user.getUsername(), "Bad credentials", clientIp, userAgent, false);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // Reset failed login attempts and update last_login_at
        jdbc.update("UPDATE users SET failed_login_attempts = 0, last_login_at = CURRENT_TIMESTAMP WHERE id = ?", user.getId());
        recordSecurityEvent("LOGIN_SUCCESS", user.getUsername(), user.getUsername(), "React REST authentication granted", clientIp, userAgent, true);

        return ResponseEntity.ok(createLoginResponse(user));
    }

    @GetMapping("/session")
    public ResponseEntity<LoginResponse> checkSession(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(createLoginResponse(userOpt.get()));
    }

    private LoginResponse createLoginResponse(User user) {
        UserAuthoritiesResult authorities = getUserAuthoritiesUseCase.execute(user.getUuid());
        List<String> roleNames = authorities.getRoles() != null
                ? authorities.getRoles().stream().map(UserAuthoritiesResult.RoleAuthority::getName).toList()
                : List.of();
        List<String> effectivePermissions = authorities.getEffectivePermissions() != null
                ? List.copyOf(authorities.getEffectivePermissions())
                : List.of();

        String fullName = ((user.getGivenName() != null ? user.getGivenName() : "") + " " + (user.getFamilyName() != null ? user.getFamilyName() : "")).trim();
        if (fullName.isEmpty()) fullName = user.getUsername();

        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofHours(24));
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getUuid().toString())
                .claim("preferred_username", user.getUsername())
                .claim("email", user.getEmail() != null ? user.getEmail() : "")
                .claim("name", fullName)
                .claim("roles", roleNames)
                .claim("permissions", effectivePermissions)
                .id(tokenId)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        LoginResponse.UserPayload userPayload = new LoginResponse.UserPayload(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                fullName,
                user.getGivenName(),
                user.getFamilyName(),
                user.getPhoneNumber(),
                user.getPictureUrl(),
                user.getLocale() != null ? user.getLocale() : "en",
                roleNames,
                effectivePermissions
        );

        return new LoginResponse(token, "Bearer", 86400, userPayload);
    }

    private void recordSecurityEvent(String eventType, String actor, String target, String detail, String ip, String userAgent, boolean succeeded) {
        try {
            jdbc.update("INSERT INTO iam_security_event(event_type, actor, target, detail, ip_address, user_agent, succeeded, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                    eventType, actor, target, detail, ip, userAgent, succeeded);
        } catch (Exception ignored) {
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserCommand cmd = RegisterUserCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .confirmPassword(request.getConfirmPassword())
                .givenName(request.getGivenName())
                .familyName(request.getFamilyName())
                .locale(request.getLocale())
                .build();

        UserResult result = registerUserUseCase.execute(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestDtoMapper.toUserResponse(result));
    }
}
