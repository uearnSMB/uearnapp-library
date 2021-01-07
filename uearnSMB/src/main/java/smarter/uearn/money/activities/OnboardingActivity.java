package smarter.uearn.money.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import smarter.uearn.money.R;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.models.KycDocuments;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.LogUtils;
import smarter.uearn.money.utils.ResizeBitmapUtil;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;

import static smarter.uearn.money.utils.AppConstants.DOCUMENT_TYPE_ADHARCARD;
import static smarter.uearn.money.utils.AppConstants.DOCUMENT_TYPE_PANCARD;
import static smarter.uearn.money.utils.AppConstants.DOCUMENT_TYPE_PASSBOOK;
import static smarter.uearn.money.utils.AppConstants.DOCUMENT_TYPE_PROFILE_PHOTO;
import static smarter.uearn.money.utils.Utils.isPanCardValid;
import static smarter.uearn.money.utils.Utils.isValidAadharNumber;

public class OnboardingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int FILE_SELECT_CODE = 110;
    public static final int RequestPermissionCode = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;
    private Uri imageFilePath;
    private LinearLayout browseTextLayout, documentsMainLayout;
    List<String> checkboxValues = new ArrayList<>();
    private TextView interviewsButton, onboardingButton, trainingButton;
    private Button nextButton, nextButton1, nextButton2;
    private TextView browse;
    private int documentsCount = 0;
    private int progressStatus = 0;
    private String docName = "";
    private EditText etPanCard, etAdharCard, etPassBook;
    private TextView error_Pancard, error_AdharCard, error_Passbook;

    private ImageView attachmentPanCard, attachmentAdhar, attachmentPassBook, attachmentProfilePhoto;
    private ImageView dltPanCard, dltAadharCard, dltPassCard, dltProfilePhoto;
    private ImageView statusPanCard, statusAadharCard, statusPassCard, statusProfilePhoto;
    private ImageView imageProgressOne;

    View lyOnBoardInfo, lyIdForm, lyProfile;
    private boolean isPANUploaded;
    private boolean isAaadharuploaded;
    private boolean isPassbookuploaded;
    private boolean isProfilePicuploaded;
    private TextView successMessage, succesProfileImgMeassage;
    private ProgressBar progressBarPANCard, progressBarAadharCard, progressBarPassbook, progressBarProfilePhoto;
    private TextView headerTitle;
    private ImageView profileImage, image_sync, image_notification;
    private LinearLayout notificationLayout;
    private String userStatus = "";

    //========New Parms for caoture Image=====================
    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;

    String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME = "UEARN";
    //=========================================================

    private boolean isAttachPANCardClicked = false;
    private boolean isAttachAadharCardClicked = false;
    private boolean isAttachPassbookClicked = false;
    private boolean isAttachProfilePicClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uearn_onboard);

        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("Onboarding");
        profileImage = (ImageView) findViewById(R.id.profile_image_back);
        profileImage.setBackgroundResource(R.drawable.ic_arrow_back_white_24px);
        profileImage.setOnClickListener(this);
        notificationLayout = (LinearLayout) findViewById(R.id.ll_notification);
        notificationLayout.setOnClickListener(this);

        image_sync = findViewById(R.id.image_sync);
        image_notification = findViewById(R.id.image_notification);
        image_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    settingsApi(userId);
                    Animation rotation = AnimationUtils.loadAnimation(OnboardingActivity.this, R.anim.button_rotate);
                    rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                    image_sync.startAnimation(rotation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        changeStatusBarColor(this);
        enableRuntimePermission();
        etPanCard = findViewById(R.id.etPanCard);
        etAdharCard = findViewById(R.id.etAdharCard);
        etPassBook = findViewById(R.id.etPassBook);


        error_Pancard = findViewById(R.id.error_Pancard);
        error_AdharCard = findViewById(R.id.error_AdharCard);
        error_Passbook = findViewById(R.id.error_Passbook);

        etPanCard.setOnClickListener(this);
        etAdharCard.setOnClickListener(this);
        etPassBook.setOnClickListener(this);

        successMessage = findViewById(R.id.tvSuccessMsg);
        succesProfileImgMeassage = findViewById(R.id.tvSuccesProfileImg);
        lyOnBoardInfo = findViewById(R.id.lyInfo);
        lyIdForm = findViewById(R.id.lyUploadDocs);
        lyProfile = findViewById(R.id.ly_Profile);

        nextButton1 = findViewById(R.id.btnNext1);
        nextButton1.setText("NEXT");
        nextButton1.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton1.setBackgroundResource(R.drawable.red_rounded_corner);
        nextButton1.setOnClickListener(this);

        nextButton2 = findViewById(R.id.btnNext2);
        nextButton2.setText("NEXT");
        nextButton2.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton2.setBackgroundResource(R.drawable.red_rounded_corner);
        nextButton2.setOnClickListener(this);

        dltPanCard = findViewById(R.id.imgDelete_PanCard);
        dltAadharCard = findViewById(R.id.imgDelete_AadharCard);
        dltPassCard = findViewById(R.id.imgDelete_BankPassbook);
        dltProfilePhoto = findViewById(R.id.imgDelete_ProfilePhoto);
        dltPanCard.setOnClickListener(this);
        dltPanCard.setEnabled(false);
        dltAadharCard.setOnClickListener(this);
        dltAadharCard.setEnabled(false);
        dltPassCard.setOnClickListener(this);
        dltPassCard.setEnabled(false);
        dltProfilePhoto.setOnClickListener(this);
        dltProfilePhoto.setEnabled(false);

        statusPanCard = findViewById(R.id.imgStatus_PanCard);
        statusAadharCard = findViewById(R.id.imgStatus_AadharCard);
        statusPassCard = findViewById(R.id.imgStatus_BankPassbook);
        statusProfilePhoto = findViewById(R.id.imgStatus_ProfilePhoto);

        progressBarPANCard = (ProgressBar) findViewById(R.id.progress_PanCard);
        progressBarAadharCard = (ProgressBar) findViewById(R.id.progress_AadharCard);
        progressBarPassbook = (ProgressBar) findViewById(R.id.progress_BankPassbook);
        progressBarProfilePhoto = (ProgressBar) findViewById(R.id.progress_ProfilePhoto);

        imageProgressOne = findViewById(R.id.img_ProgressOne);

        nextButton = findViewById(R.id.btnNext);
        nextButton.setOnClickListener(this);
        nextButton.setText("NEXT");
        nextButton.setTextSize(20);
        nextButton.setEnabled(false);
        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);

        interviewsButton = (TextView) findViewById(R.id.interviews);
        interviewsButton.setGravity(Gravity.CENTER);
        interviewsButton.setBackgroundResource(R.drawable.onboarding);
        interviewsButton.setText("Interviews");
        interviewsButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));

        onboardingButton = (TextView) findViewById(R.id.onboarding);
        onboardingButton.setGravity(Gravity.CENTER);
        onboardingButton.setBackgroundResource(R.drawable.interviews);
        onboardingButton.setText("Onboarding");
        onboardingButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));

        trainingButton = (TextView) findViewById(R.id.training);
        trainingButton.setGravity(Gravity.CENTER);
        trainingButton.setBackgroundResource(R.drawable.training);
        trainingButton.setText("Training");
        trainingButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));

        browse = (TextView) findViewById(R.id.browse);
        browse.setGravity(Gravity.CENTER);
        browse.setBackgroundResource(R.drawable.ic_browse);
        browse.setText("Browse");
        browse.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        browseTextLayout = (LinearLayout) findViewById(R.id.browse_text_layout);
        browseTextLayout.setOnClickListener(this);

        attachmentPanCard = findViewById(R.id.imgAttach_PanCard);
        attachmentAdhar = findViewById(R.id.imgAttach_AadharCard);
        attachmentPassBook = findViewById(R.id.imgAttach_BankPassbook);
        attachmentProfilePhoto = findViewById(R.id.imgAttach_ProfilePhoto);
        attachmentPanCard.setOnClickListener(this);
        attachmentAdhar.setOnClickListener(this);
        attachmentPassBook.setOnClickListener(this);
        attachmentProfilePhoto.setOnClickListener(this);

        etPanCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    successMessage.setText("");
                    error_Pancard.setVisibility(View.GONE);
                }
            }
        });

        etAdharCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    successMessage.setText("");
                    error_AdharCard.setVisibility(View.GONE);
                }
            }
        });

        etPassBook.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    successMessage.setText("");
                    error_Passbook.setVisibility(View.GONE);
                }
            }
        });
        getUserKYCDetails();
    }

    private void settingsApi(final String userId) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.GetSettings(userId, 22, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                }
            }).call();
        }
    }

    private void getUserKYCDetails() {
        try {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

            KycDocuments kyc_PANCARD = ApplicationSettings.getKycDocuments(this, AppConstants.DOCUMENT_KYC_PANCARD);
            KycDocuments kyc_AADHAR = ApplicationSettings.getKycDocuments(this, AppConstants.DOCUMENT_KYC_ADHARCARD);
            KycDocuments kyc_PASSBOOK = ApplicationSettings.getKycDocuments(this, AppConstants.DOCUMENT_KYC_PASSBOOK);
            KycDocuments kyc_PROFILE_PIC = ApplicationSettings.getKycDocuments(this, AppConstants.DOCUMENT_KYC_PROFILE_PHOTO);

            if (kyc_PANCARD != null && !Utils.isStringEmpty(userId)) {
                LogUtils.e(kyc_PANCARD.toString());
                addPanCardValue(kyc_PANCARD);
            }
            if (kyc_AADHAR != null) {
                LogUtils.e(kyc_PANCARD.toString());
                addAadharCardValue(kyc_AADHAR);
            }
            if (kyc_PASSBOOK != null) {
                LogUtils.e(kyc_PANCARD.toString());
                addPassBookValue(kyc_PASSBOOK);
            }
            if (kyc_PROFILE_PIC != null) {
                LogUtils.e(kyc_PANCARD.toString());
                addProfilePicValue(kyc_PROFILE_PIC);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addProfilePicValue(KycDocuments kyc_ProfilePic) {
        if (!Utils.isStringEmpty(kyc_ProfilePic.getDocUrl()) && kyc_ProfilePic.getUploaded()) {
            dltProfilePhoto.setEnabled(true);
            attachmentProfilePhoto.setEnabled(false);
            dltProfilePhoto.setColorFilter(getResources().getColor(R.color.app_color));
            statusProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
            isProfilePicuploaded = true;
            runOnUiThread(new Runnable() {
                public void run() {
                    if (succesProfileImgMeassage != null && isAttachProfilePicClicked) {
                        succesProfileImgMeassage.setText("Uploading file. Please wait...");
                        succesProfileImgMeassage.setTextColor(getResources().getColor(R.color.blue));
                    }
                    showProgress(progressBarProfilePhoto);
                }
            });
            if (isProfilePicuploaded) {
                nextButton2.setVisibility(View.VISIBLE);
                nextButton2.setEnabled(true);
                nextButton2.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                nextButton2.setBackgroundResource(R.drawable.red_rounded_corner);
            }
        }
    }

    private void addPassBookValue(KycDocuments kyc_passbook) {
        if (!Utils.isStringEmpty(kyc_passbook.getDocValue())) {
            etPassBook.setText(kyc_passbook.getDocValue());
            etPassBook.setEnabled(false);
            attachmentPassBook.setEnabled(false);
            dltPassCard.setEnabled(true);
            dltPassCard.setColorFilter(getResources().getColor(R.color.app_color));
            statusPassCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
            isPassbookuploaded = true;
            documentsCount++;
            runOnUiThread(new Runnable() {
                public void run() {
                    if (successMessage != null && isAttachPassbookClicked) {
                        successMessage.setText("Uploading file. Please wait...");
                        successMessage.setTextColor(getResources().getColor(R.color.blue));
                    }
                    showProgress(progressBarPassbook);
                }
            });
            if (isPANUploaded && isAaadharuploaded && isPassbookuploaded) {
                nextButton.setEnabled(true);
                nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
            }
        }
    }

    private void addAadharCardValue(KycDocuments kyc_aadhar) {
        if (!Utils.isStringEmpty(kyc_aadhar.getDocValue())) {
            etAdharCard.setText(kyc_aadhar.getDocValue());
            etAdharCard.setEnabled(false);
            attachmentAdhar.setEnabled(false);
            dltAadharCard.setEnabled(true);
            dltAadharCard.setColorFilter(getResources().getColor(R.color.app_color));
            statusAadharCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
            isAaadharuploaded = true;
            documentsCount++;
            runOnUiThread(new Runnable() {
                public void run() {
                    if (successMessage != null && isAttachAadharCardClicked) {
                        successMessage.setText("Uploading file. Please wait...");
                        successMessage.setTextColor(getResources().getColor(R.color.blue));
                    }
                    showProgress(progressBarAadharCard);
                }
            });
            if (isPANUploaded && isAaadharuploaded && isPassbookuploaded) {
                nextButton.setEnabled(true);
                nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
            }
        }
    }

    private void addPanCardValue(KycDocuments kyc_pancard) {
        if (!Utils.isStringEmpty(kyc_pancard.getDocValue())) {
            etPanCard.setText(kyc_pancard.getDocValue().toUpperCase(Locale.ENGLISH));
            etPanCard.setEnabled(false);
            attachmentPanCard.setEnabled(false);
            dltPanCard.setEnabled(true);
            dltPanCard.setColorFilter(getResources().getColor(R.color.app_color));
            statusPanCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
            isPANUploaded = true;
            documentsCount++;
            runOnUiThread(new Runnable() {
                public void run() {
                    if (successMessage != null && isAttachPANCardClicked) {
                        successMessage.setText("Uploading file. Please wait...");
                        successMessage.setTextColor(getResources().getColor(R.color.blue));
                    }
                    showProgress(progressBarPANCard);
                }
            });
            if (isPANUploaded && isAaadharuploaded && isPassbookuploaded) {
                nextButton.setEnabled(true);
                nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
            }
        }

    }

    private void showProgress(final ProgressBar progressBar) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                progressStatus = 0;
                while (progressStatus < 100) {
                    progressStatus += 1;
                    try {
                        Thread.sleep(7);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            if (progressStatus == 100) {
                                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progress_bar_completed));
                            }
                        }
                    });
                }
            }
        }).start();
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

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (lyProfile.getVisibility() == View.VISIBLE) {
            lyOnBoardInfo.setVisibility(View.GONE);
            lyIdForm.setVisibility(View.VISIBLE);
            lyProfile.setVisibility(View.GONE);
            hideErrorMessage();
        } else if (lyIdForm.getVisibility() == View.VISIBLE) {
            lyOnBoardInfo.setVisibility(View.VISIBLE);
            lyIdForm.setVisibility(View.GONE);
            lyProfile.setVisibility(View.GONE);
            hideErrorMessage();
        } else {
            hideErrorMessage();
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void hideErrorMessage() {
        successMessage.setText("");
        succesProfileImgMeassage.setText("");
        error_Pancard.setVisibility(View.GONE);
        error_AdharCard.setVisibility(View.GONE);
        error_Passbook.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        int id = view.getId();
        switch (id) {

            case R.id.btnNext1: {
                lyOnBoardInfo.setVisibility(view.GONE);
                lyIdForm.setVisibility(view.VISIBLE);
                headerTitle.setText("Identity Proof");
                break;
            }

            case R.id.btnNext2: {
                successMessage.setText("");
                succesProfileImgMeassage.setText("");
                navigateToOnboardingServiceAgreementActivity();
                break;
            }

            case R.id.browse_text_layout: {
                if (documentsCount < 5) {
                    startDialog();
                } else {
                    Toast.makeText(this, "Maximum of 5 documents can be uploaded", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.imgAttach_PanCard: {
                isAttachPANCardClicked = true;
                successMessage.setText("");
                String pancard = String.valueOf(etPanCard.getText());
                if (Utils.isStringEmpty(pancard)) {
                    error_Pancard.setText("Enter pancard number,eg: ABCDE1234F");
                    etPanCard.requestFocus();
                    error_Pancard.setVisibility(View.VISIBLE);
                    return;
                } else {
                    etPanCard.setText(pancard.toUpperCase(Locale.ENGLISH));
                    etPanCard.setSelection(etPanCard.getText().length());
                }

                if (!Utils.isStringEmpty(pancard) && isPanCardValid(pancard.toUpperCase(Locale.ENGLISH))) {

                    docName = DOCUMENT_TYPE_PANCARD;
                    startDialog();
                } else if (!Utils.isStringEmpty(pancard) && isPanCardValid(pancard) && pancard.length() > 0) {
                    error_Pancard.setText("Enter valid pancard number, eg: ABCDE1234F.");
                    etPanCard.requestFocus();
                    error_Pancard.setVisibility(View.VISIBLE);
                } else {
                    error_Pancard.setText("Enter pancard number,eg: ABCDE1234F");
                    etPanCard.requestFocus();
                    error_Pancard.setVisibility(View.VISIBLE);
                }
                break;
            }

            case R.id.imgAttach_AadharCard: {
                isAttachAadharCardClicked = true;
                successMessage.setText("");
                String aadharCard = String.valueOf(etAdharCard.getText());

                if (!Utils.isStringEmpty(aadharCard) && isValidAadharNumber(aadharCard)) {
                    docName = DOCUMENT_TYPE_ADHARCARD;
                    startDialog();
                    error_AdharCard.setVisibility(View.GONE);
                } else if (!Utils.isStringEmpty(aadharCard) && isValidAadharNumber(aadharCard) && aadharCard.length() > 0) {
                    error_AdharCard.setText("Enter valid aadhar card number.");
                    etAdharCard.requestFocus();
                    error_AdharCard.setVisibility(View.VISIBLE);
                } else {
                    error_AdharCard.setText("Enter aadhar card number.");
                    etAdharCard.requestFocus();
                    error_AdharCard.setVisibility(View.VISIBLE);
                }
                break;
            }

            case R.id.imgAttach_BankPassbook: {
                isAttachPassbookClicked = true;
                successMessage.setText("");
                String passbook = String.valueOf(etPassBook.getText());
                if (!Utils.isStringEmpty(passbook) && passbook.length() > 3) {
                    docName = DOCUMENT_TYPE_PASSBOOK;
                    startDialog();
                    error_Passbook.setVisibility(View.GONE);
                } else if (!Utils.isStringEmpty(passbook) && passbook.length() < 3 && passbook.length() > 0) {
                    error_Passbook.setText("Enter valid bank account number.");
                    error_Passbook.setVisibility(View.VISIBLE);
                } else {
                    error_Passbook.setText("Enter bank account number.");
                    error_Passbook.setVisibility(View.VISIBLE);
                }
                break;
            }

            case R.id.etAdharCard: {
                successMessage.setText("");
                error_AdharCard.setVisibility(View.GONE);
            }
            case R.id.etPanCard: {
                successMessage.setText("");
                error_Pancard.setVisibility(View.GONE);
            }
            case R.id.etPassBook: {
                successMessage.setText("");
                error_Passbook.setVisibility(View.GONE);
                break;
            }

            case R.id.imgDelete_PanCard:
                try {
                    ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_PANCARD, null);
                    dltPanCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_attachment));
                    dltPanCard.setColorFilter(getResources().getColor(R.color.delete_button_disable));
                    statusPanCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_not_uploaded));
                    progressBarPANCard.setProgress(0);
                    attachmentPanCard.setEnabled(true);
                    etPanCard.setText("");
                    etPanCard.setEnabled(true);
                    isPANUploaded = false;
                    successMessage.setText("");
                    nextButton.setEnabled(false);
                    nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.imgDelete_AadharCard:
                try {
                    ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_ADHARCARD, null);
                    dltAadharCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_attachment));
                    dltAadharCard.setColorFilter(getResources().getColor(R.color.delete_button_disable));
                    statusAadharCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_not_uploaded));
                    progressBarAadharCard.setProgress(0);
                    attachmentAdhar.setEnabled(true);
                    etAdharCard.setText("");
                    isAaadharuploaded = false;
                    etAdharCard.setEnabled(true);
                    successMessage.setText("");
                    nextButton.setEnabled(false);
                    nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.imgDelete_BankPassbook:
                try {
                    ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_PASSBOOK, null);
                    dltPassCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_attachment));
                    dltPassCard.setColorFilter(getResources().getColor(R.color.delete_button_disable));
                    statusPassCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_not_uploaded));
                    progressBarPassbook.setProgress(0);
                    etPassBook.setText("");
                    attachmentPassBook.setEnabled(true);
                    etPassBook.setEnabled(true);
                    isPassbookuploaded = false;
                    successMessage.setText("");
                    nextButton.setEnabled(false);
                    nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.imgDelete_ProfilePhoto:
                try {
                    ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_PROFILE_PHOTO, null);
                    dltProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_attachment));
                    dltProfilePhoto.setColorFilter(getResources().getColor(R.color.delete_button_disable));
                    statusProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_not_uploaded));
                    progressBarProfilePhoto.setProgress(0);
                    attachmentProfilePhoto.setEnabled(true);
                    succesProfileImgMeassage.setText("");
                    nextButton2.setEnabled(false);
                    isProfilePicuploaded = false;
                    nextButton2.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    nextButton2.setBackgroundResource(R.drawable.disable_rounded_corner);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.imgAttach_ProfilePhoto:
                isAttachProfilePicClicked = true;
                successMessage.setText("");
                docName = DOCUMENT_TYPE_PROFILE_PHOTO;
                startDialog();
                break;

            case R.id.btnNext:
                if (documentsCount > 2) {
                    ApplicationSettings.putPref(AppConstants.KYC_DOC_UPLOADED, true);
                    lyOnBoardInfo.setVisibility(view.GONE);
                    lyIdForm.setVisibility(view.GONE);
                    lyProfile.setVisibility(view.VISIBLE);
                    headerTitle.setText("Profile Photo");

                    imageProgressOne.setImageDrawable(getResources().getDrawable(R.drawable.bg_circle_status_active));
                    nextButton2.setEnabled(true);
                    nextButton2.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    nextButton2.setBackgroundResource(R.drawable.red_rounded_corner);
                } else {
                    Toast.makeText(this, "Kindly upload KYC documents", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.profile_image_back:
                onBackPressed();
                break;
        }
    }

    private void navigateToOnboardingServiceAgreementActivity() {
        Intent intent = new Intent(this, OnboardingServiceAgreementActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void startDialog() {
        final Dialog networkDialog = CustomTwoButtonDialog.capturePhotoType(this, "Upload option", "Do you want to choose file from gallery or upload picture?");
        TextView btnNo = networkDialog.findViewById(R.id.btn_no);
        btnNo.setText("CAMERA");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                dispatchTakePictureIntent();
            }
        });

        TextView btnYes = networkDialog.findViewById(R.id.btn_yes);
        btnYes.setText("GALLERY");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                browseFileFromChooser();
            }
        });
        networkDialog.show();
    }

    private void browseFileFromChooser() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select a file to upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri saveImage(Bitmap bitmap) {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Uri uri = FileProvider.getUriForFile(this, "smarter.uearn.money.fileprovider", photoFile);
        if (photoFile != null) {
            if (photoFile.exists())
                photoFile.delete();
            try {
                FileOutputStream out = new FileOutputStream(photoFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageBitmap = ResizeBitmapUtil.resizeBitmap(imageBitmap, 600, 800);
                        if (uri != null) {
                            String SavedImgPath = new File(saveImage(imageBitmap).getPath()).getAbsolutePath();
                            if (!Utils.isStringEmpty(SavedImgPath)) {
                                new UploadBitmap().execute();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (CommonUtils.isNetworkAvailable(this)) {
                        if (photoFile != null) {
                            Uri profileUri = Uri.fromFile(photoFile);
                            Log.e("KYC FILE", photoFile.getAbsolutePath());
                            if (!Utils.isStringEmpty(photoFile.getAbsolutePath())) {
                                currentPhotoPath = photoFile.getAbsolutePath();
                                new UploadBitmap().execute();
                            } else {
                                displayMessage(getBaseContext(), "Unable capture photo.");
                            }
                        } else {
                            Toast.makeText(this, "Error retrieving image!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void enableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }


    private void dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureImage();
        } else {
            captureImage2();
        }
    }

    @Override
    protected void onResume() {
        SmarterSMBApplication.setCurrentActivity(this);
        getUserKYCDetails();
        super.onResume();
    }

    class UploadBitmap extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String imageFile = new File(currentPhotoPath).toString();
            String fileName = new File(currentPhotoPath).getName();
            switch (docName) {
                case DOCUMENT_TYPE_PANCARD:
                    if (!imageFile.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (successMessage != null && isAttachPANCardClicked) {
                                    successMessage.setText("Uploading file. Please wait...");
                                    successMessage.setTextColor(getResources().getColor(R.color.blue));
                                }
                                showProgress(progressBarPANCard);
                            }
                        });
                        String pancard = String.valueOf(etPanCard.getText()).trim();
                        uploadImageFile(imageFile, DOCUMENT_TYPE_PANCARD, fileName, pancard);
                    }
                    break;
                case DOCUMENT_TYPE_ADHARCARD:
                    if (!imageFile.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (successMessage != null && isAttachAadharCardClicked) {
                                    successMessage.setText("Uploading file. Please wait...");
                                    successMessage.setTextColor(getResources().getColor(R.color.blue));
                                }
                                showProgress(progressBarAadharCard);
                            }
                        });

                        String aadharCard = String.valueOf(etAdharCard.getText()).trim();
                        uploadImageFile(imageFile, DOCUMENT_TYPE_ADHARCARD, fileName, aadharCard);
                    }
                    break;
                case DOCUMENT_TYPE_PASSBOOK:
                    if (!imageFile.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (successMessage != null && isAttachPassbookClicked) {
                                    successMessage.setText("Uploading file. Please wait...");
                                    successMessage.setTextColor(getResources().getColor(R.color.blue));
                                }
                                showProgress(progressBarPassbook);
                            }
                        });
                        String passbook = String.valueOf(etPassBook.getText()).trim();
                        uploadImageFile(imageFile, DOCUMENT_TYPE_PASSBOOK, fileName, passbook);
                    }
                    break;
                case DOCUMENT_TYPE_PROFILE_PHOTO:
                    if (!imageFile.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (succesProfileImgMeassage != null && isAttachProfilePicClicked) {
                                    succesProfileImgMeassage.setText("Uploading file. Please wait...");
                                    succesProfileImgMeassage.setTextColor(getResources().getColor(R.color.blue));
                                }
                                showProgress(progressBarProfilePhoto);
                            }
                        });
                        uploadImageFile(imageFile, DOCUMENT_TYPE_PROFILE_PHOTO, fileName, "Profile photo");
                    }
                    break;
            }
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

    public void uploadImageFile(String imageFile, String documentType, String fileName, String doc_Value) {
        if (CommonUtils.isNetworkAvailable(this)) {
            String filePath = imageFile;
            JSONObject response = DataUploadUtils.uploadImageFileToServer(filePath, Urls.getUploadFileUrl());
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

            try {
                if (response != null && !Utils.isStringEmpty(userId)) {
                    if (response.getString("url") != null) {
                        String imageUrl = response.getString("url");
                        JSONObject docDataJson = new JSONObject();
                        docDataJson.put("for_user", Integer.valueOf(userId));
                        docDataJson.put("doc_name", documentType);
                        docDataJson.put("doc_value", doc_Value);
                        docDataJson.put("doc_url", imageUrl);
                        docDataJson.put("file_name", fileName);

                        KycDocuments kycDocuments = new KycDocuments();
                        kycDocuments.setDocName(documentType);
                        kycDocuments.setDocUrl(imageUrl);
                        kycDocuments.setDocValue(doc_Value);
                        kycDocuments.setFileName(fileName);
                        kycDocuments.setForUser(Integer.valueOf(userId));
                        kycDocuments.setUserId(userId);

                        JSONObject uploadResponse = DataUploadUtils.postJsonData(Urls.postOnboardKyc(), docDataJson);
                        if (uploadResponse != null) {
                            try {
                                if (uploadResponse.has("success")) {
                                    String successMsg = uploadResponse.getString("success");
                                    if (successMsg != null && !successMsg.isEmpty()) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                kycDocuments.setUploaded(true);
                                                saveKYCData(documentType, true, kycDocuments);
                                                handleKYCResult(documentType, successMsg, true);
                                            }
                                        });
                                    }
                                } else {
                                    handleKYCResult(documentType, "Upload failed", false);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handleKYCResult(documentType, "Upload failed", false);
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleKYCResult(documentType, "Upload failed", false);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                handleKYCResult(documentType, "Upload failed", false);
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    successMessage.setText("Network not available");
                    successMessage.setTextColor(getResources().getColor(R.color.app_color));
                    if (documentType != null && !documentType.isEmpty() && documentType.equals(DOCUMENT_TYPE_PANCARD)) {
                        statusPanCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                        dltPanCard.setEnabled(true);
                        dltPanCard.setColorFilter(getResources().getColor(R.color.app_color));
                        isPANUploaded = false;
                        isAttachPANCardClicked = false;
                    } else if (documentType != null && !documentType.isEmpty() && documentType.equals(DOCUMENT_TYPE_ADHARCARD)) {
                        statusAadharCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                        dltAadharCard.setEnabled(true);
                        dltAadharCard.setColorFilter(getResources().getColor(R.color.app_color));
                        isAaadharuploaded = false;
                        isAttachAadharCardClicked = false;
                    } else if (documentType != null && !documentType.isEmpty() && documentType.equals(DOCUMENT_TYPE_PASSBOOK)) {
                        statusPassCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                        dltPassCard.setEnabled(true);
                        dltPassCard.setColorFilter(getResources().getColor(R.color.app_color));
                        isPassbookuploaded = false;
                        isAttachPassbookClicked = false;
                    } else if (documentType != null && !documentType.isEmpty() && documentType.equals(DOCUMENT_TYPE_PROFILE_PHOTO)) {
                        statusProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                        dltProfilePhoto.setEnabled(true);
                        dltProfilePhoto.setColorFilter(getResources().getColor(R.color.app_color));
                        isProfilePicuploaded = false;
                        isAttachProfilePicClicked = false;
                    }
                }
            });
        }
    }

    private void saveKYCData(String documentType, boolean isSuccess, KycDocuments kycDocuments) {
        try {
            if (kycDocuments != null && !Utils.isStringEmpty(documentType)) {
                switch (documentType) {
                    case DOCUMENT_TYPE_PANCARD: {
                        ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_PANCARD, kycDocuments);
                    }
                    break;
                    case DOCUMENT_TYPE_ADHARCARD: {
                        ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_ADHARCARD, kycDocuments);
                    }
                    break;
                    case DOCUMENT_TYPE_PASSBOOK: {
                        ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_PASSBOOK, kycDocuments);
                    }
                    break;
                    case DOCUMENT_TYPE_PROFILE_PHOTO: {
                        ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_PROFILE_PHOTO, kycDocuments);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleKYCResult(String kycDocument, String msg, boolean resultStatus) {
        if (kycDocument != null && resultStatus) {
            switch (kycDocument) {
                case DOCUMENT_TYPE_PANCARD: {
                    dltPanCard.setEnabled(true);
                    dltPanCard.setColorFilter(getResources().getColor(R.color.app_color));
                    statusPanCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
                    isPANUploaded = true;
                    etPanCard.setEnabled(false);
                    isAttachPANCardClicked = false;
                    successMessage.setText(msg);
                    successMessage.setTextColor(getResources().getColor(R.color.success_color));
                    documentsCount++;
                    if (isPANUploaded && isAaadharuploaded && isPassbookuploaded) {
                        nextButton.setEnabled(true);
                        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                    }
                }
                break;
                case DOCUMENT_TYPE_ADHARCARD: {
                    dltAadharCard.setEnabled(true);
                    dltAadharCard.setColorFilter(getResources().getColor(R.color.app_color));
                    statusAadharCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
                    isAaadharuploaded = true;
                    etAdharCard.setEnabled(false);
                    isAttachAadharCardClicked = false;
                    successMessage.setText(msg);
                    successMessage.setTextColor(getResources().getColor(R.color.success_color));
                    documentsCount++;
                    if (isPANUploaded && isAaadharuploaded && isPassbookuploaded) {
                        nextButton.setEnabled(true);
                        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                    }
                }
                break;
                case DOCUMENT_TYPE_PASSBOOK: {
                    dltPassCard.setEnabled(true);
                    dltPassCard.setColorFilter(getResources().getColor(R.color.app_color));
                    statusPassCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
                    isPassbookuploaded = true;
                    etPassBook.setEnabled(false);
                    isAttachPassbookClicked = false;
                    successMessage.setText(msg);
                    successMessage.setTextColor(getResources().getColor(R.color.success_color));
                    documentsCount++;
                    if (isPANUploaded && isAaadharuploaded && isPassbookuploaded) {
                        nextButton.setEnabled(true);
                        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                    }
                }

                break;
                case DOCUMENT_TYPE_PROFILE_PHOTO: {
                    dltProfilePhoto.setEnabled(true);
                    dltProfilePhoto.setColorFilter(getResources().getColor(R.color.app_color));
                    statusProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
                    documentsCount++;
                    isProfilePicuploaded = true;
                    isAttachProfilePicClicked = false;
                    succesProfileImgMeassage.setText(msg);
                    succesProfileImgMeassage.setVisibility(View.VISIBLE);
                    succesProfileImgMeassage.setTextColor(getResources().getColor(R.color.success_color));
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setEnabled(true);
                    nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                    nextButton1.setVisibility(View.VISIBLE);
                    nextButton1.setText("NEXT");
                    nextButton1.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    nextButton1.setBackgroundResource(R.drawable.red_rounded_corner);
                    nextButton2.setVisibility(View.VISIBLE);
                    nextButton2.setEnabled(true);
                    nextButton2.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    nextButton2.setBackgroundResource(R.drawable.red_rounded_corner);
                }
                break;
            }
        } else if (kycDocument != null && resultStatus == false) {
            switch (kycDocument) {
                case DOCUMENT_TYPE_PROFILE_PHOTO: {
                    dltProfilePhoto.setEnabled(true);
                    dltProfilePhoto.setColorFilter(getResources().getColor(R.color.app_color));
                    succesProfileImgMeassage.setText(msg);
                    succesProfileImgMeassage.setVisibility(View.VISIBLE);
                    succesProfileImgMeassage.setTextColor(getResources().getColor(R.color.app_color));
                    progressBarProfilePhoto.setProgress(0);
                    statusProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                    isProfilePicuploaded = false;
                    isAttachProfilePicClicked = false;
                }
                case DOCUMENT_TYPE_PANCARD: {
                    dltPanCard.setEnabled(true);
                    dltPanCard.setColorFilter(getResources().getColor(R.color.app_color));
                    successMessage.setText(msg);
                    successMessage.setTextColor(getResources().getColor(R.color.app_color));
                    progressBarPANCard.setProgress(0);
                    statusPanCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                    isPANUploaded = false;
                    isAttachPANCardClicked = false;
                }
                case DOCUMENT_TYPE_ADHARCARD: {
                    dltAadharCard.setEnabled(true);
                    dltAadharCard.setColorFilter(getResources().getColor(R.color.app_color));
                    successMessage.setText(msg);
                    successMessage.setTextColor(getResources().getColor(R.color.app_color));
                    progressBarAadharCard.setProgress(0);
                    statusAadharCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                    isAaadharuploaded = false;
                    isAttachAadharCardClicked = false;
                }
                case DOCUMENT_TYPE_PASSBOOK: {
                    dltPassCard.setEnabled(true);
                    dltPassCard.setColorFilter(getResources().getColor(R.color.app_color));
                    succesProfileImgMeassage.setText(msg);
                    succesProfileImgMeassage.setVisibility(View.VISIBLE);
                    succesProfileImgMeassage.setTextColor(getResources().getColor(R.color.app_color));
                    progressBarPassbook.setProgress(0);
                    statusPassCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                    isPassbookuploaded = false;
                    isAttachPassbookClicked = false;
                }
                default: {
                    successMessage.setText(msg);
                    successMessage.setTextColor(getResources().getColor(R.color.app_color));
                    isAttachPANCardClicked = false;
                    isAttachAadharCardClicked = false;
                    isAttachPassbookClicked = false;
                    isAttachProfilePicClicked = false;
                }
            }
        } else {
            successMessage.setText("Network not available");
            successMessage.setTextColor(getResources().getColor(R.color.app_color));
            String documentType = docName;
            if (documentType != null && !documentType.isEmpty() && documentType.equals(DOCUMENT_TYPE_PANCARD)) {
                statusPanCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                dltPanCard.setEnabled(true);
                dltPanCard.setColorFilter(getResources().getColor(R.color.app_color));
                isPANUploaded = false;
                isAttachPANCardClicked = false;
            } else if (documentType != null && !documentType.isEmpty() && documentType.equals(DOCUMENT_TYPE_ADHARCARD)) {
                statusAadharCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                dltAadharCard.setEnabled(true);
                dltAadharCard.setColorFilter(getResources().getColor(R.color.app_color));
                isAaadharuploaded = false;
                isAttachAadharCardClicked = false;
            } else if (documentType != null && !documentType.isEmpty() && documentType.equals(DOCUMENT_TYPE_PASSBOOK)) {
                statusPassCard.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                dltPassCard.setEnabled(true);
                dltPassCard.setColorFilter(getResources().getColor(R.color.app_color));
                isPassbookuploaded = false;
                isAttachPassbookClicked = false;
            } else if (documentType != null && !documentType.isEmpty() && documentType.equals(DOCUMENT_TYPE_PROFILE_PHOTO)) {
                statusProfilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                dltProfilePhoto.setEnabled(true);
                dltProfilePhoto.setColorFilter(getResources().getColor(R.color.app_color));
                isProfilePicuploaded = false;
                isAttachProfilePicClicked = false;
            }
        }
    }


//============================================================

    private File createImageFile4() {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                displayMessage(getBaseContext(), "Unable to create directory.");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

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
        currentPhotoPath = mCurrentPhotoPath;
        return image;
    }

    private void displayMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private void captureImage2() {
        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile4();
            if (photoFile != null) {
                LogUtils.e(photoFile.getAbsolutePath());
                Uri photoURI = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
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
                    LogUtils.e(photoFile.getAbsolutePath());

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this, "smarter.uearn.money.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            if (requestCode == 0) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                }
            }
            switch (requestCode) {
                case RequestPermissionCode:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else {

                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
