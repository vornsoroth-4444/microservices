package kh.edu.istad.platform.iam.application.usecase.permission;

import kh.edu.istad.platform.iam.application.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.model.Permission;

public final class PermissionDtoMapper {
    private PermissionDtoMapper() {}

    public static PermissionResult toResult(Permission perm) {
        if (perm == null) return null;
        return PermissionResult.builder()
                .uuid(perm.getUuid())
                .name(perm.getName())
                .resource(perm.getResource())
                .action(perm.getAction())
                .description(perm.getDescription())
                .system(perm.isSystem())
                .active(perm.isActive())
                .createdAt(perm.getCreatedAt())
                .updatedAt(perm.getUpdatedAt())
                .build();
    }
}
