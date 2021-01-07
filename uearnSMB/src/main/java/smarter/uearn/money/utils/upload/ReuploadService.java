package smarter.uearn.money.utils.upload;

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
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import smarter.uearn.money.models.Attachments;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.OneViewScreenMail;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.JSONParser;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.MyJsonObject;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * <p>
 * Title:  Reupload Service class
 * </p>
 * <p>
 * Description: this service helps to upload data  that are required for SMARTER SMB with various form
 * data, audio and image files.
 * </p>
 *
 * @author Navine
 * @version 2.0
 *          <p/>
 *          Last Modified on 08-Jan-2016 - Not Final Code
 *          <p/>
 *          Code Optimization not completed - All issues related to reupload and connectivity fixed
 *
 *          modified by srinath k.
 *
 */


public class ReuploadService extends IntentService {

    public static SQLiteDatabase db;
    public static int DONOT_UPLOAD = 0;
    public String from = null;
    public String email = null;
    public String sendTo = null;
    public String eventType = null;
    public String url = null;
    public String jd_url = null;
    public String job_card_url = null;
    public String parent = null;
    public String children = null;
    public String live_recording = null;
    public String startTime = null;
    public String endTime = null;
    public String subject = null;
    public String unread = null;
    public String message = null;
    public String lat = null;
    public String longi = null;
    public String jobcard = null;
    public String assignee = null;
    public String status = null;
    public String caller = null;
    public String filePath = null;
    public JSONObject responseAudio = null;
    public String fileUrl;
    public String recordedeventCallPhoneNUmber;
    public String recordedeventCallContactName;
    public String recordedeventType;
    public Boolean isPhoneNumber = false;
    public static Boolean isalive = false;
    public int id;
    public String bcc;
    public String cc;
    public String attachment = "";
    public String name;
    public int send_sms, disp_map_loc = 1, disp_call_rec, disp_book_appointment = 1;
    public String sms_body, msg_recepient_no;
    private long reminder_id = 0;
    private int uploadStatus = 0;
    private boolean called = false;
    private ArrayList<Attachments> attachmentsArrayList;
    private String transcription_path = "", transcription_url = "", uuid;
    private String updatedFollowup;
    private String company = "";
    public final static String AFTER_CALL_LEAD = "after_call_lead";
    public final static String CUSTOMER_TO_CONTACT = "customer_tocontact";

    public void clearData() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(this);
            db = dbHelper.getWritableDatabase();
        }
        try {
            if (filePath != null && !filePath.equalsIgnoreCase(null) && !filePath.equalsIgnoreCase("")) {
                File file = new File(filePath);
                //Log.e("Reupload Service", "File Path exists"+filePath);
                if (file != null && file.exists()) {
                    deleteFile(filePath);
                } else {
                    //Log.e("Reupload Service", "mytbl deletion"+id);
                try {
                    if(id > 0) {
                        db.delete("mytbl", "_id='" + id + "'", null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
            } else {
                //Log.e("Reupload Service", "mytbl deletion"+id);
                db.delete("mytbl", "_id='" + id + "'", null);
            }
        } catch (Exception e) {
            //Log.d("ReuploadService", "Error in deleting file " + e.getMessage());
        }
        id = 0;
        from = null;
        sendTo = null;
        eventType = null;
        this.url = null;
        jd_url = null;
        job_card_url = null;
        parent = null;
        live_recording = null;
        startTime = null;
        endTime = null;
        subject = null;
        unread = null;
        message = null;
        lat = null;
        longi = null;
        jobcard = null;
        assignee = null;
        status = null;
        caller = null;
        filePath = null;
        sms_body = null;
        uploadStatus = 0;
    }

    public boolean deleteFile(String selectedFilePath) {
        boolean deleted = false;
        File file = new File(selectedFilePath);
        if (file.exists()) {
            deleted = file.delete();
            deleteEntryFromDB(selectedFilePath);
        }
        return deleted;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MySql dbHelper = MySql.getInstance(this);
        db = dbHelper.getWritableDatabase();

        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }
    }

    public ReuploadService(String name) {
        super(name);
    }

    public ReuploadService() {
        super("");
    }

    public static void deleteEntryFromDB(String path) {
        if (path != null) {
            Cursor c = db.rawQuery("SELECT * FROM mytbl where FILE_PATH='" + path + "'", null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                db.delete("mytbl", "FILE_PATH='" + path + "'", null);
                //Log.e("Reupload Service", "File Path exists"+path);
            } else {
                //Log.e("Reupload Service", "File Path exists"+" but not removed from mytbl");
            }
            c.close();
        }
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

    @Override
    protected void onHandleIntent(Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        if (!ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false)) {
            if (!isalive) {
                isalive = true;
                if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                    //Log.d("ReuploadService", "inHandler");
                    if(!db.isOpen()) {
                        MySql dbHelper = MySql.getInstance(this);
                        db = dbHelper.getWritableDatabase();
                    }
                    checkforCalendarDatabaseEntry();

                    String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
                    if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {

                    } else {
                        ameyoUploadEntry();
                        totalAmeyoTable();
                    }

                    //checkForTranscriptFiles();
                    //Log.d("ReuploadService", "Is Started");
                } else {
                    isalive = false;
                }
            } else {
                //Log.d("ReuploadService", "Service Status " + isalive);
            }
        } else {
            //Log.d("ReuploadService", "Is logOut");
        }
    }

    private void totalAmeyoTable() {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(this);
            db = dbHelper.getWritableDatabase();
        }
        Cursor ameyoEntries_cursor = db.rawQuery("SELECT * FROM ameyo_entries", null);
        Cursor ameypReminderCursor = db.query("AmeyoReminderTable", null, null, null, null, null, "CALL_STARTIME ASC");

        if (ameyoEntries_cursor.getCount() > 0) {

            try {
                while (ameyoEntries_cursor.moveToNext()) {
                    //Log.i("SmarterBIZ","Ameyo entries "+ameyoEntries_cursor.getCount());
                    String startTime = ameyoEntries_cursor.getString(ameyoEntries_cursor.getColumnIndex("STARTTIME"));
                    //Log.d("AmeyoEntries", "StartTime"+startTime);
                    String startDidNumber = ameyoEntries_cursor.getString(ameyoEntries_cursor.getColumnIndex("TO1"));
                    //Log.d("AmeyoEntries", "Number"+startDidNumber);
                }
            } finally {
                ameyoEntries_cursor.close();
            }
        }

        if (ameypReminderCursor.getCount() >= 1) {
            //ameypReminderCursor.moveToFirst();
            try {
                while (ameypReminderCursor.moveToNext()) {
                    CommonUtils.checkToken();
                    String startTime = ameypReminderCursor.getString(ameypReminderCursor.getColumnIndex("CALL_STARTIME"));
                    //Log.d("AmeyoReminderEntries", "StartTime"+startTime);
                    String startDidNumber = ameypReminderCursor.getString(ameypReminderCursor.getColumnIndex("TO1"));
                    //Log.d("AmeyoEntries", "Number"+startDidNumber);
                }
            } finally {
                ameypReminderCursor.close();
            }
        }
    }

    public void ameyoUploadEntry() {
        final SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(this);
        db = dbHelper.getWritableDatabase();
        //Cursor ameyoEntries_cursor = db.query("ameyo_entries", null, null, null, null, null, "STARTTIME ASC");       /*Added ameyo followup table By Srinath.k*/
        Cursor ameyoEntries_cursor = db.rawQuery("SELECT * FROM ameyo_entries where UPLOAD_STATUS=0", null);
        Cursor ameyoReminder_cursor = db.query("AmeyoReminderTable", null, null, null, null, null, "CALL_STARTIME ASC");
        //Cursor ameyoReminder_cursor = db.rawQuery("SELECT * FROM AmeyoReminderTable where UPLOAD_STATUS=0", null);
        //Log.i("SmarterBIZ","Count in new db is "+ameyoEntries_cursor.getCount());
        String didSettingsNumber = ApplicationSettings.getPref(AppConstants.CLOUD_TELEPHONY, "");
        String strArray[] = didSettingsNumber.split(",");

        if (ameyoEntries_cursor.getCount() > 0) {
            //ameyoEntries_cursor.moveToFirst();
            try {
                while (ameyoEntries_cursor.moveToNext()) {
                    CommonUtils.checkToken();
                    final int dbid = ameyoEntries_cursor.getInt(ameyoEntries_cursor.getColumnIndex("_id"));
                    String startTime = ameyoEntries_cursor.getString(ameyoEntries_cursor.getColumnIndex("STARTTIME"));
                    String endTime = ameyoEntries_cursor.getString(ameyoEntries_cursor.getColumnIndex("ENDTIME"));
                    //Log.d("AmeyoEntries", "StartTime"+startTime);
                    String startDidNumber = ameyoEntries_cursor.getString(ameyoEntries_cursor.getColumnIndex("TO1"));
                    String eventType = ameyoEntries_cursor.getString(ameyoEntries_cursor.getColumnIndex("EVENT_TYPE"));
                    String timeStamp = getTimeStamp(startTime);
                    String endtimeStamp = getTimeStamp(endTime);
                    if(endtimeStamp == null) {
                        endtimeStamp = "";
                    }
                    if(timeStamp == null) {
                        timeStamp = "";
                    }
                    final String[] numbers = {startDidNumber, timeStamp, endtimeStamp};
                    if (!(eventType.equalsIgnoreCase("call_missed"))) {
                        String didNumber = ApplicationSettings.getPref(AppConstants.CLOUD_TELEPHONY, "");
                        if (startTime != null && !(startTime.isEmpty())) {

                            //Log.d("Reupload_TimeStamp", timeStamp);

                            if (startDidNumber != null && !(startDidNumber.isEmpty())) {
                                if (CommonUtils.checkForAmeyoNumber(startDidNumber)) {

                                    new APIProvider.IvrMappingUpload(numbers, 22, new API_Response_Listener<String>() {
                                        @Override
                                        public void onComplete(String data, long request_code, int failure_code) {
                                         /*   data = "+917204230507";*/
                                            if (data != null) {
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("UPLOAD_STATUS", 1);
                                                contentValues.put("TO1", data);
                                                contentValues.put("SERVER_UPLOAD", "pending");
                                                db.update("ameyo_entries", contentValues, "_id=" + dbid, null);
                                                //Log.d("data", "ivr mapping done" + data);
                                                //uploadEntryToServerByChangingNumber();
                                                //copyMytableToAmeyoTableEntry(dbid);
                                            }
                                        }
                                    }).call();
                                } else {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("UPLOAD_STATUS", 1);
                                    contentValues.put("SERVER_UPLOAD", "pending");
                                    db.update("ameyo_entries", contentValues, "_id=" + dbid, null);
                                }
                            }
                        }
                    } else {
                        if (startDidNumber != null && !(startDidNumber.isEmpty())) {
                            if (CommonUtils.checkForAmeyoNumber(startDidNumber)) {
                                new APIProvider.IvrMappingMissedUpload(numbers, 22, new API_Response_Listener<String>() {
                                    @Override
                                    public void onComplete(String data, long request_code, int failure_code) {
                                        if (data != null) {
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put("UPLOAD_STATUS", 1);
                                            contentValues.put("TO1", data);
                                            contentValues.put("SERVER_UPLOAD", "pending");
                                            db.update("ameyo_entries", contentValues, "_id=" + dbid, null);
                                            //Log.d("data", "ivr mapping done" + data);
                                        }
                                    }
                                }).call();
                            } else {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("UPLOAD_STATUS", 1);
                                contentValues.put("SERVER_UPLOAD", "pending");
                                db.update("ameyo_entries", contentValues, "_id=" + dbid, null);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ameyoEntries_cursor.close();
            }
        }
        uploadEntryToServerByChangingNumber();
        if (ameyoReminder_cursor != null && ameyoReminder_cursor.getCount() >= 1) {
            sendAmeyoReminderToServer(ameyoReminder_cursor);
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private void uploadEntryToServerByChangingNumber() {

        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(this);
            db = dbHelper.getWritableDatabase();
        }

        String selection = "UPLOAD_STATUS=" + 1 + " AND " + "SERVER_UPLOAD=" + " 'pending' ";
        Cursor ameyo_entries_cursor = db.query("ameyo_entries", null, selection, null, null, null, "STARTTIME ASC");
        //Cursor ameyo_entries_cursor = db.rawQuery("SELECT * FROM ameyo_entries where UPLOAD_STATUS=1", null);
        //Log.i("SmarterBIZ","Count in new db is "+ameyo_entries_cursor.getCount());

        if (ameyo_entries_cursor != null && ameyo_entries_cursor.getCount() > 0) {
            try {
                while (ameyo_entries_cursor.moveToNext()) {
                    OneViewScreenMail oneViewScreenMail = new OneViewScreenMail();

                    String sendTo = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("TO1"));
                    final int mytblId = ameyo_entries_cursor.getInt(ameyo_entries_cursor.getColumnIndex("_id"));

                    //String from1 = number;
                    String eventType = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("EVENT_TYPE"));
                    String filePath = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("FILE_PATH"));
                    //Log.i("SmarterBIZ", "Call recording url is " + filePath);
                    //Log.i("SmarterBIZ", "Call recording number " + sendTo);
                    JSONObject responseAudio = null;
                    if (filePath != null && !(filePath.isEmpty())) {
                        try {
                            if(transcription_path == null) {
                                transcription_path = "";
                            }
                            CommonUtils.checkToken();
                            responseAudio = DataUploadUtils.uploadAudioFileToServer(filePath, eventType, sendTo, "unknown", Urls.getUploadFileUrl());
                            //Log.i("SmarterBIZ", "Response audio is " + responseAudio);
                            if (responseAudio != null) {
                               /* ContentValues contentValues = new ContentValues();
                                contentValues.put("FILE_PATH", responseAudio.getString("url"));
                                db.update("ameyo_entries", contentValues, "_id=" + mytblId, null);
                                */
                                //Log.d("ReuploadService", "uploadAudio");
                                oneViewScreenMail.setUrl(responseAudio.getString("url"));
                                if (filePath != null) {
                                    boolean deletestatus = deleteFile(filePath);
                                    //Log.i("SmarterBIZ", "File deleted " + deletestatus);
                                }
                                //Log.i("SmarterBIZ", "After uploading url is " + responseAudio.getString("url"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //Log.i("SmarterBIZ", "EventType " + eventType);
                    if (responseAudio != null || eventType.equalsIgnoreCase("call_missed") || eventType.equals("call_in_rec_off") || eventType.equals("call_out_rec_off") || eventType.equals("call_in_rnr")  || eventType.equals("call_out_rnr")) {
                        String startTime = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("STARTTIME"));
                        String endTime = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("ENDTIME"));
                        String subject = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("SUBJECT"));
                        String message = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("MESSAGE"));
                        String lat = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("LAT"));
                        String longi = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("LONG"));
                        final String status = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("STATUS"));
                        int disp_call_rec = ameyo_entries_cursor.getInt(ameyo_entries_cursor.getColumnIndex("DISP_CALL_REC"));
                        int disp_book_appointment = ameyo_entries_cursor.getInt(ameyo_entries_cursor.getColumnIndex("DISP_BOOK_APPOINTMENT"));
                        String attachmentsList = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("ATTACHMENTS_ARRAYLIST"));
                        final int reminder_id = ameyo_entries_cursor.getInt(ameyo_entries_cursor.getColumnIndex("REMINDER_ID"));

                        final String subStatus1 = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("SUBSTATUS1"));
                        final String subStatus2 = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("SUBSTATUS2"));

                        Type type = new TypeToken<ArrayList<Attachments>>() {
                        }.getType();
                        Gson gson = new Gson();
                        ArrayList<Attachments> attachmentsArrayList = gson.fromJson(attachmentsList, type);
                        final String from = sendTo;
                        String event_type = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("EVENT_TYPE"));
                        //Log.d("Customer Number", from);
                        oneViewScreenMail.setFrom(from);
                        oneViewScreenMail.setTo(sendTo);
                        oneViewScreenMail.setActivity_type(eventType);
                        oneViewScreenMail.setStart_time(startTime);
                        oneViewScreenMail.setEnd_time(endTime);
                        oneViewScreenMail.setSubject(subject);
                        oneViewScreenMail.setMessage(message);
                        oneViewScreenMail.setLongitude(longi);
                        oneViewScreenMail.setLatitude(lat);
                        String customerName = "", customerNumber;
                        if (eventType.equals("call_in_rec_off") || eventType.equals("call_in_rec_on") || eventType.equals("sms_in")
                                || eventType.equals("call_missed") || eventType.equals("call_in_rnr")) {
                            customerName = CommonUtils.setCallerName(getApplicationContext(), from);
                            customerNumber = from;
                        } else {
                            customerName = CommonUtils.setCallerName(getApplicationContext(), sendTo);
                            customerNumber = sendTo;
                        }
                        oneViewScreenMail.setNumber(customerNumber);
                        oneViewScreenMail.setName(customerName);

                        if (disp_book_appointment == 0) {
                            oneViewScreenMail.setDb_allowbooking(false);
                        } else {
                            oneViewScreenMail.setDb_allowbooking(true);
                        }
                        if (disp_call_rec == 0) {
                            oneViewScreenMail.setShow_callrecording(false);
                        } else {
                            oneViewScreenMail.setShow_callrecording(true);
                        }
                        if (attachmentsArrayList != null) {
                            oneViewScreenMail.setAttachmentListArrayList(attachmentsArrayList);
                        }
                        //TODO: remove sending following when new digital brochure is implemented
                        if (eventType.equals("call_in_rec_on") || eventType.equals("call_out_rec_on")) {
                            oneViewScreenMail.setEventType("call");
                        } else if (eventType.equals("call_missed")) {
                            oneViewScreenMail.setEventType("missed_call");
                            String push_notification_message = "Cloud Call"; // CHECK THIS
                            oneViewScreenMail.setMessage(push_notification_message);
                        } else if (eventType.equals("live_meeting_rec")) {
                            oneViewScreenMail.setEventType("live_meeting");
                        } else if (eventType.equals("call_in_rec_off") || eventType.equals("call_out_rec_off")) {
                            oneViewScreenMail.setEventType("call_record_off");
                        } else if(eventType.equals("call_in_rnr") || eventType.equals("call_out_rnr")) {
                            oneViewScreenMail.setEventType("call_rnr");
                            oneViewScreenMail.setActivity_type("call_rnr");
                        } else if(eventType.equals("call_dnd")) {
                            oneViewScreenMail.setEventType("call_dnd");
                        } else if(eventType.equals("call_invalid")) {
                            oneViewScreenMail.setEventType("call");
                        }
                        if (event_type != null && (event_type.equals("missed_call_assigned") || event_type.equals("missed_call_attended"))) {
                            String push_notification_message = "Cloud Call";
                            if (push_notification_message != null && !push_notification_message.equals("")) {
                                oneViewScreenMail.setMessage(push_notification_message);
                                if (push_notification_message != null) {
                                    //Log.d("AmeyoMessage", push_notification_message);
                                }
                            }
                        }
                        //Log.i("SmarterBIZ", "Json obj is " + JSONParser.convertOneViewScreenMailToJSON(oneViewScreenMail));
                        new APIProvider.Post_Activity(oneViewScreenMail, 1, null, null, new API_Response_Listener<OneViewScreenMail>() {
                            @Override
                            public void onComplete(OneViewScreenMail data, long request_code, int failure_code) {
                                if (data != null) {
                                    //Log.d("ReuploadService", data.toString());
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("SERVER_UPLOAD", "done");
                                    if(!db.isOpen()) {
                                        MySql dbHelper = MySql.getInstance(getApplicationContext());
                                        db = dbHelper.getWritableDatabase();
                                    }
                                    db.update("ameyo_entries", contentValues, "_id=" + mytblId, null);
                                    if (reminder_id != 0) {
                                        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + reminder_id + "'", null);
                                        ContentValues cv = new ContentValues();
                                        cv.put("UPLOAD_STATUS", 0);
                                        cv.put("TO1", from);
                                        String toname = CommonUtils.getContactName(getApplicationContext(), from);
                                        cv.put("TONAME", toname);
                                        int no = db.update("remindertbNew", cv, "_id=" + reminder_id, null);
                                        Intent intent = new Intent(getApplicationContext(), ReuploadService.class);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            startForegroundService(intent);
                                        } else {
                                            startService(intent);
                                        }
                                    } else {
                                        //Log.d("ReuploadService", "updateSales");
                                        String sub1 = "", sub2 = "";
                                        if(subStatus1 != null) {
                                            sub1 = subStatus1;
                                        }
                                        if(subStatus2 != null) {
                                            sub2 = subStatus2;
                                        }
                                        updateSalesStage(status, from, sub1, sub2);
                                    }
                                    //db.delete("ameyo_entries", "_id='" + dbId + "'", null);
                                }
                            }
                        }).call();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ameyo_entries_cursor.close();
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        }
    }

   /* private void uploadEntryToServerByChangingNumber(final SQLiteDatabase db, final long dbId, final long push_noti_id) {
        //Log.i("SmarterBIZ","In Upload entry to server "+dbId+"Push id is "+push_noti_id);
        Cursor ameyo_entries_cursor = db.rawQuery("SELECT * FROM ameyo_entries where _id=" + "'" + dbId + "'", null);
        Cursor ameyo_push_notifications_cursor = db.rawQuery("SELECT * FROM Ameyo_Push_Notifications where _id=" + "'" + push_noti_id + "'", null);
        if (ameyo_entries_cursor.getCount() != 0 && ameyo_push_notifications_cursor.getCount() != 0) {
            ameyo_entries_cursor.moveToFirst();
            ameyo_push_notifications_cursor.moveToFirst();
            OneViewScreenMail oneViewScreenMail = new OneViewScreenMail();

            String sendTo = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("TO1"));
            String from1 = ameyo_push_notifications_cursor.getString(ameyo_push_notifications_cursor.getColumnIndex("CUSTOMER_NUMBER"));
            //Log.i("SmarterBIZ", "in db from is "+from1+", to is "+sendTo);
            String eventType = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("EVENT_TYPE"));
            String filePath = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("FILE_PATH"));
            //Log.i("SmarterBIZ","Call recording url is "+filePath);
            JSONObject responseAudio = DataUploadUtils.uploadAudioFileToServer(filePath, eventType, from1, "unknown", Urls.getUploadFileUrl());
            //Log.i("SmarterBIZ","Response adio is "+responseAudio);
            if (responseAudio != null) {
                try {
                    //Log.d("ReuploadService", "uploadAudio");
                    oneViewScreenMail.setUrl(responseAudio.getString("url"));
                    if (filePath != null) {
                        boolean deletestatus = deleteFile(filePath);
                        //Log.i("SmarterBIZ","File deleted "+deletestatus);
                    }
                    //Log.i("SmarterBIZ","After uploading url is "+responseAudio.getString("url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            String startTime = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("STARTTIME"));
            String endTime = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("ENDTIME"));
            String subject = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("SUBJECT"));
            String message = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("MESSAGE"));
            //Log.d("AmeyoMessage", message);
            String lat = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("LAT"));
            String longi = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("LONG"));
            final String status = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("STATUS"));
            int disp_call_rec = ameyo_entries_cursor.getInt(ameyo_entries_cursor.getColumnIndex("DISP_CALL_REC"));
            int disp_book_appointment = ameyo_entries_cursor.getInt(ameyo_entries_cursor.getColumnIndex("DISP_BOOK_APPOINTMENT"));
            String attachmentsList = ameyo_entries_cursor.getString(ameyo_entries_cursor.getColumnIndex("ATTACHMENTS_ARRAYLIST"));
            final int reminder_id = ameyo_entries_cursor.getInt(ameyo_entries_cursor.getColumnIndex("REMINDER_ID"));
            Type type = new TypeToken<ArrayList<Attachments>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<Attachments> attachmentsArrayList = gson.fromJson(attachmentsList, type);
            //Changing from number with the customer nummber from push notification.a
            final String from = ameyo_push_notifications_cursor.getString(ameyo_push_notifications_cursor.getColumnIndex("CUSTOMER_NUMBER"));
            String event_type = ameyo_push_notifications_cursor.getString(ameyo_push_notifications_cursor.getColumnIndex("TYPE"));
            //Log.d("Customer Number", from);
            oneViewScreenMail.setFrom(from);
            oneViewScreenMail.setTo(sendTo);
            oneViewScreenMail.setActivity_type(eventType);
            oneViewScreenMail.setStart_time(startTime);
            oneViewScreenMail.setEnd_time(endTime);
            oneViewScreenMail.setSubject(subject);
            oneViewScreenMail.setMessage(message);
            oneViewScreenMail.setLongitude(longi);
            oneViewScreenMail.setLatitude(lat);
            String customerName = "", customerNumber;
            if (eventType.equals("call_in_rec_off") || eventType.equals("call_in_rec_on") || eventType.equals("sms_in")
                    || eventType.equals("call_missed")) {
                customerName = CommonUtils.setCallerName(getApplicationContext(), from);
                customerNumber = from;
            } else {
                customerName = CommonUtils.setCallerName(getApplicationContext(), sendTo);
                customerNumber = sendTo;
            }
            oneViewScreenMail.setNumber(customerNumber);
            oneViewScreenMail.setName(customerName);

            if (disp_book_appointment == 0) {
                oneViewScreenMail.setDb_allowbooking(false);
            } else {
                oneViewScreenMail.setDb_allowbooking(true);
            }
            if (disp_call_rec == 0) {
                oneViewScreenMail.setShow_callrecording(false);
            } else {
                oneViewScreenMail.setShow_callrecording(true);
            }
            if (attachmentsArrayList != null) {
                oneViewScreenMail.setAttachmentListArrayList(attachmentsArrayList);
            }
            //TODO: remove sending following when new digital brochure is implemented
            if (eventType.equals("call_in_rec_on") || eventType.equals("call_out_rec_on")) {
                oneViewScreenMail.setEventType("call");
            } else if (eventType.equals("call_missed")) {
                oneViewScreenMail.setEventType("missed_call");
                String push_notification_message = ameyo_push_notifications_cursor.getString(ameyo_push_notifications_cursor.getColumnIndex("MESSAGE"));
                if (push_notification_message != null && !push_notification_message.equals("")) {
                    oneViewScreenMail.setMessage(push_notification_message);
                    if (push_notification_message != null) {
                        //Log.d("AmeyoMessage", push_notification_message);
                    }
                }
            } else if (eventType.equals("live_meeting_rec")) {
                oneViewScreenMail.setEventType("live_meeting");
            } else if (eventType.equals("call_in_rec_off") || eventType.equals("call_out_rec_off")) {
                oneViewScreenMail.setEventType("call_record_off");
            }
            if (event_type != null && (event_type.equals("missed_call_assigned") || event_type.equals("missed_call_attended"))) {
                String push_notification_message = ameyo_push_notifications_cursor.getString(ameyo_push_notifications_cursor.getColumnIndex("MESSAGE"));
                if (push_notification_message != null && !push_notification_message.equals("")) {
                    oneViewScreenMail.setMessage(push_notification_message);
                    if (push_notification_message != null) {
                        //Log.d("AmeyoMessage", push_notification_message);
                    }
                }
            }
            //Log.i("SmarterBIZ","Json obj is "+ JSONParser.convertOneViewScreenMailToJSON(oneViewScreenMail));
            new APIProvider.Post_Activity(oneViewScreenMail, 1, null, null, new API_Response_Listener<OneViewScreenMail>() {
                @Override
                public void onComplete(OneViewScreenMail data, long request_code, int failure_code) {
                    if (data != null) {
                        //Log.d("ReuploadService", data.toString());
                        if (reminder_id != 0) {
                            Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + reminder_id + "'", null);
                            ContentValues cv = new ContentValues();
                            cv.put("UPLOAD_STATUS", 0);
                            cv.put("TO1", from);
                            String toname = CommonUtils.getContactName(getApplicationContext(), from);
                            cv.put("TONAME", toname);
                            int no = db.update("remindertbNew", cv, "_id=" + reminder_id, null);
                            //Log.i("PushNotification","Count of reminder table updated is "+no);
                            Intent intent = new Intent(getApplicationContext(), ReuploadService.class);
                            startService(intent);
                        } else {
                            //Log.d("ReuploadService", "updateSales");
                            updateSalesStage(status, from);
                        }
                        db.delete("ameyo_entries", "_id='" + dbId + "'", null);
                        db.delete("Ameyo_Push_Notifications", "_id='" + push_noti_id + "'", null);
                    }

                }
            }).call();
//            JSONObject response = DataUploadUtils.postEmailContactInfo(Urls.getPostActivityUrl(), JSONParser.convertOneViewScreenMailToJSON(oneViewScreenMail));
        }
    }*/

    private void updateSalesStage(String status, String msg_recepient_no, String subStaus1, String subStatus2) {
        String start_time = CommonUtils.getTimeFormatInISO(new Date());
        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        getCalendarEntryInfo.setStatus(status);
        getCalendarEntryInfo.caller_number = msg_recepient_no;
        getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
        getCalendarEntryInfo.setEvent_start_date(start_time);
        if(subStaus1 != null) {
            getCalendarEntryInfo.subStatus1 = subStaus1;
        }
        if(subStatus2 != null) {
            getCalendarEntryInfo.subStatus2 = subStatus2;
        }

        String leadSource = ApplicationSettings.getPref(AFTER_CALL_LEAD, "");
        if(leadSource == null || leadSource.isEmpty()) {
            leadSource = getLeadSource(msg_recepient_no);
        }
        getCalendarEntryInfo.lead_source = leadSource;

        String customerToContact = ApplicationSettings.getPref(CUSTOMER_TO_CONTACT, "");
        if(customerToContact == null || customerToContact.isEmpty()) {
            customerToContact = getCustomerToContact(msg_recepient_no);
        }
        getCalendarEntryInfo.customer_id = customerToContact;

        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.Update_SalesStatus(getCalendarEntryInfo, 0, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        }
        CommonUtils.saveContactInfoToSmartContactDB(getApplicationContext(), msg_recepient_no, "", status, "", "", "", "", "", "");
    }

    private String getLeadSource(String number) {
        String leadSource = "";
        try {
            MySql dbHelper = MySql.getInstance(this);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if(number != null && !number.isEmpty()) {
                String selection = "TO1=" + "'" + number + "'";
                Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
                }
                if (dbase.isOpen()) {
                    dbase.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leadSource;
    }

    private String getCustomerToContact(String number) {
        String customerId = "";
        try {
            MySql dbHelper = MySql.getInstance(this);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if(number != null && !number.isEmpty()) {
                String selection = "TO1=" + "'" + number + "'";
                Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    customerId = cursor.getString(cursor.getColumnIndex("CUSTOMER_ID"));
                }
                if (dbase.isOpen()) {
                    dbase.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerId;
    }

    public void checkforDatabaseEntry() {
        Log.d("SDCallRecording", "ReuploadService - Inside checkforDatabaseEntry()");
        //Log.d("ReuploadService","Inside checkforDatabaseEntry()");
        MySql dbHelper = new MySql(this, "mydb", null, AppConstants.dBversion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor dbCursor = db.rawQuery("SELECT * FROM mytbl", null);
        try {
            dbCursor.moveToFirst();
            while (!dbCursor.isAfterLast()) {
            /*  while (dbCursor.moveToNext()) {*/
                id = dbCursor.getInt(dbCursor.getColumnIndex("_id"));
                from = dbCursor.getString(dbCursor.getColumnIndex("FROM1"));
                sendTo = dbCursor.getString(dbCursor.getColumnIndex("TO1"));

                boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                if(sequencialEndpoint) {
                    if (sendTo != null && !sendTo.isEmpty() && sendTo.contains(",")) {
                        String connectedNumber = ApplicationSettings.getPref(AppConstants.CONNECTED_CUSTOMER, "");
                        if (connectedNumber != null && !connectedNumber.isEmpty() && connectedNumber.contains("")) {
                            connectedNumber = connectedNumber.replaceAll("\\s+", "");
                        }
                        if (connectedNumber != null && !connectedNumber.isEmpty() && !connectedNumber.startsWith("+")) {
                            connectedNumber = "+" + connectedNumber;
                        }

                        if (connectedNumber != null && !connectedNumber.isEmpty()) {
                            sendTo = connectedNumber;
                        }
                    }
                }

                //Log.i("Reupload_SErvice", "in db from is "+from+", to is "+sendTo);
                eventType = dbCursor.getString(dbCursor.getColumnIndex("EVENT_TYPE"));
                url = dbCursor.getString(dbCursor.getColumnIndex("URL"));
                jd_url = dbCursor.getString(dbCursor.getColumnIndex("JD_URL"));
                job_card_url = dbCursor.getString(dbCursor.getColumnIndex("JOBCARD_URL"));
                parent = dbCursor.getString(dbCursor.getColumnIndex("PARENT"));
                children = dbCursor.getString(dbCursor.getColumnIndex("CMAIL_TR_COUNT"));
                live_recording = dbCursor.getString(dbCursor.getColumnIndex("LIVE_RECORDING"));
                startTime = dbCursor.getString(dbCursor.getColumnIndex("STARTTIME"));
                endTime = dbCursor.getString(dbCursor.getColumnIndex("ENDTIME"));
                subject = dbCursor.getString(dbCursor.getColumnIndex("SUBJECT"));
                unread = dbCursor.getString(dbCursor.getColumnIndex("UNREAD"));
                message = dbCursor.getString(dbCursor.getColumnIndex("MESSAGE"));
                lat = dbCursor.getString(dbCursor.getColumnIndex("LAT"));
                longi = dbCursor.getString(dbCursor.getColumnIndex("LONG"));
                jobcard = dbCursor.getString(dbCursor.getColumnIndex("JOBCARD"));
                assignee = dbCursor.getString(dbCursor.getColumnIndex("ASSIGNEE"));
                status = dbCursor.getString(dbCursor.getColumnIndex("STATUS"));
                caller = dbCursor.getString(dbCursor.getColumnIndex("CALLER"));
                filePath = dbCursor.getString(dbCursor.getColumnIndex("FILE_PATH"));
                bcc = dbCursor.getString(dbCursor.getColumnIndex("BCC"));
                cc = dbCursor.getString(dbCursor.getColumnIndex("CC"));
                attachment = dbCursor.getString(dbCursor.getColumnIndex("ATTACHMENT"));
                transcription_path = dbCursor.getString(dbCursor.getColumnIndex("TRANSCRIPTION_PATH"));
                uuid = dbCursor.getString(dbCursor.getColumnIndex("UUID"));

                name = dbCursor.getString(dbCursor.getColumnIndex("NAME"));

                if (name == null) {
                    name = CommonUtils.getContactName(getApplicationContext(), sendTo);
                } else if (name.isEmpty()) {
                    name = CommonUtils.getContactName(getApplicationContext(), sendTo);
                }

                sms_body = dbCursor.getString(dbCursor.getColumnIndex("SMS_BODY"));
                disp_call_rec = dbCursor.getInt(dbCursor.getColumnIndex("DISP_CALL_REC"));
                disp_book_appointment = dbCursor.getInt(dbCursor.getColumnIndex("DISP_BOOK_APPOINTMENT"));

                //TODO: Making DISPLAY MAP LOCAATION BY DEFAULT 1
                // disp_map_loc = dbCursor.getInt(dbCursor.getColumnIndex("DISP_MAP_LOC"));
                disp_map_loc = 1;
                msg_recepient_no = dbCursor.getString(dbCursor.getColumnIndex("MSG_RECEPIENT_NO"));
                reminder_id = dbCursor.getLong(dbCursor.getColumnIndex("REMINDER_ID"));
                uploadStatus = dbCursor.getInt(dbCursor.getColumnIndex("UPLOAD_STATUS"));
                String attachmentsList = dbCursor.getString(dbCursor.getColumnIndex("ATTACHMENTS_ARRAYLIST"));
                Type type = new TypeToken<ArrayList<Attachments>>() {
                }.getType();
                Gson gson = new Gson();
                attachmentsArrayList = gson.fromJson(attachmentsList, type);
                if(sendTo == null && (getApplicationContext() != null)) {
                    CommonUtils.callLogApi(getApplicationContext(), "In Local DB Search");
                } else if(sendTo != null && sendTo.isEmpty() && (getApplicationContext() != null)) {
                    CommonUtils.callLogApi(getApplicationContext(), "In Local DB Search");
                }

                if (ApplicationSettings.containsPref(AppConstants.UPLOAD_CMAIL)) {
                    boolean uploadCmail = ApplicationSettings.getPref(AppConstants.UPLOAD_CMAIL, false);
                    if (!uploadCmail) {
                            if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                                updateAppointmentToCompletedStatus(sendTo);
                            }
                    } else {
                        if (true) {
                            audioUpload();
                        }
                    }
                } else {
                    if (true) {
                        audioUpload();
                    }
                }
                dbCursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isalive = false;
        } finally {
            dbCursor.close();
        }
    }

    public void checkforCalendarDatabaseEntry() {
        MySql dbHelper = new MySql(this, "mydb", null, AppConstants.dBversion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Log.i("ReuploadService","In calendar calendar database entry");
        if (!called) {
            called = true;
            Cursor dbCursor = db.rawQuery("SELECT * FROM remindertbNew where UPLOAD_STATUS=0", null);
            //Log.i("Reupload","Calendar Db count in reupload is "+dbCursor.getCount());
            dbCursor.moveToFirst();
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = calendar.getTimeZone();
            while (!dbCursor.isAfterLast()) {
                id = dbCursor.getInt(dbCursor.getColumnIndex("_id"));
                //Log.d("Bank1AfterCallActivity", " DBID Get  " + id);
                long startTime = dbCursor.getLong(dbCursor.getColumnIndex("START_TIME"));
                long endTime = dbCursor.getLong(dbCursor.getColumnIndex("END_TIME"));
                //ToDO convert millisec to date
                Date date = new Date();
                date.setTime(startTime);
                String start_time = CommonUtils.getTimeFormatInISO(date);
                date.setTime(endTime);
                String end_time = CommonUtils.getTimeFormatInISO(date);
                String location = dbCursor.getString(dbCursor.getColumnIndex("LOCATION"));

                String appointment_id = dbCursor.getString(dbCursor.getColumnIndex("APPOINTMENT_ID"));

                String subject = dbCursor.getString(dbCursor.getColumnIndex("SUBJECT"));
                String notes = dbCursor.getString(dbCursor.getColumnIndex("NOTES"));
                String company_name = dbCursor.getString(dbCursor.getColumnIndex("COMPANY_NAME"));
                String designation = dbCursor.getString(dbCursor.getColumnIndex("DESIGNATION"));
                String calrec_url = "";
                if (dbCursor.getString(dbCursor.getColumnIndex("CALLREC_URL")) != null)
                    calrec_url = dbCursor.getString(dbCursor.getColumnIndex("CALLREC_URL"));

                //TODO: caller number should be from for incoming call
                String caller_number = dbCursor.getString(dbCursor.getColumnIndex("TO1"));

                String caller_name = "";
                if (dbCursor.getString(dbCursor.getColumnIndex("TONAME")) != null) {
                    caller_name = dbCursor.getString(dbCursor.getColumnIndex("TONAME"));
                } else {
                    if(caller_number != null && !(caller_name.isEmpty())) {
                        caller_name = CommonUtils.getContactName(getApplicationContext(), caller_number);
                        if(caller_name == null) {
                            caller_name = "";
                        }
                    }
                }

                if(caller_number == null && (getApplicationContext() != null)) {
                    CommonUtils.callLogApi(getApplicationContext(), "Event Create");
                } else if(caller_number != null && caller_number.isEmpty() && (getApplicationContext() != null)) {
                    CommonUtils.callLogApi(getApplicationContext(), "Event Create");
                }

                String website = dbCursor.getString(dbCursor.getColumnIndex("WEBSITE"));
                String status = dbCursor.getString(dbCursor.getColumnIndex("STATUS"));
                if(status == null || status.isEmpty()) {
                    if(company.equals("OYO") || company.equals("ALLIANCE")) {
                        status = "NEW DATA";
                    } else {
                        status = "Initial";
                    }
                }
                String emailId = dbCursor.getString(dbCursor.getColumnIndex("EMAILID"));

                String cmail_mailId = dbCursor.getString(dbCursor.getColumnIndex("CMAIL_MAILID"));
                String latitude = dbCursor.getString(dbCursor.getColumnIndex("LAT"));
                String longitude = dbCursor.getString(dbCursor.getColumnIndex("LONG"));
                String orderPotential = dbCursor.getString(dbCursor.getColumnIndex("ORDER_POTENTIAL"));
                if (orderPotential.equalsIgnoreCase("null")) {
                    orderPotential = "";
                }
                String source_of_lead = dbCursor.getString(dbCursor.getColumnIndex("LEAD_SOURCE"));
                String product_type = dbCursor.getString(dbCursor.getColumnIndex("PRODUCT_TYPE"));
                String company_address = dbCursor.getString(dbCursor.getColumnIndex("COMPANY_ADDRESS"));
                int upload_status = dbCursor.getInt(dbCursor.getColumnIndex("UPLOAD_STATUS"));
                String appointment_type = dbCursor.getString(dbCursor.getColumnIndex("APPOINTMENT_TYPE"));
                int completed = dbCursor.getInt(dbCursor.getColumnIndex("COMPLETED"));
                String assign_to = dbCursor.getString(dbCursor.getColumnIndex("ASSIGN_TO"));
                String alert_before = dbCursor.getString(dbCursor.getColumnIndex("ALARMSETTO"));
                String cc_email = dbCursor.getString(dbCursor.getColumnIndex("CCEMAIL"));
                String flpCount = dbCursor.getString(dbCursor.getColumnIndex("FLP_COUNT"));
                int rnrcount = dbCursor.getInt(dbCursor.getColumnIndex("RNR_COUNT"));
                String created_at = dbCursor.getString(dbCursor.getColumnIndex("CREATED_AT"));
                String substatus1 = dbCursor.getString(dbCursor.getColumnIndex("SUBSTATUS1"));
                String substatus2 = dbCursor.getString(dbCursor.getColumnIndex("SUBSTATUS2"));
                String notesimage = dbCursor.getString(dbCursor.getColumnIndex("NOTES_IMAGE"));
                String customerId = dbCursor.getString(dbCursor.getColumnIndex("CUSTOMER_ID"));

                //Added By Srinath
                String responseStatus = "";
                if (dbCursor.getString(dbCursor.getColumnIndex("RESPONSE_STATUS")) != null && !(dbCursor.getString(dbCursor.getColumnIndex("RESPONSE_STATUS")).isEmpty())) {
                    responseStatus = dbCursor.getString(dbCursor.getColumnIndex("RESPONSE_STATUS"));
                }

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
                getCalendarEntryInfo.setLead_source(source_of_lead);
                getCalendarEntryInfo.setAddress(company_address);
                getCalendarEntryInfo.product_type = product_type;
                getCalendarEntryInfo.order_potential = orderPotential;
                getCalendarEntryInfo.assign_to = assign_to;
                getCalendarEntryInfo.alarm_before = alert_before;
                getCalendarEntryInfo.rnrCount = rnrcount;
                getCalendarEntryInfo.customer_id = customerId;

                if(notesimage != null) {
                    getCalendarEntryInfo.notesImageUrl = notesimage;
                }
                if (flpCount != null) {
                    getCalendarEntryInfo.flp_count = flpCount;
                    //Log.d("REUPLOADSERVICE", "FLPCOUNT"+getCalendarEntryInfo.flp_count);
                } else {
                    getCalendarEntryInfo.flp_count = "1";
                    //Log.d("REUPLOADSERVICE", "FLPCOUNT"+getCalendarEntryInfo.flp_count);
                }
                if (cc_email != null) {
                    getCalendarEntryInfo.setCcMail(cc_email);
                }
                if (responseStatus.equalsIgnoreCase("completed")) {
                    getCalendarEntryInfo.responsestatus = "completed";
                }
                if (responseStatus.equalsIgnoreCase("missed")) {
                    getCalendarEntryInfo.responsestatus = "missed";
                }
                //Log.i("Reupload","Alarm before is "+alert_before);
                if (completed == 1) {
                    getCalendarEntryInfo.responsestatus = "completed";
                }
                if (appointment_id != null && !appointment_id.equals("")) {
                    getCalendarEntryInfo.appointment_id = appointment_id;
                }

                if(created_at != null && !(created_at.isEmpty())) {
                    getCalendarEntryInfo.created_at = created_at;
                }

                if(substatus1 != null && !substatus1.isEmpty()  && !(substatus1.equalsIgnoreCase("null"))) {
                    getCalendarEntryInfo.subStatus1 = substatus1;
                }

                if(substatus2 != null && !substatus2.isEmpty() && !(substatus2.equalsIgnoreCase("null"))) {
                    getCalendarEntryInfo.subStatus2 = substatus2;
                }

                if (upload_status == 0) {
                    CommonUtils.checkToken();
                    if(caller_number != null) {
                        checkKnowlarity(caller_number, "Follow-up created with ");
                    }
                    //TODO: check for one more field whether update or new appointment
                    offlinedataUpload(getCalendarEntryInfo, appointment_type, assign_to, appointment_id, cmail_mailId);
                }
                dbCursor.moveToNext();
            }
            //Log.d("ReuploadService","in Calendar Call");
            //Intent aIntent = new Intent(getApplicationContext(), ReuploadService.class);
            //getApplicationContext().startService(aIntent);
            dbCursor.close();
        }
        //Log.d("ReuploadService","out Calendar Call");

        String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
        if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
            boolean uploadCmail = ApplicationSettings.getPref(AppConstants.UPLOAD_CMAIL, false);
            if(uploadCmail) {
                checkforDatabaseEntry();
            } else {
                Log.d("SSU2", "ReuploadService - Upload Cmail Else Part - Before calling checkForOfflineStatusEntry()");
                checkForOfflineStatusEntry();
            }
        } else {
            checkforDatabaseEntry();
        }
    }

    private void checkKnowlarity(String caller_number, String msg) {
        //Added By Srinath.k to know knowlarity Number
        if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
            String cloud1 = "", cloud2 = "";
            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING1)) {
                cloud1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
            }
            if (ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING2)) {
                cloud2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");
            }
            if(caller_number != null && !caller_number.isEmpty()) {
                if ((cloud1 != null && cloud1.equalsIgnoreCase(caller_number) || (cloud2 != null && (cloud2.equalsIgnoreCase(caller_number))))) {
               /* if(true) {*/
                    if(getApplicationContext() != null) {
                        int version_code = CommonUtils.getVersionCode(getApplicationContext());
                        String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                                ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>" + msg+" "+caller_number
                                + "<br/> Current Activity: " + SmarterSMBApplication.currentActivity + "<br/> App Version: " + version_code;
                        ServiceApplicationUsage.callErrorLog(message);
                    }
                }
            }
        }
    }

    public void offlinedataUpload(GetCalendarEntryInfo getCalendarEntryInfo, String appointment_type, final String assign_to, String appointment_id, final String cmail_mailId) {
        Log.d("ReuploadService","Json for calendar create is "+" type "+appointment_type+ " APtid "+appointment_id+" assign"+assign_to);

        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(this);
            db = dbHelper.getWritableDatabase();
        }
        JSONObject calendarJson = JSONParser.getnewJsonObjFromCalendar(getCalendarEntryInfo);
        JSONObject responsejsonObject = null;
        CommonUtils.checkToken();
        String userEmail = "";
        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }
        //calling create calendar api
        Log.d("ReuploadService","Json for calendar create is "+calendarJson.toString()+" type "+appointment_type+ " APtid "+appointment_id+" assign"+assign_to);
        if (calendarJson != null) {
            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 1);  // Changed Upload Status from 1 to 0 - Reverted
            if(!db.isOpen()) {
                MySql dbHelper = MySql.getInstance(this);
                db = dbHelper.getWritableDatabase();
            }
            int number = db.update("remindertbNew", cv, "_id=" + id, null);
            //Log.i("ReuploadService","In Data updated is "+number);
        }

        String companyName = "";
        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            companyName = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }


//        if(companyName.equals("OYO")) {
//            if (appointment_type != null && appointment_type.equals("update_appointment")) {
//                if (assign_to != null && !assign_to.equals("null") && !assign_to.equalsIgnoreCase(userEmail)) {
//                    responsejsonObject = DataUploadUtils.postJsonData(Urls.getCalendarCreateUrl(), calendarJson);
//                    if (appointment_id != null) {
//                        Log.i("ReuploadService","Calling update calendar from prev appintment");
//                        GetCalendarEntryInfo getCalendarEntryInfo1 = new GetCalendarEntryInfo();
//                        getCalendarEntryInfo1.user_id = SmarterSMBApplication.SmartUser.getId();
//                        getCalendarEntryInfo1.appointment_id = appointment_id;
//                        getCalendarEntryInfo1.responsestatus = "completed";
//                        updateAppointmentCall(getCalendarEntryInfo1);
//                    }
//                } else {
//                    responsejsonObject = DataUploadUtils.postJsonData(Urls.getUpdateCalendarUrl(), calendarJson);
//                }
//            } else if (appointment_type != null && appointment_type.equals("complete_appointment")) {
//                getCalendarEntryInfo.tat = 0;
//                getCalendarEntryInfo.responsestatus = "completed";
//
//                new APIProvider.Update_complete_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
//                    @Override
//                    public void onComplete(String data, long request_code, int failure_code) {
//                        //Log.i("ReuploadService","In Data completed is "+data);
//                        try {
//                            if(data != null) {
//                                String email = "";
//                                String assignTo = "";
//                                String cmailId = "";
//                                if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
//                                    email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
//                                }
//                                if (assign_to == null) {
//                                    assignTo = "";
//                                } else {
//                                    assignTo = assign_to;
//                                }
//
//                                if (cmail_mailId == null) {
//                                    cmailId = "";
//                                } else {
//                                    cmailId = cmail_mailId;
//                                }
//                                MyJsonObject responsejsonObject = new MyJsonObject(data);
//                                //Log.d("ReuploadService","Update_complete_Appointment calling setUpdatedFollowup"+assignTo+" email "+email);
//                                setUpdatedFollowup(responsejsonObject, assignTo, email, cmailId);
//                            } else {
//                                ContentValues cv = new ContentValues();
//                                cv.put("UPLOAD_STATUS", 0);
//                                if(db != null && db.isOpen())
//                                    db.update("remindertbNew", cv, "_id=" + id, null);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).call();
//            } else if (appointment_type != null && appointment_type.equals("accept_appointment")) {
//                getCalendarEntryInfo.tat = 0;
//                getCalendarEntryInfo.responsestatus = "accepted";
//                //Log.d("getCalendarEntryInfo", getCalendarEntryInfo.responsestatus );
//                new APIProvider.Update_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
//                    @Override
//                    public void onComplete(String data, long request_code, int failure_code) {
//                        //Log.i("ReuploadService","In Data accept is "+data);
//                    }
//                }).call();
//
//            } else if (appointment_type != null && appointment_type.equals("Delete_Appointment")) {
//                if (id != 0) {
//                    db.delete("remindertbNew", "_id=" + id, null);
//                }
//                new APIProvider.Delete_CalendarEvent(getCalendarEntryInfo, 0, null, "Deleting Appointment..", new API_Response_Listener<String>() {
//                    @Override
//                    public void onComplete(String data, long request_code, int failure_code) {
//                        //Log.i("ReuploadService","Delete Event Successfully");
//                    }
//                }).call();
//
//            } else {
//                //complete_appointment
//                if(getCalendarEntryInfo.status != null && !getCalendarEntryInfo.status.isEmpty()) {
//                    if(getCalendarEntryInfo.status.equals("Call Back Later")){
//                        responsejsonObject = DataUploadUtils.postJsonData(Urls.getCalendarCreateUrl(), calendarJson);
//                    }
//                }
//
//            }
//        } else {
        if (appointment_type != null && appointment_type.equals("update_appointment")) {
            if (assign_to != null && !assign_to.equals("null") && !assign_to.equalsIgnoreCase(userEmail)) {
                Log.d("ReuploadService","offlinedataUpload(CalendarCreate)- calendarJson "+calendarJson);
                //Log.d("UearnActivity---", "ReuploadService --- onBackPressed" +calendarJson);

                Log.d("UearnActivity---", "Update_appointment ReuploadService ---" + calendarJson.toString());




                responsejsonObject = DataUploadUtils.postJsonData(Urls.getCalendarCreateUrl(), calendarJson);
                Log.d("ReuploadService","offlinedataUpload(CalendarCreate)- responsejsonObject "+responsejsonObject);
                if (appointment_id != null) {
                    Log.i("ReuploadService","Calling update calendar from prev appintment");
                    GetCalendarEntryInfo getCalendarEntryInfo1 = new GetCalendarEntryInfo();
                    getCalendarEntryInfo1.user_id = SmarterSMBApplication.SmartUser.getId();
                    getCalendarEntryInfo1.appointment_id = appointment_id;
                    getCalendarEntryInfo1.responsestatus = "completed";
                    updateAppointmentCall(getCalendarEntryInfo1);
                }
            } else {
                Log.d("ReuploadService","offlinedataUpload(CalendarUpdate)- calendarJson "+calendarJson);
                responsejsonObject = DataUploadUtils.postJsonData(Urls.getUpdateCalendarUrl(), calendarJson);
                Log.d("ReuploadService","offlinedataUpload(CalendarUpdate)- responsejsonObject "+responsejsonObject);
            }
        } else if (appointment_type != null && appointment_type.equals("complete_appointment")) {
            getCalendarEntryInfo.tat = 0;
            getCalendarEntryInfo.responsestatus = "completed";

            new APIProvider.Update_complete_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    //Log.i("ReuploadService","In Data completed is "+data);
                    try {
                        if(data != null) {
                            String email = "";
                            String assignTo = "";
                            String cmailId = "";
                            if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                                email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
                            }
                            if (assign_to == null) {
                                assignTo = "";
                            } else {
                                assignTo = assign_to;
                            }

                            if (cmail_mailId == null) {
                                cmailId = "";
                            } else {
                                cmailId = cmail_mailId;
                            }
                            MyJsonObject responsejsonObject = new MyJsonObject(data);
                            //Log.d("ReuploadService","Update_complete_Appointment calling setUpdatedFollowup"+assignTo+" email "+email);
                            setUpdatedFollowup(responsejsonObject, assignTo, email, cmailId);
                        } else {
                            ContentValues cv = new ContentValues();
                            cv.put("UPLOAD_STATUS", 0);
                            if(db != null && db.isOpen())
                                db.update("remindertbNew", cv, "_id=" + id, null);
                        }
                    } catch (Exception e) {
                        ContentValues cv = new ContentValues();
                        cv.put("UPLOAD_STATUS", 0);
                        if(db != null && db.isOpen())
                            db.update("remindertbNew", cv, "_id=" + id, null);
                        e.printStackTrace();
                    }
                }
            }).call();
        } else if (appointment_type != null && appointment_type.equals("accept_appointment")) {
            getCalendarEntryInfo.tat = 0;
            getCalendarEntryInfo.responsestatus = "accepted";
            //Log.d("getCalendarEntryInfo", getCalendarEntryInfo.responsestatus );
            new APIProvider.Update_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    //Log.i("ReuploadService","In Data accept is "+data);
                }
            }).call();

        } else if (appointment_type != null && appointment_type.equals("Delete_Appointment")) {
            if (id != 0) {
                if(db != null && db.isOpen())
                    db.delete("remindertbNew", "_id=" + id, null);
            }
            new APIProvider.Delete_CalendarEvent(getCalendarEntryInfo, 0, null, "Deleting Appointment..", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    //Log.i("ReuploadService","Delete Event Successfully");
                }
            }).call();

        } else {
            Log.d("ReuploadService","offlinedataUpload(CalendarCreate)- calendarJson "+calendarJson);
            responsejsonObject = DataUploadUtils.postJsonData(Urls.getCalendarCreateUrl(), calendarJson);
            Log.d("ReuploadService","offlinedataUpload(CalendarCreate)- responsejsonObject "+responsejsonObject);
        }
//        }



        //Log.d("ReuploadService","Response json object for Calendar create is "+responsejsonObject);
        if (responsejsonObject != null && responsejsonObject.has("success")) {
            String email = "";
            String assignTo = "";
            String cmailId = "";
            if(assign_to == null) {
                assignTo = "";
            } else {
                assignTo = assign_to;
            }
            if(userEmail == null) {
                userEmail = "";
            }

            if(cmail_mailId == null) {
                cmailId = "";
            } else {
                cmailId = cmail_mailId;
            }
            //Log.d("ReuploadService","after posting reponse postJsonData"+assignTo+" email "+userEmail);
            setUpdatedFollowup(responsejsonObject, assignTo, userEmail, cmailId);
        } else if(appointment_type != null && !appointment_type.equals("Delete_Appointment") && !appointment_type.equals("accept_appointment")  && !appointment_type.equals("complete_appointment")){
            if(!db.isOpen()) {
                MySql dbHelper = MySql.getInstance(this);
                db = dbHelper.getWritableDatabase();
            }
            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 0);
            if(db != null && db.isOpen())
                db.update("remindertbNew", cv, "_id=" + id, null);
            //Log.i("Reupload","Response failure");
        } else if(appointment_type == null) {
            if(!db.isOpen()) {
                MySql dbHelper = MySql.getInstance(this);
                db = dbHelper.getWritableDatabase();
            }
            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 0);
            if(db != null && db.isOpen())
                db.update("remindertbNew", cv, "_id=" + id, null);
            //Log.i("Reupload","Response failure");
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private void setUpdatedFollowup(JSONObject responsejsonObject, String assign_to, String userEmail, String cmail_mailId) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(this);
            db = dbHelper.getWritableDatabase();
        }
        try {
            String responseJson = responsejsonObject.getString("response");
            //Log.d("ReuploadService", "setUpdatedFollowup-response"+ responseJson);
            JSONObject responseJsonObj = new JSONObject(responseJson);
            if (assign_to != null && !assign_to.equals("null") && !assign_to.isEmpty() && !assign_to.equalsIgnoreCase(userEmail)) {
                if (assign_to.contains("PAN") || assign_to.contains("CIBIL")) {
                    ContentValues cv = new ContentValues();
                    cv.put("COMPLETED", 1);
                    cv.put("RESPONSE_STATUS", "completed");
                    cv.put("UPDATED_AT", System.currentTimeMillis());
                    if (db != null && db.isOpen())
                        db.update("remindertbNew", cv, "_id=" + id, null);
                } else {
                    db.delete("remindertbNew", "_id=" + id, null);
                }
            } else if (responseJsonObj.has("Id")) {
                try {
                    String updated_at = "";
                    String appointmentId = responseJsonObj.getString("Id");
                    //Log.d("ReuploadService", "follow-up id" +appointmentId);
                    //Log.d("ReuploadService", ""+ responseJson);
                    String status = "", flpcount = "", subStatus1 = "",  subStatus2 = "";
                    if (responseJsonObj.has("status")) {
                        status = responseJsonObj.getString("status");
                    }

                    if (responseJsonObj.has("flpcount")) {
                        flpcount = responseJsonObj.getString("flpcount");
                    }

                    if (responseJsonObj.has("substatus1")) {
                        subStatus1 = responseJsonObj.getString("substatus1");
                    }

                    if (responseJsonObj.has("substatus2")) {
                        subStatus2 = responseJsonObj.getString("substatus2");
                    }

                    if (responseJsonObj.has("updated_at")) {
                        updated_at = responseJsonObj.getString("updated_at");
                        //Log.d("ReuploadService", "updated_at"+ updated_at);
                    }

                    if (appointmentId != null && !(appointmentId.isEmpty())) {
                        //updating reminder is in cmails (mytb1) table
                        ContentValues cv1 = new ContentValues();
                        cv1.put("REMINDER_ID", appointmentId);

                        if (cmail_mailId != null && !(cmail_mailId.isEmpty())) {
                            db.update("mytbl", cv1, "_id=" + cmail_mailId, null);
                            //Log.d("REUPLOAD_REMINDER", ""+ cmail_mailId);
                            //Log.d("ReuploadService", "reminder updated" +appointmentId);
                        } else {
                            //Log.d("REUPLOAD_REMINDER", ""+ "appointment_id null");
                        }
                    }
                    //updating reminder table with upload status as 1 and appointment id
                    ContentValues cv = new ContentValues();
                    if (appointmentId != null && !(appointmentId.isEmpty())) {
                        cv.put("UPLOAD_STATUS", 1);
                        cv.put("APPOINTMENT_ID", appointmentId);

                        if (flpcount != null && !flpcount.isEmpty()) {
                            cv.put("FLP_COUNT", flpcount);
                        }
                        if (status != null && !(status.isEmpty())) {
                            cv.put("STATUS", status);
                        }

                        if(subStatus1 != null && !(subStatus1.isEmpty())) {
                            cv.put("SUBSTATUS1", subStatus1);
                        }

                        if(subStatus2 != null && !(subStatus2.isEmpty())) {
                            cv.put("SUBSTATUS2", subStatus2);
                        }

                        if(updated_at != null && !(updated_at.isEmpty())) {
                            long update = CommonUtils.stringToMilliSec(updated_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            //Log.d("ReuploadService", "updated_at"+update);
                            cv.put("UPDATED_AT", update);
                        }

                        //Log.d("ReuploadService", "follow-up id updated" +appointmentId);
                        if(db != null && db.isOpen())
                            db.update("remindertbNew", cv, "_id=" + id, null);
                    } else {
                        String userDetails = "<br/>Email: " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "")
                                + "<br/>ID : " + ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>";
                        int version_code = CommonUtils.getVersionCode(this);

                        String message = userDetails+"<br/><br/>"+"Version number"+version_code+"<br/><br/>"+"Reuploadload Service"
                                + "<br/>" + "AppointmentId is null"+ "<br/><br/>";

                        ServiceApplicationUsage.callErrorLog(message);
                    }
                } catch (Exception e) {
                    ContentValues cv = new ContentValues();
                    cv.put("UPLOAD_STATUS", 0);
                    if(db != null && db.isOpen())
                        db.update("remindertbNew", cv, "_id=" + id, null);
                    e.printStackTrace();
                }
            } else {
                //Log.d("ReuploadService", "null follow-up id");
                ContentValues cv = new ContentValues();
                cv.put("UPLOAD_STATUS", 0);
                if(db != null && db.isOpen())
                    db.update("remindertbNew", cv, "_id=" + id, null);
            }
        } catch (JSONException e) {
            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 0);
            if(db != null && db.isOpen())
                db.update("remindertbNew", cv, "_id=" + id, null);
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean canUpload() {
        return uploadStatus != DONOT_UPLOAD;
    }

    public void audioUpload() {
        try {
            if(transcription_path == null) {
                transcription_path = "";
            }
            String fileUrl = Urls.getUploadFileUrl();
            CommonUtils.checkToken();
            if (!eventType.equalsIgnoreCase(null)) {
                if (eventType.equalsIgnoreCase("live_meeting_rec")) {
                    ApplicationSettings.putPref(AppConstants.FROM_CMAIL, true);
                    if (new File(filePath).exists()) {
                        responseAudio = DataUploadUtils.uploadAudioFileToServer(filePath, "live_meeting_rec", "", "", fileUrl);
                    } else {
                        deleteEntryFromDB(filePath);
                        isalive = false;
                    }
                    ApplicationSettings.putPref(AppConstants.FROM_CMAIL, false);

                } else if (eventType.equalsIgnoreCase("call_out_rec_on") || eventType.equals("call_in_rec_on")) {
                    String name = null;
                    String num = null;
                    if (message != null && !message.equals("")) {
                        if (message.equalsIgnoreCase("Incoming Call")) {
                            num = from;
                            name = caller;
                        } else if (message.equalsIgnoreCase("Outgoing Call")) {
                            num = sendTo;
                            name = caller;
                        }
                    }
                    if(sendTo == null && (getApplicationContext() != null)) {
                        CommonUtils.callLogApi(getApplicationContext(), "Inside audio upload");
                    } else if(sendTo != null && sendTo.isEmpty() && (getApplicationContext() != null)) {
                        CommonUtils.callLogApi(getApplicationContext(), "Inside aaudio upload");
                    }

                    if (num != null && name != null) {
                        responseAudio = DataUploadUtils.uploadAudioFileToServer(filePath, eventType, num, name, fileUrl);
                    } else if (num != null) {
                        responseAudio = DataUploadUtils.uploadAudioFileToServer(filePath, eventType, num, "", fileUrl);
                    } else {
                        sendTo = "unknown";
                        from = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                        subject = "CALL RECORD";
                        message = "unknown";
                        responseAudio = DataUploadUtils.uploadAudioFileToServer(filePath, eventType, from, "unknown", fileUrl);
                    }

                } else {
                    String companyName = "";
                    if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
                        companyName = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
                        if(companyName.equals("OYO")) {
                            if(!eventType.equals("call_missed")) {
                                if(uuid != null) {
                                    sendCallDataToServer("");
                                }
                            } else {
                                sendCallDataToServer("");
                            }
                        } else {
                            sendCallDataToServer("");
                        }
                    } else {
                        sendCallDataToServer("");
                    }
                }
            /*following check is to avoid crash because of filepath is null for email event */
                if (eventType != null) {
                    if (eventType.equals("call_in_rec_on") || eventType.equals("call_out_rec_on") || eventType.equals("live_meeting_rec")) {
                        if (responseAudio != null) {
                            try {
                                if(transcription_path != null && !(transcription_path.isEmpty())) {
                                    String num = "";
                                    if (message != null && !message.equals("")) {
                                        if (message.equalsIgnoreCase("Incoming Call")) {
                                            num = from;
                                        } else if (message.equalsIgnoreCase("Outgoing Call")) {
                                            num = sendTo;
                                        }
                                    }
                                    if(num == null) {
                                        num = "";
                                    }
                                    transcription_url = checkForTranscriptFiles(num, transcription_path);
                                }
                                ApplicationSettings.putPref(AppConstants.CHECK_UPLOADING_STATUS, true);
                                this.fileUrl = responseAudio.getString("url");
                                recordedeventType = responseAudio.getString("call_type");
                                recordedeventCallPhoneNUmber = responseAudio.getString("phone");

                                boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                                if(sequencialEndpoint) {
                                    if (recordedeventCallPhoneNUmber == null || recordedeventCallPhoneNUmber.isEmpty()) {
                                        String connectedNumber = ApplicationSettings.getPref(AppConstants.CONNECTED_CUSTOMER, "");
                                        if (connectedNumber != null && !connectedNumber.isEmpty() && connectedNumber.contains("")) {
                                            connectedNumber = connectedNumber.replaceAll("\\s+", "");
                                        }
                                        if (connectedNumber != null && !connectedNumber.isEmpty() && !connectedNumber.startsWith("+")) {
                                            connectedNumber = "+" + connectedNumber;
                                        }

                                        if (connectedNumber != null && !connectedNumber.isEmpty()) {
                                            recordedeventCallPhoneNUmber = connectedNumber;
                                        }
                                    }
                                }

                                recordedeventCallContactName = responseAudio.getString("contact_name");
                                if (this.fileUrl != null) {
                                    if (recordedeventType.equals("call_in_rec_on") || recordedeventType.equals("call_out_rec_on")) {
                                        sendCallDataToServer(this.fileUrl);
                                    } else {
                                        sendCallDataToServer(this.fileUrl);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                //Log.d("Reupload_activities", "Event type null");
                if (!new File(filePath).exists()) {
                    deleteEntryFromDB(filePath);
                    //Log.d("Reupload_activities", "Event type null");
                }
            }
        } catch (Exception e) {
            //Log.d("Exception", "exception in Upload Audio");
        }
    }

    private void sendCallDataToServer(String url) {

        if(sendTo == null && (getApplicationContext() != null)) {
            CommonUtils.callLogApi(getApplicationContext(), "Inside Activities upload");
        } else if(sendTo != null && sendTo.isEmpty() && (getApplicationContext() != null)) {
            CommonUtils.callLogApi(getApplicationContext(), "Inside Activities upload");
        }

//        //Log.d("ReuploadService", " sendCallDataToServer "+sendTo);
        //sendTo = NotificationData.dialledCustomerNumber;

        OneViewScreenMail oneViewScreenMail = new OneViewScreenMail();
        oneViewScreenMail.setFrom(from);
        oneViewScreenMail.setTo(sendTo);
        oneViewScreenMail.setActivity_type(eventType);
        oneViewScreenMail.setStart_time(startTime);
        oneViewScreenMail.setEnd_time(endTime);
        oneViewScreenMail.setUrl(url);
        oneViewScreenMail.setSubject(subject);
        oneViewScreenMail.setMessage(message);
        oneViewScreenMail.setLongitude(longi);

        if(parent != null && !parent.isEmpty()) {
            if (parent.equals("1")) {
                oneViewScreenMail.setParent(1);
            }
        }
        oneViewScreenMail.setLatitude(lat);
        String customerName = "", customerNumber;
        if (eventType.equals("call_in_rec_off") || eventType.equals("call_in_rec_on") || eventType.equals("sms_in")
                || eventType.equals("call_missed") ||  eventType.equals("call_in_rnr")) {
            customerName = CommonUtils.setCallerName(getApplicationContext(), from);
            customerNumber = from;
        } else {
            customerName = CommonUtils.setCallerName(getApplicationContext(), sendTo);
            customerNumber = sendTo;
//            //Log.d("ReuploadService", "sendCallDataToServer:" +" Customer Number: "+customerNumber);
        }

/*
        if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
            if (screen != null && screen.equalsIgnoreCase("Bank1AfterCallActivity")) {
                String innumber1 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                String innumber2 = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING2, "");

                if(customerNumber != null && !customerNumber.isEmpty() && (customerNumber.equals(innumber1) || customerNumber.equals(innumber2) || customerNumber.contains(innumber2))) {
                    customerNumber = NotificationData.dialledCustomerNumber;
                }
            }
        }

*/
        if(uuid != null) {
            oneViewScreenMail.setuuid(uuid);
        }

//        Log.i("Reupload_SErvice","UUID is "+uuid);

        updateAppointmentToCompletedStatus(customerNumber);
        oneViewScreenMail.setNumber(customerNumber);
        if(customerName != null && !(customerName.isEmpty())) {
            oneViewScreenMail.setName(customerName);
        } else if(caller != null && !caller.isEmpty()) {
            oneViewScreenMail.setName(caller);
        } else {
            oneViewScreenMail.setName("");
        }

        if (disp_book_appointment == 0) {
            oneViewScreenMail.setDb_allowbooking(false);
        } else {
            oneViewScreenMail.setDb_allowbooking(true);
        }
        if (disp_call_rec == 0) {
            oneViewScreenMail.setShow_callrecording(false);
        } else {
            oneViewScreenMail.setShow_callrecording(true);
        }
        if (attachmentsArrayList != null) {
            oneViewScreenMail.setAttachmentListArrayList(attachmentsArrayList);
        }

        if(transcription_url != null && !(transcription_url.isEmpty())) {
            oneViewScreenMail.setTranscription_url(transcription_url);
        }
        //TODO: remove sending following when new digital brochure is implemented
        if (eventType.equals("call_in_rec_on") || eventType.equals("call_out_rec_on")) {
            oneViewScreenMail.setEventType("call");
        } else if (eventType.equals("call_missed")) {
            oneViewScreenMail.setEventType("missed_call");
        } else if (eventType.equals("live_meeting_rec")) {
            oneViewScreenMail.setEventType("live_meeting");
        } else if (eventType.equals("call_in_rec_off") || eventType.equals("call_out_rec_off")) {
            oneViewScreenMail.setEventType("call_record_off");
        } else if(eventType.equals("call_in_rnr") || eventType.equals("call_out_rnr")) {
            oneViewScreenMail.setEventType("call_rnr");
            oneViewScreenMail.setActivity_type("call_rnr");
        } else if(eventType.equals("call_dnd")) {
            oneViewScreenMail.setEventType("call_dnd");
        } else if(eventType.equals("call_invalid")) {
            String companyName = "";
            if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
                companyName = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
            }
            if(companyName.equals("OYO")) {
                oneViewScreenMail.setEventType("call_out_rec_off");
                oneViewScreenMail.setActivity_type("call_record_off");
            } else {
                oneViewScreenMail.setEventType("call");
                oneViewScreenMail.setActivity_type("call_out_rec_on");
            }

        }
        String leadSource = ApplicationSettings.getPref(AFTER_CALL_LEAD, "");
        if(leadSource == null || leadSource.isEmpty()) {
            leadSource = getLeadSource(customerNumber);
        }
        oneViewScreenMail.setSource_of_lead(leadSource);

        String customerToContact = ApplicationSettings.getPref(CUSTOMER_TO_CONTACT, "");
        if(customerToContact == null || customerToContact.isEmpty()) {
            customerToContact = getCustomerToContact(customerNumber);
        }
        oneViewScreenMail.setCustomer_to_contact(customerToContact);

        if(children != null && !children.isEmpty()) {
            oneViewScreenMail.setChildren(Integer.valueOf(children));
        }

        CommonUtils.checkToken();
        //Log.d("TransactionCount","Json obj is "+JSONParser.convertOneViewScreenMailToJSON(oneViewScreenMail));
//        Log.i("Reupload_SErvice","Post url is "+Urls.getPostActivityUrl());
        //Log.d("Reupload_SErvice","Json obj is "+JSONParser.convertOneViewScreenMailToJSON(oneViewScreenMail));
        JSONObject response = DataUploadUtils.postEmailContactInfo(Urls.getPostActivityUrl(), JSONParser.convertOneViewScreenMailToJSON(oneViewScreenMail));
        //Log.d("Reupload_SErvice","Response is "+response.toString());
        Log.d("ReuploadService","Response for Activity URL is "+response.toString());
        try {
            if (response != null) {
                //TODO: update the sales stage and appointment to completed
                //Log.d("Reupload Service", "Activity Response not null");
                isalive = false;
                String id = null, password = null;
                String event_type = eventType;
                clearData();
                String successResponse = response.getString("success");
                if (successResponse != null && !successResponse.isEmpty()) {
                    JSONObject responseJson = new JSONObject(successResponse);
                    if(responseJson.has("id")) {
                        id = responseJson.getString("id");
                    }
                    if(responseJson.has("password")) {
                        password = responseJson.getString("password");
                    }
                }
                //Added By Srinath.k
                if(customerNumber != null) {
                    checkKnowlarity(customerNumber,"Activities created with" +response.toString());
                }

                if (reminder_id != 0 && id != null) {
                        includeRecordingToReminder(id);
                    }
                if (send_sms == 1) {
                    if ((disp_call_rec == 0 && attachment != null && attachment.equals("on")) || disp_call_rec != 0) {
                        if (msg_recepient_no != null && !msg_recepient_no.equalsIgnoreCase("")) {

                            String body = sms_body + "URL is : ";
                         /*   body = body + "http://smarter-biz.com/cmail_mails/" + id;*/
                            body = body + Urls.SERVER_ADDRESS+"/cmail_mails/" + id;
                            if (SmartUser.getGroupId() > 0) {
                                int groupId = SmartUser.getGroupId();
                                body = body + "/" + groupId;
                            }
                            body = body + "\n" + "Password is : " + password;
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(msg_recepient_no, null, body, null, null);
                        }
                    }
                } else if (event_type != null && event_type.equals("call_missed")) {
                    boolean sendSMS = ApplicationSettings.getPref(AppConstants.AUTOSMS_FOR_MISSED_CALLS, false);
                    String recipient_no = from;
                    if (sendSMS) {
                        if (recipient_no != null) {
                            String body = ApplicationSettings.getPref(AppConstants.USERINFO_JD_SMS_TEMPLATE, "");
                            String url1 = ApplicationSettings.getPref(AppConstants.USERINFO_JD_URL, "no url");
                            if (!url1.equals(null) && !url1.equals("null")) {
                                body = body + " " + url1;
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(recipient_no, null, body, null, null);
                            }
                        }
                    }
                }
            } else {
                //Log.d("Reupload Service", "Activity Response is null");
            }
        } catch (JSONException j) {
            j.printStackTrace();
        }
        checkForOfflineStatusEntry();
    }

    public void includeRecordingToReminder(String cmail_mail_id) {
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            updateAppointment(cmail_mail_id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isalive = false;
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private void updateAppointment(String cmail_mail_id) {
        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        getCalendarEntryInfo.setAppointment_id(Long.toString(reminder_id));
        getCalendarEntryInfo.setCmailId(cmail_mail_id);
        getCalendarEntryInfo.update_all_fields = false;
        new APIProvider.Update_CalendarEvents(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {

            }
        }).call();
    }

    private void updateAppointmentToCompletedStatus(String number) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(this);
            db = dbHelper.getWritableDatabase();
        }
        boolean afterCallPopup = ApplicationSettings.getPref(AppConstants.SETTING_AFTER_CALL_POPUP, true);
        if (!afterCallPopup) {
            Date date = new Date();
            String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "COMPLETED='" + 0 + "'";
            Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
            //Log.i("CompletedFlowStatus","Cursor count of reminder is "+cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String customer_number = cursor.getString(cursor.getColumnIndex("TO1"));
                    if (PhoneNumberUtils.compare(customer_number, number)) {
                        long localdbId = cursor.getLong(cursor.getColumnIndex("_id"));
                        String appointment_id = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
                        long startMls = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                        //Modified By Srinath.k
                        int flpCount = cursor.getInt(cursor.getColumnIndex("FLP_COUNT"));
                        updateLocalDB(localdbId);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        TimeZone tz = TimeZone.getTimeZone("UTC");
                        df.setTimeZone(tz);
                        Date calldate = new Date();
                        try {
                            calldate = df.parse(startTime);
                        } catch (Exception e) {
                            //Log.e("SMSComposeActivity", e.toString());
                        }
                        long callTime = calldate.getTime();
                        long tatValue = callTime - startMls;
                        Integer tat = (int) (long) (tatValue);
                        int tatInSec = tat / 60000;
                        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
                        getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
                        getCalendarEntryInfo.appointment_id = appointment_id;
                        getCalendarEntryInfo.responsestatus = "completed";
                        getCalendarEntryInfo.update_all_fields = false;
                        getCalendarEntryInfo.tat = tatInSec;
                        //Modified By Srinath.k
                        getCalendarEntryInfo.flp_count = "" + flpCount;
                        updateAppointmentCall(getCalendarEntryInfo);
                    }
                    cursor.moveToNext();
                }
            }
        }
    }

    private void updateLocalDB(long localDBId) {
        if(!db.isOpen()) {
            MySql dbHelper = MySql.getInstance(this);
            db = dbHelper.getWritableDatabase();
        }
        ContentValues cv = new ContentValues();
        cv.put("COMPLETED", 1);
        if (db != null) {
            db.update("remindertbNew", cv, "_id=" + localDBId, null);
        } else {
            //Log.e("SmsCompose", "reminder db is null");
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private void updateAppointmentCall(GetCalendarEntryInfo getCalendarEntryInfo) {
        //Log.i("CompletedStatusFlow","In update appointment call" +getCalendarEntryInfo.responsestatus);
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.Update_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        }
    }

    private void checkForOfflineStatusEntry() {
        //Log.d("Urls", "Upload Cmail Else Part - Inside calling checkForOfflineStatusEntry()");
        if (db == null || (db != null && !db.isOpen())) {
            MySql dbHelper = new MySql(getApplicationContext(), "mydb", null, AppConstants.dBversion);
            db = dbHelper.getWritableDatabase();
        }

        Cursor cursor = null;
        if (ApplicationSettings.containsPref(AppConstants.UPLOAD_STATUS2)) {
            boolean uploadStatus2 = ApplicationSettings.getPref(AppConstants.UPLOAD_STATUS2, false);
            if (!uploadStatus2) {
                String selection = "UPLOAD_STATUS=0";
                cursor = db.query("SmarterContact", null, selection, null, null, null, null);
                //cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=0", null);
            } else {
                cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=2", null);
            }
        } else {
            cursor = db.rawQuery("SELECT * FROM SmarterContact where UPLOAD_STATUS=2", null);
        }

        //       Log.i("Reupload","Offline status Db count in reupload is "+dbCursor.getCount());
        cursor.moveToFirst();
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = calendar.getTimeZone();
        while (!cursor.isAfterLast()) {

            GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();

            //getCalendarEntryInfo.order_potential = orderValueEdittext.getText().toString();
            String currentStatus = cursor.getString(cursor.getColumnIndex("STATUS"));
            getCalendarEntryInfo.status = currentStatus + "(" + cursor.getCount() + ")";
            //getCalendarEntryInfo.status = cursor.getString(cursor.getColumnIndex("STATUS"));
            getCalendarEntryInfo.notes = cursor.getString(cursor.getColumnIndex("NOTES"));
            String subStatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
            getCalendarEntryInfo.subStatus1 = subStatus1 + "(" + SmarterSMBApplication.currentAppState + ")";
            //getCalendarEntryInfo.subStatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
            getCalendarEntryInfo.subStatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
            getCalendarEntryInfo.caller_number = cursor.getString(cursor.getColumnIndex("NUMBER"));
            getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
            //getCalendarEntryInfo.latitude = Double.toString(latitude);
            //getCalendarEntryInfo.longitude = Double.toString(longitude);
            if(getCalendarEntryInfo.caller_number == null && (getApplicationContext() != null)) {
                CommonUtils.callLogApi(getApplicationContext(), "Inside Sales upload");
            } else if(getCalendarEntryInfo.caller_number != null && getCalendarEntryInfo.caller_number.isEmpty() && (getApplicationContext() != null)) {
                CommonUtils.callLogApi(getApplicationContext(), "Inside Sales upload");
            }

            String eventStartDate = cursor.getString(cursor.getColumnIndex("EVENT_START_DATE"));
            //Log.d("Urls", "Reupload Service - Event Start Date " + eventStartDate);
            getCalendarEntryInfo.event_start_date = eventStartDate;

            String leadSource = ApplicationSettings.getPref(AFTER_CALL_LEAD, "");
            if(leadSource == null || leadSource.isEmpty()) {
                leadSource = getLeadSource(getCalendarEntryInfo.caller_number);
            }
            getCalendarEntryInfo.lead_source = leadSource;

            String customerToContact = ApplicationSettings.getPref(CUSTOMER_TO_CONTACT, "");
            if(customerToContact == null || customerToContact.isEmpty()) {
                customerToContact = getCustomerToContact(getCalendarEntryInfo.caller_number);
            }
            getCalendarEntryInfo.customer_id = customerToContact;

            getCalendarEntryInfo.wrapup = cursor.getString(cursor.getColumnIndex("WRAPUP"));
            getCalendarEntryInfo.extranotes = cursor.getString(cursor.getColumnIndex("EXTRANOTES"));
            getCalendarEntryInfo.transactionId = cursor.getString(cursor.getColumnIndex("UUID"));

            uploadOfflineSalesStatusIntoServer(getCalendarEntryInfo);
            cursor.moveToNext();
        }
    }

    private void uploadOfflineSalesStatusIntoServer(GetCalendarEntryInfo getCalendarEntryInfo) {
        final GetCalendarEntryInfo getCalendarEntryInfo1 = getCalendarEntryInfo;
        new APIProvider.Update_SalesStatus(getCalendarEntryInfo, 0, null, null, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                if (data != null) {
                    CommonUtils.saveBankContactInfoToSmartContactDB(getApplicationContext(), getCalendarEntryInfo1.caller_number, "", getCalendarEntryInfo1.status, getCalendarEntryInfo1.notes, "", "", getCalendarEntryInfo1.extranotes, getCalendarEntryInfo1.lead_source, getCalendarEntryInfo1.wrapup, getCalendarEntryInfo1.event_start_date, getCalendarEntryInfo1.transactionId);
                }
            }
        }).call();
    }

    private void sendAmeyoReminderToServer(Cursor ameypReminderCursor) {

        if (ameypReminderCursor.getCount() >= 1) {
            //ameypReminderCursor.moveToFirst();
            try {
                while (ameypReminderCursor.moveToNext()) {
                    CommonUtils.checkToken();
                    final int dbid = ameypReminderCursor.getInt(ameypReminderCursor.getColumnIndex("_id"));
                    String startTime = ameypReminderCursor.getString(ameypReminderCursor.getColumnIndex("CALL_STARTIME"));
                    String startDidNumber = ameypReminderCursor.getString(ameypReminderCursor.getColumnIndex("TO1"));
                    //Log.d("BeforeStartTime", startTime);
                    //Log.d("BeforeStartTime", startTime);
                    //String timeStamp = getTimeStamp(startTime);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                    Date date1 = null;
                    try {
                        date1 = format.parse(startTime);
                    } catch (Exception e) {
                        //Log.d("ReuploadService", e.toString());
                    }
                    if (date1 != null) {
                        //String number = getCloudNumber(date1.getTime());
                        String number = getCloudNumber(startTime);
                        if (number != null && !(number.isEmpty())) {
                            uploadReminderEntryToServerByChangingNumber(db, dbid, number);
                        }
                    }
                }
            } finally {
                ameypReminderCursor.close();
            }
        }

    }

    private void uploadReminderEntryToServerByChangingNumber(final SQLiteDatabase db, final int dbId, String custumerNum) {

        Cursor dbCursor = db.rawQuery("SELECT * FROM AmeyoReminderTable where _id=" + dbId, null);
        if (dbCursor != null) {
//            Log.i("Reupload", "Calendar Db count in reupload is " + dbCursor.getCount());
            dbCursor.moveToFirst();
            //pushNotifCursor.moveToFirst();
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = calendar.getTimeZone();
            /*    while (!dbCursor.isAfterLast()) {*/
            id = dbCursor.getInt(dbCursor.getColumnIndex("_id"));
            long startTime = dbCursor.getLong(dbCursor.getColumnIndex("START_TIME"));
            long endTime = dbCursor.getLong(dbCursor.getColumnIndex("END_TIME"));
            //ToDO convert millisec to date
            Date date = new Date();
            date.setTime(startTime);
            String start_time = CommonUtils.getTimeFormatInISO(date);
            date.setTime(endTime);
            String end_time = CommonUtils.getTimeFormatInISO(date);
            String location = dbCursor.getString(dbCursor.getColumnIndex("LOCATION"));

            String appointment_id = dbCursor.getString(dbCursor.getColumnIndex("APPOINTMENT_ID"));

            String subject = dbCursor.getString(dbCursor.getColumnIndex("SUBJECT"));
            String notes = dbCursor.getString(dbCursor.getColumnIndex("NOTES"));
            String company_name = dbCursor.getString(dbCursor.getColumnIndex("COMPANY_NAME"));
            String designation = dbCursor.getString(dbCursor.getColumnIndex("DESIGNATION"));
            String calrec_url = "";
            if (dbCursor.getString(dbCursor.getColumnIndex("CALLREC_URL")) != null)
                calrec_url = dbCursor.getString(dbCursor.getColumnIndex("CALLREC_URL"));
            String caller_name = "";
            if (dbCursor.getString(dbCursor.getColumnIndex("TONAME")) != null) {
                caller_name = dbCursor.getString(dbCursor.getColumnIndex("TONAME"));
            }
            //TODO: caller number should be from for incoming call
            //String caller_number = dbCursor.getString(dbCursor.getColumnIndex("TO1"));

            String caller_number = "";
            if (custumerNum != null) {
                caller_number = custumerNum;
//                Log.i("Reupload", "Ameyo Calendar Number" + caller_number);
            }

            String website = dbCursor.getString(dbCursor.getColumnIndex("WEBSITE"));
            String status = dbCursor.getString(dbCursor.getColumnIndex("STATUS"));
            String emailId = dbCursor.getString(dbCursor.getColumnIndex("EMAILID"));

            String cmail_mailId = dbCursor.getString(dbCursor.getColumnIndex("CMAIL_MAILID"));
            String latitude = dbCursor.getString(dbCursor.getColumnIndex("LAT"));
            String longitude = dbCursor.getString(dbCursor.getColumnIndex("LONG"));
            String orderPotential = dbCursor.getString(dbCursor.getColumnIndex("ORDER_POTENTIAL"));
            if (orderPotential.equalsIgnoreCase("null")) {
                orderPotential = "";
            }
            String source_of_leaad = dbCursor.getString(dbCursor.getColumnIndex("LEAD_SOURCE"));
            String product_type = dbCursor.getString(dbCursor.getColumnIndex("PRODUCT_TYPE"));
            String company_address = dbCursor.getString(dbCursor.getColumnIndex("COMPANY_ADDRESS"));
            int upload_status = dbCursor.getInt(dbCursor.getColumnIndex("UPLOAD_STATUS"));
            String appointment_type = dbCursor.getString(dbCursor.getColumnIndex("APPOINTMENT_TYPE"));
            int completed = dbCursor.getInt(dbCursor.getColumnIndex("COMPLETED"));
            String assign_to = dbCursor.getString(dbCursor.getColumnIndex("ASSIGN_TO"));
            String alert_before = dbCursor.getString(dbCursor.getColumnIndex("ALARMSETTO"));
            String cc_email = dbCursor.getString(dbCursor.getColumnIndex("CCEMAIL"));
            String flpCount = dbCursor.getString(dbCursor.getColumnIndex("FLP_COUNT"));
            String created_at = dbCursor.getString(dbCursor.getColumnIndex("CREATED_AT"));
            String substatus1 = dbCursor.getString(dbCursor.getColumnIndex("SUBSTATUS1"));
            String substatus2 = dbCursor.getString(dbCursor.getColumnIndex("SUBSTATUS2"));

            if (source_of_leaad != null && (!source_of_leaad.isEmpty())) {
                source_of_leaad = "cloudTelephony";
            }

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
            if (flpCount != null) {
                getCalendarEntryInfo.flp_count = flpCount;
                //Log.d("REUPLOADSERVICE", "FLPCOUNT" + getCalendarEntryInfo.flp_count);
            } else {
                getCalendarEntryInfo.flp_count = "1";
                //Log.d("REUPLOADSERVICE", "FLPCOUNT" + getCalendarEntryInfo.flp_count);
            }
            if (cc_email != null) {
                getCalendarEntryInfo.setCcMail(cc_email);
            }

            if(created_at != null && !(created_at.isEmpty())) {
                getCalendarEntryInfo.created_at = created_at;
            }
            //Log.i("Reupload", "Alarm before is " + alert_before);


            if (appointment_id != null && !appointment_id.equals("")) {
                getCalendarEntryInfo.appointment_id = appointment_id;
            }

            if(substatus1 != null && !substatus1.isEmpty()) {
                getCalendarEntryInfo.subStatus1 = substatus1;
            }

            if(substatus2 != null && !substatus2.isEmpty()) {
                getCalendarEntryInfo.subStatus2 = substatus2;
            }

               /* if (upload_status == 0) {*/
            //TODO: check for one more field whether update or new appointment
            JSONObject calendarJson = JSONParser.getnewJsonObjFromCalendar(getCalendarEntryInfo);
            Log.d("ReuploadService","uploadReminderEntryToServerByChangingNumber- calendarJson "+calendarJson);
            JSONObject responsejsonObject = DataUploadUtils.postJsonData(Urls.getCalendarCreateUrl(), calendarJson);
            Log.d("ReuploadService","uploadReminderEntryToServerByChangingNumber- responsejsonObject "+responsejsonObject);

            if (responsejsonObject != null && responsejsonObject.has("success")) {
                try {
                    String responseJson = responsejsonObject.getString("response");
                    JSONObject responseJsonObj = new JSONObject(responseJson);
                    if (responseJsonObj.has("Id")) {
                        db.delete("AmeyoReminderTable", "_id=" + dbId, null);
                        //Log.d("AmeyoReminderTable" , "table deled with id"+dbId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            dbCursor.close();
        }
    }

    private String getTimeStamp(String startTime) {
        String didNumber = ApplicationSettings.getPref(AppConstants.CLOUD_TELEPHONY, "");

        String strArray[] = didNumber.split(",");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        Date calldate = new Date();
        try {
            calldate = df.parse(startTime);
        } catch (Exception e) {
            //Log.e("SMSComposeActivity", e.toString());
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String timeStamp = format.format(calldate);
        timeStamp = timeStamp + "Z";
        return timeStamp;
    }

    private String getCloudNumber(String startTime) {
        String number = "";
        String selection = "UPLOAD_STATUS=" +  "'" + 1 + "'" + " AND " + "STARTTIME=" + "'" + startTime + "'";
        //Log.d("AmeyoReminders", "StartTime"+startTime);
        //Log.d("CloudNumber","StartTime"+startTime);
        Cursor cursor = db.query("ameyo_entries", null, selection, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            number = cursor.getString(cursor.getColumnIndex("TO1"));
            //Log.d("CloudNumber","getNumber"+number);
        } else {
            //Log.d("CloudNumber","No Number");
        }
        return number;
    }

    private void checkForTranscriptFiles() {
        if (CommonUtils.isNetworkAvailable(this)) {
            try {
                File dir = new File(Environment.getExternalStorageDirectory() + "/TextScripts/");
                if (dir != null && dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    if(files != null) {
                        for (int i = 0; i < files.length; i++) {
                            if (files[i].exists()) {
                                try {
                                    // boolean uploadCheck = DataUploadUtils.uploadTranscriptToServer(files[i].getAbsolutePath(), Urls.getUploadFileUrl());
                                   /* if (uploadCheck) {
                                        files[i].delete();
                                    }*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private String checkForTranscriptFiles(String num, String filePath) {
        String transUrl = "";
        if (CommonUtils.isNetworkAvailable(this)) {
            try {

                File file = new File(filePath);
                if(file.exists()) {
                    String transFile = DataUploadUtils.uploadTranscriptToServer(num, file.getAbsolutePath(), Urls.getUploadFileUrl());
                    file.delete();
                    if (transFile != null && !(transFile.isEmpty())) {
                        JSONObject jsonObject = new JSONObject(transFile);
                        if(jsonObject.has("trans_url")) {
                            transUrl = jsonObject.getString("trans_url");
                            return transUrl;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return transUrl;
    }

}
