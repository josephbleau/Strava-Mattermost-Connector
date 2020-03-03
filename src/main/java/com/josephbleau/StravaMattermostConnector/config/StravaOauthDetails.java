package com.josephbleau.StravaMattermostConnector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StravaOauthDetails {
    @Value("${strava.oauth.token-endpoint}")
    private String tokenEndpoint;

    @Value("${strava.oauth.client-id}")
    private Long clientId;

    @Value("${strava.oauth.client-secret}")
    private String clientSecret;

    @Value("${strava.oauth.refresh-token}")
    private String refreshToken;

    @Value("${strava.oauth.grant-type}")
    private String grantType;

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getGrantType() {
        return grantType;
    }
}
