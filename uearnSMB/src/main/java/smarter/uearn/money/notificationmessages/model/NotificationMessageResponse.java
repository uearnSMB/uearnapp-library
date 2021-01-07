package smarter.uearn.money.notificationmessages.model;

import java.io.Serializable;

public class NotificationMessageResponse implements Serializable {
    private NotificationMessages success[];

    public NotificationMessages[] getSuccess() {
        return success;
    }

    public void setSuccess(NotificationMessages[] success) {
        this.success = success;
    }
}
