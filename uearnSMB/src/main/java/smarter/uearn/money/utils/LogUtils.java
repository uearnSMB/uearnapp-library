package smarter.uearn.money.utils;

import android.util.Log;

/**
 */
public class LogUtils {

    //make this boolean false when u r uploading to playStore
    private static boolean LOG_ENABLE = true;

    private static String TAG = "Uearn";

    public static void e(String message) {
        if (message != null) {
            if (LOG_ENABLE)
                Log.e(TAG, message);
        }

    }

    public static void d(String message) {
        if (message != null) {
            if (LOG_ENABLE)
                Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        if (message != null) {
            if (LOG_ENABLE)
                Log.i(TAG, message);
        }
    }

    public static void v(String message) {
        if (message != null) {
            if (LOG_ENABLE)
                Log.v(TAG, message);
        }
    }
}
