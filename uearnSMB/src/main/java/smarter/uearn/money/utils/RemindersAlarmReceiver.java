package smarter.uearn.money.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


import java.util.Calendar;

/**
 * Created by kavya on 8/26/2015.
 */
public class RemindersAlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
      // Log.i("Tag","In On Receive of reminders");
        getGoogleCalEvents(context);
    }

    public void setAlarm(Context context) {
        try {
            //Log.i("Tag", "In Set Alarm Of Appointments");
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, RemindersAlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            //set alarm every one hour
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), 1 * 60 * 60 * 1000, alarmIntent);
        }catch (SecurityException se) {

        }
    }

    public void getGoogleCalEvents(Context context){
        //Log.i("Tag","In Update reminders");
        try {
           // HomeScreen.getCalendarEvents(context);
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("Error::::::", "getting appointments");
        }
    }
}
