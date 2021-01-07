package smarter.uearn.money.utils.ServerAPIConnectors;

public class SecureUrls {

    //Production Url
    public static final String SERVER_ADDRESS = "http://youearn.in";


    //New dev url
    //public static final String SERVER_ADDRESS = "http://smbdev.azurewebsites.net";

    //public static final String SERVER_ADDRESS = "http://192.168.1.12:3000";
    //public static final String SERVER_ADDRESS="http://smartersmb.azurewebsites.net";

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
    public static  String getAutoWebSignInUrl(){
        return SERVER_ADDRESS+"/index.html#/authbytoken/";
    }

    public static String getCreateCmailUrl(){
        return SERVER_ADDRESS+"/cmail_mails.json?ver=2";//+2;
    }

    public static String getcMailUrl() {
        return SERVER_ADDRESS+"/api/v1/cmail_mails.json";
    }

    public static String getgmailsUrl() { return SERVER_ADDRESS+"/api/v1/google/getMails.json"; }

    public static String getgmailComposeUrl() { return SERVER_ADDRESS+"/api/v1/common/mail/compose"; }

    public static String getgmailReplyUrl() { return  SERVER_ADDRESS+"/api/v1/google/reply.json"; }

    public static String getgmailUrl(String userId, String msgId) {
        return SERVER_ADDRESS+"/api/v1/google/getMail.json?msg_id="+msgId;
    }

    public static String getComposeGmailUrl() { return SERVER_ADDRESS+"/api/v1/google/compose.json";}

    public static String getGmailAccountsUrl(String userId) {
        /*new Api Call   :::KSN*/
        //return SERVER_ADDRESS+"/common/getemailboxes?user_id="+userId+"&ver=2";
        return SERVER_ADDRESS+"/api/v1/common/getemailboxes";
        // return SERVER_ADDRESS+"/google/getAccounts?user_id="+userId;
    }

    public static String getEnableOrDisableGmailUrl(String id, String userId, boolean enable, String type) {
        //return SERVER_ADDRESS+"/google/enable?user_id="+userId+"&enable="+enable;
        return SERVER_ADDRESS+"/api/v1/common/mails/enable?id="+id+"&enable="+enable+"&type="+type;
    }

    public static String getGmailHasTokenUrl(String userId) {
        return SERVER_ADDRESS+"/api/v1/google/hasTokens";
    }
    public static String getUploadFileUrl() {
        return SERVER_ADDRESS+"/api/v1/vents.json";
    }

    public static String getRegistrationUrl(){

        return SERVER_ADDRESS+"/api/v1/cmail_users.json?ver=2";//+2;
        //return HomeScreen.SERVER_ADDRESS+"/cmail_users.json";
    }

    public static String getLoginUrl(){

        //return SERVER_ADDRESS+"/check_login.json";
        /* New Api Call  :::KSN*/
        return SERVER_ADDRESS+"/auth";
    }

    public static String getChangePasswordUrl()
    {
        return  SERVER_ADDRESS+"/api/v1/changepwd.json";
    }

    public static String getUserDetailUrl(String userid){

        return SERVER_ADDRESS+"/cmail_users/"+userid+".json";
    }

    public static String getForgotPasswordUrl(){

        return SERVER_ADDRESS+"/api/v1/reset_password.json";
    }

    public static String getCmailContactsUrl(){
        return SERVER_ADDRESS+"/api/v1/cmail_contacts.json";
    }

    public static String getSettingsUrl(String userid){
        return SERVER_ADDRESS+"/cmail_users/"+ userid + ".json?ver=2";
    }

    public static String getUpateParentUrl(String parentid){
        return SERVER_ADDRESS+"/cmail_mails/" + parentid+ ".json";
    }

    public static String getCmailUrl(String cmailid){
        return SERVER_ADDRESS+"/cmail_mails/" + cmailid + ".json";
    }

    //Changed the calendar entry url
    public static String getCalendarCreateUrl() {
        return SERVER_ADDRESS+"/api/v1/common/event/create";
    }

    public static String getCalendarEnventsUrl() {
        //return SERVER_ADDRESS+"/google/calendar/getEvents";
        /* New Api Call :::KSN */
       /* return SERVER_ADDRESS+"/common/event/list";*/
        return SERVER_ADDRESS+"/api/v1/common/event/list";
    }

    public static String getSetGroupUserUrl() {
        return SERVER_ADDRESS+"/api/v1/setgroupsofuser";
    }

    public static String getGetGroupUserUrl(String user_id) { return SERVER_ADDRESS+"/api/v1/getgroupsofuser"; }

    public static String getSubscribeNotificationsUrl(String userId, String devicekey) {
        /*New Api Call*/
       /* return SERVER_ADDRESS+"/common/registerdevicepushnotifications?user_id="+userId+"&devicekey="+devicekey;*/
        return SERVER_ADDRESS+"/api/v1/common/registerdevicepushnotifications?devicekey="+devicekey;
    }

    public static String getGoogleCalendarEnableOrDisableUrl(String userId, boolean status) {
        return SERVER_ADDRESS+"/api/v1/google/calendar/enable?enable="+status;
    }

    public static String getDeleteSupervisorOrEngineerUrl(String userId, String dependent_id) {
        return SERVER_ADDRESS+"/api/v1/deletegroupsofuser?dependent_id="+dependent_id;
    }

    public static String getAddSecondaryEmailUrl() {
        // return SERVER_ADDRESS+"/api/google/addgmail";
        return  SERVER_ADDRESS+"/api/v1/common/addmailbox";

    }
    public static String getUpdateSecondaryEmailUrl() {
        // return SERVER_ADDRESS+"/api/google/addgmail";
        return  SERVER_ADDRESS+"/api/v1/common/updatemailbox";

    }

    public static String getUpdateCalendarUrl() {
        /*New Api Call  :::KSN*/
		/*return SERVER_ADDRESS+"/common/event/update";*/
        return SERVER_ADDRESS+"/api/v1/common/event/update";
    }

    public static String getDeleteCalendarEventUrl(String user_id, String appointment_id, String external_refernce_id, String calendar_type) {
        /*New Api Call :::KSN*/
		/*return SERVER_ADDRESS+"/common/event/delete?user_id="+user_id+"&Id="+appointment_id+"&external_calendar_reference="+external_refernce_id+"&calendar_type="+calendar_type;*/
        return SERVER_ADDRESS+"/api/v1/common/event/delete?Id="+appointment_id+"&external_calendar_reference="+external_refernce_id+"&calendar_type="+calendar_type;
    }

    public static String getExchangeTokenUrl() {
        return SERVER_ADDRESS+"/api/v1/google/exchangetoken";
    }

    public static String getSetTokenUrl(String userId, String code) { return SERVER_ADDRESS+"/api/v1/common/settokens_withcode?code="+code+"&channel=App"; }

    public static String getAcceptOrRejectUrlForAppointments() { return SERVER_ADDRESS+"/common/event/accept"; }

    public static String getUploadFilesUrl() { return SERVER_ADDRESS+"";}

    public static String getCreateworkOrderUrl() { return SERVER_ADDRESS+"/api/v1/workorder/create"; }

    public static String getWorkOrderEventsUrl() { return SERVER_ADDRESS+"/api/v1/workorder/list"; }

    public static String getDeleteWorkOrderUrl() { return SERVER_ADDRESS+"/api/v1/workorder/delete"; }

    public static String getUpdateWorkOrderUrl() {
        return SERVER_ADDRESS+"/api/v1/workorder/update";
    }

    //TODO Change url
    public static String getRemoveSecondaryAccountUrl() { return SERVER_ADDRESS+""; }

    public static String getEventFetchUrl(String user_id, String appointment_id) {
        /*New Api Call  :::KSN*/
        /*return SERVER_ADDRESS+"/common/event/list?user_id="+user_id+"&Id="+appointment_id;*/
        return SERVER_ADDRESS+"/api/v1/common/event/list?Id="+appointment_id;
    }

    public static String getFetchExternalCalendarUrl(String user_id, String external_appointment_id) {
        return SERVER_ADDRESS+"/api/v1/common/event/list?calendar_type=office365"+"&Id="+external_appointment_id; }

    public static String getWorkOrderEventUrl() { return SERVER_ADDRESS+"/api/v1/workorder/list"; }

    public static String getSalesStatusUpdate() {
        /*new Api Call  :::KSN*/
        /*return SERVER_ADDRESS+"/api/salesstatus"; */
        return SERVER_ADDRESS+"/api/v1/salesstatus";
    }

    public static String getHomeScreenActivityUrl(String user_id, String last_mail_id, long last_gmail_id, String last_office365_id) {
        if (last_office365_id != null && last_office365_id.equals("0")) {
            /*New Api Call*/
            /*return SERVER_ADDRESS + "/api/activities?user_id="+user_id+"&last_id="+last_mail_id+"&last_gmail_time="+last_gmail_id;*/
            return SERVER_ADDRESS + "/api/v1/activities?last_id="+last_mail_id+"&last_gmail_time="+last_gmail_id;
        }
        else {
            /*New Api Call*/
          /*  return SERVER_ADDRESS + "/api/activities?user_id=" + user_id + "&last_id=" + last_mail_id + "&last_gmail_time=" + last_gmail_id ;*/
            return SERVER_ADDRESS + "/api/v1/activities?last_id=" + last_mail_id + "&last_gmail_time=" + last_gmail_id ;
        }// return SERVER_ADDRESS+"/api/activities?user_id="+user_id+"&last_id="+last_mail_id;
    }

    public static String getOneViewScreenAtivityUrl(String user_id, String customer_id, String last_mail_id) {
        /*new Api Call*/
        /*return SERVER_ADDRESS+"/api/activities?user_id="+user_id+"&customerid="+customer_id+"&last_id="+last_mail_id;*/
        return SERVER_ADDRESS+"/api/v1/activities?customerid="+customer_id+"&last_id="+last_mail_id;
    }

    public static String getPostActivityUrl() { return SERVER_ADDRESS+"/api/v1/activities"; }

    public static String getSaleStatusDataUrl(String user_id) {
        /*new Api Call  :::KSN*/
        //return SERVER_ADDRESS+"/api/settings/stages?user_id="+user_id;
        return SERVER_ADDRESS+"/api/v1/settings/stages";
    }
    public static String getDigitalBrochureUrl() {

        return SERVER_ADDRESS+"/api/v1/digitalbrochure";
    }
}

// http://mobilegurukul.azuyo.com
