package com.josephbleau.StravaMattermostConnector.web.controller;

import com.josephbleau.StravaMattermostConnector.model.MattermostDetails;
import com.josephbleau.StravaMattermostConnector.model.StravaApiDetails;
import com.josephbleau.StravaMattermostConnector.model.UserDetails;
import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import com.josephbleau.StravaMattermostConnector.service.mattermost.MattermostService;
import com.josephbleau.StravaMattermostConnector.service.strava.StravaApiService;
import javastrava.auth.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import retrofit.RetrofitError;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private StravaApiService stravaApiService;
    private MattermostService mattermostService;
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    public RegistrationController(
            StravaApiService stravaApiService,
            MattermostService mattermostService,
            UserDetailsRepository userDetailsRepository) {
        this.stravaApiService = stravaApiService;
        this.mattermostService = mattermostService;
        this.userDetailsRepository = userDetailsRepository;
    }

    /**
     * Redirect the user to the Strava app authorization view.
     */
    @GetMapping("/step1")
    public RedirectView stepOne() {
        return new RedirectView(stravaApiService.getAuthorizeUrl());
    }

    /**
     * Callback that is invoked when a user authorizes our application.
     */
    @GetMapping("/step2")
    public String stepTwo(Model model, @RequestParam("code") String code, @RequestParam("scope") String scope) {
        Token token = stravaApiService.exchangeCodeForToken(code);
        Integer athleteId = token.getAthlete().getId();

        if (athleteId != null) {
            String athleteKey = String.valueOf(athleteId);
            model.addAttribute("nextStep", String.format("/registration/step3?code=%s&athleteKey=%s&stravaToken=%s", code, athleteKey, token.getToken()));
            model.addAttribute("mattermostDetails", userDetailsRepository.getUser(athleteKey).getMattermostDetails());
        }

        return "registration/step2";
    }

    @PostMapping("/step3")
    public String stepThree(
            @RequestParam("code") String code,
            @RequestParam("athleteKey") String athleteKey,
            @RequestParam("stravaToken") String stravaToken,
            @ModelAttribute MattermostDetails mattermostDetails) {

        UserDetails userDetails = userDetailsRepository.getUser(athleteKey);

        if (userDetails.isVerified()) {
            userDetails.setMattermostDetails(mattermostDetails);
        } else {
            userDetails = new UserDetails(athleteKey, false, new StravaApiDetails(stravaToken), mattermostDetails);
        }

        userDetailsRepository.saveUser(userDetails);
        mattermostService.postAddRequest(athleteKey, code);

        return "registration/step3";
    }

    /**
     * Called when a user from matter-most verifies a registration.
     */
    @GetMapping("/step4")
    public String stepFour(@RequestParam("code") String code, @RequestParam("athleteKey") String athleteKey) {
        UserDetails userDetails = userDetailsRepository.getUser(athleteKey);
        userDetails.setVerified(true);
        userDetailsRepository.saveUser(userDetails);
        return "registration/step4";
    }

    /**
     * Called when a user logs in but is already registered
     */
    @GetMapping("/alreadyRegistered")
    public String alreadyRegistered() {
        return "";
    }

    /**
     * If during token exchange (code for auth token) an error occurs then we will fail silently and redirect the user
     * back to the main page. This should only occur when the token has been previously consumed (user is pressing back
     * or refreshing at an inappropriate time).
     */
    @ExceptionHandler({RetrofitError.class})
    public String tokenExchangeError() {
        return "index";
    }
}
