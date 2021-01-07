package smarter.uearn.money.chats.model;

import java.io.Serializable;

public class ChannelDetails implements Serializable {
    private String userid;
    private String username;
    private String channel;
    private String phone;
    private PreviousMessages[] messages;

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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public PreviousMessages[] getMessages() {
        return messages;
    }

    public void setMessages(PreviousMessages[] messages) {
        this.messages = messages;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
