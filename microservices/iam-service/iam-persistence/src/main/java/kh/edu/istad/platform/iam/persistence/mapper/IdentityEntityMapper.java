package kh.edu.istad.platform.iam.persistence.mapper;

import kh.edu.istad.platform.iam.domain.model.Identity;
import kh.edu.istad.platform.iam.persistence.entity.IdentityJpaEntity;

public final class IdentityEntityMapper {
    private IdentityEntityMapper() {}

    public static Identity toDomain(IdentityJpaEntity entity) {
        if (entity == null) return null;
        return Identity.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .userId(entity.getUserId())
                .provider(entity.getProvider())
                .providerSubject(entity.getProviderSubject())
                .email(entity.getEmail())
                .emailVerified(entity.isEmailVerified())
                .displayName(entity.getDisplayName())
                .username(entity.getUsername())
                .pictureUrl(entity.getPictureUrl())
                .attributes(entity.getAttributes())
                .linkedAt(entity.getLinkedAt())
                .lastLoginAt(entity.getLastLoginAt())
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

    public static IdentityJpaEntity toEntity(Identity domain) {
        if (domain == null) return null;
        IdentityJpaEntity entity = new IdentityJpaEntity();
        entity.setId(domain.getId());
        entity.setUuid(domain.getUuid());
        entity.setUserId(domain.getUserId());
        entity.setProvider(domain.getProvider());
        entity.setProviderSubject(domain.getProviderSubject());
        entity.setEmail(domain.getEmail());
        entity.setEmailVerified(domain.isEmailVerified());
        entity.setDisplayName(domain.getDisplayName());
        entity.setUsername(domain.getUsername());
        entity.setPictureUrl(domain.getPictureUrl());
        entity.setAttributes(domain.getAttributes());
        entity.setLinkedAt(domain.getLinkedAt());
        entity.setLastLoginAt(domain.getLastLoginAt());
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
