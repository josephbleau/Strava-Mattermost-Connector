package com.josephbleau.StravaMattermostConnector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MattermostDetails {
    String host;
    String port;
    String hookToken;
    String channelName;
    String teamName;
    String userName;
    Boolean hidden;

    public MattermostDetails() {}

    public MattermostDetails(String host, String port, String hookToken, String teamName, String channelName, String userName) {
        this.host = host;
        this.port = port;
        this.hookToken = hookToken;
        this.teamName = teamName;
        this.channelName = channelName;
        this.userName = userName;
        this.hidden = false;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHookToken() {
        return hookToken;
    }

    public void setHookToken(String hookToken) {
        this.hookToken = hookToken;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @JsonIgnore
    public String getWebookUrl() {
        return host + ":" + port + "/hooks/" + hookToken;
    }

    @JsonIgnore
    public String getChannelUrl() {
        return host + ":" + port + "/" + teamName + "/channels/" + channelName;
    }
}
