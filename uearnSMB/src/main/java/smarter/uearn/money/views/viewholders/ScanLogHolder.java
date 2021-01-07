package smarter.uearn.money.views.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import smarter.uearn.money.R;


/**
 * Created by dilipkumar4813 on 21/02/17.
 */

public class ScanLogHolder extends RecyclerView.ViewHolder {

    public TextView tvNumber, tvDate, tvCallType, tvDuration;
    public ImageView mCallIcon;

    public ScanLogHolder(View itemView) {
        super(itemView);

        tvNumber = itemView.findViewById(R.id.tvNumber);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvCallType = itemView.findViewById(R.id.tvCallType);
        tvDuration = itemView.findViewById(R.id.tvDuration);
        mCallIcon = itemView.findViewById(R.id.iv_call_icon);
    }
}
