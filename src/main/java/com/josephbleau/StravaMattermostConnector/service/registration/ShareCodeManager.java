package com.josephbleau.StravaMattermostConnector.service.registration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josephbleau.StravaMattermostConnector.model.MattermostDetails;
import com.josephbleau.StravaMattermostConnector.model.UserDetails;
import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class ShareCodeManager {

    private final UserDetailsRepository userDetailsRepository;

    private final BasicTextEncryptor basicTextEncryptor;
    private final ObjectMapper objectMapper;

    @Autowired
    public ShareCodeManager(final UserDetailsRepository userDetailsRepository,
                            @Value("${connector.encryption-key}") final String encryptionKey) {
        this.userDetailsRepository = userDetailsRepository;

        basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPasswordCharArray(encryptionKey.toCharArray());

        objectMapper = new ObjectMapper();
    }

    /**
     * @return an encrypted text representation of the given users matter most settings. This code can be shared with
     * other users and provided to the endpoint /registration/share?code={code} and can be used to register new
     * users with the connector without revealing the mattermost hook token.
     */
    public String getCode(final String athleteId)  {
        UserDetails userDetails = userDetailsRepository.getUser(athleteId);
        MattermostDetails mattermostDetails = userDetails.getMattermostDetails();

        try {
            String mattermostDetailsJson = objectMapper.writeValueAsString(mattermostDetails);
            String encryptedDetails = basicTextEncryptor.encrypt(mattermostDetailsJson);

            return Base64.getUrlEncoder().encodeToString(encryptedDetails.getBytes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Something happened when trying to convert the mattermost details into JSON so they could be shared: " + e.getMessage());
        }
    }

    /**
     * @return mattermost settings after decrypting and deserializing the given code.
     */
    public MattermostDetails getSettings(final String code) {
       byte[] decoded = Base64.getUrlDecoder().decode(code.getBytes());

        try {
            String decrypted = basicTextEncryptor.decrypt(new String(decoded));
            MattermostDetails mattermostDetails = objectMapper.readValue(decrypted, MattermostDetails.class);
            mattermostDetails.setHidden(true);
            return mattermostDetails;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Something happened when converting our code back into matter most details: " + e.getMessage());
        }
    }

}
