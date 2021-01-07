package smarter.uearn.money.training.model;

import java.io.Serializable;

public class TrainingDataResponse implements Serializable {

    private String id;
    private String name;
    private String phone;
    private String email;
    private String company;
    private String groupid;
    private String batch_code;
    private String role;
    private String project;
    private String status;
    private String created_at;
    private String trainer_name;
    private String banner_notification_title;
    private String banner_notification_message;
    private String batch_status;
    private String unread_messages;
    private BatchDetails batch;
    private CalendarDetails calender;
    private TrainingDetails training;
    private TrainerDetails trainer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getBatch_code() {
        return batch_code;
    }

    public void setBatch_code(String batch_code) {
        this.batch_code = batch_code;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTrainer_name() {
        return trainer_name;
    }

    public void setTrainer_name(String trainer_name) {
        this.trainer_name = trainer_name;
    }

    public String getBanner_notification_title() {
        return banner_notification_title;
    }

    public void setBanner_notification_title(String banner_notification_title) {
        this.banner_notification_title = banner_notification_title;
    }

    public String getBanner_notification_message() {
        return banner_notification_message;
    }

    public void setBanner_notification_message(String banner_notification_message) {
        this.banner_notification_message = banner_notification_message;
    }

    public String getBatch_status() {
        return batch_status;
    }

    public void setBatch_status(String batch_status) {
        this.batch_status = batch_status;
    }

    public String getUnread_messages() {
        return unread_messages;
    }

    public void setUnread_messages(String unread_messages) {
        this.unread_messages = unread_messages;
    }

    public BatchDetails getBatch() {
        return batch;
    }

    public void setBatch(BatchDetails batch) {
        this.batch = batch;
    }

    public CalendarDetails getCalender() {
        return calender;
    }

    public void setCalender(CalendarDetails calender) {
        this.calender = calender;
    }

    public TrainingDetails getTraining() {
        return training;
    }

    public void setTraining(TrainingDetails training) {
        this.training = training;
    }

    public TrainerDetails getTrainer() {
        return trainer;
    }

    public void setTrainer(TrainerDetails trainer) {
        this.trainer = trainer;
    }
}
