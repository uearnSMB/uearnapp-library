package smarter.uearn.money.agentslots.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.activities.UearnProfileActivity;
import smarter.uearn.money.agentslots.adapter.AgentSlotsAdapter;
import smarter.uearn.money.agentslots.model.SlotData;
import smarter.uearn.money.agentslots.model.SlotResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class AgentSlotsActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rv_slots;
    private Button done;
    private ImageView img_back;
    private TextView tv_title;
    String str = "{\n" +
            "\t\"data\": [{\n" +
            "\t\t\t\"day\": \"Today\",\n" +
            "\t\t\t\"date\": \"2nd mar mon\",\n" +
            "\t\t\t\"rows\": [{\n" +
            "\t\t\t\t\t\"time\": \"3:00PM - 6:00PM\",\n" +
            "\t\t\t\t\t\"status\": \"FULL\",\n" +
            "\t\t\t\t\t\"checklist\": \"waiting list 31\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"time\": \"6:00PM - 9:00PM\",\n" +
            "\t\t\t\t\t\"status\": \"Fast Filling\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"time\": \"9:00PM - 12:00AM\",\n" +
            "\t\t\t\t\t\"status\": \"Available\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"day\": \"Tomorrow\",\n" +
            "\t\t\t\"date\": \"3rd mar tue\",\n" +
            "\t\t\t\"rows\": [{\n" +
            "\t\t\t\t\"time\": \"9:00AM - 12:00PM\",\n" +
            "\t\t\t\t\"status\": \"Available\"\n" +
            "\t\t\t}]\n" +
            "\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}";
    SlotResponse response;
    String strFromActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_slots);
        Intent intent = this.getIntent();
        if (intent.hasExtra("SLOTSREDIRECT")) {
            strFromActivity = intent.getStringExtra("SLOTSREDIRECT");
        }
        rv_slots = (RecyclerView) findViewById(R.id.rv_slots);
        done = (Button) findViewById(R.id.done);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        done.setText(getResources().getString(R.string.txt_done));
        tv_title.setText(getResources().getString(R.string.booking_slots));
        Gson g = new Gson();
        response = g.fromJson(str, SlotResponse.class);
        img_back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        getAgentSlots();
    }

    private void setupRecyclerView(SlotData data) {
        SlotResponse slotResponse = data.getSuccess();
        AgentSlotsAdapter agentSlotsAdapter = new AgentSlotsAdapter(AgentSlotsActivity.this, slotResponse);
        rv_slots.setLayoutManager(new LinearLayoutManager(this));
        rv_slots.setAdapter(agentSlotsAdapter);
        rv_slots.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;
        if(strFromActivity.equalsIgnoreCase("UearnProfileActivity")) {
            intent = new Intent(this, UearnProfileActivity.class);
        }else{
            intent = new Intent(this, UearnHome.class);
        }
        startActivity(intent);
        finish();
    }

    private void getAgentSlots(){
        try {
            String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID,"");
            new APIProvider.GetAgentSlotsApi("", 1, this, "Processing your request.", new API_Response_Listener<SlotData>() {

                @Override
                public void onComplete(SlotData data, long request_code, int failure_code) {
                    if (data != null) {
                        Log.e("AgentSlots","response :: "+data);
                        setupRecyclerView(data);
                    }
                    Log.e("AgentSlots","code :: "+failure_code);
                }
            }).call();
        }catch (Exception ex){
            Log.e("AgentSlots","Exception :: "+ex);
        }
    }
}
