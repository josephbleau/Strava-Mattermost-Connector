package com.josephbleau.stravamattermostconnector.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StravaEventRequestDTO {

    @JsonProperty("object_type")
    private StravaObjectTypeDTO objectType;

    @JsonProperty("aspect_type")
    private StravaAspectTypeDTO aspectType;

    @JsonProperty("event_time")
    private Long eventTime;

    @JsonProperty("object_id")
    private Long objectId;

    @JsonProperty("owner_id")
    private Long ownerId;

    @JsonProperty("subscription_id")
    private Integer subscriptionId;

    public StravaObjectTypeDTO getObjectType() {
        return objectType;
    }

    public StravaAspectTypeDTO getAspectType() {
        return aspectType;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public Long getObjectId() {
        return objectId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Integer getSubscriptionId() {
        return subscriptionId;
    }

}
