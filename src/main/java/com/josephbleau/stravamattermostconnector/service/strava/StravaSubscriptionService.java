package com.josephbleau.stravamattermostconnector.service.strava;

import com.josephbleau.stravamattermostconnector.config.auth.StravaOAuth2Details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class StravaSubscriptionService {

    private final StravaOAuth2Details stravaOAuth2Details;
    private final String callbackUrl;
    private final String verifyToken;
    private final boolean subscribeOnStartup;

    private final RestTemplate restTemplate;

    @Autowired
    public StravaSubscriptionService(
            final StravaOAuth2Details stravaOAuth2Details,
            @Value("${strava.subscription.callback-url}") final String callbackUrl,
            @Value("${strava.subscription.verify-token}") final String verifyToken,
            @Value("${strava.subscription.subscribe-on-startup}") final boolean subscribeOnStartup) {
        this.stravaOAuth2Details = stravaOAuth2Details;
        this.callbackUrl = callbackUrl;
        this.verifyToken = verifyToken;
        this.subscribeOnStartup = subscribeOnStartup;

        this.restTemplate = new RestTemplate();
    }

    public boolean verifySubscriptionToken(final String verificationToken) {
        if (verificationToken == null) {
            return false;
        }

        return verificationToken.equals(verificationToken);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() {
        if (subscribeOnStartup) {
            if(deleteInvalidSubscriptions()) {
                createSubscription();
            }
        }
    }

    private boolean deleteInvalidSubscriptions() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", stravaOAuth2Details.getClientId());
        parameters.put("client_secret", stravaOAuth2Details.getClientSecret());

        ResponseEntity<StravaEventSubscriptions> subscriptions = restTemplate.getForEntity(
                "https://www.strava.com/api/v3/push_subscriptions?client_id={client_id}&client_secret={client_secret}",
                StravaEventSubscriptions.class,
                parameters
        );

        if (subscriptions.getBody().isEmpty()) {
            return true;
        }

        for (StravaEventSubscription subscription : subscriptions.getBody()) {
            if (!subscription.getCallbackUrl().equals(callbackUrl)) {
                deleteSubscription(subscription);
                return true;
            }
        }

        return false;
    }

    private void deleteSubscription(StravaEventSubscription subscription) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", stravaOAuth2Details.getClientId());
        parameters.put("client_secret", stravaOAuth2Details.getClientSecret());
        parameters.put("id", subscription.getId());

        restTemplate.delete("https://www.strava.com/api/v3/push_subscriptions/{id}?client_id={client_id}&client_secret={client_secret}", parameters);
    }

    private void createSubscription() {
        StravaEventSubscriptionRequest stravaEventSubscriptionRequest = new StravaEventSubscriptionRequest();
        stravaEventSubscriptionRequest.setClientId(stravaOAuth2Details.getClientId());
        stravaEventSubscriptionRequest.setClientSecret(stravaOAuth2Details.getClientSecret());
        stravaEventSubscriptionRequest.setCallbackUrl(callbackUrl);
        stravaEventSubscriptionRequest.setVerifyToken(verifyToken);

        restTemplate.postForLocation("https://www.strava.com/api/v3/push_subscriptions", stravaEventSubscriptionRequest);
    }

    public boolean shouldSubscribeOnStartup() {
        return subscribeOnStartup;
    }
}
