package com.josephbleau.StravaMattermostConnector.web.controller;

import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import com.josephbleau.StravaMattermostConnector.service.mattermost.MattermostService;
import com.josephbleau.StravaMattermostConnector.service.strava.StravaApiService;
import com.josephbleau.StravaMattermostConnector.service.strava.StravaSubscriptionService;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaEventRequestDTO;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaObjectTypeDTO;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaWebookChallengeResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/strava")
public class StravaController {

    private MattermostService mattermostService;
    private StravaApiService stravaApiService;
    private StravaSubscriptionService stravaSubscriptionService;
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    public StravaController(
            MattermostService mattermostService,
            StravaApiService stravaApiService,
            StravaSubscriptionService stravaSubscriptionService, UserDetailsRepository userDetailsRepository) {
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
    public void event(@RequestBody StravaEventRequestDTO request) {
        if (StravaObjectTypeDTO.activity.equals(request.getObjectType())) {
            if (userDetailsRepository.getUser(String.valueOf(request.getOwnerId())).isVerified()) {
                mattermostService.postActivity(stravaApiService.getActivityForAthlete(request.getObjectId(), request.getOwnerId().intValue()));
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
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token) {

        if (stravaSubscriptionService.verifySubscriptionToken(token)) {
            return new StravaWebookChallengeResponseDTO(challenge);
        }

        return null;
    }
}
