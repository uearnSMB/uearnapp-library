package smarter.uearn.money.views.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import smarter.uearn.money.R;

/**
 * Created by dilipkumar4813 on 27/03/17.
 */

public class AppointmentViewHolder extends RecyclerView.ViewHolder {

    public TextView mPhoneText, mSubjectText, mSalesStageText;
    public TextView mTimeText, mDurationText;
    public ImageView mReminderImage, mUploadImage, audioImage, callIcon;
    public LinearLayout call_layout, play_layout;

    public AppointmentViewHolder(View itemView) {
        super(itemView);

        mReminderImage = itemView.findViewById(R.id.reminder_icon);
        mPhoneText = itemView.findViewById(R.id.phoneNo);
        mSubjectText = itemView.findViewById(R.id.subject);
        mSalesStageText = itemView.findViewById(R.id.latestSalesStage);
        mTimeText = itemView.findViewById(R.id.time);
        mDurationText = itemView.findViewById(R.id.duration);
        mUploadImage = itemView.findViewById(R.id.upload_icon);
        audioImage = itemView.findViewById(R.id.audio_icon);
        callIcon = itemView.findViewById(R.id.call_icon);
        call_layout = itemView.findViewById(R.id.call_layout);
        play_layout = itemView.findViewById(R.id.play_layout);
    }
}
