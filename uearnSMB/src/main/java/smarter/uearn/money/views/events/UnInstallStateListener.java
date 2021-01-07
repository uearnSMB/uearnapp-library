package smarter.uearn.money.views.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import smarter.uearn.money.utils.CommonUtils;

/**
 * Created by Srinath on 27-03-2017.
 */

public class UnInstallStateListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("UnInstallStateListener", "Application package removed");
        //CommonUtils.deleteInternalFiles();
    }
}
