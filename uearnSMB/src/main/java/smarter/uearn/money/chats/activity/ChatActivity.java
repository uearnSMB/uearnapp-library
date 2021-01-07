package smarter.uearn.money.chats.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.rahimlis.badgedtablayout.BadgedTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.broadcastReceiver.NetworkReceiver;
import smarter.uearn.money.chats.fragment.GroupFragment;
import smarter.uearn.money.chats.fragment.PMFragment;
import smarter.uearn.money.chats.fragment.SMEFragment;
import smarter.uearn.money.chats.fragment.TechFragment;
import smarter.uearn.money.chats.model.OnDataUpdateListener;
import smarter.uearn.money.chats.model.PMRequestChannel;
import smarter.uearn.money.chats.model.ResponseChannel;
import smarter.uearn.money.chats.receiver.ChatNetworkReceiver;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import static smarter.uearn.money.activities.UearnHome.internet_status;
import static smarter.uearn.money.utils.SmarterSMBApplication.isTechSelected;
import static smarter.uearn.money.utils.SmarterSMBApplication.isTLSelected;
import static smarter.uearn.money.utils.SmarterSMBApplication.isSMESelected;
import static smarter.uearn.money.utils.SmarterSMBApplication.isGroupSelected;

public class ChatActivity extends BaseActivity {

    private Toolbar toolbar;
    public static BadgedTabLayout tabLayout;
    private ViewPager viewPager;
    public static Socket socket = null;
    String mUsername, userId;
    public String tech_tab, pm_tab, sme_tab, group_tab;
    JSONObject jsonObject;
    public static ResponseChannel channelResponse;
    public static final String STORAGE_DIR = "voicechat";
    public static String imagePath;

    public static String chatNetwork;
    public static boolean isGroupMessageOpen = false;
    public static boolean isTLMessageOpen = false;
    public static boolean isTechMessageOpen = false;
    public static boolean isSMEMessageOpen = false;
    public static ArrayList unseenGroupMessageArray, unseenTLMessageArray, unseenTechMessageArray,
            unseenSMEMessageArray;
    public static JSONObject unseenGroupMessageObject, unseenTLMessageObject,
            unseenTechMessageObject, unseenSMEMessageObject;

    private BroadcastReceiver mNetworkReceiver;
    public static boolean chatMakingCall;
    public static OnDataUpdateListener mDataUpdateListener = new OnDataUpdateListener() {
        @Override
        public void onDataAvailable(ResponseChannel channelResponse) {
            Log.e("ChatActivity","onDataAvailable :: ");
            PMFragment.getPMData(channelResponse);
            TechFragment.getTechData(channelResponse);
            SMEFragment.getSMEData(channelResponse);
            GroupFragment.getGroupData(channelResponse);
            Log.e("ChatActivity","socket :: "+socket.connected());
            if(!socket.connected()){
                socket.connect();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_tabs);
        registerReceiver();
        if (ApplicationSettings.containsPref("SESSION_CODE")) {
            Log.e("ChatActivity","SEESION_CODE :: "+ApplicationSettings.getPref("SESSION_CODE",""));
        }else{
            ApplicationSettings.putPref("SESSION_CODE", UUID.randomUUID().toString());
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chat");
        ((TextView) toolbar.getChildAt(0)).setTextSize(24);
        createDirectory(STORAGE_DIR);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        changeStatusBarColor(this);
        tabLayout = (BadgedTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        unseenGroupMessageArray = new ArrayList();
        unseenTLMessageArray = new ArrayList();
        unseenTechMessageArray = new ArrayList();
        unseenSMEMessageArray = new ArrayList();

        if (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null) {
            mUsername= ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
            userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        }

    }

    public void changeStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }
    private void createDirectory(String dirName) {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);

        Log.e("ChatActivity","onResume::");

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu = menu;
        getMenuInflater().inflate(R.menu.uearn_chat_menu, menu);
        *//*Drawable yourdrawable = menu.getItem(0).getIcon(); // change 0 with 1,2 ...
        yourdrawable.mutate();
        yourdrawable.setColorFilter(getResources().getColor(R.colo), PorterDuff.Mode.SRC_IN);*//*
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.chat_call) {
            if(isTLMessageOpen){
                if(CommonUtils.chatC2C()){
                    chatMakingCall = true;
                    CommonUtils.chatMakeCall(ChatActivity.this, channelResponse.getChannels().getTL().getPhone(), 0, "",
                            "", "", "",
                            "", "");

                }else {
                    Log.e("TechFragment","CommonUtils.chatC2C() :: "+CommonUtils.chatC2C());
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+channelResponse.getChannels().getTL().getPhone()));

                    if (ActivityCompat.checkSelfPermission(ChatActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    }
                    startActivity(callIntent);
                }
            }else if(isTechMessageOpen) {
                if(CommonUtils.chatC2C()){
                    chatMakingCall = true;
                    CommonUtils.chatMakeCall(ChatActivity.this, channelResponse.getChannels().getTECH().getPhone(), 0, "",
                            "", "", "",
                            "", "");

                }else {
                    Log.e("TechFragment","CommonUtils.chatC2C() :: "+CommonUtils.chatC2C());
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+channelResponse.getChannels().getTECH().getPhone()));

                    if (ActivityCompat.checkSelfPermission(ChatActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    }
                    startActivity(callIntent);
                }
            }
        }
        return super.onOptionsItemSelected(item);

    }*/

    private void registerReceiver(){
        try {
            mNetworkReceiver = new ChatNetworkReceiver(mDataUpdateListener);
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {
            Log.d("Unregister", "registerReceiver mNetworkReceiver" + e.getMessage());
        }
    }


    private void channelSubscribe(){
        PMRequestChannel pmRequestChannel = new PMRequestChannel();
        pmRequestChannel.setUsername(mUsername);
        pmRequestChannel.setUserid(userId);
        pmRequestChannel.setType("PM");
        try {

            new APIProvider.GetRequestForChannelInfo(pmRequestChannel, 1, ChatActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        //Log.e("ChatActivity", "data :: " + data);
                        Gson g = new Gson();
                        channelResponse = g.fromJson(data, ResponseChannel.class);
                    }
                    //Log.e("ChatActivity", "code :: " + failure_code);
                }
            }).call();
        } catch (Exception ex) {
        }
    }
    private void setupViewPager(final ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null) {
            mUsername= ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
            userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        }
        Log.e("ChatActivity","name :: "+mUsername + " :: "+ userId);
        PMRequestChannel pmRequestChannel = new PMRequestChannel();
        pmRequestChannel.setUsername(mUsername);
        pmRequestChannel.setUserid(userId);
        Log.e("ChatActivity","pmRequestChannel :: "+pmRequestChannel.getUserid());
        try {

            new APIProvider.GetRequestForChannelInfo(pmRequestChannel, 1, ChatActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    Log.e("ChatActivity","failure_code :: "+failure_code);
                    if (data != null) {
                        Log.e("ChatActivity","Response :: "+ data);
                        Log.e("ChatActivity","Options :: "+CommonUtils.optionsChatbot());
                        Gson g = new Gson();
                        channelResponse = g.fromJson(data, ResponseChannel.class);
                        if(CommonUtils.optionsChatbot() != null){

                            try{
                                jsonObject = new JSONObject(CommonUtils.optionsChatbot());

                                tech_tab = jsonObject.getString("tech_tab");
                                //Log.e("ChatActivity","tech_tab"+tech_tab);
                                pm_tab = jsonObject.getString("pm_tab");
                                sme_tab = jsonObject.getString("sme_tab");
                                group_tab = jsonObject.getString("group_tab");
                            }catch (Exception ex){
                            }
                        }
                        adapter.addFragment(new TechFragment(), tech_tab);
                        adapter.addFragment(new PMFragment(), pm_tab);

                        adapter.addFragment(new SMEFragment(), sme_tab);
                        adapter.addFragment(new GroupFragment(), group_tab);

                        viewPager.setAdapter(adapter);
                        viewPager.setCurrentItem(1);
                        viewPager.setOffscreenPageLimit(3);

                        Log.e("ChatActivity", "Get current item :: "+viewPager.getCurrentItem());
                        if(!isTLSelected) {
                            isGroupMessageOpen = false;
                            isTechMessageOpen = false;
                            isTLMessageOpen = true;
                            isSMEMessageOpen = false;
                            tabLayout.setBadgeText(1,null);
                            try {
                                JSONObject jsonObj = new JSONObject();
                                jsonObj.put("id", UUID.randomUUID().toString());
                                jsonObj.put("session_id", ApplicationSettings.getPref("SESSION_CODE", ""));
                                jsonObj.put("channel", channelResponse.getChannels().getTL().getChannel());
                                jsonObj.put("from_id", Integer.parseInt(userId));
                                jsonObj.put("to_id", Integer.parseInt(channelResponse.getChannels().getTL().getUserid()));
                                jsonObj.put("username", mUsername);
                                jsonObj.put("message", "");
                                jsonObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                                jsonObj.put("type", "SESSION_STARTS");
                                socket.emit("CHAT_MESSAGE", jsonObj);
                            } catch (JSONException e) {
                            }
                            isTLSelected = true;
                        }

                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int i, float v, int i1) {

                            }

                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onPageSelected(int i) {
                                Log.e("ChatActivity", "onPageSelected :: " + i);
                                    try {
                                        JSONObject jsonObj = new JSONObject();
                                        jsonObj.put("id", UUID.randomUUID().toString());
                                        jsonObj.put("session_id", ApplicationSettings.getPref("SESSION_CODE",""));
                                        if(i == 1){
                                            isGroupMessageOpen = false;
                                            isTechMessageOpen = false;
                                            isTLMessageOpen = true;
                                            isSMEMessageOpen = false;
                                            if(isTLMessageOpen){
                                                Log.e("ChatActivity","messageArrayTLCount :: "+unseenTLMessageArray.size());
                                                for(int k = 0; k < unseenTLMessageArray.size(); k++){
                                                    //unseenMessageArray.get(k);
                                                    unseenTLMessageObject = (JSONObject) unseenTLMessageArray.get(k);
                                                    Log.e("ChatActivity","Message TL Array :: "+k+" :: "+unseenTLMessageObject.get("id"));
                                                    socket.emit("MESSAGE_SEEN",unseenTLMessageObject);

                                                }
                                                unseenTLMessageArray.clear();
                                                tabLayout.setBadgeText(1,null);

                                            }
                                            if(!isTLSelected){
                                                jsonObj.put("channel", channelResponse.getChannels().getTL().getChannel());
                                                jsonObj.put("to_id", Integer.parseInt(channelResponse.getChannels().getTL().getUserid()));
                                                isTLSelected = true;
                                            }else{
                                                return;
                                            }
                                        }

                                        if(i == 0){
                                            isGroupMessageOpen = false;
                                            isTechMessageOpen = true;
                                            isTLMessageOpen = false;
                                            isSMEMessageOpen = false;
                                            if(isTechMessageOpen){
                                                Log.e("ChatActivity","messageArrayTechCount :: "+unseenTechMessageArray.size());
                                                for(int k = 0; k < unseenTechMessageArray.size(); k++){
                                                    //unseenMessageArray.get(k);
                                                    unseenTechMessageObject = (JSONObject) unseenTechMessageArray.get(k);
                                                    Log.e("ChatActivity","Message Tech Array :: "+k+" :: "+unseenTechMessageObject.get("id"));
                                                    socket.emit("MESSAGE_SEEN",unseenTechMessageObject);

                                                }
                                                unseenTechMessageArray.clear();
                                                tabLayout.setBadgeText(0,null);

                                            }
                                            if(!isTechSelected) {
                                                jsonObj.put("channel", channelResponse.getChannels().getTECH().getChannel());
                                                jsonObj.put("to_id", Integer.parseInt(channelResponse.getChannels().getTECH().getUserid()));
                                                isTechSelected = true;
                                            }else{
                                                return;
                                            }
                                        }
                                        if(i == 2){
                                            isGroupMessageOpen = false;
                                            isTechMessageOpen = false;
                                            isTLMessageOpen = false;
                                            isSMEMessageOpen = true;
                                            if(isTLMessageOpen){
                                                Log.e("ChatActivity","messageArraySMECount :: "+unseenSMEMessageArray.size());
                                                for(int k = 0; k < unseenSMEMessageArray.size(); k++){
                                                    //unseenMessageArray.get(k);
                                                    unseenSMEMessageObject = (JSONObject) unseenSMEMessageArray.get(k);
                                                    Log.e("ChatActivity","Message TL Array :: "+k+" :: "+unseenSMEMessageObject.get("id"));
                                                    socket.emit("MESSAGE_SEEN",unseenSMEMessageObject);

                                                }
                                                unseenSMEMessageArray.clear();
                                                tabLayout.setBadgeText(2,null);

                                            }
                                            if(!isSMESelected) {
                                                jsonObj.put("channel", channelResponse.getChannels().getSME().getChannel());
                                                if(null != channelResponse.getChannels().getSME().getUserid()) {
                                                    jsonObj.put("to_id", Integer.parseInt(channelResponse.getChannels().getSME().getUserid()));
                                                }else{
                                                    jsonObj.put("to_id", Integer.parseInt(channelResponse.getGroupid()));
                                                }
                                                isSMESelected = true;
                                            }else{
                                                return;
                                            }

                                        }
                                        if(i == 3){
                                            isGroupMessageOpen = true;
                                            isTechMessageOpen = false;
                                            isTLMessageOpen = false;
                                            isSMEMessageOpen = false;
                                            Log.e("ChatActivity","isGroupMessageOpen :: "+isGroupMessageOpen);
                                            if(isGroupMessageOpen){
                                                Log.e("ChatActivity","messageArrayCount :: "+unseenGroupMessageArray.size());
                                                for(int k = 0; k < unseenGroupMessageArray.size(); k++){
                                                        //unseenMessageArray.get(k);
                                                    unseenGroupMessageObject = (JSONObject) unseenGroupMessageArray.get(k);
                                                        Log.e("ChatActivity","Message Array :: "+k+" :: "+unseenGroupMessageObject.get("id"));
                                                    socket.emit("MESSAGE_SEEN",unseenGroupMessageObject);

                                                }
                                                unseenGroupMessageArray.clear();
                                                tabLayout.setBadgeText(3,null);

                                            }
                                            if(!isGroupSelected) {
                                                jsonObj.put("channel", channelResponse.getChannels().getGROUP().getChannel());
                                                jsonObj.put("to_id", Integer.parseInt(channelResponse.getGroupid()));
                                                isGroupSelected = true;
                                            }else{
                                                return;
                                            }

                                        }
                                        Log.e("ChatActivity","for test :: ");
                                        jsonObj.put("from_id", Integer.parseInt(userId));
                                        jsonObj.put("username", mUsername);
                                        jsonObj.put("message", "");
                                        jsonObj.put("groupid", Integer.parseInt(channelResponse.getGroupid()));
                                        jsonObj.put("type", "SESSION_STARTS");
                                        socket.emit("CHAT_MESSAGE", jsonObj);
                                    } catch (JSONException e) {
                                    }
                                }

                            @Override
                            public void onPageScrollStateChanged(int i) {

                            }
                        });
                    }
                    //Log.e("ChatActivity", "code :: " + failure_code);
                }
            }).call();
        } catch (Exception ex) {
            Log.e("ChatActivity", "ex :: " + ex);
        }

    }




    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, UearnHome.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatMakingCall = false;
        if(mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }


    }
}
