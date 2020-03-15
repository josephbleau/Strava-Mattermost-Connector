package com.josephbleau.StravaMattermostConnector.service.strava;

import javastrava.auth.model.Token;
import javastrava.auth.ref.AuthorisationScope;
import javastrava.model.StravaActivity;
import javastrava.model.StravaAthlete;
import javastrava.service.Strava;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class StravaApiService {

    private Strava strava() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        OAuth2User oAuth2User = (OAuth2User) securityContext.getAuthentication().getPrincipal();

        Token token = new Token();
        token.setScopes(Arrays.asList(AuthorisationScope.ACTIVITY_READ_ALL, AuthorisationScope.READ));
        token.setToken((String) oAuth2User.getAttributes().get("token"));
        token.setTokenType("Bearer");

        StravaAthlete stravaAthlete = new StravaAthlete();
        stravaAthlete.setId(Integer.parseInt(oAuth2User.getName()));
        token.setAthlete(stravaAthlete);

        return new Strava(token);
    }


    public StravaActivity getActivityForAthlete(long activityId, int athleteId) {
        return strava().getActivity(activityId);
    }

    public StravaAthlete getAthlete(int athleteId) {
        return strava().getAthlete(athleteId);
    }
}
