package smarter.uearn.money.chats.model;

import java.io.Serializable;

public class ResponseChannel implements Serializable {
    private String status;
    private String message;
    private String userid;
    private String username;
    private String groupid;
    private String phone;
    private float namefontsize;
    private float textfontsize;
    private Channel channels;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Channel getChannels() {
        return channels;
    }

    public void setChannels(Channel channels) {
        this.channels = channels;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public float getNamefontsize() {
        return namefontsize;
    }

    public void setNamefontsize(float namefontsize) {
        this.namefontsize = namefontsize;
    }

    public float getTextfontsize() {
        return textfontsize;
    }

    public void setTextfontsize(float textfontsize) {
        this.textfontsize = textfontsize;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
