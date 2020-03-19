package com.josephbleau.stravamattermostconnector.repository;

import com.josephbleau.stravamattermostconnector.model.MattermostDetails;
import com.josephbleau.stravamattermostconnector.model.StravaTokenDetails;
import com.josephbleau.stravamattermostconnector.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserDetailsRepository {

    private final JedisPool jedisPool;
    private final StringRedisTemplate stringRedisTemplate;

    private final String[] userDetailFields = new String[]{
            "verified", "strava:token", "mattermost:url", "mattermost:port", "mattermost:hook-token",
            "mattermost:team-name", "mattermost:channel-name", "mattermost:user-name", "mattermost:hidden",
            "strava:token", "strava:refresh-token", "strava:expires-at"
    };

    @Autowired
    public UserDetailsRepository(final JedisPool jedisPool, StringRedisTemplate stringRedisTemplate) {
        this.jedisPool = jedisPool;
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
            userDetailsMap.put(userDetailFields[2], userDetails.getMattermostDetails().getHost());
        }

        if (userDetails.getMattermostDetails().getPort() != null) {
            userDetailsMap.put(userDetailFields[3], userDetails.getMattermostDetails().getPort());
        }

        if (userDetails.getMattermostDetails().getHookToken() != null) {
            userDetailsMap.put(userDetailFields[4], userDetails.getMattermostDetails().getHookToken());
        }

        if (userDetails.getMattermostDetails().getTeamName() != null) {
            userDetailsMap.put(userDetailFields[5], userDetails.getMattermostDetails().getTeamName());
        }

        if (userDetails.getMattermostDetails().getChannelName() != null) {
            userDetailsMap.put(userDetailFields[6], userDetails.getMattermostDetails().getChannelName());
        }

        userDetailsMap.put(userDetailFields[0], String.valueOf(userDetails.isVerified()));
        userDetailsMap.put(userDetailFields[7], userDetails.getMattermostDetails().getUserName());
        userDetailsMap.put(userDetailFields[8], String.valueOf(userDetails.getMattermostDetails().getHidden()));

        if (userDetails.getStravaTokenDetails() != null) {
            if (userDetails.getStravaTokenDetails().getToken() != null) {
                userDetailsMap.put(userDetailFields[9],userDetails.getStravaTokenDetails().getToken() );
            }

            if (userDetails.getStravaTokenDetails().getRefreshToken() != null) {
                userDetailsMap.put(userDetailFields[10], userDetails.getStravaTokenDetails().getRefreshToken());
            }

            userDetailsMap.put(userDetailFields[11], String.valueOf(userDetails.getStravaTokenDetails().getExpiresAt()));
        }

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hmset("user:" + userDetails.getAthleteKey(), userDetailsMap);
        }
    }

    public UserDetails getUser(String athleteKey) {
        Map<Object, Object> storedDetails = stringRedisTemplate.opsForHash().entries("user:" + athleteKey);

        UserDetails userDetails = new UserDetails();
        MattermostDetails mattermostDetails = new MattermostDetails();
        StravaTokenDetails stravaTokenDetails = new StravaTokenDetails();

        userDetails.setMattermostDetails(mattermostDetails);
        userDetails.setStravaTokenDetails(stravaTokenDetails);

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
        userDetails.setAthleteKey(athleteKey);

        return userDetails;
    }

    public void deleteUser(String athleteKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("user:" + athleteKey);
        }
    }

}
