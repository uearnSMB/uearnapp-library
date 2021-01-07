package smarter.uearn.money.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import smarter.uearn.money.R;
import smarter.uearn.money.models.NetworkDetails;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.LogUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.views.events.HeadPhoneReciever;
import smarter.uearn.money.views.events.NetworkBroadcastReceiver;

public class DeviceCheckCompleteActivity extends AppCompatActivity implements View.OnClickListener {

    private Button nextButton;
    public static String currentSelectedCheckBoxValue = "";
    public static int noOfCheckboxChecked = 0;
    public static int noneOfTheAboveChecked = 0;
    ImageView deviceCheck, wifiTickImage, headsetTickImage, internetspeedTickImage;
    private TextView internetspeed;
    private TextView headerTitle;
    private ImageView profileImage, image_sync, image_notification;
    private LinearLayout notificationLayout;
    private NetworkBroadcastReceiver networkBroadcastReceiver = new NetworkBroadcastReceiver();
    boolean isWIFIConnected, isHaedsetConnected, isNetSpeedGood;

    double BYTE_TO_KILOBIT = 0.008;
    double KILOBIT_TO_MEGABIT = 0.001;
    //========================================================================

    BluetoothHeadset bluetoothHeadset;
    // Get the default adapter
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    boolean isVoiceTestDone;
    String processSelected = "";


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                validateHeadsetConnection(true);
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                validateHeadsetConnection(false);
            }
        }
    };

    private BluetoothProfile.ServiceListener profileListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = (BluetoothHeadset) proxy;
                try {
                    if (bluetoothHeadset.getConnectedDevices().size() > 0) {
                        SmarterSMBApplication.isHeadPhoneConnected = true;
                        validateHeadsetConnection(true);
                    } else {
                        if (SmarterSMBApplication.isHeadPhoneConnected) {
                            validateHeadsetConnection(true);
                        } else {
                            validateHeadsetConnection(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET) {
                try {
                    SmarterSMBApplication.isHeadPhoneConnected = false;
                    validateHeadsetConnection(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    //=========================================================================

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");

            if (result != null && !result.isEmpty() && result.equals("Headset is plugged")) {
                validateHeadsetConnection(true);
            } else if (result != null && !result.isEmpty() && result.equals("Headset is unplugged")) {
                validateHeadsetConnection(false);
            }

            if (result != null && !result.isEmpty() && result.equals("internet_on") && isWifiConnected()) {
                validateWifiConnection(true);
            } else if (result != null && !result.isEmpty() && result.equals("internet_off") && !isWifiConnected()) {
                validateWifiConnection(false);
            }
        }
    };

    //=========================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_check_complete);
        changeStatusBarColor(this);
        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("Device Check");
        profileImage = (ImageView) findViewById(R.id.profile_image_back);
        profileImage.setBackgroundResource(R.drawable.ic_arrow_back_white_24px);
        profileImage.setOnClickListener(this);
        notificationLayout = (LinearLayout) findViewById(R.id.ll_notification);
        notificationLayout.setOnClickListener(this);

        image_sync = findViewById(R.id.image_sync);
        image_notification = findViewById(R.id.image_notification);
        image_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    settingsApi(userId);
                    Animation rotation = AnimationUtils.loadAnimation(DeviceCheckCompleteActivity.this, R.anim.button_rotate);
                    rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                    image_sync.startAnimation(rotation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        deviceCheck = (ImageView) findViewById(R.id.devicecheck);
        wifiTickImage = (ImageView) findViewById(R.id.wifiTickImage);
        headsetTickImage = (ImageView) findViewById(R.id.headsetTickImage);
        internetspeedTickImage = (ImageView) findViewById(R.id.internetspeedTickImage);
        internetspeed = (TextView) findViewById(R.id.internetspeed);

        ApplicationSettings.putPref(AppConstants.DEVICE_CHECK_COMPLETED, true);
        try {
            processSelected = ApplicationSettings.getPref(AppConstants.INTERVIEW_PROCESS, "");
            isVoiceTestDone = ApplicationSettings.getPref(AppConstants.VOICE_TEST_COMPLETED, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        nextButton = findViewById(R.id.btnNext);
        nextButton.setOnClickListener(this);
        nextButton.setText("NEXT");
        nextButton.setTextSize(20);
        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
        disableNextButton();

        String internetSpeed = ApplicationSettings.getPref(AppConstants.WIFI_SPEED, "");
        if (internetSpeed != null && !internetSpeed.isEmpty()) {
            internetspeed.setText(internetSpeed);
        }

        registerNetWorkBroadcastReceiver();

        try {
            if (mMessageReceiver != null) {
                LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("remote-dialer-request-event"));
            }
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "mMessageReceiver Reg Error" + e.getMessage());
        }

        try {
            if (bluetoothAdapter != null) {
                // Establish connection to the proxy.
                bluetoothAdapter.getProfileProxy(this, profileListener, BluetoothProfile.HEADSET);
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
                filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                this.registerReceiver(BTReceiver, filter);
            }
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "bluetoothAdapter Reg Error" + e.getMessage());
        }

        if ((SmarterSMBApplication.isHeadPhoneConnected || isBluetoothHeadsetConnected())) {
            headsetTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_wifi_tick));
        } else {
            headsetTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_reconnect));
        }
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    private void registerNetWorkBroadcastReceiver() {
        try {
            IntentFilter networkBroadcastFilter = new IntentFilter();
            networkBroadcastFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(networkBroadcastReceiver, networkBroadcastFilter);
        } catch (Exception e) {
            LogUtils.e("DeviceCheckActivity:" + "registerReceiver networkBroadcastReceiver" + e.getMessage());
        }
    }

    public void changeStatusBarColor(AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            this.finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnNext) {
            boolean deviceCheckCompleted = ApplicationSettings.getPref(AppConstants.DEVICE_CHECK_COMPLETED, false);
            boolean voiceTestCompleted = ApplicationSettings.getPref(AppConstants.VOICE_TEST_COMPLETED, false);
            boolean emailTestCompleted = ApplicationSettings.getPref(AppConstants.GRAMMAR_TEST_COMPLETED, false);
            boolean chatTestCompleted = ApplicationSettings.getPref(AppConstants.PROCESS_SELECTION_CHAT_COMPLETED, false);
            boolean callFromInterviewPanelCompleted = ApplicationSettings.getPref(AppConstants.CALL_FROM_INTERVIEW_PANEL_COMPLETED, false);
            boolean onboardingProcessCompleted = ApplicationSettings.getPref(AppConstants.ONBOARDING_PROCESS_COMPLETED, false);
            JSONObject processStatusObj = null;

            try {
                processStatusObj = new JSONObject();
                processStatusObj.put("device_check_completed", deviceCheckCompleted);
                processStatusObj.put("voice_test_completed", voiceTestCompleted);
                processStatusObj.put("email_test_completed", emailTestCompleted);
                processStatusObj.put("chat_test_completed", chatTestCompleted);
                processStatusObj.put("call_from_interview_panel_completed", callFromInterviewPanelCompleted);
                processStatusObj.put("onboarding_process_completed", onboardingProcessCompleted);

            } catch (Exception e) {
                e.printStackTrace();
            }

            ApplicationSettings.putPref(AppConstants.PROCESS_STATUS, processStatusObj.toString());
            postCurrentProcessStatus(processStatusObj.toString());

            if (SmarterSMBApplication.currentSelectedProcess != null && !SmarterSMBApplication.currentSelectedProcess.isEmpty() && SmarterSMBApplication.currentSelectedProcess.equalsIgnoreCase("Email")) {
                SmarterSMBApplication.currentSelectedProcess = "";
                navigateToGrammarSkillTestActivity();
            } else {
                navigateToVoiceTestActivity();
            }
        } else if (id == R.id.profile_image_back) {
            onBackPressed();
        } else if (id == R.id.ll_notification) {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            settingsApi(userId);
        }
    }

    private void navigateToGrammarSkillTestActivity() {
        Intent intent = new Intent(getApplicationContext(), GrammarSkillTestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("isFrom", true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    JSONObject processStatusResponseObj;

    private void postCurrentProcessStatus(String processSelection) {
        if (CommonUtils.isNetworkAvailable(this)) {
            final String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            final JSONObject userDetailObj = new JSONObject();
            try {
                userDetailObj.put("interview_process", processSelection);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new AsyncTask<Void, Void, JSONObject>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    String url = Urls.getUpdateUserDetailsUrl(user_id);
                    processStatusResponseObj = DataUploadUtils.putJsonData(url, userDetailObj);
                    return processStatusResponseObj;
                }

                @Override
                protected void onPostExecute(JSONObject jObject) {
                    super.onPostExecute(jObject);
                    try {
                        if ((processStatusResponseObj != null) && (processStatusResponseObj.getString("success") != null)) {

                        }
                    } catch (JSONException e) {

                    }
                }
            }.execute();
        } else {
            Toast.makeText(this, "You have no internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        SmarterSMBApplication.setCurrentActivity(this);
        try {
            registerNetWorkBroadcastReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mMessageReceiver = new HeadPhoneReciever();
            IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(mMessageReceiver, filter);
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "registerReceiver myReceiver" + e.getMessage());
        }

        try {
            if (bluetoothAdapter != null) {
                bluetoothAdapter.getProfileProxy(this, profileListener, BluetoothProfile.HEADSET);
                // Establish connection to the proxy.
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
                filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                this.registerReceiver(BTReceiver, filter);
            }
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "bluetoothAdapter Reg Error" + e.getMessage());
        }
        super.onResume();
    }

/*
    Causing some crashes some time, only unregistering receiver in onStop.
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mMessageReceiver != null) {
                unregisterReceiver(mMessageReceiver);
            }
            if (BTReceiver != null) {
                // Don't forget to unregister the ACTION_FOUND receiver.
                unregisterReceiver(BTReceiver);
            }
            if (bluetoothAdapter != null) {
                // Close proxy connection after use.
                bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    @Override
    protected void onPause() {
        try {
            if (mMessageReceiver != null) {
             //   LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
               unregisterReceiver(mMessageReceiver);
            }

            if (networkBroadcastReceiver != null) {
                unregisterReceiver(networkBroadcastReceiver);
            }

            if (bluetoothAdapter != null) {
                // Close proxy connection after use.
                bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset);
            }

            if (BTReceiver != null) {
                unregisterReceiver(BTReceiver);
            }

            super.onPause();// call this after the end of the unregistring Recivers
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = getInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            if (info.isConnected()) {
                return true;
            }
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (info.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public NetworkInfo getInfo() {
        return ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    private void navigateToVoiceTestActivity() {
        Intent intent = new Intent(getApplicationContext(), UearnVoiceTestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("isFrom", true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void settingsApi(final String userId) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.GetSettings(userId, 22, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        }
    }

/*
    private void checkWiFiSpeed() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            Integer linkSpeed = wifiInfo.getLinkSpeed();
            String wifiSpeed = String.valueOf(linkSpeed);
            if (wifiSpeed != null && !wifiSpeed.isEmpty() && !wifiSpeed.equals("null")) {
                ApplicationSettings.putPref(AppConstants.WIFI_SPEED, wifiSpeed + " Mbps");
                internetspeed.setText(wifiSpeed + " Mbps");
                try {
                    NetworkDetails networkDetails = new NetworkDetails();
                    networkDetails.setNetwork("WIFI");
                    networkDetails.setNetwork_speed(linkSpeed);
                    new APIProvider.PostRequestForNetworkDetails(networkDetails, 1, DeviceCheckCompleteActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                        @Override
                        public void onComplete(String data, long request_code, int failure_code) {
                            if (data != null && !data.isEmpty()) {
                                String type = "";
                                String message = "";
                                String description = "";
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    if (jsonObject.has("type")) {
                                        type = jsonObject.getString("type");
                                    }
                                    if (jsonObject.has("message")) {
                                        message = jsonObject.getString("message");
                                    }
                                    if (jsonObject.has("description")) {
                                        description = jsonObject.getString("description");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    validateConnectionSpeed(false, "Bad", "00");
                                }
                                if (type != null && !type.isEmpty() && type.equalsIgnoreCase("Bad")) {
                                    validateConnectionSpeed(false, "Bad", wifiSpeed);

                                } else if (type != null && !type.isEmpty() && type.equalsIgnoreCase("Good")) {
                                    validateConnectionSpeed(true, "Good", wifiSpeed);
                                }

                            }
                            Log.e("TrainerDetails", "failure_code  :: " + failure_code);
                        }
                    }).call();
                } catch (Exception e) {
                    e.printStackTrace();
                    validateConnectionSpeed(false, "Bad", "00");
                }
            } else {
                validateConnectionSpeed(false, "Bad", "00");
            }
        }
    }
*/

    //========================Internet Speed check===================

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkWiFiSpeed() {
        NetworkInfo info = getInfo();
        NetworkDetails networkDetails = new NetworkDetails();
        if (info == null || !info.isConnected()) {
            validateConnectionSpeed(false, "Bad");
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            if (info.isConnected()) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    Integer linkSpeed = wifiInfo.getLinkSpeed();
                    String wifiSpeed = String.valueOf(linkSpeed);
                    if (wifiSpeed != null && !wifiSpeed.isEmpty() && !wifiSpeed.equals("null")) {
                        ApplicationSettings.putPref(AppConstants.WIFI_SPEED, wifiSpeed + " Mbps");
                        internetspeed.setText(wifiSpeed + " Mbps");
                        networkDetails.setNetwork("WIFI");
                        networkDetails.setNetwork_speed(linkSpeed);
                    }
                }
            }

        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (info.isConnected()) {
                ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
                NetworkCapabilities nc = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                }

                Integer downSpeed = nc.getLinkDownstreamBandwidthKbps();
                Integer upSpeed = nc.getLinkUpstreamBandwidthKbps();
                double kiloBits = downSpeed * BYTE_TO_KILOBIT;
                double megaBits = kiloBits * KILOBIT_TO_MEGABIT;
                double mbpsSpeed = Math.round(megaBits);
                Double doubleSpeed = mbpsSpeed;
                Integer mbpsSpeedFinal = doubleSpeed.intValue();
                ApplicationSettings.putPref(AppConstants.WIFI_SPEED, mbpsSpeed + " Mbps");
                internetspeed.setText(mbpsSpeedFinal + " Mbps");
                networkDetails.setNetwork("MOBILEDATA");
                networkDetails.setNetwork_speed(mbpsSpeedFinal);
            }
        }
        try {
            new APIProvider.PostRequestForNetworkDetails(networkDetails, 1, DeviceCheckCompleteActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        String type = "";
                        String message = "";
                        String description = "";
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.has("type")) {
                                type = jsonObject.getString("type");
                            }
                            if (jsonObject.has("message")) {
                                message = jsonObject.getString("message");
                            }
                            if (jsonObject.has("description")) {
                                description = jsonObject.getString("description");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (type != null && !type.isEmpty() && type.equalsIgnoreCase("Bad")) {
                            validateConnectionSpeed(false, "Bad");

                        } else if (type != null && !type.isEmpty() && type.equalsIgnoreCase("Good")) {
                            validateConnectionSpeed(true, "Good");
                        }
                    }
                    Log.e("TrainerDetails", "failure_code  :: " + failure_code);
                }
            }).call();
        } catch (Exception e) {
            e.printStackTrace();
            validateConnectionSpeed(false, "Bad");
        }
    }
    //===============================================================


    //==================== Validate Internet connection Speed ========================================
    private void validateConnectionSpeed(boolean isGoodSpeed, String message) {
        if (isGoodSpeed) {
            internetspeedTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_wifi_tick));
            //   internetspeed.setText(speed + " Mbps");
            isNetSpeedGood = true;
        } else {
            internetspeedTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_reconnect));
            //  internetspeed.setText(speed + " Mbps");
            isNetSpeedGood = false;
        }

        if (!Utils.isStringEmpty(processSelected) && processSelected.contains("Voice") && !isVoiceTestDone) {
            if (isNetSpeedGood && isWIFIConnected && isHaedsetConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        } else {
            if (isNetSpeedGood && isWIFIConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        }
    }

//--------------------------Validate HeadSet Connection--------------------------------------------------

    private void validateHeadsetConnection(boolean isHeadsetConnected) {
        isHaedsetConnected = isHeadsetConnected;
        if (isHeadsetConnected) {
            SmarterSMBApplication.isHeadPhoneConnected = true;
            headsetTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_wifi_tick));
        } else {
            headsetTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_reconnect));
        }

        if (!Utils.isStringEmpty(processSelected) && processSelected.contains("Voice") && !isVoiceTestDone) {
            if (isNetSpeedGood && isWIFIConnected && isHaedsetConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        } else {
            if (isNetSpeedGood && isWIFIConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        }
    }

    //--------------------Validate Internet Connection---------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void validateWifiConnection(boolean isWifiConnected) {
        isWIFIConnected = isWifiConnected;
        checkWiFiSpeed();
        if (isWifiConnected) {
            SmarterSMBApplication.isHeadPhoneConnected = true;
            wifiTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_wifi_tick));
        } else {
            SmarterSMBApplication.isHeadPhoneConnected = false;
            wifiTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_reconnect));
            validateConnectionSpeed(false, "Bad");
            internetspeed.setText(00 + " Mbps");
        }

        if (!Utils.isStringEmpty(processSelected) && processSelected.contains("Voice") && !isVoiceTestDone) {
            if (isNetSpeedGood && isWIFIConnected && isHaedsetConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        } else {
            if (isNetSpeedGood && isWIFIConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        }
    }

    private void enableNextButton() {
        nextButton.setEnabled(true);
        nextButton.setClickable(true);
        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
    }

    private void disableNextButton() {
        nextButton.setEnabled(false);
        nextButton.setClickable(false);
        nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
    }

}
