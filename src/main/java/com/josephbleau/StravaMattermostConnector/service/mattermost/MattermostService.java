package com.josephbleau.StravaMattermostConnector.service.mattermost;

import javastrava.model.StravaActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    private final RestTemplate restTemplate;

    @Autowired
    public MattermostService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void postActivity(StravaActivity activity) {
        String url = mmUrl + ":" + mmPort + "/hooks/" + mmWebhookToken;

        MattermostPayloadDTO mmPayload = new MattermostPayloadDTO();
        mmPayload.setChannel(this.mmChannel);
        mmPayload.setUsername(this.mmUsername);
        mmPayload.setIcon_url(this.mmUserIconUrl);
        mmPayload.setText(activity.getName());

        ResponseEntity<Object> response = this.restTemplate.postForEntity(url, mmPayload, Object.class);

        System.out.println(response.getStatusCode());
    }
}
