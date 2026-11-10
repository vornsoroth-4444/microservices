package kh.edu.istad.platform.iam.infrastructure.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.edu.istad.platform.iam.application.dto.AuthUserResult;
import kh.edu.istad.platform.iam.application.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.application.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iam.domain.model.IdentityProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;

@Controller
public class TelegramAuthController {

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public TelegramAuthController(FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase) {
        this.findOrProvisionFederatedUserUseCase = findOrProvisionFederatedUserUseCase;
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

        Map<String, String> telegramAttributes = new LinkedHashMap<>();
        telegramAttributes.put("id", id);
        if (firstName != null) telegramAttributes.put("first_name", firstName);
        if (lastName != null) telegramAttributes.put("last_name", lastName);
        if (username != null) telegramAttributes.put("username", username);
        if (photoUrl != null) telegramAttributes.put("photo_url", photoUrl);
        if (authDate != null) telegramAttributes.put("auth_date", authDate);
        if (hash != null) telegramAttributes.put("hash", hash);

        String displayName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
        if (displayName.isEmpty()) {
            displayName = username != null ? username : "Telegram User";
        }

        FederatedIdentityCommand cmd = FederatedIdentityCommand.builder()
                .provider(IdentityProvider.TELEGRAM)
                .providerSubject(id)
                .email(null)
                .emailVerified(false)
                .displayName(displayName)
                .username(username != null ? username : "tg_" + id)
                .pictureUrl(photoUrl)
                .attributes(objectMapper.writeValueAsString(telegramAttributes))
                .build();

        AuthUserResult authResult = findOrProvisionFederatedUserUseCase.execute(cmd);

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        for (String auth : authResult.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(auth));
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(authResult.getUser().getUsername(), null, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        response.sendRedirect("/");
    }
}
