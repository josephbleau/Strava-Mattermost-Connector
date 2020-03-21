package com.josephbleau.stravamattermostconnector.model;

public class SharingDetails {

    private String measurementSystem;
    private boolean shareRunning;
    private boolean shareBiking;
    private boolean shareSwimming;
    private boolean sharePace;
    private boolean shareDistance;
    private boolean shareDuration;
    private boolean shareRouteMap;

    public SharingDetails() {
        this.measurementSystem = "Imperial";
        this.shareRunning = true;
        this.shareBiking = true;
        this.shareSwimming = true;
        this.sharePace = true;
        this.shareDistance = true;
        this.shareDuration = true;
        this.shareRouteMap = true;
    }

    public String getMeasurementSystem() {
        return measurementSystem;
    }

    public void setMeasurementSystem(String measurementSystem) {
        this.measurementSystem = measurementSystem;
    }

    public boolean isShareRunning() {
        return shareRunning;
    }

    public void setShareRunning(boolean shareRunning) {
        this.shareRunning = shareRunning;
    }

    public boolean isShareBiking() {
        return shareBiking;
    }

    public void setShareBiking(boolean shareBiking) {
        this.shareBiking = shareBiking;
    }

    public boolean isShareSwimming() {
        return shareSwimming;
    }

    public void setShareSwimming(boolean shareSwimming) {
        this.shareSwimming = shareSwimming;
    }

    public boolean isSharePace() {
        return sharePace;
    }

    public void setSharePace(boolean sharePace) {
        this.sharePace = sharePace;
    }

    public boolean isShareDistance() {
        return shareDistance;
    }

    public void setShareDistance(boolean shareDistance) {
        this.shareDistance = shareDistance;
    }

    public boolean isShareDuration() {
        return shareDuration;
    }

    public void setShareDuration(boolean shareDuration) {
        this.shareDuration = shareDuration;
    }

    public boolean isShareRouteMap() {
        return shareRouteMap;
    }

    public void setShareRouteMap(boolean shareRouteMap) {
        this.shareRouteMap = shareRouteMap;
    }

}
