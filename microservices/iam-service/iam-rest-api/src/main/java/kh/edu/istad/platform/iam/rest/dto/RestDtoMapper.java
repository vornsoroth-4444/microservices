package kh.edu.istad.platform.iam.rest.dto;

import kh.edu.istad.platform.iam.application.dto.*;
import kh.edu.istad.platform.iam.rest.dto.response.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RestDtoMapper {
    private RestDtoMapper() {}

    public static UserResponse toUserResponse(UserResult result) {
        if (result == null) return null;
        List<RoleItemResponse> roles = result.getRoles() != null
                ? result.getRoles().stream().map(r -> new RoleItemResponse(r.getUuid(), r.getName())).toList()
                : Collections.emptyList();

        UserResponse response = new UserResponse(
                result.getUuid(),
                result.getUsername(),
                result.getEmail(),
                result.isEmailVerified(),
                result.getName(),
                result.getLocale(),
                result.isEnabled(),
                roles,
                result.getCreatedAt(),
                result.getUpdatedAt()
        );
        response.setGivenName(result.getGivenName());
        response.setFamilyName(result.getFamilyName());
        response.setAccountNonLocked(result.isAccountNonLocked());
        response.setPhoneNumber(result.getPhoneNumber());
        response.setPictureUrl(result.getPictureUrl());
        response.setProfileUrl(result.getProfileUrl());
        response.setLastLoginAt(result.getLastLoginAt());
        return response;
    }

    public static UserResponse fromSummary(UserSummary summary) {
        if (summary == null) return null;
        List<RoleItemResponse> roles = summary.getRoles() != null
                ? summary.getRoles().stream().map(r -> new RoleItemResponse(r.getUuid(), r.getName())).toList()
                : Collections.emptyList();

        UserResponse response = new UserResponse(
                summary.getUuid(),
                summary.getUsername(),
                summary.getEmail(),
                false,
                summary.getName(),
                summary.getLocale(),
                summary.isEnabled(),
                roles,
                summary.getCreatedAt(),
                summary.getUpdatedAt()
        );
        response.setAccountNonLocked(summary.isAccountNonLocked());
        response.setGivenName(summary.getName());
        response.setLastLoginAt(summary.getLastLoginAt());
        return response;
    }

    public static UserAuthoritiesResponse toUserAuthoritiesResponse(UserAuthoritiesResult result) {
        if (result == null) return null;
        List<UserAuthoritiesResponse.RoleAuthorityResponse> roles = result.getRoles() != null
                ? result.getRoles().stream().map(r -> new UserAuthoritiesResponse.RoleAuthorityResponse(r.getUuid(), r.getName(), r.getExpiresAt())).toList()
                : Collections.emptyList();

        List<UserAuthoritiesResponse.DirectPermissionResponse> direct = result.getDirectPermissions() != null
                ? result.getDirectPermissions().stream().map(d -> new UserAuthoritiesResponse.DirectPermissionResponse(d.getName(), d.getEffect())).toList()
                : Collections.emptyList();

        return new UserAuthoritiesResponse(roles, direct, result.getEffectivePermissions());
    }

    public static IdentityResponse toIdentityResponse(IdentityResult result) {
        if (result == null) return null;
        return new IdentityResponse(
                result.getUuid(),
                result.getProvider(),
                result.getEmail(),
                result.getDisplayName(),
                result.getLinkedAt()
        );
    }

    public static RoleResponse toRoleResponse(RoleResult result) {
        if (result == null) return null;
        return new RoleResponse(result.getUuid(), result.getName(), result.isActive());
    }

    public static RoleResponse toRoleResponse(RoleSummary summary) {
        if (summary == null) return null;
        return new RoleResponse(summary.getUuid(), summary.getName(), summary.isActive());
    }

    public static PermissionResponse toPermissionResponse(PermissionResult result) {
        if (result == null) return null;
        return new PermissionResponse(
                result.getUuid(),
                result.getName(),
                result.getResource(),
                result.getAction(),
                result.getDescription(),
                result.isSystem(),
                result.isActive()
        );
    }
}
