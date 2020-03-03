package com.josephbleau.StravaMattermostConnector.service.strava;

import com.josephbleau.StravaMattermostConnector.config.StravaConfig;
import com.josephbleau.StravaMattermostConnector.model.strava.StravaAuthToken;
import com.josephbleau.StravaMattermostConnector.model.strava.StravaAuthTokenRequest;
import javastrava.auth.AuthorisationService;
import javastrava.auth.impl.AuthorisationServiceImpl;
import javastrava.auth.model.Token;
import javastrava.service.Strava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StravaAuthenticationService {
    private Token authToken;

    @Value("${strava.oauth.authorize-url}")
    private String authorizeUrl;

    @Value("${strava.oauth.authorize-redirect-url}")
    private String authorizeRedirectUrl;

    private final RestTemplate restTemplate;
    private final StravaConfig stravaConfig;

    @Autowired
    public StravaAuthenticationService(RestTemplate restTemplate, StravaConfig stravaConfig) {
        this.restTemplate = restTemplate;
        this.stravaConfig = stravaConfig;
    }

    public Strava getAuthenticatedStrava() {
        AuthorisationService authorisationService = new AuthorisationServiceImpl();
        Token token = authorisationService.tokenExchange(stravaConfig.getClientId(), stravaConfig.getClientSecret(), stravaConfig.getCode());
        return new Strava(token);
    }

    public String getAuthorizeUrl() {
        String url = authorizeUrl
                .replace("{clientId}", String.valueOf(this.stravaConfig.getClientId()))
                .replace("{redirectUri}", this.authorizeRedirectUrl);

        return url;
    }
}
