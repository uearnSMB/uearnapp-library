package smarter.uearn.money.views;

import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import smarter.uearn.money.R;

import java.io.File;
import java.io.IOException;

/* Same as media player, except that this will handle an associated play/pause toggle button, seekbar, time remaining
 * STORARGE_DIR is changed to callrecorder folder
 * Filename extension changed from .amr to .amr.smb
 */

public class SmartAudioRecorder extends LinearLayout implements OnClickListener {

    private ToggleButton recButton;
    private SmartAudioPlayer audioPlayer;
    private MediaRecorder mediaRecorder;
    private String recordFilePath;
    private TextView timerText;
    //public static final String STORAGE_DIR = "SmarterBiz Recordings";
    public static final String STORAGE_DIR = "callrecorder";
    private int cnt;

    CountDownTimer t;
    private boolean isRecording = false;
    private Context context;

    public SmartAudioRecorder(Context context) {
        super(context);
        init();
        this.context = context;
    }

    public SmartAudioRecorder(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.context = context;
    }

    public String getRecordFile() {
        return recordFilePath;
    }

    public void release() {
        if (mediaRecorder != null) {
            stopAudioRecord();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.smart_recorder, this);
        timerText = findViewById(R.id.timerText);

        recButton = findViewById(R.id.recorder_record_button);
        audioPlayer = findViewById(R.id.recorder_smart_player);
        recButton.setOnClickListener(this);

        createDirectory(STORAGE_DIR);

        t = new CountDownTimer(Long.MAX_VALUE, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                cnt++;
                String time = new Integer(cnt).toString();

                long millis = cnt;
                int seconds = (int) (millis / 60);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerText.setText(String.format("%d:%02d:%02d", minutes, seconds, millis));

            }

            @Override
            public void onFinish() {
            }
        };
    }

    private void createDirectory(String dirName) {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), dirName);
        /*File myDirectory;
        if(context != null) {
            myDirectory = new File(Environment.getExternalStorageDirectory().toString(), "/Android/data/" + context.getPackageName() + "/callrecorder/");
        } else {
            myDirectory = new File(Environment.getExternalStorageDirectory(), dirName);
        }*/
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
    }

    @Override
    public void onClick(View v) {
        //Log.i("Tag", "IsRecording " + recButton.isChecked());

        if (recButton.isChecked()) {
           // audioPlayer.setEnabled(false); //Cannot play while recording

            recButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.girl));
            //we are recording over; delete existing file
            if (recordFilePath != null) {
                File f = new File(recordFilePath);
                f.delete();
                recordFilePath = null;
            }
            startAudioRecord();
        } else {
            stopAudioRecord();

            recButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.mic));
            if (recordFilePath != null) {
                Uri fileUri = Uri.parse(recordFilePath);
                Log.d("fileUri:---", String.valueOf(fileUri));
                audioPlayer.setDataSource(fileUri);    //player will get auto enabled
            }
        }
    }

    public void startAudioRecord() {
        try {
            t.start();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            recordFilePath = Environment.getExternalStorageDirectory() + "/" + STORAGE_DIR + "/" + System.currentTimeMillis() + ".amr.smb";
            mediaRecorder.setOutputFile(recordFilePath);

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IllegalStateException e) {
            //Log.e("Caught Exception 1 ", "true" + e.toString());
        } catch (IOException e) {
            //Log.e("Caught Exception1 ", "true" + e.toString());
        } catch (RuntimeException e) {
            //Log.e("Caught Exception1 ", "true");
        }
    }

    public void stopAudioRecord() {
        try {
            if(isRecording()) {
                t.cancel();
                mediaRecorder.stop();
                mediaRecorder.release();
                isRecording = false;
            }
        } catch (Exception e) {
            //Log.e("Caught Exception2 ", "true");
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void stopAudio() {
        audioPlayer.stopMediaPlayer();
    }
}
