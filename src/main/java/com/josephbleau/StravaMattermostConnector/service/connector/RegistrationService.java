package com.josephbleau.StravaMattermostConnector.service.connector;

import com.josephbleau.StravaMattermostConnector.config.StravaConfig;
import javastrava.auth.AuthorisationService;
import javastrava.auth.impl.AuthorisationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private StravaConfig stravaConfig;

    @Autowired
    public RegistrationService(StravaConfig stravaConfig) {
        this.stravaConfig = stravaConfig;
    }

    public void registerAthlete(String code) {
        AuthorisationService authorisationService = new AuthorisationServiceImpl();

        authorisationService.tokenExchange(
                stravaConfig.getClientId(),
                stravaConfig.getClientSecret(),
                code
        );
    }
}
