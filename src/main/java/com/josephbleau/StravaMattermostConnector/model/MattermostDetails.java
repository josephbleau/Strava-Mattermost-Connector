package com.josephbleau.StravaMattermostConnector.model;

public class MattermostDetails {
    String host;
    int port;
    String hookToken;
    String channelName;

    public MattermostDetails(String host, int port, String hookToken, String channelName) {
        this.host = host;
        this.port = port;
        this.hookToken = hookToken;
        this.channelName = channelName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getHookToken() {
        return hookToken;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getWebookUrl() {
        return host + ":" + port + "/hooks/" + hookToken;
    }
}
