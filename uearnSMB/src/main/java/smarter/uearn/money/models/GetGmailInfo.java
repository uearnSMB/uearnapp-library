package smarter.uearn.money.models;

import java.util.ArrayList;

/**
 * Created by Admin on 03-07-2015.
 */
public class GetGmailInfo {
    public String userId;
    public String before;
    public String cname;
    public String messageId;
    public boolean enableGmailMails;
    public boolean enableGoogleCalendar;
    public String cc;
    public String bcc;
    public String id;
    public String loginType;
    public ArrayList<Attachments> attachmentsArrayList;


    public String to, subject, content;

    public GetGmailInfo(String userId) {
        this.userId = userId;
    }

    public GetGmailInfo(String userId, String cname, String before){
        this.userId = userId;
        this.cname = cname;
        this.before = before;
    }

    public GetGmailInfo(String userId, String messageId)
    {
       this.userId = userId;
        this.messageId = messageId;
    }

    public GetGmailInfo(String userId, boolean enableGmailMails) {
        this.userId = userId;
        this.enableGmailMails = enableGmailMails;
    }

    public GetGmailInfo(String userId, String to, String subject, String message) {
        this.userId = userId;
        this.to = to;
        this.subject = subject;
        this.content = message;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    /*public void setBcc(String bcc) {
        this.bcc = bcc;
    }*/

    public void setEnableGoogleCalendar(boolean enableGoogleCalendar) { this.enableGoogleCalendar = enableGoogleCalendar; }

    /*public boolean getIsEnableGoogleCalendar() { return enableGoogleCalendar; }*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
