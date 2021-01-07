package smarter.uearn.money.activities.appointment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import smarter.uearn.money.activities.UearnActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.views.SmartAudioPlayer;
import smarter.uearn.money.services.DeleteEventService;
import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.views.events.HeadPhoneReciever;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kavya on 7/30/2015.
 */
public class AppointmentViewActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView subjectTitle, subjectTextview, startTimeTextview, dateTextview, locationTextview, durationTextview, callerName;
    private SmartAudioPlayer smartAudioPlayer;
    private TextView notesTextView, companyAddressTextView, orderPotentialTextView, latestSalesStage;
    private WebView mapWebView;
    private SQLiteDatabase db;
    private long dbid = 0L;
    private boolean disable_ok = false;
    private LinearLayout location_layout, websiteLayout, sourceOfLeadLayout, callerNameLayout, callerNumberLayout, designationLayout, companyNameLayout, callrecordingLayout, companyAddressLayout;
    private String appointmentId;
    private String external_calendar_reference, statusString;
    private TextView callerNumber, companyName, designation, website, emailId, status, sourceOfLead, productType;
    private String responseStatus;
    private String appointmentIdFromOneView;
    private String order_value;
    private LinearLayout nextAppointmentLayout;
    private TextView nextAppointmenttext;
    private TextView nextAppointmentSubject;
    private TextView nextAppointmentTime;
    private String msg_recepient_no = "";
    private Long current_startTime = 0L;
    private Long nextAppointment_dbId;
    private long dbid1;
    private Button acceptLeadButton;
    private LinearLayout llExtraNotes;
    private TextView tvNotesImage, tvNotesAudio, tvNotesText, substatusTv1, substatusTv2, tvmyNotesText;
    private String mNotesImageUrl = "", mNotesAudioUrl = "";
    private WebView wvNotes;
    private ImageView callRecordingIcon;
    private int completed = 0;
    private String callerNumber1 = "";
    private String substatus1 = "", substatus2 = "", callername = "";
    private int rnr_count = 0;
    String screen = "";
    private List<CustomerLite> customerList = null;
    private List<CustomerLite> tempcustomerList = null;
    private String company = "";
    private LinearLayout stageLayout, subStageLayout, followupLayout, emailLayout, notesLayout;
    android.app.AlertDialog alertDialog;
    private boolean isCalled = false;
    private String customKVS = "";
    private LinearLayout defaultLayout, customLayout;
    long id = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.view_appointment);
        SmarterSMBApplication.currentActivity = this;
        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }

        changeStatusBarColor(this);
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            dbid = intent.getLongExtra("id", 0);
        }
        if (intent.hasExtra("Appointment_id")) {
            appointmentIdFromOneView = intent.getStringExtra("Appointment_id");
        }
        acceptLeadButton = findViewById(R.id.acceptButton);
        acceptLeadButton.setOnClickListener(this);
        if (intent.hasExtra("AcceptAll")) {
            acceptLeadButton.setVisibility(View.VISIBLE);
        } else {
            acceptLeadButton.setVisibility(View.GONE);
        }
        subjectTextview = findViewById(R.id.subjectText);
        subjectTitle = findViewById(R.id.subjectTextView);
        startTimeTextview = findViewById(R.id.timeText);
        dateTextview = findViewById(R.id.dayText);
        smartAudioPlayer = findViewById(R.id.player);
        locationTextview = findViewById(R.id.locationText);
        durationTextview = findViewById(R.id.durationTimeText);
        callrecordingLayout = findViewById(R.id.callrecordingLayout);
        callerName = findViewById(R.id.callerName);
        companyAddressTextView = findViewById(R.id.companyAddressTextView);
        orderPotentialTextView = findViewById(R.id.orderpotentialTextView);
        callRecordingIcon = findViewById(R.id.iv_call_recording_icon);
        callerNameLayout = findViewById(R.id.callerNameLayout);
        companyNameLayout = findViewById(R.id.companyNameLayout);
        designationLayout = findViewById(R.id.designationLayout);
        callerNumberLayout = findViewById(R.id.callerNumberLayout);
        callerNumberLayout.setOnClickListener(this);
        companyAddressLayout = findViewById(R.id.companyAddressLayout);
        sourceOfLeadLayout = findViewById(R.id.sourceOfLeadLayout);
        websiteLayout = findViewById(R.id.websiteLayout);
        location_layout = findViewById(R.id.location_layout);
        nextAppointmentLayout = findViewById(R.id.next_appointmentLayout);
        nextAppointmenttext = findViewById(R.id.nameOrNumber);
        nextAppointmentSubject = findViewById(R.id.next_appointmentsubject);
        nextAppointmentTime = findViewById(R.id.next_appointmenttime);
        substatusTv1 = findViewById(R.id.subStatueTv1);
        substatusTv2 = findViewById(R.id.subStatueTv2);
        nextAppointmentLayout.setVisibility(View.GONE);
        nextAppointmentLayout.setOnClickListener(this);
        mapWebView = findViewById(R.id.mapLocation);
        CommonUtils.initMapWebview(mapWebView, "Bengaluru");
        callerNumber = findViewById(R.id.callerNumber);
        designation = findViewById(R.id.designation);
        companyName = findViewById(R.id.companyName);
        latestSalesStage = findViewById(R.id.latestSalesStage);
        website = findViewById(R.id.website);
        emailId = findViewById(R.id.emailId);
        status = findViewById(R.id.status);
        sourceOfLead = findViewById(R.id.sourceOfLead);
        productType = findViewById(R.id.productType);
        llExtraNotes = findViewById(R.id.ll_extra_notes);
        llExtraNotes.setVisibility(View.GONE);
        tvNotesImage = findViewById(R.id.tv_notes_image);
        tvNotesImage.setVisibility(View.GONE);
        tvNotesImage.setOnClickListener(this);
        tvNotesAudio = findViewById(R.id.tv_notes_audio);
        tvNotesAudio.setVisibility(View.GONE);
        tvNotesAudio.setOnClickListener(this);
        tvNotesText = findViewById(R.id.tv_notes_text);
        tvmyNotesText = findViewById(R.id.mytext);
        tvNotesText.setOnClickListener(this);
        wvNotes = findViewById(R.id.wv_notes);
        wvNotes.setVisibility(View.GONE);
        notesTextView = findViewById(R.id.personalNotesTextview);
        stageLayout = findViewById(R.id.stageLayout);
        subStageLayout = findViewById(R.id.subStageLayout);
        followupLayout = findViewById(R.id.followupLayout);
        emailLayout = findViewById(R.id.emailLayout);
        notesLayout = findViewById(R.id.notesLayout);
        defaultLayout = findViewById(R.id.defaultLayout);
        customLayout = findViewById(R.id.customLayout);
        //extraNoteLayout = findViewById(R.id.extraNoteLayout);
        if (intent.hasExtra("disable_ok"))
            disable_ok = intent.getBooleanExtra("disable_ok", false);
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        db = dbHelper.getWritableDatabase();
        db.enableWriteAheadLogging();
        if (dbid != 0) {
            getDetailsFromDb(dbid, null);
            NotificationData.appointment_db_id = dbid;
        } else if (appointmentIdFromOneView != null && !appointmentIdFromOneView.equals("")) {
            getDetailsFromDb(0, appointmentIdFromOneView);
        }

        if (msg_recepient_no != null) {
            nextAppointment_dbId = getNextAppointmentData(msg_recepient_no, current_startTime);
        }
        //db.close();
        CommonUtils.permissionsCheck(AppointmentViewActivity.this);


        if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
            screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
            if (screen!= null && ((screen.equalsIgnoreCase("Auto1AfterCallActivity")) || (screen.equalsIgnoreCase("Auto2AfterCallActivity")))) {
//                View view1 = findViewById(R.id.devide1);
//                View view2 = findViewById(R.id.devide2);
//                View view3 = findViewById(R.id.devide3);
                View view4 = findViewById(R.id.devide4);
                View view5 = findViewById(R.id.devide5);
                View view6 = findViewById(R.id.devide6);
//                view1.setVisibility(View.GONE);
//                view2.setVisibility(View.GONE);
//                view3.setVisibility(View.GONE);
                view4.setVisibility(View.GONE);
                view5.setVisibility(View.GONE);
                view6.setVisibility(View.GONE);

                designationLayout.setVisibility(View.GONE);
                companyNameLayout.setVisibility(View.GONE);
                companyAddressLayout.setVisibility(View.GONE);
                sourceOfLeadLayout.setVisibility(View.GONE);
                websiteLayout.setVisibility(View.GONE);
                location_layout.setVisibility(View.GONE);
                mapWebView.setVisibility(View.GONE);
                tvmyNotesText.setText("Previous Activities:");
                tvNotesText.setVisibility(View.VISIBLE);
                llExtraNotes.setVisibility(View.VISIBLE);
                wvNotes.setVisibility(View.GONE);
                notesTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void changeStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }
    void showDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(R.layout.alert_dialog, null,false);

        Button cancelBtn = contentView.findViewById(R.id.cancelBtn);
        Typeface titlef = Typeface.createFromAsset(this.getAssets(),
                "fonts/SF-UI-Display-Semibold.ttf");
        TextView hints1Tv = contentView.findViewById(R.id.hints1Tv);
        hints1Tv.setTypeface(titlef);
        TextView hints2Tv = contentView.findViewById(R.id.hints2Tv);

        Typeface titleFace = Typeface.createFromAsset(this.getAssets(),
                "fonts/SF-UI-Display-Semibold.ttf");
        cancelBtn.setTypeface(titleFace);

        alertDialog = new android.app.AlertDialog.Builder(this)
                .setView(contentView)
                .create();

        alertDialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void getDetailsFromDb(long dbid, String appointment_id) {
        String timeDuration = "", playUrl = "";
        long startMls = 0, endMls;
        if (!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
            db.enableWriteAheadLogging();
        }
        String subject1 = "No Title", notes1 = "No details were added", location1 = "No location info";
        Cursor cursor = null;
        try {
            if (dbid != 0) {
                cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + dbid, null);
                //Log.d("dbid",""+dbid);
            } else if (appointment_id != null) {
                cursor = db.rawQuery("SELECT * FROM remindertbNew where APPOINTMENT_ID=" + "'" + appointment_id + "'", null);
                //Log.d("AppointmentId",appointment_id);
            }
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndex("_id"));
                subject1 = cursor.getString(cursor.getColumnIndex("SUBJECT"));
                notes1 = cursor.getString(cursor.getColumnIndex("NOTES"));
                location1 = cursor.getString(cursor.getColumnIndex("LOCATION"));
                startMls = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                //Added By Srinath
                current_startTime = startMls;
                endMls = cursor.getLong(cursor.getColumnIndex("END_TIME"));
                callername = cursor.getString(cursor.getColumnIndex("TONAME"));
                String designation = cursor.getString(cursor.getColumnIndex("DESIGNATION"));
                setDesignationView(designation);
                String companyName = cursor.getString(cursor.getColumnIndex("COMPANY_NAME"));
                setCompanyNameView(companyName);
                callerNumber1 = cursor.getString(cursor.getColumnIndex("TO1"));
                completed = cursor.getInt(cursor.getColumnIndex("COMPLETED"));
                rnr_count = cursor.getInt(cursor.getColumnIndex("RNR_COUNT"));

                setCallerNumber(callerNumber1);
                String lateststatus = CommonUtils.getStatusFromSmartContactTable(this, callerNumber1);
                //String lateststatus = CommonUtils.getStatusFromReminderTable(this, callerNumber1);
                if (lateststatus != null && !lateststatus.equals("")) {
                    latestSalesStage.setText(lateststatus);
                }
                setCallerNameViews(callername, callerNumber1);
                long durationMills = endMls - startMls;
                long min = durationMills / (1000 * 60);
                timeDuration = getTimeduration(min);
                playUrl = cursor.getString(cursor.getColumnIndex("CALLREC_URL"));


                appointmentId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
                String source_Of_Lead = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
                sourceOfLead.setText(source_Of_Lead);

                //Log.d("AppointmentId",appointmentId);
                String product_type = cursor.getString(cursor.getColumnIndex("PRODUCT_TYPE"));
                productType.setText(product_type);

                String websiteString = cursor.getString(cursor.getColumnIndex("WEBSITE"));
                String emailIdString = cursor.getString(cursor.getColumnIndex("EMAILID"));
                statusString = cursor.getString(cursor.getColumnIndex("STATUS"));
                //Log.d("status",statusString);

                String companyAddress = cursor.getString(cursor.getColumnIndex("COMPANY_ADDRESS"));
                external_calendar_reference = cursor.getString(cursor.getColumnIndex("EXTERNAL_REFERENCE"));

                companyAddressTextView.setText(companyAddress);
                website.setText(websiteString);
                emailId.setText(emailIdString);
                status.setText(statusString);
                responseStatus = cursor.getString(cursor.getColumnIndex("RESPONSE_STATUS"));
                order_value = cursor.getString(cursor.getColumnIndex("ORDER_POTENTIAL"));

                //mNotesImageUrl = "http://yahoo.com";
                mNotesImageUrl = cursor.getString(cursor.getColumnIndex("NOTES_IMAGE"));
                if ((mNotesImageUrl != null) && !mNotesImageUrl.isEmpty()) {
                    llExtraNotes.setVisibility(View.VISIBLE);
                    tvNotesImage.setVisibility(View.VISIBLE);
                }

                //mNotesAudioUrl = "http://google.com";
                mNotesAudioUrl = cursor.getString(cursor.getColumnIndex("NOTES_AUDIO"));
                substatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
                substatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
                customKVS = cursor.getString(cursor.getColumnIndex("CUSTOMKVS"));
                if(customKVS != null && !customKVS.isEmpty()){
                    defaultLayout.setVisibility(View.GONE);
                    customLayout.setVisibility(View.VISIBLE);
                    createCustomLayoutDynamically(customKVS);
                } else {
                    defaultLayout.setVisibility(View.VISIBLE);
                    customLayout.setVisibility(View.GONE);
                }

                if ((mNotesAudioUrl != null) && !mNotesAudioUrl.isEmpty()) {
                    llExtraNotes.setVisibility(View.VISIBLE);
                    tvNotesAudio.setVisibility(View.VISIBLE);
                }

                //ADDed By Srinath.k
                if (dbid == 0) {
                    dbid1 = cursor.getLong(cursor.getColumnIndex("_id"));
                }
                orderPotentialTextView.setText(order_value);
                //setAcceptOrRejectLayout();
            } else {
                //TODO: get details from server and save it to local db
                if (appointment_id != null) {
                    getCalendarEvent(appointment_id);
                } else {
                    if (appointmentId != null) {
                        getCalendarEvent(appointmentId);
                    } else {
                        //Toast.makeText(this, "You have attended to this event", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Calender Event Not Exist", Toast.LENGTH_SHORT).show();
                        goToHomeScreen();
                    }
                }
            }

            if (notes1 != null && !(notes1.isEmpty()) && !(notes1.equalsIgnoreCase(" ")) && !notes1.equals("null")) {
                notesTextView.setText(notes1);
                notesTextView.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                llExtraNotes.setVisibility(View.VISIBLE);
                tvNotesText.setVisibility(View.VISIBLE);
            }

            if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Bank1AfterCallActivity")))) {
                    if (substatus1 != null && !(substatus1.equalsIgnoreCase("null"))  && statusString != null && !statusString.equalsIgnoreCase("NEW DATA")) {
                        substatusTv1.setText(substatus1);
                    }
                    if (substatus2 != null  && !(substatus2.equalsIgnoreCase("null")) && statusString != null && !statusString.equalsIgnoreCase("NEW DATA")) {
                        substatusTv2.setText(substatus2);
                    }
                    subjectTitle.setText("Remarks  : ");
                    tvmyNotesText.setText("Remarks  : ");
                }
            }

            if (playUrl != null && (playUrl.isEmpty())) {
                if (callerNumber1 != null && (!callerNumber1.isEmpty())) {
                    playUrl = getCallRecordingUrl(callerNumber1);
                }
            } else if (playUrl == null) {
                if (callerNumber1 != null && (!callerNumber1.isEmpty())) {
                    playUrl = getCallRecordingUrl(callerNumber1);
                }
            }
            if (playUrl == null) {
                playUrl = "";
            }
            setViews(timeDuration, startMls, playUrl, subject1, location1, notes1);




//            if((statusString != null) && (statusString.length() > 1)) {
//                stageLayout.setVisibility(View.VISIBLE);
//            } else {
//                stageLayout.setVisibility(View.GONE);
//            }
//
//            int length1 = substatus1.length();
//
//
            if((substatus1 != null) && (substatus1.length() > 1) || (substatus2 != null) && (substatus2.length() > 1)) {
                subStageLayout.setVisibility(View.VISIBLE);
            } else {
                subStageLayout.setVisibility(View.GONE);
            }









        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }                            /* cursor Closed By :::KSN */
        }
    }

    private void createCustomLayoutDynamically(String customKVS) {
        try {
            if (customKVS != null && !customKVS.isEmpty()) {
                JSONObject jsonObj = new JSONObject(customKVS);
                if (jsonObj != null && jsonObj.has("customer_details")) {
                    JSONArray jsonArray = jsonObj.getJSONArray("customer_details");
                    int length = jsonArray.length();

                    for (int i = 0; i < length; i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        String key = jObj.getString("key");
                        String value = jObj.getString("value");

                        int labelColor = getResources().getColor(R.color.smb_text_dark);
                        int valueColor = getResources().getColor(R.color.smb_grey_54);
                        LinearLayout parent = new LinearLayout(this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(16, 0, 16, 8);
                        parent.setLayoutParams(params);
                        parent.setOrientation(LinearLayout.VERTICAL);

                        TextView labelTextView = new TextView(this);
                        ViewGroup.LayoutParams lpLabel = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        labelTextView.setLayoutParams(lpLabel);
                        labelTextView.setHint(key);
                        labelTextView.setPadding(0, 2, 5, 2);
                        labelTextView.setGravity(Gravity.LEFT);
                        labelTextView.setTextColor(labelColor);
                        labelTextView.setTextSize(14);
                        labelTextView.setLayoutParams(params);
                        parent.addView(labelTextView);

                        TextView valueTextView = new TextView(this);
                        ViewGroup.LayoutParams lpValue = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        valueTextView.setLayoutParams(lpValue);
                        valueTextView.setText(value);
                        valueTextView.setTextColor(valueColor);
                        valueTextView.setTextSize(14);
                        valueTextView.setLayoutParams(params);
                        parent.addView(valueTextView);

                        View separatorView = new View(this);
                        ViewGroup.LayoutParams lpSeparator = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                        separatorView.setLayoutParams(lpSeparator);
                        LinearLayout.LayoutParams separatorLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        separatorLayout.setMargins(16, 0, 16, 8);
                        separatorView.setLayoutParams(separatorLayout);
                        separatorView.setBackgroundResource(R.drawable.divider_line);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            separatorView.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.smb_grey_54));
                        }
                        parent.addView(separatorView);

                        customLayout.addView(parent);
                    }
                } else {
                    defaultLayout.setVisibility(View.VISIBLE);
                    customLayout.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            defaultLayout.setVisibility(View.VISIBLE);
            customLayout.setVisibility(View.GONE);
        }
    }

    private void getCalendarEvent(final String appointmentId) {
        new APIProvider.Get_CalendarEvent(appointmentId, 1, new API_Response_Listener<GetCalendarEntryInfo>() {
            @Override
            public void onComplete(GetCalendarEntryInfo data, long request_code, int failure_code) {
                if (data != null) {
                    CommonUtils.saveCalendarEventToLocalDB(getApplicationContext(), data, "app");
                    getDetailsFromDb(0, appointmentId);
                }
            }
        }).call();
    }


    public void setCallerNameViews(String callerNameString, String callerNumberString) {
        if (callerNameString != null && !callerNameString.equals(callerNumberString) && (!callerNameString.equals("") && !callerNameString.equals("null"))) {
            callerName.setText(callerNameString);
        } else {
            if(callerNameLayout != null) {
                callerNameLayout.setVisibility(View.GONE);
            }
        }
    }

    private String getCallRecordingUrl(String callerNumber) {
        String playUrl = "";
        if (!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
            db.enableWriteAheadLogging();
        }

        String selection = "TO1=" + "'" + callerNumber + "'";
        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");
        //Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where TO1=" + "'" + callerNumber + "'", null);

        if (cursor != null && (cursor.getCount() > 0)) {
            try {
                cursor.moveToFirst();
                do {
                    String playUrl1 = cursor.getString(cursor.getColumnIndex("CALLREC_URL"));
                    if (playUrl1 != null && !(playUrl1.isEmpty())) {
                        return playUrl1;
                    }
                } while (cursor.moveToNext());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        if (playUrl != null && !(playUrl.isEmpty())) {
            return playUrl;
        } else {
            return "";
        }
    }


/*
    public void setAcceptOrRejectLayout() {
        Log.i("ViewAppointment","Response status is "+responseStatus);
        if(responseStatus != null && responseStatus.equals("pending")) {
            acceptOrRejectAppLayout.setVisibility(View.VISIBLE);
        }
        else {
            acceptOrRejectAppLayout.setVisibility(View.GONE);
        }
    }
*/

    private void setDesignationView(String designationText) {
        if (designationText != null) {
            designation.setText(designationText);
        } else {
            designationLayout.setVisibility(View.GONE);
        }
    }

    private void setCompanyNameView(String companyNameText) {
        if (companyNameText != null) {
            companyName.setText(companyNameText);
        } else {
            companyNameLayout.setVisibility(View.GONE);
        }
    }

    private void setCallerNumber(String callernumber) {
        if (callernumber != null && !callernumber.equals("null")) {
            //callerNumber

            //CUSTOMER_NUMBER_STATUS

            boolean isShowCustomerNumber = ApplicationSettings.getPref(AppConstants.CUSTOMER_NUMBER_STATUS, false);
            if(isShowCustomerNumber) {
                callerNumber.setText(callernumber);
            }

            msg_recepient_no = callernumber;
        } else {
            callerNumberLayout.setVisibility(View.GONE);
        }
//        String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
//        if (questionsAct != null && !questionsAct.isEmpty() && questionsAct.equals("MMT")) {
//            callerNumber.setVisibility(View.GONE);
//        } else {
//            callerNumber.setVisibility(View.VISIBLE);
//        }
    }

    public void setViews(String timeDuration, long startMls, String playUrl, String subject, String location, String notes) {
        String start1 = DateFormat.format("dd-MMM-yyyy hh:mm a", startMls).toString();
        subjectTextview.setText(subject);
        locationTextview.setText(location);

        String mapKeyword = locationTextview.getText().toString();
        if (mapKeyword != null || mapKeyword != "") {
            CommonUtils.initMapWebview(mapWebView, mapKeyword);
        }
        String[] split = start1.split(" ");
        dateTextview.setText(split[0]);
        startTimeTextview.setText(split[1] + " " + split[2]);
        if (playUrl != null && !playUrl.equals(" ") && !playUrl.equals("")) {
            callrecordingLayout.setVisibility(View.VISIBLE);
            smartAudioPlayer.setDataSource(playUrl);
        }
        durationTextview.setText(timeDuration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.only_edit, menu);
        if (completed == 1) {
            menu.findItem(R.id.edit).setVisible(false);
            if(screen != null && (screen.equalsIgnoreCase("Bank1AfterCallActivity") || screen.equalsIgnoreCase("UearnActivity"))) {
                menu.findItem(R.id.new_menu).setVisible(false);
            } else if (!(screen!= null && ((screen.equalsIgnoreCase("Auto1AfterCallActivity")) || (screen.equalsIgnoreCase("Auto2AfterCallActivity"))))) {
                menu.findItem(R.id.new_menu).setVisible(true);
            }
        } else {
            menu.findItem(R.id.edit).setTitle("Edit");
        }
        restoreActionBar("Details");
        boolean isEditable = ApplicationSettings.getPref(AppConstants.IS_FOLLOWUP_EDITABLE, false);
        if(isEditable) {
            menu.findItem(R.id.edit).setVisible(true);
        } else {
            menu.findItem(R.id.edit).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar(String mTitle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7030a0")));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                break;

            case R.id.edit:
                Intent editCalendar = new Intent(this, AppointmentEditActivity.class);
                if (dbid == 0) {
                    editCalendar.putExtra("db_id", dbid1);
                } else {
                    editCalendar.putExtra("db_id", dbid);
                }
                startActivity(editCalendar);
                finish();
                break;

            case R.id.new_menu:
                if (completed == 1) {
                    Intent appointment = new Intent(this, AppointmentCreateActivity.class);
                    if (callerNumber1 != null && !(callerNumber1.isEmpty())) {
                        appointment.putExtra("callerNumber", callerNumber1);
                    }
                    if (dbid == 0) {
                        appointment.putExtra("database_id", dbid1);
                    } else {
                        appointment.putExtra("database_id", dbid);
                    }
                    startActivity(appointment);

                }
                finish();
                break;

            case R.id.action_dialer:
                if (completed != 1) {
                    boolean secondSim = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                        SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
                        @SuppressLint("MissingPermission") List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

                        //Log.d("Test", "Current list = " + subsInfoList);
                        if (subsInfoList != null) {
                            for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                                String number = subscriptionInfo.getNumber();
                                int SimStotNumber = subscriptionInfo.getSimSlotIndex();
                                if (SimStotNumber == 1) {
                                    secondSim = true;
                                }

                                //Log.d("Test", " Number is  " + number);
                            }
                        } else {
                            Toast.makeText(this, "No SIM", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        secondSim = true;
                    }

                    /*boolean mobileOnSimData = false;
                    if (ApplicationSettings.containsPref(AppConstants.MOBILE_DATA_ON_SIM)) {
                        mobileOnSimData = ApplicationSettings.getPref(AppConstants.MOBILE_DATA_ON_SIM, false);
                    }
                    */
                    if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                        /*if ((!secondSim && !(mobileOnSimData)) || (secondSim && mobileOnSimData)) {*/
                        if (callerNumber.getText() != null) {
                            String userNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                            if (userNumber != null && !(userNumber.isEmpty())) {
                                final String customernumber = callerNumber.getText().toString();
                                String sr_number = ApplicationSettings.getPref(AppConstants.SR_NUMBER, "");
                                String caller_id = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                                if (caller_id != null && !(caller_id.isEmpty())) {
                                    if (sr_number != null && !(sr_number.isEmpty())) {
                                        if (CommonUtils.isC2cNetworkAvailable(this)) {
                                            KnowlarityModel knowlarityModel = new KnowlarityModel(sr_number, userNumber, callerNumber.getText().toString());
                                            knowlarityModel.setClient_id(caller_id);
                                            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                                            if (screen != null && (screen.equalsIgnoreCase("Auto1AfterCallActivity") || screen.equalsIgnoreCase("Auto2AfterCallActivity"))) {
                                                reCall(knowlarityModel, customernumber);
                                            } else {
                                                sbiCall(knowlarityModel, customernumber);
                                            }
                                        } else {
                                            Toast.makeText(AppointmentViewActivity.this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(this, "No SR Number", Toast.LENGTH_SHORT).show();
                                        callToCustomer();
                                        this.finish();
                                    }
                                } else {
                                    Toast.makeText(this, "No Client ID", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Invalid User Number", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Invalid Customer Number", Toast.LENGTH_SHORT).show();
                        }
                       /* } else {
                            callToCustomer();
                            this.finish();
                        }*/
                    } else {
                        callToCustomer();
                        this.finish();
                    }
                } else {
                    Toast.makeText(this, "Follow-up already done", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    private void markFollowupAsCompleated(int followup_status) {

        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        db1.enableWriteAheadLogging();

        ContentValues cv = new ContentValues();
        cv.put("UPLOAD_STATUS", 0);
        cv.put("COMPLETED", 1);
        cv.put("RESPONSE_STATUS", "completed");
        if(followup_status == AppConstants.INVALID) {
            cv.put("STATUS", "OTHERS");
            cv.put("SUBSTATUS1", "INVALID");
        } else {
            cv.put("SUBJECT", "DND NUMBER");
        }
        cv.put("APPOINTMENT_TYPE", "update_appointment");

        String start = CommonUtils.getTimeFormatInISO(new Date());
        cv.put("CREATED_AT", start);
        cv.put("RNR_COUNT", rnr_count);

        if (dbid != 0) {
            db1.update("remindertbNew", cv, "_id=" + dbid, null);
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

    private void updateAppointmentCall(GetCalendarEntryInfo getCalendarEntryInfo) {

        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.Update_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        }
    }

    private void createCmail(String phoneno, int followup_status) {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        db1.enableWriteAheadLogging();

        ContentValues cv = new ContentValues();
        if(followup_status == AppConstants.INVALID) {
            cv.put("EVENT_TYPE", "call_invalid");
            cv.put("STATUS", "INVALID");
        } else {
            cv.put("EVENT_TYPE", "call_dnd");
            cv.put("STATUS", status.getText().toString());
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
        String callerName = CommonUtils.getContactName(this, phoneno);
        cv.put("CALLER", callerName);
        db1.insert("mytbl", null, cv);
        db1.close();
    }

    private void callToCustomer() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        if (callerNumber.getText() != null) {
            String num = "tel:" + callerNumber.getText();
            intent.setData(Uri.parse(num));
        }
        setCallData();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private void setCallData() {
        NotificationData.notificationData = true;
        NotificationData.appointment_id = appointmentId;
        NotificationData.statusString = status.getText().toString();
        if(notesTextView.getText().toString() != null) {
            NotificationData.notes_string = notesTextView.getText().toString();
        }
        NotificationData.order_value = order_value;
        NotificationData.isAppointment = true;
        if (callername != null) {
            NotificationData.knolarity_name = callername;
        }
        if (substatus1 != null && statusString != null && !statusString.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus1 = substatus1;
        }
        if (substatus2 != null && statusString != null && !statusString.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus2 = substatus2;
        }

        if (screen != null && ((screen.equalsIgnoreCase("Auto2AfterCallActivity") || (screen.equalsIgnoreCase("Auto1AfterCallActivity"))))) {
            NotificationData.makeACall = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smartAudioPlayer != null) {
            smartAudioPlayer.finish();
            smartAudioPlayer.release();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        db = null;
    }

    @Override
    protected void onStop() {
        if (smartAudioPlayer != null) {
            smartAudioPlayer.release();
        }
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void deleteAppointment() {
        if (CommonUtils.isNetworkAvailable(this)) {
            SmartUser smartUser = SmarterSMBApplication.SmartUser;
            GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
            getCalendarEntryInfo.setAppointment_id(appointmentId);
            getCalendarEntryInfo.setExternal_calendar_reference(external_calendar_reference);
            if (smartUser != null)
                getCalendarEntryInfo.setUser_id(smartUser.getId());
            if (dbid != 0) {
                db.delete("remindertbNew", "_id=" + dbid, null);
            }

            Intent intentService = new Intent(this, DeleteEventService.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("calenderEntryInfo", getCalendarEntryInfo);
            intentService.putExtras(bundle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intentService);
            } else {
                startService(intentService);
            }
            finish();
        } else {
            ContentValues values = new ContentValues();
            values.put("APPOINTMENT_TYPE", "Delete_Appointment");
            values.put("UPLOAD_STATUS", 0);
            values.put("STATUS", "Delete");
            db.update("remindertbNew", values, "_id=" + dbid, null);

            Intent aIntent = new Intent(this, ReuploadService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(aIntent);
            } else {
                startService(aIntent);
            }
            finish();

        }

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private void goToHomeScreen() {
        Intent intent = new Intent(this, UearnHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("appointment", true);
        startActivity(intent);
        finish();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(AppointmentViewActivity.this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    private void clickToCall(String toNumber) {

        if (ApplicationSettings.containsPref(AppConstants.DISALLOW_HEADPHONES)) {
            boolean disallowHeadphones = ApplicationSettings.getPref(AppConstants.DISALLOW_HEADPHONES, false);
            if (disallowHeadphones) {

            } else {
                if (!SmarterSMBApplication.isHeadPhone) {
                    if (!isBluetoothHeadsetConnected()) {
                        showDialog(this);
                        return;
                    }
                }
            }
        } else {
            if (!SmarterSMBApplication.isHeadPhone) {
                if (!isBluetoothHeadsetConnected()) {
                    showDialog(this);
                    return;
                }
            }
        }

        clearFlags();

        if (SmarterSMBApplication.isRemoteDialledStart || SmarterSMBApplication.currentStateIsStartMode) {
            SmarterSMBApplication.outgoingCallNotInStartMode = false;
        } else {
            SmarterSMBApplication.outgoingCallNotInStartMode = true;
        }

        SmarterSMBApplication.isDiallingFollowUpC2C = true;
        NotificationData.isSocketResponse = false;
        ServiceHandler.callDisconnected = false;
        SmarterSMBApplication.callEndedFromDuringCall = false;
        SmarterSMBApplication.navigatingToUearnHome = false;
        SmarterSMBApplication.actionMoveToNormalProcessed = false;
        navigateToUearnActivity();
        SmarterSMBApplication.isC2CAutoStart = true;
        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
        ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
        ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, "");
        ApplicationSettings.putPref(AppConstants.MMT_STATUS, "");
        ApplicationSettings.putPref("SUBJECT", "");
        UearnActivity.redialScenario = false;
        NotificationData.dialledCustomerNumber = "";
        NotificationData.customerFeedback = "";
        NotificationData.updatedCustomKVS = "";
        if (SmarterSMBApplication.connectedCustomerInProcess) {
            SmarterSMBApplication.connectedCustomerInProcess = false;
        }
        if (SmarterSMBApplication.disconnectedCustomerInProcess) {
            SmarterSMBApplication.disconnectedCustomerInProcess = false;
        }

        if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
            boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
            if (adhocCall) {
                String questionnaire = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE_FROM_SETTINGS, "");
                ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
            }
        }

        NotificationData.statusString = statusString;

        if (CommonUtils.isNetworkAvailable(this)) {
            if (toNumber != null) {
                if (toNumber != null) {
                    CommonUtils.setToast(this, "Dialling ...");
                    CommonUtils.createSbiMakeCall(this, toNumber, id, appointmentId, statusString, substatus1, substatus2, callername, "");
                }
            }
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    void showDialog(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.alert_dialog, null,false);

        Button cancelBtn = contentView.findViewById(R.id.cancelBtn);
        Typeface titlef = Typeface.createFromAsset(context.getAssets(),
                "fonts/SF-UI-Display-Semibold.ttf");
        TextView hints1Tv = contentView.findViewById(R.id.hints1Tv);
        hints1Tv.setTypeface(titlef);
        TextView hints2Tv = contentView.findViewById(R.id.hints2Tv);

        Typeface titleFace = Typeface.createFromAsset(context.getAssets(),
                "fonts/SF-UI-Display-Semibold.ttf");
        cancelBtn.setTypeface(titleFace);

        alertDialog = new android.app.AlertDialog.Builder(context)
                .setView(contentView)
                .create();

        alertDialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
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
    }

    private void navigateToUearnActivity() {
        Intent intent = new Intent(this, UearnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("showinprogress", 1);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        getCalendarEntryInfo.setAppointment_id(appointmentId);
        if (SmarterSMBApplication.SmartUser != null)
            getCalendarEntryInfo.setUser_id(SmarterSMBApplication.SmartUser.getId());
        if (id == R.id.callerNumberLayout) {
            clickToCall(callerNumber1);
        } else if (id == R.id.next_appointmentLayout) {

            Intent intent = new Intent(this, AppointmentViewActivity.class);
            if (nextAppointment_dbId != 0) {
                intent.putExtra("id", nextAppointment_dbId);
            }
            startActivity(intent);
        } else if (id == R.id.acceptButton) {
            //Log.d("AcceptAll", "" + dbid);
            if (dbid != 0) {
                ContentValues cv = new ContentValues();
                cv.put("APPOINTMENT_TYPE", "accept_appointment");
                cv.put("RESPONSE_STATUS", "accepted");
                cv.put("UPLOAD_STATUS", 0);
                db.update("remindertbNew", cv, "_id=" + dbid, null);
                Intent aIntent = new Intent(this, ReuploadService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(aIntent);
                } else {
                    startService(aIntent);
                }
                finish();
            }
        } else if (id == R.id.tv_notes_image) {
            if (mNotesImageUrl != null) {
                //Log.d("Media Image","Image URL:"+mNotesImageUrl);
                loadDataFromUrl(mNotesImageUrl);
            }
        } else if (id == R.id.tv_notes_audio) {
            if (mNotesAudioUrl != null) {
                //Log.d("Media Audio","Audio URL:"+mNotesAudioUrl);
                loadDataFromUrl(mNotesAudioUrl, "audio");
            }
        } else if (id == R.id.tv_notes_text) {
            wvNotes.setVisibility(View.GONE);
            notesTextView.setVisibility(View.VISIBLE);
        }

        /* if(id == R.id.acceptAppointmentButton) {
            //TODO: add status that it is accepted to getCalendarEntryInfo
           getCalendarEntryInfo.setResponsestatus("true");
            acceptOrRejectCalendar(getCalendarEntryInfo, "Accepting Appointment");
        }
        if(id == R.id.rejectAppointmentButton) {
            //TODO: add status that it is rejected to getCalendarEntryInfo
            getCalendarEntryInfo.setResponsestatus("false");
            acceptOrRejectCalendar(getCalendarEntryInfo, "Rejecting Appointment");
        }*/
    }

    private void getSelectedCustomer(String number) {

        try {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            dbase.enableWriteAheadLogging();

            String selection = "TO1=" + "'" + number + "'";
            Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");

            customerList = new ArrayList<>();
            tempcustomerList = new ArrayList<>();
            if(cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
                    String customerName = cursor.getString(cursor.getColumnIndex("TONAME"));
                    String customerNumber = cursor.getString(cursor.getColumnIndex("TO1"));
                    String status = cursor.getString(cursor.getColumnIndex("STATUS"));
                    String substatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
                    String substatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
                    String notes = cursor.getString(cursor.getColumnIndex("NOTES"));
                    NotificationData.statusString = status;
                    String customkvs = cursor.getString(cursor.getColumnIndex("CUSTOMKVS"));
                    CustomerLite customerLiteInfo = new CustomerLite(itemId, customerName, customerNumber, status, substatus1, substatus2, "","",notes, "", customkvs);
                    customerList.add(customerLiteInfo);
                }
            }
            CustomerLite customerLiteInfo = customerList.get(0);
            tempcustomerList.add(0, customerLiteInfo);
            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
            String customerListData = new Gson().toJson(tempcustomerList);
            ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData);
            System.out.print(customerListData);

            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sbiCall(KnowlarityModel knowlarityModel, final String customernumber) {
        NotificationData.knolarity_start_time = new Date().toString();
        /*new APIProvider.KnowlarityClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {*/
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                NotificationData.knolarity_response_time = new Date().toString();
                if (data != null && !(data.isEmpty())) {
                    if (data.contains("_SMBALERT_")) {
                        CommonUtils.setToast(getApplicationContext(),data.replace("_SMBALERT_", ""));
                        AppointmentViewActivity.this.finish();
                    } else if (data.contains("_SMBACP_")) {
                        String uuidValue = data.replace("_SMBACP_", "");
                        try {
                            NotificationData.transactionId = uuidValue;
                            NotificationData.uuid = uuidValue;
                            NotificationData.knolarity_response = "success : ";
                            setCallData();
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        NotificationData.dialledCustomerNumber = customernumber;
                        if(SmarterSMBApplication.outgoingCallNotInStartMode) {
                            NotificationData.outboundDialledCustomerNumber = customernumber;
                        }
                        CommonUtils.storeUuidHash(customernumber,uuidValue);
                        CommonUtils.setToast(getApplicationContext(), "Connecting to the customer. Please wait ...");
                        CommonUtils.showACPScreen(getApplicationContext());
                        AppointmentViewActivity.this.finish();
                    } else {
                        try {
                            NotificationData.transactionId = data;
                            NotificationData.uuid = data;
                            NotificationData.knolarity_response = "success : ";
                            setCallData();
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        NotificationData.dialledCustomerNumber = customernumber;
                        if(SmarterSMBApplication.outgoingCallNotInStartMode) {
                            NotificationData.outboundDialledCustomerNumber = customernumber;
                        }
                        CommonUtils.storeUuidHash(customernumber,data);
                        CommonUtils.setToast(getApplicationContext(), "Connecting to the customer. Please wait ...");
                        AppointmentViewActivity.this.finish();
                    }

                } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                    CommonUtils.setToast(getApplicationContext(), "Invalid Auth key");
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.INVALID_REQUEST) {
                    CommonUtils.setToast(getApplicationContext(), "Request Not Allowed");
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                    CommonUtils.setToast(getApplicationContext(), "Invalid Parameters");
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.INVALID_NUMBER) {
                    markFollowupAsCompleated(1);
                    createCmail(callerNumber.getText().toString(), 1);
                    CommonUtils.setToast(getApplicationContext(), "This number is not valid");
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.SERVER_ALERT) {
                    CommonUtils.setToast(getApplicationContext(), APIProvider.SERVER_ALERT_MESSAGE);
                } else if (failure_code == APIProvider.DND) {
                    NotificationData.knolarity_response = "DND : ";
                    markFollowupAsCompleated(0);
                    createCmail(callerNumber.getText().toString(), 0);
                    Intent intent = new Intent(AppointmentViewActivity.this, ReuploadService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                    CommonUtils.setToast(getApplicationContext(), "DND Registered Number..");
                    /*Toast.makeText(getApplicationContext(), "DND Registered Number..", Toast.LENGTH_LONG).show();*/
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                    NotificationData.knolarity_response = "agent not verified : ";
                    CommonUtils.setToast(getApplicationContext(), "Hi. Your mobile number in Not verified.");
                    /*Toast.makeText(getApplicationContext(), "Call Failed! Agent not verified, Agent has to call on SR number for verification.", Toast.LENGTH_LONG).show();*/
                    //CustomSingleButtonDialog.buildSingleButtonDialog("Error", "Call Failed! Agent not verified, Agent has to call on SR number for verification.", AppointmentViewActivity.this, true);
                } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                    NotificationData.knolarity_response = "sr not registered : ";
                    CommonUtils.setToast(getApplicationContext(), "Hey! Ask your admin to call Support at 9113907215.");
                } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                    NotificationData.knolarity_response = "agent not registered : ";
                    CommonUtils.setToast(getApplicationContext(), "Number not registered for outbound services for this agent");
                } else {
                    NotificationData.knolarity_response = "request failed : ";
                    CommonUtils.setToast(getApplicationContext(), "Please check your internet connection.");
                }
            }
            /* }).call();*/
        }).reClickToCall(knowlarityModel);

        Intent main = new Intent(this, UearnHome.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //main.putExtra("knowlarity", true);
        startActivity(main);
        CommonUtils.setToast(this, "Dialling ...");
        /*Toast toastMessage = Toast.makeText(this, "Dialling ...", Toast.LENGTH_LONG);
        toastMessage.setGravity(Gravity.CENTER, 0, 0);
        toastMessage.show();*/
        this.finish();
    }

    private void reCall(KnowlarityModel knowlarityModel, final String customernumber) {
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                if (data != null && !(data.isEmpty())) {
                    if (data.contains("_SMBALERT_")) {
                        CommonUtils.setToast(getApplicationContext(),data.replace("_SMBALERT_", ""));
                        AppointmentViewActivity.this.finish();
                    } else if (data.contains("_SMBACP_")) {
                        try {
                            setCallData();
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                            NotificationData.knolarity_start_time = new Date().toString();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Connecting to the customer. Please wait ...", Toast.LENGTH_LONG).show();
                        AppointmentViewActivity.this.finish();
                        CommonUtils.showACPScreen(getApplicationContext());
                    } else {
                        try {
                            setCallData();
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                            NotificationData.knolarity_start_time = new Date().toString();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Connecting to the customer. Please wait ...", Toast.LENGTH_LONG).show();
                        AppointmentViewActivity.this.finish();
                    }

                } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                    CommonUtils.setToast(getApplicationContext(), "Invalid Auth key");
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.INVALID_REQUEST) {
                    CommonUtils.setToast(getApplicationContext(), "Request Not Allowed");
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                    CommonUtils.setToast(getApplicationContext(), "Invalid Parameters");
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.INVALID_NUMBER) {
                    markFollowupAsCompleated(1);
                    createCmail(callerNumber.getText().toString(), 1);
                    CommonUtils.setToast(getApplicationContext(), "This number is not valid");
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.SERVER_ALERT) {
                    CommonUtils.setToast(getApplicationContext(), APIProvider.SERVER_ALERT_MESSAGE);
                } else if (failure_code == APIProvider.DND) {
                    markFollowupAsCompleated(0);
                    createCmail(callerNumber.getText().toString(), 0);
                    Intent intent = new Intent(AppointmentViewActivity.this, ReuploadService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                    Toast.makeText(getApplicationContext(), "This number is NDNC.", Toast.LENGTH_LONG).show();
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                    Toast.makeText(getApplicationContext(), "Agent_number is in NDNC.", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                    Toast.makeText(getApplicationContext(), "No active integration found.", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                    Toast.makeText(getApplicationContext(), "Number not registered for outbound services for this agent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
                }

            }
        }).reClickToCall(knowlarityModel);

        Intent main = new Intent(this, UearnHome.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //main.putExtra("knowlarity", true);
        startActivity(main);
        Toast toastMessage = Toast.makeText(this, "Dialling", Toast.LENGTH_LONG);
        toastMessage.setGravity(Gravity.CENTER, 0, 0);
        toastMessage.show();
        this.finish();
    }

    private void loadDataFromUrl(String url) {
        String r = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0,target-densityDpi=device-dpi\">";

        String s = "<html><body style=\"margin: 0; padding: 0\"><IMG  width=\"100%\" height=\"100%\" src=\"" + url + "\"><body><html>";

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.image_dailog,
                (ViewGroup) findViewById(R.id.layout_root));

        WebView wvNotes = layout.findViewById(R.id.fullimage);
        wvNotes.getSettings().setJavaScriptEnabled(true);

        wvNotes.setWebViewClient(new WebViewClient());

        // This might not work if its a child
        wvNotes.setVerticalScrollBarEnabled(true);
        wvNotes.setHorizontalScrollBarEnabled(true);
        //wvNotes.loadUrl(url);
        wvNotes.loadData(s, "text/html", "UTF-8");
        wvNotes.getSettings().setUseWideViewPort(true);
        wvNotes.getSettings().setLoadWithOverviewMode(true);

        imageDialog.setView(layout);
        imageDialog.setPositiveButton(
                "OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        imageDialog.create();
        imageDialog.show();
    }

    private void loadDataFromUrl(String url, String audio) {
        wvNotes.setVisibility(View.VISIBLE);
        wvNotes.getSettings().setJavaScriptEnabled(true);
        wvNotes.setWebViewClient(new WebViewClient());

        // This might not work if its a child
        wvNotes.setVerticalScrollBarEnabled(true);
        wvNotes.setHorizontalScrollBarEnabled(true);
        wvNotes.loadUrl(url);

        //notesTextView.setVisibility(View.GONE);
    }

    private void updateCalendar() {
        if (CommonUtils.isNetworkAvailable(this)) {
            GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
            getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
            getCalendarEntryInfo.appointment_id = appointmentId;
            getCalendarEntryInfo.responsestatus = "completed";
            getCalendarEntryInfo.update_all_fields = false;
            getCalendarEntryInfo.tat = 0;

            ContentValues values = new ContentValues();
            values.put("COMPLETED", 1);
            db.update("remindertbNew", values, "_id=" + dbid, null);

            new APIProvider.Update_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    //Log.i("ViewAppointment", "Update appointment Data in on complete is " + data);
                    //gotoAppointmentList();
                    //finish();

                }
            }).call();
        } else {
            saveInLocalDb();
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private void saveInLocalDb() {
        updateDB(0);
        Intent aIntent = new Intent(getApplicationContext(), ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(aIntent);
        } else {
            getApplicationContext().startService(aIntent);
        }
    }

    private void updateDB(int statusvalue) {
        ContentValues cv = new ContentValues();
        cv.put("APPOINTMENT_TYPE", "complete_appointment");
        cv.put("COMPLETED", 1);
        cv.put("UPLOAD_STATUS", statusvalue);
        db.update("remindertbNew", cv, "_id=" + dbid, null);

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private Long getNextAppointmentData(String number, Long current_startTime) {
        String callerNameOrNumber = number;
        String subjct = "meating";
        Long start_time = 0L;
        long nextAppointmentdbId = 0L;
        Date date = new Date();
        String selection = "START_TIME" + ">" + date.getTime() + " AND " + "COMPLETED='" + 0 + "'";
        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String customer_number = cursor.getString(cursor.getColumnIndex("TO1"));
                    if (PhoneNumberUtils.compare(customer_number, number)) {
                        start_time = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                        if (start_time > current_startTime) {
                            nextAppointmentdbId = cursor.getLong(cursor.getColumnIndex("_id"));
                            subjct = cursor.getString(cursor.getColumnIndex("SUBJECT"));

                            String toname = cursor.getString(cursor.getColumnIndex("TONAME"));
                            if (toname != null && !toname.equals("")) {
                                callerNameOrNumber = toname;
                            } else {
                                String toNumber = cursor.getString(cursor.getColumnIndex("TO1"));
                                callerNameOrNumber = toNumber;
                            }
                            if (dbid != 0) {
                                /*cursor.close();*/
                                break;
                            }
                        }
                    }
                    cursor.moveToNext();
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        String start_Time = CommonUtils.getDate(start_time, "dd/MM/yyyy hh:mm a");
        String[] seperated = start_Time.split(" ");

        if (nextAppointmentdbId != 0) {
            nextAppointmentLayout.setVisibility(View.VISIBLE);
            nextAppointmenttext.setText(callerNameOrNumber);
            nextAppointmentSubject.setText(subjct);
            if ((seperated[0] != null) && (seperated[1] != null) && (seperated[2] != null))
                nextAppointmentTime.setText(seperated[0] + "\n" + seperated[1] + " " + seperated[2]);
        }
        return nextAppointmentdbId;
    }

    public static String getTimeduration(long min) {

        if (min < 60) {
            return String.valueOf(min) + " min ";
        } else if (min >= 60) {
            long hours = min / 60;
            long minutes = min % 60;
            if (hours < 24) {
                if (minutes == 0) {
                    return String.valueOf(hours) + " Hrs ";
                } else {
                    return String.valueOf(hours) + " Hrs " + String.valueOf(minutes) + " min";
                }
            } else if (hours >= 24) {
                long days = hours / 24;
                long remain = hours % 24;
                if (remain == 0) {
                    return String.valueOf(days) + " days";
                } else {
                    return String.valueOf(days) + "+ days";
                }
            }
        }
        return "0";
    }

    private static String getOutput(Context context, String methodName, int slotId) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        String reflectionMethod = null;
        String output = null;
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            for (Method method : telephonyClass.getMethods()) {
                String name = method.getName();
                if (name.contains(methodName)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 1 && params[0].getName().equals("int")) {
                        reflectionMethod = name;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (reflectionMethod != null) {
            try {
                output = getOpByReflection(telephony, reflectionMethod, slotId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    private static String getOpByReflection(TelephonyManager telephony, String predictedMethodName, int slotID, boolean isPrivate) {

        //Log.i("Reflection", "Method: " + predictedMethodName + " " + slotID);
        String result = null;

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID;
            if (slotID != -1) {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName, parameter);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
                }
            } else {
                if (isPrivate) {
                    getSimID = telephonyClass.getDeclaredMethod(predictedMethodName);
                } else {
                    getSimID = telephonyClass.getMethod(predictedMethodName);
                }
            }

            Object ob_phone;
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            if (getSimID != null) {
                if (slotID != -1) {
                    ob_phone = getSimID.invoke(telephony, obParameter);
                } else {
                    ob_phone = getSimID.invoke(telephony);
                }

                if (ob_phone != null) {
                    result = ob_phone.toString();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //Log.i("Reflection", "Result: " + result);
        return result;
    }

}
