package com.josephbleau.StravaMattermostConnector.service.strava;

import com.josephbleau.StravaMattermostConnector.config.StravaOauthDetails;
import com.josephbleau.StravaMattermostConnector.model.strava.StravaAuthToken;
import com.josephbleau.StravaMattermostConnector.model.strava.StravaAuthTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StravaOauthService {
    private StravaAuthToken authToken;

    private final RestTemplate restTemplate;
    private final StravaOauthDetails stravaOauthDetails;

    @Autowired
    public StravaOauthService(RestTemplate restTemplate, StravaOauthDetails stravaOauthDetails) {
        this.restTemplate = restTemplate;
        this.stravaOauthDetails = stravaOauthDetails;
    }

    public StravaAuthToken getAccessToken() {
        if (this.authToken == null) {
            HttpEntity<StravaAuthTokenRequest> requestEntity = new HttpEntity<>(new StravaAuthTokenRequest(stravaOauthDetails));
            HttpEntity<StravaAuthToken> token = this.restTemplate.postForEntity(stravaOauthDetails.getTokenEndpoint(), requestEntity, StravaAuthToken.class);
            this.authToken = token.getBody();
        }

        return this.authToken;
    }
}
