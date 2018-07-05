package com.bitware.coworker.models;

/**
 * Created by KrishnaDev on 1/2/17.
 */

public class ChatsModel {


    String group_image;
    String is_deleted;
    //private String chat_id;
    private String chatRoomID;
    private String chatRoomType;
    private String sender;
    private String lastMessage;
    private String lastMessageStatus;
    private String lastMessageTime;
    private String sent_by;
    private String create_by;
    private String groupId;
    private String group_name;
    private String contentType;
    private String isLocked;
    private String Password;
    private String wallpaper;
    private String uniqueName;
    private String link;
    private String c_type;
    private String c_description;
    private String shouldNotify;
    private String timedNotify;
    private String shouldSign;
    private int unreadCount;

    public String getShouldNotify() {
        return shouldNotify;
    }

    public void setShouldNotify(String shouldNotify) {
        this.shouldNotify = shouldNotify;
    }

    public String getShouldSign() {
        return shouldSign;
    }

    public void setShouldSign(String shouldSign) {
        this.shouldSign = shouldSign;
    }

    public String getTimedNotify() {
        return timedNotify;
    }

    public void setTimedNotify(String timedNotify) {
        this.timedNotify = timedNotify;
    }

    public ChatsModel(String chatRoomID, String chatRoomType, String sender,
                      String lastMessage, String lastMessageStatus, String lastMessageTime,
                      String contentType, int unreadCount, String group_name, String create_by, String sent_by, String group_image, String is_deleted, String isLocked, String Password, String wallpaper, String uniqueName, String link, String c_type, String c_description, String shouldSign, String shouldNotify, String timedNotify) {
        //this.chat_id = chat_id;
        this.chatRoomID = chatRoomID;
        this.chatRoomType = chatRoomType;
        this.sender = sender;
        this.lastMessage = lastMessage;
        this.lastMessageStatus = lastMessageStatus;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.contentType = contentType;
        this.group_name = group_name;
        this.create_by = create_by;

        this.sent_by = sent_by;
        this.groupId = groupId;
        this.group_image = group_image;
        this.is_deleted = is_deleted;
        this.isLocked = isLocked;
        this.Password = Password;
        this.wallpaper = wallpaper;
        this.uniqueName = uniqueName;
        this.c_type = c_type;
        this.link = link;
        this.c_description = c_description;
        this.shouldSign=shouldSign;
        this.shouldNotify=shouldNotify;
        this.timedNotify=timedNotify;


    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getGroup_image() {
        return group_image;
    }

    public void setGroup_image(String group_image) {
        this.group_image = group_image;
    }

    public String getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(String is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSent_by() {
        return sent_by;
    }

    public void setSent_by(String sent_by) {
        this.sent_by = sent_by;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getC_type() {
        return c_type;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    public String getC_description() {
        return c_description;
    }

    public void setC_description(String c_description) {
        this.c_description = c_description;
    }
/*public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }*/

    public String getChatRoomID() {
        return chatRoomID;
    }

    public void setChatRoomID(String chatRoomID) {
        this.chatRoomID = chatRoomID;
    }

    public String getChatRoomType() {
        return chatRoomType;
    }

    public void setChatRoomType(String chatRoomType) {
        this.chatRoomType = chatRoomType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageStatus() {
        return lastMessageStatus;
    }

    public void setLastMessageStatus(String lastMessageStatus) {
        this.lastMessageStatus = lastMessageStatus;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }


}
