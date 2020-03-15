package com.josephbleau.stravamattermostconnector.service.strava;

import com.josephbleau.stravamattermostconnector.model.UserDetails;
import com.josephbleau.stravamattermostconnector.repository.UserDetailsRepository;
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

    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public StravaApiService(final UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    private Strava strava(final int athleteId) {
        UserDetails userDetails = userDetailsRepository.getUser(String.valueOf(athleteId));
        String stravaToken = userDetails.getStravaTokenDetails().getToken();

        Token token = new Token();
        token.setScopes(Arrays.asList(AuthorisationScope.ACTIVITY_READ_ALL, AuthorisationScope.READ));
        token.setToken(stravaToken);
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
