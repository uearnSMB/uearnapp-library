package smarter.uearn.money.agentslots.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.agentslots.model.SlotResponse;
import smarter.uearn.money.agentslots.model.SlotRows;

public class AgentSlotsAdapter extends RecyclerView.Adapter<AgentSlotsAdapter.AgentSlotsViewHolder> {
    private Context mContext;
    private SlotResponse slotResponse;
    LayoutInflater layoutInflater;

    public AgentSlotsAdapter(Context mContext, SlotResponse slotResponse){
        this.mContext = mContext;
        this.slotResponse = slotResponse;
    }
    @NonNull
    @Override
    public AgentSlotsAdapter.AgentSlotsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_agent_slots, viewGroup, false);
        AgentSlotsViewHolder viewHolder = new AgentSlotsViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AgentSlotsAdapter.AgentSlotsViewHolder agentSlotsViewHolder, int i) {
        SlotRows[] rows = slotResponse.getData()[i].getRows();
        agentSlotsViewHolder.tv_day.setText(slotResponse.getData()[i].getDay() + " : ");
        agentSlotsViewHolder.tv_date.setText(slotResponse.getData()[i].getDate());
        LinearLayoutManager hs_linearLayout = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        agentSlotsViewHolder.ll_childview.setLayoutManager(hs_linearLayout);
        agentSlotsViewHolder.ll_childview.setHasFixedSize(true);
        AgentSubSlotsAdapter agentSubSlotsAdapter = new AgentSubSlotsAdapter(mContext, rows);
        agentSlotsViewHolder.ll_childview.setAdapter(agentSubSlotsAdapter);
        //addChildView(rows);
    }

    @Override
    public int getItemCount() {
        return slotResponse.getData().length;
    }

    public void addChildView(SlotRows[] rows){

        //View view = layoutInflater.inflate(R.layout.sub_l, parent, false);
    }

    class AgentSlotsViewHolder extends RecyclerView.ViewHolder{
        public RecyclerView ll_childview;
        public TextView tv_day;
        public TextView tv_date;

        public AgentSlotsViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_childview = (RecyclerView) itemView.findViewById(R.id.ll_childview);
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
