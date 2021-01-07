package smarter.uearn.money.activities.profilesettings;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DialogTitle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.UearnProfileActivity;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.models.Address;
import smarter.uearn.money.models.KycDocuments;
import smarter.uearn.money.models.LanguageModel;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.LogUtils;
import smarter.uearn.money.utils.ResizeBitmapUtil;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;

import static smarter.uearn.money.utils.Utils.isPanCardValid;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgProfile, imgEditPhoto, navHome;
    TextView tvProfileBio, tvDOB, tvEmail, tvGender, tvPhoneNum, tvPassword, tvLocation, tvLanguage, tvCurrentProject;
    private Boolean isNameChange = false, isLanguageChange = false, isLocationChange = false, isBirthDateChange = false, isGenderSelected = false;
    private TextView tvProfileName;
    private LinearLayout lyDOB, lyGender, lyLanguage, lyLocation;
    private Address userAddress = null;
    private ImageView img_Edit_UserName, img_Edit_DOB, img_Edit_Gender, img_Edit_Email, img_Edit_Phone, img_Edit_Language, img_Edit_Location, img_Edit_CurrentProject;
    private TextView img_Text_DOB, img_Text_Gender, img_Text_Language, img_Text_Location;
    private View ul_Location, ul_Language, ul_DOB, ul_Gender;

    Button btnEditProfile, btnDone;
    TextView tvPageLabel;
    Bitmap bitmap;
    private Bitmap ThumbImage;
    String gender = "";
    private boolean isProfile, isTheme = false;
    private Uri imageFilePath, logoUri;
    protected static final int PICK_Camera_IMAGE = 0;
    protected static final int PICK_Profile_IMAGE = 2;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 3;
    private String agentBirthDate = "";
    private int LOGO_FETCH_REQUEST = 1;
    private String uploadurl, imagePath;
    String selectedImagePath;
    private List<LanguageModel> languageModel = new ArrayList<>();
    JSONObject locationDataObj = null;

    String[] AlertDialogItems = new String[]{
            "English", "Hindi", "Kannada", "Telugu", "Tamil", "Marathi", "Bengali", "Gujarati"
    };
    AlertDialog.Builder alertdialogbuilder;
    List<String> ItemsIntoList;
    DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
    int callerId = -1;
    public static final String DATE_FORMAT = "EEE, MMM d, yyyy";

    private Context ctx = this;
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

    String[] languages = null;
    boolean[] checkedLanguageItems = null;
    private ArrayList<Integer> mSelectedLanguageItems = null;
    private ArrayList<String> selectedLanguages = null;
    HashMap<String, View> languageMap = new HashMap<>();
    AlertDialog alert;
    String genderType = "";
    private TextView auditScoreTv;


    //========  NEW PARAM FOR CAPUTRE IMAGE ===============

    ImageView imageView;
    Button button;
    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;

    String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME = "UEARN";

    //===============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        SmarterSMBApplication.currentActivity = this;
        CommonUtils.permissionsCheck(ProfileActivity.this);
        mSelectedLanguageItems = new ArrayList<Integer>();
        selectedLanguages = new ArrayList<String>();
    }

    @Override
    protected void onResume() {
        updateProfileData();
        SmarterSMBApplication.setCurrentActivity(this);
        super.onResume();
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    private void initView() {

        navHome = findViewById(R.id.ivNavMenu);
        navHome.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        DrawableCompat.setTint(navHome.getDrawable(), ContextCompat.getColor(this.getApplicationContext(), R.color.home_appcolor_purple));

        tvPageLabel = findViewById(R.id.tvPageLabel);
        imgProfile = findViewById(R.id.imgProfile);
        imgEditPhoto = findViewById(R.id.imgEditPhoto);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnDone = findViewById(R.id.btnDone);
        tvProfileBio = findViewById(R.id.tvProfileBio);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvDOB = findViewById(R.id.tvDOB);
        tvGender = findViewById(R.id.tvGender);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNum = findViewById(R.id.tvPhoneNum);
        tvPassword = findViewById(R.id.tvPassword);
        tvLocation = findViewById(R.id.tvLocation);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvCurrentProject = findViewById(R.id.tvCurrentProject);


        img_Edit_DOB = findViewById(R.id.img_Edit_DOB);
        img_Edit_Gender = findViewById(R.id.img_Edit_Gender);
        img_Edit_Language = findViewById(R.id.img_Edit_Language);
        img_Edit_Location = findViewById(R.id.img_Edit_Location);

        img_Text_DOB = findViewById(R.id.img_Text_DOB);
        img_Text_Gender = findViewById(R.id.img_Text_Gender);
        img_Text_Language = findViewById(R.id.img_Text_Language);
        img_Text_Location = findViewById(R.id.img_Text_Location);


        ul_Location = findViewById(R.id.ul_Location);
        ul_Language = findViewById(R.id.ul_Language);
        ul_DOB = findViewById(R.id.ul_DOB);
        ul_Gender = findViewById(R.id.ul_Gender);


        img_Edit_DOB.setOnClickListener(this);
        img_Edit_Gender.setOnClickListener(this);
        img_Edit_Language.setOnClickListener(this);
        img_Edit_Location.setOnClickListener(this);

        img_Text_DOB.setOnClickListener(this);
        img_Text_Gender.setOnClickListener(this);
        img_Text_Language.setOnClickListener(this);
        img_Text_Location.setOnClickListener(this);

        img_Text_DOB.setClickable(false);
        img_Text_Gender.setClickable(false);
        img_Text_Language.setClickable(false);
        img_Text_Location.setClickable(false);

        navHome.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        imgEditPhoto.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        tvPageLabel.setText("Profile Settings");

        lyDOB = findViewById(R.id.lyDOB);
        lyGender = findViewById(R.id.lyGender);
        lyLanguage = findViewById(R.id.lyLanguage);
        lyLocation = findViewById(R.id.lyLocation);
        auditScoreTv = findViewById(R.id.auditScoreTv);

        lyDOB.setOnClickListener(this);
        lyLanguage.setOnClickListener(this);
        lyLocation.setOnClickListener(this);
        lyGender.setOnClickListener(this);


        languages = getLanguages();
        updateProfileData();
        getImage();
        setEditVisible(false);
        String user_status = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        if (user_status.equalsIgnoreCase("On Board")) {
            String auditScore = ApplicationSettings.getPref(AppConstants.USER_AUDIT_SCORE, "");
            auditScoreTv.setVisibility(View.VISIBLE);
            auditScoreTv.setText(auditScore);
        } else {
            auditScoreTv.setVisibility(View.GONE);
        }
    }

    private void setEditVisible(boolean isVisible) {
        if (isVisible) {
            // lyDOB,lyGender,lyLanguage,lyLocation;
            lyDOB.setClickable(true);
            lyGender.setClickable(true);
            lyLanguage.setClickable(true);
            lyLocation.setClickable(true);

            img_Text_DOB.setClickable(true);
            img_Text_Gender.setClickable(true);
            img_Text_Language.setClickable(true);
            img_Text_Location.setClickable(true);

            img_Edit_DOB.setVisibility(View.VISIBLE);
            img_Edit_Gender.setVisibility(View.VISIBLE);
            img_Edit_Language.setVisibility(View.VISIBLE);
            img_Edit_Location.setVisibility(View.VISIBLE);

            if (!Utils.isStringEmpty(tvLocation.getText().toString())){
                ul_Location.setVisibility(View.VISIBLE);
            }else{
                ul_Location.setVisibility(View.GONE);
            }

            ul_Language.setVisibility(View.VISIBLE);
            ul_DOB.setVisibility(View.VISIBLE);
            ul_Gender.setVisibility(View.VISIBLE);

            btnDone.setVisibility(View.VISIBLE);
            btnEditProfile.setVisibility(View.GONE);

        } else {

            lyDOB.setClickable(false);
            lyGender.setClickable(false);
            lyLanguage.setClickable(false);
            lyLocation.setClickable(false);

            img_Text_DOB.setClickable(false);
            img_Text_Gender.setClickable(false);
            img_Text_Language.setClickable(false);
            img_Text_Location.setClickable(false);

            img_Edit_DOB.setVisibility(View.GONE);
            img_Edit_Gender.setVisibility(View.GONE);
            img_Edit_Language.setVisibility(View.GONE);
            img_Edit_Location.setVisibility(View.GONE);


            ul_Location.setVisibility(View.GONE);
            ul_Language.setVisibility(View.GONE);
            ul_DOB.setVisibility(View.GONE);
            ul_Gender.setVisibility(View.GONE);


            btnDone.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.VISIBLE);
        }
    }

    private void updateProfileData() {

        try {
            if (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null) {
                tvProfileName.setText(ApplicationSettings.getPref(AppConstants.USERINFO_NAME, ""));
            }
            if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                tvEmail.setText(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""));
            }
            if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                tvPhoneNum.setText(ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
            }
            if (ApplicationSettings.containsPref(AppConstants.AGENT_SPOKEN_LANG)) {
                String spokenLang = ApplicationSettings.getPref(AppConstants.AGENT_SPOKEN_LANG, "");
                if (!spokenLang.equals("") && !spokenLang.equals("null")) {
                    tvLanguage.setText(spokenLang);
                } else {
                    tvLanguage.setText("English");
                }
                updateLanguages(spokenLang, languages);
            }

            if (ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
                String company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");

                tvCurrentProject.setText(company);
            } else {
                tvCurrentProject.setText("");
            }

            if (ApplicationSettings.containsPref(AppConstants.AGENT_LOCATION)) {
                String address = ApplicationSettings.getPref(AppConstants.AGENT_LOCATION, "");
                if (address != null && !Utils.isStringEmpty(address)&& address.startsWith("{")) {
                    try {
                        userAddress = new Address();
                        JSONObject addressJsonObject = new JSONObject(address);
                        if (addressJsonObject.has("address")) {
                            String addressStr = addressJsonObject.getString("address");
                            userAddress.setAddress(addressStr);
                        }
                        if (addressJsonObject.has("city")) {
                            String addressStr = addressJsonObject.getString("city");
                            userAddress.setCity(addressStr);
                        }
                        if (addressJsonObject.has("state")) {
                            String addressStr = addressJsonObject.getString("state");
                            userAddress.setState(addressStr);
                        }
                        if (addressJsonObject.has("country")) {
                            String addressStr = addressJsonObject.getString("country");
                            userAddress.setCountry(addressStr);
                        }
                        if (addressJsonObject.has("pincode")) {
                            String addressStr = addressJsonObject.getString("pincode");
                            userAddress.setPincode(addressStr);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    String userAddressLine = "";
                    if (!Utils.isStringEmpty(userAddress.getAddress())) {
                        userAddressLine = userAddress.getAddress().trim();
                    }
                    if (!Utils.isStringEmpty(userAddress.getCity())) {
                        userAddressLine = userAddressLine + ", " + userAddress.getCity().trim();
                    }
                    if (!Utils.isStringEmpty(userAddress.getState())) {
                        userAddressLine = userAddressLine + ", " + userAddress.getState().trim();
                    }
                    if (!Utils.isStringEmpty(userAddress.getCountry())) {
                        userAddressLine = userAddressLine + ", " + userAddress.getCountry().trim();
                    }
                    if (!Utils.isStringEmpty(userAddress.getPincode())) {
                        userAddressLine = userAddressLine + "-" + userAddress.getPincode().trim();
                    }

                    tvLocation.setText(userAddressLine);
                    tvLocation.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setText("");
                    tvLocation.setVisibility(View.GONE);
                    ul_Location.setVisibility(View.GONE);
                }
            }

            if (ApplicationSettings.containsPref(AppConstants.AGENT_DOB)) {
                String dob = ApplicationSettings.getPref(AppConstants.AGENT_DOB, "");
                if (dob != null && dob.length() > 14) {
                    String dobSub = dob.substring(0, dob.length() - 14);
                    tvDOB.setText(dobSub);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String userCreated = ApplicationSettings.getPref(AppConstants.USER_CREATED, "");
            //auditScoreTv
            String user_status = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
            if (user_status != null && !user_status.isEmpty() && user_status.equalsIgnoreCase("On Board")) {
                String auditScore = ApplicationSettings.getPref(AppConstants.USER_AUDIT_SCORE, "");
                auditScoreTv.setVisibility(View.VISIBLE);
                auditScoreTv.setText(auditScore);
            } else {
                auditScoreTv.setVisibility(View.GONE);
            }

            if (userCreated != null && !userCreated.isEmpty()) {
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
                String memberSince = "Member since " + monthStr + " " + year + "";
                tvProfileBio.setText(memberSince); // - Sujit to fix this error in IDE
            }

            if (ApplicationSettings.containsPref(AppConstants.USERINFO_GENDER)) {
                gender = ApplicationSettings.getPref(AppConstants.USERINFO_GENDER, "");
                if (!gender.equals("null") && !gender.isEmpty()) {
                    if (gender.equalsIgnoreCase("O")) {
                        tvGender.setText("Not to disclose");
                    } else if (gender.equalsIgnoreCase("M")) {
                        tvGender.setText("Male");
                    } else {
                        tvGender.setText("Female");
                    }
                } else {
                    tvGender.setText("");
                }
            } else {
                tvGender.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLanguages(String spokenLang, String[] languages) {

        String[] elements = spokenLang.split(",");

        if (languageModel.size() > 0) {
            languageModel.clear();
        }

        if (elements != null && elements.length > 0) {
            for (int i = 0; i < elements.length; i++) {
                LanguageModel lanModel = new LanguageModel();
                elements[i] = elements[i].trim().replaceAll("\\s+", " ");
                lanModel.setLanguage(elements[i]);
                lanModel.setSelected(true);
                languageModel.add(lanModel);
            }
        }

        HashSet<String> set = new HashSet<String>();
        if (languages != null && languages.length > 0) {
            for (int i = 0; i < languages.length; i++) {
                set.add(languages[i]);
            }
        }
        if (elements != null && elements.length > 0) {
            if (elements != null && elements.length > 0) {
                for (int i = 0; i < elements.length; i++) {
                    set.remove(elements[i]);
                }
            }
        }

        Iterator value = set.iterator();
        while (value.hasNext()) {
            LanguageModel lanModel = new LanguageModel();
            lanModel.setLanguage(value.next().toString());
            lanModel.setSelected(false);
            languageModel.add(lanModel);
        }
    }

    @Override
    public void onBackPressed() {
        if (isLocationChange || isLanguageChange || isBirthDateChange || isGenderSelected) {
            alertUpdateDta();
        } else {
            isLanguageChange = false;
            isLocationChange = false;
            isBirthDateChange = false;
            isGenderSelected = false;
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        int id = view.getId();
        if (id == R.id.ivNavMenu) {
            onBackPressed();
        } else if (id == R.id.btnEditProfile) {
            setEditVisible(true);
        } else if (id == R.id.btnDone) {
            setEditVisible(false);
            if (isLocationChange || isLanguageChange || isBirthDateChange || isGenderSelected) {
                updateUserDetails();
            }
        } else if (id == R.id.imgEditPhoto) {
            boolean isPermissionEnabled = CommonUtils.permissionsCheck(ProfileActivity.this);
            if (isPermissionEnabled) {
                try {
                    isProfile = true;
                    isTheme = false;
                    if (imgProfile.getDrawable().getConstantState() != null) {
                        if (imgProfile.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.uearn_profile_pic).getConstantState() || imgProfile.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.man_profile_pic).getConstantState() || imgProfile.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.others_pic).getConstantState()) {
                            startDialog("PROFILE PIC");
                        } else {
                            startDialogWithRemove("PROFILE PIC");
                        }
                    } else {
                        startDialog("PROFILE PIC");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else if (id == R.id.img_Text_DOB || id == R.id.img_Edit_DOB) {
            getUserDateOfBirth(view.getId(), tvDOB.getText().toString().trim());
        } else if (id == R.id.img_Text_Gender || id == R.id.img_Edit_Gender) {
            getGender();
        } else if (id == R.id.img_Text_Language || id == R.id.img_Edit_Language) {
            showLanguageAlertDialog();
        } else if (id == R.id.img_Text_Location || id == R.id.img_Edit_Location) {
            String address = "";
            userAddress = new Address();
            if (ApplicationSettings.containsPref(AppConstants.AGENT_LOCATION)) {
                address = ApplicationSettings.getPref(AppConstants.AGENT_LOCATION, "");
                if (address != null && !Utils.isStringEmpty(address) && address.startsWith("{")) {
                    try {
                        JSONObject addressJsonObject = new JSONObject(address);
                        if (addressJsonObject.has("address")) {
                            String addressStr = addressJsonObject.getString("address");
                            userAddress.setAddress(addressStr);
                        }
                        if (addressJsonObject.has("city")) {
                            String addressStr = addressJsonObject.getString("city");
                            userAddress.setCity(addressStr);
                        }
                        if (addressJsonObject.has("state")) {
                            String addressStr = addressJsonObject.getString("state");
                            userAddress.setState(addressStr);
                        }
                        if (addressJsonObject.has("country")) {
                            String addressStr = addressJsonObject.getString("country");
                            userAddress.setCountry(addressStr);
                        }
                        if (addressJsonObject.has("pincode")) {
                            String addressStr = addressJsonObject.getString("pincode");
                            userAddress.setPincode(addressStr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                editLocationPopup(userAddress);
            }

 /*
            case R.id.lyDOB: {
                getUserDateOfBirth(view.getId(), tvDOB.getText().toString().trim());
                break;
            }
            case R.id.lyGender: {
                getGender();
                break;
            }
            case R.id.lyLanguage: {
                showLanguageAlertDialog();
                break;
            }
            case R.id.lyLocation: {
                editLocationPopup(userAddress);
                break;
            }*/
        }
    }

    //===== Edit ProfileImage ====================
    private void startDialog(String imgType) {
        final Dialog networkDialog = CustomTwoButtonDialog.capturePhotoType(this, "Upload Pictures Option", "How do you want to set your picture?");
        TextView btnNo = networkDialog.findViewById(R.id.btn_no);
        btnNo.setText("CAMERA");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        captureImage();
                    } else {
                        captureImage2();
                    }
                } catch (SecurityException se) {
                    CommonUtils.displayDialog(ProfileActivity.this);
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
                startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
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
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        captureImage();
                    } else {
                        captureImage2();
                    }
                } catch (SecurityException se) {
                    CommonUtils.displayDialog(ProfileActivity.this);
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

                pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
            }
        });

        TextView btnRemovePhoto = networkDialog.findViewById(R.id.btn_removephoto);
        btnRemovePhoto.setText("REMOVE PHOTO");

        btnRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                if (gender.equals("M")) {
                    imgProfile.setImageResource(R.drawable.man_profile_pic);
                } else if (gender.equalsIgnoreCase("O")) {
                    imgProfile.setImageResource(R.drawable.others_pic);
                } else {
                    imgProfile.setImageResource(R.drawable.uearn_profile_pic);
                }
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
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

    //=============================================
    private void updateProfileImage(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                if (!ProfileActivity.this.isFinishing()) {
                    Glide.with(this)
                            .load(bitmap)
                            .circleCrop()
                            .override(200, 200)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgProfile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getImage() {
        try {
            final String imgStr = ApplicationSettings.getPref(AppConstants.APP_PROFILE_URL, "");
            if (imgStr != null && !imgStr.isEmpty()) {
                if (!ProfileActivity.this.isFinishing()) {
                    Glide.with(this)
                            .load(imgStr)
                            .circleCrop()
                            .override(200, 200)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgProfile);
                }
            } else {
                if (gender.equals("M")) {
                    imgProfile.setImageResource(R.drawable.man_profile_pic);
                } else if (gender.equalsIgnoreCase("O")) {
                    imgProfile.setImageResource(R.drawable.others_pic);
                } else {
                    imgProfile.setImageResource(R.drawable.uearn_profile_pic);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //===============Upload Profile Image=============================
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
                            if (uploadResponse != null && uploadResponse.has("success")) {
                                JSONObject successObj = uploadResponse.getJSONObject("success");
                                String profilePic = successObj.optString("profile_pic_url");
                                ApplicationSettings.putPref(AppConstants.APP_PROFILE_URL, profilePic);
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
            /*  runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No network available or poor network", Toast.LENGTH_SHORT).show();
                            imgProfile.setImageResource(R.drawable.uearn_profile_pic);
                        }
                    });
            */
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

    //================================================================
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGO_FETCH_REQUEST && resultCode == RESULT_OK) {
            if (CommonUtils.isNetworkAvailable(this)) {
                if (CommonUtils.imageValidation(this, data)) {
                    logoUri = data.getData();
                    CommonUtils.fillImageView(this, logoUri, imgProfile);
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PICK_Camera_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            if (CommonUtils.isNetworkAvailable(this)) {
                imageFilePath = Uri.fromFile(photoFile);
                if (photoFile.exists()) {
                    //imageFilePath = file.getAbsolutePath();
                    imagePath = photoFile.getAbsolutePath();
                    Drawable user_theame_img = Drawable.createFromPath(photoFile.getAbsolutePath());
                    // imgProfile.setBackground(user_theame_img);
                    // imgProfile.setImageBitmap(ThumbImage);
                    SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor edit = shre.edit();
                    edit.putString("USER_THEAME", String.valueOf(user_theame_img));
                    ApplicationSettings.putPref("PROFILE_PIC", String.valueOf(user_theame_img));
                    edit.commit();
                }
                if (imageFilePath != null) {
                    ThumbImage = null;
                    logoUri = imageFilePath;
                    imagePath = photoFile.getAbsolutePath();

                    try {

                        bitmap = BitmapFactory.decodeFile(imagePath); // load
                        // preview image
                        bitmap = ResizeBitmapUtil.resizeBitmap(bitmap, 400, 400);
                        updateProfileImage(bitmap);
                        new UploadBitmap().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Error retriving image!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_Profile_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            if (CommonUtils.isNetworkAvailable(this)) {
                imageFilePath = Uri.fromFile(photoFile);
                if (photoFile.exists()) {
                    imagePath = photoFile.getAbsolutePath();
                    //imageFilePath = file.getAbsolutePath();
                    Drawable user_theame_img = Drawable.createFromPath(photoFile.getAbsolutePath());
                    // imgProfile.setBackground(user_theame_img);
                    SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor edit = shre.edit();
                    edit.putString("USER_THEAME", String.valueOf(user_theame_img));
                    // ApplicationSettings.putPref("PROFILE_PIC", String.valueOf(user_theame_img));
                    edit.commit();
                }
                if (imageFilePath != null) {
                    ThumbImage = null;
                    logoUri = imageFilePath;
                    imagePath = photoFile.getAbsolutePath();

                    try {
                        bitmap = BitmapFactory.decodeFile(imagePath); // load
                        // preview image
                        bitmap = ResizeBitmapUtil.resizeBitmap(bitmap, 400, 400);
                        updateProfileImage(bitmap);
                        new UploadBitmap().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Error retriving image!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == GALLERY_PICTURE && resultCode == AppCompatActivity.RESULT_OK) {
            if (CommonUtils.isNetworkAvailable(this)) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Drawable user_theame_img = Drawable.createFromPath(picturePath);

                    Uri selectedImages = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImages, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndexs = c.getColumnIndex(filePath[0]);
                    selectedImagePath = c.getString(columnIndexs);
                    c.close();

                    bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                    // preview image
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
                    if (isTheme) {
                    } else {
                        updateProfileImage(bitmap);
                    }
                    try {
                        FileOutputStream out = new FileOutputStream(selectedImagePath);
                        imagePath = selectedImagePath;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new UploadBitmap().execute();

                } else {
                    Toast.makeText(getApplicationContext(), "Cancelled",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        }

        /*else if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            displayMessage(getBaseContext(), "File_Path:" + photoFile.getAbsolutePath());
        } else {
            displayMessage(getBaseContext(), "Request cancelled or something went wrong.");
        }*/
    }


    public void previewImage(Uri uri, String imgType) {
        String path = Utils.getPath(uri, ProfileActivity.this);
        if (ThumbImage == null) {
            if (path != null) {
                ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 96, 96);
            } else {

            }
        }
        if (imgType.equals("Profile")) {
            updateProfileImage(ThumbImage);
        }
    }

    private void getGender() {
        final CharSequence[] items = {"Female", "Male", "Not to disclose"};
        int checkedItem = 0;

        String gender = ApplicationSettings.getPref(AppConstants.USERINFO_GENDER, "");
        if (gender.equals("F")) {
            checkedItem = 0;
        } else if (gender.equals("M")) {
            checkedItem = 1;
        } else if (gender.equals("O")) {
            checkedItem = 2;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        String titleText = "Select Gender";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        builder.setTitle(ssBuilder)
                .setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        tvGender.setText(items[item]);
                        String selectedGenderText = tvGender.getText().toString();
                        if (selectedGenderText != null && !selectedGenderText.isEmpty()) {
                            if (selectedGenderText.equalsIgnoreCase("Female")) {
                                genderType = "F";
                            } else if (selectedGenderText.equalsIgnoreCase("Male")) {
                                genderType = "M";
                            } else {
                                genderType = "O";
                            }
                        }
                        isGenderSelected = true;
                    }
                });

        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (alert != null) {
                            alert.dismiss();
                            alert = null;
                        }
                    }
                });
        if (alert == null) {
            alert = builder.create();
        }
        alert.show();
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
        DatePickerDialog datePicker = new DatePickerDialog(ctx, R.style.CustomTheme_datePicker, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Date date = new GregorianCalendar(year, month, day).getTime();
                String formatedDate = dateFormatter.format(date);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                agentBirthDate = formatter.format(date);
                isBirthDateChange = true;
                tvDOB.setText(agentBirthDate);
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


    //========Profile Update API======================

    private void updateUserDetails() {
        //CommonUtils.permissionsCheck(UearnProfileActivity.this);
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            final JSONObject userDetailObj = new JSONObject();
            JSONArray userInfoArr = new JSONArray();

            try {
                if (isGenderSelected) {
                    String gender = genderType;
                    userDetailObj.put("gender", gender);
                }
                if (isLocationChange) {
                    String locationData = locationDataObj.toString();
                    if(locationData != null && !locationData.isEmpty()) {
                        userDetailObj.put("address", locationData);
                    }
                }

                if (isLanguageChange) {
                    String language = String.valueOf(tvLanguage.getText());
                    userDetailObj.put("lang", language);
                }

                if (isBirthDateChange) {
                    userDetailObj.put("dob", agentBirthDate);
                }

                isLanguageChange = false;
                isLocationChange = false;
                isBirthDateChange = false;
                isGenderSelected = false;
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
                            String location = successObject.optString("address");
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

        isLanguageChange = false;
        isLocationChange = false;
        isBirthDateChange = false;
        isGenderSelected = false;
    }

    //============================================================

    private void alertUpdateDta() {
        final Dialog networkDialog = CustomTwoButtonDialog.capturePhotoType(this, "Update Profile Data", "Are you sure to update profile Data?");

        TextView btnNo = networkDialog.findViewById(R.id.btn_no);
        btnNo.setText("Discard");

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                isLocationChange = false;
                isLanguageChange = false;
                isBirthDateChange = false;
                isGenderSelected = false;
                onBackPressed();
            }
        });

        TextView btnYes = networkDialog.findViewById(R.id.btn_yes);
        btnYes.setText("Update");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                if (isLocationChange || isLanguageChange || isBirthDateChange || isGenderSelected) {
                    updateUserDetails();
                }
                networkDialog.dismiss();
                isLocationChange = false;
                isLanguageChange = false;
                isBirthDateChange = false;
                isGenderSelected = false;
                onBackPressed();

            }
        });
        networkDialog.show();
    }

    //============================================================

    private String[] getLanguages() {
        try {
            String language = ApplicationSettings.getPref(AppConstants.LANGUAGES, "");
            if (language != null && !language.isEmpty()) {
                JSONArray languageArray = new JSONArray(language);
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < languageArray.length(); i++) {
                    list.add(languageArray.getString(i));
                }
                languages = list.toArray(new String[list.size()]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return languages;
    }

    public void showLanguageAlertDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        String titleText = "Select Languages";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        String[] languages = new String[languageModel.size()];
        checkedLanguageItems = new boolean[languageModel.size()];


        for (int i = 0; i < languageModel.size(); i++) {
            languages[i] = languageModel.get(i).getLanguage();
            if (checkedLanguageItems.length > 0) {
                if (languageModel.get(i).isSelected()) {
                    checkedLanguageItems[i] = true;
                } else {
                    checkedLanguageItems[i] = false;
                }
            }
        }

        builder.setTitle(ssBuilder).setMultiChoiceItems(languages, checkedLanguageItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                languageModel.get(indexSelected).setSelected(isChecked);
            }
        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            tvLanguage.setText("");
                            String mLanguages = "";
                            for (LanguageModel model : languageModel) {
                                if (model.isSelected()) {
                                    if (mLanguages.length() == 0) {
                                        mLanguages = (model.getLanguage());
                                    } else {
                                        mLanguages = (mLanguages + ", " + model.getLanguage());
                                    }
                                }
                            }
                            if (mLanguages.endsWith(",")) {
                                mLanguages = mLanguages.substring(0, mLanguages.length() - 1);
                                tvLanguage.setText(mLanguages);
                            } else {
                                tvLanguage.setText(mLanguages);
                            }
                            isLanguageChange = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .show();
    }

    public void editLocationPopup(Address address) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.item_edit_location, null);
        final EditText txt_Address = mView.findViewById(R.id.et_address);
        final EditText txt_City = mView.findViewById(R.id.et_city);
        final EditText txt_State = mView.findViewById(R.id.et_state);
        final EditText txt_PinCode = mView.findViewById(R.id.et_Pincode);
        final EditText txt_Country = mView.findViewById(R.id.et_Country);

        final TextInputLayout tl_pincode = mView.findViewById(R.id.tl_pincode);
        final TextInputLayout tl_state = mView.findViewById(R.id.tl_state);
        final TextInputLayout tl_city = mView.findViewById(R.id.tl_city);
        final TextInputLayout tl_address = mView.findViewById(R.id.tl_address);


        if (address != null) {
            if (!Utils.isStringEmpty(address.getAddress())) {
                txt_Address.setText(address.getAddress());
            }
            if (!Utils.isStringEmpty(address.getCity())) {
                txt_City.setText(address.getCity());
            }
            if (!Utils.isStringEmpty(address.getState())) {
                txt_State.setText(address.getState());
            }
            if (!Utils.isStringEmpty(address.getPincode())) {
                txt_PinCode.setText(address.getPincode());
            }
            if (!Utils.isStringEmpty(address.getCountry())) {
                txt_Country.setText(address.getCountry());
            }
        }


        Button btn_cancel = (Button) mView.findViewById(R.id.btnCancel);
        Button btn_okay = (Button) mView.findViewById(R.id.btnSave);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address addressModel = validateAddress();
                if (addressModel != null) {
                    updateUserAddress(addressModel);
                    //Update the Address to server via API Call here.
                    alertDialog.dismiss();
                } else {

                }

            }

            private Address validateAddress() {
                String address = "", city = "", state = "", zipcode = "", country = "";
                address = txt_Address.getText().toString().trim();
                city = txt_City.getText().toString().trim();
                state = txt_State.getText().toString().trim();
                zipcode = txt_PinCode.getText().toString().trim();
                country = txt_Country.getText().toString().trim();

                boolean isValid = false;

                Address addressModel = new Address();
                if (!Utils.isStringEmpty(address)) {
                    addressModel.setAddress(address);
                    isValid = true;
                } else {
                    tl_address.setError("Enter valid address.");
                    isValid = false;
                }
                if (!Utils.isStringEmpty(city)) {
                    addressModel.setCity(city);
                    isValid = true;
                } else {
                    tl_city.setError("Enter valid city name.");
                    isValid = false;
                }

                if (!Utils.isStringEmpty(state)) {
                    addressModel.setState(state);
                    isValid = true;
                } else {
                    tl_state.setError("Enter valid state name.");
                    isValid = false;
                }

                if (!Utils.isStringEmpty(country)) {
                    addressModel.setCountry(country);
                    isValid = true;
                } else {
                    isValid = true;
                }
                if (!Utils.isStringEmpty(zipcode) && Utils.isValidPinCode(zipcode)) {
                    addressModel.setPincode(zipcode);
                    isValid = true;
                } else {
                    tl_pincode.setError("Enter valid Zip-Code.");
                    isValid = false;
                }
                if (isValid) {
                    // updateUserAddress(addressModel);
                } else {
                    addressModel = null;
                }
                return addressModel;
            }
        });
        alertDialog.show();
    }

    private void updateUserAddress(Address address) {
        if (address != null) {
            try {
                locationDataObj = new JSONObject();
                String userAddressLine = "";
                if (!Utils.isStringEmpty(address.getAddress())) {
                    userAddressLine = address.getAddress().trim();
                    locationDataObj.put("address", address.getAddress().trim());
                }
                if (!Utils.isStringEmpty(address.getCity())) {
                    userAddressLine = userAddressLine + ", " + address.getCity().trim();
                    locationDataObj.put("city", address.getCity().trim());
                }
                if (!Utils.isStringEmpty(address.getState())) {
                    userAddressLine = userAddressLine + ", " + address.getState().trim();
                    locationDataObj.put("state", address.getState().trim());
                }
                if (!Utils.isStringEmpty(address.getCountry())) {
                    userAddressLine = userAddressLine + ", " + address.getCountry().trim();
                    locationDataObj.put("country", address.getCountry().trim());
                }
                if (!Utils.isStringEmpty(address.getPincode())) {
                    userAddressLine = userAddressLine + "-" + address.getPincode().trim();
                    locationDataObj.put("pincode", address.getPincode().trim());
                }
                ApplicationSettings.putPref(AppConstants.AGENT_LOCATION, locationDataObj.toString());
                tvLocation.setText(userAddressLine);
                tvLocation.setVisibility(View.VISIBLE);
                if (!Utils.isStringEmpty(tvLocation.getText().toString())) {
                    ul_Location.setVisibility(View.VISIBLE);
                } else {
                    ul_Location.setVisibility(View.GONE);
                }
                isLocationChange = true;
            } catch(Exception e){
                 e.printStackTrace();
            }
        } else {
            tvLocation.setText("");
            tvLocation.setVisibility(View.GONE);
        }
    }

    //=================New Capture Image from Camera =====================================

    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private void captureImage2() {
        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile4();
            if (photoFile != null) {
                displayMessage(getBaseContext(), photoFile.getAbsolutePath());
                LogUtils.e("CameraImageFilePath: " + photoFile.getAbsolutePath());
                Uri photoURI = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            displayMessage(getBaseContext(), "Camera is not available." + e.toString());
        }
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile();
                    //   displayMessage(getBaseContext(), photoFile.getAbsolutePath());
                    LogUtils.e(photoFile.getAbsolutePath());
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this, "smarter.uearn.money.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        if (isProfile) {
                            startActivityForResult(takePictureIntent, PICK_Camera_IMAGE);
                        } else {
                            startActivityForResult(takePictureIntent, PICK_Profile_IMAGE);
                        }
                        // startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                    displayMessage(getBaseContext(), "Unable capture photo.");
                }
            } else {
                displayMessage(getBaseContext(), "Unable capture photo.");
            }
        }

    }

    private void displayMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private File createImageFile4() {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                displayMessage(getBaseContext(), "Unable capture photo.");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //============================================================
}
