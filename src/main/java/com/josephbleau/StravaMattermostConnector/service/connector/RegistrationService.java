package com.josephbleau.StravaMattermostConnector.service.connector;

import com.josephbleau.StravaMattermostConnector.config.StravaConfig;
import com.josephbleau.StravaMattermostConnector.model.PersistedToken;
import com.josephbleau.StravaMattermostConnector.repository.PersistedTokenRepository;
import javastrava.auth.AuthorisationService;
import javastrava.auth.impl.AuthorisationServiceImpl;
import javastrava.auth.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private StravaConfig stravaConfig;
    private PersistedTokenRepository persistedTokenRepository;

    @Autowired
    public RegistrationService(StravaConfig stravaConfig, PersistedTokenRepository persistedTokenRepository) {
        this.stravaConfig = stravaConfig;
        this.persistedTokenRepository = persistedTokenRepository;
    }

    public void registerAthlete(String code) {
        AuthorisationService authorisationService = new AuthorisationServiceImpl();

        Token token = authorisationService.tokenExchange(
                stravaConfig.getClientId(),
                stravaConfig.getClientSecret(),
                code
        );

        persistedTokenRepository.save(new PersistedToken(token.getAthlete().getId(), token.getToken()));
    }
}
