package com.josephbleau.StravaMattermostConnector.service.strava;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StravaSubscriptionService {
    @Value("${strava.subscription.id}")
    private Integer subscriptionId;

    @Value("${strava.subscription.verify-token}")
    private String subscriptionVerificationToken;

    public boolean verifySubscriptionToken(String verificationToken) {
        if (verificationToken == null) {
            return false;
        }

        return verificationToken.equals(subscriptionVerificationToken);
    }
}
