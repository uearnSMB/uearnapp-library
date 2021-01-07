package smarter.uearn.money.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import smarter.uearn.money.services.AutoFollowupService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Srinath on 06-04-2017.
 */

public class AutoFollowupCreateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        //Log.d("AutoAlaramService","InReceiver");
        if(!logout) {
            if (ApplicationSettings.getPref(AppConstants.AUTO_FOLLOW_UPS_SETTINGS, false)) {
                if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                    if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")))) {

                        Calendar cal = Calendar.getInstance();
                        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0);
                        Long start = cal.getTimeInMillis();
                        Long end = cal.getTimeInMillis() + (24 * 60 * 60 * 1000);
                        MySql dbHelper = MySql.getInstance(context);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        Date date = new Date();
                        String selection = "START_TIME" + "<=" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != " + " 'deleted' " + "AND" + " RESPONSE_STATUS " + "=" + " 'accepted' ";
                        Cursor cursor = db.query("remindertbNew", null, selection, null, null, null, "START_TIME ASC");
                        int missed = 0, todo = 0;
                        try {
                            if (cursor != null && cursor.getCount() > 0) {
                                missed = cursor.getCount();
                                cursor.close();
                            } else {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }
                            String selection2 = "START_TIME" + ">" + date.getTime() + " AND " + "START_TIME" + ">=" + start + " AND " + "START_TIME" + "<=" + end + " AND " + "COMPLETED" + "=" + 0 + " AND " + "STATUS != 'deleted' " + " AND" + " RESPONSE_STATUS = 'accepted' ";
                            Cursor cursor2 = db.query("remindertbNew", null, selection2, null, null, null, "START_TIME ASC");
                            if (cursor2 != null && cursor2.getCount() > 0) {
                                todo = cursor2.getCount();
                                cursor2.close();

                            } else {
                                if (cursor2 != null) {
                                    cursor2.close();
                                }
                            }
                            CommonUtils.buildNotification(context, missed, todo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                //Log.d("DailyAlAramService","insideService");
                //Intent intent1 = new Intent(context, AutoFollowupService.class);
                //context.startService(intent1);
            }
        }
    }
}
