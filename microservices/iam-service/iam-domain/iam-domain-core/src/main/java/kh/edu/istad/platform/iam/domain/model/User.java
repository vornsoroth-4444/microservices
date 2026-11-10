package kh.edu.istad.platform.iam.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class User {
    private Long id;
    private UUID uuid;
    private String username;
    private String email;
    private boolean emailVerified;
    private String passwordHash;
    private String givenName;
    private String familyName;
    private String middleName;
    private String nickname;
    private String gender;
    private LocalDate birthdate;
    private String profileUrl;
    private String pictureUrl;
    private String websiteUrl;
    private String zoneinfo;
    private String locale;
    private String phoneNumber;
    private boolean phoneNumberVerified;
    private Address address;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private int failedLoginAttempts;
    private Instant lastLoginAt;
    private Instant passwordChangedAt;
    private boolean deleted;
    private Instant deletedAt;
    private String deletedBy;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private Long version;

    public User() {
    }

    public User(Long id, UUID uuid, String username, String email, boolean emailVerified, String passwordHash,
                String givenName, String familyName, String middleName, String nickname, String gender,
                LocalDate birthdate, String profileUrl, String pictureUrl, String websiteUrl, String zoneinfo,
                String locale, String phoneNumber, boolean phoneNumberVerified, Address address, boolean enabled,
                boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired,
                int failedLoginAttempts, Instant lastLoginAt, Instant passwordChangedAt, boolean deleted,
                Instant deletedAt, String deletedBy, Instant createdAt, String createdBy, Instant updatedAt,
                String updatedBy, Long version) {
        this.id = id;
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.emailVerified = emailVerified;
        this.passwordHash = passwordHash;
        this.givenName = givenName;
        this.familyName = familyName;
        this.middleName = middleName;
        this.nickname = nickname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.profileUrl = profileUrl;
        this.pictureUrl = pictureUrl;
        this.websiteUrl = websiteUrl;
        this.zoneinfo = zoneinfo;
        this.locale = locale;
        this.phoneNumber = phoneNumber;
        this.phoneNumberVerified = phoneNumberVerified;
        this.address = address;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lastLoginAt = lastLoginAt;
        this.passwordChangedAt = passwordChangedAt;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFullName() {
        String g = givenName != null ? givenName.trim() : "";
        String f = familyName != null ? familyName.trim() : "";
        if (!g.isEmpty() && !f.isEmpty()) {
            return g + " " + f;
        } else if (!g.isEmpty()) {
            return g;
        } else {
            return f;
        }
    }

    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.isBlank();
    }

    public void recordSuccessfulLogin() {
        this.failedLoginAttempts = 0;
        this.lastLoginAt = Instant.now();
        this.updatedAt = this.lastLoginAt;
    }

    public void recordFailedLogin(int maxConsecutiveAttempts) {
        this.failedLoginAttempts++;
        if (maxConsecutiveAttempts > 0 && this.failedLoginAttempts >= maxConsecutiveAttempts) {
            this.accountNonLocked = false;
        }
        this.updatedAt = Instant.now();
    }

    public void lock(String actor) {
        this.accountNonLocked = false;
        this.updatedAt = Instant.now();
        this.updatedBy = actor != null ? actor : "SYSTEM";
    }

    public void unlock(String actor) {
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
        this.updatedAt = Instant.now();
        this.updatedBy = actor != null ? actor : "SYSTEM";
    }

    public void enable(String actor) {
        this.enabled = true;
        this.updatedAt = Instant.now();
        this.updatedBy = actor != null ? actor : "SYSTEM";
    }

    public void disable(String actor) {
        this.enabled = false;
        this.updatedAt = Instant.now();
        this.updatedBy = actor != null ? actor : "SYSTEM";
    }

    public void softDelete(String actor) {
        this.deleted = true;
        this.deletedAt = Instant.now();
        this.deletedBy = actor != null ? actor : "SYSTEM";
        this.updatedAt = this.deletedAt;
        this.updatedBy = this.deletedBy;
    }

    public void changePassword(String newPasswordHash, String actor) {
        this.passwordHash = newPasswordHash;
        this.passwordChangedAt = Instant.now();
        this.updatedAt = this.passwordChangedAt;
        this.updatedBy = actor != null ? actor : "SYSTEM";
    }

    public void updateProfile(String givenName,
                              String familyName,
                              String middleName,
                              String nickname,
                              String gender,
                              LocalDate birthdate,
                              String profileUrl,
                              String pictureUrl,
                              String websiteUrl,
                              String zoneinfo,
                              String locale,
                              String phoneNumber,
                              Address address,
                              String actor) {
        if (givenName != null) this.givenName = givenName;
        if (familyName != null) this.familyName = familyName;
        if (middleName != null) this.middleName = middleName;
        if (nickname != null) this.nickname = nickname;
        if (gender != null) this.gender = gender;
        if (birthdate != null) this.birthdate = birthdate;
        if (profileUrl != null) this.profileUrl = profileUrl;
        if (pictureUrl != null) this.pictureUrl = pictureUrl;
        if (websiteUrl != null) this.websiteUrl = websiteUrl;
        if (zoneinfo != null) this.zoneinfo = zoneinfo;
        if (locale != null) this.locale = locale;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (address != null) this.address = address;
        this.updatedAt = Instant.now();
        this.updatedBy = actor != null ? actor : "SYSTEM";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }
    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public String getZoneinfo() { return zoneinfo; }
    public void setZoneinfo(String zoneinfo) { this.zoneinfo = zoneinfo; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public boolean isPhoneNumberVerified() { return phoneNumberVerified; }
    public void setPhoneNumberVerified(boolean phoneNumberVerified) { this.phoneNumberVerified = phoneNumberVerified; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isAccountNonExpired() { return accountNonExpired; }
    public void setAccountNonExpired(boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }
    public boolean isAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }
    public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    public void setCredentialsNonExpired(boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public Instant getPasswordChangedAt() { return passwordChangedAt; }
    public void setPasswordChangedAt(Instant passwordChangedAt) { this.passwordChangedAt = passwordChangedAt; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public static class Builder {
        private Long id;
        private UUID uuid;
        private String username;
        private String email;
        private boolean emailVerified = false;
        private String passwordHash;
        private String givenName;
        private String familyName;
        private String middleName;
        private String nickname;
        private String gender;
        private LocalDate birthdate;
        private String profileUrl;
        private String pictureUrl;
        private String websiteUrl;
        private String zoneinfo;
        private String locale = "en";
        private String phoneNumber;
        private boolean phoneNumberVerified = false;
        private Address address;
        private boolean enabled = true;
        private boolean accountNonExpired = true;
        private boolean accountNonLocked = true;
        private boolean credentialsNonExpired = true;
        private int failedLoginAttempts = 0;
        private Instant lastLoginAt;
        private Instant passwordChangedAt;
        private boolean deleted = false;
        private Instant deletedAt;
        private String deletedBy;
        private Instant createdAt;
        private String createdBy;
        private Instant updatedAt;
        private String updatedBy;
        private Long version = 0L;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder uuid(UUID uuid) { this.uuid = uuid; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder emailVerified(boolean emailVerified) { this.emailVerified = emailVerified; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder givenName(String givenName) { this.givenName = givenName; return this; }
        public Builder familyName(String familyName) { this.familyName = familyName; return this; }
        public Builder middleName(String middleName) { this.middleName = middleName; return this; }
        public Builder nickname(String nickname) { this.nickname = nickname; return this; }
        public Builder gender(String gender) { this.gender = gender; return this; }
        public Builder birthdate(LocalDate birthdate) { this.birthdate = birthdate; return this; }
        public Builder profileUrl(String profileUrl) { this.profileUrl = profileUrl; return this; }
        public Builder pictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; return this; }
        public Builder websiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; return this; }
        public Builder zoneinfo(String zoneinfo) { this.zoneinfo = zoneinfo; return this; }
        public Builder locale(String locale) { this.locale = locale; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder phoneNumberVerified(boolean phoneNumberVerified) { this.phoneNumberVerified = phoneNumberVerified; return this; }
        public Builder address(Address address) { this.address = address; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder accountNonExpired(boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; return this; }
        public Builder accountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; return this; }
        public Builder credentialsNonExpired(boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; return this; }
        public Builder failedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; return this; }
        public Builder lastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; return this; }
        public Builder passwordChangedAt(Instant passwordChangedAt) { this.passwordChangedAt = passwordChangedAt; return this; }
        public Builder deleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder deletedAt(Instant deletedAt) { this.deletedAt = deletedAt; return this; }
        public Builder deletedBy(String deletedBy) { this.deletedBy = deletedBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        public User build() {
            return new User(id, uuid, username, email, emailVerified, passwordHash, givenName, familyName,
                    middleName, nickname, gender, birthdate, profileUrl, pictureUrl, websiteUrl, zoneinfo,
                    locale, phoneNumber, phoneNumberVerified, address, enabled, accountNonExpired,
                    accountNonLocked, credentialsNonExpired, failedLoginAttempts, lastLoginAt,
                    passwordChangedAt, deleted, deletedAt, deletedBy, createdAt, createdBy, updatedAt,
                    updatedBy, version);
        }
    }
}
