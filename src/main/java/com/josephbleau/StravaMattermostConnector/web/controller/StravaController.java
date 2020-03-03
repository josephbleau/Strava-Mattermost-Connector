package com.josephbleau.StravaMattermostConnector.web.controller;

import com.josephbleau.StravaMattermostConnector.service.mattermost.MattermostService;
import com.josephbleau.StravaMattermostConnector.service.strava.StravaApiService;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaEventRequestDTO;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaObjectTypeDTO;
import com.josephbleau.StravaMattermostConnector.web.dto.StravaWebookChallengeResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    @GetMapping("/authorize")
    public RedirectView redirectWithUsingRedirectView() {
        return new RedirectView(stravaApiService.getAuthorizeUrl());
    }

    @GetMapping("/code")
    public void code(@RequestParam("code") String code) {
        System.out.println("code: " + code);
    }

    @PostMapping(path = "/event")
    @ResponseStatus(HttpStatus.OK)
    public void event(@RequestBody StravaEventRequestDTO request) {
        if (StravaObjectTypeDTO.activity.equals(request.getObjectType())) {
            this.mattermostService.postActivity(stravaApiService.getActivity(request.getObjectId()));
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
