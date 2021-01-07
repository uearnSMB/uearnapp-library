package smarter.uearn.money.notificationmessages.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.notificationmessages.activity.ViewMessageActivity;
import smarter.uearn.money.notificationmessages.model.NotificationMessageResponse;

public class NotificationMessageAdapter extends RecyclerView.Adapter<NotificationMessageAdapter.NotificationMessageViewHolder> {
    private Context mContext;
    NotificationMessageResponse notificationMessageResponse;

    public NotificationMessageAdapter(Context mContext, NotificationMessageResponse notificationMessageResponse){
        this.mContext = mContext;
        this.notificationMessageResponse = notificationMessageResponse;
    }
    @NonNull
    @Override
    public NotificationMessageAdapter.NotificationMessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_notifi_messages, viewGroup, false);
        NotificationMessageViewHolder viewHolder = new NotificationMessageViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationMessageAdapter.NotificationMessageViewHolder notificationMessageViewHolder, final int i) {

        notificationMessageViewHolder.tv_message.setText(notificationMessageResponse.getSuccess()[i].getTitle());
        notificationMessageViewHolder.tv_description.setText(notificationMessageResponse.getSuccess()[i].getMessage());
        if(notificationMessageResponse.getSuccess()[i].getStatus().equalsIgnoreCase("UNREAD")){
            Log.e("MessageAdapter","UNREAD :: "+notificationMessageResponse.getSuccess()[i].getId());
            notificationMessageViewHolder.ll_msg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }else{
            Log.e("MessageAdapter","READ :: "+notificationMessageResponse.getSuccess()[i].getId());
            notificationMessageViewHolder.ll_msg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_slot_button));
            notificationMessageViewHolder.image_message.setImageDrawable
                    (ContextCompat.getDrawable(mContext, R.drawable.mail_open));
        }
        notificationMessageViewHolder.ll_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationIntent = new Intent(mContext, ViewMessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("MessageObj",notificationMessageResponse.getSuccess()[i]);
                notificationIntent.putExtras(bundle);
                mContext.startActivity(notificationIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationMessageResponse.getSuccess().length;
    }

    class NotificationMessageViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_message;
        public TextView tv_description;
        public LinearLayout ll_msg;
        public ImageView image_message;

        public NotificationMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            ll_msg = (LinearLayout) itemView.findViewById(R.id.ll_msg);
            image_message = (ImageView) itemView.findViewById(R.id.image_message);
        }
    }
}
