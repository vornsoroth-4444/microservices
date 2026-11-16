package kh.edu.istad.platform.iam.rest.controller;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/system-config")
@PreAuthorize("hasAuthority('user:update')")
public class SystemConfigController {
    private static final Set<String> ALLOWED = Set.of("password.minLength", "password.requireUppercase", "password.requireNumber", "password.requireSymbol", "token.accessMinutes", "token.refreshDays", "social.enabled");
    private final JdbcOperations jdbc;

    public SystemConfigController(JdbcOperations jdbc) { this.jdbc = jdbc; }

    @GetMapping
    public Map<String, String> get() {
        return jdbc.queryForList("SELECT config_key, config_value FROM iam_system_config").stream()
                .collect(Collectors.toMap(row -> String.valueOf(row.get("config_key")), row -> String.valueOf(row.get("config_value"))));
    }

    @PatchMapping
    public Map<String, String> update(@RequestBody Map<String, String> values, @AuthenticationPrincipal Jwt jwt) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!ALLOWED.contains(key) || value == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported system configuration key: " + key);
            if (key.startsWith("password.require") || key.equals("social.enabled")) {
                if (!Set.of("true", "false").contains(value.toLowerCase())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " must be true or false");
                value = value.toLowerCase();
            } else {
                try {
                    int number = Integer.parseInt(value);
                    int minimum = key.equals("password.minLength") ? 8 : 1;
                    int maximum = key.equals("password.minLength") ? 128 : key.equals("token.accessMinutes") ? 1440 : 365;
                    if (number < minimum || number > maximum) throw new NumberFormatException();
                    value = Integer.toString(number);
                } catch (NumberFormatException ex) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, key + " is outside its allowed range");
                }
            }
            jdbc.update("INSERT INTO iam_system_config(config_key,config_value,updated_by,updated_at) VALUES (?,?,?,CURRENT_TIMESTAMP) ON CONFLICT (config_key) DO UPDATE SET config_value=EXCLUDED.config_value,updated_by=EXCLUDED.updated_by,updated_at=CURRENT_TIMESTAMP", key, value, jwt.getSubject());
        }
        return get();
    }
}
