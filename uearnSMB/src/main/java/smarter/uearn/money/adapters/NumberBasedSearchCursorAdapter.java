package smarter.uearn.money.adapters;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.DashboardAgent;
import smarter.uearn.money.activities.UearnActivity;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.models.GetCalendarEntryInfo;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.ReuploadService;
import smarter.uearn.money.views.events.HeadPhoneReciever;

import static com.facebook.FacebookSdk.getApplicationContext;
import static smarter.uearn.money.activities.AfterCallActivity.AFTER_CALL_LEAD;
import static smarter.uearn.money.activities.AfterCallActivity.CUSTOMER_TO_CONTACT;

public class NumberBasedSearchCursorAdapter extends CursorAdapter {

    LayoutInflater inflater;
    Context context;
    Cursor cursor;
    Cursor filterCursor;
    View view;
    String toname = "", stage1 = "", substatus1 = "", substatus2 = "", appointmentId = "", status = "", notes = "", order_value = "";
    Long dbId = 0L;
    private HeadPhoneReciever myReceiver;
    String screen = "";
    AlertDialog alertDialog;

    private List<CustomerLite> customerList = null;
    private List<CustomerLite> tempcustomerList = null;

    public NumberBasedSearchCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c,flags);
        this.context = context;
        this.cursor = c;
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    void checkHeadPhoneStatus(Context context) {
        myReceiver = new HeadPhoneReciever();
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

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final int dbid = cursor.getInt(cursor.getColumnIndex("_id"));
        TextView time = (TextView) view.findViewById(R.id.time);
        final TextView toName = (TextView) view.findViewById(R.id.phoneNo);
        TextView duration = (TextView) view.findViewById(R.id.duration);
        TextView campaignName = (TextView) view.findViewById(R.id.campaignName);
        TextView latestSalesStage = (TextView) view.findViewById(R.id.latestSalesStage);
        TextView flpCount = (TextView) view.findViewById(R.id.flpcount);
        TextView subject = (TextView) view.findViewById(R.id.subject);
        TextView subSalesStage = (TextView) view.findViewById(R.id.subSalesStage);

        final ImageView audio_icon = (ImageView) view.findViewById(R.id.audio_icon);
        ImageView call = (ImageView) view.findViewById(R.id.call_action);
        LinearLayout play_layout = (LinearLayout) view.findViewById(R.id.play_layout);
        RelativeLayout callrec_layout = (RelativeLayout) view.findViewById(R.id.callrec_layout);
        RelativeLayout call_layout = (RelativeLayout) view.findViewById(R.id.call);
        play_layout.setVisibility(View.VISIBLE);
        subject.setVisibility(View.GONE);
        subSalesStage.setVisibility(View.VISIBLE);


        if (cursor != null && cursor.getCount() > 0) {
            final long db_id = cursor.getLong(cursor.getColumnIndex("_id"));
            toname = cursor.getString(cursor.getColumnIndex("TONAME"));
            final String toNumber = cursor.getString(cursor.getColumnIndex("TO1"));
            final String notes = cursor.getString(cursor.getColumnIndex("NOTES"));
            String flp_count1 = cursor.getString(cursor.getColumnIndex("FLP_COUNT"));
            final String status = cursor.getString(cursor.getColumnIndex("STATUS"));
            String leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
            Long event_start_date = cursor.getLong(cursor.getColumnIndex("START_TIME"));
            Long event_end_date = cursor.getLong(cursor.getColumnIndex("END_TIME"));
            String path = cursor.getString(cursor.getColumnIndex("CALLREC_URL"));
            String customer_id = cursor.getString(cursor.getColumnIndex("CUSTOMER_ID"));
            substatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
            substatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
            appointmentId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
            final String order_value1 = cursor.getString(cursor.getColumnIndex("ORDER_POTENTIAL"));

            if (substatus1 != null && !(substatus1.isEmpty())) {
                subSalesStage.setText(substatus1);
            } else {
                subSalesStage.setText("");
            }
            if(toname == null) {
                toname = "";
            }
            if (toname != null && !toname.equals("")) {
                toName.setText(toname);
            } else if (toNumber != null && !toNumber.equals("")) {
                toName.setText(toNumber);
            }
            if (notes != null) {
                subject.setText(notes);
            } else {
                subject.setText("");
            }

            if (status != null && !status.isEmpty()) {
                latestSalesStage.setText(status);
            } else {
                latestSalesStage.setText("");
            }

            if(leadSource != null && !leadSource.isEmpty()) {
                campaignName.setText(leadSource);
            } else {
                campaignName.setText("");
            }

            final long current = System.currentTimeMillis();
            long timeDiff = 0L;
            if (event_start_date > current) {
                timeDiff = event_start_date - current;
            } else {
                timeDiff = 0l;
            }

            String timeString = DateFormat.format("hh:mm a", event_start_date).toString();

            Date showDate = new Date(event_start_date);
            String showDateTime = DateFormat.format("dd MMM ", showDate).toString();
            time.setVisibility(View.VISIBLE);
            play_layout.setVisibility(View.VISIBLE);
            time.setText(showDateTime);

            call_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ApplicationSettings.containsPref(AppConstants.DISALLOW_HEADPHONES)) {
                        boolean disallowHeadphones = ApplicationSettings.getPref(AppConstants.DISALLOW_HEADPHONES, false);
                        if (disallowHeadphones) {

                        } else {
                            if (!SmarterSMBApplication.isHeadPhone) {
                                if (!isBluetoothHeadsetConnected()) {
                                    showDialog(context);
                                    return;
                                }
                            }
                        }
                    } else {
                        if (!SmarterSMBApplication.isHeadPhone) {
                            if (!isBluetoothHeadsetConnected()) {
                                showDialog(context);
                                return;
                            }
                        }
                    }

                    clearFlags();

                    if(SmarterSMBApplication.isRemoteDialledStart || SmarterSMBApplication.currentStateIsStartMode){
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
                    if(SmarterSMBApplication.connectedCustomerInProcess){
                        SmarterSMBApplication.connectedCustomerInProcess = false;
                    }
                    if(SmarterSMBApplication.disconnectedCustomerInProcess){
                        SmarterSMBApplication.disconnectedCustomerInProcess = false;
                    }

                    if (ApplicationSettings.containsPref(AppConstants.ADHOC_CALL)) {
                        boolean adhocCall = ApplicationSettings.getPref(AppConstants.ADHOC_CALL, false);
                        if (adhocCall) {
                            String questionnaire = ApplicationSettings.getPref(AppConstants.QUESTIONNAIRE_FROM_SETTINGS, "");
                            ApplicationSettings.putPref(AppConstants.QUESTIONNAIRE, questionnaire);
                        }
                    }

                    boolean sequencialEndpoint = ApplicationSettings.getPref(AppConstants.C2C_SEQUENCIAL_ENDPOINT, false);
                    if (sequencialEndpoint) {
                        getSelectedCustomer(toNumber);
                    }
                    NotificationData.substatus1 = "";
                    NotificationData.substatus2 = "";
                    NotificationData.remarks = "";
                    NotificationData.statusString = status;
                    if (context != null) {
                        if (CommonUtils.isNetworkAvailable(context)) {
                            if (toNumber != null && db_id != 0) {
                                if (context != null) {
                                    CommonUtils.setToast(context, "Dialling ...");
                                    createSbiMakeCall(context, toNumber, db_id, appointmentId, status, substatus1, substatus2, toname, notes);
                                }
                            }
                        } else {
                            Toast.makeText(context, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
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
        Intent intent = new Intent(context, UearnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("showinprogress", 1);
        context.startActivity(intent);
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    private void getSelectedCustomer(String number) {

        try {
            MySql dbHelper = new MySql(context, "mydb", null, AppConstants.dBversion);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();

            String selection = "TO1=" + "'" + number + "'";
            Cursor cursor = dbase.query("remindertbNew", null, selection, null, null, null, "UPDATED_AT DESC");

            customerList = new ArrayList<>();
            tempcustomerList = new ArrayList<>();
            if(cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
                    String customerName = cursor.getString(cursor.getColumnIndex("TONAME"));
                    String customerNumber = cursor.getString(cursor.getColumnIndex("TO1"));
                    String status = cursor.getString(cursor.getColumnIndex("STATUS"));
                    String substatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
                    String substatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
                    String notes = cursor.getString(cursor.getColumnIndex("NOTES"));
                    String customkvs = cursor.getString(cursor.getColumnIndex("CUSTOMKVS"));
                    String leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
                    String referredBy = cursor.getString(cursor.getColumnIndex("REFERRED_BY"));
                    NotificationData.statusString = status;
                    CustomerLite customerLiteInfo = new CustomerLite(itemId, customerName, customerNumber, status, substatus1, substatus2, leadSource, referredBy, notes, "", customkvs);
                    customerList.add(customerLiteInfo);
                }
            }
            CustomerLite customerLiteInfo = customerList.get(0);
            tempcustomerList.add(0, customerLiteInfo);
            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
            String customerListData = new Gson().toJson(tempcustomerList);
            ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData.toString());
            //System.out.print(customerListData.toString());

            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.auto1_followup_layout, viewGroup, false);
        this.cursor = cursor;
        checkHeadPhoneStatus(context);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(myReceiver, filter);
        if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
            screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
        }
        bindView(view, context, cursor);
        return view;
    }

    private String checkSalesList() {
        String user_id = SmarterSMBApplication.SmartUser.getId();
        SalesStageInfo salesStageInfo = SmarterSMBApplication.salesStageInfo;
        if (salesStageInfo != null) {
            ArrayList<String> arrayList1 = salesStageInfo.getAppointmentSalesStage();
            if (arrayList1 == null && arrayList1.size() > 0) {
                if (CommonUtils.isNetworkAvailable(context)) {
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
                                                sbiCall(knowlarityModel, customernumber, status, context, substatus1, substatus2, callername, notes, appointmentId, dbid);
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

    public void sbiCall(KnowlarityModel knowlarityModel, final String customernumber, final String status, final Context context, final String substatus1, final String substatus2, final String callername, final String notes, final String appointmentId, final Long dbid) {
        NotificationData.knolarity_start_time = new Date().toString();
        new APIProvider.ReClickToCall(knowlarityModel, 213, true, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                NotificationData.knolarity_response_time = new Date().toString();
                if (data != null && !(data.isEmpty())) {
                    if (data.contains("_SMBALERT_")) {
                        CommonUtils.setToast(getApplicationContext(),data.replace("_SMBALERT_", ""));
                        navigateToUearnActivity();
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

    private void callSalesStage(String user_id) {
        if (CommonUtils.isNetworkAvailable(context)) {
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
}
