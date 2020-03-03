package com.josephbleau.StravaMattermostConnector.model.strava;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.josephbleau.StravaMattermostConnector.config.StravaConfig;

public class StravaAuthTokenRequest {
    @JsonProperty("client_id")
    private Integer clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("scope")
    private String scope;

    public StravaAuthTokenRequest(StravaConfig stravaConfig) {
        this.clientId = stravaConfig.getClientId();
        this.clientSecret = stravaConfig.getClientSecret();
        this.refreshToken = stravaConfig.getRefreshToken();
        this.grantType = stravaConfig.getGrantType();
        this.scope = "read";
    }
}
