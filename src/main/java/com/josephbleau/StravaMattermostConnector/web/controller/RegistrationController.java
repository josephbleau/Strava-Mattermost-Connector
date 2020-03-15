package com.josephbleau.StravaMattermostConnector.web.controller;

import com.bazaarvoice.jackson.rison.RisonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephbleau.StravaMattermostConnector.model.MattermostDetails;
import com.josephbleau.StravaMattermostConnector.model.UserDetails;
import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import com.josephbleau.StravaMattermostConnector.service.mattermost.MattermostService;
import com.josephbleau.StravaMattermostConnector.service.strava.StravaApiService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import retrofit.RetrofitError;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private StravaApiService stravaApiService;
    private MattermostService mattermostService;
    private UserDetailsRepository userDetailsRepository;

    @Value("${connector.base-url}")
    private String baseUrl;

    @Value("${connector.encryption-key}")
    private String encryptionKey;

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
     * Callback that is invoked when a user authorizes our application.
     */
    @GetMapping("/config")
    public String config(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        MattermostDetails mattermostDetails = new MattermostDetails();

        UserDetails userDetails = userDetailsRepository.getUser(oauth2User.getName());
        if (userDetails != null && userDetails.isVerified()) {
            mattermostDetails = userDetails.getMattermostDetails();
            model.addAttribute("nextStep", "/registration/finalize");
        } else {
            model.addAttribute("nextStep", "/registration/verify");
        }

        model.addAttribute("mattermostDetails", mattermostDetails);

        return "registration/config";
    }



    @PostMapping("/verify")
    public String verify(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @ModelAttribute MattermostDetails mattermostDetails)  {
        mattermostService.sendVerificationCode(mattermostDetails);
        return "registration/verify";
    }

    /**
     * Called when a user from matter-most verifies a registration.
     */
    @GetMapping("/finalize")
    public String finalize(Model model,
                           @RequestParam("code") String code,
                           @RequestParam("athleteKey") String athleteKey) throws JsonProcessingException, NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException {
        UserDetails userDetails = userDetailsRepository.getUser(athleteKey);
        userDetails.setVerified(true);
        userDetailsRepository.saveUser(userDetails);

        ObjectMapper objectMapper = new ObjectMapper(new RisonFactory());
        MattermostDetails mattermostDetails = userDetails.getMattermostDetails();
        mattermostDetails.setUserName(null);
        String settingsJson = objectMapper.writeValueAsString(mattermostDetails);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Key key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));

        byte[] encryptedSettings = cipher.doFinal(settingsJson.getBytes("UTF-8"));
        String encodedSettings = Base64.encodeBase64URLSafeString(encryptedSettings);

        model.addAttribute("shareSettingsLink", baseUrl + "?settings=" + encodedSettings);

        return "registration/finalize";
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
