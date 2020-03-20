package com.josephbleau.stravamattermostconnector.config;

import com.josephbleau.stravamattermostconnector.config.auth.StravaOAuth2Details;
import com.josephbleau.stravamattermostconnector.config.auth.StravaOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final StravaOAuth2Details stravaOAuth2Details;

    @Autowired
    public SecurityConfig(final StravaOAuth2Details stravaOAuth2Details) {
        this.stravaOAuth2Details = stravaOAuth2Details;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/strava/**", "/static/**", "/css/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/login", "/strava/**").permitAll()
                    .antMatchers("/**").authenticated()
                .and()
                .oauth2Login()
                    .userInfoEndpoint()
                        .userService(new StravaOAuth2UserService());
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(stravaClientRegistration());
    }

    private ClientRegistration stravaClientRegistration() {
        return ClientRegistration.withRegistrationId("strava")
                .clientId(stravaOAuth2Details.getClientId())
                .clientSecret(stravaOAuth2Details.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate(stravaOAuth2Details.getRedirectUri())
                .scope(stravaOAuth2Details.getScope())
                .authorizationUri(stravaOAuth2Details.getAuthorizationUri())
                .tokenUri(stravaOAuth2Details.getTokenUri())
                .clientName("Strava")
                .build();
    }

}
