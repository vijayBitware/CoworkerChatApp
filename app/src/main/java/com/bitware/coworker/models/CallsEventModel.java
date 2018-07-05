package com.bitware.coworker.models;

public class CallsEventModel {

    String type;
    String channelId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public CallsEventModel(String type, String channelId) {

        this.type = type;
        this.channelId = channelId;
    }

    public CallsEventModel(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}