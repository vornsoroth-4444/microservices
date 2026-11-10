package kh.edu.istad.platform.iam.application.usecase.role;

import kh.edu.istad.platform.iam.application.dto.PermissionResult;
import kh.edu.istad.platform.iam.application.dto.RoleResult;
import kh.edu.istad.platform.iam.application.dto.RoleSummary;
import kh.edu.istad.platform.iam.domain.model.Role;

import java.util.List;

public final class RoleDtoMapper {
    private RoleDtoMapper() {}

    public static RoleResult toResult(Role role, List<PermissionResult> permissions) {
        if (role == null) return null;
        return RoleResult.builder()
                .uuid(role.getUuid())
                .name(role.getName())
                .description(role.getDescription())
                .system(role.isSystem())
                .active(role.isActive())
                .permissions(permissions)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    public static RoleSummary toSummary(Role role) {
        if (role == null) return null;
        return new RoleSummary(role.getUuid(), role.getName(), role.isActive());
    }
}
