package smarter.uearn.money.broadcastReceiver;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.widget.Toast;

import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;

import java.util.ArrayList;

public class NotificationReceiver extends BroadcastReceiver {

    private static final long REMOVE_USER = 0;
    String ns = Context.NOTIFICATION_SERVICE;
    Context mycontext;
    long dbId = 0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mycontext = context;
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent.getAction().equals("ACCEPT_TEAM_MEMBER")) {
            Bundle bundle = null;
            if (intent.hasExtra("groupsUserInfoBundle")) {
                bundle = intent.getBundleExtra("groupsUserInfoBundle");
            }
            int notification_id = intent.getIntExtra("notification_id", 1);
            mNotificationManager.cancel(notification_id);
            if (bundle != null) {
                ArrayList<GroupsUserInfo> groupsUserInfos = new ArrayList<>();
                GroupsUserInfo groupsUserInfo = CommonUtils.convertBundleToGroupsUserInfo(bundle);
                groupsUserInfo.request = "accepted";
                groupsUserInfo.status = "invitation sent";
                groupsUserInfos.add(groupsUserInfo);
                callForSetGroupsUsers(groupsUserInfos);
            }
        } else if (intent.getAction().equals("REJECT_TEAM_MEMBER")) {
            int notification_id = intent.getIntExtra("notification_id", 1);
            mNotificationManager.cancel(notification_id);

            // Added by dilip - delete the user
            Bundle bundle = null;
            if (intent.hasExtra("groupsUserInfoBundle")) {
                bundle = intent.getBundleExtra("groupsUserInfoBundle");

                GroupsUserInfo groupsUserInfo = CommonUtils.convertBundleToGroupsUserInfo(bundle);
                new APIProvider.Remove_EngineerOrSupervisor(groupsUserInfo, REMOVE_USER, null, "Removing User..", new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        Toast.makeText(context, "User Removed", Toast.LENGTH_SHORT).show();
                        AppConstants.GROUPS_DATA = null;
                    }
                }).call();
            }

        }
        if (intent.getAction().equals("ACCEPT_APPOINTMENT")) {
            if (intent.hasExtra("notification_id")) {
                int notification_id = intent.getIntExtra("notification_id", 1);
                mNotificationManager.cancel(notification_id);
            }
            //call for Notification call
            if (intent.hasExtra("appointment_id")) {
                String appointment_id = intent.getStringExtra("appointment_id");
                //TODO: have to call update appointment
                //Log.i("Tag","Appointment id is "+appointment_id);
            }
            long dbId = intent.getLongExtra("dbId", 0);
            GetCalendarEntryInfo getCalendarEntryInfo = getInfoForUpdateFromDB(dbId);
            getCalendarEntryInfo.setResponsestatus("Accepted");
            new APIProvider.Update_CalendarEvents(getCalendarEntryInfo, 0, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    //  Log.i("NotificationActivity", "Calendar Updated");
                }
            }).call();
            if (intent.hasExtra("caller_number")) {
                //Log.i("Tag","Intent has extra caller number");
                String callerNumber = intent.getStringExtra("caller_number");
                String date = intent.getStringExtra("start_time");
                String body = "Your appointment on " + date + " is confirmed";
                sendConfirmationSMS(callerNumber, body);
            }

            // Log.i("NotificationActivity","IN Accept appointment");

        } else if (intent.getAction().equals("REJECT_APPOINTMENT")) {

            if (intent.hasExtra("notification_id")) {
                int notification_id = intent.getIntExtra("notification_id", 1);
                mNotificationManager.cancel(notification_id);
            }

            if (intent.hasExtra("caller_number")) {
                String callerNumber = intent.getStringExtra("caller_number");
                String date = intent.getStringExtra("start_time");
                String body = "Sorry, your appointment on " + date + " is not confirmed";
                if (callerNumber != null)
                    sendConfirmationSMS(callerNumber, body);
            }
            if (intent.hasExtra("dbId")) {
                //Log.i("NotificationActivity","Has dbId "+intent.getLongExtra("dbId", 0));
            }

            long dbId = intent.getLongExtra("dbId", 0);
            //Log.i("NotificationActivity","DB id is "+dbId);
            GetCalendarEntryInfo getCalendarEntryInfo = getInfoForUpdateFromDB(dbId);
            //Log.i("NotificationActivity","Calendar Info "+getCalendarEntryInfo.appointment_id);
            new APIProvider.Delete_CalendarEvent(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    //Log.i("Tag","calendar event deleted");
                }
            }).call();

        } else if (intent.getAction().equals("DELETE_INTENT")) {
            if (intent.hasExtra("DBID")) {
                //Log.i("NotificationActivity", "Intent has db id extra" + intent.getLongExtra("dbId", 12));
            }
            dbId = intent.getLongExtra("DBID", 0);
            //Log.i("NotificationActivity", "Delete intent called");
            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 1);
            long id = db.update("mytbl", cv, "_id=" + dbId, null);

        } else if (!intent.getAction().equals("REJECT_TEAM_MEMBER") && !intent.getAction().equals("ACCEPT_TEAM_MEMBER")) {
            String numberToSendMessage;
            int notification_id = intent.getIntExtra("notification_id", 1);
            mNotificationManager.cancel(notification_id);
            dbId = intent.getLongExtra("dbId", 0);
            numberToSendMessage = intent.getStringExtra("NumberToSendSMS");

            ContentValues cv = new ContentValues();
            cv.put("MSG_RECEPIENT_NO", numberToSendMessage);

            if (intent.getAction().equals("CALLRECORDING")) {
                //Log.i("NotificationActivity", "In Intent Call Recording");
                cv.put("DISP_CALL_REC", 1);
                ApplicationSettings.putPref(AppConstants.SMS_COMPOSE_CALL_RECORDING, true);
            } else {
                cv.put("DISP_CALL_REC", 0);
            }
            db.update("mytbl", cv, "_id=" + dbId, null);

            if (!intent.getAction().equals("CALLRECORDING")) {
                //Log.i("NotificationActivity", "Intent not call recording");
                sendSMS(numberToSendMessage);
            }
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
        Intent reuploadIntent = new Intent(context, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(reuploadIntent);
        } else {
            context.startService(reuploadIntent);
        }

    }

    public void sendConfirmationSMS(String recipient_no, String body) {
        if (recipient_no != null && !recipient_no.equals("")) {
            //Log.i("NotificationActivity", "Ïn send sms  for " + recipient_no + "Body is " + body);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(recipient_no, null, body, null, null);
        }

    }

    public void sendSMS(String recipient_no) {
        String body = ApplicationSettings.getPref(AppConstants.USERINFO_CALL_SMS_TEMPLATE, "");
        String url = ApplicationSettings.getPref(AppConstants.USERINFO_JD_URL, "no url");
        if (!url.equals(null) && !url.equals("null")) {
            body = body + " " + url;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(recipient_no, null, body, null, null);
        } else {
            //Log.i("Tag", "Could not send sms");
        }
    }

    private GetCalendarEntryInfo getInfoForUpdateFromDB(long dbId) {
        String subject = "Reminder", description = "", playUrl = null, callerNameOrNumber = "", toname = "", toNumber = "",
                date = "", timeDuration = "", time = "";
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(mycontext);
        db = dbHelper.getWritableDatabase();

        String location = "", companyName = "", designation = "";
        long startMls, endMls;
        String appointment_id;
        String caller_number;
        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + dbId + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            appointment_id = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
            getCalendarEntryInfo.setAppointment_id(appointment_id);
            caller_number = cursor.getString(cursor.getColumnIndex("TO1"));
            getCalendarEntryInfo.setCaller_number(caller_number);
            //Log.i("Tag","Appointment Id is "+appointment_id);
            subject = cursor.getString(cursor.getColumnIndex("SUBJECT"));
            getCalendarEntryInfo.setSubject(subject);
            description = cursor.getString(cursor.getColumnIndex("NOTES"));
            getCalendarEntryInfo.setNotes(description);
            toname = cursor.getString(cursor.getColumnIndex("TONAME"));
            getCalendarEntryInfo.setCaller_name(toname);
            toNumber = cursor.getString(cursor.getColumnIndex("TO1"));
            startMls = cursor.getLong(cursor.getColumnIndex("START_TIME"));
            endMls = cursor.getLong(cursor.getColumnIndex("END_TIME"));
            location = cursor.getString(cursor.getColumnIndex("LOCATION"));
            getCalendarEntryInfo.setLocation(location);
            companyName = cursor.getString(cursor.getColumnIndex("COMPANY_NAME"));
            getCalendarEntryInfo.setCompanyName(companyName);
            designation = cursor.getString(cursor.getColumnIndex("DESIGNATION"));
            getCalendarEntryInfo.setDesignation(designation);
            String websiteString = cursor.getString(cursor.getColumnIndex("WEBSITE"));
            getCalendarEntryInfo.setWebsite(websiteString);
            String emailIdString = cursor.getString(cursor.getColumnIndex("EMAILID"));
            getCalendarEntryInfo.setEmail_id(emailIdString);
            String customer_id = cursor.getString(cursor.getColumnIndex("CUSTOMER_ID"));
            getCalendarEntryInfo.setCustomer_id(customer_id);
            String external_reference = cursor.getString(cursor.getColumnIndex("EXTERNAL_REFERENCE"));
            getCalendarEntryInfo.setExternal_calendar_reference(external_reference);
            String start1 = DateFormat.format("dd-MMM-yyyy hh:mm a", startMls).toString();
            String end1 = DateFormat.format("dd-MMM-yyyy hh:mm a", endMls).toString();
            getCalendarEntryInfo.setEvent_start_date(start1);
            getCalendarEntryInfo.setEvent_end_date(end1);
        }
        if (db.isOpen()) {
            db.close();
        }
        return getCalendarEntryInfo;
    }

    private void callForSetGroupsUsers(ArrayList<GroupsUserInfo> groupsUserInfo) {
        new APIProvider.Set_GroupsOfUser(groupsUserInfo, 1, null, null, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                AppConstants.GROUPS_DATA = null;
            }
        }).call();

    }
}
