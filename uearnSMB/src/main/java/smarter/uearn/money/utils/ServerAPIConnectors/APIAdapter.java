package smarter.uearn.money.utils.ServerAPIConnectors;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.utils.JSONParser;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.socialmedia.ServerAuthHelper;
import smarter.uearn.money.utils.ApplicationSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ACTIVITY_SERVICE;

public abstract class APIAdapter<T, L> {

    public static int CHILD_MAIL = 10;
    public static int PARENT_MAIL = 11;

    public static int CONNECTION_FAILED = 1000;
    public static int INVALID_RESPONSE = 1001;
    public static int NO_FAILURE = -1;

    public static int NO_FILE_TO_LOAD = 1002;

    private T input_data;
    private long requestCode;

    private String filepath;
    private String fileKey;
    private API_Response_Listener<L> listener;
    private boolean knolarity = false;

    //Added bookean check to tokens call
    private boolean authenticationCheck = true;

    APIAdapter(T input_data, long requestCode, final API_Response_Listener<L> listener) {
        this.input_data = input_data;
        this.requestCode = requestCode;
        this.listener = listener;
    }

    APIAdapter(T input_data, long requestCode,boolean knolarity, final API_Response_Listener<L> listener) {
        this.input_data = input_data;
        this.requestCode = requestCode;
        this.listener = listener;
        this.knolarity = knolarity;
    }

    APIAdapter(T input_data, long requestCode, String filename, String fileKey, final API_Response_Listener<L> listener) {
        this.input_data = input_data;
        this.filepath = filename;
        this.requestCode = requestCode;
        this.listener = listener;
        this.fileKey = fileKey;
    }

    public void call() {

        String url = getURL();
        String http_method = getHttpMethod();

        JSONObject params = convertDataToJSON(input_data);
        ServerHandlerListener serverListener = new ServerHandlerListener() {
            @Override
            public void onSuccess(long requestCode, String result) {
                postProcess();
                L result_data = convertJSONToData(result);
                listener.onComplete(result_data, requestCode, NO_FAILURE);
            }

            @Override
            public void onError(long requestCode, String error) {
                postProcess();
                int failure_code = intrepret_error(error);
                // Log.d("results", "error : " + error);
                showFailureMessage(error);
                listener.onComplete(null, requestCode, failure_code);
            }

            @Override
            public void onFailure(long requestCode, int errorCode) {
                postProcess();
                //String token = ApplicationSettings.getPref(AppConstants.ACCESS__TOKEN, "empty");
                String refreshToken = ApplicationSettings.getPref("oAuthRefreshToken", "emtpy");
                //Log.d("results", "error " + errorCode + " Token : " + token + " refresh " + refreshToken);
                listener.onComplete(null, requestCode, CONNECTION_FAILED);
                //if(!SmarterSMBApplication.showConnectionTimeOutStatusCalled) {
                    showConnectionTimeOutStatus();
                //}
            }
        };

        ServerHandler server;

        if (filepath != null) {
            server = new ServerHandler(url, params, filepath, fileKey, serverListener);
        } else if(knolarity) {
            server = new ServerHandler(url, http_method, params, serverListener, true);
        } else {
            server = new ServerHandler(url, http_method, params, serverListener);
        }

        if(!url.contains("check_login.json")) {
            Long current_time = System.currentTimeMillis();
            Long seconds = TimeUnit.MILLISECONDS
                    .toSeconds(current_time);
            Log.d("TokenExpiry","Calling access token");


            if (!(ApplicationSettings.containsPref("accessToken"))) {
                ServerAuthHelper.accessTokenCall(0);
            }
            Long tokenExpiry = ApplicationSettings.getPref("TokenValidity", 0L);

            Log.d("TokenExpiry","tokenExpiry" + tokenExpiry);
            Log.d("TokenExpiry","seconds" + seconds);

            if ((tokenExpiry - seconds) > 480) {
                Log.d("TokenExpiry","difference" + (tokenExpiry-seconds));
                preProcess();
                server.call(requestCode);
            } else {
                preProcess();
                if (authenticationCheck) {
                    ServerAuthHelper.accessTokenCall(1);
                }
                Long refreshTokenvalidity = ApplicationSettings.getPref("refreshTokenValidity", 0L);
                Log.d("TokenExpiry","refreshTokenvalidity" +refreshTokenvalidity);
                Log.d("TokenExpiry","refreshTokenvalidity" +seconds);
                Log.d("TokenExpiry","refreshTokenvalidity difference" +(refreshTokenvalidity - seconds));
                if ((refreshTokenvalidity - seconds) > 480) {
                    authenticationCheck = false;
                    server.call(requestCode);
                } else {
                    authenticationCheck = true;
                    ServerAuthHelper.accessTokenCall(0);
                    server.call(requestCode);
                }
            }
        } else {
            preProcess();
            server.call(requestCode);
        }
    }

    public void reClickToCall(KnowlarityModel knowlarityModel) {
        Log.d("Knowlarity", "Make call request time " +  new Date().toString());
        String url = getURL();
        String http_method = getHttpMethod();

        //JSONObject params = convertDataToJSON(input_data);
        //Log.d("reClickToCall", input_data.toString());
        ServerHandlerListener serverListener = new ServerHandlerListener() {
            @Override
            public void onSuccess(long requestCode, String result) {
                postProcess();
                L result_data = convertJSONToData(result);
                //Log.d("results", result);
                listener.onComplete(result_data, requestCode, NO_FAILURE);
            }

            @Override
            public void onError(long requestCode, String error) {
                postProcess();
                int failure_code = intrepret_error(error);
                // Log.d("results", "error : " + error);
                showFailureMessage(error);
                listener.onComplete(null, requestCode, failure_code);
            }

            @Override
            public void onFailure(long requestCode, int errorCode) {
                postProcess();
                //String token = ApplicationSettings.getPref(AppConstants.ACCESS__TOKEN, "empty");
                //String refreshToken = ApplicationSettings.getPref("oAuthRefreshToken", "emtpy");
                //Log.d("results", "error " + errorCode + " Token : " + token + " refresh " + refreshToken);
                listener.onComplete(null, requestCode, CONNECTION_FAILED);
                //if(!SmarterSMBApplication.showConnectionTimeOutStatusCalled) {
                    showConnectionTimeOutStatus();
                //}
            }
        };
        HashMap<String , Object> map = JSONParser.getReFormdata(knowlarityModel);
        //Log.d("PingDelay", "APIAdapter - getReFormdata(): " + map.toString());
        ServerHandler server = new ServerHandler(url, http_method,null, serverListener, true);
        server.reCall(requestCode, map);
    }

    public void requestForDataCall() {

        String url = getURL();
        String http_method = getHttpMethod();

        JSONObject params = null;
        if(input_data != null && !input_data.toString().isEmpty()) {
            try {
                params = new JSONObject(input_data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Log.d("APIAdapter_input", input_data.toString());
        ServerHandlerListener serverListener = new ServerHandlerListener() {
            @Override
            public void onSuccess(long requestCode, String result) {
                postProcess();
                L result_data = convertJSONToData(result);
                //Log.d("results", result);
                listener.onComplete(result_data, requestCode, NO_FAILURE);
            }

            @Override
            public void onError(long requestCode, String error) {
                postProcess();
                int failure_code = intrepret_error(error);
                // Log.d("results", "error : " + error);
                showFailureMessage(error);
                listener.onComplete(null, requestCode, failure_code);
            }

            @Override
            public void onFailure(long requestCode, int errorCode) {
                postProcess();
                //String token = ApplicationSettings.getPref(AppConstants.ACCESS__TOKEN, "empty");
                //String refreshToken = ApplicationSettings.getPref("oAuthRefreshToken", "emtpy");
                //Log.d("results", "error " + errorCode + " Token : " + token + " refresh " + refreshToken);
                listener.onComplete(null, requestCode, CONNECTION_FAILED);
                //if(!SmarterSMBApplication.showConnectionTimeOutStatusCalled) {
                    showConnectionTimeOutStatus();
                //}
            }
        };

        ServerHandler server = new ServerHandler(url, http_method, params, serverListener);
        preProcess();
        server.call(requestCode);
    }

    public abstract String getURL();

    public String getHttpMethod() {
        return "GET";
    }

    public JSONObject convertDataToJSON(T data) {
        return null;
    }

    public L convertJSONToData(String response) {
        return null;
    }

    public int intrepret_error(String error) {
        return NO_FAILURE;
    }

    public void preProcess() {
        /* DO nothing */
    }

    public void postProcess() {
        /* DO Nothing */
    }

    private void showConnectionTimeOutStatus() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(SmarterSMBApplication.currentActivity != null) {
                    //Toast.makeText(SmarterSMBApplication.currentActivity, "Connection timeout", Toast.LENGTH_SHORT).show();
                } else if(SmarterSMBApplication.applicationContext != null) {
                    //Toast.makeText(SmarterSMBApplication.applicationContext, "Connection timeout", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showFailureMessage(String message) {
        //Toast.makeText(SmarterSMBApplication.applicationContext, message, Toast.LENGTH_SHORT).show();
    }

//    public static void showConnectionTimeOutDialog(final String message) {
//        CustomTwoButtonDialog.showConnectionTimeOutDialog(message, SmarterSMBApplication.currentActivity).show();
//    }
}
