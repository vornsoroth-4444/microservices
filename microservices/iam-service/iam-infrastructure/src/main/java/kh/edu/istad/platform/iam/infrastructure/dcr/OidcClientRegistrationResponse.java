package kh.edu.istad.platform.iam.infrastructure.dcr;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OidcClientRegistrationResponse {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("redirect_uris")
    private List<String> redirectUris;

    @JsonProperty("registration_access_token")
    private String registrationAccessToken;

    @JsonProperty("registration_client_uri")
    private String registrationClientUri;

    public OidcClientRegistrationResponse() {}

    public OidcClientRegistrationResponse(String clientId, String clientSecret, String clientName,
                                          List<String> redirectUris, String registrationAccessToken,
                                          String registrationClientUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientName = clientName;
        this.redirectUris = redirectUris;
        this.registrationAccessToken = registrationAccessToken;
        this.registrationClientUri = registrationClientUri;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public List<String> getRedirectUris() { return redirectUris; }
    public void setRedirectUris(List<String> redirectUris) { this.redirectUris = redirectUris; }
    public String getRegistrationAccessToken() { return registrationAccessToken; }
    public void setRegistrationAccessToken(String registrationAccessToken) { this.registrationAccessToken = registrationAccessToken; }
    public String getRegistrationClientUri() { return registrationClientUri; }
    public void setRegistrationClientUri(String registrationClientUri) { this.registrationClientUri = registrationClientUri; }
}
