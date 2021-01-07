package smarter.uearn.money.views.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;

import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.models.LatLong;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.JDAutoResponder;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.UrlContent;
import smarter.uearn.money.utils.upload.DataUploadUtils;

public class CallAndSMSListener extends BroadcastReceiver {

    Context context;
    private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final String ACTION_NEW_OUTGOING_SMS = "android.provider.Telephony.SMS_SENT";
    public static final String ACTION_NEW_OUTGOING_SMS1 = "android.provider.Telephony.NEW_OUTGOING_SMS";

    String storedEmailId;
    String jdurl = "www.azuyo.com";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent.getAction().equals("android.provider.Telephony.SMS_SENT")) {

        }
        storedEmailId = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null);
        if (storedEmailId == null) {
            storedEmailId = "No Email";
        }
        String action = intent.getAction();
        if (!UrlContent.isNetworkAvailable()) {

        } else {
            if (action.equalsIgnoreCase(SMS_ACTION)) {
                Bundle bundle = intent.getExtras();
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
                if (!message.isEmail()) {
                    try {
                        new upload().execute(message.getOriginatingAddress(), message.getMessageBody());
                    } catch (Exception e) {
                    }
                }
                SmsMessage[] msgs = null;
                String incomingSMSNO = null;
                String extractNo = "";
                if (bundle != null) {
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        incomingSMSNO = msgs[i].getOriginatingAddress();
                    }
                }
                String jdNumber = "+917618720282";
                String incommingMessage = msgs[0].getMessageBody();
                String smsTemplate = ApplicationSettings.getPref(AppConstants.USERINFO_JD_SMS_TEMPLATE, "0");
                Boolean isJDCheckTrue = true;

                if (isJDCheckTrue.equals(true)) {
                    JDAutoResponder autoResponder = new JDAutoResponder(context.getApplicationContext());
                    autoResponder.ProcessIncomingMSG(jdNumber, extractNo,incommingMessage, smsTemplate, jdurl, incomingSMSNO);
                } else {

                }
            }
            if (intent.getAction().equals(ACTION_NEW_OUTGOING_SMS) || action.equals(ACTION_NEW_OUTGOING_SMS1)) {
                Bundle extras = intent.getExtras();
                Object[] pdus1 = (Object[]) extras.get("pdus");
                for (Object pdu : pdus1) {
                    SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
                    String body = msg.getMessageBody();
                    Log.e("Value:::", body);
                }
                Bundle bundle = intent.getExtras();
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
                if (!message.isEmail()) {
                    try {
                        new Outupload().execute(message.getOriginatingAddress(), message.getMessageBody());
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    class upload extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String originAddress = params[0];
            String msgBody = params[1];
            try {
                sendSMSDataToServer(originAddress, msgBody);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    class Outupload extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String originAddress = params[0];
            String msgBody = params[1];
            try {
                sendOutSMSDataToServer(originAddress, msgBody);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    private void sendSMSDataToServer(String number, String message) {
        boolean dont_record_private_contacts = ApplicationSettings.getPref(AppConstants.DONT_CALL_RECORD_PRIVATE_CONTACTS, true);
        boolean capture_incoming_sms = ApplicationSettings.getPref(AppConstants.CAPTURE_INCOMING_SMS, false);
        boolean dont_record_number = false;
        if (dont_record_private_contacts) {
            if (isPrivateNumber(number)) {
                dont_record_number = true;
            }
        }
        if (number.matches(".*[A-Za-z].*")) {
            dont_record_number = true;
        }
        try {
            Boolean isLogout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
            if (!isLogout && capture_incoming_sms) {
                if (!dont_record_number) {
                    if (UrlContent.isNetworkAvailable()) {
                        String latitude = "", logitude = "";
                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            LatLong latLong = new LatLong(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
                            if (latLong != null) {
                                latitude = latLong.latitude;
                                logitude = latLong.longitude;
                            }
                        }

                        JSONObject json = new JSONObject();
                        json.put("activity_type", "sms_in");
                        json.put("event_type", "sms");
                        json.put("subject", "Incoming SMS");
                        json.put("message", message);
                        json.put("from", number);
                        if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "").equalsIgnoreCase(""))
                            json.put("to", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                        json.put("unread", false);
                        String contact_Name = CommonUtils.getContactName(context, number);
                        if (contact_Name != null && !contact_Name.equalsIgnoreCase(""))
                            json.put("caller_name", contact_Name);
                        json.put("email", storedEmailId);
                        json.put("lat", latitude);
                        json.put("long", logitude);
                        if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0")) {
                            json.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
                        }
                        json.put("created_at", CommonUtils.getTimeFormatInISO(new Date(System.currentTimeMillis())));
                        json.put("start_time", CommonUtils.getTimeFormatInISO(new Date(System.currentTimeMillis())));
                        String customer_number = CommonUtils.getContactName(context, number);

                        JSONObject customerJson = new JSONObject();
                        if (customer_number != null && !customer_number.equals(""))
                            customerJson.put("name", customer_number);
                        customerJson.put("number", number);
                        json.put("contact_person", customerJson);
                        JSONObject response = DataUploadUtils.postEmailContactInfo(Urls.getPostActivityUrl(), json);
                    } else
                        Toast.makeText(context, "Check your network setting.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendOutSMSDataToServer(String number, String message) {
        try {
            if (number != null && UrlContent.isNetworkAvailable()) {
                JSONObject json = new JSONObject();

                json.put(AppConstants.EVENT_TYPE, AppConstants.SMS_EVENT_TYPE);
                json.put("subject", "Outgoing SMS");
                json.put("message", message);
                json.put("from", number);
                if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "").equalsIgnoreCase(""))
                    json.put("to", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                json.put("unread", false);
                String contact_Name = CommonUtils.getContactName(context, number);
                if (contact_Name != null && !contact_Name.equalsIgnoreCase(""))
                    json.put("caller_name", contact_Name);
                json.put("email", storedEmailId);

                if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0"))
                    json.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));

                json.put("start_time", new Date(System.currentTimeMillis()).toGMTString());
                JSONObject response = DataUploadUtils.postEmailContactInfo(Urls.getCreateCmailUrl(), json);
                if (response != null) {
                    int parentID = response.getInt("id");
                    if (parentID > 0) {
                        JSONObject json1 = new JSONObject();
                        json1.put(AppConstants.EVENT_TYPE, AppConstants.SMS_EVENT_TYPE);
                        json1.put("subject", "Outgoing SMS");
                        if (contact_Name != null && !contact_Name.equalsIgnoreCase(""))
                            json1.put("caller_name", contact_Name);
                        json1.put("message", message);
                        if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "").equalsIgnoreCase(""))
                            json1.put("to", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                        json1.put("from", number);
                        json1.put("unread", false);

                        json1.put("email", storedEmailId);
                        json1.put("parent", parentID);
                        if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null && !ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0").equals("0"))
                            json1.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0"));
                        json1.put("start_time",new Date(System.currentTimeMillis()).toGMTString());
                        JSONObject responseChild = DataUploadUtils.postEmailContactInfo(Urls.getCreateCmailUrl(), json1);
                    }
                }
            } else
                Toast.makeText(context, "Check your network setting.", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isPrivateNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
        String[] privateContacts = ServiceHandler.loadPrivateContacts();
        if (privateContacts != null) {
            for (int i = 0; i < privateContacts.length; i++) {
                if (PhoneNumberUtils.compare(phoneNumber, privateContacts[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}
