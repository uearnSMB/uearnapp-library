package smarter.uearn.money.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import smarter.uearn.money.utils.CommonUtils;

/**
 * Created by Srinath on 16-Jan-17.
 */
public class PowerOff extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String powerOff_time = CommonUtils.getTimeFormatInISO(new java.sql.Date(System.currentTimeMillis()));
        //Log.d("SignOuTimeStamp", powerOff_time);
    }
}
