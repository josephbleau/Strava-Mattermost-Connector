package com.josephbleau.StravaMattermostConnector.web.controller;

import com.josephbleau.StravaMattermostConnector.model.MattermostDetails;
import com.josephbleau.StravaMattermostConnector.model.UserDetails;
import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import com.josephbleau.StravaMattermostConnector.service.mattermost.MattermostService;
import com.josephbleau.StravaMattermostConnector.service.registration.ShareCodeManager;
import com.josephbleau.StravaMattermostConnector.service.registration.VerificationCodeManager;
import com.josephbleau.StravaMattermostConnector.web.dto.VerificationCodeDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private MattermostService mattermostService;
    private UserDetailsRepository userDetailsRepository;
    private VerificationCodeManager verificationCodeManager;
    private ShareCodeManager shareCodeManager;

    @Autowired
    public RegistrationController(
            MattermostService mattermostService,
            UserDetailsRepository userDetailsRepository,
            VerificationCodeManager verificationCodeManager,
            ShareCodeManager shareCodeManager) {
        this.verificationCodeManager = verificationCodeManager;
        this.mattermostService = mattermostService;
        this.userDetailsRepository = userDetailsRepository;
        this.shareCodeManager = shareCodeManager;
    }

    @GetMapping("/share")
    public String share(@AuthenticationPrincipal OAuth2User oAuth2User, @RequestParam("code") String code) {
        MattermostDetails mattermostDetails = shareCodeManager.getSettings(code);
        UserDetails userDetails = new UserDetails(oAuth2User.getName(), false, mattermostDetails);
        userDetailsRepository.saveUser(userDetails);

        return "redirect:/registration/config";
    }

    @GetMapping("/config")
    public String config(Model model,
                         @AuthenticationPrincipal OAuth2User oauth2User) {
        MattermostDetails mattermostDetails = new MattermostDetails();

        UserDetails userDetails = userDetailsRepository.getUser(oauth2User.getName());
        if (userDetails != null && userDetails.isVerified()) {
            mattermostDetails = userDetails.getMattermostDetails();
            model.addAttribute("nextStep", "/registration/end");
        } else if (userDetails != null && !userDetails.isVerified() && userDetails.getMattermostDetails().getHidden()) {
            mattermostDetails = userDetails.getMattermostDetails();
            model.addAttribute("nextStep", "/registration/verify");
        } else {
            model.addAttribute("nextStep", "/registration/verify");
        }

        model.addAttribute("mattermostDetails", mattermostDetails);

        return "registration/config";
    }

    @RequestMapping(value = "/verify", method = {RequestMethod.GET, RequestMethod.POST})
    public String verify(Model model,
                         @AuthenticationPrincipal OAuth2User oAuth2User,
                         @ModelAttribute MattermostDetails mattermostDetails,
                         @RequestParam(value = "error", required = false) String error)  {

        if (StringUtils.isEmpty(error)) {
            UserDetails userDetails = userDetailsRepository.getUser(oAuth2User.getName());
            MattermostDetails hiddenMatterMostDetails = userDetails.getMattermostDetails();

            userDetails.setAthleteKey(oAuth2User.getName());
            userDetails.setVerified(false);
            mattermostDetails.setHidden(hiddenMatterMostDetails.getHidden());
            userDetails.setMattermostDetails(mattermostDetails);

            userDetailsRepository.saveUser(userDetails);

            if (mattermostDetails.getHidden()) {
                mattermostService.sendVerificationCode(hiddenMatterMostDetails, verificationCodeManager.getCode());
            } else {
                mattermostService.sendVerificationCode(mattermostDetails, verificationCodeManager.getCode());
            }
        }

        model.addAttribute("error", error);
        model.addAttribute("verificationCode", new VerificationCodeDTO());

        return "registration/verify";
    }

    @PostMapping("/check")
    public String check(@AuthenticationPrincipal OAuth2User oAuth2User,
                        @ModelAttribute VerificationCodeDTO verificationCode) {
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
    public String endNewUser(Model model,
                             @AuthenticationPrincipal OAuth2User oAuth2User,
                             HttpServletRequest request) {
        String baseUrl = String.format("%s://%s:%d",request.getScheme(),  request.getServerName(), request.getServerPort());
        model.addAttribute("shareSettingsLink", baseUrl + "/registration/share?code=" + shareCodeManager.getCode(oAuth2User.getName()));
        return "registration/end";
    }

    @PostMapping("/end")
    public String endExistingUser(Model model,
                                  @AuthenticationPrincipal OAuth2User oAuth2User,
                                  @ModelAttribute MattermostDetails mattermostDetails,
                                  HttpServletRequest request) {
        UserDetails userDetails = userDetailsRepository.getUser(oAuth2User.getName());
        userDetails.setMattermostDetails(mattermostDetails);
        userDetailsRepository.saveUser(userDetails);

        String baseUrl = String.format("%s://%s:%d",request.getScheme(),  request.getServerName(), request.getServerPort());
        model.addAttribute("shareSettingsLink", baseUrl + "/registration/share?code=" + shareCodeManager.getCode(oAuth2User.getName()));
        return "registration/end";
    }
}
