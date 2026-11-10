package kh.edu.istad.platform.iam.infrastructure.adapter;

import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentActorProviderImpl implements CurrentActorProvider {

    @Override
    public String getCurrentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "SYSTEM";
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt jwt) {
            String sub = jwt.getSubject();
            if (sub != null && !sub.isBlank()) {
                return sub;
            }
            String pref = jwt.getClaimAsString("preferred_username");
            if (pref != null && !pref.isBlank()) {
                return pref;
            }
        } else if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return auth.getName() != null ? auth.getName() : "SYSTEM";
    }
}
