package kh.edu.istad.platform.iam.persistence.mapper;

import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.persistence.entity.RoleJpaEntity;

public final class RoleEntityMapper {
    private RoleEntityMapper() {}

    public static Role toDomain(RoleJpaEntity entity) {
        if (entity == null) return null;
        return Role.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .name(entity.getName())
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

    public static RoleJpaEntity toEntity(Role domain) {
        if (domain == null) return null;
        RoleJpaEntity entity = new RoleJpaEntity();
        entity.setId(domain.getId());
        entity.setUuid(domain.getUuid());
        entity.setName(domain.getName());
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
