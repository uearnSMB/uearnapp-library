package smarter.uearn.money.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.analytics.FirebaseAnalytics;

import smarter.uearn.money.R;
import smarter.uearn.money.dialogs.CustomSingleButtonDialog;
import smarter.uearn.money.fragments.SignInFragment;
import smarter.uearn.money.fragments.SignUpFragment;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class SmarterAuthActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        changeStatusBarColor(this);
        mActivity = this;
        SmarterSMBApplication.currentActivity = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        navigateToSignInSignUp();
    }

    public void changeStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_2);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Authentication");
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, AnalyticsBundle);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (SmarterSMBApplication.permissionDialog != null) {
            SmarterSMBApplication.permissionDialog.cancel();
            SmarterSMBApplication.permissionDialog.dismiss();
            SmarterSMBApplication.permissionDialog = null;
        }
    }

    private void navigateToSignInSignUp() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = getIntent().getExtras();
        int screen = bundle.getInt("screenType");
        if (screen == 0) {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }
            fragmentTransaction.add(R.id.authentication_container, new SignInFragment());
            fragmentTransaction.addToBackStack(null);
            if (bundle.containsKey("Error_message")) {
                String msg = bundle.getString("Error_message");
                CustomSingleButtonDialog.buildSingleButtonDialog("Error", msg, this, false);
            }
        } else {
            fragmentTransaction.add(R.id.authentication_container, new SignUpFragment());
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
