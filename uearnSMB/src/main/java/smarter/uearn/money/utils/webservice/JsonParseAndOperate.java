package smarter.uearn.money.utils.webservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import smarter.uearn.money.activities.SmarterAuthActivity;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.SmarterSMBApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created on 1/16/2017.
 *
 * @author Dilip
 * @version 1.0
 *          Parsing data received the network
 */

public class JsonParseAndOperate {

    public static void parseAuthToken(Activity activity, JSONObject jsonObject) {
        try {
            //Log.d("auth", jsonObject.getString("access_token"));
            SharedPreferences prefs = activity.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("auth", jsonObject.getString("access_token"));
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseUserLogin(Activity activity, JSONObject jsonObject) {
        try {
            SharedPreferences prefs = activity.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_id", jsonObject.getString("id"));
            editor.putString("name", jsonObject.getString("name"));
            editor.putString("company", jsonObject.getString("company"));
            editor.putString("email", jsonObject.getString("email"));
            editor.putString("phone", jsonObject.getString("phone"));
            editor.putString("logo", jsonObject.getString("business_logo_url"));
            editor.putString("call_sms_template", jsonObject.getString("call_sms_template"));
            editor.putString("jd_sms_template", jsonObject.getString("jd_sms_template"));
            editor.putString("jc_sms_template", jsonObject.getString("jc_sms_template"));
            editor.putString("address", jsonObject.getString("address"));
            editor.putString("address1", jsonObject.getString("address1"));
            editor.putString("address2", jsonObject.getString("address2"));
            editor.putString("countrycode", jsonObject.getString("countrycode"));
            editor.putString("primary_contact", jsonObject.getString("primary_contact"));
            editor.apply();

            Intent dashboardIntent = new Intent(activity, SmarterAuthActivity.class);
            activity.startActivity(dashboardIntent);
        } catch (JSONException e) {
            e.printStackTrace();
            //Log.d("Auth error", "error " + e);
            Toast.makeText(activity, "Oops something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public static void parseUserForgotPassword(Activity activity, JSONObject jsonObject) {

        try {
            if (jsonObject.has("success")) {
                if (jsonObject.getString("success").equalsIgnoreCase("success")) {
                    new CustomSingleButtonDialog().buildSingleButtonDialog("Forgot password", "Password reset please check mail", activity, false);
                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new CustomSingleButtonDialog().buildSingleButtonDialog("Woops", "Please check username entered", activity, false);
    }

    public static void parseUserChangePassword(Activity activity, JSONObject jsonObject) {

    }

    public static void parseVoipNumber(String result) {

    }
    public static void parseSettingsData(Activity activity, JSONObject jsonObject) {

        SmartUser smartUser = SmarterSMBApplication.SmartUser;

        /* Name of the results are as follows
         *
         * autoacceptleads, callrecording, gpstracking, calltrackingall, followupcalls
         * teamcalls, smstracking, autofollowup, smstracking, salescurrency, applicationlogging
         * messagetemplate1, messagetemplate2, messagetemplate3, messagetemplate4
         *
         * Except for the messagetemplate names, all the items have on and off definitions
         */
        try {

            if (jsonObject.has("success")) {
                JSONObject jObject = jsonObject.getJSONObject("success");
                if (jObject.has("extraoptions")) {
                    JSONArray settingsOptions = jObject.getJSONArray("extraoptions");
                    for (int i = 0; i < settingsOptions.length(); i++) {
                        JSONObject sObject = settingsOptions.getJSONObject(i);

                        switch (sObject.getString("name")) {
                            case "gpstracking":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setGpsLocationSettings(true);
                                } else {
                                    smartUser.setGpsLocationSettings(false);
                                }
                                //Log.d("gpstracking",sObject.getString("definition"));
                                break;
                            case "autoacceptleads":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAutoAccepteLeadsSettings(true);
                                } else {
                                    smartUser.setAutoAccepteLeadsSettings(false);
                                }
                                //Log.d("autoacceptleads",sObject.getString("definition"));
                                break;
                            case "callrecording":
                                /*String role = CommonUtils.getRole(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""), activity);*/
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setCallRecordStatus(true);
                                    ApplicationSettings.putPref(AppConstants.ADMIN_GROUP_RECORDING, true);
                                    /*if (ApplicationSettings.getPref(AppConstants.SUPERVISOR_EMAIL, "").equalsIgnoreCase(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""))) {*/

                                    if(ApplicationSettings.getPref(AppConstants.TEAM_ROLE,"").equalsIgnoreCase("superadmin")) {
                                        smartUser.setCallRecordStatus(ApplicationSettings.getPref(AppConstants.ADMIN_PERSONAL_RECORDING, false));
                                    }
                                } else {
                                    smartUser.setCallRecordStatus(false);
                                    ApplicationSettings.putPref(AppConstants.ADMIN_GROUP_RECORDING, false);
                                    /*if (ApplicationSettings.getPref(AppConstants.SUPERVISOR_EMAIL, "").equalsIgnoreCase(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""))) {*/
                                    if(ApplicationSettings.getPref(AppConstants.TEAM_ROLE,"").equalsIgnoreCase("superadmin")) {
                                        smartUser.setCallRecordStatus(ApplicationSettings.getPref(AppConstants.ADMIN_PERSONAL_RECORDING, false));
                                    }
                                }
                                //Log.d("callrecording",sObject.getString("definition"));
                                break;
                            case "calltrackingall":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setRecordOnkyUnknownNumbers(true);
                                    smartUser.setCallTrackingSettings(true);
                                } else {
                                    smartUser.setCallTrackingSettings(false);
                                    smartUser.setRecordOnkyUnknownNumbers(false);
                                }
                                //Log.d("calltrackingall",sObject.getString("definition"));
                                break;
                            case "calltrackingleads":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setRecordOnlyLeadNumbers(true);
                                } else {
                                    smartUser.setRecordOnlyLeadNumbers(false);
                                }
                                //Log.d("calltrackingleads",sObject.getString("definition"));
                                break;
                            case "calltrackingteam":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setRecordOnlyTeamMemberNumbers(true);
                                } else {
                                    smartUser.setRecordOnlyTeamMemberNumbers(false);
                                }
                                //Log.d("calltrackingteam",sObject.getString("definition"));
                                break;
                            case "calltrackingpersonal":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setDonotRecordPrivateContacts(true);
                                } else {
                                    smartUser.setDonotRecordPrivateContacts(false);
                                }
                                //Log.d("calltrackingpersonal",sObject.getString("definition"));
                                break;
                            case "followupcalls":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setFollowUpCallsSettings(true);
                                } else {
                                    smartUser.setFollowUpCallsSettings(false);
                                }
                                //Log.d("followupcalls",sObject.getString("definition"));
                                break;
                            case "teamcalls":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setTeamCallSettings(true);
                                } else {
                                    smartUser.setTeamCallSettings(false);
                                }
                                //Log.d("teamcalls",sObject.getString("definition"));
                                break;
                            case "smstracking":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setSmsTrackingSettings(true);
                                } else {
                                    smartUser.setSmsTrackingSettings(false);
                                }
                                //Log.d("smstracking",sObject.getString("definition"));
                                break;
                            case "smstrackingall":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setCaptureIncomingSMS(true);
                                    smartUser.setSmsTrackingAll(true);
                                } else {
                                    smartUser.setCaptureIncomingSMS(false);
                                    smartUser.setSmsTrackingAll(false);
                                }
                                //Log.d("smstrackingall",sObject.getString("definition"));
                                break;
                            case "smstrackingleads":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setSmsTrackingLeads(true);
                                } else {
                                    smartUser.setSmsTrackingLeads(false);
                                }
                                //Log.d("smstrackingleads",sObject.getString("definition"));
                                break;
                            case "smstrackingteam":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setSmsTrackingTeam(true);
                                } else {
                                    smartUser.setSmsTrackingTeam(false);
                                }
                                //Log.d("smstrackingteam",sObject.getString("definition"));
                                break;
                            case "smstrackingpersonal":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setSmsTrackingPersonal(true);
                                } else {
                                    smartUser.setSmsTrackingPersonal(false);
                                }
                                //Log.d("smstrackingpersonal",sObject.getString("definition"));
                                break;
                            case "autofollowup":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAutoFollowUpSettings(true);
                                } else {
                                    smartUser.setAutoFollowUpSettings(false);
                                }
                                //Log.d("autofollowup",sObject.getString("definition"));
                                break;
                            case "salescurrency":
                                ApplicationSettings.putPref("salescurrency", sObject.getString("definition"));
                                //Log.d("salescurrency",sObject.getString("definition"));
                                break;
                            case "applicationlogging":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setApplicationLogging(true);
                                } else {
                                    smartUser.setApplicationLogging(false);
                                }
                                //Log.d("applicationlogging",sObject.getString("definition"));
                                break;
                            case "messagetemplate1":
                                smartUser.setCall_sms_template(sObject.getString("definition"), true);
                                //Log.d("messagetemplate1",sObject.getString("definition"));
                                break;
                            case "messagetemplate2":
                                smartUser.setbusiness_template(sObject.getString("definition"), true);
                                //Log.d("messagetemplate2",sObject.getString("definition"));
                                break;
                            case "messagetemplate3":
                                smartUser.setJc_sms_template(sObject.getString("definition"), true);
                                //Log.d("callrecording",sObject.getString("definition"));
                                break;
                            case "messagetemplate4":
                                smartUser.setJd_sms_template(sObject.getString("definition"), true);
                                //Log.d("messagetemplate4",sObject.getString("definition"));
                                break;
                            case "junkcalls":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setJunkCallSetting(true);
                                } else {
                                    smartUser.setJunkCallSetting(false);
                                }
                                //Log.d("junkcalls",sObject.getString("definition"));
                                break;
                            case "personalcalls":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setPersonalcallSetting(true);
                                } else {
                                    smartUser.setPersonalcallSetting(false);
                                }
                                //Log.d("personalcalls",sObject.getString("definition"));
                                break;

                            case "rnrcbreminder":
                                if (sObject.has("definition")) {
                                    smartUser.setrnrReminder(sObject.getInt("definition"));
                                }
                                //Log.d("rnrcbreminder",sObject.getString("definition"));
                                break;

                            case "rnrcbmax":
                                if (sObject.has("definition")) {
                                    smartUser.setRnrmax(sObject.getInt("definition"));
                                }
                                //Log.d("rnrcbmax",sObject.getString("definition"));
                                break;
                            case "cloudtelephonyout":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setCloudOutgoing(true);
                                } else {
                                    smartUser.setCloudOutgoing(false);
                                }
                                //Log.d("cloudtelephonyout",sObject.getString("definition"));
                                break;
                            case "autodialer":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAutoDailer(true);
                                } else {
                                    smartUser.setAutoDailer(false);
                                }
                                //Log.d("autodialer",sObject.getString("definition"));
                                break;

                            case "transcription":
                                if (sObject.getString("definition").equalsIgnoreCase("off")  ||  (sObject.getString("definition").equalsIgnoreCase("server_generated"))) {
                                    smartUser.setTranscription(false);
                                } else {
                                    smartUser.setTranscription(true);
                                }
                                //Log.d("transcription",sObject.getString("definition"));
                                break;

                            case "transcripted_lang":
                                if (sObject.has("definition")) {
                                    smartUser.setTranscriptLang(sObject.getString("definition"));
                                }
                                //Log.d("transcript_lang",sObject.getString("definition"));
                                break;

                            case "audio_output_format":
                                if (sObject.has("definition")) {
                                    smartUser.setTranscriptAudioFormate(sObject.getString("definition"));
                                }
                                //Log.d("autoOutputformat",sObject.getString("definition"));
                                break;

                            case "sipcall":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setSipCalls(true);
                                } else {
                                    smartUser.setSipCalls(false);
                                }
                                //Log.d("sipcall",sObject.getString("definition"));
                                break;

                            case "audio_encoding_format":
                                if (sObject.has("definition")) {
                                    smartUser.setTranscriptAudioEncodeFormate(sObject.getString("definition"));
                                }
                                //Log.d("autoEncoderformat",sObject.getString("definition"));
                                break;

                            case "rnrduration":
                                if(sObject.has("definition")) {
                                    smartUser.setRnrDuration(sObject.getString("definition"));
                                }
                                //Log.d("RNRDURATION", sObject.getString("definition"));
                                break;

                            case "aftercallact":
                                if(sObject.has("definition")) {
                                    smartUser.setAfterCallActivityScreen(sObject.getString("definition"));
                                }
                                //Log.d("AftercallActivity", sObject.getString("definition"));
                                break;
                            case "mobiledataonsim":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setMobiledataonsim(true);
                                } else {
                                    smartUser.setMobiledataonsim(false);
                                }
                                //Log.d("AftercallActivity","mobiledataonsim"+ sObject.getString("definition"));
                                break;

                            case "helpline":
                                if(sObject.has("definition")) {
                                    smartUser.setHelpline(sObject.getString("definition"));
                                }
                                //Log.d("HelpLine", sObject.getString("definition"));
                                break;
                            case "c2c_timeout":
                                if(sObject.has("definition")) {
                                    smartUser.setC2cTimeOut(sObject.getString("definition"));
                                }
                                //Log.d("HelpLine", sObject.getString("definition"));
                                break;
                            case "unscheduledcall":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setUnscheduledCall(true);
                                } else {
                                    smartUser.setUnscheduledCall(false);
                                }
                                //Log.d("unscheduledcall", sObject.getString("unscheduledcall"));
                                break;
                            case "c2cendpoint":
                                if(sObject.has("definition")) {
                                    smartUser.setC2cEndPoint(sObject.getString("definition"));
                                }
                                break;

                            case "callend_duration":
                                if(sObject.has("definition")) {
                                    smartUser.setCallEndDuration(sObject.getString("definition"));
                                }
                                break;

                            case "smboffline":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setSmbOffline(true);
                                } else {
                                    smartUser.setSmbOffline(false);
                                }
                                break;
                            case "usecampaign":
                                if(sObject.has("definition")) {
                                    smartUser.setUseCampaign(sObject.getString("definition"));
                                }
                                Log.d("Usercampaign", sObject.getString("definition"));
                                break;
                            case "showoncallcampaign":
                                if (sObject.has("definition")) {
                                    smartUser.setShownCallCampaign(sObject.getString("definition"));
                                }
                                Log.d("showoncallcampaign", sObject.getString("definition"));
                                break;

                            case "oncallact":
                                if (sObject.has("definition")) {
                                    smartUser.setOnCallAct(sObject.getString("definition"));
                                }
                                Log.d("oncallact", sObject.getString("definition"));
                                break;

                            case "questionnaire":
                                if(sObject.has("definition")) {
                                    smartUser.setQuestionAnswer(sObject.getString("definition"));
                                }
                                break;

                            case "delayacpopup":
                                if(sObject.has("definition")) {
                                    smartUser.setDelayACPopup(sObject.getString("definition"));
                                }
                                break;

                            case "donelistquery":
                                if(sObject.has("definition")) {
                                    smartUser.setDoneListQuery(sObject.getString("definition"));
                                }
                                break;

                            case "donecardquery":
                                if(sObject.has("definition")) {
                                    smartUser.setDoneCardQuery(sObject.getString("definition"));
                                }
                                break;

                            case "autosync":
                                if(sObject.has("definition")) {
                                    smartUser.setAutoSync(sObject.getString("definition"));
                                }
                                break;

                            case "truncateremindertable":
                                if(sObject.has("definition")) {
                                    smartUser.setTruncateReminderTable(sObject.getString("definition"));
                                }
                                break;

                            case "questionsact":
                                if(sObject.has("definition")) {
                                    smartUser.setQuestionsAct(sObject.getString("definition"));
                                }
                                break;

                            case "noofcustomers":
                                if(sObject.has("definition")) {
                                    smartUser.setNumberOfCustomersToCall(sObject.getString("definition"));
                                }
                                break;

                            case "ispromo":
                                if(sObject.has("definition")) {
                                    smartUser.setIsPromo(sObject.getString("definition"));
                                }
                                break;

                            case "parallel_group_size":
                                if(sObject.has("definition")) {
                                    smartUser.setParallelGroupSize(sObject.getString("definition"));
                                }
                                break;

                            case "allowallincoming":
                                if(sObject.has("definition")) {
                                    smartUser.setAllowAllIncoming(sObject.getString("definition"));
                                }
                                break;

                            case "allowalloutgoing":
                                if(sObject.has("definition")) {
                                    smartUser.setAllowAllOutgoing(sObject.getString("definition"));
                                }
                                break;
                            case "remotedialling":
                                if(sObject.has("definition")) {
                                    smartUser.setRemoteDialling(sObject.getString("definition"));
                                }
                                break;
                            case "remoteautodialling":
                                if(sObject.has("definition")) {
                                    smartUser.setRemoteDialling(sObject.getString("definition"));
                                }
                                break;
                            case "siopushurl":
                                if(sObject.has("definition")) {
                                    smartUser.setSioPushUrl(sObject.getString("definition"));
                                }
                                break;
                            case "radoptimizedpost":
                                if(sObject.has("definition")) {
                                    smartUser.setOptimizedPost(sObject.getString("definition"));
                                }
                                break;
                            case "remotediallingstartalert":
                                if(sObject.has("definition")) {
                                    smartUser.setRemoteDiallingStartAlert(sObject.getString("definition"));
                                }
                                break;

                            case "remotediallingstopalert":
                                if(sObject.has("definition")) {
                                    smartUser.setRemoteDiallingStopAlert(sObject.getString("definition"));
                                }
                                break;

                            case "defaulthomepage":
                                if(sObject.has("definition")) {
                                    smartUser.setDefaultHomePage(sObject.getString("definition"));
                                }
                                break;

                            case "defaulthomepagewv":
                                if(sObject.has("definition")) {
                                    smartUser.setDefaultHomePageWebView(sObject.getString("definition"));
                                }
                                break;

                            case "userpositivepreferences":
                                if(sObject.has("definition")) {
                                    smartUser.setUserPositivePreferences(sObject.getString("definition"));
                                }
                                break;

                            case "usernegativepreferences":
                                if(sObject.has("definition")) {
                                    smartUser.setUserNegativePreferences(sObject.getString("definition"));
                                }
                                break;

                            case "newdatastring":
                                if(sObject.has("definition")) {
                                    smartUser.setNewDataString(sObject.getString("definition"));
                                }
                                break;

                            case "followupstring":
                                if(sObject.has("definition")) {
                                    smartUser.setFollowUpString(sObject.getString("definition"));
                                }
                                break;

                            case "siodisabled":
                                if(sObject.has("definition")) {
                                    smartUser.setSIODisabled(sObject.getString("definition"));
                                }
                                break;

                            case "isreuploadenabled":
                                if(sObject.has("definition")) {
                                    smartUser.setIsReuploadEnabled(sObject.getString("definition"));
                                }
                                break;

                            case "fieldstoshow":
                                if(sObject.has("definition")) {
                                    smartUser.setFieldsToShow(sObject.getString("definition"));
                                }
                                break;

                            case "showaftercallpopup":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setShowAfterCallPopup(true);
                                } else {
                                    smartUser.setShowAfterCallPopup(false);
                                }
                                break;

                            case "appendleadsource":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAppendLeadSource(true);
                                } else {
                                    smartUser.setAppendLeadSource(false);
                                }
                                break;

                            case "launchapp":
                                if(sObject.has("definition")) {
                                    smartUser.setLaunchApp(sObject.getString("definition"));
                                } else {
                                    smartUser.setLaunchApp("");
                                }
                                break;

                            case "uploadcmail":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setUploadCmail(true);
                                } else {
                                    smartUser.setUploadCmail(false);
                                }
                                break;


                            case "followupedit":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setFollowupEditable(true);
                                } else {
                                    smartUser.setFollowupEditable(false);
                                }
                                break;

                            case "serveraptupdates":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setServerAptUpdates(true);
                                } else {
                                    smartUser.setServerAptUpdates(false);
                                }
                                break;

                            case "callback_followup":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setCallBackFollowup(true);
                                } else {
                                    smartUser.setCallBackFollowup(false);
                                }
                                //Log.d("autodialer",sObject.getString("definition"));
                                break;

                            case "show_customer_number":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setShowCustomerNumber(true);
                                } else {
                                    smartUser.setShowCustomerNumber(false);
                                }
                                //Log.d("autodialer",sObject.getString("definition"));
                                break;

                            case "truepredictive":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setTruePredictive(true);
                                } else {
                                    smartUser.setTruePredictive(false);
                                }
                                break;

                            case "radc2cendpoint":
                                if(sObject.has("definition")) {
                                    smartUser.setRADC2CEndPoint(sObject.getString("definition"));
                                }
                                break;
                            case "showalert":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setShowAlert(true);
                                } else {
                                    smartUser.setShowAlert(false);
                                }
                                break;

                            case "wrapuptime":
                                if(sObject.has("definition")) {
                                    smartUser.setWrapupTime(sObject.getString("definition"));
                                } else {
                                    smartUser.setWrapupTime("");
                                }
                                break;

                            case "showtimer":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setShowTimer(true);
                                } else {
                                    smartUser.setShowTimer(false);
                                }
                                break;

                            case "backtonetwork":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setBackToNetwork(true);
                                } else {
                                    smartUser.setBackToNetwork(false);
                                }
                                break;

                            case "donerefresh":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setDoneRefresh(true);
                                } else {
                                    smartUser.setDoneRefresh(false);
                                }
                                break;

                            case "uploadstatus2":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setUploadStatus2(true);
                                } else {
                                    smartUser.setUploadStatus2(false);
                                }
                                break;

                            case "customerinfoack":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setCustomerInfoAck(true);
                                } else {
                                    smartUser.setCustomerInfoAck(false);
                                }
                                break;

                            case "customerques":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setCustomerQues(true);
                                } else {
                                    smartUser.setCustomerQues(false);
                                }
                                break;

                            case "testnumber":
                                if(sObject.has("definition")) {
                                    smartUser.setTestNumber(sObject.getString("definition"));
                                }
                                break;

                            case "acpdata":
                                if(sObject.has("definition")) {
                                    smartUser.setACPData(sObject.getString("definition"));
                                }
                                break;

                            case "enableecb":
                                if(sObject.has("definition")) {
                                    smartUser.setEnableECB(sObject.getString("definition"));
                                }
                                break;

                            case "enablemsg":
                                if(sObject.has("definition")) {
                                    smartUser.setEnableMessage(sObject.getString("definition"));
                                }
                                break;

                            case "isemulationon":
                                if(sObject.has("definition")) {
                                    smartUser.setEmulationOn(true);
                                } else {
                                    smartUser.setEmulationOn(false);
                                }
                                break;

                            case "allowscreenshot":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAllowScreenshot(true);
                                } else {
                                    smartUser.setAllowScreenshot(false);
                                }
                                break;

                            case "systemcontrol":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setSystemControl(true);
                                } else {
                                    smartUser.setSystemControl(false);
                                }
                                break;

                            case "ssuonsio":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setSSUOnSio(true);
                                } else {
                                    smartUser.setSSUOnSio(false);
                                }
                                break;

                            case "fkcontrol":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setFKControl(true);
                                } else {
                                    smartUser.setFKControl(false);
                                }
                                break;

                            case "connectingtext":
                                if(sObject.has("definition")) {
                                    smartUser.setConnectingText(sObject.getString("definition"));
                                }
                                break;

                            case "connectedtext":
                                if(sObject.has("definition")) {
                                    smartUser.setConnectedText(sObject.getString("definition"));
                                }
                                break;

                            case "programcw":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setCallWaiting(true);
                                } else {
                                    smartUser.setCallWaiting(false);
                                }
                                break;

                            case "ibcontrol":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setIBControl(true);
                                } else {
                                    smartUser.setIBControl(false);
                                }
                                break;

                            case "autoanswer":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAutoAnswer(true);
                                } else {
                                    smartUser.setAutoAnswer(false);
                                }
                                break;

                            case "inques":
                                if(sObject.has("definition")) {
                                    smartUser.setInQuestionnaire(sObject.getString("definition"));
                                }
                                break;

                            case "showui":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setShowUI(true);
                                } else {
                                    smartUser.setShowUI(false);
                                }
                                break;

                            case "allowmobiledata":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAllowMobileData(true);
                                } else {
                                    smartUser.setAllowMobileData(false);
                                }
                                break;

                            case "homenavigation":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setHomeNavigation(true);
                                } else {
                                    smartUser.setHomeNavigation(false);
                                }
                                break;

                            case "adhoccall":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAdhocCall(true);
                                } else {
                                    smartUser.setAdhocCall(false);
                                }
                                break;

                            case "weblayout":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setWebLayout(true);
                                } else {
                                    smartUser.setWebLayout(false);
                                }
                                break;

                            case "immqsubmit":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setImmediateQuesSubmit(true);
                                } else {
                                    smartUser.setImmediateQuesSubmit(false);
                                }
                                break;

                            case "allowping":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAllowPing(true);
                                } else {
                                    smartUser.setAllowPing(false);
                                }
                                break;

                            case "chatEnable":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAllowChatBot(true);
                                    Log.e("ParseAndOp","ChatBot :: "+sObject.getString("definition"));
                                }else{
                                    smartUser.setAllowChatBot(false);
                                }
                                break;

                            case "groupChatCallEnable":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAllowGroupChatCall(true);
                                    Log.e("ParseAndOp","groupChatCallEnable :: "+sObject.getString("definition"));
                                }else{
                                    smartUser.setAllowGroupChatCall(false);
                                }
                                break;

                            case "chatOptions":
                                if (sObject.has("definition")) {
                                    smartUser.setOptionsChatbot(sObject.getString("definition"));
                                }
                                break;

                            case "chatboturl":
                                if (sObject.has("definition")) {
                                    smartUser.setChatbotURL(sObject.getString("definition"));
                                }
                                break;

                            case "c2cchat":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAllowChatC2C(true);
                                }else{
                                    smartUser.setAllowChatC2C(false);
                                }
                                break;

                            case "allowadhoccall":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAllowAdhocCall(true);
                                } else {
                                    smartUser.setAllowAdhocCall(false);
                                }
                                break;

                            case "activateussdcode":
                                if(sObject.has("definition")) {
                                    smartUser.setActivateUssdCode(sObject.getString("definition"));
                                }
                                break;

                            case "deactivateussdcode":
                                if(sObject.has("definition")) {
                                    smartUser.setDeactivateUssdCode(sObject.getString("definition"));
                                }
                                break;

                            case "agent_state":
                                if(sObject.has("definition")) {
                                    smartUser.setUserStatus(sObject.getString("definition"), true);
                                }
                                break;

                            case "disallowheadphones":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setDisallowHeadphones(true);
                                } else {
                                    smartUser.setDisallowHeadphones(false);
                                }
                                break;

                            case "allowpmc2c":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAllowPMC2C(true);
                                }else{
                                    smartUser.setAllowPMC2C(false);
                                }
                                break;
                            case "calloptions":
                                if(sObject.has("definition")) {
                                    smartUser.setCallOptions(sObject.getString("definition"));
                                }
                                break;
                            case "callwaiting":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setCallWaitingChoice(true);
                                } else {
                                    smartUser.setCallWaitingChoice(false);
                                }
                                break;
                            case "disablecallback":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setDisableCallback(true);
                                } else {
                                    smartUser.setDisableCallback(false);
                                }
                                break;
                            case "flpduration":
                                if(sObject.has("definition")) {
                                    smartUser.setFlpDuration(sObject.getString("definition"));
                                }
                                break;
                            case "additionallogininfo":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAdditionalLoginInfo(true);
                                } else {
                                    smartUser.setAdditionalLoginInfo(false);
                                }
                                break;
                            case "siorefreshtime":
                                if(sObject.has("definition")) {
                                    smartUser.setSIORefreshTime(sObject.getString("definition"));
                                }
                                break;
                            case "abortcallsession":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setAbortCallSession(true);
                                } else {
                                    smartUser.setAbortCallSession(false);
                                }
                                break;
                            case "updatecustomkvs":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setUpdateCustomKVS(true);
                                } else {
                                    smartUser.setUpdateCustomKVS(false);
                                }
                                break;
                            case "defaultdialer":
                                if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                    smartUser.setDefaultDialer(true);
                                } else {
                                    smartUser.setDefaultDialer(false);
                                }
                                break;
                        }
                        //Log.d("test", "data" + sObject.getString("name") + " " + sObject.getString("definition"));
                    }

                    String salesStages = "";
                    if (jObject.has("salesstages")) {
                        JSONArray settingsSalesStages = jObject.getJSONArray("salesstages");
                        for (int i = 0; i < settingsSalesStages.length(); i++) {
                            JSONObject sObject = settingsSalesStages.getJSONObject(i);
                            salesStages += sObject.getString("name") + "\n";
                            //Log.d("salesstages",sObject.getString("name"));
                        }

                        ApplicationSettings.putPref(AppConstants.SALES_STAGES, salesStages);
                    }

                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                    String syncDate = "Updated: " + df.format(c.getTime());

                    ApplicationSettings.putPref(AppConstants.LAST_SYNC_SETTINGS, syncDate);
                    String cloudTelephony = "";
                    if (jObject.has("cloudtelephony")) {
                        JSONArray settingsSalesStages = jObject.getJSONArray("cloudtelephony");
                        for (int i = 0; i < settingsSalesStages.length(); i++) {
                            JSONObject sObject = settingsSalesStages.getJSONObject(i);
                            cloudTelephony += sObject.getString("name") + ",";
                            //Log.d("cloudyIncoming",sObject.getString("name"));
                        }
                        //Log.d("CLOSETIME" , settingsSalesStages.toString());

                        SalesStageInfo salesStageInfo = new SalesStageInfo();
                        salesStageInfo.setAmeyoNumbers(cloudTelephony);
                        smartUser.setRecordOnlyCloudTelephonyNumbers(true);
                        ApplicationSettings.putPref(AppConstants.CLOUD_TELEPHONY, cloudTelephony);
                        ApplicationSettings.putPref(AppConstants.AMEYO_NUMBERS, cloudTelephony);
                    }

                    if (jObject.has("cloudtelephonyout")) {
                        String clodtelephonyout = jObject.getString("cloudtelephonyout");
                        JSONArray jsonArray = new JSONArray(clodtelephonyout);
                        //Log.d("cloudOutGoingArray",jsonArray.toString());
                        for (int m = 0; m < jsonArray.length(); m++) {
                            JSONObject sObject = jsonArray.getJSONObject(m);
                            if (sObject.has("name") && sObject.getString("name").equalsIgnoreCase("srnumber")) {
                                String defination = sObject.getString("definition");
                                ApplicationSettings.putPref(AppConstants.SR_NUMBER, defination);
                                //Log.d("srNumber", defination);
                            }
                            if (sObject.has("name") && sObject.getString("name").equalsIgnoreCase("authkey")) {
                                String defination = sObject.getString("definition");
                                ApplicationSettings.putPref(AppConstants.AUTH_KEY, defination);
                                //Log.d("authkey", defination);
                            }
                            if (sObject.has("name") && sObject.getString("name").equalsIgnoreCase("innumber1")) {
                                String innumber1 = sObject.getString("definition");
                                ApplicationSettings.putPref(AppConstants.CLOUD_OUTGOING1, innumber1);
                                //Log.d("inNumber1",innumber1);
                            }
                            if (sObject.has("name") && sObject.getString("name").equalsIgnoreCase("innumber2")) {
                                String innumber2 = sObject.getString("definition");
                                ApplicationSettings.putPref(AppConstants.CLOUD_OUTGOING2, innumber2);
                                //Log.d("innumber2", innumber2);
                            }
                            if (sObject.has("name") && sObject.getString("name").equalsIgnoreCase("innumber3")) {
                                String innumber3 = sObject.getString("definition");
                                ApplicationSettings.putPref(AppConstants.CLOUD_OUTGOING3, innumber3);
                                //Log.d("innumber2", innumber2);
                            }

                        }
                    }

                    if (jObject.has("autodialer")) {
                        String sutoDailer = jObject.getString("autodialer");
                        JSONArray jsonArray = new JSONArray(sutoDailer);
                        for (int m = 0; m < jsonArray.length(); m++) {
                            JSONObject sObject = jsonArray.getJSONObject(m);

                            if (sObject.has("name") && sObject.getString("name").equalsIgnoreCase("waitbefore")) {
                                String before = sObject.getString("definition");
                                ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, before);
                                //Log.d("waitbefore",sObject.getString("name"));
                            }

                            if (sObject.has("name") && sObject.getString("name").equalsIgnoreCase("waitafter")) {
                                String after = sObject.getString("definition");
                                ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, after);
                                //Log.d("WAItafter",sObject.getString("name"));
                            }

                        }
                    }
                    if(jObject.has("workslots")){
                        JSONArray settingsWorkslots = jObject.getJSONArray("workslots");
                        for (int i = 0; i < settingsWorkslots.length(); i++) {
                            JSONObject sObject = settingsWorkslots.getJSONObject(i);
                            String name = sObject.getString("name");
                            if (name != null && name.equalsIgnoreCase("ASF")) {
                                    Log.e("ParseAndOp","ASF :: "+sObject.getString("definition"));
                                    if (sObject.getString("definition").equalsIgnoreCase("on")) {
                                        Log.e("ParseAndOp","on :: ");
                                        smartUser.setASF(true);
                                    } else {
                                        Log.e("ParseAndOp","off :: ");
                                        smartUser.setASF(false);
                                    }


                            }
                        }
                    }
                    if (jObject.has("workhours")) {
                        JSONArray settingsSalesStages = jObject.getJSONArray("workhours");
                        for (int i = 0; i < settingsSalesStages.length(); i++) {
                            JSONObject sObject = settingsSalesStages.getJSONObject(i);
                            String name = sObject.getString("name");
                            if (name != null && name.equalsIgnoreCase("starttime")) {
                                String definnation = sObject.getString("definition");
                                if (definnation != null && !definnation.isEmpty()) {
                                    setOpenTime(definnation,1);
                                    //Log.d("OPENTIME",definnation);
                                } else {
                                    ApplicationSettings.putPref(AppConstants.OPEN_TIME, "09:00");
                                    //Log.d("OPENTIME","9AM");
                                }
                            } else if (name != null && name.equalsIgnoreCase("endtime")) {
                                String definnation = sObject.getString("definition");
                                if (definnation != null && !definnation.isEmpty()) {
                                    setOpenTime(definnation,2);
                                    // ApplicationSettings.putPref(AppConstants.CLOSE_TIME, definnation);
                                    //Log.d("CLOSETIME",definnation);
                                } else {
                                    ApplicationSettings.putPref(AppConstants.CLOSE_TIME, "18:00");
                                    //Log.d("CLOSETIME","6PM");
                                }

                            } else if (name != null && name.equalsIgnoreCase("MON")) {
                                String definnation = sObject.getString("definition");
                                if (definnation != null && definnation.equalsIgnoreCase("on")) {
                                    ApplicationSettings.putPref(AppConstants.MON, true);
                                    //Log.d("MON","true");
                                } else {
                                    ApplicationSettings.putPref(AppConstants.MON, false);
                                    //Log.d("MON","false");
                                }

                            } else if (name != null && name.equalsIgnoreCase("TUE")) {
                                String definnation = sObject.getString("definition");
                                if (definnation != null && definnation.equalsIgnoreCase("on")) {
                                    ApplicationSettings.putPref(AppConstants.TUE, true);
                                    //Log.d("TUE","true");
                                } else {
                                    ApplicationSettings.putPref(AppConstants.TUE, false);
                                    //Log.d("TUE","false");
                                }

                            } else if (name != null && name.equalsIgnoreCase("WED")) {
                                String definnation = sObject.getString("definition");
                                if (definnation != null && definnation.equalsIgnoreCase("on")) {
                                    ApplicationSettings.putPref(AppConstants.WEN, true);
                                    //Log.d("WED","TRUE");
                                } else {
                                    ApplicationSettings.putPref(AppConstants.WEN, false);
                                    //Log.d("WED","false");
                                }
                            } else if (name != null && name.equalsIgnoreCase("THU")) {
                                String definnation = sObject.getString("definition");
                                if (definnation != null && definnation.equalsIgnoreCase("on")) {
                                    ApplicationSettings.putPref(AppConstants.THU, true);
                                    //Log.d("THU",""+true);
                                } else {
                                    ApplicationSettings.putPref(AppConstants.THU, false);
                                    //Log.d("THU","false");
                                }

                            } else if (name != null && name.equalsIgnoreCase("FRI")) {
                                String definnation = sObject.getString("definition");
                                if (definnation != null && definnation.equalsIgnoreCase("on")) {
                                    ApplicationSettings.putPref(AppConstants.FRI, true);
                                    //Log.d("FRI",""+true);
                                } else {
                                    ApplicationSettings.putPref(AppConstants.FRI, false);
                                    //Log.d("FRI",""+true);
                                }

                            } else if (name != null && name.equalsIgnoreCase("SAT")) {
                                String definnation = sObject.getString("definition");
                                if (definnation != null && definnation.equalsIgnoreCase("on")) {
                                    ApplicationSettings.putPref(AppConstants.SAT, true);
                                    //Log.d("SAT",""+true);
                                } else {
                                    ApplicationSettings.putPref(AppConstants.SAT, false);
                                    //Log.d("SAT", ""+false);
                                }

                            } else if (name != null && name.equalsIgnoreCase("SUN")) {
                                String definnation = sObject.getString("definition");
                                //Log.d("SUN",definnation);
                                if (definnation != null && definnation.equalsIgnoreCase("on")) {
                                    ApplicationSettings.putPref(AppConstants.SUN, true);
                                    //Log.d("sun",""+false);
                                } else {
                                    ApplicationSettings.putPref(AppConstants.SUN, false);
                                    //Log.d("SUN",""+true);
                                }

                            }
                        }
                    }
                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // new CustomSingleButtonDialog().buildSingleButtonDialog("Woops", "Something went wrong please try again", activity, false);
    }

    public static ArrayList<HashMap<String, String>> teamData = new ArrayList<>();

    public static void parseTeamData(Activity activity, JSONObject jsonObject) {
        //Log.d("team", "data" + jsonObject);
        SmartUser smartUser = SmarterSMBApplication.SmartUser;
        smartUser.setEditSettings(false);

        try {
            if (jsonObject.has("success")) {
                teamData.clear();

                JSONObject jObject = null;
                jObject = jsonObject.getJSONObject("success");
                int supervisorCount = 0;
                if (jObject.has("supervisors")) {
                    JSONArray settingsOptions = jObject.getJSONArray("supervisors");
                    for (int i = 0; i < settingsOptions.length(); i++) {
                        JSONObject sObject = settingsOptions.getJSONObject(i);
                        supervisorCount++;
                        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "").equalsIgnoreCase(sObject.getString("email"))) {
                            smartUser.setEditSettings(true);
                            break;
                        }
                        ApplicationSettings.putPref(AppConstants.SUPERVISOR_EMAIL, sObject.getString("email"));
                        //Log.d("supervisor", "mail " + sObject.getString("email"));
                    }
                }

                if (jObject.has("me")) {
                    JSONObject me = jObject.getJSONObject("me");


                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", me.getString("id"));
                    map.put("email", me.getString("email"));
                    map.put("groupid", me.getString("groupinviteid"));
                    map.put("name", me.getString("name"));
                    map.put("role", me.getString("role"));

                    teamData.add(map);
                    //Log.d("team", "me" + me.getString("email"));

                    if ((me.getString("role").equalsIgnoreCase("superadmin")) || (me.getString("role").equalsIgnoreCase("admin"))) {
                        ApplicationSettings.putPref(AppConstants.SUPERVISOR_EMAIL, me.getString("email"));

                        ApplicationSettings.putPref(AppConstants.ADMIN_PERSONAL_RECORDING, ApplicationSettings.getPref(AppConstants.ADMIN_PERSONAL_RECORDING, false));
                        smartUser.setCallRecordStatus(ApplicationSettings.getPref(AppConstants.ADMIN_PERSONAL_RECORDING, false));
                    }
                }

                if (supervisorCount == 0) {
                    smartUser.setEditSettings(true);
                }

                if (jObject.has("groups")) {
                    JSONArray groups_array = jObject.getJSONArray("groups");
                    if (groups_array.length() > 0) {
                        mapGroups(groups_array);
                        /*for (int i = 0; i < groups_array.length(); i++) {
                            JSONObject userObj = new JSONObject(groups_array.get(i).toString());
                            String id = userObj.getString("id");
                            String email = userObj.getString("email");
                            String depedent_type = userObj.getString("dependenttype");
                            String phone = userObj.getString("phone");
                            String request = userObj.getString("request");

                            String status = userObj.getString("status");
                            String dependent_id = "";
                            if (userObj.has("dependentid")) {
                                dependent_id = userObj.getString("dependentid");
                            }
                            GroupsUserInfo groupsUserInfo = new GroupsUserInfo(email, depedent_type, phone, request, status);
                            groupsUserInfo.setUserId(id);
                            groupsUserInfo.setDependent_userId(dependent_id);
                            // groupsUserInfoArrayList.add(groupsUserInfo);
                        }*/
                    } else {
                        smartUser.setEditSettings(true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void mapGroups(JSONArray groupsArray) {
        try {
            if (groupsArray.length() > 0) {
                for (int i = 0; i < groupsArray.length(); i++) {
                    JSONObject userObj = null;

                    userObj = new JSONObject(groupsArray.get(i).toString());

                    String id = userObj.getString("id");
                    String email = userObj.getString("email");
                    String groupId = userObj.getString("userid");
                    String name = userObj.getString("name");
                    String role = userObj.getString("role");

                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", id);
                    map.put("email", email);
                    map.put("name", name);
                    map.put("groupid", groupId);
                    map.put("role", role);
                    //Log.d("team", "groups" + name);

                    JSONArray groupData = userObj.getJSONArray("groups");
                    if (groupData.length() > 0) {
                        map.put("multilevel", "1");
                        teamData.add(map);
                        mapGroups(userObj.getJSONArray("groups"));
                    } else {
                        map.put("multilevel", "0");
                        teamData.add(map);
                    }
                }
            } else {
                //Log.d("team", "me" + "No groups");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void setOpenTime(String open, int check) {
        String callStartTime = "";
        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(new Date().getTime());//set the current time and date for this calendar

        String[] arr = open.split(":");
        if (arr.length >= 2) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int day1 = cur_cal.get(Calendar.DAY_OF_YEAR);

                cal.add(Calendar.DAY_OF_YEAR, day1);
                int hour = 0, mins = 0;
                if (arr[0] != null && !(arr[0].isEmpty())) {
                    hour = Integer.parseInt(arr[0]);
                    cal.set(Calendar.HOUR, hour);
                }
                if (arr[1] != null && !(arr[1].isEmpty())) {
                    mins = Integer.parseInt(arr[1]);
                    cal.set(Calendar.MINUTE, mins);
                    cal.setTime(new Date());
                    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),hour , mins);
                }

                Date time = cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                SimpleDateFormat pstFormat = new SimpleDateFormat("HH:mm");
                pstFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                callStartTime = pstFormat.format(sdf.parse(time.toString()));
                if(check == 1) {
                    ApplicationSettings.putPref(AppConstants.OPEN_TIME, callStartTime);
                } else if(check == 2) {
                    ApplicationSettings.putPref(AppConstants.CLOSE_TIME, callStartTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
