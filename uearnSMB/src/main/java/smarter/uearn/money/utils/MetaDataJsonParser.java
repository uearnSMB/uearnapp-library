package smarter.uearn.money.utils;

import smarter.uearn.money.utils.ServerAPIConnectors.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mcnavine on 1/5/2016.
 * <p/>
 * <p/>
 * Replacement Class for JSONParser2
 */


public class MetaDataJsonParser {

    // String result = "";


    public String postEmail(int user_id, String email, String from, String to,
                            String cc, String bcc, String event_type, String url,
                            String jd_url, String jobcard_url, String parent,
                            String live_recording_url, String start_time, String end_time,
                            String subject, boolean unread, String message, String lat,
                            String longitude, String attachments, String jobcard, String employee_assignee, String status, String caller_name) {

		/*
         * name:string company:string email:string phone:string members:text
		 * business_logo_url:string business_tagline:string
		 * call_sms_template:string jd_sms_template:string
		 * jc_sms_template:string lat:string long:string
		 * send_map_location:string call_recording_auto:string
		 * call_recording_status:string password:string secondary_users:string
		 */
        try {

            JSONObject jsonObj = new JSONObject();

            jsonObj.put("user_id", user_id);
            jsonObj.put("email", email);
            jsonObj.put("from", from);
            jsonObj.put("to", to);
            jsonObj.put("cc", cc);
            jsonObj.put("bcc", bcc);
            jsonObj.put("event_type", event_type);
            jsonObj.put("url", url);
            jsonObj.put("jd_url", jd_url);
            jsonObj.put("jobcard_url", jobcard_url);
            jsonObj.put("parent", parent);
            jsonObj.put("live_recording_url", live_recording_url);
            jsonObj.put("start_time", start_time);
            jsonObj.put("end_time", end_time);
            jsonObj.put("subject", subject);
            jsonObj.put("unread", unread);
            jsonObj.put("message", message);
            jsonObj.put("lat", lat);
            jsonObj.put("long", longitude);
            jsonObj.put("attachments", attachments);
            jsonObj.put("job_card", jobcard);
            jsonObj.put("employee_assignee", employee_assignee);
            jsonObj.put("status", status);
            jsonObj.put("caller_name", caller_name);

            URL connUrl = new URL(Urls.getCreateCmailUrl()); //HomeScreen.SERVER_ADDRESS+"/cmail_mails.json";
            HttpURLConnection conn = (HttpURLConnection) connUrl.openConnection();


            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
           // conn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            //conn.setRequestProperty("Accept", "*/*");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type", "application/json");


            String jsonParam = jsonObj.toString();

            //send request
            byte[] outputInBytes = jsonParam.getBytes("UTF-8");
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(outputInBytes);
            os.flush();
            os.close();

            //Log.i("Reupload-Service", "postEmail : Before Post of MetaData   : ");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            //Get Response
            InputStream inputStream = null;

            try {
                 inputStream =  new BufferedInputStream(conn.getInputStream());
                 return Utils.convertStreamToString(inputStream);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        //Log.e("Reupload-Service", "PostMetaData IO Exception thrown : " + e);
                        e.printStackTrace();
                    }
                }
                conn.disconnect();
            }

            //String result = Utils.convertStreamToString(is);
            // Log.i ("Reupload-Service", " postEmail : PostMetaData result (fromUtil) is " + result );


        } catch (JSONException | IOException e) {

            e.printStackTrace();
        }
        //Log.i ("Reupload-Service", " PostEmail Method result is null : ");
        return null;
    }

}
