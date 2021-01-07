package smarter.uearn.money.fragments.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;

/**
 * Created by Srinath on 28-06-2017.
 */

public class SettingsCallsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    SmartUser smartUser = SmarterSMBApplication.SmartUser;
    TextView contactsButton, junkcontacts_button;
    RelativeLayout rlContacts, rlJunks;
    TextView emptyListText, junkTv;
    TextView mLastSync;
    ImageView mSyncNow;
    Switch autoDiler;
    private LinearLayout timerBerorelayout, timerAfterLayout;
    Switch cloud_outgoing, sipCalls;

    // Analytics variables
    private FirebaseAnalytics mFirebaseAnalytics;
    boolean administrator = false;
    Spinner timer_beforeSpinner, timer_afterSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_calls, container, false);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        //Log.d("Settingscalls","AutoDialer"+ ApplicationSettings.getPref(AppConstants.AUTO_DAILER, false)+"");

        CommonOperations.setFragmentTitle(getActivity(), "Calls");

        initialize(view);

        return view;
    }

    private void initialize(View view) {
        rlContacts = view.findViewById(R.id.rl_contacts);
        rlContacts.setOnClickListener(this);

        rlJunks = view.findViewById(R.id.rl_junkcontacts);
        rlJunks.setOnClickListener(this);

        mLastSync = view.findViewById(R.id.tv_last_sync);
        mLastSync.setText(ApplicationSettings.getPref(AppConstants.LAST_SYNC_SETTINGS, ""));

        mSyncNow = view.findViewById(R.id.ivSyncData);
        mSyncNow.setOnClickListener(this);

        contactsButton = view.findViewById(R.id.contacts_button);
        emptyListText = view.findViewById(R.id.empty_list);
        junkTv = view.findViewById(R.id.junkempty_list);
        junkcontacts_button = view.findViewById(R.id.junkcontacts_button);

        TextView tvCloudTelephony = view.findViewById(R.id.tvCloudTelephony);
        tvCloudTelephony.setText(ApplicationSettings.getPref(AppConstants.CLOUD_TELEPHONY, ""));

        loadContacts(getContext());
        loadJunkContacts(getContext());

        Switch sGpsTracking = view.findViewById(R.id.sGpsTracking);
        sGpsTracking.setOnCheckedChangeListener(this);

        Switch sCallRecording = view.findViewById(R.id.sCallRecording);
        sCallRecording.setOnCheckedChangeListener(this);

        Switch sMyCalls = view.findViewById(R.id.s_my_calls);
        sMyCalls.setOnCheckedChangeListener(this);

        sipCalls  = view.findViewById(R.id.sipCallsSwitch);
        sipCalls.setOnCheckedChangeListener(this);

        Switch voiceAnalytics = view.findViewById(R.id.voice_analytics_switch);
        if (smartUser.getGpsLocationSettings()) {
            sGpsTracking.setChecked(true);
        }

        Switch junkCallSettings = view.findViewById(R.id.junkTracking);
        junkCallSettings.setOnCheckedChangeListener(this);

        Switch personalCallSetting = view.findViewById(R.id.personalTracking);
        personalCallSetting.setOnCheckedChangeListener(this);

        Switch smsSetting = view.findViewById(R.id.smsTracking);
        smsSetting.setOnCheckedChangeListener(this);

        cloud_outgoing = view.findViewById(R.id.cloudOutgoingSwitch);
        cloud_outgoing.setOnCheckedChangeListener(this);

        autoDiler = view.findViewById(R.id.autodilerSwitch);
        autoDiler.setOnCheckedChangeListener(this);

        Switch manualDialler = view.findViewById(R.id.manual_dialler);
        manualDialler.setOnCheckedChangeListener(this);

        timer_beforeSpinner = view.findViewById(R.id.timer_before_callspinner);
        timer_afterSpinner = view.findViewById(R.id.timer_after_callspinner);
        if(getActivity() != null) {
            addSpinnerList(timer_beforeSpinner, timer_afterSpinner, getActivity());
        }

        if (SmartUser.getPersonalCallSettings()) {
            personalCallSetting.setChecked(true);
        }
        if (SmartUser.getJunkCallSetting()) {
            junkCallSettings.setChecked(true);
        }
        if (smartUser.getSmsTrackingAll()) {
            smsSetting.setChecked(true);
        }
        //Log.d("SettingCalls", "AutoDailer"+smartUser.getAutoDailer());
        if (smartUser.getAutoDailer()) {
            //Log.d("SettingCalls", "AutoDailer"+smartUser.getAutoDailer());
            autoDiler.setChecked(true);
        }

        if (smartUser.getCloudOutgoing()) {
            cloud_outgoing.setChecked(true);
        } else {
            timer_afterSpinner.setEnabled(false);
            timer_beforeSpinner.setEnabled(false);
        }

        if(smartUser.getSipCalls()) {
            sipCalls.setChecked(true);
        }

        if(smartUser.getUnscheduledCall()) {
            manualDialler.setChecked(true);
        }

        cloud_outgoing.setEnabled(false);
        sipCalls.setEnabled(false);
        autoDiler.setEnabled(false);

        checkAutoDialerEnable();

        //Check if user can edit the settings
        /*if (!ApplicationSettings.getPref(AppConstants.SUPERVISOR_EMAIL, "").equalsIgnoreCase(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""))) {*/
        /*String role = CommonUtils.getRole(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""), getContext());*/
        if(!ApplicationSettings.getPref(AppConstants.TEAM_ROLE,"").equalsIgnoreCase("superadmin")) {

            TextView tvLocked = view.findViewById(R.id.tvLocked);
            String administrator = tvLocked.getText().toString() + " " + ApplicationSettings.getPref(AppConstants.SUPERVISOR_EMAIL, "");
            tvLocked.setText(administrator);
            tvLocked.setVisibility(View.VISIBLE);

            sGpsTracking.setEnabled(false);

            if (smartUser.getGpsLocationSettings()) {
                sGpsTracking.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }

            sCallRecording.setEnabled(false);
            manualDialler.setEnabled(false);
            if(smartUser.getUnscheduledCall()) {
                manualDialler.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }

            if (smartUser.getCallRecordStatus()) {
                sCallRecording.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }

            voiceAnalytics.setEnabled(false);

            if (smartUser.getCallRecordStatus()) {
                voiceAnalytics.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }

            smsSetting.setEnabled(false);
            if (smartUser.getSmsTrackingAll()) {
                smsSetting.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }
            personalCallSetting.setEnabled(false);
            if (SmartUser.getPersonalCallSettings()) {
                personalCallSetting.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }

            junkCallSettings.setEnabled(false);
            if (SmartUser.getJunkCallSetting()) {
                junkCallSettings.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }

            cloud_outgoing.setEnabled(false);
            if (smartUser.getCloudOutgoing()) {
                cloud_outgoing.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }

            sipCalls.setEnabled(false);
            if (smartUser.getSipCalls()) {
                sipCalls.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }

            autoDiler.setEnabled(false);
            if (smartUser.getAutoDailer()) {
                autoDiler.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
            }
            timer_afterSpinner.setEnabled(false);
            timer_beforeSpinner.setEnabled(false);
        } else {
            LinearLayout administratorBlock = view.findViewById(R.id.llAdministrator);
            administratorBlock.setVisibility(View.VISIBLE);

            if (ApplicationSettings.getPref(AppConstants.ADMIN_PERSONAL_RECORDING, false)) {
                sMyCalls.setChecked(true);
            }

            smartUser.setCallRecordStatus(ApplicationSettings.getPref(AppConstants.ADMIN_PERSONAL_RECORDING, false));
            administrator = true;
        }

        if (administrator) {
            if (ApplicationSettings.getPref(AppConstants.ADMIN_GROUP_RECORDING, false)) {
                sCallRecording.setChecked(true);
            }
        } else {
            if (smartUser.getCallRecordStatus()) {
                sCallRecording.setChecked(true);
            }
        }

        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivSyncData:
                ServiceUserProfile.getUserSettings();
                getFragmentManager().popBackStack();
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.voice_analytics_switch:
                if (isChecked) {
                    smartUser.setVoiceAnalytics(true);
                    ApplicationSettings.putPref(AppConstants.VOICE_ANALYTICS, true);
                } else {
                    smartUser.setVoiceAnalytics(false);
                    ApplicationSettings.putPref(AppConstants.VOICE_ANALYTICS, false);
                }
                break;
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
                    if (administrator) {
                        ApplicationSettings.putPref(AppConstants.ADMIN_GROUP_RECORDING, true);
                    } else {
                        smartUser.setCallRecordStatus(true);
                    }
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
            case R.id.cloudOutgoingSwitch:
                saveData();
                if (isChecked) {
                    smartUser.setCloudOutgoing(true);
                    //autoDiler.setEnabled(true);
                    timer_afterSpinner.setEnabled(true);
                    timer_beforeSpinner.setEnabled(true);
                    smartUser.setSipCalls(false);
                    sipCalls.setEnabled(false);
                } else {
                    autoDiler.setEnabled(false);
                    //checkAutoDialerEnable();
                    smartUser.setCloudOutgoing(false);
                    timer_afterSpinner.setEnabled(false);
                    timer_beforeSpinner.setEnabled(false);
                    //sipCalls.setEnabled(true);
                }
                break;
            case R.id.autodilerSwitch:
                saveData();
                if (isChecked) {
                    smartUser.setAutoDailer(true);
                } else {
                    smartUser.setAutoDailer(false);
                }
                break;

            case R.id.sipCallsSwitch:
                saveData();
                if (isChecked) {
                    smartUser.setSipCalls(true);
                    smartUser.setCloudOutgoing(false);
                    cloud_outgoing.setEnabled(false);
                } else {
                    smartUser.setSipCalls(false);
                    //cloud_outgoing.setEnabled(true);
                    //checkAutoDialerEnable();
                }
                break;
            case R.id.manual_dialler:
                saveData();
                if (isChecked) {
                    smartUser.setUnscheduledCall(true);
                } else {
                    smartUser.setUnscheduledCall(false);
                }
                break;
        }
    }

    private void loadContacts(Context context) {
        if (context != null) {
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
            Cursor cursor = db1.rawQuery("SELECT * FROM PrivateContacts", null);
            if (cursor != null && cursor.getCount() > 0) {
                int count = cursor.getCount();
                if (count < 1) {
                    emptyListText.setText(getString(R.string.private_contacts_empty));
                    contactsButton.setText(getString(R.string.private_contacts_zero));
                } else {
                    emptyListText.setText("You have " + count + " personal contacts that are blocked for UEARN");
                    contactsButton.setText("You have " + count + " personal contacts.");
                }
            } else {
                emptyListText.setText("You have " + "0" + " personal contacts that are blocked for UEARN");
                contactsButton.setText("You have " + "0" + " personal contacts.");
            }
        }
    }

    private void loadJunkContacts(Context context) {
        if (context != null) {
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
            Cursor cursor = db1.rawQuery("SELECT * FROM JunkNumbers", null);
            if (cursor != null && cursor.getCount() > 0) {
                int count = cursor.getCount();
                if (count < 1) {
                    junkTv.setText(getString(R.string.private_contacts_empty));
                    junkcontacts_button.setText(getString(R.string.private_contacts_zero));
                } else {
                    junkTv.setText("You have " + count + " junk numbers that are blocked for UEARN");
                    junkcontacts_button.setText("You have " + count + " junk numbers.");
                }
            } else {
                junkTv.setText("You have " + "0" + " junk numbers that are blocked for UEARN");
                junkcontacts_button.setText("You have " + "0" + " junk numbers.");
            }
        }
    }

    private void addSpinnerList(Spinner timer_before, Spinner timer_after, Activity activity) {
        ArrayAdapter<CharSequence> callbeforeAdapter = ArrayAdapter.createFromResource(activity, R.array.timer, android.R.layout.simple_spinner_item);
        callbeforeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timer_before.setAdapter(callbeforeAdapter);
        timer_before.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> callAfterAdapter = ArrayAdapter.createFromResource(activity, R.array.timer, android.R.layout.simple_spinner_item);
        callAfterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timer_after.setAdapter(callAfterAdapter);
        timer_after.setOnItemSelectedListener(this);
        timer_after.setSelection(3);

        if(ApplicationSettings.containsPref(AppConstants.TIMER_BEFORE_CALL)) {
            String timerbefore = ApplicationSettings.getPref(AppConstants.TIMER_BEFORE_CALL, "");
                int spinnerPosition = callbeforeAdapter.getPosition(timerbefore+" secs");
                timer_before.setSelection(spinnerPosition);
        }

        if(ApplicationSettings.containsPref(AppConstants.TIMER_AFTER_CALL)) {
            String timerAfter = ApplicationSettings.getPref(AppConstants.TIMER_AFTER_CALL, "");
                int spinnerPosition = callAfterAdapter.getPosition(timerAfter+" secs");
                timer_after.setSelection(spinnerPosition);
        }
    }

    private void saveData() {
        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        int id = adapterView.getId();
        boolean timer_before = false, timer_after = false;
        if(id == R.id.timer_after_callspinner) {
            timer_after = true;
        } else if(id == R.id.timer_before_callspinner) {
            timer_before = true;
        }
        saveData();
        switch (position) {
            case 0:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "1");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "1");
                }
                break;
            case 1:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "2");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "2");
                }
                break;
            case 2:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "3");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "3");
                }
                break;
            case 3:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "4");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "4");
                }
                break;
            case 4:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "5");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "5");
                }
                break;
            case 5:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "6");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "6");
                }
                break;
            case 6:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "7");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "7");
                }
                break;
            case 7:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "8");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "8");
                }
                break;
            case 8:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "9");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "9");
                }
                break;
            case 9:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "10");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "10");
                }
                break;
            case 10:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "11");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "11");
                }
                break;
            case 11:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "12");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "12");
                }
                break;
            case 12:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "13");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "13");
                }
                break;
            case 13:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "14");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "14");
                }
                break;
            case 14:
                if(timer_before) {
                    ApplicationSettings.putPref(AppConstants.TIMER_BEFORE_CALL, "15");
                } else if(timer_after) {
                    ApplicationSettings.putPref(AppConstants.TIMER_AFTER_CALL, "15");
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void checkAutoDialerEnable() {
        if(smartUser.getSipCalls() || smartUser.getCloudOutgoing()) {
            //autoDiler.setEnabled(true);
        } else {
            autoDiler.setEnabled(false);
        }
    }
}
