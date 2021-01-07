package smarter.uearn.money.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import smarter.uearn.money.activities.DataLoadingActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;

/**
 * Created by Srinath on 03-04-2017.
 */

public class BootableService extends BroadcastReceiver {

    private SQLiteDatabase db;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
        //Log.d("BOOTABLE RECEIVER", "Bootable triggered");
        if (!logout) {
            if(CommonUtils.isNetworkAvailable(context)) {
                String userId = "";
                if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                    userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                }
                String dbPath = deleteAllLocalDB(context);
                Intent intent2 = new Intent(context, DataLoadingActivity.class);
                intent2.putExtra("EXTRA_ID",userId);
                intent2.putExtra("EXTRA_DB_PATH", dbPath);
                intent2.putExtra("EXTRA_BOOTABLE", true);
                context.startActivity(intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else {
                Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String deleteAllLocalDB(Context context) {
        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String dbPath = db.getPath();
       /* db.delete("remindertbNew", null, null);
        db.delete("WorkOrder", null, null);
        db.delete("TeamMembers", null, null);
        db.delete("Ameyo_Push_Notifications", null, null);
        db.delete("ameyo_entries", null, null);
        db.delete("FirstCall", null, null);
        db.delete("ameyo_entries", null, null);*/
        db.close();
        return dbPath;
    }

}
