package smarter.uearn.money.views.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import smarter.uearn.money.callrecord.RecorderService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CallReciever extends BroadcastReceiver {
	
	public static String numberToCall="";
    public static RecorderService recorderService;
	@Override
	public void onReceive(final Context context, Intent intent) {
        //Log.i("CallReceiver","In on receive");
		// TODO Handle on call, another call dial
        if(recorderService == null) {
            if(RecorderService.recorderServiceInitialized) {
                return;
            } else {
                RecorderService.recorderServiceInitialized = true;
                Intent start_intent = new Intent(context, RecorderService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(start_intent);
                } else {
                    context.startService(start_intent);
                }
            }
        }

        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            if(intent.hasExtra(Intent.EXTRA_PHONE_NUMBER)) {
                numberToCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                MyPhoneStateListener.NUMBER = numberToCall;
            } else if(context != null){
                try {
                    SimpleDateFormat reportFormat = new SimpleDateFormat("EEE, d MMM yy");
                    Calendar mCalendar = Calendar.getInstance();
                    String reportDate = reportFormat.format(mCalendar.getTime());
                    int reportMinute = mCalendar.get(Calendar.MINUTE);
                    int reportHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                    String reportDateTime = "<br/><br/>Date: " + reportDate + "<br/>Time: " + reportHour + ":" + reportMinute;
                    int version_code = CommonUtils.getVersionCode(context);
                    String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                            ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + " <br/>Empty Number in Call Receiver " +reportDateTime
                            + "<br/> Current Activity: " + SmarterSMBApplication.currentActivity + "<br/><br/>App Version: " + version_code;

                    //ServiceApplicationUsage.callErrorLog(message);
                } catch ( Exception e) {
                    e.printStackTrace();
                }
            }
        }
	}
}