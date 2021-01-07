package smarter.uearn.money.agentslots.model;

import java.io.Serializable;

public class SlotCancel implements Serializable {
    private String slot_id;
    private String user_id;

    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
