package com.josephbleau.StravaMattermostConnector.web.controller;

import com.bazaarvoice.jackson.rison.RisonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephbleau.StravaMattermostConnector.model.MattermostDetails;
import com.josephbleau.StravaMattermostConnector.model.StravaApiDetails;
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
import retrofit.RetrofitError;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

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
    public String stepTwo(
            Model model,
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestParam(value = "settings", required = false) String settings)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        UserDetails userDetails = userDetailsRepository.getUser(oauth2User.getName()) ;
        MattermostDetails mattermostDetails = new MattermostDetails();

        if (userDetails != null) {
            mattermostDetails = userDetails.getMattermostDetails();
            model.addAttribute("nextStep", String.format("/registration/verify"));
        }

        if (settings != null && !"".equals(settings)) {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Key secretKey = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));

            byte[] decodedSettings = Base64.decodeBase64(settings);
            String decryptedSettings = new String(cipher.doFinal(decodedSettings));

            ObjectMapper objectMapper = new ObjectMapper(new RisonFactory());
            MattermostDetails copiedSettings = objectMapper.readValue(decryptedSettings, MattermostDetails.class);

            mattermostDetails.setTeamName(copiedSettings.getTeamName());
            mattermostDetails.setChannelName(copiedSettings.getChannelName());
            mattermostDetails.setHookToken(copiedSettings.getHookToken());
            mattermostDetails.setHost(copiedSettings.getHost());
            mattermostDetails.setPort(copiedSettings.getPort());
        }

        model.addAttribute("mattermostDetails", mattermostDetails);

        return "registration/config";
    }

    @PostMapping("/verify")
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

        return "registration/verify";
    }

    /**
     * Called when a user from matter-most verifies a registration.
     */
    @GetMapping("/finalize")
    public String stepFour(Model model,
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
