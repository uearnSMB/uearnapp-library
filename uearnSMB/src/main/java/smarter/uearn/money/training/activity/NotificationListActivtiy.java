package smarter.uearn.money.training.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.training.adapter.TrainingNotificationAdapter;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.training.model.TrainingMessage;
import smarter.uearn.money.training.model.TrainingMessageResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class NotificationListActivtiy extends BaseActivity {

    private RecyclerView rv_notifications;
    TextView tv_message_count, tv_new_message_count;
    //TrainingMessageResponse trainingMessageResponse;
    TrainingMessage[] trainingMessage;
    LinearLayout ll_notification, ll_sync;
    ImageView profile_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_training);
        rv_notifications = (RecyclerView) findViewById(R.id.rv_notifications);
        tv_message_count = (TextView) findViewById(R.id.tv_message_count);
        tv_new_message_count = (TextView) findViewById(R.id.tv_new_message_count);
        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_sync.setVisibility(View.GONE);
        ll_notification.setVisibility(View.GONE);
        changeStatusBarColor(this);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void changeStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }
    private void setupRecyclerView(TrainingMessage[] trainingMessage) {
        TrainingNotificationAdapter trainingNotificationAdapter =
                new TrainingNotificationAdapter(NotificationListActivtiy.this, trainingMessage);
        rv_notifications.setLayoutManager(new LinearLayoutManager(this));
        rv_notifications.setAdapter(trainingNotificationAdapter);
        rv_notifications.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        final long notificationCount = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
        if(notificationCount > 0){
            tv_message_count.setText(notificationCount+"");
            tv_new_message_count.setText(notificationCount+"");
        }else {
            tv_message_count.setVisibility(View.GONE);
            tv_new_message_count.setText("0");
        }

        getTrainingMessages();
    }

    private void getTrainingMessages(){
        try {

            new APIProvider.GetTrainingMessageApi("", 1, NotificationListActivtiy.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        JSONObject jsonObject;
                        String successData = null;
                        try {
                            jsonObject = new JSONObject(data);
                            successData = jsonObject.getString("success");
                            Log.e("NotificationList","successData :: "+successData);
                        }catch (Exception e){

                        }
                        Gson g = new Gson();
                        trainingMessage = g.fromJson(successData, TrainingMessage[].class);
                        Log.e("NotificationList","trainingMessageResponse :: "+trainingMessage.length);
                        setupRecyclerView(trainingMessage);
                    }
                }
            }).call();
        } catch (Exception ex) {
            Log.e("NotificationList","ex :: "+ex);
        }
    }
}
