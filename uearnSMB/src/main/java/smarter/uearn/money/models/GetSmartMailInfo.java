package smarter.uearn.money.models;

/**
 * Created by Admin on 03-07-2015.
 */
public class GetSmartMailInfo {
    public String userId;
    public String lastCmailId;
    public String parentId;
    public long lastGmailTime;
    public String lastOffice365Time;

    public GetSmartMailInfo(String userId, String parentId, String lastCmailId){
        this.userId = userId;
        this.parentId = parentId;
        this.lastCmailId = lastCmailId;
    }

    public GetSmartMailInfo(String userId, String lastCmailId, long lastGmailTime) {
        this.userId = userId;
        this.parentId = null;
        this.lastCmailId = lastCmailId;
        this.lastGmailTime = lastGmailTime;
    }


}
