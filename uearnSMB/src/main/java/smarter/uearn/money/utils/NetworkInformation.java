package smarter.uearn.money.utils;

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

public class NetworkInformation {
    boolean isMobiledata = false;
    public String ConnectionQuality(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return "NO INTERNET";
        }

        if (ApplicationSettings.containsPref(AppConstants.ALLOW_MOBILE_DATA)) {
            isMobiledata = ApplicationSettings.getPref(AppConstants.ALLOW_MOBILE_DATA, false);
        }
        if(isMobiledata) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                SmarterSMBApplication.currentNetworkType = "WIFI";
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                int numberOfLevels = 5;
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                if (level == 2)
                    return "AVERAGE";
                else if (level == 3)
                    return "MODERATE";
                else if (level == 4)
                    return "GOOD";
                else if (level == 5)
                    return "EXCELLENT";
                else
                    return "POOR";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                SmarterSMBApplication.currentNetworkType = "MOBILE";
                int networkClass = getNetworkClass(getNetworkType(context));
                if (networkClass == 1)
                    return "POOR";
                else if (networkClass == 2)
                    return "GOOD";
                else if (networkClass == 3)
                    return "EXCELLENT";
                else
                    return "NO INTERNET";
            } else {
                return "NO INTERNET";
            }
        }else{
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                SmarterSMBApplication.currentNetworkType = "WIFI";
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                int numberOfLevels = 5;
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                if (level == 2)
                    return "AVERAGE";
                else if (level == 3)
                    return "MODERATE";
                else if (level == 4)
                    return "GOOD";
                else if (level == 5)
                    return "EXCELLENT";
                else
                    return "POOR";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                SmarterSMBApplication.currentNetworkType = "MOBILE";
                int networkClass = getNetworkClass(getNetworkType(context));
                if (networkClass == 1)
                    return "Please connect to WIFI";
                else if (networkClass == 2)
                    return "Please connect to WIFI";
                else if (networkClass == 3)
                    return "Please connect to WIFI";
                else
                    return "Please connect to WIFI";
            } else {
                return "Please connect to WIFI";
            }
        }
    }

    public NetworkInfo getInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    public int getNetworkClass(int networkType) {
        try {
            return getNetworkClassReflect(networkType);
        }catch (Exception ignored) {
        }

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case 16: // TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 1;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case 17: // TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return 2;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case 18: // TelephonyManager.NETWORK_TYPE_IWLAN:
                return 3;
            default:
                return 0;
        }
    }

    private int getNetworkClassReflect(int networkType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getNetworkClass = TelephonyManager.class.getDeclaredMethod("getNetworkClass", int.class);
        if (!getNetworkClass.isAccessible()) {
            getNetworkClass.setAccessible(true);
        }
        return (Integer) getNetworkClass.invoke(null, networkType);
    }

    public static int getNetworkType(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();
    }


}
