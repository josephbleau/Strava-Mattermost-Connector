package com.josephbleau.StravaMattermostConnector.web.dto;

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
}
