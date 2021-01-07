package smarter.uearn.money.agentslots.model;

import java.io.Serializable;

public class SlotResponse implements Serializable {
    private SlotDay[] data;

    public SlotDay[] getData() {
        return data;
    }

    public void setData(SlotDay[] data) {
        this.data = data;
    }
}
