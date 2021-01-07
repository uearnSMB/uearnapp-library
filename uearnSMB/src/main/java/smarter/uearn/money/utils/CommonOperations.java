package smarter.uearn.money.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GoogleAccount;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.gcm.RegistrationIntentResultReceiver;
import smarter.uearn.money.utils.gcm.RegistrationIntentService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Dilip on 1/11/2017.
 */

public final class CommonOperations {

    public final static void hideKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isNetworkConnected(Context context, int type) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo allNetworks = cm.getActiveNetworkInfo();

            boolean response = false;

            if (allNetworks == null) {
                response = false;
            } else {
                if (allNetworks.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (allNetworks.isConnected()) {
                        response = true;
                    }
                } else if (allNetworks.getType() == ConnectivityManager.TYPE_MOBILE) {
                    response = true;
                }
            }

            if (!response) {
                if (context != null) {
                    if (type == 1) {
                        CustomTwoButtonDialog.internetConnection((Activity) context).show();
                    } else {
                        Toast.makeText(context, "Network not available!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            return response;
        }
        return false;
    }

    public static String buildURL(ContentValues input) {
        StringBuilder paramsBuilder = new StringBuilder();
        int i = 0;

        try {
            for (String key : input.keySet()) {
                paramsBuilder.append((i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(input.get(key).toString(), "utf8"));
                i++;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return String.valueOf(paramsBuilder);
    }

    public static boolean getCalendarEvents(final Context context, final String calendarType, final int countForNextList, int start, int end) {
        Calendar cal = Calendar.getInstance();
        /*Start to*/
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Date date = cal.getTime();
        final Long start2 = cal.getTimeInMillis();
        String startTime = CommonUtils.getTimeFormatInISO(date);
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, +30);
        Date endDate = cal.getTime();
        final Long end2 = cal.getTimeInMillis();
        final String endTime = CommonUtils.getTimeFormatInISO(endDate);
        /*End*/

        final int end1 = end;
        final int start1 = end;
        if (CommonUtils.isNetworkAvailable(context)) {
            GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(SmarterSMBApplication.SmartUser.getId(), startTime, endTime, calendarType);
            getCalendarEntryInfo.limit = countForNextList;
            new APIProvider.Get_CalendarEvents(getCalendarEntryInfo, AppConstants.GET_CAL_EVENTS, new API_Response_Listener<ArrayList<GetCalendarEntryInfo>>() {
                @Override
                public void onComplete(ArrayList<GetCalendarEntryInfo> data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        //getCalendarEvents(context, calendarType, countForNextList + 50, end1, start1);
                        getCalendarEvents(context, calendarType, countForNextList + 100, end1, start1);
                        CommonUtils.savingGoogleCalendarDataToLocalDB(context, data);
                    } else {
                        //Log.i("CalendarFlow", "Calendar data is empty");
                        CommonUtils.calculateNextAlarm(context);
                    }
                }
            }).call();
        }
        return true;
    }

    public static void getOneMonthCalendarEvents(final Context context, final String calendarType, final int countForNextList, int end, int start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, end);
        Date date = cal.getTime();
        final String startTime = CommonUtils.getTimeFormatInISO(date);
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, start);
        Date endDate = cal.getTime();
        final String endTime = CommonUtils.getTimeFormatInISO(endDate);
        final int end1 = end;
        final int start1 = end;
        if (CommonUtils.isNetworkAvailable(context)) {
            GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(SmarterSMBApplication.SmartUser.getId(), startTime, endTime, calendarType);
            getCalendarEntryInfo.limit = countForNextList;
            new APIProvider.Get_CalendarEvents(getCalendarEntryInfo, AppConstants.GET_CAL_EVENTS, new API_Response_Listener<ArrayList<GetCalendarEntryInfo>>() {
                @Override
                public void onComplete(ArrayList<GetCalendarEntryInfo> data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        getCalendarEvents(context, calendarType, countForNextList + 100, end1, start1);
                        CommonUtils.savingGoogleCalendarDataToLocalDB(context, data);
                    } else {
                        //Log.i("CalendarFlow","Calendar data is empty");
                    }
                }
            }).call();
        }
    }

    public static void getCalendarEventsForDate(final Context context, final String calendarType, final int countForNextList, int end, int start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, end);
        Date date = cal.getTime();
        final String startTime = CommonUtils.getTimeFormatInISO(date);
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, start);
        Date endDate = cal.getTime();
        final String endTime = CommonUtils.getTimeFormatInISO(endDate);
        final int end1 = end;
        final int start1 = end;
        if (CommonUtils.isNetworkAvailable(context)) {
            GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(SmarterSMBApplication.SmartUser.getId(), startTime, endTime, calendarType);
            getCalendarEntryInfo.limit = countForNextList;
            new APIProvider.Get_CalendarEvents(getCalendarEntryInfo, AppConstants.GET_CAL_EVENTS, new API_Response_Listener<ArrayList<GetCalendarEntryInfo>>() {
                @Override
                public void onComplete(ArrayList<GetCalendarEntryInfo> data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        //getCalendarEventsForDate(context, calendarType, countForNextList + 50, end1, start1);
                        getCalendarEventsForDate(context, calendarType, countForNextList + 100, end1, start1);
                        CommonUtils.savingGoogleCalendarDataToLocalDB(context, data);
                    } else {
                        //Log.i("CalendarFlow","Calendar data is empty");
                        CommonUtils.calculateNextAlarm(context);
                    }
                }
            }).call();
        }
    }


    public static void subscribeNotificationsCall(boolean subscribe, final Context context) {
        if (subscribe) {
            //Log.d("GCM", "Subscription true");
            ApplicationSettings.putPref(AppConstants.FIRST_TIME_HOMESCREEN, false);
            subscribe = false;
            RegistrationIntentResultReceiver mReceiver;
            mReceiver = new RegistrationIntentResultReceiver(new Handler());
            mReceiver.setReceiver(new RegistrationIntentResultReceiver.Receiver() {
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == 0) {
                        String googelDevicekey = resultData.getString("GoogleIntsDeviceId");
                        //Log.d("GCM", "Device ID" + googelDevicekey);
                        if (googelDevicekey != null) {
                            GoogleAccount googleAccount = new GoogleAccount();
                            googleAccount.setUser_id(SmarterSMBApplication.SmartUser.getId());
                            googleAccount.setDevicekey(googelDevicekey);
                            new APIProvider.Subscribe_ForGoogleNotifications(googleAccount, 0, new API_Response_Listener<String>() {
                                @Override
                                public void onComplete(String data, long request_code, int failure_code) {
                                    //Log.e("GCM", "Push Notifications enabled");
                                }
                            }).call();

                        }
                    } else {
                        //Log.i("PushNotification", "Unable to get the token");
                         Toast.makeText(context, "Google registration error, notifications may fail.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            Intent intent = new Intent(context, RegistrationIntentService.class);
            intent.putExtra("receiverTag", mReceiver);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } else {
            //  Log.e("PushNotification", "Not first time");
        }
    }

    public static String getTimeduration(long min) {

        if (min < 60) {
            return String.valueOf(min) + " min ";
        } else if (min >= 60) {
            long hours = min / 60;
            long minutes = min % 60;
            if (hours < 24) {
                if (minutes == 0) {
                    return String.valueOf(hours) + " Hrs ";
                } else {
                    return String.valueOf(hours) + " Hrs " + String.valueOf(minutes) + " min";
                }
            } else if (hours >= 24) {
                long days = hours / 24;
                long remain = hours % 24;
                if (remain == 0) {
                    return String.valueOf(days) + " days";
                } else {
                    return String.valueOf(days) + "+ days";
                }
            }
        }
        return "0";
    }

    public static String getTimeFormatInISO(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    public static void setFragmentTitle(Activity fetchedActivity, String title) {
        AppCompatActivity activity = (AppCompatActivity) fetchedActivity;
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(title);
    }

    public static void changeStatusBarColor(Activity activity) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }

    public static boolean checkOneMonthEventsList(Context context) {
        MySql sqliteOpenHelper = MySql.getInstance(context);
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Long startTime = cal.getTimeInMillis();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -2);
        Long endTime = cal.getTimeInMillis();
        String checkEvents = "START_TIME" + ">=" + startTime + " AND " + "START_TIME" + "<=" + endTime + " AND " + "STATUS != " + " 'Delete' ";
        Cursor cursor2 = db.query("remindertbNew", null, checkEvents, null, null, null, "START_TIME ASC");

        if (cursor2 != null && cursor2.getCount() > 0) {
            cursor2.close();
            return true;
        } else {
            cursor2.close();
            return false;
        }
    }

    public static String buildProfileUrl() {
        String profileUserID = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        String profileUrl = // "https://www.smarter-biz.com/ssimages/" +
                 profileUserID + "-profile-image.png";
        return profileUrl;
    }

    public static void logAnanlytics(FirebaseAnalytics firebaseAnalytics, String eventName, String eventType) {
      /*  Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_17);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
        firebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_17, AnalyticsBundle);*/
    }
}
