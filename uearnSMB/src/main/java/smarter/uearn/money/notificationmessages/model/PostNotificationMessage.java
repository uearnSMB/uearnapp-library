package smarter.uearn.money.notificationmessages.model;

import java.io.Serializable;

public class PostNotificationMessage implements Serializable {
    private String user_id;
    private MessageStatus[] postNotifications;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public MessageStatus[] getPostNotifications() {
        return postNotifications;
    }

    public void setPostNotifications(MessageStatus[] postNotifications) {
        this.postNotifications = postNotifications;
    }
}
