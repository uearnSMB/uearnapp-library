package smarter.uearn.money.utils.socialmedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


// Code Refactored by Navine to migrate to latest Google Authentication
// Issue in resolving fixed in the legacy
//
//https://github.com/googlesamples/google-services/blob/master/android/signin/app/src/main/java/com/google/samples/quickstart/signin/ServerAuthCodeActivity.java


public class GooglePlusSignIn implements GoogleApiClient.OnConnectionFailedListener {

    //orig PRODUCTION - 320336915610-60f9tt45g25gbr81lpiugo5adj1kfebv.apps.googleusercontent.com
    private static final String SERVER_CLIENT_ID = "320336915610-60f9tt45g25gbr81lpiugo5adj1kfebv.apps.googleusercontent.com";
    //For DEVELOPMENT
   // private static final String SERVER_CLIENT_ID = "396887138242-aku135932lkpcjbmh8pj1m8k5c8ersqs.apps.googleusercontent.com";


    // Base URL for your token exchange server, no trailing slash.
    private static final String SERVER_BASE_URL = Urls.SERVER_ADDRESS + "/google";


    // URL where the client should POST the serverAuthCode so that the server can exchange
    // it for a refresh token.
    private static final String EXCHANGE_TOKEN_URL = SERVER_BASE_URL + "/exchangetoken";
    private static final String TAG = "GooglePlus";
    private static final int RC_GET_AUTH_CODE = 9003;
    // GoogleApiClient wraps our service connection to Google Play services and
    // provides access to the users sign in state and Google's APIs.
    private GoogleApiClient mGoogleApiClient = null;


    private Context context;
    private GoogleAutenticationListener mAuthListener;

    public static Uri imageUri;

    public interface GoogleAutenticationListener {
        void onComplete(boolean success, String displayName, String email, String failureMsg);
    }


    public GooglePlusSignIn(Context context) {
        //Log.i(TAG, "In Google plus SIgn iN");
        this.context = context;

    }

    public static synchronized GooglePlusSignIn getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new GooglePlusSignIn(context);
        }
        return INSTANCE;
    }

    private static synchronized void resetInstance() {
        INSTANCE = null;
    }

    private static GooglePlusSignIn INSTANCE;


    private GoogleApiClient buildGoogleApiClientForAuthentication() {
        // Build a GoogleApiClient with access to basic profile information.  We also request
        // the Plus API so we have access to the Plus.AccountApi functions, but note that we are
        // not actually requesting any Plus Scopes so we will not ask for or get access to the
        // user's Google+ Profile.


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(SERVER_CLIENT_ID)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                //  .enableAutoManage(this.mFragmentActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return mGoogleApiClient;
    }

    private GoogleApiClient buildGoogleApiClientForGmail() {
        // Build a GoogleApiClient with access to basic profile information.  We also request
        // the Plus API so we have access to the Plus.AccountApi functions, but note that we are
        // not actually requesting any Plus Scopes so we will not ask for or get access to the
        // user's Google+ Profile.

        Scope gmailRead = new Scope("");
        Scope gmailSend = new Scope("");
        Scope gmailCalendar = new Scope("");
        Scope gmailCalendarRead = new Scope("");

//        Scope gmailRead = new Scope("https://www.googleapis.com/auth/gmail.readonly");
//        Scope gmailSend = new Scope("https://www.googleapis.com/auth/gmail.send");
//        Scope gmailCalendar = new Scope("https://www.googleapis.com/auth/calendar");
//        Scope gmailCalendarRead = new Scope("https://www.googleapis.com/auth/calendar.readonly");


        // [START configure_signin]
        // Configure sign-in to request offline access to the user's ID, basic
        // profile, and Google Drive. The first time you request a code you will
        // be able to exchange it for an access token and refresh token, which
        // you should store. In subsequent calls, the code will only result in
        // an access token. By asking for profile access (through
        // DEFAULT_SIGN_IN) you will also get an ID Token as a result of the
        // code exchange.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //   .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestScopes(gmailRead)
                .requestScopes(gmailSend)
                .requestScopes(gmailCalendar)
                .requestScopes(gmailCalendarRead)
                .requestServerAuthCode(SERVER_CLIENT_ID)
                .requestProfile()
                .requestEmail()
                .build();
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                //  .enableAutoManage(this.mFragmentActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        return mGoogleApiClient;
    }


    public void authenticate(GoogleAutenticationListener listener) {
       // Log.i(TAG, "Inside Authenticate ..");
        mAuthListener = listener;
        mGoogleApiClient = buildGoogleApiClientForAuthentication();
        //Log.i(TAG, "calling mGoogleApiClient.connect");

        getAuthCode();
    }

    public void gmailAuthorize(GoogleAutenticationListener listener) {
        //Log.i(TAG, "Inside gmailAuthenticate ..");

        mAuthListener = listener;
        mGoogleApiClient = buildGoogleApiClientForGmail();

        //Log.i(TAG, "calling getAuthCode");

        //mGoogleApiClient.connect();

        getAuthCode();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
        if(mAuthListener != null) {
            mAuthListener.onComplete(false, null, null, connectionResult.getErrorMessage());
        }
    }


    /**
     * Gets authentication context mGoogleAPIClient for Google Sign in
     *
     * @return an authentication context, if successful.
     */
    public GoogleApiClient getAuthenticationContext() {
        if (mGoogleApiClient == null) {
            try {
                mGoogleApiClient = buildGoogleApiClientForGmail();
            } catch (Throwable t) {
                //Log.e(TAG, t.toString());
                return null;
            }
        }
        return mGoogleApiClient;
    }


    public void disconnect() {
        mGoogleApiClient.disconnect();
    }


    private void getAuthCode() {
        // Start the retrieval process for a server auth code.  If requested, ask for a refresh
        // token.  Otherwise, only get an access token if a refresh token has been previously
        // retrieved.  Getting a new access token for an existing grant does not require
        // user consent.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        //startActivityForResult(signInIntent, RC_GET_AUTH_CODE);
        startActivityForResult((Activity) context, signInIntent, RC_GET_AUTH_CODE, null);
    }


    //This method is called from the parent activity's callback
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_GET_AUTH_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            Log.d(TAG, "onActivityResult:GET_AUTH_CODE:success:" + result.getStatus().isSuccess());

            if (result.isSuccess()) {

                // [START get_auth_code]
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct != null) {
                    final String authCode = acct.getServerAuthCode();
                    final String personName = acct.getDisplayName();
                    final String email = acct.getEmail();
                    final Uri image = acct.getPhotoUrl();
                    //Log.d(TAG, "Person Name :" + personName);
                    //Log.d(TAG, "Email id :" + email);
                    //Log.d(TAG, "ServerAuthCode :" + authCode);
                    //Log.i("GoogleSignIn","Photo uri is "+image);
                    imageUri = acct.getPhotoUrl();

                    // TODO(user): send code to server and exchange for access/refresh/ID tokens.
                    // String exToken = exchangeTokenWithServer(authCode);

                    new APIProvider.ExchangeTokenCall(authCode, 1, new API_Response_Listener<String>() {
                        @Override
                        public void onComplete(String data, long request_code, int failure_code) {
                           // Log.d(TAG, "Exchange Token : is executed");

                            //ToDO (Navine) : Need to get the email from IDToken and verify (-20%)
                            if(personName != null && email != null) {
                                if (!personName.isEmpty() && !email.isEmpty()) {
                                   // Log.d(TAG, "call onComplete with personname and email");
                                    mAuthListener.onComplete(true, personName, email, null);
                                } else {
                                    // Show signed-out UI.
                                    mAuthListener.onComplete(false, null, null, "Cannot fetch email");
                                }
                            }


                        }
                    }).call();


                }

                // [END get_auth_code]
            } else {
                // Show signed-out UI.
                mAuthListener.onComplete(false, null, null, "Cannot fetch email");
            }
        } else {
            // Show signed-out UI.
            mAuthListener.onComplete(false, null, null, "Cannot fetch email");
        }

    }


    // [START on_upload_server_auth_code]
    public String exchangeTokenWithServer(String serverAuthCode) {
        // Upload the serverAuthCode to the server, which will attempt to exchange it for
        // a refresh token.  This callback occurs on a background thread, so it is OK
        // to perform synchronous network access.  Returning 'false' will fail the
        // GoogleApiClient.connect() call so if you would like the client to ignore
        // server failures, always return true.


        try {

            URL url = new URL(EXCHANGE_TOKEN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");


            //conn.setRequestProperty("idToken", idToken);
            //conn.setRequestProperty("serverAuthCode", serverAuthCode);

            JSONObject jsonObject = new JSONObject();
            try {
                //jsonObject.put("idToken", idToken);
                jsonObject.put("serverAuthCode", serverAuthCode);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type", "application/json");


            String jsonParam = jsonObject.toString();

            //send request
            byte[] outputInBytes = jsonParam.getBytes("UTF-8");
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(outputInBytes);
            os.flush();
            os.close();


            // Log.i("Tag", "idToken " + idToken + " server Auth code " + serverAuthCode);

            InputStream inputStream = null;
            try {
                int statusCode = conn.getResponseCode();

                if (statusCode != HttpURLConnection.HTTP_OK) {
                    //Log.e(TAG, "http response code is " + conn.getResponseCode());
                    //Log.i("Reupload-Service", "http Error Stream  is " + conn.getErrorStream().toString());
                }
                //Get Response


                inputStream = new BufferedInputStream(conn.getInputStream());
                String result = Utils.convertStreamToString(inputStream);
                //Log.i(TAG, "Code: " + statusCode);
                //Log.i(TAG, "Resp: " + result);


                //This method is always returning true - invariable of what is there in the body
                //Authentication code is actually not exchanged
                return result;

            } finally {
                // .e("ServerAuth Handler", " onUploadServerAuthCode : Inside Finally ");
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    //Log.e("ServerAuth Handler", "onUploadServerAuthCode : Closing Connection:  IO Exception thrown : " + e);
                    e.printStackTrace();
                }

                if (conn != null) {
                    //Log.e("Reupload-Service", "UploadImageFileToServer : Inside Finally - calling disconnect ");
                    conn.disconnect();
                }

            }
        } catch (IOException e) {
            //Log.e(TAG, "Error in auth code exchange. returning null ", e);
            return null;
        }
    }
    // [END on_upload_server_auth_code]
}














