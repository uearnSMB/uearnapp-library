package smarter.uearn.money.adapters;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;

import java.util.ArrayList;
import java.util.List;

public class AutoDialListAdapter extends RecyclerView.Adapter<AutoDialListAdapter.DataObjectHolder> {
    private static String LOG_TAG = "AutoDialListAdapter";
    private List<CustomerLite> mDataset;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView customer;
        TextView status;
        TextView substatus1;
        TextView substatus2;
        TextView leadSource;
        WebView notes;
        WebView customkvs;

        public DataObjectHolder(View itemView) {
            super(itemView);
            customer = itemView.findViewById(R.id.customerName);
            status = itemView.findViewById(R.id.status);
            substatus1 = itemView.findViewById(R.id.substatus1);
            substatus2 = itemView.findViewById(R.id.substatus2);
            leadSource = (TextView) itemView.findViewById(R.id.leadSource);
            notes = itemView.findViewById(R.id.notes);
            customkvs = itemView.findViewById(R.id.customkvs);

            customkvs.getSettings().setJavaScriptEnabled(true);
            customkvs.getSettings().setBlockNetworkLoads(false);
            if (Build.VERSION.SDK_INT >= 19) {
                customkvs.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
        }
    }

    public AutoDialListAdapter(List<CustomerLite> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_row, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        String name = mDataset.get(position).customerName;
        if (name != null && !name.isEmpty()) {
            holder.customer.setText(name);
        } else {
            String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
            if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {

            } else {
                holder.customer.setText("No Name");
            }
        }

        String status = mDataset.get(position).status;
        if (status != null && !status.isEmpty() && !status.equals("null")) {
            holder.status.setText(status);
        } else {
            String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING,"");
            if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {

            } else {
                holder.status.setText("NEW DATA");
            }
        }

        String subStatus1 = mDataset.get(position).substatus1;
        if (subStatus1 != null && !subStatus1.isEmpty() && !subStatus1.equals("null")) {
            holder.substatus1.setText(subStatus1);
        }

        String subStatus2 = mDataset.get(position).substatus2;
        if (subStatus2 != null && !subStatus2.isEmpty() && !subStatus2.equals("null")) {
            holder.substatus2.setText(subStatus2);
        }

        String leadSource = mDataset.get(position).leadSource;
        if (leadSource != null && !leadSource.isEmpty() && !leadSource.equals("null")) {
            holder.leadSource.setText(leadSource);
        }

        String notes = mDataset.get(position).notes;
        if (notes != null && !notes.isEmpty() && !notes.equals("null")) {
            holder.notes.loadData(notes, "text/html", "UTF-8");
        }

        String customkvs = mDataset.get(position).customkvs;
        if (customkvs != null && !customkvs.isEmpty() && !customkvs.equals("null")) {
            holder.customkvs.loadData(customkvs, "text/html", "UTF-8");
        }
    }

    public void addItem(CustomerLite dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        if(mDataset != null && mDataset.size() > 0)
            return mDataset.size();
        else
            return 0;
    }
}