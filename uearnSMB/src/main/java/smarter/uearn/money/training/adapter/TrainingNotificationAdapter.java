package smarter.uearn.money.training.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.agentslots.adapter.AgentSlotsAdapter;
import smarter.uearn.money.agentslots.model.SlotResponse;
import smarter.uearn.money.training.activity.NotificationFullMessageActivity;
import smarter.uearn.money.training.activity.NotificationListActivtiy;
import smarter.uearn.money.training.activity.TrainingDashboardActivity;
import smarter.uearn.money.training.model.TrainingMessage;
import smarter.uearn.money.training.model.TrainingMessageResponse;
import smarter.uearn.money.utils.Utils;


public class TrainingNotificationAdapter extends RecyclerView.Adapter<TrainingNotificationAdapter.TrainingNotificationViewHolder> {
    LayoutInflater layoutInflater;
    private Context mContext;
    private TrainingMessage[] trainingMessage;

    public TrainingNotificationAdapter(Context mContext, TrainingMessage[] trainingMessage){
        this.mContext = mContext;
        this.trainingMessage = trainingMessage;
    }
    @NonNull
    @Override
    public TrainingNotificationAdapter.TrainingNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_notification_message, viewGroup, false);
        TrainingNotificationViewHolder viewHolder = new TrainingNotificationViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingNotificationAdapter.TrainingNotificationViewHolder trainingNotificationViewHolder, @SuppressLint("RecyclerView") final int i) {

        trainingNotificationViewHolder.tv_message_sent_date.setText(Utils.getDateTrainingMessageFormat(trainingMessage[i].getSent_date()));
        trainingNotificationViewHolder.tv_message.setText(trainingMessage[i].getMessage());
        if(trainingMessage[i].getMessage_type().equalsIgnoreCase("unread")){
            trainingNotificationViewHolder.image_message_indicator.setVisibility(View.VISIBLE);
            trainingNotificationViewHolder.image_message_read_indicator.setVisibility(View.GONE);
            trainingNotificationViewHolder.ll_message_indicator.setBackgroundColor(Color.parseColor("#FCF8FF"));
        }else{
            trainingNotificationViewHolder.image_message_indicator.setVisibility(View.GONE);
            trainingNotificationViewHolder.image_message_read_indicator.setVisibility(View.VISIBLE);
            trainingNotificationViewHolder.ll_message_indicator.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        trainingNotificationViewHolder.ll_notification_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(mContext, NotificationFullMessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("TrainingMessageObj",trainingMessage[i]);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                //finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return trainingMessage.length;
    }

    class TrainingNotificationViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ll_notification_message, ll_message_indicator;
        ImageView image_message_indicator, image_message_read_indicator;
        TextView tv_message_sent_date, tv_message;

        public TrainingNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_notification_message = (LinearLayout) itemView.findViewById(R.id.ll_notification_message);
            image_message_indicator = (ImageView) itemView.findViewById(R.id.image_message_indicator);
            image_message_read_indicator = (ImageView) itemView.findViewById(R.id.image_message_read_indicator);
            tv_message_sent_date = (TextView) itemView.findViewById(R.id.tv_message_sent_date);
            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
            ll_message_indicator = (LinearLayout) itemView.findViewById(R.id.ll_message_indicator);


        }
    }
}
