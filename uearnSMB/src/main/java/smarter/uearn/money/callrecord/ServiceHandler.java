package smarter.uearn.money.callrecord;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import smarter.uearn.money.activities.AfterCallActivity;
import smarter.uearn.money.activities.CustomActivity;
import smarter.uearn.money.activities.LandingActivity;
import smarter.uearn.money.activities.MakeACallAfterCallActivity;
import smarter.uearn.money.activities.UearnActivity;
import smarter.uearn.money.R;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.speechapi.ServiceToconvertText;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.GPSTracker;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;
import smarter.uearn.money.views.events.MyPhoneStateListener;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 *
 * Modified by srinath k.
 */
public class ServiceHandler extends Handler {

    private static final int RECORDING_NOTIFICATION_ID1 = 2;
    private MediaRecordWrapper mediaRecordWrapper;
    private Context context;
    private final String TAG = "ServiceHandler";
    public static final String DEFAULT_STORAGE_LOCATION = Environment.getExternalStorageDirectory() + "/callrecorder/";
    public static final File root = new File(Environment.getExternalStorageDirectory(), "TextScripts");
    private SharedPreferences prefs = null;

    //private CallDetails callDetails;
    //Added By Srinath
    private CallDetails callDetails = null;

    public static boolean dontRecordNumber = false;

    long dbid = 0;
    private String latitude, longitude;
    public boolean isCloudOutgoing = false;
    public boolean isknowlarityCall = false;
    public String knowlarityNumber = "";
    public String start_t = "";
    public String end_t = "";
    private String knowlarity_startTime = "";
    private String knowlarity_name = "NA";
    private String stage = "", knowlaritySubStatus = "", number = "";
    static Toast toastMessage = null;
    private Dialog dialog;
    String cloud1 = "", cloud2 = "";
    private String customer_number = "";
    private String screen = "";

    public static boolean callDisconnected = false;
    public static boolean agentMissed = false;
    public static boolean incomingCallAnswered = false;

    CountDownTimer countDownTimer = null;
    public static boolean processCompleted = false;

    private String company = "";
    private boolean showAfterCallPopup = false;

    public ServiceHandler(Looper looper, Context c) {
        super(looper);
        context = c;
    }


    public void handleMessage(Message msg) {
        String no = (String) (msg.obj);
        if(no == null && (context != null)) {
            CommonUtils.callLogApi(context,"ServiceHandler");
        } else if(no != null && (no.isEmpty()) && (context != null)) {
            CommonUtils.callLogApi(context,"ServiceHandler");
        }

        //Log.d("ServiceHandler", "handleMessage " + msg.what + " number "+(String) msg.obj);

        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }

        //&& ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE") || questionsAct.equals("MMT"))

        String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
        if (questionsAct != null && !questionsAct.isEmpty()) {
            String knowlarityNumber = (String) msg.obj;
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

            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING) && ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                if (knowlarityNumber != null && !knowlarityNumber.isEmpty()) {
                    if ((cloud1 != null && !cloud1.isEmpty() && !knowlarityNumber.equalsIgnoreCase(cloud1)) && (cloud2 != null && !cloud2.isEmpty() && !knowlarityNumber.contains(cloud2))) {
                        System.out.print(knowlarityNumber);
                        return;
                    } else if (cloud3 != null && !cloud3.isEmpty() && !knowlarityNumber.equalsIgnoreCase(cloud3)) {
                        System.out.print(knowlarityNumber);
                        return;
                    }
                }
            }
        }

        if (msg.what != MyPhoneStateListener.CALL_RECORDING){
            showAfterCallPopup = ApplicationSettings.getPref(AppConstants.SHOW_AFTER_CALL_POPUP, false);
            if (showAfterCallPopup) {
                Log.d("ServiceHandler", "%%%%%% RETURNING %%%%%%%");
                return;
            }
        }

        if (msg.what == MyPhoneStateListener.INCOMING_CALL_ANSWERED) {
            if (callDisconnected) {
                callDisconnected = false;
            }
            incomingCallAnswered = true;
            NotificationData.legAConnect  = true;
            NotificationData.isSocketResponse = false;
            boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
            boolean isDontrecontNumber = checkForDontRecordNummber((String) msg.obj);
            boolean isJunkNumber = isJunkNumber((String) msg.obj);
            boolean checkReNumber = false;

            if(isLogout) {
                String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                   return;
                }
            }

//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try  {
//                        sendAppStatusInfoToServer();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            thread.start();

            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING)) {
                if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                    if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                        screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                        if (screen != null && ((screen.equalsIgnoreCase("Auto1AfterCallActivity")) || (screen.equalsIgnoreCase("Auto2AfterCallActivity")))) {
                            String num = (String) msg.obj;
                            if (num != null) {
                                boolean check = false, isAmeyo = false;
                                if(NotificationData.makeACall) {
                                    if (NotificationData.knolarity_number != null && !(NotificationData.knolarity_number.isEmpty())) {
                                        check = chechNumberExists(NotificationData.knolarity_number );
                                        isAmeyo = CommonUtils.checkForAmeyoNumber(NotificationData.knolarity_number );
                                        customer_number = NotificationData.knolarity_number;
                                    }
                                } else {
                                    check = chechNumberExists(num);
                                    isAmeyo = CommonUtils.checkForAmeyoNumber(num);
                                }

                                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                                    cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                                }
                                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                                    cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                                }

                                if(NotificationData.makeACall) {
                                    if (!check && !isAmeyo) {
                                        checkReNumber = true;
                                    }
                                } else if ((cloud1 != null && !cloud1.equalsIgnoreCase(num) || (cloud2 != null && !cloud2.equalsIgnoreCase(num)))) {
                                    if (!check && !isAmeyo) {
                                        checkReNumber = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String noOfCustomersToCall = ApplicationSettings.getPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, "");
            int noOfCustomersToDial = 0;

            if(noOfCustomersToCall != null && !noOfCustomersToCall.isEmpty()) {
                boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                if(sequencialEndpoint) {
                    noOfCustomersToDial = Integer.valueOf(noOfCustomersToCall);
                }
            }

            boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
            if(sequencialEndpoint) {

            } else {
                if (!isLogout && !isDontrecontNumber && !(isJunkNumber) && !(checkReNumber)) {
                    if (callDetails != null) {
                        //return;
                    }

                    callDetails = new CallDetails();
                    callDetails.incoming = true;
                    //ToDo Settings Implematation Check.
                    if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                        if (NotificationData.knolarity_number != null && !(NotificationData.knolarity_number.isEmpty())) {
                            knowlarityNumber = (String) msg.obj;
                            knowlaritySubStatus = "";
                            knowlarity_name = "";

                            if (ApplicationSettings.containsPref(AppConstants.CLOUD_CUSTOMER_NUMBER)) {

                            }
                            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                                cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                            }
                            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                                cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                            }
                            if ((cloud1 != null && cloud1.equalsIgnoreCase(knowlarityNumber)) || (cloud2 != null && (cloud2.equalsIgnoreCase(knowlarityNumber))) || (knowlarityNumber.isEmpty()) || (knowlarityNumber.equalsIgnoreCase("")) || (knowlarityNumber.equalsIgnoreCase("null")) || (cloud2 != null && knowlarityNumber != null && (knowlarityNumber.contains(cloud2)))) {
                                start_t = new Date().toString();
                                isknowlarityCall = true;
                                callDetails.phoneno = NotificationData.knolarity_number;

                                if (NotificationData.knolarity_start_time != null && !(NotificationData.knolarity_start_time.isEmpty())) {
                                    knowlarity_startTime = NotificationData.knolarity_start_time;
                                    NotificationData.knolarity_start_time = null;
                                }
                                number = NotificationData.knolarity_number;
                                if (NotificationData.knolarity_name != null) {
                                    knowlarity_name = NotificationData.knolarity_name;
                                    if (NotificationData.substatus1 != null) {
                                        knowlaritySubStatus = NotificationData.substatus1;
                                    }
                                    if (NotificationData.statusString != null) {
                                        stage = NotificationData.statusString;
                                    }
                                }

                                if (NotificationData.knolarity_name == null) {
                                    NotificationData.knolarity_name = "";
                                }
                                isCloudOutgoing = true;
                            } else {
                                callDetails.phoneno = (String) msg.obj;

                                int version_code = CommonUtils.getVersionCode(context);
                                String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                                        ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>Not A cloud telephony number" + callDetails.phoneno
                                        + "<br/> Current Activity: " + SmarterSMBApplication.currentActivity + "<br/> App Version: " + version_code;
                                ServiceApplicationUsage.callErrorLog(message);

                            }
                            NotificationData.knolarity_number = null;
                            NotificationData.knolarity_name = "";
                        }
                    } else {
                        callDetails.phoneno = (String) msg.obj;
                    }
                    callDetails.startTime = new Date();
                    String start = CommonUtils.getTimeFormatInISO(callDetails.startTime);
                    if (start != null) {
                        insertCallState(context, "incoming", callDetails.phoneno, start);
                    }

                    if (shouldRecord()) {
                        if (CommonUtils.checkRecordPermission(context)) {
                            startRecording();
                            if (ApplicationSettings.containsPref(AppConstants.TRANSCRIPTION) && ApplicationSettings.getPref(AppConstants.TRANSCRIPTION, false)) {
                                Intent intent = new Intent(context, ServiceToconvertText.class);
                                if (callDetails.phoneno != null) {
                                    intent.putExtra("number", callDetails.phoneno);
                                }
                                if (start != null) {
                                    intent.putExtra("startTime", start);
                                }
                                String path = getTranscriptionFilePath();

                                intent.putExtra("transcriptionPath", path);
                                callDetails.transcriptionPath = path;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(intent);
                                } else {
                                    context.startService(intent);
                                }
                            }
                        }
                    }
                }
            }

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"");
            //Log.d("ServiceHandler", "Screen " + screen + " qanda "+qanda);

            noOfCustomersToCall = ApplicationSettings.getPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, "");
            noOfCustomersToDial = 0;

            if(noOfCustomersToCall != null && !noOfCustomersToCall.isEmpty()) {
                sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                if(sequencialEndpoint) {
                    noOfCustomersToDial = Integer.valueOf(noOfCustomersToCall);
                }
            }

            if(qanda != null && !qanda.isEmpty() || noOfCustomersToDial >= 1) {
                String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                    boolean radC2CEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                    if (radC2CEndpoint) {
                        String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                        String activityToStart = "smarter.uearn.money.activities." + screen;
                        Class<?> c = null;
                        try {
                            c = Class.forName(activityToStart);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        final Class<?> finalc = c;

                        if (delayedac != null && !delayedac.isEmpty()) {
                            int timer = Integer.parseInt(delayedac);
                            Long counter = (long) timer * 1000;
                            countDownTimer = new CountDownTimer(counter, 1000) {
                                public void onTick(long millis) {
                                }

                                public void onFinish() {
                                    countDownTimer.cancel();
                                    Intent intent = new Intent(context, finalc);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("showinprogress", 1);
                                    //context.startActivity(intent);
                                }
                            }.start();
                        } else {
                            if (screen != null && screen.equalsIgnoreCase("AfterCallActivity")) {

                            } else {
                                Intent intent = new Intent(context, finalc);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("showinprogress", 1);
                                //context.startActivity(intent);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (screen != null && !screen.isEmpty() && screen.equals("UearnActivity")) {
                            String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                            //String activityToStart = "com.smartersmb.activities." + screen;
                            String activityToStart = "smarter.uearn.money.activities." + screen;
                            Class<?> c = null;
                            try {
                                c = Class.forName(activityToStart);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            final Class<?> finalc = c;

                            if (delayedac != null && !delayedac.isEmpty()) {
                                int timer = Integer.parseInt(delayedac);
                                Long counter = (long) timer * 1000;
                                countDownTimer = new CountDownTimer(counter, 1000) {
                                    public void onTick(long millis) {
                                    }

                                    public void onFinish() {
                                        countDownTimer.cancel();
                                        Intent intent = new Intent(context, finalc);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("showinprogress", 1);
                                        //context.startActivity(intent);
                                    }
                                }.start();
                            } else {

                               // For server dialing don't show during call screen
                                //if(!remoteAutoEnabled.equals("on")) {
                                   Intent intent = new Intent(context, finalc);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                   intent.putExtra("showinprogress", 1);
                                   //context.startActivity(intent);
                               //}

                            }
                        } else {
                            if (NotificationData.statusString == null || NotificationData.statusString.isEmpty() || NotificationData.statusString.equalsIgnoreCase("NEW DATA")) {
                                String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                               // String activityToStart = "com.smartersmb.activities." + screen;
                                String activityToStart = "smarter.uearn.money.activities." + screen;
                                Class<?> c = null;
                                try {
                                    c = Class.forName(activityToStart);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                                final Class<?> finalc = c;

                                if (delayedac != null && !delayedac.isEmpty()) {
                                    int timer = Integer.parseInt(delayedac);
                                    Long counter = (long) timer * 1000;
                                    countDownTimer = new CountDownTimer(counter, 1000) {
                                        public void onTick(long millis) {
                                        }

                                        public void onFinish() {
                                            countDownTimer.cancel();
                                            Intent intent = new Intent(context, finalc);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("showinprogress", 1);
                                            //context.startActivity(intent);
                                        }
                                    }.start();
                                } else {
                                    Intent intent = new Intent(context, finalc);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("showinprogress", 1);
                                    //context.startActivity(intent);
                                }
                            }
                        }
                    }
                } else {
                    sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                    if (sequencialEndpoint) {
                        String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                        //String activityToStart = "com.smartersmb.activities." + screen;
                        String activityToStart = "smarter.uearn.money.activities." + screen;
                        Class<?> c = null;
                        try {
                            c = Class.forName(activityToStart);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        final Class<?> finalc = c;

                        if (delayedac != null && !delayedac.isEmpty()) {
                            int timer = Integer.parseInt(delayedac);
                            Long counter = (long) timer * 1000;
                            countDownTimer = new CountDownTimer(counter, 1000) {
                                public void onTick(long millis) {
                                }

                                public void onFinish() {
                                    countDownTimer.cancel();
                                    Intent intent = new Intent(context, finalc);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("showinprogress", 1);
                                    //context.startActivity(intent);
                                }
                            }.start();
                        } else {
                            if (screen != null && screen.equalsIgnoreCase("AfterCallActivity")) {

                            } else {
                                Intent intent = new Intent(context, finalc);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("showinprogress", 1);
                                //context.startActivity(intent);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (screen != null && !screen.isEmpty() && screen.equals("UearnActivity")) {
                            String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                            //
                            // String activityToStart = "com.smartersmb.activities." + screen;
                            String activityToStart = "smarter.uearn.money.activities." + screen;
                            Class<?> c = null;
                            try {
                                c = Class.forName(activityToStart);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            final Class<?> finalc = c;

                            if (delayedac != null && !delayedac.isEmpty()) {
                                int timer = Integer.parseInt(delayedac);
                                Long counter = (long) timer * 1000;
                                countDownTimer = new CountDownTimer(counter, 1000) {
                                    public void onTick(long millis) {
                                    }

                                    public void onFinish() {
                                        countDownTimer.cancel();
                                        Intent intent = new Intent(context, finalc);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("showinprogress", 1);
                                        //context.startActivity(intent);
                                    }
                                }.start();
                            } else {
                                //ACP HIDE
                               // boolean isCloudOn = ApplicationSettings.getPref(AppConstants.AUTO_DAILER, false);
                                //if (!isCloudOn) {
                                    Intent intent = new Intent(context, finalc);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("showinprogress", 1);
                                    //context.startActivity(intent);
                               // }  // Temporary commented for Black Buck


                            }
                        } else {
                            if (NotificationData.statusString == null || NotificationData.statusString.isEmpty() || NotificationData.statusString.equalsIgnoreCase("NEW DATA")) {
                                String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                               // String activityToStart = "com.smartersmb.activities." + screen;
                                String activityToStart = "smarter.uearn.money.activities." + screen;
                                Class<?> c = null;
                                try {
                                    c = Class.forName(activityToStart);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                                final Class<?> finalc = c;

                                if (delayedac != null && !delayedac.isEmpty()) {
                                    int timer = Integer.parseInt(delayedac);
                                    Long counter = (long) timer * 1000;
                                    countDownTimer = new CountDownTimer(counter, 1000) {
                                        public void onTick(long millis) {
                                        }

                                        public void onFinish() {
                                            countDownTimer.cancel();
                                            Intent intent = new Intent(context, finalc);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("showinprogress", 1);
                                            //context.startActivity(intent);
                                        }
                                    }.start();
                                } else {
                                    Intent intent = new Intent(context, finalc);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("showinprogress", 1);
                                    //context.startActivity(intent);
                                }
                            }
                        }
                    }
                }
            }


        } else if (msg.what == MyPhoneStateListener.OUTGOING_CALL_DIALED) {
            if (callDisconnected) {
                callDisconnected = false;
            }
            boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
            boolean isDontrecontNumber = checkForDontRecordNummber((String) msg.obj);
            boolean isJunkNumber = isJunkNumber((String) msg.obj);
            boolean checkReNumber = false;

            if(isLogout) {
                String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                    return;
                }
            }

            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING)) {
                if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                    if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                        screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                        if (screen != null && ((screen.equalsIgnoreCase("Auto1AfterCallActivity")) || (screen.equalsIgnoreCase("Auto2AfterCallActivity")))) {
                            String num = (String) msg.obj;
                            if (num != null) {
                                boolean check = false, isAmeyo = false;
                                if(NotificationData.makeACall) {
                                    if (NotificationData.knolarity_number != null && !(NotificationData.knolarity_number.isEmpty())) {
                                        check = chechNumberExists(NotificationData.knolarity_number );
                                        isAmeyo = CommonUtils.checkForAmeyoNumber(NotificationData.knolarity_number );
                                        customer_number = NotificationData.knolarity_number;
                                    }
                                } else {
                                    check = chechNumberExists(num);
                                    isAmeyo = CommonUtils.checkForAmeyoNumber(num);
                                }

                                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                                    cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                                }
                                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                                    cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                                }

                                if(NotificationData.makeACall) {
                                    if (!check && !isAmeyo) {
                                        checkReNumber = true;
                                    }
                                } else if ((cloud1 != null && !cloud1.equalsIgnoreCase(num) || (cloud2 != null && !cloud2.equalsIgnoreCase(num)))) {
                                    if (!check && !isAmeyo) {
                                        checkReNumber = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String noOfCustomersToCall = ApplicationSettings.getPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, "");
            int noOfCustomersToDial = 0;

            if(noOfCustomersToCall != null && !noOfCustomersToCall.isEmpty()) {
                boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                if(sequencialEndpoint) {
                    noOfCustomersToDial = Integer.valueOf(noOfCustomersToCall);
                }
            }

            boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
            if(sequencialEndpoint) {

            } else {

                if (!isLogout && !isDontrecontNumber && !(isJunkNumber) && !(checkReNumber)) {
                    if (callDetails != null) {
                        //Log.e("ServiceHandler", "Already recording another call returned");
                        return;
                    }

                    callDetails = new CallDetails();
                    callDetails.incoming = true;
                    //ToDo Settings Implematation Check.
                    if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                        if (NotificationData.knolarity_number != null && !(NotificationData.knolarity_number.isEmpty())) {
                            knowlarityNumber = (String) msg.obj;
                            knowlaritySubStatus = "";
                            knowlarity_name = "";

                            if (ApplicationSettings.containsPref(AppConstants.CLOUD_CUSTOMER_NUMBER)) {
                                //Log.d("CLOUD_CUSTOMER", ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, ""));
                            }
                            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                                cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                            }
                            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                                cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                            }
                            if ((cloud1 != null && cloud1.equalsIgnoreCase(knowlarityNumber)) || (cloud2 != null && (cloud2.equalsIgnoreCase(knowlarityNumber))) || (knowlarityNumber.isEmpty()) || (knowlarityNumber.equalsIgnoreCase("")) || (knowlarityNumber.equalsIgnoreCase("null")) || (cloud2 != null && knowlarityNumber != null && (knowlarityNumber.contains(cloud2)))) {
                                start_t = new Date().toString();
                                isknowlarityCall = true;
                                callDetails.phoneno = NotificationData.knolarity_number;

                                if (NotificationData.knolarity_start_time != null && !(NotificationData.knolarity_start_time.isEmpty())) {
                                    knowlarity_startTime = NotificationData.knolarity_start_time;
                                    NotificationData.knolarity_start_time = null;
                                }
                                number = NotificationData.knolarity_number;
                                if (NotificationData.knolarity_name != null) {
                                    knowlarity_name = NotificationData.knolarity_name;
                                    if (NotificationData.substatus1 != null) {
                                        knowlaritySubStatus = NotificationData.substatus1;
                                    }
                                    if (NotificationData.statusString != null) {
                                        stage = NotificationData.statusString;
                                    }
                                }

                                if (NotificationData.knolarity_name == null) {
                                    NotificationData.knolarity_name = "";
                                }
                                isCloudOutgoing = true;
                            } else {
                                callDetails.phoneno = (String) msg.obj;

                                int version_code = CommonUtils.getVersionCode(context);
                                String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                                        ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>Not A cloud telephony number" + callDetails.phoneno
                                        + "<br/> Current Activity: " + SmarterSMBApplication.currentActivity + "<br/> App Version: " + version_code;
                                ServiceApplicationUsage.callErrorLog(message);

                            }
                            NotificationData.knolarity_number = null;
                            NotificationData.knolarity_name = "";
                        }
                    } else {
                        callDetails.phoneno = (String) msg.obj;
                    }
                    //Log.i("ServiceHandler", "Caller number is " + callDetails.phoneno);
                    callDetails.startTime = new Date();
                    String start = CommonUtils.getTimeFormatInISO(callDetails.startTime);
                    if (start != null) {
                        //Log.d("ServiceHandler", "StartinISO" + start);
                        insertCallState(context, "incoming", callDetails.phoneno, start);
                    }

                    if (shouldRecord()) {
                        if (CommonUtils.checkRecordPermission(context)) {
                            startRecording();
                            if (ApplicationSettings.containsPref(AppConstants.TRANSCRIPTION) && ApplicationSettings.getPref(AppConstants.TRANSCRIPTION, false)) {
                                Intent intent = new Intent(context, ServiceToconvertText.class);
                                if (callDetails.phoneno != null) {
                                    intent.putExtra("number", callDetails.phoneno);
                                }
                                if (start != null) {
                                    intent.putExtra("startTime", start);
                                }
                                String path = getTranscriptionFilePath();

                                intent.putExtra("transcriptionPath", path);
                                callDetails.transcriptionPath = path;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(intent);
                                } else {
                                    context.startService(intent);
                                }
                            }
                        }
                    }
                }
            }

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"");
            //Log.d("ServiceHandler", "Screen " + screen + " qanda "+qanda);

            noOfCustomersToCall = ApplicationSettings.getPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, "");
            noOfCustomersToDial = 0;

            if(noOfCustomersToCall != null && !noOfCustomersToCall.isEmpty()) {
                sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                if(sequencialEndpoint) {
                    noOfCustomersToDial = Integer.valueOf(noOfCustomersToCall);
                }
            }

            if(qanda != null && !qanda.isEmpty() || noOfCustomersToDial >= 1) {
                String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                    boolean radC2CEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                    if (radC2CEndpoint) {
                        String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                        String activityToStart = "smarter.uearn.money.activities." + screen;
                        Class<?> c = null;
                        try {
                            c = Class.forName(activityToStart);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        final Class<?> finalc = c;

                        if (delayedac != null && !delayedac.isEmpty()) {
                            int timer = Integer.parseInt(delayedac);
                            Long counter = (long) timer * 1000;
                            countDownTimer = new CountDownTimer(counter, 1000) {
                                public void onTick(long millis) {
                                }

                                public void onFinish() {
                                    countDownTimer.cancel();
                                    Intent intent = new Intent(context, finalc);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("showinprogress", 1);
                                    context.startActivity(intent);
                                }
                            }.start();
                        } else {
                            if (screen != null && screen.equalsIgnoreCase("AfterCallActivity")) {

                            } else {
                                Intent intent = new Intent(context, finalc);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("showinprogress", 1);
                                context.startActivity(intent);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (screen != null && !screen.isEmpty() && screen.equals("UearnActivity")) {
                            String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                            //String activityToStart = "com.smartersmb.activities." + screen;
                            String activityToStart = "smarter.uearn.money.activities." + screen;
                            Class<?> c = null;
                            try {
                                c = Class.forName(activityToStart);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            final Class<?> finalc = c;

                            if (delayedac != null && !delayedac.isEmpty()) {
                                int timer = Integer.parseInt(delayedac);
                                Long counter = (long) timer * 1000;
                                countDownTimer = new CountDownTimer(counter, 1000) {
                                    public void onTick(long millis) {
                                    }

                                    public void onFinish() {
                                        countDownTimer.cancel();
                                        Intent intent = new Intent(context, finalc);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("showinprogress", 1);
                                        context.startActivity(intent);
                                    }
                                }.start();
                            } else {
                                Intent intent = new Intent(context, finalc);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("showinprogress", 1);
                                context.startActivity(intent);
                            }
                        } else {
                            if (NotificationData.statusString == null || NotificationData.statusString.isEmpty() || NotificationData.statusString.equalsIgnoreCase("NEW DATA")) {
                                String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                                // String activityToStart = "com.smartersmb.activities." + screen;
                                String activityToStart = "smarter.uearn.money.activities." + screen;
                                Class<?> c = null;
                                try {
                                    c = Class.forName(activityToStart);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                                final Class<?> finalc = c;

                                if (delayedac != null && !delayedac.isEmpty()) {
                                    int timer = Integer.parseInt(delayedac);
                                    Long counter = (long) timer * 1000;
                                    countDownTimer = new CountDownTimer(counter, 1000) {
                                        public void onTick(long millis) {
                                        }

                                        public void onFinish() {
                                            countDownTimer.cancel();
                                            Intent intent = new Intent(context, finalc);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("showinprogress", 1);
                                            context.startActivity(intent);
                                        }
                                    }.start();
                                } else {
                                    Intent intent = new Intent(context, finalc);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("showinprogress", 1);
                                    context.startActivity(intent);
                                }
                            }
                        }
                    }
                } else {
                    sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                    if (sequencialEndpoint) {
                        String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                        //String activityToStart = "com.smartersmb.activities." + screen;
                        String activityToStart = "smarter.uearn.money.activities." + screen;
                        Class<?> c = null;
                        try {
                            c = Class.forName(activityToStart);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        final Class<?> finalc = c;

                        if (delayedac != null && !delayedac.isEmpty()) {
                            int timer = Integer.parseInt(delayedac);
                            Long counter = (long) timer * 1000;
                            countDownTimer = new CountDownTimer(counter, 1000) {
                                public void onTick(long millis) {
                                }

                                public void onFinish() {
                                    countDownTimer.cancel();
                                    Intent intent = new Intent(context, finalc);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("showinprogress", 1);
                                    context.startActivity(intent);
                                }
                            }.start();
                        } else {
                            if (screen != null && screen.equalsIgnoreCase("AfterCallActivity")) {

                            } else {
                                Intent intent = new Intent(context, finalc);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("showinprogress", 1);
                                context.startActivity(intent);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (screen != null && !screen.isEmpty() && screen.equals("UearnActivity")) {
                            String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                            //
                            // String activityToStart = "com.smartersmb.activities." + screen;
                            String activityToStart = "smarter.uearn.money.activities." + screen;
                            Class<?> c = null;
                            try {
                                c = Class.forName(activityToStart);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            final Class<?> finalc = c;

                            if (delayedac != null && !delayedac.isEmpty()) {
                                int timer = Integer.parseInt(delayedac);
                                Long counter = (long) timer * 1000;
                                countDownTimer = new CountDownTimer(counter, 1000) {
                                    public void onTick(long millis) {
                                    }

                                    public void onFinish() {
                                        countDownTimer.cancel();
                                        Intent intent = new Intent(context, finalc);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("showinprogress", 1);
                                        context.startActivity(intent);
                                    }
                                }.start();
                            } else {
                                Intent intent = new Intent(context, finalc);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("showinprogress", 1);
                                context.startActivity(intent);
                            }
                        } else {
                            if (NotificationData.statusString == null || NotificationData.statusString.isEmpty() || NotificationData.statusString.equalsIgnoreCase("NEW DATA")) {
                                String delayedac = ApplicationSettings.getPref(AppConstants.DELAY_AC_POPUP, "");
                                // String activityToStart = "com.smartersmb.activities." + screen;
                                String activityToStart = "smarter.uearn.money.activities." + screen;
                                Class<?> c = null;
                                try {
                                    c = Class.forName(activityToStart);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                                final Class<?> finalc = c;

                                if (delayedac != null && !delayedac.isEmpty()) {
                                    int timer = Integer.parseInt(delayedac);
                                    Long counter = (long) timer * 1000;
                                    countDownTimer = new CountDownTimer(counter, 1000) {
                                        public void onTick(long millis) {
                                        }

                                        public void onFinish() {
                                            countDownTimer.cancel();
                                            Intent intent = new Intent(context, finalc);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("showinprogress", 1);
                                            context.startActivity(intent);
                                        }
                                    }.start();
                                } else {
                                    Intent intent = new Intent(context, finalc);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("showinprogress", 1);
                                    context.startActivity(intent);
                                }
                            }
                        }
                    }
                }
            }

            if (company.equals("OYO")) {

                if(number != null && !number.isEmpty()) {
                    if (knowlarity_name == null) {
                        knowlarity_name = "NA";
                    }
                    if (stage == null) {
                        stage = "";
                    }
                    String message = "NAME   : " + knowlarity_name + "\nPHONE : " + number + "" + "\nSTATUS: " + stage;
                    if (knowlaritySubStatus != null) {
                        message = message + " | " + knowlaritySubStatus;
                    }
                    if (NotificationData.notes_string != null && !NotificationData.notes_string.isEmpty() && !NotificationData.notes_string.equalsIgnoreCase("null")) {
                        message = message + "\n" + NotificationData.notes_string;
                    }

                    if (ApplicationSettings.containsPref(AppConstants.PARALLEL_GROUP_SIZE)) {
                        String parallelGroupSize = ApplicationSettings.getPref(AppConstants.PARALLEL_GROUP_SIZE, "");
                        if (parallelGroupSize != null && !parallelGroupSize.isEmpty()) {

                        } else {
                            for (int i = 0; i < 2; i++) {
                                toastMessage = Toast.makeText(context, message + "\n" + "\n** This conversation will be recorded. **", Toast.LENGTH_SHORT);
                                toastMessage.setGravity(Gravity.CENTER, 0, 0);
                                toastMessage.show();
                            }
                        }
                    } else {
                        for (int i = 0; i < 2; i++) {
                            toastMessage = Toast.makeText(context, message + "\n" + "\n** This conversation will be recorded. **", Toast.LENGTH_SHORT);
                            toastMessage.setGravity(Gravity.CENTER, 0, 0);
                            toastMessage.show();
                        }
                    }
                }
            }
        } else if (msg.what == MyPhoneStateListener.CALL_ENDED) {

            long callEndTime= System.currentTimeMillis();
            ApplicationSettings.putPref(AppConstants.CALL_END_TIME, callEndTime);
            //Log.d("WrapupTime","MyPhoneStateListener.CALL_ENDED" +callEndTime);

            //Log.d("NewChanges","MyPhoneStateListener.CALL_ENDED");
            //Log.d("NavigateToACP","MyPhoneStateListener.CALL_ENDED");


            NotificationData.legAConnect = false;
            SmarterSMBApplication.legADisconnectScenario = true;

//            boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
//            if(truePredictive) {
//                if(SmarterSMBApplication.agentDisconnectScenario && (SmarterSMBApplication.customerDisconnectScenario || SmarterSMBApplication.legADisconnectScenario)){
//                    if(SmarterSMBApplication.navigateToACP) {
//                        //Log.d("NewChanges","Agent Returning" + String.valueOf(SmarterSMBApplication.navigateToACP));
//                        return;
//                    }
//                } else if(SmarterSMBApplication.customerDisconnectScenario && (SmarterSMBApplication.agentDisconnectScenario || SmarterSMBApplication.legADisconnectScenario)){
//                    if(SmarterSMBApplication.navigateToACP) {
//                        //Log.d("NewChanges","Customer Returning" + String.valueOf(SmarterSMBApplication.navigateToACP));
//                        return;
//                    }
//                } else if(SmarterSMBApplication.legADisconnectScenario && (SmarterSMBApplication.customerDisconnectScenario || SmarterSMBApplication.agentDisconnectScenario)){
//                    if(SmarterSMBApplication.navigateToACP) {
//                        //Log.d("NewChanges","LegA Returning" + String.valueOf(SmarterSMBApplication.navigateToACP));
//                        return;
//                    }
//                }
//
//                if (UearnActivity.callEndedFromDuringCall) {
//
//                } else {
//                    Log.d("CallDisconnected", "CallDisconnected9");
//                    callDisconnected = true;
//                    if(!SmarterSMBApplication.agentDisconnectScenario || !SmarterSMBApplication.customerDisconnectScenario){
//                        if(SmarterSMBApplication.navigateToACP) {
//                            return;
//                        } else {
//                            SmarterSMBApplication.legADisconnectScenario = true;
//                        }
//                    }
//                }
//            } else {
                //callDisconnected = true;
//            }
            processCompleted = true;

            ApplicationSettings.putPref(AppConstants.QUESTIONS_COUNT, 0);
            ApplicationSettings.putPref(AppConstants.ROOT_ARRAY_DATA_LENGTH, 0);

            if(MyPhoneStateListener.callEndedFromPhoneStateListener) {
                MyPhoneStateListener.callEndedFromPhoneStateListener = false;
                return;
            }

            MyPhoneStateListener.callEnded = false;
            if (dialog != null) {
                dialog.dismiss();
            }
            boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
            boolean isDontrecontNumber = checkForDontRecordNummber((String) msg.obj);
            boolean isJunkNumber = isJunkNumber((String) msg.obj);
            String incomingNum = msg.obj.toString();
            boolean checkReNumber = false;

            if(company.equals("OYO")) {
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                    cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                }
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                    cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                }

                if ((cloud1 != null && cloud1.equalsIgnoreCase(incomingNum) || (cloud2 != null && cloud2.equalsIgnoreCase(incomingNum)) || (cloud2 != null && incomingNum.contains(cloud2)))) {
                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING)) {
                        if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                            if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                                if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")))) {
                                    String num = (String) msg.obj;
                                    if (num != null) {
                                        boolean check = false, isAmeyo = false;
                                        if (NotificationData.makeACall && customer_number != null && !(customer_number.isEmpty())) {
                                            check = chechNumberExists(customer_number);
                                            isAmeyo = CommonUtils.checkForAmeyoNumber(customer_number);
                                        } else {
                                            check = chechNumberExists(num);
                                            isAmeyo = CommonUtils.checkForAmeyoNumber(num);
                                        }

                                        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                                            cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                                        }
                                        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                                            cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                                        }

                                        if (NotificationData.makeACall) {
                                            if (!check && !isAmeyo) {
                                                checkReNumber = true;
                                            }
                                        } else if ((cloud1 != null && !cloud1.equalsIgnoreCase(num) || (cloud2 != null && !cloud2.equalsIgnoreCase(num)) || (cloud2 != null && !num.contains(cloud2)))) {
                                            if (!check && !isAmeyo) {
                                                checkReNumber = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (context != null) {
                        if (isMyServiceRunning(ServiceToconvertText.class)) {
                            context.stopService(new Intent(context, ServiceToconvertText.class));
                        }
                    }

                    end_t = new Date().toString();


                    if (!isLogout && !isDontrecontNumber && !(isJunkNumber) && checkReNumber) {
                        String num = (String) msg.obj;
                        if (num != null && !(NotificationData.makeACall)) {
                            builddailog(context, num);
                        } else if (NotificationData.makeACall) {
                            if (customer_number != null && !(customer_number.isEmpty())) {
                                builddailog(context, customer_number);
                                NotificationData.makeACall = false;
                            }
                        }
                    }

                    if (isLogout) {
                        if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Bank1AfterCallActivity")))) {

                        } else {
                            checkDialogAndLoad(msg);
                        }
                    } else if (!isLogout && !isDontrecontNumber && !(isJunkNumber) && !(checkReNumber)) {

                        if (callDetails != null) {
                            processCallEnd();
                        } else if (((String) msg.obj) != null) {
                            callDetails = new CallDetails();
                            callDetails.incoming = false;
                            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CONNECTED_CUSTOMER, "");
                            if (connectedCustomer != null && !connectedCustomer.isEmpty()) {
                                callDetails.phoneno = connectedCustomer;
                            } else {
                                callDetails.phoneno = (String) msg.obj;
                            }
                            callDetails.startTime = new Date();
                            processCallEnd();
                        }
                    } else {
                        NotificationData.privateCall = false;
                    }
                }
            } else {
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING)) {
                    if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                        if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                            if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")))) {
                                String num = (String) msg.obj;
                                if (num != null) {
                                    boolean check = false, isAmeyo = false;
                                    if(NotificationData.makeACall &&  customer_number != null &&   !(customer_number.isEmpty())) {
                                        check = chechNumberExists(customer_number);
                                        isAmeyo = CommonUtils.checkForAmeyoNumber(customer_number);
                                    } else {
                                        check = chechNumberExists(num);
                                        isAmeyo = CommonUtils.checkForAmeyoNumber(num);
                                    }
                                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                                        cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                                    }
                                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                                        cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                                    }

                                    if(NotificationData.makeACall) {
                                        if (!check && !isAmeyo) {
                                            checkReNumber = true;
                                        }
                                    } else if ((cloud1 != null && !cloud1.equalsIgnoreCase(num) || (cloud2 != null && !cloud2.equalsIgnoreCase(num)))) {
                                        if (!check && !isAmeyo) {
                                            checkReNumber = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (context != null) {
                    if (isMyServiceRunning(ServiceToconvertText.class)) {
                        context.stopService(new Intent(context, ServiceToconvertText.class));
                    }
                }

                end_t = new Date().toString();


                if(!isLogout && !isDontrecontNumber && !(isJunkNumber) && checkReNumber) {
                    String num = (String) msg.obj;
                    if(num != null && !(NotificationData.makeACall)) {
                        builddailog(context, num);
                    } else if(NotificationData.makeACall) {
                        if(customer_number != null &&  !(customer_number.isEmpty())) {
                            builddailog(context, customer_number);
                            NotificationData.makeACall = false;
                        }
                    }
                }

                if (isLogout) {
                    if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Bank1AfterCallActivity")))) {

                    } else {
                       // Log.d("LandingActivity", "checkDialogAndLoad"+"1371");
                        //After signout each personal call end UearnLandscreen presentation hide SUJIT
                       // checkDialogAndLoad(msg);
                    }
                } else if (!isLogout && !isDontrecontNumber && !(isJunkNumber) && !(checkReNumber)) {

                    if (callDetails != null) {
                        processCallEnd();
                    } else if (((String) msg.obj) != null) {
                        callDetails = new CallDetails();
                        callDetails.incoming = false;
                        String connectedCustomer = ApplicationSettings.getPref(AppConstants.CONNECTED_CUSTOMER, "");
                        if(connectedCustomer != null && !connectedCustomer.isEmpty()) {
                            callDetails.phoneno = connectedCustomer;
                        } else {
                            callDetails.phoneno = (String) msg.obj;
                        }
                        callDetails.startTime = new Date();
                        processCallEnd();
                    }
                } else {
                    NotificationData.privateCall = false;
                }
            }

        } else if (msg.what == MyPhoneStateListener.AGENT_OR_CUSTOMER_DISCONNECT) {

            long callEndTime= System.currentTimeMillis();
            ApplicationSettings.putPref(AppConstants.CALL_END_TIME, callEndTime);

            if(NotificationData.source != null && !NotificationData.source.isEmpty() && (NotificationData.source.equalsIgnoreCase("Agent disconnected") || NotificationData.source.equalsIgnoreCase("Customer disconnected"))){
                NotificationData.legAConnect  = true;
            } else {
                NotificationData.legAConnect  = false;
            }

            boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
            if(truePredictive) {
                if(SmarterSMBApplication.agentDisconnectScenario && (SmarterSMBApplication.customerDisconnectScenario || SmarterSMBApplication.legADisconnectScenario)){
                    if(SmarterSMBApplication.navigateToACP) {
                        return;
                    }
                } else if(SmarterSMBApplication.customerDisconnectScenario && (SmarterSMBApplication.agentDisconnectScenario || SmarterSMBApplication.legADisconnectScenario)){
                    if(SmarterSMBApplication.navigateToACP) {
                        return;
                    }
                } else if(SmarterSMBApplication.legADisconnectScenario && (SmarterSMBApplication.customerDisconnectScenario || SmarterSMBApplication.agentDisconnectScenario)){
                    if(SmarterSMBApplication.navigateToACP) {
                        return;
                    }
                }

                if (UearnActivity.callEndedFromDuringCall) {

                } else {
                    callDisconnected = true;
                    if(!SmarterSMBApplication.agentDisconnectScenario || !SmarterSMBApplication.customerDisconnectScenario){
                        if(SmarterSMBApplication.navigateToACP) {
                            return;
                        } else {
                            SmarterSMBApplication.legADisconnectScenario = true;
                        }
                    }
                }
            } else {
                callDisconnected = true;
            }
            processCompleted = true;

            ApplicationSettings.putPref(AppConstants.QUESTIONS_COUNT, 0);
            ApplicationSettings.putPref(AppConstants.ROOT_ARRAY_DATA_LENGTH, 0);

            if(MyPhoneStateListener.callEndedFromPhoneStateListener) {
                MyPhoneStateListener.callEndedFromPhoneStateListener = false;
                return;
            }

            MyPhoneStateListener.callEnded = false;
            if (dialog != null) {
                dialog.dismiss();
            }
            boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
            boolean isDontrecontNumber = checkForDontRecordNummber((String) msg.obj);
            boolean isJunkNumber = isJunkNumber((String) msg.obj);
            String incomingNum = msg.obj.toString();
            boolean checkReNumber = false;

            if(company.equals("OYO")) {
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                    cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                }
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                    cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                }

                if ((cloud1 != null && cloud1.equalsIgnoreCase(incomingNum) || (cloud2 != null && cloud2.equalsIgnoreCase(incomingNum)) || (cloud2 != null && incomingNum.contains(cloud2)))) {
                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING)) {
                        if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                            if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                                if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")))) {
                                    String num = (String) msg.obj;
                                    if (num != null) {
                                        boolean check = false, isAmeyo = false;
                                        if (NotificationData.makeACall && customer_number != null && !(customer_number.isEmpty())) {
                                            check = chechNumberExists(customer_number);
                                            isAmeyo = CommonUtils.checkForAmeyoNumber(customer_number);
                                        } else {
                                            check = chechNumberExists(num);
                                            isAmeyo = CommonUtils.checkForAmeyoNumber(num);
                                        }

                                        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                                            cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                                        }
                                        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                                            cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                                        }

                                        if (NotificationData.makeACall) {
                                            if (!check && !isAmeyo) {
                                                checkReNumber = true;
                                            }
                                        } else if ((cloud1 != null && !cloud1.equalsIgnoreCase(num) || (cloud2 != null && !cloud2.equalsIgnoreCase(num)) || (cloud2 != null && !num.contains(cloud2)))) {
                                            if (!check && !isAmeyo) {
                                                checkReNumber = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (context != null) {
                        if (isMyServiceRunning(ServiceToconvertText.class)) {
                            context.stopService(new Intent(context, ServiceToconvertText.class));
                        }
                    }

                    end_t = new Date().toString();


                    if (!isLogout && !isDontrecontNumber && !(isJunkNumber) && checkReNumber) {
                        String num = (String) msg.obj;
                        if (num != null && !(NotificationData.makeACall)) {
                            builddailog(context, num);
                        } else if (NotificationData.makeACall) {
                            if (customer_number != null && !(customer_number.isEmpty())) {
                                builddailog(context, customer_number);
                                NotificationData.makeACall = false;
                            }
                        }
                    }

                    if (isLogout) {
                        if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Bank1AfterCallActivity")))) {

                        } else {
                            checkDialogAndLoad(msg);
                        }
                    } else if (!isLogout && !isDontrecontNumber && !(isJunkNumber) && !(checkReNumber)) {

                        if (callDetails != null) {
                            processCallEnd();
                        } else if (((String) msg.obj) != null) {
                            callDetails = new CallDetails();
                            callDetails.incoming = false;
                            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CONNECTED_CUSTOMER, "");
                            if (connectedCustomer != null && !connectedCustomer.isEmpty()) {
                                callDetails.phoneno = connectedCustomer;
                            } else {
                                callDetails.phoneno = (String) msg.obj;
                            }
                            callDetails.startTime = new Date();
                            processCallEnd();
                        }
                    } else {
                        NotificationData.privateCall = false;
                    }
                }
            } else {
                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING)) {
                    if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                        if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                            if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")))) {
                                String num = (String) msg.obj;
                                if (num != null) {
                                    boolean check = false, isAmeyo = false;
                                    if(NotificationData.makeACall &&  customer_number != null &&   !(customer_number.isEmpty())) {
                                        check = chechNumberExists(customer_number);
                                        isAmeyo = CommonUtils.checkForAmeyoNumber(customer_number);
                                    } else {
                                        check = chechNumberExists(num);
                                        isAmeyo = CommonUtils.checkForAmeyoNumber(num);
                                    }
                                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                                        cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                                    }
                                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                                        cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                                    }

                                    if(NotificationData.makeACall) {
                                        if (!check && !isAmeyo) {
                                            checkReNumber = true;
                                        }
                                    } else if ((cloud1 != null && !cloud1.equalsIgnoreCase(num) || (cloud2 != null && !cloud2.equalsIgnoreCase(num)))) {
                                        if (!check && !isAmeyo) {
                                            checkReNumber = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (context != null) {
                    if (isMyServiceRunning(ServiceToconvertText.class)) {
                        context.stopService(new Intent(context, ServiceToconvertText.class));
                    }
                }

                end_t = new Date().toString();


                if(!isLogout && !isDontrecontNumber && !(isJunkNumber) && checkReNumber) {
                    String num = (String) msg.obj;
                    if(num != null && !(NotificationData.makeACall)) {
                        builddailog(context, num);
                    } else if(NotificationData.makeACall) {
                        if(customer_number != null &&  !(customer_number.isEmpty())) {
                            builddailog(context, customer_number);
                            NotificationData.makeACall = false;
                        }
                    }
                }

                if (isLogout) {
                    if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Bank1AfterCallActivity")))) {

                    } else {
                        checkDialogAndLoad(msg);
                    }
                } else if (!isLogout && !isDontrecontNumber && !(isJunkNumber) && !(checkReNumber)) {

                    if (callDetails != null) {
                        processCallEnd();
                    } else if (((String) msg.obj) != null) {
                        callDetails = new CallDetails();
                        callDetails.incoming = false;
                        String connectedCustomer = ApplicationSettings.getPref(AppConstants.CONNECTED_CUSTOMER, "");
                        if(connectedCustomer != null && !connectedCustomer.isEmpty()) {
                            callDetails.phoneno = connectedCustomer;
                        } else {
                            callDetails.phoneno = (String) msg.obj;
                        }
                        callDetails.startTime = new Date();
                        processCallEnd();
                    }
                } else {
                    NotificationData.privateCall = false;
                }
            }

        } else if (msg.what == MyPhoneStateListener.MISSED_CALL) {
            if(SmarterSMBApplication.isRemoteDialledStart) {
                agentMissed = true;
            }
            if(MyPhoneStateListener.callEndedFromPhoneStateListener) {
                MyPhoneStateListener.callEndedFromPhoneStateListener = false;
                return;
            }

            MyPhoneStateListener.callEnded = false;
            boolean checkReNumber = false;
            /*CHECK to RE FLOW */
            /*CHECK to RE FLOW */
            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING)) {
                if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                    if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                        if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")))) {
                            String num = (String) msg.obj;
                            if (num != null) {
                                boolean check = false, isAmeyo = false;
                                if(NotificationData.makeACall &&  callDetails != null &&  callDetails.phoneno != null && !(callDetails.phoneno.isEmpty())) {
                                    check = chechNumberExists(callDetails.phoneno);
                                    isAmeyo = CommonUtils.checkForAmeyoNumber(callDetails.phoneno);
                                } else {
                                    check = chechNumberExists(num);
                                    isAmeyo = CommonUtils.checkForAmeyoNumber(num);
                                }

                                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                                    cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                                }
                                if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                                    cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                                }

                                if(NotificationData.makeACall) {
                                    if (!check && !isAmeyo) {
                                        checkReNumber = true;
                                    }
                                } else if ((cloud1 != null && !cloud1.equalsIgnoreCase(num) || (cloud2 != null && !num.contains(cloud2)))) {
                                    if (!check && !isAmeyo) {
                                        checkReNumber = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //Untill Here

            GPSTracker gpsTracker = new GPSTracker(context);

            if (gpsTracker.canGetLocation()) {
                latitude = Double.toString(gpsTracker.getLatitude());
                longitude = Double.toString(gpsTracker.getLongitude());
            }
            boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
            boolean isDontrecontNumber = checkForDontRecordNummber((String) msg.obj);
            boolean isJunkNumber = isJunkNumber((String) msg.obj);
            if (!isLogout && !isDontrecontNumber && !(isJunkNumber) && !(checkReNumber)) {
                callDetails = new CallDetails();
                callDetails.phoneno = (String) msg.obj;

                //Knowlarity
                if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                    if (NotificationData.knolarity_number != null && !(NotificationData.knolarity_number.isEmpty())) {
                        knowlarityNumber = (String) msg.obj;

                        String cloud1 = "", cloud2 = "";
                        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                            cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                        }
                        if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                            cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                        }
                        if ((cloud1 != null && cloud1.equalsIgnoreCase(knowlarityNumber)) || (cloud2 != null && (cloud2.equalsIgnoreCase(knowlarityNumber))) || (cloud2 != null && knowlarityNumber != null && (knowlarityNumber.contains(cloud2)))) {
                            String number = NotificationData.knolarity_number;
                            boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                            if(sequencialEndpoint) {
                                String[] numbersArray = null;
                                if (number != null && !number.isEmpty()) {
                                    numbersArray = number.split(",");
                                    if (numbersArray != null && numbersArray.length > 0) {
                                        number = numbersArray[0];
                                    }
                                }
                            }
                            callDetails.phoneno = number;
                            isknowlarityCall = true;
                        } else {
                            callDetails.phoneno = (String) msg.obj;
                        }
                        NotificationData.knolarity_number = "";
                        NotificationData.knolarity_name = "";
                        NotificationData.substatus1 = "";
                        NotificationData.substatus2 = "";

                    }
                }
                callDetails.isRecording = false;
                callDetails.startTime = new Date();
                callDetails.incoming = false;
                callDetails.callType = "missed_call";
                String start = CommonUtils.getTimeFormatInISO(callDetails.startTime);
                if (start != null) {
                    insertCallState(context, "missedCall", callDetails.phoneno, start);
                }
                //TODO: check if number is  Ameyo then make an entry in ameyo table else make an entry to mytbl table.

                String cloud1 = "";
                String cloud2 = "";

                if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
                    company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
                }

                if(company.equals("OYO")) {
                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                        cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                    }

                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                        cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                    }

                    if(callDetails != null) {
                        String inNumber = msg.obj.toString();

                        if (cloud1.equals(inNumber) || cloud2.equals(inNumber) || inNumber.contains(cloud2)) {
                            if (checkForAmeyoNumber(callDetails.phoneno)) {
                                //Adding entry to ameyo table as the number is ameyo.
                                putMissedCallDetailsToDb(callDetails, "ameyo_entries");
                                if (callDetails != null) {
                                    callDetails = null;
                                }
                            } else {
                                putMissedCallDetailsToDb(callDetails, "mytbl");
                                if (callDetails != null) {
                                    callDetails = null;
                                }
                            }
                        }
                    }


                } else {
                    if (checkForAmeyoNumber(callDetails.phoneno)) {
                        //Adding entry to ameyo table as the number is ameyo.
                        putMissedCallDetailsToDb(callDetails, "ameyo_entries");
                        if (callDetails != null) {
                            callDetails = null;
                        }
                    } else {
                        putMissedCallDetailsToDb(callDetails, "mytbl");
                        if (callDetails != null) {
                            callDetails = null;
                        }
                    }
                }
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String activityToStart = "smarter.uearn.money.activities.UearnHome";
                    Class<?> c = null;
                    try {
                        c = Class.forName(activityToStart);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Activity activity = SmarterSMBApplication.currentActivity;
                    if(activity != null) {
                        Intent intent = new Intent(activity, c);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        //activity.startActivity(intent);
                        activity.overridePendingTransition(0, 0);
                    }
                }
            });
        } else if (msg.what == MyPhoneStateListener.CALL_RECORDING) {
            if (callDetails != null) {
                return;
            }

            callDetails = new CallDetails();
            callDetails.incoming = true;
            //ToDo Settings Implematation Check.
            NotificationData.knolarity_number = (String) msg.obj;
            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                if (NotificationData.knolarity_number != null && !(NotificationData.knolarity_number.isEmpty())) {
                    knowlaritySubStatus = "";
                    knowlarity_name = "";

                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_CUSTOMER_NUMBER)) {
                        //Log.d("CLOUD_CUSTOMER", ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, ""));
                    }
                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                        cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                    }
                    if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                        cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
                    }
//                        if ((cloud1 != null && cloud1.equalsIgnoreCase(knowlarityNumber)) || (cloud2 != null && (cloud2.equalsIgnoreCase(knowlarityNumber))) || (knowlarityNumber.isEmpty()) || (knowlarityNumber.equalsIgnoreCase("")) || (knowlarityNumber.equalsIgnoreCase("null")) || (cloud2 != null && knowlarityNumber != null && (knowlarityNumber.contains(cloud2)))) {
                    start_t = new Date().toString();
                    isknowlarityCall = true;
                    callDetails.phoneno = NotificationData.knolarity_number;

                    if (NotificationData.knolarity_start_time != null && !(NotificationData.knolarity_start_time.isEmpty())) {
                        knowlarity_startTime = NotificationData.knolarity_start_time;
                        NotificationData.knolarity_start_time = null;
                    }
                    number = NotificationData.knolarity_number;
                    if (NotificationData.knolarity_name != null) {
                        knowlarity_name = NotificationData.knolarity_name;
                        if (NotificationData.substatus1 != null) {
                            knowlaritySubStatus = NotificationData.substatus1;
                        }
                        if (NotificationData.statusString != null) {
                            stage = NotificationData.statusString;
                        }
                    }

                    if (NotificationData.knolarity_name == null) {
                        NotificationData.knolarity_name = "";
                    }
                    isCloudOutgoing = true;
                    //                       }
                    NotificationData.knolarity_number = null;
                    NotificationData.knolarity_name = "";
                }
            } else {
                callDetails.phoneno = (String) msg.obj;
            }

            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CONNECTED_CUSTOMER, "");
            if(connectedCustomer != null && !connectedCustomer.isEmpty()) {
                callDetails.phoneno = connectedCustomer;
            } else {
                callDetails.phoneno = (String) msg.obj;
            }

            Log.d("SMBHOME", "Caller number is" + callDetails.phoneno);

            Log.d("SDCallRecording", "Caller number is" + callDetails.phoneno);

            //Log.i("ServiceHandler", "Caller number is " + callDetails.phoneno);
            callDetails.startTime = new Date();
            String start = CommonUtils.getTimeFormatInISO(callDetails.startTime);
            if (start != null) {
                //Log.d("ServiceHandler", "StartinISO" + start);
                insertCallState(context, "incoming", callDetails.phoneno, start);
            }

            if (shouldRecord()) {
                if (CommonUtils.checkRecordPermission(context)) {
                    startRecording();
                }
            }
        }
    }

    private void checkDialogAndLoad(Message msg) {
        //Log.d("after call", "stuff dilip");
        boolean neverAgain = ApplicationSettings.getPref(AppConstants.DO_NOT_REMIND_NOT_LOGGED_IN, false);

        if (!neverAgain) {
            boolean proceed = false;

            if (callDetails != null) {
                if (callDetails.phoneno != null) {
                    if (!callDetails.phoneno.isEmpty()) {
                        ApplicationSettings.putPref(AppConstants.STORE_NUMBER_FOR_PROCESSING, callDetails.phoneno);
                        proceed = true;
                    }
                }
            } else if (msg.obj != null) {
                ApplicationSettings.putPref(AppConstants.NOT_LOGGED_SHOW_DIALOG, true);
                ApplicationSettings.putPref(AppConstants.STORE_NUMBER_FOR_PROCESSING, (String) msg.obj);
                proceed = true;
            }

            if (proceed) {
                if (context != null) {
                    Intent showLandingScreen = new Intent(context, LandingActivity.class);
                    showLandingScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(showLandingScreen);
                }
            }
        }
    }

    private void startRecording() {
        if (isknowlarityCall) {
            //String number = "";
            if (number != null) {
                //number = knowlarityNumber;
                if(NotificationData.substatus1 != null && NotificationData.substatus1.equalsIgnoreCase("PAN APPROVED")) {

                } else {
                    if (number.length() > 5) {
                        if (number.length() > 10) {
                            number = "xxxxx" + number.substring(7, number.length());
                        } else {
                            number = "xxxxx" + number.substring(5, number.length());
                        }
                    }
                }
            }
            if (knowlarity_name == null) {
                knowlarity_name = "NA";
            }
            if (stage == null) {
                stage = "";
            }
            String message = "NAME   : " + knowlarity_name + "\nPHONE : " + number + "" + "\nSTATUS: " + stage;
            if (knowlaritySubStatus != null) {
                message = message + " | " + knowlaritySubStatus;
            }
            if(NotificationData.notes_string != null && !NotificationData.notes_string.isEmpty() && !NotificationData.notes_string.equalsIgnoreCase("null")) {
                message = message+"\n"+ NotificationData.notes_string;
            }
            for (int i = 0; i < 3; i++) {
                String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
                String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
                String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
                //&& ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE") || questionsAct.equals("MMT")))
                if (questionsAct != null && !questionsAct.isEmpty()) {

                } else if(qanda != null && !qanda.isEmpty()) {

                } else if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {

                } else {
                    boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                    if(sequencialEndpoint) {

                    } else {
                        toastMessage = Toast.makeText(context,message + "\n" + "\n*** This conversation will be recorded. ***", Toast.LENGTH_SHORT);
                        toastMessage.setGravity(Gravity.CENTER, 0, 0);
                        toastMessage.show();
                    }
                }
            }
        } else {
            String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
            String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
            String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");

            if (questionsAct != null && !questionsAct.isEmpty()) {

            } else if(qanda != null && !qanda.isEmpty()) {

            } else if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {

            } else {
                boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                if(sequencialEndpoint) {

                } else {
                    Toast toastMessage1 = Toast.makeText(context, "This conversation will be recorded. Please seek permission from other party for the recording now.", Toast.LENGTH_SHORT);
                    toastMessage1.setGravity(Gravity.CENTER, 0, 0);
                    toastMessage1.show();
                }
            }
        }

        String recordingType;
        int audio_source = -1;
        File f;
        /*if(callDetails != null && callDetails.phoneno != null) {
            f = makeOutputFile(prefs, callDetails.phoneno);
        } else {*/
        f = makeOutputFile(prefs);
        /*}*/
        if (f != null) {
            callDetails.recordFile = f.getAbsolutePath();
            callDetails.isRecording = true;

            recordingType = ApplicationSettings.getPref(AppConstants.RECORDINGTYPE, "unknown");
            if (recordingType.equals("mediaRecorder")) {
                audio_source = MediaRecorder.AudioSource.VOICE_CALL;
            } else if (recordingType.equals("audioRecorder")) {
                audio_source = MediaRecorder.AudioSource.MIC;
            }

            mediaRecordWrapper = new MediaRecordWrapper(context);
            callDetails.recordingSuccess = mediaRecordWrapper.startRecording(callDetails.recordFile, audio_source);

            if (callDetails.recordingSuccess) {
                int result_audio_source = mediaRecordWrapper.final_audio_source;

                if (result_audio_source == MediaRecorder.AudioSource.VOICE_CALL) {
                    ApplicationSettings.putPref(AppConstants.RECORDINGTYPE, "mediaRecorder");
                } else if (result_audio_source == MediaRecorder.AudioSource.MIC) {
                    ApplicationSettings.putPref(AppConstants.RECORDINGTYPE, "audioRecorder");
                }
                ApplicationSettings.putPref(AppConstants.DISABLE_CALL_REC_BUTTONS, false);

            } else {
                Toast.makeText(context, "Unable to record. Please check with your admin.", Toast.LENGTH_SHORT).show();
                deleteFile(callDetails.recordFile);
                callDetails.isRecording = false;
                int version_code = CommonUtils.getVersionCode(context);
                String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                        ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>Device not supporting call recording"
                        + "<br/> Current Activity: " + SmarterSMBApplication.currentActivity + "<br/> App Version: " + version_code;
                ServiceApplicationUsage.callErrorLog(message);
            }
        } else {
            CommonUtils.buildReportAndSend(context, "Temporary file could not be created");
        }
    }

    private void processCallEnd() {
        callDetails.endTime = new Date();
        if (shouldRecord() && mediaRecordWrapper != null) {
            new ProcessCallEndedEvent(callDetails, mediaRecordWrapper).execute();
            postProcess(callDetails);
        } else {
            callrecordOffPostProcess(callDetails);
        }
        callDetails = null;
        mediaRecordWrapper = null;
    }

    private class ProcessCallEndedEvent extends AsyncTask<Void, Void, Boolean> {
        CallDetails call;
        MediaRecordWrapper recorder;

        public ProcessCallEndedEvent(CallDetails call, MediaRecordWrapper recorder) {
            this.call = call;
            this.recorder = recorder;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return recorder.stopRecording();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //following condition is added because we should not put call detais to db if stop recording failed
            if (result) {
                //Log.i("ServiceHandler", "on post process call details phone number is " + call.phoneno);
            } else {
                Toast.makeText(context, "Low System Memory, Unable to record your call.", Toast.LENGTH_SHORT).show();
                //memoryFullNotification();
                callrecordOffPostProcess(call);
            }

        }
    }

    private void postProcess(CallDetails call) {
        long id = putCallRecordDetailsToDb(call);
        dbid = id;
        ApplicationSettings.putLongPref(AppConstants.DB_ID, dbid);
        //Log.d("Bank1AfterCallActivity", "postProcess()" + dbid);
        if (shouldPopup(call)) {  //this case only when recording is not uploaded.
            show_popup(call, id);
        } else {
            //TODO: check for ameyo number, if it is delete entry from old table, create entry in new table and wait for push notification.
            if (call != null && checkForAmeyoNumber(call.phoneno)) {
                //Deleting mytb1 entry and create a new entry in ameyo table.
                MySql dbHelper = MySql.getInstance(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("INSERT INTO ameyo_entries SELECT * FROM mytbl WHERE _id=" + dbid);
                if (db != null && db.isOpen())
                    db.close();
            } else {
                Intent intent = new Intent(context, ReuploadService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                } else {
                    context.startService(intent);
                }
            }
        }
    }

    private void callrecordOffPostProcess(CallDetails callDetails) {
        if (callDetails.recordFile != null) {
            deleteFile(callDetails.recordFile);
        }
        callDetails.isRecording = false;

        long id = putCallDetailsToDb(callDetails);
        //Log.i("Bank1AfterCallActivity","Put call details to DB id is "+id);
        dbid = id;
        ApplicationSettings.putLongPref(AppConstants.DB_ID, dbid);
        //Log.d("Bank1AfterCallActivity", "callrecordOffPostProcess()" + dbid);
        if (shouldPopup(callDetails)) {  //this case only when recording is not uploaded.
            show_popup(callDetails, id);
        } else {
            Intent intent = new Intent(context, ReuploadService.class);
            //Added By Srinath
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }


    private boolean shouldPopup(CallDetails call) {
        // modified by dilip from false to true
        /*Boolean isPopupStatus = ApplicationSettings.getPref(AppConstants.SETTING_AFTER_CALL_POPUP, true);
        if (isPopupStatus.equals(true))
            return true;
        else
            return false;*/
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        if (context != null) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void show_popup(CallDetails call, long dbid) {
        boolean showNotification = ApplicationSettings.getPref(AppConstants.SHOW_NOTIFICATION, true);
        boolean showPopup = ApplicationSettings.getPref(AppConstants.SHOW_POPUP, false);
        if (showPopup) {
            /* if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {*/
            if(NotificationData.makeACall) {
                NotificationData.makeACall = false;
                Intent intent = null;
                String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"");
                if(screen != null && (screen.equalsIgnoreCase("Auto2AfterCallActivity") || screen.equalsIgnoreCase("Auto1AfterCallActivity") )) {
                    //intent = new Intent(context, Auto1AfterCallActivity.class);
                } else {
                    intent = new Intent(context, MakeACallAfterCallActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("dbid", dbid);
                context.startActivity(intent);
            } else if(ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN) && (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"") != null) && !(ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"").isEmpty())){
                try {
                    String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"");
                    if(screen.equalsIgnoreCase("Auto2AfterCallActivity")) {
                        screen = "Auto1AfterCallActivity";
                    }
                    //String activityToStart = "com.smartersmb.activities."+screen;
                    String activityToStart = "smarter.uearn.money.activities." + screen;
                    Class<?> c = Class.forName(activityToStart);
                    Intent intent = new Intent(context, c);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("dbid", dbid);
                    intent.putExtra("callrecording", "completed");
                    if (isCloudOutgoing && knowlarityNumber != null && !(knowlarityNumber.isEmpty())) {
                        intent.putExtra("knowlarityNumber", knowlarityNumber);
                        if (knowlarity_startTime != null) {
                            intent.putExtra("start_t", knowlarity_startTime);
                        }
                        intent.putExtra("end_t", end_t);
                    }
                    if (callDetails != null) {
                        if (callDetails.phoneno != null) {
                            if (!callDetails.phoneno.isEmpty()) {
                                if (checkForAmeyoNumber(callDetails.phoneno)) {
                                    intent.putExtra("cloudcall", true);
                                }
                            }
                        }
                    }

                    String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
                    if (questionsAct != null && !questionsAct.isEmpty()) {
                        if (!UearnActivity.callEndedFromDuringCall) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("dbid", String.valueOf(dbid));
                            editor.putString("callrecording", "completed");
                            editor.putString("knowlarityNumber", knowlarityNumber);
                            editor.putString("start_t", knowlarity_startTime);
                            editor.putString("end_t", end_t);
                            if (callDetails != null) {
                                if (callDetails.phoneno != null) {
                                    if (!callDetails.phoneno.isEmpty()) {
                                        if (checkForAmeyoNumber(callDetails.phoneno)) {
                                            editor.putString("cloudcall", String.valueOf(true));
                                        }
                                    }
                                }
                            }
                            editor.commit();
                        }
                    }
                    if (questionsAct != null && !questionsAct.isEmpty()) {
                        if (NotificationData.dialledCustomerNumber != null && !NotificationData.dialledCustomerNumber.isEmpty()) {
                            if (UearnActivity.callEndedFromDuringCall) {
                                if(!SmarterSMBApplication.showSingleAcp) {
                                    SmarterSMBApplication.showSingleAcp = true;
                                    Log.i("CallEnded","CallEnded 1");
                                    //context.startActivity(intent);
                                }
                            } else if (NotificationData.statusString != null && !NotificationData.statusString.isEmpty() && NotificationData.statusString.equalsIgnoreCase("ASSIGNTOL2")) {
                                Log.i("CallEnded","CallEnded 2");
                                //context.startActivity(intent);
                            } else {
                                if(!NotificationData.isSocketResponse) {
                                    Log.i("CallEnded","CallEnded 3");
                                    //context.startActivity(intent);
                                }
                            }
                        } else if (!NotificationData.isSocketResponse) {
                            boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
                            if (truePredictive) {
                               if(SmarterSMBApplication.callEndedACPTimeout) {
                                   SmarterSMBApplication.callEndedACPTimeout = false;
                               } else {
                                   //context.startActivity(intent);
                               }
                            }
                        }
                    } else {
                        boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
                        if (truePredictive) {
                            if (UearnActivity.callEndedFromDuringCall) {

                            } else {
                                if (UearnActivity.serverDialingRedialScenario || SmarterSMBApplication.tpdRedialScenario) {
                                    UearnActivity.serverDialingRedialScenario = false;
                                    SmarterSMBApplication.tpdRedialScenario = false;
                                } else if (UearnHome.callEndedFromHomeScreen) {
                                    UearnHome.callEndedFromHomeScreen = false;
                                } else if (SmarterSMBApplication.callEndedACPTimeout) {
                                    SmarterSMBApplication.callEndedACPTimeout = false;
                                } else {
                                    if(SmarterSMBApplication.navigateToACP) {

                                    } else {
                                        SmarterSMBApplication.navigateToACP = true;
                                        Log.i("CallEnded","CallEnded 5");
                                        //context.startActivity(intent);
                                    }
                                }
                            }
                        } else {
                            Log.i("CallEnded","CallEnded 6");
                            //context.startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(context, AfterCallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("dbid", dbid);
                if (isCloudOutgoing && knowlarityNumber != null && !(knowlarityNumber.isEmpty())) {
                    intent.putExtra("knowlarityNumber", knowlarityNumber);
                    if (knowlarity_startTime != null) {
                        intent.putExtra("start_t", knowlarity_startTime);
                    }
                    intent.putExtra("end_t", end_t);
                }
                if (callDetails != null) {
                    if (callDetails.phoneno != null) {
                        if (!callDetails.phoneno.isEmpty()) {
                            if (checkForAmeyoNumber(callDetails.phoneno)) {
                                intent.putExtra("cloudcall", true);
                            }
                        }
                    }
                }
                Log.i("CallEnded","CallEnded 7");
                //context.startActivity(intent);
            }

        } else if (showNotification) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

            Intent afterCallPopupIntent = new Intent(context, AfterCallActivity.class);
            afterCallPopupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            afterCallPopupIntent.putExtra("notification_id", RECORDING_NOTIFICATION_ID1);
            afterCallPopupIntent.putExtra("phoneNumber", call.phoneno);
            afterCallPopupIntent.putExtra("incoming", call.incoming);
            afterCallPopupIntent.putExtra("startTime", call.startTime.getTime());
            afterCallPopupIntent.putExtra("endTime", call.endTime.getTime());
            afterCallPopupIntent.putExtra("dbid", dbid);
            String callerName = CommonUtils.getContactName(context, call.phoneno);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, afterCallPopupIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent delete_intent = new Intent();
            delete_intent.setAction("DELETE_INTENT");
            if (dbid != 0) {
                delete_intent.putExtra("DBID", dbid);
            }
            PendingIntent delete_pendingIntent = PendingIntent.getBroadcast(context, 0, delete_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            String commentText = "";
            RemoteViews remoteView = new RemoteViews("smarter.uearn.money", R.layout.after_call_notification);
            if (!callerName.equals("")) {
                if (shouldRecord()) {
                    remoteView.setImageViewResource(R.id.call_rec_on_or_off, R.drawable.red);
                    commentText = "Share call recording or Digital Brochure";
                } else {
                    commentText = "Share Digital Brochure";
                }
                remoteView.setTextViewText(R.id.callerNameOrNumber, callerName);
                remoteView.setTextViewText(R.id.commentText, commentText);
            } else {
                if (shouldRecord()) {
                    remoteView.setImageViewResource(R.id.call_rec_on_or_off, R.drawable.red);
                    commentText = "Contacts or share Call recording or Digital Brochure ";
                } else {
                    commentText = "Contacts or share Digital Brochure";
                }
                remoteView.setTextViewText(R.id.callerNameOrNumber, call.phoneno);
                remoteView.setTextViewText(R.id.commentText, commentText);
            }
            Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.small_logo)
                    .setWhen(0)
                    .setDeleteIntent(delete_pendingIntent)
                    .setContentIntent(pendingIntent)
                    .setOngoing(false);
            Notification notification = notificationBuilder.build();
            notification.contentView = remoteView;
            // mNotificationManager.notify(RECORDING_NOTIFICATION_ID1, notification);
            // removeNotification(mNotificationManager, RECORDING_NOTIFICATION_ID1, dbid);
        }
    }

    private boolean chechNumberExists(String number) {
        String mobileNo = CommonUtils.checkNo(number);
        if(mobileNo != null) {
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
            Cursor cursor = db1.rawQuery("SELECT * FROM remindertbNew where TO1=" + "'" + mobileNo + "'", null);
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
            return false;
        }
    }

    private void removeNotification(long dbid) {
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("UPLOAD_STATUS", 1);
        long id = db.update("mytbl", cv, "_id=" + dbid, null);
        if (db != null && db.isOpen()) {
            db.close();
        }
        Intent intent = new Intent(context, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private boolean shouldRecord() {
        if (ApplicationSettings.getPref(AppConstants.SETTING_CALL_RECORDING_STATE, false)) {
            return true;
        } else {
            return false;
        }
    }


    private File makeOutputFile(SharedPreferences prefs) {
        File dir = new File(DEFAULT_STORAGE_LOCATION);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                CommonUtils.buildReportAndSend(context, "RecordService::makeOutputFile unable to create directory " + dir + ": " + e);
                Toast t = Toast.makeText(context,"CallRecorder was unable to create the directory " + dir + " to store recordings: " + e, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return null;
            }
        } else {
            if (!dir.canWrite()) {
                CommonUtils.buildReportAndSend(context, "RecordService::makeOutputFile does not have write permission for directory: " + dir);
                Toast t = Toast.makeText(context,"CallRecorder does not have write permission for the directory directory " + dir + " to store recordings", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return null;
            }
        }

        String prefix = getFileName();
        int audioformat = 2;

        if(ApplicationSettings.containsPref(AppConstants.TRANSCRIPT_AUDIO_FORMATE)) {
            String outputFormat = ApplicationSettings.getPref(AppConstants.TRANSCRIPT_AUDIO_FORMATE, "");
            if ((outputFormat != null) && !(outputFormat.isEmpty())) {
                if(outputFormat.equalsIgnoreCase("mpeg4")) {
                    audioformat = 2;
                } else if(outputFormat.equalsIgnoreCase("amrwb")) {
                    audioformat = 4;
                } else if(outputFormat.equalsIgnoreCase("amrnb")) {
                    audioformat = 3;
                } else if(outputFormat.equalsIgnoreCase("aacadts")) {
                    audioformat = 6;
                } else if(outputFormat.equalsIgnoreCase("default")) {
                    audioformat = 2;
                } else if(outputFormat.equalsIgnoreCase("threegpp")) {
                    audioformat = 1;
                } else if(outputFormat.equalsIgnoreCase("webm")) {
                    audioformat = 9;
                } else {
                    audioformat = 2;
                }
            } else {
                audioformat = 2;
            }
        } else {
            audioformat = 2;
        }

        String suffix = "";
        switch (audioformat) {
            case MediaRecorder.OutputFormat.THREE_GPP:
                suffix = ".3gpp.smb";
                break;
            case MediaRecorder.OutputFormat.MPEG_4:
                //suffix = ".mpg.smb";
                suffix = ".mp4.smb";
                break;
            case MediaRecorder.OutputFormat.AMR_NB:
                suffix = ".amr.smb"; //".amr"
                break;
            case MediaRecorder.OutputFormat.AMR_WB:
                suffix = ".awb.smb";
                break;
            case MediaRecorder.OutputFormat.WEBM:
                suffix = ".webm.smb"; //".webm"
                break;
            case MediaRecorder.OutputFormat.AAC_ADTS:
                suffix = ".aac.smb"; //".amr"
                break;
        }

        try {
            return File.createTempFile(prefix, suffix, dir);
        } catch (IOException e) {

            long n = randomLongInternal();
            if (n == Long.MIN_VALUE) {
                n = 0;
            } else {
                n = Math.abs(n);
            }
            String name = prefix + Long.toString(n) + suffix;
            File f = new File(dir, name);

            if(f == null) {
                CommonUtils.buildReportAndSend(context, "RecordService::makeOutputFile does not have write permission for directory: " + dir);
                Toast t = Toast.makeText(context,"CallRecorder was unable to create temp file in " + dir + ": " + e, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                return null;
            }

            return f;
        }
    }

    public static long randomLongInternal() {
        SecureRandom randomNumberGenerator = new SecureRandom();
        return randomNumberGenerator.nextLong();
    }

    private String getTranscriptionFilePath() {
        File root = new File(Environment.getExternalStorageDirectory(), "TextScripts");
        if (!root.exists()) {
            root.mkdirs();
        }
        File scriptfile = new File(root, "TRS-"+getFileName() + "script.txt");
        return scriptfile.getAbsolutePath();
    }

    private String getFileName() {
        String user_id = "", date = "", leadSource = "";

        if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
            user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Calendar cal = Calendar.getInstance();
            Date d = new Date();
            cal.setTime(d);
            Date startTime = cal.getTime();
            String requiredStartTime = CommonUtils.getTimeFormatInISO(startTime);
            requiredStartTime = requiredStartTime.replace(":", "-");
            requiredStartTime = requiredStartTime.replace(".", "-");
            //Log.d("requiredStartTime" , requiredStartTime);
            date = requiredStartTime;
        }
        String prefix = user_id;
        prefix += "" + "-";
        boolean appendLeadSource = ApplicationSettings.getPref(AppConstants.APPEND_LEAD_SOURCE, false);
        if (appendLeadSource) {
            if(callDetails != null && callDetails.phoneno != null) {
                prefix += callDetails.phoneno;
                leadSource = getLeadSource(callDetails.phoneno);
            }
            prefix += "" + "-";
            if(leadSource != null && !leadSource.isEmpty()){
                prefix += leadSource;
            } else {
                prefix += "N";
            }
        } else {
            if(callDetails != null && callDetails.phoneno != null) {
                prefix += callDetails.phoneno;
            }
        }
        prefix += "" + "-";
        prefix += date;
        return prefix;
    }

    private void deleteFile(String file) {
        File f = new File(file);
        if (f.exists() == true) {
            f.delete();
        }
    }

    //TODO: Combine below two functions
    public long putCallDetailsToDb(CallDetails callDetails) {
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String callerName = CommonUtils.getContactName(context, callDetails.phoneno);

        if (callDetails.incoming) {
            if (!isknowlarityCall) {
                cv.put("EVENT_TYPE", "call_in_rec_off");
                cv.put("FROM1", callDetails.phoneno);
                cv.put("MSG_RECEPIENT_NO", callDetails.phoneno);
                if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                    cv.put("TO1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                    //Log.d("Agent Number", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                }
                cv.put("SUBJECT", "Incoming Call");
                if (knowlarity_name != null && !(knowlarity_name.isEmpty())) {
                    cv.put("CALLER", knowlarity_name);
                } else {
                    cv.put("CALLER", callerName);
                }
            } else {
                cv.put("EVENT_TYPE", "call_out_rec_off");
                if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                    cv.put("FROM1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                }
                if ((cloud1 != null && cloud1.equalsIgnoreCase(knowlarityNumber) || (cloud2 != null && (cloud2.equalsIgnoreCase(knowlarityNumber))))) {
                    if ((cloud1 != null && cloud1.equalsIgnoreCase(callDetails.phoneno) || (cloud2 != null && (cloud2.equalsIgnoreCase(callDetails.phoneno))))) {
                        if(ApplicationSettings.containsPref(AppConstants.CLOUD_CUSTOMER_NUMBER)  && (ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "") != null) && !(ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "").isEmpty())) {
                            cv.put("TO1", ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, ""));
                        } else  {
                            cv.put("TO1", callDetails.phoneno);
                        }
                    } else {
                        cv.put("TO1", callDetails.phoneno);
                    }
                } else {
                    cv.put("TO1", callDetails.phoneno);
                }
                cv.put("MSG_RECEPIENT_NO", callDetails.phoneno);
                cv.put("SUBJECT", "Outgoing Call");
                cv.put("CALLER", callerName);
            }
        } else {
            cv.put("EVENT_TYPE", "call_out_rec_off");
            if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                cv.put("FROM1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
            }
            cv.put("TO1", callDetails.phoneno);
            cv.put("MSG_RECEPIENT_NO", callDetails.phoneno);
            cv.put("SUBJECT", "Outgoing Call");
        }
        cv.put("EMAIL", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null));
        cv.put("PARENT", 0);
        if (callDetails.startTime != null) {
            //Log.d("ServiceHandler", "" + callDetails.startTime.toString());
            cv.put("STARTTIME", CommonUtils.getTimeFormatInISO(callDetails.startTime));
        }
        if (callDetails.endTime != null)
            cv.put("ENDTIME", CommonUtils.getTimeFormatInISO(callDetails.endTime));
        cv.put("UNREAD", "false");

        if (shouldPopup(callDetails)) {
            cv.put("UPLOAD_STATUS", 0);        //should not upload to server coz user may choose to send sms or setalarm in popup
        }
        if (latitude != null && longitude != null) {
            cv.put("DISP_MAP_LOC", 1);
            cv.put("LAT", latitude);
            cv.put("LONG", longitude);
        }
        //cv.put("FILE_PATH", callDetails.recordFile);
        //Added By Srinath.k
        long id = 0L;
        if (checkForAmeyoNumber(callDetails.phoneno)) {
            id = db.insert("ameyo_entries", null, cv);
        } else {
            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
            }
            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
            }
            if(callDetails.phoneno != null && !callDetails.phoneno.isEmpty()) {
                if ((cloud1 != null && cloud1.equalsIgnoreCase(callDetails.phoneno) || (cloud2 != null && (callDetails.phoneno.contains(cloud2))))) {

                } else {
                    id = db.insert("mytbl", null, cv);
                }
            } else {
                id = db.insert("mytbl", null, cv);
            }
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if(dbHelper != null)
            dbHelper.close();
        //Log.d("Bank1AfterCallActivity", " putCallDetailsToDb - DBID from Service Handler " + id);
        return id;
    }

    public long putCallRecordDetailsToDb(CallDetails callDetails) {
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("EMAIL", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null));
        cv.put("PARENT", 0);
        if (callDetails.startTime != null)
            cv.put("STARTTIME", CommonUtils.getTimeFormatInISO(callDetails.startTime));
        if (callDetails.endTime != null)
            cv.put("ENDTIME", CommonUtils.getTimeFormatInISO(callDetails.endTime));
        cv.put("UNREAD", "false");
        cv.put("SUBJECT", "CALL RECORD");

        String callerName = CommonUtils.getContactName(context, callDetails.phoneno);
        cv.put("FILE_PATH", callDetails.recordFile);
        if(callDetails.transcriptionPath != null && !(callDetails.transcriptionPath.isEmpty())) {
            cv.put("TRANSCRIPTION_PATH", callDetails.transcriptionPath);
        }
        cv.put("DISP_CALL_REC", 1);
        if (callDetails.incoming == true) {
            if (!isknowlarityCall) {
                cv.put("EVENT_TYPE", "call_in_rec_on");
                cv.put("FROM1", callDetails.phoneno);
                if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                    cv.put("TO1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                }
                cv.put("MSG_RECEPIENT_NO", callDetails.phoneno);
                cv.put("MESSAGE", "Incoming Call");
                if (knowlarity_name != null && !knowlarity_name.isEmpty()) {
                    cv.put("CALLER", knowlarity_name);
                } else {
                    cv.put("CALLER", callerName);
                }
            } else {
                cv.put("EVENT_TYPE", "call_out_rec_on");
                if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                    cv.put("FROM1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                }
                if ((cloud1 != null && cloud1.equalsIgnoreCase(knowlarityNumber) || (cloud2 != null && (cloud2.equalsIgnoreCase(knowlarityNumber))))) {
                    if ((cloud1 != null && cloud1.equalsIgnoreCase(callDetails.phoneno) || (cloud2 != null && (cloud2.equalsIgnoreCase(callDetails.phoneno))))) {
                        if(ApplicationSettings.containsPref(AppConstants.CLOUD_CUSTOMER_NUMBER)  && (ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "") != null) && !(ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "").isEmpty())) {
                            cv.put("TO1", ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, ""));
                        } else  {
                            cv.put("TO1", callDetails.phoneno);
                        }
                    } else {
                        cv.put("TO1", callDetails.phoneno);
                    }
                } else {
                    cv.put("TO1", callDetails.phoneno);
                }
                cv.put("MESSAGE", "Outgoing Call");
                cv.put("MSG_RECEPIENT_NO", callDetails.phoneno);
                cv.put("CALLER", callerName);
            }
        } else {
            cv.put("EVENT_TYPE", "call_out_rec_on");
            if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                cv.put("FROM1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
            }
            cv.put("TO1", callDetails.phoneno);
            cv.put("MESSAGE", "Outgoing Call");
            cv.put("MSG_RECEPIENT_NO", callDetails.phoneno);
        }
        if (shouldPopup(callDetails)) {
            cv.put("UPLOAD_STATUS", 0);        //should not upload to server coz user may choose to send sms or setalarm in popup
        }
        if (latitude != null && longitude != null) {
            cv.put("DISP_MAP_LOC", 1);
            cv.put("LAT", latitude);
            cv.put("LONG", longitude);
        }
        //Added By Srinath.k
        long id = 0L;
        if (checkForAmeyoNumber(callDetails.phoneno)) {
            id = db.insert("ameyo_entries", null, cv);
        } else {
            id = db.insert("mytbl", null, cv);
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if(dbHelper != null)
            dbHelper.close();
        //Log.d("", "putCallRecordDetailsToDb - DBID from Service Handler " + id);
        return id;
    }

    private long putMissedCallDetailsToDb(CallDetails callDetails, String table_name) {

        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        //Log.i("ServiceHandler", "Missed Caller number is " + callDetails.phoneno);
        cv.put("PARENT", 0);
        cv.put("EMAIL", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null));
        cv.put("EVENT_TYPE", "call_missed");
        if (callDetails.startTime != null) {
            cv.put("STARTTIME", CommonUtils.getTimeFormatInISO(callDetails.startTime));
            cv.put("UNREAD", "false");
            cv.put("SUBJECT", "Missed Call");

            String callerName = CommonUtils.getContactName(context, callDetails.phoneno);
            cv.put("CALLER", callerName);
        }
        //Log.d("ServiceHandler","Incoming Call Number" +callDetails.phoneno);
        cv.put("FROM1", callDetails.phoneno);
        cv.put("MSG_RECEPIENT_NO", callDetails.phoneno);
        cv.put("TO1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
       /* LatLong latLong = CommonUtils.getLatLong(context);
        latitude = latLong.latitude;
        longitude = latLong.longitude;*/
        if (latitude != null && longitude != null) {
            cv.put("DISP_MAP_LOC", 1);
            cv.put("LAT", latitude);
            cv.put("LONG", longitude);
        }

        if (table_name.equalsIgnoreCase("ameyo_entries")) {
            cv.put("UPLOAD_STATUS", 0);
        }

        long id = db.insert(table_name, null, cv);
        db.close();

        Intent intent = new Intent(context, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        return id;

    }

    public boolean isPrivateNumber(String phoneNumber, String countryCode) {
        phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
        //String[] privateContacts = loadPrivateContacts();
        String[] privateContacts = loadPrivateContacts(context);
        if (privateContacts != null) {
            for (int i = 0; i < privateContacts.length; i++) {
                if (PhoneNumberUtils.compare(phoneNumber, privateContacts[i])) {
                    //Log.d("Phone Number", phoneNumber);
                    //Log.d("Phone Number", privateContacts[i]);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isJunkNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
        //String[] privateContacts = loadPrivateContacts();
        String[] junkContacts = loadJunkContacts(context);
        if (junkContacts != null) {
            for (int i = 0; i < junkContacts.length; i++) {
                if (PhoneNumberUtils.compare(phoneNumber, junkContacts[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHelplineNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
        if (ApplicationSettings.containsPref(AppConstants.HELP_LINE)) {
            String helpLine = ApplicationSettings.getPref(AppConstants.HELP_LINE, "");
            if(helpLine != null && !(helpLine.isEmpty())) {
                return phoneNumber.equalsIgnoreCase(helpLine);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public boolean isLeadNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
        String[] leadContacts = loadContacts(AppConstants.LEAD_CONTACTS);
        if (leadContacts != null) {
            for (int i = 0; i < leadContacts.length; i++) {
                if (PhoneNumberUtils.compare(phoneNumber, leadContacts[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCloudTelephonyNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
        String[] leadContacts = loadContacts(AppConstants.AMEYO_NUMBERS);
        if (leadContacts != null) {
            for (int i = 0; i < leadContacts.length; i++) {
                if (PhoneNumberUtils.compare(phoneNumber, leadContacts[i])) {
                    //Log.d("ServiceHandler", "Ameyo Number is true");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTeamMemberNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
        String[] leadContacts = loadContacts(AppConstants.TEAM_MEMBER_CONTACTS);
        if (leadContacts != null) {
            for (int i = 0; i < leadContacts.length; i++) {
                if (PhoneNumberUtils.compare(phoneNumber, leadContacts[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String[] loadPrivateContacts() {
        String contacts = ApplicationSettings.getPref(AppConstants.PRIVATE_CONTACTS, null);
        if (contacts != null) {
//            int count = PrivateContactsActivity.countOccurrences(contacts, ',');
//            String[] splittedContacts = new String[count + 1];
//            splittedContacts = contacts.split(",");
//            return splittedContacts;
        }
        return null;
    }

    public static String[] loadPrivateContacts(Context context1) {
        if (context1 != null) {
            MySql dbHelper = MySql.getInstance(context1);
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
            Cursor cursor = db1.query("PrivateContacts", null, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                try {
                    String[] splittedContacts = new String[cursor.getCount()];
                    int i = 0;
                    while (cursor.moveToNext()) {
                        String number = cursor.getString(cursor.getColumnIndex("NUMBER"));
                        splittedContacts[i] = number;
                        i++;
                    }
                    return splittedContacts;
                } finally {
                    cursor.close();
                    db1.close();
                }

            }
        }
        return null;
    }


    public static String[] loadJunkContacts(Context context1) {
        if (context1 != null) {
            MySql dbHelper = MySql.getInstance(context1);
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
            Cursor cursor = db1.query("JunkNumbers", null, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                try {
                    String[] splittedContacts = new String[cursor.getCount()];
                    int i = 0;
                    while (cursor.moveToNext()) {
                        String number = cursor.getString(cursor.getColumnIndex("NUMBER"));
                        splittedContacts[i] = number;
                        i++;
                    }
                    return splittedContacts;
                } finally {
                    cursor.close();
                    db1.close();
                }

            }
        }
        return null;
    }


    public String[] loadContacts(String key) {
        String leadContacts = ApplicationSettings.getPref(key, null);
        if (leadContacts != null) {
            String[] splittedLeadContacts;
            splittedLeadContacts = leadContacts.split(",");
            return splittedLeadContacts;
        }
        return null;
    }

    private boolean checkForDontRecordNummber(String number) {
        boolean dont_record_private_contacts = ApplicationSettings.getPref(AppConstants.DONT_CALL_RECORD_PRIVATE_CONTACTS, true);
        boolean recordOnlyLeadNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_LEAD_CONTACTS, false);
        //boolean recordOnlyCloudTelephonyNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_CLOUND_TELEPHONY_NUMBERS, false);
        boolean recordOnlyCloudTelephonyNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_CLOUND_TELEPHONY_NUMBERS, true);
        recordOnlyCloudTelephonyNumbers = true;

        boolean recordOnlyTeamMembersNumbers = ApplicationSettings.getPref(AppConstants.ONLY_RECORD_TEAM_MEMBERS_NUMBERS, false);

        if(NotificationData.privateCall){
            return true;
        }
        if (number != null) {
            if (dont_record_private_contacts) {
                dontRecordNumber = isPrivateNumber(number, "IN");
            }  else {
                //TODO:Commented By Srinath.k dont remove the code. future use
               /* dontRecordNumber = true;
                if (recordOnlyLeadNumbers) {
                    if (isLeadNumber(number))
                        dontRecordNumber = false;
                }
                if (recordOnlyTeamMembersNumbers) {
                    if (isTeamMemberNumber(number))
                        dontRecordNumber = false;
                }
                if (recordOnlyCloudTelephonyNumbers) {
                    if (isCloudTelephonyNumber(number))
                        dontRecordNumber = false;
                }*/ // Until Here
                dontRecordNumber = false;
            }
        }

        if(ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN) && (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"") != null) && !(ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"").isEmpty())) {
            try {
                String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                if(context != null && screen.equalsIgnoreCase(AppConstants.BANK1)) {
                    if(ApplicationSettings.containsPref(AppConstants.USER_ROLE) && ApplicationSettings.getPref(AppConstants.USER_ROLE, "") != null) {
                        String role = ApplicationSettings.getPref(AppConstants.USER_ROLE,"");
                        if (!role.equalsIgnoreCase("user") || !role.equalsIgnoreCase("non-user")) {
                            dontRecordNumber = true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dontRecordNumber;
    }

    private boolean checkForAmeyoNumber(String phoneNumber) {
        if(phoneNumber != null && !phoneNumber.isEmpty()) {
            phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
            String[] ameyoNumber = loadAmeyoNumbers();
            //Log.i("SmarterBIZ","Ameyo number is "+ameyoNumber);
            if (ameyoNumber != null) {
                for (int i = 0; i < ameyoNumber.length; i++) {
                    if (PhoneNumberUtils.compare(phoneNumber, ameyoNumber[i])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String[] loadAmeyoNumbers() {
        String contacts = ApplicationSettings.getPref(AppConstants.AMEYO_NUMBERS, null);
        //Log.i("SmsComposeActivity","Countacts are "+contacts);
        if (contacts != null) {
//            int count = PrivateContactsActivity.countOccurrences(contacts, ',');
//            String[] splittedContacts = new String[count + 1];
//            splittedContacts = contacts.split(",");
//            return splittedContacts;
        }
        return null;
    }

    private void insertCallState(Context context, String state, String number, String startTime) {
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STATE", state);
        contentValues.put("NUMBER", number);
        contentValues.put("START_TIME", startTime);
        db.insert("CallData", null, contentValues);
        //Log.d("CallRecording", "insertCallState" + number + state + startTime);
        db.close();
    }

    private void builddailog(Context context, String num) {
        Intent intent = new Intent(context, CustomActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("customer_number", num);
        context.startActivity(intent);
    }

    private String getLeadSource(String number) {
        String leadSource = "";
        try {
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if(number != null && !number.isEmpty()) {
                String selection = "TO1=" + "'" + number + "'";
                Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
                }
                if (dbase.isOpen()) {
                    dbase.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leadSource;
    }

//    private static void sendAppStatusInfoToServer(){
//        JSONObject jsonObject = new JSONObject();
//        try {
//            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")){
//                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
//            }
//            jsonObject.put("activity_type", "In Call");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        DataUploadUtils.postAppStatus(Urls.postAppStatus(), jsonObject);
//    }
}

