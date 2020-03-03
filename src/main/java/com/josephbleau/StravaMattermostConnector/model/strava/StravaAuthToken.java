package com.josephbleau.StravaMattermostConnector.model.strava;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StravaAuthToken {
    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_at")
    private Long expiresAt;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("athlete")
    private StravaAthlete athlete;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public StravaAthlete getAthlete() {
        return athlete;
    }

    public void setAthlete(StravaAthlete athlete) {
        this.athlete = athlete;
    }
}
