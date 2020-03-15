package com.josephbleau.StravaMattermostConnector.web.controller;

import com.josephbleau.StravaMattermostConnector.config.auth.VerificationCodeManager;
import com.josephbleau.StravaMattermostConnector.model.MattermostDetails;
import com.josephbleau.StravaMattermostConnector.model.UserDetails;
import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import com.josephbleau.StravaMattermostConnector.service.mattermost.MattermostService;
import com.josephbleau.StravaMattermostConnector.web.dto.VerificationCodeDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private MattermostService mattermostService;
    private UserDetailsRepository userDetailsRepository;
    private VerificationCodeManager verificationCodeManager;

    @Autowired
    public RegistrationController(
            MattermostService mattermostService,
            UserDetailsRepository userDetailsRepository, VerificationCodeManager verificationCodeManager) {
        this.verificationCodeManager = verificationCodeManager;
        this.mattermostService = mattermostService;
        this.userDetailsRepository = userDetailsRepository;
    }

    /**
     * Callback that is invoked when a user authorizes our application.
     */
    @GetMapping("/config")
    public String config(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        MattermostDetails mattermostDetails = new MattermostDetails();

        UserDetails userDetails = userDetailsRepository.getUser(oauth2User.getName());
        if (userDetails != null && userDetails.isVerified()) {
            mattermostDetails = userDetails.getMattermostDetails();
            model.addAttribute("nextStep", "/registration/end");
        } else {
            model.addAttribute("nextStep", "/registration/verify");
        }

        model.addAttribute("mattermostDetails", mattermostDetails);

        return "registration/config";
    }

    @RequestMapping(value = "/verify", method = {RequestMethod.GET, RequestMethod.POST})
    public String verify(@AuthenticationPrincipal OAuth2User oAuth2User, Model model, @ModelAttribute MattermostDetails mattermostDetails, @RequestParam(value = "error", required = false) String error)  {

        if (StringUtils.isEmpty(error)) {
            UserDetails userDetails = userDetailsRepository.getUser(oAuth2User.getName());
            userDetails.setAthleteKey(oAuth2User.getName());
            userDetails.setVerified(false);
            userDetails.setMattermostDetails(mattermostDetails);
            userDetailsRepository.saveUser(userDetails);

            mattermostService.sendVerificationCode(mattermostDetails, verificationCodeManager.getCode());
        }

        model.addAttribute("error", error);
        model.addAttribute("verificationCode", new VerificationCodeDTO());

        return "registration/verify";
    }

    @PostMapping("/check")
    public String check(Model model, @AuthenticationPrincipal OAuth2User oAuth2User, @ModelAttribute VerificationCodeDTO verificationCode) {
        if (verificationCodeManager.verify(verificationCode.getCode())) {
            UserDetails userDetails = userDetailsRepository.getUser(oAuth2User.getName());
            userDetails.setVerified(true);
            userDetailsRepository.saveUser(userDetails);

            return "redirect:/registration/end";
        } else {
            return "redirect:/registration/verify?error=invalid_code";
        }
    }

    @GetMapping("/end")
    public String endNewUser() {
        return "registration/end";
    }

    @PostMapping("/end")
    public String endExistingUser(@AuthenticationPrincipal OAuth2User oAuth2User, @ModelAttribute MattermostDetails mattermostDetails) {
        UserDetails userDetails = userDetailsRepository.getUser(oAuth2User.getName());
        userDetails.setMattermostDetails(mattermostDetails);
        userDetailsRepository.saveUser(userDetails);

        return "registration/end";
    }
}
