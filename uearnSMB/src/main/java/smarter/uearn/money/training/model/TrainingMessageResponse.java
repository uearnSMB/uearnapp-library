package smarter.uearn.money.training.model;

import java.io.Serializable;

public class TrainingMessageResponse implements Serializable {
    private TrainingMessage[] messages;

    public TrainingMessage[] getMessages() {
        return messages;
    }

    public void setMessages(TrainingMessage[] messages) {
        this.messages = messages;
    }
}
