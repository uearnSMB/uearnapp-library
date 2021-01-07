package smarter.uearn.money.models;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.webservice.Constants;

import java.util.Calendar;
import java.util.Date;

import android.os.Build;

/**
 * Created by Santosh on 18-06-2015.
 */
public class SmartUser {
    // Declaration of Objects of the cmail_users class
    private String id = "";
    private String name = "";
    private String bizprize = "";
    private String updated_at = "";
    private String company = "";
    private String email = "";
    private String phone = "";
    private String business_logo_url = "";
    private String business_tagline = "";
    private String call_sms_template = "";
    private String business_template = "";
    private String jd_sms_template = "";
    private String jc_sms_template = "";
    private boolean send_map_location;
    private String password = "";
    private String payment_status;
    private String gender ="";
    private String jd_no = "";
    private String address = "";
    private String yt_video_link = "";
    private String website = "";
    private String countrycode = "";
    private String customer_testimonial_url = "";
    private String google_map_url = "";
    private String google_map_landmark = "";
    private String jd_url = "";
    private String report_email;
    private String login_method = "";
    private String facility_photo_url1 = "";
    private String facility_photo_url2 = "";
    private String facility_photo_url3 = "";
    private String facility_photo_url4 = "";
    private String primary_contact = "";
    private String language ="";
    private String education ="";
    private String workexperience ="";
    private String businessprocess ="";
    private String otherexperience ="";
    private String additionaldata ="";

    //Modified By Srinath :::KSN
    private boolean followup_edit = false;
    private boolean callRecord_status = false;
    private boolean afterCallpopup = true;
    private boolean captureIncomingSMS = false;
    private boolean donotRecordPrivateContacts = true;
    private boolean recordOnlyUnknownNumbers = true;
    private boolean recordOnlyLeadNumbers = true;
    private boolean recordOnlyCloudTelephonyNumbers = false;
    private boolean recordOnlyTeamMemberNumbers = true;
    private boolean enquiryResponseOn = false;
    private boolean voiceAnalytics = false;
    private boolean isFollowup_edit = false;
    private String user_status = "";
    private boolean customerNumberStatus = false;
    private String created_at = "";


    private String facebook_link = "";
    private String twitter_link = "";
    private String linked_link = "";
    private String jobTitle = "";
    private String youtubeChannelLink = "";
    private String my_server_url = "";
    private String my_sio_url = "";
    private String my_dashboardsio_url = "";

    //Modified By Srinath :::KSN
    private boolean showPopupSettings = true;
    private boolean showNotificationSettings = false;
    private boolean gpsLocationSettings = false;

    // Dilip
    private boolean autoAcceptSettings = true;
    private boolean callTrackingAll = true;
    private boolean followUpSettings = false;
    private boolean teamCallSettings = false;
    private boolean smsTrackingSettings = false;
    private boolean autoFollowUpsSettings = true;
    private boolean applicationLoggingSettings = false;
    private boolean smsTrackingAll = false;
    private boolean smstrackingleads = false;
    private boolean smstrackingteam = true;
    private boolean smstrackingpersonal = false;
    private boolean appUpdated = true;
    private boolean editSettings = false;
    private boolean cloudOutgoing = false;
    private boolean autoDailer = false;
    private boolean showAfterCallPopup = false;
    private boolean appendLeadSource = false;
    private boolean uploadCmail = false;
    private boolean truePredictive = false;
    private boolean showAlert = false;
    private boolean showTimer = false;
    private boolean backToNetwork = false;
    private boolean doneRefresh = false;
    private boolean uploadStatus2 = false;
    private boolean customerInfoAck = false;
    private boolean customerQues = false;
    private boolean isEmulationOn = false;
    private boolean serveraptupdates = false;
    private String callend_duration = "";
    private boolean allowScreenshot = false;
    private boolean systemcontrol = false;
    private boolean ssuonsio = false;
    private boolean fkcontrol = false;
    private boolean callwaiting = false;
    private boolean ibcontrol = false;
    private boolean adhoccall = false;
    private boolean autoanswer = false;
    private boolean showui = false;
    private boolean allowmobiledata = false;
    private boolean ASF = false;
    private boolean homenavigation = false;
    private boolean weblayout = false;
    private boolean immquessubmit = false;
    private boolean allowping = false;
    private boolean allowchatbot = false;
    private boolean c2cchat = false;
    private boolean allowGroupChatCall = false;
    private boolean allowAdhocCall = false;
    private boolean disallowheadphones = false;
    private boolean allowpmC2c = false;
    private boolean callwaitingchoice = false;
    private boolean disablecallback = false;
    private boolean additionallogininfo = false;
    private boolean abortcallsession = false;
    private boolean updatecustomkvs = false;
    private boolean defaultdialer = false;

    public String getBusiness_tagline() {
        return business_tagline;
    }

    public boolean isMon() {
        return  ApplicationSettings.getPref(AppConstants.MON,false);
    }

    public boolean isTue() {
        return  ApplicationSettings.getPref(AppConstants.TUE,false);
    }

    public boolean isWen() {
        return  ApplicationSettings.getPref(AppConstants.WEN,false);
    }

    public boolean isThu() {
        return  ApplicationSettings.getPref(AppConstants.THU,false);
    }

    public boolean isFri() {
        return  ApplicationSettings.getPref(AppConstants.FRI,false);
    }

    public boolean isSat() {
        return  ApplicationSettings.getPref(AppConstants.SAT,false);
    }

    public boolean isSun() {
        return  ApplicationSettings.getPref(AppConstants.SUN,false);
    }

    private boolean book_appointment_status = true;
    private boolean autoSmsForMissedCalls = false;
    private boolean junkCallSetting = true;
    private boolean personalcallSetting = true;
    private int rnrReminder = 1;
    private int rnrMax = 3;

    private String office365_token;
    private String accessToken;
    private String profileUrl = "";
    private String role = "";
    private String dob = "";
    private String lang = "";
    private String locality = "";

    public void setMon(boolean mon) {
        this.mon = mon;
        ApplicationSettings.putPref(AppConstants.MON,mon);
    }

    public void setTue(boolean tue) {
        this.tue = tue;
        ApplicationSettings.putPref(AppConstants.TUE,tue);
    }

    public void setWen(boolean wen) {
        this.wen = wen;
        ApplicationSettings.putPref(AppConstants.WEN,wen);
    }

    public void setFri(boolean fri) {
        this.fri = fri;
        ApplicationSettings.putPref(AppConstants.FRI,fri);
    }

    public void setSat(boolean sat) {
        this.sat = sat;
        ApplicationSettings.putPref(AppConstants.SAT,sat);
    }

    public void setSun(boolean sun) {
        this.sun = sun;
        ApplicationSettings.putPref(AppConstants.SUN,sun);
    }

    public void setThu(boolean thu) {
        this.thu = thu;
        ApplicationSettings.putPref(AppConstants.THU,thu);
    }

    private boolean mon = false, tue = false, wen =false , thu =false, fri = false, sat = false, sun = false;

    public SmartUser() {

        call_sms_template = AppConstants.DEFAULT_AFTER_CALL_TEMPLATE;
        business_template = AppConstants.DEFAULT_BUSINESS_TEMPLATE;
        jd_sms_template = AppConstants.DEFAULT_JD_TEMPLATE;
        jc_sms_template = AppConstants.DEFAULT_JOBCARD_TEMPLATE;

        showNotificationSettings = ApplicationSettings.getPref(AppConstants.SHOW_NOTIFICATION, false);
        showPopupSettings = ApplicationSettings.getPref(AppConstants.SHOW_POPUP, true);
        callRecord_status = false;
        afterCallpopup = true;
        captureIncomingSMS = false;
        gpsLocationSettings = false;
        autoSmsForMissedCalls = false;
        donotRecordPrivateContacts = ApplicationSettings.getPref(AppConstants.DONT_CALL_RECORD_PRIVATE_CONTACTS, true);
        recordOnlyUnknownNumbers = false;
        recordOnlyLeadNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_LEAD_CONTACTS, true);
        recordOnlyCloudTelephonyNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_CLOUND_TELEPHONY_NUMBERS, false);
        recordOnlyTeamMemberNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_TEAM_MEMBERS_NUMBERS, false);
        enquiryResponseOn = false;
    }

    public SmartUser(String name, String email, String phone, String password, String login_method, String gender) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.login_method = login_method;
        this.gender = gender;
    }


    public SmartUser(String name, String email, String phone, String password, String login_method, String gender, String language, String education, String workexperience, String businessprocess, String otherexperience, String additionaldata) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.login_method = login_method;
        this.gender = gender;
        this.language = language;
        this.education = education;
        this.workexperience = workexperience;
        this.businessprocess = businessprocess;
        this.otherexperience = otherexperience;
        this.additionaldata = additionaldata;
    }

    // getter and setter methods for the objects
    public boolean getCallRecordStatus() {
        return callRecord_status;
    }

    public void setCallRecordStatus(boolean value) {
        if (value) {
            callRecord_status = true;
            ApplicationSettings.putPref(AppConstants.SETTING_CALL_RECORDING_STATE, true);
            ApplicationSettings.putPref(AppConstants.CALL_RECORDING_OFF, false);
        } else {
            callRecord_status = false;
            ApplicationSettings.putPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
            ApplicationSettings.putPref(AppConstants.CALL_RECORDING_OFF, true);

        }
    }

    public void setVoiceAnalytics(boolean value) {
        if (value) {
            voiceAnalytics = true;
            ApplicationSettings.putPref(AppConstants.VOICE_ANALYTICS, true);
        } else {
            voiceAnalytics = false;
            ApplicationSettings.putPref(AppConstants.VOICE_ANALYTICS, false);
        }
    }

    public boolean getVoiceAnalytics() {
        return voiceAnalytics;
    }










    public boolean getBookAppointment() {
        return book_appointment_status;
    }

    public void setBookAppointment(boolean value) {
        if (value) {
            book_appointment_status = true;
            ApplicationSettings.putPref(AppConstants.SETTINGS_BOOK_APPOINTMENT_STATUS, true);

        } else {
            book_appointment_status = false;
            ApplicationSettings.putPref(AppConstants.SETTINGS_BOOK_APPOINTMENT_STATUS, false);
        }

    }

    public boolean getAfterCallpopup() {
        return afterCallpopup;
    }

    public void setAfterCallpopup(boolean value) {
        afterCallpopup = value;
        ApplicationSettings.putPref(AppConstants.SETTING_AFTER_CALL_POPUP, value);
    }

    public boolean getCaptureIncomingSMS() {
        return captureIncomingSMS;
    }

    public void setCaptureIncomingSMS(boolean value) {
        captureIncomingSMS = value;
        ApplicationSettings.putPref(AppConstants.CAPTURE_INCOMING_SMS, value);
    }

    public boolean getDonotRecordPrivateContacts() {
        return donotRecordPrivateContacts;
    }

    public void setDonotRecordPrivateContacts(boolean value) {
        donotRecordPrivateContacts = value;
        ApplicationSettings.putPref(AppConstants.DONT_CALL_RECORD_PRIVATE_CONTACTS, value);
    }

    public boolean getRecordOnlyUnknownNumbers() {
        return recordOnlyUnknownNumbers;
    }

    public void setRecordOnkyUnknownNumbers(boolean value) {
        recordOnlyUnknownNumbers = value;
        ApplicationSettings.putPref(AppConstants.ONLY_CALL_RECORD_UNKNOWN_NUMBERS, value);
    }

    public void setRecordOnlyLeadNumbers(boolean value) {
        recordOnlyLeadNumbers = value;
        ApplicationSettings.putPref(AppConstants.ONLY_RECORD_LEAD_CONTACTS, value);
    }

    public boolean getRecordOnlyLeadNumbers() {
        return recordOnlyLeadNumbers;
    }

    public boolean getEnquiryResponseOn() {
        return enquiryResponseOn;
    }

    public void setEnquiryResponseOn(boolean value) {
        enquiryResponseOn = value;
        ApplicationSettings.putPref(AppConstants.SETTING_JD_CHECK, value);
    }

    public void setShowNotificationSettings(boolean value) {
        this.showNotificationSettings = value;
        ApplicationSettings.putPref(AppConstants.SHOW_NOTIFICATION, value);
    }

    public boolean getShowNotificationSettings() {
        return showNotificationSettings;
    }

    public void setShowPopupSettings(boolean value) {
        this.showPopupSettings = value;
        ApplicationSettings.putPref(AppConstants.SHOW_POPUP, value);
    }

    public boolean getShowPopupSettings() {
        return showPopupSettings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id, boolean save) {
        this.id = id;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_ID, id);
        }
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public void setName(String name, boolean save) {
        this.name = name;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_NAME, name);
        }
    }

    public String getBizprize() { return bizprize;}


    public void setBizPrize(String bizprize, boolean save) {
        this.bizprize = bizprize;
        if (save) {
            writeToAppContstant(AppConstants.BIZ_PRIZE_VALUE, bizprize);
        }
    }

    public String getUpdatedAt() { return updated_at;}

    public void setUpdatedAt(String updated_at, boolean save) {
        this.updated_at = updated_at;
        if (save) {
            writeToAppContstant(AppConstants.UPDATED_AT, updated_at);
        }
    }


    public void setGender(String gender, boolean save) {
        this.gender = gender;
        if(save) {
            writeToAppContstant(AppConstants.USERINFO_GENDER, gender);
        }
    }





    public String getUserSatus(){return user_status;}

    public void setUserStatus(String user_status, boolean save) {
        this.user_status = user_status;
        if(save) {
            writeToAppContstant(AppConstants.USER_STATUS, user_status);
        }
    }


    public String getUserCreated(){return created_at;}

    public void setUserCreated(String created_at, boolean save) {
        this.created_at = created_at;
        if(save) {
            writeToAppContstant(AppConstants.USER_CREATED, created_at);
        }
    }





    public void setShowCustomerNumberStatus(String user_status, boolean save){
        this.user_status = user_status;
        if(save) {
            writeToAppContstant(AppConstants.CUSTOMER_NUMBER_STATUS, user_status);
        }

    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company, boolean save) {
        this.company = company;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_COMPANY, company);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email, boolean save) {
        this.email = email;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_EMAIL, email);
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone, boolean save) {
        this.phone = phone;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_PHONE, phone);
        }
    }

    public String getBusiness_logo_url() {
        return business_logo_url;
    }

    public void setBusiness_logo_url(String business_logo_url, boolean save) {
        this.business_logo_url = business_logo_url;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_BUSINESS_LOGO_URL, business_logo_url);
        }
    }

    public void setBusiness_tagline(String business_tagline, boolean save) {
        this.business_tagline = business_tagline;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_BUSINESS_TAGLINE, business_tagline);
        }
    }

    public String getCall_sms_template() {
        return call_sms_template;
    }

    public void setCall_sms_template(String call_sms_template, boolean save) {
        this.call_sms_template = call_sms_template;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_CALL_SMS_TEMPLATE, call_sms_template);
        }
    }

    public String getbusiness_template() {
        return business_template;
    }

    public void setbusiness_template(String business_template, boolean save) {
        this.business_template = business_template;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_BUSINESS_TEMPLATE, business_template);
        }
    }

    public String getJd_sms_template() {
        return jd_sms_template;
    }

    public void setJd_sms_template(String jd_sms_template, boolean save) {
        this.jd_sms_template = jd_sms_template;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_JD_SMS_TEMPLATE, jd_sms_template);
        }
    }

    public String getJc_sms_template() {
        return jc_sms_template;
    }

    public void setJc_sms_template(String jc_sms_template, boolean save) {
        this.jc_sms_template = jc_sms_template;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_JC_SMS_TEMPLATE, jc_sms_template);
        }
    }

    public String getJd_no() {
        return jd_no;
    }

    public void setJd_no(String jd_no, boolean save) {
        this.jd_no = jd_no;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_JDNO, jd_no);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address, boolean save) {
        this.address = address;
        if (save) {
            writeToAppContstant(AppConstants.ADDRESS, address);
            ApplicationSettings.putPref(AppConstants.AGENT_LOCATION, address);
        }
    }

    public String getYt_video_link() {
        return yt_video_link;
    }

    public void setYt_video_link(String yt_video_link, boolean save) {
        this.yt_video_link = yt_video_link;
        if (save) {
            writeToAppContstant(AppConstants.PROMO_VIDEO_LINK, yt_video_link);
        }
    }

    public String getLinked_link() {
        return linked_link;
    }

    public void setLinked_link(String linked_link, boolean save) {
        this.linked_link = linked_link;
        if (save) {
            writeToAppContstant(AppConstants.LINKED_IN_LINK, linked_link);
        }

    }

    public String getFacebook_link() {
        return facebook_link;
    }

    public void setFacebook_link(String facebook_link, boolean save) {
        this.facebook_link = facebook_link;
        if (save) {
            writeToAppContstant(AppConstants.FACEBOOK_LINK, facebook_link);
        }
    }

    public String getTwitter_link() {
        return twitter_link;
    }

    public void setTwitter_link(String twitter_link, boolean save) {
        this.twitter_link = twitter_link;
        if (save) {
            writeToAppContstant(AppConstants.TWITTER_LINK, twitter_link);
        }
    }


    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle, boolean save) {
        this.jobTitle = jobTitle;
        if (save) {
            writeToAppContstant(AppConstants.JOB_TITLE, jobTitle);
        }

    }

    public String getYoutubeChannelLink() {
        return youtubeChannelLink;
    }

    public void setYoutubeChannelLink(String youtubeChannelLink, boolean save) {
        this.youtubeChannelLink = youtubeChannelLink;
        if (save) {
            writeToAppContstant(AppConstants.YOUTUBE_CHANNEL_LINK, youtubeChannelLink);
        }
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website, boolean save) {
        this.website = website;
        if (save) {
            writeToAppContstant(AppConstants.WEBSITE, website);
        }
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getGoogle_map_url() {
        return google_map_url;
    }

    public void setGoogle_map_url(String google_map_url, boolean save) {
        this.google_map_url = google_map_url;
        if (save) {
            writeToAppContstant(AppConstants.MAP_URL, google_map_url);
        }
    }

    public String getGoogle_map_landmark() {
        return google_map_landmark;
    }

    public void setGoogle_map_landmark(String google_map_landmark, boolean save) {
        this.google_map_landmark = google_map_landmark;
        if (save) {
            writeToAppContstant(AppConstants.GOOGLE_MAP_LANDMARK, google_map_landmark);
        }
    }

    public String getJd_url() {
        return jd_url;
    }

    public void setJd_url(String jd_url, boolean save) {
        this.jd_url = jd_url;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_JD_URL, jd_url);
        }
    }

    public String getCustomer_testimonial_url() {
        return customer_testimonial_url;
    }

    public void setCustomer_testimonial_url(String customer_testimonial_url, boolean save) {
        this.customer_testimonial_url = customer_testimonial_url;
        if (save) {
            writeToAppContstant(AppConstants.TESTIMONIAL, customer_testimonial_url);
        }
    }

    public String getFacility_photo_url1() {
        return facility_photo_url1;
    }

    public void setFacility_photo_url1(String facility_photo_url1, boolean save) {
        this.facility_photo_url1 = facility_photo_url1;
        if (save) {
            writeToAppContstant(AppConstants.FACILITY_PHOTO_URL1, facility_photo_url1);
        }
    }

    public String getFacility_photo_url2() {
        return facility_photo_url2;
    }

    public void setFacility_photo_url2(String facility_photo_url2, boolean save) {
        this.facility_photo_url2 = facility_photo_url2;
        if (save) {
            writeToAppContstant(AppConstants.FACILITY_PHOTO_URL2, facility_photo_url2);
        }
    }

    public String getFacility_photo_url3() {
        return facility_photo_url3;
    }

    public void setFacility_photo_url3(String facility_photo_url3, boolean save) {
        this.facility_photo_url3 = facility_photo_url3;
        if (save) {
            writeToAppContstant(AppConstants.FACILITY_PHOTO_URL3, facility_photo_url3);
        }
    }

    public String getFacility_photo_url4() {
        return facility_photo_url4;
    }

    public void setFacility_photo_url4(String facility_photo_url4, boolean save) {
        this.facility_photo_url4 = facility_photo_url4;
        if (save) {
            writeToAppContstant(AppConstants.FACILITY_PHOTO_URL4, facility_photo_url4);
        }
    }

    public String getPrimary_contact() {
        return primary_contact;
    }

    public void setPrimary_contact(String primary_contact, boolean save) {
        this.primary_contact = primary_contact;
        if (save) {
            writeToAppContstant(AppConstants.PRIMARY_CONTACT, primary_contact);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password, boolean save) {
        this.password = password;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_PASSWORD, password);
        }
    }

    public String getLogin_method() {
        return login_method;
    }

    public void setLogin_method(String login_method, boolean save) {
        this.login_method = login_method;
        if (save) {
            writeToAppContstant(AppConstants.LOGIN_METHOD, login_method);
        }
    }

    public String getMyServerUrl_method() {
        return my_server_url;
    }

    public void setMyServerUrl_method(String url, boolean save) {
        this.my_server_url = url;
        if (save) {
            writeToAppContstant(AppConstants.MY_SERVER_URL, my_server_url);
        }
    }

    public String getMySioUrl_method() {
        return my_sio_url;
    }

    public void setMySioUrl_method(String url, boolean save) {
        this.my_sio_url = url;
        if (save) {
            writeToAppContstant(AppConstants.MY_SIO_URL, my_sio_url);
        }
    }

    public void setMyDashboardSioUrl_method(String url, boolean save) {
        this.my_dashboardsio_url = url;
        if (save) {
            writeToAppContstant(AppConstants.MY_DASHBOARD_SIO_URL, my_dashboardsio_url);
        }
    }

    public void loadfromAppConstants() {
        id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0");
        name = ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
        company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        phone = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
        business_logo_url = ApplicationSettings.getPref(AppConstants.USERINFO_BUSINESS_LOGO_URL, "");
        business_tagline = ApplicationSettings.getPref(AppConstants.USERINFO_BUSINESS_TAGLINE, "");
        call_sms_template = ApplicationSettings.getPref(AppConstants.USERINFO_CALL_SMS_TEMPLATE, "");
        jd_sms_template = ApplicationSettings.getPref(AppConstants.USERINFO_JD_SMS_TEMPLATE, "");
        jc_sms_template = ApplicationSettings.getPref(AppConstants.USERINFO_JC_SMS_TEMPLATE, "");
        jd_no = ApplicationSettings.getPref(AppConstants.USERINFO_JDNO, "");
        address = ApplicationSettings.getPref(AppConstants.ADDRESS, "");
        yt_video_link = ApplicationSettings.getPref(AppConstants.PROMO_VIDEO_LINK, "");

        facebook_link = ApplicationSettings.getPref(AppConstants.FACEBOOK_LINK, "");
        twitter_link = ApplicationSettings.getPref(AppConstants.TWITTER_LINK, "");
        linked_link = ApplicationSettings.getPref(AppConstants.LINKED_IN_LINK, "");

        website = ApplicationSettings.getPref(AppConstants.WEBSITE, "");
        //countrycode=
        customer_testimonial_url = ApplicationSettings.getPref(AppConstants.TESTIMONIAL, "");
        google_map_url = ApplicationSettings.getPref(AppConstants.MAP_URL, "");
        google_map_landmark = ApplicationSettings.getPref(AppConstants.GOOGLE_MAP_LANDMARK, "");
        jd_url = ApplicationSettings.getPref(AppConstants.USERINFO_JD_URL, "");
        login_method = ApplicationSettings.getPref(AppConstants.LOGIN_METHOD, "");
        facility_photo_url1 = ApplicationSettings.getPref(AppConstants.FACILITY_PHOTO_URL1, "");
        facility_photo_url2 = ApplicationSettings.getPref(AppConstants.FACILITY_PHOTO_URL2, "");
        facility_photo_url3 = ApplicationSettings.getPref(AppConstants.FACILITY_PHOTO_URL3, "");
        facility_photo_url4 = ApplicationSettings.getPref(AppConstants.FACILITY_PHOTO_URL4, "");
        password = ApplicationSettings.getPref(AppConstants.USERINFO_PASSWORD, "");
        primary_contact = ApplicationSettings.getPref(AppConstants.PRIMARY_CONTACT, "");
        callRecord_status = ApplicationSettings.getPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
        afterCallpopup = ApplicationSettings.getPref(AppConstants.SETTING_AFTER_CALL_POPUP, true); //changed by dilip
        captureIncomingSMS = ApplicationSettings.getPref(AppConstants.CAPTURE_INCOMING_SMS, false);
        book_appointment_status = ApplicationSettings.getPref(AppConstants.SETTINGS_BOOK_APPOINTMENT_STATUS, false);
        gpsLocationSettings = ApplicationSettings.getPref(AppConstants.GPS_SETTINGS, false);
        donotRecordPrivateContacts = ApplicationSettings.getPref(AppConstants.DONT_CALL_RECORD_PRIVATE_CONTACTS, false);
        recordOnlyUnknownNumbers = ApplicationSettings.getPref(AppConstants.ONLY_CALL_RECORD_UNKNOWN_NUMBERS, false);
        recordOnlyLeadNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_LEAD_CONTACTS, true);
        recordOnlyTeamMemberNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_TEAM_MEMBERS_NUMBERS, false);
        recordOnlyCloudTelephonyNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_CLOUND_TELEPHONY_NUMBERS, false);
        enquiryResponseOn = ApplicationSettings.getPref(AppConstants.SETTING_JD_CHECK, false);
        office365_token = ApplicationSettings.getPref(AppConstants.OFFICE365_ACCESS_TOKEN, "");
        payment_status = ApplicationSettings.getPref(AppConstants.USERINFO_PAYMENT_STATUS, "");
        accessToken = ApplicationSettings.getPref(AppConstants.ACCESS__TOKEN, "");
        my_server_url = ApplicationSettings.getPref(AppConstants.MY_SERVER_URL, "");
        my_sio_url = ApplicationSettings.getPref(AppConstants.MY_SIO_URL, "");

        /*Load to Constants*/
        if (my_server_url != null && !(my_server_url.isEmpty()) && (my_server_url != " ") && !(my_server_url.equalsIgnoreCase("null"))) {
            Urls.SERVER_ADDRESS = my_server_url;
        } else {
            Urls.SERVER_ADDRESS = Urls.PERMANENT_SERVER_ADDRESS;
        }
        Constants.DOMAIN_URL = Urls.SERVER_ADDRESS + "/";

        if (my_sio_url != null && !(my_sio_url.isEmpty()) && (my_sio_url != " ") && !(my_sio_url.equalsIgnoreCase("null"))) {
            Urls.SERVER_SIO_ADDRESS = my_sio_url;
        } else {
            Urls.SERVER_SIO_ADDRESS = Urls.PERMANENT_SIO_ADDRESS;
        }

        //members;
        //lat;
        //longi;
        //send_map_location;
        //call_recording_auto;
        //call_recording_status;
        //created_at;
        //updated_at;
        //password;
        //secondary_users;
        //payment_status;
        //address1;
        //address2;
        //yt_video_link2;
        //yt_audio_link;
        //yt_audio_link2;
        //confirmation_code;
        //user_status;
        //countrycode;
        //facility_photo_url;
        //map_url
        //report_enabled;
        //report_email;
        //login_method;
        //company_alias;
    }

    public static SmartUser getLocaleUser() {
        SmartUser smartUser = new SmartUser();
        smartUser.loadfromAppConstants();
        return smartUser;
    }

    public void doSave() {
        setId(id, true);
        setName(name, true);
        setCompany(company, true);
        setEmail(email, true);
        setPhone(phone, true);
        setBusiness_logo_url(business_logo_url, true);
        setBusiness_tagline(business_tagline, true);
        setCall_sms_template(call_sms_template, true);
        setJd_sms_template(jd_sms_template, true);
        setbusiness_template(business_template, true);
        setJc_sms_template(jc_sms_template, true);
        setPassword(password, true);
        setJd_no(jd_no, true);
        setAddress(address, true);
        setYt_video_link(yt_video_link, true);
        setWebsite(website, true);
        setCustomer_testimonial_url(customer_testimonial_url, true);
        setGoogle_map_url(google_map_url, true);
        setGoogle_map_landmark(google_map_landmark, true);
        setJd_url(jd_url, true);
        setLogin_method(login_method, true);
        setFacility_photo_url1(facility_photo_url1, true);
        setFacility_photo_url2(facility_photo_url2, true);
        setFacility_photo_url3(facility_photo_url3, true);
        setFacility_photo_url4(facility_photo_url4, true);
        setPrimary_contact(primary_contact, true);
        //setCountrycode();
        setCallRecordStatus(this.callRecord_status);
        setBookAppointment(book_appointment_status);
        setAfterCallpopup(this.afterCallpopup);
        setCaptureIncomingSMS(this.captureIncomingSMS);
        setDonotRecordPrivateContacts(this.donotRecordPrivateContacts);
        setRecordOnkyUnknownNumbers(this.recordOnlyUnknownNumbers);
        setRecordOnlyLeadNumbers(this.recordOnlyLeadNumbers);

        setLinked_link(linked_link, true);
        setFacebook_link(facebook_link, true);
        setTwitter_link(twitter_link, true);
        setGpsLocationSettings(this.gpsLocationSettings);
        setOffice365_token(office365_token);
        setEnquiryResponseOn(ApplicationSettings.getPref(AppConstants.SETTING_JD_CHECK, false));
        setPayment_status(payment_status, true);
        setOffice365_token(office365_token);
        setAccessToken(accessToken);
        setProfileUrl(profileUrl);
        setMyServerUrl_method(my_server_url,true);
        setMySioUrl_method(my_sio_url,true);
        setMyDashboardSioUrl_method(my_dashboardsio_url,true);
    }

    public void writeToAppContstant(String property, String value) {
        ApplicationSettings.putPref(property, value);
    }

    public boolean getSend_map_location() {
        return send_map_location;
    }

    public void setSend_map_location(boolean value) {
        send_map_location = value;
    }

    public boolean getAutoSmsForMissedCalls() {
        return autoSmsForMissedCalls;
    }

    public void setAutoSMSForMissedCalls(boolean value) {
        autoSmsForMissedCalls = value;
        ApplicationSettings.putPref(AppConstants.AUTOSMS_FOR_MISSED_CALLS, value);
    }

    public void setReport_email(String report_email) {
        this.report_email = report_email;
    }

    public String getOffice365_token() {
        return office365_token;
    }

    public void setOffice365_token(String value) {
        office365_token = value;
        ApplicationSettings.putPref(AppConstants.OFFICE365_ACCESS_TOKEN, value);
    }

    public void setPayment_status(String payment_status, boolean save) {
        this.payment_status = payment_status;
        if (save) {
            writeToAppContstant(AppConstants.USERINFO_PAYMENT_STATUS, payment_status);
        }
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        ApplicationSettings.putPref(AppConstants.ACCESS__TOKEN, accessToken);
    }

    public void setUserRole(String role) {
        this.role = role;
        ApplicationSettings.putPref(AppConstants.USER_ROLE, role);
    }

    public String getUserRole() {
        return role;
    }


    public void setUserLocation(String locality) {
        this.locality = locality;
        ApplicationSettings.putPref(AppConstants.AGENT_LOCATION, locality);
    }

    public String getUserLocation() {
        return locality;
    }

    public void setUserDob(String dob) {
        this.dob = dob;
        ApplicationSettings.putPref(AppConstants.AGENT_DOB, dob);
    }

    public String getUserDob() {
        return dob;
    }

    public void setUserSpokenLang(String spokenLang) {
        this.lang = spokenLang;
        ApplicationSettings.putPref(AppConstants.AGENT_SPOKEN_LANG, spokenLang);
    }

    public String getUserSpokenLang() {
        return lang;
    }









    public boolean getGpsLocationSettings() {
        return gpsLocationSettings;
    }

    public void setGpsLocationSettings(boolean gpsLocationSettings) {
        this.gpsLocationSettings = gpsLocationSettings;
        ApplicationSettings.putPref(AppConstants.GPS_SETTINGS, gpsLocationSettings);
    }

    // Dilip kumar modifications
    public boolean getAutoAccepteLeadsSettings() {
        return autoAcceptSettings;
    }

    public void setAutoAccepteLeadsSettings(boolean autoAcceptLeadsSettings) {
        this.autoAcceptSettings = autoAcceptLeadsSettings;
        ApplicationSettings.putPref(AppConstants.AUTO_ACCEPT_LEADS_SETTINGS, autoAcceptLeadsSettings);
    }

    public boolean getCallTrackingSettings() {
        return callTrackingAll;
    }

    public void setCallTrackingSettings(boolean callTrackingAllSettings) {
        this.callTrackingAll = callTrackingAllSettings;
        ApplicationSettings.putPref(AppConstants.CALL_TRACKING_ALL_SETTINGS, callTrackingAllSettings);
    }

    public boolean getFollowUpCallsSettings() {
        return followUpSettings;
    }

    public void setFollowUpCallsSettings(boolean followUpCallsSettings) {
        this.followUpSettings = followUpCallsSettings;
        ApplicationSettings.putPref(AppConstants.CALL_TRACKING_ALL_SETTINGS, followUpCallsSettings);
    }

    public boolean getTeamCallSettings() {
        return teamCallSettings;
    }

    public void setTeamCallSettings(boolean teamCallSettings) {
        this.teamCallSettings = teamCallSettings;
        ApplicationSettings.putPref(AppConstants.TEAM_CALLS_SETTINGS, teamCallSettings);
    }

    public boolean getSmsTrackingSettings() {
        return smsTrackingSettings;
    }

    public void setSmsTrackingSettings(boolean smsTrackSettings) {
        this.smsTrackingSettings = smsTrackSettings;
        ApplicationSettings.putPref(AppConstants.SMS_TRACKING_SETTINGS, smsTrackSettings);
    }

    public boolean getAutoFollowUpSettings() {
        return autoFollowUpsSettings;
    }

    public void setAutoFollowUpSettings(boolean autoFollowUp) {
        this.autoFollowUpsSettings = autoFollowUp;
        ApplicationSettings.putPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, autoFollowUp);
    }

    public boolean getApplicationLogging() {
        return applicationLoggingSettings;
    }

    public void setApplicationLogging(boolean applicationLogging) {
        this.applicationLoggingSettings = applicationLogging;
        ApplicationSettings.putPref(AppConstants.APPLICATION_LOGGING, applicationLogging);
    }

    public boolean getSmsTrackingAll() {
        return smsTrackingAll;
    }

    public void setSmsTrackingAll(boolean smsTrackingAll) {
        this.smsTrackingAll = smsTrackingAll;
        ApplicationSettings.putPref(AppConstants.SMS_TRACKING_ALL, smsTrackingAll);
    }

    public boolean getSmsTrackingLeads() {
        return smstrackingleads;
    }

    public void setSmsTrackingLeads(boolean smstrackingleads) {
        this.smstrackingleads = smstrackingleads;
        ApplicationSettings.putPref(AppConstants.SMS_TRACKING_LEADS, smstrackingleads);
    }

    public boolean getSmsTrackingTeam() {
        return smstrackingteam;
    }

    public void setSmsTrackingTeam(boolean smstrackingteam) {
        this.smstrackingteam = smstrackingteam;
        ApplicationSettings.putPref(AppConstants.SMS_TRACKING_TEAM, smstrackingteam);
    }

    public boolean getSmsTrackingPersonal() {
        return smstrackingpersonal;
    }

    public void setSmsTrackingPersonal(boolean smstrackingpersonal) {
        this.smstrackingpersonal = smstrackingpersonal;
        ApplicationSettings.putPref(AppConstants.SMS_TRACKING_PRIVATE, smstrackingpersonal);
    }

    public void setAppUpdated(boolean appUpd) {
        this.appUpdated = appUpd;
        ApplicationSettings.putPref(AppConstants.APP_UPDATE, appUpdated);
    }

    public boolean getAppUpdated() {
        return appUpdated;
    }

    public boolean getEditSettings() {
        return editSettings;
    }

    public void setEditSettings(boolean settings) {
        this.editSettings = settings;
        ApplicationSettings.putPref(AppConstants.CAN_EDIT_SETTINGS, settings);
    }

    public boolean getRecordOnlyCloudTelephonyNumbers() {
        return recordOnlyCloudTelephonyNumbers;
    }

    public void setRecordOnlyCloudTelephonyNumbers(boolean recordOnlyCloudTelephonyNumbers) {
        this.recordOnlyCloudTelephonyNumbers = recordOnlyCloudTelephonyNumbers;
        ApplicationSettings.putPref(AppConstants.ONLY_RECORD_CLOUND_TELEPHONY_NUMBERS, recordOnlyCloudTelephonyNumbers);
    }

    public boolean getRecordOnlyTeamMemberNumbers() {
        return recordOnlyTeamMemberNumbers;
    }

    public void setRecordOnlyTeamMemberNumbers(boolean recordOnlyTeamMemberNumbers) {
        this.recordOnlyTeamMemberNumbers = recordOnlyTeamMemberNumbers;
        ApplicationSettings.putPref(AppConstants.ONLY_RECORD_TEAM_MEMBERS_NUMBERS, recordOnlyTeamMemberNumbers);
    }

    public void setGroupId(int groupId) {
        ApplicationSettings.putPref(AppConstants.GROUP_ID, groupId);
    }

    public static int getGroupId() {
        return ApplicationSettings.getPref(AppConstants.GROUP_ID, 0);
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
        ApplicationSettings.putPref(AppConstants.APP_PROFILE_URL, profileUrl);
    }

    public String getProfileUrl() {
        String profile = "";
        if(ApplicationSettings.containsPref(AppConstants.APP_PROFILE_URL)) {
            if( ApplicationSettings.getPref(AppConstants.APP_PROFILE_URL, "") != null) {
                profile = ApplicationSettings.getPref(AppConstants.APP_PROFILE_URL, "");
            }
        }
        return profile;
    }

    public void setJunkCallSetting(boolean junkCallSetting) {
        this.junkCallSetting = junkCallSetting;
        ApplicationSettings.putPref(AppConstants.JUNKCALL_SETTING, junkCallSetting);
    }

    public static boolean getJunkCallSetting() {
        return ApplicationSettings.getPref(AppConstants.JUNKCALL_SETTING, false);
    }

    public void setPersonalcallSetting(boolean personalcallSetting) {
        this.personalcallSetting = personalcallSetting;
        ApplicationSettings.putPref(AppConstants.PERSONALCALL_SETTING, personalcallSetting);
    }

    public static boolean getPersonalCallSettings() {
        return ApplicationSettings.getPref(AppConstants.PERSONALCALL_SETTING, false);
    }

    public void setrnrReminder(int rnrReminder) {
        this.rnrReminder = rnrReminder;
        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, rnrReminder);
    }

    public static int getrnrreminderSettings() {
        return ApplicationSettings.getPref(AppConstants.RNR_REMINDER, 0);
    }

    public void setRnrmax(int rnrmax) {
        this.rnrMax = rnrmax;
        ApplicationSettings.putPref(AppConstants.RNR_CALLBACKS_NO, rnrmax);
    }

    public int getRnrMax() {
        return ApplicationSettings.getPref(AppConstants.RNR_CALLBACKS_NO, 0);
    }

    public boolean getCloudOutgoing() {
        return ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false);
    }

    public void setCloudOutgoing(boolean cloudOutgoing) {
        this.cloudOutgoing = cloudOutgoing;
        ApplicationSettings.putPref(AppConstants.CLOUD_OUTGOING, cloudOutgoing);
    }

    public boolean getAutoDailer() {
        return ApplicationSettings.getPref(AppConstants.AUTO_DAILER, false);
    }

    public void setAutoDailer(boolean autoDailer) {
        this.autoDailer = autoDailer;
        ApplicationSettings.putPref(AppConstants.AUTO_DAILER, autoDailer);
    }

    public void setTranscriptLang(String transcriptLang) {
        ApplicationSettings.putPref(AppConstants.TRANSCRIPT_LANG, transcriptLang);
    }

    public String getTranscriptLang() {
        return ApplicationSettings.getPref(AppConstants.TRANSCRIPT_LANG, "");
    }

    public void setTranscriptAudioFormate(String transcriptAudioFormate) {
        ApplicationSettings.putPref(AppConstants.TRANSCRIPT_AUDIO_FORMATE, transcriptAudioFormate);
    }

    public String getTranscriptAudioFormate() {
        return ApplicationSettings.getPref(AppConstants.TRANSCRIPT_AUDIO_FORMATE, "");
    }

    public void setTranscriptAudioEncodeFormate(String transcriptAudioEncodeFormate) {
        ApplicationSettings.putPref(AppConstants.TRANSCRIPT_AUDIO_ENCODE_FORMATE, transcriptAudioEncodeFormate);
    }

    public String getTranscriptAudioEncodeFormate() {
        return ApplicationSettings.getPref(AppConstants.TRANSCRIPT_AUDIO_ENCODE_FORMATE, "");
    }

    public void setRnrDuration(String rnrDuration) {
        ApplicationSettings.putPref(AppConstants.RNR_DURATION, rnrDuration);
    }

    public String getRnrDuration() {
        return ApplicationSettings.getPref(AppConstants.RNR_DURATION, "");
    }

    public void setFlpDuration(String flpDuration) {
        ApplicationSettings.putPref(AppConstants.FLP_DURATION, flpDuration);
    }

    public String getFlpDuration() {
        return ApplicationSettings.getPref(AppConstants.FLP_DURATION, "");
    }


    public void setSipCalls(boolean sipCalls) {
        ApplicationSettings.putPref(AppConstants.SIP_CALLS, sipCalls);
    }

    public boolean getSipCalls() {
        return ApplicationSettings.getPref(AppConstants.SIP_CALLS, false);
    }

    public boolean getTranscription() {
        return ApplicationSettings.getPref(AppConstants.TRANSCRIPTION, false);
    }

    public void setTranscription(boolean transcription) {
        ApplicationSettings.putPref(AppConstants.TRANSCRIPTION, transcription);
    }

    public void setMobiledataonsim(boolean mobiledataonsim) {
        ApplicationSettings.putPref(AppConstants.MOBILE_DATA_ON_SIM, mobiledataonsim);
    }

    public void setWorkDay1(String work1) {
        ApplicationSettings.putPref(AppConstants.WORK_DOW1, work1);
    }

    public void setWorkDay2(String work2) {
        ApplicationSettings.putPref(AppConstants.WORK_DOW1, work2);
    }

    public String getWorkDay1() {
        return ApplicationSettings.getPref(AppConstants.WORK_DOW1, "");
    }

    public String getWorkDay2() {
        return ApplicationSettings.getPref(AppConstants.WORK_DOW2, "");
    }

    public void setOpenTime(String openTime) {
        ApplicationSettings.putPref(AppConstants.OPEN_TIME, openTime);
    }

    public void setCloseTime(String closeTime) {
        ApplicationSettings.putPref(AppConstants.CLOSE_TIME, closeTime);
    }

    public String getOpenTime() {
        String open = ApplicationSettings.getPref(AppConstants.OPEN_TIME, "");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int[] arr = new int[2];
        if (open != null && !(open.isEmpty())) {
            arr = CommonUtils.getNumbers(open);
        }
        if(arr != null && arr.length > 0) {
            String dateAsString = "03:30";
            try {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), arr[0], arr[1]);
                Date time = cal.getTime();
                dateAsString = CommonUtils.getTimeFormatInUTC(time);
            } catch (Exception e) {
                e.printStackTrace();
                dateAsString = "03:30";
            }
            return dateAsString;
        } else {
            return "03:30";
        }
    }

    public String getCloseTime() {
        String close =  ApplicationSettings.getPref(AppConstants.CLOSE_TIME, "");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int[] arr = new int[2];
        if (close != null && !(close.isEmpty())) {
            arr = CommonUtils.getNumbers(close);
        }
        if(arr != null && arr.length > 0) {
            String dateAsString = "13:30";
            try {
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), arr[0], arr[1]);
                Date time = cal.getTime();
                dateAsString = CommonUtils.getTimeFormatInUTC(time);
            } catch (Exception e) {
                e.printStackTrace();
                dateAsString = "13:30";
            }
            return dateAsString;
        } else {
            return "13:30";
        }
    }

    public void setAfterCallActivityScreen(String screen) {
        ApplicationSettings.putPref(AppConstants.AFTERCALLACTIVITY_SCREEN, screen);
    }

    public String getAfterCallScreen() {
        return ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
    }

    public void setHelpline(String helpline) {
        ApplicationSettings.putPref(AppConstants.HELP_LINE, helpline);
    }


    public String getCallEndDuration() {
        return callend_duration;
    }

    public void setCallEndDuration(String duration) {
        callend_duration = duration;
        ApplicationSettings.putPref(AppConstants.CALL_END_DURATION, duration);
    }

    public void setC2cEndPoint(String endpoint) {
        ApplicationSettings.putPref(AppConstants.C2C_ENDPOINT, endpoint);
    }








    public String getC2cEndPoint() {
        if(ApplicationSettings.containsPref(AppConstants.C2C_ENDPOINT)) {
            return ApplicationSettings.getPref(AppConstants.C2C_ENDPOINT, "");
        } else {
            return "http://etsrds.kapps.in/webapi/smarterbiz/api/smarterbiz_c2c.py";
        }
    }

    public void setSmbOffline(boolean offline) {
        ApplicationSettings.putPref(AppConstants.SMB_OFFLINE, offline);
    }

    public boolean getSmbOffline() {
        return ApplicationSettings.getPref(AppConstants.SMB_OFFLINE, false);
    }

    public String getHelpLine() {
        if(ApplicationSettings.containsPref(AppConstants.HELP_LINE)) {
            return ApplicationSettings.getPref(AppConstants.HELP_LINE, "");
        } else {
            return "";
        }
    }

    public void setC2cTimeOut(String c2cTimeOut) {
        ApplicationSettings.putPref(AppConstants.C2C_TIMEOUT, c2cTimeOut);
    }

    public void setUnscheduledCall(boolean unscheduledCall) {
        ApplicationSettings.putPref(AppConstants.UNSCHEDULED_CALL, unscheduledCall);
    }

    public boolean getUnscheduledCall() {
        return ApplicationSettings.getPref(AppConstants.UNSCHEDULED_CALL, false);
    }
    /*
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getCall_recording_auto() {
        return call_recording_auto;
    }

    public void setCall_recording_auto(String call_recording_auto) {
        this.call_recording_auto = call_recording_auto;
    }

    public String getCall_recording_status() {
        return call_recording_status;
    }

    public void setCall_recording_status(String call_recording_status) {
        this.call_recording_status = call_recording_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getSecondary_users() {
        return secondary_users;
    }


    public void setSecondary_users(String secondary_users) {
        this.secondary_users = secondary_users;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }*/

    /*public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }*/

    /*public String getYt_video_link2() {
        return yt_video_link2;
    }

    public void setYt_video_link2(String yt_video_link2) {
        this.yt_video_link2 = yt_video_link2;
    }

    public String getYt_audio_link() {
        return yt_audio_link;
    }

    public void setYt_audio_link(String yt_audio_link) {
        this.yt_audio_link = yt_audio_link;
    }

    public String getYt_audio_link2() {
        return yt_audio_link2;
    }

    public void setYt_audio_link2(String yt_audio_link2) {
        this.yt_audio_link2 = yt_audio_link2;
    }

    public String getConfirmation_code() {
        return confirmation_code;
    }

    public void setConfirmation_code(String confirmation_code) {
        this.confirmation_code = confirmation_code;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }*/

    /*public String getFacility_photo_url() {
        return facility_photo_url;
    }

    public void setFacility_photo_url(String facility_photo_url) {
        this.facility_photo_url = facility_photo_url;
    }*/

    /*public String getMap_url() {
        return map_url;
    }

    public void setMap_url(String map_url) {
        this.map_url = map_url;
    }*/

    /*public String getReport_enabled() {
        return report_enabled;
    }

    public void setReport_enabled(String report_enabled) {
        this.report_enabled = report_enabled;
    }

    public String getLogin_method() {
        return login_method;
    }

    public void setLogin_method(String login_method) {
        this.login_method = login_method;
    }

    public String getCompany_alias() {
        return company_alias;
    }

    public void setCompany_alias(String company_alias) {
        this.company_alias = company_alias;
    }

     public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }*/

    public  void  setUseCampaign(String screen) {
        ApplicationSettings.putPref(AppConstants.USE_CAMPAIGN, screen);
    }

    public String getUseCampaign() {
        return ApplicationSettings.getPref(AppConstants.USE_CAMPAIGN, "");
    }

    public void setShownCallCampaign (String screen) {
        ApplicationSettings.putPref(AppConstants.SHOWON_CALL_CAMPAIGN, screen);
    }

    public String getShownCallCampaign () {
        return ApplicationSettings.getPref(AppConstants.SHOWON_CALL_CAMPAIGN, "");
    }

    public void setOnCallAct (String screen) {
        ApplicationSettings.putPref(AppConstants.ON_CALL_ACT, screen);
    }

    public String getOnCallAct () {
        return ApplicationSettings.getPref(AppConstants.ON_CALL_ACT, "");
    }

    public void setQuestionAnswer(String questionnaire) {
        ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
        ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE_FROM_SETTINGS, questionnaire);
    }

    public String getQuestionAnswer() {
        return ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
    }

    public void setACPData(String acpData) {
        ApplicationSettings.putPref(AppConstants.ACP_DATA, acpData);
    }

    public String getACPData() {
        return ApplicationSettings.getPref(AppConstants.ACP_DATA, "");
    }

    public void setDelayACPopup(String delayACPopup) {
        ApplicationSettings.putPref(AppConstants.DELAY_AC_POPUP, delayACPopup);
    }

    public String getDelayACPopup() {
        return ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
    }

    public void setDoneListQuery(String doneListQuery) {
        ApplicationSettings.putPref(AppConstants.DONE_LIST_QUERY, doneListQuery);
    }

    public String getDoneListQuery() {
        return ApplicationSettings.getPref(AppConstants.DONE_LIST_QUERY, "");
    }

    public void setDoneCardQuery(String doneCardQuery) {
        ApplicationSettings.putPref(AppConstants.DONE_CARD_QUERY, doneCardQuery);
    }

    public String getDoneCardQuery() {
        return ApplicationSettings.getPref(AppConstants.DONE_CARD_QUERY, "");
    }

    public void setAutoSync(String autoSync) {
        ApplicationSettings.putPref(AppConstants.AUTO_SYNC, autoSync);
    }

    public void setTruncateReminderTable(String truncate) {
        ApplicationSettings.putPref(AppConstants.TRUNCATE_REMINDER_TABLE, truncate);
    }

    public String getAutoSync() {
        return ApplicationSettings.getPref(AppConstants.AUTO_SYNC, "");
    }

    public void setQuestionsAct(String questionsAct) {
        ApplicationSettings.putPref(AppConstants.QUESTIONS_ACT, questionsAct);
    }

    public String getQuestionsAct() {
        return ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
    }

    public void setNumberOfCustomersToCall(String noOfCustomers) {
        ApplicationSettings.putPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, noOfCustomers);
    }

    public String getNumberOfCustomersToCall() {
        return ApplicationSettings.getPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, "");
    }

    public void setIsPromo(String isPromo) {
        ApplicationSettings.putPref(AppConstants.IS_PROMO, isPromo);
    }

    public String getIsPromo() {
        return ApplicationSettings.getPref(AppConstants.IS_PROMO, "");
    }

    public void setParallelGroupSize(String parallelGroupSize) {
        ApplicationSettings.putPref(AppConstants.PARALLEL_GROUP_SIZE, parallelGroupSize);
    }

    public String getParallelGroupSize() {
        return ApplicationSettings.getPref(AppConstants.PARALLEL_GROUP_SIZE, "");
    }

    public void setAllowAllIncoming(String allowAllIncoming) {
        ApplicationSettings.putPref(AppConstants.ALLOW_ALL_INCOMING, allowAllIncoming);
    }

    public String getAllowAllIncoming() {
        return ApplicationSettings.getPref(AppConstants.ALLOW_ALL_INCOMING, "");
    }

    public void setAllowAllOutgoing(String allowAllOutgoing) {
        ApplicationSettings.putPref(AppConstants.ALLOW_ALL_OUTGOING, allowAllOutgoing);
    }

    public String getAllowAllOutgoing() {
        return ApplicationSettings.getPref(AppConstants.ALLOW_ALL_OUTGOING, "");
    }
    public void setRemoteDialling(String remoteDialAlert) {
        ApplicationSettings.putPref(AppConstants.REMOTE_AUTO_DIALLING, remoteDialAlert);
    }

    public void setSioPushUrl(String pushurl) {
        ApplicationSettings.putPref(AppConstants.SIOPUSH_URL, pushurl);
    }

    public void setOptimizedPost(String optimized) {
        ApplicationSettings.putPref(AppConstants.RAD_OPTIMIZED_POST, optimized);
    }

    public void setRemoteDiallingStartAlert(String remoteDialAlert) {
        ApplicationSettings.putPref(AppConstants.REMOTE_DIALLING_START_ALERT, remoteDialAlert);
    }

    public void setRemoteDiallingStopAlert(String remoteDialAlert) {
        ApplicationSettings.putPref(AppConstants.REMOTE_DIALLING_STOP_ALERT, remoteDialAlert);
    }

    public void setDefaultHomePage(String homepage) {
        ApplicationSettings.putPref(AppConstants.DEFAULT_HOME_PAGE, "");
    }

    public void setDefaultHomePageWebView(String homepageWebView) {
        ApplicationSettings.putPref(AppConstants.DEFAULT_HOME_PAGE_WEB_VIEW, homepageWebView);
    }

    public void setUserPositivePreferences(String pref) {
        ApplicationSettings.putPref(AppConstants.USER_POSITIVE_PREFERENCES, pref);
    }

    public void setUserNegativePreferences(String pref) {
        ApplicationSettings.putPref(AppConstants.USER_NEGATIVE_PREFERENCES, pref);
    }

    public void setNewDataString(String newDataString) {
        ApplicationSettings.putPref(AppConstants.NEW_DATA_STRING, newDataString);
    }

    public String getNewDataString() {
        return ApplicationSettings.getPref(AppConstants.NEW_DATA_STRING, "");
    }

    public void setFollowUpString(String followUpString) {
        ApplicationSettings.putPref(AppConstants.FOLLOW_UP_STRING, followUpString);
    }

    public String getFollowUpString() {
        return ApplicationSettings.getPref(AppConstants.FOLLOW_UP_STRING, "");
    }

    public void setSIODisabled(String sioDisabled) {
        ApplicationSettings.putPref(AppConstants.SIO_DISABLED, sioDisabled);
    }

    public String getSIODisabled() {
        return ApplicationSettings.getPref(AppConstants.SIO_DISABLED, "");
    }

    public void setIsReuploadEnabled(String isReuploadEnabled) {
        ApplicationSettings.putPref(AppConstants.IS_REUPLOAD_ENABLED, isReuploadEnabled);
    }

    public String getIsReuploadEnabled() {
        return ApplicationSettings.getPref(AppConstants.IS_REUPLOAD_ENABLED, "");
    }

    public void setFieldsToShow(String fieldsToShow) {
        ApplicationSettings.putPref(AppConstants.FIELDS_TO_SHOW, fieldsToShow);
    }

    public String getFieldsToShow() {
        return ApplicationSettings.getPref(AppConstants.FIELDS_TO_SHOW, "");
    }

    public boolean getShowAfterCallPopup() {
        return showAfterCallPopup;
    }

    public void setShowAfterCallPopup(boolean showAfterCallPopup) {
        this.showAfterCallPopup = showAfterCallPopup;
        ApplicationSettings.putPref(AppConstants.SHOW_AFTER_CALL_POPUP, showAfterCallPopup);
    }

    public boolean getAppendLeadSource() {
        return appendLeadSource;
    }

    public void setAppendLeadSource(boolean appendLeadSource) {
        this.appendLeadSource = appendLeadSource;
        ApplicationSettings.putPref(AppConstants.APPEND_LEAD_SOURCE, appendLeadSource);
    }

    public void setLaunchApp(String launchApp) {
        ApplicationSettings.putPref(AppConstants.LAUNCH_APP, launchApp);
    }

    public String getLaunchApp() {
        return ApplicationSettings.getPref(AppConstants.LAUNCH_APP, "");
    }

    public boolean getUploadCmail() {
        return uploadCmail;
    }

    public void setUploadCmail(boolean uploadCmail) {
        this.uploadCmail = uploadCmail;
        ApplicationSettings.putPref(AppConstants.UPLOAD_CMAIL, uploadCmail);
    }

    public void setFollowupEditable(boolean value) {
        ApplicationSettings.putPref(AppConstants.IS_FOLLOWUP_EDITABLE, value);
        this.isFollowup_edit = value;
    }

    public boolean getFollowupEditable() {
        return isFollowup_edit;
    }

    public boolean getServerAptUpdates() {
        return serveraptupdates;
    }

    public void setServerAptUpdates(boolean serveraptupdates) {
        this.serveraptupdates = serveraptupdates;
        ApplicationSettings.putPref(AppConstants.SERVER_APT_UPDATES, serveraptupdates);
    }

    public boolean getCallBackFollowup() {
        return ApplicationSettings.getPref(AppConstants.CALLBACK_FOLLOWUP, false);
    }

    public void setCallBackFollowup(boolean callBackFollowup) {
        ApplicationSettings.putPref(AppConstants.CALLBACK_FOLLOWUP, callBackFollowup);
    }

    public void setShowCustomerNumber(boolean showCustomerNumber) {
        ApplicationSettings.putPref(AppConstants.CUSTOMER_NUMBER_STATUS, showCustomerNumber);
    }

    public boolean getCustomerQues() {
        return customerQues;
    }

    public void setCustomerQues(boolean customerQues) {
        this.customerQues = customerQues;
        ApplicationSettings.putPref(AppConstants.CUSTOMER_QUES, customerQues);
    }

    public boolean getTruePredictive() {
        return truePredictive;
    }

    public void setTruePredictive(boolean truePredictive) {
        this.truePredictive = truePredictive;
        ApplicationSettings.putPref(AppConstants.TRUE_PREDICTIVE, truePredictive);
    }

    public void setRADC2CEndPoint(String endpoint) {
        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }
        if(company != null && !company.isEmpty() && (company.equals("OYO") || company.equals("MMT"))) {

        } else {
//            ApplicationSettings.putPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, endpoint);
//            if (endpoint != null && !endpoint.isEmpty() && (endpoint.contains("sequencial") || endpoint.contains("hybrid") || endpoint.contains("predictive"))) {
//                ApplicationSettings.putPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, true);
//                if (endpoint.contains("hybrid") || endpoint.contains("predictive")) {
//                    ApplicationSettings.putPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, true);
//                }
//            } else {
//                ApplicationSettings.putPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
//            }
        }
    }

    public String getRADC2CEndPoint() {
        if(ApplicationSettings.containsPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT)) {
            return ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, "");
        } else {
            return "http://etsrds.kapps.in/webapi/smarterbiz/api/smarterbiz_c2c.py";
        }
    }

    public boolean getShowAlert() {
        return showAlert;
    }

    public void setShowAlert(boolean showAlert) {
        this.showAlert = showAlert;
        ApplicationSettings.putPref(AppConstants.SHOW_ALERT, showAlert);
    }

    public void setWrapupTime(String wrapupTime) {
        ApplicationSettings.putPref(AppConstants.WRAPUP_TIME, wrapupTime);
    }

    public String getWrapupTime() {
        return ApplicationSettings.getPref(AppConstants.WRAPUP_TIME, "");
    }

    public boolean getShowTimer() {
        return showTimer;
    }

    public void setShowTimer(boolean showTimer) {
        this.showTimer = showTimer;
        ApplicationSettings.putPref(AppConstants.SHOW_TIMER, showTimer);
    }

    public boolean getBackToNetwork() {
        return backToNetwork;
    }

    public void setBackToNetwork(boolean backToNetwork) {
        this.backToNetwork = backToNetwork;
        ApplicationSettings.putPref(AppConstants.BACK_TO_NETWORK, backToNetwork);
    }

    public boolean getDoneRefresh() {
        return doneRefresh;
    }

    public void setDoneRefresh(boolean doneRefresh) {
        this.doneRefresh = doneRefresh;
        ApplicationSettings.putPref(AppConstants.DONE_REFRESH, doneRefresh);
    }

    public boolean getUploadStatus2() {
        return uploadStatus2;
    }

    public void setUploadStatus2(boolean uploadStatus2) {
        this.uploadStatus2 = uploadStatus2;
        ApplicationSettings.putPref(AppConstants.UPLOAD_STATUS2, uploadStatus2);
    }

    public boolean getCustomerInfoAck() {
        return customerInfoAck;
    }

    public void setCustomerInfoAck(boolean customerInfoAck) {
        this.customerInfoAck = customerInfoAck;
        ApplicationSettings.putPref(AppConstants.CUSTOMER_INFO_ACK, customerInfoAck);
    }

    public void setTestNumber(String testNumber) {
        ApplicationSettings.putPref(AppConstants.TEST_NUMBER, testNumber);
    }

    public String getTestNumber() {
        return ApplicationSettings.getPref(AppConstants.TEST_NUMBER, "");
    }

    public void setEnableECB(String enableECBTime) {
        ApplicationSettings.putPref(AppConstants.ENABLE_ECB, enableECBTime);
    }

    public boolean getEmulationOn() {
        return isEmulationOn;
    }

    public void setEmulationOn(boolean emulationOn) {
        this.isEmulationOn = emulationOn;
        ApplicationSettings.putPref(AppConstants.ENABLE_EMULATION, emulationOn);
    }

    public String getEnableECB() {
        return ApplicationSettings.getPref(AppConstants.ENABLE_ECB, "");
    }

    public void setEnableMessage(String enableMessage) {
        ApplicationSettings.putPref(AppConstants.ENABLE_MESSAGE, enableMessage);
    }

    public String getEnableMessage() {
        return ApplicationSettings.getPref(AppConstants.ENABLE_MESSAGE, "");
    }

    public boolean getAllowScreenshot() {
        return allowScreenshot;
    }

    public void setAllowScreenshot(boolean allowScreenshot) {
        this.allowScreenshot = allowScreenshot;
        ApplicationSettings.putPref(AppConstants.ALLOW_SCREENSHOT, allowScreenshot);
    }

    public boolean getSystemControl() {
        return systemcontrol;
    }

    public void setSystemControl(boolean systemcontrol) {
        this.systemcontrol = systemcontrol;
        ApplicationSettings.putPref(AppConstants.SYSTEM_CONTROL, systemcontrol);
    }

    public boolean getSSUOnSio() {
        return ssuonsio;
    }

    public void setSSUOnSio(boolean ssuonsio) {
        this.ssuonsio = ssuonsio;
        ApplicationSettings.putPref(AppConstants.SSU_ON_SIO, ssuonsio);
    }

    public boolean getFKControl() {
        return fkcontrol;
    }

    public void setFKControl(boolean fkcontrol) {
        this.fkcontrol = fkcontrol;
        ApplicationSettings.putPref(AppConstants.FK_CONTROL, fkcontrol);
    }

    public void setConnectingText(String connectingText) {
        ApplicationSettings.putPref(AppConstants.CONNECTING_TEXT, connectingText);
    }

    public String getConnectingText() {
        return ApplicationSettings.getPref(AppConstants.CONNECTING_TEXT, "");
    }

    public void setConnectedText(String connectedText) {
        ApplicationSettings.putPref(AppConstants.CONNECTED_TEXT, connectedText);
    }

    public String getConnectedText() {
        return ApplicationSettings.getPref(AppConstants.CONNECTED_TEXT, "");
    }

    public boolean getCallWaiting() {
        return callwaiting;
    }

    public void setCallWaiting(boolean callwaiting) {
        this.callwaiting = callwaiting;
        ApplicationSettings.putPref(AppConstants.CALL_WAITING, callwaiting);
    }

    public boolean getCallWaitingChoice() {
        return callwaitingchoice;
    }

    public void setCallWaitingChoice(boolean callwaitingchoice) {
        this.callwaitingchoice = callwaitingchoice;
        ApplicationSettings.putPref(AppConstants.CALLWAITING_CHOICE, callwaitingchoice);
    }

    public boolean getDisableCallback() {
        return disablecallback;
    }

    public void setDisableCallback(boolean disablecallback) {
        this.disablecallback = disablecallback;
        ApplicationSettings.putPref(AppConstants.DISABLE_CALLBACK, disablecallback);
    }

    public boolean getAdditionalLoginInfo() {
        return additionallogininfo;
    }

    public void setAdditionalLoginInfo(boolean additionallogininfo) {
        this.additionallogininfo = additionallogininfo;
        ApplicationSettings.putPref(AppConstants.ADDITIONAL_LOGIN_INFO, additionallogininfo);
    }

    public boolean getIBControl() {
        return ibcontrol;
    }

    public void setIBControl(boolean ibcontrol) {
        this.ibcontrol = ibcontrol;
        ApplicationSettings.putPref(AppConstants.IB_CONTROL, ibcontrol);
    }

    public boolean getAdhocCall() {
        return adhoccall;
    }

    public void setAdhocCall(boolean adhoccall) {
        this.adhoccall = adhoccall;
        ApplicationSettings.putPref(AppConstants.ADHOC_CALL, adhoccall);
    }


    public boolean getAutoAnswer() {
        return autoanswer;
    }

    public void setAutoAnswer(boolean autoanswer) {
        this.autoanswer = autoanswer;
        ApplicationSettings.putPref(AppConstants.AUTO_ANSWER, autoanswer);
    }

    public void setInQuestionnaire(String questionnaire) {
        ApplicationSettings.putPref(AppConstants.IN_QUESTIONNAIRE_FROM_SETTINGS, questionnaire);
        ApplicationSettings.putPref("CLOUDNUMBER2QUES", questionnaire);
    }

    public String getInQuestionnaire() {
        return ApplicationSettings.getPref(AppConstants.IN_QUESTIONNAIRE_FROM_SETTINGS, "");
    }

    public boolean getShowUI() {
        return showui;
    }

    public void setShowUI(boolean showui) {
        this.showui = showui;
        ApplicationSettings.putPref(AppConstants.SHOW_UI, showui);
    }

    public boolean getAllowMobileData() {
        return allowmobiledata;
    }

    public void setAllowMobileData(boolean allowmobiledata) {
        this.allowmobiledata = allowmobiledata;
        ApplicationSettings.putPref(AppConstants.ALLOW_MOBILE_DATA, allowmobiledata);
    }

    public boolean getASF() {
        return ASF;
    }

    public void setASF(boolean ASF) {
        this.ASF = ASF;
        ApplicationSettings.putPref(AppConstants.ASF, ASF);
    }

    public boolean getHomeNavigation() {
        return homenavigation;
    }

    public void setHomeNavigation(boolean homenavigation) {
        this.homenavigation = homenavigation;
        ApplicationSettings.putPref(AppConstants.HOME_NAVIGATION, homenavigation);
    }

    public boolean getWebLayout() {
        return weblayout;
    }

    public void setWebLayout(boolean weblayout) {
        this.weblayout = weblayout;
        ApplicationSettings.putPref(AppConstants.WEB_LAYOUT, weblayout);
    }

    public boolean getImmediateQuesSubmit() {
        return immquessubmit;
    }

    public void setImmediateQuesSubmit(boolean immquessubmit) {
        this.immquessubmit = immquessubmit;
        ApplicationSettings.putPref(AppConstants.IMM_QUES_SUBMIT, immquessubmit);
    }

    public boolean getAllowPing() {
        return allowping;
    }

    public void setAllowPing(boolean allowping) {
        this.allowping = allowping;
        ApplicationSettings.putPref(AppConstants.ALLOW_PING, allowping);
    }

    public boolean getAllowChatBot() {
        return allowchatbot;
    }

    public void setAllowChatBot(boolean allowchatbot) {
        this.allowchatbot = allowchatbot;
        ApplicationSettings.putPref(AppConstants.ALLOW_CHAT_BOT, allowchatbot);
    }

    public void setOptionsChatbot(String chatOptions) {
        ApplicationSettings.putPref(AppConstants.OPTIONS_CHAT_BOT, chatOptions);
    }

    public String getOptionsChatbot() {
        return ApplicationSettings.getPref(AppConstants.OPTIONS_CHAT_BOT, "");
    }

    public void setChatbotURL(String chatURL) {
        ApplicationSettings.putPref(AppConstants.CHAT_BOT_URL, chatURL);
    }

    public void setSmStateToScreen(String szString) {
        ApplicationSettings.putPref(AppConstants.SM_STATE_TO_SCREEN, szString);
    }

    public String getSmStateToScreen() {
        return ApplicationSettings.getPref(AppConstants.SM_STATE_TO_SCREEN, "");
    }

    public String getChatbotURL() {
        return ApplicationSettings.getPref(AppConstants.CHAT_BOT_URL, "");
    }

    public boolean getAllowChatC2C(){
        return c2cchat;
    }

    public void setAllowChatC2C(boolean c2cchat){
        this.c2cchat = c2cchat;
        ApplicationSettings.putPref(AppConstants.CHAT_C2C, c2cchat);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language, boolean save) {
        this.language = language;
        if(save) {
            writeToAppContstant(AppConstants.USERINFO_LANGUAGE, language);
        }
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education, boolean save) {
        this.education = education;
        if(save) {
            writeToAppContstant(AppConstants.USERINFO_EDUCATION, education);
        }
    }

    public String getWorkExperience() {
        return workexperience;
    }

    public void setWorkExperience(String workexperience, boolean save) {
        this.workexperience = workexperience;
        if(save) {
            writeToAppContstant(AppConstants.USERINFO_WORK_EXPERIENCE, workexperience);
        }
    }

    public String getBusinessProcess() {
        return businessprocess;
    }

    public void setBusinessProcess(String businessprocess, boolean save) {
        this.businessprocess = businessprocess;
        if(save) {
            writeToAppContstant(AppConstants.USERINFO_BUSINESS_PROCESS, businessprocess);
        }
    }

    public String getOtherExperience() {
        return otherexperience;
    }

    public void setOtherExperience(String otherexperience, boolean save) {
        this.otherexperience = otherexperience;
        if(save) {
            writeToAppContstant(AppConstants.USERINFO_OTHER_EXPERIENCE, otherexperience);
        }
    }

    public String getAdditionalData() {
        return additionaldata;
    }

    public void setAdditionalData(String additionaldata, boolean save) {
        this.additionaldata = additionaldata;
        if(save) {
            writeToAppContstant(AppConstants.USERINFO_ADDITIONAL_DATA, additionaldata);
        }
    }

    public boolean getAllowGroupChatCall() {
        return allowGroupChatCall;
    }

    public void setAllowGroupChatCall(boolean allowGroupChatCall) {
        this.allowGroupChatCall = allowGroupChatCall;
        ApplicationSettings.putPref(AppConstants.ALLOW_GROUP_CHAT_CALL, allowGroupChatCall);
    }

    public boolean getAllowAdhocCall() {
        return allowAdhocCall;
    }

    public void setAllowAdhocCall(boolean allowadhoccall) {
        this.allowAdhocCall = allowadhoccall;
        ApplicationSettings.putPref(AppConstants.ALLOW_ADHOC_CALL, allowadhoccall);
    }

    public String getActivateUssdCode() {
        return ApplicationSettings.getPref(AppConstants.ACTIVATE_USSD_CODE, "");
    }

    public void setActivateUssdCode(String connectedText) {
        ApplicationSettings.putPref(AppConstants.ACTIVATE_USSD_CODE, connectedText);
    }

    public String getDeactivateUssdCode() {
        return ApplicationSettings.getPref(AppConstants.DEACTIVATE_USSD_CODE, "");
    }

    public void setDeactivateUssdCode(String connectedText) {
        ApplicationSettings.putPref(AppConstants.DEACTIVATE_USSD_CODE, connectedText);
    }

    public boolean getDisallowHeadphones() {
        return disallowheadphones;
    }

    public void setDisallowHeadphones(boolean disallowheadphones) {
        this.disallowheadphones = disallowheadphones;
        ApplicationSettings.putPref(AppConstants.DISALLOW_HEADPHONES, disallowheadphones);
    }

    public boolean getAllowPMC2C(){
        return allowpmC2c;
    }

    public void setAllowPMC2C(boolean allowpmC2c){
        this.allowpmC2c = allowpmC2c;
        ApplicationSettings.putPref(AppConstants.ALLOW_PM_C2C, allowpmC2c);
    }

    public void setCallOptions(String callOptions) {
        ApplicationSettings.putPref(AppConstants.CALL_OPTIONS, callOptions);
    }

    public String getCallOptions() {
        return ApplicationSettings.getPref(AppConstants.CALL_OPTIONS, "");
    }

    public void setSIORefreshTime(String sioRefreshTime) {
        ApplicationSettings.putPref(AppConstants.SIO_REFRESH_TIME, sioRefreshTime);
    }

    public String getSIORefreshTime() {
        return ApplicationSettings.getPref(AppConstants.SIO_REFRESH_TIME, "");
    }

    public boolean getAbortCallSession() {
        return abortcallsession;
    }

    public void setAbortCallSession(boolean abortcallsession) {
        this.abortcallsession = abortcallsession;
        ApplicationSettings.putPref(AppConstants.ABORT_CALL_SESSION, abortcallsession);
    }

    public boolean getUpdateCustomKVS() {
        return updatecustomkvs;
    }

    public void setUpdateCustomKVS(boolean updatecustomkvs) {
        this.updatecustomkvs = updatecustomkvs;
        ApplicationSettings.putPref(AppConstants.UPDATE_CUSTOM_KVS, updatecustomkvs);
    }

    public boolean getDefaultDialer() {
        return defaultdialer;
    }

    public void setDefaultDialer(boolean defaultdialer) {
        this.defaultdialer = defaultdialer;
        ApplicationSettings.putPref(AppConstants.DEFAULT_DIALER, defaultdialer);
    }

}
