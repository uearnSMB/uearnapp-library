package smarter.uearn.money.training.model;

import java.io.Serializable;

public class MessageReadRequest implements Serializable {
    private String trainer_id;
    private String message_id;
    private String message_mark;

    public String getTrainer_id() {
        return trainer_id;
    }

    public void setTrainer_id(String trainer_id) {
        this.trainer_id = trainer_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_mark() {
        return message_mark;
    }

    public void setMessage_mark(String message_mark) {
        this.message_mark = message_mark;
    }
}
