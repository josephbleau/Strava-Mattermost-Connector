package com.josephbleau.StravaMattermostConnector.config.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates an OAuth2User with authorities set to the granted scopes and
 * the username set to the athlete id.
 */
public class StravaOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String athleteIdPatternString = "\\bid=(\\d*)";
    private static final Pattern athleteIdPattern = Pattern.compile(athleteIdPatternString, Pattern.MULTILINE);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Extract the athlete id from the additional parameters (currently being stored as a "almost json"
        // string so we're using regex to pull it out.
        String athleteDetails = (String) userRequest.getAdditionalParameters().get("athlete");
        Matcher matcher = athleteIdPattern.matcher(athleteDetails);
        matcher.find();
        String athleteId = matcher.group(1);

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        Map<String, Object> attributes = new HashMap<>();

        for (String scope : userRequest.getAccessToken().getScopes()) {
            authorities.add(new SimpleGrantedAuthority(scope));
        }

        attributes.put("athleteId", athleteId);
        attributes.put("token", userRequest.getAccessToken().getTokenValue());

        return new DefaultOAuth2User(authorities, attributes, "athleteId");
    }
}
