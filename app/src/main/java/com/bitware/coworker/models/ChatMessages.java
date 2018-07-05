package com.bitware.coworker.models;

/**
 * Created by KrishnaDev on 1/7/17.
 */

public class ChatMessages {

    public static final int SENDER_TEXT_MESSAGE = 0;//textSender
    public static final int RECEIVER_TEXT__MESSAGE = 1;//textReceiver
    public static final int SENDER_IMAGE_MESSAGE = 2;//imageSender
    public static final int RECEIVER_IMAGE_MESSAGE = 3;//imageReceiver
    public static final int RECEIVER_LOCATION_MESSAGE = 6;//imageReceiver
    public static final int SENDER_LOCATION_MESSAGE = 5;//imageReceiver
    public static final int SENDER_VIDEO_MESSAGE = 7;//imageReceiver
    public static final int RECEIVER_VIDEO_MESSAGE = 8;//imageReceiver
    public static final int SENDER_CONTACT_MESSAGE = 11;//imageReceiver
    public static final int RECEIVER_CONTACT_MESSAGE = 12;//imageReceiver
    public static final int CREATE_GROUP_MSG = 13;//imageReceiver
    public static final int DATE_HEADER = 4;//imageReceiver
    public static final int SENDER_AUDIO_MESSAGE = 14;//AudioSender
    public static final int RECEIVER_AUDIO_MESSAGE = 15;//AudioReceiver
    public static final int SENDER_DOC_MESSAGE = 16;//AudioSender
    public static final int RECEIVER_DOC_MESSAGE = 17;//AudioReceiver
    public static final int SENDER_STICKER_MESSAGE = 18;//AudioReceiver
    public static final int RECEIVER_STICKER_MESSAGE = 19;//AudioReceiver

    public static final String SENDER = "0"; //sender
    public static final String RECEIVER = "1"; //receiver
    public static final String SENDER_IMAGE = "2"; //sender
    public static final String RECEIVER_IMAGE = "3"; //receiver
    public static final String RECEIVER_LOCATION = "6"; //receiver
    public static final String SENDER_LOCATION = "5"; //receiver
    public static final String SENDER_VIDEO = "7"; //receiver
    public static final String RECEIVER_VIDEO = "8"; //receiver
    public static final String RECEIVER_CONTACT = "12"; //receiver
    public static final String SENDER_CONTACT = "11"; //sender
    public static final String HEADER = "4";
    public static final String CREATE_GROUP = "13";
    public static final String SENDER_AUDIO= "14"; //sender
    public static final String RECEIVER_AUDIO= "15"; //receiver
    public static final String SENDER_DOC= "16"; //receiver
    public static final String RECEIVER_DOC= "17"; //receiver
    public static final String SENDER_STICKER= "18"; //receiver
    public static final String RECEIVER_STICKER= "19"; //receiver

    public static final String GROUP = "10";
    public static final String BROADCAST = "20";


    private String msgId;
    private String userID;
    private String messageText;
    private String userType;
    private Status messageStatus;
    private String msgType;
    private String seenTime;
    private String deliveredTime;
    private String isDownload;
    private String caption;
    private String contentStatus;
    private String sentTime;
    private String upload_Image;
    private String date;
    private String type;
    private Boolean isSelected;


    public ChatMessages(String msgId, String userId, String msg, String chatType, String contentStatus,
                        String userType, String sortDate, String sentTime, String deliveredTime,
                        String seenTime, String caption, String download) {
        this.msgId = msgId;
        this.userID = userId;
        this.messageText = msg;
        this.msgType = chatType;
        this.contentStatus = contentStatus;
        this.userType = userType;
        this.date = sortDate;
        this.sentTime = sentTime;
        this.deliveredTime = deliveredTime;
        this.seenTime = seenTime;
        this.caption = caption;
        this.isDownload = download;
        this.isSelected=false;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public ChatMessages() {

    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getIsDownload() {
        return isDownload;
    }

    public void setIsDownload(String isDownload) {
        this.isDownload = isDownload;
    }

    public String getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(String deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public String getSeenTime() {
        return seenTime;
    }

    public void setSeenTime(String seenTime) {
        this.seenTime = seenTime;
    }

    public String getUpload_Image() {
        return upload_Image;
    }

    public void setUpload_Image(String upload_Image) {
        this.upload_Image = upload_Image;
    }


    public String getChatType() {
        return msgType;
    }

    public void setChatType(String msgType) {
        this.msgType = msgType;
    }

    public String getMessageText() {

        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Status getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }
}
