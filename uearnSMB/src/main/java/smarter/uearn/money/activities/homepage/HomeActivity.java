package smarter.uearn.money.activities.homepage;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.UearnFAQActivity;
import smarter.uearn.money.activities.UearnReferAndEarnActivity;
import smarter.uearn.money.activities.feedback.FeedBackActivity;
import smarter.uearn.money.activities.profilesettings.ProfileActivity;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.ReuploadService;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView homeNav, navClose;
    View DrawerMenu;
    DrawerLayout drawerLayout;
    TextView nevSettings, navAbout, navFAQ, nevRefEarn, navAppVersion, navLogOut, nevFeedBack;
    TextView countNotification;
    RelativeLayout lyNotification;
    String userStatus = "";
    LinearLayout lyBtnWorkSlots, lyBtnInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //=====Navigation Drawer ============

        homeNav = findViewById(R.id.ivNavMenu);
        DrawerMenu = findViewById(R.id.leftDrawerMenu);
        drawerLayout = findViewById(R.id.drawer_layout);
        navClose = findViewById(R.id.imgNavClose);
        nevSettings = findViewById(R.id.nevSettings);
        navAbout = findViewById(R.id.navAbout);
        navFAQ = findViewById(R.id.navFAQ);
        nevRefEarn = findViewById(R.id.nevRefEarn);
        navAppVersion = findViewById(R.id.navAppVersion);
        navLogOut = findViewById(R.id.navLogOut);
        nevFeedBack = findViewById(R.id.nevFeedBack);

        navClose.setOnClickListener(this);
        navLogOut.setOnClickListener(this);
        nevSettings.setOnClickListener(this);
        navAbout.setOnClickListener(this);
        navFAQ.setOnClickListener(this);
        nevRefEarn.setOnClickListener(this);
        navAppVersion.setOnClickListener(this);
        nevFeedBack.setOnClickListener(this);

        lyBtnWorkSlots = findViewById(R.id.lyBtnWorkSlots);
        lyBtnInvite = findViewById(R.id.lyBtnInvite);

        //==========HomePage ToolBar contents contents===================

        lyNotification = findViewById(R.id.lyNotification);
        countNotification = findViewById(R.id.tvNotificationCount);
        lyNotification.setOnClickListener(this);
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLeftDrawer();
            }
        });
        getApplicationVersion();
        validateAppStatus();
    }

    private void validateAppStatus() {

        try {
            if (CommonUtils.allowASF()) {
                lyBtnWorkSlots.setVisibility(View.VISIBLE);
            } else {
                lyBtnWorkSlots.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        boolean isTrue = false;
        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("ON BOARD") || userStatus.equalsIgnoreCase("ON BOARDED"))) {
            isTrue = true;
        } else if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("OJT"))) {
            isTrue = true;
        } else if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("PRODUCTION"))) {
            isTrue = true;
        } else if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("PROJECT") || userStatus.equalsIgnoreCase("PROJECT ASSIGNED"))) {
            isTrue = true;
        } else {
            isTrue = false;
        }
        if (isTrue) {
            lyBtnInvite.setVisibility(View.VISIBLE);
        } else {
            lyBtnInvite.setVisibility(View.GONE);
        }
    }

    private void getApplicationVersion() {
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            String pkgName = getApplicationContext().getPackageName();
            PackageInfo pkgInfo = null;
            try {
                pkgInfo = pm.getPackageInfo(pkgName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String ver = pkgInfo.versionName;
            navAppVersion.setText("App Version " + ver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReferAndEarn() {
        Intent referIntent = new Intent(this, UearnReferAndEarnActivity.class);
        startActivity(referIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void toggleLeftDrawer() {
        if (drawerLayout.isDrawerOpen(DrawerMenu)) {
            drawerLayout.closeDrawer(DrawerMenu);
        } else {
            drawerLayout.openDrawer(DrawerMenu);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        validateAppStatus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        int id = view.getId();
        //==========Home Nav views click====================================
        if (id == R.id.imgNavClose) {
            toggleLeftDrawer();//Close Nav-drawer layout
        } else if (id == R.id.nevSettings) {
            toggleLeftDrawer();//Close Nav-drawer layout
            Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.navAbout) {
            toggleLeftDrawer();//Close Nav-drawer layout
        } else if (id == R.id.nevRefEarn) {
            toggleLeftDrawer();//Close Nav-drawer layout
            getReferAndEarn();
        } else if (id == R.id.navFAQ) {
            toggleLeftDrawer();//Close Nav-drawer layout
            Intent intent = new Intent(this, UearnFAQActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.navAppVersion) {
            toggleLeftDrawer();//Close Nav-drawer layout
        } else if (id == R.id.nevFeedBack) {
            toggleLeftDrawer();
            getUserFeedback();
        } else if (id == R.id.navLogOut) {
            toggleLeftDrawer();//Close Nav-drawer layout
            signOutAndExit();
            //================================================
        } else if (id == R.id.lyNotification) {
        }
    }

    private void getUserFeedback() {
        Intent feedbackIntent = new Intent(this, FeedBackActivity.class);
        startActivity(feedbackIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void signOutAndExit() {
        new CustomTwoButtonDialog().exitApplicationDialog(HomeActivity.this, "Sign out", "You will not get updates any more. Are you sure you want to logout?").show();
    }
}
