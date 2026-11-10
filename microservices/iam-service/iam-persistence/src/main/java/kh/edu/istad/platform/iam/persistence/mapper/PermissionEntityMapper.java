package kh.edu.istad.platform.iam.persistence.mapper;

import kh.edu.istad.platform.iam.domain.model.Permission;
import kh.edu.istad.platform.iam.persistence.entity.PermissionJpaEntity;

public final class PermissionEntityMapper {
    private PermissionEntityMapper() {}

    public static Permission toDomain(PermissionJpaEntity entity) {
        if (entity == null) return null;
        return Permission.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .name(entity.getName())
                .resource(entity.getResource())
                .action(entity.getAction())
                .description(entity.getDescription())
                .system(entity.isSystem())
                .active(entity.isActive())
                .deleted(entity.isDeleted())
                .deletedAt(entity.getDeletedAt())
                .deletedBy(entity.getDeletedBy())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .version(entity.getVersion())
                .build();
    }

    public static PermissionJpaEntity toEntity(Permission domain) {
        if (domain == null) return null;
        PermissionJpaEntity entity = new PermissionJpaEntity();
        entity.setId(domain.getId());
        entity.setUuid(domain.getUuid());
        entity.setName(domain.getName());
        entity.setResource(domain.getResource());
        entity.setAction(domain.getAction());
        entity.setDescription(domain.getDescription());
        entity.setSystem(domain.isSystem());
        entity.setActive(domain.isActive());
        entity.setDeleted(domain.isDeleted());
        entity.setDeletedAt(domain.getDeletedAt());
        entity.setDeletedBy(domain.getDeletedBy());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setVersion(domain.getVersion());
        return entity;
    }
}
