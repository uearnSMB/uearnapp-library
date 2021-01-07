package smarter.uearn.money.training.model;

import java.io.Serializable;

public class BatchDetails implements Serializable {

    private int id;
    private String groupid;
    private String trainer_id;
    private String start_date;
    private String end_date;
    private String start_time;
    private String duration;
    private String capacity;
    private String agents_list;
    private String training_location;
    private String status;
    private String batch_rating;
    private String batch_remarks;
    private String created_at;
    private String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getTrainer_id() {
        return trainer_id;
    }

    public void setTrainer_id(String trainer_id) {
        this.trainer_id = trainer_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getAgents_list() {
        return agents_list;
    }

    public void setAgents_list(String agents_list) {
        this.agents_list = agents_list;
    }

    public String getTraining_location() {
        return training_location;
    }

    public void setTraining_location(String training_location) {
        this.training_location = training_location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBatch_rating() {
        return batch_rating;
    }

    public void setBatch_rating(String batch_rating) {
        this.batch_rating = batch_rating;
    }

    public String getBatch_remarks() {
        return batch_remarks;
    }

    public void setBatch_remarks(String batch_remarks) {
        this.batch_remarks = batch_remarks;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
