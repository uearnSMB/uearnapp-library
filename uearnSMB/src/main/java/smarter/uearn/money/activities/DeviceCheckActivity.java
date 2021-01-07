package smarter.uearn.money.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import java.util.ArrayList;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;
import smarter.uearn.money.views.events.HeadPhoneReciever;
import smarter.uearn.money.views.events.NetworkBroadcastReceiver;

public class DeviceCheckActivity extends AppCompatActivity implements View.OnClickListener {

    private Button nextButton;
    List<String> checkboxValues = new ArrayList<>();
    public static String currentSelectedCheckBoxValue = "";
    public static int noOfCheckboxChecked = 0;
    public static int noneOfTheAboveChecked = 0;
    ImageView deviceCheck, wifiTickImage, headsetTickImage;
    private TextView wifi, wifitick, headset, headsettick;

    private NetworkBroadcastReceiver networkBroadcastReceiver = new NetworkBroadcastReceiver();
    private Activity activity;
    private boolean wifiConnected = false;
    private LinearLayout deviceCheckTextLayout;
    private CardView deviceCardView;
    private TextView headerTitle;
    private ImageView profileImage, image_sync, image_notification;
    private LinearLayout notificationLayout;

    BluetoothHeadset bluetoothHeadset;
    // Get the default adapter
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    boolean isWIFIConnected, isHaedsetConnected;
    String processSelected = "";
    boolean isVoiceTestDone;


    //===========================================================================================================
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
    //========================================================================

    //--------------------------Validate HeadSet Connection--------------------------------------------------

    private void validateHeadsetConnection(boolean isHeadsetConnected) {
        isHaedsetConnected = isHeadsetConnected;
        if (isHeadsetConnected) {
            SmarterSMBApplication.isHeadPhoneConnected = true;
            headset.setText("Headphone with mic");
            headsettick.setText("Connected");
            headsettick.setTextColor(ContextCompat.getColor(activity, R.color.connected));
            headsetTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_wifi_tick));
        } else {
            SmarterSMBApplication.isHeadPhoneConnected = false;
            headset.setText("Headphone & Mic not connected");
            headsettick.setText("Reconnect");
            headsettick.setTextColor(ContextCompat.getColor(activity, R.color.reconnect));
            headsetTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_reconnect));
        }

        if (!Utils.isStringEmpty(processSelected) && processSelected.contains("Voice") && !isVoiceTestDone) {
            if (isWIFIConnected && isHaedsetConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        } else {
            if (isWIFIConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        }

        if (wifiConnected && (SmarterSMBApplication.isHeadPhoneConnected || isBluetoothHeadsetConnected())) {
            deviceCardView.setVisibility(View.VISIBLE);
            deviceCheckTextLayout.setVisibility(View.GONE);
        } else {
            deviceCardView.setVisibility(View.GONE);
            deviceCheckTextLayout.setVisibility(View.VISIBLE);
        }
    }

    //--------------------Validate Internet Connection---------------------------------------
    private void validateWifiConnection(boolean isWifiConnected) {
        isWIFIConnected = isWifiConnected;
        if (isWifiConnected) {
            SmarterSMBApplication.isWifiConnected = true;
            wifi.setText("Broadband WiFi Connection");
            wifitick.setText("Connected");
            wifitick.setTextColor(ContextCompat.getColor(activity, R.color.connected));
            wifiTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_wifi_tick));
        } else {
            SmarterSMBApplication.isWifiConnected = false;
            wifi.setText("Not connected with WiFi");
            wifitick.setText("Reconnect");
            wifitick.setTextColor(ContextCompat.getColor(activity, R.color.reconnect));
            wifiTickImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_reconnect));
        }

        if (!Utils.isStringEmpty(processSelected) && processSelected.contains("Voice") && !isVoiceTestDone) {
            if (isWIFIConnected && isHaedsetConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        } else {
            if (isWIFIConnected) {
                enableNextButton();
            } else {
                disableNextButton();
            }
        }


        if (wifiConnected && (SmarterSMBApplication.isHeadPhoneConnected || isBluetoothHeadsetConnected())) {
            deviceCardView.setVisibility(View.VISIBLE);
            deviceCheckTextLayout.setVisibility(View.GONE);
        } else {
            deviceCardView.setVisibility(View.GONE);
            deviceCheckTextLayout.setVisibility(View.VISIBLE);
        }
    }
    //---------------------------------------------------

    private void registerNetWorkBroadcastReceiver() {
        try {
            IntentFilter networkBroadcastFilter = new IntentFilter();
            networkBroadcastFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(networkBroadcastReceiver, networkBroadcastFilter);
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "registerReceiver networkBroadcastReceiver" + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_check);

        changeStatusBarColor(this);
        activity = this;
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
                    Animation rotation = AnimationUtils.loadAnimation(DeviceCheckActivity.this, R.anim.button_rotate);
                    rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                    image_sync.startAnimation(rotation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            processSelected = ApplicationSettings.getPref(AppConstants.INTERVIEW_PROCESS, "");
            isVoiceTestDone = ApplicationSettings.getPref(AppConstants.VOICE_TEST_COMPLETED, false);
        } catch (Exception e) {
            e.printStackTrace();
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


        deviceCheck = (ImageView) findViewById(R.id.devicecheck);
        wifiTickImage = (ImageView) findViewById(R.id.wifiTickImage);
        headsetTickImage = (ImageView) findViewById(R.id.headsetTickImage);

        wifi = (TextView) findViewById(R.id.wifiText);
        wifitick = (TextView) findViewById(R.id.wifiTickText);
        headset = (TextView) findViewById(R.id.headsetText);
        headsettick = (TextView) findViewById(R.id.headsetTickText);

        wifitick.setPaintFlags(wifitick.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        headsettick.setPaintFlags(headsettick.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        deviceCheckTextLayout = (LinearLayout) findViewById(R.id.deviceCheckTextLayout);
        deviceCardView = (CardView) findViewById(R.id.deviceCardView3);

        nextButton = findViewById(R.id.btnNext);
        nextButton.setOnClickListener(this);
        nextButton.setText("NEXT");
        nextButton.setTextSize(20);
        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);

        if ((SmarterSMBApplication.isHeadPhoneConnected || isBluetoothHeadsetConnected())) {
            validateHeadsetConnection(true);
        } else {
            validateHeadsetConnection(false);
        }

        if (wifiConnected && (SmarterSMBApplication.isHeadPhoneConnected || isBluetoothHeadsetConnected())) {
            deviceCardView.setVisibility(View.VISIBLE);
            deviceCheckTextLayout.setVisibility(View.GONE);
        } else {
            deviceCardView.setVisibility(View.GONE);
            deviceCheckTextLayout.setVisibility(View.VISIBLE);
        }
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
            navigateToWifiCheckActivity();
        } else if (id == R.id.profile_image_back) {
            onBackPressed();
        } else if (id == R.id.ll_notification) {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            settingsApi(userId);
        }
    }

    private void navigateToWifiCheckActivity() {
        Intent intent = new Intent(this, WifiCheckActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        try {
            mMessageReceiver = new HeadPhoneReciever();
            IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(mMessageReceiver, filter);
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "registerReceiver myReceiver" + e.getMessage());
        }
        try {
            registerNetWorkBroadcastReceiver();
        } catch (Exception e) {
            e.printStackTrace();
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
    }

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
            if (BTReceiver!=null){
                unregisterReceiver(BTReceiver);
            }
        super.onPause(); // call this after the end of the unregistring Recivers
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

    private void settingsApi(final String userId) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.GetSettings(userId, 22, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
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
