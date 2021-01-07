package smarter.uearn.money.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;

import smarter.uearn.money.services.HandleAppStatusService;
import smarter.uearn.money.utils.CommonUtils;

/**
 * Created by smb on 29-12-2016.
 */

public class ShutdownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        float percentage = (level/ (float) scale) * 100;
        //Log.d("Batterypercentage",""+ percentage );

        String signout_time = CommonUtils.getTimeFormatInISO(new java.sql.Date(System.currentTimeMillis()));

        //setnextAlarm(context, id);
        Intent intent1 = new Intent(context, HandleAppStatusService.class);
        //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("batteryStatus","" +percentage);
        intent1.putExtra("timeStamp", signout_time);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }

        //Log.d("SignOuTimeStamp", signout_time);

    }
}
