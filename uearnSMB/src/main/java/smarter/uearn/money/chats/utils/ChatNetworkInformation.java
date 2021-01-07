package smarter.uearn.money.chats.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static smarter.uearn.money.activities.UearnHome.pingInProgress;
import static smarter.uearn.money.activities.UearnHome.pingResponselist;
import static smarter.uearn.money.activities.UearnHome.pingTimer;
import static smarter.uearn.money.activities.UearnHome.timerStopped;
import static smarter.uearn.money.activities.UearnHome.pingResponse;
import static smarter.uearn.money.activities.UearnHome.pingResponselist;

public class ChatNetworkInformation {
    boolean isMobiledata = false;
    public String ConnectionQuality(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return "NO INTERNET";
        }

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return "WIFI";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return "MOBILEDATA";
            } else {
                return "OTHER";
            }
    }

    public NetworkInfo getInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

}
