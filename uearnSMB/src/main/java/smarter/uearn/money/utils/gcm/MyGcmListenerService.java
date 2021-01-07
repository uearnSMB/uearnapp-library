package smarter.uearn.money.utils.gcm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.WebStorage;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import smarter.uearn.money.activities.AfterCallActivity;
import smarter.uearn.money.activities.MyAlertDialog;
import smarter.uearn.money.activities.MyStopAlertDialog;
import smarter.uearn.money.activities.SmarterAuthActivity;
import smarter.uearn.money.activities.SplashScreenActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.broadcastReceiver.Alarm_Receiver;
import smarter.uearn.money.broadcastReceiver.DailyAlarmReceiver;
import smarter.uearn.money.callrecord.RecorderService;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.HomeScreenMail;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.models.WorkOrderEntryInfo;
import smarter.uearn.money.notificationmessages.activity.NotificationMessageActivity;
import smarter.uearn.money.services.AcceptOrRejectNotificationService;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ResponseInterface;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.MyJsonObject;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.webservice.Constants;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;
import smarter.uearn.money.utils.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static smarter.uearn.money.utils.SmarterSMBApplication.connectToPushServer;

/**
 * Created by kavya on 12/9/2015.
 *
 * @Modified Srinath k.
 */
public class MyGcmListenerService extends GcmListenerService {

    String appointmentId = "";
    private final int NOTIFICATION_ID = 237;
    public static int value = 0;
    HomeScreenMail mail = new HomeScreenMail();
    String source = "", sender = "", offlineUrl = "";
    String start_time, end_time;
    Long start = 0L, end = 0L;
    SQLiteDatabase db;
    private String deleted = "";
    private ArrayList<String> deleteList = new ArrayList<>();
    private String posturl = "", getUrl = "";
    private String agentStatus = "";

    Handler handler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {
            // Toast.makeText(getBaseContext(), msg.getData().getString("message"), Toast.LENGTH_LONG).show();
            //if (AppConstants.RESET_DEVICE || AppConstants.KILL_APP) { //App reload
            if (AppConstants.KILL_APP) {
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), UearnHome.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        getApplicationContext().startActivity(intent);
                        //                           ((Activity) getApplicationContext()).overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                    }
                }, 1000L);
            }
        }
    };

    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pushNotifData = intent.getStringExtra("pushnotifdata");
            String pushNotifMessage = intent.getStringExtra("pushnotifmessage");
            Log.d("SIOTEST", "onReceive" +"Message" + pushNotifMessage +"Data" + pushNotifData);
            Bundle pushBundle = new Bundle();
            pushBundle.putString("info", pushNotifData);
            pushBundle.putString("message", pushNotifMessage);
            onMessageReceived("",pushBundle);
        }
    };

    @Override
    public void onCreate() {
        String pushReceiverRegistrationStatus = ApplicationSettings.getPref(AppConstants.PUSHRECEIVER_REGISTRATION_STATUS, "");
        if (pushReceiverRegistrationStatus == null || pushReceiverRegistrationStatus.isEmpty()) {
            Log.d("SIOTEST", "pushReceiverRegistered");
            try {
                if (mPushReceiver != null) {
                    LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver, new IntentFilter("smb-sio-push-event"));
                    ApplicationSettings.putPref(AppConstants.PUSHRECEIVER_REGISTRATION_STATUS, "ON");
                }
            } catch (Exception e) {

            }
        }
    }

    private void sendMessageToLocalBroadcastReceiver(String message) {
        Log.d("PushNotification", "sendMessageToLocalBroadcastReceiver "+message);
        Intent intent = new Intent("connected-customer-push-event");
        intent.putExtra("connectedcustomer",message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMessageToSmbHome(String message) {
        Log.d("PushNotification", "sendMessageToSmbHome");
        Intent intent = new Intent("remote-dialer-request-event");
        intent.putExtra("result", message);
        Log.d("SMBHOMETEST", "sendMessageToSmbHome " + message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMessageToSmbHome(String message, String getUrl, String ttsEnable, String showCancel) {
        Log.d("PushNotification", "sendMessageToSmbHome");
        Intent intent = new Intent("remote-dialer-request-event");
        intent.putExtra("geturl", getUrl);
        intent.putExtra("ttsenable", ttsEnable);
        intent.putExtra("showcancel", showCancel);
        intent.putExtra("result", message);
        Log.d("SMBHOMETEST", "sendMessageToSmbHome " + message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private boolean shouldRecord() {
        return ApplicationSettings.getPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
    }

    private void startRecording() {
        if (shouldRecord()) {
            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
            Log.d("SMBHOME", "connectedCustomer OUTSIDE" + connectedCustomer);
            if (connectedCustomer != null && !connectedCustomer.isEmpty()) {
                ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, connectedCustomer);
                Log.d("SMBHOME", "Starting service for recording of" + connectedCustomer);
                Intent recorderIntent = new Intent(this, RecorderService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(recorderIntent);
                } else {
                    startService(recorderIntent);
                }
            }
        }
    }

    private void showACPScreen() {
        String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
        String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"");

        if (shouldRecord()) {
            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
            Log.d("SMBHOME", "connectedCustomer OUTSIDE" + connectedCustomer);
            if (connectedCustomer != null && !connectedCustomer.isEmpty()) {
                ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, connectedCustomer);
                Log.d("SMBHOME", "Starting service for recording of" + connectedCustomer);
                Intent recorderIntent = new Intent(this, RecorderService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(recorderIntent);
                } else {
                    startService(recorderIntent);
                }
            }
        }

        if(screen != null && !screen.isEmpty()) {
            String activityToStart = "smarter.uearn.money.activities." + screen;
            Class<?> c = null;
            try {
                c = Class.forName(activityToStart);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            final Class<?> finalc = c;

            Intent intent = new Intent(this, finalc);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if(qanda != null && !qanda.isEmpty()) {
                intent.putExtra("showinprogress", 1);
            }

            startActivity(intent);
        }


    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("PushNotification", "************In Message Received*********");
        //Log.d("PushNotification", "IN on message received Message is ***** " + data.getString("message"));
        //Log.d("PushNotification", "INfo is " + data.getString("info"));

        if(data != null && !data.isEmpty()) {
            String myserverurlInfo = data.getString("info");
            try {
                JSONObject myserverurlInfoObj = new JSONObject(myserverurlInfo);
                String myserverurlInfoData = "";
                if (myserverurlInfoObj.has("data")) {
                    myserverurlInfoData = myserverurlInfoObj.getString("data");
                    if (myserverurlInfoData != null && !myserverurlInfoData.isEmpty()) {
                        JSONObject infoJsonObject = new JSONObject(myserverurlInfoData);
                        if (infoJsonObject != null && infoJsonObject.has("serverurl")) {
                            String serverURL = infoJsonObject.getString("serverurl");
                            if (serverURL != null && !serverURL.isEmpty()) {
                                ApplicationSettings.putPref(AppConstants.MY_SERVER_URL, serverURL);
                                Urls.SERVER_ADDRESS = serverURL;
//                            Handler handler = new Handler(Looper.getMainLooper());
//                            handler.post(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "New server added", Toast.LENGTH_SHORT).show();
//                                }
//                            });
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);

        if (!isLogout) {
            try {
                JSONObject jsonObject = new JSONObject(data.getString("info"));

                if (jsonObject.has("response")) {
                    String responsedata = jsonObject.getString("response");
                    JSONObject responseJson = new JSONObject(responsedata);
                    String valuedata = responseJson.getString("value");
                    JSONArray valueJsonArray = new JSONArray(valuedata);
                    for (int i = 0; i < valueJsonArray.length(); i++) {
                        JSONObject dataJson = new JSONObject(valueJsonArray.get(i).toString());
                        String resource = dataJson.getString("resourceData");
                        JSONObject resourceJson = new JSONObject(resource);
                        String appointment_id = resourceJson.getString("id");
                        if (appointment_id != null) {
                            getExternalCalendar(appointment_id);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String message = data.getString("message");

            Thread t = new Thread() {
                public void run() {
                    Looper.prepare();
                    Message myMessage = handler.obtainMessage();
                    Bundle resBundle = new Bundle();
                    resBundle.putString("message", message);
                    myMessage.setData(resBundle);
                    handler.sendMessage(myMessage);
                }
            };
            t.start();

            if (message.equalsIgnoreCase("created salesstages")) {
                buildBasicNotification("Settings", "Your settings have been changed");
                ServiceUserProfile.getUserSettings();
            }

            if (data.containsKey("info")) {
                String information = data.getString("info");
                try {
                    JSONObject jsonObject = new JSONObject(information);
                    if (jsonObject.has("id")) {
                        appointmentId = jsonObject.getString("id");
                    }
                    if (jsonObject.has("idlist")) {
                        String idList = jsonObject.getString("idlist");
                        JSONArray array = new JSONArray(idList);
                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject1 = new JSONObject(array.getString(i));
                                deleteList.add(jsonObject1.getString("id"));
                            }
                        }
                    }
                    if (jsonObject.has("responsestatus")) {
                        deleted = jsonObject.getString("responsestatus");
                    }
                    if (jsonObject.has("source")) {
                        source = jsonObject.getString("source");

                    }
                    if (jsonObject.has("sender")) {
                        sender = jsonObject.getString("sender");
                    }
                    if(jsonObject.has("geturl")) {
                        offlineUrl = jsonObject.getString("geturl");
                    }
                    if(jsonObject.has("start_time")) {
                        start_time = jsonObject.getString("start_time");
                    }
                    if(jsonObject.has("end_time")) {
                        end_time = jsonObject.getString("end_time");
                    }

                    if(jsonObject.has("posturl")) {
                        posturl = jsonObject.getString("posturl");
                    }

                    if(jsonObject.has("geturl")) {
                        getUrl = jsonObject.getString("geturl");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (message != null) {
                if (message.equals("new appointment")) {
                    value++;

                }
                if (message.equals("new appointment") || message.equals("updated appointment")) {
                    if (appointmentId != null) {
                        if (message.equals("updated appointment") && deleted != null && deleted.equalsIgnoreCase("deleted")) {
                            //TODO:: bulk delete
                            if (deleteList != null && deleteList.size() > 0) {
                                for (int i = 0; i < deleteList.size(); i++) {
                                    deleteLocalFollowupRecord(deleteList.get(i));
                                }
                            } else {
                                deleteLocalFollowupRecord(appointmentId);
                            }
                        } else {
                            //TODO: Commented for Testing::: Srinath.k
                            getCalendarEvent(appointmentId, message);
                        }
                    }
                } else if (message.equals("workorder created")) {
                    getWorkorderEvent(appointmentId);
                }
                //TODO: change the message based on the server send message
                else if (message.equals("created salesstages") || message.equals("updated salesstages")) {
                    updateSalesStageFunnel();
                } else if (message.equals("group updated")) {
                    refreshTeamListForAssignTo();
                    updateSalesStageFunnel();
                } else if (message.equals("teammember request")) {
                    String information = data.getString("info");
                    GroupsUserInfo groupsUserInfo = getGroupsUserInfo(information);
                    groupsUserInfo.dependent_type = "engineer";
                    if (groupsUserInfo != null) {
                        ArrayList<GroupsUserInfo> groupsUserInfos = new ArrayList<GroupsUserInfo>();
                        groupsUserInfos.add(groupsUserInfo);
                        AppConstants.GROUPS_DATA = groupsUserInfos;
                        acceptOrRejectTeamMember(groupsUserInfo);
                    }
                } else if (message.equals("supervisor request")) {
                    String information = data.getString("info");
                    GroupsUserInfo groupsUserInfo = getGroupsUserInfo(information);
                    groupsUserInfo.dependent_type = "supervisor";
                    if (groupsUserInfo != null) {
                        ArrayList<GroupsUserInfo> groupsUserInfos = new ArrayList<GroupsUserInfo>();
                        groupsUserInfos.add(groupsUserInfo);
                        AppConstants.GROUPS_DATA = groupsUserInfos;
                        acceptOrRejectTeamMember(groupsUserInfo);
                    }
                } else if (message.equals("customer number mapping to IVR")) {
                    String info = data.getString("info");
                    String customer_number = "", type = "", agentMessage = "", agentName = "";
                    long start_time_value = 0;
                    try {
                        MyJsonObject jsonObject = new MyJsonObject(info);
                        String data1 = jsonObject.getString("data");
                        JSONObject infoJsonObject = new JSONObject(data1);
                        customer_number = infoJsonObject.getString("customer_number");
                        type = infoJsonObject.getString("type");
                        if (type != null && (type.equals("missed_call_attended") || type.equals("missed_call_assigned"))) {
                            if (infoJsonObject.has("agent")) {
                                String agentString = infoJsonObject.getString("agent");
                                if (agentString != null) {
                                    MyJsonObject jsonObject1 = new MyJsonObject(agentString);
                                    agentMessage = jsonObject1.getString("message");
                                    agentName = jsonObject1.getString("name");
                                    mail.setMissedCallAgent(agentName);
                                    if (customer_number != null && (agentMessage != null))
                                        displayNotification(customer_number, agentMessage);
                                }
                            }
                        }
                        String start_time = infoJsonObject.getString("start_time");
                        if (start_time != null) {
                            MyJsonObject start_time_json = new MyJsonObject(start_time);
                            String dateTime = start_time_json.getString("dateTime");
                            if (dateTime != null) {
                                start_time_value = CommonUtils.stringToMilliSec(dateTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    saveDetailsTOAmeyoPushNotificationTable(getApplicationContext(), customer_number, start_time_value, type, agentMessage);
                } else if (message.equals("bulk appointment")) {
                    if (appointmentId != null) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(data.getString("info"));
                            String firstId = jsonObject1.getString("firstid");
                            String lastId = jsonObject1.getString("lastid");
                            MySql dbHelper = MySql.getInstance(getApplicationContext());
                            db = dbHelper.getWritableDatabase();
                            Date date = new Date();
                            Calendar c = Calendar.getInstance();
                            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
                            start = c.getTimeInMillis();
                            start_time = CommonUtils.getTimeFormatInISO(c.getTime());
                            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
                            end = c.getTimeInMillis();
                            end_time = CommonUtils.getTimeFormatInISO(c.getTime());

                            if (firstId.equalsIgnoreCase(lastId)) {
                                getCalendarEvent(firstId, "new appointment");
                            } else {
                                if ((firstId != null) && (lastId != null)) {
                                    getSmartUserCalendarDataFromServer(start_time, end_time, 0, firstId, lastId);
                                } else {
                                    getSmartUserCalendarDataFromServer(start_time, end_time, 0);
                                }
                            }

                            if (source != null) {
                                if (source.equals("Excel")) {
                                    displayOrUpdateNotification();
                                } else {
                                    if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                                        if (!ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "").equals(sender)) {
                                            displayOrUpdateNotification();
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (message.equals("broadcast message")) { // Used for indicating another sign in
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        if (data1 != null && !(data1.isEmpty())) {
                            displayOrUpdateNotification(data1);
                        }
                    } catch (JSONException e) {

                    }
                } else if (message.equals("system alert")) { // Used for indicating another sign in
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
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
                            NotificationData.outboundDialledCustomerNumber = customerNumber;
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

                            ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, customerName);
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customerNumber);
                            startRecording();
                        }

                        String sign_out = "";
                        if(infoJsonObject.has("sign_out")) {
                            sign_out = infoJsonObject.getString("sign_out");
                        }

                        if (alert_message != null && !(alert_message.isEmpty())) {
                            Log.e("GCMListener","dialog :: "+alert_message);
                            Intent intent4 = new Intent(this, MyStopAlertDialog.class);
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
                            if(offlineUrl != null && !offlineUrl.isEmpty()) {
                                intent4.putExtra("GETURL", offlineUrl);
                            }
                            startActivity(intent4);
                        }

                    } catch (JSONException e) {
                        //Log.d("Exception in broadCast", e.toString());
                    }
                } else if (message.equalsIgnoreCase("cloud missed")) {
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        if (data1 != null && !(data1.isEmpty())) {
                            displayOrUpdateNotification(data1);
                        }
                    } catch (JSONException e) {

                    }
                } else if (message.equalsIgnoreCase("version message")) {

                    String versionNumber = data.getString("version_number");
                    if (versionNumber != null) {
                        if (!versionNumber.isEmpty()) {
                            ApplicationSettings.putPref(AppConstants.APP_UPDATE_NUMBER,
                                    Integer.parseInt(versionNumber));
                        }
                    }
                } else if (message.equalsIgnoreCase("rad message")) {
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        JSONObject infoJsonObject = new JSONObject(data1);

                        String alert_message = infoJsonObject.getString("message");
                        ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, alert_message);

                        sendMessageToSmbHome(alert_message);

                    } catch (JSONException e) {

                    }
                }else if (message.equalsIgnoreCase("rad stop")) {
                    String information = data.getString("info");
                    String messageString = "", titleString = "",actionString = "", stopMessageString = "";
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        JSONObject infoJsonObject = new JSONObject(data1);

                        if (infoJsonObject.has("message")) {
                            messageString = infoJsonObject.getString("message");
                        }
                        if (infoJsonObject.has("title")) {
                            titleString = infoJsonObject.getString("title");
                        }
                        if (infoJsonObject.has("stopmessage")) {
                            stopMessageString = infoJsonObject.getString("stopmessage");
                        }
                        if(infoJsonObject.has("action")){
                            actionString = infoJsonObject.getString("action");
                        }

                        SmarterSMBApplication.moveToRADStop(messageString, titleString, actionString, stopMessageString);

                    } catch (JSONException e) {

                    }
                }
                else if (message.equalsIgnoreCase("connected customer")) {

                    String customerNumber = "";
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        if (data1 != null && !(data1.isEmpty())) {
                            customerNumber = data1;
                            if (customerNumber != null) {
                                if (!customerNumber.isEmpty()) {
                                    sendMessageToLocalBroadcastReceiver(customerNumber);
                                }
                            }
                        }
                    } catch (JSONException e) {

                    }

                } else if (message.equalsIgnoreCase("customer alert")) {
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        JSONObject infoJsonObject = new JSONObject(data1);

                        String alert_message = infoJsonObject.getString("message");
                        String alert_title = infoJsonObject.getString("title");
                        String tts_enable = infoJsonObject.getString("tts_enable");
                        String customerNumber = infoJsonObject.getString("customer_number");
                        String customerName = infoJsonObject.getString("customer_name");

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

                            ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, customerName);
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customerNumber);
                        }

                        String showcancel = "";
                        if(infoJsonObject.has("showcancel")) {
                            showcancel = infoJsonObject.getString("showcancel");
                        }

                        if(infoJsonObject.has("showaftercall")) {
                            ApplicationSettings.putPref(AppConstants.SHOW_AFTER_CALL_POPUP, true);
                            showACPScreen();
                        } else {
                            if (alert_message != null && !(alert_message.isEmpty())) {
                                sendMessageToSmbHome(alert_message, getUrl, tts_enable, showcancel);
                            }
                        }

                    } catch (JSONException e) {

                    }

                } else if (message.equals("signout message")) {

                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }

                        String displayMessage = "You have signed in from another device. You are automatically signed out from here.";
                        if (data1 != null && !(data1.isEmpty())) {
                            displayMessage = data1;
                        }
                        displayOrUpdateNotification(displayMessage);
                        signOutAndShowDialog();

                    } catch (JSONException e) {

                    }
                } else if (message.equals("sync settings")) {
                    ServiceUserProfile.getUsersTeam();
                    String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    String[] settingsParam = {Constants.GET, Constants.DOMAIN_URL + Constants.SETTINGS_STAGES + userId};
                    CommonUtils.checkToken();
                    WebService webCall = new WebService(7, new ResponseInterface() {
                        @Override
                        public boolean getResponseStatus(boolean result) {
                            return true;
                        }
                    });
                    webCall.execute(settingsParam);
                } else if (message.equals("sync appointments")) {
                    Calendar c = Calendar.getInstance();
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
                    start = c.getTimeInMillis();
                    start_time = CommonUtils.getTimeFormatInISO(c.getTime());
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
                    end = c.getTimeInMillis();
                    end_time = CommonUtils.getTimeFormatInISO(c.getTime());

                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        JSONObject infoJsonObject = new JSONObject(data1);

                        if (infoJsonObject.has("start_time")) {
                            start_time = infoJsonObject.getString("start_time");
                        }
                        if (infoJsonObject.has("end_time")) {
                            end_time = infoJsonObject.getString("end_time");
                        }
                    } catch (JSONException e) {

                    }
                    //Log.d("CommonUtils"," start "+start_time+" end "+end_time);
                    getSmartUserCalendarDataFromServer(start_time, end_time, 0);
                } else if (message.equalsIgnoreCase("go offline")) {
                    if(offlineUrl != null && !offlineUrl.isEmpty()) {
                        offlineUrl = offlineUrl.replaceAll("\\/","/");
                        callToServerGetApi(offlineUrl, "offline", 0);
                    }
                } else if (message.equalsIgnoreCase("go online")) {
                    if(offlineUrl != null && !offlineUrl.isEmpty()) {
                        offlineUrl = offlineUrl.replaceAll("\\/","/");
                        callToServerGetApi(offlineUrl, "online", 0);
                    }
                } else if(message.equalsIgnoreCase("start sync")) {
                    getSmartUserCalendarDataFromServer(start_time, end_time, 0);
                } else if(message.equalsIgnoreCase("reset application")) {
                    // Don't delete commented code it can be used later

//                    if(CommonUtils.isNetworkAvailable(getApplicationContext())) {
//                        Log.d("DataReload", "Reload data in reset application");
//                        AppConstants.RESET_DEVICE = true;
//                    }
//                    boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
//                    if(truePredictive) {
//                        if (SmarterSMBApplication.connectedCustomerState != null && !SmarterSMBApplication.connectedCustomerState.isEmpty() && !SmarterSMBApplication.connectedCustomerState.equalsIgnoreCase("Logged Out")) {
//                            //Log.d("DataReloading","SmarterSMBApplication.connectedCustomerState IF" + SmarterSMBApplication.connectedCustomerState);
//                            SmarterSMBApplication.triggerDataLoadingAfterSubmit = true;
//                        } else {
//                            //Log.d("DataReloading","SmarterSMBApplication.startDataLoadingActivity() ELSE" + SmarterSMBApplication.connectedCustomerState);
//                            SmarterSMBApplication.startDataLoadingActivity();
//                        }
//                    } else {
//                        SmarterSMBApplication.startDataLoadingActivity();
//                    }
                    if(SmarterSMBApplication.isCampaignOver){
                        SmarterSMBApplication.isCampaignOver = false;
                    }
                    SmarterSMBApplication.startDataLoadingActivity();
                }else if(message.equalsIgnoreCase("agent status")) {
                    //SmarterSMBApplication.newMessageReceived();
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        JSONObject infoJsonObject = new JSONObject(data1);

                        if (infoJsonObject.has("message")) {
                            agentStatus = infoJsonObject.getString("message");
                        }
                    } catch (JSONException e) {

                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ApplicationSettings.putPref(AppConstants.USER_STATUS, agentStatus);
                                String activityToStart = SmarterSMBApplication.getNextActivityName();
                                Class<?> cl = null;
                                try {
                                    cl = Class.forName(activityToStart);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                                final Class<?> landingClass = cl;

                                Intent landingIntent = new Intent(SmarterSMBApplication.applicationContext, landingClass);
                                landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                SmarterSMBApplication.applicationContext.startActivity(landingIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else if(message.equalsIgnoreCase("notification alert")) {
                    if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                        boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                        if (fkControl) {
                            SmarterSMBApplication.notificationAlertReceived = true;
                        }
                    }
                    String information = data.getString("info");
                    String messageString = "", titleString = "",actionString = "";
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        JSONObject infoJsonObject = new JSONObject(data1);

                        if (infoJsonObject.has("message")) {
                            messageString = infoJsonObject.getString("message");
                        }
                        if (infoJsonObject.has("title")) {
                            titleString = infoJsonObject.getString("title");
                        }
                        if(infoJsonObject.has("action")){
                            actionString = infoJsonObject.getString("action");
                        }
                    } catch (JSONException e) {

                    }
                    String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
                    if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
                        SmarterSMBApplication.newMessageReceived(messageString, titleString, actionString);
                    }else{
                        SmarterSMBApplication.newTrainerMessageReceived(messageString, titleString, actionString);
                    }
                    displayOrUpdateNotification(titleString);
                }
                else if(message.equalsIgnoreCase("start logging")) {
                    AppConstants.POSTCALLOG = true;
                    AppConstants.POSTCALLOGURL = posturl;
                } else if(message.equalsIgnoreCase("stop logging")) {
                    AppConstants.POSTCALLOG = false;
                    AppConstants.POSTCALLOGURL = "";
                    if(getUrl != null && !getUrl.isEmpty()) {
                        callToServerGetApi(getUrl, "", 1);
                    }
                } else if(message.equalsIgnoreCase("kill app")) {
                    AppConstants.KILL_APP = true;
                } else if (message.equalsIgnoreCase("customer info")) {
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        if (jsonObject.has("data")) {

                        }
                    } catch (JSONException e) {

                    }
                } else if(message.equalsIgnoreCase("register pushreceiver")) {
                    String pushReceiverRegistrationStatus = ApplicationSettings.getPref(AppConstants.PUSHRECEIVER_REGISTRATION_STATUS, "");
                    if (pushReceiverRegistrationStatus == null || pushReceiverRegistrationStatus.isEmpty()) {
                        //Log.d("SIOTEST", "pushReceiverRegistered");
                        try {
                            if (mPushReceiver != null) {
                                LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver, new IntentFilter("smb-sio-push-event"));
                                ApplicationSettings.putPref(AppConstants.PUSHRECEIVER_REGISTRATION_STATUS, "ON");
                            }
                        } catch (Exception e) {

                        }
                    }
                } else if(message.equalsIgnoreCase("start pushsocket")) {
                    connectToPushServer();
                } else if(message.equalsIgnoreCase("get dbentries")) {
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        JSONObject infoJsonObject = new JSONObject(data1);
                        String query = "";
                        if (infoJsonObject.has("query")) {
                            query = infoJsonObject.getString("query");
                        }
                        if (query != null && !query.isEmpty()) {
                            final String queryString = query;
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try  {
                                        if (queryString != null && !queryString.isEmpty()) {
                                            SmarterSMBApplication.sendDBEntriesInfoToServer(queryString);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        }
                    } catch (JSONException e) {

                    }
                } else if (message.equals("customer disconnected")) {
                    //Log.d("TruePredictiveTest", "Inside customer disconnected");
                    // Used for indicating another sign in
                    String information = data.getString("info");
                    try {
                        JSONObject jsonObject = new JSONObject(information);
                        String data1 = "";
                        if (jsonObject.has("data")) {
                            data1 = jsonObject.getString("data");
                        }
                        JSONObject infoJsonObject = new JSONObject(data1);

                        String customerNumber = "";
                        if (infoJsonObject.has("customer_number")) {
                            customerNumber = infoJsonObject.getString("customer_number");
                        }

                        customerNumber = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
                        if (customerNumber != null && !customerNumber.isEmpty()) {
                            boolean statusConnected = ApplicationSettings.getPref(AppConstants.STATUS_CONNECTED, false);
                            //Log.d("TruePredictiveTest", "statusConnected" + statusConnected);
                            String disConnectedCustomer = ApplicationSettings.getPref(AppConstants.CUSTOMER_DISCONNECT, "");
                            //Log.d("TruePredictiveTest", "disConnectedCustomer" + disConnectedCustomer);
                            if (statusConnected) {
                                if (disConnectedCustomer != null && !disConnectedCustomer.isEmpty()) {
                                    //Log.d("TruePredictiveTest", "Customer disconnected by Agent");
                                } else {
                                    //Log.d("TruePredictiveTest", "Customer disconnected" + customerNumber);
                                    NotificationData.source = "Customer disconnected";
                                    if (NotificationData.dialledCustomerNumber == null || NotificationData.dialledCustomerNumber.isEmpty()) {
                                        NotificationData.dialledCustomerNumber = customerNumber;
                                    }
                                    SmarterSMBApplication.sendCallDisconnectInfoToServer(NotificationData.dialledCustomerNumber, NotificationData.source);
                                    ApplicationSettings.putPref(AppConstants.CUSTOMER_DISCONNECT, customerNumber);
                                    SmarterSMBApplication.stopRecording(customerNumber);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        //Log.d("Exception in broadCast", e.toString());
                    }
                }
            }
        } else {

            final String message = data.getString("message");

            if(message.equalsIgnoreCase("notification alert")) {
                if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                    boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                    if (fkControl) {
                        SmarterSMBApplication.notificationAlertReceived = true;
                    }
                }
                String information = data.getString("info");
                String messageString = "", titleString = "",actionString = "";
                try {
                    JSONObject jsonObject = new JSONObject(information);
                    String data1 = "";
                    if (jsonObject.has("data")) {
                        data1 = jsonObject.getString("data");
                    }
                    JSONObject infoJsonObject = new JSONObject(data1);

                    if (infoJsonObject.has("message")) {
                        messageString = infoJsonObject.getString("message");
                    }
                    if (infoJsonObject.has("title")) {
                        titleString = infoJsonObject.getString("title");
                    }
                    if(infoJsonObject.has("action")){
                        actionString = infoJsonObject.getString("action");
                    }
                } catch (JSONException e) {

                }
                String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
                if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
                    SmarterSMBApplication.newMessageReceived(messageString, titleString, actionString);
                }else{
                    SmarterSMBApplication.newTrainerMessageReceived(messageString, titleString, actionString);
                }
                displayOrUpdateNotification(titleString);
            } else if (message.equals("system alert")) { // Used for indicating another sign in
                String information = data.getString("info");
                try {
                    JSONObject jsonObject = new JSONObject(information);
                    String data1 = "";
                    if (jsonObject.has("data")) {
                        data1 = jsonObject.getString("data");
                    }

                    JSONObject infoJsonObject = new JSONObject(data1);

                    String alert_message = infoJsonObject.getString("message");
                    String alert_title = infoJsonObject.getString("title");
                    String tts_enable = infoJsonObject.getString("tts_enable");
                    String sign_in = "";
                    if (infoJsonObject.has("sign_in")) {
                        sign_in = infoJsonObject.getString("sign_in");
                    }

                    isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);

                    if (!isLogout) {
                        if (alert_message != null && !(alert_message.isEmpty())) {
                            Log.e("GCMListener","dialog :: 23 ");
                            Intent intent4 = new Intent(this, MyAlertDialog.class);
                            intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent4.putExtra("MESSAGE", alert_message);
                            intent4.putExtra("TTSENABLE", tts_enable);
                            intent4.putExtra("TITLE", alert_title);
                            intent4.putExtra("SIGNIN", sign_in);
                            if (offlineUrl != null && !offlineUrl.isEmpty()) {
                                intent4.putExtra("GETURL", offlineUrl);
                            }
                            startActivity(intent4);
                        }
                    }

                } catch (JSONException e) {

                }
            } else if(message.equalsIgnoreCase("register pushreceiver")) {
                String pushReceiverRegistrationStatus = ApplicationSettings.getPref(AppConstants.PUSHRECEIVER_REGISTRATION_STATUS, "");
                if (pushReceiverRegistrationStatus == null || pushReceiverRegistrationStatus.isEmpty()) {
                    //Log.d("SIOTEST", "pushReceiverRegistered");
                    try {
                        if (mPushReceiver != null) {
                            LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver, new IntentFilter("smb-sio-push-event"));
                            ApplicationSettings.putPref(AppConstants.PUSHRECEIVER_REGISTRATION_STATUS, "ON");
                        }
                    } catch (Exception e) {

                    }
                }
            } else if(message.equalsIgnoreCase("start pushsocket")) {
                connectToPushServer();
            } else if(message.equalsIgnoreCase("get dbentries")) {
                String information = data.getString("info");
                try {
                    JSONObject jsonObject = new JSONObject(information);
                    String data1 = "";
                    if (jsonObject.has("data")) {
                        data1 = jsonObject.getString("data");
                    }
                    JSONObject infoJsonObject = new JSONObject(data1);
                    String query = "";
                    if (infoJsonObject.has("query")) {
                        query = infoJsonObject.getString("query");
                    }
                    if (query != null && !query.isEmpty()) {
                        final String queryString = query;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try  {
                                    if (queryString != null && !queryString.isEmpty()) {
                                        SmarterSMBApplication.sendDBEntriesInfoToServer(queryString);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    }
                } catch (JSONException e) {

                }
            } else if (message.equals("customer disconnected")) {
                //Log.d("TruePredictiveTest", "Inside customer disconnected");
                // Used for indicating another sign in
                String information = data.getString("info");
                try {
                    JSONObject jsonObject = new JSONObject(information);
                    String data1 = "";
                    if (jsonObject.has("data")) {
                        data1 = jsonObject.getString("data");
                    }
                    JSONObject infoJsonObject = new JSONObject(data1);

                    String customerNumber = "";
                    if (infoJsonObject.has("customer_number")) {
                        customerNumber = infoJsonObject.getString("customer_number");
                    }

                    customerNumber = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
                    if (customerNumber != null && !customerNumber.isEmpty()) {
                        boolean statusConnected = ApplicationSettings.getPref(AppConstants.STATUS_CONNECTED, false);
                        //Log.d("TruePredictiveTest", "statusConnected" + statusConnected);
                        String disConnectedCustomer = ApplicationSettings.getPref(AppConstants.CUSTOMER_DISCONNECT, "");
                        //Log.d("TruePredictiveTest", "disConnectedCustomer" + disConnectedCustomer);
                        if (statusConnected) {
                            if (disConnectedCustomer != null && !disConnectedCustomer.isEmpty()) {
                                //Log.d("TruePredictiveTest", "Customer disconnected by Agent");
                            } else {
                                //Log.d("TruePredictiveTest", "Customer disconnected" + customerNumber);
                                NotificationData.source = "Customer disconnected";
                                if (NotificationData.dialledCustomerNumber == null || NotificationData.dialledCustomerNumber.isEmpty()) {
                                    NotificationData.dialledCustomerNumber = customerNumber;
                                }
                                SmarterSMBApplication.sendCallDisconnectInfoToServer(NotificationData.dialledCustomerNumber, NotificationData.source);
                                ApplicationSettings.putPref(AppConstants.CUSTOMER_DISCONNECT, customerNumber);
                                SmarterSMBApplication.stopRecording(customerNumber);
                            }
                        }
                    }
                } catch (JSONException e) {
                    //Log.d("Exception in broadCast", e.toString());
                }
            }
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


    private void getSmartUserCalendarDataFromServer(final String startTime, final String endTime, final int nextgetcount, String firstId, String lastId) {
        final GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(SmarterSMBApplication.SmartUser.getId(), startTime, endTime, "smartercalendar", firstId, lastId);
        getCalendarEntryInfo.limit = nextgetcount;
        new APIProvider.Get_CalendarEvents(getCalendarEntryInfo, AppConstants.GET_CAL_EVENTS, new API_Response_Listener<ArrayList<GetCalendarEntryInfo>>() {
            @Override
            public void onComplete(ArrayList<GetCalendarEntryInfo> data, long request_code, int failure_code) {
                if (data != null && !data.isEmpty()) {
                    //new SaveToLocalDB(data, nextgetcount + 50).execute();
                    new SaveToLocalDB(data, nextgetcount + 100).execute();
                }
            }
        }).call();
    }

    private void getSmartUserCalendarDataFromServer(final String startTime, final String endTime, final int nextgetcount) {
        final GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(SmarterSMBApplication.SmartUser.getId(), startTime, endTime, "smartercalendar");
        getCalendarEntryInfo.limit = nextgetcount;
        new APIProvider.Get_CalendarEvents(getCalendarEntryInfo, AppConstants.GET_CAL_EVENTS, new API_Response_Listener<ArrayList<GetCalendarEntryInfo>>() {
            @Override
            public void onComplete(ArrayList<GetCalendarEntryInfo> data, long request_code, int failure_code) {
                if (data != null && !data.isEmpty()) {
                    //new SaveToLocalDB(data, nextgetcount + 50).execute();
                    new SaveToLocalDB(data, nextgetcount + 100).execute();
                }
            }
        }).call();
    }

    class SaveToLocalDB extends AsyncTask<ArrayList<GetCalendarEntryInfo>, Void, Void> {
        ArrayList<GetCalendarEntryInfo> getCalendarEntryInfos;
        int nextgetcount;

        public SaveToLocalDB(ArrayList<GetCalendarEntryInfo> getCalendarEntryInfos, int nextCount) {
            if (getCalendarEntryInfos.size() != 0) {
                //Log.d("data", "test" + getCalendarEntryInfos.get(0).getCaller_number());
            }
            this.getCalendarEntryInfos = getCalendarEntryInfos;
            this.nextgetcount = nextCount;
        }

        @Override
        protected Void doInBackground(ArrayList<GetCalendarEntryInfo>... params) {
            CommonUtils.saveCalendarDataToLocalDB(db, start, end, getApplicationContext(), getCalendarEntryInfos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getSmartUserCalendarDataFromServer(start_time, end_time, nextgetcount);
            super.onPostExecute(aVoid);
        }
    }

    private void getCalendarEvent(String appointmentId, final String typeOfAppointment) {
        new APIProvider.Get_CalendarEvent(appointmentId, 1, new API_Response_Listener<GetCalendarEntryInfo>() {
            @Override
            public void onComplete(GetCalendarEntryInfo data, long request_code, int failure_code) {
                if (data != null) {
                    value++;
                    //Log.d("Value : ", "" + value);
                    if (typeOfAppointment.equals("new appointment")) {
                        if (source != null) {
                            if (source.equals("Excel")) {
                                displayOrUpdateNotification();
                            } else {
                                if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                                    if (!ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "").equals(sender)) {
                                        displayOrUpdateNotification();
                                    }
                                }
                            }
                        }
                    }

                    if (typeOfAppointment.equals("new appointment")) {
                        //Log.d("update appointment", "updating appointment");
                        CommonUtils.saveCalendarEventToLocalDB(getApplicationContext(), data, "app");
                    } else if (typeOfAppointment.equals("updated appointment")) {
                        //Log.d("update appointment", "updating appointment");
                        CommonUtils.updateAppointmentInLocalDB(getApplicationContext(), data, "app");
                    }
                }
            }
        }).call();
    }

    private void deleteLocalFollowupRecord(String appointmentId) {
        //Log.d("MYGCM", "Deleted_followup_ID"+appointmentId);
        MySql dbHelper = MySql.getInstance(getApplicationContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM remindertbNew where APPOINTMENT_ID=" + "'" + appointmentId + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            database.delete("remindertbNew", "APPOINTMENT_ID='" + appointmentId + "'", null);
            //Log.d("MYGCM", "Deleted_followup_ID "+appointmentId+" deleted");
        }
        database.close();
    }

    private void getWorkorderEvent(String appointmentId) {
        new APIProvider.Get_WorkOrderEvent(appointmentId, 2, new API_Response_Listener<WorkOrderEntryInfo>() {
            @Override
            public void onComplete(WorkOrderEntryInfo data, long request_code, int failure_code) {
                CommonUtils.saveWorkOrderToLocalDB(getApplicationContext(), data);
            }
        }).call();
    }

    private void getExternalCalendar(String appointmentId) {
        new APIProvider.Get_External_Calendar_Event(appointmentId, 2, new API_Response_Listener<GetCalendarEntryInfo>() {
            @Override
            public void onComplete(GetCalendarEntryInfo data, long request_code, int failure_code) {
                if (data != null) {
                    CommonUtils.saveCalendarEventToLocalDB(getApplicationContext(), data, "externalCalendar");
                }
            }
        }).call();
    }

    private void displayOrUpdateNotification() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        long start = c.getTimeInMillis();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
        long end = c.getTimeInMillis();
        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        Intent appointmentList;
        if (!logout) {
            //appointmentList = new Intent(getApplicationContext(), New_AppointmentList.class);
            appointmentList = new Intent(getApplicationContext(), UearnHome.class);
            appointmentList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            appointmentList.putExtra("appointment", true);
            appointmentList.putExtra("start", start);
            appointmentList.putExtra("end", end);
            appointmentList.putExtra("appointment", true);
            appointmentList.putExtra("start_time", "");
            appointmentList.putExtra("end_time", "");
            appointmentList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            /*appointmentList = new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
            appointmentList = new Intent(this, SmarterAuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appointmentList.putExtra("screenType", 0);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, appointmentList, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //RemoteViews remoteView = new RemoteViews("smarter.uearn.money", R.layout.gcm_notification);
        //remoteView.setTextViewText(R.id.appointments, "You have " + value + " new follow-ups sent by ");
        //remoteView.setTextViewText(R.id.reminderSender, sender);

        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.small_logo)
                .setColor(Color.parseColor("#dd374c"))
                .setContentTitle("You have" + " new follow-ups sent by ")
                .setContentText(sender)
                //.addExtras(bundle)
                //.setContent(remoteView)
                .setAutoCancel(true);
        //builder.setContentTitle("Follow-ups");

        /*if((sender != null) && (source != null) ) {
            builder.setContentText("You have " + value + " new follow-ups \n Sender: " + sender + " Source: " + source);
        } else  {
            builder.setContentText("You have " + value + " follow-ups ");
        }*/
        Intent acceptAllIntent;
        Intent assignedIntent;
        SmartUser smartUser = SmarterSMBApplication.SmartUser;

        if (!logout) {
            Intent smartIntent = new Intent(this, UearnHome.class);
            smartIntent.putExtra("screenType", 1);

            assignedIntent = smartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //acceptAllIntent = new Intent(this, AcceptAllFollowupsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            /*appointmentList = new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
            assignedIntent = new Intent(this, SmarterAuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            assignedIntent.putExtra("screenType", 0);

            acceptAllIntent = new Intent(this, SmarterAuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            acceptAllIntent.putExtra("screenType", 0);
        }

        //Intent intentAccept = new Intent(getApplicationContext(), AcceptAllFollowupsActivity.class);
        //PendingIntent pendingAssignedIntent = PendingIntent.getActivity(getApplicationContext(), this.NOTIFICATION_ID, assignedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingAssignedIntent = PendingIntent.getActivity(getApplicationContext(), this.NOTIFICATION_ID, appointmentList, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pendingIntentAccept = PendingIntent.getActivity(getApplicationContext(), 0, intentAccept, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pendingAcceptAll = PendingIntent.getActivity(getApplicationContext(), this.NOTIFICATION_ID, acceptAllIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pendingCancel = PendingIntent.getActivity(getApplicationContext(), this.NOTIFICATION_ID, acceptAllIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.small_logo);
        builder.setColor(Color.parseColor("#dd374c"));
        builder.setWhen(0).setAutoCancel(true);
//        if (!smartUser.getAutoAccepteLeadsSettings()) {
//            builder.setContentIntent(pendingIntentAccept);
//        } else {
//            builder.setContentIntent(pendingAssignedIntent);
//        }
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


        if (!smartUser.getAutoAccepteLeadsSettings()) {
           /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder.addAction(R.drawable.ic_done_all_black_24px, "Accept All", pendingAcceptAll);
                builder.addAction(R.drawable.ic_clear_black_24px, "Cancel", pendingCancel);
            }*/
        }

        nManager.notify("App Name", NOTIFICATION_ID, builder.build());
    }

    private void displayOrUpdateNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = createID();
        String channelId = "Uearn-channel-id";
        String channelName = "Uearn Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.small_logo)//R.mipmap.ic_launcher
                .setContentTitle("Uearn")
                .setContentText(message)
                .setVibrate(new long[]{100, 250})
                .setLights(Color.YELLOW, 500, 5000)
                .setAutoCancel(true)
                .setColor(Color.parseColor("#dd374c"));

        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        if(!logout) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(new Intent(this, NotificationMessageActivity.class));
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        } else {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(new Intent(this, SplashScreenActivity.class));
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        }

        notificationManager.notify(notificationId, mBuilder.build());
    }

    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.ENGLISH).format(now));
        return id;
    }

    private void updateSalesStageFunnel() {
        if (CommonUtils.isNetworkAvailable(this)) {
            SmartUser smartUser = SmarterSMBApplication.SmartUser;
            //Log.i("PushNotification", "In update sales stage Funnel");
            new APIProvider.Get_Sales_Status(smartUser.getId(), 22, new API_Response_Listener<SalesStageInfo>() {
                @Override
                public void onComplete(SalesStageInfo data, long request_code, int failure_code) {
                    if (data != null) {
                        data.dosave();
                        SmarterSMBApplication.salesStageInfo = data;
                    }
                }
            }).call();
        }
    }

    private void refreshTeamListForAssignTo() {
        if (CommonUtils.isNetworkAvailable(this)) {
            SmartUser smartUser = SmarterSMBApplication.SmartUser;
            new APIProvider.Get_All_Team_Members(smartUser.getId(), 23, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                @Override
                public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                    if (data != null) {
                        CommonUtils.saveTeamdataToLocalDB(getApplicationContext(), data);
                        // CommonUtils.updateTeamdataToLocalDB(getApplicationContext(), data);
                    }
                }
            }).call();
        }
    }

    private void acceptOrRejectTeamMember(GroupsUserInfo groupsUserInfo) {
        //Log.i("PushNotification", "In accept or reject ************** ");
        final boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        if (groupsUserInfo != null && !logout) {
            showAcceptOrRejectTeamMembersNotification(groupsUserInfo);
        }
    }

    private void showAcceptOrRejectTeamMembersNotification(GroupsUserInfo groupsUserInfo) {
        //Log.i("PushNotification", "In show Accept or reject team member method");
        Intent intent = new Intent(getApplicationContext(), AcceptOrRejectNotificationService.class);
        Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(groupsUserInfo);
        intent.putExtra("groupsUserInfoBundle", bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(intent);
        } else {
            getApplicationContext().startService(intent);
        }
    }

    private GroupsUserInfo getGroupsUserInfo(String info) {
        GroupsUserInfo groupsUserInfo = new GroupsUserInfo();
        if (info != null) {
            try {
                MyJsonObject jsonObject = new MyJsonObject(info);
                String user = jsonObject.getString("user");
                if (user != null) {
                    MyJsonObject jsonObject1 = new MyJsonObject(user);
                    String name = jsonObject1.getString("name");
                    String email_id = jsonObject1.getString("email");
                    String phone_no = jsonObject1.getString("phone");
                    String request = jsonObject1.getString("request");
                    String status = jsonObject1.getString("status");
                    groupsUserInfo.name = name;
                    groupsUserInfo.email = email_id;
                    groupsUserInfo.request = request;
                    groupsUserInfo.phone = phone_no;
                    groupsUserInfo.status = status;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return groupsUserInfo;
    }


    private void saveDetailsTOAmeyoPushNotificationTable(Context context, String customer_number, long start_time, String type, String message) {
        //TODO: save all the details to ameyo push notifications table
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();

        //Log.d("ameyo database", "customer number : " + customer_number + " message : " + message + " start time : " + start_time);

        ContentValues cv = new ContentValues();
        cv.put("CUSTOMER_NUMBER", customer_number);
        cv.put("START_TIME", start_time);
        cv.put("TYPE", type);
        cv.put("MESSAGE", message);
        long id = db.insert("Ameyo_Push_Notifications", null, cv);
        //Log.i("ameyo database", "No of rows inserted are " + id);

        if (db != null && db.isOpen()) {
            db.close();
        }

        if(dbHelper != null)
            dbHelper.close();
    }

    private void displayNotification(String number, String message) {
        if ((number != null) && (message != null)) {
            NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(this);
            Intent acivitiesList;
            boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
            if (!logout) {
                acivitiesList = new Intent(getApplicationContext(), UearnHome.class);
                acivitiesList.putExtra("fromCloud", true);
                acivitiesList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                acivitiesList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                acivitiesList = new Intent(this, SmarterAuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                acivitiesList.putExtra("screenType", 0);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, acivitiesList, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(number);
            builder.setContentText(message);
            builder.setSmallIcon(R.drawable.missed_call);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            nManager.notify("App Name", NOTIFICATION_ID, builder.build());
        }
    }

    private void buildBasicNotification(String title, String description) {
        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent acivitiesList;
        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        if (!logout) {
            acivitiesList = new Intent(getApplicationContext(), UearnHome.class);
            acivitiesList.putExtra("fromCloud", true);
            acivitiesList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            acivitiesList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            acivitiesList = new Intent(this, SmarterAuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            acivitiesList.putExtra("screenType", 0);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, acivitiesList, PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.small_logo)
                .setColor(Color.parseColor("#dd374c"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        nManager.notify(0, builder.build());

    }

    private void signOutAndShowDialog() {

        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            WebStorage webstorage = WebStorage.getInstance();
            webstorage.deleteAllData();
            SmarterSMBApplication.appSettings.setLogout(true);

            ApplicationSettings.putPref(AppConstants.SUPERVISOR_EMAIL, "");
            ApplicationSettings.putPref(AppConstants.CLOUD_TELEPHONY, "");
            ApplicationSettings.putPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
            ApplicationSettings.putPref(AppConstants.DISABLE_CALL_REC_BUTTONS, false);
            ApplicationSettings.putPref(AppConstants.APP_UPDATE, false);

            Intent intent3 = new Intent(getApplicationContext(), UearnHome.class);
            intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent3.putExtra("EXITSIGNIN", true);
            getApplicationContext().startActivity(intent3);
        } catch(Exception e) {

        }
    }
}
