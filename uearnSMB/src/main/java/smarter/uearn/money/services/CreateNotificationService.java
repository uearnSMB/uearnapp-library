package smarter.uearn.money.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;

import smarter.uearn.money.activities.appointment.AppointmentViewActivity;
import smarter.uearn.money.broadcastReceiver.CallNotificationsReceiver;
import smarter.uearn.money.R;
import smarter.uearn.money.activities.SmarterAuthActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.MySql;

import java.security.SecureRandom;
import java.util.Random;

public class CreateNotificationService extends IntentService {

    private long id = 0;
    private SQLiteDatabase db;
    private SecureRandom random;
    private String lead_source;
    private String cmail_reference;
    Intent resultIntent;
    private static boolean check = true;
    long date_time = 0;
    String toNumber;
    String toname = "";
    long notificationTime = 0;
    long appointmentid = 0;
    String appointmentId = "", status = "", substatus1 = "", substatus2 = "";
    long current = System.currentTimeMillis();
    long start_time = 0l;


    public CreateNotificationService() {
        super("");
    }

    public CreateNotificationService(String name) {
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

        boolean notificationBody = false;

        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        if (intent.hasExtra("id")) {
            id = intent.getLongExtra("id", 0);
            //Log.i("Reminder", "Has extra of dbid" + id);
        }
        //Log.i("Tag", "App logout is in create notification service " + logout);
        MySql dbHelper = MySql.getInstance(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        String subject = "", description = "", playUrl = null, callerNameOrNumber = "", time = "", date = "";
        //Cursor cursor=db.rawQuery("SELECT * FROM remindertbNew where _id="+"'"+id+"'", null);
        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + id + "'" + " AND " + " COMPLETED " + "!= " + 1 + " AND " + "STATUS != 'deleted' ", null);
        // Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + id + "'" + " AND " + "STATUS != 'deleted' ", null);
        //Log.i("Reminder", "Reminder Count " + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            subject = cursor.getString(cursor.getColumnIndex("SUBJECT"));
            description = cursor.getString(cursor.getColumnIndex("NOTES"));
            playUrl = cursor.getString(cursor.getColumnIndex("CALLREC_URL"));
            toname = cursor.getString(cursor.getColumnIndex("TONAME"));
            lead_source = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
            cmail_reference = cursor.getString(cursor.getColumnIndex("REFERENCE_CMAIL"));
            notificationTime = cursor.getLong(cursor.getColumnIndex("START_TIME"));
            appointmentId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
            status = cursor.getString(cursor.getColumnIndex("STATUS"));
            substatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
            substatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
            start_time = cursor.getLong(cursor.getColumnIndex("START_TIME"));


            date_time = cursor.getLong(cursor.getColumnIndex("START_TIME"));
            String start1 = DateFormat.format("dd-MMM-yyyy hh:mm a", date_time).toString();
            //Log.i("CreateNotification", "Start time " + start1);
            String[] split = start1.split(" ");
            time = split[0];
            date = split[1] + " " + split[2];
            if (toname != null && !toname.equals("")) {
                callerNameOrNumber = toname;
            } else {
                toNumber = cursor.getString(cursor.getColumnIndex("TO1"));
                String nameFromLog = getContactName(toNumber);
                if (nameFromLog != null && !(nameFromLog.isEmpty())) {
                    callerNameOrNumber = nameFromLog;
                } else {
                    callerNameOrNumber = toNumber;
                }
            }
        }

        //Log.i("Reminder", "Subject" + subject + " Description is " + description + " Play url is " + playUrl);
        //Bundle bundle=new Bundle();
        //bundle.putLong("id", id);

        // RemoteViews remoteView = new RemoteViews("smarter.uearn.money", R.layout.reminder_notification);
        // remoteView.setTextViewText(R.id.appointment_text, date);
        // remoteView.setTextViewText(R.id.nameOrNumber, "With\t" + callerNameOrNumber);
        // remoteView.setTextViewText(R.id.reminderStatus, subject);
        // remoteView.setTextViewText(R.id.description, description);
        // remoteView.setTextViewText(R.id.dateText, time);
        // remoteView.setTextViewText(R.id.timeText, date);

        //Log.i("Reminder", "play url is :" + playUrl + ":");
        if (playUrl != null) {
            if (!playUrl.equals("")) {
                //remoteView.setImageViewResource(R.id.call_rec_on_or_off, R.drawable.red);
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.small_logo)
                //.setContent(remoteView)
                .setAutoCancel(true);

        mBuilder.setContentTitle("Let's follow-up ");

        // Check and add data to the body of the notification
        String notificationContent = "";
        if (!callerNameOrNumber.isEmpty()) {
            notificationBody = true;
            notificationContent += " With " + callerNameOrNumber;
        }

        if (!date.isEmpty() || !time.isEmpty()) {
            notificationBody = true;
            notificationContent += " at " + date;
        }

        if (!subject.isEmpty()) {
            notificationBody = true;
            notificationContent += "\n Subject: " + subject;
        }

        if (!notificationContent.isEmpty()) {
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationContent));
        } else {
            notificationBody = false;
        }

        // Creates an explicit intent for an Activity in your app
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        if (!logout) {
            stackBuilder.addParentStack(AppointmentViewActivity.class);

        } else {
            stackBuilder.addParentStack(SmarterAuthActivity.class);
            resultIntent = new Intent(this, SmarterAuthActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            resultIntent.putExtra("screenType", 0);
        }

        //Log.i("Tag", "id= " + id);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.

        // Adds the back stack for the Intent (but not the Intent itself)

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        int n = random.nextInt(9999 - 1000) + 1000;
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(n, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setAutoCancel(true);


        int notifcationId = 0;
        // Notification with call and view action
        if (toNumber != null) {
            if (!logout) {

                SecureRandom rand = new SecureRandom();
                Bundle extras = new Bundle();

                notifcationId = rand.nextInt();

                extras.putInt(CallNotificationsReceiver.NOTIFICATION_ID, notifcationId);
                extras.putString(CallNotificationsReceiver.CALL_NUMBER, toNumber);
                extras.putLong(CallNotificationsReceiver.APPOINTMENT_ID, appointmentid);

                Intent callIntent = new Intent(this, CallNotificationsReceiver.class);
                callIntent.setAction(CallNotificationsReceiver.CALL_ACTION);
                callIntent.putExtras(extras);
                PendingIntent pendingCallIntent =
                        PendingIntent.getBroadcast(this, notifcationId, callIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent viewIntent = new Intent(this, CallNotificationsReceiver.class);
                viewIntent.setAction(CallNotificationsReceiver.VIEW_ACTION);
                viewIntent.putExtras(extras);
                PendingIntent pendingViewIntent =
                        PendingIntent.getBroadcast(this, notifcationId, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                    //Log.d("AftercallScreen", ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, ""));
                    String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                    if (screen != null && (!(screen.equalsIgnoreCase("Auto1AfterCallActivity")) && (screen.equalsIgnoreCase("Auto2AfterCallActivity")))) {
                        mBuilder.addAction(R.drawable.ic_notification_calendar, "View", pendingViewIntent); // Dilip changed this
                        mBuilder.setPriority(Notification.PRIORITY_MAX)
                                .setWhen(0);
                    } else if(screen != null && !(screen.equalsIgnoreCase("Auto1AfterCallActivity"))) {
                        mBuilder.addAction(R.drawable.ic_notification_call, "Call", pendingCallIntent);
                        mBuilder.addAction(R.drawable.ic_notification_calendar, "View", pendingViewIntent); // Dilip changed this
                        mBuilder.setPriority(Notification.PRIORITY_MAX)
                                .setWhen(0);
                    } else {
                        mBuilder.setPriority(Notification.PRIORITY_MAX)
                                .setWhen(0);
                    }
                } else {
                    mBuilder.addAction(R.drawable.ic_notification_call, "Call", pendingCallIntent);
                    mBuilder.addAction(R.drawable.ic_notification_calendar, "View", pendingViewIntent); // Dilip changed this
                    mBuilder.setPriority(Notification.PRIORITY_MAX)
                            .setWhen(0);
                }
            }
        }

         NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Check if data for the notification is available
        if (!notificationContent.isEmpty()) {
            if (notificationBody) {
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(100, mBuilder.build());
            }
        }
        db.close();
    }

    public String getContactName(String phoneNumber) {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            return contactName;
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

}
