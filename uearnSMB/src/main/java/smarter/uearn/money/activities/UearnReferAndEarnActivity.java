package smarter.uearn.money.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class UearnReferAndEarnActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView whatsappLayout, facebookLayout, messengerLayout, moreLayout;
    private TextView referalCodeTv,tvAmount, referralProgram;
    private String userId;
    private TextView tvPageLabel;
    private ImageView ivNavMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_earn);
        userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    private void shareByMore(String referalCode) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        String shareString = "Work from home for a software BPO / Call centre firm. \nDownload App: https://play.google.com/store/apps/details?id=smarter.uearn.money \n Referral code:"+"UEARN"+userId;
        share.putExtra(Intent.EXTRA_TEXT, shareString);
        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void getWhatsup(String referalCode) {
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            String shareString = "Work from home for a software BPO / Call centre firm. \nDownload App: https://play.google.com/store/apps/details?id=smarter.uearn.money \n Referral code:"+"UEARN"+userId;
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareString);
            sendIntent.putExtra("jid",  "@s.whatsapp.net"); //phone number without "+" prefix
            sendIntent.setPackage("com.whatsapp");
            boolean installed  =   appInstalledOrNot("com.whatsapp");
            if(installed) {
                startActivity(sendIntent);
            } else {
                Toast.makeText(this, "You do not have WhatsApp App please install and share.", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e) {
            Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void shareByMessanger(String referCode) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String shareString = "Work from home for a software BPO / Call centre firm. \nDownload App: https://play.google.com/store/apps/details?id=smarter.uearn.money \n Referral code:"+"UEARN"+userId;
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.facebook.orca");
        boolean installed  =   appInstalledOrNot("com.facebook.orca");
        if(installed) {
            startActivity(sendIntent);
        } else {
            Toast.makeText(this, "You do not have Messenger App please install and share.", Toast.LENGTH_SHORT).show();

        }

    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }


    public void shareByFacebook(String referCode) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
         intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no effect!
        String shareString = "Work from home for a software BPO / Call centre firm. \nDownload App: https://play.google.com/store/apps/details?id=smarter.uearn.money \n Referral code:"+"UEARN"+userId;
        intent.putExtra(Intent.EXTRA_TEXT, shareString);
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + shareString;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }
        startActivity(intent);
    }

    public void restoreActionBar(String mTitle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7030A0")));
    }


    private void initUI() {
        ivNavMenu= findViewById(R.id.ivNavMenu);
        ivNavMenu.setOnClickListener(this);
        tvPageLabel= findViewById(R.id.tvPageLabel);
        tvPageLabel.setText("Invite And Earn");
        whatsappLayout = findViewById(R.id.tvWtsApp);
        whatsappLayout.setOnClickListener(this);
        facebookLayout = findViewById(R.id.tvFaceBook);
        facebookLayout.setOnClickListener(this);
        messengerLayout = findViewById(R.id.tvMessanger);
        messengerLayout.setOnClickListener(this);
        moreLayout = findViewById(R.id.tvMore);
        moreLayout.setOnClickListener(this);

        referalCodeTv = findViewById(R.id.referalCode);
        referalCodeTv.setText(" UEARN"+userId);
        tvAmount = findViewById(R.id.tvAmount);
        referralProgram = findViewById(R.id.referralProgram);
        referralProgram.setOnClickListener(this);
    }


    public void changeStatusBarColor(AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ivNavMenu) {
            onBackPressed();
        } else if (id == R.id.tvWtsApp) {
            getWhatsup("");
        } else if (id == R.id.tvFaceBook) {
            shareByFacebook("");
        } else if (id == R.id.tvMessanger) {
            shareByMessanger("");
        } else if (id == R.id.tvMore) {
            shareByMore("");
        } else if (id == R.id.referralProgram) {
            Intent referIntent = new Intent(this, UearnReferralProgram.class);
            startActivity(referIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }
}
