package smarter.uearn.money.fragments.settings;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import smarter.uearn.money.activities.team.TeamSettingsActivity;
import smarter.uearn.money.dialogs.CustomChangePasswordDialog;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;

/**
 * Created on 21/02/2017
 *
 * @author Dilip
 * @version 1.0
 */

public class SettingsOptionFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private SmartUser smartUser = SmarterSMBApplication.SmartUser;
    private boolean administrator = false;
    int scriptDailog = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        initializeUiElements(view);

        return view;
    }

    private void initializeUiElements(View v) {
        RelativeLayout relativecalls = v.findViewById(R.id.rl_calls);
        relativecalls.setOnClickListener(this);

        RelativeLayout relativeLeads = v.findViewById(R.id.rl_leads);
        relativeLeads.setOnClickListener(this);

        RelativeLayout relativeFollowUps = v.findViewById(R.id.rl_follow_ups);
        relativeFollowUps.setOnClickListener(this);

        RelativeLayout relativeSales = v.findViewById(R.id.rl_sales);
        relativeSales.setOnClickListener(this);

        RelativeLayout relativeTeam = v.findViewById(R.id.rl_team);
        relativeTeam.setOnClickListener(this);

        RelativeLayout relativeTemplates = v.findViewById(R.id.rl_message_templates);
        relativeTemplates.setOnClickListener(this);

        RelativeLayout relativeDigitalBrochure = v.findViewById(R.id.rl_digital_brochure);
        relativeDigitalBrochure.setOnClickListener(this);

        RelativeLayout relativeChangePassword = v.findViewById(R.id.rl_change_password);
        relativeChangePassword.setOnClickListener(this);

        RelativeLayout relativeDashboard = v.findViewById(R.id.rl_dashboard);
        relativeDashboard.setOnClickListener(this);

        TextView mLastSync = v.findViewById(R.id.tv_last_sync);
        mLastSync.setText(ApplicationSettings.getPref(AppConstants.LAST_SYNC_SETTINGS, ""));

        ImageView mSyncNow = v.findViewById(R.id.ivSyncData);
        mSyncNow.setOnClickListener(this);

        Spinner rnr_callback_spinner = v.findViewById(R.id.rnr_callback_spinner);
        Spinner rnr_reminder_spinner = v.findViewById(R.id.rnr_reminders_spinner);
        /*if (getActivity() != null) {
            addSpinnerList(rnr_callback_spinner, rnr_reminder_spinner, getActivity());
        }*/
        /*String role = CommonUtils.getRole(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""), getContext());*/

        /*if (!ApplicationSettings.getPref(AppConstants.SUPERVISOR_EMAIL, "").equalsIgnoreCase(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""))) {*/
        if(!ApplicationSettings.getPref(AppConstants.TEAM_ROLE,"").equalsIgnoreCase("superadmin")) {

        } else {
            //Log.d("Role", ApplicationSettings.getPref(AppConstants.TEAM_ROLE,""));
            relativeDigitalBrochure.setVisibility(View.VISIBLE);
            relativeTemplates.setVisibility(View.VISIBLE);
        }
    }

    private void addSpinnerList(Spinner callback_spinner, Spinner reminder_spinner, Activity activity) {
        ArrayAdapter<CharSequence> callbackAdapter = ArrayAdapter.createFromResource(activity, R.array.rnr_callbacks, android.R.layout.simple_spinner_item);
        callbackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        callback_spinner.setAdapter(callbackAdapter);
        callback_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                position = position + 1;
                ApplicationSettings.putPref(AppConstants.RNR_CALLBACKS_NO, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
       });

        ArrayAdapter<CharSequence> rnrReminderAdapter = ArrayAdapter.createFromResource(activity, R.array.rnr_reminders, android.R.layout.simple_spinner_item);
        rnrReminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminder_spinner.setAdapter(rnrReminderAdapter);
        reminder_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 1);
                        break;
                    case 1:
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 2);
                        break;
                    case 2:
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 3);
                        break;
                    case 3:
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 4);
                        break;
                    case 4:
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 6);
                        break;
                    case 5:
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 24);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //dummy
        callback_spinner.setSelection(3);

        if (ApplicationSettings.containsPref(AppConstants.RNR_CALLBACKS_NO)) {
            int callbacks = ApplicationSettings.getPref(AppConstants.RNR_CALLBACKS_NO, 0);
            if (callbacks > 0) {
                callback_spinner.setSelection(callbacks - 1);
            }
        }

        if (ApplicationSettings.containsPref(AppConstants.RNR_REMINDER)) {
            int reminders = ApplicationSettings.getPref(AppConstants.RNR_REMINDER, 0);
            if (reminders > 0) {
                String reminderString = reminders + " hr";
                int spinnerPosition = rnrReminderAdapter.getPosition(reminderString);
                reminder_spinner.setSelection(spinnerPosition);
            }
        }
    }

    private void testSingleApi() {

    }

    @Override
    public void onClick(View view) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (view.getId()) {
//            case R.id.rl_calls:
//                logAnalytics("Leads_Settings", "RelativeLayout");
//                fragmentTransaction.replace(R.id.settings_container, new SettingsCallsFragment());
//                break;
//            case R.id.rl_leads:
//                logAnalytics("Leads_Settings", "RelativeLayout");
//                fragmentTransaction.replace(R.id.settings_container, new SettingsLeadsFragment());
//                break;
//            case R.id.rl_follow_ups:
//                logAnalytics("Follow_Ups_Settings", "RelativeLayout");
////                fragmentTransaction.replace(R.id.settings_container, new SettingsFollowUpsFragment());
//                break;
//            case R.id.rl_sales:
//                logAnalytics("Sales_Settings", "RelativeLayout");
//                fragmentTransaction.replace(R.id.settings_container, new SettingsSalesFragment());
//                break;
            case R.id.rl_team:
                logAnalytics("Team_Settings", "RelativeLayout");
                startActivity(new Intent(getActivity(), TeamSettingsActivity.class));
                break;

            case R.id.rl_change_password:
                logAnalytics("Change_Password_Settings", "RelativeLayout");
                new CustomChangePasswordDialog().buildChangePasswordDialog(getContext(), getActivity()).show();
                break;
//            case R.id.rl_digital_brochure:
//                logAnalytics("Digital_Brochure_Settings", "RelativeLayout");
//                startActivity(new Intent(getActivity(), DigitalSettingsActivity.class));
//                break;



            case R.id.ivSyncData:
                ServiceUserProfile.getUserSettings();
                if (getActivity() != null) {
                    getActivity().finish();
                }
                break;
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void logAnalytics(String eventName, String eventType) {
        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_10);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
        mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_10, AnalyticsBundle);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.s_my_calls:
                if (isChecked) {
                    smartUser.setCallRecordStatus(true);
                    ApplicationSettings.putPref(AppConstants.ADMIN_PERSONAL_RECORDING, true);
                } else {
                    smartUser.setCallRecordStatus(false);
                    ApplicationSettings.putPref(AppConstants.ADMIN_PERSONAL_RECORDING, false);
                }
                break;
            case R.id.sCallRecording:
                saveData();
                if (isChecked) {
                    if (scriptDailog == 1) {
                        if (getActivity() != null) {
                            CustomSingleButtonDialog.buildSingleButtonDialog("Voice Analytics", "Please Download offline speech recognition library. \n Setting > Language & Input > Google Voice typing > Offline Speech recognition > All > English(India)", getActivity(), true);
                        }
                    }
                    if (administrator) {
                        ApplicationSettings.putPref(AppConstants.ADMIN_GROUP_RECORDING, true);
                    } else {
                        smartUser.setCallRecordStatus(true);
                    }
                    scriptDailog = 1;
                } else {
                    if (administrator) {
                        ApplicationSettings.putPref(AppConstants.ADMIN_GROUP_RECORDING, false);
                    } else {
                        smartUser.setCallRecordStatus(false);
                    }
                }
                break;
            case R.id.sGpsTracking:
                saveData();
                if (isChecked) {
                    smartUser.setGpsLocationSettings(true);
                } else {
                    smartUser.setGpsLocationSettings(false);
                }
                break;
            case R.id.switchAutoFollowUps:
                logAnalytics("Auto_Follow_Up_Switch_Settings", "CheckBox");
                saveData();
                if (isChecked) {
                    smartUser.setAutoFollowUpSettings(true);
                } else {
                    smartUser.setAutoFollowUpSettings(false);
                }
                break;

            case R.id.junkTracking:
                saveData();
                if (isChecked) {
                    smartUser.setJunkCallSetting(true);
                } else {
                    smartUser.setJunkCallSetting(false);
                }
                break;
            case R.id.personalTracking:
                saveData();
                if (isChecked) {
                    smartUser.setPersonalcallSetting(true);
                } else {
                    smartUser.setPersonalcallSetting(false);
                }
                break;
            case R.id.smsTracking:
                saveData();
                if (isChecked) {
                    smartUser.setSmsTrackingAll(true);
                } else {
                    smartUser.setSmsTrackingAll(false);
                }
                break;
        }
    }

    private void saveData() {
        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
    }

}
