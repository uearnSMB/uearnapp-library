package smarter.uearn.money.utils.socialmedia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class GooglePlusSignInOrig implements ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    public static final int GOOGLEPLUS_SIGN_IN = 0;
    private Context context;
    private boolean signedInUser;
    //public static int Registration=1;
    //public static int Login=2;
    private int action;
    private boolean canResolve;
    private ProgressDialog waitDialog;

    public GooglePlusSignInOrig(Context context) {
        this.context = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).
                addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
        //googlePlusLogin();
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void callConnect() {
        //Log.i("Tag","In google plus signUp");
        signedInUser = true;
        canResolve = true;
        showWaitDialog();
        // Log.i("Tag","In Connect..");
        mGoogleApiClient.connect();
    }

    public void callDisconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void showWaitDialog() {
        if (context != null) {
            waitDialog = new ProgressDialog(context);
            waitDialog.setMessage("Connecting Google Server...");
            waitDialog.setCancelable(true);
            waitDialog.setOwnerActivity((Activity) context);
            waitDialog.show();
        }
    }

    public void dismissWaitDialog() {
        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

	/*private void googlePlusLogin() {
		  if (!mGoogleApiClient.isConnecting()) {
			  	
		           signedInUser = true;
		           resolveSignInError();
		  }else{
			  mGoogleApiClient.connect();
		  }
		}*/

    @Override
    public void onConnected(Bundle arg0) {

        signedInUser = false;
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (!result.hasResolution()) {
            if (!((Activity) context).isFinishing()) {
                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), (Activity) context, 0).show();
            }
        }

        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = result;

            if (signedInUser) {
                resolveSignInError();
            }
        }
    }

    private void resolveSignInError() {

        if (canResolve) {
            canResolve = false;
            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult((Activity) context, GOOGLEPLUS_SIGN_IN);
                } catch (SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        } else {
            Toast.makeText(context, "Error occured, Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    private void getProfileInformation() {
        try {
            //Log.i("Tag", "get user info");
            dismissWaitDialog();
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                //String personPhotoUrl = currentPerson.getImage().getUrl();
                //String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                // Log.i("Tag", personName+" "+email);
                if (action == SocialMediaManager.REGISTRATION) {
                    //SocialMediaRegistrationActivity registration= (SocialMediaRegistrationActivity)context;
                    // registration.socialMediaRegistration(personName, email, "gplus");
                } else if (action == SocialMediaManager.LOGIN) {
                    //Log.i("Tag", "call login");
		                	 /*LoginActivity login=(LoginActivity)context;
								login.socialMediaLogin(email, "gplus");*/
                } else if (action == SocialMediaManager.SPLASH_REGISTRATION) {
                             /*SplashScreen splashScreen = (SplashScreen)context;
                             splashScreen.socialMediaRegistration(personName, email, "gplus");*/
                } else if (action == SocialMediaManager.LOGIN_REGISTRATION) {
                             /*LoginActivity loginScreen = (LoginActivity)context;
                             //loginScreen.socialMediaRegistration(personName, email, "gplus");*/
                }
                //username.setText(personName);
                //emailLabel.setText(email);
                // new LoadProfileImage(image).execute(personPhotoUrl);
                // update profile frame with new info about Google Account
                // profile
                // updateProfile(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onactivityresult(boolean resultStatus) {
        dismissWaitDialog();
        //Log.i("Tag","In On activity Result" +mGoogleApiClient.isConnecting());
        if (resultStatus == true) {
            signedInUser = false;
        }
        mIntentInProgress = false;
        if (!mGoogleApiClient.isConnecting()) {
            showWaitDialog();
            mGoogleApiClient.connect();
        }
    }

}
