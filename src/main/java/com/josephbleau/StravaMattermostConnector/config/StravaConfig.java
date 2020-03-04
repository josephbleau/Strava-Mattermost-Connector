package com.josephbleau.StravaMattermostConnector.config;

import javastrava.auth.AuthorisationService;
import javastrava.auth.impl.AuthorisationServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StravaConfig {
    @Value("${strava.oauth.token-endpoint}")
    private String tokenEndpoint;

    @Value("${strava.oauth.client-id}")
    private Integer clientId;

    @Value("${strava.oauth.client-secret}")
    private String clientSecret;

    @Value("${strava.oauth.refresh-token}")
    private String refreshToken;

    @Value("${strava.oauth.grant-type}")
    private String grantType;

    @Value("${strava.oauth.authorize-url}")
    private String authorizeUrl;

    @Value("${strava.oauth.authorize-redirect-url}")
    private String authorizeRedirectUrl;

    public String getAuthorizeUrl() {
        return authorizeUrl
                .replace("{clientId}", String.valueOf(getClientId()))
                .replace("{redirectUri}", this.authorizeRedirectUrl);
    }

    @Bean
    public AuthorisationService authorisationService() {
        return new AuthorisationServiceImpl();
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public Integer getClientId() {
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