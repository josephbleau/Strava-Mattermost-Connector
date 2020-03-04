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
import org.springframework.web.servlet.view.RedirectView;

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

    @GetMapping("/register")
    public RedirectView register() {
        return new RedirectView(stravaApiService.getAuthorizeUrl());
    }

    @GetMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    public void auth(@RequestParam("code") String code) {
        mattermostService.postAddRequest(code);
    }

    @GetMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void add(@RequestParam("code") String code) {
        stravaApiService.registerAthlete(code);
    }

    @PostMapping(path = "/event")
    @ResponseStatus(HttpStatus.OK)
    public void event(@RequestBody StravaEventRequestDTO request) {
        if (StravaObjectTypeDTO.activity.equals(request.getObjectType())) {
            this.mattermostService.postActivity(stravaApiService.getActivityForAthlete(request.getObjectId(), request.getOwnerId().intValue()));
        }
    }

    @GetMapping(path = "/event")
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
