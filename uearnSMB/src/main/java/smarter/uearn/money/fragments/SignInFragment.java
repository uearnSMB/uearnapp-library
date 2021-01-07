package smarter.uearn.money.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.DataLoadingActivity;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.models.LoginInfo;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.FileCleanUp;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIAdapter;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.UearnEditText;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.Validation;
import smarter.uearn.money.utils.Validations;
import smarter.uearn.money.utils.socialmedia.ServerAuthHelper;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private LinearLayout llRoot;
    private EditText emailEditText, passwordEditText;
    private Button btnLogin;
    private TextView forgot_passwordText, tvSignUp, error_email_tv, error_password_tv;
    private static final String DEFAULT_STORAGE_LOCATION = Environment.getExternalStorageDirectory() + "/callrecorder/";
    private FirebaseAnalytics mFirebaseAnalytics;
    private Bundle AnalyticsBundle = new Bundle();
    private Activity activity;
    private Drawable errorIcon;
    public static ArrayList<Double> pingResponselist = null;
    public static String pingResponse = "";
    public static CountDownTimer pingTimer;
    public static boolean timerStopped = false;
    private static boolean pingInProgress = false;
    private TextView versionText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int currentVer = android.os.Build.VERSION.SDK_INT;
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        CommonUtils.permissionsCheck(getActivity());
        initializeUiElements(view);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        File callRecorderDir = new File(DEFAULT_STORAGE_LOCATION);
        if (getContext() != null) {
            FileCleanUp fileCleanUp = new FileCleanUp(getContext(), callRecorderDir, "mydb");
            fileCleanUp.traverseToCleanCallRecorder();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initializeUiElements(View view) {

        versionText = view.findViewById(R.id.versionText);
        String appVersion = "v " + AppConstants.APP_VERSION;
        versionText.setText(appVersion);
        llRoot = view.findViewById(R.id.llRoot);
        llRoot.requestFocus();
        emailEditText = view.findViewById(R.id.emailEditText);
        error_email_tv = view.findViewById(R.id.error_email_tv);
        error_password_tv = view.findViewById(R.id.error_password_tv);
        errorIcon = getResources().getDrawable(R.drawable.error_icons);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
        passwordEditText = view.findViewById(R.id.passwordEditText);
        passwordEditText.setTransformationMethod(new PasswordTransformationMethod());

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyBoard(emailEditText);
                    emailEditText.setHint("");
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyBoard(passwordEditText);
                    passwordEditText.setHint("");
                }
            }
        });

        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnLogin.setText("LOGIN");
        btnLogin.setTextSize(20);
        btnLogin.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        btnLogin.setBackgroundResource(R.drawable.red_rounded_corner);

        tvSignUp = view.findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(this);

        RelativeLayout tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(this);

        forgot_passwordText = view.findViewById(R.id.forgot_passwordText);
        forgot_passwordText.setOnClickListener(this);

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            String email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
            emailEditText.setText(email);
        }
    }

    private void showKeyBoard(EditText editText) {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onClick(View view) {

        CommonOperations.hideKeyboard(getContext(), view);
        int id = view.getId();
        if (id == R.id.btnLogin) {
            logEvent("Login");
            if (Validations.isValidEmail(emailEditText.getText().toString())) {
                error_email_tv.setVisibility(View.GONE);

                if (passwordEditText.getText().toString().length() > 0) {
                    error_password_tv.setVisibility(View.GONE);
                    submitCredentials();
                } else {
                    error_password_tv.setVisibility(View.VISIBLE);
                }

            } else {
                error_email_tv.setVisibility(View.VISIBLE);
                error_password_tv.setVisibility(View.GONE);
                error_email_tv.setText(R.string.invalid_email);
            }
        } else if (id == R.id.tvSignUp) {
            logEvent("Sign Up");
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.authentication_container, new SignUpFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (id == R.id.forgot_passwordText) {
            logEvent("Forgot Password");
            error_password_tv.setVisibility(View.GONE);
            if (Validations.validateTextLayout(emailEditText, 1)) {
                final Dialog forgotPwdDialog = CustomTwoButtonDialog.resetPassword(getActivity());
                TextView btnYes = forgotPwdDialog.findViewById(R.id.btn_yes);
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forgotPwdDialog.dismiss();
                        if (CommonOperations.isNetworkConnected(getContext(), 1)) {
                            if (Validations.validateTextLayout(emailEditText, 1)) {
                                error_email_tv.setVisibility(View.GONE);
                                forgot_password(emailEditText.getText().toString());
                            } else {
                                error_email_tv.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

                forgotPwdDialog.show();
            } else {
                error_email_tv.setVisibility(View.VISIBLE);
            }
        }
    }

    public void submitCredentials() {
        if (checkValidation()) {
            if (CommonOperations.isNetworkConnected(getContext(), 1)) {
                boolean isPermissionEnabled = CommonUtils.permissionsCheck(getActivity());
                if (isPermissionEnabled) {
                    login_user(emailEditText.getText().toString(), passwordEditText.getText().toString(), "smb");
                }
            }
        }
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.hasText(passwordEditText, "Enter password")) {
            ret = false;
        }
        return ret;
    }

    private boolean checkValidation(EditText emailEditText) {
        boolean ret = true;
        if (!Validation.isEmailAddress(emailEditText, true, "Enter valid email"))
            ret = false;
        return ret;
    }

    private void pingAndCheckWifiConnectivityStatus() {
        String str = "";
        pingResponse = "";
        pingResponselist = new ArrayList<Double>();
        if (!pingInProgress) {
            startPingTimer();
        }
    }

    private void startPingTimer() {
        pingTimer = new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
                pingInProgress = true;
                String response = Utils.ping("www.google.com");
                if (response != null && !response.isEmpty()) {
                    if (response.contains(".")) {
                        int index = response.indexOf(".") + 2;
                        if (index > 0) {
                            String str = response.substring(0, index);
                            if (str != null && !str.isEmpty()) {
                                Double d = Double.valueOf(str);
                                pingResponselist.add(d);
                            }
                        }
                    } else {
                        if (response.contains(" ")) {
                            response = response.substring(0, response.indexOf(" "));
                            Double d = Double.valueOf(response);
                            pingResponselist.add(d);
                        } else {
                            Double d = Double.valueOf(response);
                            pingResponselist.add(d);
                        }
                    }
                }
            }

            public void onFinish() {
                if (pingTimer != null) {
                    pingTimer.cancel();
                    pingTimer = null;
                }
                timerStopped = true;
                double total = 0;
                if (pingResponselist != null && pingResponselist.size() > 0) {
                    for (int i = 0; i < pingResponselist.size(); i++) {
                        total = total + pingResponselist.get(i);
                    }
                    double average = total / pingResponselist.size();
                    average = Math.round(average * 100.0) / 100.0;
                    pingResponse = Double.toString(average);
                }
                pingInProgress = false;
                if (timerStopped) {
                    String str = "";
                    timerStopped = false;
                    if (pingResponse != null && !pingResponse.isEmpty() && pingResponse.contains(".")) {
                        str = pingResponse.split("\\.")[0];
                    } else {
                        str = pingResponse;
                    }
                    if (str != null && !str.isEmpty()) {
                        str = str.replaceAll("\\s", "");
                        str = Utils.getNumericString(str);
                        int result = Integer.parseInt(str);
                        if (result > 0 && result < 50) {
                            //Good
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Login Failed, Try Again!", activity, false);
                                }
                            });
                        } else if (result >= 50 && result <= 100) {
                            //Average
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Login Failed, Try Again!", activity, false);
                                }
                            });
                        } else if (result >= 100 && result <= 500) {
                            //Weak
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Login Failed, Try Again!", activity, false);
                                }
                            });
                        } else if (result > 500) {
                            CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Login Failed, please check your WiFi / internet connection.", activity, false);
                        }
                    } else {
                        //No internet
                        CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Login Failed, please check your WiFi / internet connection.", activity, false);
                    }
                } else {
                    //Good
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Login Failed, Try Again!", activity, false);
                        }
                    });
                }
            }
        }.start();
    }

    private void sendPingRequestToCheckNetworkSpeed() {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            pingAndCheckWifiConnectivityStatus();
        }
    }

    public void login_user(String emailid, String password, final String login_method) {
        LoginInfo loginInfo = new LoginInfo(emailid, password, login_method);
        if (activity == null) {
            Activity activity1 = getActivity();
            if (activity1 == null) {
                activity = ((Activity) getContext());
            } else {
                activity = activity1;
            }
        }
        APIProvider.CheckLogin checkLogin = new APIProvider.CheckLogin(loginInfo, 1, activity, "Verifying Credentials..", new API_Response_Listener<SmartUser>() {

            @Override
            public void onComplete(SmartUser data, long request_code, int failure_code) {
                if (failure_code != APIAdapter.NO_FAILURE) {
                    if (failure_code == APIProvider.INVALID_CREDENTIALS) {
                        error_password_tv.setVisibility(View.VISIBLE);
                        error_password_tv.setText(R.string.invalid_password);
                    } else if (failure_code == APIProvider.INVALID_EMAIL) {
                        error_email_tv.setVisibility(View.VISIBLE);
                        error_email_tv.setText(R.string.invalid_email);
                    } else if (failure_code == APIProvider.LOGIN_DENIED) {
                        error_email_tv.setVisibility(View.VISIBLE);
                        error_email_tv.setText(R.string.invalid_email);
                    } else if (failure_code == APIProvider.INVALID_LOGIN_METHOD) {
                        error_email_tv.setVisibility(View.VISIBLE);
                        error_email_tv.setText(R.string.invalid_email);
                    } else if (failure_code == APIProvider.LOGIN_FAILED) {
                        error_email_tv.setVisibility(View.VISIBLE);
                        error_email_tv.setText(R.string.invalid_email);
                    } else {
                        if (activity == null) {
                            Activity activity1 = getActivity();
                            if (activity1 == null) {
                                activity = ((Activity) getContext());
                            } else {
                                activity = activity1;
                            }
                        }
                        if (CommonUtils.isNetworkAvailable(getActivity())) {
                            CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Login Failed, Try Again!", activity, false);
                        } else {
                            CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Please check your internet connection", activity, false);
                        }
                    }
                } else {
                    String dbPath;

                    if (SmarterSMBApplication.domainURLNotMatching) {
                        SmarterSMBApplication.domainURLNotMatching = false;
                        login_user(emailEditText.getText().toString(), passwordEditText.getText().toString(), "smb");
                    } else if (data != null) {
                        data.doSave();
                        SmarterSMBApplication.SmartUser = data;
                        try {
                            AppConstants.USER_EMAIL_ADDRESS = emailEditText.getText().toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dbPath = removeAllOldCalendars();
                        ServerAuthHelper.accessTokenCall(0);
                        if (activity == null) {
                            Activity activity1 = getActivity();
                            if (activity1 == null) {
                                activity = ((Activity) getContext());
                            } else {
                                activity = activity1;
                            }
                        }
                        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, false);
                        CommonOperations.subscribeNotificationsCall(true, getApplicationContext());
                        String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
                        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
                            Intent intent = new Intent(activity, DataLoadingActivity.class);
                            intent.putExtra("EXTRA_ID", data.getId());
                            intent.putExtra("EXTRA_DB_PATH", dbPath);
                            intent.putExtra("FIRST_TIME", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getActivity().startActivity(intent);
                            activity.finish();
                        } else {
                            String activityToStart = SmarterSMBApplication.getNextActivityName();
                            Class<?> cl = null;
                            try {
                                cl = Class.forName(activityToStart);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            final Class<?> landingClass = cl;

                            Intent landingIntent = new Intent(getActivity(), landingClass);
                            startActivity(landingIntent);
                            getActivity().finish();
                        }
                    } else {
                        if (activity == null) {
                            Activity activity1 = getActivity();
                            if (activity1 == null) {
                                activity = ((Activity) getContext());
                            } else {
                                activity = activity1;
                            }
                        }
                        if (CommonUtils.isNetworkAvailable(getActivity())) {
                            CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Login Failed, Try Again!", activity, false);
                        } else {
                            CustomSingleButtonDialog.buildSingleButtonDialog("Login", "Please check your internet connection", activity, false);
                        }
                    }
                }
            }
        });
        checkLogin.call();
    }

    public String removeAllOldCalendars() {
        String dbPath = "";
        Context context = getActivity();
        if (context != null) {
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                dbPath = db.getPath();
                db.delete("remindertbNew", null, null);
                db.delete("JunkNumbers", null, null);
                db.delete("PrivateContacts", null, null);
                db.delete("CallData", null, null);
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
        return dbPath;
    }

    public void forgot_password(final String email) {
        APIProvider.ForgotPassword forgotPassword = new APIProvider.ForgotPassword(email, 1, getActivity(), "Processing your request.", new API_Response_Listener<Void>() {
            @Override
            public void onComplete(Void data, long request_code, int failure_code) {
                if (failure_code == -1) {
                    CustomSingleButtonDialog.buildSingleButtonDialog(getString(R.string.password_reset_text), "An email with link to reset your password has been sent, please check your email " + email, getActivity(), false);
                    ApplicationSettings.putPref("forgotPassword", email);
                } else if (failure_code == APIProvider.INVALID_CREDENTIALS) {
                    CustomSingleButtonDialog.buildSingleButtonDialog(getString(R.string.password_reset_text), "Password reset failed, please try after some time or contact your adminstrator.", getActivity(), false);
                }
            }
        });
        forgotPassword.call();
    }

    @Override
    public void onResume() {
        super.onResume();
        logEvent("Login");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public void logEvent(String name) {
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_3);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, AnalyticsBundle);
    }
}
