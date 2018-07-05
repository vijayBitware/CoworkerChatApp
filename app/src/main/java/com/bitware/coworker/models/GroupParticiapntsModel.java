package com.bitware.coworker.models;

/**
 * Created by user on 03-06-2017.
 */

public class GroupParticiapntsModel {
    String user_id, group_id, joined_at, added_by, isAdmin;

    public GroupParticiapntsModel(String user_id, String group_id, String joined_at, String added_by, String isAdmin) {
        this.user_id = user_id;
        this.group_id = group_id;
        this.joined_at = joined_at;
        this.added_by = added_by;
        this.isAdmin = isAdmin;
    }

    public GroupParticiapntsModel() {

    }

    public String getUser_id() {

        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getJoined_at() {
        return joined_at;
    }

    public void setJoined_at(String joined_at) {
        this.joined_at = joined_at;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }
}
