package smarter.uearn.money.utils.ServerAPIConnectors;

import android.util.Log;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

public class Urls {

    //Production Url
    public static String PERMANENT_SERVER_ADDRESS = "https://youearn.in";
    //public static String SERVER_ADDRESS = "https://smarter-biz.com";
    //http://www15.smarter-biz.com
    public static String SERVER_ADDRESS = "https://youearn.in";

    //PRASHANTA
//    public static final String PERMANENT_SERVER_ADDRESS = "http://34. 93.8.198:5000";
//    public static String SERVER_ADDRESS = "http://34.93.8.198:5000";


    public static final String KNOWLARIRTY_SERVER_ADDRESS = "https://kpi.knowlarity.com";





    //public static String SERVER_ADDRESS = "http://smarterbiz.co";

    //public static String SERVER_ADDRESS = "http://smarter-biz.com";

    //public static final String SERVER_ADDRESS = "http://smbprod.azurewebsites.net";
    //New dev url
    //public static final String SERVER_ADDRESS = "https://smbdev.azurewebsites.net";

    //public static final String SERVER_ADDRESS = "http://192.168.1.11:3000";
    //public static String SERVER_ADDRESS = "http://35.187.227.191";
    //public static final String SERVER_ADDRESS="http://smartersmb.azurewebsites.net";

    public static String SERVER_SIO_ADDRESS = "http://35.187.227.191:3002";

    public static final String PERMANENT_SIO_ADDRESS = "http://35.187.227.191:3002";
    /*public static String DASHBOARD_SIO_ADDRESS = "http://192.168.0.131:3002";*/
    public static String DASHBOARD_SIO_ADDRESS = "http://35.200.169.37:3002";
    //public static String CHAT_SERVER_ADDRESS = CommonUtils.chatbotURL();
    /*public static String getSendContactUrl(String contact) {
        return String
                .format("http://mobilegurukul.azuyo.com/index.php/site/AddMissedCalls/number/%s/",
                        contact);
    }

    public static String getSendSMSUrl() {
        return "http://mobilegurukul.azuyo.com/index.php/askSms/receive/";
    }

    public static String getMainUrl() {
        return HomeScreen.SERVER_ADDRESS+"/mails.json";
    }
    */
    public static String getUrl() {
        return SERVER_ADDRESS += "";
    }

    public static String getSioAddress() { return SERVER_SIO_ADDRESS += "";}
    public static String getDashboardSioAddress() { return DASHBOARD_SIO_ADDRESS += "";}

    public static String getAutoWebSignInUrl() {
        return SERVER_ADDRESS + "/index.html#/authbytoken/";
    }

    public static String getApplicationVersion() {
        return SERVER_ADDRESS + "/api/v2/admin/appversion";
    }

    public static String getRemoteDialerStart(String userId, String message, String pingResponse, String networkType) {
        message = message.replace(" ","%20");
        return SERVER_ADDRESS + "/api/v2/bcp/remotedialstart?user_id=" + userId+"&options="+message+"&pingdelay="+pingResponse+"&networktype="+networkType;
    }

    public static String getRemoteDialerStop(String userId, String message, String totalLoggedInTime, String totalActiveTime) {
        message = message.replace(" ","%20");
        return SERVER_ADDRESS + "/api/v2/bcp/remotedialstop?user_id=" + userId+"&options="+message+"&loggedin_time="+totalLoggedInTime+"&active_time="+totalActiveTime;
    }

    public static String getRemoteDialerStop() {
        return SERVER_ADDRESS + "/api/v2/bcp/remotedialstop";
    }

    public static String getUearnVoiceTestInfoUrl(String userid) {
        return SERVER_ADDRESS + "/api/v2/uearn/languages?user_id="+userid;
    }

    public static String getCreateCmailUrl() {
        //Migrated to secure Api
      /*  return SERVER_ADDRESS+"/cmail_mails.json?ver=2";//+2;*/
        return SERVER_ADDRESS + "/api/v2/cmail_mails.json?ver=2";//+2;
    }

    public static String getcMailUrl() {
        return SERVER_ADDRESS + "/api/v2/cmail_mails.json";
    }

    public static String getgmailsUrl() {
        return SERVER_ADDRESS + "/api/v2/google/getMails.json";
    }

    public static String getgmailComposeUrl() {
        return SERVER_ADDRESS + "/api/v2/common/mail/compose";
    }

    public static String getgmailReplyUrl() {
        //Migrate to secure Api
       /* return  SERVER_ADDRESS+"/google/reply.json";*/
        return SERVER_ADDRESS + "/api/v2/google/reply.json";
    }

    public static String getgmailUrl(String userId, String msgId) {
        //Migrate to secure Api
        /*return SERVER_ADDRESS+"/google/getMail.json?user_id="+userId+"&msg_id="+msgId;*/
        return SERVER_ADDRESS + "/api/v2/google/getMail.json?user_id=" + userId + "&msg_id=" + msgId;
    }

    public static String getComposeGmailUrl() {
       /* return SERVER_ADDRESS+"/google/compose.json";*/
        return SERVER_ADDRESS + "/api/v2/google/compose.json";
    }

    public static String getGmailAccountsUrl(String userId) {
        /*Migrated to secure API*/
        /*return SERVER_ADDRESS+"/common/getemailboxes?user_id="+userId+"&ver=2";*/
        return SERVER_ADDRESS + "/api/v2/common/getemailboxes?user_id=" + userId + "&ver=2";
        // return SERVER_ADDRESS+"/google/getAccounts?user_id="+userId;
    }

    public static String getEnableOrDisableGmailUrl(String id, String userId, boolean enable, String type) {
        //Migrated
        //return SERVER_ADDRESS+"/google/enable?user_id="+userId+"&enable="+enable;
        /*return SERVER_ADDRESS+"/common/mails/enable?user_id="+userId+"&id="+id+"&enable="+enable+"&type="+type;*/
        return SERVER_ADDRESS + "/api/v2/common/mails/enable?user_id=" + userId + "&id=" + id + "&enable=" + enable + "&type=" + type;
    }

    public static String getGmailHasTokenUrl(String userId) {
        //Migrated
      /*  return SERVER_ADDRESS+"/google/hasTokens?user_id="+userId;*/
        return SERVER_ADDRESS + "/api/v2/google/hasTokens?user_id=" + userId;
    }

    public static String getUploadFileUrl() {
        //Chaned to v2 api :::KSN
       /* return SERVER_ADDRESS+"/events.json";*/
        //Log.d("ReuploadService","Call getUploadFileUrl()");
        return SERVER_ADDRESS + "/api/v2/events.json";
    }

    public static String getUploadImageUrl(String userId) {
        return SERVER_ADDRESS + "/api/v2/cmail_users/"+userId+".json";
    }

    public static String getRegistrationUrl() {

        //Migrated
        /*return SERVER_ADDRESS+"/cmail_users.json?ver=2";//+2;*/
        return SERVER_ADDRESS + "/api/v2/cmail_users.json?source_of_login=android&ver=2";//+2;
        //return HomeScreen.SERVER_ADDRESS+"/cmail_users.json";
    }

    public static String getLoginUrl() {

        /*return SERVER_ADDRESS+"/check_login.json";*/
       /* return SERVER_ADDRESS + "/check_login.json&version=5.4.3";*/
        return PERMANENT_SERVER_ADDRESS + "/check_login.json";
        //return HomeScreen.SERVER_ADDRESS+"/check_login.json";
    }

    public static String getChangePasswordUrl() {
        //Migrated
       /* return  SERVER_ADDRESS+"/changepwd.json";*/
        return SERVER_ADDRESS + "/api/v2/changepwd.json";
    }

    public static String getUserDetailUrl(String userid) {

        /*return SERVER_ADDRESS+"/cmail_users/"+userid+".json";*/
        return SERVER_ADDRESS + "/api/v2/cmail_users/" + userid + ".json";
    }

    public static String getForgotPasswordUrl() {
        return SERVER_ADDRESS + "/reset_password_mail";
    }

    public static String getCmailContactsUrl() {
       /* return SERVER_ADDRESS+"/cmail_contacts.json";*/
        return SERVER_ADDRESS + "/api/v2/cmail_contacts.json";
    }

    public static String getSettingsUrl(String userid) {
        //Migrated
       /* return SERVER_ADDRESS+"/cmail_users/"+ userid + ".json?ver=2";*/
        return SERVER_ADDRESS + "/api/v2/cmail_users/" + userid + ".json?ver=2";
    }

    public static String getUpateParentUrl(String parentid) {
        //Migrated
       /* return SERVER_ADDRESS+"/cmail_mails/" + parentid+ ".json";*/
        return SERVER_ADDRESS + "/api/v2/cmail_mails/" + parentid + ".json";
    }

    public static String getCmailUrl(String cmailid) {
        //Migrated
      /*  return SERVER_ADDRESS+"/cmail_mails/" + cmailid + ".json";*/
        return SERVER_ADDRESS + "/api/v2/cmail_mails/" + cmailid + ".json";
    }

    //Changed the calendar entry url
    public static String getCalendarCreateUrl() {
       /* return SERVER_ADDRESS+"/common/event/create";*/
        Log.d("CommonEvent" , "getCalendarCreateUrl() called");
        return SERVER_ADDRESS + "/api/v2/common/event/create";
    }

    public static String getCalendarEnventsUrl(String user_id, String start_time, String end_time, int skip, String calendar_type) {
        //return SERVER_ADDRESS+"/google/calendar/getEvents";
        /*return SERVER_ADDRESS+"/common/event/list?user_id="+user_id+"&start_date="+start_time+"&end_date="+end_time+"&skip="+skip+"&calendar_type="+calendar_type+"&top=50";*/
        return SERVER_ADDRESS + "/api/v2/common/event/list?user_id=" + user_id + "&start_date=" + start_time + "&end_date=" + end_time + "&skip=" + skip + "&calendar_type=" + calendar_type + "&top=100";
    }

    public static String getCalendarEnventsUrl(String user_id, String start_time, String end_time, int skip, String calendar_type, String startId, String endId) {
        //return SERVER_ADDRESS+"/google/calendar/getEvents";
        /*return SERVER_ADDRESS+"/common/event/list?user_id="+user_id+"&start_date="+start_time+"&end_date="+end_time+"&skip="+skip+"&calendar_type="+calendar_type+"&top=50";*/
        return SERVER_ADDRESS + "/api/v2/common/event/list?user_id=" + user_id + "&firstId=" + startId + "&lastId=" + endId + "&start_date=" + start_time + "&end_date=" + end_time + "&skip=" + skip + "&calendar_type=" + calendar_type + "&top=100";
    }

    public static String getSetGroupUserUrl() {
        //Migrated
        /*return SERVER_ADDRESS+"/api/setgroupsofuser";*/
        return SERVER_ADDRESS + "/api/v2/setgroupsofuser";
    }

    public static String getUpdateUserDetailsUrl(String userId) {
        return SERVER_ADDRESS + "/api/v2/cmail_users/"+userId+".json";
    }

    public static String getFeedbackUrl(String userId) {
        return  SERVER_ADDRESS + "/send_feedback";
    }

    public static String getGetGroupUserUrl(String user_id) {
        //Migrated
     /*   return SERVER_ADDRESS+"/api/getgroupsofuser?user_id='"+user_id+"'"; */
        return SERVER_ADDRESS + "/api/v2/getgroupsofuser?user_id='" + user_id + "'";
    }

    public static String getUploadVoiceTestUrl() {
        return SERVER_ADDRESS + "/api/v2/uearn/voiceCalls";
    }

    public static String getSubscribeNotificationsUrl(String userId, String devicekey) {
        /*Migrated to secure Api*/
        /*return SERVER_ADDRESS+"/common/registerdevicepushnotifications?user_id="+userId+"&devicekey="+devicekey;*/
        return SERVER_ADDRESS + "/api/v2/common/registerdevicepushnotifications?user_id=" + userId + "&devicekey=" + devicekey;
    }

    public static String getGoogleCalendarEnableOrDisableUrl(String userId, boolean status) {
        //Migrated
        /*return SERVER_ADDRESS+"/google/calendar/enable?user_id="+userId+"&enable="+status;*/
        return SERVER_ADDRESS + "/api/v2/google/calendar/enable?user_id=" + userId + "&enable=" + status;
    }

    public static String getDeleteSupervisorOrEngineerUrl(String userId, String dependent_id) {
        //Migrated
        /*return SERVER_ADDRESS+"/api/deletegroupsofuser?user_id="+userId+"&dependent_id="+dependent_id;*/
        return SERVER_ADDRESS + "/api/v2/deletegroupsofuser?user_id=" + userId + "&dependent_id=" + dependent_id;
    }

    public static String getAddSecondaryEmailUrl() {
        //Migrated
        // return SERVER_ADDRESS+"/api/google/addgmail";
       /* return  SERVER_ADDRESS+"/common/addmailbox";*/
        return SERVER_ADDRESS + "/api/v2/common/addmailbox";
    }

    public static String getUpdateSecondaryEmailUrl() {
        // return SERVER_ADDRESS+"/api/google/addgmail";
        /*return  SERVER_ADDRESS+"/common/updatemailbox";*/
        return SERVER_ADDRESS + "/api/v2/common/updatemailbox";

    }

    public static String getUpdateCalendarUrl() {
        //changd to v2 api
       /* return SERVER_ADDRESS+"/common/event/update";*/
        return SERVER_ADDRESS + "/api/v2/common/event/update";
    }

    public static String getDeleteCalendarEventUrl(String user_id, String appointment_id, String external_refernce_id, String calendar_type) {
        //Migrated
        /*return SERVER_ADDRESS+"/common/event/delete?user_id="+user_id+"&Id="+appointment_id+"&external_calendar_reference="+external_refernce_id+"&calendar_type="+calendar_type;*/
        return SERVER_ADDRESS + "/api/v2/common/event/delete?user_id=" + user_id + "&Id=" + appointment_id + "&external_calendar_reference=" + external_refernce_id + "&calendar_type=" + calendar_type;
    }

    public static String getExchangeTokenUrl() {
        /*return SERVER_ADDRESS+"/google/exchangetoken";*/
        return SERVER_ADDRESS + "/api/v2/google/exchangetoken";
    }

    public static String getSetTokenUrl(String userId, String code) {
        //Migrated
        /*return SERVER_ADDRESS+"/common/settokens_withcode?user_id="+userId+"&code="+code+"&channel=App";*/
        return SERVER_ADDRESS + "/api/v2/common/settokens_withcode?user_id=" + userId + "&code=" + code + "&channel=App";
    }

    public static String getAcceptOrRejectUrlForAppointments() {
        //Migrated
        /*return SERVER_ADDRESS+"/common/event/accept";*/
        return SERVER_ADDRESS + "/api/v2/common/event/accept";
    }

    public static String getUploadFilesUrl() {
        return SERVER_ADDRESS + "";
    }

    public static String getCreateworkOrderUrl() {
        //Migrated
        /*return SERVER_ADDRESS+"/workorder/create";*/
        return SERVER_ADDRESS + "/api/v2/workorder/create";
    }

    public static String getWorkOrderEventsUrl() {
        /*return SERVER_ADDRESS+"/workorder/list";*/
        return SERVER_ADDRESS + "/api/v2/workorder/list";
    }

    public static String getDeleteWorkOrderUrl() {
        //Migrated
        /*return SERVER_ADDRESS+"/workorder/delete";*/
        return SERVER_ADDRESS + "/api/v2/workorder/delete";
    }

    public static String getUpdateWorkOrderUrl() {
        //Migrated
        /*return SERVER_ADDRESS+"/workorder/update";*/
        return SERVER_ADDRESS + "/api/v2/workorder/update";
    }

    //TODO Change url
    public static String getRemoveSecondaryAccountUrl() {
        return SERVER_ADDRESS + "";
    }

    public static String getEventFetchUrl(String user_id, String appointment_id) {
        /*Migrated to secure Api*/
       /* return SERVER_ADDRESS+"/common/event/list?user_id="+user_id+"&Id="+appointment_id;*/
        return SERVER_ADDRESS + "/api/v2/common/event/list?user_id=" + user_id + "&Id=" + appointment_id;
    }

    public static String getFetchExternalCalendarUrl(String user_id, String external_appointment_id) {
       /* return SERVER_ADDRESS+"/common/event/list?user_id="+user_id+"&calendar_type=office365"+"&Id="+external_appointment_id;*/
        return SERVER_ADDRESS + "/api/v2/common/event/list?user_id=" + user_id + "&calendar_type=office365" + "&Id=" + external_appointment_id;
    }

    public static String getWorkOrderEventUrl() {
        /*return SERVER_ADDRESS+"/workorder/list";*/
        return SERVER_ADDRESS + "/api/v2/workorder/list";
    }

    public static String getSalesStatusUpdate() {
       /* return SERVER_ADDRESS+"/api/salesstatus";*/
        Log.d("Urls", "getSalesStatusUpdate() called");
        return SERVER_ADDRESS + "/api/v2/salesstatus";
    }

    public static String getHomeScreenActivityUrl(String user_id, String last_mail_id, long last_gmail_id, String last_office365_id) {
        if (last_office365_id != null && last_office365_id.equals("0")) {
            return SERVER_ADDRESS + "/api/v2/activities?user_id=" + user_id + "&last_id=" + last_mail_id + "&last_gmail_time=" + last_gmail_id;
        } else {
            return SERVER_ADDRESS + "/api/v2/activities?user_id=" + user_id + "&last_id=" + last_mail_id + "&last_gmail_time=" + last_gmail_id + "&last_office365_time=" + last_office365_id;
        }// return SERVER_ADDRESS+"/api/activities?user_id="+user_id+"&last_id="+last_mail_id;
    }

    public static String getOneViewScreenAtivityUrl(String user_id, String customer_id, String last_mail_id) {
        return SERVER_ADDRESS + "/api/v2/activities?user_id=" + user_id + "&customerid=" + customer_id + "&last_id=" + last_mail_id;
    }

    public static String getPostActivityUrl() {
        /*return SERVER_ADDRESS+"/api/activities";*/
        return SERVER_ADDRESS + "/api/v2/activities";
    }

    public static String getSaleStatusDataUrl(String user_id) {
        /*Migrated to secure Api*/
        /*return SERVER_ADDRESS+"/api/settings/stages?user_id="+user_id; */
        return SERVER_ADDRESS + "/api/v2/settings/stages?user_id=" + user_id;
    }

    public static String getDigitalBrochureUrl() {
        //mIGRATED
        /*return SERVER_ADDRESS+"/api/v1/digitalbrochure";*/
        return SERVER_ADDRESS + "/api/v2/digitalbrochure";
    }

    public static String getAllTeamMembersUrl(String user_id) {
        /*Migrated to secure api*/
        /*return SERVER_ADDRESS+"/api/team?user_id="+user_id+"&type=groupList&select=[\"phone\",\"email\",\"name\",\"id\",\"request\"]&filter={\"request\":\"accepted\"}";*/
        return SERVER_ADDRESS + "/api/v2/team?user_id=" + user_id + "&type=groupList&select=[\"phone\",\"email\",\"name\",\"id\",\"request\",\"role\"]&filter={\"request\":\"accepted\"}";
    }

    public static String getTeamMemberUrl(String user_id) {
        return SERVER_ADDRESS + "/api/v2/team?user_id=" + user_id + "&type=groupList&select=[\"phone\",\"email\",\"name\",\"id\",\"request\",\"dependenttype\"]";
    }

    public static String getActivitiesGraphUrl(String user_id, String startDate, String endDate) {
        //Migrated
        /*return SERVER_ADDRESS+"/status/graphs?user_id="+user_id+"&start_date="+startDate+"&end_date="+endDate+"&criteriatype={%22unattendedmissedcalls%22:{%22type%22:[%22plotly%22,%22excel%22]},%22advancedreceived%22:{%22type%22:%22plotly%22},%22actedupon%22:{%22type%22:[%22plotly%22,%20%22excel%22]},%22activities%22:{%22type%22:[%22plotly%22,%20%22excel%22]},%22maplocations%22:{%22type%22:%22plotly%22}}&forleads=false";*/
       /* return SERVER_ADDRESS+"/status/graphs?user_id="+user_id+"&start_date="+startDate+"&end_date="+endDate+"&criteriatype={%22unattendedmissedcalls%22:{%22type%22:[%22plotly%22,%22excel%22]},%22advancedreceived%22:{%22type%22:%22plotly%22},%22actedupon%22:{%22type%22:[%22plotly%22,%20%22excel%22]},%22activities%22:{%22type%22:[%22plotly%22,%20%22excel%22]},%22maplocations%22:{%22type%22:%22plotly%22}}&forleads=false";*/
        return SERVER_ADDRESS + "/api/v2/status/graphs?user_id=" + user_id + "&start_date=" + startDate + "&end_date=" + endDate + "&criteriatype={%22unattendedmissedcalls%22:{%22type%22:[%22plotly%22,%22excel%22]},%22advancedreceived%22:{%22type%22:%22plotly%22},%22actedupon%22:{%22type%22:[%22plotly%22,%20%22excel%22]},%22activities%22:{%22type%22:[%22plotly%22,%20%22excel%22]},%22maplocations%22:{%22type%22:%22plotly%22}}&forleads=false";
    }

    public static String getSalesGraphUrl(String user_id, String startDate, String endDate) {
        //Migrated
        /*return SERVER_ADDRESS+"/status/salesgraph?user_id="+user_id+"&start_date="+startDate+"&end_date="+endDate+"&criteriatype={%22salesstatus%22:%20{%22type%22:[%22plotly%22,%22excel%22]},%20%22statusoccurance%22:%20{%22type%22:[%22plotly%22,%22excel%22]},%20%22salesfunnelcumulative%22:%20{%22type%22:[%22excel%22,%20%22dashboard%22]},%20%22status%22:%20{%22type%22:[%22plotly%22,%22excel%22]},%20%22funnel%22:%20{%22type%22:%22salesguyemail%22},%20%22ordervalue%22:%20{%22type%22:%20[%22plotly%22,%20%22excel%22]}}&forleads=false";*/
        return SERVER_ADDRESS + "/api/v2/status/salesgraph?user_id=" + user_id + "&start_date=" + startDate + "&end_date=" + endDate + "&criteriatype={%22salesstatus%22:%20{%22type%22:[%22plotly%22,%22excel%22]},%20%22statusoccurance%22:%20{%22type%22:[%22plotly%22,%22excel%22]},%20%22salesfunnelcumulative%22:%20{%22type%22:[%22excel%22,%20%22dashboard%22]},%20%22status%22:%20{%22type%22:[%22plotly%22,%22excel%22]},%20%22funnel%22:%20{%22type%22:%22salesguyemail%22},%20%22ordervalue%22:%20{%22type%22:%20[%22plotly%22,%20%22excel%22]}}&forleads=false";
    }

    public static String getAppointmentGraphUrl(String user_id, String startDate, String endDate) {
        //Migrated
        /*return SERVER_ADDRESS+"/status/appointmentgraph?user_id="+user_id+"&start_date="+startDate+"&end_date="+endDate+"&criteriatype={%22appointments%22:{%22type%22:[%22dashboard%22,%22excel%22]},%20%22appointmenttile%22:{%22type%22:%20%22dashboard%22}}&forleads=false";*/
        return SERVER_ADDRESS + "/api/v2/status/appointmentgraph?user_id=" + user_id + "&start_date=" + startDate + "&end_date=" + endDate + "&criteriatype={%22appointments%22:{%22type%22:[%22dashboard%22,%22excel%22]},%20%22appointmenttile%22:{%22type%22:%20%22dashboard%22}}&forleads=false";
    }

    public static String getTalkTimeGraphUrl(String user_id, String startDate, String endDate) {
        //Migrated
        /*return SERVER_ADDRESS+"/status/appointmentgraph?user_id="+user_id+"&start_date="+startDate+"&end_date="+endDate+"&criteriatype={%22appointments%22:{%22type%22:[%22dashboard%22,%22excel%22]},%20%22appointmenttile%22:{%22type%22:%20%22dashboard%22}}&forleads=false";*/
        return SERVER_ADDRESS + "/api/v2/status/talktimegraph?user_id=" + user_id + "&start_date=" + startDate + "&end_date=" + endDate + "&criteriatype={%22talktime%22:{%22type%22:%22dashboard%22},%20%22callcount%22:{%22type%22:%20[%22plotly%22,%20%22excel%22]}}&forleads=false";
    }

    public static String getAppStatus(String userid) {
        //Migrated
        return SERVER_ADDRESS + "/api/v2/app_status?user_id=" + userid + "activity_type=signed";
    }

    public static String postAppStatus() {
        return SERVER_ADDRESS + "/api/v2/app_status";
    }

    public static String getFollowupsUrl(String userid, String start_time, String end_time) {
        return SERVER_ADDRESS + "/api/v2/status/followupsgraph?user_id=" + userid + "&start_date=" + start_time + "&end_date=" + end_time + "&timezone=Asia/Kolkata&userset=Team&criteriatype={\"followupdashboard\":{\"type\":\"excel\"}}";
    }


    public static String getDashboardUrl(String userid, String start_time, String end_time) {
        return SERVER_ADDRESS + "/api/v2/status/dashboardgraph?user_id=" + userid + "&start_date=" + start_time + "&end_date=" + end_time + "&timezone=Asia/Kolkata&userset=Team&includeteam=1&criteriatype={\"appdashboard\":{\"type\":\"excel\"}}";
    }

    public static String getnewDashboardUrl(String userid, String start_time, String end_time) {
        return SERVER_ADDRESS + "/api/v2/status/newdashboardgraph?user_id=" + userid + "&start_date=" + start_time + "&end_date=" + end_time + "&timezone=Asia/Calcutta&criteria=graphs&criteriatype={\"newappdashboard\":{\"type\":\"excel\"}}";
    }

    public static String getOyoDashboardUrl(String userid, String start_time, String end_time) {
        return SERVER_ADDRESS + "/api/v2/oyo_outbound?user_id=" + userid + "&start_date=" + start_time + "&end_date=" + end_time + "&agents_list=" + userid;
    }

    public static String getMisseddcallDashboardUrl(String userid, String start_time, String end_time) {
        return SERVER_ADDRESS + "/api/v2/status/callsdashboardgraph?user_id=" + userid + "&start_date=" + start_time + "&end_date=" + end_time + "&timezone=Asia/Calcutta&criteria=graphs&criteriatype={\"callsdashboard\":{\"type\":\"excel\"}}";
    }

    public static String getLeadsAndSourceDashboardUrl(String userid, String start_time, String end_time) {
        return SERVER_ADDRESS + "/api/v2/status/leadssourcegraph?user_id=" + userid +"&userset=Team" + "&start_date=" + start_time + "&end_date=" + end_time + "&criteriatype={%22leadssource%22:%20{%22type%22:[%22plotly%22,%22excel%22]}}";
    }

    public static String getUserLocationUrl(String userid, String start_time, String end_time) {
        return SERVER_ADDRESS + "/api/v2/status/graphsnew?user_id=" + userid +"&userset=Team" + "&start_date=" + start_time + "&end_date=" + end_time;
    }

    public static String getAllTeamStructuresUrl(String user_id) {
        /*Migrated to secure api*/
        /*return SERVER_ADDRESS+"/api/team?user_id="+user_id+"&type=groupList&select=[\"phone\",\"email\",\"name\",\"id\",\"request\"]&filter={\"request\":\"accepted\"}";*/
        return SERVER_ADDRESS + "/api/v2/team?user_id=" + user_id + "&filter={\"request\":\"accepted\"}";
    }

    public static String getIvrMappingUrl(String userId, String number) {
        String time = CommonUtils.getTimeFormatInISO(Calendar.getInstance().getTime());
        //String time = CommonUtils.getTime(String.valueOf(Calendar.getInstance().getTime()));
        return SERVER_ADDRESS + "/api/v2/ivrcustomer?user_id=" + userId + "&bizact={\"from\":\"" + number + "\",\"start_time\":\"" + time + "\"}";
    }

    public static String getIvrMappingUrl(String userId, String number, String time, String endTime) {
        /*TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String time = df.format(sentTime);*/
        return SERVER_ADDRESS + "/api/v2/ivrcustomer?user_id=" + userId + "&bizact={\"from\":\"" + number + "\",\"start_time\":\"" + time + "\",\"end_time\":\"" + endTime+ "\"}";
    }

    public static String getIvrMappingMissedCallUrl(String userId, String number, String time) {
        return SERVER_ADDRESS + "/api/v2/ivrcustomer?user_id=" + userId + "&event_type=unattended" + "&bizact={\"from\":\"" + number + "\",\"start_time\":\"" + time + "\"}";
    }

    public static String getCallStatusUpdateUrl() {
        return SERVER_ADDRESS + "/api/v2/callstatusupdate";
    }

    public static String getPersonalContactsUrl() {
        return SERVER_ADDRESS + "/api/v2/common/event/listcontacts?user_id=";
    }

    public static String getAddPersonalContactUrl() {
        return SERVER_ADDRESS + "/api/v2/common/event/createpersonal";
    }

    public static String getDeleteContactsUrl() {
        return SERVER_ADDRESS + "/api/v2/common/event/deletecontacts";
    }

    public static String getAddJunkContactUrl(){
        return SERVER_ADDRESS + "/api/v2/common/event/createjunk";
    }

    public static String getUpdateTeamMemberUrl(){
        return SERVER_ADDRESS + "/api/v2/cmail_users/updateuser";
    }

    public static String getKnowlarityRegistrationUrl() {
        return KNOWLARIRTY_SERVER_ADDRESS + "/Basic/v1/account/agent";
    }

    public static String getKnowlarityClickToCallUrl() {
        return KNOWLARIRTY_SERVER_ADDRESS + "/Basic/v1/account/call/makecall";
    }

    public static String getCmailCallrecording(String user_id, String customerId) {
        /*return SERVER_ADDRESS+"/api/v2/cmail_mails/getlastcalls?user_id="+user_id+"&customer_id="+customerId+"&count=3";*/
        return SERVER_ADDRESS+"/api/v2/cmail_mails/getlastcalls?user_id="+user_id+"&number="+customerId+"&count=3";
    }

    public static String getKnowlarityCalllogs(String startTime, String endTime, String knowlarityNumber, String customerNumber) {
        startTime = startTime.replace("+","%2B");
        startTime = startTime.replace(" ","%20");
        startTime = startTime.replace(":","%3A");

        endTime = endTime.replace("+","%2B");
        endTime = endTime.replace(" ","%20");
        endTime = endTime.replace(":","%3A");

        knowlarityNumber = knowlarityNumber.replace("+","%2b");
        customerNumber = customerNumber.replace("+","%2b");

        return "https://kpi.knowlarity.com/Basic/v1/account/calllog?start_time="+startTime+"&end_time="+endTime+"&limit=1&knowlarity_number="+knowlarityNumber+"&customer_number="+customerNumber;
    }

    public static String getQdeDashboard(String user_id, String start_time, String end_time) {
        return SERVER_ADDRESS+"/api/v2/status/dashboard_dialed_qde?user_id="+user_id+"&start_date="+start_time+"&end_date="+end_time;
    }

    public static String getReClickToCallUrl() {
        return "http://etsrds.kapps.in/webapi/smarterbiz/api/smarterbiz_15301_c2c.py";
    }

    public static String getC2cClickToCallUrl() {
        String url;
        //return "http://etsrds.kapps.in/webapi/smarterbiz/api/smarterbiz_c2c.py";
       /* return "http://101.53.130.199/webapi/smarterbiz/api/smarterbiz_c2c.py";*/
        if(ApplicationSettings.containsPref(AppConstants.C2C_ENDPOINT)) {
            url = ApplicationSettings.getPref(AppConstants.C2C_ENDPOINT, "");
        } else {
            url =  "http://etsrds.kapps.in/webapi/smarterbiz/api/smarterbiz_c2c.py";
        }

        //Log.d("getC2cClickToCallUrl",url);
        return url;
    }

    public static String getUserUrl(String serialNum, String userId) {
        return SERVER_ADDRESS+"/api/v2/getsubscriptions?user_id=" + userId + "&simsl="+serialNum;
    }

    public static String getUserFaqUrl() {
        return SERVER_ADDRESS+"/uearn/faq";
    }

    public static String getUearnDashboard(String userid, String start_time, String end_time) {
        return SERVER_ADDRESS + "/api/v2/uearn/getdashboard?user_id=" + userid + "&start_date=" + start_time + "&end_date=" + end_time + "&agents_list=" + userid;
    }

    public static String getUearnedMonthPassbookUrl(String startTime) {
        return SERVER_ADDRESS + "/api/v2/uearn/passbook/month_wise?today=" + startTime;
    }

    public static String getUearnPassbookLifetimeUrl(String userid) {
        return SERVER_ADDRESS + "/api/v2/uearn/passbook/lifetime?user_id=" + userid;
    }


    public static String getContactedListUrl(String userid, String startTime, String endTime) {
        //String urlll = SERVER_ADDRESS + "/api/v2/common/event/contacted?start_date=" + startTime + "&end_date=" + endTime + "?user_id=" +userid;
        return SERVER_ADDRESS + "/api/v2/common/event/contacted?start_date=" + startTime + "&end_date=" + endTime + "&user_id=" +userid;
    }


    public static String getPasswordResetUrl(String email) {
        //String urlll = SERVER_ADDRESS + "/api/v2/common/event/contacted?start_date=" + startTime + "&end_date=" + endTime + "?user_id=" +userid;
        return SERVER_ADDRESS + "/api/v2/reset_password.json?email="+ email;
    }


    public static String getUearnDayWisePassbookUrl(String userid, String start_time, String end_time, String company) {
        Log.d("Url:-", SERVER_ADDRESS + "/api/v2/uearn/passbook/day_wise?user_id=" + userid + "&start_date=" + start_time + "&end_date=" + end_time + "&company=" +company);
        return SERVER_ADDRESS + "/api/v2/uearn/passbook/day_wise?user_id=" + userid + "&start_date=" + start_time + "&end_date=" + end_time + "&agents_list=" + "&company=" +company;
    }

    public static String getRequestForDataUrl(String userId, String message) {
        try {
            message = URLEncoder.encode(message, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return SERVER_ADDRESS + "/api/v2/requestfordata?user_id=" + userId+"&options="+message;
    }

    public static String getIvrCustomerMappingUrl(String userId, String transactionId) {
        return SERVER_ADDRESS + "/api/v2/ivrcustomer?user_id=" + userId + "&transactionid=" + transactionId;
    }

    public static String postIvrCustomerMappingUrl() {
        return SERVER_ADDRESS + "/api/v2/ivrcustomer";
    }

    public static String postCallDisconnectUrl() {
        return SERVER_ADDRESS + "/api/v2/calldisconnect";
    }

    public static String postTransferCallUrl() {
        return SERVER_ADDRESS + "/api/v2/callTransfer";
    }

    public static String postMergeCallUrl() {
        return SERVER_ADDRESS + "/api/v2/callMerge";
    }

    public static String postBackToNetworkUrl() {
        Log.d("Urls", "Urls - postBackToNetworkUrl() called");
        return SERVER_ADDRESS + "/api/v2/backtonetwork";
    }

    public static String postSalesEntriesUrl() {
        return SERVER_ADDRESS + "/api/v2/salesentries";
    }

    public static String postDBEntriesUrl() {
        return SERVER_ADDRESS + "/api/v2/dbentries";
    }

    public static String postMySettings() {
        return SERVER_ADDRESS + "/api/v2/mysettings";
    }

    public static String getAgentSlotUrl(String userId) {
        return SERVER_ADDRESS + "/api/v2/getSlotList?user_id="+userId;
    }

    public static String getRequestForBookingSlotUrl(String userId) {

        return SERVER_ADDRESS + "/api/v2/userregisterSlot" ;
    }

    public static String getNotificationMessageUrl(String userId) {
        return SERVER_ADDRESS + "/api/v2/getNotificationList?user_id="+userId;
    }

    public static String getRequestForPostMessageUrl(String userId) {

        return SERVER_ADDRESS + "/api/v2/updateNotifications";
    }

    public static String getRequestForCancelSlotUrl(String userId) {

        return SERVER_ADDRESS + "/api/v2/usercancelSlot";
    }

    public static String getRequestForChannelInfo(String userId) {

        return CommonUtils.chatbotURL() + "/api/v2/subscribe" ;
    }

    public static String getRequestForTechSupport(String userId) {

        return SERVER_ADDRESS + "/api/v2/admin/techsupport";
    }

    public static String getTrainingDashboardUrl(String userId) {
        return SERVER_ADDRESS + "/api/v2/training/userdata/?user_id="+userId;
    }
    public static String getHelpDeskQuestion(String helpDeskId) {
        return SERVER_ADDRESS + "/api/v2/training/helpdeskinfo?helpdesk_id="+helpDeskId;
    }
    public static String submitHelpDeskQuestion(String strAnswer, String questionID) {
        Log.e("URLs","URL for Help :: "+SERVER_ADDRESS + "/api/v2/training/helpdeskinfo?responsetype="+strAnswer+"&question_id="+questionID);
        return SERVER_ADDRESS + "/api/v2/training/helpdeskinfo?responsetype="+strAnswer+"&question_id="+questionID;
    }
    public static String getHelpDeskList() {
        return SERVER_ADDRESS + "/api/v2/training/helpdesklist";
    }
    public static String getRequestForTrainerReviewUrl(String userId) {

        return SERVER_ADDRESS + "/api/v2/training/review" ;
    }

    public static String getTrainerReviewUrl(String user_id, String batchcode, String day) {
        Log.e("Urls","url::: "+SERVER_ADDRESS + "/api/v2/training/review?user_id="+user_id +"&batch_code="+batchcode+"&day="+day);
        return SERVER_ADDRESS + "/api/v2/training/review?user_id="+user_id +"&batch_code="+batchcode+"&day="+day ;
    }

    public static String getTrainingMessageUrl(String userId) {
        return SERVER_ADDRESS + "/api/v2/training/notifications?user_id="+userId ;
    }

    public static String getReadTrainingMessageUrl(String date) {
        return SERVER_ADDRESS + "/api/v2/training/notification" ;
    }

    public static String getUearnTotalAgentsAndEarningsUrl(String userId) {
        return SERVER_ADDRESS + "/uearn/data?user_id="+userId;
    }

    public static String getServiceUserAgreementUrl() {
        return SERVER_ADDRESS + "/api/v2/uearn/terms";
    }

    public static String getQuestionsUrl(String userId) {
        return SERVER_ADDRESS + "/api/v2/uearn/questions?user_id="+userId;
    }

    public static String getGrammarTestInfo() {
        return SERVER_ADDRESS + "/api/v2/uearn/answers";
    }

    public static String postOnboardKyc(){
        return SERVER_ADDRESS + "/api/v2/recruitment/upload_doc";
    }

    public static String getRequestForNetworkDetailsUrl(String userId) {

        return SERVER_ADDRESS + "/api/v2/uearn/check_speed" ;
    }
}

// http://mobilegurukul.azuyo.com
