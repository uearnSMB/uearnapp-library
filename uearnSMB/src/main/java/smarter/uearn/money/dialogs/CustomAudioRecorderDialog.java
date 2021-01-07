package smarter.uearn.money.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import smarter.uearn.money.callrecord.MediaRecordWrapper;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.upload.DataUploadUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created on 20/04/17.
 *
 * @author Dilip
 * @version 1.0
 */

public class CustomAudioRecorderDialog implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    private boolean recording = true;
    private boolean playing = true;
    private TextView mRecordingText, mPlayPauseText;
    private LinearLayout mMediaControls;
    private AlertDialog alertDialog;
    private MediaRecordWrapper mediaRecordWrapper;
    private String path;
    private MediaPlayer mediaPlayer;
    Context mContext;

    public void buildAudioRecorder(Context context, Activity activity) {
        mContext = context;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_audio_recorder, null);
        builder.setView(dialogView);

        SecureRandom rand = new SecureRandom();
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/notesaudio" + rand.nextInt() + ".mp4"; // Previously set as 3gp
        mediaRecordWrapper = new MediaRecordWrapper(context);
        mediaPlayer = new MediaPlayer();

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        mRecordingText = dialogView.findViewById(R.id.tv_start_stop_recording);
        mRecordingText.setOnClickListener(this);

        mPlayPauseText = dialogView.findViewById(R.id.tv_media_play);
        mPlayPauseText.setOnClickListener(this);

        TextView mStopText = dialogView.findViewById(R.id.tv_media_stop);
        mStopText.setOnClickListener(this);

        ImageView mClose = dialogView.findViewById(R.id.iv_close);
        mClose.setOnClickListener(this);

        mMediaControls = dialogView.findViewById(R.id.llMediaControls);

        Button btnSave = dialogView.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        int id = view.getId();
        if (id == R.id.tv_start_stop_recording) {
            if (recording) {
                recording = false;
                mediaRecordWrapper.startRecording(path, MediaRecorder.AudioSource.MIC);
                mRecordingText.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_media_stop, 0, 0);
            } else {
                mediaRecordWrapper.stopRecording();
                mMediaControls.setVisibility(View.VISIBLE);
                recording = true;
                mRecordingText.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_start_recording, 0, 0);
            }
        } else if (id == R.id.tv_media_play) {
            if (playing) {
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer = MediaPlayer.create(mContext, Uri.parse(path));
                mediaPlayer.start();

                playing = false;
                mPlayPauseText.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_media_pause, 0, 0);
            } else {
                playing = true;
                mPlayPauseText.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_start_recording, 0, 0);
            }
        } else if (id == R.id.tv_media_stop) {
            playing = true;
            mPlayPauseText.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_start_recording, 0, 0);
        } else if (id == R.id.iv_close) {
            alertDialog.dismiss();
        } else if (id == R.id.btn_save) {
            UploadNotesAudio uploadNotesAudio = new UploadNotesAudio();
            uploadNotesAudio.execute();
            alertDialog.dismiss();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playing = true;
        mPlayPauseText.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_start_recording, 0, 0);
    }

    class UploadNotesAudio extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (path != null) {
                uploadAudioFile();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mContext != null) {
                Toast.makeText(mContext, "Your audio recording notes has been successfully saved.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadAudioFile() {
        String filePath = path;
        JSONObject response = DataUploadUtils.uploadNotesAudioFileToServer(filePath, "", "", "", Urls.getUploadFileUrl());
        try {
            if (response != null && response.getString("url") != null) {
                String audioUrl = response.getString("url");
                ApplicationSettings.putPref(AppConstants.AFTER_CALL_AUDIO_URL, audioUrl);
                //Log.d("notes url: ", "url:" + audioUrl);
                new File(path).delete();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
