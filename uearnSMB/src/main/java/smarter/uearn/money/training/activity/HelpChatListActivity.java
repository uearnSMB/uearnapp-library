package smarter.uearn.money.training.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;

public class HelpChatListActivity extends BaseActivity {

    LinearLayout ll_notification, ll_sync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_topic_chat);

        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);
        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_sync.setVisibility(View.GONE);
        ll_notification.setVisibility(View.GONE);
    }
}
