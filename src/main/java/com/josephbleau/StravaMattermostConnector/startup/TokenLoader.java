package com.josephbleau.StravaMattermostConnector.startup;

import javastrava.auth.TokenManager;
import javastrava.auth.model.Token;
import javastrava.model.StravaAthlete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

@Component
public class TokenLoader implements ApplicationListener<ContextRefreshedEvent> {
    private JedisPool jedisPool;

    @Autowired
    public TokenLoader(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        TokenManager tokenManager = TokenManager.instance();

        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys("*"); // Get all keys (they're all athlete id's)

        for (String athleteIdAsString : keys) {
            String persistedToken = jedis.get(athleteIdAsString);
            Token token = new Token();
            token.setToken(persistedToken);
            StravaAthlete athlete = new StravaAthlete();
            athlete.setId(Integer.valueOf(athleteIdAsString));
            token.setAthlete(new StravaAthlete());
            tokenManager.storeToken(token);
        }
    }
}
