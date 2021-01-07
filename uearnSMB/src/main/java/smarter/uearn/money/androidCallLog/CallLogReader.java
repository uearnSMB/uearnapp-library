package smarter.uearn.money.androidCallLog;

/**
 * Created by Navine on 12/4/2015.
 */


import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import java.util.Date;

public class CallLogReader {
    //private final String TAG = CallLogReader.class.getSimpleName();
    private final String TAG = "ServiceHandler";

    private String[] queryFields;
    private String callTypeFilter;
    private String sortBy;
    private Cursor mCursor;
    private Context appContext;

    public CallLogReader(Context context) {
        queryFields = null;
        callTypeFilter = null;
        sortBy = CallLog.Calls.DATE + " desc";
        appContext = context;
    }

    private int getSimIdInfo() {
        mCursor.moveToFirst();
        for (String s : new String[]{"phone_id", "simnum", "sim_id", "simid", "sub_id", "subid", "subscription_id", "subscription"}) {
            int id = mCursor.getColumnIndex(s);
            if (id >= 0) {
                if (s.equals("phone_id") || s.equals("sim_id")) {

                    // For API : 16 (4.1.2) - There is no SIM info which is a chanllenge
                    // Exception handled when the sim info is null or not available (-1) is returned


                    // phone_id / sim_id are coming  as 0 / 1 instead of 1/2 : Assumption is phone_id will work only for 0/1 and not 1/2 unlike other cases
                    // Why 0/1 are converted to 1/2 is 0->for perferred sim option : 0-> No prefrenece set, 1->  Sim Slot 1, 2 - Sim Slot 2

                    int simInfo = 0;
                    try {
                        simInfo = Integer.valueOf(mCursor.getString(id));

                    } catch (NumberFormatException e) {
                        //Log.e(TAG, "Unable to retrieve the sim info from cursor log or the info may be null");
                        e.printStackTrace();
                        continue;
                    }
                    if (simInfo == 0 || simInfo == 1) {
                        //Log.d(TAG, "SIM id column found / assumed as (0/1) phone_id/sim_id -> " + s + ": " + simInfo);
                        return simInfo + 1;
                    }

                }


               // Log.d(TAG, "SIM id column found:" + s);
               // Log.d(TAG, "SIM id value :" + id);


                try {
                    return Integer.valueOf(mCursor.getString(id));
                } catch (Exception e) {
                    //Log.e(TAG, "Unable to retrieve the sim info from cursor log or the info may be null");
                    e.printStackTrace();
                    continue;
                }


            }
        }
        //Log.d(TAG, "No SIM ID column found");
        return -1;
    }

    public String getCallNumber() {
        return mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.NUMBER));
    }

    public String getCallType() {
        //Log.i("CallLogReader","Mcursor is "+mCursor);
        int typeCode = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.TYPE)));
        //Log.i("CallLogReader","Call type code is "+typeCode);
        String callType = "";
        switch (typeCode) {
            case CallLog.Calls.OUTGOING_TYPE:
                callType = "OUTGOING";
                break;

            case CallLog.Calls.INCOMING_TYPE:
                callType = "INCOMING";
                break;

            case CallLog.Calls.MISSED_TYPE:
                callType = "MISSED";
                break;
            case 5:
                callType = "REJECTED";
                break;

        }

        return callType;
    }

    public String getCallDateStr() {
        return mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.DATE));
    }

    public Date getCallDate() {
        String dateStr = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.DATE));
        return new Date(Long.valueOf(dateStr));
    }

    public int getCallDuration() {
        return Integer.valueOf(mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.DURATION)));
    }

    public int getCallSIM() {
        return getSimIdInfo();
    }
}