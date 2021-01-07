package smarter.uearn.money.utils.webservice;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import smarter.uearn.money.models.SingleApiAppointmentModel;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.JSONParser;
import smarter.uearn.money.utils.ServerAPIConnectors.ServerHandler;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.ClientHttpRequest;
import smarter.uearn.money.utils.upload.ClientHttpsRequest;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created on 05/06/17.
 *
 * @author dilipkumar4813
 * @version 1.0
 */

public class SingleApiUpload {

    private static final String KEY_NOTES_SERVER_FILE_NAME = "recording";
    private static final String KEY_SERVER_FILE_NAME = "audio";
    private static final String KEY_SERVER_PHONE = "phone";
    private static final String KEY_SERVER_CALL_TYPE = "call_type";
    private static final String KEY_SERVER_CONTACT_NAME = "contact_name";
    private static final String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

    static JSONObject mUploadObject;
    static boolean mError = false;
    static Activity mActivity;

    public static void runTask(JSONObject uploadObject, Activity activity) {
        mUploadObject = uploadObject;
        mActivity = activity;

        if (mUploadObject == null) {
            return;
        } else {
            new UploadData().execute();
        }
    }

    public static class UploadData extends AsyncTask<Void, Void, SingleApiAppointmentModel> {

        @Override
        protected SingleApiAppointmentModel doInBackground(Void... params) {
            return postSingleApiData(mUploadObject, "", "");
        }

        @Override
        protected void onPostExecute(SingleApiAppointmentModel singleApiAppointmentModel) {
            if (mError) {
                if (mActivity != null) {
                    Toast.makeText(mActivity, "Could not connect to server, please try later.", Toast.LENGTH_SHORT).show();
                }
            }

            mUploadObject = null;
            mError = false;
            mActivity = null;
        }
    }

    public static SingleApiAppointmentModel postSingleApiData(JSONObject jsonObject, String imagePath, String callRecordingPath) {
        String cloudFileName = callRecordingPath;
        try {
           /* Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ServerHandler.PORT_CONNECTION_URL, ServerHandler.PORT_NUMBER), ServerHandler.TIME_OUT_SOCKET);*/

            URLConnection conn = new URL(Urls.getCallStatusUpdateUrl()).openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection connection = (HttpsURLConnection) conn;
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");

                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Single Api", "Auth token : " + token);
                }

                ClientHttpsRequest httpRequest = new ClientHttpsRequest(connection);

                if (!imagePath.isEmpty()) {
                    httpRequest.setParameter("event_type_str", "IMAGE");
                    httpRequest.setParameter("content_type", "image/jpeg");
                    httpRequest.setParameter("image", new File(imagePath));
                }

                if (!callRecordingPath.isEmpty()) {
                    httpRequest.setParameter("event_type_str", "AUDIO");
                    httpRequest.setParameter("audio_content_type", "audio/mp3");
                    httpRequest.setParameter("content_type", "audio");

                    httpRequest.setParameter(KEY_NOTES_SERVER_FILE_NAME, cloudFileName, new File(callRecordingPath));
                    httpRequest.setParameter(KEY_SERVER_CALL_TYPE, "");
                    httpRequest.setParameter(KEY_SERVER_PHONE, "");
                    httpRequest.setParameter(KEY_SERVER_CONTACT_NAME, "");
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                //Log.d("Single Api", "Json in thread : " + jsonObject.toString());
                String jsonParam = jsonObject.toString();

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
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
                    inputStream = new BufferedInputStream(conn.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Single API response", result);
                    return JSONParser.getSingleApiResponse(result);
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

                if (ApplicationSettings.getPref("accessToken", "") != null) {
                    String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                    connection.setRequestProperty("Authorization", token);
                    //Log.d("Single Api", "Auth token : " + token);
                }

                ClientHttpRequest httpRequest = new ClientHttpRequest(connection);

                if (!imagePath.isEmpty()) {
                    httpRequest.setParameter("event_type_str", "IMAGE");
                    httpRequest.setParameter("content_type", "image/jpeg");
                    httpRequest.setParameter("image", new File(imagePath));
                }

                if (!callRecordingPath.isEmpty()) {
                    httpRequest.setParameter("event_type_str", "AUDIO");
                    httpRequest.setParameter("audio_content_type", "audio/mp3");
                    httpRequest.setParameter("content_type", "audio");

                    httpRequest.setParameter(KEY_NOTES_SERVER_FILE_NAME, cloudFileName, new File(callRecordingPath));
                    httpRequest.setParameter(KEY_SERVER_CALL_TYPE, "");
                    httpRequest.setParameter(KEY_SERVER_PHONE, "");
                    httpRequest.setParameter(KEY_SERVER_CONTACT_NAME, "");
                }

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                //Log.d("Single Api", "Json in thread : " + jsonObject.toString());
                String jsonParam = jsonObject.toString();

                byte[] outputInBytes = jsonParam.getBytes("UTF-8");
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
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
                    inputStream = new BufferedInputStream(conn.getInputStream());
                    String result = Utils.convertStreamToString(inputStream);
                    //Log.d("Single API response", result);
                    return JSONParser.getSingleApiResponse(result);
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
            mError = true;
            //Log.e("Single API", "SocketTimeoutException: " + e.getMessage());
            return null;
        } catch (MalformedURLException e) {
            //Log.e("Single API", "PostMetaData MalURL Exception thrown : " + e);
            e.printStackTrace();
        } catch (IOException e) {
            //Log.e("Single API", "PostMetaData IO Exception thrown : " + e);
            e.printStackTrace();
        } catch (Exception e) {
            //Log.e("Single API", "PostMetaData Exception thrown : " + e);
            e.printStackTrace();
        }
        return null;
    }
}
