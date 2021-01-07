package smarter.uearn.money.models;

import java.io.Serializable;

public class NetworkSuccessDetails implements Serializable {
    private String type;
    private String message;
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
