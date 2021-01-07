package smarter.uearn.money.activities;
import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;

import java.io.File;
import java.text.DecimalFormat;

import android.graphics.drawable.GradientDrawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.AdSize;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.feedback.FeedBackActivity;
import smarter.uearn.money.activities.homepage.HomeActivity;
import smarter.uearn.money.activities.profilesettings.ProfileActivity;
import smarter.uearn.money.agentslots.activity.AgentSlotsActivity;
import smarter.uearn.money.broadcastReceiver.Alarm_Receiver;
import smarter.uearn.money.broadcastReceiver.NetworkReceiver;
import smarter.uearn.money.callrecord.RecorderService;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.chats.activity.ChatActivity;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.fragments.AutoDillerFragment;
import smarter.uearn.money.models.ContactedInfo;
import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.ProgressItem;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.notificationmessages.activity.NotificationMessageActivity;
import smarter.uearn.money.notificationmessages.model.NotificationMessageResponse;
import smarter.uearn.money.notificationmessages.model.NotificationMessages;
import smarter.uearn.money.services.AcceptOrRejectNotificationService;
import smarter.uearn.money.services.CalldataSendService;
import smarter.uearn.money.services.SocketIOService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NetworkInformation;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ResponseInterface;
import smarter.uearn.money.utils.ServerAPIConnectors.APIAdapter;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.UearnEditText;
import smarter.uearn.money.utils.UserActivities;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.webservice.Constants;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;
import smarter.uearn.money.utils.webservice.WebService;
import smarter.uearn.money.views.CustomSeekBar;
import smarter.uearn.money.views.events.ConnectivityReceiver;
import smarter.uearn.money.views.events.HeadPhoneReciever;
import smarter.uearn.money.views.events.NetworkBroadcastReceiver;

import static com.facebook.FacebookSdk.getApplicationContext;
import static smarter.uearn.money.utils.SmarterSMBApplication.isGroupSelected;
import static smarter.uearn.money.utils.SmarterSMBApplication.isSMESelected;
import static smarter.uearn.money.utils.SmarterSMBApplication.isTLSelected;
import static smarter.uearn.money.utils.SmarterSMBApplication.isTechSelected;
import static smarter.uearn.money.utils.SmarterSMBApplication.mSocket;
import static smarter.uearn.money.utils.SmarterSMBApplication.resetHomeNotInStartMode;
import static smarter.uearn.money.utils.SmarterSMBApplication.startSmbSio;
import static smarter.uearn.money.chats.activity.ChatActivity.channelResponse;

import static smarter.uearn.money.chats.activity.ChatActivity.socket;

public class UearnHome extends BaseActivity implements View.OnClickListener, View.OnSystemUiVisibilityChangeListener, API_Response_Listener<SmartUser> {

    private int GET_USER = 1001;
    private static UearnHome instance;
    private HeadPhoneReciever myReceiver;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    //private ConnectivityReceiver connectivityReceiver;
    public DrawerLayout drawer;
    private SQLiteDatabase db;
    private ImageView ivSyncData;
    private MenuItem sync, chart, notification;
    public static Button swipe_to_start, connectingStatus, manual_swipe_to_start;
    public static boolean manualDialing = false;
    public static boolean dialingFromList = false;
    public static boolean autoDial = false;
    public static boolean remoteAutoDialingStarted = false;
    private static boolean activityVisible = true;
    private boolean checkAuto = false;
    static Long start = 0L, end = 0L;
    private int firstCallfollowups = 0;
    private WebView  webViewText;
    private String getUrl = null;
    private TextView totalFirstCall, completedFollowups, followupsMissed, uearnedTextView;
    private TextView doneFollowupsBtn, followupToDoBtn, followupsMissedBtn;
    private int flp_count = 0;
    int v = 0;
    public static String start_time = null, end_time = null;
    private FragmentManager fragmentManager;
    private Activity mActivity;
    private String company = "";
    private String selectedLeadSource = "All";
    private TextView followupsToDo, uearnedBonusTextView ;
    private TextView loginHours, breakTime, totalCalls, talkTime;
    private boolean isStart = false;
    private boolean sendRemoteDialStartRequest = false;
    private ImageView profilePic;
    private LinearLayout today_uearn, voiceTestContent, todayUearnContent, youCanEarnMore,
            firstCallFollowup, acp_bottom_layout, agentGoalLayout, ll_update_goalLayout, ll_bottom_layout, manual_dial_layout;
    private RelativeLayout start_layout, activeTime_layout;
    AlertDialog alertDialog;
    private boolean noMissedFollowups = false;
    String userStatus = "";
    int totalFlpMissed = 0, totalFlpTodo = 0, totalFlpFirstCall = 0, donecount = 0;
    public static boolean retrySalesStageUpdateFailed = false;
    public static boolean remoteAutoDialingStartClicked = false;
    public static boolean callEndedFromHomeScreen = false;
    public static boolean predictiveModeDoneList = false;
    private LinearLayout setGoalLayout, setGoalsLayout, goalLayout, goalsCompletedLayout;
    private RelativeLayout goalsNotCompletedLayout;
    private TextView currentMonth, earnTv, hintsTv, newGoalSet;
    private EditText goalEarnedTv;
    private View underLineView;
    private CustomSeekBar seekbar;
    private ArrayList<ProgressItem> progressItemList;
    private ProgressItem mProgressItem;
    private LinearLayout connectionStatusLayout;
    private TextView setGoal, numberOfDaysLeft, youEarnTv, earned_remaing_tv;
    private TextView connectionStatus, testNow;
    private String bizPrize, mtdEarn;
    private Button gotItButton;
    CountDownTimer waitTimer;
    private int currentDay, lastDay, daysLeft;
    private LinearLayout completedFollowupsLayout, onStartlayout, gotItlayout;
    private AVLoadingIndicatorView uearnLoader, connectedLoader, testNowLoader;
    private float totalActiveTimeSpan = 0;
    private String remoteAutoEnabled = "";
    public static List<ContactedInfo> contactedInfoList;
    public static CountDownTimer pingTimer;
    public static ArrayList<Double> pingResponselist = null;
    public static String pingResponse = "";
    public static boolean timerStopped = false;
    public static boolean pingInProgress  = false;
    private Activity activity = null;
    private String connectingText = "";
    private long waittime = 0;
    private boolean stayathome = false;
    private String callState = "";
    View decorView = null;
    private WebView defaultMsgWebView;
    private LinearLayout defaultMsgWebViewLayout;
    NetworkInformation networkInformation;
    private BroadcastReceiver mNetworkReceiver;
    private ImageView image_chat, image_notification, image_sync;
    public static TextView tv_message_count;
    public LinearLayout today_uearn_layout;
    public LinearLayout leadsconnectedAndfollowupLayout, activeTimeLayout, internetStatusLayout, additionalLoginInfoLayout;
    public static TextView internet_status, internet_status_goal_before;
    private LinearLayout ll_today_goal;
    private Animation noNetworkAnimation = null;
    private String buttonText = null;
    private String loggedInTimeFromApp = "0:0:0";
    private String activeTimeFromApp = "0:0:0";

    private CardView adhocCallCardView;
    private LinearLayout adhocCallViewLayout;
    private UearnEditText adhocCallEditText;
    private ImageButton adhocCallImageButton;
    View DrawerMenu;
    DrawerLayout drawerLayout;
    TextView nevSettings, navAbout, navFAQ, nevRefEarn, navAppVersion, navLogOut, nevFeedBack, navWorkSlots;
    ImageView homeNav, navClose;
    LinearLayout ll_follow_ups;
    LinearLayout lyBtnWorkSlots, lyBtnInvite, ll_non_uearn;
    TextView tv_user_name;
    FloatingActionButton manualDial;
    private ImageView goalLayoutImageView, setGoalsLayoutImageView, nonUearnLayoutImageView;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        unregisterAllReceivers();
    }

    private Emitter.Listener onNewRadMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String serverMessage = (String) args[0];
                        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

                        if (!userId.isEmpty()) {
                            if (serverMessage.startsWith("http://") || serverMessage.startsWith("https://")) {
                                webViewText.loadUrl(serverMessage);
                            } else {
                                webViewText.loadDataWithBaseURL(null, serverMessage, "text/html", "utf-8", null);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = "";
            String result = intent.getStringExtra("result");

            if (intent.hasExtra("geturl")) {
                getUrl = intent.getStringExtra("geturl");
                String tts = "no";
                if (intent.hasExtra("ttsenable")) {
                    tts = intent.getStringExtra("ttsenable");
                }

                String showcancel = "no";
                if (intent.hasExtra("showcancel")) {
                    showcancel = intent.getStringExtra("showcancel");
                }

                if (result != null && !result.isEmpty()) {
                    boolean showAlert = ApplicationSettings.getPref(AppConstants.SHOW_ALERT, false);
                    if (!showAlert) {

                    } else {
                        Intent intent4 = new Intent(UearnHome.this, MyStopAlertDialog.class);
                        intent4.putExtra("MESSAGE", result);
                        intent4.putExtra("SHOWCANCEL", showcancel);
                        intent4.putExtra("TTSENABLE", tts);
                        intent4.putExtra("TITLE", "CUSTOMER INFORMATION");
                        intent4.putExtra("SIGNOUT", "");
                        startActivityForResult(intent4, 3);
                    }
                }
            } else if (result.equals("start_request")) {
                try {
                    message = intent.getStringExtra("usermessage");
                } catch (NullPointerException e) {
                } catch (Exception e) {
                }
                sendRemoteDialStartRequest(message);
            } else if (result.equals("stop_request")) {
                try {
                    message = intent.getStringExtra("usermessage");
                } catch (NullPointerException e) {
                } catch (Exception e) {
                }
                if(message != null && !message.isEmpty()) {
                    sendRemoteDialStopRequest(message);
                } else {
                    sendRemoteDialStopRequest("Taking a break");
                }
                endActiveCall();
            } else if (result.equals("internet_off")) {
                testNowLoader.setVisibility(View.VISIBLE);
                testNow.setVisibility(View.GONE);
                if ((SmarterSMBApplication.SmartUser != null) && !SmarterSMBApplication.SmartUser.getEmulationOn()) {
                    if (connectingStatus != null) {
                        connectionStatus.setText(getResources().getString(R.string.connection_check_msg));
                        connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_check));
                        testNow.setBackground(getResources().getDrawable(R.drawable.connection_check));
                        connectionStatusLayout.setVisibility(View.VISIBLE);
                        connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_check));
                        connectionStatusLayout.setVisibility(View.GONE);
                    }
                    sendPingRequestToCheckNetworkSpeed();
                }
            } else if (result.equals("connecting_call")) {
                Log.d("FKDemoTest", "UearnHome - inside connecting_call");
                SmarterSMBApplication.outgoingCallNotInStartMode = true;
                NotificationData.isSocketResponse = false;
                SmarterSMBApplication.systemAlertReceived = false;
                navigateToUearnActivity();
            } else if (result.equals("call_external_customer")) {
                launchRecentApp();
                String externalCustomerNo = ApplicationSettings.getPref(AppConstants.EXTERNAL_CUSTOMER_NUMBER, "");
                if(externalCustomerNo != null && !externalCustomerNo.isEmpty()){
                    adhocCallEditText.setText(externalCustomerNo);
                }
            } else if (result.equals("system_alert_received")) {
                launchRecentApp();
                SmarterSMBApplication.outgoingCallNotInStartMode = true;
                navigateToUearnActivity();
            } else if(result.equals("sign_out")) {
                SmarterSMBApplication.isFirstCall = false;
                NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

                WebStorage webstorage = WebStorage.getInstance();
                webstorage.deleteAllData();
                SmarterSMBApplication.appSettings.setLogout(true);

                ApplicationSettings.putPref(AppConstants.SUPERVISOR_EMAIL, "");
                ApplicationSettings.putPref(AppConstants.CLOUD_TELEPHONY, "");
                ApplicationSettings.putPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
                ApplicationSettings.putPref(AppConstants.DISABLE_CALL_REC_BUTTONS, false);
                ApplicationSettings.putPref(AppConstants.APP_UPDATE, false);
                finish();
            } else if (result.equals("Headset is plugged")) {

            } else if (result.equals("Headset is unplugged")) {

            } else {
                if (result != null && !(result.isEmpty())) {
                    if (result.startsWith("clickview")) {
                        String[] args = result.split("@", 2);
                        int resID = getResources().getIdentifier(args[1], "id", getPackageName());
                        try {
                            View toClick = findViewById(resID);
                            toClick.performClick();
                        } catch (Exception e) {

                        }
                    } else {
                        ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, result);
                        if (isActivityVisible()) {
                            if (result.startsWith("http://") || result.startsWith("https://")) {
                                webViewText.loadUrl(result);
                            } else {
                                if (NotificationData.backToNetworkResponseMsg != null && !NotificationData.backToNetworkResponseMsg.isEmpty()) {
                                    String viewData = "<!DOCTYPE html><html><head><meta name='viewport' content='width=device-width, user-scalable=no'/><title>Viewport Test</title></head><body style='margin:0px;'><div><center><font color=green><H2>" + NotificationData.backToNetworkResponseMsg + "</H2></center></div></body></html>";
                                    webViewText.loadDataWithBaseURL(null, viewData, "text/html", "utf-8", null);
                                    NotificationData.backToNetworkResponseMsg = "";
                                } else {
                                    webViewText.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);
                                }
                            }
                        }
                    }
                }
            }
            if(SmarterSMBApplication.moveToNormal) {
                //SmarterSMBApplication.moveToNormal = false;
                if(SmarterSMBApplication.legADisconnected) {
                    SmarterSMBApplication.legADisconnected = false;
                }
                SmarterSMBApplication.actionMoveToNormalProcessed = true;
                moveToNormalAction();
            }

        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        setContentView(R.layout.uearnsmblayout);
        activity = this;
        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
            if (fkControl) {
                NotificationManager notifManager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancelAll();
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    NotificationManager notifManager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    notifManager.cancelAll();
                }
            }
        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
            if (ibControl) {
                NotificationManager notifManager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancelAll();
            }
        }

        if ((SmarterSMBApplication.SmartUser != null) && SmarterSMBApplication.SmartUser.getEmulationOn()) {
            try {
                if (mMessageReceiver != null) {
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                }
            } catch (Exception e) {
                Log.d("SIOTEST", "mMessageReceiver Unreg Error" + e.getMessage());
            }
        }

        try {
            if (mMessageReceiver != null) {
                LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("remote-dialer-request-event"));
            }
        } catch (Exception e) {
            Log.d("SIOTEST", "mMessageReceiver Reg Error" + e.getMessage());
        }

        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                decorView = getWindow().getDecorView();
                decorView.setOnSystemUiVisibilityChangeListener(this);
            }
        }

        checkHeadPhoneStatus();


        try {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            new CustomTwoButtonDialog().signOut(UearnHome.this);
        }

        flp_count = 1;
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                supportInvalidateOptionsMenu();
            }
        });

        initializeNavigation();
        remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
        if(remoteAutoEnabled == null || remoteAutoEnabled.isEmpty()){
            remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.RAD_OPTIMIZED_POST, "");
        }
        instance = this;
        initUi();

        hideMenuItemsForFKAndIB();

        if ((SmarterSMBApplication.SmartUser != null) && !SmarterSMBApplication.SmartUser.getEmulationOn()) {
            if(SmarterSMBApplication.endTheSession){
                //Log.d("LoginTime", "UearnHome - totalLoggedInTime()- 1");
                totalLoggedInTime();
                if (connectingStatus != null) {
                    connectionStatus.setText(getResources().getString(R.string.connection_good_msg));
                    connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_good));
                    testNow.setBackground(getResources().getDrawable(R.drawable.connection_good));
                    connectionStatusLayout.setVisibility(View.VISIBLE);
                    connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_good));
                    enableStartButton();
                    connectionStatusLayout.setVisibility(View.GONE);
                }
            } else {
                testNowLoader.setVisibility(View.VISIBLE);
                testNow.setVisibility(View.GONE);
                if (connectingStatus != null) {
                    connectionStatus.setText(getResources().getString(R.string.connection_check_msg));
                    connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_check));
                    testNow.setBackground(getResources().getDrawable(R.drawable.connection_check));
                    connectionStatusLayout.setVisibility(View.VISIBLE);
                    connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_check));
                    sendPingRequestToCheckNetworkSpeed();
                    connectionStatusLayout.setVisibility(View.GONE);
                }
            }
        }

        initCalender();
        if (CommonUtils.isNetworkAvailable(this)) {
            if(SmarterSMBApplication.navigateFromUearnProfile){

            } else {
                getUearnDashboardInfo();
                getContactedList();
            }
        } else {
            daysLeft();
            Handler uiHandler = new Handler();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    getOfflineUearnValue();
                    getOfflineContactedData();
                }
            });

            connectedLoader.setVisibility(View.GONE);
            completedFollowups.setVisibility(View.VISIBLE);
            if(contactedInfoList != null && !contactedInfoList.equals("null") && contactedInfoList.size() > 0) {
                completedFollowups.setText(String.valueOf(contactedInfoList.size()));
            } else {
                completedFollowups.setText("0");
            }

            sendRemoteDialStartRequest = false;
            uearnLoader.setVisibility(View.GONE);
            connectedLoader.setVisibility(View.GONE);
            completedFollowups.setVisibility(View.VISIBLE);
            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
            internet_status.setText("NO INTERNET");
            internet_status_goal_before.setText("NO INTERNET");
            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
            noNetworkAnimation(internet_status, internet_status_goal_before);
            disableStartButton();
            connectionStatusLayout.setVisibility(View.GONE);
        }
        reactIntent();
        bizPrize = ApplicationSettings.getPref(AppConstants.BIZ_PRIZE_VALUE, "");

        if(remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()){
            firstCallFollowup.setVisibility(View.GONE);
        } else {
            firstCallFollowup.setVisibility(View.VISIBLE);
        }
        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {   daysLeft();
            agentGoalLayout.setVisibility(View.VISIBLE);
            youCanEarnMore.setVisibility(View.VISIBLE);
            setUearnTodayLayoutVisibility("visible");
            setStartButtonText();

            if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                    || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                    || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                disableStartButton();
            }else{
               enableStartButton();
            }
            daysLeft();
            if(currentDay == 1) {
                String lastUpdateAt = ApplicationSettings.getPref(AppConstants.UPDATED_AT, "");
                if(lastUpdateAt.isEmpty()) {
                    if (bizPrize.equals("")) {
                        bizPrize = "10,000";
                        goalLayout.setVisibility(View.VISIBLE);
                        setGoalLayout.setVisibility(View.GONE);
                        setGoalsLayout.setVisibility(View.GONE);
                    } else {
                        goalLayout.setVisibility(View.GONE);
                        setGoalLayout.setVisibility(View.GONE);
                        setGoalsLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    boolean isValid = CommonUtils.compareToDate(lastUpdateAt);
                    if(isValid) {
                        bizPrize = "10,000";
                        goalLayout.setVisibility(View.VISIBLE);
                        setGoalLayout.setVisibility(View.GONE);
                        setGoalsLayout.setVisibility(View.GONE);
                    } else {
                        if (bizPrize.equals("")) {
                            bizPrize = "10,000";
                            goalLayout.setVisibility(View.VISIBLE);
                            setGoalLayout.setVisibility(View.GONE);
                            setGoalsLayout.setVisibility(View.GONE);
                        } else {
                            goalLayout.setVisibility(View.GONE);
                            setGoalLayout.setVisibility(View.GONE);
                            setGoalsLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                if (bizPrize.equals("")) {
                    bizPrize = "10,000";
                    goalLayout.setVisibility(View.VISIBLE);
                    setGoalLayout.setVisibility(View.GONE);
                    setGoalsLayout.setVisibility(View.GONE);
                } else {
                    goalLayout.setVisibility(View.GONE);
                    setGoalLayout.setVisibility(View.GONE);
                    setGoalsLayout.setVisibility(View.VISIBLE);
                }
            }
        } else {
            agentGoalLayout.setVisibility(View.GONE);
            youCanEarnMore.setVisibility(View.GONE);
            today_uearn.setVisibility(View.GONE);
            today_uearn_layout.setVisibility(View.GONE);
        }
        getProjectType();
        getLocalDbFlpCounts(v);
        String configText = ApplicationSettings.getPref(AppConstants.RAD_MESSAGE_VALUE,"");
        if (webViewText!=null) {
            if (configText != null && !configText.isEmpty()) {
                try {
                    if (configText.startsWith("http://") || configText.startsWith("https://")) {
                        webViewText.loadUrl(configText);
                    } else {
                        webViewText.loadDataWithBaseURL(null, configText, "text/html", "utf-8", null);

                    }
                } catch (Exception e) {

                }
            }
        }

        if (ApplicationSettings.containsPref(AppConstants.WEB_LAYOUT)) {
            boolean webLayout = ApplicationSettings.getPref(AppConstants.WEB_LAYOUT, false);
            if (webLayout) {
                String homepageWebView = ApplicationSettings.getPref(AppConstants.DEFAULT_HOME_PAGE_WEB_VIEW, "");
                if (homepageWebView != null && !homepageWebView.isEmpty()) {
                    if (homepageWebView.startsWith("http://") || homepageWebView.startsWith("https://")) {
                        defaultMsgWebView.loadUrl(homepageWebView);
                    } else {
                        defaultMsgWebView.loadData(homepageWebView, "text/html", "UTF-8");
                    }
                }
            }
        }

        if (UearnActivity.goToHomeScreen) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UearnActivity.goToHomeScreen = false;
                    UearnActivity.onBackPressed = false;
                    manualDialing = false;
                    dialingFromList = false;
                    autoDial = true;
                    click();
                }
            }, 1);
        }

        if (getIntent() != null && getIntent().getBooleanExtra("EXIT", false)) {
            this.finish();
        } else if(getIntent() != null && getIntent().hasExtra("ProfileImage")) {

        } else if(getIntent() != null && getIntent().hasExtra("VoiceTestCompleted")) {
            Boolean isVoiceTestCompleted = getIntent().getExtras().getBoolean("VoiceTestCompleted");
            if(isVoiceTestCompleted) {
                voiceTestContent.setVisibility(View.GONE);
                youCanEarnMore.setVisibility(View.VISIBLE);
                today_uearn.setVisibility(View.GONE);
                today_uearn_layout.setVisibility(View.GONE);
                youCanEarnMore.setVisibility(View.GONE);
                setButtonLayoutVisibility(false);
            } else {
            }
        }
        if(ApplicationSettings.containsPref(AppConstants.USER_STATUS)) {
            String user_status = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
            if(user_status.equals("Voice Test Completed")) {
                voiceTestContent.setVisibility(View.GONE);
                youCanEarnMore.setVisibility(View.VISIBLE);
                today_uearn.setVisibility(View.GONE);
                today_uearn_layout.setVisibility(View.GONE);
                youCanEarnMore.setVisibility(View.GONE);
                setButtonLayoutVisibility(false);
            }
        }
    }

    public void getProjectType(){

        String businessLogoURL = ApplicationSettings.getPref(AppConstants.USERINFO_BUSINESS_LOGO_URL,"");
        String urlImage = "";
        if(businessLogoURL != null && !businessLogoURL.isEmpty()){
            urlImage = businessLogoURL;
        }

        String profileProjectType = ApplicationSettings.getPref(AppConstants.MY_PROFILE_PROJECT_TYPE,"");
        if(profileProjectType.equalsIgnoreCase("UEARN") || profileProjectType.equalsIgnoreCase("")){
            goalLayout.setVisibility(View.GONE);
            setGoalsLayout.setVisibility(View.VISIBLE);
            if(urlImage != null && !urlImage.isEmpty()) {
                Picasso.get().load(urlImage).into(setGoalsLayoutImageView);
            }
            ll_non_uearn.setVisibility(View.GONE);
            ll_update_goalLayout.setVisibility(View.GONE);
            bizPrize = ApplicationSettings.getPref(AppConstants.BIZ_PRIZE_VALUE, "");
            if (bizPrize.equals("")) {
                bizPrize = "10,000";
                goalLayout.setVisibility(View.VISIBLE);
                if(urlImage != null && !urlImage.isEmpty()) {
                    Picasso.get().load(urlImage).into(goalLayoutImageView);
                }
                setGoalLayout.setVisibility(View.GONE);
                setGoalsLayout.setVisibility(View.GONE);
            } else {
                goalLayout.setVisibility(View.GONE);
                setGoalLayout.setVisibility(View.GONE);
                setGoalsLayout.setVisibility(View.VISIBLE);
                if(urlImage != null && !urlImage.isEmpty()) {
                    Picasso.get().load(urlImage).into(setGoalsLayoutImageView);
                }
            }
        } else if(profileProjectType.contains("_NONSMBGIG")){
            goalLayout.setVisibility(View.GONE);
            setGoalsLayout.setVisibility(View.GONE);
            ll_non_uearn.setVisibility(View.VISIBLE);
            if(urlImage != null && !urlImage.isEmpty()) {
                Picasso.get().load(urlImage).into(nonUearnLayoutImageView);
            }
            tv_user_name.setText(ApplicationSettings.getPref(AppConstants.USERINFO_NAME, ""));
            ll_update_goalLayout.setVisibility(View.GONE);
        } else{
            goalLayout.setVisibility(View.GONE);
            setGoalsLayout.setVisibility(View.VISIBLE);
            if(urlImage != null && !urlImage.isEmpty()) {
                Picasso.get().load(urlImage).into(setGoalsLayoutImageView);
            }
            ll_non_uearn.setVisibility(View.GONE);
            ll_update_goalLayout.setVisibility(View.GONE);
            bizPrize = ApplicationSettings.getPref(AppConstants.BIZ_PRIZE_VALUE, "");
            if (bizPrize.equals("")) {
                bizPrize = "10,000";
                goalLayout.setVisibility(View.VISIBLE);
                if(urlImage != null && !urlImage.isEmpty()) {
                    Picasso.get().load(urlImage).into(goalLayoutImageView);
                }
                setGoalLayout.setVisibility(View.GONE);
                setGoalsLayout.setVisibility(View.GONE);
            } else {
                goalLayout.setVisibility(View.GONE);
                setGoalLayout.setVisibility(View.GONE);
                setGoalsLayout.setVisibility(View.VISIBLE);
                if(urlImage != null && !urlImage.isEmpty()) {
                    Picasso.get().load(urlImage).into(setGoalsLayoutImageView);
                }
            }
        }
    }

    private void noNetworkAnimation(TextView internet_status, TextView internet_status_goal_before) {
        noNetworkAnimation = new AlphaAnimation(0.0f, 2.0f);
        noNetworkAnimation.setDuration(500);
        noNetworkAnimation.setStartOffset(20);
        noNetworkAnimation.setRepeatMode(Animation.REVERSE);
        noNetworkAnimation.setRepeatCount(Animation.INFINITE);
        internet_status.startAnimation(noNetworkAnimation);
        internet_status_goal_before.startAnimation(noNetworkAnimation);
    }

    private void hideMenuItemsForFKAndIB() {
        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
            if (fkControl) {
                if(image_notification != null) {
                    image_notification.setVisibility(View.VISIBLE);
                }
                if(image_chat != null) {
                    image_chat.setVisibility(View.VISIBLE);
                }
                if(image_sync != null) {
                    image_sync.setVisibility(View.VISIBLE);
                }
                long notificationCount = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
                if(notificationCount > 0){
                    tv_message_count.setVisibility(View.VISIBLE);
                } else{
                    tv_message_count.setVisibility(View.GONE);
                }
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if(image_notification != null) {
                        image_notification.setVisibility(View.VISIBLE);
                    }
                    if(image_chat != null) {
                        image_chat.setVisibility(View.VISIBLE);
                    }
                    if(image_sync != null) {
                        image_sync.setVisibility(View.VISIBLE);
                    }
                    long notificationCount = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
                    if(notificationCount > 0){
                        tv_message_count.setVisibility(View.VISIBLE);
                    } else{
                        tv_message_count.setVisibility(View.GONE);
                    }
                }
            }
        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
            if (ibControl) {
                if(image_notification != null) {
                    image_notification.setVisibility(View.VISIBLE);
                }
                if(image_chat != null) {
                    image_chat.setVisibility(View.VISIBLE);
                }
                if(image_sync != null) {
                    image_sync.setVisibility(View.VISIBLE);
                }
                long notificationCount = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
                if(notificationCount > 0){
                    tv_message_count.setVisibility(View.VISIBLE);
                } else{
                    tv_message_count.setVisibility(View.GONE);
                }
            }
        } else {
            setVisibilityMenuItems("true");
        }
    }

    private void getOfflineContactedData() {
        String data = ApplicationSettings.getPref(AppConstants.CONTACTED_DATA, "");
        if (data != null && !data.isEmpty()) {
            contactedInfoList = new ArrayList<>();
            JSONObject jsonObject = null;
            JSONArray connectedArray = null;
            JSONObject connectedObj = null;
            try {
                if (jsonObject != null && jsonObject.has("success")) {
                    connectedArray = jsonObject.getJSONArray("success");
                    for (int i = 0; i < connectedArray.length(); i++) {
                        connectedObj = connectedArray.getJSONObject(i);
                        ContactedInfo contactedInfo = new ContactedInfo();
                        contactedInfo.id = connectedObj.optString("id");
                        contactedInfo.event_type = connectedObj.optString("event_type");
                        contactedInfo.start_time = connectedObj.optString("start_time");
                        contactedInfo.url = connectedObj.optString("url");
                        contactedInfo.name = connectedObj.optString("name");
                        contactedInfo.number = connectedObj.optString("number");
                        contactedInfo.lead_source = connectedObj.optString("lead_source");
                        contactedInfo.stage = connectedObj.optString("stage");
                        contactedInfo.substage1 = connectedObj.optString("substage1");
                        contactedInfo.substage2 = connectedObj.optString("substage2");
                        contactedInfo.subject = connectedObj.optString("subject");
                        contactedInfo.customkvs = connectedObj.optString("customkvs");
                        contactedInfoList.add(contactedInfo);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            completedFollowups.setVisibility(View.VISIBLE);
                            if(contactedInfoList != null) {
                                completedFollowups.setText(String.valueOf(contactedInfoList.size()));
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                connectedLoader.setVisibility(View.GONE);
                completedFollowups.setVisibility(View.VISIBLE);
                completedFollowups.setText(String.valueOf(contactedInfoList.size()));
                e.printStackTrace();
            }
        }
    }

    private void getOfflineUearnValue() {
        String data = ApplicationSettings.getPref(AppConstants.UEARN_DASHBOARD_DATA, "");
        if (data != null && !data.isEmpty()) {
            JSONObject jsonObject = null;
            JSONArray customersList = null;
            JSONObject custObj = null;
            String[] customerNames = null;
            try {
                jsonObject = new JSONObject(data);
                if (jsonObject.has("uearned")) {
                    String uearnedValue = jsonObject.getString("uearned");
                    uearnedTextView.setText(uearnedValue);
                }
                if (jsonObject.has("bonus")) {
                    String bonusValue = jsonObject.getString("bonus");
                    //uearnedBonusTextView.setText("Includes bonus " + bonusValue);
                }

                if (jsonObject.has("audited_score")) {
                    String auditedScore = jsonObject.getString("audited_score");
                    ApplicationSettings.putPref(AppConstants.USER_AUDIT_SCORE, auditedScore);
                }
                bizPrize = ApplicationSettings.getPref(AppConstants.BIZ_PRIZE_VALUE, "");
                if (jsonObject.has("mtd_earned")) {
                    mtdEarn = jsonObject.getString("mtd_earned");
                    youEarnTv.setText(mtdEarn);
                    String earnedValue = "Rs." + bizPrize;
                    showGoalSeekbar(mtdEarn);

                    if(mtdEarn != null && !mtdEarn.isEmpty() && !mtdEarn.equals("Rs. 0")) {
                        String earnLSub = mtdEarn.substring(4);
                        String earnRSub = "";
                        if (earnLSub != null && !earnLSub.isEmpty() && !earnLSub.equals("0") && earnLSub.length() > 0) {
                            earnRSub = earnLSub.substring(0, earnLSub.length() - 3);
                        }
                        String result = bizPrize.replaceAll("['.,]", "");
                        float toMeetGoal;
                        if(result != null && !result.isEmpty() && earnLSub != null && !earnLSub.isEmpty()){
                            if (Float.parseFloat(result) > Float.parseFloat(earnLSub)) {
                                toMeetGoal = Float.parseFloat(result) - Float.parseFloat(earnLSub);
                                earnTv.setText(String.format("%.2f", toMeetGoal));
                            } else {
                                goalsCompletedLayout.setVisibility(View.VISIBLE);
                                setGoalLayout.setVisibility(View.GONE);
                                setGoalsLayout.setVisibility(View.GONE);
                                goalLayout.setVisibility(View.GONE);
                            }
                        } else {
                            setGoalLayout.setVisibility(View.GONE);
                            setGoalsLayout.setVisibility(View.GONE);
                        }
                    }
                    if (earnedValue.equals(mtdEarn)) {
                        goalsCompletedLayout.setVisibility(View.VISIBLE);
                        setGoalLayout.setVisibility(View.GONE);
                        setGoalsLayout.setVisibility(View.GONE);
                        goalLayout.setVisibility(View.GONE);
                    }
                }

                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        String activeTime = jsonObject.optString("active_time");
                        if (activeTime != null && !activeTime.isEmpty() && !activeTime.equals("null") && !activeTime.equals("0:0:0") && !activeTime.equals("00:00:00") && !activeTime.equals("0h 0m 0s")) {
                            //Log.d("LoginTime", "active_time 1" + activeTime);
                            followupsToDo.setText(activeTime);
                        } else {
                            long totalActiveTime = ApplicationSettings.getPref(AppConstants.TOTAL_ACTIVE_TIME, 0l);
                            convertActiveMillisecondsToTime(totalActiveTime);
                            //Log.d("LoginTime", "active_time 2" + activeTimeFromApp);
                            followupsToDo.setText(activeTimeFromApp);
                        }
                    } else {
                        String activeTime = jsonObject.optString("active_time");
                        if (activeTime != null && !activeTime.isEmpty()) {
                            //Log.d("LoginTime", "active_time 3" + activeTime);
                            followupsToDo.setText(activeTime);
                        }
                    }
                } else {
                    String activeTime = jsonObject.optString("active_time");
                    if (activeTime != null && !activeTime.isEmpty()) {
                        //Log.d("LoginTime", "active_time 4" + activeTime);
                        followupsToDo.setText(activeTime);
                    }
                }

                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        String loggedInTime = jsonObject.optString("loggedin_time");
                        if (loggedInTime != null && !loggedInTime.isEmpty() && !loggedInTime.equals("null") && !loggedInTime.equals("0:0:0") && !loggedInTime.equals("00:00:00") && !loggedInTime.equals("0h 0m 0s")) {
                            //Log.d("LoginTime", "loggedin_time 1" + loggedInTime);
                            followupsMissed.setText(loggedInTime);
                            //followupsMissed.setTextSize(30);
                        } else {
                            long totalLoggedInTime = ApplicationSettings.getPref(AppConstants.TOTAL_LOGGED_IN_TIME, 0l);
                            convertLoggedInMillisecondsToTime(totalLoggedInTime);
                            //Log.d("LoginTime", "loggedin_time 2" + loggedInTimeFromApp);
                            followupsMissed.setText(loggedInTimeFromApp);
                            //followupsMissed.setTextSize(30);
                        }
                    }
                }

                String loginHrs = jsonObject.optString("login_hours");
                if (loginHrs != null && !loginHrs.isEmpty()) {
                    loginHours.setText(loginHrs);
                }

                String brkTime = jsonObject.optString("break_time");
                if (brkTime != null && !brkTime.isEmpty()) {
                    breakTime.setText(brkTime);
                }

                String tlkTime = jsonObject.optString("talk_time");
                if (tlkTime != null && !tlkTime.isEmpty()) {
                    talkTime.setText(tlkTime);
                }

                String totCalls = jsonObject.optString("total_calls");
                if (totCalls != null && !totCalls.isEmpty()) {
                    totalCalls.setText(totCalls);
                }

                String earnDifference = jsonObject.optString("earn_difference");
                if(earnDifference != null && !earnDifference.isEmpty()){
                    earned_remaing_tv.setText(earnDifference);
                }
                String goalValue = jsonObject.optString("goal_value");
                if(goalValue != null && !goalValue.isEmpty()){
                    earnTv.setText(goalValue);
                }

            } catch (Exception e) {

            }
        }

        getProjectType();
    }

    private void daysLeft() {
        Calendar calendar = Calendar.getInstance();
        lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        daysLeft = lastDay - currentDay;
        numberOfDaysLeft.setText(String.valueOf(daysLeft) + " more days to earn ");
    }

    private void showGoalSeekbar (String mtd_earn) {
        if(mtdEarn != null && !mtdEarn.isEmpty() && !mtdEarn.equals("Rs. 0")) {
            String earnLSub = mtd_earn.substring(4);
            String earnRSub = "";
            if(earnLSub != null && !earnLSub.isEmpty() && !earnLSub.equals("0") && earnLSub.length() > 0) {
                earnRSub = earnLSub.substring(0, earnLSub.length() - 3);
            }
            String result = bizPrize.replaceAll("['.,]", "");

            float toMeetGoal;
            if(result != null && !result.isEmpty() && earnLSub != null && !earnLSub.isEmpty()) {
                if(Float.parseFloat(result) > Float.parseFloat(earnLSub)) {
                    toMeetGoal = Float.parseFloat(result) - Float.parseFloat(earnLSub);
                    earnTv.setText(String.format("%.2f", toMeetGoal));
                } else {
                    goalsCompletedLayout.setVisibility(View.VISIBLE);
                    setGoalLayout.setVisibility(View.GONE);
                    setGoalsLayout.setVisibility(View.GONE);
                    goalLayout.setVisibility(View.GONE);
                }
            } else {
                //goalsCompletedLayout.setVisibility(View.VISIBLE);
                setGoalLayout.setVisibility(View.GONE);
                setGoalsLayout.setVisibility(View.GONE);
                //goalLayout.setVisibility(View.GONE);
            }

        } else {
            earnTv.setText(String.valueOf(bizPrize));
        }

        float currentPercent = 0;
        float remainingPercent = 0;

        if(currentDay != 0) {
            currentPercent = Float.parseFloat(String.valueOf(currentDay))/ Float.parseFloat(String.valueOf(lastDay)) * 100;
        }
        if(daysLeft != 0) {
            remainingPercent = Float.parseFloat(String.valueOf(daysLeft))/ Float.parseFloat(String.valueOf(lastDay)) * 100;
        }

        int currentPercentage = Math.round(currentPercent);
        int remainingPercentage = Math.round(remainingPercent);
        initDataToSeekbar(currentPercentage, remainingPercentage);
    }
    JSONObject rsponseObj;
    private void setGoalValue(String goalAmount) {
        if (CommonUtils.isNetworkAvailable(this)) {
            final String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            final JSONObject userDetailObj = new JSONObject();
            JSONArray userInfoArr = new JSONArray();
            try {
                userDetailObj.put("bizprize", goalAmount);
                Date todayDate = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
                String todayString = formatter.format(todayDate);
                userDetailObj.put("due_date", todayString);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            new AsyncTask<Void, Void, JSONObject>() {
                ProgressDialog progressBar;

                @Override
                protected void onPreExecute() {
                    //progressBar = ProgressDialog.show(LiveMeetingActivity.this, "", "Sending...");
                    super.onPreExecute();
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    JSONObject jsonObject = null;
                    String url = Urls.getUpdateUserDetailsUrl(user_id);
                    rsponseObj = DataUploadUtils.putJsonData(url, userDetailObj);
                    return rsponseObj;
                }

                @Override
                protected void onPostExecute(JSONObject jObject) {
                    super.onPostExecute(jObject);

                    try {
                        if ((rsponseObj != null) && (rsponseObj.getString("success") != null)) {
                            String response = rsponseObj.getString("success");
                            JSONObject jsonResponse = new JSONObject(response);
                            String bizPrize1 = jsonResponse.optString("bizprize");
                            String updated_at = jsonResponse.optString("due_date");
                            bizPrize = bizPrize1;
                            showGoalValue(bizPrize);
                            ApplicationSettings.putPref(AppConstants.BIZ_PRIZE_VALUE, bizPrize);
                            ApplicationSettings.putPref(AppConstants.UPDATED_AT, updated_at);
                        }

                    } catch (JSONException e) {
                        Log.e("UearnHome", "JSON Parsing error on upload response");
                    }
                }
            }.execute();
        }

        getProjectType();
    }

    private void showGoalValue(String goalValue) {
        if(mtdEarn != null && !mtdEarn.isEmpty() && !mtdEarn.equals("Rs. 0")) {
            String earnLSub = mtdEarn.substring(4);
            String earnRSub = "";
            if(earnLSub != null && !earnLSub.isEmpty() && !earnLSub.equals("0") && earnLSub.length() > 0) {
                earnRSub = earnLSub.substring(0, earnLSub.length() - 3);
            }
            String result = bizPrize.replaceAll("['.,]", "");
            float toMeetGoal;
            if(result != null && !result.isEmpty() && earnLSub != null && !earnLSub.isEmpty()) {
                if(Float.parseFloat(result) > Float.parseFloat(earnLSub)) {
                    toMeetGoal = Float.parseFloat(result) - Float.parseFloat(earnLSub);
                    earnTv.setText(String.format(result));
                    earned_remaing_tv.setText(String.format("%.2f", toMeetGoal));
                } else {
                    goalsCompletedLayout.setVisibility(View.VISIBLE);
                    setGoalLayout.setVisibility(View.GONE);
                    setGoalsLayout.setVisibility(View.GONE);
                    goalLayout.setVisibility(View.GONE);
                }
            } else {
                goalsCompletedLayout.setVisibility(View.VISIBLE);
                setGoalLayout.setVisibility(View.GONE);
                setGoalsLayout.setVisibility(View.GONE);
                goalLayout.setVisibility(View.GONE);
            }

        } else {
            earnTv.setText(String.valueOf(goalValue));
            earned_remaing_tv.setText(String.valueOf(goalValue));
        }
    }

    public static UearnHome getInstance() {
        return instance;
    }

    public void refresh() {
        onResume();
    }

    private void getContactedList() {
        if (CommonUtils.isNetworkAvailable(this)) {
            completedFollowups.setVisibility(View.VISIBLE);
            connectedLoader.setVisibility(View.VISIBLE);
            final String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            final UserActivities userActivities = new UserActivities(userId, start_time, end_time);
            new APIProvider.GetContactedList(userActivities, 0, this, "", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    connectedLoader.setVisibility(View.GONE);
                    completedFollowups.setVisibility(View.VISIBLE);
                    contactedInfoList = new ArrayList<>();
                    if (data != null && !data.isEmpty()) {
                        JSONObject jsonObject = null;
                        JSONArray connectedArray = null;
                        JSONObject connectedObj = null;
                        try {
                            jsonObject = new JSONObject(data);
                            if (jsonObject.has("success")) {
                                ApplicationSettings.putPref(AppConstants.CONTACTED_DATA, data);
                                connectedArray = jsonObject.getJSONArray("success");
                                for (int i = 0; i < connectedArray.length(); i++) {
                                    connectedObj = connectedArray.getJSONObject(i);
                                    ContactedInfo contactedInfo = new ContactedInfo();
                                    contactedInfo.id = connectedObj.optString("id");
                                    contactedInfo.event_type = connectedObj.optString("event_type");
                                    contactedInfo.start_time = connectedObj.optString("start_time");
                                    contactedInfo.url = connectedObj.optString("url");
                                    contactedInfo.name = connectedObj.optString("name");
                                    contactedInfo.number = connectedObj.optString("number");
                                    contactedInfo.lead_source = connectedObj.optString("lead_source");
                                    contactedInfo.stage = connectedObj.optString("stage");
                                    contactedInfo.substage1 = connectedObj.optString("substage1");
                                    contactedInfo.substage2 = connectedObj.optString("substage2");
                                    contactedInfo.subject = connectedObj.optString("subject");
                                    contactedInfo.customkvs = connectedObj.optString("customkvs");
                                    contactedInfoList.add(contactedInfo);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        connectedLoader.setVisibility(View.GONE);
                                        completedFollowups.setVisibility(View.VISIBLE);
                                        completedFollowups.setText(String.valueOf(contactedInfoList.size()));
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            connectedLoader.setVisibility(View.GONE);
                            completedFollowups.setVisibility(View.VISIBLE);
                            completedFollowups.setText(String.valueOf(contactedInfoList.size()));
                            e.printStackTrace();
                        }
                    } else {
                        connectedLoader.setVisibility(View.GONE);
                        Handler uiHandler = new Handler();
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                getOfflineContactedData();
                            }
                        });

                    }
                }

            }).call();
        } else {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendRemoteDialStartRequest = false;
                    uearnLoader.setVisibility(View.GONE);
                    connectedLoader.setVisibility(View.GONE);
                    completedFollowups.setVisibility(View.VISIBLE);
                    connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                    connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
                    testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
                    connectionStatusLayout.setVisibility(View.VISIBLE);
                    connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
                    internet_status.setText("NO INTERNET");
                    internet_status_goal_before.setText("NO INTERNET");
                    internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                    internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                    noNetworkAnimation(internet_status, internet_status_goal_before);
                    disableStartButton();
                    connectionStatusLayout.setVisibility(View.GONE);

                }
            });
        }
    }

    void checkHeadPhoneStatus() {
        myReceiver = new HeadPhoneReciever();
    }

    private void click() {

        if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
//            Cursor flpMissedCusrosr = getFollowUpMissedCursor();
//            if(flpMissedCusrosr != null && flpMissedCusrosr.getCount() > 0) {
//                SmarterSMBApplication.callingFollowUps = true;
//            } else {
//                SmarterSMBApplication.callingFollowUps = false;
//            }

            if (UearnActivity.dismissAlertDialog)
                UearnActivity.dismissAlertDialog = false;

            if (UearnHome.dialingFromList || UearnHome.manualDialing) {

            } else {
                clearPreferences();
                clearAllContext();
            }

            if (remoteAutoEnabled.equals("onsolicit") /*|| remoteAutoDialingStarted*/) {

                if(CommonUtils.allowASF()) {
                    if (swipe_to_start.getText().toString().equalsIgnoreCase("STOP") || manual_swipe_to_start.getText().toString().equalsIgnoreCase("STOP")) {
                        today_uearn.setVisibility(View.INVISIBLE);
                        today_uearn_layout.setVisibility(View.INVISIBLE);
                        SmarterSMBApplication.stopRequestFromUearnHome = true;
                        Intent intent4 = new Intent(this, MyStopAlertDialog.class);
                        intent4.putExtra("TITLE", "Stop Auto dialling");
                        String stopString = ApplicationSettings.getPref(AppConstants.REMOTE_DIALLING_STOP_ALERT, "Do you want to stop receiving calls from server?");
                        intent4.putExtra("RDMESSAGE", stopString);
                        intent4.putExtra("RDACTION", "STOP");
                        startActivityForResult(intent4, 1);
                        start_layout.setVisibility(View.GONE);
                    }
                    else {
                        if (CommonUtils.isNetworkAvailable(this)) {
                            if (!sendRemoteDialStartRequest) {
                                if(SmarterSMBApplication.callingFollowUps){
                                    hideWebViewAndAdhocCallLayout();
                                    sendRemoteDialStartRequest("followups onstart");
                                } else {
                                    hideWebViewAndAdhocCallLayout();
                                    sendRemoteDialStartRequest("Ready to take any call");
                                }
                            }
                        } else {
                            sendRemoteDialStartRequest = false;
                            uearnLoader.setVisibility(View.GONE);
                            connectedLoader.setVisibility(View.GONE);
                            completedFollowups.setVisibility(View.VISIBLE);
                            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
                            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
                            connectionStatusLayout.setVisibility(View.VISIBLE);
                            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
                            internet_status.setText("NO INTERNET");
                            internet_status_goal_before.setText("NO INTERNET");
                            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                            noNetworkAnimation(internet_status, internet_status_goal_before);
                            disableStartButton();
                            connectionStatusLayout.setVisibility(View.GONE);
                        }
                    }
                } else if (remoteAutoDialingStarted) {
                    if(SmarterSMBApplication.stopButtonClicked) {
                        SmarterSMBApplication.stopButtonClicked = false;
                        SmarterSMBApplication.stopRequestFromUearnHome = true;
                        Intent intent4 = new Intent(this, MyStopAlertDialog.class);
                        intent4.putExtra("TITLE", "Stop Auto dialling");
                        String stopString = ApplicationSettings.getPref(AppConstants.REMOTE_DIALLING_STOP_ALERT, "Do you want to stop receiving calls from server?");
                        intent4.putExtra("RDMESSAGE", stopString);
                        intent4.putExtra("RDACTION", "STOP");
                        startActivityForResult(intent4, 1);
                        start_layout.setVisibility(View.GONE);
                    }
                } else {
                    if (CommonUtils.isNetworkAvailable(this)) {
                        if (!sendRemoteDialStartRequest) {
                            if(SmarterSMBApplication.stopButtonClicked) {
                                SmarterSMBApplication.stopButtonClicked = false;
                                SmarterSMBApplication.stopRequestFromUearnHome = true;
                                Intent intent4 = new Intent(this, MyStopAlertDialog.class);
                                intent4.putExtra("TITLE", "Stop Auto dialling");
                                String stopString = ApplicationSettings.getPref(AppConstants.REMOTE_DIALLING_STOP_ALERT, "Do you want to stop receiving calls from server?");
                                intent4.putExtra("RDMESSAGE", stopString);
                                intent4.putExtra("RDACTION", "STOP");
                                startActivityForResult(intent4, 1);
                                start_layout.setVisibility(View.GONE);
                            } else if(SmarterSMBApplication.callingFollowUps){
                                hideWebViewAndAdhocCallLayout();
                                sendRemoteDialStartRequest("followups onstart");
                            } else {
                                hideWebViewAndAdhocCallLayout();
                                sendRemoteDialStartRequest("Ready to take any call");
                            }
                        }
                    } else {
                        sendRemoteDialStartRequest = false;
                        uearnLoader.setVisibility(View.GONE);
                        connectedLoader.setVisibility(View.GONE);
                        completedFollowups.setVisibility(View.VISIBLE);
                        connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                        connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
                        testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
                        connectionStatusLayout.setVisibility(View.VISIBLE);
                        connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
                        internet_status.setText("NO INTERNET");
                        internet_status_goal_before.setText("NO INTERNET");
                        internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                        internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                        noNetworkAnimation(internet_status, internet_status_goal_before);
                        disableStartButton();
                        connectionStatusLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                if (CommonUtils.isNetworkAvailable(this)) {
                    if (!sendRemoteDialStartRequest) {
                        if(SmarterSMBApplication.callingFollowUps){
                            hideWebViewAndAdhocCallLayout();
                            sendRemoteDialStartRequest("followups onstart");
                        } else {
                            hideWebViewAndAdhocCallLayout();
                            sendRemoteDialStartRequest("Ready to take any call");
                        }
                    }
                } else {
                    sendRemoteDialStartRequest = false;
                    uearnLoader.setVisibility(View.GONE);
                    connectedLoader.setVisibility(View.GONE);
                    completedFollowups.setVisibility(View.VISIBLE);
                    connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                    connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
                    testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
                    connectionStatusLayout.setVisibility(View.VISIBLE);
                    connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
                    internet_status.setText("NO INTERNET");
                    internet_status_goal_before.setText("NO INTERNET");
                    internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                    internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                    noNetworkAnimation(internet_status, internet_status_goal_before);
                    disableStartButton();
                    connectionStatusLayout.setVisibility(View.GONE);
                }
            }
            return;
        }

        if(ApplicationSettings.containsPref(AppConstants.USER_ROLE) && ApplicationSettings.getPref(AppConstants.USER_ROLE, "") != null) {
            String role = ApplicationSettings.getPref(AppConstants.USER_ROLE, "");
            if (!role.equalsIgnoreCase("user") || !role.equalsIgnoreCase("non-user")) {
                setStartButtonVisibility(true);
                onStartlayout.setVisibility(View.GONE);
                isStart = false;
                noMissedFollowups = true;
                Toast.makeText(this, "Your User Id is not configured as an agent\nYou do not have permissions to make outgoing calls", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (ApplicationSettings.getPref(AppConstants.AUTO_DAILER, false) && ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
            if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                if (!(ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity"))))) {
                    if (checkDb()) {
                        checkAuto = true;
                        Handler uiHandler = new Handler();
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTransaction(new AutoDillerFragment(), 1);
                            }
                        });
                    } else {
                        if(UearnHome.manualDialing) {

                        } else {
                            isStart = false;
                            noMissedFollowups = true;
                            Toast.makeText(this, "You do not have missed follow-ups", Toast.LENGTH_SHORT).show();
                            requestForData();
                        }
                    }
                }
            } else {
                if (checkDb()) {
                    checkAuto = true;
                    Handler uiHandler = new Handler();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fragmentTransaction(new AutoDillerFragment(), 1);
                        }
                    });
                } else {
                    if(UearnHome.manualDialing) {

                    } else {
                        isStart = false;
                        noMissedFollowups = true;
                        Toast.makeText(this, "You do not have missed follow-ups", Toast.LENGTH_SHORT).show();
                        requestForData();
                    }
                }
            }
        } else {
            Handler uiHandler = new Handler();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    fragmentTransaction(new AutoDillerFragment(), 1);
                }
            });
        }
    }

    public static long getstartTime() {
        return start;
    }

    public static long getendTime() {
        return end;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String message = "";
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                if (result.equals("start_request")) {
                    try {
                        message = data.getStringExtra("usermessage");
                    } catch (NullPointerException e) {

                    } catch (Exception e) {

                    }
                    sendRemoteDialStartRequest(message);
                } else {
                    try {
                        message = data.getStringExtra("usermessage");

                    } catch (NullPointerException e) {

                    } catch (Exception e) {

                    }
                    if(message != null && !message.isEmpty()) {
                        SmarterSMBApplication.currentStateIsStartMode = false;
                        SmarterSMBApplication.stayAtHomeScenario = false;
                        sendRemoteDialStopRequest(message);
                    } else {
                        sendRemoteDialStopRequest("Taking a break");
                    }
                    ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, "");
                    ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
                    ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
                    NotificationData.dialledCustomerNumber = "";
                    NotificationData.dialledCustomerName = "";
                    endActiveCall();
                }
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, "");
            }
        }

        if (requestCode == 2) {
            String user_id = "";
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            }

            if (mSocket != null && mSocket.connected()) {
                mSocket.emit("nextcustomer-ack", user_id, resultCode);
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, "");
            } else {
                //Do Nothing
            }
        }

        if (requestCode == 3) {
            if (getUrl != null && !getUrl.isEmpty()) {
                callToServerGetApi(getUrl + "&result=" + resultCode, "", 1);
            }
        }
    }

    private void sendRemoteDialStopRequest(String message) {
        if (CommonUtils.isNetworkAvailable(this)) {
            if(!SmarterSMBApplication.connectedCustomerInProcess){
                SmarterSMBApplication.connectedCustomerInProcess = true;
            }
            new APIProvider.GetRemoteDialerStop(message, 0, this, "Stopping. Please wait..", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    String text = "";
                    String action = "";
                    try {
                        SmarterSMBApplication.isFirstCall = false;
                        JSONObject jsonObject = new JSONObject(data);
                        SmarterSMBApplication.isRemoteDialledStopRequest = true;
                        if (jsonObject.has("text")) {
                            text = jsonObject.getString("text");
                        }
                        if (jsonObject.has("action")) {
                            action = jsonObject.getString("action");
                        }
                    } catch (Exception e) {
                    }
                    SmarterSMBApplication.isRemoteDialledStart = false;
                    SmarterSMBApplication.endTheSession = false;

                    if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
                        boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
                        if (systemControl) {
                            SmarterSMBApplication.disableStatusBarAndNavigation = false;
                        }
                    }

                    if (action != null && !action.isEmpty() && action.equalsIgnoreCase("movetonormal")) {
                        SmarterSMBApplication.moveToNormal = true;
                        ActivityManager am = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
                        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                        if(cn != null && cn.getClassName().equals("smarter.uearn.money.activities.UearnActivity")) {
                            finish();
                        }
                    }
                    if (action != null && !action.isEmpty() && action.equalsIgnoreCase("callfollowups")) {
                        //SmarterSMBApplication.callingFollowUps = true;
                        //sendRemoteDialStartRequest("call followups");
                    } else {
                        if (text != null && !text.isEmpty()) {
                            remoteAutoDialingStarted = false;
                            remoteAutoDialingStartClicked = false;
                            setStartButtonText();
                            if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                    || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                    || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                                disableStartButton();
                            }else{
                               enableStartButton();
                            }
                            ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, text);
                            if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                                if (!remoteAutoDialingStarted) {
                                    getUearnDashboardInfo();
                                    getContactedList();
                                }
                            }
                        }
                    }
                }
            }).call();
        }  else {
            sendRemoteDialStartRequest = false;
            uearnLoader.setVisibility(View.GONE);
            connectedLoader.setVisibility(View.GONE);
            completedFollowups.setVisibility(View.VISIBLE);
            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
            internet_status.setText("NO INTERNET");
            internet_status_goal_before.setText("NO INTERNET");
            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
            noNetworkAnimation(internet_status, internet_status_goal_before);
            disableStartButton();
            connectionStatusLayout.setVisibility(View.GONE);
        }
    }

    private boolean checkDb() {
        Date date = new Date();
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + 1 + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        Cursor cursor1 = db1.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        Cursor cursor2 = null;
        Cursor cursor3 = null;
        if (cursor1 == null || cursor1.getCount() <= 0) {
            try {
                if (cursor1 != null) {
                    cursor1.close();
                    cursor1 = null;
                }
                String selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT = " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
                cursor2 = db1.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
                if (cursor2 == null || cursor2.getCount() <= 0) {
                    try {
                        if (cursor2 != null) {
                            cursor2.close();
                            cursor2 = null;
                        }
                        String selection3 = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + "<" + start + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
                        cursor3 = db.query("remindertbNew", null, selection3, null, null, null, "START_TIME DESC");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (cursor1 != null && cursor1.getCount() > 0) {
            return true;
        } else if (cursor2 != null && cursor2.getCount() > 0) {
            return true;
        } else return cursor3 != null && cursor3.getCount() > 0;
    }

    private void fragmentTransaction(Fragment fragment, int v) {
        try {
            if (v == 0) {

            }
            start_layout.setVisibility(View.GONE);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.detach(fragment);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void sendRemoteDialStartRequest(String message) {
        if (CommonUtils.isNetworkAvailable(this)) {
            setVisibilityMenuItems("false");
            SmarterSMBApplication.isFirstCall = true;
            SmarterSMBApplication.isCallLanded = false;
            setButtonLayoutVisibility(false);
            onStartlayout.setVisibility(View.VISIBLE);

            boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
            if(truePredictive){
                if(SmarterSMBApplication.isRemoteDialledStart || SmarterSMBApplication.callingFollowUps) {
                    onStartlayout.setVisibility(View.GONE);
                    setStartButtonVisibility(true);
                }
            }

            if(SmarterSMBApplication.connectedCustomerInProcess){
                SmarterSMBApplication.connectedCustomerInProcess = false;
            }
            if(SmarterSMBApplication.disconnectedCustomerInProcess){
                SmarterSMBApplication.disconnectedCustomerInProcess = false;
            }
            sendRemoteDialStartRequest = true;

            new APIProvider.GetRemoteDialerStart(message, 0, this, "Scheduling. Please wait..", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                    String text = "", url = "";
                    String action = "";
                    try {
                        //connectingStatus.setText("Connecting...");
                        if(data == null || data.length() == 0 || data.equalsIgnoreCase("")){
                            setButtonLayoutVisibility(true);
                        }
                        SmarterSMBApplication.isRemoteDialledStopRequest = false;
                        JSONObject jsonObject = new JSONObject(data);
                        setButtonLayoutVisibility(true);
                        if (jsonObject.has("text")) {
                            text = jsonObject.getString("text");
                        }
                        if (jsonObject.has("show")) {
                            connectingText = jsonObject.getString("show");
                            if(connectingText != null && !connectingText.isEmpty() && connectingText.equalsIgnoreCase("Please wait..")){
                                connectingText = "Please wait...";
                            }
                            connectingStatus.setText(connectingText);
                        }

                        if (jsonObject.has("waittime")) {
                            waittime = jsonObject.getLong("waittime");
                            startTimer(waittime);
                        }

                        if (jsonObject.has("stay_at_home")) {
                            stayathome = jsonObject.getBoolean("stay_at_home");
                            if(stayathome) {
                                SmarterSMBApplication.stayAtHomeScenario = true;
                                SmarterSMBApplication.outgoingCallNotInStartMode = true;
                            }
                        }

                        if (jsonObject.has("loggedintime")) {
                            long loggedinTime = jsonObject.getLong("loggedintime");
                            if(loggedinTime != 0) {
                                ApplicationSettings.putPref(AppConstants.TOTAL_LOGGED_IN_TIME, 0l);
                                SmarterSMBApplication.totalLoggedInTime = "";
                                ApplicationSettings.putPref(AppConstants.TOTAL_LOGGED_IN_TIME, loggedinTime);
                                //Log.d("LoginTime", "UearnHome-startRequest() loggedinTime IF" + loggedinTime);
                            }
                        }

                        if (jsonObject.has("activetime")) {
                            long activeTime = jsonObject.getLong("activetime");
                            if(activeTime != 0) {
                                ApplicationSettings.putPref(AppConstants.TOTAL_ACTIVE_TIME, 0l);
                                SmarterSMBApplication.totalActiveTime = "";
                                ApplicationSettings.putPref(AppConstants.TOTAL_ACTIVE_TIME, activeTime);
                                //Log.d("LoginTime", "UearnHome-startRequest() activeTime" + activeTime);
                            }
                        }

                        if (jsonObject.has("action")) {
                            action = jsonObject.getString("action");

                            if (action != null && !action.isEmpty() && action.equalsIgnoreCase("pdstarted")) {
                                SmarterSMBApplication.followupsInPredictive = false;
                            }

                            if (action != null && !action.isEmpty() && action.equalsIgnoreCase("followupsstarted")) {
                                SmarterSMBApplication.followupsInPredictive = true;
                            }

                            if(action != null && !action.isEmpty() && action.equalsIgnoreCase("movetonormal")) {
                                SmarterSMBApplication.moveToNormal = true;
                                SmarterSMBApplication.actionMoveToNormalProcessed = true;
                                moveToNormalAction();
                            }

                            if(action != null && !action.isEmpty() && action.equalsIgnoreCase("movetoslotbooking")) {
                                showSlotsDialog(text);
                                if(waitTimer != null) {
                                    waitTimer.cancel();
                                    waitTimer = null;
                                }
                                SmarterSMBApplication.moveToNormal = false;
                                //sendRemoteDialStartRequest = true;
                                onStartlayout.setVisibility(View.GONE);
                                setStartButtonVisibility(true);
                                sendRemoteDialStopRequest("Taking a break");
                            }

                            if(action != null && !action.isEmpty() && action.equalsIgnoreCase("noslot")) {

                                SmarterSMBApplication.moveToNormal = true;
                                SmarterSMBApplication.actionMoveToNormalProcessed = true;
                                moveToNormalAction();
                            }
                        }
                        if (jsonObject.has("url")) {
                            url = jsonObject.getString("url");
                        }

                        if (jsonObject.has("rdmessage")) {
                            String message = jsonObject.getString("rdmessage");
                            ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, message);
                            if (isActivityVisible()) {
                                if (message.startsWith("http://") || message.startsWith("https://")) {
                                    webViewText.loadUrl(message);
                                } else {
                                    webViewText.loadDataWithBaseURL(null, message, "text/html", "utf-8", null);
                                }
                            }
                        }

                        SmarterSMBApplication.isRemoteDialledStart = true;

                        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
                            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
                            if (systemControl) {
                                SmarterSMBApplication.disableStatusBarAndNavigation = true;
                            }
                        }

                        if (jsonObject.has("info")) {
                            String information = jsonObject.getString("info");
                            JSONObject jsonObject2 = new JSONObject(information);
                            String data1 = "";
                            if (jsonObject2.has("data")) {
                                data1 = jsonObject2.getString("data");
                            }
                            JSONObject infoJsonObject = new JSONObject(data1);
                            String alert_message = infoJsonObject.getString("message");
                            String alert_title = infoJsonObject.getString("title");
                            String tts_enable = infoJsonObject.getString("tts_enable");
                            String customerNumber = "";
                            if (infoJsonObject.has("customer_number")) {
                                customerNumber = infoJsonObject.getString("customer_number");
                            }
                            String customerName = "";
                            if (infoJsonObject.has("customer_name")) {
                                customerName = infoJsonObject.getString("customer_name");
                            }
                            if (customerNumber != null && !(customerNumber.isEmpty())) {
                                NotificationData.notificationData = true;
                                NotificationData.knolarity_name = customerName;
                                NotificationData.knolarity_number = customerNumber;
                                NotificationData.dialledCustomerNumber = customerNumber;
                                NotificationData.substatus1 = "";
                                NotificationData.substatus2 = "";
                                NotificationData.statusString = "";
                                NotificationData.notes_string = "";

                                if (infoJsonObject.has("status")) {
                                    NotificationData.statusString = infoJsonObject.getString("status");
                                }

                                if (infoJsonObject.has("substatus1")) {
                                    NotificationData.substatus1 = infoJsonObject.getString("substatus1");
                                }

                                if (infoJsonObject.has("substatus2")) {
                                    NotificationData.substatus2 = infoJsonObject.getString("substatus2");
                                }

                                if (infoJsonObject.has("notes")) {
                                    NotificationData.notes_string = infoJsonObject.getString("notes");
                                }
                                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                                    boolean radC2CEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                                    if (radC2CEndpoint) {
                                        CustomerLite customerLiteInfo = new CustomerLite(0, NotificationData.knolarity_name, NotificationData.dialledCustomerNumber, NotificationData.statusString, NotificationData.substatus1, NotificationData.substatus2, "", "", NotificationData.notes_string, "", "");
                                        List<CustomerLite> tempcustomerList = new ArrayList<>();
                                        tempcustomerList.add(0, customerLiteInfo);
                                        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
                                        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
                                        String customerListData = new Gson().toJson(tempcustomerList);
                                        ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData);
                                    }
                                }
                                ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, customerName);
                                ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customerNumber);
                                startRecording();
                            }

                            String sign_out = "";
                            if (infoJsonObject.has("sign_out")) {
                                sign_out = infoJsonObject.getString("sign_out");
                            }

                            String offlineUrl = "";
                            if (infoJsonObject.has("geturl")) {
                                offlineUrl = infoJsonObject.getString("geturl");
                            }

                            getUrl = offlineUrl;

                            if (alert_message != null && !(alert_message.isEmpty())) {
                                Intent intent4 = new Intent(UearnHome.this, MyStopAlertDialog.class);
                                intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                if (infoJsonObject.has("rdmessage")) {
                                    alert_message = infoJsonObject.getString("rdmessage");
                                    intent4.putExtra("RDMESSAGE", alert_message);
                                } else {
                                    intent4.putExtra("MESSAGE", alert_message);
                                }
                                if (infoJsonObject.has("rdaction")) {
                                    String rdaction = infoJsonObject.getString("rdaction");
                                    intent4.putExtra("RDACTION", rdaction);
                                }
                                intent4.putExtra("TTSENABLE", tts_enable);
                                intent4.putExtra("TITLE", alert_title);
                                intent4.putExtra("SIGNOUT", sign_out);
                                if (offlineUrl != null && !offlineUrl.isEmpty()) {
                                    intent4.putExtra("GETURL", offlineUrl);
                                }
                                startActivityForResult(intent4, 3);
                            }
                        }

                    } catch (Exception e) {

                    }

                    if (text != null && !text.isEmpty()) {
                        remoteAutoDialingStarted = true;
                        remoteAutoDialingStartClicked = true;
                        setStartButtonVisibility(false);
                        today_uearn_layout.setVisibility(View.GONE);
                        sendRemoteDialStartRequest = false;
                        if (text.startsWith("http://") || text.startsWith("https://")) {
                            webViewText.loadUrl(text);
                        } else {
                            if(text != null && !text.isEmpty() && text.contains("campaign is already dialled out")) {
                                webViewText.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                            } else if(text != null && !text.isEmpty() && text.contains("You have completed your current campaign")) {
                                webViewText.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                            }else {
                                webViewText.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
                            }
                        }

                        ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, text);
                    } else {
                        sendRemoteDialStartRequest = false;

                        String configText = ApplicationSettings.getPref(AppConstants.RAD_MESSAGE_VALUE, "");
                        if (configText != null && !configText.isEmpty()) {
                            SmarterSMBApplication.actionMoveToNormalProcessed = true;
                            moveToNormalAction();
                        } else {
                            resetHomeScreenToOriginalState();
                        }
                    }
                }
            }).call();
        }  else {
            sendRemoteDialStartRequest = false;
            uearnLoader.setVisibility(View.GONE);
            connectedLoader.setVisibility(View.GONE);
            completedFollowups.setVisibility(View.VISIBLE);
            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
            setButtonLayoutVisibility(true);
            internet_status.setText("NO INTERNET");
            internet_status_goal_before.setText("NO INTERNET");
            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
            noNetworkAnimation(internet_status, internet_status_goal_before);
            disableStartButton();
            connectionStatusLayout.setVisibility(View.GONE);
        }
    }

    private void enableStartStopButton() {
        setStartButtonVisibility(true);
        enableStartButton();
        setButtonTextAsStop();
    }

    private void startRecording() {
        if (shouldRecord()) {
            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
            if (connectedCustomer != null && !connectedCustomer.isEmpty()) {
                ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, connectedCustomer);
                Intent recorderIntent = new Intent(this, RecorderService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(recorderIntent);
                } else {
                    startService(recorderIntent);
                }
            }
        }
    }

    private void getUearnDashboardInfo() {
        if (CommonUtils.isNetworkAvailable(this)) {
            uearnLoader.setVisibility(View.VISIBLE);
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            UserActivities userActivities = new UserActivities(userId, start_time, end_time);
            Log.e("UearnHome","Dashboard info");
            new APIProvider.GetUearnDashboard(userActivities, 0, this, "", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    uearnLoader.setVisibility(View.GONE);
                    if (data != null && !data.isEmpty()) {
                        ApplicationSettings.putPref(AppConstants.UEARN_DASHBOARD_DATA, data);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(data);
                            if (jsonObject.has("uearned")) {
                                String uearnedValue = jsonObject.getString("uearned");
                                uearnedTextView.setText(uearnedValue);
                            }
                            if (jsonObject.has("bonus")) {
                                String bonusValue = jsonObject.getString("bonus");
                                //uearnedBonusTextView.setText("Includes bonus " + bonusValue);
                            }

                            if (jsonObject.has("audited_score")) {
                                String auditedScore = jsonObject.getString("audited_score");
                                ApplicationSettings.putPref(AppConstants.USER_AUDIT_SCORE, auditedScore);
                            }

                            if (jsonObject.has("mtd_earned")) {
                                mtdEarn = jsonObject.getString("mtd_earned");
                                youEarnTv.setText(mtdEarn);
                                String earnedValue = "Rs." + bizPrize;
                                showGoalSeekbar(mtdEarn);
                                if(mtdEarn != null && !mtdEarn.isEmpty() && !mtdEarn.equals("Rs. 0")) {
                                    String earnLSub = mtdEarn.substring(4);
                                    String earnRSub = "";
                                    if (earnLSub != null && !earnLSub.isEmpty() && !earnLSub.equals("0") && earnLSub.length() > 0) {
                                        earnRSub = earnLSub.substring(0, earnLSub.length() - 3);
                                    }
                                    String result = bizPrize.replaceAll("['.,]", "");
                                    float toMeetGoal;
                                    if(result != null && !result.isEmpty() && earnLSub != null && !earnLSub.isEmpty()){
                                        if (Float.parseFloat(result) > Float.parseFloat(earnLSub)) {
                                            toMeetGoal = Float.parseFloat(result) - Float.parseFloat(earnLSub);
                                            earnTv.setText(String.format("%.2f", toMeetGoal));
                                        } else {
                                            goalsCompletedLayout.setVisibility(View.VISIBLE);
                                            setGoalLayout.setVisibility(View.GONE);
                                            setGoalsLayout.setVisibility(View.GONE);
                                            goalLayout.setVisibility(View.GONE);
                                        }
                                    } else {
                                        //goalsCompletedLayout.setVisibility(View.VISIBLE);
                                        setGoalLayout.setVisibility(View.GONE);
                                        setGoalsLayout.setVisibility(View.GONE);
                                        //goalLayout.setVisibility(View.GONE);
                                        getProjectType();
                                    }

                                }
                                if (earnedValue.equals(mtdEarn)) {
                                    goalsCompletedLayout.setVisibility(View.VISIBLE);
                                    setGoalLayout.setVisibility(View.GONE);
                                    setGoalsLayout.setVisibility(View.GONE);
                                    goalLayout.setVisibility(View.GONE);
                                }
                            }

                            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                                if (fkControl) {
                                    String activeTime = jsonObject.optString("active_time");
                                    if (activeTime != null && !activeTime.isEmpty() && !activeTime.equals("null") && !activeTime.equals("0:0:0") && !activeTime.equals("00:00:00") && !activeTime.equals("0h 0m 0s")) {
                                        //Log.d("LoginTime", "active_time 5" + activeTime);
                                        followupsToDo.setText(activeTime);
                                    } else {
                                        long totalActiveTime = ApplicationSettings.getPref(AppConstants.TOTAL_ACTIVE_TIME, 0l);
                                        convertActiveMillisecondsToTime(totalActiveTime);
                                        //Log.d("LoginTime", "active_time 6" + activeTimeFromApp);
                                        followupsToDo.setText(activeTimeFromApp);
                                    }
                                } else {
                                    String activeTime = jsonObject.optString("active_time");
                                    if (activeTime != null && !activeTime.isEmpty()) {
                                        //Log.d("LoginTime", "active_time 7" + activeTime);
                                        followupsToDo.setText(activeTime);
                                    }
                                }
                            } else {
                                String activeTime = jsonObject.optString("active_time");
                                if (activeTime != null && !activeTime.isEmpty()) {
                                    //Log.d("LoginTime", "active_time 8" + activeTime);
                                    followupsToDo.setText(activeTime);
                                }
                            }

                            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                                if (fkControl) {
                                    String loggedInTime = jsonObject.optString("loggedin_time");
                                    if (loggedInTime != null && !loggedInTime.isEmpty() && !loggedInTime.equals("null") && !loggedInTime.equals("0:0:0") && !loggedInTime.equals("00:00:00") && !loggedInTime.equals("0h 0m 0s")) {
                                        //Log.d("LoginTime", "loggedin_time 5" + loggedInTime);
                                        followupsMissed.setText(loggedInTime);
                                        //followupsMissed.setTextSize(30);
                                    } else {
                                        long totalLoggedInTime = ApplicationSettings.getPref(AppConstants.TOTAL_LOGGED_IN_TIME, 0l);
                                        convertLoggedInMillisecondsToTime(totalLoggedInTime);
                                        //Log.d("LoginTime", "loggedin_time 6" + loggedInTimeFromApp);
                                        followupsMissed.setText(loggedInTimeFromApp);
                                    }
                                }
                            }

                            String loginHrs = jsonObject.optString("login_hours");
                            if (loginHrs != null && !loginHrs.isEmpty()) {
                                loginHours.setText(loginHrs);
                            }

                            String brkTime = jsonObject.optString("break_time");
                            if (brkTime != null && !brkTime.isEmpty()) {
                                breakTime.setText(brkTime);
                            }

                            String tlkTime = jsonObject.optString("talk_time");
                            if (tlkTime != null && !tlkTime.isEmpty()) {
                                talkTime.setText(tlkTime);
                            }

                            String totCalls = jsonObject.optString("total_calls");
                            if (totCalls != null && !totCalls.isEmpty()) {
                                totalCalls.setText(totCalls);
                            }

                            String earnDifference = jsonObject.optString("earn_difference");
                            if(earnDifference != null && !earnDifference.isEmpty()){
                                earned_remaing_tv.setText(earnDifference);
                            }
                            String goalValue = jsonObject.optString("goal_value");
                            if(goalValue != null && !goalValue.isEmpty()){
                                earnTv.setText(goalValue);
                            }

                        } catch (JSONException e) {
                            uearnLoader.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    } else {
                        uearnLoader.setVisibility(View.GONE);

                        Handler uiHandler = new Handler();
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                getOfflineUearnValue();
                            }
                        });

                    }
                }
            }).call();
        } else {
            sendRemoteDialStartRequest = false;
            uearnLoader.setVisibility(View.GONE);
            connectedLoader.setVisibility(View.GONE);
            completedFollowups.setVisibility(View.VISIBLE);
            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
            internet_status.setText("NO INTERNET");
            internet_status_goal_before.setText("NO INTERNET");
            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
            noNetworkAnimation(internet_status, internet_status_goal_before);
            disableStartButton();
            connectionStatusLayout.setVisibility(View.GONE);
        }
    }

    private void initializeCalenderDateAndTime() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        start = c.getTimeInMillis();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
        end = c.getTimeInMillis();
    }

    private void clearFlagsBeforeExit() {

        SmarterSMBApplication.isCampaignOver = false;
        SmarterSMBApplication.moveToNormal = false;
        NotificationData.isSocketResponse = false;
        SmarterSMBApplication.systemAlertReceived = false;

        clearFlags();

        setVisibilityMenuItems("true");
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                showSystemUI();
            }
        }

        SmarterSMBApplication.incomingCallRejectedByAgent = false;
        ServiceHandler.agentMissed = false;

        UearnHome.remoteAutoDialingStartClicked = false;
        sendRemoteDialStartRequest = false;
        remoteAutoDialingStarted = false;

        uearnLoader.setVisibility(View.GONE);
        connectedLoader.setVisibility(View.GONE);

        setStartButtonText();
        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
            disableStartButton();
        }else{
            enableStartButton();
        }

        setStartButtonVisibility(true);
        enableStartButton();
        onStartlayout.setVisibility(View.GONE);
        youCanEarnMore.setVisibility(View.VISIBLE);
        setUearnTodayLayoutVisibility("visible");
        start_layout.setVisibility(View.GONE);
        setButtonLayoutVisibility(true);

        Intent myService = new Intent(this, SocketIOService.class);
        stopService(myService);
        //Log.d("SocketIOTest", "Socket IO Call stopService");

        if (ApplicationSettings.containsPref(AppConstants.CALLWAITING_CHOICE)) {
            boolean callWaitingChoice = ApplicationSettings.getPref(AppConstants.CALLWAITING_CHOICE, false);
            if (callWaitingChoice) {
                String ussdCode = "*43#";
                if (ApplicationSettings.containsPref(AppConstants.ACTIVATE_USSD_CODE)) {
                    String activateCode = ApplicationSettings.getPref(AppConstants.ACTIVATE_USSD_CODE, "");
                    if (activateCode != null && !activateCode.isEmpty()) {
                        ussdCode = activateCode;
                    }
                }
                startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + Uri.encode(ussdCode))));
            }
        }
    }

    private void reactIntent() {
        initializeCalenderDateAndTime();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            clearFlagsBeforeExit();
            clearData(1);
        }
        if (getIntent().getBooleanExtra("EXITSIGNIN", false)) {
            clearFlagsBeforeExit();
            clearData(0);
        } else if (getIntent().hasExtra("firstcall")) {
            Intent intent = getIntent();
            if (intent.hasExtra("start")) {
                start = intent.getLongExtra("start", 0);
            }
            if (intent.hasExtra("end")) {
                end = intent.getLongExtra("end", 0);
            }
            v = 0;
            getLocalDbFlpCounts(v);
        } else if (getIntent().hasExtra("FIRST_TIME")) {
            CommonUtils.permissionsCheck(UearnHome.this);
            Intent intent = getIntent();
            boolean check = intent.getBooleanExtra("SETTINGS", false);
            boolean checkData = intent.getBooleanExtra("SETTINGSDATA", false);
            boolean checkTeam = intent.getBooleanExtra("SETTINGSTEAM", false);
            if (check) {
                CustomSingleButtonDialog.buildSingleButtonDialog("Overall Settings", "Failed to sync settings, please check your internet connection.", this, false);
            }
            if (checkData) {
                CustomSingleButtonDialog.buildSingleButtonDialog("Data sync error", "Failed to sync data, please check your internet connection.", this, false);
            }
            if (checkTeam) {
                CustomSingleButtonDialog.buildSingleButtonDialog("Team Settings", "Failed to sync team details, please check your internet connection.", this, false);
            }
        } else if (getIntent().hasExtra("ATOCOMPLETE")) {
            CustomSingleButtonDialog.buildSingleButtonDialog("Auto Dialler", "No more numbers to dial.", this, false);
        }
        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
            if (isKitKat) {
                getSupportActionBar().setElevation(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                goalEarnedTv.removeTextChangedListener(this);
                String originalStr = s.toString();
                if(originalStr.isEmpty()) {
                    goalEarnedTv.setText(originalStr);
                    bizPrize = "";
                } else {
                    try {
                        String originalString = s.toString();
                        Long longval;
                        if (originalString.contains(",")) {
                            originalString = originalString.replaceAll(",", "");
                        }
                        longval = Long.parseLong(originalString);
                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        formatter.applyPattern("#,###,###,###");
                        String formattedString = formatter.format(longval);
                        goalEarnedTv.setText(formattedString);
                        bizPrize = formattedString;
                        goalEarnedTv.setSelection(goalEarnedTv.getText().length());

                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
                goalEarnedTv.addTextChangedListener(this);
            }
        };

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()){
                //hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            hintsTv.setVisibility(View.VISIBLE);
            goalEarnedTv.setFocusable(false);
            if(bizPrize.isEmpty()) {
                goalEarnedTv.setText("10,000");
            } else {
                goalEarnedTv.setText(bizPrize);
            }

            goalEarnedTv.setTextColor(getApplication().getResources().getColor(R.color.white));
            goalEarnedTv.setBackgroundResource(0);
            underLineView.setVisibility(View.VISIBLE);
            setGoalLayout.setVisibility(View.VISIBLE);
            goalEarnedTv.setFocusable(true);
            goalEarnedTv.setVisibility(View.VISIBLE);
            //setGoalLayout.setVisibility(View.GONE);
        }
    }

    private void toggleLeftDrawer() {
        if (drawerLayout.isDrawerOpen(DrawerMenu)) {
            drawerLayout.closeDrawer(DrawerMenu);
        } else {
            drawerLayout.openDrawer(DrawerMenu);
        }
    }

    private void initUi() {
        onStartlayout = findViewById(R.id.onStartlayout);
        gotItButton = findViewById(R.id.gotItButton);
        gotItButton.setOnClickListener(this);
        gotItlayout = findViewById(R.id.gotItlayout);
        gotItlayout.setOnClickListener(this);
        connectingStatus = findViewById(R.id.connectingStatus);
        agentGoalLayout = findViewById(R.id.agentGoalLayout);
        ivSyncData = findViewById(R.id.ivSyncData);
        swipe_to_start = findViewById(R.id.swipe_to_start);
        swipe_to_start.setOnClickListener(this);
        manual_swipe_to_start = findViewById(R.id.manual_swipe_to_start);
        manual_swipe_to_start.setOnClickListener(this);
        voiceTestContent = findViewById(R.id.voiceTestContent);
        youCanEarnMore = findViewById(R.id.youCanEarnMore);
        uearnLoader = findViewById(R.id.uearnLoader);
        connectedLoader = findViewById(R.id.connectedLoader);
        testNowLoader = findViewById(R.id.testNowLoader);
        setGoal = findViewById(R.id.setGoal);
        setGoal.setOnClickListener(this);
        numberOfDaysLeft = findViewById(R.id.numberOfDaysLeft);
        youEarnTv = findViewById(R.id.youEarnTv);
        goalsCompletedLayout = findViewById(R.id.goalsCompletedLayout);
        goalsCompletedLayout.setVisibility(View.GONE);
        goalsNotCompletedLayout = findViewById(R.id.goalsNotCompletedLayout);
        goalsNotCompletedLayout.setVisibility(View.GONE);
        goalEarnedTv = findViewById(R.id.goalEarnedTv);
        goalEarnedTv.addTextChangedListener(onTextChangedListener());
        newGoalSet = findViewById(R.id.newGoalSet);
        newGoalSet.setOnClickListener(this);
        underLineView = findViewById(R.id.underLine);
        setGoalLayout = findViewById(R.id.setGoalLayout);
        setGoalLayout.setVisibility(View.GONE);
        setGoalLayout.setOnClickListener(this);
        goalLayout = findViewById(R.id.goalLayout);
        goalLayout.setVisibility(View.VISIBLE);
        goalEarnedTv.setPaintFlags(1);
        goalEarnedTv.setOnClickListener(this);
        hintsTv = findViewById(R.id.hintsTv);
        hintsTv.setOnClickListener(this);
        setGoalsLayout = findViewById(R.id.setGoalsLayout);
        setGoalsLayout.setVisibility(View.GONE);
        currentMonth = findViewById(R.id.currentMonth);
        earnTv = findViewById(R.id.earnTv);
        seekbar = ((CustomSeekBar) findViewById(R.id.seekBar));

        goalLayoutImageView = findViewById(R.id.goalLayoutImageView);
        setGoalsLayoutImageView = findViewById(R.id.setGoalsLayoutImageView);
        nonUearnLayoutImageView = findViewById(R.id.nonUearnLayoutImageView);

        earned_remaing_tv = findViewById(R.id.earned_remaing_tv);
        internet_status = findViewById(R.id.internet_status);
        image_sync = findViewById(R.id.image_sync);
        tv_message_count = findViewById(R.id.tv_message_count);
        image_notification = findViewById(R.id.image_notification);
        image_chat = findViewById(R.id.image_chat);
        today_uearn_layout = (LinearLayout) findViewById(R.id.today_uearn_layout);
        leadsconnectedAndfollowupLayout = (LinearLayout) findViewById(R.id.leadsconnectedAndfollowupLayout);
        activeTimeLayout = (LinearLayout) findViewById(R.id.activeTimeLayout);
        internetStatusLayout = (LinearLayout) findViewById(R.id.internetStatusLayout);
        additionalLoginInfoLayout = (LinearLayout) findViewById(R.id.additionalLoginInfoLayout);
        ll_today_goal = (LinearLayout) findViewById(R.id.ll_today_goal);
        internet_status_goal_before = findViewById(R.id.internet_status_goal_before);
        ll_update_goalLayout = findViewById(R.id.ll_update_goalLayout);
        ll_update_goalLayout.setOnClickListener(this);
        ll_bottom_layout = findViewById(R.id.ll_bottom_layout);

        DrawerMenu = findViewById(R.id.leftDrawerMenu);
        drawerLayout = findViewById(R.id.drawer_layout);
        nevSettings = findViewById(R.id.nevSettings);
        navAbout = findViewById(R.id.navAbout);
        navFAQ = findViewById(R.id.navFAQ);
        nevRefEarn = findViewById(R.id.nevRefEarn);
        navAppVersion = findViewById(R.id.navAppVersion);
        navLogOut = findViewById(R.id.navLogOut);
        nevFeedBack = findViewById(R.id.nevFeedBack);

        navClose = findViewById(R.id.imgNavClose);
        navLogOut.setOnClickListener(this);
        nevSettings.setOnClickListener(this);
        navAbout.setOnClickListener(this);
        navFAQ.setOnClickListener(this);
        nevRefEarn.setOnClickListener(this);
        navAppVersion.setOnClickListener(this);
        nevFeedBack.setOnClickListener(this);
        lyBtnWorkSlots = findViewById(R.id.lyBtnWorkSlots);
        lyBtnInvite = findViewById(R.id.lyBtnInvite);
        navWorkSlots = findViewById(R.id.navWorkSlots);
        navWorkSlots.setOnClickListener(this);
        navClose.setOnClickListener(this);

        ll_non_uearn = findViewById(R.id.ll_non_uearn);
        tv_user_name = findViewById(R.id.tv_user_name);

        Calendar c = Calendar.getInstance();
        String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};
        String month = monthName[c.get(Calendar.MONTH)];
        int year = c.get(Calendar.YEAR);
        int date = c.get(Calendar.DATE);
        currentMonth.setText(month + " " + year);
        earnTv = findViewById(R.id.earnTv);
        seekbar = ((CustomSeekBar) findViewById(R.id.seekBar));
        seekbar.setMinimumHeight(30);
        seekbar.setMax(8);
        seekbar.setProgress(8);
        todayUearnContent = findViewById(R.id.todayUearnContent);
        today_uearn = findViewById(R.id.today_uearn);
        today_uearn.setOnClickListener(this);

        setUearnTodayLayoutVisibility("");

        profilePic = findViewById(R.id.profile_image);
        profilePic.setOnClickListener(this);
        webViewText = findViewById(R.id.webViewText);
        webViewText.setVerticalScrollBarEnabled(false);
        webViewText.setHorizontalScrollBarEnabled(false);

        if(SmarterSMBApplication.isRemoteDialledStart) {
            webViewText.setVisibility(View.VISIBLE);
        } else {
            webViewText.setVisibility(View.GONE);
        }

        totalFirstCall = findViewById(R.id.firstcall);
        firstCallFollowup = findViewById(R.id.firstCallFollowup);
        firstCallFollowup.setOnClickListener(this);
        doneFollowupsBtn = findViewById(R.id.doneFollowupsBtn);
        doneFollowupsBtn.setOnClickListener(this);
        followupToDoBtn = findViewById(R.id.followupToDoBtn);
        followupsMissedBtn = findViewById(R.id.followupsMissedBtn);
        followupsMissedBtn.setOnClickListener(this);

        ll_follow_ups = findViewById(R.id.ll_follow_ups);
        ll_follow_ups.setOnClickListener(this);

        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
            if (fkControl) {
                followupsMissedBtn.setText("Logged in");
            } else {
                followupsMissedBtn.setText("Follow-ups to do");
            }
        } else {
            followupsMissedBtn.setText("Follow-ups to do");
        }

        //uearnedBonusTextView = findViewById(R.id.uearnedBonusTextView);
        uearnedTextView = findViewById(R.id.uearnedTextView);
        completedFollowups = findViewById(R.id.doneFollowups);
        completedFollowupsLayout = findViewById(R.id.completedFollowupsLayout);
        completedFollowupsLayout.setOnClickListener(this);
        followupsToDo = findViewById(R.id.followupsToDo);
        followupsMissed = findViewById(R.id.followupsMissed);
        followupsMissed.setOnClickListener(this);
        start_layout = findViewById(R.id.start_layout);
        fragmentManager = getSupportFragmentManager();
        //acp_bottom_layout = findViewById(R.id.acp_bottom_layout);
        manual_dial_layout = findViewById(R.id.manual_dial_layout);

        if (ApplicationSettings.containsPref(AppConstants.UNSCHEDULED_CALL)) {
            boolean schedulecall = ApplicationSettings.getPref(AppConstants.UNSCHEDULED_CALL, false);
            if (schedulecall) {
                manual_dial_layout.setVisibility(View.VISIBLE);
                swipe_to_start.setVisibility(View.GONE);
                //acp_bottom_layout.setVisibility(View.GONE);
            } else {
                manual_dial_layout.setVisibility(View.GONE);
                swipe_to_start.setVisibility(View.VISIBLE);
                //acp_bottom_layout.setVisibility(View.VISIBLE);
            }
        } else {
            manual_dial_layout.setVisibility(View.GONE);
            swipe_to_start.setVisibility(View.VISIBLE);
            //acp_bottom_layout.setVisibility(View.VISIBLE);
        }

        loginHours = findViewById(R.id.loginHours);
        breakTime = findViewById(R.id.breakTime);
        totalCalls = findViewById(R.id.totalCalls);
        talkTime = findViewById(R.id.talkTime);

        connectionStatusLayout = findViewById(R.id.connectionStatusLayout);
        connectionStatus = findViewById(R.id.connectionStatus);
        testNow = findViewById(R.id.testNow);
        testNow.setOnClickListener(this);
        image_chat.setOnClickListener(this);
        image_notification.setOnClickListener(this);
        image_sync.setOnClickListener(this);
        ll_today_goal.setOnClickListener(this);
        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }
        Typeface semi_bold = Typeface.createFromAsset(this.getAssets(), "fonts/SF-UI-Display-Semibold.ttf");
        Typeface face = Typeface.createFromAsset(this.getAssets(),
                "fonts/sf-pro-display-regular.ttf");
        userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");

        manualDial = findViewById(R.id.manualDial);
        manualDial.setOnClickListener(this);

        defaultMsgWebViewLayout = findViewById(R.id.defaultMsgWebViewLayout);
        defaultMsgWebView = findViewById(R.id.defaultMsgWebView);
        defaultMsgWebView.getSettings().setJavaScriptEnabled(true);
        defaultMsgWebView.getSettings().setBlockNetworkLoads (false);
        if (Build.VERSION.SDK_INT >= 19) {
            defaultMsgWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }


        defaultMsgWebView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                WebView.HitTestResult hr = ((WebView)v).getHitTestResult();
                if(hr!=null){
                    if(hr.getType()== WebView.HitTestResult.EDIT_TEXT_TYPE){
                        clearFlags();
                        SmarterSMBApplication.outgoingCallNotInStartMode = true;
                        SmarterSMBApplication.webViewOutgoingCallEventTriggered = true;
                    }
                }
                return false;
            }
        });

        adhocCallCardView = findViewById(R.id.adhocCall_card_view);
        adhocCallViewLayout = findViewById(R.id.adhocCallViewLayout);
        adhocCallEditText = findViewById(R.id.adhocCallEditText);
        adhocCallEditText.setSelection(adhocCallEditText.getText().length());
        adhocCallEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                adhocCallEditText.setText("");
                return false;
            }
        });
        adhocCallImageButton = findViewById(R.id.adhocCallImageButton);
        adhocCallImageButton.setOnClickListener(this);

        if (ApplicationSettings.containsPref(AppConstants.WEB_LAYOUT)) {
            boolean webLayout = ApplicationSettings.getPref(AppConstants.WEB_LAYOUT, false);
            if (webLayout) {
                if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        defaultMsgWebViewLayout.setVisibility(View.VISIBLE);
                    } else {
                        defaultMsgWebViewLayout.setVisibility(View.GONE);
                    }
                } else if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                    boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                    if (adhocCall) {
                        defaultMsgWebViewLayout.setVisibility(View.VISIBLE);
                    } else {
                        defaultMsgWebViewLayout.setVisibility(View.GONE);
                    }
                } else {
                    defaultMsgWebViewLayout.setVisibility(View.GONE);
                }
            } else {
                if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        if (ApplicationSettings.containsPref(AppConstants.ALLOW_ADHOC_CALL)) {
                            boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ALLOW_ADHOC_CALL, false);
                            if (allowAdhocCall) {
                                adhocCallCardView.setVisibility(View.VISIBLE);
                                adhocCallViewLayout.setVisibility(View.VISIBLE);
                            } else {
                                adhocCallCardView.setVisibility(View.GONE);
                                adhocCallViewLayout.setVisibility(View.GONE);
                            }
                        } else {
                            adhocCallCardView.setVisibility(View.GONE);
                            adhocCallViewLayout.setVisibility(View.GONE);
                        }
                    } else {
                        adhocCallCardView.setVisibility(View.GONE);
                        adhocCallViewLayout.setVisibility(View.GONE);
                    }
                } else if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                    boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                    if (adhocCall) {
                        adhocCallCardView.setVisibility(View.VISIBLE);
                        adhocCallViewLayout.setVisibility(View.VISIBLE);
                    } else {
                        adhocCallCardView.setVisibility(View.GONE);
                        adhocCallViewLayout.setVisibility(View.GONE);
                    }
                } else {
                    adhocCallCardView.setVisibility(View.GONE);
                    adhocCallViewLayout.setVisibility(View.GONE);
                }
            }
        } else {
            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if (ApplicationSettings.containsPref(AppConstants.ALLOW_ADHOC_CALL)) {
                        boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ALLOW_ADHOC_CALL, false);
                        if (allowAdhocCall) {
                            adhocCallCardView.setVisibility(View.VISIBLE);
                            adhocCallViewLayout.setVisibility(View.VISIBLE);
                        } else {
                            adhocCallCardView.setVisibility(View.GONE);
                            adhocCallViewLayout.setVisibility(View.GONE);
                        }
                    } else {
                        adhocCallCardView.setVisibility(View.GONE);
                        adhocCallViewLayout.setVisibility(View.GONE);
                    }
                } else {
                    adhocCallCardView.setVisibility(View.GONE);
                    adhocCallViewLayout.setVisibility(View.GONE);
                }
            } else if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                if (adhocCall) {
                    adhocCallCardView.setVisibility(View.VISIBLE);
                    adhocCallViewLayout.setVisibility(View.VISIBLE);
                } else {
                    adhocCallCardView.setVisibility(View.GONE);
                    adhocCallViewLayout.setVisibility(View.GONE);
                }
            } else {
                adhocCallCardView.setVisibility(View.GONE);
                adhocCallViewLayout.setVisibility(View.GONE);
            }
        }
        getApplicationVersion();

        try {
            if (CommonUtils.allowASF()) {
                lyBtnWorkSlots.setVisibility(View.VISIBLE);
            } else {
                lyBtnWorkSlots.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        boolean isTrue = false;
        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("ON BOARD") || userStatus.equalsIgnoreCase("ON BOARDED"))) {
            isTrue = true;
        } else if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("OJT"))) {
            isTrue = true;
        } else if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("PRODUCTION"))) {
            isTrue = true;
        } else if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("PROJECT") || userStatus.equalsIgnoreCase("PROJECT ASSIGNED"))) {
            isTrue = true;
        } else {
            isTrue = false;
        }
        if (isTrue) {
            lyBtnInvite.setVisibility(View.VISIBLE);
        } else {
            lyBtnInvite.setVisibility(View.GONE);
        }
    }

    private void initDataToSeekbar(int currentPercentage, int remainingPercentage) {
        progressItemList = new ArrayList<ProgressItem>();
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = currentPercentage;
        mProgressItem.color = R.color.seekbar_yellow;
        progressItemList.add(mProgressItem);
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = remainingPercentage;
        mProgressItem.color = R.color.seekbar_grey;
        progressItemList.add(mProgressItem);
        seekbar.initData(progressItemList);
        seekbar.invalidate();
    }

    void showDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(R.layout.alert_dialog, null,false);

        Button cancelBtn = contentView.findViewById(R.id.cancelBtn);
        Typeface titlef = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
        TextView hints1Tv = contentView.findViewById(R.id.hints1Tv);
        hints1Tv.setTypeface(titlef);
        TextView hints2Tv = contentView.findViewById(R.id.hints2Tv);

        Typeface titleFace = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
        cancelBtn.setTypeface(titleFace);

        alertDialog = new AlertDialog.Builder(this).setView(contentView).create();
        alertDialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    void showSlotsDialog(String message) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(R.layout.alert_lucky_dialog, null,false);

        Document doc = Jsoup.parse(message);
        Element p= doc.select("H2").first();
        String text = doc.body().text();

        Button cancelBtn = contentView.findViewById(R.id.cancelBtn);
        Button yesBtn = contentView.findViewById(R.id.yesBtn);
        Typeface titlef = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
        TextView hints1Tv = contentView.findViewById(R.id.hints1Tv);
        hints1Tv.setTypeface(titlef);
        TextView hints2Tv = contentView.findViewById(R.id.hints2Tv);
        hints2Tv.setTypeface(titlef);
        hints2Tv.setText(text);
        Typeface titleFace = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
        cancelBtn.setTypeface(titleFace);
        yesBtn.setTypeface(titleFace);

        alertDialog = new AlertDialog.Builder(this).setView(contentView).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(UearnHome.this, UearnHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(UearnHome.this, AgentSlotsActivity.class);
                intent.putExtra("SLOTSREDIRECT", "UearnHome");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    private void clearFlags() {
        NotificationData.customerFeedback = "";
        NotificationData.updatedCustomKVS = "";
        ServiceHandler.callDisconnected = false;
        SmarterSMBApplication.incomingCallAcceptedByAgent = false;
        SmarterSMBApplication.incomingCallRejectedByAgent = false;
        SmarterSMBApplication.endTheSession = false;
        SmarterSMBApplication.callEndedFromDuringCall = false;
        SmarterSMBApplication.actionMoveToNormalProcessed = false;
        NotificationData.dialledCustomerNumber = "";
        NotificationData.dialledCustomerName = "";
        SmarterSMBApplication.isCampaignOver = false;
        SmarterSMBApplication.navigatingToUearnHome = false;
        SmarterSMBApplication.isDiallingFollowUpC2C = false;
        SmarterSMBApplication.manualDialScenario = false;
        SmarterSMBApplication.followupsInPredictive = false;
        SmarterSMBApplication.callStateIsDisconnected = false;
        SmarterSMBApplication.oneCallIsActive = false;
        SmarterSMBApplication.enableECBAndESB = false;
        SmarterSMBApplication.matchingInNumberNotInStartMode = false;
        SmarterSMBApplication.outgoingCallNotInStartMode = false;
        SmarterSMBApplication.isInNumberMatching = false;
        SmarterSMBApplication.currentStateIsStartMode = false;
        SmarterSMBApplication.autoCallAnswered = false;
        SmarterSMBApplication.agentIsInConnectingState = false;
        SmarterSMBApplication.agentIsInConnectedState = false;
        NotificationData.isSocketResponse = false;
        SmarterSMBApplication.currentModeIsConnected = false;
        SmarterSMBApplication.remoteEnabledRedialScenario = false;
        SmarterSMBApplication.webViewOutgoingCallEventTriggered = false;
        SmarterSMBApplication.moveToRAD = false;
        SmarterSMBApplication.moveToNormal = false;
        NotificationData.outboundDialledCustomerNumber = "";
        NotificationData.outboundDialledCustomerName = "";
        NotificationData.outboundDialledTransactionId = "";
        SmarterSMBApplication.takingBreakInPD = false;
        SmarterSMBApplication.lastConnectedCustomer = "";
        SmarterSMBApplication.lastConnectedCustomerFeedback ="";
        SmarterSMBApplication.isCurrentQuesMandatory = false;
    }

    private void deactivateCW() {
        //Log.d("FKDemoTest","Inside deactivateCW()");
        String ussdCode = "#43#";
        if (ApplicationSettings.containsPref(AppConstants.DEACTIVATE_USSD_CODE)) {
            String deactivateCode = ApplicationSettings.getPref(AppConstants.DEACTIVATE_USSD_CODE, "");
            if (deactivateCode != null && !deactivateCode.isEmpty()) {
                ussdCode = deactivateCode;
            }
        }
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + Uri.encode(ussdCode))));
    }

    private void activateCW() {
        //Log.d("FKDemoTest","Inside activateCW()");
        String ussdCode = "*43#";
        if (ApplicationSettings.containsPref(AppConstants.ACTIVATE_USSD_CODE)) {
            String activateCode = ApplicationSettings.getPref(AppConstants.ACTIVATE_USSD_CODE, "");
            if (activateCode != null && !activateCode.isEmpty()) {
                ussdCode = activateCode;
            }
        }
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + Uri.encode(ussdCode))));
    }

    private void start() {
        if(SmarterSMBApplication.networkCheckInProgress){
            Toast.makeText(this, "Please wait. Checking connection status...", Toast.LENGTH_SHORT).show();
        } else {
            clearFlags();

            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    String questionnaire = ApplicationSettings.getPref(AppConstants.IN_QUESTIONNAIRE_FROM_SETTINGS, "");
                    ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                }
            }

            if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                if (adhocCall) {
                    String questionnaire = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE_FROM_SETTINGS, "");
                    ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                }
            }

            if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
                boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
                if (systemControl) {
                    SmarterSMBApplication.disableStatusBarAndNavigation = true;
                    hideSystemUI();
                }
            }

            if (userStatus != null && !(userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
                boolean isPermissionEnabled = CommonUtils.permissionsCheck(UearnHome.this);
                if (isPermissionEnabled) {
                    Intent intent2 = new Intent(this, UearnVoiceTestActivity.class);
                    startActivity(intent2);
                }
            } else {
                buttonText = swipe_to_start.getText().toString();
                if (buttonText != null && !buttonText.isEmpty() && buttonText.equals("STOP")){
                    SmarterSMBApplication.stopButtonClicked = true;
                }
                if (ApplicationSettings.containsPref(AppConstants.UNSCHEDULED_CALL)) {
                    boolean schedulecall = ApplicationSettings.getPref(AppConstants.UNSCHEDULED_CALL, false);
                    if (schedulecall) {
                        buttonText = manual_swipe_to_start.getText().toString();
                    }
                }
                SmarterSMBApplication.currentAppState = "Started";
                if ((SmarterSMBApplication.SmartUser != null) && !SmarterSMBApplication.SmartUser.getEmulationOn()) {
                    if (buttonText.equals("START CALL") || buttonText.equals("START CALL NOW / BOOK SLOT")) {
                        if (ApplicationSettings.containsPref(AppConstants.DISALLOW_HEADPHONES)) {
                            boolean disallowHeadphones = ApplicationSettings.getPref(AppConstants.DISALLOW_HEADPHONES, false);
                            if (disallowHeadphones) {

                            } else {
                                if (!SmarterSMBApplication.isHeadPhone) {
                                    if (!isBluetoothHeadsetConnected()) {
                                        showDialog();
                                        return;
                                    }
                                }
                            }
                        } else {
                            if (!SmarterSMBApplication.isHeadPhone) {
                                if (!isBluetoothHeadsetConnected()) {
                                    showDialog();
                                    return;
                                }
                            }
                        }
                    }
                }

                SmarterSMBApplication.currentStateIsStartMode = true;

                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        long loggedInStartTime = System.currentTimeMillis();
                        ApplicationSettings.putPref(AppConstants.LOGGED_IN_START_TIME, loggedInStartTime);
                        //Log.d("LoginTime", "UearnHome - loggedInStartTime" + loggedInStartTime);
                    }
                }

                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        boolean trueCallerFound = isAppInstalled(this, "com.truecaller");
                        if(trueCallerFound){
                            SmarterSMBApplication.disableStatusBarAndNavigation = false;
                            Toast.makeText(this, "Please uninstall truecaller app before you proceed", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            if (ApplicationSettings.containsPref(AppConstants.CALL_WAITING)) {
                                boolean callWaiting = ApplicationSettings.getPref(AppConstants.CALL_WAITING, false);
                                if (callWaiting) {
                                    try {
                                        if(swipe_to_start.getText().toString().equalsIgnoreCase("STOP") || manual_swipe_to_start.getText().toString().equalsIgnoreCase("STOP")){

                                        }else{
                                            deactivateCW();
                                        }
                                    } catch (Exception e) {
                                        Log.e("UearnHome", "Error calling deactivateCW()", e);
                                        int version_code = CommonUtils.getVersionCode(this);
                                        String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                                                ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>UearnHome - Error calling deactivateCW(): " + e.getMessage();
                                        ServiceApplicationUsage.callErrorLog(message);
                                    }
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            initiateRequestOnStart(buttonText);
                                        }
                                    }, 100);
                                } else {
                                    initiateRequestOnStart(buttonText);
                                }
                            } else {
                                initiateRequestOnStart(buttonText);
                            }
                        }
                    } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                        boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                        if (ibControl) {
                            if (ApplicationSettings.containsPref(AppConstants.CALL_WAITING)) {
                                boolean callWaiting = ApplicationSettings.getPref(AppConstants.CALL_WAITING, false);
                                if (callWaiting) {
                                    try {
                                        if(swipe_to_start.getText().toString().equalsIgnoreCase("STOP") || manual_swipe_to_start.getText().toString().equalsIgnoreCase("STOP")){

                                        }else{
                                            deactivateCW();
                                        }
                                    } catch (Exception e) {
                                        Log.e("UearnHome", "Error calling deactivateCW()", e);
                                        int version_code = CommonUtils.getVersionCode(this);
                                        String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                                                ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>UearnHome - Error calling deactivateCW(): " + e.getMessage();
                                        ServiceApplicationUsage.callErrorLog(message);
                                    }
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            initiateRequestOnStart(buttonText);
                                        }
                                    }, 100);
                                } else {
                                    initiateRequestOnStart(buttonText);
                                }
                            } else {
                                initiateRequestOnStart(buttonText);
                            }
                        } else {
                            initiateRequestOnStart(buttonText);
                        }
                    } else {
                        initiateRequestOnStart(buttonText);
                    }
                } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        if (ApplicationSettings.containsPref(AppConstants.CALL_WAITING)) {
                            boolean callWaiting = ApplicationSettings.getPref(AppConstants.CALL_WAITING, false);
                            if (callWaiting) {
                                try {
                                    if(swipe_to_start.getText().toString().equalsIgnoreCase("STOP") || manual_swipe_to_start.getText().toString().equalsIgnoreCase("STOP")){

                                    }else{
                                        deactivateCW();
                                    }
                                } catch (Exception e) {
                                    Log.e("UearnHome", "Error calling deactivateCW()", e);
                                    int version_code = CommonUtils.getVersionCode(this);
                                    String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                                            ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>UearnHome - Error calling deactivateCW(): " + e.getMessage();
                                    ServiceApplicationUsage.callErrorLog(message);
                                }
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        initiateRequestOnStart(buttonText);
                                    }
                                }, 100);
                            } else {
                                initiateRequestOnStart(buttonText);
                            }
                        } else {
                            initiateRequestOnStart(buttonText);
                        }
                    } else {
                        initiateRequestOnStart(buttonText);
                    }
                } else {
                    initiateRequestOnStart(buttonText);
                }
            }
        }
    }

    private void initiateRequestOnStart(String buttonText) {
        if (webViewText != null) {
            webViewText.setVisibility(View.VISIBLE);
        }
        UearnActivity.onBackPressed = false;
        manualDialing = false;
        dialingFromList = false;
        autoDial = true;
        ApplicationSettings.putPref(AppConstants.MMT_STATUS, "");
        ApplicationSettings.putPref("SUBJECT", "");
        UearnActivity.redialScenario = false;
        if (buttonText.equalsIgnoreCase("START CALL") || buttonText.equalsIgnoreCase("START CALL NOW / BOOK SLOT")) {
            setStartButtonVisibility(false);
            onStartlayout.setVisibility(View.VISIBLE);
        }
        if (remoteAutoEnabled.equals("on")) {
            youCanEarnMore.setVisibility(View.GONE);
            setUearnTodayLayoutVisibility("invisible");
            start_layout.setVisibility(View.VISIBLE);
            if (SmarterSMBApplication.isRemoteDialledStart) {
                onStartlayout.setVisibility(View.GONE);
                setStartButtonVisibility(true);
                youCanEarnMore.setVisibility(View.VISIBLE);
                setUearnTodayLayoutVisibility("visible");
                start_layout.setVisibility(View.GONE);
                setVisibilityMenuItems("true");
                sendRemoteDialStopRequest("Taking a break");
            } else {
                click();
                if (noMissedFollowups) {
                    noMissedFollowups = false;
                    isStart = false;
                } else {
                    isStart = true;
                }
                autoDial = false;
            }
        }else if (remoteAutoEnabled.equals("onsolicit")) {
            youCanEarnMore.setVisibility(View.GONE);
            setUearnTodayLayoutVisibility("invisible");
            start_layout.setVisibility(View.VISIBLE);
            if (SmarterSMBApplication.isRemoteDialledStart) {
                onStartlayout.setVisibility(View.GONE);
                setStartButtonVisibility(true);
                youCanEarnMore.setVisibility(View.VISIBLE);
                setUearnTodayLayoutVisibility("visible");
                start_layout.setVisibility(View.GONE);
                setVisibilityMenuItems("true");
                sendRemoteDialStopRequest("Taking a break");
            } else {
                click();
                if (noMissedFollowups) {
                    noMissedFollowups = false;
                    isStart = false;
                } else {
                    isStart = true;
                }
                autoDial = false;
            }
        } else {
            start_layout.setVisibility(View.GONE);
            if (isStart) {
                isStart = false;
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                    youCanEarnMore.setVisibility(View.GONE);
                } else {
                    removeFragment();
                }
            } else {
                click();
                if (noMissedFollowups) {
                    noMissedFollowups = false;
                    isStart = false;
                } else {
                    isStart = true;
                }
                autoDial = false;
            }

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.swipe_to_start || id == R.id.manual_swipe_to_start) {
            if (CommonUtils.isNetworkAvailable(this)) {
                if (remoteAutoEnabled == null || remoteAutoEnabled.isEmpty()) {
                    Toast.makeText(this, "Required settings are not enabled. Please re-sync", Toast.LENGTH_SHORT).show();
                } else {
                    start();
                }
            } else {
                sendRemoteDialStartRequest = false;
                uearnLoader.setVisibility(View.GONE);
                connectedLoader.setVisibility(View.GONE);
                completedFollowups.setVisibility(View.VISIBLE);
                connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
                testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
                connectionStatusLayout.setVisibility(View.VISIBLE);
                connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
                internet_status.setText("NO INTERNET");
                internet_status_goal_before.setText("NO INTERNET");
                internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                noNetworkAnimation(internet_status, internet_status_goal_before);
                disableStartButton();
                connectionStatusLayout.setVisibility(View.GONE);
            }
        } else if (id == R.id.gotItButton) {
            sendRemoteDialStopRequest("Taking a break");
            gotItButtonAction();
        } else if (id == R.id.profile_image) {
            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                if (fkControl) {

                }
            } else {
                unregisterAllReceivers();
            }
            if (pingTimer != null) {
                pingTimer.cancel();
                pingTimer = null;
            }
            timerStopped = false;
            pingInProgress = false;

//                if(gotItlayout.getVisibility() == View.VISIBLE){
//                    gotItButtonAction();
//                }

            testNowLoader.setVisibility(View.GONE);
            testNow.setVisibility(View.VISIBLE);
            SmarterSMBApplication.moveToRAD = false;
            //Intent intent3 = new Intent(this, UearnProfileActivity.class);
            SmarterSMBApplication.moveToNormal = false;
            //startActivity(intent3);
            toggleLeftDrawer();
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            //this.finish();
        } else if (id == R.id.ll_today_goal) {
            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                        boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                        if (homeNavigation) {
                            SmarterSMBApplication.moveToRAD = false;
                            Intent ueranIntent = new Intent(this, UearnPassbookActivity.class);
                            SmarterSMBApplication.moveToNormal = false;
                            startActivity(ueranIntent);
                        }
                    }
                }
            } else if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                            boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                            if (homeNavigation) {
                                SmarterSMBApplication.moveToRAD = false;
                                Intent ueranIntent = new Intent(this, UearnPassbookActivity.class);
                                SmarterSMBApplication.moveToNormal = false;
                                startActivity(ueranIntent);
                            }
                        }
                    }
                }
            } else {
                unregisterConnectivityReceiver();
                SmarterSMBApplication.moveToRAD = false;
                Intent ueranIntent = new Intent(this, UearnPassbookActivity.class);
                SmarterSMBApplication.moveToNormal = false;
                startActivity(ueranIntent);
            }
        } else if (id == R.id.firstCallFollowup) {
            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                        boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                        if (homeNavigation) {
                            if (firstCallfollowups > 0) {
                                ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                                SmarterSMBApplication.moveToRAD = false;
                                Intent ueranFollowup = new Intent(this, UearnFollowupActivity.class);
                                ueranFollowup.putExtra("FollowUpType", "FirstCall");
                                isStart = false;
                                removeFragment();
                                startActivity(ueranFollowup);
                                this.finish();
                            } else {
                                Toast.makeText(this, "You don't have any data to dial!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            } else if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                            boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                            if (homeNavigation) {
                                if (firstCallfollowups > 0) {
                                    ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                                    SmarterSMBApplication.moveToRAD = false;
                                    Intent ueranFollowup = new Intent(this, UearnFollowupActivity.class);
                                    ueranFollowup.putExtra("FollowUpType", "FirstCall");
                                    isStart = false;
                                    removeFragment();
                                    startActivity(ueranFollowup);
                                    this.finish();
                                } else {
                                    Toast.makeText(this, "You don't have any data to dial!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            } else {
                if (firstCallfollowups > 0) {
                    ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                    SmarterSMBApplication.moveToRAD = false;
                    Intent ueranFollowup = new Intent(this, UearnFollowupActivity.class);
                    ueranFollowup.putExtra("FollowUpType", "FirstCall");
                    isStart = false;
                    removeFragment();
                    startActivity(ueranFollowup);
                    this.finish();
                } else {
                    Toast.makeText(this, "You don't have any data to dial!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == R.id.followupsMissed) {
            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                        boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                        if (homeNavigation) {
                            ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                            SmarterSMBApplication.moveToRAD = false;
                            Intent followupMissed = new Intent(this, UearnFollowupActivity.class);
                            followupMissed.putExtra("FollowUpType", "FollowUpMissed");
                            isStart = false;
                            removeFragment();
                            startActivity(followupMissed);
                            this.finish();
                        }
                    }
                }
            } else if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                            boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                            if (homeNavigation) {
                                ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                                SmarterSMBApplication.moveToRAD = false;
                                Intent followupMissed = new Intent(this, UearnFollowupActivity.class);
                                followupMissed.putExtra("FollowUpType", "FollowUpMissed");
                                isStart = false;
                                removeFragment();
                                startActivity(followupMissed);
                                this.finish();
                            }
                        }
                    }
                }
            } else {
                ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                SmarterSMBApplication.moveToRAD = false;
                Intent followupMissed = new Intent(this, UearnFollowupActivity.class);
                followupMissed.putExtra("FollowUpType", "FollowUpMissed");
                isStart = false;
                removeFragment();
                startActivity(followupMissed);
                this.finish();
            }
        } else if (id == R.id.followupsMissedBtn) {
            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                        boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                        if (homeNavigation) {
                            ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                            SmarterSMBApplication.moveToRAD = false;
                            Intent followupsMissed = new Intent(this, UearnFollowupActivity.class);
                            followupsMissed.putExtra("FollowUpType", "FollowUpMissed");
                            isStart = false;
                            removeFragment();
                            startActivity(followupsMissed);
                            this.finish();
                        }
                    }
                }
            } else if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                            boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                            if (homeNavigation) {
                                ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                                SmarterSMBApplication.moveToRAD = false;
                                Intent followupsMissed = new Intent(this, UearnFollowupActivity.class);
                                followupsMissed.putExtra("FollowUpType", "FollowUpMissed");
                                isStart = false;
                                removeFragment();
                                startActivity(followupsMissed);
                                this.finish();
                            }
                        }
                    }
                }
            } else {
                ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                SmarterSMBApplication.moveToRAD = false;
                Intent followupsMissed = new Intent(this, UearnFollowupActivity.class);
                followupsMissed.putExtra("FollowUpType", "FollowUpMissed");
                isStart = false;
                removeFragment();
                startActivity(followupsMissed);
                this.finish();
            }
        } else if (id == R.id.ll_follow_ups) {
            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                        boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                        if (homeNavigation) {
                            ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                            SmarterSMBApplication.moveToRAD = false;
                            Intent followupsMissed = new Intent(this, UearnFollowupActivity.class);
                            followupsMissed.putExtra("FollowUpType", "FollowUpMissed");
                            isStart = false;
                            removeFragment();
                            startActivity(followupsMissed);
                            this.finish();
                        }
                    }
                }
            } else if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                            boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                            if (homeNavigation) {
                                ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                                SmarterSMBApplication.moveToRAD = false;
                                Intent followupsMissed = new Intent(this, UearnFollowupActivity.class);
                                followupsMissed.putExtra("FollowUpType", "FollowUpMissed");
                                isStart = false;
                                removeFragment();
                                startActivity(followupsMissed);
                                this.finish();
                            }
                        }
                    }
                }
            } else {
                ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");
                SmarterSMBApplication.moveToRAD = false;
                Intent followupsMissed = new Intent(this, UearnFollowupActivity.class);
                followupsMissed.putExtra("FollowUpType", "FollowUpMissed");
                isStart = false;
                removeFragment();
                startActivity(followupsMissed);
                this.finish();
            }
        } else if (id == R.id.completedFollowupsLayout) {
            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                        boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                        if (homeNavigation) {
                            removeFragment();
                            SmarterSMBApplication.moveToRAD = false;
                            Intent intent = new Intent(this, DashboardAgent.class);
                            DashboardAgent.contactedInfoList = contactedInfoList;
                            this.startActivity(intent);
                        }
                    }
                }
            } else if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                            boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                            if (homeNavigation) {
                                removeFragment();
                                SmarterSMBApplication.moveToRAD = false;
                                Intent intent = new Intent(this, DashboardAgent.class);
                                DashboardAgent.contactedInfoList = contactedInfoList;
                                this.startActivity(intent);
                            }
                        }
                    }
                }
            } else {
                removeFragment();
                unregisterConnectivityReceiver();
                SmarterSMBApplication.moveToRAD = false;
                Intent intent = new Intent(this, DashboardAgent.class);
                DashboardAgent.contactedInfoList = contactedInfoList;
                this.startActivity(intent);
            }
        } else if (id == R.id.doneFollowupsBtn) {
            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                        boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                        if (homeNavigation) {
                            removeFragment();
                            SmarterSMBApplication.moveToRAD = false;
                            Intent intent1 = new Intent(this, DashboardAgent.class);
                            DashboardAgent.contactedInfoList = contactedInfoList;
                            this.startActivity(intent1);
                        }
                    }
                }
            } else if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        if (ApplicationSettings.containsPref(AppConstants.HOME_NAVIGATION)) {
                            boolean homeNavigation = ApplicationSettings.getPref(AppConstants.HOME_NAVIGATION, false);
                            if (homeNavigation) {
                                removeFragment();
                                SmarterSMBApplication.moveToRAD = false;
                                Intent intent1 = new Intent(this, DashboardAgent.class);
                                DashboardAgent.contactedInfoList = contactedInfoList;
                                this.startActivity(intent1);
                            }
                        }
                    }
                }
            } else {
                removeFragment();
                unregisterConnectivityReceiver();
                SmarterSMBApplication.moveToRAD = false;
                Intent intent1 = new Intent(this, DashboardAgent.class);
                DashboardAgent.contactedInfoList = contactedInfoList;
                this.startActivity(intent1);
            }
        } else if (id == R.id.hintsTv) {//goalEarnedTv.setBackgroundResource(R.drawable.textview_border);
            underLineView.setVisibility(View.GONE);
            //goalEarnedTv.setWidth(140);
            goalEarnedTv.setGravity(Gravity.CENTER);
            //goalEarnedTv.setHeight(52);
            goalEarnedTv.setPaintFlags(0);
            hintsTv.setVisibility(View.GONE);
            goalEarnedTv.setTextColor(Color.parseColor("#8f8a8a"));
            setGoalLayout.setVisibility(View.VISIBLE);
            goalEarnedTv.setFocusable(true);
            goalEarnedTv.setClickable(true);
            goalEarnedTv.setEnabled(true);
            goalEarnedTv.setFocusableInTouchMode(true);
            goalEarnedTv.requestFocus();
            goalEarnedTv.setText("10000");
            ll_update_goalLayout.setVisibility(View.VISIBLE);
            today_uearn_layout.setVisibility(View.GONE);
            ll_bottom_layout.setVisibility(View.GONE);
            if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                if (allowAdhocCall) {
                    adhocCallCardView.setVisibility(View.GONE);
                    adhocCallViewLayout.setVisibility(View.GONE);
                }
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(goalEarnedTv, InputMethodManager.SHOW_IMPLICIT);
        } else if (id == R.id.ll_update_goalLayout) {
            ll_update_goalLayout.setVisibility(View.VISIBLE);
            goalEarnedTv.setVisibility(View.VISIBLE);
            setGoalLayout.setVisibility(View.VISIBLE);
            setGoal.setVisibility(View.VISIBLE);
            goalEarnedTv.setPaintFlags(0);
            hintsTv.setVisibility(View.GONE);
            goalEarnedTv.setTextColor(Color.parseColor("#8f8a8a"));
            setGoalLayout.setVisibility(View.VISIBLE);
            goalEarnedTv.setFocusable(true);
            goalEarnedTv.setClickable(true);
            goalEarnedTv.setEnabled(true);
            goalEarnedTv.setFocusableInTouchMode(true);
            goalEarnedTv.requestFocus();
            //goalEarnedTv.setText("10000");
            today_uearn_layout.setVisibility(View.GONE);
            ll_bottom_layout.setVisibility(View.GONE);
        } else if (id == R.id.goalEarnedTv) {//goalEarnedTv.setBackgroundResource(R.drawable.textview_border);
            underLineView.setVisibility(View.GONE);
            //goalEarnedTv.setWidth(140);
            //goalEarnedTv.setGravity(Gravity.CENTER);
            //goalEarnedTv.setHeight(52);
            goalEarnedTv.setPaintFlags(0);
            hintsTv.setVisibility(View.GONE);
            goalEarnedTv.setTextColor(Color.parseColor("#8f8a8a"));
            setGoalLayout.setVisibility(View.VISIBLE);
            goalEarnedTv.setFocusable(true);
            goalEarnedTv.setClickable(true);
            goalEarnedTv.setEnabled(true);
            goalEarnedTv.setFocusableInTouchMode(true);
            goalEarnedTv.requestFocus();
            goalEarnedTv.setText("10000");
            ll_update_goalLayout.setVisibility(View.VISIBLE);
            goalEarnedTv.setVisibility(View.VISIBLE);
            setGoalLayout.setVisibility(View.VISIBLE);
            setGoal.setVisibility(View.VISIBLE);
            today_uearn_layout.setVisibility(View.GONE);
            ll_bottom_layout.setVisibility(View.GONE);
            InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm2.showSoftInput(goalEarnedTv, InputMethodManager.SHOW_IMPLICIT);
        } else if (id == R.id.setGoalLayout) {
            goalLayout.setVisibility(View.GONE);
            setGoalsLayout.setVisibility(View.VISIBLE);
            setGoalLayout.setVisibility(View.VISIBLE);
            setGoalLayout.setVisibility(View.VISIBLE);
            ll_update_goalLayout.setVisibility(View.VISIBLE);
            earnTv.setText(goalEarnedTv.getText());
            if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                if (allowAdhocCall) {
                    adhocCallCardView.setVisibility(View.VISIBLE);
                    adhocCallViewLayout.setVisibility(View.VISIBLE);
                }
            }
        } else if (id == R.id.setGoal) {
            String earnValue = goalEarnedTv.getText().toString();
            if (!earnValue.equals("0") && !earnValue.equals("")) {
                setGoalValue(earnValue);
                setGoalLayout.setVisibility(View.GONE);
                goalLayout.setVisibility(View.GONE);
                setGoalsLayout.setVisibility(View.VISIBLE);
                ll_update_goalLayout.setVisibility(View.GONE);
                today_uearn_layout.setVisibility(View.VISIBLE);
                ll_bottom_layout.setVisibility(View.VISIBLE);
                if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                    boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                    if (allowAdhocCall) {
                        adhocCallCardView.setVisibility(View.VISIBLE);
                        adhocCallViewLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                Toast.makeText(this, "Uearn goal value should be greater than zero", Toast.LENGTH_SHORT).show();
                bizPrize = "10,000";
                goalLayout.setVisibility(View.VISIBLE);
                setGoalLayout.setVisibility(View.GONE);
                setGoalsLayout.setVisibility(View.GONE);
                underLineView.setVisibility(View.GONE);
                //goalEarnedTv.setWidth(140);
                goalEarnedTv.setGravity(Gravity.CENTER);
                //goalEarnedTv.setHeight(52);
                goalEarnedTv.setPaintFlags(0);
                hintsTv.setVisibility(View.VISIBLE);
                goalEarnedTv.setFocusable(false);
                goalEarnedTv.setText(bizPrize);
                //goalEarnedTv.setTextColor(getApplication().getResources().getColor(R.color.white));
                goalEarnedTv.setBackgroundResource(0);
                underLineView.setVisibility(View.VISIBLE);
                setGoalLayout.setVisibility(View.VISIBLE);
                goalEarnedTv.setFocusable(true);
                //setGoalLayout.setVisibility(View.GONE);
                if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                    boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                    if (allowAdhocCall) {
                        adhocCallCardView.setVisibility(View.GONE);
                        adhocCallViewLayout.setVisibility(View.GONE);
                    }
                }
            }
        } else if (id == R.id.testNow) {
            SmarterSMBApplication.networkCheckInProgress = true;
            testNowLoader.setVisibility(View.VISIBLE);
            testNow.setVisibility(View.GONE);
            if ((SmarterSMBApplication.SmartUser != null) && !SmarterSMBApplication.SmartUser.getEmulationOn()) {
                if (connectingStatus != null) {
                    connectionStatus.setText(getResources().getString(R.string.connection_check_msg));
                    connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_check));
                    testNow.setBackground(getResources().getDrawable(R.drawable.connection_check));
                    connectionStatusLayout.setVisibility(View.VISIBLE);
                    connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_check));
                    connectionStatusLayout.setVisibility(View.GONE);
                }
                sendPingRequestToCheckNetworkSpeed();
            }
        } else if (id == R.id.newGoalSet) {
            goalsCompletedLayout.setVisibility(View.GONE);
            goalLayout.setVisibility(View.VISIBLE);
        } else if (id == R.id.image_chat) {//Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
            if (CommonUtils.isNetworkAvailable(this)) {
                if (CommonUtils.allowChatBot()) {
                    Log.e("UearnHome", "ChatDetails :: " + CommonUtils.chatbotURL());
                    Intent chatIntent = new Intent(this, ChatActivity.class);
                    startActivity(chatIntent);
                } else {
                    Toast.makeText(this, "Chat disabled, please contact your manager", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You have no internet connection", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.image_notification) {//if (CommonUtils.allowASF()) {
            Intent notificationIntent = new Intent(this, NotificationMessageActivity.class);
            startActivity(notificationIntent);
                /*} else {
                    Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                }*/
        } else if (id == R.id.image_sync) {
            SmarterSMBApplication.syncMenuItemClicked = true;
            boolean isMobiledata = false;
            if (gotItlayout.getVisibility() == View.VISIBLE) {
                Toast.makeText(this, "Click on GOT IT button first", Toast.LENGTH_SHORT).show();
            } else {
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                    if (internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                            || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                            || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                        disableStartButton();
                    } else {
                        enableStartButton();
                    }
                }
                if (webViewText != null) {
                    webViewText.setVisibility(View.GONE);
                }
                start_layout.setVisibility(View.VISIBLE);
                gotItlayout.setVisibility(View.GONE);
                setStartButtonVisibility(true);
                bizPrize = ApplicationSettings.getPref(AppConstants.BIZ_PRIZE_VALUE, "");
                if (CommonUtils.isNetworkAvailable(this)) {

                    String connectionStatusStr = connectionStatus.getText().toString();
                    if (connectionStatusStr != null && !connectionStatusStr.isEmpty() &&
                            connectionStatusStr.equalsIgnoreCase(getResources().getString(R.string.no_connection_msg))) {
                        disableStartButton();
                        internet_status.setText("NO INTERNET");
                        internet_status_goal_before.setText("NO INTERNET");
                        internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                        internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                        noNetworkAnimation(internet_status, internet_status_goal_before);
                        Toast.makeText(this, "You have no internet connection", Toast.LENGTH_SHORT).show();
                    } else {
                        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                            if (fkControl) {
                                Toast.makeText(this, "Syncing...", Toast.LENGTH_LONG).show();
                                settingsApi(SmarterSMBApplication.SmartUser.getId());
                            }
                        } else {
                            initCalender();
                            if (start_time != null && end_time != null) {
                                Toast.makeText(this, "Syncing...", Toast.LENGTH_SHORT).show();
                                if (ApplicationSettings.containsPref(AppConstants.DATA_START_TIME)) {
                                    long start = ApplicationSettings.getPref(AppConstants.DATA_START_TIME, 0l);
                                    setSync(start, 0);
                                } else {
                                    getSmartUserCalendarDataFromServer(start_time, end_time, 0);
                                }
                                Intent intent = new Intent(this, ReuploadService.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(intent);
                                } else {
                                    startService(intent);
                                }
                                syncSettings();
                                getUearnDashboardInfo();
                                settingsApi(SmarterSMBApplication.SmartUser.getId());
                                getContactedList();
                                getUpdatedUserDetails();
                                getLocalDbFlpCounts(v);
                                callingReuploadService();

                                if (SmarterSMBApplication.currentStateIsStartMode || SmarterSMBApplication.stayAtHomeScenario) {
                                    getConnectedCustomerInfo(start_time, end_time, 0);
                                }
                                if (ApplicationSettings.containsPref(AppConstants.ALLOW_PING)) {
                                    boolean allowPing = ApplicationSettings.getPref(AppConstants.ALLOW_PING, false);
                                    if (allowPing) {
                                        sendPingRequestToCheckNetworkSpeed();
                                    }
                                }
                            }
                            if (internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                    || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                    || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                                disableStartButton();
                            } else {
                                enableStartButton();
                            }

                            refresh();
                        }
                    }
                } else {
                    disableStartButton();
                    internet_status.setText("NO INTERNET");
                    internet_status_goal_before.setText("NO INTERNET");
                    internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                    internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                    noNetworkAnimation(internet_status, internet_status_goal_before);
                    Toast.makeText(this, "You have no internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == R.id.adhocCallImageButton) {
            String number = adhocCallEditText.getText().toString();
            adhocCallEditText.setCursorVisible(false);
            if (number == null || number.isEmpty()) {
                adhocCallEditText.setText("Number cannot be empty");
            } else {
                adhocCallEditText.setText("Dialing.." + number);
                adhocCallFromHome(number);
            }
        } else if (id == R.id.imgNavClose) {
            toggleLeftDrawer();//Close Nav-drawer layout
        } else if (id == R.id.nevSettings) {
            toggleLeftDrawer();//Close Nav-drawer layout
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.navAbout) {
            toggleLeftDrawer();//Close Nav-drawer layout
        } else if (id == R.id.nevRefEarn) {
            toggleLeftDrawer();//Close Nav-drawer layout
            getReferAndEarn();
        } else if (id == R.id.navWorkSlots) {
            Intent intentSlots = new Intent(this, AgentSlotsActivity.class);
            //intentSlots.putExtra("SLOTSREDIRECT", "UearnProfileActivity");
            startActivity(intentSlots);
            finish();
        } else if (id == R.id.navFAQ) {
            toggleLeftDrawer();//Close Nav-drawer layout
            Intent intent = new Intent(this, UearnFAQActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.navAppVersion) {
            toggleLeftDrawer();//Close Nav-drawer layout
        } else if (id == R.id.nevFeedBack) {
            toggleLeftDrawer();
            getUserFeedback();
        } else if (id == R.id.navLogOut) {
            toggleLeftDrawer();//Close Nav-drawer layout
            signOutAndExit();
        } else if (id == R.id.manualDial) {
            SmarterSMBApplication.manualDialScenario = true;
            if (SmarterSMBApplication.currentStateIsStartMode) {
                if (ApplicationSettings.containsPref(AppConstants.UNSCHEDULED_CALL)) {
                    boolean schedulecall = ApplicationSettings.getPref(AppConstants.UNSCHEDULED_CALL, false);
                    if (schedulecall) {
                        scheduleCall();
                    }
                }
            } else {
                manualDialStartButtonClickedCheckDialog();
            }
        }
    }

    private void scheduleCall() {
        if (ApplicationSettings.containsPref(AppConstants.USER_ROLE) && ApplicationSettings.getPref(AppConstants.USER_ROLE, "") != null) {
            String role = ApplicationSettings.getPref(AppConstants.USER_ROLE, "");
            if (role.equalsIgnoreCase("user") || role.equalsIgnoreCase("non-user")) {
                SmarterSMBApplication.manualDialFromHomeScreen = true;
                Intent intent = new Intent(this, MakeACallActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Your user Id is not configured as an agent\nYou do not have permissions to make outgoing calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserFeedback() {
        Intent feedbackIntent = new Intent(this, FeedBackActivity.class);
        // Intent feedbackIntent = new Intent (this, UearnFeedbackActivity.class);
        startActivity(feedbackIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }



    private void getApplicationVersion() {
        try {
            PackageManager pm = activity.getPackageManager();
            String pkgName = activity.getPackageName();
            PackageInfo pkgInfo = null;
            try {
                pkgInfo = pm.getPackageInfo(pkgName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String ver = pkgInfo.versionName;
            navAppVersion.setText("App Version " + ver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReferAndEarn() {
        Intent referIntent = new Intent(this, UearnReferAndEarnActivity.class);
        startActivity(referIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void adhocCallFromHome(String number) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(4, getResources().getColor(R.color.button_color));
                adhocCallImageButton.setBackground(drawable);
            }
        });

        String toNumber = "";
        if(number != null && !number.isEmpty() && !number.startsWith("+91")) {
            toNumber = "+91" + number;
        } else {
            toNumber = number;
        }
        clearFlags();
        SmarterSMBApplication.outgoingCallNotInStartMode = true;
        SmarterSMBApplication.webViewOutgoingCallEventTriggered = true;
        createSbiMakeCall(this, toNumber, 0, "", "", "", "", "", "");
    }

    private void gotItButtonAction() {
        youCanEarnMore.setVisibility(View.VISIBLE);
        if(webViewText != null) {
            webViewText.setVisibility(View.GONE);
        }
        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
            if (fkControl) {

            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {

                }
            }
        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
            if (ibControl) {

            }
        } else {
            setVisibilityMenuItems("true");
        }
        remoteAutoDialingStarted = false;
        setUearnTodayLayoutVisibility("visible");
        start_layout.setVisibility(View.VISIBLE);
        gotItlayout.setVisibility(View.GONE);
        setStartButtonVisibility(true);
        if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
            if (ibControl) {
                if (ApplicationSettings.containsPref(AppConstants.ALLOW_ADHOC_CALL)) {
                    boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ALLOW_ADHOC_CALL, false);
                    if (allowAdhocCall) {
                        showWebViewAndAdhocCallLayout();
                    } else {
                        hideWebViewAndAdhocCallLayout();
                    }
                } else {
                    hideWebViewAndAdhocCallLayout();
                }
            } else {
                hideWebViewAndAdhocCallLayout();
            }
        } else if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
            boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
            if (adhocCall) {
                showWebViewAndAdhocCallLayout();
            } else {
                hideWebViewAndAdhocCallLayout();
            }
        } else {
            hideWebViewAndAdhocCallLayout();
        }

       setStartButtonText();
        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
            disableStartButton();
        }else{
            enableStartButton();
        }
        SmarterSMBApplication.moveToNormal = false;
        SmarterSMBApplication.isCampaignOver = false;

        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                showSystemUI();
            }
        }
    }

    private void initializeNavigation() {
        Toolbar toolbar = findViewById(R.id.tool_bar1);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        changeStatusBarColor(this);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu = menu;

        if(!SmarterSMBApplication.isRemoteDialledStart) {
            SmarterSMBApplication.currentAppState = "Idle";
            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                if (fkControl) {
                    getMenuInflater().inflate(R.menu.uearnhomescreen_fk_ib_menu, menu);
                } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        getMenuInflater().inflate(R.menu.uearnhomescreen_fk_ib_menu, menu);
                    } else {
                        getMenuInflater().inflate(R.menu.uearnhomescreen_menu, menu);
                    }
                } else {
                    getMenuInflater().inflate(R.menu.uearnhomescreen_menu, menu);
                }
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    getMenuInflater().inflate(R.menu.uearnhomescreen_fk_ib_menu, menu);
                } else {
                    getMenuInflater().inflate(R.menu.uearnhomescreen_menu, menu);
                }
            } else {
                getMenuInflater().inflate(R.menu.uearnhomescreen_menu, menu);
            }
            sync = menu.findItem(R.id.syncItem);
            chart = menu.findItem(R.id.chartItem);
            notification = menu.findItem(R.id.notification);
            return super.onCreateOptionsMenu(menu);
        } else {
            if (CommonUtils.isNetworkAvailable(this)) {
                SmarterSMBApplication.currentAppState = "Connecting";
            }
            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                if (fkControl) {
                    getMenuInflater().inflate(R.menu.uearnhomescreen_fk_ib_menu, menu);
                } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        getMenuInflater().inflate(R.menu.uearnhomescreen_fk_ib_menu, menu);
                    } else {
                        getMenuInflater().inflate(R.menu.uearnhomescreen_menu, menu);
                    }
                } else {
                    getMenuInflater().inflate(R.menu.uearnhomescreen_menu, menu);
                }
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    getMenuInflater().inflate(R.menu.uearnhomescreen_fk_ib_menu, menu);
                } else {
                    getMenuInflater().inflate(R.menu.uearnhomescreen_menu, menu);
                }
            } else {
                getMenuInflater().inflate(R.menu.uearnhomescreen_menu, menu);
            }
            sync = menu.findItem(R.id.syncItem);
            chart = menu.findItem(R.id.chartItem);
            notification = menu.findItem(R.id.notification);
            sync.setVisible(false);
            chart.setVisible(false);
            notification.setVisible(false);
            return super.onCreateOptionsMenu(menu);
        }
    }*/

    private void getLocalDbFlpCounts(int v) {

        MySql dbHelper = MySql.getInstance(this);
        db = dbHelper.getWritableDatabase();

        Cursor flpMissed = getFlpMissedCursor(v);
        if (flpMissed != null && flpMissed.getCount() > 0 && !flpMissed.isClosed()) {
            totalFlpMissed = flpMissed.getCount();
            followupsMissed.setText("" + (totalFlpMissed));
            flpMissed.close();
        }

        Cursor flpTodo = getFlpTodoCursor(v);
        if (flpTodo != null && flpTodo.getCount() > 0 && !flpTodo.isClosed()) {
            totalFlpTodo = flpTodo.getCount();
            flpTodo.close();
        }

        int totalFollowup =  flpMissed.getCount();
        followupsMissed.setText(""+(totalFollowup));

        Cursor firstCall = getFirstCallCursor();
        if (firstCall != null && firstCall.getCount() > 0 && !firstCall.isClosed()) {
            boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
            if (truePredictive) {
                totalFlpFirstCall = 0;
            } else {
                totalFlpFirstCall = firstCall.getCount();
                firstCallfollowups = totalFlpFirstCall;
            }
            SpannableString mySpannableString = new SpannableString(Integer.toString(totalFlpFirstCall));
            mySpannableString.setSpan(new UnderlineSpan(), 0, mySpannableString.length(), 0);
            totalFirstCall.setText(mySpannableString);
            totalFirstCall.setTextColor(getResources().getColor(R.color.uearn_green_cercle));
            firstCall.close();
        }

        int followupToCall = totalFlpFirstCall + totalFlpTodo + totalFlpMissed;
    }

    private void initCalender() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        start = c.getTimeInMillis();
        start_time = CommonUtils.getTimeFormatInISO(c.getTime());
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
        end = c.getTimeInMillis();
        end_time = CommonUtils.getTimeFormatInISO(c.getTime());
    }

    private Cursor getFirstCallCursor() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        String selection4 = "";
        if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
            selection4 = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT = " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " +  "'" + selectedLeadSource + "'";
        else
            selection4 = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT = " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
        Cursor firstCall = db.query("remindertbNew", null, selection4, null, null, null, "START_TIME ASC");
        return firstCall;
    }

    private Cursor getFlpTodoCursor(int v) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        Date date = new Date();
        String selection2;
        Long current = System.currentTimeMillis();
        boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
        if (truePredictive) {
            if (v == 0) {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
            } else if (v == 1) {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
            } else {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
            }
        } else {
            if (v == 0) {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' ";
            } else if (v == 1) {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' ";
            } else {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' ";
            }
        }
        Cursor flpTodo = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
        return flpTodo;
    }

    private Cursor getFlpMissedCursor(int v) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        Date date = new Date();
        Cursor flpMissed;
        String selection;
        Long current = System.currentTimeMillis();
        boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
        if (truePredictive) {
            if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                selection = "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + "0" + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
            else
                selection = "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + "0" + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        } else {
            if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                selection = "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
            else
                selection = "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        }
        flpMissed = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        return flpMissed;
    }

    private Cursor getFollowUpMissedCursor() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Date date = new Date();
        Cursor flpMissed;
        String selection = "";

        if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
            selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
        else
            selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";

        flpMissed = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        return flpMissed;
    }

    private void removeFragment() {
        if (getSupportFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
            if (fragmentTransaction != null && fragment != null) {
                fragmentTransaction.remove(fragment).commitAllowingStateLoss();
            }
        }
    }

    private void requestForData() {
        String data = getAppointmentCountAndLeadSource();
        sendRequestForData(data);
    }

    private String getAppointmentCountAndLeadSource() {
        String jsonData = "";
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        int count = 0;
        Cursor cursor = db.rawQuery("SELECT LEAD_SOURCE, COUNT(_id) from remindertbNew WHERE RESPONSE_STATUS = 'accepted' GROUP BY LEAD_SOURCE", null);
        JSONArray jsonArray = new JSONArray();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                try {
                    JSONObject jsonObj = new JSONObject();
                    String leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
                    int totalAppointment = cursor.getInt(cursor.getColumnIndex("COUNT(_id)"));
                    jsonObj.put("campaign", leadSource);
                    jsonObj.put("accepted", totalAppointment);
                    jsonArray.put(jsonObj);
                    cursor.moveToNext();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        JSONObject requestForDataObj = new JSONObject();
        try {
            requestForDataObj.put("activitytype", "AUTO DATA OVER");
            requestForDataObj.put("requestfordata", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonData = requestForDataObj.toString();
        return jsonData;
    }

    private void sendRequestForData(String message) {
        if (CommonUtils.isNetworkAvailable(this)) {
            if(activity != null && !activity.isFinishing()) {
                new APIProvider.GetRequestForData(message, 0, activity, "Sending request for data. Please wait..", new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        if (data != null && !data.isEmpty()) {

                        }
                    }
                }).requestForDataCall();
            }
        } else {
            setStartButtonText();
            sendRemoteDialStartRequest = false;
            uearnLoader.setVisibility(View.GONE);
            connectedLoader.setVisibility(View.GONE);
            completedFollowups.setVisibility(View.VISIBLE);
            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
            internet_status.setText("NO INTERNET");
            internet_status_goal_before.setText("NO INTERNET");
            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
            noNetworkAnimation(internet_status, internet_status_goal_before);
            disableStartButton();
            connectionStatusLayout.setVisibility(View.GONE);
        }
    }

    private void callToServerGetApi(String apiUrl, final String data_status, final int value) {
        new APIProvider.ServerGetOffline(apiUrl, 1l, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                if(value == 0) {
                    AppConstants.DATA_STATUS = data_status;
                }
            }
        }).call();
    }

    private void clearData(int i) {
        if (i == 0) {
            showSignOutNotification();
        }
        SmarterSMBApplication.currentNetworkType = "";
        Intent intent = new Intent(this, CalldataSendService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("batteryStatus", "100%");
        Calendar calendar = Calendar.getInstance();
        String logoutTimeStamp = DateFormat.format("dd-MMM-yyyy hh:mm a", calendar).toString();
        intent.putExtra("timeStamp", logoutTimeStamp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        deleteExistingAlarm();
        deleteAllLocalDB();
        String userEmail = "";
        String accessToken = "";
        String after_call_screen = "";
        String userId = "";
        String userStatus = "";

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }

        if (ApplicationSettings.getPref("accessToken", "") != null) {
            accessToken = ApplicationSettings.getPref("accessToken", "");
        }

        if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null) {
            after_call_screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
        }

        if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
            userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        }

        if (ApplicationSettings.getPref(AppConstants.USER_STATUS, "") != null) {
            userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        }

        SharedPreferences.Editor editor = ApplicationSettings.getEditor();
        if (editor != null) {
            editor.clear().commit();
        }

        ApplicationSettings.putPref(AppConstants.USERINFO_ID, userId);
        ApplicationSettings.putPref(AppConstants.USERINFO_EMAIL, userEmail);
//        ApplicationSettings.putPref("accessToken", accessToken);
//        ApplicationSettings.putPref(AppConstants.AFTERCALLACTIVITY_SCREEN, after_call_screen);
        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, true);
//        ApplicationSettings.putPref(AppConstants.USER_STATUS, userStatus);
        this.finish();

        if(android.os.Build.VERSION.SDK_INT >= 21) {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.AppTask> tasks = am.getAppTasks();
                if (tasks != null && tasks.size() > 0) {
                    tasks.get(0).setExcludeFromRecents(true);
                }
            }
        }
    }

    private void showSignOutNotification() {
        String message = "Signed out of Uearn Application";

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.small_logo)
                .setContentTitle("Uearn Message")
                .setContentText("Information")
                .setAutoCancel(true);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        builder.setSmallIcon(R.drawable.small_logo);
        builder.setWhen(0).setAutoCancel(true);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        nManager.notify("App Name", 237, builder.build());
    }

    public void deleteExistingAlarm() {
        Intent intent = new Intent(activity, Alarm_Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm1.cancel(pendingIntent);
    }

    public void deleteAllLocalDB() {
        MySql dbHelper = new MySql(this, "mydb", null, AppConstants.dBversion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("remindertbNew", null, null);
        db.delete("WorkOrder", null, null);
        db.delete("TeamMembers", null, null);
        db.delete("Ameyo_Push_Notifications", null, null);
        db.delete("ameyo_entries", null, null);
        db.delete("FirstCall", null, null);
        db.delete("ameyo_entries", null, null);
        db.close();
    }

    private void registerAllReceivers() {

        try {
            networkBroadcastReceiver = new NetworkBroadcastReceiver();
            IntentFilter networkBroadcastFilter = new IntentFilter();
            networkBroadcastFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(networkBroadcastReceiver, networkBroadcastFilter);
        } catch (Exception e) {
            Log.d("Unregister", "registerReceiver networkBroadcastReceiver" + e.getMessage());
        }

        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
            if (fkControl) {

            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {

                } else {
//                    try {
//                        connectivityReceiver = new ConnectivityReceiver();
//                        IntentFilter connectivityBroadcastFilter = new IntentFilter();
//                        connectivityBroadcastFilter.addAction("android.intent.action.AIRPLANE_MODE");
//                        registerReceiver(connectivityReceiver, connectivityBroadcastFilter);
//                    } catch (Exception e) {
//                        Log.d("Unregister", "registerReceiver connectivityReceiver" + e.getMessage());
//                    }
                }
            } else {
//                try {
//                    connectivityReceiver = new ConnectivityReceiver();
//                    IntentFilter connectivityBroadcastFilter = new IntentFilter();
//                    connectivityBroadcastFilter.addAction("android.intent.action.AIRPLANE_MODE");
//                    registerReceiver(connectivityReceiver, connectivityBroadcastFilter);
//                } catch (Exception e) {
//                    Log.d("Unregister", "registerReceiver connectivityReceiver" + e.getMessage());
//                }
            }
        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
            if (ibControl) {

            } else {
//                try {
//                    connectivityReceiver = new ConnectivityReceiver();
//                    IntentFilter connectivityBroadcastFilter = new IntentFilter();
//                    connectivityBroadcastFilter.addAction("android.intent.action.AIRPLANE_MODE");
//                    registerReceiver(connectivityReceiver, connectivityBroadcastFilter);
//                } catch (Exception e) {
//                    Log.d("Unregister", "registerReceiver connectivityReceiver" + e.getMessage());
//                }
            }
        } else {
//            try {
//                connectivityReceiver = new ConnectivityReceiver();
//                IntentFilter connectivityBroadcastFilter = new IntentFilter();
//                connectivityBroadcastFilter.addAction("android.intent.action.AIRPLANE_MODE");
//                registerReceiver(connectivityReceiver, connectivityBroadcastFilter);
//            } catch (Exception e) {
//                Log.d("Unregister", "registerReceiver connectivityReceiver" + e.getMessage());
//            }
        }

        try {
            myReceiver = new HeadPhoneReciever();
            IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(myReceiver, filter);
        } catch (Exception e) {
            Log.d("Unregister", "registerReceiver myReceiver" + e.getMessage());
        }

        if ((SmarterSMBApplication.SmartUser != null) && SmarterSMBApplication.SmartUser.getEmulationOn()) {
            connectionStatus.setText(getResources().getString(R.string.connection_good_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_good));
            testNow.setBackground(getResources().getDrawable(R.drawable.connection_good));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_good));
            enableStartButton();
            connectionStatusLayout.setVisibility(View.GONE);
        } else {
            try {
                mNetworkReceiver = new NetworkReceiver();
                registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (Exception e) {
                Log.d("Unregister", "registerReceiver mNetworkReceiver" + e.getMessage());
            }
        }
    }

    private void unregisterAllReceivers(){
        try {

            if ((SmarterSMBApplication.SmartUser != null) && SmarterSMBApplication.SmartUser.getEmulationOn()) {

            } else {
                if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                    boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                    if (adhocCall && SmarterSMBApplication.currentStateIsStartMode) {
                        if (mMessageReceiver != null) {
                            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                        }
                    }
                } else {
                    if (SmarterSMBApplication.currentStateIsStartMode) {
                        if (mMessageReceiver != null) {
                            if(SmarterSMBApplication.stayAtHomeScenario){

                            } else {
                                LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
                            }
                        }
                    }
                }
            }

            if (myReceiver != null) {
                unregisterReceiver(myReceiver);
            }

//            if (connectivityReceiver != null) {
//                unregisterReceiver(connectivityReceiver);
//            }

            if (networkBroadcastReceiver != null) {
                unregisterReceiver(networkBroadcastReceiver);
            }
            if(mNetworkReceiver != null) {
                unregisterReceiver(mNetworkReceiver);
            }
        } catch (Exception e) {

        }
    }

    private void unregisterConnectivityReceiver(){
        if(SmarterSMBApplication.isDiallingFromDoneList){

        } else {
//            if (connectivityReceiver != null) {
//                unregisterReceiver(connectivityReceiver);
//            }
        }
        /*if(mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SmarterSMBApplication.moveToRAD = false;
    }

    private void resetEarnValue() {
        bizPrize = ApplicationSettings.getPref(AppConstants.BIZ_PRIZE_VALUE, "");
        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) { daysLeft();
            agentGoalLayout.setVisibility(View.VISIBLE);
            youCanEarnMore.setVisibility(View.VISIBLE);
            setUearnTodayLayoutVisibility("visible");
            daysLeft();
            if(currentDay == 1) {
                String lastUpdateAt = ApplicationSettings.getPref(AppConstants.UPDATED_AT, "");
                if(lastUpdateAt.isEmpty()) {
                    if (bizPrize.equals("")) {
                        bizPrize = "10,000";
                        goalLayout.setVisibility(View.VISIBLE);
                        setGoalLayout.setVisibility(View.GONE);
                        setGoalsLayout.setVisibility(View.GONE);
                    } else {
                        goalLayout.setVisibility(View.GONE);
                        setGoalLayout.setVisibility(View.GONE);
                        setGoalsLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    boolean isValid = CommonUtils.compareToDate(lastUpdateAt);
                    if(isValid) {
                        bizPrize = "10,000";
                        goalLayout.setVisibility(View.VISIBLE);
                        setGoalLayout.setVisibility(View.GONE);
                        setGoalsLayout.setVisibility(View.GONE);
                    } else {
                        if (bizPrize.equals("")) {
                            bizPrize = "10,000";
                            goalLayout.setVisibility(View.VISIBLE);
                            setGoalLayout.setVisibility(View.GONE);
                            setGoalsLayout.setVisibility(View.GONE);
                        } else {
                            goalLayout.setVisibility(View.GONE);
                            setGoalLayout.setVisibility(View.GONE);
                            setGoalsLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                if (bizPrize.equals("")) {
                    bizPrize = "10,000";
                    goalEarnedTv.setText("10,000");
                    goalLayout.setVisibility(View.VISIBLE);
                    setGoalLayout.setVisibility(View.GONE);
                    setGoalsLayout.setVisibility(View.GONE);
                } else {
                    goalLayout.setVisibility(View.GONE);
                    setGoalLayout.setVisibility(View.GONE);
                    setGoalsLayout.setVisibility(View.VISIBLE);
                }
            }
        } else {
            agentGoalLayout.setVisibility(View.GONE);
            youCanEarnMore.setVisibility(View.GONE);
            today_uearn.setVisibility(View.GONE);
        }
        getProjectType();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        getCurrentNetworkType();
        boolean isSocketIOServiceRunning = isSocketServiceRunning(SocketIOService.class);
        if(!isSocketIOServiceRunning){
            if (ApplicationSettings.containsPref(AppConstants.SIO_REFRESH_TIME)) {
                String sioRefreshTime = ApplicationSettings.getPref(AppConstants.SIO_REFRESH_TIME, "");
                if (sioRefreshTime != null && !sioRefreshTime.isEmpty()) {
                    //Log.d("SocketIOTest", "Socket IO Call startService from Uearn Home");
                    Intent myService = new Intent(activity, SocketIOService.class);
                    startService(myService);
                }
            }
        }
        remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
        if(remoteAutoEnabled == null || remoteAutoEnabled.isEmpty()){
            remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.RAD_OPTIMIZED_POST, "");
        }
        if(remoteAutoDialingStarted) {
            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                if (fkControl) {
                    hideSystemUI();
                } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        hideSystemUI();
                    }
                }
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    hideSystemUI();
                }
            }
        }

        if (CommonUtils.isNetworkAvailable(this)) {
            if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                    || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                    || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
               disableStartButton();
            }else{
               enableStartButton();
            }
        } else {
            internet_status.setText("NO INTERNET");
            internet_status_goal_before.setText("NO INTERNET");
            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
            noNetworkAnimation(internet_status, internet_status_goal_before);
            disableStartButton();
        }

        if(SmarterSMBApplication.syncMenuItemClicked){
            SmarterSMBApplication.syncMenuItemClicked = false;
        } else {
            registerAllReceivers();
        }

        resetEarnValue();
        SmarterSMBApplication.setCurrentActivity(this);
        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }
        if(company != null && company.isEmpty()) {
            getUpdatedUserDetails();
        }

        if(SmarterSMBApplication.matchingInNumberNotInStartMode){
            navigateToUearnActivity();
        }

        if (retrySalesStageUpdateFailed) {
            retrySalesStageUpdateFailed = false;
            if(activity != null && !activity.isFinishing()) {
                retrySalesStageShowAlertDialog();
            } else {
                retrySalesStageUpdateFailed = false;
                endCall();
                if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                        || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                        || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                    disableStartButton();
                }else{
                    enableStartButton();
                }
                setStartButtonText();
                setStartButtonVisibility(true);
                youCanEarnMore.setVisibility(View.VISIBLE);
                setUearnTodayLayoutVisibility("visible");
                start_layout.setVisibility(View.GONE);
                hideMenuItemsForFKAndIB();
            }
            disableStartButton();
            return;
        }
        if (CommonUtils.isNetworkAvailable(this)) {
            String connectionStatusStr = internet_status.getText().toString();
            if (connectionStatusStr != null && !connectionStatusStr.isEmpty() && connectionStatusStr.equalsIgnoreCase("NO INTERNET")) {
                disableStartButton();
                return;
            }
        } else {
           disableStartButton();
            return;
        }

        pingInProgress = false;

        if(SmarterSMBApplication.isCampaignOver){
            moveToNormalAction();
        } else {
            if(SmarterSMBApplication.endTheSession){
                totalLoggedInTime();
                if (connectingStatus != null) {
                    connectionStatus.setText(getResources().getString(R.string.connection_good_msg));
                    connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_good));
                    testNow.setBackground(getResources().getDrawable(R.drawable.connection_good));
                    connectionStatusLayout.setVisibility(View.VISIBLE);
                    connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_good));
                    if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                            || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                            || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                        disableStartButton();
                    }else{
                        enableStartButton();
                    }
                    connectionStatusLayout.setVisibility(View.GONE);
                }
            } else {
                testNowLoader.setVisibility(View.VISIBLE);
                testNow.setVisibility(View.GONE);
                if ((SmarterSMBApplication.SmartUser!=null) && !SmarterSMBApplication.SmartUser.getEmulationOn()) {
                    if (connectingStatus != null) {
                        connectionStatus.setText(getResources().getString(R.string.connection_check_msg));
                        connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_check));
                        testNow.setBackground(getResources().getDrawable(R.drawable.connection_check));
                        connectionStatusLayout.setVisibility(View.VISIBLE);
                        connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_check));
                        connectionStatusLayout.setVisibility(View.GONE);
                    }
                }

                if ((SmarterSMBApplication.SmartUser!=null) && !SmarterSMBApplication.SmartUser.getEmulationOn()) {
                    sendPingRequestToCheckNetworkSpeed();
                }
            }
            if (SmarterSMBApplication.currentAppState != null && !SmarterSMBApplication.currentAppState.isEmpty() && SmarterSMBApplication.currentAppState.equalsIgnoreCase("Connecting")) {

            } else {
                SmarterSMBApplication.currentAppState = "Idle";
            }

            Handler uiHandler = new Handler();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    getOfflineUearnValue();
                    getOfflineContactedData();
                }
            });

            //if (CommonUtils.allowASF()) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    final long notificationCount = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
                    if (notificationCount > 0) {
                        tv_message_count.setText(String.valueOf(notificationCount));
                    } else {
                        getNotificationMessages();
                    }
                }
            });
            //}

            if (SmarterSMBApplication.isFirstCall) {
                //remoteAutoDialingStarted = true;
                setStartButtonVisibility(true);
                onStartlayout.setVisibility(View.GONE);
            }

            startSmbSio();
            getLocalDbFlpCounts(v);

            if (CommonUtils.isNetworkAvailable(this)) {

            } else {
                sendRemoteDialStartRequest = false;
                uearnLoader.setVisibility(View.GONE);
                connectedLoader.setVisibility(View.GONE);
                completedFollowups.setVisibility(View.VISIBLE);
                connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
                testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
                connectionStatusLayout.setVisibility(View.VISIBLE);
                connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
                internet_status.setText("NO INTERNET");
                internet_status_goal_before.setText("NO INTERNET");
                internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                noNetworkAnimation(internet_status, internet_status_goal_before);
                disableStartButton();
                connectionStatusLayout.setVisibility(View.GONE);
            }

            if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                if (UearnHome.dialingFromList || UearnHome.manualDialing) {

                } else {
                    clearAllContext();
                }
                if (AppConstants.KILL_APP) {
                    AppConstants.KILL_APP = false;
                    finishAffinity();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }

                if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
                    if (!SmarterSMBApplication.isRemoteDialledStart) {
                        if(SmarterSMBApplication.stayAtHomeScenario && remoteAutoEnabled.equals("onsolicit")){
//                            today_uearn.setVisibility(View.GONE);
//                            today_uearn_layout.setVisibility(View.GONE);
                            SmarterSMBApplication.currentStateIsStartMode = true;
                            setButtonTextAsStop();
                            return;
                        }
                    } else {
                        if (remoteAutoEnabled.equals("on")) {
                            boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
                            if (truePredictive) {
                                youCanEarnMore.setVisibility(View.GONE);
                                setUearnTodayLayoutVisibility("invisible");
                                start_layout.setVisibility(View.VISIBLE);
                            } else {
                                youCanEarnMore.setVisibility(View.GONE);
                                setUearnTodayLayoutVisibility("invisible");
                                start_layout.setVisibility(View.VISIBLE);
                                setButtonLayoutVisibility(false);
                                onStartlayout.setVisibility(View.VISIBLE);
                            }
                        } else if (remoteAutoEnabled.equals("onsolicit")) {
                            boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
                            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                                if (fkControl) {

                                }
                            } else if (truePredictive && CommonUtils.allowASF() == true) {
                                youCanEarnMore.setVisibility(View.GONE);
                                setUearnTodayLayoutVisibility("invisible");
                                start_layout.setVisibility(View.VISIBLE);
                            } else {
                                setButtonLayoutVisibility(false);
                                onStartlayout.setVisibility(View.VISIBLE);
                                setUearnTodayLayoutVisibility("invisible");
                            }
                        }
                    }
                }

                if (SmarterSMBApplication.noNetworkAvailable) {
                    SmarterSMBApplication.noNetworkAvailable = false;
                    if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                            || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                            || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                        disableStartButton();
                    }else{
                       enableStartButton();
                    }
                    setStartButtonText();
                    youCanEarnMore.setVisibility(View.VISIBLE);
                    setUearnTodayLayoutVisibility("visible");
                    start_layout.setVisibility(View.GONE);
                }

                if ((SmarterSMBApplication.SmartUser != null) && !SmarterSMBApplication.SmartUser.getEmulationOn()) {
                    if (SmarterSMBApplication.headphoneNotConnected) {
                        if (CommonUtils.isNetworkAvailable(this)) {
                            getUearnDashboardInfo();
                            getContactedList();
                        } else {
                            sendRemoteDialStartRequest = false;
                            uearnLoader.setVisibility(View.GONE);
                            connectedLoader.setVisibility(View.GONE);
                            completedFollowups.setVisibility(View.VISIBLE);
                            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
                            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
                            connectionStatusLayout.setVisibility(View.VISIBLE);
                            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
                            internet_status.setText("NO INTERNET");
                            internet_status_goal_before.setText("NO INTERNET");
                            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                            noNetworkAnimation(internet_status, internet_status_goal_before);
                            disableStartButton();
                            connectionStatusLayout.setVisibility(View.GONE);
                        }

                        remoteAutoDialingStarted = false;
                        showDialog();
                        SmarterSMBApplication.headphoneNotConnected = false;
                        youCanEarnMore.setVisibility(View.VISIBLE);
                        setUearnTodayLayoutVisibility("visible");
                        start_layout.setVisibility(View.GONE);
                    }
                }

                if (SmarterSMBApplication.redialStartRequest) {
                    if (CommonUtils.isNetworkAvailable(this)) {
                        getUearnDashboardInfo();
                        getContactedList();
                        remoteAutoDialingStarted = false;
                        SmarterSMBApplication.redialStartRequest = false;
                        youCanEarnMore.setVisibility(View.VISIBLE);
                        setUearnTodayLayoutVisibility("visible");
                        start_layout.setVisibility(View.GONE);
                    } else {
                        sendRemoteDialStartRequest = false;
                        uearnLoader.setVisibility(View.GONE);
                        connectedLoader.setVisibility(View.GONE);
                        completedFollowups.setVisibility(View.VISIBLE);
                        connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                        connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
                        testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
                        connectionStatusLayout.setVisibility(View.VISIBLE);
                        connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
                        internet_status.setText("NO INTERNET");
                        internet_status_goal_before.setText("NO INTERNET");
                        internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                        internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                        noNetworkAnimation(internet_status, internet_status_goal_before);
                        disableStartButton();
                        connectionStatusLayout.setVisibility(View.GONE);
                    }
                }

                if (SmarterSMBApplication.backToNetwork) {
                    youCanEarnMore.setVisibility(View.VISIBLE);
                    setUearnTodayLayoutVisibility("visible");
                    start_layout.setVisibility(View.GONE);
                    setButtonLayoutVisibility(true);
                    onStartlayout.setVisibility(View.GONE);
                    SmarterSMBApplication.backToNetwork = false;
                    callingReuploadService();
                    endActiveCall();
                    sendRemoteDialStopRequest("Taking a break");
                    return;
                }
                if (SmarterSMBApplication.sendCallDisconnectStopRequest) {
                    sendRemoteDialStopRequest("Campaign Over");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SmarterSMBApplication.sendCallDisconnectStopRequest = false;
                }
                if (SmarterSMBApplication.moveToRADSTOP) {
                    sendRemoteDialStopRequest(SmarterSMBApplication.stopRADMsg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SmarterSMBApplication.moveToRADSTOP = false;
                }

                if (SmarterSMBApplication.sendCallDisconnectStartFollowUpRequest) {
                    sendRemoteDialStopRequest("call followups");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SmarterSMBApplication.sendCallDisconnectStartFollowUpRequest = false;
                }

                if (resetHomeNotInStartMode) {
                    resetHomeNotInStartMode = false;
                    SmarterSMBApplication.oneCallIsActive = false;
                    resetHomeScreenToOriginalStateNotInStartMode();
                    return;
                }

                if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        if ((SmarterSMBApplication.navigateFromUearnProfile && !remoteAutoDialingStarted) || SmarterSMBApplication.sendStopRequest) {
                            if (webViewText != null) {
                                webViewText.setVisibility(View.GONE);
                            }
                            if(image_notification != null) {
                                image_notification.setVisibility(View.VISIBLE);
                            }
                            if(image_chat != null) {
                                image_chat.setVisibility(View.VISIBLE);
                            }
                            if(image_sync != null) {
                                image_sync.setVisibility(View.VISIBLE);
                            }
                            long notificationCount = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
                            if(notificationCount > 0){
                                tv_message_count.setVisibility(View.VISIBLE);
                            } else{
                                tv_message_count.setVisibility(View.GONE);
                            }
                            if (waitTimer != null) {
                                waitTimer.cancel();
                                waitTimer = null;
                            }
                            SmarterSMBApplication.disableStatusBarAndNavigation = false;
                            ServiceHandler.agentMissed = false;
                            SmarterSMBApplication.endTheSession = false;
                            SmarterSMBApplication.navigateFromUearnProfile = false;
                            SmarterSMBApplication.sendStopRequest = false;
                            setStartButtonText();
                            if (internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                    || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                    || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                               disableStartButton();
                            } else {
                               enableStartButton();
                            }
                            setStartButtonVisibility(true);
                            gotItlayout.setVisibility(View.GONE);
                            youCanEarnMore.setVisibility(View.VISIBLE);
                            setUearnTodayLayoutVisibility("visible");
                            start_layout.setVisibility(View.GONE);

                            if (SmarterSMBApplication.isRemoteDialledStart) {
                                if (ApplicationSettings.containsPref(AppConstants.CALL_WAITING)) {
                                    boolean callWaiting = ApplicationSettings.getPref(AppConstants.CALL_WAITING, false);
                                    if (callWaiting) {
                                        try {
                                            activateCW();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            String questionnaire = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE_FROM_SETTINGS, "");
                            ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);

                            SmarterSMBApplication.isRemoteDialledStart = false;
                            if (SmarterSMBApplication.stopRequestMsg != null && !SmarterSMBApplication.stopRequestMsg.isEmpty()) {
                                if (ApplicationSettings.containsPref(AppConstants.ALLOW_ADHOC_CALL)) {
                                    boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ALLOW_ADHOC_CALL, false);
                                    if (allowAdhocCall) {
                                        showWebViewAndAdhocCallLayout();
                                    }
                                }
                                sendRemoteDialStopRequest(SmarterSMBApplication.stopRequestMsg);
                                SmarterSMBApplication.stopRequestMsg = "";
                            } else {
                                if (ApplicationSettings.containsPref(AppConstants.ALLOW_ADHOC_CALL)) {
                                    boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ALLOW_ADHOC_CALL, false);
                                    if (allowAdhocCall) {
                                        showWebViewAndAdhocCallLayout();
                                    }
                                }
                                sendRemoteDialStopRequest("Taking a break");
                            }
                            SmarterSMBApplication.currentStateIsStartMode = false;
                            return;
                        }

                        if (ServiceHandler.agentMissed || SmarterSMBApplication.incomingCallRejectedByAgent || SmarterSMBApplication.endTheSession) {
                            totalLoggedInTime();
                            if (webViewText != null) {
                                webViewText.setVisibility(View.GONE);
                            }
                            if(image_notification != null) {
                                image_notification.setVisibility(View.VISIBLE);
                            }
                            if(image_chat != null) {
                                image_chat.setVisibility(View.VISIBLE);
                            }
                            if(image_sync != null) {
                                image_sync.setVisibility(View.VISIBLE);
                            }
                            long notificationCount = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
                            if(notificationCount > 0){
                                tv_message_count.setVisibility(View.VISIBLE);
                            } else{
                                tv_message_count.setVisibility(View.GONE);
                            }
                            if (waitTimer != null) {
                                waitTimer.cancel();
                                waitTimer = null;
                            }
                            SmarterSMBApplication.disableStatusBarAndNavigation = false;
                            ServiceHandler.agentMissed = false;
                            SmarterSMBApplication.endTheSession = false;
                            enableStartButton();
                            setButtonTextAsStop();
                            setStartButtonVisibility(true);
                            gotItlayout.setVisibility(View.GONE);
                            youCanEarnMore.setVisibility(View.GONE);
                            today_uearn.setVisibility(View.GONE);
                            start_layout.setVisibility(View.GONE);
                            if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                                boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                                if (adhocCall) {
                                    showWebViewAndAdhocCallLayout();
                                } else {
                                    hideWebViewAndAdhocCallLayout();
                                }
                            } else {
                                hideWebViewAndAdhocCallLayout();
                            }

                            if (SmarterSMBApplication.isRemoteDialledStart) {
                                if (ApplicationSettings.containsPref(AppConstants.CALL_WAITING)) {
                                    boolean callWaiting = ApplicationSettings.getPref(AppConstants.CALL_WAITING, false);
                                    if (callWaiting) {
                                        try {
                                            activateCW();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            SmarterSMBApplication.isRemoteDialledStart = false;
                            remoteAutoDialingStarted = true;
                            //sendRemoteDialStopRequest("Taking a break");
                            return;
                        }
                    }
                } else if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        if (ServiceHandler.agentMissed || SmarterSMBApplication.incomingCallRejectedByAgent || SmarterSMBApplication.endTheSession || SmarterSMBApplication.navigateFromUearnProfile) {
                            totalLoggedInTime();
                            if (webViewText != null) {
                                webViewText.setVisibility(View.GONE);
                            }
                            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                                fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                                if (fkControl) {
                                    endActiveCall();
                                }
                            }

                            if(image_notification != null) {
                                image_notification.setVisibility(View.VISIBLE);
                            }
                            if(image_chat != null) {
                                image_chat.setVisibility(View.VISIBLE);
                            }
                            if(image_sync != null) {
                                image_sync.setVisibility(View.VISIBLE);
                            }
                            long notificationCount = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
                            if(notificationCount > 0){
                                tv_message_count.setVisibility(View.VISIBLE);
                            } else{
                                tv_message_count.setVisibility(View.GONE);
                            }

                            if (waitTimer != null) {
                                waitTimer.cancel();
                                waitTimer = null;
                            }
                            SmarterSMBApplication.disableStatusBarAndNavigation = false;
                            ServiceHandler.agentMissed = false;
                            SmarterSMBApplication.endTheSession = false;
                            //SmarterSMBApplication.navigateFromUearnProfile = false;
                            setStartButtonText();
                            setStartButtonVisibility(true);
                            if (internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                    || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                    || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                                disableStartButton();
                            } else {
                                enableStartButton();
                            }
                            gotItlayout.setVisibility(View.GONE);
                            youCanEarnMore.setVisibility(View.VISIBLE);
                            setUearnTodayLayoutVisibility("visible");
                            start_layout.setVisibility(View.GONE);
                            onStartlayout.setVisibility(View.GONE);
                            setButtonLayoutVisibility(true);

                            if (SmarterSMBApplication.isRemoteDialledStart) {
                                if (ApplicationSettings.containsPref(AppConstants.CALL_WAITING)) {
                                    boolean callWaiting = ApplicationSettings.getPref(AppConstants.CALL_WAITING, false);
                                    if (callWaiting) {
                                        try {
                                            activateCW();
                                        } catch (Exception e) {
                                            Log.e("UearnHome", "Error calling activateCW()", e);
                                            int version_code = CommonUtils.getVersionCode(activity);
                                            String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                                                    ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>UearnHome - Error calling activateCW(): " + e.getMessage();
                                            ServiceApplicationUsage.callErrorLog(message);
                                        }
                                    }
                                }
                            }
                            SmarterSMBApplication.isRemoteDialledStart = false;
                            if (CommonUtils.allowASF() ) {
                                if(SmarterSMBApplication.navigateFromUearnProfile){
                                    SmarterSMBApplication.navigateFromUearnProfile = false;
                                }else{
                                    today_uearn.setVisibility(View.GONE);
                                    today_uearn_layout.setVisibility(View.GONE);
                                    setButtonTextAsStop();
                                    SmarterSMBApplication.navigateFromUearnProfile = false;
                                }

                                return;
                            } else {
                                sendRemoteDialStopRequest("Taking a break");
                                SmarterSMBApplication.navigateFromUearnProfile = false;
                                return;
                            }

                        }
                    }

                } else if (remoteAutoEnabled.equals("onsolicit") && SmarterSMBApplication.endTheSession) {
                    totalLoggedInTime();
                    boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
                    if (truePredictive && SmarterSMBApplication.isRemoteDialledStart) {
                        start_layout.setVisibility(View.GONE);
                        setUearnTodayLayoutVisibility("visible");
                    } else {
                        if (webViewText != null) {
                            webViewText.setVisibility(View.GONE);
                        }
                        setVisibilityMenuItems("false");
                        if (waitTimer != null) {
                            waitTimer.cancel();
                            waitTimer = null;
                        }

                        SmarterSMBApplication.disableStatusBarAndNavigation = false;
                        ServiceHandler.agentMissed = false;
                        SmarterSMBApplication.endTheSession = false;
                        enableStartButton();
                        setButtonTextAsStop();
                        setStartButtonVisibility(true);
                        gotItlayout.setVisibility(View.GONE);
                        youCanEarnMore.setVisibility(View.GONE);
                        today_uearn.setVisibility(View.GONE);
                        start_layout.setVisibility(View.GONE);
                        SmarterSMBApplication.navigateFromUearnProfile = false;
                        SmarterSMBApplication.sendStopRequest = false;
                        SmarterSMBApplication.isRemoteDialledStart = false;
                    }
                } else if (ServiceHandler.agentMissed || SmarterSMBApplication.incomingCallRejectedByAgent || SmarterSMBApplication.endTheSession || SmarterSMBApplication.navigateFromUearnProfile) {
                    totalLoggedInTime();
                    if (webViewText != null) {
                        webViewText.setVisibility(View.GONE);
                    }
                    setVisibilityMenuItems("true");
                    if (waitTimer != null) {
                        waitTimer.cancel();
                        waitTimer = null;
                    }
                    SmarterSMBApplication.disableStatusBarAndNavigation = false;
                    ServiceHandler.agentMissed = false;
                    SmarterSMBApplication.endTheSession = false;
                    SmarterSMBApplication.navigateFromUearnProfile = false;
                    setStartButtonText();
                    setStartButtonVisibility(true);
                    if (internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                            || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                            || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                        disableStartButton();
                    } else {
                        enableStartButton();
                    }
                    gotItlayout.setVisibility(View.GONE);
                    youCanEarnMore.setVisibility(View.VISIBLE);
                    setUearnTodayLayoutVisibility("visible");
                    start_layout.setVisibility(View.GONE);

                    if (SmarterSMBApplication.isRemoteDialledStart) {
                        if (ApplicationSettings.containsPref(AppConstants.CALL_WAITING)) {
                            boolean callWaiting = ApplicationSettings.getPref(AppConstants.CALL_WAITING, false);
                            if (callWaiting) {
                                try {
                                    activateCW();
                                } catch (Exception e) {
                                    Log.e("UearnHome", "Error calling activateCW()", e);
                                    int version_code = CommonUtils.getVersionCode(activity);
                                    String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                                            ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>UearnHome - Error calling activateCW(): " + e.getMessage();
                                    ServiceApplicationUsage.callErrorLog(message);
                                }
                            }
                        }
                    }

                    SmarterSMBApplication.isRemoteDialledStart = false;
                    SmarterSMBApplication.currentStateIsStartMode = false;
                    sendRemoteDialStopRequest("Taking a break");
                    return;
                }

                if (SmarterSMBApplication.moveToRAD) {
                    boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
                    if (truePredictive) {
                        setStartButtonText();
                        SmarterSMBApplication.moveToRAD = false;
                        remoteAutoDialingStarted = false;
                        sendRemoteDialStartRequest = false;
                        if (CommonUtils.isNetworkAvailable(this)) {
                            Cursor flpMissedCusrosr = getFollowUpMissedCursor();
                            if (flpMissedCusrosr != null && flpMissedCusrosr.getCount() > 0) {
                            } else {
                                if (CommonUtils.allowASF() == true && remoteAutoEnabled.equals("onsolicit")) {
                                    setButtonTextAsStop();
                                } else {
                                    hideWebViewAndAdhocCallLayout();
                                    sendRemoteDialStartRequest("Ready to take any call");
                                }
                            }
                        } else {
                            sendRemoteDialStartRequest = false;
                            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                String configText = ApplicationSettings.getPref(AppConstants.RAD_MESSAGE_VALUE, "");
                if (configText != null && !configText.isEmpty()) {
                    if (configText.startsWith("http://") || configText.startsWith("https://")) {
                        webViewText.loadUrl(configText);
                    } else {
                        if (NotificationData.backToNetworkResponseMsg != null && !NotificationData.backToNetworkResponseMsg.isEmpty()) {
                            String viewData = "<!DOCTYPE html><html><head><meta name='viewport' content='width=device-width, user-scalable=no'/><title>Viewport Test</title></head><body style='margin:0px;'><div><center><font color=green><H2>" + NotificationData.backToNetworkResponseMsg + "</H2></center></div></body></html>";
                            webViewText.loadDataWithBaseURL(null, viewData, "text/html", "utf-8", null);
                            NotificationData.backToNetworkResponseMsg = "";
                            sendRemoteDialStopRequest("Taking a break");
                        } else {
                            if (configText != null && !configText.isEmpty() && configText.contains("campaign is already dialled out")) {
                                webViewText.loadDataWithBaseURL(null, configText, "text/html", "utf-8", null);
                            } else if (configText != null && !configText.isEmpty()) {
                                webViewText.loadDataWithBaseURL(null, configText, "text/html", "utf-8", null);
                            }
                        }
                    }
                } else {
                    String homepage = ApplicationSettings.getPref(AppConstants.DEFAULT_HOME_PAGE, "");
                    if (homepage != null && !homepage.isEmpty()) {
                        if (homepage.startsWith("http://") || homepage.startsWith("https://")) {
                            webViewText.loadUrl(homepage);
                        } else {
                            if (NotificationData.backToNetworkResponseMsg != null && !NotificationData.backToNetworkResponseMsg.isEmpty()) {
                                String viewData = "<!DOCTYPE html><html><head><meta name='viewport' content='width=device-width, user-scalable=no'/><title>Viewport Test</title></head><body style='margin:0px;'><div><center><font color=green><H2>" + NotificationData.backToNetworkResponseMsg + "</H2></center></div></body></html>";
                                webViewText.loadDataWithBaseURL(null, viewData, "text/html", "utf-8", null);
                                NotificationData.backToNetworkResponseMsg = "";
                                sendRemoteDialStopRequest("Taking a break");
                            } else {
                                webViewText.loadDataWithBaseURL(null, homepage, "text/html", "utf-8", null);
                            }
                        }
                    } else {
                        //webViewText.loadDataWithBaseURL(null, defaultRADText, "text/html", "utf-8", null);
                    }
                }
                if (SmarterSMBApplication.matchingInNumberNotInStartMode) {
                    navigateToUearnActivity();
                }
                return;
            }

            boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
            if (sequencialEndpoint) {
                if (UearnActivity.startAutodialler) {
                    UearnActivity.startAutodialler = false;

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    click();
                }
            } else {
                if (UearnActivity.redialScenarioCompleted) {
                    if (autoDial) {
                        UearnActivity.redialScenarioCompleted = false;
                        click();
                    }
                }
            }
            if (ServiceHandler.agentMissed) {
                ServiceHandler.agentMissed = false;
                start();
            }
        }

        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
            disableStartButton();
        }else{
            enableStartButton();
        }

    }

    private void getNotificationMessages(){
        final long[] count = {1};
        final List<String> status = new ArrayList<>();
        try {
            String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID,"");

            new APIProvider.GetNotificationMessagesApi("", 1, this, "Processing your request.", new API_Response_Listener<NotificationMessageResponse>() {

                @Override
                public void onComplete(NotificationMessageResponse data, long request_code, int failure_code) {
                    if (data != null) {
                        for (NotificationMessages notificationMessages : data.getSuccess()) {
                            if(notificationMessages.getStatus().equalsIgnoreCase("UNREAD")){
                                status.add(notificationMessages.getStatus());
                            }
                        }

                        if(status.size() > 0){
                            tv_message_count.setText(String.valueOf(status.size()));
                        }else {
                            tv_message_count.setVisibility(View.GONE);
                        }
                        ApplicationSettings.putLongPref(AppConstants.notificationCount,status.size());
                    }
                }
            }).call();
        }catch (Exception ex){
            Log.e("NotificationMessage","Exception :: "+ex);
        }
    }

    private void moveToNormalAction() {
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }
        onStartlayout.setVisibility(View.GONE);
        setStartButtonVisibility(true);
        gotItlayout = findViewById(R.id.gotItlayout);
        gotItlayout.setOnClickListener(this);
        enableStartButton();
        remoteAutoDialingStarted = false;
        SmarterSMBApplication.redialStartRequest = false;

        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                SmarterSMBApplication.disableStatusBarAndNavigation = false;
            }
        }

        if (ApplicationSettings.containsPref(AppConstants.CALL_WAITING)) {
            boolean callWaiting = ApplicationSettings.getPref(AppConstants.CALL_WAITING, false);
            if (callWaiting) {
                try {
                    activateCW();
                } catch (Exception e){
                    Log.e("UearnHome", "Error calling activateCW()", e);
                    int version_code = CommonUtils.getVersionCode(activity);
                    String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                            ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>UearnHome - Error calling activateCW(): " + e.getMessage();
                    ServiceApplicationUsage.callErrorLog(message);
                }
            }
        }

        SmarterSMBApplication.isRemoteDialledStart = false;

        setStartButtonText();
        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
            disableStartButton();
        }else{
           enableStartButton();
        }

        if(webViewText != null) {
            String configText = ApplicationSettings.getPref(AppConstants.RAD_MESSAGE_VALUE,"");
            Log.e("UearnHome","configText :: "+configText);
            if(configText != null && !configText.isEmpty()) {
                webViewText.loadDataWithBaseURL(null, configText, "text/html", "utf-8", null);
            }
            webViewText.setVisibility(View.VISIBLE);
        }
        gotItlayout.setVisibility(View.VISIBLE);
        setStartButtonVisibility(false);
        youCanEarnMore.setVisibility(View.GONE);
        today_uearn.setVisibility(View.GONE);
        start_layout.setVisibility(View.VISIBLE);
        SmarterSMBApplication.redialStartRequest = false;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onStartlayout.setVisibility(View.GONE);
                setStartButtonVisibility(false);
                youCanEarnMore.setVisibility(View.GONE);
                today_uearn.setVisibility(View.GONE);
                start_layout.setVisibility(View.VISIBLE);

                hideWebViewAndAdhocCallLayout();
                gotItlayout.setVisibility(View.VISIBLE);
                SmarterSMBApplication.moveToNormal = false;
            }
        });

        if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
            if (ibControl) {

            } else {
                SmarterSMBApplication.isCampaignOver = true;
            }
        } else {
            SmarterSMBApplication.isCampaignOver = true;
        }

        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                showSystemUI();
            }
        }

        // To check once for field issue
        ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if(cn != null && cn.getClassName().equals("smarter.uearn.money.activities.UearnActivity")) {
            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                if (fkControl) {

                }
            } else if (SmarterSMBApplication.currentAppState != null && !SmarterSMBApplication.currentAppState.isEmpty() && SmarterSMBApplication.currentAppState.equalsIgnoreCase("Connecting")) {
                if (this.isFinishing()) {

                } else {
                    this.finish();
                }
            }
        }
        //sendRemoteDialStopRequest("");
    }

    private void sendPingRequestToCheckNetworkSpeed() {
        if (CommonUtils.isNetworkAvailable(this)) {

            pingAndCheckWifiConnectivityStatus();

        } else {
            uearnLoader.setVisibility(View.GONE);
            connectedLoader.setVisibility(View.GONE);
            completedFollowups.setVisibility(View.VISIBLE);
            testNowLoader.setVisibility(View.GONE);
            SmarterSMBApplication.networkCheckInProgress = false;
            testNow.setVisibility(View.VISIBLE);
            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
            internet_status.setText("NO INTERNET");
            internet_status_goal_before.setText("NO INTERNET");
            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
            noNetworkAnimation(internet_status, internet_status_goal_before);
            disableStartButton();
            connectionStatusLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterConnectivityReceiver();
//if (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null) {
        Log.e("UearnActivity","SEESION_CODE :: "+ApplicationSettings.getPref("SESSION_CODE",""));
        String mUsername= ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

        //}
        if(channelResponse != null && socket != null) {
            try {
                JSONObject smeObj = new JSONObject();
                smeObj.put("channel", channelResponse.getChannels().getSME().getChannel());
                smeObj.put("userid", Integer.parseInt(channelResponse.getUserid()));
                smeObj.put("username", mUsername);
                smeObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                socket.emit("USER_DISCONNECT", smeObj);
            } catch (JSONException e) {
            }

            try {
                JSONObject tlObj = new JSONObject();
                tlObj.put("channel", channelResponse.getChannels().getTL().getChannel());
                tlObj.put("userid", Integer.parseInt(channelResponse.getUserid()));
                tlObj.put("username", mUsername);
                tlObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                socket.emit("USER_DISCONNECT", tlObj);
            } catch (JSONException e) {
            }
            try {
                JSONObject groupObj = new JSONObject();
                groupObj.put("channel", channelResponse.getChannels().getGROUP().getChannel());
                groupObj.put("userid", Integer.parseInt(channelResponse.getUserid()));
                groupObj.put("username", mUsername);
                groupObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                socket.emit("USER_DISCONNECT", groupObj);
            } catch (JSONException e) {
            }
            try {
                JSONObject techObj = new JSONObject();
                techObj.put("channel", channelResponse.getChannels().getTECH().getChannel());
                techObj.put("userid", Integer.parseInt(channelResponse.getUserid()));
                techObj.put("username", mUsername);
                techObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                socket.emit("USER_DISCONNECT", techObj);
            } catch (JSONException e) {
            }

            //socket.emit("USER_DISCONNECT", channelResponse.getChannels().getTL().getChannel(), userId, mUsername);
            //socket.emit("USER_DISCONNECT", channelResponse.getChannels().getTECH().getChannel(), userId, mUsername);
            //socket.emit("USER_DISCONNECT", channelResponse.getChannels().getSME().getChannel(), userId, mUsername);
            //socket.emit("USER_DISCONNECT", channelResponse.getChannels().getGROUP().getChannel(), userId, mUsername);
            try {

                if(isTLSelected){
                    Log.e("ChatActivity","for test 1:: ");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("id", UUID.randomUUID().toString());
                    jsonObj.put("session_id", ApplicationSettings.getPref("SESSION_CODE",""));
                    jsonObj.put("channel", channelResponse.getChannels().getTL().getChannel());
                    jsonObj.put("to_id", Integer.parseInt(channelResponse.getChannels().getTL().getUserid()));
                    jsonObj.put("from_id", Integer.parseInt(userId));
                    jsonObj.put("username", mUsername);
                    jsonObj.put("message", "");
                    jsonObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                    jsonObj.put("type", "SESSION_ENDS");
                    socket.emit("CHAT_MESSAGE", jsonObj);
                    isTLSelected = false;
                }

                if(isTechSelected) {
                    Log.e("ChatActivity","for test 2:: ");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("id", UUID.randomUUID().toString());
                    jsonObj.put("session_id", ApplicationSettings.getPref("SESSION_CODE",""));
                    jsonObj.put("channel", channelResponse.getChannels().getTECH().getChannel());
                    jsonObj.put("to_id", Integer.parseInt(channelResponse.getChannels().getTECH().getUserid()));
                    jsonObj.put("from_id", Integer.parseInt(userId));
                    jsonObj.put("username", mUsername);
                    jsonObj.put("message", "");
                    jsonObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                    jsonObj.put("type", "SESSION_ENDS");
                    socket.emit("CHAT_MESSAGE", jsonObj);
                    isTechSelected = false;
                }
                if(isSMESelected) {
                    Log.e("ChatActivity","for test 3:: ");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("id", UUID.randomUUID().toString());
                    jsonObj.put("session_id", ApplicationSettings.getPref("SESSION_CODE",""));
                    jsonObj.put("channel", channelResponse.getChannels().getSME().getChannel());
                    if(null != channelResponse.getChannels().getSME().getUserid()) {
                        jsonObj.put("to_id", Integer.parseInt(channelResponse.getChannels().getSME().getUserid()));
                    }else{
                        jsonObj.put("to_id", Integer.parseInt(channelResponse.getGroupid()));
                    }
                    //jsonObj.put("to_id", Integer.parseInt(channelResponse.getChannels().getSME().getUserid()));
                    jsonObj.put("from_id", Integer.parseInt(userId));
                    jsonObj.put("username", mUsername);
                    jsonObj.put("message", "");
                    jsonObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                    jsonObj.put("type", "SESSION_ENDS");
                    socket.emit("CHAT_MESSAGE", jsonObj);
                    isSMESelected = false;
                }
                if(isGroupSelected) {
                    Log.e("ChatActivity","for test 4:: ");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("id", UUID.randomUUID().toString());
                    jsonObj.put("session_id", ApplicationSettings.getPref("SESSION_CODE",""));
                    jsonObj.put("channel", channelResponse.getChannels().getGROUP().getChannel());
                    jsonObj.put("to_id", Integer.parseInt(channelResponse.getGroupid()));
                    jsonObj.put("from_id", Integer.parseInt(userId));
                    jsonObj.put("username", mUsername);
                    jsonObj.put("message", "");
                    jsonObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                    jsonObj.put("type", "SESSION_ENDS");
                    socket.emit("CHAT_MESSAGE", jsonObj);
                    isGroupSelected = false;
                }


            } catch (JSONException e) {
            }

            ApplicationSettings.removePref("SESSION_CODE");
            Log.e("UearnActivity","SEESION_CODE after :: "+ApplicationSettings.getPref("SESSION_CODE",""));
            Log.e("UearnActivity","Channel Resposner :: "+channelResponse + " :: "+socket);
            socket.disconnect();
            socket = null;
            channelResponse = null;

        }

    }

    private boolean shouldRecord() {
        if (ApplicationSettings.getPref(AppConstants.SETTING_CALL_RECORDING_STATE, false)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        exitOnclickOfBackButtonDialog(UearnHome.this, "", "Are you sure you want to exit from Uearn?").show();
    }

    public void getUpdatedUserDetails() {
        if(SmarterSMBApplication.SmartUser != null) {
            new APIProvider.SmartUser_Get(SmarterSMBApplication.SmartUser.getId(), GET_USER, this).call();
        }
    }

    @Override
    public void onComplete(SmartUser smartUser, long request_code, int failure_code) {
        if (request_code == GET_USER) {
            if (failure_code == APIAdapter.NO_FAILURE) {
                if (smartUser != null) {
                    smartUser.doSave();
                    SmarterSMBApplication.SmartUser = smartUser;
                } else {
                    Toast.makeText(this, "Error while getting details from Server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void settingsApi(final String userId) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.GetSettings(userId, 22, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        }
    }

    private void syncSettings() {
        Toast.makeText(this, "Syncing...", Toast.LENGTH_SHORT).show();
        ServiceUserProfile.getUsersTeam();
        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        String[] data = {Constants.GET, Constants.DOMAIN_URL + Constants.SETTINGS_STAGES + userId};
        CommonUtils.checkToken();
        WebService webCall = new WebService(7, new ResponseInterface() {
            @Override
            public boolean getResponseStatus(boolean result) {
                return true;
            }
        });
        webCall.execute(data);
        if(userId != null) {
            getTeamMembersCall(userId);
        }
    }

    private void getTeamMembersCall(String id) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.Get_All_Team_Members(id, 535, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                @Override
                public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                    if (data != null && data.size() > 0) {
                        CommonUtils.saveTeamdataToLocalDB(UearnHome.this, data);
                        for (int i = 0; i < data.size(); i++) {
                            String request = data.get(i).request;
                            if ((request != null) && request.equals("invited")) {
                                Intent intent = new Intent(UearnHome.this, AcceptOrRejectNotificationService.class);
                                Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(data.get(i));
                                intent.putExtra("groupsUserInfoBundle", bundle);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(intent);
                                } else {
                                    startService(intent);
                                }
                            }
                        }
                    }
                }
            }).call();
        }
    }

    private void getConnectedCustomerInfo(final String startTime, final String endTime, final int nextgetcount) {
        if (CommonUtils.isNetworkAvailable(this)) {

            final RotateAnimation rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(1000);
            rotate.setRepeatCount(ValueAnimator.INFINITE);
            String user_id = "";
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            }

            final GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(user_id, startTime, endTime, "smartercalendar");
            getCalendarEntryInfo.limit = nextgetcount;
            new APIProvider.Get_ConnectedCustomerInfo(getCalendarEntryInfo, AppConstants.GET_CAL_EVENTS, null, "", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.has("connectedinfo")) {
                                String info = jsonObject.getString("connectedinfo");
                                if (info != null && !info.isEmpty()) {
                                    JSONObject infoObject = new JSONObject(info);

                                    if (infoObject.has("number")) {
                                        String customerNumber = infoObject.getString("number");
                                        NotificationData.dialledCustomerNumber = customerNumber;
                                        NotificationData.isSocketResponse = true;
                                    }

                                    if (infoObject.has("name")) {
                                        String customerName = infoObject.getString("name");
                                        NotificationData.dialledCustomerName = customerName;
                                    }

                                    if (infoObject.has("lead_source")) {
                                        String leadSource = infoObject.getString("lead_source");
                                        if (leadSource != null && !leadSource.isEmpty() && !leadSource.equals("null")) {
                                            NotificationData.leadSource = leadSource;
                                        } else {
                                            NotificationData.leadSource = "";
                                        }
                                    }

                                    if (infoObject.has("status")) {
                                        String status = infoObject.getString("status");
                                        if (status != null && !status.isEmpty() && !status.equals("null")) {
                                            NotificationData.statusString = status;
                                        } else {
                                            NotificationData.statusString = "";
                                        }
                                    }

                                    if (infoObject.has("substatus1")) {
                                        String substatus1 = infoObject.getString("substatus1");
                                        if (substatus1 != null && !substatus1.isEmpty() && !substatus1.equals("null")) {
                                            NotificationData.substatus1 = substatus1;
                                        } else {
                                            NotificationData.substatus1 = "";
                                        }
                                    }

                                    if (infoObject.has("substatus2")) {
                                        String substatus2 = infoObject.getString("substatus2");
                                        if (substatus2 != null && !substatus2.isEmpty() && !substatus2.equals("null")) {
                                            NotificationData.substatus2 = substatus2;
                                        } else {
                                            NotificationData.substatus2 = "";
                                        }
                                    }

                                    if (infoObject.has("notes")) {
                                        String notes = infoObject.getString("notes");
                                        if (notes != null && !notes.isEmpty() && !notes.equals("null")) {
                                            NotificationData.notes_string = notes;
                                        } else {
                                            NotificationData.notes_string = "";
                                        }
                                    } else if (infoObject.has("remarks")) {
                                        String remarks = infoObject.getString("remarks");
                                        if (remarks != null && !remarks.isEmpty() && !remarks.equals("null")) {
                                            NotificationData.notes_string = remarks;
                                        } else {
                                            NotificationData.notes_string = "";
                                        }
                                    }

                                    if (infoObject.has("transactionid")) {
                                        String transactionId = infoObject.getString("transactionid");
                                        NotificationData.outboundDialledTransactionId = transactionId;
                                    }

                                    if (infoObject.has("message")) {
                                        NotificationData.customKVS = infoObject.getString("message");
                                    }

                                    if (infoObject.has("questionnaire")) {
                                        String questionnaire = "";
                                        ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, "");
                                        if (ApplicationSettings.containsPref(AppConstants.CUSTOMER_QUES)) {
                                            boolean customerQues = ApplicationSettings.getPref(AppConstants.CUSTOMER_QUES, false);
                                            if (customerQues) {
                                                questionnaire = infoObject.getString("questionnaire");
                                                if (questionnaire != null && !questionnaire.isEmpty() && !questionnaire.equals("null")) {
                                                    if (questionnaire.contains("\\n")) {
                                                        String newline = System.getProperty("line.separator");
                                                        questionnaire = questionnaire.replaceAll("\\\\n", newline);
                                                        ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                                                    } else if(!questionnaire.equals("[]")) {
                                                        ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                                                    } else {
                                                        ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                                                    }
                                                } else {
                                                    questionnaire = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE_FROM_SETTINGS, "");
                                                    ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                                                }
                                            } else {
                                                questionnaire = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE_FROM_SETTINGS, "");
                                                ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                                            }
                                        } else {
                                            questionnaire = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE_FROM_SETTINGS, "");
                                            ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                                        }
                                    }
                                    launchRecentApp();
                                    SmarterSMBApplication.outgoingCallNotInStartMode = true;
                                    navigateToUearnActivity();
                                } else {
                                    Toast.makeText(activity, "No info for connected customer", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            }).call();

        } else {
            if (sync != null) {
                sync.setVisible(true);
            }

            sendRemoteDialStartRequest = false;
            uearnLoader.setVisibility(View.GONE);
            connectedLoader.setVisibility(View.GONE);
            completedFollowups.setVisibility(View.VISIBLE);
            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
            internet_status.setText("NO INTERNET");
            internet_status_goal_before.setText("NO INTERNET");
            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
            noNetworkAnimation(internet_status, internet_status_goal_before);
            disableStartButton();
            connectionStatusLayout.setVisibility(View.GONE);
        }
    }

    private void getSmartUserCalendarDataFromServer(final String startTime, final String endTime, final int nextgetcount) {
        if (CommonUtils.isNetworkAvailable(this)) {

            final RotateAnimation rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(1000);
            rotate.setRepeatCount(ValueAnimator.INFINITE);
            String user_id = "";
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            }

            final GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(user_id, startTime, endTime, "smartercalendar");
            getCalendarEntryInfo.limit = nextgetcount;
            new APIProvider.Get_CalendarEvents(getCalendarEntryInfo, AppConstants.GET_CAL_EVENTS, new API_Response_Listener<ArrayList<GetCalendarEntryInfo>>() {
                @Override
                public void onComplete(ArrayList<GetCalendarEntryInfo> data, long request_code, int failure_code) {
                    if (data != null && data.size() > 0) {
                        new SaveToLocalDB(data, nextgetcount + 100).execute();

                    } else {
                        if (sync != null) {
                            sync.setVisible(true);
                        }
                    }
                    if (CommonUtils.notifier != null) {
                        CommonUtils.notifier.notify(true);
                    }
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                    String syncDate = "" + df.format(c.getTime());
                    if (syncDate != null) {
                        ApplicationSettings.putPref(AppConstants.SYNC_FOLLOWUP_DATE, syncDate);
                    }
                }
            }).call();

        } else {
            if (sync != null) {
                sync.setVisible(true);
            }

            sendRemoteDialStartRequest = false;
            uearnLoader.setVisibility(View.GONE);
            connectedLoader.setVisibility(View.GONE);
            completedFollowups.setVisibility(View.VISIBLE);
            connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
            testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
            internet_status.setText("NO INTERNET");
            internet_status_goal_before.setText("NO INTERNET");
            internet_status.setTextColor(getResources().getColor(R.color.no_connection));
            internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
            noNetworkAnimation(internet_status, internet_status_goal_before);
            disableStartButton();
            connectionStatusLayout.setVisibility(View.GONE);
        }
    }

    class SaveToLocalDB extends AsyncTask<ArrayList<GetCalendarEntryInfo>, Void, Void> {
        ArrayList<GetCalendarEntryInfo> getCalendarEntryInfos;
        int nextgetcount;

        public SaveToLocalDB(ArrayList<GetCalendarEntryInfo> getCalendarEntryInfos, int nextCount) {
            if (getCalendarEntryInfos != null && getCalendarEntryInfos.size() > 0) {
                this.getCalendarEntryInfos = getCalendarEntryInfos;
                this.nextgetcount = nextCount;
            }
        }

        @Override
        protected Void doInBackground(ArrayList<GetCalendarEntryInfo>... params) {
            CommonUtils.saveCalendarSyncDataToLocalDB(db, start, end, activity, getCalendarEntryInfos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (ApplicationSettings.containsPref(AppConstants.DATA_START_TIME)) {
                long start = ApplicationSettings.getPref(AppConstants.DATA_START_TIME, 0l);
                setSync(start, nextgetcount);
            } else {
                if (start_time != null && end_time != null) {
                    getSmartUserCalendarDataFromServer(start_time, end_time, nextgetcount);
                }
            }
            super.onPostExecute(aVoid);
        }
    }

    private void setSync(long startValue, int count) {
        String time = CommonUtils.getTimeFormatInISO(new Date(startValue));
        long pastValue = startValue - (7 * 24 * 60 * 60 * 1000);
        if (startValue > start && startValue < end) {
            getSmartUserCalendarDataFromServer(time, end_time, count);
        } else if (pastValue > start) {
            getSmartUserCalendarDataFromServer(time, end_time, count);

        } else if (start > startValue) {
            getSmartUserCalendarDataFromServer(time, end_time, count);
        } else {
            if (sync != null) {
                sync.setVisible(true);
            }
        }
    }

    private void retrySalesStageShowAlertDialog() {

        try {

            final Dialog dialog = CustomSingleButtonDialog.buildAlertDialog("Network Error","Please check your internet connection and speed to continue dialling.", this, false);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            TextView btnYes = (TextView) dialog.findViewById(R.id.btn_yes);
            btnYes.setText("OK");
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                    retrySalesStageUpdateFailed = false;
                    endCall();
                    if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                            || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                            || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                        disableStartButton();
                    }else{
                       enableStartButton();
                    }
                    setStartButtonText();
                    setStartButtonVisibility(true);
                    youCanEarnMore.setVisibility(View.VISIBLE);
                    setUearnTodayLayoutVisibility("visible");
                    start_layout.setVisibility(View.GONE);
                    hideMenuItemsForFKAndIB();
                }
            });
            dialog.show();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void endCall() {
        UearnHome.remoteAutoDialingStartClicked = false;
        sendRemoteDialStartRequest = false;
        remoteAutoDialingStarted = false;
        callEndedFromHomeScreen = true;
        endActiveCall();
    }

    public String getAllSalesStatusEntriesFromDB() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM SmarterContact", null);

        if (cursor != null && cursor.getCount() > 0) {
            JSONArray resultSet = new JSONArray();
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null) {
                        try {
                            if (cursor.getString(i) != null) {
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }
            cursor.close();
            if (db != null && db.isOpen()) {
                db.close();
            }
            return resultSet.toString();
        } else {
            return "";
        }
    }

    private void clearPreferences() {
        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
        UearnActivity.serverDialingRedialScenario = false;
        ApplicationSettings.putPref(AppConstants.STATUS_CONNECTED, false);
        ApplicationSettings.putPref(AppConstants.CUSTOMER_DISCONNECT, "");
        ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, "");
        ApplicationSettings.putPref(AppConstants.AFTER_CALL_NAME, "");
    }

    private void clearAllContext() {

        ApplicationSettings.putPref(UearnActivity.AFTER_CALL_NOTES, "");
        ApplicationSettings.putPref(UearnActivity.AFTER_CALL_ASSIGN, "");
        ApplicationSettings.putPref(AppConstants.AFTER_CALL_AUDIO_URL, "");
        ApplicationSettings.putPref(UearnActivity.AFTER_CALL_DESIGNATION, "");
        ApplicationSettings.putPref(UearnActivity.AFTER_CALL_PHONE, "");
        ApplicationSettings.putPref(UearnActivity.AFTER_CALL_COMPANY, "");
        ApplicationSettings.putPref(UearnActivity.AFTER_CALL_EMAIL, "");
        ApplicationSettings.putPref(UearnActivity.AFTER_CALL_NAME, "");
        ApplicationSettings.putPref(UearnActivity.CUSTOMER_TO_CONTACT, "");
        ApplicationSettings.putPref(UearnActivity.AFTER_CALL_ADDRESS, "");
        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
        ApplicationSettings.putPref(AppConstants.CALL_END_TIME, 0l);
        ApplicationSettings.putPref(AppConstants.CALL_SUBMIT_TIME, 0l);
        ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, "");
        ApplicationSettings.putPref(AppConstants.CUSTOMER_DISCONNECT, "");
        ApplicationSettings.putPref(AppConstants.STATUS_CONNECTED,false);

        NotificationData.knolarity_name = "";
        NotificationData.knolarity_number = "";
        NotificationData.substatus1 = "";
        NotificationData.substatus2 = "";
        NotificationData.statusString = "";
        NotificationData.notes_string = "";
        NotificationData.source = "";
        NotificationData.referredBy = "";
        NotificationData.customKVS = "";
        UearnActivity.serverDialingRedialScenario = false;
        DashboardAgent.dialingFromList = false;
        SmarterSMBApplication.agentDisconnectScenario = false;
        SmarterSMBApplication.customerDisconnectScenario = false;
        SmarterSMBApplication.legADisconnectScenario = false;
        SmarterSMBApplication.navigateToACP = false;
        SmarterSMBApplication.connectedCustomerNumber = "";
        SmarterSMBApplication.connectedCustomerName = "";
        SmarterSMBApplication.connectedCustomerState = "";
        SmarterSMBApplication.isCurrentQuesMandatory = false;

        if(SmarterSMBApplication.connectedCustomerInProcess){
            SmarterSMBApplication.connectedCustomerInProcess = false;
        }

        if(SmarterSMBApplication.disconnectedCustomerInProcess){
            SmarterSMBApplication.disconnectedCustomerInProcess = false;
        }
    }

    private void pingAndCheckWifiConnectivityStatus() {
        String str = "";
        pingResponse = "";
        pingResponselist = new ArrayList<Double>();
        if(!pingInProgress) {
            startPingTimer();
        }
    }

    private void callingReuploadService() {
        Intent aIntent = new Intent(this, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(aIntent);
        } else {
            this.startService(aIntent);
        }
    }

    private void startTimer(long waitTime) {
        waitTimer = new CountDownTimer(waitTime, 1000) {
            public void onTick(long millisUntilFinished) {
                if(SmarterSMBApplication.incomingCallRejectedByAgent){
                    onStartlayout.setVisibility(View.GONE);
                    if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                        boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                        if (fkControl) {

                        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                            if (ibControl) {

                            } else {
                                setStartButtonVisibility(true);
                            }
                        } else {
                            setStartButtonVisibility(true);
                        }
                    } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                        boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                        if (ibControl) {

                        } else {
                            setStartButtonVisibility(true);
                        }
                    } else {
                        setStartButtonVisibility(true);
                    }
                    boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
                    if(remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty() && truePredictive){

                    } else {
                        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                            if (fkControl) {

                            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                                if (ibControl) {

                                } else {
                                    enableStartStopButton();
                                }
                            } else {
                                enableStartStopButton();
                            }
                        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                            if (ibControl) {

                            } else {
                                enableStartStopButton();
                            }
                        } else {
                            enableStartStopButton();
                        }
                    }
                    resetHomeScreenToOriginalState();
                }
            }
            public void onFinish() {
                if (!SmarterSMBApplication.isCallLanded) {
                    onStartlayout.setVisibility(View.GONE);
                    if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                        boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                        if (fkControl) {

                        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                            if (ibControl) {

                            } else {
                                setStartButtonVisibility(false);
                            }
                        } else {
                            setStartButtonVisibility(false);
                        }
                    } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                        boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                        if (ibControl) {

                        } else {
                            setStartButtonVisibility(false);
                        }
                    } else {
                        setStartButtonVisibility(false);
                    }
                    boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
                    if(remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty() && truePredictive){

                    } else {
                        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                            if (fkControl) {

                            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                                if (ibControl) {

                                } else {
                                    //enableStartStopButton();
                                }
                            } else {
                                //enableStartStopButton();
                            }
                        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                            if (ibControl) {

                            } else {
                                //enableStartStopButton();
                            }
                        } else {
                            //enableStartStopButton();
                        }
                    }
                }
                if(SmarterSMBApplication.incomingCallRejectedByAgent){
                    resetHomeScreenToOriginalState();
                } else {
                    boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
                    if (isLogout) {

                    } else {
                        if(remoteAutoDialingStarted) {
                            endActiveCall();
                            SmarterSMBApplication.oneCallIsActive = false;
                            if(SmarterSMBApplication.stayAtHomeScenario){
                                if (waitTimer != null) {
                                    waitTimer.cancel();
                                    waitTimer = null;
                                }
                                enableStartButton();
                                setButtonTextAsStop();
                                setStartButtonVisibility(true);
                                webViewText.setVisibility(View.GONE);
                                SmarterSMBApplication.isRemoteDialledStart = false;
                                today_uearn.setVisibility(View.VISIBLE);
                                today_uearn_layout.setVisibility(View.VISIBLE);
                                showUearnTodayUI();
                                setVisibilityMenuItems("true");
                            } else {
                                navigateToUearnActivity();
                            }
                        } else {
                            if(waitTimer != null) {
                                waitTimer.cancel();
                                waitTimer = null;
                            }
                        }
                    }
                }
            }
        }.start();
    }

    private void resetHomeScreenToOriginalStateNotInStartMode() {
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                showSystemUI();
            }
        }

        SmarterSMBApplication.incomingCallRejectedByAgent = false;
        ServiceHandler.agentMissed = false;

        UearnHome.remoteAutoDialingStartClicked = false;
        sendRemoteDialStartRequest = false;
        remoteAutoDialingStarted = false;

        uearnLoader.setVisibility(View.GONE);
        connectedLoader.setVisibility(View.GONE);

        setStartButtonText();
        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
            disableStartButton();
        }else{
            enableStartButton();
        }

        setStartButtonVisibility(true);
        enableStartButton();
        onStartlayout.setVisibility(View.GONE);
        youCanEarnMore.setVisibility(View.VISIBLE);
        setUearnTodayLayoutVisibility("visible");
        start_layout.setVisibility(View.GONE);
        setButtonLayoutVisibility(true);

        if (connectingStatus != null) {
            connectionStatus.setText(getResources().getString(R.string.connection_good_msg));
            connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_good));
            testNow.setBackground(getResources().getDrawable(R.drawable.connection_good));
            connectionStatusLayout.setVisibility(View.VISIBLE);
            connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_good));
            if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                    || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                    || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                disableStartButton();
            }else{
               enableStartButton();
            }
            connectionStatusLayout.setVisibility(View.GONE);
        }
        testNowLoader.setVisibility(View.GONE);
        testNow.setVisibility(View.VISIBLE);
    }

    private void resetHomeScreenToOriginalState() {
        setVisibilityMenuItems("true");
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                showSystemUI();
            }
        }

        SmarterSMBApplication.incomingCallRejectedByAgent = false;
        ServiceHandler.agentMissed = false;

        UearnHome.remoteAutoDialingStartClicked = false;
        sendRemoteDialStartRequest = false;
        remoteAutoDialingStarted = false;

        uearnLoader.setVisibility(View.GONE);
        connectedLoader.setVisibility(View.GONE);

        setStartButtonText();
        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
            disableStartButton();
        }else{
            enableStartButton();
        }

        setStartButtonVisibility(true);
        enableStartButton();
        onStartlayout.setVisibility(View.GONE);
        youCanEarnMore.setVisibility(View.VISIBLE);
        setUearnTodayLayoutVisibility("visible");
        start_layout.setVisibility(View.GONE);
        setButtonLayoutVisibility(true);
        sendRemoteDialStopRequest("Taking a break");
    }

    private void navigateToUearnActivity() {
        if(SmarterSMBApplication.systemAlertReceived) {
            NotificationData.isSocketResponse = true;
        }
        Intent intent = new Intent(this, UearnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        intent.putExtra("showinprogress", 1);
        startActivity(intent);
        this.finish();
    }

    private void launchRecentApp() {
        ActivityManager activtyManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos)
        {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName()))
            {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    class AsyncTaskPing extends AsyncTask<String, Void, ArrayList<Double>> {
        boolean isMobiledata = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected ArrayList<Double> doInBackground(String... strings) {
            try {
                pingInProgress  = true;
                String response = Utils.ping("www.google.com");
                if (response != null && !response.isEmpty()) {
                    if(response.contains(".")) {
                        int index = response.indexOf(".") + 2;
                        if (index > 0) {
                            String str = response.substring(0, index);
                            if (str != null && !str.isEmpty()) {
                                Double d = Double.valueOf(str);
                                pingResponselist.add(d);
                            }
                        }
                    } else {
                        if (response.contains(" ")) {
                            response = response.substring(0, response.indexOf(" "));
                            Double d = Double.valueOf(response);
                            pingResponselist.add(d);
                        } else {
                            Double d = Double.valueOf(response);
                            pingResponselist.add(d);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pingResponselist;
        }
        @Override
        protected void onPostExecute(ArrayList<Double> pingResponselist) {
            super.onPostExecute(pingResponselist);
            if(pingTimer != null) {
                pingTimer.cancel();
                pingTimer = null;
            }
            timerStopped = true;
            double total = 0;
            if(pingResponselist != null && pingResponselist.size() > 0){
                for(int i=0; i<pingResponselist.size(); i++){
                    total = total + pingResponselist.get(i);
                }
                double average = total / pingResponselist.size();
                average = Math.round(average * 100.0) / 100.0;
                pingResponse = Double.toString(average);
            }
            pingInProgress  = false;
            if(timerStopped) {
                String str = "";
                timerStopped = false;
                if (pingResponse != null && !pingResponse.isEmpty() && pingResponse.contains(".")) {
                    str = pingResponse.split("\\.")[0];
                } else {
                    str = pingResponse;
                }
                testNowLoader.setVisibility(View.GONE);
                testNow.setVisibility(View.VISIBLE);
                SmarterSMBApplication.networkCheckInProgress = false;

                if(SmarterSMBApplication.isRemoteDialledStart) {

                } else {
                    if(gotItlayout.getVisibility() == View.VISIBLE){

                    } else {
                        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                            if (fkControl) {

                            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                                if (ibControl) {

                                }
                            }
                        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                            if (ibControl) {

                            }
                        } else {
                            setVisibilityMenuItems("true");
                        }
                    }
                }

                if (str != null && !str.isEmpty()) {
                    str = str.replaceAll("\\s", "");
                    str = Utils.getNumericString(str);
                    int result = Integer.parseInt(str);
                    SmarterSMBApplication.pingStatusResponse = str;
                    SmarterSMBApplication.pingStatusResponse = str;
                    if (result > 0 && result < 50) {
                        connectionStatus.setText(getResources().getString(R.string.connection_good_msg));
                        connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_good));
                        testNow.setBackground(getResources().getDrawable(R.drawable.connection_good));
                        connectionStatusLayout.setVisibility(View.VISIBLE);
                        connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_good));
                        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                            disableStartButton();
                        }else{
                            internet_status.setText("GOOD");
                            internet_status_goal_before.setText("GOOD");
                            enableStartButton();
                        }
                        connectionStatusLayout.setVisibility(View.GONE);
                    } else if (result >= 50 && result <= 500) {
                        connectionStatus.setText(getResources().getString(R.string.connection_average_msg));
                        connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_average));
                        testNow.setBackground(getResources().getDrawable(R.drawable.connection_average));
                        connectionStatusLayout.setVisibility(View.VISIBLE);
                        connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_average));
                        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                            disableStartButton();
                        }else{
                            internet_status.setText("MODERATE");
                            internet_status_goal_before.setText("MODERATE");
                            enableStartButton();
                        }
                        connectionStatusLayout.setVisibility(View.GONE);
                    } else if (result >= 500 && result <= 750) {
                        connectionStatus.setText(getResources().getString(R.string.connection_weak_msg));
                        connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_weak));
                        testNow.setBackground(getResources().getDrawable(R.drawable.connection_weak));
                        connectionStatusLayout.setVisibility(View.VISIBLE);
                        connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_weak));
                        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                            disableStartButton();
                        }else{
                            internet_status.setText("AVERAGE");
                            internet_status_goal_before.setText("AVERAGE");
                           enableStartButton();
                        }
                        connectionStatusLayout.setVisibility(View.GONE);
                    } else if (result > 750) {
                        connectionStatus.setText(getResources().getString(R.string.connection_poor_msg));
                        connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_poor));
                        testNow.setBackground(getResources().getDrawable(R.drawable.connection_poor));
                        connectionStatusLayout.setVisibility(View.VISIBLE);
                        connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_poor));
                        internet_status.setText("POOR");
                        internet_status_goal_before.setText("POOR");
                        internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                        internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                            disableStartButton();
                        }else{
                           enableStartButton();
                        }
                        connectionStatusLayout.setVisibility(View.GONE);
                    }
                } else {
                    sendRemoteDialStartRequest = false;
                    uearnLoader.setVisibility(View.GONE);
                    connectedLoader.setVisibility(View.GONE);
                    completedFollowups.setVisibility(View.VISIBLE);
                    connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                    connectionStatus.setBackgroundColor(getResources().getColor(R.color.no_connection));
                    testNow.setBackground(getResources().getDrawable(R.drawable.no_connection));
                    connectionStatusLayout.setVisibility(View.VISIBLE);
                    connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.no_connection));
                    internet_status.setText("NO INTERNET");
                    internet_status_goal_before.setText("NO INTERNET");
                    internet_status.setTextColor(getResources().getColor(R.color.no_connection));
                    internet_status_goal_before.setTextColor(getResources().getColor(R.color.no_connection));
                    noNetworkAnimation(internet_status, internet_status_goal_before);
                    disableStartButton();
                    connectionStatusLayout.setVisibility(View.GONE);
                    if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                            || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                            || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                        disableStartButton();
                    }else{
                        enableStartButton();
                    }
                }
            } else {
                connectionStatus.setText(getResources().getString(R.string.connection_good_msg));
                connectionStatus.setBackgroundColor(getResources().getColor(R.color.connection_good));
                testNow.setBackground(getResources().getDrawable(R.drawable.connection_good));
                connectionStatusLayout.setVisibility(View.VISIBLE);
                connectionStatusLayout.setBackgroundColor(getResources().getColor(R.color.connection_good));
                connectionStatusLayout.setVisibility(View.GONE);
                if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                        || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                        || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                    disableStartButton();
                }else{
                    internet_status.setText("GOOD");
                    internet_status_goal_before.setText("GOOD");
                    enableStartButton();
                }
            }
            if(SmarterSMBApplication.matchingInNumberNotInStartMode){
                navigateToUearnActivity();
            }
        }
    }
    private void startPingTimer() {
        AsyncTaskPing asyncTask=new AsyncTaskPing();
        asyncTask.execute("");
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                if (SmarterSMBApplication.disableStatusBarAndNavigation) {
                    hideSystemUI();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(SmarterSMBApplication.isRemoteDialledStart) {
            return false;
        } else {
            if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
                boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
                if (systemControl) {
                    if (SmarterSMBApplication.disableStatusBarAndNavigation) {
                        return false;
                    } else {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if(setGoalLayout.getVisibility() != View.VISIBLE){
                                actionOnBack();
                            }else{
                                onResume();
                                ll_bottom_layout.setVisibility(View.VISIBLE);
                                hintsTv.setVisibility(View.VISIBLE);
                                if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                                    boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                                    if (allowAdhocCall) {
                                        adhocCallCardView.setVisibility(View.VISIBLE);
                                        adhocCallViewLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                        }
                        return true;
                    }
                } else {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if(setGoalLayout.getVisibility() != View.VISIBLE){
                            actionOnBack();
                        }else{
                            onResume();
                            ll_bottom_layout.setVisibility(View.VISIBLE);
                            hintsTv.setVisibility(View.VISIBLE);
                            if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                                boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                                if (allowAdhocCall) {
                                    adhocCallCardView.setVisibility(View.VISIBLE);
                                    adhocCallViewLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            } else {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(setGoalLayout.getVisibility() != View.VISIBLE){
                        actionOnBack();
                    }else{
                        onResume();
                        ll_bottom_layout.setVisibility(View.VISIBLE);
                        hintsTv.setVisibility(View.VISIBLE);
                        if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                            boolean allowAdhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                            if (allowAdhocCall) {
                                adhocCallCardView.setVisibility(View.VISIBLE);
                                adhocCallViewLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    private void actionOnBack() {
        exitOnclickOfBackButtonDialog(UearnHome.this, "", "Are you sure you want to exit from Uearn?").show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                if(SmarterSMBApplication.disableStatusBarAndNavigation) {
                    if (hasFocus) {
                        hideSystemUI();
                    } else {
                        showSystemUI();
                    }
                } else {
                    showSystemUI();
                }
            }
        } else {
            if(SmarterSMBApplication.isRemoteDialledStart) {

            } else {
                finishCurrentActivity();
            }
        }
    }

    private void hideSystemUI() {
        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                if(decorView == null) {
                    decorView = getWindow().getDecorView();
                }
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }

    private void showSystemUI() {
        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
            if (systemControl) {
                if(decorView == null) {
                    decorView = getWindow().getDecorView();
                }
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    private void finishCurrentActivity() {
        ActivityManager am = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if(cn != null && cn.getClassName().equals("smarter.uearn.money.activities.UearnActivity")) {
            if (Build.VERSION.SDK_INT < 16) {
                ActivityCompat.finishAffinity(this);
            } else {
                finishAffinity();
            }
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
        }
    }

    private void setVisibilityMenuItems(String value){
        if(value != null && !value.isEmpty() && value.equalsIgnoreCase("true")){
            if(image_notification != null) {
                image_notification.setVisibility(View.VISIBLE);
            }
            if(image_chat != null) {
                image_chat.setVisibility(View.VISIBLE);
            }
            if(image_sync != null) {
                image_sync.setVisibility(View.VISIBLE);
            }
            final long notificationCount = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
            if(notificationCount > 0){
                tv_message_count.setVisibility(View.VISIBLE);
            }else{
                tv_message_count.setVisibility(View.GONE);
            }
        } else if(value != null && !value.isEmpty() && value.equalsIgnoreCase("false")){
            if(image_notification != null) {
                image_notification.setVisibility(View.GONE);
            }
            if(image_chat != null) {
                image_chat.setVisibility(View.GONE);
            }
            if(image_sync != null) {
                image_sync.setVisibility(View.GONE);
                tv_message_count.setVisibility(View.GONE);
            }
        }
    }

    public Dialog buildTwoButtonDialog(Activity activity, String title, String message) {
        final Dialog twoButtonDialog = new Dialog(activity);
        twoButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        twoButtonDialog.setContentView(R.layout.uearn_activity_common_two_button);
        twoButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView tvTitle = twoButtonDialog.findViewById(R.id.tvDialogTitle);
        tvTitle.setText(title);
        TextView tvMessage = twoButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);
        return twoButtonDialog;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @RequiresPermission(android.Manifest.permission.MODIFY_PHONE_STATE)
    public void endActiveCall() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager tm = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (tm != null) {
                    tm.endCall();
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                Class classTelephony = Class.forName(telephonyManager.getClass().getName());
                Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
                methodGetITelephony.setAccessible(true);
                Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
                Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
                methodEndCall.invoke(telephonyInterface);
            }
        } catch (Exception e) {
            Log.e("UearnHome", "Error calling ITelephony#endActiveCall()", e);
            int version_code = CommonUtils.getVersionCode(this);
            String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                    ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>UearnHome - Error calling ITelephony#endActiveCall(): " + e.getMessage();
            ServiceApplicationUsage.callErrorLog(message);
        }
    }

    private void setUearnTodayLayoutVisibility(String value){
        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
            if (fkControl) {
                showUearnTodayUI();
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    showUearnTodayUI();
                }
            }
        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
            if (ibControl) {
                showUearnTodayUI();
            }
        } else {
            if(value != null && !value.isEmpty() && value.equalsIgnoreCase("visible")){
                today_uearn.setVisibility(View.VISIBLE);
                today_uearn_layout.setVisibility(View.VISIBLE);
                showUearnTodayUI();
            } else if(value != null && !value.isEmpty() && value.equalsIgnoreCase("invisible")){
                today_uearn.setVisibility(View.INVISIBLE);
                today_uearn_layout.setVisibility(View.INVISIBLE);
                leadsconnectedAndfollowupLayout.setVisibility(View.GONE);
                activeTimeLayout.setVisibility(View.GONE);
            }
        }
    }

    private void showUearnTodayUI(){
        internetStatusLayout.setVisibility(View.VISIBLE);
        if (ApplicationSettings.containsPref(AppConstants.SHOW_UI)) {
            boolean showUI = ApplicationSettings.getPref(AppConstants.SHOW_UI, false);
            if (showUI) {
                leadsconnectedAndfollowupLayout.setVisibility(View.VISIBLE);
                activeTimeLayout.setVisibility(View.VISIBLE);
                if (ApplicationSettings.containsPref(AppConstants.ADDITIONAL_LOGIN_INFO)) {
                    boolean additionalLoginInfo = ApplicationSettings.getPref(AppConstants.ADDITIONAL_LOGIN_INFO, false);
                    if (additionalLoginInfo) {
                        additionalLoginInfoLayout.setVisibility(View.VISIBLE);
                    } else {
                        additionalLoginInfoLayout.setVisibility(View.GONE);
                    }
                } else {
                    additionalLoginInfoLayout.setVisibility(View.GONE);
                }
            } else {
                leadsconnectedAndfollowupLayout.setVisibility(View.GONE);
                activeTimeLayout.setVisibility(View.GONE);
                additionalLoginInfoLayout.setVisibility(View.GONE);
            }
        } else {
            leadsconnectedAndfollowupLayout.setVisibility(View.VISIBLE);
            activeTimeLayout.setVisibility(View.VISIBLE);
            if (ApplicationSettings.containsPref(AppConstants.ADDITIONAL_LOGIN_INFO)) {
                boolean additionalLoginInfo = ApplicationSettings.getPref(AppConstants.ADDITIONAL_LOGIN_INFO, false);
                if (additionalLoginInfo) {
                    additionalLoginInfoLayout.setVisibility(View.VISIBLE);
                } else {
                    additionalLoginInfoLayout.setVisibility(View.GONE);
                }
            } else {
                additionalLoginInfoLayout.setVisibility(View.GONE);
            }
        }
    }

    static int getLoggedInTimeDifference(long loggedInStartTime, long loggedInEndTime) {
        if (loggedInEndTime > 0 && loggedInStartTime > 0) {
            int totalLoggedInTime = (int)loggedInEndTime - (int)loggedInStartTime;
            return totalLoggedInTime;
        }
        return 0;
    }

    private void totalLoggedInTime(){
        long loggedInEndTime= System.currentTimeMillis();
        ApplicationSettings.putPref(AppConstants.LOGGED_IN_END_TIME, loggedInEndTime);
        long loggedInStartTime = ApplicationSettings.getPref(AppConstants.LOGGED_IN_START_TIME, 0l);
        long loggedInTime = getLoggedInTimeDifference(loggedInStartTime, loggedInEndTime);
        ApplicationSettings.putPref(AppConstants.LOGGED_IN_TIME, loggedInTime);

        long totalLoggedInTime = ApplicationSettings.getPref(AppConstants.TOTAL_LOGGED_IN_TIME, 0l);
        totalLoggedInTime = getTotalLoggedInTime(totalLoggedInTime, loggedInTime);
        ApplicationSettings.putPref(AppConstants.TOTAL_LOGGED_IN_TIME, totalLoggedInTime);
        ApplicationSettings.putPref(AppConstants.LOGGED_IN_START_TIME, 0l);
        ApplicationSettings.putPref(AppConstants.LOGGED_IN_END_TIME, 0l);
        ApplicationSettings.putPref(AppConstants.LOGGED_IN_TIME, 0l);
    }

    static int getTotalLoggedInTime(long totalLoggedInTime, long loggedInTime) {
        int sumOfLoggedInTime = (int)totalLoggedInTime + (int)loggedInTime;
        return sumOfLoggedInTime;
    }

    private String convertLoggedInMillisecondsToTime (long totalLoggedInTime){
        int hr = (int)(totalLoggedInTime / (1000 * 60 * 60)) % 24;
        int min = (int)(totalLoggedInTime / (1000 * 60)) % 60;
        int sec = (int)(totalLoggedInTime / 1000) % 60;
        loggedInTimeFromApp = hr+":"+min+":"+sec;
        SmarterSMBApplication.totalLoggedInTime = String.valueOf(totalLoggedInTime);
        return loggedInTimeFromApp;
    }

    private String convertActiveMillisecondsToTime (long totalActiveTime){
        int hr = (int)(totalActiveTime / (1000 * 60 * 60)) % 24;
        int min = (int)(totalActiveTime / (1000 * 60)) % 60;
        int sec = (int)(totalActiveTime / 1000) % 60;
        activeTimeFromApp = hr+":"+min+":"+sec;
        SmarterSMBApplication.totalActiveTime = String.valueOf(totalActiveTime);
        return activeTimeFromApp;
    }

    public boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public void createSbiMakeCall(final Context context, final String number,final long dbid, final String appointmentId,final String status, final  String substatus1, final  String substatus2, final String callername, final String notes) {
        if(context != null) {
            if(checkPermission(this)) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager subscriptionManager = SubscriptionManager.from(activity);
                    List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                    if (subsInfoList != null) {
                        for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                            String number1 = subscriptionInfo.getNumber();
                            int SimStotNumber = subscriptionInfo.getSimSlotIndex();
                            if (SimStotNumber == 1) {

                            }
                        }
                    } else {
                        adhocCallEditText.setText("No SIM");
                        return;
                    }
                }

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setShape(GradientDrawable.RECTANGLE);
                        drawable.setStroke(2, getResources().getColor(R.color.light_gray));
                        adhocCallImageButton.setBackground(drawable);
                    }
                });

                if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                    if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                        if (number != null) {
                            String userNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                            if (userNumber != null && !(userNumber.isEmpty())) {
                                final String customernumber = number;
                                String sr_number = ApplicationSettings.getPref(AppConstants.SR_NUMBER, "");
                                String caller_id = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");

                                if (caller_id != null && !(caller_id.isEmpty())) {
                                    if (sr_number != null && !(sr_number.isEmpty())) {
                                        if (CommonUtils.isC2cNetworkAvailable(context)) {
                                            KnowlarityModel knowlarityModel = new KnowlarityModel(sr_number, userNumber, customernumber);
                                            knowlarityModel.setClient_id(caller_id);
                                            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                                            if (screen != null && (screen.equalsIgnoreCase("UearnActivity"))) {
                                                NotificationData.appointment_db_id = dbid;
                                                adhocCallEditText.setText("Calling.."+number);
                                                sbiCall(knowlarityModel, customernumber, status, context, substatus1, substatus2, callername, notes, appointmentId, dbid);
                                            }
                                        } else {
                                            adhocCallEditText.setText("No internet connection");
                                        }
                                    } else {
                                        adhocCallEditText.setText("No SR Number");
                                    }
                                } else {
                                    adhocCallEditText.setText("No Client ID");
                                }
                            } else {
                                adhocCallEditText.setText("Invalid User Number");
                            }
                        } else {
                            adhocCallEditText.setText("Invalid Customer Number");
                        }
                    }
                }
            }
        }
    }

    public void sbiCall(KnowlarityModel knowlarityModel, final String customernumber, final String status, final Context context, final String substatus1, final String substatus2, final String callername, final String notes, final String appointmentId, final Long dbid) {
        NotificationData.knolarity_start_time = new Date().toString();
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                NotificationData.knolarity_response_time = new Date().toString();
                if (data != null && !(data.isEmpty())) {
                    if (data.contains("_SMBALERT_")) {
                        if(SmarterSMBApplication.outgoingCallNotInStartMode){
                            String msg = data.replace("_SMBALERT_", "");
                            if(msg != null && !msg.isEmpty() && msg.contains("Invalid")) {
                                adhocCallEditText.setText("Invalid Number");
                            }
                        } else {
                            CommonUtils.setToast(activity,data.replace("_SMBALERT_", ""));
                        }
                    } else if (data.contains("_SMBACP_")) {
                        String uuidValue = data.replace("_SMBACP_", "");
                        try {
                            NotificationData.transactionId = uuidValue;
                            NotificationData.uuid = uuidValue;
                            NotificationData.knolarity_response = "success : ";
                            setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        storeUuidHash(customernumber,uuidValue);
                        CommonUtils.showACPScreen(activity);
                    } else {
                        try {
                            NotificationData.transactionId = data;
                            Log.d("NotificationTrans", NotificationData.transactionId);
                            NotificationData.uuid = data;
                            NotificationData.knolarity_response = "success : ";
                            setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        storeUuidHash(customernumber,data);
                    }

                } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                    adhocCallEditText.setText("Invalid Auth key");
                } else if (failure_code == APIProvider.INVALID_REQUEST) {
                    adhocCallEditText.setText("Invalid Request");
                } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                    adhocCallEditText.setText("Invalid Parameters");
                } else if (failure_code == APIProvider.INVALID_NUMBER) {
                    adhocCallEditText.setText("Invalid Number");
                } else if (failure_code == APIProvider.DND) {
                    adhocCallEditText.setText("DND Number");
                } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                    adhocCallEditText.setText("Agent not verified");
                } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                    adhocCallEditText.setText("Agent not registered");
                } else {
                    adhocCallEditText.setText("Request failed");
                }
            }
        }).reClickToCall(knowlarityModel);
    }

    public void storeUuidHash(String custNo, String uuidval) {
        HashMap<String, String> testHashMap = new HashMap<String, String>();
        testHashMap.put("customer", custNo);
        testHashMap.put("uuid",uuidval);
        Gson gson = new Gson();
        String hashMapString = gson.toJson(testHashMap);
        ApplicationSettings.putPref(AppConstants.CUSTOMER_UUID_MAP, hashMapString);
    }

    public static void setCallData(String appointmentId, String status, String substatus1, String substatus2, String callername, String notes) {
        NotificationData.notificationData = true;
        NotificationData.appointment_id = appointmentId;
        NotificationData.statusString = status;
        NotificationData.notes_string = notes;
        NotificationData.order_value = "0";
        NotificationData.isAppointment = true;
        String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
        if (screen != null && ((screen.equalsIgnoreCase("Auto2AfterCallActivity") || (screen.equalsIgnoreCase("Auto1AfterCallActivity"))))) {
            NotificationData.makeACall = true;
        }
        if(callername != null) {
            NotificationData.knolarity_name = callername;
        }
        if(substatus1 != null && status != null && !status.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus1 = substatus1;
        }
        if(substatus2 != null && status != null && !status.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus2 = substatus2;
        }
    }

    private void hideWebViewAndAdhocCallLayout() {
        if (ApplicationSettings.containsPref(AppConstants.WEB_LAYOUT)) {
            boolean webLayout = ApplicationSettings.getPref(AppConstants.WEB_LAYOUT, false);
            if (webLayout) {
                defaultMsgWebView.setVisibility(View.GONE);
            } else {
                adhocCallCardView.setVisibility(View.GONE);
                adhocCallViewLayout.setVisibility(View.GONE);
            }
        } else {
            adhocCallCardView.setVisibility(View.GONE);
            adhocCallViewLayout.setVisibility(View.GONE);
        }
    }

    private void showWebViewAndAdhocCallLayout() {
        if (ApplicationSettings.containsPref(AppConstants.WEB_LAYOUT)) {
            boolean webLayout = ApplicationSettings.getPref(AppConstants.WEB_LAYOUT, false);
            if (webLayout) {
                defaultMsgWebView.setVisibility(View.VISIBLE);
            } else {
                adhocCallCardView.setVisibility(View.VISIBLE);
                adhocCallViewLayout.setVisibility(View.VISIBLE);
            }
        } else {
            adhocCallCardView.setVisibility(View.VISIBLE);
            adhocCallViewLayout.setVisibility(View.VISIBLE);
        }
    }

    //===============SignOut==================================

    private void signOutAndExit() {
        boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
        if (truePredictive) {
            SmarterSMBApplication.triggerDataLoadingAfterSubmit = false;
        }
        SmarterSMBApplication.moveToRAD = false;
        SmarterSMBApplication.currentStateIsStartMode = false;
        SmarterSMBApplication.stayAtHomeScenario = false;
        String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
            SmarterSMBApplication.signoutNotificationReceived = false;
            File dir = new File(Environment.getExternalStorageDirectory() + "/callrecorder/");
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (ApplicationSettings.containsPref(AppConstants.SSU_ON_SIO)) {
                boolean ssuonsio = ApplicationSettings.getPref(AppConstants.SSU_ON_SIO, false);
                if (ssuonsio) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sendSalesEntriesInfoToServer();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            }

            Cursor cursor = null;
            if (ApplicationSettings.containsPref(AppConstants.UPLOAD_STATUS2)) {
                boolean uploadStatus2 = ApplicationSettings.getPref(AppConstants.UPLOAD_STATUS2, false);
                if (!uploadStatus2) {
                    cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=0", null);
                } else {
                    cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=2", null);
                }
            } else {
                cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=2", null);
            }

            if (cursor != null && cursor.getCount() > 0) {
                callReuploadService();
                checkInternalFiles();
            } else {
                String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                    SmarterSMBApplication.isRemoteDialledStart = false;
                    SmarterSMBApplication.isFirstCall = false;

                    if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
                        boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
                        if (systemControl) {
                            SmarterSMBApplication.disableStatusBarAndNavigation = false;
                        }
                    }

                    //Log.d("RemoteDialledStart","1655    " +SmarterSMBApplication.isRemoteDialledStart);
                    sendRemoteDialStopRequest("Taking a break");
                    if (truePredictive) {
                        //killCall(UearnProfileActivity.this);
                    }
                    new CustomTwoButtonDialog().exitApplicationDialog(UearnHome.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
                } else {
                    String selection = "UPLOAD_STATUS" + "=" + 0;
                    cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
                    if (dir.isDirectory()) {
                        String[] children = dir.list();
                        if ((children != null && children.length > 0) || (cursor != null && cursor.getCount() > 0)) {
                            callReuploadService();
                            checkInternalFiles();
                        } else {
                            new CustomTwoButtonDialog().exitApplicationDialog(UearnHome.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
                        }
                    } else {
                        new CustomTwoButtonDialog().exitApplicationDialog(UearnHome.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
                    }
                }
            }
        } else {
            new CustomTwoButtonDialog().exitApplicationDialog(UearnHome.this, "Sign out", "You will not get updates any more. Are you sure you want to logout?").show();
        }
    }

    private void sendSalesEntriesInfoToServer() {
        String salesStatusFromDb = getAllSalesStatusEntriesFromDB();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")) {
                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
            }
            jsonObject.put("salesstatusentries", salesStatusFromDb);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            DataUploadUtils.postSalesEntries(Urls.postSalesEntriesUrl(), jsonObject);
        }
    }

    private void callReuploadService() {
        Intent aIntent = new Intent(this, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(aIntent);
        } else {
            this.startService(aIntent);
        }
    }




    private void checkInternalFiles() {
        final Dialog dialog = CustomTwoButtonDialog.buildTwoButtonDialog(this, "Warning", "There is data to be uploaded, please don't sign out. Connect to internet & sync before you sign out.");
        TextView btnNo = dialog.findViewById(R.id.btn_no);
        btnNo.setText("OK");
        btnNo.setTextSize(10);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        TextView btnYes = dialog.findViewById(R.id.btn_yes);
        btnYes.setText("LOGOUT");
        btnYes.setTextSize(10);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new CustomTwoButtonDialog().exitApplicationDialog(UearnHome.this, "Sign out", "Your business activities will not be tracked any more. Are you sure you want to log out?").show();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                sendBroadcast(broadcastIntent);
            }
        });
        dialog.show();
    }

    private void manualDialStartButtonClickedCheckDialog() {

        try {

            final Dialog dialog = CustomSingleButtonDialog.buildAlertDialog("Info","You have not registered your presence for voice calls. Click Start to register and then proceed to initiate calls.", this, false);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            TextView btnYes = (TextView) dialog.findViewById(R.id.btn_yes);
            btnYes.setText("OK");
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });
            dialog.show();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void enableStartButton(){
        swipe_to_start.setEnabled(true);
        swipe_to_start.setClickable(true);
        swipe_to_start.setTextColor(activity.getResources().getColor(R.color.white));
        swipe_to_start.setBackgroundResource(R.drawable.green_rounded_corner);

        manual_swipe_to_start.setEnabled(true);
        manual_swipe_to_start.setClickable(true);
        manual_swipe_to_start.setTextColor(activity.getResources().getColor(R.color.white));
        manual_swipe_to_start.setBackgroundResource(R.drawable.green_rounded_corner);
    }

    private void disableStartButton(){
        swipe_to_start.setEnabled(false);
        swipe_to_start.setClickable(false);
        swipe_to_start.setTextColor(activity.getResources().getColor(R.color.white));
        swipe_to_start.setBackgroundResource(R.drawable.grey_rounded_corner);

        manual_swipe_to_start.setEnabled(false);
        manual_swipe_to_start.setClickable(false);
        manual_swipe_to_start.setTextColor(activity.getResources().getColor(R.color.white));
        manual_swipe_to_start.setBackgroundResource(R.drawable.grey_rounded_corner);
    }

    private void setStartButtonText() {
        if(CommonUtils.allowASF()){
            swipe_to_start.setText("START CALL NOW / BOOK SLOT");
            manual_swipe_to_start.setText("START CALL NOW / BOOK SLOT");
        } else {
            swipe_to_start.setText("START CALL");
            manual_swipe_to_start.setText("START CALL");
        }
    }

    private void setButtonTextAsStop() {
        swipe_to_start.setText("STOP");
        manual_swipe_to_start.setText("STOP");
    }

    private void setStartButtonVisibility(boolean visibility){
        if(visibility){
            swipe_to_start.setVisibility(View.VISIBLE);
            if (ApplicationSettings.containsPref(AppConstants.UNSCHEDULED_CALL)) {
                boolean schedulecall = ApplicationSettings.getPref(AppConstants.UNSCHEDULED_CALL, false);
                if (schedulecall) {
                    swipe_to_start.setVisibility(View.GONE);
                    manual_swipe_to_start.setVisibility(View.VISIBLE);
                    manualDial.show();
                }
            }
        } else {
            swipe_to_start.setVisibility(View.GONE);
            manual_swipe_to_start.setVisibility(View.GONE);
            manualDial.hide();
        }
    }

    private void setButtonLayoutVisibility(boolean visibility){
        if (ApplicationSettings.containsPref(AppConstants.UNSCHEDULED_CALL)) {
            boolean schedulecall = ApplicationSettings.getPref(AppConstants.UNSCHEDULED_CALL, false);
            if (schedulecall) {
                manual_dial_layout.setVisibility(View.VISIBLE);
                //acp_bottom_layout.setVisibility(View.GONE);
                swipe_to_start.setVisibility(View.GONE);
            } else {
                manual_dial_layout.setVisibility(View.GONE);
                if(visibility) {
                    swipe_to_start.setVisibility(View.VISIBLE);
                    //acp_bottom_layout.setVisibility(View.VISIBLE);
                } else {
                    swipe_to_start.setVisibility(View.GONE);
                    //acp_bottom_layout.setVisibility(View.GONE);
                }
            }
        } else {
            manual_dial_layout.setVisibility(View.GONE);
            if(visibility) {
                swipe_to_start.setVisibility(View.VISIBLE);
                //acp_bottom_layout.setVisibility(View.VISIBLE);
            } else {
                swipe_to_start.setVisibility(View.GONE);
                //acp_bottom_layout.setVisibility(View.GONE);
            }
        }
    }

    private boolean isSocketServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private Dialog exitOnclickOfBackButtonDialog(final Activity activity, String title, String message) {

        final Dialog exitDialog = buildTwoButtonDialog(activity, title, message);
        exitDialog.setCancelable(false);
        exitDialog.setCanceledOnTouchOutside(false);

        exitDialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        TextView btnNo = exitDialog.findViewById(R.id.btn_no);
        btnNo.setText("CANCEL");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exitDialog != null) {
                    exitDialog.dismiss();
                }
            }
        });

        TextView btnYes = exitDialog.findViewById(R.id.btn_yes);
        btnYes.setText("YES");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exitDialog != null) {
                    exitDialog.cancel();
                    if (remoteAutoEnabled.equals("on")) {

                    } else {
                        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {

                        }
                        isStart = false;
                        removeFragment();
                    }
                    ActivityCompat.finishAffinity(activity);
                }
            }
        });
        return exitDialog;
    }

    private void getCurrentNetworkType() {
        NetworkInfo info = getInfo();
        if (info == null || !info.isConnected()) {
            SmarterSMBApplication.currentNetworkType = "";
        }
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
            if (info.isConnected()) {
               SmarterSMBApplication.currentNetworkType = "WIFI";
            }
        } else if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (info.isConnected()) {
                SmarterSMBApplication.currentNetworkType = "MOBILE";
            }
        }
    }

    public NetworkInfo getInfo() {
        return ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }
}