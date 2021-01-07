package smarter.uearn.money.utils;

import java.util.ArrayList;

/**
 * Created by Srinath on 03-02-2017.
 */

public class FollowupsReports {

    public static String avgTalkTime;
    public static String avgTatTime;
    public static String avgCalls;
    public  String completed;
    public  String updateStatus;
    public  String overdue;
    public  String headers;
    public String overdueList;

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public void setHeaders(String headers) {
        if(headers != null) {
            ApplicationSettings.putPref("FollowupHeaders", headers);
        }
        this.headers = headers;
    }

    public static String getHeaders() {
        if(ApplicationSettings.getPref("FollowupHeaders","") != null) {
           return ApplicationSettings.getPref("FollowupHeaders","");
        }
        return "";
    }

    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }

    public void setOverdue(String overDue) {
        this.overdue = overDue;
    }

        public String getCompleted(){
            return this.completed;
        }

    public String getUpdateStatus() {
        return this.updateStatus;
    }

    public String getOverdue() {
        return this.overdue;
    }

    public static void setOverDueList(ArrayList<String> list) {
        ApplicationSettings.putValue(list, "FollowupsReports");
    }

    public static ArrayList<String> getOverDueList(){
        return ApplicationSettings.getValue("FollowupsReports");
    }

}
