package kh.edu.istad.platform.iam.persistence.mapper;

import kh.edu.istad.platform.iam.domain.model.RolePermission;
import kh.edu.istad.platform.iam.domain.model.UserPermission;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import kh.edu.istad.platform.iam.persistence.entity.RolePermissionJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.UserPermissionJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.UserRoleJpaEntity;

public final class AssignmentEntityMapper {
    private AssignmentEntityMapper() {}

    public static UserRole toDomain(UserRoleJpaEntity entity) {
        if (entity == null) return null;
        return UserRole.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .roleId(entity.getRoleId())
                .assignedAt(entity.getAssignedAt())
                .assignedBy(entity.getAssignedBy())
                .expiresAt(entity.getExpiresAt())
                .revokedAt(entity.getRevokedAt())
                .revokedBy(entity.getRevokedBy())
                .reason(entity.getReason())
                .build();
    }

    public static UserRoleJpaEntity toEntity(UserRole domain) {
        if (domain == null) return null;
        UserRoleJpaEntity entity = new UserRoleJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setRoleId(domain.getRoleId());
        entity.setAssignedAt(domain.getAssignedAt());
        entity.setAssignedBy(domain.getAssignedBy());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setRevokedAt(domain.getRevokedAt());
        entity.setRevokedBy(domain.getRevokedBy());
        entity.setReason(domain.getReason());
        return entity;
    }

    public static RolePermission toDomain(RolePermissionJpaEntity entity) {
        if (entity == null) return null;
        return RolePermission.builder()
                .id(entity.getId())
                .roleId(entity.getRoleId())
                .permissionId(entity.getPermissionId())
                .assignedAt(entity.getAssignedAt())
                .assignedBy(entity.getAssignedBy())
                .expiresAt(entity.getExpiresAt())
                .revokedAt(entity.getRevokedAt())
                .revokedBy(entity.getRevokedBy())
                .reason(entity.getReason())
                .build();
    }

    public static RolePermissionJpaEntity toEntity(RolePermission domain) {
        if (domain == null) return null;
        RolePermissionJpaEntity entity = new RolePermissionJpaEntity();
        entity.setId(domain.getId());
        entity.setRoleId(domain.getRoleId());
        entity.setPermissionId(domain.getPermissionId());
        entity.setAssignedAt(domain.getAssignedAt());
        entity.setAssignedBy(domain.getAssignedBy());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setRevokedAt(domain.getRevokedAt());
        entity.setRevokedBy(domain.getRevokedBy());
        entity.setReason(domain.getReason());
        return entity;
    }

    public static UserPermission toDomain(UserPermissionJpaEntity entity) {
        if (entity == null) return null;
        return UserPermission.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .permissionId(entity.getPermissionId())
                .effect(entity.getEffect())
                .assignedAt(entity.getAssignedAt())
                .assignedBy(entity.getAssignedBy())
                .expiresAt(entity.getExpiresAt())
                .revokedAt(entity.getRevokedAt())
                .revokedBy(entity.getRevokedBy())
                .reason(entity.getReason())
                .build();
    }

    public static UserPermissionJpaEntity toEntity(UserPermission domain) {
        if (domain == null) return null;
        UserPermissionJpaEntity entity = new UserPermissionJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setPermissionId(domain.getPermissionId());
        entity.setEffect(domain.getEffect());
        entity.setAssignedAt(domain.getAssignedAt());
        entity.setAssignedBy(domain.getAssignedBy());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setRevokedAt(domain.getRevokedAt());
        entity.setRevokedBy(domain.getRevokedBy());
        entity.setReason(domain.getReason());
        return entity;
    }
}
