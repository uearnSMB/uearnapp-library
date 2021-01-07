package smarter.uearn.money.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;

public class ProcessSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private Button nextButton;
    List<String> checkboxValues = new ArrayList<>();
    public static String currentSelectedCheckBoxValue = "";
    public static int noOfCheckboxChecked = 0;
    public static int noneOfTheAboveChecked = 0;
    private CheckBox voiceCheckbox, emailCheckbox, chatCheckbox;
    private TextView headerTitle;
    private TextView skip;
    private ImageView profileImage,image_sync,image_notification;
    private LinearLayout notificationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_selection);
        changeStatusBarColor(this);

        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("Process Selection");
        profileImage = (ImageView) findViewById(R.id.profile_image_back);
        profileImage.setBackgroundResource(R.drawable.ic_arrow_back_white_24px);
        profileImage.setOnClickListener(this);
        notificationLayout = findViewById(R.id.ll_notification);
        notificationLayout.setOnClickListener(this);
        image_sync = findViewById(R.id.image_sync);
        image_notification = findViewById(R.id.image_notification);


        image_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    settingsApi(userId);
                    Animation rotation = AnimationUtils.loadAnimation(ProcessSelectionActivity.this, R.anim.button_rotate);
                    rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                    image_sync.startAnimation(rotation);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        skip = (TextView) findViewById(R.id.skip);
        skip.setPaintFlags(skip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        skip.setOnClickListener(this);

        nextButton = findViewById(R.id.btnNext);
        nextButton.setOnClickListener(this);
        nextButton.setText("NEXT");
        nextButton.setTextSize(20);
        nextButton.setEnabled(false);
        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);

        // Please do not change the sequence of below code
        voiceCheckbox = findViewById(R.id.voiceCheckbox);
        emailCheckbox = findViewById(R.id.emailCheckbox);
        chatCheckbox = findViewById(R.id.chatCheckbox);


        if (ApplicationSettings.containsPref(AppConstants.VOICE_TEST_COMPLETED)) {
            boolean voiceTestCompleted = ApplicationSettings.getPref(AppConstants.VOICE_TEST_COMPLETED, false);
            if (voiceTestCompleted) {
                voiceCheckbox.setChecked(true);
                voiceCheckbox.setEnabled(false);
                voiceCheckbox.setClickable(false);
            }
        }

        if (ApplicationSettings.containsPref(AppConstants.GRAMMAR_TEST_COMPLETED)) {
            boolean grammarTestCompleted = ApplicationSettings.getPref(AppConstants.GRAMMAR_TEST_COMPLETED, false);
            if (grammarTestCompleted) {
                emailCheckbox.setChecked(true);
                emailCheckbox.setEnabled(false);
                emailCheckbox.setClickable(false);
            }
        }

        if (ApplicationSettings.containsPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED)) {
            boolean processSelectionChatCompleted = ApplicationSettings.getPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED, false);
            if (processSelectionChatCompleted) {
                chatCheckbox.setChecked(true);
                chatCheckbox.setEnabled(false);
                chatCheckbox.setClickable(false);
            }
        }

        voiceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                     @Override
                                                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                         if (isChecked) {
                                                             voiceCheckbox.setChecked(true);
                                                         }
                                                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                             buttonView.setButtonTintList(ContextCompat.getColorStateList(getBaseContext(), R.color.checkbox_green));
                                                         }

                                                         if (voiceCheckbox.isEnabled() && isChecked) {
                                                             nextButton.setEnabled(true);
                                                             nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                                                         } else {
                                                             if ((emailCheckbox.isChecked()&& emailCheckbox.isEnabled()) || (chatCheckbox.isChecked()&& chatCheckbox.isEnabled())) {
                                                                 nextButton.setEnabled(true);
                                                                 nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                                                             } else {
                                                                 nextButton.setEnabled(false);
                                                                 nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
                                                             }
                                                         }
                                                     }
                                                 }
        );

        emailCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                     @Override
                                                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                         if (isChecked) {
                                                             emailCheckbox.setChecked(true);
                                                         }
                                                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                             buttonView.setButtonTintList(ContextCompat.getColorStateList(getBaseContext(), R.color.checkbox_green));
                                                         }
                                                         if (emailCheckbox.isEnabled() && isChecked) {
                                                             nextButton.setEnabled(true);
                                                             nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                                                         } else {
                                                             if ((voiceCheckbox.isChecked()&& voiceCheckbox.isEnabled()) || (chatCheckbox.isChecked() && chatCheckbox.isEnabled())) {
                                                                 nextButton.setEnabled(true);
                                                                 nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                                                             } else {
                                                                 nextButton.setEnabled(false);
                                                                 nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
                                                             }
                                                         }
                                                     }
                                                 }
        );


        chatCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if (isChecked) {
                                                            chatCheckbox.setChecked(true);
                                                        }
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                            buttonView.setButtonTintList(ContextCompat.getColorStateList(getBaseContext(), R.color.checkbox_green));
                                                        }

                                                        if (chatCheckbox.isEnabled() && isChecked) {
                                                            nextButton.setEnabled(true);
                                                            nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                                                        } else {
                                                            if ((emailCheckbox.isChecked()&& emailCheckbox.isEnabled()) || (voiceCheckbox.isChecked() &&voiceCheckbox.isEnabled()) ) {
                                                                nextButton.setEnabled(true);
                                                                nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                                                            } else {
                                                                nextButton.setEnabled(false);
                                                                nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
                                                            }
                                                        }
                                                    }
                                                }
        );

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
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        navigateToWelcomeActivity();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnNext) {
            String selectedProcess = "";
            if (voiceCheckbox.isEnabled() && voiceCheckbox.isChecked() && emailCheckbox.isEnabled() && emailCheckbox.isChecked() && chatCheckbox.isEnabled() && chatCheckbox.isChecked()) {
                selectedProcess = "Voice" + "," + "Email" + "," + "Chat";
            } else if (voiceCheckbox.isEnabled() && voiceCheckbox.isChecked() && emailCheckbox.isEnabled() && emailCheckbox.isChecked()) {
                selectedProcess = "Voice" + "," + "Email";
            } else if (voiceCheckbox.isEnabled() && voiceCheckbox.isChecked() && chatCheckbox.isEnabled() && chatCheckbox.isChecked()) {
                selectedProcess = "Voice" + "," + "Chat";
            } else if (emailCheckbox.isEnabled() && emailCheckbox.isChecked() && chatCheckbox.isEnabled() && chatCheckbox.isChecked()) {
                selectedProcess = "Email" + "," + "Chat";
            } else if (voiceCheckbox.isEnabled() && voiceCheckbox.isChecked()) {
                selectedProcess = "Voice";
            } else if (emailCheckbox.isEnabled() && emailCheckbox.isChecked()) {
                selectedProcess = selectedProcess + "Email";
            } else if (chatCheckbox.isEnabled() && chatCheckbox.isChecked()) {
                selectedProcess = selectedProcess + "Chat";
            }
            ApplicationSettings.putPref(AppConstants.INTERVIEW_PROCESS, selectedProcess);

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
            } catch (Exception e) {

            }

            ApplicationSettings.putPref(AppConstants.PROCESS_STATUS, processStatusObj.toString());
            postCurrentProcessStatus(processStatusObj.toString());

            SmarterSMBApplication.callingFromProcessSelection = true;
            if (voiceCheckbox.isEnabled() && voiceCheckbox.isChecked()) {
                navigateToDeviceCheckActivity();
            } else if (emailCheckbox.isEnabled() && emailCheckbox.isChecked()) {
                if (ApplicationSettings.containsPref(AppConstants.VOICE_TEST_COMPLETED)) {
                    voiceTestCompleted = ApplicationSettings.getPref(AppConstants.VOICE_TEST_COMPLETED, false);
                    if (voiceTestCompleted) {
                        if (CommonUtils.isNetworkAvailable(this)) {
                            navigateToGrammarSkillTestActivity();
                        } else {
                            Toast.makeText(this, "You have no internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        SmarterSMBApplication.currentSelectedProcess = "Email";
                        navigateToDeviceCheckActivity();
                    }
                } else {
                    SmarterSMBApplication.currentSelectedProcess = "Email";
                    navigateToDeviceCheckActivity();
                }
            } else if (chatCheckbox.isEnabled() && chatCheckbox.isChecked()) {
                navigateToProcessSelectionChatActivity();
            }
        } else if (id == R.id.profile_image_back) {
            onBackPressed();
        } else if (id == R.id.ll_notification) {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            settingsApi(userId);
        } else if (id == R.id.skip) {
            navigateToHomeActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    private void navigateToDeviceCheckActivity() {
        Intent intent = new Intent(this, DeviceCheckActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void navigateToGrammarSkillTestActivity() {
        Intent intent = new Intent(this, GrammarSkillTestActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void navigateToProcessSelectionChatActivity() {
        Intent intent = new Intent(this, ProcessSelectionChatActivity.class);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void navigateToWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
