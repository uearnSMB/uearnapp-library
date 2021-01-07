package smarter.uearn.money.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.homepage.HomeActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;

public class GrammarSkillTestResultActivity extends AppCompatActivity implements View.OnClickListener {

    List<String> checkboxValues = new ArrayList<>();
    private TextView grade, levelText, totalTimeTaken;
    private Button doneButton;
    private TextView headerTitle;
    private ImageView profileImage, profileImageBack,image_sync,image_notification;
    private LinearLayout notificationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_test_complete);
        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("Interview");
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileImage.setBackgroundResource(R.drawable.ic_menu);
        profileImageBack = (ImageView) findViewById(R.id.profile_image_back);
        profileImageBack.setVisibility(View.GONE);
        notificationLayout = (LinearLayout) findViewById(R.id.ll_notification);
        notificationLayout.setOnClickListener(this);

        image_sync = findViewById(R.id.image_sync);
        image_notification = findViewById(R.id.image_notification);
        image_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                settingsApi(userId);
                Animation rotation = AnimationUtils.loadAnimation( GrammarSkillTestResultActivity.this, R.anim.button_rotate);
                rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                v.startAnimation(rotation);
            }
        });
        changeStatusBarColor(this);

        grade = findViewById(R.id.grade);
        grade.setBackgroundResource(R.drawable.grade);
        levelText = findViewById(R.id.levelText);

        doneButton = findViewById(R.id.btnDone);
        doneButton.setOnClickListener(this);
        doneButton.setText("DONE");
        doneButton.setTextSize(20);
        doneButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        doneButton.setBackgroundResource(R.drawable.red_rounded_corner);

        String grammarTestLevel = ApplicationSettings.getPref(AppConstants.GRAMMAR_TEST_LEVEL, "");
        if (grammarTestLevel != null && !grammarTestLevel.isEmpty()) {
            String[] arrOfStr = grammarTestLevel.split(" ", 2);
            for (int i = 0; i < arrOfStr.length; i++) {
                grade.setText(arrOfStr[0]);
                levelText.setText(arrOfStr[1]);
            }
            ApplicationSettings.putPref(AppConstants.GRAMMAR_TEST_LEVEL, "");
        }

        totalTimeTaken = findViewById(R.id.totalTimeTaken);
        long grammarTestTotalTime = ApplicationSettings.getPref(AppConstants.GRAMMAR_TEST_TOTAL_TIME, 0l);
        convertActiveMillisecondsToTime(grammarTestTotalTime);
    }

    private void convertActiveMillisecondsToTime(long totalActiveTime) {
        int hr = (int) (totalActiveTime / (1000 * 60 * 60)) % 24;
        int min = (int) (totalActiveTime / (1000 * 60)) % 60;
        int sec = (int) (totalActiveTime / 1000) % 60;
        String totalTime = "";

        if (hr > 0) {
            if(hr == 1) {
                totalTime = "Time taken: " + hr + " hour, " + min + " minutes, " + sec + " seconds";
            } else {
                totalTime = "Time taken: " + hr + " hours, " + min + " minutes, " + sec + " seconds";
            }
        } else if (min > 0) {
            if(min == 1) {
                totalTime = "Time taken: " + min + " minute, " + sec + " seconds";
            } else {
                totalTime = "Time taken: " + min + " minutes, " + sec + " seconds";
            }
        } else {
            if(sec == 1) {
                totalTime = "Time taken: " + sec + " second";
            } else {
                totalTime = "Time taken: " + sec + " seconds";
            }
        }

        totalTimeTaken.setText(totalTime);
        ApplicationSettings.putPref(AppConstants.GRAMMAR_TEST_TOTAL_TIME, 0l);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
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

        }
        return true;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnDone:
                ApplicationSettings.putPref(AppConstants.GRAMMAR_TEST_COMPLETED, true);

                boolean deviceCheckCompleted = ApplicationSettings.getPref(AppConstants.DEVICE_CHECK_COMPLETED, false);
                boolean voiceTestCompleted = ApplicationSettings.getPref(AppConstants.VOICE_TEST_COMPLETED, false);
                boolean emailTestCompleted = ApplicationSettings.getPref(AppConstants.GRAMMAR_TEST_COMPLETED, false);
                boolean chatTestCompleted = ApplicationSettings.getPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED, false);
                boolean callFromInterviewPanelCompleted = ApplicationSettings.getPref(AppConstants.CALL_FROM_INTERVIEW_PANEL_COMPLETED, false);
                boolean onboardingProcessCompleted = ApplicationSettings.getPref(AppConstants.ONBOARDING_PROCESS_COMPLETED, false);

                JSONObject processStatusObj = null;
                try {
                    processStatusObj = new JSONObject();
                    processStatusObj.put("device_check_completed", deviceCheckCompleted);
                    processStatusObj.put("voice_test_completed", voiceTestCompleted);
                    processStatusObj.put("email_test_completed", emailTestCompleted);
                    processStatusObj.put("chat_test_completed", chatTestCompleted);
                    processStatusObj.put("call_from_interview_panel_completed", callFromInterviewPanelCompleted);
                    processStatusObj.put("onboarding_process_completed", onboardingProcessCompleted);
                } catch (Exception e){

                }

                ApplicationSettings.putPref(AppConstants.PROCESS_STATUS, processStatusObj.toString());
                postCurrentProcessStatus(processStatusObj.toString());
                if (SmarterSMBApplication.callingFromProcessSelection) {
                    String processSelected = ApplicationSettings.getPref(AppConstants.INTERVIEW_PROCESS, "");
                    voiceTestCompleted = ApplicationSettings.getPref(AppConstants.VOICE_TEST_COMPLETED, false);
                    chatTestCompleted = ApplicationSettings.getPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED, false);
                    if (processSelected != null && !processSelected.isEmpty() && processSelected.contains("Voice") && !voiceTestCompleted) {
                        navigateToUearnVoiceTestActivity();
                    } else if (processSelected != null && !processSelected.isEmpty() && processSelected.contains("Chat") && !chatTestCompleted) {
                        navigateToProcessSelectionChatActivity();
                    } else {
                        navigateToHomeActivity();
                    }
                } else {
                    navigateToHomeActivity();
                }
        }
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void navigateToProcessSelectionChatActivity() {
        Intent intent = new Intent(this, ProcessSelectionChatActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        this.finish();
    }

    private void navigateToUearnVoiceTestActivity() {
        Intent intent = new Intent(this, UearnVoiceTestActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        this.finish();
    }

    JSONObject processStatusResponseObj;
    private void postCurrentProcessStatus(String processSelection) {
        if (CommonUtils.isNetworkAvailable(this)) {
            final String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            final JSONObject userDetailObj = new JSONObject();
            try {
                userDetailObj.put("interview_process", processSelection);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new AsyncTask<Void, Void, JSONObject>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    String url = Urls.getUpdateUserDetailsUrl(user_id);
                    processStatusResponseObj = DataUploadUtils.putJsonData(url, userDetailObj);
                    return processStatusResponseObj;
                }

                @Override
                protected void onPostExecute(JSONObject jObject) {
                    super.onPostExecute(jObject);
                    try {
                        if ((processStatusResponseObj != null) && (processStatusResponseObj.getString("success") != null)) {

                        }
                    } catch (JSONException e) {

                    }
                }
            }.execute();
        } else {
            Toast.makeText(this, "You have no internet connection.", Toast.LENGTH_SHORT).show();
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
