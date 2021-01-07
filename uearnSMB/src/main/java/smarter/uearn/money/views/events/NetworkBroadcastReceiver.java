package smarter.uearn.money.views.events;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import smarter.uearn.money.activities.SmarterAuthActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.ReuploadService;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static smarter.uearn.money.utils.SmarterSMBApplication.applicationContext;
import static smarter.uearn.money.utils.SmarterSMBApplication.startSmbSio;


public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private Context context = null;
    public boolean isNetworkAvailable = false;
    public static boolean isNetworkAvailableCalled = false;


    @Override
    public void onReceive(final Context context, final Intent intent) {
        this.context = context;
        if (isNetworkAvailable(context)) {
            startSmbSio();
            boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
            if (!isLogout) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;


                if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.DeviceCheckActivity")) {
                    if (isWifiConnected()) {
                        isNetworkAvailableCalled = false;
                        sendMessageToSmbHome("internet_on");
                    } else {
                        sendMessageToSmbHome("internet_off");
                    }
                } else if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.DeviceCheckCompleteActivity")) {
                    if (isWifiConnected()) {
                        isNetworkAvailableCalled = false;
                        sendMessageToSmbHome("internet_on");
                    } else {
                        sendMessageToSmbHome("internet_off");
                    }
                } else if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.WifiCheckActivity")) {
                    if (isWifiConnected()) {
                        isNetworkAvailableCalled = false;
                        sendMessageToSmbHome("internet_on");
                    } else {
                        sendMessageToSmbHome("internet_off");
                    }
                } else if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.JoinUsActivity")) {
                    if (isWifiConnected()) {
                        isNetworkAvailableCalled = false;
                        sendMessageToSmbHome("internet_on");
                    } else {
                        sendMessageToSmbHome("internet_off");
                    }
                } else if (isNetworkAvailableCalled) {
                    isNetworkAvailableCalled = false;
                    SmarterSMBApplication.backToNetwork = true;
                    SmarterSMBApplication.getCurrentActivity();
                    navigateToUearnHome();
                }
            } else if (ApplicationSettings.containsPref(AppConstants.IS_REUPLOAD_ENABLED)) {
                String isReuploadEnabled = ApplicationSettings.getPref(AppConstants.IS_REUPLOAD_ENABLED, "");
                if (isReuploadEnabled != null && !isReuploadEnabled.isEmpty()) {
                    if (isReuploadEnabled.equalsIgnoreCase("on")) {
                        if (ApplicationSettings.containsPref(AppConstants.BACK_TO_NETWORK)) {
                            boolean backToNetwork = ApplicationSettings.getPref(AppConstants.BACK_TO_NETWORK, false);
                            if (backToNetwork) {
                                sendBackToNetworkToServer();
                            }
                        }
                        callingReuploadService();
                    }
                } else {
                    if (ApplicationSettings.containsPref(AppConstants.BACK_TO_NETWORK)) {
                        boolean backToNetwork = ApplicationSettings.getPref(AppConstants.BACK_TO_NETWORK, false);
                        if (backToNetwork) {
                            sendBackToNetworkToServer();
                        }
                    }
                    callingReuploadService();
                }
            } else {
                if (ApplicationSettings.containsPref(AppConstants.BACK_TO_NETWORK)) {
                    boolean backToNetwork = ApplicationSettings.getPref(AppConstants.BACK_TO_NETWORK, false);
                    if (backToNetwork) {
                        sendBackToNetworkToServer();
                    }
                }
                callingReuploadService();
            }
        } else {
            if (!isNetworkAvailableCalled) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.UearnActivity")) {

                } else {
                    //navigateToUearnHome();
                    if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.DeviceCheckActivity")) {
                        sendMessageToSmbHome("internet_off");
                    } else if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.DeviceCheckCompleteActivity")) {
                        sendMessageToSmbHome("internet_off");
                    } else if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.WifiCheckActivity")) {
                        sendMessageToSmbHome("internet_off");
                    } else if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.JoinUsActivity")) {
                        sendMessageToSmbHome("internet_off");
                    } else {
                        sendMessageToSmbHome("internet_off");
                        SmarterSMBApplication.noNetworkAvailable = true;
                        isNetworkAvailableCalled = true;
                        Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                        if (cn != null && cn.getClassName().equals("smarter.uearn.money.activities.DataLoadingActivity")) {
                            Handler handler = new Handler();
                            handler.post(new Runnable() {
                                public void run() {
                                    backToLogin("Failed to sync, please check your WiFi / internet connection.");
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void backToLogin(String msg) {
        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, true);
        Intent intent = new Intent(context, SmarterAuthActivity.class);
        intent.putExtra("Error_message", msg);
        intent.putExtra("screenType", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    private boolean isWifiConnected() {
        NetworkInfo info = getInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            if (info.isConnected()) {
                return true;
            }
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (info.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public NetworkInfo getInfo() {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    private void callingReuploadService() {
        isNetworkAvailableCalled = false;
        Intent aIntent = new Intent(context, ReuploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(aIntent);
        } else {
            context.startService(aIntent);
        }
    }

    private void sendBackToNetworkToServer() {
        final JSONObject jsonObject = new JSONObject();
        try {
            if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")) {
                jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
            }
            jsonObject.put("number", NotificationData.backToNetworkCustomerNo);
            jsonObject.put("transactionid", NotificationData.backToNetworkTransactionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject backToNetworkResponseJsonObj = DataUploadUtils.postBackToNetwork(Urls.postBackToNetworkUrl(), jsonObject);
                    if (backToNetworkResponseJsonObj != null) {
                        if (backToNetworkResponseJsonObj.has("success")) {
                            String msg = backToNetworkResponseJsonObj.getString("success");
                            if (msg != null && !msg.isEmpty()) {
                                if (UearnHome.remoteAutoDialingStartClicked) {

                                } else {
                                    NotificationData.backToNetworkResponseMsg = msg;
                                    navigateToUearnHome();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo allNetworks = cm.getActiveNetworkInfo();

            boolean response = false;

            if (allNetworks == null) {
                response = false;
            } else {
                if (allNetworks.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (allNetworks.isConnected()) {
                        response = true;
                    }
                } else if (allNetworks.getType() == ConnectivityManager.TYPE_MOBILE) {
                    response = true;
                }
            }

            if (response) {
                if (AppConstants.DATA_STATUS.equalsIgnoreCase("offline")) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return response;
            }
        }
        return false;
    }

    private static void sendMessageToSmbHome(String message) {
        Log.d("PushNotification", "sendMessageToSmbHome");
        Intent intent = new Intent("remote-dialer-request-event");
        intent.putExtra("result", message);
        Log.d("SMBHOMETEST", "sendMessageToSmbHome " + message);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }

    private void navigateToUearnHome() {
        boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        if (!isLogout) {
            String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
            if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
                Intent intent = new Intent(context, UearnHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            }
        }
    }
}
