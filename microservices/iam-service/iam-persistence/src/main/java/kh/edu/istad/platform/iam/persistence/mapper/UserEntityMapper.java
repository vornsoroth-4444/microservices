package kh.edu.istad.platform.iam.persistence.mapper;

import kh.edu.istad.platform.iam.domain.model.Address;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.persistence.entity.AddressEmbeddable;
import kh.edu.istad.platform.iam.persistence.entity.UserJpaEntity;

public final class UserEntityMapper {
    private UserEntityMapper() {}

    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        Address address = null;
        if (entity.getAddress() != null) {
            address = Address.builder()
                    .streetAddress(entity.getAddress().getStreetAddress())
                    .locality(entity.getAddress().getLocality())
                    .region(entity.getAddress().getRegion())
                    .postalCode(entity.getAddress().getPostalCode())
                    .country(entity.getAddress().getCountry())
                    .build();
        }

        return User.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .emailVerified(entity.isEmailVerified())
                .passwordHash(entity.getPasswordHash())
                .givenName(entity.getGivenName())
                .familyName(entity.getFamilyName())
                .middleName(entity.getMiddleName())
                .nickname(entity.getNickname())
                .gender(entity.getGender())
                .birthdate(entity.getBirthdate())
                .profileUrl(entity.getProfileUrl())
                .pictureUrl(entity.getPictureUrl())
                .websiteUrl(entity.getWebsiteUrl())
                .zoneinfo(entity.getZoneinfo())
                .locale(entity.getLocale())
                .phoneNumber(entity.getPhoneNumber())
                .phoneNumberVerified(entity.isPhoneNumberVerified())
                .address(address)
                .enabled(entity.isEnabled())
                .accountNonExpired(entity.isAccountNonExpired())
                .accountNonLocked(entity.isAccountNonLocked())
                .credentialsNonExpired(entity.isCredentialsNonExpired())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .lastLoginAt(entity.getLastLoginAt())
                .passwordChangedAt(entity.getPasswordChangedAt())
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

    public static UserJpaEntity toEntity(User domain) {
        if (domain == null) return null;
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setUuid(domain.getUuid());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setEmailVerified(domain.isEmailVerified());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setGivenName(domain.getGivenName());
        entity.setFamilyName(domain.getFamilyName());
        entity.setMiddleName(domain.getMiddleName());
        entity.setNickname(domain.getNickname());
        entity.setGender(domain.getGender());
        entity.setBirthdate(domain.getBirthdate());
        entity.setProfileUrl(domain.getProfileUrl());
        entity.setPictureUrl(domain.getPictureUrl());
        entity.setWebsiteUrl(domain.getWebsiteUrl());
        entity.setZoneinfo(domain.getZoneinfo());
        entity.setLocale(domain.getLocale());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setPhoneNumberVerified(domain.isPhoneNumberVerified());

        if (domain.getAddress() != null) {
            AddressEmbeddable addr = new AddressEmbeddable(
                    domain.getAddress().getStreetAddress(),
                    domain.getAddress().getLocality(),
                    domain.getAddress().getRegion(),
                    domain.getAddress().getPostalCode(),
                    domain.getAddress().getCountry()
            );
            entity.setAddress(addr);
        }

        entity.setEnabled(domain.isEnabled());
        entity.setAccountNonExpired(domain.isAccountNonExpired());
        entity.setAccountNonLocked(domain.isAccountNonLocked());
        entity.setCredentialsNonExpired(domain.isCredentialsNonExpired());
        entity.setFailedLoginAttempts(domain.getFailedLoginAttempts());
        entity.setLastLoginAt(domain.getLastLoginAt());
        entity.setPasswordChangedAt(domain.getPasswordChangedAt());
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
