package com.josephbleau.StravaMattermostConnector.service.strava;

import com.josephbleau.StravaMattermostConnector.model.strava.StravaAuthToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StravaOauthService {
    private final RestTemplate restTemplate;

    public StravaOauthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public StravaAuthToken getAccessToken() {
        return null;
    }
}
