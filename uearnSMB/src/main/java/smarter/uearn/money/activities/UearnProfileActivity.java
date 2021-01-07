package smarter.uearn.money.activities;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import smarter.uearn.money.R;
import smarter.uearn.money.agentslots.activity.AgentSlotsActivity;
import smarter.uearn.money.broadcastReceiver.Alarm_Receiver;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.fragments.settings.SettingsOptionFragment;
import smarter.uearn.money.models.AttachmentList;
import smarter.uearn.money.services.CalldataSendService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.ReuploadService;

public class UearnProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ImageView outside_imageview, profile_theme;
    private Button voice_test_btn, training_btn, startEarningBtn;
    private Uri imageFilePath, logoUri;
    private CircleImageView iv_profile_pic;
    protected static final int PICK_Camera_IMAGE = 0;
    protected static final int PICK_Profile_IMAGE = 2;
    private Bitmap ThumbImage;
    private int LOGO_FETCH_REQUEST = 1;
    private String uploadurl, imagePath;
    private RelativeLayout user_profile_theme;
    private LinearLayout settting_layout, faqLayout, sign_out_layout, back_imageview, getFeedback, referAndEarn, slotsregister, ll_main_slot_register;
    private boolean isProfile, isTheme = false;
    private TextView appVersionTv, member_since, languageTv, stepLeftTv, stepsAwayTv;
    private TextView gendertypeTv, user_birth_date, user_email_id, phone_no, company_type;
    private ImageView edit_email_id, editGender, edit_phone_no, edit_location, edit_language, edit_user_name, editDateOfBirth;
    private EditText user_name;
    ImageView img_logo;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 3;
    private Intent pictureActionIntent = null;
    private TextView locationTv, auditScoreTv;
    Bitmap bitmap;
    String gender = "";
    private String agentBirthDate = "";
    private Boolean isNameChange = false, isLanguageChange = false, isLocationChange = false, isBirthDateChange = false, isGenderSelected = false;
    String selectedImagePath;
    AlertDialog.Builder alertdialogbuilder;
    String[] AlertDialogItems = new String[]{
            "English", "Hindi", "Kannada", "Telugu", "Tamil", "Marathi", "Bengali", "Gujarati"
    };

    List<String> ItemsIntoList;
    private LinearLayout voiceTestStageLayout;

    boolean[] Selectedtruefalse = new boolean[]{
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false
    };

    public SettingsOptionFragment settingsOptionFragment;
    DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
    int callerId = -1;
    public static final String DATE_FORMAT = "EEE, MMM d, yyyy";

    private Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_uearn_profile);
        CommonUtils.permissionsCheck(UearnProfileActivity.this);
        changeStatusBarColor(UearnProfileActivity.this);
        SmarterSMBApplication.currentActivity = this;
        initUi();
        getImage();

        String userCreated = ApplicationSettings.getPref(AppConstants.USER_CREATED, "");
        //auditScoreTv
        String user_status = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        if(user_status.equals("On Board")) {
            String auditScore = ApplicationSettings.getPref(AppConstants.USER_AUDIT_SCORE, "");
            auditScoreTv.setVisibility(View.VISIBLE);
            auditScoreTv.setText(auditScore);
        } else {
            auditScoreTv.setVisibility(View.GONE);
        }

        if(userCreated != null && !userCreated.isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm");
            String newDateStr = null;
            try {
                newDateStr = simpleDateFormat.format(simpleDateFormat.parse(userCreated));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String str[] = newDateStr.split("-");
            int month = Integer.parseInt(str[1]);
            String monthStr = (String) getMonth(month);
            String year = str[0];
            String memberSince = "Member since "+monthStr+" "+year+"";
            member_since.setText(memberSince); // - Sujit to fix this error in IDE
        }
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }



    private void initUi() {
        voiceTestStageLayout = findViewById(R.id.voiceTestStageLayout);
        back_imageview = findViewById(R.id.back_imageview);
        stepLeftTv = findViewById(R.id.stepLeftTv);
        stepsAwayTv = findViewById(R.id.stepsAwayTv);
        outside_imageview = findViewById(R.id.outside_imageview);
        iv_profile_pic = findViewById(R.id.iv_profile_pic);
        appVersionTv = findViewById(R.id.appVersionTv);
        String appVersion = "v " + AppConstants.APP_VERSION;
        appVersionTv.setText(appVersion);
        profile_theme = findViewById(R.id.profile_theme);
        profile_theme.setOnClickListener(this);
        profile_theme.setVisibility(View.GONE);
        back_imageview.setOnClickListener(this);
        outside_imageview.setOnClickListener(this);
        voice_test_btn = findViewById(R.id.voice_test_btn);
        training_btn = findViewById(R.id.training_btn);
        startEarningBtn = findViewById(R.id.dummycall_btn);
        voice_test_btn.setBackgroundColor(getResources().getColor(R.color.user_status_color));
        user_profile_theme = findViewById(R.id.user_profile_theme);
        settting_layout = findViewById(R.id.settting_layout);
        settting_layout.setOnClickListener(this);
        settting_layout.setVisibility(View.GONE);
        sign_out_layout = findViewById(R.id.sign_out_layout);
        sign_out_layout.setOnClickListener(this);
        gendertypeTv = findViewById(R.id.gendertypeTv);
        gendertypeTv.setTextColor(getResources().getColor(R.color.error_message_shape_color));
        languageTv = findViewById(R.id.languateTv);
        locationTv = findViewById(R.id.locationTv);
        editDateOfBirth = findViewById(R.id.editDateOfBirth);
        editDateOfBirth.setOnClickListener(this);
        getFeedback = findViewById(R.id.getFeedback);
        getFeedback.setOnClickListener(this);
        referAndEarn = findViewById(R.id.referAndEarn);
        referAndEarn.setOnClickListener(this);
        auditScoreTv = findViewById(R.id.auditScoreTv);
        member_since = findViewById(R.id.member_since);
        faqLayout = findViewById(R.id.faqLayout);
        editGender = findViewById(R.id.editGender);
        editGender.setOnClickListener(this);
        faqLayout.setOnClickListener(this);
        slotsregister = findViewById(R.id.slotsregister);
        ll_main_slot_register = findViewById(R.id.ll_main_slot_register);
        slotsregister.setOnClickListener(this);
        if (ApplicationSettings.containsPref(AppConstants.USERINFO_GENDER)) {
            gender = ApplicationSettings.getPref(AppConstants.USERINFO_GENDER, "");
            if(gender.equals("M")) {
                iv_profile_pic.setImageResource(R.drawable.man_profile_pic);
            } else if (gender.equalsIgnoreCase("O")) {
                iv_profile_pic.setImageResource(R.drawable.others_pic);
            }else {
                iv_profile_pic.setImageResource(R.drawable.uearn_profile_pic);
            }
            if(!gender.equals("null") && !gender.isEmpty()) {
                if(gender.equalsIgnoreCase("O")) {
                    gendertypeTv.setText("OTH");
                } else {
                    gendertypeTv.setText(gender);
                }

            } else {
                gendertypeTv.setText("");
            }

            if(!gender.isEmpty()) {
                editGender.setVisibility(View.GONE);
            } else {
                editGender.setVisibility(View.VISIBLE);
            }

        } else {
            gendertypeTv.setText("");
            editGender.setVisibility(View.VISIBLE);
        }
        company_type = findViewById(R.id.company_type);
        if (ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            String company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
            company_type.setText(company);
        } else {
            company_type.setText("");
        }

        user_name = findViewById(R.id.user_name);
        user_birth_date = findViewById(R.id.user_birth_date);
        user_email_id = findViewById(R.id.user_email_id);
        phone_no = findViewById(R.id.phone_no);
        edit_email_id = findViewById(R.id.edit_email_id);
        edit_email_id.setVisibility(View.GONE);
        edit_phone_no = findViewById(R.id.edit_phone_no);
        edit_phone_no.setVisibility(View.GONE);
        edit_location = findViewById(R.id.edit_location);
        edit_location.setOnClickListener(this);
//edit_location.setVisibility(View.GONE);
        edit_language = findViewById(R.id.edit_language);
        edit_language.setOnClickListener(this);
//edit_language.setVisibility(View.GONE);
        edit_user_name = findViewById(R.id.edit_user_name);
        edit_user_name.setOnClickListener(this);
        edit_user_name.setVisibility(View.GONE);

        if (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null) {
            user_name.setText(ApplicationSettings.getPref(AppConstants.USERINFO_NAME, ""));
        }

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            user_email_id.setText(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""));
        }

        if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
            phone_no.setText(ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
        }

        if(CommonUtils.allowASF()){
            ll_main_slot_register.setVisibility(View.VISIBLE);
        } else {
            ll_main_slot_register.setVisibility(View.GONE);
        }
        if (ApplicationSettings.containsPref(AppConstants.USER_STATUS)) {
            String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
//

            switch(userStatus){
                case "Registered":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("3 steps left");
                    stepsAwayTv.setText("Your are 3 steps away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    break;
                case "":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("3 steps left");
                    stepsAwayTv.setText("Your are 3 steps away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    break;

                case "Voice Test":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("2 steps left");
                    stepsAwayTv.setText("Your are 2 steps away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    break;

                case "Voice Reject":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("3 steps left");
                    stepsAwayTv.setText("Your are 3 steps away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    break;

                case "Voice Approved":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("2 steps left");
                    stepsAwayTv.setText("Your are 2 steps away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    break;

                case "In Training":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("2 steps left");
                    stepsAwayTv.setText("Your are 2 steps away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    break;

                case "Training Reject":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("2 steps left");
                    stepsAwayTv.setText("Your are 2 steps away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    break;


                case "Re Training":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("2 steps left");
                    stepsAwayTv.setText("Your are 2 steps away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    break;

                case "Training Complete":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("1 step left");
                    stepsAwayTv.setText("Your are 1 step away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_gray_color));
                    break;


                case "Document Pending":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("1 step left");
                    stepsAwayTv.setText("Your are 1 step away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
                    break;

                case "Project Pending":
                    voiceTestStageLayout.setVisibility(View.VISIBLE);
                    stepLeftTv.setText("1 step left");
                    stepsAwayTv.setText("Your are 1 step away to start earning, so get a head start by doing it now.");
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
                    break;

                case "OJT":
                    voiceTestStageLayout.setVisibility(View.GONE);
                    stepLeftTv.setVisibility(View.GONE);
                    stepsAwayTv.setVisibility(View.GONE);
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    break;

                case "Project":
                    voiceTestStageLayout.setVisibility(View.GONE);
                    stepLeftTv.setVisibility(View.GONE);
                    stepsAwayTv.setVisibility(View.GONE);
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    break;

                case "Production":
                    voiceTestStageLayout.setVisibility(View.GONE);
                    stepLeftTv.setVisibility(View.GONE);
                    stepsAwayTv.setVisibility(View.GONE);
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    break;


                case "On Board":
                    voiceTestStageLayout.setVisibility(View.GONE);
                    stepLeftTv.setVisibility(View.GONE);
                    stepsAwayTv.setVisibility(View.GONE);
                    voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
                    break;




                default:
                    break;
//code to be executed if all cases are not matched;
            }




// if (userStatus.equalsIgnoreCase("Voice Testing")) {
// stepLeftTv.setText("3 steps left");
// stepsAwayTv.setText("Your are 3 steps away to start earning, so get a head start by doing it now.");
// voice_test_btn.setBackgroundColor(getResources().getColor(R.color.darkGrey));
// } else if (userStatus.equalsIgnoreCase("Voice Reject")) {
// stepLeftTv.setText("3 steps left");
// stepsAwayTv.setText("Your are 3 steps away to start earning, so get a head start by doing it now.");
// voice_test_btn.setBackgroundColor(getResources().getColor(R.color.user_status_color));
// } else if (userStatus.equalsIgnoreCase("Training")) {
// stepLeftTv.setText("1 steps left");
// stepsAwayTv.setText("Your are 1 steps away to start earning, so get a head start by doing it now.");
// voice_test_btn.setBackgroundColor(getResources().getColor(R.color.user_status_color));
// training_btn.setBackgroundColor(getResources().getColor(R.color.user_status_color));
// } else if(userStatus != null && !userStatus.isEmpty() && userStatus.equalsIgnoreCase("Voice Approved") || userStatus.equalsIgnoreCase("In Training")) {
// //stepLeftTv stepsAwayTv
// stepLeftTv.setText("2 steps left");
// stepsAwayTv.setText("Your are 2 steps away to start earning, so get a head start by doing it now.");
// voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
// training_btn.setBackgroundColor(getResources().getColor(R.color.user_status_color));
// startEarningBtn.setBackgroundColor(getResources().getColor(R.color.user_status_color));
// } else if(userStatus != null && !userStatus.isEmpty() && userStatus.equalsIgnoreCase("Training Complete")) {
// stepLeftTv.setText("1 steps left");
// stepsAwayTv.setText("Your are 1 steps away to start earning, so get a head start by doing it now.");
// voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
// training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
// startEarningBtn.setBackgroundColor(getResources().getColor(R.color.user_status_color));
// } else if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
// stepLeftTv.setVisibility(View.GONE);
// stepsAwayTv.setVisibility(View.GONE);
// voice_test_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
// training_btn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
// startEarningBtn.setBackgroundColor(getResources().getColor(R.color.uearn_green_cercle));
// }
        }


        if (ApplicationSettings.containsPref(AppConstants.AGENT_SPOKEN_LANG)) {
            String spokenLang = ApplicationSettings.getPref(AppConstants.AGENT_SPOKEN_LANG, "");
            if(!spokenLang.equals("") && !spokenLang.equals("null")) {
                languageTv.setText(spokenLang);
            } else {
                languageTv.setText("English");
            }
        }

        if(ApplicationSettings.containsPref(AppConstants.AGENT_DOB)) {
            String dob = ApplicationSettings.getPref(AppConstants.AGENT_DOB, "");
            if(dob != null && dob.length() > 14) {
                String dobSub = dob.substring(0, dob.length() - 14);
                user_birth_date.setText(dobSub);
            }
        }

        if(ApplicationSettings.containsPref(AppConstants.AGENT_LOCATION)) {
            String location = ApplicationSettings.getPref(AppConstants.AGENT_LOCATION, "");
            if(!location.equals("null")) {
                locationTv.setText(location);
            }
        }

    }

    private void getImage() {
        final String imgStr = ApplicationSettings.getPref(AppConstants.APP_PROFILE_URL, "");
        Log.d("PROFILE_PIC", "imgStr-345"+imgStr);
        Log.d("PROFILE_PIC", "getImage-345");
        if (imgStr != null && !imgStr.isEmpty()) {
            Log.d("PROFILE_PIC", "imgStr-348"+imgStr);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Picasso.get().load(imgStr).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.d("PROFILE_PIC", "pic-349");
                            iv_profile_pic.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            if(gender.equals("M")) {
                                iv_profile_pic.setImageResource(R.drawable.man_profile_pic);
                            } else if (gender.equalsIgnoreCase("O")) {
                                iv_profile_pic.setImageResource(R.drawable.others_pic);
                            }else {
                                iv_profile_pic.setImageResource(R.drawable.uearn_profile_pic);
                            }
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {}
                    });
                }
            });






//            SendHttpRequestTask task = new SendHttpRequestTask();
//            if(imgStr != null && !imgStr.isEmpty()){
//                task.execute(imgStr);
//            }
        } else {
            if(gender.equals("M")) {
                iv_profile_pic.setImageResource(R.drawable.man_profile_pic);
            }else if (gender.equalsIgnoreCase("O")) {
                iv_profile_pic.setImageResource(R.drawable.others_pic);
            }else {
                iv_profile_pic.setImageResource(R.drawable.uearn_profile_pic);
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void onClickToEdit(View view) {

        int id = view.getId();
        if (id == R.id.edit_user_name) {
        } else if (id == R.id.edit_email_id) {
        } else if (id == R.id.edit_phone_no) {
        } else if (id == R.id.edit_location) {
        } else if (id == R.id.edit_language) {
        }

    }

    public View getView (){
        View convertView = null;
        ViewGroup parent;
        if( convertView == null ){
            convertView = LayoutInflater.from(this).inflate(R.layout.text_inpu_password, (ViewGroup) getView(), false);
        }
        return convertView;
    }

    private void getUserFeedback() {
        Intent feedbackIntent = new Intent (this, UearnFeedbackActivity.class);
        startActivity(feedbackIntent);
    }

    private void getReferAndEarn() {
        Intent referIntent = new Intent (this, UearnReferAndEarnActivity.class);
        startActivity(referIntent);
    }


    private void getUserLocation() {
        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this);
        inputAlert.setTitle("Location");
        final EditText userInput = new EditText(this);
        userInput.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(250)
        });
        String currentLocation = locationTv.getText().toString();
        if(!currentLocation.isEmpty()) {
            userInput.setText(currentLocation);
        }
        inputAlert.setView(userInput);
        inputAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInputValue = userInput.getText().toString();
                if(userInputValue.length() > 0) {
                    isLocationChange = true;
                    String previousLocation = locationTv.getText().toString();
                    if(previousLocation.isEmpty() || !previousLocation.equals(userInputValue)) {
                        isLocationChange = true;
                    }  else {
                        isLocationChange = false;
                    }
                } else {
                    isLocationChange = false;
                }
                locationTv.setText(userInputValue);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
            }
        });
        inputAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = inputAlert.create();
        alertDialog.show();
    }

    public void changeStatusBarColor(AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back_imageview) {
            onBackPressed();
        } else if (id == R.id.edit_user_name) {
            edit_user_name.setEnabled(true);
            user_name.setEnabled(true);
            user_name.setFocusableInTouchMode(true);
            user_name.requestFocus();
            user_name.setClickable(true);
            user_name.setEnabled(true);
            user_name.setSelection(user_name.getText().length());
            String userName = String.valueOf(user_name.getText());
            isNameChange = true;
        } else if (id == R.id.outside_imageview) {
            boolean isPermissionEnabled = CommonUtils.permissionsCheck(UearnProfileActivity.this);
            if (isPermissionEnabled) {
                isProfile = true;
                isTheme = false;
                if (iv_profile_pic.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.uearn_profile_pic).getConstantState() || iv_profile_pic.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.man_profile_pic).getConstantState() || iv_profile_pic.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.others_pic).getConstantState()) {
                    startDialog("PROFILE PIC");
                } else {
                    startDialogWithRemove("PROFILE PIC");
                }
            }
        } else if (id == R.id.profile_theme) {
            isProfile = false;
            isTheme = true;
            startDialog("PROFILE THEME");
        } else if (id == R.id.edit_email_id) {
        } else if (id == R.id.edit_phone_no) {
        } else if (id == R.id.edit_location) {
            getUserLocation();
        } else if (id == R.id.editDateOfBirth) {
            getUserDateOfBirth(view.getId(), user_birth_date.getText().toString().trim());
        } else if (id == R.id.edit_language) {
            getLanguage();
        } else if (id == R.id.editGender) {
            getGender();
        } else if (id == R.id.settting_layout) {//                Intent intent = new Intent(this, ProfileSettingsActivity.class);
//                startActivity(intent);
        } else if (id == R.id.getFeedback) {
            getUserFeedback();
        } else if (id == R.id.referAndEarn) {
            getReferAndEarn();
        } else if (id == R.id.sign_out_layout) {
            signOutAndExit();
        } else if (id == R.id.faqLayout) {
            Intent intent = new Intent(this, UearnFAQActivity.class);
            startActivity(intent);
        } else if (id == R.id.slotsregister) {
            Intent intentSlots = new Intent(this, AgentSlotsActivity.class);
            intentSlots.putExtra("SLOTSREDIRECT", "UearnProfileActivity");
            startActivity(intentSlots);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        String appVersion = "v " + AppConstants.APP_VERSION;
        appVersionTv.setText(appVersion);
    }

    private void getUserDateOfBirth(int callerId, String dateText) {
        this.callerId = callerId;
        Date date = null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateInString = dateText;
        Date previousBirthDate = null;

        try {

            previousBirthDate = formatter.parse(dateInString);
            System.out.println(previousBirthDate);
            System.out.println(formatter.format(previousBirthDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }



        try {
            if (dateText.equals(""))
                date = new Date();
            else
                date = previousBirthDate;

        } catch (Exception exp) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        DatePickerDialog datePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Date date = new GregorianCalendar(year, month, day).getTime();
                String formatedDate = dateFormatter.format(date);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                agentBirthDate = formatter.format(date);
                isBirthDateChange = true;
                user_birth_date.setText(agentBirthDate);
            }
        }, year, month, day);
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "OK", datePicker);
        datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        datePicker.show();
    }




    private void updateUserDetails() {
        //CommonUtils.permissionsCheck(UearnProfileActivity.this);
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            final JSONObject userDetailObj = new JSONObject();
            JSONArray userInfoArr = new JSONArray();

            try {

                if(isGenderSelected) {
                    String genderType = String.valueOf(gendertypeTv.getText());
                    userDetailObj.put("gender", genderType);
                }

                if(isLocationChange) {
                    String location = String.valueOf(locationTv.getText());
                    userDetailObj.put("locality", location);
                }

                if(isLanguageChange) {
                    String language = String.valueOf(languageTv.getText());
                    userDetailObj.put("lang", language);
                }

                if(isBirthDateChange) {
                    userDetailObj.put("dob", agentBirthDate);
                }

                Log.d("userDetailObj:--", userDetailObj.toString());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            new AsyncTask<Void, Void, JSONObject>() {
                ProgressDialog progressBar;

                @Override
                protected void onPreExecute() {
                    //progressBar = ProgressDialog.show(LiveMeetingActivity.this, "", "Sending...");
                    super.onPreExecute();
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    JSONObject jsonObject = null;
                    String user_id = "";
                    if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                        user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    }
                    String url = Urls.getUpdateUserDetailsUrl(user_id);
                    //Log.d("UearnVoiceRecordReview: " + url + ");
                    Log.d("UearnVoiceRecordReview", "URL" + url);


                    //JSONObject response = DataUploadUtils.postEmailContactInfo(Urls.getNewSingleCallPostUrl(), afterCallDetailsjson);

                    JSONObject rsponseObj = DataUploadUtils.putJsonData(url, userDetailObj);
                    JSONObject successObject = null;
                    //JSONObject responseAudio = DataUploadUtils.uploadNotesAudioFileToServer(audioPath, "live_meeting_rec", "", "", url);
                    try {

                        if (rsponseObj != null && rsponseObj.has("success")) {
                            successObject = rsponseObj.getJSONObject("success");
                            String dob = successObject.optString("dob");
                            String lang = successObject.optString("lang");
                            String location = successObject.optString("locality");
                            String gender = successObject.optString("gender");
                            ApplicationSettings.putPref(AppConstants.AGENT_DOB, dob);
                            ApplicationSettings.putPref(AppConstants.AGENT_LOCATION, location);
                            ApplicationSettings.putPref(AppConstants.AGENT_SPOKEN_LANG, lang);
                            ApplicationSettings.putPref(AppConstants.USERINFO_GENDER, gender);
                        }

                    } catch (JSONException e) {
                        //Log.e("LiveMeating", "JSON Parsing error on upload response");
                    }
                    return jsonObject;
                }

                @Override
                protected void onPostExecute(JSONObject jObject) {
//                        if (progressBar.isShowing()) {
//                            progressBar.dismiss();
//                        }
                    super.onPostExecute(jObject);
                }
            }.execute();
        } else {
            ApplicationSettings.putPref(AppConstants.FROM_CMAIL, false);
            ApplicationSettings.putPref(AppConstants.DRAFT_LIVEMEETING, false);
        }


    }

    private void startDialog(String imgType) {
        final Dialog networkDialog = CustomTwoButtonDialog.capturePhotoType(this, "Upload Pictures Option", "How do you want to set your picture?");

        TextView btnNo = networkDialog.findViewById(R.id.btn_no);
        btnNo.setText("CAMERA");

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ContentValues values2 = new ContentValues(4);
                values2.put(MediaStore.Images.Media.DISPLAY_NAME, Calendar.getInstance().toString());
                values2.put(MediaStore.Images.Media.DESCRIPTION, "this is description");
                values2.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                try {
                    imageFilePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values2);

                    //uploadImageFile(imageFilePath.toString());
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
                    if (isProfile) {
                        startActivityForResult(intent2, PICK_Camera_IMAGE);
                    } else {
                        startActivityForResult(intent2, PICK_Profile_IMAGE);
                    }
                } catch (SecurityException se) {
                    CommonUtils.displayDialog(UearnProfileActivity.this);
                }

            }
        });

        TextView btnYes = networkDialog.findViewById(R.id.btn_yes);
        btnYes.setText("GALLERY");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                Intent pictureActionIntent = null;

                pictureActionIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(
                        pictureActionIntent,
                        GALLERY_PICTURE);


            }
        });
        networkDialog.show();
    }


    private void startDialogWithRemove(String imgType) {
        final Dialog networkDialog = CustomTwoButtonDialog.capturePhotoTypeWithRemove(this, "Upload Pictures Option", "How do you want to set your picture?");

        TextView btnCamera = networkDialog.findViewById(R.id.btn_camera);
        btnCamera.setText("CAMERA");

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ContentValues values2 = new ContentValues(4);
                values2.put(MediaStore.Images.Media.DISPLAY_NAME, Calendar.getInstance().toString());
                values2.put(MediaStore.Images.Media.DESCRIPTION, "this is description");
                values2.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                try {
                    imageFilePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values2);

                    //uploadImageFile(imageFilePath.toString());
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
                    if (isProfile) {
                        startActivityForResult(intent2, PICK_Camera_IMAGE);
                    } else {
                        startActivityForResult(intent2, PICK_Profile_IMAGE);
                    }
                } catch (SecurityException se) {
                    CommonUtils.displayDialog(UearnProfileActivity.this);
                }

            }
        });

        TextView btnGallery = networkDialog.findViewById(R.id.btn_gallery);
        btnGallery.setText("GALLERY");

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                Intent pictureActionIntent = null;

                pictureActionIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(
                        pictureActionIntent,
                        GALLERY_PICTURE);


            }
        });

        TextView btnRemovePhoto = networkDialog.findViewById(R.id.btn_removephoto);
        btnRemovePhoto.setText("REMOVE PHOTO");

        btnRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                if(gender.equals("M")) {
                    iv_profile_pic.setImageResource(R.drawable.man_profile_pic);
                } else if (gender.equalsIgnoreCase("O")) {
                    iv_profile_pic.setImageResource(R.drawable.others_pic);
                }else {
                    iv_profile_pic.setImageResource(R.drawable.uearn_profile_pic);
                }

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            sendRemoveProfilePicRequestToServer();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        networkDialog.show();
    }

    private void sendRemoveProfilePicRequestToServer() {
        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

        JSONObject profilejson = new JSONObject();
        try {
            profilejson.put("profile_pic_url", "");
            JSONObject uploadResponse = DataUploadUtils.putJsonData(Urls.getUploadImageUrl(userId), profilejson);
            try {
                if (uploadResponse != null && uploadResponse.has("success")) {
                    JSONObject successObj = uploadResponse.getJSONObject("success");
                    String profilePic = successObj.optString("profile_pic_url");
                    ApplicationSettings.putPref(AppConstants.APP_PROFILE_URL, profilePic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendRemoteDialStopRequest(String message) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.GetRemoteDialerStop(message, 0, this, "Stopping. Please wait..", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    String text = "";
                    try {
                        SmarterSMBApplication.isRemoteDialledStart = false;
                        SmarterSMBApplication.isRemoteDialledStopRequest = true;

                        if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
                            boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
                            if (systemControl) {
                                SmarterSMBApplication.disableStatusBarAndNavigation = false;
                            }
                        }

                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.has("text")) {
                            text = jsonObject.getString("text");
                        }
                    } catch (Exception e) {

                    }

                    if (text != null && !text.isEmpty()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, text);
                    }
                }
            }).call();
        }  else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendSalesEntriesInfoToServer() {
        String salesStatusFromDb = getAllSalesStatusEntriesFromDB();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")) {
                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
            }
            jsonObject.put("salesstatusentries", salesStatusFromDb);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            DataUploadUtils.postSalesEntries(Urls.postSalesEntriesUrl(), jsonObject);
        }
    }

    public String getAllSalesStatusEntriesFromDB() {
        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM SmarterContact", null);

        if (cursor != null && cursor.getCount() > 0) {
            JSONArray resultSet = new JSONArray();
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();

                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null) {
                        try {
                            if (cursor.getString(i) != null) {
                                rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }
            cursor.close();
            if (db != null && db.isOpen()) {
                db.close();
            }
            return resultSet.toString();
        } else {
            return "";
        }
    }

    private void signOutAndExit() {
        boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
        if(truePredictive) {
            SmarterSMBApplication.triggerDataLoadingAfterSubmit = false;
        }
        SmarterSMBApplication.moveToRAD = false;
        String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
            SmarterSMBApplication.signoutNotificationReceived = false;
            File dir = new File(Environment.getExternalStorageDirectory() + "/callrecorder/");
            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (ApplicationSettings.containsPref(AppConstants.SSU_ON_SIO)) {
                boolean ssuonsio = ApplicationSettings.getPref(AppConstants.SSU_ON_SIO, false);
                if (ssuonsio) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sendSalesEntriesInfoToServer();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            }

            Cursor cursor = null;
            if (ApplicationSettings.containsPref(AppConstants.UPLOAD_STATUS2)) {
                boolean uploadStatus2 = ApplicationSettings.getPref(AppConstants.UPLOAD_STATUS2, false);
                if (!uploadStatus2) {
                    cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=0", null);
                } else {
                    cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=2", null);
                }
            } else {
                cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=2", null);
            }

            if (cursor != null && cursor.getCount() > 0) {
                callReuploadService();
                checkInternalFiles();
            } else {
                String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                    SmarterSMBApplication.isRemoteDialledStart = false;
                    SmarterSMBApplication.isFirstCall = false;

                    if (ApplicationSettings.containsPref(AppConstants.SYSTEM_CONTROL)) {
                        boolean systemControl = ApplicationSettings.getPref(AppConstants.SYSTEM_CONTROL, false);
                        if (systemControl) {
                            SmarterSMBApplication.disableStatusBarAndNavigation = false;
                        }
                    }

                    //Log.d("RemoteDialledStart","1655    " +SmarterSMBApplication.isRemoteDialledStart);
                    sendRemoteDialStopRequest("Taking a break");
                    if(truePredictive) {
                        //killCall(UearnProfileActivity.this);
                    }
                    new CustomTwoButtonDialog().exitApplicationDialog(UearnProfileActivity.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
                } else {
                    String selection = "UPLOAD_STATUS" + "=" + 0;
                    cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
                    if (dir.isDirectory()) {
                        String[] children = dir.list();
                        if ((children != null && children.length > 0) || (cursor != null && cursor.getCount() > 0)) {
                            callReuploadService();
                            checkInternalFiles();
                        } else {
                            new CustomTwoButtonDialog().exitApplicationDialog(UearnProfileActivity.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
                        }
                    } else {
                        new CustomTwoButtonDialog().exitApplicationDialog(UearnProfileActivity.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
                    }
                }
            }
        } else {
            new CustomTwoButtonDialog().exitApplicationDialog(UearnProfileActivity.this, "Sign out", "You will not get project specific updates any more. Are you sure you want to logout?").show();
        }
    }

    public void killCall(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
            methodGetITelephony.setAccessible(true);
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
            Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void callReuploadService() {
        Intent aIntent = new Intent(this, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(aIntent);
        } else {
            this.startService(aIntent);
        }
    }

    private void getGender() {
        final String[] singleChoiceItems = getResources().getStringArray(R.array.gender_array);
        final int itemSelected = -1;
        new AlertDialog.Builder(this)
                .setTitle("Select your gender")
                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                        String selectedGender = singleChoiceItems[selectedIndex];
                        String gender = "";
                        if(selectedGender.equals("Male")) {
                            gender = "M";
                        } else if(selectedGender.equals("Female")) {
                            gender = "F";
                        } else {
                            gender = "OTH";
                        }
                        String previousGender = gendertypeTv.getText().toString();
                        if(previousGender.isEmpty()) {
                            gendertypeTv.setText(gender);
                            isGenderSelected = true;
                        } else if (!previousGender.equals(gender)) {
                            gendertypeTv.setText(gender);
                            isGenderSelected = true;
                        } else {
                            gendertypeTv.setText(gender);
                            isGenderSelected = false;
                        }
                    }
                })
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gendertypeTv.setText("");
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void getLanguage() {
        final boolean[] Selectedtruefalse1 = new boolean[]{
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        };

        String selectedLanguage = languageTv.getText().toString();

        String[] elements = selectedLanguage.split(",");


        boolean[] array = new boolean[AlertDialogItems.length];

        if(elements != null && elements.length > 0) {

            for (int i = 0; i < elements.length; i++) {

                Log.d("Language:-", elements[i]);
                List<String> abcd = Arrays.asList(AlertDialogItems);
                int in = abcd.indexOf(elements[i]);

                if (elements[i].contains("English")) {
                    Selectedtruefalse1[0] = true;
                } else if (elements[i].contains("Hindi")) {
                    Selectedtruefalse1[1] = true;

                } else if (elements[i].contains("Kannada")) {
                    Selectedtruefalse1[2] = true;

                } else if (elements[i].contains("Telugu")) {
                    Selectedtruefalse1[3] = true;

                } else if (elements[i].contains("Tamil")) {
                    Selectedtruefalse1[4] = true;

                } else if (elements[i].contains("Marathi")) {
                    Selectedtruefalse1[5] = true;

                } else if (elements[i].contains("Bengali")) {
                    Selectedtruefalse1[6] = true;

                } else if (elements[i].contains("Gujarati")) {
                    Selectedtruefalse1[7] = true;

                }

            }
        }

        if(!selectedLanguage.isEmpty()) {
            ArrayList<String> myList = new ArrayList<String>(Arrays.asList(selectedLanguage.substring(1, selectedLanguage.length() - 0).replaceAll("\"", "").split(",")));
        }

        alertdialogbuilder = new AlertDialog.Builder(this);
        ItemsIntoList = Arrays.asList(AlertDialogItems);
        alertdialogbuilder.setMultiChoiceItems(AlertDialogItems, Selectedtruefalse1, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            }
        });


        alertdialogbuilder.setCancelable(false);
        alertdialogbuilder.setTitle("Languages");
        final String previousLang = languageTv.getText().toString();
        alertdialogbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertdialogbuilder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(languageTv.getText().toString() == null || languageTv.getText().toString().isEmpty()) {
                    languageTv.setText("English");
                }
            }
        });

        final AlertDialog dialog = alertdialogbuilder.create();
        dialog.show();

        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean wantToCloseDialog = false;
                languageTv.setText("");
                int a = 0;
                while(a < Selectedtruefalse1.length)
                {
                    boolean value = Selectedtruefalse1[a];
                    if(value){
                        if(languageTv.getText().length() > 0) {
                            isLanguageChange = true;
                            languageTv.setText(languageTv.getText() + ", " + ItemsIntoList.get(a));
                            String currentLang = languageTv.getText() + ", " + ItemsIntoList.get(a);
                            if(previousLang.isEmpty() || !previousLang.equals(currentLang)) {
                                isLanguageChange = true;
                            }  else {
                                isLanguageChange = false;
                            }

                        } else {
                            isLanguageChange = true;
                            languageTv.setText(languageTv.getText()  + ItemsIntoList.get(a));
                        }
                    }
                    a++;
                }

                if(languageTv.getText().toString() == null || languageTv.getText().toString().isEmpty()){
                    Toast.makeText(SmarterSMBApplication.currentActivity, "Please select atleast one language", Toast.LENGTH_SHORT).show();
                    wantToCloseDialog = false;
                } else {
                    wantToCloseDialog = true;
                }

                if(wantToCloseDialog)
                    dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }


    private void checkInternalFiles() {
        final Dialog dialog = CustomTwoButtonDialog.buildTwoButtonDialog(this, "Warning", "There is data to be uploaded, please don't sign out. Connect to internet & sync before you sign out.");
        TextView btnNo = dialog.findViewById(R.id.btn_no);
        btnNo.setText("OK");
        btnNo.setTextSize(10);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        TextView btnYes = dialog.findViewById(R.id.btn_yes);
        btnYes.setText("FORCE LOGOUT");
        btnYes.setTextSize(10);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new CustomTwoButtonDialog().exitApplicationDialog(UearnProfileActivity.this, "Sign out", "Your business activities will not be tracked any more. Are you sure you want to log out?").show();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                sendBroadcast(broadcastIntent);
            }
        });
        dialog.show();
    }

    public void uploadImageFile(String imgFile) {
        if (CommonUtils.isNetworkAvailable(this)) {
            String filePath = imgFile;
            JSONObject response = DataUploadUtils.uploadImageFileToServer(filePath, Urls.getUploadFileUrl());
            try {
                if (response != null && response.getString("url") != null) {
                    String imageUrl = response.getString("url");
                    if (isProfile) {
                        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

                        JSONObject profilejson = new JSONObject();
                        profilejson.put("profile_pic_url", imageUrl);
                        JSONObject uploadResponse = DataUploadUtils.putJsonData(Urls.getUploadImageUrl(userId), profilejson);
                        try {
                            if (uploadResponse.has("success")) {
                                JSONObject successObj = uploadResponse.getJSONObject("success");
                                String profilePic = successObj.optString("profile_pic_url");
                                ApplicationSettings.putPref(AppConstants.APP_PROFILE_URL, profilePic);
                                Glide.with(this)
                                        .load(profilePic)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(iv_profile_pic);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

                        JSONObject profilejson = new JSONObject();
                        profilejson.put("profile_pic_url", imageUrl);
                        Log.d("profilejson:-", profilejson.toString());
                        JSONObject uploadResponse = DataUploadUtils.putJsonData(Urls.getUploadImageUrl(userId), profilejson);

                        try {
                            if (uploadResponse != null && uploadResponse.getString("url") != null) {
                                Log.d("UploadResponse:-", uploadResponse.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No network available or poor network", Toast.LENGTH_SHORT).show();
                            iv_profile_pic.setImageResource(R.drawable.uearn_profile_pic);
                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    class UploadBitmap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            uploadImageFile(imagePath);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGO_FETCH_REQUEST && resultCode == RESULT_OK) {
            if (CommonUtils.isNetworkAvailable(this)) {
                if (CommonUtils.imageValidation(this, data)) {
                    logoUri = data.getData();
                    CommonUtils.fillImageView(this, logoUri, iv_profile_pic);
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_Camera_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            if (CommonUtils.isNetworkAvailable(this)) {
                if (data != null) {
                    logoUri = data.getData();
                    ThumbImage = null;
                    if (logoUri != null) {
                        previewImage(logoUri, "Profile");
                    } else {
                        Toast.makeText(this, "Error retriving image! inside", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (imageFilePath != null) {
                        ThumbImage = null;
                        logoUri = imageFilePath;
                        previewImage(logoUri, "Profile");
                        Uri profileUri = imageFilePath;
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(profileUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imgDecodableString = cursor.getString(columnIndex);
                        imagePath = imgDecodableString;
                        cursor.close();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore
                                    .Images
                                    .Media
                                    .getBitmap(getApplicationContext().getContentResolver(), imageFilePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String profileUserID = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                        String profileUrl = profileUserID + "-image.png";

                        imagePath = Utils.getPath(profileUri, this);
                        int lastIndex = imagePath.lastIndexOf("/") + 1;
                        imagePath = imagePath.replace(
                                imagePath.substring(lastIndex, imagePath.length())
                                , profileUrl);
                        try {
                            FileOutputStream out = new FileOutputStream(imagePath);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 667, false);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new UploadBitmap().execute();
                        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor edit = shre.edit();
                        edit.putString("USER_THEAME", String.valueOf(imageFilePath));
                    } else {
                        Toast.makeText(this, "Error retriving image!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_Profile_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {

            if (CommonUtils.isNetworkAvailable(this)) {
                File file = new File(getRealPathFromURI(imageFilePath));
                if (file.exists()) {
                    //imageFilePath = file.getAbsolutePath();
                    Drawable user_theame_img = Drawable.createFromPath(file.getAbsolutePath());
                    user_profile_theme.setBackground(user_theame_img);
                    SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor edit = shre.edit();
                    edit.putString("USER_THEAME", String.valueOf(user_theame_img));
                    // ApplicationSettings.putPref("PROFILE_PIC", String.valueOf(user_theame_img));
                    edit.commit();

                }


                Uri profileUri = imageFilePath;
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(profileUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                imagePath = imgDecodableString;
                cursor.close();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(getApplicationContext().getContentResolver(), imageFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String profileUserID = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                String profileUrl = profileUserID + "-image.png";

                imagePath = Utils.getPath(profileUri, this);
                int lastIndex = imagePath.lastIndexOf("/") + 1;
                imagePath = imagePath.replace(
                        imagePath.substring(lastIndex, imagePath.length())
                        , profileUrl);
                try {
                    FileOutputStream out = new FileOutputStream(imagePath);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 667, false);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                new UploadBitmap().execute();


                //previewImage(logoUri, "Theame");
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_PICTURE && resultCode == AppCompatActivity.RESULT_OK) {
            if (CommonUtils.isNetworkAvailable(this)) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Drawable user_theame_img = Drawable.createFromPath(picturePath);
                    if (isTheme) {
                        user_profile_theme.setBackground(user_theame_img);
                    } else {
                        iv_profile_pic.setImageDrawable(user_theame_img);
                    }

                    Uri selectedImages = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImages, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndexs = c.getColumnIndex(filePath[0]);
                    selectedImagePath = c.getString(columnIndexs);
                    c.close();
                    if (selectedImagePath != null) {
                        //txt_image_path.setText(selectedImagePath);
                    }
                    bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                    // preview image
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
                    try {
                        FileOutputStream out = new FileOutputStream(selectedImagePath);
                        imagePath = selectedImagePath;
                        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 667, false);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new UploadBitmap().execute();
                    iv_profile_pic.setImageBitmap(bitmap);

                } else {
                    Toast.makeText(getApplicationContext(), "Cancelled",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private class uploadMedia extends AsyncTask<Void, Void, Void> {
        ProgressDialog waitDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                waitDialog = new ProgressDialog(UearnProfileActivity.this);
                waitDialog.setMessage("Uploading Media Content..");
                waitDialog.setCancelable(false);
                if (waitDialog != null) {
                    waitDialog.show();
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (CommonUtils.isNetworkAvailable(UearnProfileActivity.this)) {
                if (logoUri != null) {
                    if (uploadurl != null) {
                        String filePath = Utils.getPath(logoUri, UearnProfileActivity.this);
                        File file = new File(filePath);
                        String fileName = file.getName();
                        try {
                            AttachmentList attachments = new AttachmentList(fileName, uploadurl);
                            //attachmentsLists.add(attachments);
                        } catch (Exception e) {
                            //Log.e("IO Exception", e.toString());
                        }
                    }
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (waitDialog != null) {
                waitDialog.dismiss();
            }
            super.onPostExecute(aVoid);
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_in_top);
    }


    public void previewImage(Uri uri, String imgType) {
        String path = Utils.getPath(uri, UearnProfileActivity.this);

        if (ThumbImage == null) {
            if (path != null) {
                ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 96, 96);
            } else {
                //Log.e("Tag", "path is null" + " uri= " + uri);
            }
        }
        if (imgType.equals("Profile")) {
            iv_profile_pic.setImageBitmap(ThumbImage);
        }
    }


    @Override
    public void onBackPressed() {
        if(isLocationChange || isLanguageChange || isBirthDateChange || isGenderSelected) {
            updateUserDetails();
        }
        SmarterSMBApplication.navigateFromUearnProfile = true;
        String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
            Intent intent = new Intent(this, UearnHome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            overridePendingTransition(0, 0);
            startActivityForResult(intent, 0);
            this.finish();
        }
        isLanguageChange = false;
        isLocationChange = false;
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


