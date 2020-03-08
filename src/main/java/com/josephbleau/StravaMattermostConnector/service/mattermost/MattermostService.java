package com.josephbleau.StravaMattermostConnector.service.mattermost;

import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import javastrava.model.StravaActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MattermostService {
    @Value("${mattermost.user.name}")
    private String mmUsername;

    @Value("${mattermost.user.icon-url}")
    private String mmUserIconUrl;

    @Value("${connector.approval-url}")
    private String approvalUrl;

    private final RestTemplate restTemplate;
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    public MattermostService(RestTemplate restTemplate, UserDetailsRepository userDetailsRepository) {
        this.restTemplate = restTemplate;
        this.userDetailsRepository = userDetailsRepository;
    }

    private MattermostPayloadDTO simpleTextPayload(String athleteKey, String message) {
        MattermostPayloadDTO mmPayload = new MattermostPayloadDTO();
        mmPayload.setChannel(userDetailsRepository.getUser(athleteKey).getMattermostDetails().getChannelName());
        mmPayload.setUsername(this.mmUsername);
        mmPayload.setIcon_url(this.mmUserIconUrl);
        mmPayload.setText(message);
        return mmPayload;
    }

    public void postActivity(StravaActivity activity) {
        String athleteId = String.valueOf(activity.getAthlete().getId());
        this.restTemplate.postForLocation(getMattermostPostEndpoint(athleteId), simpleTextPayload(athleteId, activity.getName()));
    }

    public void postAddRequest(String athleteKey, String code) {
        String message = "An athlete has requested to have their activities shared with this channel, click this link to approve: %s?code=%s&athleteKey=%s";

        String endpoint = getMattermostPostEndpoint(athleteKey);
        MattermostPayloadDTO payload = simpleTextPayload(athleteKey, String.format(message, approvalUrl, code, athleteKey));

        this.restTemplate.postForLocation(endpoint, payload);
    }

    private String getMattermostPostEndpoint(String athleteKey) {
        return userDetailsRepository.getUser(athleteKey).getMattermostDetails().getWebookUrl();
    }
}
