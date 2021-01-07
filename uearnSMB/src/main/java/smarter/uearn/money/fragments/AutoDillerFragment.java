package smarter.uearn.money.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import smarter.uearn.money.activities.AutoDailerActivity;
import smarter.uearn.money.activities.UearnActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.ReuploadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Srinath on 30-01-2018.
 */

public class AutoDillerFragment extends Fragment implements View.OnClickListener{

    public TextView name, custNumber, campaignName, stage, dateTv, stage1, stage2, counterText, messageText, firstCallText, followupText;
    private TextView first_done, first_todo, f_done, f_todo, flpTv, notesTv;
    private TextView autoText, agentNameText;
    private RelativeLayout stopImage, playIcon, progressbar, messageLayout;
    private RelativeLayout startLayout;
    private LinearLayout frameLayout;
    private Button stop, startButton;

    SQLiteDatabase db;
    String number = "",nameFromdb, notes = "";
    int itemId;
    private String appointmentId = "", substatus1 = "", substatus2 = "", statusFronDb = "", orderPotential = "";
    private int rnr_count = 0;
    private CountDownTimer countDownTimer;
    private Long start = 0L, end = 0L, dbId = 0L;
    boolean isCounterRunning = false, firstcallCheck = false;
    public Context context;
    public String leadSource = "";
    private List<CustomerLite> customerList = null;
    private List<CustomerLite> tempcustomerList = null;
    public static String sequencialUUID = "";
    private String company = "";
    public final static String AFTER_CALL_LEAD = "after_call_lead";
    public final static String CUSTOMER_TO_CONTACT = "customer_tocontact";

//    public static String autoDialerFragmentTransactionId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.autodial_layout, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        name = view.findViewById(R.id.name);
        custNumber = view.findViewById(R.id.customerNumber);
        campaignName = view.findViewById(R.id.campaignName);
        stage = view.findViewById(R.id.stage);
        dateTv = view.findViewById(R.id.date);
        stage1 = view.findViewById(R.id.substage1);
        stage2 = view.findViewById(R.id.substage2);
        counterText = view.findViewById(R.id.counterText);
        messageText = view.findViewById(R.id.message);
        firstCallText = view.findViewById(R.id.firstcall_count);
        followupText = view.findViewById(R.id.followup_count);
        notesTv = view.findViewById(R.id.notes);
        stop = view.findViewById(R.id.stop);

        first_todo = view.findViewById(R.id.first_todo_text);
        first_done = view.findViewById(R.id.first_done);
        f_todo = view.findViewById(R.id.f_todo_text);
        f_done = view.findViewById(R.id.f_done_text);
        flpTv = view.findViewById(R.id.flpTv);
        frameLayout = getActivity().findViewById(R.id.fragmentLayout);

        startLayout = getActivity().findViewById(R.id.start_layout);
        startButton = getActivity().findViewById(R.id.start_button);
        autoText = getActivity().findViewById(R.id.autoText);
        agentNameText = getActivity().findViewById(R.id.agentNameText);

        stopImage = view.findViewById(R.id.stop_image);
        playIcon = view.findViewById(R.id.playIcon);
        progressbar = view.findViewById(R.id.progressbar);
        stopImage.setOnClickListener(this);
        playIcon.setOnClickListener(this);
        progressbar.setOnClickListener(this);

        messageLayout = view.findViewById(R.id.message_layout);

        context = getContext();
        MySql dbHelper = MySql.getInstance(getActivity());
        db = dbHelper.getWritableDatabase();
        start = UearnHome.getstartTime();
        end = UearnHome.getendTime();

        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }

        String useCampaign = ApplicationSettings.getPref(AppConstants.USE_CAMPAIGN, "");
        if (useCampaign != null && !useCampaign.isEmpty() && !useCampaign.equals("null")) {
            leadSource = useCampaign;
        } else {
            leadSource = "";
        }

        String fieldsToShow = ApplicationSettings.getPref(AppConstants.FIELDS_TO_SHOW, "");
        if(fieldsToShow != null && !fieldsToShow.isEmpty()) {
            try {
                JSONObject jsonobject = new JSONObject(fieldsToShow);
                if (jsonobject.has("AutoDillerFragment")) {
                    String value = jsonobject.getString("AutoDillerFragment");
                    List<String> items = Arrays.asList(value.split("\\s*,\\s*"));
                    if(items != null && items.size() > 0) {
                        for(int i=0; i<items.size(); i++){
                            String viewName = items.get(i);
                            viewName = viewName.replaceAll("[^A-Za-z0-9]","");
                            int resID = context.getResources().getIdentifier(viewName,"id", context.getPackageName());

                            switch(resID)
                            {
                                case R.id.stage :
                                    stage.setVisibility(View.GONE);
                                    break;
                                case R.id.substage1 :
                                    stage1.setVisibility(View.GONE);
                                    break;
                                case R.id.substage2 :
                                    stage2.setVisibility(View.GONE);
                                    break;
                                default :
                                    break;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
//        if (questionsAct != null && !questionsAct.isEmpty() && ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE")))) {
//            stage.setVisibility(View.GONE);
//            stage1.setVisibility(View.GONE);
//            stage2.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onResume() {

        super.onResume();

        boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
        if(sequencialEndpoint) {
            if (UearnActivity.redialScenario || UearnActivity.redialScenario) {
                stopImage.setVisibility(View.GONE);
                playIcon.setVisibility(View.GONE);
                progressbar.setVisibility(View.GONE);
                messageLayout.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                stopImage.setVisibility(View.GONE);
            } else {
                stopImage.setVisibility(View.VISIBLE);
                playIcon.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.VISIBLE);
                messageLayout.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
                stopImage.setVisibility(View.VISIBLE);
                sequentialDiallingScenario();
            }
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            normalCallDiallingScenario();
        }
    }

    private void normalCallDiallingScenario() {

        NotificationData.privateCall = false;
        NotificationData.dialledCustomerNumber = "";

        String useCampaign = ApplicationSettings.getPref(AppConstants.USE_CAMPAIGN, "");
        if (useCampaign != null && !useCampaign.isEmpty() && !useCampaign.equals("null")) {
            leadSource = useCampaign;
        } else {
            leadSource = "";
        }

        if(stop != null) {
            stop.setOnClickListener(this);
        }
        NotificationData.callFromDialer = true;
        NotificationData.substatus1 = "";
        NotificationData.substatus2 = "";
        NotificationData.remarks = "";
        createfollowupAutoDailerView();
        followupsCount();
    }

    private void sequentialDiallingScenario() {

        NotificationData.privateCall = false;
        NotificationData.dialledCustomerNumber = "";

        UearnActivity.callRecordingStarted = false;
        //       Bank1AfterCallActivity.callConnectedAndAcknowledged = false;

        String useCampaign = ApplicationSettings.getPref(AppConstants.USE_CAMPAIGN, "");
        if (useCampaign != null && !useCampaign.isEmpty() && !useCampaign.equals("null")) {
            leadSource = useCampaign;
        } else {
            leadSource = "";
        }

        if(stop != null) {
            stop.setOnClickListener(this);
        }
        NotificationData.callFromDialer = true;
        NotificationData.substatus1 = "";
        NotificationData.substatus2 = "";
        NotificationData.remarks = "";

        boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
        String noOfCustomersToCall = ApplicationSettings.getPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, "");
        int noOfCustomersToDial = 0;

        if(noOfCustomersToCall != null && !noOfCustomersToCall.isEmpty()) {
            noOfCustomersToDial = Integer.valueOf(noOfCustomersToCall);
        }

        if(noOfCustomersToDial >= 1 && sequencialEndpoint) {
            playIcon.setVisibility(View.GONE);
            createfollowupAutoDailerViewSequential();
            followupsCount();
        } else {
            playIcon.setVisibility(View.VISIBLE);
            createfollowupAutoDailerView();
            followupsCount();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.stop) {
            // fragmentTransaction(smbHome);
            UearnHome.autoDial = false;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                isCounterRunning = false;
            }
            frameLayout.setVisibility(View.GONE);
            if(getFragmentManager() != null)
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.frame_container)).commitAllowingStateLoss();
            startLayout.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.VISIBLE);
            agentNameText.setVisibility(View.VISIBLE);
            autoText.setVisibility(View.VISIBLE);

        } else if (id == R.id.playIcon) {
            if (getContext() != null && CommonUtils.isNetworkAvailable(getContext())) {
                if (number != null) {
                    if(countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    AutoDailerActivity.PlayListener playListener = new AutoDailerActivity.PlayListener() {
                        @Override
                        public void resumeActivity(boolean check) {
                            isCounterRunning = false;
                            startcounterTimer();
                        }
                    };
                    CommonUtils.showCallRecordings(getContext(), number, playListener);

                } else {
                    Toast.makeText(getContext(), "No Recording found as this was RNR customer.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startcounterTimer() {
        //Long currentTime = System.currentTimeMillis();
        SmarterSMBApplication.isC2CAutoStart = true;
        SmarterSMBApplication.currentAppState = "Started";
        Log.d("Autodial", "startcounterTimer()-SmarterSMBApplication.isC2CAutoStart" + SmarterSMBApplication.isC2CAutoStart);
        if (!isCounterRunning) {
            if (number != null && !(number.isEmpty())) {
                final String timebefore = ApplicationSettings.getPref(AppConstants.TIMER_BEFORE_CALL,"");
                if(timebefore != null && !(timebefore.isEmpty())) {
                    Long counter = 0L;
                    if(firstcallCheck) {
                        counter = 4000L;
                        counterText.setText("3");
                    } else {
                        int timer = Integer.parseInt(timebefore);
                        counter = (long) timer * 1000;
                        counter += 1000;
                        int sec = (int) Math.ceil( (double) counter / 1000);
                        counterText.setText("" + sec);
                    }

                    if(counter != 0) {
                        countDownTimer = new CountDownTimer(counter,1000) {

                            public void onTick(long millis) {
                                isCounterRunning = true;
                                int sec = (int) ( millis / 1000);
                                counterText.setText("" + sec);
                            }

                            public void onFinish() {
                                messageText.setText("Please wait");
                                isCounterRunning = false;
                                makeAutoDailer();
                            }
                        }.start();
                    } else {
                        if(getContext() != null) {
                            Toast.makeText(getContext(), "Counter set to 0 secs, please change the settings", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if(getContext() != null) {
                        Toast.makeText(getContext(), "please set the counter time in settings", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if(getContext() != null) {
                    Toast.makeText(getContext(), "Customer number is not available.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startcounterTimerSequential() {
        if (!isCounterRunning) {
            if (number != null && !(number.isEmpty())) {
                final String timebefore = ApplicationSettings.getPref(AppConstants.TIMER_BEFORE_CALL,"");
                if(timebefore != null && !(timebefore.isEmpty())) {
                    Long counter = 0L;
                    if(firstcallCheck) {
                        counter = 4000L;
                        counterText.setText("3");
                    } else {
                        int timer = Integer.parseInt(timebefore);
                        counter = (long) timer * 1000;
                        counter += 1000;
                        int sec = (int) Math.ceil( (double) counter / 1000);
                        counterText.setText("" + sec);
                    }

                    if(counter != 0) {
                        countDownTimer = new CountDownTimer(counter,1000) {

                            public void onTick(long millis) {
                                isCounterRunning = true;
                                int sec = (int) ( millis / 1000);
                                counterText.setText("" + sec);
                            }

                            public void onFinish() {
                                messageText.setText("Please wait");
                                isCounterRunning = false;
                                makeAutoDailerSequential();
                            }
                        }.start();
                    } else {
                        if(getContext() != null) {
                            Toast.makeText(getContext(), "Counter set to 0 secs, please change the settings", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if(getContext() != null) {
                        Toast.makeText(getContext(), "please set the counter time in settings", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if(getContext() != null) {
                    Toast.makeText(getContext(), "Customer number is not available.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isCounterRunning = false;
        }
        super.onPause();
    }

    private boolean checkDoNotCall() {
        return statusFronDb != null && statusFronDb.equalsIgnoreCase("NOT INTERESTED") && substatus1 != null && substatus1.equalsIgnoreCase("DO NOT CALL");
    }

    private boolean checkPermission() {
        return getActivity() == null || context == null || ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void makeAutoDailer() {
        if(checkDoNotCall()) {
            markFollowupAsCompleated(0, itemId);
            createCmail(number, 0);
            if(context != null) {
                Intent intent = new Intent(context, ReuploadService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                } else {
                    context.startService(intent);
                }
                Toast.makeText(context, "Status is Not Interested DO NOT CALL. ", Toast.LENGTH_LONG).show();
            }
            messageText.setText("");
            if(!UearnActivity.onBackPressed) {
                createfollowupAutoDailerView();
                followupsCount();
                return;
            }
        }
        boolean secondSim = false, firstsim = false;
        if(checkPermission()) {
/*
            if (getContext() != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(getContext());
                List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                    String number = subscriptionInfo.getNumber();
                    int SimStotNumber = subscriptionInfo.getSimSlotIndex();
                    if (SimStotNumber == 1) {
                        secondSim = true;
                    }
                    if (SimStotNumber == 0) {
                        firstsim = true;
                    }
                    //Log.d("Test", " Number is  " + number);
                }
            } else {
                secondSim = true;
            }
*/
          /*  boolean mobileOnSimData = false;crea
            if (ApplicationSettings.containsPref(AppConstants.MOBILE_DATA_ON_SIM)) {
                mobileOnSimData = ApplicationSettings.getPref(AppConstants.MOBILE_DATA_ON_SIM, false);
            }*/

            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                /*if ((!secondSim && !(mobileOnSimData)) || (secondSim && mobileOnSimData)) {*/
                if (number != null) {
                    String userNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                    if (userNumber != null && !(userNumber.isEmpty())) {
                        final String customernumber = number;
                        String sr_number = ApplicationSettings.getPref(AppConstants.SR_NUMBER, "");
                        String caller_id = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                        if (caller_id != null && !(caller_id.isEmpty())) {
                            if (sr_number != null && !(sr_number.isEmpty())) {
                                KnowlarityModel knowlarityModel = new KnowlarityModel(sr_number, userNumber, number);
                                knowlarityModel.setClient_id(caller_id);
                                if (getContext() != null && CommonUtils.isC2cNetworkAvailable(getContext())) {
                                    try {
                                        setCallData();
                                        NotificationData.knolarity_number = customernumber;
                                        ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                                        if (nameFromdb != null) {
                                            NotificationData.knolarity_name = nameFromdb;
                                        }
                                        NotificationData.knolarity_start_time = new Date().toString();
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("Knowlarity", "Make call request time " +  new Date().toString());
                                        /*new APIProvider.KnowlarityClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {*/
                                    new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
                                        @Override
                                        public void onComplete(String data, long request_code, int failure_code) {
                                            Log.d("Knowlarity", "Make call response time " + new Date().toString());
                                            NotificationData.knolarity_response_time = new Date().toString();
                                            if (data != null && !(data.isEmpty())) {
                                                if (data.contains("_SMBALERT_")) {
                                                    CommonUtils.setToast(context,data.replace("_SMBALERT_", ""));
                                                } else if (data.contains("_SMBACP_")) {
                                                    String uuidValue = data.replace("_SMBACP_", "");
                                                    NotificationData.transactionId = uuidValue;
                                                    NotificationData.uuid = uuidValue;
                                                    NotificationData.dialledCustomerNumber = customernumber;
                                                    CommonUtils.storeUuidHash(customernumber,uuidValue);
                                                    CommonUtils.storeAutoDialerHash(customernumber);
                                                    messageText.setText("Connecting to the customer. Please wait ...");
                                                    NotificationData.knolarity_response ="success : ";
                                                    CommonUtils.showACPScreen(context);
                                                } else {
                                                    NotificationData.transactionId = data;
                                                    NotificationData.uuid = data;
                                                    NotificationData.dialledCustomerNumber = customernumber;
                                                    CommonUtils.storeUuidHash(customernumber,data);
                                                    CommonUtils.storeAutoDialerHash(customernumber);
                                                    messageText.setText("Connecting to the customer. Please wait ...");
                                                    NotificationData.knolarity_response ="success : ";
                                                }

                                            } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                                                if (context != null) {
                                                    Toast.makeText(context, "Invalid Auth key", Toast.LENGTH_LONG).show();
                                                }
                                            } else if (failure_code == APIProvider.INVALID_REQUEST) {
                                                if (context != null) {
                                                    Toast.makeText(context, "Invalid request", Toast.LENGTH_LONG).show();
                                                }
                                            } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                                                if (context != null) {
                                                    Toast.makeText(context, "Invalid Parameters", Toast.LENGTH_LONG).show();
                                                }
                                            } else if (failure_code == APIProvider.INVALID_NUMBER) {
                                                updateSalesStage(number, 1);
                                                markFollowupAsCompleated(1, itemId);
//                                                createCmail(number, 1);
                                                if (context != null) {
                                                    Intent intent = new Intent(context, ReuploadService.class);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        context.startForegroundService(intent);
                                                    } else {
                                                        context.startService(intent);
                                                    }
                                                    Toast.makeText(context, "This number is not valid", Toast.LENGTH_LONG).show();
                                                }
                                                messageText.setText("This number is not valid");
                                                NotificationData.knolarity_response ="Invalid Numbers : ";
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerView();
                                                followupsCount();
                                            } else if (failure_code == APIProvider.SERVER_ALERT) {
                                                Toast.makeText(context, APIProvider.SERVER_ALERT_MESSAGE, Toast.LENGTH_LONG).show();
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerView();
                                                followupsCount();
                                            } else if (failure_code == APIProvider.REPEAT_CALLS) {
                                                markRepeatCallAsCompleted(itemId);
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerView();
                                                followupsCount();
                                            } else if (failure_code == APIProvider.DND) {
                                                markFollowupAsCompleated(0, itemId);
                                                createCmail(number, 0);
                                                if (context != null) {
                                                    Intent intent = new Intent(context, ReuploadService.class);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        context.startForegroundService(intent);
                                                    } else {
                                                        context.startService(intent);
                                                    }
                                                    Toast.makeText(context, "This number is DND.", Toast.LENGTH_LONG).show();
                                                }
                                                //Log.d("KnowlarityCall", "Make call Response " + ": DND CALL");
                                                messageText.setText("This number is DND.");
                                                NotificationData.knolarity_response ="success : ";
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerView();
                                                followupsCount();
                                            } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                                                messageText.setText("Hi. Your mobile number in Not verified.");
                                                NotificationData.knolarity_response ="Agent not verified ";
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerView();
                                                followupsCount();
                                                //Log.d("KnowlarityCall", "Make call Response " + ": AGENT NOT VERIFIED");
                                                if (context != null) {
                                                    Toast.makeText(context, "Hi. Your mobile number in Not verified.", Toast.LENGTH_LONG).show();
                                                }
                                            } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                                                messageText.setText("Hey! Ask your admin to call Support at 9113907215.");
                                                //Log.d("KnowlarityCall", "Make call Response " + ": NO SR NUMBER");
                                                NotificationData.knolarity_response ="No sr number : ";
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerView();
                                                followupsCount();
                                                if (context != null) {
                                                    Toast.makeText(context, "Hey! Ask your admin to call Support at 9113907215.", Toast.LENGTH_LONG).show();
                                                }
                                            }  else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                                                messageText.setText("Number not registered for outbound services for this agent");
                                                NotificationData.knolarity_response ="Agent not registered :  ";
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerView();
                                                followupsCount();
                                                if (context != null) {
                                                    Toast.makeText(context, "Number not registered for outbound services for this agent", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                NotificationData.knolarity_response ="Please check your internet connection. ";
                                                messageText.setText("Please check your internet connection.");
                                                //Log.d("KnowlarityCall", "Make call Response " + ": REQUEST FAILED");
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerView();
                                                followupsCount();
                                                if (context != null) {
                                                    Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                        /*}).call();*/
                                    }).reClickToCall(knowlarityModel);
                                    messageText.setText("Dialling");

                                } else {
                                    messageText.setText("You have no Internet connection.");
                                    if (context != null) {
                                        Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                messageText.setText("No SR Number.");
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                createfollowupAutoDailerView();
                                followupsCount();
                                callToCustomer();
                                if (context != null) {
                                    Toast.makeText(context, "No SR Number", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            messageText.setText("No Client ID.");
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            createfollowupAutoDailerView();
                            followupsCount();
                            if (context != null) {
                                Toast.makeText(context, "No Client ID", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        createfollowupAutoDailerView();
                        followupsCount();
                        messageText.setText("Your device number seems invalid.");
                        if (context != null) {
                            Toast.makeText(context, "Your device number seems invalid.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    createfollowupAutoDailerView();
                    followupsCount();
                    messageText.setText("This number is not valid");
                    if (context != null) {
                        Toast.makeText(context, "This number is not valid", Toast.LENGTH_SHORT).show();
                    }
                }
               /* } else {
                    callToCustomer();
                }*/
            } else {
                 callToCustomer();
            }
        } else if(getActivity() != null) {
            CommonUtils.permissionsCheck(getActivity());
        } else if(context != null) {
            Toast.makeText(context, "please enable System permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeAutoDailerSequential() {
        if(checkDoNotCall()) {
            markFollowupAsCompleated(0, itemId);
            createCmail(number, 0);
            if(context != null) {
                Intent intent = new Intent(context, ReuploadService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                } else {
                    context.startService(intent);
                }
                Toast.makeText(context, "Status is Not Interested DO NOT CALL. ", Toast.LENGTH_LONG).show();
            }
            messageText.setText("");
            if(!UearnActivity.onBackPressed) {
                createfollowupAutoDailerViewSequential();
                followupsCount();
                return;
            }
        }
        if(checkPermission()) {
            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                if (number != null) {
                    String userNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                    if (userNumber != null && !(userNumber.isEmpty())) {
                        final String customernumber = number;
                        String sr_number = ApplicationSettings.getPref(AppConstants.SR_NUMBER, "");
                        String caller_id = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                        if (caller_id != null && !(caller_id.isEmpty())) {
                            if (sr_number != null && !(sr_number.isEmpty())) {
                                KnowlarityModel knowlarityModel = new KnowlarityModel(sr_number, userNumber, number);
                                knowlarityModel.setClient_id(caller_id);
                                if (getContext() != null && CommonUtils.isC2cNetworkAvailable(getContext())) {
                                    try {
                                        NotificationData.knolarity_number = customernumber;
                                        ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                                        if (nameFromdb != null) {
                                            NotificationData.knolarity_name = nameFromdb;
                                        }
                                        NotificationData.knolarity_start_time = new Date().toString();
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                    new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
                                        @Override
                                        public void onComplete(String data, long request_code, int failure_code) {
                                            Log.d("Knowlarity", "Make call response time " +  new Date().toString());
                                            //Log.d("KnowlarityCall", "Make call Response time " + new Date().toString());
                                            NotificationData.knolarity_response_time = new Date().toString();
                                            String customerListData = new Gson().toJson(tempcustomerList);
                                            ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData);

                                            if (data != null && !(data.isEmpty())) {
                                                if (data.contains("_SMBALERT_")) {
                                                    CommonUtils.setToast(context,data.replace("_SMBALERT_", ""));
                                                } else if (data.contains("_SMBACP_")) {
                                                    String uuidValue = data.replace("_SMBACP_", "");
                                                    handleDNDAndInvalidSuccessScenario();
                                                    handleRepeatedCallsSuccessScenario();
                                                    NotificationData.transactionId = uuidValue;
                                                    NotificationData.uuid = uuidValue;
                                                    CommonUtils.storeUuidHash(customernumber, uuidValue);
                                                    CommonUtils.storeAutoDialerHash(customernumber);
                                                    messageText.setText("Connecting: Answer incoming call");
                                                    NotificationData.knolarity_response = "success : ";
                                                    CommonUtils.showACPScreen(context);
                                                } else {
                                                    handleDNDAndInvalidSuccessScenario();
                                                    handleRepeatedCallsSuccessScenario();
                                                    NotificationData.transactionId = data;
                                                    NotificationData.uuid = data;
                                                    CommonUtils.storeUuidHash(customernumber, data);
                                                    CommonUtils.storeAutoDialerHash(customernumber);
                                                    messageText.setText("Connecting: Answer incoming call");
                                                    NotificationData.knolarity_response = "success : ";
                                                }
                                            } else if (failure_code == APIProvider.SERVER_ALERT) {
                                                CommonUtils.setToast(context, APIProvider.SERVER_ALERT_MESSAGE);
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerViewSequential();
                                                followupsCount();
                                            } else if (failure_code == APIProvider.REPEAT_CALLS) {
                                                handleRepeatedCallsScenario();
                                            } else if (failure_code == APIProvider.DND) {
                                                handleDNDScenario();
                                            } else if (failure_code == APIProvider.INVALID_NUMBER) {
                                                handleInvalidScenario();
                                            } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                                                if (context != null) {
                                                    Toast.makeText(context, "Invalid Auth key", Toast.LENGTH_LONG).show();
                                                }
                                            } else if (failure_code == APIProvider.INVALID_REQUEST) {
                                                if (context != null) {
                                                    Toast.makeText(context, "Invalid request", Toast.LENGTH_LONG).show();
                                                }
                                            } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                                                if (context != null) {
                                                    Toast.makeText(context, "Invalid Parameters", Toast.LENGTH_LONG).show();
                                                }
                                            } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                                                messageText.setText("Hi. Your mobile number in Not verified.");
                                                NotificationData.knolarity_response ="Agent not verified ";
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerViewSequential();
                                                followupsCount();
                                                //Log.d("KnowlarityCall", "Make call Response " + ": AGENT NOT VERIFIED");
                                                if (context != null) {
                                                    Toast.makeText(context, "Hi. Your mobile number in Not verified.", Toast.LENGTH_LONG).show();
                                                }
                                            } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                                                messageText.setText("Hey! Ask your admin to call Support at 9113907215.");
                                                //Log.d("KnowlarityCall", "Make call Response " + ": NO SR NUMBER");
                                                NotificationData.knolarity_response ="No sr number : ";
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerViewSequential();
                                                followupsCount();
                                                if (context != null) {
                                                    Toast.makeText(context, "Hey! Ask your admin to call Support at 9113907215.", Toast.LENGTH_LONG).show();
                                                }
                                            } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                                                messageText.setText("Number not registered for outbound services for this agent");
                                                NotificationData.knolarity_response ="Agent not registered :  ";
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                createfollowupAutoDailerViewSequential();
                                                followupsCount();
                                                if (context != null) {
                                                    Toast.makeText(context, "Number not registered for outbound services for this agent", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                if (CommonUtils.isC2cNetworkAvailable(getActivity())) {
                                                    NotificationData.knolarity_response = "Please check your internet connection. ";
                                                    messageText.setText("Please check your internet connection.");
                                                    //Log.d("KnowlarityCall", "Make call Response " + ": REQUEST FAILED");
                                                    try {
                                                        Thread.sleep(4000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    createfollowupAutoDailerViewSequential();
                                                    followupsCount();
                                                    if (context != null) {
                                                        Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                        }
                                        /*}).call();*/
                                    }).reClickToCall(knowlarityModel);
                                    messageText.setText("Dialling");

                                } else {
                                    messageText.setText("You have no Internet connection.");
                                    if (context != null) {
                                        Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                messageText.setText("No SR Number.");
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                createfollowupAutoDailerViewSequential();
                                followupsCount();
                                callToCustomer();
                                if (context != null) {
                                    Toast.makeText(context, "No SR Number", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            messageText.setText("No Client ID.");
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            createfollowupAutoDailerViewSequential();
                            followupsCount();
                            if (context != null) {
                                Toast.makeText(context, "No Client ID", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        createfollowupAutoDailerViewSequential();
                        followupsCount();
                        messageText.setText("Your device number seems invalid.");
                        if (context != null) {
                            Toast.makeText(context, "Your device number seems invalid.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    createfollowupAutoDailerViewSequential();
                    followupsCount();
                    messageText.setText("This number is not valid");
                    if (context != null) {
                        Toast.makeText(context, "This number is not valid", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                callToCustomer();
            }
        } else if(getActivity() != null) {
            CommonUtils.permissionsCheck(getActivity());
        } else if(context != null) {
            Toast.makeText(context, "please enable System permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleInvalidScenario() {

        String listOfInvalidNumbers = ApplicationSettings.getPref(AppConstants.LIST_OF_INVALID_NUMBERS, "");
        String[] listOfInvalidNumbersArray = null;
        if(listOfInvalidNumbers != null && !listOfInvalidNumbers.isEmpty() && listOfInvalidNumbers.startsWith("[") && listOfInvalidNumbers.endsWith("]")) {
            listOfInvalidNumbers = listOfInvalidNumbers.substring(1, listOfInvalidNumbers.length()-1);
            listOfInvalidNumbersArray = listOfInvalidNumbers.split(",");
            if(listOfInvalidNumbersArray != null && listOfInvalidNumbersArray.length > 0) {
                for(int i=0; i<listOfInvalidNumbersArray.length; i++) {
                    if(listOfInvalidNumbersArray[i].contains("\"")) {
                        listOfInvalidNumbersArray[i] = listOfInvalidNumbersArray[i].replaceAll("\"","");
                    }
                }
            }
        }

        if(listOfInvalidNumbersArray != null && listOfInvalidNumbersArray.length > 0) {
            if(listOfInvalidNumbersArray.length == 1) {
                String InvalidNumber = listOfInvalidNumbersArray[0];
                Toast.makeText(context, InvalidNumber + " is INVALID", Toast.LENGTH_LONG).show();
                for (int j = 0; j < tempcustomerList.size(); j++) {
                    CustomerLite customerLite = tempcustomerList.get(j);
                    if (customerLite.customerNumber.contains(listOfInvalidNumbersArray[0])) {
                        customerLite.type = "INVALID";
                        updateSalesStage(customerLite.customerNumber, 1);
                        markFollowupAsCompleated(0, customerLite.id);
//                        createCmail(customerLite.customerNumber, 1);
                        if (context != null) {
                            Intent intent = new Intent(context, ReuploadService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(intent);
                            } else {
                                context.startService(intent);
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ApplicationSettings.putPref(AppConstants.LIST_OF_INVALID_NUMBERS, "");
                createfollowupAutoDailerViewSequential();
                followupsCount();
            } else {

                if (listOfInvalidNumbersArray != null && listOfInvalidNumbersArray.length > 0 && tempcustomerList != null && tempcustomerList.size() > 0) {
                    if (listOfInvalidNumbersArray.length == tempcustomerList.size()) {
                        Toast.makeText(context, "All " + listOfInvalidNumbersArray.length + " numbers are INVALID", Toast.LENGTH_LONG).show();
                    }
                }
                for (int j = 0; j < tempcustomerList.size(); j++) {
                    CustomerLite customerLite = tempcustomerList.get(j);
                    customerLite.type = "INVALID";
                    updateSalesStage(customerLite.customerNumber, 1);
                    markFollowupAsCompleated(1, customerLite.id);
//                    createCmail(customerLite.customerNumber, 1);
                }

                if (context != null) {
                    Intent intent = new Intent(context, ReuploadService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent);
                    } else {
                        context.startService(intent);
                    }
                }

                ApplicationSettings.putPref(AppConstants.LIST_OF_INVALID_NUMBERS, "");
                createfollowupAutoDailerViewSequential();
                followupsCount();
            }
        }
    }

    private void handleDNDScenario() {

        String listOfDNDNumbers = ApplicationSettings.getPref(AppConstants.LIST_OF_DND_NUMBERS, "");
        String[] listOfDNDNumbersArray = null;
        if(listOfDNDNumbers != null && !listOfDNDNumbers.isEmpty() && listOfDNDNumbers.startsWith("[") && listOfDNDNumbers.endsWith("]")) {
            listOfDNDNumbers = listOfDNDNumbers.substring(1, listOfDNDNumbers.length()-1);
            listOfDNDNumbersArray = listOfDNDNumbers.split(",");
            if(listOfDNDNumbersArray != null && listOfDNDNumbersArray.length > 0) {
                for(int i=0; i<listOfDNDNumbersArray.length; i++) {
                    if(!listOfDNDNumbersArray[i].startsWith("+")) {
                        listOfDNDNumbersArray[i] = "+91" + listOfDNDNumbersArray[i];
                    }
                    if(listOfDNDNumbersArray[i].contains("\"")) {
                        listOfDNDNumbersArray[i] = listOfDNDNumbersArray[i].replaceAll("\"","");
                    }
                }
            }
        }

        if(listOfDNDNumbersArray != null && listOfDNDNumbersArray.length > 0) {
            if(listOfDNDNumbersArray.length == 1) {
                String DNDNumber = listOfDNDNumbersArray[0];
                Toast.makeText(context, DNDNumber + " is DND", Toast.LENGTH_LONG).show();
                for (int j = 0; j < tempcustomerList.size(); j++) {
                    CustomerLite customerLite = tempcustomerList.get(j);
                    if (customerLite.customerNumber.contains(listOfDNDNumbersArray[0])) {
                        customerLite.type = "DND";
                        updateSalesStage(customerLite.customerNumber, 0);
                        markFollowupAsCompleated(0, customerLite.id);
                        createCmail(customerLite.customerNumber, 0);
                        if (context != null) {
                            Intent intent = new Intent(context, ReuploadService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(intent);
                            } else {
                                context.startService(intent);
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ApplicationSettings.putPref(AppConstants.LIST_OF_DND_NUMBERS, "");
                createfollowupAutoDailerViewSequential();
                followupsCount();
            } else {

                if (listOfDNDNumbersArray != null && listOfDNDNumbersArray.length > 0 && tempcustomerList != null && tempcustomerList.size() > 0) {
                    if (listOfDNDNumbersArray.length == tempcustomerList.size()) {
                        Toast.makeText(context, "All " + listOfDNDNumbersArray.length + " numbers are DND", Toast.LENGTH_LONG).show();
                    }
                }
                for (int j = 0; j < tempcustomerList.size(); j++) {
                    CustomerLite customerLite = tempcustomerList.get(j);
                    customerLite.type = "DND";
                    updateSalesStage(customerLite.customerNumber, 0);
                    markFollowupAsCompleated(0, customerLite.id);
                    createCmail(customerLite.customerNumber, 0);
                }

                if (context != null) {
                    Intent intent = new Intent(context, ReuploadService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent);
                    } else {
                        context.startService(intent);
                    }
                }

                ApplicationSettings.putPref(AppConstants.LIST_OF_DND_NUMBERS, "");
                createfollowupAutoDailerViewSequential();
                followupsCount();
            }
        }
    }

    private void handleRepeatedCallsScenario() {

        String listOfRepeatedNumbers = ApplicationSettings.getPref(AppConstants.LIST_OF_REPEATED_NUMBERS, "");
        String[] listOfRepeatedNumbersArray = null;
        if (listOfRepeatedNumbers != null && !listOfRepeatedNumbers.isEmpty() && listOfRepeatedNumbers.startsWith("[") && listOfRepeatedNumbers.endsWith("]")) {
            listOfRepeatedNumbers = listOfRepeatedNumbers.substring(1, listOfRepeatedNumbers.length() - 1);
            listOfRepeatedNumbersArray = listOfRepeatedNumbers.split(",");
            if (listOfRepeatedNumbersArray != null && listOfRepeatedNumbersArray.length > 0) {
                for (int i = 0; i < listOfRepeatedNumbersArray.length; i++) {
                    if (listOfRepeatedNumbersArray[i].contains("\"")) {
                        listOfRepeatedNumbersArray[i] = listOfRepeatedNumbersArray[i].replaceAll("\"", "");
                    }
                }
            }
        }

        if (listOfRepeatedNumbersArray != null && listOfRepeatedNumbersArray.length > 0) {
            for (int i = 0; i < listOfRepeatedNumbersArray.length; i++) {
                int itemId = getItemId(listOfRepeatedNumbersArray[i]);
                markRepeatCallAsCompleted(itemId);
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApplicationSettings.putPref(AppConstants.LIST_OF_REPEATED_NUMBERS, "");
        createfollowupAutoDailerViewSequential();
        followupsCount();
    }

    private void handleDNDAndInvalidSuccessScenario() {

        String listOfDNDNumbers = ApplicationSettings.getPref(AppConstants.LIST_OF_DND_NUMBERS, "");
        String[] listOfDNDNumbersArray = null;
        if (listOfDNDNumbers != null && !listOfDNDNumbers.isEmpty() && listOfDNDNumbers.startsWith("[") && listOfDNDNumbers.endsWith("]")) {
            listOfDNDNumbers = listOfDNDNumbers.substring(1, listOfDNDNumbers.length() - 1);
            listOfDNDNumbersArray = listOfDNDNumbers.split(",");
            if (listOfDNDNumbersArray != null && listOfDNDNumbersArray.length > 0) {
                for (int i = 0; i < listOfDNDNumbersArray.length; i++) {
                    if(!listOfDNDNumbersArray[i].startsWith("+")) {
                        listOfDNDNumbersArray[i] = "+91" + listOfDNDNumbersArray[i];
                    }
                    if (listOfDNDNumbersArray[i].contains("\"")) {
                        listOfDNDNumbersArray[i] = listOfDNDNumbersArray[i].replaceAll("\"", "");
                    }
                }
            }
        }

        if (listOfDNDNumbersArray != null && listOfDNDNumbersArray.length > 0) {
            for (int i = 0; i < listOfDNDNumbersArray.length; i++) {
                for (int j = 0; j < tempcustomerList.size(); j++) {
                    CustomerLite customerLite = tempcustomerList.get(j);
                    if (customerLite.customerNumber.contains(listOfDNDNumbersArray[i])) {
                        customerLite.type = "DND";
                        updateSalesStage(customerLite.customerNumber, 0);
                        markFollowupAsCompleated(0, customerLite.id);
                        createCmail(customerLite.customerNumber, 0);
                        if (context != null) {
                            Intent intent = new Intent(context, ReuploadService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(intent);
                            } else {
                                context.startService(intent);
                            }
                        }
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        followupsCount();
                    }
                }
            }
        }

        String listOfInvalidNumbers = ApplicationSettings.getPref(AppConstants.LIST_OF_INVALID_NUMBERS, "");
        String[] listOfInvalidNumbersArray = null;
        if (listOfInvalidNumbers != null && !listOfInvalidNumbers.isEmpty() && listOfInvalidNumbers.startsWith("[") && listOfInvalidNumbers.endsWith("]")) {
            listOfInvalidNumbers = listOfInvalidNumbers.substring(1, listOfInvalidNumbers.length() - 1);
            listOfInvalidNumbersArray = listOfInvalidNumbers.split(",");
            if (listOfInvalidNumbersArray != null && listOfInvalidNumbersArray.length > 0) {
                for (int i = 0; i < listOfInvalidNumbersArray.length; i++) {
                    if (listOfInvalidNumbersArray[i].contains("\"")) {
                        listOfInvalidNumbersArray[i] = listOfInvalidNumbersArray[i].replaceAll("\"", "");
                    }
                }
            }
        }

        if (listOfInvalidNumbersArray != null && listOfInvalidNumbersArray.length > 0) {
            for (int i = 0; i < listOfInvalidNumbersArray.length; i++) {
                for (int j = 0; j < tempcustomerList.size(); j++) {
                    CustomerLite customerLite = tempcustomerList.get(j);
                    if (customerLite.customerNumber.contains(listOfInvalidNumbersArray[i])) {
                        customerLite.type = "INVALID";
                        break;
                    }
                }
            }
        }

        if (listOfInvalidNumbersArray != null && listOfInvalidNumbersArray.length > 0) {
            for (int i = 0; i < listOfInvalidNumbersArray.length; i++) {
                updateSalesStage(listOfInvalidNumbersArray[i], 1);
                int itemId = getItemId(listOfInvalidNumbersArray[i]);
                markFollowupAsCompleated(1, itemId);
//                createCmail(listOfInvalidNumbersArray[i], 1);
                if (context != null) {
                    Intent intent = new Intent(context, ReuploadService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent);
                    } else {
                        context.startService(intent);
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            followupsCount();
        }

        String customerListData = new Gson().toJson(tempcustomerList);
        ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData);

        if(tempcustomerList != null && tempcustomerList.size() > 0) {
            List<CustomerLite> tempcustomerListExcludingInvalid = new ArrayList<>(tempcustomerList);
            for(int i=0; i<tempcustomerListExcludingInvalid.size(); i++) {
                CustomerLite customerLite = tempcustomerListExcludingInvalid.get(i);
                if(customerLite.type != null && !customerLite.type.isEmpty() && customerLite.type.equalsIgnoreCase("INVALID")){
                    tempcustomerListExcludingInvalid.remove(i);
                }
        }
            String customerListDataExcludingInvalid = new Gson().toJson(tempcustomerListExcludingInvalid);
            ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA_EXCLUDING_INVALID, customerListDataExcludingInvalid);
        }
    }

    private void handleRepeatedCallsSuccessScenario() {

        String listOfRepeatedNumbers = ApplicationSettings.getPref(AppConstants.LIST_OF_REPEATED_NUMBERS, "");
        String[] listOfRepeatedNumbersArray = null;
        if (listOfRepeatedNumbers != null && !listOfRepeatedNumbers.isEmpty() && listOfRepeatedNumbers.startsWith("[") && listOfRepeatedNumbers.endsWith("]")) {
            listOfRepeatedNumbers = listOfRepeatedNumbers.substring(1, listOfRepeatedNumbers.length() - 1);
            listOfRepeatedNumbersArray = listOfRepeatedNumbers.split(",");
            if (listOfRepeatedNumbersArray != null && listOfRepeatedNumbersArray.length > 0) {
                for (int i = 0; i < listOfRepeatedNumbersArray.length; i++) {
                    if (listOfRepeatedNumbersArray[i].contains("\"")) {
                        listOfRepeatedNumbersArray[i] = listOfRepeatedNumbersArray[i].replaceAll("\"", "");
                    }
                }
            }
        }

        if (listOfRepeatedNumbersArray != null && listOfRepeatedNumbersArray.length > 0) {
            for (int i = 0; i < listOfRepeatedNumbersArray.length; i++) {
                int itemId = getItemId(listOfRepeatedNumbersArray[i]);
                markRepeatCallAsCompleted(itemId);
            }
        }

        ApplicationSettings.putPref(AppConstants.LIST_OF_REPEATED_NUMBERS, "");
        followupsCount();
    }

    private int getItemId(String customerNumber) {
        if(tempcustomerList != null && tempcustomerList.size() > 0) {
            for (int k = 0; k < tempcustomerList.size(); k++) {
                CustomerLite customerLite = tempcustomerList.get(k);
                if(customerLite.customerNumber.equals(customerNumber)) {
                    return customerLite.id;
                }
            }
        }
        return 0;
    }

    private void updateSalesStage(String customerNumber, int flp_status) {

        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        getCalendarEntryInfo.caller_number = customerNumber;

        if(flp_status == AppConstants.INVALID){
            getCalendarEntryInfo.status = "OTHERS";
            getCalendarEntryInfo.subStatus1 = "INVALID";
        } else {
            getCalendarEntryInfo.status = "NEW DATA";
        }

        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        getCalendarEntryInfo.user_id = userId;

        String leadSource = ApplicationSettings.getPref(AFTER_CALL_LEAD, "");
        if(leadSource == null || leadSource.isEmpty()) {
            leadSource = getLeadSource(customerNumber);
        }
        getCalendarEntryInfo.lead_source = leadSource;

        String customerToContact = ApplicationSettings.getPref(CUSTOMER_TO_CONTACT, "");
        if(customerToContact == null || customerToContact.isEmpty()) {
            customerToContact = getCustomerToContact(customerNumber);
        }
        getCalendarEntryInfo.customer_id = customerToContact;

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            new APIProvider.Update_SalesStatus(getCalendarEntryInfo, 0, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        //Log.d("AutodillerFragment", "Make call Response " + data);
                    }
                }
            }).call();
        }
    }

    private String getLeadSource(String number) {
        String leadSource = "";
        try {
            MySql dbHelper = MySql.getInstance(getActivity());
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

    private String getCustomerToContact(String number) {
        String customerId = "";
        try {
            MySql dbHelper = MySql.getInstance(getActivity());
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if(number != null && !number.isEmpty()) {
                String selection = "TO1=" + "'" + number + "'";
                Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    customerId = cursor.getString(cursor.getColumnIndex("CUSTOMER_ID"));
                }
                if (dbase.isOpen()) {
                    dbase.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerId;
    }

    private void callToCustomer() {
        if(context != null ) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            if (number != null) {
                String num = "tel:" + number;
                intent.setData(Uri.parse(num));
            }
            setCallData();
            if (context != null && ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(intent);
        }
    }


    private void setCallData() {
        NotificationData.appointment_db_id = itemId;
        NotificationData.notificationData = true;
        NotificationData.appointment_id = appointmentId;
        NotificationData.statusString = statusFronDb;
        if(notes != null) {
            NotificationData.notes_string = notes;
        }
        NotificationData.order_value = orderPotential;
        NotificationData.isAppointment = true;
        if (substatus1 != null) {
            NotificationData.substatus1 = substatus1;
        }
        if (substatus2 != null) {
            NotificationData.substatus2 = substatus2;
        }
    }

    private void markFollowupAsCompleated(int status, int itemId) {
        if(getContext() != null) {
            MySql dbHelper = MySql.getInstance(getActivity());
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 0);
            cv.put("COMPLETED", 1);
            cv.put("RESPONSE_STATUS", "completed");
            if(status == 1) {
                cv.put("STATUS", "OTHERS");
                cv.put("SUBSTATUS1", "INVALID");
            } else {
                cv.put("SUBJECT", "DND NUMBER");
            }
            cv.put("APPOINTMENT_TYPE", "complete_appointment");

            String start = CommonUtils.getTimeFormatInISO(new Date());
            cv.put("CREATED_AT", start);
            cv.put("RNR_COUNT", rnr_count);

            if (itemId != 0) {
                db1.update("remindertbNew", cv, "_id=" + itemId, null);
            }
            db1.close();

           /* GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
            getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
            getCalendarEntryInfo.appointment_id = appointmentId;
            getCalendarEntryInfo.responsestatus = "completed";
            getCalendarEntryInfo.update_all_fields = false;
            getCalendarEntryInfo.tat = 0;
            getCalendarEntryInfo.rnrCount = 0;

            //Log.d("UpdateMethRnrCount", "" + rnr_count);

            if (start != null && !(start.isEmpty())) {
                getCalendarEntryInfo.created_at = start;
            }

            updateAppointmentCall(getCalendarEntryInfo);*/
        }
    }

    private void updateAppointmentCall(GetCalendarEntryInfo getCalendarEntryInfo) {

        if (getContext() != null && CommonUtils.isNetworkAvailable(getContext())) {
            new APIProvider.Update_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        }
    }


    private void createCmail(String phoneno, int flp_status) {
        if(getContext() != null) {
            MySql dbHelper = MySql.getInstance(getActivity());
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            if(flp_status == AppConstants.INVALID){
                cv.put("EVENT_TYPE", "call_invalid");
                cv.put("STATUS", "INVALID");
            } else {
                cv.put("EVENT_TYPE", "call_dnd");
                if(statusFronDb != null && !statusFronDb.isEmpty())
                    cv.put("STATUS", statusFronDb);
                else
                    cv.put("STATUS", "NEW DATA");
            }
            if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                cv.put("FROM1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                cv.put("TO1", phoneno);
                cv.put("MSG_RECEPIENT_NO", phoneno);

            }
            cv.put("SUBJECT", "Outgoing Call");
            cv.put("MESSAGE", "Outgoing Call");
            cv.put("UPLOAD_STATUS", 1);
            cv.put("EMAIL", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null));
            cv.put("PARENT", 0);
            cv.put("STARTTIME", CommonUtils.getTimeFormatInISO(new Date()));
            cv.put("ENDTIME", CommonUtils.getTimeFormatInISO(new Date()));
            cv.put("UNREAD", "false");
            String callerName = CommonUtils.getContactName(getContext(), phoneno);
            cv.put("CALLER", callerName);
            db1.insert("mytbl", null, cv);
            db1.close();
        }
    }

    private void createfollowupAutoDailerViewSequential() {
        number = "";
        Date date = new Date();

        String newDataString = ApplicationSettings.getPref(AppConstants.NEW_DATA_STRING, "");
        String followUpString = ApplicationSettings.getPref(AppConstants.FOLLOW_UP_STRING, "");

        String selection = "";
        if(leadSource != null && !leadSource.isEmpty()) {
            if(followUpString != null && !followUpString.isEmpty()) {
                selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + followUpString + " AND" + " LEAD_SOURCE = " + "'" + leadSource + "'";
            } else {
                selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "FLP_COUNT >= " + 1 + " AND " + "(" + "RESPONSE_STATUS " + "=" + " 'accepted' " + " OR " + "COMPLETED" + "=" + 0 + ")" + " AND" + " LEAD_SOURCE = " + "'" + leadSource + "'";
            }
        } else {
            if (followUpString != null && !followUpString.isEmpty()) {
                selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + followUpString;
            } else {
                selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "FLP_COUNT >= " + 1 + " AND " + "(" + "RESPONSE_STATUS " + "=" + " 'accepted' " + " OR " + "COMPLETED" + "=" + 0 + ")";
            }
        }
        Cursor cursor1 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");
        Cursor cursor2 = null;

        if (cursor1 == null || cursor1.getCount() <= 0) {
            cursor1.close();
            String selection2 = "";
            if(leadSource != null && !leadSource.isEmpty()) {
                if(newDataString != null && !newDataString.isEmpty()) {
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + newDataString + " AND" + " LEAD_SOURCE = " + "'" + leadSource + "'";
                } else {
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "FLP_COUNT = " + 0 +  " AND " + "(" + "RESPONSE_STATUS " + "=" + " 'accepted' " + " OR " + "COMPLETED" + "=" + 0 + ")" + " AND" + " LEAD_SOURCE = " +  "'" + leadSource + "'";
                }
            } else {
                if (newDataString != null && !newDataString.isEmpty()) {
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + newDataString;
                } else {
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "FLP_COUNT = " + 0 +  " AND " + "(" + "RESPONSE_STATUS " + "=" + " 'accepted' " + " OR " + "COMPLETED" + "=" + 0 + ")";
                }
            }
            cursor2 = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME DESC");
        }

        String noOfCustomersToCall = ApplicationSettings.getPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, "");
        int noOfCustomersToDial = 0;

        if(noOfCustomersToCall != null && !noOfCustomersToCall.isEmpty()) {
            noOfCustomersToDial = Integer.valueOf(noOfCustomersToCall);
        }

        if (cursor1 != null && (cursor1.getCount() > 0)) {
            try {
                setAutoDailerSequential(cursor1);
                if(tempcustomerList != null && tempcustomerList.size() > 0) {
                    int customerListSize = 0;
                    if(tempcustomerList.size() <= noOfCustomersToDial) {
                        customerListSize = tempcustomerList.size();
                    }
                    for(int i=0; i<customerListSize; i++) {
                        CustomerLite customer = tempcustomerList.get(i);
                        String custNumber = customer.customerNumber;
                        number += custNumber+",";
                    }
                    number = number.substring(0, number.length() - 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (cursor2 != null && cursor2.getCount() > 0) {
            try {
                setAutoDailerSequential(cursor2);
                if(tempcustomerList != null && tempcustomerList.size() > 0) {
                    int customerListSize = 0;
                    if(tempcustomerList.size() <= noOfCustomersToDial) {
                        customerListSize = tempcustomerList.size();
                    }
                    for(int i=0; i<customerListSize; i++) {
                        CustomerLite customer = tempcustomerList.get(i);
                        String custNumber = customer.customerNumber;
                        number += custNumber+",";
                    }
                    number = number.substring(0, number.length() - 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor2.close();
            }
        } else {
            previousFollowupsSequential();
        }
        messageText.setText("Please wait");
        startcounterTimerSequential();
    }

    private void createfollowupAutoDailerView() {
        Date date = new Date();

        String newDataString = ApplicationSettings.getPref(AppConstants.NEW_DATA_STRING, "");
        String followUpString = ApplicationSettings.getPref(AppConstants.FOLLOW_UP_STRING, "");

        String selection = "";
        if(leadSource != null && !leadSource.isEmpty()) {
            if(followUpString != null && !followUpString.isEmpty()) {
                selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + followUpString + " AND" + " LEAD_SOURCE = " + "'" + leadSource + "'";
            } else {
                selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "FLP_COUNT >= " + 1 + " AND " + "(" + "RESPONSE_STATUS " + "=" + " 'accepted' " + " OR " + "COMPLETED" + "=" + 0 + ")" + " AND" + " LEAD_SOURCE = " + "'" + leadSource + "'";
            }
        } else {
            if (followUpString != null && !followUpString.isEmpty()) {
                selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + followUpString;
            } else {
                selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "FLP_COUNT >= " + 1 + " AND " + "(" + "RESPONSE_STATUS " + "=" + " 'accepted' " + " OR " + "COMPLETED" + "=" + 0 + ")";
            }
        }
        Cursor cursor1 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");
        Cursor cursor2 = null;

        if (cursor1 == null || cursor1.getCount() <= 0) {
            cursor1.close();
            String selection2 = "";
            if(leadSource != null && !leadSource.isEmpty()) {
                if(newDataString != null && !newDataString.isEmpty()) {
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + newDataString + " AND" + " LEAD_SOURCE = " + "'" + leadSource + "'";
                } else {
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "FLP_COUNT = " + 0 +  " AND " + "(" + "RESPONSE_STATUS " + "=" + " 'accepted' " + " OR " + "COMPLETED" + "=" + 0 + ")" + " AND" + " LEAD_SOURCE = " +  "'" + leadSource + "'";
                }
            } else {
                if (newDataString != null && !newDataString.isEmpty()) {
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + newDataString;
                } else {
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "FLP_COUNT = " + 0 +  " AND " + "(" + "RESPONSE_STATUS " + "=" + " 'accepted' " + " OR " + "COMPLETED" + "=" + 0 + ")";
                }
            }
            cursor2 = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME DESC");
        }

        if (cursor1 != null && (cursor1.getCount() > 0)) {
            try {
                cursor1.moveToFirst();
                setAutoDailer(cursor1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (cursor2 != null && cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            try {
                setAutoDailer(cursor2);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor2.close();
            }
        } else {
            previousFollowups();
        }
        startcounterTimer();
    }

    private void previousFollowups() {
        Date date = new Date();
        String selection = "";
        if(leadSource != null && !leadSource.isEmpty())
            selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + "<" + start + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' " + " AND" + " LEAD_SOURCE = " +  "'" + leadSource + "'";
        else
            selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + "<" + start + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        Cursor cursor3 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");

        if (cursor3 != null && (cursor3.getCount() > 0)) {
            try {
                cursor3.moveToFirst();
                setAutoDailer(cursor3);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor3.close();
            }
        } else {
            if(context != null) {
                Toast.makeText(context, "Your Auto Dialler has No Database.", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        UearnHome.getInstance().refresh();
                    }
                });
            }
            stop.performClick();
            //TODO:: CLOSING A
           /* Intent intent = new Intent(this, SmbHome.class);
            intent.putExtra("AUTOCOMPLETE", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //this.finish();*/
        }
    }

    private void previousFollowupsSequential() {
        number = "";
        Date date = new Date();
        String selection = "";
        if(leadSource != null && !leadSource.isEmpty())
            selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + "<" + start + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' " + " AND" + " LEAD_SOURCE = " +  "'" + leadSource + "'";
        else
            selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + "<" + start + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        Cursor cursor3 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");

        String noOfCustomersToCall = ApplicationSettings.getPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, "");
        int noOfCustomersToDial = 0;

        if(noOfCustomersToCall != null && !noOfCustomersToCall.isEmpty()) {
            noOfCustomersToDial = Integer.valueOf(noOfCustomersToCall);
        }

        if (cursor3 != null && (cursor3.getCount() > 0)) {
            try {
                setAutoDailerSequential(cursor3);
                if(tempcustomerList != null && tempcustomerList.size() > 0) {
                    int customerListSize = 0;
                    if(tempcustomerList.size() <= noOfCustomersToDial) {
                        customerListSize = tempcustomerList.size();
                    }
                    for(int i=0; i<customerListSize; i++) {
                        CustomerLite customer = tempcustomerList.get(i);
                        String custNumber = customer.customerNumber;
                        number += custNumber+",";
                    }
                    number = number.substring(0, number.length() - 1);
                }
            }  catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor3.close();
            }
        } else {
            if(context != null) {
                Toast.makeText(context, "Your Auto Dialler has No Database.", Toast.LENGTH_SHORT).show();
            }
            stop.performClick();
        }
    }

    private void setAutoDailer(Cursor cursor2) {
        itemId = cursor2.getInt(cursor2.getColumnIndex("_id"));
        nameFromdb = cursor2.getString(cursor2.getColumnIndex("TONAME"));
        number = cursor2.getString(cursor2.getColumnIndex("TO1"));

        if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
            NotificationData.dialledCustomerNumber = number; // Temporary enabled for Black Buck
        } else {
            NotificationData.dialledCustomerNumber = number;
        }

        String useCampaign = ApplicationSettings.getPref(AppConstants.USE_CAMPAIGN, "");
        if (useCampaign != null && !useCampaign.isEmpty() && !useCampaign.equals("null")) {
            leadSource = useCampaign;
        } else {
            leadSource = cursor2.getString(cursor2.getColumnIndex("LEAD_SOURCE"));
        }

        statusFronDb = cursor2.getString(cursor2.getColumnIndex("STATUS"));
        substatus1 = cursor2.getString(cursor2.getColumnIndex("SUBSTATUS1"));
        substatus2 = cursor2.getString(cursor2.getColumnIndex("SUBSTATUS2"));
        appointmentId = cursor2.getString(cursor2.getColumnIndex("APPOINTMENT_ID"));
        orderPotential = cursor2.getString(cursor2.getColumnIndex("ORDER_POTENTIAL"));
        long startTime = cursor2.getLong(cursor2.getColumnIndex("START_TIME"));
        rnr_count = cursor2.getInt(cursor2.getColumnIndex("RNR_COUNT"));
        String flpvalue = cursor2.getString(cursor2.getColumnIndex("FLP_COUNT"));
        notes = cursor2.getString(cursor2.getColumnIndex("NOTES"));

        if(notes != null && !notes.equalsIgnoreCase("null")) {
            notesTv.setText(notes);
        }
        if (nameFromdb != null && !(nameFromdb.isEmpty())) {
            name.setText(nameFromdb);
        } else {
            name.setText("No Name");
        }


        if(leadSource != null && !leadSource.isEmpty()) {
            campaignName.setText(leadSource);
            campaignName.setVisibility(View.VISIBLE);
        } else {
            campaignName.setText("");
            campaignName.setVisibility(View.GONE);
        }

        String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
        if (questionsAct != null && !questionsAct.isEmpty() && ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE")))) {
            campaignName.setText("");
            campaignName.setVisibility(View.VISIBLE);
        }

        stage.setText(statusFronDb);
        stage1.setText(substatus1);
        stage2.setText(substatus2);

        if (questionsAct != null && !questionsAct.isEmpty() && (questionsAct.equals("GFIT"))){
            custNumber.setText(number);
            custNumber.setVisibility(View.VISIBLE);
        }

        if(company.equals("OYO")) {
            campaignName.setVisibility(View.GONE);
            custNumber.setText(number);
            custNumber.setVisibility(View.VISIBLE);
        }

        if (questionsAct != null && !questionsAct.isEmpty() && (questionsAct.equals("MMT"))) {
            campaignName.setVisibility(View.GONE);
            custNumber.setVisibility(View.VISIBLE);
            String emailId = cursor2.getString(cursor2.getColumnIndex("EMAILID"));
            if (emailId != null && !emailId.isEmpty()) {
                custNumber.setText(emailId);
            }
        }

        if(flpvalue != null) {
            if(flpvalue.equalsIgnoreCase("0")) {
                firstcallCheck = true;
            }
        }

        String timeString = DateFormat.format("hh:mm a", startTime).toString();
        Date showDate = new Date(startTime);
        String showDateTime = DateFormat.format("dd MMM ", showDate).toString();
        dateTv.setText(timeString + "" + showDateTime);
        messageText.setText("Please wait");

        Calendar calStart = new GregorianCalendar();
        calStart.setTime(showDate);
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        Long startInMillis = calStart.getTimeInMillis();

        Calendar calEnd = new GregorianCalendar();
        calEnd.setTime(showDate);
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 0);
        calEnd.set(Calendar.MILLISECOND, 0);
        Long endInMillis = calEnd.getTimeInMillis();

        int totalFollowupCount = 0;
        String totalfollowups = "";
        if(leadSource != null && !leadSource.isEmpty()) {
            totalfollowups = "START_TIME" + ">=" + startInMillis + " AND " + "START_TIME" + "<=" + endInMillis + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + " RESPONSE_STATUS = 'accepted' " + " AND " + " LEAD_SOURCE = " +  "'" + leadSource + "' AND " + "FLP_COUNT >= " + 1;
        } else {
            totalfollowups = "START_TIME" + ">=" + startInMillis + " AND " + "START_TIME" + "<=" + endInMillis + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + " RESPONSE_STATUS = 'accepted' " + " AND " + "FLP_COUNT >= " + 1;
        }
        Cursor follwupcursor = db.query("remindertbNew", null, totalfollowups, null, null, null, "START_TIME ASC");
        if (follwupcursor != null && follwupcursor.getCount() > 0) {
            totalFollowupCount = follwupcursor.getCount();
            follwupcursor.close();
        }
        f_todo.setText(""+totalFollowupCount);
        String startDate =CommonUtils.getTimeFormatInISO(new Date(startInMillis));
        String date = CommonUtils.getDate(startDate);
        if (date.equalsIgnoreCase(CommonUtils.getTodayDate())) {
            flpTv.setText("FOLLOW_UPs: Today" );
        } else {
            flpTv.setText("FOLLOW_UPs: " + showDateTime + "");
        }

    }

    private void setAutoDailerSequential(Cursor cursor) {
        customerList = new ArrayList<>();
        tempcustomerList = new ArrayList<>();
        if(cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                itemId = cursor.getInt(cursor.getColumnIndex("_id"));
                String customerName = cursor.getString(cursor.getColumnIndex("TONAME"));
                String customerNumber = cursor.getString(cursor.getColumnIndex("TO1"));
                String status = cursor.getString(cursor.getColumnIndex("STATUS"));
                String substatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
                String substatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
                String notes = cursor.getString(cursor.getColumnIndex("NOTES"));
                NotificationData.statusString = status;
                String customkvs = cursor.getString(cursor.getColumnIndex("CUSTOMKVS"));
                CustomerLite customerLiteInfo = new CustomerLite(itemId, customerName, customerNumber, status, substatus1, substatus2, "","",notes, "", customkvs);
                //CustomerLite customerLiteInfo = new CustomerLite(itemId, customerName, customerNumber, status, substatus1, substatus2, notes, "", customkvs);
                customerList.add(customerLiteInfo);
            }
        }

        int size=4;

        String noOfCustomersToCall = ApplicationSettings.getPref(AppConstants.NO_OF_CUSTOMERS_TO_CALL, "");
        int noOfCustomersToDial = 0;

        if(noOfCustomersToCall != null && !noOfCustomersToCall.isEmpty()) {
            noOfCustomersToDial = Integer.valueOf(noOfCustomersToCall);
        }

        if(noOfCustomersToCall != null && !noOfCustomersToCall.isEmpty()) {
            noOfCustomersToDial = Integer.valueOf(noOfCustomersToCall);
            if(noOfCustomersToDial <= noOfCustomersToDial) {
                size = noOfCustomersToDial;
            }
        }

        if(customerList.size() <= size) {
            size = customerList.size();
        }

        for(int i=0; i<size; i++) {
            CustomerLite customerLiteInfo = customerList.get(i);
            tempcustomerList.add(i, customerLiteInfo);
        }

        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
        String customerListData = new Gson().toJson(tempcustomerList);
        ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData);
        System.out.print(customerListData);
    }

    private void followupsCount() {
        Date date = new Date();
        start = UearnHome.getstartTime();
        end = UearnHome.getendTime();
        int totalCompleted = 0, totalFirstCallCount = 0, totalFollowupCount = 0, followupDone = 0;
        String selection2 = "";
        if(leadSource != null && !leadSource.isEmpty()) {
            selection2 = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'deleted' " + " AND " + " LEAD_SOURCE = " +  "'" + leadSource + "' AND " + "FLP_COUNT = " + 0;
        } else {
            selection2 = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT = " + 0;
        }
        Cursor cursor = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
        if (cursor != null && (cursor.getCount() > 0)) {
            totalCompleted = cursor.getCount();
            cursor.close();
        }

        String totalFirstCall = "";
        if(leadSource != null && !leadSource.isEmpty()) {
            totalFirstCall = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND " + " LEAD_SOURCE = " +  "'" + leadSource + "' AND " + "FLP_COUNT = " + 0;
        } else {
            totalFirstCall = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND " + "FLP_COUNT = " + 0;
        }
        Cursor fcursor = db.query("remindertbNew", null, totalFirstCall, null, null, null, "START_TIME ASC");
        if (fcursor != null && fcursor.getCount() > 0) {
            totalFirstCallCount = fcursor.getCount();
            fcursor.close();
        }

        first_done.setText(""+totalCompleted);
        first_todo.setText(""+totalFirstCallCount);
        //firstCallText.setText("" + totalCompleted + "/" + totalFirstCallCount);

        String followupCompleted = "";
        if(leadSource != null && !leadSource.isEmpty()) {
            followupCompleted = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'deleted' " + " AND " + " LEAD_SOURCE = " +  "'" + leadSource + "' AND " + "FLP_COUNT >= " + 1;
        } else {
            followupCompleted = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 1;
        }
        Cursor fCompletedcursor = db.query("remindertbNew", null, followupCompleted, null, null, null, "START_TIME ASC");
        if (fCompletedcursor != null && (fCompletedcursor.getCount() > 0)) {
            followupDone = fCompletedcursor.getCount();
            cursor.close();
        }

        f_done.setText(""+followupDone);

        //followupText.setText(followupDone + "/" + totalFollowupCount);
    }


    private void fragmentTransaction(Fragment fragment) {
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.add(R.id.team_frame, fragment);
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void markRepeatCallAsCompleted(int itemId) {
        if(getContext() != null) {
            MySql dbHelper = MySql.getInstance(getActivity());
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 1);
            cv.put("COMPLETED", 1);
            cv.put("RESPONSE_STATUS", "completed");
            cv.put("APPOINTMENT_TYPE", "complete_appointment");
            if (itemId != 0) {
                db1.update("remindertbNew", cv, "_id=" + itemId, null);
            }
            db1.close();
        }
    }
}