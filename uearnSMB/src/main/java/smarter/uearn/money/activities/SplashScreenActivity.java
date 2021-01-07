package smarter.uearn.money.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;

import static smarter.uearn.money.utils.SmarterSMBApplication.connectToPushServer;
import static smarter.uearn.money.utils.SmarterSMBApplication.mSocket;
import static smarter.uearn.money.utils.SmarterSMBApplication.pushSioUrl;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private ImageView splashImage;
    private TextView splashText;
    private Activity activity;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        splashImage = findViewById(R.id.splash_image);
        splashText = findViewById(R.id.splash_text);
        //changeStatusBarColor(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        activity = this;
        SmarterSMBApplication.currentActivity = this;

        splashImage.setAlpha(0f);
        splashImage.animate()
                .alpha(1f)
                .setStartDelay(TimeUnit.SECONDS.toMillis(1))
                .setDuration(TimeUnit.SECONDS.toMillis(2));

        splashText.setAlpha(0f);
        splashText.animate()
                .alpha(1f)
                .setStartDelay(TimeUnit.SECONDS.toMillis(1))
                .setDuration(TimeUnit.SECONDS.toMillis(2));

        SmarterSMBApplication.setCurrentActivity(this);
        startUearnSio();

        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        if (logout) {
            getUearnTotalAgentsAndEarnings(true);
            Intent i = new Intent(SplashScreenActivity.this, SmarterAuthActivity.class);
            i.putExtra("screenType", 0);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            ApplicationSettings.putPref(AppConstants.APP_LOGOUT, false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    settingsApi(userId);
                    getUearnTotalAgentsAndEarnings(false);
                }
            }, SPLASH_TIME_OUT);
        }
    }

    public void changeStatusBarColor(AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.app_color_purple));
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

    private void getUearnTotalAgentsAndEarnings(final boolean logout) {
        if (CommonUtils.isNetworkAvailable(this)) {
            if (activity != null && !activity.isFinishing()) {
                new APIProvider.GetUearnTotalAgentsAndEarnings("", 0, null, "Please wait..", new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        if (data != null && !data.isEmpty()) {
                            ApplicationSettings.putPref(AppConstants.UEARN_DATA, data);
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                if (jsonObject.has("uearn_data")) {
                                    String uearnData = jsonObject.getString("uearn_data");
                                    if (uearnData != null && !uearnData.isEmpty()) {
                                        JSONObject uearnDataObject = new JSONObject(uearnData);
                                        if (uearnDataObject.has("user_id")) {
                                            String userId = uearnDataObject.getString("user_id");
                                            if (userId != null && !userId.isEmpty()) {
                                                ApplicationSettings.putPref(AppConstants.USERINFO_ID, userId);
                                            }
                                        }
                                        if (uearnDataObject.has("agent_state")) {
                                            String agentState = uearnDataObject.getString("agent_state");
                                            if (agentState != null && !agentState.isEmpty()) {
                                                ApplicationSettings.putPref(AppConstants.USER_STATUS, agentState);
                                            }
                                        }
                                        if (uearnDataObject.has("interview_process")) {
                                            String processStatus = uearnDataObject.getString("interview_process");
                                            if (processStatus != null && !processStatus.isEmpty() && processStatus.startsWith("{")) {
                                                JSONObject processStatusJsonObject = new JSONObject(processStatus);
                                                if (processStatusJsonObject.has("device_check_completed")) {
                                                    boolean deviceCheckCompleted = processStatusJsonObject.getBoolean("device_check_completed");
                                                    ApplicationSettings.putPref(AppConstants.DEVICE_CHECK_COMPLETED, deviceCheckCompleted);
                                                }
                                                if (processStatusJsonObject.has("voice_test_completed")) {
                                                    boolean voiceTestCompleted = processStatusJsonObject.getBoolean("voice_test_completed");
                                                    ApplicationSettings.putPref(AppConstants.VOICE_TEST_COMPLETED, voiceTestCompleted);
                                                }
                                                if (processStatusJsonObject.has("email_test_completed")) {
                                                    boolean emailTestCompleted = processStatusJsonObject.getBoolean("email_test_completed");
                                                    ApplicationSettings.putPref(AppConstants.GRAMMAR_TEST_COMPLETED, emailTestCompleted);
                                                }
                                                if (processStatusJsonObject.has("chat_test_completed")) {
                                                    boolean chatTestCompleted = processStatusJsonObject.getBoolean("chat_test_completed");
                                                    ApplicationSettings.putPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED, chatTestCompleted);
                                                }
                                                if (processStatusJsonObject.has("call_from_interview_panel_completed")) {
                                                    boolean callFromInterviewPanelCompleted = processStatusJsonObject.getBoolean("call_from_interview_panel_completed");
                                                    ApplicationSettings.putPref(AppConstants.CALL_FROM_INTERVIEW_PANEL_COMPLETED, callFromInterviewPanelCompleted);
                                                }
                                                if (processStatusJsonObject.has("onboarding_process_completed")) {
                                                    boolean onboardingProcessCompleted = processStatusJsonObject.getBoolean("onboarding_process_completed");
                                                    ApplicationSettings.putPref(AppConstants.ONBOARDING_PROCESS_COMPLETED, onboardingProcessCompleted);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (jsonObject.has("languages")) {
                                    String languages = jsonObject.getString("languages");
                                    ApplicationSettings.putPref(AppConstants.LANGUAGES, languages);
                                }
                                if (jsonObject.has("education_qualifications")) {
                                    String educationQualifications = jsonObject.getString("education_qualifications");
                                    ApplicationSettings.putPref(AppConstants.EDUCATION_QUALIFICATIONS, educationQualifications);
                                }
                                if (jsonObject.has("experience")) {
                                    String experience = jsonObject.getString("experience");
                                    ApplicationSettings.putPref(AppConstants.EXPERIENCE, experience);
                                }
                                if (jsonObject.has("hardware_setup")) {
                                    String hardwareSetup = jsonObject.getString("hardware_setup");
                                    ApplicationSettings.putPref(AppConstants.HARDWARE_SETUP, hardwareSetup);
                                }
                            } catch (Exception e) {

                            }

                            if (!logout) {
                                navigateToHome();
                            }
                        } else {
                            Toast.makeText(activity, "No valid response from server", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(SplashScreenActivity.this, JoinUsActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                }).requestForDataCall();
            }
        } else {
            if (!logout) {
                navigateToHome();
            } else {
                Toast.makeText(activity, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SplashScreenActivity.this, JoinUsActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    private void navigateToHome() {
        CommonOperations.subscribeNotificationsCall(true, getApplicationContext());
        boolean voiceTestCompleted = ApplicationSettings.getPref(AppConstants.VOICE_TEST_COMPLETED, false);
        boolean grammarTestCompleted = ApplicationSettings.getPref(AppConstants.GRAMMAR_TEST_COMPLETED, false);
        boolean processSelectionChatCompleted = ApplicationSettings.getPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED, false);

        String activityToStart = "";
        Class<?> cl = null;

        if (voiceTestCompleted || grammarTestCompleted || processSelectionChatCompleted) {
            activityToStart = "smarter.uearn.money.activities.homepage.HomeActivity";
        } else {
            activityToStart = SmarterSMBApplication.getNextActivityName();
        }

        try {
            cl = Class.forName(activityToStart);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        final Class<?> landingClass = cl;
        Intent landingIntent = new Intent(activity, landingClass);
        landingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(landingIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void startUearnSio() {

        String prefUrl = ApplicationSettings.getPref(AppConstants.SIOPUSH_URL, "");
        if (pushSioUrl != null && !pushSioUrl.isEmpty()) {
            if (prefUrl.equalsIgnoreCase(pushSioUrl)) {
                if (mSocket != null && !mSocket.connected()) {
                    connectToPushServer();
                } else {
                    if (mSocket == null) {
                        connectToPushServer();
                    }
                }
            } else {
                pushSioUrl = prefUrl;

                if (mSocket != null && mSocket.connected()) {
                    try {
                        mSocket.disconnect();
                        mSocket = null;
                    } catch (Exception e) {
                    }
                }
                connectToPushServer();
            }
        } else {
            pushSioUrl = prefUrl;
            connectToPushServer();
        }
    }
}
