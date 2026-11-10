package kh.edu.istad.platform.iam.application.dto;

import kh.edu.istad.platform.iam.domain.model.Address;

import java.time.LocalDate;
import java.util.UUID;

public class UpdateUserProfileCommand {
    private UUID userUuid;
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
    private Address address;

    public UpdateUserProfileCommand() {}

    public static Builder builder() { return new Builder(); }

    public UUID getUserUuid() { return userUuid; }
    public void setUserUuid(UUID userUuid) { this.userUuid = userUuid; }
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
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public static class Builder {
        private final UpdateUserProfileCommand target = new UpdateUserProfileCommand();
        public Builder userUuid(UUID userUuid) { target.setUserUuid(userUuid); return this; }
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
        public Builder address(Address address) { target.setAddress(address); return this; }
        public UpdateUserProfileCommand build() { return target; }
    }
}
