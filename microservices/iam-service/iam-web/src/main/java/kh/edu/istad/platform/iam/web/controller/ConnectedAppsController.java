package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.web.dto.ConnectedAppModel;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/profile/apps")
public class ConnectedAppsController {

    private final JdbcOperations jdbcOperations;
    private final OAuth2AuthorizationConsentService authorizationConsentService;
    private final RegisteredClientRepository registeredClientRepository;

    public ConnectedAppsController(JdbcOperations jdbcOperations,
                                  OAuth2AuthorizationConsentService authorizationConsentService,
                                  RegisteredClientRepository registeredClientRepository) {
        this.jdbcOperations = jdbcOperations;
        this.authorizationConsentService = authorizationConsentService;
        this.registeredClientRepository = registeredClientRepository;
    }

    @GetMapping
    public String connectedApps(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        String principalName = auth.getName();
        String sql = "SELECT registered_client_id, authorities FROM oauth2_authorization_consent WHERE principal_name = ?";
        List<ConnectedAppModel> apps = jdbcOperations.query(sql, (rs, rowNum) -> {
            String registeredClientId = rs.getString("registered_client_id");
            String authorities = rs.getString("authorities");
            RegisteredClient client = registeredClientRepository.findById(registeredClientId);
            String clientName = client != null ? client.getClientName() : registeredClientId;
            Set<String> scopes = authorities != null ? new HashSet<>(Arrays.asList(authorities.split(","))) : Collections.emptySet();
            return new ConnectedAppModel(registeredClientId, clientName, scopes);
        }, principalName);

        model.addAttribute("apps", apps);
        return "connected-apps";
    }

    @PostMapping("/{clientId}/revoke")
    public String revokeApp(Authentication auth,
                            @PathVariable String clientId,
                            RedirectAttributes redirectAttributes) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        OAuth2AuthorizationConsent consent = authorizationConsentService.findById(clientId, auth.getName());
        if (consent != null) {
            authorizationConsentService.remove(consent);
            redirectAttributes.addFlashAttribute("successMessage", "Application access revoked successfully");
        }

        return "redirect:/profile/apps";
    }
}
