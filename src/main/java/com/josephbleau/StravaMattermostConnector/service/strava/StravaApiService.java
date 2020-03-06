package com.josephbleau.StravaMattermostConnector.service.strava;

import com.josephbleau.StravaMattermostConnector.config.StravaConfig;
import javastrava.auth.TokenManager;
import javastrava.auth.model.Token;
import javastrava.auth.ref.AuthorisationScope;
import javastrava.model.StravaActivity;
import javastrava.service.Strava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class StravaApiService {

    private StravaConfig stravaConfig;

    @Autowired
    public StravaApiService(StravaConfig stravaConfig) {
        this.stravaConfig = stravaConfig;
    }

    private Strava strava(int athleteId) {
        TokenManager tokenManager = TokenManager.instance();
        Token token = tokenManager.retrieveTokenWithExactScope(athleteId, new AuthorisationScope[]{AuthorisationScope.ACTIVITY_READ_ALL, AuthorisationScope.ACTIVITY_READ});
        return new Strava(token);
    }

    public StravaActivity getActivityForAthlete(long activityId, int athleteId) {
        return strava(athleteId).getActivity(activityId);
    }

    public String getAuthorizeUrl() {
        return stravaConfig.getAuthorizeUrl();
    }
}
