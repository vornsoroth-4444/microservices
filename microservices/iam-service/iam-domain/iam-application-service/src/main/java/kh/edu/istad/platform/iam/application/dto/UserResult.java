package kh.edu.istad.platform.iam.application.dto;

import kh.edu.istad.platform.iam.domain.model.Address;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class UserResult {
    private UUID uuid;
    private String username;
    private String email;
    private boolean emailVerified;
    private String name;
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
    private List<RoleSummary> roles;
    private Instant createdAt;
    private Instant updatedAt;

    public UserResult() {
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
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
    public List<RoleSummary> getRoles() { return roles; }
    public void setRoles(List<RoleSummary> roles) { this.roles = roles; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private final UserResult target = new UserResult();

        public Builder uuid(UUID uuid) { target.setUuid(uuid); return this; }
        public Builder username(String username) { target.setUsername(username); return this; }
        public Builder email(String email) { target.setEmail(email); return this; }
        public Builder emailVerified(boolean emailVerified) { target.setEmailVerified(emailVerified); return this; }
        public Builder name(String name) { target.setName(name); return this; }
        public Builder givenName(String givenName) { target.setGivenName(givenName); return this; }
        public Builder familyName(String familyName) { target.setFamilyName(familyName); return this; }
        public Builder middleName(String middleName) { target.setMiddleName(middleName); return this; }
        public Builder nickname(String nickname) { target.setNickname(nickname); return this; }
        public Builder gender(String gender) { target.setGender(gender); return this; }
        public Builder birthdate(LocalDate birthdate) { target.setBirthdate(birthdate); return this; }
        public Builder profileUrl(String profileUrl) { target.setProfileUrl(profileUrl); return this; }
        public Builder pictureUrl(String pictureUrl) { target.setPictureUrl(pictureUrl); return this; }
        public Builder websiteUrl(String websiteUrl) { target.setWebsiteUrl(websiteUrl); return this; }
        public Builder zoneinfo(String zoneinfo) { target.setZoneinfo(zoneinfo); return this; }
        public Builder locale(String locale) { target.setLocale(locale); return this; }
        public Builder phoneNumber(String phoneNumber) { target.setPhoneNumber(phoneNumber); return this; }
        public Builder phoneNumberVerified(boolean verified) { target.setPhoneNumberVerified(verified); return this; }
        public Builder address(Address address) { target.setAddress(address); return this; }
        public Builder enabled(boolean enabled) { target.setEnabled(enabled); return this; }
        public Builder accountNonExpired(boolean exp) { target.setAccountNonExpired(exp); return this; }
        public Builder accountNonLocked(boolean locked) { target.setAccountNonLocked(locked); return this; }
        public Builder credentialsNonExpired(boolean cred) { target.setCredentialsNonExpired(cred); return this; }
        public Builder roles(List<RoleSummary> roles) { target.setRoles(roles); return this; }
        public Builder createdAt(Instant createdAt) { target.setCreatedAt(createdAt); return this; }
        public Builder updatedAt(Instant updatedAt) { target.setUpdatedAt(updatedAt); return this; }

        public UserResult build() {
            return target;
        }
    }
}
