package com.josephbleau.StravaMattermostConnector.service.strava;

import com.josephbleau.StravaMattermostConnector.model.strava.StravaActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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

    private final StravaOauthService stravaOauthService;
    private final RestTemplate restTemplate;

    @Autowired
    private StravaApiService(StravaOauthService stravaOauthService, RestTemplate restTemplate) {
        this.stravaOauthService = stravaOauthService;
        this.restTemplate = restTemplate;
    }

    MultiValueMap<String, String> getAuthHeader() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + stravaOauthService.getAccessToken().getAccessToken());
        return headers;
    }

    public StravaActivity getActivity(long activityId) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, getAuthHeader());
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("id", String.valueOf(activityId));
        HttpEntity<StravaActivity> activity = restTemplate.postForEntity(apiUrl + activityEndpoint, requestEntity, StravaActivity.class, pathParameters);
        return activity.getBody();
    }

    public boolean verifySubscriptionToken(String verificationToken) {
        if (verificationToken == null) {
            return false;
        }

        return verificationToken.equals(subscriptionVerificationToken);
    }
}
