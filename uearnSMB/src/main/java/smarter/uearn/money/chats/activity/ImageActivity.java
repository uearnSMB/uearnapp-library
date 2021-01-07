package smarter.uearn.money.chats.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class ImageActivity extends BaseActivity {
    private ImageView image_fullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);
        PhotoView image_fullscreen = (PhotoView) findViewById(R.id.image_fullscreen);
        Intent intent = getIntent();
        String imageURL = intent.getExtras().getString("IMAGE_URL");
        Picasso.get().load(imageURL).into(image_fullscreen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }
}
