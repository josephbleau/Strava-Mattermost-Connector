package com.josephbleau.StravaMattermostConnector.service.strava;

import javastrava.model.StravaActivity;
import javastrava.service.Strava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StravaApiService {

    @Value("${strava.subscription.id}")
    private Integer subscriptionId;

    @Value("${strava.subscription.verify-token}")
    private String subscriptionVerificationToken;

    @Value("${strava.api.url}")
    private String apiUrl;

    @Value("${strava.api.activity-endpoint}")
    private String activityEndpoint;



    private final StravaAuthenticationService stravaAuthenticationService;

    @Autowired
    private StravaApiService(StravaAuthenticationService stravaAuthenticationService) {
        this.stravaAuthenticationService = stravaAuthenticationService;
    }

    public StravaActivity getActivity(long activityId) {
        Strava strava = stravaAuthenticationService.getAuthenticatedStrava();
        StravaActivity activity = strava.getActivity(activityId);
        return activity;
    }

    public boolean verifySubscriptionToken(String verificationToken) {
        if (verificationToken == null) {
            return false;
        }

        return verificationToken.equals(subscriptionVerificationToken);
    }

    public String getAuthorizeUrl() {
        return this.stravaAuthenticationService.getAuthorizeUrl();
    }
}
