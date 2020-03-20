package com.josephbleau.stravamattermostconnector.service.strava;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StravaEventSubscription {
    private String id;

    @JsonProperty("callback_url")
    private String callbackUrl;

    public String getId() {
        return id;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }
}
