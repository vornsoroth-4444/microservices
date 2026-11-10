package kh.edu.istad.platform.iam.infrastructure.security;

import kh.edu.istad.platform.iam.application.usecase.user.RecordLoginAttemptUseCase;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventsListener {

    private final RecordLoginAttemptUseCase recordLoginAttemptUseCase;

    public AuthenticationEventsListener(RecordLoginAttemptUseCase recordLoginAttemptUseCase) {
        this.recordLoginAttemptUseCase = recordLoginAttemptUseCase;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        if (username != null && !username.isBlank()) {
            recordLoginAttemptUseCase.execute(username, true);
        }
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        if (username != null && !username.isBlank()) {
            recordLoginAttemptUseCase.execute(username, false);
        }
    }
}
