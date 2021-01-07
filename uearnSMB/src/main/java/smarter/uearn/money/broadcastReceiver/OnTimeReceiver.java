package smarter.uearn.money.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import smarter.uearn.money.services.CreateNotificationService;

/**
 * Created by smb on 03-01-2017.
 */

public class OnTimeReceiver extends BroadcastReceiver {

    public String event="No Title", description="No details were added";
    public long id=0;
    private boolean check = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.hasExtra("id")){
            id=intent.getLongExtra("id", 0);

        }

        //setnextAlarm(context, id);
        Intent intent1 = new Intent(context, CreateNotificationService.class);
        //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("id", id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }

    }
}
