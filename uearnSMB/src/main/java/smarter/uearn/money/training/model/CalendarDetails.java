package smarter.uearn.money.training.model;

import java.io.Serializable;

public class CalendarDetails implements Serializable {

    private String batch_code;
    private String day_of_training;
    private String[] present_to_training;
    private String[] absent_to_training;

    public String getBatch_code() {
        return batch_code;
    }

    public void setBatch_code(String batch_code) {
        this.batch_code = batch_code;
    }

    public String getDay_of_training() {
        return day_of_training;
    }

    public void setDay_of_training(String day_of_training) {
        this.day_of_training = day_of_training;
    }

    public String[] getPresent_to_training() {
        return present_to_training;
    }

    public void setPresent_to_training(String[] present_to_training) {
        this.present_to_training = present_to_training;
    }

    public String[] getAbsent_for_traing() {
        return absent_to_training;
    }

    public void setAbsent_for_traing(String[] absent_for_traing) {
        this.absent_to_training = absent_for_traing;
    }
}
