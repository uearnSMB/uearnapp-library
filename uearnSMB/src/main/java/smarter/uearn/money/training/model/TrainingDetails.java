package smarter.uearn.money.training.model;

import java.io.Serializable;

public class TrainingDetails implements Serializable {

    private String agent_id;
    private String days_attended;
    private String hours_completed;
    private String location;

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getDays_attended() {
        return days_attended;
    }

    public void setDays_attended(String days_attended) {
        this.days_attended = days_attended;
    }

    public String getHours_completed() {
        return hours_completed;
    }

    public void setHours_completed(String hours_completed) {
        this.hours_completed = hours_completed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
