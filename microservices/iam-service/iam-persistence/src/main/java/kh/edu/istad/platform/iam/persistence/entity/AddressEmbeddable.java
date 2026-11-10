package kh.edu.istad.platform.iam.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {

    @Column(name = "address_street_address")
    private String streetAddress;

    @Column(name = "address_locality", length = 100)
    private String locality;

    @Column(name = "address_region", length = 100)
    private String region;

    @Column(name = "address_postal_code", length = 20)
    private String postalCode;

    @Column(name = "address_country", length = 2)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.CHAR)
    private String country;

    public AddressEmbeddable() {}

    public AddressEmbeddable(String streetAddress, String locality, String region, String postalCode, String country) {
        this.streetAddress = streetAddress;
        this.locality = locality;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
