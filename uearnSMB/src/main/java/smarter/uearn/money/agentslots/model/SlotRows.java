package smarter.uearn.money.agentslots.model;

import java.io.Serializable;

public class SlotRows implements Serializable {
    private String time;
    private String status;
    private String checklist;
    private String slot_id;
    private boolean already_booked;
    private boolean isActive;
    private String status_text;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public boolean isAlready_booked() {
        return already_booked;
    }

    public void setAlready_booked(boolean already_booked) {
        this.already_booked = already_booked;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }
}
