package smarter.uearn.money.utils.ServerAPIConnectors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.JSONParser;
import smarter.uearn.money.utils.SmarterSMBApplication;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * Modified by srinath k.
 */

public class ServerHandler {
    public static final int PORT_NUMBER = 443;
    public static final String PORT_CONNECTION_URL = "www.smbdev.azurewebsites.net";
    public static final int TIME_OUT_SOCKET = 5000;
    public static final String ERROR_MESSAGE = "Busy. We will retry after some time.";

    private ServerHandlerListener listener;
    private String url_string;
    private String request_method;
    private JSONObject api_params;

    private String filepath;
    private String fileKey;
    private long request_code;
    private static ImageView IMAGEVIEW;
    private boolean knolarity = false;
    public static int fileLength = 0;
    private HashMap<String, String> map;

    ServerHandler(String url_string, String request_method, JSONObject api_params, ServerHandlerListener listener) {
        this.url_string = url_string;
        this.request_method = request_method;
        this.api_params = api_params;
        this.listener = listener;
    }

    ServerHandler(String url_string, ServerHandlerListener listener) {
        this.url_string = url_string;
        this.request_method = "GET";
        this.api_params = null;
        this.listener = listener;
    }

    ServerHandler(String url_string, JSONObject api_params, String filename, String fileKey, ServerHandlerListener listener) {
        this.url_string = url_string;
        this.request_method = "POST";
        this.api_params = api_params;
        this.filepath = filename;
        this.fileKey = fileKey;
        this.listener = listener;
    }

    ServerHandler(String url_string, String request_method, JSONObject api_params, ServerHandlerListener listener, boolean knolarity) {
        this.url_string = url_string;
        this.request_method = request_method;
        this.api_params = api_params;
        this.listener = listener;
        this.knolarity = knolarity;
    }

    public void call(long request_code) {
        this.request_code = request_code;

        if (filepath != null) {
            // Log.i("ServerHandler","File path is not null calling file uploader");
            FileUploader uploader = new FileUploader();
            uploader.execute();
        } else {
            ServerCaller caller = new ServerCaller();
            caller.execute();
        }
    }

    /* New Api For Login*/
    public void reCall(long request_code, HashMap<String, Object> map) {
        this.request_code = request_code;
        ReClickToCall uploader = new ReClickToCall();
        uploader.execute(map);
    }


    /* New Api For Login*/
    public void loginCall(long request_code) {
        this.request_code = request_code;
        LoginApiCaller loginApiCaller = new LoginApiCaller();
        loginApiCaller.execute();
    }

    private class ServerCaller extends AsyncTask<Void, Void, Bundle> {

        private boolean mError = false;

        @Override
        protected Bundle doInBackground(Void... params) {
            try {
                URLConnection conn = new URL(url_string).openConnection();
                if (conn instanceof HttpsURLConnection) {
                    HttpsURLConnection connection = (HttpsURLConnection) conn;
                    String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                    if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                        connection.setConnectTimeout(30000);
                        connection.setReadTimeout(30000);
                    } else {
                        connection.setConnectTimeout(20000);
                        connection.setReadTimeout(20000);
                    }

                    connection.setDoInput(true);

                    if (knolarity) {
                        connection.setRequestProperty("channel", "Basic");
                        connection.setRequestProperty("x-api-key", "rB84j4JwTe3eod3nZNKQ6ZA2dCJ8kjA8LoHHlZYh");
                        connection.setRequestProperty("authorization", "e005b6c3-33ec-4369-a90e-8f869d1546b1");
                        connection.setRequestProperty("cache-control", "no-cache");
                    } else {
                        if (ApplicationSettings.getPref("accessToken", "") != null) {
                            String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                            connection.setRequestProperty("Authorization", token);
                        }
                    }

                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod(request_method);
                    connection.connect();
                    if (api_params != null) {
                        OutputStreamWriter oStream = new OutputStreamWriter(connection.getOutputStream());
                        oStream.write(api_params.toString());
                        oStream.flush();
                        oStream.close();
                    }
                    int responsecode = connection.getResponseCode();
                    Bundle bundle = new Bundle();
                    if (responsecode == 200 || responsecode == 204) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        String resultString = result.toString();
                        connection.disconnect();
                        bundle.putInt("responseCode", responsecode);
                        bundle.putString("result", resultString);
                        return bundle;
                    } else {
                        bundle.putInt("responseCode", responsecode);
                        return bundle;
                    }
                } else {
                    HttpURLConnection connection = (HttpURLConnection) conn;
                    String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                    if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                        connection.setConnectTimeout(30000);
                        connection.setReadTimeout(30000);
                    } else {
                        connection.setConnectTimeout(20000);
                        connection.setReadTimeout(20000);
                    }

                    connection.setDoInput(true);

                    if (knolarity) {
                        connection.setRequestProperty("channel", "Basic");
                        connection.setRequestProperty("x-api-key", "rB84j4JwTe3eod3nZNKQ6ZA2dCJ8kjA8LoHHlZYh");
                        connection.setRequestProperty("authorization", "e005b6c3-33ec-4369-a90e-8f869d1546b1");
                        connection.setRequestProperty("cache-control", "no-cache");
                    } else {
                        if (ApplicationSettings.getPref("accessToken", "") != null) {
                            String token = "Bearer" + " " + ApplicationSettings.getPref("accessToken", "");
                            connection.setRequestProperty("Authorization", token);
                        }
                    }

                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod(request_method);
                    connection.connect();
                    if (api_params != null) {
                        OutputStreamWriter oStream = new OutputStreamWriter(connection.getOutputStream());
                        oStream.write(api_params.toString());
                        oStream.flush();
                        oStream.close();
                    }
                    int responsecode = connection.getResponseCode();
                    Bundle bundle = new Bundle();
                    if (responsecode == 200 || responsecode == 204) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        String resultString = result.toString();
                        connection.disconnect();
                        bundle.putInt("responseCode", responsecode);
                        bundle.putString("result", resultString);
                        return bundle;
                    } else {
                        bundle.putInt("responseCode", responsecode);
                        return bundle;
                    }
                }
            } catch (SocketTimeoutException e) {
                mError = true;
                SmarterSMBApplication.isSocketConnectionTimeOut = true;
                System.err.println("SocketTimeoutException: " + e.getMessage());
                return null;
            } catch (UnsupportedEncodingException e) {
                System.err.println("UnsupportedEncondingException: " + e.getMessage());
                return null;
            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);
            try {
                if (mError) {
                    if(SmarterSMBApplication.applicationContext != null) {
                        //Toast.makeText(SmarterSMBApplication.applicationContext, ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                }

                if (bundle != null) {
                    if (bundle.containsKey("result")) {
                        String response = bundle.getString("result");
                        String error = JSONParser.getErrorString(response);
                        if (error == null) {
                            ServerHandler.this.listener.onSuccess(request_code, response);
                        } else {
                            ServerHandler.this.listener.onError(request_code, error);
                        }
                    } else if (bundle.containsKey("responseCode")) {
                        int errorcode = bundle.getInt("responseCode");
                        ServerHandler.this.listener.onFailure(request_code, errorcode);
                    }
                } else {
                    ServerHandler.this.listener.onFailure(request_code, APIAdapter.CONNECTION_FAILED);
                }
            } catch (Exception e) {

            }


        }
    }

    private class FileUploader extends AsyncTask<Void, Void, Bundle> {
        private boolean mError = false;

        String crlf = "\r\n";
        String twoHyphens = "--";
        private SecureRandom random = new SecureRandom();

        protected String randomString() {
            return Long.toString(random.nextLong(), 36);
        }

        String boundary = "---------------------------" + randomString() + randomString() + randomString();

        @Override
        protected Bundle doInBackground(Void... params) {
            try {
                URLConnection conn = new URL(url_string).openConnection();
                if (conn instanceof HttpsURLConnection) {
                    HttpsURLConnection connection = (HttpsURLConnection) conn;
                    connection.setConnectTimeout(20000);
                    connection.setReadTimeout(20000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    connection.setRequestProperty("Accept-Encoding", "");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.connect();
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    if (api_params != null) {
                        Iterator<String> keys = api_params.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            write_parameter(oStream, key, (String) api_params.get(key));
                        }
                    }

                    if (filepath != null) {
                        write_fileParams(oStream, filepath, fileKey);
                        InputStream is = new FileInputStream(filepath);
                        pipe(is, oStream);
                    }

                    oStream.flush();
                    oStream.close();
                    int responsecode = connection.getResponseCode();
                    Bundle bundle = new Bundle();
                    if (responsecode == 200 || responsecode == 204) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        String resultString = result.toString();
                        connection.disconnect();
                        bundle.putInt("responseCode", responsecode);
                        bundle.putString("result", resultString);
                        return bundle;
                    } else {
                        bundle.putInt("responseCode", responsecode);
                        return bundle;
                    }
                } else {
                    HttpURLConnection connection = (HttpURLConnection) conn;
                    connection.setConnectTimeout(20000);
                    connection.setReadTimeout(20000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    connection.setRequestProperty("Accept-Encoding", "");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.connect();
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    if (api_params != null) {
                        Iterator<String> keys = api_params.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            write_parameter(oStream, key, (String) api_params.get(key));
                        }
                    }

                    if (filepath != null) {
                        write_fileParams(oStream, filepath, fileKey);
                        InputStream is = new FileInputStream(filepath);
                        pipe(is, oStream);
                    }

                    oStream.flush();
                    oStream.close();
                    int responsecode = connection.getResponseCode();
                    Bundle bundle = new Bundle();
                    if (responsecode == 200 || responsecode == 204) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        String resultString = result.toString();
                        connection.disconnect();
                        bundle.putInt("responseCode", responsecode);
                        bundle.putString("result", resultString);
                        return bundle;
                    } else {
                        bundle.putInt("responseCode", responsecode);
                        return bundle;
                    }
                }
            } catch (SocketTimeoutException e) {
                mError = true;
                return null;
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);
            try {
                if (mError) {
                    if(SmarterSMBApplication.applicationContext != null){
                        //Toast.makeText(SmarterSMBApplication.applicationContext, ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                }

                if (bundle != null) {
                    if (bundle.containsKey("result")) {
                        String response = bundle.getString("result");
                        String error = JSONParser.getErrorString(response);
                        if (error == null) {
                            ServerHandler.this.listener.onSuccess(request_code, response);
                        } else {
                            ServerHandler.this.listener.onError(request_code, error);
                        }
                    } else if (bundle.containsKey("responseCode")) {
                        int errorcode = bundle.getInt("responseCode");
                        ServerHandler.this.listener.onFailure(request_code, errorcode);
                    }
                } else {
                    ServerHandler.this.listener.onFailure(request_code, APIAdapter.CONNECTION_FAILED);
                }
            } catch (Exception e) {
            }
        }

        private void write_parameter(DataOutputStream os, String key, String value) throws IOException {
            os.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            os.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + this.crlf + this.crlf + value + this.crlf);
            os.writeBytes(this.crlf);
        }


        private void write_fileParams(DataOutputStream os, String filepath, String fileKey) throws IOException {
            os.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            os.writeBytes("Content-Disposition: form-data; name=\"" + fileKey + "\";filename=\"" + filepath + "\"" + this.crlf);
            os.writeBytes(this.crlf);
            String type = URLConnection.guessContentTypeFromName(filepath);
            if (type == null)
                type = "application/octet-stream";
            String key = "Content-Type: ";
            write_parameter(os, key, type);
        }

        private void pipe(InputStream in, DataOutputStream out) throws IOException {
            byte[] buf = new byte[50000];
            int nread;
            int total = 0;
            synchronized (in) {
                while ((nread = in.read(buf, 0, buf.length)) >= 0) {
                    out.write(buf, 0, nread);
                    total += nread;
                }
            }
            out.flush();
            buf = null;
        }
    }

    private class LoginApiCaller extends AsyncTask<Void, Void, Bundle> {

        String header, cookie;
        private boolean mError = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bundle doInBackground(Void... params) {
            try {
                URLConnection conn = new URL(url_string).openConnection();
                if (conn instanceof HttpsURLConnection) {
                    HttpsURLConnection connection = (HttpsURLConnection) conn;
                    connection.setConnectTimeout(20000);
                    connection.setReadTimeout(20000);
                    connection.setDoInput(true);

                    if (request_method.equalsIgnoreCase("Post") || request_method.equalsIgnoreCase("PUT")) {
                        connection.setDoOutput(true);
                    } else {
                        connection.setDoOutput(false);
                    }
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod(request_method);
                    connection.connect();
                    if (api_params != null) {
                        OutputStreamWriter oStream = new OutputStreamWriter(connection.getOutputStream());
                        oStream.write(api_params.toString());
                        oStream.flush();
                        oStream.close();
                    }
                    int responsecode = connection.getResponseCode();
                    Bundle bundle = new Bundle();
                    if (responsecode == 200 || responsecode == 204) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        if (connection.getHeaderField("x-access-token") != null) {
                            header = connection.getHeaderField("x-access-token");
                            bundle.putString("sessionHeader", header);
                        }
                        if (connection.getHeaderField("Set-Cookie") != null) {
                            Map<String, List<String>> headerSet = connection.getHeaderFields();
                            ArrayList<String> cookies = new ArrayList<>(headerSet.get("set-cookie"));
                            bundle.putStringArrayList("cookies", cookies);
                        }
                        String resultString = result.toString();
                        connection.disconnect();
                        bundle.putInt("responseCode", responsecode);
                        bundle.putString("result", resultString);
                        return bundle;
                    } else {
                        bundle.putInt("responseCode", responsecode);
                        return bundle;
                    }
                } else {
                    HttpURLConnection connection = (HttpURLConnection) conn;
                    connection.setConnectTimeout(20000);
                    connection.setReadTimeout(20000);
                    connection.setDoInput(true);

                    if (request_method.equalsIgnoreCase("Post") || request_method.equalsIgnoreCase("PUT")) {
                        connection.setDoOutput(true);
                    } else {
                        connection.setDoOutput(false);
                    }
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod(request_method);
                    connection.connect();
                    if (api_params != null) {
                        OutputStreamWriter oStream = new OutputStreamWriter(connection.getOutputStream());
                        oStream.write(api_params.toString());
                        oStream.flush();
                        oStream.close();
                    }
                    int responsecode = connection.getResponseCode();
                    Bundle bundle = new Bundle();
                    if (responsecode == 200 || responsecode == 204) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        if (connection.getHeaderField("x-access-token") != null) {
                            header = connection.getHeaderField("x-access-token");
                            bundle.putString("sessionHeader", header);
                        }
                        if (connection.getHeaderField("Set-Cookie") != null) {
                            Map<String, List<String>> headerSet = connection.getHeaderFields();
                            ArrayList<String> cookies = new ArrayList<>(headerSet.get("set-cookie"));
                            bundle.putStringArrayList("cookies", cookies);
                        }
                        String resultString = result.toString();
                        connection.disconnect();
                        bundle.putInt("responseCode", responsecode);
                        bundle.putString("result", resultString);
                        return bundle;
                    } else {
                        bundle.putInt("responseCode", responsecode);
                        return bundle;
                    }
                }
            } catch (SocketTimeoutException e) {
                mError = true;
                return null;
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);

            try {
                if (mError) {
                    if(SmarterSMBApplication.applicationContext != null){
                        //Toast.makeText(SmarterSMBApplication.applicationContext, ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                }

                if (bundle != null) {
                    if (bundle.containsKey("result")) {
                        if (bundle.containsKey("sessionHeader")) {
                            String header = bundle.getString("sessionHeader");
                            AppConstants.TOKEN = header;
                        }
                        if (bundle.containsKey("cookies")) {
                            AppConstants.COOKIE = bundle.getStringArrayList("cookies");
                        }
                        String response = bundle.getString("result");
                        String error = JSONParser.getErrorString(response);
                        if (error == null) {
                            ServerHandler.this.listener.onSuccess(request_code, response);
                        } else {
                            ServerHandler.this.listener.onError(request_code, error);
                        }
                    } else if (bundle.containsKey("responseCode")) {
                        int errorcode = bundle.getInt("responseCode");
                        ServerHandler.this.listener.onFailure(request_code, errorcode);
                    }
                } else {
                    ServerHandler.this.listener.onFailure(request_code, APIAdapter.CONNECTION_FAILED);
                }
            } catch (Exception e) {

            }
        }
    }

    public static void setProfilePic(ImageView imageView, String url_string) {
        IMAGEVIEW = imageView;
        GetXMLTask task = new GetXMLTask();
        task.execute(new String[]{url_string});
    }

    //To getImages
    private static class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;

            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            try {

                if (result != null) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    private class ReClickToCall extends AsyncTask<Map<String, Object>, Void, Bundle> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bundle doInBackground(Map<String, Object>... maps) {
            try {
                URLConnection conn = new URL(url_string).openConnection();
                if (conn instanceof HttpsURLConnection) {
                    HttpsURLConnection connection = (HttpsURLConnection) conn;
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    //For SBI
                    String auth_key = ApplicationSettings.getPref(AppConstants.AUTH_KEY, "");
                    if (auth_key != null && !auth_key.isEmpty()) {
                        connection.setRequestProperty("auth_key", auth_key);
                    } else {
                        connection.setRequestProperty("auth_key", "LIBKWbrQ-6oqsU-Ks2wvWscC6-DUT3Uvm9s");
                    }

                    if (ApplicationSettings.containsPref(AppConstants.C2C_TIMEOUT)) {
                        String timeOut = ApplicationSettings.getPref(AppConstants.C2C_TIMEOUT, "");
                        try {
                            if (timeOut != null && !timeOut.isEmpty()) {
                                int requestTime = Integer.parseInt(timeOut);
                                if (requestTime > 0) {
                                    connection.setConnectTimeout(requestTime);
                                    connection.setReadTimeout(requestTime);
                                } else {
                                    connection.setConnectTimeout(20000);
                                    connection.setReadTimeout(20000);
                                }
                            } else {
                                connection.setConnectTimeout(20000);
                                connection.setReadTimeout(20000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        connection.setConnectTimeout(20000);
                        connection.setReadTimeout(20000);
                    }
                    Map<String, Object> params = new LinkedHashMap<>();
                    params = maps[0];
                    StringBuilder postData = new StringBuilder();

                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                    connection.setDoInput(true);
                    connection.setDoOutput(true);

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
                        connection.disconnect();
                        bundle.putInt("responseCode", statusCode);
                        bundle.putString("result", resultString);
                        return bundle;
                    }
                } else {
                    HttpURLConnection connection = (HttpURLConnection) conn;
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    //For SBI
                    String auth_key = ApplicationSettings.getPref(AppConstants.AUTH_KEY, "");
                    if (auth_key != null && !auth_key.isEmpty()) {
                        connection.setRequestProperty("auth_key", auth_key);
                    } else {
                        connection.setRequestProperty("auth_key", "LIBKWbrQ-6oqsU-Ks2wvWscC6-DUT3Uvm9s");
                    }

                    if (ApplicationSettings.containsPref(AppConstants.C2C_TIMEOUT)) {
                        String timeOut = ApplicationSettings.getPref(AppConstants.C2C_TIMEOUT, "");
                        try {
                            if (timeOut != null && !timeOut.isEmpty()) {
                                int requestTime = Integer.parseInt(timeOut);
                                if (requestTime > 0) {
                                    connection.setConnectTimeout(requestTime);
                                    connection.setReadTimeout(requestTime);
                                } else {
                                    connection.setConnectTimeout(20000);
                                    connection.setReadTimeout(20000);
                                }
                            } else {
                                connection.setConnectTimeout(20000);
                                connection.setReadTimeout(20000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        connection.setConnectTimeout(20000);
                        connection.setReadTimeout(20000);
                    }
                    Map<String, Object> params = new LinkedHashMap<>();
                    params = maps[0];
                    StringBuilder postData = new StringBuilder();

                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                    connection.setDoInput(true);
                    connection.setDoOutput(true);

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
                        connection.disconnect();
                        bundle.putInt("responseCode", statusCode);
                        bundle.putString("result", resultString);
                        return bundle;
                    }
                }
            } catch (MalformedURLException e) {

            } catch (IOException e) {

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);
            try {
                if (bundle != null) {
                    if (bundle.containsKey("result")) {
                        String response = bundle.getString("result");
                        String error = JSONParser.getRfErrorString(response);
                        if (error == null) {
                            ServerHandler.this.listener.onSuccess(request_code, response);
                        } else {
                            ServerHandler.this.listener.onError(request_code, response);
                        }
                    } else if (bundle.containsKey("responseCode")) {
                        int errorcode = bundle.getInt("responseCode");
                        ServerHandler.this.listener.onFailure(request_code, errorcode);
                    }
                } else {
                    ServerHandler.this.listener.onFailure(request_code, APIAdapter.CONNECTION_FAILED);
                }
            } catch (Exception e) {

            }

        }
    }
}
