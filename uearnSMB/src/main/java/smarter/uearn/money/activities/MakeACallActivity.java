package smarter.uearn.money.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import smarter.uearn.money.adapters.NumberBasedSearchCursorAdapter;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.JSONParser;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Validations;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.views.events.HeadPhoneReciever;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static smarter.uearn.money.activities.AfterCallActivity.AFTER_CALL_LEAD;
import static smarter.uearn.money.activities.AfterCallActivity.CUSTOMER_TO_CONTACT;

public class MakeACallActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText callerNumber;
    String stage1 = "";
    private RelativeLayout searviewLayout, closelayout;
    private ListView searchList;
    private SQLiteDatabase db;
    NumberBasedSearchCursorAdapter customCursorAdapter;
    long id = 0l;
    private String name = "", status = "", substatus1 = "", substatus2 = "", leadSource = "", appointmentId = "";
    private HeadPhoneReciever myReceiver;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.make_call);

        MySql dbHelper = new MySql(this, "mydb", null, AppConstants.dBversion);
        db = dbHelper.getWritableDatabase();

        callerNumber = findViewById(R.id.numbertoCall);
        searviewLayout = findViewById(R.id.searchLayout);
        closelayout = findViewById(R.id.home_cancel_layout);
        searchList = findViewById(R.id.searchListView);
        searchList.setOnItemClickListener(this);
        SmarterSMBApplication.currentActivity = this;
        RelativeLayout makeACall = findViewById(R.id.call_action_layout);
        makeACall.setOnClickListener(this);
        closelayout.setOnClickListener(this);
        changeStatusBarColor(this);

        myReceiver = new HeadPhoneReciever();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
        callerNumber.requestFocusFromTouch();
        callerNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number = callerNumber.getText().toString();
                search(number);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(callerNumber, 0);
    }

    private void search(String searchContent) {
        MySql dbHelper = new MySql(this, "mydb", null, AppConstants.dBversion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (searchContent != null && searchContent != "") {
            String selection = "TO1 LIKE '%" + searchContent + "%' ";
            Cursor cursor3 = db.query(true, "remindertbNew", null, selection, null, "TO1", null, "START_TIME DESC", null);
            if (cursor3 != null) {
                if (customCursorAdapter != null) {
                    searviewLayout.setVisibility(View.VISIBLE);
                    customCursorAdapter.changeCursor(cursor3);
                    customCursorAdapter.notifyDataSetChanged();
                    searchList.setAdapter(customCursorAdapter);
                } else {
                    customCursorAdapter = new NumberBasedSearchCursorAdapter(this, cursor3, 0);
                    searchList.setAdapter(customCursorAdapter);
                }
            }
        }
    }

    private void getLatestRecord(String num) {

        String selection = "TO1 = '" + num + "' ";
        Cursor cursor = db.query(true, "remindertbNew", null, selection, null, "TO1", null, "START_TIME DESC", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getLong(cursor.getColumnIndex("_id"));
            name = cursor.getString(cursor.getColumnIndex("TONAME"));
            status = cursor.getString(cursor.getColumnIndex("STATUS"));
            substatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
            substatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
            leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
            appointmentId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
        } else {
            id = 0l; name = ""; status = ""; substatus1 = ""; substatus2 = ""; leadSource = "";
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.call_action_layout) {

            if (ApplicationSettings.containsPref(AppConstants.DISALLOW_HEADPHONES)) {
                boolean disallowHeadphones = ApplicationSettings.getPref(AppConstants.DISALLOW_HEADPHONES, false);
                if (disallowHeadphones) {

                } else {
                    if (!SmarterSMBApplication.isHeadPhone) {
                        if (!isBluetoothHeadsetConnected()) {
                            showDialog(this);
                            return;
                        }
                    }
                }
            } else {
                if (!SmarterSMBApplication.isHeadPhone) {
                    if (!isBluetoothHeadsetConnected()) {
                        showDialog(this);
                        return;
                    }
                }
            }

            String num = callerNumber.getText().toString();
            if(num == null || num.isEmpty()){
                Toast.makeText(this, "Number cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (!Validations.isValidNumber(num)) {
                if(callerNumber != null) {
                    if (num != null && !num.isEmpty() && !num.startsWith("+91")) {
                        num = "+91" + num;
                    }
                    callerNumber.setText(num);
                    callerNumber.setSelection(callerNumber.getText().length());
                }
                Toast.makeText(this, "Please provide a valid phone number", Toast.LENGTH_SHORT).show();
            } else {
                clickToCall(num);
            }
        } else if(id == R.id.home_cancel_layout) {
            this.finish();
        }
    }

    private void prepareData(String num) {
        try {
            List<CustomerLite> customerList = new ArrayList<>();
            List<CustomerLite> tempcustomerList = new ArrayList<>();

            String name = "No Name";
            String customerName = getCallerName(num);
            if(customerName != null && !customerName.isEmpty()) {
                name = customerName;
            }
            CustomerLite customerLiteInfo = new CustomerLite(0, name, num, status, substatus1, substatus2, leadSource,"","", "", "");
            customerList.add(customerLiteInfo);
            tempcustomerList.add(0, customerLiteInfo);
            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
            String customerListData = new Gson().toJson(tempcustomerList);
            ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData);
            System.out.print(customerListData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCallerName(String number) {
        String customerName = "";
        try {
            MySql dbHelper = new MySql(this, "mydb", null, AppConstants.dBversion);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            String selection = "TO1=" + "'" + number + "'";
            Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                customerName = cursor.getString(cursor.getColumnIndex("TONAME"));
            }
            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        restoreActionBar("Make A Call");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clickToCall(String toNumber) {

        if (ApplicationSettings.containsPref(AppConstants.DISALLOW_HEADPHONES)) {
            boolean disallowHeadphones = ApplicationSettings.getPref(AppConstants.DISALLOW_HEADPHONES, false);
            if (disallowHeadphones) {

            } else {
                if (!SmarterSMBApplication.isHeadPhone) {
                    if (!isBluetoothHeadsetConnected()) {
                        showDialog(this);
                        return;
                    }
                }
            }
        } else {
            if (!SmarterSMBApplication.isHeadPhone) {
                if (!isBluetoothHeadsetConnected()) {
                    showDialog(this);
                    return;
                }
            }
        }

        clearFlags();

        if (SmarterSMBApplication.isRemoteDialledStart || SmarterSMBApplication.currentStateIsStartMode) {
            SmarterSMBApplication.outgoingCallNotInStartMode = false;
        } else {
            SmarterSMBApplication.outgoingCallNotInStartMode = true;
        }

        SmarterSMBApplication.isDiallingFollowUpC2C = true;
        NotificationData.isSocketResponse = false;
        ServiceHandler.callDisconnected = false;
        SmarterSMBApplication.callEndedFromDuringCall = false;
        SmarterSMBApplication.navigatingToUearnHome = false;
        SmarterSMBApplication.actionMoveToNormalProcessed = false;
        SmarterSMBApplication.isC2CAutoStart = true;
        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
        ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
        ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, "");
        ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, "");
        ApplicationSettings.putPref(AppConstants.MMT_STATUS, "");
        ApplicationSettings.putPref("SUBJECT", "");
        UearnActivity.redialScenario = false;
        NotificationData.dialledCustomerNumber = "";
        NotificationData.customerFeedback = "";
        NotificationData.updatedCustomKVS = "";
        if (SmarterSMBApplication.connectedCustomerInProcess) {
            SmarterSMBApplication.connectedCustomerInProcess = false;
        }
        if (SmarterSMBApplication.disconnectedCustomerInProcess) {
            SmarterSMBApplication.disconnectedCustomerInProcess = false;
        }

        if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
            boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
            if (adhocCall) {
                String questionnaire = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE_FROM_SETTINGS, "");
                ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
            }
        }

        NotificationData.statusString = status;

        if (CommonUtils.isNetworkAvailable(this)) {
            if (toNumber != null && !toNumber.isEmpty() && !toNumber.startsWith("+91")) {
                toNumber = "+91" + toNumber;
            }
            callerNumber.setText(toNumber);
            callerNumber.setSelection(callerNumber.getText().length());
            if (toNumber != null) {
                CommonUtils.setToast(this, "Dialling ...");
                createSbiMakeCall(this, toNumber, id, appointmentId, status, substatus1, substatus2, name, "");
            }
        } else {
            Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    void showDialog(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.alert_dialog, null,false);

        Button cancelBtn = contentView.findViewById(R.id.cancelBtn);
        Typeface titlef = Typeface.createFromAsset(context.getAssets(),
                "fonts/SF-UI-Display-Semibold.ttf");
        TextView hints1Tv = contentView.findViewById(R.id.hints1Tv);
        hints1Tv.setTypeface(titlef);
        TextView hints2Tv = contentView.findViewById(R.id.hints2Tv);

        Typeface titleFace = Typeface.createFromAsset(context.getAssets(),
                "fonts/SF-UI-Display-Semibold.ttf");
        cancelBtn.setTypeface(titleFace);

        alertDialog = new AlertDialog.Builder(context)
                .setView(contentView)
                .create();

        alertDialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void clearFlags() {
        NotificationData.customerFeedback = "";
        NotificationData.updatedCustomKVS = "";
        ServiceHandler.callDisconnected = false;
        SmarterSMBApplication.incomingCallAcceptedByAgent = false;
        SmarterSMBApplication.incomingCallRejectedByAgent = false;
        SmarterSMBApplication.endTheSession = false;
        SmarterSMBApplication.callEndedFromDuringCall = false;
        SmarterSMBApplication.actionMoveToNormalProcessed = false;
        NotificationData.dialledCustomerNumber = "";
        NotificationData.dialledCustomerName = "";
        SmarterSMBApplication.isCampaignOver = false;
        SmarterSMBApplication.navigatingToUearnHome = false;
        SmarterSMBApplication.isDiallingFollowUpC2C = false;
        SmarterSMBApplication.manualDialScenario = false;
        SmarterSMBApplication.followupsInPredictive = false;
        SmarterSMBApplication.callStateIsDisconnected = false;
        SmarterSMBApplication.oneCallIsActive = false;
        SmarterSMBApplication.enableECBAndESB = false;
        SmarterSMBApplication.matchingInNumberNotInStartMode = false;
        SmarterSMBApplication.outgoingCallNotInStartMode = false;
        SmarterSMBApplication.isInNumberMatching = false;
        SmarterSMBApplication.currentStateIsStartMode = false;
        SmarterSMBApplication.autoCallAnswered = false;
        SmarterSMBApplication.agentIsInConnectingState = false;
        SmarterSMBApplication.agentIsInConnectedState = false;
        NotificationData.isSocketResponse = false;
        SmarterSMBApplication.currentModeIsConnected = false;
        SmarterSMBApplication.remoteEnabledRedialScenario = false;
        SmarterSMBApplication.webViewOutgoingCallEventTriggered = false;
        SmarterSMBApplication.moveToRAD = false;
        SmarterSMBApplication.moveToNormal = false;
        NotificationData.outboundDialledCustomerNumber = "";
        NotificationData.outboundDialledCustomerName = "";
        NotificationData.outboundDialledTransactionId = "";
        SmarterSMBApplication.takingBreakInPD = false;
        SmarterSMBApplication.lastConnectedCustomer = "";
        SmarterSMBApplication.lastConnectedCustomerFeedback ="";
        NotificationData.emailId = "";
    }

    private void navigateToUearnActivity() {
        Intent intent = new Intent(this, UearnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("showinprogress", 1);
        startActivity(intent);
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    private String checkNo() {
        String value = callerNumber.getText().toString();
        String numberWithCode = "";
        if (value != null) {
            if (CommonUtils.buildE164Number("", value) != null) {
                callerNumber.setText(CommonUtils.buildE164Number("", value));
            } else {
                if ((ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "")) != null) {
                    String customerno = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                    if (!customerno.toString().startsWith("+")) {
                        customerno = "+" + customerno;
                    }
                    if (CommonUtils.buildE164Number("", customerno.toString()) != null) {
                        String e164Region;
                        try {
                            PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
                            Phonenumber.PhoneNumber phoneNumber = instance.parse(customerno.toString(), "");
                            e164Region = instance.getRegionCodeForNumber(phoneNumber);
                            if (e164Region != null) {
                                numberWithCode = CommonUtils.buildE164Number(e164Region, value);
                            }
                        } catch (NumberParseException e) {

                        }
                    }
                    if (numberWithCode != null) {
                        callerNumber.setText(numberWithCode);
                    }
                }
            }
        }
        return numberWithCode;
    }

    public void createSbiMakeCall(final Context context, final String number,final long dbid, final String appointmentId,final String status, final  String substatus1, final  String substatus2, final String callername, final String notes) {
        if(context != null) {
            if(checkPermission(context)) {
                boolean secondSim = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
                    List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                    if (subsInfoList != null) {
                        for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                            String number1 = subscriptionInfo.getNumber();
                            int SimStotNumber = subscriptionInfo.getSimSlotIndex();
                            if (SimStotNumber == 1) {
                                secondSim = true;
                            }
                        }
                    } else {
                        CommonUtils.setToast(context, "No SIM");
                        return;
                    }
                } else {
                    secondSim = true;
                }

                /*boolean mobileOnSimData = false;
                if (ApplicationSettings.containsPref(AppConstants.MOBILE_DATA_ON_SIM)) {
                    mobileOnSimData = ApplicationSettings.getPref(AppConstants.MOBILE_DATA_ON_SIM, false);
                }*/

                if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                    if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
                        /*if ((!secondSim && !(mobileOnSimData)) || (secondSim && mobileOnSimData)) {*/
                        if (number != null) {
                            String userNumber = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                            if (userNumber != null && !(userNumber.isEmpty())) {
                                final String customernumber = number;
                                String sr_number = ApplicationSettings.getPref(AppConstants.SR_NUMBER, "");
                                String caller_id = ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING1, "");

                                if (caller_id != null && !(caller_id.isEmpty())) {
                                    if (sr_number != null && !(sr_number.isEmpty())) {
                                        if (CommonUtils.isC2cNetworkAvailable(context)) {
                                            KnowlarityModel knowlarityModel = new KnowlarityModel(sr_number, userNumber, customernumber);
                                            knowlarityModel.setClient_id(caller_id);
                                            String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
                                            if (screen != null && (screen.equalsIgnoreCase("Bank1AfterCallActivity") || screen.equalsIgnoreCase("UearnActivity"))) {
                                                NotificationData.appointment_db_id = dbid;
                                                if (dbid == 0) {
                                                    NotificationData.callFromDialer = true;
                                                }

                                                if (!existInDB(customernumber)) {
                                                    createAppointmentInAcceptedStateNotInDB(customernumber);
                                                }

                                                if (!existInDB(customernumber)) {
                                                    SmarterSMBApplication.createAppointmentForManualDialNumberNotInDB = true;
                                                    sbiCall(knowlarityModel, customernumber, status, context, substatus1, substatus2, callername, notes, appointmentId, dbid);
                                                } else {
                                                    sbiCall(knowlarityModel, customernumber, status, context, substatus1, substatus2, callername, notes, appointmentId, dbid);
                                                }
                                            } else {
                                                callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                                            }
                                        } else {
                                            Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        CommonUtils.setToast(context, "No SR Number");
                                        callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                                    }
                                } else {
                                    CommonUtils.setToast(context, "No Client ID");
                                }
                            } else {
                                CommonUtils.setToast(context, "Invalid User Number");
                            }
                        } else {
                            CommonUtils.setToast(context, "Invalid Customer Number");
                        }
                       /* } else {
                            callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                        }*/
                    } else {
                        callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                    }
                } else {
                    Log.d("AppointmentId", "CommonUtls id:----" +dbid);
                    Log.d("AppointmentId", "CommonUtls appointmentId:----" +appointmentId);
                    NotificationData.appointment_db_id = dbid;
                    callToCustomer(context, number, appointmentId, status, substatus1, substatus2, callername, notes);
                }
            } else {
                Toast.makeText(context, "please enable APP Permissions in Phone Settings. APPs -> UEARN -> Permissions ", Toast.LENGTH_SHORT).show();
            }
        } else {
            CommonUtils.setToast(context,"Follow-up already done");
        }
    }

    private boolean existInDB(String customerNumber) {
        MySql dbHelper = new MySql(this, "mydb", null, AppConstants.dBversion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where TO1=" + "'" + customerNumber + "'", null);
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public void sbiCall(KnowlarityModel knowlarityModel, final String customernumber, final String status, final Context context, final String substatus1, final String substatus2, final String callername, final String notes, final String appointmentId, final Long dbid) {
        NotificationData.knolarity_start_time = new Date().toString();
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                NotificationData.knolarity_response_time = new Date().toString();
                if (data != null && !(data.isEmpty())) {
                    if (data.contains("_SMBALERT_")) {
                        CommonUtils.setToast(getApplicationContext(),data.replace("_SMBALERT_", ""));
                        if(data.contains("Dialing Error. Please try after sometime")){

                        } else {
                            navigateToUearnActivity();
                        }
                    } else if (data.contains("_SMBACP_")) {
                        String uuidValue = data.replace("_SMBACP_", "");
                        try {
                            NotificationData.transactionId = uuidValue;
                            NotificationData.uuid = uuidValue;
                            NotificationData.knolarity_response = "success : ";
                            CommonUtils.setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        storeUuidHash(customernumber,uuidValue);
                        CommonUtils.setToast(getApplicationContext(), "Connecting to the customer. Please wait ...");
                        navigateToUearnActivity();
                    } else {
                        try {
                            NotificationData.transactionId = data;
                            Log.d("NotificationTrans", NotificationData.transactionId);
                            NotificationData.uuid = data;
                            NotificationData.knolarity_response = "success : ";
                            CommonUtils.setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
                            NotificationData.knolarity_number = customernumber;
                            ApplicationSettings.putPref(AppConstants.CLOUD_CUSTOMER_NUMBER, customernumber);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        storeUuidHash(customernumber,data);
                        CommonUtils.setToast(getApplicationContext(), "Connecting to the customer. Please wait ...");
                        navigateToUearnActivity();
                    }
                } else if (failure_code == APIProvider.INVALID_AUTH_KEY) {
                    Toast.makeText(getApplicationContext() ,"Invalid Auth key", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_REQUEST) {
                    Toast.makeText(getApplicationContext(), "Invalid Request", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_PARAMETER) {
                    Toast.makeText(getApplicationContext() ,"Invalid Parameters", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.INVALID_NUMBER) {
                    updateSalesStage(customernumber);
                    markFollowupAsCompleated(context, customernumber, dbid, appointmentId, 1);
                    Toast.makeText(getApplicationContext(), "This number is not valid", Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.SERVER_ALERT) {
                    Toast.makeText(getApplicationContext(), APIProvider.SERVER_ALERT_MESSAGE, Toast.LENGTH_LONG).show();
                } else if (failure_code == APIProvider.DND) {
                    ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                    if(cn != null && cn.getClassName().equals("smarter.uearn.money.activities.DashboardAgent")) {
                        CommonUtils.setToast(context, "DND Registered Number.");
                    } else {
                        CommonUtils.setToast(context, "DND Registered Number.");
                        NotificationData.knolarity_response = "DND : ";
                        markFollowupAsCompleated(context, customernumber, dbid, appointmentId, 0);
                        createCmail(context, customernumber, status, 0);
                        Intent intent = new Intent(context, ReuploadService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent);
                        } else {
                            context.startService(intent);
                        }
                    }
                } else if (failure_code == APIProvider.AGENET_NOT_VERIFIED) {
                    NotificationData.knolarity_response = "Agent bot verified : ";
                    CommonUtils.setToast(context,"Hi. Your mobile number in Not verified.");
                } else if (failure_code == APIProvider.SR_NOT_REGISTERED) {
                    NotificationData.knolarity_response = "Sr not registered : ";
                    CommonUtils.setToast(context, "Hey! Ask your admin to call Support at 9113907215.");
                } else if (failure_code == APIProvider.AGENT_NOT_REGISTERED) {
                    NotificationData.knolarity_response = "agent not registered : ";
                    Toast.makeText(getApplicationContext(), "Number not registered for outbound services for this agent", Toast.LENGTH_LONG).show();
                } else {
                    NotificationData.knolarity_response = "request failed : ";
                    CommonUtils.setToast(context,  "Please check your internet connection.");
                }
            }
            /*}).call();*/
        }).reClickToCall(knowlarityModel);
    }

    private void createAppointmentInAcceptedStateNotInDB(final String customerNumber) {

        MySql dbHelper = new MySql(this, "mydb", null, AppConstants.dBversion);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long start_time = System.currentTimeMillis();
        long end_time = (start_time + (60 * 10 * 1000));

        ContentValues cv = new ContentValues();
        cv.put("START_TIME", start_time);
        cv.put("END_TIME", end_time);
        cv.put("STATUS", "NEW DATA");
        cv.put("TO1", customerNumber);
        cv.put("FLP_COUNT", 99);
        cv.put("TONAME", "No Name");
        cv.put("UPLOAD_STATUS", 0);
        cv.put("COMPLETED", 1);
        cv.put("RESPONSE_STATUS", "accepted");
        cv.put("APPOINTMENT_TYPE", "update_appointment");
        String start = CommonUtils.getTimeFormatInISO(new Date());
        cv.put("CREATED_AT", start_time);
        cv.put("UPDATED_AT", start_time);
        cv.put("ORDER_POTENTIAL", "");
        cv.put("ASSIGN_TO", "");

        if (db != null && db.isOpen()) {
            db.close();
        }
        if(dbHelper != null)
            dbHelper.close();

        new Thread() {
            @Override
            public void run() {

                long start_time = System.currentTimeMillis();
                long end_time = (start_time + (60 * 10 * 1000));
                Date startDate = new Date();
                startDate.setTime(start_time);
                String startTime = CommonUtils.getTimeFormatInISO(startDate);
                Date endDate = new Date();
                endDate.setTime(end_time);
                String endTime = CommonUtils.getTimeFormatInISO(endDate);

                GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();

                getCalendarEntryInfo.setEvent_start_date(startTime);
                getCalendarEntryInfo.setEvent_end_date(endTime);

                getCalendarEntryInfo.setCaller_number(customerNumber);
                getCalendarEntryInfo.setCaller_name("No Name");
                getCalendarEntryInfo.setLead_source(NotificationData.leadSource);
                getCalendarEntryInfo.setStatus("NEW DATA");
                getCalendarEntryInfo.setLatitude(String.valueOf(0));
                getCalendarEntryInfo.setLongitude(String.valueOf(0));
                getCalendarEntryInfo.setResponsestatus("accepted");

                if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
                    JSONObject calendarJson = JSONParser.getnewJsonObjFromCalendar(getCalendarEntryInfo);
                    JSONObject responsejsonObject = null;

                    responsejsonObject = DataUploadUtils.postJsonData(Urls.getCalendarCreateUrl(), calendarJson);
                    if (responsejsonObject != null) {
                        if (calendarJson != null) {

                            MySql dbHelper = new MySql(getApplicationContext(), "mydb", null, AppConstants.dBversion);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            try {
                                String responseJson = responsejsonObject.getString("response");
                                JSONObject responseJsonObj = new JSONObject(responseJson);
                                if (responseJsonObj.has("Id")) {
                                    String id = responseJsonObj.getString("Id");
                                    ContentValues cv = new ContentValues();
                                    cv.put("UPLOAD_STATUS", 0);  // Changed Upload Status from 1 to 0 - Reverted
                                    db.update("remindertbNew", cv, "_id=" + id, null);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }.start();
    }

    public void callToCustomer(Context context, String number, String appointmentId, String status, String substatus1, String substatus2, String callername, String notes) {
        if(context != null) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            if (number != null) {
                String num = "tel:" + number;
                intent.setData(Uri.parse(num));
            }
            setCallData(appointmentId, status, substatus1, substatus2, callername, notes);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(intent);
        }
    }

    public void setCallData(String appointmentId, String status, String substatus1, String substatus2, String callername, String notes) {
        NotificationData.notificationData = true;
        NotificationData.appointment_id = appointmentId;
        NotificationData.statusString = status;
        NotificationData.notes_string = notes;
        NotificationData.order_value = "0";
        NotificationData.isAppointment = true;
        String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
        if (screen != null && ((screen.equalsIgnoreCase("Auto2AfterCallActivity") || (screen.equalsIgnoreCase("Auto1AfterCallActivity"))))) {
            NotificationData.makeACall = true;
        }
        if(callername != null) {
            NotificationData.knolarity_name = callername;
        }
        if(substatus1 != null && status != null && !status.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus1 = substatus1;
        }
        if(substatus2 != null && status != null && !status.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus2 = substatus2;
        }
    }

    public void markFollowupAsCompleated(Context context, String phoneno, Long dbid, String appointmentId, int status) {

        MySql dbHelper = MySql.getInstance(context);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();

        if(context != null && dbid != 0 && appointmentId != null) {

            ContentValues cv = new ContentValues();
            cv.put("UPLOAD_STATUS", 0);
            cv.put("COMPLETED", 1);
            cv.put("RESPONSE_STATUS", "completed");
            cv.put("APPOINTMENT_TYPE", "complete_appointment");
            if(status == AppConstants.INVALID) {
                cv.put("STATUS", "OTHERS");
                cv.put("SUBSTATUS1", "INVALID");
            } else {
                cv.put("SUBJECT", "DND NUMBER");
            }
            String start = CommonUtils.getTimeFormatInISO(new Date());
            cv.put("CREATED_AT", start);
            cv.put("UPDATED_AT", start);
            cv.put("RNR_COUNT", "0");

            if (dbid != 0) {
                db1.update("remindertbNew", cv, "_id=" + dbid, null);
            }
            if (db1 != null && db1.isOpen()) {
                db1.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }

           /* GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
            getCalendarEntryInfo.user_id = SmarterSMBApplication.SmartUser.getId();
            getCalendarEntryInfo.appointment_id = appointmentId;
            getCalendarEntryInfo.responsestatus = "completed";
            getCalendarEntryInfo.update_all_fields = false;
            getCalendarEntryInfo.tat = 0;
            getCalendarEntryInfo.rnrCount = 0;

            if (start != null && !(start.isEmpty())) {
                getCalendarEntryInfo.created_at = start;
            }*/

            //updateAppointmentCall(context, getCalendarEntryInfo, dbid);
        } else {

            String statusValue = checkSalesList();
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
            cv.put("UPDATED_AT", start_time);
            if(statusValue != null) {
                cv.put("STATUS", statusValue);
            }
            cv.put("UPLOAD_STATUS", 0);
            cv.put("ALARMSETTO", 0);
            cv.put("COMPANY_ADDRESS", "");
            cv.put("PRODUCT_TYPE", "");
            cv.put("ORDER_POTENTIAL", "");
            cv.put("LEAD_SOURCE", "");
            cv.put("COMPLETED", 1);
            cv.put("TO1", phoneno);
            cv.put("TONAME", "");

            cv.put("RESPONSE_STATUS", "completed");
            cv.put("APPOINTMENT_TYPE", "complete_appointment");

            db1.insert("remindertbNew", null, cv);
            if (db1 != null && db1.isOpen()) {
                db1.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    public void createCmail(Context context, String phoneno, String status, int flp_status) {
        if(context != null) {
            MySql dbHelper = MySql.getInstance(context);
            SQLiteDatabase db1 = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            if(flp_status == AppConstants.INVALID) {
                cv.put("EVENT_TYPE", "call_invalid");
                cv.put("STATUS", "INVALID");
            } else {
                cv.put("EVENT_TYPE", "call_dnd");
                cv.put("STATUS", status);
            }
            if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                cv.put("FROM1", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
                cv.put("TO1", phoneno);
                cv.put("MSG_RECEPIENT_NO", phoneno);

            }
            cv.put("SUBJECT", "Outgoing Call");
            cv.put("MESSAGE", "Outgoing Call");
            cv.put("UPLOAD_STATUS", 1);
            cv.put("EMAIL", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, null));
            cv.put("PARENT", 0);
            cv.put("STARTTIME", CommonUtils.getTimeFormatInISO(new Date()));
            cv.put("ENDTIME", CommonUtils.getTimeFormatInISO(new Date()));
            cv.put("UNREAD", "false");
            String callerName = CommonUtils.getContactName(context, phoneno);
            cv.put("CALLER", callerName);
            db1.insert("mytbl", null, cv);
            if (db1 != null && db1.isOpen()) {
                db1.close();
            }
            if (dbHelper != null) {
                dbHelper.close();
            }
        }
    }

    private String checkSalesList() {
        String user_id = SmarterSMBApplication.SmartUser.getId();
        SalesStageInfo salesStageInfo = SmarterSMBApplication.salesStageInfo;
        if (salesStageInfo != null) {
            ArrayList<String> arrayList1 = salesStageInfo.getAppointmentSalesStage();
            if (arrayList1 == null ) {
                callSalesStage(user_id);
            } else if(arrayList1.size() <= 0) {
                callSalesStage(user_id);
            }else {
                stage1 = arrayList1.get(0);
                return stage1;
            }
        }

        return stage1;
    }

    private void callSalesStage(String user_id) {
        if (CommonUtils.isNetworkAvailable(this)) {
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
    }

    private void updateSalesStage(String customerNumber) {

        GetCalendarEntryInfo getCalendarEntryInfo = new GetCalendarEntryInfo();
        getCalendarEntryInfo.caller_number = customerNumber;
        getCalendarEntryInfo.status = "OTHERS";
        getCalendarEntryInfo.subStatus1 = "INVALID";

        String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        getCalendarEntryInfo.user_id = userId;

        String leadSource = ApplicationSettings.getPref(AFTER_CALL_LEAD, "");
        if(leadSource == null || leadSource.isEmpty()) {
            leadSource = getLeadSource(customerNumber);
        }
        getCalendarEntryInfo.lead_source = leadSource;

        String customerToContact = ApplicationSettings.getPref(CUSTOMER_TO_CONTACT, "");
        if(customerToContact == null || customerToContact.isEmpty()) {
            customerToContact = getCustomerToContact(customerNumber);
        }
        getCalendarEntryInfo.customer_id = customerToContact;

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            new APIProvider.Update_SalesStatus(getCalendarEntryInfo, 0, null, null, new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
                        //Log.d("AutodillerFragment", "Make call Response " + data);
                    }
                }
            }).call();
        }
    }

    public static void storeUuidHash(String custNo, String uuidval) {
        HashMap<String, String> testHashMap = new HashMap<String, String>();
        testHashMap.put("customer", custNo);
        testHashMap.put("uuid",uuidval);

        Gson gson = new Gson();
        String hashMapString = gson.toJson(testHashMap);

        //Log.d("Bank1AfterCallActivity", "storeUuidHash:" + custNo+" UUID: "+uuidval);

        ApplicationSettings.putPref(AppConstants.CUSTOMER_UUID_MAP, hashMapString);
    }

    private String getLeadSource(String number) {
        String leadSource = "";
        try {
            MySql dbHelper = MySql.getInstance(getApplicationContext());
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if(number != null && !number.isEmpty()) {
                String selection = "TO1=" + "'" + number + "'";
                Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
                }
                if (dbase != null && dbase.isOpen()) {
                    dbase.close();
                }
                if (dbHelper != null) {
                    dbHelper.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leadSource;
    }

    private String getCustomerToContact(String number) {
        String customerId = "";
        try {
            MySql dbHelper = MySql.getInstance(getApplicationContext());
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            if(number != null && !number.isEmpty()) {
                String selection = "TO1=" + "'" + number + "'";
                Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    customerId = cursor.getString(cursor.getColumnIndex("CUSTOMER_ID"));
                }
                if (dbase != null && dbase.isOpen()) {
                    dbase.close();
                }
                if (dbHelper != null) {
                    dbHelper.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerId;
    }

    public static boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db.isOpen()) {
            db.close();
        }
    }
}
