package smarter.uearn.money.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import smarter.uearn.money.fragments.settings.SettingsOptionFragment;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.services.AcceptOrRejectNotificationService;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ResponseInterface;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.webservice.Constants;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;
import smarter.uearn.money.utils.webservice.WebService;

import java.util.ArrayList;

public class ProfileSettingsActivity extends BaseActivity {

    public SettingsOptionFragment settingsOptionFragment;
    private boolean loaded = false;
    private ProgressBar circleView;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_profile_settings);
        circleView = (ProgressBar) findViewById(R.id.circleView);
        changeStatusBarColor(this);
        SmarterSMBApplication.currentActivity = this;
        restoreActionBar("Settings");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6ca7d9")));
        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, false);

        if (!loaded) {
            loaded = true;
            ServiceUserProfile.getUserSettings();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        settingsOptionFragment = new SettingsOptionFragment();
        fragmentTransaction.add(R.id.settings_container, settingsOptionFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(ApplicationSettings.getPref(AppConstants.TEAM_ROLE,"").equalsIgnoreCase("superadmin")) {
            if (ApplicationSettings.getPref(AppConstants.SAVE_SETTINGS, false)) {
                ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, false);
                ServiceUserProfile.saveUserSettings(this);
                ServiceUserProfile.saveUserWorkhoursSettings(this);
                ServiceUserProfile.saveUserAutoDailerSettings(this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.items, menu);
        menu.findItem(R.id.action_one).setIcon(getResources().getDrawable(R.drawable.ic_cached));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if(item.getItemId() == R.id.action_one ){
            Toast.makeText(this, "Syncing...", Toast.LENGTH_SHORT).show();
            if(menu != null) {
               menu.findItem(R.id.action_done).setVisible(true);
               menu.findItem(R.id.action_one).setVisible(false);
            }
            circleView.setVisibility(View.VISIBLE);
            ServiceUserProfile.getUsersTeam();
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            String[] data = {Constants.GET, Constants.DOMAIN_URL + Constants.SETTINGS_STAGES + userId};
            CommonUtils.checkToken();
            WebService webCall = new WebService(7, new ResponseInterface() {
                @Override
                public boolean getResponseStatus(boolean result) {
                    circleView.setVisibility(View.GONE);
                    return true;
                }
            });
            webCall.execute(data);
            if(userId != null) {
                getTeamMembersCall(userId);
            }
        }
        return true;
    }

    private void getTeamMembersCall(String id) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.Get_All_Team_Members(id, 535, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                @Override
                public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                    if (data != null) {
                        CommonUtils.saveTeamdataToLocalDB(ProfileSettingsActivity.this, data);
                        for (int i = 0; i < data.size(); i++) {
                            String request = data.get(i).request;
                            if ((request != null) && request.equals("invited")) {
                                Intent intent = new Intent(ProfileSettingsActivity.this, AcceptOrRejectNotificationService.class);
                                Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(data.get(i));
                                intent.putExtra("groupsUserInfoBundle", bundle);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(intent);
                                } else {
                                    startService(intent);
                                }
                            }
                        }
                    }
                }
            }).call();
        }
    }
}
