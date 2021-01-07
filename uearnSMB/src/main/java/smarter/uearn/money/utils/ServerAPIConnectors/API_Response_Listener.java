package smarter.uearn.money.utils.ServerAPIConnectors;

/**
 * Created by Bala on 6/30/2015.
 */
public interface API_Response_Listener<T> {
    void onComplete(T data, long request_code, int failure_code);
}
