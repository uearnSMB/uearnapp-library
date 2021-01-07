package smarter.uearn.money.utils.webservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ResponseInterface;
import smarter.uearn.money.utils.ServerAPIConnectors.ServerHandler;
import smarter.uearn.money.utils.SmarterSMBApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created on 1/18/2017.
 *
 * @author Dilip
 * @version 1.0
 *          <p>
 *          Class to consume web service
 *          Parse data by sending response object to parser
 */

public class WebService extends AsyncTask<String, Void, String> {

    private Activity threadActivity;
    private int operation;
    private ProgressDialog progressDialog;
    private JSONObject rawJsonObject;
    private boolean mError = false;
    private ResponseInterface mListener = null;

    WebService(int method) {
        this.operation = method;
    }

    public WebService(int method, ResponseInterface mListener) {
        this.operation = method;
        this.mListener = mListener;
    }

    WebService(Activity activity, int method) {
        this.threadActivity = activity;
        this.operation = method;
        progressDialog = new ProgressDialog(activity);
    }

    WebService(Activity activity, int method, JSONObject jsonObject) {
        this.threadActivity = activity;
        this.operation = method;
        this.rawJsonObject = jsonObject;
        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Do not load progress dialog when auth token has to be extracted
        if ((operation != 1) && (operation != 7) && (operation != 8) && (operation != 10)) {

            if (operation == 9) {
               /* progressDialog.setMessage("Saving...");
                progressDialog.setCanceledOnTouchOutside(false);*/
            } else {
                progressDialog.setMessage("Loading...");
            }

           // progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        // Declared outside to ensure closure after usage to avoid leaks
        BufferedReader in = null;
        DataOutputStream wr = null;
        HttpURLConnection conn = null;
        OutputStreamWriter streamWriter = null;
        try {
            /*Socket socket = new Socket();
            //socket.connect(new InetSocketAddress(ServerHandler.PORT_CONNECTION_URL, ServerHandler.PORT_NUMBER), ServerHandler.TIME_OUT_SOCKET);
            socket.connect(new InetSocketAddress(ServerHandler.PORT_CONNECTION_URL, ServerHandler.PORT_NUMBER), ServerHandler.TIME_OUT_SOCKET);
*/
            URL url = new URL(params[1]);
            //Log.d("url","url : "+url.toString());
            conn = (HttpURLConnection) url.openConnection();

            /*
             * If operation is 1 set to get auth token
             * else operation using get with basic application/json
             *
             */
            if (params[0].equalsIgnoreCase(Constants.GET)) {
                conn.setRequestMethod("GET");
                conn.setDoOutput(false);
            } else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }

            // form urlencoded will be set only for auth tokens
            if ((operation == 1) || (params.length > 2)) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            } else {
                conn.setRequestProperty("Content-Type", "application/json");
            }

            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);

            if (operation > 2) { // Add auth to header if its not login or get auth
                String token = ApplicationSettings.getPref("accessToken", "");
                //Log.d("access token","Bearer "+ token);
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }
            }

            if (operation == 9) {
                //Log.d(("json data"), rawJsonObject.toString());
                streamWriter = new OutputStreamWriter(conn.getOutputStream());
                streamWriter.write(rawJsonObject.toString());
                streamWriter.flush();
            }

            if ((operation == 1) || (params.length > 2)) {
                String urlParameters = params[2];
                //Log.d("urlParametrs", urlParameters);
                byte[] postData = urlParameters.getBytes();
                int postDataLength = postData.length;

                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                //Log.d("urlParametrs", Integer.toString(postDataLength));
                wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
            }

            conn.connect();

            int responseCode = conn.getResponseCode();
            // If response is success then return string to postExecute
            if ((responseCode == 200) || (responseCode == 204)) {

                in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                //Log.d("WebService", "Response " + response.toString());
                return response.toString();
            }
        } catch (SocketTimeoutException e) {
            mError = true;
            return null;
            //e.printStackTrace(); prints to terminal causing the crash
            //Log.d("auth", "error " + e);
        } catch (IOException e) {
            mError = true;
            return null;
            // e.printStackTrace(); prints to terminal causing the crash
            //Log.d("auth", "error " + e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (wr != null) {
                    wr.close();
                }

                if (conn != null) {
                    conn.disconnect();
                }

                if (streamWriter != null) {
                    streamWriter.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if(mListener != null) {
            mListener.getResponseStatus(true);
        }
        if ((operation != 1) && (operation != 7) && (operation != 8) && (operation != 10) && ((operation != 9))) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        if (mError) {
            if (threadActivity != null) {
                //Toast.makeText(threadActivity, ServerHandler.ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(SmarterSMBApplication.applicationContext, ServerHandler.ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        } else if (response == null) {
            if (threadActivity != null) {
                Toast.makeText(threadActivity, "Oops something went wrong", Toast.LENGTH_SHORT).show();
            }
            //CustomSingleButtonDialog.buildSingleButtonDialog("Error", "Oops something went wrong", threadActivity, false);
        }

        //Log.d("auth response", "response" + response);

        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                switch (operation) {
                    case 1:
                        JsonParseAndOperate.parseAuthToken(threadActivity, jsonObject);
                        break;
                    case 2: //Login information
                        JsonParseAndOperate.parseUserLogin(threadActivity, jsonObject);
                        break;
                    case 3: //Forgot password
                        JsonParseAndOperate.parseUserForgotPassword(threadActivity, jsonObject);
                        break;
                    case 4: //Change password
                        JsonParseAndOperate.parseUserChangePassword(threadActivity, jsonObject);
                        break;
                    case 5: //Sign up
                        JsonParseAndOperate.parseUserLogin(threadActivity, jsonObject);
                        break;
                    case 6: //Appointment list
                        //Log.d("appointments", "test" + jsonObject);
                        break;
                    case 7: // Settings list
                        JsonParseAndOperate.parseSettingsData(threadActivity, jsonObject);
                        break;
                    case 8: // Users team settings
                        JsonParseAndOperate.parseTeamData(threadActivity, jsonObject);
                        break;
                    case 9: // Save settings data
                        //Log.d("Settings", "saved" + jsonObject);
                        break;
                    case 10:
                        //Log.d("Smarter Logging", "data" + jsonObject);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Log.d("auth", "Parsing error" + e);
            }
        }
    }
}
