package com.josephbleau.StravaMattermostConnector.service.strava;

import com.josephbleau.StravaMattermostConnector.config.StravaConfig;
import javastrava.auth.AuthorisationService;
import javastrava.auth.impl.AuthorisationServiceImpl;
import javastrava.auth.model.Token;
import javastrava.model.webhook.StravaEventSubscription;
import javastrava.service.Strava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StravaAuthenticationService {
    private Token authToken;

    @Value("${strava.oauth.authorize-url}")
    private String authorizeUrl;

    @Value("${strava.oauth.authorize-redirect-url}")
    private String authorizeRedirectUrl;

    private final AuthorisationService authorisationService;
    private final StravaConfig stravaConfig;

    private final Map<String, Token> tokensByCode;
    private final Map<Integer, Token> tokensByAthleteId;

    @Autowired
    public StravaAuthenticationService( StravaConfig stravaConfig) {
        this.authorisationService =  new AuthorisationServiceImpl();
        this.stravaConfig = stravaConfig;

        this.tokensByCode = new HashMap<>();
        this.tokensByAthleteId = new HashMap<>();
    }

    public Strava getAuthenticatedStravaByCode(String code) {
        if (this.tokensByCode.get(code) == null) {
            Token token = this.authorisationService.tokenExchange(stravaConfig.getClientId(), stravaConfig.getClientSecret(), code);

            this.tokensByCode.put(code, token);
            this.tokensByAthleteId.put(token.getAthlete().getId(), token);
        }

        return new Strava(this.tokensByCode.get(code));
    }

    public Strava getStravaByAthleteId(Integer athleteId) {
        return new Strava(this.tokensByAthleteId.get(athleteId));
    }

    public String getAuthorizeUrl() {
        return authorizeUrl
                .replace("{clientId}", String.valueOf(this.stravaConfig.getClientId()))
                .replace("{redirectUri}", this.authorizeRedirectUrl);
    }
}
