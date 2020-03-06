package com.josephbleau.StravaMattermostConnector.service.mattermost;

import javastrava.model.StravaActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MattermostService {
    @Value("${mattermost.url}")
    private String mmUrl;

    @Value("${mattermost.port}")
    private String mmPort;

    @Value("${mattermost.hook-token}")
    private String mmWebhookToken;

    @Value("${mattermost.user.name}")
    private String mmUsername;

    @Value("${mattermost.user.icon-url}")
    private String mmUserIconUrl;

    @Value("${mattermost.channel}")
    private String mmChannel;

    @Value("${connector.approval-url}")
    private String approvalUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public MattermostService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getWebhookUrl() {
        return mmUrl + ":" + mmPort + "/hooks/" + mmWebhookToken;
    }

    private MattermostPayloadDTO simpleTextPayload(String message) {
        MattermostPayloadDTO mmPayload = new MattermostPayloadDTO();
        mmPayload.setChannel(this.mmChannel);
        mmPayload.setUsername(this.mmUsername);
        mmPayload.setIcon_url(this.mmUserIconUrl);
        mmPayload.setText(message);
        return mmPayload;
    }

    public void postActivity(StravaActivity activity) {
        this.restTemplate.postForEntity(getWebhookUrl(), simpleTextPayload(activity.getName()), Object.class);
    }

    public void postAddRequest(String code) {
        String message = "An athlete has requested to have their activities shared with this channel, click this link to approve: " +
                         approvalUrl + "?code=" + code;

        this.restTemplate.postForEntity(getWebhookUrl(), simpleTextPayload(message), Object.class);
    }

    // TODO: Implement per-user MM URL and channel configuration which will be set-up on registration
    public String getMattermostChannelUrlForAthlete(int athleteId) {
        return mmUrl + "channels/running";
    }
}
