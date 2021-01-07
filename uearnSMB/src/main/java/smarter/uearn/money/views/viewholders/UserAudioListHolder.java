package smarter.uearn.money.views.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.views.CallRecordsView;

/**
 * Created by Srinath on 06-09-2017.
 */

public class UserAudioListHolder extends RecyclerView.ViewHolder {

    public TextView status, date, time;
    public CallRecordsView smartAudioPlayer;
    public UserAudioListHolder(View view) {
        super(view);
        smartAudioPlayer = view.findViewById(R.id.player1);
        status = view.findViewById(R.id.record_status);
        date = view.findViewById(R.id.dateTv);
        time = view.findViewById(R.id.timeTv);
    }
}
