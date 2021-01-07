package smarter.uearn.money.utils;

import java.util.ArrayList;

import smarter.uearn.money.callrecord.RecorderService;
import smarter.uearn.money.models.GoogleAccount;
import smarter.uearn.money.models.GroupsUserInfo;

public class AppConstants {
    public static final String EVENT_TYPE = "event_type";
    public static final String SMS_EVENT_TYPE = "sms";
    public static final String CALL_EVENT_TYPE = "call";
    public static final String PREF_EMAIL_ID = "pref_email_id";
    public static final String PREF_DATE_CHECK = "pref_date_check";
    public static final String PREF_CONTACT_STRING = "pref_contact_string";
    public static final String ONE_TIME_SPLASH = "one_time_splash";
    public static final String KEY_CRITIRICISM = "537f37cc28ae4563c1000002";
    public static final String SHOW_POPUP = "show_popup";
    public static final String IS_MAN = "is_man";
    public static final String USER_STATUS = "user_status";
    public static final String USER_CREATED = "created_at";
    public static final String SHOW_NOTIFICATION = "show_notification";
    public static final String OFFICE365_ACCESS_TOKEN = "access_token";
    public static final String USERINFO_BUSINESS_LOGO_URL = "business_logo_url";
    public static final String USERINFO_BUSINESS_TAGLINE = "business_tagline";
    public static final String USERINFO_CALL_SMS_TEMPLATE = "call_sms_template";
    public static final String USERINFO_COMPANY = "company";
    public static final String USERINFO_JDNO = "jd_no";
    public static final String USERINFO_EMAIL = "email";
    public static final String USERINFO_ID = "id";
    public static final String BIZ_PRIZE_VALUE = "bizprize";
    public static final String UPDATED_AT = "due_date";
    public static final String USERINFO_JC_SMS_TEMPLATE = "jc_sms_template";
    public static final String USERINFO_JD_SMS_TEMPLATE = "jd_sms_template";
    public static final String USERINFO_BUSINESS_TEMPLATE = "business_template";
    public static final String USERINFO_NAME = "name";
    public static final String USERINFO_GENDER = "gender";
    public static final String USERINFO_LANGUAGE= "language";
    public static final String USERINFO_EDUCATION = "education";
    public static final String USERINFO_WORK_EXPERIENCE = "workexperience";
    public static final String USERINFO_BUSINESS_PROCESS = "businessprocess";
    public static final String USERINFO_OTHER_EXPERIENCE = "otherexperience";
    public static final String USERINFO_ADDITIONAL_DATA = "additionaldata";
    public static final String USERINFO_PASSWORD = "password";
    public static final String USERINFO_PAYMENT_STATUS = "payment_status";
    public static final String USERINFO_PHONE = "phone";
    public static final String USERINFO_ADDRESS = "address";
    public static final String USERINFO_JD_URL = "jd_url";
    public static final String START_TIME = "start_time";
    public static final String SETTING_CALL_RECORDING_STATE = "call_recording";
    public static final String SETTING_JD_CHECK = "jd_check";
    public static final String SETTING_AFTER_CALL_POPUP = "after_call_popup";
    public static final String CAPTURE_INCOMING_SMS = "capture_incoming_sms";
    public static final String CALL_RECORDING_OFF = "call_recording_off";
    public static final String FROM_CMAIL = "from_cmail";
    public static final String CHECK_TOGGLE_STATUS = "check_toggle_status";
    public static final String TO_EMAIL = "to_email";
    public static final String SMS_COMPOSE_CALL_RECORDING = "sms_call_recording";
    public static final String GROUP_ID = "group_id";
    public static final String LAST_SYNC_SETTINGS = "syncdate";
    public static final String ADMIN_PERSONAL_RECORDING = "call_recording_personal_administrator";
    public static final String ADMIN_GROUP_RECORDING = "call_recording_group_administrator";
    public static final String VOICE_ANALYTICS = "voice_analytics";
    public static final String IS_FOLLOWUP_EDITABLE = "followup_edit";
    public static final String RNR_CALLBACKS_NO = "rnr_callbacks_no";
    public static final String RNR_REMINDER = "rnr_reminders";
    public static final String SYNC_FOLLOWUP_DATE = "synk_followup_date";
    public static final String RNR_DURATION = "rnr_duration";
    public static final String FLP_DURATION = "flp_duration";
    public static final String SIO_REFRESH_TIME = "sio_refresh_time";
    public static final String HELP_LINE = "help_line";
    public static final String C2C_ENDPOINT = "c2cendpoint";
    public static final String SMB_OFFLINE = "smboffline";
    public static final String SUPERVISOR = "supervisor";
    public static final String DATA_START_TIME = "data_start";
    public static final String C2C_TIMEOUT = "c2c_timeout";
    public static final String UNSCHEDULED_CALL = "unscheduled_call";
    public static final String APP_INT_TRANSACTION_COUNTER = "app_internal_transaction_counter";
    public static final String TRUE_PREDICTIVE = "TRUE_PREDICTIVE";
    public static final String QUESTIONS_COUNT = "QUESTIONS_COUNT";
    public static final String ROOT_ARRAY_DATA_LENGTH = "ROOT_ARRAY_DATA_LENGTH";
    public static final String WRAPUP_TIME = "WRAPUP_TIME";
    public static final String ENABLE_ECB = "ENABLE_ECB";
    public static final String ENABLE_MESSAGE = "ENABLE_MESSAGE";
    public static final String CONNECTING_TEXT = "CONNECTING_TEXT";
    public static final String CONNECTED_TEXT = "CONNECTED_TEXT";
    public static final String CALL_OPTIONS = "CALL_OPTIONS";
    public static final String ENABLE_EMULATION = "ENABLE_EMULATION";
    public static final String SHOW_TIMER = "SHOW_TIMER";
    public static final String BACK_TO_NETWORK = "BACK_TO_NETWORK";
    public static final String ALLOW_SCREENSHOT = "ALLOW_SCREENSHOT";
    public static final String SYSTEM_CONTROL = "SYSTEM_CONTROL";
    public static final String FK_CONTROL = "FK_CONTROL";
    public static final String IB_CONTROL = "IB_CONTROL";
    public static final String ADHOC_CALL = "ADHOC_CALL";
    public static final String CALL_WAITING = "CALL_WAITING";
    public static final String AUTO_ANSWER = "AUTO_ANSWER";
    public static final String DONE_REFRESH = "DONE_REFRESH";
    public static final String UPLOAD_STATUS2 = "UPLOAD_STATUS2";
    public static final String CUSTOMER_INFO_ACK = "CUSTOMER_INFO_ACK";
    public final static String AFTER_CALL_NAME = "after_call_name";
    public final static String AGENT_DOB ="dob";
    public final static String AGENT_SPOKEN_LANG = "lang";
    public final static String AGENT_LOCATION = "locality";
    public static final String APP_LOGOUT = "app_logout";
    public static final String DRAFT_LIVEMEETING = "draft_livemeeting";
    public static final String CALL_END_DURATION = "callend_duration";
    public static final String SM_STATE_TO_SCREEN = "sm_state_to_screen";

    public static final String FROM_JD_SMS = "from_jd_sms";

    public static final String CHECK_LOGIN_STATUS = "check_login_status";

    public static final String FIRST_TIME_GMAIL = "first_time_gmail";

    public static final String CHECK_UPLOADING_STATUS = "check_uploading_status";

    public static final String DEFAULT_JD_TEMPLATE = "Hi, Thank you for your interest in our solution. Please find more information at ";

    public static final String DEFAULT_JOBCARD_TEMPLATE = "Dear Customer, You can find details of the Job card below. ";

    public static final String DEFAULT_AFTER_CALL_TEMPLATE = "Hi, you may now access the call recording and more at ";
    public static final String DEFAULT_BUSINESS_TEMPLATE = "Nice talking to you. Please click here to see our company brochure and location";

    public static final String DISABLE_CALL_REC_BUTTONS = "disable_call_rec_buttons";

    public static final String CALL_OUTGOING = "call_outgoing";
    public static final String CALL_INCOMING = "call_incoming";

    public static final String RECORDINGTYPE = "recordingtype";
    public static final String WEBSITE = "website";
    public static final String PROMO_VIDEO_LINK = "promo_video_link";
    public static final String TESTIMONIAL = "testimonial";
    public static final String FACILITY_PHOTO_URL1 = "facility_photo_url1";
    public static final String FACILITY_PHOTO_URL2 = "facility_photo_url2";
    public static final String FACILITY_PHOTO_URL3 = "facility_photo_url3";
    public static final String FACILITY_PHOTO_URL4 = "facility_photo_url4";
    public static final String MAP_URL = "map_url";
    public static final String GOOGLE_MAP_LANDMARK = "google_map_landmark";
    public static final String ADDRESS = "address";
    public static final String PRIVATE_CONTACTS = "private_contacts";
    public static final String AMEYO_NUMBERS = "ameyo_numbers";
    public static final String LEAD_CONTACTS = "lead_contacts";
    public static final String TEAM_MEMBER_CONTACTS = "team_member_contacts";
    public static final String DONT_CALL_RECORD_PRIVATE_CONTACTS = "dont_call_record_private_contacts";
    public static final String ONLY_CALL_RECORD_UNKNOWN_NUMBERS = "only_call_record_unknown_numbers";
    public static final String ONLY_RECORD_LEAD_CONTACTS = "only_record_lead_contacts";
    public static final String ONLY_RECORD_CLOUND_TELEPHONY_NUMBERS = "only_record_cloud_telephone_numbers";
    public static final String ONLY_RECORD_TEAM_MEMBERS_NUMBERS = "only_record_team_members_numbers";
    public static final String PRIMARY_CONTACT = "primary_contact";
    public static final String LOGIN_METHOD = "login_method";
    public static final String MY_SERVER_URL = "my_server_url";
    public static final String MY_SIO_URL = "my_sio_url";
    public static final String MY_DASHBOARD_SIO_URL = "my_dashboard_sio_url";
    public static final String CUSTOMER_NUMBER_STATUS = "";

    public static final String FIRST_TIME_HOMESCREEN = "first_time_homescreen";

    public static final String LINKED_IN_LINK = "linked_in_link";
    public static final String FACEBOOK_LINK = "facebook_link";
    public static final String TWITTER_LINK = "twitter_link";
    public static final String JOB_TITLE = "job_title";
    public static final String YOUTUBE_CHANNEL_LINK = "youtube_channel_link";
    public static final String ACCESS__TOKEN = "token";
    public static final String AUTOSMS_FOR_MISSED_CALLS = "auto_sms_for_missed_calls";
    public static final String GPS_SETTINGS = "gps_settings";
    public static final String AUTO_ACCEPT_LEADS_SETTINGS = "auto_accept";
    public static final String AUTO_FOLLOW_UPS_SETTINGS = "auto_follow_ups";
    public static final String SMS_TRACKING_SETTINGS = "sms_tracking_settings";
    public static final String TEAM_CALLS_SETTINGS = "team_calls_settings";
    public static final String FOLLOW_UP_CALLS_SETTINGS = "follow_up_calls_settings";
    public static final String APPLICATION_LOGGING = "application_logging_settings";
    public static final String SMS_TRACKING_ALL = "sms_track_alk";
    public static final String SMS_TRACKING_LEADS = "sms_track_leads";
    public static final String SMS_TRACKING_TEAM = "sms_track_team";
    public static final String SMS_TRACKING_PRIVATE = "sms_track_private";
    public static final String CALL_TRACKING_ALL_SETTINGS = "call_tracking_all";
    public static final String SETTINGS_BOOK_APPOINTMENT_STATUS = "book_appointment_status";
    public static final String CAN_EDIT_SETTINGS = "edit_settings";
    public static final String CHECK_TIMER = "check_pref";
    public static final String SAVE_SETTINGS = "settings_save";
    public static final String NEW_USER = "new_user";
    public static final String SALES_STAGES = "salesstages";
    public static final String CLOUD_TELEPHONY = "cloud_telephony_numbers";
    public static final String SUPERVISOR_EMAIL = "supervisor_email";
    public static final String TEAM_CURRENCY = "team_currency";
    public static final String APP_UPDATE = "app_update";
    public static final String APP_UPDATE_NUMBER = "update_number";
    public static final String APP_PROFILE_URL = "profile_url";
    public static final String JUNKCALL_SETTING = "junkcall_setting";
    public static final String PERSONALCALL_SETTING = "personalcall_setting";
    public static final String CLOUD_OUTGOING = "cloud_outgoing";
    public static final String AUTO_DAILER = "auto_dailer";
    public static final String TRANSCRIPTION = "transcription";
    public static final String SIP_CALLS = "sip_calls";
    public static final String TRANSCRIPT_LANG = "transcript_lang";
    public static final String TRANSCRIPT_AUDIO_FORMATE = "transcript_audio_formate";
    public static final String TRANSCRIPT_AUDIO_ENCODE_FORMATE = "transcript_audio_encode_formate";
    public static final String AFTERCALLACTIVITY_SCREEN = "aftercallactivity_screen";
    public static final String CLOUD_CUSTOMER_NUMBER = "cloud_customer_number";
    public static final String MOBILE_DATA_ON_SIM = "mibile_data_on_sim";
    public static final String TEAM_ROLE = "team_role";
    public static final String CUSTOMER_UUID_MAP = "customer_uuid_map";
    public static final String CUSTOMER_AUTODIAL_MAP = "customer_autodial_map";

    public static final String RAD_MESSAGE_VALUE = "radmessage";

    public static final String SR_NUMBER = "sr_number";
    public static final String AUTH_KEY = "authkey";
    public static final String CLOUD_OUTGOING1 = "cloud_outgoing1";
    public static final String CLOUD_OUTGOING2 = "cloud_outgoing2";
    public static final String CLOUD_OUTGOING3 = "cloud_outgoing3";

    public static final String TIMER_BEFORE_CALL = "timer_before_call";
    public static final String TIMER_AFTER_CALL = "timer_after_call";

    public static final String WORK_DOW1 = "work_dow1";
    public static final String WORK_DOW2 = "work_dow2";
    public static final String OPEN_TIME = "open_time";
    public static final String CLOSE_TIME = "close_time";

    public static final String MON = "mon";
    public static final String TUE = "tue";
    public static final String WEN = "wen";
    public static final String THU = "thu";
    public static final String FRI = "fri";
    public static final String SAT = "sat";
    public static final String SUN = "sun";

    public static final String PAN_TOTAL = "pan_total";
    public static final String PAN_APPROVED = "pan_approved";
    public static final String QDE_TOTAL = "qde_total";
    public static final String QDE_APPROVED = "qde_approved";
    public static final String PAN_RANK = "pan_rank";
    public static final String QDE_RANK = "qde_rank";
    public static final String PANQDE_UPDATE_TIME = "qde_rank";

    public static final ArrayList<GoogleAccount> GOOGLE_ACCOUNTSLIST = new ArrayList<>();

    /*initiazed  vaiables for Auth Login */
    public static String TOKEN = null;
    public static ArrayList<String> COOKIE = null;

    /*verion modified 31 to 32*/
    public static final int dBversion = 45; // Count incremented for 104th version - 44; 104 - 43 to 44
    public static RecorderService recorderService;
    public static final int GET_CAL_EVENTS = 5;
    public static int UNATTENDED_COUNT = 0, UPCOMING_COUNT = 0;

    public static String FIREBASE_ID_1 = "Landing";
    public static String FIREBASE_ID_2 = "Authentication";
    public static String FIREBASE_ID_3 = "Sign In";
    public static String FIREBASE_ID_4 = "Sign Up";
    public static String FIREBASE_ID_5 = "Home";
    public static String FIREBASE_ID_6 = "Dashboard";
    public static String FIREBASE_ID_7 = "Supervisor";
    public static String FIREBASE_ID_8 = "Preferences";
    public static String FIREBASE_ID_9 = "Change_Password";
    public static String FIREBASE_ID_10 = "Settings_List";
    public static String FIREBASE_ID_11 = "After_Call_SMS_Templates";
    public static String FIREBASE_ID_12 = "Business_Card";
    public static String FIREBASE_ID_13 = "Digital_Brochure";
    public static String FIREBASE_ID_14 = "After_Call";
    public static String FIREBASE_ID_15 = "Feedback_Activity";
    public static String FIREBASE_ID_16 = "Add_Team_Member_Activity";
    public static String FIREBASE_ID_17 = "HOME";
    public static String FIREBASE_ID_18 = "Follow_Ups_Dashboard";
    public static String FIREBASE_ID_19 = "Sales_Dashboard";
    public static String FIREBASE_ID_20 = "First_Call_Dashboard";
    public static String FIREBASE_ID_21 = "Follow_Up_Settings";
    public static String FIREBASE_ID_22 = "Sales_Settings";
    public static String FIREBASE_ID_23 = "SMS_Email_Templates";
    public static final String AFTER_CALL_AUDIO_URL = "audio_url";
    public static final String AFTER_CALL_IMAGE_URL = "image_url";
    public static final String MULTI_DEVICE_SIGN_OUT = "device_sign_out";
    public static final String DO_NOT_REMIND_NOT_LOGGED_IN = "neverRequestDialog";
    public static final String STORE_NUMBER_FOR_PROCESSING = "numberProcessing";
    public static final String NOT_LOGGED_EVENT_TYPE = "notLoggedPersonal";
    public static final String NOT_LOGGED_SHOW_DIALOG = "showLandingDialog";
    public static final String PROFILE_PIC = "profilePic";
    public static String USER_EMAIL_ADDRESS = "";
    public static String USER_VOIP_NUMBER = "voip_number";
    public static String DATA_STATUS = "online";

    public static final String BANK1_AFTERCALL = "smarter.uearn.money.activities.Bank1AfterCallActivity";
    public static final String AFTERCALL = "smarter.uearn.money.activities.AfterCallActivity";
    public static final String Auto1AFTERCALL = "smarter.uearn.money.activities.Auto1AfterCallActivity";
    public static final String Auto2AFTERCALL = "smarter.uearn.money.activities.Auto2AfterCallActivity";
    public static ArrayList<GroupsUserInfo> GROUPS_DATA = null;
    public static final String BANK1 = "Bank1AfterCallActivity";
    public static final String USER_ROLE = "role";

    //Login activity
    public static int GET_TEAM_MEMBERS = 535;

    //Reset Device
    public static boolean RESET_DEVICE = false;
    public static boolean IN_A_CALL = false;
    public static boolean POSTCALLOG = false;
    public static String POSTCALLOGURL = "";
    public static int INVALID = 1;

    public static boolean KILL_APP = false;
    public static boolean SIGN_OUT = false;
    public static final String USE_CAMPAIGN = "usecampaign";
    public static final String SHOWON_CALL_CAMPAIGN = "showoncallcampaign";
    public static final String ON_CALL_ACT = "oncallact";
    public static String CUSTOMER_FEEDBACK = "customerfeedback";

    public static String INCOMING_CALL_DISCONNECTED = "calldisconnected";
    public static final String QUESTIONNAIRE = "questionnaire";
    public static final String ACP_DATA = "acpdata";
    public static final String DB_ID = "DB_ID";

    public static final String DELAY_AC_POPUP = "delayacpopup";
    public static final String DONE_LIST_QUERY = "donelistquery";
    public static final String DONE_CARD_QUERY = "donecardquery";
    public static final String AUTO_SYNC = "autosync";
    public static final String TRUNCATE_REMINDER_TABLE = "truncateremindertable";
    public static final String QUESTIONS_ACT = "questionsact";
    public static final String NO_OF_CUSTOMERS_TO_CALL = "noofcustomers";
    public static final String IS_PROMO = "ispromo";
    public static final String PARALLEL_GROUP_SIZE = "parallel_group_size";
    public static final String ALLOW_ALL_INCOMING = "allowallincoming";
    public static final String ALLOW_ALL_OUTGOING = "allowalloutgoing";
    public static final String NEW_DATA_STRING = "newdatastring";
    public static final String FOLLOW_UP_STRING = "followupstring";
    public static final String REMOTE_AUTO_DIALLING = "remotedialling";
    public static final String SIOPUSH_URL = "siopushurl";
    public static final String SIOPUSH_DEFAULT_URL = "http://35.200.212.234:3002";
    public static final String RAD_OPTIMIZED_POST = "radoptimizedpost";
    public static final String DEFAULT_HOME_PAGE = "defaulthomepage";
    public static final String DEFAULT_HOME_PAGE_WEB_VIEW = "defaulthomepagewebview";
    public static final String REMOTE_DIALLING_START_ALERT = "remotediallingstartalert";
    public static final String REMOTE_DIALLING_STOP_ALERT = "remotediallingstopalert";
    public static final String USER_POSITIVE_PREFERENCES = "userpositivepreferences";
    public static final String USER_NEGATIVE_PREFERENCES = "usernegativepreferences";
    public static final String TEST_NUMBER = "testnumber";

    public static final String UEARN_DASHBOARD_DATA = "UEARN_DASHBOARD_DATA";
    public static final String CUSTOMER_LIST_DATA = "CUSTOMER_LIST_DATA";
    public static final String CONNECTED_CUSTOMER = "CONNECTED_CUSTOMER";
    public static final String CONNECTED_CUSTOMER_NAME = "CONNECTED_CUSTOMER_NAME";
    public static final String LIST_OF_DND_NUMBERS = "LIST_OF_DND_NUMBERS";
    public static final String LIST_OF_INVALID_NUMBERS = "LIST_OF_INVALID_NUMBERS";
    public static final String C2C_SEQUENCIAL_ENDPOINT = "C2C_SEQUENCIAL_ENDPOINT";
    public static final String C2C_HYBRID_ENDPOINT = "C2C_HYBRID_ENDPOINT";

    public static final String SIO_DISABLED = "SIO_DISABLED";
    public static final String IS_REUPLOAD_ENABLED = "IS_REUPLOAD_ENABLED";

    public static final String CALL_END_TIME = "CALL_END_TIME";
    public static final String CALL_SUBMIT_TIME = "CALL_SUBMIT_TIME";

    public static final String LOGGED_IN_START_TIME = "LOGGED_IN_START_TIME";
    public static final String LOGGED_IN_END_TIME = "LOGGED_IN_END_TIME";
    public static final String LOGGED_IN_TIME = "LOGGED_IN_TIME";
    public static final String TOTAL_LOGGED_IN_TIME = "TOTAL_LOGGED_IN_TIME";

    public static final String ACTIVE_TIME_START = "ACTIVE_TIME_START";
    public static final String ACTIVE_TIME_END = "ACTIVE_TIME_END";
    public static final String ACTIVE_TIME = "ACTIVE_TIME";
    public static final String TOTAL_ACTIVE_TIME = "TOTAL_ACTIVE_TIME";

    public static final String INCOMING_NUMBER = "INCOMING_NUMBER";

    public static final String PREVIOUS_QUES = "PREVIOUS_QUES";
    public static final String FIELDS_TO_SHOW = "FIELDS_TO_SHOW";

    public static final String MMT_STATUS = "MMT_STATUS";
    public static final String MMT_SUBSTATUS = "MMT_SUBSTATUS";

    public static final String SHOW_AFTER_CALL_POPUP = "SHOW_AFTER_CALL_POPUP";
    public static final String APPEND_LEAD_SOURCE = "APPEND_LEAD_SOURCE";
    public static final String LAUNCH_APP = "LAUNCH_APP";
    public static final String CUSTOMER_LIST_DATA_EXCLUDING_INVALID = "CUSTOMER_LIST_DATA_EXCLUDING_INVALID";
    public static final String LIST_OF_REPEATED_NUMBERS = "LIST_OF_REPEATED_NUMBERS";
    public static final String PUSHRECEIVER_REGISTRATION_STATUS = "PUSHRECEIVER_REGISTRATION_STATUS";

    public static final String CALL_FROM_CALENDAR_VIEW = "CALL_FROM_CALENDAR_VIEW";

    public static final String UPLOAD_CMAIL = "UPLOAD_CMAIL";
    public static final String FILL_QDE = "FILL_QDE";
    public static final String MMT_DURING_CALL_NOTES = "MMT_DURING_CALL_NOTES";


    public static final String SHOW_ALERT = "SHOW_ALERT";
    //public static final String CONNECTION_STATUS_MSG = "CONNECTION_STATUS_MSG";

    public static final String CUSTOMER_DISCONNECT = "CUSTOMER_DISCONNECT";
    public static final String STATUS_CONNECTED = "STATUS_CONNECTED";

    public static final String CALLBACK_FOLLOWUP = "callback_followup";
    public static final String SERVER_APT_UPDATES = "SERVER_APT_UPDATES";

    public static String APP_VERSION = "3.3";

    // Added By Sujit

    public static final String SUMMARY_VISIBILITY = "SUMMARY_VISIBILITY";

    public static final String CUSTOMER_QUES = "CUSTOMER_QUES";
    public static final String QUESTIONNAIRE_FROM_SETTINGS = "QUESTIONNAIRE_FROM_SETTINGS";
    public static final String IN_QUESTIONNAIRE_FROM_SETTINGS = "IN_QUESTIONNAIRE_FROM_SETTINGS";
    public static final String WEB_LAYOUT = "WEB_LAYOUT";
    public static final String IMM_QUES_SUBMIT = "IMM_QUES_SUBMIT";
    public static final String ALLOW_PING = "ALLOW_PING";

    public static final String SELECTED_DATE_FROM_CALENDAR_VIEW = "SELECTED_DATE_FROM_CALENDAR_VIEW";
    public static final String USER_AUDIT_SCORE = "audited_score";

    public static final String UEARN_PASSBOOK = "UEARN_PASSBOOK";
    public static final String UEARN_PASSBOOK_MONTH = "UEARN_PASSBOOK_MONTH";
    public static final String UEARN_PASSBOOK_LIFETIME = "UEARN_PASSBOOK_LIFETIME";
    public static final String CONTACTED_DATA = "CONTACTED_DATA";
    public static final String ALLOW_REDIAL_FOR_REMOTE_ENABLED = "ALLOW_REDIAL_FOR_REMOTE_ENABLED";

    public static final String WIFI_CHECK_COMPLETED = "WIFI_CHECK_COMPLETED";
    public static final String KYC_DOC_UPLOADED = "KYC_DOC_UPLOADED";
    public static final String EXP_LETTER_UPLOADED = "EXP_LETTER_UPLOADED";
    public static final String IS_BLUETOOTH_HEADSET = "IS_BLUETOOTH_HEADSET";
    public static final String GRAMMAR_TEST_LEVEL = "GRAMMAR_TEST_LEVEL";
    public static final String GRAMMAR_TEST_START_TIME = "GRAMMAR_TEST_START_TIME";
    public static final String GRAMMAR_TEST_END_TIME = "GRAMMAR_TEST_END_TIME";
    public static final String GRAMMAR_TEST_TOTAL_TIME = "GRAMMAR_TEST_TOTAL_TIME";

    public static final String VOICE_TEST_COMPLETED = "VOICE_TEST_COMPLETED";
    public static final String GRAMMAR_TEST_COMPLETED = "GRAMMAR_TEST_COMPLETED";
    public static final String PROCESS_SELECTION_CHAT_COMPLETED = "PROCESS_SELECTION_CHAT_COMPLETED";
    public static final String DEVICE_CHECK_COMPLETED = "DEVICE_CHECK_COMPLETED";
    public static final String CALL_FROM_INTERVIEW_PANEL_COMPLETED = "CALL_FROM_INTERVIEW_PANEL_COMPLETED";
    public static final String ONBOARDING_PROCESS_COMPLETED = "ONBOARDING_PROCESS_COMPLETED";

    public static final String LANGUAGES = "LANGUAGES";
    public static final String EDUCATION_QUALIFICATIONS = "EDUCATION_QUALIFICATIONS";
    public static final String EXPERIENCE = "EXPERIENCE";
    public static final String HARDWARE_SETUP = "HARDWARE_SETUP";

    public static final String VOICE_TEST_SCRIPT = "";
    public static final String VOICE_TEST_FILE = "voice_test_file";
    public static final String VOICE_TEST_LANG = "voice_test_lang";

    public static final String SSU_ON_SIO = "SSU_ON_SIO";
    public static final String SHOW_UI = "SHOW_UI";
    public static final String HOME_NAVIGATION = "HOME_NAVIGATION";
    public static final String notificationCount = "NOTIFICATION_COUNT";

    public static final String ALLOW_MOBILE_DATA = "allowmobiledata";
    public static final String ASF = "ASF";
    public static final String WIFI_SPEED = "WIFI_SPEED";
    public static final String UEARN_DATA = "UEARN_DATA";
    public static final String INTERVIEW_PROCESS = "INTERVIEW_PROCESS";
    public static final String PROCESS_STATUS = "PROCESS_STATUS";

    public static final String ALLOW_CHAT_BOT = "allowchatbot";
    public static final String OPTIONS_CHAT_BOT = "optionschatbot";
    public static final String CHAT_BOT_URL = "chatboturl";
    public static final String ALLOW_GROUP_CHAT_CALL = "allowGroupChatCall";

    public static final String DEVELOPER_KEY = "AIzaSyD9-DiMj8o8v_S0L05hOIrYL7AUsdQEz3w";
    public static final String YOUTUBE_VIDEO_CODE = "2uOjS-N4YCU";

    public static final String DOCUMENT_TYPE_PANCARD="PAN";
    public static final String DOCUMENT_TYPE_ADHARCARD="AADHAR";
    public static final String DOCUMENT_TYPE_PASSBOOK="PASS BOOK";
    public static final String DOCUMENT_TYPE_PROFILE_PHOTO="PROFILE PHOTO";
    public static final String DOCUMENT_TYPE_SERVICE_AGREEMENT="SERVICE AGREEMENT";
    public static final String CHAT_C2C = "c2cchat";
    public static final String ALLOW_PM_C2C = "allowpmc2c";

    public static final String trainingnotificationCount = "TRAINING_NOTIFICATION_COUNT";


    public static final String USER_STATUS_REGISTERED= "REGISTERED";
    public static final String USER_STATUS_READY_FOR_TESTS= "READY FOR TESTS";
    public static final String USER_STATUS_ON_BOARDING= "ON BOARDING";
    public static final String USER_STATUS_IN_TRAINING= "IN TRAINING";
    public static final String USER_STATUS_VOICE_TEST= "VOICE TEST";
    public static final String USER_STATUS_EMAIL_TEST= "EMAIL TEST";
    public static final String USER_STATUS_CHAT_TEST= "CHAT TEST";


    public static final String FEEDBACK_PAGE_ERROR= "Error";
    public static final String FEEDBACK_PAGE_SUGGESTION= "Suggestions";
    public static final String FEEDBACK_PAGE_RATE= "Rate";
    public static final String FEEDBACK_PAGE_OTHER= "Other";


    public static final String DOCUMENT_KYC_PANCARD="PAN";
    public static final String DOCUMENT_KYC_ADHARCARD="AADHAR";
    public static final String DOCUMENT_KYC_PASSBOOK="PASS_BOOK";
    public static final String DOCUMENT_KYC_PROFILE_PHOTO="PROFILE_PHOTO";
    public static final String DOCUMENT_KYC_SERVICE_AGREEMENT="SERVICE_AGREEMENT";

    public static final String ALLOW_ADHOC_CALL = "ALLOW_ADHOC_CALL";
    public static final String ACTIVATE_USSD_CODE = "ACTIVATE_USSD_CODE";
    public static final String DEACTIVATE_USSD_CODE = "DEACTIVATE_USSD_CODE";
    public static final String MY_PROFILE_PROJECT_TYPE = "MY_PROFILE_PROJECT_TYPE";
    public static final String DISALLOW_HEADPHONES = "DISALLOW_HEADPHONES";

    public static final String CALLWAITING_CHOICE = "CALLWAITING_CHOICE";
    public static final String DISABLE_CALLBACK = "DISABLE_CALLBACK";
    public static final String BUSINESS_LOGO_URL = "BUSINESS_LOGO_URL";
    public static final String ADDITIONAL_LOGIN_INFO = "ADDITIONAL_LOGIN_INFO";
    public static final String ABORT_CALL_SESSION = "ABORT_CALL_SESSION";
    public static final String UPDATE_CUSTOM_KVS = "UPDATE_CUSTOM_KVS";
    public static final String DEFAULT_DIALER = "DEFAULT_DIALER";
    public static final String EXTERNAL_CUSTOMER_NUMBER = "EXTERNAL_CUSTOMER_NUMBER";
}