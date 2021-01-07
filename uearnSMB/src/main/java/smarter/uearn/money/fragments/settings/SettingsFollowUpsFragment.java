package smarter.uearn.money.fragments.settings;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.fourmob.timepicker.RadialPickerLayout;
import com.fourmob.timepicker.TimePickerDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created on 21/02/2017
 *
 * @author Dilip
 * @version 1.0
 */
public class SettingsFollowUpsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private View view;

    SmartUser smartUser = SmarterSMBApplication.SmartUser;

    TextView mLastSync, openTime, closeTime;
    EditText rnr_callback_et;
    private RelativeLayout mLayout, tLayout, wLayout, thursdayLayout, fLayout, satrdayLayout, sundayLayout;
    ImageView mSyncNow;
    public int check, final_start_hour, final_end_hour, final_start_min, final_end_min, final_start_year, final_end_year, final_start_month, final_end_month, final_start_dayOfMonth, final_end_dayOfMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_follow_ups, container, false);
        CommonOperations.setFragmentTitle(getActivity(), "Smarter Analytics");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {

        mLastSync = view.findViewById(R.id.tv_last_sync);
        openTime = view.findViewById(R.id.opentime);
        closeTime = view.findViewById(R.id.closetime);
        rnr_callback_et = view.findViewById(R.id.rnr_callback_et);
        rnr_callback_et.setInputType(InputType.TYPE_CLASS_NUMBER);

        openTime.setOnClickListener(this);
        closeTime.setOnClickListener(this);

        mLayout = view.findViewById(R.id.m_layout);
        tLayout = view.findViewById(R.id.t_layout);
        wLayout = view.findViewById(R.id.w_layout);
        thursdayLayout = view.findViewById(R.id.thursday_layout);
        fLayout = view.findViewById(R.id.f_layout);
        satrdayLayout = view.findViewById(R.id.strday_layout);
        sundayLayout = view.findViewById(R.id.sunday_layout);

        mLayout.setOnClickListener(this);
        tLayout.setOnClickListener(this);
        wLayout.setOnClickListener(this);
        thursdayLayout.setOnClickListener(this);
        fLayout.setOnClickListener(this);
        satrdayLayout.setOnClickListener(this);
        sundayLayout.setOnClickListener(this);

        checkDays();

        Calendar c = Calendar.getInstance();
        initTimeVariables(c);

        mLastSync.setText(ApplicationSettings.getPref(AppConstants.LAST_SYNC_SETTINGS, ""));

        mSyncNow = view.findViewById(R.id.ivSyncData);
        mSyncNow.setOnClickListener(this);

        Switch sAutoFollowUps = view.findViewById(R.id.switchAutoFollowUps);
        sAutoFollowUps.setOnCheckedChangeListener(this);

        if (smartUser.getAutoFollowUpSettings()) {
            sAutoFollowUps.setChecked(true);
        }

        Spinner rnr_callback_spinner = view.findViewById(R.id.rnr_callback_spinner);
        Spinner rnr_reminder_spinner = view.findViewById(R.id.rnr_reminders_spinner);
        if (getActivity() != null) {
            addSpinnerList(rnr_callback_spinner, rnr_reminder_spinner, getActivity());
        }

        //updateTv();
        checkTime();
        /*if (!ApplicationSettings.getPref(AppConstants.SUPERVISOR_EMAIL, "").equalsIgnoreCase(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""))) {*/
        /*String role = CommonUtils.getRole(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""), getContext());*/
        if(!ApplicationSettings.getPref(AppConstants.TEAM_ROLE,"").equalsIgnoreCase("superadmin")) {
            TextView tvLocked = view.findViewById(R.id.tvLocked);
            String administrator = tvLocked.getText().toString() + " " + ApplicationSettings.getPref(AppConstants.SUPERVISOR_EMAIL, "");
            tvLocked.setText(administrator);
            tvLocked.setVisibility(View.VISIBLE);

            sAutoFollowUps.setEnabled(false);
            rnr_callback_spinner.setEnabled(false);
            rnr_reminder_spinner.setEnabled(false);
            rnr_callback_et.setEnabled(false);

            mLayout.setEnabled(false);
            tLayout.setEnabled(false);
            wLayout.setEnabled(false);
            thursdayLayout.setEnabled(false);
            fLayout.setEnabled(false);
            satrdayLayout.setEnabled(false);
            sundayLayout.setEnabled(false);

            openTime.setEnabled(false);
            closeTime.setEnabled(false);

//            if (smartUser.getAutoFollowUpSettings()) {
//                sAutoFollowUps.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
//            }
        }
    }

 /*   private void setTime(String openTime2, String closeTime2, int value) {
        TimeZone tz = TimeZone.getTimeZone("UTC");

        try {
            if(value == 0) {
                *//*DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(tz);
                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                Date date = df.parse(openTime2);
                df.setTimeZone(TimeZone.getDefault());
                String formattedDate = df.format(date);

                String nowAsISO = dateFormat.format(df.parse(new Date(openTime2).toString()));
                //String open = dateFormat.parse(nowAsISO).toString();
                openTime.setText(nowAsISO);*//*
            } else if(value == 2) {
             *//*   DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(tz);
                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String nowAsISO2 = dateFormat.format(df.parse(new Date(closeTime2).toString()));
                //String close = dateFormat.parse(nowAsISO2).toString();
                closeTime.setText(nowAsISO2);*//*
            } else {
              *//*  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df.setTimeZone(tz);
                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String nowAsISO = dateFormat.format(df.parse(new Date(openTime2).toString()));
                String nowAsISO2 = dateFormat.format(df.parse(new Date(closeTime2).toString()));
                //String open = dateFormat.parse(nowAsISO).toString();
                //String close = dateFormat.parse(nowAsISO2).toString();
                openTime.setText(nowAsISO);
                closeTime.setText(nowAsISO2);*//*
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void checkDays() {
        if (ApplicationSettings.getPref(AppConstants.MON, false)) {
            mLayout.setBackgroundColor(getResources().getColor(R.color.button_color));
        } else {
            mLayout.setBackgroundColor(getResources().getColor(R.color.smb_text));
        }
        if (ApplicationSettings.getPref(AppConstants.TUE, false)) {
            tLayout.setBackgroundColor(getResources().getColor(R.color.button_color));
        } else {
            tLayout.setBackgroundColor(getResources().getColor(R.color.smb_text));
        }
        if (ApplicationSettings.getPref(AppConstants.WEN, false)) {
            wLayout.setBackgroundColor(getResources().getColor(R.color.button_color));
        } else {
            wLayout.setBackgroundColor(getResources().getColor(R.color.smb_text));
        }
        if (ApplicationSettings.getPref(AppConstants.THU, false)) {
            thursdayLayout.setBackgroundColor(getResources().getColor(R.color.button_color));
        } else {
            thursdayLayout.setBackgroundColor(getResources().getColor(R.color.smb_text));
        }
        if (ApplicationSettings.getPref(AppConstants.FRI, false)) {
            fLayout.setBackgroundColor(getResources().getColor(R.color.button_color));
        } else {
            fLayout.setBackgroundColor(getResources().getColor(R.color.smb_text));
        }
        if (ApplicationSettings.getPref(AppConstants.SAT, false)) {
            satrdayLayout.setBackgroundColor(getResources().getColor(R.color.button_color));
        } else {
            satrdayLayout.setBackgroundColor(getResources().getColor(R.color.smb_text));
        }
        if (ApplicationSettings.getPref(AppConstants.SUN, false)) {
            sundayLayout.setBackgroundColor(getResources().getColor(R.color.button_color));
        } else {
            sundayLayout.setBackgroundColor(getResources().getColor(R.color.smb_text));
        }
    }

    public void initTimeVariables(Calendar c) {
        final_start_hour = c.get(Calendar.HOUR_OF_DAY);
        final_end_hour = c.get(Calendar.HOUR_OF_DAY) + 1;
        final_start_min = c.get(Calendar.MINUTE);
        final_end_min = c.get(Calendar.MINUTE);
        final_start_dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        final_end_dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        final_start_month = c.get(Calendar.MONTH);
        final_end_month = c.get(Calendar.MONTH);
        final_start_year = c.get(Calendar.YEAR);
        final_end_year = c.get(Calendar.YEAR);
    }

 /*   private void updateTv() {
        String open = ApplicationSettings.getPref(AppConstants.OPEN_TIME, "");
        String close = ApplicationSettings.getPref(AppConstants.CLOSE_TIME, "");
        if(open != null && !(open.isEmpty())) {
            try {
                String [] openSplit = open.split(":");
                if(openSplit.length > 0) {
                    int openvalue = 0;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < openSplit.length; i++) {
                        if (openSplit[i] != null && !openSplit[i].isEmpty()) {
                            if (openSplit[i].matches("[0-9]+")) {
                                openvalue = Integer.parseInt(open);
                                openvalue = openvalue + 5;
                                if (openvalue < 24) {
                                } else {
                                    openvalue = openvalue - 24;
                                }
                                sb.append(""+openvalue+":");
                            }
                        }
                    }
                    openTime.setText(openvalue);
                }
            } catch (Exception e) {
                e.printStackTrace();
                openTime.setText("8AM");
            }
        } else {
            try {
                String [] openSplit = close.split(":");
                if(openSplit.length > 0) {
                    int openvalue = 0;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < openSplit.length; i++) {
                        if (openSplit[i] != null && !openSplit[i].isEmpty()) {
                            if (openSplit[i].matches("[0-9]+")) {
                                openvalue = Integer.parseInt(open);
                                openvalue = openvalue + 5;
                                if (openvalue < 24) {
                                } else {
                                    openvalue = openvalue - 24;
                                }
                                sb.append(""+openvalue+":");
                            }
                        }
                    }
                    closeTime.setText(openvalue);
                }
            } catch (Exception e) {
                e.printStackTrace();
                openTime.setText("8AM");
            }
            openTime.setText("8AM");
        }
    }*/

    private void checkTime() {
        if(ApplicationSettings.containsPref(AppConstants.OPEN_TIME)){
            String open = ApplicationSettings.getPref(AppConstants.OPEN_TIME, "");
            if(open != null && !(open.isEmpty())) {
                //String opentime = setTime(open);
                openTime.setText(open);
            }  else {
                closeTime.setText("09:00");
            }
        } else {
            openTime.setText("09:00");
        }
        if(ApplicationSettings.containsPref(AppConstants.CLOSE_TIME)) {
            String close = ApplicationSettings.getPref(AppConstants.CLOSE_TIME, "");
            if(close != null && !(close.isEmpty())) {
                //String closeT = setTime(close);
                closeTime.setText(close);
            }  else {
                closeTime.setText("18:00");
            }
        } else {
            closeTime.setText("18:00");
        }
    }

    private String setTime(String open) {
        String callStartTime = "";
        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(new Date().getTime());//set the current time and date for this calendar

        String[] arr = open.split(":");
        if (arr.length >= 2) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int day1 = cur_cal.get(Calendar.DAY_OF_YEAR);

                cal.add(Calendar.DAY_OF_YEAR, day1);
                if (arr[0] != null && !(arr[0].isEmpty())) {
                    int hour = Integer.parseInt(arr[0]);
                    cal.set(Calendar.HOUR, hour);
                }
                if (arr[1] != null && !(arr[1].isEmpty())) {
                    int mins = Integer.parseInt(arr[1]);
                    cal.set(Calendar.MINUTE, mins);
                }
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
                cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
                cal.set(Calendar.YEAR, year);
                Date time = cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                SimpleDateFormat pstFormat = new SimpleDateFormat("kk:mm");
                pstFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                callStartTime = pstFormat.format(sdf.parse(time.toString()));
                return callStartTime;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return callStartTime;
    }

    public void updateTime(int hours, int mins, int checkVar) {

        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(new Date().getTime());//set the current time and date for this calendar

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), hours, mins);
        Date time = cal.getTime();
        String dateAsString = CommonUtils.getTimeFormatInUTC(time);
      /*  SimpleDateFormat outputFmt = new SimpleDateFormat("HH:mm");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        outputFmt.setTimeZone(tz);
        String dateAsString = outputFmt.format(time);*/
        String hrs = "", minit = "";
        if (hours < 10) {
            hrs = "0"+hours;
        } else {
            hrs = ""+hours;
        }
        if(mins < 10) {
            minit = "0"+mins;
        } else {
            minit = ""+mins;
        }
        String oTime = hrs + ":" + minit;

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String aTime = new
                StringBuilder().append(""+hours).append(':')
                .append(""+minutes).append(" ").append(timeSet).toString();

        if (checkVar == 0) {
            if (ApplicationSettings.containsPref(AppConstants.OPEN_TIME) && ApplicationSettings.containsPref(AppConstants.CLOSE_TIME)) {
                try {
                    openTime.setText(ApplicationSettings.getPref(AppConstants.OPEN_TIME, ""));
                    closeTime.setText(ApplicationSettings.getPref(AppConstants.CLOSE_TIME, ""));
                } catch (NullPointerException e) {
                    openTime.setText(aTime);
                    closeTime.setText(aTime);
                }
            } else {
                openTime.setText(aTime);
                closeTime.setText(aTime);
            }
        } else if (checkVar == 1) {
            ApplicationSettings.putPref(AppConstants.OPEN_TIME, oTime);
            openTime.setText(aTime);
        } else if (checkVar == 2) {
            ApplicationSettings.putPref(AppConstants.CLOSE_TIME, oTime);
            closeTime.setText(aTime);
        }
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.switchAutoFollowUps) {
            logAnalytics("Auto_Follow_Up_Switch_Settings", "CheckBox");
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            if (isChecked) {
                smartUser.setAutoFollowUpSettings(true);
                smartUser.setCallRecordStatus(true);
                ApplicationSettings.putPref(AppConstants.ADMIN_GROUP_RECORDING, true);
            } else {
                smartUser.setAutoFollowUpSettings(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        final String TIMEPICKER_TAG = "timepicker";
        int id = v.getId();
        if (id == R.id.ivSyncData) {
            logAnalytics("Sync_Data_Follow_Up_Settings", "ImageView");
            ServiceUserProfile.getUserSettings();
            getFragmentManager().popBackStack();
        } else if (id == R.id.opentime) {
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                    final_start_hour = hourOfDay;
                    final_start_min = minute;
                    int endHourOfDay = 0, endMinute;
                    endMinute = minute + 60;
                    if (endMinute >= 60) {
                        endHourOfDay = endHourOfDay + 1;
                        endMinute = minute % 60;
                    }
                    endHourOfDay = endHourOfDay + hourOfDay;
                    if (hourOfDay >= 24) {
                        endHourOfDay = endHourOfDay % 24;
                    }

                    final_end_hour = endHourOfDay;
                    final_end_min = endMinute;
                    updateTime(hourOfDay, minute, 1);
                       /* Calendar calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), Calendar.MONTH, Calendar.DAY_OF_MONTH,
                                hourOfDay, minute, 0);
                        long startTime = calendar.getTimeInMillis();
                        String satrtime = CommonUtils.getTimeFormatInISO(new Date(startTime));
                        setTime(satrtime, "0", 0);*/

                }
            }, final_start_hour, final_start_min, false, false);
            timePickerDialog.setVibrate(true);
            timePickerDialog.setCloseOnSingleTapMinute(false);
            timePickerDialog.show(getChildFragmentManager(), TIMEPICKER_TAG);
        } else if (id == R.id.closetime) {
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            final TimePickerDialog timePickerDialog2 = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                    final_start_hour = hourOfDay;
                    final_start_min = minute;
                    int endHourOfDay = 0, endMinute;
                    endMinute = minute + 60;
                    if (endMinute >= 60) {
                        endHourOfDay = endHourOfDay + 1;
                        endMinute = minute % 60;
                    }
                    endHourOfDay = endHourOfDay + hourOfDay;
                    if (hourOfDay >= 24) {
                        endHourOfDay = endHourOfDay % 24;
                    }

                    final_end_hour = endHourOfDay;
                    final_end_min = endMinute;
                    updateTime(hourOfDay, minute, 2);

                       /* Calendar calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), Calendar.MONTH, Calendar.DAY_OF_MONTH,
                                hourOfDay, minute, 0);
                        long startTime = calendar.getTimeInMillis();
                        String satrtime = CommonUtils.getTimeFormatInISO(new Date(startTime));
                        setTime("0", satrtime, 1);*/
                }
            }, final_start_hour, final_start_min, false, false);
            timePickerDialog2.setVibrate(true);
            timePickerDialog2.setCloseOnSingleTapMinute(false);
            timePickerDialog2.show(getChildFragmentManager(), TIMEPICKER_TAG);
        } else if (id == R.id.m_layout) {
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            changeLaayoutColor(mLayout, AppConstants.MON);
        } else if (id == R.id.t_layout) {
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            changeLaayoutColor(tLayout, AppConstants.TUE);
        } else if (id == R.id.w_layout) {
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            changeLaayoutColor(wLayout, AppConstants.WEN);
        } else if (id == R.id.thursday_layout) {
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            changeLaayoutColor(thursdayLayout, AppConstants.THU);
        } else if (id == R.id.f_layout) {
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            changeLaayoutColor(fLayout, AppConstants.FRI);
        } else if (id == R.id.strday_layout) {
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            changeLaayoutColor(satrdayLayout, AppConstants.SAT);
        } else if (id == R.id.sunday_layout) {
            ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
            changeLaayoutColor(sundayLayout, AppConstants.SUN);
        }
    }

    private void changeLaayoutColor(RelativeLayout linearLayout, String constant) {
        ColorDrawable buttonColor;
        int colorId;

        buttonColor = (ColorDrawable) linearLayout.getBackground();
        colorId = buttonColor.getColor();

        if (colorId == getResources().getColor(R.color.button_color)) {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.smb_text));
            ApplicationSettings.putPref(constant,false);
            //Log.d("WEEKS",constant + "true");
        } else if (colorId == getResources().getColor(R.color.smb_text)) {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.button_color));
            ApplicationSettings.putPref(constant,true);
            //Log.d("WEEKS",constant + "flase");
        }

    }

    private void logAnalytics(String eventName, String eventType) {
        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_21);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
        mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_21, AnalyticsBundle);
    }

    private void addSpinnerList(Spinner callback_spinner, Spinner reminder_spinner, Activity activity) {
        ArrayAdapter<CharSequence> callbackAdapter = ArrayAdapter.createFromResource(activity, R.array.rnr_callbacks, android.R.layout.simple_spinner_item);
        callbackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        callback_spinner.setAdapter(callbackAdapter);
        callback_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                position = position + 1;
                ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
                ApplicationSettings.putPref(AppConstants.RNR_CALLBACKS_NO, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<CharSequence> rnrReminderAdapter = ArrayAdapter.createFromResource(activity, R.array.rnr_reminders, android.R.layout.simple_spinner_item);
        rnrReminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminder_spinner.setAdapter(rnrReminderAdapter);
        reminder_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 1);
                        break;
                    case 1:
                        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 2);
                        break;
                    case 2:
                        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 3);
                        break;
                    case 3:
                        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 4);
                        break;
                    case 4:
                        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 6);
                        break;
                    case 5:
                        ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
                        ApplicationSettings.putPref(AppConstants.RNR_REMINDER, 24);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (ApplicationSettings.containsPref(AppConstants.RNR_CALLBACKS_NO)) {
            int callbacks = ApplicationSettings.getPref(AppConstants.RNR_CALLBACKS_NO, 0);
            if (callbacks > 0) {
                callback_spinner.setSelection(callbacks - 1);
            }
        } else {
            callback_spinner.setSelection(3);
        }

        if (ApplicationSettings.containsPref(AppConstants.RNR_REMINDER)) {
            int reminders = ApplicationSettings.getPref(AppConstants.RNR_REMINDER, 0);
            if (reminders > 0) {
                String reminderString = reminders + " hr";
                int spinnerPosition = rnrReminderAdapter.getPosition(reminderString);
                reminder_spinner.setSelection(spinnerPosition);
                rnr_callback_et.setText(""+reminders);
            }
        } else {
            reminder_spinner.setSelection(0);
        }

        rnr_callback_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.d("RNR",charSequence+"");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String rnrReminder = rnr_callback_et.getText().toString();
                if(rnrReminder != null && !rnrReminder.isEmpty()) {
                    int rnr = Integer.parseInt(rnrReminder);
                    ApplicationSettings.putPref(AppConstants.RNR_REMINDER, rnr);
                    ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
                }
            }
        });
    }
}
