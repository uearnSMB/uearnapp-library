package smarter.uearn.money.activities;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.ReuploadService;

import java.util.ArrayList;

public class MakeACallAfterCallActivity extends BaseActivity implements View.OnClickListener {

    public String to, from;
    Long dbid;
    String stage1 = "";
    public final static String AFTER_CALL_NOTES = "afterCallNotes";
    public final static String AFTER_CALL_ASSIGN = "assignTo";
    public final static String AFTER_CALL_NAME = "after_call_name";
    public final static String AFTER_CALL_DESIGNATION = "after_call_designation";
    public final static String AFTER_CALL_PHONE = "after_call_phone";
    public final static String AFTER_CALL_COMPANY = "after_call_company";
    public final static String AFTER_CALL_EMAIL = "after_call_email";
    public final static String AFTER_CALL_LEAD = "after_call_lead";
    public final static String AFTER_CALL_ADDRESS = "after_call_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        restoreActionBar("Call");
        setContentView(R.layout.make_call_aftercall_activity);
        Button submit = findViewById(R.id.submit_buttom);
        submit.setOnClickListener(this);
        SmarterSMBApplication.currentActivity = this;
        getDetailsFromDb();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.submit_buttom) {
            this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDetailsFromDb() {

        String ns = Context.NOTIFICATION_SERVICE;
        String filePath = "";
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("dbid") && intent.getLongExtra("dbid", 0) != 0) {
                dbid = intent.getLongExtra("dbid", 0);
            }
            if (intent.hasExtra("notification_id")) {
                int id = intent.getIntExtra("notification_id", 1);
                mNotificationManager.cancel(id);
            }
        }

        if (dbid != 0) {
            String endTime = "";

            MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM mytbl where _id=" + "'" + dbid + "'", null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    from = cursor.getString(cursor.getColumnIndex("FROM1"));
                    to = cursor.getString(cursor.getColumnIndex("TO1"));
                }
                cursor.close();
            if (db.isOpen()) {
                db.close();
            }

            dbHelper.close();
        }

        String name = "";
        if (name != null && !(name.isEmpty())) {
            restoreActionBar(name);
        } else if(to != null && !(to.isEmpty())) {
            restoreActionBar(to);
        } else {
            restoreActionBar("Call");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        NotificationData.substatus1 = "";
        NotificationData.substatus2 = "";
        NotificationData.knolarity_name = "";
        NotificationData.knolarity_number = "";
        NotificationData.knolarity_start_time = "";
        NotificationData.appointment_db_id = 0;

        createFirstCallFollowup();
        ContentValues cv = new ContentValues();
        cv.put("UPLOAD_STATUS", 1);
        if(dbid != 0) {
            try {
                MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                db1.update("mytbl", cv, "_id=" + dbid, null);
                db1.close();
                Intent intent1 = new Intent(this, ReuploadService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent1);
                } else {
                    startService(intent1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ApplicationSettings.putPref(AFTER_CALL_NOTES, "");
        ApplicationSettings.putPref(AFTER_CALL_ASSIGN, "");
        ApplicationSettings.putPref(AppConstants.AFTER_CALL_AUDIO_URL, "");
        ApplicationSettings.putPref(AFTER_CALL_DESIGNATION, "");
        ApplicationSettings.putPref(AFTER_CALL_PHONE, "");
        ApplicationSettings.putPref(AFTER_CALL_COMPANY, "");
        ApplicationSettings.putPref(AFTER_CALL_EMAIL, "");
        ApplicationSettings.putPref(AFTER_CALL_LEAD, "");
        ApplicationSettings.putPref(AFTER_CALL_ADDRESS, "");
        ApplicationSettings.putPref(AFTER_CALL_NAME, "");
        finish();
    }

    private void createFirstCallFollowup() {
        String statusValue = checkSalesList();

        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long start_time = System.currentTimeMillis();
        long endTime = (start_time + (60 * 60 * 1000));

        ContentValues cv = new ContentValues();
        cv.put("SUBJECT", "");
        cv.put("NOTES", "");
        cv.put("START_TIME", start_time);
        cv.put("END_TIME", endTime);
        cv.put("LOCATION", "");
        cv.put("COMPANY_NAME", "");
        cv.put("DESIGNATION", "");
        cv.put("EMAILID", "");
        cv.put("WEBSITE", "");
        if(statusValue != null) {
            cv.put("STATUS", statusValue);
        }
        cv.put("UPLOAD_STATUS", 0);
        cv.put("ALARMSETTO", 0);
        cv.put("COMPANY_ADDRESS", "");
        cv.put("PRODUCT_TYPE", "");
        cv.put("ORDER_POTENTIAL", "");
        cv.put("LEAD_SOURCE", "");
        cv.put("FLP_COUNT", 0);
        cv.put("COMPLETED", 1);

        if (ApplicationSettings.getPref(AppConstants.AFTER_CALL_AUDIO_URL, "") != null) {
            cv.put("NOTES_AUDIO", ApplicationSettings.getPref(AppConstants.AFTER_CALL_AUDIO_URL, ""));
        }

        if (to != null) {
            cv.put("TO1", to);
            cv.put("TONAME", "");
            cv.put("RESPONSE_STATUS", "completed");
            db.insert("remindertbNew", null, cv);
        }

        if (db.isOpen()) {
            db.close();
        }

        dbHelper.close();
    }

    private String checkSalesList() {
        String user_id = SmarterSMBApplication.SmartUser.getId();
        SalesStageInfo salesStageInfo = SmarterSMBApplication.salesStageInfo;

        if (salesStageInfo != null) {
            ArrayList<String> arrayList1 = salesStageInfo.getAppointmentSalesStage();
            if (arrayList1 == null && arrayList1.size() > 0) {
                if (CommonUtils.isNetworkAvailable(MakeACallAfterCallActivity.this)) {
                    new APIProvider.Get_Sales_Status(user_id, 22, new API_Response_Listener<SalesStageInfo>() {
                        @Override
                        public void onComplete(SalesStageInfo data, long request_code, int failure_code) {
                            if (data != null) {
                                data.dosave();
                                SmarterSMBApplication.salesStageInfo = data;
                                ArrayList<String> arrayList2 = data.getAppointmentSalesStage();
                                if (arrayList2 != null && (arrayList2.size() > 0)) {
                                    stage1 = arrayList2.get(0);
                                }
                            }
                        }
                    }).call();
                }
            } else {
                stage1 = arrayList1.get(0);
                return stage1;
            }
        }

        return stage1;
    }
}
