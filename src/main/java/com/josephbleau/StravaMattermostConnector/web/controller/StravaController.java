package com.josephbleau.StravaMattermostConnector.web.controller;

import com.josephbleau.StravaMattermostConnector.service.mattermost.MattermostService;
import com.josephbleau.StravaMattermostConnector.service.strava.StravaApiService;
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

    @Autowired
    public StravaController(MattermostService mattermostService, StravaApiService stravaApiService) {
        this.mattermostService = mattermostService;
        this.stravaApiService = stravaApiService;
    }

    @RequestMapping(path = "/event", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void event(@RequestBody StravaEventRequestDTO request) {
        if (request.getObjectType() == StravaObjectTypeDTO.activity) {
            this.mattermostService.postActivity(stravaApiService.getActivity(request.getObjectId()));
        }
    }

    @RequestMapping(path = "/event", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody StravaWebookChallengeResponseDTO createWebhookSubscription(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token) {

        if (stravaApiService.verifySubscriptionToken(token)) {
            return new StravaWebookChallengeResponseDTO(challenge);
        }

        return null;
    }
}
