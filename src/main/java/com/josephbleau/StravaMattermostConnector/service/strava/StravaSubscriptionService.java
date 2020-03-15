package com.josephbleau.StravaMattermostConnector.service.strava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StravaSubscriptionService {

    private final Integer subscriptionId;
    private final String subscriptionVerificationToken;

    @Autowired
    public StravaSubscriptionService(
            @Value("${strava.subscription.id}") final Integer subscriptionId,
            @Value("${strava.subscription.verify-token}") final String subscriptionVerificationToken) {
        this.subscriptionId = subscriptionId;
        this.subscriptionVerificationToken = subscriptionVerificationToken;
    }

    public boolean verifySubscriptionToken(final String verificationToken) {
        if (verificationToken == null) {
            return false;
        }

        return verificationToken.equals(subscriptionVerificationToken);
    }

}
