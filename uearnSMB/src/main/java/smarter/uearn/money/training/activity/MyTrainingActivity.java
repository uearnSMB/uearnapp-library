package smarter.uearn.money.training.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.SmarterSMBApplication;

import static smarter.uearn.money.training.activity.TrainingDashboardActivity.remainingDays;

public class MyTrainingActivity extends BaseActivity {
    TrainingDataResponse trainingDataResponse;
    TextView tv_agent_id, tv_user_name, tv_days_attended, tv_batch_timings, tv_location,
            tv_completed_hours, tv_reamining_days, tv_trainer, tv_which_day, tv_message_count;
    RelativeLayout rl_training_completed, rl_batch_not_allocated, rl_second;
    LinearLayout ll_sync, ll_notification;
    ImageView profile_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_training);
        tv_agent_id = (TextView) findViewById(R.id.tv_agent_id);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_days_attended = (TextView) findViewById(R.id.tv_days_attended);
        tv_batch_timings = (TextView) findViewById(R.id.tv_batch_timings);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_completed_hours = (TextView) findViewById(R.id.tv_completed_hours);
        tv_reamining_days = (TextView) findViewById(R.id.tv_reamining_days);
        tv_trainer = (TextView) findViewById(R.id.tv_trainer);
        //tv_which_day = (TextView) findViewById(R.id.tv_which_day);
        tv_message_count = (TextView) findViewById(R.id.tv_message_count);
        rl_training_completed = (RelativeLayout) findViewById(R.id.rl_training_completed);
        rl_batch_not_allocated = (RelativeLayout)  findViewById(R.id.rl_batch_not_allocated);
        rl_second = (RelativeLayout) findViewById(R.id.rl_second);
        profile_image = (ImageView) findViewById(R.id.profile_image);

        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);

        changeStatusBarColor(this);
        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_sync.setVisibility(View.GONE);
        Intent intent = this.getIntent();
        if (intent.hasExtra("MY_TRAINING_DETAILS")) {
            trainingDataResponse = (TrainingDataResponse)intent.getSerializableExtra("MY_TRAINING_DETAILS");
            if(trainingDataResponse.getBatch_status().equalsIgnoreCase("INPROGRESS")) {
                tv_agent_id.setText(trainingDataResponse.getId());
                tv_user_name.setText(trainingDataResponse.getName());
                tv_days_attended.setText(trainingDataResponse.getTraining().getDays_attended());
                tv_batch_timings.setText(trainingDataResponse.getBatch().getStart_time());
                tv_location.setText(trainingDataResponse.getTraining().getLocation());
                tv_completed_hours.setText(trainingDataResponse.getTraining().getHours_completed());
                tv_reamining_days.setText(remainingDays+ "");
                tv_trainer.setText(trainingDataResponse.getTrainer_name());
                //tv_which_day.setText(trainingDataResponse.getData().getBatch().getCurrent_day());
                rl_batch_not_allocated.setVisibility(View.GONE);
                rl_training_completed.setVisibility(View.GONE);
            }else if(trainingDataResponse.getBatch_status().equalsIgnoreCase("COMPLETED")){
                rl_training_completed.setVisibility(View.VISIBLE);
                tv_agent_id.setText(trainingDataResponse.getId());
                tv_user_name.setText(trainingDataResponse.getName());
                tv_days_attended.setText(trainingDataResponse.getTraining().getDays_attended());
                tv_batch_timings.setText(trainingDataResponse.getBatch().getStart_time());
                tv_location.setText(trainingDataResponse.getTraining().getLocation());
                tv_completed_hours.setText(trainingDataResponse.getTraining().getHours_completed());
                tv_reamining_days.setText(remainingDays+"");
                tv_trainer.setText(trainingDataResponse.getTrainer_name());
                rl_batch_not_allocated.setVisibility(View.GONE);
            }else{
                rl_training_completed.setVisibility(View.GONE);
                rl_batch_not_allocated.setVisibility(View.VISIBLE);
                rl_second.setVisibility(View.GONE);
                tv_agent_id.setText(trainingDataResponse.getId());
                tv_user_name.setText(trainingDataResponse.getName());
            }
        }

        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long notificationCount = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
                if (notificationCount > 0) {
                    Intent intent = null;
                    intent = new Intent(MyTrainingActivity.this, NotificationListActivtiy.class);
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
