package smarter.uearn.money.activities.appointment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.timepicker.RadialPickerLayout;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import smarter.uearn.money.activities.LandingActivity;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.LatLong;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.views.CustomItem;
import smarter.uearn.money.adapters.FilteredArrayAdapter;
import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.SmarterSMBApplication;

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


public class AppointmentEditActivity extends BaseActivity implements OnItemSelectedListener, OnClickListener, smarter.uearn.money.views.events.OnItemSelectedListener, View.OnFocusChangeListener {

    public static TextView start_time, start_date, to_mail;
    public EditText notes, location, designation, companyName, emailId;
    public EditText subject, callerName, callerNumber, substatus2ET;
    private CustomItem customItem, websiteCustomItem;
    public long id = 0;
    public Spinner spinner, statusSpinner, spinnerDuration;
    public SQLiteDatabase db;
    public long dbStart, dbEnd, dbTravelTime;
    public String dbSubject, dbNotes, dbLocation, dbDesignation, dbCompanyName, dbNotesImage;
    public long alarmBefore = 0;
    private TextInputLayout subStatus2Til;
    public int final_start_hour, final_end_hour, final_start_min, final_end_min, final_start_year, final_end_year, final_start_month, final_end_month, final_start_dayOfMonth, final_end_dayOfMonth;
    public String toNumber, callRec_url, toname;
    public String appointmentId;
    private String status, dbstatusString, assign_to_email;
    private EditText orderPotential, leadSource, productType, companyAddress;
    AutoCompleteTextView assign_to, cc;
    LatLong latLong;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> alarmarrayList = new ArrayList<>();
    ArrayAdapter<GroupsUserInfo> arrayAdapter;
    private WebView mapWebView;
    private Button minButton, hourButton, dayButton, monthButton;
    private boolean check = false;
    private  long dbId = 0L;
    private LinearLayout invalidlayout, notInterestedLayout, interesedLayout, substatus2ll;
    private boolean statusCheck = false;
    private Spinner invalidSpinner, notInterestedSpinner, interesedSpinner;
    String  subStatus1 = "", subStatus2 = "", dbSubStatus2 = "", dbSubStatus1 = "", rnrCount;
    private TextView leadformTv, subStatus2text;
    private Button attachQde;
    private ImageButton localAttach;
    private String imagePath = "", imageUrl;
    private final static int LOAD_IMAGE = 101;
    List<String> panList = new ArrayList<>();
    List<String> qdeList = new ArrayList<>();
    List<String> smeList = new ArrayList<>();
    List<String> cibilList = new ArrayList<>();
    private ArrayList<GroupsUserInfo> groupsUserInfos;
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private String company = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.compose_appointment);
        SmarterSMBApplication.currentActivity = this;
        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }

        if(ApplicationSettings.getPref(AppConstants.USERINFO_ID,"").isEmpty()){
            this.finish();
            startActivity(new Intent(this,LandingActivity.class));
        }

        changeStatusBarColor(this);
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        initUi();
        initDb();
        setLocalDbData();
    }

    public void initTimeVariables() {
        Date d = new Date(dbStart);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(d);
        Date d2 = new Date(dbEnd);
        Calendar cal1 = GregorianCalendar.getInstance();
        cal1.setTime(d2);
        final_start_hour = cal.get(Calendar.HOUR_OF_DAY);
        final_end_hour = cal1.get(Calendar.HOUR_OF_DAY);
        final_start_min = cal.get(Calendar.MINUTE);
        final_end_min = cal1.get(Calendar.MINUTE);
        final_start_dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        final_end_dayOfMonth = cal1.get(Calendar.DAY_OF_MONTH);
        final_start_month = cal.get(Calendar.MONTH);
        final_end_month = cal1.get(Calendar.MONTH);
        final_start_year = cal.get(Calendar.YEAR);
        final_end_year = cal1.get(Calendar.YEAR);
    }

    private void setStatusSelection(String statusString) {
        int selection = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            if (statusString != null && statusString.equals(arrayList.get(i))) {
                selection = i;
            }
        }
        statusSpinner.setSelection(selection);
    }

    @Override
    public void onItemSelected(AdapterView<?> spin, View v, int pos, long id) {
        if (spin.getId() == R.id.spinner) {
            switch (pos) {
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
        if (spin.getId() == R.id.statusSpinner) {
            status = arrayList.get(pos);
            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                if(screen != null && screen.equalsIgnoreCase("Bank1AfterCallActivity")) {
                    if (status.equalsIgnoreCase("INVALID")) {
                        interesedLayout.setVisibility(View.GONE);
                        notInterestedLayout.setVisibility(View.GONE);
                        substatus2ll.setVisibility(View.GONE);
                        invalidlayout.setVisibility(View.VISIBLE);
                        statusCheck = true;
                    } else if (status.equalsIgnoreCase("INTERESTED")) {
                        invalidlayout.setVisibility(View.GONE);
                        notInterestedLayout.setVisibility(View.GONE);
                        interesedLayout.setVisibility(View.VISIBLE);
                        substatus2ll.setVisibility(View.VISIBLE);
                        if(dbSubStatus2 != null) {
                            substatus2ET.setText(dbSubStatus2);
                        }
                        statusCheck = true;
                    } else if (status.equalsIgnoreCase("NOT INTERESTED")) {
                        invalidlayout.setVisibility(View.GONE);
                        notInterestedLayout.setVisibility(View.VISIBLE);
                        interesedLayout.setVisibility(View.GONE);
                        substatus2ll.setVisibility(View.GONE);
                        statusCheck = true;
                    } else {
                        invalidlayout.setVisibility(View.GONE);
                        notInterestedLayout.setVisibility(View.GONE);
                        interesedLayout.setVisibility(View.GONE);
                        substatus2ll.setVisibility(View.GONE);
                        statusCheck = false;
                    }
                }
            }
        }

        if (spin.getId() == R.id.spinner_duration) {
            int modifiedHour = final_end_hour;
            int modifiedMinute = final_end_min;
            switch (pos) {
                case 0:
                    if (modifiedMinute + 15 > 60) {
                        modifiedMinute = (modifiedMinute + 15) - modifiedMinute;

                        if(modifiedHour>=24){
                            modifiedHour=0;
                        }else{
                            modifiedHour++;
                        }
                    } else {
                        modifiedMinute += 15;
                    }
                    break;
                case 1:
                    if (modifiedMinute + 30 > 60) {
                        modifiedMinute = (modifiedMinute + 30) - modifiedMinute;
                        if(modifiedHour>=24){
                            modifiedHour=0;
                        }else{
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

                    if(modifiedHour>=24){
                        modifiedHour=0;
                    } else{
                        modifiedHour++;
                    }
                    break;
            }

            final_end_hour = modifiedHour;
            final_end_min = modifiedMinute;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        if (check) {
            restoreActionBar("Set Follow-up");
        } else {
            restoreActionBar("Edit Follow-up");
        }
        MenuItem actionOne = menu.findItem(R.id.action_one);
        actionOne.setVisible(true);
        actionOne.setTitle("SAVE");
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
            setDailog();
        } else if (id == R.id.action_one) {
            checkNo();
            if(!isValidEmail(emailId)){
                Toast.makeText(this, "Please enter valid email id", Toast.LENGTH_SHORT).show();
            } else  if(substatus2ll.getVisibility() == View.VISIBLE) {
                String pan = substatus2ET.getText().toString();
                if(pan != null && !pan.isEmpty() && pan.length() == 10) {
                    boolean panCheck =  CommonUtils.panValidation(pan);
                    if(panCheck) {
                        dosaveInLocalDB();
                    } else {
                        subStatus2Til.setError("Invalid PAN format ABCDE1234F");
                    }
                } else {
                    subStatus2Til.setError("PAN Number Must be 10 digits");
                }
            } else {
                dosaveInLocalDB();
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        final Calendar calendar = Calendar.getInstance();
        final String DATEPICKER_TAG = "datepicker";
        final String TIMEPICKER_TAG = "timepicker";

        if (v.getId() == R.id.time1) {

            final com.fourmob.timepicker.TimePickerDialog timePickerDialog = com.fourmob.timepicker.TimePickerDialog.newInstance(new com.fourmob.timepicker.TimePickerDialog.OnTimeSetListener() {
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
            final com.fourmob.datepicker.DatePickerDialog datePickerDialog = com.fourmob.datepicker.DatePickerDialog.newInstance(new com.fourmob.datepicker.DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(com.fourmob.datepicker.DatePickerDialog datePickerDialog, int year, int month, int day) {
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

        } else if(v.getId() == R.id.attach_button) {
            SecureRandom rand = new SecureRandom();
            imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/notesimage" + rand.nextInt() + ".JPEG"; // Previously set as JPG
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
            startActivityForResult(intent, LOAD_IMAGE);
        } else if(v.getId() == R.id.local_attach) {
            if (imageUrl != null) {
                loadDataFromUrl(imageUrl);
            }
        }  else if (v.getId() == R.id.time2) {
            final com.fourmob.timepicker.TimePickerDialog timePickerDialog = com.fourmob.timepicker.TimePickerDialog.newInstance(new com.fourmob.timepicker.TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                    final_end_hour = hourOfDay;
                    final_end_min = minute;
                }
            }, final_end_hour, final_end_min, false, false);
            timePickerDialog.setVibrate(true);
            timePickerDialog.setCloseOnSingleTapMinute(false);
            timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);

        } else if (v.getId() == R.id.date2) {
            final com.fourmob.datepicker.DatePickerDialog datePickerDialog = com.fourmob.datepicker.DatePickerDialog.newInstance(new com.fourmob.datepicker.DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(com.fourmob.datepicker.DatePickerDialog datePickerDialog, int year, int month, int day) {
                    final_end_year = year;
                    final_end_month = month;
                    final_end_dayOfMonth = day;
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
            datePickerDialog.setVibrate(true);
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(true);
            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
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
            int startHour = 0;

            final_start_min = minutes;
            hour = hour + 1;
            if (hour >= 24) {
                startHour = startHour + 1;
                final_start_hour = startHour;
                updateTime(start_time, startHour, minutes);
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
        } else if (v.getId() == R.id.dayButton) {
            dayButton.setBackgroundColor(getResources().getColor(R.color.blue));
            minButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            hourButton.setBackgroundColor(getResources().getColor(R.color.half_black));
            monthButton.setBackgroundColor(getResources().getColor(R.color.half_black));

            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minutes = rightNow.get(Calendar.MINUTE);
            int startHour = 0;

            final_start_min = minutes;
            hour = hour + 3;
            if (hour >= 24) {
                startHour = startHour + 3;
                final_start_hour = startHour;
                updateTime(start_time, startHour, minutes);
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

        // Append in a StringBuilder
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
        String aDate = new StringBuilder().append(d).append('-').append(month).append('-').append(y).toString();
        view.setText(aDate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db.isOpen()) {
            db.close();
            db = null;
        }
    }

    private void goToHomeScreen() {
        finish();
    }

    //todo have to add company field
    private void dosaveInLocalDB() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        Calendar calender = new GregorianCalendar(final_start_year, final_start_month, final_start_dayOfMonth, final_start_hour, final_start_min);
        long start_time = calender.getTimeInMillis();
        Calendar calender1 = new GregorianCalendar(final_end_year, final_end_month, final_end_dayOfMonth, final_end_hour, final_end_min);
        long end_time = calender1.getTimeInMillis();
        Date date = new Date();

        if (start_time > date.getTime() && end_time > start_time) {
            if (!callerNumber.getText().toString().equals("") || !emailId.getText().toString().equals("")) {
                String sub = subject.getText().toString();
                boolean canput = true;

                ContentValues cv = new ContentValues();
                ContentValues cvPotential = new ContentValues();
                if (!check) {
                    cv.put("APPOINTMENT_ID", appointmentId);
                    cv.put("APPOINTMENT_TYPE", "update_appointment");
                }

                cv.put("SUBJECT", sub);
                cv.put("NOTES", notes.getText().toString());
                if(dbNotesImage != null) {
                    cv.put("NOTES_IMAGE", dbNotesImage);
                }
                cv.put("START_TIME", start_time);
                cv.put("END_TIME", end_time);
                cv.put("LOCATION", customItem.getText());
                cv.put("COMPANY_NAME", companyName.getText().toString());
                cv.put("DESIGNATION", designation.getText().toString());
                cv.put("EMAILID", emailId.getText().toString());
                cv.put("WEBSITE", websiteCustomItem.getText());
                if (statusSpinner.getSelectedItem().toString() != null) {
                    cv.put("STATUS", statusSpinner.getSelectedItem().toString());
                } else {
                    cv.put("STATUS", status);
                }
                cv.put("UPLOAD_STATUS", 0);
                cv.put("ALARMSETTO", alarmBefore);
                cv.put("COMPANY_ADDRESS", companyAddress.getText().toString());
                cv.put("PRODUCT_TYPE", productType.getText().toString());
                if (orderPotential.getText().toString() != null && !(orderPotential.getText().toString().equalsIgnoreCase("null"))) {
                    cv.put("ORDER_POTENTIAL", orderPotential.getText().toString());
                } else {
                    cv.put("ORDER_POTENTIAL", "");
                }
                if(rnrCount != null) {
                    cv.put("RNR_COUNT", rnrCount);
                }

                cvPotential.put("ORDER_POTENTIAL", orderPotential.getText().toString());
                cv.put("LEAD_SOURCE", leadSource.getText().toString());
                cv.put("TO1", callerNumber.getText().toString());
                if (latLong != null && latLong.latitude != null && latLong.longitude != null) {
                    cv.put("LAT", latLong.latitude);
                    cv.put("LONG", latLong.longitude);
                }
                if (callerName.getText().toString() != null) {
                    toname = callerName.getText().toString();
                    cv.put("TONAME", toname);
                }
                String userEmail = "";
                if(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                    userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL,"");
                }
                String assign = getAssignToMail();
                boolean assignCheck = false;
                if (assign != null && !assign.isEmpty()) {
                    assignCheck = true;
                    cv.put("ASSIGN_TO", assign);
                }  else if(assign_to_email != null && (assign_to_email.isEmpty())) {
                    cv.put("ASSIGN_TO", userEmail);
                    assign_to_email = userEmail;
                    cv.put("RESPONSE_STATUS", "accepted");
                } else if(assign_to_email == null) {
                    cv.put("ASSIGN_TO", userEmail);
                    assign_to_email = userEmail;
                    cv.put("RESPONSE_STATUS", "accepted");
                }

                if (imageUrl != null) {
                    cv.put("NOTES_IMAGE", imageUrl);
                }

                if(statusCheck) {
                    if(subStatus1 != null && !(subStatus1.equalsIgnoreCase("Choose an option"))) {
                        cv.put("SUBSTATUS1", subStatus1);
                    }
                    if(substatus2ET.getText().toString() != null  && !(substatus2ET.getText().toString().isEmpty())) {
                        subStatus2 =  substatus2ET.getText().toString();
                        cv.put("SUBSTATUS2", substatus2ET.getText().toString());
                    }
                }

                if (cc.getText().toString() != null) {
                    cv.put("CCEMAIL", cc.getText().toString());
                }
                String start = CommonUtils.getTimeFormatInISO(new Date());
                if(start != null) {
                    cv.put("CREATED_AT", start);
                }
                if (id != 0) {
                    db.update("remindertbNew", cv, "_id=" + id, null);
                } else {
                    dbId = db.insert("remindertbNew", null, cv);
                }

                if (db != null && db.isOpen()) {
                    db.close();
                }

                long alarm_set_to = start_time - (alarmBefore * 60 * 1000);

                if (assign_to_email!= null && (assign_to_email.equalsIgnoreCase(userEmail)) ) {
                    if(id != 0) {
                        CommonUtils.setAlarm(this, id, alarm_set_to, "");

                    } else if(dbId != 0){
                        CommonUtils.setAlarm(this, dbId, alarm_set_to, "");
                    }
                }
                if(assignCheck) {
                    Toast.makeText(this, "Follow-up assign to "+assign, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();
                }
                if(assignCheck) {
                    createFirstCallFollowup();
                }
                Intent aIntent = new Intent(getApplicationContext(), ReuploadService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getApplicationContext().startForegroundService(aIntent);
                } else {
                    getApplicationContext().startService(aIntent);
                }
                goToHomeScreen();
            } else {
                Toast.makeText(this, "Please enter customer number or email id", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "You have selected past date & time, pls select future date & Time.", Toast.LENGTH_SHORT).show();
        }
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
        } else if(requestCode == 1) {
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
        assign_to.setText(mail.name);
        if (mail != null && mail.name != null && !mail.name.equals(""))
            assign_to.setText(mail.name);
        else
            assign_to.setText(mail.email);
        assign_to.setSelection(mail.name.length());
        assign_to.dismissDropDown();
        assign_to_email = mail.email;
    }

    @Override
    public void getFilteredCount(int size) {

    }

    @Override
    public void onBackPressed() {
        setDailog();
    }

    public void setDailog() {
        final AlertDialog.Builder dailog = new AlertDialog.Builder(this);
        dailog.setMessage("Follow-up not saved");
        dailog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dailog.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dailog.show();
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

    private void initUi() {
        emailId = findViewById(R.id.emailIdEditText);
        websiteCustomItem = findViewById(R.id.websiteCustomItem);
        callerName = findViewById(R.id.callerName);
        callerNumber = findViewById(R.id.callerNumber);
        assign_to = findViewById(R.id.assignedToAutext);
        orderPotential = findViewById(R.id.orderpotential);
        leadSource = findViewById(R.id.sourceOfLead);
        productType = findViewById(R.id.product_type);
        mapWebView = findViewById(R.id.mapLocation);
        interesedLayout = findViewById(R.id.interested_ll);
        notInterestedLayout = findViewById(R.id.notIterested_layout);
        invalidlayout = findViewById(R.id.invalid_layout);
        substatus2ll = findViewById(R.id.subStatus2ll);
        substatus2ET = findViewById(R.id.subStatus2ET);
        subStatus2Til = findViewById(R.id.subStatus2Til);
        companyAddress  = findViewById(R.id.companyAddressCustomItem);
        cc = findViewById(R.id.ccEdittext);
        to_mail = findViewById(R.id.to_mail);
        minButton = findViewById(R.id.minutesButton);
        hourButton = findViewById(R.id.hoursButton);
        dayButton = findViewById(R.id.dayButton);
        monthButton = findViewById(R.id.monthButton);
        attachQde = findViewById(R.id.attach_button);
        localAttach = findViewById(R.id.local_attach);
        leadformTv = findViewById(R.id.leadform_tv);
        subStatus2text = findViewById(R.id.subStatus2text);
        ImageButton phoneButton = findViewById(R.id.phone_button);
        spinner = findViewById(R.id.spinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        minButton.setOnClickListener(this);
        hourButton.setOnClickListener(this);
        dayButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        attachQde.setOnClickListener(this);
        localAttach.setOnClickListener(this);
        phoneButton.setOnClickListener(this);
        phoneButton.setVisibility(View.GONE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(2);
        spinner.setOnItemSelectedListener(this);
        alarmarrayList.add("0");
        alarmarrayList.add("5");
        alarmarrayList.add("10");
        alarmarrayList.add("15");
        alarmarrayList.add("30");
        alarmarrayList.add("60");

        invalidSpinner = findViewById(R.id.invalidstatusSpinner);
        interesedSpinner = findViewById(R.id.interestedstatusSpinner);
        notInterestedSpinner = findViewById(R.id.notInterestedstatusSpinner);

        ArrayAdapter invalidAdapter = ArrayAdapter.createFromResource(this, R.array.invalid_spinner_array, android.R.layout.simple_spinner_item);
        invalidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        invalidSpinner.setAdapter(invalidAdapter);

        ArrayAdapter interestedAdapter = ArrayAdapter.createFromResource(this, R.array.interested_spinner_editarray, android.R.layout.simple_spinner_item);
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
                if(dbSubStatus1 == null) {
                    dbSubStatus1 = "";
                }
                if(dbSubStatus1 != null && !(dbSubStatus1.equalsIgnoreCase("PAN APPROVED") || dbSubStatus1.equalsIgnoreCase("LEAD SHEET INCOMPLETE") || dbSubStatus1.equalsIgnoreCase("COMPANY NOT LISTED") || dbSubStatus1.equalsIgnoreCase("DESIGNATION NOT OK") || dbSubStatus1.equalsIgnoreCase("SURROGATE NOT OK"))) {
                    subStatus1 = interesedSpinner.getSelectedItem().toString();
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

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            to_mail.setText(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""));
        }
        callerNumber.setOnFocusChangeListener(this);
        boolean gpsSettings = ApplicationSettings.getPref(AppConstants.GPS_SETTINGS, false);
        if (gpsSettings) {
            latLong = CommonUtils.getLatLong(this);
        }
        callerNumber.setFocusable(false);
        callerNumber.setClickable(false);
        callerNumber.setEnabled(false);
    }

    private void initDb() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        if (intent.hasExtra("db_id")) {
            id = intent.getLongExtra("db_id", 0);
        }
    }

    private void setLocalDbData() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        if (id != 0) {
            Cursor cur = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + id + "'", null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                dbStart = cur.getLong(cur.getColumnIndex("START_TIME"));
                dbEnd = cur.getLong(cur.getColumnIndex("END_TIME"));
                dbSubject = cur.getString(cur.getColumnIndex("SUBJECT"));
                dbNotes = cur.getString(cur.getColumnIndex("NOTES"));
                dbNotesImage = cur.getString(cur.getColumnIndex("NOTES_IMAGE"));
                dbLocation = cur.getString(cur.getColumnIndex("LOCATION"));
                dbTravelTime = cur.getLong(cur.getColumnIndex("TRAVEL_TIME"));
                dbCompanyName = cur.getString(cur.getColumnIndex("COMPANY_NAME"));
                dbDesignation = cur.getString(cur.getColumnIndex("DESIGNATION"));
                toNumber = cur.getString(cur.getColumnIndex("TO1"));
                dbstatusString = cur.getString(cur.getColumnIndex("STATUS"));
                callRec_url = cur.getString(cur.getColumnIndex("CALLREC_URL"));
                toname = cur.getString(cur.getColumnIndex("TONAME"));
                appointmentId = cur.getString(cur.getColumnIndex("APPOINTMENT_ID"));
                String websiteString = cur.getString(cur.getColumnIndex("WEBSITE"));
                imageUrl = cur.getString(cur.getColumnIndex("NOTES_IMAGE"));
                dbSubStatus2 = cur.getString(cur.getColumnIndex("SUBSTATUS2"));
                dbSubStatus1 = cur.getString(cur.getColumnIndex("SUBSTATUS1"));
                rnrCount = cur.getString(cur.getColumnIndex("RNR_COUNT"));

                String lead_source = cur.getString(cur.getColumnIndex("LEAD_SOURCE"));
                if (lead_source != null) {
                    leadSource.setText(lead_source);
                }
                String product_type = cur.getString(cur.getColumnIndex("PRODUCT_TYPE"));
                if (product_type != null) {
                    productType.setText(product_type);
                }

                String company_Address = cur.getString(cur.getColumnIndex("COMPANY_ADDRESS"));
                if (company_Address != null)
                    companyAddress.setText(company_Address);
                if (websiteString != null) {
                    websiteCustomItem.getEdittext().setText(websiteString);
                }

                String emailIdString = cur.getString(cur.getColumnIndex("EMAILID"));
                if (emailIdString != null) {
                    emailId.setText(emailIdString);
                }

                String orderValue = cur.getString(cur.getColumnIndex("ORDER_POTENTIAL"));
                if (orderValue != null) {
                    if(orderValue.equalsIgnoreCase("null")) {
                        orderPotential.setText(" ");
                    } else {
                        orderPotential.setText(orderValue);
                    }
                }

                if( cur.getString(cur.getColumnIndex("ALARMSETTO")) != null) {
                    String alarmSetTo = cur.getString(cur.getColumnIndex("ALARMSETTO"));
                    if(alarmSetTo != null) {
                        setAlarmBeforeSpinner(alarmSetTo);
                    }
                }

                start_date = findViewById(R.id.date1);
                String date1 = DateFormat.format("dd-MMM-yyyy", dbStart).toString();
                start_date.setText(date1);
                start_date.setOnClickListener(this);
                start_time = findViewById(R.id.time1);
                String time1 = DateFormat.format("hh:mm a", dbStart).toString();
                start_time = findViewById(R.id.time1);
                start_time.setText(time1);
                start_time.setOnClickListener(this);
                subject = findViewById(R.id.subjectEdittext);
                subject.setText(dbSubject);

                notes = findViewById(R.id.personalNotesEdittext);
                if(dbNotes != null) {
                    if(dbNotes.equalsIgnoreCase("null")) {
                        notes.setText("");
                    } else {
                        notes.setText(dbNotes);
                    }
                }

                companyName = findViewById(R.id.companyCustomItem);
                companyName.setText(dbCompanyName);

                designation = findViewById(R.id.designationCustomItem);
                designation.setText(dbDesignation);

                customItem = findViewById(R.id.locationCustomItem);
                location = customItem.getEdittext();
                location.setText(dbLocation);

                if (dbLocation != null) {
                    CommonUtils.initMapWebview(mapWebView, dbLocation);
                } else {
                    CommonUtils.initMapWebview(mapWebView, "Bengaluru");
                }
                if (toname != null) {
                    callerName.setText(toname);
                }
                if (toNumber != null) {
                    callerNumber.setText(toNumber);
                }
                initTimeVariables();
            }
            cur.close();
        }

        if(company.equals("OYO")) {
            String[] strings = {"Already Booked", "Booking Lost", "New Booking", "Not Interested", "Call Back Later", "Language Barrier", "Others"};
            for (int i = 0; i < strings.length; i++) {
                arrayList.add(strings[i]);
            }

        } else {
            SalesStageInfo salesStageInfo = SmarterSMBApplication.salesStageInfo;
            if (salesStageInfo != null) {
                arrayList = salesStageInfo.getAppointmentSalesStage();
            } else {
                String[] strings = {"Initial", "Contacted", "Contact in future", "Not interested", "Interested", "Proposal sent", "Order placed"};
                for (int i = 0; i < strings.length; i++) {
                    arrayList.add(strings[i]);
                }
            }
        }

        spinnerDuration = findViewById(R.id.spinner_duration);
        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this,R.array.spinner_duration, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);
        spinnerDuration.setOnItemSelectedListener(this);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(this);

        if (dbstatusString != null)
            setStatusSelection(dbstatusString);
        ArrayList<GroupsUserInfo> groupsUserInfos = CommonUtils.getGroupsUserInfoFromDB(this);
        arrayAdapter = new FilteredArrayAdapter<GroupsUserInfo>(this, R.layout.customfor_teammembers_autocomplete, groupsUserInfos, this) {
            @Override
            protected boolean keepObject(GroupsUserInfo obj, String mask) {
                mask = mask.toLowerCase();
                return obj.name.toLowerCase().startsWith(mask) || obj.email.toLowerCase().startsWith(mask) || obj.phone.startsWith(mask);
            }
        };
        assign_to.setAdapter(arrayAdapter);

        if (getIntent().hasExtra("FloatingAction")) {
            check = getIntent().getBooleanExtra("FloatingAction", true);
        }

        if (location != null) {
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
        }
        if(dbSubStatus1 != null && (dbSubStatus1.equalsIgnoreCase("PAN APPROVED") || dbSubStatus1.equalsIgnoreCase("TL APPROVED") || dbSubStatus1.equalsIgnoreCase("LEAD SHEET INCOMPLETE") || dbSubStatus1.equalsIgnoreCase("COMPANY NOT LISTED") || dbSubStatus1.equalsIgnoreCase("DESIGNATION NOT OK") || dbSubStatus1.equalsIgnoreCase("SURROGATE NOT OK"))) {
            interesedSpinner.setVisibility(View.GONE);
            subStatus2text.setText(dbSubStatus1);
            subStatus2text.setVisibility(View.VISIBLE);
            subStatus1 = dbSubStatus1;
        }
    }

    private void setAlarmBeforeSpinner(String status) {
        int selection = 0;
        for (int i = 0; i < alarmarrayList.size(); i++) {
            if (status != null && status.equals(alarmarrayList.get(i))) {
                selection = i;
            }
        }
        spinner.setSelection(selection);
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
        imageDialog.setPositiveButton(
                "OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        imageDialog.create();
        imageDialog.show();
    }

    private List<String> getAssignList() {
        groupsUserInfos = CommonUtils.getGroupsUserInfoFromDB(this);
        List<String> list = new ArrayList<>();
        list.add("Optional");
        for (int i = 0; i < groupsUserInfos.size(); i++) {
            GroupsUserInfo groupsUserInfo = groupsUserInfos.get(i);
            String pan = "PAN@";
            String pan1 = "PAN1@";
            String tl = "TL";
            String sme = "SME@";
            String sme1 = "SME1@";
            String cibil1 = "CIBIL";
            String cibil2= "CIBIL1@";
            if (groupsUserInfo.email.contains(pan) || groupsUserInfo.email.contains(pan1)) {
                panList.add(groupsUserInfo.email);
                list.add(groupsUserInfo.name);
            } else if (groupsUserInfo.email.contains(sme) || groupsUserInfo.email.contains(sme1)) {
                smeList.add(groupsUserInfo.email);
                list.add(groupsUserInfo.name);
            } else if (groupsUserInfo.email.contains(tl)) {
                qdeList.add(groupsUserInfo.email);
                list.add(groupsUserInfo.name);
            } else if (groupsUserInfo.email.contains(cibil1) || groupsUserInfo.email.contains(cibil2)) {
                cibilList.add(groupsUserInfo.email);
                list.add(groupsUserInfo.name);
            }
        }
        return list;
    }

    private String getAssignToMail() {
        getAssignList();
        String assignTo = "";
        if(assign_to_email != null && !assign_to_email.isEmpty()) {
            assignTo = assign_to_email;
        } else if(status != null && status.equalsIgnoreCase("INTERESTED")) {
            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                if (screen != null && screen.equalsIgnoreCase("Bank1AfterCallActivity")) {
                    if (subStatus1 != null && subStatus1.equalsIgnoreCase("PAN AVAILABLE")) {
                        if (panList != null && panList.size() > 0) {
                            assignTo = panList.get(0);
                        }
                    } else if (subStatus1 != null && (subStatus1.equalsIgnoreCase("PAN APPROVED") || subStatus1.equalsIgnoreCase("LEAD SHEET INCOMPLETE") || subStatus1.equalsIgnoreCase("COMPANY NOT LISTED") || subStatus1.equalsIgnoreCase("DESIGNATION NOT OK") || subStatus1.equalsIgnoreCase("SURROGATE NOT OK"))) {
                        if (smeList != null && smeList.size() > 0) {
                            assignTo = smeList.get(0);
                        } else if (cibilList != null && cibilList.size() > 0) {
                            assignTo = cibilList.get(0);
                        }
                    } else if (subStatus1 != null && (subStatus1.equalsIgnoreCase("TL APPROVED") || subStatus1.equalsIgnoreCase("REPEAT ENTRY") || subStatus1.equalsIgnoreCase("CIBIL NOT ELIGIBLE") || subStatus1.equalsIgnoreCase("LOW CIBIL") || subStatus1.equalsIgnoreCase("DEFAULTER"))) {
                        if(cibilList != null && cibilList.size() > 0){
                            assignTo = cibilList.get(0);
                        }
                    }
                }
            }
        }
        return assignTo;
    }

    private void createFirstCallFollowup() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
            if (screen != null && screen.equalsIgnoreCase("Bank1AfterCallActivity")) {
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
                if (status != null) {
                    cv.put("STATUS", status);
                }
                cv.put("UPLOAD_STATUS", 0);
                cv.put("ALARMSETTO", 0);
                cv.put("COMPANY_ADDRESS", "");
                cv.put("PRODUCT_TYPE", "");
                cv.put("ORDER_POTENTIAL", "");
                cv.put("LEAD_SOURCE", "");
                cv.put("FLP_COUNT", 0);
                cv.put("COMPLETED", 1);
                if (subStatus1 != null) {
                    cv.put("SUBSTATUS1", subStatus1);
                }
                if (subStatus2 != null) {
                    cv.put("SUBSTATUS2", subStatus2);
                }
                if (rnrCount != null) {
                    cv.put("RNR_COUNT", rnrCount);
                }
                if (imageUrl != null) {
                    cv.put("NOTES_IMAGE", imageUrl);
                }
                if (callerNumber.getText().toString() != null) {
                    cv.put("TO1", callerNumber.getText().toString());
                }
                if (toname != null) {
                    cv.put("TONAME", toname);
                }
                cv.put("RESPONSE_STATUS", "completed");
                db.insert("remindertbNew", null, cv);
            }
        }
    }
}
