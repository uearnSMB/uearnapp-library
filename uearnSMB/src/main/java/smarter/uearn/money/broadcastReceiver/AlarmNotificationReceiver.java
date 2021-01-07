package smarter.uearn.money.broadcastReceiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.FollowupsReports;
import smarter.uearn.money.utils.UserActivities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dilipkumar4813 on 09/02/17.
 * Broadcast for sending notification for overdue
 */

public class AlarmNotificationReceiver extends BroadcastReceiver {

    private int overdue = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Notification");
        wl.acquire();

        getOverdue(context);

        wl.release();
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmNotificationReceiver.class);
        intent.putExtra("time", Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 00);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    private void getOverdue(final Context context) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date startTime = cal.getTime();
        cal.setTime(new Date());
        Date endTime = cal.getTime();
        String requiredStartTime = CommonUtils.getTimeFormatInISO(startTime);
        String requiredEndTime = CommonUtils.getTimeFormatInISO(endTime);
        String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        UserActivities userActivities = new UserActivities(user_id, requiredStartTime, requiredEndTime);

        APIProvider.GetFollowUps getFollowUps = new APIProvider.GetFollowUps(userActivities, 1, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {

                if (data != null) {
                    FollowupsReports data1 = new FollowupsReports();
                    if ((FollowupsReports.getOverDueList() != null) && (FollowupsReports.getOverDueList().size() > 0)) {
                        ArrayList<String> list = FollowupsReports.getOverDueList();
                        String values[] = null;
                        ArrayList<String[]> valueArray = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                                String email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
                                StringBuilder builder = new StringBuilder(list.get(i));
                                if (list.get(i).contains(email)) {
                                    values = list.get(i).split(",");
                                    //Log.d("SPlit values", values.toString());
                                }
                            }
                        }
                        String headers = FollowupsReports.getHeaders();
                        String headersarray[];
                        if (headers != null && !headers.isEmpty()) {
                            headersarray = headers.split(",");
                            if (headersarray.length > 0) {
                                for (int i = 0; i < headersarray.length; i++) {
                                    String header = headersarray[i];
                                    if (header.contains("Not called at all")) {
                                        if (values != null) {
                                            String value = values[i];
                                            if (value != null) {
                                                overdue = Integer.parseInt(value);
                                                buildNotification(context);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        getFollowUps.call();
    }

    private void buildNotification(Context context) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentTitle("Your Overdue");
        notificationBuilder.setContentText("You have " + String.valueOf(overdue) + " Overdue Appointments");
        notificationBuilder.setSmallIcon(R.drawable.small_logo);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
