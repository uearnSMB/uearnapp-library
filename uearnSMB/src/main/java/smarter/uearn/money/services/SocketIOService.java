package smarter.uearn.money.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class SocketIOService extends Service {

    private Timer timer;
    private TimerTask task;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        //Log.d("SocketIOTest", "Socket IO Service Created");
        startService();
    }

    private void startService() {
        int delay = 5000; // delay for 5 sec.
        int period = 0; // repeat every sec.

        if (ApplicationSettings.containsPref(AppConstants.SIO_REFRESH_TIME)) {
            String sioRefreshTime = ApplicationSettings.getPref(AppConstants.SIO_REFRESH_TIME, "");
            if (sioRefreshTime != null && !sioRefreshTime.isEmpty()) {
                //Log.d("SocketIOTest", "sioRefreshTime"+sioRefreshTime);
                period = Integer.parseInt(sioRefreshTime);
                if(period > 0){
                    period = period * 60 * 1000;
                    //Log.d("SocketIOTest", "period"+period);
                }

                timer = new Timer();

                timer.scheduleAtFixedRate(task = new TimerTask() {
                    public void run() {
                        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                            //Log.d("SocketIOTest", "SmarterSMBApplication.registerSocket()");
                            SmarterSMBApplication.registerSocket();
                        }
                    }
                }, delay, period);
            }
        }
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        task.cancel();
        //Log.d("SocketIOTest", "Service Stopped ...");
        super.onDestroy();
    }
}


