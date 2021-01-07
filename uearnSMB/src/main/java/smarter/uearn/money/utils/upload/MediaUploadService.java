package smarter.uearn.money.utils.upload;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import smarter.uearn.money.models.AttachmentList;
import smarter.uearn.money.models.OneViewScreenMail;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.callrecord.CallDetails;
import smarter.uearn.money.dialogs.LiveMeetingSMSDialog;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.JSONParser;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;


/**
 * <p>
 * Title: MediaUploadService Intent Service
 * </p>
 * <p>
 * Description: this service intent helps to upload LiveMeeting data that are required for SMARTER SMB with various form
 * data, including audio and image files.
 * </p>
 *
 * @author Navine
 * @version 2.0
 *          <p/>
 *          Last Modified on 11-Jan-2016 - Not Final Code
 *          <p/>
 *          Code Optimization partially completed
 *          <p/>
 *          <p/>
 *          Changes done : Send Audio data to server - signature changed.
 */

public class MediaUploadService extends IntentService {

    private static final String WAKELOCK_KEY = "A_UPLOAD";

    private String TAG = "MediaUploadService";
    private String latitude, longitude;
    private ArrayList<AttachmentList> attachmentsArrayList;

    public MediaUploadService() {
        super("MediaUploadService");
    }

    public boolean deleteFile(String selectedFilePath) {

        File file = new File(selectedFilePath);
        //if file exists then delete and return true else return false
        return file.exists() && file.delete();
    }

    public void insertDB(SQLiteDatabase db, CallDetails callDetails) {
        ContentValues cv = new ContentValues();
        cv.put("EMAIL", ApplicationSettings.getPref(AppConstants.PREF_EMAIL_ID, null));
        cv.put("EVENT_TYPE", AppConstants.CALL_EVENT_TYPE);
        cv.put("PARENT", 0);

        if (callDetails.startTime != null)
            cv.put("STARTTIME", callDetails.startTime.toString());

        if (callDetails.endTime != null)
            cv.put("ENDTIME", callDetails.endTime.toString());

        cv.put("UNREAD", "false");
        cv.put("SUBJECT", "CALL RECORD");
        String callerName = CommonUtils.getContactName(getApplicationContext(), callDetails.phoneno);
        cv.put("CALLER", callerName);
        cv.put("FILE_PATH", callDetails.recordFile);
        if (callDetails.incoming) {
            if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                cv.put("FROM1", callDetails.phoneno);
                cv.put("TO1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                cv.put("MESSAGE", "Incoming Call");
            }

        } else {
            if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                cv.put("FROM1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                cv.put("TO1", callDetails.phoneno);
                cv.put("MESSAGE", "Outgoing Call");
            }
        }
        db.insert("mytbl", null, cv);

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private SQLiteDatabase initDB() {
        MySql dbHelper = MySql.getInstance(this);
        return dbHelper.getWritableDatabase();
    }

    private void initCallDetails(Intent intent, CallDetails call) {
        if (intent.hasExtra("file_path")) {
            call.recordFile = intent.getStringExtra("file_path");
            call.incoming = intent.getBooleanExtra("incoming", false);
            call.phoneno = intent.getStringExtra("phoneno");
            call.startTime = new Date(intent.getLongExtra("startTime", 0));
            call.endTime = new Date(intent.getLongExtra("endTime", 0));
        }
        if(intent.hasExtra("latitude")) {
            latitude = intent.getStringExtra("latitude");
        }
        if(intent.hasExtra("logitude")) {
            longitude = intent.getStringExtra("logitude");
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

        JSONObject responseAudio = null;
        CallDetails callDetails = new CallDetails();
        String audioPath;
        String fileUrl;

        SQLiteDatabase db;
        PowerManager.WakeLock mWakeLock = null;

        String url = Urls.getUploadFileUrl();

        Boolean isLiveMeeting = ApplicationSettings.getPref(AppConstants.FROM_CMAIL, false);
        if (!isLiveMeeting) {
            initCallDetails(intent, callDetails);
            audioPath = callDetails.recordFile;
        } else {
            attachmentsArrayList = (ArrayList<AttachmentList>) intent.getSerializableExtra("Upload_File_List");
            audioPath = intent.getStringExtra("file_path");
        }
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getApplicationContext()
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    WAKELOCK_KEY);
        }
        mWakeLock.acquire();
        db = initDB();
        if (audioPath != null) {
            if (isLiveMeeting) {
                responseAudio = DataUploadUtils.uploadAudioFileToServer(audioPath, "live_meeting_rec", "", "", url);

            } else {
                String name = null;
                //insertDB(callDetails);   Insert into DB only if upload fails

                if (callDetails.phoneno != null && !callDetails.phoneno.equalsIgnoreCase(""))
                    name = CommonUtils.getContactName(getApplicationContext(), callDetails.phoneno);

                if (name == null) name = "";

                if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                    if (callDetails.phoneno != null && !callDetails.phoneno.equalsIgnoreCase(""))
                        responseAudio = DataUploadUtils.uploadAudioFileToServer(audioPath, "call", callDetails.phoneno, name, url);
                } else {
                    //Log.i("Tag", "Network not available");
                }
            }
        }

        if (responseAudio != null) {
            Boolean isDeleted = deleteFile(audioPath);

            if (isDeleted) {
                //Log.i(TAG, "Delete file " + audioPath + " successful");
            } else {
                //Log.e(TAG, "Delete file " + audioPath + " FAILURE ");
            }

            try {
                fileUrl = responseAudio.getString("url");
                //recordedeventType = responseAudio.getString("call_type");
                //recordedeventCallPhoneNUmber = responseAudio.getString("phone");
                //recordedeventCallContactName = responseAudio.getString("contact_name");
                if (fileUrl != null) {
                    sendCallDataToServer(fileUrl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Log.e(TAG, "JSON Parsing error on upload response");
            }
        } else {
            //Upload was a failure.
            insertDB(db, callDetails);
        }

        mWakeLock.release();
        db.close();
    }

    private void sendCallDataToServer(String url) {
        String checkType;
        try {
            Boolean isLiveMeeting = ApplicationSettings.getPref(AppConstants.FROM_CMAIL, false);
            if (isLiveMeeting) {
                ApplicationSettings.putPref(AppConstants.FROM_CMAIL, false);
                OneViewScreenMail oneViewScreenMail = new OneViewScreenMail();
                oneViewScreenMail.setSubject("Live Meeting");
                oneViewScreenMail.setActivity_type("live_meeting_rec");
                oneViewScreenMail.setEventType("live_meeting");
                String cname = CommonUtils.setCallerName(getApplicationContext(), ApplicationSettings.getPref(AppConstants.TO_EMAIL, "").replace(" ", "").trim());
                oneViewScreenMail.setName(cname);
                oneViewScreenMail.setNumber(ApplicationSettings.getPref(AppConstants.TO_EMAIL, "").replace(" ", "").trim());
                checkType = "live_meeting_rec";
                if (latitude != null && longitude != null) {
                    oneViewScreenMail.setLatitude(latitude);
                    oneViewScreenMail.setLongitude(longitude);
                }
                oneViewScreenMail.setUrl(url);
                oneViewScreenMail.setStart_time(CommonUtils.getTimeFormatInISO(new Date(System.currentTimeMillis())));
                if (attachmentsArrayList != null && attachmentsArrayList.size() > 0) {
                    oneViewScreenMail.setAttachmentsSerilizableList(attachmentsArrayList);
                }
                if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "no_email") != null) {
                    oneViewScreenMail.setFrom(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "no_email"));
                }
                if (ApplicationSettings.getPref(AppConstants.TO_EMAIL, "") != null) {
                    oneViewScreenMail.setTo(ApplicationSettings.getPref(AppConstants.TO_EMAIL, ""));
                }
                //Added By Srinath

                //TODO: should send a json array
                JSONArray attachmentJsonArray = new JSONArray();

                //TODO have to add the attachment name
                if (attachmentsArrayList != null) {
                    if (attachmentsArrayList.size() > 0) {
                        for (int i = 0; i < attachmentsArrayList.size(); i++) {
                            AttachmentList attachmentslists = attachmentsArrayList.get(i);
                            JSONObject attachmentJsonObj = new JSONObject();
                            attachmentJsonObj.put("filename", attachmentslists.attachment_name);
                            attachmentJsonObj.put("url", attachmentslists.attachment_url);
                            attachmentJsonArray.put(attachmentJsonObj);
                        }
                    }
                }
                JSONObject responseChild = DataUploadUtils.postEmailContactInfo(Urls.getPostActivityUrl(), JSONParser.convertOneViewScreenMailToJSON(oneViewScreenMail));
                if (checkType.equals("live_meeting_rec")) {
                    String phoneNo = ApplicationSettings.getPref(AppConstants.TO_EMAIL, "no_email");
                    phoneNo = phoneNo.replace("-", "").replace(" ", "").trim();
                    boolean isPhoneNumber = Validation.isPhoneNumber(phoneNo);
                    if (isPhoneNumber) {
                        //LiveMeeting.isPhoneNumber = false;
                        if (responseChild != null) {
                            String success = responseChild.getString("success");
                            JSONObject jsonObject = new JSONObject(success);
                            String id = jsonObject.getString("id");
                            String password = jsonObject.getString("password");
                            if (ApplicationSettings.getPref(AppConstants.TO_EMAIL, "no_email") != null) {
                                if (ApplicationSettings.getPref(AppConstants.TO_EMAIL, "").length() > 0) {
                                    String messagetoUser = sendMessageText() + "URL: ";
                                   /* messagetoUser = messagetoUser + "http://smarter-biz.com/cmail_mails/" + id;*/
                                    messagetoUser = messagetoUser + Urls.SERVER_ADDRESS+"/cmail_mails/" + id;
                                    messagetoUser = messagetoUser + "\n"+ "Password: "+ password;
                                    String sendMessageNumber = ApplicationSettings.getPref(AppConstants.TO_EMAIL, "").replace(" ", "").trim();
                                    if (sendMessageNumber != null && !sendMessageNumber.equalsIgnoreCase("")) {
                                        Intent intent = new Intent(getApplicationContext(), LiveMeetingSMSDialog.class);
                                        intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("phonenumber", sendMessageNumber);
                                        intent.putExtra("message", messagetoUser);
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(MediaUploadService.this, "SMS Not Delivered!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException json) {

        }

    }
    public String sendMessageText() {
        return "Please find live meeting ";
    }
}
