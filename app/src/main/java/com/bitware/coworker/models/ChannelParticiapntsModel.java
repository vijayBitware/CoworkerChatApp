package com.bitware.coworker.models;

/**
 * Created by user on 03-06-2017.
 */

public class ChannelParticiapntsModel {
    String user_id, channel_id, joined_at, added_by, participantRole;

    public ChannelParticiapntsModel(String user_id, String channel_id, String joined_at, String added_by, String participantRole) {
        this.user_id = user_id;
        this.channel_id = channel_id;
        this.joined_at = joined_at;
        this.added_by = added_by;
        this.participantRole = participantRole;
    }

    public ChannelParticiapntsModel() {

    }

    public String getUser_id() {

        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getParticipantRole() {
        return participantRole;
    }

    public void setParticipantRole(String participantRole) {
        this.participantRole = participantRole;
    }
}
