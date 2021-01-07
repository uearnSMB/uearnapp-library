package smarter.uearn.money.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.JSONParser;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Srinath on 03-03-2017.
 */

public class AutoFollowupService extends IntentService {

    public AutoFollowupService() {
        super("");
    }

    SQLiteDatabase db;
    boolean check = false;

    @Override
    protected void onHandleIntent(Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        if (!isLogout) {
            //Log.d("AutoFollowups", ApplicationSettings.getPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, false) + "");
            if (ApplicationSettings.getPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, false)) {
                if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                    //Log.d("AutoFollowups", "Service started");
                    int count = 0;
                    if (intent.hasExtra("fromBoot")) {
                        count = intent.getIntExtra("fromBoot", 0);
                    }
                    findOverDueFollowups(count);
                }
            }
        }
        //Log.d("AutoFollowups", "inONHandler");
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "smarter.uearn.money";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.small_logo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    private void findOverDueFollowups(int count) {
        Long start = 0l, end = 0l;
        Calendar cal = Calendar.getInstance();
        if (count == 1) {
            Calendar cur_cal = Calendar.getInstance();
            cur_cal.setTimeInMillis(new Date().getTime());
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int day1 = cur_cal.get(Calendar.DAY_OF_YEAR);
            cal.add(Calendar.DAY_OF_YEAR, day1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
            cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
            cal.set(Calendar.YEAR, year);

            end = cal.getTimeInMillis();
            int day = cal.get(Calendar.DAY_OF_WEEK);
            day = day - 1;
            if (day == 0) {
                day = 7;
            }

            boolean check = false;
            int j = 0;
            for (int i = day; i < 8; i--) {
                j++;
                if (i == 0) {
                    i = 7;
                }
                switch (i) {
                    case Calendar.SUNDAY:
                        check = ApplicationSettings.getPref(AppConstants.SUN, false);
                        break;

                    case Calendar.MONDAY:
                        check = ApplicationSettings.getPref(AppConstants.MON, false);
                        break;

                    case Calendar.TUESDAY:
                        check = ApplicationSettings.getPref(AppConstants.TUE, false);
                        break;

                    case Calendar.WEDNESDAY:
                        check = ApplicationSettings.getPref(AppConstants.WEN, false);
                        break;

                    case Calendar.THURSDAY:
                        check = ApplicationSettings.getPref(AppConstants.THU, false);
                        break;

                    case Calendar.FRIDAY:
                        check = ApplicationSettings.getPref(AppConstants.FRI, false);
                        break;

                    case Calendar.SATURDAY:
                        check = ApplicationSettings.getPref(AppConstants.SAT, false);
                        break;
                }

                if (check) {
                    break;
                }
                if (j >= 8) {
                    return;
                }
            }
            if (j > 0) {
                start = end - (j * 24 * 60 * 60 * 1000);
            } else {
                start = end - (24 * 60 * 60 * 1000);
            }

            createFollowup(start, end, 0);
        } else {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
            start = cal.getTimeInMillis();
            end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
            int day = cal.get(Calendar.DAY_OF_WEEK);
            day = day + 1;
            if (day == 8) {
                day = 1;
            }
            boolean check = false;
            int j = 0;
            for (int i = day; i < 8; i++) {
                j++;
                if (i == 7) {
                    i = 1;
                }
                switch (i) {
                    case Calendar.SUNDAY:
                        check = ApplicationSettings.getPref(AppConstants.SUN, false);
                        break;

                    case Calendar.MONDAY:
                        check = ApplicationSettings.getPref(AppConstants.MON, false);
                        break;

                    case Calendar.TUESDAY:
                        check = ApplicationSettings.getPref(AppConstants.TUE, false);
                        break;

                    case Calendar.WEDNESDAY:
                        check = ApplicationSettings.getPref(AppConstants.WEN, false);
                        break;

                    case Calendar.THURSDAY:
                        check = ApplicationSettings.getPref(AppConstants.THU, false);
                        break;

                    case Calendar.FRIDAY:
                        check = ApplicationSettings.getPref(AppConstants.FRI, false);
                        break;

                    case Calendar.SATURDAY:
                        check = ApplicationSettings.getPref(AppConstants.SAT, false);
                        break;
                }

                if (check) {
                    break;
                }
            }

            createFollowup(start, end, j);
        }
    }

    private void createFollowup(Long start, Long end, int j) {
        MySql dbHelper = MySql.getInstance(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        if (cursor.getCount() > 0) {
            //Log.d("AutoFollowups", "insideCursor");
            cursor.moveToFirst();
            TimeZone tz = cal.getTimeZone();
            //buildNotification(getApplicationContext(), cursor.getCount());
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
               /* id = cursor.getInt(cursor.getColumnIndex("_id"));*/
                long startTime = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                long endTime = cursor.getLong(cursor.getColumnIndex("END_TIME"));
                long updatestartTime = startTime;
                long epdateEndTime = endTime;
                //ToDO convert millisec to date
                Date date2 = new Date();
                date2.setTime(startTime);
                String start_time = CommonUtils.getTimeFormatInISO(date2);
                date2.setTime(endTime);
                String end_time = CommonUtils.getTimeFormatInISO(date2);
                String location = cursor.getString(cursor.getColumnIndex("LOCATION"));

                String appointment_id = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));

                String subject = cursor.getString(cursor.getColumnIndex("SUBJECT"));
                String notes = cursor.getString(cursor.getColumnIndex("NOTES"));
                String company_name = cursor.getString(cursor.getColumnIndex("COMPANY_NAME"));
                String designation = cursor.getString(cursor.getColumnIndex("DESIGNATION"));
                String calrec_url = "";
                if (cursor.getString(cursor.getColumnIndex("CALLREC_URL")) != null)
                    calrec_url = cursor.getString(cursor.getColumnIndex("CALLREC_URL"));
                String caller_name = "";
                if (cursor.getString(cursor.getColumnIndex("TONAME")) != null) {
                    caller_name = cursor.getString(cursor.getColumnIndex("TONAME"));
                }
                //TODO: caller number should be from for incoming call
                String caller_number = cursor.getString(cursor.getColumnIndex("TO1"));

                String website = cursor.getString(cursor.getColumnIndex("WEBSITE"));
                String status = cursor.getString(cursor.getColumnIndex("STATUS"));
                String emailId = cursor.getString(cursor.getColumnIndex("EMAILID"));

                String cmail_mailId = cursor.getString(cursor.getColumnIndex("CMAIL_MAILID"));
                String latitude = cursor.getString(cursor.getColumnIndex("LAT"));
                String longitude = cursor.getString(cursor.getColumnIndex("LONG"));
                String orderPotential = cursor.getString(cursor.getColumnIndex("ORDER_POTENTIAL"));
                String source_of_leaad = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
                String product_type = cursor.getString(cursor.getColumnIndex("PRODUCT_TYPE"));
                String company_address = cursor.getString(cursor.getColumnIndex("COMPANY_ADDRESS"));
                int upload_status = cursor.getInt(cursor.getColumnIndex("UPLOAD_STATUS"));
                String appointment_type = cursor.getString(cursor.getColumnIndex("APPOINTMENT_TYPE"));
                int completed = cursor.getInt(cursor.getColumnIndex("COMPLETED"));
                String assign_to = cursor.getString(cursor.getColumnIndex("ASSIGN_TO"));
                String alert_before = cursor.getString(cursor.getColumnIndex("ALARMSETTO"));
                String cc_email = cursor.getString(cursor.getColumnIndex("CCEMAIL"));
                String flpCount = cursor.getString(cursor.getColumnIndex("FLP_COUNT"));
                String subStatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
                String subStatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));

                String creaed_at = CommonUtils.getTimeFormatInISO(new Date());
                Long current = System.currentTimeMillis();
                if (j == 0) {
                    Long endtime = current + (1000 * 60 * 30);
                    Date date1 = new Date();
                    date1.setTime(current);
                    start_time = CommonUtils.getTimeFormatInISO(date1);
                    date1.setTime(endtime);
                    end_time = CommonUtils.getTimeFormatInISO(date1);
                } else {
                    start_time = getStartTime(startTime, j);
                    end_time = getEndTime(endTime, j);
                }
                alert_before = "10";
                GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo(SmarterSMBApplication.SmartUser.getId(),
                        SmarterSMBApplication.SmartUser.getEmail(), "", start_time, end_time, cmail_mailId, calrec_url, location, notes,
                        "", subject, tz.getID(), caller_name, company_name);
                getCalendarEntryInfo.setDesignation(designation);
                getCalendarEntryInfo.setCaller_number(caller_number);
                getCalendarEntryInfo.setWebsite(website);
                getCalendarEntryInfo.setEmail_id(emailId);
                getCalendarEntryInfo.setStatus(status);
                getCalendarEntryInfo.setLatitude(latitude);
                getCalendarEntryInfo.setLongitude(longitude);
                getCalendarEntryInfo.setLead_source(source_of_leaad);
                getCalendarEntryInfo.setAddress(company_address);
                getCalendarEntryInfo.product_type = product_type;
                getCalendarEntryInfo.order_potential = orderPotential;
                getCalendarEntryInfo.assign_to = assign_to;
                getCalendarEntryInfo.alarm_before = alert_before;
                getCalendarEntryInfo.flp_count = "99";
                if(subStatus1 != null) {
                    getCalendarEntryInfo.subStatus1 = subStatus1;
                }
                if(subStatus2 != null) {
                    getCalendarEntryInfo.subStatus2 = subStatus2;
                }
                if (cc_email != null) {
                    getCalendarEntryInfo.setCcMail(cc_email);
                }
                if (creaed_at != null && !(creaed_at.isEmpty())) {
                    getCalendarEntryInfo.created_at = creaed_at;
                }
               /* if (!((status.trim().equalsIgnoreCase(new String("Invalid"))) || (status.trim().equalsIgnoreCase(new String("Not interested"))) || (status.trim().equalsIgnoreCase(new String("Order placed"))) || (status.trim().equalsIgnoreCase(new String("Lost"))))) {*/
                if (caller_number != null && !(caller_number.isEmpty())) {
                    String appointmentID = getNextAppointmentData(caller_number, startTime);
                    if (appointmentID != null && !(appointmentID.isEmpty())) {
                        getCalendarEntryInfo.setResponsestatus("accepted");
                        getCalendarEntryInfo.appointment_id = appointmentID;
                        updateNextFollowup(getCalendarEntryInfo, id, cmail_mailId);
                    } else {
                        getCalendarEntryInfo.setResponsestatus("accepted");
                        createAutoFollowup(getCalendarEntryInfo, caller_number);
                    }
                    if (!check) {
                        updateMissedFollowups(getCalendarEntryInfo, id, updatestartTime, epdateEndTime, appointment_id);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Empty Number!!! Unable to create Autofollowup.", Toast.LENGTH_SHORT).show();
                }

              /*  }*/
                cursor.moveToNext();
            }
        }
    }

    private String getStartTime(Long startTime, int value) {
        startTime = startTime + (1000 * 60 * 60 * 24 * value);
        Date date = new Date();
        date.setTime(startTime);
        String start_time = CommonUtils.getTimeFormatInISO(date);
        return start_time;
    }

    private String getEndTime(Long endTime, int value) {
        endTime = endTime + (1000 * 60 * 60 * 24 * value);
        Date date = new Date();
        date.setTime(endTime);
        String end_time = CommonUtils.getTimeFormatInISO(date);
        return end_time;
    }

    private void createAutoFollowup(GetCalendarEntryInfo getCalendarEntryInfo, String number) {
        /*Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where TO1="  + number, null);*/
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
        Long start1 = cal.getTimeInMillis();
        Long end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);

        String selection = "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + " AND " + "TO1 =" + number;
        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        long startTime = 0L;
        int count = 0;
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    startTime = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                    long endTime = cursor.getLong(cursor.getColumnIndex("END_TIME"));
                    int completed = cursor.getInt(cursor.getColumnIndex("COMPLETED"));
                    if (completed != 0) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        String selection2 = "START_TIME" + ">=" + startTime + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + " AND " + "TO1 =" + number;
        Cursor cursor2 = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
        if (cursor2 != null) {
            try {
                while (cursor2.moveToNext()) {
                    if (cursor2.getString(cursor.getColumnIndex("RESPONSE_STATUS")).equalsIgnoreCase("missed")) {
                        ++count;
                        //Log.d("AutoFollowupService", "MissedCount" + count);
                    } else {
                        //Log.d("AutoFollowupService", "Not MissedCount");
                    }
                }
            } catch (Exception e) {

            } finally {
                cursor2.close();
            }
        }
        if (count < 10) {
            if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                JSONObject calendarJson = JSONParser.getnewJsonObjFromCalendar(getCalendarEntryInfo);
                JSONObject responsejsonObject = null;

                responsejsonObject = DataUploadUtils.postJsonData(Urls.getCalendarCreateUrl(), calendarJson);
                if (responsejsonObject != null) {
                    Log.d("ReuploadService", "AutoFollowupService" + responsejsonObject);
                }
            }
        } else {
            check = true;
        }
    }

    private void updateMissedFollowups(GetCalendarEntryInfo getCalendarEntryInfo, int dbid, Long updateStartTime, Long updateEndTime, String appointmentId) {
        GetCalendarEntryInfo getCalendarEntryInfo1 = getCalendarEntryInfo;
        getCalendarEntryInfo1.setResponsestatus("missed");

        Date date = new Date();
        date.setTime(updateStartTime);
        String event_start_time = CommonUtils.getTimeFormatInISO(date);

        Date date2 = new Date();
        date2.setTime(updateEndTime);
        String event_end_time = CommonUtils.getTimeFormatInISO(date2);

        getCalendarEntryInfo1.setEvent_start_date(event_start_time);
        getCalendarEntryInfo1.setEvent_end_date(event_end_time);

        if (appointmentId != null) {
            getCalendarEntryInfo1.appointment_id = appointmentId;
        }
        JSONObject calendarJson = JSONParser.getnewJsonObjFromCalendar(getCalendarEntryInfo1);
        JSONObject responsejsonObject = null;
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            responsejsonObject = DataUploadUtils.postJsonData(Urls.getUpdateCalendarUrl(), calendarJson);
            if (responsejsonObject != null) {
                //Log.d("AutoFollowups", "Update_followup_SuccessResponse");
                ContentValues contentValues = new ContentValues();
                contentValues.put("RESPONSE_STATUS", "missed");
                db.update("remindertbNew", contentValues, "_id=" + dbid, null);
            }
        }
    }

    private String getNextAppointmentData(String number, Long current_startTime) {
        String callerNameOrNumber = number;
        String subjct = "meating";
        Long start_time = 0L;
        long nextFollowupdbId = 0L;
        String nextAppointmentdbId = "";
        Date date = new Date();
        String selection = "START_TIME" + ">" + date.getTime() + " AND " + "COMPLETED='" + 0 + "'";
        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String customer_number = cursor.getString(cursor.getColumnIndex("TO1"));
                if (PhoneNumberUtils.compare(customer_number, number)) {
                    start_time = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                    if (start_time > current_startTime) {
                        nextFollowupdbId = cursor.getLong(cursor.getColumnIndex("_id"));
                        nextAppointmentdbId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
                        return nextAppointmentdbId;
                    }
                }
                cursor.moveToNext();
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return nextAppointmentdbId;
    }

    private void updateNextFollowup(GetCalendarEntryInfo getCalendarEntryInfo, int dbid, String cmail_mailId) {

        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where APPOINTMENT_ID=" + "'" + dbid + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            long startTime = cursor.getLong(cursor.getColumnIndex("START_TIME"));
            long endTime = cursor.getLong(cursor.getColumnIndex("END_TIME"));

            Date date = new Date();
            date.setTime(startTime);
            String start_time = CommonUtils.getTimeFormatInISO(date);

            Date date2 = new Date();
            date2.setTime(endTime);
            String end_time = CommonUtils.getTimeFormatInISO(date2);

            getCalendarEntryInfo.setEvent_start_date(start_time);
            getCalendarEntryInfo.setEvent_end_date(end_time);

            JSONObject calendarJson = JSONParser.getnewJsonObjFromCalendar(getCalendarEntryInfo);
            JSONObject responsejsonObject = null;
            if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                responsejsonObject = DataUploadUtils.postJsonData(Urls.getUpdateCalendarUrl(), calendarJson);
                if (responsejsonObject != null) {
                    if (responsejsonObject != null && responsejsonObject.has("success")) {
                        try {
                            String responseJson = responsejsonObject.getString("response");
                            JSONObject responseJsonObj = new JSONObject(responseJson);

                            if (responseJsonObj.has("Id")) {
                                String appointmentId = responseJsonObj.getString("Id");
                                //updating reminder is in cmails (mytb1) table
                               /* ContentValues cv1 = new ContentValues();
                                cv1.put("REMINDER_ID", appointmentId);
                                db.update("mytbl", cv1, "_id=" + cmail_mailId, null);*/
                                //updating reminder table with upload status as 1 and appointment id
                                ContentValues cv = new ContentValues();
                                cv.put("UPLOAD_STATUS", 1);
                                cv.put("APPOINTMENT_ID", appointmentId);
                                //Log.d("AutoFollowup_updatenext", ""+ appointmentId);
                                db.update("remindertbNew", cv, "_id=" + dbid, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (cursor != null && !(cursor.isClosed())) {
            cursor.close();
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

}
