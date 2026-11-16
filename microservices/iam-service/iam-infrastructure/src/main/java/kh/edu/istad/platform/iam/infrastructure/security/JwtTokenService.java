package kh.edu.istad.platform.iam.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;

    @Value("${spring.security.oauth2.authorizationserver.issuer:http://localhost:9000}")
    private String issuer;

    public JwtTokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(UUID userUuid,
                                String username,
                                String email,
                                String fullName,
                                List<String> roles,
                                Collection<String> permissions) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofHours(24)))
                .subject(userUuid.toString())
                .claim("preferred_username", username != null ? username : "")
                .claim("email", email != null ? email : "")
                .claim("name", fullName != null ? fullName : (username != null ? username : ""))
                .claim("roles", roles != null ? roles : List.of())
                .claim("permissions", permissions != null ? permissions : List.of())
                .id(UUID.randomUUID().toString());

        return jwtEncoder.encode(JwtEncoderParameters.from(builder.build())).getTokenValue();
    }
}
