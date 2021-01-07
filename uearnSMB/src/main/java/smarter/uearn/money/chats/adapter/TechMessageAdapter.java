package smarter.uearn.money.chats.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.MyStopAlertDialog;
import smarter.uearn.money.agentslots.activity.AgentSlotsActivity;
import smarter.uearn.money.chats.activity.AudioActivity;
import smarter.uearn.money.chats.activity.ImageActivity;
import smarter.uearn.money.chats.model.GroupMessage;
import smarter.uearn.money.chats.model.TechMessage;
import smarter.uearn.money.chats.model.TechSupportRequest;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.views.ChatAudioPlayer;
import smarter.uearn.money.views.SmartAudioPlayer;

import static smarter.uearn.money.chats.activity.ChatActivity.channelResponse;

public class TechMessageAdapter extends RecyclerView.Adapter {
    Context context;
    private List<TechMessage> mMessages;
    private String userName;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_USER_LEFT = 3;
    private static final int VIEW_TYPE_MESSAGE_BUTTON = 4;
    public TechMessageAdapter(Context context, List<TechMessage> messages, String userName){
        this.context = context;
        this.userName = userName;
        mMessages = messages;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        /*View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.item_message, viewGroup, false);
        return new ViewHolder(v);*/
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_message_sent, viewGroup, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_message_received, viewGroup, false);
            return new ReceivedMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_USER_LEFT) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_user_left, viewGroup, false);
            return new UserLeftHolder(view);
        }
        else if (viewType == VIEW_TYPE_MESSAGE_BUTTON) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_button_received, viewGroup, false);
            return new MessageButtonHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        TechMessage message = mMessages.get(position);
        /*viewHolder.setMessage(message.getMessage());
        viewHolder.setUsername(message.getUsername());*/
        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_USER_LEFT:
                ((UserLeftHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_BUTTON:
                ((MessageButtonHolder) viewHolder).bind(message);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        TechMessage message = (TechMessage) mMessages.get(position);

        //Log.e("MessageAdapter","message ::: "+message.getType());
        if(message.getType().equalsIgnoreCase("BUTTON")){
            return VIEW_TYPE_MESSAGE_BUTTON;
        }else if(message.getMessageType().equalsIgnoreCase("USER_ADDED")){
            return VIEW_TYPE_USER_LEFT;
        }else if(message.getMessageType().equalsIgnoreCase("USER_LEFT")){
            return VIEW_TYPE_USER_LEFT;
        }
        else {
            if (message.getUsername().equals(userName)) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else if (message.getUsername().equalsIgnoreCase("system")) {
                // If some other user sent the message
                return VIEW_TYPE_USER_LEFT;
            } else if (message.getMessage().equalsIgnoreCase("BUTTONMESSAGE")) {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_BUTTON;
            } else {
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }
    }
    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private TextView mMessageView;
        private ImageButton mImageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            //mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
        }

        /*private int getUsernameColor(String username) {
            int hash = 7;
            for (int i = 0, len = username.length(); i < len; i++) {
                hash = username.codePointAt(i) + (hash << 5) - hash;
            }
            int index = Math.abs(hash % mUsernameColors.length);
            return mUsernameColors[index];
        }*/
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, image_message_time, audio_message_time;
        ImageView image_message_body, audio_message_body;
        WebView text_message_body;
        LinearLayout ll_image, ll_text, ll_audio;
        private ChatAudioPlayer audioPlayer;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            //text_message_body = (WebView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            image_message_body = (ImageView) itemView.findViewById(R.id.image_message_body);
            image_message_time = (TextView) itemView.findViewById(R.id.image_message_time);
            ll_image = (LinearLayout) itemView.findViewById(R.id.ll_image);
            ll_text = (LinearLayout) itemView.findViewById(R.id.ll_text);
            ll_audio = (LinearLayout) itemView.findViewById(R.id.ll_audio);
            //audio_message_body = (ImageView) itemView.findViewById(R.id.audio_message_body);
            audio_message_time = (TextView) itemView.findViewById(R.id.audio_message_time);
            audioPlayer = itemView.findViewById(R.id.player);
        }

        void bind(final TechMessage message) {
            //Log.e("MessageAdapter","message :: "+message.getMessage() +" :: "+message.getTime());
            //messageText.setText(message.getMessage());
            if(message.getType().equalsIgnoreCase("TEXT")) {
                ll_image.setVisibility(View.GONE);
                ll_audio.setVisibility(View.GONE);
                ll_text.setVisibility(View.VISIBLE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    messageText.setText(Html.fromHtml(message.getMessage(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    messageText.setText(Html.fromHtml(message.getMessage()));
                }
                timeText.setText(message.getTime());
                messageText.setTextSize(channelResponse.getTextfontsize());
            }else if(message.getType().equalsIgnoreCase("IMAGE")){
                ll_image.setVisibility(View.VISIBLE);
                ll_audio.setVisibility(View.GONE);
                ll_text.setVisibility(View.GONE);
                Picasso.get().load(message.getMessage()).resize(110, 110).
                        centerCrop().into(image_message_body);
                image_message_time.setText(message.getTime());
                image_message_body.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ImageActivity.class);
                        i.putExtra("IMAGE_URL", message.getMessage());
                        context.startActivity(i);
                    }
                });
            }else if(message.getType().equalsIgnoreCase("AUDIO")){
                ll_image.setVisibility(View.GONE);
                ll_audio.setVisibility(View.VISIBLE);
                ll_text.setVisibility(View.GONE);
                audio_message_time.setText(message.getTime());
                Uri fileUrl = Uri.parse(message.getMessage());
                audioPlayer.setDataSource(fileUrl);
                /*audio_message_body.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, AudioActivity.class);
                        i.putExtra("AUDIO_URL", message.getMessage());
                        context.startActivity(i);
                    }
                });*/
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView image_message_body;
        LinearLayout ll_call_chat;
        private ChatAudioPlayer audioPlayer;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            image_message_body = (ImageView) itemView.findViewById(R.id.image_message_body);
            //audio_message_body = (ImageView) itemView.findViewById(R.id.audio_message_body);
            ll_call_chat = (LinearLayout) itemView.findViewById(R.id.ll_call_chat);
            audioPlayer = itemView.findViewById(R.id.player);
        }

        void bind(final TechMessage message) {
            //messageText.setText(message.getMessage());
            if(message.getType().equalsIgnoreCase("TEXT")) {
                messageText.setVisibility(View.VISIBLE);
                image_message_body.setVisibility(View.GONE);
                audioPlayer.setVisibility(View.GONE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    messageText.setText(Html.fromHtml(message.getMessage(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    messageText.setText(Html.fromHtml(message.getMessage()));
                }
                messageText.setTextSize(channelResponse.getTextfontsize());
            }else if(message.getType().equalsIgnoreCase("IMAGE")){
                messageText.setVisibility(View.GONE);
                image_message_body.setVisibility(View.VISIBLE);
                audioPlayer.setVisibility(View.GONE);
                Picasso.get().load(message.getMessage()).resize(110, 110).
                        centerCrop().into(image_message_body);
                image_message_body.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ImageActivity.class);
                        i.putExtra("IMAGE_URL", message.getMessage());
                        context.startActivity(i);
                    }
                });
            }else if(message.getType().equalsIgnoreCase("AUDIO")){
                messageText.setVisibility(View.GONE);
                image_message_body.setVisibility(View.GONE);
                audioPlayer.setVisibility(View.VISIBLE);
                Uri fileUrl = Uri.parse(message.getMessage());
                audioPlayer.setDataSource(fileUrl);
                /*audio_message_body.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, AudioActivity.class);
                        i.putExtra("AUDIO_URL", message.getMessage());
                        context.startActivity(i);
                    }
                });*/
            }

            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
            nameText.setTextSize(channelResponse.getNamefontsize());

            nameText.setText(message.getUsername().toString());
            timeText.setText(message.getTime());
            ll_call_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adhocCall("8939438850");
                    CommonUtils.chatMakeCall(context, "", 0, "",
                            "", "", "",
                            "", "");
                }
            });

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    private class UserLeftHolder extends RecyclerView.ViewHolder {
        TextView messageText, text_message_user;

        UserLeftHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            text_message_user = (TextView) itemView.findViewById(R.id.text_message_user);
        }

        void bind(TechMessage message) {
            text_message_user.setTextSize(channelResponse.getNamefontsize());
            messageText.setTextSize(channelResponse.getTextfontsize());
            messageText.setText(message.getMessage());
            text_message_user.setText(message.getUsername());

            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
        }
    }

    private class MessageButtonHolder extends RecyclerView.ViewHolder {
        TextView btn_received_items;

        MessageButtonHolder(View itemView) {
            super(itemView);

            btn_received_items = itemView.findViewById(R.id.btn_received_items);
        }

        void bind(final TechMessage message) {
            btn_received_items.setText(message.getMessage());

            btn_received_items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        TechSupportRequest techSupportRequest = new TechSupportRequest();
                        //pmRequestChannel.setUsername(mUsername);
                        techSupportRequest.setUser_id(Integer.parseInt(message.getUserid()));
                        techSupportRequest.setTech_id(Integer.parseInt(channelResponse.getChannels().getTECH().getUserid()));
                        techSupportRequest.setSession_id(ApplicationSettings.getPref("SESSION_CODE",""));
                        new APIProvider.GetRequestForTechSupport(techSupportRequest, 1, (Activity) context, "Processing your request.", new API_Response_Listener<String>() {

                            @Override
                            public void onComplete(String data, long request_code, int failure_code) {
                                if (data != null) {
                                    showMessage(data);
                                }
                                //Log.e("ChatActivity", "code :: " + failure_code);
                            }
                        }).call();
                    } catch (Exception ex) {
                    }
                }
            });

            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
        }
    }

    private void showMessage(String data){
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.alert_message_dialog, null,false);
        Button yesBtn = contentView.findViewById(R.id.yesBtn);
        Typeface titlef = Typeface.createFromAsset(context.getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
        TextView tv_header = contentView.findViewById(R.id.tv_title);
        tv_header.setTypeface(titlef);
        TextView tv_description = contentView.findViewById(R.id.tv_message);
        tv_description.setTypeface(titlef);
        tv_header.setText("Info");
        tv_description.setText(data);
        Typeface titleFace = Typeface.createFromAsset(context.getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
        yesBtn.setTypeface(titleFace);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).setView(contentView).create();
        LinearLayout layout = new LinearLayout(context);
        //final Spinner spinner = new Spinner(this);

        layout.setOrientation(LinearLayout.VERTICAL);
        //layout.addView(spinner_stop);
        alertDialog.setCancelable(false);
        alertDialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void adhocCall(String phoneNumber){
        String toNumber = "";
        if(phoneNumber != null && !phoneNumber.isEmpty() && !phoneNumber.startsWith("+91")) {
            toNumber = "+91" + phoneNumber;
        } else {
            toNumber = phoneNumber;
        }
        SmarterSMBApplication.outgoingCallNotInStartMode = true;
        SmarterSMBApplication.webViewOutgoingCallEventTriggered = true;
        callToNumbers(context, toNumber, 0, "", "", "", "", "", "");
    }

    private void callToNumbers(final Context context, final String number,final long dbid, final String appointmentId,final String status, final  String substatus1, final  String substatus2, final String callername, final String notes){
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
                        if (screen != null && (screen.equalsIgnoreCase("UearnActivity"))) {
                            NotificationData.appointment_db_id = dbid;
                            //adhocCallEditText.setText("Calling.."+number);
                            sbiCall(knowlarityModel, customernumber, status, context, substatus1, substatus2, callername, notes, appointmentId, dbid);
                        }
                    } else {
                        //adhocCallEditText.setText("No internet connection");
                    }
                } else {
                    //adhocCallEditText.setText("No SR Number");
                }
            } else {
                //adhocCallEditText.setText("No Client ID");
            }
        }
    }

    public void sbiCall(KnowlarityModel knowlarityModel, final String customernumber, final String status, final Context context, final String substatus1, final String substatus2, final String callername, final String notes, final String appointmentId, final Long dbid) {
        NotificationData.knolarity_start_time = new Date().toString();
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                //NotificationData.knolarity_response_time = new Date().toString();
                if (data != null && !(data.isEmpty())) {
                    Log.e("TechMessage","date");
                    Log.e("TechMessage","date 1 :: "+data);

                }else{
                    Log.e("TechMessage","date else  :: "+failure_code);
                }
            }
        }).reClickToCall(knowlarityModel);
    }
}
