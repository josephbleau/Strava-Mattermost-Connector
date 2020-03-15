package com.josephbleau.StravaMattermostConnector.web.controller;

import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import com.josephbleau.StravaMattermostConnector.service.mattermost.MattermostService;
import com.josephbleau.StravaMattermostConnector.service.strava.StravaApiService;
import com.josephbleau.StravaMattermostConnector.service.strava.StravaSubscriptionService;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaEventRequestDTO;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaObjectTypeDTO;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaWebookChallengeResponseDTO;
import javastrava.model.StravaActivity;
import javastrava.model.StravaAthlete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/strava")
public class StravaController {

    private final MattermostService mattermostService;
    private final StravaApiService stravaApiService;
    private final StravaSubscriptionService stravaSubscriptionService;
    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public StravaController(
            final MattermostService mattermostService,
            final StravaApiService stravaApiService,
            final StravaSubscriptionService stravaSubscriptionService, UserDetailsRepository userDetailsRepository) {
        this.mattermostService = mattermostService;
        this.stravaApiService = stravaApiService;
        this.stravaSubscriptionService = stravaSubscriptionService;
        this.userDetailsRepository = userDetailsRepository;
    }

    /**
     * Strava Webhook callback. This endpoint is called whenever an activity is created, updated, or deleted.
     */
    @PostMapping(path = "/event")
    @ResponseStatus(HttpStatus.OK)
    public void event(@RequestBody final StravaEventRequestDTO request) {
        if (StravaObjectTypeDTO.activity.equals(request.getObjectType())) {
            if (userDetailsRepository.getUser(String.valueOf(request.getOwnerId())).isVerified()) {
                StravaActivity activity = stravaApiService.getActivityForAthlete(request.getObjectId(), request.getOwnerId().intValue());
                StravaAthlete athlete = stravaApiService.getAthlete(request.getOwnerId().intValue());

                mattermostService.postActivity(athlete, activity);
            }
        }
    }

    /**
     * Strava Subscription challenge callback. When a new webhook subscription is requested this endpoint is provided
     * and echos the challenge payload back to the caller.
     */
    @GetMapping(path = "/event")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody StravaWebookChallengeResponseDTO createWebhookSubscription(
            @RequestParam("hub.mode") final String mode,
            @RequestParam("hub.challenge") final String challenge,
            @RequestParam("hub.verify_token") final String token) {

        if (stravaSubscriptionService.verifySubscriptionToken(token)) {
            return new StravaWebookChallengeResponseDTO(challenge);
        }

        return null;
    }

}
