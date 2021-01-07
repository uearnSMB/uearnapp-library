package smarter.uearn.money.views.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import smarter.uearn.money.callrecord.RecorderService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;

import static smarter.uearn.money.utils.SmarterSMBApplication.applicationContext;

public class HeadPhoneReciever extends BroadcastReceiver {

    private static final String TAG = "MainActivity";

    @Override public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    SmarterSMBApplication.isHeadPhone = false;
                    sendMessageToHeadphoneActivity("Headset is unplugged");
                    break;
                case 1:
                    SmarterSMBApplication.isHeadPhone = true;
                    sendMessageToHeadphoneActivity("Headset is plugged");
                    break;
                default:
                    Log.d(TAG, "I have no idea what the headset state is");
            }
        }
    }

    private static void sendMessageToHeadphoneActivity(String message) {
        Intent intent = new Intent("remote-dialer-request-event");
        intent.putExtra("result", message);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }
}