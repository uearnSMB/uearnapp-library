package smarter.uearn.money.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.RatingBar;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import smarter.uearn.money.R;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.dialogs.CustomAudioRecorderDialog;
import smarter.uearn.money.dialogs.CustomLogPicker;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.fragments.aftercall.AfterCallAssignFragment;
import smarter.uearn.money.fragments.aftercall.AfterCallNotesFragment;
import smarter.uearn.money.fragments.aftercall.CustomerDetailFragment;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.PersonalJunkContact;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.models.WorkOrderEntryInfo;
import smarter.uearn.money.services.DeleteEventService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.AzureMediaUploader;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.GPSTracker;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;

public class AfterCallActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAnalytics mFirebaseAnalytics;

    private double latitude = 0;
    private double longitude = 0;

    Spinner mSpinnerSalesStages, mSpinnerDuration, mSpinnerAlertBefore;
    TextView mNameOrNumber, mAutoFollowUpNext;
    LinearLayout mNotesClick, mNotesTalk, mNotesType;
    LinearLayout mNotALead, mJunkCall, mPersonalContact;
    LinearLayout mSendSms, mCallAgain, mAssignTo;
    TextView mCustomTime, mCustomDate, next_followupTv;
    ProgressBar progressBar;
    long currentTime = System.currentTimeMillis();
    Calendar mCalendar, mTodayCalendar;
    ArrayList<String> salesArrayList = new ArrayList<>();
    ArrayAdapter<String> salesAdapter;
    Long rnr_id = 0L;
    long localdbId = 0;
    private int showInProgress = 0;

    private final static int LOAD_IMAGE = 101;
    LinearLayout llAfterCallLayout;

    public static int APPOINTMENT_SET = 11;
    public String customerNumber = "";
    private long dbid = 0L, reminderdbId = 0L;
    long nextAppointment_dbId = 0L;
    public String to, from;
    private String message, eventType, subject, callStartTime, callEndTime, callerName, cotactName = "", duraionOfACall = "", ameyocallStartTime = "";

    // Ensure to clear all these items while exiting the screen
    public final static String AFTER_CALL_NOTES = "afterCallNotes";
    public final static String AFTER_CALL_ASSIGN = "assignTo";
    public final static String AFTER_CALL_NAME = "after_call_name";
    public final static String AFTER_CALL_DESIGNATION = "after_call_designation";
    public final static String AFTER_CALL_PHONE = "after_call_phone";
    public final static String AFTER_CALL_COMPANY = "after_call_company";
    public final static String AFTER_CALL_EMAIL = "after_call_email";
    public final static String AFTER_CALL_LEAD = "after_call_lead";
    public final static String AFTER_CALL_ADDRESS = "after_call_address";
    public final static String CUSTOMER_TO_CONTACT = "customer_tocontact";

    private static final String TAG = AfterCallActivity.class.getSimpleName();
    private static String activityHeading = "";
    private String status, workOrderStatus;
    private boolean canUpload = true;
    private boolean notAlead = false;
    private String mydbAppointmentId = "";
    private int flpCount = 0, rnr_count = 0, rnr_callbacks = 0;
    private boolean cloudCheck = false;
    GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
    public int final_start_hour = 0, final_end_hour = 0, final_start_min = 0, final_end_min = 0, final_start_year, final_end_year, final_start_month, final_end_month, final_start_dayOfMonth, final_end_dayOfMonth;
    public long dbStart, dbEnd, dbTravelTime, nextAppointmentStartTime;
    public String dbSubject, dbNotes, dbLocation, dbDesignation, dbCompanyName, dbwebsiteString, db_appointmentId, dborderValue, dbemailIdString, dbcompany_Address, dbproduct_type, dblead_source, db_external_reference, dbstatusString;
    public String toNumber, callRec_url, toname;
    String appointmentId = null;
    boolean preAlarmSet = false; // Change this to true after setting the time
    private boolean call_status_update = false;
    private boolean nextfollowup = false;
    private boolean doNotCloseActivity = false;

    private ArrayList<String> private_contacts_array = new ArrayList<>();
    private int alarmBefore = 0;
    LinearLayout llCallAgain, layout5, formButtonLayout, endCallButtonLayout, isInterestedButtonsLayout;
    RelativeLayout btnTen, btnThirty, btnSixty;
    String imagePath, imageUrl = "";
    TextView errorText, call_rnr;
    private boolean autoCheck = false;
    private String callDateTime = "", possibleNumber = "";
    private int flpValue = 1;
    private boolean knowlarity = false;

    public static boolean onBackPressed = false;

    private LinearLayout ratingsLayout;
    private LinearLayout freeTextLayout;
    private LinearLayout dropdownLayout;
    private LinearLayout buttonsLayout;
    private LinearLayout isInterestedLayout;

    private Button nextButton;
    private ImageView endCallButton;
    private String currentSelectedScenario = "";
    private Spinner spinner;
    private Button yesButton, noButton, creditCardButton, debitCardButton, walletsButton, payTMButton, cashButton;
    private String formStatus = "NO";
    private String dropDownValueFromList = "";
    private String paymentModeFromButton = "";
    private TextView customerRatings;

    private LinkedHashMap questionnaireHash = new LinkedHashMap();
    private Hashtable dropDownHash = new Hashtable();
    private Hashtable buttonHash = new Hashtable();

    private TextView ratingsTextView;
    private TextView freeTextTextView;
    private TextView dropDownTextView;
    private TextView buttonsTextView;
    private TextView isInterestedTextView;

    private EditText customerExperience;

    private LinkedHashMap tempQnAHash = new LinkedHashMap();
    private LinkedHashMap questionAnswerHash = new LinkedHashMap();
    List<String> tempQuesList = new ArrayList<>();

    // CENTRAL
    JSONArray rootArrayData = null;
    private int questionCount = 0;

    String rootQ = "";
    String rootR = "";
    String dropDownData = "";

    private String currentRatings = "";
    private String extranote, leadsrc, wrapup, eventstartdate, transactionid;
    private RatingBar ratingsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_after_call);

        SmarterSMBApplication.currentActivity = this;

        changeStatusBarColor(this);
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        llAfterCallLayout = (LinearLayout) findViewById(R.id.llAfterCall);
        llAfterCallLayout.setVisibility(View.GONE);

        llCallAgain = (LinearLayout) findViewById(R.id.llCallAgain);
        llCallAgain.setVisibility(View.GONE);

        mNameOrNumber = (TextView) findViewById(R.id.tv_name_or_number);
        assert mNameOrNumber != null;
        mNameOrNumber.setOnClickListener(this);

        mAutoFollowUpNext = (TextView) findViewById(R.id.tv_auto_follow_count);
        call_rnr = (TextView) findViewById(R.id.call_rnr);

        mSpinnerSalesStages = (Spinner) findViewById(R.id.spinner_sales_stage);
        loadSalesStages();

        mSpinnerDuration = (Spinner) findViewById(R.id.s_duration);
        mSpinnerAlertBefore = (Spinner) findViewById(R.id.s_alert_before);
        loadDurationAndAlertBefore();

        mCustomDate = (TextView) findViewById(R.id.tv_custom_date);
        assert mCustomDate != null;
        mCustomDate.setOnClickListener(this);

        mCustomTime = (TextView) findViewById(R.id.tv_custom_time);
        next_followupTv = (TextView) findViewById(R.id.next_followup_textview);
        assert mCustomTime != null;
        mCustomTime.setOnClickListener(this);
        next_followupTv.setOnClickListener(this);

        mNotesClick = (LinearLayout) findViewById(R.id.ll_notes_click);
        assert mNotesClick != null;
        mNotesClick.setOnClickListener(this);

        mNotesTalk = (LinearLayout) findViewById(R.id.ll_notes_record);
        assert mNotesTalk != null;
        mNotesTalk.setOnClickListener(this);

        mNotesType = (LinearLayout) findViewById(R.id.ll_notes_type);
        assert mNotesType != null;
        mNotesType.setOnClickListener(this);

        mNotALead = (LinearLayout) findViewById(R.id.ll_not_a_lead);
        assert mNotALead != null;
        mNotALead.setOnClickListener(this);

        mJunkCall = (LinearLayout) findViewById(R.id.ll_junk_call);
        assert mJunkCall != null;
        mJunkCall.setOnClickListener(this);

        mPersonalContact = (LinearLayout) findViewById(R.id.ll_personal_contact);
        assert mPersonalContact != null;
        mPersonalContact.setOnClickListener(this);

        mSendSms = (LinearLayout) findViewById(R.id.ll_send_sms);
        assert mSendSms != null;
        mSendSms.setOnClickListener(this);

        mCallAgain = (LinearLayout) findViewById(R.id.ll_call_again);
        assert mCallAgain != null;
        mCallAgain.setOnClickListener(this);

        mAssignTo = (LinearLayout) findViewById(R.id.ll_assign_to);
        assert mAssignTo != null;
        mAssignTo.setOnClickListener(this);

        layout5 = (LinearLayout) findViewById(R.id.layout5);
        layout5.setVisibility(View.VISIBLE);

        formButtonLayout = (LinearLayout) findViewById(R.id.formButtonLayout);
        formButtonLayout.setVisibility(View.VISIBLE);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        endCallButtonLayout = (LinearLayout) findViewById(R.id.endCallButtonLayout);
        endCallButtonLayout.setVisibility(View.VISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        ratingsView=findViewById(R.id.ratingsBar);

        ratingsLayout = (LinearLayout) findViewById(R.id.ratingsLayout);
        freeTextLayout = (LinearLayout) findViewById(R.id.freeTextLayout);
        dropdownLayout = (LinearLayout) findViewById(R.id.dropDownLayout);
        buttonsLayout = (LinearLayout) findViewById(R.id.buttonsLayout);
        isInterestedLayout = (LinearLayout) findViewById(R.id.isInterestedLayout);
        isInterestedButtonsLayout = (LinearLayout) findViewById(R.id.isInterestedButtonsLayout);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        endCallButton = (ImageView) findViewById(R.id.endCallButton);
        endCallButton.setOnClickListener(this);

        customerRatings = (TextView) findViewById(R.id.title_normal);

        ratingsTextView = (TextView) findViewById(R.id.ratingsTextView);
        freeTextTextView = (TextView) findViewById(R.id.freeTextTextView);
        dropDownTextView = (TextView) findViewById(R.id.dropDownTextView);
        buttonsTextView = (TextView) findViewById(R.id.buttonsTextView);
        isInterestedTextView = (TextView) findViewById(R.id.isInterestedTextView);

        customerExperience = (EditText) findViewById(R.id.customerExperience);

        yesButton = (Button) findViewById(R.id.yes_button);
        noButton = (Button) findViewById(R.id.no_button);

        creditCardButton = (Button) findViewById(R.id.creditCard_button);
        debitCardButton = (Button) findViewById(R.id.debitCard_button);
        walletsButton = (Button) findViewById(R.id.wallets_button);
        payTMButton = (Button) findViewById(R.id.paytm_button);
        cashButton = (Button) findViewById(R.id.cash_button);

        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);

        creditCardButton.setOnClickListener(this);
        debitCardButton.setOnClickListener(this);
        walletsButton.setOnClickListener(this);
        payTMButton.setOnClickListener(this);
        cashButton.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        mCalendar = Calendar.getInstance();
        mTodayCalendar = Calendar.getInstance();

        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        mCustomDate.setText("Today");
        llCallAgain.setVisibility(View.VISIBLE);

        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute + 1);

        mTodayCalendar = mCalendar;

        String cmeridian = "";

        if (mCalendar.getTime().getTime() > currentTime) {
            switch (mCalendar.get(Calendar.AM_PM)) {
                case Calendar.AM:
                    cmeridian = "AM";
                    break;
                case Calendar.PM:
                    cmeridian = "PM";
                    break;
            }
        }

        int hourTw = hour;

        if (hourTw > 12) {
            hourTw -= 12;
        }

        if (minute < 10) {
            mCustomTime.setText("" + hourTw + ":0" + minute + " " + cmeridian);
        } else {
            mCustomTime.setText("" + hourTw + ":" + minute + " " + cmeridian);
        }
        alarmBefore = 10;

        boolean gpsSettings = ApplicationSettings.getPref(AppConstants.GPS_SETTINGS, false);
        if (gpsSettings) {
            getLatLong();
        }

        if (NotificationData.notificationData) {
            if (NotificationData.isWorkOrder) {
                if (NotificationData.statusString != null) {
                    workOrderStatus = NotificationData.statusString;
                }
            }

            if (NotificationData.notes_string != null) {
                if (NotificationData.notes_string.equalsIgnoreCase("null")) {
                    ApplicationSettings.putPref(AFTER_CALL_NOTES, " ");
                } else {
                    ApplicationSettings.putPref(AFTER_CALL_NOTES, NotificationData.notes_string);
                }
            }

            if (NotificationData.appointment_db_id != 0) {
                reminderdbId = NotificationData.appointment_db_id;
                getDetailsFromDb(String.valueOf(reminderdbId));
            }
        }

        getDetailsFromDb();
        loadPrivateContacts();
        setLayoutHeader();

        ApplicationSettings.putPref(AFTER_CALL_PHONE, customerNumber);

        if (customerNumber != null && !(customerNumber.isEmpty())) {
            nextAppointment_dbId = getNextAppointmentData(customerNumber);
            if (nextAppointmentStartTime != 0) {
                String start_Time = CommonUtils.getDate(nextAppointmentStartTime, "dd/MM/yyyy hh:mm a");
                String[] seperated = start_Time.split(" ");
                next_followupTv.setText("set at " + seperated[0] + "\n" + "\t" + seperated[1] + " " + seperated[2]);
            }

        }

        Resources resources = getResources();
        final Drawable arrow = resources.getDrawable(R.drawable.ic_chevron_right_black_24dp);

        if (customerNumber != null) {

            getLastUpdatedSalesStage();

            if (checkForCloudTelephony(customerNumber)) {
                if (CommonUtils.isNetworkAvailable(AfterCallActivity.this)) {
                    //Cloud Telephony Added By Srinath.k
                    //TODO: Check the Subject every Time
                    if (subject != null) {
                        if (subject.equals("Incoming Call") || eventType.equals("call_in_rec_on")) {
                            String didNumber = ApplicationSettings.getPref(AppConstants.CLOUD_TELEPHONY, "");
                            String strArray[] = didNumber.split(",");
                            if (strArray[0] != null && callStartTime != null) {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                TimeZone tz = TimeZone.getTimeZone("UTC");
                                df.setTimeZone(tz);
                                Date calldate = new Date();
                                String endTimeStamp = "";
                                try {
                                    calldate = df.parse(callStartTime);
                                    if (callEndTime != null) {
                                        endTimeStamp = CommonUtils.getTimeStamp(callEndTime);
                                    }
                                    if (endTimeStamp == null) {
                                        endTimeStamp = "";
                                    }
                                } catch (Exception e) {
                                    //Log.e("SMSComposeActivity", e.toString());
                                }

                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                                format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                                String timeStamp = format.format(calldate);
                                timeStamp = timeStamp + "Z";

                                String[] numbers = {strArray[0], timeStamp, endTimeStamp};
                                new APIProvider.IvrMappingUpload(numbers, 22, new API_Response_Listener<String>() {
                                    @Override
                                    public void onComplete(String data, long request_code, int failure_code) {

                                        mNameOrNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null);
                                        progressBar.setVisibility(View.GONE);
                                        if (data != null) {
                                            mNameOrNumber.setText(data);
                                            customerNumber = data;
                                            if (customerNumber != null && !(customerNumber.isEmpty())) {
                                                MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
                                                SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("TO1", customerNumber);
                                                if (dbid != 0) {
                                                    db1.update("ameyo_entries", contentValues, "_id=" + dbid, null);
                                                }
                                                db1.close();
                                            }
                                        }
                                        getPreviousNotes(customerNumber);
                                        updateAppointmentToCompletedStatus();
                                    }
                                }).call();
                            }
                        }
                    }
                } else {
                    mNameOrNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null);
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                mNameOrNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null);
                progressBar.setVisibility(View.GONE);
            }
        } else {
            mNameOrNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null);
            progressBar.setVisibility(View.GONE);
        }

        btnTen = (RelativeLayout) findViewById(R.id.btn_ten);
        btnTen.setOnClickListener(this);

        btnThirty = (RelativeLayout) findViewById(R.id.btn_thirty);
        btnThirty.setOnClickListener(this);

        btnSixty = (RelativeLayout) findViewById(R.id.btn_sixty);
        btnSixty.setOnClickListener(this);

        if (showInProgress == 1) {
            handleQuestionnaireAndACP();
        } else {
            errorText = (TextView) findViewById(R.id.tv_number_error);
            final AlertDialog alertDialog = new CustomLogPicker().buildChangePasswordDialog(this, this);

            errorText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            logAnalytics("error_no_number_logs", "TextView");
                            customerNumber = CustomLogPicker.selectedNumber;
                            if (customerNumber.isEmpty()) {
                                Toast.makeText(AfterCallActivity.this, "You have still not selected a number", Toast.LENGTH_SHORT).show();
                            } else {
                                ApplicationSettings.putPref(AFTER_CALL_PHONE, customerNumber);
                                setLayoutHeader();
                                mNameOrNumber.setText(customerNumber);
                                getLastUpdatedSalesStage();
                                errorText.setVisibility(View.GONE);
                            }
                        }
                    });
                    alertDialog.show();
                }
            });

            if (customerNumber == null || customerNumber.isEmpty()) {
                errorText.setVisibility(View.VISIBLE);
                showDailogForCumstomerNumber(alertDialog);
            }
        }
        String name = ApplicationSettings.getPref(AfterCallActivity.AFTER_CALL_NAME, "");
        if (name != null && !(name.isEmpty())) {
            mNameOrNumber.setText(name);
        }

        rnr_count = getRNRCount();
        int rnr = rnr_count + 1;
        call_rnr.setText("RNR #" + rnr);
        if (getIntent().hasExtra("knowlarityNumber")) {
            knowlarity = true;
        }
        NotificationData.privateCall = false;
    }

    private void updateMytbl(int value) {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (value == 0) {
            if (eventType != null && eventType.equalsIgnoreCase("call_in_rec_off") || eventType.equalsIgnoreCase("call_in_rec_on")) {
                cv.put("EVENT_TYPE", "call_in_rnr");
            } else {
                cv.put("EVENT_TYPE", "call_out_rnr");
            }
        }
        if (from != null && !(from.isEmpty()) && to != null && !(to.isEmpty())) {
            cv.put("FROM1", to);
            cv.put("TO1", from);
            cv.put("SUBJECT", "Outgoing Call");
            cv.put("MESSAGE", "Outgoing Call");
        }
        if (dbid != 0) {
            db1.update("mytbl", cv, "_id=" + dbid, null);
        }
        if (db1.isOpen()) {
            db1.close();
        }
    }

    private void checkRNR() {
        if (customerNumber != null && (!customerNumber.isEmpty())) {
            if (!chechFirstCall(customerNumber) || (flpValue == 0)) {
                if (duraionOfACall != null && duraionOfACall.equalsIgnoreCase("0")) {
                    if (salesAdapter != null && !salesAdapter.isEmpty()) {
                        int position = salesAdapter.getPosition("Not interested");
                        if (position > 0) {
                            mSpinnerSalesStages.setSelection(position);
                        }
                    }
                }
            }
        }
    }

    private void getLastUpdatedSalesStage() {
        String lateststatus = CommonUtils.getStatusFromSmartContactTable(this, customerNumber);
        mSpinnerSalesStages.setAdapter(salesAdapter);
        if (lateststatus != null && !(lateststatus.equals(null))) {
            int spinnerPosition = salesAdapter.getPosition(lateststatus);
            mSpinnerSalesStages.setSelection(spinnerPosition);
        } else {
            mSpinnerSalesStages.setSelection(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void loadPrivateContacts() {

        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        Cursor cursor = db1.rawQuery("SELECT * FROM PrivateContacts", null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String number = cursor.getString(cursor.getColumnIndex("NUMBER"));
                    private_contacts_array.add(number);
                }
            } finally {
                cursor.close();
            }
        }
        db1.close();
    }

    private void addPersonalToServer(String number) {
        PersonalJunkContact personalContact = new PersonalJunkContact(0, number);

        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.AddPersonalNumberApi(personalContact, 13, null, "Adding number", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {

                    }
                }
            }).call();
        }
    }

    private void addJunkToServer(String number) {
        PersonalJunkContact personalContact = new PersonalJunkContact(number, 0);

        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.AddJunkNumberApi(personalContact, 13, null, "", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {

                    }
                }
            }).call();
        }
    }

    private void savePrivateContacts(String number) {

        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        if (number != null && !(number.isEmpty())) {
            Cursor cursor = db1.rawQuery("SELECT * FROM PrivateContacts where NUMBER=" + "'" + number + "'", null);
            if (!(cursor != null && cursor.getCount() > 0)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("NUMBER", number);
                db1.insert("PrivateContacts", null, contentValues);

                addPrivateContact(number);
            }
        }
        db1.close();
    }

    private void addPrivateContact(String number) {
        PersonalJunkContact personalContact = new PersonalJunkContact(0, number);

        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.AddPersonalNumberApi(personalContact, 22, null, "Adding number", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {

                    }
                }
            }).call();
        }
    }

    private void saveJunkNumbers(String number) {

        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        if (number != null && !(number.isEmpty())) {
            Cursor cursor = db1.rawQuery("SELECT * FROM JunkNumbers where NUMBER=" + "'" + number + "'", null);
            if (!(cursor != null && cursor.getCount() > 0)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("NUMBER", number);
                contentValues.put("BLOCK_CALL", 0);
                db1.insert("JunkNumbers", null, contentValues);
            }

        }
        db1.close();
    }

    public void setLayoutHeader() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (customerNumber != null && !(customerNumber.isEmpty())) {
            Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where TO1=" + "'" + customerNumber + "'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    String followUpValue = cursor.getString(cursor.getColumnIndex("FLP_COUNT"));
                    if (followUpValue != null && !followUpValue.isEmpty()) {
                        int temp = Integer.parseInt(followUpValue);
                        if (temp > flpCount && (temp != 99)) {
                            flpCount = Integer.parseInt(followUpValue);
                        }
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
        } else if (reminderdbId != 0) {

            Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + reminderdbId + "'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                String followUpValue = cursor.getString(cursor.getColumnIndex("FLP_COUNT"));
                if (followUpValue != null && !followUpValue.isEmpty()) {
                    int count = Integer.parseInt(followUpValue);
                    if (count > flpCount && (count != 99)) {
                        flpCount = count;
                    }
                }
            }
            cursor.close();

        }

        if (db.isOpen()) {
            db.close();
        }

        dbHelper.close();

        if (flpCount == 0) {
            activityHeading = "First Call";
            mAutoFollowUpNext.setText("Next follow up #1");
            setDayTime(1);

            mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH) + 1);
            mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
            mCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));

            final_start_year = mCalendar.get(Calendar.YEAR);
            final_start_month = mCalendar.get(Calendar.MONTH);
            final_start_dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            final_end_year = mCalendar.get(Calendar.YEAR);
            final_end_month = mCalendar.get(Calendar.MONTH);
            final_end_dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            final_start_min = Calendar.getInstance().get(Calendar.MINUTE);
            final_start_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            final_end_min = Calendar.getInstance().get(Calendar.MINUTE) + 15;
            final_end_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (final_end_min >= 60) {
                final_end_min = final_end_min - 60;
                final_end_hour++;
            }

        } else {
            int flp = flpCount + 1;
            activityHeading = "Follow-up #" + flp;
            int nextCount = flpCount + 1;
            mAutoFollowUpNext.setText("Next follow up #" + nextCount);
            if (ApplicationSettings.getPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, false)) {

                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH);
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                int max = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) - 1;

                mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH) + 1);
                mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
                mCalendar.set(Calendar.YEAR, year);

                final_start_year = mCalendar.get(Calendar.YEAR);
                final_start_month = mCalendar.get(Calendar.MONTH);
                final_start_dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
                final_end_year = mCalendar.get(Calendar.YEAR);
                final_end_month = mCalendar.get(Calendar.MONTH);
                final_end_dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
                final_start_min = Calendar.getInstance().get(Calendar.MINUTE);
                final_start_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                final_end_min = Calendar.getInstance().get(Calendar.MINUTE) + 15;
                final_end_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                if (final_end_min >= 60) {
                    final_end_min = final_end_min - 60;
                    final_end_hour++;
                }
            }
        }

        restoreActionBar(activityHeading);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6ca7d9")));
    }

    public void getDetailsFromDb() {

        String ns = Context.NOTIFICATION_SERVICE;
        String filePath = "";
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("dbid") && intent.getLongExtra("dbid", 0) != 0) {
                dbid = intent.getLongExtra("dbid", 0);
            }
            if (intent.hasExtra("showinprogress")) {
                showInProgress = intent.getIntExtra("showinprogress", 0);
            }
            if (intent.hasExtra("notification_id")) {
                int id = intent.getIntExtra("notification_id", 1);
                mNotificationManager.cancel(id);
            }
        }

        if (dbid != 0) {
            String endTime = "";

            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (intent.hasExtra("cloudcall")) {
                Cursor cursor = db.rawQuery("SELECT * FROM ameyo_entries where _id=" + "'" + dbid + "'", null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    from = cursor.getString(cursor.getColumnIndex("FROM1"));
                    to = cursor.getString(cursor.getColumnIndex("TO1"));
                    eventType = cursor.getString(cursor.getColumnIndex("EVENT_TYPE"));
                    message = cursor.getString(cursor.getColumnIndex("MESSAGE"));
                    subject = cursor.getString(cursor.getColumnIndex("SUBJECT"));
                    callStartTime = cursor.getString(cursor.getColumnIndex("STARTTIME"));
                    ameyocallStartTime = callStartTime;
                    endTime = cursor.getString(cursor.getColumnIndex("ENDTIME"));
                    filePath = cursor.getString(cursor.getColumnIndex("FILE_PATH"));
                    if (endTime != null) {
                        callEndTime = endTime;
                    }
                    if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "").equals(from)) {
                        customerNumber = to;
                    } else {
                        customerNumber = from;
                    }
                }

                if (customerNumber != null) {
                    callerName = CommonUtils.getContactName(getApplicationContext(), customerNumber);
                    if (callerName != null && !callerName.equals("")) {
                        String temp = customerNumber;
                        customerNumber = temp;
                    }
                    getPreviousNotes(customerNumber);
                    updateAppointmentToCompletedStatus();
                }
                cursor.close();
            } else {
                Cursor cursor = db.rawQuery("SELECT * FROM mytbl where _id=" + "'" + dbid + "'", null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    from = cursor.getString(cursor.getColumnIndex("FROM1"));
                    to = cursor.getString(cursor.getColumnIndex("TO1"));
                    eventType = cursor.getString(cursor.getColumnIndex("EVENT_TYPE"));
                    message = cursor.getString(cursor.getColumnIndex("MESSAGE"));
                    subject = cursor.getString(cursor.getColumnIndex("SUBJECT"));
                    callStartTime = cursor.getString(cursor.getColumnIndex("STARTTIME"));
                    endTime = cursor.getString(cursor.getColumnIndex("ENDTIME"));
                    cotactName = cursor.getString(cursor.getColumnIndex("CALLER"));
                    filePath = cursor.getString(cursor.getColumnIndex("FILE_PATH"));
                    if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "").equals(from)) {
                        customerNumber = to;
                    } else {
                        customerNumber = from;
                    }
                }

                if (customerNumber != null) {
                    callerName = CommonUtils.getContactName(getApplicationContext(), customerNumber);
                    if (callerName != null && !callerName.equals("")) {
                        String temp = customerNumber;
                        customerNumber = temp;
                    }
                    getPreviousNotes(customerNumber);
                    updateAppointmentToCompletedStatus();
                }
                cursor.close();
            }

            ContentValues cv = new ContentValues();
            if (customerNumber == null || customerNumber.isEmpty()) {
                if (possibleNumber != null && !(possibleNumber.isEmpty())) {
                    customerNumber = possibleNumber;
                    cv.put("TO1", customerNumber);
                }
            }
            if (customerNumber != null && !customerNumber.isEmpty()) {
                duraionOfACall = getLastCallDuration(customerNumber);
                if (duraionOfACall != null && (duraionOfACall.equalsIgnoreCase("0") || duraionOfACall.isEmpty())) {
                    if (salesAdapter != null && !salesAdapter.isEmpty()) {
                        int position = salesAdapter.getPosition("RNR");
                        if (position > 0) {
                            mSpinnerSalesStages.setSelection(position);
                        }
                    }
                    call_rnr.setVisibility(View.VISIBLE);
                    if (filePath != null && !(filePath.isEmpty())) {
                        boolean check = deleteRecordFile(filePath);
                        if (check) {
                            cv.put("FILE_PATH", "");
                        }
                    }
                    cv.put("STARTTIME", endTime);
                    if (eventType != null && (eventType.equalsIgnoreCase("call_in_rec_off") || eventType.equalsIgnoreCase("call_in_rec_on"))) {
                        cv.put("EVENT_TYPE", "call_in_rnr");
                    } else {
                        cv.put("EVENT_TYPE", "call_out_rnr");
                    }
                    if (intent.hasExtra("cloudcall")) {

                    } else {
                        db.update("mytbl", cv, "_id=" + dbid, null);
                    }
                } else {
                    if (duraionOfACall != null && !(duraionOfACall.isEmpty())) {
                        call_rnr.setVisibility(View.GONE);
                        Long endInMillis = CommonUtils.stringToMilliSec(endTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        int sec = Integer.parseInt(duraionOfACall);
                        endInMillis = endInMillis - (sec * 1000);
                        ameyocallStartTime = CommonUtils.getTimeFormatInISO(new Date(endInMillis));
                        cv.put("STARTTIME", ameyocallStartTime);
                        if (intent.hasExtra("cloudcall")) {

                        } else {
                            db.update("mytbl", cv, "_id=" + dbid, null);
                        }
                    }
                }
            }

            if (db != null && db.isOpen()) {
                db.close();
            }
            if (dbHelper != null)
                dbHelper.close();
        }

        if (callerName != null && !callerName.equals("")) {
            mNameOrNumber.setText(callerName);
        } else if (customerNumber != null) {
            mNameOrNumber.setText(customerNumber);
        } else {
            Toast.makeText(this, "Customer number could not be captured", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPreviousNotes(String number) {
        String notes = CommonUtils.getNotesFromSmartContactTable(this, number);
        if (notes != null) {
            if (!notes.isEmpty()) {
                if (!notes.equalsIgnoreCase("null")) {
                    ApplicationSettings.putPref(AFTER_CALL_NOTES, notes);
                    return;
                }
            }
        }
        ApplicationSettings.putPref(AFTER_CALL_NOTES, " ");
    }

    private void loadSalesStages() {

        SalesStageInfo salesStageInfo = SmarterSMBApplication.salesStageInfo;
        if (salesStageInfo != null) {
            salesArrayList = salesStageInfo.getAppointmentSalesStage();

            if (salesArrayList == null) {
                salesArrayList = checkSalesList();
            } else if (salesArrayList.isEmpty()) {
                salesArrayList = checkSalesList();
            }
        } else {
            salesArrayList = checkSalesList();
            if (salesArrayList == null) {
                salesArrayList.add("Initial");
            } else if (salesArrayList.isEmpty()) {
                salesArrayList.add("Initial");
            }
        }

        salesAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_textview, salesArrayList);
        salesAdapter.setDropDownViewResource(R.layout.item_spinner_textview);
        mSpinnerSalesStages.setAdapter(salesAdapter);

        mSpinnerSalesStages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                status = salesArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean deleteRecordFile(String file) {
        File f = new File(file);
        if (f.exists() == true) {
            f.delete();
            return true;
        } else {
            return false;
        }
    }

    private void loadDurationAndAlertBefore() {
        final ArrayList<String> itemsDuration = new ArrayList<>();
        itemsDuration.add("15 min");
        itemsDuration.add("30 min");
        itemsDuration.add("60 min");

        final ArrayList<String> itemsAlarmBefore = new ArrayList<>();
        itemsAlarmBefore.add("0 min");
        itemsAlarmBefore.add("15 min");
        itemsAlarmBefore.add("30 min");
        itemsAlarmBefore.add("60 min");

        ArrayAdapter<String> durationAdapter1 = new ArrayAdapter<>(this, R.layout.item_spinner_textview, itemsDuration);
        durationAdapter1.setDropDownViewResource(R.layout.item_spinner_textview);
        mSpinnerDuration.setAdapter(durationAdapter1);

        mSpinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                int modifyMinute = mCalendar.get(Calendar.MINUTE);
                int modifyHour = mCalendar.get(Calendar.HOUR_OF_DAY);

                switch (position) {
                    case 0:
                        if (modifyMinute + 15 > 60) {
                            modifyMinute = (modifyMinute + 15) - 60;
                            modifyHour++;
                        } else {
                            modifyMinute += 15;
                        }
                        break;
                    case 1:
                        if (modifyMinute + 30 > 60) {
                            modifyMinute = (modifyMinute + 30) - 60;
                            modifyHour++;
                        } else {
                            modifyMinute += 30;
                        }
                        break;
                    case 2:
                        if (modifyHour + 60 == 60) {
                            modifyHour++;
                        } else {
                            modifyMinute = (modifyMinute + 60) - 60;
                            modifyHour++;
                        }
                        break;
                }

                final_end_hour = modifyHour;
                final_end_min = modifyMinute;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> durationAdapter2 = new ArrayAdapter<>(this, R.layout.item_spinner_textview, itemsAlarmBefore);
        durationAdapter2.setDropDownViewResource(R.layout.item_spinner_textview);
        mSpinnerAlertBefore.setAdapter(durationAdapter2);

        mSpinnerAlertBefore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        alarmBefore = 0;
                        break;
                    case 1:
                        alarmBefore = 10;
                        break;
                    case 2:
                        alarmBefore = 30;
                        break;
                    case 3:
                        alarmBefore = 60;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private ArrayList<String> checkSalesList() {
        ArrayList<String> arrayList1 = new ArrayList<>();

        String user_id = SmarterSMBApplication.SmartUser.getId();
        if (CommonUtils.isNetworkAvailable(AfterCallActivity.this)) {
            new APIProvider.Get_Sales_Status(user_id, 22, new API_Response_Listener<SalesStageInfo>() {
                @Override
                public void onComplete(SalesStageInfo data, long request_code, int failure_code) {
                    if (data != null) {
                        data.dosave();
                        SmarterSMBApplication.salesStageInfo = data;
                    }
                }
            }).call();
        }
        SalesStageInfo salesStageInfo = SmarterSMBApplication.salesStageInfo;
        if (salesStageInfo != null) {
            arrayList1 = salesStageInfo.getAppointmentSalesStage();
            if (arrayList1 == null) {
                arrayList1.add("Unknown");
            } else if (arrayList1.isEmpty()) {
                arrayList1.add("Unknown");
            }
        }
        return arrayList1;
    }

    private void updateAppointmentToCompletedStatus() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long startMls = 0;
        String appointment_id = null;
        int rnr = 0;
        if (reminderdbId != 0) {
            localdbId = reminderdbId;
            Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + reminderdbId + "'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                appointment_id = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
                mydbAppointmentId = appointment_id;
                startMls = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                String flpvalue = cursor.getString(cursor.getColumnIndex("FLP_COUNT"));
                rnr = cursor.getInt(cursor.getColumnIndex("RNR_COUNT"));
                if (flpvalue != null && !flpvalue.isEmpty()) {
                    flpCount = Integer.parseInt(flpvalue);
                }
            }
        } else {
            Date date = new Date();
            String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "COMPLETED='" + 0 + "'";
            Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String customer_number = cursor.getString(cursor.getColumnIndex("TO1"));
                    if (PhoneNumberUtils.compare(customer_number, customerNumber)) {
                        localdbId = cursor.getLong(cursor.getColumnIndex("_id"));
                        appointment_id = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
                        mydbAppointmentId = appointment_id;
                        startMls = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                        String flpvalue = cursor.getString(cursor.getColumnIndex("FLP_COUNT"));
                        rnr = cursor.getInt(cursor.getColumnIndex("RNR_COUNT"));
                        if (flpvalue != null && !flpvalue.isEmpty()) {
                            flpCount = Integer.parseInt(flpvalue);
                        }
                    }
                    cursor.moveToNext();
                }
            }
        }
        updateLocalDB(localdbId);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        Date calldate = new Date();
        try {
            calldate = df.parse(callStartTime);
        } catch (Exception e) {

        }
        long callTime = calldate.getTime();
        long tatValue = callTime - startMls;
        if (tatValue < 0) {
            //FOR UPCOMING APPOINTMENTS TAT IS 0
            tatValue = 0;
        }
        Integer tat = (int) (long) (tatValue);
        int tatInSec = (tat / 1000) % 60;
        getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
        getCalendarEntryInfo.appointment_id = appointment_id;
        getCalendarEntryInfo.responsestatus = "completed";
        getCalendarEntryInfo.update_all_fields = false;
        getCalendarEntryInfo.tat = tatInSec;
        getCalendarEntryInfo.flp_count = "" + flpCount;
        if (duraionOfACall != null && !(duraionOfACall.equalsIgnoreCase("0"))) {
            rnr = 0;
        }
        getCalendarEntryInfo.rnrCount = rnr;

        if (callStartTime != null && !(callStartTime.isEmpty())) {
            getCalendarEntryInfo.created_at = callStartTime;
        }
        if (db.isOpen()) {
            db.close();
        }
        dbHelper.close();
    }

    private Long getNextAppointmentData(String number) {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long nextAppointmentdbId = 0L;
        Date date = new Date();
        String selection = "START_TIME" + ">" + date.getTime() + " AND " + "COMPLETED='" + 0 + "'";
        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String customer_number = cursor.getString(cursor.getColumnIndex("TO1"));
                if (PhoneNumberUtils.compare(customer_number, number)) {
                    nextAppointmentdbId = cursor.getLong(cursor.getColumnIndex("_id"));
                    appointmentId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
                    nextAppointmentStartTime = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                }
                cursor.moveToNext();
            }
        }

        cursor.close();

        if (db.isOpen()) {
            db.close();
        }

        dbHelper.close();
        return nextAppointmentdbId;
    }

    public void resetYesNoButton() {
        yesButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
        yesButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
        noButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
        noButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
    }

    private void initSeekBar() {
        ratingsView.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                customerRatings.setText("Value:" + String.valueOf(rating));
            }
        });
    }
    private void createSpinner(String key) {

        final List<String> dropDownValues = new ArrayList<>();

        if (dropDownHash.containsKey(key)) {
            String dropDownData = dropDownHash.get(key).toString();
            if (dropDownData != null && !dropDownData.isEmpty()) {
                if (dropDownData.contains("[")) {
                    dropDownData = dropDownData.replace("[", "");
                }
                if (dropDownData.contains("]")) {
                    dropDownData = dropDownData.replace("]", "");
                }
            }
            String[] dropDownArray = dropDownData.split(",");
            for (int i = 0; i < dropDownArray.length; i++) {
                if (dropDownArray[i].contains("'")) {
                    dropDownArray[i] = dropDownArray[i].substring(1, dropDownArray[i].length() - 1);
                    if (dropDownArray[i].contains("'")) {
                        dropDownArray[i] = dropDownArray[i].substring(1, dropDownArray[i].length());
                    }
                    dropDownValues.add(dropDownArray[i]);
                }
            }
        }

        spinner = (Spinner) findViewById(R.id.activity_spinner1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dropDownValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String title = dropDownValues.get(i);
                dropDownValueFromList = title;
                currentSelectedScenario = title;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public boolean killCall(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
            methodGetITelephony.setAccessible(true);
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
            Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    private void updateHash() {

        String experience = customerExperience.getText().toString();
        String ratings = customerRatings.getText().toString();
        if (ratings != null && !ratings.isEmpty()) {
            if (ratings.contains("Please select")) {
                ratings = "0";
            } else {
                ratings = ratings.substring(ratings.lastIndexOf(":") + 1);
            }
            currentRatings = ratings;
        }

        String currentStatus = String.valueOf(formStatus);

        String payModeFromList = String.valueOf(dropDownValueFromList);
        dropDownValueFromList = "";

        String payModeFromButton = String.valueOf(paymentModeFromButton);

        if (tempQuesList != null && tempQuesList.size() > 0) {
            for (int k = 0; k < tempQuesList.size(); k++) {
                Set set = tempQnAHash.entrySet();
                Iterator i = set.iterator();
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    String value = "";
                    String key = (String) me.getKey();
                    if (tempQuesList.contains(key)) {
                        value = (String) me.getValue();
                    }

                    if (value != null && !value.isEmpty()) {
                        if (value.equalsIgnoreCase("RT")) {
                            if (!questionAnswerHash.containsKey(key)) {
                                questionAnswerHash.put(key, ratings);
                                customerRatings.setText("");
                            }
                        } else if (value.equalsIgnoreCase("FT")) {
                            if (!questionAnswerHash.containsKey(key)) {
                                questionAnswerHash.put(key, experience);
                                customerExperience.setText("");
                            }
                        } else if (value.equalsIgnoreCase("DD")) {
                            if (!questionAnswerHash.containsKey(key) && !questionAnswerHash.containsValue(payModeFromList))
                                questionAnswerHash.put(key, payModeFromList);
                        } else if (value.equalsIgnoreCase("BT")) {
                            if (!questionAnswerHash.containsKey(key) && !questionAnswerHash.containsValue(payModeFromButton))
                                questionAnswerHash.put(key, payModeFromButton);
                        } else if (value.equalsIgnoreCase("YN")) {
                            if (!questionAnswerHash.containsKey(key)) {
                                questionAnswerHash.put(key, currentStatus);
                            }
                        }
                    }
                }
            }
        }

        System.out.println(questionAnswerHash.toString());

        Set set = questionAnswerHash.entrySet();
        Iterator i = set.iterator();
        JSONObject bankFeedbackFormObj = null;
        JSONArray jsonArray = new JSONArray();
        while (i.hasNext()) {

            Map.Entry me = (Map.Entry) i.next();
            String question = (String) me.getKey();
            String answer = (String) me.getValue();

            bankFeedbackFormObj = new JSONObject();
            try {
                bankFeedbackFormObj.put("Q:", question);
                bankFeedbackFormObj.put("A:", answer);
                jsonArray.put(bankFeedbackFormObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject experienceObj = new JSONObject();
        try {
            experienceObj.put("experience", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(experienceObj);
        NotificationData.customerFeedback = experienceObj.toString();
    }

    private void updateLocalDB(long localDBId) {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("COMPLETED", 1);
        if (callStartTime != null && !(callStartTime.isEmpty())) {
            cv.put("CREATED_AT", callStartTime);
        }
        cv.put("RNR_COUNT", rnr_count);
        int no = db.update("remindertbNew", cv, "_id=" + localDBId, null);

        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null)
            dbHelper.close();
    }

    private void resetBackground() {
        btnTen.setBackgroundResource(R.drawable.editext_background);
        btnThirty.setBackgroundResource(R.drawable.editext_background);
        btnSixty.setBackgroundResource(R.drawable.editext_background);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.btn_ten:
                logAnalytics("Ten_Minutes", "TextView");

                resetBackground();
                autoCheck = true;
                btnTen.setBackgroundColor(getResources().getColor(R.color.smb_blue));

                preAlarmSet = true;
                mCalendar = Calendar.getInstance();

                int calculateMinute1 = Calendar.getInstance().get(Calendar.MINUTE) + 10;
                int calculateHour1 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                if (calculateMinute1 >= 60) {
                    final_start_min = calculateMinute1 - 60;
                    final_start_hour = calculateHour1 + 1;
                } else {
                    final_start_min = calculateMinute1;
                    final_start_hour = calculateHour1;
                }

                int calculateEndMinute1 = Calendar.getInstance().get(Calendar.MINUTE) + 25;

                if (calculateEndMinute1 >= 60) {
                    final_end_min = calculateEndMinute1 - 60;
                    final_end_hour = calculateHour1 + 1;
                } else {
                    final_end_min = calculateEndMinute1;
                    final_end_hour = calculateHour1;
                }

                Calendar cDtTm = Calendar.getInstance();

                final_start_year = cDtTm.get(Calendar.YEAR);
                final_start_month = cDtTm.get(Calendar.MONTH);
                final_start_dayOfMonth = cDtTm.get(Calendar.DAY_OF_MONTH);
                final_end_year = cDtTm.get(Calendar.YEAR);
                final_end_month = cDtTm.get(Calendar.MONTH);
                final_end_dayOfMonth = cDtTm.get(Calendar.DAY_OF_MONTH);
                Toast.makeText(this, "Alarm set for 10 min", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_thirty:
                logAnalytics("Thirty_Minutes", "TextView");

                resetBackground();
                autoCheck = true;
                btnThirty.setBackgroundColor(getResources().getColor(R.color.smb_blue));

                preAlarmSet = true;
                mCalendar = Calendar.getInstance();

                int calculateMinute2 = Calendar.getInstance().get(Calendar.MINUTE) + 30;
                int calculateHour2 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                if (calculateMinute2 >= 60) {
                    final_start_min = calculateMinute2 - 60;
                    final_start_hour = calculateHour2 + 1;
                } else {
                    final_start_min = calculateMinute2;
                    final_start_hour = calculateHour2;
                }

                int calculateEndMinute2 = Calendar.getInstance().get(Calendar.MINUTE) + 45;

                if (calculateEndMinute2 >= 60) {
                    final_end_min = calculateEndMinute2 - 60;
                    final_end_hour = calculateHour2 + 1;
                } else {
                    final_end_min = calculateEndMinute2;
                    final_end_hour = calculateHour2;
                }

                Calendar cDtTm1 = Calendar.getInstance();

                final_start_year = cDtTm1.get(Calendar.YEAR);
                final_start_month = cDtTm1.get(Calendar.MONTH);
                final_start_dayOfMonth = cDtTm1.get(Calendar.DAY_OF_MONTH);
                final_end_year = cDtTm1.get(Calendar.YEAR);
                final_end_month = cDtTm1.get(Calendar.MONTH);
                final_end_dayOfMonth = cDtTm1.get(Calendar.DAY_OF_MONTH);
                Toast.makeText(this, "Alarm set for 30 min", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_sixty:
                logAnalytics("Sixty_Minutes", "TextView");

                resetBackground();
                autoCheck = true;
                btnSixty.setBackgroundColor(getResources().getColor(R.color.smb_blue));

                preAlarmSet = true;
                mCalendar = Calendar.getInstance();

                int calculateMinute3 = Calendar.getInstance().get(Calendar.MINUTE) + 60;
                int calculateHour3 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                if (calculateMinute3 >= 60) {
                    final_start_min = calculateMinute3 - 60;
                    final_start_hour = calculateHour3 + 1;
                } else {
                    final_start_min = calculateMinute3;
                    final_start_hour = calculateHour3;
                }

                int calculateEndMinute3 = Calendar.getInstance().get(Calendar.MINUTE) + 75;

                if (calculateEndMinute3 >= 60) {
                    final_end_min = calculateEndMinute3 - 60;
                    final_end_hour = calculateHour3 + 1;

                    if (final_end_min >= 60) {
                        final_end_min = final_end_min - 60;
                        final_end_hour = calculateHour3 + 1;
                    }
                }

                Calendar cDtTm2 = Calendar.getInstance();

                final_start_year = cDtTm2.get(Calendar.YEAR);
                final_start_month = cDtTm2.get(Calendar.MONTH);
                final_start_dayOfMonth = cDtTm2.get(Calendar.DAY_OF_MONTH);
                final_end_year = cDtTm2.get(Calendar.YEAR);
                final_end_month = cDtTm2.get(Calendar.MONTH);
                final_end_dayOfMonth = cDtTm2.get(Calendar.DAY_OF_MONTH);
                Toast.makeText(this, "Alarm set for 60 min", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_name_or_number:
                logAnalytics("Customer_Details", "TextView");

                final AlertDialog alertDialog = new CustomLogPicker().buildChangePasswordDialog(this, this);

                if (customerNumber == null) {
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            customerNumber = CustomLogPicker.selectedNumber;
                            if (customerNumber.isEmpty()) {
                                Toast.makeText(AfterCallActivity.this, "You have still not selected a number", Toast.LENGTH_SHORT).show();
                            } else {
                                ApplicationSettings.putPref(AFTER_CALL_PHONE, customerNumber);
                                setLayoutHeader();
                                mNameOrNumber.setText(customerNumber);
                                errorText.setVisibility(View.GONE);
                                getLastUpdatedSalesStage();
                            }
                        }
                    });
                    alertDialog.show();
                } else if (customerNumber.isEmpty()) {
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            customerNumber = CustomLogPicker.selectedNumber;
                            if (customerNumber.isEmpty()) {
                                Toast.makeText(AfterCallActivity.this, "You have still not selected a number", Toast.LENGTH_SHORT).show();
                            } else {
                                ApplicationSettings.putPref(AFTER_CALL_PHONE, customerNumber);
                                setLayoutHeader();
                                mNameOrNumber.setText(customerNumber);
                                errorText.setVisibility(View.GONE);
                                getLastUpdatedSalesStage();
                            }
                        }
                    });
                    alertDialog.show();
                } else {
                    MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + String.valueOf(reminderdbId) + "'", null);
                    if (cursor != null) {
                        cursor.close();
                        db.close();
                        dbHelper.close();
                    }

                    llAfterCallLayout.setVisibility(View.GONE);
                    String paramNameNumber = "";

                    if (!callerName.isEmpty()) {
                        paramNameNumber = callerName;
                    } else if (!customerNumber.isEmpty()) {
                        paramNameNumber = customerNumber;
                    }
                    fragmentTransaction.add(R.id.fl_after_call_container, CustomerDetailFragment.newInstance(paramNameNumber, String.valueOf(reminderdbId), false));
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.ll_notes_click:
                logAnalytics("Notes_Image", "TextView");

                doNotCloseActivity = true;
                Random rand = new Random();
                imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/notesimage" + rand.nextInt() + ".JPEG"; // Previously set as JPG
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
                startActivityForResult(intent, LOAD_IMAGE);
                break;
            case R.id.ll_notes_record:
                logAnalytics("Notes_Audio_Recording", "TextView");

                new CustomAudioRecorderDialog().buildAudioRecorder(this, this);
                break;
            case R.id.ll_notes_type:
                logAnalytics("Notes_Text", "TextView");

                llAfterCallLayout.setVisibility(View.GONE);
                String notes = ApplicationSettings.getPref(AFTER_CALL_NOTES, " ");
                fragmentTransaction.add(R.id.fl_after_call_container, AfterCallNotesFragment.newInstance(notes));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.ll_not_a_lead:
                logAnalytics("Not_A_Lead", "TextView");

                notAlead = true;
                if (customerNumber != null) {
                    deleteFutureAppointments(customerNumber, "notAlead");
                    AfterCallActivity.this.finish();
                    Toast.makeText(this, "No more follow-ups set.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_junk_call:
                if (ApplicationSettings.containsPref(AppConstants.JUNKCALL_SETTING)) {
                    if (ApplicationSettings.getPref(AppConstants.JUNKCALL_SETTING, false)) {
                        logAnalytics("Junk_Call", "TextView");

                        canUpload = false;
                        if (customerNumber != null && (!customerNumber.isEmpty())) {
                            addJunkToServer(customerNumber);
                            saveJunkNumbers(customerNumber);
                            deleteFutureAppointments(customerNumber, "private");
                        }
                        AfterCallActivity.this.finish();
                        Toast.makeText(this, "Marked as junk call", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Junk contacts are disabled, Please Check Settings", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.ll_personal_contact:
                if (ApplicationSettings.containsPref(AppConstants.PERSONALCALL_SETTING)) {
                    if (ApplicationSettings.getPref(AppConstants.PERSONALCALL_SETTING, false)) {
                        logAnalytics("Personal_Call", "TextView");
                        canUpload = false;
                        if (customerNumber != null && (!customerNumber.isEmpty())) {
                            private_contacts_array.add(customerNumber);
                            addPersonalToServer(customerNumber);
                            savePrivateContacts(customerNumber);
                            deleteFutureAppointments(customerNumber, "private");
                            AfterCallActivity.this.finish();
                            Toast.makeText(this, "Added to personal contacts.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Please Check Settings", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Personal contacts are disabled, Please Check Settings", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ll_assign_to:
                logAnalytics("Assign_To", "TextView");
                MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor mCursor = db.rawQuery("SELECT * FROM TeamMembers", null);
                try {
                    if (mCursor != null && mCursor.getCount() > 0) {
                        llAfterCallLayout.setVisibility(View.GONE);
                        fragmentTransaction.add(R.id.fl_after_call_container, new AfterCallAssignFragment());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else {
                        Toast.makeText(this, "You do not have team", Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    mCursor.close();
                    db.close();
                }

                break;

            case R.id.nextButton:
                JSONObject jsonobject = null;
                updateHash();

                resetYesNoButton();

                if (currentSelectedScenario != null && !currentSelectedScenario.isEmpty()) {
                    try {
                        jsonobject = rootArrayData.getJSONObject(questionCount);
                        if (jsonobject.has(currentSelectedScenario)) {
                            rootArrayData = jsonobject.getJSONArray(currentSelectedScenario);
                            questionCount = 0;
                            if (rootArrayData.length() != questionCount) {
                                jsonobject = rootArrayData.getJSONObject(questionCount);
                                rootQ = jsonobject.getString("q");
                                rootR = jsonobject.getString("at");
                                if (jsonobject != null) {
                                    if (jsonobject.has("dd_data")) {
                                        dropDownData = jsonobject.getString("dd_data");
                                        if (dropDownData != null && dropDownData.length() > 0) {
                                            dropDownHash.put(rootQ, dropDownData);
                                        }
                                    }
                                }
                                questionnaireHash.put(rootQ, rootR);
                                tempQuesList.add(questionCount, rootQ);
                                tempQnAHash.put(rootQ, rootR);

                                if (rootR.equalsIgnoreCase("RT")) {
                                    ratingsLayout.setVisibility(View.VISIBLE);
                                    ratingsTextView.setText(rootQ);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    initSeekBar();
                                } else if (rootR.equalsIgnoreCase("FT")) {
                                    freeTextLayout.setVisibility(View.VISIBLE);
                                    freeTextTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("DD")) {
                                    dropdownLayout.setVisibility(View.VISIBLE);
                                    dropDownTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    createSpinner(rootQ);
                                } else if (rootR.equalsIgnoreCase("BT")) {
                                    buttonsLayout.setVisibility(View.VISIBLE);
                                    buttonsTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("YN")) {
                                    isInterestedLayout.setVisibility(View.VISIBLE);
                                    isInterestedTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                isInterestedLayout.setVisibility(View.VISIBLE);
                                isInterestedTextView.setText("Thank you for your survey");
                                isInterestedTextView.setTextSize(32);
                                isInterestedTextView.setTypeface(null, Typeface.BOLD);
                                dropdownLayout.setVisibility(View.GONE);
                                ratingsLayout.setVisibility(View.GONE);
                                formButtonLayout.setVisibility(View.GONE);
                                isInterestedButtonsLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                freeTextLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            questionCount++;
                            if (rootArrayData.length() != questionCount) {
                                jsonobject = rootArrayData.getJSONObject(questionCount);
                                if (jsonobject != null) {
                                    rootQ = jsonobject.getString("q");
                                    rootR = jsonobject.getString("at");
                                    if (jsonobject.has("dd_data")) {
                                        dropDownData = jsonobject.getString("dd_data");
                                        if (dropDownData != null && dropDownData.length() > 0) {
                                            dropDownHash.put(rootQ, dropDownData);
                                        }
                                    }
                                }
                                questionnaireHash.put(rootQ, rootR);
                                tempQuesList.add(questionCount, rootQ);
                                tempQnAHash.put(rootQ, rootR);

                                if (rootR.equalsIgnoreCase("RT")) {
                                    ratingsLayout.setVisibility(View.VISIBLE);
                                    ratingsTextView.setText(rootQ);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    initSeekBar();
                                } else if (rootR.equalsIgnoreCase("FT")) {
                                    freeTextLayout.setVisibility(View.VISIBLE);
                                    freeTextTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("DD")) {
                                    dropdownLayout.setVisibility(View.VISIBLE);
                                    dropDownTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    createSpinner(rootQ);
                                } else if (rootR.equalsIgnoreCase("BT")) {
                                    buttonsLayout.setVisibility(View.VISIBLE);
                                    buttonsTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("YN")) {
                                    isInterestedLayout.setVisibility(View.VISIBLE);
                                    isInterestedTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                isInterestedLayout.setVisibility(View.VISIBLE);
                                isInterestedTextView.setText("Thank you for your survey");
                                isInterestedTextView.setTextSize(32);
                                isInterestedTextView.setTypeface(null, Typeface.BOLD);
                                dropdownLayout.setVisibility(View.GONE);
                                ratingsLayout.setVisibility(View.GONE);
                                formButtonLayout.setVisibility(View.GONE);
                                isInterestedButtonsLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                freeTextLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    currentSelectedScenario = "";
                } else if (currentRatings != null && !currentRatings.isEmpty() && !currentRatings.equals("0")) {
                    try {
                        String key = "";
                        jsonobject = rootArrayData.getJSONObject(questionCount);
                        if (jsonobject.has(currentRatings)) {
                            questionCount = 0;
                            if (jsonobject != null) {
                                Iterator<String> keys = jsonobject.keys();
                                while (keys.hasNext()) {
                                    key = (String) keys.next();
                                    if (key.equals(currentRatings)) {
                                        key = currentRatings;
                                        break;
                                    }
                                }
                                jsonobject.get(key);
                                rootQ = jsonobject.getJSONObject(key).getString("q");
                                rootR = jsonobject.getJSONObject(key).getString("at");
                                if (jsonobject != null) {
                                    if (jsonobject.getJSONObject(key).has("dd_data")) {
                                        dropDownData = jsonobject.getJSONObject(key).getString("dd_data");
                                        if (dropDownData != null && dropDownData.length() > 0) {
                                            dropDownHash.put(rootQ, dropDownData);
                                        }
                                    }
                                }
                                questionnaireHash.put(rootQ, rootR);
                                tempQuesList.add(questionCount, rootQ);
                                tempQnAHash.put(rootQ, rootR);

                                if (rootR.equalsIgnoreCase("RT")) {
                                    ratingsLayout.setVisibility(View.VISIBLE);
                                    ratingsTextView.setText(rootQ);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    initSeekBar();
                                } else if (rootR.equalsIgnoreCase("FT")) {
                                    freeTextLayout.setVisibility(View.VISIBLE);
                                    freeTextTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("DD")) {
                                    dropdownLayout.setVisibility(View.VISIBLE);
                                    dropDownTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    createSpinner(rootQ);
                                } else if (rootR.equalsIgnoreCase("BT")) {
                                    buttonsLayout.setVisibility(View.VISIBLE);
                                    buttonsTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("YN")) {
                                    isInterestedLayout.setVisibility(View.VISIBLE);
                                    isInterestedTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                isInterestedLayout.setVisibility(View.VISIBLE);
                                isInterestedTextView.setText("Thank you for your survey");
                                isInterestedTextView.setTextSize(32);
                                isInterestedTextView.setTypeface(null, Typeface.BOLD);
                                dropdownLayout.setVisibility(View.GONE);
                                ratingsLayout.setVisibility(View.GONE);
                                formButtonLayout.setVisibility(View.GONE);
                                isInterestedButtonsLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                freeTextLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            questionCount++;
                            if (rootArrayData.length() != questionCount) {
                                jsonobject = rootArrayData.getJSONObject(questionCount);
                                if (jsonobject != null) {
                                    rootQ = jsonobject.getString("q");
                                    rootR = jsonobject.getString("at");
                                    if (jsonobject.has("dd_data")) {
                                        dropDownData = jsonobject.getString("dd_data");
                                        if (dropDownData != null && dropDownData.length() > 0) {
                                            dropDownHash.put(rootQ, dropDownData);
                                        }
                                    }
                                }
                                questionnaireHash.put(rootQ, rootR);
                                tempQuesList.add(questionCount, rootQ);
                                tempQnAHash.put(rootQ, rootR);

                                if (rootR.equalsIgnoreCase("RT")) {
                                    ratingsLayout.setVisibility(View.VISIBLE);
                                    ratingsTextView.setText(rootQ);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    initSeekBar();
                                } else if (rootR.equalsIgnoreCase("FT")) {
                                    freeTextLayout.setVisibility(View.VISIBLE);
                                    freeTextTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("DD")) {
                                    dropdownLayout.setVisibility(View.VISIBLE);
                                    dropDownTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    createSpinner(rootQ);
                                } else if (rootR.equalsIgnoreCase("BT")) {
                                    buttonsLayout.setVisibility(View.VISIBLE);
                                    buttonsTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("YN")) {
                                    isInterestedLayout.setVisibility(View.VISIBLE);
                                    isInterestedTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                isInterestedLayout.setVisibility(View.VISIBLE);
                                isInterestedTextView.setText("Thank you for your survey");
                                isInterestedTextView.setTextSize(32);
                                isInterestedTextView.setTypeface(null, Typeface.BOLD);
                                dropdownLayout.setVisibility(View.GONE);
                                ratingsLayout.setVisibility(View.GONE);
                                formButtonLayout.setVisibility(View.GONE);
                                isInterestedButtonsLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                freeTextLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    currentRatings = "";
                    customerRatings.setText("");
                } else if (formStatus != null && !formStatus.isEmpty()) {
                    try {
                        jsonobject = rootArrayData.getJSONObject(questionCount);
                        formStatus = formStatus.substring(0, 1);
                        if (jsonobject.has(formStatus)) {
                            rootArrayData = jsonobject.getJSONArray(formStatus);
                            questionCount = 0;
                            if (rootArrayData.length() != questionCount) {
                                jsonobject = rootArrayData.getJSONObject(questionCount);
                                rootQ = jsonobject.getString("q");
                                rootR = jsonobject.getString("at");
                                questionnaireHash.put(rootQ, rootR);
                                tempQuesList.add(questionCount, rootQ);
                                tempQnAHash.put(rootQ, rootR);

                                if (rootR.equalsIgnoreCase("RT")) {
                                    ratingsLayout.setVisibility(View.VISIBLE);
                                    ratingsTextView.setText(rootQ);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    initSeekBar();
                                } else if (rootR.equalsIgnoreCase("FT")) {
                                    freeTextLayout.setVisibility(View.VISIBLE);
                                    freeTextTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("DD")) {
                                    dropdownLayout.setVisibility(View.VISIBLE);
                                    dropDownTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    createSpinner(rootQ);
                                } else if (rootR.equalsIgnoreCase("BT")) {
                                    buttonsLayout.setVisibility(View.VISIBLE);
                                    buttonsTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("YN")) {
                                    isInterestedLayout.setVisibility(View.VISIBLE);
                                    isInterestedTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                isInterestedLayout.setVisibility(View.VISIBLE);
                                isInterestedTextView.setText("Thank you for your survey");
                                isInterestedTextView.setTextSize(32);
                                isInterestedTextView.setTypeface(null, Typeface.BOLD);
                                dropdownLayout.setVisibility(View.GONE);
                                ratingsLayout.setVisibility(View.GONE);
                                formButtonLayout.setVisibility(View.GONE);
                                isInterestedButtonsLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                freeTextLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            questionCount++;
                            if (rootArrayData.length() != questionCount) {
                                jsonobject = rootArrayData.getJSONObject(questionCount);
                                if (jsonobject != null) {
                                    rootQ = jsonobject.getString("q");
                                    rootR = jsonobject.getString("at");
                                }
                                questionnaireHash.put(rootQ, rootR);
                                tempQuesList.add(questionCount, rootQ);
                                tempQnAHash.put(rootQ, rootR);

                                if (rootR.equalsIgnoreCase("RT")) {
                                    ratingsLayout.setVisibility(View.VISIBLE);
                                    ratingsTextView.setText(rootQ);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    initSeekBar();
                                } else if (rootR.equalsIgnoreCase("FT")) {
                                    freeTextLayout.setVisibility(View.VISIBLE);
                                    freeTextTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("DD")) {
                                    dropdownLayout.setVisibility(View.VISIBLE);
                                    dropDownTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                    createSpinner(rootQ);
                                } else if (rootR.equalsIgnoreCase("BT")) {
                                    buttonsLayout.setVisibility(View.VISIBLE);
                                    buttonsTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    isInterestedLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                } else if (rootR.equalsIgnoreCase("YN")) {
                                    isInterestedLayout.setVisibility(View.VISIBLE);
                                    isInterestedTextView.setText(rootQ);
                                    ratingsLayout.setVisibility(View.GONE);
                                    freeTextLayout.setVisibility(View.GONE);
                                    dropdownLayout.setVisibility(View.GONE);
                                    buttonsLayout.setVisibility(View.GONE);
                                    endCallButtonLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                isInterestedLayout.setVisibility(View.VISIBLE);
                                isInterestedTextView.setText("Thank you for your survey");
                                isInterestedTextView.setTextSize(32);
                                isInterestedTextView.setTypeface(null, Typeface.BOLD);
                                dropdownLayout.setVisibility(View.GONE);
                                ratingsLayout.setVisibility(View.GONE);
                                formButtonLayout.setVisibility(View.GONE);
                                isInterestedButtonsLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                freeTextLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    formStatus = "";
                } else {
                    try {
                        questionCount++;
                        if (rootArrayData.length() != questionCount) {
                            jsonobject = rootArrayData.getJSONObject(questionCount);
                            if (jsonobject != null) {
                                rootQ = jsonobject.getString("q");
                                rootR = jsonobject.getString("at");
                                if (jsonobject.has("dd_data")) {
                                    dropDownData = jsonobject.getString("dd_data");
                                    if (dropDownData != null && dropDownData.length() > 0) {
                                        dropDownHash.put(rootQ, dropDownData);
                                    }
                                }
                            }
                            questionnaireHash.put(rootQ, rootR);
                            tempQuesList.add(questionCount, rootQ);
                            tempQnAHash.put(rootQ, rootR);

                            if (rootR.equalsIgnoreCase("RT")) {
                                ratingsLayout.setVisibility(View.VISIBLE);
                                ratingsTextView.setText(rootQ);
                                freeTextLayout.setVisibility(View.GONE);
                                dropdownLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                isInterestedLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                                initSeekBar();
                            } else if (rootR.equalsIgnoreCase("FT")) {
                                freeTextLayout.setVisibility(View.VISIBLE);
                                freeTextTextView.setText(rootQ);
                                ratingsLayout.setVisibility(View.GONE);
                                dropdownLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                isInterestedLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                            } else if (rootR.equalsIgnoreCase("DD")) {
                                dropdownLayout.setVisibility(View.VISIBLE);
                                dropDownTextView.setText(rootQ);
                                ratingsLayout.setVisibility(View.GONE);
                                freeTextLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                isInterestedLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                                createSpinner(rootQ);
                            } else if (rootR.equalsIgnoreCase("BT")) {
                                buttonsLayout.setVisibility(View.VISIBLE);
                                buttonsTextView.setText(rootQ);
                                ratingsLayout.setVisibility(View.GONE);
                                freeTextLayout.setVisibility(View.GONE);
                                dropdownLayout.setVisibility(View.GONE);
                                isInterestedLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                            } else if (rootR.equalsIgnoreCase("YN")) {
                                isInterestedLayout.setVisibility(View.VISIBLE);
                                isInterestedTextView.setText(rootQ);
                                ratingsLayout.setVisibility(View.GONE);
                                freeTextLayout.setVisibility(View.GONE);
                                dropdownLayout.setVisibility(View.GONE);
                                buttonsLayout.setVisibility(View.GONE);
                                endCallButtonLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            isInterestedLayout.setVisibility(View.VISIBLE);
                            isInterestedTextView.setText("Thank you for your survey");
                            isInterestedTextView.setTextSize(32);
                            isInterestedTextView.setTypeface(null, Typeface.BOLD);
                            dropdownLayout.setVisibility(View.GONE);
                            ratingsLayout.setVisibility(View.GONE);
                            formButtonLayout.setVisibility(View.GONE);
                            isInterestedButtonsLayout.setVisibility(View.GONE);
                            buttonsLayout.setVisibility(View.GONE);
                            freeTextLayout.setVisibility(View.GONE);
                            endCallButtonLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.yes_button:
                formStatus = "YES";
                yesButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_item_color));
                yesButton.setTextColor(Color.WHITE);
                yesButton.setBackground(getResources().getDrawable(R.drawable.custom_rounded_corners));
                noButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                noButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                break;
            case R.id.no_button:
                formStatus = "NO";
                noButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_item_color));
                noButton.setTextColor(Color.WHITE);
                noButton.setBackground(getResources().getDrawable(R.drawable.custom_rounded_corners));
                yesButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                yesButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                break;
            case R.id.creditCard_button:
                paymentModeFromButton = "Credit Card";
                creditCardButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_item_color));
                creditCardButton.setTextColor(Color.WHITE);
                creditCardButton.setBackground(getResources().getDrawable(R.drawable.custom_rounded_corners));
                debitCardButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                debitCardButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                walletsButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                walletsButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                payTMButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                payTMButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                cashButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                cashButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                break;
            case R.id.debitCard_button:
                paymentModeFromButton = "Debit Card";
                debitCardButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_item_color));
                debitCardButton.setTextColor(Color.WHITE);
                debitCardButton.setBackground(getResources().getDrawable(R.drawable.custom_rounded_corners));
                creditCardButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                creditCardButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                walletsButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                walletsButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                payTMButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                payTMButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                cashButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                cashButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                break;
            case R.id.wallets_button:
                paymentModeFromButton = "Wallets";
                walletsButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_item_color));
                walletsButton.setTextColor(Color.WHITE);
                walletsButton.setBackground(getResources().getDrawable(R.drawable.custom_rounded_corners));
                creditCardButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                creditCardButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                debitCardButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                debitCardButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                payTMButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                payTMButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                cashButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                cashButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                break;
            case R.id.paytm_button:
                paymentModeFromButton = "PayTM";
                payTMButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_item_color));
                payTMButton.setTextColor(Color.WHITE);
                payTMButton.setBackground(getResources().getDrawable(R.drawable.custom_rounded_corners));
                creditCardButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                creditCardButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                debitCardButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                debitCardButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                walletsButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                walletsButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                cashButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                cashButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                break;
            case R.id.cash_button:
                paymentModeFromButton = "Cash";
                cashButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_item_color));
                cashButton.setTextColor(Color.WHITE);
                cashButton.setBackground(getResources().getDrawable(R.drawable.custom_rounded_corners));
                creditCardButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                creditCardButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                debitCardButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                debitCardButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                walletsButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                walletsButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                payTMButton.setBackground(getResources().getDrawable(R.drawable.acp_button));
                payTMButton.setTextColor(ContextCompat.getColor(this, R.color.smb_grey_54));
                break;
            case R.id.endCallButton:
                //killCall(this);
                break;
            case R.id.ll_call_again:
                logAnalytics("Call_Again", "TextView");

                if (customerNumber != null) {
                    if (!customerNumber.isEmpty()) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        String callData = "tel:" + customerNumber;
                        callIntent.setData(Uri.parse(callData));
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        AfterCallActivity.this.finish();
                        startActivity(callIntent);
                    }
                }
                break;

            case R.id.tv_custom_date:

                logAnalytics("Custom_Date", "TextView");

                autoCheck = true;
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog
                        = new DatePickerDialog(this, R.style.MaterialPickerTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        preAlarmSet = true;

                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.YEAR, year);
                        SimpleDateFormat fmt = new SimpleDateFormat("EEE, d MMM yy");

                        final_start_year = year;
                        final_start_month = month;
                        final_start_dayOfMonth = dayOfMonth;
                        final_end_year = year;
                        final_end_month = month;
                        final_end_dayOfMonth = dayOfMonth;
                        Date currentDate = new Date();
                        String curentDate = DateFormat.format("yyyy/MM/dd", currentDate).toString();

                        String currentYearMonth = DateFormat.format("yyyy/MM", currentDate).toString();
                        String currentDay = String.valueOf(Integer.parseInt(DateFormat.format("dd", currentDate).toString()) + 1);

                        String settingDate = DateFormat.format("yyyy/MM/dd", mCalendar.getTime()).toString();
                        String settingYearMonth = DateFormat.format("yyyy/MM", mCalendar.getTime()).toString();
                        String settingDay = String.valueOf(Integer.parseInt(DateFormat.format("dd", mCalendar.getTime()).toString()));

                        llCallAgain.setVisibility(View.GONE);
                        if (curentDate.equalsIgnoreCase(settingDate)) {
                            mCustomDate.setText("Today");
                            llCallAgain.setVisibility(View.VISIBLE);
                        } else if ((currentYearMonth.equalsIgnoreCase(settingYearMonth)) && (currentDay.equalsIgnoreCase(settingDay))) {
                            mCustomDate.setText("Tomorrow");
                        } else {
                            mCustomDate.setText(fmt.format(mCalendar.getTime()));
                        }

                        int calculateMinuteAccordingly = mCalendar.get(Calendar.MINUTE);
                        int calculateHourAccordingly = mCalendar.get(Calendar.HOUR_OF_DAY);

                        final_start_min = calculateMinuteAccordingly;
                        final_start_hour = calculateHourAccordingly;

                        if (calculateMinuteAccordingly + 15 >= 60) {
                            final_end_min = calculateMinuteAccordingly + 15 - 60;
                            final_end_hour = calculateHourAccordingly + 1;
                        } else {
                            final_end_min = calculateMinuteAccordingly + 15;
                            final_end_hour = calculateHourAccordingly;
                        }

                    }
                }, year, month, day);

                Calendar c1 = Calendar.getInstance();
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

                break;
            case R.id.tv_custom_time:
                logAnalytics("Custom_Time", "TextView");

                autoCheck = true;
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePicker
                        = new TimePickerDialog(this, R.style.MaterialPickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        preAlarmSet = true;

                        String meridian = "";

                        // Prathik issue
                        Calendar datetime = null;
                        if (mCustomDate != null) {
                            if (mCustomDate.getText().toString().equalsIgnoreCase("today")) {
                                datetime = Calendar.getInstance();

                                int getYear = datetime.get(Calendar.YEAR);
                                int getMonth = datetime.get(Calendar.MONTH);
                                int getDay = datetime.get(Calendar.DAY_OF_MONTH);

                                final_start_year = getYear;
                                final_start_month = getMonth;
                                final_start_dayOfMonth = getDay;

                                mCalendar.set(Calendar.DAY_OF_MONTH, getDay);
                                mCalendar.set(Calendar.MONTH, getMonth);
                                mCalendar.set(Calendar.YEAR, getYear);
                            } else {
                                datetime = mCalendar.getInstance();
                            }
                        } else {
                            datetime = mCalendar.getInstance();
                        }
                        datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        datetime.set(Calendar.MINUTE, selectedMinute);

                        switch (datetime.get(Calendar.AM_PM)) {
                            case Calendar.AM:
                                meridian = "AM";
                                break;
                            case Calendar.PM:
                                meridian = "PM";
                                break;
                        }

                        final_start_min = selectedMinute;
                        final_start_hour = selectedHour;

                        int endHourOfDay = 0, endMinute;
                        endMinute = selectedMinute + 60;
                        if (endMinute >= 60) {
                            endHourOfDay = endHourOfDay + 1;
                            endMinute = selectedMinute % 60;
                        }
                        endHourOfDay = endHourOfDay + selectedHour;
                        if (selectedHour >= 24) {
                            endHourOfDay = endHourOfDay % 24;
                        }


                        mCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        mCalendar.set(Calendar.MINUTE, selectedMinute);

                        if (mCalendar.getTime().getTime() > currentTime) {

                            int end = 0;
                            int modifiedEndMinute = endMinute;
                            int modifiedEndHour = endHourOfDay;
                            switch (end) {
                                case 0:
                                    if (modifiedEndMinute + 15 > 60) {
                                        modifiedEndMinute = (modifiedEndMinute + 15) - modifiedEndMinute;

                                        if (modifiedEndHour >= 24) {
                                            modifiedEndHour = 0;
                                        } else {
                                            modifiedEndHour++;
                                        }
                                    } else {
                                        modifiedEndMinute += 15;
                                    }
                                    break;
                                case 1:
                                    if (modifiedEndMinute + 30 > 60) {
                                        modifiedEndMinute = (modifiedEndMinute + 30) - modifiedEndMinute;
                                        if (modifiedEndHour >= 24) {
                                            modifiedEndHour = 0;
                                        } else {
                                            modifiedEndHour++;
                                        }
                                    } else {
                                        modifiedEndMinute += 30;
                                    }
                                    break;
                                case 2:
                                    if (modifiedEndMinute != 60) {
                                        modifiedEndMinute = (modifiedEndMinute + 60) - modifiedEndMinute;
                                    }

                                    if (modifiedEndHour >= 24) {
                                        modifiedEndHour = 0;
                                    } else {
                                        modifiedEndHour++;
                                    }
                                    break;
                            }

                            final_end_hour = modifiedEndHour;
                            final_end_min = modifiedEndMinute;

                            if (selectedHour > 12) {
                                selectedHour -= 12;
                            }

                            if (selectedMinute < 10) {
                                mCustomTime.setText("" + selectedHour + ":0" + selectedMinute + " " + meridian);
                            } else {
                                mCustomTime.setText("" + selectedHour + ":" + selectedMinute + " " + meridian);
                            }
                        } else {
                            Toast.makeText(AfterCallActivity.this, "Past Date & Time selected, please put follow up in future.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, hour, minute, false);
                timePicker.show();
                break;

            case R.id.next_followup_textview:
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (mCalendar != null && mCalendar.getTime().getTime() > currentTime) {

        } else {
            Toast.makeText(AfterCallActivity.this, "Past Date & Time selected, please put follow up in future.", Toast.LENGTH_SHORT).show();
            return;
        }

        ServiceHandler.callDisconnected = false;
        layout5.setVisibility(View.GONE);
        formButtonLayout.setVisibility(View.GONE);
        endCallButtonLayout.setVisibility(View.GONE);
        isInterestedLayout.setVisibility(View.GONE);
        endCallButton.setVisibility(View.GONE);

        if (customerNumber == null) {
            final Dialog edialog = CustomTwoButtonDialog.afterCallBack(this);
            TextView btnYes = (TextView) edialog.findViewById(R.id.btn_yes);

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edialog.dismiss();
                    AfterCallActivity.this.finish();
                }
            });

            edialog.show();
        } else if (customerNumber.isEmpty()) {
            final Dialog edialog = CustomTwoButtonDialog.afterCallBack(this);
            TextView btnYes = (TextView) edialog.findViewById(R.id.btn_yes);

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edialog.dismiss();
                    AfterCallActivity.this.finish();
                }
            });

            edialog.show();
        } else if (llAfterCallLayout.getVisibility() == View.GONE) {
            llAfterCallLayout.setVisibility(View.VISIBLE);
            super.onBackPressed();
        } else {
            AfterCallActivity.this.finish();
        }
    }

    private String getLastContactNumber() {
        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        if (ServiceHandler.callDisconnected) {
            showACPScreen();
        }
    }

    private void showACPScreen() {
        llAfterCallLayout.setVisibility(View.VISIBLE);
        llCallAgain.setVisibility(View.VISIBLE);

        layout5.setVisibility(View.GONE);
        formButtonLayout.setVisibility(View.GONE);
        endCallButtonLayout.setVisibility(View.GONE);
        isInterestedLayout.setVisibility(View.GONE);
        endCallButton.setVisibility(View.GONE);
    }

    private void handleQuestionnaireAndACP() {

        String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
        if (qanda != null && !qanda.isEmpty()) {
            try {
                rootArrayData = new JSONArray(qanda);
                JSONObject jsonobject = rootArrayData.getJSONObject(questionCount);
                rootQ = jsonobject.getString("q");
                rootR = jsonobject.getString("at");
                if (jsonobject.has("dd_data")) {
                    dropDownData = jsonobject.getString("dd_data");
                    if (dropDownData != null && dropDownData.length() > 0) {
                        dropDownHash.put(rootQ, dropDownData);
                    }
                }
                questionnaireHash.put(rootQ, rootR);
                tempQuesList.add(questionCount, rootQ);
                tempQnAHash.put(rootQ, rootR);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            llAfterCallLayout.setVisibility(View.GONE);
            llCallAgain.setVisibility(View.GONE);

            layout5.setVisibility(View.VISIBLE);
            formButtonLayout.setVisibility(View.VISIBLE);
            endCallButtonLayout.setVisibility(View.VISIBLE);

            if (rootR.equalsIgnoreCase("RT")) {
                ratingsLayout.setVisibility(View.VISIBLE);
                ratingsTextView.setText(rootQ);
                freeTextLayout.setVisibility(View.GONE);
                dropdownLayout.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.GONE);
                isInterestedLayout.setVisibility(View.GONE);
                endCallButtonLayout.setVisibility(View.VISIBLE);
                initSeekBar();
            } else if (rootR.equalsIgnoreCase("FT")) {
                freeTextLayout.setVisibility(View.VISIBLE);
                freeTextTextView.setText(rootQ);
                ratingsLayout.setVisibility(View.GONE);
                dropdownLayout.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.GONE);
                isInterestedLayout.setVisibility(View.GONE);
                endCallButtonLayout.setVisibility(View.VISIBLE);
            } else if (rootR.equalsIgnoreCase("DD")) {
                dropdownLayout.setVisibility(View.VISIBLE);
                dropDownTextView.setText(rootQ);
                ratingsLayout.setVisibility(View.GONE);
                freeTextLayout.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.GONE);
                isInterestedLayout.setVisibility(View.GONE);
                endCallButtonLayout.setVisibility(View.VISIBLE);
                createSpinner(rootQ);
            } else if (rootR.equalsIgnoreCase("BT")) {
                buttonsLayout.setVisibility(View.VISIBLE);
                buttonsTextView.setText(rootQ);
                ratingsLayout.setVisibility(View.GONE);
                freeTextLayout.setVisibility(View.GONE);
                dropdownLayout.setVisibility(View.GONE);
                isInterestedLayout.setVisibility(View.GONE);
                endCallButtonLayout.setVisibility(View.VISIBLE);
            } else if (rootR.equalsIgnoreCase("YN")) {
                isInterestedLayout.setVisibility(View.VISIBLE);
                isInterestedTextView.setText(rootQ);
                ratingsLayout.setVisibility(View.GONE);
                freeTextLayout.setVisibility(View.GONE);
                dropdownLayout.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.GONE);
                endCallButtonLayout.setVisibility(View.VISIBLE);
            }
        } else {
            layout5.setVisibility(View.GONE);
            formButtonLayout.setVisibility(View.GONE);
            endCallButtonLayout.setVisibility(View.GONE);
            isInterestedLayout.setVisibility(View.GONE);
            endCallButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        layout5.setVisibility(View.GONE);
        formButtonLayout.setVisibility(View.GONE);
        endCallButtonLayout.setVisibility(View.GONE);
        isInterestedLayout.setVisibility(View.GONE);
        endCallButton.setVisibility(View.GONE);

        if (eventType != null) {
            boolean numberError = false;
            if (customerNumber == null) {
                numberError = true;
            } else if (customerNumber.isEmpty()) {
                numberError = true;
            }

            if (numberError) {
                SimpleDateFormat reportFormat = new SimpleDateFormat("EEE, d MMM yy");
                String reportDate = reportFormat.format(mCalendar.getTime());
                String possibleNumber = getLastContactNumber();
                // Made Changes to Null Numbers
                if (possibleNumber != null) {
                    cotactName = possibleNumber;
                }
                if (customerNumber == null) {
                    numberError = true;
                } else if (customerNumber.isEmpty()) {
                    numberError = true;
                }
                if (numberError) {
                    int reportMinute = mCalendar.get(Calendar.MINUTE);
                    int reportHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                    String reportDateTime = "<br/><br/>Date: " + reportDate + "<br/>Time: " + reportHour + ":" + reportMinute;
                    int version_code = CommonUtils.getVersionCode(this);
                    Toast.makeText(this, "The number was not recorded correctly", Toast.LENGTH_SHORT).show();
                    String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                            ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>Possible Number " + possibleNumber + reportDateTime
                            + "<br/> Current Activity: " + SmarterSMBApplication.currentActivity + "<br/><br/>App Version: " + version_code;

                    ServiceApplicationUsage.callErrorLog(message);
                }
                //Untill Here
            }

            if (!doNotCloseActivity) {
                getCalendarEntryInfo.setNotesAudioUrl(ApplicationSettings.getPref(AppConstants.AFTER_CALL_AUDIO_URL, ""));
                MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                if (canUpload) {
                    if (customerNumber != null && (!customerNumber.isEmpty())) {
                        if (!chechFirstCall(customerNumber)) {
                            createFirstCallFollowup();
                        }

                        if (rnr_id != 0) {
                            updateRnr(rnr_count);
                        }
                        updateRnRLocaldb(rnr_count + 1, customerNumber);

                        if (knowlarity) {

                        }
                        if ((eventType.equals("call_in_rec_off") || eventType.equals("call_in_rec_on")) && checkForAmeyoNumber(customerNumber)) {
                            ContentValues cv = new ContentValues();

                            cv.put("STATUS", status);

                            if (mydbAppointmentId != null && !(mydbAppointmentId.isEmpty())) {
                                cv.put("REMINDER_ID", mydbAppointmentId);
                            }
                            cv.put("TO1", customerNumber);
                            if (dbid != 0) {
                                db.update("ameyo_entries", cv, "_id=" + dbid, null);
                            }
                            if (db != null && db.isOpen()) {
                                db.close();
                            }
                            cloudCheck = true;
                            Intent intent = new Intent(this, ReuploadService.class);
                            if (!notAlead) {
                                if ((final_start_hour != 0) || (final_end_hour != 0) || (final_start_min != 0) || (final_end_min != 0)) {
                                    createNewAppointment();
                                    intent.putExtra("APPOINTMENT_SET", true);
                                    intent.putExtra("status", status);
                                }
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(intent);
                            } else {
                                startService(intent);
                            }
                            finish();
                        } else {
                            checkForNumber();
                            updateAppointmentCall(getCalendarEntryInfo);
                            updateSalesStage();

                            ContentValues cv = new ContentValues();
                            cv.put("UPLOAD_STATUS", 1);
                            if (latitude != 0 && longitude != 0) {
                                cv.put("DISP_MAP_LOC", 1);
                                cv.put("LAT", latitude);
                                cv.put("LONG", longitude);
                            }
                            if (mydbAppointmentId != null && !(mydbAppointmentId.isEmpty())) {
                                cv.put("REMINDER_ID", mydbAppointmentId);
                            }

                            if (dbid != 0) {
                                db.update("mytbl", cv, "_id=" + dbid, null);
                            }

                            if (db != null && db.isOpen()) {
                                db.close();
                            }

                            if (NotificationData.isWorkOrder) {
                                updateWorkOrder();
                            }

                            NotificationData.clear();
                            Intent intent = new Intent(this, ReuploadService.class);
                            if (!notAlead) {
                                if ((final_start_hour != 0) || (final_end_hour != 0) || (final_start_min != 0) || (final_end_min != 0)) {
                                    if ((!nextfollowup)) {
                                        if (autoCheck) {
                                            createNewAppointment();
                                            intent.putExtra("APPOINTMENT_SET", true);
                                            intent.putExtra("status", status);
                                        } else {
                                            if (ApplicationSettings.getPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, false)) {
                                                if (mSpinnerSalesStages.getSelectedItem().toString() != null) {
                                                    String statusName = mSpinnerSalesStages.getSelectedItem().toString();
                                                    if (statusName != null && !statusName.isEmpty()) {
                                                        if (nextAppointment_dbId == 0) {
                                                            if (ApplicationSettings.getPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, false)) {
                                                                if (!autoCheck) {
                                                                    if (duraionOfACall != null && (duraionOfACall.equalsIgnoreCase("0"))) {
                                                                        if (!((statusName.trim().equalsIgnoreCase(new String("Invalid"))) || (statusName.trim().equalsIgnoreCase(new String("Not interested"))) || (statusName.trim().equalsIgnoreCase(new String("Order placed"))) || (statusName.trim().equalsIgnoreCase(new String("Lost"))))) {
                                                                            if (ApplicationSettings.containsPref(AppConstants.RNR_CALLBACKS_NO)) {
                                                                                rnr_callbacks = ApplicationSettings.getPref(AppConstants.RNR_CALLBACKS_NO, 0);
                                                                            }
                                                                            if (rnr_count < rnr_callbacks || (status.equalsIgnoreCase("INTERESTED"))) {
                                                                                setrnrFollowup();
                                                                                createNewAppointment();
                                                                                intent.putExtra("APPOINTMENT_SET", true);
                                                                                intent.putExtra("status", statusName);

                                                                            }
                                                                            if (rnr_count == (rnr_callbacks + 1)) {
                                                                                checkRNR();
                                                                            }
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                                startForegroundService(intent);
                                                                            } else {
                                                                                startService(intent);
                                                                            }
                                                                            finish();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else if ((!nextfollowup)) {
                                    if (mSpinnerSalesStages.getSelectedItem().toString() != null) {
                                        String statusName = mSpinnerSalesStages.getSelectedItem().toString();
                                        if (statusName != null && !statusName.isEmpty()) {
                                            if (nextAppointment_dbId == 0) {
                                                if (ApplicationSettings.getPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, false)) {
                                                    if (!autoCheck) {
                                                        if (!((statusName.trim().equalsIgnoreCase(new String("Invalid"))) || (statusName.trim().equalsIgnoreCase(new String("Not interested"))) || (statusName.trim().equalsIgnoreCase(new String("Order placed"))) || (statusName.trim().equalsIgnoreCase(new String("Lost"))))) {
                                                            if (duraionOfACall != null && duraionOfACall.equalsIgnoreCase("0")) {
                                                                if ((rnr_count < rnr_callbacks) || (status.equalsIgnoreCase("INTERESTED"))) {
                                                                    setrnrFollowup();
                                                                    createNewAppointment();
                                                                    intent.putExtra("APPOINTMENT_SET", true);
                                                                    intent.putExtra("status", statusName);
                                                                } else if (rnr_count == 4) {
                                                                    checkRNR();
                                                                }
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    startForegroundService(intent);
                                                                } else {
                                                                    startService(intent);
                                                                }
                                                                finish();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(intent);
                            } else {
                                startService(intent);
                            }
                            finish();
                        }
                    }
                }

                if (db.isOpen()) {
                    db.close();
                }

                dbHelper.close();
            }

            ApplicationSettings.putPref(AFTER_CALL_NOTES, "");
            ApplicationSettings.putPref(AFTER_CALL_ASSIGN, "");
            ApplicationSettings.putPref(AFTER_CALL_NAME, "");
            ApplicationSettings.putPref(AppConstants.AFTER_CALL_AUDIO_URL, "");
            ApplicationSettings.putPref(AFTER_CALL_DESIGNATION, "");
            ApplicationSettings.putPref(AFTER_CALL_PHONE, "");
            ApplicationSettings.putPref(AFTER_CALL_COMPANY, "");
            ApplicationSettings.putPref(AFTER_CALL_EMAIL, "");
            ApplicationSettings.putPref(AFTER_CALL_LEAD, "");
            ApplicationSettings.putPref(AFTER_CALL_ADDRESS, "");
        }

        NotificationData.customerFeedback = "";
        ServiceHandler.callDisconnected = false;
    }

    private void createFirstCallFollowup() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long start_time = System.currentTimeMillis();
        long endTime = (start_time + (60 * 60 * 1000));

        ContentValues cv = new ContentValues();
        cv.put("SUBJECT", "");
        cv.put("NOTES", "");
        cv.put("START_TIME", start_time);
        cv.put("END_TIME", endTime);
        cv.put("LOCATION", "");
        cv.put("COMPANY_NAME", "");
        cv.put("DESIGNATION", "");
        cv.put("EMAILID", "");
        cv.put("WEBSITE", "");
        cv.put("STATUS", "Initial");
        cv.put("UPLOAD_STATUS", 0);
        cv.put("ALARMSETTO", 0);
        cv.put("COMPANY_ADDRESS", "");
        cv.put("PRODUCT_TYPE", "");
        cv.put("ORDER_POTENTIAL", "");
        cv.put("LEAD_SOURCE", "");
        cv.put("FLP_COUNT", 0);
        cv.put("COMPLETED", 1);
        if (duraionOfACall != null && !(duraionOfACall.equalsIgnoreCase("0"))) {
            cv.put("RNR_COUNT", 0);
        } else {
            cv.put("RNR_COUNT", 1);
        }
        if (imageUrl != null) {
            cv.put("NOTES_IMAGE", imageUrl);
        }
        if (ApplicationSettings.getPref(AppConstants.AFTER_CALL_AUDIO_URL, "") != null) {
            cv.put("NOTES_AUDIO", ApplicationSettings.getPref(AppConstants.AFTER_CALL_AUDIO_URL, ""));
        }

        if (customerNumber != null) {
            cv.put("TO1", customerNumber);
            cv.put("TONAME", "");
            if (!checkForAmeyoNumber(customerNumber)) {
                cv.put("RESPONSE_STATUS", "completed");
                long dbId = db.insert("remindertbNew", null, cv);
            }
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null)
            dbHelper.close();
    }

    private boolean checkForCloudTelephony(String phoneNumber) {
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
            String[] ameyoNumber = loadCloudTelephony();
            if (ameyoNumber != null) {
                for (int i = 0; i < ameyoNumber.length; i++) {
                    String ameyoCheck = "+" + ameyoNumber[i];
                    if (phoneNumber != null && phoneNumber.equalsIgnoreCase(ameyoCheck)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkForAmeyoNumber(String phoneNumber) {
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
            String[] ameyoNumber = loadCloudTelephony();
            if (ameyoNumber != null) {
                for (int i = 0; i < ameyoNumber.length; i++) {
                    if (PhoneNumberUtils.compare(phoneNumber, "+" + ameyoNumber[i])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String[] loadCloudTelephony() {
       return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == APPOINTMENT_SET) {
            if (data != null) {
                call_status_update = data.getBooleanExtra("APPOINTMENT_SET", true);
                String status = data.getStringExtra("status");
                if (status != null) {
                    setStatusStringForSpinner(status);
                }
                nextfollowup = true;
            }
        } else if (requestCode == LOAD_IMAGE) {
            if (resultCode == RESULT_OK) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
                try {
                    FileOutputStream out = new FileOutputStream(imagePath);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 800, false);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    out.flush();
                    out.close();

                    Toast.makeText(this, "Your image has been added to the notes", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new UploadBitmap().execute();
            }
        }

        doNotCloseActivity = false;
    }

    class UploadBitmap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            uploadImageFile();
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

    public void uploadImageFile() {
        String filePath = imagePath;
        JSONObject response = DataUploadUtils.uploadImageFileToServer(filePath, Urls.getUploadFileUrl());
        try {
            if (response != null && response.getString("url") != null) {
                imageUrl = response.getString("url");
                getCalendarEntryInfo.setNotesImageUrl(imageUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setStatusStringForSpinner(String statusString) {
        int selection = 0;
        if (statusString != null) {
            if (salesArrayList != null) {
                for (int i = 0; i < salesArrayList.size(); i++) {
                    if (statusString != null && statusString.equals(salesArrayList.get(i))) {
                        selection = i;
                    }
                }
                mSpinnerSalesStages.setSelection(selection, true);
            }
        }
    }

    private void deleteFutureAppointments(String number, String type) {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Long start = 0L;
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        start = c.getTimeInMillis();

        if (type.equalsIgnoreCase("notAlead")) {
            start = start + (24 * 60 * 60 * 1000);
        }
        Cursor appointmentCursor = database.rawQuery("SELECT * FROM remindertbNew WHERE TO1='" + number + "' AND START_TIME >" + start, null);

        if (appointmentCursor.getCount() > 0) {
            appointmentCursor.moveToFirst();
            do {
                String itemId;
                long startMls, endMls;
                GetCalendarEntryInfo calendarEntryItem = new GetCalendarEntryInfo();

                itemId = appointmentCursor.getString(appointmentCursor.getColumnIndex("_id"));
                calendarEntryItem.setAppointment_id(appointmentCursor.getString(appointmentCursor.getColumnIndex("APPOINTMENT_ID")));
                calendarEntryItem.setCaller_number(appointmentCursor.getString(appointmentCursor.getColumnIndex("TO1")));
                calendarEntryItem.setSubject(appointmentCursor.getString(appointmentCursor.getColumnIndex("SUBJECT")));
                calendarEntryItem.setNotes(appointmentCursor.getString(appointmentCursor.getColumnIndex("NOTES")));
                calendarEntryItem.setCaller_name(appointmentCursor.getString(appointmentCursor.getColumnIndex("TONAME")));
                startMls = appointmentCursor.getLong(appointmentCursor.getColumnIndex("START_TIME"));
                endMls = appointmentCursor.getLong(appointmentCursor.getColumnIndex("END_TIME"));
                calendarEntryItem.setLocation(appointmentCursor.getString(appointmentCursor.getColumnIndex("LOCATION")));
                calendarEntryItem.setCompanyName(appointmentCursor.getString(appointmentCursor.getColumnIndex("COMPANY_NAME")));
                calendarEntryItem.setDesignation(appointmentCursor.getString(appointmentCursor.getColumnIndex("DESIGNATION")));
                calendarEntryItem.setWebsite(appointmentCursor.getString(appointmentCursor.getColumnIndex("WEBSITE")));
                calendarEntryItem.setEmail_id(appointmentCursor.getString(appointmentCursor.getColumnIndex("EMAILID")));
                calendarEntryItem.setCustomer_id(appointmentCursor.getString(appointmentCursor.getColumnIndex("CUSTOMER_ID")));
                calendarEntryItem.setExternal_calendar_reference(appointmentCursor.getString(appointmentCursor.getColumnIndex("EXTERNAL_REFERENCE")));
                String start1 = DateFormat.format("dd-MMM-yyyy hh:mm a", startMls).toString();
                String end1 = DateFormat.format("dd-MMM-yyyy hh:mm a", endMls).toString();
                calendarEntryItem.setEvent_start_date(start1);
                calendarEntryItem.setEvent_end_date(end1);

                if (CommonUtils.isNetworkAvailable(this)) {
                    Intent intentService = new Intent(this, DeleteEventService.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("calenderEntryInfo", (Serializable) calendarEntryItem);
                    intentService.putExtras(bundle);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intentService);
                    } else {
                        startService(intentService);
                    }
                    database.delete("remindertbNew", "_id=" + itemId, null);
                } else {
                    ContentValues values = new ContentValues();
                    values.put("APPOINTMENT_TYPE", "Delete_Appointment");
                    values.put("UPLOAD_STATUS", 0);
                    values.put("STATUS", "Delete");
                    database.update("remindertbNew", values, "_id=" + itemId, null);

                    Intent aIntent = new Intent(this, ReuploadService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(aIntent);
                    } else {
                        startService(aIntent);
                    }
                    finish();
                }

            } while (appointmentCursor.moveToNext());
        }

        appointmentCursor.close();

        if (database.isOpen()) {
            database.close();
        }

        dbHelper.close();
    }

    private int getRNRCount() {
        int rnrCount = 0;
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (customerNumber != null && !customerNumber.isEmpty()) {
            String selection = "TO1=" + "'" + customerNumber + "'";
            Cursor cur = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");
            try {
                if (cur != null && cur.getCount() > 0) {
                    cur.moveToFirst();
                    rnr_count = cur.getInt(cur.getColumnIndex("RNR_COUNT"));
                    rnr_id = cur.getLong(cur.getColumnIndex("_id"));
                    rnrCount = rnr_count;
                }
            } finally {
                cur.close();
                db.close();
            }
        }
        if (duraionOfACall != null && !(duraionOfACall.equalsIgnoreCase("0"))) {
            return 0;
        } else {
            return rnrCount;
        }
    }

    private void updateRnr(int rnr_count) {

    }

    private void updateRnRLocaldb(int rnr_count, String customerNumber) {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Long> ids = new ArrayList<>();

        Cursor cur = db.rawQuery("SELECT * FROM remindertbNew where TO1=" + "'" + customerNumber + "'", null);
        cur.moveToFirst();
        int i = 0;
        try {
            if (cur != null && cur.getCount() > 0) {
                while (!cur.isAfterLast()) {
                    long id = cur.getLong(cur.getColumnIndex("_id"));
                    ids.add(id);
                    cur.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cur.close();
        }

        if (duraionOfACall != null && !(duraionOfACall.equalsIgnoreCase("0"))) {
            rnr_count = 0;
        }
        for (int k = 0; k < ids.size(); k++) {
            try {
                ContentValues cv = new ContentValues();
                cv.put("RNR_COUNT", rnr_count);
                Long id = ids.get(k);
                if (id != 0) {
                    db.update("remindertbNew", cv, "_id=" + id, null);
                }
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        db.close();
    }

    private Long createNewAppointment() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (reminderdbId != 0 && !cloudCheck) {
            Cursor cur = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + reminderdbId + "'", null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                dbStart = cur.getLong(cur.getColumnIndex("START_TIME"));
                dbEnd = cur.getLong(cur.getColumnIndex("END_TIME"));
                dbSubject = cur.getString(cur.getColumnIndex("SUBJECT"));
                if (ApplicationSettings.getPref(AFTER_CALL_NOTES, null) != null) {
                    dbNotes = ApplicationSettings.getPref(AFTER_CALL_NOTES, "");
                } else {
                    if (cur.getString(cur.getColumnIndex("NOTES")) == null) {
                        dbNotes = "";
                    } else {
                        dbNotes = cur.getString(cur.getColumnIndex("NOTES"));
                    }
                }
                dbLocation = cur.getString(cur.getColumnIndex("LOCATION"));
                dbTravelTime = cur.getLong(cur.getColumnIndex("TRAVEL_TIME"));
                dbCompanyName = cur.getString(cur.getColumnIndex("COMPANY_NAME"));
                dbDesignation = cur.getString(cur.getColumnIndex("DESIGNATION"));
                toNumber = cur.getString(cur.getColumnIndex("TO1"));
                if (mSpinnerSalesStages.getSelectedItem().toString() != null) {
                    dbstatusString = mSpinnerSalesStages.getSelectedItem().toString();
                } else {
                    dbstatusString = cur.getString(cur.getColumnIndex("STATUS"));
                }
                callRec_url = cur.getString(cur.getColumnIndex("CALLREC_URL"));
                toname = cur.getString(cur.getColumnIndex("TONAME"));
                db_appointmentId = cur.getString(cur.getColumnIndex("APPOINTMENT_ID"));
                dbwebsiteString = cur.getString(cur.getColumnIndex("WEBSITE"));
                db_external_reference = cur.getString(cur.getColumnIndex("EXTERNAL_REFERENCE"));
                dblead_source = cur.getString(cur.getColumnIndex("LEAD_SOURCE"));
                dbproduct_type = cur.getString(cur.getColumnIndex("PRODUCT_TYPE"));
                dbcompany_Address = cur.getString(cur.getColumnIndex("COMPANY_ADDRESS"));
                String followupValue = cur.getString(cur.getColumnIndex("FLP_COUNT"));

                if (followupValue != null && (!followupValue.isEmpty())) {
                    flpCount = Integer.parseInt(followupValue);
                    flpCount = flpCount + 1;
                } else {
                    flpCount = 1;
                }

                dbemailIdString = cur.getString(cur.getColumnIndex("EMAILID"));
                dborderValue = cur.getString(cur.getColumnIndex("ORDER_POTENTIAL"));

            }
            cur.close();
        } else {
            dbStart = 0;
            dbSubject = "";
            if (ApplicationSettings.getPref(AFTER_CALL_NOTES, null) != null) {
                if (!ApplicationSettings.getPref(AFTER_CALL_NOTES, "").isEmpty()) {
                    dbNotes = ApplicationSettings.getPref(AFTER_CALL_NOTES, "");
                } else {
                    dbNotes = "";
                }
            }
            if (ApplicationSettings.getPref(AFTER_CALL_ADDRESS, "") != null) {
                dbLocation = ApplicationSettings.getPref(AFTER_CALL_ADDRESS, "");
            }
            dbTravelTime = 0;
            if (ApplicationSettings.getPref(AFTER_CALL_COMPANY, "") != null) {
                dbCompanyName = ApplicationSettings.getPref(AFTER_CALL_COMPANY, "");
            }
            if (ApplicationSettings.getPref(AFTER_CALL_DESIGNATION, "") != null) {
                dbDesignation = ApplicationSettings.getPref(AFTER_CALL_DESIGNATION, "");
            }
            if (customerNumber != null) {
                toNumber = customerNumber;
            }
            if (mSpinnerSalesStages.getSelectedItem().toString() != null) {
                dbstatusString = mSpinnerSalesStages.getSelectedItem().toString();
            } else {
                dbstatusString = "initial";
            }
            callRec_url = "";
            toname = "";
            if (callerName != null) {
                if (!callerName.isEmpty()) {
                    toname = callerName;
                }
            }
            dbwebsiteString = "";
            db_external_reference = "";
            dblead_source = "";
            dbproduct_type = "";
            dbcompany_Address = "";
            dbemailIdString = "";

            dborderValue = "";
            flpCount = 1;
        }

        Long ameyoDbid = dosaveInDB();
        if (db.isOpen()) {
            db.close();
        }

        dbHelper.close();
        return ameyoDbid;
    }

    private Long dosaveInDB() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Calendar calender = new GregorianCalendar(final_start_year, final_start_month, final_start_dayOfMonth, final_start_hour, final_start_min);
        long start_time = calender.getTimeInMillis();
        Calendar calender1 = new GregorianCalendar(final_end_year, final_end_month, final_end_dayOfMonth, final_end_hour, final_end_min);
        long end_time = calender1.getTimeInMillis();
        Date date = new Date();

        boolean canput = true;
        long alarmBefor = 0;

        ContentValues cv = new ContentValues();

        String userEmail = "";
        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }

        if (appointmentId != null && !appointmentId.isEmpty()) {
            cv.put("APPOINTMENT_ID", appointmentId);
            cv.put("APPOINTMENT_TYPE", "update_appointment");
            String asign = null;
        }

        cv.put("SUBJECT", dbSubject);
        cv.put("NOTES", ApplicationSettings.getPref(AFTER_CALL_NOTES, ""));
        cv.put("START_TIME", start_time);
        cv.put("END_TIME", end_time);
        cv.put("LOCATION", ApplicationSettings.getPref(AFTER_CALL_ADDRESS, ""));
        cv.put("COMPANY_NAME", ApplicationSettings.getPref(AFTER_CALL_COMPANY, ""));
        cv.put("DESIGNATION", ApplicationSettings.getPref(AFTER_CALL_DESIGNATION, ""));
        cv.put("EMAILID", ApplicationSettings.getPref(AFTER_CALL_EMAIL, ""));
        cv.put("WEBSITE", dbwebsiteString);
        cv.put("STATUS", dbstatusString);
        if (checkForAmeyoNumber(toNumber)) {
            cv.put("UPLOAD_STATUS", 1);
        } else {
            cv.put("UPLOAD_STATUS", 0);
        }
        cv.put("ALARMSETTO", alarmBefor);
        cv.put("COMPANY_ADDRESS", dbCompanyName);
        cv.put("PRODUCT_TYPE", dbproduct_type);
        if (dborderValue != null && !(dborderValue.equalsIgnoreCase("null"))) {
            cv.put("ORDER_POTENTIAL", dborderValue);
        } else {
            cv.put("ORDER_POTENTIAL", "");
        }
        cv.put("LEAD_SOURCE", ApplicationSettings.getPref(AFTER_CALL_LEAD, ""));
        if (customerNumber != null && !(customerNumber.isEmpty())) {
            cv.put("TO1", customerNumber);
        } else if (ApplicationSettings.getPref(AFTER_CALL_PHONE, "") != null) {
            customerNumber = ApplicationSettings.getPref(AFTER_CALL_PHONE, "");
            cv.put("TO1", ApplicationSettings.getPref(AFTER_CALL_PHONE, ""));
        }
        cv.put("RESPONSE_STATUS", "accepted");
        if (ApplicationSettings.getPref(AFTER_CALL_ASSIGN, "") != null) {
            if (!ApplicationSettings.getPref(AFTER_CALL_ASSIGN, "").isEmpty()) {
                cv.put("ASSIGN_TO", ApplicationSettings.getPref(AFTER_CALL_ASSIGN, ""));
            }
        }
        cv.put("FLP_COUNT", flpCount);
        if (duraionOfACall != null && !(duraionOfACall.equalsIgnoreCase("0"))) {
            cv.put("RNR_COUNT", 0);
        } else {
            int rnr = rnr_count + 1;
            cv.put("RNR_COUNT", rnr);
        }
        cv.put("NOTES_IMAGE", imageUrl);
        cv.put("NOTES_AUDIO", ApplicationSettings.getPref(AppConstants.AFTER_CALL_AUDIO_URL, ""));

        if (ApplicationSettings.containsPref(AFTER_CALL_NAME)) {
            String customerName = ApplicationSettings.getPref(AFTER_CALL_NAME, "");
            if (customerName != null && !(customerName.isEmpty())) {
                cv.put("TONAME", customerName);
            }
        } else if (cotactName != null && !(cotactName.isEmpty())) {
            cv.put("TONAME", cotactName);
        } else {
            cv.put("TONAME", "");
        }

        if (ameyocallStartTime != null && !(ameyocallStartTime.isEmpty())) {
            cv.put("CREATED_AT", ameyocallStartTime);
        }

        long alarm_set_to_current = 0L;
        if (true) {

            boolean aFollow = false;

            String cmeridian = "";
            int minute = mCalendar.get(Calendar.MINUTE);
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            if (mCalendar.getTime().getTime() > currentTime) {
                switch (mCalendar.get(Calendar.AM_PM)) {
                    case Calendar.AM:
                        cmeridian = "AM";
                        break;
                    case Calendar.PM:
                        cmeridian = "PM";
                        break;
                }
            }

            int hourTw = hour;

            if (hourTw > 12) {
                hourTw -= 12;
            }

            int finalHour = final_start_hour;

            if (finalHour > 12) {
                finalHour -= 12;
            }

            String timeSet = " ";
            if (final_start_min < 10) {
                timeSet = " " + finalHour + ":0" + final_start_min + " " + cmeridian;
            } else {
                timeSet = " " + finalHour + ":" + final_start_min + " " + cmeridian;
            }

            SimpleDateFormat fmt = new SimpleDateFormat("EEE, d MMM yy");
            Calendar checkInstance = Calendar.getInstance();
            checkInstance.set(Calendar.DAY_OF_MONTH, final_start_dayOfMonth);
            checkInstance.set(Calendar.MONTH, final_start_month);
            checkInstance.set(Calendar.YEAR, final_start_year);

            String showDate = fmt.format(checkInstance.getTime());

            if (preAlarmSet) {
                alarm_set_to_current = start_time - (alarmBefore * 60 * 1000);
            } else {
                if (dbstatusString != null) {
                    if (!dbstatusString.equalsIgnoreCase("invalid") && !dbstatusString.equalsIgnoreCase("lost") && !dbstatusString.equalsIgnoreCase("not interested")) {
                        if (ApplicationSettings.getPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, false)) {
                            Toast.makeText(this, "Auto follow-up set for \n" + showDate + timeSet, Toast.LENGTH_SHORT).show();
                            aFollow = true;
                        }
                        alarm_set_to_current = start_time - (alarmBefore * 60 * 1000);
                    }
                }
            }

            if (dbstatusString != null) {
                if (!dbstatusString.equalsIgnoreCase("invalid") && !dbstatusString.equalsIgnoreCase("lost") && !aFollow && !dbstatusString.equalsIgnoreCase("not interested")) {
                    Calendar verifyCalendar = Calendar.getInstance();
                    int verifyDayOfMonth = verifyCalendar.get(Calendar.DAY_OF_MONTH);
                    int verifyMonth = verifyCalendar.get(Calendar.MONTH);
                    if ((final_start_dayOfMonth == verifyDayOfMonth) && (final_start_month == verifyMonth)) {
                        Toast.makeText(this, "Follow-up set at \n" + timeSet, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Follow-up set at \n" + showDate + timeSet, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (!cloudCheck) {
                if (nextAppointment_dbId != 0) {
                    if (db == null) {
                        db = dbHelper.getWritableDatabase();
                    }
                    db.update("remindertbNew", cv, "_id=" + nextAppointment_dbId, null);

                    if (dbstatusString != null) {
                        if (!dbstatusString.equalsIgnoreCase("invalid") && !dbstatusString.equalsIgnoreCase("lost") && !dbstatusString.equalsIgnoreCase("not interested")) {
                            CommonUtils.setAlarm(this, nextAppointment_dbId, alarm_set_to_current, "");
                        }
                    }
                    if (db != null && db.isOpen()) {
                        db.close();
                    }
                    if (dbHelper != null)
                        dbHelper.close();

                    return nextAppointment_dbId;
                } else {
                    if (db == null) {
                        db = dbHelper.getWritableDatabase();
                    }

                    long dbId = db.insert("remindertbNew", null, cv);
                    if (dbstatusString != null) {
                        if (!dbstatusString.equalsIgnoreCase("invalid") && !dbstatusString.equalsIgnoreCase("lost") && !dbstatusString.equalsIgnoreCase("not interested")) {
                            CommonUtils.setAlarm(this, dbId, alarm_set_to_current, "");
                        }
                    }

                    if (db.isOpen()) {
                        db.close();
                    }

                    dbHelper.close();
                    return dbId;
                }
            } else {
                if (callStartTime != null) {
                    cv.put("CALL_STARTIME", callStartTime);
                }

                if (db == null) {
                    db = dbHelper.getWritableDatabase();
                }
                Long dbId = db.insert("AmeyoReminderTable", null, cv);

                if (dbstatusString != null) {
                    if (!dbstatusString.equalsIgnoreCase("invalid") && !dbstatusString.equalsIgnoreCase("lost") && !dbstatusString.equalsIgnoreCase("not interested")) {
                        CommonUtils.setAlarm(this, dbId, alarm_set_to_current, "");
                    }
                }
                if (db.isOpen()) {
                    db.close();
                }

                dbHelper.close();

                return dbId;
            }
        } else {

        }
        return 0L;
    }

    private void checkForNumber() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (customerNumber == null || customerNumber.equals("")) {
            ContentValues cv = new ContentValues();
            if (subject != null) {
                if (subject.equals("Incoming Call") || eventType.equals("call_in_rec_on")) {
                    cv.put("FROM1", customerNumber);
                } else if (subject.equals("Outgoing Call") || eventType.equals("call_out_rec_on")) {
                    cv.put("TO1", customerNumber);
                }
            }
            cv.put("MSG_RECEPIENT_NO", customerNumber);
            db.update("mytbl", cv, "_id=" + dbid, null);
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null)
            dbHelper.close();
    }

    private void updateSalesStage() {
        String start_time = CommonUtils.getTimeFormatInISO(new Date());
        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        getCalendarEntryInfo.setStatus(status);
        getCalendarEntryInfo.notes = ApplicationSettings.getPref(AFTER_CALL_NOTES, "");
        getCalendarEntryInfo.caller_number = customerNumber;
        getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
        getCalendarEntryInfo.latitude = Double.toString(latitude);
        getCalendarEntryInfo.longitude = Double.toString(longitude);
        getCalendarEntryInfo.setEvent_start_date(start_time);

        getCalendarEntryInfo.extranotes = NotificationData.customerFeedback;

        String leadSource = ApplicationSettings.getPref(AFTER_CALL_LEAD, "");
        if (leadSource == null || leadSource.isEmpty()) {
            leadSource = getLeadSource(customerNumber);
        }
        getCalendarEntryInfo.lead_source = leadSource;

        String customerToContact = ApplicationSettings.getPref(CUSTOMER_TO_CONTACT, "");
        if (customerToContact == null || customerToContact.isEmpty()) {
            customerToContact = getCustomerToContact(customerNumber);
        }
        getCalendarEntryInfo.customer_id = customerToContact;

        extranote = getCalendarEntryInfo.extranotes;
        leadsrc = getCalendarEntryInfo.lead_source;
        wrapup = getCalendarEntryInfo.wrapup;
        eventstartdate = getCalendarEntryInfo.event_start_date;
        transactionid = getCalendarEntryInfo.transactionId;

        if (CommonUtils.isNetworkAvailable(this)) {
            if (!call_status_update) {
                new APIProvider.Update_SalesStatus(getCalendarEntryInfo, 0, null, null, new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        if (data != null) {
                            CommonUtils.saveContactInfoToSmartContactDB(getApplicationContext(), customerNumber, "", status, ApplicationSettings.getPref(AFTER_CALL_NOTES, ""), extranote, leadsrc, wrapup, eventstartdate, transactionid);
                        }
                    }
                }).call();
            }
        } else {
            if (ApplicationSettings.containsPref(AppConstants.UPLOAD_STATUS2)) {
                boolean uploadStatus2 = ApplicationSettings.getPref(AppConstants.UPLOAD_STATUS2, false);
                if (!uploadStatus2) {
                    CommonUtils.saveOflineStatusToSmartContactDB(getApplicationContext(), customerNumber, "", status, ApplicationSettings.getPref(AFTER_CALL_NOTES, ""), getCalendarEntryInfo.subStatus1, getCalendarEntryInfo.subStatus2, getCalendarEntryInfo.extranotes, getCalendarEntryInfo.lead_source, getCalendarEntryInfo.wrapup, getCalendarEntryInfo.event_start_date, getCalendarEntryInfo.transactionId);
                } else {
                    CommonUtils.saveOflineStatusToSmartContactDB2(getApplicationContext(), customerNumber, "", status, ApplicationSettings.getPref(AFTER_CALL_NOTES, ""), getCalendarEntryInfo.subStatus1, getCalendarEntryInfo.subStatus2, getCalendarEntryInfo.extranotes, getCalendarEntryInfo.lead_source, getCalendarEntryInfo.wrapup, getCalendarEntryInfo.event_start_date, getCalendarEntryInfo.transactionId);
                }
            } else {
                CommonUtils.saveOflineStatusToSmartContactDB2(getApplicationContext(), customerNumber, "", status, ApplicationSettings.getPref(AFTER_CALL_NOTES, ""), getCalendarEntryInfo.subStatus1, getCalendarEntryInfo.subStatus2, getCalendarEntryInfo.extranotes, getCalendarEntryInfo.lead_source, getCalendarEntryInfo.wrapup, getCalendarEntryInfo.event_start_date, getCalendarEntryInfo.transactionId);
            }
        }
    }

    private String getLeadSource(String number) {
        String leadSource = "";
        try {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if (number != null && !number.isEmpty()) {
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
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if (number != null && !number.isEmpty()) {
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

    public void getLatLong() {
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void updateAppointmentCall(GetCalendarEntryInfo getCalendarEntryInfo) {

        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.Update_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        } else {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 0);
            cv.put("APPOINTMENT_TYPE", "complete_appointment");
            if (localdbId != 0) {
                db.update("remindertbNew", cv, "_id=" + localdbId, null);
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private void updateWorkOrder() {
        WorkOrderEntryInfo newworkOrderEntryInfo = new WorkOrderEntryInfo();
        newworkOrderEntryInfo.work_order_status = workOrderStatus;
        newworkOrderEntryInfo.notes = ApplicationSettings.getPref(AFTER_CALL_NOTES, "");
        new APIProvider.Update_WorkOrder(newworkOrderEntryInfo, 0, null, null, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                if (data != null) {
                    if (data.equals("successfully created the event")) {

                    }
                }
            }
        }).call();
    }

    private void setrnrFollowup() {
        if (ApplicationSettings.containsPref(AppConstants.RNR_REMINDER)) {
            int rnrReminder = ApplicationSettings.getPref(AppConstants.RNR_REMINDER, 0);

            if (rnrReminder == 1) {
                int calculateMinute3 = Calendar.getInstance().get(Calendar.MINUTE) + 60;
                int calculateHour3 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                try {
                    if (calculateMinute3 >= 60) {
                        final_start_min = calculateMinute3 - 60;
                        final_start_hour = calculateHour3 + 1;
                    } else {
                        final_start_min = calculateMinute3;
                        final_start_hour = calculateHour3;
                    }

                    int calculateEndMinute3 = Calendar.getInstance().get(Calendar.MINUTE) + 75;

                    if (calculateEndMinute3 >= 60) {
                        final_end_min = calculateEndMinute3 - 60;
                        final_end_hour = calculateHour3 + 1;

                        if (final_end_min >= 60) {
                            final_end_min = final_end_min - 60;
                            final_end_hour = calculateHour3 + 1;
                        }
                    }

                    Calendar cDtTm2 = Calendar.getInstance();

                    final_start_year = cDtTm2.get(Calendar.YEAR);
                    final_start_month = cDtTm2.get(Calendar.MONTH);
                    final_start_dayOfMonth = cDtTm2.get(Calendar.DAY_OF_MONTH);
                    final_end_year = cDtTm2.get(Calendar.YEAR);
                    final_end_month = cDtTm2.get(Calendar.MONTH);
                    final_end_dayOfMonth = cDtTm2.get(Calendar.DAY_OF_MONTH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (rnrReminder > 1) {
                int calculateMinute3 = Calendar.getInstance().get(Calendar.MINUTE);
                int calculateHour3 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + rnrReminder;

                if (calculateMinute3 >= 60) {
                    final_start_min = calculateMinute3 - 60;
                    final_start_hour = calculateHour3 + 1;
                } else {
                    final_start_min = calculateMinute3;
                    final_start_hour = calculateHour3;
                }

                int calculateEndMinute3 = Calendar.getInstance().get(Calendar.MINUTE) + 75;

                if (calculateEndMinute3 >= 60) {
                    final_end_min = calculateEndMinute3 - 60;
                    final_end_hour = calculateHour3 + 1;

                    if (final_end_min >= 60) {
                        final_end_min = final_end_min - 60;
                        final_end_hour = calculateHour3 + 1;
                    }
                }

                Calendar cDtTm2 = Calendar.getInstance();

                final_start_year = cDtTm2.get(Calendar.YEAR);
                final_start_month = cDtTm2.get(Calendar.MONTH);
                final_start_dayOfMonth = cDtTm2.get(Calendar.DAY_OF_MONTH);
                final_end_year = cDtTm2.get(Calendar.YEAR);
                final_end_month = cDtTm2.get(Calendar.MONTH);
                final_end_dayOfMonth = cDtTm2.get(Calendar.DAY_OF_MONTH);
            }
        }
    }

    private void setDayTime(int count) {

        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);
        int startHour = 0;
        if (duraionOfACall != null && duraionOfACall.equalsIgnoreCase("0")) {

            int calculateMinute3 = Calendar.getInstance().get(Calendar.MINUTE) + 60;
            int calculateHour3 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

            if (calculateMinute3 >= 60) {
                final_start_min = calculateMinute3 - 60;
                final_start_hour = calculateHour3 + 1;
            } else {
                final_start_min = calculateMinute3;
                final_start_hour = calculateHour3;
            }

            int calculateEndMinute3 = Calendar.getInstance().get(Calendar.MINUTE) + 75;

            if (calculateEndMinute3 >= 60) {
                final_end_min = calculateEndMinute3 - 60;
                final_end_hour = calculateHour3 + 1;

                if (final_end_min >= 60) {
                    final_end_min = final_end_min - 60;
                    final_end_hour = calculateHour3 + 1;
                }
            }

            Calendar cDtTm2 = Calendar.getInstance();

            final_start_year = cDtTm2.get(Calendar.YEAR);
            final_start_month = cDtTm2.get(Calendar.MONTH);
            final_start_dayOfMonth = cDtTm2.get(Calendar.DAY_OF_MONTH);
            final_end_year = cDtTm2.get(Calendar.YEAR);
            final_end_month = cDtTm2.get(Calendar.MONTH);
            final_end_dayOfMonth = cDtTm2.get(Calendar.DAY_OF_MONTH);

        } else {
            final_start_min = minutes;
            final_start_hour = hour;
            int endHourOfDay = 0, endMinute;
            endMinute = minutes + 60;
            if (endMinute >= 60) {
                endHourOfDay = endHourOfDay + 1;
                endMinute = minutes % 60;
            }
            endHourOfDay = endHourOfDay + hour;
            if (hour >= 24) {
                endHourOfDay = endHourOfDay % 24;
            }
            final_end_hour = endHourOfDay;
            final_end_min = endMinute;

            int day = rightNow.get(Calendar.DAY_OF_MONTH);
            int month = rightNow.get(Calendar.MONTH);
            int year = rightNow.get(Calendar.YEAR);

            final_start_year = year;
            final_start_month = month;
            final_start_dayOfMonth = day + count;
            final_end_year = year;
            final_end_month = month;
            final_end_dayOfMonth = day + count;
        }
    }

    private void getDetailsFromDb(String appointment_id) {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + appointment_id + "'", null);
        int count = cursor.getCount();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String location = cursor.getString(cursor.getColumnIndex("LOCATION"));
            String callerName = cursor.getString(cursor.getColumnIndex("TONAME"));
            String designation = cursor.getString(cursor.getColumnIndex("DESIGNATION"));
            String companyName = cursor.getString(cursor.getColumnIndex("COMPANY_NAME"));
            String callerNumber = cursor.getString(cursor.getColumnIndex("TO1"));
            String emailIdString = cursor.getString(cursor.getColumnIndex("EMAILID"));
            String sourceOfLead = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
            rnr_count = cursor.getInt(cursor.getColumnIndex("RNR_COUNT"));
            flpValue = cursor.getInt(cursor.getColumnIndex("FLP_COUNT"));

            ApplicationSettings.putPref(AFTER_CALL_NAME, callerName);
            ApplicationSettings.putPref(AFTER_CALL_DESIGNATION, designation);
            ApplicationSettings.putPref(AFTER_CALL_PHONE, callerNumber);
            ApplicationSettings.putPref(AFTER_CALL_COMPANY, companyName);
            ApplicationSettings.putPref(AFTER_CALL_EMAIL, emailIdString);
            ApplicationSettings.putPref(AFTER_CALL_LEAD, sourceOfLead);
            ApplicationSettings.putPref(AFTER_CALL_ADDRESS, location);
        }

        cursor.close();

        if (db.isOpen()) {
            db.close();
        }

        dbHelper.close();
    }

    private void logAnalytics(String eventName, String eventType) {
        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_14);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
        mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_14, AnalyticsBundle);
    }

    private boolean chechFirstCall(String number) {
        if (reminderdbId != 0) {
            return true;
        }
        if (number != null) {
            MySql dbhelpr = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase dbase = dbhelpr.getWritableDatabase();
            Long current = System.currentTimeMillis();
            String selection = "START_TIME" + "<=" + current + " AND " + "TO1=" + "'" + number + "'" + " AND " + "STATUS != 'deleted' " + " AND" + " RESPONSE_STATUS = 'accepted' ";
            Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, null);
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    return true;
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (dbase != null && dbase.isOpen()) {
                    dbase.close();
                }
            }
        }
        return false;
    }

    private void showDailogForCumstomerNumber(AlertDialog alertDialog) {
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                logAnalytics("error_no_number_logs", "TextView");
                customerNumber = CustomLogPicker.selectedNumber;
                if (customerNumber != null && !(customerNumber.isEmpty())) {
                    customerNumber = CommonUtils.checkNo(customerNumber);
                }

                if (customerNumber == null || customerNumber.isEmpty()) {
                    customerNumber = getLastContactNumber();
                    if (customerNumber != null && !(customerNumber.isEmpty())) {
                        customerNumber = CommonUtils.checkNo(customerNumber);
                        setCustomerNumber(customerNumber);
                        possibleNumber = customerNumber;
                        getDetailsFromDb();
                        nextAppointment_dbId = getNextAppointmentData(customerNumber);
                    }
                } else {
                    setCustomerNumber(customerNumber);
                    possibleNumber = customerNumber;
                    getDetailsFromDb();
                    nextAppointment_dbId = getNextAppointmentData(customerNumber);
                }
            }
        });
        alertDialog.show();
    }

    private void setCustomerNumber(String number) {
        ApplicationSettings.putPref(AFTER_CALL_PHONE, number);
        setLayoutHeader();
        mNameOrNumber.setText(number);
        getLastUpdatedSalesStage();
        errorText.setVisibility(View.GONE);
    }

    private String getLastCallDuration(String number) {
        return "";
    }
}