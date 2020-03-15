package com.josephbleau.stravamattermostconnector.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StravaWebookChallengeResponseDTO {

    @JsonProperty("hub.challenge")
    private String challenge;

    public StravaWebookChallengeResponseDTO(String challenge) {
        this.challenge = challenge;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

}
