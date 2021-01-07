package smarter.uearn.money.agentslots.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import smarter.uearn.money.R;
import smarter.uearn.money.agentslots.activity.AgentSlotsActivity;
import smarter.uearn.money.agentslots.model.SlotCancel;
import smarter.uearn.money.agentslots.model.SlotRows;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;

public class AgentSubSlotsAdapter extends RecyclerView.Adapter<AgentSubSlotsAdapter.AgentSubSlotsViewHolder> {
    private Context mContext;
    private SlotRows[] rows;
    Activity activity;

    public AgentSubSlotsAdapter(Context mContext, SlotRows[] rows){
        this.mContext = mContext;
        this.rows = rows;
        activity = (Activity) mContext;
    }
    @NonNull
    @Override
    public AgentSubSlotsAdapter.AgentSubSlotsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.sub_list_agent_slots, viewGroup, false);
        AgentSubSlotsViewHolder viewHolder = new AgentSubSlotsViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AgentSubSlotsAdapter.AgentSubSlotsViewHolder agentSubSlotsViewHolder, final int i) {
        agentSubSlotsViewHolder.tv_time_period.setText(rows[i].getTime());
        agentSubSlotsViewHolder.tv_available.setText(rows[i].getStatus());
        Log.e("AgentSlots","isBooked :: "+rows[i].getSlot_id() + " :: "+rows[i].isActive());
        if(rows[i].isActive()) {
            if (!rows[i].isAlready_booked()) {
                agentSubSlotsViewHolder.tv_book.setEnabled(true);
                agentSubSlotsViewHolder.tv_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("AgentAdapter", "Slot_id :: " + rows[i].getSlot_id());
                        String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                        SlotCancel slotRows = new SlotCancel();
                        slotRows.setSlot_id(rows[i].getSlot_id());
                        slotRows.setUser_id(user_id);
                        try {

                            new APIProvider.GetRequestForBookingSlot(slotRows, 1, activity, "Processing your request.", new API_Response_Listener<String>() {

                                @Override
                                public void onComplete(String data, long request_code, int failure_code) {
                                    if (data != null) {
                                        Log.e("AgentSlots", "data :: " + data);
                                        Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();
                                        agentSubSlotsViewHolder.tv_book.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rect_grey_slots));
                                        agentSubSlotsViewHolder.tv_book.setEnabled(true);
                                        agentSubSlotsViewHolder.tv_book.setText("Cancel");
                                        notifyDataSetChanged();
                                        Intent intent = new Intent(mContext, AgentSlotsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                    }
                                    Log.e("AgentSlots", "code :: " + failure_code);
                                }
                            }).call();
                        } catch (Exception ex) {
                            Log.e("AgentSlots", "Exception :: " + ex);
                        }
                    }
                });
            } else {
                agentSubSlotsViewHolder.tv_book.setEnabled(true);
                agentSubSlotsViewHolder.tv_book.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rect_grey_slots));
                agentSubSlotsViewHolder.tv_book.setText("Cancel");
                agentSubSlotsViewHolder.tv_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("AgentAdapter", "Slot_id :: " + rows[i].getSlot_id());
                        String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                        SlotCancel slotCancel = new SlotCancel();
                        slotCancel.setSlot_id(rows[i].getSlot_id());
                        slotCancel.setUser_id(user_id);
                        try {
                            //String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                            new APIProvider.GetRequestForCancelSlot(slotCancel, 1, activity, "Processing your request.", new API_Response_Listener<String>() {

                                @Override
                                public void onComplete(String data, long request_code, int failure_code) {
                                    if (data != null) {
                                        Log.e("AgentSlots", "data :: " + data);
                                        Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();
                                        agentSubSlotsViewHolder.tv_book.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green_slot_button));
                                        agentSubSlotsViewHolder.tv_book.setEnabled(true);
                                        agentSubSlotsViewHolder.tv_book.setText("Book");
                                        notifyDataSetChanged();
                                        Intent intent = new Intent(mContext, AgentSlotsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                    }
                                    Log.e("AgentSlots", "code :: " + failure_code);
                                }
                            }).call();
                        } catch (Exception ex) {
                            Log.e("AgentSlots", "Exception :: " + ex);
                        }
                    }
                });
            }
        }else{
            agentSubSlotsViewHolder.tv_book.setEnabled(false);
            agentSubSlotsViewHolder.ll_sub_list_slots.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_slot_button));
            agentSubSlotsViewHolder.tv_book.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_slot_button));
            agentSubSlotsViewHolder.tv_book.setText("");
        }

        if(rows[i].getChecklist() != null && !rows[i].getChecklist().isEmpty()) {
            agentSubSlotsViewHolder.tv_waiting_list.setText(rows[i].getChecklist());
        }else{
            agentSubSlotsViewHolder.tv_waiting_list.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rows.length;
    }

    class AgentSubSlotsViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_time_period;
        public TextView tv_available;
        public TextView tv_waiting_list;
        public Button tv_book;
        public LinearLayout ll_sub_list_slots;

        public AgentSubSlotsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time_period = (TextView) itemView.findViewById(R.id.tv_time_period);
            tv_available = (TextView) itemView.findViewById(R.id.tv_available);
            tv_waiting_list = (TextView) itemView.findViewById(R.id.tv_waiting_list);
            tv_book = (Button) itemView.findViewById(R.id.tv_book);
            ll_sub_list_slots = (LinearLayout) itemView.findViewById(R.id.ll_sub_list_slots);
        }
    }
}
