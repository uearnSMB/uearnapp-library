package smarter.uearn.money.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.appointment.AppointmentViewActivity;
import smarter.uearn.money.activities.appointment.ViewCalendarActivity;
import smarter.uearn.money.adapters.FlpCursorAdapter;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.ReuploadService;

public class UearnCompletedActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    ListView flp_list;
    private FlpCursorAdapter flpCursorAdapter;
    private LinearLayout toolbar_head_view;
    private SQLiteDatabase db;
    public static Long start = 0l, end = 0l;
    private String selectedLeadSource = "All";
    private TextView done_count, toolbar_spinnerTitle, actionbar_title;
    private MenuItem sync, search;
    private MenuItem searchMenuItem;
    private Spinner toolbar_activity_spinner;
    public static String start_time = null, end_time = null;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_uearn_completed);
        flp_list = findViewById(R.id.flp_list);
        flp_list.setOnItemClickListener(this);
        done_count = findViewById(R.id.done_count);
        changeStatusBarColor(this);
        selectedLeadSource = "All";

        SmarterSMBApplication.currentActivity = this;
        initCalender();

        Toolbar toolbar = findViewById(R.id.tool_bar1);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        }

        toolbar_head_view = findViewById(R.id.toolbar_head_view);
        toolbar_head_view.setOnClickListener(this);
        actionbar_title = findViewById(R.id.actionbar_title);
        actionbar_title.setText("Done");

        try {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            new CustomTwoButtonDialog().signOut(UearnCompletedActivity.this);
        }
        reactIntent();
        createSpinner();
        getLocalDbFlpCounts();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.toolbar_head_view:
                toolbar_activity_spinner.setEnabled(false);
                toolbar_activity_spinner.performClick();
                toolbar_activity_spinner.setClickable(false);
                toolbar_activity_spinner.setFocusable(true);
                toolbar_activity_spinner.setFocusableInTouchMode(true);
                toolbar_activity_spinner.requestFocus();
                break;
        }
    }

    private void initCalender() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        start = c.getTimeInMillis();
        start_time = CommonUtils.getTimeFormatInISO(c.getTime());
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
        end = c.getTimeInMillis();
        end_time = CommonUtils.getTimeFormatInISO(c.getTime());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        } else if (itemId == R.id.syncItem) {
            String selectedDateFromSpinner = "TODAY";
            if(selectedDateFromSpinner != null && !selectedDateFromSpinner.isEmpty()) {
                if(selectedDateFromSpinner.equalsIgnoreCase("TODAY")) {
                    initCalender();
                } else if(selectedDateFromSpinner.equalsIgnoreCase("YESTERDAY")) {
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(new Date());
                    calendar2.set(Calendar.DAY_OF_YEAR, calendar2.get(Calendar.DAY_OF_YEAR) - 1);
                    calendar2.set(Calendar.HOUR_OF_DAY, 0);
                    calendar2.set(Calendar.MINUTE, 0);
                    calendar2.set(Calendar.SECOND, 0);
                    calendar2.set(Calendar.MILLISECOND, 0);
                    start = calendar2.getTimeInMillis();
                    start_time = CommonUtils.getTimeFormatInISO(new Date(start));
                }
            }

            if (start_time != null && end_time != null) {
                item.setVisible(false);
                Toast.makeText(this, "Syncing...", Toast.LENGTH_SHORT).show();
                if (ApplicationSettings.containsPref(AppConstants.DATA_START_TIME)) {
                    long start = ApplicationSettings.getPref(AppConstants.DATA_START_TIME, 0l);
                    setSync(start, 0);
                } else {
                    getSmartUserCalendarDataFromServer(start_time, end_time, 0);
                }
                Intent intent = new Intent(this, ReuploadService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSmartUserCalendarDataFromServer(final String startTime, final String endTime, final int nextgetcount) {
        Log.d("StartEndTime", "SMBHOME getSmartUserCalendarDataFromServer() - Start: " + start + "Start Time: " + startTime + "End: " + end + "End Time: " + endTime);
        if (CommonUtils.isNetworkAvailable(this)) {
            final RotateAnimation rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(1000);
            rotate.setRepeatCount(ValueAnimator.INFINITE);
            String user_id = "";
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            }

            final GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(user_id, startTime, endTime, "smartercalendar");
            getCalendarEntryInfo.limit = nextgetcount;
            new APIProvider.Get_CalendarEvents(getCalendarEntryInfo, AppConstants.GET_CAL_EVENTS, new API_Response_Listener<ArrayList<GetCalendarEntryInfo>>() {
                @Override
                public void onComplete(ArrayList<GetCalendarEntryInfo> data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        new SaveToLocalDB(data, nextgetcount + 100).execute();
                    } else {
                        if (sync != null) {
                            sync.setVisible(true);
                        }
                    }
                    if (CommonUtils.notifier != null) {
                        CommonUtils.notifier.notify(true);
                    }
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                    String syncDate = "" + df.format(c.getTime());
                    if (syncDate != null) {
                        ApplicationSettings.putPref(AppConstants.SYNC_FOLLOWUP_DATE, syncDate);
                    }
                }
            }).call();
        } else {
            if (sync != null) {
                sync.setVisible(true);
            }
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    class SaveToLocalDB extends AsyncTask<ArrayList<GetCalendarEntryInfo>, Void, Void> {
        ArrayList<GetCalendarEntryInfo> getCalendarEntryInfos;
        int nextgetcount;

        public SaveToLocalDB(ArrayList<GetCalendarEntryInfo> getCalendarEntryInfos, int nextCount) {
            if (getCalendarEntryInfos.size() != 0) {

            }
            this.getCalendarEntryInfos = getCalendarEntryInfos;
            this.nextgetcount = nextCount;
        }

        @Override
        protected Void doInBackground(ArrayList<GetCalendarEntryInfo>... params) {
            CommonUtils.saveCalendarSyncDataToLocalDB(db, start, end, getApplicationContext(), getCalendarEntryInfos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (ApplicationSettings.containsPref(AppConstants.DATA_START_TIME)) {
                long start = ApplicationSettings.getPref(AppConstants.DATA_START_TIME, 0l);
                setSync(start, nextgetcount);
            } else {
                if (start_time != null && end_time != null) {
                    getSmartUserCalendarDataFromServer(start_time, end_time, nextgetcount);
                }
            }
            super.onPostExecute(aVoid);
        }
    }

    private void setSync(long startValue, int count) {
        String time = CommonUtils.getTimeFormatInISO(new Date(startValue));
        long pastValue = startValue - (7 * 24 * 60 * 60 * 1000);
        if (startValue > start && startValue < end) {
            getSmartUserCalendarDataFromServer(time, end_time, count);
        } else if (pastValue > start) {
            getSmartUserCalendarDataFromServer(time, end_time, count);

        } else if (start > startValue) {
            getSmartUserCalendarDataFromServer(time, end_time, count);
        } else {
            if (sync != null) {
                sync.setVisible(true);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
        String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
        if (questionsAct != null && !questionsAct.isEmpty() && ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE")))) {

        } else {
            ApplicationSettings.putPref(AppConstants.CHECK_TIMER, true);
            Intent intent = new Intent(this, AppointmentViewActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("disable_ok", true);
            this.startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (ApplicationSettings.containsPref(AppConstants.FK_CONTROL)) {
            boolean fkControl = ApplicationSettings.getPref(AppConstants.FK_CONTROL, false);
            if (fkControl) {
                getMenuInflater().inflate(R.menu.dashboard_fk_ib_menu, menu);
            } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                if (ibControl) {
                    getMenuInflater().inflate(R.menu.dashboard_fk_ib_menu, menu);
                } else {
                    getMenuInflater().inflate(R.menu.dashboard_menu, menu);
                }
            } else {
                getMenuInflater().inflate(R.menu.dashboard_menu, menu);
            }
        } else if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
            boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
            if (ibControl) {
                getMenuInflater().inflate(R.menu.dashboard_fk_ib_menu, menu);
            } else {
                getMenuInflater().inflate(R.menu.dashboard_menu, menu);
            }
        } else {
            getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        }
        sync = menu.findItem(R.id.syncItem);
        searchMenuItem = menu.findItem(R.id.searchAppointment);
        searchMenuItem.setVisible(true);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.searchAppointment);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        try {
            EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.white));
            searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFollowup(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFollowup(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchMenuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        searchView.setQuery("", false);
                        return true;
                    }
                });

        return super.onCreateOptionsMenu(menu);
    }

    private void createSpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("TODAY");
        categories.add("YESTERDAY");
        categories.add("THIS MONTH");
        toolbar_activity_spinner = findViewById(R.id.toolbar_activity_spinner1);
        if (getIntent().hasExtra("firstcall")) {
            toolbar_activity_spinner.setSelection(4);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_tv, categories) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(16);
                return v;
            }
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(R.layout.spinner_tv);
        toolbar_activity_spinner.setAdapter(dataAdapter);
        toolbar_spinnerTitle = findViewById(R.id.toolbar_spinnerTitle);
        toolbar_spinnerTitle.setText("TODAY");
        toolbar_activity_spinner.setOnItemSelectedListener(this);
        toolbar_activity_spinner.requestFocus();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.activity_spinner1 || spinner.getId() == R.id.toolbar_activity_spinner1) {
            switch (i) {
                case 0:
                    toolbar_spinnerTitle.setText("TODAY");
                    initializeCalenderDateAndTime();
                    getLocalDbFlpCounts();
                    check = false;
                    break;
                case 1:
                    toolbar_spinnerTitle.setText("YESTERDAY");
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(new Date());
                    calendar2.set(Calendar.DAY_OF_YEAR, calendar2.get(Calendar.DAY_OF_YEAR) - 1);
                    calendar2.set(Calendar.HOUR_OF_DAY, 0);
                    calendar2.set(Calendar.MINUTE, 0);
                    calendar2.set(Calendar.SECOND, 0);
                    calendar2.set(Calendar.MILLISECOND, 0);
                    start = calendar2.getTimeInMillis();
                    end = start + (24 * 60 * 60 * 1000);
                    end = end - (60 * 1000);
                    start_time = CommonUtils.getTimeFormatInISO(new Date(start));
                    end_time = CommonUtils.getTimeFormatInISO(new Date(end));
                    check = false;
                    getLocalDbFlpCounts();
                    break;
                case 2:
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(new Date());
                    calendar3.set(Calendar.DAY_OF_YEAR, calendar3.get(Calendar.DAY_OF_YEAR) - 7);
                    toolbar_spinnerTitle.setText("LAST 30 DAYS");
                    calendar3.set(Calendar.HOUR_OF_DAY, 0);
                    calendar3.set(Calendar.MINUTE, 0);
                    calendar3.set(Calendar.SECOND, 0);
                    calendar3.set(Calendar.MILLISECOND, 0);
                    start = calendar3.getTimeInMillis();
                    end = System.currentTimeMillis();
                    start_time = CommonUtils.getTimeFormatInISO(new Date(start));
                    end_time = CommonUtils.getTimeFormatInISO(new Date(end));
                    check = false;
                    getLocalDbFlpCounts();
                    break;
                case 3:
                    Calendar calendar4 = Calendar.getInstance();
                    calendar4.setTime(new Date());
                    calendar4.set(Calendar.DAY_OF_YEAR, calendar4.get(Calendar.DAY_OF_YEAR) + 30);
                    calendar4.set(Calendar.HOUR_OF_DAY, 0);
                    calendar4.set(Calendar.MINUTE, 0);
                    calendar4.set(Calendar.SECOND, 0);
                    calendar4.set(Calendar.MILLISECOND, 0);
                    start = System.currentTimeMillis();
                    end = calendar4.getTimeInMillis();
                    start_time = CommonUtils.getTimeFormatInISO(new Date(start));
                    end_time = CommonUtils.getTimeFormatInISO(new Date(end));
                    toolbar_spinnerTitle.setText("NEXT 30 DAYS");
                    getLocalDbFlpCounts();
                    check = false;
                    break;

                case 4:
                    if (!check) {
                        Intent intent2 = new Intent(this, ViewCalendarActivity.class);
                        intent2.putExtra("firstcall", 2);
                        startActivityForResult(intent2, 111);
                        check = false;
                    }
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void searchFollowup(String searchContent) {
        flp_list.setVisibility(View.VISIBLE);
        if (searchContent != null && !searchContent.equalsIgnoreCase("")) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String selection = "TO1 LIKE '%" + searchContent + "%' OR SUBJECT LIKE '%" + searchContent + "%' OR LOCATION LIKE '%" + searchContent + "%' OR COMPANY_NAME LIKE '%" + searchContent + "%' OR EMAILID LIKE '%" + searchContent + "%' OR TONAME LIKE '%" + searchContent + "%' OR WEBSITE LIKE '%" + searchContent + "%' OR STATUS LIKE '%" + searchContent + "%' OR SUBSTATUS1 LIKE '%" + searchContent + "%' OR SUBSTATUS2 LIKE '%" + searchContent + "%'";
            Cursor cursor3 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");
            if (cursor3 != null) {
                if (flpCursorAdapter != null) {
                    flp_list.setVisibility(View.VISIBLE);
                    flpCursorAdapter.changeCursor(cursor3);
                    flpCursorAdapter.notifyDataSetChanged();
                    flp_list.setAdapter(flpCursorAdapter);
                } else {
                    flpCursorAdapter = new FlpCursorAdapter(this,cursor3,0,1);
                    flp_list.setAdapter(flpCursorAdapter);
                }
            }
        } else {
            getLocalDbFlpCounts();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public void changeStatusBarColor(Activity activity) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }

    public void restoreActionBar(String mTitle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dd374c")));
    }


    @Override
    protected void onResume() {
        flp_list.setVisibility(View.VISIBLE);
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    private void getLocalDbFlpCounts() {
        Cursor done = getFlpDoneCursor();
        if (done != null) {
            if (flpCursorAdapter != null) {
                flp_list.setVisibility(View.VISIBLE);
                flpCursorAdapter.changeCursor(done);
                flpCursorAdapter.notifyDataSetChanged();
                flp_list.setAdapter(flpCursorAdapter);
            } else {
                flpCursorAdapter = new FlpCursorAdapter(UearnCompletedActivity.this,done,1,1);
                flp_list.setAdapter(flpCursorAdapter);
            }
        }
    }

    private Cursor getFlpDoneCursor() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        int donecount = 1;
        String selection3 = "UPDATED_AT" + ">=" + start + " AND " + "UPDATED_AT" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND (ASSIGN_TO IS NULL OR ASSIGN_TO = '')";
        String doneCardQuery = ApplicationSettings.getPref(AppConstants.DONE_CARD_QUERY, "");
        if(doneCardQuery != null && !doneCardQuery.isEmpty()) {
            selection3 = selection3 + doneCardQuery;
        }

        if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
            selection3 =  selection3 + " AND" + " LEAD_SOURCE = " +  "'" + selectedLeadSource + "'";

        Cursor doneUnique = db.query("remindertbNew", null, selection3, null, null, null, "UPDATED_AT DESC");
        if (doneUnique != null) {
            if (doneUnique.getCount() > 0) {
                donecount = doneUnique.getCount();
                    done_count.setText("" + donecount);
            } else {
                    done_count.setText("" + 0);
            }
            doneUnique.close();
        }

        Cursor done = db.query("remindertbNew", null, selection3, null, "TO1", null, "UPDATED_AT DESC");
        return done;
    }

    private void reactIntent() {
        initializeCalenderDateAndTime();

        Intent intent = getIntent();
        if (intent.hasExtra("start")) {
            start = intent.getLongExtra("start", 0);
        }
        if (intent.hasExtra("end")) {
            end = intent.getLongExtra("end", 0);
        }
    }

    private void initializeCalenderDateAndTime() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        start = c.getTimeInMillis();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
        end = c.getTimeInMillis();
    }
}
