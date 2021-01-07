package smarter.uearn.money.training.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.training.adapter.TrainingHelpTopicsAdapter;
import smarter.uearn.money.training.adapter.TrainingNotificationAdapter;
import smarter.uearn.money.training.model.HelpTopics;
import smarter.uearn.money.training.model.HelpTopicsData;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.training.model.TrainingMessage;
import smarter.uearn.money.training.model.TrainingMessageResponse;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;

@RequiresApi(api = Build.VERSION_CODES.M)
public class HelpListActivity extends BaseActivity {

    String helpTopics = "{\n" +
            "\t\"data\": [{\n" +
            "\t\t\"helpdesk_id\": \"001\",\n" +
            "\t\t\"helpdesk_type\": \"Call Connecting Issue\",\n" +
            "\t\t\"helpdeskmessage\": \"Call can't able place or dialling\"\n" +
            "\t}, {\n" +
            "\t\t\"helpdesk_id\": \"002\",\n" +
            "\t\t\"helpdesk_type\": \"Internet Technical Issue\",\n" +
            "\t\t\"helpdeskmessage\": \"Mobile data or WiFi related issues\"\n" +
            "\t}, {\n" +
            "\t\t\"helpdesk_id\": \"003\",\n" +
            "\t\t\"helpdesk_type\": \"Agent call log issue\",\n" +
            "\t\t\"helpdeskmessage\": \"Call can't able place or dialling\"\n" +
            "\t},{\n" +
            "\t\t\"helpdesk_id\": \"004\",\n" +
            "\t\t\"helpdesk_type\": \"Post call update issue\",\n" +
            "\t\t\"helpdeskmessage\": \"Call summarize and other issues\"\n" +
            "\t},{\n" +
            "\t\t\"helpdesk_id\": \"005\",\n" +
            "\t\t\"helpdesk_type\": \"Call slot schedule issue\",\n" +
            "\t\t\"helpdeskmessage\": \"Manage you schedule related issue\"\n" +
            "\t},{\n" +
            "\t\t\"helpdesk_id\": \"006\",\n" +
            "\t\t\"helpdesk_type\": \"Plan to take off or leave\",\n" +
            "\t\t\"helpdeskmessage\": \"Planning to take for vacation related\"\n" +
            "\t}]\n" +
            "}";
    private RecyclerView rv_help_topics;
    LinearLayout ll_notification, ll_sync;
    //HelpTopics[] helpTopicsList;
    HelpTopicsData helpTopicsData;
    ImageView profile_image;
    EditText help_search;
    HelpTopics[] arrayTopics;
    List<HelpTopics> helpTopicsList = new ArrayList<HelpTopics>();
    List<HelpTopics> arrayListTopics = new ArrayList<HelpTopics>();
    Set<HelpTopics> setListTopics = new ArraySet<HelpTopics>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_training);
        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);
        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_sync.setVisibility(View.GONE);
        ll_notification.setVisibility(View.GONE);
        rv_help_topics = (RecyclerView) findViewById(R.id.rv_help_topics);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        help_search = (EditText) findViewById(R.id.help_search);

        changeStatusBarColor(this);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        help_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(help_search.getText().toString().trim().length() >= 3){
                    /*for (int i = 0; i < arrayListTopics.size(); i++) {
                        if (arrayListTopics.get(i).getHelpdesk_type().toLowerCase().contains(help_search.getText().toString())) {
                            helpTopicsList = findIssueByID(arrayListTopics.get(i).getHelpdesk_id());
                            setupRecyclerView(helpTopicsList);
                        }
                    }*/

                    helpTopicsList = findIssueByID(help_search.getText().toString().toLowerCase());
                    setupRecyclerView(helpTopicsList);
                }
            }
        });

        help_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.e("HelpList","onTextChanged :: "+s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length() >= 3){
                    helpTopicsList = findIssueByID(help_search.getText().toString().toLowerCase());
                    setupRecyclerView(helpTopicsList);

                }else{
                    helpTopicsList.clear();
                    //arrayListTopics.clear();
                    //arrayListTopics = helpTopicsData.getData();
                    setupRecyclerView(arrayListTopics);
                }
            }
        });

//            JSONObject jsonObject;
//            String successData = null;
//            try {
//                jsonObject = new JSONObject(helpTopics);
//                successData = jsonObject.getString("data");
//                Log.e("NotificationList","successData :: "+helpTopics);
//                Gson g = new Gson();
//                helpTopicsList = g.fromJson(successData, HelpTopics[].class);
//                Log.e("NotificationList","trainingMessageResponse :: "+helpTopicsList[1].getHelpdesk_type());
//                setupRecyclerView(helpTopicsList);
//            }catch (Exception e){
//
//
//        }
        getTrainingDashboardDetails();
    }

    public ArrayList<HelpTopics> findIssueByID(String helpDeskID) {
        Log.e("HelpList","helpDeskID :: "+helpDeskID);
        ArrayList<HelpTopics> matches = new ArrayList<HelpTopics>();
        // go through list of members and compare name with given name
        for(HelpTopics helpTopics : arrayListTopics) {
            if (helpTopics.getHelpdesk_type().toLowerCase().contains(helpDeskID)) {
                Log.e("HelpList","helpTopics :: "+helpTopics.getHelpdesk_id());
                matches.add(helpTopics); // adds matching member to the return list
                Log.e("HelpList","Size for matches :: "+ matches.size());
            }
        }
        return matches; // return the matches, which is empty when no member with the given name was found
    }

    public void changeStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }
    private void setupRecyclerView(List<HelpTopics> helpTopicsList) {
        TrainingHelpTopicsAdapter trainingHelpTopicsAdapter =
                new TrainingHelpTopicsAdapter(HelpListActivity.this, helpTopicsList);
        rv_help_topics.setLayoutManager(new LinearLayoutManager(this));
        rv_help_topics.setAdapter(trainingHelpTopicsAdapter);
        rv_help_topics.setItemAnimator(new DefaultItemAnimator());
    }
    private void getTrainingDashboardDetails(){
        try {

            new APIProvider.GetHelpDeskList("", 1, HelpListActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
//                        JSONObject jsonObject;
//                        String successData = null;
//                        try {
//                            jsonObject = new JSONObject(data);
//                            //successData = jsonObject.getString("success");
//                        }catch (Exception e){
//
//                        }
                        Gson g = new Gson();
                        helpTopicsData= g.fromJson(data, HelpTopicsData.class);
                        //helpTopicsList.add(helpTopicsData.);
                        arrayListTopics = helpTopicsData.getData();
                        setupRecyclerView(arrayListTopics);
                    }
                }
            }).call();
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);

    }
}
