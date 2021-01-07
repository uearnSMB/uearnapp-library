package smarter.uearn.money.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import smarter.uearn.money.models.ScanLog;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.views.viewholders.ScanLogHolder;

import java.util.ArrayList;

public class ScanLogDialogAdapter extends RecyclerView.Adapter<ScanLogHolder> {

    private ArrayList<ScanLog> scans = new ArrayList<>();
    private Context context;
    private Activity activity;
    private ArrayList<String> private_contacts_array = new ArrayList<>();
    private OnCallLogClicked mOnCallLogClicked;

    public interface OnCallLogClicked {
        void onLogItemClicked(String number);
    }

    public ScanLogDialogAdapter(ArrayList<ScanLog> logs, Context cntxt, Activity acty, OnCallLogClicked onCallLogClicked) {
        this.scans = logs;
        this.context = cntxt;
        this.activity = acty;
        this.mOnCallLogClicked = onCallLogClicked;
    }

    @Override
    public ScanLogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_log, parent, false);
        return new ScanLogHolder(view);
    }

    @Override
    public void onBindViewHolder(ScanLogHolder holder, int position) {
        ScanLog singleLog = scans.get(position);
        final String numberValue = singleLog.getNumber();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnCallLogClicked.onLogItemClicked(numberValue);
            }
        });

        holder.tvNumber.setText(singleLog.getNumber());
        String name = getContactName(singleLog.getNumber());
        if (name != null) {
            if (!name.isEmpty()) {
                holder.tvNumber.setText(name);
            }
        }

        final String phoneNumber = singleLog.getNumber();

        holder.tvDate.setText(singleLog.getDateTime());
        holder.tvCallType.setText(singleLog.getCallType());
        holder.tvDuration.setText(singleLog.getCallDuration() + " min");

        if (singleLog.getCallType().equalsIgnoreCase("missed")) {
            holder.mCallIcon.setColorFilter(context.getResources().getColor(android.R.color.holo_red_light));
            holder.mCallIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.missed_call));
        } else if (singleLog.getCallType().equalsIgnoreCase("outgoing")) {
            holder.mCallIcon.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            holder.mCallIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.outgoing_callon));
        }

        holder.mCallIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                activity.startActivity(callIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scans.size();
    }

    public String getContactName(String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return "";
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}
