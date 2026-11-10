package kh.edu.istad.platform.iam.application.dto;

import kh.edu.istad.platform.iam.domain.model.Address;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CreateUserCommand {
    private String username;
    private String email;
    private String password;
    private String givenName;
    private String familyName;
    private String gender;
    private LocalDate birthdate;
    private String phoneNumber;
    private String zoneinfo;
    private String locale;
    private Address address;
    private List<UUID> roleUuids;

    public CreateUserCommand() {}

    public static Builder builder() { return new Builder(); }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }
    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getZoneinfo() { return zoneinfo; }
    public void setZoneinfo(String zoneinfo) { this.zoneinfo = zoneinfo; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public List<UUID> getRoleUuids() { return roleUuids; }
    public void setRoleUuids(List<UUID> roleUuids) { this.roleUuids = roleUuids; }

    public static class Builder {
        private final CreateUserCommand target = new CreateUserCommand();
        public Builder username(String username) { target.setUsername(username); return this; }
        public Builder email(String email) { target.setEmail(email); return this; }
        public Builder password(String password) { target.setPassword(password); return this; }
        public Builder givenName(String givenName) { target.setGivenName(givenName); return this; }
        public Builder familyName(String familyName) { target.setFamilyName(familyName); return this; }
        public Builder gender(String gender) { target.setGender(gender); return this; }
        public Builder birthdate(LocalDate birthdate) { target.setBirthdate(birthdate); return this; }
        public Builder phoneNumber(String phoneNumber) { target.setPhoneNumber(phoneNumber); return this; }
        public Builder zoneinfo(String zoneinfo) { target.setZoneinfo(zoneinfo); return this; }
        public Builder locale(String locale) { target.setLocale(locale); return this; }
        public Builder address(Address address) { target.setAddress(address); return this; }
        public Builder roleUuids(List<UUID> roleUuids) { target.setRoleUuids(roleUuids); return this; }
        public CreateUserCommand build() { return target; }
    }
}
