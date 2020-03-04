package com.josephbleau.StravaMattermostConnector.startup;

import com.josephbleau.StravaMattermostConnector.model.PersistedToken;
import com.josephbleau.StravaMattermostConnector.repository.PersistedTokenRepository;
import javastrava.auth.TokenManager;
import javastrava.auth.model.Token;
import javastrava.model.StravaAthlete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class TokenLoader implements ApplicationListener<ContextRefreshedEvent> {
    private PersistedTokenRepository persistedTokenRepository;

    @Autowired
    public TokenLoader(PersistedTokenRepository persistedTokenRepository) {
        this.persistedTokenRepository = persistedTokenRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        TokenManager tokenManager = TokenManager.instance();

        for(PersistedToken persistedToken : persistedTokenRepository.findAll()) {
            Token token = new Token();
            token.setToken(persistedToken.getToken());
            StravaAthlete athlete = new StravaAthlete();
            athlete.setId(persistedToken.getAthleteId());
            token.setAthlete(new StravaAthlete());
            tokenManager.storeToken(token);
        }
    }
}
