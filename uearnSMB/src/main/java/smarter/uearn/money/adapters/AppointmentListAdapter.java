package smarter.uearn.money.adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.appointment.AppointmentViewActivity;
import smarter.uearn.money.models.AppointmentModel;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.SmartUser;
import smarter.uearn.money.services.DeleteEventService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.views.viewholders.AppointmentViewHolder;

/**
 * Created on 27/03/17.
 *
 * @author Dilip
 * @version 1.0
 */

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentViewHolder> {

    Context context;
    private ArrayList<AppointmentModel> mAppointmentList = new ArrayList<>();
    MediaPlayer mMediaPlayer;
    boolean play = false;

    public AppointmentListAdapter(ArrayList<AppointmentModel> items) {
        mAppointmentList = items;
    }


    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment_list, parent, false);

        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, final int position) {

        final AppointmentModel modelItem = mAppointmentList.get(position);
        final String appointmentId = modelItem.getmAppointmentId();

        final String toname = modelItem.getmName();
        final String toNumber = modelItem.getmPhone();
        String notes = modelItem.getmNote();

        final AppointmentViewHolder holder1 = holder;

        String lateststatus = CommonUtils.getStatusFromSmartContactTable(context, toNumber);

        if (toname != null && !toname.equals("")) {
            holder.mPhoneText.setText(toname);
        } else if (toNumber != null && !toNumber.equals("")) {
            holder.mPhoneText.setText(toNumber);
        }

        if (notes != null) {
            holder.mSubjectText.setText(notes);
        }

        if (modelItem.getmUploadStatus() == 0) {
            holder.mUploadImage.setVisibility(View.VISIBLE);
        } else {
            holder.mUploadImage.setVisibility(View.INVISIBLE);
        }

        if (lateststatus != null && !lateststatus.equals("")) {
            holder.mSalesStageText.setText(lateststatus);
        }

        long start = modelItem.getmStartTime();
        long end = modelItem.getmEndTime();
        long durationMills = end - start;
        long min = durationMills / (1000 * 60);
        String timeDuration = CommonOperations.getTimeduration(min);
        long current = System.currentTimeMillis();
        long timeDiff = 0L;
        if (start > current) {
            timeDiff = start - current;
        } else {
            timeDiff = 0l;
        }

        final String path = getAudioPath(modelItem.getmLongItemId());
        if (path != null && !(path.isEmpty())) {
            holder.audioImage.setVisibility(View.VISIBLE);
        } else {
            holder.audioImage.setVisibility(View.INVISIBLE);
        }

        String timeString = DateFormat.format("hh:mm a", start).toString();

        Date showDate = new Date(start);
        String showDateTime = DateFormat.format("dd-MM-yy ", showDate).toString();
        long mins = timeDiff / (1000 * 60);
        if (mins > 60) {
            Long hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
            Long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(timeDiff));
            if (hours < 12) {
                holder.mTimeText.setText(hours + " hr " + minutes + " min");
            } else {
                holder.mTimeText.setText(showDateTime + "\n" + timeString);
            }
        } else {
            holder.mTimeText.setText(mins + " min");
        }

        if (timeDuration != null) {
            holder.mDurationText.setText(timeDuration);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AppointmentViewActivity.class);
                intent.putExtra("id", modelItem.getmLongItemId());
                intent.putExtra("disable_ok", true);
                context.startActivity(intent);
            }
        });

        holder.play_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer = new MediaPlayer();
                try {
                    if (CommonUtils.isNetworkAvailable(context)) {
                        if (path != null && !(path.isEmpty())) {
                            if (!play) {
                                play = true;
                                mMediaPlayer = new MediaPlayer();
                                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mMediaPlayer.setDataSource(path);
                                mMediaPlayer.prepareAsync();
                                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        if (mMediaPlayer != null) {
                                            mMediaPlayer.start();
                                            holder1.audioImage.setImageResource(R.drawable.ic_pause_circle_filled_black_24px);
                                        }
                                    }
                                });
                                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        if (mMediaPlayer != null) {
                                            if (mMediaPlayer.isPlaying()) {
                                                mMediaPlayer.pause();
                                                mMediaPlayer.stop();
                                                mMediaPlayer.reset();
                                            }
                                            mMediaPlayer.release();
                                            mMediaPlayer = null;
                                            holder1.audioImage.setImageResource(R.drawable.ic_play_circle_outline_black_24px);
                                        }

                                    }
                                });
                            } else {
                                play = false;
                                if (mMediaPlayer != null) {
                                    mMediaPlayer.pause();
                                    mMediaPlayer.stop();
                                    mMediaPlayer.release();
                                    mMediaPlayer = null;
                                }
                                holder1.audioImage.setImageResource(R.drawable.ic_play_circle_outline_black_24px);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to Delete?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAppointmentList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, mAppointmentList.size());
                                deleteAppointment(modelItem.getmLongItemId(), appointmentId, modelItem.getmExternalCalendar());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return false;
            }
        });

        holder.call_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toNumber != null && !toNumber.equals("")) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    String num = "tel:" + toNumber;
                    intent.setData(Uri.parse(num));

                    NotificationData.notificationData = true;
                    NotificationData.appointment_db_id = modelItem.getmLongItemId();
                    NotificationData.appointment_id = modelItem.getmAppointmentId();
                    NotificationData.statusString = modelItem.getmStatus();
                    NotificationData.notes_string = modelItem.getmNote();
                    NotificationData.order_value = modelItem.getmOrderPotential();
                    NotificationData.isAppointment = true;
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAppointmentList.size();
    }

    private void deleteAppointment(final long dbId, String appointment_id, String external_calendar_reference) {

        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SmartUser smartUser = SmarterSMBApplication.SmartUser;
        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        getCalendarEntryInfo.setAppointment_id(appointment_id);
        getCalendarEntryInfo.setExternal_calendar_reference(external_calendar_reference);
        if (smartUser != null)
            getCalendarEntryInfo.setUser_id(smartUser.getId());

        if (CommonUtils.isNetworkAvailable(context)) {
            db.delete("remindertbNew", "_id=" + dbId, null);
            Intent intentService = new Intent(context, DeleteEventService.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("calenderEntryInfo", getCalendarEntryInfo);
            intentService.putExtras(bundle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentService);
            } else {
                context.startService(intentService);
            }

            ContentValues values = new ContentValues();
            values.put("APPOINTMENT_TYPE", "Delete_Appointment");
            values.put("UPLOAD_STATUS", 0);
            values.put("STATUS", "Delete");
            db.update("remindertbNew", values, "_id=" + dbId, null);

            if (db != null && db.isOpen()) {
                db.close();
            }

            Intent aIntent = new Intent(context, ReuploadService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(aIntent);
            } else {
                context.startService(aIntent);
            }
        }
    }

    private String getAudioPath(Long id) {
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String path = "";
        String number = "";
        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + id, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            number = cursor.getString(cursor.getColumnIndex("TO1"));
            path = cursor.getString(cursor.getColumnIndex("CALLREC_URL"));
        }
        if ((path == null || path.isEmpty()) && number != null && !(number.isEmpty())) {
            String selection = "TO1=" + "'" + number + "'";
            Cursor cursor2 = db.query("remindertbNew", null, selection, null, null, null, "START_TIME DESC");

            if (cursor2 != null && (cursor2.getCount() > 0)) {
                try {
                    cursor2.moveToFirst();
                    do {
                        String tempPath = cursor2.getString(cursor2.getColumnIndex("CALLREC_URL"));
                        if (tempPath != null && !(tempPath.isEmpty())) {
                            path = tempPath;
                            return path;
                        }
                    } while (cursor2.moveToNext());

                } catch (Exception e) {

                }
            }

            if (cursor2 != null && !cursor2.isClosed()) {
                cursor2.close();
            }
            if (path != null && !(path.isEmpty())) {
                return path;
            } else {
                return "";
            }
        }
        return path;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
