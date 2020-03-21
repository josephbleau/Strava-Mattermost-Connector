package com.josephbleau.stravamattermostconnector.repository;

import com.josephbleau.stravamattermostconnector.model.MattermostDetails;
import com.josephbleau.stravamattermostconnector.model.SharingDetails;
import com.josephbleau.stravamattermostconnector.model.StravaTokenDetails;
import com.josephbleau.stravamattermostconnector.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserDetailsRepository {

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public UserDetailsRepository(final StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void saveUser(UserDetails userDetails) {
        Map<String, String> userDetailsMap = new HashMap<>();

        if (userDetails == null) {
            throw new NullPointerException("UserDetails cannot be null.");
        }

        if (userDetails.getMattermostDetails() == null) {
            throw new NullPointerException("MattermostDetails inside of UserDetails cannot be null.");
        }

        if ( userDetails.getMattermostDetails().getHost() != null) {
            userDetailsMap.put("mattermost:url", userDetails.getMattermostDetails().getHost());
        }

        if (userDetails.getMattermostDetails().getPort() != null) {
            userDetailsMap.put("mattermost:port", userDetails.getMattermostDetails().getPort());
        }

        if (userDetails.getMattermostDetails().getHookToken() != null) {
            userDetailsMap.put("mattermost:hook-token", userDetails.getMattermostDetails().getHookToken());
        }

        if (userDetails.getMattermostDetails().getTeamName() != null) {
            userDetailsMap.put("mattermost:team-name", userDetails.getMattermostDetails().getTeamName());
        }

        if (userDetails.getMattermostDetails().getChannelName() != null) {
            userDetailsMap.put("mattermost:channel-name", userDetails.getMattermostDetails().getChannelName());
        }

        userDetailsMap.put("verified", String.valueOf(userDetails.isVerified()));
        userDetailsMap.put("mattermost:user-name", userDetails.getMattermostDetails().getUserName());
        userDetailsMap.put("mattermost:hidden", String.valueOf(userDetails.getMattermostDetails().getHidden()));

        if (userDetails.getStravaTokenDetails() != null) {
            if (userDetails.getStravaTokenDetails().getToken() != null) {
                userDetailsMap.put("strava:token" ,userDetails.getStravaTokenDetails().getToken() );
            }

            if (userDetails.getStravaTokenDetails().getRefreshToken() != null) {
                userDetailsMap.put("strava:refresh-token", userDetails.getStravaTokenDetails().getRefreshToken());
            }

            userDetailsMap.put("strava:expires-at", String.valueOf(userDetails.getStravaTokenDetails().getExpiresAt()));
        }

        userDetailsMap.put("sharing:measurement-system", userDetails.getSharingDetails().getMeasurementSystem());

        if (userDetails.getSharingDetails().isShareRunning()) {
            userDetailsMap.put("sharing:running", String.valueOf(userDetails.getSharingDetails().isShareRunning()));
        } else {
            userDetailsMap.put("sharing:running", "false");
        }

        if (userDetails.getSharingDetails().isShareBiking()) {
            userDetailsMap.put("sharing:biking", String.valueOf(userDetails.getSharingDetails().isShareBiking()));
        } else {
            userDetailsMap.put("sharing:biking", "false");
        }

        if (userDetails.getSharingDetails().isShareSwimming()) {
            userDetailsMap.put("sharing:swimming", String.valueOf(userDetails.getSharingDetails().isShareSwimming()));
        } else {
            userDetailsMap.put("sharing:swimming", "false");
        }

        if (userDetails.getSharingDetails().isShareDistance()) {
            userDetailsMap.put("sharing:distance", String.valueOf(userDetails.getSharingDetails().isShareDistance()));
        } else {
            userDetailsMap.put("sharing:distance", "false");
        }

        if (userDetails.getSharingDetails().isSharePace()) {
            userDetailsMap.put("sharing:pace", String.valueOf(userDetails.getSharingDetails().isSharePace()));
        } else {
            userDetailsMap.put("sharing:pace", "false");
        }

        if (userDetails.getSharingDetails().isShareDuration()) {
            userDetailsMap.put("sharing:duration", String.valueOf(userDetails.getSharingDetails().isShareDistance()));
        } else {
            userDetailsMap.put("sharing:duration", "false");
        }

        if (userDetails.getSharingDetails().isShareRouteMap()) {
            userDetailsMap.put("sharing:route-map", String.valueOf(userDetails.getSharingDetails().isShareRouteMap()));
        } else {
            userDetailsMap.put("sharing:route-map", "false");
        }

        stringRedisTemplate.opsForHash().putAll("user:" + userDetails.getAthleteKey(), userDetailsMap);
    }

    public UserDetails getUser(String athleteKey) {
        Map<Object, Object> storedDetails = stringRedisTemplate.opsForHash().entries("user:" + athleteKey);

        UserDetails userDetails = new UserDetails();
        MattermostDetails mattermostDetails = new MattermostDetails();
        StravaTokenDetails stravaTokenDetails = new StravaTokenDetails();
        SharingDetails sharingDetails = new SharingDetails();

        userDetails.setMattermostDetails(mattermostDetails);
        userDetails.setStravaTokenDetails(stravaTokenDetails);
        userDetails.setSharingDetails(sharingDetails);

        if (storedDetails.containsKey("mattermost:hidden")) {
            mattermostDetails.setHidden(Boolean.parseBoolean((String) storedDetails.get("mattermost:hidden")));
        }

        if (storedDetails.containsKey("verified")) {
            userDetails.setVerified(Boolean.parseBoolean((String) storedDetails.get("verified")));
        }

        if (storedDetails.containsKey("strava:expires-at")) {
            stravaTokenDetails.setExpiresAt(Integer.parseInt((String) storedDetails.get("strava:expires-at")));
        }

        mattermostDetails.setChannelName((String) storedDetails.get("mattermost:channel-name"));
        mattermostDetails.setHookToken((String) storedDetails.get("mattermost:hook-token"));
        mattermostDetails.setHost((String) storedDetails.get("mattermost:url"));
        mattermostDetails.setPort((String) storedDetails.get("mattermost:port"));
        mattermostDetails.setTeamName((String) storedDetails.get("mattermost:team-name"));
        mattermostDetails.setUserName((String) storedDetails.get("mattermost:user-name"));
        stravaTokenDetails.setToken((String) storedDetails.get("strava:token"));
        stravaTokenDetails.setRefreshToken((String) storedDetails.get("strava:refresh-token"));
        sharingDetails.setMeasurementSystem((String) storedDetails.get("sharing:measurement-system"));

        if (storedDetails.get("sharing:running") == null) {
            sharingDetails.setShareRunning(false);
        } else {
            sharingDetails.setShareRunning(Boolean.parseBoolean((String)storedDetails.get("sharing:running")));
        }

        if (storedDetails.get("sharing:biking") == null) {
            sharingDetails.setShareBiking(false);
        } else {
            sharingDetails.setShareBiking(Boolean.parseBoolean((String) storedDetails.get("sharing:biking")));
        }

        if (storedDetails.get("sharing:swimming") == null) {
            sharingDetails.setShareSwimming(false);
        } else {
            sharingDetails.setShareSwimming(Boolean.parseBoolean((String) storedDetails.get("sharing:swimming")));
        }

        if (storedDetails.get("sharing:distance") == null) {
            sharingDetails.setShareDistance(false);
        } else {
            sharingDetails.setShareDistance(Boolean.parseBoolean((String) storedDetails.get("sharing:distance")));
        }

        if (storedDetails.get("sharing:pace") == null) {
            sharingDetails.setSharePace(false);
        } else {
            sharingDetails.setSharePace(Boolean.parseBoolean((String) storedDetails.get("sharing:pace")));
        }

        if (storedDetails.get("sharing:duration") == null) {
            sharingDetails.setShareDuration(false);
        } else {
            sharingDetails.setShareDuration(Boolean.parseBoolean((String) storedDetails.get("sharing:duration")));
        }

        if (storedDetails.get("sharing:route-map") == null) {
            sharingDetails.setShareRouteMap(false);
        } else {
            sharingDetails.setShareRouteMap(Boolean.parseBoolean((String) storedDetails.get("sharing:route-map")));
        }

        userDetails.setAthleteKey(athleteKey);

        return userDetails;
    }

    public void deleteUser(String athleteKey) {
        stringRedisTemplate.delete("user:"+athleteKey);
    }

}
