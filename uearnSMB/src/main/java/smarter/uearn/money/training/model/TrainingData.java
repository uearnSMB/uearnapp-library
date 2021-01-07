package smarter.uearn.money.training.model;

import java.io.Serializable;

public class TrainingData implements Serializable {

    private BatchDetails Batch;
    private CalendarDetails calender;
    private TrainingDetails Training;
    private TrainerDetails Trainer;

    public BatchDetails getBatch() {
        return Batch;
    }

    public void setBatch(BatchDetails batch) {
        Batch = batch;
    }

    public CalendarDetails getCalender() {
        return calender;
    }

    public void setCalender(CalendarDetails calender) {
        this.calender = calender;
    }

    public TrainingDetails getTraining() {
        return Training;
    }

    public void setTraining(TrainingDetails training) {
        Training = training;
    }

    public TrainerDetails getTrainer() {
        return Trainer;
    }

    public void setTrainer(TrainerDetails trainer) {
        Trainer = trainer;
    }
}
