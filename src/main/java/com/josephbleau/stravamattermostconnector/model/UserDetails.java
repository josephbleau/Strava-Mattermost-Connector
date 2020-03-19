package com.josephbleau.stravamattermostconnector.model;

public class UserDetails {
    private String athleteKey;
    private boolean verified;

    private MattermostDetails mattermostDetails;
    private StravaTokenDetails stravaTokenDetails;

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

    public StravaTokenDetails getStravaTokenDetails() {
        return stravaTokenDetails;
    }

    public void setStravaTokenDetails(StravaTokenDetails stravaTokenDetails) {
        this.stravaTokenDetails = stravaTokenDetails;
    }
}
