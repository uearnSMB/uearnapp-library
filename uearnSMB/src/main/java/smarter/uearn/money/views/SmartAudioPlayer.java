package smarter.uearn.money.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import smarter.uearn.money.activities.appointment.AppointmentViewActivity;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.ApplicationSettings;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/* Same as media player, except that this will handle an associated play/pause toggle button, seekbar, time remaining
 */

public class SmartAudioPlayer extends LinearLayout implements OnPreparedListener, OnCompletionListener, OnSeekBarChangeListener, OnClickListener {
	
	private SeekBar seekbar;
	private ToggleButton playButton;
	private TextView timeLeftView, timeRightView;

	private MediaPlayer mediaPlayer;
	private double duration;
	private boolean isPlaying = false;
	private Uri sourceUri;
	private String sourceUrl;
	private Handler durationHandler = new Handler();
	private ProgressDialog waitDialog;
	private boolean isPreparing = false;
	
	private Context context;

	public SmartAudioPlayer(Context context) {
		super(context);
        this.context = context;
		init();
	}

	public SmartAudioPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.context = context;
		init();
	}

	public void setEnabled(boolean enabled) {
		if (!enabled) {
			finish();
		}
		playButton.setEnabled(enabled);
	}

	private void init() {
		inflate(getContext(), R.layout.smart_player, this);
		seekbar = findViewById(R.id.player_seekbar);
		playButton = findViewById(R.id.player_play);
		timeLeftView = findViewById(R.id.player_timeleft);
		timeRightView = findViewById(R.id.player_timeright);
		playButton.setEnabled(false); //Enable only when datasource is attached
		//setButtonState(false);
		setRemainingTime(0);
		playButton.setOnClickListener(this);
		seekbar.setOnSeekBarChangeListener(this);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
		//google analytics for play button click in alarm popup
		if(context instanceof AppointmentViewActivity){
		}
	}

	private void setRemainingTime(double timeRemaining) {
		timeLeftView.setText(String.format("%02d:%02d ", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
	}
	
	private void updateProgress() {
		int timeElapsed = mediaPlayer.getCurrentPosition();
        Log.i("TAG", "Time Elapsed " + timeElapsed);
        double timeRemaining = duration - timeElapsed;


        timeRightView.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) duration), TimeUnit.MILLISECONDS.toSeconds((long) duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) duration))));
        
        if(timeRemaining > 0){
        	setRemainingTime(timeElapsed);
        	seekbar.setProgress(timeElapsed);
        } else {
        	seekbar.setProgress(0);
        	onCompletion(mediaPlayer);
        }
	}

	//TODO: Do Toast on error
	public void setDataSource(Uri uri) {
		try {
			sourceUri = uri;
			mediaPlayer.reset();
			mediaPlayer.setDataSource(context, uri);
			isPreparing=true;
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
			mediaPlayer.reset();
		}
	}
	
	//TODO: Do Toast on error
	public void setDataSource(String url) {
		int currentVersion = Build.VERSION.SDK_INT;

		if (currentVersion < Build.VERSION_CODES.HONEYCOMB) {
			url = url.replace("https://", "http://");
		}
		try {
			sourceUrl = url;
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url);
			isPreparing = true;
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
	//	Log.i("Tag", "Media Preparation Done");
		if(waitDialog!=null){
			waitDialog.dismiss();
		}
		isPreparing=false;
		playButton.setEnabled(true);
		if(isPlaying){
			start();
		}
		if(duration == 0) {
			duration = (mp.getDuration());
		}
		seekbar.setMax((int)duration);
	}
	
	public void start() {

		if(mediaPlayer != null){
			ApplicationSettings.mediaPlayer = mediaPlayer; // Added by Dilip
			mediaPlayer.start();
			durationHandler.postDelayed(updateSeekBarTime, 100);
		}
	}

	public void pause() {
		//setButtonState(false);
		if(mediaPlayer != null) {
			mediaPlayer.pause();
			durationHandler.removeCallbacks(updateSeekBarTime);
		}
	}
	
	public void release() {
		if (mediaPlayer != null) {
			pause();
			mediaPlayer.pause();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	public void finish() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			playButton.setChecked(false);
			setRemainingTime(0);
			durationHandler.removeCallbacks(updateSeekBarTime);
			isPlaying = false;
			seekbar.setProgress(0);
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer arg0) {
		finish();
		/* Android MediaPlayer doesn't work as per documentation */
		if (sourceUrl != null) {
			setDataSource(sourceUrl);
		}else if(sourceUri != null){
			setDataSource(sourceUri);
		}
	}
	
	private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
        	if(mediaPlayer!=null){
        		updateProgress();
        		durationHandler.postDelayed(this, 100);
        	}
        }
    };



	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		try {
			 if (mediaPlayer != null || mediaPlayer.isPlaying()) {
			 if (fromUser)
				 mediaPlayer.seekTo(progress);
			 } else if (mediaPlayer == null) {
				 Toast.makeText(context, "Media is not running", Toast.LENGTH_SHORT).show();
				 seekBar.setProgress(0);
			 }
		 } catch (Exception e) {
			 //Log.e("seek bar", "" + e);
			 seekBar.setEnabled(false);
		 }
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		seekbar.setSecondaryProgress(seekbar.getProgress());
	}

	@Override
	public void onClick(View v) {
		//Log.i("Tag", "IsPlaying " + isPlaying);
		if (!isPlaying) {
			if(isPreparing){
				waitDialog=new ProgressDialog(context);
				waitDialog.setTitle("Buffering Audio");
				waitDialog.setMessage("Please Wait..");
				waitDialog.setCancelable(true);
				waitDialog.show();
			}else{
				start();
			}
			isPlaying = true;
		} else {
			pause();
			isPlaying = false;
		}
	}

    public void setPlayButton(boolean play) {
        //Log.i("ViewDetails","play is smart Audio player is "+play);
        if(play) {
            playButton.setChecked(true);
            start();
            isPlaying = true;
        }
        else {
            playButton.setChecked(false);
            isPlaying = false;
            pause();
        }
    }

	/*
	 * Method to media player exception
	 * Check if media player is playing then stop
	 *
	 * @Params : Void
	 * @Return : Void
	 *
	 * Called within SmartAudioRecorder
	 *
	*/
	public void stopMediaPlayer(){
		if(mediaPlayer != null && mediaPlayer.isPlaying()){
			mediaPlayer.pause();
		}
	}

    public boolean isPlaying() {
        return isPlaying;
    }


}
