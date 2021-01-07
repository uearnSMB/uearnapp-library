package smarter.uearn.money.callrecord;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.views.events.CallReciever;
import smarter.uearn.money.views.events.MyPhoneStateListener;
import smarter.uearn.money.services.DeleteBefore30daysAppointmentsService;

public class RecorderService extends Service {
	
	private MyPhoneStateListener phoneStateListener = null;
	private TelephonyManager telephony;
	private Context context;
    public static String phoneNo = null;
	private static boolean triggerdDeleteAppointment = false;
	public static boolean recorderServiceInitialized = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		AppConstants.recorderService = this;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			startMyOwnForeground();
		else
			startForeground(1, new Notification());

		if (!triggerdDeleteAppointment) {
			DeleteBefore30daysAppointmentsService deleteService = new DeleteBefore30daysAppointmentsService();
			deleteService.setAlarmTrigger(context);
			triggerdDeleteAppointment = true;
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	private void startMyOwnForeground(){
		String NOTIFICATION_CHANNEL_ID = "smarter.uearn.money";
		String channelName = "My Background Service";
		NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
		chan.setLightColor(Color.BLUE);
		chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		assert manager != null;
		manager.createNotificationChannel(chan);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
		Notification notification = notificationBuilder.setOngoing(true)
				.setSmallIcon(R.drawable.small_logo)
				.setContentTitle("")
				.setPriority(NotificationManager.IMPORTANCE_MIN)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build();
		startForeground(2, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if ((flags & START_FLAG_REDELIVERY) !=0) {

		}
		if (phoneStateListener == null) {
			phoneStateListener = new MyPhoneStateListener(getApplicationContext());
			telephony = (TelephonyManager) context .getSystemService(Context.TELEPHONY_SERVICE);
			telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE|PhoneStateListener.LISTEN_SIGNAL_STRENGTHS|PhoneStateListener.LISTEN_SERVICE_STATE);
		}
//
//		boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
//		boolean showaftercall = ApplicationSettings.getPref(AppConstants.SHOW_AFTER_CALL_POPUP, false);
//
//		if(sequencialEndpoint || showaftercall) {
//			String connectedCustomer = ApplicationSettings.getPref(AppConstants.CONNECTED_CUSTOMER, "");
//			if(connectedCustomer != null && !connectedCustomer.isEmpty() && !ServiceHandler.callDisconnected) {
//				phoneStateListener.startCallRecording(connectedCustomer);
//			}
//		}
//
//		boolean truePredictive = ApplicationSettings.getPref(AppConstants.TRUE_PREDICTIVE, false);
//		if (truePredictive) {
//			if(NotificationData.source != null && !NotificationData.source.isEmpty() && NotificationData.source.equalsIgnoreCase("Agent disconnected")) {
//				phoneStateListener.stopCallRecording(NotificationData.dialledCustomerNumber);
//			} else {
//				String disConnectedCustomer = ApplicationSettings.getPref(AppConstants.CUSTOMER_DISCONNECT, "");
//				if (disConnectedCustomer != null && !disConnectedCustomer.isEmpty()) {
//					phoneStateListener.stopCallRecording(disConnectedCustomer);
//				}
//			}
//		} else {
//			String disConnectedCustomer = ApplicationSettings.getPref(AppConstants.CUSTOMER_DISCONNECT, "");
//			if (disConnectedCustomer != null && !disConnectedCustomer.isEmpty() && !ServiceHandler.callDisconnected) {
//				phoneStateListener.stopCallRecording(disConnectedCustomer);
//			}
//		}

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		CallReciever.recorderService = null;
		if (phoneStateListener != null) {
			 telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		     telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE|PhoneStateListener.LISTEN_SIGNAL_STRENGTHS|PhoneStateListener.LISTEN_SERVICE_STATE);
		}
		//TODO handle the case if recording is going on.
		super.onDestroy();
	}

    @Override
    public void onTaskRemoved(Intent rootIntent){
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//
//        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}
	
