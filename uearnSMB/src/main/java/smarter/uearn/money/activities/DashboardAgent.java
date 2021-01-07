package smarter.uearn.money.activities;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import smarter.uearn.money.activities.appointment.AppointmentViewActivity;
import smarter.uearn.money.adapters.FilledFormsAdapter;
import smarter.uearn.money.adapters.FlpCursorAdapter;
import smarter.uearn.money.adapters.UearnContactedAdapter;
import smarter.uearn.money.models.ContactedInfo;
import smarter.uearn.money.models.CustomerDetails;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.services.AcceptOrRejectNotificationService;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ResponseInterface;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.MyJsonObject;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.UserActivities;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.webservice.Constants;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;
import smarter.uearn.money.utils.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static smarter.uearn.money.utils.ServerAPIConnectors.Urls.getDashboardSioAddress;

public class DashboardAgent extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    ListView filledforms_list, connected_list;
    TextView done_count, oyo_done_count, actionbar_title, toolbar_spinnerTitle;
    private FlpCursorAdapter flpCursorAdapter;
    String user_id = "";
    private Socket mSocket;
    private JSONObject dashboardJson;
    private TextView avgTalkTime, panCount, qdeCount, rtdRewardsCount;
    public static Long start = 0l, end = 0l;
    public static String start_time = null, end_time = null;
    private MenuItem searchMenuItem;
    private RelativeLayout talkTimeLayout, talktimeBar, doneBar, panBar, qdeBar, filledFormsBar, uearnedBar, rewardsCountLayout, campaignSpinnerLayout, oyoTimeReportBar, oyoUearnedBar, oyoDoneBar, dateSpinnerLayout, sbi_timeReport;
    private LinearLayout agentTableLayout, toolbar_head_view;
    private TableLayout agentTable;
    private TextView uearnedText;
    private ScrollView bottomLayout;
    private CardView doneCard, oyoDoneCard;
    private String use_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
    public String talkTimeMins = "00", talkTimeHours = "00", talkTimeSec = "00";
    private SQLiteDatabase db;
    private String selectedLeadSource = "";
    private ImageView spinnerDropDownView = null;
    private LinearLayout head_view, campaign_head_view, campaignLayout;
    private Spinner activity_spinner, activity_spinner2, toolbar_activity_spinner;
    private TextView spinnerTitle, spinnerTitle2;
    int selection = 0;
    private MenuItem sync;
    private ImageView ivSyncData;
    private LinearLayout flpLayout;
    private LinearLayout oyo_dashboard_time_layout, oyoFlpLayout;
    private ScrollView oyo_uearned_layout; //oyo_timeReportScrool_layout
    private CardView filledFormsCard = null;
    private CardView uearnedCard = null;
    private TextView uearnedTextView = null;
    private TextView filledFormTextView = null;
    private TextView oyoCalls_connected_tv, oyoContactedPercentTv,oyoUniqueConnectedTv, oyoLoginTime, oyoAvgCallTime, oyoAvgHandlingTime; //oyo_contacted_tv oyo_unique_tv login_time oyo_avg_call_time_tv oyo_avg_handling_tv
    private TextView oyoUearnedTv, oyoBonusTv, oyoUearnedPaid, oyoBookedRoomNights, oyoLastHourBookedRoom, oyoUsedRoomNights;//oyo_uearned_tv2 oyo_bonus_tv uearned_paid_tv no_of_booked_room_nights_tv used_room_night
    private TextView oyoMealsPlanSoldTv, oyoMealsLastHour, oyoMealsPlanUsed, oyoUearnedCountTv ; //no_of_meals_tv2 oyo_meals_lh_tv oyo_meals_plan_used_tv
    String custQuestionanire = "";
    private TextView idleTimeTxt, waitTimeTxt, spokenTime, wrapTimeTxt, activeTimeTxt;
    List<CustomerDetails> customerDetailsList = null;
    ListView customersListView;
    List<String> categories = new ArrayList<>();
    private String company = "";
    public static List<ContactedInfo> contactedInfoList;
    private Boolean isContacted = false;
    private Boolean isFilledForms = false;
    private TextView filledFormsText = null;
    private LinearLayout noDataTextViewlayout;

    private String dateForActionBar = "";
    public static boolean dialingFromList = false;

    private Emitter.Listener onRtdAck = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];
                    JSONArray dataArray = (JSONArray) args[2];
                    parseJson(dataArray);
                }
            });
        }
    };

    private Emitter.Listener onRtdNack = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.d("RTD NACK", "");
                }
            });
        }
    };

    private Emitter.Listener onRtdfeed = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];
                    Integer uid = (Integer) args[1];
                    String json = (String) args[2];
                    parseFeedJson(json, uid);
                }
            });
        }
    };

    private void parseJson(JSONArray jsonArray) {
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String data = jsonObject.getString("data");
                    String uid = jsonObject.getString("uid");
                    MyJsonObject dataObj = new MyJsonObject(data);
                    String dilledCount = dataObj.getString("rtd_dialled_count");
                    String panCount = dataObj.getString("rtd_pangen_count");
                    String qdeCount = dataObj.getString("rtd_qdegen_count");
                    String talkTimeCount = dataObj.getString("rtd_talktime_count");
                    String idleTimeCount = dataObj.getString("rtd_idletime_count");
                    String waitTimeCount = dataObj.getString("rtd_waittime_count");
                    String spokenTimeCount = dataObj.getString("rtd_spokentime_count");
                    String wrapTimeCount = dataObj.getString("rtd_wraptime_count");
                    String activeTimeCount = dataObj.getString("rtd_activetime_count");
                    String rtdRewardsCount = dataObj.getString("rtd_rewards_count");
                    ApplicationSettings.putPref(uid + "dilled", dilledCount);
                    ApplicationSettings.putPref(uid + "pan", panCount);
                    ApplicationSettings.putPref(uid + "qde", qdeCount);
                    ApplicationSettings.putPref(uid + "talktime", talkTimeCount);
                    ApplicationSettings.putPref(uid + "idletime", idleTimeCount);
                    ApplicationSettings.putPref(uid + "waittime", waitTimeCount);
                    ApplicationSettings.putPref(uid + "spokentime", spokenTimeCount);
                    ApplicationSettings.putPref(uid + "wraptime", wrapTimeCount);
                    ApplicationSettings.putPref(uid + "activetime", activeTimeCount);
                    ApplicationSettings.putPref(uid + "rtdRewardsCount", rtdRewardsCount);
                }
                plotDashboard();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseFeedJson(String jsonString, int uid) {
        try {
            MyJsonObject dataObj = new MyJsonObject(jsonString);
            String dilledCount = dataObj.getString("rtd_dialled_count");
            String panCount = dataObj.getString("rtd_pangen_count");
            String qdeCount = dataObj.getString("rtd_qdegen_count");
            String talkTimeCount = dataObj.getString("rtd_talktime_count");
            String idleTimeCount = dataObj.getString("rtd_idletime_count");
            String waitTimeCount = dataObj.getString("rtd_waittime_count");
            String spokenTimeCount = dataObj.getString("rtd_spokentime_count");
            String wrapTimeCount = dataObj.getString("rtd_wraptime_count");
            String activeTimeCount = dataObj.getString("rtd_activetime_count");
            String rtdRewardsCount = dataObj.getString("rtd_rewards_count");
            ApplicationSettings.putPref(uid + "dilled", dilledCount);
            ApplicationSettings.putPref(uid + "pan", panCount);
            ApplicationSettings.putPref(uid + "qde", qdeCount);
            ApplicationSettings.putPref(uid + "talktime", talkTimeCount);
            ApplicationSettings.putPref(uid + "idletime", idleTimeCount);
            ApplicationSettings.putPref(uid + "waittime", waitTimeCount);
            ApplicationSettings.putPref(uid + "spokentime", spokenTimeCount);
            ApplicationSettings.putPref(uid + "wraptime", wrapTimeCount);
            ApplicationSettings.putPref(uid + "activetime", activeTimeCount);
            ApplicationSettings.putPref(uid + "rtdRewardsCount", rtdRewardsCount);

            plotDashboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        isContacted = true;
        SmarterSMBApplication.currentActivity = this;
        String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
        setContentView(R.layout.dashboard_agent_gfit);
        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }
        done_count = findViewById(R.id.done_count);
        done_count.setText(String.valueOf(contactedInfoList.size()));
        oyo_done_count = findViewById(R.id.oyo_done_count);
        avgTalkTime = findViewById(R.id.Avg_count);
        panCount = findViewById(R.id.pan_count);
        qdeCount = findViewById(R.id.qde_count);
        rtdRewardsCount = findViewById(R.id.rtdRewardsCount);
        user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        oyoCalls_connected_tv = findViewById(R.id.calls_connected_tv);
        oyoContactedPercentTv = findViewById(R.id.oyo_contacted_tv);
        oyoUniqueConnectedTv = findViewById(R.id.oyo_unique_tv);
        oyoLoginTime = findViewById(R.id.login_time);
        oyoAvgCallTime = findViewById(R.id.oyo_avg_call_time_tv);
        oyoAvgHandlingTime = findViewById(R.id.oyo_avg_handling_tv);
        oyoUearnedTv = findViewById(R.id.oyo_uearned_tv2);
        oyoBonusTv = findViewById(R.id.oyo_bonus_tv);
        oyoUearnedPaid = findViewById(R.id.uearned_paid_tv);
        oyoBookedRoomNights = findViewById(R.id.no_of_booked_room_nights_tv);
        oyoLastHourBookedRoom = findViewById(R.id.oyoLastHourBooked);
        oyoUsedRoomNights = findViewById(R.id.used_room_night);
        oyoMealsPlanSoldTv = findViewById(R.id.no_of_meals_tv2);
        oyoMealsLastHour = findViewById(R.id.oyo_meals_lh_tv);
        oyoMealsPlanUsed = findViewById(R.id.oyo_meals_plan_used_tv);
        oyoUearnedCountTv = findViewById(R.id.oyo_uearned_count);
        dateSpinnerLayout = findViewById(R.id.date_spinner_layout);
        filledFormsText = findViewById(R.id.filled_forms_text);
        idleTimeTxt = findViewById(R.id.idleTimeTxt);
        waitTimeTxt = findViewById(R.id.waitTimeTxt);
        spokenTime = findViewById(R.id.spokenTime);
        wrapTimeTxt = findViewById(R.id.wrapTimeTxt);
        activeTimeTxt = findViewById(R.id.activeTimeTxt);
        noDataTextViewlayout = findViewById(R.id.noDataTextViewlayout);
        init();
        if (questionsAct != null && !questionsAct.isEmpty()) {
            filledforms_list = findViewById(R.id.filledforms_list);
            filledforms_list.setOnItemClickListener(this);
        }

        connected_list = findViewById(R.id.connected_list);
        connected_list.setOnItemClickListener(this);

        if (questionsAct != null && !questionsAct.isEmpty() && questionsAct.equals("MMT")) {
            filledFormsText.setText("ACTIVATED");
            uearnedText.setText("ACTIVE TIME");
        }

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
        Intent intent = getIntent();
        actionbar_title.setText("Dashboard");
        changeStatusBarColor(this);
        createSpinner();
        createSpinner2();
    }

    private void init() {
        CardView talktimeCard = findViewById(R.id.talktime_card);
        CardView panGenCard = findViewById(R.id.pan_gen_card);
        CardView qdeGenCard = findViewById(R.id.qde_gen_cardview);
        doneCard = findViewById(R.id.done_card);
        oyoDoneCard = findViewById(R.id.oyo_done_card);
        talkTimeLayout = findViewById(R.id.talkTime_layout);
        agentTableLayout = findViewById(R.id.agent_table_layout);
        oyo_dashboard_time_layout = findViewById(R.id.oyo_dashboard_time_layout);
        oyo_uearned_layout = findViewById(R.id.oyo_uearned_layout);
        agentTable = findViewById(R.id.agent_table);
        oyoFlpLayout = findViewById(R.id.oyo_flp_layout);
        doneBar = findViewById(R.id.d_view);
        panBar = findViewById(R.id.p_view);
        qdeBar = findViewById(R.id.qde_view);
        talktimeBar = findViewById(R.id.t_view);
        oyoDoneBar = findViewById(R.id.oyo_d_view);
        oyoTimeReportBar = findViewById(R.id.oyo_time_report_view);
        oyoUearnedBar = findViewById(R.id.oyo_uearned_view);
        talktimeCard.setOnClickListener(this);
        panGenCard.setOnClickListener(this);
        qdeGenCard.setOnClickListener(this);
        doneCard.setOnClickListener(this);
        ivSyncData = findViewById(R.id.ivSyncData);
        head_view = findViewById(R.id.head_view);
        spinnerDropDownView = findViewById(R.id.spinner_dropdown2);
        campaign_head_view = findViewById(R.id.campaign_head_view);
        campaignLayout = findViewById(R.id.campaign_layout);
        head_view.setOnClickListener(this);
        campaign_head_view.setOnClickListener(this);
        String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
        if (questionsAct != null && !questionsAct.isEmpty()) {
            filledFormsCard = findViewById(R.id.filled_forms_card);
            uearnedCard = findViewById(R.id.uearned_card);
            filledFormsCard.setOnClickListener(this);
            uearnedCard.setOnClickListener(this);
            filledFormsBar = findViewById(R.id.ff_view);
            uearnedBar = findViewById(R.id.ue_view);
            talktimeCard.setVisibility(View.GONE);
            panGenCard.setVisibility(View.GONE);
            qdeGenCard.setVisibility(View.GONE);
            panBar.setVisibility(View.GONE);
            qdeBar.setVisibility(View.GONE);
            talktimeBar.setVisibility(View.GONE);
            filledFormsBar.setVisibility(View.GONE);
            uearnedBar.setVisibility(View.GONE);
        } else {
            rewardsCountLayout.setVisibility(View.VISIBLE);
            talktimeCard.setVisibility(View.VISIBLE);
            panGenCard.setVisibility(View.VISIBLE);
            qdeGenCard.setVisibility(View.VISIBLE);
            panBar.setVisibility(View.GONE);
            qdeBar.setVisibility(View.GONE);
            talktimeBar.setVisibility(View.GONE);
        }

        uearnedText = findViewById(R.id.uearned_text);
        uearnedTextView = findViewById(R.id.uearned_value);
        filledFormTextView = findViewById(R.id.filled_forms_value);
        flpLayout = findViewById(R.id.flp_layout);
        String questionAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
        if (questionAct != null && !questionAct.isEmpty()) {

        } else {
            campaignSpinnerLayout.setVisibility(View.VISIBLE);
            rewardsCountLayout.setVisibility(View.VISIBLE);
            oyoFlpLayout.setVisibility(View.GONE);
            flpLayout.setVisibility(View.VISIBLE);
        }

        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        db = dbHelper.getWritableDatabase();
    }

    private void createSpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("TODAY");
        categories.add("YESTERDAY");
        categories.add("ALL IN PAST");

        toolbar_activity_spinner = findViewById(R.id.toolbar_activity_spinner1);
        activity_spinner = findViewById(R.id.activity_spinner1);
        if (getIntent().hasExtra("firstcall")) {
            activity_spinner.setSelection(4);
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

    private void createSpinner2() {

        List<String> categories = new ArrayList<>();
        String useCampaign = ApplicationSettings.getPref(AppConstants.USE_CAMPAIGN, "");
        if (useCampaign != null && !useCampaign.isEmpty() && !useCampaign.equals("null")) {
            if (categories != null) {
                categories.add(0, useCampaign);
                spinnerDropDownView.setVisibility(View.VISIBLE);
                campaign_head_view.setEnabled(false);
                campaign_head_view.setClickable(false);
            }
        } else {
            categories.add(0, "All");
            List<String> leadSourceListFromDB = getListOfLeadSourceFromDB();
            if (leadSourceListFromDB != null && !leadSourceListFromDB.isEmpty() && leadSourceListFromDB.size() > 0)
                categories.addAll(leadSourceListFromDB);
            if (categories == null || categories.size() == 0) {
                if ((useCampaign != null && !useCampaign.isEmpty())) {
                    if (categories != null) {
                        categories.add(0, useCampaign);
                        spinnerDropDownView.setVisibility(View.VISIBLE);
                        campaign_head_view.setEnabled(false);
                        campaign_head_view.setClickable(false);
                    }
                } else {
                    categories.add(0, " ");
                    spinnerDropDownView.setVisibility(View.GONE);
                    campaign_head_view.setEnabled(false);
                    campaign_head_view.setClickable(false);
                }
            }
        }

        activity_spinner2 = findViewById(R.id.activity_spinner2);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_tv, categories) {
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
        activity_spinner2.setAdapter(dataAdapter);
        spinnerTitle2 = findViewById(R.id.spinnerTitle2);
        activity_spinner2.setOnItemSelectedListener(this);
        activity_spinner2.requestFocus();

        if (useCampaign != null && !useCampaign.isEmpty() && !useCampaign.equals("null")) {
            spinnerTitle2.setText(useCampaign);
            selectedLeadSource = useCampaign;
            dataAdapter.notifyDataSetChanged();
        } else if (categories != null && categories.size() > 0) {

            if (categories.size() == 1 && categories.get(0).equals("All")) {
                campaign_head_view.setEnabled(false);
                campaign_head_view.setClickable(false);
            }

            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).equals(selectedLeadSource)) {
                    activity_spinner2.setSelection(i);
                    spinnerTitle2.setText(categories.get(i));
                    selectedLeadSource = categories.get(i);
                    break;
                }
            }

            if (selectedLeadSource.isEmpty() || selectedLeadSource.equals("All")) {
                activity_spinner2.setSelection(0);
                spinnerTitle2.setText(categories.get(0));
                selectedLeadSource = categories.get(0);
            }

            dataAdapter.notifyDataSetChanged();
        }
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

    private void getContactedList() {
        if (CommonUtils.isNetworkAvailable(this)) {
            contactedInfoList = new ArrayList<>();
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            UserActivities userActivities = new UserActivities(userId, start_time, end_time);
            new APIProvider.GetContactedList(userActivities, 0, this, "Processing your request.", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        JSONObject jsonObject = null;
                        JSONArray connectedArray = null;
                        JSONObject connectedObj = null;
                        try {
                            jsonObject = new JSONObject(data);
                            if (jsonObject.has("success")) {
                                noDataTextViewlayout.setVisibility(View.GONE);
                                connectedArray = jsonObject.getJSONArray("success");
                                for (int i = 0; i < connectedArray.length(); i++) {
                                    connectedObj = connectedArray.getJSONObject(i);
                                    ContactedInfo contactedInfo = new ContactedInfo();
                                    contactedInfo.id = connectedObj.optString("id");
                                    contactedInfo.event_type = connectedObj.optString("event_type");
                                    contactedInfo.start_time = connectedObj.optString("start_time");
                                    contactedInfo.url = connectedObj.optString("url");
                                    contactedInfo.name = connectedObj.optString("name");
                                    contactedInfo.number = connectedObj.optString("number");
                                    contactedInfo.lead_source = connectedObj.optString("lead_source");
                                    contactedInfo.stage = connectedObj.optString("stage");
                                    contactedInfo.substage1 = connectedObj.optString("substage1");
                                    contactedInfo.substage2 = connectedObj.optString("substage2");
                                    contactedInfo.subject = connectedObj.optString("subject");
                                    contactedInfo.customkvs = connectedObj.optString("customkvs");
                                    contactedInfoList.add(contactedInfo);
                                }

                                connected_list.setVisibility(View.VISIBLE);
                                done_count.setText(String.valueOf(contactedInfoList.size()));
                                Collections.reverse(contactedInfoList);
                                UearnContactedAdapter uearnPassbookAdapter = new UearnContactedAdapter(contactedInfoList);
                                connected_list.setAdapter(uearnPassbookAdapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        done_count.setText("0");
                        connected_list.setVisibility(View.GONE);
                        filledforms_list.setVisibility(View.GONE);
                        noDataTextViewlayout.setVisibility(View.VISIBLE);
                    }
                }
            }).call();
        } else {
            connected_list.setVisibility(View.VISIBLE);
            done_count.setText(String.valueOf(contactedInfoList.size()));
            Collections.reverse(contactedInfoList);
            UearnContactedAdapter uearnPassbookAdapter = new UearnContactedAdapter(contactedInfoList);
            connected_list.setAdapter(uearnPassbookAdapter);
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        connected_list.setVisibility(View.VISIBLE);
        SmarterSMBApplication.setCurrentActivity(this);
        String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
        if (questionsAct != null && !questionsAct.isEmpty()) {
            getListOfCustomers();
            getCustomersCount();
        }

        if(selection == 5) {
            connected_list.setVisibility(View.GONE);
            noDataTextViewlayout.setVisibility(View.GONE);
            filledforms_list.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
        int id = view.getId();
        if (id == R.id.done_card) {
            isContacted = true;
            isFilledForms = false;
            switchOffSocket();
            selection = 1;
            connected_list.setVisibility(View.VISIBLE);
            filledFormsBar.setVisibility(View.GONE);
            if (sbi_timeReport != null) {
                sbi_timeReport.setVisibility(View.GONE);
            }
            syncWithServer();
            if (questionsAct != null && !questionsAct.isEmpty() && ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE") || questionsAct.equals("MMT")))) {
                filledforms_list.setVisibility(View.GONE);
                filledFormsBar.setVisibility(View.GONE);
                uearnedBar.setVisibility(View.GONE);
            }
            searchMenuItem.setVisible(true);
            talkTimeLayout.setVisibility(View.GONE);
            agentTableLayout.setVisibility(View.GONE);
            doneBar.setVisibility(View.VISIBLE);
            qdeBar.setVisibility(View.GONE);
            panBar.setVisibility(View.GONE);
            talktimeBar.setVisibility(View.GONE);
        } else if (id == R.id.oyo_done_card) {
            selection = 1;
            oyo_uearned_layout.setVisibility(View.GONE);
            oyo_dashboard_time_layout.setVisibility(View.GONE);
            connected_list.setVisibility(View.VISIBLE);
            syncWithServer();
            searchMenuItem.setVisible(true);
            oyoDoneBar.setVisibility(View.VISIBLE);
            oyoTimeReportBar.setVisibility(View.GONE);
            oyoUearnedBar.setVisibility(View.GONE);
        } else if (id == R.id.oyo_uearned_card_layout) {
            oyo_uearned_layout.setVisibility(View.VISIBLE);
            oyo_dashboard_time_layout.setVisibility(View.GONE);
            connected_list.setVisibility(View.GONE);
            syncWithServer();
            searchMenuItem.setVisible(false);
            oyoDoneBar.setVisibility(View.GONE);
            oyoTimeReportBar.setVisibility(View.GONE);
            oyoUearnedBar.setVisibility(View.VISIBLE);
        } else if (id == R.id.talktime_card) {
            selection = 2;
            handleView();
            connected_list.setVisibility(View.GONE);
            if (questionsAct != null && !questionsAct.isEmpty() && ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE") || questionsAct.equals("MMT")))) {
                filledforms_list.setVisibility(View.GONE);
                filledFormsBar.setVisibility(View.GONE);
                uearnedBar.setVisibility(View.GONE);
            }
            searchMenuItem.setVisible(false);
            talkTimeLayout.setVisibility(View.GONE);
            agentTableLayout.setVisibility(View.VISIBLE);
            doneBar.setVisibility(View.GONE);
            qdeBar.setVisibility(View.GONE);
            panBar.setVisibility(View.GONE);
            talktimeBar.setVisibility(View.VISIBLE);
            fetchDashboardDataFromServer();
            if (sbi_timeReport != null) {
                sbi_timeReport.setVisibility(View.VISIBLE);
            }
            updateTimerTable();
        } else if (id == R.id.pan_gen_card) {
            switchOffSocket();
            selection = 3;
            connected_list.setVisibility(View.GONE);
            if (sbi_timeReport != null) {
                sbi_timeReport.setVisibility(View.GONE);
            }
            if (questionsAct != null && !questionsAct.isEmpty() && ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE") || questionsAct.equals("MMT")))) {
                filledforms_list.setVisibility(View.GONE);
                filledFormsBar.setVisibility(View.GONE);
                uearnedBar.setVisibility(View.GONE);
            }
            searchMenuItem.setVisible(false);
            talkTimeLayout.setVisibility(View.GONE);
            agentTableLayout.setVisibility(View.VISIBLE);
            doneBar.setVisibility(View.GONE);
            talktimeBar.setVisibility(View.GONE);
            qdeBar.setVisibility(View.GONE);
            panBar.setVisibility(View.VISIBLE);
            plotTable(0);
        } else if (id == R.id.oyo_talktime_card) {
            connected_list.setVisibility(View.GONE);
            oyo_dashboard_time_layout.setVisibility(View.VISIBLE);
            oyoDoneBar.setVisibility(View.GONE);
            oyoTimeReportBar.setVisibility(View.VISIBLE);
            oyoUearnedBar.setVisibility(View.GONE);


            switchOffSocket();
            selection = 4;
            connected_list.setVisibility(View.GONE);
            if (sbi_timeReport != null) {
                sbi_timeReport.setVisibility(View.GONE);
            }
            if (questionsAct != null && !questionsAct.isEmpty() && ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE") || questionsAct.equals("MMT")))) {
                filledforms_list.setVisibility(View.GONE);
                filledFormsBar.setVisibility(View.GONE);
                uearnedBar.setVisibility(View.GONE);
            }
            searchMenuItem.setVisible(false);
            talkTimeLayout.setVisibility(View.GONE);
            agentTableLayout.setVisibility(View.VISIBLE);
            doneBar.setVisibility(View.GONE);
            talktimeBar.setVisibility(View.GONE);
            panBar.setVisibility(View.GONE);
            qdeBar.setVisibility(View.VISIBLE);
            plotTable(1);
        } else if (id == R.id.qde_gen_cardview) {
            switchOffSocket();
            selection = 4;
            connected_list.setVisibility(View.GONE);
            if (sbi_timeReport != null) {
                sbi_timeReport.setVisibility(View.GONE);
            }
            if (questionsAct != null && !questionsAct.isEmpty() && ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE") || questionsAct.equals("MMT")))) {
                filledforms_list.setVisibility(View.GONE);
                filledFormsBar.setVisibility(View.GONE);
                uearnedBar.setVisibility(View.GONE);
            }
            searchMenuItem.setVisible(false);
            talkTimeLayout.setVisibility(View.GONE);
            agentTableLayout.setVisibility(View.VISIBLE);
            doneBar.setVisibility(View.GONE);
            talktimeBar.setVisibility(View.GONE);
            panBar.setVisibility(View.GONE);
            qdeBar.setVisibility(View.VISIBLE);
            plotTable(1);
        } else if (id == R.id.head_view) {
            if (sbi_timeReport != null) {
                sbi_timeReport.setVisibility(View.GONE);
            }
            activity_spinner.setEnabled(false);
            activity_spinner.performClick();
            activity_spinner.setClickable(false);
            activity_spinner.setFocusable(true);
            activity_spinner.setFocusableInTouchMode(true);
            activity_spinner.requestFocus();
        } else if (id == R.id.campaign_head_view) {
            activity_spinner2.setEnabled(false);
            activity_spinner2.performClick();
            activity_spinner2.setClickable(false);
            activity_spinner2.setFocusable(true);
            activity_spinner2.setFocusableInTouchMode(true);
            activity_spinner2.requestFocus();
        } else if (id == R.id.filled_forms_card) {
            searchMenuItem.setVisible(true);
            noDataTextViewlayout.setVisibility(View.GONE);
            selection = 5;
            connected_list.setVisibility(View.GONE);
            filledforms_list.setVisibility(View.VISIBLE);
            doneBar.setVisibility(View.GONE);
            talktimeBar.setVisibility(View.GONE);
            panBar.setVisibility(View.GONE);
            qdeBar.setVisibility(View.GONE);
            filledFormsBar.setVisibility(View.VISIBLE);
            uearnedBar.setVisibility(View.GONE);
            isContacted = false;
            isFilledForms = true;
            getListOfCustomers();
            getCustomersCount();
        } else if (id == R.id.uearned_card) {
            selection = 6;
            connected_list.setVisibility(View.GONE);
            filledforms_list.setVisibility(View.GONE);
            searchMenuItem.setVisible(false);
            talkTimeLayout.setVisibility(View.GONE);
            agentTableLayout.setVisibility(View.GONE);
            doneBar.setVisibility(View.GONE);
            talktimeBar.setVisibility(View.GONE);
            panBar.setVisibility(View.GONE);
            qdeBar.setVisibility(View.GONE);
            filledFormsBar.setVisibility(View.GONE);
            uearnedBar.setVisibility(View.VISIBLE);
            getUearnedValue();
        } else if (id == R.id.toolbar_head_view) {
            toolbar_activity_spinner.setEnabled(false);
            toolbar_activity_spinner.performClick();
            toolbar_activity_spinner.setClickable(false);
            toolbar_activity_spinner.setFocusable(true);
            toolbar_activity_spinner.setFocusableInTouchMode(true);
            toolbar_activity_spinner.requestFocus();
        }
    }

    private void getListOfCustomers() {

        customerDetailsList = new ArrayList<>();

        String data = ApplicationSettings.getPref(AppConstants.UEARN_DASHBOARD_DATA, "");
        if (data != null && !data.isEmpty()) {
            JSONObject jsonObject = null;
            JSONArray customersList = null;
            JSONObject custObj = null;
            String[] customerNames = null;
            try {
                jsonObject = new JSONObject(data);
                customersList = jsonObject.getJSONArray("list");
                customerNames = new String[customersList.length()];
                for (int i = 0; i < customersList.length(); i++) {
                    custObj = customersList.getJSONObject(i);

                    String custName = "";
                    String leadSource = "";
                    String updatedAt = "";

                    if (custObj.has("name")) {
                        custName = custObj.getString("name");
                        if (custName != null && !custName.isEmpty() && custName.equals("null")) {
                            custName = "No Name";
                        }
                    }

                    if (custObj.has("lead_source")) {
                        leadSource = custObj.getString("lead_source");
                        if (leadSource != null && !leadSource.isEmpty() && leadSource.equals("null")) {
                            leadSource = "";
                        }
                    }

                    if (custObj.has("updated_at")) {
                        updatedAt = custObj.getString("updated_at");
                    }
                    customerNames[i] = custName;
                    CustomerDetails customerDetailsInfo = new CustomerDetails(custName, leadSource, updatedAt);
                    customerDetailsList.add(i, customerDetailsInfo);

                }

                customersListView= findViewById(R.id.listView);
                FilledFormsAdapter adapter = new FilledFormsAdapter(customerDetailsList);

                filledforms_list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getSelectedCustomerDetails(final String customerName, final int currentSelectedIndex) {
        String data = ApplicationSettings.getPref(AppConstants.UEARN_DASHBOARD_DATA, "");
        if (data != null && !data.isEmpty()) {
            JSONObject jsonObject = null;
            JSONArray customersList = null;
            JSONObject custObj = null;
            try {
                jsonObject = new JSONObject(data);
                customersList = jsonObject.getJSONArray("list");
                for (int i = 0; i < customersList.length(); i++) {
                    custObj = customersList.getJSONObject(i);
                    if (custObj.has("name")) {
                        String custName = custObj.getString("name");
                        if (custName != null && !custName.isEmpty() && !custName.equals("null") && !custName.equalsIgnoreCase("No Name") && custName.equalsIgnoreCase(customerName)) {
                            custQuestionanire = custObj.getString("comment");
                            break;
                        } else if(i == currentSelectedIndex) {
                            custQuestionanire = custObj.getString("comment");
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getUearnedValue() {
        String data = ApplicationSettings.getPref(AppConstants.UEARN_DASHBOARD_DATA, "");
        if (data != null && !data.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if(company.equals("MMT")){
                    if (jsonObject.has("active_time")) {
                        TextView textView = new TextView(getApplicationContext());
                        textView.setTypeface(null, Typeface.BOLD);
                        textView.setTextColor(Color.BLACK);
                        String activeTime = jsonObject.getString("active_time");
                        textView.setText(activeTime);
                        uearnedTextView.setText(activeTime);
                    }
                } else {
                    if (jsonObject.has("uearned")) {
                        String uearnedValue = jsonObject.getString("uearned");
                        TextView textView = new TextView(getApplicationContext());
                        textView.setTypeface(null, Typeface.BOLD);
                        textView.setTextColor(Color.BLACK);
                        textView.setText(uearnedValue);
                        uearnedTextView.setText(uearnedValue);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getCustomersCount() {
        String data = ApplicationSettings.getPref(AppConstants.UEARN_DASHBOARD_DATA, "");
        if (data != null && !data.isEmpty()) {
            JSONObject jsonObject = null;
            JSONArray customersList = null;
            try {
                jsonObject = new JSONObject(data);
                customersList = jsonObject.getJSONArray("list");
                if (customersList != null && customersList.length() > 0) {
                    String customersCount = String.valueOf(customersList.length());
                    filledFormTextView.setText(customersCount);
                } else {
                    filledFormTextView.setText("0");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleView() {
        doneBar.setVisibility(View.GONE);
        talktimeBar.setVisibility(View.GONE);
        panBar.setVisibility(View.GONE);
        qdeBar.setVisibility(View.GONE);
    }

    private void searchInDone(String searchContent) {
        connected_list.setVisibility(View.VISIBLE);
        talkTimeLayout.setVisibility(View.GONE);
        agentTableLayout.setVisibility(View.GONE);
        doneBar.setVisibility(View.VISIBLE);
        qdeBar.setVisibility(View.GONE);
        panBar.setVisibility(View.GONE);
        talktimeBar.setVisibility(View.GONE);
        ArrayList<ContactedInfo> contactedInfoListNew = new ArrayList<>();
        for(int i = 0; i< contactedInfoList.size(); i++) {
            String name = contactedInfoList.get(i).name;
            boolean isContain = containsIgnoreCase(name, searchContent);
            if(isContain) {
                contactedInfoListNew.add(contactedInfoList.get(i));
            }
       }

        if(contactedInfoListNew != null && contactedInfoListNew.size() > 0) {
            UearnContactedAdapter uearnPassbookAdapter = new UearnContactedAdapter(contactedInfoListNew);
            connected_list.setAdapter(uearnPassbookAdapter);
            connected_list.setVisibility(View.VISIBLE);
            noDataTextViewlayout.setVisibility(View.GONE);
        } else {
            connected_list.setVisibility(View.GONE);
            noDataTextViewlayout.setVisibility(View.VISIBLE);
        }
    }

    private void searchInFieldForm(String searchContent) {
        filledforms_list.setVisibility(View.VISIBLE);
        talkTimeLayout.setVisibility(View.GONE);
        agentTableLayout.setVisibility(View.GONE);
        filledFormsBar.setVisibility(View.VISIBLE);
        qdeBar.setVisibility(View.GONE);
        panBar.setVisibility(View.GONE);
        talktimeBar.setVisibility(View.GONE);
        ArrayList<CustomerDetails> searchCustomerFeedback = new ArrayList<>();
        for(int i = 0; i< customerDetailsList.size(); i++) {
            String name = customerDetailsList.get(i).customerName;
            boolean isContain = containsIgnoreCase(name, searchContent);
            if(isContain) {
                searchCustomerFeedback.add(customerDetailsList.get(i));
            }
        }

        if(searchCustomerFeedback != null && searchCustomerFeedback.size() > 0) {
            FilledFormsAdapter adapter = new FilledFormsAdapter(searchCustomerFeedback);
            filledforms_list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            filledforms_list.setVisibility(View.VISIBLE);
            noDataTextViewlayout.setVisibility(View.GONE);
        } else {
            filledforms_list.setVisibility(View.GONE);
            noDataTextViewlayout.setVisibility(View.VISIBLE);
        }
    }


    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
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
        if(doneBar.getVisibility() == View.VISIBLE) {
            searchMenuItem.setVisible(true);
        }

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
                if(isFilledForms) {
                    //filledFormsBar.setVisibility(View.VISIBLE);
                    searchInFieldForm(query);
                } else {
                    searchInDone(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(isFilledForms) {
//                    filledFormsBar.setVisibility(View.VISIBLE);
                    searchInFieldForm(newText);
                } else {
                    searchInDone(newText);
                }
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







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(isFilledForms) {
            filledFormsBar.setVisibility(View.VISIBLE);
        } else {
            doneBar.setVisibility(View.VISIBLE);
        }
        talktimeBar.setVisibility(View.GONE);
        panBar.setVisibility(View.GONE);
        qdeBar.setVisibility(View.GONE);
        if (itemId == android.R.id.home) {
            this.finish();
        } else if (itemId == R.id.syncItem) {
            String selectedDateFromSpinner = "";
            if(spinnerTitle != null){
                selectedDateFromSpinner = spinnerTitle.getText().toString();
            }
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

    private void syncWithServer() {
        if (start_time != null && end_time != null){
            if (ApplicationSettings.containsPref(AppConstants.DATA_START_TIME)) {
                long start = ApplicationSettings.getPref(AppConstants.DATA_START_TIME, 0l);
                setSync(start, 0);
            } else {
                getSmartUserCalendarDataFromServer(start_time, end_time, 0);
            }
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
            //Toast.makeText(this, "You Now have new Database to call", Toast.LENGTH_SHORT).show();
        }

    }

    private void getSmartUserCalendarDataFromServer(final String startTime, final String endTime, final int nextgetcount) {
        Log.d("StartEndTime", "DA getSmartUserCalendarDataFromServer() - Start: " + start + "Start Time: " + startTime + "End: " + end + "End Time: " + endTime);
        if (CommonUtils.isNetworkAvailable(this)) {

            final RotateAnimation rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(1000);
            rotate.setRepeatCount(ValueAnimator.INFINITE);
            ivSyncData.startAnimation(rotate);
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
                        new DashboardAgent.SaveToLocalDB(data, nextgetcount + 100).execute();

                    } else {
                        ivSyncData.clearAnimation();
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
                if (start != null && end != null) {
                    getSmartUserCalendarDataFromServer(start_time, end_time, nextgetcount);
                }
            }
           /* if (start_time != null && end_time != null) {
                getSmartUserCalendarDataFromServer(start_time, end_time, nextgetcount);
            }*/
            super.onPostExecute(aVoid);
        }
    }


    @Override
    @SuppressLint({"NewApi", "LocalSuppress"})
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
        ListView list = (ListView) adapterView;
        if(list.getId()== R.id.connected_list) {
            String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
            if (questionsAct != null && !questionsAct.isEmpty() && ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE") || questionsAct.equals("MMT")))) {

            } else {
                ContactedInfo customer = (ContactedInfo) (adapterView.getAdapter().getItem(i));
                String customerNo = customer.number;
                if(customerNo != null && !customerNo.isEmpty()){
                    String custId = getId(customerNo);
                    if(custId != null && !custId.isEmpty() && !custId.equals("null")) {
                        id = Long.valueOf(custId);
                    }
                }
                SmarterSMBApplication.isDiallingFromDoneList = true;
                ApplicationSettings.putPref(AppConstants.CHECK_TIMER, true);
                Intent intent = new Intent(this, AppointmentViewActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("disable_ok", true);
                this.startActivity(intent);
            }
        } else {
            String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");
            //&& ((questionsAct.equals("GFIT") || questionsAct.equals("ALLIANCE") || questionsAct.equals("MMT")))
            if (questionsAct != null && !questionsAct.isEmpty()) {
                CustomerDetails customer = (CustomerDetails) (adapterView.getAdapter().getItem(i));
                final String customerName = customer.customerName;
                getSelectedCustomerDetails(customerName, i);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), UearnListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("data", custQuestionanire);
                        intent.putExtra("selected_customer", customerName);
                        getApplicationContext().startActivity(intent);
                    }
                });
            }
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

    private void fetchDashboardDataFromServer() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;

            if(mSocket != null && mSocket.connected()) {

            } else {
                mSocket = IO.socket(getDashboardSioAddress(), opts);
                mSocket.on("rtd_register_ack", onRtdAck);
                mSocket.on("rtd_feed", onRtdfeed);
                mSocket.on("connect", onConnected);
                mSocket.on("rtd_register_nack", onRtdNack);
                try {
                    mSocket.connect();
                } catch (Exception e) {
                    Toast.makeText(this, "Please check your internet connection. ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } catch (java.net.URISyntaxException e) {
            Toast.makeText(this, "DB Not downloaded", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //Log.d("SocketEmit", dashboardJson.toString());
            String company = "";
            if (ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
                company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
                if (company != null && !company.isEmpty()) {
                    if (mSocket != null && mSocket.connected()) {
                        mSocket.emit("rtd_register", "app", user_id, company, dashboardJson);
                    }
                } else {
                    if (mSocket != null && mSocket.connected()) {
                        mSocket.emit("rtd_register", "app", user_id, "", dashboardJson);
                    }
                }
            } else {
                if (mSocket != null && mSocket.connected()) {
                    mSocket.emit("rtd_register", "app", user_id, "", dashboardJson);
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        if (mSocket != null && mSocket.connected()) {
            try {
                mSocket.off("dbfile", onRtdAck);
                mSocket.off("connect", onConnected);
                mSocket.disconnect();
                if (db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {

            }
        }
        super.onDestroy();
    }


    private void getLocalDbFlpCounts() {

        Cursor done = getFlpDoneCursor();
        if (done != null) {
            if (flpCursorAdapter != null) {
                //flp_list.setVisibility(View.VISIBLE);
                flpCursorAdapter.changeCursor(done);
                flpCursorAdapter.notifyDataSetChanged();
                //flp_list.setAdapter(flpCursorAdapter);
                //done.close();
            } else {
                flpCursorAdapter = new FlpCursorAdapter(DashboardAgent.this,done,0,1);
                //flp_list.setAdapter(flpCursorAdapter);
            }
        }
    }

    private Cursor getFlpDoneCursor() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        int donecount = 1;
        //Log.d("Dashboard", "done_start"+start);
        //Log.d("Dashboard", "done_end"+end);
        String selection3 = "UPDATED_AT" + ">=" + start + " AND " + "UPDATED_AT" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND (ASSIGN_TO IS NULL OR ASSIGN_TO = '')";

        String doneCardQuery = ApplicationSettings.getPref(AppConstants.DONE_CARD_QUERY, "");
//        doneCardQuery = " AND ASSIGN_TO IS NOT NULL";

        if(doneCardQuery != null && !doneCardQuery.isEmpty()) {
            selection3 = selection3 + doneCardQuery;
        }

        if(selectedLeadSource != null && !selectedLeadSource.isEmpty() && !selectedLeadSource.equals("All"))
            selection3 =  selection3 + " AND" + " LEAD_SOURCE = " +  "'" + selectedLeadSource + "'";

        Cursor doneUnique = db.query("remindertbNew", null, selection3, null, null, null, "UPDATED_AT DESC");
        if (doneUnique != null) {
            //Log.d("ReuploadService", "done cursor count"+doneUnique.getCount());
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

    private void plotDashboard() {
        if (ApplicationSettings.containsPref(user_id + "talktime")) {
            String talkTime = ApplicationSettings.getPref(user_id + "talktime", "");
            if (talkTime != null && !talkTime.isEmpty()) {
                int talk = Integer.parseInt(talkTime);
                int hours = 0, mins = 0, sec = 0;
                if (talk > 0) {
                    hours = talk / 3600;
                    mins = (talk % 3600) / 60;
                    sec = talk % 60;
                    talkTimeMins = new DecimalFormat("00").format(mins);
                    talkTimeHours = new DecimalFormat("00").format(hours);
                    talkTimeSec = new DecimalFormat("00").format(sec);
                    //avgTalkTime.setText(""+talkTimeHours + ":" + talkTimeMins + ":" + talkTimeSec);
                } else {
                    //avgTalkTime.setText("00:00:00");
                }
            } else {
                //avgTalkTime.setText("00:00:00");
            }
        }

        if (ApplicationSettings.containsPref(user_id + "pan")) {
            String pan = ApplicationSettings.getPref(user_id + "pan", "");
            //panCount.setText(pan);
        }

        if (ApplicationSettings.containsPref(user_id + "qde")) {
            String qde = ApplicationSettings.getPref(user_id + "qde", "");
            //qdeCount.setText(qde);
        }

        if (ApplicationSettings.containsPref(user_id + "rtdRewardsCount")) {
            final String rewardPtoins = ApplicationSettings.getPref(user_id + "rtdRewardsCount", "");
            if (rewardPtoins != null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(rtdRewardsCount != null) {
                            rtdRewardsCount.setText("");
                        }
                    }
                });
            }
        }
    }

    private void updateTimerTable() {
        agentTable.removeAllViews();
        String talkTime = "";
        if (ApplicationSettings.containsPref(user_id + "idletime")) {
            talkTime = ApplicationSettings.getPref(user_id + "idletime", "");

            if (talkTime != null && !talkTime.isEmpty() && !talkTime.equals("null")) {
                if(talkTime.contains(".")) {
                    talkTime = talkTime.substring(0,talkTime.indexOf('.'));
                }
                int talk = Integer.parseInt(talkTime);
                int hours = 0, mins = 0, sec = 0;
                if (talk > 0) {
                    hours = talk / 3600;
                    mins = (talk % 3600) / 60;
                    sec = talk % 60;
                    String talkTimeMins = new DecimalFormat("00").format(mins);
                    String talkTimeHours = new DecimalFormat("00").format(hours);
                    String talkTimeSec = new DecimalFormat("00").format(sec);
                    String idleTime = (" "+talkTimeHours + ":" + talkTimeMins + ":" + talkTimeSec+" ");
                    idleTimeTxt.setText(idleTime);
                } else {
                    idleTimeTxt.setText(" 00:00:00 ");
                }
            } else {
                idleTimeTxt.setText(" 00:00:00 ");
            }
        }

        if (ApplicationSettings.containsPref(user_id + "waittime")) {
            talkTime = ApplicationSettings.getPref(user_id + "waittime", "");
            if (talkTime != null && !talkTime.isEmpty() && !talkTime.equals("null")) {
                if(talkTime.contains(".")) {
                    talkTime = talkTime.substring(0,talkTime.indexOf('.'));
                }
                int talk = Integer.parseInt(talkTime);
                int hours = 0, mins = 0, sec = 0;
                if (talk > 0) {
                    hours = talk / 3600;
                    mins = (talk % 3600) / 60;
                    sec = talk % 60;
                    String talkTimeMins = new DecimalFormat("00").format(mins);
                    String talkTimeHours = new DecimalFormat("00").format(hours);
                    String talkTimeSec = new DecimalFormat("00").format(sec);
                    String waitTime = (" "+talkTimeHours + ":" + talkTimeMins + ":" + talkTimeSec+" ");
                    waitTimeTxt.setText(waitTime);
                } else {
                    waitTimeTxt.setText(" 00:00:00 ");
                }
            } else {
                waitTimeTxt.setText(" 00:00:00 ");
            }
        }

        if (ApplicationSettings.containsPref(user_id + "spokentime")) {
            talkTime = ApplicationSettings.getPref(user_id + "spokentime", "");
            if (talkTime != null && !talkTime.isEmpty() && !talkTime.equals("null")) {
                if(talkTime.contains(".")) {
                    talkTime = talkTime.substring(0,talkTime.indexOf('.'));
                }
                int talk = Integer.parseInt(talkTime);
                int hours = 0, mins = 0, sec = 0;
                if (talk > 0) {
                    hours = talk / 3600;
                    mins = (talk % 3600) / 60;
                    sec = talk % 60;
                    String talkTimeMins = new DecimalFormat("00").format(mins);
                    String talkTimeHours = new DecimalFormat("00").format(hours);
                    String talkTimeSec = new DecimalFormat("00").format(sec);
                    String spokenTimes = (" "+talkTimeHours + ":" + talkTimeMins + ":" + talkTimeSec+" ");
                    spokenTime.setText(spokenTimes);
                } else {
                    spokenTime.setText(" 00:00:00 ");
                }
            } else {
                spokenTime.setText(" 00:00:00 ");
            }
        }

        if (ApplicationSettings.containsPref(user_id + "wraptime")) {
            talkTime = ApplicationSettings.getPref(user_id + "wraptime", "");
            if (talkTime != null && !talkTime.isEmpty() && !talkTime.equals("null")) {
                if(talkTime.contains(".")) {
                    talkTime = talkTime.substring(0,talkTime.indexOf('.'));
                }
                int talk = Integer.parseInt(talkTime);
                int hours = 0, mins = 0, sec = 0;
                if (talk > 0) {
                    hours = talk / 3600;
                    mins = (talk % 3600) / 60;
                    sec = talk % 60;
                    String talkTimeMins = new DecimalFormat("00").format(mins);
                    String talkTimeHours = new DecimalFormat("00").format(hours);
                    String talkTimeSec = new DecimalFormat("00").format(sec);
                    String wrapTime = (" "+talkTimeHours + ":" + talkTimeMins + ":" + talkTimeSec+" ");
                    wrapTimeTxt.setText(wrapTime);
                } else {
                    wrapTimeTxt.setText(" 00:00:00 ");
                }
            } else {
                wrapTimeTxt.setText(" 00:00:00 ");
            }
        }

        if (ApplicationSettings.containsPref(user_id + "activetime")) {
            talkTime = ApplicationSettings.getPref(user_id + "activetime", "");
            if (talkTime != null && !talkTime.isEmpty() && !talkTime.equals("null")) {
                if(talkTime.contains(".")) {
                    talkTime = talkTime.substring(0,talkTime.indexOf('.'));
                }
                int talk = Integer.parseInt(talkTime);
                int hours = 0, mins = 0, sec = 0;
                if (talk > 0) {
                    hours = talk / 3600;
                    mins = (talk % 3600) / 60;
                    sec = talk % 60;
                    String talkTimeMins = new DecimalFormat("00").format(mins);
                    String talkTimeHours = new DecimalFormat("00").format(hours);
                    String talkTimeSec = new DecimalFormat("00").format(sec);
                    String activeTime = (" "+talkTimeHours + ":" + talkTimeMins + ":" + talkTimeSec+" ");
                    activeTimeTxt.setText(activeTime);
                } else {
                    activeTimeTxt.setText(" 00:00:00 ");
                }
            } else {
                activeTimeTxt.setText(" 00:00:00 ");
            }
        }
    }

    private void plotTimerTable() {
        agentTable.removeAllViews();

        for (int i = 0; i < 6; i++) {
            ArrayList<String> list1 = new ArrayList<>();
            if (i == 0) {
                list1.add("TODAY'S TIME STAT");
                list1.add("DURATION (HH:MM:SS)");
            } else {
                String talkTime = "";
                switch (i) {
                    case 1:
                        list1.add("IDLE TIME");
                        if (ApplicationSettings.containsPref(user_id + "idletime")) {
                            talkTime = ApplicationSettings.getPref(user_id + "idletime", "");
                        }
                        break;
                    case 2:
                        list1.add("WAIT TIME");
                        if (ApplicationSettings.containsPref(user_id + "waittime")) {
                            talkTime = ApplicationSettings.getPref(user_id + "waittime", "");
                        }
                        break;
                    case 3:
                        list1.add("SPOKEN TIME");
                        if (ApplicationSettings.containsPref(user_id + "spokentime")) {
                            talkTime = ApplicationSettings.getPref(user_id + "spokentime", "");
                        }
                        break;
                    case 4:
                        list1.add("WRAP TIME");
                        if (ApplicationSettings.containsPref(user_id + "wraptime")) {
                            talkTime = ApplicationSettings.getPref(user_id + "wraptime", "");
                        }
                        break;
                    case 5:
                        list1.add("ACTIVE TIME");
                        if (ApplicationSettings.containsPref(user_id + "activetime")) {
                            talkTime = ApplicationSettings.getPref(user_id + "activetime", "");
                        }
                        break;
                    default:
                        break;
                }

                if (talkTime != null && !talkTime.isEmpty() && !talkTime.equals("null")) {
                    if(talkTime.contains(".")) {
                        talkTime = talkTime.substring(0,talkTime.indexOf('.'));
                    }
                    int talk = Integer.parseInt(talkTime);
                    int hours = 0, mins = 0, sec = 0;
                    if (talk > 0) {
                        hours = talk / 3600;
                        mins = (talk % 3600) / 60;
                        sec = talk % 60;
                        String talkTimeMins = new DecimalFormat("00").format(mins);
                        String talkTimeHours = new DecimalFormat("00").format(hours);
                        String talkTimeSec = new DecimalFormat("00").format(sec);
                        //list1.add("" + talkTimeHours + ":" + talkTimeMins + ":" + talkTimeSec);
                        list1.add(" "+talkTimeHours + ":" + talkTimeMins + ":" + talkTimeSec+" ");
                        //avgTalkTime.setText(""+talkTimeHours + ":" + talkTimeMins + ":" + talkTimeSec);
                    } else {
                        list1.add(" 00:00:00 ");
                        //avgTalkTime.setText("00:00:00");
                    }
                } else {
                    list1.add(" 00:00:00 ");
                    //avgTalkTime.setText("00:00:00");
                }
            }

            TableRow row1 = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            row1.setPadding(15, 5, 15, 5);

            for (int j = 0; j < list1.size(); j++) {
                row1.setLayoutParams(lp);
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView qty = new TextView(this);
                qty.setText(list1.get(j));
                layout.addView(qty);
                qty.setPadding(15, 15, 15, 15);
                qty.setTextColor(getResources().getColor(R.color.black));
                layout.setBackground(getResources().getDrawable(R.drawable.board));
                row1.addView(layout);
            }
            agentTable.addView(row1);
        }
    }

    private void plotTable(int entry) {
        agentTable.removeAllViews();
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("CUSTOMER");
        list1.add("PAN");
        list1.add("STATUS");
        TableRow row1 = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        row1.setPadding(15, 5, 15, 5);

        for (int j = 0; j < list1.size(); j++) {
            row1.setLayoutParams(lp);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            TextView qty = new TextView(this);
            qty.setText(list1.get(j));
            layout.addView(qty);
            qty.setPadding(15, 15, 15, 15);
            qty.setTextColor(getResources().getColor(R.color.black));
            layout.setBackground(getResources().getDrawable(R.drawable.board));
            row1.addView(layout);
        }
        agentTable.addView(row1, 0);
        //createRows(entry);
    }

    private Cursor getPanAndQdeCursor(int entry) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        if (entry == 0) {
            String selection = "( SUBSTATUS1 = 'NOT ELIGIBLE' OR ( SUBSTATUS1 = '" + "PAN APPROVED" + "'  AND RESPONSE_STATUS = 'accepted' ) OR ( SUBSTATUS1 = 'PAN AVAILABLE' AND RESPONSE_STATUS = 'completed' ) OR SUBSTATUS1 = 'WRONG PAN NUMBER' OR SUBSTATUS1 = 'RECENT REJECT' OR SUBSTATUS1 = 'ALREADY IN PROCESS' OR SUBSTATUS1 = 'LEAD SHEET INCOMPLETE' OR ( SUBSTATUS1 =  'PAN APPROVED' AND RESPONSE_STATUS = 'completed' ) OR ( SUBSTATUS1 = 'TL APPROVED' AND RESPONSE_STATUS = 'completed' ) OR SUBSTATUS1 = 'COMPANY NOT LISTED' OR SUBSTATUS1 = 'DESIGNATION NOT OK' OR SUBSTATUS1 = 'SURROGATE NOT OK' OR SUBSTATUS1 = 'REPEAT ENTRY' OR SUBSTATUS1 = 'CIBIL NOT ELIGIBLE' OR SUBSTATUS1 = 'LOW CIBIL' OR SUBSTATUS1 = 'DEFAULTER' OR SUBSTATUS1 = 'APPROVED' OR SUBSTATUS1 = 'APPROVED WITHOUT DOCS' OR SUBSTATUS1 = 'REFERRED' OR SUBSTATUS1 = 'BUREAU ERROR' ) " + "AND ( UPDATED_AT" + ">=" + start + " AND " + "UPDATED_AT" + "<=" + end + " AND " + "STATUS != 'deleted' )";
            return db.query("remindertbNew ", null, selection, null, "TO1", "Count(Distinct(TO1)) >= 1", "UPDATED_AT DESC");
        } else {
            String selection = " ( SUBSTATUS1 = 'LEAD SHEET INCOMPLETE' OR ( SUBSTATUS1 =  'PAN APPROVED' AND RESPONSE_STATUS = 'completed' AND (NOTES_IMAGE IS NOT NULL AND NOTES_IMAGE <> '')) OR ( SUBSTATUS1 = 'TL APPROVED' AND RESPONSE_STATUS = 'completed' ) OR SUBSTATUS1 = 'COMPANY NOT LISTED' OR SUBSTATUS1 = 'DESIGNATION NOT OK' OR SUBSTATUS1 = 'SURROGATE NOT OK' OR SUBSTATUS1 = 'REPEAT ENTRY' OR SUBSTATUS1 = 'CIBIL NOT ELIGIBLE' OR SUBSTATUS1 = 'LOW CIBIL' OR SUBSTATUS1 = 'DEFAULTER' OR SUBSTATUS1 = 'APPROVED' OR SUBSTATUS1 = 'APPROVED WITHOUT DOCS' OR SUBSTATUS1 = 'REFERRED' OR SUBSTATUS1 = 'BUREAU ERROR' )" + " AND ( UPDATED_AT" + ">=" + start + " AND " + "UPDATED_AT" + "<=" + end + " AND " + "STATUS != 'deleted' )";
            return db.query("remindertbNew", null, selection, null, "TO1", "Count(Distinct(TO1)) >= 1", "UPDATED_AT DESC");
        }
    }



    private String[] getLatestStatus(String num, int entity) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            db = dbHelper.getWritableDatabase();
        }
        String status = "", responseStatus;
        String [] arr = new String[2];
        if (entity == 0) {
            //Log.d("Dashboard", "start"+start);
            //Log.d("Dashboard", "end" + end);
            String selection = "UPDATED_AT" + ">=" + start + " AND " + "UPDATED_AT" + "<=" + end +" AND TO1 = '" +num+"' AND ( SUBSTATUS1 = 'NOT ELIGIBLE' OR  SUBSTATUS1 = 'PAN APPROVED' OR ( SUBSTATUS1 = 'PAN AVAILABLE' AND RESPONSE_STATUS = 'completed' ) OR SUBSTATUS1 = 'WRONG PAN NUMBER' OR SUBSTATUS1 = 'RECENT REJECT' OR SUBSTATUS1 = 'ALREADY IN PROCESS' OR SUBSTATUS1 = 'LEAD SHEET INCOMPLETE' OR ( SUBSTATUS1 = 'TL APPROVED' AND RESPONSE_STATUS = 'completed' ) OR SUBSTATUS1 = 'COMPANY NOT LISTED' OR SUBSTATUS1 = 'DESIGNATION NOT OK' OR SUBSTATUS1 = 'SURROGATE NOT OK' OR SUBSTATUS1 = 'REPEAT ENTRY' OR SUBSTATUS1 = 'CIBIL NOT ELIGIBLE' OR SUBSTATUS1 = 'LOW CIBIL' OR SUBSTATUS1 = 'DEFAULTER' OR SUBSTATUS1 = 'APPROVED' OR SUBSTATUS1 = 'APPROVED WITHOUT DOCS' OR SUBSTATUS1 = 'REFERRED' OR SUBSTATUS1 = 'BUREAU ERROR' ) ";
            /*String selection = "UPDATED_AT" + ">=" + start + " AND " + "UPDATED_AT" + "<=" + end +" AND TO1 = '" +num+"' AND ( SUBSTATUS1 = 'NOT ELIGIBLE' OR  SUBSTATUS1 = 'PAN AVAILABLE' OR SUBSTATUS1 = 'WRONG PAN NUMBER' OR SUBSTATUS1 = 'RECENT REJECT' OR SUBSTATUS1 = 'ALREADY IN PROCESS' OR SUBSTATUS1 = 'LEAD SHEET INCOMPLETE' OR SUBSTATUS1 = 'COMPANY NOT LISTED' OR SUBSTATUS1 = 'DESIGNATION NOT OK' OR SUBSTATUS1 = 'SURROGATE NOT OK' OR SUBSTATUS1 = 'REPEAT ENTRY' OR SUBSTATUS1 = 'CIBIL NOT ELIGIBLE' OR SUBSTATUS1 = 'LOW CIBIL' OR SUBSTATUS1 = 'DEFAULTER' OR SUBSTATUS1 = 'APPROVED' OR SUBSTATUS1 = 'APPROVED WITHOUT DOCS' OR SUBSTATUS1 = 'REFERRED' OR SUBSTATUS1 = 'BUREAU ERROR' OR SUBSTATUS1 = 'TL APPROVED' OR SUBSTATUS1 = 'PAN APPROVED' ) ";*/
            Cursor cursor = db.query("remindertbNew ", null, selection, null, null, null, "UPDATED_AT DESC");
            try {
                if(cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    /* while (!cursor.isAfterLast()) {*/
                    status = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
                    responseStatus = cursor.getString(cursor.getColumnIndex("RESPONSE_STATUS"));
                    long updated_at = cursor.getLong(cursor.getColumnIndex("UPDATED_AT"));
                    String name = cursor.getString(cursor.getColumnIndex("TONAME"));
                    arr[0] = status;
                    arr[1] = responseStatus;
                    //Log.d("Dashboard", "number"+num);
                    //Log.d("Dashboard", "status" + status);
                    //Log.d("Dashboard", "responsestatus" + responseStatus);
                    //Log.d("Dashboard", "updated_at" + updated_at);
                    //Log.d("Dashboard", "name" + name);
                    cursor.moveToNext();
                 /*   }
                    cursor.close();;*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return arr;

        } else {
            String selection ="UPDATED_AT" + ">=" + start + " AND " + "UPDATED_AT" + "<=" + end + " AND TO1 = '" +num+"' AND ( SUBSTATUS1 = 'LEAD SHEET INCOMPLETE' OR ( SUBSTATUS1 =  'PAN APPROVED' AND RESPONSE_STATUS = 'completed' ) OR ( SUBSTATUS1 = 'TL APPROVED' AND RESPONSE_STATUS = 'completed' ) OR SUBSTATUS1 = 'COMPANY NOT LISTED' OR SUBSTATUS1 = 'DESIGNATION NOT OK' OR SUBSTATUS1 = 'SURROGATE NOT OK' OR SUBSTATUS1 = 'REPEAT ENTRY' OR SUBSTATUS1 = 'CIBIL NOT ELIGIBLE' OR SUBSTATUS1 = 'LOW CIBIL' OR SUBSTATUS1 = 'DEFAULTER' OR SUBSTATUS1 = 'APPROVED' OR SUBSTATUS1 = 'APPROVED WITHOUT DOCS' OR SUBSTATUS1 = 'REFERRED' OR SUBSTATUS1 = 'BUREAU ERROR' )";
            Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
            try {
                if(cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    /* while (!cursor.isAfterLast()) {*/
                    status = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
                    responseStatus = cursor.getString(cursor.getColumnIndex("RESPONSE_STATUS"));
                    arr[0] = status;
                    arr[1] = responseStatus;
                    //Log.d("Dashboard", "number"+num);
                    //Log.d("Dashboard", "status"+status);
                    //Log.d("Dashboard", "responsestatus"+responseStatus);

                    /*    cursor.moveToNext();
                    }*/
                    cursor.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return arr;
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(isFilledForms) {
            filledFormsBar.setVisibility(View.VISIBLE);
        } else {
            doneBar.setVisibility(View.VISIBLE);
        }
        talktimeBar.setVisibility(View.GONE);
        panBar.setVisibility(View.GONE);
        qdeBar.setVisibility(View.GONE);
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.activity_spinner1 || spinner.getId() == R.id.toolbar_activity_spinner1)
        {
            switch (i) {
                case 0:
                    toolbar_spinnerTitle.setText("TODAY");
                    initCalender();

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(new Date());
                    end = endCalendar.getTimeInMillis();
                    end_time = CommonUtils.getTimeFormatInISO(new Date(end));
                    user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    if(isContacted) {
                        getContactedList();
                    } else {
                        getUearnDashboardInfo(user_id, start_time, end_time);
                    }



//                    UearnContactedAdapter uearnPassbookAdapter = new UearnContactedAdapter(contactedInfoList);
//                    connected_list.setAdapter(uearnPassbookAdapter);

                    // getLocalDbFlpCounts();
                    Log.d("StartEndTime", "DA TODAY - Start: " + start + "Start Time: " + start_time + "End: " + end + "End Time: " + end_time);
                    break;
                case 1:
                    toolbar_spinnerTitle.setText("Y'DAY");
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(new Date());
                    calendar2.set(Calendar.DAY_OF_YEAR, calendar2.get(Calendar.DAY_OF_YEAR) - 1);
                    calendar2.set(Calendar.HOUR_OF_DAY, 0);
                    calendar2.set(Calendar.MINUTE, 0);
                    calendar2.set(Calendar.SECOND, 0);
                    calendar2.set(Calendar.MILLISECOND, 0);
                    start = calendar2.getTimeInMillis();

                    start_time = CommonUtils.getTimeFormatInISO(new Date(start));

                    calendar2 = Calendar.getInstance();
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
                    user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    if(isContacted) {
                        getContactedList();
                        break;
                    } else {
                        getUearnDashboardInfo(user_id, start_time, end_time);
                        break;
                    }




                    // getLocalDbFlpCounts();
                    //Log.d("StartEndTime", "DA YESTERDAY - Start: " + start + "Start Time: " + start_time + "End: " + end + "End Time: " + end_time);



                case 2:
                    toolbar_spinnerTitle.setText("PAST");
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(new Date());
                    calendar3.set(Calendar.DAY_OF_YEAR, calendar3.get(Calendar.DAY_OF_YEAR) - 30);
                    calendar3.set(Calendar.HOUR_OF_DAY, 0);
                    calendar3.set(Calendar.MINUTE, 0);
                    calendar3.set(Calendar.SECOND, 0);
                    calendar3.set(Calendar.MILLISECOND, 0);
                    start = calendar3.getTimeInMillis();
                    start_time = CommonUtils.getTimeFormatInISO(new Date(start));
                    //getLocalDbFlpCounts();
                    if(isContacted) {
                        doneBar.setVisibility(View.VISIBLE);
                        filledFormsBar.setVisibility(View.GONE);
                        getContactedList();
                    } else {
                        doneBar.setVisibility(View.GONE);
                        filledFormsBar.setVisibility(View.VISIBLE);
                        getUearnDashboardInfo(user_id, start_time, end_time);
                    }
                    break;

            }
        }
        else if(spinner.getId() == R.id.activity_spinner2) {
            activity_spinner2.setSelection(i);
            String selectedItem = adapterView.getSelectedItem().toString();
            spinnerTitle2.setText(selectedItem);
            selectedLeadSource = selectedItem;
            //getLocalDbFlpCounts();

            if(selection == 1) {
                connected_list.setVisibility(View.VISIBLE);
                searchMenuItem.setVisible(true);
                talkTimeLayout.setVisibility(View.GONE);
                agentTableLayout.setVisibility(View.GONE);
                doneBar.setVisibility(View.VISIBLE);
                qdeBar.setVisibility(View.GONE);
                panBar.setVisibility(View.GONE);
                talktimeBar.setVisibility(View.GONE);
            } else if(selection == 2) {
                handleView();
                connected_list.setVisibility(View.GONE);
                searchMenuItem.setVisible(false);
                talkTimeLayout.setVisibility(View.GONE);
                agentTableLayout.setVisibility(View.VISIBLE);
                doneBar.setVisibility(View.GONE);
                qdeBar.setVisibility(View.GONE);
                panBar.setVisibility(View.GONE);
                talktimeBar.setVisibility(View.VISIBLE);
                //plotTimerTable();
                if(sbi_timeReport != null) {
                    sbi_timeReport.setVisibility(View.VISIBLE);
                }
                updateTimerTable();
            } else if(selection == 3) {
                connected_list.setVisibility(View.GONE);
                searchMenuItem.setVisible(false);
                talkTimeLayout.setVisibility(View.GONE);
                agentTableLayout.setVisibility(View.VISIBLE);
                doneBar.setVisibility(View.GONE);
                talktimeBar.setVisibility(View.GONE);
                qdeBar.setVisibility(View.GONE);
                panBar.setVisibility(View.VISIBLE);
                plotTable(0);
            } else if(selection == 4) {
                connected_list.setVisibility(View.GONE);
                searchMenuItem.setVisible(false);
                talkTimeLayout.setVisibility(View.GONE);
                agentTableLayout.setVisibility(View.VISIBLE);
                doneBar.setVisibility(View.GONE);
                talktimeBar.setVisibility(View.GONE);
                panBar.setVisibility(View.GONE);
                qdeBar.setVisibility(View.VISIBLE);
                plotTable(1);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        switchOffSocket();
    }

    private void switchOffSocket() {
        if (mSocket != null && mSocket.connected()) {
            try {
                mSocket.off("rtd_register_ack", onRtdAck);
                mSocket.off("rtd_feed", onRtdfeed);
                mSocket.off("connect", onConnected);
                mSocket.off("rtd_register_nack", onRtdNack);
                mSocket.disconnect();
                mSocket = null;
            } catch (Exception e) {
            }
        }
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
        if(userId != null) {
            getTeamMembersCall(userId);
        }
    }

    private void getTeamMembersCall(String id) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.Get_All_Team_Members(id, 535, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                @Override
                public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                    if (data != null) {
                        CommonUtils.saveTeamdataToLocalDB(DashboardAgent.this, data);
                        for (int i = 0; i < data.size(); i++) {
                            String request = data.get(i).request;
                            if ((request != null) && request.equals("invited")) {
                                Intent intent = new Intent(DashboardAgent.this, AcceptOrRejectNotificationService.class);
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



    private void sendRemoteDialStopRequest(String message) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.GetRemoteDialerStop(message, 0, this, "Stopping. Please wait..", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                    String text = "";
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.has("text")) {
                            text = jsonObject.getString("text");
                        }
                    } catch (Exception e) {

                    }

                    if (text != null && !text.isEmpty()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, text);
                    }
                }
            }).call();
        }  else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }


    private void getUearnDashboardInfo(String userId, String startTime, String endTime) {
        if (CommonUtils.isNetworkAvailable(this)) {
            UserActivities userActivities = new UserActivities(userId, startTime, endTime);
            new APIProvider.GetUearnDashboard(userActivities, 0, this, "Processing your request.", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        //Log.d("Response from Server", "Data is " + data);
                        ApplicationSettings.putPref(AppConstants.UEARN_DASHBOARD_DATA, data);
                        onResume();
                    }
                }
            }).call();
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
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
        spinnerTitle.setText(dateForActionBar);
        activity_spinner.setSelection(3);
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

    private String getId(String number) {
        String id = "";
        try {
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if (number != null && !number.isEmpty()) {
                String selection = "TO1=" + "'" + number + "'";
                Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    id = cursor.getString(cursor.getColumnIndex("_id"));
                }
                if (dbase.isOpen()) {
                    dbase.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
