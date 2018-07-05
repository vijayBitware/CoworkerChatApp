package com.bitware.coworker.models;

/**
 * Created by KrishnaDev on 1/30/17.
 */

public class ChatsList {
    private String name;
    private String lastMsg;
    private String userImage;
    private String chatRoomID;
    private String chatRoomType;
    private String sender;
    private String lastMsgStatus;
    private String unreadCount;
    private String lastMessageTime;

    public ChatsList(String name, String lstMsg, String userImages, String chatRoomID,
                     String chatRoomType, String sender, String lstMsgStatus, String unreadCount) {

        this.name = name;
        this.lastMsg = lstMsg;
        this.userImage = userImages;
        this.chatRoomID = chatRoomID;
        this.chatRoomType = chatRoomType;
        this.sender = sender;
        this.lastMsgStatus = lstMsgStatus;
        this.unreadCount = unreadCount;

    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastMsgStatus() {
        return lastMsgStatus;
    }

    public void setLastMsgStatus(String lastMsgStatus) {
        this.lastMsgStatus = lastMsgStatus;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChatRoomType() {
        return chatRoomType;
    }

    public void setChatRoomType(String chatRoomType) {
        this.chatRoomType = chatRoomType;
    }

    public String getChatRoomID() {
        return chatRoomID;
    }

    public void setChatRoomID(String chatRoomID) {
        this.chatRoomID = chatRoomID;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
