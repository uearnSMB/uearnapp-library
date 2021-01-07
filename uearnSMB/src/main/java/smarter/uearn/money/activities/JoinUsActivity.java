package smarter.uearn.money.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

// import com.google.android.youtube.player.YouTubeInitializationResult;
// import com.google.android.youtube.player.YouTubePlayer;
// import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.text.DecimalFormat;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.views.events.NetworkBroadcastReceiver;

public class JoinUsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUp;
    private TextView signIn;
    private TextView internet;
    private TextView totalAgentTV, totalEarningsTV, totalAgent, totalEarning;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Activity activity = null;
    private ImageView imageSecond;
    private View view1, view2, view3;
    //private YouTubePlayer youTubePlayer = null;
    private FrameLayout lyVideo, lyThumbView;
    private NetworkBroadcastReceiver networkBroadcastReceiver;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            if (result != null && !result.isEmpty() && result.equals("internet_on")) {
                internet.setVisibility(View.GONE);
                totalAgent.setVisibility(View.VISIBLE);
                totalAgentTV.setVisibility(View.VISIBLE);
                totalEarning.setVisibility(View.VISIBLE);
                totalEarningsTV.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                view3.setVisibility(View.VISIBLE);
                signUp.setBackgroundResource(R.drawable.red_rounded_corner);
                signIn.setTextColor(getResources().getColor(R.color.app_color_red));
                signIn.setEnabled(true);
                signUp.setEnabled(true);
                getUearnTotalAgentsAndEarnings();
            } else if (result != null && !result.isEmpty() && result.equals("internet_off")) {
                internet.setText("No internet connection");
                internet.setVisibility(View.VISIBLE);
                internet.setTextColor(getResources().getColor(R.color.uearn_red_color));
                totalAgent.setVisibility(View.GONE);
                totalAgentTV.setVisibility(View.GONE);
                totalEarning.setVisibility(View.GONE);
                totalEarningsTV.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
                signUp.setBackgroundResource(R.drawable.gray_rounded_corner);
                signIn.setTextColor(getResources().getColor(R.color.custom_gray_color));
                signIn.setEnabled(false);
                signUp.setEnabled(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);
        activity = this;
        SmarterSMBApplication.currentActivity = this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        imageSecond = findViewById(R.id.image_second);
        imageSecond.setOnClickListener(this);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        lyVideo = findViewById(R.id.lyVideoContainer);
        lyThumbView = findViewById(R.id.lyThumbnail);

        signUp = findViewById(R.id.btnSignUp);
        signUp.setOnClickListener(this);
        signUp.setText("JOIN US NOW");
        signUp.setTextSize(18);
        signUp.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        signUp.setBackgroundResource(R.drawable.red_rounded_corner);

        signIn = findViewById(R.id.btnSignIn);
        signIn.setOnClickListener(this);

        totalAgent = findViewById(R.id.totalAgents);
        totalEarning = findViewById(R.id.totalEarnings);
        totalAgentTV = findViewById(R.id.totalAgentstv);
        totalEarningsTV = findViewById(R.id.totalEarningstv);
        internet = findViewById(R.id.tv_internet);

        changeStatusBarColor(this);
        getUearnTotalAgentsAndEarnings();

//         YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
//         youtubeFragment.initialize(AppConstants.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
//             @Override
//             public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
//                 if (!restored) {
//                     youTubePlayer = player;
//                     youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
//                     player.setPlayerStateChangeListener(playerStateChangeListener);
//                     player.setPlaybackEventListener(playbackEventListener);
//                 }
//             }

//             @Override
//             public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//                 youTubePlayer = null;
//             }
//         });

        try {
            if (mMessageReceiver != null) {
                LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("remote-dialer-request-event"));
            }
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "mMessageReceiver Reg Error" + e.getMessage());
        }

        try {
            networkBroadcastReceiver = new NetworkBroadcastReceiver();
            IntentFilter networkBroadcastFilter = new IntentFilter();
            networkBroadcastFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(networkBroadcastReceiver, networkBroadcastFilter);
        } catch (Exception e) {
            Log.d("DeviceCheckActivity", "registerReceiver networkBroadcastReceiver" + e.getMessage());
        }

        if (CommonUtils.isNetworkAvailable(this)) {

        } else {
            internet.setText("No internet connection");
            internet.setTextColor(getResources().getColor(R.color.uearn_red_color));
            totalAgent.setVisibility(View.GONE);
            totalAgentTV.setVisibility(View.GONE);
            totalEarning.setVisibility(View.GONE);
            totalEarningsTV.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.GONE);
            signUp.setBackgroundResource(R.drawable.gray_rounded_corner);
            signIn.setTextColor(getResources().getColor(R.color.custom_gray_color));
            signIn.setEnabled(false);
            signUp.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
//        try {
//            networkBroadcastReceiver = new NetworkBroadcastReceiver();
//            IntentFilter networkBroadcastFilter = new IntentFilter();
//            networkBroadcastFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//            registerReceiver(networkBroadcastReceiver, networkBroadcastFilter);
//        } catch (Exception e) {
//            Log.d("DeviceCheckActivity", "registerReceiver networkBroadcastReceiver" + e.getMessage());
//        }
    }

    public void changeStatusBarColor(AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.app_color_purple));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        SmarterSMBApplication.signoutNotificationReceived = false;

        try {
            if (networkBroadcastReceiver != null) {
                unregisterReceiver(networkBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent authenticationIntent = new Intent(this, SmarterAuthActivity.class);
        String itemName = "";

        int id = view.getId();
        if (id == R.id.btnSignUp) {
            authenticationIntent.putExtra("screenType", 1);
            itemName = "Sign Up";

            Bundle AnalyticsBundle = new Bundle();
            AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_1);
            AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
            AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
            mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_1, AnalyticsBundle);
            startActivity(authenticationIntent);
            JoinUsActivity.this.finish();
        } else if (id == R.id.btnSignIn) {
            Bundle AnalyticsBundle;
            authenticationIntent.putExtra("screenType", 0);
            itemName = "Sign In";

            AnalyticsBundle = new Bundle();
            AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_1);
            AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
            AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
            mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_1, AnalyticsBundle);
            startActivity(authenticationIntent);
            JoinUsActivity.this.finish();
        } else if (id == R.id.image_second) {
//             try {
//                 if (youTubePlayer != null) {
//                     lyVideo.setVisibility(View.VISIBLE);
//                     lyThumbView.setVisibility(View.GONE);
//                     youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
//                     youTubePlayer.loadVideo(AppConstants.YOUTUBE_VIDEO_CODE);
//                     youTubePlayer.play();
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
        }
    }

//     private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

//         @Override
//         public void onBuffering(boolean arg0) {
//         }

//         @Override
//         public void onPaused() {
//         }

//         @Override
//         public void onPlaying() {
//         }

//         @Override
//         public void onSeekTo(int arg0) {
//         }

//         @Override
//         public void onStopped() {

//         }

//     };

//     private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

//         @Override
//         public void onAdStarted() {

//         }

//         @Override
//         public void onError(YouTubePlayer.ErrorReason arg0) {
//             lyVideo.setVisibility(View.GONE);
//             lyThumbView.setVisibility(View.VISIBLE);
//         }

//         @Override
//         public void onLoaded(String arg0) {
//         }

//         @Override
//         public void onLoading() {
//         }

//         @Override
//         public void onVideoEnded() {
//             lyVideo.setVisibility(View.GONE);
//             lyThumbView.setVisibility(View.VISIBLE);
//         }

//         @Override
//         public void onVideoStarted() {

//         }
//     };

    @Override
    protected void onStop() {
        super.onStop();
        if (mMessageReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (networkBroadcastReceiver != null) {
                unregisterReceiver(networkBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUearnTotalAgentsAndEarnings() {
        String data = ApplicationSettings.getPref(AppConstants.UEARN_DATA, "");
        if (data != null && !data.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (jsonObject.has("uearn_data")) {
                    String uearnData = jsonObject.getString("uearn_data");
                    if (uearnData != null && !uearnData.isEmpty()) {
                        JSONObject uearnDataObject = new JSONObject(uearnData);
                        if (uearnDataObject.has("total_agents")) {
                            String totalAgents = uearnDataObject.getString("total_agents");
                            if (totalAgents != null && !totalAgents.isEmpty()) {
                                double totalAgentsD = Double.parseDouble(totalAgents);
                                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                decimalFormat.setGroupingUsed(true);
                                decimalFormat.setGroupingSize(3);
                                String totalAgentVal = decimalFormat.format(totalAgentsD);
                                totalAgent.setText(totalAgentVal);
                            }
                        }
                        if (uearnDataObject.has("total_earned_money")) {
                            String totalEarnedMoney = uearnDataObject.getString("total_earned_money");
                            if (totalEarnedMoney != null && !totalEarnedMoney.isEmpty()) {
                                totalEarning.setText(totalEarnedMoney);
                            }
                        }
                    }
                }
                if (jsonObject.has("languages")) {
                    String languages = jsonObject.getString("languages");
                    ApplicationSettings.putPref(AppConstants.LANGUAGES, languages);
                }
                if (jsonObject.has("education_qualifications")) {
                    String educationQualifications = jsonObject.getString("education_qualifications");
                    ApplicationSettings.putPref(AppConstants.EDUCATION_QUALIFICATIONS, educationQualifications);
                }
                if (jsonObject.has("experience")) {
                    String experience = jsonObject.getString("experience");
                    ApplicationSettings.putPref(AppConstants.EXPERIENCE, experience);
                }
                if (jsonObject.has("hardware_setup")) {
                    String hardwareSetup = jsonObject.getString("hardware_setup");
                    ApplicationSettings.putPref(AppConstants.HARDWARE_SETUP, hardwareSetup);
                }
            } catch (Exception e) {

            }
        } else {
            getUearnTotalAgentsAndEarningsOnline();
        }
    }

    private void getUearnTotalAgentsAndEarningsOnline() {
        if (CommonUtils.isNetworkAvailable(this)) {
            if (activity != null && !activity.isFinishing()) {
                new APIProvider.GetUearnTotalAgentsAndEarnings("", 0, null, "Please wait..", new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        if (data != null && !data.isEmpty()) {
                            ApplicationSettings.putPref(AppConstants.UEARN_DATA, data);
                            getUearnTotalAgentsAndEarnings();
                        }
                    }
                }).requestForDataCall();
            }
        }
    }
}
