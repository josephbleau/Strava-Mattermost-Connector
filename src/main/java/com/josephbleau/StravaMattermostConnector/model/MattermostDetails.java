package com.josephbleau.StravaMattermostConnector.model;

public class MattermostDetails {
    String host;
    String port;
    String hookToken;
    String channelName;
    String teamName;
    String userName;

    public MattermostDetails(String host, String port, String hookToken, String teamName, String channelName, String userName) {
        this.host = host;
        this.port = port;
        this.hookToken = hookToken;
        this.teamName = teamName;
        this.channelName = channelName;
        this.userName = userName;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getHookToken() {
        return hookToken;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getUserName() {
        return userName;
    }

    public String getWebookUrl() {
        return host + ":" + port + "/hooks/" + hookToken;
    }

    public String getChannelUrl() {
        return host + ":" + port + "/" + teamName + "/channels/" + channelName;
    }
}
