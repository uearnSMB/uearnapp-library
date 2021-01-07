package smarter.uearn.money.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import smarter.uearn.money.fragments.SignUpFragment;
import smarter.uearn.money.models.AppStatus;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GoogleAccountsList;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.PersonalJunkContact;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.services.AcceptOrRejectNotificationService;
import smarter.uearn.money.R;
import smarter.uearn.money.services.SocketIOService;
import smarter.uearn.money.training.activity.TrainingDashboardActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIAdapter;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.GPSTracker;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;
import smarter.uearn.money.views.events.NetworkBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static smarter.uearn.money.utils.ServerAPIConnectors.Urls.getSioAddress;

public class DataLoadingActivity extends BaseActivity {

    public static int GET_TEAM_MEMBERS = 535;
    ProgressBar home_progress;
    String id = "";
    String dbPath = "";
    private TextView percentageTv;
    private ObjectAnimator waterLevelAnim, amplitudeAnim;
    private int percentage = 48;
    private Socket mSocket;
    static int attempts = 0, teamAttemps = 0, stageAttempts =0 , voipCallAttempts = 0, groupUserAttempts = 0;
    private boolean settingsCheck = false;
    private int backgroundIteration = 0;
    private SQLiteDatabase db;
    private String activityType = "";
    public boolean stagesCheck = false, teamCheck = false, voipNumberCheck = false, groupUserCheck = false;
    private boolean dbFetchedSuccessfully = false;
    private final int REQUEST_PERMISSION_CALL_PHONE=1;
    private boolean readPhoneCallLogs = false;
    private boolean phoneCall = false;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private Activity activity = null;
    public static ArrayList<Double> pingResponselist = null;
    public static String pingResponse = "";
    public static CountDownTimer pingTimer;
    public static boolean timerStopped = false;
    private static boolean pingInProgress  = false;

    private Emitter.Listener onDBFetchAvailable = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    byte[] img = (byte[]) args[0];
                    FileOutputStream outs = null;
                    dbFetchedSuccessfully = true;
                    //Log.d("SMB_DEBUG_LOGS", "DB Fetched successfully" + dbFetchedSuccessfully);
                    //Log.d("SMB_DEBUG_LOGS", "Response time after emit - onDBFetchAvailable" + System.currentTimeMillis());
                    //Log.d("SMB_DEBUG_LOGS", "Loading the DB to " + dbPath);
                    try {
                        outs = new FileOutputStream(dbPath);
                    } catch (IOException e) {
                        //Log.d("SMB_DEBUG_LOGS", "Exception " + e.toString());
                        backToLogin("DB loading failed, please check your WiFi / internet connection.");
                        DataLoadingActivity.this.finish();
                    }

                    BufferedOutputStream bos = new BufferedOutputStream(outs);
                    try {
                        bos.write(img);
                        bos.flush();
                        bos.close();

                        //Log.d("SMB_DEBUG_LOGS", "Loaded the DB to " + dbPath);
                        continueWithOtherDBData();
                    } catch (IOException e) {
                        //Log.d("SMB_DEBUG_LOGS", "Exception " + e.toString());
                        backToLogin("DB loading failed, please check your WiFi / internet connection.");
                        DataLoadingActivity.this.finish();
                    }
                }
            });
        }
    };

    private Emitter.Listener onRtdNack = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.d("SMB_DEBUG_LOGS", "onRtdNack " + "Unable to fetch data! please try it again.");
                    Toast.makeText(DataLoadingActivity.this, "Unable to fetch data! please try it again.", Toast.LENGTH_SHORT).show();
                    backToLogin("Unable to fetch data!, please check your WiFi / internet connection.");
                    DataLoadingActivity.this.finish();
                }
            });
        }
    };

    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -7);
            Date date = cal.getTime();
            String startTime = CommonUtils.getTimeFormatInISO(date);
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, +30);
            Date endDate = cal.getTime();
            String endTime = CommonUtils.getTimeFormatInISO(endDate);
            //Log.d("SMB_DEBUG_LOGS", "Start_time " + startTime);
            //Log.d("SMB_DEBUG_LOGS", "EndTime " + endTime);
            if (mSocket != null && mSocket.connected()) {
                try {
                    if (!id.isEmpty()) {
                        //Log.d("SMB_DEBUG_LOGS", "Request time before emit - onConnected" + System.currentTimeMillis());
                        //Log.d("SMB_DEBUG_LOGS", "Requesting DB for " + id);

                        runOnUiThread( new Runnable(){
                            public void run(){
                                //Log.d("SMB_DEBUG_LOGS", "START TIMER - runOnUiThread()");
                                startTimer();
                            }
                        });

                        mSocket.emit("getdb", id, startTime, endTime);
                    }
                } catch (Exception e) {

                }
            }
        }
    };

    public void clearId() {
        id = "";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_loading);
        activity = this;
        initUi();
        getSupportActionBar().hide();
        changeStatusBarColor(this);

        SmarterSMBApplication.currentActivity = this;

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Log.d("DataReload", "Reload data in DataLoadingActivity");

        disconnectSocketAndSIOURL();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle((Html.fromHtml("Uearn")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303F9F")));
        SmarterSMBApplication.appSettings.setLogout(false);
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        db = dbHelper.getWritableDatabase();

        home_progress = (ProgressBar) findViewById(R.id.home_progress);

        ApplicationSettings.putPref(AppConstants.TRANSCRIPTION, false);
        ApplicationSettings.putPref(AppConstants.LEAD_CONTACTS, "" + "");
        new SignUpFragment().newUserPreferences();
        if (getIntent().hasExtra("EXTRA_DB_PATH")) {
            dbPath = getIntent().getStringExtra("EXTRA_DB_PATH");
        } else {
            Toast.makeText(this, "User DB path does not exist", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (getIntent().hasExtra("EXTRA_ID")) {
            id = getIntent().getStringExtra("EXTRA_ID");
        } else {
            Toast.makeText(this, "Something went wrong please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }

        networkBroadcastReceiver= new NetworkBroadcastReceiver();
        try {
            if (networkBroadcastReceiver != null) {
                IntentFilter networkBroadcastFilter = new IntentFilter();
                networkBroadcastFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                registerReceiver(networkBroadcastReceiver, networkBroadcastFilter);
            }
        } catch (Exception e) {
            // already registered
        }

        if (!(getIntent().hasExtra("EXTRA_BOOTABLE"))) {
            //killCall(this);
            CommonOperations.subscribeNotificationsCall(true, this);
            getStatusFromServer(id);
            activityType = "signin";
        } else {
            CheckPendingTransaction();
            activityType = "poweron";
        }

        runOnUiThread(new Runnable() {
            public void run() {
                home_progress.setProgress(10);
                percentageTv.setText("10%");
            }
        });
        //Log.d("DataReload", "Reload data in DataLoadingActivity 10%");
    }

    public static void killCall(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
            methodGetITelephony.setAccessible(true);
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
            Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            methodEndCall.invoke(telephonyInterface);
            Log.d("DataReload", "SmarterSMBApplication - killCall()");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void CheckPendingTransaction() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        File dir = new File(Environment.getExternalStorageDirectory() + "/callrecorder/");
        String selection3 = "UPLOAD_STATUS" + "=" + 0;
        Cursor cursor3 = db.query("remindertbNew", null, selection3, null, null, null, "START_TIME ASC");

        if (dir.isDirectory()) {
            String[] children = dir.list();
            if ((children != null && children.length > 0) || (cursor3 != null && cursor3.getCount() > 0)) {
                cursor3.close();
                createCounter();
            } else {
                deleteAllLocalDB();
                getStatusFromServer(id);
            }
        } else {
            deleteAllLocalDB();
            getStatusFromServer(id);
        }
    }

    public String deleteAllLocalDB() {
        dbPath = db.getPath();
        try {
            db.delete("mytbl", null, null);
            db.delete("ameyo_entries", null, null);
            db.delete("reminderSubjectTbl", null, null);
            db.delete("remindertbNew", null, null);
            db.delete("AmeyoReminderTable", null, null);
            db.delete("SmarterContact", null, null);
            db.delete("WorkOrder", null, null);
            db.delete("TeamMembers", null, null);
            db.delete("Ameyo_Push_Notifications", null, null);
            db.delete("FirstCall", null, null);
            db.delete("PrivateContacts", null, null);
            db.delete("JunkNumbers", null, null);
            db.delete("CallData", null, null);
        } catch (Exception e) {

        } finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
        return dbPath;
    }

    public void continueWithOtherDBData() {
        if (mSocket != null && mSocket.connected()) {
            try {
                mSocket.off("dbfile", onDBFetchAvailable);
                mSocket.off("connect", onConnected);
                mSocket.disconnect();
            } catch (Exception e) {

            }
        }

        home_progress.setProgress(90);
        percentageTv.setText("90%");
        getTeamMembersCall(id, this);

//        if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
//            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
//            if (screen != null && !screen.equalsIgnoreCase("UearnActivity")) {
//                CommonUtils.calculateNextAlarm(this);
//            }
//        } else {
//            CommonUtils.calculateNextAlarm(this);
//        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        String syncDate = "" + df.format(c.getTime());
        long millis = df.getCalendar().getTimeInMillis();

        if (syncDate != null) {
            ApplicationSettings.putPref(AppConstants.SYNC_FOLLOWUP_DATE, syncDate);
            ApplicationSettings.putPref(AppConstants.DATA_START_TIME, millis);
        }
    }

    private void initUi() {
        percentageTv = (TextView) findViewById(R.id.percentageTv);
        attempts = 0; teamAttemps = 0; stageAttempts =0; voipCallAttempts = 0; groupUserAttempts = 0;
    }

    public boolean getCalendarEvents(final Context context, final String calendarType, final int countForNextList, int start, int end) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Date date = cal.getTime();
        String startTime = CommonUtils.getTimeFormatInISO(date);
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, +30);
        Date endDate = cal.getTime();
        final String endTime = CommonUtils.getTimeFormatInISO(endDate);

        final int end1 = end;
        final int start1 = end;
        if (CommonUtils.isNetworkAvailable(context)) {
            GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(SmarterSMBApplication.SmartUser.getId(), startTime, endTime, calendarType);
            getCalendarEntryInfo.limit = countForNextList;
            Log.e("DataLoading",":: Get_CalendarEvents ::");
            new APIProvider.Get_CalendarEvents(getCalendarEntryInfo, AppConstants.GET_CAL_EVENTS, new API_Response_Listener<ArrayList<GetCalendarEntryInfo>>() {
                @Override
                public void onComplete(ArrayList<GetCalendarEntryInfo> data, long request_code, int failure_code) {
                    Log.e("DataLoading",":: Get_CalendarEvents Response ::");
                    if (data != null && !data.isEmpty()) {
                        percentage = percentage + 2;
                        if (percentage < 100) {
                            home_progress.setProgress(percentage);
                            percentageTv.setText("" + percentage + "%");
                        }
                        getCalendarEvents(getApplicationContext(), calendarType, countForNextList + 100, end1, start1);
                        CommonUtils.savingGoogleCalendarDataToLocalDB(getApplicationContext(), data);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                home_progress.setProgress(98);
                                percentageTv.setText("98%");
                            }
                        });
                        CommonUtils.calculateNextAlarm(getApplicationContext());
                    }
                }
            }).call();
        }
        return true;
    }

    private void getStatusFromServer(final String id) {
        if (CommonUtils.isNetworkAvailable(this)) {
            Log.e("DataLoading",":: Get_Sales_Status ::");
            new APIProvider.Get_Sales_Status(id, 22, new API_Response_Listener<SalesStageInfo>() {
                @Override
                public void onComplete(SalesStageInfo data, long request_code, int failure_code) {
                    Log.e("DataLoading",":: Get_Sales_Status Response ::");
                    if (data != null) {
                        stageAttempts = 4;
                        data.dosave();
                        SmarterSMBApplication.salesStageInfo = data;
                    } else {
                        stageAttempts = stageAttempts + 1;
                        if (stageAttempts < 3) {
                            getStatusFromServer(id);
                        } else if(stageAttempts == 3) {
                            stagesCheck = true;
                        }
                    }
                    if(stagesCheck) {
                        backToLogin("Failed to sync, please check your WiFi / internet connection or contact your administrator.");
                        DataLoadingActivity.this.finish();
                    } else if (stageAttempts >= 3) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                home_progress.setProgress(20);
                                percentageTv.setText("20%");
                            }
                        });
                        getGroupsOfUsers(id);
                    }

                }
            }).call();
        }
    }

    private void getGroupsOfUsers(final String user_id) {
        if (CommonUtils.isNetworkAvailable(this)) {
            Log.e("DataLoading",":: Get_GroupsUser ::");
            new APIProvider.Get_GroupsUser(user_id, 1, null, null, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                @Override
                public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                    if (data != null) {
                        Log.e("DataLoading",":: Get_GroupsUser Response ::");
                        groupUserAttempts = 4;
                        ArrayList<GroupsUserInfo> groupsUserInfos = new ArrayList<GroupsUserInfo>();
                        setDataToEngineerListOrSupervisor(data);
                        for (int i = 0; i < data.size(); i++) {
                            String request = data.get(i).request;
                            if ((request != null) && request.equals("invited")) {
                                groupsUserInfos.add(data.get(i));
                                Intent intent = new Intent(getApplicationContext(), AcceptOrRejectNotificationService.class);
                                Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(data.get(i));
                                intent.putExtra("groupsUserInfoBundle", bundle);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    getApplicationContext().startForegroundService(intent);
                                } else {
                                    getApplicationContext().startService(intent);
                                }
                            }
                        }
                        AppConstants.GROUPS_DATA = groupsUserInfos;
                    } else {
                        groupUserAttempts = groupUserAttempts + 1;
                        if (groupUserAttempts < 3) {
                            getGroupsOfUsers(id);
                        } else if(groupUserAttempts == 3) {
                            groupUserCheck = true;
                        }
                    }
                    if(groupUserCheck) {
                        backToLogin("Failed to sync, please check your WiFi / internet connection or contact your administrator.");
                        DataLoadingActivity.this.finish();
                    } else if (groupUserAttempts >= 3) {

                        ServiceUserProfile.getUsersTeam(DataLoadingActivity.this);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                home_progress.setProgress(40);
                                percentageTv.setText("40%");
                            }
                        });
                        settingsApi(user_id);
                    }
                }
            }).call();
        }
    }

    private void getTeamMembersCall(final String id, final Context context) {
        if (CommonUtils.isNetworkAvailable(this)) {
            Log.e("DataLoading",":: Get_All_Team_Members ::");
            new APIProvider.Get_All_Team_Members(id, GET_TEAM_MEMBERS, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                @Override
                public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                    if (data != null) {
                        Log.e("DataLoading",":: Get_All_Team_Members Response ::");
                        teamAttemps = 4;
                        CommonUtils.saveTeamdataToDB(context, data);
                        for (int i = 0; i < data.size(); i++) {
                            String request = data.get(i).request;
                            Log.e("DataLoading","request api :: "+request);
                            if ((request != null) && request.equals("invited")) {
                                Intent intent = new Intent(DataLoadingActivity.this, AcceptOrRejectNotificationService.class);
                                Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(data.get(i));
                                intent.putExtra("groupsUserInfoBundle", bundle);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(intent);
                                } else {
                                    startService(intent);
                                }
                            }
                        }
                    } else {
                        teamAttemps = teamAttemps + 1;
                        if (teamAttemps < 3) {
                            getTeamMembersCall(id, DataLoadingActivity.this);
                        } else if(teamAttemps == 3){
                            teamCheck = true;
                        }
                    }
                    if(teamCheck) {
                        backToLogin("Failed to sync, please check your WiFi / internet connection or contact your administrator.");
                        DataLoadingActivity.this.finish();
                    } else if (teamAttemps >= 3) {
                        home_progress.setProgress(95);
                        percentageTv.setText("95%");
                        postAppStatus(activityType);
                    }
                }
            }).call();
        }
    }

    private void requestForData() {
        String data = getAppointmentCountAndLeadSource();
        sendRequestForData(data);
    }

    private void postAppStatus(String activityType) {
        String userId = "";
        double latitude = 0;
        double longitude = 0;
        if (ApplicationSettings.containsPref(AppConstants.USERINFO_ID)) {
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            }
        }
        boolean gpsSettings = ApplicationSettings.getPref(AppConstants.GPS_SETTINGS, false);
        boolean isRecording = ApplicationSettings.getPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
        int version_code = CommonUtils.getVersionCode(DataLoadingActivity.this);
        String versionNumber = "" + version_code;
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }
        String timeStamp = CommonUtils.getTimeFormatInISO(new java.sql.Date(System.currentTimeMillis()));
        if ((userId != null) && (timeStamp != null) && (activityType != null) && (versionNumber != null)) {
            AppStatus appstatus = new AppStatus(activityType, "" + gpsSettings, "" + isRecording, "" + 0, userId, timeStamp, versionNumber, latitude, longitude);
            if (CommonUtils.isNetworkAvailable(this)) {
                Log.e("DataLoading",":: PostAppStatus ::");
                new APIProvider.PostAppStatus(appstatus, 0, new API_Response_Listener<AppStatus>() {
                    @Override
                    public void onComplete(AppStatus data, long request_code, int failure_code) {
                        Log.e("DataLoading",":: PostAppStatus Response ::");
                        landIngScreen();
                    }
                }).call();
            } else {
                landIngScreen();
            }
        } else {
            landIngScreen();
        }
    }

    private void landIngScreen() {

        sendMySettingsInfoToServer();

        ApplicationSettings.putPref(AppConstants.SHOW_POPUP, true);
        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, false);

        runOnUiThread(new Runnable() {
            public void run() {
                home_progress.setProgress(100);
                percentageTv.setText("100%");
            }
        });

//        runOnUiThread(new Runnable(){
//            @Override
//            public void run() {
//                requestForData();
//            }
//        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DataLoadingActivity.this.finish();

                if(SmarterSMBApplication.signoutNotificationReceived) {
                    ApplicationSettings.putPref(AppConstants.APP_LOGOUT, true);
                    SmarterSMBApplication.signoutNotificationReceived = false;
                } else {
                    ApplicationSettings.putPref(AppConstants.APP_INT_TRANSACTION_COUNTER, 0);
                    String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
                    if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
                        if (ApplicationSettings.containsPref(AppConstants.SIO_REFRESH_TIME)) {
                            String sioRefreshTime = ApplicationSettings.getPref(AppConstants.SIO_REFRESH_TIME, "");
                            if (sioRefreshTime != null && !sioRefreshTime.isEmpty()) {
                                //Log.d("SocketIOTest", "Socket IO Call startService");
                                Intent myService = new Intent(activity, SocketIOService.class);
                                startService(myService);
                            }
                        }
                        Intent dashboard = new Intent(DataLoadingActivity.this, UearnHome.class);
                        dashboard.putExtra("FIRST_TIME", true);
                        dashboard.putExtra("SETTINGS", settingsCheck);
                        dashboard.putExtra("SETTINGSDATA", false);
                        dashboard.putExtra("SETTINGSTEAM", false);
                        startActivity(dashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | dashboard.FLAG_ACTIVITY_CLEAR_TASK));
                        if (ApplicationSettings.containsPref(AppConstants.CALLWAITING_CHOICE)) {
                            boolean callWaitingChoice = ApplicationSettings.getPref(AppConstants.CALLWAITING_CHOICE, false);
                            if (callWaitingChoice) {
                                String ussdCode = "#43#";
                                if (ApplicationSettings.containsPref(AppConstants.DEACTIVATE_USSD_CODE)) {
                                    String deactivateCode = ApplicationSettings.getPref(AppConstants.DEACTIVATE_USSD_CODE, "");
                                    if (deactivateCode != null && !deactivateCode.isEmpty()) {
                                        ussdCode = deactivateCode;
                                    }
                                }
                                startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + Uri.encode(ussdCode))));
                            }
                        }
                        /*Log.e("UearnHome","userStatus ::: "+userStatus);
                        Intent dashboard = new Intent(getApplicationContext(), TrainingDashboardActivity.class);
                        startActivity(dashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | dashboard.FLAG_ACTIVITY_CLEAR_TASK));*/
                    }
                    else if(userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("Training"))) {
                        Intent training = new Intent(DataLoadingActivity.this, TrainingDashboardActivity.class);
                        training.putExtra("FIRST_TIME", true);
                        training.putExtra("SETTINGS", settingsCheck);
                        training.putExtra("SETTINGSDATA", false);
                        training.putExtra("SETTINGSTEAM", false);
                        startActivity(training.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | training.FLAG_ACTIVITY_CLEAR_TASK));

                    }
                }

            }
        }, 1000);
    }

    @SuppressLint("MissingPermission")
    private void getVoipNumber() {

        boolean permissionCheck = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean phoneCallPermission = showPhoneCallPermission();
            if(phoneCallPermission) {
                permissionCheck = CommonUtils.permissionsCheck(this);
            } else {
                permissionCheck = CommonUtils.permissionsCheck(this);
            }
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            boolean readCallLogs = showReadCallLogPermission();
//            if(readCallLogs) {
//                permissionCheck = CommonUtils.permissionsCheck(this);
//            } else {
//                permissionCheck = CommonUtils.permissionsCheck(this);
//            }
//        }

        else {
            permissionCheck = CommonUtils.permissionsCheck(this);
        }

        if (permissionCheck) {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String[] input = new String[2];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                input[0] =  UUID.randomUUID().toString();
            } else {
                input[0] = tMgr.getSimSerialNumber();
            }
            input[1] = id;
            if (CommonUtils.isNetworkAvailable(this)) {
                Log.e("DataLoading",":: GetUser ::");
                new APIProvider.GetUser(input, 0l, new API_Response_Listener<String[]>() {
                    @Override
                    public void onComplete(String data[], long request_code, int failure_code) {
                        if (data != null) {
                            Log.e("DataLoading",":: GetUser Response ::");
                            if(data[1] != null && !data[1].isEmpty() && data[1].equalsIgnoreCase("signout")) {
                                if(data[2] != null) {
                                    backToLogin(data[2]);
                                }
                                DataLoadingActivity.this.finish();
                            } else {
                                voipCallAttempts = 4;
                                voipNumberCheck = false;
                            }
                        } else {
                            voipCallAttempts = voipCallAttempts + 1;
                            if (voipCallAttempts < 3) {
                                getVoipNumber();
                            } else if(voipCallAttempts == 3){
                                voipNumberCheck = true;
                            }
                        }
                        if(voipNumberCheck) {
                            backToLogin("Failed to verify your SIM details, please check your internet connection.");
                            DataLoadingActivity.this.finish();
                        } else if (voipCallAttempts >= 3) {
                            home_progress.setProgress(75);
                            percentageTv.setText("75%");
                            fetchDbFromServer();
                        }
                    }
                }).call();
            }
        } else {
            backToLogin("Failed to read your SIM, please check \n 1. SIM is inserted correctly. \n 2. Check for SIM permissions in Settings > App > UEARN > Permissions");
            DataLoadingActivity.this.finish();
        }
    }

    private boolean showPhoneCallPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.CALL_PHONE, REQUEST_PERMISSION_CALL_PHONE);
            } else {
                requestPermission(Manifest.permission.CALL_PHONE, REQUEST_PERMISSION_CALL_PHONE);
            }
        } else {
            phoneCall = true;
            //Toast.makeText(DataLoadingActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
        }
        return phoneCall;
    }




    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }


    private void fetchDbFromServer() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;

            mSocket = IO.socket(getSioAddress(),opts);
            mSocket.on("dbfile", onDBFetchAvailable);
            mSocket.on("connect", onConnected);
            mSocket.on("dbfile-nack", onRtdNack);
            try {
                mSocket.connect();
            } catch (Exception e) {
                Toast.makeText(this, "Please check your internet connection. ", Toast.LENGTH_SHORT).show();
                backToLogin("DB not downloaded, please check your WiFi / internet connection.");
                finish();
            }
        } catch (java.net.URISyntaxException e) {
            Toast.makeText(this, "DB Not downloaded", Toast.LENGTH_SHORT).show();
            backToLogin("DB not downloaded, please check your WiFi / internet connection.");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (db.isOpen()) {
                db.close();
            }
            if (mSocket != null && mSocket.connected()) {
                mSocket.off("dbfile", onDBFetchAvailable);
                mSocket.off("connect", onConnected);
                mSocket.disconnect();
            }
            if (networkBroadcastReceiver != null) {
                unregisterReceiver(networkBroadcastReceiver);
                networkBroadcastReceiver = null;
            }
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    private void settingsApi(final String userId) {
        if (CommonUtils.isNetworkAvailable(this)) {
            Log.e("DataLoading",":: GetSettings ::");
            new APIProvider.GetSettings(userId, 22, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null && !(data.isEmpty())) {
                        Log.e("DataLoading",":: GetSettings Response ::");
                        attempts = 4;
                    } else {
                        attempts = attempts + 1;
                        if (attempts < 3) {
                            settingsApi(userId);
                        } else if(attempts == 3) {
                            settingsCheck = true;
                        }
                    }
                    if(settingsCheck) {
                        backToLogin("Failed to sync, please check your WiFi / internet connection or contact your administrator.");
                        DataLoadingActivity.this.finish();
                    } else if (attempts >= 3) {
                        if(ApplicationSettings.containsPref(AppConstants.USER_ROLE) && ApplicationSettings.getPref(AppConstants.USER_ROLE, "") != null) {
                            String role = ApplicationSettings.getPref(AppConstants.USER_ROLE,"");
                            if (!role.equalsIgnoreCase("user") || !role.equalsIgnoreCase("non-user")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        home_progress.setProgress(75);
                                        percentageTv.setText("75%");
                                    }
                                });
                                fetchDbFromServer();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        home_progress.setProgress(50);
                                        percentageTv.setText("50%");
                                    }
                                });
                                getVoipNumber();
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    home_progress.setProgress(75);
                                    percentageTv.setText("75%");
                                }
                            });
                            fetchDbFromServer();
                        }

                    }
                }
            }).call();
        }
    }

    private void setDataToEngineerListOrSupervisor(ArrayList<GroupsUserInfo> data) {
        GroupsUserInfo supervisorData;
        for (int i = 0; i < data.size(); i++) {
            GroupsUserInfo groupsUserInfo = data.get(i);
            if (!groupsUserInfo.status.equals("deleted")) {
                if (!groupsUserInfo.dependent_type.equals("engineer")) {
                    supervisorData = groupsUserInfo;
                    if (supervisorData != null && supervisorData.email != null) {
                        ApplicationSettings.putPref(AppConstants.SUPERVISOR, supervisorData.email);
                    }
                }
            }
        }
    }

    private void createCounter() {
        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, true);
        Intent intent = new Intent(DataLoadingActivity.this, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        Long counter = (long) 1000;
        new CountDownTimer(counter, 1000) {
            public void onTick(long millis) {

            }
            public void onFinish() {
                CheckPendingTransaction();
            }
        }.start();
    }

    private void backToLogin(String msg) {
        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, true);
        Intent loginIntent = new Intent(DataLoadingActivity.this, SmarterAuthActivity.class);
        loginIntent.putExtra("Error_message", msg);
        loginIntent.putExtra("screenType", 0);
        startActivity(loginIntent);
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
            requestForDataObj.put("activitytype", "SIGN IN");
            requestForDataObj.put("requestfordata", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonData = requestForDataObj.toString();
        System.out.print(jsonData);
        return jsonData;
    }

    private void sendRequestForData(String message) {
        if (CommonUtils.isNetworkAvailable(this)) {
            if(activity != null && !activity.isFinishing()) {
                Log.e("DataLoading",":: GetRequestForData ::");
                new APIProvider.GetRequestForData(message, 0, activity, "Sending request for data. Please wait..", new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        Log.e("DataLoading",":: GetRequestForData Response ::");
                        if (data != null && !data.isEmpty()) {
                        } else {
                            backToLogin("No response - get request for data, please check your WiFi / internet connection.");
                            DataLoadingActivity.this.finish();
                        }
                    }
                }).requestForDataCall();
            }
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            backToLogin("Failed to get request for data, please check your WiFi / internet connection or contact your administrator.");
            DataLoadingActivity.this.finish();
        }
    }

    private void disconnectSocketAndSIOURL() {
        if(SmarterSMBApplication.pushSioUrl != null) {
            SmarterSMBApplication.pushSioUrl = null;
        }

        if(SmarterSMBApplication.mSocket != null) {
            SmarterSMBApplication.mSocket.disconnect();
        }
    }

    private static void sendMySettingsInfoToServer(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SmarterSMBApplication.getInstance().getApplicationContext());
        Map<String,?> keys = prefs.getAll();

        final JSONArray settingsArray = new JSONArray();

        if(keys != null && !keys.isEmpty()) {
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                //Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                try {
                    JSONObject mySettingsObj = new JSONObject();
                    mySettingsObj.put("key", entry.getKey());
                    mySettingsObj.put("value", entry.getValue().toString());
                    settingsArray.put(mySettingsObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //System.out.print(settingsArray);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")){
                            jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
                        }
                        jsonObject.put("my_settings", settingsArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    DataUploadUtils.postMySettings(Urls.postMySettings(), jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void pingAndCheckWifiConnectivityStatus() {
        String str = "";
        pingResponse = "";
        pingResponselist = new ArrayList<Double>();
        if(!pingInProgress) {
            startPingTimer();
        }
    }

    private void startPingTimer() {
        pingTimer = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
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
            }
            public void onFinish() {
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
                    if (str != null && !str.isEmpty()) {
                        str = str.replaceAll("\\s", "");
                        str = Utils.getNumericString(str);
                        int result = Integer.parseInt(str);
                        if (result > 0 && result < 50) {
                            //Good
                            backToLogin("Failed to fetch DB, please contact tech support.");
                            DataLoadingActivity.this.finish();


                        } else if (result >= 50 && result <= 100) {
                            //Average
                            backToLogin("Failed to fetch DB, please contact tech support.");
                            DataLoadingActivity.this.finish();

                        } else if (result >= 100 && result <= 500) {
                            //Week
                            backToLogin("Failed to fetch DB, please check your WiFi / internet connection.");
                            DataLoadingActivity.this.finish();
                            //connectionStatus.setText(getResources().getString(R.string.connection_weak_msg));

                        } else if (result > 500) {
                            backToLogin("Failed to fetch DB, please check your WiFi / internet connection.");
                            DataLoadingActivity.this.finish();
                            //connectionStatus.setText(getResources().getString(R.string.connection_poor_msg));
                        }
                    } else {
                        //No internet
                        backToLogin("Failed to fetch DB, please check your WiFi / internet connection.");
                        DataLoadingActivity.this.finish();
                        //connectionStatus.setText(getResources().getString(R.string.no_connection_msg));
                    }
                } else {
                    //Good
                    backToLogin("Failed to fetch DB, please contact tech support.");
                    DataLoadingActivity.this.finish();

                    //connectionStatus.setText(getResources().getString(R.string.connection_good_msg));
                }
            }
        }.start();
    }

    private void sendPingRequestToCheckNetworkSpeed() {
        if (CommonUtils.isNetworkAvailable(this)) {
            pingAndCheckWifiConnectivityStatus();
        } else {

        }
    }

    private void startTimer() {
        new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Log.d("SMB_DEBUG_LOGS", "CountDownTimer: " + "    " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if(dbFetchedSuccessfully){
                    //Log.d("SMB_DEBUG_LOGS", "DB fetched successfully from backend");
                } else {
                    //Log.d("SMB_DEBUG_LOGS", "Failed to fetch DB, please check your WiFi / internet connection.");

//                    backToLogin("Failed to fetch DB, please check your WiFi / internet connection.");
//                    DataLoadingActivity.this.finish();

                    sendPingRequestToCheckNetworkSpeed();
                }
            }
        }.start();
    }
}
