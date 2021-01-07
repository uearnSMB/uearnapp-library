package smarter.uearn.money.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import smarter.uearn.money.models.LatLong;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.upload.GPSTracker;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class SettingsPreferencesActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private TextView emptyListText;
    private Button contactsButton;
    private boolean privateContacts , cloudTelephonyContacts , teamMembersContacts , appointmentContacts ;
    private CheckBox privateContactsCheckBox, appointmentContactsCheckBox, teamMembersContactsCheckBox, cloudTelephonyCheckBox;
    private RelativeLayout privateContactsLayout, appointmentContactsLayout, teamMembersLayout, cloudTelephonyLayout;
    private Switch callRecording, afterCallPopup, captureIncomingSms, autoMessageForMissedCalls, gpsSettings;
    private ImageView popupImage, notificationImage, popupCheckImage, notificationCheckimage, selectedImage;
    boolean popupSelected, notificationSelected;
    SmartUser smartUser = SmarterSMBApplication.SmartUser;

    private LinearLayout cardViewLayout;
    private CardView cardView1, cardView2;
    private View view;

    // Analytics variables
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle AnalyticsBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_settings_preferences);
        changeStatusBarColor(this);
        SmarterSMBApplication.currentActivity = this;
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        inItValues();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(SettingsPreferencesActivity.this);
    }

    private void inItValues() {
        privateContactsLayout = findViewById(R.id.privateContactsLayout);
        appointmentContactsLayout = findViewById(R.id.appointmentContactsLayout);
        teamMembersLayout = findViewById(R.id.teamContactsLayout);
        cloudTelephonyLayout = findViewById(R.id.cloudTelephonyLayout);

        privateContactsCheckBox = findViewById(R.id.privateContactsRadioButton);
        appointmentContactsCheckBox = findViewById(R.id.appointmentContacts_checkbox);
        teamMembersContactsCheckBox = findViewById(R.id.team_Contacts_checkbox);
        cloudTelephonyCheckBox = findViewById(R.id.cloundTelephonyRadioButton);

        privateContactsCheckBox.setOnCheckedChangeListener(this);
        appointmentContactsCheckBox.setOnCheckedChangeListener(this);
        teamMembersContactsCheckBox.setOnCheckedChangeListener(this);
        cloudTelephonyCheckBox.setOnCheckedChangeListener(this);

        privateContactsCheckBox.setOnClickListener(this);
        appointmentContactsCheckBox.setOnClickListener(this);
        teamMembersContactsCheckBox.setOnClickListener(this);
        cloudTelephonyCheckBox.setOnClickListener(this);

        contactsButton = findViewById(R.id.contacts_button);
        emptyListText = findViewById(R.id.empty_list);

        callRecording = findViewById(R.id.callRecord);

        afterCallPopup = findViewById(R.id.switchAfterCall);
        captureIncomingSms = findViewById(R.id.switchTrackSMS);
        autoMessageForMissedCalls = findViewById(R.id.switchAutoResponse);
        gpsSettings = findViewById(R.id.gps_switch);
        popupImage = findViewById(R.id.aftercallPopupImage);
        notificationImage = findViewById(R.id.afterCallNotificationImage);
        popupCheckImage = findViewById(R.id.popup_checked);
        notificationCheckimage = findViewById(R.id.notification_checked);
        cardViewLayout = findViewById(R.id.cardViewLayout);
        cardView1 = findViewById(R.id.card_view);
        cardView2 = findViewById(R.id.card_view2);

        popupImage.setOnClickListener(this);
        notificationImage.setOnClickListener(this);

        popupSelected = smartUser.getShowPopupSettings();
        notificationSelected = smartUser.getShowNotificationSettings();

        callRecording.setOnCheckedChangeListener(this);

        afterCallPopup.setOnCheckedChangeListener(this);
        captureIncomingSms.setOnCheckedChangeListener(this);
        autoMessageForMissedCalls.setOnCheckedChangeListener(this);
        gpsSettings.setOnCheckedChangeListener(this);
        checkStates();
        privateContacts = smartUser.getDonotRecordPrivateContacts();
        appointmentContacts = smartUser.getRecordOnlyLeadNumbers();
        teamMembersContacts = smartUser.getRecordOnlyTeamMemberNumbers();
        cloudTelephonyContacts = smartUser.getRecordOnlyCloudTelephonyNumbers();

        if(!(appointmentContacts) && !(teamMembersContacts) && !(cloudTelephonyContacts)) {
            privateContacts = true;
        }

        contactsButton.setOnClickListener(this);
        enableOrDisableButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        restoreActionBar("Settings");
        MenuItem actionOne  = menu.findItem(R.id.action_one);
        actionOne.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if(id == R.id.appointmentContacts_checkbox) {
            logEvent("contacts","checkbox");
            if(appointmentContacts) {
                appointmentContacts = false;
            }
            else {
                appointmentContacts = true;
                privateContacts = false;
            }
        }
        else if(id == R.id.privateContactsRadioButton) {
            privateContacts = !privateContacts;
        }
        else if (id == R.id.cloundTelephonyRadioButton) {
            if (cloudTelephonyContacts) {
                cloudTelephonyContacts = false;
            }
            else {
                cloudTelephonyContacts = true;
                privateContacts = false;
            }
        } else if (id == R.id.team_Contacts_checkbox) {
            if (teamMembersContacts) {
                teamMembersContacts = false;
            }
            else {
                teamMembersContacts = true;
                privateContacts = false;
            }
        }  else if(id == R.id.aftercallPopupImage) {
            if(popupSelected) {
                popupSelected = false;
            } else {
                popupSelected = true;
                notificationSelected = false;
            }
            disableEnableCustomCallRecordButtons();
        } else if(id == R.id.afterCallNotificationImage) {
            if(notificationSelected) {
                notificationSelected = false;
            } else {
                notificationSelected = true;
                popupSelected = false;
            }
            disableEnableCustomCallRecordButtons();
        }
        enableOrDisableButtons();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        int id = compoundButton.getId();
        if(id == R.id.privateContactsRadioButton) {
            if(isChecked) {
                smartUser.setDonotRecordPrivateContacts(true);
                smartUser.setRecordOnlyLeadNumbers(false);
                smartUser.setRecordOnlyCloudTelephonyNumbers(false);
                smartUser.setRecordOnlyTeamMemberNumbers(false);
            }  else {
                smartUser.setDonotRecordPrivateContacts(false);
            }
        }
        if(id == R.id.appointmentContacts_checkbox) {
            if(isChecked) {
                smartUser.setRecordOnlyLeadNumbers(true);
                smartUser.setDonotRecordPrivateContacts(false);
            }  else {
                smartUser.setRecordOnlyLeadNumbers(false);
            }
        }
        if (id == R.id.cloundTelephonyRadioButton) {
            if (isChecked) {
                smartUser.setDonotRecordPrivateContacts(false);
                smartUser.setRecordOnlyCloudTelephonyNumbers(true);
            }  else {
                smartUser.setRecordOnlyCloudTelephonyNumbers(false);
            }
        }
        if (id == R.id.team_Contacts_checkbox) {
            if (isChecked) {
                smartUser.setDonotRecordPrivateContacts(false);
                smartUser.setRecordOnlyTeamMemberNumbers(true);
            } else {
                smartUser.setRecordOnlyTeamMemberNumbers(false);
            }
        }
        if(id == R.id.callRecord){
            if(isChecked){
                smartUser.setCallRecordStatus(true);
            } else{
                smartUser.setCallRecordStatus(false);
            }
        }
        else if (id == R.id.gps_switch) {
            if (isChecked) {
                LatLong latLong = getLatLong();
                smartUser.setGpsLocationSettings(true);
            }
            else {
                smartUser.setGpsLocationSettings(false);
            }
        }
        enableOrDisableButtons();
    }

    public LatLong getLatLong() {
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            LatLong latLong=new LatLong(Double.toString(gpsTracker.getLatitude()),Double.toString(gpsTracker.getLongitude()));
            return latLong;
        } else {
            gpsTracker.showSettingsAlert();
            return null;
        }
    }

    public void checkStates() {

        SmartUser smartUser = SmarterSMBApplication.SmartUser;
        if(smartUser != null) {
            callRecording.setChecked(smartUser.getCallRecordStatus());
            afterCallPopup.setChecked(smartUser.getAfterCallpopup());
            captureIncomingSms.setChecked(smartUser.getCaptureIncomingSMS());
            autoMessageForMissedCalls.setChecked(smartUser.getAutoSmsForMissedCalls());
            gpsSettings.setChecked(smartUser.getGpsLocationSettings());
            disableEnableCustomCallRecordButtons();
        }
    }

    private void enableOrDisableButtons() {
        if(privateContacts) {
            //TODO: if private contacts are enabled, disable all remaining layouts.
            privateContactsCheckBox.setChecked(true);
            enableDisableView(findViewById(R.id.appointmentContactsLayout), false);
            enableDisableView(findViewById(R.id.teamContactsLayout), false);
            enableDisableView(findViewById(R.id.cloudTelephonyLayout), false);
        } else {
            enableDisableView(findViewById(R.id.appointmentContactsLayout), true);
            enableDisableView(findViewById(R.id.teamContactsLayout), true);
            enableDisableView(findViewById(R.id.cloudTelephonyLayout), true);
            privateContactsCheckBox.setChecked(false);
            appointmentContactsCheckBox.setChecked(appointmentContacts);
            teamMembersContactsCheckBox.setChecked(teamMembersContacts);
            cloudTelephonyCheckBox.setChecked(cloudTelephonyContacts);
        }
    }

    private void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    public void disableEnableCustomCallRecordButtons(){

        if(popupSelected) {
            smartUser.setShowPopupSettings(true);
            popupCheckImage.setVisibility(View.VISIBLE);
            int colorcode = Color.parseColor("#2e6ffd");
            cardView1.setCardBackgroundColor(colorcode);

        }else {
            smartUser.setShowPopupSettings(false);
            popupCheckImage.setVisibility(View.GONE);
            cardView1.setCardBackgroundColor(Color.parseColor("#BDBDBD"));
        }

        if(notificationSelected) {
            smartUser.setShowNotificationSettings(true);
            notificationCheckimage.setVisibility(View.VISIBLE);
            cardView2.setCardBackgroundColor(Color.parseColor("#2e6ffd"));
        } else {
            smartUser.setShowNotificationSettings(false);
            notificationCheckimage.setVisibility(View.GONE);
            cardView2.setCardBackgroundColor(Color.parseColor("#BDBDBD"));
        }
    }

    public void logEvent(String name,String event){
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_8);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, event);
        if(mFirebaseAnalytics != null) {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, AnalyticsBundle);
        }
    }
}