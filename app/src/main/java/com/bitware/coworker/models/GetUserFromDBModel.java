package com.bitware.coworker.models;

/**
 * Created by KrishnaDev on 1/6/17.
 */

public class GetUserFromDBModel {

    private String Mobile;
    private String Name;
    private String Reged_Name;
    private String Image;
    private String Status;
    private String ShownInContactsPage;
    private String ZoeChatId;

    public GetUserFromDBModel(String mobile, String name, String Reg_name, String image, String status,
                              String shownInContactsPage, String zoeChatId) {

        this.Mobile = mobile;
        this.Name = name;
        this.Reged_Name = Reg_name;
        this.Image = image;
        this.Status = status;
        this.ShownInContactsPage = shownInContactsPage;
        this.ZoeChatId = zoeChatId;

    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getReged_Name() {
        return Reged_Name;
    }

    public void setReged_Name(String reged_Name) {
        Reged_Name = reged_Name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getShownInContactsPage() {
        return ShownInContactsPage;
    }

    public void setShownInContactsPage(String shownInContactsPage) {
        ShownInContactsPage = shownInContactsPage;
    }

    public String getZoeChatId() {
        return ZoeChatId;
    }

    public void setZoeChatId(String zoeChatId) {
        ZoeChatId = zoeChatId;
    }


}
