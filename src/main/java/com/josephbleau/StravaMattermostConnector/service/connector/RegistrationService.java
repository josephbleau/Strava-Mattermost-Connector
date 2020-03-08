package com.josephbleau.StravaMattermostConnector.service.connector;

import com.josephbleau.StravaMattermostConnector.config.StravaConfig;
import com.josephbleau.StravaMattermostConnector.repository.UserDetailsRepository;
import javastrava.auth.AuthorisationService;
import javastrava.auth.impl.AuthorisationServiceImpl;
import javastrava.auth.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private StravaConfig stravaConfig;
    private UserDetailsRepository userDetailsRepository;

    // Temporary variables, user will enter these
    private String host;
    private int port;
    private String hookToken;
    private String channelName;

    @Autowired
    public RegistrationService(
            StravaConfig stravaConfig,
            UserDetailsRepository userDetailsRepository,
            @Value("${mattermost.url}") String host,
            @Value("${mattermost.port}") int port,
            @Value("${mattermost.hook-token}") String hookToken,
            @Value("${mattermost.channel}") String channelName) {
        this.stravaConfig = stravaConfig;
        this.userDetailsRepository = userDetailsRepository;
        this.host = host;
        this.port = port;
        this.hookToken = hookToken;
        this.channelName = channelName;
    }

    public void registerTempUser(String code) {
        userDetailsRepository.saveUser(
                code,
                null,
                host,
                port,
                hookToken,
                channelName
        );
    }

    public void registerAthlete(String code) {
        AuthorisationService authorisationService = new AuthorisationServiceImpl();

        Token token = authorisationService.tokenExchange(
                stravaConfig.getClientId(),
                stravaConfig.getClientSecret(),
                code
        );

        userDetailsRepository.deleteUser(code);
        userDetailsRepository.saveUser(
                token.getAthlete().getId(),
                token.getToken(),
                host,
                port,
                hookToken,
                channelName
        );
    }
}
