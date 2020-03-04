package com.josephbleau.StravaMattermostConnector.service.strava;

import javastrava.model.StravaActivity;
import javastrava.service.Strava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StravaApiService {

    @Value("${strava.subscription.id}")
    private Integer subscriptionId;

    @Value("${strava.subscription.verify-token}")
    private String subscriptionVerificationToken;

    private final StravaAuthenticationService stravaAuthenticationService;

    @Autowired
    private StravaApiService(StravaAuthenticationService stravaAuthenticationService) {
        this.stravaAuthenticationService = stravaAuthenticationService;
    }

    public void registerAthlete(String code) {
        this.stravaAuthenticationService.getAuthenticatedStravaByCode(code);
    }

    public StravaActivity getActivityForAthlete(long activityId, int athleteId) {
        Strava strava = stravaAuthenticationService.getStravaByAthleteId(athleteId);
        return strava.getActivity(activityId);
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
