package com.josephbleau.stravamattermostconnector.web.controller;

import com.josephbleau.stravamattermostconnector.model.MattermostDetails;
import com.josephbleau.stravamattermostconnector.model.StravaTokenDetails;
import com.josephbleau.stravamattermostconnector.model.UserDetails;
import com.josephbleau.stravamattermostconnector.repository.UserDetailsRepository;
import com.josephbleau.stravamattermostconnector.service.mattermost.MattermostService;
import com.josephbleau.stravamattermostconnector.service.registration.ShareCodeManager;
import com.josephbleau.stravamattermostconnector.service.registration.VerificationCodeManager;
import com.josephbleau.stravamattermostconnector.web.dto.VerificationCodeDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final MattermostService mattermostService;
    private final UserDetailsRepository userDetailsRepository;
    private final VerificationCodeManager verificationCodeManager;
    private final ShareCodeManager shareCodeManager;
    private final OAuth2AuthorizedClientRepository clientRepository;

    @Autowired
    public RegistrationController(
            final MattermostService mattermostService,
            final UserDetailsRepository userDetailsRepository,
            final VerificationCodeManager verificationCodeManager,
            final ShareCodeManager shareCodeManager, OAuth2AuthorizedClientRepository clientRepository) {
        this.verificationCodeManager = verificationCodeManager;
        this.mattermostService = mattermostService;
        this.userDetailsRepository = userDetailsRepository;
        this.shareCodeManager = shareCodeManager;
        this.clientRepository = clientRepository;
    }

    @GetMapping("/share")
    public String share(
            @AuthenticationPrincipal final OAuth2User oAuth2User,
            @RequestParam("code") final String code,
            final HttpServletRequest request) {
        MattermostDetails mattermostDetails = shareCodeManager.getSettings(code);

        UserDetails userDetails = new UserDetails(oAuth2User.getName(), false, mattermostDetails);
        userDetails.setStravaTokenDetails(getTokenDetails(request));

        userDetailsRepository.saveUser(userDetails);

        return "redirect:/registration/config";
    }

    @GetMapping("/config")
    public String config(Model model,
                         @AuthenticationPrincipal final OAuth2User oauth2User) {
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
                         @AuthenticationPrincipal final OAuth2User oAuth2User,
                         @ModelAttribute MattermostDetails mattermostDetails,
                         @RequestParam(value = "error", required = false) final String error,
                         HttpServletRequest request)  {

        if (StringUtils.isEmpty(error)) {
            UserDetails userDetails = userDetailsRepository.getUser(oAuth2User.getName());
            MattermostDetails hiddenMatterMostDetails = userDetails.getMattermostDetails();

            userDetails.setAthleteKey(oAuth2User.getName());
            userDetails.setVerified(false);
            mattermostDetails.setHidden(hiddenMatterMostDetails.getHidden());
            userDetails.setMattermostDetails(mattermostDetails);
            userDetails.setStravaTokenDetails(getTokenDetails(request));

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
    public String check(@AuthenticationPrincipal final OAuth2User oAuth2User,
                        @ModelAttribute final VerificationCodeDTO verificationCode,
                        final HttpServletRequest request) {
        if (verificationCodeManager.verify(verificationCode.getCode())) {
            UserDetails userDetails = userDetailsRepository.getUser(oAuth2User.getName());
            userDetails.setVerified(true);
            userDetails.setStravaTokenDetails(getTokenDetails(request));
            userDetailsRepository.saveUser(userDetails);

            return "redirect:/registration/end";
        } else {
            return "redirect:/registration/verify?error=invalid_code";
        }
    }

    @GetMapping("/end")
    public String endNewUser(Model model,
                             @AuthenticationPrincipal final OAuth2User oAuth2User,
                             final HttpServletRequest request) {
        String baseUrl = String.format("%s://%s:%d",request.getScheme(),  request.getServerName(), request.getServerPort());
        model.addAttribute("shareSettingsLink", baseUrl + "/registration/share?code=" + shareCodeManager.getCode(oAuth2User.getName()));
        return "registration/end";
    }

    @PostMapping("/end")
    public String endExistingUser(Model model,
                                  @AuthenticationPrincipal final OAuth2User oAuth2User,
                                  @ModelAttribute final MattermostDetails mattermostDetails,
                                  final HttpServletRequest request) {
        UserDetails userDetails = userDetailsRepository.getUser(oAuth2User.getName());
        userDetails.setMattermostDetails(mattermostDetails);
        userDetails.setStravaTokenDetails(getTokenDetails(request));
        userDetailsRepository.saveUser(userDetails);

        String baseUrl = String.format("%s://%s:%d",request.getScheme(),  request.getServerName(), request.getServerPort());
        model.addAttribute("shareSettingsLink", baseUrl + "/registration/share?code=" + shareCodeManager.getCode(oAuth2User.getName()));
        return "registration/end";
    }

    private StravaTokenDetails getTokenDetails(HttpServletRequest servletRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        OAuth2AuthorizedClient client = this.clientRepository.loadAuthorizedClient(
                        "strava",
                        authentication,
                        servletRequest
        );

        StravaTokenDetails stravaTokenDetails = new StravaTokenDetails();
        stravaTokenDetails.setToken(client.getAccessToken().getTokenValue());
        stravaTokenDetails.setRefreshToken(client.getRefreshToken().getTokenValue());
        stravaTokenDetails.setExpiresAt(client.getAccessToken().getExpiresAt().toEpochMilli());

        return stravaTokenDetails;
    }
}
