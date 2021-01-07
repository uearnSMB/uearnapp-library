package smarter.uearn.money.utils.webservice;

import android.app.Activity;
import android.content.ContentValues;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created 28-02-2017.
 *
 * @author Dilip
 * @version 1.0
 */

public class ServiceApplicationUsage {

    public static void callErrorLog(String message) {

        ContentValues values = new ContentValues();
        values.put("message", message);
        values.put("email", "bsvijay@smarter-biz.com");
        String[] data = {Constants.POST, Constants.DOMAIN_URL + Constants.SMARTER_ERROR_LOGS, CommonOperations.buildURL(values)};
        CommonUtils.checkToken();
        WebService webCall = new WebService(10);
        webCall.execute(data);
    }

    public static void remoteDialStart(Activity activity,String whentostart) {
        JSONObject passing = new JSONObject();
        try {
            passing.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, ""));
            passing.put("whentostart", whentostart);
            String[] data = {Constants.POST, Constants.DOMAIN_URL + Constants.SMARTER_REMOTE_DIAL_START};
            CommonUtils.checkToken();
            WebService webCall = new WebService(activity,9, passing);
            webCall.execute(data);
        } catch (JSONException e) {

        }
    }

    public static void remoteDialStop(Activity activity,String stopReason) {
        JSONObject passing = new JSONObject();
        try {
            passing.put("user_id", ApplicationSettings.getPref(AppConstants.USERINFO_ID, ""));
            passing.put("stopreason", stopReason);
            String[] data = {Constants.POST, Constants.DOMAIN_URL + Constants.SMARTER_REMOTE_DIAL_STOP};
            CommonUtils.checkToken();
            WebService webCall = new WebService(activity, 9, passing);
            webCall.execute(data);
        } catch (JSONException e) {

        }
    }

}
