package smarter.uearn.money.chats.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.chats.activity.AudioActivity;
import smarter.uearn.money.chats.activity.ImageActivity;
import smarter.uearn.money.chats.model.GroupMessage;
import smarter.uearn.money.chats.model.TechMessage;
import smarter.uearn.money.chats.model.TechSupportRequest;
import smarter.uearn.money.dialogs.CustomTwoButtonDialog;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.views.ChatAudioPlayer;
import smarter.uearn.money.views.SmartAudioPlayer;

import static smarter.uearn.money.chats.activity.ChatActivity.channelResponse;
import static smarter.uearn.money.chats.activity.ChatActivity.chatMakingCall;

public class GroupAdapter extends RecyclerView.Adapter {
    Context context;
    private List<GroupMessage> mMessages;
    private String userName;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_USER_LEFT = 3;
    private static final int VIEW_TYPE_MESSAGE_BUTTON = 4;
    public GroupAdapter(Context context, List<GroupMessage> messages, String userName){
        this.context = context;
        this.userName = userName;
        mMessages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
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
        }else if (viewType == VIEW_TYPE_MESSAGE_BUTTON) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_button_received, viewGroup, false);
            return new MessageButtonHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        GroupMessage message = mMessages.get(position);
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
        GroupMessage message = (GroupMessage) mMessages.get(position);

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

        void bind(final GroupMessage message) {
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

        void bind(final GroupMessage message) {
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

            nameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatMakingCall = true;
                    Log.e("GroupAdapter","Details :: "+" :: "+message.getPhone());
                    if (CommonUtils.allowGroupChatCall()) {
                        startDialog(message.getUsername(),message.getPhone());
                    }

                }
            });

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    private class UserLeftHolder extends RecyclerView.ViewHolder {
        TextView messageText,  text_message_user;

        UserLeftHolder(View itemView) {
            super(itemView);
            text_message_user = (TextView) itemView.findViewById(R.id.text_message_user);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        }

        void bind(GroupMessage message) {
            messageText.setText(message.getMessage());
            text_message_user.setText(message.getUsername());
            text_message_user.setTextSize(channelResponse.getNamefontsize());
            messageText.setTextSize(channelResponse.getTextfontsize());
            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
        }
    }

    private class MessageButtonHolder extends RecyclerView.ViewHolder {
        TextView btn_received_items;

        MessageButtonHolder(View itemView) {
            super(itemView);

            btn_received_items =  itemView.findViewById(R.id.btn_received_items);
        }

        void bind(final GroupMessage message) {
            btn_received_items.setText(message.getMessage());

            btn_received_items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        TechSupportRequest techSupportRequest = new TechSupportRequest();
                        //pmRequestChannel.setUsername(mUsername);
                        techSupportRequest.setUser_id(Integer.parseInt(message.getUserid()));
                        new APIProvider.GetRequestForTechSupport(techSupportRequest, 1, (Activity) context, "Processing your request.", new API_Response_Listener<String>() {

                            @Override
                            public void onComplete(String data, long request_code, int failure_code) {
                                if (data != null) {

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

    private void startDialog(String agentName, final String agentNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Call");
        String[] options = {"Voice call to "+agentName};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if(CommonUtils.chatC2C()){
                            CommonUtils.chatMakeCall(context, agentNumber, 0, "",
                                    "", "", "",
                                    "", "");
                        }else {
                            Log.e("PMFragment","CommonUtils.chatC2C() :: "+CommonUtils.chatC2C());
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+agentNumber));

                            if (ActivityCompat.checkSelfPermission(context,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            context.startActivity(callIntent);
                        }
                }
            }
        });


        AlertDialog diag = builder.create();

        //Display the message!
        diag.show();
    }
}
