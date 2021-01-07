package smarter.uearn.money.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import smarter.uearn.money.activities.AutoDailerActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.adapters.RecordingAdapter;
import smarter.uearn.money.callrecord.RecorderService;
import smarter.uearn.money.models.AppStatus;
import smarter.uearn.money.models.AttachmentList;
import smarter.uearn.money.models.Attachments;
import smarter.uearn.money.models.ChangePasswordInfo;
import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.GoogleAccountsList;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.HomeScreenMail;
import smarter.uearn.money.models.LatLong;
import smarter.uearn.money.models.OneViewScreenMail;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.models.SmartMail;
import smarter.uearn.money.models.WorkOrderEntryInfo;
import smarter.uearn.money.services.AcceptOrRejectNotificationService;
import smarter.uearn.money.utils.ServerAPIConnectors.APIAdapter;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.broadcastReceiver.Alarm_Receiver;
import smarter.uearn.money.services.CreateOptionsNotificationService;
import smarter.uearn.money.broadcastReceiver.OnTimeReceiver;
import smarter.uearn.money.services.UpdateAlarmTriggerService;
import smarter.uearn.money.R;
import smarter.uearn.money.activities.SmarterAuthActivity;
import smarter.uearn.money.utils.socialmedia.ServerAuthHelper;
import smarter.uearn.money.utils.upload.GPSTracker;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

import static com.facebook.FacebookSdk.getApplicationContext;

import static android.content.Context.ALARM_SERVICE;
//import static smarter.uearn.money.fragments.AutoDillerFragment.autoDialerFragmentTransactionId;

public class CommonUtils {

    private Context context;
    private static Boolean isDialogShow = false;
    public static ProgressDialog waitDialog;
    private static Boolean isFirstNumber = false;
    private static Boolean isSecondNumber = false;
    public static long MAX_FILE_SIZE = 819200;
    public static int total = 0;
    public static int activities = 0;
    private static boolean authenticationCheck = true;
    public static String start_date = "";
    public static String stage1 = "";
    public final static String AFTER_CALL_LEAD = "after_call_lead";
    public final static String CUSTOMER_TO_CONTACT = "customer_tocontact";

    public CommonUtils(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public static boolean customDialog(final Context context, final String number, final String Message, final Boolean isTrue) {
        // custom dialog
        final SmsManager smsManager = SmsManager.getDefault();
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.customdialog_sms);
        dialog.setTitle("Do you want to send SMS ?");

        TextView text = dialog.findViewById(R.id.text);
        TextView message = dialog.findViewById(R.id.message);
        String contactName = getContactName(context, number);
        if (contactName != null && !contactName.equalsIgnoreCase(""))
            text.setText("To : " + contactName);
        else
            text.setText("To : " + number);
        message.setText("Message : \n\n" + Message);
        Button dialogokButton = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogcancelButton = dialog.findViewById(R.id.dialogButtonCancel);
        // if button is clicked, close the custom dialog
        dialogokButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (number != null && !number.equalsIgnoreCase("")) {
                }
                Toast.makeText(context, "SMS Sent!", Toast.LENGTH_LONG).show();
            }
        });

        dialogcancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });

        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();


                    return true;
                }
                return false;
            }

        });

        dialog.show();
        return isDialogShow;
    }

    public static boolean customDialog(final Context context, final String number, final String Message, final Boolean isTrue, final String parentId) {
        // custom dialog
        final SmsManager smsManager = SmsManager.getDefault();
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.customdialog_sms);
        dialog.setTitle("Do you want to send SMS ?");

        // set the custom dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.text);
        TextView message = dialog.findViewById(R.id.message);
        String contactName = getContactName(context, number);
        if (contactName != null && !contactName.equalsIgnoreCase(""))
            text.setText("To : " + contactName);
        else
            text.setText("To : " + number);
        message.setText("Message : \n\n" + Message);
        Button dialogokButton = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogcancelButton = dialog.findViewById(R.id.dialogButtonCancel);

        // if button is clicked, close the custom dialog
        dialogokButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                dialog.dismiss();
                if (number != null && !number.equalsIgnoreCase("")) {
                    if (Message.length() > 160) {
                        //ArrayList<String> parts = (ArrayList<String>) splitInChunks(Message, 150);
                                       /*for (String str : parts) {
                                            smsManager.sendTextMessage(number, null, str, null, null);
									  }*/

                        SmsManager sms = SmsManager.getDefault();
                        ArrayList<String> parts = sms.divideMessage(Message);
                        sms.sendMultipartTextMessage(number, null, parts, null, null);

                    } else {
                        smsManager.sendTextMessage(number, null,
                                Message, null, null);

                    }
                }
                Toast.makeText(context,
                        "SMS Sent!", Toast.LENGTH_LONG)
                        .show();
            }
        });

        dialogcancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isTrue.equals(true)) {
                    ApplicationSettings.putPref(AppConstants.CALL_OUTGOING, false);
                    ApplicationSettings.putPref(AppConstants.CALL_INCOMING, false);
                    callDelete(context, parentId);
                } else {
                    isDialogShow = true;
                }
            }
        });
        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    if (isTrue.equals(true)) {
                        ApplicationSettings.putPref(AppConstants.CALL_OUTGOING, false);
                        ApplicationSettings.putPref(AppConstants.CALL_INCOMING, false);
                        callDelete(context, parentId);
                    } else {
                        isDialogShow = true;
                    }
                    return true;
                }
                return false;
            }

        });
        dialog.show();
        return isDialogShow;
    }

    public static boolean customDialogJobCard(final Context context, final String number, final String Message, final Boolean isTrue, final String parentId) {
        // custom dialog
        final SmsManager smsManager = SmsManager.getDefault();
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.customdialog_sms);
        dialog.setTitle("Do you want to send SMS ?");

        // set the custom dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.text);
        TextView message = dialog.findViewById(R.id.message);
        String contactName = getContactName(context, number);
        if (contactName != null && !contactName.equalsIgnoreCase(""))
            text.setText("To : " + contactName);
        else
            text.setText("To : " + number);
        message.setText("Message : \n\n" + Message);
        Button dialogokButton = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogcancelButton = dialog.findViewById(R.id.dialogButtonCancel);

        // if button is clicked, close the custom dialog
        dialogokButton.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                dialog.dismiss();
                if (number != null && !number.equalsIgnoreCase("")) {
                    if (Message.length() > 160) {
                        //ArrayList<String> parts = (ArrayList<String>) splitInChunks(Message, 150);
                                       /*for (String str : parts) {
                                            smsManager.sendTextMessage(number, null, str, null, null);
									  }*/

                        SmsManager sms = SmsManager.getDefault();
                        ArrayList<String> parts = sms.divideMessage(Message);
                        sms.sendMultipartTextMessage(number, null, parts, null, null);

                    } else {
                        smsManager.sendTextMessage(number, null,
                                Message, null, null);

                    }
                }
                Toast.makeText(context,
                        "SMS Sent!", Toast.LENGTH_LONG)
                        .show();

            }
        });

        dialogcancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (isFirstNumber.equals(true)) {
                    //isFirstNumber=false;
                    isSecondNumber = true;
                }

            }
        });

        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();

                    if (isFirstNumber.equals(true)) {
                        //isFirstNumber=false;
                        isSecondNumber = true;
                    }
                    if (isTrue.equals(true) && isFirstNumber.equals(true) && isSecondNumber.equals(true)) {
                        isFirstNumber = false;
                        isSecondNumber = false;
                        callDelete(context, parentId);
                    } else {
                        context.startActivity(new Intent(context,
                                UearnHome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        isDialogShow = true;
                    }
                    return true;
                }
                return false;
            }

        });

        dialog.show();
        return isDialogShow;
    }

    public static void customdialog(Context context, String content) {                            //called when login is unsuccessfull to display invalid credentials
        final Dialog dialog_custom = new Dialog(context);
        dialog_custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_custom.setContentView(R.layout.custom);
        TextView text = dialog_custom.findViewById(R.id.text);
        // text.setText(result[1] + " " + result[2] + " " + result[3]);
        text.setText(content);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        Button dialogButton = dialog_custom.findViewById(R.id.dialogButtonOK);
        dialogButton.setBackgroundColor(Color.WHITE);
        dialogButton.setTextColor(Color.BLACK);
        text.setTextColor(Color.BLACK);

        dialogButton.setTypeface(font);
        dialogButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                dialog_custom.dismiss();
            }
        });
        dialog_custom.show();
    }

    public static void customDailog(Context context, String message) {
        final AlertDialog.Builder dailog = new AlertDialog.Builder(context);
        dailog.setMessage("Appointment not saved");
        dailog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dailog.show();

    }

    public static void customdialogForPasswordFailure(final Activity context, String content) {                            //called when login is unsuccessfull to display invalid credentials
        final Dialog dialog_custom = new Dialog(context);
        dialog_custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_custom.setContentView(R.layout.custom_password);
        TextView text = dialog_custom.findViewById(R.id.text);
        // text.setText(result[1] + " " + result[2] + " " + result[3]);
        text.setText(content);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/helveticathin.ttf");
        Button dialogButton = dialog_custom.findViewById(R.id.dialogButtonOK);
        dialogButton.setText("Sign Up");
        dialogButton.setTypeface(font);
        dialogButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //context.startActivity(new Intent(context, SocialMediaRegistrationActivity.class));
                context.finish();
            }
        });
        dialog_custom.show();
    }

    public static void customDialogforChangePasswordSuccess(final Activity context, String content) {                            //called when login is unsuccessfull to display invalid credentials
        final Dialog dialog_custom = new Dialog(context);
        dialog_custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_custom.setContentView(R.layout.custom_password);
        TextView text = dialog_custom.findViewById(R.id.text);
        // text.setText(result[1] + " " + result[2] + " " + result[3]);
        text.setText(content);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/helveticathin.ttf");
        Button dialogButton = dialog_custom.findViewById(R.id.dialogButtonOK);
        dialogButton.setText("OK");
        dialogButton.setTypeface(font);
        dialogButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                SmarterSMBApplication.appSettings.setLogout(true);
                ApplicationSettings.putPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
                ApplicationSettings.putPref(AppConstants.CALL_RECORDING_OFF, true);
                ApplicationSettings.putPref(AppConstants.DISABLE_CALL_REC_BUTTONS, false);
                context.startActivity(new Intent(context, SmarterAuthActivity.class));
                context.finish();
            }
        });
        dialog_custom.show();
    }

    public static String getContactName(Context context, final String phoneNumber) {
        String contactName = "";
        if (phoneNumber != null && !phoneNumber.equalsIgnoreCase("")) {
            Uri uri;
            String[] projection;
            Uri mBaseUri = Contacts.Phones.CONTENT_FILTER_URL;
            projection = new String[]{android.provider.Contacts.People.NAME};
            try {
                Class<?> c = Class.forName("android.provider.ContactsContract$PhoneLookup");
                mBaseUri = (Uri) c.getField("CONTENT_FILTER_URI").get(mBaseUri);
                projection = new String[]{"display_name"};
            } catch (Exception e) {
            }
            uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber));
            try {
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        contactName = cursor.getString(0);
                    }

                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
                cursor = null;
            } catch (SecurityException se) {
            }

        }
        return contactName;
    }

    public static boolean customDialog(final Context context, final String number, final String Message, final Boolean isTrue, final String number2, final String Message2, final String id) {
        // custom dialog

        final SmsManager smsManager = SmsManager.getDefault();
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.customdialog_sms);
        dialog.setTitle("Do you want to send SMS ?");

        // set the custom dialog components - text, image and button
        TextView text = dialog.findViewById(R.id.text);
        TextView message = dialog.findViewById(R.id.message);

        String contactName = getContactName(context, number);
        if (contactName != null && !contactName.equalsIgnoreCase(""))
            text.setText("To : " + contactName);
        else
            text.setText("To : " + number);
        message.setText("Message : \n\n" + Message);
        Button dialogokButton = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogcancelButton = dialog.findViewById(R.id.dialogButtonCancel);

        // if button is clicked, close the custom dialog
        dialogokButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                dialog.dismiss();
                if (number != null && !number.equalsIgnoreCase("")) {
                    if (Message.length() > 160) {
                        //ArrayList<String> parts = (ArrayList<String>) splitInChunks(Message, 150);
                                       /*for (String str : parts) {
                                            smsManager.sendTextMessage(number, null, str, null, null);
									  }*/

                        SmsManager sms = SmsManager.getDefault();
                        ArrayList<String> parts = sms.divideMessage(Message);
                        sms.sendMultipartTextMessage(number, null, parts, null, null);

                        if (isTrue.equals(true)) {
                            context.startActivity(new Intent(context,
                                    UearnHome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            isDialogShow = true;
                            customDialogJobCard(context, number2, Message2, true, id);
                        }
                    } else {
                        smsManager.sendTextMessage(number, null,
                                Message, null, null);
                        if (isTrue.equals(true)) {
                            context.startActivity(new Intent(context,
                                    UearnHome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            isDialogShow = true;
                            customDialogJobCard(context, number2, Message2, true, id);
                        }
                    }
                }
                Toast.makeText(context,
                        "SMS Sent!", Toast.LENGTH_LONG)
                        .show();

            }
        });

        dialogcancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();

                    return true;
                }
                return false;
            }

        });

        dialog.show();
        return isDialogShow;
    }

    public static void callDelete(final Context context, final String id) {

        final class DeleteMail extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                waitDialog = new ProgressDialog(context);
                waitDialog.setMessage("Deleting");
                waitDialog.setCancelable(false);
                if (waitDialog != null)
                    waitDialog.show();
            }

            protected String doInBackground(String... urls) {

                String url1 = Urls.getCmailUrl(id);//HomeScreen.SERVER_ADDRESS+"/cmail_mails/" + id+ ".json";

                URL url = null;
                try {
                    url = new URL(url1);
                } catch (MalformedURLException exception) {
                    exception.printStackTrace();
                }
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestMethod("DELETE");
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                if (waitDialog != null) {
                    try {
                        waitDialog.dismiss();
                        waitDialog = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                context.startActivity(new Intent(context,
                        UearnHome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        }
        DeleteMail l = new DeleteMail();
        l.execute();
    }

    public static void validateLive(final AutoCompleteTextView textView) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (textView.getText().length() >= 8) {
                    Validation.isValidEmailorPhone(textView);
                }
            }
        });
    }

    public static String replaceBrackets(String input) {
        String output = input;
        if (input.contains("<") && input.contains(">")) {
            output = input.substring(input.indexOf("<") + 1, input.indexOf(">"));
        }
        return output;
    }

    public static String removePunctuations(String input) {
        return input.replaceAll("[ -]", "");
    }


    public static void process_contacts(final Context context, final AutoCompleteTextView textview1, String contact_string) {

        String[] cntct_arr = contact_string.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_spinner, cntct_arr);
        textview1.setThreshold(1);
        textview1.setAdapter(adapter);

        textview1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String resString = textview1.getText().toString();
                if (resString.contains("<") && resString.contains(">")) {
                    String cntct_number = resString.substring(resString.indexOf("<") + 1, resString.indexOf(">"));
                    cntct_number = cntct_number.replace(" ", "").replace("-", "");
                    textview1.setText(cntct_number);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String resString = textview1.getText().toString();
                if (resString.contains("<") && resString.contains(">")) {

                    String cntct_number = resString.substring(
                            resString.indexOf("<") + 1, resString.indexOf(">"));
                    if (cntct_number.contains(" "))
                        cntct_number = cntct_number.replace(" ", "");
                    if (cntct_number.contains("-"))
                        cntct_number = cntct_number.replace("-", "");
                    textview1.setText(cntct_number);
                }

            }
        });
    }

    public static void deleteDatabaseEntry(long dbid, Activity activity) {
        MySql dbHelper = MySql.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM mytbl where _id=" + dbid, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            db.delete("mytbl", "_id=" + dbid, null);
        }

        if (c != null && !c.isClosed()) {
            c.close();
        }
        db.close();
    }

    public static void sendMessageToPhoneNo(Activity activity, String messageTemplate, String toNumber, String parentChainId, String id, String cmail_url, String password) {
        if (messageTemplate != null && messageTemplate.length() > 0) {
            if (toNumber.length() > 0) {
                String messagetoUser = messageTemplate + "URL: ";
                if (cmail_url != null && !cmail_url.equals("") && !cmail_url.equals("null")) {
                    messagetoUser = messagetoUser + cmail_url;
                } else {
                    /* messagetoUser = messagetoUser + "http://smarter-biz.com/cmail_mails/" + id;*/
                    messagetoUser = messagetoUser + Urls.SERVER_ADDRESS+"/cmail_mails/" + id;
                }
                messagetoUser = messagetoUser + "\n" + "Password: " + password;
                String sendMessageNumber = "";
                sendMessageNumber = toNumber.replace(" ", "").trim();
                if (sendMessageNumber != null && !sendMessageNumber.equalsIgnoreCase("")) {
                    CommonUtils.customDialog(activity, sendMessageNumber, messagetoUser, true, parentChainId);
                }

            } else {
                Toast.makeText(activity, "SMS Not Delivered!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Log.e("Send Email", "message template empty");
        }
    }

    public static void sendJobCardToPhone(Activity activity, String customerNumber, String employeeNumber, String messageTemplate,
                                          String parentChainId, String id, String cmail_url, String password, String status) {

        if (!Pattern.matches(Validation.EMAIL_REGEX, customerNumber.replace(" ", "").trim())
                && !Pattern.matches(Validation.EMAIL_REGEX, employeeNumber.replace(" ", "").trim())) {
            //both are numbers
            String messagetoUser = messageTemplate + "URL: ";
            if (cmail_url != null && !cmail_url.equals("undefined") && !cmail_url.equals("null")) {
                messagetoUser = messagetoUser + cmail_url;
            } else {
                messagetoUser = messagetoUser + Urls.SERVER_ADDRESS+"/cmail_mails/" + id;
            }
            // messagetoUser=messagetoUser+ "\n" + "Password: " + password+"\n\n"+"Your Job/Order Status : "+status;

            messagetoUser = messagetoUser + "\n" + "Password: " + password;

            String sendMessageNumber = employeeNumber.replace(" ", "").trim();

            String sendMessageNumber2 = customerNumber.replace(" ", "").trim();

            if (sendMessageNumber != null && !sendMessageNumber.equalsIgnoreCase("") &&
                    sendMessageNumber2 != null && !sendMessageNumber2.equalsIgnoreCase("")) {
                CommonUtils.customDialog(activity, sendMessageNumber, messagetoUser, false, sendMessageNumber2, messagetoUser, parentChainId);
            }
        } else if (!Pattern.matches(Validation.EMAIL_REGEX, customerNumber.replace(" ", "").trim())) {
            //if only customer address is phone
            if (customerNumber.length() > 0) {
                if (messageTemplate != null && messageTemplate.length() > 0) {
                    String messagetoUser = messageTemplate + "URL: ";
                    if (cmail_url != null && !cmail_url.equals("undefined") && !cmail_url.equals("null")) {
                        messagetoUser = messagetoUser + cmail_url;
                    } else {
                        messagetoUser = messagetoUser + Urls.SERVER_ADDRESS+"/cmail_mails/" + id;
                    }
                    messagetoUser = messagetoUser + "\n" + "Password: " + password + "\n\n" + "Your Job/Order Status : " + status;
                    String sendMessageNumber = customerNumber.replace(" ", "").trim();
                    CommonUtils.customDialog(activity, sendMessageNumber, messagetoUser, true);
                }
            }
        } else if (!Pattern.matches(Validation.EMAIL_REGEX, employeeNumber.replace(" ", "").trim())) {
            //if only assignee address is phone
            if (employeeNumber.length() > 0) {
                if (messageTemplate != null && messageTemplate.length() > 0) {
                    String messagetoUser = messageTemplate + "URL: ";
                    if (cmail_url != null && !cmail_url.equals("undefined") && !cmail_url.equals("null")) {
                        messagetoUser = messagetoUser + cmail_url;
                    } else {
                        /* messagetoUser = messagetoUser + "http://smarter-biz.com/cmail_mails/" + id;*/
                        messagetoUser = messagetoUser + Urls.SERVER_ADDRESS+"/cmail_mails/" + id;
                    }
                    messagetoUser = messagetoUser + "\n" + "Password: " + password + "\n\n" + "Your Job/Order Status : " + status;
                    String sendMessageNumber = employeeNumber.replace(" ", "").trim();
                    CommonUtils.customDialog(activity, sendMessageNumber, messagetoUser, true);
                }
            }
        } else if (Pattern.matches(Validation.EMAIL_REGEX, employeeNumber.replace(" ", "").trim())
                && Pattern.matches(Validation.EMAIL_REGEX, customerNumber.replace(" ", "").trim())) {
            //if both are address are email
            //Log.i("Tag","Employee number is "+employeeNumber);
            activity.startActivity(new Intent(activity, UearnHome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            activity.finish();
        }

    }

    public static boolean isC2cNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            //Added By Srinth for server Check;
            return response;

        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            //Added By Srinth for server Check;
            if(response) {
                return !AppConstants.DATA_STATUS.equalsIgnoreCase("offline");
            } else {
                return response;
            }
        }
        return false;
    }

    public static boolean isNetworkWifiAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                    response = false;
                }
            }
            //Added By Srinth for server Check;
            if(response) {
                return !AppConstants.DATA_STATUS.equalsIgnoreCase("offline");
            } else {
                return response;
            }
        }
        return false;
    }

    public static String getNetworkTypeAndSignalStrength(Context context) {
        String networkType = "";
        int signalStrength = 0;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isAvailable() && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                networkType = "Wifi";
                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                signalStrength = wifiManager.getConnectionInfo().getRssi();
            } else if (mobile.isAvailable() && mobile.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                networkType = "Mobile 3G";
                TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo().get(0);
                CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
                signalStrength = cellSignalStrengthGsm.getDbm();
            } else {
                networkType = "No Network";
            }
            SmarterSMBApplication.currentSignalStrength = signalStrength;
        }
        return networkType;
    }

    public static String getTime(String createdDate) {
        if (createdDate != null) {
            try {
                SimpleDateFormat utcdateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                utcdateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date eventDate = utcdateFormat.parse(createdDate);
                DateFormat displayTimeFormat = DateFormat.getTimeInstance();
                displayTimeFormat.setTimeZone(Calendar.getInstance().getTimeZone());
                String display_time = displayTimeFormat.format(eventDate);
                return display_time;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return createdDate;
    }

    public static String getDate(String createdDate) {
        if (createdDate != null) {
            try {
                SimpleDateFormat utcdateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                utcdateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date eventDate = utcdateFormat.parse(createdDate);

                DateFormat displayDateFormat = DateFormat.getDateInstance();
                displayDateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
                String display_date = displayDateFormat.format(eventDate);
                return display_date;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return createdDate;
    }

    public static String getTimeFormatInISO(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    public static String getTimeFormatInUTC(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    public static int[] getNumbers(String nums) {
        int[] array = new int[2];
        try {
            String[] arr = nums.split(":");
            if (arr.length >= 2) {
                if (arr[0] != null && !(arr[0].isEmpty())) {
                    int hour = Integer.parseInt(arr[0]);
                    array[0] = hour;
                }
                if (arr[1] != null && !(arr[1].isEmpty())) {
                    int mins = Integer.parseInt(arr[1]);
                    array[1] = mins;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public static long stringToMilliSec(String string, String date_format) {
        DateFormat dateFormat = new SimpleDateFormat(date_format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date eventDate = null;
        try {
            if(!string.equalsIgnoreCase("0")) {
                eventDate = dateFormat.parse(string);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millisec = 0;
        if (eventDate != null)
            millisec = eventDate.getTime();

        return millisec;
    }

    public static LatLong getLatLong(Context context) {
        GPSTracker gpsTracker = new GPSTracker(context);
        if (gpsTracker.canGetLocation()) {

            LatLong latLong = new LatLong(Double.toString(gpsTracker.getLatitude()), Double.toString(gpsTracker.getLongitude()));
            return latLong;
        } else {
            gpsTracker.showSettingsAlert();
            return null;
        }
    }

    public static boolean imageValidation(Context context, Intent data) {
        if (data != null) {
            long size = Utils.getFileSize(data.getData(), context);
            if (size < MAX_FILE_SIZE) {
                return true;
            } else {
                Toast.makeText(context, "Please upload file less than 800 KB", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(context, "Error fetching image", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static void fillImageView(Context context, Uri uri, ImageView imageView) {
        if (uri != null) {
            Bitmap ThumbImage = null;
            String path = Utils.getPath(uri, context);
            ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 96, 96);
            imageView.setImageBitmap(ThumbImage);
            imageView.setBackgroundResource(android.R.color.transparent);
        }
    }


    /*
        public static void copyContactsToString(final Context context) {

            final class ContactsUpdate extends AsyncTask<String, Void, String> {

                protected String doInBackground(String... urls) {
                    StringBuilder sb = new StringBuilder();
                    try {

                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        Cursor cursor = context.getContentResolver().query(intent.getData(), null, null, null, null);
                        while (cursor.moveToNext()) {

                            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                            if (hasPhone.equalsIgnoreCase("1"))
                                hasPhone = "true";
                            else
                                hasPhone = "false";

                            if (Boolean.parseBoolean(hasPhone)) {

                                Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                                try {
                                    while (phones != null && phones.moveToNext()) {
                                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        sb.append(name);
                                        sb.append("  < ");
                                        sb.append(phoneNumber);
                                        sb.append(" > ");
                                        sb.append(",");

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("Error::::::", "getting contacts");
                                }
                                */
/*finally
                            {
								if (phones != null && !phones.isClosed()) {
									stopManagingCursor( phones );
									phones.close();
								}
							}*//*

                        }

                        // Find Email Addresses
                        Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                        try {
                            while (emails != null && emails.moveToNext()) {
                                String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                                if (emailAddress != null) {
                                    sb.append(name);
                                    sb.append("  < ");
                                    sb.append(emailAddress);
                                    sb.append(" > ");
                                    sb.append(",");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Error:::::::", "getting contacts");
                        }
						*/
/*finally
                        {
							if (emails != null && !emails.isClosed()) {
								stopManagingCursor( emails );
								emails.close();
							}
						}*//*

                    }
                    return sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error:::::::", "getting contacts");
                }
				*/
/*finally
                {
					if (cursor != null && !cursor.isClosed()) {
						stopManagingCursor( cursor );
						cursor.close();
					}
				}*//*

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    SmarterSMBApplication.appSettings.setContactString(result);
                }

            }
        }
        ContactsUpdate l = new ContactsUpdate();
        l.execute();
    }
*/
    public static String getEmbedingLandmark(String landmark) {
        String suffix = "&amp;";
        String temp = null;
        if (landmark != null) {
            temp = landmark.replace(" ", "%20");
        }
        if (temp != null) {
            return temp + suffix;
        }
        return "" + suffix;
    }

    private static Dialog dialog;

    public static void displayChangePassword(final Activity activity) {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_password_dialog_layout);

        final EditText oldPassword = dialog.findViewById(R.id.oldpassword);
        final EditText newPassword = dialog.findViewById(R.id.newpassword);
        final EditText reEnterPassword = dialog.findViewById(R.id.reEnterPassword);
        Button button = dialog.findViewById(R.id.confirmbutton);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(activity.getApplicationContext())) {
                    if (checkValidationForChangePassword(oldPassword, newPassword) == true) {
                        if (newPassword.getText().toString().equals(reEnterPassword.getText().toString()))
                            changePassword(activity, oldPassword.getText().toString(), newPassword.getText().toString(), reEnterPassword.getText().toString());
                        else
                            Toast.makeText(activity, "Password not matched", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "Please Enter all fields", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(activity, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
    }

    private static boolean checkValidationForChangePassword(EditText oldPassword, EditText newPassword) {

        boolean result = true;
        if (Validation.hasText(oldPassword, "Enter Old Password") == false)
            result = false;
        if (Validation.hasText(newPassword, "Enter New Password") == false)
            result = false;

        return result;
    }

    private static void changePassword(final Activity activity, String oldpassword, String newPassword, String confirmPassword) {
        ChangePasswordInfo changePasswordInfo = new ChangePasswordInfo(SmarterSMBApplication.SmartUser.getId(), oldpassword, newPassword, confirmPassword);
        APIProvider.ChangePassword forgotPassword = new APIProvider.ChangePassword(changePasswordInfo, 1, activity, "Sending Values..", new API_Response_Listener<Void>() {
            @Override
            public void onComplete(Void data, long request_code, int failure_code) {
                if (failure_code == -1) {
                    if (dialog != null) {
                        dialog.dismiss();
                        CommonUtils.customDialogforChangePasswordSuccess(activity, "Password Reset Successful");

                    }
                } else if (failure_code == APIProvider.INCORRECT_PASSWORD) {
                    CommonUtils.customdialog(activity, "In Correct Password, please try again");
                    //failure
                } else if (failure_code == APIProvider.ERROR_READING_USER)
                    CommonUtils.customdialog(activity, "Error reading user, please try again");
            }
        });
        forgotPassword.call();
    }

    public static void initMapWebview(WebView mapWebView, String mapKeyword) {
        String landmark = CommonUtils.getEmbedingLandmark(mapKeyword);
        if (landmark != null) {
//            String iframe = "<iframe src='https://maps.google.com/maps?q=" + landmark + "output=embed&amp;z=12' width=350 height=200 frameborder=0 style=border:0></iframe>";
//            mapWebView.getSettings().setJavaScriptEnabled(true);
//            mapWebView.loadData(iframe, "text/html", "utf-8");
        }
    }

    public static int getUnttendAppointmentsCount(SQLiteDatabase db, Context context) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0);
        long startTime = c.getTimeInMillis();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 23, 59);
        long endTime = c.getTimeInMillis();

        String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + startTime + " AND " + "START_TIME" + "<=" + endTime + " AND " + "COMPLETED" + "=" + 0;
        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        int count = cursor.getCount();
        AppConstants.UNATTENDED_COUNT = count;

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return count;
    }

    public static int getUpcomingAppointmentsCount(SQLiteDatabase db, Context context) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0);
        long startTime = c.getTimeInMillis();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 23, 59);
        long endTime = c.getTimeInMillis();

        String selection = "START_TIME" + ">=" + date.getTime() + " AND " + "START_TIME" + ">=" + startTime + " AND " + "START_TIME" + "<=" + endTime + " AND " + "COMPLETED" + "=" + 0;
        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
        int count = cursor.getCount();
        AppConstants.UPCOMING_COUNT = count;

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return count;
    }

    public static Bundle convertSmartMailToBundle(SmartMail smartMail) {
        Bundle bundle = new Bundle();
        bundle.putString("from", smartMail.getFrom());
        bundle.putString("name", smartMail.getName());
        bundle.putString("subject", smartMail.getSubject());
        bundle.putString("startTime", smartMail.getStartTime());
        bundle.putString("createdTime", smartMail.getCreatedAt());
        bundle.putString("updatedTime", smartMail.getUpdatedAt());
        bundle.putString("parent", smartMail.getParent());
        bundle.putString("to", smartMail.getTo());
        bundle.putString("message", smartMail.getMessage());
        bundle.putString("id", smartMail.getId());
        bundle.putString("eventtype", smartMail.getEventType());
        bundle.putString("caller_name", smartMail.getCallerName());
        bundle.putString("password", smartMail.getPassword());
        if (smartMail.getHTMLMessage() != null)
            bundle.putString("HTMLMessage", smartMail.getHTMLMessage());
        if (smartMail.getMessageId() != null)
            bundle.putString("message_id", smartMail.getMessageId());
        if (smartMail.getEventType() != null && (smartMail.getEventType().equals("job_card")
                || smartMail.getEventType().equals("draft_jobcard"))) {

            bundle.putString("jobcard", smartMail.getJobcard());
            bundle.putString("employee_assignee", smartMail.getEmployeeAssignee());
            bundle.putString("status", smartMail.getStatus());
        }
        if (smartMail.getEventType() != null && (smartMail.getEventType().equals("call") ||
                smartMail.getEventType().equals("live_meeting") || smartMail.getEventType().equals("draft_live_meeting"))) {
            if (smartMail.getUrl() != null)
                bundle.putString("URL", smartMail.getUrl());
        }

        bundle.putString("attachment_filename", smartMail.getAttachmentFilename());
        bundle.putString("attachment_fileUrl", smartMail.getAttachmentFileLink());

        //Added AttachmentList to the home_screen Bundle
        bundle.putSerializable("attachment_array", smartMail.getAttachmentList());

        return bundle;
    }

    public static Bundle convertOneViewScreenMailToBundle(OneViewScreenMail smartMail) {
        Bundle bundle = new Bundle();
        bundle.putString("from", smartMail.getFrom());
        bundle.putString("name", smartMail.getName());
        bundle.putString("subject", smartMail.getSubject());
        bundle.putString("startTime", smartMail.getStart_time());
        bundle.putString("createdTime", smartMail.getCreated_time());
        bundle.putString("to", smartMail.getTo());
        bundle.putString("message", smartMail.getMessage());
        bundle.putString("id", smartMail.getId());
        bundle.putString("eventtype", smartMail.getActivity_type());
        bundle.putString("caller_name", smartMail.getName());
        bundle.putString("password", smartMail.getPassword());
        if (smartMail.getUrl() != null && !smartMail.getUrl().equals(""))
            bundle.putString("URL", smartMail.getUrl());
        //Added AttachmentList to the home_screen Bundle
        bundle.putSerializable("attachment_array", smartMail.getAttachmentsSerilizableList());

        return bundle;
    }


    public static SmartMail convertBundleToSmartMail(Bundle bundle) {
        SmartMail smartMail = new SmartMail();

        if (bundle.containsKey("from"))
            smartMail.setFrom(bundle.getString("from"));
        if (bundle.containsKey("name"))
            smartMail.setName(bundle.getString("name"));
        if (bundle.containsKey("subject"))
            smartMail.setSubject(bundle.getString("subject"));
        if (bundle.containsKey("startTime"))
            smartMail.setStartTime(bundle.getString("startTime"));
        if (bundle.containsKey("createdTime"))
            smartMail.setCreatedAt(bundle.getString("createdTime"));
        if (bundle.containsKey("updatedTime"))
            smartMail.setUpdatedAt(bundle.getString("updatedTime"));
        if (bundle.containsKey("parent"))
            smartMail.setParent(bundle.getString("parent"));
        if (bundle.containsKey("to"))
            smartMail.setTo(bundle.getString("to"));
        if (bundle.containsKey("message"))
            smartMail.setMessage(bundle.getString("message"));
        if (bundle.containsKey("id"))
            smartMail.setId(bundle.getString("id"));
        if (bundle.containsKey("eventtype"))
            smartMail.setEventType(bundle.getString("eventtype"));
        if (bundle.containsKey("jobcard"))
            smartMail.setJobcard(bundle.getString("jobcard"));
        if (bundle.containsKey("employee_assignee"))
            smartMail.setEmployeeAssignee(bundle.getString("employee_assignee"));
        if (bundle.containsKey("status"))
            smartMail.setStatus(bundle.getString("status"));
        if (bundle.containsKey("URL"))
            smartMail.setUrl(bundle.getString("URL"));
        if (bundle.containsKey("caller_name"))
            smartMail.setCallerName(bundle.getString("caller_name"));
        if (bundle.containsKey("HTMLMessage"))
            smartMail.setHTMLMessage(bundle.getString("HTMLMessage"));
        if (bundle.containsKey("message_id"))
            smartMail.setMessageId(bundle.getString("message_id"));
        if (bundle.containsKey("password"))
            smartMail.setPassword(bundle.getString("password"));
        if (bundle.containsKey("attachment_filename"))
            smartMail.setAttachmentFilename(bundle.getString("attachment_filename"));
        if (bundle.containsKey("attachment_fileUrl"))
            smartMail.setAttachmentFileLink(bundle.getString("attachment_fileUrl"));
        if (bundle.containsKey("attachment_array")) {
            try {
                smartMail.setAttachmentList((ArrayList<AttachmentList>) bundle.getSerializable("attachment_array"));
            } catch (Exception e) {
                //Log.i("Exception", e.toString());
            }
        }
        return smartMail;
    }


    public static Bundle convertWorkOrderToBundle(WorkOrderEntryInfo workOrderEntryInfo) {
        Bundle bundle = new Bundle();
        bundle.putString("work_order_id", workOrderEntryInfo.work_order_id);
        bundle.putString("id", workOrderEntryInfo.id);
        bundle.putString("user_id", workOrderEntryInfo.user_id);
        bundle.putString("subject", workOrderEntryInfo.subject);
        bundle.putString("startTime", workOrderEntryInfo.event_start_date);
        bundle.putString("endTime", workOrderEntryInfo.event_end_date);
        bundle.putString("notes", workOrderEntryInfo.notes);

        bundle.putString("customer_name", workOrderEntryInfo.customer_name);
        bundle.putString("customer_number", workOrderEntryInfo.customer_number);
        bundle.putString("customer_email", workOrderEntryInfo.customer_email);
        bundle.putString("customer_company", workOrderEntryInfo.company_name);
        bundle.putString("company_address", workOrderEntryInfo.company_address);
        bundle.putString("website", workOrderEntryInfo.website);
        bundle.putString("designation", workOrderEntryInfo.designation);
        if (workOrderEntryInfo.latLong != null) {
            bundle.putString("latitude", workOrderEntryInfo.latLong.latitude);
            bundle.putString("longitude", workOrderEntryInfo.latLong.longitude);
        }
        bundle.putString("advance_date", workOrderEntryInfo.advance_date);
        bundle.putString("balance_due_date", workOrderEntryInfo.balance_due_date);
        bundle.putFloat("total_price", workOrderEntryInfo.total_price);
        bundle.putFloat("amount_receivable", workOrderEntryInfo.amount_receivable);
        bundle.putFloat("received_amount", workOrderEntryInfo.received_amount);
        bundle.putFloat("tax", workOrderEntryInfo.tax);
        bundle.putString("assigned_to", workOrderEntryInfo.assigned_to);
        bundle.putString("share_with", workOrderEntryInfo.share_with);
        bundle.putString("reminder_start_date", workOrderEntryInfo.reminder_start_date);
        bundle.putString("work_order_status", workOrderEntryInfo.work_order_status);
        bundle.putInt("work_completion", workOrderEntryInfo.work_completion);

        Gson gson = new Gson();
        String inputString = gson.toJson(workOrderEntryInfo.attachmentsArrayList);
        bundle.putString("attachment_arrayList", inputString);

      	/*  bundle.putString("attachment_filename", smartMail.getAttachmentFilename());
            bundle.putString("attachment_fileUrl", smartMail.getAttachmentFileLink());
		*/

        return bundle;
    }

    public static WorkOrderEntryInfo convertBundleToWorkOrder(Bundle bundle) {
        WorkOrderEntryInfo workOrderEntryInfo = new WorkOrderEntryInfo();
        ArrayList<Attachments> attachmentsArrayList = new ArrayList<>();
        String attachmentString = null;
        String latitude = "", logitude = "";
        if (bundle.containsKey("work_order_id"))
            workOrderEntryInfo.work_order_id = bundle.getString("work_order_id");
        if (bundle.containsKey("id"))
            workOrderEntryInfo.id = bundle.getString("id");
        if (bundle.containsKey("user_id"))
            workOrderEntryInfo.user_id = bundle.getString("user_id");
        if (bundle.containsKey("subject"))
            workOrderEntryInfo.subject = bundle.getString("subject");
        if (bundle.containsKey("startTime"))
            workOrderEntryInfo.event_start_date = bundle.getString("startTime");
        if (bundle.containsKey("endTime"))
            workOrderEntryInfo.event_end_date = (bundle.getString("endTime"));
        if (bundle.containsKey("notes"))
            workOrderEntryInfo.notes = (bundle.getString("notes"));
        if (bundle.containsKey("customer_name"))
            workOrderEntryInfo.customer_name = (bundle.getString("customer_name"));
        if (bundle.containsKey("customer_number"))
            workOrderEntryInfo.customer_number = (bundle.getString("customer_number"));
        if (bundle.containsKey("customer_email"))
            workOrderEntryInfo.customer_email = (bundle.getString("customer_email"));
        if (bundle.containsKey("customer_company"))
            workOrderEntryInfo.company_name = (bundle.getString("customer_company"));
        if (bundle.containsKey("company_address"))
            workOrderEntryInfo.company_address = (bundle.getString("company_address"));
        if (bundle.containsKey("website"))
            workOrderEntryInfo.website = (bundle.getString("website"));
        if (bundle.containsKey("designation"))
            workOrderEntryInfo.designation = (bundle.getString("designation"));
        if (bundle.containsKey("latitude"))
            latitude = (bundle.getString("latitude"));
        if (bundle.containsKey("longitude"))
            logitude = (bundle.getString("longitude"));
        LatLong latLong = new LatLong(latitude, logitude);
        workOrderEntryInfo.latLong = latLong;

        if (bundle.containsKey("advance_date"))
            workOrderEntryInfo.advance_date = (bundle.getString("advance_date"));
        if (bundle.containsKey("balance_due_date"))
            workOrderEntryInfo.balance_due_date = (bundle.getString("balance_due_date"));
        if (bundle.containsKey("total_price"))
            workOrderEntryInfo.total_price = (bundle.getFloat("total_price"));
        if (bundle.containsKey("amount_receivable"))
            workOrderEntryInfo.amount_receivable = (bundle.getFloat("amount_receivable"));
        if (bundle.containsKey("received_amount"))
            workOrderEntryInfo.received_amount = (bundle.getFloat("received_amount"));
        if (bundle.containsKey("tax"))
            workOrderEntryInfo.tax = (bundle.getFloat("tax"));
        if (bundle.containsKey("assigned_to"))
            workOrderEntryInfo.assigned_to = (bundle.getString("assigned_to"));
        if (bundle.containsKey("share_with"))
            workOrderEntryInfo.share_with = bundle.getString("share_with");
        if (bundle.containsKey("reminder_start_date"))
            workOrderEntryInfo.reminder_start_date = bundle.getString("reminder_start_date");
        if (bundle.containsKey("work_order_status"))
            workOrderEntryInfo.work_order_status = bundle.getString("work_order_status");
        if (bundle.containsKey("work_completion"))
            workOrderEntryInfo.work_completion = bundle.getInt("work_completion");
        /*if(bundle.containsKey("attachment_arraylist"))
            attachmentString = bundle.getString("attachment_arraylist");
        if(attachmentString != null) {
            Type type = new TypeToken<ArrayList<Attachments>>() {}.getType();
            Log.i("Reupload","Type is "+type.toString());
            Gson gson = new Gson();
            attachmentsArrayList = gson.fromJson(attachmentString, type);
        }
        workOrderEntryInfo.attachmentsArrayList = attachmentsArrayList;*/
        if (bundle.containsKey("attachment_arrayList"))
            attachmentString = bundle.getString("attachment_arrayList");
        //Log.i("KSN: attachmentString","Type is "+attachmentString);
        if (attachmentString != null) {
            Type type = new TypeToken<ArrayList<Attachments>>() {
            }.getType();
            //Log.i("Reupload","Type is "+type.toString());
            Gson gson = new Gson();
            attachmentsArrayList = gson.fromJson(attachmentString, type);
        }
        workOrderEntryInfo.attachmentsArrayList = attachmentsArrayList;
        return workOrderEntryInfo;
    }

    public static String getCallerNameFromDbid(Context context, long dbid) {
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        String toNo, toName;
        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + dbid + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            toNo = cursor.getString(cursor.getColumnIndex("TO1"));
            toName = cursor.getString(cursor.getColumnIndex("TONAME"));
            if (toNo != null && !toNo.equals("null"))
                return toNo;
            else if (toName != null)
                return toName;

        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }		                                        /*cursor Closed   :::KSN*/
        db.close();
        return null;
    }

    public static String getCallerNumber(SmartMail smartMail) {
        String callerNo = "";
        if (smartMail == null) return null;

        if (smartMail.getEventType().equals("gmail")) {
            return getCallerNameForMail(smartMail);
        }

        if (smartMail.getMessage() != null && (smartMail.getMessage().equalsIgnoreCase("Incoming Call") || smartMail.getSubject().equalsIgnoreCase("Incoming Call") ||
                smartMail.getSubject().equalsIgnoreCase("Incoming SMS"))) {

            callerNo = smartMail.getFrom();
        } else if (smartMail.getEventType() != null && smartMail.getEventType().equals("missed_call")) {
            callerNo = smartMail.getFrom();
        } else if (smartMail.getSubject() != null && (smartMail.getSubject().equalsIgnoreCase("Incoming Call") || smartMail.getSubject().equalsIgnoreCase("Incoming SMS"))) {
            callerNo = smartMail.getFrom();
        } else if (smartMail.getEventType() != null && smartMail.getMessage() != null && (smartMail.getEventType().equals("email") || smartMail.getEventType().equals("live_meeting")
                || smartMail.getEventType().equals("job_card") || smartMail.getEventType().equals("sms")
                || smartMail.getSubject().equals("Outgoing Call") || smartMail.getMessage().equals("Outgoing Call")
                || smartMail.getEventType().equals("draft_live_meeting") || smartMail.getEventType().equals("draft_jobcard"))) {

            callerNo = smartMail.getTo();
        } else if ((smartMail.getEventType().equals("email") || smartMail.getEventType().equals("live_meeting")
                || smartMail.getEventType().equals("job_card") || smartMail.getEventType().equals("sms")
                || smartMail.getSubject().equals("Outgoing Call") || smartMail.getEventType().equals("draft_live_meeting") || smartMail.getEventType().equals("draft_jobcard"))) {
            callerNo = smartMail.getTo();
        }
        return callerNo;
    }

    public static String setCallerName(Context context, String number) {

        String callerName = "";
        if (number != null) {
            callerName = "";
            if (Validation.isPhoneNumber(number)) {
                String contactName = CommonUtils.getContactName(context, number);
                if (contactName != null && !contactName.equalsIgnoreCase("")) {
                    callerName = contactName;
                }
            }
        }
        return callerName;
    }

    public static String handleMultipleEmailAddresses(String input) {
        String[] fields = input.split("[,;]");
        if (fields != null && fields.length >= 1) {
            return fields[0];
        }
        return input;
    }

    public static String getCallerNameForMail(SmartMail mail) {
        String email = SmarterSMBApplication.SmartUser.getEmail();
        if (mail.getTo().contains(email)) {
            return mail.getFrom();
        } else {
            return mail.getTo();
        }
    }

    public static void savingGoogleCalendarDataToLocalDB(Context context, ArrayList<GetCalendarEntryInfo> calendarEventsArrayList) {
        //Log.i("CalendarFlow", "In saving calendar data to local db");
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db;
        String lead_contacts = ApplicationSettings.getPref(AppConstants.LEAD_CONTACTS, "");
        long start_time = 0, end_time = 0;
        db = dbHelper.getWritableDatabase();
        if (calendarEventsArrayList != null) {
            for (int i = 0; i < calendarEventsArrayList.size(); i++) {
                GetCalendarEntryInfo googelCalendarEvent = calendarEventsArrayList.get(i);
                ContentValues cv = new ContentValues();
                cv.put("SUBJECT", googelCalendarEvent.subject);
                cv.put("NOTES", googelCalendarEvent.notes);
                start_time = stringToMilliSec(googelCalendarEvent.event_start_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                end_time = stringToMilliSec(googelCalendarEvent.event_end_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                cv.put("START_TIME", start_time);
                cv.put("END_TIME", end_time);
                cv.put("LOCATION", googelCalendarEvent.location);
                if (googelCalendarEvent.caller_number != null && !googelCalendarEvent.caller_number.equals("")) {
                    lead_contacts = lead_contacts + "," + googelCalendarEvent.caller_number;
                }
                cv.put("TO1", googelCalendarEvent.caller_number);
                /*if (googelCalendarEvent.alarm_before != null && !(googelCalendarEvent.alarm_before.isEmpty())) {
                    Long alarm = stringToMilliSec(googelCalendarEvent.alarm_before, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    cv.put("ALARMSETTO", alarm);
                } else {
                    cv.put("ALARMSETTO", start_time);
                }*/
                cv.put("CALLREC_URL", googelCalendarEvent.callrec_url);
                cv.put("FROM1", googelCalendarEvent.from);
                cv.put("TONAME", googelCalendarEvent.caller_name);
                cv.put("COMPANY_NAME", googelCalendarEvent.companyName);
                cv.put("DESIGNATION", googelCalendarEvent.designation);
                //Log.i("CommonUtils", "appointment id is " + googelCalendarEvent.appointment_id);
                cv.put("APPOINTMENT_ID", googelCalendarEvent.appointment_id);
                cv.put("EMAILID", googelCalendarEvent.email_id);
                cv.put("WEBSITE", googelCalendarEvent.website);
                cv.put("STATUS", googelCalendarEvent.status);
                cv.put("RESPONSE_STATUS", googelCalendarEvent.responsestatus);
                cv.put("EXTERNAL_REFERENCE", googelCalendarEvent.external_calendar_reference);
                cv.put("CUSTOMER_ID", googelCalendarEvent.customer_id);
                cv.put("REFERENCE_CMAIL", googelCalendarEvent.reference_cmail);
                cv.put("LEAD_SOURCE", googelCalendarEvent.lead_source);
                cv.put("RNR_COUNT", googelCalendarEvent.rnrCount);

                if (googelCalendarEvent.order_potential != null && !(googelCalendarEvent.order_potential.equalsIgnoreCase("null"))) {
                    cv.put("ORDER_POTENTIAL", googelCalendarEvent.order_potential);
                } else {
                    cv.put("ORDER_POTENTIAL", "");
                }

                if(googelCalendarEvent.subStatus1 != null && !(googelCalendarEvent.subStatus1.isEmpty())) {
                    cv.put("SUBSTATUS1", googelCalendarEvent.subStatus1);
                }

                if(googelCalendarEvent.subStatus2 != null && !(googelCalendarEvent.subStatus2.isEmpty())) {
                    cv.put("SUBSTATUS2", googelCalendarEvent.subStatus2);
                }

                cv.put("PRODUCT_TYPE", googelCalendarEvent.product_type);
                cv.put("COMPANY_ADDRESS", googelCalendarEvent.address);
                if (googelCalendarEvent.getFlpCount() != null && !(googelCalendarEvent.getFlpCount().isEmpty())) {
                    try {
                        int flp_count = Integer.parseInt(googelCalendarEvent.flp_count);
                        cv.put("FLP_COUNT", flp_count);
                        //Log.d("Login Common Utils", "" + flp_count);
                    } catch (NumberFormatException e) {
                        cv.put("FLP_COUNT", 0);
                        //Log.d("Login Common Utils", "exception");
                        e.printStackTrace();
                    }
                } else {
                    //Log.d("Login Common Utils", "else" + 0);
                }



                if (googelCalendarEvent.responsestatus != null && googelCalendarEvent.responsestatus.equals("completed")) {
                    cv.put("COMPLETED", 1);
                } else {
                    cv.put("COMPLETED", 0);
                }
                long dbId = 0;
                if (googelCalendarEvent.caller_number != null && !googelCalendarEvent.caller_number.equals("")) {

                    Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where APPOINTMENT_ID=" + "'" + googelCalendarEvent.appointment_id + "'", null);
                    if (!(cursor != null && cursor.getCount() > 0)) {
                        dbId = db.insert("remindertbNew", null, cv);
                        //Log.d("Common Utils", "Inserted DBID" + dbId);
                    } else {
                        //Log.d("Common Utils", "DBID Already exists with " + googelCalendarEvent.appointment_id);
                    }

                }
                if (googelCalendarEvent.caller_number != null && !googelCalendarEvent.caller_number.equals("")) {
                    saveContactInfoToSmartContactDB(context, googelCalendarEvent.caller_number, googelCalendarEvent.email_id,
                            googelCalendarEvent.status, googelCalendarEvent.notes, googelCalendarEvent.extranotes, googelCalendarEvent.lead_source, googelCalendarEvent.wrapup, googelCalendarEvent.event_start_date, googelCalendarEvent.transactionId);
                }
                if (googelCalendarEvent.responsestatus != null && googelCalendarEvent.responsestatus.equals("deleted")) {
                    if (dbId != 0) {
                        //Log.d("Common Utils", "Deleted DBID" + dbId);
                        db.delete("remindertbNew", "_id='" + dbId + "'", null);

                    }
                }
            }
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if(dbHelper != null)
            dbHelper.close();
        ApplicationSettings.putPref(AppConstants.LEAD_CONTACTS, lead_contacts);
    }

    public static void saveCalendarDataToLocalDB(SQLiteDatabase db, long startTime, long endTime, Context context, ArrayList<GetCalendarEntryInfo> calendarEventsArrayList) {
        String lead_contacts = ApplicationSettings.getPref(AppConstants.LEAD_CONTACTS, "");
        long start_time = 0, end_time = 0, alarm_set_to = 0;
        MySql dbHelper = null;

        if ((db == null) || ((db != null) && !db.isOpen())) {
            dbHelper = MySql.getInstance(context);
            db = dbHelper.getWritableDatabase();
            //Log.d("CommonUtils", "db open state "+db.isOpen());
        }

        if ((db != null) && (db.isOpen())) {
            //Log.d("CommonUtils", "Deleting remindertable");
            String truncate = ApplicationSettings.getPref(AppConstants.TRUNCATE_REMINDER_TABLE, "");
            if (truncate != null && !truncate.isEmpty()) {
                db.delete("remindertbNew", null, null);
                //Log.d("CommonUtils", "Deleted remindertable");
            }
        }

        if (calendarEventsArrayList != null) {
            for (int i = 0; i < calendarEventsArrayList.size(); i++) {
                GetCalendarEntryInfo googelCalendarEvent = calendarEventsArrayList.get(i);
                //check for local db if existing
                boolean existing = checkLocalDBforCalendarID(db, context, googelCalendarEvent.appointment_id);

                String autoSync = ApplicationSettings.getPref(AppConstants.AUTO_SYNC, "");
                if(autoSync != null && !autoSync.isEmpty()) {
                    existing = true;
                }

                //Log.d("CommonUtils", "saveCalendarDataToLocalDB existing" + existing);
                if (!existing) {
                    ContentValues cv = new ContentValues();
                    cv.put("SUBJECT", googelCalendarEvent.subject);
                    cv.put("NOTES", googelCalendarEvent.notes);
                    start_time = stringToMilliSec(googelCalendarEvent.event_start_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    end_time = stringToMilliSec(googelCalendarEvent.event_end_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    cv.put("START_TIME", start_time);
                    cv.put("END_TIME", end_time);
                    cv.put("LOCATION", googelCalendarEvent.location);
                    if (googelCalendarEvent.caller_number != null && !googelCalendarEvent.caller_number.equals("")) {
                        lead_contacts = lead_contacts + "," + googelCalendarEvent.caller_number;
                    }
                    cv.put("TO1", googelCalendarEvent.caller_number);
                    cv.put("ALARMSETTO", googelCalendarEvent.alarm_before);
                    cv.put("CALLREC_URL", googelCalendarEvent.callrec_url);
                    cv.put("FROM1", googelCalendarEvent.from);
                    cv.put("TONAME", googelCalendarEvent.caller_name);
                    cv.put("COMPANY_NAME", googelCalendarEvent.companyName);
                    cv.put("DESIGNATION", googelCalendarEvent.designation);
                    //Log.d("CommonUtils", "appointment id is " + googelCalendarEvent.appointment_id);
                    cv.put("APPOINTMENT_ID", googelCalendarEvent.appointment_id);
                    String appointmentId = googelCalendarEvent.appointment_id;
                    cv.put("EMAILID", googelCalendarEvent.email_id);
                    cv.put("WEBSITE", googelCalendarEvent.website);
                    if (googelCalendarEvent.alarm_before != null && !(googelCalendarEvent.alarm_before.isEmpty())) {
                        alarm_set_to = stringToMilliSec(googelCalendarEvent.alarm_before, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    } else {
                        alarm_set_to = start_time;
                    }

                    String status = googelCalendarEvent.status;

                    cv.put("STATUS", googelCalendarEvent.status);
                    cv.put("RESPONSE_STATUS", googelCalendarEvent.responsestatus);
                    cv.put("EXTERNAL_REFERENCE", googelCalendarEvent.external_calendar_reference);
                    cv.put("CUSTOMER_ID", googelCalendarEvent.customer_id);
                    cv.put("REFERENCE_CMAIL", googelCalendarEvent.reference_cmail);
                    cv.put("LEAD_SOURCE", googelCalendarEvent.lead_source);

                    if (googelCalendarEvent.order_potential != null && !(googelCalendarEvent.order_potential.equalsIgnoreCase("null"))) {
                        cv.put("ORDER_POTENTIAL", googelCalendarEvent.order_potential);
                    } else {
                        cv.put("ORDER_POTENTIAL", googelCalendarEvent.order_potential);
                    }

                    cv.put("PRODUCT_TYPE", googelCalendarEvent.product_type);
                    cv.put("COMPANY_ADDRESS", googelCalendarEvent.address);

                    /*flp_count added by srinath.k*/
                    cv.put("NOTES_AUDIO", googelCalendarEvent.notesAudioUrl);
                    cv.put("NOTES_IMAGE", googelCalendarEvent.notesImageUrl);

                    if (googelCalendarEvent.flp_count != null && !(googelCalendarEvent.flp_count.isEmpty())) {
                        try {
                            int flp_count = Integer.parseInt(googelCalendarEvent.flp_count);
                            cv.put("FLP_COUNT", flp_count);
                            //Log.d("CommonUtils", "" + flp_count);
                        } catch (NumberFormatException e) {
                            cv.put("FLP_COUNT", 0);
                            //Log.d("CommonUtils", "exception");
                            e.printStackTrace();
                        }
                    }

                    cv.put("RNR_COUNT", googelCalendarEvent.rnrCount);

                    if (googelCalendarEvent.getSubStatus1() != null && !(googelCalendarEvent.getSubStatus1().isEmpty())) {
                        cv.put("SUBSTATUS1", googelCalendarEvent.getSubStatus1());
                    }

                    if (googelCalendarEvent.getSubStatus2() != null && !(googelCalendarEvent.getSubStatus2().isEmpty())) {
                        cv.put("SUBSTATUS2", googelCalendarEvent.getSubStatus2());
                    }

                    //Log.d("CommonUtils", "Appointment ID : " + googelCalendarEvent.appointment_id + " - Sales stage :" + googelCalendarEvent.status + " - Number :" + googelCalendarEvent.caller_number + " - Time:" + start_time + " response " + googelCalendarEvent.responsestatus);

                    if (googelCalendarEvent.responsestatus != null && googelCalendarEvent.responsestatus.equals("completed")) {
                        cv.put("COMPLETED", 1);
                    } else {
                        cv.put("COMPLETED", 0);
                    }

                    if (googelCalendarEvent.updatedAt != null && !(googelCalendarEvent.updatedAt.isEmpty())) {
                        long updated_time = stringToMilliSec(googelCalendarEvent.updatedAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        cv.put("CREATED_AT", updated_time);
                        cv.put("UPDATED_AT", updated_time);
                    }

                    if (db == null || (db != null && !db.isOpen())) {
                        dbHelper = new MySql(context, "mydb", null, AppConstants.dBversion);
                        db = dbHelper.getWritableDatabase();
                    }

                    // Modified by dilip
                    boolean found = false;
                    boolean inSync = true;
                    boolean sameRecord = false;

                    //Cursor checkRecord = db.rawQuery("SELECT * from remindertbNew WHERE TO1='" + googelCalendarEvent.caller_number + "'", null);
                    Cursor checkRecord = db.rawQuery("SELECT * from remindertbNew WHERE APPOINTMENT_ID='" + appointmentId + "'", null);

                    if (checkRecord.moveToFirst()) {
                        do {
                            int syncValue = checkRecord.getInt(checkRecord.getColumnIndex("UPLOAD_STATUS"));
                            if (syncValue == 1) {
                                inSync = false;
                            }

                            String foundStatus = checkRecord.getString(checkRecord.getColumnIndex("STATUS"));
                            int startMils = checkRecord.getInt(checkRecord.getColumnIndex("START_TIME"));
                            //Log.d("table udd", "Item status" + foundStatus);
                            if (foundStatus.equalsIgnoreCase(status)) {
                                if (start_time == startMils) {
                                    sameRecord = true;
                                    //Log.d("table udd", "Same item");
                                }
                            }
                            found = true;
                        } while (checkRecord.moveToNext());
                    }

                    long dbId = 0;
                    if (!sameRecord) {
                        if (inSync) {
                            //Log.d("table udd", "item insync");
                            if (googelCalendarEvent.caller_number != null && !(googelCalendarEvent.caller_number.isEmpty())) {

                                if (found) {
                                    //Log.d("table udd", "item found" + appointmentId);
                                    //dbId = db.update("remindertbNew", cv, "TO1='" + googelCalendarEvent.caller_number + "'", null);
                                    dbId = db.update("remindertbNew", cv, "APPOINTMENT_ID='" + appointmentId + "'", null);
                                } else {
                                    //Log.d("table udd", "not found");
                                    dbId = db.insert("remindertbNew", null, cv);
                                }

                                if (googelCalendarEvent.caller_number != null && !googelCalendarEvent.caller_number.equals("")) {
                                    saveContactInfoToSmartContactDB(context, googelCalendarEvent.caller_number, googelCalendarEvent.email_id,
                                            googelCalendarEvent.status, googelCalendarEvent.notes, googelCalendarEvent.extranotes, googelCalendarEvent.lead_source, googelCalendarEvent.wrapup, googelCalendarEvent.event_start_date, googelCalendarEvent.transactionId);
                                }
                                Calendar cal = Calendar.getInstance();
                                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59);
                                Date date = new Date();
                                long currentTimedate = date.getTime();
                                long end = cal.getTimeInMillis();
                                /*if (alarm_set_to > currentTimedate && alarm_set_to < end) {
                                    if (dbId != 0 && (googelCalendarEvent.responsestatus != null) && (googelCalendarEvent.responsestatus.equals("deleted"))) {
                                       // setAlarm(context, dbId, alarm_set_to, "");
                                    }
                                }*/

                                if (googelCalendarEvent.responsestatus != null && googelCalendarEvent.responsestatus.equals("deleted")) {
                                    db.delete("remindertbNew", "_id='" + dbId + "'", null);
                                }
                            }

                        }
                        //Modification end - Dilip

                    } else {
                        //Log.e("CommonUtils","Entry already exist");
                    }

                    if (dbId != 0) {
                        long current = System.currentTimeMillis();
                        if(current > 0 && current < start_time) {
                            if (alarm_set_to != 0) {
                                setAlarm(context, dbId, alarm_set_to, "");
                            } else {
                                setAlarm(context, dbId, start_time, "");
                            }
                        }
                        //Log.d("SetAlarmInSync", ""+alarm_set_to);
                    }

                    if (checkRecord != null && !checkRecord.isClosed()) {
                        checkRecord.close();
                    }
                }
            }
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if(dbHelper != null)
            dbHelper.close();
        ApplicationSettings.putPref(AppConstants.LEAD_CONTACTS, lead_contacts);
    }

    /*Sync Data*/
    public static void saveCalendarSyncDataToLocalDB(SQLiteDatabase db, long startTime, long endTime, Context context, ArrayList<GetCalendarEntryInfo> calendarEventsArrayList) {
        String lead_contacts = ApplicationSettings.getPref(AppConstants.LEAD_CONTACTS, "");
        long start_time = 0, end_time = 0, alarm_set_to = 0;
        MySql dbHelper = null;
        if (db != null && !db.isOpen()) {
            dbHelper = MySql.getInstance(context);
            db = dbHelper.getWritableDatabase();
        }
        if (calendarEventsArrayList != null) {
            for (int i = 0; i < calendarEventsArrayList.size(); i++) {
                GetCalendarEntryInfo googelCalendarEvent = calendarEventsArrayList.get(i);
                ContentValues cv = new ContentValues();
                cv.put("SUBJECT", googelCalendarEvent.subject);
                cv.put("NOTES", googelCalendarEvent.notes);
                start_time = stringToMilliSec(googelCalendarEvent.event_start_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                end_time = stringToMilliSec(googelCalendarEvent.event_end_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                cv.put("START_TIME", start_time);
                cv.put("END_TIME", end_time);
                cv.put("LOCATION", googelCalendarEvent.location);
                if (googelCalendarEvent.caller_number != null && !googelCalendarEvent.caller_number.equals("")) {
                    lead_contacts = lead_contacts + "," + googelCalendarEvent.caller_number;
                }
                cv.put("TO1", googelCalendarEvent.caller_number);
                cv.put("ALARMSETTO", googelCalendarEvent.alarm_before);
                cv.put("CALLREC_URL", googelCalendarEvent.callrec_url);
                cv.put("FROM1", googelCalendarEvent.from);
                cv.put("TONAME", googelCalendarEvent.caller_name);
                cv.put("COMPANY_NAME", googelCalendarEvent.companyName);
                cv.put("DESIGNATION", googelCalendarEvent.designation);
                //Log.i("CommonUtils", "appointment id is " + googelCalendarEvent.appointment_id);
                cv.put("APPOINTMENT_ID", googelCalendarEvent.appointment_id);
                String appointmentId = googelCalendarEvent.appointment_id;
                cv.put("EMAILID", googelCalendarEvent.email_id);
                cv.put("WEBSITE", googelCalendarEvent.website);
                if (googelCalendarEvent.alarm_before != null && !(googelCalendarEvent.alarm_before.isEmpty())) {
                    alarm_set_to = stringToMilliSec(googelCalendarEvent.alarm_before, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                } else {
                    alarm_set_to = start_time;
                }
                cv.put("STATUS", googelCalendarEvent.status);
                cv.put("RESPONSE_STATUS", googelCalendarEvent.responsestatus);
                cv.put("EXTERNAL_REFERENCE", googelCalendarEvent.external_calendar_reference);
                cv.put("CUSTOMER_ID", googelCalendarEvent.customer_id);
                cv.put("REFERENCE_CMAIL", googelCalendarEvent.reference_cmail);
                cv.put("LEAD_SOURCE", googelCalendarEvent.lead_source);

                if (googelCalendarEvent.order_potential != null && !(googelCalendarEvent.order_potential.equalsIgnoreCase("null"))) {
                    cv.put("ORDER_POTENTIAL", googelCalendarEvent.order_potential);
                } else {
                    cv.put("ORDER_POTENTIAL", googelCalendarEvent.order_potential);
                }

                cv.put("PRODUCT_TYPE", googelCalendarEvent.product_type);
                cv.put("COMPANY_ADDRESS", googelCalendarEvent.address);

                /*flp_count added by srinath.k*/
                cv.put("NOTES_AUDIO", googelCalendarEvent.notesAudioUrl);
                cv.put("NOTES_IMAGE", googelCalendarEvent.notesImageUrl);

                if (googelCalendarEvent.flp_count != null && !(googelCalendarEvent.flp_count.isEmpty())) {
                    try {
                        int flp_count = Integer.parseInt(googelCalendarEvent.flp_count);
                        cv.put("FLP_COUNT", flp_count);
                        //Log.d("Common Utils", "" + flp_count);
                    } catch (NumberFormatException e) {
                        cv.put("FLP_COUNT", 0);
                        //Log.d("Common Utils", "exception");
                        e.printStackTrace();
                    }
                }

                cv.put("RNR_COUNT", googelCalendarEvent.rnrCount);

                if (googelCalendarEvent.getSubStatus1() != null && !(googelCalendarEvent.getSubStatus1().isEmpty())) {
                    cv.put("SUBSTATUS1", googelCalendarEvent.getSubStatus1());
                }

                if (googelCalendarEvent.getSubStatus2() != null && !(googelCalendarEvent.getSubStatus2().isEmpty())) {
                    cv.put("SUBSTATUS2", googelCalendarEvent.getSubStatus2());
                }

                   /* Log.d("Dt fetched", "Appointment ID : " + googelCalendarEvent.appointment_id + " - Sales stage :" +
                            googelCalendarEvent.status +
                            " - Number :" + googelCalendarEvent.caller_number + " - Time:" + start_time);*/

                if (googelCalendarEvent.responsestatus != null && googelCalendarEvent.responsestatus.equals("completed")) {
                    cv.put("COMPLETED", 1);
                } else {
                    cv.put("COMPLETED", 0);
                }
                if (db == null || (db != null && !db.isOpen())) {
                    dbHelper = new MySql(context, "mydb", null, AppConstants.dBversion);
                    db = dbHelper.getWritableDatabase();
                }


                boolean existing = checkLocalDBforCalendarID(db, context, googelCalendarEvent.appointment_id);
                long dbId = 0;
                if (googelCalendarEvent.caller_number != null && !(googelCalendarEvent.caller_number.isEmpty())) {
                    if (existing) {
                        //Log.d("table udd", "item found" + appointmentId);
                        dbId = db.update("remindertbNew", cv, "APPOINTMENT_ID='" + appointmentId + "'", null);
                    } else {
                        //Log.d("table udd", "not found");
                        dbId = db.insert("remindertbNew", null, cv);
                    }
                    saveContactInfoToSmartContactDB(context, googelCalendarEvent.caller_number, googelCalendarEvent.email_id,
                            googelCalendarEvent.status, googelCalendarEvent.notes, googelCalendarEvent.extranotes, googelCalendarEvent.lead_source, googelCalendarEvent.wrapup, googelCalendarEvent.event_start_date, googelCalendarEvent.transactionId);
                }

                if (googelCalendarEvent.responsestatus != null && googelCalendarEvent.responsestatus.equals("deleted")) {
                    db.delete("remindertbNew", "_id='" + dbId + "'", null);
                } else {
                    if (dbId != 0) {
                        long current = System.currentTimeMillis();
                        if (current > 0 && current < start_time) {
                            if (alarm_set_to != 0) {
                                setAlarm(context, dbId, alarm_set_to, "");
                            } else {
                                setAlarm(context, dbId, start_time, "");
                            }
                        }
                        //Log.d("SetAlarmInSync", ""+alarm_set_to);
                    }
                }
            }
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null)
            dbHelper.close();
        ApplicationSettings.putPref(AppConstants.LEAD_CONTACTS, lead_contacts);
    }

    /**/

    public static void saveGoogleCalendarDataToLocalDB(SQLiteDatabase db, Context context, ArrayList<GetCalendarEntryInfo> calendarEventsArrayList) {
        String lead_contacts = ApplicationSettings.getPref(AppConstants.LEAD_CONTACTS, "");
        long start_time = 0, end_time = 0;
        if (calendarEventsArrayList != null) {
            for (int i = 0; i < calendarEventsArrayList.size(); i++) {
                GetCalendarEntryInfo googelCalendarEvent = calendarEventsArrayList.get(i);
                //check for local db if existing
                boolean existing = checkLocalDBforExtenalID(db, context, googelCalendarEvent.external_calendar_reference);
                if (!existing) {
                    ContentValues cv = new ContentValues();
                    cv.put("SUBJECT", googelCalendarEvent.subject);
                    cv.put("NOTES", googelCalendarEvent.notes);
                    start_time = stringToMilliSec(googelCalendarEvent.event_start_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    end_time = stringToMilliSec(googelCalendarEvent.event_end_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    cv.put("START_TIME", start_time);
                    cv.put("END_TIME", end_time);
                    cv.put("LOCATION", googelCalendarEvent.location);
                    //TODO: Need to check properly the to field
                    if (googelCalendarEvent.caller_number != null && !googelCalendarEvent.caller_number.equals("")) {
                        lead_contacts = lead_contacts + "," + googelCalendarEvent.caller_number;
                    }
                    cv.put("TO1", googelCalendarEvent.caller_number);
                    cv.put("ALARMSETTO", googelCalendarEvent.alarm_before);
                    cv.put("CALLREC_URL", googelCalendarEvent.callrec_url);
                    cv.put("FROM1", googelCalendarEvent.from);
                    cv.put("TONAME", googelCalendarEvent.caller_name);
                    cv.put("COMPANY_NAME", googelCalendarEvent.companyName);
                    cv.put("DESIGNATION", googelCalendarEvent.designation);
                    //Log.i("CommonUtils", "appointment id is " + googelCalendarEvent.appointment_id);
                    cv.put("APPOINTMENT_ID", googelCalendarEvent.appointment_id);
                    String appointmentId = googelCalendarEvent.appointment_id;
                    cv.put("EMAILID", googelCalendarEvent.email_id);
                    cv.put("WEBSITE", googelCalendarEvent.website);

                    String status = googelCalendarEvent.status;

                    cv.put("STATUS", googelCalendarEvent.status);
                    cv.put("RESPONSE_STATUS", googelCalendarEvent.responsestatus);
                    cv.put("EXTERNAL_REFERENCE", googelCalendarEvent.external_calendar_reference);
                    cv.put("CUSTOMER_ID", googelCalendarEvent.customer_id);
                    cv.put("REFERENCE_CMAIL", googelCalendarEvent.reference_cmail);
                    cv.put("LEAD_SOURCE", googelCalendarEvent.lead_source);
                    cv.put("ORDER_POTENTIAL", googelCalendarEvent.order_potential);
                    cv.put("PRODUCT_TYPE", googelCalendarEvent.product_type);
                    cv.put("COMPANY_ADDRESS", googelCalendarEvent.address);
                    if (googelCalendarEvent.responsestatus != null && googelCalendarEvent.responsestatus.equals("completed")) {
                        cv.put("COMPLETED", 1);
                    } else {
                        cv.put("COMPLETED", 0);
                    }

                    // Modified by dilip
                    boolean found = false;
                    boolean inSync = true;
                    boolean sameRecord = false;

                    //Cursor checkRecord = db.rawQuery("SELECT * from remindertbNew WHERE TO1='" + googelCalendarEvent.caller_number + "'", null);
                    Cursor checkRecord = db.rawQuery("SELECT * from remindertbNew WHERE APPOINTMENT_ID='" + appointmentId + "'", null);

                    if (checkRecord.moveToFirst()) {
                        do {
                            int syncValue = checkRecord.getInt(checkRecord.getColumnIndex("UPLOAD_STATUS"));
                            if (syncValue == 1) {
                                inSync = false;
                            }

                            String foundStatus = checkRecord.getString(checkRecord.getColumnIndex("STATUS"));
                            int startMils = checkRecord.getInt(checkRecord.getColumnIndex("START_TIME"));
                            //Log.d("table udd", "Item status" + foundStatus);
                            if (foundStatus.equalsIgnoreCase(status)) {
                                if (start_time == startMils) {
                                    sameRecord = true;
                                    //Log.d("table udd", "Same item");
                                }
                            }
                            found = true;
                        } while (checkRecord.moveToNext());
                    }


                    if (!sameRecord) {
                        if (inSync) {
                            if (googelCalendarEvent.caller_number != null && !(googelCalendarEvent.caller_number.isEmpty())) {
                                long dbId;
                                if (found) {
                                    //dbId = db.update("remindertbNew", cv, "TO1='" + googelCalendarEvent.caller_number + "'", null);
                                    dbId = db.update("remindertbNew", cv, "APPOINTMENT_ID='" + appointmentId + "'", null);
                                } else {
                                    dbId = db.insert("remindertbNew", null, cv);
                                }

                                if (googelCalendarEvent.caller_number != null && !googelCalendarEvent.caller_number.equals("")) {
                                    saveContactInfoToSmartContactDB(context, googelCalendarEvent.caller_number, googelCalendarEvent.email_id,
                                            googelCalendarEvent.status, googelCalendarEvent.notes, googelCalendarEvent.extranotes, googelCalendarEvent.lead_source, googelCalendarEvent.wrapup, googelCalendarEvent.event_start_date, googelCalendarEvent.transactionId);
                                }
                                if (googelCalendarEvent.responsestatus != null && googelCalendarEvent.responsestatus.equals("deleted")) {
                                    db.delete("remindertbNew", "_id='" + dbId + "'", null);
                                }
                            }
                        }
                        //Modification end - Dilip
                        // calculateNextAlarm(context, db, googelCalendarEvent.responsestatus, dbId);
                    } else {
                        //Log.e("CommonUtils","Entry already exist");
                    }
                    if (checkRecord != null && !checkRecord.isClosed()) {
                        checkRecord.close();
                    }
                }
            }
        }
        /*if(db != null && db.isOpen()) {
            db.close();						*//*db Closed    :::KSN*//*
        }*/

        if (db != null && db.isOpen()) {
            db.close();
        }
        ApplicationSettings.putPref(AppConstants.LEAD_CONTACTS, lead_contacts);
    }

    public static void saveCalendarEventToLocalDB(Context context, GetCalendarEntryInfo getCalendarEntryInfo, String calendarType) {
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        String lead_contacts = ApplicationSettings.getPref(AppConstants.LEAD_CONTACTS, "");
        boolean existing = false;
        if (calendarType.equals("externalCalendar")) {
            existing = checkLocalDBforExtenalID(db, context, getCalendarEntryInfo.external_calendar_reference);
        } else if (calendarType.equals("app")) {
            existing = checkLocalDBforCalendarID(db, context, getCalendarEntryInfo.appointment_id);
        }
        long start_time = 0, end_time = 0, alarm_set_to = 0;
        if (!existing) {
            ContentValues cv = new ContentValues();
            if (getCalendarEntryInfo.subject != null)
                cv.put("SUBJECT", getCalendarEntryInfo.subject);
            if (getCalendarEntryInfo.notes != null)
                cv.put("NOTES", getCalendarEntryInfo.notes);
            start_time = stringToMilliSec(getCalendarEntryInfo.event_start_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            end_time = stringToMilliSec(getCalendarEntryInfo.event_end_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            cv.put("START_TIME", start_time);
            cv.put("END_TIME", end_time);
            cv.put("LOCATION", getCalendarEntryInfo.location);
            //Log.i("CommonUtils", "Caller number is " + getCalendarEntryInfo.caller_number);
            if (getCalendarEntryInfo.caller_number != null && !getCalendarEntryInfo.caller_number.equals("")) {
                lead_contacts = lead_contacts + "," + getCalendarEntryInfo.caller_number;
            }
            cv.put("TO1", getCalendarEntryInfo.caller_number);
            if (getCalendarEntryInfo.alarm_before != null && !(getCalendarEntryInfo.alarm_before.isEmpty())  && !(getCalendarEntryInfo.alarm_before.equalsIgnoreCase("0"))) {
                alarm_set_to = stringToMilliSec(getCalendarEntryInfo.alarm_before, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            } else {
                alarm_set_to = start_time;
            }
            cv.put("ALARMSETTO", getCalendarEntryInfo.alarm_before);

            cv.put("CALLREC_URL", getCalendarEntryInfo.callrec_url);
            cv.put("FROM1", getCalendarEntryInfo.from);
            cv.put("TONAME", getCalendarEntryInfo.caller_name);
            cv.put("COMPANY_NAME", getCalendarEntryInfo.companyName);
            cv.put("DESIGNATION", getCalendarEntryInfo.designation);
            cv.put("APPOINTMENT_ID", getCalendarEntryInfo.appointment_id);
            String appointmentId = getCalendarEntryInfo.appointment_id;

            cv.put("EMAILID", getCalendarEntryInfo.email_id);
            cv.put("WEBSITE", getCalendarEntryInfo.website);

            String status = getCalendarEntryInfo.status;

            cv.put("STATUS", getCalendarEntryInfo.status);
            //Log.d("FollowupStatus", getCalendarEntryInfo.status);

            /*Added By Srinath.k*/
            cv.put("RESPONSE_STATUS", getCalendarEntryInfo.responsestatus);
            //Log.d("GCM_RESPONSE", getCalendarEntryInfo.responsestatus);
            //cv.put("RESPONSE_STATUS", "unassigned");

            if (getCalendarEntryInfo.flp_count != null && !(getCalendarEntryInfo.flp_count.isEmpty())) {
                //Log.d("Common utils", getCalendarEntryInfo.flp_count);
                cv.put("FLP_COUNT", getCalendarEntryInfo.flp_count);
            } else {
                //Log.d("Common utils", "flp count is null");
                cv.put("FLP_COUNT", 0);
            }
            cv.put("RNR_COUNT", getCalendarEntryInfo.rnrCount);
            //Log.d("RNR_value", "Common Utils"+getCalendarEntryInfo.rnrCount);

            cv.put("EXTERNAL_REFERENCE", getCalendarEntryInfo.external_calendar_reference);
            cv.put("CUSTOMER_ID", getCalendarEntryInfo.customer_id);
            cv.put("REFERENCE_CMAIL", getCalendarEntryInfo.reference_cmail);
            cv.put("LEAD_SOURCE", getCalendarEntryInfo.lead_source);
            if (getCalendarEntryInfo.order_potential != null && !(getCalendarEntryInfo.order_potential.equalsIgnoreCase("null"))) {
                cv.put("ORDER_POTENTIAL", getCalendarEntryInfo.order_potential);
            } else {
                cv.put("ORDER_POTENTIAL", "");
            }

            cv.put("PRODUCT_TYPE", getCalendarEntryInfo.product_type);
            cv.put("COMPANY_ADDRESS", getCalendarEntryInfo.address);
            if (getCalendarEntryInfo.flp_count != null && !(getCalendarEntryInfo.flp_count.isEmpty())) {
                try {
                    int flp_count = Integer.parseInt(getCalendarEntryInfo.flp_count);
                    cv.put("FLP_COUNT", flp_count);
                    //Log.d("Common Utils", "" + flp_count);
                } catch (NumberFormatException e) {
                    cv.put("FLP_COUNT", 0);
                    //Log.d("Common Utils", "exception");
                    e.printStackTrace();
                }
            }

            if( getCalendarEntryInfo.caller_name == null) {
                getCalendarEntryInfo.caller_name = "";
            }
            if( getCalendarEntryInfo.caller_number == null) {
                getCalendarEntryInfo.caller_number = "";
            }
            String name = getCalendarEntryInfo.caller_name;
            String number = getCalendarEntryInfo.caller_number;
            if(getCalendarEntryInfo.subStatus1 != null && !(getCalendarEntryInfo.subStatus1.isEmpty())) {
                cv.put("SUBSTATUS1", getCalendarEntryInfo.subStatus1);
                //Log.d("Common Utils", "SUBSTATUS1"+getCalendarEntryInfo.subStatus1);
                if(getCalendarEntryInfo.responsestatus != null && !getCalendarEntryInfo.responsestatus.equalsIgnoreCase("completed")) {
                    if (getCalendarEntryInfo.subStatus1.equalsIgnoreCase("PAN APPROVED")) {
                        if (context != null) {
                            Toast.makeText(context, "Your PAN of " + name + " ( " + number + " ) has now been approved. The customer will now be dialled through Auto Dial.", Toast.LENGTH_LONG).show();
                        }
                    } else if (getCalendarEntryInfo.subStatus1.equalsIgnoreCase("APPROVED") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("APPROVED WITHOUT DOCS") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("REFERRED") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("BUREAU ERROR")) {
                        if (context != null) {
                            Toast.makeText(context, "Your customer " + name + " ( " + number + ") has been CIBIL Approved. The customer will now be dialled through the auto Dial.", Toast.LENGTH_LONG).show();
                        }
                    } else if (getCalendarEntryInfo.subStatus1.equalsIgnoreCase("TL APPROVED")) {
                        if (context != null) {
                            Toast.makeText(context, "Your Lead Sheet has been Approved by your TL. It has now gone to CIBIL verifier for checking. You will be notified when CIBIL verifier completes the QDE process.", Toast.LENGTH_LONG).show();
                        }
                    } else if(getCalendarEntryInfo.subStatus1.equalsIgnoreCase("NOT ELIGIBLE") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("RECENT REJECT") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("ALREADY IN PROCESS")  || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("WRONG PAN NUMBER") ) {
                        if (context != null) {
                            Toast.makeText(context, "Your PAN of " + name + " ( " + number + " ) has been rejected.", Toast.LENGTH_LONG).show();
                        }
                    } else if(getCalendarEntryInfo.subStatus1.equalsIgnoreCase("LEAD SHEET INCOMPLETE") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("COMPANY NOT LISTED") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("DESIGNATION NOT OK")  || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("SURROGATE NOT OK") ) {
                        if (context != null) {
                            Toast.makeText(context, "Your LEAD Sheet of " + name + " ( " + number + " ) has been rejected by your TL.", Toast.LENGTH_LONG).show();
                        }
                    } else if(getCalendarEntryInfo.subStatus1.equalsIgnoreCase("REPEAT ENTRY") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("CIBIL NOT ELIGIBLE") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("LOW CIBIL") || getCalendarEntryInfo.subStatus1.equalsIgnoreCase("DEFAULTER")) {
                        if (context != null) {
                            Toast.makeText(context, "Your customer " + name + " ( " + number + ") has been Rejected by CIBIL. ", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            if(getCalendarEntryInfo.subStatus2 != null && !(getCalendarEntryInfo.subStatus2.isEmpty())) {
                cv.put("SUBSTATUS2", getCalendarEntryInfo.subStatus2);
                //Log.d("Common Utils", "SUBSTATUS2"+getCalendarEntryInfo.subStatus2);
            }

            if(getCalendarEntryInfo.notesImageUrl != null && !(getCalendarEntryInfo.notesImageUrl.isEmpty())) {
                cv.put("NOTES_IMAGE", getCalendarEntryInfo.notesImageUrl);
            }

            if(getCalendarEntryInfo.updatedAt != null && !getCalendarEntryInfo.updatedAt.isEmpty()) {
                long update = CommonUtils.stringToMilliSec(getCalendarEntryInfo.updatedAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                //Log.d("GCM SERVICE", "updated_at"+update);
                cv.put("UPDATED_AT", update);
                cv.put("CREATED_AT", update);
            } else {
                //Log.d("GCM SERVICE", "updated_at is null");
            }
            //Log.i("CommonUtils", "IN saving it to local db response status is " + getCalendarEntryInfo.responsestatus);
            if (getCalendarEntryInfo.responsestatus != null && getCalendarEntryInfo.responsestatus.equals("completed")) {
                cv.put("COMPLETED", 1);
            } else
                cv.put("COMPLETED", 0);

            //Log.d("ReuploadService","MyGCMListenerService- New Appointment "+cv.toString());
            // Modified by dilip
            boolean found = false;
            boolean inSync = true;
            boolean sameRecord = false;

            //Cursor checkRecord = db.rawQuery("SELECT * from remindertbNew WHERE TO1='" + getCalendarEntryInfo.caller_number + "'", null);
            Cursor checkRecord = db.rawQuery("SELECT * from remindertbNew WHERE APPOINTMENT_ID='" + appointmentId + "'", null);

            if (checkRecord.moveToFirst()) {
                do {
                    int syncValue = checkRecord.getInt(checkRecord.getColumnIndex("UPLOAD_STATUS"));
                    if (syncValue == 1) {
                        inSync = false;
                    }
                    String foundStatus = checkRecord.getString(checkRecord.getColumnIndex("STATUS"));
                    int startMils = checkRecord.getInt(checkRecord.getColumnIndex("START_TIME"));
                    //Log.d("table udd", "Item status" + foundStatus);
                    if (foundStatus.equalsIgnoreCase(status)) {
                        if (start_time == startMils) {
                            sameRecord = true;
                            //Log.d("table udd", "Same item");
                        }
                    }
                    found = true;
                } while (checkRecord.moveToNext());
            }

            if (!sameRecord) {
                if (inSync) {
                    if (getCalendarEntryInfo.caller_number != null && !(getCalendarEntryInfo.caller_number.isEmpty())) {
                        long dbId = 0L;
                        if (found) {
                            //dbId = db.update("remindertbNew", cv, "TO1='" + getCalendarEntryInfo.caller_number + "'", null);
                            dbId = db.update("remindertbNew", cv, "APPOINTMENT_ID='" + appointmentId + "'", null);
                        } else {
                            dbId = db.insert("remindertbNew", null, cv);
                        }

                        if (getCalendarEntryInfo.responsestatus != null && getCalendarEntryInfo.responsestatus.equals("pending")) {
                            showNotification(context, dbId);
                        }
                        if (getCalendarEntryInfo.caller_number != null && !getCalendarEntryInfo.caller_number.equals("")) {
                            saveContactInfoToSmartContactDB(context, getCalendarEntryInfo.caller_number, getCalendarEntryInfo.email_id,
                                    getCalendarEntryInfo.status, getCalendarEntryInfo.notes, getCalendarEntryInfo.extranotes, getCalendarEntryInfo.lead_source, getCalendarEntryInfo.wrapup, getCalendarEntryInfo.event_start_date, getCalendarEntryInfo.transactionId);
                        }

                        Calendar cal = Calendar.getInstance();
                        /*cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59);*/
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        //Date date = new Date();
                        long currentTimedate = cal.getTimeInMillis();
                        long end = cal.getTimeInMillis();
                        /*if (alarm_set_to > currentTimedate && alarm_set_to < end) {*/
                        if (dbId != 0) {
                            //Log.d("Common_Utils", "SetAlarm from cloudC");
                            setAlarm(context, dbId, alarm_set_to, "");
                        }
                        /* }*/
                    }
                }
                //End of modification - Dilip
            }
            ApplicationSettings.putPref(AppConstants.LEAD_CONTACTS, lead_contacts);
            //Log.i("CommonUtils","Lead numbers are "+lead_contacts);
            getUnttendAppointmentsCount(db, context);
            getUpcomingAppointmentsCount(db, context);
            if (checkRecord != null && !checkRecord.isClosed()) {
                checkRecord.close();
            }
        /*if(db != null && db.isOpen()) {
            db.close();					*//*Db Close   :::KSN*//*
        }*/
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
        if(dbHelper != null)
            dbHelper.close();
    }

    public static void updateAppointmentInLocalDB(Context context, GetCalendarEntryInfo getCalendarEntryInfo, String calendarType) {
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        String lead_contacts = ApplicationSettings.getPref(AppConstants.LEAD_CONTACTS, "");
        boolean existing = false;
        Long dbid = 0L;
        if (calendarType.equals("externalCalendar")) {
            // existing = checkLocalDBforExtenalID(db, context, getCalendarEntryInfo.external_calendar_reference);
            dbid = getLocalDBforExternalReferenceID(db, context, getCalendarEntryInfo.external_calendar_reference);
        } else if (calendarType.equals("app")) {
            //TODO:change the long values of start time and endtime
            //existing = checkLocalDBforCalendarID(db, context, getCalendarEntryInfo.appointment_id);
            dbid = getLocalDBforCalendarID(db, context, getCalendarEntryInfo.appointment_id);
        }
        long start_time = 0, end_time = 0;
        if (existing) {
            //db.delete("remindertbNew", "APPOINTMENT_ID='" + getCalendarEntryInfo.appointment_id + "'", null);
        }

        ContentValues cv = new ContentValues();
        if (getCalendarEntryInfo.subject != null)
            cv.put("SUBJECT", getCalendarEntryInfo.subject);
        if (getCalendarEntryInfo.notes != null)
            cv.put("NOTES", getCalendarEntryInfo.notes);
        start_time = stringToMilliSec(getCalendarEntryInfo.event_start_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        end_time = stringToMilliSec(getCalendarEntryInfo.event_end_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        cv.put("START_TIME", start_time);
        cv.put("END_TIME", end_time);
        cv.put("LOCATION", getCalendarEntryInfo.location);
        if (getCalendarEntryInfo.caller_number != null && !getCalendarEntryInfo.caller_number.equals("")) {
            lead_contacts = lead_contacts + "," + getCalendarEntryInfo.caller_number;
        }
        cv.put("TO1", getCalendarEntryInfo.caller_number);
        cv.put("ALARMSETTO", getCalendarEntryInfo.alarm_before);
        cv.put("CALLREC_URL", getCalendarEntryInfo.callrec_url);
        cv.put("FROM1", getCalendarEntryInfo.from);
        cv.put("TONAME", getCalendarEntryInfo.caller_name);
        cv.put("COMPANY_NAME", getCalendarEntryInfo.companyName);
        cv.put("DESIGNATION", getCalendarEntryInfo.designation);
        cv.put("APPOINTMENT_ID", getCalendarEntryInfo.appointment_id);
        cv.put("EMAILID", getCalendarEntryInfo.email_id);
        cv.put("WEBSITE", getCalendarEntryInfo.website);
        cv.put("STATUS", getCalendarEntryInfo.status);
        cv.put("RESPONSE_STATUS", getCalendarEntryInfo.responsestatus);
        cv.put("EXTERNAL_REFERENCE", getCalendarEntryInfo.external_calendar_reference);
        cv.put("CUSTOMER_ID", getCalendarEntryInfo.customer_id);
        cv.put("REFERENCE_CMAIL", getCalendarEntryInfo.reference_cmail);
        cv.put("LEAD_SOURCE", getCalendarEntryInfo.lead_source);
        if (getCalendarEntryInfo.order_potential != null && !(getCalendarEntryInfo.order_potential.equalsIgnoreCase("null"))) {
            cv.put("ORDER_POTENTIAL", getCalendarEntryInfo.order_potential);
        } else {
            cv.put("ORDER_POTENTIAL", "");
        }

        cv.put("PRODUCT_TYPE", getCalendarEntryInfo.product_type);
        cv.put("COMPANY_ADDRESS", getCalendarEntryInfo.address);
        if (!(getCalendarEntryInfo.responsestatus.equalsIgnoreCase("completed"))) {
            if (getCalendarEntryInfo.flp_count != null && !(getCalendarEntryInfo.flp_count.isEmpty())) {
                try {
                    int flp_count = Integer.parseInt(getCalendarEntryInfo.flp_count);
                    cv.put("FLP_COUNT", flp_count);
                    //Log.d("Common Utils", "" + flp_count);
                } catch (NumberFormatException e) {
                    cv.put("FLP_COUNT", 0);
                    //Log.d("Common Utils", "exception");
                    e.printStackTrace();
                }
            }
        }
        cv.put("RNR_COUNT", getCalendarEntryInfo.rnrCount);

        if(getCalendarEntryInfo.subStatus1 != null && !(getCalendarEntryInfo.subStatus1.isEmpty())) {
            cv.put("SUBSTATUS1", getCalendarEntryInfo.subStatus1);
        }

        if(getCalendarEntryInfo.subStatus2 != null && !(getCalendarEntryInfo.subStatus2.isEmpty())) {
            cv.put("SUBSTATUS2", getCalendarEntryInfo.subStatus2);
        }

        if(getCalendarEntryInfo.notesImageUrl != null && !(getCalendarEntryInfo.notesImageUrl.isEmpty())) {
            cv.put("NOTES_IMAGE", getCalendarEntryInfo.notesImageUrl);
        }

        if(getCalendarEntryInfo.updatedAt != null && !getCalendarEntryInfo.updatedAt.isEmpty()) {
            long update = CommonUtils.stringToMilliSec(getCalendarEntryInfo.updatedAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            //Log.d("GCM SERVICE", "updated_at"+update);
            cv.put("UPDATED_AT", update);
            cv.put("CREATED_AT", update);
        } else {
            //Log.d("GCM SERVICE", "updated_at is null");
        }

        if (getCalendarEntryInfo.responsestatus != null && getCalendarEntryInfo.responsestatus.equals("completed")) {
            cv.put("COMPLETED", 1);
        } else {
            cv.put("COMPLETED", 0);
        }

        //Log.d("ReuploadService","MyGCMListenerService- Update Appointment "+cv.toString());

        if (getCalendarEntryInfo.caller_number != null && !(getCalendarEntryInfo.caller_number.isEmpty())) {
            if (dbid > 0) {
                int id = db.update("remindertbNew", cv, "_id=" + dbid, null);
            } else {
                dbid = db.insert("remindertbNew", null, cv);
            }
            if (getCalendarEntryInfo.responsestatus != null && getCalendarEntryInfo.responsestatus.equals("pending")) {
                showNotification(context, dbid);
            }
        }
        //setAlarm(context, dbId, start_time);
        ApplicationSettings.putPref(AppConstants.LEAD_CONTACTS, lead_contacts);
        getUnttendAppointmentsCount(db, context);
        getUpcomingAppointmentsCount(db, context);
        if (db != null && db.isOpen()) {
            db.close();
        }
        if(dbHelper != null)
            dbHelper.close();
    }

    private static boolean checkLocalDBforExtenalID(SQLiteDatabase db, Context context, String extenalReferenceId) {
        boolean existing = false;
        if (extenalReferenceId != null) {
            Cursor c = db.rawQuery("SELECT * FROM remindertbNew where EXTERNAL_REFERENCE='" + extenalReferenceId + "'", null);
            try {
                if (c.getCount() > 0) {
                    existing = true;
                    c.moveToFirst();
                }
            } catch (SQLiteCantOpenDatabaseException se) {

            }
            if (c != null) {
                c.close();
            }				/*cursor Closed*/
        }
        return existing;
    }

    /*Added By Srinath To get DBID for existing followup*/
    private static Long getLocalDBforExternalReferenceID(SQLiteDatabase db, Context context, String referenceId) {
        if (db != null && !db.isOpen()) {
            MySql dbHelper = MySql.getInstance(context);
            db = dbHelper.getWritableDatabase();
        }
        boolean existing = false;
        Long dbid = 0L;
        String selection = "EXTERNAL_REFERENCE='" + referenceId + "'";
        if (db != null && db.isOpen()) {
            Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
            if (cursor.getCount() > 0) {
                existing = true;
                cursor.moveToFirst();
                dbid = cursor.getLong(cursor.getColumnIndex("_id"));

            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return dbid;
    }

    private static long getLocalDBforCalendarID(SQLiteDatabase db, Context context, String appointmentId) {
        if (db != null && !db.isOpen()) {
            MySql dbHelper = MySql.getInstance(context);
            db = dbHelper.getWritableDatabase();
        }
        boolean existing = false;
        Long dbid = 0L;
        String selection = "APPOINTMENT_ID='" + appointmentId + "'";
        if (db != null && db.isOpen()) {
            Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
            if (cursor != null && cursor.getCount() > 0) {
                existing = true;
                cursor.moveToFirst();
                dbid = cursor.getLong(cursor.getColumnIndex("_id"));
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return dbid;
    }

    private static boolean checkLocalDBforCalendarID(SQLiteDatabase db, Context context, String appointmentId) {
        if (db != null && !db.isOpen()) {
            MySql dbHelper = MySql.getInstance(context);
            db = dbHelper.getWritableDatabase();
        }
        boolean existing = false;
        String selection = "APPOINTMENT_ID='" + appointmentId + "'";
        if (db != null && db.isOpen()) {
            Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
            if (cursor != null && cursor.getCount() > 0) {
                existing = true;
                cursor.moveToFirst();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return existing;
    }

    public static void saveContactInfoToSmartContactDB(Context context, String number, String emailId, String salesStatus, String notes, String extraNotes, String leadSource, String wrapUp, String eventStartDate, String transactionId) {
        //Log.i("CommonUtils","In save contact info to local db"+number+"Email id "+emailId+"Status "+salesStatus);
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long dbId;
        if (ApplicationSettings.containsPref(AppConstants.UPLOAD_STATUS2)) {
            boolean uploadStatus2 = ApplicationSettings.getPref(AppConstants.UPLOAD_STATUS2, false);
            if (!uploadStatus2) {
                dbId = checkSmartContactDB(db, context, number);
            } else {
                dbId = checkSmartContactDB2(db, context, number);
            }
        } else {
            dbId = checkSmartContactDB2(db, context, number);
        }

        //Log.i("CommonUtils","DB ID IS "+dbId);
        if (dbId != 0) {
            cv.put("STATUS", salesStatus);
            cv.put("NOTES", notes);
            cv.put("EXTRANOTES", extraNotes);
            cv.put("LEAD_SOURCE", leadSource);
            cv.put("WRAPUP", wrapUp);
            cv.put("EVENT_START_DATE", eventStartDate);
            cv.put("UUID", transactionId);
            cv.put("UPLOAD_STATUS", 1);
            long id = db.update("SmarterContact", cv, "_id=" + dbId, null);
            //Log.i("CommonUtils","DB UPDATED "+id);
        } else {
            cv.put("EMAILID", emailId);
            cv.put("STATUS", salesStatus);
            cv.put("NUMBER", number);
            cv.put("NOTES", notes);
            cv.put("EXTRANOTES", extraNotes);
            cv.put("LEAD_SOURCE", leadSource);
            cv.put("WRAPUP", wrapUp);
            cv.put("EVENT_START_DATE", eventStartDate);
            cv.put("UUID", transactionId);
            cv.put("UPLOAD_STATUS", 1);
            long dbid = db.insert("SmarterContact", null, cv);
            //Log.i("CommonUtils","DB ENTRY CREATED"+dbid);
        }
        /*
        if(db != null && db.isOpen()) {
			db.close();				*//*db Closed :::KSN*//*
        }*/

        if (db != null && db.isOpen()) {
            db.close();
        }
        if(dbHelper != null)
            dbHelper.close();
    }

    public static void saveBankContactInfoToSmartContactDB(Context context, String number, String emailId, String salesStatus, String notes, String substatus1, String substatus2, String extraNotes, String leadSource, String wrapUp, String eventStartDate, String transactionId) {

        try {
            //Log.i("CommonUtils","In save contact info to local db"+number+"Email id "+emailId+"Status "+salesStatus);
            MySql dbHelper = new MySql(context, "mydb", null, AppConstants.dBversion);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();

            long dbId;
            if (ApplicationSettings.containsPref(AppConstants.UPLOAD_STATUS2)) {
                boolean uploadStatus2 = ApplicationSettings.getPref(AppConstants.UPLOAD_STATUS2, false);
                if (!uploadStatus2) {
                    dbId = checkSmartContactDB(db, context, number);
                } else {
                    dbId = checkSmartContactDB2(db, context, number);
                }
            } else {
                dbId = checkSmartContactDB2(db, context, number);
            }

            //Log.i("CommonUtils","DB ID IS "+dbId);
            if (dbId != 0) {
                cv.put("STATUS", salesStatus);
                cv.put("SUBSTATUS1", substatus1);
                cv.put("SUBSTATUS2", substatus2);
                cv.put("NOTES", notes);
                cv.put("EXTRANOTES", extraNotes);
                cv.put("LEAD_SOURCE", leadSource);
                cv.put("WRAPUP", wrapUp);
                cv.put("EVENT_START_DATE", eventStartDate);
                cv.put("UUID", transactionId);
                cv.put("UPLOAD_STATUS", 1);
                long id = db.update("SmarterContact", cv, "_id=" + dbId, null);
                //Log.i("CommonUtils","DB UPDATED "+id);
            } else {
                cv.put("EMAILID", emailId);
                cv.put("STATUS", salesStatus);
                cv.put("SUBSTATUS1", substatus1);
                cv.put("SUBSTATUS2", substatus2);
                cv.put("NUMBER", number);
                cv.put("NOTES", notes);
                cv.put("EXTRANOTES", extraNotes);
                cv.put("LEAD_SOURCE", leadSource);
                cv.put("WRAPUP", wrapUp);
                cv.put("EVENT_START_DATE", eventStartDate);
                cv.put("UUID", transactionId);
                cv.put("UPLOAD_STATUS", 1);
                long dbid = db.insert("SmarterContact", null, cv);
                //Log.i("CommonUtils","DB ENTRY CREATED"+dbid);
            }
        }catch(Exception e){
            e.getMessage();
        }
    }

    //Offline sales status Added by Srinath
    public static void saveOflineStatusToSmartContactDB(Context context, String number, String emailId, String salesStatus, String notes, String extraNotes, String leadSource, String wrapUp, String eventStartDate, String transactionId) {
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long dbId = checkSmartContactDB(db, context, number);
        if (dbId != 0) {
            cv.put("STATUS", salesStatus);
            cv.put("NOTES", notes);
            cv.put("EXTRANOTES", extraNotes);
            cv.put("LEAD_SOURCE", leadSource);
            cv.put("WRAPUP", wrapUp);
            cv.put("EVENT_START_DATE", eventStartDate);
            cv.put("UUID", transactionId);
            cv.put("UPLOAD_STATUS", 0);
            cv.put("NUMBER", number);
            db.update("SmarterContact", cv, "_id=" + dbId, null);
        } else {
            cv.put("EMAILID", emailId);
            cv.put("STATUS", salesStatus);
            cv.put("NUMBER", number);
            cv.put("NOTES", notes);
            cv.put("EXTRANOTES", extraNotes);
            cv.put("LEAD_SOURCE", leadSource);
            cv.put("WRAPUP", wrapUp);
            cv.put("EVENT_START_DATE", eventStartDate);
            cv.put("UUID", transactionId);
            cv.put("UPLOAD_STATUS", 0);
            db.insert("SmarterContact", null, cv);
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public static void saveOflineStatusToSmartContactDB(Context context, String number, String emailId, String salesStatus, String notes, String subStatus1, String subStatus2, String extraNotes, String leadSource, String wrapUp, String eventStartDate, String transactionId) {
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long dbId = checkSmartContactDB(db, context, number);
        if (dbId != 0) {
            cv.put("STATUS", salesStatus);
            cv.put("NOTES", notes);
            cv.put("UPLOAD_STATUS", 0);
            cv.put("NUMBER", number);
            cv.put("SUBSTATUS1", subStatus1);
            cv.put("SUBSTATUS2", subStatus2);
            cv.put("EXTRANOTES", extraNotes);
            cv.put("LEAD_SOURCE", leadSource);
            cv.put("WRAPUP", wrapUp);
            cv.put("EVENT_START_DATE", eventStartDate);
            cv.put("UUID", transactionId);
            db.update("SmarterContact", cv, "_id=" + dbId, null);
        } else {
            cv.put("EMAILID", emailId);
            cv.put("STATUS", salesStatus);
            cv.put("NUMBER", number);
            cv.put("NOTES", notes);
            cv.put("SUBSTATUS1", subStatus1);
            cv.put("SUBSTATUS2", subStatus2);
            cv.put("EXTRANOTES", extraNotes);
            cv.put("LEAD_SOURCE", leadSource);
            cv.put("WRAPUP", wrapUp);
            cv.put("EVENT_START_DATE", eventStartDate);
            cv.put("UUID", transactionId);
            cv.put("UPLOAD_STATUS", 0);
            db.insert("SmarterContact", null, cv);
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public static void saveOflineStatusToSmartContactDB2(Context context, String number, String emailId, String salesStatus, String notes, String subStatus1, String subStatus2, String extraNotes, String leadSource, String wrapUp, String eventStartDate, String transactionId) {
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long dbId = checkSmartContactDB2(db, context, number);
        if (dbId != 0) {
            cv.put("STATUS", salesStatus);
            cv.put("NOTES", notes);
            cv.put("UPLOAD_STATUS", 2);
            cv.put("NUMBER", number);
            cv.put("SUBSTATUS1", subStatus1);
            cv.put("SUBSTATUS2", subStatus2);
            cv.put("EXTRANOTES", extraNotes);
            cv.put("LEAD_SOURCE", leadSource);
            cv.put("WRAPUP", wrapUp);
            cv.put("EVENT_START_DATE", eventStartDate);
            cv.put("UUID", transactionId);
            db.update("SmarterContact", cv, "_id=" + dbId, null);
        } else {
            cv.put("EMAILID", emailId);
            cv.put("STATUS", salesStatus);
            cv.put("NUMBER", number);
            cv.put("NOTES", notes);
            cv.put("SUBSTATUS1", subStatus1);
            cv.put("SUBSTATUS2", subStatus2);
            cv.put("EXTRANOTES", extraNotes);
            cv.put("LEAD_SOURCE", leadSource);
            cv.put("WRAPUP", wrapUp);
            cv.put("EVENT_START_DATE", eventStartDate);
            cv.put("UUID", transactionId);
            cv.put("UPLOAD_STATUS", 2);
            db.insert("SmarterContact", null, cv);
        }

        Log.d("Urls", "saveOflineStatusToSmartContactDB" + "Number: " + number+ "EmailId: " + emailId+ "SalesStatus: " + salesStatus+ "Notes: " + notes+ "Substatus1: " + subStatus1+ "Substatus2: " + subStatus2+ "ExtraNotes: " + extraNotes+ "LeadSource: " + leadSource+ "Wrapup: " + wrapUp+ "EventStartDate: " + eventStartDate+ "TransactionId: " + transactionId);

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public static void saveStatusToSmartContactDB(Context context, String number, String emailId, String salesStatus, String notes, String subStatus1, String subStatus2, String extraNotes, String leadSource, String wrapUp, String eventStartDate, String transactionId) {
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("EMAILID", emailId);
        cv.put("STATUS", salesStatus);
        cv.put("NUMBER", number);
        cv.put("NOTES", notes);
        cv.put("SUBSTATUS1", subStatus1);
        cv.put("SUBSTATUS2", subStatus2);
        cv.put("EXTRANOTES", extraNotes);
        cv.put("LEAD_SOURCE", leadSource);
        cv.put("WRAPUP", wrapUp);
        cv.put("EVENT_START_DATE", eventStartDate);
        cv.put("UUID", transactionId);
        cv.put("UPLOAD_STATUS", 2);
        db.insert("SmarterContact", null, cv);

        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private static long checkSmartContactDB(SQLiteDatabase db, Context context, String number) {
        long dbId = 0;
        try {
            Cursor c = db.rawQuery("SELECT * FROM SmarterContact where NUMBER ='" + number + "' AND UPLOAD_STATUS != '2'", null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                long id = c.getLong(c.getColumnIndex("_id"));
                dbId = id;
            } else {
                dbId = 0;
            }
            if (c != null) {
                c.close();
            }
        }catch(Exception e){
            e.getMessage();
        }
        return dbId;
    }

    private static long checkSmartContactDB2(SQLiteDatabase db, Context context, String number) {
        long dbId = 0;
        try {
            Cursor c = db.rawQuery("SELECT * FROM SmarterContact where NUMBER ='" + number + "' AND UPLOAD_STATUS = '2'", null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                long id = c.getLong(c.getColumnIndex("_id"));
                dbId = id;
            } else {
                dbId = 0;
            }
            if (c != null) {
                c.close();
            }
        }catch(Exception e){
            e.getMessage();
        }
        return dbId;
    }

    public static String getStatusFromSmartContactTable(Context context, String number) {
        //Log.i("CommonUtils", "Number for status is " + number);
        String status = "";
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM SmarterContact where NUMBER='" + number + "'", null);
        //Log.i("CommonUtils", "Cursor count is " + c.getCount());
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String statusString = c.getString(c.getColumnIndex("STATUS"));
            status = statusString;
            //Log.d("Latest Sales Status", status);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }						/*cursor Closed :::KSN*/
        if (db != null && db.isOpen()) {
            db.close();					/*db Closed :::KSN*/
        }
        return status;
    }

    public static String[] getStatusArrayFromSmartContactTable(Context context, String number) {
        //Log.i("CommonUtils", "Number for status is " + number);
        String status = "";
        String[] statusArr = new String[3];
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        String query = "NUMBER='" +number+"'";
        Cursor c = db.rawQuery("SELECT * FROM SmarterContact where NUMBER='" + number + "'", null);
        /*Cursor c = db.query("SmarterContact", null, query, null, "NUMBER", null, "_id DESC");*/
        //Log.i("CommonUtils", "Cursor count is " + c.getCount());
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String statusString = c.getString(c.getColumnIndex("STATUS"));
            String subStatus1 = c.getString(c.getColumnIndex("SUBSTATUS1"));
            String subStatus2 = c.getString(c.getColumnIndex("SUBSTATUS2"));
            status = statusString;
            if(status == null) {
                status = "";
            }
            if(subStatus1 == null) {
                subStatus1 = "";
            }
            if(subStatus2 == null) {
                subStatus2 = "";
            }
            statusArr[0] = status;
            statusArr[1] = subStatus1;
            statusArr[2] = subStatus2;
            //Log.d("Latest Sales Status", status);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }						/*cursor Closed :::KSN*/
        if (db != null && db.isOpen()) {
            db.close();					/*db Closed :::KSN*/
        }
        return statusArr;
    }

    public static String getStatusFromReminderTable(Context context, String number) {
        //Log.i("CommonUtils", "Number for status is " + number);
        String status = "";
        SQLiteDatabase db;
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        //String selection = "SELECT * FROM remindertbNew where TO1='" + number + "'";
        String selection = " TO1='" + number + "'" + " AND " + "STATUS != 'deleted' ";
        //Cursor c = db.rawQuery(+ "BY TO1 ASC LIMIT 1", null);
        Cursor c = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");

        //Log.i("CommonUtils", "Cursor count is " + c.getCount());
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String statusString = c.getString(c.getColumnIndex("STATUS"));
            status = statusString;
            //Log.d("Latest Sales Status", status);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }						/*cursor Closed :::KSN*/
        if (db != null && db.isOpen()) {
            db.close();					/*db Closed :::KSN*/
        }
        return status;
    }


    public static String[] getsubStatusFromReminderTable(Context context, String number) {
        try {
            //Log.i("CommonUtils", "Number for status is " + number);
            String[] arr = new String[3];
            arr[0] = "";
            arr[1] = "";
            arr[2] = "";
            String status = "", status2 = "";
            SQLiteDatabase db;
            MySql dbHelper = MySql.getInstance(context);
            db = dbHelper.getWritableDatabase();
            // String selection = "TO1='" + number + "'";
            Cursor c = db.rawQuery("SELECT * FROM remindertbNew WHERE TO1= '" + number + "'" + "ORDER BY UPDATED_AT DESC LIMIT 1", null);
            // Cursor c = db.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
            //Log.i("CommonUtils", "Cursor count is " + c.getCount());
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                String substatus1 = c.getString(c.getColumnIndex("SUBSTATUS1"));
                String substatus2 = c.getString(c.getColumnIndex("SUBSTATUS2"));
                int completed = c.getInt(c.getColumnIndex("COMPLETED"));
                String qde = c.getString(c.getColumnIndex("NOTES_IMAGE"));
                if (completed == 1) {
                    if (substatus1 != null && (substatus1.equalsIgnoreCase("PAN APPROVED") || (qde != null && !qde.isEmpty()))) {
                        arr[0] = substatus1;
                        if (substatus2 != null && !substatus2.isEmpty()) {
                            arr[1] = substatus2;
                        }
                        if (qde != null && !qde.isEmpty()) {
                            arr[2] = qde;
                        }
                    } else if (substatus1 != null && substatus1.equalsIgnoreCase("TL APPROVED")) {
                        arr[0] = substatus1;
                        if (substatus2 != null && !substatus2.isEmpty()) {
                            arr[1] = substatus2;
                        }
                        if (qde != null && !qde.isEmpty()) {
                            arr[2] = qde;
                        }
                    } else if (substatus1 != null && (substatus1.equalsIgnoreCase("NOT ELIGIBLE") || substatus1.equalsIgnoreCase("RECENT REJECT") || substatus1.equalsIgnoreCase("ALREADY IN PROCESS") || substatus1.equalsIgnoreCase("WRONG PAN NUMBER"))) {
                        arr[0] = substatus1;
                        if (substatus2 != null && !substatus2.isEmpty()) {
                            arr[1] = substatus2;
                        }
                        if (qde != null && !qde.isEmpty()) {
                            arr[2] = qde;
                        }
                    } else {
                        arr[0] = "";
                        arr[1] = "";
                        arr[2] = "";
                    }
                }
                //Log.d("Latest Sales Status", status);
            }
            if (c != null && !c.isClosed()) {
                c.close();
            }                        /*cursor Closed :::KSN*/
            if (db != null && db.isOpen()) {
                db.close();                    /*db Closed :::KSN*/
            }
            return arr;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static String getNotesFromSmartContactTable(Context context, String number) {
        try {
            String notes = "";
            SQLiteDatabase db;
            MySql dbHelper = MySql.getInstance(context);
            db = dbHelper.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM SmarterContact where NUMBER='" + number + "'", null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                String notesString = c.getString(c.getColumnIndex("NOTES"));
                notes = notesString;
            }
            if (c != null) {
                c.close();
            }                        /*cursor Closed :::KSN*/
            if (db != null && db.isOpen()) {
                db.close();                    /*db Closed :::KSN*/
            }
            return notes;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean calculateNextAlarm(Context context) {
        try {
            //Log.i("CalendarFlow", "****** In calculateNextAlarm method *********");
            MySql dbHelper = new MySql(context, "mydb", null, AppConstants.dBversion);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
            long start = c.getTimeInMillis();
            Date date = new Date();
            //TODO: Change the query to get only todays calendar events.
            //Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew", null);
            //String selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start;
            String selection2 = "START_TIME" + ">=" + start;
            Cursor cursor = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
            //Log.i("CalendarFlow", "Local db count is " + cursor.getCount());
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    long alarmsetto = cursor.getLong(cursor.getColumnIndex("ALARMSETTO"));
                    long dbid = cursor.getLong(cursor.getColumnIndex("_id"));
                    Long startTime = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                    if (alarmsetto == 0) {
                        setAlarm(context, dbid, startTime, "");
                        //Log.d("calculateNextAlarm", "AlarmSetZERO" + startTime);
                    } else {
                        setAlarm(context, dbid, alarmsetto, "");
                        //Log.d("calculateNextAlarm", "AlarmSetNotZERO" + alarmsetto);
                    }
                    cursor.moveToNext();
                }
            }
            if (cursor != null) {
                cursor.close();
            }                             //cursor Closed
            if (db != null && db.isOpen()) {
                db.close();
            }
            if(dbHelper != null){
                dbHelper.close();
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /*public static void calculateNextAlarm(Context context, String responseStatus, long dbid) {
        long[] listOfAlarmSetTo;
        MySql dbHelper = new MySql(context, "mydb", null, AppConstants.dBversion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59);
        long currentTimedate = date.getTime();
        long end = cal.getTimeInMillis();
        Cursor c = db.rawQuery("SELECT * FROM remindertbNew", null);
        if(c.getCount() > 0){
            c.moveToFirst();
            listOfAlarmSetTo = new long[c.getCount()];
            int i = 0;
            while(!c.isAfterLast()) {
                if(c.getLong(c.getColumnIndex("ALARMSETTO")) != 0) {
                    listOfAlarmSetTo[i] = c.getLong(c.getColumnIndex("ALARMSETTO"));
                } else {
                    listOfAlarmSetTo[i] = c.getLong(c.getColumnIndex("START_TIME"));
                }
                i++;
                c.moveToNext();
            }
            Arrays.sort(listOfAlarmSetTo);
            for(int j = 0;j < listOfAlarmSetTo.length;j++){
                if(listOfAlarmSetTo[j] > currentTimedate && listOfAlarmSetTo[j] < end) {
                    long dbId = getdbId(listOfAlarmSetTo[j], db);
                    if (dbId != 0) {
                        // Log.i("CommonUtils","response status in calculate next alarm is "+responseStatus);
                        if(responseStatus != null && responseStatus.equals("pending")) {
                            if(dbid != 0)
                                showNotification(context, dbid);
                            else
                                showNotification(context, dbId);
                            Log.i("CommonUtils","DB id in Alarm set is "+dbId);
                        } else {
                              Log.i("CommonUtils","Calling set alarm");
                            setAlarm(context, dbId, listOfAlarmSetTo[j],"");
                        }
                        break;
                    }
                }
                else {
                    Log.i("CommonUtils", "missed alarm exists in local db");
                }
            }
        }
    }
*/
    public static long getdbId(long alarmsetTo, SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT * FROM remindertbNew where ALARMSETTO='" + alarmsetTo + "'", null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            return c.getLong(c.getColumnIndex("_id"));
        }
        return 0;
    }

    public static void deleteBefore30daysCalendarEvents(Context context) {
        try {
            //TODO: get the calendar events which are 30 days before and delete all those
            //Log.i("CalendarFlow","In delete before 30 days calendar events method");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -30);
            Date date = cal.getTime();
            long startTime = date.getTime();
            //Log.i("CalendarFlow","Start time in delete calendar events is "+startTime);
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String selection2 = "START_TIME" + "<=" + startTime + " AND " + "COMPLETED=1";
            Cursor cursor = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
            if (cursor != null && cursor.getCount() > 0) {
                //Log.i("CalendarFlow","Before 30 days calendar entry exists in local db"+ cursor.getCount());
                cursor.moveToNext();
                while (!cursor.isAfterLast()) {
                    long dbid = cursor.getLong(cursor.getColumnIndex("_id"));
                    db.delete("remindertbNew", "_id='" + dbid + "'", null);
                    cursor.moveToNext();
                }
            }
            if (cursor != null) {
                cursor.close();
            }                            /* cursor Closed :::KSN*/
            if (db != null && db.isOpen()) {
                db.close();            /*db Closed :::KSN*/
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void showNotification(Context context, long dbId) {
        String ns = Context.NOTIFICATION_SERVICE;
        Intent intent1 = new Intent(context, CreateOptionsNotificationService.class);
        intent1.putExtra("id", dbId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }
    }

    /*public static void setAlarm(Context context, long dbId, long alarmSetTO){
        try {
            Log.i("CalendarFlow", "****In Set Alarm*****");
            Intent intent = new Intent(context, Alarm_Receiver.class);
            intent.putExtra("id", dbId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, alarmSetTO, pendingIntent);
        }catch (SecurityException se) {
			//As the security exception occured while setting a trigger, started a service for 10 min before this alarm
			//In service, set trigger for the appointments next to this time. So that exception will not occur as the previous alarm triggers are completed
          	UpdateAlarmTriggerService alarmTriggerService = new UpdateAlarmTriggerService();
			alarmTriggerService.setAlarmTrigger(alarmSetTO - 600000);
        }
    }*/

    public static void setAlarm(Context context, long dbId, long alarmSetTO, String overdue) {
        try {
            Intent intent = new Intent(context, Alarm_Receiver.class);
            intent.putExtra("id", dbId);
            SecureRandom random = new SecureRandom();
            int intentvalue = random.nextInt(32767 - 1) + 1;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intentvalue, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (pendingIntent != null) {
                //Log.d("ALARMUtil", "pendingIntent set");
            }
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(alarmSetTO, pendingIntent);
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, alarmSetTO, pendingIntent);
            } else {
                alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, alarmSetTO, pendingIntent);
            }
        } catch (SecurityException se) {
            //Log.e("SecurityException", se.toString());
            //As the security exception occured while setting a trigger, started a service for 10 min before this alarm
            //In service, set trigger for the appointments next to this time. So that exception will not occur as the previous alarm triggers are completed
            UpdateAlarmTriggerService alarmTriggerService = new UpdateAlarmTriggerService();
            alarmTriggerService.setAlarmTrigger(alarmSetTO - 600000);
        }
    }

    public static void setOnTimeAlarm(Context context, long dbid, long alarmSetvalue) {
        try {
            //Log.i("CalendarFlow", "****In Set Alarm*****");
            if(context != null) {
                Intent intent = new Intent(context, OnTimeReceiver.class);
                intent.putExtra("id", dbid);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC, alarmSetvalue, pendingIntent);
            }
        } catch (SecurityException se) {
            //Log.e("SecurityException", se.toString());
            UpdateAlarmTriggerService alarmTriggerService = new UpdateAlarmTriggerService();
            alarmTriggerService.setAlarmTrigger(alarmSetvalue - 600000);
        }
    }

    public static String formatDateFromOnetoAnother(String date, String givenformat, String resultformat) {
        String result = "";
        SimpleDateFormat sdf;
        SimpleDateFormat sdf1;

        try {
            sdf = new SimpleDateFormat(givenformat);
            sdf1 = new SimpleDateFormat(resultformat);
            result = sdf1.format(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            sdf = null;
            sdf1 = null;
        }

        return result;
    }

    public static GetCalendarEntryInfo getNextAppointmentDetails(Context context) {
        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        SQLiteDatabase db;
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        long start = c.getTimeInMillis();
        String startTime = CommonUtils.getTimeFormatInISO(c.getTime());
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
        long end = c.getTimeInMillis();
        MySql dbHelper = MySql.getInstance(context);
        db = dbHelper.getWritableDatabase();
        String subject = "Reminder", callerNameOrNumber = "";
        long start_time = 0;
        String selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0;
        Cursor cursor = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            subject = cursor.getString(cursor.getColumnIndex("SUBJECT"));
            start_time = cursor.getLong(cursor.getColumnIndex("START_TIME"));
            String toname = cursor.getString(cursor.getColumnIndex("TONAME"));

            if (toname != null && !toname.equals("")) {
                callerNameOrNumber = toname;
            } else {
                String toNumber = cursor.getString(cursor.getColumnIndex("TO1"));
                callerNameOrNumber = toNumber;
            }
        }
        if (cursor != null) {
            cursor.close();
        }							/*cursor Closed :::KSN */
        if (db != null && db.isOpen()) {
            db.close();										/*db Closed ::: KSN */
        }
        //TODO: get the latest appointment details from db
        getCalendarEntryInfo.caller_name = callerNameOrNumber;
        getCalendarEntryInfo.subject = subject;
        String start_Time = getDate(start_time, "dd/MM/yyyy hh:mm a");
        getCalendarEntryInfo.setEvent_start_date(start_Time);
        return getCalendarEntryInfo;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static void saveWorkOrdersToLocalDB(Context context, ArrayList<WorkOrderEntryInfo> workOrderEntryInfoArrayList) {
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        long start_time, end_time;
        if ((workOrderEntryInfoArrayList != null) && (db != null)) {
            for (int i = 0; i < workOrderEntryInfoArrayList.size(); i++) {
                WorkOrderEntryInfo workOrderEntryInfo = workOrderEntryInfoArrayList.get(i);
                ContentValues cv = new ContentValues();
                boolean existing = checkLocalDBforWorkorderID(db, context, workOrderEntryInfo.work_order_id);
                if (!existing) {
                    cv.put("WORK_ORDER_ID", workOrderEntryInfo.work_order_id);
                    cv.put("SUBJECT", workOrderEntryInfo.subject);
                    start_time = stringToMilliSec(workOrderEntryInfo.event_start_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    end_time = stringToMilliSec(workOrderEntryInfo.event_end_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    cv.put("START_TIME", start_time);
                    cv.put("END_TIME", end_time);
                    cv.put("STATUS", workOrderEntryInfo.work_order_status);
                    cv.put("NOTES", workOrderEntryInfo.notes);
                    cv.put("REMINDER_START_DATE", workOrderEntryInfo.reminder_start_date);
                    cv.put("WORK_COMPLETION", workOrderEntryInfo.work_completion);
                    cv.put("CUSTOMER_NAME", workOrderEntryInfo.customer_name);
                    cv.put("CUSTOMER_NUMBER", workOrderEntryInfo.customer_number);
                    cv.put("DESIGNATION", workOrderEntryInfo.designation);
                    cv.put("EMAIL_ID", workOrderEntryInfo.customer_email);
                    cv.put("COMPANY_NAME", workOrderEntryInfo.company_name);
                    cv.put("COMPANY_ADDRESS", workOrderEntryInfo.company_address);
                    cv.put("WEBSITE", workOrderEntryInfo.website);
                    cv.put("TOTAL_PRICE", workOrderEntryInfo.total_price);
                    cv.put("TAX", workOrderEntryInfo.tax);
                    cv.put("ADVANCE_START_DATE", workOrderEntryInfo.advance_date);
                    cv.put("RECEIVED_AMOUNT", workOrderEntryInfo.received_amount);
                    cv.put("BALANCE_DUE_START_DATE", workOrderEntryInfo.balance_due_date);
                    cv.put("AMOUNT_RECEIVABLE", workOrderEntryInfo.amount_receivable);
                    Gson gson = new Gson();
                    String inputString = gson.toJson(workOrderEntryInfo.attachmentsArrayList);
                    cv.put("ATTACHMENTS_ARRAYLIST", inputString);
                    long dbId = db.insert("WorkOrder", null, cv);
                } else {
                    //Log.e("CommonUtils","Work order already exist");
                }
            }
        }
		/*if(db != null && db.isOpen()) {
			db.close(); 					*//* Db Closed :::KSN *//*
		}*/

        if (db != null && db.isOpen()) {
            db.close();
        }
        if(dbHelper != null)
            dbHelper.close();
    }

    public static void saveWorkOrderToLocalDB(Context context, WorkOrderEntryInfo workOrderEntryInfoArrayList) {
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        long start_time, end_time;
        if (workOrderEntryInfoArrayList != null) {
            WorkOrderEntryInfo workOrderEntryInfo = workOrderEntryInfoArrayList;
            ContentValues cv = new ContentValues();
            boolean existing = checkLocalDBforWorkorderID(db, context, workOrderEntryInfo.work_order_id);
            if (existing) {
                db.delete("WorkOrder", "WORK_ORDER_ID='" + workOrderEntryInfo.work_order_id + "'", null);
            }
            cv.put("WORK_ORDER_ID", workOrderEntryInfo.work_order_id);
            cv.put("SUBJECT", workOrderEntryInfo.subject);
            start_time = stringToMilliSec(workOrderEntryInfo.event_start_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            end_time = stringToMilliSec(workOrderEntryInfo.event_end_date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            cv.put("START_TIME", start_time);
            cv.put("END_TIME", end_time);
            cv.put("STATUS", workOrderEntryInfo.work_order_status);
            cv.put("NOTES", workOrderEntryInfo.notes);
            cv.put("REMINDER_START_DATE", workOrderEntryInfo.reminder_start_date);
            cv.put("WORK_COMPLETION", workOrderEntryInfo.work_completion);
            cv.put("CUSTOMER_NAME", workOrderEntryInfo.customer_name);
            cv.put("CUSTOMER_NUMBER", workOrderEntryInfo.customer_number);
            cv.put("DESIGNATION", workOrderEntryInfo.designation);
            cv.put("EMAIL_ID", workOrderEntryInfo.customer_email);
            cv.put("COMPANY_NAME", workOrderEntryInfo.company_name);
            cv.put("COMPANY_ADDRESS", workOrderEntryInfo.company_address);
            cv.put("WEBSITE", workOrderEntryInfo.website);
            cv.put("TOTAL_PRICE", workOrderEntryInfo.total_price);
            cv.put("TAX", workOrderEntryInfo.tax);
            cv.put("ADVANCE_START_DATE", workOrderEntryInfo.advance_date);
            cv.put("RECEIVED_AMOUNT", workOrderEntryInfo.received_amount);
            cv.put("BALANCE_DUE_START_DATE", workOrderEntryInfo.balance_due_date);
            cv.put("AMOUNT_RECEIVABLE", workOrderEntryInfo.amount_receivable);
            if (workOrderEntryInfo.cc != null) {
                cv.put("CCEMAIL", workOrderEntryInfo.cc);
            }
            Gson gson = new Gson();
            String inputString = gson.toJson(workOrderEntryInfo.attachmentsArrayList);
            cv.put("ATTACHMENTS_ARRAYLIST", inputString);

            long dbId = db.insert("WorkOrder", null, cv);
            if (db != null && db.isOpen()) {
                db.close();			/* db closed ::KSN*/
            }
            Toast.makeText(context, "Work order created.", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean checkLocalDBforWorkorderID(SQLiteDatabase db, Context context, String appointmentId) {
        boolean existing = false;
        String selection = "WORK_ORDER_ID='" + appointmentId + "'";
        Cursor cursor = db.query("WorkOrder", null, selection, null, null, null, "START_TIME ASC");
        if (cursor != null && cursor.getCount() > 0) {
            existing = true;
            cursor.moveToFirst();
        }
        if (cursor != null) {
            cursor.close();
        }
        return existing;
    }

    public static Bundle convertHomeScreenMailToBundle(HomeScreenMail mail) {

        Bundle bundle = new Bundle();
        bundle.putString("name", mail.getName());
        bundle.putString("subject", mail.getSubject());
        bundle.putString("startTime", mail.getStart_time());
        bundle.putString("createdTime", mail.getCreated_time());
        bundle.putString("message", mail.getMessage());
        bundle.putString("id", mail.getId());
        bundle.putString("eventtype", mail.getEvent_type());
        bundle.putString("caller_name", mail.getName());
        if (mail.getHtmlMessage() != null)
            bundle.putString("HTMLMessage", mail.getHtmlMessage());
        return bundle;
    }

    public static Bundle convertGroupsUserInfoToBundle(GroupsUserInfo groupsUserInfo) {
        Bundle bundle = new Bundle();
        bundle.putString("name", groupsUserInfo.name);
        bundle.putString("email_id", groupsUserInfo.email);
        bundle.putString("dependent_type", groupsUserInfo.dependent_type);
        bundle.putString("phone_number", groupsUserInfo.phone);
        bundle.putString("request", groupsUserInfo.request);
        return bundle;
    }

    public static GroupsUserInfo convertBundleToGroupsUserInfo(Bundle bundle) {
        GroupsUserInfo groupsUserInfo = new GroupsUserInfo();
        if (bundle.containsKey("name"))
            groupsUserInfo.name = bundle.getString("name");
        if (bundle.containsKey("email_id"))
            groupsUserInfo.email = bundle.getString("email_id");
        if (bundle.containsKey("dependent_type"))
            groupsUserInfo.dependent_type = bundle.getString("dependent_type");
        if (bundle.containsKey("request"))
            groupsUserInfo.request = bundle.getString("request");
        if (bundle.containsKey("phone_number"))
            groupsUserInfo.phone = bundle.getString("phone_number");
        return groupsUserInfo;
    }

    public static long insertToDatabase(Activity activity, OneViewScreenMail smartMail) {
        MySql dbHelper = MySql.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("EMAIL", smartMail.getEmail());
        cv.put("FROM1", smartMail.getFrom());
        cv.put("TO1", smartMail.getTo());
        cv.put("EVENT_TYPE", smartMail.getActivity_type());
        cv.put("URL", smartMail.getUrl());
        cv.put("STARTTIME", smartMail.getStart_time());
        cv.put("ENDTIME", smartMail.getEnd_time());
        cv.put("SUBJECT", smartMail.getSubject());
        cv.put("UNREAD", "false");
        cv.put("MESSAGE", smartMail.getMessage());
        cv.put("LAT", smartMail.getLatitude());
        cv.put("LONG", smartMail.getLongitude());
        cv.put("STATUS", smartMail.getStatus());
        cv.put("CALLER", smartMail.getName());
        cv.put("UPLOAD", 0);
        long dbid = db.insert("mytbl", null, cv);
        db.close();
        // Cursor c=db.query("mytbl", null, null, null, null, null, null);

        return dbid;
    }

    public static void saveTeamdataToLocalDB(Context activity, ArrayList<GroupsUserInfo> groupsUserInfos) {
        try {
            MySql dbHelper = MySql.getInstance(activity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String team_contacts = ApplicationSettings.getPref(AppConstants.TEAM_MEMBER_CONTACTS, "");
            if (db != null) {
                db.delete("TeamMembers", null, null);
                //Log.d("commonUtils", "db removed");
                for (int i = 0; i < groupsUserInfos.size(); i++) {
                    GroupsUserInfo groupsUserInfo = groupsUserInfos.get(i);
                    ContentValues cv = new ContentValues();
                    cv.put("NAME", groupsUserInfo.name);
                    //Log.d("team name in db","Team Api"+groupsUserInfo.name);
                    cv.put("EMAILID", groupsUserInfo.email);
                    //Log.d("EmailId IN DB",groupsUserInfo.email );
                    cv.put("NUMBER", groupsUserInfo.phone);

                    if (groupsUserInfo.role != null) {
                        cv.put("ROLE", groupsUserInfo.role);
                        //Log.d("CommonUtils", "Role"+groupsUserInfo.role);
                    }
                    if (groupsUserInfo.phone != null && !groupsUserInfo.phone.equals("")) {
                        team_contacts = team_contacts + "," + groupsUserInfo.phone;
                    }
                    long dbid = db.insert("TeamMembers", null, cv);
                    //Log.d("team name in db","dbid"+dbid);

                }
                ApplicationSettings.putPref(AppConstants.TEAM_MEMBER_CONTACTS, team_contacts);
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveTeamdataToDB(Context activity, ArrayList<GroupsUserInfo> groupsUserInfos) {
        try {
            MySql dbHelper = MySql.getInstance(activity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String team_contacts = ApplicationSettings.getPref(AppConstants.TEAM_MEMBER_CONTACTS, "");
            if (db != null) {
                //db.delete("TeamMembers", null, null);
                //Log.d("commonUtils", "db removed");
                for (int i = 0; i < groupsUserInfos.size(); i++) {
                    GroupsUserInfo groupsUserInfo = groupsUserInfos.get(i);
                    ContentValues cv = new ContentValues();
                    cv.put("NAME", groupsUserInfo.name);
                    //Log.d("team name in db","Team Api"+groupsUserInfo.name);
                    cv.put("EMAILID", groupsUserInfo.email);
                    //Log.d("EmailId IN DB",groupsUserInfo.email );
                    cv.put("NUMBER", groupsUserInfo.phone);

                    if (groupsUserInfo.role != null) {
                        cv.put("ROLE", groupsUserInfo.role);
                        //Log.d("CommonUtils", "Role"+groupsUserInfo.role);
                    }
                    if (groupsUserInfo.phone != null && !groupsUserInfo.phone.equals("")) {
                        team_contacts = team_contacts + "," + groupsUserInfo.phone;
                    }
                    //long dbid = db.insert("TeamMembers", null, cv);
                    //Log.d("team name in db","dbid"+dbid);

                }
                ApplicationSettings.putPref(AppConstants.TEAM_MEMBER_CONTACTS, team_contacts);
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ArrayList<GroupsUserInfo> getGroupsUserInfoFromDB(Activity activity) {
        try {
            MySql dbHelper = MySql.getInstance(activity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor dbCursor = db.rawQuery("SELECT * FROM TeamMembers", null);
            ArrayList<GroupsUserInfo> groupsUserInfos = new ArrayList<>();
            dbCursor.moveToFirst();
            //Log.d("Common", "Team cursor count"+dbCursor.getCount());
            while (!dbCursor.isAfterLast()) {
                String name = dbCursor.getString(dbCursor.getColumnIndex("NAME"));
                //Log.d("team name in db","Team Api"+name);
                String phone = dbCursor.getString(dbCursor.getColumnIndex("NUMBER"));
                String email_id = dbCursor.getString(dbCursor.getColumnIndex("EMAILID"));
                //Log.d("team name in db","Team Api"+email_id);

                GroupsUserInfo groupsUserInfo = new GroupsUserInfo(email_id, name, phone);
                groupsUserInfos.add(groupsUserInfo);
                dbCursor.moveToNext();
            }
            if (dbCursor != null) {
                dbCursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
            return groupsUserInfos;
        } catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }

    public static boolean permissionsCheck(final Activity context) {
        boolean permissionsGranted = true;

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            try {
                if ((ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
                    permissionsGranted = false;
                    //displayDialog(context);
                    final Dialog dialog = CustomSingleButtonDialog.buildPermissionDialog("Enable required permissions",
                            "In phone settings\nApps -> UEARN -> permissions\nEnable required permissions", context, false);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    SmarterSMBApplication.permissionDialog = dialog;
                    TextView btnYes = dialog.findViewById(R.id.btn_yes);
                    btnYes.setText("Enable Now");
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            final Intent i = new Intent();
                            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            if(context != null)
                                i.setData(Uri.parse("package:" + context.getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            context.startActivity(i);
                        }
                    });
                    dialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return permissionsGranted;
    }

    public static void displayDialog(final Context context) {
        final AlertDialog.Builder dailog = new AlertDialog.Builder(context);
        dailog.setMessage(Html.fromHtml("Please enable ALL" + "<b>" + " permissions " + "</b>" + "for you to use all UEARN features.")).setTitle("Permissions required");
        dailog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                if(context != null)
                    i.setData(Uri.parse("package:" + context.getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(i);
                dialog.dismiss();
            }
        });
        dailog.setCancelable(false);
        dailog.show();
    }

    public static boolean checkRecordPermission(Context context) {
        boolean permissionsGranted = true;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false;
            }

        }
        return permissionsGranted;
    }

    //Find Souce of Lead ::: Added Ny Srinath
    public static boolean getSourceOfLead(Long dbid, Activity activity) {

        MySql dbHelper = MySql.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        if (dbid != 0) {
            cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + dbid + "'", null);
            //Log.d("CursorCount",""+ cursor.getCount());
            //Log.d("CursorCount",""+ dbid);
        }
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String source_Of_Lead = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
            if (source_Of_Lead != null && source_Of_Lead.isEmpty()) {
                //Log.d("SOurceOfLead", source_Of_Lead);
                return true;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
        return false;
    }


    /*Get Fallow-up count*/
    public static HashMap<String, Integer> getUpdatedCount(Activity activity) {
        int unattended_count = 0;
        int upcoming_count = 0;
        int completed_count = 0;
        HashMap<String, Integer> map = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        Long start = c.getTimeInMillis();
        String start_time = CommonUtils.getTimeFormatInISO(c.getTime());
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
        Long end = c.getTimeInMillis();
        String end_time = CommonUtils.getTimeFormatInISO(c.getTime());

        MySql dbHelper = MySql.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Date date = new Date();
        String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'Delete' ";
        if (!db.isOpen()) {
            MySql dbHelpr = MySql.getInstance(activity);
            db = dbHelpr.getWritableDatabase();
        }
        Cursor unattended = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");

        String selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'Delete' ";
        Cursor upcoming = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");

        String selection3 = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'Delete' ";
        Cursor completed = db.query("remindertbNew", null, selection3, null, null, null, "START_TIME ASC");

        if (unattended != null) {
            unattended_count = unattended.getCount();
            //Log.i("New_Appointment","Unattended is "+unattended.getCount());
        }

        if (upcoming != null) {
            upcoming_count = upcoming.getCount();
            //Log.i("New_Appointment","U is "+upcoming.getCount());
        }

        if (completed != null) {
            completed_count = completed.getCount();
            //Log.i("New_Appointment","Unattended is "+completed.getCount());
        }

        map.put("unattended_count", unattended_count);
        map.put("upcoming_count", upcoming_count);
        map.put("completed_count", completed_count);

        return map;
    }

    public static HashMap<String, String> getPhoneNumber(Long dbid, Context context) {
        MySql dbHelpr = MySql.getInstance(context);
        SQLiteDatabase db = dbHelpr.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + dbid + "'", null);
        String number = "", appointmentId = "", status = "", notes = "", order_value = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            number = cursor.getString(cursor.getColumnIndex("TO1"));
            appointmentId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
            status = cursor.getString(cursor.getColumnIndex("STATUS"));
            notes = cursor.getString(cursor.getColumnIndex("NOTES"));
            order_value = cursor.getString(cursor.getColumnIndex("ORDER_POTENTIAL"));

			/*NotificationData.notificationData = true;
			NotificationData.appointment_id = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
			NotificationData.statusString = cursor.getString(cursor.getColumnIndex("STATUS"));
			NotificationData.notes_string = cursor.getString(cursor.getColumnIndex("NOTES"));
			NotificationData.order_value = cursor.getString(cursor.getColumnIndex("ORDER_POTENTIAL"));
			NotificationData.isAppointment = true;*/
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("number", number);
        hashMap.put("status", status);
        hashMap.put("appointmentId", appointmentId);
        hashMap.put("notes", notes);
        hashMap.put("orderValue", order_value);

	/*	Log.d("Utils_numberMessage", number);
		Log.d("Utils_appointmentId", appointmentId);
		Log.d("Utils_status", status);
		Log.d("Utils_notes", notes);
		Log.d("Utils_order_value", order_value);*/
        if (cursor != null) {
            cursor.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelpr != null) {
            dbHelpr.close();
        }
        return hashMap;
    }

    public static void setactivitiesMessage() {
        ApplicationSettings.putPref("Call and SMS settings.", "initial");
        ApplicationSettings.putPref("Create your business card.", "initial");
        ApplicationSettings.putPref("Create your digital brochure.", "initial");
        ApplicationSettings.putPref("Add your team.", "initial");

    }

    public static int getTotalAvtivitiesCount(SQLiteDatabase db, int noOfDays) {
        Calendar cal = Calendar.getInstance();
        Long end = 0L, start = 0L;
        if (noOfDays == 1) {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
            start = cal.getTimeInMillis();
            end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
        } else if (noOfDays == 30) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -31);
            Date date = cal.getTime();
            start = cal.getTimeInMillis();
            String startTime = CommonUtils.getTimeFormatInISO(date);
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, +1);
            Date endDate = cal.getTime();
            end = cal.getTimeInMillis();
        }

        String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'Delete' ";
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

        int total = cursor2.getCount();
        //total = total +activities;
        if (cursor2 != null) {
            cursor2.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        return total;
    }

    public static int getTotalAvtivitiesCount(SQLiteDatabase db, Long start, Long end) {

        int total = 0;
        if (start != 0 && end != 0) {
            String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'Delete' ";
            Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");
            if (cursor2 != null) {
                total = cursor2.getCount();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
        }

        //total = total +activities;
        return total;
    }

    public static int getUncompletedCount(SQLiteDatabase db) {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        Long start = c.getTimeInMillis();
        //Log.d("StartTimeInMillis", start+"");
        Long end = c.getTimeInMillis() + (24 * 60 * 60 * 1000);
        //Log.d("StartTimeInMillis", end+"");

        String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'Delete' ";
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

        int total = cursor2.getCount();
        if (cursor2 != null) {
            cursor2.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        //total = total +activities;
        return total;
    }

    public static int getUncompletedCount(SQLiteDatabase db, int noOfDays) {
        Calendar cal = Calendar.getInstance();
        Long end = 0L, start = 0L;
        if (noOfDays == 1) {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
            start = cal.getTimeInMillis();
            end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
        } else if (noOfDays == 30) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -30);
            Date date = cal.getTime();
            start = cal.getTimeInMillis();

            cal.setTime(new Date());
            end = cal.getTimeInMillis();
            /*String startTime = CommonUtils.getTimeFormatInISO(date);
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, +1);
            Date endDate = cal.getTime();
            end = cal.getTimeInMillis();*/
        }
        //Log.d("StartTimeInMillis", end + "");

        String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

        int total = cursor2.getCount();
        if (cursor2 != null) {
            cursor2.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        //total = total +activities;
        return total;
    }

    public static int getUncompletedCount(SQLiteDatabase db, Long start, Long end) {
        int total = 0;
        if (start != 0 && end != 0) {
            String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
            Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");
            if (cursor2 != null) {
                total = cursor2.getCount();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
        }
        return total;
    }

    public static int getCompletedCount(SQLiteDatabase db) {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        Long start = c.getTimeInMillis();
        //Log.d("StartTimeInMillis", start+"");
        Long end = c.getTimeInMillis() + (24 * 60 * 60 * 1000);
        //Log.d("StartTimeInMillis", end+"");

        String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'Delete' ";
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

        int total = cursor2.getCount();
        if (cursor2 != null) {
            cursor2.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        //total = total +activities;
        return total;
    }

    public static int getCompletedCount(SQLiteDatabase db, int noOfDays) {
        Calendar cal = Calendar.getInstance();
        Long end = 0L, start = 0L;
        if (noOfDays == 1) {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
            start = cal.getTimeInMillis();
            end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
        } else if (noOfDays == 30) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -30);
            Date date = cal.getTime();
            start = cal.getTimeInMillis();
            cal.setTime(new Date());
            end = cal.getTimeInMillis();
        }
        String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'deleted' ";
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

        int total = cursor2.getCount();
        if (cursor2 != null) {
            cursor2.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        return total;
    }

    public static int getCompletedCount(SQLiteDatabase db, Long start, Long end) {

        int total = 0;
        if (start != 0 && end != 0) {
            String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 1 + " AND " + "STATUS != 'deleted' ";
            Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");
            if (cursor2 != null) {
                total = cursor2.getCount();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
        }
        return total;
    }


    public static String getCountryCode() {

        String mobileNo = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
        StringBuffer buffer = new StringBuffer(mobileNo);
        String substring = buffer.substring(0, 2);
        return substring;

    }

    public static int getCountryCodeNumber() {
        String mobileNo = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        int countryCode = 91;
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(mobileNo, "");
            countryCode = numberProto.getCountryCode();
        } catch (NumberParseException e) {
            //System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return countryCode;
    }

    public static String replacewithCountryCode(String no) {
        StringBuilder buffer = new StringBuilder(no);
        //Log.d("BufferString", buffer.substring(0, 2));
        if (getCountryCode().equals("91"))
            if (!buffer.substring(0, 3).equals("+91")) {
                if (no.startsWith("0")) {
                    buffer.deleteCharAt(0);
                    buffer.insert(0, "+91");
                    return buffer.toString();
                } else if (buffer.substring(0, 2).equals("91") && buffer.length() > 10) {
                    // buffer.replace()
                    buffer.delete(0, 2);
                    buffer.insert(0, "+91");
                } else if (!buffer.substring(0, 1).equals("91")) {
                    buffer.insert(0, "+91");
                    return buffer.toString();
                }
            }
        return buffer.toString();
    }

    public static Drawable getDrawable(Activity activity) {
        ImageView ivFabIcon = new ImageView(activity);
        ivFabIcon.setImageResource(R.drawable.ic_arrow_back_white_24px);
        ivFabIcon.setBackgroundResource(R.drawable.circle_background);
        ivFabIcon.setMaxHeight(20);
        Drawable drawable = ivFabIcon.getDrawable();
        return drawable;
    }

    public static Drawable getDrawableMenu(Activity activity) {
        ImageView ivFabIcon = new ImageView(activity);
        ivFabIcon.setImageResource(R.drawable.ic_menu);
        ivFabIcon.setBackgroundResource(R.drawable.circle_background);
        ivFabIcon.setMaxHeight(20);
        Drawable drawable = ivFabIcon.getDrawable();
        return drawable;
    }

    public static String buildE164Number(String countryCode, String phNum) {

        String e164Number = null;
        try {
            //Log.d("CommonUtils","buildE164Number" + countryCode);
            /*  if(phNum.matches("[0-9]+") && phNum.length() > 2) {*/
            if (phNum != null && !(phNum.isEmpty())) {
                PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber phoneNumber = instance.parse(phNum, countryCode);
                e164Number = instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            }

        } catch (NumberParseException e) {
            //Log.d("Caught: " + e.getMessage(), e.toString());
            e.printStackTrace();
        }
        return e164Number;
    }

    public static String getCountryCodes(String phNum) {
        String e164Number = null;
        try {
            PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber = instance.parse(phNum, "");
            //int countryCode = phoneNumber.getCountryCode();

            //e164Number = instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            e164Number = instance.getRegionCodeForNumber(phoneNumber);
            //Log.d("Country Code", e164Number+"");
        } catch (NumberParseException e) {
            //Log.d("Caught: " + e.getMessage(), e.toString());

        }
        return e164Number;
    }

    public static String buildNumber(String numbers) {
		/*String e164Number;
		try {
			PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
			Phonenumber.PhoneNumber phoneNumber = instance.parse(number, "");
			int countryCode = phoneNumber.getCountryCode();
			Log.d("Country Code", countryCode+"");
			e164Number = instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
		} catch (NumberParseException e) {
			Log.d("Caught: " + e.getMessage(), e.toString());
			e164Number = number;
		}
		return e164Number;*/

        boolean isValid;
        Phonenumber.PhoneNumber number;
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();

        String numStr = "";
        for (String r : util.getSupportedRegions()) {
            try {
                // check if it's a possible number
                isValid = util.isPossibleNumber(numbers, r);
                if (isValid) {
                    number = util.parse(numbers, r);

                    // check if it's a valid number for the given region
                    isValid = util.isValidNumberForRegion(number, r);
                    if (isValid) {
                        numStr = "+" + number.getCountryCode() + "" + number.getNationalNumber();
                    }
                }
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        }
        return numStr;
    }

    public static int getZeroFollowupCount(SQLiteDatabase db, int noOfDays) {
        int total = 0;

        Calendar cal = Calendar.getInstance();
        Long end = 0L, start = 0L;
        if (noOfDays == 1) {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
            start = cal.getTimeInMillis();
            end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
        } else if (noOfDays == 30) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -31);
            Date date = cal.getTime();
            start = cal.getTimeInMillis();
            String startTime = CommonUtils.getTimeFormatInISO(date);
            cal.setTime(new Date());
            end = cal.getTimeInMillis();
        }

        String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT = " + " '0' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' " + "AND" + " COMPLETED " + "!=" + 1;
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

        /*String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' " + " AND " + "FLP_COUNT = " + " '0' ";
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");
*/
        if (cursor2.getCount() > 0) {
            total = cursor2.getCount();
        }
        if (cursor2 != null) {
            cursor2.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        return total;
    }

    public static int getZeroFollowupCount(SQLiteDatabase db, Long start, Long end) {
        int total = 0;
        if (start != 0 && end != 0) {
            String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT = " + " '0' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' " + "AND" + " COMPLETED " + "!=" + 1;
            Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

            if (cursor2.getCount() > 0) {
                total = cursor2.getCount();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
        }
        return total;
    }

    public static int getOneFollowedupCount(SQLiteDatabase db, int noOfDays) {
        int total = 0;

        Calendar cal = Calendar.getInstance();
        Long end = 0L, start = 0L;
        if (noOfDays == 1) {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
            start = cal.getTimeInMillis();
            end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
        } else if (noOfDays == 30) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -31);
            Date date = cal.getTime();
            start = cal.getTimeInMillis();
            String startTime = CommonUtils.getTimeFormatInISO(date);
           /* cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, +1);
            Date endDate = cal.getTime();
            end = cal.getTimeInMillis();*/
            cal.setTime(new Date());
            end = cal.getTimeInMillis();
        }
        //Log.d("StartTimeInMillis", start + "");
        //Log.d("StartTimeInMillis", end + "");

        String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT = " + " '0' " + " AND " + "COMPLETED" + "=" + 1;
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

       /* String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COUNTER = " + " '1' ";
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");*/

        if (cursor2.getCount() > 0) {
            total = cursor2.getCount();
        }
        if (cursor2 != null) {
            cursor2.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        return total;
    }

    public static int getOneFollowedupCount(SQLiteDatabase db, Long start, Long end) {
        int total = 0;
        if (start != 0 && end != 0) {
            //String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COUNTER = " + " '1' ";
            String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT = " + " '0' " + " AND " + "COMPLETED" + "=" + 1;
            Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

            if (cursor2.getCount() > 0) {
                total = cursor2.getCount();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return total;
    }


    public static int getTotalAssignedCount(SQLiteDatabase db, int noOfDays) {
        int total = 0, completed = 0;

        Calendar cal = Calendar.getInstance();
        Long end = 0L, start = 0L;
        if (noOfDays == 1) {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
            start = cal.getTimeInMillis();
            end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
        } else if (noOfDays == 30) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -31);
            Date date = cal.getTime();
            start = cal.getTimeInMillis();
            cal.setTime(new Date());
            end = cal.getTimeInMillis();
        }

        String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' " + "AND" + " FLP_COUNT" + "=" + " '0' ";
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

        if (cursor2.getCount() > 0) {
            total = cursor2.getCount();
        }
        if (cursor2 != null) {
            cursor2.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }

        return total;
    }

    public static int getTotalAssignedCount(SQLiteDatabase db, Long start, Long end) {

        int total = 0;
        if (start != 0 && end != 0) {
            String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' " + "AND" + " FLP_COUNT" + "=" + " '0' ";
            Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

            if (cursor2.getCount() > 0) {
                total = cursor2.getCount();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return total;
    }

    public static int getTotalunAssignedCount(SQLiteDatabase db, int noOfDays) {
        int total = 0;

        Calendar cal = Calendar.getInstance();
        Long end = 0L, start = 0L;
        if (noOfDays == 1) {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
            start = cal.getTimeInMillis();
            end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
        } else if (noOfDays == 30) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -31);
            Date date = cal.getTime();
            start = cal.getTimeInMillis();
           /* String startTime = CommonUtils.getTimeFormatInISO(date);
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, +1);
            Date endDate = cal.getTime();
            end = cal.getTimeInMillis();*/
            cal.setTime(new Date());
            end = cal.getTimeInMillis();
        }
        //Log.d("StartTimeInMillis", start + "");
        //Log.d("StartTimeInMillis", end + "");

        String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'unassigned' ";
        Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

        if (cursor2.getCount() > 0) {
            total = cursor2.getCount();
        }
        if (cursor2 != null) {
            cursor2.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        return total;
    }

    public static int getTotalunAssignedCount(SQLiteDatabase db, Long start, Long end) {

        int total = 0;
        if (start != 0 && end != 0) {
            String compltedselection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'unassigned' ";
            Cursor cursor2 = db.query("remindertbNew", null, compltedselection, null, null, null, "START_TIME ASC");

            if (cursor2 != null && cursor2.getCount() > 0) {
                total = cursor2.getCount();
            }
            if (cursor2 != null) {
                cursor2.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return total;
    }

    public static void deleteInternalFiles() {
        DeleteExternalStorageCaller deleteExternalStorageCaller = new DeleteExternalStorageCaller();
        deleteExternalStorageCaller.execute();
    }

    public static class DeleteExternalStorageCaller extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            File externalStorageDirectory = Environment.getExternalStorageDirectory();

            if(externalStorageDirectory != null) {
                File dir = new File(externalStorageDirectory + "/callrecorder/");
                // File dir = new File(Environment.getExternalStorageDirectory() .toString(), "/Android/data/" + .getPackageName()+ "/callrecorder/");
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }
            }

            /*File dir2 = new File(Environment.getExternalStorageDirectory() + "/TextScripts/");
            // File dir = new File(Environment.getExternalStorageDirectory() .toString(), "/Android/data/" + .getPackageName()+ "/callrecorder/");
            if (dir2.isDirectory()) {
                String[] children = dir2.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir2, children[i]).delete();
                }
            }*/


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public static void removeAllOldCalendars(Context context) {
        //MySql dbHelper = new MySql(getContext(), "mydb", null, AppConstants.dBversion);
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("remindertbNew", null, null);
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public static void googleAccountsCall(String user_id, Context context) {
        if (context != null) {
            if (CommonUtils.isNetworkAvailable(context)) {
                new APIProvider.Get_GmailAccounts(user_id, 1, new API_Response_Listener<GoogleAccountsList>() {
                    @Override
                    public void onComplete(GoogleAccountsList data, long request_code, int failure_code) {
                        if (failure_code == APIAdapter.NO_FAILURE) {
                            //todo; put a null pointer exception check
                            if (data != null) {
                                data.doSave();
                                SmarterSMBApplication.googleAccountsList = data;
                            }

                        }
                    }
                }).call();
            }
        }
    }


    /**
     * Get user Sales Status list
     *
     * @param id 'Using the user ID fetch the details
     */
    public static void getStatusFromServer(String id, Context context) {
        if (context != null) {
            if (CommonUtils.isNetworkAvailable(context)) {
                new APIProvider.Get_Sales_Status(id, 22, new API_Response_Listener<SalesStageInfo>() {
                    @Override
                    public void onComplete(SalesStageInfo data, long request_code, int failure_code) {
                        if (data != null) {
                            data.dosave();
                            SmarterSMBApplication.salesStageInfo = data;
                        }
                    }
                }).call();
            }
        }
    }

    /**
     * Get User Team List
     *
     * @param user_id 'Fetch the users logged in account details
     */
    public static void getGroupsOfUsers(String user_id, Context context) {
        if (context != null) {
            if (CommonUtils.isNetworkAvailable(context)) {
                new APIProvider.Get_GroupsUser(user_id, 1, null, null, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                    @Override
                    public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                        if (data != null) {
                            for (int i = 0; i < data.size(); i++) {
                                String request = data.get(i).request;
                                if ((request != null) && request.equals("invited")) {
                                    Intent intent = new Intent(getApplicationContext(), AcceptOrRejectNotificationService.class);
                                    Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(data.get(i));
                                    intent.putExtra("groupsUserInfoBundle", bundle);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        getApplicationContext().startForegroundService(intent);
                                    } else {
                                        getApplicationContext().startService(intent);
                                    }
                                }
                            }
                        }
                    }
                }).call();
            }
        }
    }


    /**
     * Get User Team List
     *
     * @param id, context
     */
    public static void getTeamMembersCall(String id, final Context context) {
        if (CommonUtils.isNetworkAvailable(context)) {
            new APIProvider.Get_All_Team_Members(id, 535, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                @Override
                public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                    if (data != null) {
                        CommonUtils.saveTeamdataToLocalDB(context, data);
                        for (int i = 0; i < data.size(); i++) {
                            String request = data.get(i).request;
                            if ((request != null) && request.equals("invited")) {
                                Intent intent = new Intent(context, AcceptOrRejectNotificationService.class);
                                Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(data.get(i));
                                intent.putExtra("groupsUserInfoBundle", bundle);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(intent);
                                } else {
                                    context.startService(intent);
                                }
                            }
                        }
                    }
                }
            }).call();
        }
    }

    public static void buildNotification(Context context, int followUpCount, int todoCount) {

        try {
            Intent mainIntent = new Intent(context, UearnHome.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
            String userName = "";
            if(ApplicationSettings.containsPref(AppConstants.USERINFO_NAME) && (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null)) {
                userName = ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
            }

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            int total = followUpCount+todoCount;

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setContentTitle("Follow-ups")
                    .setSound(defaultSoundUri)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Hi "+userName+",\nCall up your remaining "+String.valueOf(total) + " Customers.\nCall them and achieve 100% completion to your activities for the day"))
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.small_logo);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void buildmissedNotification(Context context, int followUpCount, int todoCount) {
        try {
            Intent mainIntent = new Intent(context, UearnHome.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
            String userName = "";
            if(ApplicationSettings.containsPref(AppConstants.USERINFO_NAME) && (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null)) {
                userName = ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
            }

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setContentTitle("Missed Follow-ups")
                    .setSound(defaultSoundUri)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Hi "+userName+", You have missed "+String.valueOf(followUpCount) + " Follow-ups"))
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.small_logo);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Long getNextAppointmentData(Context context, String number, Long current_startTime) {

        SQLiteDatabase db;
        MySql dbHelpr = MySql.getInstance(context);
        db = dbHelpr.getWritableDatabase();
        Cursor cursor = null;
        String nextAppointmentdbId = "";
        long nextFollowupdbId = 0L;

        try {
            String callerNameOrNumber = number;
            String subjct = "meating";
            Long start_time = 0L;

            Date date = new Date();
            String selection = "START_TIME" + ">" + date.getTime() + " AND " + "COMPLETED='" + 0 + "'";
            cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String customer_number = cursor.getString(cursor.getColumnIndex("TO1"));
                    if (PhoneNumberUtils.compare(customer_number, number)) {
                        start_time = cursor.getLong(cursor.getColumnIndex("START_TIME"));
                        if (start_time > current_startTime) {
                            nextFollowupdbId = cursor.getLong(cursor.getColumnIndex("_id"));
                            nextAppointmentdbId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
                            return nextFollowupdbId;
                        }
                    }
                    cursor.moveToNext();
                }
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !(cursor.isClosed())) {
                cursor.close();
                if (db != null && db.isOpen()) {
                    db.close();
                }
                if (dbHelpr != null) {
                    dbHelpr.close();
                }
            }
        }

        return nextFollowupdbId;
    }

    public static void createOrUpdateFollowup(Context context) {
        UpdateYesterDayFollowups updateYesterDayFollowups = new UpdateYesterDayFollowups(context);
        updateYesterDayFollowups.execute();
    }

    public static class UpdateYesterDayFollowups extends AsyncTask<Void, Void, Void> {
        Context context;

        public UpdateYesterDayFollowups(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String userEmail = "";
            if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
            }

            Calendar cur_cal = Calendar.getInstance();
            cur_cal.setTimeInMillis(System.currentTimeMillis());

            Calendar cal = Calendar.getInstance();
            cal.set(cur_cal.get(Calendar.YEAR), cur_cal.get(Calendar.MONTH), cur_cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            Long end = cal.getTimeInMillis();
            Long start = end - (24 * 60 * 60 * 1000);
            //Log.d("Bootable_SERVICE",""+start);
            //Log.d("Bootable_SERVICE",""+end);

            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Date date = new Date();

            String selection = "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
            Cursor dbCursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
            if (dbCursor.getCount() > 0) {
                //Log.d("Bootable", "insideCursor");
                dbCursor.moveToFirst();
                while (!dbCursor.isAfterLast()) {
                    int id = dbCursor.getInt(dbCursor.getColumnIndex("_id"));
                    long startTime = dbCursor.getLong(dbCursor.getColumnIndex("START_TIME"));
                    long endTime = dbCursor.getLong(dbCursor.getColumnIndex("END_TIME"));

                    //TODO: starttime has to change to next day
                    ContentValues contentValues = new ContentValues();

                    contentValues.put("LOCATION", dbCursor.getString(dbCursor.getColumnIndex("LOCATION")));
                    contentValues.put("SUBJECT", dbCursor.getString(dbCursor.getColumnIndex("SUBJECT")));
                    contentValues.put("NOTES", dbCursor.getString(dbCursor.getColumnIndex("NOTES")));
                    contentValues.put("COMPANY_NAME", dbCursor.getString(dbCursor.getColumnIndex("COMPANY_NAME")));
                    contentValues.put("DESIGNATION", dbCursor.getString(dbCursor.getColumnIndex("DESIGNATION")));

                    if (dbCursor.getString(dbCursor.getColumnIndex("CALLREC_URL")) != null) {
                        contentValues.put("CALLREC_URL", dbCursor.getString(dbCursor.getColumnIndex("CALLREC_URL")));
                    }
                    if (dbCursor.getString(dbCursor.getColumnIndex("TONAME")) != null) {
                        contentValues.put("TONAME", dbCursor.getString(dbCursor.getColumnIndex("TONAME")));
                    }
                    contentValues.put("TO1", dbCursor.getString(dbCursor.getColumnIndex("TO1")));
                    String number = " ";
                    number = dbCursor.getString(dbCursor.getColumnIndex("TO1"));
                    contentValues.put("WEBSITE", dbCursor.getString(dbCursor.getColumnIndex("WEBSITE")));
                    contentValues.put("STATUS", dbCursor.getString(dbCursor.getColumnIndex("STATUS")));
                    contentValues.put("EMAILID", dbCursor.getString(dbCursor.getColumnIndex("EMAILID")));
                    if (dbCursor.getString(dbCursor.getColumnIndex("CMAIL_MAILID")) != null) {
                        contentValues.put("CMAIL_MAILID", dbCursor.getString(dbCursor.getColumnIndex("CMAIL_MAILID")));
                    }
                    contentValues.put("LAT", dbCursor.getString(dbCursor.getColumnIndex("LAT")));
                    contentValues.put("LONG", dbCursor.getString(dbCursor.getColumnIndex("LONG")));
                    contentValues.put("ORDER_POTENTIAL", dbCursor.getString(dbCursor.getColumnIndex("ORDER_POTENTIAL")));
                    contentValues.put("LEAD_SOURCE", dbCursor.getString(dbCursor.getColumnIndex("LEAD_SOURCE")));
                    contentValues.put("PRODUCT_TYPE", dbCursor.getString(dbCursor.getColumnIndex("PRODUCT_TYPE")));
                    contentValues.put("COMPANY_ADDRESS", dbCursor.getString(dbCursor.getColumnIndex("COMPANY_ADDRESS")));
                    contentValues.put("UPLOAD_STATUS", 0);
                    contentValues.put("ALARMSETTO", 10);
                    if (userEmail != null && !(userEmail.isEmpty())) {
                        contentValues.put("ASSIGN_TO", userEmail);
                        contentValues.put("CCEMAIL", userEmail);
                    }
                    int flpvalue = 0;
                    if (dbCursor.getString(dbCursor.getColumnIndex("FLP_COUNT")) != null && !(dbCursor.getString(dbCursor.getColumnIndex("FLP_COUNT"))).isEmpty()) {
                        flpvalue = Integer.parseInt(dbCursor.getString(dbCursor.getColumnIndex("FLP_COUNT")));
                        contentValues.put("FLP_COUNT", flpvalue + 1);
                    } else {
                        contentValues.put("FLP_COUNT", 1);
                    }

                    String start_time1 = "";
                    String end_time1 = "";
                    Long updatedStart = 0L, updatedENd = 0L;
                    if (flpvalue == 0) {
                        flpvalue = flpvalue + 1;
                        start_time1 = getStartTime(startTime, 1);
                        end_time1 = getEndTime(endTime, 1);
                        updatedStart = startTime + (1000 * 60 * 60 * 24 * 1);
                        updatedENd = endTime + (1000 * 60 * 60 * 24 * 1);
                        //Log.d("BootableService", "startTime"+start_time1);
                    } else if (flpvalue == 1) {

                        flpvalue = flpvalue + 1;
                        start_time1 = getStartTime(startTime, 1);
                        end_time1 = getEndTime(endTime, 1);
                        updatedStart = startTime + (1000 * 60 * 60 * 24 * 1);
                        updatedENd = endTime + (1000 * 60 * 60 * 24 * 1);
                        //Log.d("BootableService", "startTime"+start_time1);
                    } else if (flpvalue == 2) {
                        flpvalue = flpvalue + 1;
                        start_time1 = getStartTime(startTime, 4);
                        end_time1 = getEndTime(endTime, 4);
                        updatedStart = startTime + (1000 * 60 * 60 * 24 * 4);
                        updatedENd = endTime + (1000 * 60 * 60 * 24 * 4);
                        //Log.d("BootableService", "startTime"+start_time1);
                    } else {

                        flpvalue = flpvalue + 1;
                        start_time1 = getStartTime(startTime, 2);
                        end_time1 = getEndTime(endTime, 2);
                        updatedStart = startTime + (1000 * 60 * 60 * 24 * 2);
                        updatedENd = endTime + (1000 * 60 * 60 * 24 * 2);
                        //Log.d("BootableService", "startTime"+start_time1);
                    }
               /* if(start_time1 != null) {
                    contentValues.put("START_TIME", updatedStart);
                } if(end_time1 != null) {

                }*/
                    contentValues.put("RESPONSE_STATUS", "accepted");
                    /*contentValues.put();*/
                    //TODO: set appointmenttype as update if next appointment id is there
                    Long appointmentId = 0L;
                    if (number != null && !(number.isEmpty())) {
                        appointmentId = CommonUtils.getNextAppointmentData(context, number, startTime);
                    }
                    if (appointmentId != 0) {
                        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + appointmentId + "'", null);
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            contentValues.put("APPOINTMENT_ID", cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID")));
                            contentValues.put("APPOINTMENT_TYPE", "update_appointment");
                            contentValues.put("START_TIME", cursor.getLong(cursor.getColumnIndex("START_TIME")));
                            contentValues.put("END_TIME", cursor.getLong(cursor.getColumnIndex("END_TIME")));
                            db.update("remindertbNew", contentValues, "_id=" + appointmentId, null);
                        }
                    } else {
                        contentValues.put("START_TIME", updatedStart);
                        contentValues.put("END_TIME", updatedENd);
                        long dbId = db.insert("remindertbNew", null, contentValues);
                    }
                    markAsMissed(db, contentValues, dbCursor, id);
                    dbCursor.moveToNext();
                }

                if (dbCursor != null && !(dbCursor.isClosed())) {
                    dbCursor.close();
                }

                if (db != null && db.isOpen()) {
                    db.close();
                }
                if(dbHelper != null)
                    dbHelper.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent1 = new Intent(context, ReuploadService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent1);
            } else {
                context.startService(intent1);
            }
            super.onPostExecute(aVoid);
        }
    }

    private static void markAsMissed(SQLiteDatabase db, ContentValues contentValues, Cursor cursor, int id) {
        contentValues.put("RESPONSE_STATUS", "missed");
        contentValues.put("APPOINTMENT_TYPE", "update_appointment");
        contentValues.put("APPOINTMENT_ID", cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID")));
        db.update("remindertbNew", contentValues, "_id=" + id, null);
    }

    private static String getStartTime(Long startTime, int value) {
        startTime = startTime + (1000 * 60 * 60 * 24 * value);
        Date date = new Date();
        date.setTime(startTime);
        String start_time = CommonUtils.getTimeFormatInISO(date);
        //Log.d("STARTTIMEISO", start_time);
        return start_time;
    }

    private static String getEndTime(Long endTime, int value) {
        endTime = endTime + (1000 * 60 * 60 * 24 * value);
        Date date = new Date();
        date.setTime(endTime);
        String end_time = CommonUtils.getTimeFormatInISO(date);
        //Log.d("ENDTIMEISO", end_time);
        return end_time;
    }

//    public static int getOverdueCount(String flpcount, Context context, Long start, Long end) {
//        Cursor cursor1;
//        MySql dbHelper = new MySql(context, "mydb", null, AppConstants.dBversion);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Date date = new Date();
//
//        if (flpcount.equalsIgnoreCase("0")) {
//            String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + SmarterMainActivity.getstartTime() + " AND " + "START_TIME" + "<=" + SmarterMainActivity.getendTime() + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT = " + 0 + " AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
//            //String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
//            cursor1 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
//
//            int flpZeroCount = 0;
//            if (cursor1 != null) {
//                if (cursor1.moveToFirst()) {
//                    do {
//                        if (cursor1.getInt(cursor1.getColumnIndex("FLP_COUNT")) == 0) {
//                            flpZeroCount++;
//                        }
//                    } while (cursor1.moveToNext());
//                }
//                return flpZeroCount;
//            }
//        } else {
//            String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + " AND " + "FLP_COUNT != " + " '0' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
//            cursor1 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
//            int flpOneCount = 0;
//            if (cursor1 != null) {
//                if (cursor1.moveToFirst()) {
//                    do {
//                        if (cursor1.getInt(cursor1.getColumnIndex("FLP_COUNT")) != 0) {
//                            flpOneCount++;
//                        }
//                    } while (cursor1.moveToNext());
//                }
//                return flpOneCount;
//            }
//        }
//
//        if (cursor1 != null && cursor1.getCount() > 0) {
//            return cursor1.getCount();
//        } else {
//            return 0;
//        }
//
//    }

    public static void buildReportAndSend(Context context, String info) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        String timeStamp = formatter.format(date);

        String userid = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        String phoneNumber = ApplicationSettings.getPref(AppConstants.PRIMARY_CONTACT, "");
        String userMail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        String osVersion = System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        String apiLevel = android.os.Build.VERSION.RELEASE + "(" + android.os.Build.VERSION.SDK_INT + ")";
        String device = android.os.Build.DEVICE;
        String deviceModel = android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

        String message = "<br/>Call recording problem<br/><br/>User ID: " + userid
                + "<br/>User Email: " + userMail + "<br/>Contact info: " + phoneNumber
                + "<br/>Device: " + device + "<br/>"
                + "<br/>OS version: " + osVersion + "<br/>API Level: " + apiLevel
                + "<br/>Device Model: " + deviceModel + "<br/>Time Stamp: " + timeStamp + "<br/>";

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED)) {
                message += "<br/><span style='color:red'>Read contacts permission disabled</span>";
            } else {
                message += "<br/><span style='color:green'>Read contacts permission enabled</span>";
            }

            if ((ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                message += "<br/><span style='color:red'>External storage permission disabled</span>";
            } else {
                message += "<br/><span style='color:green'>External storage permission enabled</span>";
            }

            if ((ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                message += "<br/>Read External storage permission disabled</span>";
            } else {
                message += "<br/><span style='color:green'>Read external storage permission enabled</span>";
            }

            if ((ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
                message += "<br/><span style='color:red'>Call phone permission disabled</span>";
            } else {
                message += "<br/><span style='color:green'>Call phone permission enabled</span>";
            }

            if ((ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                message += "<br/><span style='color:red'>Access fine location permission disabled</span>";
            } else {
                message += "<br/><span style='color:green'>Access fine location permission enabled</span>";
            }

            if ((ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
                message += "<br/><span style='color:red'>Record audio permission disabled</span>";
            } else {
                message += "<br/><span style='color:green'>Record audio permission enabled</span>";
            }
        }
        int version_code = CommonUtils.getVersionCode(context);
        message += "<br/><br/>" + info;
        message += "<br/>Current Activity: "+ SmarterSMBApplication.currentActivity;
        message += "<br/>App Version: "+version_code;
        //Log.d("Device details", message);
        ServiceApplicationUsage.callErrorLog(message);
    }

    public static String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        String startTime = CommonUtils.getTimeFormatInISO(calendar.getTime());
        String startTime2 = CommonUtils.getDate(startTime);
        return startTime2;
    }

    public static String getYesterDayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String startTime = CommonUtils.getTimeFormatInISO(calendar.getTime());
        String startTime2 = CommonUtils.getDate(startTime);
        return startTime2;
    }

    public static String getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1);
        String startTime = CommonUtils.getTimeFormatInISO(calendar.getTime());
        String startTime2 = CommonUtils.getDate(startTime);
        return startTime2;
    }

    public static String getAllDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);
        String startTime = CommonUtils.getTimeFormatInISO(calendar.getTime());
        String startTime2 = CommonUtils.getDate(startTime);
        return startTime2;
    }

    public static void checkToken() {

        Long current_time = System.currentTimeMillis();
        Long seconds = TimeUnit.MILLISECONDS
                .toSeconds(current_time);
        Long tokenExpiry = ApplicationSettings.getPref("TokenValidity", 0L);
        //Log.d("TokenExpiry","" + (tokenExpiry));

        if ((tokenExpiry - seconds) > 480) {
            //Log.d("TokenExpiry","" + (tokenExpiry-seconds));
        } else {
            if (authenticationCheck) {
                ServerAuthHelper.accessTokenCall(1);
            }
            Long refreshTokenvalidity = ApplicationSettings.getPref("refreshTokenValidity", 0L);
            //Log.d("TokenExpiry","" + (refreshTokenvalidity));
            if ((refreshTokenvalidity - seconds) > 480) {
                authenticationCheck = false;
            } else {
                authenticationCheck = true;
                ServerAuthHelper.accessTokenCall(0);
            }
        }
    }

    public static boolean checkForAmeyoNumber(String phoneNumber) {
        if(phoneNumber != null && !phoneNumber.isEmpty()) {
            phoneNumber = phoneNumber.replace(" ", "").replace("-", "");
            String[] ameyoNumber = loadAmeyoNumbers();
            //Log.i("SmarterBIZ","Ameyo number is "+ameyoNumber);
            if (ameyoNumber != null) {
                for (int i = 0; i < ameyoNumber.length; i++) {
                    if (PhoneNumberUtils.compare(phoneNumber, ameyoNumber[i])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String[] loadAmeyoNumbers() {
        String contacts = ApplicationSettings.getPref(AppConstants.AMEYO_NUMBERS, null);
        //Log.i("SmsComposeActivity", "Countacts are " + contacts);
//        if (contacts != null) {
//            int count = PrivateContactsActivity.countOccurrences(contacts, ',');
//            String[] splittedContacts = new String[count + 1];
//            splittedContacts = contacts.split(",");
//            return splittedContacts;
//        }
        return null;
    }

    public static String getContactName(String phoneNumber, Context context) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            return contactName;
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public static int getVersionCode(Context context) {
        int version_code = 0;
        try {
            final String appPackageName = context.getPackageName();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(appPackageName, 0);
            version_code = pInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version_code;
    }

    public static String checkNo(String number) {
        String callernumber = number;
        String numberWithCode = null;

        if (callernumber != null &&!(callernumber.isEmpty())) {
            if (buildE164Number("", callernumber) != null) {
                numberWithCode = buildE164Number("", callernumber);
                return numberWithCode;
            } else {
                if ((ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "")) != null) {
                    String customerno = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                    if (!customerno.startsWith("+")) {
                        customerno = "+" + customerno;
                    }

                    if (buildE164Number("", customerno) != null) {
                        String e164Region;
                        try {
                            PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
                            Phonenumber.PhoneNumber phoneNumber = instance.parse(customerno, "");
                            e164Region = instance.getRegionCodeForNumber(phoneNumber);
                            //Log.d("Country Code", e164Region+"");
                            if (e164Region != null) {
                                numberWithCode = buildE164Number(e164Region, callernumber);
                            }
                        } catch (NumberParseException e) {
                            //Log.d("Caught: " + e.getMessage(), e.toString());
                        }
                    }
                    if (numberWithCode != null) {
                        //* callerNumber.setText(numberWithCode);
                        return numberWithCode;
                    }
                }
            }
        }
        return numberWithCode;
    }

    public static void showCallRecordings(final Context context, String number) {
        if(number != null) {
            number = number.replace("+", "%2b");
        }
        new APIProvider.GetCmailmailsCallRecording(number, 232, null, "loading", new API_Response_Listener<ArrayList<String[]>>() {
            @Override
            public void onComplete(ArrayList<String[]> data, long request_code, int failure_code) {
                if(data != null && !(data.isEmpty())) {
                    if(context != null) {
                        final RecordingAdapter recordingAdapter = new RecordingAdapter(context, data);
                        final Dialog dailog = new Dialog(context, R.style.Dialog_Theme);
                        dailog.setContentView(R.layout.audio_dailog);
                        //dailog.setTitle("Call Recordings");
                       /* int dividerId = dailog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                        View divider = dailog.findViewById(dividerId);
                        divider.setBackgroundColor(ContextCompat.getColor(context, R.color.black));

                        int textViewId = dailog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
                        TextView tv = (TextView) dailog.findViewById(textViewId);
                        tv.setTextColor(ContextCompat.getColor(context, R.color.black));
                        */
                        dailog.show();

                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        RecyclerView recyclerView = dailog.findViewById(R.id.recycleDialog);
                        TextView close = dailog.findViewById(R.id.close);
                        close.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //closeAudio();
                                /*if(recordingAdapter != null) {
                                    recordingAdapter.closeAudio();
                                }*/
                                if(ApplicationSettings.mediaPlayer != null) {
                                    ApplicationSettings.mediaPlayer.pause();
                                    ApplicationSettings.mediaPlayer.stop();
                                    ApplicationSettings.mediaPlayer.release();
                                    ApplicationSettings.mediaPlayer = null;

                                }
                                dailog.dismiss();
                            }
                        });

                        dailog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                //closeAudio();
                               /* if(recordingAdapter != null) {
                                    recordingAdapter.closeAudio();
                                }*/
                                if(ApplicationSettings.mediaPlayer != null) {
                                    ApplicationSettings.mediaPlayer.pause();
                                    ApplicationSettings.mediaPlayer.stop();
                                    ApplicationSettings.mediaPlayer.release();
                                    ApplicationSettings.mediaPlayer = null;
                                }
                            }
                        });

                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(recordingAdapter);
                    }

                } else {
                    if(context != null) {
                        Toast.makeText(context, "No Recording found as this was RNR customer.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).call();
    }

    public interface StopAudio {
        void stopAudio(boolean value);
    }

    private static void  closeAudio() {
        try {
           /* if (CallRecordsView.mediaPlayer != null && CallRecordsView.mediaPlayer.isPlaying()) {
                CallRecordsView.mediaPlayer.pause();
                CallRecordsView.mediaPlayer.seekTo(0);
                CallRecordsView.mediaPlayer.stop();
                CallRecordsView.mediaPlayer.release();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCallRecordings(final Context context, String number, final AutoDailerActivity.PlayListener playListener) {
        if(number != null) {
            number = number.replace("+", "%2b");
        }
        new APIProvider.GetCmailmailsCallRecording(number, 232, null, "loading", new API_Response_Listener<ArrayList<String[]>>() {
            @Override
            public void onComplete(ArrayList<String[]> data, long request_code, int failure_code) {
                if(data != null && !(data.isEmpty())) {
                    //Log.d("CallRecordings", data.toString());
                    final RecordingAdapter recordingAdapter = new RecordingAdapter(context, data);

                    final Dialog dailog = new Dialog(context);
                    dailog.setContentView(R.layout.audio_dailog);
                    dailog.setTitle("Call Recordings");
                    dailog.show();

                    final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    RecyclerView recyclerView = dailog.findViewById(R.id.recycleDialog);
                    TextView close = dailog.findViewById(R.id.close);
                    close.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dailog.dismiss();
                            closeAudio();
                            playListener.resumeActivity(true);

                        }
                    });

                    dailog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            closeAudio();
                            playListener.resumeActivity(true);
                        }
                    });
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(recordingAdapter);
                } else {
                    playListener.resumeActivity(true);
                    Toast.makeText(context,"No Recording found as this was RNR customer.", Toast.LENGTH_SHORT).show();
                }
            }
        }).call();
    }

    public static void playContactedCallRecordings(final Context context, String callRecordUrl, final AutoDailerActivity.PlayListener playListener) {

        List<String> calldata = new ArrayList<String>();
        calldata.add(callRecordUrl);

        String[] data = new String[calldata.size()];
        data[0] = callRecordUrl;

        ArrayList<String[]> recording = new ArrayList<>();
        recording.add(data);


        final RecordingAdapter recordingAdapter = new RecordingAdapter(context, recording  );

        final Dialog dailog = new Dialog(context);
        dailog.setContentView(R.layout.audio_dailog);
        dailog.setTitle("Call Recordings");
        dailog.show();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = dailog.findViewById(R.id.recycleDialog);
        TextView close = dailog.findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dailog.dismiss();
                closeAudio();
                playListener.resumeActivity(true);

            }
        });

        dailog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                closeAudio();
                playListener.resumeActivity(true);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recordingAdapter);

    }

    public static String setTime(String open) {
        String callStartTime = "";
        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(new Date().getTime());//set the current time and date for this calendar

        String[] arr = open.split(":");
        if (arr.length >= 2) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int day1 = cur_cal.get(Calendar.DAY_OF_YEAR);

                cal.add(Calendar.DAY_OF_YEAR, day1);
                if (arr[0] != null && !(arr[0].isEmpty())) {
                    int hour = Integer.parseInt(arr[0]);
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                }
                if (arr[1] != null && !(arr[1].isEmpty())) {
                    int mins = Integer.parseInt(arr[1]);
                    cal.set(Calendar.MINUTE, mins);
                }
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
                cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
                cal.set(Calendar.YEAR, year);
                Date time = cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                SimpleDateFormat pstFormat = new SimpleDateFormat("kk");
                pstFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                callStartTime = pstFormat.format(sdf.parse(time.toString()));
                return callStartTime;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return callStartTime;
    }

    public static void putAudioStreams(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }


    public interface INotifier {
        void notify(boolean data);
    }

    public static INotifier notifier;

    public static void upSubStatus(Context context, String subStatus1, String subStatus2, Long id) {
        ContentValues cv = new ContentValues();
        MySql dbHelpr = MySql.getInstance(context);
        SQLiteDatabase db1 = dbHelpr.getWritableDatabase();
        if(subStatus1 != null) {
            cv.put("SUBSTATUS1", subStatus1);
        }
        if(subStatus2 != null) {
            cv.put("SUBSTATUS2", subStatus2);
        }
        if((subStatus1 != null && !subStatus1.isEmpty()) || ((subStatus2 != null && !subStatus2.isEmpty()))) {
           /* if (reminderdbId != 0) {
                db1.update("remindertbNew", cv, "_id=" + reminderdbId, null);
            } else if (lastDbId != 0) {*/
            db1.update("remindertbNew", cv, "_id=" + id, null);
            /*}*/
        }
    }

    public static void updateRnRLocaldb(Context context, int rnr_count, String customerNumber, String duraionOfACall ) {
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Long> ids = new ArrayList<>();

        Cursor cur = db.rawQuery("SELECT * FROM remindertbNew where TO1=" + "'" + customerNumber + "'", null);
        cur.moveToFirst();
        int i =0;
        try {
            if(cur != null &&cur.getCount() > 0) {
                while (!cur.isAfterLast()) {
                    long id = cur.getLong(cur.getColumnIndex("_id"));
                    ids.add(id);
                    cur.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cur.close();
            if (db != null && db.isOpen()) {
                db.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        }

        if (duraionOfACall != null && !(duraionOfACall.equalsIgnoreCase("0"))) {
            rnr_count = 0;
        }
        if(ids != null && !ids.isEmpty()) {
            for (int k = 0; k < ids.size(); k++) {
                try {
                    ContentValues cv = new ContentValues();
                    cv.put("RNR_COUNT", rnr_count);
                    Long id = ids.get(k);
                    if (id != 0) {
                        db.update("remindertbNew", cv, "_id=" + id, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public static boolean chechFirstCall(Context context, String number, Long reminderdbId, int completed) {
        if (reminderdbId != 0 && completed != 1) {
            return true;
        }
        if (number != null) {
            MySql dbhelpr = MySql.getInstance(context);
            SQLiteDatabase dbase = dbhelpr.getWritableDatabase();
            Long current = System.currentTimeMillis();
            String selection = "START_TIME" + "<=" + current + " AND " + "TO1=" + "'" + number + "'" + " AND " + "STATUS != 'deleted' " + " AND" + " RESPONSE_STATUS = 'accepted' ";
            Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, null);
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    return true;
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (dbase != null && dbase.isOpen()) {
                    dbase.close();
                }
                if (dbhelpr != null) {
                    dbhelpr.close();
                }
            }
        }
        return false;
    }

    public static void updateAppointmentCall(Context context, GetCalendarEntryInfo getCalendarEntryInfo, Long localdbId) {

        if (CommonUtils.isNetworkAvailable(context)) {
            new APIProvider.Update_Appointment(getCalendarEntryInfo, 1, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        } else {
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 0);
            cv.put("APPOINTMENT_TYPE", "complete_appointment");
            if(localdbId != 0) {
                db.update("remindertbNew", cv, "_id=" + localdbId, null);
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }


    /*    private Long createNewAppointment(Context context, long reminderdbId ,boolean cloudCheck, String notes, String status, String customerNumber) {
     *//* MySql dbHelper = new MySql(context, "mydb", null, AppConstants.dBversion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long dbStart, dbEnd, dbTravelTime, nextAppointmentStartTime;
        String toname = "",callRec_url="",toNumber = "", dbImageUrl = "", dbSubject, dbNotes, dbLocation, dbDesignation, dbCompanyName, dbwebsiteString, db_appointmentId, dborderValue, dbemailIdString, dbcompany_Address, dbproduct_type, dblead_source, db_external_reference, dbstatusString;
        int flpCount = 0;
        String AFTER_CALL_NOTES = "afterCallNotes";
        String AFTER_CALL_ASSIGN = "assignTo";
        String AFTER_CALL_NAME = "after_call_name";
        String AFTER_CALL_DESIGNATION = "after_call_designation";
        String AFTER_CALL_PHONE = "after_call_phone";
        String AFTER_CALL_COMPANY = "after_call_company";
        String AFTER_CALL_EMAIL = "after_call_email";
        String AFTER_CALL_LEAD = "after_call_lead";
        String AFTER_CALL_ADDRESS = "after_call_address";
        if (reminderdbId != 0 && !cloudCheck) {
            Cursor cur = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + reminderdbId + "'", null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                dbStart = cur.getLong(cur.getColumnIndex("START_TIME"));
                dbEnd = cur.getLong(cur.getColumnIndex("END_TIME"));
                dbSubject = cur.getString(cur.getColumnIndex("SUBJECT"));
                if (notes != null) {
                    dbNotes = notes;
                } else {
                    if (cur.getString(cur.getColumnIndex("NOTES")) == null) {
                        dbNotes = "";
                    } else {
                        dbNotes = cur.getString(cur.getColumnIndex("NOTES"));
                    }
                }
                dbLocation = cur.getString(cur.getColumnIndex("LOCATION"));
                dbTravelTime = cur.getLong(cur.getColumnIndex("TRAVEL_TIME"));
                dbCompanyName = cur.getString(cur.getColumnIndex("COMPANY_NAME"));
                dbDesignation = cur.getString(cur.getColumnIndex("DESIGNATION"));
                toNumber = cur.getString(cur.getColumnIndex("TO1"));
                dbImageUrl = cur.getString(cur.getColumnIndex("NOTES_IMAGE"));

                if (status != null && !status.isEmpty()) {
                    dbstatusString = status;
                } else {
                    dbstatusString = cur.getString(cur.getColumnIndex("STATUS"));
                }
                callRec_url = cur.getString(cur.getColumnIndex("CALLREC_URL"));
                toname = cur.getString(cur.getColumnIndex("TONAME"));
                db_appointmentId = cur.getString(cur.getColumnIndex("APPOINTMENT_ID"));
                dbwebsiteString = cur.getString(cur.getColumnIndex("WEBSITE"));
                db_external_reference = cur.getString(cur.getColumnIndex("EXTERNAL_REFERENCE"));
                dblead_source = cur.getString(cur.getColumnIndex("LEAD_SOURCE"));
                dbproduct_type = cur.getString(cur.getColumnIndex("PRODUCT_TYPE"));
                dbcompany_Address = cur.getString(cur.getColumnIndex("COMPANY_ADDRESS"));
                String followupValue = cur.getString(cur.getColumnIndex("FLP_COUNT"));
                String cc_email = cur.getString(cur.getColumnIndex("CCEMAIL"));

                if (followupValue != null && (!followupValue.isEmpty())) {
                    flpCount = Integer.parseInt(followupValue);
                    flpCount = flpCount + 1;
                } else {
                    flpCount = 1;
                }

                dbemailIdString = cur.getString(cur.getColumnIndex("EMAILID"));
                dborderValue = cur.getString(cur.getColumnIndex("ORDER_POTENTIAL"));

            }
            cur.close();
        } else {
            dbStart = 0;
            dbSubject = "";
            if (ApplicationSettings.getPref(AFTER_CALL_NOTES, null) != null) {
                if (!ApplicationSettings.getPref(AFTER_CALL_NOTES, "").isEmpty()) {
                    dbNotes = ApplicationSettings.getPref(AFTER_CALL_NOTES, "");
                } else {
                    dbNotes = "";
                }
            }
            if (ApplicationSettings.getPref(AFTER_CALL_ADDRESS, "") != null) {
                dbLocation = ApplicationSettings.getPref(AFTER_CALL_ADDRESS, "");
            }
            dbTravelTime = 0;
            if (ApplicationSettings.getPref(AFTER_CALL_COMPANY, "") != null) {
                dbCompanyName = ApplicationSettings.getPref(AFTER_CALL_COMPANY, "");
            }
            if (ApplicationSettings.getPref(AFTER_CALL_DESIGNATION, "") != null) {
                dbDesignation = ApplicationSettings.getPref(AFTER_CALL_DESIGNATION, "");
            }
            if (customerNumber != null) {
                toNumber = customerNumber;
            }
            if (status != null) {
                dbstatusString = status;
            } else {
                dbstatusString = checkSalesList();
            }
            callRec_url = "";
            toname = "";
            if (callerName != null) {
                if (!callerName.isEmpty()) {
                    toname = callerName;
                }
            }

            dbwebsiteString = "";
            db_external_reference = "";
            dblead_source = "";
            dbproduct_type = "";
            dbcompany_Address = "";
            dbemailIdString = "";

            dborderValue = "";
            flpCount = 1;
        }


        Long ameyoDbid = dosaveInDB();

        if (db.isOpen()) {
            db.close();
        }

        dbHelper.close();

        return ameyoDbid;*//*
    }*/
    public static void createMakeCall(final Context context, final String number,final long dbid, final String appointmentId,final String status, final  String substatus1, final  String substatus2, final String callername, final String notes) {
        if(context != null) {
            boolean secondSim = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
                List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

                //Log.d("Test", "Current list = " + subsInfoList);
                if (subsInfoList != null) {
                    for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                        String number1 = subscriptionInfo.getNumber();
                        int SimStotNumber = subscriptionInfo.getSimSlotIndex();
                        if (SimStotNumber == 1) {
                            secondSim = true;
                        }

                        //Log.d("Test", " Number is  " + number);
                    }
                } else {
                    Toast.makeText(context, "No SIM", Toast.LENGTH_SHORT).show();
                }
            } else {
                secondSim = true;
            }

            /*boolean mobileOnSimData = false;
            if (ApplicationSettings.containsPref(AppConstants.MOBILE_DATA_ON_SIM)) {
                mobileOnSimData = ApplicationSettings.getPref(AppConstants.MOBILE_DATA_ON_SIM, false);
            }*/

            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                /*if ((!secondSim && !(mobileOnSimData)) || (secondSim && mobileOnSimData)) {*/
                if (number != null) {
                    String userNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                    if (userNumber != null && !(userNumber.isEmpty())) {
                        final String customernumber = number;
                        String sr_number = ApplicationSettings.getPref(AppConstants.SR_NUMBER, "");
                        String caller_id = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                        if (caller_id != null && !(caller_id.isEmpty())) {
                            if (sr_number != null && !(sr_number.isEmpty())) {
                                KnowlarityModel knowlarityModel = new KnowlarityModel(sr_number, userNumber, number);
                                knowlarityModel.setClient_id(caller_id);
                                if (CommonUtils.isC2cNetworkAvailable(context)) {
                                    NotificationData.appointment_db_id = dbid;
                                    new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
                                        @Override
                                        public void onComplete(String data, long request_code, int failure_code) {
                                            if (data != null && !(data.isEmpty())) {
                                                if (data.contains("_SMBALERT_")) {
                                                    CommonUtils.setToast(context,data.replace("_SMBALERT_", ""));
                                                } else if (data.contains("_SMBACP_")) {
                                                    String uuidValue = data.replace("_SMBACP_", "");
                                                    try {
                                                        setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
                                                        NotificationData.knolarity_number = customernumber;
                                                        ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                                                        NotificationData.knolarity_start_time = new Date().toString();
                                                    } catch (NullPointerException e) {
                                                        e.printStackTrace();
                                                    }
                                                    CommonUtils.storeUuidHash(customernumber,uuidValue);
                                                    Toast.makeText(context, "Connecting to the customer. Please wait ...", Toast.LENGTH_LONG).show();
                                                    CommonUtils.showACPScreen(context);
                                                } else {
                                                    try {
                                                        setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
                                                        NotificationData.knolarity_number = customernumber;
                                                        ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                                                        NotificationData.knolarity_start_time = new Date().toString();
                                                    } catch (NullPointerException e) {
                                                        e.printStackTrace();
                                                    }
                                                    CommonUtils.storeUuidHash(customernumber,data);
                                                    Toast.makeText(context, "Connecting to the customer. Please wait ...", Toast.LENGTH_LONG).show();
                                                }

                                            } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                                                if (context != null) {
                                                    CommonUtils.setToast(context, "Invalid Auth key");
                                                }
                                            } else if (failure_code == APIProvider.INVALID_REQUEST) {
                                                if (context != null) {
                                                    CommonUtils.setToast(context, "Request Not Allowed");
                                                }
                                            } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                                                if (context != null) {
                                                    CommonUtils.setToast(context, "Invalid Parameters");
                                                }
                                            } else if (failure_code == APIProvider.INVALID_NUMBER) {
                                                markFollowupAsCompleated(context, customernumber, dbid, appointmentId, 1);
                                                createCmail(context,customernumber, status, 1);
                                                if (context != null) {
                                                    CommonUtils.setToast(context, "This number is not valid");
                                                }
                                            } else if (failure_code == APIProvider.SERVER_ALERT) {
                                                Toast.makeText(context, APIProvider.SERVER_ALERT_MESSAGE, Toast.LENGTH_LONG).show();
                                            } else if (failure_code == APIProvider.DND) {
                                                markFollowupAsCompleated(context, customernumber, dbid, appointmentId, 0);
                                                createCmail(context,customernumber, status, 0);
                                                Intent intent = new Intent(context, ReuploadService.class);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    context.startForegroundService(intent);
                                                } else {
                                                    context.startService(intent);
                                                }
                                                Toast.makeText(context, "This number is NDNC.", Toast.LENGTH_LONG).show();
                                            } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                                                Toast.makeText(context, "Call Failed!", Toast.LENGTH_LONG).show();

                                            } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                                                Toast.makeText(context, "Hey! Ask your admin to call Support at 9113907215.", Toast.LENGTH_LONG).show();
                                            } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                                                Toast.makeText(getApplicationContext(), "Number not registered for outbound services for this agent", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }).reClickToCall(knowlarityModel);
                                    Toast toastMessage = Toast.makeText(context, "Dialling", Toast.LENGTH_LONG);
                                    toastMessage.setGravity(Gravity.CENTER, 0, 0);
                                    toastMessage.show();
                                } else {
                                    Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "No SR Number", Toast.LENGTH_SHORT).show();
                                callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                            }
                        } else {
                            Toast.makeText(context, "No Client ID", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Invalid User Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Invalid Customer Number", Toast.LENGTH_SHORT).show();
                }
                /*} else {
                    callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                }*/
            } else {
                callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
            }
        }
    }

    public static boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void createSbiMakeCall(final Context context, final String number,final long dbid, final String appointmentId,final String status, final  String substatus1, final  String substatus2, final String callername, final String notes) {
        if(context != null) {
            if(checkPermission(context)) {
                boolean secondSim = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
                    List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                    if (subsInfoList != null) {
                        for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                            String number1 = subscriptionInfo.getNumber();
                            int SimStotNumber = subscriptionInfo.getSimSlotIndex();
                            if (SimStotNumber == 1) {
                                secondSim = true;
                            }
                        }
                    } else {
                        setToast(context, "No SIM");
                        return;
                    }
                } else {
                    secondSim = true;
                }

                /*boolean mobileOnSimData = false;
                if (ApplicationSettings.containsPref(AppConstants.MOBILE_DATA_ON_SIM)) {
                    mobileOnSimData = ApplicationSettings.getPref(AppConstants.MOBILE_DATA_ON_SIM, false);
                }*/

                if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                    if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                        /*if ((!secondSim && !(mobileOnSimData)) || (secondSim && mobileOnSimData)) {*/
                        if (number != null) {
                            String userNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                            if (userNumber != null && !(userNumber.isEmpty())) {
                                final String customernumber = number;
                                String sr_number = ApplicationSettings.getPref(AppConstants.SR_NUMBER, "");
                                String caller_id = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");

                                if (caller_id != null && !(caller_id.isEmpty())) {
                                    if (sr_number != null && !(sr_number.isEmpty())) {
                                        if (CommonUtils.isC2cNetworkAvailable(context)) {
                                            KnowlarityModel knowlarityModel = new KnowlarityModel(sr_number, userNumber, customernumber);
                                            knowlarityModel.setClient_id(caller_id);
                                            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                                            if (screen != null && (screen.equalsIgnoreCase("Bank1AfterCallActivity") || screen.equalsIgnoreCase("UearnActivity"))) {
                                                NotificationData.appointment_db_id = dbid;
                                                if (dbid == 0) {
                                                    NotificationData.callFromDialer = true;
                                                }
                                                sbiCall(knowlarityModel, customernumber, status, context, substatus1, substatus2, callername, notes, appointmentId, dbid);
                                            } else {
                                                callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                                            }
                                        } else {
                                            Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        setToast(context, "No SR Number");
                                        callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                                    }
                                } else {
                                    setToast(context, "No Client ID");
                                }
                            } else {
                                setToast(context, "Invalid User Number");
                            }
                        } else {
                            setToast(context, "Invalid Customer Number");
                        }
                       /* } else {
                            callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                        }*/
                    } else {
                        callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                    }
                } else {
                    Log.d("AppointmentId", "CommonUtls id:----" +dbid);
                    Log.d("AppointmentId", "CommonUtls appointmentId:----" +appointmentId);
                    NotificationData.appointment_db_id = dbid;
                    callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                }
            } else {
                Toast.makeText(context, "please enable APP Permissions in Phone Settings. APPs -> UEARN -> Permissions ", Toast.LENGTH_SHORT).show();
            }
        } else {
            setToast(context,"Follow-up already done");
        }
    }

    public static void setToast(Context context, String msg) {
        Toast toastMessage = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toastMessage.setGravity(Gravity.CENTER, 0, 0);
        toastMessage.show();
    }

    public static void sbiCall(KnowlarityModel knowlarityModel, final String customernumber, final String status, final Context context, final String substatus1, final String substatus2, final String callername, final String notes, final String appointmentId, final Long dbid) {
        NotificationData.knolarity_start_time = new Date().toString();

        /*new APIProvider.KnowlarityClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {*/
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                NotificationData.knolarity_response_time = new Date().toString();
                if (data != null && !(data.isEmpty())) {
                    if (data.contains("_SMBALERT_")) {
                        CommonUtils.setToast(getApplicationContext(),data.replace("_SMBALERT_", ""));
                    } else if (data.contains("_SMBACP_")) {
                        String uuidValue = data.replace("_SMBACP_", "");
                        try {
                            NotificationData.transactionId = uuidValue;
                            NotificationData.uuid = uuidValue;
                            NotificationData.knolarity_response = "success : ";
                            setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        storeUuidHash(customernumber,uuidValue);
                        setToast(getApplicationContext(), "Connecting to the customer. Please wait ...");
                        CommonUtils.showACPScreen(getApplicationContext());
                    } else {
                        try {
                            NotificationData.transactionId = data;
                            Log.d("NotificationTrans", NotificationData.transactionId);
                            NotificationData.uuid = data;
                            NotificationData.knolarity_response = "success : ";
                            setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        storeUuidHash(customernumber,data);
                        setToast(getApplicationContext(), "Connecting to the customer. Please wait ...");
                    }

                } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                    Toast.makeText(getApplicationContext() ,"Invalid Auth key", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_REQUEST) {
                    Toast.makeText(getApplicationContext(), "Invalid Request", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                    Toast.makeText(getApplicationContext() ,"Invalid Parameters", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_NUMBER) {
                    updateSalesStage(customernumber);
                    markFollowupAsCompleated(context, customernumber, dbid, appointmentId, 1);
//                    createCmail(context,customernumber, status, 1);
                    Toast.makeText(getApplicationContext(), "This number is not valid", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.SERVER_ALERT) {
                    Toast.makeText(getApplicationContext(), APIProvider.SERVER_ALERT_MESSAGE, Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.DND) {
                    ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                    if(cn != null && cn.getClassName().equals("smarter.uearn.money.activities.DashboardAgent")) {
                        setToast(context, "DND Registered Number.");
                    } else {
                        setToast(context, "DND Registered Number.");
                        NotificationData.knolarity_response = "DND : ";
                        markFollowupAsCompleated(context, customernumber, dbid, appointmentId, 0);
                        createCmail(context, customernumber, status, 0);
                        Intent intent = new Intent(context, ReuploadService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent);
                        } else {
                            context.startService(intent);
                        }
                    }
                } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                    NotificationData.knolarity_response = "Agent bot verified : ";
                    setToast(context,"Hi. Your mobile number in Not verified.");
                } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                    NotificationData.knolarity_response = "Sr not registered : ";
                    setToast(context, "Hey! Ask your admin to call Support at 9113907215.");
                } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                    NotificationData.knolarity_response = "agent not registered : ";
                    Toast.makeText(getApplicationContext(), "Number not registered for outbound services for this agent", Toast.LENGTH_LONG).show();
                } else {
                    NotificationData.knolarity_response = "request failed : ";
                    setToast(context,  "Please check your internet connection.");
                }
            }
            /*}).call();*/
        }).reClickToCall(knowlarityModel);
    }


/*

    private void reCall(KnowlarityModel knowlarityModel, final String customernumber) {
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                if (data != null && !(data.isEmpty())) {
                    Log.d("KnowlarityCall", "Response" + data);
                    try {
                        setCallData();
                        NotificationData.knolarity_number = customernumber;
                        ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        NotificationData.knolarity_start_time = new Date().toString();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Request received successfully.. Please wait..", Toast.LENGTH_LONG).show();
                    AppointmentViewActivity.this.finish();
                } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                    Toast.makeText(getApplicationContext(), "Unable to process your request.", Toast.LENGTH_LONG).show();
                    //CustomSingleButtonDialog.buildSingleButtonDialog("Error", "Call Failed! Agent not verified, Agent has to call on SR number for verification.", AppointmentViewActivity.this, true);
                } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                    Toast.makeText(getApplicationContext(), "No active integration found.", Toast.LENGTH_LONG).show();
                    //CustomSingleButtonDialog.buildSingleButtonDialog("Error", "No active integration found.", AppointmentViewActivity.this, true);
                } else {
                    Toast.makeText(getApplicationContext(), "Request failed.", Toast.LENGTH_LONG).show();
                }
            }
        }).reClickToCall(knowlarityModel);
    }*/

    public static void callToCustomer(Context context, String number, String appointmentId, String status, String substatus1, String substatus2, String callername, String notes) {
        if(context != null) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            if (number != null) {
                String num = "tel:" + number;
                intent.setData(Uri.parse(num));
            }
            setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(intent);
        }
    }

    public static void setCallData(String appointmentId, String status, String substatus1, String substatus2, String callername, String notes) {
        NotificationData.notificationData = true;
        NotificationData.appointment_id = appointmentId;
        NotificationData.statusString = status;
        NotificationData.notes_string = notes;
        NotificationData.order_value = "0";
        NotificationData.isAppointment = true;
        String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
        if (screen != null && ((screen.equalsIgnoreCase("Auto2AfterCallActivity") || (screen.equalsIgnoreCase("Auto1AfterCallActivity"))))) {
            NotificationData.makeACall = true;
        }
        if(callername != null) {
            NotificationData.knolarity_name = callername;
        }
        if(substatus1 != null && status != null && !status.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus1 = substatus1;
        }
        if(substatus2 != null && status != null && !status.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus2 = substatus2;
        }
    }

    public static void markFollowupAsCompleated(Context context, String phoneno, Long dbid, String appointmentId, int status) {

        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();

        if(context != null && dbid != 0 && appointmentId != null) {

            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 0);
            cv.put("COMPLETED", 1);
            cv.put("RESPONSE_STATUS", "completed");
            cv.put("APPOINTMENT_TYPE", "complete_appointment");
            if(status == AppConstants.INVALID) {
                cv.put("STATUS", "OTHERS");
                cv.put("SUBSTATUS1", "INVALID");
            } else {
                cv.put("SUBJECT", "DND NUMBER");
            }
            String start = CommonUtils.getTimeFormatInISO(new Date());
            cv.put("CREATED_AT", start);
            cv.put("UPDATED_AT", start);
            cv.put("RNR_COUNT", "0");

            if (dbid != 0) {
                db1.update("remindertbNew", cv, "_id=" + dbid, null);
            }
            if (db1 != null && db1.isOpen()) {
                db1.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }

           /* GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
            getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
            getCalendarEntryInfo.appointment_id = appointmentId;
            getCalendarEntryInfo.responsestatus = "completed";
            getCalendarEntryInfo.update_all_fields = false;
            getCalendarEntryInfo.tat = 0;
            getCalendarEntryInfo.rnrCount = 0;

            if (start != null && !(start.isEmpty())) {
                getCalendarEntryInfo.created_at = start;
            }*/

            //updateAppointmentCall(context, getCalendarEntryInfo, dbid);
        } else {

            String statusValue = checkSalesList();
            long start_time = System.currentTimeMillis();
            long endTime = (start_time + (60 * 60 * 1000));

            ContentValues cv = new ContentValues();
            cv.put("SUBJECT", "");
            cv.put("NOTES", "");
            cv.put("START_TIME", start_time);
            cv.put("END_TIME", endTime);
            cv.put("LOCATION", "");
            cv.put("COMPANY_NAME", "");
            cv.put("DESIGNATION", "");
            cv.put("EMAILID", "");
            cv.put("WEBSITE", "");
            cv.put("UPDATED_AT", start_time);
            if(statusValue != null) {
                cv.put("STATUS", statusValue);
            }
            cv.put("UPLOAD_STATUS", 0);
            cv.put("ALARMSETTO", 0);
            cv.put("COMPANY_ADDRESS", "");
            cv.put("PRODUCT_TYPE", "");
            cv.put("ORDER_POTENTIAL", "");
            cv.put("LEAD_SOURCE", "");
            cv.put("COMPLETED", 1);
            cv.put("TO1", phoneno);
            cv.put("TONAME", "");

            cv.put("RESPONSE_STATUS", "completed");
            cv.put("APPOINTMENT_TYPE", "complete_appointment");

            db1.insert("remindertbNew", null, cv);
            if (db1 != null && db1.isOpen()) {
                db1.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    private static String checkSalesList() {
        String user_id = SmarterSMBApplication.SmartUser.getId();
        SalesStageInfo salesStageInfo = SmarterSMBApplication.salesStageInfo;
        if (salesStageInfo != null) {
            ArrayList<String> arrayList1 = salesStageInfo.getAppointmentSalesStage();
            if (arrayList1 == null ) {
                callSalesStage(user_id);
            } else if(arrayList1.size() <= 0) {
                callSalesStage(user_id);
            }else {
                stage1 = arrayList1.get(0);
                return stage1;
            }
        }

        return stage1;
    }

    private static void callSalesStage(String user_id) {
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            new APIProvider.Get_Sales_Status(user_id, 22, new API_Response_Listener<SalesStageInfo>() {
                @Override
                public void onComplete(SalesStageInfo data, long request_code, int failure_code) {
                    if (data != null) {
                        data.dosave();
                        SmarterSMBApplication.salesStageInfo = data;
                        ArrayList<String> arrayList2 = data.getAppointmentSalesStage();
                        if (arrayList2 != null && (arrayList2.size() > 0)) {
                            stage1 = arrayList2.get(0);
                        }
                    }
                }
            }).call();
        }
    }

    public static void createCmail(Context context, String phoneno, String status, int flp_status) {
        if(context != null) {
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            if(flp_status == AppConstants.INVALID) {
                cv.put("EVENT_TYPE", "call_invalid");
                cv.put("STATUS", "INVALID");
            } else {
                cv.put("EVENT_TYPE", "call_dnd");
                cv.put("STATUS", status);
            }
            if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                cv.put("FROM1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                cv.put("TO1", phoneno);
                cv.put("MSG_RECEPIENT_NO", phoneno);

            }
            cv.put("SUBJECT", "Outgoing Call");
            cv.put("MESSAGE", "Outgoing Call");
            cv.put("UPLOAD_STATUS", 1);
            cv.put("EMAIL", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null));
            cv.put("PARENT", 0);
            cv.put("STARTTIME", CommonUtils.getTimeFormatInISO(new Date()));
            cv.put("ENDTIME", CommonUtils.getTimeFormatInISO(new Date()));
            cv.put("UNREAD", "false");
            String callerName = CommonUtils.getContactName(context, phoneno);
            cv.put("CALLER", callerName);
            db1.insert("mytbl", null, cv);
            if (db1 != null && db1.isOpen()) {
                db1.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    public static String getLastCallDuration(Context context,String number) {
        return "";
    }

    public static int getRNRCount(Context context, String customerNumber) {
        int rnrCount = 1;
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (customerNumber != null && !customerNumber.isEmpty()) {
            Long current = System.currentTimeMillis();
            String selection = "TO1=" + "'" + customerNumber + "'" + " AND " + "START_TIME" + "<=" + current;
            Cursor cur = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
            try {
                if (cur != null && cur.getCount() > 0) {
                    cur.moveToFirst();
                    int rnr_count = cur.getInt(cur.getColumnIndex("RNR_COUNT"));
                    //rnr_count = rnr_count + 1;
                    rnrCount = rnr_count;
                    //Log.d("getRNRCOUNTRnrCount", "" + rnr_count);
                    return rnrCount;
                }
            } finally {
                cur.close();
                if (db != null && db.isOpen()) {
                    db.close();
                }
                if (dbHelper != null) {
                    dbHelper.close();
                }
            }
        }
        return rnrCount;
    }

    public static void callLogApi(Context context, String className) {
        try {
            SimpleDateFormat reportFormat = new SimpleDateFormat("EEE, d MMM yy");
            Calendar mCalendar = Calendar.getInstance();
            String reportDate = reportFormat.format(mCalendar.getTime());
            int reportMinute = mCalendar.get(Calendar.MINUTE);
            int reportHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            String reportDateTime = "<br/><br/>Date: " + reportDate + "<br/>Time: " + reportHour + ":" + reportMinute;
            int version_code = CommonUtils.getVersionCode(context);
            String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                    ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + " <br/>Empty Number in " + className + reportDateTime
                    + "<br/> Current Activity: " + SmarterSMBApplication.currentActivity + "<br/><br/>App Version: " + version_code;

//            ServiceApplicationUsage.callErrorLog(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTimeStamp(String startTime) {
        String didNumber = ApplicationSettings.getPref(AppConstants.CLOUD_TELEPHONY, "");

        String strArray[] = didNumber.split(",");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        Date calldate = new Date();
        try {
            calldate = df.parse(startTime);
        } catch (Exception e) {
            //Log.e("SMSComposeActivity", e.toString());
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String timeStamp = format.format(calldate);
        timeStamp = timeStamp + "Z";
        return timeStamp;
    }


    //MakeACall API
    public static void clickToCall(Context context, String customerNumber) {
        boolean secondSim = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
            List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            for (SubscriptionInfo subscriptionInfo : subsInfoList) {
                String number = subscriptionInfo.getNumber();
                int SimStotNumber = subscriptionInfo.getSimSlotIndex();
                if (SimStotNumber == 1) {
                    secondSim = true;
                }
            }
        } else {
            secondSim = true;
        }

        /*boolean mobileOnSimData = false;
        if (ApplicationSettings.containsPref(AppConstants.MOBILE_DATA_ON_SIM)) {
            mobileOnSimData = ApplicationSettings.getPref(AppConstants.MOBILE_DATA_ON_SIM, false);
        }*/

        if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
            /*if ((!secondSim && !(mobileOnSimData)) || (secondSim && mobileOnSimData)) {*/
            if (customerNumber != null) {
                String userNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                if (userNumber != null && !(userNumber.isEmpty())) {
                    final String customernumber = customerNumber;
                    String sr_number = ApplicationSettings.getPref(AppConstants.SR_NUMBER, "");
                    String caller_id = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                    if (caller_id != null && !(caller_id.isEmpty())) {
                        if (sr_number != null && !(sr_number.isEmpty())) {
                            if (CommonUtils.isC2cNetworkAvailable(context)) {
                                KnowlarityModel knowlarityModel = new KnowlarityModel(sr_number, userNumber, customerNumber);
                                knowlarityModel.setClient_id(caller_id);
                                String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                                if (screen != null && (screen.equalsIgnoreCase("Auto1AfterCallActivity") || screen.equalsIgnoreCase("Auto2AfterCallActivity"))) {
                                    reCall(context,knowlarityModel, customernumber);
                                } else {
                                    sbiCall(context,knowlarityModel, customernumber);
                                }
                            } else {
                                Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "No SR Number", Toast.LENGTH_SHORT).show();
                            callToCustomer(context, customerNumber);
                        }
                    } else {
                        Toast.makeText(context, "No Client ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Invalid User Number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Invalid Customer Number", Toast.LENGTH_SHORT).show();
            }
         /*   } else {
                callToCustomer(context, customerNumber);
            }*/
        } else {
            callToCustomer(context, customerNumber);
        }
    }

    public static boolean shouldRecord() {
        return ApplicationSettings.getPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
    }

    public static void showACPScreen(Context cntxt) {
        String qanda = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE, "");
        String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN,"");

        if (shouldRecord()) {
            String connectedCustomer = ApplicationSettings.getPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
            //Log.d("SMBHOME", "connectedCustomer OUTSIDE" + connectedCustomer);
            if (connectedCustomer != null && !connectedCustomer.isEmpty()) {
                ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, connectedCustomer);
                //Log.d("SMBHOME", "Starting service for recording of" + connectedCustomer);
                Intent recorderIntent = new Intent(cntxt, RecorderService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cntxt.startForegroundService(recorderIntent);
                } else {
                    cntxt.startService(recorderIntent);
                }
            }
        }

        if(screen != null && !screen.isEmpty()) {
            String activityToStart = "smarter.uearn.money.activities." + screen;
            Class<?> c = null;
            try {
                c = Class.forName(activityToStart);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            final Class<?> finalc = c;

            Intent intent = new Intent(cntxt, finalc);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if(qanda != null && !qanda.isEmpty()) {
                intent.putExtra("showinprogress", 1);
            }

            cntxt.startActivity(intent);
        }
    }

    public static void storeUuidHash(String custNo, String uuidval) {
        HashMap<String, String> testHashMap = new HashMap<String, String>();
        testHashMap.put("customer", custNo);
        testHashMap.put("uuid",uuidval);

        Gson gson = new Gson();
        String hashMapString = gson.toJson(testHashMap);

        //Log.d("Bank1AfterCallActivity", "storeUuidHash:" + custNo+" UUID: "+uuidval);

        ApplicationSettings.putPref(AppConstants.CUSTOMER_UUID_MAP, hashMapString);
    }

    public static String fetchUuidHash(String custNo) {

        String storedHashMapString = ApplicationSettings.getPref(AppConstants.CUSTOMER_UUID_MAP, "");
        String storedUUID = null;
        if( storedHashMapString != null && (!(storedHashMapString.isEmpty()))) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);
            String storedCustomer = testHashMap2.get("customer");
            if (storedCustomer != null && (!(storedCustomer.isEmpty()))) {
                if (storedCustomer.equals(custNo)) {
                    storedUUID = testHashMap2.get("uuid");
                }
            }
        }

        //       ApplicationSettings.putPref(AppConstants.CUSTOMER_UUID_MAP, "");
        //Log.d("Bank1AfterCallActivity", "fetchUuidHash: "+storedHashMapString+" customer"+custNo+ " UUID:"+storedUUID);
        return storedUUID;
    }

    public static void storeAutoDialerHash(String custNo) {
        HashMap<String, String> testHashMap = new HashMap<String, String>();
        testHashMap.put("customer", custNo);
        testHashMap.put("autodialed","1");

        Gson gson = new Gson();
        String hashMapString = gson.toJson(testHashMap);

        ApplicationSettings.putPref(AppConstants.CUSTOMER_AUTODIAL_MAP, hashMapString);
    }

    public static String fetchAutoDialerHash(String custNo) {

        String storedHashMapString = ApplicationSettings.getPref(AppConstants.CUSTOMER_AUTODIAL_MAP, "");
        String storedAutoDial = null;
        if( storedHashMapString != null && (!(storedHashMapString.isEmpty()))) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);
            String storedCustomer = testHashMap2.get("customer");
            if (storedCustomer != null && (!(storedCustomer.isEmpty()))) {
                if (storedCustomer.equals(custNo)) {
                    storedAutoDial = testHashMap2.get("autodialed");
                }
            }
        }

        return storedAutoDial;
    }

    public static String getLastCustomerNumber() {
        String storedHashMapString = ApplicationSettings.getPref(AppConstants.CUSTOMER_UUID_MAP, "");
        if( storedHashMapString != null && (!(storedHashMapString.isEmpty()))) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);
            String storedCustomer = testHashMap2.get("customer");
            //Log.d("Bank1AfterCallActivity", "getLastCustomerNumber: "+storedHashMapString+ " Customer Number:"+storedCustomer);
            return storedCustomer;
        } else {
            return "";
        }

    }

    public static void sbiCall(Context context, KnowlarityModel knowlarityModel, final String customernumber) {
        NotificationData.knolarity_start_time = new Date().toString();
        /*new APIProvider.KnowlarityClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {*/
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                NotificationData.knolarity_response_time = new Date().toString();
                if (data != null && !(data.isEmpty())) {
                    if (data.contains("_SMBALERT_")) {
                        CommonUtils.setToast(getApplicationContext(),data.replace("_SMBALERT_", ""));
                    } else if (data.contains("_SMBACP_")) {
                        String uuidValue = data.replace("_SMBACP_", "");
                        NotificationData.uuid = uuidValue;
                        NotificationData.knolarity_response = "success : ";
                        try {
                            setCallData(customernumber);
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        CommonUtils.storeUuidHash(customernumber,uuidValue);
                        Toast.makeText(getApplicationContext(), "Connecting to the customer. Please wait ...", Toast.LENGTH_LONG).show();
                        CommonUtils.showACPScreen(getApplicationContext());
                    } else {
                        NotificationData.transactionId = data;
                        NotificationData.uuid = data;
                        NotificationData.knolarity_response = "success : ";
                        try {
                            setCallData(customernumber);
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        CommonUtils.storeUuidHash(customernumber,data);
                        Toast.makeText(getApplicationContext(), "Connecting to the customer. Please wait ...", Toast.LENGTH_LONG).show();
                    }
                } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                    Toast.makeText(getApplicationContext() ,"Invalid Auth key", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_REQUEST) {
                    Toast.makeText(getApplicationContext(), "Invalid Request", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                    Toast.makeText(getApplicationContext() ,"Invalid Parameters", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_NUMBER) {
                    Toast.makeText(getApplicationContext(), "This number is not valid", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.SERVER_ALERT) {
                    Toast.makeText(getApplicationContext(), APIProvider.SERVER_ALERT_MESSAGE, Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.DND) {
                    NotificationData.knolarity_response = "dnd : ";
                    Toast.makeText(getApplicationContext(), "DND Registered Number.", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                    NotificationData.knolarity_response = "Agent not verified : ";
                    Toast.makeText(getApplicationContext(), "Hi. Your mobile number in Not verified.", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                    NotificationData.knolarity_response = "sr not registered : ";
                    Toast.makeText(getApplicationContext(), "Hey! Ask your admin to call Support at 9113907215.", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                    NotificationData.knolarity_response = "Agent not registered : ";
                    setToast(getApplicationContext(), "Number not registered for outbound services for this agent");
                } else {
                    NotificationData.knolarity_response = "request failed : ";
                    Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        }).call();

     /*   Intent main = new Intent(context, SmarterMainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(main);*/
        Toast toastMessage = Toast.makeText(context, "Dialling", Toast.LENGTH_LONG);
        toastMessage.setGravity(Gravity.CENTER, 0, 0);
        toastMessage.show();
    }

    public static void reCall(Context context, KnowlarityModel knowlarityModel, final String customernumber) {
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                if (data != null && !(data.isEmpty())) {
                    //Log.d("KnowlarityCall", "Response" + data);
                    NotificationData.transactionId = data;
                    NotificationData.uuid = data;
                    NotificationData.knolarity_response = "success : ";
                    try {
                        setCallData(customernumber);
                        NotificationData.knolarity_number = customernumber;
                        ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        NotificationData.knolarity_start_time = new Date().toString();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Connecting to the customer. Please wait ...", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                    Toast.makeText(getApplicationContext() ,"Invalid Auth key", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_REQUEST) {
                    Toast.makeText(getApplicationContext(), "Invalid Request", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                    /*CommonUtils.setToast(context, "Invalid Parameters");*/
                    Toast.makeText(getApplicationContext() ,"Invalid Parameters", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_NUMBER) {
                    Toast.makeText(getApplicationContext(), "This number is not valid", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                    Toast.makeText(getApplicationContext(), "Unable to process your request.", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                    Toast.makeText(getApplicationContext(), "Hey! Ask your admin to call Support at 9113907215.", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                    Toast.makeText(getApplicationContext(), "Number not registered for outbound services for this agent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        }).reClickToCall(knowlarityModel);

       /* Intent main = new Intent(context, SmarterMainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(main);*/
        Toast toastMessage = Toast.makeText(context, "Dialling", Toast.LENGTH_LONG);
        toastMessage.setGravity(Gravity.CENTER, 0, 0);
        toastMessage.show();
    }

    public static void callToCustomer(Context context, String customer_number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        if (customer_number != null) {
            String num = "tel:" + customer_number;
            intent.setData(Uri.parse(num));
        }
        setCallData(customer_number);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.startActivity(intent);
    }

    public static void setCallData(String customer_number) {
        NotificationData.appointment_id = "";
        NotificationData.statusString = "";
        NotificationData.notes_string = "";
        NotificationData.order_value = "";
        if(customer_number != null) {
            NotificationData.knolarity_name = customer_number;
        }
        NotificationData.substatus1 = "";
        NotificationData.substatus2 = "";
        // NotificationData.makeACall = true;
    }

    public static boolean panValidation(String pan) {
        final String pan_pattern = "(([A-Za-z]{5})([0-9]{4})([a-zA-Z]))";
        if (pan.length() == 10) {
            String s = pan; // get your editext value here
            Pattern pattern = Pattern.compile(pan_pattern);
            Matcher matcher = pattern.matcher(s);
            return matcher.matches();
        } else {
            //tip_pan.setError("PAN Number Must be 10 digits");
            return false;
        }
    }

    public static void postAppStatus(Context context, String activityType) {
        String userId = "";
        double latitude = 0;
        double longitude = 0;
        if(ApplicationSettings.containsPref(AppConstants.USERINFO_ID)) {
            if(ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            }
        }
        boolean gpsSettings = ApplicationSettings.getPref(AppConstants.GPS_SETTINGS, false);
        boolean isRecording = ApplicationSettings.getPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
        //String activityType = "signout";
        int version_code = 0;
        if(getApplicationContext() != null) {
            version_code = CommonUtils.getVersionCode(getApplicationContext());
        }
        String versionNumber = ""+version_code;
        if(context != null) {
            GPSTracker gpsTracker = new GPSTracker(context);
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
            }
        }
        String timeStamp = CommonUtils.getTimeFormatInISO(new java.sql.Date(System.currentTimeMillis()));
        if ((userId != null) && (timeStamp != null) && (activityType != null) && (versionNumber != null)) {
            AppStatus appstatus = new AppStatus(activityType, "" + gpsSettings, "" + isRecording, "" + 0, userId, timeStamp, versionNumber, latitude, longitude);
            //Log.d("app status", " " + appstatus);
            if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                new APIProvider.PostAppStatus(appstatus, 0, new API_Response_Listener<AppStatus>() {
                    @Override
                    public void onComplete(AppStatus data, long request_code, int failure_code) {
                        //Log.d("Log out timestamp", "Data is " + data);
                    }
                }).call();
            }
        }
    }

    public static String getRole(String email, Context context) {
        String role = "";
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor dbCursor = db.rawQuery("SELECT * FROM TeamMembers WHERE EMAILID=" + "'" + email + "'", null);
            if (dbCursor != null && dbCursor.getCount() > 0) {
                dbCursor.moveToFirst();
                role = dbCursor.getString(dbCursor.getColumnIndex("ROLE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return role;
    }

    private static void updateSalesStage(String customerNumber) {

        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        getCalendarEntryInfo.caller_number = customerNumber;
        getCalendarEntryInfo.status = "OTHERS";
        getCalendarEntryInfo.subStatus1 = "INVALID";

        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        getCalendarEntryInfo.user_id = userId;

        String leadSource = ApplicationSettings.getPref(AFTER_CALL_LEAD, "");
        if(leadSource == null || leadSource.isEmpty()) {
            leadSource = getLeadSource(customerNumber);
        }
        getCalendarEntryInfo.lead_source = leadSource;

        String customerToContact = ApplicationSettings.getPref(CUSTOMER_TO_CONTACT, "");
        if(customerToContact == null || customerToContact.isEmpty()) {
            customerToContact = getCustomerToContact(customerNumber);
        }
        getCalendarEntryInfo.customer_id = customerToContact;

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            new APIProvider.Update_SalesStatus(getCalendarEntryInfo, 0, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        //Log.d("AutodillerFragment", "Make call Response " + data);
                    }
                }
            }).call();
        }
    }

    private static String getLeadSource(String number) {
        String leadSource = "";
        try {
            MySql dbHelper = MySql.getInstance(getApplicationContext());
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if(number != null && !number.isEmpty()) {
                String selection = "TO1=" + "'" + number + "'";
                Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
                }
                if (dbase != null && dbase.isOpen()) {
                    dbase.close();
                }
                if (dbHelper != null) {
                    dbHelper.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leadSource;
    }

    private static String getCustomerToContact(String number) {
        String customerId = "";
        try {
            MySql dbHelper = MySql.getInstance(getApplicationContext());
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if(number != null && !number.isEmpty()) {
                String selection = "TO1=" + "'" + number + "'";
                Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    customerId = cursor.getString(cursor.getColumnIndex("CUSTOMER_ID"));
                }
                if (dbase != null && dbase.isOpen()) {
                    dbase.close();
                }
                if (dbHelper != null) {
                    dbHelper.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerId;
    }

    public static String convertDate(String date) {
        boolean isValid = checkIfDateIsInCorrectFormat(date);
        if(isValid) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date d = null;
            try {
                d = f.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat f2 = new SimpleDateFormat("dd MMM yyyy");
            String dateString = f2.format(d);
            System.out.println(dateString);
            return dateString;
        } else {
            return date;
        }
    }

    private static boolean checkIfDateIsInCorrectFormat(String date){
        boolean isValid = isValidDateFormat(date, "yyyy-MM-dd'T'HH:mm:ss.SSS");
        return isValid;
    }

    private static boolean isValidDateFormat(String inputDateString, String format){
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            f.parse(inputDateString);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }


    public static  String convertNumberToRupee(String rupee) {
        String rupeeStr = "";
        String lastThreeDights = "";
        try {
            String originalString = rupee.toString();

            Long longval;
            if (originalString.contains(",")) {
                originalString = originalString.replaceAll(",", "");
            }

            String earnRSub = originalString.substring(0, originalString.length() - 3);


            if (originalString.length() > 3)
            {
                lastThreeDights = originalString.substring(originalString.length() - 3);
            }
            else
            {
                lastThreeDights = originalString;
            }


            longval = Long.parseLong(earnRSub);

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            rupeeStr = formatter.format(longval);

            //setting text after format to EditTex
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return rupeeStr+lastThreeDights;
    }



    public static  String convertNumbersToRupee(String rupee) {
        String rupeeStr = "";
        String lastThreeDights = "";
        try {
            String originalString = rupee.toString();

            Long longval;
            if (originalString.contains(",")) {
                originalString = originalString.replaceAll(",", "");
            }

            // String earnRSub = originalString.substring(0, originalString.length() - 3);


//            if (originalString.length() > 3)
//            {
//                lastThreeDights = originalString.substring(originalString.length() - 3);
//            }
//            else
//            {
//                lastThreeDights = originalString;
//            }


            longval = Long.parseLong(originalString);

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###,###,###");
            rupeeStr = formatter.format(longval);

            //setting text after format to EditTex
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return rupeeStr+lastThreeDights;
    }

    public static boolean compareToDate (String update_at) {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String updateAtdateStr = getDateFromString(update_at);
        calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String todaydateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Log.d("startofMonth", "CommonUtils -- todaydateStr"+ todaydateStr);
        Log.d("startofMonth", "CommonUtils -- updateAtdateStr"+ updateAtdateStr);

        boolean isGreatar = CheckDates(updateAtdateStr, todaydateStr) ;
        return isGreatar;
    }

    public static boolean CheckDates(String update_at, String todayDate)    {
        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd");
        boolean b = false;
        try {
            if(dfDate.parse(update_at).before(dfDate.parse(todayDate)))
            {
                b = true;//If start date is before end date
            }
            else if(dfDate.parse(update_at).equals(dfDate.parse(todayDate)))
            {
                b = false;//If two dates are equal
            }
            else
            {
                b = false; //If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    public static String getDateFromString(String date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = f.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = "";
        if(d != null) {
            dateString = f2.format(d);
        } else {
            dateString = date;
        }
        System.out.println(dateString);
        return dateString;
    }

    public static boolean allowScreenshot() {
        if (ApplicationSettings.containsPref(AppConstants.ALLOW_SCREENSHOT)) {
            boolean allowScreenshot = ApplicationSettings.getPref(AppConstants.ALLOW_SCREENSHOT, false);
            if (allowScreenshot) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean allowASF() {
        if (ApplicationSettings.containsPref(AppConstants.ASF)) {
            boolean allowASF = ApplicationSettings.getPref(AppConstants.ASF, false);
            if (allowASF) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean allowChatBot(){
        if(ApplicationSettings.containsPref(AppConstants.ALLOW_CHAT_BOT)){
            boolean allowChatBot = ApplicationSettings.getPref(AppConstants.ALLOW_CHAT_BOT, false);
            if (allowChatBot) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static String optionsChatbot(){
        String optionsChatbot = "";
        if(ApplicationSettings.containsPref(AppConstants.OPTIONS_CHAT_BOT)){
            optionsChatbot = ApplicationSettings.getPref(AppConstants.OPTIONS_CHAT_BOT, "");
        }
        return optionsChatbot;
    }

    public static String chatbotURL(){
        String urlChatbot = "";
        if(ApplicationSettings.containsPref(AppConstants.CHAT_BOT_URL)){
            urlChatbot = ApplicationSettings.getPref(AppConstants.CHAT_BOT_URL, "");
        }
        return urlChatbot;
    }

    public static boolean chatC2C(){
        if (ApplicationSettings.containsPref(AppConstants.CHAT_C2C)) {
            boolean chatC2C = ApplicationSettings.getPref(AppConstants.CHAT_C2C, false);
            if (chatC2C) {
                return true;
            } else {
                return false;
            }
        }
        return false;

    }

    public static boolean allowPMC2C(){
        if (ApplicationSettings.containsPref(AppConstants.ALLOW_PM_C2C)) {
            boolean allowPMC2C = ApplicationSettings.getPref(AppConstants.ALLOW_PM_C2C, false);
            if (allowPMC2C) {
                return true;
            } else {
                return false;
            }
        }
        return false;

    }

    public static void chatMakeCall(final Context context, final String number,final long dbid, final String appointmentId,final String status, final  String substatus1, final  String substatus2, final String callername, final String notes) {
        if(context != null) {
            boolean secondSim = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
                List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

                //Log.d("Test", "Current list = " + subsInfoList);
                if (subsInfoList != null) {
                    for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                        String number1 = subscriptionInfo.getNumber();
                        int SimStotNumber = subscriptionInfo.getSimSlotIndex();
                        if (SimStotNumber == 1) {
                            secondSim = true;
                        }

                        //Log.d("Test", " Number is  " + number);
                    }
                } else {
                    Toast.makeText(context, "No SIM", Toast.LENGTH_SHORT).show();
                }
            } else {
                secondSim = true;
            }

            /*boolean mobileOnSimData = false;
            if (ApplicationSettings.containsPref(AppConstants.MOBILE_DATA_ON_SIM)) {
                mobileOnSimData = ApplicationSettings.getPref(AppConstants.MOBILE_DATA_ON_SIM, false);
            }*/

            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                /*if ((!secondSim && !(mobileOnSimData)) || (secondSim && mobileOnSimData)) {*/
                if (number != null) {
                    String userNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                    if (userNumber != null && !(userNumber.isEmpty())) {
                        final String customernumber = number;
                        String toNumber = "";
                        if(number != null && !number.isEmpty() && !number.startsWith("+91")) {
                            toNumber = "+91" + number;
                        } else {
                            toNumber = number;
                        }
                        String sr_number = ApplicationSettings.getPref(AppConstants.SR_NUMBER, "");
                        String caller_id = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");
                        if (caller_id != null && !(caller_id.isEmpty())) {
                            if (sr_number != null && !(sr_number.isEmpty())) {
                                Log.e("CommonUtils","Data :: "+sr_number + " :: "+userNumber + " :: "+toNumber);
                                KnowlarityModel knowlarityModel = new KnowlarityModel(sr_number, userNumber, toNumber);
                                knowlarityModel.setClient_id(caller_id);
                                if (CommonUtils.isC2cNetworkAvailable(context)) {
                                    //NotificationData.appointment_db_id = dbid;
                                    new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
                                        @Override
                                        public void onComplete(String data, long request_code, int failure_code) {
                                            if (data != null && !(data.isEmpty())) {
                                                Log.e("CommonUtils","data :: "+data);
                                                if (data.contains("_SMBALERT_")) {
                                                    Log.e("CommonUtils","3");
                                                    CommonUtils.setToast(context,data.replace("_SMBALERT_", ""));
                                                } else if (data.contains("_SMBACP_")) {

                                                    Log.e("CommonUtils","2");
                                                    Toast.makeText(context, "Connecting to the customer. Please wait ...", Toast.LENGTH_LONG).show();
                                                } else {

                                                    Log.e("CommonUtils","1");
                                                    Toast.makeText(context, "Connecting to the customer. Please wait ...", Toast.LENGTH_LONG).show();
                                                }

                                            } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                                                if (context != null) {
                                                    CommonUtils.setToast(context, "Invalid Auth key");
                                                }
                                            } else if (failure_code == APIProvider.INVALID_REQUEST) {
                                                if (context != null) {
                                                    CommonUtils.setToast(context, "Request Not Allowed");
                                                }
                                            } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                                                if (context != null) {
                                                    CommonUtils.setToast(context, "Invalid Parameters");
                                                }
                                            } else if (failure_code == APIProvider.INVALID_NUMBER) {
                                                if (context != null) {
                                                    CommonUtils.setToast(context, "This number is not valid");
                                                }
                                            } else if (failure_code == APIProvider.SERVER_ALERT) {
                                                Toast.makeText(context, APIProvider.SERVER_ALERT_MESSAGE, Toast.LENGTH_LONG).show();
                                            } else if (failure_code == APIProvider.DND) {

                                                Toast.makeText(context, "This number is NDNC.", Toast.LENGTH_LONG).show();
                                            } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                                                Toast.makeText(context, "Call Failed!", Toast.LENGTH_LONG).show();

                                            } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                                                Toast.makeText(context, "Hey! Ask your admin to call Support at 9113907215.", Toast.LENGTH_LONG).show();
                                            } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                                                Toast.makeText(getApplicationContext(), "Number not registered for outbound services for this agent", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }).reClickToCall(knowlarityModel);
                                    Toast toastMessage = Toast.makeText(context, "Dialling", Toast.LENGTH_LONG);
                                    toastMessage.setGravity(Gravity.CENTER, 0, 0);
                                    toastMessage.show();
                                } else {
                                    Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "No SR Number", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "No Client ID", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Invalid User Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Invalid Customer Number", Toast.LENGTH_SHORT).show();
                }
                /*} else {
                    callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                }*/
            } else {
                Log.e("CommonUtils","4");
                callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
            }
        }
    }

    public static boolean allowGroupChatCall(){
        if(ApplicationSettings.containsPref(AppConstants.ALLOW_GROUP_CHAT_CALL)){
            boolean allowGroupChatCall = ApplicationSettings.getPref(AppConstants.ALLOW_GROUP_CHAT_CALL, false);
            if (allowGroupChatCall) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
