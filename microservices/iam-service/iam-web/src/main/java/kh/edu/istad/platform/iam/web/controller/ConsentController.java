package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.web.dto.ConsentModel;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ConsentController {

    private final RegisteredClientRepository registeredClientRepository;

    public ConsentController(RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
    }

    @GetMapping("/oauth2/consent")
    public String consent(
            @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
            @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
            @RequestParam(OAuth2ParameterNames.STATE) String state,
            @RequestParam(name = OAuth2ParameterNames.REDIRECT_URI, required = false) String redirectUri,
            Model model,
            Principal principal) {

        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        String clientName = registeredClient != null ? registeredClient.getClientName() : clientId;

        Set<String> scopesToApprove = new HashSet<>();
        if (StringUtils.hasText(scope)) {
            scopesToApprove.addAll(Arrays.asList(scope.split(" ")));
        }

        ConsentModel consentModel = new ConsentModel(
                clientId,
                clientName,
                new ArrayList<>(scopesToApprove),
                state,
                redirectUri
        );

        model.addAttribute("consent", consentModel);
        model.addAttribute("principalName", principal != null ? principal.getName() : "");
        return "consent";
    }
}
