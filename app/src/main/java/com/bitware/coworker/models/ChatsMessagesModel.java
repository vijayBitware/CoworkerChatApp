package com.bitware.coworker.models;

/**
 * Created by KrishnaDev on 1/2/17.
 */

public class ChatsMessagesModel {
    private String id = "";
    private String userID;
    private String content;
    private String contentType;
    private String contentStatus;
    private String sender;
    private String sentTime;
    private String deliveredTime;
    private String seenTime;
    private String lat;
    private String lng;
    private String c_name;
    private String c_number;
    private int caption;
    private int isDownloaded;
    private int isUploaded;
    private String GroupID;
    private String shouldSign;

    public int getShowPreview() {
        return showPreview;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public String getMetaDescritpion() {
        return metaDescritpion;
    }

    public String getMetaLogo() {
        return metaLogo;
    }

    private  int showPreview;
    private String metaTitle,metaDescritpion,metaLogo;

    public String getActiontype() {
        return actiontype;
    }

    public void setActiontype(String actiontype) {
        this.actiontype = actiontype;
    }

    private String chatRoomType;
    private String actiontype;

    public String getIs_starred() {
        return is_starred;
    }

    public void setIs_starred(String is_starred) {
        this.is_starred = is_starred;
    }

    private String is_starred;

    public String getMediaLinks() {
        return mediaLinks;
    }

    public void setMediaLinks(String mediaLinks) {
        this.mediaLinks = mediaLinks;
    }

    private  String mediaLinks;


    public String getShouldSign() {
        return shouldSign;
    }

    public void setShouldSign(String shouldSign) {
        this.shouldSign = shouldSign;
    }

    public void setShowPreview(int showPreview) {
        this.showPreview = showPreview;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public void setMetaDescritpion(String metaDescritpion) {
        this.metaDescritpion = metaDescritpion;
    }

    public void setMetaLogo(String metaLogo) {
        this.metaLogo = metaLogo;
    }

    public ChatsMessagesModel(String id, String userID, String content, String contentType,
                              String contentStatus, String sender, String groupId, String sentTime, String deliveredTime, String seenTime,
                              int caption, int isUploaded, int isDownloaded, String lat, String lng,
                              String contactName, String contactNumber, String chatRoomType, String isStarred, String mediaLinks, String ActionType, int showPreview, String metaTitle, String metaDescritpion, String metaLogo, String shouldSign) {
        this.id = id;
        this.userID = userID;
        this.content = content;
        this.contentType = contentType;
        this.contentStatus = contentStatus;
        this.sender = sender;
        this.sentTime = sentTime;
        this.deliveredTime = deliveredTime;
        this.seenTime = seenTime;
        this.caption = caption;
        this.isDownloaded = isDownloaded;
        this.isUploaded = isUploaded;
        this.lat = lat;
        this.lng = lng;
        this.c_name = contactName;
        this.c_number = contactNumber;
        this.chatRoomType = chatRoomType;
        this.GroupID = groupId;
        this.is_starred=isStarred;
        this.mediaLinks=mediaLinks;
        this.actiontype=ActionType;
        this.metaDescritpion = metaDescritpion;
        this.metaLogo = metaLogo;
        this.showPreview = showPreview;
        this.metaTitle = metaTitle;
        this.shouldSign=shouldSign;

    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getChatRoomType() {
        return chatRoomType;
    }

    public void setChatRoomType(String chatRoomType) {
        this.chatRoomType = chatRoomType;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getC_number() {
        return c_number;
    }

    public void setC_number(String c_number) {
        this.c_number = c_number;
    }

    public int getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(int isUploaded) {
        this.isUploaded = isUploaded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
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

    public int getCaption() {
        return caption;
    }

    public void setCaption(int caption) {
        this.caption = caption;
    }

    public int getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(int isDownloaded) {
        this.isDownloaded = isDownloaded;
    }


}
