package com.josephbleau.StravaMattermostConnector.model;

public class UserDetails {
    private String athleteKey;
    private StravaApiDetails stravaApiDetails;
    private MattermostDetails mattermostDetails;

    public UserDetails(String athleteKey, StravaApiDetails stravaApiDetails, MattermostDetails mattermostDetails) {
        this.athleteKey = athleteKey;
        this.stravaApiDetails = stravaApiDetails;
        this.mattermostDetails = mattermostDetails;
    }

    public String getAthleteKey() {
        return athleteKey;
    }

    public StravaApiDetails getStravaApiDetails() {
        return stravaApiDetails;
    }

    public MattermostDetails getMattermostDetails() {
        return mattermostDetails;
    }
}
