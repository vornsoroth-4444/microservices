package kh.edu.istad.platform.iam.infrastructure.dcr;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class OidcClientRegistrationRequest {

    @JsonProperty("client_name")
    @JsonAlias({"clientName", "name"})
    private String clientName;

    @JsonProperty("redirect_uris")
    @JsonAlias({"redirect_uri", "redirectUris", "redirectUri"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> redirectUris = new ArrayList<>();

    @JsonProperty("grant_types")
    @JsonAlias({"grant_type", "grantTypes", "grantType"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> grantTypes = new ArrayList<>();

    @JsonProperty("response_types")
    @JsonAlias({"response_type", "responseTypes", "responseType"})
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> responseTypes = new ArrayList<>();

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("token_endpoint_auth_method")
    @JsonAlias({"tokenEndpointAuthMethod"})
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
