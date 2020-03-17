package com.josephbleau.stravamattermostconnector.service.strava.oauth2;

import com.josephbleau.stravamattermostconnector.config.auth.StravaOAuth2Details;
import com.josephbleau.stravamattermostconnector.model.UserDetails;
import com.josephbleau.stravamattermostconnector.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SimpleOAuth2TokenRefresher {

    private final UserDetailsRepository userDetailsRepository;
    private final StravaOAuth2Details stravaOAuth2Details;
    private final RestTemplate restTemplate;

    @Autowired
    public SimpleOAuth2TokenRefresher(UserDetailsRepository userDetailsRepository, StravaOAuth2Details stravaOAuth2Details) {
        this.userDetailsRepository = userDetailsRepository;
        this.stravaOAuth2Details = stravaOAuth2Details;
        this.restTemplate = new RestTemplate();
    }

    public String refreshTokenForAthlete(UserDetails userDetails) {
        StravaOAuth2RefreshTokenRequest request = new StravaOAuth2RefreshTokenRequest(
                stravaOAuth2Details.getClientId(),
                stravaOAuth2Details.getClientSecret(),
                "refresh_token",
                userDetails.getStravaTokenDetails().getRefreshToken()
        );

        StravaOAuth2RefreshTokenResponse response = restTemplate.postForObject(
                stravaOAuth2Details.getTokenUri(),
                request,
                StravaOAuth2RefreshTokenResponse.class
        );

        userDetails.getStravaTokenDetails().setToken(response.getAccessToken());
        userDetails.getStravaTokenDetails().setRefreshToken(response.getRefreshToken());
        userDetails.getStravaTokenDetails().setExpiresAt(response.getExpiresAt());

        userDetailsRepository.saveUser(userDetails);

        return userDetails.getStravaTokenDetails().getToken();
    }
}
