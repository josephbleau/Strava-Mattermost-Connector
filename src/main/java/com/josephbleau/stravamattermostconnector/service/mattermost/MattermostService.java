package com.josephbleau.stravamattermostconnector.service.mattermost;

import com.josephbleau.stravamattermostconnector.model.MattermostDetails;
import com.josephbleau.stravamattermostconnector.model.UserDetails;
import com.josephbleau.stravamattermostconnector.repository.UserDetailsRepository;
import com.josephbleau.stravamattermostconnector.service.google.StaticMapService;
import javastrava.model.StravaActivity;
import javastrava.model.StravaAthlete;
import net.bis5.mattermost.client4.hook.IncomingWebhookClient;
import net.bis5.mattermost.model.IncomingWebhookRequest;
import net.bis5.mattermost.model.SlackAttachment;
import net.bis5.mattermost.model.SlackAttachmentField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MattermostService {

    private final String mmUsername;
    private final String mmUserIconUrl;

    private final UserDetailsRepository userDetailsRepository;
    private final StaticMapService staticMapService;

    private static final String activityNameTemplate = "**%s**  \n";
    private static final String distanceTemplate = "%.2f %s";
    private static final String paceTemplate = "%.2f %s";
    private static final String durationTemplate = "%dh %02dm";

    private static final String mph = "mph";
    private static final String kph = "kph";
    private static final String km = "km";
    private static final String mi = "mi";

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
        UserDetails userDetails = userDetailsRepository.getUser(String.valueOf(athlete.getId()));
        IncomingWebhookRequest payload = new IncomingWebhookRequest();
        SlackAttachment attachment = new SlackAttachment();

        String text = String.format("%s completed a new activity!", athlete.getFirstname());

        int hours = activity.getMovingTime() / 60 / 60;
        int totalMinutes = activity.getMovingTime() / 60;
        int minutes = totalMinutes % 60;

        float imperialDistance = activity.getDistance() / 1609.34f;
        float metricDistance = activity.getDistance() / 1000.00f;
        float imperialSpeed = activity.getAverageSpeed() * 2.23694f;
        float metricSpeed = activity.getAverageSpeed() * 3.6f;

        String speedUnits = mph;
        String distanceUnits = mi;
        float distance = imperialDistance;
        float speed = imperialSpeed;

        if ("metric".equalsIgnoreCase(userDetails.getSharingDetails().getMeasurementSystem())) {
            speedUnits = kph;
            distanceUnits = km;
            distance = metricDistance;
            speed = metricSpeed;
        }

        String paceString = String.format(paceTemplate, speed, speedUnits);
        String distanceString = String.format(distanceTemplate, distance, distanceUnits);
        String durationString = String.format(durationTemplate, hours, minutes);

        if (userDetails.getSharingDetails().isShareRouteMap()) {
            attachment.setImageUrl(staticMapService.generateStaticMap(activity));
        }

        List<SlackAttachmentField> slackAttachmentFieldList = new ArrayList<>();
        SlackAttachmentField typeField = new SlackAttachmentField();
        typeField.setTitle("Activity");
        typeField.setValue(activity.getType().getDescription());
        typeField.setShortField(true);

        SlackAttachmentField distanceField = new SlackAttachmentField();
        distanceField.setTitle("Distance");
        distanceField.setValue(distanceString);
        distanceField.setShortField(true);

        SlackAttachmentField durationField = new SlackAttachmentField();
        durationField.setTitle("Duration");
        durationField.setValue(durationString);
        durationField.setShortField(true);

        SlackAttachmentField paceField = new SlackAttachmentField();
        paceField.setTitle("Pace");
        paceField.setValue(paceString);
        paceField.setShortField(true);

        slackAttachmentFieldList.add(typeField);
        slackAttachmentFieldList.add(durationField);
        slackAttachmentFieldList.add(distanceField);
        slackAttachmentFieldList.add(paceField);

        attachment.setColor("#fc5200");
        attachment.setFields(slackAttachmentFieldList);
        attachment.setTitle(activity.getName());

        payload.setChannel(userDetails.getMattermostDetails().getChannelName());
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

    public void sendVerificationCode(final MattermostDetails mattermostDetails, final String verificationCode) {
        final String message = String.format("Hey there, this is the Strava Mattermost Connector. Here is your verification code: %s", verificationCode);

        IncomingWebhookClient incomingWebhookClient = new IncomingWebhookClient(mattermostDetails.getWebookUrl());

        IncomingWebhookRequest payload = new IncomingWebhookRequest();
        payload.setChannel("@" + mattermostDetails.getUserName());
        payload.setUsername(this.mmUsername);
        payload.setIconUrl(this.mmUserIconUrl);
        payload.setText(message);

        incomingWebhookClient.postByIncomingWebhook(payload);
    }

    private String getMattermostPostEndpoint(final String athleteKey) {
        return userDetailsRepository.getUser(athleteKey).getMattermostDetails().getWebookUrl();
    }

}
