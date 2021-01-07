package smarter.uearn.money.views.events;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.android.internal.telephony.ITelephony;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import smarter.uearn.money.activities.UearnActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.KnowlarityCallLogs;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;
import static smarter.uearn.money.utils.CommonUtils.getLastCustomerNumber;
import static smarter.uearn.money.utils.SmarterSMBApplication.applicationContext;


public class MyPhoneStateListener extends PhoneStateListener {

    public static String NUMBER = "";
    public static final int INCOMING_CALL_ANSWERED = 0;
    public static final int OUTGOING_CALL_DIALED = 1;
    public static final int MISSED_CALL = 2;
    public static final int CALL_ENDED = 3;
    public static final int CALL_RECORDING = 4;
    public static final int AGENT_OR_CUSTOMER_DISCONNECT = 5;
    private Handler handler;
    private String incomingNmr;
    boolean ring = false, offhook = false, idle = false;
    public static boolean inOffhook;
    public static boolean waitingCallEnd = false;
    public Context context1;
    public static Long currentTime = 0L;
    String numberToSend = "";
    public static boolean callEndedFromPhoneStateListener = false;
    boolean isCounterRunning = false;

    private CountDownTimer countDownTimer;

    public static boolean callEnded = false;
    public static String cloud_number2 = "";

    public MyPhoneStateListener(Context context) {
        HandlerThread thread = new HandlerThread("PhoneEventsHandler");
        thread.start();
        Looper mServiceLooper = thread.getLooper();
        handler = new ServiceHandler(mServiceLooper, context);
        context1 = context;
    }

    @RequiresPermission(android.Manifest.permission.MODIFY_PHONE_STATE)
    public void answerCall() {
        try {
            SmarterSMBApplication.deviceSupportsAutoAnswer = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager tm = (TelecomManager) context1.getSystemService(Context.TELECOM_SERVICE);
                if (ActivityCompat.checkSelfPermission(context1, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (tm != null) {
                    tm.acceptRingingCall();
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context1.getSystemService(Context.TELEPHONY_SERVICE);
                Class classTelephony = Class.forName(telephonyManager.getClass().getName());
                Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
                methodGetITelephony.setAccessible(true);
                Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
                Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                Method methodAnswerCall = telephonyInterfaceClass.getDeclaredMethod("answerRingingCall");
                methodAnswerCall.invoke(telephonyInterface);
            }
        } catch (Exception e) {
            SmarterSMBApplication.deviceSupportsAutoAnswer = false;
            Log.e("MyPhoneStateListener", "Error calling ITelephony#answerCall()", e);
            int version_code = CommonUtils.getVersionCode(context1);
            String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                    ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>PhoneStateListener - Error calling ITelephony#answerCall(): " + e.getMessage();
            ServiceApplicationUsage.callErrorLog(message);
        }
    }

    @RequiresPermission(android.Manifest.permission.MODIFY_PHONE_STATE)
    public void rejectCall() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager tm = (TelecomManager) context1.getSystemService(Context.TELECOM_SERVICE);
                if (ActivityCompat.checkSelfPermission(context1, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (tm != null) {
                    tm.endCall();
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context1.getSystemService(Context.TELEPHONY_SERVICE);
                Class classTelephony = Class.forName(telephonyManager.getClass().getName());
                Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
                methodGetITelephony.setAccessible(true);
                Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
                Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
                methodEndCall.invoke(telephonyInterface);
            }
        } catch (Exception e) {
            int version_code = CommonUtils.getVersionCode(context1);
            String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                    ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>PhoneStateListener - Error calling ITelephony#rejectCall(): " + e.getMessage();
            ServiceApplicationUsage.callErrorLog(message);
        }
    }

    private static void sendMessageToUearnHome(String message) {
        Intent intent = new Intent("remote-dialer-request-event");
        intent.putExtra("result", message);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    private static void sendMessageToUearnActivity(String message) {
        Intent intent = new Intent("connected-customer-push-event");
        intent.putExtra("result", message);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    private void incomingCallLanding() {
        SmarterSMBApplication.incomingCallLandingToDevice = true;
        SmarterSMBApplication.callEndedFromDuringCall = false;
        SmarterSMBApplication.callStateIsDisconnected = false;
        if(SmarterSMBApplication.oneCallIsActive){
            SmarterSMBApplication.oneCallISActiveNextCallLanded = true;
        } else {
            if(SmarterSMBApplication.isRemoteDialledStart) {
                SmarterSMBApplication.oneCallIsActive = true;
            }
        }
    }

    private void incomingCallLanding(String incomingNumber) {
        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
            String cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
            if (cloud1 != null && cloud1.contains(incomingNumber) && !SmarterSMBApplication.currentAppState.equals("ACP") && !SmarterSMBApplication.oneCallIsActive){
                SmarterSMBApplication.incomingCallLandingToDevice = true;
                SmarterSMBApplication.callEndedFromDuringCall = false;
                SmarterSMBApplication.callStateIsDisconnected = false;
                if (SmarterSMBApplication.oneCallIsActive) {
                    SmarterSMBApplication.oneCallISActiveNextCallLanded = true;
                } else {
                    if(SmarterSMBApplication.currentStateIsStartMode) {
                        SmarterSMBApplication.oneCallIsActive = true;
                    } else {
                        if(SmarterSMBApplication.webViewOutgoingCallEventTriggered) {
                            sendMessageToUearnHome("connecting_call");
                        }
                    }
                }
            }
        }
    }

    private void callStateIdle(){
        SmarterSMBApplication.callStateIsDisconnected = true;
        if(SmarterSMBApplication.callEndedFromDuringCall) {
            SmarterSMBApplication.callEndedFromDuringCall = false;
            SmarterSMBApplication.incomingCallRejectedByAgent = false;
        } else {
            if(SmarterSMBApplication.incomingCallAcceptedByAgent){
                SmarterSMBApplication.incomingCallRejectedByAgent = false;
            } else {
                SmarterSMBApplication.incomingCallRejectedByAgent = true;
            }
        }
    }

    private void callStateRinging() {
        SmarterSMBApplication.incomingCallLandingToDevice = true;
        SmarterSMBApplication.callEndedFromDuringCall = false;
        SmarterSMBApplication.callStateIsDisconnected = false;
        if(SmarterSMBApplication.oneCallIsActive){
            SmarterSMBApplication.oneCallISActiveNextCallLanded = true;
        } else {
            if(SmarterSMBApplication.isRemoteDialledStart || SmarterSMBApplication.stayAtHomeScenario) {
                SmarterSMBApplication.oneCallIsActive = true;
            }
        }
    }

    private void callStateOffhook() {
        if(SmarterSMBApplication.isRemoteDialledStart || SmarterSMBApplication.isDiallingFollowUpC2C || SmarterSMBApplication.currentStateIsStartMode ||SmarterSMBApplication.remoteEnabledRedialScenario) {
            SmarterSMBApplication.incomingCallAcceptedByAgent = true;
            SmarterSMBApplication.legADisconnected = false;
            if(SmarterSMBApplication.isRemoteDialledStart || SmarterSMBApplication.currentStateIsStartMode) {
                String cloudNumber2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                if(cloud_number2 != null && !cloud_number2.isEmpty() && cloudNumber2.contains(cloud_number2)){
                    SmarterSMBApplication.currentAppState = "";
                    SmarterSMBApplication.cloud2IncomingCall = true;
                    SmarterSMBApplication.inboundCallInOutbound = true;
                    sendMessageToUearnActivity("call_answered");
                }else{
                    if(SmarterSMBApplication.stayAtHomeScenario) {
                        if(SmarterSMBApplication.manualDialScenario || SmarterSMBApplication.isDiallingFollowUpC2C || SmarterSMBApplication.isDiallingFromDoneList) {
                            sendMessageToUearnHome("connecting_call");
                        }
                    } else {
                        SmarterSMBApplication.currentAppState = "Connecting";
                        SmarterSMBApplication.cloud2IncomingCall = false;
                        SmarterSMBApplication.inboundCallInOutbound = false;
                        SmarterSMBApplication.postInboundDataToServer = true;
                        sendMessageToUearnActivity("launch_from_recent");
                    }
                }

            } else {
                String cloudNumber2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                if(cloud_number2 != null && !cloud_number2.isEmpty() && cloudNumber2.contains(cloud_number2)){
                    SmarterSMBApplication.currentAppState = "";
                    SmarterSMBApplication.cloud2IncomingCall = true;
                    SmarterSMBApplication.inboundCallInOutbound = true;
                    sendMessageToUearnActivity("call_answered");
                }else {
                    if (SmarterSMBApplication.stayAtHomeScenario && SmarterSMBApplication.oneCallIsActive) {
                        SmarterSMBApplication.relaunchSameActivityStayHome = false;
                        sendMessageToUearnActivity("call_answered");
                    } else {
                        sendMessageToUearnHome("connecting_call");
                    }
                }
            }
        } else {
            if(SmarterSMBApplication.webViewOutgoingCallEventTriggered) {
                SmarterSMBApplication.incomingCallAcceptedByAgent = true;
                sendMessageToUearnHome("connecting_call");
            }
        }
    }

    private void callStateIdleFK(){
        SmarterSMBApplication.callStateIsDisconnected = true;
        SmarterSMBApplication.currentModeIsConnected = false;
        if(SmarterSMBApplication.callEndedFromDuringCall) {
            SmarterSMBApplication.callEndedFromDuringCall = false;
            SmarterSMBApplication.incomingCallRejectedByAgent = false;
        } else {
            if(SmarterSMBApplication.incomingCallAcceptedByAgent){
                SmarterSMBApplication.incomingCallRejectedByAgent = false;
            } else {
                SmarterSMBApplication.incomingCallRejectedByAgent = true;
                if (SmarterSMBApplication.currentAppState.equals("Connecting") && SmarterSMBApplication.isInNumberMatching && SmarterSMBApplication.isRemoteDialledStart){
                    SmarterSMBApplication.oneCallIsActive = false;
                    SmarterSMBApplication.incomingCallRejectedByAgent = false;
                }
            }
        }
        if(SmarterSMBApplication.isInNumberMatching && !SmarterSMBApplication.autoCallDisconnected) {
            if(SmarterSMBApplication.isRemoteDialledStart && SmarterSMBApplication.incomingCallAcceptedByAgent) {
                sendMessageToUearnActivity("call_disconnected");
            }
        } else {
            SmarterSMBApplication.incomingCallRejectedByAgent = false;
        }
    }

    private void callStateRingingFK(String incomingNumber) {
        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
            if (fkControl) {
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                    String cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                    if (cloud1 != null && cloud1.contains(incomingNumber) && !SmarterSMBApplication.oneCallIsActive){
                        SmarterSMBApplication.isInNumberMatching = true;
                        incomingCallLanding();
                        if(SmarterSMBApplication.isRemoteDialledStart) {
                            if (ApplicationSettings.containsPref(AppConstants.AUTO_ANSWER)) {
                                boolean autoAnswer = ApplicationSettings.getPref(AppConstants.AUTO_ANSWER, false);
                                if (autoAnswer) {
                                    answerCall();
                                }
                            }
                            if(SmarterSMBApplication.deviceSupportsAutoAnswer) {
                                SmarterSMBApplication.deviceSupportsAutoAnswer = false;
                                sendMessageToUearnActivity("call_answered");
                            }
                        }
                    } else {
                        SmarterSMBApplication.isInNumberMatching = false;
                        if(SmarterSMBApplication.isRemoteDialledStart && SmarterSMBApplication.oneCallIsActive) {
                            rejectCall();
                        } else if(SmarterSMBApplication.isRemoteDialledStart && SmarterSMBApplication.autoCallDisconnected){
                            rejectCall();
                        } else if(SmarterSMBApplication.isRemoteDialledStart && cloud1 != null && !cloud1.contains(incomingNumber)){
                            rejectCall();
                        }
                    }
                } else {
                    incomingCallLanding();
                }
            } else {
                incomingCallLanding();
            }
        } else {
            incomingCallLanding();
        }
    }

    private void callStateOffhookFK(String incomingNumber) {
        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
            String cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
            if (cloud1 != null && cloud1.contains(incomingNumber)) {
                SmarterSMBApplication.isInNumberMatching = true;
                incomingCallLanding(incomingNumber);
                if (SmarterSMBApplication.currentStateIsStartMode) {
                    if (SmarterSMBApplication.currentAppState.equals("Connected") && SmarterSMBApplication.oneCallIsActive) {
                        SmarterSMBApplication.launchAppFromRecentTasks = true;
                        sendMessageToUearnActivity("launch_from_recent");
                    } else {
                        SmarterSMBApplication.incomingCallAcceptedByAgent = true;
                        SmarterSMBApplication.legADisconnected = false;
                        sendMessageToUearnActivity("call_answered");
                    }
                }
            } else {
                SmarterSMBApplication.isInNumberMatching = false;
                if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.autoCallDisconnected) {
                    rejectCall();
                } else if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.currentAppState.equals("ACP") && cloud1 != null && !cloud1.contains(incomingNumber)) {
                    rejectCall();
                } else {
                    SmarterSMBApplication.launchAppFromRecentTasks = true;
                    sendMessageToUearnActivity("launch_from_recent");
                }
            }
        }
    }

    private void callStateIdleIB(){
        SmarterSMBApplication.callStateIsDisconnected = true;
        if(SmarterSMBApplication.callEndedFromDuringCall) {
            SmarterSMBApplication.callEndedFromDuringCall = false;
            SmarterSMBApplication.incomingCallRejectedByAgent = false;
        } else {
            if(SmarterSMBApplication.incomingCallAcceptedByAgent){
                SmarterSMBApplication.incomingCallRejectedByAgent = false;
            } else {
                if(SmarterSMBApplication.currentStateIsStartMode) {
                    SmarterSMBApplication.incomingCallRejectedByAgent = true;
                }
            }
        }
        if(SmarterSMBApplication.isInNumberMatching && !SmarterSMBApplication.autoCallDisconnected) {
            if(SmarterSMBApplication.currentStateIsStartMode) {
                sendMessageToUearnActivity("call_disconnected");
            } else {
                if(SmarterSMBApplication.incomingCallAcceptedByAgent) {
                    SmarterSMBApplication.matchingInNumberNotInStartMode = true;
                } else {
                    SmarterSMBApplication.matchingInNumberNotInStartMode = false;
                }
            }
        } else {
            SmarterSMBApplication.incomingCallRejectedByAgent = false;
        }
    }

    private void callStateRingingIB(String incomingNumber) {
        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
            if (fkControl) {
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                    String cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                    if (cloud1 != null && cloud1.contains(incomingNumber) && !SmarterSMBApplication.oneCallIsActive) {
                        SmarterSMBApplication.isInNumberMatching = true;
                        incomingCallLanding(incomingNumber);
                        if (SmarterSMBApplication.currentStateIsStartMode) {
                            if (ApplicationSettings.containsPref(AppConstants.AUTO_ANSWER)) {
                                boolean autoAnswer = ApplicationSettings.getPref(AppConstants.AUTO_ANSWER, false);
                                if (autoAnswer) {
                                    answerCall();
                                }
                            }
                            sendMessageToUearnActivity("call_answered");
                        }
                    } else {
                        SmarterSMBApplication.isInNumberMatching = false;
                        if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.oneCallIsActive) {
                            rejectCall();
                        } else if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.autoCallDisconnected) {
                            rejectCall();
                        } else if (SmarterSMBApplication.currentStateIsStartMode && cloud1 != null && !cloud1.contains(incomingNumber)) {
                            rejectCall();
                        }
                    }
                } else {
                    incomingCallLanding(incomingNumber);
                }
            } else if (ApplicationSettings.containsPref(AppConstants.AUTO_ANSWER)) {
                boolean autoAnswer = ApplicationSettings.getPref(AppConstants.AUTO_ANSWER, false);
                if (autoAnswer) {
                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                        String cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                        if (cloud1 != null && cloud1.contains(incomingNumber) && !SmarterSMBApplication.oneCallIsActive) {
                            SmarterSMBApplication.isInNumberMatching = true;
                            incomingCallLanding(incomingNumber);
                            if (SmarterSMBApplication.currentStateIsStartMode) {
                                answerCall();
                                sendMessageToUearnActivity("call_answered");
                            }
                        } else {
                            SmarterSMBApplication.isInNumberMatching = false;
                            if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.oneCallIsActive) {
                                rejectCall();
                            } else if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.autoCallDisconnected) {
                                rejectCall();
                            } else if (SmarterSMBApplication.currentStateIsStartMode && cloud1 != null && !cloud1.contains(incomingNumber)) {
                                rejectCall();
                            }
                        }
                    } else {
                        incomingCallLanding(incomingNumber);
                    }
                }
            } else {
                incomingCallLanding(incomingNumber);
            }
        } else if (ApplicationSettings.containsPref(AppConstants.AUTO_ANSWER)) {
            boolean autoAnswer = ApplicationSettings.getPref(AppConstants.AUTO_ANSWER, false);
            if (autoAnswer) {
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                    String cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                    if (cloud1 != null && cloud1.contains(incomingNumber) && !SmarterSMBApplication.oneCallIsActive) {
                        SmarterSMBApplication.isInNumberMatching = true;
                        incomingCallLanding(incomingNumber);
                        if (SmarterSMBApplication.currentStateIsStartMode) {
                            answerCall();
                            sendMessageToUearnActivity("call_answered");
                        }
                    } else {
                        SmarterSMBApplication.isInNumberMatching = false;
                        if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.oneCallIsActive) {
                            rejectCall();
                        } else if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.autoCallDisconnected) {
                            rejectCall();
                        } else if (SmarterSMBApplication.currentStateIsStartMode && cloud1 != null && !cloud1.contains(incomingNumber)) {
                            rejectCall();
                        }
                    }
                } else {
                    incomingCallLanding(incomingNumber);
                }
            } else {
                incomingCallLanding(incomingNumber);
            }
        } else {
            incomingCallLanding(incomingNumber);
        }
    }

    private void callStateOffhookIB(String incomingNumber) {
        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
            String cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
            if (cloud1 != null && cloud1.contains(incomingNumber)) {
                SmarterSMBApplication.isInNumberMatching = true;
                incomingCallLanding(incomingNumber);
                if (SmarterSMBApplication.currentStateIsStartMode) {
                    if (SmarterSMBApplication.currentAppState.equals("Connected") && SmarterSMBApplication.oneCallIsActive) {
                        SmarterSMBApplication.launchAppFromRecentTasks = true;
                        sendMessageToUearnActivity("launch_from_recent");
                    } else {
                        if (SmarterSMBApplication.currentAppState.equals("ACP")) {

                        } else {
                            SmarterSMBApplication.incomingCallAcceptedByAgent = true;
                            SmarterSMBApplication.legADisconnected = false;
                            sendMessageToUearnActivity("call_answered");
                        }
                    }
                } else if(SmarterSMBApplication.outgoingCallNotInStartMode){
                    SmarterSMBApplication.incomingCallAcceptedByAgent = true;
                }
            } else {
                SmarterSMBApplication.isInNumberMatching = false;
                if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.autoCallDisconnected) {
                    rejectCall();
                } else if (SmarterSMBApplication.currentStateIsStartMode && SmarterSMBApplication.currentAppState.equals("ACP") && cloud1 != null && !cloud1.contains(incomingNumber)) {
                    rejectCall();
                } else {
                    SmarterSMBApplication.launchAppFromRecentTasks = true;
                    sendMessageToUearnActivity("launch_from_recent");
                }
            }
        } else {
            SmarterSMBApplication.launchAppFromRecentTasks = true;
            sendMessageToUearnActivity("launch_from_recent");
        }
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        SmarterSMBApplication.curSimSignalStrength = signalStrength.toString();
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        SmarterSMBApplication.curSimService = serviceState.toString();
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (ApplicationSettings.containsPref(AppConstants.DEFAULT_DIALER)) {
            boolean defaultDialer = ApplicationSettings.getPref(AppConstants.DEFAULT_DIALER, false);
            if (defaultDialer) {
                //Log.d("DefaultDialerTest", "MyPhoneStateListener - onCallStateChanged" + state + incomingNumber);
                String cloudNumber = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                if(cloudNumber != null && !cloudNumber.isEmpty() && !cloudNumber.equals(incomingNumber)) {
                    //Log.d("DefaultDialerTest", "MyPhoneStateListener - sendMessageToUearnHome" + cloudNumber + incomingNumber);
                    ApplicationSettings.putPref(AppConstants.EXTERNAL_CUSTOMER_NUMBER, incomingNumber);
                    rejectCall();
                    if(state == 2) {
                        //Log.d("DefaultDialerTest", "MyPhoneStateListener - sendMessageToUearnHome - call_external_customer");
                        sendMessageToUearnHome("call_external_customer");
                    }
                }
            }
        }
        cloud_number2 = incomingNumber;
        if(state == 0){
            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                if (fkControl) {
                    callStateIdleFK();
                } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        callStateIdleIB();
                    } else {
                        callStateIdle();
                    }
                } else {
                    callStateIdle();
                }
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    callStateIdleIB();
                } else {
                    callStateIdle();
                }
            } else {
                callStateIdle();
            }
        } else if(state == 1){
            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                if (fkControl) {
                    callStateRingingFK(incomingNumber);
                } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        callStateRingingIB(incomingNumber);
                    } else {
                        callStateRinging();
                    }
                } else {
                    callStateRinging();
                }
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    callStateRingingIB(incomingNumber);
                } else {
                    callStateRinging();
                }
            } else {
                callStateRinging();
            }
        } else if(state == 2){
            if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
                boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
                if (fkControl) {
                    callStateOffhookFK(incomingNumber);
                } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                    boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                    if (ibControl) {
                        callStateOffhookIB(incomingNumber);
                    } else {
                        callStateOffhook();
                    }
                } else {
                    callStateOffhook();
                }
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    callStateOffhookIB(incomingNumber);
                } else {
                    callStateOffhook();
                }
            } else {
                callStateOffhook();
            }
        }

        String role = ApplicationSettings.getPref(AppConstants.USER_ROLE,"");
        if(role != null && !role.isEmpty()) {
            if (!role.equalsIgnoreCase("user") || !role.equalsIgnoreCase("non-user")) {
                return;
            }
        }

        if(incomingNumber == null || incomingNumber.isEmpty()) {
            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                    String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                    if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                        if (SmarterSMBApplication.isRemoteDialledStart) {
                            incomingNumber = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                        } else {
                            if (SmarterSMBApplication.isC2CAutoStart) {
                                incomingNumber = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                            } else {
                                return;
                            }
                        }
                    } else {
                        if (SmarterSMBApplication.isC2CAutoStart) {
                            incomingNumber = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                        } else {
                            return;
                        }
                    }
                } else {
                    return;
                }
            } else {
                incomingNumber = NotificationData.dialledCustomerNumber;
            }
        }

        if(incomingNumber != null && !incomingNumber.isEmpty()) {
            String testNumber = ApplicationSettings.getPref(AppConstants.TEST_NUMBER, "");
            if(testNumber != null && !testNumber.isEmpty()){
                if(incomingNumber.contains(testNumber)){
                    callEnded = false;
                    final String pingResponse = Utils.ping("www.google.com");
                    final String networkType = CommonUtils.getNetworkTypeAndSignalStrength(context1);
                    final String signalStrength = String.valueOf(SmarterSMBApplication.currentSignalStrength);
                    if(SmarterSMBApplication.currentAppState != null && !SmarterSMBApplication.currentAppState.isEmpty() && !SmarterSMBApplication.currentAppState.equalsIgnoreCase("Connecting")) {
                        endCall(incomingNumber);
                    }
                    callingReuploadService();
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                sendCurrentAppStateToServer(pingResponse, networkType, signalStrength);
                            }  catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
            }
        }

        String allowAllIncoming = ApplicationSettings.getPref(AppConstants.ALLOW_ALL_INCOMING, "");
        String allowAllOutgoing = ApplicationSettings.getPref(AppConstants.ALLOW_ALL_OUTGOING, "");
        if ((allowAllIncoming != null && !allowAllIncoming.isEmpty())) {
            if(allowAllIncoming.contains("true")) {
                if(SmarterSMBApplication.isRemoteDialledStart) {
                    String currentAppState = SmarterSMBApplication.currentAppState;
                    if(currentAppState != null && !currentAppState.isEmpty() && (currentAppState.equalsIgnoreCase("Connecting") || currentAppState.equalsIgnoreCase("Connected") || currentAppState.equalsIgnoreCase("ACP"))) {
                        if(state == TelephonyManager.CALL_STATE_IDLE){
                            if(SmarterSMBApplication.callEndedFromDuringCall) {
                                ServiceHandler.callDisconnected = true;
                            }
                        }
                        return;
                    } else {
                        allowCall();
                    }
                } else if(SmarterSMBApplication.isC2CAutoStart) {
                    String currentAppState = SmarterSMBApplication.currentAppState;
                    if(currentAppState != null && !currentAppState.isEmpty() && (currentAppState.equalsIgnoreCase("Connecting") || currentAppState.equalsIgnoreCase("Connected") || currentAppState.equalsIgnoreCase("ACP"))) {
                        if(state == TelephonyManager.CALL_STATE_IDLE){
                            if(SmarterSMBApplication.callEndedFromDuringCall) {
                                ServiceHandler.callDisconnected = true;
                            }
                        }
                        return;
                    } else {
                        allowCall();
                    }
                } else {
                    allowCall();
                }
            } else {
                if(allowAllOutgoing.contains("true")) {
                    return;
                } else {
                    if(callEnded) {
                        callEnded = false;
                    }
                    if(SmarterSMBApplication.isRemoteDialledStart) {
                        endCall(incomingNumber);
                    }
                }
            }
        } else {
            allowCall();
        }
        super.onCallStateChanged(state, incomingNumber);
        if(incomingNumber == null && (context1 != null)) {
            CommonUtils.callLogApi(context1, "MyPhoneStateListener for "+getLastCustomerNumber());
            incomingNumber = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
        } else if(incomingNumber != null  && (incomingNumber.isEmpty()) && (context1 != null)) {
            CommonUtils.callLogApi(context1, "MyPhoneStateListener for "+getLastCustomerNumber());
            incomingNumber = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
        }

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                if(callEndedFromPhoneStateListener) {
//                  callEndedFromPhoneStateListener = false;
                    return;
                }

                if(!SmarterSMBApplication.isFirstCall){
                    if (SmarterSMBApplication.isRemoteDialledStart) {
                        SmarterSMBApplication.isCallLanded = true;
                        Intent intent = new Intent(getApplicationContext(), UearnHome.class);
                        SmarterSMBApplication.isFirstCall = true;
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                }
                UearnActivity.onBackPressed = true;
                if(AppConstants.POSTCALLOG  && AppConstants.POSTCALLOGURL != null && !(AppConstants.POSTCALLOGURL.isEmpty())) {
                    postCallLogs();
                }
                ring = true;
                incomingNmr = incomingNumber;

                /*Added By Srinth- Block Calls*/
                TelephonyManager telephonyManager = (TelephonyManager) context1.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Class clazz = Class.forName(telephonyManager.getClass().getName());
                    Method method = clazz.getDeclaredMethod("getITelephony");
                    method.setAccessible(true);
                    ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
                    if (checkIfNumberExists(incomingNumber)) {
                        telephonyService = (ITelephony) method.invoke(telephonyManager);
                        telephonyService.silenceRinger();
                        telephonyService.endCall();
                    }
                } catch (Exception e) {

                }
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                if(callEndedFromPhoneStateListener) {
                    callEndedFromPhoneStateListener = false;
                    return;
                }
                if (!inOffhook) {
                    offhook = true;
                    inOffhook = true;
                    currentTime = System.currentTimeMillis();
                    //String allowsAllIncoming = ApplicationSettings.getPref(AppConstants.ALLOW_ALL_INCOMING, "");

                    //String allowAllIncoming = ApplicationSettings.getPref(AppConstants.ALLOW_ALL_INCOMING, "");
                    //String allowAllOutgoing = ApplicationSettings.getPref(AppConstants.ALLOW_ALL_OUTGOING, "");
                    if ((allowAllIncoming != null && !allowAllIncoming.isEmpty())) {
                        if (allowAllIncoming.contains("true")) {
                            if (checkIfCallingNumberExistsInDB(incomingNumber)) {
                                NotificationData.dialledCustomerNumber = incomingNumber;
                                sendCallStateMessage(incomingNumber);
                            } else {
                                sendCallStateMessage(incomingNumber); // Temporary enabled for Black Buck
                            }
                        } else {
                            sendCallStateMessage(incomingNumber);
                        }
                    } else {
                        sendCallStateMessage(incomingNumber);
                    }
//                    if (checkIfCallingNumberExistsInDB(incomingNumber)) {
//                        NotificationData.dialledCustomerNumber = incomingNumber;
//                        sendCallStateMessage(incomingNumber);
//                    } else {
//
//                    }
                } else {
                    //Log.i("CallToAfterCallFlow", "Already in Offhook state ****" + incomingNumber);
                    //TODO:Need to Test fully. Added By Srinath.k
                    final String incomingNumber1 = incomingNumber;
                    final String numberToSend1 = numberToSend;
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(5000);
                                if(!(checkMissedCall(incomingNumber1))) {
                                    if (numberToSend1 != null && !(numberToSend1.isEmpty())) {
                                        Message message = handler.obtainMessage(CALL_ENDED, numberToSend1);
                                        handler.sendMessage(message);
                                    }

                                    if (incomingNumber1 != null && !(incomingNumber1.isEmpty())) {
                                        if (!(numberToSend1.equalsIgnoreCase(incomingNumber1))) {
                                            Message message1 = handler.obtainMessage(CALL_ENDED, incomingNumber1);
                                            handler.sendMessage(message1);
                                        }
                                    }
                                    ring = false;
                                    offhook = false;
                                    idle = false;
                                } else {
                                    if (incomingNumber1 != null && !(incomingNumber1.isEmpty())) {
                                        if (CommonUtils.checkForAmeyoNumber(incomingNumber1)) {
                                            putMissedCallData(incomingNumber1, "ameyo_entries");
                                        } else {
                                            putMissedCallData(incomingNumber1, "mytbl");
                                        }
                                    }
                                }
                                //interrupt();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                if(callEndedFromPhoneStateListener) {
                    callEndedFromPhoneStateListener = false;
                    return;
                }
                //Log.i("MyPHone", "IS call state idle");
                idle = true;
                inOffhook = false;
                if (ring && !offhook) {
                    //Log.i("MyPHone", "IS missed call");
                    //Log.i("MyPhone", "Incoming number is " + incomingNumber + "Number from Call receiver is " + incomingNmr);
                    ring = false;
                    if (incomingNumber == null) {
                        numberToSend = incomingNmr;
                    } else if (incomingNumber.equals("")) {
                        numberToSend = incomingNmr;
                    } else {
                        numberToSend = incomingNumber;
                    }
                    if (numberToSend != null) {
                        numberToSend = numberToSend.replace(" ", "");
                    }
                    Message message = handler.obtainMessage(MISSED_CALL, numberToSend);
                    handler.sendMessage(message);
                } else if (offhook) {
                    if (incomingNumber != null && incomingNumber.equals("")) {
                        numberToSend = NUMBER;
                    } else {
                        numberToSend = incomingNumber;
                    }
                    if (numberToSend != null) {
                        numberToSend = numberToSend.replace(" ", "");
                    }
                    Message message = handler.obtainMessage(CALL_ENDED, numberToSend);
                    handler.sendMessage(message);
                }
                ring = false;
                offhook = false;
                idle = false;
                break;
        }
    }

    private String checkNo(String number) {
        String callernumber = number;
        String numberWithCode = null;

        if (callernumber != null &&!(callernumber.isEmpty())) {
            if (CommonUtils.buildE164Number("", callernumber) != null) {
                numberWithCode = CommonUtils.buildE164Number("", callernumber);
                return numberWithCode;
            } else {
                if ((ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "")) != null) {
                    String customerno = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                    if (!customerno.startsWith("+")) {
                        customerno = "+" + customerno;
                    }

                    if (CommonUtils.buildE164Number("", customerno) != null) {
                        String e164Region;
                        try {
                            PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
                            Phonenumber.PhoneNumber phoneNumber = instance.parse(customerno, "");
                            e164Region = instance.getRegionCodeForNumber(phoneNumber);
                            if (e164Region != null) {
                                numberWithCode = CommonUtils.buildE164Number(e164Region, callernumber);
                            }
                        } catch (NumberParseException e) {
                        }
                    }
                    if (numberWithCode != null) {
                        return numberWithCode;
                    }
                }
            }
        }
        return numberWithCode;
    }

    private void allowCall() {

    }

    private void endCall(String incomingNumber) {
        String cloud1 = "";
        String cloud2 = "";
        String cloud3 = "";
        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
            cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
        }
        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
            cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
        }
        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING3)) {
            cloud3 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING3, "");
        }

        if(incomingNumber != null && !incomingNumber.isEmpty() && (incomingNumber.contains(cloud2) || (incomingNumber.equalsIgnoreCase(cloud1) || incomingNumber.equalsIgnoreCase(cloud3)))) {

        } else {
            if(!callEnded) {
                callEnded = true;
                callEndedFromPhoneStateListener = true;
                TelephonyManager telephonyManager = (TelephonyManager) context1.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Class clazz = Class.forName(telephonyManager.getClass().getName());
                    Method method = clazz.getDeclaredMethod("getITelephony");
                    method.setAccessible(true);
                    ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
                    telephonyService = (ITelephony) method.invoke(telephonyManager);
                    telephonyService.silenceRinger();
                    telephonyService.endCall();
                } catch (Exception e) {

                }
            }
        }
    }

    private void sendCallStateMessage(String incomingNumber) {
        if (ring) {
            if (incomingNumber == null) {
                numberToSend = incomingNmr;
            } else if (incomingNumber.equals("")) {
                numberToSend = incomingNmr;
            } else {
                numberToSend = incomingNumber;
            }
            if (numberToSend != null) {
                numberToSend = numberToSend.replace(" ", "");
            }
            SmarterSMBApplication.incomingCallAcceptedByAgent = true;
            Message message = handler.obtainMessage(INCOMING_CALL_ANSWERED, numberToSend);
            handler.sendMessage(message);
        } else {
            //OUTGOING CALL
            if (incomingNumber == null) {
                numberToSend = NUMBER;
            } else if (incomingNumber.equals("")) {
                numberToSend = NUMBER;
            } else {
                numberToSend = incomingNumber;
            }
            if (numberToSend != null) {
                numberToSend = numberToSend.replace(" ", "");
            }
            String callerNumber = checkNo(numberToSend);
            Message message;
            if (callerNumber != null) {
                message = handler.obtainMessage(OUTGOING_CALL_DIALED, callerNumber);
            } else {
                message = handler.obtainMessage(OUTGOING_CALL_DIALED, numberToSend);
            }
            handler.sendMessage(message);
        }
        inOffhook = false;
    }

    private boolean checkMissedCall(String number) {
        return false;
    }

    private void putMissedCallData(String numberToSend, String table_name) {

        if (numberToSend != null && !(numberToSend.isEmpty())) {
            SQLiteDatabase db;
            MySql dbHelper = MySql.getInstance(context1);
            db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            //Log.i("ServiceHandler", "Missed Caller number is " + numberToSend);
            cv.put("PARENT", 0);
            cv.put("EMAIL", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null));
            cv.put("EVENT_TYPE", "call_missed");
            /*if(callDetails.startTime != null) {*/
            cv.put("STARTTIME", CommonUtils.getTimeFormatInISO(new Date()));
            cv.put("UNREAD", "false");
            cv.put("SUBJECT", "Missed Call");

            String callerName = CommonUtils.getContactName(context1, numberToSend);
            cv.put("CALLER", callerName);
            /*}*/
            cv.put("FROM1", numberToSend);
            cv.put("MSG_RECEPIENT_NO", numberToSend);
            cv.put("TO1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));

            long id = db.insert(table_name, null, cv);
            db.close();
        }
    }

    private boolean checkIfNumberExists(String checkNumber) {
        boolean found = false;
        MySql dbHelper = MySql.getInstance(context1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM JunkNumbers", null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String number = cursor.getString(cursor.getColumnIndex("NUMBER"));

                    if (checkNumber.equalsIgnoreCase(number)) {
                        if(cursor.getInt(cursor.getColumnIndex("BLOCK_CALL"))==1){
                            found = true;
                        }
                    }
                }
            } finally {
                cursor.close();
            }
        }
        db.close();

        return found;
    }

    private boolean checkIfCallingNumberExistsInDB(String checkNumber) {
        if(checkNumber != null && !checkNumber.isEmpty()) {
            MySql dbHelper = MySql.getInstance(context1);
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
            Cursor cursor = db1.rawQuery("SELECT * FROM remindertbNew where TO1=" + "'" + checkNumber + "'", null);
            if (cursor != null && cursor.getCount() > 0) {
                try {
                    cursor.close();
                    db1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void postCallLogs() {
        if(context1 != null && CommonUtils.isNetworkAvailable(context1)) {
            String callLandedTime = new Date().toString();
            if (NotificationData.knolarity_start_time != null && NotificationData.knolarity_response_time != null && NotificationData.knolarity_response != null) {
                String responseTime = NotificationData.knolarity_response + NotificationData.knolarity_response_time;
                KnowlarityCallLogs knowlarityCallLogs = new KnowlarityCallLogs(NotificationData.knolarity_start_time, responseTime, callLandedTime);
                new APIProvider.ServerPostCallLog(knowlarityCallLogs, 1, new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {

                    }
                }).call();
            }
        }
    }


    public void startCallRecording(String connectedCustomer) {
        if(connectedCustomer != null && !connectedCustomer.isEmpty() && !ServiceHandler.callDisconnected) {
            Message message = handler.obtainMessage(CALL_RECORDING, connectedCustomer);
            handler.sendMessage(message);
        }
    }


    public void stopCallRecording(String disConnectedCustomer) {
        boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
        if (truePredictive) {
            if(NotificationData.source != null && !NotificationData.source.isEmpty() && NotificationData.source.equalsIgnoreCase("Agent disconnected")) {
                Message message = handler.obtainMessage(AGENT_OR_CUSTOMER_DISCONNECT, NotificationData.dialledCustomerNumber);
                handler.sendMessage(message);
            } else {
                if (disConnectedCustomer != null && !disConnectedCustomer.isEmpty()) {
                    Message message = handler.obtainMessage(AGENT_OR_CUSTOMER_DISCONNECT, disConnectedCustomer);
                    handler.sendMessage(message);
                }
            }
        } else {
            if (disConnectedCustomer != null && !disConnectedCustomer.isEmpty() && !ServiceHandler.callDisconnected) {
                Message message = handler.obtainMessage(CALL_ENDED, disConnectedCustomer);
                handler.sendMessage(message);
            }
        }
    }

    private static void sendCurrentAppStateToServer(String pingResponse, String networkType, String signalStrength) {
        final JSONObject jsonObject = new JSONObject();
        try {
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")) {
                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
            }
            jsonObject.put("state", SmarterSMBApplication.currentAppState);
            jsonObject.put("pingtest", pingResponse);
            jsonObject.put("networktype", networkType);
            jsonObject.put("signalstrength", signalStrength);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataUploadUtils.postIvrCustomerMapping(Urls.postIvrCustomerMappingUrl(), jsonObject);
    }

    private void callingReuploadService() {
        Intent aIntent = new Intent(context1, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context1.startForegroundService(aIntent);
        } else {
            context1.startService(aIntent);
        }
    }
}
