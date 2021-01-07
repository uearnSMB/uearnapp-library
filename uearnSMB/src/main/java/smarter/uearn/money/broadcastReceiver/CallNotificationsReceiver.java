package smarter.uearn.money.broadcastReceiver;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import smarter.uearn.money.activities.appointment.AppointmentViewActivity;

/**
 * Created on 06/06/17.
 *
 * @author dilipkumar4813
 * @version 1.0
 */

public class CallNotificationsReceiver extends BroadcastReceiver {

    public static final String CALL_ACTION = "call";
    public static final String VIEW_ACTION = "view";
    public static final String CALL_NUMBER = "number";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String APPOINTMENT_ID = "appointment_id";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        int id = extras.getInt(NOTIFICATION_ID, 1);
        String number = extras.getString(CALL_NUMBER);
        long appointmentId = extras.getLong(APPOINTMENT_ID);

        //Log.d("Notification ID", "" + id);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(100);

        if (intent.getAction() == VIEW_ACTION) {
            Intent resultIntent = new Intent(context, AppointmentViewActivity.class);
            resultIntent.putExtra("id", appointmentId);
           /* resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity(resultIntent); // Check if this block of code passes
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
           /* callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(callIntent);
        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}
