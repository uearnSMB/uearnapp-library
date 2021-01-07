package smarter.uearn.money.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import smarter.uearn.money.utils.CommonUtils;

import java.util.Calendar;

/**
 * Created by kavya on 8/26/2015.
 */
public class DeleteBefore30daysAppointmentsService extends WakefulBroadcastReceiver {

    private Context context;
    private AlarmManager alarmMgr;
    @Override
    public void onReceive(Context context, Intent intent) {
        updateContactString(context);
    }

    public void updateContactString(Context context) {
        // Log.d("CalendarFlow","****** In update calendar Trigger method*****");
        try {
            CommonUtils.deleteBefore30daysCalendarEvents(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAlarmTrigger(Context context) {
        //Log.i("CalendarFlow","In set alarm of contacts alarm receiver"+context);
        try {
            Intent alarmIntent = new Intent(context, DeleteBefore30daysAppointmentsService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            // Set the alarm's trigger time to 12:00 .m.
            calendar.set(Calendar.HOUR_OF_DAY, 24);
            calendar.set(Calendar.MINUTE, 00);
            alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }catch (SecurityException se) {
            //Log.e("SecurityException",se.toString());
        }
    }

}
