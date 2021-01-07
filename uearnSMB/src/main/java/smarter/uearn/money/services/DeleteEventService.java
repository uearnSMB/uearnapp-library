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

import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;

public class DeleteEventService extends Service {

    int i=0;
    GetCalendarEntryInfo calendarEntryInfo;

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
        boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        if (!isLogout) {
            calendarEntryInfo = (GetCalendarEntryInfo) intent.getSerializableExtra("calenderEntryInfo");
            deleteEvent(calendarEntryInfo);
        }
            if (stopSelfResult(startId)) {
                //Log.d("DeleteEventService" , "Service Stopped"+ startId);
            }
            return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private  void deleteEvent(GetCalendarEntryInfo calendarEntryInfo) {
        final GetCalendarEntryInfo calendarEntyInfo = calendarEntryInfo;

        new APIProvider.Delete_CalendarEvent(calendarEntryInfo, 0, null, "Deleting Appointment..", new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                i++;
                 //Log.i("AppointmentList","Data is "+data + i );
                if(data != null && data.equals("successfully deleted calendar event")) {

                } else {
                    if(i<3) {
                        deleteEvent(calendarEntyInfo);
                    }else {
                        //Log.i("AppointmentList","inside Data is "+data + i );
                    }
                }

            }
        }).call();
    }

}
