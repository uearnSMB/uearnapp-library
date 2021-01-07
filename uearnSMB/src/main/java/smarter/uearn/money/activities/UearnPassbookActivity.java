package smarter.uearn.money.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import smarter.uearn.money.R;
import smarter.uearn.money.adapters.UearnPassbookViewpagerAdapter;
import smarter.uearn.money.models.PassbookLifetimeInfo;
import smarter.uearn.money.models.UearnPassbookInfo;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.UserActivities;

public class UearnPassbookActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ListView uearned_list;
    private LinearLayout uearn_layout;
    private TextView currentMonth;
    private CardView today_card, lifetimeEarnedCard;
    private ImageView showmoreImg;
    private Boolean isShowMore = true;
    private RelativeLayout todayEarnedView, lifetimeEarnedView;
    private List<UearnPassbookInfo> passbookInfoList;
    private UearnPassbookViewpagerAdapter adapter;

    private ScrollView uearnPassbookScrollView;
    private LinearLayout uearnPassbookLayout;
    private PassbookLifetimeInfo passbookLifetimeObj = null;
    private Button total_earned_till_date;
    private CardView infoCard;
    private CardView projectsCard;
    private CardView bonusCard;
    private CardView summaryCard;
    private TextView dateOfJoinTxt;
    private TextView dateOfJoin;
    private TextView totalWorkingDaysTxt;
    private TextView totalWorkingDays;
    private TextView totalActiveTimeTxt;
    private TextView totalActiveTime;
    private TextView totalPaymentDone;
    private TextView closingBalance;
    private TextView callEarningTxt;
    private TextView callEarning;
    private TextView bonusTxt;
    private TextView bonus;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_uearn_passbook);
        restoreActionBar("Passbook");
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        changeStatusBarColor(this);
        SmarterSMBApplication.currentActivity = this;
        today_card = findViewById(R.id.today_card);
        today_card.setOnClickListener(this);
        lifetimeEarnedCard = findViewById(R.id.lifetimeEarnedCard);
        lifetimeEarnedCard.setOnClickListener(this);
        showmoreImg = findViewById(R.id.showmoreImg);
        showmoreImg.setImageResource(R.drawable.up_arrow);
        lifetimeEarnedView = findViewById(R.id.lifetimeEarnedView);
        todayEarnedView = findViewById(R.id.todayEarnedView);
        todayEarnedView.setVisibility(View.VISIBLE);
        tabLayout = findViewById(R.id.sliding_tabs);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setVisibility(View.VISIBLE);
        uearn_layout = findViewById(R.id.uearn_layout);
        uearn_layout.setVisibility(View.GONE);
        currentMonth = findViewById(R.id.currentMonth);
        passbookLifetimeObj = new PassbookLifetimeInfo();
        getUearnPassbookLifetimeData();
        uearnPassbookScrollView = findViewById(R.id.uearn_passbook_scrollview);
        uearnPassbookLayout = findViewById(R.id.uearn_passbook_layout);
        total_earned_till_date = findViewById(R.id.total_earned_till_date);
        infoCard = findViewById(R.id.infoCard);
        projectsCard = findViewById(R.id.projectsCard);
        bonusCard = findViewById(R.id.bonusCard);
        summaryCard = findViewById(R.id.summaryCard);
        dateOfJoinTxt = findViewById(R.id.dateOfJoinTxt);
        dateOfJoin = findViewById(R.id.dateOfJoin);
        totalWorkingDaysTxt = findViewById(R.id.totalWorkingDaysTxt);
        totalWorkingDays = findViewById(R.id.totalWorkingDays);
        totalActiveTimeTxt = findViewById(R.id.totalActiveTimeTxt);
        totalActiveTime = findViewById(R.id.totalActiveTime);
        totalPaymentDone = findViewById(R.id.totalPaymentDone);
        closingBalance = findViewById(R.id.closingBalance);
        callEarningTxt = findViewById(R.id.callEarningTxt);
        callEarning = findViewById(R.id.callEarning);
        bonusTxt = findViewById(R.id.bonusTxt);
        bonus = findViewById(R.id.bonus);
        getUearnMonth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    private void getUearnPassbookLifetimeData() {
        if (CommonUtils.isNetworkAvailable(this)) {
            ApplicationSettings.putPref(AppConstants.UEARN_PASSBOOK_LIFETIME, "");
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            UserActivities userActivities = new UserActivities(userId, "", "");

            new APIProvider.GetUearnPassbookLifetime(userActivities, 0, this, "Processing your request.", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        JSONObject jsonObject = null;
                        JSONObject successObject = null;
                        JSONObject agentObject = null;
                        JSONObject balanceObject = null;
                        JSONObject projectsObject = null;
                        JSONArray projectsArray = null;
                        JSONObject bonusObject = null;
                        JSONArray bonusArray = null;
                        try {
                            jsonObject = new JSONObject(data);
                            ApplicationSettings.putPref(AppConstants.UEARN_PASSBOOK_LIFETIME, data);
                            if (jsonObject != null && jsonObject.has("success")) {
                                successObject = jsonObject.getJSONObject("success");

                                if (successObject != null && successObject.has("currency")) {
                                    passbookLifetimeObj.currency = successObject.optString("currency");
                                }

                                if (successObject != null && successObject.has("agent")) {
                                    agentObject = successObject.getJSONObject("agent");
                                    if (agentObject != null && agentObject.has("doj")) {
                                        JSONObject dateOfJoinObject = agentObject.getJSONObject("doj");
                                        if (dateOfJoinObject != null && dateOfJoinObject.has("message")) {
                                            String dateOfJoiningTxt = dateOfJoinObject.optString("message");
                                            if(dateOfJoiningTxt != null && !dateOfJoiningTxt.isEmpty()) {
                                                passbookLifetimeObj.date_of_join_txt = dateOfJoiningTxt;
                                            }
                                        }
                                        if (dateOfJoinObject != null && dateOfJoinObject.has("value")) {
                                            String dateOfJoining = dateOfJoinObject.optString("value");
                                            if(dateOfJoining != null && !dateOfJoining.isEmpty()) {
                                                dateOfJoining = CommonUtils.convertDate(dateOfJoining);
                                            }
                                            passbookLifetimeObj.date_of_join = dateOfJoining;
                                        }
                                    }
                                    if (agentObject != null && agentObject.has("wd")) {
                                        JSONObject totalWorkingDaysObject = agentObject.getJSONObject("wd");
                                        if (totalWorkingDaysObject != null && totalWorkingDaysObject.has("message")) {
                                            passbookLifetimeObj.total_working_days_txt = totalWorkingDaysObject.optString("message");
                                        }
                                        if (totalWorkingDaysObject != null && totalWorkingDaysObject.has("value")) {
                                            passbookLifetimeObj.total_working_days = totalWorkingDaysObject.optString("value");
                                        }
                                    }
                                    if (agentObject != null && agentObject.has("aht")) {
                                        JSONObject totalActiveTimeObject = agentObject.getJSONObject("aht");
                                        if (totalActiveTimeObject != null && totalActiveTimeObject.has("message")) {
                                            passbookLifetimeObj.total_active_time_txt = totalActiveTimeObject.optString("message");
                                        }
                                        if (totalActiveTimeObject != null && totalActiveTimeObject.has("value")) {
                                            passbookLifetimeObj.total_active_time = totalActiveTimeObject.optString("value");
                                        }
                                    }
                                    if (agentObject != null && agentObject.has("summary")) {
                                        JSONObject summaryObject = agentObject.getJSONObject("summary");
                                        if (summaryObject != null && summaryObject.has("balance")) {
                                            balanceObject = summaryObject.getJSONObject("balance");
                                            if (balanceObject != null && balanceObject.has("payment_done")) {
                                                passbookLifetimeObj.payment_done = balanceObject.optString("payment_done");
                                            }
                                            if (balanceObject != null && balanceObject.has("closing_balance")) {
                                                passbookLifetimeObj.closing_balance = balanceObject.optString("closing_balance");
                                            }
                                            if (balanceObject != null && balanceObject.has("total_earned")) {
                                                passbookLifetimeObj.total_earned = balanceObject.optString("total_earned");
                                            }
                                        }
                                    }
                                }

                                if (successObject != null && successObject.has("projects")) {
                                    projectsObject = successObject.getJSONObject("projects");
                                    if (projectsObject != null && projectsObject.has("list")) {
                                        projectsArray = projectsObject.getJSONArray("list");
                                        if(projectsArray != null && projectsArray.length() > 0){
                                            for (int i = 0; i < projectsArray.length(); i++) {
                                                JSONObject projectsArrayObj = projectsArray.getJSONObject(i);
                                                String name = "";
                                                String earned = "";
                                                if (projectsArrayObj != null) {
                                                    if (projectsArrayObj.has("name")) {
                                                        name = projectsArrayObj.optString("name");
                                                    }
                                                    if (projectsArrayObj.has("value")) {
                                                        earned = projectsArrayObj.optString("value");
                                                    }
                                                }
                                                passbookLifetimeObj.projectsHash.put(name, earned);
                                            }
                                        }
                                    }
                                    if (projectsObject != null && projectsObject.has("summary")) {
                                        JSONObject summaryObject = projectsObject.getJSONObject("summary");
                                        if (summaryObject != null && summaryObject.has("message")) {
                                            String summaryValueTxt = summaryObject.optString("message");
                                            passbookLifetimeObj.call_earning_txt = summaryValueTxt;
                                        }
                                        if (summaryObject != null && summaryObject.has("value")) {
                                            String summaryValue = summaryObject.optString("value");
                                            if(summaryValue != null && !summaryValue.isEmpty()) {
                                                passbookLifetimeObj.call_earning = summaryValue;
                                            }
                                        }
                                    }
                                }

                                if (successObject != null && successObject.has("bonus")) {
                                    bonusObject = successObject.getJSONObject("bonus");
                                    if (bonusObject != null && bonusObject.has("list")) {
                                        bonusArray = bonusObject.getJSONArray("list");
                                        if(bonusArray != null && bonusArray.length() > 0){
                                            for (int i = 0; i < bonusArray.length(); i++) {
                                                JSONObject bonusArrayObj = bonusArray.getJSONObject(i);
                                                String name = "";
                                                String bonus = "";
                                                if (bonusArrayObj != null) {
                                                    if (bonusArrayObj.has("name")) {
                                                        name = bonusArrayObj.optString("name");
                                                    }
                                                    if (bonusArrayObj.has("value")) {
                                                        bonus = bonusArrayObj.optString("value");
                                                    }
                                                }
                                                passbookLifetimeObj.bonusHash.put(name, bonus);
                                            }
                                        }
                                    }
                                    if (bonusObject != null && bonusObject.has("summary")) {
                                        JSONObject summaryObject = bonusObject.getJSONObject("summary");
                                        if (summaryObject != null && summaryObject.has("message")) {
                                            passbookLifetimeObj.bonus_txt = summaryObject.optString("message");
                                        }
                                        if (summaryObject != null && summaryObject.has("value")) {
                                            passbookLifetimeObj.bonus = summaryObject.optString("value");
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).call();
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            String data = ApplicationSettings.getPref(AppConstants.UEARN_PASSBOOK_LIFETIME, "");
            if (data != null && !data.isEmpty()) {
                JSONObject jsonObject = null;
                JSONObject successObject = null;
                JSONObject agentObject = null;
                JSONObject balanceObject = null;
                JSONObject projectsObject = null;
                JSONArray projectsArray = null;
                JSONObject bonusObject = null;
                JSONArray bonusArray = null;
                try {
                    jsonObject = new JSONObject(data);
                    if (jsonObject != null && jsonObject.has("success")) {
                        successObject = jsonObject.getJSONObject("success");

                        if (successObject != null && successObject.has("currency")) {
                            passbookLifetimeObj.currency = successObject.optString("currency");
                        }

                        if (successObject != null && successObject.has("agent")) {
                            agentObject = successObject.getJSONObject("agent");
                            if (agentObject != null && agentObject.has("doj")) {
                                JSONObject dateOfJoinObject = agentObject.getJSONObject("doj");
                                if (dateOfJoinObject != null && dateOfJoinObject.has("message")) {
                                    String dateOfJoiningTxt = dateOfJoinObject.optString("message");
                                    if(dateOfJoiningTxt != null && !dateOfJoiningTxt.isEmpty()) {
                                        passbookLifetimeObj.date_of_join_txt = dateOfJoiningTxt;
                                    }
                                }
                                if (dateOfJoinObject != null && dateOfJoinObject.has("value")) {
                                    String dateOfJoining = dateOfJoinObject.optString("value");
                                    if(dateOfJoining != null && !dateOfJoining.isEmpty()) {
                                        dateOfJoining = CommonUtils.convertDate(dateOfJoining);
                                    }
                                    passbookLifetimeObj.date_of_join = dateOfJoining;
                                }
                            }
                            if (agentObject != null && agentObject.has("wd")) {
                                JSONObject totalWorkingDaysObject = agentObject.getJSONObject("wd");
                                if (totalWorkingDaysObject != null && totalWorkingDaysObject.has("message")) {
                                    passbookLifetimeObj.total_working_days_txt = totalWorkingDaysObject.optString("message");
                                }
                                if (totalWorkingDaysObject != null && totalWorkingDaysObject.has("value")) {
                                    passbookLifetimeObj.total_working_days = totalWorkingDaysObject.optString("value");
                                }
                            }
                            if (agentObject != null && agentObject.has("aht")) {
                                JSONObject totalActiveTimeObject = agentObject.getJSONObject("aht");
                                if (totalActiveTimeObject != null && totalActiveTimeObject.has("message")) {
                                    passbookLifetimeObj.total_active_time_txt = totalActiveTimeObject.optString("message");
                                }
                                if (totalActiveTimeObject != null && totalActiveTimeObject.has("value")) {
                                    passbookLifetimeObj.total_active_time = totalActiveTimeObject.optString("value");
                                }
                            }
                            if (agentObject != null && agentObject.has("summary")) {
                                JSONObject summaryObject = agentObject.getJSONObject("summary");
                                if (summaryObject != null && summaryObject.has("balance")) {
                                    balanceObject = summaryObject.getJSONObject("balance");
                                    if (balanceObject != null && balanceObject.has("payment_done")) {
                                        passbookLifetimeObj.payment_done = balanceObject.optString("payment_done");
                                    }
                                    if (balanceObject != null && balanceObject.has("closing_balance")) {
                                        passbookLifetimeObj.closing_balance = balanceObject.optString("closing_balance");
                                    }
                                    if (balanceObject != null && balanceObject.has("total_earned")) {
                                        passbookLifetimeObj.total_earned = balanceObject.optString("total_earned");
                                    }
                                }
                            }
                        }

                        if (successObject != null && successObject.has("projects")) {
                            projectsObject = successObject.getJSONObject("projects");
                            if (projectsObject != null && projectsObject.has("list")) {
                                projectsArray = projectsObject.getJSONArray("list");
                                if(projectsArray != null && projectsArray.length() > 0){
                                    for (int i = 0; i < projectsArray.length(); i++) {
                                        JSONObject projectsArrayObj = projectsArray.getJSONObject(i);
                                        String name = "";
                                        String earned = "";
                                        if (projectsArrayObj != null) {
                                            if (projectsArrayObj.has("name")) {
                                                name = projectsArrayObj.optString("name");
                                            }
                                            if (projectsArrayObj.has("value")) {
                                                earned = projectsArrayObj.optString("value");
                                            }
                                        }
                                        passbookLifetimeObj.projectsHash.put(name, earned);
                                    }
                                }
                            }
                            if (projectsObject != null && projectsObject.has("summary")) {
                                JSONObject summaryObject = projectsObject.getJSONObject("summary");
                                if (summaryObject != null && summaryObject.has("message")) {
                                    String summaryValueTxt = summaryObject.optString("message");
                                    passbookLifetimeObj.call_earning_txt = summaryValueTxt;
                                }
                                if (summaryObject != null && summaryObject.has("value")) {
                                    String summaryValue = summaryObject.optString("value");
                                    if(summaryValue != null && !summaryValue.isEmpty()) {
                                        passbookLifetimeObj.call_earning = summaryValue;
                                    }
                                }
                            }
                        }

                        if (successObject != null && successObject.has("bonus")) {
                            bonusObject = successObject.getJSONObject("bonus");
                            if (bonusObject != null && bonusObject.has("list")) {
                                bonusArray = bonusObject.getJSONArray("list");
                                if(bonusArray != null && bonusArray.length() > 0){
                                    for (int i = 0; i < bonusArray.length(); i++) {
                                        JSONObject bonusArrayObj = bonusArray.getJSONObject(i);
                                        String name = "";
                                        String bonus = "";
                                        if (bonusArrayObj != null) {
                                            if (bonusArrayObj.has("name")) {
                                                name = bonusArrayObj.optString("name");
                                            }
                                            if (bonusArrayObj.has("value")) {
                                                bonus = bonusArrayObj.optString("value");
                                            }
                                        }
                                        passbookLifetimeObj.bonusHash.put(name, bonus);
                                    }
                                }
                            }
                            if (bonusObject != null && bonusObject.has("summary")) {
                                JSONObject summaryObject = bonusObject.getJSONObject("summary");
                                if (summaryObject != null && summaryObject.has("message")) {
                                    passbookLifetimeObj.bonus_txt = summaryObject.optString("message");
                                }
                                if (summaryObject != null && summaryObject.has("value")) {
                                    passbookLifetimeObj.bonus = summaryObject.optString("value");
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }
    }


    private void getOfflinePassbookMonth() {
        String data = ApplicationSettings.getPref(AppConstants.UEARN_PASSBOOK_MONTH, "");
        if (data != null && !data.isEmpty()) {
            JSONObject jsonObject = null;
            JSONArray monthArray = null;
            JSONObject monthObj = null;
            try {
                jsonObject = new JSONObject(data);
                if (jsonObject.has("success")) {
                    monthArray = jsonObject.getJSONArray("success");
                    for (int i = 0; i < monthArray.length(); i++) {
                        monthObj = monthArray.getJSONObject(i);
                        UearnPassbookInfo passbookInfoObj = new UearnPassbookInfo();
                        passbookInfoObj.year = monthObj.optString("year");
                        passbookInfoObj.month = monthObj.optString("month");
                        passbookInfoObj.start_date = monthObj.optString("start_date");
                        passbookInfoObj.end_date = monthObj.optString("end_date");
                        passbookInfoList.add(passbookInfoObj);
                    }

                    if (passbookInfoList.size() <= 4) {
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    } else {
                        tabLayout.setTabGravity(4);
                        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    }

                    uearn_layout.setVisibility(View.VISIBLE);
                    UearnPassbookInfo info = passbookInfoList.get(passbookInfoList.size() - 1);
                    currentMonth.setText(info.month);

                    adapter = new UearnPassbookViewpagerAdapter(getSupportFragmentManager(), passbookInfoList);
                    tabLayout.setupWithViewPager(viewPager);
                    viewPager.setAdapter(adapter);


                    tabLayout.getTabAt(passbookInfoList.size() - 1).select();
                    viewPager.setCurrentItem(passbookInfoList.size() - 1);
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            viewPager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void getUearnMonth() {
        passbookInfoList = new ArrayList<>();
        if (CommonUtils.isNetworkAvailable(this)) {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Date startDate = new Date();
            String event_start_time = CommonUtils.getTimeFormatInISO(startDate);
            UserActivities userActivities = new UserActivities(userId, event_start_time, "");

            new APIProvider.GetUearnwdMonthPassbook(userActivities, 0, this, "Processing your request.", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                    if (data != null && !data.isEmpty()) {
                        JSONObject jsonObject = null;
                        JSONArray monthArray = null;
                        JSONObject monthObj = null;
                        try {
                            jsonObject = new JSONObject(data);
                            if (jsonObject.has("success")) {
                                ApplicationSettings.putPref(AppConstants.UEARN_PASSBOOK_MONTH, "");
                                ApplicationSettings.putPref(AppConstants.UEARN_PASSBOOK_MONTH, data);
                                monthArray = jsonObject.getJSONArray("success");
                                for (int i = 0; i < monthArray.length(); i++) {
                                    monthObj = monthArray.getJSONObject(i);
                                    UearnPassbookInfo passbookInfoObj = new UearnPassbookInfo();
                                    passbookInfoObj.year = monthObj.optString("year");
                                    passbookInfoObj.month = monthObj.optString("month");
                                    passbookInfoObj.start_date = monthObj.optString("start_date");
                                    passbookInfoObj.end_date = monthObj.optString("end_date");
                                    passbookInfoList.add(passbookInfoObj);
                                }

                                if (passbookInfoList.size() <= 4) {
                                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                                } else {
                                    tabLayout.setTabGravity(4);
                                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                                }

                                uearn_layout.setVisibility(View.VISIBLE);
                                UearnPassbookInfo info = passbookInfoList.get(passbookInfoList.size() - 1);
                                currentMonth.setText(info.month);

                                adapter = new UearnPassbookViewpagerAdapter(getSupportFragmentManager(), passbookInfoList);
                                tabLayout.setupWithViewPager(viewPager);
                                viewPager.setAdapter(adapter);


                                tabLayout.getTabAt(passbookInfoList.size() - 1).select();
                                viewPager.setCurrentItem(passbookInfoList.size() - 1);
                                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        int currentPos = tab.getPosition();
                                        UearnPassbookInfo info = passbookInfoList.get(currentPos);
                                        currentMonth.setText(info.month);
                                        viewPager.setCurrentItem(tab.getPosition());
                                    }

                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {
                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Handler uiHandler = new Handler();
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                getOfflinePassbookMonth();
                            }
                        });
                    }
                }
            }).call();
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            String data = ApplicationSettings.getPref(AppConstants.UEARN_PASSBOOK_MONTH, "");
            if (data != null && !data.isEmpty()) {
                JSONObject jsonObject = null;
                JSONArray monthArray = null;
                JSONObject monthObj = null;
                try {
                    jsonObject = new JSONObject(data);
                    if (jsonObject.has("success")) {
                        monthArray = jsonObject.getJSONArray("success");
                        for (int i = 0; i < monthArray.length(); i++) {
                            monthObj = monthArray.getJSONObject(i);
                            UearnPassbookInfo passbookInfoObj = new UearnPassbookInfo();
                            passbookInfoObj.year = monthObj.optString("year");
                            passbookInfoObj.month = monthObj.optString("month");
                            passbookInfoObj.start_date = monthObj.optString("start_date");
                            passbookInfoObj.end_date = monthObj.optString("end_date");
                            passbookInfoList.add(passbookInfoObj);
                        }

                        if (passbookInfoList.size() <= 4) {
                            tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        } else {
                            tabLayout.setTabGravity(4);
                            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                        }

                        uearn_layout.setVisibility(View.VISIBLE);
                        UearnPassbookInfo info = passbookInfoList.get(passbookInfoList.size() - 1);
                        currentMonth.setText(info.month);

                        adapter = new UearnPassbookViewpagerAdapter(getSupportFragmentManager(), passbookInfoList);
                        tabLayout.setupWithViewPager(viewPager);
                        viewPager.setAdapter(adapter);


                        tabLayout.getTabAt(passbookInfoList.size() - 1).select();
                        viewPager.setCurrentItem(passbookInfoList.size() - 1);
                        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                viewPager.setCurrentItem(tab.getPosition());
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {
                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    public void restoreActionBar(String mTitle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7030a0")));
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.today_card:
                viewPager.setVisibility(View.VISIBLE);
                lifetimeEarnedView.setVisibility(View.GONE);
                total_earned_till_date.setVisibility(View.GONE);
                uearnPassbookScrollView.setVisibility(View.GONE);
                todayEarnedView.setVisibility(View.VISIBLE);
                if (!isShowMore) {
                    showmoreImg.setImageResource(R.drawable.up_arrow);
                    tabLayout.setVisibility(View.VISIBLE);
                    isShowMore = true;
                } else {
                    showmoreImg.setImageResource(R.drawable.down_arrow);
                    isShowMore = false;
                    tabLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.lifetimeEarnedCard:
                viewPager.setVisibility(View.GONE);
                getUearnPassbookLifetimeData();
                setVisibilityPassbookLifetime();
                showmoreImg.setImageResource(R.drawable.down_arrow);
                isShowMore = false;
                tabLayout.setVisibility(View.GONE);
                lifetimeEarnedView.setVisibility(View.VISIBLE);
                total_earned_till_date.setText("TOTAL EARNED TILL DATE\n" + "  " + passbookLifetimeObj.currency + " " +  passbookLifetimeObj.total_earned);
                total_earned_till_date.setTextColor(getApplication().getResources().getColor(R.color.black_text_color));
                total_earned_till_date.setBackgroundResource(R.drawable.grey_rounded_lifetime);
                todayEarnedView.setVisibility(View.GONE);

                dateOfJoinTxt.setText(passbookLifetimeObj.date_of_join_txt);
                dateOfJoin.setText(passbookLifetimeObj.date_of_join);
                totalWorkingDaysTxt.setText(passbookLifetimeObj.total_working_days_txt);
                totalWorkingDays.setText(passbookLifetimeObj.total_working_days);
                totalActiveTimeTxt.setText(passbookLifetimeObj.total_active_time_txt);
                totalActiveTime.setText(passbookLifetimeObj.total_active_time);
                totalPaymentDone.setText(passbookLifetimeObj.currency + " " + passbookLifetimeObj.payment_done);
                closingBalance.setText(passbookLifetimeObj.currency + " " + passbookLifetimeObj.closing_balance);
                callEarningTxt.setText(passbookLifetimeObj.call_earning_txt);
                callEarning.setText(passbookLifetimeObj.currency + " " + passbookLifetimeObj.call_earning);
                bonusTxt.setText(passbookLifetimeObj.bonus_txt);
                bonus.setText(passbookLifetimeObj.currency + " " + passbookLifetimeObj.bonus);

                createProjectTableLayout();
                createBonusTableLayout();

                break;
        }
    }

    private void createProjectTableLayout() {
        TableLayout projectsTable = (TableLayout) findViewById(R.id.projects_table);

        TableRow projectsTableRow = new TableRow(this);
        projectsTableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView projectsTextView = new TextView(this);
        TableRow.LayoutParams projectsTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        projectsTextView.setLayoutParams(projectsTextViewParam);
        projectsTextView.setGravity(Gravity.LEFT);
        projectsTextView.setPadding(9, 15, 0, 0);
        String htmlString="<u>PROJECTS</u>";
        projectsTextView.setText(Html.fromHtml(htmlString));
        projectsTextView.setTypeface(null, Typeface.BOLD);
        projectsTableRow.addView(projectsTextView);

        projectsTable.removeAllViews();
        projectsTable.addView(projectsTableRow);

        Set set = passbookLifetimeObj.projectsHash.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            String name = (String) me.getKey();
            String earned = (String) me.getValue();

            TableRow projectsListTableRow = new TableRow(this);
            projectsListTableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView projectNameTextView = new TextView(this);
            TableRow.LayoutParams projectNameTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
            projectNameTextView.setLayoutParams(projectNameTextViewParam);
            projectNameTextView.setGravity(Gravity.LEFT);
            projectNameTextView.setPadding(9, 15, 0, 0);
            projectNameTextView.setText(name);
            projectsListTableRow.addView(projectNameTextView);

            TextView projectEarnedTextView = new TextView(this);
            TableRow.LayoutParams projectEarnedTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
            projectEarnedTextView.setLayoutParams(projectEarnedTextViewParam);
            projectEarnedTextView.setGravity(Gravity.RIGHT);
            projectEarnedTextView.setPadding(0, 15, 9, 5);
            projectEarnedTextView.setText(passbookLifetimeObj.currency + " " + earned);
            projectsListTableRow.addView(projectEarnedTextView);

            projectsTable.addView(projectsListTableRow);
        }
    }

    private void createBonusTableLayout() {
        TableLayout bonusTable = (TableLayout) findViewById(R.id.bonus_table);

        TableRow bonusTableRow = new TableRow(this);
        bonusTableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView bonusTextView = new TextView(this);
        TableRow.LayoutParams bonusTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        bonusTextView.setLayoutParams(bonusTextViewParam);
        bonusTextView.setGravity(Gravity.LEFT);
        bonusTextView.setPadding(9, 15, 0, 0);
        String htmlString="<u>BONUS</u>";
        bonusTextView.setText(Html.fromHtml(htmlString));
        bonusTextView.setTypeface(null, Typeface.BOLD);
        bonusTableRow.addView(bonusTextView);

        bonusTable.removeAllViews();
        bonusTable.addView(bonusTableRow);

        if(passbookLifetimeObj.bonusHash != null && passbookLifetimeObj.bonusHash.size() > 0) {

            Set set = passbookLifetimeObj.bonusHash.entrySet();
            Iterator i = set.iterator();
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                String name = (String) me.getKey();
                String bonus = (String) me.getValue();

                TableRow bonusListTableRow = new TableRow(this);
                bonusListTableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView bonusNameTextView = new TextView(this);
                TableRow.LayoutParams bonusNameTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
                bonusNameTextView.setLayoutParams(bonusNameTextViewParam);
                bonusNameTextView.setGravity(Gravity.LEFT);
                bonusNameTextView.setPadding(9, 15, 0, 5);
                bonusNameTextView.setText(name);
                bonusListTableRow.addView(bonusNameTextView);

                TextView bonusValueTextView = new TextView(this);
                TableRow.LayoutParams bonusValueTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
                bonusValueTextView.setLayoutParams(bonusValueTextViewParam);
                bonusValueTextView.setGravity(Gravity.RIGHT);
                bonusValueTextView.setPadding(0, 15, 9, 0);
                bonusValueTextView.setText(passbookLifetimeObj.currency + " " + bonus);
                bonusListTableRow.addView(bonusValueTextView);

                bonusTable.addView(bonusListTableRow);
            }
        }
    }

    private void setVisibilityPassbookLifetime() {
        uearnPassbookScrollView.setVisibility(View.VISIBLE);
        uearnPassbookLayout.setVisibility(View.VISIBLE);
        total_earned_till_date.setVisibility(View.VISIBLE);
        infoCard.setVisibility(View.VISIBLE);
        projectsCard.setVisibility(View.VISIBLE);
        bonusCard.setVisibility(View.VISIBLE);
        summaryCard.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
