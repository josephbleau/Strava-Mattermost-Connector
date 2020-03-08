package com.josephbleau.StravaMattermostConnector.repository;

import com.josephbleau.StravaMattermostConnector.model.MattermostDetails;
import com.josephbleau.StravaMattermostConnector.model.StravaApiDetails;
import com.josephbleau.StravaMattermostConnector.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserDetailsRepository {
    private JedisPool jedisPool;

    private String[] userDetailFields = new String[]{
            "strava:token", "mattermost:url", "mattermost:port", "mattermost:hook-token", "mattermost:team-name", "mattermost:channel-name"
    };

    @Autowired
    public UserDetailsRepository(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void saveUser(String athleteKey, String stravaApiToken, String mattermostHost, int mattermostPort, String mattermostHookToken, String mattermostTeamName, String mattermostChannelName) {
        Map<String, String> userDetails = new HashMap<>();

        // Temp users wont have this yet, so don't send it.
        if (stravaApiToken != null) {
            userDetails.put(userDetailFields[0], stravaApiToken);
        }

        userDetails.put(userDetailFields[1], mattermostHost);
        userDetails.put(userDetailFields[2], String.valueOf(mattermostPort));
        userDetails.put(userDetailFields[3], mattermostHookToken);
        userDetails.put(userDetailFields[4], mattermostTeamName);
        userDetails.put(userDetailFields[5], mattermostChannelName);

        jedisPool.getResource().hmset("user:"+ athleteKey, userDetails);
    }

    public void saveUser(int athleteId, String stravaApiToken, String mattermostHost, int mattermostPort, String mattermostHookToken, String mattermostTeamName, String mattermostChannelName) {
        saveUser(String.valueOf(athleteId), stravaApiToken, mattermostHost, mattermostPort, mattermostHookToken, mattermostTeamName, mattermostChannelName);
    }

    public UserDetails getUser(String athleteKey) {
        List<String> userDetailValues = jedisPool.getResource().hmget("user:" + athleteKey, userDetailFields);

        MattermostDetails mattermostDetails = new MattermostDetails(
                userDetailValues.get(1),
                Integer.parseInt(userDetailValues.get(2)),
                userDetailValues.get(3),
                userDetailValues.get(4),
                userDetailValues.get(5)
        );

        StravaApiDetails stravaApiDetails = new StravaApiDetails(userDetailValues.get(0));

        return new UserDetails(athleteKey, stravaApiDetails, mattermostDetails);
    }

    public UserDetails getUser(int athleteId) {
        return getUser(String.valueOf(athleteId));
    }

    public void deleteUser(String athleteKey) {
        jedisPool.getResource().del("user:" + athleteKey);
    }
}
