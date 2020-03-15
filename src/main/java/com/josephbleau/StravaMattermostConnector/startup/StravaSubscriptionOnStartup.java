package com.josephbleau.StravaMattermostConnector.startup;

import com.josephbleau.StravaMattermostConnector.service.strava.StravaSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * When the application starts we should register with Strava so that all events are sent
 * to us.
 */
@Component
public class StravaSubscriptionOnStartup  implements ApplicationListener<ContextRefreshedEvent> {

    private final StravaSubscriptionService stravaSubscriptionService;

    @Autowired
    public StravaSubscriptionOnStartup(final StravaSubscriptionService stravaSubscriptionService) {
        this.stravaSubscriptionService = stravaSubscriptionService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

    }

}
