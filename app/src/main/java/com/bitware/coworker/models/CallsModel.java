package com.bitware.coworker.models;

/**
 * Created by krishnadev on 13/04/17.
 */

public class CallsModel {
    String zoeChatId = "";
    String username = "";
    String userImage = "";
    long call_time;
    String callType = "";
    String callLog = "";


    public CallsModel(String zoeChatId, String username, String userImage, long Call_time, String callType, String CallLog) {
        this.zoeChatId = zoeChatId;
        this.username = username;
        this.userImage = userImage;
        this.call_time = Call_time;
        this.callType = callType;
        this.callLog = CallLog;

    }

    public String getZoeChatId() {
        return zoeChatId;
    }

    public void setZoeChatId(String zoeChatId) {
        this.zoeChatId = zoeChatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public long getCall_time() {
        return call_time;
    }

    public void setCall_time(long call_time) {
        this.call_time = call_time;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallLog() {
        return callLog;
    }

    public void setCallLog(String callLog) {
        this.callLog = callLog;
    }


}
