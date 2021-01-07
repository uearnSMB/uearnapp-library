package smarter.uearn.money.utils.ServerAPIConnectors;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Build;
import android.os.Message;
import android.util.Log;

import smarter.uearn.money.agentslots.model.SlotCancel;
import smarter.uearn.money.agentslots.model.SlotData;
import smarter.uearn.money.chats.model.PMRequestChannel;
import smarter.uearn.money.chats.model.TechSupportRequest;
import smarter.uearn.money.models.AppStatus;
import smarter.uearn.money.models.ChangePasswordInfo;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GetGmailInfo;
import smarter.uearn.money.models.GetSmartMailInfo;
import smarter.uearn.money.models.GoogleAccount;
import smarter.uearn.money.models.GoogleAccountsList;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.HomeScreenMail;
import smarter.uearn.money.models.LoginInfo;
import smarter.uearn.money.models.NetworkDetails;
import smarter.uearn.money.models.Office365Info;
import smarter.uearn.money.models.OneViewScreenMail;
import smarter.uearn.money.models.PersonalJunkContact;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.models.SingleApiAppointmentModel;
import smarter.uearn.money.models.SmartMail;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.models.WorkOrderEntryInfo;
import smarter.uearn.money.notificationmessages.model.NotificationMessageResponse;
import smarter.uearn.money.notificationmessages.model.PostNotificationMessage;
import smarter.uearn.money.training.model.HelpDeskInfoData;
import smarter.uearn.money.training.model.MessageReadRequest;
import smarter.uearn.money.training.model.TrainerReview;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.JSONParser;
import smarter.uearn.money.utils.KnowlarityCallLogs;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.UserActivities;
import smarter.uearn.money.utils.webservice.Constants;
import smarter.uearn.money.utils.webservice.JsonParseAndOperate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bala on 6/30/2015.
 */
public class APIProvider {

    public static int INVALID_CREDENTIALS = 1001;
    public static int LOGIN_FAILED = 1002;
    public static int LOGIN_DENIED = 1020;

    public static int FORGOT_PASSWORD_FAILED = 1003;

    public static int EMAIL_ALREADY_EXISTS = 1004;
    public static int INVALID_EMAIL = 1005;
    public static int INVALID_LOGIN_METHOD = 1035;
    public static int CANNOT_CREATE_USER = 1006;

    public static int NUMBER_ALREADY_EXISTS = 1019;

    public static int CANNOT_CREATE_MAIL = 1007;
    public static int CANNOT_UPDATE_USER = 1008;
    public static int CANNOT_PUT_MAIL = 1010;

    public static int CANNOT_DELETE_MAIL = 1011;

    public static int FILE_UPLOAD_FAILED = 1012;

    public static int CANNOT_GET_USER = 1013;

    public static int UPDATE_MAIL = 1014;

    public static int INCORRECT_PASSWORD = 1015;

    public static int ERROR_READING_USER = 1016;

    public static int WORKORDER_ID_NULL = 535;

    public static int WORKORDER_TIME_ERROR = 135;
    public static int DUPLICATE_WORKORDER = 35;
    public static int INVALID_GRANT = 235;
    public static String LoginType = null;
    /* */
    public static int WORKORDER_QUOTA = 686;
    public static int DND = 1100;
    public static int SR_NOT_REGISTERED = 1101;
    public static int AGENET_NOT_VERIFIED = 1102;
    public static int INVALID_NUMBER = 110;
    public static int INVALID_PARAMETER = 112;
    public static int INVALID_AUTH_KEY = 113;
    public static int INVALID_REQUEST = 114;

    public static int SERVER_ALERT = 2001;
    public static int REPEAT_CALLS = 2002;
    public static int AGENT_NOT_REGISTERED = 2003;

    public static final String SERVER_ALERT_MESSAGE = "Your request is in process. You will be serviced shortly";


    public static class SmartUser_Post extends WaitingAPIAdapter<SmartUser, SmartUser> {

        public SmartUser_Post(SmartUser data, long requestCode, Activity activity, String message, API_Response_Listener<SmartUser> listener) {
            super(data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getRegistrationUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(SmartUser data) {
            return JSONParser.getJsonFromCmailUser(data);
        }

        @Override
        public SmartUser convertJSONToData(String response) {
            return JSONParser.getCmailUserFromString(response);
        }

        @Override
        public int intrepret_error(String error) {
            int failure_code = CANNOT_CREATE_USER;

            if (error.contains("cannot create the cmail_user") || error.contains("Cannot create user")) {
                failure_code = CANNOT_CREATE_USER;
            } else if (error.contains("Incorrect email address") || error.contains("invalid email")) {
                failure_code = INVALID_EMAIL;
            } else if (error.contains("Login is denied")) {
                failure_code = LOGIN_DENIED;
            } else if (error.contains("email has already been taken")) {
                String[] errors = error.split("\\s*;\\s*");
                if (errors.length == 2) {
                    LoginType = errors[1];
                }
                failure_code = EMAIL_ALREADY_EXISTS;
            } else if (error.contains("Registration with this phonenumber already exists")) {
                failure_code = APIProvider.NUMBER_ALREADY_EXISTS;
            }
            return failure_code;
        }
    }

    public static class CheckLogin extends WaitingAPIAdapter<LoginInfo, SmartUser> {
        public LoginInfo data;

        public CheckLogin(LoginInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<SmartUser> listener) {
            super(input_data, requestCode, activity, message, listener);
            this.data = input_data;
        }

        @Override
        public String getURL() {
            String url = Urls.getLoginUrl();

          /*  List<NameValuePair> postparams = new ArrayList<NameValuePair>();
            postparams.add(new BasicNameValuePair("email", data.userName));
            postparams.add(new BasicNameValuePair("password", data.password));
            postparams.add(new BasicNameValuePair("login_method", data.loginMethod));
            postparams.add(new BasicNameValuePair("ver", "3"));
            String paramString = URLEncodedUtils.format(postparams, "utf-8");*/

            ContentValues values = new ContentValues();
            values.put("email", data.userName);
            values.put("password", data.password);
            values.put("ver", "3");
            values.put("login_method", data.loginMethod);
            values.put("source_of_login", "UearnApp_android");
            values.put("appversion", AppConstants.APP_VERSION);
            values.put("device_info", getDeviceInfo());

            StringBuilder paramsBuilder = new StringBuilder();

            try {

                int i = 0;
                for (String key : values.keySet()) {
                    paramsBuilder.append((i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(values.get(key).toString(), "utf8"));
                    i++;
                }

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            url = url + "?" + paramsBuilder;
            //Log.i("SmarterBIZ","Login url is "+url);
            return url;
        }

        public String getDeviceInfo() {
            String  details =  "-RELEASE-"+ Build.VERSION.RELEASE
                    +"-SDK-"+ Build.VERSION.SDK_INT
                    +"-BOARD-"+Build.BOARD
                    +"-BRAND-"+Build.BRAND
                    +"-DISPLAY-"+Build.DISPLAY
                    +"-HARDWARE-"+Build.HARDWARE
                    +"-MANUFACTURER-"+Build.MANUFACTURER
                    +"-MODEL-"+Build.MODEL
                    +"-PRODUCT-"+Build.PRODUCT
                    +"-SERIAL-"+Build.SERIAL;

            return details;
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public SmartUser convertJSONToData(String response) {
            return JSONParser.getCmailUserFromString(response);
        }

        @Override
        public int intrepret_error(String error) {
            int failure_code = LOGIN_FAILED;

            if (error.contains("You are not registered with this account") || error.contains("Invalid Credentials")) {
                failure_code = INVALID_CREDENTIALS;
            } else if (error.contains("User does not exist") || error.contains("Email id does not exist")) {
                failure_code = INVALID_EMAIL;
            } else if (error.contains("Login is denied")) {
                failure_code = LOGIN_DENIED;
            } else if (error.contains("You are not registered with this account") || error.contains("please check your login_method")) {
                failure_code = INVALID_LOGIN_METHOD;
            }
            return failure_code;
        }
    }

    public static class ForgotPassword extends WaitingAPIAdapter<String, Void> {

        UserActivities userActivities;
        String email;

        public ForgotPassword(String input_data, long requestCode, Activity activity, String message, API_Response_Listener<Void> listener) {
            super(input_data, requestCode, activity, message, listener);
            this.email = input_data;
        }

        public ForgotPassword(String input_data, long requestCode, API_Response_Listener<Void> listener) {
            super(input_data, requestCode, null, null, listener);
            this.email = input_data;
        }

        @Override
        public String getURL() {


            String url = Urls.getForgotPasswordUrl();
            ContentValues values = new ContentValues();
            values.put("email", email);
            url = url + "?email=" + email;
            return url;
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public JSONObject convertDataToJSON(String data) {

            System.out.print(data);
            return super.convertDataToJSON(data);
        }

    }


    public static class ChangePassword extends WaitingAPIAdapter<ChangePasswordInfo, Void> {
        ChangePasswordInfo changePasswordInfo;

        public ChangePassword(ChangePasswordInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<Void> listener) {
            super(input_data, requestCode, activity, message, listener);
            this.changePasswordInfo = input_data;
        }

        @Override
        public String getURL() {

            String url = Urls.getChangePasswordUrl();
            ContentValues values = new ContentValues();
            values.put("user_id", changePasswordInfo.user_id);
            values.put("password", changePasswordInfo.oldPassword);
            values.put("new_password", changePasswordInfo.newPassword);
            values.put("confirm_password", changePasswordInfo.confirmPassword);

            StringBuilder paramsBuilder = new StringBuilder();

            try {

                int i = 0;
                for (String key : values.keySet()) {
                    paramsBuilder.append((i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(values.get(key).toString(), "utf8"));
                    i++;
                }

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            url = url + "?" + paramsBuilder;

            return url;
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }


        @Override
        public int intrepret_error(String error) {
            //Log.i("Tag","Im Api provider Erroe  ;"+error);
            int failure_code = ERROR_READING_USER;
            if (error.contains("Error reading user") || error.contains("incorrect user id")) {
                failure_code = ERROR_READING_USER;
            } else if (error.contains("incorrect original password")) {
                failure_code = INCORRECT_PASSWORD;
            }
            return failure_code;
        }
    }

    public static class SmartMail_Post extends WaitingAPIAdapter<SmartMail, SmartMail> {

        public SmartMail_Post(SmartMail input_data, long requestCode, Activity activity, String message, API_Response_Listener<SmartMail> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getCreateCmailUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(SmartMail data) {
            return JSONParser.getJsonFromCmailMail(data);
        }

        @Override
        public SmartMail convertJSONToData(String response) {
            return JSONParser.getCmailMailFromString(response);
        }

        @Override
        public int intrepret_error(String error) {
            int failure_code = CANNOT_CREATE_MAIL;

            if (error.contains("Cannot find cmail user")) {
                failure_code = INVALID_CREDENTIALS;
            } else if (error.contains("error creating cmail")) {
                failure_code = CANNOT_CREATE_MAIL;
            }
            return failure_code;
        }
    }

    public static class SmartUser_Get extends WaitingAPIAdapter<String, SmartUser> {
        String userId;

        public SmartUser_Get(String input_data, long requestCode, API_Response_Listener<SmartUser> listener) {
            super(input_data, requestCode, null, null, listener);
            this.userId = input_data;
        }

        @Override
        public String getURL() {
            return Urls.getSettingsUrl(userId);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public SmartUser convertJSONToData(String response) {
            return JSONParser.getCmailUserFromString(response);
        }

        @Override
        public int intrepret_error(String error) {
            return CANNOT_GET_USER;
        }

    }


    public static class SmartUser_Put extends WaitingAPIAdapter<SmartUser, SmartUser> {
        SmartUser smartUser;

        public SmartUser_Put(SmartUser input_data, long requestCode, Activity activity, String message, API_Response_Listener<SmartUser> listener) {
            super(input_data, requestCode, activity, message, listener);
            this.smartUser = input_data;
        }

        public SmartUser_Put(SmartUser input_data, long requestCode, API_Response_Listener<SmartUser> listener) {
            super(input_data, requestCode, null, null, listener);
            this.smartUser = input_data;
        }

        @Override
        public String getURL() {
            return Urls.getSettingsUrl(smartUser.getId());
        }

        @Override
        public String getHttpMethod() {
            return "PUT";
        }

        @Override
        public JSONObject convertDataToJSON(SmartUser data) {
            return JSONParser.getJsonFromCmailUser(data);
        }

        @Override
        public SmartUser convertJSONToData(String response) {
            return JSONParser.getCmailUserFromString(response);
        }

        @Override
        public int intrepret_error(String error) {
            return CANNOT_UPDATE_USER;
        }
    }

    public static class SmartMail_Get_Child_Gmails extends WaitingAPIAdapter<GetGmailInfo, ArrayList<OneViewScreenMail>> {
        String userid;
        String cname;
        String before;

        public SmartMail_Get_Child_Gmails(GetGmailInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<ArrayList<OneViewScreenMail>> listener) {
            super(input_data, requestCode, activity, message, listener);
            userid = input_data.userId;
            cname = input_data.cname;
            before = input_data.before;
        }

        public SmartMail_Get_Child_Gmails(GetGmailInfo input_data, long requestCode, API_Response_Listener<ArrayList<OneViewScreenMail>> listener) {
            super(input_data, requestCode, null, null, listener);
            userid = input_data.userId;
            cname = input_data.cname;
            before = input_data.before;
        }

        @Override
        public String getURL() {
            String url = Urls.getgmailsUrl();
           /* List<NameValuePair> postparams = new ArrayList<NameValuePair>();
            postparams.add(new BasicNameValuePair("user_id", userid));
            postparams.add(new BasicNameValuePair("cname", cname));
            if (before != null)
                postparams.add(new BasicNameValuePair("before", before));

            String paramString = URLEncodedUtils.format(postparams, "utf-8");*/


            ContentValues values = new ContentValues();
            values.put("user_id", userid);
            values.put("cname", cname);
            if (before != null) values.put("before", before);


            StringBuilder paramsBuilder = new StringBuilder();

            try {

                int i = 0;
                for (String key : values.keySet()) {
                    paramsBuilder.append((i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(values.get(key).toString(), "utf8"));
                    i++;
                }

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            url += "?" + paramsBuilder;
            return url;
        }

        @Override
        public ArrayList<OneViewScreenMail> convertJSONToData(String response) {
            return JSONParser.getSmartMailArrayFromString(response);
        }
    }


    public static class Get_Gmail extends WaitingAPIAdapter<GetGmailInfo, SmartMail> {
        String userid;
        String messageId;

        public Get_Gmail(GetGmailInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<SmartMail> listener) {
            super(input_data, requestCode, activity, message, listener);
            userid = input_data.userId;
            messageId = input_data.messageId;
        }

        @Override
        public String getURL() {
            String url = Urls.getgmailUrl(userid, messageId);
            return url;
        }

        @Override
        public SmartMail convertJSONToData(String response) {
            // Log.i("Tag", "Response in api provider is " + response);
            SmartMail smartMail = new SmartMail();
            smartMail = JSONParser.getRequiredStringFromGmailResp(response);
            return smartMail;
        }
    }


    public static class Get_GmailAccounts extends WaitingAPIAdapter<String, GoogleAccountsList> {
        String userid;

        public Get_GmailAccounts(String input_data, long requestCode, API_Response_Listener<GoogleAccountsList> listener) {
            super(input_data, requestCode, null, null, listener);
            userid = input_data;
        }

        @Override
        public String getURL() {
            String url = Urls.getGmailAccountsUrl(userid);
            return url;
        }

        @Override
        public GoogleAccountsList convertJSONToData(String response) {
            GoogleAccountsList googleAccountsList = JSONParser.getArrayListFromJsonArray(response);
            return googleAccountsList;
        }
    }

    public static class Smart_Gmail_Post extends WaitingAPIAdapter<GetGmailInfo, String> {

        String user_id, to, subject, messageText;

        public Smart_Gmail_Post(GetGmailInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            user_id = input_data.userId;
            to = input_data.to;
            subject = input_data.subject;
            messageText = input_data.content;
        }

        @Override
        public String getURL() {
            String url = Urls.getgmailComposeUrl();
            /*List<NameValuePair> postparams = new ArrayList<NameValuePair>();
            postparams.add(new BasicNameValuePair("user_id", user_id));
            postparams.add(new BasicNameValuePair("to", to));
            postparams.add(new BasicNameValuePair("subject", subject));
            postparams.add(new BasicNameValuePair("text", messageText));
            String paramString = URLEncodedUtils.format(postparams, "utf-8");
            url = url + "?" + paramString;*/
            return url;
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(GetGmailInfo data) {
            return JSONParser.getJsonObjForGmail(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class Compose_Smart_Gmail extends WaitingAPIAdapter<GetGmailInfo, String> {

        String user_id, to, subject, messageText;

        public Compose_Smart_Gmail(GetGmailInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            user_id = input_data.userId;
            to = input_data.to;
            subject = input_data.subject;
            messageText = input_data.content;
        }

        @Override
        public String getURL() {
            String url = Urls.getComposeGmailUrl();
            /*List<NameValuePair> postparams = new ArrayList<NameValuePair>();
            postparams.add(new BasicNameValuePair("user_id", user_id));
            postparams.add(new BasicNameValuePair("to", to));
            postparams.add(new BasicNameValuePair("subject", subject));
            postparams.add(new BasicNameValuePair("text", messageText));
            String paramString = URLEncodedUtils.format(postparams, "utf-8");
            url = url + "?" + paramString;*/
            return url;
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(GetGmailInfo data) {
            return JSONParser.getJsonForComposeGmail(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class SmartGmail_Reply extends WaitingAPIAdapter<GetGmailInfo, String> {

        String user_id, to, subject, message_id, message;

        public SmartGmail_Reply(GetGmailInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            user_id = input_data.userId;
            to = input_data.to;
            subject = input_data.subject;
            message_id = input_data.messageId;
            message = input_data.content;
        }

        @Override
        public String getURL() {
            String url = Urls.getgmailReplyUrl();
//            List<NameValuePair> postparams = new ArrayList<NameValuePair>();
//            postparams.add(new BasicNameValuePair("user_id", user_id));
//            postparams.add(new BasicNameValuePair("to", to));
//            postparams.add(new BasicNameValuePair("subject", subject));
//            postparams.add(new BasicNameValuePair("message_id", message_id));
//            postparams.add(new BasicNameValuePair("text", message));
//            String paramString = URLEncodedUtils.format(postparams, "utf-8");
//            url = url + "?" + paramString;

            return url;

        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(GetGmailInfo data) {
            return JSONParser.getJsonObjForGmail(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class GetUearnVoiceTestInfo extends WaitingAPIAdapter<UserActivities, String> {

        UserActivities userActivities;
        SmartUser smartUser;
        String userId;

        public GetUearnVoiceTestInfo(UserActivities input_data, long requestCode, Activity activity, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, msg, listener);
            userActivities = input_data;
            userId = input_data.user_id;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            Log.d("VOICETEST:-",Urls.getUearnVoiceTestInfoUrl(user_id));
            return Urls.getUearnVoiceTestInfoUrl(user_id);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getUearnVoiceTestInfo(response, userId);
        }

    }

    public static class Get_EnableOrDisable_Gmails extends WaitingAPIAdapter<GetGmailInfo, String> {

        String userId, logintype;
        boolean enableGmailMail;
        String id;


        public Get_EnableOrDisable_Gmails(GetGmailInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            userId = input_data.userId;
            enableGmailMail = input_data.enableGmailMails;
            id = input_data.getId();
            logintype = input_data.getLoginType();
        }

        @Override
        public String getURL() {
            return Urls.getEnableOrDisableGmailUrl(id, userId, enableGmailMail, logintype);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class Get_ConnectedCustomerInfo extends WaitingAPIAdapter<GetCalendarEntryInfo, String> {

        String userId, calendarType;
        String start_date, end_date;
        String appointment_start_id, appointment_end_id;
        int skip;

        public Get_ConnectedCustomerInfo(GetCalendarEntryInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            userId = input_data.user_id;
            start_date = input_data.event_start_date;
            end_date = input_data.event_end_date;
            skip = input_data.limit;
            calendarType = input_data.calendar_type;

            appointment_start_id = input_data.appointmentStartId;
            appointment_end_id = input_data.appointmentEndId;
        }

        @Override
        public String getURL() {
            String url = Urls.getCalendarEnventsUrl(userId, start_date, end_date, skip, calendarType);

            if ((!appointment_start_id.isEmpty()) && (!appointment_end_id.isEmpty())) {
                url = Urls.getCalendarEnventsUrl(userId, start_date, end_date, skip, calendarType, appointment_start_id, appointment_end_id);
            }

            return url;
        }

        @Override
        public String convertJSONToData(String response) {
            return response;
        }
    }

    public static class Get_CalendarEvents extends WaitingAPIAdapter<GetCalendarEntryInfo, ArrayList<GetCalendarEntryInfo>> {

        String userId, calendarType;
        String start_date, end_date;
        String appointment_start_id, appointment_end_id;
        int skip;

        public Get_CalendarEvents(GetCalendarEntryInfo input_data, long requestCode, API_Response_Listener<ArrayList<GetCalendarEntryInfo>> listener) {
            super(input_data, requestCode, null, null, listener);
            userId = input_data.user_id;
            start_date = input_data.event_start_date;
            end_date = input_data.event_end_date;
            skip = input_data.limit;
            calendarType = input_data.calendar_type;

            appointment_start_id = input_data.appointmentStartId;
            appointment_end_id = input_data.appointmentEndId;
        }

        @Override
        public String getURL() {
            String url = Urls.getCalendarEnventsUrl(userId, start_date, end_date, skip, calendarType);

            if ((!appointment_start_id.isEmpty()) && (!appointment_end_id.isEmpty())) {
                url = Urls.getCalendarEnventsUrl(userId, start_date, end_date, skip, calendarType, appointment_start_id, appointment_end_id);
            }

            //Log.d("Events check", url);
            // Log.i("ApiProvider","url for getting events is "+url);
/*            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("start_date", start_date);
            values.put("end_date", end_date);
            values.put("calendar_type", calendarType);
            values.put("top", 50);
            values.put("skip", skip);
            StringBuilder paramsBuilder = new StringBuilder();

            try {
                int i = 0;
                for (String key : values.keySet()) {
                    paramsBuilder.append((i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(values.get(key).toString(), "utf8"));
                    i++;
                }

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            url = url + "?" + paramsBuilder;*/
            // Log.i("ApiProvider", "Get calendar events url is " + url);
            return url;
        }

        @Override
        public ArrayList<GetCalendarEntryInfo> convertJSONToData(String response) {
            return JSONParser.getArrayListOfCalEventsFromJson(response);
        }
    }

    public static class Update_CalendarEvents extends WaitingAPIAdapter<GetCalendarEntryInfo, String> {

        public Update_CalendarEvents(GetCalendarEntryInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getUpdateCalendarUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(GetCalendarEntryInfo data) {
            return JSONParser.getJSonForUpdateCalendar(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class Update_Appointment extends WaitingAPIAdapter<GetCalendarEntryInfo, String> {

        public Update_Appointment(GetCalendarEntryInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            //Log.i("CompletedStatusFlow","Update calendar flow");
            return Urls.getUpdateCalendarUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(GetCalendarEntryInfo data) {
            //Log.i("ApiProvider","Json for Update reminder is "+JSONParser.getJsonForUpdatingFewFieldsInAppointment(data));
            return JSONParser.getJsonForUpdatingFewFieldsInAppointment(data);
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.i("ApiProvider", "response is " + response);
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class Update_complete_Appointment extends WaitingAPIAdapter<GetCalendarEntryInfo, String> {

        public Update_complete_Appointment(GetCalendarEntryInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            //Log.i("CompletedStatusFlow","Update calendar flow");
            return Urls.getUpdateCalendarUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(GetCalendarEntryInfo data) {
            //Log.i("ApiProvider","Json for Update reminder is "+JSONParser.getJsonForUpdatingFewFieldsInAppointment(data));
            return JSONParser.getJsonForUpdatingFewFieldsInAppointment(data);
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.i("ApiProvider", "response is " + response);
            return response;
        }
    }

    public static class Delete_CalendarEvent extends WaitingAPIAdapter<GetCalendarEntryInfo, String> {
        String external_calendar_reference, appointment_id, user_id, calendar_type;

        public Delete_CalendarEvent(GetCalendarEntryInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            external_calendar_reference = input_data.getExternal_calendar_reference();
            appointment_id = input_data.getAppointment_id();
            user_id = SmarterSMBApplication.SmartUser.getId();
            calendar_type = SmarterSMBApplication.SmartUser.getLogin_method();
        }

        @Override
        public String getURL() {
            //Log.i("ApiProvider","Url for delete is "+Urls.getDeleteCalendarEventUrl(user_id, appointment_id, external_calendar_reference, calendar_type));
            return Urls.getDeleteCalendarEventUrl(user_id, appointment_id, external_calendar_reference, calendar_type);
        }


        @Override
        public String getHttpMethod() {
            return "DELETE";
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.i("ApiProvider","Response in deleteee is "+response);
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }


    public static class Set_GroupsOfUser extends WaitingAPIAdapter<ArrayList<GroupsUserInfo>, String> {

        public Set_GroupsOfUser(ArrayList<GroupsUserInfo> input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getSetGroupUserUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(ArrayList<GroupsUserInfo> data) {
            // Log.i("ApiProvider","Group accept payload is "+JSONParser.convertGroupsInfoToJson(data));
            return JSONParser.convertGroupsInfoToJson(data);
        }

        @Override
        public int intrepret_error(String error) {
            int failure_code = EMAIL_ALREADY_EXISTS;
            if (error.contains("phonenumber already exists")) {
                failure_code = NUMBER_ALREADY_EXISTS;
            } else if (error.contains("Email has already been taken")) {
                failure_code = EMAIL_ALREADY_EXISTS;
            }
            return failure_code;
        }

        @Override
        public String convertJSONToData(String response) {
            return "Success";
        }
    }

    public static class Get_GroupsUser extends WaitingAPIAdapter<String, ArrayList<GroupsUserInfo>> {

        String user_id;

        public Get_GroupsUser(String input_data, long requestCode, Activity activity, String message, API_Response_Listener<ArrayList<GroupsUserInfo>> listener) {
            super(input_data, requestCode, activity, message, listener);
            user_id = input_data;
        }

        @Override
        public String getURL() {
            return Urls.getGetGroupUserUrl(user_id);
        }

        @Override
        public ArrayList<GroupsUserInfo> convertJSONToData(String response) {
            return JSONParser.getGroupUsersInfoFromJsonObj(response);
        }
    }

    public static class Subscribe_ForGoogleNotifications extends WaitingAPIAdapter<GoogleAccount, String> {
        String user_id, devicekey;

        public Subscribe_ForGoogleNotifications(GoogleAccount input_data, long requestCode, API_Response_Listener<String> listener) {
            super(input_data, requestCode, null, null, listener);
            user_id = input_data.getUser_id();
            devicekey = input_data.getDevicekey();
        }

        @Override
        public String getURL() {
            // Log.i("ApiProvider","In subscribe url call "+Urls.getSubscribeNotificationsUrl(user_id, devicekey));
            return Urls.getSubscribeNotificationsUrl(user_id, devicekey);
        }

        @Override
        public String convertJSONToData(String response) {
            return super.convertJSONToData(response);
        }
    }


    public static class UnSubscribe_GoogleNotifications extends WaitingAPIAdapter<GoogleAccount, String> {


        public UnSubscribe_GoogleNotifications(GoogleAccount input_data, long requestCode, API_Response_Listener<String> listener) {
            super(input_data, requestCode, null, null, listener);
        }

        @Override
        public String getURL() {
            return null;
        }
    }

    public static class Google_Calendar_EnableOrDisable extends WaitingAPIAdapter<GetGmailInfo, String> {
        String user_id;
        boolean calendarStatus;

        public Google_Calendar_EnableOrDisable(GetGmailInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            user_id = input_data.userId;
            calendarStatus = input_data.enableGoogleCalendar;
           /* if(calendarStatus)
                status = "enable";
            else
                status = "disable";*/

        }

        @Override
        public String getURL() {
            return Urls.getGoogleCalendarEnableOrDisableUrl(user_id, calendarStatus);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class Remove_EngineerOrSupervisor extends WaitingAPIAdapter<GroupsUserInfo, String> {

        String user_id, dependent_userId;

        public Remove_EngineerOrSupervisor(GroupsUserInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            user_id = input_data.userId;
            dependent_userId = input_data.dependent_userId;
        }

        @Override
        public String getURL() {
            return Urls.getDeleteSupervisorOrEngineerUrl(user_id, dependent_userId);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class Add_SecondaryGmail extends WaitingAPIAdapter<GoogleAccount, String> {

        public Add_SecondaryGmail(GoogleAccount input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getAddSecondaryEmailUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(GoogleAccount data) {
            return JSONParser.getJsonObjForAddingSecondaryGmail(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getErrorString(response);
        }
    }

    public static class Update_SecondaryAccount extends WaitingAPIAdapter<GoogleAccount, String> {

        public Update_SecondaryAccount(GoogleAccount input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getUpdateSecondaryEmailUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(GoogleAccount data) {
            //Log.i("Tag", "Json obj for Sec user is " + JSONParser.getJsonObjForUpdatingSecondaryAccount(data));
            return JSONParser.getJsonObjForUpdatingSecondaryAccount(data);
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.i("Tag", "Response in Adding Secondary email is " + response);
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class Remove_SecondaryAccount extends WaitingAPIAdapter<GoogleAccount, String> {

        public Remove_SecondaryAccount(GoogleAccount input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getRemoveSecondaryAccountUrl();
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.i("ApiProvider","Response in remove account is "+JSONParser.getSuccessStatusFromJson(response));
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class ExchangeTokenCall extends WaitingAPIAdapter<String, String> {

        public ExchangeTokenCall(String serverAuthCode, long requestCode, API_Response_Listener<String> listener) {
            super(serverAuthCode, requestCode, null, null, listener);
        }

        @Override
        public String getURL() {
            //Log.i("Tag","Exchange token Url is "+Urls.getExchangeTokenUrl());
            return Urls.getExchangeTokenUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(String data) {

            JSONObject jsonObject = new JSONObject();
            try {
                //jsonObject.put("idToken", idToken);
                jsonObject.put("serverAuthCode", data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Log.i("ApiProvider","Json object for Exchange token is "+jsonObject);
            return jsonObject;
        }
    }

    public static class Set_Tokens_For_o365 extends WaitingAPIAdapter<Office365Info, String> {
        Office365Info office365Info;

        public Set_Tokens_For_o365(Office365Info serverAuthCode, long requestCode, API_Response_Listener<String> listener) {
            super(serverAuthCode, requestCode, null, null, listener);
            //authCode = serverAuthCode;
            office365Info = serverAuthCode;
        }

        @Override
        public String getURL() {
            String userId = SmarterSMBApplication.SmartUser.getId();
            //Log.i("ApiProvider","Token url for Set tokens for office365 is "+Urls.getSetTokenUrl(userId, office365Info.code));
            return Urls.getSetTokenUrl(userId, office365Info.code);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.i("ApiProvider","Response for set tokens is "+ response);
            return super.convertJSONToData(response);
        }
    }

    public static class Get_WorkOrderEvents extends WaitingAPIAdapter<GetCalendarEntryInfo, ArrayList<WorkOrderEntryInfo>> {

        String userId, calendar_type;
        String start_date, end_date;


        public Get_WorkOrderEvents(GetCalendarEntryInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<ArrayList<WorkOrderEntryInfo>> listener) {
            super(input_data, requestCode, activity, message, listener);
            userId = input_data.user_id;
            start_date = input_data.event_start_date;
            end_date = input_data.event_end_date;
            calendar_type = input_data.calendar_type;
        }

        @Override
        public String getURL() {

            String url = Urls.getWorkOrderEventsUrl();
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("start_date", start_date);
            values.put("end_date", end_date);
            values.put("calendar_type", calendar_type);

            StringBuilder paramsBuilder = new StringBuilder();

            try {

                int i = 0;
                for (String key : values.keySet()) {
                    paramsBuilder.append((i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(values.get(key).toString(), "utf8"));
                    i++;
                }

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            url = url + "?" + paramsBuilder;
            return url;
        }

        @Override
        public ArrayList<WorkOrderEntryInfo> convertJSONToData(String response) {
            //todo; change json res
            return JSONParser.getArrayListOfWorkOrderEventsFromJson(response);
        }
    }

    public static class Get_WorkOrderEvent extends WaitingAPIAdapter<String, WorkOrderEntryInfo> {

        String userId = SmarterSMBApplication.SmartUser.getId();
        String id;

        public Get_WorkOrderEvent(String input_data, long requestCode, Activity activity, String message, API_Response_Listener<WorkOrderEntryInfo> listener) {
            super(input_data, requestCode, activity, message, listener);
            id = input_data;
        }

        public Get_WorkOrderEvent(String input_data, long requestCode, API_Response_Listener<WorkOrderEntryInfo> listener) {
            super(input_data, requestCode, null, null, listener);
            id = input_data;
        }

        @Override
        public String getURL() {

            String url = Urls.getWorkOrderEventUrl();

            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("id", id);


            StringBuilder paramsBuilder = new StringBuilder();

            try {

                int i = 0;
                for (String key : values.keySet()) {
                    paramsBuilder.append((i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(values.get(key).toString(), "utf8"));
                    i++;
                }

            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            url = url + "?" + paramsBuilder;
            return url;
        }

        @Override
        public WorkOrderEntryInfo convertJSONToData(String response) {

            //todo; change json res
            return JSONParser.getWorkOrderEventFromJson(response);
        }
    }


    public static class Create_WorkOrder extends WaitingAPIAdapter<WorkOrderEntryInfo, String> {

        public Create_WorkOrder(WorkOrderEntryInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getCreateworkOrderUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(WorkOrderEntryInfo data) {
            return JSONParser.getJsonForWorkOrder(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessAndErrorStatusFromJson(response);
        }

        @Override
        public int intrepret_error(String error) {
            int failure_code = 1;

            if (error.equals("workorder id is mandatory")) {
                failure_code = WORKORDER_ID_NULL;
            } else if (error.equals("The specified time range is empty.")) {
                failure_code = WORKORDER_TIME_ERROR;
            } else if (error.equals("You are creating a duplicate workorder with the same workorder id")) {
                failure_code = DUPLICATE_WORKORDER;
            } else if (error.equals("User quota issue unknown")) {
                failure_code = WORKORDER_QUOTA;
            }
            return failure_code;
        }
    }

    public static class Update_WorkOrder extends WaitingAPIAdapter<WorkOrderEntryInfo, String> {

        public Update_WorkOrder(WorkOrderEntryInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getUpdateWorkOrderUrl();
        }

        @Override
        public JSONObject convertDataToJSON(WorkOrderEntryInfo data) {
            return JSONParser.getJsonForUpdateWorkOrder(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }
    }

    public static class Get_CalendarEvent extends WaitingAPIAdapter<String, GetCalendarEntryInfo> {
        String user_id, appointment_id;

        public Get_CalendarEvent(String input_data, long requestCode, API_Response_Listener<GetCalendarEntryInfo> listener) {
            super(input_data, requestCode, null, null, listener);
            appointment_id = input_data;
            user_id = SmarterSMBApplication.SmartUser.getId();
        }

        @Override
        public String getURL() {
            return Urls.getEventFetchUrl(user_id, appointment_id);
        }

        @Override
        public GetCalendarEntryInfo convertJSONToData(String response) {
            return JSONParser.getCalendarEventInfoFromJson(response);
        }
    }

    public static class Get_External_Calendar_Event extends WaitingAPIAdapter<String, GetCalendarEntryInfo> {

        String user_id, appoi_id;

        public Get_External_Calendar_Event(String input_data, long requestCode, API_Response_Listener<GetCalendarEntryInfo> listener) {
            super(input_data, requestCode, null, null, listener);
            appoi_id = input_data;
            user_id = SmarterSMBApplication.SmartUser.getId();
        }

        @Override
        public String getURL() {
            return Urls.getFetchExternalCalendarUrl(user_id, appoi_id);
        }

        @Override
        public GetCalendarEntryInfo convertJSONToData(String response) {
            return JSONParser.getCalendarEventInfoFromJson(response);
        }
    }

    public static class Update_SalesStatus extends WaitingAPIAdapter<GetCalendarEntryInfo, String> {

        public Update_SalesStatus(GetCalendarEntryInfo input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public String getURL() {
            return Urls.getSalesStatusUpdate();
        }

        @Override
        public JSONObject convertDataToJSON(GetCalendarEntryInfo data) {
            return JSONParser.getJsonForSalesUpdate(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessAndErrorStatusFromJson(response);
        }
    }

    public static class Get_Activity_Mails extends WaitingAPIAdapter<GetSmartMailInfo, ArrayList<HomeScreenMail>> {
        String user_id, last_mail_id, last_office365_id;
        long last_gmail_id;
        //TODO Has to change the getSmartMailInfo data to activity

        public Get_Activity_Mails(GetSmartMailInfo input_data, long requestCode, API_Response_Listener<ArrayList<HomeScreenMail>> listener) {
            super(input_data, requestCode, null, null, listener);
            user_id = input_data.userId;
            last_mail_id = input_data.lastCmailId;
            last_gmail_id = input_data.lastGmailTime;
            last_office365_id = input_data.lastOffice365Time;
        }

        @Override
        public String getURL() {
            return Urls.getHomeScreenActivityUrl(user_id, last_mail_id, last_gmail_id, last_office365_id);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public ArrayList<HomeScreenMail> convertJSONToData(String response) {
            return JSONParser.getHomeScreenMailArrayFromString(response);
        }
    }

    public static class Get_All_Activity_Mails extends WaitingAPIAdapter<GetSmartMailInfo, ArrayList<OneViewScreenMail>> {
        String user_id, customer_id, last_mail_id;

        public Get_All_Activity_Mails(GetSmartMailInfo input_data, long requestCode, API_Response_Listener<ArrayList<OneViewScreenMail>> listener) {
            super(input_data, requestCode, null, null, listener);
            user_id = input_data.userId;
            customer_id = input_data.parentId;
            last_mail_id = input_data.lastCmailId;
        }

        @Override
        public String getURL() {
            return Urls.getOneViewScreenAtivityUrl(user_id, customer_id, last_mail_id);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public ArrayList<OneViewScreenMail> convertJSONToData(String response) {
            return JSONParser.getOneViewScreenMailFromString(response);
        }
    }

    public static class Post_Activity extends WaitingAPIAdapter<OneViewScreenMail, OneViewScreenMail> {

        public Post_Activity(OneViewScreenMail input_data, long requestCode, Activity activity, String message, API_Response_Listener<OneViewScreenMail> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public JSONObject convertDataToJSON(OneViewScreenMail data) {
            return JSONParser.convertOneViewScreenMailToJSON(data);
        }

        @Override
        public String getURL() {
            return Urls.getPostActivityUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public OneViewScreenMail convertJSONToData(String response) {
            // Log.i("ApiProvider","response of send sms is "+response.toString());
            return JSONParser.convertPostResponseToMail(response);
        }
    }

    public static class Delete_Activity extends WaitingAPIAdapter<OneViewScreenMail, String> {

        public Delete_Activity(OneViewScreenMail input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public JSONObject convertDataToJSON(OneViewScreenMail data) {
            return JSONParser.getDeleteJsonForActivity(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessAndErrorStatusFromJson(response);
        }

        @Override
        public String getURL() {
            return Urls.getPostActivityUrl();
        }

        @Override
        public String getHttpMethod() {
            return "DELETE";
        }
    }

    public static class Get_Sales_Status extends WaitingAPIAdapter<String, SalesStageInfo> {
        String user_id;

        public Get_Sales_Status(String input_data, long requestCode, API_Response_Listener<SalesStageInfo> listener) {
            super(input_data, requestCode, null, null, listener);
            user_id = input_data;
        }

        @Override
        public String getURL() {
            // Log.i("ApiProvider","Sales stage url is "+Urls.getSaleStatusDataUrl(user_id));
            return Urls.getSaleStatusDataUrl(user_id);
        }

        @Override
        public SalesStageInfo convertJSONToData(String response) {
            try {
                if(response != null && !(response.isEmpty())) {
                    JSONObject jsonObject = new JSONObject(response);
                    JsonParseAndOperate.parseSettingsData(null, jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return JSONParser.getSalesStageArrayList(response);
        }
    }

    /*Added DegitalBrochure Api :::KSN*/
    public static class GetDigitalBrochure extends WaitingAPIAdapter<String, SmartUser> {
        String userId;

        public GetDigitalBrochure(String input_data, long requestCode, API_Response_Listener<SmartUser> listener) {
            super(input_data, requestCode, null, null, listener);
            this.userId = input_data;
        }

        @Override
        public String getURL() {
            return Urls.getDigitalBrochureUrl();
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public SmartUser convertJSONToData(String response) {
            return JSONParser.getCmailUserFromString(response);
        }

        @Override
        public int intrepret_error(String error) {
            return CANNOT_GET_USER;
        }

    }

    public static class Get_All_Team_Members extends WaitingAPIAdapter<String, ArrayList<GroupsUserInfo>> {
        String user_id;

        public Get_All_Team_Members(String input_data, long requestCode, API_Response_Listener<ArrayList<GroupsUserInfo>> listener) {
            super(input_data, requestCode, null, null, listener);
            user_id = input_data;
        }

        @Override
        public String getURL() {
            return Urls.getAllTeamMembersUrl(user_id);
        }

        @Override
        public ArrayList<GroupsUserInfo> convertJSONToData(String response) {
            return JSONParser.getGroupsUserInfoFromJson(response);
        }
    }

    //Added By Srinath

    public static class GetSalesGraphUrl extends WaitingAPIAdapter<UserActivities, UserActivities> {

        UserActivities userActivities;

        public GetSalesGraphUrl(UserActivities input_data, long requestCode, Activity activity, API_Response_Listener<UserActivities> listener) {
            super(input_data, requestCode, activity, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
            /*Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -15);
            Date startTime = cal.getTime();
            cal.setTime(new Date());
            Date endTime = cal.getTime();
            String requiredStartTime = CommonUtils.getTimeFormatInISO(startTime);
            String requiredEndTime = CommonUtils.getTimeFormatInISO(endTime);
*/

            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getSalesGraphUrl(user_id, requiredStartTime, requiredEndTime);
            //return "http://smbdev.azurewebsites.net/status/graphs?user_id=257&start_date=2016-10-29T08:45:31.822Z&end_date=2016-10-30T08:45:31.822Z&criteriatype={%22salesstatus%22:%20{%22type%22:[%22plotly%22,%22excel%22]},%20%22statusoccurance%22:%20{%22type%22:[%22plotly%22,%22excel%22]},%20%22salesfunnelcumulative%22:%20{%22type%22:[%22excel%22,%20%22dashboard%22]},%20%22status%22:%20{%22type%22:[%22plotly%22,%22excel%22]},%20%22funnel%22:%20{%22type%22:%22salesguyemail%22},%20%22ordervalue%22:%20{%22type%22:%20[%22plotly%22,%20%22excel%22]}}&forleads=false";
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public UserActivities convertJSONToData(String response) {
            return JSONParser.getSalesReports(response);
        }
    }

    /*Activities Graph Api :::KSN*/
    public static class GetActivitiesGraphUrl extends WaitingAPIAdapter<UserActivities, UserActivities> {

        UserActivities userActivities;

        public GetActivitiesGraphUrl(UserActivities input_data, long requestCode, API_Response_Listener<UserActivities> listener) {
            super(input_data, requestCode, null, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
         /*   Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -15);
            Date startTime = cal.getTime();
            cal.setTime(new Date());
            Date endTime = cal.getTime();
            String requiredStartTime = CommonUtils.getTimeFormatInISO(startTime);
            String requiredEndTime = CommonUtils.getTimeFormatInISO(endTime);*/

            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getActivitiesGraphUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public UserActivities convertJSONToData(String response) {
            return JSONParser.getActivitiesReports(response);
        }
    }

    public static class GetAppointmentGraph extends WaitingAPIAdapter<UserActivities, UserActivities> {

        UserActivities userActivities;

        public GetAppointmentGraph(UserActivities input_data, long requestCode, API_Response_Listener<UserActivities> listener) {
            super(input_data, requestCode, null, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getAppointmentGraphUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public UserActivities convertJSONToData(String response) {
            return JSONParser.getAppointmentReports(response);
        }
    }

    public static class GetTalkTimeGraph extends WaitingAPIAdapter<UserActivities, UserActivities> {

        UserActivities userActivities;
        SmartUser smartUser;

        public GetTalkTimeGraph(UserActivities input_data, long requestCode, API_Response_Listener<UserActivities> listener) {
            super(input_data, requestCode, null, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getTalkTimeGraphUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public UserActivities convertJSONToData(String response) {
            return JSONParser.getTalkTimeReports(response, userActivities.userMail);
        }
    }

    public static class GetApplicationversion extends WaitingAPIAdapter<String, Integer> {

        public GetApplicationversion(String input_data, long requestCode, API_Response_Listener<Integer> listener) {
            super(input_data, requestCode, null, null, listener);
        }

        @Override
        public String getURL() {
            return Urls.getApplicationVersion();
        }

        @Override
        public Integer convertJSONToData(String response) {
            return JSONParser.getApplicationVersion(response);
        }
    }


    public static class GetAppStatus extends WaitingAPIAdapter<String, Integer> {

        public GetAppStatus(String input_data, long requestCode, API_Response_Listener<Integer> listener) {
            super(input_data, requestCode, null, null, listener);
        }

        @Override
        public String getURL() {
            return Urls.getAppStatus("UserId");
        }

        @Override
        public Integer convertJSONToData(String response) {
            //return JSONParser.getApplicationVersion(response);
            return 0;
        }
    }

    public static class PostAppStatus extends WaitingAPIAdapter<AppStatus, AppStatus> {

        AppStatus appStatus;

        public PostAppStatus(AppStatus input_data, long requestCode, API_Response_Listener<AppStatus> listener) {
            super(input_data, requestCode, null, null, listener);
            appStatus = input_data;
        }

        @Override
        public String getURL() {
            return Urls.postAppStatus();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(AppStatus data) {
            return JSONParser.getJsonAppStatus(data);
        }

        @Override
        public AppStatus convertJSONToData(String response) {
            //Log.d("test", "response" + response);
            //return JSONParser.getApplicationVersion(response);
            return null;
        }
    }

    public static class GetFollowUps extends WaitingAPIAdapter<UserActivities, String> {

        UserActivities userActivities;
        SmartUser smartUser;

        public GetFollowUps(UserActivities input_data, long requestCode, API_Response_Listener<String> listener) {
            super(input_data, requestCode, null, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getFollowupsUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getFollwupsReports(response);
        }
    }


    public static class GetDashboardReport extends WaitingAPIAdapter<UserActivities, ArrayList<HashMap<String, String>>> {

        UserActivities userActivities;
        SmartUser smartUser;

        public GetDashboardReport(UserActivities input_data, long requestCode, API_Response_Listener<ArrayList<HashMap<String, String>>> listener) {
            super(input_data, requestCode, null, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            //Log.d("dashboard drill down", "url : " + Urls.getDashboardUrl(user_id, requiredStartTime, requiredEndTime));
            //return Urls.getDashboardUrl(user_id, requiredStartTime, requiredEndTime);
            return Urls.getnewDashboardUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public ArrayList<HashMap<String, String>> convertJSONToData(String response) {
            //return JSONParser.getDashboardReports(response);
            return JSONParser.getLatestDashboardReports(response);
            //return response;
        }
    }

    //GET OYO DASHBOARD INFO

    public static class GetOYODashboard extends WaitingAPIAdapter<UserActivities, String> {

        UserActivities userActivities;
        SmartUser smartUser;
        String userId;

        public GetOYODashboard(UserActivities input_data, long requestCode, Activity activity, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, msg, listener);
            userActivities = input_data;
            userId = input_data.user_id;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getOyoDashboardUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getOyoDashInfo(response, userId);
        }

//        @Override
//        public String convertJSONToData(String response) {
//            System.out.print(response);
//            getOyoDashInfo
//
//            //return JSONParser.getOyoDashboardReports(response);
//            //return response;
//        }
    }

//    public static class GetOYODashboard extends WaitingAPIAdapter<UserActivities, ArrayList<HashMap<String, String>>> {
//
//        UserActivities userActivities;
//        SmartUser smartUser;
//
//        public GetOYODashboard(UserActivities input_data, long requestCode, Activity activity, String msg, API_Response_Listener<ArrayList<HashMap<String, String>>> listener) {
//            super(input_data, requestCode, activity, msg, listener);
//            userActivities = input_data;
//        }
//
//        @Override
//        public String getURL() {
//            String user_id = userActivities.user_id;
//            String requiredStartTime = userActivities.startTime;
//            String requiredEndTime = userActivities.endTime;
//            return Urls.getOyoDashboardUrl(user_id, requiredStartTime, requiredEndTime);
//        }
//
//        @Override
//        public ArrayList<HashMap<String, String>> convertJSONToData(String response) {
//            return JSONParser.getOyoDashboardReports(response);
//        }
//    }

    public static class GetQdeDashboardReport extends WaitingAPIAdapter<UserActivities, ArrayList<HashMap<String, String>>> {

        UserActivities userActivities;
        SmartUser smartUser;

        public GetQdeDashboardReport(UserActivities input_data, long requestCode, API_Response_Listener<ArrayList<HashMap<String, String>>> listener) {
            super(input_data, requestCode, null, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            //Log.d("dashboard drill down", "url : " + Urls.getDashboardUrl(user_id, requiredStartTime, requiredEndTime));
            //return Urls.getDashboardUrl(user_id, requiredStartTime, requiredEndTime);
            return Urls.getQdeDashboard(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public ArrayList<HashMap<String, String>> convertJSONToData(String response) {
            return JSONParser.parseQdeDashboardRespone(response);

        }
    }

    public static class GetMissedDashboardReport extends WaitingAPIAdapter<UserActivities, ArrayList<HashMap<String, String>>> {

        UserActivities userActivities;
        SmartUser smartUser;

        public GetMissedDashboardReport(UserActivities input_data, long requestCode, API_Response_Listener<ArrayList<HashMap<String, String>>> listener) {
            super(input_data, requestCode, null, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            //Log.d("dashboard drill down", "url : " + Urls.getDashboardUrl(user_id, requiredStartTime, requiredEndTime));
            //return Urls.getDashboardUrl(user_id, requiredStartTime, requiredEndTime);
            return Urls.getMisseddcallDashboardUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public ArrayList<HashMap<String, String>> convertJSONToData(String response) {
            return JSONParser.getMissedDashboardReports(response);
        }
    }

    public static class GetLeadsAndSourceDashboardReport extends WaitingAPIAdapter<UserActivities, ArrayList<ArrayList<String>>> {

        UserActivities userActivities;
        SmartUser smartUser;

        public GetLeadsAndSourceDashboardReport(UserActivities input_data, long requestCode, API_Response_Listener<ArrayList<ArrayList<String>>> listener) {
            super(input_data, requestCode, null, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getLeadsAndSourceDashboardUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public ArrayList<ArrayList< String>> convertJSONToData(String response) {
            return JSONParser.getleadsAndSourceResponse(response);
        }
    }

    public static class GetUserLocation extends WaitingAPIAdapter<UserActivities, ArrayList<HashMap<String, String>>> {

        UserActivities userActivities;
        SmartUser smartUser;

        public GetUserLocation(UserActivities input_data, long requestCode, API_Response_Listener<ArrayList<HashMap<String, String>>> listener) {
            super(input_data, requestCode, null, null, listener);
            userActivities = input_data;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getUserLocationUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public ArrayList<HashMap<String, String>> convertJSONToData(String response) {
            return JSONParser.getLatLong(response);
        }
    }

    public static class IvrMapping extends WaitingAPIAdapter<String, String> {

        String customerNumber;

        public IvrMapping(String number, long requestCode, API_Response_Listener<String> listener) {
            super(number, requestCode, null, null, listener);
            customerNumber = number;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String printUrl = Urls.getIvrMappingUrl(userId, customerNumber);
            //Log.d("ameyo", printUrl);
            return printUrl;
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("ameyo", response);
            return JSONParser.getIvrMappingNumber(response);
        }
    }

    public static class IvrMappingUpload extends WaitingAPIAdapter<String[], String> {

        String customerNumber;
        long time = 0l;
        String timeStamp, endTimeStamp = "";

        public IvrMappingUpload(String[] number, long requestCode, API_Response_Listener<String> listener) {
            super(number, requestCode, null, null, listener);
            try {
                customerNumber = number[0];
                timeStamp = number[1];
                if(number.length > 1) {
                    endTimeStamp = number[2];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //  time = Long.valueOf(number[2]);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String printUrl = Urls.getIvrMappingUrl(userId, customerNumber, timeStamp, endTimeStamp);
            //Log.d("ameyo", printUrl);
            return printUrl;
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("ameyo", "+917204230507");
            return JSONParser.getIvrMappingNumber(response);
        }
    }

    public static class IvrMappingMissedUpload extends WaitingAPIAdapter<String[], String> {

        String customerNumber;
        long time = 0l;
        String timeStamp;

        public IvrMappingMissedUpload(String[] number, long requestCode, API_Response_Listener<String> listener) {
            super(number, requestCode, null, null, listener);
            customerNumber = number[0];
            timeStamp = number[1];
            //  time = Long.valueOf(number[2]);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String printUrl = Urls.getIvrMappingMissedCallUrl(userId, customerNumber, timeStamp);
            //Log.d("ameyoMissed", printUrl);
            return printUrl;
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("ameyoMissed", response);
            return JSONParser.getIvrMappingNumber(response);
        }
    }


    // Save Appointment Single API
    public static class SaveSingleApiAppointment extends WaitingAPIAdapter<SingleApiAppointmentModel, SingleApiAppointmentModel> {

        public SaveSingleApiAppointment(SingleApiAppointmentModel inputData, long requestCode, Activity activity, String message, API_Response_Listener<SingleApiAppointmentModel> listener) {
            super(inputData, requestCode, activity, message, listener);
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            inputData.setUserId(userId);
            // input_data to access the object
        }

        @Override
        public String getURL() {
            return Urls.getCallStatusUpdateUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(SingleApiAppointmentModel data) {
            return JSONParser.getSingleApiJson(data, 3);
        }

        @Override
        public SingleApiAppointmentModel convertJSONToData(String response) {
            //Log.d("Single API", response);
            return JSONParser.getSingleApiResponse(response);
        }
    }

    public static class GetDashboardUserLogs extends WaitingAPIAdapter<GetSmartMailInfo, ArrayList<HomeScreenMail>> {
        String user_id, last_mail_id, last_office365_id;
        long last_gmail_id;

        public GetDashboardUserLogs(GetSmartMailInfo input_data, long requestCode, API_Response_Listener<ArrayList<HomeScreenMail>> listener) {
            super(input_data, requestCode, null, null, listener);
            user_id = input_data.userId;
            last_mail_id = input_data.lastCmailId;
            last_gmail_id = input_data.lastGmailTime;
            last_office365_id = input_data.lastOffice365Time;
        }

        @Override
        public String getURL() {
            return Urls.getHomeScreenActivityUrl(user_id, last_mail_id, last_gmail_id, last_office365_id);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public ArrayList<HomeScreenMail> convertJSONToData(String response) {
            return JSONParser.getHomeScreenMailArrayFromString(response);
        }
    }

    /**
     * Added by dilip for personal and junk calls
     */
    public static class GetPersonalJunkNumberApi extends WaitingAPIAdapter<String, List<PersonalJunkContact>> {

        public GetPersonalJunkNumberApi(String inputData, long requestCode, Activity activity, String message, API_Response_Listener<List<PersonalJunkContact>> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            return Urls.getPersonalContactsUrl() + userId;
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public List<PersonalJunkContact> convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            return JSONParser.getPersonal(response);
        }
    }

    public static class AddPersonalNumberApi extends WaitingAPIAdapter<PersonalJunkContact, String> {

        public AddPersonalNumberApi(PersonalJunkContact inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getAddPersonalContactUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(PersonalJunkContact data) {
            return JSONParser.getJsonForPersonal(data);
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("Add personal contact", response);
            return response;
        }
    }

    public static class AddJunkNumberApi extends WaitingAPIAdapter<PersonalJunkContact, String> {

        public AddJunkNumberApi(PersonalJunkContact inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getAddJunkContactUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(PersonalJunkContact data) {
            return JSONParser.getJsonForJunk(data);
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("Add junk contact", response);
            return response;
        }
    }

    // Delete personal contact
    public static class DeletePersonalJunkApi extends WaitingAPIAdapter<List<PersonalJunkContact>, String> {

        public DeletePersonalJunkApi(List<PersonalJunkContact> inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            return Urls.getDeleteContactsUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(List<PersonalJunkContact> data) {
            return JSONParser.getDeletePersonalJunk(data);
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            return response;
        }
    }

   /* public static class KnowlarityClickToCall extends WaitingAPIAdapter<KnowlarityModel, String> {

        public KnowlarityClickToCall(KnowlarityModel inputData, long requestCode, boolean knowlarity, API_Response_Listener<String> listener) {
            super(inputData, requestCode, knowlarity, listener);
        }

        @Override
        public String getURL() {
            return Urls.getKnowlarityClickToCallUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(KnowlarityModel data) {
            return JSONParser.getKnolarityJson(data);
        }

        @Override
        public int intrepret_error(String error) {
            int failure_code = AGENET_NOT_VERIFIED;

            if (error.contains("Do Not Call registry")) {
                failure_code = DND;
            } else if (error.contains("Agent not verified")) {
                failure_code = AGENET_NOT_VERIFIED;
            } else if (error.contains("No active integration found")) {
                failure_code = SR_NOT_REGISTERED;
            }
            return failure_code;
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            return response;
        }
    }*/

    public static class KnowlarityClickToCall extends WaitingAPIAdapter<KnowlarityModel, String> {

        public KnowlarityClickToCall(KnowlarityModel inputData, long requestCode, boolean knowlarity, API_Response_Listener<String> listener) {
            super(inputData, requestCode, knowlarity, listener);
        }

        @Override
        public String getURL() {
            return Urls.getC2cClickToCallUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(KnowlarityModel data) {
            return JSONParser.getKnolarityJson(data);
        }

        @Override
        public int intrepret_error(String error) {
            int failure_code = AGENET_NOT_VERIFIED;

            if (error.contains("Do Not Call registry")) {
                failure_code = DND;
            } else if (error.contains("Agent not verified")) {
                failure_code = AGENET_NOT_VERIFIED;
            } else if (error.contains("No active integration found")) {
                failure_code = SR_NOT_REGISTERED;
            }
            return failure_code;
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            return response;
        }
    }

    public static class ReClickToCall extends WaitingAPIAdapter<KnowlarityModel, String> {

        public ReClickToCall(KnowlarityModel inputData, long requestCode, boolean knowlarity, API_Response_Listener<String> listener) {
            super(inputData, requestCode, knowlarity, listener);
            if(ApplicationSettings.containsPref(AppConstants.C2C_ENDPOINT)) {
                String url = ApplicationSettings.getPref(AppConstants.C2C_ENDPOINT, "");
                Log.d("ReClickToCall", url+" "+ApplicationSettings.getPref(AppConstants.USERINFO_ID, ""));
                if (url.contains("youearn.in")) {
                    String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    inputData.setUser_id(userId);
                }
            }

        }

        @Override
        public String getURL() {
            return Urls.getC2cClickToCallUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public int intrepret_error(String response) {

            Log.d("Knowlarity", "Failure" + response);

            int failure_code = 0;

            JSONParser.parseC2cRespon(response);
            String error = JSONParser.getRfErrorString(response);

            if(error.contains("Wrong Parameter customer_number") || error.contains("No Valid customer_number") || error.contains("Invalid Number Format")) {
                failure_code = INVALID_NUMBER;
            } else if(error.contains("Invalid/Missing Parameters")) {
                failure_code = INVALID_PARAMETER;
            } else if(error.contains("Invalid Auth Key")) {
                failure_code = INVALID_AUTH_KEY;
            }  else if(error.contains("Request Not Allowed")) {
                failure_code = INVALID_REQUEST;
            } else if (error.contains("in Do Not Call registry")) {
            /*} else if (error.contains("customer_number is in NDNC")) {*/
                failure_code = DND;
            /*} else if (error.contains("agent_number is in NDNC")) {*/
            } else if (error.contains("Agent not verified")) {
                failure_code = AGENET_NOT_VERIFIED;
            } else if (error.contains("No active integration found")) {
                failure_code = SR_NOT_REGISTERED;
            } else if (error.contains("Server Alert:")) {
                failure_code = SERVER_ALERT;
            } else if (error.contains("Repeated Calls:")) {
                failure_code = REPEAT_CALLS;
            } else if (error.contains("Call Failed! Agent Number") || error.contains("not registered")) {
                failure_code = AGENT_NOT_REGISTERED;
            }
            return failure_code;
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            Log.d("Knowlarity", "Success" + response);
            return JSONParser.parseC2cRespon(response);
        }
    }

    public static class C2cClickToCall extends WaitingAPIAdapter<KnowlarityModel, String> {

        public C2cClickToCall(KnowlarityModel inputData, long requestCode, boolean knowlarity, API_Response_Listener<String> listener) {
            super(inputData, requestCode, knowlarity, listener);
        }

        @Override
        public String getURL() {
            return Urls.getC2cClickToCallUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public int intrepret_error(String error) {
            int failure_code = AGENET_NOT_VERIFIED;

            if (error.contains("customer_number is in NDNC")) {
                failure_code = DND;
            } else if (error.contains("agent_number is in NDNC")) {
                failure_code = AGENET_NOT_VERIFIED;
            } else if (error.contains("No active integration found")) {
                failure_code = SR_NOT_REGISTERED;
            }
            return failure_code;
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            return response;
        }
    }

    public static class KnowlarityRegistration extends WaitingAPIAdapter<KnowlarityModel, String> {

        public KnowlarityRegistration(KnowlarityModel inputData, long requestCode, boolean knowlarity, API_Response_Listener<String> listener) {
            super(inputData, requestCode, knowlarity, listener);
        }

        @Override
        public String getURL() {
            return Urls.getKnowlarityRegistrationUrl();
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(KnowlarityModel data) {
            return JSONParser.getKnolarityRegistrationJson(data);
        }

        @Override
        public String convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            return response;
        }
    }

    public static class GetKnowlarityCallLogs extends WaitingAPIAdapter<KnowlarityModel, String[]> {

        KnowlarityModel knowlarityModel;
        public GetKnowlarityCallLogs(KnowlarityModel inputData, long requestCode, boolean knowlarity, API_Response_Listener<String[]> listener) {
            super(inputData, requestCode, knowlarity, listener);
            knowlarityModel = inputData;
        }

        @Override
        public String getURL() {
            String start_time = knowlarityModel.getStart_time();
            String end_time = knowlarityModel.getEnd_time();
            String knowlarity_number = knowlarityModel.getKnowlarity_number();
            String customer_number = knowlarityModel.getCustomer_number();
            String url = Urls.getKnowlarityCalllogs(start_time,end_time, knowlarity_number, customer_number);
            String encodedURL="";
           /* try {
                 encodedURL = URLEncoder.encode(url, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            return url;
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String[] convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            return JSONParser.getKnowlarityCallog(response);
        }
    }

    public static class GetCmailmailsCallRecording extends WaitingAPIAdapter<String, ArrayList<String[]>> {

        String cmail_id = "";
        public GetCmailmailsCallRecording(String inputData, long requestCode, Activity activity, String message, API_Response_Listener<ArrayList<String[]>> listener) {
            super(inputData, requestCode, activity, message, listener);
            cmail_id = inputData;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            if(cmail_id != null) {
                return Urls.getCmailCallrecording(userId, cmail_id);
            } else {
                return Urls.getCmailCallrecording(userId, "");
            }
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public ArrayList<String[]> convertJSONToData(String response) {
            //Log.d("Get Cmail Response and junk", response);
            return JSONParser.getCmailCallRecordings(response);
        }
    }

    public static class GetSettings extends WaitingAPIAdapter<String, String> {
        String user_id;

        public GetSettings(String input_data, long requestCode, API_Response_Listener<String> listener) {
            super(input_data, requestCode, null, null, listener);
            user_id = input_data;
        }

        @Override
        public String getURL() {
            return Urls.getSaleStatusDataUrl(user_id);
        }

        @Override
        public String convertJSONToData(String response) {
            try {
                if(response == null) {
                    return "";
                }
                if(response != null && !(response.isEmpty())) {
                    JSONObject jsonObject = new JSONObject(response);
                    JsonParseAndOperate.parseSettingsData(null, jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    public static class GetUser extends WaitingAPIAdapter<String[], String[]> {
        String simSerialNum;
        String id;

        public GetUser(String[] input_data, long requestCode, API_Response_Listener<String[]> listener) {
            super(input_data, requestCode, null, null, listener);
            this.simSerialNum = input_data[0];
            this.id = input_data[1];
        }

        @Override
        public String getURL() {
            return Urls.getUserUrl(simSerialNum, id);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String[] convertJSONToData(String response) {
            String voipNumber = "", nextAction = "", signoutReason = "", myProfile = "";
            String[] arr = new String[3];
            arr[0] = "";
            arr[1] = "";
            arr[2] = "";
            try {
                if(response != null && !(response.isEmpty())) {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("my_voip_number")) {
                        voipNumber = jsonObject.getString("my_voip_number");
                        ApplicationSettings.putPref(AppConstants.USER_VOIP_NUMBER, voipNumber);
                    }
                    if(jsonObject.has("my_server")) {
                        String serverUrl = jsonObject.getString("my_server");
                        if(serverUrl != null && !serverUrl.isEmpty() && !serverUrl.equalsIgnoreCase("null")) {
                            Urls.SERVER_ADDRESS = serverUrl;
                            Constants.DOMAIN_URL = Urls.SERVER_ADDRESS + "/";
                            ApplicationSettings.putPref(AppConstants.MY_SERVER_URL, serverUrl);
                        }
                    }
                    if (jsonObject.has("my_sio")) {
                        String mysiourl = jsonObject.getString("my_sio");
                        if (mysiourl != null && !(mysiourl.isEmpty()) && (mysiourl != " ") && !(mysiourl.equalsIgnoreCase("null"))) {
                            Urls.SERVER_SIO_ADDRESS = mysiourl;
                        }
                    }
                    if (jsonObject.has("my_rtd")) {
                        String mydashboardsiourl = jsonObject.getString("my_rtd");
                        if (mydashboardsiourl != null && !(mydashboardsiourl.isEmpty()) && (mydashboardsiourl != " ") && !(mydashboardsiourl.equalsIgnoreCase("null"))) {
                            Urls.DASHBOARD_SIO_ADDRESS = mydashboardsiourl;
                        }
                    }
                    if(jsonObject.has("next_action")) {
                        nextAction = jsonObject.getString("next_action");
                    }
                    if(jsonObject.has("reason")) {
                        signoutReason = jsonObject.getString("reason");
                    }
                    /*if(jsonObject.has("my_profile")){
                        myProfile = jsonObject.getString("my_profile");
                        ApplicationSettings.putPref(AppConstants.MY_PROFILE_PROJECT_TYPE, myProfile);
                    }else{
                        ApplicationSettings.putPref(AppConstants.MY_PROFILE_PROJECT_TYPE, "");
                    }*/
                    arr[0] = voipNumber;
                    arr[1] = nextAction;
                    arr[2] = signoutReason;
                    return arr;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return arr;
        }
    }

    public static class ServerGetOffline extends WaitingAPIAdapter<String, String> {
        String offlineUrl;


        public ServerGetOffline(String input_data, long requestCode, API_Response_Listener<String> listener) {
            super(input_data, requestCode, null, null, listener);
            this.offlineUrl = input_data;
        }

        @Override
        public String getURL() {
            return offlineUrl;
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return "success";
        }
    }

    public static class ServerPostCallLog extends WaitingAPIAdapter<KnowlarityCallLogs, String> {
        String offlineUrl;


        public ServerPostCallLog(KnowlarityCallLogs input_data, long requestCode, API_Response_Listener<String> listener) {
            super(input_data, requestCode, null, null, listener);
            // this.offlineUrl = input_data;
        }

        @Override
        public String getURL() {
            return AppConstants.POSTCALLOGURL;
        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }

        @Override
        public JSONObject convertDataToJSON(KnowlarityCallLogs data) {
            return JSONParser.getKnowlarityCallLogJson(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return "success";
        }
    }


    public static class GetUearnDashboardInfo extends WaitingAPIAdapter<UserActivities, String> {

        UserActivities userActivities;
        String userId;

        public GetUearnDashboardInfo(UserActivities input_data, long requestCode, Activity activity, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, msg, listener);
            userActivities = input_data;
            userId = input_data.user_id;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getUearnDashboard(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            System.out.print(response);
            return response;
        }
    }







    public static class GetUearnDashboard extends WaitingAPIAdapter<UserActivities, String> {

        UserActivities userActivities;
        String userId;

        public GetUearnDashboard(UserActivities input_data, long requestCode, Activity activity, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, msg, listener);
            userActivities = input_data;
            userId = input_data.user_id;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getUearnDashboard(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            System.out.print(response);
            return response;
        }
    }

    public static class GetUearnwdMonthPassbook extends WaitingAPIAdapter<UserActivities, String> {

        UserActivities userActivities;
        String userId;

        public GetUearnwdMonthPassbook(UserActivities input_data, long requestCode, Activity activity, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, msg, listener);
            userActivities = input_data;
            userId = input_data.user_id;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getUearnedMonthPassbookUrl(requiredStartTime);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            System.out.print(response);
            return response;
        }
    }

    public static class GetUearnPassbookLifetime extends WaitingAPIAdapter<UserActivities, String> {

        UserActivities userActivities;
        String userId;

        public GetUearnPassbookLifetime(UserActivities input_data, long requestCode, Activity activity, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, msg, listener);
            userActivities = input_data;
            userId = input_data.user_id;
        }

        @Override
        public String getURL() {
            String userId = userActivities.user_id;
            return Urls.getUearnPassbookLifetimeUrl(userId);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            System.out.print(response);
            return response;
        }
    }

    public static class GetContactedList extends WaitingAPIAdapter<UserActivities, String> {

        UserActivities userActivities;
        String userId;

        public GetContactedList(UserActivities input_data, long requestCode, Activity activity, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, msg, listener);
            userActivities = input_data;
            userId = input_data.user_id;
        }

        public GetContactedList(UserActivities input_data, int requestCode, Runnable runnable, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, (Activity) runnable, msg, listener);
            userActivities = input_data;
            userId = input_data.user_id;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getContactedListUrl(user_id, requiredStartTime, requiredEndTime);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            System.out.print(response);
            return response;
        }
    }




    public static class GetUearnwdDayWisePassbook extends WaitingAPIAdapter<UserActivities, String> {

        UserActivities userActivities;
        String userId;

        public GetUearnwdDayWisePassbook(UserActivities input_data, long requestCode, Activity activity, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, msg, listener);
            userActivities = input_data;
            userId = input_data.user_id;
        }

        @Override
        public String getURL() {
            String user_id = userActivities.user_id;
            String company =  ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
            String requiredStartTime = userActivities.startTime;
            String requiredEndTime = userActivities.endTime;
            return Urls.getUearnDayWisePassbookUrl(user_id, requiredStartTime, requiredEndTime, company);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            System.out.print(response);
            return response;
        }
    }





    public static class GetRemoteDialerStart extends WaitingAPIAdapter<String, String> {

        String bodymessage;

        public GetRemoteDialerStart(String input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url =  Urls.getRemoteDialerStart(userId,bodymessage,SmarterSMBApplication.pingStatusResponse,SmarterSMBApplication.currentNetworkType);
            return url;
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }


        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getRemoteDialerResponse(response);
        }
    }

    public static class GetRemoteDialerStop extends WaitingAPIAdapter<String, String> {

        String bodymessage;

        public GetRemoteDialerStop(String input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getRemoteDialerStop(userId,bodymessage,SmarterSMBApplication.totalLoggedInTime,SmarterSMBApplication.totalActiveTime);

            Log.d("RemoteDialer", "URL "+url);

            return url;

        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }


        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getRemoteDialerResponse(response);
        }
    }

    public static class GetRequestForData extends WaitingAPIAdapter<String, String> {

        String bodymessage;

        public GetRequestForData(String input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getRequestForDataUrl(userId,bodymessage);
            Log.d("RequestForData", "URL "+url);
            return url;

        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }


        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getRequestForDataResponse(response);
        }
    }

    public static class GetConnectedCustomerInfo extends WaitingAPIAdapter<String, String> {

        public GetConnectedCustomerInfo(String input_data, long requestCode, Activity activity, String msg, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, msg, listener);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String transactionId = NotificationData.transactionId;
            Log.d("NotificationTrans", "UserId" + userId + "TransactionId"+ transactionId);
            String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
            if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                if(transactionId == null || transactionId.isEmpty()){
                    transactionId = "XXXXYYYY";
                }
            }
            return Urls.getIvrCustomerMappingUrl(userId, transactionId);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            System.out.print(response);
            return response;
        }
    }
    //Added by praveen to get agent time slots
    public static class GetAgentSlotsApi extends WaitingAPIAdapter<String, SlotData> {
        public GetAgentSlotsApi(String inputData, long requestCode, Activity activity, String message, API_Response_Listener<SlotData> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Log.e("Apiprovider","userId::: "+userId);
            return Urls.getAgentSlotUrl(userId);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public SlotData convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            return JSONParser.getAgentSlotsResponse(response);
        }
    }

    public static class GetRequestForBookingSlot extends WaitingAPIAdapter<SlotCancel, String> {
        SlotCancel slotRows;
        String bodymessage;

        public GetRequestForBookingSlot(SlotCancel input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            slotRows = input_data;
            //bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getRequestForBookingSlotUrl(userId);
            Log.d("RequestForData", "URL "+url);
            return url;

        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }


        /*@Override
        public PostSlotResponse convertJSONToData(String response) {
            Gson g = new Gson();
            PostSlotResponse response1 = g.fromJson(response, PostSlotResponse.class);
            Log.e("RequestForData", "response "+response1.getSuccess());
            return response1;
        }*/

        @Override
        public JSONObject convertDataToJSON(SlotCancel data) {
            return JSONParser.getPostSlotsResponse(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class GetNotificationMessagesApi extends WaitingAPIAdapter<String, NotificationMessageResponse> {
        public GetNotificationMessagesApi(String inputData, long requestCode, Activity activity, String message, API_Response_Listener<NotificationMessageResponse> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Log.e("Apiprovider","userId::: "+userId);
            return Urls.getNotificationMessageUrl(userId);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public NotificationMessageResponse convertJSONToData(String response) {
            //Log.d("Get personal and junk", response);
            return JSONParser.getNotificationMessagesResponse(response);
        }
    }

    public static class GetRequestForReadMessage extends WaitingAPIAdapter<PostNotificationMessage, String> {
        PostNotificationMessage postNotificationMessage;
        String bodymessage;

        public GetRequestForReadMessage(PostNotificationMessage input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            postNotificationMessage = input_data;
            //bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getRequestForPostMessageUrl(userId);
            Log.d("RequestForData", "URL "+url);
            return url;

        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }


        /*@Override
        public PostSlotResponse convertJSONToData(String response) {
            Gson g = new Gson();
            PostSlotResponse response1 = g.fromJson(response, PostSlotResponse.class);
            Log.e("RequestForData", "response "+response1.getSuccess());
            return response1;
        }*/

        @Override
        public JSONObject convertDataToJSON(PostNotificationMessage data) {
            return JSONParser.getPostReadResponse(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class GetRequestForCancelSlot extends WaitingAPIAdapter<SlotCancel, String> {
        SlotCancel slotRows;
        String bodymessage;

        public GetRequestForCancelSlot(SlotCancel input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            slotRows = input_data;
            //bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getRequestForCancelSlotUrl(userId);
            Log.d("RequestForData", "URL "+url);
            return url;

        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }



        @Override
        public JSONObject convertDataToJSON(SlotCancel data) {
            return JSONParser.getPostCancelSlotsResponse(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class GetRequestForChannelInfo extends WaitingAPIAdapter<PMRequestChannel, String> {
        PMRequestChannel pmRequestChannel;
        String bodymessage;

        public GetRequestForChannelInfo(PMRequestChannel input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            pmRequestChannel = input_data;
            //bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getRequestForChannelInfo(userId);
            Log.d("RequestForData", "URL "+url);
            return url;

        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }


        /*@Override
        public PostSlotResponse convertJSONToData(String response) {
            Gson g = new Gson();
            PostSlotResponse response1 = g.fromJson(response, PostSlotResponse.class);
            Log.e("RequestForData", "response "+response1.getSuccess());
            return response1;
        }*/

        @Override
        public JSONObject convertDataToJSON(PMRequestChannel data) {
            return JSONParser.getPostChannelInfoResponse(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getObjectChannelFromJson(response);
        }
    }

    public static class GetRequestForTechSupport extends WaitingAPIAdapter<TechSupportRequest, String> {
        TechSupportRequest techSupportRequest;
        String bodymessage;

        public GetRequestForTechSupport(TechSupportRequest input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            techSupportRequest = input_data;
            //bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getRequestForTechSupport(userId);
            Log.d("RequestForData", "URL "+url);
            return url;

        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }


        /*@Override
        public PostSlotResponse convertJSONToData(String response) {
            Gson g = new Gson();
            PostSlotResponse response1 = g.fromJson(response, PostSlotResponse.class);
            Log.e("RequestForData", "response "+response1.getSuccess());
            return response1;
        }*/

        @Override
        public JSONObject convertDataToJSON(TechSupportRequest data) {
            return JSONParser.getPostTechSupportResponse(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class GetTrainingDataApi extends WaitingAPIAdapter<String, String> {
        public GetTrainingDataApi(String inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Log.e("Apiprovider","userId::: "+userId);
            return Urls.getTrainingDashboardUrl(userId);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getObjectChannelFromJson(response);
        }
    }
    public static class GetHelpDeskQuestion extends WaitingAPIAdapter<String, String> {
        String strId;
        public GetHelpDeskQuestion(String inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
            this.strId=inputData;
        }

        @Override
        public String getURL() {
            //String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Log.e("Apiprovider","userId::: "+strId);
            return Urls.getHelpDeskQuestion(strId);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getObjectChannelFromJson(response);
        }
    }
    public static class SubmitHelpDeskAnswer extends WaitingAPIAdapter<HelpDeskInfoData, String> {
        HelpDeskInfoData strId;
        public SubmitHelpDeskAnswer(HelpDeskInfoData inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
            this.strId=inputData;
        }

        @Override
        public String getURL() {
            //String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Log.e("Apiprovider","userId::: "+strId.getQuestionID());
            return Urls.submitHelpDeskQuestion(strId.getStrAnswer().toUpperCase(), strId.getQuestionID());
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getObjectChannelFromJson(response);
        }
    }
    public static class GetHelpDeskList extends WaitingAPIAdapter<String, String> {

        public GetHelpDeskList(String inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);

        }

        @Override
        public String getURL() {

            return Urls.getHelpDeskList();
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getObjectChannelFromJson(response);
        }
    }
    public static class GetRequestForTrainerReview extends WaitingAPIAdapter<TrainerReview, String> {
        String bodymessage;

        public GetRequestForTrainerReview(TrainerReview input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            //bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getRequestForTrainerReviewUrl(userId);
            Log.e("RequestForData", "URL "+url);
            return url;

        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }


        /*@Override
        public PostSlotResponse convertJSONToData(String response) {
            Gson g = new Gson();
            PostSlotResponse response1 = g.fromJson(response, PostSlotResponse.class);
            Log.e("RequestForData", "response "+response1.getSuccess());
            return response1;
        }*/

        @Override
        public JSONObject convertDataToJSON(TrainerReview data) {
            return JSONParser.getPostTrainerReview(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

    public static class GetTrainerReviewApi extends WaitingAPIAdapter<TrainerReview, String> {
        String date;
        String day;
        TrainerReview reviewDetails;
        public GetTrainerReviewApi(TrainerReview inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
            reviewDetails = inputData;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Log.e("Apiprovider","String::: "+date);
            return Urls.getTrainerReviewUrl(userId,reviewDetails.getBatch_code(), reviewDetails.getDay());
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            Log.e("Apiprovider","response :: "+response);
            return JSONParser.getObjectChannelFromJson(response);
        }
    }

    public static class GetTrainingMessageApi extends WaitingAPIAdapter<String, String> {
        public GetTrainingMessageApi(String inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            return Urls.getTrainingMessageUrl(userId);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getObjectChannelFromJson(response);
        }
    }

    public static class SetReadTrainingMessageApi extends WaitingAPIAdapter<MessageReadRequest, String> {
        public SetReadTrainingMessageApi(MessageReadRequest inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            return Urls.getReadTrainingMessageUrl(userId);
        }

        @Override
        public String getHttpMethod() {
            return "PUT";
        }

        @Override
        public JSONObject convertDataToJSON(MessageReadRequest data) {
            return JSONParser.getPostTrainingReadMessage(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getObjectChannelFromJson(response);
        }
    }

    public static class GetUearnTotalAgentsAndEarnings extends WaitingAPIAdapter<String, String> {

        public GetUearnTotalAgentsAndEarnings(String input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getUearnTotalAgentsAndEarningsUrl(userId);
            return url;
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getUearnTotalAgentsAndEarningsResponse(response);
        }
    }

    public static class GetServiceUserAgreement extends WaitingAPIAdapter<String, String> {

        public GetServiceUserAgreement(String input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            String url = Urls.getServiceUserAgreementUrl();
            return url;
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return response;
        }
    }

    public static class GetQuestionsApi extends WaitingAPIAdapter<String, String> {
        public GetQuestionsApi(String inputData, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(inputData, requestCode, activity, message, listener);
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            return Urls.getQuestionsUrl(userId);
        }

        @Override
        public String getHttpMethod() {
            return "GET";
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getQuestionsResponse(response);
        }
    }

    public static class PostRequestForNetworkDetails extends WaitingAPIAdapter<NetworkDetails, String> {
        String bodymessage;

        public PostRequestForNetworkDetails(NetworkDetails input_data, long requestCode, Activity activity, String message, API_Response_Listener<String> listener) {
            super(input_data, requestCode, activity, message, listener);
            //bodymessage = input_data;
        }

        @Override
        public String getURL() {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String url = Urls.getRequestForNetworkDetailsUrl(userId);
            Log.e("RequestForData", "URL "+url);
            return url;

        }

        @Override
        public String getHttpMethod() {
            return "POST";
        }


        /*@Override
        public PostSlotResponse convertJSONToData(String response) {
            Gson g = new Gson();
            PostSlotResponse response1 = g.fromJson(response, PostSlotResponse.class);
            Log.e("RequestForData", "response "+response1.getSuccess());
            return response1;
        }*/

        @Override
        public JSONObject convertDataToJSON(NetworkDetails data) {
            return JSONParser.getPostNetworkDetails(data);
        }

        @Override
        public String convertJSONToData(String response) {
            return JSONParser.getSuccessStatusFromJson(response);
        }
    }

}
