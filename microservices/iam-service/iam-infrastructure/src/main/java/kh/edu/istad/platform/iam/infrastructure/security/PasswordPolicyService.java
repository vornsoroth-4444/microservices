package kh.edu.istad.platform.iam.infrastructure.security;

import kh.edu.istad.platform.iam.application.port.out.PasswordPolicy;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyService implements PasswordPolicy {
    private final JdbcOperations jdbc;

    public PasswordPolicyService(JdbcOperations jdbc) { this.jdbc = jdbc; }

    @Override
    public void validate(String password) {
        if (password == null) throw new IllegalArgumentException("Password is required");
        int minimum = Integer.parseInt(value("password.minLength", "12"));
        if (password.length() < minimum) throw new IllegalArgumentException("Password must be at least " + minimum + " characters");
        if (Boolean.parseBoolean(value("password.requireUppercase", "true")) && password.chars().noneMatch(Character::isUpperCase)) throw new IllegalArgumentException("Password must include an uppercase letter");
        if (Boolean.parseBoolean(value("password.requireNumber", "true")) && password.chars().noneMatch(Character::isDigit)) throw new IllegalArgumentException("Password must include a number");
        if (Boolean.parseBoolean(value("password.requireSymbol", "false")) && password.chars().noneMatch(ch -> !Character.isLetterOrDigit(ch))) throw new IllegalArgumentException("Password must include a symbol");
    }

    private String value(String key, String fallback) {
        return jdbc.query("SELECT config_value FROM iam_system_config WHERE config_key = ?", rs -> rs.next() ? rs.getString(1) : fallback, key);
    }
}
