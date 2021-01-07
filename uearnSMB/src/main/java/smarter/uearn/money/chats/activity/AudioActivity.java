package smarter.uearn.money.chats.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.views.SmartAudioPlayer;

public class AudioActivity extends BaseActivity {
    private SmartAudioPlayer audioPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        Intent intent = getIntent();
        String audioURL = intent.getExtras().getString("AUDIO_URL");
        audioPlayer = findViewById(R.id.player);
        Log.e("AudioActivity","audioURL ::: "+audioURL);
        Uri fileUrl = Uri.parse(audioURL);
        Log.e("AudioActivity","fileUrl ::: "+fileUrl);
        audioPlayer.setDataSource(fileUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioPlayer.finish();
    }
}
