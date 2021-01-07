package smarter.uearn.money.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.models.SalesStageInfo;
import smarter.uearn.money.services.AcceptOrRejectNotificationService;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.KnowlarityModel;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static smarter.uearn.money.utils.ServerAPIConnectors.Urls.getSioAddress;

/**
 * Created by Srinath on 08-01-2018.
 */

public class DataLoadingFragment extends Fragment {

    private TextView percentageTv;
    private ProgressBar home_progress;
    private static  String EXTRA_BOOTABLE = "";
    private static  String EXTRA_DB_PATH = "";
    private int percentage = 48;
    private Socket mSocket;
    String id = "";
    static String dbPath = "";
    private Context context;
    private Activity activity;

    public static int GET_TEAM_MEMBERS = 535;

   /* public static DataLoadingFragment newInstance(String param1, String param2) {
        DataLoadingFragment fragment = new DataLoadingFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_BOOTABLE, param1);
        args.putString(EXTRA_DB_PATH, param2);
        fragment.setArguments(args);
        return fragment;
    }
*/
    private Emitter.Listener onDBFetchAvailable = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] img = (byte[]) args[0];
                                FileOutputStream outs = null;

                                //Log.d("SMB_DEBUG_LOGS","Loading the DB to "+DataLoadingFragment.dbPath);
                                try {
                                    outs = new FileOutputStream(DataLoadingFragment.dbPath);
                                } catch (IOException e) {
                                    //Log.d("SMB_DEBUG_LOGS","Exception "+e.toString());
                                }

                                BufferedOutputStream bos = new BufferedOutputStream(outs);
                                try {
                                    bos.write(img);
                                    bos.flush();
                                    bos.close();

                                    //Log.d("SMB_DEBUG_LOGS","Loaded the DB to "+dbPath);
                                    continueWithOtherDBData();
                                } catch (IOException e) {
                                    //Log.d("SMB_DEBUG_LOGS","Exception "+e.toString());
                                }
                            }
                        });
                    }
                }
            };
            thread.start();
        }
    };

    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -7);
            Date date = cal.getTime();
            String startTime = CommonUtils.getTimeFormatInISO(date);
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, +30);
            Date endDate = cal.getTime();
            String endTime = CommonUtils.getTimeFormatInISO(endDate);
            //Log.d("SMB_DEBUG_LOGS", "Start_time " + startTime);
            //Log.d("SMB_DEBUG_LOGS", "EndTime " + endTime);
            if (mSocket != null && mSocket.connected()) {
                try {
                    if (!id.isEmpty()) {
                        //Log.d("SMB_DEBUG_LOGS", "Requesting DB for " + id);
                        mSocket.emit("getdb", id, startTime, endTime);
                    }
                } catch (Exception e) {

                }
            }
        }
    };

    /*
    * Start fetching settings from backed
    */
    public void continueWithOtherDBData() {
        if (mSocket != null && mSocket.connected()) {
            try {
                mSocket.off("dbfile", onDBFetchAvailable);
                mSocket.off("connect", onConnected);
                mSocket.disconnect();
            } catch (Exception e) {

            }
        }
        //googleAccountsCall(id);
        if(context != null) {
            getTeamMembersCall(id, context);
            CommonUtils.calculateNextAlarm(context);
            if(activity != null) {
                ServiceUserProfile.getUserSettings(activity);
            }

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            String syncDate = "" + df.format(c.getTime());
            if (syncDate != null) {
                ApplicationSettings.putPref(AppConstants.SYNC_FOLLOWUP_DATE, syncDate);
            }
        }

    }


    public void clearId() {
        id = "";
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_data_loading, container, false);
        context = getContext();
        activity = getActivity();
        init(view);
        ServiceUserProfile.getUserSettings(getActivity());
        getStatusFromServer(id);
        return view;
    }

    private void init(View view) {

        percentageTv = view.findViewById(R.id.percentageTv);
        home_progress = view.findViewById(R.id.home_progress);
        Bundle bundle = getArguments();
        if(bundle != null) {
            id = bundle.getString("EXTRA_ID");
            dbPath = bundle.getString("EXTRA_DB_PATH");
            //Log.d("dbPath", dbPath);
        }
        SmarterSMBApplication.appSettings.setLogout(false);
        new SignUpFragment().newUserPreferences();
        ApplicationSettings.putPref(AppConstants.SHOW_POPUP, true);
        ApplicationSettings.putPref(AppConstants.APP_LOGOUT, false);
        ApplicationSettings.putPref(AppConstants.TRANSCRIPTION, false);
        ApplicationSettings.putPref(AppConstants.LEAD_CONTACTS, "" + "");
        if(getContext() != null) {
            CommonOperations.subscribeNotificationsCall(true, getContext());
        }

        /*if (EXTRA_DB_PATH != null) {
            dbPath = EXTRA_DB_PATH;
        }*/
        //TODO:: handle null db_paths
    }

    /**
     * Get user Sales Status list
     *
     * @param id 'Using the user ID fetch the details
     */
    private void getStatusFromServer(final String id) {
        if(context != null) {
            if (CommonUtils.isNetworkAvailable(context)) {
                new APIProvider.Get_Sales_Status(id, 22, new API_Response_Listener<SalesStageInfo>() {
                    @Override
                    public void onComplete(SalesStageInfo data, long request_code, int failure_code) {
                        if (data != null) {
                            data.dosave();
                            SmarterSMBApplication.salesStageInfo = data;
                        }
                        home_progress.setProgress(20);
                        percentageTv.setText("20%");
                        getGroupsOfUsers(id);
                    }
                }).call();
            }
        }
    }

    /**
     * Get User Team List
     *
     * @param user_id 'Fetch the users logged in account details
     */
    private void getGroupsOfUsers(String user_id) {
        if(context != null) {
            if (CommonUtils.isNetworkAvailable(context)) {
                new APIProvider.Get_GroupsUser(user_id, 1, null, null, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                    @Override
                    public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                        if (data != null) {
                            for (int i = 0; i < data.size(); i++) {
                                String request = data.get(i).request;
                                if ((request != null) && request.equals("invited")) {
                                    Intent intent = new Intent(context, AcceptOrRejectNotificationService.class);
                                    Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(data.get(i));
                                    intent.putExtra("groupsUserInfoBundle", bundle);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(intent);
                                    } else {
                                        context.startService(intent);
                                    }
                                }
                            }
                        }
                        home_progress.setProgress(40);
                        percentageTv.setText("40%");
                        knowlarityRegistration();
                    }
                }).call();
            }
        }
    }

    /**
     * Get User Team List
     *
     * @param id, context
     */
    private void getTeamMembersCall(String id, final android.content.Context context) {
        if(context != null) {
            if (CommonUtils.isNetworkAvailable(context)) {
                new APIProvider.Get_All_Team_Members(id, GET_TEAM_MEMBERS, new API_Response_Listener<ArrayList<GroupsUserInfo>>() {
                    @Override
                    public void onComplete(ArrayList<GroupsUserInfo> data, long request_code, int failure_code) {
                        if (data != null) {
                            CommonUtils.saveTeamdataToLocalDB(context, data);
                            for (int i = 0; i < data.size(); i++) {
                                String request = data.get(i).request;
                                if ((request != null) && request.equals("invited")) {
                                    Intent intent = new Intent(context, AcceptOrRejectNotificationService.class);
                                    Bundle bundle = CommonUtils.convertGroupsUserInfoToBundle(data.get(i));
                                    intent.putExtra("groupsUserInfoBundle", bundle);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(intent);
                                    } else {
                                        context.startService(intent);
                                    }
                                }
                            }
                        }

                        Handler handler = new Handler();
                        home_progress.setProgress(100);
                        percentageTv.setText("100%");

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(getActivity() != null) {
                                    getActivity().finish();
                                }
                                ApplicationSettings.putPref(AppConstants.NEW_USER, false);
                                Intent dashboard = new Intent(context, UearnHome.class);
                                startActivity(dashboard.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

                            }
                        }, 1000);
                    }
                }).call();
            }
        }
    }

    private void loadPrivateContacts() {
       /* if(context != null) {
            if (CommonUtils.isNetworkAvailable(context)) {
                new APIProvider.GetPersonalJunkNumberApi("", 1, this, "", new API_Response_Listener<List<PersonalJunkContact>>() {
                    @Override
                    public void onComplete(List<PersonalJunkContact> data, long request_code, int failure_code) {
                        if (data != null) {

                            //Log.d("private contacts", "" + data);
                            for (PersonalJunkContact item : data) {
                                MySql dbHelper = new MySql(activity, "mydb", null, AppConstants.dBversion);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();

                                if (item.getType().equalsIgnoreCase("personal")) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("NUMBER", item.getPhoneNumber());
                                    db.insert("PrivateContacts", null, contentValues);
                                } else {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("NUMBER", item.getPhoneNumber());
                                    db.insert("JunkNumbers", null, contentValues);
                                }

                                db.close();
                            }
                        }
                        home_progress.setProgress(40);
                        percentageTv.setText("40%");
                        knowlarityRegistration();
                    }
                }).call();
            }*/
       /* }*/
    }

    private void knowlarityRegistration() {
        try {
            if(ApplicationSettings.containsPref(AppConstants.CLOUD_OUTGOING) && ApplicationSettings.getPref(AppConstants.CLOUD_OUTGOING, false)) {
                String phone = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                String country_code = "+" + CommonUtils.getCountryCodeNumber();
                String email = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
                String first_name = ApplicationSettings.getPref(AppConstants.USERINFO_NAME, "");
                String last_name = "";
                KnowlarityModel knowlarityModel = new KnowlarityModel(country_code, email, first_name, last_name, phone);
                new APIProvider.KnowlarityRegistration(knowlarityModel, 234, true, new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        if (data != null && !(data.isEmpty())) {
                            //Log.d("KnowlarityRegistration", data);
                        }
                    }
                }).call();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            home_progress.setProgress(75);
            percentageTv.setText("75%");
            fetchDbFromServer();
        }
    }

    private void fetchDbFromServer() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;

            mSocket = IO.socket(getSioAddress(),opts);
            mSocket.on("dbfile",onDBFetchAvailable);
            mSocket.on("connect",onConnected);
            try {
                mSocket.connect();
            } catch (Exception e) {
                Toast.makeText(context, "Please check your internet connection. ", Toast.LENGTH_SHORT).show();
                if(getActivity() != null) {
                    getActivity().finish();
                }
            }
        } catch (java.net.URISyntaxException e) {
            Toast.makeText(context, "DB Not downloaded", Toast.LENGTH_SHORT).show();
            if(getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    private void fragmentTransaction(Fragment fragment) {
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_container, fragment);
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


   /* @Override
    protected void onDestroy() {
        try {
            mSocket.off("dbfile", onDBFetchAvailable);
            mSocket.off("connect", onConnected);
            mSocket.disconnect();
        } catch (Exception e) {

        }

        super.onDestroy();
    }*/



}
