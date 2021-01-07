package smarter.uearn.money.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UrlContent {

	public static boolean isNetworkAvailable( )
	{
		ConnectivityManager cm = (ConnectivityManager) SmarterSMBApplication.getInstance().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }
}
