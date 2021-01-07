package smarter.uearn.money.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import smarter.uearn.money.utils.CommonUtils;

/**
 * Created by kavya on 8/26/2015.
 */
public class UpdateAlarmTriggerService extends WakefulBroadcastReceiver {

    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        updateContactString(context);
    }

    public void updateContactString(Context context) {
        //Log.i("CalendarFlow","****** In update calendar Trigger method*****");
        //TODO: add the delete before 30 days calendar data from local db
        try {
            CommonUtils.calculateNextAlarm(context);
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("Error::::::", "getting contacts");
        }
    }

    public void setAlarmTrigger(long millisec) {
        //Log.i("CalendarFlow","In set alarm of contacts alarm receiver");
        try {
            if(context != null) {
                Intent alarmIntent = new Intent(context, UpdateAlarmTriggerService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                manager.set(AlarmManager.RTC_WAKEUP, millisec, pendingIntent);
            }
        }catch (SecurityException se) {
            //Log.e("SecurityException",se.toString());
        }
    }

}
