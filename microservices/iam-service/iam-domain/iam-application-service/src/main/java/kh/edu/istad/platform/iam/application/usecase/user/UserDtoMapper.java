package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.RoleSummary;
import kh.edu.istad.platform.iam.application.dto.UserResult;
import kh.edu.istad.platform.iam.application.dto.UserSummary;
import kh.edu.istad.platform.iam.domain.model.User;

import java.util.List;

public final class UserDtoMapper {
    private UserDtoMapper() {}

    public static UserResult toResult(User user, List<RoleSummary> roles) {
        if (user == null) return null;
        return UserResult.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .name(user.getFullName())
                .givenName(user.getGivenName())
                .familyName(user.getFamilyName())
                .middleName(user.getMiddleName())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .birthdate(user.getBirthdate())
                .profileUrl(user.getProfileUrl())
                .pictureUrl(user.getPictureUrl())
                .websiteUrl(user.getWebsiteUrl())
                .zoneinfo(user.getZoneinfo())
                .locale(user.getLocale())
                .phoneNumber(user.getPhoneNumber())
                .phoneNumberVerified(user.isPhoneNumberVerified())
                .address(user.getAddress())
                .enabled(user.isEnabled())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static UserSummary toSummary(User user, List<RoleSummary> roles) {
        if (user == null) return null;
        return new UserSummary(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getLocale(),
                user.isEnabled(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
