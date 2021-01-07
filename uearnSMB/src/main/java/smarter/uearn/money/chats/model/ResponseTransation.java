package smarter.uearn.money.chats.model;

import java.io.Serializable;

public class ResponseTransation implements Serializable {
    private String message;
    private TransationDetails data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TransationDetails getData() {
        return data;
    }

    public void setData(TransationDetails data) {
        this.data = data;
    }
}
