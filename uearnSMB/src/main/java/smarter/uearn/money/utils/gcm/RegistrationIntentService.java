package smarter.uearn.money.utils.gcm;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import smarter.uearn.money.R;

import java.io.IOException;


/**
 * Created by kavya on 12/9/2015.
 */
public class RegistrationIntentService extends IntentService {

    String token;

    private static final String[] TOPICS = {"global"};// TODO: HAVE TO SEND ONLY CALENDAR

    public RegistrationIntentService() {
        super("RegistrationIntentService");
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

        InstanceID instanceID = InstanceID.getInstance(this);
        String id = instanceID.getId();
        ResultReceiver resultReceiver = intent.getParcelableExtra("receiverTag");
        try {
            token = instanceID.getToken("396887138242",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            //token = FirebaseInstanceId.getInstance().getToken();
            Bundle bundle = new Bundle();
            bundle.putString("GoogleIntsDeviceId",token);
            if (resultReceiver != null) {
                resultReceiver.send(0, bundle);
            }
            subscribeTopics(token);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            //FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + topic);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}
