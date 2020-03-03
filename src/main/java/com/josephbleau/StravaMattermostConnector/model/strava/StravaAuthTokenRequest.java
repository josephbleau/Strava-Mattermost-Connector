package com.josephbleau.StravaMattermostConnector.model.strava;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.josephbleau.StravaMattermostConnector.config.StravaOauthDetails;

public class StravaAuthTokenRequest {
    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("grant_type")
    private String grantType;

    public StravaAuthTokenRequest(StravaOauthDetails oauthDetails) {
        this.clientId = oauthDetails.getClientId();
        this.clientSecret = oauthDetails.getClientSecret();
        this.refreshToken = oauthDetails.getRefreshToken();
        this.grantType = oauthDetails.getGrantType();
    }
}
