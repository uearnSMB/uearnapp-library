package smarter.uearn.money.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import smarter.uearn.money.models.AppStatus;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;

/**
 * Created by Srinath on 16-Jan-17.
 *
 * @Modified Dilip Kumar
 */
public class HandleAppStatusService extends Service {


    //private float percentage;
    private String percentage;
    private String timeStamp;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent.hasExtra("batteryStatus")) {
            try {
                //percentage = intent.getIntExtra("batteryStatus", 0);
                percentage = intent.getStringExtra("batteryStatus");
            } catch (ClassCastException e) {
                percentage = "0";
            }
        }

        if (intent.hasExtra("timeStamp")) {
            timeStamp = intent.getStringExtra("timeStamp");
        }
        CommonUtils.deleteInternalFiles();
        postAppStatus();
        return START_STICKY;
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
        stopSelf();
    }

}
