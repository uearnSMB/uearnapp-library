package smarter.uearn.money.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import smarter.uearn.money.R;
import smarter.uearn.money.models.UearnVoiceTestInfo;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.views.SmartAudioPlayer;
import smarter.uearn.money.views.VisualizerView;
import smarter.uearn.money.views.events.HeadPhoneReciever;

public class StartUearnVoiceRecord extends AppCompatActivity implements View.OnClickListener {

    private HeadPhoneReciever myReceiver;
    private Button doneButton, startOverButton;
    private TextView hints1Tv, hints2Tv, questionTv;
    AlertDialog alertDialog;
    private MediaRecorder mediaRecorder;
    private String recordFilePath;
    private boolean isRecording = false;
    public static final String STORAGE_DIR = "callrecorder";
    private int questionLength, numberOfTime = 0;
    private Handler handler, uidelayHandler;
    private long startTime, currentTime, finishedTime = 0L;
    private int duration = 22000;
    private int endTime = 0;
    private UearnVoiceTestInfo voiceInfo;
    private TextView ready, placemic, readtext, timer, starttv;
    private ImageButton micButton;
    public int seconds = 05;
    Timer t;
    private LinearLayout buttonLayout;
    private SmartAudioPlayer audioPlayer;
    private VisualizerView visualizerView;
    private TextView headerTitle;
    private ImageView profileImage, image_sync, image_notification;
    private LinearLayout notificationLayout;
    private TextView readytv, placemictv;
    private TextView voiceuploaderror;
    ProgressDialog progressBar;
    Runnable questionTextRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_uearn_voice_record);
        SmarterSMBApplication.currentActivity = this;

        Intent i = getIntent();
        voiceInfo = (UearnVoiceTestInfo) i.getSerializableExtra("VoiceInfo");

        timer = findViewById(R.id.timer);
        starttv = findViewById(R.id.startTv);
        readytv = findViewById(R.id.ready);
        placemictv = findViewById(R.id.placemic);

        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(this);
        doneButton.setTextColor(Color.WHITE);
        doneButton.setEnabled(false);
        doneButton.setClickable(false);
        doneButton.setBackground(this.getResources().getDrawable(R.drawable.grey_rounded_corner));

        hints1Tv = findViewById(R.id.hints1Tv);
        hints2Tv = findViewById(R.id.hints2Tv);

        micButton = findViewById(R.id.micButton);
        micButton.setOnClickListener(this);

        startOverButton = findViewById(R.id.startOverButton);
        startOverButton.setOnClickListener(this);

        buttonLayout = findViewById(R.id.buttonLayout);

        questionTv = findViewById(R.id.questionTv);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/sf-pro-display-regular.ttf");
        Typeface titleFace = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Display-Semibold.ttf");
        hints1Tv.setTypeface(titleFace);
        hints2Tv.setTypeface(face);

        String question1 = voiceInfo.para1;
        questionTv.setText(voiceInfo.para1);
        questionTv.setMovementMethod(new ScrollingMovementMethod());
        questionLength = question1.length();
        numberOfTime = questionLength / 5;

        audioPlayer = findViewById(R.id.player);
        voiceuploaderror = findViewById(R.id.tv_voice_upload_error);

        changeStatusBarColor(this);
        init();
        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("Interview");
        profileImage = (ImageView) findViewById(R.id.profile_image_back);
        profileImage.setBackgroundResource(R.drawable.ic_arrow_back_white_24px);
        profileImage.setOnClickListener(this);
        notificationLayout = (LinearLayout) findViewById(R.id.ll_notification);
        notificationLayout.setOnClickListener(this);
        visualizerView = (VisualizerView) findViewById(R.id.visualizer);
        progressBar = new ProgressDialog(StartUearnVoiceRecord.this);

        image_sync = findViewById(R.id.image_sync);
        image_notification = findViewById(R.id.image_notification);
        image_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                settingsApi(userId);
                Animation rotation = AnimationUtils.loadAnimation(StartUearnVoiceRecord.this, R.anim.button_rotate);
                rotation.setRepeatCount(Animation.RELATIVE_TO_SELF);
                v.startAnimation(rotation);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            myReceiver = new HeadPhoneReciever();
            IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(myReceiver, filter);
        } catch (Exception e) {
            Log.d("Unregister", "registerReceiver myReceiver" + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        createDirectory(STORAGE_DIR);
    }

    private void startActionOnTextView() {
        handler = new Handler();
        startTime = System.currentTimeMillis();
        currentTime = startTime;
        final int[] increment = {1};
        questionTextRunnable = new Runnable() {
            @Override
            public void run() {
                currentTime = System.currentTimeMillis();
                finishedTime = currentTime - startTime;
                if (finishedTime >= duration + 30) {
                    voiceReadingFinishedProcess();
                } else {
                    endTime = (int) (finishedTime / 52);
                    if (endTime <= questionTv.getText().length()) {
                        if (voiceInfo.isPara1 && !voiceInfo.isPara2) {
                            questionTv.setText(voiceInfo.para2);
                        } else if (voiceInfo.isPara2 && !voiceInfo.isPara3 && !voiceInfo.para3.isEmpty()) {
                            questionTv.setText(voiceInfo.para3);
                        } else {
                            questionTv.setText(voiceInfo.para1);
                        }
                        Spannable spannableString = new SpannableString(questionTv.getText().toString());
                        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green_text_color)), 0, endTime, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        questionTv.setText(spannableString);
                        if(questionTv.getLineCount()<13) {
                            questionTv.scrollTo(0, 9 * increment[0]);
                        }else {
                            questionTv.scrollTo(0, 14 * increment[0]);
                        }
                        increment[0]++;
                        handler.postDelayed(this, 1000);
                    } else {
                        voiceReadingFinishedProcess();
                    }
                }
            }
        };
        handler.postDelayed(questionTextRunnable, 9);

        /*  delay is not required for language change and ui change.this code no more use  */

        /*uidelayHandler = new Handler();
        long delayInMilliSecond = 24000;
        if (voiceInfo.getLanguageType().equalsIgnoreCase("English")) {
            delayInMilliSecond = 20000;
        }
        uidelayHandler.postDelayed(new Runnable() {
            public void run() {
                uiChange();
            }
        }, delayInMilliSecond);*/
    }

    private void voiceReadingFinishedProcess() {
        questionTv.setText(voiceInfo.para1);
        Spannable spannableString = new SpannableString(questionTv.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green_text_color)), 0, questionTv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        questionTv.setText(spannableString);
        int scroll_amount = (int) (questionTv.getLineCount() * questionTv.getLineHeight()) - (questionTv.getBottom() - questionTv.getTop());
        questionTv.scrollTo(0, scroll_amount);
        uiChange();

    }

    private void uiChange() {
        startOverButton.setTextColor(getResources().getColor(R.color.uearn_red_color));
        startOverButton.setEnabled(true);
        startOverButton.setBackground(this.getResources().getDrawable(R.drawable.edit_text_shape));
        doneButton.setTextColor(Color.WHITE);
        doneButton.setEnabled(true);
        doneButton.setClickable(true);
        doneButton.setBackground(this.getResources().getDrawable(R.drawable.red_rounded_corner));
        if (recordFilePath != null && !recordFilePath.isEmpty()) {
            audioPlayer.setDataSource(recordFilePath);
            audioPlayer.setVisibility(View.VISIBLE);
            hints1Tv.setVisibility(View.INVISIBLE);
            visualizerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    public void changeStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }

    public void restoreActionBar(String mTitle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7030A0")));
    }

    public void startAudioRecord() {
        startActionOnTextView();
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recordFilePath = Environment.getExternalStorageDirectory() + "/" + STORAGE_DIR + "/" + System.currentTimeMillis() + ".amr.smb";
            mediaRecorder.setOutputFile(recordFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            handler.post(updateVisualizer);
        } catch (IllegalStateException e) {
            Log.e("IllegalStateException:", "true" + e.toString());
        } catch (IOException e) {
            Log.e("IOException:", "true" + e.toString());
        } catch (RuntimeException e) {
            Log.e("RuntimeException:", "true");
        }
    }

    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                int x = mediaRecorder.getMaxAmplitude();
                visualizerView.addAmplitude(x);
                visualizerView.invalidate();
                handler.postDelayed(this, 40);
            }
        }
    };

    public void stopAudioRecord() {
        try {
            if (isRecording()) {
                mediaRecorder.stop();
                mediaRecorder.release();
                handler.removeCallbacks(updateVisualizer);
                handler.removeCallbacks(questionTextRunnable);
                visualizerView.setVisibility(View.GONE);
                isRecording = false;
            }
        } catch (Exception e) {

        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.doneButton) {
            String voiceTestAlertMessage = ApplicationSettings.getPref("UearnVoiceTestAlertMessage", "");
            if (!voiceTestAlertMessage.equalsIgnoreCase("")) {
                showVoiceTestAlertMessageDialog(StartUearnVoiceRecord.this, "Alert", voiceTestAlertMessage).show();
            } else {
                voiceuploaderror.setVisibility(View.GONE);
                startOverButton.setTextColor(getResources().getColor(R.color.uearn_red_color));
                doneButton.setTextColor(getResources().getColor(R.color.uearn_red_color));
                doneButton.setEnabled(false);
                doneButton.setClickable(false);
                doneButton.setBackground(this.getResources().getDrawable(R.drawable.edit_text_shape));
                if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                    if (!voiceInfo.isPara1) {
                        if (!voiceInfo.para2.isEmpty()) {
                            Intent intent1 = new Intent(this, StartUearnVoiceRecord.class);
                            voiceInfo.isPara1 = true;
                            sendAudioDatatoServer(recordFilePath, 1);
                        } else {
                            Intent intent1 = new Intent(this, VoiceTestCompleteActivity.class);
                            intent1.putExtra("VoiceTestCompleted", true);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            sendAudioDatatoServer(recordFilePath, 3);
                        }
                    } else if (voiceInfo.isPara1 && !voiceInfo.isPara2) {
                        if (!voiceInfo.para3.isEmpty()) {
                            Intent intent1 = new Intent(this, StartUearnVoiceRecord.class);
                            voiceInfo.isPara2 = true;
                            sendAudioDatatoServer(recordFilePath, 2);
                        } else {
                            Intent intent1 = new Intent(this, VoiceTestCompleteActivity.class);
                            intent1.putExtra("VoiceTestCompleted", true);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            sendAudioDatatoServer(recordFilePath, 3);
                        }
                    } else {
                        Intent intent1 = new Intent(this, VoiceTestCompleteActivity.class);
                        intent1.putExtra("VoiceTestCompleted", true);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        sendAudioDatatoServer(recordFilePath, 3);
                    }

                } else {
                    voiceuploaderror.setVisibility(View.VISIBLE);
                    voiceuploaderror.setText("No internet connection");
                    voiceuploaderror.setTextColor(getResources().getColor(R.color.uearn_red_color));
                    doneButton.setTextColor(Color.WHITE);
                    doneButton.setEnabled(true);
                    doneButton.setClickable(true);
                    doneButton.setBackground(this.getResources().getDrawable(R.drawable.red_rounded_corner));
                }
            }
        } else if (id == R.id.startOverButton) {
            voiceuploaderror.setVisibility(View.GONE);
            hints1Tv.setVisibility(View.VISIBLE);
            startOverButton.setTextColor(getResources().getColor(R.color.uearn_red_color));
            startOverButton.setBackground(this.getResources().getDrawable(R.drawable.edit_text_shape));
            doneButton.setTextColor(Color.WHITE);
            doneButton.setEnabled(false);
            doneButton.setClickable(false);
            doneButton.setBackground(this.getResources().getDrawable(R.drawable.grey_rounded_corner));
            startOverButton.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.VISIBLE);
            stopAudioRecord();
            Intent i = getIntent();
            voiceInfo = (UearnVoiceTestInfo) i.getSerializableExtra("VoiceInfo");
            questionTv.setMovementMethod(new ScrollingMovementMethod());
            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/sf-pro-display-regular.ttf");
            Typeface titleFace = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Display-Semibold.ttf");
            hints1Tv.setTypeface(titleFace);
            hints2Tv.setTypeface(face);
            String question1 = voiceInfo.para1;
            questionLength = question1.length();
            numberOfTime = questionLength / 5;
            startAudioRecord();
            visualizerView.setVisibility(View.VISIBLE);
            audioPlayer.setVisibility(View.GONE);
            audioPlayer.setEnabled(false);
        } else if (id == R.id.micButton) {
            timer.setVisibility(View.VISIBLE);
            starttv.setVisibility(View.GONE);
            t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            timer.setText("00:" + "0" + String.valueOf(seconds));
                            seconds -= 1;
                            if (seconds == -1) {
                                if (t != null) {
                                    t.cancel();
                                    t = null;
                                    onClickMic();
                                }
                            }
                        }
                    });
                }
            }, 0, 1000);
        } else if (id == R.id.profile_image_back) {
            onBackPressed();
        } else if (id == R.id.ll_notification) {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            settingsApi(userId);
        }
    }

    public void sendDataToServer() {
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            final JSONObject voiceTestObj = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    array.put(voiceInfo.para1Url);
                } else if (i == 1) {
                    array.put(voiceInfo.para2Url);
                } else if (i == 2) {
                    array.put(voiceInfo.para3Url);
                }
            }

            try {
                voiceTestObj.put("user_id", user_id);
                voiceTestObj.put("lang", voiceInfo.name);
                voiceTestObj.put("url", array);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            new AsyncTask<Void, Void, JSONObject>() {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setMessage("Please wait");
                            progressBar.show();
                        }
                    });
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    JSONObject jsonObject = null;
                    String url = Urls.getUploadVoiceTestUrl();
                    JSONObject rsponseObj = DataUploadUtils.postJsonData(url, voiceTestObj);
                    try {
                        if ((rsponseObj != null) && (rsponseObj.getString("success") != null)) {
                            String response = rsponseObj.getString("success");
                            if (response.equals("Voice url updated")) {
                                Intent intent1 = new Intent(getApplicationContext(), VoiceTestCompleteActivity.class);
                                intent1.putExtra("VoiceTestCompleted", true);
                                ApplicationSettings.putPref(AppConstants.USER_STATUS, "Voice Test");
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent1);
                            }
                        }

                    } catch (JSONException e) {

                    }
                    return jsonObject;
                }

                @Override
                protected void onPostExecute(JSONObject jObject) {
                    super.onPostExecute(jObject);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    });
                }
            }.execute();
        } else {

            ApplicationSettings.putPref(AppConstants.FROM_CMAIL, false);
            ApplicationSettings.putPref(AppConstants.DRAFT_LIVEMEETING, false);
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void sendAudioDatatoServer(final String audioPath, final int paraNo) {
        final String[] audioUrlPath = {""};

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            new AsyncTask<Void, Void, String>() {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setMessage("Please wait");
                            progressBar.setCancelable(false);
                            progressBar.setCanceledOnTouchOutside(false);
                            progressBar.show();
                        }
                    });
                }

                @Override
                protected String doInBackground(Void... params) {
                    String url = Urls.getUploadFileUrl();
                    JSONObject responseAudio = DataUploadUtils.uploadNotesAudioFileToServer(audioPath, "live_meeting_rec", "", "", url);
                    try {
                        if ((responseAudio != null) && (responseAudio.getString("url") != null)) {
                            String path = responseAudio.getString("url");
                            audioUrlPath[0] = path;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    voiceuploaderror.setVisibility(View.VISIBLE);
                                    voiceuploaderror.setText("Upload failed");
                                    voiceuploaderror.setTextColor(getResources().getColor(R.color.uearn_red_color));
                                }
                            });
                        }

                    } catch (JSONException e) {

                    }
                    String path = audioUrlPath[0];
                    Intent intent1 = new Intent(getApplicationContext(), StartUearnVoiceRecord.class);
                    if (paraNo == 1) {
                        voiceInfo.para1Url = path;
                        intent1.putExtra("VoiceInfo", voiceInfo);
                        startActivity(intent1);
                    } else if (paraNo == 2) {
                        voiceInfo.para2Url = path;
                        intent1.putExtra("VoiceInfo", voiceInfo);
                        startActivity(intent1);
                    } else if (paraNo == 3) {
                        voiceInfo.para3Url = path;
                        sendDataToServer();
                    }
                    ApplicationSettings.putPref("PARA1", path);
                    return path;
                }

                @Override
                protected void onPostExecute(String jObject) {
                    super.onPostExecute(jObject);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.dismiss();
                        }
                    });
                }
            }.execute();
        } else {
            voiceuploaderror.setVisibility(View.VISIBLE);
            voiceuploaderror.setText("No internet connection");
            voiceuploaderror.setTextColor(getResources().getColor(R.color.uearn_red_color));
            doneButton.setTextColor(Color.WHITE);
            doneButton.setEnabled(true);
            doneButton.setClickable(true);
            doneButton.setBackground(this.getResources().getDrawable(R.drawable.red_rounded_corner));
        }
    }

    private void onClickMic() {
        timer.setVisibility(View.GONE);
        micButton.setVisibility(View.GONE);
        readytv.setVisibility(View.GONE);
        placemictv.setVisibility(View.INVISIBLE);
        starttv.setVisibility(View.GONE);
        visualizerView.setVisibility(View.VISIBLE);
        hints1Tv.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.VISIBLE);
        stopAudioRecord();
        Intent i = getIntent();
        voiceInfo = (UearnVoiceTestInfo) i.getSerializableExtra("VoiceInfo");
        questionTv.setMovementMethod(new ScrollingMovementMethod());
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/sf-pro-display-regular.ttf");
        Typeface titleFace = Typeface.createFromAsset(getAssets(), "fonts/SF-UI-Display-Semibold.ttf");
        hints1Tv.setTypeface(titleFace);
        hints2Tv.setTypeface(face);
        String question1 = voiceInfo.para1;
        questionLength = question1.length();
        numberOfTime = questionLength / 5;
        startAudioRecord();
    }

    private void createDirectory(String dirName) {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(audioPlayer != null) {
            audioPlayer.stopMediaPlayer();
        }
    }

    private void settingsApi(final String userId) {
        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.GetSettings(userId, 22, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {

                }
            }).call();
        }
    }

    public Dialog showVoiceTestAlertMessageDialog(final Activity activity, String title, String message) {

        final Dialog exitDialog = buildTwoButtonDialog(activity, title, message);

        TextView btnNo = exitDialog.findViewById(R.id.btn_no);
        btnNo.setText("NO");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
            }
        });

        TextView btnYes = exitDialog.findViewById(R.id.btn_yes);
        btnYes.setText("YES");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
                voiceuploaderror.setVisibility(View.GONE);
                startOverButton.setTextColor(getResources().getColor(R.color.uearn_red_color));
                doneButton.setTextColor(getResources().getColor(R.color.uearn_red_color));
                doneButton.setEnabled(false);
                doneButton.setClickable(false);
                doneButton.setBackground(StartUearnVoiceRecord.this.getResources().getDrawable(R.drawable.edit_text_shape));
                if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                    if (!voiceInfo.isPara1) {
                        if (!voiceInfo.para2.isEmpty()) {
                            Intent intent1 = new Intent(StartUearnVoiceRecord.this, StartUearnVoiceRecord.class);
                            voiceInfo.isPara1 = true;
                            sendAudioDatatoServer(recordFilePath, 1);
                        } else {
                            Intent intent1 = new Intent(StartUearnVoiceRecord.this, VoiceTestCompleteActivity.class);
                            intent1.putExtra("VoiceTestCompleted", true);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            sendAudioDatatoServer(recordFilePath, 3);
                        }


                    } else if (voiceInfo.isPara1 && !voiceInfo.isPara2) {
                        if (!voiceInfo.para3.isEmpty()) {
                            Intent intent1 = new Intent(StartUearnVoiceRecord.this, StartUearnVoiceRecord.class);
                            voiceInfo.isPara2 = true;
                            sendAudioDatatoServer(recordFilePath, 2);
                        } else {
                            Intent intent1 = new Intent(StartUearnVoiceRecord.this, VoiceTestCompleteActivity.class);
                            intent1.putExtra("VoiceTestCompleted", true);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            sendAudioDatatoServer(recordFilePath, 3);
                        }
                    } else {
                        Intent intent1 = new Intent(StartUearnVoiceRecord.this, VoiceTestCompleteActivity.class);
                        intent1.putExtra("VoiceTestCompleted", true);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        sendAudioDatatoServer(recordFilePath, 3);
                    }

                } else {
                    voiceuploaderror.setVisibility(View.VISIBLE);
                    voiceuploaderror.setText("No internet connection");
                    voiceuploaderror.setTextColor(getResources().getColor(R.color.uearn_red_color));
                    doneButton.setTextColor(Color.WHITE);
                    doneButton.setEnabled(true);
                    doneButton.setClickable(true);
                    doneButton.setBackground(StartUearnVoiceRecord.this.getResources().getDrawable(R.drawable.red_rounded_corner));
                }
            }
        });
        exitDialog.setCancelable(false);
        exitDialog.setCanceledOnTouchOutside(false);
        return exitDialog;
    }

    public static Dialog buildTwoButtonDialog(Activity activity, String title, String message) {
        final Dialog twoButtonDialog = new Dialog(activity);
        twoButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        twoButtonDialog.setContentView(R.layout.dialog_common_two_button);

        twoButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = twoButtonDialog.findViewById(R.id.tvDialogTitle);
        tvTitle.setText(title);

        TextView tvMessage = twoButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        return twoButtonDialog;
    }
}
