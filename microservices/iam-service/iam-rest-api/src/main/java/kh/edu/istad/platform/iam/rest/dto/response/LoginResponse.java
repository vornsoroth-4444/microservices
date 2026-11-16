package kh.edu.istad.platform.iam.rest.dto.response;

import java.util.List;
import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserPayload user
) {
    public record UserPayload(
            UUID uuid,
            String username,
            String email,
            String name,
            String givenName,
            String familyName,
            String phoneNumber,
            String pictureUrl,
            String locale,
            List<String> roles,
            List<String> permissions
    ) {}
}
