package smarter.uearn.money.utils.upload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.socialmedia.ServerAuthHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * <p>
 * Title: Data Upload Utility class
 * </p>
 * <p>
 * Description: this class helps to upload data  that are required for SMARTER SMB with various form
 * data, including files.
 * </p>
 *
 * @author Navine
 * @version 2.0
 *          <p/>
 *          Last Modified on 04-Jan-2016 - Not Final Code
 *          <p/>
 *          Code Optimization completed
 */
public class DataUploadUtils {
    private static final String KEY_NOTES_SERVER_FILE_NAME = "recording";
    private static final String KEY_SERVER_FILE_NAME = "audio";
    private static final String KEY_SERVER_PHONE = "phone";
    private static final String KEY_SERVER_CALL_TYPE = "call_type";
    private static final String KEY_SERVER_CONTACT_NAME = "contact_name";
    private static final String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

    private static final String KEY_USER_ID = "user_id";

    private static boolean authenticationCheck = true;

    public static JSONObject postEmailContactInfo(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);

        }
        return null;
    }

    public static JSONObject uploadAudioFileToServer(String filePath, String call_type, String phone, String contact_name, String url) {
        String cloudFileName = filePath;
        try {
            if ((filePath == null) || (!new File(filePath).exists())) {
                return null;
            }
            Pattern pattern = Pattern.compile("\\.smb");
            Matcher matcher = pattern.matcher(filePath);

            if (matcher.find()) {
                cloudFileName = filePath.substring(0, filePath.length() - 4);
            }
            //Added By Srinath.k
            URLConnection conn = new URL(url).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                //Added accessTOken to Upload Api :::KSN
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                }

                ClientHttpsRequest httpRequest = new ClientHttpsRequest(connection);

                httpRequest.setParameter("event_type_str", "AUDIO");
                httpRequest.setParameter("audio_content_type", "audio/mp3");
                httpRequest.setParameter("content_type", "audio");

                httpRequest.setParameter(KEY_SERVER_FILE_NAME, cloudFileName, new File(filePath));
                httpRequest.setParameter(KEY_SERVER_CALL_TYPE, call_type);
                httpRequest.setParameter(KEY_SERVER_PHONE, phone);
                httpRequest.setParameter(KEY_SERVER_CONTACT_NAME, contact_name);

                if (user_id != null) {
                    httpRequest.setParameter(KEY_USER_ID, user_id);
                    //Log.d("AUDIO_USER_ID", user_id);
                } else {
                    //Log.d("AUDIO_USER_ID", " ");
                    httpRequest.setParameter(KEY_USER_ID, " ");
                }

                InputStream repsonseStream = null;
                try {
                    repsonseStream = httpRequest.post();
                    String response = Utils.convertStreamToString(repsonseStream);
                    //Log.d("ReuploadService","Response for Upload Audio File is "+response.toString());
                    //Log.d("Response", response);
                    if (!response.equals("") && response.length() > 0) {
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.isNull("errors"))
                            return responseJSON;
                        else {
                            String error = responseJSON.getString("errors");
                            //Log.e("Reupload-Service", "Error in UploadFileToServer: error type is " + error);
                        }
                    }
                } finally {
                    try {
                        if (repsonseStream != null) {
                            repsonseStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (httpRequest.connection != null) {
                        httpRequest.connection.disconnect();
                    }
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                //Added accessTOken to Upload Api :::KSN
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                }

                ClientHttpRequest httpRequest = new ClientHttpRequest(connection);

                httpRequest.setParameter("event_type_str", "AUDIO");
                httpRequest.setParameter("audio_content_type", "audio/mp3");
                httpRequest.setParameter("content_type", "audio");

                httpRequest.setParameter(KEY_SERVER_FILE_NAME, cloudFileName, new File(filePath));
                httpRequest.setParameter(KEY_SERVER_CALL_TYPE, call_type);
                httpRequest.setParameter(KEY_SERVER_PHONE, phone);
                httpRequest.setParameter(KEY_SERVER_CONTACT_NAME, contact_name);

                if (user_id != null) {
                    httpRequest.setParameter(KEY_USER_ID, user_id);
                    //Log.d("AUDIO_USER_ID", user_id);
                } else {
                    //Log.d("AUDIO_USER_ID", " ");
                    httpRequest.setParameter(KEY_USER_ID, " ");
                }

                InputStream repsonseStream = null;
                try {
                    repsonseStream = httpRequest.post();
                    String response = Utils.convertStreamToString(repsonseStream);
                    //Log.d("ReuploadService","Response for Upload Audio File is "+response.toString());
                    //Log.d("Response", response);
                    if (!response.equals("") && response.length() > 0) {
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.isNull("errors"))
                            return responseJSON;
                        else {
                            String error = responseJSON.getString("errors");
                            //Log.e("Reupload-Service", "Error in UploadFileToServer: error type is " + error);
                        }
                    }
                } finally {
                    try {
                        if (repsonseStream != null) {
                            repsonseStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (httpRequest.connection != null) {
                        httpRequest.connection.disconnect();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static JSONObject uploadNotesAudioFileToServer(String filePath, String call_type, String phone, String contact_name, String url) {
        String cloudFileName = filePath;
        //Log.d("Url", url);
        try {
            if ((filePath == null) || (!new File(filePath).exists())) {
                return null;
            }
            Pattern pattern = Pattern.compile("\\.smb");
            Matcher matcher = pattern.matcher(filePath);

            if (matcher.find()) {
                cloudFileName = filePath.substring(0, filePath.length() - 4);
            }
            //Added By Srinath.k
            URLConnection conn = new URL(url).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                //Added accessTOken to Upload Api :::KSN
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                ClientHttpsRequest httpRequest = new ClientHttpsRequest(connection);

                httpRequest.setParameter("event_type_str", "AUDIO");
                httpRequest.setParameter("audio_content_type", "audio/mp3");
                httpRequest.setParameter("content_type", "audio");

                httpRequest.setParameter(KEY_NOTES_SERVER_FILE_NAME, cloudFileName, new File(filePath));
                httpRequest.setParameter(KEY_SERVER_CALL_TYPE, call_type);
                httpRequest.setParameter(KEY_SERVER_PHONE, phone);
                httpRequest.setParameter(KEY_SERVER_CONTACT_NAME, contact_name);

                if (user_id != null) {
                    httpRequest.setParameter(KEY_USER_ID, user_id);
                    //Log.d("AUDIO_USER_ID", user_id);
                } else {
                    //Log.d("AUDIO_USER_ID", " ");
                    httpRequest.setParameter(KEY_USER_ID, " ");
                }

                InputStream repsonseStream = null;
                try {
                    repsonseStream = httpRequest.post();
                    String response = Utils.convertStreamToString(repsonseStream);
                    //Log.d("Response", response);
                    if (!response.equals("") && response.length() > 0) {
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.isNull("errors"))
                            return responseJSON;
                        else {
                            String error = responseJSON.getString("errors");
                            //Log.e("Reupload-Service", "Error in UploadFileToServer: error type is " + error);
                        }
                    }
                } finally {
                    try {
                        if (repsonseStream != null) {
                            repsonseStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (httpRequest.connection != null) {
                        httpRequest.connection.disconnect();
                    }
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                //Added accessTOken to Upload Api :::KSN
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                ClientHttpRequest httpRequest = new ClientHttpRequest(connection);

                httpRequest.setParameter("event_type_str", "AUDIO");
                httpRequest.setParameter("audio_content_type", "audio/mp3");
                httpRequest.setParameter("content_type", "audio");

                httpRequest.setParameter(KEY_NOTES_SERVER_FILE_NAME, cloudFileName, new File(filePath));
                httpRequest.setParameter(KEY_SERVER_CALL_TYPE, call_type);
                httpRequest.setParameter(KEY_SERVER_PHONE, phone);
                httpRequest.setParameter(KEY_SERVER_CONTACT_NAME, contact_name);

                if (user_id != null) {
                    httpRequest.setParameter(KEY_USER_ID, user_id);
                    //Log.d("AUDIO_USER_ID", user_id);
                } else {
                    //Log.d("AUDIO_USER_ID", " ");
                    httpRequest.setParameter(KEY_USER_ID, " ");
                }

                InputStream repsonseStream = null;
                try {
                    repsonseStream = httpRequest.post();
                    String response = Utils.convertStreamToString(repsonseStream);
                    //Log.d("Response", response);
                    if (!response.equals("") && response.length() > 0) {
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.isNull("errors"))
                            return responseJSON;
                        else {
                            String error = responseJSON.getString("errors");
                            //Log.e("Reupload-Service", "Error in UploadFileToServer: error type is " + error);
                        }
                    }
                } finally {
                    try {
                        if (repsonseStream != null) {
                            repsonseStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (httpRequest.connection != null) {
                        httpRequest.connection.disconnect();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static JSONObject uploadImageFileToServer(String filePath, String url) {
        try {
            checkToken();
            if ((filePath == null) || (!new File(filePath).exists())) {
                return null;
            }

            //Added By Srinath.k
            URLConnection conn = new URL(url).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                //Added accessTOken to Upload Api :::KSN
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                ClientHttpsRequest httpRequest = new ClientHttpsRequest(connection);

                httpRequest.setParameter("event_type_str", "IMAGE");
                httpRequest.setParameter("content_type", "image/jpeg");
                httpRequest.setParameter("image", new File(filePath));

                if (user_id != null) {
                    httpRequest.setParameter("user_id", user_id);
                    //Log.d("IMAGE_USER_ID", user_id);
                } else {
                    //Log.d("IMAGE_USER_ID", " ");
                    httpRequest.setParameter("user_id", " ");
                }

                InputStream inputStream = null;
                try {
                    inputStream = httpRequest.post();
                    String response = Utils.convertStreamToString(inputStream);
                    //Log.d("IMAGE_UPLOAD_RESPONSE", response);

                    if (!response.equals("") && response.length() > 0) {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.isNull("errors")) {
                            return jsonResponse;
                        } else {
                            jsonResponse.getString("errors");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (httpRequest.connection != null) {
                        httpRequest.connection.disconnect();
                    }
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                //Added accessTOken to Upload Api :::KSN
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                ClientHttpRequest httpRequest = new ClientHttpRequest(connection);

                httpRequest.setParameter("event_type_str", "IMAGE");
                httpRequest.setParameter("content_type", "image/jpeg");
                httpRequest.setParameter("image", new File(filePath));

                if (user_id != null) {
                    httpRequest.setParameter("user_id", user_id);
                    //Log.d("IMAGE_USER_ID", user_id);
                } else {
                    //Log.d("IMAGE_USER_ID", " ");
                    httpRequest.setParameter("user_id", " ");
                }

                InputStream inputStream = null;
                try {
                    inputStream = httpRequest.post();
                    String response = Utils.convertStreamToString(inputStream);
                    //Log.d("IMAGE_UPLOAD_RESPONSE", response);

                    if (!response.equals("") && response.length() > 0) {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.isNull("errors")) {
                            return jsonResponse;
                        } else {
                            jsonResponse.getString("errors");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (httpRequest.connection != null) {
                        httpRequest.connection.disconnect();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject postSalesStatusUpdateData(String urlString, JSONObject jsonObject) {
        try {
            URLConnection conn = new URL(urlString).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                /*Added AccessToken to create calendar api  :::KSN*/
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                String jsonParam = jsonObject.toString();
                //Log.d("Json_params", jsonParam);
                //Log.d("TruePredictiveTest", "urlString" + urlString + " jsonParam" + jsonParam);

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(outputInBytes);
                os.flush();
                os.close();

                try {
                    if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                //Get Response
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Response", result);
                    //Log.d("TruePredictiveTest", "Response" + result);
                    return new JSONObject(result);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                /*Added AccessToken to create calendar api  :::KSN*/
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                String jsonParam = jsonObject.toString();
                //Log.d("Json_params", jsonParam);
                //Log.d("TruePredictiveTest", "urlString" + urlString + " jsonParam" + jsonParam);

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(outputInBytes);
                os.flush();
                os.close();

                try {
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                //Get Response
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Response", result);
                    //Log.d("TruePredictiveTest", "Response" + result);
                    return new JSONObject(result);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            }
        } catch (SocketTimeoutException e) {
            //Log.e("Reupload-Service", "PostMetaData JSON Exception thrown : " + e);
            //if(!SmarterSMBApplication.showConnectionTimeOutStatusCalled) {
            showConnectionTimeOutStatus();
            //}
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            //Log.d("Urls", "MalformedURLException thrown : " + e);
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            //Log.d("Urls", "IOException thrown : " + e);
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            //Log.d("Urls", "JSONException thrown : " + e);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            //Log.d("Urls", "Exception thrown : " + e);
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject postJsonData(String urlString, JSONObject jsonObject) {
        try {
            URLConnection conn = new URL(urlString).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                /*Added AccessToken to create calendar api  :::KSN*/
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                String jsonParam = jsonObject.toString();
                //Log.d("Json_params", jsonParam);
                //Log.d("TruePredictiveTest", "urlString" + urlString + " jsonParam" + jsonParam);

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(outputInBytes);
                os.flush();
                os.close();

                try {
                    if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                //Get Response
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Response", result);
                    //Log.d("TruePredictiveTest", "Response" + result);
                    return new JSONObject(result);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                /*Added AccessToken to create calendar api  :::KSN*/
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                String jsonParam = jsonObject.toString();
                //Log.d("Json_params", jsonParam);
                //Log.d("TruePredictiveTest", "urlString" + urlString + " jsonParam" + jsonParam);

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(outputInBytes);
                os.flush();
                os.close();

                try {
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                //Get Response
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Response", result);
                    //Log.d("TruePredictiveTest", "Response" + result);
                    return new JSONObject(result);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            //Log.e("Reupload-Service", "PostMetaData MalURL Exception thrown : " + e);
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            //Log.e("Reupload-Service", "PostMetaData JSON Exception thrown : " + e);
            //if(!SmarterSMBApplication.showConnectionTimeOutStatusCalled) {
            showConnectionTimeOutStatus();
            //}
            e.printStackTrace();
        } catch (IOException e) {
            //Log.e("Reupload-Service", "PostMetaData IO Exception thrown : " + e);
            e.printStackTrace();
        } catch (JSONException e) {
            //Log.e("Reupload-Service", "PostMetaData JSON Exception thrown : " + e);
            e.printStackTrace();
        } catch (Exception e) {
            //Log.e("Reupload-Service", "PostMetaData Exception thrown : " + e);
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject putJsonData(String urlString, JSONObject jsonObject) {
        try {
            URLConnection conn = new URL(urlString).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                //conn.setRequestMethod("POST");
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                /*Added AccessToken to create calendar api  :::KSN*/
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                String jsonParam = jsonObject.toString();
                //Log.d("Json_params", jsonParam);

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(outputInBytes);
                os.flush();
                os.close();

                try {
                    if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                //Get Response
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Response", result);
                    return new JSONObject(result);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                //conn.setRequestMethod("POST");
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                /*Added AccessToken to create calendar api  :::KSN*/
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                String jsonParam = jsonObject.toString();
                //Log.d("Json_params", jsonParam);

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(outputInBytes);
                os.flush();
                os.close();

                try {
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                //Get Response
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Response", result);
                    return new JSONObject(result);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            //Log.e("Reupload-Service", "PostMetaData MalURL Exception thrown : " + e);
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            //Log.e("Reupload-Service", "PostMetaData JSON Exception thrown : " + e);
            //if(!SmarterSMBApplication.showConnectionTimeOutStatusCalled) {
            showConnectionTimeOutStatus();
            //}
            e.printStackTrace();
        } catch (IOException e) {
            //Log.e("Reupload-Service", "PostMetaData IO Exception thrown : " + e);
            e.printStackTrace();
        } catch (JSONException e) {
            //Log.e("Reupload-Service", "PostMetaData JSON Exception thrown : " + e);
            e.printStackTrace();
        } catch (Exception e) {
            //Log.e("Reupload-Service", "PostMetaData Exception thrown : " + e);
            e.printStackTrace();
        }
        return null;
    }

    private static void checkToken() {

        Long current_time = System.currentTimeMillis();
        Long seconds = TimeUnit.MILLISECONDS
                .toSeconds(current_time);
        Long tokenExpiry = ApplicationSettings.getPref("TokenValidity", 0L);
        //Log.d("TokenExpiry","" + (tokenExpiry));

        if ((tokenExpiry - seconds) > 180) {
            //Log.d("TokenExpiry","" + (tokenExpiry-seconds));
        } else {
            if (authenticationCheck) {
                ServerAuthHelper.accessTokenCall(1);
            }
            Long refreshTokenvalidity = ApplicationSettings.getPref("refreshTokenValidity", 0L);
            //Log.d("TokenExpiry","" + (refreshTokenvalidity));
            if ((refreshTokenvalidity - seconds) > 180) {
                authenticationCheck = false;
            } else {
                authenticationCheck = true;
                ServerAuthHelper.accessTokenCall(0);
            }
        }
    }

    public static String uploadTranscriptToServer(String num, String filePath, String url) {
        //Log.d("Url", url);
        String transFile = "";
        try {
            if ((filePath == null) || (!new File(filePath).exists())) {
                return null;
            }
            //Added By Srinath.k
            URLConnection conn = new URL(url).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                //Added accessTOken to Upload Api :::KSN
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                ClientHttpRequest httpRequest = new ClientHttpRequest(connection);

                httpRequest.setParameter("content_type", "text/plain");
                httpRequest.setParameter("transcription", new File(filePath));

                if (user_id != null) {
                    httpRequest.setParameter(KEY_USER_ID, user_id);
                    //Log.d("Trascribe_USER_ID", user_id);
                } else {
                    //Log.d("Transcribe_USER_ID", " ");
                    httpRequest.setParameter(KEY_USER_ID, " ");
                }
                if(num != null) {
                    //Log.d("Transcribe_phone", num);
                    httpRequest.setParameter("phone",num);
                }
                if(ApplicationSettings.containsPref(AppConstants.TRANSCRIPT_LANG)) {
                    String lang = ApplicationSettings.getPref(AppConstants.TRANSCRIPT_LANG, "");
                    if(lang != null) {
                        //Log.d("Transcribe_lang", lang);
                        httpRequest.setParameter("transcripted_lang",lang);
                    }
                }

                InputStream repsonseStream = null;
                try {
                    repsonseStream = httpRequest.post();
                    String response = Utils.convertStreamToString(repsonseStream);
                    //Log.d("TranscriptResponse", response);
                    if (!response.equals("") && response.length() > 0) {
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.isNull("errors"))
                            return response;
                        else {
                            String error = responseJSON.getString("errors");
                            //Log.e("Reupload-Service", "Error in UploadTranscribeFileToServer: error type is " + error);
                        }
                    }
                } finally {
                    try {
                        if (repsonseStream != null) {
                            repsonseStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (httpRequest.connection != null) {
                        httpRequest.connection.disconnect();
                    }
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                //Added accessTOken to Upload Api :::KSN
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                ClientHttpRequest httpRequest = new ClientHttpRequest(connection);

                httpRequest.setParameter("content_type", "text/plain");
                httpRequest.setParameter("transcription", new File(filePath));

                if (user_id != null) {
                    httpRequest.setParameter(KEY_USER_ID, user_id);
                    //Log.d("Trascribe_USER_ID", user_id);
                } else {
                    //Log.d("Transcribe_USER_ID", " ");
                    httpRequest.setParameter(KEY_USER_ID, " ");
                }
                if(num != null) {
                    //Log.d("Transcribe_phone", num);
                    httpRequest.setParameter("phone",num);
                }
                if(ApplicationSettings.containsPref(AppConstants.TRANSCRIPT_LANG)) {
                    String lang = ApplicationSettings.getPref(AppConstants.TRANSCRIPT_LANG, "");
                    if(lang != null) {
                        //Log.d("Transcribe_lang", lang);
                        httpRequest.setParameter("transcripted_lang",lang);
                    }
                }

                InputStream repsonseStream = null;
                try {
                    repsonseStream = httpRequest.post();
                    String response = Utils.convertStreamToString(repsonseStream);
                    //Log.d("TranscriptResponse", response);
                    if (!response.equals("") && response.length() > 0) {
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.isNull("errors"))
                            return response;
                        else {
                            String error = responseJSON.getString("errors");
                            //Log.e("Reupload-Service", "Error in UploadTranscribeFileToServer: error type is " + error);
                        }
                    }
                } finally {
                    try {
                        if (repsonseStream != null) {
                            repsonseStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (httpRequest.connection != null) {
                        httpRequest.connection.disconnect();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return transFile;
    }

    public static JSONObject postIvrCustomerMapping(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);
        }
        return null;
    }

    public static JSONObject postCallDisconnect(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);
        }
        return null;
    }

    public static JSONObject postBackToNetwork(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);
        }
        return null;
    }

    public static JSONObject postSalesEntries(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);
        }
        return null;
    }

    public static JSONObject postDBEntries(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);
        }
        return null;
    }

    public static JSONObject postAppStatus(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);
        }
        return null;
    }

    public static JSONObject postMySettings(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);
        }
        return null;
    }

    public static JSONObject postInCallActivity(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);
        }
        return null;
    }

    public static void showConnectionTimeOutStatus() {
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

    public static JSONObject postTechSupport(String url, JSONObject json) {
        if (url != null && json != null) {
            checkToken();
            return postJsonData(url, json);
        }
        return null;
    }

    public static JSONObject postGrammarTestInfoData(String urlString, JSONObject jsonObject) {
        try {
            URLConnection conn = new URL(urlString).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                /*Added AccessToken to create calendar api  :::KSN*/
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                String jsonParam = jsonObject.toString();
                //Log.d("Json_params", jsonParam);
                //Log.d("TruePredictiveTest", "urlString" + urlString + " jsonParam" + jsonParam);

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(outputInBytes);
                os.flush();
                os.close();

                try {
                    if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                //Get Response
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Response", result);
                    //Log.d("TruePredictiveTest", "Response" + result);
                    return new JSONObject(result);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            } else {
                HttpURLConnection connection = (HttpURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                /*Added AccessToken to create calendar api  :::KSN*/
                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Autherization", token);
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                String jsonParam = jsonObject.toString();
                //Log.d("Json_params", jsonParam);
                //Log.d("TruePredictiveTest", "urlString" + urlString + " jsonParam" + jsonParam);

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                os.write(outputInBytes);
                os.flush();
                os.close();

                try {
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                //Get Response
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Response", result);
                    //Log.d("TruePredictiveTest", "Response" + result);
                    return new JSONObject(result);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    connection.disconnect();
                }
            }
        } catch (SocketTimeoutException e) {
            //Log.e("Reupload-Service", "PostMetaData JSON Exception thrown : " + e);
            //if(!SmarterSMBApplication.showConnectionTimeOutStatusCalled) {
            showConnectionTimeOutStatus();
            //}
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            //Log.d("Urls", "MalformedURLException thrown : " + e);
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            //Log.d("Urls", "IOException thrown : " + e);
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            //Log.d("Urls", "JSONException thrown : " + e);
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            //Log.d("Urls", "Exception thrown : " + e);
            e.printStackTrace();
            return null;
        }
    }
}
