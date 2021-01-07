package smarter.uearn.money.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.WelcomeActivity;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIAdapter;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.UearnEditText;
import smarter.uearn.money.utils.Validation;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SignUpFragment extends Fragment implements View.OnClickListener, API_Response_Listener<SmartUser>, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    LinearLayout llRoot;
    private UearnEditText nameEditText, mobileEditText, emailEditText, passwordEditText,confirmPassword, languageEditText, educationalQualificationEditText, workExperienceEditText, voiceSupportExpEditText, hardwareEditText;
    RadioGroup genderRadioGroup, experienceRadioGroup, businessProcessRadioGroup;
    String genderType = "F";
    String customerSupportExperience = "Yes";
    String businessProcess = "Voice";
    Button btnSignUp;
    TextView tvTerms, accountTv, logIn, agrementTv;
    TextView error_name_tv, error_mobile_no_tv, error_email_tv, error_password_tv,error_ConPassword_tv, error_language_tv, error_qualification_tv, error_experience_tv, error_experience_in_voice_tv, error_choose_setup_tv;
    public static int GET_TEAM_MEMBERS = 535;
    private boolean isName, isMobileNo, isEmail, isPassword;
    private String language, education, workExperience, otherExperience, hardware, additionalData;
    private ArrayList<String> selectedLanguages = null;
    private ArrayList<String> selectedHardwares = null;
    private Activity activity;

    private FirebaseAnalytics mFirebaseAnalytics;
    private TextView versionText;
    AlertDialog alert;
    private LinearLayout languageMainLayout;
    private LinearLayout hardwareMainLayout;
    private View view;
    HashMap<String, View> languageMap = new HashMap<>();
    boolean[] checkedLanguageItems = null;
    LinkedHashMap<String, View> hardwareMap = new LinkedHashMap<>();
    boolean[] checkedHardwareItems = null;
    private ArrayList<Integer> mSelectedLanguageItems = null;
    private ArrayList<Integer> mSelectedHardwareItems = null;
    String[] languages = null;
    String[] qualifications = null;
    String[] years = null;
    String[] hardwares = null;
    LinearLayout languageContainerLayout;
    LinearLayout hardwareContainerLayout;
    private TextView languageText;
    private TextView hardwareText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        CommonUtils.permissionsCheck(getActivity());
        ServiceUserProfile.getAuth(getActivity());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        languages = getLanguages();
        if (languages != null) {
            checkedLanguageItems = new boolean[languages.length];
            int size = languages.length - 1;
            for (int i = 0; i < size; i++) {
                checkedLanguageItems[i] = false;
            }
        }

        qualifications = getEducationQualifications();
        years = getNoOfYears();

        hardwares = getHardwares();
        if (hardwares != null) {
            checkedHardwareItems = new boolean[hardwares.length];
            int size = hardwares.length - 1;
            for (int i = 0; i < size; i++) {
                checkedHardwareItems[i] = false;
            }
        }

        initializeUiElements(view);
        return view;
    }

    private void initializeUiElements(View v) {

        versionText = v.findViewById(R.id.versionText);
        String appVersion = "v " + AppConstants.APP_VERSION;
        versionText.setText(appVersion);

        isName = false;
        isMobileNo = false;
        isEmail = false;
        isPassword = false;

        llRoot = v.findViewById(R.id.llRoot);
        accountTv = v.findViewById(R.id.accountTv);
        logIn = v.findViewById(R.id.logIn);
        logIn.setOnClickListener(this);

        error_name_tv = v.findViewById(R.id.error_name_tv);
        error_mobile_no_tv = v.findViewById(R.id.error_mobile_no_tv);
        error_email_tv = v.findViewById(R.id.error_email_tv);
        error_password_tv = v.findViewById(R.id.error_password_tv);
        error_ConPassword_tv = v.findViewById(R.id.error_ConPassword_tv);
        error_language_tv = v.findViewById(R.id.error_language_tv);
        error_qualification_tv = v.findViewById(R.id.error_qualification_tv);
        error_experience_tv = v.findViewById(R.id.error_work_experience_tv);
        error_experience_in_voice_tv = v.findViewById(R.id.error_voice_experience_tv);
        error_choose_setup_tv = v.findViewById(R.id.error_choose_setup_tv);

        languageMainLayout = v.findViewById(R.id.language_main_layout);
        hardwareMainLayout = v.findViewById(R.id.hardware_main_layout);

        nameEditText = v.findViewById(R.id.nameEditText);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textLength = nameEditText.getText().toString();
                if (textLength.length() == 0) {
                    isName = false;
                } else {
                    isName = true;
                    error_name_tv.setVisibility(View.GONE);
                }
                isName = textLength.length() > 0;
                checkAllUserDetailsEntered();
            }
        });
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }
                    showKeyBoard(nameEditText);
                    nameEditText.setHint("");
                    isName = true;
                }
            }
        });

        mobileEditText = v.findViewById(R.id.mobileEditText);
        mobileEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textLength = mobileEditText.getText().toString();
                if (textLength.length() == 0) {
                    isMobileNo = false;
                } else {
                    if (textLength.length() > 9 && textLength.length() < 11) {
                        error_mobile_no_tv.setVisibility(View.GONE);
                        isMobileNo = true;
                    }
                }
                isMobileNo = textLength.length() > 0;
                checkAllUserDetailsEntered();
            }
        });
        mobileEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }
                    if (nameEditText.getText().toString().length() == 0) {
                        error_name_tv.setVisibility(View.VISIBLE);
                    } else {
                        error_name_tv.setVisibility(View.GONE);
                    }
                    isMobileNo = true;
                    showKeyBoard(mobileEditText);
                    mobileEditText.setHint("");
                }
            }
        });

        emailEditText = v.findViewById(R.id.emailEditText);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textLength = emailEditText.getText().toString();
                if (textLength.length() == 0) {
                    isEmail = false;
                } else {
                    error_email_tv.setVisibility(View.GONE);
                    isEmail = true;
                }
                isEmail = textLength.length() > 0;
                checkAllUserDetailsEntered();
            }
        });
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }
                    if (!isName) {
                        error_name_tv.setVisibility(View.VISIBLE);
                    } else {
                        error_name_tv.setVisibility(View.GONE);
                    }
                    if (!isMobileNo) {
                        error_mobile_no_tv.setVisibility(View.VISIBLE);
                    } else {
                        error_mobile_no_tv.setVisibility(View.GONE);
                    }
                    isEmail = true;
                    showKeyBoard(emailEditText);
                    emailEditText.setHint("");
                }
            }
        });

        passwordEditText = v.findViewById(R.id.passwordEditText);

        confirmPassword = v.findViewById(R.id.etConfirmPassword);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textLength = passwordEditText.getText().toString();

                if (textLength.length() == 0) {
                    isPassword = false;
                } else {
                    error_password_tv.setVisibility(View.GONE);
                    isPassword = true;
                }
                isPassword = textLength.length() > 0;
                checkAllUserDetailsEntered();
            }
        });

        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }
                    if (!isEmail) {
                        error_email_tv.setVisibility(View.VISIBLE);
                    } else {
                        error_email_tv.setVisibility(View.GONE);
                    }
                    if (!isName) {
                        error_name_tv.setVisibility(View.VISIBLE);
                    } else {
                        error_name_tv.setVisibility(View.GONE);
                    }
                    if (!isMobileNo) {
                        error_mobile_no_tv.setVisibility(View.VISIBLE);
                    } else {
                        error_mobile_no_tv.setVisibility(View.GONE);
                    }
                    isPassword = true;
                    showKeyBoard(passwordEditText);
                    passwordEditText.setHint("");
                }
            }
        });
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }
                    if (!isEmail) {
                        error_email_tv.setVisibility(View.VISIBLE);
                    } else {
                        error_email_tv.setVisibility(View.GONE);
                    }
                    if (!isName) {
                        error_name_tv.setVisibility(View.VISIBLE);
                    } else {
                        error_name_tv.setVisibility(View.GONE);
                    }
                    if (!isMobileNo) {
                        error_mobile_no_tv.setVisibility(View.VISIBLE);
                    } else {
                        error_mobile_no_tv.setVisibility(View.GONE);
                    }
                    isPassword = true;
                    showKeyBoard(passwordEditText);
                    passwordEditText.setHint("");
                }
            }
        });

        genderRadioGroup = v.findViewById(R.id.genderradiogroup);
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    String selectedGenderText = checkedRadioButton.getText().toString();
                    if (selectedGenderText != null && !selectedGenderText.isEmpty()) {
                        if (selectedGenderText.equalsIgnoreCase("Female")) {
                            genderType = "F";
                        } else if (selectedGenderText.equalsIgnoreCase("Male")) {
                            genderType = "M";
                        } else {
                            genderType = "O";
                        }
                    }
                }
            }
        });

        mSelectedLanguageItems = new ArrayList<Integer>();
        selectedLanguages = new ArrayList<String>();

        languageEditText = v.findViewById(R.id.languageEditText);
        languageEditText.setText("Select Languages");
        language = languageEditText.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            languageEditText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(languageEditText, false);
            } catch (Exception e) {

            }
        }
        languageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }
                    showLanguageAlertDialog();
                }
            }
        });
        languageEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showLanguageAlertDialog();
            }
        });

        mSelectedHardwareItems = new ArrayList<Integer>();
        selectedHardwares = new ArrayList<String>();

        hardwareEditText = v.findViewById(R.id.hardwareEditText);
        hardwareEditText.setText("Select Setup");
        hardware = hardwareEditText.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hardwareEditText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(hardwareEditText, false);
            } catch (Exception e) {

            }
        }
        hardwareEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }
                    showHardwareAlertDialog();
                }
            }
        });
        hardwareEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showHardwareAlertDialog();
            }
        });

        educationalQualificationEditText = v.findViewById(R.id.educationalQualificationEditText);
        educationalQualificationEditText.setText("Select Qualification");
        education = educationalQualificationEditText.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            educationalQualificationEditText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(educationalQualificationEditText, false);
            } catch (Exception e) {

            }
        }
        educationalQualificationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    educationalQualificationEditText.setText("Select Qualification");
                    education = educationalQualificationEditText.getText().toString();
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    String titleText = "Select Qualification";
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
                    SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                    ssBuilder.setSpan(
                            foregroundColorSpan,
                            0,
                            titleText.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    builder.setTitle(ssBuilder)
                            .setItems(qualifications, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    educationalQualificationEditText.setText(qualifications[item]);
                                    education = educationalQualificationEditText.getText().toString();
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
            }
        });
        educationalQualificationEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String titleText = "Select Qualification";
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                ssBuilder.setSpan(
                        foregroundColorSpan,
                        0,
                        titleText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                builder.setTitle(ssBuilder)
                        .setItems(qualifications, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                educationalQualificationEditText.setText(qualifications[item]);
                                education = educationalQualificationEditText.getText().toString();
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
        });

        workExperienceEditText = v.findViewById(R.id.workExperienceEditText);
        workExperienceEditText.setText("Select Experience");
        workExperience = workExperienceEditText.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            workExperienceEditText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(workExperienceEditText, false);
            } catch (Exception e) {

            }
        }
        workExperienceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    workExperienceEditText.setText("Select Experience");
                    workExperience = workExperienceEditText.getText().toString();
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    String titleText = "Select Experience";
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
                    SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                    ssBuilder.setSpan(
                            foregroundColorSpan,
                            0,
                            titleText.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    builder.setTitle(ssBuilder)
                            .setItems(years, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    workExperienceEditText.setText(years[item]);
                                    workExperience = workExperienceEditText.getText().toString();
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
            }
        });
        workExperienceEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String titleText = "Select Experience";
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                ssBuilder.setSpan(
                        foregroundColorSpan,
                        0,
                        titleText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                builder.setTitle(ssBuilder)
                        .setItems(years, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                workExperienceEditText.setText(years[item]);
                                workExperience = workExperienceEditText.getText().toString();
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
        });

        experienceRadioGroup = v.findViewById(R.id.customersupportradiogroup);
        experienceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    customerSupportExperience = checkedRadioButton.getText().toString();
                }
            }
        });

        businessProcessRadioGroup = v.findViewById(R.id.businessprocessradiogroup);
        businessProcessRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    businessProcess = checkedRadioButton.getText().toString();
                }
            }
        });

        voiceSupportExpEditText = v.findViewById(R.id.voiceSupportExpEditText);
        voiceSupportExpEditText.setText("Select Experience");
        otherExperience = voiceSupportExpEditText.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            voiceSupportExpEditText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(voiceSupportExpEditText, false);
            } catch (Exception e) {

            }
        }
        voiceSupportExpEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    voiceSupportExpEditText.setText("Select Experience");
                    otherExperience = voiceSupportExpEditText.getText().toString();
                    if (alert != null) {
                        alert.dismiss();
                        alert = null;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    String titleText = "Select Experience";
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
                    SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                    ssBuilder.setSpan(
                            foregroundColorSpan,
                            0,
                            titleText.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    builder.setTitle(ssBuilder)
                            .setItems(years, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    voiceSupportExpEditText.setText(years[item]);
                                    otherExperience = voiceSupportExpEditText.getText().toString();
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
            }
        });
        voiceSupportExpEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String titleText = "Select Experience";
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                ssBuilder.setSpan(
                        foregroundColorSpan,
                        0,
                        titleText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                builder.setTitle(ssBuilder)
                        .setItems(years, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                voiceSupportExpEditText.setText(years[item]);
                                otherExperience = voiceSupportExpEditText.getText().toString();
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
        });

        btnSignUp = v.findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
        btnSignUp.setText("REGISTER");
        btnSignUp.setTextSize(20);
        btnSignUp.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        btnSignUp.setBackgroundResource(R.drawable.red_rounded_corner);
        tvTerms = v.findViewById(R.id.tvTerms);
        tvTerms.setOnClickListener(this);
        agrementTv = v.findViewById(R.id.agrementTv);
    }

    private void addLanguageViewToLayout(String displayName, final int position) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (!languageMap.containsKey(displayName) && languageMap.size() % 4 == 0) {
            View containerLayoutView = inflater.inflate(R.layout.layout_container, languageMainLayout, false);
            languageContainerLayout = containerLayoutView.findViewById(R.id.lang_container);
            languageContainerLayout.setTag("lv" + languageMap.size() / 4);
            languageEditText.setMaxLines(languageMap.size() / 4 + 1);
            languageMainLayout.addView(languageContainerLayout);

        } else {
            languageEditText.setLines(languageMap.size() / 4 + 1);
            languageContainerLayout = languageMainLayout.findViewWithTag("lv" + languageMap.size() / 4);
        }
        view = inflater.inflate(R.layout.custom_language_text, languageContainerLayout, false);
        languageText = (TextView) view.findViewById(R.id.language_text);
        ImageView ivClose = (ImageView) view.findViewById(R.id.close_icon);
        if (!languageMap.containsKey(displayName)) {
            languageText.setText(displayName);
            languageText.setTag(position);
            view.setTag("view" + position);
            ivClose.setTag(displayName + "_" + position);
            languageContainerLayout.addView(view);
            languageMap.put(displayName, view);
        }
        ((ImageView) view.findViewById(R.id.close_icon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language = null;
                int position = -2;
                String tag = (String) v.getTag();
                if (tag != null) {
                    language = tag.split("_")[0];
                    position = Integer.parseInt(tag.split("_")[1]);
                    checkedLanguageItems[position] = false;
                    mSelectedLanguageItems.remove((Object) position);
                    selectedLanguages.remove(language);
                    removeLanguageViewFromLayout(language, position);

                    if (mSelectedLanguageItems.size() > 0) {
                        languageEditText.setText(" ");
                        selectedLanguages = new ArrayList<>();
                        languageMap = new HashMap<>();
                        languageMainLayout.removeAllViews();

                    }
                    for (Integer i : mSelectedLanguageItems) {
                        selectedLanguages.add(languages[i]);
                        addLanguageViewToLayout(languages[i], i);
                    }
                }
            }


        });
    }

    private void removeLanguageViewFromLayout(String displayName, final int position) {
        for (Map.Entry<String, View> entry : languageMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(displayName)) {
                int totTags = languageMap.size() / 4;
                for (int i = 0; i <= totTags; i++) {
                    LinearLayout containerLayout = languageMainLayout.findViewWithTag("lv" + i);
                    LinearLayout view = containerLayout.findViewWithTag("view" + position);
                    if (view != null) {
                        containerLayout.removeView(view);
                        break;
                    }
                }
            }
        }
        removeLanguageKeyFromMap(displayName);
        if (languageMap.size() == 0) {
            languageEditText.setText("Select Languages");
            language = languageEditText.getText().toString();
        }
    }

    private void removeLanguageKeyFromMap(String languageText) {
        Iterator<String> iterator = languageMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.contains(languageText)) {
                iterator.remove();
            }
        }
    }

    public void showLanguageAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String titleText = "Select Languages";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.setTitle(ssBuilder)
                .setMultiChoiceItems(languages, checkedLanguageItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            languageEditText.setText(" ");
                            mSelectedLanguageItems.add(indexSelected);
                            selectedLanguages.add(languages[indexSelected]);
                            if (!languageMap.containsKey(languages[indexSelected])) {
                                addLanguageViewToLayout(languages[indexSelected], indexSelected);
                            }
                        } else if (mSelectedLanguageItems.contains(indexSelected)) {
                            mSelectedLanguageItems.remove(Integer.valueOf(indexSelected));
                            selectedLanguages.remove(languages[indexSelected]);
                            if (languageMap.containsKey(languages[indexSelected])) {
                                removeLanguageViewFromLayout(languages[indexSelected], indexSelected);
                            }
                        }

                    }

                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String selectedIndex = "";
                        if (mSelectedLanguageItems.size() > 0) {
                            languageEditText.setText(" ");
                            selectedLanguages = new ArrayList<>();
                            languageMap = new HashMap<>();
                            languageMainLayout.removeAllViews();

                        }
                        for (Integer i : mSelectedLanguageItems) {
                            selectedIndex += i + ", ";
                            selectedLanguages.add(languages[i]);
                            addLanguageViewToLayout(languages[i], i);
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

    private void addHardwareViewToLayout(String displayName, final int position) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        boolean isSelectedCharOverlapping = false;
        int size = hardwareMap.size();
        if (displayName.length() > 29) {
            isSelectedCharOverlapping = true;
        } else {
            String lastEnteredKey = null;
            for (String entry : hardwareMap.keySet()) {
                lastEnteredKey = entry;
                if (entry.length() > 29) {
                    size = size + 1;
                }
            }
            if (lastEnteredKey != null && lastEnteredKey.length() > 29) {
                isSelectedCharOverlapping = true;
            }
        }
        if (!hardwareMap.containsKey(displayName) && (size % 2 == 0 || isSelectedCharOverlapping)) {
            View containerLayoutView = inflater.inflate(R.layout.layout_container, hardwareMainLayout, false);
            hardwareContainerLayout = containerLayoutView.findViewById(R.id.lang_container);
            hardwareContainerLayout.setTag("lv" + size / 2);
            if (size == 0) {
                hardwareEditText.setLines(1);
            } else {
                hardwareEditText.setLines(hardwareEditText.getMaxLines() + 1);
                Log.i("line", String.valueOf(hardwareEditText.getMaxLines()));
            }

            hardwareMainLayout.addView(hardwareContainerLayout);

        } else {
            hardwareContainerLayout = hardwareMainLayout.findViewWithTag("lv" + size / 2);
        }
        view = inflater.inflate(R.layout.custom_hardware_text, hardwareContainerLayout, false);
        hardwareText = (TextView) view.findViewById(R.id.hardware_text);
        ImageView ivClose = (ImageView) view.findViewById(R.id.close_icon);
        if (!hardwareMap.containsKey(displayName)) {
            hardwareText.setText(displayName);
            hardwareText.setTag(position);
            view.setTag("view" + position);
            ivClose.setTag(displayName + "_" + position);
            hardwareContainerLayout.addView(view);
            hardwareMap.put(displayName, view);
        }

        ((ImageView) view.findViewById(R.id.close_icon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hardware = null;
                int position = -2;
                String tag = (String) v.getTag();
                if (tag != null) {
                    hardware = tag.split("_")[0];
                    position = Integer.parseInt(tag.split("_")[1]);
                    checkedHardwareItems[position] = false;
                    mSelectedHardwareItems.remove((Object) position);
                    selectedHardwares.remove(hardware);
                    removeHardwareViewFromLayout(hardware);

                    hardwareEditText.setText(" ");
                    selectedHardwares = new ArrayList<>();
                    hardwareMap = new LinkedHashMap<>();
                    hardwareMainLayout.removeAllViews();

                    for (Integer i : mSelectedHardwareItems) {
                        selectedHardwares.add(hardwares[i]);
                        addHardwareViewToLayout(hardwares[i], i);
                    }
                    if (hardwareMap.size() == 0) {
                        hardwareEditText.setLines(1);
                        hardwareEditText.setText("Select Setup");
                    }
                }
            }
        });
    }

    private void removeHardwareViewFromLayout(String displayName) {
        removeHardwareKeyFromMap(displayName);
        if (hardwareMap.size() == 0) {
            hardwareEditText.setText("Select Setup");
            hardware = hardwareEditText.getText().toString();
        }
    }

    private void removeHardwareKeyFromMap(String hardwareText) {
        Iterator<String> iterator = hardwareMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.contains(hardwareText)) {
                iterator.remove();
            }
        }
    }

    public void showHardwareAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String titleText = "Select Setup";
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.purple_color));
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                titleText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        builder.setTitle(ssBuilder)
                .setMultiChoiceItems(hardwares, checkedHardwareItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            hardwareEditText.setText(" ");
                            mSelectedHardwareItems.add(indexSelected);
                            selectedHardwares.add(hardwares[indexSelected]);
                            if (!hardwareMap.containsKey(hardwares[indexSelected])) {
                                addHardwareViewToLayout(hardwares[indexSelected], indexSelected);
                            }
                        } else if (mSelectedHardwareItems.contains(indexSelected)) {
                            mSelectedHardwareItems.remove(Integer.valueOf(indexSelected));
                            selectedHardwares.remove(hardwares[indexSelected]);
                            if (hardwareMap.containsKey(hardwares[indexSelected])) {
                                removeHardwareViewFromLayout(hardwares[indexSelected]);
                            }
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        hardwareEditText.setText(" ");
                        selectedHardwares = new ArrayList<>();
                        hardwareMap = new LinkedHashMap<>();
                        hardwareMainLayout.removeAllViews();
                        for (Integer i : mSelectedHardwareItems) {
                            selectedHardwares.add(hardwares[i]);
                            addHardwareViewToLayout(hardwares[i], i);
                        }
                        if (hardwareMap.size() == 0) {
                            hardwareEditText.setLines(1);
                            hardwareEditText.setText("Select Setup");
                            hardware = hardwareEditText.getText().toString();
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

    private void checkAllUserDetailsEntered() {
        if (isName && isMobileNo && isEmail && isPassword) {
            error_name_tv.setVisibility(View.GONE);
            error_mobile_no_tv.setVisibility(View.GONE);
            error_email_tv.setVisibility(View.GONE);
            error_password_tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent conditionsIntent = new Intent(Intent.ACTION_VIEW);
        Uri termsAndConditionsUrl = null;

        int id = view.getId();
        if (id == R.id.btnSignUp) {
            logEvent("Registration");
            if (CommonUtils.isNetworkAvailable(getContext())) {
                if (getValidBasicInfo()) {
                    String username, email, phoneNo, passwd;
                    username = nameEditText.getText().toString();
                    email = emailEditText.getText().toString();
                    phoneNo = "+91" + mobileEditText.getText().toString();
                    passwd = passwordEditText.getText().toString();

                    if (selectedLanguages != null && selectedLanguages.size() > 0) {
                        String[] languages = selectedLanguages.toArray(new String[selectedLanguages.size()]);
                        if (languages != null && languages.length > 0) {
                            StringBuilder sb = new StringBuilder();
                            for (String s : languages) {
                                sb.append(s).append(",");
                            }
                            language = sb.deleteCharAt(sb.length() - 1).toString();
                        }
                    }

                    JSONObject otherExpObject = null;
                    try {
                        otherExpObject = new JSONObject();
                        otherExpObject.put("voice_support", voiceSupportExpEditText.getText().toString());
                        otherExpObject.put("customer_support_experience", customerSupportExperience);
                    } catch (Exception e) {

                    }

                    otherExperience = otherExpObject.toString();

                    if (selectedHardwares != null && selectedHardwares.size() > 0) {
                        String[] hardwares = selectedHardwares.toArray(new String[selectedHardwares.size()]);
                        if (hardwares != null && hardwares.length > 0) {
                            StringBuilder sb = new StringBuilder();
                            for (String s : hardwares) {
                                sb.append(s).append(",");
                            }
                            hardware = sb.deleteCharAt(sb.length() - 1).toString();
                        }
                    }

                    JSONObject additionalDataObject = null;
                    try {
                        additionalDataObject = new JSONObject();
                        additionalDataObject.put("setup_options", hardware);
                    } catch (Exception e) {

                    }

                    additionalData = additionalDataObject.toString();

                    SmartUser smartUser = new SmartUser(username, email, phoneNo, passwd, "smb", genderType, language, education, workExperience, businessProcess, otherExperience, additionalData);

                    if (activity == null) {
                        Activity activity1 = getActivity();
                        if (activity1 == null) {
                            activity = ((Activity) getContext());
                        } else {
                            activity = activity1;
                        }
                    }
                    boolean isPermissionEnabled = CommonUtils.permissionsCheck(getActivity());
                    if (isPermissionEnabled) {
                        registration(smartUser);
                    }
                }
            } else {
                Toast.makeText(getActivity(), "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.tvTerms) {
            if (alert != null) {
                alert.dismiss();
                alert = null;
            }
            logEvent("Terms");
            termsAndConditionsUrl = Uri.parse(Urls.SERVER_ADDRESS + "/terms");
            conditionsIntent.setData(termsAndConditionsUrl);
            startActivity(conditionsIntent);
        } else if (id == R.id.logIn) {
            if (alert != null) {
                alert.dismiss();
                alert = null;
            }
            logEvent("Sign In");
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.authentication_container, new SignInFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void registration(SmartUser smartUser) {
        new APIProvider.SmartUser_Post(smartUser, 1, getActivity(), "Creating User...", this).call();
    }

    private boolean isValidGender() {
        return true;
    }

    private boolean isValidName() {
        if (!Validation.hasText(nameEditText, "Name")) {
            error_name_tv.setVisibility(View.VISIBLE);
            error_name_tv.setText(R.string.invalid_name);
            return false;
        } else {
            error_name_tv.setVisibility(View.GONE);
            error_mobile_no_tv.setVisibility(View.GONE);
            error_email_tv.setVisibility(View.GONE);
            error_password_tv.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean isValidPhoneNumber() {
        boolean isValidPhone = Validation.isValidPhoneNumber(mobileEditText, "IN");
        if (isValidPhone) {
            error_mobile_no_tv.setVisibility(View.GONE);
            return isValidPhone;
        } else {
            error_mobile_no_tv.setVisibility(View.VISIBLE);
            return isValidPhone;
        }
    }

    private boolean isValidEmail() {
        if (!Validation.isEmailAddress(emailEditText, true, "")) {
            error_email_tv.setVisibility(View.VISIBLE);
            error_email_tv.setText(R.string.invalid_email);
            return false;
        } else {
            error_email_tv.setVisibility(View.GONE);
            error_password_tv.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean isValidPassword() {
        if (!Validation.haspwdText(passwordEditText, "Password")) {
            error_password_tv.setVisibility(View.VISIBLE);
            error_password_tv.setText("Please provide password with 8 characters");
            return false;
        } else {
            error_password_tv.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean isValidConfirmPassword(){
        if(passwordEditText.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())){
            error_ConPassword_tv.setVisibility(View.GONE);
            return true;
        }else{
            error_ConPassword_tv.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private boolean isLanguageSelected() {
        if (selectedLanguages != null && selectedLanguages.size() > 0) {
            error_language_tv.setVisibility(View.GONE);
            return true;
        } else {
            error_language_tv.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private boolean isQualificationSelected() {
        if (education != null && !education.isEmpty() && education.equalsIgnoreCase("Select Qualification")) {
            error_qualification_tv.setVisibility(View.VISIBLE);
            return false;
        } else {
            error_qualification_tv.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean isWorkExperienceSelected() {
        if (workExperience != null && !workExperience.isEmpty() && workExperience.equalsIgnoreCase("Select Experience")) {
            error_experience_tv.setVisibility(View.VISIBLE);
            return false;
        } else {
            error_experience_tv.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean isExperienceInVoiceSelected() {
        if (otherExperience != null && !otherExperience.isEmpty() && otherExperience.equalsIgnoreCase("Select Experience")) {
            error_experience_in_voice_tv.setVisibility(View.VISIBLE);
            return false;
        } else {
            error_experience_in_voice_tv.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean isHardwareSetupSelected() {
        if (selectedHardwares != null && selectedHardwares.size() > 0) {
            error_choose_setup_tv.setVisibility(View.GONE);
            return true;
        } else {
            error_choose_setup_tv.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public boolean getValidBasicInfo() {
        boolean isValid_Gender = isValidGender();
        boolean isValid_Name = isValidName();
        boolean isValid_PhoneNumber = isValidPhoneNumber();
        boolean isValid_Email = isValidEmail();
        boolean isValidPassword = isValidPassword();
        boolean isLanguageSelected = isLanguageSelected();
        boolean isQualificationSelected = isQualificationSelected();
        boolean isWorkExperienceSelected = isWorkExperienceSelected();
        boolean isExperienceInVoiceSelected = isExperienceInVoiceSelected();
        boolean isHardwareSetupSelected = isHardwareSetupSelected();
        boolean isConfirmPassword = isValidConfirmPassword();
        return isValid_Gender && isValid_Name && isValid_PhoneNumber && isConfirmPassword && isValid_Email && isValidPassword && isLanguageSelected && isQualificationSelected && isWorkExperienceSelected && isExperienceInVoiceSelected && isHardwareSetupSelected;
    }

    @Override
    public void onComplete(SmartUser cmail_user, long request_code, int failure_code) {

        if (failure_code == APIAdapter.NO_FAILURE && cmail_user != null) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            cmail_user.doSave();
            SmarterSMBApplication.SmartUser = cmail_user;
            ApplicationSettings.putPref(AppConstants.NEW_USER, true);
            newUserPreferences();
            SmarterSMBApplication.SmartUser.setId(cmail_user.getId(), true);
            CommonOperations.subscribeNotificationsCall(true, getContext());
            removeAllOldCalendars();
            CommonOperations.getCalendarEvents(getContext(), "smartercalendar", 0, -7, +30);
            getStatusFromServer(cmail_user.getId());
            getTeamMembersCall(cmail_user.getId(), getContext());
            ServiceUserProfile.getUserSettings(getActivity());

            ApplicationSettings.putPref(AppConstants.APP_LOGOUT, false);
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("registration", "Newregistration");
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            getActivity().finish();
        } else if (failure_code != APIAdapter.NO_FAILURE) {
            String failure_msg = null;
            if (failure_code == APIProvider.INVALID_CREDENTIALS) {
                error_password_tv.setVisibility(View.VISIBLE);
                error_password_tv.setText(R.string.invalid_password);
            } else if (failure_code == APIProvider.INVALID_EMAIL) {
                error_email_tv.setVisibility(View.VISIBLE);
                error_email_tv.setText(R.string.invalid_email);
            } else if (failure_code == APIProvider.LOGIN_DENIED) {
                error_email_tv.setVisibility(View.VISIBLE);
                error_email_tv.setText(R.string.login_denied);
            } else if (failure_code == APIProvider.INVALID_LOGIN_METHOD) {
                error_email_tv.setVisibility(View.VISIBLE);
                error_email_tv.setText(R.string.invalid_email);
            } else if (failure_code == APIProvider.LOGIN_FAILED) {
                error_email_tv.setVisibility(View.VISIBLE);
                error_email_tv.setText(R.string.invalid_email);
            } else if (failure_code == APIProvider.EMAIL_ALREADY_EXISTS) {
                error_email_tv.setVisibility(View.VISIBLE);
                error_email_tv.setText(R.string.email_already_exists);
            } else if (failure_code == APIProvider.NUMBER_ALREADY_EXISTS) {
                error_mobile_no_tv.setVisibility(View.VISIBLE);
                error_mobile_no_tv.setText(R.string.number_already_exists);
            } else {
                if (activity == null) {
                    Activity activity1 = getActivity();
                    if (activity1 == null) {
                        activity = ((Activity) getContext());
                    } else {
                        activity = activity1;
                    }
                }
                CustomSingleButtonDialog.buildSingleButtonDialog("Register", "Registration Failed, Try Again!", activity, false);
            }
            openErrorDialog(failure_msg);
        }
    }

    private void showKeyBoard(UearnEditText editText) {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void newUserPreferences() {
        SmartUser smartUser = SmarterSMBApplication.SmartUser;
        smartUser.setAutoAccepteLeadsSettings(true);
        smartUser.setCallRecordStatus(false);
        smartUser.setGpsLocationSettings(false);
        smartUser.setCallTrackingSettings(true);
        smartUser.setRecordOnlyLeadNumbers(false);
        smartUser.setRecordOnlyTeamMemberNumbers(false);
        smartUser.setDonotRecordPrivateContacts(true);
        smartUser.setFollowUpCallsSettings(true);
        smartUser.setTeamCallSettings(false);
        smartUser.setSmsTrackingSettings(false);
        smartUser.setSmsTrackingAll(false);
        smartUser.setSmsTrackingLeads(true);
        smartUser.setSmsTrackingTeam(false);
        smartUser.setSmsTrackingPersonal(true);
        smartUser.setAutoFollowUpSettings(true);
        smartUser.setShowPopupSettings(true);
        ApplicationSettings.putPref("salescurrency", "INR");
        smartUser.setApplicationLogging(false);
        smartUser.setCall_sms_template("Hi, you may now access the call recording and more at ", true);
        smartUser.setbusiness_template("Nice talking to you. Please click here to see our company brochure ", true);
        smartUser.setJc_sms_template("Dear Customer, You can find the details of your order below ", true);
        smartUser.setJd_sms_template("Hi, Thank you for your interest. Please find the details here ", true);
        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 1);
        ApplicationSettings.putPref(AppConstants.RNR_CALLBACKS_NO, 3);
        ApplicationSettings.putPref(AppConstants.SHOW_POPUP, true);
    }

    private void removeAllOldCalendars() {
        MySql dbHelper = MySql.getInstance(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("remindertbNew", null, null);
        db.close();
    }

    private void getStatusFromServer(String id) {
        if (CommonUtils.isNetworkAvailable(getContext())) {
            new APIProvider.Get_Sales_Status(id, 22, new API_Response_Listener<SalesStageInfo>() {
                @Override
                public void onComplete(SalesStageInfo data, long request_code, int failure_code) {
                    if (data != null) {
                        data.dosave();
                        SmarterSMBApplication.salesStageInfo = data;
                    }
                }
            }).call();
        }
    }

    private void getTeamMembersCall(String id, final Context context) {
        if (CommonUtils.isNetworkAvailable(getContext())) {
            new APIProvider.Get_All_Team_Members(id, GET_TEAM_MEMBERS, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                @Override
                public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                    if (data != null) {
                        CommonUtils.saveTeamdataToLocalDB(context, data);
                    }
                }
            }).call();
        }
    }

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

        }
        return languages;
    }

    private String[] getEducationQualifications() {
        try {
            String qualification = ApplicationSettings.getPref(AppConstants.EDUCATION_QUALIFICATIONS, "");
            if (qualification != null && !qualification.isEmpty()) {
                JSONArray qualificationArray = new JSONArray(qualification);
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < qualificationArray.length(); i++) {
                    list.add(qualificationArray.getString(i));
                }
                qualifications = list.toArray(new String[list.size()]);
            }
        } catch (Exception e) {

        }
        return qualifications;
    }

    private String[] getNoOfYears() {
        try {
            String experience = ApplicationSettings.getPref(AppConstants.EXPERIENCE, "");
            if (experience != null && !experience.isEmpty()) {
                JSONArray experienceArray = new JSONArray(experience);
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < experienceArray.length(); i++) {
                    list.add(experienceArray.getString(i));
                }
                years = list.toArray(new String[list.size()]);
            }
        } catch (Exception e) {

        }
        return years;
    }

    private String[] getHardwares() {
        try {
            String hardware = ApplicationSettings.getPref(AppConstants.HARDWARE_SETUP, "");
            if (hardware != null && !hardware.isEmpty()) {
                JSONArray hardwareArray = new JSONArray(hardware);
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < hardwareArray.length(); i++) {
                    list.add(hardwareArray.getString(i));
                }
                hardwares = list.toArray(new String[list.size()]);
            }
        } catch (Exception e) {

        }
        return hardwares;
    }

    public void openErrorDialog(String messageText) {
        if (messageText != null) {
            if (messageText.contains("is already registered.")) {
                final Dialog exitDialog = CustomTwoButtonDialog.buildTwoButtonDialog(getActivity(), "UEARN Sign up", messageText);
                TextView btnNo = exitDialog.findViewById(R.id.btn_no);
                btnNo.setText("Goto Sign in");
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exitDialog.dismiss();

                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.authentication_container, new SignInFragment());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });

                TextView btnYes = exitDialog.findViewById(R.id.btn_yes);
                btnYes.setText("New Sign up");
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exitDialog.dismiss();

                    }
                });
                exitDialog.show();
            } else {
                CustomSingleButtonDialog.buildSingleButtonDialog("Registration failed", messageText, getActivity(), false);
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void logEvent(String name) {
        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_4);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, AnalyticsBundle);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
