package smarter.uearn.money.activities.appointment;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.fourmob.datepicker.DatePickerDialog;
import com.fourmob.timepicker.RadialPickerLayout;
import com.fourmob.timepicker.TimePickerDialog;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.AfterCallActivity;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.adapters.FilteredArrayAdapter;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.LatLong;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.models.SmartMail;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Validation;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.views.CustomItem;
import smarter.uearn.money.views.SmartAudioPlayer;

public class AppointmentCreateActivity extends BaseActivity implements OnItemSelectedListener, OnClickListener, View.OnTouchListener, smarter.uearn.money.views.events.OnItemSelectedListener, View.OnFocusChangeListener {

    public TextView start_time, start_date, to_mail;
    public Spinner spinner, statusSpinner, spinnerDuration;
    public AutoCompleteTextView subject, assign_to, cc;
    private EditText location;
    private EditText personalNotes, emailId;
    private EditText companyName, companyAddress, designationEt;
    private WebView mapWebView;
    private CustomItem customItem, websiteCustomItem;
    private SmartAudioPlayer audioPlayer;
    private LinearLayout callrecordingLayout;
    private LinearLayout callerNameLayout;
    private LinearLayout invalidlayout, notInterestedLayout, interesedLayout, substatus2ll;
    private Spinner invalidSpinner, notInterestedSpinner, interesedSpinner;
    private EditText callerName, callerNumber, substatus2ET;
    private EditText orderPotential, leadSource, productType;
    private Button minButton, hourButton, dayButton, monthButton;
    private LinearLayout nextAppointmentLayout;
    private TextView nextAppointmenttext;
    private TextView nextAppointmentSubject;
    private TextView nextAppointmentTime;
    private TextView leadformTv;
    private Button attachQde;
    private ImageButton localAttach;
    private boolean referalCustomer = false;
    public long alarmBefore = 0;
    private ArrayList<String> list_of_subjects = new ArrayList<String>();
    public SQLiteDatabase db;
    public int final_start_hour, final_end_hour, final_start_min, final_end_min, final_start_year, final_end_year, final_start_month, final_end_month, final_start_dayOfMonth, final_end_dayOfMonth;
    SmartMail smartMail;
    private String status;
    LatLong latLong;
    private ImageButton phoneButton;
    private String notesExtra, statusExtra, orderValueExtra, assign_to_email;
    private long cmail_db_id;
    private SalesStageInfo salesStageInfo = SmarterSMBApplication.salesStageInfo;
    ArrayAdapter<GroupsUserInfo> arrayAdapter;
    ArrayList<String> arrayList = new ArrayList<>();
    String number;
    int minutes = 0;
    int hours = 0;
    int endHours = 0;
    int endMinutes = 0;
    boolean cloudTelephony = false;
    String callStartTime = "";
    String subStatus1 = "", subStatus2 = "";
    private boolean statusCheck = false;
    private String imagePath = "", imageUrl;
    private final static int LOAD_IMAGE = 101;
    private MySql dbHelper = null;
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    List<String> leadSourceListFromDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.compose_appointment);
        SmarterSMBApplication.currentActivity = this;
        changeStatusBarColor(this);
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));

        callerNumber = findViewById(R.id.callerNumber);
        callerName = findViewById(R.id.callerName);
        emailId = findViewById(R.id.emailIdEditText);
        personalNotes = findViewById(R.id.personalNotesEdittext);
        personalNotes.setScroller(new Scroller(this));
        personalNotes.setMaxLines(3);
        personalNotes.setVerticalScrollBarEnabled(true);
        personalNotes.setMovementMethod(new ScrollingMovementMethod());

        websiteCustomItem = findViewById(R.id.websiteCustomItem);
        initUi();

        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("SetFollowup Screen");

        try {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                if (extras.containsKey("name")) {
                    String cname = getIntent().getStringExtra("name");
                    if (!cname.isEmpty()) {
                        callerName.setText(cname);
                    }
                }
                if (extras.containsKey("notes")) {
                    String notes = getIntent().getStringExtra("notes");
                    if (!notes.isEmpty()) {
                        personalNotes.setText(notes);
                    }
                }
                if (extras.containsKey("email")) {
                    String cemail = getIntent().getStringExtra("email");
                    if (!cemail.isEmpty()) {
                        emailId.setText(cemail);
                    }
                }
                if (extras.containsKey("phone")) {
                    String cphone = getIntent().getStringExtra("phone");
                    if (!cphone.isEmpty()) {
                        callerNumber.setText(cphone);
                    }
                }
                if (extras.containsKey("website")) {
                    String webt = getIntent().getStringExtra("website");
                    if (!webt.isEmpty()) {
                        websiteCustomItem.getEdittext().setText(webt);
                    }
                }
            }
        } catch (NullPointerException e) {

        }
        customItem = findViewById(R.id.locationCustomItem);
        location = customItem.getEdittext();
        location.setOnTouchListener(this);
        companyName = findViewById(R.id.companyCustomItem);
        companyName.setOnTouchListener(this);
        companyAddress = findViewById(R.id.companyAddressCustomItem);
        designationEt = findViewById(R.id.designationCustomItem);
        designationEt.setOnTouchListener(this);
        spinner = findViewById(R.id.spinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(2);
        spinner.setOnItemSelectedListener(this);

        if (salesStageInfo != null) {
            arrayList = salesStageInfo.getAppointmentSalesStage();
        }

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(this);
        statusSpinner.setEnabled(false);
        statusSpinner.setClickable(false);
        subject = findViewById(R.id.subjectEdittext);
        subject.setOnTouchListener(this);
        assign_to = findViewById(R.id.assignedToAutext);
        callrecordingLayout = findViewById(R.id.callrecordingLayout);
        callrecordingLayout.setVisibility(View.GONE);
        callerNameLayout = findViewById(R.id.callerNameLayout);
        audioPlayer = findViewById(R.id.player);
        personalNotes.setOnTouchListener(this);
        emailId.setOnTouchListener(this);
        websiteCustomItem.getEdittext().setOnTouchListener(this);
        phoneButton = findViewById(R.id.phone_button);
        phoneButton.setOnClickListener(this);
        mapWebView = findViewById(R.id.mapLocation);
        orderPotential = findViewById(R.id.orderpotential);
        leadSource = findViewById(R.id.sourceOfLead);
        productType = findViewById(R.id.product_type);
        cc = findViewById(R.id.ccEdittext);
        to_mail = findViewById(R.id.to_mail);
        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            to_mail.setText(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""));
        }
        callerNumber.setOnFocusChangeListener(this);
        boolean gpsSettings = ApplicationSettings.getPref(AppConstants.GPS_SETTINGS, false);
        if (gpsSettings)
            latLong = CommonUtils.getLatLong(this);

        Intent intent = getIntent();
        if (intent.hasExtra("SmartmailBundle")) {
            Bundle bundle = intent.getBundleExtra("SmartmailBundle");
            smartMail = CommonUtils.convertBundleToSmartMail(bundle);
        }
        if (intent.hasExtra("caller_number")) {
            String caller_number = intent.getStringExtra("caller_number");
            if (caller_number != null && !caller_number.equals(""))
                callerNumber.setText(caller_number);
        }
        if (intent.hasExtra("NotesField")) {
            notesExtra = intent.getStringExtra("NotesField");
        }
        if (intent.hasExtra("OrderValue")) {
            orderValueExtra = intent.getStringExtra("OrderValue");
        }
        if (intent.hasExtra("StatusChanged")) {
            statusExtra = intent.getStringExtra("StatusChanged");
        }
        if (intent.hasExtra("cmail_db_id")) {
            cmail_db_id = intent.getLongExtra("cmail_db_id", 0);
        }
        if (intent.hasExtra("CloudTelephony")) {
            cloudTelephony = true;
        }
        if (intent.hasExtra("CallStartTime")) {
            callStartTime = intent.getStringExtra("CallStartTime");
        }
        setPhoneNumberViews();
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        if (intent.hasExtra("NotificationId")) {
            int notificationId = intent.getIntExtra("NotificationId", 0);
            mNotificationManager.cancel(notificationId);
        }
        Calendar c = Calendar.getInstance();
        initTimeVariables(c);

        dbHelper = new MySql(this, "mydb", null, AppConstants.dBversion);
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM reminderSubjectTbl", null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                list_of_subjects.add(cursor.getString(cursor.getColumnIndex("SUBJECT")));
                cursor.moveToNext();
            }
        }
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list_of_subjects);
        subject.setAdapter(subjectAdapter);

        ArrayList<GroupsUserInfo> groupsUserInfos = CommonUtils.getGroupsUserInfoFromDB(AppointmentCreateActivity.this);
        arrayAdapter = new FilteredArrayAdapter<GroupsUserInfo>(this, R.layout.customfor_teammembers_autocomplete, groupsUserInfos, this) {
            @Override
            protected boolean keepObject(GroupsUserInfo obj, String mask) {
                mask = mask.toLowerCase();
                return obj.name.toLowerCase().startsWith(mask) || obj.email.toLowerCase().startsWith(mask) || obj.phone.startsWith(mask);
            }
        };
        assign_to.setAdapter(arrayAdapter);
        start_time = findViewById(R.id.time1);
        updateTime(start_time, final_start_hour, final_start_min);
        start_date = findViewById(R.id.date1);
        updateDate(start_date, final_start_year, final_start_month + 1, final_start_dayOfMonth);
        start_date.setOnClickListener(this);
        start_time.setOnClickListener(this);
        minButton = findViewById(R.id.minutesButton);
        hourButton = findViewById(R.id.hoursButton);
        dayButton = findViewById(R.id.dayButton);
        monthButton = findViewById(R.id.monthButton);
        minButton.setOnClickListener(this);
        hourButton.setOnClickListener(this);
        dayButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        attachQde = findViewById(R.id.attach_button);
        localAttach = findViewById(R.id.local_attach);
        leadformTv = findViewById(R.id.leadform_tv);
        attachQde.setOnClickListener(this);
        localAttach.setOnClickListener(this);
        nextAppointmentLayout = findViewById(R.id.next_appointment_Layout);
        nextAppointmenttext = findViewById(R.id.nameOrNumber);
        nextAppointmentSubject = findViewById(R.id.next_appointmentsubject);
        nextAppointmentTime = findViewById(R.id.next_appointmenttime);
        nextAppointmentLayout.setVisibility(View.GONE);
        nextAppointmentLayout.setOnClickListener(this);

        if (smartMail != null) {
            if (smartMail.getUrl() != null) {
                callrecordingLayout.setVisibility(View.VISIBLE);
                audioPlayer.setDataSource(smartMail.getUrl());
            }
        }
        CommonUtils.initMapWebview(mapWebView, "Bengaluru");

        if (smartMail != null) {
            if ((smartMail.getMessage() != null && smartMail.getMessage().equalsIgnoreCase("Incoming Call")) || (smartMail.getSubject() != null && (smartMail.getSubject().equalsIgnoreCase("Incoming Call") || smartMail.getSubject().equalsIgnoreCase("Incoming SMS")))) {
                getDetailsFromDB(smartMail.getFrom());
            } else {
                getDetailsFromDB(smartMail.getTo());
            }
        }
        if (intent.hasExtra("mobile_number")) {
            number = intent.getStringExtra("mobile_number");
            if (number != null)
                getNextAppointmentData(number, 0L);
        }
        if (intent.hasExtra("minutes")) {
            minutes = intent.getIntExtra("minutes", 0) + 5;
        }
        if (intent.hasExtra("hours")) {
            hours = intent.getIntExtra("hours", 0);
        }
        if (intent.hasExtra("endHours")) {
            endHours = intent.getIntExtra("endHours", 0);
        }
        if (intent.hasExtra("endMinutes")) {
            endMinutes = intent.getIntExtra("endMinutes", 0);
        }
        if ((minutes != 0) && (hours != 0) && (endHours != 0) && (endMinutes != 0)) {
            final_start_min = minutes;
            final_start_hour = hours;
            final_end_min = endMinutes;
            final_end_hour = endHours;
            updateTime(start_time, hours, minutes);
        }

        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String mapKeyword = location.getText().toString();
                CommonUtils.initMapWebview(mapWebView, mapKeyword);
            }
        });

        getDetailsFromIntentExtra();
        if (intent.hasExtra("createFeomMessageBox")) {
            String[] content = getIntent().getStringArrayExtra("messageContent");
            fillDataWithMessageContent(content);
        }

        spinnerDuration = findViewById(R.id.spinner_duration);
        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this,R.array.spinner_duration, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);
        spinnerDuration.setOnItemSelectedListener(this);

        leadSourceListFromDB = getListOfLeadSourceFromDB();
        ArrayAdapter<String> leadSourceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, leadSourceListFromDB);
        leadSourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (getIntent().hasExtra("database_id")) {
            Long dbid = getIntent().getLongExtra("database_id", 0);
            getDetailsFromDB(dbid);
        }
    }

    private void initUi() {
        interesedLayout = findViewById(R.id.interested_ll);
        notInterestedLayout = findViewById(R.id.notIterested_layout);
        invalidlayout = findViewById(R.id.invalid_layout);
        substatus2ll = findViewById(R.id.subStatus2ll);

        substatus2ET = findViewById(R.id.subStatus2ET);

        invalidSpinner = findViewById(R.id.invalidstatusSpinner);
        interesedSpinner = findViewById(R.id.interestedstatusSpinner);
        notInterestedSpinner = findViewById(R.id.notInterestedstatusSpinner);

        ArrayAdapter invalidAdapter = ArrayAdapter.createFromResource(this, R.array.invalid_spinner_array, android.R.layout.simple_spinner_item);
        invalidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        invalidSpinner.setAdapter(invalidAdapter);

        ArrayAdapter interestedAdapter = ArrayAdapter.createFromResource(this, R.array.interested_spinner_array, android.R.layout.simple_spinner_item);
        interestedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interesedSpinner.setAdapter(interestedAdapter);

        ArrayAdapter notInteresteddAdapter = ArrayAdapter.createFromResource(this, R.array.notinterested_spinner_array, android.R.layout.simple_spinner_item);
        notInteresteddAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notInterestedSpinner.setAdapter(notInteresteddAdapter);

        invalidSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subStatus1 = invalidSpinner.getSelectedItem().toString();
                substatus2ll.setVisibility(View.GONE);
                subStatus2 = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        interesedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subStatus1 = interesedSpinner.getSelectedItem().toString();
                if (subStatus1 != null && subStatus1.equalsIgnoreCase("PAN PENDING")) {

                } else if (subStatus1 != null && subStatus1.equalsIgnoreCase("PAN AVAILABLE")) {
                    substatus2ll.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        notInterestedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subStatus1 = notInterestedSpinner.getSelectedItem().toString();
                substatus2ll.setVisibility(View.GONE);
                subStatus2 = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getDetailsFromDB(String callernumber) {
        if (!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        if (callernumber != null && !callernumber.equals("")) {
            Cursor cur = null;
            cur = db.rawQuery("SELECT * FROM remindertbNew WHERE TO1= '" + callernumber + "'", null);
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                String subjectString = cur.getString(cur.getColumnIndex("SUBJECT"));
                if (subjectString != null) {
                    subject.setText(subjectString);
                    subject.setTextColor(Color.GRAY);
                }
                String notes = cur.getString(cur.getColumnIndex("NOTES"));
                if (notes != null) {
                    personalNotes.setText(notes);
                    personalNotes.setTextColor(Color.GRAY);
                }
                String locationString = cur.getString(cur.getColumnIndex("LOCATION"));
                if (locationString != null) {
                    location.setText(locationString);
                    location.setTextColor(Color.GRAY);
                }
                String callerNameString = cur.getString(cur.getColumnIndex("TONAME"));
                if (callerNameString != null && !callerNameString.equals("")) {
                    callerNameLayout.setVisibility(View.VISIBLE);
                    callerName.setText(callerNameString);
                }
                String callerNumberString = cur.getString(cur.getColumnIndex("TO1"));
                if (callerNumberString != null && !callerNumberString.equals("")) {
                    callerNumber.setText(callerNumberString);
                }

                String lead_source = cur.getString(cur.getColumnIndex("LEAD_SOURCE"));
                if (lead_source != null) {
                    leadSource.setText(lead_source);
                }
                String product_type = cur.getString(cur.getColumnIndex("PRODUCT_TYPE"));
                if (product_type != null) {
                    productType.setText(product_type);
                }

                String designation = cur.getString(cur.getColumnIndex("DESIGNATION"));
                if (designation != null) {
                    designationEt.setText(designation);
                    designationEt.setTextColor(Color.GRAY);
                }
                String companyNameText = cur.getString(cur.getColumnIndex("COMPANY_NAME"));
                if (companyNameText != null) {
                    companyName.setText(companyNameText);
                    companyName.setTextColor(Color.GRAY);
                }
                String company_Address = cur.getString(cur.getColumnIndex("COMPANY_ADDRESS"));
                if (company_Address != null) {
                    companyAddress.setText(company_Address);
                    companyAddress.setTextColor(Color.GRAY);
                }
                String websiteString = cur.getString(cur.getColumnIndex("WEBSITE"));
                if (websiteString != null) {
                    websiteCustomItem.getEdittext().setText(websiteString);
                    websiteCustomItem.getEdittext().setTextColor(Color.GRAY);
                }
                String emailIdString = cur.getString(cur.getColumnIndex("EMAILID"));
                if (emailIdString != null) {
                    emailId.setText(emailIdString);
                    emailId.setTextColor(Color.GRAY);
                }
                cur.moveToNext();
            }
            cur.close();
        }
    }

    private void getDetailsFromDB(Long dbId) {
        if (!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        if (dbId != 0) {
            Cursor cur = null;
            cur = db.rawQuery("SELECT * FROM remindertbNew WHERE _id= '" + dbId + "'", null);
            if (cur != null) {
                cur.moveToFirst();
                String subjectString = cur.getString(cur.getColumnIndex("SUBJECT"));
                if (subjectString != null) {
                    subject.setText(subjectString);
                    subject.setTextColor(Color.GRAY);
                }
                String notes = cur.getString(cur.getColumnIndex("NOTES"));
                if (notes != null) {
                    personalNotes.setText(notes);
                    personalNotes.setTextColor(Color.GRAY);
                }
                String locationString = cur.getString(cur.getColumnIndex("LOCATION"));
                if (locationString != null) {
                    location.setText(locationString);
                    location.setTextColor(Color.GRAY);
                }
                String callerNameString = cur.getString(cur.getColumnIndex("TONAME"));
                if (callerNameString != null && !callerNameString.equals("")) {
                    callerNameLayout.setVisibility(View.VISIBLE);
                    callerName.setText(callerNameString);
                }
                String callerNumberString = cur.getString(cur.getColumnIndex("TO1"));
                if (callerNumberString != null && !callerNumberString.equals("")) {
                    callerNumber.setText(callerNumberString);
                }
                String lead_source = cur.getString(cur.getColumnIndex("LEAD_SOURCE"));
                if (lead_source != null) {
                    leadSource.setText(lead_source);
                }
                String product_type = cur.getString(cur.getColumnIndex("PRODUCT_TYPE"));
                if (product_type != null) {
                    productType.setText(product_type);
                }
                String designationText = cur.getString(cur.getColumnIndex("DESIGNATION"));
                if (designationText != null) {
                    designationEt.setText(designationText);
                    designationEt.setTextColor(Color.GRAY);
                }
                String companyNameText = cur.getString(cur.getColumnIndex("COMPANY_NAME"));
                if (companyNameText != null) {
                    companyName.setText(companyNameText);
                    companyName.setTextColor(Color.GRAY);
                }
                String company_Address = cur.getString(cur.getColumnIndex("COMPANY_ADDRESS"));
                if (company_Address != null) {
                    companyAddress.setText(company_Address);
                    companyAddress.setTextColor(Color.GRAY);
                }
                String websiteString = cur.getString(cur.getColumnIndex("WEBSITE"));
                if (websiteString != null) {
                    websiteCustomItem.getEdittext().setText(websiteString);
                    websiteCustomItem.getEdittext().setTextColor(Color.GRAY);
                }
                String emailIdString = cur.getString(cur.getColumnIndex("EMAILID"));
                if (emailIdString != null) {
                    emailId.setText(emailIdString);
                    emailId.setTextColor(Color.GRAY);
                }
                cur.close();
            }
        }
    }

    private void getDetailsFromIntentExtra() {
        if (notesExtra != null) {
            personalNotes.setText(notesExtra);
        }
        if (orderValueExtra != null) {
            orderPotential.setText(orderValueExtra);
        }
        if (statusExtra != null) {
            setStatusStringForSpinner(statusExtra);
        }
    }

    private void fillDataWithMessageContent(String[] content) {
        callerName.setText(content[1] + " " + content[2]);
        if (content[3].matches(".*\\d.")) {
            callerNumber.setText(content[3]);
        } else {
            for (int i = 0; i < content.length; i++) {
                if (content[i].matches(".*\\d.")) {
                    if (content[i].length() >= 10) {
                        callerNumber.setText(content[i]);
                    }
                }
            }
        }
    }

    private void setStatusStringForSpinner(String statusString) {
        int selection = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            if (statusString != null && statusString.equals(arrayList.get(i))) {
                selection = i;
            }
        }
        statusSpinner.setSelection(selection, true);
    }

    private void setPhoneNumberViews() {
        String callerNameString = "";
        if (smartMail != null) {
            if (smartMail.getEventType().equals("email")) {
                emailId.setText(smartMail.getFrom());
            } else {
                if (smartMail.getCallerName() != null) {
                    callerNameString = smartMail.getCallerName();
                }
                if (smartMail.getMessage() != null && (smartMail.getMessage().equalsIgnoreCase("Incoming Call"))) {
                    callerNumber.setText(smartMail.getFrom());
                    getPreviousSalesStatus(smartMail.getFrom());
                    if (!callerNameString.equals("") && !callerNameString.equals(smartMail.getFrom())) {
                        callerName.setText(callerNameString);
                    }
                } else if (smartMail.getSubject() != null && (smartMail.getSubject().equalsIgnoreCase("Incoming Call") ||
                        smartMail.getSubject().equalsIgnoreCase("Incoming SMS"))) {
                    callerNumber.setText(smartMail.getFrom());
                    getPreviousSalesStatus(smartMail.getFrom());
                    if (!callerNameString.equals("") && !callerNameString.equals(smartMail.getFrom())) {
                        callerName.setText(callerNameString);
                    }
                } else {
                    callerNumber.setText(smartMail.getTo());
                    getPreviousSalesStatus(smartMail.getTo());
                    if (!callerNameString.equals("") && !callerNameString.equals(smartMail.getTo())) {
                        callerName.setText(callerNameString);
                    }
                }
            }
        }
    }

    private void getPreviousSalesStatus(String number) {
        String status = CommonUtils.getStatusFromSmartContactTable(this, number);
        if (status != null && !status.equals("")) {
            setStatusStringForSpinner(status);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.spinner) {
            switch (position) {
                case 0:
                    alarmBefore = 0;
                    break;
                case 1:
                    alarmBefore = 5;
                    break;
                case 2:
                    alarmBefore = 10;
                    break;
                case 3:
                    alarmBefore = 15;
                    break;
                case 4:
                    alarmBefore = 30;
                    break;
                case 5:
                    alarmBefore = 60;
                    break;
                default:
                    alarmBefore = 0;
            }
        }
        if (parent.getId() == R.id.statusSpinner) {
            status = arrayList.get(position);
            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                if (status.equalsIgnoreCase("INVALID")) {
                    interesedLayout.setVisibility(View.GONE);
                    notInterestedLayout.setVisibility(View.GONE);
                    invalidlayout.setVisibility(View.VISIBLE);
                    statusCheck = true;
                } else if (status.equalsIgnoreCase("INTERESTED")) {
                    invalidlayout.setVisibility(View.GONE);
                    notInterestedLayout.setVisibility(View.GONE);
                    interesedLayout.setVisibility(View.VISIBLE);
                    statusCheck = true;
                } else if (status.equalsIgnoreCase("NOT INTERESTED")) {
                    invalidlayout.setVisibility(View.GONE);
                    notInterestedLayout.setVisibility(View.VISIBLE);
                    interesedLayout.setVisibility(View.GONE);
                    statusCheck = true;
                } else {
                    invalidlayout.setVisibility(View.GONE);
                    notInterestedLayout.setVisibility(View.GONE);
                    interesedLayout.setVisibility(View.GONE);
                    statusCheck = false;
                }
            }
        }

        if (parent.getId() == R.id.spinner_duration) {
            int modifiedHour = final_end_hour;
            int modifiedMinute = final_end_min;
            switch (position) {
                case 0:
                    if (modifiedMinute + 15 > 60) {
                        modifiedMinute = (modifiedMinute + 15) - modifiedMinute;

                        if (modifiedHour >= 24) {
                            modifiedHour = 0;
                        } else {
                            modifiedHour++;
                        }
                    } else {
                        modifiedMinute += 15;
                    }
                    break;
                case 1:
                    if (modifiedMinute + 30 > 60) {
                        modifiedMinute = (modifiedMinute + 30) - modifiedMinute;
                        if (modifiedHour >= 24) {
                            modifiedHour = 0;
                        } else {
                            modifiedHour++;
                        }
                    } else {
                        modifiedMinute += 30;
                    }
                    break;
                case 2:
                    if (modifiedMinute != 60) {
                        modifiedMinute = (modifiedMinute + 60) - modifiedMinute;
                    }

                    if (modifiedHour >= 24) {
                        modifiedHour = 0;
                    } else {
                        modifiedHour++;
                    }
                    break;
            }

            final_end_hour = modifiedHour;
            final_end_min = modifiedMinute;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
            if (screen != null && screen.equalsIgnoreCase("Bank1AfterCallActivity")) {
                referalCustomer = true;
                restoreActionBar("Reference Customer");
            } else {
                restoreActionBar("Set Follow-up");
            }
        } else {
            restoreActionBar("Set Follow-up");
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6ca7d9")));

        MenuItem save = menu.findItem(R.id.action_one);
        save.setTitle("SAVE");
        return super.onCreateOptionsMenu(menu);
    }

    private boolean isValidEmail(EditText editText) {
        String text = editText.getText().toString().trim();
        return text == null || text.isEmpty() || Pattern.matches(EMAIL_REGEX, text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int id = mi.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        } else if (id == R.id.action_one) {
            if (!isValidEmail(emailId)) {
                Toast.makeText(this, "Please enter valid email id", Toast.LENGTH_SHORT).show();
            } else if (checkForTeamMemberInLocalDB(assign_to.getText().toString())) {
                checkNo();
                dosaveInLocalDB();
            } else
                Toast.makeText(this, "Choose a valid team member", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void dosaveInLocalDB() {
        if (!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        Calendar calender = new GregorianCalendar(final_start_year, final_start_month, final_start_dayOfMonth, final_start_hour, final_start_min);
        long start_time = calender.getTimeInMillis();
        Calendar calender1 = new GregorianCalendar(final_end_year, final_end_month, final_end_dayOfMonth, final_end_hour, final_end_min);
        long end_time = calender1.getTimeInMillis();
        Date date = new Date();
        String lead_contacts = ApplicationSettings.getPref(AppConstants.LEAD_CONTACTS, "");
        if (start_time > date.getTime() && end_time > start_time) {
            if (!callerNumber.getText().toString().equals("")) {
                String sub = subject.getText().toString();
                boolean canput = true;
                if (sub != null) {
                    if (list_of_subjects != null) {
                        for (int i = 0; i < list_of_subjects.size(); i++) {
                            if (sub.equals(list_of_subjects.get(i))) {
                                canput = false;
                                break;
                            }
                        }
                    }
                    if (canput) {
                        ContentValues cv1 = new ContentValues();
                        cv1.put("SUBJECT", sub);
                        db.insert("reminderSubjectTbl", null, cv1);
                        if (db != null && db.isOpen()) {
                            db.close();
                        }
                        if (dbHelper != null)
                            dbHelper.close();
                    }
                }

                ContentValues cv = new ContentValues();
                cv.put("SUBJECT", sub);
                cv.put("NOTES", personalNotes.getText().toString());
                cv.put("START_TIME", start_time);
                cv.put("END_TIME", end_time);
                cv.put("LOCATION", customItem.getText());
                cv.put("COMPANY_NAME", companyName.getText().toString());
                cv.put("DESIGNATION", designationEt.getText().toString());
                cv.put("EMAILID", emailId.getText().toString());
                cv.put("WEBSITE", websiteCustomItem.getText());
                cv.put("STATUS", status);
                if (checkForAmeyoNumber(callerNumber.getText().toString())) {
                    cv.put("UPLOAD_STATUS", 1);
                } else {
                    cv.put("UPLOAD_STATUS", 0);
                }
                cv.put("ALARMSETTO", alarmBefore);
                cv.put("COMPANY_ADDRESS", companyAddress.getText().toString());
                cv.put("PRODUCT_TYPE", productType.getText().toString());
                cv.put("ORDER_POTENTIAL", orderPotential.getText().toString());

                String leadsource = leadSource.getText().toString();
                if (leadsource != null && !leadsource.isEmpty()) {
                    cv.put("LEAD_SOURCE", leadsource);
                }

                if (referalCustomer) {
                    String useCampaign = ApplicationSettings.getPref(AppConstants.USE_CAMPAIGN, "");
                    if (useCampaign != null && !useCampaign.isEmpty() && !useCampaign.equals("null")) {
                        cv.put("LEAD_SOURCE", useCampaign + "_REFERRAL");
                    }
                }

                String start = CommonUtils.getTimeFormatInISO(new Date());
                if (start != null) {
                    cv.put("CREATED_AT", start);
                }
                cv.put("FLP_COUNT", 99);

                if (statusCheck) {
                    if (subStatus1 != null && !(subStatus1.equalsIgnoreCase("Choose an option"))) {
                        cv.put("SUBSTATUS1", subStatus1);
                    }
                    if (substatus2ET.getText().toString() != null && !(substatus2ET.getText().toString().isEmpty())) {
                        cv.put("SUBSTATUS2", substatus2ET.getText().toString());
                    }
                }
                cv.put("TO1", callerNumber.getText().toString());
                if (callerNumber.getText().toString() != null && !callerNumber.getText().toString().equals(""))
                    lead_contacts = lead_contacts + "," + callerNumber.getText().toString();

                if (latLong != null && latLong.latitude != null && latLong.longitude != null) {
                    cv.put("LAT", latLong.latitude);
                    cv.put("LONG", latLong.longitude);
                }

                if (callerName.getText().toString() != null && !(callerName.getText().toString().isEmpty())) {
                    cv.put("TONAME", callerName.getText().toString().toLowerCase());
                } else if (callerNumber.getText().toString() != null && !(callerNumber.getText().toString().isEmpty())) {
                    String name = CommonUtils.getContactName(getApplicationContext(), callerNumber.getText().toString());
                    cv.put("TONAME", name);
                } else {
                    cv.put("TONAME", "");
                }
                String userEmail = "";
                if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                    userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
                }

                if (cc.getText().toString() != null) {
                    cv.put("CCEMAIL", cc.getText().toString());
                }
                if (assign_to_email != null) {
                    cv.put("ASSIGN_TO", assign_to_email);
                } else if (assign_to_email != null && (assign_to_email.isEmpty())) {
                    cv.put("ASSIGN_TO", userEmail);
                    cv.put("RESPONSE_STATUS", "accepted");
                    assign_to_email = userEmail;
                } else if (assign_to_email == null) {
                    cv.put("ASSIGN_TO", userEmail);
                    assign_to_email = userEmail;
                    cv.put("RESPONSE_STATUS", "accepted");
                }
                if (imageUrl != null) {
                    cv.put("NOTES_IMAGE", imageUrl);
                }
                if (smartMail != null) {
                    if ((smartMail.getMessage() != null && smartMail.getMessage().equalsIgnoreCase("Incoming Call")) || (smartMail.getSubject() != null && (smartMail.getSubject().equalsIgnoreCase("Incoming Call") || smartMail.getSubject().equalsIgnoreCase("Incoming SMS")))) {
                        cv.put("TO1", smartMail.getFrom());
                    } else {
                        cv.put("TO1", smartMail.getTo());
                    }
                    if (smartMail.getUrl() != null) {
                        cv.put("CALLREC_URL", smartMail.getUrl());
                    }
                    cv.put("CMAIL_MAILID", cmail_db_id);
                }
                if (!db.isOpen()) {
                    MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
                    db = dbHelper.getWritableDatabase();
                }
                if (!cloudTelephony) {
                    long dbId = db.insert("remindertbNew", null, cv);

                    if (cmail_db_id != 0) {
                        ContentValues cv1 = new ContentValues();
                        cv1.put("REMINDER_ID", dbId);
                        db.update("mytbl", cv1, "_id=" + cmail_db_id, null);
                    }
                    //TODO: Add number to lead Contacts(including check if number already exists)
                    if (assign_to_email != null && !assign_to_email.equals("")) {
                        Toast.makeText(this, "Follow-up assigned", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Follow-up set", Toast.LENGTH_SHORT).show();
                    }
                    ApplicationSettings.putPref(AppConstants.LEAD_CONTACTS, lead_contacts);
                    Intent intent = new Intent();
                    intent.putExtra("APPOINTMENT_SET", true);
                    intent.putExtra("status", status);
                    setResult(AfterCallActivity.APPOINTMENT_SET, intent);

                    Calendar c = Calendar.getInstance();
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
                    long alarm_set_to = start_time - (alarmBefore * 60 * 1000);
                    CommonUtils.saveContactInfoToSmartContactDB(this, callerNumber.getText().toString(), emailId.getText().toString(), status, personalNotes.getText().toString(), "", "", "", "", "");
                    if (assign_to_email != null && assign_to_email.equalsIgnoreCase(userEmail)) {
                        CommonUtils.setAlarm(this, dbId, alarm_set_to, "");
                    }
                } else {
                    if (callStartTime != null && !(callStartTime.isEmpty())) {
                        cv.put("CALL_STARTIME", callStartTime);
                    }
                    Long dbId = db.insert("AmeyoReminderTable", null, cv);
                }
                if (db != null && db.isOpen()) {
                    db.close();
                }
                if (dbHelper != null)
                    dbHelper.close();
                Intent aIntent = new Intent(getApplicationContext(), ReuploadService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getApplicationContext().startForegroundService(aIntent);
                } else {
                    getApplicationContext().startService(aIntent);
                }
                finish();
            } else {
                Toast.makeText(this, "Please enter customer number ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "You have selected past date & time, pls select future date & Time.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkForAmeyoNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
        String[] ameyoNumber = loadAmeyoNumbers();
        if (ameyoNumber != null) {
            for (int i = 0; i < ameyoNumber.length; i++) {
                if (PhoneNumberUtils.compare(phoneNumber, ameyoNumber[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private String[] loadAmeyoNumbers() {
        return null;
    }

    private boolean checkForTeamMemberInLocalDB(String nameOrNumber) {
        if (!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        //TODO: check for local db for name or number if exists
        boolean existing = false;
        Cursor c;
        if (assign_to_email != null && !assign_to_email.equals("")) {
            return true;
        } else {
            if (nameOrNumber.equals("")) {
                existing = true;
                c = null;
            } else if (Validation.isPhoneNumber(nameOrNumber)) {
                c = db.rawQuery("SELECT * FROM TeamMembers where NUMBER='" + nameOrNumber + "'", null);
                c.moveToFirst();
                if (c.getCount() > 0) {
                    existing = true;
                    assign_to_email = c.getString(c.getColumnIndex("EMAILID"));
                }
            } else {
                c = db.rawQuery("SELECT * FROM TeamMembers where NAME='" + nameOrNumber + "'", null);
                c.moveToFirst();
                if (c.getCount() > 0) {
                    existing = true;
                    assign_to_email = c.getString(c.getColumnIndex("EMAILID"));
                } else {
                    c = db.rawQuery("SELECT * FROM TeamMembers where EMAILID='" + nameOrNumber + "'", null);
                    c.moveToFirst();
                    if (c.getCount() > 0) {
                        existing = true;
                        assign_to_email = nameOrNumber;
                    }
                }
            }
            if (c != null)
                c.close();
            return existing;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        final Calendar calendar = Calendar.getInstance();
        final String DATEPICKER_TAG = "datepicker";
        final String TIMEPICKER_TAG = "timepicker";

        if (v.getId() == R.id.time1) {

            final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                    final_start_hour = hourOfDay;
                    final_start_min = minute;
                    int endHourOfDay = 0, endMinute;
                    endMinute = minute + 60;
                    if (endMinute >= 60) {
                        endHourOfDay = endHourOfDay + 1;
                        endMinute = minute % 60;
                    }
                    endHourOfDay = endHourOfDay + hourOfDay;
                    if (hourOfDay >= 24) {
                        endHourOfDay = endHourOfDay % 24;
                    }

                    final_end_hour = endHourOfDay;
                    final_end_min = endMinute;
                    updateTime(start_time, hourOfDay, minute);
                }
            }, final_start_hour, final_start_min, false, false);
            timePickerDialog.setVibrate(true);
            timePickerDialog.setCloseOnSingleTapMinute(false);
            timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
        } else if (v.getId() == R.id.date1) {
            final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

                    final_start_year = year;
                    final_start_month = month;
                    final_start_dayOfMonth = day;
                    final_end_year = year;
                    final_end_month = month;
                    final_end_dayOfMonth = day;
                    updateDate(start_date, year, month + 1, day);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
            datePickerDialog.setVibrate(true);
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(true);
            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
        } else if (v.getId() == R.id.attach_button) {
            SecureRandom rand = new SecureRandom();
            imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/notesimage" + rand.nextInt() + ".JPEG"; // Previously set as JPG
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
            startActivityForResult(intent, LOAD_IMAGE);
        } else if (v.getId() == R.id.local_attach) {
            if (imageUrl != null) {
                loadDataFromUrl(imageUrl);
            }
        } else if (v.getId() == R.id.minutesButton) {
            dayButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            minButton.setBackgroundColor(getResources().getColor(R.color.blue));
            hourButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            monthButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minutes = rightNow.get(Calendar.MINUTE);
            int start_hour = 0;

            minutes = minutes + 10;
            if (minutes >= 60) {
                hour = hour + 1;
                minutes = minutes % 60;
            }
            if (hour >= 24) {
                hour = hour % 24;
            }
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
            updateTime(start_time, hour, minutes);
            int day = rightNow.get(Calendar.DAY_OF_MONTH);
            int month = rightNow.get(Calendar.MONTH);
            int year = rightNow.get(Calendar.YEAR);

            final_start_year = year;
            final_start_month = month;
            final_start_dayOfMonth = day;
            final_end_year = year;
            final_end_month = month;
            final_end_dayOfMonth = day;

            updateDate(start_date, year, month + 1, day);
        } else if (v.getId() == R.id.hoursButton) {
            dayButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            minButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            hourButton.setBackgroundColor(getResources().getColor(R.color.blue));
            monthButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minutes = rightNow.get(Calendar.MINUTE);
            int day = rightNow.get(Calendar.DAY_OF_MONTH);
            int month = rightNow.get(Calendar.MONTH);
            int year = rightNow.get(Calendar.YEAR);

            int startHour = 0;

            final_start_min = minutes;
            hour = hour + 1;
            if (hour >= 24) {
                startHour = startHour + 1;
                final_start_hour = startHour;
                updateTime(start_time, startHour, minutes);
                updateDate(start_date, year, month + 1, day + 1);
            } else {
                final_start_hour = hour;
                updateTime(start_time, hour, minutes);
            }
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
            final_start_year = year;
            final_start_month = month;
            final_start_dayOfMonth = day;
            final_end_year = year;
            final_end_month = month;
            final_end_dayOfMonth = day;

            updateDate(start_date, year, month + 1, day);
        } else if (v.getId() == R.id.dayButton) {
            dayButton.setBackgroundColor(getResources().getColor(R.color.blue));
            minButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            hourButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            monthButton.setBackgroundColor(getResources().getColor(R.color.half_black));

            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minutes = rightNow.get(Calendar.MINUTE);

            int day = rightNow.get(Calendar.DAY_OF_MONTH);
            int month = rightNow.get(Calendar.MONTH);
            int year = rightNow.get(Calendar.YEAR);

            int startHour = 0;

            final_start_min = minutes;
            hour = hour + 3;
            if (hour >= 24) {
                startHour = startHour + 3;
                final_start_hour = startHour;
                updateTime(start_time, startHour, minutes);
                updateDate(start_date, year, month + 1, day + 1);
            } else {
                final_start_hour = hour;
                updateTime(start_time, hour, minutes);
            }
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
            final_start_year = year;
            final_start_month = month;
            final_start_dayOfMonth = day;
            final_end_year = year;
            final_end_month = month;
            final_end_dayOfMonth = day;

            updateDate(start_date, year, month + 1, day);
        } else if (v.getId() == R.id.monthButton) {
            dayButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            minButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            hourButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            monthButton.setBackgroundColor(getResources().getColor(R.color.blue));
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(Calendar.DAY_OF_MONTH);
            int month = rightNow.get(Calendar.MONTH);
            int year = rightNow.get(Calendar.YEAR);

            final_start_year = year;
            final_start_month = month;
            final_start_dayOfMonth = day + 1;
            final_end_year = year;
            final_end_month = month;
            final_end_dayOfMonth = day + 1;

            updateDate(start_date, year, month + 1, day + 1);
        }
    }

    public void initTimeVariables(Calendar c) {
        final_start_hour = c.get(Calendar.HOUR_OF_DAY);
        final_end_hour = c.get(Calendar.HOUR_OF_DAY) + 1;
        final_start_min = c.get(Calendar.MINUTE);
        final_end_min = c.get(Calendar.MINUTE);
        final_start_dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        final_end_dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        final_start_month = c.get(Calendar.MONTH);
        final_end_month = c.get(Calendar.MONTH);
        final_start_year = c.get(Calendar.YEAR);
        final_end_year = c.get(Calendar.YEAR);
    }

    public void updateTime(TextView v, int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();
        v.setText(aTime);
    }

    public void updateDate(TextView view, int y, int m, int d) {
        String month;
        switch (m) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
            default:
                month = "Jan";
                break;
        }
        String aDate = new StringBuilder().append(d).append(' ').append(month).append(' ').append(y).toString();/*.append(' ').append(DayOfWeek)*/
        view.setText(aDate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        location.setTextColor(Color.BLACK);
        companyName.setTextColor(Color.BLACK);
        companyAddress.setTextColor(Color.BLACK);
        designationEt.setTextColor(Color.BLACK);
        subject.setTextColor(Color.BLACK);
        personalNotes.setTextColor(Color.BLACK);
        emailId.setTextColor(Color.BLACK);
        websiteCustomItem.getEdittext().setTextColor(Color.BLACK);

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOAD_IMAGE) {
            if (resultCode == RESULT_OK) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
                attachQde.setVisibility(View.GONE);
                localAttach.setVisibility(View.VISIBLE);
                leadformTv.setText("ATTACHED QDE : ");
                try {
                    FileOutputStream out = new FileOutputStream(imagePath);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 667, false);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    Toast.makeText(this, "Image Added", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new UploadBitmap().execute();
            }
        } else if (requestCode == 1) {
            if (data == null)
                return;
            Uri contactData = data.getData();
            if (contactData == null)
                return;
            Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
            cursor.moveToFirst();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemSelected(GroupsUserInfo mail) {
        if (mail != null && mail.name != null && !mail.name.equals(""))
            assign_to.setText(mail.name);
        else
            assign_to.setText(mail.email);
        assign_to.setSelection(mail.name.length());
        assign_to.dismissDropDown();
        assign_to_email = mail.email;

    }

    @Override
    public void getFilteredCount(final int size) {

    }

    private Long getNextAppointmentData(String number, Long current_startTime) {
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        db = dbHelper.getWritableDatabase();

        String callerNameOrNumber = number;
        String subjct = "meating";
        Long start_time = 0L;
        long nextAppointmentdbId = 0L;
        Date date = new Date();
        String selection = "START_TIME" + ">" + date.getTime() + " AND " + "COMPLETED='" + 0 + "'";
        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        if (cursor.getCount() > 0) {
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
                        if (cmail_db_id != 0) {
                            cursor.close();
                            break;
                        }
                    }
                }
                cursor.moveToNext();
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

    @Override
    public void onFocusChange(View view, boolean b) {
        int id = view.getId();
        if (id == R.id.callerNumber) {
            if (b) {

            } else {
                checkNo();
            }
        }
    }

    private void checkNo() {
        String value = callerNumber.getText().toString();
        if (value != null) {

            String numberWithCode = null;
            if (CommonUtils.buildE164Number("", value) != null) {
                callerNumber.setText(CommonUtils.buildE164Number("", value));
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
                                numberWithCode = CommonUtils.buildE164Number(e164Region, value);
                            }
                        } catch (NumberParseException e) {

                        }
                    }
                    if (numberWithCode != null) {
                        callerNumber.setText(numberWithCode);
                    }
                }
            }
        }
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromUrl(String url) {

        String s = "<html><body style=\"margin: 0; padding: 0\"><IMG  width=\"100%\" height=\"100%\" src=\"" + url + "\"><body><html>";

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.image_dailog,(ViewGroup) findViewById(R.id.layout_root));

        WebView wvNotes = layout.findViewById(R.id.fullimage);
        wvNotes.getSettings().setJavaScriptEnabled(true);
        wvNotes.setWebViewClient(new WebViewClient());
        wvNotes.setVerticalScrollBarEnabled(true);
        wvNotes.setHorizontalScrollBarEnabled(true);
        wvNotes.loadData(s, "text/html", "UTF-8");
        wvNotes.getSettings().setUseWideViewPort(true);
        wvNotes.getSettings().setLoadWithOverviewMode(true);

        imageDialog.setView(layout);
        imageDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        imageDialog.create();
        imageDialog.show();
    }

    private List<String> getListOfLeadSourceFromDB() {
        List<String> leadSourceList = new ArrayList<>();
        Cursor c = db.rawQuery("select DISTINCT LEAD_SOURCE from remindertbNew", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String leadSource = c.getString(c.getColumnIndex("LEAD_SOURCE"));
                    if (leadSource != null && !leadSource.isEmpty())
                        leadSourceList.add(leadSource);
                } while (c.moveToNext());
            }
        }
        return leadSourceList;
    }
}
