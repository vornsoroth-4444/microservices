package kh.edu.istad.platform.iam.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
public class WebTokenService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;

    public WebTokenService(@Autowired(required = false) JwtEncoder jwtEncoder,
                           @Value("${spring.security.oauth2.authorizationserver.issuer:http://localhost:9000}") String issuer) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
    }

    public String generateToken(UUID userUuid,
                                String username,
                                String email,
                                String fullName,
                                List<String> roles,
                                Collection<String> permissions) {
        if (jwtEncoder == null) return "";
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofHours(24)))
                .subject(userUuid.toString())
                .claim("preferred_username", username != null ? username : "")
                .claim("email", email != null ? email : "")
                .claim("name", fullName != null ? fullName : (username != null ? username : ""))
                .claim("roles", roles != null ? roles : List.of())
                .claim("permissions", permissions != null ? permissions : List.of())
                .id(UUID.randomUUID().toString())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
