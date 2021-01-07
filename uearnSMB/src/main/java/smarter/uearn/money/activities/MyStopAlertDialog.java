package smarter.uearn.money.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.text.HtmlCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import smarter.uearn.money.R;
import smarter.uearn.money.agentslots.activity.AgentSlotsActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class MyStopAlertDialog extends Activity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    TextToSpeech mTts = null;
    String strMessage = "";
    String ttsenable = "no";
    String strTitle = "";
    String signOut = "";
    String signIn = "";
    String availableTill = "";
    String reqtype = "start_request";
    String userPreference = "";
    String action = "";
    String posButton = "OK";
    String negButton = "NOT NOW";

    private void sendMessageToSmbHome() {
        Log.d("PushNotification", "sendMessageToSmbHome ");
        Intent intent = new Intent("remote-dialer-request-event");
        intent.putExtra("result", reqtype);
        intent.putExtra("usermessage", availableTill);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dismissAlertDialog();
    }

    private void dismissAlertDialog() {
        String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
        if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
            if (UearnActivity.dismissAlertDialog) {
                UearnActivity.dismissAlertDialog = false;
                this.finish();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        final String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
        Intent intent = this.getIntent();

        if (intent.hasExtra("TITLE")) {
            strTitle = intent.getStringExtra("TITLE");
        }

        if (intent.hasExtra("SIGNOUT")) {
            signOut = intent.getStringExtra("SIGNOUT");
        }

        if (intent.hasExtra("SIGNIN")) {
            signIn = intent.getStringExtra("SIGNIN");
        }

        if (intent.hasExtra("TTSENABLE")) {
            ttsenable = intent.getStringExtra("TTSENABLE");
            if (ttsenable.equals("yes")) {
                mTts = new TextToSpeech(this, this);
            }
        }

        if (intent.hasExtra("MESSAGE")) {
            strMessage = intent.getStringExtra("MESSAGE");
            AlertDialog alertDialog1 = null;
            String showCancel = "no";
            if (intent.hasExtra("SHOWCANCEL")) {
                showCancel = intent.getStringExtra("SHOWCANCEL");
            }
            if (!strTitle.equals("NO DISPLAY")) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MyStopAlertDialog.this).create();
                alertDialog.setTitle(strTitle);
                alertDialog.setCancelable(false);
                setFinishOnTouchOutside(false);
                if (showCancel.equals("yes")) {
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    Log.d("MyAlertDialog", "Clicked OK");
                                    alertDialog.dismiss();
                                    String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                                    if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                                        Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }
                                    finish();
                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_CANCELED, returnIntent);
                                    Log.d("MyAlertDialog", "Clicked CANCEL");
                                    alertDialog.dismiss();
                                    finish();
                                }
                            });
                } else {

                    LayoutInflater inflater = LayoutInflater.from(this);
                    View contentView = inflater.inflate(R.layout.alert_campaign_change_dialog, null,false);
                    Button yesBtn = contentView.findViewById(R.id.yesBtn);
                    Typeface titlef = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
                    TextView tv_header = contentView.findViewById(R.id.tv_title);
                    tv_header.setTypeface(titlef);
                    WebView tv_description = contentView.findViewById(R.id.tv_message);
                    //tv_description.setTypeface(titlef);
                    tv_header.setText(strTitle);
                    if (strMessage.startsWith("http://") || strMessage.startsWith("https://")) {
                        tv_description.loadUrl(strMessage);
                    } else {
                        tv_description.loadDataWithBaseURL(null, strMessage, "text/html", "utf-8", null);
                    }
                    Typeface titleFace = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
                    yesBtn.setTypeface(titleFace);
                    alertDialog1 = new AlertDialog.Builder(MyStopAlertDialog.this).setView(contentView).create();

                    //layout.addView(spinner_stop);
                    alertDialog1.setCancelable(false);
                    alertDialog1.show();

                    AlertDialog finalAlertDialog = alertDialog1;
                    yesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            Log.d("MyAlertDialog", "Clicked OK");
                            finalAlertDialog.dismiss();
                            String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                            if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                                Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            }
                            finish();
                        }
                    });
                    /*alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    Log.d("MyAlertDialog", "Clicked OK");
                                    alertDialog.dismiss();
                                    String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                                    if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                                        Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }
                                    finish();
                                }
                            });*/
                }

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                WebView webView = new WebView(alertDialog1.getContext());
                if (webView != null) {
                    webView.setWebViewClient(new WebViewClient());
                    WebSettings settings = webView.getSettings();
                    if (settings != null) {
                        try {
                            webView.clearCache(true);
                            webView.clearHistory();
                            settings.setJavaScriptEnabled(true);
                            settings.setLoadWithOverviewMode(true);
                            settings.setUseWideViewPort(true);
                            settings.setDomStorageEnabled(true);
                        } catch (Exception e) {

                        }
                    }
                }

                if (strMessage.startsWith("http://") || strMessage.startsWith("https://")) {
                    webView.loadUrl(strMessage);
                } else {
                    webView.loadDataWithBaseURL(null, strMessage, "text/html", "utf-8", null);
                }

                layout.addView(webView);
                alertDialog1.setView(layout);
                if (alertDialog1 != null) {
                    alertDialog1.show();
                }
            }
        }

        if (intent.hasExtra("RDMESSAGE")) {
            strMessage = intent.getStringExtra("RDMESSAGE");
            action = intent.getStringExtra("RDACTION");
            if (action.equals("START") || action.equals("START_PUSH")) {
                userPreference = ApplicationSettings.getPref(AppConstants.USER_POSITIVE_PREFERENCES, "Ready to take any call, Ready for followups only");
            } else {
                reqtype = "stop_request";
                posButton = "YES";
                negButton = "CANCEL";
                userPreference = ApplicationSettings.getPref(AppConstants.USER_NEGATIVE_PREFERENCES, "Stepping out, Low Mobile Signal, Poor Broadband, End of Work");
            }

            LayoutInflater inflater = LayoutInflater.from(this);
            View contentView = inflater.inflate(R.layout.alert_stop_dialog, null,false);
            final Spinner spinner_stop = contentView.findViewById(R.id.spinner_stop);
            Button cancelBtn = contentView.findViewById(R.id.cancelBtn);
            Button yesBtn = contentView.findViewById(R.id.yesBtn);
            Typeface titlef = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
            TextView tv_header = contentView.findViewById(R.id.tv_header);
            tv_header.setTypeface(titlef);
            TextView tv_description = contentView.findViewById(R.id.tv_description);
            tv_description.setTypeface(titlef);
            tv_header.setText(strTitle);
            tv_description.setText(strMessage);
            Typeface titleFace = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
            cancelBtn.setTypeface(titleFace);
            yesBtn.setTypeface(titleFace);
            final AlertDialog alertDialog = new AlertDialog.Builder(MyStopAlertDialog.this).setView(contentView).create();
            LinearLayout layout = new LinearLayout(this);
            //final Spinner spinner = new Spinner(this);
            List<String> timeArray = Arrays.asList(userPreference.split("\\s*,\\s*"));
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_stop.setAdapter(spinnerArrayAdapter);
            layout.setOrientation(LinearLayout.VERTICAL);
            //layout.addView(spinner_stop);
            alertDialog.setCancelable(false);
            alertDialog.show();
            spinner_stop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                int count = 0;

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    availableTill = spinner_stop.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                        boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                        if (ibControl) {
                            UearnHome.remoteAutoDialingStarted = true;
                        }
                    }
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", reqtype);
                    returnIntent.putExtra("usermessage", availableTill);
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    alertDialog.dismiss();
                    finish();
                    if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                        boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                        if (ibControl) {
                            if(SmarterSMBApplication.stopRequestFromUearnHome){
                                navigateToUearnHome(true);
                            }
                        }
                    }
                    if(remoteAutoEnabled.equals("onsolicit")){
                        if(SmarterSMBApplication.stopRequestFromUearnHome){
                            navigateToUearnHome(true);
                        }
                    }
                }
            });
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    availableTill = spinner_stop.getSelectedItem().toString();
                    if (action.equals("START_PUSH") || action.equals("STOP_PUSH")) {
                        sendMessageToSmbHome();
                    } else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", reqtype);
                        returnIntent.putExtra("usermessage", availableTill);
                        setResult(Activity.RESULT_OK, returnIntent);
                    }
                    alertDialog.dismiss();
                    finish();
                    if (ApplicationSettings.containsPref(AppConstants.IB_CONTROL)) {
                        boolean ibControl = ApplicationSettings.getPref(AppConstants.IB_CONTROL, false);
                        if (ibControl) {
                            if(SmarterSMBApplication.stopRequestFromUearnHome){
                                SmarterSMBApplication.stopRequestMsg = availableTill;
                                if(SmarterSMBApplication.stopRequestMsg == null || SmarterSMBApplication.stopRequestMsg.isEmpty()){
                                    SmarterSMBApplication.stopRequestMsg = "Stepping out";
                                }
                                SmarterSMBApplication.sendStopRequest = true;
                                navigateToUearnHome(false);
                            }
                        }
                    }
                    /*if(remoteAutoEnabled.equals("onsolicit")){
                        Log.e("MyStopAlertDialog","Comming to onSolicit");
                        Log.e("MyStopAlertDialog","stopRequestFromUearnHome :: "+SmarterSMBApplication.stopRequestFromUearnHome);
                        if(SmarterSMBApplication.stopRequestFromUearnHome){
                            SmarterSMBApplication.stopRequestMsg = availableTill;
                            if(SmarterSMBApplication.stopRequestMsg == null || SmarterSMBApplication.stopRequestMsg.isEmpty()){
                                SmarterSMBApplication.stopRequestMsg = "Stepping out";
                            }
                            SmarterSMBApplication.sendStopRequest = true;
                            navigateToUearnHome(false);
                        }
                    }*/
                }
            });
            /*
            spinner.setAdapter(spinnerArrayAdapter);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(spinner);

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negButton,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", reqtype);
                            returnIntent.putExtra("usermessage", availableTill);
                            setResult(Activity.RESULT_CANCELED, returnIntent);
                            alertDialog.dismiss();
                            finish();
                        }
                    });


            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, posButton,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            availableTill = spinner.getSelectedItem().toString();
                            if (action.equals("START_PUSH") || action.equals("STOP_PUSH")) {
                                sendMessageToSmbHome();
                            } else {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result", reqtype);
                                returnIntent.putExtra("usermessage", availableTill);
                                setResult(Activity.RESULT_OK, returnIntent);
                            }
                            alertDialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.setTitle(strTitle);
            alertDialog.setMessage(strMessage);
            alertDialog.setCancelable(false);
            setFinishOnTouchOutside(false);
            alertDialog.setView(layout);
            layout.setPadding(20, 5, 20, 60);
            if (alertDialog != null) {
                alertDialog.show();
            }*/
        }

        if (intent.hasExtra("NOTIFICATIONMSGG")) {
            strMessage = intent.getStringExtra("NOTIFICATIONMSGG");
            strTitle = intent.getStringExtra("TITLENOTIFICATION");
            Log.e("MySoptAlert","Messages :: "+strMessage + " :: "+strTitle +" :: "
                    +intent.getStringExtra("NOTIFICATIONACTION"));
            if(intent.getStringExtra("NOTIFICATIONACTION").equalsIgnoreCase("movetoslot")){
                LayoutInflater inflater = LayoutInflater.from(this);
                View contentView = inflater.inflate(R.layout.alert_message_dialog, null,false);
                Button yesBtn = contentView.findViewById(R.id.yesBtn);
                Typeface titlef = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
                TextView tv_header = contentView.findViewById(R.id.tv_title);
                tv_header.setTypeface(titlef);
                TextView tv_description = contentView.findViewById(R.id.tv_message);
                tv_description.setTypeface(titlef);
                tv_header.setText(strTitle);
                tv_description.setText(strMessage);
                Typeface titleFace = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
                yesBtn.setTypeface(titleFace);
                final AlertDialog alertDialog = new AlertDialog.Builder(MyStopAlertDialog.this).setView(contentView).create();
                LinearLayout layout = new LinearLayout(this);
                //final Spinner spinner = new Spinner(this);

                layout.setOrientation(LinearLayout.VERTICAL);
                //layout.addView(spinner_stop);
                alertDialog.setCancelable(false);
                alertDialog.show();

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyStopAlertDialog.this, AgentSlotsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }else if(intent.getStringExtra("NOTIFICATIONACTION").equalsIgnoreCase("movetostopRAD")){
                LayoutInflater inflater = LayoutInflater.from(this);
                View contentView = inflater.inflate(R.layout.alert_message_dialog, null,false);
                Button yesBtn = contentView.findViewById(R.id.yesBtn);
                Typeface titlef = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
                TextView tv_header = contentView.findViewById(R.id.tv_title);
                tv_header.setTypeface(titlef);
                TextView tv_description = contentView.findViewById(R.id.tv_message);
                tv_description.setTypeface(titlef);
                tv_header.setText(strTitle);
                tv_description.setText(strMessage);
                Typeface titleFace = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
                yesBtn.setTypeface(titleFace);
                final AlertDialog alertDialog = new AlertDialog.Builder(MyStopAlertDialog.this).setView(contentView).create();
                LinearLayout layout = new LinearLayout(this);
                //final Spinner spinner = new Spinner(this);

                layout.setOrientation(LinearLayout.VERTICAL);
                //layout.addView(spinner_stop);
                alertDialog.setCancelable(false);
                alertDialog.show();

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        navigateToUearnHome(false);
                    }
                });
            }
            else{

                LayoutInflater inflater = LayoutInflater.from(this);
                View contentView = inflater.inflate(R.layout.alert_message_dialog, null,false);
                Button yesBtn = contentView.findViewById(R.id.yesBtn);
                Typeface titlef = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
                TextView tv_header = contentView.findViewById(R.id.tv_title);
                tv_header.setTypeface(titlef);
                TextView tv_description = contentView.findViewById(R.id.tv_message);
                tv_description.setTypeface(titlef);
                tv_header.setText(strTitle);
                tv_description.setText(strMessage);
                Typeface titleFace = Typeface.createFromAsset(getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
                yesBtn.setTypeface(titleFace);
                final AlertDialog alertDialog = new AlertDialog.Builder(MyStopAlertDialog.this).setView(contentView).create();
                LinearLayout layout = new LinearLayout(this);
                //final Spinner spinner = new Spinner(this);

                layout.setOrientation(LinearLayout.VERTICAL);
                //layout.addView(spinner_stop);
                alertDialog.setCancelable(false);
                alertDialog.show();

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }

        }
    }

    private void signOutOnly() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        WebStorage webstorage = WebStorage.getInstance();
        webstorage.deleteAllData();
        SmarterSMBApplication.appSettings.setLogout(true);

        ApplicationSettings.putPref(AppConstants.SUPERVISOR_EMAIL, "");
        ApplicationSettings.putPref(AppConstants.CLOUD_TELEPHONY, "");
        ApplicationSettings.putPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
        ApplicationSettings.putPref(AppConstants.DISABLE_CALL_REC_BUTTONS, false);
        ApplicationSettings.putPref(AppConstants.APP_UPDATE, false);

        Intent intent3 = new Intent(this, UearnHome.class);
        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent3.putExtra("EXIT", true);
        startActivity(intent3);
    }

    @Override
    protected void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }

        if (signOut != null && !signOut.isEmpty()) {
            signOutOnly();
            signOut = "";
        }

        if (signIn != null && !signIn.isEmpty()) {
            signIn = "";
            Intent loginIntent = new Intent(this, SmarterAuthActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.putExtra("screenType", 0);
            startActivity(loginIntent);
        }

        super.onDestroy();
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            mTts.setOnUtteranceCompletedListener(this);
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
                if (strMessage == null || "".equals(strMessage)) {
                } else {
                    HashMap<String, String> myHashAlarm = new HashMap<String, String>();
                    myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
                    myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
                    mTts.speak(strMessage, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
                }
            }
        }
    }

    public void onUtteranceCompleted(String utteranceId) {
    }

    private void navigateToUearnHome(boolean value) {
        if(value == true){
            UearnHome.remoteAutoDialingStarted = true;
            SmarterSMBApplication.endTheSession = true;
        } else {
            UearnHome.remoteAutoDialingStarted = false;
        }
        Intent intent = new Intent (this, UearnHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.overridePendingTransition(0, 0);
        startActivity(intent);
        this.finish();
    }
}