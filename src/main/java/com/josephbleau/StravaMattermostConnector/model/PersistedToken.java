package com.josephbleau.StravaMattermostConnector.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PersistedToken {
    @Id
    @Column(name = "athlete_id")
    private Integer athleteId;

    @Column(name = "token")
    private String token;

    public PersistedToken(){}

    public PersistedToken(Integer athleteId, String token) {
        this.athleteId = athleteId;
        this.token = token;
    }

    public Integer getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Integer athleteId) {
        this.athleteId = athleteId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}