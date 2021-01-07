package smarter.uearn.money.utils.ServerAPIConnectors;

import android.app.Activity;
import android.app.ProgressDialog;

import smarter.uearn.money.activities.UearnHome;


/**
 * Created by Bala on 6/30/2015.
 */
public abstract class WaitingAPIAdapter<T, L> extends APIAdapter<T, L> {

    private ProgressDialog waitDialog;
    private Activity activity;

    public WaitingAPIAdapter(T input_data, long requestCode, Activity activity, String message, API_Response_Listener<L> listener) {
        super(input_data, requestCode, listener);
        if(activity != null) {
            waitDialog = new ProgressDialog(activity);
            waitDialog.setMessage(message);
            waitDialog.setCancelable(false);
            this.activity = activity;
            waitDialog.setOwnerActivity(activity);
        }
    }

    public WaitingAPIAdapter(T input_data, long requestCode, boolean knolarity, API_Response_Listener<L> listener) {
        super(input_data, requestCode,knolarity, listener);
    }

    @Override
    public void preProcess() {
        super.preProcess();
        if (activity == null) {
            return;
        }
        if (activity.isFinishing()) {
            return;
        }
        if(waitDialog != null && !waitDialog.isShowing()){
            if (activity instanceof UearnHome) {
                return;
            } else {
                waitDialog.show();
            }



        }
    }

    @Override
    public void postProcess() {
        super.postProcess();
        if (activity == null) {
            return;
        }
        if (activity.isFinishing()) {
            return;
        }
        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.dismiss();
            waitDialog = null;
        }
    }
}
