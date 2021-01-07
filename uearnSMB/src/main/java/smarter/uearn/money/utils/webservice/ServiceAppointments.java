package smarter.uearn.money.utils.webservice;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import smarter.uearn.money.utils.CommonOperations;

import java.util.Calendar;

/**
 * Created by dilipkumar4813 on 17/02/17.
 *
 * @author Dilip
 * @version 1.0
 */

public class ServiceAppointments {

    static private String start_time, end_time;

    public static void getCalendarEvents(Activity activity, int skip) {
        //ServiceUserProfile.getAuth(activity);

        initializeCalenderDateAndTime();

        SharedPreferences prefs = activity.getSharedPreferences("SESSION", Context.MODE_PRIVATE);

        String builtUrl = Constants.DOMAIN_URL + Constants.GET_CALENDAR_EVENTS_URL + prefs.getString("user_id", "")
                + "&start_date=" + start_time + "&end_date=" + end_time +
                "&skip=" + skip + "&calendar_type=smartercalendar&top=50";

        //Log.d("appointment", "url " + builtUrl);

        if (activity != null) {
            if (CommonOperations.isNetworkConnected(activity, 2)) {

                String[] data = {Constants.GET, builtUrl};
                WebService webCall = new WebService(activity, 6);
                webCall.execute(data);
            }
        }
    }

    private static void initializeCalenderDateAndTime() {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
        start_time = CommonOperations.getTimeFormatInISO(c.getTime());
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
        end_time = CommonOperations.getTimeFormatInISO(c.getTime());
    }


}
