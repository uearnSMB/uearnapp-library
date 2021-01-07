package smarter.uearn.money.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;

import static smarter.uearn.money.utils.AppConstants.DOCUMENT_TYPE_SERVICE_AGREEMENT;

public class OnboardingServiceAgreementActivity extends AppCompatActivity implements View.OnClickListener {

    private Button submitButton;
    List<String> checkboxValues = new ArrayList<>();
    private TextView interviewsButton, onboardingButton, trainingButton;
    private Button nextButton;
    private Activity activity = null;
    private WebView view = null;
    private ImageView imageProgressOne, imageProgressTwo;
    private TextView headerTitle;
    private ImageView profileImage, profileImageBack, image_sync;
    private LinearLayout notificationLayout;


    String currentPhotoPath;
    private Uri imageFilePath;
    private static final int FILE_SELECT_CODE = 110;
    public static final int RequestPermissionCode = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView imgAttach_Sign;
    private ImageView imgDelete_Sign;
    private ImageView imgStatus_Sign;

    private ProgressBar progress_sign;
    TextView tvSuccesProfileImg;
    private int progressStatus = 0;
    private int documentsCount = 0;
    private boolean isScanCopyUploaded;
    private CheckBox agreeCheckbox;


    //========New Parms for caoture Image=====================
    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;

    String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME = "UEARN";
    //=========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_agreement);
        changeStatusBarColor(this);
        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("Service Agreement");
        headerTitle.setTextSize(18);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileImage.setBackgroundResource(R.drawable.ic_menu);
        profileImage.setVisibility(View.VISIBLE);
        profileImageBack = (ImageView) findViewById(R.id.profile_image_back);
        profileImageBack.setVisibility(View.GONE);
        notificationLayout = (LinearLayout) findViewById(R.id.ll_notification);
        notificationLayout.setOnClickListener(this);
        activity = this;

        image_sync = findViewById(R.id.image_sync);
        image_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                settingsApi(userId);
                Animation rotation = AnimationUtils.loadAnimation(OnboardingServiceAgreementActivity.this, R.anim.button_rotate);
                rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                v.startAnimation(rotation);
            }
        });

        nextButton = findViewById(R.id.btnNext);
        nextButton.setOnClickListener(this);
        nextButton.setText("SUBMIT");
        nextButton.setTextSize(20);
        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);

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

        imageProgressOne = findViewById(R.id.img_ProgressOne);
        imageProgressTwo = findViewById(R.id.img_Progress3);


        imgAttach_Sign = findViewById(R.id.imgAttach_Sign);
        imgDelete_Sign = findViewById(R.id.imgDelete_Sign);
        imgStatus_Sign = findViewById(R.id.imgStatus_Sign);
        progress_sign = findViewById(R.id.progress_sign);
        tvSuccesProfileImg = findViewById(R.id.tvSuccesProfileImg);

        imgAttach_Sign.setOnClickListener(this);
        imgDelete_Sign.setOnClickListener(this);

        imageProgressOne.setImageDrawable(getResources().getDrawable(R.drawable.bg_circle_status_active));
        imageProgressTwo.setImageDrawable(getResources().getDrawable(R.drawable.bg_circle_status_active));

        getServiceUserAgreement();

        nextButton.setEnabled(false);
        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);

        view = (WebView) this.findViewById(R.id.webView);
        view.getSettings().setJavaScriptEnabled(true);

        agreeCheckbox = findViewById(R.id.agreeTerms);
        agreeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                     @Override
                                                     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                         if (isChecked) {
                                                             agreeCheckbox.setChecked(true);
                                                         }
                                                         if (agreeCheckbox.isEnabled() && agreeCheckbox.isChecked() && isScanCopyUploaded) {
                                                             nextButton.setEnabled(true);
                                                             nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                                                             nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                                                         } else {
                                                             nextButton.setEnabled(false);
                                                             nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                                                             nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
                                                         }
                                                     }
                                                 }
        );

        getServiceAgreementKYC();
    }

    private void getServiceAgreementKYC() {
        try {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            KycDocuments kyc_SERVICE_AGREEMENT = ApplicationSettings.getKycDocuments(this, AppConstants.DOCUMENT_KYC_SERVICE_AGREEMENT);
            if (kyc_SERVICE_AGREEMENT != null && !Utils.isStringEmpty(userId)) {
                LogUtils.e(kyc_SERVICE_AGREEMENT.toString());
                addKycAgreement(kyc_SERVICE_AGREEMENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addKycAgreement(KycDocuments kyc_service_agreement) {
        if (!Utils.isStringEmpty(kyc_service_agreement.getDocUrl()) && kyc_service_agreement.getUploaded()) {
            isScanCopyUploaded = true;
            agreeCheckbox.setChecked(true);
            imgDelete_Sign.setEnabled(true);
            imgAttach_Sign.setEnabled(false);
            runOnUiThread(new Runnable() {
                public void run() {
                    if (tvSuccesProfileImg != null) {
                        tvSuccesProfileImg.setText("Uploading file. Please wait...");
                        tvSuccesProfileImg.setTextColor(getResources().getColor(R.color.blue));
                    }
                    showProgress(progress_sign);
                }
            });
            imgDelete_Sign.setColorFilter(getResources().getColor(R.color.app_color));
            imgStatus_Sign.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
            if (agreeCheckbox.isEnabled() && agreeCheckbox.isChecked() && isScanCopyUploaded) {
                nextButton.setEnabled(true);
                nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
            }
        }
    }

    @Override
    protected void onResume() {
        getServiceAgreementKYC();
        SmarterSMBApplication.setCurrentActivity(this);
        super.onResume();
    }

    public void restoreActionBar(String mTitle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7030A0")));
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
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnNext:
                navigateToHomeActivity();
                break;
            case R.id.ll_notification:
                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                settingsApi(userId);
                break;

            case R.id.imgAttach_Sign:
                startDialog();
                break;

            case R.id.imgDelete_Sign:
                try {
                    ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_SERVICE_AGREEMENT, null);
                    imgDelete_Sign.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_attachment));
                    imgDelete_Sign.setColorFilter(getResources().getColor(R.color.delete_button_disable));
                    imgStatus_Sign.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_not_uploaded));
                    progress_sign.setProgress(0);
                    imgAttach_Sign.setEnabled(true);
                    tvSuccesProfileImg.setText("");
                    nextButton.setEnabled(false);
                    nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
                    isScanCopyUploaded = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
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

    private void navigateToHomeActivity() {
        SmarterSMBApplication.isServiceAgreementDone = true;
        Intent intent = new Intent(this, VoiceTestCompleteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void getServiceUserAgreement() {
        if (CommonUtils.isNetworkAvailable(this)) {
            if (activity != null && !activity.isFinishing()) {
                new APIProvider.GetServiceUserAgreement("", 0, activity, "Please wait..", new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        if (data != null && !data.isEmpty()) {
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                if (jsonObject.has("success")) {
                                    String successObj = jsonObject.getString("success");
                                    if (successObj != null && !successObj.isEmpty()) {
                                        view.loadDataWithBaseURL(null, successObj, "text/html", "utf-8", null);
                                    }
                                }
                            } catch (Exception e) {

                            }
                        } else {

                        }
                    }
                }).requestForDataCall();
            }
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startDialog() {
        final Dialog networkDialog = CustomTwoButtonDialog.capturePhotoType(this, "Upload option", "Do you want to choose file from gallery or upload picture?");
        TextView btnNo = networkDialog.findViewById(R.id.btn_no);
        btnNo.setText("CAMERA");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    captureImage();
                } else {
                    captureImage2();
                }
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                imageFilePath = FileProvider.getUriForFile(this, "smarter.uearn.money.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFilePath);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void browseFileFromChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
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

    class UploadBitmap extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String imageFile = new File(currentPhotoPath).toString();
            String fileName = new File(currentPhotoPath).getName();
            if (!imageFile.isEmpty()) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (tvSuccesProfileImg != null) {
                            tvSuccesProfileImg.setText("Uploading file. Please wait...");
                            tvSuccesProfileImg.setTextColor(getResources().getColor(R.color.blue));
                        }
                        showProgress(progress_sign);
                    }
                });

                uploadImageFile(imageFile, DOCUMENT_TYPE_SERVICE_AGREEMENT, fileName, "");
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
                    tvSuccesProfileImg.setText("Network not available");
                    tvSuccesProfileImg.setTextColor(getResources().getColor(R.color.app_color));
                    imgStatus_Sign.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
                    imgDelete_Sign.setEnabled(true);
                    imgDelete_Sign.setColorFilter(getResources().getColor(R.color.app_color));
                    isScanCopyUploaded = false;
                }
            });
        }
    }

    private void handleKYCResult(String kycDocument, String msg, boolean resultStatus) {
        if (kycDocument != null && resultStatus) {
            imgDelete_Sign.setEnabled(true);
            imgDelete_Sign.setColorFilter(getResources().getColor(R.color.app_color));
            imgStatus_Sign.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_uploaded));
            isScanCopyUploaded = true;
            tvSuccesProfileImg.setText(msg);
            tvSuccesProfileImg.setVisibility(View.VISIBLE);
            tvSuccesProfileImg.setTextColor(getResources().getColor(R.color.success_color));
            documentsCount++;
            if (agreeCheckbox.isEnabled() && agreeCheckbox.isChecked() && isScanCopyUploaded) {
                nextButton.setEnabled(true);
                nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
            }
        } else if (kycDocument != null && resultStatus == false) {
            imgDelete_Sign.setEnabled(true);
            imgDelete_Sign.setColorFilter(getResources().getColor(R.color.app_color));
            tvSuccesProfileImg.setText(msg);
            tvSuccesProfileImg.setVisibility(View.VISIBLE);
            tvSuccesProfileImg.setTextColor(getResources().getColor(R.color.app_color));
            progress_sign.setProgress(0);
            imgStatus_Sign.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
            isScanCopyUploaded = false;
        } else {
            imgDelete_Sign.setEnabled(true);
            imgDelete_Sign.setColorFilter(getResources().getColor(R.color.app_color));
            tvSuccesProfileImg.setText("Network not available");
            tvSuccesProfileImg.setTextColor(getResources().getColor(R.color.app_color));
            imgStatus_Sign.setImageDrawable(getResources().getDrawable(R.drawable.ic_attachment_failed));
            isScanCopyUploaded = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 1200, 800, false);
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

    private void saveKYCData(String documentType, boolean isSuccess, KycDocuments kycDocuments) {
        try {
            if (kycDocuments != null && !Utils.isStringEmpty(documentType)) {
                switch (documentType) {
                    case DOCUMENT_TYPE_SERVICE_AGREEMENT: {
                        ApplicationSettings.setKycDocument(this, AppConstants.DOCUMENT_KYC_SERVICE_AGREEMENT, kycDocuments);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createImageFile4() {
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
