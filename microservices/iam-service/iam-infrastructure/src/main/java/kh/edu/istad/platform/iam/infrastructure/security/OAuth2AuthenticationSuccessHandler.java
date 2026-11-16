package kh.edu.istad.platform.iam.infrastructure.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.edu.istad.platform.iam.application.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.application.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final JwtTokenService jwtTokenService;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository,
                                              RoleRepository roleRepository,
                                              GetUserAuthoritiesUseCase getUserAuthoritiesUseCase,
                                              JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.getUserAuthoritiesUseCase = getUserAuthoritiesUseCase;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            User user = resolveUser(authentication);
            if (user == null) {
                log.error("Could not resolve local user for principal: {}", authentication.getName());
                response.sendRedirect("/login?error=user_not_found");
                return;
            }

            // Guarantee USER role is assigned
            ensureUserRole(user);

            // Fetch authorities and permissions
            UserAuthoritiesResult authorities = getUserAuthoritiesUseCase.execute(user.getUuid());
            List<String> roleNames = authorities.getRoles() != null
                    ? authorities.getRoles().stream().map(UserAuthoritiesResult.RoleAuthority::getName).toList()
                    : List.of("USER");
            List<String> permissions = authorities.getEffectivePermissions() != null
                    ? List.copyOf(authorities.getEffectivePermissions())
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

            String origin = determineOrigin(request);
            String redirectUrl = origin + "/?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) + "&social=true";

            // Clear iam_auth_origin cookie
            Cookie clearCookie = new Cookie("iam_auth_origin", "");
            clearCookie.setPath("/");
            clearCookie.setMaxAge(0);
            response.addCookie(clearCookie);

            log.info("Social login success for user: {}, role: {}, redirecting to: {}", user.getUsername(), roleNames, redirectUrl);
            response.sendRedirect(redirectUrl);
        } catch (Exception ex) {
            log.error("Error in OAuth2AuthenticationSuccessHandler", ex);
            response.sendRedirect("/login?error=" + URLEncoder.encode(ex.getMessage() != null ? ex.getMessage() : "auth_error", StandardCharsets.UTF_8));
        }
    }

    private User resolveUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            // 1. userUuid
            Object uuidAttr = oauth2User.getAttribute("userUuid");
            if (uuidAttr != null) {
                try {
                    Optional<User> u = userRepository.findByUuid(UUID.fromString(uuidAttr.toString()));
                    if (u.isPresent()) return u.get();
                } catch (Exception ignored) {}
            }
            // 2. username
            Object usernameAttr = oauth2User.getAttribute("username");
            if (usernameAttr != null) {
                Optional<User> u = userRepository.findByUsername(usernameAttr.toString());
                if (u.isPresent()) return u.get();
            }
            // 3. email
            Object emailAttr = oauth2User.getAttribute("email");
            if (emailAttr != null && !emailAttr.toString().isBlank()) {
                Optional<User> u = userRepository.findByEmail(emailAttr.toString());
                if (u.isPresent()) return u.get();
            }
            // 4. OidcUser details
            if (oauth2User instanceof OidcUser oidc) {
                if (oidc.getEmail() != null) {
                    Optional<User> u = userRepository.findByEmail(oidc.getEmail());
                    if (u.isPresent()) return u.get();
                }
                if (oidc.getPreferredUsername() != null) {
                    Optional<User> u = userRepository.findByUsername(oidc.getPreferredUsername());
                    if (u.isPresent()) return u.get();
                }
            }
        }
        return userRepository.findByUsernameOrEmail(authentication.getName()).orElse(null);
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
                        .reason("Social sign-in default role")
                        .build();
                userRepository.saveUserRole(ur);
            }
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
}
