package smarter.uearn.money.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import smarter.uearn.money.models.PersonalJunkContact;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class CustomActivity  extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.custome_dialog_layout);
        SmarterSMBApplication.currentActivity = this;
        this.setFinishOnTouchOutside(false);
        TextView tv = findViewById(R.id.message);
        Button exit = findViewById(R.id.no);
        Button ok = findViewById(R.id.ok);
        final String customerNumber = getIntent().getStringExtra("customer_number");
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPersonalToServer(customerNumber);
                savePrivateContacts(customerNumber);
                finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv.setText("This is a new enquiry, please update DMS");
    }

    private void addPersonalToServer(String number) {
        PersonalJunkContact personalContact = new PersonalJunkContact(0, number);

        if (CommonUtils.isNetworkAvailable(this)) {
            new APIProvider.AddPersonalNumberApi(personalContact, 13, null, "Adding number", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {

                    }
                }
            }).call();
        }
    }

    private void savePrivateContacts(String number) {

        MySql dbHelper = MySql.getInstance(SmarterSMBApplication.currentActivity);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        if (number != null && !(number.isEmpty())) {
            Cursor cursor = db1.rawQuery("SELECT * FROM PrivateContacts where NUMBER=" + "'" + number + "'", null);
            if (!(cursor != null && cursor.getCount() > 0)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("NUMBER", number);
                db1.insert("PrivateContacts", null, contentValues);
            }
        }
        db1.close();
    }
}
