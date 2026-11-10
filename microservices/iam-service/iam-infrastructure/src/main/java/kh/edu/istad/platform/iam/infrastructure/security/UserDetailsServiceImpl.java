package kh.edu.istad.platform.iam.infrastructure.security;

import kh.edu.istad.platform.iam.application.dto.AuthUserResult;
import kh.edu.istad.platform.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase;

    public UserDetailsServiceImpl(LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase) {
        this.loadUserForAuthenticationUseCase = loadUserForAuthenticationUseCase;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            AuthUserResult authResult = loadUserForAuthenticationUseCase.execute(username);
            return new SecurityUser(
                    authResult.getUser(),
                    authResult.getRoles(),
                    authResult.getPermissions(),
                    authResult.getAuthorities()
            );
        } catch (Exception ex) {
            throw new UsernameNotFoundException("User not found: " + username, ex);
        }
    }
}
