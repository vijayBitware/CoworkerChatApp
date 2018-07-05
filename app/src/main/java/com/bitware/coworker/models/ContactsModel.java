package com.bitware.coworker.models;

/**
 * Created by KrishnaDev on 12/31/16.
 */

public class ContactsModel {

    private String mobile_no;
    private String name;
    private String registeredName;
    private String image;
    private String status;
    private String zeoChatId;
    private String id;
    private int showInContactsPage;

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    Boolean isSelected;

    public ContactsModel(String mobile_no, String name, String registeredName, String image,
                         String status, int showInContactsPage, String zeoChatId, String id,Boolean isSelected) {
        this.mobile_no = mobile_no;
        this.name = name;
        this.id = id;
        this.showInContactsPage = showInContactsPage;
        this.image = image;
        this.registeredName = registeredName;
        this.status = status;
        this.zeoChatId = zeoChatId;
        this.isSelected=isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZeoChatId() {
        return zeoChatId;
    }

    public void setZeoChatId(String zeoChatId) {
        this.zeoChatId = zeoChatId;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public void setRegisteredName(String registeredName) {
        this.registeredName = registeredName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShowInContactsPage() {
        return showInContactsPage;
    }

    public void setShowInContactsPage(int showInContactsPage) {
        this.showInContactsPage = showInContactsPage;
    }


}
