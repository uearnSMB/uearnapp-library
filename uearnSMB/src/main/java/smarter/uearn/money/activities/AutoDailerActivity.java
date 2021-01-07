package smarter.uearn.money.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import smarter.uearn.money.R;
import smarter.uearn.money.models.GetCalendarEntryInfo;
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

public class AutoDailerActivity extends BaseActivity implements View.OnClickListener {
    public TextView name, stage, dateTv, stage1, stage2, counterText, messageText, firstCallText, followupText;
    RelativeLayout stopImage, playIcon, progressbar;
    SQLiteDatabase db;
    String number = "", nameFromdb, notes = "";
    int itemId;
    private String appointmentId = "", substatus1 = "", substatus2 = "", statusFronDb = "", orderPotential = "";
    private int rnr_count = 0;
    private CountDownTimer countDownTimer;
    private Long start = 0L, end = 0L, dbId = 0L;
    boolean isCounterRunning = false, firstcallCheck = false;
    private TextView first_done, first_todo, f_done, f_todo, flpTv;
    private PlayListener playListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.autodial_layout);
        SmarterSMBApplication.currentActivity = this;
        initUi();
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        db = dbHelper.getWritableDatabase();
        start = UearnHome.getstartTime();
        end = UearnHome.getendTime();
        changeStatusBarColor(this);
    }

    private void setPlayListener(PlayListener playListener) {
        this.playListener = playListener;
    }

    private void initUi() {
        name = findViewById(R.id.name);
        stage = findViewById(R.id.stage);
        dateTv = findViewById(R.id.date);
        stage1 = findViewById(R.id.substage1);
        stage2 = findViewById(R.id.substage2);
        counterText = findViewById(R.id.counterText);
        messageText = findViewById(R.id.message);
        firstCallText = findViewById(R.id.firstcall_count);
        followupText = findViewById(R.id.followup_count);

        first_todo = findViewById(R.id.first_todo_text);
        first_done = findViewById(R.id.first_done);
        f_todo = findViewById(R.id.f_todo_text);
        f_done = findViewById(R.id.f_done_text);
        flpTv = findViewById(R.id.flpTv);

        stopImage = findViewById(R.id.stop_image);
        playIcon = findViewById(R.id.playIcon);
        progressbar = findViewById(R.id.progressbar);
        stopImage.setOnClickListener(this);
        playIcon.setOnClickListener(this);
        progressbar.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        createfollowupAutoDailerView();
        followupsCount();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.progressbar) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            Intent intent = new Intent(this, UearnHome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        } else if (id == R.id.playIcon) {
            if (CommonUtils.isNetworkAvailable(this)) {
                if (number != null) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    PlayListener playListener = new PlayListener() {
                        @Override
                        public void resumeActivity(boolean check) {
                            isCounterRunning = false;
                            startcounterTimer();
                        }
                    };
                    CommonUtils.showCallRecordings(this, number, playListener);

                } else {
                    Toast.makeText(this, "No Recording found as this was RNR customer.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void createfollowupAutoDailerView() {
        Date date = new Date();

        String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + 1 + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        Cursor cursor1 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");
        Cursor cursor2 = null;
        if (cursor1 == null || cursor1.getCount() <= 0) {
            cursor1.close();
            String selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT = " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
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
        String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + "<" + start + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
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
            Toast.makeText(this, "Your Auto Dialler has No Database.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAutoDailer(Cursor cursor2) {
        itemId = cursor2.getInt(cursor2.getColumnIndex("_id"));
        nameFromdb = cursor2.getString(cursor2.getColumnIndex("TONAME"));
        number = cursor2.getString(cursor2.getColumnIndex("TO1"));
        statusFronDb = cursor2.getString(cursor2.getColumnIndex("STATUS"));
        substatus1 = cursor2.getString(cursor2.getColumnIndex("SUBSTATUS1"));
        substatus2 = cursor2.getString(cursor2.getColumnIndex("SUBSTATUS2"));
        appointmentId = cursor2.getString(cursor2.getColumnIndex("APPOINTMENT_ID"));
        orderPotential = cursor2.getString(cursor2.getColumnIndex("ORDER_POTENTIAL"));
        long startTime = cursor2.getLong(cursor2.getColumnIndex("START_TIME"));
        rnr_count = cursor2.getInt(cursor2.getColumnIndex("RNR_COUNT"));
        String flpvalue = cursor2.getString(cursor2.getColumnIndex("FLP_COUNT"));
        notes = cursor2.getString(cursor2.getColumnIndex("NOTES"));
        if (nameFromdb != null && !(nameFromdb.isEmpty())) {
            name.setText(nameFromdb);
        } else {
            name.setText("No Name");
        }
        stage.setText(statusFronDb);
        stage1.setText(substatus1);
        stage2.setText(substatus2);

        if (flpvalue != null) {
            if (flpvalue.equalsIgnoreCase("0")) {
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
        String totalfollowups = "START_TIME" + ">=" + startInMillis + " AND " + "START_TIME" + "<=" + endInMillis + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND " + "FLP_COUNT >= " + 1;
        Cursor follwupcursor = db.query("remindertbNew", null, totalfollowups, null, null, null, "START_TIME ASC");
        if (follwupcursor != null && follwupcursor.getCount() > 0) {
            totalFollowupCount = follwupcursor.getCount();
            follwupcursor.close();
        }
        f_todo.setText("" + totalFollowupCount);
        String startDate = CommonUtils.getTimeFormatInISO(new Date(startInMillis));
        String date = CommonUtils.getDate(startDate);
        if (date.equalsIgnoreCase(CommonUtils.getTodayDate())) {
            flpTv.setText("FOLLOW_UPs: Today");
        } else {
            flpTv.setText("FOLLOW_UPs: " + showDateTime + "");
        }
    }

    private void followupsCount() {
        Date date = new Date();
        start = UearnHome.getstartTime();
        end = UearnHome.getendTime();
        int totalCompleted = 0, totalFirstCallCount = 0, totalFollowupCount = 0, followupDone = 0;
        String selection2 = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT = " + 0;
        Cursor cursor = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
        if (cursor != null && (cursor.getCount() > 0)) {
            totalCompleted = cursor.getCount();
            cursor.close();
        }

        String totalFirstCall = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND " + "FLP_COUNT = " + 0;
        Cursor fcursor = db.query("remindertbNew", null, totalFirstCall, null, null, null, "START_TIME ASC");
        if (fcursor != null && fcursor.getCount() > 0) {
            totalFirstCallCount = fcursor.getCount();
            fcursor.close();
        }

        first_done.setText("" + totalCompleted);
        first_todo.setText("" + totalFirstCallCount);

        String followupCompleted = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 1;
        Cursor fCompletedcursor = db.query("remindertbNew", null, followupCompleted, null, null, null, "START_TIME ASC");
        if (fCompletedcursor != null && (fCompletedcursor.getCount() > 0)) {
            followupDone = fCompletedcursor.getCount();
            cursor.close();
        }

        f_done.setText("" + followupDone);
    }

    private void startcounterTimer() {
        if (!isCounterRunning) {
            if (number != null && !(number.isEmpty())) {
                String timebefore = ApplicationSettings.getPref(AppConstants.TIMER_BEFORE_CALL, "");
                if (timebefore != null && !(timebefore.isEmpty())) {
                    Long counter = 0L;
                    if (firstcallCheck) {
                        counter = 5000L;
                    } else {
                        int timer = Integer.parseInt(timebefore);
                        counter = (long) timer * 1000;
                    }
                    if (counter != 0) {
                        countDownTimer = new CountDownTimer(counter, 1000) {
                            public void onTick(long millis) {
                                isCounterRunning = true;
                                int sec = (int) millis / 1000;
                                counterText.setText("" + sec);
                            }

                            public void onFinish() {
                                messageText.setText("Please wait");
                                isCounterRunning = false;
                                makeAutoDailer();
                            }
                        }.start();
                    } else {
                        Toast.makeText(this, "Counter set to 0 secs, please change the settings", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "please set the counter time in settings", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (countDownTimer != null) {
            countDownTimer.cancel();
            isCounterRunning = false;
        }
    }

    private void makeAutoDailer() {
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
                            if (CommonUtils.isC2cNetworkAvailable(this)) {

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

                                new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
                                    @Override
                                    public void onComplete(String data, long request_code, int failure_code) {
                                        if (data != null && !(data.isEmpty())) {
                                            if (data.contains("_SMBALERT_")) {
                                                CommonUtils.setToast(getApplicationContext(), data.replace("_SMBALERT_", ""));
                                            } else if (data.contains("_SMBACP_")) {
                                                String uuidValue = data.replace("_SMBACP_", "");
                                                CommonUtils.storeUuidHash(customernumber, uuidValue);
                                                messageText.setText("Connecting to the customer. Please wait ...");
                                                CommonUtils.showACPScreen(getApplicationContext());
                                            } else {
                                                CommonUtils.storeUuidHash(customernumber, data);
                                                messageText.setText("Connecting to the customer. Please wait ...");
                                            }

                                        } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                                            CommonUtils.setToast(getApplicationContext(), "Invalid Auth key");
                                        } else if (failure_code == APIProvider.INVALID_REQUEST) {
                                            CommonUtils.setToast(getApplicationContext(), "Request Not Allowed");
                                        } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                                            CommonUtils.setToast(getApplicationContext(), "Invalid Parameters");
                                        } else if (failure_code == APIProvider.INVALID_NUMBER) {
                                            CommonUtils.setToast(getApplicationContext(), "This number is not valid");
                                        } else if (failure_code == APIProvider.SERVER_ALERT) {
                                            Toast.makeText(getApplicationContext(), APIProvider.SERVER_ALERT_MESSAGE, Toast.LENGTH_LONG).show();
                                        } else if (failure_code == APIProvider.DND) {
                                            markFollowupAsCompleated();
                                            createCmail(number);
                                            Intent intent = new Intent(AutoDailerActivity.this, ReuploadService.class);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                startForegroundService(intent);
                                            } else {
                                                startService(intent);
                                            }
                                            messageText.setText("This number is DND.");
                                            createfollowupAutoDailerView();
                                            followupsCount();
                                            Toast.makeText(getApplicationContext(), "Call Failed! Number is in Do Not Call registry.", Toast.LENGTH_LONG).show();
                                        } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                                            messageText.setText("Hi. Your mobile number in Not verified.");
                                            createfollowupAutoDailerView();
                                            followupsCount();
                                            Toast.makeText(getApplicationContext(), "Hi. Your mobile number in Not verified.", Toast.LENGTH_LONG).show();
                                        } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                                            messageText.setText("No active integration found.");
                                            createfollowupAutoDailerView();
                                            followupsCount();
                                            Toast.makeText(getApplicationContext(), "No active integration found.", Toast.LENGTH_LONG).show();
                                        } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                                            messageText.setText("Number not registered for outbound services for this agent");
                                            createfollowupAutoDailerView();
                                            followupsCount();
                                            Toast.makeText(getApplicationContext(), "Number not registered for outbound services for this agent", Toast.LENGTH_LONG).show();
                                        } else {
                                            messageText.setText("Please check your internet connection.");
                                            createfollowupAutoDailerView();
                                            followupsCount();
                                            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    /*}).call();*/
                                }).reClickToCall(knowlarityModel);
                                messageText.setText("Dialling");

                            } else {
                                messageText.setText("Request failed.");
                                Toast.makeText(AutoDailerActivity.this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            messageText.setText("No SR Number.");
                            createfollowupAutoDailerView();
                            followupsCount();
                            Toast.makeText(this, "No SR Number", Toast.LENGTH_SHORT).show();
                            callToCustomer();
                            this.finish();
                        }
                    } else {
                        messageText.setText("No Client ID.");
                        createfollowupAutoDailerView();
                        followupsCount();
                        Toast.makeText(this, "No Client ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    createfollowupAutoDailerView();
                    followupsCount();
                    messageText.setText("Invalid User Number.");
                    Toast.makeText(this, "Invalid User Number", Toast.LENGTH_SHORT).show();
                }
            } else {
                createfollowupAutoDailerView();
                followupsCount();
                messageText.setText("Invalid Customer Number.");
                Toast.makeText(this, "Invalid Customer Number", Toast.LENGTH_SHORT).show();
            }
        } else {
            callToCustomer();
            this.finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void callToCustomer() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        if (number != null) {
            String num = "tel:" + number;
            intent.setData(Uri.parse(num));
        }
        setCallData();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void setCallData() {
        NotificationData.appointment_db_id = itemId;
        NotificationData.notificationData = true;
        NotificationData.appointment_id = appointmentId;
        NotificationData.statusString = statusFronDb;
        if (notes != null) {
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

    private void markFollowupAsCompleated() {

        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("UPLOAD_STATUS", 1);
        cv.put("COMPLETED", 1);
        cv.put("RESPONSE_STATUS", "completed");
        cv.put("APPOINTMENT_TYPE", "complete_appointment");
        cv.put("SUBJECT", "DND NUMBER");
        String start = CommonUtils.getTimeFormatInISO(new Date());
        cv.put("CREATED_AT", start);
        cv.put("RNR_COUNT", rnr_count);

        if (itemId != 0) {
            db1.update("remindertbNew", cv, "_id=" + itemId, null);
        }
        db1.close();

        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
        getCalendarEntryInfo.appointment_id = appointmentId;
        getCalendarEntryInfo.responsestatus = "completed";
        getCalendarEntryInfo.update_all_fields = false;
        getCalendarEntryInfo.tat = 0;
        getCalendarEntryInfo.rnrCount = 0;

        if (start != null && !(start.isEmpty())) {
            getCalendarEntryInfo.created_at = start;
        }
    }

    private void createCmail(String phoneno) {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("EVENT_TYPE", "call_dnd");
        if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
            cv.put("FROM1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
            cv.put("TO1", phoneno);
            cv.put("MSG_RECEPIENT_NO", phoneno);

        }
        cv.put("SUBJECT", "Outgoing Call");
        cv.put("MESSAGE", "Outgoing Call");
        cv.put("STATUS", statusFronDb);
        cv.put("UPLOAD_STATUS", 1);
        cv.put("EMAIL", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null));
        cv.put("PARENT", 0);
        cv.put("STARTTIME", CommonUtils.getTimeFormatInISO(new Date()));
        cv.put("ENDTIME", CommonUtils.getTimeFormatInISO(new Date()));
        cv.put("UNREAD", "false");
        String callerName = CommonUtils.getContactName(this, phoneno);
        cv.put("CALLER", callerName);
        db1.insert("mytbl", null, cv);
        db1.close();
    }

    public interface PlayListener {
        void resumeActivity(boolean check);
    }
}
