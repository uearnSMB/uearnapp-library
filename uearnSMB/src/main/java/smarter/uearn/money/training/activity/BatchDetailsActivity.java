package smarter.uearn.money.training.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utilities;
import smarter.uearn.money.utils.Utils;

import static smarter.uearn.money.training.activity.TrainingDashboardActivity.whichDay;

public class BatchDetailsActivity extends BaseActivity {
    TextView tv_batch_id, tv_days_training, tv_batch_start_date, tv_batch_timings, tv_batch_capacity
            , tv_batch_end_date, tv_remaining_days, tv_trainer_name, tv_current_day, tv_link,
            tv_which_day, tv_message_count;
    ImageView image_mail, image_share, image_copy, profile_image;
    TrainingDataResponse trainingDataResponse;
    String currentDateandTime;
    LinearLayout ll_which_day_batch, ll_batch_training_details, ll_batch_share_details,
            ll_batch_not_started, ll_sync, ll_notification;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_details);
        tv_batch_id = (TextView) findViewById(R.id.tv_batch_id);
        tv_days_training = (TextView) findViewById(R.id.tv_days_training);
        tv_batch_start_date = (TextView) findViewById(R.id.tv_batch_start_date);
        tv_batch_timings = (TextView) findViewById(R.id.tv_batch_timings);
        tv_batch_capacity = (TextView) findViewById(R.id.tv_batch_capacity);
        tv_batch_end_date = (TextView) findViewById(R.id.tv_batch_end_date);
        tv_remaining_days = (TextView) findViewById(R.id.tv_remaining_days);
        tv_trainer_name = (TextView) findViewById(R.id.tv_trainer_name);
        tv_current_day = (TextView) findViewById(R.id.tv_current_day);
        tv_link = (TextView) findViewById(R.id.tv_link);
        tv_which_day = (TextView) findViewById(R.id.tv_which_day);
        tv_message_count = (TextView) findViewById(R.id.tv_message_count);
        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);

        image_mail = (ImageView) findViewById(R.id.image_mail);
        image_share = (ImageView) findViewById(R.id.image_share);
        image_copy = (ImageView) findViewById(R.id.image_copy);

        ll_which_day_batch = (LinearLayout) findViewById(R.id.ll_which_day_batch);
        ll_batch_training_details = (LinearLayout) findViewById(R.id.ll_batch_training_details);
        ll_batch_share_details = (LinearLayout) findViewById(R.id.ll_batch_share_details);
        ll_batch_not_started = (LinearLayout) findViewById(R.id.ll_batch_not_started);

        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_sync.setVisibility(View.GONE);

        profile_image = (ImageView) findViewById(R.id.profile_image);

        changeStatusBarColor(this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        currentDateandTime = sdf.format(new Date());

        Intent intent = this.getIntent();
        if (intent.hasExtra("BATCH_DETAILS")) {
            trainingDataResponse = (TrainingDataResponse)intent.getSerializableExtra("BATCH_DETAILS");
            if(trainingDataResponse.getBatch_status().equalsIgnoreCase("INPROGRESS") ||
                    trainingDataResponse.getBatch_status().equalsIgnoreCase("COMPLETED")) {
                tv_batch_id.setText("Batch ID :  " + trainingDataResponse.getBatch_code());
                long diff_days_training = 0;
                try{
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateEnd = sdf1.parse(Utils.getDateBatchDaysFormat(trainingDataResponse.getBatch().getEnd_date()));
                    Date dateStart = sdf1.parse(Utils.getDateBatchDaysFormat(trainingDataResponse.getBatch().getStart_date()));
                    diff_days_training = dateEnd.getTime() - dateStart.getTime();
                    Log.e("BatchDetails","diff :: "+ TimeUnit.DAYS.convert(diff_days_training, TimeUnit.MILLISECONDS));
                }catch (Exception ex){
                    Log.e("BatchDetails","exception :: "+ex);
                }
                tv_days_training.setText(TimeUnit.DAYS.convert(diff_days_training, TimeUnit.MILLISECONDS)+1 + " Days Training");
                tv_batch_start_date.setText(Utils.getDateBatchFormat(trainingDataResponse.getBatch().getStart_date()));
                tv_batch_timings.setText(trainingDataResponse.getBatch().getStart_time());
                tv_batch_capacity.setText(trainingDataResponse.getBatch().getCapacity());
                tv_batch_end_date.setText(Utils.getDateBatchFormat(trainingDataResponse.getBatch().getEnd_date()));
                long diff = 0;
                try{
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateEnd = sdf1.parse(Utils.getDateBatchDaysFormat(trainingDataResponse.getBatch().getEnd_date()));
                    Date dateStart = sdf1.parse(Utils.getDateBatchDaysFormat(currentDateandTime));
                    diff = dateEnd.getTime() - dateStart.getTime();
                    Log.e("BatchDetails","diff :: "+ TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                }catch (Exception ex){
                    Log.e("BatchDetails","exception :: "+ex);
                }

                tv_remaining_days.setText(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+"");
                tv_trainer_name.setText(trainingDataResponse.getTrainer_name());
                tv_link.setText(trainingDataResponse.getBatch().getTraining_location());
                tv_which_day.setText(trainingDataResponse.getCalender().getDay_of_training());
                tv_current_day.setText("Today [" + Utils.getDate(currentDateandTime) + "]");
            }else{
                tv_batch_id.setText("Batch ID :  " + trainingDataResponse.getBatch_code());
                ll_which_day_batch.setVisibility(View.GONE);
                ll_batch_training_details.setVisibility(View.GONE);
                ll_batch_share_details.setVisibility(View.GONE);
                ll_batch_not_started.setVisibility(View.VISIBLE);
            }
        }
        image_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        image_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Insert Subject here");
                String content = tv_link.getText().toString();
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,content);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        image_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                ClipboardManager myClipboard = null;
                ClipData myClip;
                text = tv_link.getText().toString();
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Text Copied",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long notificationCount = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
                if (notificationCount > 0) {
                    Intent intent = null;
                    intent = new Intent(BatchDetailsActivity.this, NotificationListActivtiy.class);
                    startActivity(intent);
                    //finish();
                } else {
                }

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void sendEmail() {
        Log.e("BatchDetails", "Send email");
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Training link");
        emailIntent.putExtra(Intent.EXTRA_TEXT, tv_link.getText().toString());
        //emailIntent.setType("plain/text");
        emailIntent.setType("message/rfc822");
        emailIntent.setPackage("com.google.android.gm");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.e("BatchDetails", "Finished sending email...");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BatchDetailsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        final long notificationCount = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
        if(notificationCount > 0){
            tv_message_count.setText(notificationCount+"");
        }else {
            tv_message_count.setVisibility(View.GONE);
        }
    }
}
