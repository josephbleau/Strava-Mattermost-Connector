package com.josephbleau.StravaMattermostConnector.model;

public class MattermostDetails {
    String host;
    int port;
    String hookToken;
    String channelName;
    String teamName;

    public MattermostDetails(String host, int port, String hookToken, String teamName, String channelName) {
        this.host = host;
        this.port = port;
        this.hookToken = hookToken;
        this.teamName = teamName;
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

    public String getChannelUrl() {
        return host + ":" + port + "/" + teamName + "/channels/" + channelName;
    }
}
