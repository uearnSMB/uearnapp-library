package smarter.uearn.money.utils.webservice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ResponseInterface;
import smarter.uearn.money.utils.SmarterSMBApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dilip on 1/19/2017.
 * Class related to user profile for network operations
 */

public class ServiceUserProfile {

    /*
     * Method to get Auth token
     * Uses post operation
     *
     * @Params Activity
     * @Return null
     */
    public static void getAuth(Activity activity) {

        if (CommonOperations.isNetworkConnected(activity, 1)) {
            String urlParameters = "grant_type=" + Constants.AUTH_GRANT_TYPE + "&resource=" + Constants.AUTH_RESOURCE +
                    "&client_id=" + Constants.AUTH_CLIENT_ID + "&username=" + Constants.AUTH_USERNAME
                    + "&password=" + Constants.AUTH_PWD + "&scope" + Constants.AUTH_SCOPE;

            String[] data = {Constants.POST, Constants.AUTH_URL, urlParameters};

            if (activity != null) {
                WebService aws = new WebService(activity, 1);
                aws.execute(data);
            }
        }
    }

    /*
     * Method to login user
     * Uses get operation no auth required
     *
     * @Params Activity
     * @Return null
     */
    public static void userLogin(Activity activity, String username, String password, String loginMethod) {
        if (activity != null) {
            if (CommonOperations.isNetworkConnected(activity, 1)) {
                ContentValues values = new ContentValues();
                values.put("email", username);
                values.put("password", password);
                values.put("ver", "3");
                values.put("login_method", loginMethod);

                String[] data = {Constants.GET, Constants.DOMAIN_URL + Constants.LOGIN_API + CommonOperations.buildURL(values)};
                WebService webCall = new WebService(activity, 2);
                webCall.execute(data);
            }
        }
    }

    /*
     * Method to request new forgot
     * Uses get operation auth required
     *
     * @Params Activity,String
     * @Return null
     */
    public static void userForgotPassword(Activity activity, String username) {
        if (activity != null) {
            if (CommonOperations.isNetworkConnected(activity, 1)) {
                String[] data = {Constants.GET, Constants.DOMAIN_URL + Constants.FORGOT_API + "email=" + username};

                WebService webCall = new WebService(activity, 3);
                webCall.execute(data);
            }
        }
    }

    /*
     * Method to change user password
     * Uses POST operation auth required
     *
     * @Params Activity,String,String,String
     * @Return null
     */
    public static void userChangePassword(Activity activity, String password, String newPassword) {
        if (activity != null) {
            if (CommonOperations.isNetworkConnected(activity, 1)) {
                SharedPreferences prefs = activity.getSharedPreferences("SESSION", Context.MODE_PRIVATE);

                ContentValues values = new ContentValues();
                values.put("user_id", prefs.getString("user_id", ""));
                values.put("password", password);
                values.put("new_password", newPassword);
                values.put("confirm_password", newPassword);

                String[] data = {Constants.POST, Constants.DOMAIN_URL + Constants.CHANGE_PASSWORD_API + CommonOperations.buildURL(values)};
                CommonUtils.checkToken();
                WebService webCall = new WebService(activity, 4);
                webCall.execute(data);
            }
        }
    }

    /*
     * Method to register new user
     * Uses POST operation auth required
     *
     * @Params Activity, String, String, String
     * @Return null
     */
    public static void userRegistration(Activity activity, String username, String fullName, String phone, String password, String loginMethod) {
        if (CommonOperations.isNetworkConnected(activity, 1)) {
            // User registration will be a model with a big ass set of items to be added to the server

            ContentValues values = new ContentValues();
            values.put("name", fullName);
            values.put("email", username);
            values.put("password", password);
            values.put("phone", phone);
            values.put("members", " ");
            values.put("login_method", loginMethod);
            values.put("business_logo_url", " ");
            values.put("call_sms_template", AppConstants.DEFAULT_AFTER_CALL_TEMPLATE);
            values.put("jd_sms_template", AppConstants.DEFAULT_JD_TEMPLATE);
            values.put("business_template", AppConstants.DEFAULT_BUSINESS_TEMPLATE);
            values.put("jc_sms_template", AppConstants.DEFAULT_JOBCARD_TEMPLATE);
            values.put("twitter_link", " ");
            values.put("linkedin_link", " ");
            values.put("facebook_link", " ");
            values.put("jobtitle", " ");
            values.put("youtube_link", " ");
            values.put("lat", " ");
            values.put("long", " ");
            values.put("google_map_url", " ");
            values.put("call_recording_auto", " ");
            values.put("call_recording_status", " ");
            values.put("secondary_users", " ");
            values.put("jd_no", " ");
            values.put("address", " ");
            values.put("website", " ");
            values.put("yt_video_link", " ");
            values.put("customer_testimonial_url", " ");
            values.put("facility_photo_url1", " ");
            values.put("google_map_landmark", " ");
            values.put("facility_photo_url2", " ");
            values.put("facility_photo_url3", " ");
            values.put("facility_photo_url4", " ");
            values.put("primary_contact", " ");
            values.put("send_map_location", false);
            values.put("db_allowbooking", true);

            //Log.d("Auth", " " + CommonOperations.buildURL(values));
            String[] data = {Constants.POST, Constants.DOMAIN_URL + Constants.USER_REGISTRATION, CommonOperations.buildURL(values)};

            if (activity != null) {
                WebService webCall = new WebService(activity, 5);
                webCall.execute(data);
            }
        }
    }

    public static void getUserSettings(Activity activity) {

        if (activity != null) {
            if (CommonOperations.isNetworkConnected(activity, 1)) {

                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                // String[] data = {Constants.GET, Constants.DOMAIN_URL+Constants.SETTINGS_STAGES+"9538"};
                String[] data = {Constants.GET, Constants.DOMAIN_URL + Constants.SETTINGS_STAGES + userId};
                WebService webCall = new WebService(activity, 7);
                webCall.execute(data);

                getUsersTeam(activity);
            }
        }
    }

    /**
     * Method called when notification for sales stages arrive
     */
    public static boolean getUserSettings() {

        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        // String[] data = {Constants.GET, Constants.DOMAIN_URL+Constants.SETTINGS_STAGES+"9538"};
        String[] data = {Constants.GET, Constants.DOMAIN_URL + Constants.SETTINGS_STAGES + userId};
        CommonUtils.checkToken();
        WebService webCall = new WebService(7);
        webCall.execute(data);
        getUsersTeam();
        return true;
    }

    public static void getUsersTeam(Activity activity) {
        if (activity != null) {
            if (CommonOperations.isNetworkConnected(activity, 2)) {
                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                String[] data = {Constants.GET, Constants.DOMAIN_URL + Constants.SETTINGS_TEAM + userId};
                //Log.d("team url", "url : " + data);
                CommonUtils.checkToken();
                WebService webCall = new WebService(activity, 8);
                webCall.execute(data);
            }
        }
    }

    /**
     * Method called when notification to update sales stages arrives
     */
    public static void getUsersTeam() {
        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        String[] data = {Constants.GET, Constants.DOMAIN_URL + Constants.SETTINGS_TEAM + userId};
        //Log.d("team url", "url : " + data);
        CommonUtils.checkToken();
        WebService webCall = new WebService(8);
        webCall.execute(data);
    }

    public static void saveUserSettings(Activity activity) {

        SmartUser smartUser = SmarterSMBApplication.SmartUser;

        JSONObject passing = new JSONObject();
        JSONArray list = new JSONArray();

        try {
            passing.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, ""));

            for (int i = 0; i < 36; i++) {
                JSONObject listItem = new JSONObject();

                listItem.put("stage", i + 1);
                listItem.put("type", "extraoptions");

                String name = "";
                String definition = "";

                switch (i) {
                    case 0:
                        name = "autoacceptleads";
                        if (smartUser.getAutoAccepteLeadsSettings()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 1:
                        name = "callrecording";
                        if (ApplicationSettings.getPref(AppConstants.ADMIN_GROUP_RECORDING, false)) {
                            //if (smartUser.getCallRecordStatus()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }

                        break;
                    case 2:
                        name = "gpstracking";
                        if (smartUser.getGpsLocationSettings()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 3:
                        name = "calltrackingall";
                        if (smartUser.getCallTrackingSettings()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 4:
                        name = "calltrackingleads";
                        if (smartUser.getRecordOnlyLeadNumbers()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }

                        break;
                    case 5:
                        name = "calltrackingteam";
                        if (smartUser.getRecordOnlyTeamMemberNumbers()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 6:
                        name = "calltrackingpersonal";
                        if (smartUser.getDonotRecordPrivateContacts()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 7:
                        name = "followupcalls";
                        if (smartUser.getFollowUpCallsSettings()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 8:
                        name = "teamcalls";
                        if (smartUser.getTeamCallSettings()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 9:
                        name = "smstrackingall";
                        if (smartUser.getSmsTrackingAll()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 10:
                        name = "smstrackingleads";

                        if (smartUser.getSmsTrackingLeads()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }

                        break;
                    case 11:
                        name = "smstrackingteam";

                        if (smartUser.getSmsTrackingTeam()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 12:
                        name = "smstrackingpersonal";

                        if (smartUser.getSmsTrackingPersonal()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 13:
                        name = "autofollowup";
                        if (smartUser.getAutoFollowUpSettings()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 14:
                        name = "salescurrency";
                        definition = ApplicationSettings.getPref(AppConstants.TEAM_CURRENCY, "INR");
                        break;
                    case 15:
                        name = "applicationlogging";
                        if (smartUser.getApplicationLogging()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 16:
                        name = "messagetemplate1";
                        definition = smartUser.getCall_sms_template();
                        break;
                    case 17:
                        name = "messagetemplate2";
                        definition = smartUser.getbusiness_template();
                        break;
                    case 18:
                        name = "messagetemplate3";
                        definition = smartUser.getJc_sms_template();
                        break;
                    case 19:
                        name = "messagetemplate4";
                        definition = smartUser.getJd_sms_template();
                        break;
                    case 20:
                        name = "junkcalls";
                        if (SmartUser.getJunkCallSetting()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;
                    case 21:
                        name = "personalcalls";
                        if (SmartUser.getPersonalCallSettings()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;

                    case 22:
                        name = "rnrcbreminder";
                        definition = ""+SmartUser.getrnrreminderSettings();
                        break;

                    case 23:
                        name = "rnrcbmax";
                        definition = ""+smartUser.getRnrMax();
                        break;

                    case 24:
                        name = "cloudtelephonyout";
                        if(smartUser.getCloudOutgoing()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;

                    case 25:
                        name = "autodialer";
                        if(smartUser.getAutoDailer()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;

                    case 27:
                        name = "transcription";
                        definition = ""+smartUser.getTranscription();
                        //Log.d("Settings transcription", " " + definition);
                        break;
                    case 28:
                        name = "transcripted_lang";
                        definition = ""+smartUser.getTranscriptLang();
                        //Log.d("Settings transcription", "lang " + definition);
                        break;
                    case 29:
                        name = "audio_output_format";
                        definition = ""+smartUser.getTranscriptAudioFormate();
                        //Log.d("Settings transcription", "o/p formmate " + definition);
                        break;
                    case 30:
                        name = "audio_encoding_format";
                        definition = ""+smartUser.getTranscriptAudioEncodeFormate();
                        //Log.d("Settings transcription", "encodeing " + definition);
                        break;
                    case 31:
                        name = "aftercallact";
                        definition = ""+smartUser.getAfterCallScreen();
                        //Log.d("Settings transcription", "afterCall " + definition);
                        break;
                    case 32:
                        name = "helpline";
                        definition = ""+smartUser.getHelpLine();
                        //Log.d("Settings transcription", "helpLine " + definition);
                        break;
                    case 33:
                        name = "unscheduledcall";
                        if(smartUser.getUnscheduledCall()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        //Log.d("Settings transcription", "helpLine " + definition);
                        break;
                    case 34:
                        name = "c2cendpoint";
                        definition = ""+smartUser.getC2cEndPoint();
                        break;
                    case 35:
                        name = "smboffline";
                        if(smartUser.getSmbOffline()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        break;

                }
                listItem.put("name", name);
                listItem.put("definition", definition);
                list.put(listItem);
            }
            passing.put("list", list);
            passing.put("type", "extraoptions");

            //Log.d("json save settings", " " + passing);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (activity != null) {
            if (CommonOperations.isNetworkConnected(activity, 2)) {
                String[] data = {Constants.POST, Constants.DOMAIN_URL + Constants.SETTINGS_STAGES_SAVE};
                WebService webCall = new WebService(activity, 9, passing);
                webCall.execute(data);
            }
        }
    }

    public static void saveUserWorkhoursSettings(Activity activity) {

        SmartUser smartUser = SmarterSMBApplication.SmartUser;

        JSONObject passing = new JSONObject();
        JSONArray list = new JSONArray();

        try {
            passing.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, ""));

            for (int i = 0; i < 12; i++) {
                JSONObject listItem = new JSONObject();

                listItem.put("stage", i + 1);
                listItem.put("type", "workhours");

                String name = "";
                String definition = "";

                switch (i) {
                    case 0:
                        name = "startdow";
                        definition = "SAT";
                        break;
                    case 1:
                        name = "enddow";
                        definition = "SUN";

                        break;
                    case 2:
                        name = "starttime";
                        definition = smartUser.getOpenTime();
                        //Log.d("WorkstartTime",""+ smartUser.getOpenTime());
                        break;
                    case 4:
                        name = "endtime";
                        definition = smartUser.getCloseTime();
                        //Log.d("WorkcloseTime",""+ smartUser.getCloseTime());
                        break;
                    case 5:
                        name = "MON";
                        if(smartUser.isMon()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        //Log.d("WorkMON",""+ smartUser.isMon());
                        break;
                    case 6:
                        name = "TUE";
                        if(smartUser.isTue()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        //Log.d("tue",""+ smartUser.isTue());
                        break;
                    case 7:
                        name = "WED";
                        if(smartUser.isWen()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        //Log.d("wed",""+ smartUser.isWen());
                        break;
                    case 8:
                        name = "THU";
                        if(smartUser.isThu()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        //Log.d("thu",""+ smartUser.isThu());
                        break;
                    case 9:
                        name = "FRI";
                        if(smartUser.isFri()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        //Log.d("fri",""+ smartUser.isFri());
                        break;
                    case 10:
                        name = "SAT";
                        if(smartUser.isSat()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        //Log.d("sat",""+ smartUser.isSat());
                        break;
                    case 11:
                        name = "SUN";
                        if(smartUser.isSun()) {
                            definition = "on";
                        } else {
                            definition = "off";
                        }
                        //Log.d("SUn",""+ smartUser.isSun());
                        break;
                }
                listItem.put("name", name);
                listItem.put("definition", definition);
                list.put(listItem);
            }
            passing.put("list", list);
            passing.put("type", "workhours");

            //Log.d("json save settings", " " + passing);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (activity != null) {
            if (CommonOperations.isNetworkConnected(activity, 2)) {
                String[] data = {Constants.POST, Constants.DOMAIN_URL + Constants.SETTINGS_STAGES_SAVE};
                WebService webCall = new WebService(activity, 9, passing);
                webCall.execute(data);
            }
        }
    }

    public static void saveUserAutoDailerSettings(Activity activity) {

        JSONObject passing = new JSONObject();
        JSONArray list = new JSONArray();

        try {
            passing.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, ""));

            for (int i = 0; i < 2; i++) {
                JSONObject listItem = new JSONObject();

                listItem.put("stage", i + 1);
                listItem.put("type", "autodialer");

                String name = "";
                String definition = "";

                switch (i) {
                    case 0:
                        name = "waitbefore";
                        definition = ""+ApplicationSettings.getPref(AppConstants.TIMER_BEFORE_CALL,"");
                        break;
                    case 1:
                        name = "waitafter";
                        definition = ""+ApplicationSettings.getPref(AppConstants.TIMER_AFTER_CALL,"");
                        break;
                }
                listItem.put("name", name);
                listItem.put("definition", definition);
                list.put(listItem);
            }
            passing.put("list", list);
            passing.put("type", "autodialer");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (activity != null) {
            if (CommonOperations.isNetworkConnected(activity, 2)) {
                String[] data = {Constants.POST, Constants.DOMAIN_URL + Constants.SETTINGS_STAGES_SAVE};
                WebService webCall = new WebService(activity, 9, passing);
                webCall.execute(data);
            }
        }
    }
}
