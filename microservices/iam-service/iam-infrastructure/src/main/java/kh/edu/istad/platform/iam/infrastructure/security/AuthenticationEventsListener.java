package kh.edu.istad.platform.iam.infrastructure.security;

import kh.edu.istad.platform.iam.application.usecase.user.RecordLoginAttemptUseCase;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationEventsListener {

    private final RecordLoginAttemptUseCase recordLoginAttemptUseCase;
    private final JdbcOperations jdbc;

    public AuthenticationEventsListener(RecordLoginAttemptUseCase recordLoginAttemptUseCase, JdbcOperations jdbc) {
        this.recordLoginAttemptUseCase = recordLoginAttemptUseCase;
        this.jdbc = jdbc;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        if (username != null && !username.isBlank()) {
            recordLoginAttemptUseCase.execute(username, true);
            record(resolveUsername(username), true, null);
        }
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        if (username != null && !username.isBlank()) {
            recordLoginAttemptUseCase.execute(username, false);
            record(resolveUsername(username), false, event.getException().getClass().getSimpleName());
        }
    }

    private void record(String username, boolean succeeded, String detail) {
        String ip = "unknown";
        String agent = "unknown";
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            var request = attributes.getRequest();
            ip = request.getRemoteAddr();
            agent = request.getHeader("User-Agent");
        }
        jdbc.update("INSERT INTO iam_security_event(event_type, actor, target, detail, ip_address, user_agent, succeeded) VALUES (?, ?, ?, ?, ?, ?, ?)",
                succeeded ? "LOGIN_SUCCESS" : "LOGIN_FAILURE", username, username, detail, ip, agent, succeeded);
    }

    private String resolveUsername(String name) {
        return jdbc.query("SELECT u.username FROM users u LEFT JOIN identities i ON i.user_id=u.id AND i.is_deleted=false WHERE u.username=? OR lower(u.email)=lower(?) OR i.provider_subject=? ORDER BY CASE WHEN u.username=? THEN 0 WHEN lower(u.email)=lower(?) THEN 1 ELSE 2 END LIMIT 1",
                rs -> rs.next() ? rs.getString(1) : name, name, name, name, name, name);
    }
}
