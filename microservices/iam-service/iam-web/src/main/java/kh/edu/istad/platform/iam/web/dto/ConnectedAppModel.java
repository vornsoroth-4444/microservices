package kh.edu.istad.platform.iam.web.dto;

import java.util.Set;

public class ConnectedAppModel {
    private String clientId;
    private String clientName;
    private Set<String> scopes;

    public ConnectedAppModel() {}

    public ConnectedAppModel(String clientId, String clientName, Set<String> scopes) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.scopes = scopes;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Set<String> getScopes() { return scopes; }
    public void setScopes(Set<String> scopes) { this.scopes = scopes; }
}
