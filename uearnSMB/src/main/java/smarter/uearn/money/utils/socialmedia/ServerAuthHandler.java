/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package smarter.uearn.money.utils.socialmedia;

// Code Refactored by Navine to improve the performance

/**
 * Logic to handle the ServerAuthCode exchange for offline access during sign-in.
 */
//public class ServerAuthHandler implements GoogleApiClient.ServerAuthCodeCallbacks {

    public class ServerAuthHandler{

     /*   private static final String TAG = ServerAuthHandler.class.getSimpleName();

    // Base URL for your token exchange server, no trailing slash.
    private static final String SERVER_BASE_URL = Urls.SERVER_ADDRESS + "/google";

    // URL where the client should GET the scopes that the server would like granted
    // before asking for a serverAuthCode.
    private static final String SELECT_SCOPES_URL = SERVER_BASE_URL + "/selectscopes";

    private static final String SERVER_HAS_TOKEN_URL = SERVER_BASE_URL + "/hasTokens";

    // URL where the client should POST the serverAuthCode so that the server can exchange
    // it for a refresh token.
    private static final String EXCHANGE_TOKEN_URL = SERVER_BASE_URL + "/exchangetoken";

    // Used to mock the state of a server that would receive an auth code to exchange
    // for a refresh token,  If true, the client will assume that the server has the
    // permissions it wants and will not send an auth code on sign in.  If false,
    // the client will request offline access on sign in and send and new auth code
    // to the server.  True by default because this sample does not implement a server
    // so there would be nowhere to send the code.
    private boolean mServerHasToken = false;
//    private int action;

    private Activity mActivity;

    public static String serverAuthCodeString, idTokenString;

    public ServerAuthHandler(Activity activity) {
        mActivity = activity;
    }

    // [START on_check_server_authorization]
    @Override
    public CheckResult onCheckServerAuthorization(String idToken, Set<Scope> scopeSet) {
        //Log.i(TAG, "Checking if server is authorized.");

        // Check if the server has a token.  Since this callback executes in a background
        // thread it is OK to do synchronous network access in this check.
        // boolean serverHasToken = false;


        boolean serverHasToken = serverHasTokenFor(idToken);
        //Log.i(TAG, "Function serverHasToken returned : " + serverHasToken);


        if (!serverHasToken) {
            //Log.i(TAG, "Server Does not have a valid token - prompt user for consent dialog");

            // Server does not have a valid refresh token, so request a new
            // auth code which can be exchanged for one.  This will cause the user to see the
            // consent dialog and be prompted to grant offline access.

            // Ask the server which scopes it would like to have for offline access.  This
            // can be distinct from the scopes granted to the client.  By getting these values
            // from the server, you can change your server's permissions without needing to
            // recompile the client application.

            //  Scope added directly from client - To expedite login time
            //  Server code to get the SCOPES_URL is removed.

            HashSet<Scope> serverScopeSet = new HashSet<Scope>();
            serverScopeSet.add(new Scope("https://www.googleapis.com/auth/plus.me"));
            serverScopeSet.add(new Scope("https://www.googleapis.com/auth/gmail.readonly"));
            serverScopeSet.add(new Scope("https://www.googleapis.com/auth/gmail.send"));
            serverScopeSet.add(new Scope("https://www.googleapis.com/auth/calendar"));
            serverScopeSet.add(new Scope("https://www.googleapis.com/auth/calendar.readonly"));


            // This tells GoogleApiClient that the server needs a new serverAuthCode with
            // access to the scopes in serverScopeSet.  Note that we are not asking the server
            // if it already has such a token because this is a sample application.  In reality,
            // you should only do this on the first user sign-in or if the server loses or deletes
            // the refresh token.
            //Log.i(TAG + "::onCheckServerAuth", "calling newAuthRequiredResult in ServerAuthHandler with serverScopeSet " + serverScopeSet.toString());

            return CheckResult.newAuthRequiredResult(serverScopeSet);
        } else {
            // Server already has a valid refresh token with the correct scopes, no need to
            // ask the user for offline access again.
            //Log.i(TAG + "::onCheckServerAuth", "calling newAuthRequiredResult without new Authentication");

            return CheckResult.newAuthNotRequiredResult();
        }
    }
    // [END on_check_server_authorization]

    // [START on_upload_server_auth_code]
    @Override
    public boolean onUploadServerAuthCode(String idToken, String serverAuthCode) {
        // Upload the serverAuthCode to the server, which will attempt to exchange it for
        // a refresh token.  This callback occurs on a background thread, so it is OK
        // to perform synchronous network access.  Returning 'false' will fail the
        // GoogleApiClient.connect() call so if you would like the client to ignore
        // server failures, always return true.
        serverAuthCodeString = serverAuthCode;
        idTokenString = idToken;

        Log.i("GooglePlus", "Server Auth Code "+serverAuthCodeString);
        Log.i("GooglePlus","Id token is "+idTokenString);
        return true;
    }
    // [END on_upload_server_auth_code]


    private boolean serverHasTokenFor(String idToken) {
        // TODO: This method is a stub, in a real application we would make an HTTP
        // request to the server here.

        //Log.i("TAG", "serverHasTokenFor called  with idToken : " + idToken);


        try {


            URL url = new URL(SERVER_HAS_TOKEN_URL + "?token='" + idToken + "'");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("GET");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");


            conn.setDoInput(true);
            conn.setDoOutput(false);
            InputStream inputStream = null;
            try {

                int responseCode = conn.getResponseCode();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "http response code is " + conn.getResponseCode());
                    //Log.i("Reupload-Service", "http Error Stream  is " + conn.getErrorStream().toString());
                }


                // Convert the response to set of Scope objects.
                if (responseCode == 200) {
                    //Get Response

                    inputStream = new BufferedInputStream(conn.getInputStream());
                    String responseBody = Utils.convertStreamToString(inputStream);
                    //  Log.i(TAG, "serverHasToken url :" + url.toString());
                    //Log.i(TAG, "serverHasToken response (Body) :" + responseBody);
                    if (responseBody.equals("true")) {
                        return true;
                    }
                } else {
                    Log.e(TAG, "Error in getting server token - resonseCode : " + responseCode);
                }

            } finally {
                //Log.e("ServerAuth Handler", " serverHasTokenFor : Inside Finally ");
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    Log.e("Reupload-Service", "UploadImageFileToServer : Closing Connection:  IO Exception thrown : " + e);
                    e.printStackTrace();
                }

                if (conn != null) {
                    //Log.e("Reupload-Service", "UploadImageFileToServer : Inside Finally - calling disconnect ");
                    conn.disconnect();
                }

            }

        } catch (IOException e) {
            Log.e(TAG, "Error in getting server scopes.", e);
        }

        // This tells GoogleApiClient that the server needs a new serverAuthCode with
        // access to the scopes in serverScopeSet.  Note that we are not asking the server
        // if it already has such a token because this is a sample application.  In reality,
        // you should only do this on the first user sign-in or if the server loses or deletes
        // the refresh token.
//        return CheckResult.newAuthRequiredResult(serverScopeSet);


        //Log.i("TAG", "ServerAuthHandler:serverHasToken returning false");
        return false;
    }


    public void setServerHasToken(boolean serverHasToken) {
        mServerHasToken = serverHasToken;
    }

*/
}
