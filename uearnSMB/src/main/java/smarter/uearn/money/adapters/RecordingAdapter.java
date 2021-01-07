package smarter.uearn.money.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.views.CallRecordsView;
import smarter.uearn.money.views.viewholders.UserAudioListHolder;

import java.util.ArrayList;
import java.util.Date;

public class RecordingAdapter extends RecyclerView.Adapter<UserAudioListHolder>  {

    private  Context context;
    ArrayList<String[]> list;
    private CallRecordsView smartAudioPlayer;
    LayoutInflater mInflater;
    UserAudioListHolder holder2;

    public RecordingAdapter(Context context, ArrayList<String[]> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public UserAudioListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.callrecording_dailog, parent, false);
        return new UserAudioListHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(UserAudioListHolder holder, int position) {
        try {
            final UserAudioListHolder holder1 = holder;
            holder2 = holder;
            String[] arr= list.get(position);
            final String playUrl = arr[0];
            String startTime = arr[1];
            String record_status = arr[2];
            if (playUrl != null && !playUrl.equals(" ") && !playUrl.equals("")) {
                holder1.smartAudioPlayer.setDataSource(playUrl);
            }
            holder1.smartAudioPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (playUrl != null && !playUrl.equals(" ") && !playUrl.equals("")) {
                        holder1.smartAudioPlayer.setDataSource(playUrl);
                    }
                }
            });
            if(startTime != null && !startTime.isEmpty()) {
                Long millis = CommonUtils.stringToMilliSec(startTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                String timeString = DateFormat.format("hh:mm a", millis).toString();
                Date showDate = new Date(millis);
                String showDateTime = DateFormat.format("dd MMM ", showDate).toString();
                holder1.date.setText(showDateTime);
                holder1.time.setText(timeString);
                holder1.status.setText(record_status);
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

