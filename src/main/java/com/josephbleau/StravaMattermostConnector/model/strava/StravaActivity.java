package com.josephbleau.StravaMattermostConnector.model.strava;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StravaActivity {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("distance")
    private Long distance;

    @JsonProperty("moving_time")
    private Long movingTime;

    @JsonProperty("elapsed_time")
    private Long elapsedTime;

    @JsonProperty("type")
    private String type;

    @JsonProperty("athlete")
    private StravaAthlete athlete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getMovingTime() {
        return movingTime;
    }

    public void setMovingTime(Long movingTime) {
        this.movingTime = movingTime;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StravaAthlete getAthlete() {
        return athlete;
    }

    public void setAthlete(StravaAthlete athlete) {
        this.athlete = athlete;
    }
}
