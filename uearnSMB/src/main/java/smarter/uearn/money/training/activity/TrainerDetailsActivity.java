package smarter.uearn.money.training.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.training.model.TrainerReview;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.views.CustomCalendarView;

import static smarter.uearn.money.training.activity.TrainingDashboardActivity.whichDay;

public class TrainerDetailsActivity extends BaseActivity {
    TextView tv_batch_code, tv_trainer_name, tv_which_day, tv_submit, tv_date_display, tv_message_count;
    EditText et_review;
    RatingBar rating_star;
    ImageView image_date_picker;
    TrainingDataResponse trainingDataResponse;
    private int mYear, mMonth, mDay;
    String currentDateandTime;
    LinearLayout ll_sync, ll_notification;
    ImageView profile_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_details);
        tv_batch_code = (TextView) findViewById(R.id.tv_batch_code);
        tv_trainer_name = (TextView) findViewById(R.id.tv_trainer_name);
        tv_which_day = (TextView) findViewById(R.id.tv_which_day);
        et_review = (EditText) findViewById(R.id.et_review);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_date_display = (TextView) findViewById(R.id.tv_date_display);
        rating_star = (RatingBar) findViewById(R.id.rating_star);
        image_date_picker = (ImageView) findViewById(R.id.image_date_picker);
        tv_message_count = (TextView) findViewById(R.id.tv_message_count);
        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_sync.setVisibility(View.GONE);
        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        changeStatusBarColor(this);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        currentDateandTime = sdf.format(new Date());
        Log.e("TrainerDetails","calender :: "+currentDateandTime);
        Log.e("TrainerDetails","get our time  :: "+ Utils.getDate(currentDateandTime));
        // calendar.add(Calendar.DAY_OF_MONTH,i);
        tv_date_display.setText(Utils.getDateWithoutDay(currentDateandTime) + " ["+ Utils.getDate(currentDateandTime) + "]");
        Intent intent = this.getIntent();
        if (intent.hasExtra("TRAINER_DETAILS")) {
            trainingDataResponse = (TrainingDataResponse)intent.getSerializableExtra("TRAINER_DETAILS");
            getReview(trainingDataResponse.getCalender().getDay_of_training(), trainingDataResponse.getBatch_code());


        }
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(et_review.getText().toString().trim().length() > 1){
                    Toast.makeText(TrainerDetailsActivity.this, "Please provide your review", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                Log.e("TrainerDetails","Rating :: "+rating_star.getRating());
                if(rating_star.getRating() < 1){
                    Toast.makeText(TrainerDetailsActivity.this, "Please provide your Rating", Toast.LENGTH_SHORT).show();
                    return;
                }
                reviewSubmit(trainingDataResponse.getId(), trainingDataResponse.getGroupid(),
                        tv_which_day.getText().toString(), trainingDataResponse.getBatch_code(),
                        et_review.getText().toString(), String.valueOf(rating_star.getRating()));
            }
        });

        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long notificationCount = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
                if (notificationCount > 0) {
                    Intent intent = null;
                    intent = new Intent(TrainerDetailsActivity.this, NotificationListActivtiy.class);
                    startActivity(intent);
                    //finish();
                } else {
                }

            }
        });

        /*image_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(TrainerDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                                //txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                Log.e("TrainerDetails","date and time :: "+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                currentDateandTime = sdf.format(calendar.getTime());
                                Log.e("TrainerDetails","calendarsf  :: "+currentDateandTime);
                                Log.e("TrainerDetails","get our time asef  :: "+ Utils.getDate(currentDateandTime));
                                //getReview(dayOfMonth+"/"+"0"+(monthOfYear + 1)+"/"+year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });*/

        final long notificationCount = ApplicationSettings.getLongPref(AppConstants.trainingnotificationCount, 0);
        if(notificationCount > 0){
            tv_message_count.setText(notificationCount+"");
        }else {
            tv_message_count.setVisibility(View.GONE);
        }
    }

    private void initUI(TrainingDataResponse trainingDataResponse){
        tv_batch_code.setText("Batch ID :  "+trainingDataResponse.getBatch_code());
        tv_trainer_name.setText(trainingDataResponse.getTrainer_name());
        //tv_which_day.setText(whichDay + "");
    }
    private void reviewSubmit(String user_id, String groupid, String day, String batch_code, String review, String rating){
        TrainerReview trainerReview = new TrainerReview();
        trainerReview.setUser_id(user_id);
        trainerReview.setGroupid(groupid);
        trainerReview.setDay(day);
        trainerReview.setBatch_code(batch_code);
        trainerReview.setReview(review);
        trainerReview.setRating(rating);
        try {
            new APIProvider.GetRequestForTrainerReview(trainerReview, 1, TrainerDetailsActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        Log.e("TrainerDetails","Response data  :: "+data);
                        Toast.makeText(TrainerDetailsActivity.this, "Review Posted Successfully", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("TrainerDetails","failure_code  :: "+failure_code);
                }
            }).call();
        }catch (Exception e){

        }
    }

    private void getReview(String dayTraining, String batchCode){
        Log.e("TrainerDetails","BatchDetails :: "+dayTraining + " :: "+batchCode);
        TrainerReview review = new TrainerReview();
        review.setDay(dayTraining);
        review.setBatch_code(batchCode);
        try{
            new APIProvider.GetTrainerReviewApi(review, 1, TrainerDetailsActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        Log.e("TrainerDetails","Response data  :: "+data);
                        JSONObject jsonObject;
                        String successData = null;
                        try {
                            jsonObject = new JSONObject(data);
                            successData = jsonObject.getString("success");
                        }catch (Exception e){

                        }
                        Gson g = new Gson();
                        TrainerReview trainerReview = g.fromJson(successData, TrainerReview.class);
                        tv_which_day.setText(trainerReview.getDay());
                        tv_date_display.setText(Utils.getDateWithoutDay(currentDateandTime)
                                + " ["+ Utils.getDate(trainerReview.getDate()) + "]");
                        if(null != trainerReview.getRating()) {
                            rating_star.setRating(Float.valueOf(trainerReview.getRating()));
                        }
                        if(null != trainerReview.getReview()){
                            et_review.setText(trainerReview.getReview());
                        }

                        initUI(trainingDataResponse);
                    }
                    Log.e("TrainerDetails","failure_code  :: "+failure_code);
                }


            }).call();
        }catch (Exception e){

        }
    }
}
