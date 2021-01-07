package smarter.uearn.money.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.homepage.HomeActivity;
import smarter.uearn.money.models.UearnVoiceTestInfo;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.UserActivities;

public class UearnVoiceTestActivity extends AppCompatActivity implements View.OnClickListener {

    public static boolean dialingFromList = false;
    private static String[] languageName = new String[10];
    String user_id = "";
    private int selectedIndex = 0;
    List<UearnVoiceTestInfo> voiceTestArr = new ArrayList<>();
    private com.wangjie.wheelview.WheelView wva;
    private Button voiceTestButton;
    private TextView headerTitle;
    private ImageView profileImage, image_sync, image_notification;
    private LinearLayout notificationLayout;
    private boolean isFromDeviceCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uearn_voice_test);
        changeStatusBarColor(this);
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                isFromDeviceCheck = getIntent().getExtras().getBoolean("isFrom", false);
            }
        }

        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("Interview");
        profileImage = (ImageView) findViewById(R.id.profile_image_back);
        profileImage.setBackgroundResource(R.drawable.ic_arrow_back_white_24px);
        profileImage.setOnClickListener(this);
        notificationLayout = (LinearLayout) findViewById(R.id.ll_notification);
        notificationLayout.setOnClickListener(this);

        image_sync = findViewById(R.id.image_sync);
        image_notification = findViewById(R.id.image_notification);
        image_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                settingsApi(userId);
                Animation rotation = AnimationUtils.loadAnimation(UearnVoiceTestActivity.this, R.anim.button_rotate);
                rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                v.startAnimation(rotation);
            }
        });
        user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        getUearnVoiceTestInfos(user_id, "", "");

        voiceTestButton = findViewById(R.id.voiceTest);
        voiceTestButton.setOnClickListener(this);
        voiceTestButton.setText("GET STARTED");
        voiceTestButton.setTextSize(20);
        voiceTestButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        voiceTestButton.setBackgroundResource(R.drawable.red_rounded_corner);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    private void getUearnVoiceTestInfos(String userId, String startTime, String endTime) {

        UserActivities userActivities = new UserActivities(userId, startTime, endTime);
        APIProvider.GetUearnVoiceTestInfo getVoiceTestInfo = new APIProvider.GetUearnVoiceTestInfo(userActivities, 0, this, "Please wait", new API_Response_Listener<String>() {
            @Override
            public void onComplete(String voiceTestArrs, long request_code, int failure_code) {
                if (voiceTestArrs != null) {
                    String getVoiceInfo = ApplicationSettings.getPref("UearnVoiceTestInfo", "");
                    UearnVoiceTestInfo[] listOfCustomers = null;
                    if (getVoiceInfo != null && !getVoiceInfo.isEmpty()) {
                        listOfCustomers = new Gson().fromJson(getVoiceInfo, UearnVoiceTestInfo[].class);
                        languageName = new String[listOfCustomers.length];
                        for (int i = 0; i < listOfCustomers.length; i++) {
                            UearnVoiceTestInfo testInfo = new UearnVoiceTestInfo();
                            String name = listOfCustomers[i].name;
                            String languageType = listOfCustomers[i].languageType;
                            testInfo.name = name;
                            languageName[i] = name;
                            testInfo.languageType = languageType;
                            testInfo.para1 = listOfCustomers[i].para1;
                            testInfo.para2 = listOfCustomers[i].para2;
                            testInfo.para3 = listOfCustomers[i].para3;
                            voiceTestArr.add(testInfo);
                        }
                        UearnVoiceTestInfo[] listOf = null;
                    }
                    getLanguageType();
                }
            }
        });
        getVoiceTestInfo.call();
    }

    public void getLanguageType() {

        Button voiceTest = findViewById(R.id.voiceTest);

        assert voiceTest != null;
        voiceTest.setOnClickListener(this);
        wva = findViewById(R.id.main_wv);

        wva.setOffset(2);

        wva.setSeletion(0);
        wva.setNextFocusLeftId(2);
        wva.setItems(Arrays.asList(languageName));
        wva.setOnWheelViewListener(new com.wangjie.wheelview.WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                selectedIndex = 0;
            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isFromDeviceCheck) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.voiceTest:
                Intent intent2 = new Intent(this, StartUearnVoiceRecord.class);
                selectedIndex = wva.getSeletedIndex();
                UearnVoiceTestInfo info = voiceTestArr.get(selectedIndex);
                intent2.putExtra("VoiceInfo", info);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.profile_image_back:
                onBackPressed();
                break;
            case R.id.ll_notification:
                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                settingsApi(userId);
                break;
        }
    }

    private void settingsApi(final String userId) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.GetSettings(userId, 22, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        }
    }
}