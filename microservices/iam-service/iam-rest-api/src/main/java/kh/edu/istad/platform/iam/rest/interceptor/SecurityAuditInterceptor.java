package kh.edu.istad.platform.iam.rest.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityAuditInterceptor implements HandlerInterceptor {
    private final JdbcOperations jdbc;

    public SecurityAuditInterceptor(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (request.getRequestURI().endsWith("/audit-logs") || request.getRequestURI().endsWith("/activity") || request.getRequestURI().endsWith("/login-history")) return;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String actor = auth == null ? "anonymous" : auth.getName();
        String path = request.getRequestURI();
        String target = path.substring(path.lastIndexOf('/') + 1);
        String event = "API_" + request.getMethod();
        String detail = path + " returned " + response.getStatus();
        String agent = request.getHeader("User-Agent");
        jdbc.update("INSERT INTO iam_security_event(event_type, actor, target, detail, ip_address, user_agent, succeeded) VALUES (?, ?, ?, ?, ?, ?, ?)",
                event, actor, target, detail, request.getRemoteAddr(), agent, response.getStatus() < 400);
    }
}
