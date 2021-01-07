package smarter.uearn.money.adapters;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Filter;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.DashboardAgent;
import smarter.uearn.money.activities.UearnActivity;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.models.ContactedInfo;
import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.views.events.HeadPhoneReciever;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UearnContactedAdapter extends BaseAdapter {
    private List<ContactedInfo> contactedInfoList;
    private Context context1;
    private HeadPhoneReciever myReceiver;
    AlertDialog alertDialog;
    private List<CustomerLite> customerList = null;
    private List<CustomerLite> tempcustomerList = null;
    public static boolean callFromDoneList = false;
    String screen = "";
    long doneId = 0l;
    private String doneStatus = "",
            doneSubstatus1 = "",
            doneSubstatus2 = "",
            doneNotes = "";

    public UearnContactedAdapter(List<ContactedInfo> customerDetails) {
        this.contactedInfoList = customerDetails;
    }

    @Override
    public int getCount() {
        return contactedInfoList.size();
    }

    @Override
    public ContactedInfo getItem(int position) {
        return contactedInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }


    public void updateListView(List<ContactedInfo> contactedInfoList){
        this.contactedInfoList = contactedInfoList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_list_item, null);
        } else {
            result = convertView;
        }

        context1 = parent.getContext();

        checkHeadPhoneStatus(context1);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        context1.registerReceiver(myReceiver, filter);

        final ContactedInfo contacted = contactedInfoList.get(position);

        final TextView toName = result.findViewById(R.id.phoneNo);
        TextView campaignName = result.findViewById(R.id.campaignName);
        TextView latestSalesStage = result.findViewById(R.id.latestSalesStage);
        TextView flpCount = result.findViewById(R.id.flpcount);
        TextView subject = result.findViewById(R.id.subject);
        TextView subSalesStage = result.findViewById(R.id.subSalesStage);
        toName.setText(contacted.name);

        final long id = Long.valueOf(contacted.id);
        final String toNumber = contacted.number;
        final String name = contacted.name;
        final String status = contacted.stage;
        final String substatus2 = contacted.substage2;

        String leadSource = contacted.lead_source;
        if(leadSource != null && !leadSource.isEmpty() && !leadSource.equals("null")) {
            campaignName.setText(leadSource);
            campaignName.setVisibility(View.VISIBLE);
        } else {
            campaignName.setVisibility(View.GONE);
        }

        latestSalesStage.setText(contacted.stage);

        String substatus1 = contacted.substage1;
        if(substatus1 != null && !substatus1.isEmpty() && !substatus1.equals("null")) {
            subSalesStage.setText(substatus1);
            subSalesStage.setVisibility(View.VISIBLE);
        } else {
            subSalesStage.setVisibility(View.GONE);
        }

        TextView time = result.findViewById(R.id.time);
        Long millis = CommonUtils.stringToMilliSec(contacted.start_time, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String timeString = DateFormat.format("hh:mm a", millis).toString();
        time.setText(timeString);

        TextView actdate = result.findViewById(R.id.actdate);
        Date showDate = new Date(millis);
        String showDateTime = DateFormat.format("dd MMM ", showDate).toString();
        actdate.setText(showDateTime);

        RelativeLayout callrec_layout = result.findViewById(R.id.play_callrec_layout);
        RelativeLayout call_layout = result.findViewById(R.id.call);

        callrec_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parent.getContext() != null) {
                    try {
                        if (CommonUtils.isNetworkAvailable(parent.getContext())) {
                            if (contacted.number != null) {
                                ArrayList<String[]> data = new ArrayList<>();
                                String[] arr = new String[3];
                                arr[0] = contacted.url;
                                arr[1] = "";
                                arr[2] = "";
                                data.add(arr);

                                final RecordingAdapter recordingAdapter = new RecordingAdapter(parent.getContext(), data);
                                final Dialog dailog = new Dialog(parent.getContext(), R.style.Dialog_Theme);
                                dailog.setContentView(R.layout.audio_dailog);
                                dailog.show();
                                final LinearLayoutManager layoutManager = new LinearLayoutManager(parent.getContext());
                                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                RecyclerView recyclerView = dailog.findViewById(R.id.recycleDialog);
                                TextView close = dailog.findViewById(R.id.close);
                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (ApplicationSettings.mediaPlayer != null) {
                                            ApplicationSettings.mediaPlayer.pause();
                                            ApplicationSettings.mediaPlayer.stop();
                                            ApplicationSettings.mediaPlayer.release();
                                            ApplicationSettings.mediaPlayer = null;
                                        }
                                        dailog.dismiss();
                                    }
                                });

                                dailog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        if (ApplicationSettings.mediaPlayer != null) {
                                            ApplicationSettings.mediaPlayer.pause();
                                            ApplicationSettings.mediaPlayer.stop();
                                            ApplicationSettings.mediaPlayer.release();
                                            ApplicationSettings.mediaPlayer = null;
                                        }
                                    }
                                });
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(recordingAdapter);
                            } else {
                                if (parent.getContext() != null) {
                                    Toast.makeText(parent.getContext(), "No Recording found as this was RNR customer.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(parent.getContext(), "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        call_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                  callViaWhatsApp(toNumber);

                if (ApplicationSettings.containsPref(AppConstants.DISALLOW_HEADPHONES)) {
                    boolean disallowHeadphones = ApplicationSettings.getPref(AppConstants.DISALLOW_HEADPHONES, false);
                    if (disallowHeadphones) {

                    } else {
                        if (!SmarterSMBApplication.isHeadPhone) {
                            if (!isBluetoothHeadsetConnected()) {
                                showDialog(context1);
                                return;
                            }
                        }
                    }
                } else {
                    if (!SmarterSMBApplication.isHeadPhone) {
                        if (!isBluetoothHeadsetConnected()) {
                            showDialog(context1);
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

                SmarterSMBApplication.isDiallingFromDoneList = true;
                SmarterSMBApplication.isDiallingFollowUpC2C = true;
                NotificationData.isSocketResponse = false;
                ServiceHandler.callDisconnected = false;
                SmarterSMBApplication.callEndedFromDuringCall = false;
                SmarterSMBApplication.navigatingToUearnHome = false;
                SmarterSMBApplication.actionMoveToNormalProcessed = false;
                navigateToUearnActivity();
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
//                    if (ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
//                        NotificationData.dialledCustomerNumber = toNumber; // Temporary enabled for Black Buck
//                    } else {
//                        NotificationData.dialledCustomerNumber = toNumber;
//                    }
                NotificationData.statusString = status;
                ActivityManager am = (ActivityManager) context1.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    cn = am.getRunningTasks(1).get(0).topActivity;
                }
                if (cn != null && (cn.getClassName().equals("smarter.uearn.money.activities.DashboardAgent") || cn.getClassName().equals("smarter.uearn.money.activities.UearnCompletedActivity"))) {
                    if (substatus1 != null && !substatus1.isEmpty() && substatus1.equalsIgnoreCase("INVALID")) {
                        Toast.makeText(getApplicationContext(), "This number is not valid", Toast.LENGTH_LONG).show();
                        return;
                    } else if (status != null && (status.equals("NEW DATA") || status.equals("NOT INTERESTED") || status.equals("NOT ELIGIBLE") || status.equals("OTHERS") || status.equals("ACTIVATED") || status.equals("ASSIGNTOL2") || status.equals("INTERESTED CALLBACK"))) {
                        callFromDoneList = true;
                        SmarterSMBApplication.lastConnectedCustomerNumber = ""; //For click to call we are clearing lastConnectedCustomerNumber to present during call screen.
                    }
                    DashboardAgent.dialingFromList = true;
                }
                if (context1 != null) {
                    if (CommonUtils.isNetworkAvailable(context1)) {
                        if (toNumber != null) {
                            CommonUtils.createSbiMakeCall(context1, toNumber, id, "", status, substatus1, substatus2, name, "");
                        }
                    } else {
                        Toast.makeText(context1, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return result;
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
        Intent intent = new Intent(context1, UearnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("showinprogress", 1);
        context1.startActivity(intent);
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    private void getSelectedCustomer(String number) {

        try {
            MySql dbHelper = MySql.getInstance(context1);
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
                    NotificationData.statusString = status;
                    String customkvs = cursor.getString(cursor.getColumnIndex("CUSTOMKVS"));
                    CustomerLite customerLiteInfo = new CustomerLite(itemId, customerName, customerNumber, status, substatus1, substatus2, "","",notes, "", customkvs);
                    customerList.add(customerLiteInfo);
                }
            }
            CustomerLite customerLiteInfo = customerList.get(0);
            tempcustomerList.add(0, customerLiteInfo);
            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER, "");
            ApplicationSettings.putPref(AppConstants.CONNECTED_CUSTOMER_NAME, "");
            String customerListData = new Gson().toJson(tempcustomerList);
            ApplicationSettings.putPref(AppConstants.CUSTOMER_LIST_DATA, customerListData);
            System.out.print(customerListData);

            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
