package com.josephbleau.StravaMattermostConnector.service.mattermost;

import com.josephbleau.StravaMattermostConnector.model.MattermostDetails;
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

import java.util.Collections;

@Service
public class MattermostService {

    private final String mmUsername;
    private final String mmUserIconUrl;

    private final UserDetailsRepository userDetailsRepository;
    private final StaticMapService staticMapService;

    private static final String attachmentTemplate = "**%s**\n" +
            "\n" +
            "* **Distance:** %.2f mi\n" +
            "* **Pace:** %.2f mph\n" +
            "* **Duration:** %dh %02dm\n";

    @Autowired
    public MattermostService(
            @Value("${mattermost.user.name}") final String mmUsername,
            @Value("${mattermost.user.icon-url}") final String mmUserIconUrl,
            final UserDetailsRepository userDetailsRepository,
            final StaticMapService staticMapService) {
        this.mmUsername = mmUsername;
        this.mmUserIconUrl = mmUserIconUrl;
        this.userDetailsRepository = userDetailsRepository;
        this.staticMapService = staticMapService;
    }


    private IncomingWebhookRequest activityPayload(final StravaAthlete athlete, final StravaActivity activity) {
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
        payload.setAttachments(Collections.singletonList(attachment));
        payload.setUsername(this.mmUsername);
        payload.setIconUrl(this.mmUserIconUrl);

        return payload;
    }

    public void postActivity(final StravaAthlete athlete, final StravaActivity activity) {
        String athleteId = String.valueOf(activity.getAthlete().getId());

        IncomingWebhookClient incomingWebhookClient = new IncomingWebhookClient(getMattermostPostEndpoint(athleteId));
        IncomingWebhookRequest payload = activityPayload(athlete, activity);

        incomingWebhookClient.postByIncomingWebhook(payload);
    }

    public String sendVerificationCode(final MattermostDetails mattermostDetails, final String verificationCode) {
        final String message = String.format("Hey there, this is the Strava Mattermost Connector. Here is your verification code: %s", verificationCode);

        IncomingWebhookClient incomingWebhookClient = new IncomingWebhookClient(mattermostDetails.getWebookUrl());

        IncomingWebhookRequest payload = new IncomingWebhookRequest();
        payload.setChannel("@" + mattermostDetails.getUserName());
        payload.setUsername(this.mmUsername);
        payload.setIconUrl(this.mmUserIconUrl);
        payload.setText(message);

        incomingWebhookClient.postByIncomingWebhook(payload);

        return verificationCode;
    }

    private String getMattermostPostEndpoint(final String athleteKey) {
        return userDetailsRepository.getUser(athleteKey).getMattermostDetails().getWebookUrl();
    }

}
