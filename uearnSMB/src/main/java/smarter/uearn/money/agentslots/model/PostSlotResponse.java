package smarter.uearn.money.agentslots.model;

import java.io.Serializable;

public class PostSlotResponse implements Serializable {
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
