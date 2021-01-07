package smarter.uearn.money.views.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.utils.AppConstants;

import static smarter.uearn.money.utils.SmarterSMBApplication.applicationContext;


public class ConnectivityReceiver extends BroadcastReceiver {

	private Context context;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		this.context = context;
		boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
		if(isAirplaneModeOn){
			sendMessageToUearnHome("sign_out");
			//signOut();
			// handle Airplane Mode on
		} else {
			// handle Airplane Mode off
		}
	}

	private static void sendMessageToUearnHome(String message) {
		Intent intent = new Intent("remote-dialer-request-event");
		intent.putExtra("result", message);
		LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
	}

//	private void signOut() {
//		AppConstants.SIGN_OUT = true;
//		if (AppConstants.SIGN_OUT) {
//			Handler mHandler = new Handler();
//			mHandler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					Intent intent = new Intent(context, UearnHome.class);
//					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//					context.startActivity(intent);
//				}
//			}, 1000L);
//		}
//
//	}
}
