package smarter.uearn.money.activities.feedback;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.internal.Util;
import smarter.uearn.money.R;
import smarter.uearn.money.activities.feedback.interfaces.ErrorFeedBackInterface;
import smarter.uearn.money.activities.feedback.interfaces.OtherInterface;
import smarter.uearn.money.activities.feedback.interfaces.RateInterface;
import smarter.uearn.money.activities.feedback.interfaces.SuggestionsInterface;
import smarter.uearn.money.training.activity.CalendarActivity;
import smarter.uearn.money.training.activity.TrainingDashboardActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;

public class FeedBackActivity extends AppCompatActivity implements View.OnClickListener, ErrorFeedBackInterface, OtherInterface, RateInterface, SuggestionsInterface {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    TextView tvPageLabel;

    Button btnSubmit, btnCancel;
    String feedBack_Other, feedBack_Suggestions, feedBack_Error,feedBack_Rating;
    String feedbackSubject = "";
    boolean feedbackError, feedbackSuggestion, feedbackRate, feedbackOther;
    String review="";
    ImageView  navHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        navHome = findViewById(R.id.ivNavMenu);
        navHome.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        DrawableCompat.setTint(navHome.getDrawable(), ContextCompat.getColor(this.getApplicationContext(), R.color.home_appcolor_purple));


        tvPageLabel = findViewById(R.id.tvPageLabel);
        tvPageLabel.setText("Feedback");

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        navHome.setOnClickListener(this);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new ErrorFragment(), AppConstants.FEEDBACK_PAGE_ERROR);
        adapter.addFragment(new SuggestionsFragment(), AppConstants.FEEDBACK_PAGE_SUGGESTION);
        adapter.addFragment(new RateFragment(), AppConstants.FEEDBACK_PAGE_RATE);
        adapter.addFragment(new OtherFragment(), AppConstants.FEEDBACK_PAGE_OTHER);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if(viewPager.getCurrentItem() == 0){
            feedbackSubject = AppConstants.FEEDBACK_PAGE_ERROR;
            feedbackError = true;
            feedbackSuggestion = false;
            feedbackRate = false;
            feedbackOther = false;
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                if(i == 0){
                    feedbackSubject = AppConstants.FEEDBACK_PAGE_ERROR;
                    feedbackError = true;
                    feedbackSuggestion = false;
                    feedbackRate = false;
                    feedbackOther = false;
                }
                if(i == 1){
                    feedbackSubject = AppConstants.FEEDBACK_PAGE_SUGGESTION;
                    feedbackError = false;
                    feedbackSuggestion = true;
                    feedbackRate = false;
                    feedbackOther = false;
                }
                if(i == 2){
                    feedbackSubject = AppConstants.FEEDBACK_PAGE_RATE;
                    feedbackError = false;
                    feedbackSuggestion = false;
                    feedbackRate = true;
                    feedbackOther = false;
                }
                if(i == 3){
                    feedbackSubject = AppConstants.FEEDBACK_PAGE_OTHER;
                    feedbackError = false;
                    feedbackSuggestion = false;
                    feedbackRate = false;
                    feedbackOther = true;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void sendErrorFeedBack(String message) {
        feedBack_Error = message;
    }

    @Override
    public void sendOtherFedBack(String message) {
        feedBack_Other = message;
    }

    @Override
    public void sendRatingInfo(String message) {
        feedBack_Rating=message;
    }

    @Override
    public void sendSuggestions(String message) {
        feedBack_Suggestions = message;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        int id = view.getId();
        switch (id) {

            case R.id.btnSubmit: {
                validateFeedBack();
            }
            break;
            case R.id.btnCancel: {
                onBackPressed();
            }
            break;

            case R.id.ivNavMenu: {
                onBackPressed();
            }
            break;

        }
    }

    private void validateFeedBack() {
       String other="", suggestions="", error="", rating = "";
       if (!Utils.isStringEmpty(feedBack_Error)){
           error = feedBack_Error.trim();
        }
        if (!Utils.isStringEmpty(feedBack_Other)){
            other = feedBack_Other.trim();
        }
        if (!Utils.isStringEmpty(feedBack_Suggestions)){
            suggestions = feedBack_Suggestions.trim();
        }

        if (!Utils.isStringEmpty(feedBack_Rating)){
            rating = feedBack_Rating.trim();
        }

        if(feedbackError){
            review = error;
        }else{
            error = "";
        }

        if(feedbackOther){
            review = other;
        }else{
            other = "";
        }

        if(feedbackSuggestion){
            review=suggestions;
        }else{
            suggestions="";
        }

        if(feedbackRate){
            review=rating;
        }else{
            rating="";
        }
        if(review.length() > 0){
            setUserFeedback(review);
        }else{
            Toast.makeText(this, "Please provide your details", Toast.LENGTH_SHORT).show();
        }
    }

    JSONObject rsponseObj;

    private void setUserFeedback(String review) {
        if (CommonUtils.isNetworkAvailable(this)) {
            final String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            final String userName = ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
            final String email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
            final String phone = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
            final String company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");

            final JSONObject userDetailObj = new JSONObject();
            JSONArray userInfoArr = new JSONArray();
            try {

                userDetailObj.put("user_id", user_id);
                userDetailObj.put("name", userName);
                userDetailObj.put("email", email);
                userDetailObj.put("phone", phone);
                userDetailObj.put("company", company);
                userDetailObj.put("reason", feedbackSubject);
                userDetailObj.put("message", review);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new AsyncTask<Void, Void, JSONObject>() {
                ProgressDialog progressBar;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    JSONObject jsonObject = null;
                    String url = Urls.getFeedbackUrl(user_id);
                    rsponseObj = DataUploadUtils.postJsonData(url, userDetailObj);
                    return rsponseObj;
                }

                @Override
                protected void onPostExecute(JSONObject jObject) {
                    super.onPostExecute(jObject);
                    try {
                        if ((rsponseObj != null) && (rsponseObj.getString("success") != null)) {
                            String response = rsponseObj.getString("success");
                            if(response.equalsIgnoreCase("Your feedback sent to the concerned authority")) {
                                userFeedbackSuccessDialog();
                            } else {
                                finishActivity();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No network available or poor network", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                    }
                }
            }.execute();
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void userFeedbackSuccessDialog() {
        Toast.makeText(this, "Feedback successfully submitted", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    private void finishActivity() {
        this.finish();
    }
}