package smarter.uearn.money.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;

import com.google.firebase.analytics.FirebaseAnalytics;
import smarter.uearn.money.adapters.UearnAdapter;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class UearnListActivity extends BaseActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    ListView listView;
    private LinkedHashMap questionAnswerHash = new LinkedHashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        setContentView(R.layout.activity_uearn);
        changeStatusBarColor(this);

        SmarterSMBApplication.currentActivity = this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        Intent intent = getIntent();

        String dataString = "";

        if (intent.hasExtra("data")) {
            dataString = intent.getStringExtra("data");
            System.out.print(dataString);
        }

        if (intent.hasExtra("selected_customer")) {
            String customerName = intent.getStringExtra("selected_customer");
            restoreActionBar(customerName);
        }

        try {
            JSONArray jsonArray = new JSONArray(dataString);
            JSONObject jsonObject = null;
            for(int i=0; i< jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String q = jsonObject.getString("Q:");
                String a = jsonObject.getString("A:");
                questionAnswerHash.put(q, a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView= findViewById(R.id.listView);
        UearnAdapter adapter = new UearnAdapter(questionAnswerHash);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
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
}
