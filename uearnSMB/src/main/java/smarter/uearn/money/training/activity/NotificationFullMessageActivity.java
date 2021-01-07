package smarter.uearn.money.training.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import smarter.uearn.money.notificationmessages.model.NotificationMessages;
import smarter.uearn.money.training.model.MessageReadRequest;
import smarter.uearn.money.training.model.TrainingMessage;
import smarter.uearn.money.training.model.TrainingMessageResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;

public class NotificationFullMessageActivity extends BaseActivity {
    TrainingMessage trainingMessage;
    TextView tv_message_date, tv_full_message, tv_message_title;
    LinearLayout ll_notification, ll_sync;
    ImageView profile_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_full_message);
        tv_message_date = (TextView) findViewById(R.id.tv_message_date);
        tv_full_message = (TextView) findViewById(R.id.tv_full_message);
        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        tv_message_title = (TextView) findViewById(R.id.tv_message_title);
        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_sync.setVisibility(View.GONE);
        ll_notification.setVisibility(View.GONE);
        changeStatusBarColor(this);
        Intent intent = getIntent();
        if (null != intent) { //Null Checking
            trainingMessage = (TrainingMessage) intent.getSerializableExtra("TrainingMessageObj");
            tv_message_date.setText(Utils.getDateTrainingMessageFormat(trainingMessage.getSent_date()));
            tv_full_message.setText(trainingMessage.getMessage());
            tv_message_title.setText(trainingMessage.getTitle());
            if(trainingMessage.getMessage_type().equalsIgnoreCase("unread")){
                setReadTrainingMessage();
            }
        }

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
    private void setReadTrainingMessage(){
        Log.e("NotificationList","setReadTrainingMessage :: ");
        MessageReadRequest messageReadRequest = new MessageReadRequest();
        messageReadRequest.setMessage_id(trainingMessage.getMessage_id());
        messageReadRequest.setTrainer_id("12345");
        messageReadRequest.setMessage_mark("READ");
        try {

            new APIProvider.SetReadTrainingMessageApi(messageReadRequest, 1, NotificationFullMessageActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        JSONObject jsonObject;
                        String successData = null;
                        try {
                            jsonObject = new JSONObject(data);
                            successData = jsonObject.getString("success");
                            Log.e("NotificationList","successData :: "+successData);
                            final long[] notificationCount = {ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0)};
                            ApplicationSettings.putLongPref(AppConstants.trainingnotificationCount, notificationCount[0] - 1);
                            notificationCount[0] = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
                            Log.e("NewMessageReceived","notificationCount :: "+notificationCount[0]);
                        }catch (Exception e){

                        }

                    }
                }
            }).call();
        } catch (Exception ex) {
            Log.e("NotificationList","ex :: "+ex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }
}
