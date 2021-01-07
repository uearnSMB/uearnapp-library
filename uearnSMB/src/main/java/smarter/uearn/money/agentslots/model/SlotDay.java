package smarter.uearn.money.agentslots.model;

import java.io.Serializable;

public class SlotDay implements Serializable {
    private String day;
    private String date;
    private SlotRows[] rows;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public SlotRows[] getRows() {
        return rows;
    }

    public void setRows(SlotRows[] rows) {
        this.rows = rows;
    }
}
