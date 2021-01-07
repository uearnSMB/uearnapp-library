package smarter.uearn.money.utils.socialmedia;

import android.util.Log;

import smarter.uearn.money.utils.ServerAPIConnectors.ServerHandlerListener;
import smarter.uearn.money.utils.JSONParser;

/**
 * Created by Srinath on 15-Nov-16.
 */
public class ServerAuthHelper {

    public static void accessTokenCall(final int requestcode) {
        ServerHandlerListener serverListener = new ServerHandlerListener() {
            @Override
            public void onSuccess(long requestCode, String result) {
                //Log.d("results", result);
                savedata(result, requestcode);
            }

            @Override
            public void onFailure(long requestCode, int errorCode) {
                //Toast.makeText(LoginActivity.this,"Filed to get tokens",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(long requestCode, String error) {
                //Toast.makeText(LoginActivity.this,"Error while getting tokens",Toast.LENGTH_SHORT).show();
            }
        };
        ServerAuth serverAuth = new ServerAuth(serverListener);
        serverAuth.call(requestcode);
    }

    public static void savedata(String result, int request_code) {
        if(request_code == 0) {
            //Log.d("Auth Response", result);
            JSONParser.getAccessToken(result);
        } else if(request_code == 1) {
            //Log.d("Auth Response", result);
            JSONParser.getRefreshToken(result);
        }
    }

}
