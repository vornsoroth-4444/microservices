package kh.edu.istad.platform.iam.infrastructure.dcr;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OidcClientRegistrationRequest {

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("redirect_uris")
    private List<String> redirectUris;

    @JsonProperty("grant_types")
    private List<String> grantTypes;

    @JsonProperty("response_types")
    private List<String> responseTypes;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("token_endpoint_auth_method")
    private String tokenEndpointAuthMethod;

    public OidcClientRegistrationRequest() {}

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public List<String> getRedirectUris() { return redirectUris; }
    public void setRedirectUris(List<String> redirectUris) { this.redirectUris = redirectUris; }
    public List<String> getGrantTypes() { return grantTypes; }
    public void setGrantTypes(List<String> grantTypes) { this.grantTypes = grantTypes; }
    public List<String> getResponseTypes() { return responseTypes; }
    public void setResponseTypes(List<String> responseTypes) { this.responseTypes = responseTypes; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getTokenEndpointAuthMethod() { return tokenEndpointAuthMethod; }
    public void setTokenEndpointAuthMethod(String tokenEndpointAuthMethod) { this.tokenEndpointAuthMethod = tokenEndpointAuthMethod; }
}
