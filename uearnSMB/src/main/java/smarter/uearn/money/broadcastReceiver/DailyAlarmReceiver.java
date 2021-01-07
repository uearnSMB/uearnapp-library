package smarter.uearn.money.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Srinath on 03-03-2017.
 */

public class DailyAlarmReceiver extends BroadcastReceiver {

    Context mainContext;
    int followUpCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        //Log.d("DailyAlAramService","InReceiver");
        if(!logout) {
            Calendar cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
            Long start = cal.getTimeInMillis();
            Long end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Date date = new Date();
            int overdue = 0, scheduled = 0, done = 0;
            String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' "+ "AND"+ " RESPONSE_STATUS "+ "=" +" 'accepted' ";
            Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
            if(ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")))) {
                } else {
                    if (cursor != null && cursor.getCount() > 0) {
                        CommonUtils.buildmissedNotification(context, cursor.getCount(), 0);
                    }
                }
            } else {
                if (cursor != null && cursor.getCount() > 0) {
                    CommonUtils.buildmissedNotification(context, cursor.getCount(), 0);
                }
            }

           /* if (intent.hasExtra("flag")) {
                int flag = intent.getIntExtra("flag", 0);
                if(flag == 1) {
                    if (ApplicationSettings.getPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, false)) {
                        Log.d("DailyAlAramService","insideService");
                        Intent intent1 = new Intent(context, AutoFollowupService.class);
                        context.startService(intent1);
                    }
                }
            }*/
        }
    }


}
