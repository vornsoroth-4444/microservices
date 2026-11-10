package kh.edu.istad.platform.iam.web.dto;

import java.util.List;

public class ConsentModel {
    private String clientId;
    private String clientName;
    private List<String> scopes;
    private String state;
    private String redirectUri;

    public ConsentModel() {}

    public ConsentModel(String clientId, String clientName, List<String> scopes, String state, String redirectUri) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.scopes = scopes;
        this.state = state;
        this.redirectUri = redirectUri;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public List<String> getScopes() { return scopes; }
    public void setScopes(List<String> scopes) { this.scopes = scopes; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
}
