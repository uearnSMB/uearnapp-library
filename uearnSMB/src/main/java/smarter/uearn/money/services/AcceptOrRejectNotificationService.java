package smarter.uearn.money.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.activities.appointment.AppointmentViewActivity;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.CommonUtils;

import java.security.SecureRandom;
import java.util.Random;

public class AcceptOrRejectNotificationService extends IntentService {

	private SecureRandom random;
	String email_id, phone_number, name, type;
    int APPOINTMENT_ID = 16;
	GroupsUserInfo groupsUserInfo;
	Bundle bundle = null;
	public AcceptOrRejectNotificationService(){
		super("");
	}

	public AcceptOrRejectNotificationService(String name) {
		super(name);
		
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		random = new SecureRandom();
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
				.setContentTitle("App is running in background")
				.setPriority(NotificationManager.IMPORTANCE_MIN)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build();
		startForeground(2, notification);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			startMyOwnForeground();
		else
			startForeground(1, new Notification());

		if (intent.hasExtra("groupsUserInfoBundle")) {
			bundle = intent.getBundleExtra("groupsUserInfoBundle");
			groupsUserInfo = CommonUtils.convertBundleToGroupsUserInfo(bundle);
		}
        Intent acceptIntent = new Intent();
        acceptIntent.setAction("ACCEPT_TEAM_MEMBER");
		if (bundle != null)
			acceptIntent.putExtra("groupsUserInfoBundle", bundle);
		acceptIntent.putExtra("notification_id", APPOINTMENT_ID);
        Intent rejectIntent = new Intent();
        rejectIntent.setAction("REJECT_TEAM_MEMBER");
		rejectIntent.putExtra("notification_id", APPOINTMENT_ID);

        //Added by dilip, check notification receiver CLASS
        rejectIntent.putExtra("dependent_id", groupsUserInfo.dependent_userId);
        rejectIntent.putExtra("dependent_type", groupsUserInfo.dependent_type);
        if (bundle != null)
			rejectIntent.putExtra("groupsUserInfoBundle", bundle);
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        	PendingIntent rejectpendingIntent = PendingIntent.getBroadcast(this, 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteView = new RemoteViews("smarter.uearn.money", R.layout.notification_accept_reject_team_member);
        remoteView.setOnClickPendingIntent(R.id.acceptAppointment, pendingIntent);
        remoteView.setOnClickPendingIntent(R.id.rejectAppointment, rejectpendingIntent);
		if (groupsUserInfo.name != null && !groupsUserInfo.name.equals(""))
			remoteView.setTextViewText(R.id.email_id, groupsUserInfo.name+"<"+groupsUserInfo.email+">");
		else
			remoteView.setTextViewText(R.id.email_id, groupsUserInfo.email);
		//Log.i("PushNotification","Group user info dependent type is "+groupsUserInfo.dependent_type+","+groupsUserInfo.request);
		if (groupsUserInfo.dependent_type.equals("engineer")) {
			remoteView.setTextViewText(R.id.text, " has requested to be your team member");
		}
		else if (groupsUserInfo.dependent_type.equals("supervisor")) {
			remoteView.setTextViewText(R.id.text, " has added you to his team");
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.small_logo)
		        //.setContentTitle("You have a Reminder!")
		        //.setContentText(subject)
		        //.addExtras(bundle)
                .setContent(remoteView)
		        .setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
        Notification notification = mBuilder.build();
		//CODE CLEANUP :::KSN
        notification.bigContentView = remoteView;
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(AppointmentViewActivity.class);

	    int n = random.nextInt(9999 - 1000) + 1000;
		PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(n, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		NotificationManager mNotificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		//Random random = new Random();
	    int m = random.nextInt(9999 - 1000) + 1000;
        //APPOINTMENT_ID = m;
		mNotificationManager.notify(APPOINTMENT_ID, notification);
		
		//calculate and set next alarm so that, even if user doesn't respond to current alarm popup next alarm must be shown

	}
}
