package com.josephbleau.StravaMattermostConnector.service.mattermost;

import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import com.josephbleau.StravaMattermostConnector.service.google.StaticMapService;
import javastrava.model.StravaActivity;
import javastrava.model.StravaAthlete;
import net.bis5.mattermost.client4.hook.IncomingWebhookClient;
import net.bis5.mattermost.model.IncomingWebhookRequest;
import net.bis5.mattermost.model.SlackAttachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MattermostService {
    @Value("${mattermost.user.name}")
    private String mmUsername;

    @Value("${mattermost.user.icon-url}")
    private String mmUserIconUrl;

    @Value("${connector.approval-url}")
    private String approvalUrl;

    private UserDetailsRepository userDetailsRepository;
    private StaticMapService staticMapService;

    private static final String attachmentTemplate = "**%s**\n" +
            "\n" +
            "* **Distance:** %.2f mi\n" +
            "* **Pace:** %.2f mph\n" +
            "* **Duration:** %dh %02dm\n";

    @Autowired
    public MattermostService(UserDetailsRepository userDetailsRepository, StaticMapService staticMapService) {
        this.userDetailsRepository = userDetailsRepository;
        this.staticMapService = staticMapService;
    }

    private IncomingWebhookRequest simpleTextPayload(String athleteKey, String message) {
        IncomingWebhookRequest payload = new IncomingWebhookRequest();
        payload.setChannel(userDetailsRepository.getUser(athleteKey).getMattermostDetails().getChannelName());
        payload.setUsername(this.mmUsername);
        payload.setIconUrl(this.mmUserIconUrl);
        payload.setText(message);
        return payload;
    }

    private IncomingWebhookRequest activityPayload(StravaAthlete athlete, StravaActivity activity) {
        String text = String.format("%s completed a new activity!", athlete.getFirstname());

        int hours = activity.getMovingTime() / 60 / 60;
        int totalMinutes = activity.getMovingTime() / 60;
        int minutes = totalMinutes % 60;

        String attachmentText = String.format(
                attachmentTemplate,
                activity.getName(),
                activity.getDistance() / 1609.34,
                activity.getAverageSpeed() * 2.23694,
                hours, minutes
        );

        IncomingWebhookRequest payload = new IncomingWebhookRequest();
        SlackAttachment attachment = new SlackAttachment();
        attachment.setText(attachmentText);
        attachment.setImageUrl(staticMapService.generateStaticMap(activity));
        attachment.setColor("#fc5200");

        payload.setChannel(userDetailsRepository.getUser(String.valueOf(athlete.getId())).getMattermostDetails().getChannelName());
        payload.setText(text);
        payload.setAttachments(Arrays.asList(attachment));
        payload.setUsername(this.mmUsername);
        payload.setIconUrl(this.mmUserIconUrl);

        return payload;
    }

    public void postActivity(StravaAthlete athlete, StravaActivity activity) {
        String athleteId = String.valueOf(activity.getAthlete().getId());

        IncomingWebhookClient incomingWebhookClient = new IncomingWebhookClient(getMattermostPostEndpoint(athleteId));
        IncomingWebhookRequest payload = activityPayload(athlete, activity);

        incomingWebhookClient.postByIncomingWebhook(payload);
    }

    public void postAddRequest(String athleteKey, String code) {
        String message = String.format(
                "An athlete has requested to have their activities shared with this channel, click this link to approve: %s?code=%s&athleteKey=%s",
                approvalUrl, code, athleteKey
        );

        IncomingWebhookClient incomingWebhookClient = new IncomingWebhookClient(getMattermostPostEndpoint(athleteKey));
        IncomingWebhookRequest payload = simpleTextPayload(athleteKey, message);

        incomingWebhookClient.postByIncomingWebhook(payload);
    }

    private String getMattermostPostEndpoint(String athleteKey) {
        return userDetailsRepository.getUser(athleteKey).getMattermostDetails().getWebookUrl();
    }
}
