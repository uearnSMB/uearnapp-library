package smarter.uearn.money.activities.interview_process;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.DeviceCheckActivity;
import smarter.uearn.money.activities.GrammarSkillTestActivity;
import smarter.uearn.money.activities.ProcessSelectionChatActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;

public class InterViewProcessActivity extends AppCompatActivity implements View.OnClickListener {


    ImageView ivNavMenu, imgDeviceCheck, imgVoiceTest, imgInterviewPannel, imgEmailTest, imgChatTest;
    private Button nextButton;
    TextView tvDeviceCheck, tvVoiceTest, tvEmailTest, tvChatTest, tvInterviewPannel;

    List<String> checkboxValues = new ArrayList<>();
    public static String currentSelectedCheckBoxValue = "";
    public static int noOfCheckboxChecked = 0;
    public static int noneOfTheAboveChecked = 0;
    private CheckBox voiceCheckbox, emailCheckbox, chatCheckbox;

    boolean deviceCheckCompleted = false;
    boolean voiceTestCompleted = false;
    boolean grammarTestCompleted = false;
    boolean callFromInterviewPanel = false;
    boolean processSelectionChatCompleted = false;

    private void initView() {

        ivNavMenu = findViewById(R.id.ivNavMenu);
        ivNavMenu.setVisibility(View.GONE);

        nextButton = findViewById(R.id.btnTakeTest);
        nextButton.setOnClickListener(this);

        imgDeviceCheck = findViewById(R.id.imgDeviceCheck);
        imgVoiceTest = findViewById(R.id.imgVoiceTest);
        imgInterviewPannel = findViewById(R.id.imgInterviewPannel);

        imgEmailTest = findViewById(R.id.imgEmailTest);
        imgChatTest = findViewById(R.id.imgChatTest);

        tvDeviceCheck = findViewById(R.id.tvDeviceCheck);
        tvVoiceTest = findViewById(R.id.tvVoiceTest);
        tvEmailTest = findViewById(R.id.tvEmailTest);
        tvChatTest = findViewById(R.id.tvChatTest);
        tvInterviewPannel = findViewById(R.id.tvInterviewPannel);

        // initially next button will be disabled once any process selection is selected make it enable.
        nextButton.setEnabled(false);
        nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_view_process);
        initView();

        voiceCheckbox = findViewById(R.id.voiceCheckbox);
        chatCheckbox = findViewById(R.id.chatCheckbox);
        emailCheckbox = findViewById(R.id.emailCheckbox);

        //============Voice test status ==================

        if (ApplicationSettings.containsPref(AppConstants.VOICE_TEST_COMPLETED)) {
            voiceTestCompleted = ApplicationSettings.getPref(AppConstants.VOICE_TEST_COMPLETED, false);
            if (voiceTestCompleted) {
                voiceCheckbox.setChecked(true);
                voiceCheckbox.setEnabled(false);
                voiceCheckbox.setClickable(false);
                imgVoiceTest.setImageResource(R.drawable.ic_status_success);
                tvVoiceTest.setText("Voice test successfully completed");
                tvVoiceTest.setTextColor(ContextCompat.getColor(this, R.color.secondary2_text_color));
                //ic_status_success
            } else {
                imgVoiceTest.setImageResource(R.drawable.ic_status_pending);
                tvVoiceTest.setText("Voice test is pending");
                tvVoiceTest.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
            }
        } else {
            imgVoiceTest.setImageResource(R.drawable.ic_status_pending);
            tvVoiceTest.setText("Voice test is pending");
            tvVoiceTest.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
        }

        //============Device test status ==================

        if (ApplicationSettings.containsPref(AppConstants.DEVICE_CHECK_COMPLETED)) {
            deviceCheckCompleted = ApplicationSettings.getPref(AppConstants.DEVICE_CHECK_COMPLETED, false);
            if (deviceCheckCompleted) {
                imgDeviceCheck.setImageResource(R.drawable.ic_status_success);
                tvDeviceCheck.setText("Device check successfully completed");
                tvDeviceCheck.setTextColor(ContextCompat.getColor(this, R.color.secondary2_text_color));
            } else {
                imgDeviceCheck.setImageResource(R.drawable.ic_status_pending);
                tvDeviceCheck.setText("Device check is pending");
                tvDeviceCheck.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
            }

        } else {
            imgDeviceCheck.setImageResource(R.drawable.ic_status_pending);
            tvDeviceCheck.setText("Device check is pending");
            tvDeviceCheck.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
        }


        //============ Call From Interview_panel status ==================

        if (ApplicationSettings.containsPref(AppConstants.CALL_FROM_INTERVIEW_PANEL_COMPLETED)) {
            callFromInterviewPanel = ApplicationSettings.getPref(AppConstants.CALL_FROM_INTERVIEW_PANEL_COMPLETED, false);
            if (callFromInterviewPanel) {
                imgInterviewPannel.setImageResource(R.drawable.ic_status_success);
                tvInterviewPannel.setText("Call from Interview panel is successfully completed.");
                tvInterviewPannel.setTextColor(ContextCompat.getColor(this, R.color.secondary2_text_color));
            } else {
                imgInterviewPannel.setImageResource(R.drawable.ic_status_pending);
                tvInterviewPannel.setText("Pending call from Interview panel");
                tvInterviewPannel.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
            }

        } else {
            imgInterviewPannel.setImageResource(R.drawable.ic_status_pending);
            tvInterviewPannel.setText("Pending call from Interview panel");
            tvInterviewPannel.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
        }

        //============Email test status ==================

        if (ApplicationSettings.containsPref(AppConstants.GRAMMAR_TEST_COMPLETED)) {
            grammarTestCompleted = ApplicationSettings.getPref(AppConstants.GRAMMAR_TEST_COMPLETED, false);
            if (grammarTestCompleted) {
                emailCheckbox.setChecked(true);
                emailCheckbox.setEnabled(false);
                emailCheckbox.setClickable(false);
                imgEmailTest.setImageResource(R.drawable.ic_status_success);
                tvEmailTest.setText("Email test successfully completed");
                tvEmailTest.setTextColor(ContextCompat.getColor(this, R.color.secondary2_text_color));
                //ic_status_success secondary2_text_color
            } else {
                imgEmailTest.setImageResource(R.drawable.ic_status_pending);
                tvEmailTest.setText("Email test is pending");
                tvEmailTest.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
            }

        } else {
            imgEmailTest.setImageResource(R.drawable.ic_status_pending);
            tvEmailTest.setText("Email test is pending");
            tvEmailTest.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
        }

        //============Chat  test status ==================

        if (ApplicationSettings.containsPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED)) {
            processSelectionChatCompleted = ApplicationSettings.getPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED, false);
            if (processSelectionChatCompleted) {
                chatCheckbox.setChecked(true);
                chatCheckbox.setEnabled(false);
                chatCheckbox.setClickable(false);
                imgChatTest.setImageResource(R.drawable.ic_status_success);
                tvChatTest.setText("Chat test successfully completed");
                tvChatTest.setTextColor(ContextCompat.getColor(this, R.color.secondary2_text_color));
            } else {
                imgChatTest.setImageResource(R.drawable.ic_status_pending);
                tvChatTest.setText("Chat test is pending");
                tvChatTest.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
            }
        } else {
            imgChatTest.setImageResource(R.drawable.ic_status_pending);
            tvChatTest.setText("Chat test is pending");
            tvChatTest.setTextColor(ContextCompat.getColor(this, R.color.error_colorCode));
        }

        if (voiceTestCompleted && grammarTestCompleted && processSelectionChatCompleted) {
            //    nextButton.setVisibility(View.GONE);
            nextButton.setText("DONE");
        } else {
            //  nextButton.setVisibility(View.VISIBLE);
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
                                                             if ((emailCheckbox.isChecked() && emailCheckbox.isEnabled())  || (chatCheckbox.isChecked() && chatCheckbox.isEnabled())) {
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
                                                             if ((voiceCheckbox.isChecked() && voiceCheckbox.isEnabled()) || (chatCheckbox.isChecked() && chatCheckbox.isEnabled())) {
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
                                                            if ((emailCheckbox.isChecked() && emailCheckbox.isEnabled()) || (voiceCheckbox.isChecked() && voiceCheckbox.isEnabled())) {
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


    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnTakeTest) {
            if (voiceTestCompleted && grammarTestCompleted && processSelectionChatCompleted) {
                onBackPressed();
            } else {
                /* For Email and Voice test Device test is mandatory
                 * 1) for Voice Test, High-speed internet,Headset connection.
                 * 2) For Email good internet connection only.
                 * */

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

                postProcessSelection(selectedProcess);
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
                /*
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
                            }else{
                                SmarterSMBApplication.currentSelectedProcess = "Email";
                                navigateToDeviceCheckActivity();
                            }
                        } else {
                            SmarterSMBApplication.currentSelectedProcess = "Email";
                            navigateToDeviceCheckActivity();
                        }
                    } else if (chatCheckbox.isEnabled() && chatCheckbox.isChecked()) {
                        navigateToProcessSelectionChatActivity();
                    }*/
            }
        }
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

    //==================================================
    JSONObject responseObj;

    private void postProcessSelection(String processSelection) {
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
                    responseObj = DataUploadUtils.putJsonData(url, userDetailObj);
                    return responseObj;
                }

                @Override
                protected void onPostExecute(JSONObject jObject) {
                    super.onPostExecute(jObject);
                    try {
                        if ((responseObj != null) && (responseObj.getString("success") != null)) {
                            Log.e("ProcessSelection", "response :: " + responseObj.getString("success"));
                            //Toast.makeText(ProcessSelectionActivity.this, "Wifi speed sent", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {

                    }
                }
            }.execute();
        } else {
            Toast.makeText(this, "You have no internet connection.", Toast.LENGTH_SHORT).show();
        }
    }
    //==================================================
}