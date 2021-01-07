package smarter.uearn.money.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.android.internal.telephony.ITelephony;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.AfterCallActivity;
import smarter.uearn.money.activities.DataLoadingActivity;
import smarter.uearn.money.activities.JoinUsActivity;
import smarter.uearn.money.activities.LandingActivity;
import smarter.uearn.money.activities.MyAlertDialog;
import smarter.uearn.money.activities.MyStopAlertDialog;
import smarter.uearn.money.activities.UearnActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.broadcastReceiver.ConnectivityReceiver;
import smarter.uearn.money.callrecord.RecorderService;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.chats.model.ResponseChannel;
import smarter.uearn.money.chats.model.ResponseTransation;
import smarter.uearn.money.chats.model.TransationDetails;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.models.AppSettings;
import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GoogleAccountsList;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;
import smarter.uearn.money.views.events.NetworkBroadcastReceiver;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static smarter.uearn.money.activities.UearnHome.internet_status;
import static smarter.uearn.money.activities.UearnHome.internet_status_goal_before;
import static smarter.uearn.money.activities.UearnHome.pingInProgress;
import static smarter.uearn.money.activities.UearnHome.pingResponse;
import static smarter.uearn.money.activities.UearnHome.pingResponselist;
import static smarter.uearn.money.activities.UearnHome.pingTimer;
import static smarter.uearn.money.activities.UearnHome.swipe_to_start;
import static smarter.uearn.money.activities.UearnHome.timerStopped;
import static smarter.uearn.money.chats.activity.ChatActivity.chatMakingCall;

public class SmarterSMBApplication extends MultiDexApplication {

    private static SmarterSMBApplication mInstance;
    private String emailId;
    public static Activity mActivity;
    public static SmartUser SmartUser;
    public static GoogleAccountsList googleAccountsList;
    public static SalesStageInfo salesStageInfo;
    public static AppSettings appSettings;
    public static Context applicationContext;
    public static Activity currentActivity;
    public static Socket mSocket = null;
    public static String pushSioUrl = null;
    public static boolean isHeadPhone = false;
    public static int redialClickCount = 0;
    public static SQLiteDatabase db;
    public static String lastConnectedCustomerNumber = "";
    public static boolean headphoneNotConnected = false;

    public static boolean callEndedACPTimeout = false;
    public static boolean moveToNormal = false;
    public static boolean moveToRAD = false;
    public static boolean moveToRADSTOP = false;

    public static boolean signoutNotificationReceived = false;
    public static boolean callingFollowUps = false;

    public static boolean redialStartRequest = false;

    public static boolean sendCallDisconnectStopRequest = false;
    public static boolean sendCallDisconnectStartFollowUpRequest = false;

    public static boolean connectedCustomerInProcess = false;
    public static boolean disconnectedCustomerInProcess = false;

    public static boolean isCallLanded = false;
    public static boolean isFirstCall = false;
    public static boolean agentDisconnectScenario = false;
    public static boolean customerDisconnectScenario = false;
    public static boolean legADisconnectScenario = false;
    public static boolean navigateToACP = false;

    public static String connectedCustomerNumber = "";
    public static String connectedCustomerName = "";

    public static String connectedCustomerState = "";
    public static boolean triggerDataLoadingAfterSubmit = false;

    public static String currentAppState = "";
    public static boolean showSingleAcp = false;
    public static int currentSignalStrength = 0;

    public static boolean tpdRedialScenario = false;
    public static boolean isRemoteDialledStart = false;
    public static boolean isC2CAutoStart = false;

    public static boolean backToNetwork = false;
    public static boolean noNetworkAvailable = false;
    public static boolean timerStarted = false;
    public static boolean isRemoteDialledStopRequest = false;
    public static Dialog permissionDialog = null;
    public static boolean navigateFromUearnProfile = false;

    public static boolean actionMoveToNormalProcessed = false;
    public static boolean navigatingToUearnHome = false;
    public static boolean launchAppButtonClicked = false;
    public static boolean legADisconnected = false;

    public static boolean incomingCallLandingToDevice = false;
    public static boolean incomingCallAcceptedByAgent = false;
    public static boolean incomingCallRejectedByAgent = false;
    public static boolean callEndedFromDuringCall = false;
    public static boolean endTheSession = false;
    public static boolean enableECBAndESB = false;

    public static boolean oneCallIsActive = false;
    public static boolean oneCallISActiveNextCallLanded = false;
    public static boolean remoteEnabledRedialScenario = false;
    public static boolean isCampaignOver = false;
    public static  boolean disableStatusBarAndNavigation = false;

    public static  boolean networkCheckInProgress = false;
    public static boolean callStateIsDisconnected = false;
    public static boolean isActivityLaunchedFromReceiver = false;

    public static boolean isDiallingFollowUpC2C = false;
    public static boolean isDiallingFromDoneList = false;
    public static boolean followupsInPredictive = false;
    public static boolean isInNumberMatching = false;

    public static boolean autoCallAnswered = false;
    public static boolean autoCallDisconnected = false;

    public static boolean stopRequestFromUearnHome = false;

    public static String stopRequestMsg = "";
    public static boolean sendStopRequest = false;

    public static boolean matchingInNumberNotInStartMode = false;
    public static boolean resetHomeNotInStartMode = false;
    public static boolean launchAppFromRecentTasks = false;

    public static boolean currentModeIsConnected = false;
    public static boolean outgoingCallNotInStartMode = false;
    public static boolean currentStateIsStartMode = false;
    public static boolean deviceSupportsAutoAnswer = false;
    public static String pingStatusResponse = "";


    public static String stopRADMsg = "";
    public static boolean agentIsInConnectingState = false;
    public static boolean agentIsInConnectedState = false;

    public static boolean webViewOutgoingCallEventTriggered = false;
    public static String callEndTime = "";
    public static boolean takingBreakInPD = false;

    public static String curSimSignalStrength = "None";
    public static String curSimService = "None";
    public static String netStatus = "None";
    //public static boolean showConnectionTimeOutStatusCalled = false;

    public static String lastConnectedCustomer = "";
    public static String lastConnectedCustomerFeedback = "";
    public static boolean isTechSelected = false;
    public static boolean isTLSelected = false;
    public static boolean isSMESelected = false;
    public static boolean isGroupSelected = false;

    public static String totalLoggedInTime = "";
    public static String totalActiveTime = "";
    public static boolean isCurrentQuesMandatory = false;
    public static boolean isDecisionMakingQues = false;
    public static String currentSelectedValue = "";
    public static boolean isHeadPhoneConnected = false;
    public static boolean isWifiConnected = false;
    public static boolean isServiceAgreementDone = false;
    public static String currentSelectedProcess = "";
    public static boolean domainURLNotMatching = false;
    public static boolean callingFromProcessSelection = false;
    public static boolean transferCallEnabled = false;
    public static boolean conferenceCallEnabled = false;

    public static boolean cloud2IncomingCall = false;
    public static boolean endCallButtonClicked = false;
    public static boolean notificationAlertReceived = false;
    public static boolean inboundCallInOutbound = false;
    public static boolean postInboundDataToServer = false;

    public static boolean manualDialFromHomeScreen = false;
    public static boolean stayAtHomeScenario = false;
    public static boolean stopButtonClicked = false;
    public static boolean relaunchSameActivityStayHome = false;
    public static boolean systemAlertReceived = false;
    public static boolean manualDialScenario = false;

    public static boolean syncMenuItemClicked = false;
    public static boolean abortCallSessionScenario = false;
    public static boolean isSocketConnectionTimeOut = false;
    public static boolean createAppointmentForManualDialNumberNotInDB = false;
    public static String currentNetworkType = "";

    public static void setCurrentActivity (Activity activity){
        mActivity=activity;
    }
    public static Activity getCurrentActivity(){
        return mActivity;
    }

    public static ResponseTransation transationDetails;

    public static List<View> getWindowManagerViews() {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {

                // get the list from WindowManagerImpl.mViews
                Class wmiClass = Class.forName("android.view.WindowManagerImpl");
                Object wmiInstance = wmiClass.getMethod("getDefault").invoke(null);

                Method getViewRootNames = wmiClass.getMethod("getViewRootNames");
                Method getRootView = wmiClass.getMethod("getRootView", String.class);
                String[] rootViewNames = (String[])getViewRootNames.invoke(wmiInstance, (Object[])null);

                for(String viewName : rootViewNames) {
                    View rootView = (View)getRootView.invoke(wmiInstance, viewName);
                    Log.d("WIND-VIEWS", "Found root view: " + viewName + ": " + rootView);
                }

                return viewsFromWM(wmiClass, wmiInstance);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                // get the list from WindowManagerGlobal.mViews
                Class wmiClass = Class.forName("android.view.WindowManagerGlobal");
                Object wmiInstance = wmiClass.getMethod("getInstance").invoke(null);

                Method getViewRootNames = wmiClass.getMethod("getViewRootNames");
                Method getRootView = wmiClass.getMethod("getRootView", String.class);
                String[] rootViewNames = (String[])getViewRootNames.invoke(wmiInstance, (Object[])null);

                for(String viewName : rootViewNames) {
                    View rootView = (View)getRootView.invoke(wmiInstance, viewName);
                    Log.d("WIND-VIEWS", "Found root view: " + viewName + ": " + rootView);
                }

                return viewsFromWM(wmiClass, wmiInstance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<View>();
    }

    private static List<View> viewsFromWM(Class wmClass, Object wmInstance) throws Exception {

        Field viewsField = wmClass.getDeclaredField("mViews");
        viewsField.setAccessible(true);
        Object views = viewsField.get(wmInstance);

        if (views instanceof List) {
            return (List<View>) viewsField.get(wmInstance);
        } else if (views instanceof View[]) {
            return Arrays.asList((View[])viewsField.get(wmInstance));
        }

        return new ArrayList<View>();
    }

    private static String takeScreenshot(int scrno)
    {
        String encImage = "";

        if(CommonUtils.allowScreenshot()) {

            try {
                List<View> vlist = getWindowManagerViews();

                if (scrno<0) {
                    for (View vOb : vlist) {
                        View nOb = (View) vOb;
                        try {
                            nOb.setDrawingCacheEnabled(true);
                            Bitmap bitmap = Bitmap.createBitmap(nOb.getDrawingCache());
                            nOb.setDrawingCacheEnabled(true);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();
                            encImage = Base64.encodeToString(b, Base64.DEFAULT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    View nOb = (View) vlist.get(scrno);
                    if (vlist.size()>scrno) {
                        try {
                            nOb.setDrawingCacheEnabled(true);
                            Bitmap bitmap = Bitmap.createBitmap(nOb.getDrawingCache());
                            nOb.setDrawingCacheEnabled(true);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();
                            encImage = Base64.encodeToString(b, Base64.DEFAULT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            } catch (Exception e) {

            }
        }

        if (encImage.isEmpty()) {
            if(CommonUtils.allowScreenshot()) {
                View v1 = getCurrentActivity().getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                encImage = Base64.encodeToString(b, Base64.DEFAULT);
            }
        }
        return encImage;
    }


    public static void connectToPushServer() {
        Log.d("SOCKETIO", "Called connectToPushServer()" + "PushSIO URL: " + pushSioUrl);
        try {
            if (pushSioUrl != null && !pushSioUrl.isEmpty()) {
                IO.Options opts = new IO.Options();
                opts.forceNew = true;
                opts.reconnection = false;

                mSocket = IO.socket(pushSioUrl, opts);
                mSocket.on("connect", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("registerme", userId);
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("registerme-ack", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        //Log.d("SIOTEST", "registerme-ack "+args[0]);
                    }
                });

                mSocket.on("isalive", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    String pushReceiverRegistrationStatus = ApplicationSettings.getPref(AppConstants.PUSHRECEIVER_REGISTRATION_STATUS, "");
                                    if (pushReceiverRegistrationStatus != null && !pushReceiverRegistrationStatus.isEmpty()) {
                                        mSocket.emit("isalive-ack", userId, pushReceiverRegistrationStatus);
                                    } else {
                                        mSocket.emit("isalive-ack", userId);
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("takescreenshot", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        //
                        int screenNo = -1;
                        if ((args.length>1) && (args[0]!=null)) {
                            screenNo = Integer.parseInt((String) args[0]);
                        }
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                mSocket.emit("takescreenshot-ack", userId, takeScreenshot(screenNo));
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("getsimsignal", new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {
                        Log.d("SIOTEST", "getsimsignal "+SmarterSMBApplication.curSimSignalStrength);
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                mSocket.emit("getsimsignal-ack", userId, SmarterSMBApplication.curSimSignalStrength);
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("getsimservice", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        Log.d("SIOTEST", "getsimservice "+SmarterSMBApplication.curSimService);
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                mSocket.emit("getsimservice-ack", userId, SmarterSMBApplication.curSimService);
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("getnetstatus", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                mSocket.emit("getnetstatus-ack", userId, SmarterSMBApplication.pingStatusResponse);
                            } catch (Exception e) {

                            }
                        }
                    }
                });
/*
                mSocket.on("pushnotif", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        String pushmessage = (String) args[0];
                        String pushmessageData = (String) args[1];
                        Log.d("SIOTEST", "socket io received "+pushmessage);

                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("pushnotif-ack", userId);
                                }
                            } catch (Exception e) {

                            }
                        }

                        Bundle pushBundle = new Bundle();
                        pushBundle.putString("info", pushmessageData);
                        pushBundle.putString("message", pushmessage);
                        sendMessage(pushBundle);
                        //sendMessageToGcmListenerService(pushmessage, pushmessageData);
                    }
                });
*/

                mSocket.on("pushnotif", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        String pushmessage = (String) args[0];
                        String pushmessageData = (String) args[1];
                        Log.d("SIOTEST", "socket2 io received " + pushmessage);

                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    //Log.d("NavigateToACP","Connected customer state: "+ connectedCustomerState);
                                    if (connectedCustomerState == null || connectedCustomerState.isEmpty()) {
                                        connectedCustomerState = "Connecting";
                                    }
                                    mSocket.emit("pushnotif-ack", userId, pushmessageData, connectedCustomerState);
                                }
                            } catch (Exception e) {

                            }
                        }

                        Bundle pushBundle = new Bundle();
                        pushBundle.putString("info", pushmessageData);
                        pushBundle.putString("message", pushmessage);
                        if(chatMakingCall){
                            Gson g = new Gson();
                            transationDetails = g.fromJson(pushmessageData, ResponseTransation.class);
                            GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
                            getCalendarEntryInfo.caller_number = transationDetails.getData().getCustomer_number();
                            getCalendarEntryInfo.transactionId = transationDetails.getData().getTransactionid();
                            getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
                            getCalendarEntryInfo.mode = "CHAT";
                            final JSONObject jsonObj = JSONParser.getJsonForSalesUpdate(getCalendarEntryInfo);
                            JSONObject responsejsonObject = DataUploadUtils.postSalesStatusUpdateData(Urls.getSalesStatusUpdate(), jsonObj);
                            String data = "";
                            if (responsejsonObject != null) {
                                data = responsejsonObject.toString();
                                try {
                                    if(responsejsonObject.getString("response").equalsIgnoreCase("")){
                                        chatMakingCall = true;
                                    }else{
                                        chatMakingCall = false;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            return;
                        }
                        sendMessage(pushBundle);
                        //sendMessageToGcmListenerService(pushmessage, pushmessageData);
                    }
                });

                mSocket.on("getsalesentries", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        final String query = (String) args[0];
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("getsalesentries-ack", userId);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                sendSalesEntriesInfoToServer(query);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    thread.start();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("getdbentries", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        final String query = (String) args[0];
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("getdbentries_ack", userId);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                sendDBEntriesInfoToServer(query);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    thread.start();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("runoscommand", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        final String command = (String) args[0];
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                final String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("runoscommand_ack", userId);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                sendLogcatViaMail(command);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    thread.start();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("getmysettings", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("getmysettings_ack", userId);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                sendMySettingsInfoToServer();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    thread.start();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("getpref", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        final String preferenceKey = (String) args[0];
                        final String preferenceDefaultValue = (String) args[1];
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("getpref_ack", userId);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if(preferenceDefaultValue != null){
                                                    if(preferenceDefaultValue.equalsIgnoreCase("")){
                                                        String preferenceValueString = ApplicationSettings.getPref(preferenceKey, "");
                                                        System.out.println(preferenceValueString);
                                                    } else  if(preferenceDefaultValue.equalsIgnoreCase("0l")){
                                                        long preferenceValueLong = ApplicationSettings.getPref(preferenceKey, Long.parseLong(preferenceDefaultValue));
                                                        System.out.println(preferenceValueLong);
                                                    } else if(preferenceDefaultValue.equalsIgnoreCase("false")){
                                                        boolean preferenceValueBoolean = ApplicationSettings.getPref(preferenceKey, Boolean.valueOf(preferenceDefaultValue));
                                                        System.out.println(preferenceValueBoolean);
                                                    } else  if(preferenceDefaultValue.equalsIgnoreCase("0f")){
                                                        float preferenceValueFloat = ApplicationSettings.getFloatPref(preferenceKey, Float.parseFloat(preferenceDefaultValue));
                                                        System.out.println(preferenceValueFloat);
                                                    } else  if(preferenceDefaultValue.equalsIgnoreCase("0")){
                                                        long preferenceValueLong = ApplicationSettings.getLongPref(preferenceKey, Long.parseLong(preferenceDefaultValue));
                                                        System.out.println(preferenceValueLong);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    thread.start();
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                });

                mSocket.on("setpref", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        final String preferenceKey = (String) args[0];
                        final String preferenceDefaultValue = (String) args[1];
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("setpref_ack", userId);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (preferenceDefaultValue.matches("[0-9]+")) {
                                                    ApplicationSettings.putPref(preferenceKey, preferenceDefaultValue);
                                                } else if (preferenceDefaultValue.contains("false") || preferenceDefaultValue.contains("true")) {
                                                    ApplicationSettings.putPref(preferenceKey, Boolean.valueOf(preferenceDefaultValue));
                                                } else {
                                                    ApplicationSettings.putPref(preferenceKey, preferenceDefaultValue);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    thread.start();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });

                mSocket.on("questionnaire", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        String questionnaire = (String) args[0];
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("questionnaire-ack", userId);
                                }
                            } catch (Exception e) {

                            }
                        }

                        ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                    }
                });

                mSocket.on("getagentstatus", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        final String agentStatus = (String) args[0];
                        if (mSocket != null && mSocket.connected()) {
                            try {
                                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                                if (!userId.isEmpty()) {
                                    mSocket.emit("getagentstatus_ack", userId);
                                }
                            } catch (Exception e) {

                            }
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
                    }
                });

                mSocket.connect();
            }
        } catch (Exception e) {
            Log.e("SOCKETIO", "exception: " + e.getMessage());
        }
    }

    public static void registerSocket() {
        //Log.d("SocketIOTest", "Called registerSocket()" + "PushSIO URL: " + pushSioUrl);
        try {
            if (mSocket != null && mSocket.connected()) {
                try {
                    String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    if (!userId.isEmpty()) {
                        //Log.d("SocketIOTest", "SmarterSMBApplication emit register me");
                        mSocket.emit("registerme", userId);
                    }
                } catch (Exception e) {

                }

                mSocket.on("registerme-ack", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        //Log.d("SocketIOTest", "registerme-ack "+args[0]);
                    }
                });
            } else {
                connectToPushServer();
            }
        } catch (Exception e) {
            Log.e("SOCKETIO", "exception: " + e.getMessage());
        }
    }

    private static void syncAppointment(final String appointmentId, final String typeOfAppointment) {
        new APIProvider.Get_CalendarEvent(appointmentId, 1, new API_Response_Listener<GetCalendarEntryInfo>() {
            @Override
            public void onComplete(GetCalendarEntryInfo data, long request_code, int failure_code) {
                if (data != null) {
                    if (typeOfAppointment.equals("new appointment")) {
                        //Log.d("SMBSIO", "new appointment "+appointmentId);
                        CommonUtils.saveCalendarEventToLocalDB(applicationContext, data, "app");
                    } else if (typeOfAppointment.equals("updated appointment")) {
                        //Log.d("SMBSIO", "updating appointment"+appointmentId);
                        CommonUtils.updateAppointmentInLocalDB(applicationContext, data, "app");
                    }
                }
            }
        }).call();
    }

    private static void sendMessageToUearnActivity(String message) {
        //Log.d("FKDemoTest", "SmarterSMBApplication - sendMessageToUearnActivity111");
        Intent intent = new Intent("connected-customer-push-event");
        intent.putExtra("result", message);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    private static void sendMessage(Bundle data) {

        String message = data.getString("message");

        if (message.equals("system alert")) {

            if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    if(SmarterSMBApplication.currentStateIsStartMode) {
                        String information = data.getString("info");
                        try {
                            JSONObject jsonObject = new JSONObject(information);
                            String data1 = "";
                            if (jsonObject.has("data")) {
                                data1 = jsonObject.getString("data");
                            }
                            JSONObject infoJsonObject = new JSONObject(data1);
                            if (infoJsonObject.has("customer_number")) {
                                String customerNumber = infoJsonObject.getString("customer_number");
                                NotificationData.inboundCustomerNumber = customerNumber;
                            }
                            if (infoJsonObject.has("customer_name")) {
                                String customerName = infoJsonObject.getString("customer_name");
                                NotificationData.inboundCustomerName = customerName;
                            }
                            if (infoJsonObject.has("transactionid")) {
                                String transactionId = infoJsonObject.getString("transactionid");
                                NotificationData.inboundTransactionId = transactionId;
                            }
                            if (infoJsonObject.has("message")) {
                                NotificationData.customKVS = infoJsonObject.getString("message");
                            }
                        } catch (JSONException e) {
                            Log.d("Exception in broadCast", e.toString());
                        }
                    } else {
                        NotificationData.outboundDialledCustomerNumber = "";
                        NotificationData.outboundDialledCustomerName = "";
                        NotificationData.outboundDialledTransactionId = "";
                        String information = data.getString("info");
                        try {
                            JSONObject jsonObject = new JSONObject(information);
                            String data1 = "";
                            if (jsonObject.has("data")) {
                                data1 = jsonObject.getString("data");
                            }
                            JSONObject infoJsonObject = new JSONObject(data1);
                            if (infoJsonObject.has("customer_number")) {
                                String customerNumber = infoJsonObject.getString("customer_number");
                                NotificationData.outboundDialledCustomerNumber = customerNumber;
                            }
                            if (infoJsonObject.has("customer_name")) {
                                String customerName = infoJsonObject.getString("customer_name");
                                NotificationData.outboundDialledCustomerName = customerName;
                            }
                            if (infoJsonObject.has("transactionid")) {
                                String transactionId = infoJsonObject.getString("transactionid");
                                NotificationData.outboundDialledTransactionId = transactionId;
                            }
                            if (infoJsonObject.has("message")) {
                                NotificationData.customKVS = infoJsonObject.getString("message");
                            }
                        } catch (JSONException e) {
                            Log.d("Exception in broadCast", e.toString());
                        }
                        navigateToUearnActivity();
                        SmarterSMBApplication.incomingCallAcceptedByAgent = true;
                        SmarterSMBApplication.legADisconnected = false;
                        sendMessageToUearnActivity("call_answered");
                    }
                }
            } else {

                boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
                if(truePredictive) {

                    if(CommonUtils.allowASF()) {
                        if (SmarterSMBApplication.takingBreakInPD) {
                            SmarterSMBApplication.takingBreakInPD = false;
                            return;
                        }
                    }

                    if(!connectedCustomerInProcess){
                        connectedCustomerInProcess = true;
                        disconnectedCustomerInProcess = true;
                    } else {
                        return;
                    }
                }

                NotificationData.dialledCustomerNumber = "";
                NotificationData.dialledCustomerName = "";
                SmarterSMBApplication.relaunchSameActivityStayHome = false;

                // Used for indicating another sign in
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
                        connectedCustomerNumber = customerNumber;
                        NotificationData.dialledCustomerNumber = customerNumber;
                        NotificationData.outboundDialledCustomerNumber = customerNumber;
                        Log.d("PredictiveTesting", "Customer Number: " + customerNumber);
                        SmarterSMBApplication.agentIsInConnectingState = false;
                        SmarterSMBApplication.endTheSession = false;
                    }

                    String customerName = "";
                    if (infoJsonObject.has("customer_name")) {
                        customerName = infoJsonObject.getString("customer_name");
                        connectedCustomerName = customerName;
                        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, connectedCustomerName);
                        NotificationData.dialledCustomerName = customerName;

                        String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                        if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {

                        } else {
                            if (NotificationData.dialledCustomerNumber != null && !NotificationData.dialledCustomerNumber.isEmpty()) {
                                if (customerName != null && !customerName.isEmpty() && customerName.equalsIgnoreCase("No Name")) {
                                    customerName = getCustomerName(NotificationData.dialledCustomerNumber);

                                    connectedCustomerName = customerName;
                                    NotificationData.dialledCustomerName = customerName;
                                }
                            }
                        }
                    }

                    if (customerNumber != null && !(customerNumber.isEmpty())) {
                        NotificationData.notificationData = true;
                        NotificationData.knolarity_name = customerName;
                        NotificationData.dialledCustomerName = customerName;
                        NotificationData.knolarity_number = customerNumber;
                        NotificationData.dialledCustomerNumber = customerNumber;
                        NotificationData.substatus1 = "";
                        NotificationData.substatus2 = "";
                        NotificationData.statusString = "";
                        NotificationData.notes_string = "";

                        if (infoJsonObject.has("email_id")) {
                            String emailId = infoJsonObject.getString("email_id");
                            if (emailId != null && !emailId.isEmpty() && !emailId.equals("null")) {
                                NotificationData.emailId = emailId;
                            } else {
                                NotificationData.emailId = "";
                            }
                        }

                        if (infoJsonObject.has("lead_source")) {
                            String leadSource = infoJsonObject.getString("lead_source");
                            if (leadSource != null && !leadSource.isEmpty() && !leadSource.equals("null")) {
                                NotificationData.leadSource = leadSource;
                            } else {
                                NotificationData.leadSource = "";
                            }
                        }

                        if (infoJsonObject.has("status")) {
                            String status = infoJsonObject.getString("status");
                            if (status != null && !status.isEmpty() && !status.equals("null")) {
                                NotificationData.statusString = status;
                            } else {
                                NotificationData.statusString = "";
                            }
                        }

                        if (infoJsonObject.has("substatus1")) {
                            String substatus1 = infoJsonObject.getString("substatus1");
                            if (substatus1 != null && !substatus1.isEmpty() && !substatus1.equals("null")) {
                                NotificationData.substatus1 = substatus1;
                            } else {
                                NotificationData.substatus1 = "";
                            }
                        }

                        if (infoJsonObject.has("substatus2")) {
                            String substatus2 = infoJsonObject.getString("substatus2");
                            if (substatus2 != null && !substatus2.isEmpty() && !substatus2.equals("null")) {
                                NotificationData.substatus2 = substatus2;
                            } else {
                                NotificationData.substatus2 = "";
                            }
                        }

                        if (infoJsonObject.has("notes")) {
                            String notes = infoJsonObject.getString("notes");
                            if (notes != null && !notes.isEmpty() && !notes.equals("null")) {
                                NotificationData.notes_string = notes;
                            } else {
                                NotificationData.notes_string = "";
                            }
                        } else if (infoJsonObject.has("remarks")) {
                            String remarks = infoJsonObject.getString("remarks");
                            if (remarks != null && !remarks.isEmpty() && !remarks.equals("null")) {
                                NotificationData.notes_string = remarks;
                            } else {
                                NotificationData.notes_string = "";
                            }
                        }

                        if (infoJsonObject.has("transactionid")) {
                            NotificationData.transactionId = infoJsonObject.getString("transactionid");
                            NotificationData.uuid = infoJsonObject.getString("transactionid");
                        }

                        if (infoJsonObject.has("message")) {
                            NotificationData.customKVS = infoJsonObject.getString("message");
                        }

                        if (infoJsonObject.has("questionnaire")) {
                            String questionnaire = "";
                            ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, "");
                            if (ApplicationSettings.containsPref(AppConstants.CUSTOMER_QUES)) {
                                boolean customerQues = ApplicationSettings.getPref(AppConstants.CUSTOMER_QUES, false);
                                if (customerQues) {
                                    questionnaire = infoJsonObject.getString("questionnaire");
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

                        try {
                            CommonUtils.saveBankContactInfoToSmartContactDB(applicationContext, NotificationData.dialledCustomerNumber, "", NotificationData.statusString, NotificationData.notes_string, NotificationData.substatus1, NotificationData.substatus2, "", NotificationData.leadSource, "", "", NotificationData.transactionId);
                        } catch(Exception e){
                            e.printStackTrace();
                        }

                        String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                        if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                            boolean radC2CEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                            if (radC2CEndpoint) {
                                CustomerLite customerLiteInfo = new CustomerLite(0, connectedCustomerName, connectedCustomerNumber, NotificationData.statusString, NotificationData.substatus1, NotificationData.substatus2, NotificationData.leadSource, "", NotificationData.notes_string, "", NotificationData.customKVS);
                                List<CustomerLite> tempcustomerList = new ArrayList<>();
                                tempcustomerList.add(0, customerLiteInfo);
                                ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
                                ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
                                String customerListData = new Gson().toJson(tempcustomerList);
                                ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData.toString());
                            }
                        }

                        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, customerName);
                        ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customerNumber);
                        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, customerNumber);
                        startRecording();
                    }

                    String sign_out = "";
                    if (infoJsonObject.has("sign_out")) {
                        sign_out = infoJsonObject.getString("sign_out");
                    }

                    String offlineUrl = "";
                    if(infoJsonObject.has("geturl")) {
                        offlineUrl = infoJsonObject.getString("geturl");
                    }

                    String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
                    if (qanda != null && !(qanda.isEmpty())) {
                        if (customerNumber != null && !customerNumber.isEmpty()) {
                            String statusString = getLatestStatus(customerNumber);
                            if(statusString == null || statusString.isEmpty()){
                                statusString = NotificationData.statusString;
                            }
                            if (statusString != null && !statusString.isEmpty()) {
                                if(statusString.equalsIgnoreCase("ASSIGNTOL2")){

                                } else {
                                    if(SmarterSMBApplication.stayAtHomeScenario) {
                                        SmarterSMBApplication.systemAlertReceived = true;
                                        sendMessageToSmbHome("system_alert_received");
                                    } else {
                                        if (SmarterSMBApplication.isRemoteDialledStart || SmarterSMBApplication.isDiallingFollowUpC2C || UearnActivity.redialScenario || SmarterSMBApplication.outgoingCallNotInStartMode) {
                                            if(SmarterSMBApplication.outgoingCallNotInStartMode){
                                                SmarterSMBApplication.systemAlertReceived = true;
                                            }
                                            showACPScreen();
                                        }
                                    }
                                }
                            } else if (statusString.isEmpty()) {
                                Log.d("PredictiveTesting", "System Alert - showACPScreen ELSE IF");
                                showACPScreen();
                            }
                        }
                    } else {
                        if (infoJsonObject.has("showaftercall")) {
                            //Log.d("NavigateToACP","showaftercall");
                            ApplicationSettings.putPref(AppConstants.SHOW_AFTER_CALL_POPUP, true);
                            Log.d("ShowACP", "SmarterSMBApplication - showACPScreen3");
                            showACPScreen();
                        } else if (alert_message != null && !(alert_message.isEmpty())) {

                            boolean showAlert = ApplicationSettings.getPref(AppConstants.SHOW_ALERT, false);
                            if (!showAlert) {
                                //Log.d("NewChanges", String.valueOf(showAlert));
                                //Log.d("NavigateToACP",String.valueOf(showAlert));
                                //Log.d("EmptyCardIssue",String.valueOf(showAlert));
                                //Log.d("LocalTesting", String.valueOf(showAlert));
                                Log.d("ShowACP", "SmarterSMBApplication - showACPScreen4");
                                showACPScreen();
                            } else {
                                if (UearnActivity.dismissAlertDialog)
                                    UearnActivity.dismissAlertDialog = false;
                                if (UearnActivity.dismissAlertDialog)
                                    UearnActivity.dismissAlertDialog = false;

                                final Intent intent4 = new Intent(applicationContext, MyStopAlertDialog.class);
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
                                applicationContext.startActivity(intent4);
                            }
                        }
                    }

                } catch (JSONException e) {
                    Log.d("Exception in broadCast", e.toString());
                }
            }
        } else if (message.equalsIgnoreCase("rad message")) {
            String information = data.getString("info");
            try {
                JSONObject jsonObject = new JSONObject(information);
                String data1 = "";
                String action = "";
                if (jsonObject.has("data")) {
                    data1 = jsonObject.getString("data");
                }

                JSONObject infoJsonObject = new JSONObject(data1);

                String alert_message = infoJsonObject.getString("message");
                ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, alert_message);

                if (infoJsonObject.has("action")) {
                    action = infoJsonObject.getString("action");
                }

                if (action != null && !action.isEmpty() && action.equalsIgnoreCase("pdstarted")) {
                    SmarterSMBApplication.followupsInPredictive = false;
                }

                if (action != null && !action.isEmpty() && action.equalsIgnoreCase("followupsstarted")) {
                    SmarterSMBApplication.followupsInPredictive = true;
                }

                if (action != null && !action.isEmpty() && action.equalsIgnoreCase("movetorad")) {
                    SmarterSMBApplication.moveToNormal = false;
                    SmarterSMBApplication.moveToRAD = true;
                    SmarterSMBApplication.callingFollowUps = false;
                }

                if (action != null && !action.isEmpty() && action.equalsIgnoreCase("movetonormal")) {
                    if (SmarterSMBApplication.currentAppState != null && !SmarterSMBApplication.currentAppState.isEmpty() && (SmarterSMBApplication.currentAppState.equalsIgnoreCase("Connected") || SmarterSMBApplication.currentAppState.equalsIgnoreCase("ACP"))) {

                    } else {
                        if(SmarterSMBApplication.stayAtHomeScenario){

                        } else {
                            SmarterSMBApplication.moveToNormal = true;
                            SmarterSMBApplication.moveToRAD = false;
                            SmarterSMBApplication.callingFollowUps = false;
                        }
                    }
                    //showHomeScreen();
                }

                if (action != null && !action.isEmpty() && action.equalsIgnoreCase("rad stop")) {
                    SmarterSMBApplication.moveToRADSTOP = true;
                    SmarterSMBApplication.moveToNormal = false;
                    SmarterSMBApplication.moveToRAD = false;
                    SmarterSMBApplication.callingFollowUps = false;
                    //showHomeScreen();
                }

                if (SmarterSMBApplication.currentAppState != null && !SmarterSMBApplication.currentAppState.isEmpty() && (SmarterSMBApplication.currentAppState.equalsIgnoreCase("Connecting"))) {
                    sendMessageToUearnActivity("rad_message");
                } else {
                    sendMessageToSmbHome(alert_message);
                }

            } catch (JSONException e) {
                //Log.d("Exception in broadCast", e.toString());
            }
        } else if (message.equalsIgnoreCase("connected customer")) {
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
                //Log.d("Exception in broadCast", e.toString());
            }
        } else if (message.equalsIgnoreCase("customer alert")) {
            //Log.d("TruePredictiveTest", "Inside Customer Alert");
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
                    //Log.d("TruePredictiveTest", "Customer Alert - Customer No: " + customerNumber);
                    NotificationData.notificationData = true;
                    NotificationData.knolarity_name = customerName;
                    NotificationData.dialledCustomerName = customerName;
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

                    if (infoJsonObject.has("lead_source")) {
                        NotificationData.leadSource = infoJsonObject.getString("lead_source");
                    }

                    if (infoJsonObject.has("referred_by")) {
                        NotificationData.referredBy = infoJsonObject.getString("referred_by");
                    }

                    if (infoJsonObject.has("notes")) {
                        NotificationData.notes_string = infoJsonObject.getString("notes");
                    } else if (infoJsonObject.has("remarks")) {
                        NotificationData.notes_string = infoJsonObject.getString("remarks");
                    }

                    if (infoJsonObject.has("message")) {
                        NotificationData.customKVS = infoJsonObject.getString("message");
                    }

                    if (infoJsonObject.has("questionnaire")) {
                        String questionnaire = "";
                        ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, "");
                        if (ApplicationSettings.containsPref(AppConstants.CUSTOMER_QUES)) {
                            boolean customerQues = ApplicationSettings.getPref(AppConstants.CUSTOMER_QUES, false);
                            if (customerQues) {
                                questionnaire = infoJsonObject.getString("questionnaire");
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

                    String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                    if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                        boolean radC2CEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                        if (radC2CEndpoint) {
                            CustomerLite customerLiteInfo = new CustomerLite(0, NotificationData.knolarity_name, NotificationData.dialledCustomerNumber, NotificationData.statusString, NotificationData.substatus1, NotificationData.substatus2, NotificationData.leadSource, NotificationData.referredBy, NotificationData.notes_string, "", NotificationData.customKVS);
                            List<CustomerLite> tempcustomerList = new ArrayList<>();
                            tempcustomerList.add(0, customerLiteInfo);
                            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
                            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
                            String customerListData = new Gson().toJson(tempcustomerList);
                            ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData.toString());
                        }
                    }

                    ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, customerName);
                    ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customerNumber);
                }

                String sign_out = "";
                if (infoJsonObject.has("sign_out")) {
                    sign_out = infoJsonObject.getString("sign_out");
                }

                String showcancel = "";
                if (infoJsonObject.has("showcancel")) {
                    showcancel = infoJsonObject.getString("showcancel");
                }

                if (infoJsonObject.has("showaftercall")) {
                    ApplicationSettings.putPref(AppConstants.SHOW_AFTER_CALL_POPUP, true);
                    showACPScreen();
                } else {
                    if (alert_message != null && !(alert_message.isEmpty())) {
                        //Log.d("TestNotification", "Calling sendMessageToSmbHome" + alert_message);
                        sendMessageToSmbHome(alert_message, "", tts_enable, showcancel);
                    }
                }
            } catch (JSONException e) {
                //Log.d("Exception in broadCast", e.toString());
            }
        } else if (message.equals("customer disconnected")) {
//            if(!disconnectedCustomerInProcess){
//                disconnectedCustomerInProcess = true;
//            } else {
//                return;
//            }

            long callEndTime= System.currentTimeMillis();
            ApplicationSettings.putPref(AppConstants.CALL_END_TIME, callEndTime);
            //Log.d("WrapupTime","SmarterSMBApplication - customer disconnected" +callEndTime);

            //Log.d("NavigateToACP","connectedCustomerState" + connectedCustomerState);
            //Log.d("WrapupTime","SmarterSMBApplication - Customer Disconnected" + connectedCustomerState);
            if(connectedCustomerState != null && !connectedCustomerState.isEmpty() && connectedCustomerState.equalsIgnoreCase("Connecting")) {
                return;
            } else {
                if(SmarterSMBApplication.disconnectedCustomerInProcess){
                    SmarterSMBApplication.disconnectedCustomerInProcess = false;
                }
            }

            // Used for indicating another sign in
            //Log.d("NavigateToACP","Inside Customer disconnect");
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
                    //Log.d("NewChanges","statusConnected" + statusConnected);
                    //Log.d("NavigateToACP","statusConnected" + statusConnected);
                    //Log.d("TruePredictiveTest", "statusConnected" + statusConnected);
                    String disConnectedCustomer = ApplicationSettings.getPref(AppConstants.CUSTOMER_DISCONNECT, "");
                    //Log.d("TruePredictiveTest", "disConnectedCustomer" + disConnectedCustomer);
                    if (statusConnected) {
                        if (disConnectedCustomer != null && !disConnectedCustomer.isEmpty()) {
                            //Log.d("NewChanges", "Already disconnected by Agent");
                            //Log.d("NavigateToACP", "Already disconnected by Agent");
                            SmarterSMBApplication.customerDisconnectScenario = false;
                            //Log.d("TruePredictiveTest", "Customer disconnected by Agent");
                        } else {
                            //Log.d("NewChanges","disconnectedCustomerInProcess" + disconnectedCustomerInProcess);
                            //Log.d("NavigateToACP","disconnectedCustomerInProcess" + disconnectedCustomerInProcess);
                            if (!disconnectedCustomerInProcess) {
                                disconnectedCustomerInProcess = true;
                            } else {
                                return;
                            }
                            //Log.d("TruePredictiveTest", "Customer disconnected" + customerNumber);
                            //Log.d("NewChanges","Customer disconnected" + customerNumber);
                            //Log.d("NavigateToACP","Customer disconnected" + customerNumber);
                            SmarterSMBApplication.customerDisconnectScenario = true;
                            NotificationData.source = "Customer disconnected";
                            if (NotificationData.dialledCustomerNumber == null || NotificationData.dialledCustomerNumber.isEmpty()) {
                                NotificationData.dialledCustomerNumber = customerNumber;
                            }
                            sendCallDisconnectInfoToServer(NotificationData.dialledCustomerNumber, NotificationData.source);
                            ApplicationSettings.putPref(AppConstants.CUSTOMER_DISCONNECT, customerNumber);
                            stopRecording(customerNumber);
                        }
                    } else {
                        SmarterSMBApplication.customerDisconnectScenario = false;
                    }
                }
            } catch (JSONException e) {
                //Log.d("Exception in broadCast", e.toString());
            }
        } else if (message.equals("call disconnected")) {
            if (SmarterSMBApplication.currentAppState != null && !SmarterSMBApplication.currentAppState.isEmpty() && (SmarterSMBApplication.currentAppState.equalsIgnoreCase("Connected") || SmarterSMBApplication.currentAppState.equalsIgnoreCase("ACP"))) {

            } else {
                //Log.d("LegADisc", "Inside call disconnect");
                //sendCallDisconnectStopRequest("Taking a break");
                legADisconnected = true;
                sendCallDisconnectStopRequest = true;
                callDisconnect();

                String information = data.getString("info");
                try {
                    JSONObject jsonObject = new JSONObject(information);
                    String data1 = "";
                    String action = "";
                    if (jsonObject.has("data")) {
                        data1 = jsonObject.getString("data");
                    }

                    JSONObject infoJsonObject = new JSONObject(data1);

                    String alert_message = infoJsonObject.getString("message");
                    ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, alert_message);

                    if (infoJsonObject.has("action")) {
                        action = infoJsonObject.getString("action");
                    }


                    if (action != null && !action.isEmpty() && action.equalsIgnoreCase("movetonormal")) {
                        SmarterSMBApplication.moveToNormal = true;
                        SmarterSMBApplication.moveToRAD = false;
                        SmarterSMBApplication.callingFollowUps = false;
                        //showHomeScreen();
                    }
                    sendMessageToSmbHome(alert_message);

                } catch (JSONException e) {
                    //Log.d("Exception in broadCast", e.toString());
                }

                ActivityManager am = (ActivityManager)currentActivity.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                if(cn != null && cn.getClassName().equals("smarter.uearn.money.activities.UearnActivity")) {
                    if (SmarterSMBApplication.currentAppState != null && !SmarterSMBApplication.currentAppState.isEmpty() && SmarterSMBApplication.currentAppState.equalsIgnoreCase("Connecting")) {
                        if (currentActivity.isFinishing()) {
                        } else {
                            SmarterSMBApplication.endTheSession = true;
                            showHomeScreen();
                        }
                    }
                }
            }
        } else if (message.equals("disconnect call")) {
            legADisconnected = true;
            sendCallDisconnectStopRequest = true;
            callDisconnect();
        } else if (message.equals("reset application")) {
            Log.d("SOCKET-SIO", "Inside reset application");
            if(SmarterSMBApplication.isCampaignOver){
                SmarterSMBApplication.isCampaignOver = false;
            }
            SmarterSMBApplication.moveToNormal = false;
            boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
            if (truePredictive) {
                sendRemoteDialerStop("Taking a break");
            } else {
                sendRemoteDialerStop("");
            }

            SmarterSMBApplication.startDataLoadingActivity();
        } else if (message.equalsIgnoreCase("new appointment") || message.equalsIgnoreCase("updated appointment")) {
            String syncId = data.getString("info");
            syncAppointment(syncId, message);
        } else if (message.equals("restart application")) {

            String userEmail = "";
            String userId = "";

            if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
            }

            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            }

            SharedPreferences.Editor editor = ApplicationSettings.getEditor();
            if (editor != null) {
                editor.clear().commit();
            }

            ApplicationSettings.putPref(AppConstants.USERINFO_ID, userId);
            ApplicationSettings.putPref(AppConstants.USERINFO_EMAIL, userEmail);
            ApplicationSettings.putPref(AppConstants.APP_LOGOUT, true);

            Intent showLandingScreen = new Intent(applicationContext, JoinUsActivity.class);
            showLandingScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            applicationContext.startActivity(showLandingScreen);
        } else if (message.equalsIgnoreCase("click inview")) {
            String information = data.getString("info");
            //Log.d("SIOTEST CLICKVIEW", information);
            try {
                JSONObject jsonObject = new JSONObject(information);
                String data1 = "";
                if (jsonObject.has("data")) {
                    data1 = jsonObject.getString("data");
                }
                //Log.d("data1 CLICKVIEW", data1);
                if (data1 != null && !(data1.isEmpty())) {
                    final String[] args = data1.split("@", 3);
                    final int resID = mInstance.getResources().getIdentifier(args[0], "id", mInstance.getPackageName());
                    getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (args[1].equals("W")) {
                                    int vno = Integer.parseInt((String) args[2]);
                                    List<View> vlist = getWindowManagerViews();
                                    if (vlist.size()>vno) {
                                        vlist.get(vno).findViewById(resID).performClick();
                                    }
                                } else if (args[1].equals("A")) {
                                    getCurrentActivity().findViewById(resID).performClick();
                                } else {
                                    getVisibleViewFromCurrentFragmentActivity().findViewById(resID).performClick();
                                }

                            } catch (Exception e) {
                                Log.d("SIOTEST","clickinview "+e.toString());
                            }

                        }
                    });
                }
            } catch (JSONException e) {
                //Log.d("Exception in broadCast", e.toString());
            }
        } else if (message.equalsIgnoreCase("set inview")) {
            String information = data.getString("info");
            //Log.d("SIOTEST CLICKVIEW", information);
            try {
                JSONObject jsonObject = new JSONObject(information);
                String data1 = "";
                if (jsonObject.has("data")) {
                    data1 = jsonObject.getString("data");
                }
                //Log.d("data1 CLICKVIEW", data1);
                if (data1 != null && !(data1.isEmpty())) {
                    final String[] args = data1.split("@", 4);
                    final int resID = mInstance.getResources().getIdentifier(args[0], "id", mInstance.getPackageName());
                    getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (args[1].equals("W")) {
                                    int vno = Integer.parseInt((String) args[2]);
                                    List<View> vlist = getWindowManagerViews();
                                    if (vlist.size()>vno) {
                                        ((TextView) vlist.get(vno).findViewById(resID)).setText(args[3]);
                                    }
                                } else if (args[1].equals("A")) {
                                    ((TextView) getCurrentActivity().findViewById(resID)).setText(args[3]);
                                } else {
                                    ((TextView) getVisibleViewFromCurrentFragmentActivity().findViewById(resID)).setText(args[3]);
                                }
                            } catch (Exception e) {
                                Log.d("SIOTEST","setinview "+e.toString());
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                //Log.d("Exception in broadCast", e.toString());
            }
        }
    }

    private static View getVisibleViewFromCurrentFragmentActivity() {
        FragmentManager fragmentManager = ((AppCompatActivity)getCurrentActivity()).getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment.getView();
        }
        return null;
    }

    private static void sendRemoteDialerStop(String message) {
        if (CommonUtils.isNetworkAvailable(applicationContext)) {
            if(!SmarterSMBApplication.connectedCustomerInProcess){
                SmarterSMBApplication.connectedCustomerInProcess = true;
            }
            new APIProvider.GetRemoteDialerStop(message, 0, null, "Stopping. Please wait..", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        }
    }

    private static void showHomeScreen() {

        String activityToStart = "smarter.uearn.money.activities." + "UearnHome";
        Class<?> c = null;
        try {
            c = Class.forName(activityToStart);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        final Class<?> finalc = c;

        Intent intent = new Intent(applicationContext, finalc);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        applicationContext.startActivity(intent);

    }

    private static void sendDisconnectMessageToLocalBroadcastReceiver(String message) {
        //Log.d("PushNotification", "sendDisconnectMessageToLocalBroadcastReceiver " + message);
        Intent intent = new Intent("connected-customer-push-event");
        intent.putExtra("connectedcustomer", message);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    };

    private static void sendMessageToLocalBroadcastReceiver(String message) {
        Intent intent = new Intent("connected-customer-push-event");
        intent.putExtra("connectedcustomer",message);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    private static void sendMessageToSmbHome(String message) {
        Intent intent = new Intent("remote-dialer-request-event");
        intent.putExtra("result", message);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    private static void sendMessageToSmbHome(String message, String getUrl, String ttsEnable, String showCancel) {
        Intent intent = new Intent("remote-dialer-request-event");
        intent.putExtra("geturl", getUrl);
        intent.putExtra("ttsenable", ttsEnable);
        intent.putExtra("showcancel", showCancel);
        intent.putExtra("result", message);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    private static boolean shouldRecord() {
        return ApplicationSettings.getPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
    }

    private static void startRecording() {
        if (shouldRecord()) {
            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
            if (connectedCustomer != null && !connectedCustomer.isEmpty()) {
                ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, connectedCustomer);
                if(applicationContext != null) {
                    Intent recorderIntent = new Intent(applicationContext, RecorderService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        applicationContext.startForegroundService(recorderIntent);
                    } else {
                        applicationContext.startService(recorderIntent);
                    }
                }
            }
        }
    }

    public static void stopRecording(String customerNumber) {
        if (shouldRecord()) {
            //Log.d("NewChanges", "SmarterSMBApplication - stopRecording()- on");
            if(applicationContext != null) {
                Intent recorderIntent = new Intent(applicationContext, RecorderService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    applicationContext.startForegroundService(recorderIntent);
                } else {
                    applicationContext.startService(recorderIntent);
                }
            }
        } else {
            //Log.d("NewChanges", "SmarterSMBApplication - stopRecording()- off");
            ServiceHandler.callDisconnected = true;
            ServiceHandler.processCompleted = true;
            navigateToACPScreen();
        }
    }

    private static void showACPScreen() {
        String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
        String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"");
        NotificationData.isSocketResponse = true;
        if (shouldRecord()) {
            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
            Log.d("PredictiveTesting", "connectedCustomer OUTSIDE" + connectedCustomer);
            if (connectedCustomer != null && !connectedCustomer.isEmpty()) {
                ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, connectedCustomer);
                NotificationData.dialledCustomerNumber = connectedCustomer;
                if(applicationContext != null) {
                    Log.d("PredictiveTesting", "Starting service for recording of" + connectedCustomer);
                    Intent recorderIntent = new Intent(applicationContext, RecorderService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        applicationContext.startForegroundService(recorderIntent);
                    } else {
                        applicationContext.startService(recorderIntent);
                    }
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

            Intent intent = new Intent(applicationContext, finalc);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if(qanda != null && !qanda.isEmpty()) {
                intent.putExtra("showinprogress", 1);
            }

            Log.d("PredictiveTesting", "System Alert - applicationContext.startActivity");
            applicationContext.startActivity(intent); // Temporary commented for Black Buck
        }
    }

    public static void sendMessageToGcmListenerService(String message, String data) {
        Log.d("SIOTEST", "sendMessageToGcmListenerService "+message+ "Data" +data);
        Intent intent = new Intent("smb-sio-push-event");
        intent.putExtra("pushnotifmessage",message);
        intent.putExtra("pushnotifdata",data);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    private static void navigateToACPScreen() {
        String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
        String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");

        if (shouldRecord()) {
            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
            //Log.d("SMBHOME", "connectedCustomer OUTSIDE" + connectedCustomer);
            if (connectedCustomer != null && !connectedCustomer.isEmpty()) {
                ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, connectedCustomer);
                NotificationData.dialledCustomerNumber = connectedCustomer;
                //Log.d("SMBHOME", "Starting service for recording of" + connectedCustomer);
                Intent recorderIntent = new Intent(applicationContext, RecorderService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    applicationContext.startForegroundService(recorderIntent);
                } else {
                    applicationContext.startService(recorderIntent);
                }
            }
        }

        if (screen != null && !screen.isEmpty()) {
            String activityToStart = "smarter.uearn.money.activities." + screen;
            Class<?> c = null;
            try {
                c = Class.forName(activityToStart);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            final Class<?> finalc = c;

            Intent intent = new Intent(applicationContext, finalc);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (qanda != null && !qanda.isEmpty()) {
                intent.putExtra("showinprogress", 1);
            }
            //Log.d("NewChanges","startActivity");
            //Log.d("NavigateToACP","startActivity");

            boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
            if(truePredictive) {
                if(SmarterSMBApplication.navigateToACP) {
                    //Log.d("WrapupTime", "SmarterSMBApplication.navigateToACP IF" + SmarterSMBApplication.navigateToACP);
                } else {
                    //Log.d("WrapupTime", "ServiceHandler - Navigating to ACP5");
                    SmarterSMBApplication.navigateToACP = true;
                    applicationContext.startActivity(intent);
                }
            } else {
                SmarterSMBApplication.navigateToACP = true;
                applicationContext.startActivity(intent);
            }
        }
    }

    public static void startDataLoadingActivity() {
        //killCall(applicationContext);
        MySql dbHelper = MySql.getInstance(applicationContext);
        db = dbHelper.getWritableDatabase();
        AppConstants.RESET_DEVICE = false;
        ApplicationSettings.putPref(AppConstants.REMOTE_AUTO_DIALLING, "");
        ApplicationSettings.putPref(AppConstants.TRUE_PREDICTIVE, false);
        SmarterSMBApplication.connectedCustomerState = "";
        if (db.isOpen()) {
            if (CommonUtils.isNetworkAvailable(applicationContext)) {
                final String dbPath = db.getPath();
                final String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(applicationContext, "Hi, your New Database to call is getting Downloaded, Please wait.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(applicationContext, DataLoadingActivity.class);
                        intent.putExtra("EXTRA_ID", userId);
                        intent.putExtra("EXTRA_DB_PATH", dbPath);
                        //intent.putExtra("EXTRA_BOOTABLE", true);
                        Log.d("DataReload", "Hi, your New Database to call is getting Downloaded, Please wait.");
                        applicationContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                });
                //Toast.makeText(applicationContext, "Hi, your New Database to call is getting Downloaded, Please wait.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void navigateToUearnActivity() {
        String activityToStart = "smarter.uearn.money.activities.UearnActivity";
        Class<?> c = null;
        try {
            c = Class.forName(activityToStart);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        final Class<?> finalc = c;

        Intent intent = new Intent(applicationContext, finalc);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("showinprogress", 1);
        applicationContext.startActivity(intent);
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

    public static void startSmbSio() {

        String prefUrl = ApplicationSettings.getPref(AppConstants.SIOPUSH_URL, "http://www.youearn.in:4001");
        Log.d("SOCKETIO", "Called startSmbSio()" + "PushSIO URL: " + pushSioUrl);
        if (pushSioUrl != null && !pushSioUrl.isEmpty()) {
            if (prefUrl.equalsIgnoreCase(pushSioUrl)) {
                if (mSocket != null && !mSocket.connected()) {
                    connectToPushServer();
                } else {
                    if (mSocket == null) {
                        connectToPushServer();
                    }
                }
            } else {
                pushSioUrl =  prefUrl;

                if (mSocket != null && mSocket.connected()) {
                    try {
                        Log.d("SOCKETIO", "Called startSmbSio() - mSocket disconnected" + "PushSIO URL: " + pushSioUrl);
                        mSocket.disconnect();
                        mSocket = null;
                    } catch (Exception e) {
                        Log.e("SOCKETIO", "exception: " + e.getMessage());
                        Log.e("SOCKETIO", "exception: " + e.toString());
                    }
                }
                connectToPushServer();
            }
        } else {
            pushSioUrl =  prefUrl;
            connectToPushServer();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        applicationContext = this;
        appSettings = new AppSettings();
        appSettings.loadfromAppConstants();
        loadCmailUser();

        startSmbSio();

        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);

        /*Facebook App tracker*/
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public static String getCurrentSmState() {
        String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        if(userStatus == null || userStatus.isEmpty()){
            userStatus = "NEW";
        }
        return userStatus.toUpperCase();
    }

    public static String getNextActivityName() {
        String nextActString = "";
        JSONObject smStateToScreen = new JSONObject();
        try {
            smStateToScreen.put("NEW", "smarter.uearn.money.activities.JoinUsActivity");
            smStateToScreen.put("UNREGISTERED", "smarter.uearn.money.activities.JoinUsActivity");
            smStateToScreen.put("REGISTERED", "smarter.uearn.money.activities.WelcomeActivity");
            smStateToScreen.put("READY FOR TESTS", "smarter.uearn.money.activities.ProcessSelectionActivity");
            smStateToScreen.put("VOICE TEST", "smarter.uearn.money.activities.ProcessSelectionActivity");
            smStateToScreen.put("EMAIL TEST", "smarter.uearn.money.activities.ProcessSelectionActivity");
            smStateToScreen.put("CHAT TEST", "smarter.uearn.money.activities.ProcessSelectionChatActivity");
            smStateToScreen.put("ON BOARDING", "smarter.uearn.money.activities.homepage.HomeActivity");
            smStateToScreen.put("IN TRAINING", "smarter.uearn.money.activities.homepage.HomeActivity");
            smStateToScreen.put("DOCUMENTS PENDING", "smarter.uearn.money.activities.homepage.HomeActivity");
            smStateToScreen.put("SUBMIT IDENTITY", "smarter.uearn.money.activities.homepage.HomeActivity");
            smStateToScreen.put("SUBMIT PROFILE", "smarter.uearn.money.activities.homepage.HomeActivity");
            smStateToScreen.put("SERVICE AGREEMENT", "smarter.uearn.money.activities.homepage.HomeActivity");
            smStateToScreen.put("ON BOARDED", "smarter.uearn.money.activities.UearnHome");
            smStateToScreen.put("OJT", "smarter.uearn.money.activities.UearnHome");
            smStateToScreen.put("PRODUCTION", "smarter.uearn.money.activities.UearnHome");
            smStateToScreen.put("PROJECT", "smarter.uearn.money.activities.UearnHome");
            smStateToScreen.put("PROJECT ASSIGNED", "smarter.uearn.money.activities.UearnHome");
            smStateToScreen.put("PRODUCTION-VOICE", "smarter.uearn.money.activities.UearnHome");
            smStateToScreen.put("PRODUCTION-CHAT", "smarter.uearn.money.activities.UearnHome");
            smStateToScreen.put("ON BOARD", "smarter.uearn.money.activities.UearnHome");
        } catch (JSONException e2) {

        }

        if (!SmartUser.getSmStateToScreen().isEmpty()) {
            try {
                smStateToScreen = new JSONObject(SmartUser.getSmStateToScreen());
            } catch (JSONException e1) {
            }
        }

        try {
            nextActString = smStateToScreen.getString(getCurrentSmState());
        } catch (JSONException e) {

        }

        Log.d("getNextActivityName", "Landing Screen " + nextActString + " "+getCurrentSmState());

        return nextActString;
    }

    public static SmarterSMBApplication getInstance() throws NullPointerException {
        if (mInstance == null)
            throw new NullPointerException(" Error to create Uearn Application instance");
        return mInstance;
    }

    public static void loadCmailUser() {
        boolean isLogout = appSettings.getLogout();
        if (!isLogout) {
            SmartUser = new SmartUser();
            SmartUser.loadfromAppConstants();
            googleAccountsList = new GoogleAccountsList();
            googleAccountsList.loadGmailAccounts();
            salesStageInfo = new SalesStageInfo();
            salesStageInfo.loadSalesStage();
        }
    }

    public String getSDCard() {
        return Environment.getExternalStorageDirectory().toString() + "/";
    }

    public String getCallRecorderPath() {
        return String.format("%sCallRecorder/", getSDCard());
        // return "/mnt/sdcard0/CallRecorder";
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    private static String getLatestStatus(String number) {
        String latestStatus = "";
        try {
            MySql dbHelper = MySql.getInstance(applicationContext);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            String selection = "TO1=" + "'" + number + "'";
            Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                latestStatus = cursor.getString(cursor.getColumnIndex("STATUS"));
            }
            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return latestStatus;
    }

    public static void sendCallDisconnectInfoToServer(String customerNumber, String source) {
        //Log.d("TruePredictiveTest", "sendCallDisconnectInfoToServer " + customerNumber);
        JSONObject jsonObject = new JSONObject();
        try {
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")) {
                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
            }
            jsonObject.put("number", customerNumber);
            jsonObject.put("transactionid", NotificationData.transactionId);
            jsonObject.put("source", source);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataUploadUtils.postCallDisconnect(Urls.postCallDisconnectUrl(), jsonObject);
    }

    public static void callDisconnect() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager tm = (TelecomManager) applicationContext.getSystemService(Context.TELECOM_SERVICE);
                if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (tm != null) {
                    tm.endCall();
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
                Class classTelephony = Class.forName(telephonyManager.getClass().getName());
                Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
                methodGetITelephony.setAccessible(true);
                Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
                Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
                methodEndCall.invoke(telephonyInterface);
            }
        } catch (Exception e) {
            Log.e("SmarterSMBApplication", "Error calling ITelephony#endActiveCall()", e);
            int version_code = CommonUtils.getVersionCode(applicationContext);
            String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                    ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>SmarterSMBApplication - Error calling ITelephony#endActiveCall(): " + e.getMessage();
            ServiceApplicationUsage.callErrorLog(message);
        }
    }

    private static void sendSalesEntriesInfoToServer(String query) {
        //String query = "SELECT * FROM SmarterContact";
        String salesStatusFromDb = getDBEntriesFromDB(query);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")){
                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
            }
            jsonObject.put("salesstatusentries", salesStatusFromDb);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject != null) {
            DataUploadUtils.postSalesEntries(Urls.postSalesEntriesUrl(), jsonObject);
        }
    }

    public static void sendDBEntriesInfoToServer(String query) {
        String dbEntriesFromDb = getDBEntriesFromDB(query);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")){
                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
            }
            jsonObject.put("query", query);
            jsonObject.put("response", dbEntriesFromDb);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject != null) {
            DataUploadUtils.postDBEntries(Urls.postDBEntriesUrl(), jsonObject);
        }
    }

    public static String getDBEntriesFromDB(String query) {
        MySql dbHelper = MySql.getInstance(applicationContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null && cursor.getCount() > 0) {
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

    public static void sendLogcatViaMail(String command) {
        File outputFile = new File(Environment.getExternalStorageDirectory(), "logcat.txt");
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {

            Process process = Runtime.getRuntime().exec(command);
            reader = new InputStreamReader(process.getInputStream());

            // write output stream
            writer = new FileWriter(outputFile);
            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                if (n == -1)
                    break;
                writer.write(buffer, 0, n);
            } while (true);

            reader.close();
            writer.close();
            String data = FileUtils.readFileToString(outputFile, "UTF-8");
            ServiceApplicationUsage.callErrorLog(data);
        } catch (IOException e) {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            e.printStackTrace();
            return;
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

    private static String getCustomerName(String number) {
        String customerName = "";
        try {
            MySql dbHelper = MySql.getInstance(applicationContext);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();

            String selection = "TO1=" + "'" + number + "'";
            Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT ASC");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                customerName = cursor.getString(cursor.getColumnIndex("TONAME"));
            }
            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerName;
    }

    private static void sendConnectedCustomerInfoToServer(String customerNumber){
        SmarterSMBApplication.connectedCustomerState = "Connecting";
        JSONObject jsonObject = new JSONObject();
        try {
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")){
                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
            }
            jsonObject.put("number", customerNumber);
            jsonObject.put("transactionid", NotificationData.transactionId);
            jsonObject.put("from", "SmarterSMBApplication");
            jsonObject.put("state", "Connecting");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("NavigateToACP","sendConnectedCustomerInfoToServer" + customerNumber);
        //Log.d("NavigateToACP","sendConnectedCustomerInfoToServer" + customerNumber + "State "+ "Connecting");
        DataUploadUtils.postIvrCustomerMapping(Urls.postIvrCustomerMappingUrl(), jsonObject);
    }

    static class ConnectedCustomerConnectedInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String customerNumber = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
            sendConnectedCustomerInfoToServer(customerNumber);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

/*
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            MultiDex.install(this);
        } catch (RuntimeException e) {

        }
    }
*/

    private Thread.UncaughtExceptionHandler defaultUEH;

    // handler listener
    private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();

            String errorValue = "<br/>User ID: " + ApplicationSettings.getPref(AppConstants.USERINFO_ID, "")
                    + "<br/>User email: " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "")
                    + "<br/><br/>Stack trace: " + ex.getStackTrace()
                    + "<br/>Localized Message: " + ex.getLocalizedMessage()
                    + "<br/>Message: " + ex.getMessage()
                    + "<br/>Cause: " + ex.getCause();

            for (int ct = 0; ct < 6; ct++) {
                if (ex.getStackTrace()[ct] != null) {
                    errorValue += "<br/><br/>Class name: " + ex.getStackTrace()[ct].getClassName()
                            + "<br/>Method name: " + ex.getStackTrace()[ct].getMethodName()
                            + "<br/>Line number: " + ex.getStackTrace()[ct].getLineNumber();
                } else {
                    break;
                }
            }
            int version_code = CommonUtils.getVersionCode(applicationContext);
            errorValue += "<br/> Current Activity: "+currentActivity;
            errorValue += "<br/> App Version: "+version_code;
            ServiceApplicationUsage.callErrorLog(errorValue);
        }
    };

    public static String countNotification;
    public static void newMessageReceived(final String message, final String title, final String actionString){
        final long[] notificationCount = {ApplicationSettings.getLongPref(AppConstants.notificationCount, 0)};
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(applicationContext, "New notification received", Toast.LENGTH_SHORT).show();
                showNotificationAlertDialog(message, title);
                //showAlertDialog(applicationContext, message, title, actionString);
                ApplicationSettings.putLongPref(AppConstants.notificationCount, notificationCount[0] + 1);
                notificationCount[0] = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
                Log.e("NewMessageReceived","notificationCount :: "+notificationCount[0]);
                countNotification = String.valueOf(notificationCount[0]);
                //UearnHome.tv_message_count.setText(countNotification);
                Log.e("NewMessageReceived","countNotification :: "+countNotification);
            }
        });
    }

    public static String countTrainingNotification;
    public static void newTrainerMessageReceived(final String message, final String title, final String actionString){
        final long[] notificationCount = {ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0)};
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(applicationContext, "New notification received", Toast.LENGTH_SHORT).show();
                showAlertDialog(applicationContext, message, title, actionString);
                ApplicationSettings.putLongPref(AppConstants.trainingnotificationCount, notificationCount[0] + 1);
                notificationCount[0] = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
                Log.e("NewMessageReceived","notificationCount :: "+notificationCount[0]);
                countTrainingNotification = String.valueOf(notificationCount[0]);
                //UearnHome.tv_message_count.setText(countNotification);
                Log.e("NewMessageReceived","countNotification :: "+countTrainingNotification);
            }
        });
    }

    private static void showNotificationAlertDialog(String message, String title) {

        try {

            final Dialog dialog = CustomSingleButtonDialog.buildAlertDialog(title,message, SmarterSMBApplication.getCurrentActivity(), false);
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

    public static void showAlertDialog(Context context, String message, String title, String actionString) {
        final Intent intent4 = new Intent(applicationContext, MyStopAlertDialog.class);
        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent4.putExtra("NOTIFICATIONMSGG", message);
        intent4.putExtra("TITLENOTIFICATION",title);
        intent4.putExtra("NOTIFICATIONACTION",actionString);
        applicationContext.startActivity(intent4);
    }

    public static void moveToRADStop(final String message, final String title,
                                     final String actionString, final String stopString){
        final Intent intent4 = new Intent(applicationContext, MyStopAlertDialog.class);
        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent4.putExtra("NOTIFICATIONMSGG", message);
        intent4.putExtra("TITLENOTIFICATION",title);
        intent4.putExtra("NOTIFICATIONACTION",actionString);
        SmarterSMBApplication.moveToRADSTOP = true;
        SmarterSMBApplication.stopRADMsg = stopString;
        sendRemoteDialStopRequest(stopString);
        applicationContext.startActivity(intent4);
    }

    public static void sendRemoteDialStopRequest(String message) {
        Log.e("MyStopAlert", "message :: " + message);
        new APIProvider.GetRemoteDialerStop(message, 0, null, "Stopping. Please wait..", new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {

                Log.e("MyStopAlert", "data :: " + data);
            }
        }).call();
    }


}
