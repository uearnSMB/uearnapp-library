package smarter.uearn.money.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import smarter.uearn.money.services.CreateNotificationService;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class Alarm_Receiver extends BroadcastReceiver {

    public String event = "No Title", description = "No details were added";
    public long id = 0;
    Long current = System.currentTimeMillis();


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.hasExtra("id")) {
            id = intent.getLongExtra("id", 0);
        }

        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        if (!logout) {
            if (id != 0) {
                if(ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                    String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                    if(screen != null && !((screen.equalsIgnoreCase("Bank1AfterCallActivity")) || (screen.equalsIgnoreCase("UearnActivity")))) {
                        if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")))) {
                            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false) && ApplicationSettings.getPref(AppConstants.AUTO_DAILER, false)) {
                                setReClickToAlarm(context, id);
                            } else {
                                Intent intent1 = new Intent(context, CreateNotificationService.class);
                                intent1.putExtra("id", id);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(intent1);
                                } else {
                                    context.startService(intent1);
                                }

                            }
                        } else {
                            Intent intent1 = new Intent(context, CreateNotificationService.class);
                            intent1.putExtra("id", id);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(intent1);
                            } else {
                                context.startService(intent1);
                            }

                        }
                    }
                } else {
                    Intent intent1 = new Intent(context, CreateNotificationService.class);
                    intent1.putExtra("id", id);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent1);
                    } else {
                        context.startService(intent1);
                    }

                }
            }
        }
    }

    private void setReClickToAlarm(Context context, long dbid) {
        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        String toname = "";
        String appointmentId = "", status = "", substatus1 = "", substatus2 = "" , toNumber ="";
        long start_time = 0l;


        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long date_time = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + id + "'" + " AND " + " COMPLETED " + "!= " + 1 + " AND " + "STATUS != 'deleted' ", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            toname = cursor.getString(cursor.getColumnIndex("TONAME"));
            appointmentId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
            status = cursor.getString(cursor.getColumnIndex("STATUS"));
            substatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
            substatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
            start_time = cursor.getLong(cursor.getColumnIndex("START_TIME"));
            toNumber = cursor.getString(cursor.getColumnIndex("TO1"));
        }
        if(ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
            //Log.d("AftercallScreen", ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") );
            if (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "") != null && ((ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto1AfterCallActivity")) || (ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "").equalsIgnoreCase("Auto2AfterCallActivity")))) {
                if (toNumber != null && id != 0) {
                    current = current - 6000;
                    if(current <= start_time) {
                        if (appointmentId == null) {
                            appointmentId = "";
                        }
                        if (status == null) {
                            status = "";
                        }
                        if (substatus1 == null) {
                            substatus1 = "";
                        }
                        if (substatus2 == null) {
                            substatus2 = "";
                        }
                        if (toname == null) {
                            toname = "";
                        }
                        if (description == null) {
                            description = "";
                        }
                        if (!logout) {
                            if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false) && ApplicationSettings.getPref(AppConstants.AUTO_DAILER, false)) {
                                CommonUtils.createMakeCall(context, toNumber, id, appointmentId, status, substatus1, substatus2, toname, description);
                            }
                        }
                    }
                }
            }
        }
    }
}
