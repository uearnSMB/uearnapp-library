package smarter.uearn.money.notificationmessages.model;

import java.io.Serializable;

public class MessageStatus implements Serializable {
    private String id;
    private String status;
    private String create_ist;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_ist() {
        return create_ist;
    }

    public void setCreate_ist(String create_ist) {
        this.create_ist = create_ist;
    }
}
