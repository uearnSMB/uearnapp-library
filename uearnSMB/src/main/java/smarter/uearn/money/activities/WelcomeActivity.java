package smarter.uearn.money.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import smarter.uearn.money.activities.feedback.FeedBackActivity;
import smarter.uearn.money.activities.homepage.HomeActivity;
import smarter.uearn.money.activities.interview_process.InterViewProcessActivity;
import smarter.uearn.money.activities.profilesettings.ProfileActivity;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.ReuploadService;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView welcomeText;
    private Button nextButton;
    private TextView imageOne, imageTwo, imageThree;
    private TextView headerTitle;
    private ImageView profileImage, profileImageBack,image_sync,image_notification;
    private LinearLayout notificationLayout;
    private LinearLayout syncLayout;

    ImageView homeNav, navClose;
    View DrawerMenu;
    DrawerLayout drawerLayout;
    TextView nevSettings, navAbout, navFAQ, nevRefEarn, navAppVersion, navLogOut, nevFeedBack;
    String userStatus = "";
    LinearLayout lyBtnWorkSlots,lyBtnInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        changeStatusBarColor(this);

        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("Process Selection");
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileImage.setBackgroundResource(R.drawable.ic_menu);
        profileImage.setVisibility(View.VISIBLE);
        profileImageBack = (ImageView) findViewById(R.id.profile_image_back);
        profileImageBack.setVisibility(View.GONE);
        notificationLayout = findViewById(R.id.ll_notification);
        notificationLayout.setOnClickListener(this);
        syncLayout = findViewById(R.id.ll_sync);
        syncLayout.setOnClickListener(this);

        image_sync = findViewById(R.id.image_sync);
        image_notification = findViewById(R.id.image_notification);
        image_sync.setOnClickListener(this);

        //=====Navigation Drawer ============

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
        profileImage.setOnClickListener(this);

        lyBtnWorkSlots = findViewById(R.id.lyBtnWorkSlots);
        lyBtnInvite = findViewById(R.id.lyBtnInvite);

        //==========HomePage ToolBar contents contents===================

        welcomeText = findViewById(R.id.welcomeText1);
        nextButton = findViewById(R.id.btnNext);
        nextButton.setOnClickListener(this);
        nextButton.setText("NEXT");
        nextButton.setTextSize(20);
        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);

        if (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null) {
            String userName = ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
            String welcomeMsg = userName + " you have been successfully registered to Uearn.";
            welcomeText.setText(welcomeMsg);
        }

        imageOne = findViewById(R.id.image_one);
        imageTwo = findViewById(R.id.image_two);
        imageThree = findViewById(R.id.image_three);
        settingsApi(SmarterSMBApplication.SmartUser.getId());
        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, false);
        CommonOperations.subscribeNotificationsCall(true, this);
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
        SmarterSMBApplication.setCurrentActivity(this);
        validateAppStatus();
        super.onResume();
    }

    public void changeStatusBarColor(AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            toggleLeftDrawer();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnNext:
                String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
                if (userStatus != null && !userStatus.isEmpty() && userStatus.equalsIgnoreCase("Voice Test Completed")) {
                    navigateToOnboardingActivity();
                } else {
                    navigateToUearnProcessSelectionActivity();
                }
                break;
            case R.id.image_sync:
               try{
                   String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                   settingsApi(userId);
                   Animation rotation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.button_rotate);
                   rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                   image_sync.startAnimation(rotation);
               }catch (Exception e){
                   e.printStackTrace();
               }
               break;

            case R.id.imgNavClose: {
                toggleLeftDrawer();
                break;
            }

            case R.id.nevSettings: {
                toggleLeftDrawer();
                Intent i = new Intent(WelcomeActivity.this, ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }

            case R.id.navAbout: {
                toggleLeftDrawer();
                break;
            }
            case R.id.nevRefEarn: {
                toggleLeftDrawer();
                getReferAndEarn();
                break;
            }

            case R.id.navFAQ: {
                toggleLeftDrawer();
                Intent intent = new Intent(this, UearnFAQActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }

            case R.id.navAppVersion: {
                toggleLeftDrawer();
                break;
            }

            case R.id.nevFeedBack: {
                getUserFeedback();
                break;
            }

            case R.id.profile_image: {
                toggleLeftDrawer();
                break;
            }

            case R.id.navLogOut: {
                signOutAndExit();
                toggleLeftDrawer();
                break;
            }
        }
    }

    private void getUserFeedback() {
        Intent feedbackIntent = new Intent(this, FeedBackActivity.class);
        startActivity(feedbackIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void navigateToUearnProcessSelectionActivity() {
        Intent intent = new Intent(this, ProcessSelectionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void navigateToOnboardingActivity() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

    //===============SignOut==================================

    private void signOutAndExit() {
        boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
        if (truePredictive) {
            SmarterSMBApplication.triggerDataLoadingAfterSubmit = false;
        }
        SmarterSMBApplication.moveToRAD = false;
        String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
            SmarterSMBApplication.signoutNotificationReceived = false;
            File dir = new File(Environment.getExternalStorageDirectory() + "/callrecorder/");
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (ApplicationSettings.containsPref(AppConstants.SSU_ON_SIO)) {
                boolean ssuonsio = ApplicationSettings.getPref(AppConstants.SSU_ON_SIO, false);
                if (ssuonsio) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sendSalesEntriesInfoToServer();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            }

            Cursor cursor = null;
            if (ApplicationSettings.containsPref(AppConstants.UPLOAD_STATUS2)) {
                boolean uploadStatus2 = ApplicationSettings.getPref(AppConstants.UPLOAD_STATUS2, false);
                if (!uploadStatus2) {
                    cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=0", null);
                } else {
                    cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=2", null);
                }
            } else {
                cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=2", null);
            }

            if (cursor != null && cursor.getCount() > 0) {
                callReuploadService();
                checkInternalFiles();
            } else {
                String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                    SmarterSMBApplication.isRemoteDialledStart = false;
                    SmarterSMBApplication.isFirstCall = false;

                    if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
                        boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
                        if (systemControl) {
                            SmarterSMBApplication.disableStatusBarAndNavigation = false;
                        }
                    }

                    //Log.d("RemoteDialledStart","1655    " +SmarterSMBApplication.isRemoteDialledStart);
                    sendRemoteDialStopRequest("Taking a break");
                    if (truePredictive) {
                        //killCall(UearnProfileActivity.this);
                    }
                    new CustomTwoButtonDialog().exitApplicationDialog(WelcomeActivity.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
                } else {
                    String selection = "UPLOAD_STATUS" + "=" + 0;
                    cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
                    if (dir.isDirectory()) {
                        String[] children = dir.list();
                        if ((children != null && children.length > 0) || (cursor != null && cursor.getCount() > 0)) {
                            callReuploadService();
                            checkInternalFiles();
                        } else {
                            new CustomTwoButtonDialog().exitApplicationDialog(WelcomeActivity.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
                        }
                    } else {
                        new CustomTwoButtonDialog().exitApplicationDialog(WelcomeActivity.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
                    }
                }
            }
        } else {
            new CustomTwoButtonDialog().exitApplicationDialog(WelcomeActivity.this, "Sign out", "You will not get updates any more. Are you sure you want to logout?").show();
        }
    }

    private void sendSalesEntriesInfoToServer() {
        String salesStatusFromDb = getAllSalesStatusEntriesFromDB();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")) {
                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
            }
            jsonObject.put("salesstatusentries", salesStatusFromDb);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            DataUploadUtils.postSalesEntries(Urls.postSalesEntriesUrl(), jsonObject);
        }
    }

    private void callReuploadService() {
        Intent aIntent = new Intent(this, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(aIntent);
        } else {
            this.startService(aIntent);
        }
    }

    public String getAllSalesStatusEntriesFromDB() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM SmarterContact", null);

        if (cursor != null && cursor.getCount() > 0) {
            JSONArray resultSet = new JSONArray();
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null) {
                        try {
                            if (cursor.getString(i) != null) {
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }
            cursor.close();
            if (db != null && db.isOpen()) {
                db.close();
            }
            return resultSet.toString();
        } else {
            return "";
        }
    }

    private void checkInternalFiles() {
        final Dialog dialog = CustomTwoButtonDialog.buildTwoButtonDialog(this, "Warning", "There is data to be uploaded, please don't sign out. Connect to internet & sync before you sign out.");
        TextView btnNo = dialog.findViewById(R.id.btn_no);
        btnNo.setText("OK");
        btnNo.setTextSize(10);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        TextView btnYes = dialog.findViewById(R.id.btn_yes);
        btnYes.setText("FORCE LOGOUT");
        btnYes.setTextSize(10);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new CustomTwoButtonDialog().exitApplicationDialog(WelcomeActivity.this, "Sign out", "Your business activities will not be tracked any more. Are you sure you want to log out?").show();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                sendBroadcast(broadcastIntent);
            }
        });
        dialog.show();
    }

    private void sendRemoteDialStopRequest(String message) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.GetRemoteDialerStop(message, 0, this, "Stopping. Please wait..", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    String text = "";
                    try {
                        SmarterSMBApplication.isRemoteDialledStart = false;
                        SmarterSMBApplication.isRemoteDialledStopRequest = true;

                        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
                            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
                            if (systemControl) {
                                SmarterSMBApplication.disableStatusBarAndNavigation = false;
                            }
                        }
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.has("text")) {
                            text = jsonObject.getString("text");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }
}
