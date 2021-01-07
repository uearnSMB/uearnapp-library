package smarter.uearn.money.notificationmessages.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.notificationmessages.adapter.NotificationMessageAdapter;
import smarter.uearn.money.notificationmessages.model.NotificationMessageResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;

public class NotificationMessageActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rv_messages;
    private ImageView img_back;
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_messages);
        rv_messages = (RecyclerView) findViewById(R.id.rv_messages);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.title_message));
        changeStatusBarColor(this);
        img_back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotificationMessages();
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

    public void changeStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }
    private void getNotificationMessages(){
        try {
            String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID,"");
            new APIProvider.GetNotificationMessagesApi("", 1, this, "Processing your request.", new API_Response_Listener<NotificationMessageResponse>() {

                @Override
                public void onComplete(NotificationMessageResponse data, long request_code, int failure_code) {
                    if (data != null) {
                        //Log.e("NotificationMessage","data :: "+data);
                        if(data.getSuccess().length == 0){
                            Toast.makeText(NotificationMessageActivity.this, "You don't have any notifications", Toast.LENGTH_SHORT).show();
                        }
                        setupRecyclerView(data);
                    }
                    //Log.e("NotificationMessage","code :: "+failure_code);
                }
            }).call();
        }catch (Exception ex){
            //Log.e("NotificationMessage","Exception :: "+ex);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;
        intent = new Intent(this, UearnHome.class);
        startActivity(intent);
        finish();
    }

    private void setupRecyclerView(NotificationMessageResponse notificationMessageResponse) {
        NotificationMessageAdapter notificationMessageAdapter =
                new NotificationMessageAdapter(NotificationMessageActivity.this, notificationMessageResponse);
        rv_messages.setLayoutManager(new LinearLayoutManager(this));
        rv_messages.setAdapter(notificationMessageAdapter);
        rv_messages.setItemAnimator(new DefaultItemAnimator());
    }
}
