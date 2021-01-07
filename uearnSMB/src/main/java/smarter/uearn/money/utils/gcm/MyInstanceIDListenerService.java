package smarter.uearn.money.utils.gcm;

import android.content.Intent;
import android.os.Build;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by kavya on 12/9/2015.
 * If the token is refreshed we should send refresh token to server, for that we are using this class.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        //Log.i("PushNotification","********Token Refreshed*********");
        Intent intent = new Intent(this, RegistrationIntentService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
}
