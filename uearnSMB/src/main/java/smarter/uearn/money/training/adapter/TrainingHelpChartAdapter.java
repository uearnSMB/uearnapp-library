package smarter.uearn.money.training.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import smarter.uearn.money.R;
import smarter.uearn.money.training.activity.ChartHelpActivity;
import smarter.uearn.money.training.model.HelpDeskInfoData;
import smarter.uearn.money.training.model.HelpDeskInfoDataObject;
import smarter.uearn.money.training.model.HelpTopics;

public class TrainingHelpChartAdapter extends RecyclerView.Adapter<TrainingHelpChartAdapter.TrainingHelpChartAdapterViewHolder> {
    LayoutInflater layoutInflater;
    private Context mContext;
    //    HelpTopics[] helpTopicsList;
    ArrayList<HelpDeskInfoData> stringArrayList;
    onClickAnswerButton onClickAnswerButton;
   // ArrayList<String> arrayAns;

    public TrainingHelpChartAdapter(Context mContext, ArrayList<HelpDeskInfoData> stringArrayList, onClickAnswerButton onClickAnswerButton) {
        this.mContext = mContext;
        this.stringArrayList = stringArrayList;
        this.onClickAnswerButton = onClickAnswerButton;
//        arrayAns = new ArrayList<>();
//        arrayAns.add(null);
    }

    @NonNull
    @Override
    public TrainingHelpChartAdapter.TrainingHelpChartAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_training_chart, viewGroup, false);
        TrainingHelpChartAdapter.TrainingHelpChartAdapterViewHolder viewHolder = new TrainingHelpChartAdapter.TrainingHelpChartAdapterViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingHelpChartAdapter.TrainingHelpChartAdapterViewHolder trainingHelpTopicsAdapterViewHolder, int i) {
        HelpDeskInfoData helpDeskInfoDataObject=stringArrayList.get(i);
        trainingHelpTopicsAdapterViewHolder.tv_txtchartbox.setText(helpDeskInfoDataObject.getQuestion());

        if(null != helpDeskInfoDataObject.getQuestionId()){
            if (null == helpDeskInfoDataObject.getStrAnswer()) {
                trainingHelpTopicsAdapterViewHolder.ll_btnQuesLayout.setVisibility(View.VISIBLE);
                trainingHelpTopicsAdapterViewHolder.btnAns.setVisibility(View.GONE);
            } else {
                trainingHelpTopicsAdapterViewHolder.btnAns.setVisibility(View.VISIBLE);
                trainingHelpTopicsAdapterViewHolder.btnAns.setText(helpDeskInfoDataObject.getStrAnswer());
                trainingHelpTopicsAdapterViewHolder.ll_btnQuesLayout.setVisibility(View.GONE);
            }

            trainingHelpTopicsAdapterViewHolder.btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trainingHelpTopicsAdapterViewHolder.ll_btnQuesLayout.setVisibility(View.GONE);
                    trainingHelpTopicsAdapterViewHolder.btnAns.setVisibility(View.VISIBLE);
                    trainingHelpTopicsAdapterViewHolder.btnAns.setText("No");
                    stringArrayList.get(trainingHelpTopicsAdapterViewHolder.getAdapterPosition()).setStrAnswer("No");
                    stringArrayList.get(trainingHelpTopicsAdapterViewHolder.getAdapterPosition()).setQuestionID(helpDeskInfoDataObject.getQuestionId());
                    onClickAnswerButton.onAnswerQuesion(stringArrayList.get
                            (trainingHelpTopicsAdapterViewHolder.getAdapterPosition()),trainingHelpTopicsAdapterViewHolder.getAdapterPosition());
                }
            });
            trainingHelpTopicsAdapterViewHolder.btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trainingHelpTopicsAdapterViewHolder.ll_btnQuesLayout.setVisibility(View.GONE);
                    trainingHelpTopicsAdapterViewHolder.btnAns.setVisibility(View.VISIBLE);
                    trainingHelpTopicsAdapterViewHolder.btnAns.setText("Yes");
                    stringArrayList.get(trainingHelpTopicsAdapterViewHolder.getAdapterPosition()).setStrAnswer("Yes");
                    stringArrayList.get(trainingHelpTopicsAdapterViewHolder.getAdapterPosition()).setQuestionID(helpDeskInfoDataObject.getQuestionId());
                    onClickAnswerButton.onAnswerQuesion(stringArrayList.
                            get(trainingHelpTopicsAdapterViewHolder.getAdapterPosition()),trainingHelpTopicsAdapterViewHolder.getAdapterPosition());
                }
            });
        }
        else{
            trainingHelpTopicsAdapterViewHolder.ll_btnQuesLayout.setVisibility(View.GONE);
            trainingHelpTopicsAdapterViewHolder.btnAns.setVisibility(View.GONE);
        }
    }

   public void addItem(HelpDeskInfoData s){
        stringArrayList.add(s);
        //arrayAns.add(null);
        notifyDataSetChanged(); }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    class TrainingHelpChartAdapterViewHolder extends RecyclerView.ViewHolder{

        /*LinearLayout ll_notification_message, ll_message_indicator;
        ImageView image_message_indicator, image_message_read_indicator;*/
        TextView tv_txtchartbox;
        LinearLayout ll_btnQuesLayout;
        Button  btnYes,btnNo,btnAns;
        public TrainingHelpChartAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_btnQuesLayout = (LinearLayout) itemView.findViewById(R.id.ll_btnQuesLayout);

            tv_txtchartbox = (TextView) itemView.findViewById(R.id.tv_txtchartbox);
            btnYes = (AppCompatButton) itemView.findViewById(R.id.btnYes);
            btnNo = (AppCompatButton) itemView.findViewById(R.id.btnNo);
            btnAns = (AppCompatButton) itemView.findViewById(R.id.btnAns);
//            btnYes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                   onClickAnswerButton.onAnswerQuesion("Yes",getAdapterPosition());
//                }
//            });

        }
    }

    public interface onClickAnswerButton{
        public void onAnswerQuesion(HelpDeskInfoData strAnswer,int position);
    }
}

