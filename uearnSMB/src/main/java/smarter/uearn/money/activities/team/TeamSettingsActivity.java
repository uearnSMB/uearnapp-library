package smarter.uearn.money.activities.team;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.models.GoogleAccountsList;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.utils.ServerAPIConnectors.APIAdapter;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.adapters.EngineersListViewAdapter;
import smarter.uearn.money.utils.webservice.JsonParseAndOperate;
import smarter.uearn.money.views.events.OnEngineerClickedListener;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Validation;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamSettingsActivity extends BaseActivity implements View.OnClickListener, OnEngineerClickedListener, View.OnLongClickListener {

    private static final long REMOVE_USER = 0;
    private EditText engineer;
    private TextView supervisor;
    private Button acceptSupervisor, inviteSupervisor, pendingSupervisor, acceptedSupervisor, addEngineer, pendingDelete;
    private EngineersListViewAdapter engineersListViewAdapter;
    private ArrayList<GroupsUserInfo> engineersList = new ArrayList<>();
    private String user_id = SmarterSMBApplication.SmartUser.getId();
    private GroupsUserInfo supervisorData;
    private TextView loadingTextView, supervisorRole, supervisorName;
    boolean check;
    LinearLayout supervisorLayout, engineersLayout;
    Button addTeamMember;
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle AnalyticsBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_settings_team);
        changeStatusBarColor(this);

        if (ApplicationSettings.getPref(AppConstants.NEW_USER, false)) {
            showHelp();
        }

        supervisor = findViewById(R.id.supervisorEmailId);
        supervisorRole = findViewById(R.id.tv_supervisor_role);
        supervisorName = findViewById(R.id.supervisor_name);
        supervisorLayout = findViewById(R.id.supervisor_layout);
        engineersLayout = findViewById(R.id.engineer_layout);
        addTeamMember = findViewById(R.id.btn_add_team_member);
        addTeamMember.setOnClickListener(this);

        engineer = findViewById(R.id.engineer_email);
        if (getIntent().hasExtra("ContactFragment")) {
            check = getIntent().getBooleanExtra("ContactFragment", true);
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        inviteSupervisor = findViewById(R.id.invite_supervisor_button);
        acceptSupervisor = findViewById(R.id.accept_supervisor_button);
        pendingSupervisor = findViewById(R.id.pending_supervisor_button);
        acceptedSupervisor = findViewById(R.id.accepted_supervisor_button);
        pendingDelete = findViewById(R.id.pendingDelete);

        loadingTextView = findViewById(R.id.loadingText);

        addEngineer = findViewById(R.id.add_engineer_button);
        addEngineer.setOnClickListener(this);

        engineersListViewAdapter = new EngineersListViewAdapter(this, engineersList, this);
        ListView engineers_list = findViewById(R.id.lv_nonscroll_list);
        engineers_list.setAdapter(engineersListViewAdapter);

        inviteSupervisor.setOnClickListener(this);
        acceptSupervisor.setOnClickListener(this);
        setListViewHeightBasedOnChildren(engineers_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGroupsOfUsers();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    private void showHelp() {
        ApplicationSettings.putPref(AppConstants.NEW_USER, false);
        CustomTwoButtonDialog.newTeam(this).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        restoreActionBar("Team Settings");
        MenuItem actionOne = menu.findItem(R.id.action_one);
        actionOne.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        GoogleAccountsList googleAccountsList = SmarterSMBApplication.googleAccountsList;
        String userMail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");

        if (id == R.id.add_engineer_button) {
            logEvent("add engineer");
            if (CommonUtils.isNetworkAvailable(this)) {
                if (Validation.isEmailAddress(engineer, true, "Invalid email Address")) {
                    String emailId = engineer.getText().toString();
                    if (emailId.equalsIgnoreCase(userMail)) {
                        Toast.makeText(this, "Do not add your own email ID", Toast.LENGTH_SHORT).show();
                    } else {
                        GroupsUserInfo engineerDetails = new GroupsUserInfo(emailId, "engineer", "", "Invite", "Invitation Sent");
                        engineersList.add(engineerDetails);
                        engineersListViewAdapter.notifyDataSetChanged(engineersList);
                        doSave("engineer", engineer.getText().toString());
                    }
                } else {
                    Toast.makeText(this, "Please Enter Valid Email Id", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.invite_supervisor_button) {

        } else if (id == R.id.accept_supervisor_button) {
            logEvent("accept supervisor");
            if (CommonUtils.isNetworkAvailable(this)) {
                if (supervisorData != null) {
                    supervisorData.request = "accepted";
                    ArrayList<GroupsUserInfo> groupUsersArrayList = new ArrayList<>();
                    groupUsersArrayList.add(supervisorData);
                    setGroupsOfUser(groupUsersArrayList, "Accepting Invitation");
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btn_add_team_member) {
            Intent intent = new Intent(this, AddTeamMemberActivity.class);
            startActivity(intent);
        }
    }

    private void getGroupsOfUsers() {
        loadingTextView.setVisibility(View.VISIBLE);
        new APIProvider.Get_GroupsUser(user_id, 1, null, null, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
            @Override
            public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                loadingTextView.setVisibility(View.GONE);
                if (data != null) {
                    setDataToEngineerListOrSupervisor(data);
                }
            }
        }).call();
    }

    private void setDataToEngineerListOrSupervisor(ArrayList<GroupsUserInfo> data) {
        engineersList.clear();
        boolean hasSupervisor = false;

        for (int i = 0; i < data.size(); i++) {
            GroupsUserInfo groupsUserInfo = data.get(i);
            if (!groupsUserInfo.status.equals("deleted")) {
                if (groupsUserInfo.dependent_type.equals("engineer")) {
                    engineersList.add(groupsUserInfo);
                } else {
                    supervisorData = groupsUserInfo;
                    supervisor.setText(supervisorData.email);
                    if(supervisorData.name != null) {
                        supervisorName.setText(""+ supervisorData.name);
                    }
                    if (supervisorData != null && supervisorData.email != null) {
                        ApplicationSettings.putPref(AppConstants.SUPERVISOR, supervisorData.email);
                    }
                    hasSupervisor = true;

                    for (HashMap<String, String> member : JsonParseAndOperate.teamData) {
                        if (supervisorData.email.equalsIgnoreCase(member.get("email"))) {
                            if (member.containsKey("role")) {
                                supervisorRole.setText("" + member.get("role"));
                            }
                        }
                    }
                    setSupervisorButton(supervisorData.request, supervisorData.status);
                }
            }
        }

        if (hasSupervisor) {
            supervisorLayout.setVisibility(View.VISIBLE);
        }

        if (engineersList.size() == 0) {
            engineersLayout.setVisibility(View.GONE);
        } else {
            engineersLayout.setVisibility(View.VISIBLE);
        }
        engineersListViewAdapter.notifyDataSetChanged(engineersList);
    }

    private void setSupervisorButton(String request, String status) {
        if (status.equals("pending delete")) {
            acceptSupervisor.setVisibility(View.GONE);
            inviteSupervisor.setVisibility(View.GONE);
            acceptedSupervisor.setVisibility(View.GONE);
            pendingSupervisor.setVisibility(View.GONE);
            pendingDelete.setVisibility(View.VISIBLE);
            supervisor.setFocusable(false);
            supervisor.setLongClickable(true);
        } else if (request.equals("invite")) {
            inviteSupervisor.setVisibility(View.GONE);
            acceptedSupervisor.setVisibility(View.GONE);
            acceptSupervisor.setVisibility(View.GONE);
            pendingDelete.setVisibility(View.GONE);
            pendingSupervisor.setVisibility(View.VISIBLE);
            supervisor.setFocusable(true);
            supervisor.setLongClickable(true);
        } else if (request.equals("invited")) {
            inviteSupervisor.setVisibility(View.GONE);
            pendingSupervisor.setVisibility(View.GONE);
            acceptSupervisor.setVisibility(View.GONE);
            pendingDelete.setVisibility(View.GONE);
            acceptSupervisor.setVisibility(View.VISIBLE);
            supervisor.setFocusable(false);
            supervisor.setLongClickable(true);
        } else if (request.equals("accepted")) {
            inviteSupervisor.setVisibility(View.GONE);
            acceptSupervisor.setVisibility(View.GONE);
            pendingDelete.setVisibility(View.GONE);
            pendingSupervisor.setVisibility(View.GONE);
            acceptedSupervisor.setVisibility(View.VISIBLE);
            supervisor.setFocusable(false);
            supervisor.setLongClickable(true);
        }
    }

    private void doSave(String dependent_type, String email) {
        ArrayList<GroupsUserInfo> groupsUserInfoArrayList = new ArrayList<>();
        GroupsUserInfo groupUser = new GroupsUserInfo(email, dependent_type, "", "invite", "invitation sent");
        groupsUserInfoArrayList.add(groupUser);
        setGroupsOfUser(groupsUserInfoArrayList, "Sending Invitation");
    }

    private void setGroupsOfUser(ArrayList<GroupsUserInfo> groupsArrayList, String message) {
        new APIProvider.Set_GroupsOfUser(groupsArrayList, 1, this, message, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                engineer.setText("");
                if (data != null) {
                    Toast.makeText(TeamSettingsActivity.this, "Updated Changes", Toast.LENGTH_SHORT).show();
                } else if (failure_code != APIAdapter.CONNECTION_FAILED) {
                    Toast.makeText(TeamSettingsActivity.this, "failed to assign new team member. \n Please check you mail id.", Toast.LENGTH_SHORT).show();
                }
                getGroupsOfUsers();
            }
        }).call();
    }

    @Override
    public void onLongClick(GroupsUserInfo groupsUserInfo) {
        if (CommonUtils.isNetworkAvailable(this)) {
            displayDialogToChangeUser(groupsUserInfo, "Do you want to delete your Team member?");
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onButtonClicked(GroupsUserInfo groupsUserInfo) {
        if (CommonUtils.isNetworkAvailable(this)) {
            groupsUserInfo.request = "accepted";
            ArrayList<GroupsUserInfo> groupUsersArraylist = new ArrayList<>();
            groupUsersArraylist.add(groupsUserInfo);
            setGroupsOfUser(groupUsersArraylist, "Accepting Invitation...");
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteAcceptClicked(GroupsUserInfo groupsUserInfo) {
        if (CommonUtils.isNetworkAvailable(this)) {
            groupsUserInfo.status = "deleted";
            displayDialogToChangeUser(groupsUserInfo, "Do you want to remove your team member?");
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayDialogToChangeUser(final GroupsUserInfo groupsUserInfo, String textContent) {
        final Dialog deleteMember = CustomTwoButtonDialog.deleteTeamMember(this);
        TextView btnNo = deleteMember.findViewById(R.id.btn_yes);
        btnNo.setText("Yes");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new APIProvider.Remove_EngineerOrSupervisor(groupsUserInfo, REMOVE_USER, TeamSettingsActivity.this, "Removing User..", new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        Toast.makeText(TeamSettingsActivity.this, "User Removed", Toast.LENGTH_SHORT).show();
                        getGroupsOfUsers();
                    }
                }).call();
                deleteMember.dismiss();
            }
        });
        deleteMember.show();
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        if (id == R.id.supervisorEmailId) {
            if (supervisor.getText() != null) {
                if (!supervisor.getText().toString().equals("")) {
                    displayDialogToChangeUser(supervisorData, "Do you want to remove your Supervisor?");
                }
            }
        }
        return true;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void logEvent(String name) {
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_7);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, AnalyticsBundle);
    }
}
