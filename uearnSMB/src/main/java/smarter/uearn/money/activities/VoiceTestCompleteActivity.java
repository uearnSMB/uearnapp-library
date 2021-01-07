package smarter.uearn.money.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.homepage.HomeActivity;
import smarter.uearn.money.adapters.ProcessSelectionAdapter;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.views.events.CheckBoxInterface;

public class VoiceTestCompleteActivity extends AppCompatActivity implements View.OnClickListener, CheckBoxInterface {

    public static Button submitButton;
    List<String> checkboxValues = new ArrayList<>();
    private LinkedHashMap checkboxHash = new LinkedHashMap();
    private HashMap<String, Boolean> checkedDataHashMap, selectedCheckedDataHashMap;
    private LinearLayout checkboxLayout;
    private TextView completedTextView, callfromTextView;
    private Activity activity;
    private TextView headerTitle;
    private ImageView profileImage, profileImageBack, image_sync, image_notification;
    private LinearLayout notificationLayout;
    private RadioButton rdb_morning, rdb_afternoon, rdb_evening;
    String checkBoxSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_test_complete);
        changeStatusBarColor(this);
        activity = this;
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
                Animation rotation = AnimationUtils.loadAnimation(VoiceTestCompleteActivity.this, R.anim.button_rotate);
                rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                v.startAnimation(rotation);
            }
        });

        completedTextView = findViewById(R.id.completedtv);
        callfromTextView = findViewById(R.id.callfromtv);

        rdb_morning = (RadioButton) findViewById(R.id.rdb_morning);
        rdb_afternoon = (RadioButton) findViewById(R.id.rdb_afternoon);
        rdb_evening = (RadioButton) findViewById(R.id.rdb_evening);

        submitButton = findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(this);
        submitButton.setText("SUBMIT");
        submitButton.setTextSize(20);
        submitButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        submitButton.setEnabled(false);
        submitButton.setClickable(false);
        submitButton.setBackgroundResource(R.drawable.disable_rounded_corner);

        checkboxLayout = (LinearLayout) findViewById(R.id.checkboxLayout);
        checkboxLayout.setVisibility(View.VISIBLE);

        if (SmarterSMBApplication.isServiceAgreementDone) {
            headerTitle.setText("Onboarding");
            completedTextView.setText("You have completed the on boarding process.");
            callfromTextView.setText("You'll receive a call from our training manager within 48 hrs.");
        } else {
            headerTitle.setText("Interview");
            completedTextView.setText("You have successfully completed your voice test.");
            callfromTextView.setText("You'll receive a call from our interview panel within 48 hrs.");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        int id = view.getId();
        if (id == R.id.rdb_morning) {
            if (checked)
                checkBoxSelected = "Morning (09:00 AM - 12:00 PM)";
            rdb_morning.setButtonTintList(ContextCompat.getColorStateList(VoiceTestCompleteActivity.this, R.color.checkbox_green));
            rdb_afternoon.setButtonTintList(ContextCompat.getColorStateList(VoiceTestCompleteActivity.this, R.color.checkbox_gray));
            rdb_evening.setButtonTintList(ContextCompat.getColorStateList(VoiceTestCompleteActivity.this, R.color.checkbox_gray));
            rdb_morning.setChecked(true);
            rdb_afternoon.setChecked(false);
            rdb_evening.setChecked(false);
        } else if (id == R.id.rdb_afternoon) {
            if (checked)
                checkBoxSelected = "Afternoon (12:00 PM - 03:00 PM)";
            rdb_afternoon.setButtonTintList(ContextCompat.getColorStateList(VoiceTestCompleteActivity.this, R.color.checkbox_green));
            rdb_morning.setButtonTintList(ContextCompat.getColorStateList(VoiceTestCompleteActivity.this, R.color.checkbox_gray));
            rdb_evening.setButtonTintList(ContextCompat.getColorStateList(VoiceTestCompleteActivity.this, R.color.checkbox_gray));
            rdb_afternoon.setChecked(true);
            rdb_morning.setChecked(false);
            rdb_evening.setChecked(false);
        } else if (id == R.id.rdb_evening) {
            if (checked)
                checkBoxSelected = "Evening (03:00 PM - 06:00 PM)";
            rdb_evening.setButtonTintList(ContextCompat.getColorStateList(VoiceTestCompleteActivity.this, R.color.checkbox_green));
            rdb_morning.setButtonTintList(ContextCompat.getColorStateList(VoiceTestCompleteActivity.this, R.color.checkbox_gray));
            rdb_afternoon.setButtonTintList(ContextCompat.getColorStateList(VoiceTestCompleteActivity.this, R.color.checkbox_gray));
            rdb_evening.setChecked(true);
            rdb_morning.setChecked(false);
            rdb_afternoon.setChecked(false);
        }

        if (checkBoxSelected.equalsIgnoreCase("")) {
            submitButton.setEnabled(false);
            submitButton.setClickable(false);
            submitButton.setBackgroundResource(R.drawable.disable_rounded_corner);
        } else {
            submitButton.setEnabled(true);
            submitButton.setClickable(true);
            submitButton.setBackgroundResource(R.drawable.red_rounded_corner);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }


    @Override
    public void onBackPressed() {

    }

    private void createCheckbox(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("time_slot")) {
            String checkboxData = jsonObject.getString("time_slot");
            if (checkboxData != null && checkboxData.length() > 0) {
                checkboxHash.put("time_slot", checkboxData);
            }
        }

        checkboxValues = new ArrayList<>();
        checkedDataHashMap = new HashMap<>();
        selectedCheckedDataHashMap = new HashMap<>();

        if (checkboxHash.containsKey("time_slot")) {

            String checkboxData = checkboxHash.get("time_slot").toString();
            if (checkboxData != null && !checkboxData.isEmpty()) {
                if (checkboxData.contains("[")) {
                    checkboxData = checkboxData.replace("[", "");
                }
                if (checkboxData.contains("]")) {
                    checkboxData = checkboxData.replace("]", "");
                }
            }
            String[] checkboxArray = checkboxData.split(",");
            for (int i = 0; i < checkboxArray.length; i++) {
                if (checkboxArray[i].contains("'")) {
                    checkboxArray[i] = checkboxArray[i].substring(1, checkboxArray[i].length() - 1);
                    if (checkboxArray[i].contains("'")) {
                        checkboxArray[i] = checkboxArray[i].substring(1, checkboxArray[i].length());
                    }
                    checkboxValues.add(checkboxArray[i]);
                    checkedDataHashMap.put(checkboxArray[i], false);
                }
            }
        }

        if (selectedCheckedDataHashMap.size() > 0) {
            submitButton.setEnabled(true);
            submitButton.setClickable(true);
            submitButton.setBackgroundResource(R.drawable.red_rounded_corner);
        } else {
            submitButton.setEnabled(false);
            submitButton.setClickable(false);
            submitButton.setBackgroundResource(R.drawable.disable_rounded_corner);
        }
        ListView listView = findViewById(R.id.llChb);
        ProcessSelectionAdapter adapter = new ProcessSelectionAdapter(this, R.layout.checkbox_list, checkboxValues);
        adapter.delegate = VoiceTestCompleteActivity.this;
        listView.setAdapter(adapter);
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
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSubmit) {
            String slotdetail = checkBoxSelected;
            if (slotdetail != null && !slotdetail.isEmpty()) {
                postSlotDetails(slotdetail);
            }

            if (SmarterSMBApplication.isServiceAgreementDone) {
                ApplicationSettings.putPref(AppConstants.ONBOARDING_PROCESS_COMPLETED, true);
            } else {
                ApplicationSettings.putPref(AppConstants.VOICE_TEST_COMPLETED, true);
            }

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

            if (SmarterSMBApplication.callingFromProcessSelection) {
                String processSelected = ApplicationSettings.getPref(AppConstants.INTERVIEW_PROCESS, "");
                emailTestCompleted = ApplicationSettings.getPref(AppConstants.GRAMMAR_TEST_COMPLETED, false);
                chatTestCompleted = ApplicationSettings.getPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED, false);
                if (processSelected != null && !processSelected.isEmpty() && processSelected.contains("Email") && !emailTestCompleted) {
                    navigateToGrammarSkillTestActivity();
                } else if (processSelected != null && !processSelected.isEmpty() && processSelected.contains("Chat") && !chatTestCompleted) {
                    navigateToProcessSelectionChatActivity();
                } else {
                    navigateToHomeActivity();
                }
            } else {
                navigateToHomeActivity();
            }
        } else if (id == R.id.ll_notification) {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            settingsApi(userId);
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

    private void navigateToGrammarSkillTestActivity() {
        Intent intent = new Intent(this, GrammarSkillTestActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        this.finish();
    }

    private void navigateToProcessSelectionChatActivity() {
        Intent intent = new Intent(this, ProcessSelectionChatActivity.class);
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
                            if (SmarterSMBApplication.isServiceAgreementDone) {
                                SmarterSMBApplication.isServiceAgreementDone = false;
                            }
                        }
                    } catch (JSONException e) {

                    }
                }
            }.execute();
        } else {
            Toast.makeText(this, "You have no internet connection.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCheckBoxSelected(int position, String title, boolean isSelected) {
        if (isSelected) {
            selectedCheckedDataHashMap.put(title, isSelected);
        } else {
            selectedCheckedDataHashMap.remove(title);
        }
        if (selectedCheckedDataHashMap.size() > 0) {
            submitButton.setEnabled(true);
            submitButton.setClickable(true);
            submitButton.setBackgroundResource(R.drawable.red_rounded_corner);
        } else {
            submitButton.setEnabled(false);
            submitButton.setClickable(false);
            submitButton.setBackgroundResource(R.drawable.disable_rounded_corner);
        }
    }

    JSONObject responseObj;

    private void postSlotDetails(String slotDetail) {
        if (CommonUtils.isNetworkAvailable(this)) {
            final String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            final JSONObject userDetailObj = new JSONObject();
            try {
                userDetailObj.put("slot_details", slotDetail);
                if (SmarterSMBApplication.isServiceAgreementDone) {
                    userDetailObj.put("user_status", "Onboarding");
                } else {
                    userDetailObj.put("user_status", "Interview");
                }
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
                    responseObj = DataUploadUtils.putJsonData(url, userDetailObj);
                    return responseObj;
                }

                @Override
                protected void onPostExecute(JSONObject jObject) {
                    super.onPostExecute(jObject);
                    try {
                        if ((responseObj != null) && (responseObj.getString("success") != null)) {

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
