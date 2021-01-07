package smarter.uearn.money.speechapi;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ServiceToconvertText extends Service {

    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    protected final Messenger mServerMessenger = new Messenger(new IncomingHandler(this));

    protected boolean mIsListening;
    protected volatile boolean mIsCountDownOn;
    private boolean mIsStreamSolo;

    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    static final int MSG_RECOGNIZER_CANCEL = 2;
    private StringBuilder sb = new StringBuilder();
    private File scriptfile;
    private FileWriter writer;
    private String TAG = "ServiceToText";
    private CountDownTimer mNoSpeechCountDown;
    public boolean muted = false;


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        try {
            if (!muted) {
                   /* mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);*/

              /*  AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE)
                int result = am.requestAudioFocus(afChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    am.registerMediaButtonEventReceiver(RemoteControlReceiver);
                }
                am.abandonAudioFocus(afChangeListener);*/

                muted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Added
         /*mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);*/

        if(ApplicationSettings.containsPref(AppConstants.TRANSCRIPT_LANG)) {
            String transcipt_lang = ApplicationSettings.getPref(AppConstants.TRANSCRIPT_LANG, "");
            if(transcipt_lang != null && !(transcipt_lang.isEmpty())) {
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, transcipt_lang);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        transcipt_lang);
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                        transcipt_lang);
            } else {
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        "en-IN");

                mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                        "en-IN");
            }
        } else {
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    "en-IN");

            mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                    "en-IN");
        }

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        mNoSpeechCountDown = new CountDownTimer(5000, 5000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                mIsCountDownOn = false;
                Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
                try {
                    mServerMessenger.send(message);
                    message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                    mServerMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "smarter.uearn.money";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.small_logo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected  class IncomingHandler extends Handler {
        private WeakReference<ServiceToconvertText> mtarget;

        IncomingHandler(ServiceToconvertText target) {
            mtarget = new WeakReference<ServiceToconvertText>(target);
        }


        @Override
        public void handleMessage(Message msg) {
            final ServiceToconvertText target = mtarget.get();

            switch (msg.what) {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (!target.mIsListening)
                    {
                        target.mSpeechRecognizer.startListening(target.mSpeechRecognizerIntent);
                        target.mIsListening = true;
                        //Log.d(TAG, "message start listening"); //$NON-NLS-1$
                    }
                    break;

                case MSG_RECOGNIZER_CANCEL:
                    target.mIsListening = false;
                    target.mSpeechRecognizer.cancel();
                    target.mIsListening = false;
                    //Log.d(TAG, "message canceled recognizer"); //$NON-NLS-1$
                    break;
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if(writer != null) {
            try {
                writer.append(sb.toString() + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (mIsCountDownOn) {
                mNoSpeechCountDown.cancel();
            }
            if (mSpeechRecognizer != null) {
                mSpeechRecognizer.destroy();
            }
            if(muted) {
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                muted = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected class SpeechRecognitionListener implements RecognitionListener {

        int i = 0;
        @Override
        public void onBeginningOfSpeech() {
            // speech input will be processed, so there is no need for count down anymore
            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            //Log.d("ServiceTotext", "onBeginingOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {
            //Log.d("ServiceToTextService", "BufferedReceiver");
        }

        @Override
        public void onEndOfSpeech() {
            //Log.d("ServiceToText", "onEndOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onError(int error)
        {
            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            mIsListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try
            {
                mServerMessenger.send(message);
            }
            catch (RemoteException e)
            {

            }
            //Log.d("ServicetoText", "error = " + error); //$NON-NLS-1$
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {
            //Log.d("ServiceToTextService", "EventType"+eventType);
        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {
            //Log.d("ServiceToTextService", "onPartialForSpeech");
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mIsCountDownOn = true;
                mNoSpeechCountDown.start();

            }
            //Log.d("ServiceToTextService", "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results) {
            i++;
            ArrayList<String> arrayList = results.getStringArrayList("results_recognition");
            sb.append("Speech "+i +"# "+arrayList.get(0) + "\n");
            //Log.d("ServiceToTextService", sb.toString()); //$NON-NLS-1$
            mIsCountDownOn = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
            try {
                mServerMessenger.send(message);
                message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onRmsChanged(float rmsdB) {
            //Log.d("ServiceToTextService", "OnRMSChanged"); //$NON-NLS-1$
        }

    }

    private String getFileName() {
        String user_id = "", date = "";

        if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
            user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Calendar cal = Calendar.getInstance();
            Date d = new Date();
            cal.setTime(d);
            Date startTime = cal.getTime();
            String requiredStartTime = CommonUtils.getTimeFormatInISO(startTime);
            requiredStartTime = requiredStartTime.replace(":", "-");
            requiredStartTime = requiredStartTime.replace(".", "-");
            date = requiredStartTime;
        }
        String prefix = user_id + date;
        prefix += "" + "-";
        return prefix;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File root = new File(Environment.getExternalStorageDirectory(), "TextScripts");
        if (!root.exists()) {
            root.mkdirs();
        }
        if(intent != null && intent.hasExtra("transcriptionPath")) {
            String path = intent.getStringExtra("transcriptionPath");
            if (path != null && !(path.isEmpty())) {
                try {
                    scriptfile = new File(path);
                    writer = new FileWriter(scriptfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }

        return Service.START_STICKY;
    }
}
