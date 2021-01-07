package smarter.uearn.money.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class UearnValueActivity extends BaseActivity implements View.OnClickListener {

    private CardView today_card, this_month_card, lifetime_card;
    private RelativeLayout today_bar_view, this_month_bar_view, lifetime_bar_view;
    private TextView date_time;
    private LinearLayout uearn_passbook_content, passbookLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_uearn_value);
        restoreActionBar("Uearned");
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        changeStatusBarColor(this);
        SmarterSMBApplication.currentActivity = this;
        initUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    private void initUi() {
        today_card = findViewById(R.id.today_card);
        this_month_card = findViewById(R.id.this_month_card);
        lifetime_card = findViewById(R.id.lifetime_card);
        uearn_passbook_content = findViewById(R.id.uearn_passbook_content);
        today_card.setOnClickListener(this);
        this_month_card.setOnClickListener(this);
        lifetime_card.setOnClickListener(this);
        passbookLayout = findViewById(R.id.passbookLayout);
        passbookLayout.setOnClickListener(this);
        today_bar_view = findViewById(R.id.today_bar_view);
        today_bar_view.setVisibility(View.VISIBLE);
        this_month_bar_view = findViewById(R.id.this_month_bar_view);
        lifetime_bar_view = findViewById(R.id.lifetime_bar_view);
        date_time = findViewById(R.id.date_time);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.home) {
            onBackPressed();


            today_bar_view.setVisibility(View.VISIBLE);
            this_month_bar_view.setVisibility(View.GONE);
            lifetime_bar_view.setVisibility(View.GONE);
            date_time.setText("05 Feb 2019");
        } else if (id == R.id.today_card) {
            today_bar_view.setVisibility(View.VISIBLE);
            this_month_bar_view.setVisibility(View.GONE);
            lifetime_bar_view.setVisibility(View.GONE);
            date_time.setText("05 Feb 2019");
        } else if (id == R.id.this_month_card) {
            today_bar_view.setVisibility(View.GONE);
            this_month_bar_view.setVisibility(View.VISIBLE);
            lifetime_bar_view.setVisibility(View.GONE);
        } else if (id == R.id.lifetime_card) {
            today_bar_view.setVisibility(View.GONE);
            this_month_bar_view.setVisibility(View.GONE);
            lifetime_bar_view.setVisibility(View.VISIBLE);
        } else if (id == R.id.passbookLayout) {
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

    public void restoreActionBar(String mTitle) {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dd374c")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        MenuItem sendButton  = menu.findItem(R.id.action_one);
        sendButton.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int id = mi.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }
}
