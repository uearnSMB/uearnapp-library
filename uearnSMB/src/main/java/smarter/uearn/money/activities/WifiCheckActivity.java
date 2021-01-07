package smarter.uearn.money.activities;

import android.app.Activity;
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

import org.json.JSONObject;

import smarter.uearn.money.R;
import smarter.uearn.money.models.NetworkDetails;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.views.events.NetworkBroadcastReceiver;

public class WifiCheckActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView wifiText1, speedtv, nexttv;
    private Button nextButton;
    private ImageView wifirefresh, networkstatus;
    private Activity activity;
    private TextView headerTitle;
    private ImageView profileImage, image_sync;
    private LinearLayout notificationLayout;
    double BYTE_TO_KILOBIT = 0.008;
    double KILOBIT_TO_MEGABIT = 0.001;
    private NetworkBroadcastReceiver networkBroadcastReceiver = new NetworkBroadcastReceiver();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_check);
        wifiText1 = findViewById(R.id.wifiText1);
        speedtv = findViewById(R.id.speedtv);
        nexttv = findViewById(R.id.nexttv);
        networkstatus = findViewById(R.id.image1);
        wifirefresh = findViewById(R.id.wifiRefreshImage);
        wifirefresh.setOnClickListener(this);

        activity = this;
        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("Device Check");
        profileImage = (ImageView) findViewById(R.id.profile_image_back);
        profileImage.setBackgroundResource(R.drawable.ic_arrow_back_white_24px);
        profileImage.setOnClickListener(this);
        notificationLayout = (LinearLayout) findViewById(R.id.ll_notification);
        notificationLayout.setOnClickListener(this);

        image_sync = findViewById(R.id.image_sync);
        image_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                settingsApi(userId);
                Animation rotation = AnimationUtils.loadAnimation(WifiCheckActivity.this, R.anim.button_rotate);
                rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                v.startAnimation(rotation);
            }
        });

        nextButton = findViewById(R.id.btnNext);
        nextButton.setOnClickListener(this);
        nextButton.setText("NEXT");
        nextButton.setTextSize(20);
        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);

        changeStatusBarColor(this);
        try {
            registerNetWorkBroadcastReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CommonUtils.isNetworkAvailable(this)) {
            checkWiFiSpeed();
        } else {
            wifiText1.setText("00");
            networkstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_internet));
            speedtv.setText("You are not connected to internet");
            nexttv.setVisibility(View.GONE);
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
        }

        try {
            if (mMessageReceiver != null) {
                LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("remote-dialer-request-event"));
            }
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "mMessageReceiver Reg Error" + e.getMessage());
        }

 /*       try {
            IntentFilter networkBroadcastFilter = new IntentFilter();
            networkBroadcastFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(networkBroadcastReceiver, networkBroadcastFilter);
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "registerReceiver networkBroadcastReceiver" + e.getMessage());
        }*/
    }

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
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        try {
            registerNetWorkBroadcastReceiver();
        } catch (Exception e) {
            e.printStackTrace();
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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        if (view==null){
            return;
        }

        int id = view.getId();
        switch (id) {
            case R.id.wifiRefreshImage:
                if (CommonUtils.isNetworkAvailable(this)) {
                    Animation rotation = AnimationUtils.loadAnimation(this, R.anim.button_rotate);
                    rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                    wifirefresh.startAnimation(rotation);
                    checkWiFiSpeed();
                } else {
                    wifiText1.setText("00");
                    networkstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_internet));
                    speedtv.setText("You are not connected to internet");
                    nexttv.setVisibility(View.GONE);
                    nextButton.setEnabled(false);
                    nextButton.setClickable(false);
                    nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
                }
                break;
            case R.id.btnNext:
                navigateToDeviceCheckCompleteActivity();
                break;
            case R.id.profile_image_back:
                this.finish();
                break;
            case R.id.ll_notification:
                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                settingsApi(userId);
                break;
        }
    }

    private void navigateToDeviceCheckCompleteActivity() {
        Intent intent = new Intent(this, DeviceCheckCompleteActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public NetworkInfo getInfo() {
        return ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkWiFiSpeed() {
        NetworkInfo info = getInfo();
        NetworkDetails networkDetails = new NetworkDetails();
        if (info == null || !info.isConnected()) {
            SmarterSMBApplication.isWifiConnected = false;
            wifiText1.setText("00");
            networkstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_internet));
            speedtv.setText("You are not connected to internet");
            nexttv.setVisibility(View.GONE);
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
        }else
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            if (info.isConnected()) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    Integer linkSpeed = wifiInfo.getLinkSpeed();
                    String wifiSpeed = String.valueOf(linkSpeed);
                    if (wifiSpeed != null && !wifiSpeed.isEmpty() && !wifiSpeed.equals("null")) {
                        ApplicationSettings.putPref(AppConstants.WIFI_SPEED, wifiSpeed + " Mbps");
                        wifiText1.setText(wifiSpeed);
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
                wifiText1.setText(mbpsSpeedFinal + "");
                networkDetails.setNetwork("MOBILEDATA");
                networkDetails.setNetwork_speed(mbpsSpeedFinal);
            }
        }
        try {
            new APIProvider.PostRequestForNetworkDetails(networkDetails, 1, WifiCheckActivity.this, "Processing your request.", new API_Response_Listener<String>() {

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

                        }
                        if (type != null && !type.isEmpty() && type.equalsIgnoreCase("Bad")) {
                            networkstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_sad));
                            speedtv.setText(message);
                            nexttv.setVisibility(View.VISIBLE);
                            nexttv.setText(description);
                            nextButton.setEnabled(false);
                            nextButton.setClickable(false);
                            nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
                        } else if (type != null && !type.isEmpty() && type.equalsIgnoreCase("Good")) {
                            networkstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_smile));
                            speedtv.setText(message);
                            nexttv.setVisibility(View.VISIBLE);
                            nexttv.setText(description);
                            nextButton.setEnabled(true);
                            nextButton.setClickable(true);
                            nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
                        }
                    }
                    Log.e("TrainerDetails", "failure_code  :: " + failure_code);
                }
            }).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (networkBroadcastReceiver != null) {
                unregisterReceiver(networkBroadcastReceiver);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");

            if (result != null && !result.isEmpty() && result.equals("internet_on") && isWifiConnected()) {
                SmarterSMBApplication.isWifiConnected = true;
                checkWiFiSpeed();

            } else if (result != null && !result.isEmpty() && result.equals("internet_off") && !isWifiConnected()) {
                SmarterSMBApplication.isWifiConnected = false;
                wifiText1.setText("00");
                networkstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_internet));
                speedtv.setText("You are not connected to internet");
                nexttv.setVisibility(View.GONE);
                nextButton.setEnabled(false);
                nextButton.setClickable(false);
                nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
            }
        }
    };

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
}
