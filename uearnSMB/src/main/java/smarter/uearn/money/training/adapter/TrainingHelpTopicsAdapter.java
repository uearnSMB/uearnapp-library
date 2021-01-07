package smarter.uearn.money.training.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.training.activity.ChartHelpActivity;
import smarter.uearn.money.training.activity.HelpChatListActivity;
import smarter.uearn.money.training.activity.HelpListActivity;
import smarter.uearn.money.training.activity.TrainingDashboardActivity;
import smarter.uearn.money.training.model.HelpTopics;
import smarter.uearn.money.training.model.TrainingMessage;

public class TrainingHelpTopicsAdapter extends RecyclerView.Adapter<TrainingHelpTopicsAdapter.TrainingHelpTopicsAdapterViewHolder> {
    LayoutInflater layoutInflater;
    private Context mContext;
    List<HelpTopics> helpTopicsList;

    public TrainingHelpTopicsAdapter(Context mContext, List<HelpTopics> helpTopicsList){
        this.mContext = mContext;
        this.helpTopicsList = helpTopicsList;
    }
    @NonNull
    @Override
    public TrainingHelpTopicsAdapter.TrainingHelpTopicsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_help, viewGroup, false);
        TrainingHelpTopicsAdapterViewHolder viewHolder = new TrainingHelpTopicsAdapterViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingHelpTopicsAdapter.TrainingHelpTopicsAdapterViewHolder trainingHelpTopicsAdapterViewHolder, int i) {

        trainingHelpTopicsAdapterViewHolder.tv_help_des.setText(helpTopicsList.get(i).getHelpdeskmessage());
        trainingHelpTopicsAdapterViewHolder.tv_help_header.setText(helpTopicsList.get(i).getHelpdesk_type());

    }

    @Override
    public int getItemCount() {
        return helpTopicsList.size();
    }

    class TrainingHelpTopicsAdapterViewHolder extends RecyclerView.ViewHolder{

        /*LinearLayout ll_notification_message, ll_message_indicator;
        ImageView image_message_indicator, image_message_read_indicator;*/
        TextView tv_help_header, tv_help_des;
        LinearLayout ll_next;

        public TrainingHelpTopicsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_next = (LinearLayout) itemView.findViewById(R.id.ll_next);

            tv_help_header = (TextView) itemView.findViewById(R.id.tv_help_header);
            tv_help_des = (TextView) itemView.findViewById(R.id.tv_help_des);

            ll_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = null;
                        intent = new Intent(mContext, ChartHelpActivity.class);
                        intent.putExtra("str", helpTopicsList.get(position).getHelpdesk_id());
                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }
}
