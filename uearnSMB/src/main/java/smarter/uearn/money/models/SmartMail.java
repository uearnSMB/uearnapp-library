package smarter.uearn.money.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SmartMail implements Serializable, Cloneable{

	/*
	 * "attachments": "", "bcc": "", "cc": "", "children": null, "created_at":
	 * "2014-02-10T06:09:36Z", "email": "", "end_time": "2014-02-10T06:09:00Z",
	 * "event_type": "email", "from": "", "id": 1, "jd_url": "", "jobcard_url":
	 * "", "lat": "", "live_recording_url": "", "long": "", "message":
	 * "Hello, this is a test message", "parent": null, "start_time":
	 * "2014-02-10T06:09:00Z", "subject": "Welcome", "to": "", "unread": true,
	 * "updated_at": "2014-02-10T06:09:36Z", "url": "", "user_id": 1
	 */

	/*"created_at": "2014-02-10T06:09:36Z",
    "email": "",
    "end_time": "2014-02-10T06:09:00Z",
    "event_type": "email",
    "from": "",
    "id": 1,
    "jd_url": "",
    "jobcard_url": "",
    "lat": "",
    "live_recording_url": "",
    "long": "",
    "message": "Hello, this is a test message",
    "parent": null,
    "start_time": "2014-02-10T06:09:00Z",
    "subject": "Welcome",
    "to": "",
    "unread": true,
    "updated_at": "2014-02-10T06:09:36Z",
    "url": "",
    "user_id": 1
    
    */
	private String name;
	private String attachments;
	private String bcc;
	private String cc;
	private String children;
	private String createdAt;
	private String email;

	private String eventType;
	private String from;
	private String Id;
	private String jdUrl;
	private String jobCardUrl;
	private String latitude;
	private String liveRecordingUrl;
	private String longitude;
	private String message;
    private String messageId;
	private String parent;
	private String startTime;
	private String endTime;

	private String subject;
	private String to;
	private boolean unread;
	private String updatedAt;
	private String url;
	private String userId;
	private String jobcard;
	private String employeeAssignee;
	private String status;
	private String callerName;
	private String password;
	private ArrayList<SmartMail> childrenInfo;
	private String cmail_url;
	private String htmlMessage;
	private String attachmentFilename = null;
    private String attachmentFileLink = null;
	public SmartMail() {
		// TODO Auto-generated constructor stub
	}

	private ArrayList<AttachmentList> attachmentList;

    public SmartMail(String userId, String to, String from, String eventType, String parent, String message, String subject,
                     String employeeAssignee, String email, String jobcard, String status, String id)
    {
        this.userId = userId;
        this.to = to;
        this.from = from;
        this.eventType = eventType;
        this.parent = parent;
        this.message = message;
        this.subject = subject;
        this.email = email;
        this.employeeAssignee = employeeAssignee;
        this.jobcard = jobcard;
        if(status != null)
            this.status = status;
		this.Id = id;
    }


    public SmartMail(String userId, String to, String from, String eventType, String parent, String message, String subject,
                     String startTime, String playUrl)
    {
        this.userId = userId;
        this.to = to;
        this.from = from;
        this.eventType = eventType;
        this.parent = parent;
        this.message = message;
        this.subject = subject;
        this.startTime = startTime;
        this.url = playUrl;
    }
    public SmartMail(String name, String attachments, String bcc, String cc,
					 String children, String createdAt, String email, String endTime,
					 String eventType, String from, String id, String jdUrl,
					 String jobCardUrl, String latitude, String liveRecordingUrl,
					 String longitude, String message, String parent, String startTime,
					 String subject, String to, boolean unread, String updatedAt,
					 String url, String userId, String jobcard, String employeeAssignee, String status, String callerName, ArrayList<SmartMail> childernInfo) {
		// TODO Auto-generated constructor stub
		this.name=name;
		this.attachments = attachments;
		this.bcc = bcc;
		this.cc = cc;
		this.children = children;
		this.createdAt = createdAt;
		this.email = email;
		this.endTime = endTime;
		this.eventType = eventType;
		this.from = from;
		this.Id = id;
		this.jdUrl = jdUrl;
		this.jobCardUrl = jobCardUrl;
		this.latitude = latitude;
		this.liveRecordingUrl = liveRecordingUrl;
		this.longitude = longitude;
		this.message = message;
		this.parent = parent;
		this.startTime = startTime;
		this.subject = subject;
		this.to = to;
		this.unread = unread;
		this.updatedAt = updatedAt;
		this.url = url;
		this.userId = userId;
		this.jobcard=jobcard;
		this.employeeAssignee=employeeAssignee;
		this.status=status;
		this.callerName=callerName;
		this.childrenInfo = childernInfo;
	}
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getCallerName() {
		return callerName;
	}

	public void setCallerName(String callerName) {
		this.callerName = callerName;
	}

	public String getJobcard() {
		return jobcard;
	}

	public void setJobcard(String jobcard) {
		this.jobcard = jobcard;
	}

	public String getEmployeeAssignee() {
		return employeeAssignee;
	}

	public void setEmployeeAssignee(String employeeAssignee) {
		this.employeeAssignee = employeeAssignee;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCmail_url(String cmail_url){
		this.cmail_url=cmail_url;
	}
	
	public String getCmail_url(){
		return cmail_url;
	}
	
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getJdUrl() {
		return jdUrl;
	}

	public void setJdUrl(String jdUrl) {
		this.jdUrl = jdUrl;
	}

	public String getJobCardUrl() {
		return jobCardUrl;
	}

	public void setJobCardUrl(String jobCardUrl) {
		this.jobCardUrl = jobCardUrl;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLiveRecordingUrl() {
		return liveRecordingUrl;
	}

	public void setLiveRecordingUrl(String liveRecordingUrl) {
		this.liveRecordingUrl = liveRecordingUrl;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getMessage() {
		return message;
	}

	public String getHTMLMessage() { return this.htmlMessage; }

	public void setMessage(String message) {
		this.message = message;
	}

    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getMessageId() { return messageId; }

	public void setHTMLMessage(String html) { this.htmlMessage = html;}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

    public String getAttachmentFileLink() {
        return attachmentFileLink;
    }

    public void setAttachmentFileLink(String attachmentFileLink) {
        this.attachmentFileLink = attachmentFileLink;
    }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

	public SmartMail clone()  {
		try {
			return (SmartMail) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	//Attachments Added To Smartmail  :::KSN
	public void setAttachmentList(ArrayList<AttachmentList> attachmentList) {

		this.attachmentList = attachmentList;
	}
	public ArrayList<AttachmentList> getAttachmentList(){
		return attachmentList;
	}

}
