package com.josephbleau.StravaMattermostConnector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StravaOAuth2Details {
    @Value("${spring.security.oauth2.client.registration.strava.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.strava.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.strava.client-grant-type}")
    private String clientGrantType;

    @Value("${spring.security.oauth2.client.registration.strava.scope}")
    private String scope;

    @Value("${spring.security.oauth2.client.registration.strava.authorization-uri}")
    private String authorizationUri;

    @Value("${spring.security.oauth2.client.registration.strava.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.strava.redirect-uri}")
    private String redirectUri;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientGrantType() {
        return clientGrantType;
    }

    public String getScope() {
        return scope;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}