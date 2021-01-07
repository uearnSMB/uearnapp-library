package smarter.uearn.money.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import smarter.uearn.money.R;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;

public class UearnFeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayAdapter<CharSequence> feedBackAdapter;
    private Spinner feedBackSpinner;
    private EditText feedbackText;
    private Button submitBtn;
    private String feedbackSubject = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_uearn_feedback);
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        restoreActionBar("Feedback");
        SmarterSMBApplication.currentActivity = this;
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        changeStatusBarColor(this);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    public void restoreActionBar(String mTitle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7030A0")));
    }


    private void initUI() {
        feedBackSpinner = findViewById(R.id.feedbackSpinner);
        feedbackText = findViewById(R.id.feedbackText);
        feedBackAdapter = ArrayAdapter.createFromResource(this, R.array.feedback_spinner_array, android.R.layout.simple_spinner_item);
        feedBackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedBackSpinner.setAdapter(feedBackAdapter);
        submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);
        feedBackSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = feedBackSpinner.getSelectedItem().toString();
                if (selectedItem != null && !selectedItem.isEmpty() && !selectedItem.equals("Choose an option")) {
                    feedbackSubject = selectedItem;
                    if(selectedItem.equalsIgnoreCase("Choose an option")) {
                        feedbackText.setHint("Describe an issue/feedback");
                    } else if (selectedItem.equalsIgnoreCase("Report an error")) {
                        feedbackText.setHint("Mention your issues here");
                    } else if (selectedItem.equalsIgnoreCase("Give a suggestion")) {
                        feedbackText.setHint("Give your suggestion here");
                    } else if (selectedItem.equalsIgnoreCase("Anything else")) {
                        feedbackText.setHint("Give your comment here");
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.submitBtn:
                InputMethodManager imm = (InputMethodManager)SmarterSMBApplication.currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(SmarterSMBApplication.currentActivity.getWindow().getDecorView().getWindowToken(), 0);
                if(!feedbackSubject.isEmpty() && !feedbackText.getText().toString().isEmpty()) {
                    setUserFeedback();
                } else {
                    if(feedbackSubject.isEmpty()) {
                        Toast.makeText(this, "Please choose an option from the drop down", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Please provide valid feedback.", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    JSONObject rsponseObj;

    private void setUserFeedback() {
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
                userDetailObj.put("message", feedbackText.getText().toString());
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Thank you for your feedback")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishActivity();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Feedback");
        alert.show();
    }




    private void userFeedbackUpdate() {
        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this);
        inputAlert.setTitle("Your feedback sent to the concerned authority");
        final EditText userInput = new EditText(this);
        userInput.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(250)
        });
        //String currentLocation = locationTv.getText().toString();
//        if(!currentLocation.isEmpty()) {
//            userInput.setText(currentLocation);
//        }
        inputAlert.setView(userInput);
        inputAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String userInputValue = userInput.getText().toString();

            }
        });


//        inputAlert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        AlertDialog alertDialog = inputAlert.create();
        alertDialog.show();
    }

    private void finishActivity() {
        this.finish();
    }

    public void changeStatusBarColor(AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
