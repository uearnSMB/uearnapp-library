package smarter.uearn.money.utils.socialmedia;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ServerAPIConnectors.APIAdapter;
import smarter.uearn.money.utils.ServerAPIConnectors.ServerHandlerListener;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.JSONParser;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Srinath on 11/8/2016.
 */
public class ServerAuth {

    private static final String SERVER_CLIENT_ID = "e4acd36b-44b4-48cf-8e1a-1ab9f45b469f";
    private static final String SERVER_RESOURCE = Urls.PERMANENT_SERVER_ADDRESS +"/smarterbizapp";
    private static final String SERVER_SCOPE = "openid";
    private static final String SERVER_GRANT = "password";
    private static final String SERVER_PWD = "Smarterbiz1";
    private static final String SERVER_USERNAME = "app@rbcsmarterbiz.onmicrosoft.com";
    /* private static final String SERVER_URL = "https://login.windows.net/rbcsmarterbiz.onmicrosoft.com/oauth2/token";*/
    /*private static final String SERVER_URL = "https://smarterbiz.co/secured/oauth2/token";*/
    /* private static final String SERVER_URL = "http://35.187.227.191/secured/oauth2/token";*/
    private static String SERVER_URL =  Urls.PERMANENT_SERVER_ADDRESS +"/secured/oauth2/token";
    private String accessTokenUrl;
    private long request_code;
    private ServerHandlerListener listener;

    public ServerAuth() {
    }

    public ServerAuth( ServerHandlerListener listener) {
        this.listener = listener;
    }

    public void call(int request_code) {
        this.request_code = request_code;
        if(request_code == 0) {
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("grant_type", SERVER_GRANT);
            params.put("resource", SERVER_RESOURCE);
            params.put("client_id", SERVER_CLIENT_ID);
            params.put("username", SERVER_USERNAME);
            params.put("password", SERVER_PWD);
            params.put("scope", SERVER_SCOPE);
            if(ApplicationSettings.containsPref(AppConstants.USERINFO_ID)) {
                String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID,"");
                if(user_id != null &&  !(user_id.isEmpty())) {
                    params.put("user_id",user_id);
                } else {
                    params.put("user_id", "0");
                }
            }  else {
                params.put("user_id", "0");
            }

            if(ApplicationSettings.containsPref(AppConstants.USERINFO_EMAIL)) {
                if( ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL,"") != null &&  !(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL,"").isEmpty())) {
                    params.put("useremail", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""));
                } else if(AppConstants.USER_EMAIL_ADDRESS != null && !(AppConstants.USER_EMAIL_ADDRESS.isEmpty())) {
                    params.put("useremail", AppConstants.USER_EMAIL_ADDRESS);
                }
            } else if(AppConstants.USER_EMAIL_ADDRESS != null && !(AppConstants.USER_EMAIL_ADDRESS.isEmpty())) {
                params.put("useremail", AppConstants.USER_EMAIL_ADDRESS);
            }

            ServerAuthTokenCall serverAuthTokenCall = new ServerAuthTokenCall();
            serverAuthTokenCall.execute(params);

        } if(request_code == 1) {
            String refreshToken = ApplicationSettings.getPref("oAuthRefreshToken", null);
            Map<String,Object> params1 = new LinkedHashMap<>();
            params1.put("grant_type", "refresh_token");
            params1.put("resource", SERVER_RESOURCE);
            params1.put("client_id", SERVER_CLIENT_ID);
            params1.put("refresh_token", refreshToken);

            if(ApplicationSettings.containsPref(AppConstants.USERINFO_ID)) {
                String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID,"");
                if( user_id != null &&  !(user_id.isEmpty())) {
                    params1.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, ""));
                } else {
                    params1.put("user_id", "0");
                }
            } else {
                params1.put("user_id", "0");
            }

            if(ApplicationSettings.containsPref(AppConstants.USERINFO_EMAIL)) {
                if( ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL,"") != null &&  !(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL,"").isEmpty())) {
                    params1.put("useremail", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""));
                } else if(AppConstants.USER_EMAIL_ADDRESS != null && !(AppConstants.USER_EMAIL_ADDRESS.isEmpty())) {
                    params1.put("useremail", AppConstants.USER_EMAIL_ADDRESS);
                }
            } else if(AppConstants.USER_EMAIL_ADDRESS != null && !(AppConstants.USER_EMAIL_ADDRESS.isEmpty())) {
                params1.put("useremail", AppConstants.USER_EMAIL_ADDRESS);
            }

            ServerAuthTokenCall serverAuthTokenCall = new ServerAuthTokenCall();
            serverAuthTokenCall.execute(params1);
        }
    }

    private class ServerAuthTokenCall extends AsyncTask<Map<String, Object>, Void, Bundle> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bundle doInBackground(Map<String, Object>... maps) {
            try {
                URLConnection conn = new URL(SERVER_URL).openConnection();
                if (conn instanceof HttpsURLConnection) {
                    HttpsURLConnection connection = (HttpsURLConnection) conn;
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");

                    Map<String, Object> params = new LinkedHashMap<>();
                    params = maps[0];
                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String,Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty( "charset", "utf-8");

                    OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(postDataBytes);
                    os.flush();
                    os.close();

                    InputStream inputStream = null;
                    int statusCode = connection.getResponseCode();
                    Bundle bundle = new Bundle();
                    if (statusCode == 200 || statusCode == 204) {
                        InputStream in = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        String resultString = result.toString();
                        //Log.d("AuthTokenResult", resultString);
                        connection.disconnect();
                        bundle.putInt("responseCode", statusCode);
                        bundle.putString("result", resultString);
                        return bundle;
                    }
                } else {
                    HttpURLConnection connection = (HttpURLConnection) conn;
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");

                    Map<String, Object> params = new LinkedHashMap<>();
                    params = maps[0];
                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String,Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty( "charset", "utf-8");

                    OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(postDataBytes);
                    os.flush();
                    os.close();

                    InputStream inputStream = null;
                    int statusCode = connection.getResponseCode();
                    Bundle bundle = new Bundle();
                    if (statusCode == 200 || statusCode == 204) {
                        InputStream in = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        String resultString = result.toString();
                        //Log.d("AuthTokenResult", resultString);
                        connection.disconnect();
                        bundle.putInt("responseCode", statusCode);
                        bundle.putString("result", resultString);
                        return bundle;
                    }
                }
            } catch (MalformedURLException e) {
                //Log.d("ExceptionInServerAuth:", e.toString());
            } catch(IOException e) {
                //Log.d("ExceptionInServerAuth:", e.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);
            if (bundle != null) {
                if (bundle.containsKey("result")) {
                    String response = bundle.getString("result");
                    String error = JSONParser.getErrorString(response);
                    if (error == null) {
                        ServerAuth.this.listener.onSuccess(request_code, response);
                    } else {
                        ServerAuth.this.listener.onError(request_code, error);
                    }
                } else if (bundle.containsKey("responseCode")) {
                    int errorcode = bundle.getInt("responseCode");
                    ServerAuth.this.listener.onFailure(request_code, errorcode);
                }
            } else {
                ServerAuth.this.listener.onFailure(request_code, APIAdapter.CONNECTION_FAILED);
            }
        }
    }

}
