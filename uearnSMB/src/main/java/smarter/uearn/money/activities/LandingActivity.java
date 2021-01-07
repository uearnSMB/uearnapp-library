package smarter.uearn.money.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import smarter.uearn.money.dialogs.CustomNotLoggedDialog;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.R;
import smarter.uearn.money.training.activity.TrainingDashboardActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.ReuploadService;

import static com.facebook.FacebookSdk.getApplicationContext;


public class LandingActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private float lastX;

    private ViewFlipper vfLanding;
    private ImageView dot1, dot2, dot3, dot4, dot5, dot6;
    private GestureDetector mGestureDetector;
    private ImageView logo;
    private TextView logoTitle,logoTitle2, logoContent, logoContent2;
    private TextView signInBtn;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_landing);
        SmarterSMBApplication.currentActivity = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        initializeUiElements();
        client = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(AppInvite.API).build();

        boolean checkNotLoggedDialog = ApplicationSettings.getPref(AppConstants.NOT_LOGGED_SHOW_DIALOG, false);
        if (checkNotLoggedDialog) {
            ApplicationSettings.putPref(AppConstants.NOT_LOGGED_SHOW_DIALOG, false);
            //CustomNotLoggedDialog.buildNotLoggedDialog(this);
        }

        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(client, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {

                                } else {

                                }
                            }
                        });
        changeStatusBarColor(this);
    }

    private void initializeUiElements() {
        dot1 = findViewById(R.id.ivDot1);
        dot2 = findViewById(R.id.ivDot2);
        dot3 = findViewById(R.id.ivDot3);
        logo = findViewById(R.id.logo);
        logoTitle = findViewById(R.id.logo_title);
        logoTitle2 = findViewById(R.id.logo_title2);
        logoContent = findViewById(R.id.logo_content);
        logoContent2 = findViewById(R.id.logo_content2);
        signInBtn = findViewById(R.id.signInBtn);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/sf-pro-display-regular.ttf");
        logoContent.setTypeface(face);
        logoContent2.setTypeface(face);
        signInBtn.setTypeface(face);
        Typeface titleFace = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
        logoTitle.setTypeface(titleFace);
        int color = Color.WHITE;
        vfLanding = findViewById(R.id.vfLanding);

        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        vfLanding.setInAnimation(in);

        for(int i=0; i< 3; i++) {
            ImageView imageView = new ImageView(this);
            overridePendingTransition( R.anim.enter, R.anim.exit);
            vfLanding.addView(imageView);
        }

        logo.setImageResource(R.drawable.earn_home_image);
        logoTitle.setText("Earn from home ");
        logoTitle2.setText("Get into a community of");
        logoContent.setText("customer experience professionals");
        logoContent2.setText("working from home");
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        boolean isFirstTimeGmail = SmarterSMBApplication.appSettings.isFirstTimeGmailAuth();
        if (isFirstTimeGmail) {
            SmarterSMBApplication.appSettings.setFirstTimeGmailAuth(false);
            SmarterSMBApplication.appSettings.setLogout(true);
        }
        boolean logout = true;
        if(ApplicationSettings.containsPref(AppConstants.APP_LOGOUT)) {
            logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        }
        Button btnSignIn = findViewById(R.id.btnSignIn);
        assert btnSignIn != null;
        btnSignIn.setOnClickListener(this);
        signInBtn = findViewById(R.id.signInBtn);
        assert signInBtn != null;
        signInBtn.setOnClickListener(this);
        if (!logout) {
            if (CommonUtils.isNetworkAvailable(this)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startReuploadService();
                    }
                }, 10);
                String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
                if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
                    //Intent i = new Intent(LandingActivity.this, TrainingDashboardActivity.class);
                    Intent i = new Intent(LandingActivity.this, UearnHome.class);
                    startActivity(i);
                }
                else if(userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("Training"))) {
                    Intent i = new Intent(LandingActivity.this, TrainingDashboardActivity.class);
                    startActivity(i);
                }
                finish();
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else {
            boolean isFirstTime = SmarterSMBApplication.appSettings.getFirstTimeLaunch();
            if (isFirstTime) {
                SmarterSMBApplication.appSettings.setFirstTimeLaunch(false);
                SmarterSMBApplication.appSettings.setLogout(true);
            }
        }

        if (ApplicationSettings.getPref(AppConstants.MULTI_DEVICE_SIGN_OUT, false)) {
            ApplicationSettings.putPref(AppConstants.MULTI_DEVICE_SIGN_OUT, false);
            CustomSingleButtonDialog.multiDeviceSignOutDialog(this).show();
        }
    }

    private void startReuploadService() {
        Intent intent = new Intent(this, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    public void onClick(View view) {

        SmarterSMBApplication.signoutNotificationReceived = false;

        Intent authenticationIntent = new Intent(this, SmarterAuthActivity.class);
        String itemName = "";

        switch (view.getId()) {
            case R.id.btnSignIn:
                authenticationIntent.putExtra("screenType", 1);
                itemName = "Sign Up";
                break;

            case R.id.signInBtn:
                authenticationIntent.putExtra("screenType", 0);
                itemName = "Sign In";
                break;

        }

        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_1);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
        mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_1, AnalyticsBundle);
        startActivity(authenticationIntent);
        this.finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {

        mGestureDetector.onTouchEvent(touchevent);

        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() > e2.getX()) {
                vfLanding.setInAnimation(LandingActivity.this, R.anim.enter);
                vfLanding.setOutAnimation(LandingActivity.this, R.anim.exit);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vfLanding.showNext();
                        changeDots(vfLanding.getDisplayedChild());
                    }
                }, 100);
            }

            if (e1.getX() < e2.getX()) {
                vfLanding.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.landing_in_left));
                vfLanding.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.landing_out_right));

                vfLanding.showPrevious();
                changeDots(vfLanding.getDisplayedChild());
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void changeDots(int item) {
        changeSizeOfImage(dot1, 0);
        changeSizeOfImage(dot2, 0);
        changeSizeOfImage(dot3, 0);
        if (item == 0) {
            changeSizeOfImage(dot1, 1);
            dot1.setImageDrawable(this.getResources().getDrawable(R.drawable.ellipse_1));
            logo.setVisibility(View.VISIBLE);
            vfLanding.setVisibility(View.VISIBLE);
            overridePendingTransition( R.anim.enter, R.anim.exit);
            logo.setImageResource(R.drawable.earn_home_image);
            logoTitle2.setVisibility(View.VISIBLE);
            logoContent2.setVisibility(View.VISIBLE);
            logoTitle.setText("Earn from home");
            logoTitle2.setText("Get into a community of");
            logoContent.setText("customer experience professionals");
            logoContent2.setText("working from home");
        } else if (item == 1) {
            changeSizeOfImage(dot2, 1);
            logo.setImageResource(R.drawable.selection_image);
            overridePendingTransition( R.anim.enter, R.anim.exit);
            logoTitle2.setVisibility(View.VISIBLE);
            logoTitle.setText("Selection & training");
            logoTitle2.setText("Register for free & get trained on");
            logoContent.setText("various top projects");
            logoContent2.setVisibility(View.GONE);
        } else if (item == 2) {
            changeSizeOfImage(dot3, 1);
            logo.setImageResource(R.drawable.be_ur_own_boss);
            overridePendingTransition( R.anim.enter, R.anim.exit);
            logoTitle2.setVisibility(View.VISIBLE);
            logoContent2.setVisibility(View.VISIBLE);
            logoTitle.setText("Be your own boss");
            logoTitle2.setText("Flexible work hours, performance");
            logoContent.setText("bonuses & earn for every work");
            logoContent2.setText("unit delivered");
        }
    }

    private void changeSizeOfImage(ImageView dot, int mode) {
        int size;
        int color;

        if (mode == 0) {
            size = 20;
            dot.setImageDrawable(this.getResources().getDrawable(R.drawable.ellipse_108));
        } else {
            size = 20;
            dot.setImageDrawable(this.getResources().getDrawable(R.drawable.ellipse_1));
        }
        ViewGroup.LayoutParams layoutParams = dot.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        dot.setLayoutParams(layoutParams);
    }
}