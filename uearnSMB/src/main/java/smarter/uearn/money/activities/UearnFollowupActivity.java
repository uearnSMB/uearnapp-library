package smarter.uearn.money.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.appointment.AppointmentViewActivity;
import smarter.uearn.money.activities.appointment.ViewCalendarActivity;
import smarter.uearn.money.adapters.FlpCursorAdapter;
import smarter.uearn.money.broadcastReceiver.Alarm_Receiver;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.fragments.AutoDillerFragment;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.services.AcceptOrRejectNotificationService;
import smarter.uearn.money.services.CalldataSendService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.CustomSpinner;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ResponseInterface;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.webservice.Constants;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;
import smarter.uearn.money.utils.webservice.WebService;
import smarter.uearn.money.views.events.HeadPhoneReciever;

public class UearnFollowupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private FlpCursorAdapter flpCursorAdapter;
    private ListView flp_list;
    private TextView noDataTextView;
    private LinearLayout main_layout, fragmentLayout, head_view, toolbar_head_view;
    private RelativeLayout fView, oView, dView, start_layout;
    int v = 0, selection = 0;
    private SQLiteDatabase db;
    private String selectedLeadSource = "";
    public static String start_time = null, end_time = null;
    static Long start = 0L, end = 0L;
    private String dateForActionBar = "";
    public static boolean check = false;
    private boolean checkFirst = false;
    private boolean allInPast = false;
    private TextView flp_todo, flp_overdue, flp_done, flp_first_call, actionbar_title;
    private Animation flpMissedAnimation = null;
    private int flp_count = 0;
    private MenuItem sync, search;
    private MenuItem searchMenuItem;
    private ImageView ivSyncData;
    private CustomSpinner toolbar_activity_spinner;
    private Spinner activity_spinner;
    private TextView spinnerTitle, toolbar_spinnerTitle;
    private Button startButton;
    public static boolean manualDialing = false;
    public static boolean dialingFromList = false;
    public static boolean autoDial = false;
    private boolean remoteAutoDialingStarted = false;
    private boolean sendRemoteDialStartRequest = false;
    private boolean checkAuto = false;
    private FragmentManager fragmentManager;
    private LinearLayout flp_layout;
    private String followUpType = "";
    String selectedDateFromSpinner = "TODAY";
    //private HeadPhoneReciever myReceiver;
    private Activity activity = null;
    FloatingActionButton manualDial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_uearn_followup);
        activity = this;
        selectedLeadSource = "All";
        SmarterSMBApplication.currentActivity = this;
        try {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }  catch (Exception e) {
            new CustomTwoButtonDialog().signOut(UearnFollowupActivity.this);
        }
        flp_count = 1;
        changeStatusBarColor(this);

        Intent intent = getIntent();
        followUpType = intent.getStringExtra("FollowUpType");
        initUi();
//        try{
//            if (myReceiver != null) {
//                IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
//                registerReceiver(myReceiver, filter);
//            }
//        } catch (Exception e){
//
//        }
        createSpinner();
        reactIntent();
    }

    private void getFirstcallFollowup() {
        if(!db.isOpen()){
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }

        Cursor firstCallFollowusps = getFirstCallCursor();
        if (firstCallFollowusps != null && firstCallFollowusps.getCount() > 0) {
            if (flpCursorAdapter != null) {
                flp_list.setVisibility(View.VISIBLE);
                noDataTextView.setVisibility(View.GONE);
                flpCursorAdapter.changeCursor(firstCallFollowusps);
                flpCursorAdapter.notifyDataSetChanged();
                flp_list.setAdapter(flpCursorAdapter);
            } else {
                flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this,firstCallFollowusps,0,0);
                flp_list.setAdapter(flpCursorAdapter);
                flpCursorAdapter.notifyDataSetChanged();
                flp_list.setVisibility(View.GONE);
            }
        } else {
            flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this,firstCallFollowusps,0,0);
            flp_list.setAdapter(flpCursorAdapter);
            flpCursorAdapter.notifyDataSetChanged();
            flp_list.setVisibility(View.GONE);
        }
    }

    private void getFollowUpToDO() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        Cursor followUpToDo = getFlpTodoCursor(0);
        if (followUpToDo != null && followUpToDo.getCount() > 0) {
            if (flpCursorAdapter != null) {
                flp_list.setVisibility(View.VISIBLE);
                flpCursorAdapter.changeCursor(followUpToDo);
                flpCursorAdapter.notifyDataSetChanged();
                flp_list.setAdapter(flpCursorAdapter);
                noDataTextView.setVisibility(View.GONE);
            } else {
                flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this,followUpToDo,0,0);
                flp_list.setAdapter(flpCursorAdapter);
                flpCursorAdapter.notifyDataSetChanged();
                flp_list.setVisibility(View.GONE);
            }
        } else {
            flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this,followUpToDo,0,0);
            flp_list.setAdapter(flpCursorAdapter);
            flpCursorAdapter.notifyDataSetChanged();
            flp_list.setVisibility(View.GONE);
            noDataTextView.setVisibility(View.VISIBLE);
        }
    }

    private void getMissedFollop() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        Cursor missedCursor = getFlpMissedCursor(0);
        if (missedCursor != null && missedCursor.getCount() > 0) {
            flp_list.setVisibility(View.VISIBLE);
            if (flpCursorAdapter != null) {
                flp_list.setVisibility(View.VISIBLE);
                flpCursorAdapter.changeCursor(missedCursor);
                flpCursorAdapter.notifyDataSetChanged();
                flp_list.setAdapter(flpCursorAdapter);
                noDataTextView.setVisibility(View.GONE);
            } else {
                flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this,missedCursor,0,0);
                flp_list.setAdapter(flpCursorAdapter);
                flpCursorAdapter.notifyDataSetChanged();
                flp_list.setVisibility(View.GONE);
            }
        } else {
            flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this,missedCursor,0,0);
            flp_list.setAdapter(flpCursorAdapter);
            flpCursorAdapter.notifyDataSetChanged();
            flp_list.setVisibility(View.GONE);
            noDataTextView.setVisibility(View.VISIBLE);
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
        searchView.setBackgroundColor(getResources().getColor(R.color.white));
        try {
            EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(getResources().getColor(R.color.goal_text));
            searchEditText.setHintTextColor(getResources().getColor(R.color.goal_text));
            searchEditText.setBackgroundColor(getResources().getColor(R.color.white));
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

                return true;
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

    private void searchFollowup(String searchContent) {
        flp_list.setVisibility(View.VISIBLE);
        if (searchContent != null && !searchContent.equalsIgnoreCase("")) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String selection = "TO1 LIKE '%" + searchContent + "%' OR SUBJECT LIKE '%" + searchContent + "%' OR LOCATION LIKE '%" + searchContent + "%' OR COMPANY_NAME LIKE '%" + searchContent + "%' OR EMAILID LIKE '%" + searchContent + "%' OR TONAME LIKE '%" + searchContent + "%' OR WEBSITE LIKE '%" + searchContent + "%' OR STATUS LIKE '%" + searchContent + "%' OR SUBSTATUS1 LIKE '%" + searchContent + "%' OR SUBSTATUS2 LIKE '%" + searchContent + "%'";
            Cursor cursor3 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");
            if (cursor3 != null && cursor3.getCount() > 0) {
                if (flpCursorAdapter != null) {
                    noDataTextView.setVisibility(View.GONE);
                    flp_list.setVisibility(View.VISIBLE);
                    flpCursorAdapter.changeCursor(cursor3);
                    flpCursorAdapter.notifyDataSetChanged();
                    flp_list.setAdapter(flpCursorAdapter);
                } else {
                    flpCursorAdapter = new FlpCursorAdapter(this,cursor3,0,1);
                    flp_list.setAdapter(flpCursorAdapter);
                    flpCursorAdapter.notifyDataSetChanged();
                }
            } else {
                flp_list.setVisibility(View.GONE);
                noDataTextView.setVisibility(View.VISIBLE);
            }
        } else {
            if(followUpType.equals("FollowUpToDo")) {
                selection = 1;
                setCardColors(1);
            } else if(followUpType.equals("FirstCall")) {
                selection = 4;
                setCardColors(4);
            }
            else {
                selection = 2;
                setCardColors(2);
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

    public void restoreActionBar(String mTitle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dd374c")));
    }

    private void createCursorAdaptor(Cursor cursor) {
        flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this, cursor,0,0);
        flp_list.setAdapter(flpCursorAdapter);
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
            navigateToUearnHome();
        } else if (itemId == R.id.syncItem) {
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
                syncSettings();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void syncSettings() {
        Toast.makeText(this, "Syncing...", Toast.LENGTH_SHORT).show();
        ServiceUserProfile.getUsersTeam();
        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        String[] data = {Constants.GET, Constants.DOMAIN_URL + Constants.SETTINGS_STAGES + userId};
        CommonUtils.checkToken();
        WebService webCall = new WebService(7, new ResponseInterface() {
            @Override
            public boolean getResponseStatus(boolean result) {
                return true;
            }
        });
        webCall.execute(data);
        if (userId != null) {
            getTeamMembersCall(userId);
        }
    }

    private void getTeamMembersCall(String id) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.Get_All_Team_Members(id, 535, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                @Override
                public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                    if (data != null) {
                        CommonUtils.saveTeamdataToLocalDB(UearnFollowupActivity.this, data);
                        for (int i = 0; i < data.size(); i++) {
                            String request = data.get(i).request;
                            if ((request != null) && request.equals("invited")) {
                                Intent intent = new Intent(UearnFollowupActivity.this, AcceptOrRejectNotificationService.class);
                                Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(data.get(i));
                                intent.putExtra("groupsUserInfoBundle", bundle);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(intent);
                                } else {
                                    startService(intent);
                                }
                            }
                        }
                    }
                }
            }).call();
        }
    }

    private void getSmartUserCalendarDataFromServer(final String startTime, final String endTime, final int nextgetcount) {
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.activity_spinner1 || spinner.getId() == R.id.toolbar_activity_spinner1 ) {
            switch (i) {
                case 0:
                    allInPast = false;
                    toolbar_spinnerTitle.setText("TODAY");
                    selectedDateFromSpinner = "TODAY";
                    initializeCalenderDateAndTime();
                    v = 0;
                    if(followUpType.equals("FollowUpToDo")) {
                        //getFollowUpToDO();
                    } else if (followUpType.equals("FirstCall")) {
                        getFirstcallFollowup();
                    } else {
                        getMissedFollop();
                        //getFollowUpToDO();
                    }
                    check = false;
                    break;
                case 1:
                    allInPast = false;
                    toolbar_spinnerTitle.setText("Y'DAY");
                    selectedDateFromSpinner = "YESTERDAY";
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
                    v = 2;
                    check = false;
                    getMissedFollop();
                    break;
                case 2:
                    allInPast = true;
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(new Date());
                    calendar3.set(Calendar.DAY_OF_YEAR, calendar3.get(Calendar.DAY_OF_YEAR) - 7);
                    //toolbar_spinnerTitle.setText("LAST 30 DAYS");
                    toolbar_spinnerTitle.setText("PAST");
                    selectedDateFromSpinner = "LAST 30 DAYS";
                    calendar3.set(Calendar.HOUR_OF_DAY, 0);
                    calendar3.set(Calendar.MINUTE, 0);
                    calendar3.set(Calendar.SECOND, 0);
                    calendar3.set(Calendar.MILLISECOND, 0);
                    start = calendar3.getTimeInMillis();
                    end = System.currentTimeMillis();
                    start_time = CommonUtils.getTimeFormatInISO(new Date(start));
                    end_time = CommonUtils.getTimeFormatInISO(new Date(end));
                    v = 1;
                    check = false;
                    getMissedFollop();
                    break;
                case 3:
                    allInPast = false;
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
                    toolbar_spinnerTitle.setText("LATER");
                    selectedDateFromSpinner = "NEXT 30 DAYS";
                    v = 2;
                    if(followUpType.equals("FollowUpToDo")) {
                        getFollowUpToDO();
                    } else if (followUpType.equals("FirstCall")) {
                        getFirstcallFollowup();
                    } else {
                        //getMissedFollop();
                        getFollowUpToDO();
                    }
                    check = false;
                    break;

                case 4:
                    allInPast = false;
                    if (!check) {
                        //finish();
                        Intent intent2 = new Intent(this, ViewCalendarActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent2.putExtra("FollowUpType", 3);
                        intent2.putExtra("FollowUpsType", followUpType);
                        startActivityForResult(intent2, 111);
                        check = false;
                    }
                    break;

                case 41:
                    allInPast = true;
                    Calendar calendar5 = Calendar.getInstance();
                    calendar5.setTime(new Date());
                    calendar5.set(Calendar.DAY_OF_YEAR, calendar5.get(Calendar.DAY_OF_YEAR) - 7);
                    toolbar_spinnerTitle.setText("LAST 30 DAYS");
                    selectedDateFromSpinner = "LAST 30 DAYS";
                    calendar5.set(Calendar.HOUR_OF_DAY, 0);
                    calendar5.set(Calendar.MINUTE, 0);
                    calendar5.set(Calendar.SECOND, 0);
                    calendar5.set(Calendar.MILLISECOND, 0);
                    start = calendar5.getTimeInMillis();
                    end = System.currentTimeMillis();
                    start_time = CommonUtils.getTimeFormatInISO(new Date(start));
                    end_time = CommonUtils.getTimeFormatInISO(new Date(end));
                    v = 1;
                    check = false;
                    if(followUpType.equals("FollowUpToDo")) {
                        getFollowUpToDO();
                    } else if (followUpType.equals("FirstCall")) {
                        getFirstcallFollowup();
                    } else {
                        //getMissedFollop();
                        getFollowUpToDO();
                    }
                    break;
                case 6:
                    allInPast = false;
                    Calendar calendar6 = Calendar.getInstance();
                    calendar6.setTime(new Date());
                    calendar6.set(Calendar.DAY_OF_YEAR, calendar6.get(Calendar.DAY_OF_YEAR) + 30);
                    calendar6.set(Calendar.HOUR_OF_DAY, 0);
                    calendar6.set(Calendar.MINUTE, 0);
                    calendar6.set(Calendar.SECOND, 0);
                    calendar6.set(Calendar.MILLISECOND, 0);
                    start = System.currentTimeMillis();
                    end = calendar6.getTimeInMillis();
                    start_time = CommonUtils.getTimeFormatInISO(new Date(start));
                    end_time = CommonUtils.getTimeFormatInISO(new Date(end));
                    //toolbar_spinnerTitle.setText("NEXT 30 DAYS");
                    toolbar_spinnerTitle.setText("LATER");
                    selectedDateFromSpinner = "NEXT 30 DAYS";
                    v = 2;
                    if(followUpType.equals("FollowUpToDo")) {
                        getFollowUpToDO();
                    } else if (followUpType.equals("FirstCall")) {
                        getFirstcallFollowup();
                    } else {
                        //getMissedFollop();
                        getFollowUpToDO();
                    }
                    check = false;
                    break;

                case 7:
                    allInPast = false;
                    if (!check) {
                        check = false;
                        Intent intent2 = new Intent(this, ViewCalendarActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent2.putExtra("FollowUpType", followUpType);
                        startActivityForResult(intent2, 111);
                        //this.finish();
                    }
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
    protected void onResume() {
        if(followUpType != null && followUpType.equals("FollowUpToDo")) {
            getFollowUpToDO();
        } else if (followUpType != null && followUpType.equals("FirstCall")) {
            getFirstcallFollowup();
        } else {
            getMissedFollop();
            //getFollowUpToDO();
        }
        flp_list.setVisibility(View.VISIBLE);
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);

        if(SmarterSMBApplication.isDiallingFromDoneList || SmarterSMBApplication.manualDialFromHomeScreen){
            SmarterSMBApplication.isDiallingFromDoneList = false;
            SmarterSMBApplication.manualDialFromHomeScreen = false;
            navigateToUearnHome();
        }
    }

    private void navigateToUearnHome() {
        SmarterSMBApplication.navigatingToUearnHome = true;
        Intent intent = new Intent (this, UearnHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed()
    {
        if(SmarterSMBApplication.isDiallingFollowUpC2C){
            SmarterSMBApplication.isDiallingFollowUpC2C = false;
            navigateToUearnHome();
        } else {
            navigateToUearnHome();
        }
    }

    private void initUi() {
        fragmentLayout = findViewById(R.id.fragmentLayout);
        flp_todo = findViewById(R.id.flp_todo);
        flp_overdue = findViewById(R.id.flp_overdue);
        flp_done = findViewById(R.id.flp_done);
        flp_first_call = findViewById(R.id.flp_first_call);
        fView = findViewById(R.id.f_view);
        dView = findViewById(R.id.d_view);
        oView = findViewById(R.id.o_view);
        flp_list = findViewById(R.id.flp_list);
        noDataTextView = findViewById(R.id.noDataTextView);
        flp_list.setOnItemClickListener(this);
        main_layout = findViewById(R.id.main_layout);
        CardView flp_missed_card = findViewById(R.id.flp_overdue_card);
        CardView flp_todo_card = findViewById(R.id.flp_todo_card);
        CardView flp_donr_card = findViewById(R.id.flp_done_card);
        CardView first_call_card = findViewById(R.id.first_call_card);
        flp_donr_card.setOnClickListener(this);
        flp_missed_card.setOnClickListener(this);
        flp_todo_card.setOnClickListener(this);
        first_call_card.setOnClickListener(this);

        head_view = findViewById(R.id.head_view);
        head_view.setOnClickListener(this);
        startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        start_layout = findViewById(R.id.start_layout);
        flp_layout = findViewById(R.id.flp_layout);

        Toolbar toolbar = findViewById(R.id.tool_bar1);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
        }

        toolbar_head_view = findViewById(R.id.toolbar_head_view);
        toolbar_head_view.setOnClickListener(this);
        actionbar_title = findViewById(R.id.actionbar_title);
        Intent intent = getIntent();
        followUpType = intent.getStringExtra("FollowUpType");
        if(followUpType != null && followUpType.equals("FollowUpToDo")) {
            actionbar_title.setText("Follow-ups");
        } else if (followUpType != null && followUpType.equals("FirstCall")) {
            actionbar_title.setText("First call");
            noDataTextView.setVisibility(View.GONE);
            toolbar_head_view.setVisibility(View.GONE);
        } else
        {
            actionbar_title.setText("Follow-ups");
        }

        toolbar_spinnerTitle = findViewById(R.id.toolbar_spinnerTitle);
        toolbar_activity_spinner = (CustomSpinner)findViewById(R.id.toolbar_activity_spinner1);

        manualDial = findViewById(R.id.manualDial);
        manualDial.setOnClickListener(this);

        if (ApplicationSettings.containsPref(AppConstants.UNSCHEDULED_CALL)) {
            boolean schedulecall = ApplicationSettings.getPref(AppConstants.UNSCHEDULED_CALL, false);
            if (schedulecall) {
                manualDial.show();
            } else {
                manualDial.hide();
            }
        } else {
            manualDial.hide();
        }
    }

    private void  createSpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("TODAY");
        categories.add("YESTERDAY");
        categories.add("ALL IN PAST");
        categories.add("ALL IN FUTURE");
        categories.add("CHOOSE A DATE");

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

        if (CommonUtils.start_date != null && !CommonUtils.start_date.isEmpty()) {
            String startDate = CommonUtils.start_date;
            if (startDate != null && !startDate.isEmpty() && !startDate.equals("0") && startDate.length() > 0) {
                String selectedDate = startDate.substring(0, startDate.length() - 5);
                toolbar_spinnerTitle.setText(selectedDate);
            }

           // toolbar_spinnerTitle.setText(CommonUtils.start_date);
        } else {
            toolbar_spinnerTitle.setText("TODAY");
        }

        toolbar_activity_spinner.setOnItemSelectedListener(this);
        toolbar_activity_spinner.requestFocus();
    }

    private void setCardColors(int i) {
        flp_list.setVisibility(View.VISIBLE);
        main_layout.setVisibility(View.VISIBLE);
        flp_layout.setVisibility(View.GONE);

        fragmentLayout.setVisibility(View.GONE);

        fView.setVisibility(View.GONE);
        dView.setVisibility(View.GONE);
        oView.setVisibility(View.GONE);

        if (i == 1) {
            fView.setVisibility(View.VISIBLE);
            Cursor flpTodo = getFlpTodoCursor(v);
            if (flpTodo != null && flpTodo.getCount() > 0) {
                noDataTextView.setVisibility(View.GONE);
                if (flpCursorAdapter != null) {
                    flp_list.setVisibility(View.VISIBLE);
                    flpCursorAdapter.changeCursor(flpTodo);
                    flpCursorAdapter.notifyDataSetChanged();
                    flp_list.setAdapter(flpCursorAdapter);
                } else {
                    flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this, flpTodo, 0,0);
                    flp_list.setAdapter(flpCursorAdapter);
                    flpCursorAdapter.notifyDataSetChanged();
                }
            } else {
                flp_list.setVisibility(View.GONE);
                noDataTextView.setVisibility(View.VISIBLE);
            }
        } else if (i == 2) {
            oView.setVisibility(View.VISIBLE);
            Cursor flpOverdue = getFlpMissedCursor(v);
            if (flpOverdue != null) {
                noDataTextView.setVisibility(View.GONE);

                if (flpCursorAdapter != null) {
                    flp_list.setVisibility(View.VISIBLE);
                    flpCursorAdapter.changeCursor(flpOverdue);
                    flpCursorAdapter.notifyDataSetChanged();
                    flp_list.setAdapter(flpCursorAdapter);
                } else {
                    flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this, flpOverdue, 0,0);
                    flp_list.setAdapter(flpCursorAdapter);
                    flpCursorAdapter.notifyDataSetChanged();
                }
            } else {
                flp_list.setVisibility(View.GONE);
                noDataTextView.setVisibility(View.VISIBLE);
            }
        } else if (i == 3) {
            Cursor flpDoneCursor = getFlpDoneCursor(v);
            if (flpDoneCursor != null && flpDoneCursor.getCount() > 0) {
                noDataTextView.setVisibility(View.GONE);
                if (flpCursorAdapter != null) {
                    flp_list.setVisibility(View.VISIBLE);
                    flpCursorAdapter.changeCursor(flpDoneCursor);
                    flpCursorAdapter.notifyDataSetChanged();
                    flp_list.setAdapter(flpCursorAdapter);
                } else {
                    flpCursorAdapter = new FlpCursorAdapter(UearnFollowupActivity.this, flpDoneCursor, 0,0);
                    flp_list.setAdapter(flpCursorAdapter);
                    flpCursorAdapter.notifyDataSetChanged();
                }
            } else {
                flp_list.setVisibility(View.GONE);
                noDataTextView.setVisibility(View.VISIBLE);
            }
        } else if (i == 4) {

            dView.setVisibility(View.VISIBLE);
            Cursor flpCallCursor = getFirstCallCursor();
            if (flpCallCursor != null && flpCallCursor.getCount() > 0) {
                noDataTextView.setVisibility(View.GONE);
                if (flpCursorAdapter != null) {
                    flp_list.setVisibility(View.VISIBLE);
                    noDataTextView.setVisibility(View.GONE);
                    flpCursorAdapter.changeCursor(flpCallCursor);
                    flpCursorAdapter.notifyDataSetChanged();
                    flp_list.setAdapter(flpCursorAdapter);
                } else {
                    createCursorAdaptor(flpCallCursor);
                }
            } else {
                flp_list.setVisibility(View.GONE);
                noDataTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private Cursor getFlpTodoCursor(int v) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        Date date = new Date();
        String selection2;
        Long current = System.currentTimeMillis();
        boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
        if (truePredictive) {
            if (v == 0) {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
            } else if (v == 1) {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
            } else {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
            }
        } else {
            if (v == 0) {
                //TODAY FollowupsTODO + FollowupsMissed

                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' ";
            }



            else if (v == 1) {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' ";
            } else {
                if (selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " + "'" + selectedLeadSource + "'";
                else
                    selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS = 'accepted' ";
            }
        }
        Cursor flpTodo = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
        return flpTodo;
    }

    private Cursor getFlpMissedCursor(int v) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        Date date = new Date();
        Cursor flpMissed;
        String selection;
        Long current = System.currentTimeMillis();
        boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
        if (truePredictive) {
            if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                selection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + "0" + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
            else
                selection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + "0" + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        } else {
            if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                selection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
            else
                selection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + flp_count + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        }
        flpMissed = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        return flpMissed;
    }

    private Cursor getFlpDoneCursor(int v) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        String selection3;
        Long current = System.currentTimeMillis();
        if (v == 0) {
            if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                selection3 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'Delete' " + " AND" + " LEAD_SOURCE = " +  "'" + selectedLeadSource + "'";
            else
                selection3 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'Delete' ";
        } else if (v == 1) {
            if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                selection3 = "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'Delete' " + " AND" + " LEAD_SOURCE = " +  "'" + selectedLeadSource + "'";
            else
                selection3 = "START_TIME" + "<=" + current + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'Delete' ";
        } else {
            if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
                selection3 = "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'Delete' " + " AND" + " LEAD_SOURCE = " +  "'" + selectedLeadSource + "'";
            else
                selection3 = "START_TIME" + ">=" + current + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'Delete' ";
        }
        Cursor done = db.query("remindertbNew", null, selection3, null, null, null, "UPDATED_AT DESC");
        return done;
    }

    private void reactIntent() {
        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, false);
        initializeCalenderDateAndTime();
        if (getIntent().getBooleanExtra("EXIT", false)) {                    //if user has logged out then close
            clearData(1);
        }

        if (getIntent().getBooleanExtra("EXITSIGNIN", false)) {                    //if user has logged out then close
            clearData(0);
        } else if (getIntent().hasExtra("firstcall")) {
            Intent intent = getIntent();
            if (intent.hasExtra("start")) {
                start = intent.getLongExtra("start", 0);
            }
            if (intent.hasExtra("end")) {
                end = intent.getLongExtra("end", 0);
            }

            if (intent.hasExtra("start_time")) {
                start_time = intent.getStringExtra("start_time");
            }

            if (intent.hasExtra("end_time")) {
                end_time = intent.getStringExtra("end_time");
            }
            if (start_time != null) {
                dateForActionBar = CommonUtils.getDate(start_time);
                CommonUtils.start_date = dateForActionBar;
            }
            check = true;
            v = 0;
            checkFirst = false;
        } else if (getIntent().hasExtra("FIRST_TIME")) {
            CommonUtils.permissionsCheck(UearnFollowupActivity.this);

            Intent intent = getIntent();
            boolean check = intent.getBooleanExtra("SETTINGS", false);
            boolean checkData = intent.getBooleanExtra("SETTINGSDATA", false);
            boolean checkTeam = intent.getBooleanExtra("SETTINGSTEAM", false);
            if (check) {
                CustomSingleButtonDialog.buildSingleButtonDialog("Overall Settings", "Failed to sync settings, please check your internet connection.", this, false);
            }
            if (checkData) {
                CustomSingleButtonDialog.buildSingleButtonDialog("Data sync error", "Failed to sync data, please check your internet connection.", this, false);
            }
            if (checkTeam) {
                CustomSingleButtonDialog.buildSingleButtonDialog("Team Settings", "Failed to sync team details, please check your internet connection.", this, false);
            }
            selection = 9;
            setCardColors(9);
            flp_list.setVisibility(View.GONE);

            if (ApplicationSettings.getPref(AppConstants.AUTO_DAILER, false) && ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                    String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                    if (screen != null && (screen.equalsIgnoreCase("Bank1AfterCallActivity") || screen.equalsIgnoreCase("UearnActivity"))) {
                    }
                }
            }
            checkFirst = true;
        } else if (getIntent().hasExtra("ATOCOMPLETE")) {
            CustomSingleButtonDialog.buildSingleButtonDialog("Auto Dialler", "No more numbers to dial.", this, false);
            checkFirst = false;
        } else if (getIntent().hasExtra("followup")) {
            Intent intent = getIntent();
            if (intent.hasExtra("start")) {
                start = intent.getLongExtra("start", 0);
            }
            if (intent.hasExtra("end")) {
                end = intent.getLongExtra("end", 0);
            }

            if (intent.hasExtra("start_time")) {
                start_time = intent.getStringExtra("start_time");
            }

            if (intent.hasExtra("end_time")) {
                end_time = intent.getStringExtra("end_time");
            }
            if (start_time != null) {
                dateForActionBar = CommonUtils.getDate(start_time);
                if(dateForActionBar != null && !dateForActionBar.isEmpty()) {
                    convertSelectedDateToCalendar(dateForActionBar);
                }
                CommonUtils.start_date = dateForActionBar;
                String startDate = dateForActionBar;
                //toolbar_spinnerTitle.setText(dateForActionBar);
                if (startDate != null && !startDate.isEmpty() && !startDate.equals("0") && startDate.length() > 0) {
                    String selectedDate = startDate.substring(0, startDate.length() - 5);
                    toolbar_spinnerTitle.setText(selectedDate);
                }
                toolbar_activity_spinner.setSelection(4);
            }
            check = true;
            v = 0;
            if(followUpType.equals("FollowUpToDo")) {
                getFollowUpToDO();
            } else if (followUpType.equals("FirstCall")) {
                getFirstcallFollowup();
            } else {
                //getMissedFollop();
                getFollowUpToDO();
            }
            checkFirst = false;
        } else {
            checkFirst = false;
        }
        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
            if (isKitKat) {
                getSupportActionBar().setElevation(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertSelectedDateToCalendar(String strCurrentDate){
        String inputPattern = "MMM dd, yyyy";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(strCurrentDate);// it's format should be same as inputPattern
            str = outputFormat.format(date);
            ApplicationSettings.putPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void clearData(int i) {
        Intent intent = new Intent(this, CalldataSendService.class);
        intent.putExtra("batteryStatus", "100%");
        Calendar calendar = Calendar.getInstance();
        String logoutTimeStamp = DateFormat.format("dd-MMM-yyyy hh:mm a", calendar).toString();
        intent.putExtra("timeStamp", logoutTimeStamp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        deleteExistingAlarm();
        deleteAllLocalDB();
        String userEmail = "";
        String accessToken = "";
        String after_call_screen = "";

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }

        if (ApplicationSettings.getPref("accessToken", "") != null) {
            accessToken = ApplicationSettings.getPref("accessToken", "");
        }

        if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null) {
            after_call_screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
        }

        SharedPreferences.Editor editor = ApplicationSettings.getEditor();
        if (editor != null) {
            editor.clear().commit();
        }

        ApplicationSettings.putPref(AppConstants.USERINFO_EMAIL, userEmail);
        ApplicationSettings.putPref("accessToken", accessToken);
        ApplicationSettings.putPref(AppConstants.AFTERCALLACTIVITY_SCREEN, after_call_screen);
        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, true);

        finish();
    }

    private void initializeCalenderDateAndTime() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        start = c.getTimeInMillis();
        start_time = CommonUtils.getTimeFormatInISO(c.getTime());
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
        end = c.getTimeInMillis();
        end_time = CommonUtils.getTimeFormatInISO(c.getTime());
        dateForActionBar = CommonUtils.getDate(start_time);
    }

    public void deleteExistingAlarm() {

        Intent intent = new Intent(getApplicationContext(), Alarm_Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm1.cancel(pendingIntent);
    }

    public void deleteAllLocalDB() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("remindertbNew", null, null);
        db.delete("WorkOrder", null, null);
        db.delete("TeamMembers", null, null);
        db.delete("Ameyo_Push_Notifications", null, null);
        db.delete("ameyo_entries", null, null);
        db.delete("FirstCall", null, null);
        db.delete("ameyo_entries", null, null);
        db.close();
    }

    private Cursor getFirstCallCursor() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        String selection4 = "";
        if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
            selection4 = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT = " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' " + " AND" + " LEAD_SOURCE = " +  "'" + selectedLeadSource + "'";
        else
            selection4 = "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT = " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
        Cursor firstCall = db.query("remindertbNew", null, selection4, null, null, null, "START_TIME ASC");
        return firstCall;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {

            case R.id.head_view:
                activity_spinner.setEnabled(false);
                activity_spinner.performClick();
                activity_spinner.setClickable(false);
                activity_spinner.setFocusable(true);
                activity_spinner.setFocusableInTouchMode(true);
                activity_spinner.requestFocus();
                break;

            case R.id.first_call_card:
                selection = 4;
                setCardColors(4);
                break;

            case R.id.flp_todo_card:
                selection = 1;
                removeFragment();
                setCardColors(1);
                if(flpCursorAdapter != null){
                    flpCursorAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.flp_overdue_card:
                selection = 2;
                removeFragment();
                setCardColors(2);
                break;

            case R.id.start_button:
                UearnActivity.onBackPressed = false;
                manualDialing = false;
                dialingFromList = false;
                autoDial = true;
                click();
                break;

            case R.id.toolbar_head_view:
                toolbar_activity_spinner.setEnabled(false);
                toolbar_activity_spinner.performClick();
                toolbar_activity_spinner.setClickable(false);
                toolbar_activity_spinner.setFocusable(true);
                toolbar_activity_spinner.setFocusableInTouchMode(true);
                toolbar_activity_spinner.requestFocus();
                break;

            case R.id.manualDial:
                if(SmarterSMBApplication.currentStateIsStartMode) {
                    if (ApplicationSettings.containsPref(AppConstants.UNSCHEDULED_CALL)) {
                        boolean schedulecall = ApplicationSettings.getPref(AppConstants.UNSCHEDULED_CALL, false);
                        if (schedulecall) {
                            scheduleCall();
                        }
                    }
                } else {
                    manualDialStartButtonClickedCheckDialog();
                }
                break;
        }
    }

    private void manualDialStartButtonClickedCheckDialog() {

        try {

            final Dialog dialog = CustomSingleButtonDialog.buildAlertDialog("Info","You have not registered your presence for voice calls. Click Start to register and then proceed to initiate calls.", this, false);
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

    private void scheduleCall() {
        if (ApplicationSettings.containsPref(AppConstants.USER_ROLE) && ApplicationSettings.getPref(AppConstants.USER_ROLE, "") != null) {
            String role = ApplicationSettings.getPref(AppConstants.USER_ROLE, "");
            if (role.equalsIgnoreCase("user") || role.equalsIgnoreCase("non-user")) {
                Intent intent = new Intent(this, MakeACallActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Your user Id is not configured as an agent\nYou do not have permissions to make outgoing calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void click() {
        String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
        remoteAutoEnabled = "";
        if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {

            if(UearnActivity.dismissAlertDialog)
                UearnActivity.dismissAlertDialog = false;

            if (remoteAutoEnabled.equals("onsolicit") || remoteAutoDialingStarted) {
                Intent intent4 = new Intent(this, MyAlertDialog.class);
                intent4.putExtra("TITLE", "Stop Auto dialling");
                if (!remoteAutoDialingStarted) {
                    String startString = ApplicationSettings.getPref(AppConstants.REMOTE_DIALLING_START_ALERT,
                            "Are you ready to accept new calls from server now?");
                    intent4.putExtra("RDMESSAGE", startString);
                    intent4.putExtra("RDACTION", "START");
                } else {
                    String stopString = ApplicationSettings.getPref(AppConstants.REMOTE_DIALLING_STOP_ALERT,
                            "Do you want to stop receiving calls from server?");
                    intent4.putExtra("RDMESSAGE", stopString);
                    intent4.putExtra("RDACTION", "STOP");
                }
                startActivityForResult(intent4, 1);
            } else {
                if (CommonUtils.isNetworkAvailable(this)) {
                    if (!sendRemoteDialStartRequest) {
                        //sendRemoteDialStartRequest("Ready to take any call");
                    }
                } else {
                    sendRemoteDialStartRequest = false;
                    Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }  else {
            //CommonUtils.createSbiMakeCall(context1, toNumber, id, appoiontment_id_tocall, status_tocall, substatus1_tocall, substatus2_tocall, toname_tocall, notes_tocall);
        }

        if(ApplicationSettings.containsPref(AppConstants.USER_ROLE) && ApplicationSettings.getPref(AppConstants.USER_ROLE, "") != null) {
            String role = ApplicationSettings.getPref(AppConstants.USER_ROLE, "");
            if (!role.equalsIgnoreCase("user") || !role.equalsIgnoreCase("non-user")) {
                Toast.makeText(this, "Your User Id is not configured as an agent\nYou do not have permissions to make outgoing calls", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (ApplicationSettings.getPref(AppConstants.AUTO_DAILER, false) && ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
            if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                if (!(ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity"))))) {
                    if (checkDb()) {
                        checkAuto = true;
                        fragmentTransaction(new AutoDillerFragment(), 1);
                    } else {

                    }
                }
            } else {
                if (checkDb()) {
                    checkAuto = true;
                    fragmentTransaction(new AutoDillerFragment(), 1);
                } else {

                }
            }
        }
    }

    private void fragmentTransaction(Fragment fragment, int v) {
        try {
            if (v == 0) {
                flp_list.setVisibility(View.GONE);
                flp_layout.setVisibility(View.GONE);
            }
            start_layout.setVisibility(View.GONE);
            fragmentLayout.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.detach(fragment);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDb() {
        Date date = new Date();
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();

        String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT >= " + 1 + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        Cursor cursor1 = db1.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        Cursor cursor2 = null;
        Cursor cursor3 = null;
        if (cursor1 == null || cursor1.getCount() <= 0) {
            try {
                if (cursor1 != null) {
                    cursor1.close();
                    cursor1 = null;
                }
                String selection2 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND " + "FLP_COUNT = " + 0 + " AND" + " RESPONSE_STATUS = 'accepted' ";
                cursor2 = db1.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
                if (cursor2 == null || cursor2.getCount() <= 0) {
                    try {
                        if (cursor2 != null) {
                            cursor2.close();
                            cursor2 = null;
                        }
                        String selection3 = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + "<" + start + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
                        cursor3 = db.query("remindertbNew", null, selection3, null, null, null, "START_TIME DESC");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (cursor1 != null && cursor1.getCount() > 0) {
            return true;
        } else if (cursor2 != null && cursor2.getCount() > 0) {
            return true;
        } else return cursor3 != null && cursor3.getCount() > 0;
    }

    private void sendRequestForData(String message) {
        if (CommonUtils.isNetworkAvailable(this)) {
            if(activity != null && !activity.isFinishing()) {
                new APIProvider.GetRequestForData(message, 0, activity, "Sending request for data. Please wait..", new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        if (data != null && !data.isEmpty()) {
                            Log.d("Response from Server", "Data is " + data);
                        }
                    }
                }).requestForDataCall();
            }
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAppointmentCountAndLeadSource() {
        String jsonData = "";
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        int count = 0;
        Cursor cursor = db.rawQuery("SELECT LEAD_SOURCE, COUNT(_id) from remindertbNew WHERE RESPONSE_STATUS = 'accepted' GROUP BY LEAD_SOURCE", null);
        JSONArray jsonArray = new JSONArray();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                try {
                    JSONObject jsonObj = new JSONObject();
                    String leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
                    int totalAppointment = cursor.getInt(cursor.getColumnIndex("COUNT(_id)"));
                    jsonObj.put("campaign", leadSource);
                    jsonObj.put("accepted", totalAppointment);
                    jsonArray.put(jsonObj);
                    cursor.moveToNext();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        JSONObject requestForDataObj = new JSONObject();
        try {
            requestForDataObj.put("activitytype", "AUTO DATA OVER");
            requestForDataObj.put("requestfordata", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonData = requestForDataObj.toString();
        return jsonData;
    }

    private void removeFragment() {
        if (getSupportFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
            if (fragmentTransaction != null && fragment != null) {
                fragmentTransaction.remove(fragment).commitAllowingStateLoss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
