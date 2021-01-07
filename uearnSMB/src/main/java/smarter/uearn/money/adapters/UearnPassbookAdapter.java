package smarter.uearn.money.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.models.UearnRangePassbookInfo;

public class UearnPassbookAdapter extends RecyclerView.Adapter<UearnPassbookAdapter.MyViewHolder> {

    private List<UearnRangePassbookInfo> passbookInfoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView dateOfEarningTextView, callEarning, todayEarning, brand, closingBalanceEarned, description;
        private ImageView brandImg;
        private RelativeLayout brandLayout, brandImgLayout;

        public MyViewHolder(View view) {
            super(view);
            dateOfEarningTextView = view.findViewById(R.id.dateOfEarning);
            callEarning = view.findViewById(R.id.callEarning);
            todayEarning = view.findViewById(R.id.todayEarning);
            brand = view.findViewById(R.id.brand);
            closingBalanceEarned = view.findViewById(R.id.closingBalanceEarned);
            brandImg = view.findViewById(R.id.brandImg);
            brandLayout = view.findViewById(R.id.brandLayout);
            brandImgLayout = view.findViewById(R.id.brandImgLayout);
            description = view.findViewById(R.id.description);
        }
    }

    public UearnPassbookAdapter(List<UearnRangePassbookInfo> passbookInfoList) {
        this.passbookInfoList = passbookInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passbook_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UearnRangePassbookInfo UearnRangePassbookInfo = passbookInfoList.get(position);
        holder.dateOfEarningTextView.setText(UearnRangePassbookInfo.day);
        holder.callEarning.setText(UearnRangePassbookInfo.name);
        holder.todayEarning.setText(UearnRangePassbookInfo.sign + " " + UearnRangePassbookInfo.uearned);
        if(UearnRangePassbookInfo.brand.equalsIgnoreCase("UEARN")) {
            holder.brandImgLayout.setVisibility(View.VISIBLE);
            holder.brandLayout.setVisibility(View.GONE);
        } else {
            holder.brandImgLayout.setVisibility(View.GONE);
            holder.brandLayout.setVisibility(View.VISIBLE);
            holder.brand.setText(UearnRangePassbookInfo.brand);
        }

        holder.closingBalanceEarned.setText(UearnRangePassbookInfo.closing_balance);
        holder.description.setText(UearnRangePassbookInfo.description);
    }

    @Override
    public int getItemCount() {
        return passbookInfoList.size();
    }
}