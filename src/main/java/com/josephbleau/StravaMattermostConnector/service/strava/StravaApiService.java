package com.josephbleau.StravaMattermostConnector.service.strava;

import com.josephbleau.StravaMattermostConnector.config.StravaConfig;
import javastrava.auth.model.Token;
import javastrava.auth.ref.AuthorisationScope;
import javastrava.model.StravaActivity;
import javastrava.model.StravaAthlete;
import javastrava.service.Strava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;

@Service
public class StravaApiService {

    private StravaConfig stravaConfig;
    private JedisPool jedisPool;

    @Autowired
    public StravaApiService(StravaConfig stravaConfig, JedisPool jedisPool) {
        this.stravaConfig = stravaConfig;
        this.jedisPool = jedisPool;
    }

    private Strava strava(int athleteId) {
        Jedis jedis = jedisPool.getResource();
        Token token = new Token();
        token.setScopes(Arrays.asList(AuthorisationScope.ACTIVITY_READ_ALL, AuthorisationScope.READ));
        token.setToken(jedis.get(String.valueOf(athleteId)));
        StravaAthlete stravaAthlete = new StravaAthlete();
        stravaAthlete.setId(athleteId);

        return new Strava(token);
    }

    public StravaActivity getActivityForAthlete(long activityId, int athleteId) {
        return strava(athleteId).getActivity(activityId);
    }

    public String getAuthorizeUrl() {
        return stravaConfig.getAuthorizeUrl();
    }
}
