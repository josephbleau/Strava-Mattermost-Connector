package com.josephbleau.StravaMattermostConnector.service.connector;

import com.josephbleau.StravaMattermostConnector.config.StravaConfig;
import javastrava.auth.AuthorisationService;
import javastrava.auth.impl.AuthorisationServiceImpl;
import javastrava.auth.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

@Service
public class RegistrationService {
    private StravaConfig stravaConfig;
    private JedisPool jedisPool;

    @Autowired
    public RegistrationService(StravaConfig stravaConfig,JedisPool jedisPool) {
        this.stravaConfig = stravaConfig;
        this.jedisPool = jedisPool;
    }

    public void registerAthlete(String code) {
        AuthorisationService authorisationService = new AuthorisationServiceImpl();

        Token token = authorisationService.tokenExchange(
                stravaConfig.getClientId(),
                stravaConfig.getClientSecret(),
                code
        );

        jedisPool.getResource().set(String.valueOf(token.getAthlete().getId()), token.getToken());
    }
}
