package com.josephbleau.StravaMattermostConnector.service.strava;

import com.josephbleau.StravaMattermostConnector.config.StravaConfig;
import com.josephbleau.StravaMattermostConnector.model.UserDetails;
import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import javastrava.auth.model.Token;
import javastrava.auth.ref.AuthorisationScope;
import javastrava.model.StravaActivity;
import javastrava.model.StravaAthlete;
import javastrava.service.Strava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class StravaApiService {

    private StravaConfig stravaConfig;
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    public StravaApiService(StravaConfig stravaConfig, UserDetailsRepository userDetailsRepository) {
        this.stravaConfig = stravaConfig;
        this.userDetailsRepository = userDetailsRepository;
    }

    private Strava strava(int athleteId) {
        UserDetails userDetails = userDetailsRepository.getUser(athleteId);

        Token token = new Token();
        token.setScopes(Arrays.asList(AuthorisationScope.ACTIVITY_READ_ALL, AuthorisationScope.READ));
        token.setToken(userDetails.getStravaApiDetails().getToken());
        token.setTokenType("Bearer");

        StravaAthlete stravaAthlete = new StravaAthlete();
        stravaAthlete.setId(athleteId);

        token.setAthlete(stravaAthlete);

        return new Strava(token);
    }

    public StravaActivity getActivityForAthlete(long activityId, int athleteId) {
        return strava(athleteId).getActivity(activityId);
    }

    public String getAuthorizeUrl() {
        return stravaConfig.getAuthorizeUrl();
    }
}
