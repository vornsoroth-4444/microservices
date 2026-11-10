package kh.edu.istad.platform.iam.application.usecase.identity;

import kh.edu.istad.platform.iam.application.dto.IdentityResult;
import kh.edu.istad.platform.iam.domain.model.Identity;

public final class IdentityDtoMapper {
    private IdentityDtoMapper() {}

    public static IdentityResult toResult(Identity identity) {
        if (identity == null) return null;
        return new IdentityResult(
                identity.getUuid(),
                identity.getProvider(),
                identity.getEmail(),
                identity.getDisplayName(),
                identity.getUsername(),
                identity.getPictureUrl(),
                identity.getLinkedAt()
        );
    }
}
