package smarter.uearn.money.models;

/**
 * Class to incorporate the single API which will replace 5 API calls with just a single one
 * <p>
 * Created on 05/06/17.
 *
 * @author Dilip Kumar
 * @version 1.0
 */

public class SingleApiAppointmentModel {

    // FirstBlock
    private String userId = "";
    private String userLatitude = "", userLongitude = "";
    private String contactPersonName = "", contactPersonCompany = "", contactPersonDesignation = "",
            contactPersonNumber = "", contactPersonEmail = "", contactPersonAddress = "",
            contactPersonWebsite = "";

    // Create Appointment Block
    private String createAppointmentFrom = "", createAppointmentTo = "",
            createAppointmentStartTime = "", createAppointmentEndTime = "",
            createAppointmentTimezone = "";
    private String createAppointmentSubject = "", createAppointmentRepeat = "",
            createAppointmentAlertBefore = "", createAppointmentLeadSource = "",
            createAppointmentProductType = "", createAppointmentSetEventToTeamMail = "";
    private String createAppointmentCc = "", createAppointmentFlpCount = "", createAppointmentStatus = "",
            createAppointmentOrderPotential = "", createAppointmentDescription = "";

    // UpdateAppointmentBlock
    private String updateAppointmentId = "", updateAppointmentFrom = "", updateAppointmentTo = "",
            updateAppointmentStartTime = "", updateAppointmentTimeZone = "", updateAppointmentEndTime = "",
            updateAppointmentSubject = "", updateAppointmentRepeat = "";
    private String updateAppointmentAlertBefore = "", updateAppointmentLeadSource = "",
            updateAppointmentProductType = "", updateAppointmentSetEventTeamEmail = "",
            updateAppoingmentCc = "", updateAppointmentResponseStatus = "";
    private String updateAppointmentFlpCount = "", updateAppointmentStatus = "",
            updateAppointmentOrderPotential = "", updateAppointmentDescription = "";

    // Bizact block
    private String fromNumber = "", toNumber = "", activityType = "", subject = "", url = "",
            startTime = "", endTime = "", message = "", parent = "", eventType = "",
            unread = "", name = "", caller_name = "";

    public String getCreateAppointmentFrom() {
        return createAppointmentFrom;
    }

    public void setCreateAppointmentFrom(String createAppointmentFrom) {
        this.createAppointmentFrom = createAppointmentFrom;
    }

    public String getCreateAppointmentTo() {
        return createAppointmentTo;
    }

    public void setCreateAppointmentTo(String createAppointmentTo) {
        this.createAppointmentTo = createAppointmentTo;
    }

    public String getCreateAppointmentStartTime() {
        return createAppointmentStartTime;
    }

    public void setCreateAppointmentStartTime(String createAppointmentStartTime) {
        this.createAppointmentStartTime = createAppointmentStartTime;
    }

    public String getCreateAppointmentEndTime() {
        return createAppointmentEndTime;
    }

    public void setCreateAppointmentEndTime(String createAppointmentEndTime) {
        this.createAppointmentEndTime = createAppointmentEndTime;
    }

    public String getCreateAppointmentTimezone() {
        return createAppointmentTimezone;
    }

    public void setCreateAppointmentTimezone(String createAppointmentTimezone) {
        this.createAppointmentTimezone = createAppointmentTimezone;
    }

    public String getCreateAppointmentSubject() {
        return createAppointmentSubject;
    }

    public void setCreateAppointmentSubject(String createAppointmentSubject) {
        this.createAppointmentSubject = createAppointmentSubject;
    }

    public String getCreateAppointmentRepeat() {
        return createAppointmentRepeat;
    }

    public void setCreateAppointmentRepeat(String createAppointmentRepeat) {
        this.createAppointmentRepeat = createAppointmentRepeat;
    }

    public String getCreateAppointmentAlertBefore() {
        return createAppointmentAlertBefore;
    }

    public void setCreateAppointmentAlertBefore(String createAppointmentAlertBefore) {
        this.createAppointmentAlertBefore = createAppointmentAlertBefore;
    }

    public String getCreateAppointmentLeadSource() {
        return createAppointmentLeadSource;
    }

    public void setCreateAppointmentLeadSource(String createAppointmentLeadSource) {
        this.createAppointmentLeadSource = createAppointmentLeadSource;
    }

    public String getCreateAppointmentProductType() {
        return createAppointmentProductType;
    }

    public void setCreateAppointmentProductType(String createAppointmentProductType) {
        this.createAppointmentProductType = createAppointmentProductType;
    }

    public String getCreateAppointmentSetEventToTeamMail() {
        return createAppointmentSetEventToTeamMail;
    }

    public void setCreateAppointmentSetEventToTeamMail(String createAppointmentSetEventToTeamMail) {
        this.createAppointmentSetEventToTeamMail = createAppointmentSetEventToTeamMail;
    }

    public String getCreateAppointmentCc() {
        return createAppointmentCc;
    }

    public void setCreateAppointmentCc(String createAppointmentCc) {
        this.createAppointmentCc = createAppointmentCc;
    }

    public String getCreateAppointmentFlpCount() {
        return createAppointmentFlpCount;
    }

    public void setCreateAppointmentFlpCount(String createAppointmentFlpCount) {
        this.createAppointmentFlpCount = createAppointmentFlpCount;
    }

    public String getCreateAppointmentStatus() {
        return createAppointmentStatus;
    }

    public void setCreateAppointmentStatus(String createAppointmentStatus) {
        this.createAppointmentStatus = createAppointmentStatus;
    }

    public String getCreateAppointmentOrderPotential() {
        return createAppointmentOrderPotential;
    }

    public void setCreateAppointmentOrderPotential(String createAppointmentOrderPotential) {
        this.createAppointmentOrderPotential = createAppointmentOrderPotential;
    }

    public String getCreateAppointmentDescription() {
        return createAppointmentDescription;
    }

    public void setCreateAppointmentDescription(String createAppointmentDescription) {
        this.createAppointmentDescription = createAppointmentDescription;
    }

    public String getUpdateAppointmentId() {
        return updateAppointmentId;
    }

    public void setUpdateAppointmentId(String updateAppointmentId) {
        this.updateAppointmentId = updateAppointmentId;
    }

    public String getUpdateAppointmentFrom() {
        return updateAppointmentFrom;
    }

    public void setUpdateAppointmentFrom(String updateAppointmentFrom) {
        this.updateAppointmentFrom = updateAppointmentFrom;
    }

    public String getUpdateAppointmentTo() {
        return updateAppointmentTo;
    }

    public void setUpdateAppointmentTo(String updateAppointmentTo) {
        this.updateAppointmentTo = updateAppointmentTo;
    }

    public String getUpdateAppointmentStartTime() {
        return updateAppointmentStartTime;
    }

    public void setUpdateAppointmentStartTime(String updateAppointmentStartTime) {
        this.updateAppointmentStartTime = updateAppointmentStartTime;
    }

    public String getUpdateAppointmentTimeZone() {
        return updateAppointmentTimeZone;
    }

    public void setUpdateAppointmentTimeZone(String updateAppointmentTimeZone) {
        this.updateAppointmentTimeZone = updateAppointmentTimeZone;
    }

    public String getUpdateAppointmentEndTime() {
        return updateAppointmentEndTime;
    }

    public void setUpdateAppointmentEndTime(String updateAppointmentEndTime) {
        this.updateAppointmentEndTime = updateAppointmentEndTime;
    }

    public String getUpdateAppointmentSubject() {
        return updateAppointmentSubject;
    }

    public void setUpdateAppointmentSubject(String updateAppointmentSubject) {
        this.updateAppointmentSubject = updateAppointmentSubject;
    }

    public String getUpdateAppointmentRepeat() {
        return updateAppointmentRepeat;
    }

    public void setUpdateAppointmentRepeat(String updateAppointmentRepeat) {
        this.updateAppointmentRepeat = updateAppointmentRepeat;
    }

    public String getUpdateAppointmentAlertBefore() {
        return updateAppointmentAlertBefore;
    }

    public void setUpdateAppointmentAlertBefore(String updateAppointmentAlertBefore) {
        this.updateAppointmentAlertBefore = updateAppointmentAlertBefore;
    }

    public String getUpdateAppointmentLeadSource() {
        return updateAppointmentLeadSource;
    }

    public void setUpdateAppointmentLeadSource(String updateAppointmentLeadSource) {
        this.updateAppointmentLeadSource = updateAppointmentLeadSource;
    }

    public String getUpdateAppointmentProductType() {
        return updateAppointmentProductType;
    }

    public void setUpdateAppointmentProductType(String updateAppointmentProductType) {
        this.updateAppointmentProductType = updateAppointmentProductType;
    }

    public String getUpdateAppointmentSetEventTeamEmail() {
        return updateAppointmentSetEventTeamEmail;
    }

    public void setUpdateAppointmentSetEventTeamEmail(String updateAppointmentSetEventTeamEmail) {
        this.updateAppointmentSetEventTeamEmail = updateAppointmentSetEventTeamEmail;
    }

    public String getUpdateAppoingmentCc() {
        return updateAppoingmentCc;
    }

    public void setUpdateAppoingmentCc(String updateAppoingmentCc) {
        this.updateAppoingmentCc = updateAppoingmentCc;
    }

    public String getUpdateAppointmentResponseStatus() {
        return updateAppointmentResponseStatus;
    }

    public void setUpdateAppointmentResponseStatus(String updateAppointmentResponseStatus) {
        this.updateAppointmentResponseStatus = updateAppointmentResponseStatus;
    }

    public String getUpdateAppointmentFlpCount() {
        return updateAppointmentFlpCount;
    }

    public void setUpdateAppointmentFlpCount(String updateAppointmentFlpCount) {
        this.updateAppointmentFlpCount = updateAppointmentFlpCount;
    }

    public String getUpdateAppointmentStatus() {
        return updateAppointmentStatus;
    }

    public void setUpdateAppointmentStatus(String updateAppointmentStatus) {
        this.updateAppointmentStatus = updateAppointmentStatus;
    }

    public String getUpdateAppointmentOrderPotential() {
        return updateAppointmentOrderPotential;
    }

    public void setUpdateAppointmentOrderPotential(String updateAppointmentOrderPotential) {
        this.updateAppointmentOrderPotential = updateAppointmentOrderPotential;
    }

    public String getUpdateAppointmentDescription() {
        return updateAppointmentDescription;
    }

    public void setUpdateAppointmentDescription(String updateAppointmentDescription) {
        this.updateAppointmentDescription = updateAppointmentDescription;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(String userLatitude) {
        this.userLatitude = userLatitude;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(String userLongitude) {
        this.userLongitude = userLongitude;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonCompany() {
        return contactPersonCompany;
    }

    public void setContactPersonCompany(String contactPersonCompany) {
        this.contactPersonCompany = contactPersonCompany;
    }

    public String getContactPersonDesignation() {
        return contactPersonDesignation;
    }

    public void setContactPersonDesignation(String contactPersonDesignation) {
        this.contactPersonDesignation = contactPersonDesignation;
    }

    public String getContactPersonNumber() {
        return contactPersonNumber;
    }

    public void setContactPersonNumber(String contactPersonNumber) {
        this.contactPersonNumber = contactPersonNumber;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonAddress() {
        return contactPersonAddress;
    }

    public void setContactPersonAddress(String contactPersonAddress) {
        this.contactPersonAddress = contactPersonAddress;
    }

    public String getContactPersonWebsite() {
        return contactPersonWebsite;
    }

    public void setContactPersonWebsite(String contactPersonWebsite) {
        this.contactPersonWebsite = contactPersonWebsite;
    }

    public String getCaller_name() {
        return caller_name;
    }

    public void setCaller_name(String caller_name) {
        this.caller_name = caller_name;
    }
}
