package smarter.uearn.money.utils;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import smarter.uearn.money.agentslots.model.SlotCancel;
import smarter.uearn.money.agentslots.model.SlotData;
import smarter.uearn.money.chats.model.PMRequestChannel;
import smarter.uearn.money.chats.model.ResponseChannel;
import smarter.uearn.money.chats.model.TechSupportRequest;
import smarter.uearn.money.models.AppStatus;
import smarter.uearn.money.models.AttachmentList;
import smarter.uearn.money.models.Attachments;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GetGmailInfo;
import smarter.uearn.money.models.GoogleAccount;
import smarter.uearn.money.models.GoogleAccountsList;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.HomeScreenMail;
import smarter.uearn.money.models.LatLong;
import smarter.uearn.money.models.NetworkDetails;
import smarter.uearn.money.models.OneViewScreenMail;
import smarter.uearn.money.models.PersonalJunkContact;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.models.SingleApiAppointmentModel;
import smarter.uearn.money.models.SmartMail;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.models.UearnVoiceTestInfo;
import smarter.uearn.money.models.WorkOrderEntryInfo;
import smarter.uearn.money.notificationmessages.model.NotificationMessageResponse;
import smarter.uearn.money.notificationmessages.model.PostNotificationMessage;
import smarter.uearn.money.office365.Onedrive.OnedriveObject;
import smarter.uearn.money.training.model.MessageReadRequest;
import smarter.uearn.money.training.model.TrainerReview;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.ServerAPIConnectors.MyJsonObject;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.webservice.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class JSONParser {

    public static JSONObject getJsonFromCmailUser(SmartUser smartUser) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("company", "UEARN");
            jsonObj.put("name", smartUser.getName());
            jsonObj.put("email", smartUser.getEmail());
            jsonObj.put("phone", smartUser.getPhone());
            jsonObj.put("members", "");
            jsonObj.put("gender", smartUser.getGender());
            jsonObj.put("business_logo_url", smartUser.getBusiness_logo_url());
            jsonObj.put("call_sms_template", smartUser.getCall_sms_template());
            jsonObj.put("jd_sms_template", smartUser.getJd_sms_template());
            jsonObj.put("business_template", smartUser.getbusiness_template());
            jsonObj.put("jc_sms_template", smartUser.getJc_sms_template());
            jsonObj.put("twitter_link", smartUser.getTwitter_link());
            jsonObj.put("linkedin_link", smartUser.getLinked_link());
            jsonObj.put("facebook_link", smartUser.getFacebook_link());
            jsonObj.put("jobtitle", smartUser.getJobTitle());
            jsonObj.put("youtube_link", smartUser.getYoutubeChannelLink());
            jsonObj.put("lat", "");
            jsonObj.put("long", "");
            jsonObj.put("google_map_url", smartUser.getGoogle_map_url());
            jsonObj.put("call_recording_auto", "");
            jsonObj.put("call_recording_status", "");
            jsonObj.put("password", smartUser.getPassword());
            jsonObj.put("secondary_users", "");
            jsonObj.put("jd_no", smartUser.getJd_no());
            jsonObj.put("address", smartUser.getAddress());
            jsonObj.put("website", smartUser.getWebsite());
            jsonObj.put("yt_video_link", smartUser.getYt_video_link());
            jsonObj.put("customer_testimonial_url", smartUser.getCustomer_testimonial_url());
            jsonObj.put("facility_photo_url1", smartUser.getFacility_photo_url1());
            jsonObj.put("google_map_landmark", smartUser.getGoogle_map_landmark());
            jsonObj.put("login_method", smartUser.getLogin_method());
            jsonObj.put("facility_photo_url2", smartUser.getFacility_photo_url2());
            jsonObj.put("facility_photo_url3", smartUser.getFacility_photo_url3());
            jsonObj.put("facility_photo_url4", smartUser.getFacility_photo_url4());
            jsonObj.put("primary_contact", smartUser.getPrimary_contact());
            jsonObj.put("send_map_location", smartUser.getSend_map_location());
            jsonObj.put("db_allowbooking", smartUser.getBookAppointment());
            jsonObj.put("website", smartUser.getWebsite());
            jsonObj.put("source_of_login", "UearnApp_android");
            jsonObj.put("reg_mode", "ANDROID");
            jsonObj.put("lang", smartUser.getLanguage());
            jsonObj.put("education", smartUser.getEducation());
            jsonObj.put("work_experience", smartUser.getWorkExperience());
            jsonObj.put("business_process", smartUser.getBusinessProcess());
            jsonObj.put("other_experience", smartUser.getOtherExperience());
            jsonObj.put("additional_data", smartUser.getAdditionalData());
            System.out.print(jsonObj);
            return jsonObj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJsonObjForGmail(GetGmailInfo gmailInfo) {
        JSONObject jsonObj = new JSONObject();
        JSONObject messageObj = new JSONObject();
        JSONObject bodyJsonObj = new JSONObject();
        JSONObject senderJsonObj = new JSONObject();
        JSONObject fromJsonObj = new JSONObject();
        JSONArray toJsonArray = new JSONArray();
        JSONObject toJsonObj = new JSONObject();
        JSONArray ccJsonArray = new JSONArray();
        JSONObject ccJsonObj = new JSONObject();
        JSONObject emailAddressJsonObj = new JSONObject();
        JSONObject toEmailAddressJson = new JSONObject();
        JSONObject ccEmailAddressJson = new JSONObject();

        JSONArray attachmentsJsonArray = new JSONArray();

        try {
            jsonObj.put("user_id", gmailInfo.userId);

            bodyJsonObj.put("contentType", "Text");
            bodyJsonObj.put("content", gmailInfo.content);

            emailAddressJsonObj.put("address", SmarterSMBApplication.SmartUser.getEmail());
            toEmailAddressJson.put("address", gmailInfo.to);
            ccEmailAddressJson.put("address", gmailInfo.cc);

            senderJsonObj.put("emailAddress", emailAddressJsonObj);
            fromJsonObj.put("emailAddress", emailAddressJsonObj);
            toJsonObj.put("emailAddress", toEmailAddressJson);
            toJsonArray.put(toJsonObj);
            ccJsonObj.put("emailAddress", ccEmailAddressJson);
            ccJsonArray.put(ccJsonObj);

            if (gmailInfo.attachmentsArrayList != null) {
                if (gmailInfo.attachmentsArrayList.size() > 0) {
                    for (int i = 0; i < gmailInfo.attachmentsArrayList.size(); i++) {
                        Attachments attachments = gmailInfo.attachmentsArrayList.get(i);
                        JSONObject attachmentJsonObj = new JSONObject();
                        attachmentJsonObj.put("name", attachments.attachment_name);
                        attachmentJsonObj.put("sourceUrl", attachments.attachment_url);
                        attachmentJsonObj.put("uploadtype", "link");
                        attachmentJsonObj.put("permission", "Edit");
                        attachmentJsonObj.put("providerType", "OneDriveConsumer");
                        attachmentJsonObj.put("isFolder", false);
                        attachmentsJsonArray.put(attachmentJsonObj);
                    }
                }
            }

            messageObj.put("subject", gmailInfo.subject);
            messageObj.put("body", bodyJsonObj);
            messageObj.put("sender", senderJsonObj);
            messageObj.put("from", fromJsonObj);
            messageObj.put("toRecipients", toJsonArray);
            //Log.i("JsonParser","CC address is "+gmailInfo.cc);
            if (gmailInfo.cc != null && !gmailInfo.cc.equals("")) {
                messageObj.put("ccRecipients", ccJsonArray);
            }
            messageObj.put("attachments", attachmentsJsonArray);

            jsonObj.put("Message", messageObj);
            jsonObj.put("SaveToSentItems", true);



/*            jsonObj.put("to",gmailInfo.to);
            jsonObj.put("subject", gmailInfo.subject);
            jsonObj.put("text", gmailInfo.content);
            jsonObj.put("cc", gmailInfo.cc);
            jsonObj.put("bcc", gmailInfo.bcc);*/

            //Log.i("Tag","JSON Obj For google create email is "+jsonObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    public static JSONObject getJsonForComposeGmail(GetGmailInfo gmailInfo) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("user_id", gmailInfo.userId);
            jsonObj.put("to", gmailInfo.to);
            jsonObj.put("subject", gmailInfo.subject);
            jsonObj.put("text", gmailInfo.content);
            jsonObj.put("cc", gmailInfo.cc);
            jsonObj.put("bcc", gmailInfo.bcc);
            if (gmailInfo.messageId != null)
                jsonObj.put("message_id", gmailInfo.messageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }


    public static String getUearnVoiceTestInfo(String response, String userId) {
        FollowupsReports followups = new FollowupsReports();
        MyJsonObject jsonObject = null;
        String success = " ";
        UearnVoiceTestInfo testInfo;
        List<UearnVoiceTestInfo> voiceTestArr = new ArrayList<>();
        JSONArray successJsonArray;
        String alertMessage = "";

        try {
            jsonObject = new MyJsonObject(response);
            if(jsonObject.has("alert_message")) {
                alertMessage = jsonObject.getString("alert_message");
                ApplicationSettings.putPref("UearnVoiceTestAlertMessage", alertMessage);
            }
            String successJson = jsonObject.getString("success");

            successJsonArray = new JSONArray(successJson);
            JSONObject myJson;
            JSONArray array = new JSONArray();
            for (int i = 0; i < successJsonArray.length(); i++) {
                myJson = successJsonArray.getJSONObject(i);
                JSONObject object = new JSONObject();
                try {
                    object.put("name", myJson.getString("name"));
                    object.put("languageType" , myJson.getString("ln"));
                    String text = myJson.getString("text");
                    JSONArray jsonArray = new JSONArray(text);

                    ArrayList<String> paragraphList = new ArrayList<>();
                    for(int j=0; j<jsonArray.length(); j++) {
                        String p1 = jsonArray.getString(j);
                        if(j == 0) {
                            object.put("para1" , p1);
                        } else if(j == 1){
                            object.put("para2" , p1);
                        } else if(j == 2){
                            object.put("para3" , p1);
                        } else {

                        }
                    }
                    array.put(object);
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("Json is " , array.toString());
            }

            ApplicationSettings.putPref("UearnVoiceTestInfo", array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return " ";
    }

    public static String getErrorString(String response) {
        try {
            JSONObject jobj = new JSONObject(response);
            if (jobj.has("errors") && jobj.has("logintype") && jobj.has("errorsphone")) {
                return jobj.getString("errorsphone");
            } else if (jobj.has("errors") && jobj.has("logintype")) {
                return jobj.getString("errors") + ";" + jobj.getString("logintype");
            } else if (jobj.has("errors")) {
                if(jobj.getString("errors") != null && jobj.getString("errors").equalsIgnoreCase("failed to assign new team member")) {
                    if (jobj.has("developerMsg")) {
                        String devMsg = jobj.getString("developerMsg");
                        if(devMsg != null && !devMsg.isEmpty()) {
                            JSONObject jsonObject = new JSONObject(devMsg);
                            if(jsonObject.has("developerMsg")) {
                                String devError = jsonObject.getString("developerMsg");
                                if(devError != null && !devError.isEmpty()) {
                                    JSONObject devObject = new JSONObject(devError);
                                    if(devObject.has("errors")) {
                                        return devObject.getString("errors");
                                    }
                                }
                            } else {
                                return jobj.getString("errors");
                            }
                        } else {
                            return jobj.getString("errors");
                        }
                    } else {
                        return jobj.getString("errors");
                    }
                } else {
                    return jobj.getString("errors");
                }
            } else if (jobj.has("error")) {
                return jobj.getString("error");
            }
            return null;
        } catch (JSONException e) {
            //e.printStackTrace();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jobj = jsonArray.getJSONObject(i);
                    if (jobj.has("errors")) {
                        return jobj.getString("errors");
                    }
                }
                return null;
            } catch (JSONException e1) {
                e1.printStackTrace();
                return "json exception";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown error";
        }
    }


    public static String getRfErrorString(String response) {
        try {
            JSONObject jobj = new JSONObject(response);
            if(jobj.has("status")) {
                String status = jobj.getString("status");
                if(status != null && status.equalsIgnoreCase("success")) {
                    return null;
                } else if(status != null && status.equalsIgnoreCase("failure")){
                    if(jobj.has("error")) {
                        return jobj.getString("error");
                    } else {
                        return "error";
                    }
                } else {
                    return "error";
                }
            }else if (jobj.has("errors") && jobj.has("logintype") && jobj.has("errorsphone")) {
                return jobj.getString("errorsphone");
            } else if (jobj.has("errors") && jobj.has("logintype")) {
                return jobj.getString("errors") + ";" + jobj.getString("logintype");
            } else if (jobj.has("errors")) {
                return jobj.getString("errors");
            } else if (jobj.has("error")) {
                return jobj.getString("error");
            }

            return null;
        } catch (JSONException e) {
            //e.printStackTrace();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jobj = jsonArray.getJSONObject(i);
                    if (jobj.has("errors")) {
                        return jobj.getString("errors");
                    }
                }
                return null;
            } catch (JSONException e1) {
                e1.printStackTrace();
                return "json exception";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown error";
        }
    }

    //this method is user for registration, login and settings
    public static SmartUser getCmailUserFromString(String cmailuserString) {
        SmartUser smartUser = SmartUser.getLocaleUser();   //this is called instead of constructor coz constructor has default values which ll override user settings
        try {
            MyJsonObject jobj = new MyJsonObject(cmailuserString);
            smartUser.setId(jobj.getString("id"), false);
            smartUser.setName(jobj.getString("name"), false);
            smartUser.setBizPrize(jobj.getString("bizprize"), true);
            smartUser.setUpdatedAt(jobj.getString("due_date"), true);
            smartUser.setGender(jobj.getString("gender"), true);
            smartUser.setLanguage(jobj.getString("lang"), true);
            smartUser.setEducation(jobj.getString("education"), true);
            smartUser.setWorkExperience(jobj.getString("work_experience"), true);
            smartUser.setBusinessProcess(jobj.getString("business_process"), true);
            smartUser.setOtherExperience(jobj.getString("other_experience"), true);
            smartUser.setUserStatus(jobj.getString("status"), true);
            smartUser.setUserCreated(jobj.getString("created_at"), true);
            smartUser.setCompany(jobj.getString("company"), false);
            smartUser.setEmail(jobj.getString("email"), true);
            smartUser.setPhone(jobj.getString("phone"), true);
            smartUser.setBusiness_logo_url(jobj.getString("business_logo_url"), true);
            smartUser.setBusiness_tagline(jobj.getString("business_tagline"), false);
            smartUser.setCall_sms_template(jobj.getString("call_sms_template"), false);
            smartUser.setbusiness_template(jobj.getString("business_template"), false);
            smartUser.setJd_sms_template(jobj.getString("jd_sms_template"), false);
            smartUser.setJc_sms_template(jobj.getString("jc_sms_template"), false);
            smartUser.setJd_no(jobj.getString("jd_no"), false);
            smartUser.setAddress(jobj.getString("address"), true);
            smartUser.setYt_video_link(jobj.getString("yt_video_link"), false);
            smartUser.setTwitter_link(jobj.getString("twitter_link"), false);
            smartUser.setFacebook_link(jobj.getString("facebook_link"), false);
            smartUser.setLinked_link(jobj.getString("linkedin_link"), false);
            smartUser.setJobTitle(jobj.getString("jobtitle"), false);
            smartUser.setYoutubeChannelLink(jobj.getString("youtube_link"), false);
            smartUser.setWebsite(jobj.getString("website"), false);
            smartUser.setCountrycode(jobj.getString("countrycode"));
            smartUser.setCustomer_testimonial_url(jobj.getString("customer_testimonial_url"), false);
            smartUser.setGoogle_map_url(jobj.getString("google_map_url"), false);
            smartUser.setGoogle_map_landmark(jobj.getString("google_map_landmark"), false);
            smartUser.setJd_url(jobj.getString("jd_url"), false);
            smartUser.setLogin_method(jobj.getString("login_method"), false);
            smartUser.setFacility_photo_url1(jobj.getString("facility_photo_url1"), false);
            smartUser.setFacility_photo_url2(jobj.getString("facility_photo_url2"), false);
            smartUser.setFacility_photo_url3(jobj.getString("facility_photo_url3"), false);
            smartUser.setFacility_photo_url4(jobj.getString("facility_photo_url4"), false);
            smartUser.setPassword(jobj.getString("password"), false);
            smartUser.setPrimary_contact(jobj.getString("primary_contact"), false);
            smartUser.setPayment_status(jobj.getString("payment_status"), true);
            smartUser.setAccessToken(jobj.getString("token"));
            smartUser.setUserRole(jobj.getString("role"));
            smartUser.setUserDob(jobj.getString("dob"));
            smartUser.setUserSpokenLang(jobj.getString("lang"));
            smartUser.setUserLocation(jobj.getString("locality"));
            //Added By Srinath.k
            if (jobj.has("profile_pic_url") && (jobj.getString("profile_pic_url") != null) && !(jobj.getString("profile_pic_url").equalsIgnoreCase("null"))) {
                smartUser.setProfileUrl(jobj.getString("profile_pic_url"));
            }
            if (jobj.getString("send_map_location") != null && jobj.getString("send_map_location") != "") {
                //Modified :::KSN
                smartUser.setSend_map_location(jobj.getBoolean("send_map_location"));
            } else
                smartUser.setSend_map_location(false);

            if (jobj.getString("db_allowbooking") != null && jobj.getString("db_allowbooking") != "") {
                smartUser.setBookAppointment(jobj.getBoolean("db_allowbooking"));
            } else {
                smartUser.setBookAppointment(false);
            }

            if (jobj.getInt("groupid") > 0) {
                smartUser.setGroupId(jobj.getInt("groupid"));
            }

            if (jobj.has("domain_url")) {
                String permanentServerURL = Urls.PERMANENT_SERVER_ADDRESS;
                if (permanentServerURL != null && !permanentServerURL.isEmpty() && !permanentServerURL.equals("null")) {
                    String domainurl = jobj.getString("domain_url");
                    if (domainurl != null && !(domainurl.isEmpty()) && (domainurl != " ") && !(domainurl.equalsIgnoreCase("null"))) {
                        if (!domainurl.equals(permanentServerURL)) {
                            SmarterSMBApplication.domainURLNotMatching = true;
                            Urls.PERMANENT_SERVER_ADDRESS = domainurl;
                        }
                    }
                }
            }

            if (jobj.has("server_url")) {
                String url = jobj.getString("server_url");
                if(url != null && !(url.isEmpty()) && (url != " ") && !(url.equalsIgnoreCase("null"))) {
                    Urls.SERVER_ADDRESS = url;
                    smartUser.setMyServerUrl_method(Urls.SERVER_ADDRESS, true);
                } else {
                    Urls.SERVER_ADDRESS = Urls.PERMANENT_SERVER_ADDRESS;
                }
            } else {
                Urls.SERVER_ADDRESS = Urls.PERMANENT_SERVER_ADDRESS;
            }

            Constants.DOMAIN_URL = Urls.SERVER_ADDRESS + "/";

            if (jobj.has("sio_url")) {
                String mysiourl = jobj.getString("sio_url");
                if (mysiourl != null && !(mysiourl.isEmpty()) && (mysiourl != " ") && !(mysiourl.equalsIgnoreCase("null"))) {
                    Urls.SERVER_SIO_ADDRESS = mysiourl;
                    smartUser.setMySioUrl_method(Urls.SERVER_SIO_ADDRESS, true);
                } else {
                    Urls.SERVER_SIO_ADDRESS = Urls.PERMANENT_SIO_ADDRESS;
                }
            } else {
                Urls.SERVER_SIO_ADDRESS = Urls.PERMANENT_SIO_ADDRESS;
            }

            if (jobj.has("rtd_url")) {
                String mydashboardsiourl = jobj.getString("rtd_url");
                if (mydashboardsiourl != null && !(mydashboardsiourl.isEmpty()) && (mydashboardsiourl != " ") && !(mydashboardsiourl.equalsIgnoreCase("null"))) {
                    Urls.DASHBOARD_SIO_ADDRESS = mydashboardsiourl;
                    smartUser.setMyDashboardSioUrl_method(Urls.DASHBOARD_SIO_ADDRESS, true);
                }
            }

            if (jobj.has("app_state_to_screen")) {
                String szString = "";
                szString = jobj.getString("app_state_to_screen");
                smartUser.setSmStateToScreen(szString);
            }

            if(jobj.has("branchcode")){
                String brachCode = "";
                brachCode = jobj.getString("branchcode");
                if(brachCode != null && !(brachCode.isEmpty()) && (brachCode != " ") && !(brachCode.equalsIgnoreCase("null"))){
                    ApplicationSettings.putPref(AppConstants.MY_PROFILE_PROJECT_TYPE, brachCode);
                }else{
                    ApplicationSettings.putPref(AppConstants.MY_PROFILE_PROJECT_TYPE, "");
                }
            }

            return smartUser;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJsonFromCmailMail(SmartMail cmailMail) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("user_id", cmailMail.getUserId());
            jsonObj.put("email", cmailMail.getEmail());
            jsonObj.put("from", cmailMail.getFrom());
            jsonObj.put("to", cmailMail.getTo());
            jsonObj.put("cc", cmailMail.getCc());
            jsonObj.put("bcc", cmailMail.getBcc());
            jsonObj.put("event_type", cmailMail.getEventType());
            jsonObj.put("url", cmailMail.getUrl());
            jsonObj.put("jd_url", cmailMail.getJdUrl());
            jsonObj.put("jobcard_url", cmailMail.getJobCardUrl());
            jsonObj.put("parent", cmailMail.getParent());
            jsonObj.put("live_recording_url", cmailMail.getLiveRecordingUrl());
            jsonObj.put("start_time", cmailMail.getStartTime());
            jsonObj.put("end_time", cmailMail.getEndTime());
            jsonObj.put("subject", cmailMail.getSubject());
            //jsonObj.put("unread", cmailMail.isUnread());
            jsonObj.put("message", cmailMail.getMessage());
            jsonObj.put("lat", cmailMail.getLatitude());
            jsonObj.put("long", cmailMail.getLongitude());
            jsonObj.put("attachments", cmailMail.getAttachments());
            jsonObj.put("job_card", cmailMail.getJobcard());
            jsonObj.put("employee_assignee", cmailMail.getEmployeeAssignee());
            jsonObj.put("status", cmailMail.getStatus());
            jsonObj.put("caller_name", cmailMail.getCallerName());

            return jsonObj;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static SmartMail getCmailMailFromString(String cmailMailString) {
        SmartMail cmailMail = new SmartMail();
        try {
            JSONObject jobj = new JSONObject(cmailMailString);
            cmailMail.setUserId(jobj.getString("user_id"));
            cmailMail.setId(jobj.getString("id"));
            cmailMail.setEmail(jobj.getString("email"));
            cmailMail.setFrom(jobj.getString("from"));
            cmailMail.setTo(jobj.getString("to"));
            cmailMail.setCc(jobj.getString("cc"));
            cmailMail.setBcc(jobj.getString("bcc"));
            cmailMail.setEventType(jobj.getString("event_type"));
            cmailMail.setUrl(jobj.getString("url"));
            cmailMail.setJdUrl(jobj.getString("jd_url"));
            cmailMail.setJobCardUrl(jobj.getString("jobcard_url"));
            cmailMail.setParent(jobj.getString("parent"));
            cmailMail.setLiveRecordingUrl(jobj.getString("live_recording_url"));
            cmailMail.setStartTime(jobj.getString("start_time"));
            cmailMail.setEndTime(jobj.getString("end_time"));
            cmailMail.setSubject(jobj.getString("subject"));
            cmailMail.setPassword(jobj.getString("password"));
            cmailMail.setMessage(jobj.getString("message"));
            cmailMail.setLatitude(jobj.getString("lat"));
            cmailMail.setLongitude(jobj.getString("long"));
            cmailMail.setAttachments(jobj.getString("attachments"));
            cmailMail.setJobcard(jobj.getString("job_card"));
            cmailMail.setEmployeeAssignee(jobj.getString("employee_assignee"));
            cmailMail.setStatus(jobj.getString("status"));
            cmailMail.setCallerName(jobj.getString("caller_name"));

            return cmailMail;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<OneViewScreenMail> getSmartMailArrayFromString(String cmailArrayString) {

        try {
            JSONArray jArray = new JSONArray(cmailArrayString);

            if (jArray.length() == 0) {
                return null;
            }
            ArrayList<OneViewScreenMail> parentList = new ArrayList<OneViewScreenMail>();

            for (int i = 0; i < jArray.length(); i++) {
                MyJsonObject parent_jobj = new MyJsonObject(jArray.getString(i));
                OneViewScreenMail smartMail = new OneViewScreenMail();

                String name = parent_jobj.getString("name");
                smartMail.setName(name);
                String created_at = parent_jobj.getString("created_at");
                //String createdTime = CommonUtils.getTime(created_at);
                smartMail.setStart_time(created_at);
                String end_time = parent_jobj.getString("end_time");
                smartMail.setEnd_time(end_time);
                String event_type = parent_jobj.getString("event_type");
                smartMail.setActivity_type(event_type);
                String id = parent_jobj.getString("id");
                smartMail.setId(id);
                String messageId = parent_jobj.getString("msgId");
                if (messageId != null && !messageId.equals("")) {
                    smartMail.setId(messageId);
                }

                String message = parent_jobj.getString("message");
                smartMail.setMessage(message);
                String email = parent_jobj.getString("email");
                smartMail.setEmail(email);
                String from = parent_jobj.getString("from");
                smartMail.setFrom(from);
                String to = parent_jobj.getString("to");
                smartMail.setTo(to);
                String bcc = parent_jobj.getString("bcc");
                smartMail.setBcc(bcc);
                String start_time = parent_jobj.getString("start_time");
                smartMail.setStart_time(start_time);

                String date = parent_jobj.getString("date");
                if (date != null && !date.equals("")) {
                    smartMail.setStart_time(date);
                }
                String subject = parent_jobj.getString("subject");
                smartMail.setSubject(subject);
                String recordUrl = parent_jobj.getString("url");
                smartMail.setUrl(recordUrl);
                String status = parent_jobj.getString("status");
                smartMail.setStatus(status);
                String callerName = parent_jobj.getString("caller_name");
                if (smartMail.getActivity_type().equals("gmail")) {
                    callerName = CommonUtils.handleMultipleEmailAddresses(callerName);
                }
                smartMail.setName(callerName);

                String password = parent_jobj.getString("password");
                smartMail.setPassword(password);
                String cmail_url = parent_jobj.getString("cmail_url");
                smartMail.setCmail_url(cmail_url);

                //Added AttachmentList to SmartMail: KSN
                try {
                    if (parent_jobj.has("attachments_list")) {
                        ArrayList<AttachmentList> arrayAttachments = new ArrayList<AttachmentList>();
                        AttachmentList attachmentList = new AttachmentList();
                        String attachments_list = parent_jobj.getString("attachments_list");
                        JSONArray jsonArray = new JSONArray(attachments_list);
                        for (int j = 0; j < jsonArray.length(); j++) {
                            MyJsonObject list_json = new MyJsonObject(jsonArray.getString(j));
                            String attachment_name = list_json.getString("filename");
                            attachmentList.setAttachment_name(attachment_name);
                            String attachment_file_link = list_json.getString("filelink");
                            attachmentList.setAttachment_url(attachment_file_link);
                            arrayAttachments.add(attachmentList);
                        }
                        smartMail.setAttachmentsSerilizableList(arrayAttachments);
                    }
                } catch (Exception e) {

                }

                if (event_type.equals("office365")) {
                    String senderForJson = parent_jobj.getString("sender");
                    if (senderForJson != null) {
                        JSONObject senderJson = new JSONObject(senderForJson);
                        String emailAddressForJson = senderJson.getString("emailAddress");
                        JSONObject emailAddressJson = new JSONObject(emailAddressForJson);
                        String emailAddress = emailAddressJson.getString("address");
                        smartMail.setEmail(emailAddress);
                        smartMail.setActivity_type("office365");
                        String oSubject = parent_jobj.getString("subject");
                        smartMail.setSubject(oSubject);

                        String messageJson = parent_jobj.getString("body");
                        JSONObject msgJson = new JSONObject(messageJson);
                        String oMessage = msgJson.getString("content");
                        smartMail.setHtmlMessage(oMessage);
                        String contentType = msgJson.getString("contentType");
                        if (contentType.equals("text")) {
                            String omessagebody = parent_jobj.getString("bodyPreview");
                            smartMail.setMessage(omessagebody);
                        } else {
                            smartMail.setMessage(null);
                        }
                        String ostart_time = parent_jobj.getString("start_time");
                        smartMail.setStart_time(ostart_time);

                        String oSender = parent_jobj.getString("");
                    }
                }

                /*String children = parent_jobj.getString("children");

                if (!children.equalsIgnoreCase("[]")) {
                    smartMail.setChildrenInfo(getSmartChildArrayFromString(children));
                }*/
                parentList.add(smartMail);
            }
            return parentList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //CODE CLEANUP  :::KSN
/*    public static ArrayList<SmartMail> getSmartChildArrayFromString(String cmailChildString){
        try {
            JSONArray jarray = new JSONArray(cmailChildString);
            ArrayList<SmartMail> childrenList = new ArrayList<SmartMail>();

            for(int i=0; i<jarray.length(); i++){
                JSONObject childObject = new JSONObject(jarray.get(i).toString());

                SmartMail temp = new SmartMail();

                String name = childObject.getString("name");
                temp.setName(name);
                String timeTemp = childObject.getString("created_at");
                //String created_time = CommonUtils.getTime(timeTemp);
                temp.setCreatedAt(timeTemp);
                String end_time = childObject.getString("end_time");
                temp.setEndTime(end_time);
                String event_type = childObject.getString("event_type");
                temp.setEventType(event_type);
                String id = childObject.getString("id");
                temp.setId(id);
                String message = childObject.getString("message");
                temp.setMessage(message);
                String email = childObject.getString("email");
                temp.setEmail(email);
                String from = childObject.getString("from");
                temp.setFrom(from);
                String to = childObject.getString("to");
                temp.setTo(to);
                String start_time = childObject.getString("start_time");
                temp.setStartTime(start_time);
                String subject = childObject.getString("subject");
                temp.setSubject(subject);
                String parentid = childObject.getString("parent");
                temp.setParent(parentid);
                String recordUrl = childObject.getString("url");
                temp.setUrl(recordUrl);
                String updated_at = childObject.getString("updated_at");
                temp.setUpdatedAt(updated_at);
                String user_id = childObject.getString("user_id");
                temp.setUserId(user_id);
                String jobcard = childObject.getString("job_card");
                temp.setJobcard(jobcard);
                String employeeAssignee = childObject.getString("employee_assignee");
                temp.setEmployeeAssignee(employeeAssignee);
                String status = childObject.getString("status");
                temp.setStatus(status);
                String callerName = childObject.getString("caller_name");
                temp.setCallerName(callerName);
                String bcc = childObject.getString("bcc");
                temp.setBcc(bcc);
                String password = childObject.getString("password");
                temp.setPassword(password);
                String cmail_url = childObject.getString("cmail_url");
                temp.setCmail_url(cmail_url);
                childrenList.add(temp);
            }
            return childrenList;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }*/

    public static String getSuccessStatusFromJson(String response) {
        String result = null;
        try {
            JSONObject jobj = new JSONObject(response);
            if (jobj.has("success")) {
                return jobj.getString("success");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static String getSuccessAndErrorStatusFromJson(String response) {
        String result = null;
        try {
            JSONObject jobj = new JSONObject(response);
            if (jobj.has("success")) {
                return jobj.getString("success");
            } else if (jobj.has("errors")) {
                return jobj.getString("errors");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    //CODE CLEANUP  :::KSN
 /*   public static JSONObject getJsonFromEventsRequestInfo(EventRequestInfo info){
        try{
            JSONObject jobj = new JSONObject();

            if(info.getEvent_type_str() != null)
                jobj.put("event_type_str", info.getEvent_type_str());
            if(info.getAudio_content_type() != null)
                jobj.put("audio_content_type", info.getAudio_content_type());
            if(info.getContent_type() != null)
                jobj.put("content_type", info.getContent_type());
            if(info.getCall_type() != null)
                jobj.put("call_type", info.getCall_type());
            if(info.getPhone() != null)
                jobj.put("phone", info.getPhone());
            if(info.getContact_name() != null)
                jobj.put("contact_name", info.getContact_name());

            return jobj;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }*/

    //CODE CLEANUP  :::KSN
 /*   public static EventResponseInfo getEventResponseFromString(String response){
        EventResponseInfo responseInfo = new EventResponseInfo();
        try{
            JSONObject jobj = new JSONObject(response);

            responseInfo.setAudio_content_type(jobj.getString("audio_content_type"));
            responseInfo.setAudio_file_name(jobj.getString("audio_file_name"));
            responseInfo.setAudio_file_size(jobj.getString("audio_file_size"));
            responseInfo.setAudio_updated_at(jobj.getString("audio_updated_at"));
            responseInfo.setCall_type(jobj.getString("call_type"));
            responseInfo.setContact_name(jobj.getString("contact_name"));
            responseInfo.setCreated_at(jobj.getString("created_at"));
            responseInfo.setDescription(jobj.getString("description"));
            responseInfo.setEvent_type(jobj.getString("event_type"));
            responseInfo.setEvent_type_str(jobj.getString("event_type_str"));
            responseInfo.setId(jobj.getString("id"));
            responseInfo.setLat(jobj.getString("lat"));
            responseInfo.setLong(jobj.getString("long"));
            responseInfo.setMatch_id(jobj.getString("match_id"));
            responseInfo.setPhone(jobj.getString("phone"));
            responseInfo.setPhoto_content_type(jobj.getString("photo_content_type"));
            responseInfo.setPhoto_file_name(jobj.getString("photo_file_name"));
            responseInfo.setPhoto_file_size(jobj.getString("photo_file_size"));
            responseInfo.setPhoto_updated_at(jobj.getString("photo_updated_at"));
            responseInfo.setSender(jobj.getString("sender"));
            responseInfo.setSender_pic_thumb(jobj.getString("sender_pic_thumb"));
            responseInfo.setSent_status(jobj.getString("sent_status"));
            responseInfo.setSpam(jobj.getString("spam"));
            responseInfo.setSummary(jobj.getString("summary"));
            responseInfo.setUpdated_at(jobj.getString("updated_at"));
            responseInfo.setUrl(jobj.getString("url"));
            responseInfo.setVideo_content_type(jobj.getString("video_content_type"));
            responseInfo.setVideo_file_name(jobj.getString("video_file_name"));
            responseInfo.setVideo_file_size(jobj.getString("video_file_size"));
            responseInfo.setVideo_updated_at(jobj.getString("video_updated_at"));

            return responseInfo;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }*/

    public static class EmailInfo {
        String content;
        String mimeType;
    }


    public static SmartMail getRequiredStringFromGmailResp(String resposeOfGmail) {

        String from = "", to = "", subject = "", start_date = "";
        SmartMail smartMail = new SmartMail();
        MyJsonObject jsonObject = null;
        String mimeType = null;
        String message_id = null;

        try {
            MyJsonObject parent_jobj = new MyJsonObject(resposeOfGmail);

            message_id = parent_jobj.getString("id");

            String payload = parent_jobj.getString("payload");
            //payload object
            jsonObject = new MyJsonObject(payload);
            mimeType = jsonObject.getString("mimeType");

            //   Log.i("Tag","mimitype "+mimeType );

            String headers = jsonObject.getString("headers");

            // header as jsonArray
            JSONArray headerObj = new JSONArray(headers);
            for (int n = 0; n < headerObj.length(); n++) {
                JSONObject object = headerObj.getJSONObject(n);
                if (object.getString("name").equals("From")) {
                    from = object.getString("value");
                }
                if (object.getString("name").equals("To")) {
                    to = object.getString("value");
                }
                if (object.getString("name").equals("Subject")) {
                    subject = object.getString("value");
                }
                if (object.getString("name").equals("Date")) {
                    String date = object.getString("value");
                    SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy h:m:s Z");
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date d = format.parse(date);
                        DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        start_date = isoFormat.format(d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        smartMail.setMessageId(message_id);
        smartMail.setSubject(subject);
        smartMail.setTo(to);
        smartMail.setFrom(from);
        smartMail.setStartTime(start_date);
        smartMail.setCreatedAt(start_date);
        smartMail.setUpdatedAt(start_date);
        smartMail.setEventType("gmail");

        EmailInfo email = parseEmailForContent(jsonObject);
        if (email != null) {
            if (email.mimeType.equals("text/plain")) {
                try {
                    byte[] data = Base64.decode(email.content, Base64.URL_SAFE);
                    String decodedbody = new String(data, "UTF-8");
                    smartMail.setMessage(decodedbody);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (email.mimeType.equals("text/html")) {
                smartMail.setHTMLMessage(email.content);
            }
        }

        return smartMail;
    }

    public static EmailInfo getContent(JSONObject jsonObject) {
        try {
            String mimeType = jsonObject.getString("mimeType");

            if (mimeType.equals("text/plain") || mimeType.equals("text/html")) {
                String body = jsonObject.getString("body");
                JSONObject bodyJsonObject = new JSONObject(body);
                String content = bodyJsonObject.getString("data");

                EmailInfo result = new EmailInfo();

                result.content = content;
                result.mimeType = mimeType;
                return result;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EmailInfo parseEmailForContent(JSONObject jsonObject) {
        EmailInfo result = null;

        try {
            /* Try for text/plain or text/html */
            result = getContent(jsonObject);

            if (result != null) return result;

            /* Try multi part */
            String parts = jsonObject.getString("parts");
            JSONArray partsArray = new JSONArray(parts);

            /* go through all text plain parts first */
            for (int n = 0; n < partsArray.length(); n++) {
                JSONObject partObject = partsArray.getJSONObject(n);
                String partsMime = partObject.getString("mimeType");
                if (partsMime.equals("text/html")) {
                    return getContent(partObject);
                }
            }

            /* Now try for 1st html message */
            for (int n = 0; n < partsArray.length(); n++) {
                JSONObject partObject = partsArray.getJSONObject(n);
                String partsMime = partObject.getString("mimeType");
                if (partsMime.equals("text/html")) {
                    return getContent(partObject);
                }
            }


            /* we are still here could mean that we are parts within parts ?? */
            String part2 = partsArray.getJSONObject(0).getString("parts");
            if (part2 != null & part2.length() != 0) {
                return parseEmailForContent(partsArray.getJSONObject(0));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    //CODE CLEANUP  :::KSN
    /*public static String getHTMLContent(MyJsonObject jsonObject) {
        try {
            String body = jsonObject.getString("body");
            MyJsonObject bodyJsonObject = new MyJsonObject(body);
            String content = bodyJsonObject.getString("data");
            return content;
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/


    //CODE CLEANUP  :::KSN
   /* private static Spanned decodebody(String mimetype, String encodedString) {

        String decodedbody = "";
        encodedString = encodedString.replace("-","+").replace("_","/");
        Spanned spannedBody = null;
        byte[] data = Base64.decode(encodedString, Base64.URL_SAFE);

        if(mimetype.equals("text/html")) {
            try {
                decodedbody = new String(data, "UTF-8");
                spannedBody = Html.fromHtml(decodedbody);
            } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
            }
        }
        //Log.i("Tag","Decoded String is ");
        if(mimetype.equals("text/html")) {
            decodedbody = Html.fromHtml(decodedbody).toString();

          //  Log.i("Tag","Decoded body is html" +decodedbody);
        }

//        //encodedString = encodedString.replace("-","+").replace("_","/");
//        String decodedbody = "";
//        if (encodedString != null) {
//            byte[] data = Base64.decode(encodedString, Base64.DEFAULT);
//
//        }
        //Log.i("Tag","Decoded String is "+decodedbody);

        return spannedBody;
    }
*/
    public static GoogleAccountsList getArrayListFromJsonArray(String response) {
        GoogleAccountsList googleAccountsList = new GoogleAccountsList();
        ArrayList<GoogleAccount> googleAccountArrayList = new ArrayList<>();
        String email = "";
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                GoogleAccount googleAccount = new GoogleAccount();
                email = jsonObject.getString("email");
                googleAccount.setEmail(email);
                String id = jsonObject.getString("id");
                googleAccount.setId(id);
                String user_id = jsonObject.getString("user_id");
                googleAccount.setUser_id(user_id);
                String access_token = jsonObject.getString("access_token");
                googleAccount.setAccessToken(access_token);
                String refresh_token = jsonObject.getString("refresh_token");
                googleAccount.setRefreshToken(refresh_token);
                boolean enableGmail = jsonObject.getBoolean("enabled");
                googleAccount.setIsEnable(enableGmail);
                if (jsonObject.has("enable_calendar")) {
                    if (!jsonObject.getString("enable_calendar").equals("null")) {
                        boolean enableCalendar = jsonObject.getBoolean("enable_calendar");
                        googleAccount.setGoogleCalendarEnable(enableCalendar);
                    }
                }
                //TODO: confirm it
                boolean primary_email = jsonObject.getBoolean("primary_email");
                googleAccount.setIsPrimary(primary_email);

                String typeofAccount = jsonObject.getString("type");
                googleAccount.setTypeOfAccount(typeofAccount);
                googleAccountArrayList.add(googleAccount);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        googleAccountsList.setGoogleAccountList(googleAccountArrayList);
        return googleAccountsList;
    }

    //CODE CLEANUP  :::KSN
    /*public static String getStringFromJsonObj(String response) {
        String result = "";
        try {
            MyJsonObject jsonObject = new MyJsonObject(response);
            result = jsonObject.getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }*/

    //CODE CLEANUP  :::KSN
   /* public static JSONObject getJsonObjFromCalendar(GetCalendarEntryInfo getCalendarEntryInfo){
        JSONObject jsonObj = new JSONObject();

        if(getCalendarEntryInfo.caller_number != null) {
            if (getCalendarEntryInfo.caller_number.equals(getCalendarEntryInfo.caller_name)) {
                getCalendarEntryInfo.caller_name = "";
            }
        } else
            getCalendarEntryInfo.caller_name = "";
        if(getCalendarEntryInfo.callrec_url !=null){
            if(getCalendarEntryInfo.callrec_url.equals("null"))
                getCalendarEntryInfo.callrec_url = "";
        }
        else
            getCalendarEntryInfo.callrec_url = "";
       // Log.i("Tag","In json for calendar"+getCalendarEntryInfo.caller_number+"To number is "+getCalendarEntryInfo.to);
        String notes1 = "\""+getCalendarEntryInfo.notes+"\";"+"\""+getCalendarEntryInfo.cmailId+
                "\";"+"\""+getCalendarEntryInfo.callrec_url+"\";"+"\""+getCalendarEntryInfo.caller_name+"\";"+"\""+getCalendarEntryInfo.companyName
                +"\";"+"\""+getCalendarEntryInfo.caller_name+"\";"+"\""+getCalendarEntryInfo.designation+"\";"+"\""
                +getCalendarEntryInfo.caller_number+"\";"+"\""+getCalendarEntryInfo.status+"\";"+"\""+""+"\";"+"\""+getCalendarEntryInfo.email_id+"\";"
                +"\""+getCalendarEntryInfo.location+"\";"+"\""+getCalendarEntryInfo.website+"\";"+"\""+getCalendarEntryInfo.last_contactedON+"\";";
        //Log.i("Tag","NOTES CHANGED IS "+notes1);
        *//*String notes = getCalendarEntryInfo.notes+","+getCalendarEntryInfo.cmailId+
                ","+getCalendarEntryInfo.callrec_url+","+getCalendarEntryInfo.email_id+","+getCalendarEntryInfo.companyName+
                ","+getCalendarEntryInfo.caller_name+","+getCalendarEntryInfo.designation+","+getCalendarEntryInfo.caller_number+
                ","+getCalendarEntryInfo.subject+",";*//*
        //Log.i("Tag", "Notes storing in google calendar are " + notes1);
*//*        String testing[] = notes1.split("\\s*;\\s*", 10);
        Log.i("Tag","size while creating is "+testing.length);
        for (int j= 0; j < testing.length; j++) {
            Log.i("Values","\n Value while creating is"+testing[j].replace("\"", ""));
        }*//*

        try {
            *//*Log.i("Tag","Details Storing into db are: "+getCalendarEntryInfo.user_id+","+getCalendarEntryInfo.callrec_url+","+getCalendarEntryInfo.cmailId
                    +","+getCalendarEntryInfo.event_end_date+","+getCalendarEntryInfo.event_start_date+","+getCalendarEntryInfo.location+
                    ","+getCalendarEntryInfo.subject+"time zone "+getCalendarEntryInfo.timeZone+","+getCalendarEntryInfo.from);*//*
            //Log.i("Tag","User id is "+getCalendarEntryInfo.user_id+"Smarter smv user id "+SmarterSMBApplication.SmartUser.getId());
            jsonObj.put("user_id", getCalendarEntryInfo.user_id);
            jsonObj.put("from", getCalendarEntryInfo.from);
            jsonObj.put("to", "");
            jsonObj.put("event_start_date", getCalendarEntryInfo.event_start_date);
            jsonObj.put("event_end_date", getCalendarEntryInfo.event_end_date);
            jsonObj.put("location", getCalendarEntryInfo.location);
            jsonObj.put("subject", getCalendarEntryInfo.subject);
            jsonObj.put("description", notes1);
            jsonObj.put("repeat_params", getCalendarEntryInfo.repeate);
            jsonObj.put("time_zone", getCalendarEntryInfo.timeZone);
            //Log.i("Tag","Calendar json Object is "+jsonObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }*/


    public static JSONObject getnewJsonObjFromCalendar(GetCalendarEntryInfo getCalendarEntryInfo) {
        JSONObject jsonObj = new JSONObject();

        if (getCalendarEntryInfo.caller_number != null) {
            if (getCalendarEntryInfo.caller_number.equals(getCalendarEntryInfo.caller_name)) {
                getCalendarEntryInfo.caller_name = "";
            }
        } else
            getCalendarEntryInfo.caller_name = "";
        if (getCalendarEntryInfo.callrec_url != null) {
            if (getCalendarEntryInfo.callrec_url.equals("null"))
                getCalendarEntryInfo.callrec_url = "";
        } else
            getCalendarEntryInfo.callrec_url = "";
        try {
            jsonObj.put("user_id", SmarterSMBApplication.SmartUser.getId());
            jsonObj.put("from", getCalendarEntryInfo.from);
            jsonObj.put("to", "");

            //TODO: ADD another json for date and time
            JSONObject startdatejson = new JSONObject();
            startdatejson.put("dateTime", getCalendarEntryInfo.event_start_date);
            startdatejson.put("timeZone", getCalendarEntryInfo.timeZone);

            JSONObject endDateJson = new JSONObject();
            endDateJson.put("dateTime", getCalendarEntryInfo.event_end_date);
            endDateJson.put("timeZone", getCalendarEntryInfo.timeZone);

            jsonObj.put("start", startdatejson);
            jsonObj.put("end", endDateJson);

            jsonObj.put("location", getCalendarEntryInfo.location);
            jsonObj.put("subject", getCalendarEntryInfo.subject);
            jsonObj.put("description", getCalendarEntryInfo.notes);
            jsonObj.put("repeat_params", getCalendarEntryInfo.repeate);
            jsonObj.put("status", getCalendarEntryInfo.status);

            JSONObject contact_person = new JSONObject();
            contact_person.put("company", getCalendarEntryInfo.companyName);
            contact_person.put("name", getCalendarEntryInfo.caller_name);
            contact_person.put("designation", getCalendarEntryInfo.designation);
            contact_person.put("number", getCalendarEntryInfo.caller_number);
            contact_person.put("email", getCalendarEntryInfo.email_id);
            contact_person.put("address", getCalendarEntryInfo.address);
            contact_person.put("website", getCalendarEntryInfo.website);

            jsonObj.put("contact_person", contact_person);

            if (getCalendarEntryInfo.latitude != null)
                jsonObj.put("lat", getCalendarEntryInfo.latitude);
            if (getCalendarEntryInfo.longitude != null)
                jsonObj.put("long", getCalendarEntryInfo.longitude);

            jsonObj.put("alert_before", getCalendarEntryInfo.alarm_before);
            jsonObj.put("order_potential", getCalendarEntryInfo.order_potential);
            jsonObj.put("lead_source", getCalendarEntryInfo.lead_source);
            jsonObj.put("product_type", getCalendarEntryInfo.product_type);

            /*flp count is added by srinath*/
            jsonObj.put("product_type", getCalendarEntryInfo.product_type);

            if (getCalendarEntryInfo.assign_to != null) {
                jsonObj.put("seteventto_teamemail", getCalendarEntryInfo.assign_to);
            }
            if (getCalendarEntryInfo.appointment_id != null) {
                jsonObj.put("Id", getCalendarEntryInfo.appointment_id);
            }

            if (getCalendarEntryInfo.getCcMail() != null) {
                jsonObj.put("cc", getCalendarEntryInfo.getCcMail());
            }
            jsonObj.put("rnrcount", getCalendarEntryInfo.rnrCount);

            if (getCalendarEntryInfo.getFlpCount() != null) {
                jsonObj.put("flpcount", getCalendarEntryInfo.flp_count);
                //Log.d("JSON_PARSER", "FLPCOUNT" + getCalendarEntryInfo.flp_count);
            } else {
                jsonObj.put("flpcount", 1);
                //Log.d("JSON_PARSER", "FLPCOUNT" + 1);
            }

            if (getCalendarEntryInfo.getNotesImageUrl() != null) {
                jsonObj.put("image_url", getCalendarEntryInfo.getNotesImageUrl());
            }

            if (getCalendarEntryInfo.subStatus1 != null && !(getCalendarEntryInfo.subStatus1.isEmpty())) {
                jsonObj.put("substatus1", getCalendarEntryInfo.subStatus1);
            }

            if (getCalendarEntryInfo.subStatus2 != null && !(getCalendarEntryInfo.subStatus2.isEmpty())) {
                jsonObj.put("substatus2", getCalendarEntryInfo.subStatus2);
            }

            if (getCalendarEntryInfo.created_at != null && !(getCalendarEntryInfo.created_at.isEmpty())) {
                jsonObj.put("created_at", getCalendarEntryInfo.created_at);
            }

            if (getCalendarEntryInfo.responsestatus != null && (getCalendarEntryInfo.responsestatus.equalsIgnoreCase("completed") || getCalendarEntryInfo.responsestatus.equalsIgnoreCase("missed"))) {
                jsonObj.put("responsestatus", getCalendarEntryInfo.responsestatus);
            }

            jsonObj.put("customer_tocontact", getCalendarEntryInfo.customer_id);

            //Log.i("JsonParser","Calendar json Object is "+jsonObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    public static ArrayList<GetCalendarEntryInfo> getArrayListOfCalEventsFromJson(String response) {

        try {
            JSONObject mainJsonObj = new JSONObject(response);

            String jsonArraydata = mainJsonObj.getString("event");

            JSONArray jArray = new JSONArray(jsonArraydata);
            ArrayList<GetCalendarEntryInfo> calendarEntriesArrayList = new ArrayList<>();
            if (jArray.length() == 0) {
                return null;
            }
            for (int i = 0; i < jArray.length(); i++) {
                MyJsonObject jsonObject = new MyJsonObject(jArray.get(i).toString());
                GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();

                String appointmentId = jsonObject.getString("Id");
                getCalendarEntryInfo.setAppointment_id(appointmentId);

                String userId = jsonObject.getString("user_id");
                getCalendarEntryInfo.setUser_id(userId);
                String subject = jsonObject.getString("subject");
                getCalendarEntryInfo.setSubject(subject);
                String location = jsonObject.getString("location");
                getCalendarEntryInfo.setLocation(location);
                String start_time = jsonObject.getString("start_date");
                getCalendarEntryInfo.setEvent_start_date(start_time);
                String end_time = jsonObject.getString("end_date");
                getCalendarEntryInfo.setEvent_end_date(end_time);
                String timeZone = jsonObject.getString("time_zone");
                getCalendarEntryInfo.setTimeZone(timeZone);
                String description = jsonObject.getString("description");
                getCalendarEntryInfo.setNotes(description);
                String status = jsonObject.getString("status");
                getCalendarEntryInfo.setStatus(status);
                String callerName = jsonObject.getString("name");
                getCalendarEntryInfo.setCaller_name(callerName);
                String designation = jsonObject.getString("designation");
                getCalendarEntryInfo.setDesignation(designation);
                String caller_number = jsonObject.getString("number");
                getCalendarEntryInfo.setCaller_number(caller_number);
                String email = jsonObject.getString("email");
                getCalendarEntryInfo.setEmail_id(email);
                String website = jsonObject.getString("website");
                getCalendarEntryInfo.setWebsite(website);
                String company = jsonObject.getString("company");
                getCalendarEntryInfo.setCompanyName(company);
                String companyAddress = jsonObject.getString("address");
                getCalendarEntryInfo.address = companyAddress;
                String notesImageUrl = jsonObject.getString("image_url");
                //Log.d("notes image url", "url:" + notesImageUrl);
                if (notesImageUrl != null && !notesImageUrl.isEmpty()) {
                    getCalendarEntryInfo.setNotesImageUrl(notesImageUrl);
                } else {
                    notesImageUrl = jsonObject.getString("ss_image_url");
                    if (notesImageUrl != null && !notesImageUrl.isEmpty()) {
                        getCalendarEntryInfo.setNotesImageUrl(notesImageUrl);
                    }
                }


                String notesAudioUrl = jsonObject.getString("audio_rec_url");
                //Log.d("notes audio url", "url:" + notesAudioUrl);
                if (notesAudioUrl != null) {
                    getCalendarEntryInfo.setNotesAudioUrl(notesAudioUrl);
                }

                String external_calendar_reference = jsonObject.getString("external_calendar_reference");
                if (external_calendar_reference != null) {
                    getCalendarEntryInfo.setExternal_calendar_reference(external_calendar_reference);
                }
                String id = jsonObject.getString("id");
                getCalendarEntryInfo.setCustomer_id(id);

                if (jsonObject.has("remainder_datetime")) {
                    String reminderDateTime = jsonObject.getString("remainder_datetime");
                    if (reminderDateTime != null && !(reminderDateTime.isEmpty())) {
                        getCalendarEntryInfo.alarm_before = reminderDateTime;
                        //Log.d("getCalendarAlarmEvents", getCalendarEntryInfo.alarm_before);
                    }
                }
                //Log.i("Tag", "Description Field in Json Parser is " + description);

                // TODO: FOR ACCEPT / REJECT Calendar
                String responseStuatus = jsonObject.getString("responsestatus");
                getCalendarEntryInfo.setResponsestatus(responseStuatus);
                //Log.i("JsonParser", "In getting all calendar appointments response status is " + responseStuatus);
                String appointment_id = jsonObject.getString("Id");
                getCalendarEntryInfo.setAppointment_id(appointment_id);

                String leadSource = jsonObject.getString("lead_source");
                getCalendarEntryInfo.setLead_source(leadSource);

                String cmail_mail_reference = jsonObject.getString("reference_cmail");
                getCalendarEntryInfo.setReference_cmail(cmail_mail_reference);

                String order_potential = jsonObject.getString("order_potential");
                getCalendarEntryInfo.order_potential = order_potential;

                String product_type = jsonObject.getString("product_type");
                getCalendarEntryInfo.product_type = product_type;

                if(jsonObject.has("updated_at")) {
                    String update = jsonObject.getString("updated_at");
                    if(update!= null) {
                        getCalendarEntryInfo.setupdatedAt(update);
                    }
                }

                //followup count parser Added By Srinath
                if (jsonObject.has("flpcount")) {
                    String flpCount = jsonObject.getString("flpcount");
                    if (flpCount != null && (!flpCount.isEmpty()) && (flpCount != "null")) {
                        JSONArray flpArray = new JSONArray(flpCount);
                        for (int j = 0; j < flpArray.length(); j++) {
                            flpCount = flpArray.getString(j);
                            //Log.d("Login parser"," FLPCount in DB"+flpCount);
                        }
                    }
                    if (flpCount != null && (!flpCount.isEmpty()) && (flpCount != "null")) {
                        getCalendarEntryInfo.setFlpCount(flpCount);
                        //Log.d("Login parser", " FLPCount Set" + flpCount);
                    } else if (flpCount == null) {
                        getCalendarEntryInfo.setFlpCount("0");
                        //Log.d("Login parser", " FLPCount Set" + 0);
                    } else if (flpCount != null && (flpCount.isEmpty())) {
                        getCalendarEntryInfo.setFlpCount("0");
                        //Log.d("Login parser", " FLPCount Set" + 0);
                    }
                }

                if (jsonObject.has("substatus1")) {
                    //Log.d("notes image url", "Number" + caller_number);
                    //Log.d("notes image url", "SubStatus1" + jsonObject.getString("substatus1"));
                    getCalendarEntryInfo.setSubStatus1(jsonObject.getString("substatus1"));
                }

                if (jsonObject.has("substatus2")) {
                    getCalendarEntryInfo.setSubStatus2(jsonObject.getString("substatus2"));
                }

                if (jsonObject.has("rnrcount")) {
                    int rnrCount = 0;
                    if (jsonObject.getString("rnrcount") != null && !(jsonObject.getString("rnrcount").isEmpty()) && !(jsonObject.getString("rnrcount").equalsIgnoreCase("null"))) {
                        rnrCount = jsonObject.getInt("rnrcount");
                    }
                    getCalendarEntryInfo.setRnrCount(rnrCount);
                }

                String url = jsonObject.getString("url");
                getCalendarEntryInfo.callrec_url = url;
                //Sales status array, need only latest one
                String salesStatusArray = jsonObject.getString("salesstatus");
                if (salesStatusArray != null) {
                    JSONArray statusArray = new JSONArray(salesStatusArray);
                    for (int j = 0; j < statusArray.length(); j++) {
                        JSONObject jsonObject1 = new JSONObject(statusArray.get(j).toString());
                        String status1 = jsonObject1.getString("status");
                        getCalendarEntryInfo.setStatus(status1);
                        String description1 = jsonObject1.getString("description");
                        getCalendarEntryInfo.setNotes(description1);
                        String orderPotential = jsonObject1.getString("order_potential");
                        getCalendarEntryInfo.order_potential = orderPotential;
                    }
                }
                if (!status.equals("deleted")) {
                    calendarEntriesArrayList.add(getCalendarEntryInfo);
                }
            }

            return calendarEntriesArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject convertGroupsInfoToJson(ArrayList<GroupsUserInfo> groupUsersArrayInfo) {
        JSONObject groupsInfo = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < groupUsersArrayInfo.size(); i++) {
            GroupsUserInfo groupsUserInfo = groupUsersArrayInfo.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("dependenttype", groupsUserInfo.dependent_type);
                jsonObject.put("email", groupsUserInfo.email);
                jsonObject.put("phone", groupsUserInfo.phone);
                jsonObject.put("request", groupsUserInfo.request);
                jsonObject.put("status", groupsUserInfo.status);

                jsonObject.put("name", groupsUserInfo.name);
                jsonObject.put("pincode", groupsUserInfo.pincode);
                jsonObject.put("country", groupsUserInfo.country);
                jsonObject.put("state", groupsUserInfo.state);
                jsonObject.put("city", groupsUserInfo.city);
                jsonObject.put("locality", groupsUserInfo.region);

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            groupsInfo.put("user_id", SmarterSMBApplication.SmartUser.getId());
            groupsInfo.put("list", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return groupsInfo;
    }

    public static ArrayList<GroupsUserInfo> getGroupUsersInfoFromJsonObj(String response) {
        ArrayList<GroupsUserInfo> groupsUserInfoArrayList = new ArrayList<>();
        try {
            JSONObject mainJson = new JSONObject(response);
            String groups = mainJson.getString("groups");
            JSONArray groups_array = new JSONArray(groups);
            if (groups_array.length() > 0) {
                for (int i = 0; i < groups_array.length(); i++) {
                    JSONObject userObj = new JSONObject(groups_array.get(i).toString());
                    String id = userObj.getString("id");
                    String email = userObj.getString("email");
                    String depedent_type = userObj.getString("dependenttype");
                    String phone = userObj.getString("phone");
                    String request = userObj.getString("request");
                    String status = userObj.getString("status");
                    String name = userObj.getString("name");
                    String dependent_id = userObj.getString("dependentid");
                    GroupsUserInfo groupsUserInfo = new GroupsUserInfo(email, depedent_type, phone, request, status);
                    groupsUserInfo.setUserId(id);
                    groupsUserInfo.setName(name);

                    groupsUserInfo.setDependent_userId(dependent_id);
                    groupsUserInfoArrayList.add(groupsUserInfo);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return groupsUserInfoArrayList;
    }

    public static JSONObject getJsonObjForAddingSecondaryGmail(GoogleAccount data) {
        JSONObject secondaryUserDetails = new JSONObject();
        int enableGmail;
        if (data.getIsEnable()) {
            enableGmail = 1;
        } else
            enableGmail = 0;
        try {
            secondaryUserDetails.put("user_id", data.getUser_id());
            secondaryUserDetails.put("email", data.getEmail());
            secondaryUserDetails.put("user_id", data.getUser_id());
            secondaryUserDetails.put("enable_calendar", data.getIsGoogleCalendarEnable());
            secondaryUserDetails.put("enabled", data.getIsEnable());
            secondaryUserDetails.put("primary_email", data.getisPrimary());
            secondaryUserDetails.put("mailboxtype", data.getTypeOfAccount());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return secondaryUserDetails;
    }

    public static JSONObject getJsonObjForUpdatingSecondaryAccount(GoogleAccount data) {
        JSONObject secondaryUserDetails = new JSONObject();
        int enableGmail;
        if (data.getIsEnable()) {
            enableGmail = 1;
        } else
            enableGmail = 0;
        try {
            secondaryUserDetails.put("email", data.getEmail());
            secondaryUserDetails.put("primary_email", data.getisPrimary());
            secondaryUserDetails.put("id", data.getId());
            secondaryUserDetails.put("user_id", data.getUser_id());
            secondaryUserDetails.put("mailboxtype", data.getTypeOfAccount());
            secondaryUserDetails.put("ver", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return secondaryUserDetails;
    }

    public static JSONObject getJSonForUpdateCalendar(GetCalendarEntryInfo getCalendarEntryInfo) {
        JSONObject jsonObject = new JSONObject();
        JSONObject startJsonObj = new JSONObject();
        JSONObject endJsonObj = new JSONObject();
        JSONObject contact_person = new JSONObject();
        SmartUser smartUser = SmarterSMBApplication.SmartUser;
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = calendar.getTimeZone();
        String timeZone = tz.getID();
        try {
            if (getCalendarEntryInfo.update_all_fields) {
                jsonObject.put("user_id", SmarterSMBApplication.SmartUser.getId());
                jsonObject.put("Id", getCalendarEntryInfo.appointment_id);
                jsonObject.put("subject", getCalendarEntryInfo.subject);
                jsonObject.put("description", getCalendarEntryInfo.notes);
                jsonObject.put("start", startJsonObj);
                jsonObject.put("end", endJsonObj);
                jsonObject.put("status", getCalendarEntryInfo.status);
                jsonObject.put("location", getCalendarEntryInfo.location);
                if (getCalendarEntryInfo.latLong != null) {
                    jsonObject.put("lat", getCalendarEntryInfo.latLong.latitude);
                    jsonObject.put("long", getCalendarEntryInfo.latLong.longitude);
                }
                jsonObject.put("alert_before", getCalendarEntryInfo.alarm_before);
                jsonObject.put("order_potential", getCalendarEntryInfo.order_potential);
                jsonObject.put("lead_source", getCalendarEntryInfo.lead_source);

                jsonObject.put("responsestatus", "accepted");
                startJsonObj.put("dateTime", getCalendarEntryInfo.event_start_date);
                startJsonObj.put("timeZone", timeZone);

                endJsonObj.put("dateTime", getCalendarEntryInfo.event_end_date);
                endJsonObj.put("timeZone", timeZone);

                jsonObject.put("user_id", smartUser.getId());
                //jsonObject.put("calendar_type", smartUser.getLogin_method());
                //TODO add the extra fields to Calendar Entry Info..
                jsonObject.put("customer_tocontact", getCalendarEntryInfo.customer_id);
                jsonObject.put("transactionallocation", "");
                jsonObject.put("external_calendar_reference", getCalendarEntryInfo.external_calendar_reference);

                contact_person.put("company", getCalendarEntryInfo.companyName);
                contact_person.put("name", getCalendarEntryInfo.caller_name);
                contact_person.put("designation", getCalendarEntryInfo.designation);
                contact_person.put("number", getCalendarEntryInfo.caller_number);
                contact_person.put("email", getCalendarEntryInfo.email_id);
                contact_person.put("address", getCalendarEntryInfo.address);
                contact_person.put("website", getCalendarEntryInfo.website);
                jsonObject.put("contact_person", contact_person);
                jsonObject.put("product_type", getCalendarEntryInfo.product_type);
            } else {
                jsonObject.put("user_id", SmarterSMBApplication.SmartUser.getId());
                jsonObject.put("Id", getCalendarEntryInfo.appointment_id);
                jsonObject.put("reference_cmail", getCalendarEntryInfo.cmailId);
                jsonObject.put("responsestatus", "completed");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject getJsonForUpdatingFewFieldsInAppointment(GetCalendarEntryInfo getCalendarEntryInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", SmarterSMBApplication.SmartUser.getId());
            jsonObject.put("Id", getCalendarEntryInfo.appointment_id);
            jsonObject.put("responsestatus", getCalendarEntryInfo.responsestatus);
            //TAT Added to jsonObject :::KSN
            jsonObject.put("turnaroundtime", getCalendarEntryInfo.tat);
            if (getCalendarEntryInfo.created_at != null && !(getCalendarEntryInfo.created_at.isEmpty())) {
                jsonObject.put("created_at", getCalendarEntryInfo.created_at);
            }

            if (getCalendarEntryInfo.rnrCount >= 0) {
                jsonObject.put("rnrcount", getCalendarEntryInfo.rnrCount);
            }

            if (!getCalendarEntryInfo.getNotesImageUrl().isEmpty()) {
                jsonObject.put("image_url", getCalendarEntryInfo.getNotesImageUrl());
            }

            if (!getCalendarEntryInfo.getNotesAudioUrl().isEmpty()) {
                jsonObject.put("audio_rec_url", getCalendarEntryInfo.getNotesAudioUrl());
            }

            if (getCalendarEntryInfo.lead_source != null && !(getCalendarEntryInfo.lead_source.isEmpty())) {
                jsonObject.put("lead_source", getCalendarEntryInfo.lead_source);
            }

            if (getCalendarEntryInfo.customer_id != null && !(getCalendarEntryInfo.customer_id.isEmpty())) {
                jsonObject.put("customer_tocontact", getCalendarEntryInfo.customer_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getJsonForWorkOrder(WorkOrderEntryInfo workOrderEntryInfo) {
        JSONObject jsonObject = new JSONObject();
        JSONObject startJson = new JSONObject();
        JSONObject endJson = new JSONObject();
        JSONObject progressJson = new JSONObject();
        JSONObject contact_person = new JSONObject();
        JSONArray attachmentsArray = new JSONArray();
        try {
            jsonObject.put("workorder_id", workOrderEntryInfo.work_order_id);
            jsonObject.put("user_id", workOrderEntryInfo.user_id);

            startJson.put("dateTime", workOrderEntryInfo.event_start_date);
            startJson.put("timeZone", workOrderEntryInfo.timeZone);

            jsonObject.put("start", startJson);

            endJson.put("dateTime", workOrderEntryInfo.event_end_date);
            endJson.put("timeZone", workOrderEntryInfo.timeZone);

            jsonObject.put("end", endJson);

            progressJson.put("status", "");
            progressJson.put("percent_complete", workOrderEntryInfo.work_completion);
            progressJson.put("additinal_notes", "");

            jsonObject.put("progress", progressJson);
            if (workOrderEntryInfo.latLong != null && workOrderEntryInfo.latLong.longitude != null && workOrderEntryInfo.latLong.latitude != null) {
                jsonObject.put("lat", workOrderEntryInfo.latLong.latitude);
                jsonObject.put("long", workOrderEntryInfo.latLong.longitude);
            }
            jsonObject.put("reminder_datetime", workOrderEntryInfo.reminder_start_date);
            jsonObject.put("advance_datetime", workOrderEntryInfo.advance_date);
            jsonObject.put("balancedue_datetime", workOrderEntryInfo.balance_due_date);
            jsonObject.put("priority", "normal");
            jsonObject.put("amount_receivable", workOrderEntryInfo.amount_receivable);
            jsonObject.put("total_price", workOrderEntryInfo.total_price);
            jsonObject.put("taxes", workOrderEntryInfo.tax);
            jsonObject.put("received_amount", workOrderEntryInfo.received_amount);
            jsonObject.put("subject", workOrderEntryInfo.subject);
            jsonObject.put("description", workOrderEntryInfo.notes);
            jsonObject.put("progress_status", workOrderEntryInfo.work_order_status);
            jsonObject.put("seteventto_teamemail", workOrderEntryInfo.assigned_to);

            contact_person.put("company", workOrderEntryInfo.company_name);
            contact_person.put("name", workOrderEntryInfo.customer_name);
            contact_person.put("designation", workOrderEntryInfo.designation);
            contact_person.put("number", workOrderEntryInfo.customer_number);
            contact_person.put("email", workOrderEntryInfo.customer_email);
            contact_person.put("address", workOrderEntryInfo.company_address);
            contact_person.put("website", workOrderEntryInfo.website);

            jsonObject.put("contact_person", contact_person);

            if (workOrderEntryInfo.attachmentsArrayList != null) {
                for (int i = 0; i < workOrderEntryInfo.attachmentsArrayList.size(); i++) {
                    JSONObject attachment_item = new JSONObject();
                    attachment_item.put("name", workOrderEntryInfo.attachmentsArrayList.get(i).attachment_name);
                    attachment_item.put("sourceUrl", workOrderEntryInfo.attachmentsArrayList.get(i).attachment_url);
                    attachment_item.put("uploadtype", "link");
                    attachmentsArray.put(attachment_item);
                }
            }

            jsonObject.put("attachments", attachmentsArray);
            if (workOrderEntryInfo.cc != null) {
                jsonObject.put("cc", workOrderEntryInfo.cc);
            }

            //TODO: Attachments is pending


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject;
    }


    public static JSONObject getJsonForUpdateWorkOrder(WorkOrderEntryInfo workOrderEntryInfo) {
        JSONObject jsonObject = new JSONObject();
        JSONObject startJson = new JSONObject();
        JSONObject endJson = new JSONObject();
        JSONObject progressJson = new JSONObject();
        JSONObject contact_person = new JSONObject();
        JSONArray attachmentsArray = new JSONArray();

        try {
            jsonObject.put("workorder_id", workOrderEntryInfo.work_order_id);
            jsonObject.put("user_id", workOrderEntryInfo.user_id);

            startJson.put("dateTime", workOrderEntryInfo.event_start_date);
            startJson.put("timeZone", workOrderEntryInfo.timeZone);

            jsonObject.put("start", startJson);

            endJson.put("dateTime", workOrderEntryInfo.event_end_date);
            endJson.put("timeZone", workOrderEntryInfo.timeZone);

            jsonObject.put("end", endJson);

            progressJson.put("progress_status", workOrderEntryInfo.work_order_status);
            progressJson.put("percent_complete", workOrderEntryInfo.work_completion);
            progressJson.put("additinal_notes", "");

            jsonObject.put("progress", progressJson);
            if (workOrderEntryInfo.latLong != null && workOrderEntryInfo.latLong.longitude != null && workOrderEntryInfo.latLong.latitude != null) {
                jsonObject.put("lat", workOrderEntryInfo.latLong.latitude);
                jsonObject.put("long", workOrderEntryInfo.latLong.longitude);
            }
            jsonObject.put("reminder_datetime", workOrderEntryInfo.reminder_start_date);
            jsonObject.put("advance_datetime", workOrderEntryInfo.advance_date);
            jsonObject.put("balancedue_datetime", workOrderEntryInfo.balance_due_date);
            jsonObject.put("priority", "normal");
            jsonObject.put("amount_receivable", workOrderEntryInfo.amount_receivable);
            jsonObject.put("total_price", workOrderEntryInfo.total_price);
            jsonObject.put("received_amount", workOrderEntryInfo.received_amount);
            jsonObject.put("subject", workOrderEntryInfo.subject);
            jsonObject.put("description", workOrderEntryInfo.notes);
            jsonObject.put("status", workOrderEntryInfo.work_order_status);
            jsonObject.put("seteventto_teamemail", workOrderEntryInfo.assigned_to);
            //Log.i("JsonParser","ID IS "+workOrderEntryInfo.id);
            jsonObject.put("id", workOrderEntryInfo.id);

            contact_person.put("company", workOrderEntryInfo.company_name);
            contact_person.put("name", workOrderEntryInfo.customer_name);
            contact_person.put("designation", workOrderEntryInfo.designation);
            contact_person.put("number", workOrderEntryInfo.customer_number);
            contact_person.put("email", workOrderEntryInfo.customer_email);
            contact_person.put("address", workOrderEntryInfo.company_address);
            contact_person.put("website", workOrderEntryInfo.website);

            jsonObject.put("contact_person", contact_person);

            if (workOrderEntryInfo.attachmentsArrayList != null) {
                for (int i = 0; i < workOrderEntryInfo.attachmentsArrayList.size(); i++) {
                    JSONObject attachment_item = new JSONObject();
                    attachment_item.put("name", workOrderEntryInfo.attachmentsArrayList.get(i).attachment_name);
                    attachment_item.put("sourceUrl", workOrderEntryInfo.attachmentsArrayList.get(i).attachment_url);
                    attachment_item.put("uploadtype", "link");
                    attachmentsArray.put(attachment_item);
                }
            }
            jsonObject.put("attachments", attachmentsArray);
            //TODO: HAve to remove this
            jsonObject.put("transactionallocation", 95);
            jsonObject.put("customer_tocontact", 296894);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject;
    }

    public static ArrayList<WorkOrderEntryInfo> getArrayListOfWorkOrderEventsFromJson(String response) {

        try {
            JSONObject mainJsonObj = new JSONObject(response);
            String jsonArraydata = mainJsonObj.getString("success");
            JSONArray jArray = new JSONArray(jsonArraydata);

            ArrayList<WorkOrderEntryInfo> workOrderEntryInfos = new ArrayList<>();
            if (jArray.length() == 0) {
                return null;
            }

            for (int i = 0; i < jArray.length(); i++) {
                WorkOrderEntryInfo workOrderEntryInfo = new WorkOrderEntryInfo();
                ArrayList<Attachments> attachmentsArrayList = new ArrayList<>();
                MyJsonObject jsonObject = new MyJsonObject(jArray.get(i).toString());
                String workOrderId = jsonObject.getString("workorderid");
                workOrderEntryInfo.work_order_id = workOrderId;
                String id = jsonObject.getString("id");
                workOrderEntryInfo.id = id;
                String start_time = jsonObject.getString("start_date");
                workOrderEntryInfo.event_start_date = start_time;
                String end_time = jsonObject.getString("end_date");
                workOrderEntryInfo.event_end_date = end_time;
                String assign_to = jsonObject.getString("seteventto_teamemail");
                workOrderEntryInfo.assigned_to = assign_to;
                String subject = jsonObject.getString("subject");
                workOrderEntryInfo.subject = subject;
                String reminder_date = jsonObject.getString("remainder_datetime");
                workOrderEntryInfo.reminder_start_date = reminder_date;
                String total_price = jsonObject.getString("total_price");
                if (total_price != null) {
                    Float totalPrice = Float.parseFloat(total_price);
                    workOrderEntryInfo.total_price = totalPrice;
                }
                String tax = jsonObject.getString("taxes");
                if (tax != null) {
                    Float tax_float = Float.parseFloat(tax);
                    workOrderEntryInfo.tax = tax_float;
                }
                String notes = jsonObject.getString("description");
                workOrderEntryInfo.notes = notes;

                String received_amount = jsonObject.getString("received_amount");
                if (received_amount != null) {
                    Float receivedAmount = Float.parseFloat(received_amount);
                    workOrderEntryInfo.received_amount = receivedAmount;
                }
                String amount_receivable = jsonObject.getString("amount_receivable");
                if (amount_receivable != null) {
                    Float amountReceivable = Float.parseFloat(amount_receivable);
                    workOrderEntryInfo.amount_receivable = amountReceivable;
                }
                String advance_date = jsonObject.getString("advance_date");
                workOrderEntryInfo.advance_date = advance_date;
                String balance_due_date = jsonObject.getString("balance_due_date");
                workOrderEntryInfo.balance_due_date = balance_due_date;
                String name = jsonObject.getString("name");
                workOrderEntryInfo.customer_name = name;
                String designation = jsonObject.getString("designation");
                workOrderEntryInfo.designation = designation;
                String number = jsonObject.getString("number");
                workOrderEntryInfo.customer_number = number;
                String email = jsonObject.getString("email");
                workOrderEntryInfo.customer_email = email;
                String address = jsonObject.getString("address");
                workOrderEntryInfo.company_address = address;
                String website = jsonObject.getString("website");
                workOrderEntryInfo.website = website;
                String company = jsonObject.getString("company");
                workOrderEntryInfo.company_name = company;
                String lat = jsonObject.getString("lat");
                String longi = jsonObject.getString("long");
                LatLong latLong = new LatLong(lat, longi);
                workOrderEntryInfo.latLong = latLong;
                String status = jsonObject.getString("progress_status");
                workOrderEntryInfo.work_order_status = status;

                String progressArray = jsonObject.getString("progress");

                JSONArray progress_array = new JSONArray(progressArray);
                MyJsonObject progressObj = new MyJsonObject(progress_array.get(progress_array.length() - 1).toString());

                //String status = progressObj.getString("status");
                // workOrderEntryInfo.work_order_status = status;
                String attachments = jsonObject.getString("attachment_list");

                JSONArray attachmentsArray = new JSONArray(attachments);
                for (int j = 0; j < attachmentsArray.length(); j++) {
                    JSONObject attachmentJson = new JSONObject(attachmentsArray.get(j).toString());
                    String url = attachmentJson.getString("filelink");
                    String filename = attachmentJson.getString("filename");
                    Attachments attachments1 = new Attachments(filename, url);
                    attachmentsArrayList.add(attachments1);
                }

                String work_completed = progressObj.getString("percent_complete");
                if (work_completed != null) {
                    int workComplete = Integer.parseInt(work_completed);
                    workOrderEntryInfo.work_completion = workComplete;
                }
                workOrderEntryInfo.attachmentsArrayList = attachmentsArrayList;

                workOrderEntryInfos.add(workOrderEntryInfo);
            }

            return workOrderEntryInfos;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WorkOrderEntryInfo getWorkOrderEventFromJson(String response) {
        try {
            JSONObject mainJsonObj = new JSONObject(response);
            String jsonArraydata = mainJsonObj.getString("success");
            JSONArray jArray = new JSONArray(jsonArraydata);

            if (jArray.length() == 0) {
                return null;
            }
            WorkOrderEntryInfo workOrderEntryInfo = new WorkOrderEntryInfo();

            for (int i = 0; i < jArray.length(); i++) {
                MyJsonObject jsonObject = new MyJsonObject(jArray.get(i).toString());
                ArrayList<Attachments> attachmentsArrayList = new ArrayList<>();
                String workOrderId = jsonObject.getString("workorderid");
                workOrderEntryInfo.work_order_id = workOrderId;
                // Log.i("JsonParser","ID IS ******"+jsonObject.getString("id"));
                String id = jsonObject.getString("id");
                workOrderEntryInfo.id = id;
                String start_time = jsonObject.getString("start_date");
                workOrderEntryInfo.event_start_date = start_time;
                String end_time = jsonObject.getString("end_date");
                workOrderEntryInfo.event_end_date = end_time;
                String assign_to = jsonObject.getString("seteventto_teamemail");
                workOrderEntryInfo.assigned_to = assign_to;
                String subject = jsonObject.getString("subject");
                workOrderEntryInfo.subject = subject;
                String notes = jsonObject.getString("description");
                workOrderEntryInfo.notes = notes;
                String status = jsonObject.getString("progress_status");
                workOrderEntryInfo.work_order_status = status;
                String reminder_date = jsonObject.getString("remainder_datetime");
                workOrderEntryInfo.reminder_start_date = reminder_date;
                String total_price = jsonObject.getString("total_price");
                Float totalPrice = Float.parseFloat(total_price);
                workOrderEntryInfo.total_price = totalPrice;
                String tax = jsonObject.getString("taxes");
                if (tax != null) {
                    Float tax_float = Float.parseFloat(tax);
                    workOrderEntryInfo.tax = tax_float;
                }
                String received_amount = jsonObject.getString("received_amount");
                Float receivedAmount = Float.parseFloat(received_amount);
                workOrderEntryInfo.received_amount = receivedAmount;
                String amount_receivable = jsonObject.getString("amount_receivable");
                Float amountReceivable = Float.parseFloat(amount_receivable);
                workOrderEntryInfo.amount_receivable = amountReceivable;
                String advance_date = jsonObject.getString("advance_date");
                workOrderEntryInfo.advance_date = advance_date;
                String balance_due_date = jsonObject.getString("balance_due_date");
                workOrderEntryInfo.balance_due_date = balance_due_date;
                String name = jsonObject.getString("name");
                workOrderEntryInfo.customer_name = name;
                String designation = jsonObject.getString("designation");
                workOrderEntryInfo.designation = designation;
                String number = jsonObject.getString("number");
                workOrderEntryInfo.customer_number = number;
                String email = jsonObject.getString("email");
                workOrderEntryInfo.customer_email = email;
                String address = jsonObject.getString("address");
                workOrderEntryInfo.company_address = address;
                String website = jsonObject.getString("website");
                workOrderEntryInfo.website = website;
                String company = jsonObject.getString("company");
                workOrderEntryInfo.company_name = company;
                String lat = jsonObject.getString("lat");
                String longi = jsonObject.getString("long");
                LatLong latLong = new LatLong(lat, longi);
                workOrderEntryInfo.latLong = latLong;
                String progressArray = jsonObject.getString("progress");

                JSONArray progress_array = new JSONArray(progressArray);
                MyJsonObject progressObj = new MyJsonObject(progress_array.get(progress_array.length() - 1).toString());
                String attachments = jsonObject.getString("attachment_list");

                JSONArray attachmentsArray = new JSONArray(attachments);
                for (int j = 0; j < attachmentsArray.length(); j++) {
                    JSONObject attachmentJson = new JSONObject(attachmentsArray.get(j).toString());
                    String url = attachmentJson.getString("filelink");
                    String filename = attachmentJson.getString("filename");
                    Attachments attachments1 = new Attachments(filename, url);
                    attachmentsArrayList.add(attachments1);
                }

                // String status = progressObj.getString("status");
                //workOrderEntryInfo.work_order_status = status;
                String work_completed = progressObj.getString("percent_complete");
                if (work_completed != null) {
                    int workComplete = Integer.parseInt(work_completed);
                    workOrderEntryInfo.work_completion = workComplete;
                }
                workOrderEntryInfo.attachmentsArrayList = attachmentsArrayList;
            }

            return workOrderEntryInfo;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GetCalendarEntryInfo getCalendarEventInfoFromJson(String response) {

        Log.d("ReuploadService","MyGCMListenerService- getCalendarEventInfoFromJson "+response);

        try {
            JSONObject mainJsonObj = new JSONObject(response);

            String jsonArraydata = mainJsonObj.getString("event");

            JSONArray jArray = new JSONArray(jsonArraydata);
            ArrayList<GetCalendarEntryInfo> calendarEntriesArrayList = new ArrayList<>();
            GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();

            if (jArray.length() == 0) {
                return null;
            }
            for (int i = 0; i < jArray.length(); i++) {
                MyJsonObject jsonObject = new MyJsonObject(jArray.get(i).toString());

                String appointmentId = jsonObject.getString("Id");
                getCalendarEntryInfo.setAppointment_id(appointmentId);

                String userId = jsonObject.getString("user_id");
                getCalendarEntryInfo.setUser_id(userId);

                String subject = jsonObject.getString("subject");
                if (subject != null)
                    getCalendarEntryInfo.setSubject(subject);
                String location = jsonObject.getString("location");
                if (location != null)
                    getCalendarEntryInfo.setLocation(location);
                String start_time = jsonObject.getString("start_date");
                getCalendarEntryInfo.setEvent_start_date(start_time);
                String end_time = jsonObject.getString("end_date");
                getCalendarEntryInfo.setEvent_end_date(end_time);

                String alarm_before = jsonObject.getString("remainder_datetime");
                getCalendarEntryInfo.alarm_before = alarm_before;

                String timeZone = jsonObject.getString("time_zone");
                getCalendarEntryInfo.setTimeZone(timeZone);
                String description = jsonObject.getString("description");
                if (description != null)
                    getCalendarEntryInfo.setNotes(description);
                String status = jsonObject.getString("status");

                if (status != null)
                    getCalendarEntryInfo.setStatus(status);
                String callerName = jsonObject.getString("name");
                if (callerName != null)
                    getCalendarEntryInfo.setCaller_name(callerName);
                String designation = jsonObject.getString("designation");
                if (designation != null)
                    getCalendarEntryInfo.setDesignation(designation);
                String caller_number = jsonObject.getString("number");
                if (caller_number != null)
                    getCalendarEntryInfo.setCaller_number(caller_number);
                String email = jsonObject.getString("email");
                if (email != null)
                    getCalendarEntryInfo.setEmail_id(email);
                String website = jsonObject.getString("website");
                if (website != null)
                    getCalendarEntryInfo.setWebsite(website);
                String company = jsonObject.getString("company");
                if (company != null)
                    getCalendarEntryInfo.setCompanyName(company);
                String notes = jsonObject.getString("notes");
                if (notes != null)
                    getCalendarEntryInfo.setNotes(notes);

                String external_calendar_reference = jsonObject.getString("external_calendar_reference");
                if (external_calendar_reference != null) {
                    getCalendarEntryInfo.setExternal_calendar_reference(external_calendar_reference);
                }
                String id = jsonObject.getString("id");
                getCalendarEntryInfo.setCustomer_id(id);

                // TODO: FOR ACCEPT / REJECT Calendar
                String responseStuatus = jsonObject.getString("responsestatus");
                //Log.d("Followup_Status", responseStuatus);
                getCalendarEntryInfo.setResponsestatus(responseStuatus);

                String appointment_id = jsonObject.getString("Id");
                getCalendarEntryInfo.setAppointment_id(appointment_id);

                String leadSource = jsonObject.getString("lead_source");
                getCalendarEntryInfo.setLead_source(leadSource);

                String cmail_mail_reference = jsonObject.getString("reference_cmail");
                getCalendarEntryInfo.setReference_cmail(cmail_mail_reference);

                String order_potential = jsonObject.getString("order_potential");
                getCalendarEntryInfo.order_potential = order_potential;

                String product_type = jsonObject.getString("product_type");
                getCalendarEntryInfo.product_type = product_type;

                String company_address = jsonObject.getString("address");
                getCalendarEntryInfo.address = company_address;

                String url = jsonObject.getString("url");
                if (url != null) {
                    //Log.i("JsonParser", "Call rec url for one event is " + url);
                }
                if (jsonObject.has("flpcount")) {
                    String flpCount = jsonObject.getString("flpcount");
                    if (flpCount != null && (!flpCount.isEmpty()) && (flpCount != "null")) {
                        JSONArray flpArray = new JSONArray(flpCount);
                        for (int j = 0; j < flpArray.length(); j++) {
                            flpCount = flpArray.getString(j);
                            //Log.d("FLPCount in DB", flpCount);
                        }
                    }
                    if (flpCount != null && (!flpCount.isEmpty()) && (flpCount != "null")) {
                        getCalendarEntryInfo.setFlpCount(flpCount);
                        //Log.d("FLPCount in DB", flpCount);
                    } else if (flpCount == null) {
                        //Log.d("FLPCount in DB", "null");
                        getCalendarEntryInfo.setFlpCount("0");
                    } else if (flpCount != null && flpCount.isEmpty()) {
                        //Log.d("FLPCount in DB", "null");
                        getCalendarEntryInfo.setFlpCount("0");
                    }
                }

                if(jsonObject.has("updated_at")) {
                    String update = jsonObject.getString("updated_at");
                    if(update!= null) {
                        getCalendarEntryInfo.setupdatedAt(update);
                    }
                }

                if (jsonObject.has("substatus1")) {
                    getCalendarEntryInfo.setSubStatus1(jsonObject.getString("substatus1"));
                }

                if (jsonObject.has("substatus2")) {
                    getCalendarEntryInfo.setSubStatus2(jsonObject.getString("substatus2"));
                }

                if (jsonObject.has("image_url")) {
                    getCalendarEntryInfo.setNotesImageUrl(jsonObject.getString("image_url"));
                }

                if (jsonObject.has("rnrcount")) {
                    int rnrCount = 0;
                    if (jsonObject.getString("rnrcount") != null && !(jsonObject.getString("rnrcount").isEmpty()) && !(jsonObject.getString("rnrcount").equalsIgnoreCase("null"))) {
                        rnrCount = jsonObject.getInt("rnrcount");
                    }
                    getCalendarEntryInfo.setRnrCount(rnrCount);
                }

                getCalendarEntryInfo.callrec_url = url;
                //Sales status array, need only latest one
                String salesStatusArray = jsonObject.getString("salesstatus");
                if (salesStatusArray != null) {
                    JSONArray statusArray = new JSONArray(salesStatusArray);
                    for (int j = 0; j < statusArray.length(); j++) {
                        JSONObject jsonObject1 = new JSONObject(statusArray.get(j).toString());
                        //TODO: check it GCM Added by Srinath.k  -- ram requirement.
                        String status1 = jsonObject1.getString("status");
                        if (jsonObject1.has("substatus1")) {
                            getCalendarEntryInfo.setSubStatus1(jsonObject.getString("substatus1"));
                        }

                        if (jsonObject1.has("substatus2")) {
                            getCalendarEntryInfo.setSubStatus2(jsonObject.getString("substatus2"));
                        }
                        getCalendarEntryInfo.setStatus(status1);
                        String description1 = jsonObject1.getString("description");
                        getCalendarEntryInfo.setNotes(description1);
                        String orderPotential = jsonObject1.getString("order_potential");
                        getCalendarEntryInfo.order_potential = orderPotential;
                    }
                }               //calendarEntriesArrayList.add(getCalendarEntryInfo);
            }

            return getCalendarEntryInfo;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJsonForSalesUpdate(GetCalendarEntryInfo getCalendarEntryInfo) {
        JSONObject jsonObject = new JSONObject();
        JSONObject contactPersonJson = new JSONObject();
        try {
            contactPersonJson.put("number", getCalendarEntryInfo.caller_number);

            if(NotificationData.emailId != null && !NotificationData.emailId.isEmpty()) {
                contactPersonJson.put("email_id", NotificationData.emailId);
            }

            jsonObject.put("user_id", getCalendarEntryInfo.user_id);
            jsonObject.put("contact_person", contactPersonJson);
            if(getCalendarEntryInfo.notes != null) {
                jsonObject.put("description", getCalendarEntryInfo.notes);
            }
            jsonObject.put("order_potential", getCalendarEntryInfo.order_potential);

            if (getCalendarEntryInfo.status != null && !getCalendarEntryInfo.status.isEmpty()) {
                jsonObject.put("status", getCalendarEntryInfo.status);
                if(getCalendarEntryInfo.status.contains("(")) {
                    getCalendarEntryInfo.status = getCalendarEntryInfo.status.substring(0, getCalendarEntryInfo.status.indexOf("("));
                }
            }

            jsonObject.put("lat", getCalendarEntryInfo.latitude);
            jsonObject.put("long", getCalendarEntryInfo.longitude);
            jsonObject.put("start_time", getCalendarEntryInfo.event_start_date);
            jsonObject.put("created_at", getCalendarEntryInfo.created_at);
            if (getCalendarEntryInfo.subStatus1 != null && !(getCalendarEntryInfo.subStatus1.isEmpty()) && !(getCalendarEntryInfo.subStatus1.equalsIgnoreCase("null"))) {
                jsonObject.put("substatus1", getCalendarEntryInfo.subStatus1);
            }
            if (getCalendarEntryInfo.subStatus2 != null && !(getCalendarEntryInfo.subStatus2 .isEmpty()) && !(getCalendarEntryInfo.subStatus2.equalsIgnoreCase("null"))) {
                jsonObject.put("substatus2", getCalendarEntryInfo.subStatus2);
            }
            if(getCalendarEntryInfo.machine_status != null) {
                jsonObject.put("machine_status", getCalendarEntryInfo.machine_status);
            }
            if(getCalendarEntryInfo.extranotes != null) {
                jsonObject.put("extranotes", getCalendarEntryInfo.extranotes);
                Log.d("ExtraNotes", "JSONParser - getCalendarEntryInfo.extranotes: " + getCalendarEntryInfo.extranotes);
            }
            if(getCalendarEntryInfo.wrapup != null) {
                jsonObject.put("wrapup", getCalendarEntryInfo.wrapup);
            }

            String leadSource = getCalendarEntryInfo.lead_source;
            if(leadSource != null && !leadSource.isEmpty()) {
                jsonObject.put("lead_source", leadSource);
            }

            String customerToContact = getCalendarEntryInfo.customer_id;
            if(customerToContact != null && !customerToContact.isEmpty()) {
                jsonObject.put("customer_tocontact", customerToContact);
            }

            String transactionId = getCalendarEntryInfo.transactionId;
            if(transactionId != null && !transactionId.isEmpty()) {
                jsonObject.put("transactionid", transactionId);
            }

            String mode = getCalendarEntryInfo.mode;
            if(mode != null && !mode.isEmpty()){
                jsonObject.put("mode", mode);
            }

            if(getCalendarEntryInfo.notesImageUrl != null && !getCalendarEntryInfo.notesImageUrl.isEmpty()) {
                jsonObject.put("ss_image_url", getCalendarEntryInfo.notesImageUrl);
            }

            jsonObject.put("appointment_id", getCalendarEntryInfo.appointment_id);
            jsonObject.put("call_end_time", SmarterSMBApplication.callEndTime);

            if(getCalendarEntryInfo.customkvs != null) {
                jsonObject.put("customkvs", getCalendarEntryInfo.customkvs);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("SalesStatusUpdate","JSONParser - getJsonForSalesUpdate "+jsonObject.toString());
        return jsonObject;
    }

    public static JSONObject convertOneViewScreenMailToJSON(OneViewScreenMail oneViewScreenMail) {
        JSONObject jsonObject = new JSONObject();
        try {
            //SmartUser smartUser = SmarterSMBApplication.SmartUser;
            jsonObject.put("user_id", SmarterSMBApplication.SmartUser.getId());
            jsonObject.put("activity_type", oneViewScreenMail.getActivity_type());
            jsonObject.put("start_time", oneViewScreenMail.getStart_time());
            jsonObject.put("created_at", oneViewScreenMail.getStart_time());
            jsonObject.put("end_time", oneViewScreenMail.getEnd_time());
            jsonObject.put("url", oneViewScreenMail.getUrl());
            jsonObject.put("message", oneViewScreenMail.getMessage());
            jsonObject.put("subject", oneViewScreenMail.getSubject());
            jsonObject.put("parent", oneViewScreenMail.getParent());
            //Log.d("Message For Missed calls", oneViewScreenMail.getMessage());
            jsonObject.put("lat", oneViewScreenMail.getLatitude());
            jsonObject.put("long", oneViewScreenMail.getLongitude());
            jsonObject.put("db_allowbookappointment", oneViewScreenMail.isDb_allowbooking());
            jsonObject.put("showcallrecordtoenduser", oneViewScreenMail.isShow_callrecording());
            jsonObject.put("from", oneViewScreenMail.getFrom());
            jsonObject.put("to", oneViewScreenMail.getTo());
            jsonObject.put("event_type", oneViewScreenMail.getEventType());

            JSONObject customerJson = new JSONObject();
            customerJson.put("name", oneViewScreenMail.getName());
            customerJson.put("number", oneViewScreenMail.getNumber());
            customerJson.put("company", oneViewScreenMail.getCompany());
            customerJson.put("designation", oneViewScreenMail.getDesignation());
            customerJson.put("address", oneViewScreenMail.getAddress());
            customerJson.put("email", oneViewScreenMail.getEmail());
            customerJson.put("website", oneViewScreenMail.getWebsite());
            jsonObject.put("contact_person", customerJson);

            if(oneViewScreenMail.getUuid() != null && !oneViewScreenMail.getUuid().isEmpty()) {
                jsonObject.put("transactionid", oneViewScreenMail.getUuid());
            }

            JSONArray attachments_jsonArray = new JSONArray();
            if (oneViewScreenMail.getAttachmentListArrayList() != null && oneViewScreenMail.getAttachmentListArrayList().size() != 0) {
                for (int i = 0; i < oneViewScreenMail.getAttachmentListArrayList().size(); i++) {
                    Attachments attachmentList = oneViewScreenMail.getAttachmentListArrayList().get(i);
                    JSONObject attachmentJson = new JSONObject();
                    attachmentJson.put("filename", attachmentList.attachment_name);
                    attachmentJson.put("url", attachmentList.attachment_url);
                    attachments_jsonArray.put(attachmentJson);
                }
                jsonObject.put("attachments_list", attachments_jsonArray);
            }

            if (oneViewScreenMail.getTranscription_url() != null && !(oneViewScreenMail.getTranscription_url().isEmpty())) {
                jsonObject.put("transcription_url", oneViewScreenMail.getTranscription_url());
                //jsonObject.put("transcription_lang", "en-IN");
            }
            if (ApplicationSettings.containsPref(AppConstants.TRANSCRIPT_LANG)) {
                String lang = ApplicationSettings.getPref(AppConstants.TRANSCRIPT_LANG, "");
                if (lang != null) {
                    jsonObject.put("transcription_lang", lang);
                }
            }
            if(oneViewScreenMail.getSource_of_lead() != null && !oneViewScreenMail.getSource_of_lead().isEmpty()) {
                jsonObject.put("lead_source", oneViewScreenMail.getSource_of_lead());
            }
            if(oneViewScreenMail.getCustomerToContact() != null && !oneViewScreenMail.getCustomerToContact().isEmpty()) {
                jsonObject.put("customer_tocontact", oneViewScreenMail.getCustomerToContact());
            }
            jsonObject.put("children", oneViewScreenMail.getChildren());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static ArrayList<HomeScreenMail> getHomeScreenMailArrayFromString(String cmailArrayString) {
        ArrayList<HomeScreenMail> homeScreenList = new ArrayList<HomeScreenMail>();
        try {
            JSONArray jArray = new JSONArray(cmailArrayString);
            if (jArray.length() == 0) {
                return null;
            }
            for (int i = 0; i < jArray.length(); i++) {
                MyJsonObject parent_jobj = new MyJsonObject(jArray.getString(i));
                HomeScreenMail homeScreenMail = new HomeScreenMail();
                String id = parent_jobj.getString("id");
                String name = parent_jobj.getString("name");
                String designation = parent_jobj.getString("designation");
                String number = parent_jobj.getString("number");
                String email = parent_jobj.getString("email");
                String address = parent_jobj.getString("address");
                String website = parent_jobj.getString("website");
                String company = parent_jobj.getString("company");
                String created_time = parent_jobj.getString("created_at");
                String customer_to_contact = parent_jobj.getString("customer_tocontact");
                String activity_type = parent_jobj.getString("activity_type");
                //TODO: has to be removed when new parameters are supported in digital brov
                String event_type = parent_jobj.getString("event_type");
                String start_time = parent_jobj.getString("start_time");
                String status = parent_jobj.getString("status");            /*Added Sales Status to Mail*/
                String duration = "";
                if (parent_jobj.has("duration")) {
                    duration = parent_jobj.getString("duration");
                    if ((duration != null) && (duration != "") && (!duration.isEmpty())) {
                        JSONObject jsonObject = new JSONObject(duration);
                        String hours = jsonObject.getString("hours");
                        String minutes = jsonObject.getString("minutes");
                        String seconds = jsonObject.getString("seconds");
                        duration = hours + ":" + minutes + ":" + seconds;
                    }
                }
                String subject = parent_jobj.getString("subject");

                homeScreenMail.setId(id);
                homeScreenMail.setName(name);
                homeScreenMail.setDesignation(designation);
                homeScreenMail.setNumber(number);
                homeScreenMail.setEmail(email);
                homeScreenMail.setAddress(address);
                homeScreenMail.setWebsite(website);
                homeScreenMail.setCompany(company);
                homeScreenMail.setStart_time(start_time);
                homeScreenMail.setCreated_time(created_time);
                homeScreenMail.setCustomer_tocontact(customer_to_contact);
                homeScreenMail.setActivity_type(activity_type);
                homeScreenMail.setEvent_type(event_type);
                homeScreenMail.setStatus(status);
                homeScreenMail.setDuration(duration);
                homeScreenMail.setSubject(subject);

                //For office 365 mails
                if (event_type != null && event_type.equals("office365")) {
                    String senderForJson = parent_jobj.getString("sender");
                    if (senderForJson != null && !senderForJson.equals("")) {
                        MyJsonObject senderJson = new MyJsonObject(senderForJson);
                        String emailAddressForJson = senderJson.getString("emailAddress");
                        MyJsonObject emailAddressJson = new MyJsonObject(emailAddressForJson);
                        String emailAddress = emailAddressJson.getString("address");
                        homeScreenMail.setEmail(emailAddress);
                        homeScreenMail.setActivity_type("office365");
                        String oSubject = parent_jobj.getString("subject");
                        homeScreenMail.setSubject(oSubject);

                        String messageJson = parent_jobj.getString("body");
                        JSONObject msgJson = new JSONObject(messageJson);
                        String oMessage = msgJson.getString("content");
                        homeScreenMail.setHtmlMessage(oMessage);
                        String contentType = msgJson.getString("contentType");
                        if (contentType.equals("text")) {
                            String omessagebody = parent_jobj.getString("bodyPreview");
                            homeScreenMail.setMessage(omessagebody);
                        } else {
                            homeScreenMail.setMessage(null);
                        }
                        String ostart_time = parent_jobj.getString("start_time");
                        homeScreenMail.setStart_time(ostart_time);
                    }
                }
                homeScreenList.add(homeScreenMail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return homeScreenList;
    }

    public static ArrayList<OneViewScreenMail> getOneViewScreenMailFromString(String response) {
        ArrayList<OneViewScreenMail> oneViewScreenMailArrayList = new ArrayList<>();
        try {
            String number = "";
            Attachments attachmentList = new Attachments();
            AttachmentList attachmentList1 = new AttachmentList();
            MyJsonObject jsonObject = new MyJsonObject(response);
            String successString = jsonObject.getString("success");
            MyJsonObject jsonObject1 = new MyJsonObject(successString);

            String contact_person = jsonObject1.getString("contact_person");
            //Log.i("JsonParser","Contact person is "+contact_person);
            if (contact_person != null && !contact_person.equals("")) {
                MyJsonObject contact_json = new MyJsonObject(contact_person);
                number = contact_json.getString("number");
            }

            String responseString = jsonObject1.getString("response");
            JSONArray responseJson = new JSONArray(responseString);
            for (int i = 0; i < responseJson.length(); i++) {
                MyJsonObject jsonObject2 = new MyJsonObject(responseJson.getString(i));
                OneViewScreenMail oneViewScreenMail = new OneViewScreenMail();
                String id = jsonObject2.getString("id");
                String created_at = jsonObject2.getString("created_at");
                String activityType = jsonObject2.getString("activity_type");
                String activity = jsonObject2.getString("activity");
                //Log.i("JsonParser","Activity is "+activity);
                MyJsonObject activityObj = new MyJsonObject(activity);
                String activity_id = activityObj.getString("id");
                String start_time = activityObj.getString("start_time");
                String end_time = activityObj.getString("end_time");
                // Log.i("JsonParser","End time from server is "+end_time);
                String subject = activityObj.getString("subject");
                String message = activityObj.getString("message");
                String password = activityObj.getString("password");
                String order_potential = activityObj.getString("order_potential");
                String url = activityObj.getString("url");
                String bitly_url = activityObj.getString("cmail_url");
                String reminder_date_time = activityObj.getString("remainder_datetime");
                String status = activityObj.getString("status");
                String description = activityObj.getString("description");
                String lead_source = activityObj.getString("lead_source");
                String calendar_type = activityObj.getString("calendar_type");
                String location = activityObj.getString("location");
                String response_status = activityObj.getString("responsestatus");
                String product_type = activityObj.getString("product_type");
                String to = activityObj.getString("to");
                String from = activityObj.getString("from");
                if (activityObj.has("attachments")) {
                    ArrayList<Attachments> arrayAttachments = new ArrayList<Attachments>();
                    ArrayList<AttachmentList> attachmentListArrayList = new ArrayList<AttachmentList>();

                    String attachments_list = activityObj.getString("attachments");
                    JSONArray jsonArray = new JSONArray(attachments_list);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        MyJsonObject list_json = new MyJsonObject(jsonArray.getString(j));
                        String attachment_name = list_json.getString("filename");
                        String attachment_file_link = list_json.getString("filelink");

                        attachmentList.setAttachment_name(attachment_name);
                        attachmentList.setAttachment_url(attachment_file_link);
                        arrayAttachments.add(attachmentList);

                        attachmentList1.setAttachment_name(attachment_name);
                        attachmentList1.setAttachment_url(attachment_file_link);
                        attachmentListArrayList.add(attachmentList1);
                    }
                    oneViewScreenMail.setAttachmentListArrayList(arrayAttachments);
                    oneViewScreenMail.setAttachmentsSerilizableList(attachmentListArrayList);
                }
                oneViewScreenMail.setId(id);
                oneViewScreenMail.setActivity_type(activityType);
                oneViewScreenMail.setCreated_time(created_at);
                oneViewScreenMail.setActivity_id(activity_id);
                oneViewScreenMail.setStart_time(start_time);
                oneViewScreenMail.setEnd_time(end_time);
                oneViewScreenMail.setSubject(subject);
                oneViewScreenMail.setMessage(message);
                oneViewScreenMail.setPassword(password);
                oneViewScreenMail.setOrder_potential(order_potential);
                oneViewScreenMail.setUrl(url);
                oneViewScreenMail.setStatus(status);
                oneViewScreenMail.setDescription(description);
                oneViewScreenMail.setSource_of_lead(lead_source);
                oneViewScreenMail.setLocation(location);
                oneViewScreenMail.setProduct_type(product_type);
                oneViewScreenMail.setResponsestatus(response_status);
                oneViewScreenMail.setTo(number);
                oneViewScreenMail.setFrom(from);

                oneViewScreenMailArrayList.add(oneViewScreenMail);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception i) {
            i.printStackTrace();
        }
        return oneViewScreenMailArrayList;
    }

    public static JSONObject getDeleteJsonForActivity(OneViewScreenMail oneViewScreenMail) {
        JSONObject jsonObject = new JSONObject();
        SmartUser smartUser = SmarterSMBApplication.SmartUser;
        try {
            jsonObject.put("user_id", smartUser.getId());
            jsonObject.put("id", oneViewScreenMail.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static OneViewScreenMail convertPostResponseToMail(String response) {
        OneViewScreenMail oneViewScreenMail = new OneViewScreenMail();
        try {
            MyJsonObject responseJson = new MyJsonObject(response);
            String success = responseJson.getString("success");
            MyJsonObject successJson = new MyJsonObject(success);
            String id = successJson.getString("id");
            String password = successJson.getString("password");

            oneViewScreenMail.setId(id);
            oneViewScreenMail.setPassword(password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return oneViewScreenMail;
    }

    public static SalesStageInfo getSalesStageArrayList(String response) {
        SalesStageInfo salesStageInfo = new SalesStageInfo();
        SmartUser smartUser = SmarterSMBApplication.SmartUser;
        ArrayList<String> appointmentStatusArrayList = new ArrayList<>();
        ArrayList<String> workOrderStatusArrayList = new ArrayList<>();
        String ameyoNumbers = "";
        try {
            MyJsonObject jsonObject = new MyJsonObject(response);
            String successString = jsonObject.getString("success");
            MyJsonObject successJson = new MyJsonObject(successString);
            String list = successJson.getString("list");
            JSONArray jsonArray = new JSONArray(list);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = new JSONObject(jsonArray.get(i).toString());
                String status = jsonObject1.getString("name");
                String statusType = jsonObject1.getString("type");
                if (statusType.equals("appointment")) {
                    appointmentStatusArrayList.add(status);
                } else if (statusType.equals("workorder")) {
                    workOrderStatusArrayList.add(status);
                }
            }
            if (successJson.has("cloudtelephony")) {
                /*String cloudtelephony = successJson.getString("cloudtelephony");
                JSONArray cloudTelephonyJsonArray = new JSONArray(cloudtelephony);
                for (int i = 0; i < cloudTelephonyJsonArray.length(); i++) {
                    MyJsonObject jsonObject1 = new MyJsonObject(cloudTelephonyJsonArray.getString(i));
                    String number = jsonObject1.getString("name");
                    Log.i("SmarterBIZ", "Number is " + number);
                    if (number != null && !number.equals("")) {
                        ameyoNumbers = ameyoNumbers + "," + number;
                        Log.i("SmarterBIZ","Ameyo numbers are "+ameyoNumbers);
                    }
                }*/
                JSONArray settingsSalesStages = successJson.getJSONArray("cloudtelephony");
                for (int i = 0; i < settingsSalesStages.length(); i++) {
                    JSONObject sObject = settingsSalesStages.getJSONObject(i);
                    if (i == 0) {
                        ameyoNumbers += sObject.getString("name");
                    } else {
                        ameyoNumbers += "," + sObject.getString("name");
                    }
                }
                //Log.i("SmarterBIZ","Ameyo numbers are "+ameyoNumbers);
                salesStageInfo.setAmeyoNumbers(ameyoNumbers);
                smartUser.setRecordOnlyCloudTelephonyNumbers(true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        salesStageInfo.setAppointmentSalesStage(appointmentStatusArrayList);
        salesStageInfo.setWorkorderSalesStage(workOrderStatusArrayList);
        return salesStageInfo;
    }

    public static ArrayList<GroupsUserInfo> getGroupsUserInfoFromJson(String response) {
        ArrayList<GroupsUserInfo> groupsUserInfos = new ArrayList<>();
        try {
            MyJsonObject jsonObject = new MyJsonObject(response);
            String successJson = jsonObject.getString("success");
            MyJsonObject jsonObject1 = new MyJsonObject(successJson);
            String groups = jsonObject1.getString("groups");
            if (groups != null) {
                JSONArray jsonArray = new JSONArray(groups);
                for (int i = 0; i < jsonArray.length(); i++) {
                    GroupsUserInfo groupsUserInfo = new GroupsUserInfo();
                    MyJsonObject jsonObject2 = new MyJsonObject(jsonArray.getString(i));
                    String name = jsonObject2.getString("name");
                    String email = jsonObject2.getString("email");
                    String phone_number = jsonObject2.getString("phone");
                    String request = jsonObject2.getString("request");
                    String role = "";
                    if (jsonObject2.has("role")) {
                        role = jsonObject2.getString("role");
                    }
                    groupsUserInfo.name = name;
                    groupsUserInfo.email = email;
                    groupsUserInfo.request = request;
                    groupsUserInfo.role = role;
                    //Log.d("JSON_Parser", "role in parsing"+groupsUserInfo.role);
                    groupsUserInfo.phone = phone_number;
                    groupsUserInfos.add(groupsUserInfo);

                    ApplicationSettings.putPref("Team:" + groupsUserInfo.email, groupsUserInfo.phone);
                    ApplicationSettings.putPref("CHATTEAM:" + jsonObject2.getString("id"), groupsUserInfo.phone);
                    Log.e("JsonParser","Details :: "+email+" :: "+name + " :: "+ " :: "+ phone_number+ " :: " +ApplicationSettings.getPref("Team:" + groupsUserInfo.email, groupsUserInfo.phone));
                }
            }
/*            String userLevel = jsonObject1.getString("user_level");
            if (userLevel != null) {
                JSONArray jsonArray = new JSONArray(userLevel);
                for (int i = 0; i < jsonArray.length(); i++) {
                    GroupsUserInfo groupsUserInfo = new GroupsUserInfo();
                    MyJsonObject jsonObject2 = new MyJsonObject(jsonArray.getString(i));
                    String name = jsonObject2.getString("name");
                    String email = jsonObject2.getString("email");
                    String phone_number = jsonObject2.getString("phone");
                    groupsUserInfo.name = name;
                    groupsUserInfo.email = email;
                    groupsUserInfo.phone = phone_number;
                    groupsUserInfos.add(groupsUserInfo);
                }
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return groupsUserInfos;
    }

    public static GroupsUserInfo getTeamMemberFromJson(String response, String email_id) {
        GroupsUserInfo groupsUserInfo = new GroupsUserInfo();
        MyJsonObject jsonObject = null;
        try {
            jsonObject = new MyJsonObject(response);
            String successJson = jsonObject.getString("success");
            MyJsonObject jsonObject1 = new MyJsonObject(successJson);
            String groups = jsonObject1.getString("groups");
            if (groups != null) {
                JSONArray jsonArray = new JSONArray(groups);
                for (int i = 0; i < jsonArray.length(); i++) {
                    MyJsonObject jsonObject2 = new MyJsonObject(jsonArray.getString(i));
                    String email = jsonObject2.getString("email");
                    if (email.equals(email_id)) {
                        String phone_number = jsonObject2.getString("phone");
                        String name = jsonObject2.getString("name");
                        String dependent_type = jsonObject2.getString("dependenttype");
                        groupsUserInfo.email = email;
                        groupsUserInfo.phone = phone_number;
                        groupsUserInfo.name = name;
                        groupsUserInfo.dependent_type = dependent_type;
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return groupsUserInfo;
    }

    //Added By Srinath
    public static UserActivities getSalesReports(String response) {

        UserActivities userActivities = new UserActivities();
        MyJsonObject jsonObject = null;
        try {
            jsonObject = new MyJsonObject(response);
              /*Sales funnel Data :::KSN*/
            String successJson = jsonObject.getString("salesfunnelcumulative");
            //Log.d("salesfunnelcumulative", successJson);
            MyJsonObject jsonObject1 = new MyJsonObject(successJson);
            String plotly = jsonObject1.getString("dashboard");
            //Log.d("salesfunnel:plotly", plotly);
            MyJsonObject jsonObject2 = new MyJsonObject(plotly);
            String team = jsonObject2.getString("org");
            //Log.d("salesfunnel:team", team);
            if (team != null) {
                ArrayList<String> salesList = new ArrayList<String>();
                ArrayList<String> valueList = new ArrayList<String>();
                JSONArray jsonArray = new JSONArray(team);
                for (int j = 0; j < jsonArray.length(); j++) {
                    /*ArrayList<String> salesList = new ArrayList<String>();
                    ArrayList<String> valueList = new ArrayList<String>();
                    MyJsonObject jsonObject3 = new MyJsonObject(jsonArray.getString(j));
                    String user = jsonObject3.getString("user");
                    if (user.equals(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null))) {
                        String list = jsonObject3.getString("list");
                        JSONArray listArray = new JSONArray(list);
                        for (int i = 0; i < listArray.length(); i++) {
                            MyJsonObject listJson = new MyJsonObject(listArray.getString(i));
                            String stage = listJson.getString("stage");
                            //Log.d("salesfunnel:stage", stage);
                            String value = listJson.getString("value");
                            //Log.d("salesfunnel:value", value);
                            salesList.add(stage);
                            valueList.add(value);
                        }
                        userActivities.setSalesStage(salesList);
                        userActivities.setSalesValue(valueList);
                    }*/

                    MyJsonObject listJson = new MyJsonObject(jsonArray.getString(j));
                    String stage = listJson.getString("stage");
                    // Log.d("salesfunnel:stage", stage);
                    String value = listJson.getString("value");
                    //Log.d("salesfunnel:value", value);
                    salesList.add(stage);
                    valueList.add(value);
                }
                userActivities.setSalesStage(salesList);
                userActivities.setSalesValue(valueList);
            }

            /*funnel cummilative*/
/*
            String funnelcumilativeJson = jsonObject.getString("salesfunnelcumulative");
            Log.d("salesfunnelcumulative", funnelcumilativeJson);
            MyJsonObject funnelObj = new MyJsonObject(funnelcumilativeJson);
            String funnelheaderjson = funnelObj.getString("headers");
            String funnelrowjson = funnelObj.getString("rows");
            Log.d("header", funnelheaderjson);
            if(funnelheaderjson != null) {
                JSONArray jsonArray = new JSONArray(funnelheaderjson);
                for(int j=0; j< jsonArray.length(); j++) {
                    ArrayList<String> headerList = new ArrayList< String>();
                    String funnelString = jsonArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(funnelString);
                    if(jsonArray1 != null) {
                        for(int x=0; x< jsonArray1.length(); x++) {
                            String headerNames = jsonArray1.getString(x);
                            headerList.add(headerNames);
                            Log.d("headerName", headerNames);
                        }
                    }
                    userActivities.setfunnelcumulativeheader(headerList);
                }
            }

            if(funnelrowjson != null) {
                JSONArray jsonArray = new JSONArray(funnelrowjson);
                for(int j=0; j< jsonArray.length(); j++) {
                    ArrayList<String> rowList = new ArrayList< String>();
                    String funnelString = jsonArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(funnelString);
                    if(jsonArray1 != null) {
                        for(int x=0; x< jsonArray1.length(); x++) {
                            String rowsNames = jsonArray1.getString(x);
                            rowList.add(rowsNames);
                            Log.d("rowsNames", rowsNames);
                        }
                    }
                    userActivities.setcumulativerowValues(rowList,j);
                }
                userActivities.setCumulativerowSize(jsonArray.length());
            }
*/

            /*Sales Stage Excel:*/
            String funnelJson = jsonObject.getString("funnel");
            //Log.d("funnelJson", funnelJson);
            MyJsonObject funneljsonObj = new MyJsonObject(funnelJson);
            String headerjson = funneljsonObj.getString("headers");
            String rowjson = funneljsonObj.getString("rows");
            //Log.d("header", headerjson);
            if (headerjson != null) {
                JSONArray jsonArray = new JSONArray(headerjson);
                for (int j = 0; j < jsonArray.length(); j++) {
                    ArrayList<String> headerList = new ArrayList<String>();
                    String funnelString = jsonArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(funnelString);
                    if (jsonArray1 != null) {
                        for (int x = 0; x < jsonArray1.length(); x++) {
                            String headerNames = jsonArray1.getString(x);
                            headerList.add(headerNames);
                            //Log.d("headerName", headerNames);
                        }
                    }
                    userActivities.setfunnelheader(headerList);
                }
            }

            if (rowjson != null) {
                JSONArray jsonArray = new JSONArray(rowjson);
                for (int j = 0; j < jsonArray.length(); j++) {
                    ArrayList<String> rowList = new ArrayList<String>();
                    String funnelString = jsonArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(funnelString);
                    if (jsonArray1 != null) {
                        for (int x = 0; x < jsonArray1.length(); x++) {
                            String rowsNames = jsonArray1.getString(x);
                            rowList.add(rowsNames);
                            //Log.d("rowsNames", rowsNames);
                        }
                    }
                    userActivities.setfunnelrowValues(rowList, j);
                }
                userActivities.setfunnelrowSize(jsonArray.length());
            }

            /*Excel Sales Funnel*/
            String salesfunnelJson = jsonObject.getString("salesfunnelcumulative");
            //Log.d("salesfunnelJson", salesfunnelJson);
            MyJsonObject salesfunneljsonObj = new MyJsonObject(salesfunnelJson);
            String funnelExcelJson = salesfunneljsonObj.getString("excel");
            MyJsonObject funnelObj = new MyJsonObject(funnelExcelJson);
            String salesFunnelheader = funnelObj.getString("headers");
            String salesFunnelrowValus = funnelObj.getString("rows");
            //Log.d("salesfunnelheader", salesFunnelheader);
            if (salesFunnelheader != null) {
                JSONArray jsonArray = new JSONArray(salesFunnelheader);
                ArrayList<String> headerList = new ArrayList<String>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    String salesfunnelString = jsonArray.getString(j);
                    headerList.add(salesfunnelString);
                }
                userActivities.setFunnelCumulativeHeader(headerList);
            }

            if (salesFunnelrowValus != null) {
                JSONArray jsonArray = new JSONArray(salesFunnelrowValus);
                for (int j = 0; j < jsonArray.length(); j++) {
                    ArrayList<String> rowList = new ArrayList<String>();
                    String funnelString = jsonArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(funnelString);
                    if (jsonArray1 != null) {
                        for (int x = 0; x < jsonArray1.length(); x++) {
                            String rowsNames = jsonArray1.getString(x);
                            rowList.add(rowsNames);
                            //  Log.d("salesFunnelrowsNames", rowsNames);
                        }
                    }
                    userActivities.setcumulativerowValues(rowList, j);
                }
                userActivities.setFunnelCumulativerowSize(jsonArray.length());
            }

            /*StatusJson*/
            String statusJson = jsonObject.getString("status");
            //Log.d("status", statusJson);
            MyJsonObject statusjsonObj = new MyJsonObject(statusJson);
            String plotlyjson = statusjsonObj.getString("plotly");
            //Log.d("status", plotlyjson);
            if (plotlyjson != null) {
                JSONArray jsonArray = new JSONArray(plotlyjson);
                ArrayList<String> statusList = new ArrayList<String>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    ArrayList<String> teamList = new ArrayList<String>();
                    ArrayList<String> valueList = new ArrayList<String>();
                    MyJsonObject statusjsonObj3 = new MyJsonObject(jsonArray.getString(j));
                    String statusName = statusjsonObj3.getString("name");
                    statusList.add(statusName);
                    String xValue = statusjsonObj3.getString("x");
                    if (xValue != null) {
                        JSONArray xValueArray = new JSONArray((xValue));
                        for (int x = 0; x < xValueArray.length(); x++) {
                            String teamNames = xValueArray.getString(x);
                            teamList.add(teamNames);
                            // Log.d("satusName", teamNames);
                        }
                    }

                    String yValue = statusjsonObj3.getString("y");
                    if (yValue != null) {
                        JSONArray yValueArray = new JSONArray(yValue);
                        for (int y = 0; y < yValueArray.length(); y++) {

                            String statusValue = yValueArray.getString(y);
                            valueList.add(statusValue);
                            //Log.d("satusName", statusValue);
                        }
                    }
                    userActivities.setTeamlist(teamList, j);
                    userActivities.setStatusValue(valueList, j);
                }
                userActivities.setStatusName(statusList);
            }


              /*salesGraph ::Excel*/
            String excelStatusjson = jsonObject.getString("status");
            //Log.d("Excelstatus", excelStatusjson);
            MyJsonObject statusExcelObjct = new MyJsonObject(excelStatusjson);
            String excelStatus = statusExcelObjct.getString("excel");
            MyJsonObject statusJsonObj = new MyJsonObject(excelStatus);
            String statusheaderjson = statusJsonObj.getString("headers");
            String statusrowjson = statusJsonObj.getString("rows");
            // Log.d("statusheaderjson", statusheaderjson);
            if (statusheaderjson != null) {
                JSONArray jsonArray = new JSONArray(statusheaderjson);
                ArrayList<String> headerList = new ArrayList<String>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    String funnelString = jsonArray.getString(j);
                    headerList.add(funnelString);
                }
                userActivities.setStatusExcelheader(headerList);
            }

            if (statusrowjson != null) {
                JSONArray jsonArray = new JSONArray(statusrowjson);
                for (int j = 0; j < jsonArray.length(); j++) {
                    ArrayList<String> rowList = new ArrayList<String>();
                    String statusString = jsonArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(statusString);
                    if (jsonArray1 != null) {
                        for (int x = 0; x < jsonArray1.length(); x++) {
                            String rowsNames = jsonArray1.getString(x);
                            rowList.add(rowsNames);
                            //Log.d("rowsNames", rowsNames);
                        }
                    }
                    userActivities.setStatusExcelRowValues(rowList, j);
                }
                userActivities.setExcelStatusRowSize(jsonArray.length());
            }

          /*  if(statusrowjson != null) {
                JSONArray jsonArray = new JSONArray(statusrowjson);
                for(int j=0; j< jsonArray.length(); j++) {
                    ArrayList<String> rowList = new ArrayList< String>();
                    String funnelString = jsonArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(funnelString);
                    if(jsonArray1 != null) {
                        for(int x=0; x< jsonArray1.length(); x++) {
                            String rowsNames = jsonArray1.getString(x);
                            rowList.add(rowsNames);
                            Log.d("rowsNames", rowsNames);
                        }
                    }
                    userActivities.setstatusrowValues(rowList,j);
                }
                userActivities.setStatusRowSize(jsonArray.length());
            }*/

        } catch (Exception e) {
            //Log.d("Exception", e.toString());
        }
        return userActivities;
    }

    public static UserActivities getActivitiesReports(String response) {
        UserActivities userActivities = new UserActivities();
        MyJsonObject jsonObject = null;
        String count = "";
        try {
            jsonObject = new MyJsonObject(response);
             /*Activities Response*/
            String successJson = jsonObject.getString("activities");
            MyJsonObject jsonObject1 = new MyJsonObject(successJson);
            String plotly = jsonObject1.getString("plotly");
            if (plotly != null) {
                JSONArray jsonArray = new JSONArray(plotly);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ArrayList<String> activityName = new ArrayList<String>();
                    ArrayList<String> activityCount = new ArrayList<String>();
                    ArrayList<String> activityholder = new ArrayList<String>();

                    MyJsonObject jsonObject2 = new MyJsonObject(jsonArray.getString(i));
                    String name = jsonObject2.getString("name");
                    // Log.d("Activiesity", name);
                    activityName.add(name);
                    String yaxis = jsonObject2.getString("y");
                    String xaxis = jsonObject2.getString("x");
                    JSONArray xaxisarray = new JSONArray(xaxis);
                    for (int x = 0; x < xaxisarray.length(); x++) {
                        String value = xaxisarray.getString(x);
                        activityholder.add(value);
                        //Log.d("ActiviesityValue", value);
                    }
                    JSONArray yaxisarray = new JSONArray(yaxis);
                    for (int j = 0; j < yaxisarray.length(); j++) {
                        String value = yaxisarray.getString(j);
                        //Log.d("ActiviesityValue", value);
                        activityCount.add(value);
                    }
                    userActivities.setActivityName(activityName, i);
                    userActivities.setActivityValue(activityCount, i);
                    userActivities.setActivityxaxis(activityholder, i);
                }
            }

             /*unattendedmissedcalls :::KSN*/
            String missedcallsJson = jsonObject.getString("unattendedmissedcalls");
            // Log.d("appointmentJson", missedcallsJson);
            MyJsonObject calljsonObj = new MyJsonObject(missedcallsJson);
            String callplotlyjson = calljsonObj.getString("plotly");
            //Log.d("missedcallsJson", callplotlyjson);
            if (callplotlyjson != null) {
                JSONArray jsonArray = new JSONArray(callplotlyjson);
                ArrayList<String> callList = new ArrayList<String>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    ArrayList<String> teamList = new ArrayList<String>();
                    ArrayList<String> valueList = new ArrayList<String>();
                    MyJsonObject statusjsonObj3 = new MyJsonObject(jsonArray.getString(j));
                    String callName = statusjsonObj3.getString("name");
                    callList.add(callName);
                    String xValue = statusjsonObj3.getString("x");
                    if (xValue != null) {
                        JSONArray xValueArray = new JSONArray((xValue));
                        for (int x = 0; x < xValueArray.length(); x++) {
                            String teamNames = xValueArray.getString(x);
                            teamList.add(teamNames);
                            //Log.d("appointmentJson", teamNames);
                        }
                    }

                    String yValue = statusjsonObj3.getString("y");
                    if (yValue != null) {
                        JSONArray yValueArray = new JSONArray(yValue);
                        for (int y = 0; y < yValueArray.length(); y++) {
                            String statusValue = yValueArray.getString(y);
                            valueList.add(statusValue);
                            // Log.d("appointmentJson", statusValue);
                        }
                    }
                    userActivities.setcallTeamList(teamList, j);
                    userActivities.setcallValue(valueList, j);
                }
                userActivities.setcallName(callList);
            }
            /*Excel for missed Calls Data*/
            String callexceljson = calljsonObj.getString("excel");
            //Log.d("missedcallsJson", callplotlyjson);
            MyJsonObject rowjson = new MyJsonObject(callexceljson);
            String headerJson = rowjson.getString("headers");
            String rows = rowjson.getString("rows");
            if (rows != null) {
                JSONArray rowsArray = new JSONArray((rows));
                for (int j = 0; j < rowsArray.length(); j++) {
                    ArrayList<String> rowList = new ArrayList<String>();
                    String funnelString = rowsArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(funnelString);
                    if (jsonArray1 != null) {
                        for (int x = 0; x < jsonArray1.length(); x++) {
                            String rowsNames = jsonArray1.getString(x);
                            rowList.add(rowsNames);
                            //Log.d("rowsNames", rowsNames);
                        }
                    }
                    userActivities.setexcelCallAnalysis(rowList, j);
                }
                userActivities.setcallRowSize(rowsArray.length());
            }

            //Log.d("header", headerJson);
            if (headerJson != null) {
                JSONArray jsonArray = new JSONArray(headerJson);
                ArrayList<String> headerList = new ArrayList<String>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    String callHeaderString = jsonArray.getString(j);
                    headerList.add(callHeaderString);
                }
                userActivities.setexcelCallHeader(headerList);
            }



            /*AppointmentsJson :::KSN*/
            String appointmentJson = jsonObject.getString("actedupon");
            //Log.d("appointmentJson", appointmentJson);
            MyJsonObject statusjsonObj = new MyJsonObject(appointmentJson);
            String plotlyjson = statusjsonObj.getString("plotly");
            // Log.d("appointmentJson", plotlyjson);
            if (plotlyjson != null) {
                JSONArray jsonArray = new JSONArray(plotlyjson);
                ArrayList<String> statusList = new ArrayList<String>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    ArrayList<String> teamList = new ArrayList<String>();
                    ArrayList<String> valueList = new ArrayList<String>();
                    MyJsonObject statusjsonObj3 = new MyJsonObject(jsonArray.getString(j));
                    String statusName = statusjsonObj3.getString("name");
                    statusList.add(statusName);
                    String xValue = statusjsonObj3.getString("x");
                    if (xValue != null) {
                        JSONArray xValueArray = new JSONArray((xValue));
                        for (int x = 0; x < xValueArray.length(); x++) {
                            String teamNames = xValueArray.getString(x);
                            teamList.add(teamNames);
                            // Log.d("appointmentJson", teamNames);
                        }
                    }

                    String yValue = statusjsonObj3.getString("y");
                    if (yValue != null) {
                        JSONArray yValueArray = new JSONArray(yValue);
                        for (int y = 0; y < yValueArray.length(); y++) {
                            String statusValue = yValueArray.getString(y);
                            valueList.add(statusValue);
                            //  Log.d("appointmentJson", statusValue);
                        }
                    }
                    userActivities.setApntmentTeamList(teamList, j);
                    userActivities.setAppointmentValue(valueList, j);
                }
                userActivities.setAppontmentName(statusList);
            }

            /*Activities excel*/
            String activitiesJson = jsonObject.getString("activities");
            //Log.d("salesfunnelcumulative", activitiesJson);
            MyJsonObject activitylObj = new MyJsonObject(activitiesJson);
            String exceljson = activitylObj.getString("excel");
            MyJsonObject excelObj = new MyJsonObject(exceljson);
            String activitiesheaderjson = excelObj.getString("headers");
            String activitiesrowjson = excelObj.getString("rows");
            // Log.d("header", activitiesheaderjson);
            if (activitiesheaderjson != null) {
                JSONArray jsonArray = new JSONArray(activitiesheaderjson);
                ArrayList<String> headerList = new ArrayList<String>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    String funnelString = jsonArray.getString(j);
                    headerList.add(funnelString);
                }
                userActivities.setactivitiesheader(headerList);
            }

            if (activitiesrowjson != null) {
                JSONArray jsonArray = new JSONArray(activitiesrowjson);
                for (int j = 0; j < jsonArray.length(); j++) {
                    ArrayList<String> rowList = new ArrayList<String>();
                    String funnelString = jsonArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(funnelString);
                    if (jsonArray1 != null) {
                        for (int x = 0; x < jsonArray1.length(); x++) {
                            String rowsNames = jsonArray1.getString(x);
                            rowList.add(rowsNames);
                            //Log.d("rowsNames", rowsNames);
                        }
                    }
                    userActivities.setactivityrowValues(rowList, j);
                }
                userActivities.setactivityowSize(jsonArray.length());
            }


        } catch (JSONException e) {
            //Log.d("Exception", e.toString());
        }
        return userActivities;
    }

    public static UserActivities getAppointmentReports(String response) {
        UserActivities userActivities = new UserActivities();
        MyJsonObject jsonObject = null;
        String count = "";
        try {
             /*Excel for Appointments Calls Data*/
            jsonObject = new MyJsonObject(response);
            String responseJson = jsonObject.getString("appointments");
            MyJsonObject appointmentObj = new MyJsonObject(responseJson);
            String appexceljson = appointmentObj.getString("excel");
            //Log.d("missedcallsJson", appexceljson);
            MyJsonObject rowjson = new MyJsonObject(appexceljson);
            String headerJson = rowjson.getString("headers");
            String rows = rowjson.getString("rows");
            if (rows != null) {
                JSONArray rowsArray = new JSONArray((rows));
                for (int j = 0; j < rowsArray.length(); j++) {
                    ArrayList<String> rowList = new ArrayList<String>();
                    String funnelString = rowsArray.getString(j);
                    JSONArray jsonArray1 = new JSONArray(funnelString);
                    if (jsonArray1 != null) {
                        for (int x = 0; x < jsonArray1.length(); x++) {
                            String rowsNames = jsonArray1.getString(x);
                            rowList.add(rowsNames);
                            // Log.d("rowsNames", rowsNames);
                        }
                    }
                    userActivities.setAppointmentRowValue(rowList, j);
                }
                userActivities.setAppointmentRowSize(rowsArray.length());
            }

            //Log.d("header", headerJson);
            if (headerJson != null) {
                JSONArray jsonArray = new JSONArray(headerJson);
                ArrayList<String> headerList = new ArrayList<String>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    String callHeaderString = jsonArray.getString(j);
                    headerList.add(callHeaderString);
                }
                userActivities.setAppointmentExcelHeader(headerList);
            }
            //TAT Calculation

            String dashboardjson = appointmentObj.getString("dashboard");
            MyJsonObject tatJson = new MyJsonObject(dashboardjson);
            String tat = tatJson.getString("TAT");
            userActivities.setTat(tat);

            //Appointments Tales
            String tileresponse = jsonObject.getString("appointmenttile");
            MyJsonObject tileJson = new MyJsonObject(tileresponse);
            String companyLevel = tileJson.getString("companylevel");
            MyJsonObject apntmentValues = new MyJsonObject(companyLevel);
            String completed = apntmentValues.getString("completed");
            String upcoming = apntmentValues.getString("upcoming");
            String pending = apntmentValues.getString("pending");

            userActivities.setUpcoming(upcoming);
            userActivities.setComleted(completed);
            userActivities.setPending(pending);

        } catch (JSONException exception) {
            //Log.d("JsonParser", exception.toString());
        }
        return userActivities;
    }

    //parse Json TO get AccessToken
    public static void getAccessToken(String response) {
        MyJsonObject jsonObject = null;
        try {
            jsonObject = new MyJsonObject(response);
            //Log.d("TokenResponse", response);
            String accessToken = jsonObject.getString("access_token");
            String tokenValidity = jsonObject.getString("expires_on");
            String refreshToken = jsonObject.getString("refresh_token");
            ApplicationSettings.putPref("accessToken", accessToken);
            Long tokenValidrange = Long.parseLong(tokenValidity);
            ApplicationSettings.putPref("TokenValidity", tokenValidrange);
            ApplicationSettings.putPref("oAuthRefreshToken", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //RefreshToken
    public static void getRefreshToken(String response) {
        MyJsonObject jsonObject = null;
        try {
            jsonObject = new MyJsonObject(response);
            String refreshToken = jsonObject.getString("access_token");
            String refreshTokenValidity = jsonObject.getString("expires_on");
            Long refreshTokenRange = Long.parseLong(refreshTokenValidity);
            ApplicationSettings.putPref("accessToken", refreshToken);
            ApplicationSettings.putPref("refreshTokenValidity", refreshTokenRange);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static UserActivities getTalkTimeReports(String response, String userMail) {
        UserActivities userActivities = new UserActivities();
        MyJsonObject jsonObject = null;
        String count = "";
        try {
             /*Excel for Appointments Calls Data*/
            jsonObject = new MyJsonObject(response);
            String responseJson = jsonObject.getString("talktime");
            MyJsonObject appointmentObj = new MyJsonObject(responseJson);
            if (appointmentObj.has(userMail) && (appointmentObj.length() != 0)) {
                String talktime = appointmentObj.getString(userMail);
                MyJsonObject talkTimejson = new MyJsonObject(talktime);
                int totalTalkTime = talkTimejson.getInt("totaltime");
                int totatcalls = talkTimejson.getInt("totalcalls");
                userActivities.setTotalTalkTime(totalTalkTime);
                userActivities.setTotalCalls(totatcalls);
            } else {
                userActivities.setTotalCalls(0);
                userActivities.setTotalTalkTime(0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userActivities;
    }
    /*//OYO DASHBOARD INFO

    public static ArrayList<HashMap<String, String>> getOyoDashboardReports(String response) {
        MyJsonObject jsonObject = null;
        String success = " ";
        String user_email = "";

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            user_email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }

        boolean userUploaded = false;
        ArrayList<HashMap<String, String>> agentList = new ArrayList<HashMap<String, String>>();

        try {

            JSONObject jobj = new JSONObject(response);
            if(jobj.has("data_summary")) {
                String status = jobj.getString("data_summary");
                if (status != null && status.equalsIgnoreCase("data_summary")) {

                }
            }
            jsonObject = new MyJsonObject(response);
            Log.d("OYO DAsjsonObject:---",jsonObject.toString());
            String responseJson = jsonObject.getString("data_summary");
            MyJsonObject dashboardObj = new MyJsonObject(responseJson);
            String selfString = dashboardObj.getString("17188");
            MyJsonObject selfJsonObj = new MyJsonObject(selfString);
            String data_upload = selfJsonObj.getString("selfJsonObj");
            String data_completed = selfJsonObj.getString("data_completed");
            String total_dialled = selfJsonObj.getString("total_dialled");
            String total_contacted = selfJsonObj.getString("total_contacted");
            String unique_dialled = selfJsonObj.getString("unique_dialled");
            String unique_contacted = selfJsonObj.getString("unique_contacted");

            JSONObject oyo_dashboard_info = new JSONObject();
            try {
                oyo_dashboard_info.put("data_upload", data_upload);
                oyo_dashboard_info.put("data_completed", data_completed);
                oyo_dashboard_info.put("total_dialled", total_dialled);
                oyo_dashboard_info.put("total_contacted", total_contacted);
                oyo_dashboard_info.put("unique_dialled", unique_dialled);
                oyo_dashboard_info.put("unique_contacted", unique_contacted);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(oyo_dashboard_info != null) {
                String oyoDashInfo = oyo_dashboard_info.toString();
                ApplicationSettings.putPref("OYO_DASHBOARD_INFO", oyoDashInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return agentList;
    }
*/


    public static String getOyoDashInfo(String response, String userId) {
        FollowupsReports followups = new FollowupsReports();
        MyJsonObject jsonObject = null;
        String success = " ";
        try {
            jsonObject = new MyJsonObject(response);
            Log.d("jsonObject:---",jsonObject.toString());
            String data_summaryJson = jsonObject.getString("data_obj");
            MyJsonObject myDataObjJsonObj = new MyJsonObject(data_summaryJson);
            String myJson = myDataObjJsonObj.getString(userId);
            MyJsonObject data_summaryJsonObj = new MyJsonObject(myJson);
            Log.d("data_summaryJsonObj:--",data_summaryJsonObj.toString());

            //CALLS CONNECTED

            String total_contacted = data_summaryJsonObj.getString("total_contacted");
            String contacted_percent = data_summaryJsonObj.getString("contacted_percent");
            String unique_contacted = data_summaryJsonObj.getString("unique_contacted");


            //Login Time
            String login_time = data_summaryJsonObj.getString("login_time");
            String avg_call_time = data_summaryJsonObj.getString("avg_call_time");
            String avg_handling_time = data_summaryJsonObj.getString("avg_handling_time");

            //UEARNED
            String total_uearned = data_summaryJsonObj.getString("total_uearned");
            String bonus = data_summaryJsonObj.getString("bonus");
            String paid = data_summaryJsonObj.getString("paid");


            //Booked Room
            String nights = data_summaryJsonObj.getString("nights");
            String used_room_nights = data_summaryJsonObj.getString("used_room_nights");

            //MEALS PLAN
            String meals = data_summaryJsonObj.getString("meals");
            String meals_plan_used = data_summaryJsonObj.getString("meals_plan_used");

            //LAST HOUR

            String lhData_objJson = jsonObject.getString("LHdata_obj");
            MyJsonObject mylhData_objJsonObj = new MyJsonObject(lhData_objJson);
            String myLhDataJson = mylhData_objJsonObj.getString(userId);
            MyJsonObject myLhDataJson_summaryJsonObj = new MyJsonObject(myLhDataJson);
            Log.d("data_summaryJsonObj:--",data_summaryJsonObj.toString());

            //LAST HOUR BOOKED ROOM
            String lastHourMeals = myLhDataJson_summaryJsonObj.getString("meals");
            String lastHourBookedRoom = myLhDataJson_summaryJsonObj.getString("nights");




            JSONObject oyo_dashboard_info = new JSONObject();
            try {
                oyo_dashboard_info.put("total_contacted", total_contacted);
                oyo_dashboard_info.put("contacted_percent", contacted_percent);
                oyo_dashboard_info.put("unique_contacted", unique_contacted);

                oyo_dashboard_info.put("login_time", login_time);
                oyo_dashboard_info.put("avg_call_time", avg_call_time);
                oyo_dashboard_info.put("avg_handling_time", avg_handling_time);

                oyo_dashboard_info.put("total_uearned", total_uearned);
                oyo_dashboard_info.put("bonus", bonus);

                oyo_dashboard_info.put("meals", meals);
                oyo_dashboard_info.put("nights", nights);
                oyo_dashboard_info.put("paid", paid);


                oyo_dashboard_info.put("nights", nights);
                oyo_dashboard_info.put("used_room_nights", used_room_nights);
                oyo_dashboard_info.put("lastHourBookedRoom", lastHourBookedRoom);

                oyo_dashboard_info.put("meals", meals);
                oyo_dashboard_info.put("meals_plan_used", meals_plan_used);
                oyo_dashboard_info.put("lastHourMeals", lastHourMeals);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(oyo_dashboard_info != null) {
                String oyoDashInfo = oyo_dashboard_info.toString();
                ApplicationSettings.putPref("OYO_DASHBOARD_INFO", oyoDashInfo);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return success;
    }



    public static String getFollwupsReports(String response) {
        FollowupsReports followups = new FollowupsReports();
        MyJsonObject jsonObject = null;
        String success = " ";
        try {
            jsonObject = new MyJsonObject(response);
            String responseJson = jsonObject.getString("followupdashboard");
            MyJsonObject appointmentObj = new MyJsonObject(responseJson);
            String headerJson = appointmentObj.getString("headers");
            String rowsJson = appointmentObj.getString("rows");
            if (rowsJson != null) {
                JSONArray rowsArray = new JSONArray(rowsJson);
                String total = "";
                ArrayList<String> headerList = new ArrayList<String>();
                for (int i = 0; i < rowsArray.length(); i++) {
                    total = rowsArray.getString(i);
                    headerList.add(total);
                }
                /*String headerString = rowsArray.getString(0);
                MyJsonObject headerObj = new MyJsonObject(headerString);
                String completed = headerObj.getString("Called, updated");
                String updateState = headerObj.getString("Called, not updated");
                String overdue = headerObj.getString("Not called at all");*/
                //Log.d("Followups: completed", headerJson);
                //Log.d("Followups: upcoming", rowsJson);
                //Log.d("Followups: overdue", total);
                FollowupsReports.setOverDueList(headerList);
                followups.setHeaders(headerJson);
                /*followups.setUpdateStatus(rowsJson);
                followups.setOverdue(total);*/
                success = "Success";
                return "Success";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static int getApplicationVersion(String response) {
        MyJsonObject jsonObject = null;
        int versioncode = 0;
        try {
            if (response != null) {
                jsonObject = new MyJsonObject(response);
                versioncode = jsonObject.getInt("version_code");
                return versioncode;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return versioncode;
    }

    public static String getRemoteDialerResponse(String response) {
        MyJsonObject jsonObject = null;
        String resultmessage = "";
        try {
            if (response != null) {
                jsonObject = new MyJsonObject(response);
                resultmessage = jsonObject.getString("success");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultmessage;
    }

    public static String getRequestForDataResponse(String response) {
        MyJsonObject jsonObject = null;
        String resultmessage = "";
        try {
            if (response != null) {
                jsonObject = new MyJsonObject(response);
                resultmessage = jsonObject.getString("success");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultmessage;
    }

    public static String getUearnTotalAgentsAndEarningsResponse(String response) {
        MyJsonObject jsonObject = null;
        String resultmessage = "";
        try {
            if (response != null) {
                jsonObject = new MyJsonObject(response);
                resultmessage = jsonObject.getString("success");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultmessage;
    }

    public static String getServiceUserAgreementResponse(String response) {
        MyJsonObject jsonObject = null;
        String resultmessage = "";
        try {
            if (response != null) {
                jsonObject = new MyJsonObject(response);
                resultmessage = jsonObject.getString("success");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultmessage;
    }

    public static String getQuestionsResponse(String response) {
        MyJsonObject jsonObject = null;
        String resultmessage = "";
        try {
            if (response != null) {
                jsonObject = new MyJsonObject(response);
                resultmessage = jsonObject.getString("success");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultmessage;
    }



    public static ArrayList<HashMap<String, String>> parseQdeDashboardRespone(String respone) {
        MyJsonObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(respone);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<>();
                    String agentData = jsonArray.getString(i);
                    JSONObject obj = new JSONObject(agentData);
                    if (obj.has("user")) {
                        String email = obj.getString("user");
                        map.put("userEmail", email);
                    }
                    if (obj.has("dial_count")) {
                        String dial_count = obj.getString("dial_count");
                        map.put("dialCount", dial_count);
                    }
                    if (obj.has("pan_count")) {
                        String pan_count = obj.getString("pan_count");
                        map.put("panCount", pan_count);
                    }
                    if (obj.has("qde_count")) {
                        String qde_count = obj.getString("qde_count");
                        map.put("qdeCount", qde_count);
                    }
                    if (obj.has("pan_approve")) {
                        String pan_approve = obj.getString("pan_approve");
                        map.put("panApprove", pan_approve);
                    }
                    if (obj.has("pan_reject")) {
                        String pan_reject = obj.getString("pan_reject");
                        map.put("panReject", pan_reject);
                    }
                    if (obj.has("qde_approve")) {
                        String qde_approve = obj.getString("qde_approve");
                        map.put("qdeApprove", qde_approve);
                    }
                    if (obj.has("qde_reject")) {
                        String qde_reject = obj.getString("qde_reject");
                        map.put("qdeReject", qde_reject);
                    }
                    //Log.d("Qderesponse",map.toString());
                    list.add(map);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    //OYO DASHBOARD INFO

    public static ArrayList<HashMap<String, String>> getOyoDashboardReports(String response) {
        MyJsonObject jsonObject = null;
        String success = " ";
        String user_email = "";

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            user_email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }

        boolean userUploaded = false;
        ArrayList<HashMap<String, String>> agentList = new ArrayList<HashMap<String, String>>();

        try {

            JSONObject jobj = new JSONObject(response);
            if(jobj.has("data_summary")) {
                String status = jobj.getString("data_summary");
                if (status != null && status.equalsIgnoreCase("data_summary")) {

                }
            }
            jsonObject = new MyJsonObject(response);
            Log.d("OYO DAsjsonObject:---",jsonObject.toString());
            String responseJson = jsonObject.getString("data_summary");
            MyJsonObject dashboardObj = new MyJsonObject(responseJson);
            String selfString = dashboardObj.getString("17188");
            MyJsonObject selfJsonObj = new MyJsonObject(selfString);
            String data_upload = selfJsonObj.getString("selfJsonObj");
            String data_completed = selfJsonObj.getString("data_completed");
            String total_dialled = selfJsonObj.getString("total_dialled");
            String total_contacted = selfJsonObj.getString("total_contacted");
            String unique_dialled = selfJsonObj.getString("unique_dialled");
            String unique_contacted = selfJsonObj.getString("unique_contacted");

            JSONObject oyo_dashboard_info = new JSONObject();
            try {
                oyo_dashboard_info.put("data_upload", data_upload);
                oyo_dashboard_info.put("data_completed", data_completed);
                oyo_dashboard_info.put("total_dialled", total_dialled);
                oyo_dashboard_info.put("total_contacted", total_contacted);
                oyo_dashboard_info.put("unique_dialled", unique_dialled);
                oyo_dashboard_info.put("unique_contacted", unique_contacted);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(oyo_dashboard_info != null) {
                String oyoDashInfo = oyo_dashboard_info.toString();
                ApplicationSettings.putPref("OYO_DASHBOARD_INFO", oyoDashInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return agentList;
    }

    public static ArrayList<HashMap<String, String>> getLatestDashboardReports(String response) {
        MyJsonObject jsonObject = null;
        String success = " ";
        String user_email = "";

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            user_email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }

        boolean userUploaded = false;
        ArrayList<HashMap<String, String>> agentList = new ArrayList<HashMap<String, String>>();

        try {
            jsonObject = new MyJsonObject(response);
            String responseJson = jsonObject.getString("newappdashboard");
            MyJsonObject dashboardObj = new MyJsonObject(responseJson);
            HashMap<String, String> map1 = new HashMap<>();
            String agentEmail = dashboardObj.getString("email");
            map1.put("agent", dashboardObj.getString("email"));
            String selfString = dashboardObj.getString("self");
            MyJsonObject selfJsonObj = new MyJsonObject(selfString);

            map1.put("called", selfJsonObj.getString("Called once"));
            map1.put("notCalled", selfJsonObj.getString("Not called at all")); //Overdue
            map1.put("calledUpcoming", selfJsonObj.getString("Call In Future")); //Followup
            map1.put("followup", selfJsonObj.getString("Followed up"));
            map1.put("notFollowedup", selfJsonObj.getString("Not Followedup"));
            map1.put("followedUpcoming", selfJsonObj.getString("Upcoming Followups"));
            map1.put("leadsInProgress", selfJsonObj.getString("Leads in progress"));
            map1.put("leadsNotInterested", selfJsonObj.getString("Leads not interested"));

            /*if (!agentEmail.equalsIgnoreCase(user_email)) {
                agentList.add(map1);
            } else {
                if (!userUploaded) {
                    if (agentEmail.equalsIgnoreCase(user_email)) {
                        agentList.add(map1);
                        userUploaded = true;
                    }
                }
            }*/

            if (dashboardObj.has("team")) {
                String teamString = dashboardObj.getString("team");
                MyJsonObject teamJson = new MyJsonObject(teamString);
                ApplicationSettings.putPref("called", teamJson.getString("Called once"));
                ApplicationSettings.putPref("notCalled", teamJson.getString("Not called at all"));
                ApplicationSettings.putPref("callUpcoming", teamJson.getString("Call In Future"));
                ApplicationSettings.putPref("followup", teamJson.getString("Followed up"));
                ApplicationSettings.putPref("notFollowedup", teamJson.getString("Not Followedup"));
                ApplicationSettings.putPref("followupcoming", teamJson.getString("Upcoming Followups"));
                ApplicationSettings.putPref("leadsInProgress", teamJson.getString("Leads in progress"));
                ApplicationSettings.putPref("leadsNotInterested", teamJson.getString("Leads not interested"));
                HashMap<String, String> map = new HashMap<>();
                map.put("agent", "team");
                map.put("called", teamJson.getString("Called once"));
                map.put("notCalled", teamJson.getString("Not called at all")); //Overdue
                map.put("calledUpcoming", teamJson.getString("Call In Future")); //Followup
                map.put("followup", teamJson.getString("Followed up"));
                map.put("notFollowedup", teamJson.getString("Not Followedup"));
                map.put("followedUpcoming", teamJson.getString("Upcoming Followups"));
                map.put("leadsInProgress", teamJson.getString("Leads in progress"));
                map.put("leadsNotInterested", teamJson.getString("Leads not interested"));
                agentList.add(map);
            } else {
                ApplicationSettings.putPref("called", selfJsonObj.getString("Called once"));
                ApplicationSettings.putPref("notCalled", selfJsonObj.getString("Not called at all"));
                ApplicationSettings.putPref("callUpcoming", selfJsonObj.getString("Call In Future"));
                ApplicationSettings.putPref("followup", selfJsonObj.getString("Followed up"));
                ApplicationSettings.putPref("notFollowedup", selfJsonObj.getString("Not Followedup"));
                ApplicationSettings.putPref("followupcoming", selfJsonObj.getString("Upcoming Followups"));
                ApplicationSettings.putPref("leadsInProgress", selfJsonObj.getString("Leads in progress"));
                ApplicationSettings.putPref("leadsNotInterested", selfJsonObj.getString("Leads not interested"));
            }
            agentList.add(map1);

            String groupsJson = dashboardObj.getString("groups");
            JSONArray jsonArray = new JSONArray(groupsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                MyJsonObject ajentJsonObj = new MyJsonObject(jsonArray.get(i).toString());
                String indivisualString = ajentJsonObj.getString("self");
                MyJsonObject individualJson = new MyJsonObject(indivisualString);

                HashMap<String, String> map = new HashMap<>();
                String agentName = ajentJsonObj.getString("email");
                map.put("agent", ajentJsonObj.getString("email"));
                map.put("called", individualJson.getString("Called once"));
                map.put("notCalled", individualJson.getString("Not called at all")); //Overdue
                map.put("calledUpcoming", individualJson.getString("Call In Future")); //Followup
                map.put("followup", individualJson.getString("Followed up"));
                map.put("notFollowedup", individualJson.getString("Not Followedup"));
                map.put("followedUpcoming", individualJson.getString("Upcoming Followups"));
                map.put("leadsInProgress", individualJson.getString("Leads in progress"));
                map.put("leadsNotInterested", individualJson.getString("Leads not interested"));

                if (ajentJsonObj.has("team")) {
                    String totalString = ajentJsonObj.getString("team");
                    MyJsonObject totalJsonObj = new MyJsonObject(totalString);
                    map.put("teamcalled", totalJsonObj.getString("Called once"));
                    map.put("teamnotCalled", totalJsonObj.getString("Not called at all")); //Overdue
                    map.put("teamcalledUpcoming", totalJsonObj.getString("Call In Future")); //Followup
                    map.put("teamfollowup", totalJsonObj.getString("Followed up"));
                    map.put("teamnotFollowedup", totalJsonObj.getString("Not Followedup"));
                    map.put("teamfollowedUpcoming", totalJsonObj.getString("Upcoming Followups"));
                    map.put("teamleadsInProgress", totalJsonObj.getString("Leads in progress"));
                    map.put("teamleadsNotInterested", totalJsonObj.getString("Leads not interested"));
                }

                if (!agentName.equalsIgnoreCase(user_email)) {
                    agentList.add(map);
                } else {
                    if (!userUploaded) {
                        if (agentName.equalsIgnoreCase(user_email)) {
                            agentList.add(map);
                            userUploaded = true;
                        }
                    }
                }
                JSONArray groups_array = ajentJsonObj.getJSONArray("groups");
                if (groups_array.length() > 0) {
                    agentList = mapGroups(groups_array, agentList);
                }
            }
            //Collections.reverse(agentList);
            UserActivities.setAgentList(agentList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return agentList;
    }

    private static ArrayList<HashMap<String, String>> mapGroups(JSONArray groupsArray, ArrayList<HashMap<String, String>> arrayList) {
        try {
            if (groupsArray.length() > 0) {
                for (int i = 0; i < groupsArray.length(); i++) {
                    JSONObject userObj = null;
                    userObj = new JSONObject(groupsArray.get(i).toString());
                    String indivisualString = userObj.getString("self");
                    MyJsonObject individualJson = new MyJsonObject(indivisualString);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("agent", userObj.getString("email"));
                    map.put("called", individualJson.getString("Called once"));
                    map.put("notCalled", individualJson.getString("Not called at all")); //Overdue
                    map.put("calledUpcoming", individualJson.getString("Call In Future")); //Followup
                    map.put("followup", individualJson.getString("Followed up"));
                    map.put("notFollowedup", individualJson.getString("Not Followedup"));
                    map.put("followedUpcoming", individualJson.getString("Upcoming Followups"));
                    map.put("leadsInProgress", individualJson.getString("Leads in progress"));
                    map.put("leadsNotInterested", individualJson.getString("Leads not interested"));

                    if (userObj.has("team")) {
                        String totalString = userObj.getString("team");
                        MyJsonObject totalJsonObj = new MyJsonObject(totalString);
                        map.put("teamcalled", totalJsonObj.getString("Called once"));
                        map.put("teamnotCalled", totalJsonObj.getString("Not called at all")); //Overdue
                        map.put("teamcalledUpcoming", totalJsonObj.getString("Call In Future")); //Followup
                        map.put("teamfollowup", totalJsonObj.getString("Followed up"));
                        map.put("teamnotFollowedup", totalJsonObj.getString("Not Followedup"));
                        map.put("teamfollowedUpcoming", totalJsonObj.getString("Upcoming Followups"));
                        map.put("teamleadsInProgress", totalJsonObj.getString("Leads in progress"));
                        map.put("teamleadsNotInterested", totalJsonObj.getString("Leads not interested"));

                    }
                    ///Log.d("IndTeam", "MapList"+ map.toString());
                    arrayList.add(map);
                    //Log.d("IndTeam", "ArrayList"+ arrayList.toString());
                    JSONArray groupData = userObj.getJSONArray("groups");
                    if (groupData.length() > 0) {
                        mapGroups(userObj.getJSONArray("groups"), arrayList);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }


    public static ArrayList<HashMap<String, String>> getMissedDashboardReports(String response) {
        MyJsonObject jsonObject = null;
        String success = " ";
        String user_email = "";

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            user_email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }

        boolean userUploaded = false;
        ArrayList<HashMap<String, String>> agentList = new ArrayList<HashMap<String, String>>();

        try {
            jsonObject = new MyJsonObject(response);
            String responseJson = jsonObject.getString("callsdashboard");
            MyJsonObject dashboardObj = new MyJsonObject(responseJson);
            HashMap<String, String> map1 = new HashMap<>();
            String agentEmail = dashboardObj.getString("email");
            map1.put("agent", dashboardObj.getString("email"));
            String selfString = dashboardObj.getString("self");
            MyJsonObject selfJsonObj = new MyJsonObject(selfString);

            map1.put("firstcallmissed", selfJsonObj.getString("First-Call-Missed-NCB"));
            map1.put("flpmissed", selfJsonObj.getString("Flp-Call-Missed-NCB")); //Overdue
            map1.put("firstoutgoing", selfJsonObj.getString("First-Call-Outgoing")); //Followup
            map1.put("flpoutgoing", selfJsonObj.getString("Flp-Call-Outgoing"));
            map1.put("firstcallincoming", selfJsonObj.getString("First-Call-Incoming"));
            map1.put("flpincoming", selfJsonObj.getString("Flp-Call-Incoming"));
            map1.put("firstcallrnr", selfJsonObj.getString("First-Call-RNR"));
            map1.put("flprnr", selfJsonObj.getString("Flp-Call-RNR"));
            map1.put("firsttalktime", selfJsonObj.getString("First-Call-Talktime"));
            map1.put("flptalktime", selfJsonObj.getString("Flp-Call-Talktime"));
            map1.put("firstcallcount", selfJsonObj.getString("First-Call-Count"));
            map1.put("flpcallcount", selfJsonObj.getString("Flp-Call-Count"));

            if (!agentEmail.equalsIgnoreCase(user_email)) {
                agentList.add(map1);
            } else {
                if (!userUploaded) {
                    if (agentEmail.equalsIgnoreCase(user_email)) {
                        agentList.add(map1);
                        userUploaded = true;
                    }
                }
            }

            if (dashboardObj.has("team")) {
                String teamString = dashboardObj.getString("team");
                MyJsonObject teamJson = new MyJsonObject(teamString);
                ApplicationSettings.putPref("firstcallmissed", teamJson.getString("First-Call-Missed-NCB"));
                ApplicationSettings.putPref("flpmissed", teamJson.getString("Flp-Call-Missed-NCB"));
                ApplicationSettings.putPref("firstoutgoing", teamJson.getString("First-Call-Outgoing"));
                ApplicationSettings.putPref("flpoutgoing", teamJson.getString("Flp-Call-Outgoing"));
                ApplicationSettings.putPref("firstcallincoming", teamJson.getString("First-Call-Incoming"));
                ApplicationSettings.putPref("flpincoming", teamJson.getString("Flp-Call-Incoming"));
                ApplicationSettings.putPref("firstcallrnr", teamJson.getString("First-Call-RNR"));
                ApplicationSettings.putPref("flprnr", teamJson.getString("Flp-Call-RNR"));
                ApplicationSettings.putPref("firsttalktime", teamJson.getString("First-Call-Talktime"));
                ApplicationSettings.putPref("flptalktime", teamJson.getString("Flp-Call-Talktime"));
                ApplicationSettings.putPref("firstcallcount", teamJson.getString("Flp-Call-Incoming"));
                ApplicationSettings.putPref("flpcallcount", teamJson.getString("Flp-Call-Incoming"));
            } else {
                ApplicationSettings.putPref("firstcallmissed", selfJsonObj.getString("First-Call-Missed-NCB"));
                ApplicationSettings.putPref("flpmissed", selfJsonObj.getString("Flp-Call-Missed-NCB"));
                ApplicationSettings.putPref("firstoutgoing", selfJsonObj.getString("First-Call-Outgoing"));
                ApplicationSettings.putPref("flpoutgoing", selfJsonObj.getString("Flp-Call-Outgoing"));
                ApplicationSettings.putPref("firstcallincoming", selfJsonObj.getString("First-Call-Incoming"));
                ApplicationSettings.putPref("flpincoming", selfJsonObj.getString("Flp-Call-Incoming"));
                ApplicationSettings.putPref("firstcallrnr", selfJsonObj.getString("First-Call-RNR"));
                ApplicationSettings.putPref("flprnr", selfJsonObj.getString("Flp-Call-RNR"));
                ApplicationSettings.putPref("firsttalktime", selfJsonObj.getString("First-Call-Talktime"));
                ApplicationSettings.putPref("flptalktime", selfJsonObj.getString("Flp-Call-Talktime"));
                ApplicationSettings.putPref("firstcallcount", selfJsonObj.getString("First-Call-Count"));
                ApplicationSettings.putPref("flpcallcount", selfJsonObj.getString("Flp-Call-Count"));
            }

            String groupsJson = dashboardObj.getString("groups");
            JSONArray jsonArray = new JSONArray(groupsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                MyJsonObject ajentJsonObj = new MyJsonObject(jsonArray.get(i).toString());
                String indivisualString = ajentJsonObj.getString("self");
                MyJsonObject individualJson = new MyJsonObject(indivisualString);

                HashMap<String, String> map = new HashMap<>();
                String agentName = ajentJsonObj.getString("email");
                map.put("agent", ajentJsonObj.getString("email"));
                map.put("firstcallmissed", individualJson.getString("First-Call-Missed-NCB"));
                map.put("flpmissed", individualJson.getString("Flp-Call-Missed-NCB")); //Overdue
                map.put("firstoutgoing", individualJson.getString("First-Call-Outgoing")); //Followup
                map.put("flpoutgoing", individualJson.getString("Flp-Call-Outgoing"));
                map.put("firstcallincoming", individualJson.getString("First-Call-Incoming"));
                map.put("flpincoming", individualJson.getString("Flp-Call-Incoming"));
                map.put("firstcallrnr", individualJson.getString("First-Call-RNR"));
                map.put("flprnr", individualJson.getString("Flp-Call-RNR"));
                map.put("firsttalktime", individualJson.getString("First-Call-Talktime"));
                map.put("flptalktime", individualJson.getString("Flp-Call-Talktime"));
                map.put("firstcallcount", individualJson.getString("First-Call-Count"));
                map.put("flpcallcount", individualJson.getString("Flp-Call-Count"));

                if (ajentJsonObj.has("team")) {
                    String totalString = ajentJsonObj.getString("team");
                    MyJsonObject totalJsonObj = new MyJsonObject(totalString);
                    map.put("teamfirstcallmissed", totalJsonObj.getString("First-Call-Missed-NCB"));
                    map.put("teamflpmissed", totalJsonObj.getString("Flp-Call-Missed-NCB")); //Overdue
                    map.put("teamfirstoutgoing", totalJsonObj.getString("First-Call-Outgoing")); //Followup
                    map.put("teamflpoutgoing", totalJsonObj.getString("Flp-Call-Outgoing"));
                    map.put("teamfirstcallincoming", totalJsonObj.getString("First-Call-Incoming"));
                    map.put("teamflpincoming", totalJsonObj.getString("Flp-Call-Incoming"));
                    map.put("teamfirstcallrnr", totalJsonObj.getString("First-Call-RNR"));
                    map.put("teamflprnr", totalJsonObj.getString("Flp-Call-RNR"));
                    map.put("teamfirsttalktime", totalJsonObj.getString("First-Call-Talktime"));
                    map.put("teamflptalktime", totalJsonObj.getString("Flp-Call-Talktime"));
                    map.put("teamfirstcallcount", totalJsonObj.getString("First-Call-Count"));
                    map.put("teamflpcallcount", totalJsonObj.getString("Flp-Call-Count"));
                }

                if (!agentName.equalsIgnoreCase(user_email)) {
                    agentList.add(map);
                } else {
                    if (!userUploaded) {
                        if (agentName.equalsIgnoreCase(user_email)) {
                            agentList.add(map);
                            userUploaded = true;
                        }
                    }
                }
                JSONArray groups_array = ajentJsonObj.getJSONArray("groups");
                if (groups_array.length() > 0) {
                    agentList = mapMissedCallGroups(groups_array, agentList);
                }
            }

            UserActivities.setMissedAgentList(agentList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return agentList;
    }

    private static ArrayList<HashMap<String, String>> mapMissedCallGroups(JSONArray groupsArray, ArrayList<HashMap<String, String>> arrayList) {
        try {
            if (groupsArray.length() > 0) {
                for (int i = 0; i < groupsArray.length(); i++) {
                    JSONObject userObj = null;
                    userObj = new JSONObject(groupsArray.get(i).toString());
                    String indivisualString = userObj.getString("self");
                    MyJsonObject selfJsonObj = new MyJsonObject(indivisualString);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("agent", userObj.getString("email"));
                    map.put("firstcallmissed", selfJsonObj.getString("First-Call-Missed-NCB"));
                    map.put("flpmissed", selfJsonObj.getString("Flp-Call-Missed-NCB")); //Overdue
                    map.put("firstoutgoing", selfJsonObj.getString("First-Call-Outgoing")); //Followup
                    map.put("flpoutgoing", selfJsonObj.getString("Flp-Call-Outgoing"));
                    map.put("firstcallincoming", selfJsonObj.getString("First-Call-Incoming"));
                    map.put("flpincoming", selfJsonObj.getString("Flp-Call-Incoming"));
                    map.put("firstcallrnr", selfJsonObj.getString("First-Call-RNR"));
                    map.put("flprnr", selfJsonObj.getString("Flp-Call-RNR"));
                    map.put("firsttalktime", selfJsonObj.getString("First-Call-Talktime"));
                    map.put("flptalktime", selfJsonObj.getString("Flp-Call-Talktime"));
                    map.put("firstcallcount", selfJsonObj.getString("First-Call-Count"));
                    map.put("flpcallcount", selfJsonObj.getString("Flp-Call-Count"));

                    if (userObj.has("team")) {
                        String totalString = userObj.getString("team");
                        MyJsonObject totalJsonObj = new MyJsonObject(totalString);
                        map.put("teamfirstcallmissed", totalJsonObj.getString("First-Call-Missed-NCB"));
                        map.put("teamflpmissed", totalJsonObj.getString("Flp-Call-Missed-NCB")); //Overdue
                        map.put("teamfirstoutgoing", totalJsonObj.getString("First-Call-Outgoing")); //Followup
                        map.put("teamflpoutgoing", totalJsonObj.getString("Flp-Call-Outgoing"));
                        map.put("teamfirstcallincoming", totalJsonObj.getString("First-Call-Incoming"));
                        map.put("teamflpincoming", totalJsonObj.getString("Flp-Call-Incoming"));
                        map.put("teamfirstcallrnr", totalJsonObj.getString("First-Call-RNR"));
                        map.put("teamflprnr", totalJsonObj.getString("Flp-Call-RNR"));
                        map.put("teamfirsttalktime", totalJsonObj.getString("First-Call-Talktime"));
                        map.put("teamflptalktime", totalJsonObj.getString("Flp-Call-Talktime"));
                        map.put("teamfirstcallcount", totalJsonObj.getString("First-Call-Count"));
                        map.put("teamflpcallcount", totalJsonObj.getString("Flp-Call-Count"));
                    }
                    arrayList.add(map);
                    JSONArray groupData = userObj.getJSONArray("groups");
                    if (groupData.length() > 0) {
                        mapGroups(userObj.getJSONArray("groups"), arrayList);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getLatLong(String response) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        try {
            MyJsonObject myJsonObject = new MyJsonObject(response);
            String maplocations = myJsonObject.getString("maplocations");
            JSONArray array = new JSONArray(maplocations);

            for (int i = 0; i < array.length(); i++) {
                MyJsonObject locations = new MyJsonObject(array.getString(i));
                HashMap<String, String> map = new HashMap<>();
                map.put("userlat", locations.getString("latitude"));
                map.put("userlong", locations.getString("longitude"));
                map.put("usermail", locations.getString("email"));
                arrayList.add(map);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static ArrayList<ArrayList<String>> getleadsAndSourceResponse(String respose) {
        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        try {
            MyJsonObject myJsonObject = new MyJsonObject(respose);
            String rows = myJsonObject.getString("leadssource");
            MyJsonObject rowsJson = new MyJsonObject(rows);
            String headerArray = rowsJson.getString("headers");
            JSONArray headerJsonArray = new JSONArray(headerArray);

            for (int i = 0; i < headerJsonArray.length(); i++) {
                /*HashMap<String, String> map = new HashMap<>();*/
                ArrayList<String> list = new ArrayList<>();
                String value = headerJsonArray.getString(i);
                list.add(value);
               /* String value= headerJsonArray.getString(i);
                String[] valuesArr = value.split(",");
                for(int j =0; j < valuesArr.length; j++ ) {
                    map.put("header"+j,valuesArr[j]);
                }*/
                arrayList.add(list);
            }

            String rowsArray = rowsJson.getString("rows");
            JSONArray rowsJsonArray = new JSONArray(rowsArray);
            for (int i = 0; i < rowsJsonArray.length(); i++) {

                ArrayList<String> list = new ArrayList<>();
                String value = rowsJsonArray.getString(i);
                JSONArray arr = new JSONArray(value);
                for (int j = 0; j < arr.length(); j++) {
                    list.add(arr.getString(j));
                }
               /* String[] valuesArr = value.split(",");
                for(int j =0; j<valuesArr.length; j ++) {
                    list.add(valuesArr.g)
                }*/
                arrayList.add(list);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public static String getIvrMappingNumber(String response) {
        //Log.d("ameyo", response);
        MyJsonObject jsonObject = null;
        String success = "";
        String number = "";

        try {
            jsonObject = new MyJsonObject(response);
            if (jsonObject.has("number")) {
                number = jsonObject.getString("number");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return number;
    }

    public static JSONObject getJsonAppStatus(AppStatus appStatus) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("activity_type", appStatus.activity_type);
            jsonObj.put("gps_status", appStatus.gps_status);
            jsonObj.put("call_recording_status", appStatus.call_recording_status);
            jsonObj.put("battery_status", appStatus.battery_status);
            jsonObj.put("user_id", appStatus.user_id);
            jsonObj.put("version", appStatus.versionNumber);
            JSONObject gpsjsonObj = new JSONObject();
            gpsjsonObj.put("latitude", appStatus.latitude);
            gpsjsonObj.put("longitude", appStatus.longitude);
            jsonObj.put("gps_status", gpsjsonObj.toString());
            return jsonObj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Single API for appointment creation and updation
    // 1 for create, 2 for update and 3 for all
    public static JSONObject getSingleApiJson(SingleApiAppointmentModel inputData, int type) {
        JSONObject singleAPI = new JSONObject();

        try {
            // First block of data
            singleAPI.put("user_id", inputData.getUserId());
            singleAPI.put("lat", inputData.getUserLatitude());
            singleAPI.put("long", inputData.getUserLongitude());

            JSONObject contactPersonObject = new JSONObject();
            contactPersonObject.put("company", inputData.getContactPersonCompany());
            contactPersonObject.put("name", inputData.getContactPersonName());
            contactPersonObject.put("designation", inputData.getContactPersonCompany());
            contactPersonObject.put("number", inputData.getContactPersonCompany());
            contactPersonObject.put("email", inputData.getContactPersonCompany());
            contactPersonObject.put("address", inputData.getContactPersonCompany());
            contactPersonObject.put("website", inputData.getContactPersonCompany());
            singleAPI.put("contact_person", contactPersonObject); // JSON Object

            // Second block of data
            if (type == 1 || type == 3) {
                JSONObject createAppointmentObject = new JSONObject();
                createAppointmentObject.put("from", inputData.getCreateAppointmentFrom());
                createAppointmentObject.put("to", inputData.getCreateAppointmentTo());


                JSONObject createAppointmentStartObject = new JSONObject();
                createAppointmentStartObject.put("dateTime", inputData.getCreateAppointmentStartTime());
                createAppointmentStartObject.put("timeZone", inputData.getCreateAppointmentTimezone());

                createAppointmentObject.put("start", createAppointmentStartObject);

                JSONObject createAppointmentEndObject = new JSONObject();
                createAppointmentEndObject.put("dateTime", inputData.getCreateAppointmentEndTime());
                createAppointmentEndObject.put("timeZone", inputData.getCreateAppointmentTimezone());

                createAppointmentObject.put("end", createAppointmentEndObject);

                createAppointmentObject.put("subject", inputData.getCreateAppointmentSubject());
                createAppointmentObject.put("repeat_params", inputData.getCreateAppointmentRepeat());
                createAppointmentObject.put("alert_before", inputData.getCreateAppointmentAlertBefore());
                createAppointmentObject.put("lead_source", inputData.getCreateAppointmentLeadSource());
                createAppointmentObject.put("product_type", inputData.getCreateAppointmentProductType());
                createAppointmentObject.put("seteventto_teamemail", inputData.getCreateAppointmentSetEventToTeamMail());
                createAppointmentObject.put("cc", inputData.getCreateAppointmentCc());
                createAppointmentObject.put("responsestatus", "accepted");
                createAppointmentObject.put("flpcount", inputData.getCreateAppointmentFlpCount());
                createAppointmentObject.put("status", inputData.getCreateAppointmentStatus());
                createAppointmentObject.put("order_potential", inputData.getCreateAppointmentOrderPotential());
                createAppointmentObject.put("description", inputData.getCreateAppointmentDescription());
                singleAPI.put("create_appointment", createAppointmentObject); // JSON Object
            }

            if (type == 3 || type == 2) {
                JSONObject updateAppointmentObject = new JSONObject();

                updateAppointmentObject.put("Id", inputData.getUpdateAppointmentId());
                updateAppointmentObject.put("from", inputData.getUpdateAppointmentFrom());
                updateAppointmentObject.put("to", inputData.getUpdateAppointmentTo());

                JSONObject updateAppoinemtnStartObject = new JSONObject();
                updateAppoinemtnStartObject.put("dateTime", inputData.getUpdateAppointmentStartTime());
                updateAppoinemtnStartObject.put("timeZone", inputData.getUpdateAppointmentTimeZone());

                updateAppointmentObject.put("start", updateAppoinemtnStartObject);

                JSONObject updateAppointmentEndObject = new JSONObject();
                updateAppointmentEndObject.put("dateTime", inputData.getUpdateAppointmentEndTime());
                updateAppointmentEndObject.put("timeZone", inputData.getUpdateAppointmentTimeZone());

                updateAppointmentObject.put("end", updateAppointmentEndObject);

                updateAppointmentObject.put("subject", inputData.getUpdateAppointmentSubject());
                updateAppointmentObject.put("repeat_params", inputData.getUpdateAppointmentRepeat());
                updateAppointmentObject.put("alert_source", inputData.getUpdateAppointmentAlertBefore());
                updateAppointmentObject.put("lead_source", inputData.getUpdateAppointmentLeadSource());
                updateAppointmentObject.put("product_type", inputData.getUpdateAppointmentProductType());
                updateAppointmentObject.put("seteventto_teamemail", inputData.getUpdateAppointmentSetEventTeamEmail());
                updateAppointmentObject.put("cc", ""); // inputData.getUpdateAppointmentcc); // Add CC
                updateAppointmentObject.put("responsestatus", inputData.getUpdateAppointmentResponseStatus());
                updateAppointmentObject.put("flpcount", inputData.getUpdateAppointmentFlpCount());
                updateAppointmentObject.put("status", inputData.getUpdateAppointmentStatus());
                updateAppointmentObject.put("order_potential", inputData.getUpdateAppointmentOrderPotential());
                updateAppointmentObject.put("description", inputData.getUpdateAppointmentDescription());

                singleAPI.put("update_appointment", updateAppointmentObject); // Json Object
            }

            // Fourth block of data

            if (type == 3) {
                JSONObject bizActObject = new JSONObject();

                bizActObject.put("from", inputData.getFromNumber());
                bizActObject.put("to", inputData.getToNumber());
                bizActObject.put("activity_type", inputData.getActivityType());
                bizActObject.put("subject", inputData.getSubject());
                bizActObject.put("url", inputData.getUrl());
                bizActObject.put("start_time", inputData.getStartTime());
                bizActObject.put("end_time", inputData.getEndTime());
                bizActObject.put("message", inputData.getMessage());
                bizActObject.put("parent", Integer.parseInt(inputData.getParent()));
                bizActObject.put("eventy_type", inputData.getEventType());
                bizActObject.put("unread", inputData.getUnread());
                bizActObject.put("name", inputData.getName());
                bizActObject.put("caller_name", inputData.getCaller_name());

                singleAPI.put("bizact", bizActObject); // Json Object
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return singleAPI;
    }

    public static SingleApiAppointmentModel getSingleApiResponse(String data) {
        SingleApiAppointmentModel singleApiAppointmentModel = new SingleApiAppointmentModel();
        return singleApiAppointmentModel;
    }

    public static JSONObject getJsonForPersonal(PersonalJunkContact data) {
        JSONObject object = new JSONObject();
        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        try {
            object.put("user_id", userId);
            object.put("phone", data.getPhoneNumber());
            object.put("name", data.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }


    public static JSONObject getJsonForJunk(PersonalJunkContact data) {
        JSONObject object = new JSONObject();
        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        try {
            object.put("user_id", userId);
            object.put("phone", data.getPhoneNumber());
            object.put("name", data.getType());
            object.put("status", data.getStatus());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    public static ArrayList<String[]> getCmailCallRecordings(String response) {
        ArrayList<String[]> cmail_recordinds = new ArrayList<>();
        try {
            JSONArray personalArray = new JSONArray(response);
            for (int i = 0; i < personalArray.length(); i++) {
                JSONObject jItem = personalArray.getJSONObject(i);
                String url = jItem.getString("url");
                String start_time = jItem.getString("start_time");
                String status = jItem.getString("status");
                String[] arr = new String[3];
                arr[0] = "" + url;
                arr[1] = "" + start_time;
                arr[2] = "" + status;
                cmail_recordinds.add(arr);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return cmail_recordinds;
    }

    public static List<PersonalJunkContact> getPersonal(String response) {
        List<PersonalJunkContact> returnList = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(response);

            if (data.has("personal")) {
                JSONArray personalArray = data.getJSONArray("personal");
                for (int i = 0; i < personalArray.length(); i++) {
                    JSONObject jItem = personalArray.getJSONObject(i);
                    PersonalJunkContact item = new PersonalJunkContact();
                    item.setName(jItem.getString("name"));
                    item.setPhoneNumber(jItem.getString("number"));
                    item.setType(jItem.getString("type_of_num"));
                    returnList.add(item);
                }
            }

            if (data.has("junk_blocked")) {
                JSONArray blockedJunkArray = data.getJSONArray("junk_blocked");
                for (int i = 0; i < blockedJunkArray.length(); i++) {
                    JSONObject jItem = blockedJunkArray.getJSONObject(i);
                    PersonalJunkContact item = new PersonalJunkContact();
                    item.setName(jItem.getString("name"));
                    item.setPhoneNumber(jItem.getString("number"));
                    item.setType(jItem.getString("type_of_num"));
                    returnList.add(item);
                }
            }

            if (data.has("junk_unblocked")) {
                JSONArray unblockedJunkArray = data.getJSONArray("junk_unblocked");
                for (int i = 0; i < unblockedJunkArray.length(); i++) {
                    JSONObject jItem = unblockedJunkArray.getJSONObject(i);
                    PersonalJunkContact item = new PersonalJunkContact();
                    item.setName(jItem.getString("name"));
                    item.setPhoneNumber(jItem.getString("number"));
                    item.setType(jItem.getString("type_of_num"));
                    returnList.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    // Get JSON object for deletion
    public static JSONObject getDeletePersonalJunk(List<PersonalJunkContact> data) {
        JSONObject object = new JSONObject();
        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

        JSONArray deleteArray = new JSONArray();
        try {
            object.put("user_id", userId);

            for (PersonalJunkContact item : data) {
                JSONObject jItem = new JSONObject();

                deleteArray.put(item.getPhoneNumber());
            }
            object.put("delete_numbers", deleteArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static JSONObject getKnolarityJson(KnowlarityModel knowlarityModel) {
        JSONObject jsonObject = new JSONObject();
        try {
            String uid = knowlarityModel.getUser_id();
            if(uid != null && !uid.isEmpty()) {
                jsonObject.put("user_id", uid);
            }
            jsonObject.put("k_number", knowlarityModel.getK_name());
            if(ApplicationSettings.containsPref(AppConstants.USER_VOIP_NUMBER)) {
                String voipNumber = ApplicationSettings.getPref(AppConstants.USER_VOIP_NUMBER, "");
                if(voipNumber != null && !voipNumber.isEmpty()) {
                    jsonObject.put("agent_number", voipNumber);
                } else {
                    jsonObject.put("agent_number", knowlarityModel.getAgent_name());
                }
            } else {
                jsonObject.put("agent_number", knowlarityModel.getAgent_name());
            }
            jsonObject.put("customer_number", knowlarityModel.getCustomer_number());
            jsonObject.put("caller_id", knowlarityModel.getCaller_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static HashMap<String, Object> getReFormdata(KnowlarityModel knowlarityModel) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("customer_number", knowlarityModel.getCustomer_number());
        if(ApplicationSettings.containsPref(AppConstants.USER_VOIP_NUMBER)) {
            String voipNumber = ApplicationSettings.getPref(AppConstants.USER_VOIP_NUMBER, "");
            if(voipNumber != null && !voipNumber.isEmpty()) {
                params.put("agent_number", voipNumber);
            } else {
                params.put("agent_number", knowlarityModel.getAgent_name());
            }
        } else {
            params.put("agent_number", knowlarityModel.getAgent_name());
        }
        params.put("caller_id", knowlarityModel.getCaller_id());
        String uid = knowlarityModel.getUser_id();
        if(uid != null && !uid.isEmpty()) {
            params.put("user_id", uid);
        }
        boolean allowRedialForRemoteEnabled = ApplicationSettings.getPref(AppConstants.ALLOW_REDIAL_FOR_REMOTE_ENABLED, false);
        if (allowRedialForRemoteEnabled) {
            params.put("redial", 1);
        }
        boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
        if(sequencialEndpoint) {
            if (ApplicationSettings.containsPref(AppConstants.IS_PROMO)) {
                String isPromo = ApplicationSettings.getPref(AppConstants.IS_PROMO, "");
                if (isPromo != null && !isPromo.isEmpty()) {
                    params.put("is_promo", isPromo);
                }
            } else {
                params.put("is_promo", 1);
            }
            boolean hybridEndpoint = ApplicationSettings.getPref(AppConstants.C2C_HYBRID_ENDPOINT, false);
            if (hybridEndpoint) {
                if (ApplicationSettings.containsPref(AppConstants.PARALLEL_GROUP_SIZE)) {
                    String parallelGroupSize = ApplicationSettings.getPref(AppConstants.PARALLEL_GROUP_SIZE, "");
                    if (parallelGroupSize != null && !parallelGroupSize.isEmpty()) {
                        params.put("parallel_group_size", parallelGroupSize);
                    }
                } else {
                    params.put("parallel_group_size", 2);
                }
                params.put("allow_push", 1);
            }
        }

        if(SmarterSMBApplication.pingStatusResponse != null && !SmarterSMBApplication.pingStatusResponse.isEmpty()){
            params.put("pingdelay", SmarterSMBApplication.pingStatusResponse);
            //Log.d("PingDelay", "JSONParser - getReFormdata(): " + params.toString());
        }

        if(SmarterSMBApplication.currentNetworkType != null && !SmarterSMBApplication.currentNetworkType.isEmpty()){
            params.put("networktype", SmarterSMBApplication.currentNetworkType);
        }

        /*  params.put("caller_id","+918048131028");*/
        return params;
    }

    public static JSONObject getKnolarityRegistrationJson(KnowlarityModel knowlarityModel) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("country_code", knowlarityModel.getCountry_code());
            jsonObject.put("email", knowlarityModel.getEmail());
            jsonObject.put("first_name", knowlarityModel.getFirst_name());
            jsonObject.put("last_name", knowlarityModel.getLast_name());
            jsonObject.put("phone", knowlarityModel.getPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getKnowlarityCallLogJson(KnowlarityCallLogs knowlarityCallLogs) {
        JSONObject jsonObject = new JSONObject();
        try {
            String user_id = "", customer_number = "", voipNumber = "";
            if(ApplicationSettings.containsPref(AppConstants.USERINFO_ID) && ApplicationSettings.getPref(AppConstants.USERINFO_ID,"") != null) {
                user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID,"");
            }
            if(ApplicationSettings.containsPref(AppConstants.USER_VOIP_NUMBER) && (ApplicationSettings.getPref(AppConstants.USER_VOIP_NUMBER, "") != null)) {
                voipNumber = ApplicationSettings.getPref(AppConstants.USER_VOIP_NUMBER, "");
                user_id = user_id + voipNumber;
            } else if(ApplicationSettings.containsPref(AppConstants.USERINFO_PHONE) && (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null)) {
                voipNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                user_id = user_id + voipNumber;
            }
            if(NotificationData.knolarity_number != null) {
                customer_number = NotificationData.knolarity_number;
            }
            jsonObject.put("c2c_init", knowlarityCallLogs.getApiRequestTime());
            jsonObject.put("c2c_success", knowlarityCallLogs.getApiResponseTime());
            jsonObject.put("c2c_incoming", knowlarityCallLogs.getCallLandTime());
            jsonObject.put("user_id", user_id);
            jsonObject.put("customer_number",customer_number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // Get JSON object for deletion
    public static String[] getKnowlarityCallog(String data) {
        String call_duration = "", customer_number = "";
        String[] arr = new String[0];
        try {
            MyJsonObject jsonObject = new MyJsonObject(data);
            String object = jsonObject.getString("objects");
            JSONArray personalArray = new JSONArray(object);
            for (int i = 0; i < personalArray.length(); i++) {
                arr = new String[2];
                JSONObject jItem = personalArray.getJSONObject(0);
                call_duration = jItem.getString("agent_number");
                customer_number = jItem.getString("customer_number");
                arr[0] = call_duration;
                arr[1] = customer_number;
                return arr;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }

    public static String parseC2cRespon(String c2CResponse) {
        String callerId = "";
        String listOfDNDNumbers = null;
        String listOfInvalidNumbers = null;
        String listOfRepeatedNumbers = null;
        try {
            MyJsonObject myJsonObject = new MyJsonObject(c2CResponse);
            if(myJsonObject.has("status")) {
                String status = myJsonObject.getString("status");
                if (status != null && status.equalsIgnoreCase("success")) {
                    callerId = myJsonObject.getString("call_id");
                }
            }
            if(myJsonObject.has("ndnc_nums")) {
                listOfDNDNumbers = myJsonObject.getString("ndnc_nums");
                ApplicationSettings.putPref(AppConstants.LIST_OF_DND_NUMBERS, listOfDNDNumbers);
            }
            if(myJsonObject.has("invalid_customer_number")) {
                listOfInvalidNumbers = myJsonObject.getString("invalid_customer_number");
                ApplicationSettings.putPref(AppConstants.LIST_OF_INVALID_NUMBERS, listOfInvalidNumbers);
            }
            if(myJsonObject.has("repeated_numbers")) {
                listOfRepeatedNumbers = myJsonObject.getString("repeated_numbers");
                ApplicationSettings.putPref(AppConstants.LIST_OF_REPEATED_NUMBERS, listOfRepeatedNumbers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return callerId;
    }

    public static SlotData getAgentSlotsResponse(String slotResponse) {
        Gson g = new Gson();
        SlotData response = g.fromJson(slotResponse, SlotData.class);

        return response;
    }

    public static JSONObject getPostSlotsResponse(SlotCancel data) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("slot_id", data.getSlot_id());
            jsonObj.put("user_id", data.getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }

    public static NotificationMessageResponse getNotificationMessagesResponse(String notificationMessageResponse) {
        Gson g = new Gson();
        NotificationMessageResponse response = g.fromJson(notificationMessageResponse, NotificationMessageResponse.class);

        return response;
    }

    public static JSONObject getPostReadResponse(PostNotificationMessage data) {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj1 = new JSONObject();
        try {
            jsonObj.put("user_id", data.getUser_id());
            jsonObj1.put("id", data.getPostNotifications()[0].getId());
            jsonObj1.put("status", data.getPostNotifications()[0].getStatus());
            jsonArray.put(jsonObj1);
            jsonObj.put("postNotifications", jsonArray);
            Log.e("JSONParser","jsonObj :: "+jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }

    public static JSONObject getPostCancelSlotsResponse(SlotCancel data) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("slot_id", data.getSlot_id());
            jsonObj.put("user_id", data.getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }

    public static JSONObject getPostChannelInfoResponse(PMRequestChannel data) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("username", data.getUsername());
            jsonObj.put("userid", data.getUserid());
            jsonObj.put("type", data.getType());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }

    public static ResponseChannel getChannelsResponse(String channelResponse) {
        Gson g = new Gson();
        ResponseChannel response = g.fromJson(channelResponse, ResponseChannel.class);

        return response;
    }

    public static String getObjectChannelFromJson(String response) {
        String result = null;
        try {
            JSONObject jobj = new JSONObject(response);
            if (jobj != null) {
                return String.valueOf(jobj);
            }
            //return jobj.getString("success");
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static JSONObject getPostTechSupportResponse(TechSupportRequest data) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("tech_id", data.getTech_id());
            jsonObj.put("user_id", data.getUser_id());
            jsonObj.put("session_id", data.getSession_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }

    public static TrainingDataResponse getTrainingDataResponse(String trainingDataResponse) {
        Gson g = new Gson();
        TrainingDataResponse response = g.fromJson(trainingDataResponse, TrainingDataResponse.class);

        return response;
    }

    public static JSONObject getPostTrainerReview(TrainerReview data) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("user_id", data.getUser_id());
            jsonObj.put("groupid", data.getGroupid());
            jsonObj.put("day", data.getDay());
            jsonObj.put("batch_code", data.getBatch_code());
            jsonObj.put("review", data.getReview());
            jsonObj.put("rating", data.getRating());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }

    public static JSONObject getPostTrainingReadMessage(MessageReadRequest data) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("trainer_id", data.getTrainer_id());
            jsonObj.put("message_id", data.getMessage_id());
            jsonObj.put("message_mark", data.getMessage_mark());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }

    public static JSONObject getPostNetworkDetails(NetworkDetails data) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("network", data.getNetwork());
            jsonObj.put("network_speed", data.getNetwork_speed());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }
}
