package smarter.uearn.money.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.activities.appointment.AppointmentViewActivity;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.MySql;

import java.security.SecureRandom;
import java.util.Random;

public class CreateOptionsNotificationService extends IntentService {

	private long id=0;
	private SQLiteDatabase db;
    private SecureRandom random;
    String appointment_id;
    String caller_number;
    String start_time;
    int APPOINTMENT_ID = 35;

	public CreateOptionsNotificationService(){
		super("");
	}

	public CreateOptionsNotificationService(String name) {
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
		
		if(intent.hasExtra("id")){
            id=intent.getLongExtra("id", 0);
            //Log.i("Reminder", "Has extra of dbid"+id);
		}
        MySql dbHelper = MySql.getInstance(getApplicationContext());
		db=dbHelper.getWritableDatabase();
		String subject="Reminder", description = "", playUrl = null, callerNameOrNumber = "", toname = "", toNumber = "",
                date = "", timeDuration = "", time = "";
        String location = "", companyName = "", designation = "";
        long startMls, endMls;
		Cursor cursor=db.rawQuery("SELECT * FROM remindertbNew where _id="+"'"+id+"'", null);
        //Log.i("Reminder","Reminder Count "+cursor.getCount());
        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();

		if(cursor.getCount() > 0){
			cursor.moveToFirst();
            appointment_id = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
            getCalendarEntryInfo.setAppointment_id(appointment_id);
            caller_number = cursor.getString(cursor.getColumnIndex("TO1"));
            getCalendarEntryInfo.setCaller_number(caller_number);
            //Log.i("Tag","Appointment Id is "+appointment_id);
			subject=cursor.getString(cursor.getColumnIndex("SUBJECT"));
            getCalendarEntryInfo.setSubject(subject);
            description = cursor.getString(cursor.getColumnIndex("NOTES"));
            getCalendarEntryInfo.setNotes(description);
            toname = cursor.getString(cursor.getColumnIndex("TONAME"));
            getCalendarEntryInfo.setCaller_name(toname);
            toNumber = cursor.getString(cursor.getColumnIndex("TO1"));
            startMls = cursor.getLong(cursor.getColumnIndex("START_TIME"));
            start_time = DateFormat.format("dd-MMM-yyyy hh:mm a", startMls).toString();
            endMls = cursor.getLong(cursor.getColumnIndex("END_TIME"));
            location = cursor.getString(cursor.getColumnIndex("LOCATION"));
            getCalendarEntryInfo.setLocation(location);
            companyName = cursor.getString(cursor.getColumnIndex("COMPANY_NAME"));
            getCalendarEntryInfo.setCompanyName(companyName);
            designation = cursor.getString(cursor.getColumnIndex("DESIGNATION"));
            getCalendarEntryInfo.setDesignation(designation);
            String websiteString = cursor.getString(cursor.getColumnIndex("WEBSITE"));
            getCalendarEntryInfo.setWebsite(websiteString);
            String emailIdString = cursor.getString(cursor.getColumnIndex("EMAILID"));
            getCalendarEntryInfo.setEmail_id(emailIdString);

            String start1 = DateFormat.format("dd-MMM-yyyy hh:mm a", startMls).toString();
            String end1 = DateFormat.format("dd-MMM-yyyy hh:mm a", endMls).toString();
            getCalendarEntryInfo.setEvent_start_date(start1);
            getCalendarEntryInfo.setEvent_end_date(end1);
            String[] split = start1.split(" ");
            //Log.i("Tag","Split values"+split[0]+" time "+split[1]);
            date = split[0];
            time = split[1]+" "+split[2];
            long durationMills = endMls-startMls;
            long min = durationMills/(1000*60);
            timeDuration = CommonOperations.getTimeduration(min);
		}

        //Log.i("Reminder","Subject"+subject+" Description is "+description+" Play url is "+playUrl);
		//Bundle bundle=new Bundle();
		//bundle.putLong("id", id);

        Intent acceptIntent = new Intent();
        acceptIntent.setAction("ACCEPT_APPOINTMENT");
        acceptIntent.putExtra("caller_number", caller_number);
        acceptIntent.putExtra("dbId", id);
        acceptIntent.putExtra("start_time", start_time);
        //Log.i("Tag","Caller number is "+caller_number);
        acceptIntent.putExtra("appointment_id", appointment_id);
        acceptIntent.putExtra("notification_id", APPOINTMENT_ID);

        Intent rejectIntent = new Intent();
        rejectIntent.setAction("REJECT_APPOINTMENT");
        rejectIntent.putExtra("caller_number", caller_number);
        //Log.i("Tag", "Caller number is " + caller_number);
        rejectIntent.putExtra("appointment_id", appointment_id);
        rejectIntent.putExtra("notification_id", APPOINTMENT_ID);
        rejectIntent.putExtra("start_time", start_time);
        rejectIntent.putExtra("dbId", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent rejectpendingIntent = PendingIntent.getBroadcast(this, 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteView = new RemoteViews("smarter.uearn.money", R.layout.notification_accept_reject_appointment);
        remoteView.setOnClickPendingIntent(R.id.acceptAppointment, pendingIntent);
        remoteView.setOnClickPendingIntent(R.id.rejectAppointment, rejectpendingIntent);
        remoteView.setTextViewText(R.id.caller_name, toname);
        remoteView.setTextViewText(R.id.subject_text, subject);
//        remoteView.setTextViewText(R.id.description, description);
        remoteView.setTextViewText(R.id.number_or_email, toNumber);
        remoteView.setTextViewText(R.id.date_text, date);
        remoteView.setTextViewText(R.id.duration_text, timeDuration);
        remoteView.setTextViewText(R.id.time_text, time);
        //Log.i("Reminder", "play url is :"+playUrl+":");
/*
        if(playUrl != null) {
            if(!playUrl.equals("")) {
                remoteView.setImageViewResource(R.id.call_rec_on_or_off, R.drawable.red);
            }
        }
*/

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

		Intent resultIntent = new Intent(this, AppointmentViewActivity.class);
			resultIntent.putExtra("id", id);
			//Log.i("Tag","id= "+id);
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(AppointmentViewActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);

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
		db.close();
	}
}
