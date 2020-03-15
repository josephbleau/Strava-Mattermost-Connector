package com.josephbleau.StravaMattermostConnector.model;

public class UserDetails {
    private String athleteKey;
    private boolean verified;
    private MattermostDetails mattermostDetails;

    public UserDetails(String athleteKey, boolean verified, MattermostDetails mattermostDetails) {
        this.athleteKey = athleteKey;
        this.verified = verified;
        this.mattermostDetails = mattermostDetails;
    }

    public String getAthleteKey() {
        return athleteKey;
    }

    public void setAthleteKey(String athleteKey) {
        this.athleteKey = athleteKey;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public MattermostDetails getMattermostDetails() {
        return mattermostDetails;
    }

    public void setMattermostDetails(MattermostDetails mattermostDetails) {
        this.mattermostDetails = mattermostDetails;
    }
}
