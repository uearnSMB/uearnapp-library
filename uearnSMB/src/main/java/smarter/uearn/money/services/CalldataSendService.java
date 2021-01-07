package smarter.uearn.money.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import smarter.uearn.money.models.AppStatus;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;

import java.io.File;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CalldataSendService extends IntentService {
    private SQLiteDatabase db;
    private String percentage;
    private String timeStamp;
    public CalldataSendService() {
        super("CalldataSendService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        if (intent != null) {
            MySql dbHelper = MySql.getInstance(getApplicationContext());
            db = dbHelper.getWritableDatabase();

            if (intent.hasExtra("batteryStatus")) {
                try {
                    percentage = intent.getStringExtra("batteryStatus");
                } catch (ClassCastException e) {
                    percentage = "0";
                }
            }
            if (intent.hasExtra("timeStamp")) {
                timeStamp = intent.getStringExtra("timeStamp");
            }
            postAppStatus();

            //CommonOperations.subscribeNotificationsCall(true, getApplicationContext());
            //sendCallDataToServer(db);
            removeLocalRecorderFiles();
            removeLocalScriptFiles();

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

    private void removeLocalRecorderFiles() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/callrecorder/");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    private void removeLocalScriptFiles() {
        File dir2 = new File(Environment.getExternalStorageDirectory() + "/TextScripts/");

        if (dir2.isDirectory()) {
            String[] children = dir2.list();
            File[] files = dir2.listFiles();
            for (int i = 0; i < children.length; i++) {
                //TODO: Check the code
                if(files[i].exists()) {
                    new File(dir2, children[i]).delete();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void postAppStatus() {
        String userId = "";
        if(ApplicationSettings.containsPref(AppConstants.USERINFO_ID)) {
            if(ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            }
        }
        boolean gpsSettings = ApplicationSettings.getPref(AppConstants.GPS_SETTINGS, false);
        boolean isRecording = ApplicationSettings.getPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
        String activityType = "signout";
        int version_code = 0;
        if(getApplicationContext() != null) {
            version_code = CommonUtils.getVersionCode(getApplicationContext());
        }
        String versionNumber = ""+version_code;

        if ((userId != null) && (timeStamp != null) && (activityType != null) && (versionNumber != null)) {
            AppStatus appstatus = new AppStatus(activityType, "" + gpsSettings, "" + isRecording, "" + percentage, userId, timeStamp, versionNumber);
            //Log.d("app status", " " + appstatus);
            if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                new APIProvider.PostAppStatus(appstatus, 0, new API_Response_Listener<AppStatus>() {
                    @Override
                    public void onComplete(AppStatus data, long request_code, int failure_code) {
                        //Log.d("Log out timestamp", "Data is " + data);
                    }
                }).call();
            }
        }
    }

}
