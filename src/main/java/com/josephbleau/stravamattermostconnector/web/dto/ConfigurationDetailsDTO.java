package com.josephbleau.stravamattermostconnector.web.dto;

import com.josephbleau.stravamattermostconnector.model.MattermostDetails;
import com.josephbleau.stravamattermostconnector.model.SharingDetails;

public class ConfigurationDetailsDTO {
    private SharingDetails sharingDetails;
    private MattermostDetails mattermostDetails;

    public SharingDetails getSharingDetails() {
        return sharingDetails;
    }

    public void setSharingDetails(SharingDetails sharingDetails) {
        this.sharingDetails = sharingDetails;
    }

    public MattermostDetails getMattermostDetails() {
        return mattermostDetails;
    }

    public void setMattermostDetails(MattermostDetails mattermostDetails) {
        this.mattermostDetails = mattermostDetails;
    }
}
