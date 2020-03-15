package com.josephbleau.stravamattermostconnector.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StravaOAuth2Details {

    private final String clientId;
    private final String clientSecret;
    private final String scope;
    private final String authorizationUri;
    private final String tokenUri;
    private final String redirectUri;

    @Autowired
    public StravaOAuth2Details(
            @Value("${spring.security.oauth2.client.registration.strava.client-id}") final String clientId,
            @Value("${spring.security.oauth2.client.registration.strava.client-secret}") final String clientSecret,
            @Value("${spring.security.oauth2.client.registration.strava.scope}") final String scope,
            @Value("${spring.security.oauth2.client.registration.strava.authorization-uri}") final String authorizationUri,
            @Value("${spring.security.oauth2.client.registration.strava.token-uri}") final String tokenUri,
            @Value("${spring.security.oauth2.client.registration.strava.redirect-uri}") final String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.authorizationUri = authorizationUri;
        this.tokenUri = tokenUri;
        this.redirectUri = redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
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