package com.josephbleau.StravaMattermostConnector.repository;

import com.josephbleau.StravaMattermostConnector.model.MattermostDetails;
import com.josephbleau.StravaMattermostConnector.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserDetailsRepository {
    private JedisPool jedisPool;

    private String[] userDetailFields = new String[]{
            "verified", "strava:token", "mattermost:url", "mattermost:port", "mattermost:hook-token", "mattermost:team-name", "mattermost:channel-name", "mattermost:user-name"
    };

    @Autowired
    public UserDetailsRepository(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void saveUser(UserDetails userDetails) {
        Map<String, String> userDetailsMap = new HashMap<>();

        if (userDetails == null) {
            throw new NullPointerException("UserDetails cannot be null.");
        }

        if (userDetails.getMattermostDetails() == null) {
            throw new NullPointerException("MattermostDetails inside of UserDetails cannot be null.");
        }

        userDetailsMap.put(userDetailFields[0], String.valueOf(userDetails.isVerified()));
        userDetailsMap.put(userDetailFields[2], userDetails.getMattermostDetails().getHost());
        userDetailsMap.put(userDetailFields[3], userDetails.getMattermostDetails().getPort());
        userDetailsMap.put(userDetailFields[4], userDetails.getMattermostDetails().getHookToken());
        userDetailsMap.put(userDetailFields[5], userDetails.getMattermostDetails().getTeamName());
        userDetailsMap.put(userDetailFields[6], userDetails.getMattermostDetails().getChannelName());
        userDetailsMap.put(userDetailFields[7], userDetails.getMattermostDetails().getUserName());

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hmset("user:" + userDetails.getAthleteKey(), userDetailsMap);
        }
    }

    public UserDetails getUser(String athleteKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> userDetailValues = jedis.hmget("user:" + athleteKey, userDetailFields);

            if (userDetailValues == null || userDetailValues.size() < userDetailFields.length) {
                return null;
            }

            MattermostDetails mattermostDetails = new MattermostDetails(
                    userDetailValues.get(2),
                    userDetailValues.get(3),
                    userDetailValues.get(4),
                    userDetailValues.get(5),
                    userDetailValues.get(6),
                    userDetailValues.get(7)
            );

            Boolean verified = Boolean.valueOf(userDetailValues.get(0));

            return new UserDetails(athleteKey, verified, mattermostDetails);
        }
    }

    public void deleteUser(String athleteKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("user:" + athleteKey);
        }
    }
}
