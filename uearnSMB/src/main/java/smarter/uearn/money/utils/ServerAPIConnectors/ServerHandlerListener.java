package smarter.uearn.money.utils.ServerAPIConnectors;

/**
 * Created by Admin on 23-06-2015.
 */
public interface ServerHandlerListener {

    void onSuccess(long requestCode, String result);
    void onError(long requestCode, String error); /* API returns an error */
    void onFailure(long requestCode, int errorCode);  /* HTTP response failure */
}
