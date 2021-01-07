package smarter.uearn.money.agentslots.model;

import java.io.Serializable;

public class SlotData implements Serializable {
    private SlotResponse success;

    public SlotResponse getSuccess() {
        return success;
    }

    public void setSuccess(SlotResponse success) {
        this.success = success;
    }
}
