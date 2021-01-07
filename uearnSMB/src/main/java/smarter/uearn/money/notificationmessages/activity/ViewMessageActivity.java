package smarter.uearn.money.notificationmessages.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.notificationmessages.model.MessageStatus;
import smarter.uearn.money.notificationmessages.model.NotificationMessages;
import smarter.uearn.money.notificationmessages.model.PostNotificationMessage;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.Utils;

public class ViewMessageActivity extends BaseActivity {

    TextView tv_message, tv_title, tv_time;
    WebView web_description;
    NotificationMessages notificationMessages;
    ImageView img_back;
    PostNotificationMessage postNotificationMessage;
    MessageStatus[] messageStatus ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        tv_message = (TextView) findViewById(R.id.tv_message);
        web_description = (WebView) findViewById(R.id.web_description);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_time = (TextView) findViewById(R.id.tv_time);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_title.setText("Message");

        changeStatusBarColor(this);
        Intent intent = getIntent();
        if (null != intent) { //Null Checking
            notificationMessages = (NotificationMessages) intent.getSerializableExtra("MessageObj");
            tv_message.setText(notificationMessages.getTitle());
            tv_time.setText(Utils.getDateTime(notificationMessages.getCreate_ist()));
        }

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        postNotificationMessage = new PostNotificationMessage();
        messageStatus = new MessageStatus[]{new MessageStatus()};
        Log.e("MessageActivity","count :: "+notificationMessages.getId());
        postNotificationMessage.setUser_id(notificationMessages.getUser_id());
        messageStatus[0].setId(notificationMessages.getId());
        messageStatus[0].setStatus("READ");
        postNotificationMessage.setPostNotifications(messageStatus);
        if(notificationMessages.getStatus().equalsIgnoreCase("UNREAD")) {
            ReadMessageApi(postNotificationMessage);
        }else{

        }
        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlData = notificationMessages.getMessage();
        // Load html source code into webview to show the html content.
        web_description.loadDataWithBaseURL(null, htmlData, mimeType, encoding, null);
    }

    public void changeStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void ReadMessageApi(PostNotificationMessage postNotificationMessage){
        try {
            String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID,"");
            new APIProvider.GetRequestForReadMessage(postNotificationMessage, 1, this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        Log.e("ViewMessage","data :: "+data);
                        //Toast.makeText(ViewMessageActivity.this,data,Toast.LENGTH_SHORT).show();
                        //Reduce count for notification
                        final long[] notificationCount = {ApplicationSettings.getLongPref(AppConstants.notificationCount, 0)};
                        ApplicationSettings.putLongPref(AppConstants.notificationCount, notificationCount[0] - 1);
                        notificationCount[0] = ApplicationSettings.getLongPref(AppConstants.notificationCount, 0);
                        Log.e("NewMessageReceived","notificationCount :: "+notificationCount[0]);
                    }
                    Log.e("ViewMessage","code :: "+failure_code);
                }
            }).call();
        }catch (Exception ex){
            Log.e("ViewMessage","Exception :: "+ex);
        }
    }
}
