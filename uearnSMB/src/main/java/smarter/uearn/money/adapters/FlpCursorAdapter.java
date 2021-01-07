package smarter.uearn.money.adapters;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
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
import smarter.uearn.money.activities.DashboardAgent;
import smarter.uearn.money.activities.UearnActivity;
import smarter.uearn.money.activities.UearnCompletedActivity;
import smarter.uearn.money.activities.UearnFollowupActivity;
import smarter.uearn.money.callrecord.ServiceHandler;
import smarter.uearn.money.models.CustomerLite;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.views.events.HeadPhoneReciever;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FlpCursorAdapter extends CursorAdapter {

    private final Context context1;
    private int operationMode;
    private int currentFlags;
    private Cursor cursor;
    private LayoutInflater inflater;
    private HeadPhoneReciever myReceiver;
    AlertDialog alertDialog;

    View view;
    String screen = "";
    long doneId = 0l;
    private String doneStatus = "",
            doneSubstatus1 = "",
            doneSubstatus2 = "",
            doneNotes = "";

    private List<CustomerLite> customerList = null;
    private List<CustomerLite> tempcustomerList = null;
    private String company = "";
    public static boolean callFromDoneList = false;

    public FlpCursorAdapter(Context context, Cursor c, int flags,int operationMode) {
        super(context, c, flags);
        this.context1 = context;
        this.cursor = c;
        this.currentFlags = flags;
        this.operationMode = operationMode;
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.auto1_followup_layout, viewGroup, false);

        checkHeadPhoneStatus(context);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(myReceiver, filter);

        if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
            screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
        }

        if(ApplicationSettings.containsPref(AppConstants.USERINFO_COMPANY)) {
            company = ApplicationSettings.getPref(AppConstants.USERINFO_COMPANY, "");
        }

        bindView(view, context, cursor);
        return view;
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
        TextView time = view.findViewById(R.id.time);
        TextView actdate = view.findViewById(R.id.actdate);
        final TextView toName = view.findViewById(R.id.phoneNo);
        TextView campaignName = view.findViewById(R.id.campaignName);
        TextView latestSalesStage = view.findViewById(R.id.latestSalesStage);
        TextView flpCount = view.findViewById(R.id.flpcount);
        TextView subject = view.findViewById(R.id.subject);
        TextView subSalesStage = view.findViewById(R.id.subSalesStage);

        final ImageView audio_icon = view.findViewById(R.id.audio_icon);
        ImageView call = view.findViewById(R.id.call_action);
        LinearLayout play_layout = view.findViewById(R.id.play_layout);
        RelativeLayout callrec_layout = view.findViewById(R.id.callrec_layout);
        RelativeLayout call_layout = view.findViewById(R.id.call);
        RelativeLayout flpLayout = view.findViewById(R.id.flp_layout);
        play_layout.setVisibility(View.VISIBLE);

        // subSalesStage.setVisibility(View.VISIBLE);

        String fieldsToShow = ApplicationSettings.getPref(AppConstants.FIELDS_TO_SHOW, "");
        if (fieldsToShow != null && !fieldsToShow.isEmpty()) {
            try {
                JSONObject jsonobject = new JSONObject(fieldsToShow);
                if (jsonobject.has("FlpCursorAdapter")) {
                    String value = jsonobject.getString("FlpCursorAdapter");
                    List<String> items = Arrays.asList(value.split("\\s*,\\s*"));
                    if (items != null && items.size() > 0) {
                        for (int i = 0; i < items.size(); i++) {
                            String viewName = items.get(i);
                            viewName = viewName.replaceAll("[^A-Za-z0-9]", "");
                            int resID = context1.getResources().getIdentifier(viewName, "id", context1.getPackageName());

                            switch (resID) {
                                case R.id.latestSalesStage:
                                    latestSalesStage.setVisibility(View.GONE);
                                    break;
                                case R.id.subSalesStage:
                                    //subSalesStage.setVisibility(View.INVISIBLE);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (cursor != null && cursor.getCount() > 0) {
            final long id = cursor.getLong(cursor.getColumnIndex("_id"));
            final String toname = cursor.getString(cursor.getColumnIndex("TONAME"));
            final String toNumber = cursor.getString(cursor.getColumnIndex("TO1"));
            final String notes = cursor.getString(cursor.getColumnIndex("NOTES"));
            String flp_count1 = cursor.getString(cursor.getColumnIndex("FLP_COUNT"));

            String leadSource = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));
            final String status = cursor.getString(cursor.getColumnIndex("STATUS"));
            Long event_start_date = cursor.getLong(cursor.getColumnIndex("START_TIME"));
            Long event_updated_date = cursor.getLong(cursor.getColumnIndex("UPDATED_AT"));
            final String substatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
            final String substatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
            final String appointmentId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
            final int completed = cursor.getInt(cursor.getColumnIndex("COMPLETED"));
            final String flpSubject = cursor.getString(cursor.getColumnIndex("SUBJECT"));

            Log.d("AppointmentId", "FLPCursor id:----" + appointmentId);
            Log.d("AppointmentId", "FLPCursor appointmentId 111111:----" + appointmentId);


            Log.d("AppointmentId", "FLPCursor:-----" + appointmentId);

            //Log.d("FlpCursorAdapter","ID: "+id+" toNumber: "+ toNumber+" toname: " +toname +" FlpCount:"+flp_count1);
            if (completed == 1) {
                flpLayout.setVisibility(View.VISIBLE);
            } else {
                flpLayout.setVisibility(View.GONE);
            }

            if (flp_count1 != null && !flp_count1.isEmpty()) {
                try {
                    Integer flp = Integer.parseInt(flp_count1);
                    if (flp == 100 || (flp == 99)) {
                        audio_icon.setVisibility(View.INVISIBLE);
                    } else if (flp == 0) {
                        audio_icon.setVisibility(View.INVISIBLE);
                    } else {
                        play_layout.setVisibility(View.VISIBLE);
                        audio_icon.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            final String status_tocall, appoiontment_id_tocall, substatus1_tocall, substatus2_tocall, toname_tocall, notes_tocall;
            if (status == null) {
                status_tocall = "";
            } else {
                status_tocall = status;
            }

            if (leadSource != null && !leadSource.isEmpty()) {
                campaignName.setText(leadSource);
                campaignName.setVisibility(View.VISIBLE);
            } else {
                campaignName.setText("");
                campaignName.setVisibility(View.GONE);
            }

            String questionsAct = ApplicationSettings.getPref(AppConstants.QUESTIONS_ACT, "");


            if (appointmentId == null) {
                appoiontment_id_tocall = "";
            } else {
                appoiontment_id_tocall = appointmentId;
            }

            if (substatus1 == null) {
                substatus1_tocall = "";
            } else if (substatus1 != null && !(substatus1.equalsIgnoreCase("null"))) {
                substatus1_tocall = substatus1;
            } else {
                substatus1_tocall = "";
            }
            if (substatus2 == null) {
                substatus2_tocall = "";
            } else if (substatus2 != null && !(substatus2.equalsIgnoreCase("null"))) {
                substatus2_tocall = substatus2;
            } else {
                substatus2_tocall = "";
            }

            if (toName == null) {
                toname_tocall = "";
            } else {
                toname_tocall = toname;
            }

            if (notes == null) {
                notes_tocall = "";
            } else {
                notes_tocall = notes;
            }

            if (substatus1 != null && !substatus1.isEmpty()){
                subSalesStage.setVisibility(View.VISIBLE);
                subSalesStage.setText(substatus1);
            } else {
                subSalesStage.setText("");
            }

            if (flpSubject != null && !flpSubject.isEmpty() && !flpSubject.equalsIgnoreCase("CALLBACK CUSTOMER")) {
                subject.setVisibility(View.VISIBLE);
                subject.setText(flpSubject);
            } else {
                subject.setVisibility(View.GONE);
            }


            if (toname != null && !toname.isEmpty()) {
                toName.setText(toname);
            } else {
                toName.setText("No Name");
            }

            String customerName = toName.getText().toString();
            if ((customerName == null || customerName.isEmpty())) {
                if (toNumber != null && !toNumber.equals("")) {
                    toName.setText(toNumber);
                }
            }

            if (status != null && !status.isEmpty()) {
                latestSalesStage.setText(status);
            } else {
                latestSalesStage.setText("");
            }

            final long current = System.currentTimeMillis();
            long timeDiff = 0L;

            Long dateToUse;
            if (operationMode == 0) {
                /*For SMB-HOME activity, we should use only start date for the view*/
                dateToUse = event_start_date;
            } else {
                dateToUse = event_updated_date;
            }

            if (dateToUse > current) {
                timeDiff = dateToUse - current;
            } else {
                timeDiff = 0l;
            }

            String timeString = DateFormat.format("hh:mm a", dateToUse).toString();

            Date showDate = new Date(dateToUse);
            String showDateTime = DateFormat.format("dd MMM ", showDate).toString();
            long mins = timeDiff / (1000 * 60);
            time.setVisibility(View.VISIBLE);

            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
            Long start = c.getTimeInMillis();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
            Long end = c.getTimeInMillis();

            if ((start < dateToUse) && (end > dateToUse)) {
                time.setText(timeString);
            } else {
                time.setText(timeString);
            }
            actdate.setText(showDateTime);

            if (completed == 1) {
                if (toNumber != null && !toNumber.isEmpty()) {
                    Integer flp = getFlpCount(toNumber);
                    if (flp == 100 || (flp == 99)) {
                        flpCount.setText(1 + "");
                        audio_icon.setVisibility(View.INVISIBLE);
                    } else {
                        flpCount.setText(flp + "");
                        play_layout.setVisibility(View.VISIBLE);
                        audio_icon.setVisibility(View.VISIBLE);
                    }
                } else {
                    flpCount.setText("1");
                    time.setText(timeString);
                }
            }

            callrec_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context1 != null) {
                        try {
                            if (CommonUtils.isNetworkAvailable(context1)) {
                                if (toNumber != null) {
                                    CommonUtils.showCallRecordings(context1, toNumber);
                                } else {
                                    if (context1 != null) {
                                        Toast.makeText(context1, "No Recording found as this was RNR customer.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(context1, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
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
                    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
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
                                if (toNumber != null && id != 0) {
                                    if (context1 != null) {
                                        CommonUtils.setToast(context1, "Dialling ...");
                                        if (screen != null && (screen.equalsIgnoreCase("Auto1AfterCallActivity")) || (screen.equalsIgnoreCase("Auto2AfterCallActivity"))) {
                                            CommonUtils.createMakeCall(context1, toNumber, id, appoiontment_id_tocall, status_tocall, substatus1_tocall, substatus2_tocall, toname_tocall, notes_tocall);
                                        } else if (screen != null && (screen.equalsIgnoreCase("Bank1AfterCallActivity") || screen.equalsIgnoreCase("UearnActivity"))) {
                                            if ((completed != 1)) {
                                                CommonUtils.createSbiMakeCall(context1, toNumber, id, appoiontment_id_tocall, status_tocall, substatus1_tocall, substatus2_tocall, toname_tocall, notes_tocall);
                                            } else {
                                                getLatestRecord(toNumber);
                                                if (doneStatus == null) {
                                                    doneStatus = "";
                                                }
                                                if (doneSubstatus1 == null) {
                                                    doneSubstatus1 = "";
                                                }
                                                if (doneSubstatus2 == null) {
                                                    doneSubstatus2 = "";
                                                }
                                                if (doneNotes == null) {
                                                    doneNotes = "";
                                                }
                                                Log.d("Appointment Id", "FLPCursor appointmentid:----" + appointmentId);
                                                CommonUtils.createSbiMakeCall(context1, toNumber, doneId, "", doneStatus, doneSubstatus1, doneSubstatus2, toname_tocall, doneNotes);
                                            }
                                        } else {
                                            Log.d("Appointment Id", "FLPCursor appointmentid:----" + appointmentId);
                                            callToCustomer(context1, toNumber, appointmentId, status, substatus1, substatus2, toname_tocall, notes, id);
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context1, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
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

//    private void callViaWhatsApp(String phoneNumber) {
//        String callingId = "";
//        String mimeType = "";
//        if(phoneNumber != null && phoneNumber.length() > 0) {
//            phoneNumber = phoneNumber.substring(1, phoneNumber.length());
//        }
//        Cursor cursor = getContactIdByNumber(phoneNumber);
//        while (cursor.moveToNext()) {
//            callingId = cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID));
//            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
//            mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
//            String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//            Log.d("WData", callingId+ " "+ displayName + " " + mimeType + " " + phoneNo);
//        }
//        if(callingId != null && !callingId.isEmpty()) {
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse("content://com.android.contacts/data/" + callingId), mimeType);
//            intent.setPackage("com.whatsapp");
//            context1.startActivity(intent);
//        } else {
//            Intent sendIntent = new Intent("android.intent.action.MAIN");
//            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//            String waNumber = phoneNumber.replace("+", "");
//            sendIntent.putExtra("jid", waNumber + "@s.whatsapp.net");
//            context1.startActivity(sendIntent);
//        }
//    }
//
//    public Cursor getContactIdByNumber(String number) {
//        {
//            ContentResolver cr = context1.getContentResolver();
//            try {
//                Uri uri = ContactsContract.Data.CONTENT_URI;
//                String where = ContactsContract.CommonDataKinds.Phone.NUMBER + " IN (?)" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "='vnd.android.cursor.item/vnd.com.whatsapp.voip.call'";
//                String[] selectionArgs = {number + "@s.whatsapp.net"};
//                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;
//                return cr.query(uri, null, where, selectionArgs, sortOrder);
//            } catch (Exception ex) {
//                String message = ex.getMessage();
//                Log.e("mine", "Error: " + message, ex);
//                return null;
//            }
//        }
//    }

    private void getLatestRecord(String num) {

        if (context1 != null) {
            MySql dbHelper = MySql.getInstance(context1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String selection = "TO1 = '" + num + "' ";
            Cursor cursor = db.query(true, "remindertbNew", null, selection, null, "TO1", null, "START_TIME DESC", null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                doneId = cursor.getLong(cursor.getColumnIndex("_id"));
                doneStatus = cursor.getString(cursor.getColumnIndex("STATUS"));
                doneSubstatus1 = cursor.getString(cursor.getColumnIndex("SUBSTATUS1"));
                doneSubstatus2 = cursor.getString(cursor.getColumnIndex("SUBSTATUS2"));
                doneNotes = cursor.getString(cursor.getColumnIndex("NOTES"));
                cursor.close();
            } else {
                doneId = 0;
                doneStatus = "";
                doneSubstatus1 = "";
                doneSubstatus2 = "";
                doneNotes = "";
            }
            if (db.isOpen()) {
                db.close();
            }
        }
    }

    public static void callToCustomer(Context context, String number, String appointmentId, String status, String substatus1, String substatus2, String callername, String notes, Long dbId) {
        if (context != null) {
            NotificationData.appointment_db_id = dbId;
            Intent intent = new Intent(Intent.ACTION_CALL);
            if (number != null) {
                String num = "tel:" + number;
                intent.setData(Uri.parse(num));
            }
            setCallData(appointmentId, status, substatus1, substatus2, callername, notes, dbId);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(intent);
        }
    }

    public static void setCallData(String appointmentId, String status, String substatus1, String substatus2, String callername, String notes, Long dbid) {
        NotificationData.notificationData = true;
        NotificationData.appointment_id = appointmentId;
        NotificationData.statusString = status;
        NotificationData.notes_string = notes;
        NotificationData.order_value = "0";
        NotificationData.isAppointment = true;
        NotificationData.appointment_db_id = dbid;
        if (callername != null) {
            NotificationData.knolarity_name = "";
        }
        if (substatus1 != null && status != null && !status.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus1 = substatus1;
        } else {
            NotificationData.substatus1 = "";
        }
        if (substatus2 != null && status != null && !status.equalsIgnoreCase("NEW DATA")) {
            NotificationData.substatus2 = substatus2;
        } else {
            NotificationData.substatus2 = "";
        }
    }

    private int  getFlpCount(String number) {
        int count = 1;
        try {

            MySql dbHelper = MySql.getInstance(context1);
            SQLiteDatabase dbase = dbHelper.getWritableDatabase();
            String selection = "";

            if(currentFlags == 1) {
                selection = "UPDATED_AT" + ">=" + UearnCompletedActivity.start + " AND " + "UPDATED_AT" + "<=" + UearnCompletedActivity.end + " AND " + "TO1=" + "'" + number + "'" + " AND " + "COMPLETED" + "=" + 1 + " AND (ASSIGN_TO IS NULL OR ASSIGN_TO = '')";
            } else {
                selection = "UPDATED_AT" + ">=" + DashboardAgent.start + " AND " + "UPDATED_AT" + "<=" + DashboardAgent.end + " AND " + "TO1=" + "'" + number + "'" + " AND " + "COMPLETED" + "=" + 1 + " AND (ASSIGN_TO IS NULL OR ASSIGN_TO = '')";
            }

            String doneListQuery = ApplicationSettings.getPref(AppConstants.DONE_LIST_QUERY, "");
            if(doneListQuery != null && !doneListQuery.isEmpty()) {
                selection = selection + doneListQuery;
            }

            Log.d("FlpCursor", "done_start"+DashboardAgent.start);
            Log.d("FlpCursor", "done_end"+DashboardAgent.end);
            Cursor numberForm = dbase.query("remindertbNew", null, selection, null, null, null, null);
            if (numberForm != null && numberForm.getCount() > 0) {
                count = numberForm.getCount();
                numberForm.close();
            }
            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
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
