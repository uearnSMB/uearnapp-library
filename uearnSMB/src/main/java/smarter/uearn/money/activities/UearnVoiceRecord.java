package smarter.uearn.money.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.models.UearnVoiceTestInfo;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class UearnVoiceRecord extends BaseActivity implements View.OnClickListener {

    private ImageButton recButton;
    CountDownTimer t;
    private UearnVoiceTestInfo voiceInfo;
    private TextView interviewsButton, onboardingButton, trainingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uearn_voice_record);
        recButton = findViewById(R.id.recButton);
        recButton.setOnClickListener(this);
        restoreActionBar("Voice Test");
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        changeStatusBarColor(this);
        SmarterSMBApplication.currentActivity = this;
        Intent i = getIntent();
        voiceInfo = (UearnVoiceTestInfo) i.getSerializableExtra("VoiceInfo");

        interviewsButton = (TextView) findViewById(R.id.interviews);
        interviewsButton.setGravity(Gravity.CENTER);
        interviewsButton.setBackgroundResource(R.drawable.interviews);
        interviewsButton.setText("Interviews");
        interviewsButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));

        onboardingButton = (TextView) findViewById(R.id.onboarding);
        onboardingButton.setGravity(Gravity.CENTER);
        onboardingButton.setBackgroundResource(R.drawable.onboarding);
        onboardingButton.setText("Onboarding");
        onboardingButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));

        trainingButton = (TextView) findViewById(R.id.training);
        trainingButton.setGravity(Gravity.CENTER);
        trainingButton.setBackgroundResource(R.drawable.training);
        trainingButton.setText("Training");
        trainingButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
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
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7030A0")));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recButton:
                Intent intent = new Intent(this, StartUearnVoiceRecord.class);
                intent.putExtra("VoiceInfo", voiceInfo);
                startActivity(intent);
        }
    }
}
