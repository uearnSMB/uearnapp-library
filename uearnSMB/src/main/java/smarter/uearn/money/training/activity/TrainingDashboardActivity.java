package smarter.uearn.money.training.activity;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.activities.UearnProfileActivity;
import smarter.uearn.money.activities.homepage.HomeActivity;
import smarter.uearn.money.activities.profilesettings.ProfileActivity;
import smarter.uearn.money.chats.activity.ChatActivity;
import smarter.uearn.money.chats.model.ResponseChannel;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;

public class TrainingDashboardActivity extends BaseActivity{
    ImageView image_cancel, profile_image;
    LinearLayout ll_welcome_header, ll_batch, ll_trainer, ll_my_training, ll_notification,
            ll_my_calendar, ll_help_desk, ll_sync, ll_show_notification;
    TrainingDataResponse trainingDataResponse;
    TextView tv_message_count, tv_user_name, tv_batch, tv_user_name_my_training, tv_trainer_name,
            tv_calendar_date, tv_banner_title;
    String currentDateandTime;
    public static long remainingDays = 0;
    public static long whichDay = 0;
    public static long trainingDays = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_dashboard);
        image_cancel = (ImageView) findViewById(R.id.image_cancel);
        ll_welcome_header = (LinearLayout) findViewById(R.id.ll_welcome_header);
        ll_batch = (LinearLayout) findViewById(R.id.ll_batch);
        ll_trainer = (LinearLayout) findViewById(R.id.ll_trainer);
        ll_my_training = (LinearLayout) findViewById(R.id.ll_my_training);
        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);
        ll_my_calendar= (LinearLayout) findViewById(R.id.ll_my_calendar);
        ll_help_desk = (LinearLayout) findViewById(R.id.ll_help_desk);
        tv_message_count = (TextView) findViewById(R.id.tv_message_count);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_batch = (TextView) findViewById(R.id.tv_batch);
        tv_user_name_my_training = (TextView) findViewById(R.id.tv_user_name_my_training);
        tv_trainer_name = (TextView) findViewById(R.id.tv_trainer_name);
        tv_calendar_date = (TextView) findViewById(R.id.tv_calendar_date);
        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_show_notification = (LinearLayout) findViewById(R.id.ll_show_notification);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        tv_banner_title = (TextView) findViewById(R.id.tv_banner_title);



        changeStatusBarColor(this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        currentDateandTime = sdf.format(new Date());
        tv_calendar_date.setText(Utils.getDateBatchFormat(currentDateandTime));
        getTrainingDashboardDetails();
        image_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_welcome_header.setVisibility(View.GONE);
            }
        });

        ll_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(TrainingDashboardActivity.this)) {
                    Intent intent = null;
                    intent = new Intent(TrainingDashboardActivity.this, BatchDetailsActivity.class);
                    intent.putExtra("BATCH_DETAILS", trainingDataResponse);
                    startActivity(intent);
                    //finish();
                }else{
                    Toast.makeText(TrainingDashboardActivity.this, "Please check your network", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll_trainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(TrainingDashboardActivity.this)) {
                    if (trainingDataResponse.getBatch_status().equalsIgnoreCase("INPROGRESS") ||
                            trainingDataResponse.getBatch_status().equalsIgnoreCase("COMPLETED")) {
                        Intent intent = null;
                        intent = new Intent(TrainingDashboardActivity.this, TrainerDetailsActivity.class);
                        intent.putExtra("TRAINER_DETAILS", trainingDataResponse);
                        startActivity(intent);
                        //finish();
                    } else {

                    }
                }else{
                    Toast.makeText(TrainingDashboardActivity.this, "Please check your network", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ll_my_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(TrainingDashboardActivity.this)) {
                    Intent intent = null;
                    intent = new Intent(TrainingDashboardActivity.this, MyTrainingActivity.class);
                    intent.putExtra("MY_TRAINING_DETAILS", trainingDataResponse);
                    startActivity(intent);
                    //finish();
                }else{
                    Toast.makeText(TrainingDashboardActivity.this, "Please check your network", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long notificationCount = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
                //if (notificationCount > 0) {
                    Intent intent = null;
                    intent = new Intent(TrainingDashboardActivity.this, NotificationListActivtiy.class);
                    startActivity(intent);
                    //finish();
                //} else {
                //}

            }
        });

        ll_show_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(TrainingDashboardActivity.this, NotificationListActivtiy.class);
                startActivity(intent);
            }
        });
        ll_my_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(TrainingDashboardActivity.this)) {
                    if (trainingDataResponse.getCalender() != null) {
                        Intent intent = null;
                        intent = new Intent(TrainingDashboardActivity.this, CalendarActivity.class);
                        intent.putExtra("CALENDAR_DETAILS", trainingDataResponse);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(TrainingDashboardActivity.this, "Please check your network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll_help_desk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(TrainingDashboardActivity.this, HelpListActivity.class);
                //intent.putExtra("CALENDAR_DETAILS", trainingDataResponse);
                startActivity(intent);
            }
        });

        ll_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTrainingDashboardDetails();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(TrainingDashboardActivity.this, HomeActivity.class);
                //intent.putExtra("CALENDAR_DETAILS", trainingDataResponse);
                startActivity(intent);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getTrainingDashboardDetails(){
        try {
            Log.e("TrainingDashboard","successData api :: ");
            new APIProvider.GetTrainingDataApi("", 1, TrainingDashboardActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        JSONObject jsonObject;
                        String successData = null;
                        try {
                            jsonObject = new JSONObject(data);
                            successData = jsonObject.getString("success");
                            Log.e("TrainingDashboard","successData :: "+successData);
                        }catch (Exception e){

                        }
                        Gson g = new Gson();
                        trainingDataResponse = g.fromJson(successData, TrainingDataResponse.class);
                        //trainingDataResponse.setBatch_status("NOTSTARTED");
                        //trainingDataResponse.setBatch_status("COMPLETED");
                        initUI(trainingDataResponse);
                    }
                }
            }).call();
        } catch (Exception ex) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initUI(TrainingDataResponse trainingDataResponse){

        if(trainingDataResponse.getBatch_status().equalsIgnoreCase("INPROGRESS") ||
                trainingDataResponse.getBatch_status().equalsIgnoreCase("COMPLETED") ) {
            tv_user_name.setText(trainingDataResponse.getBanner_notification_message());
            tv_batch.setText("Batch " + Utils.getDateBatchFormat(trainingDataResponse.getBatch().getStart_date()));
            tv_user_name_my_training.setText(trainingDataResponse.getName());
            tv_trainer_name.setText(trainingDataResponse.getTrainer_name());
            tv_banner_title.setText(trainingDataResponse.getBanner_notification_title());

            if (Integer.parseInt(trainingDataResponse.getUnread_messages()) > 0) {
                tv_message_count.setText(trainingDataResponse.getUnread_messages());
            } else {
                tv_message_count.setVisibility(View.GONE);
            }

            long diff = 0;
            try{
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                Date dateEnd = sdf1.parse(Utils.getDateBatchDaysFormat(trainingDataResponse.getBatch().getEnd_date()));
                Date dateStart = sdf1.parse(Utils.getDateBatchDaysFormat(currentDateandTime));
                diff = dateEnd.getTime() - dateStart.getTime();
                Log.e("Dashboard","diff :: "+ TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                remainingDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            }catch (Exception ex){
                Log.e("Dashboard","exception :: "+ex);
            }

            long diff_which_day = 0;
            try{
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                Date dateStart = sdf1.parse(Utils.getDateBatchDaysFormat(trainingDataResponse.getBatch().getStart_date()));
                Date dateCurrent = sdf1.parse(Utils.getDateBatchDaysFormat(currentDateandTime));
                diff_which_day = dateCurrent.getTime() - dateStart.getTime();
                Log.e("Dashboard","WhichDay :: "+ TimeUnit.DAYS.convert(diff_which_day, TimeUnit.MILLISECONDS));
                whichDay = TimeUnit.DAYS.convert(diff_which_day, TimeUnit.MILLISECONDS) + 1;
            }catch (Exception ex){
                Log.e("Dashboard","exception :: "+ex);
            }

            long diff_days_training = 0;
            try{
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                Date dateEnd = sdf1.parse(Utils.getDateBatchDaysFormat(trainingDataResponse.getBatch().getEnd_date()));
                Date dateStart = sdf1.parse(Utils.getDateBatchDaysFormat(trainingDataResponse.getBatch().getStart_date()));
                diff_days_training = dateEnd.getTime() - dateStart.getTime();
                Log.e("BatchDetails","diff :: "+ TimeUnit.DAYS.convert(diff_days_training, TimeUnit.MILLISECONDS));
                trainingDays = TimeUnit.DAYS.convert(diff_days_training, TimeUnit.MILLISECONDS) + 1;
            }catch (Exception ex){
                Log.e("BatchDetails","exception :: "+ex);
            }
            ApplicationSettings.putLongPref(AppConstants.trainingnotificationCount, Integer.parseInt(trainingDataResponse.getUnread_messages()));
        }else{
            tv_user_name.setText(trainingDataResponse.getName());
            tv_user_name_my_training.setText(trainingDataResponse.getName());
            tv_batch.setText("");
            tv_trainer_name.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    final long notificationCount = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
                    if (notificationCount > 0) {
                        tv_message_count.setText(notificationCount + "");
                    } else {
                        tv_message_count.setVisibility(View.GONE);
                    }
                }
            });

    }

}
