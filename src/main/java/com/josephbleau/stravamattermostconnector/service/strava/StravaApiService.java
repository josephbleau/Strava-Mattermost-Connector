package com.josephbleau.stravamattermostconnector.service.strava;

import com.josephbleau.stravamattermostconnector.model.UserDetails;
import com.josephbleau.stravamattermostconnector.repository.UserDetailsRepository;
import com.josephbleau.stravamattermostconnector.service.strava.oauth2.SimpleOAuth2TokenRefresher;
import javastrava.auth.model.Token;
import javastrava.auth.ref.AuthorisationScope;
import javastrava.model.StravaActivity;
import javastrava.model.StravaAthlete;
import javastrava.service.Strava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;

@Service
public class StravaApiService {

    private final UserDetailsRepository userDetailsRepository;
    private final SimpleOAuth2TokenRefresher simpleOAuth2TokenRefresher;

    @Autowired
    public StravaApiService(final UserDetailsRepository userDetailsRepository, SimpleOAuth2TokenRefresher simpleOAuth2TokenRefresher) {
        this.userDetailsRepository = userDetailsRepository;
        this.simpleOAuth2TokenRefresher = simpleOAuth2TokenRefresher;
    }

    private Strava strava(final int athleteId) {
        UserDetails userDetails = userDetailsRepository.getUser(String.valueOf(athleteId));
        String stravaToken = userDetails.getStravaTokenDetails().getToken();

        Instant expires = Instant.ofEpochSecond(userDetails.getStravaTokenDetails().getExpiresAt());

        Token token = new Token();

        if (expires.isAfter(Instant.now())) {
            token.setToken(stravaToken);
        } else {
            token.setToken(simpleOAuth2TokenRefresher.refreshTokenForAthlete(userDetails));
        }

        token.setScopes(Arrays.asList(AuthorisationScope.ACTIVITY_READ_ALL, AuthorisationScope.READ));
        token.setTokenType("Bearer");

        StravaAthlete stravaAthlete = new StravaAthlete();
        stravaAthlete.setId(Integer.parseInt(userDetails.getAthleteKey()));
        token.setAthlete(stravaAthlete);

        return new Strava(token);
    }


    public StravaActivity getActivityForAthlete(final long activityId, final int athleteId) {
        return strava(athleteId).getActivity(activityId);
    }

    public StravaAthlete getAthlete(final int athleteId) {
        return strava(athleteId).getAthlete(athleteId);
    }

}
