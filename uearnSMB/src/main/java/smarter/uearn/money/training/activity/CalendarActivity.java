package smarter.uearn.money.training.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.training.model.BatchDetails;
import smarter.uearn.money.training.model.CalendarDetails;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.views.CustomCalendarView;

import static smarter.uearn.money.training.activity.TrainingDashboardActivity.trainingDays;
import static smarter.uearn.money.utils.Utils.getDateTrainingMessageFormatDate;

public class CalendarActivity extends BaseActivity implements CustomCalendarView.CustomCalendarViewListener {
    private CustomCalendarView customCalendarView;
    TrainingDataResponse trainingDataResponse;
    TextView agent_code, tv_no_training_days, tv_which_day, tv_message_count;
    private BatchDetails batchDetails;
    private CalendarDetails calendarDetails;
    LinearLayout ll_sync, ll_notification;
    ImageView profile_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        customCalendarView = findViewById(R.id.customCalendarView);
        agent_code = (TextView) findViewById(R.id.agent_code);
        tv_no_training_days = (TextView) findViewById(R.id.tv_no_training_days);
        tv_which_day = (TextView) findViewById(R.id.tv_which_day);
        tv_message_count = (TextView) findViewById(R.id.tv_message_count);
        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);
        profile_image = (ImageView) findViewById(R.id.profile_image);

        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_sync.setVisibility(View.GONE);

        changeStatusBarColor(this);

        Intent intent = this.getIntent();
        if (intent.hasExtra("CALENDAR_DETAILS")) {
            trainingDataResponse = (TrainingDataResponse)intent.getSerializableExtra("CALENDAR_DETAILS");
            agent_code.setText(trainingDataResponse.getBatch_code());
            tv_no_training_days.setText(trainingDays +" Days Training");
            /*int j = Integer.parseInt(trainingDataResponse.getRemaining_days());
            int i = Integer.parseInt(trainingDataResponse.getTraining_days());
            int result = i - j;*/
            batchDetails=trainingDataResponse.getBatch();
            calendarDetails=trainingDataResponse.getCalender();
            if(null != trainingDataResponse.getCalender().getDay_of_training()) {
                tv_which_day.setText(trainingDataResponse.getCalender().getDay_of_training());
            }
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Date date1=null;
        Date date2=null;
        try {
//            String str1=  Utils.getDateTrainingMessageFormat(batchDetails.getBatch_start_date());
//            String  str2= Utils.getDateTrainingMessageFormat(batchDetails.getBatch_start_date());
           date1 =getDateTrainingMessageFormatDate(batchDetails.getStart_date());
           date2 =getDateTrainingMessageFormatDate(batchDetails.getEnd_date());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long notificationCount = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
                if (notificationCount > 0) {
                    Intent intent = null;
                    intent = new Intent(CalendarActivity.this, NotificationListActivtiy.class);
                    startActivity(intent);
                    //finish();
                } else {
                }

            }
        });
        //  clearSelectedDayButton.setOnClickListener(v -> robotoCalendarView.clearSelectedDay());

        // Set listener, in this case, the same activity


//        markDayButton.setOnClickListener(view -> {

          //  Random random = new Random(System.currentTimeMillis());
         //   int style = random.nextInt(2);
//            int daySelected = random.nextInt(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//            calendar.set(Calendar.DAY_OF_MONTH, daySelected);

//            switch (style) {
//                case 0:
//                    customCalendarView.markCircleImage1(calendar.getTime());
//                    break;
//                case 1:
//                    customCalendarView.markCircleImage2(calendar.getTime());
//                    break;
//                default:
//                    break;
//            }


//        });
        customCalendarView.setCustomCalendarListener(this);

        customCalendarView.setShortWeekDays(false);

        customCalendarView.showDateTitle(true);

        customCalendarView.setDate(new Date(),date1,date2,calendarDetails);
//        for(int i=13;i<27;i++){
//            // int  j=1;
//            Calendar calendar = Calendar.getInstance();
//            // calendar.add(Calendar.DAY_OF_MONTH,i);
//            calendar.set(Calendar.DAY_OF_MONTH, i);
//            if(i %3==0){
//                customCalendarView.markCircleImage2(calendar.getTime());
//            }
//            else{
//                customCalendarView.markCircleImage1(calendar.getTime());
//            }
//            // j=j+1;
//        }
//        batchDetails.getBatch_end_date();
//        if ( customCalendarView.getDate().after(date1)) {
//            customCalendarView.markDayAsSelectedDay(date1);
//        }
       // customCalendarView.
    }

    @Override
    public void onDayClick(Date date) {

    }

    @Override
    public void onDayLongClick(Date date) {

    }

    @Override
    public void onRightButtonClick() {

    }

    @Override
    public void onLeftButtonClick() {

    }
    public Date stringtoDate(String strdate)throws Exception {

        Date date1=new SimpleDateFormat("MM/dd/yy").parse(strdate);
       return date1;
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
