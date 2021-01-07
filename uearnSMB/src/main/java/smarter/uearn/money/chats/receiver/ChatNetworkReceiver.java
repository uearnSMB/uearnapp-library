package smarter.uearn.money.chats.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import smarter.uearn.money.chats.model.OnDataUpdateListener;
import smarter.uearn.money.chats.model.PMRequestChannel;
import smarter.uearn.money.chats.model.ResponseChannel;
import smarter.uearn.money.chats.utils.ChatNetworkInformation;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.NetworkInformation;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import static smarter.uearn.money.chats.activity.ChatActivity.channelResponse;
import static smarter.uearn.money.chats.activity.ChatActivity.socket;

import static smarter.uearn.money.chats.activity.ChatActivity.chatNetwork;
//import static smarter.uearn.money.chats.fragment.GroupFragment.mAdapter;

public class ChatNetworkReceiver extends BroadcastReceiver {
    ChatNetworkInformation networkInformation = new ChatNetworkInformation();
    String mUsername, userId;
    private OnDataUpdateListener mDataUpdateListener = null;
    public ChatNetworkReceiver(OnDataUpdateListener dataUpdateListener) {
        mDataUpdateListener = dataUpdateListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        chatNetwork = networkInformation.ConnectionQuality(context);
        if (networkInformation.ConnectionQuality(context).equalsIgnoreCase("NO INTERNET")){
            Log.e("ChatActivity","internet No ::"+networkInformation.ConnectionQuality(context));
            socket.disconnect();
        }else{
            if(null != socket) {
                Log.e("NetworkReceiver", "socket 123:: " + socket.connected());
                socket.disconnect();
            }

            if(null == socket) {
                connectConnection();
            }

            fetchMessages(context);
            Log.e("ChatActivity","internet Yes ::"+networkInformation.ConnectionQuality(context));
        }
    }

    private void connectConnection(){
        //if(socket == null) {
        if (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null) {
            mUsername= ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
            userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        }
        try {
            //socket = IO.socket("http://3.6.155.59:3005");
            socket = IO.socket(CommonUtils.chatbotURL());
            socket.connect();
            //socket.emit("connection", mUsername);
            socket.on("connect", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("ChatActivity","args 123 :: "+ args.length+" ::: "+socket.id());
                    JSONObject jsonObj = new JSONObject();
                    try {
                        jsonObj.put("userid", Integer.parseInt(userId));
                        jsonObj.put("socketid", socket.id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("SOCKET_CONNECTION", jsonObj);
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();

            }
        //}
    }
    private void fetchMessages(Context context){
        if (ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "") != null) {
            mUsername= ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
            userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        }
        Log.e("ChatActivity","name :: "+mUsername + " :: "+ userId);
        PMRequestChannel pmRequestChannel = new PMRequestChannel();
        pmRequestChannel.setUsername(mUsername);
        pmRequestChannel.setUserid(userId);

        try {

            new APIProvider.GetRequestForChannelInfo(pmRequestChannel, 1, (Activity) context, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        Log.e("ChatNetworkReceiver", "data :: " + data);
                        Gson g = new Gson();
                        channelResponse = g.fromJson(data, ResponseChannel.class);
                        //mAdapter.notifyDataSetChanged();
                        if (null != mDataUpdateListener) {
                            mDataUpdateListener.onDataAvailable(channelResponse);
                        }
                    }
                    //Log.e("ChatActivity", "code :: " + failure_code);
                }
            }).call();
        } catch (Exception ex) {
        }
    }
}
