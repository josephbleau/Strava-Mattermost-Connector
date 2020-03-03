package com.josephbleau.StravaMattermostConnector.service.strava;

import com.josephbleau.StravaMattermostConnector.model.strava.StravaActivity;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaAspectTypeDTO;
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

    private final RestTemplate restTemplate;

    @Autowired
    private StravaApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public StravaActivity getActivity(long activityId) {
        return null;
    }

    public boolean verifySubscriptionToken(String verificationToken) {
        if (verificationToken == null) {
            return false;
        }

        return verificationToken.equals(subscriptionVerificationToken);
    }
}
