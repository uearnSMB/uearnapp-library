package smarter.uearn.money.views.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import smarter.uearn.money.R;

/**
 * Created by Srinath on 31-07-2017.
 */

public class DashboardViewHolder extends RecyclerView.ViewHolder {

    public TextView firstCall;
    public TextView followup;
    public TextView title;
    public TextView userTotal;
    public ImageView phone;

    public DashboardViewHolder(View view) {
        super(view);

        firstCall = view.findViewById(R.id.firstcall);
        followup = view.findViewById(R.id.followup);
        phone = view.findViewById(R.id.phone);
        title = view.findViewById(R.id.titleTview);
        userTotal = view.findViewById(R.id.totalTv);
    }
}
