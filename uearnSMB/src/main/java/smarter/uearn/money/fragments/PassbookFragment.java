package smarter.uearn.money.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.adapters.UearnPassbookAdapter;
import smarter.uearn.money.models.UearnPassbookInfo;
import smarter.uearn.money.models.UearnPassbookSummaryInfo;
import smarter.uearn.money.models.UearnRangePassbookInfo;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.UserActivities;

public class PassbookFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private UearnPassbookInfo passbookInfo;
    private LinearLayout summaryLayout, noDataLayout;
    private TextView openingBalance, totalPaid, uearned, bonus, totalpaymentDone, closingBalance;


    private RecyclerView passbookRecyclerView;
    private UearnPassbookAdapter adapter;
    private List<UearnRangePassbookInfo> passbookRangeInfoList;
    private UearnPassbookSummaryInfo summaryInfo;

    public PassbookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.passbook_followup_layout, container, false);
        initializeUiElements(view);
        return view;
    }

    private void initializeUiElements(View view) {
        summaryLayout = view.findViewById(R.id.summaryLayout);
        openingBalance = view.findViewById(R.id.openingBalance);
        totalPaid = view.findViewById(R.id.totalPaid);
        uearned = view.findViewById(R.id.uearned);
        bonus = view.findViewById(R.id.bonus);
        totalpaymentDone = view.findViewById(R.id.totalpaymentDone);
        closingBalance = view.findViewById(R.id.closingBalance);
        passbookRecyclerView = view.findViewById(R.id.passbookRV);
        passbookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        passbookInfo = (UearnPassbookInfo) getArguments().getSerializable("pass_info");
        noDataLayout = view.findViewById(R.id.noDataLayout);
        noDataLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passbookInfo = (UearnPassbookInfo) getArguments().getSerializable("pass_info");
        getUearnDayPassbook(passbookInfo);
    }

    private void getUearnDayPassbook(UearnPassbookInfo passInfo) {
        passbookRangeInfoList = new ArrayList<>();
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            String userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");

            UserActivities userActivities = new UserActivities(userId, passInfo.start_date, passInfo.end_date);
            new APIProvider.GetUearnwdDayWisePassbook(userActivities, 0, getActivity(), "Processing your request.", new API_Response_Listener<String>() {
                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null && !data.isEmpty()) {
                        JSONObject jsonObject = null;
                        JSONArray monthArray = null;
                        JSONObject monthObj = null;
                        try {
                            jsonObject = new JSONObject(data);
                            if (jsonObject.has("success")) {
                                ApplicationSettings.putPref(AppConstants.UEARN_PASSBOOK, "");
                                ApplicationSettings.putPref(AppConstants.UEARN_PASSBOOK, data);
                                monthArray = jsonObject.getJSONArray("success");
                                for (int i = 0; i < monthArray.length(); i++) {
                                    monthObj = monthArray.getJSONObject(i);
                                    UearnRangePassbookInfo passbookInfoObj = new UearnRangePassbookInfo();
                                    passbookInfoObj.day = monthObj.optString("day");
                                    passbookInfoObj.brand = monthObj.optString("brand");
                                    passbookInfoObj.img_url = monthObj.optString("img_url");
                                    passbookInfoObj.name = monthObj.optString("name");
                                    passbookInfoObj.description = monthObj.optString("description");
                                    passbookInfoObj.uearned = monthObj.optString("uearned");
                                    passbookInfoObj.sign = monthObj.optString("sign");
                                    passbookInfoObj.closing_balance = monthObj.optString("closing_balance");
                                    passbookRangeInfoList.add(passbookInfoObj);
                                }
                                showPassBookList(passbookRangeInfoList);
                            }

                            if (jsonObject.has("summary")) {
                                JSONObject summaryObj = jsonObject.getJSONObject("summary");
                                summaryInfo = new UearnPassbookSummaryInfo();
                                summaryInfo.earnings = summaryObj.optString("earnings");
                                summaryInfo.opening_balance = summaryObj.optString("opening_balance");
                                summaryInfo.paid_amount = summaryObj.optString("paid_amount");
                                summaryInfo.total_balance = summaryObj.optString("total_balance");
                                summaryInfo.closing_balance = summaryObj.optString("closing_balance");
                                summaryInfo.bonus = summaryObj.optString("bonus");
                                showSummary();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Handler uiHandler = new Handler();
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //getOfflinePassbookInfo();
                            }
                        });
                    }
                }
            }).call();
        } else {
            Toast.makeText(getActivity(), "You have no Internet connection.", Toast.LENGTH_SHORT).show();

            Handler uiHandler = new Handler();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    //getOfflinePassbookInfo();
                }
            });

        }
    }

    private void getOfflinePassbookInfo () {
        {
            String data = ApplicationSettings.getPref(AppConstants.UEARN_PASSBOOK, "");
            JSONObject jsonObject = null;
            JSONArray monthArray = null;
            JSONObject monthObj = null;
            try {
                jsonObject = new JSONObject(data);
                if (jsonObject.has("success")) {
                    monthArray = jsonObject.getJSONArray("success");
                    for (int i = 0; i < monthArray.length(); i++) {
                        monthObj = monthArray.getJSONObject(i);
                        UearnRangePassbookInfo passbookInfoObj = new UearnRangePassbookInfo();
                        passbookInfoObj.day = monthObj.optString("day");
                        passbookInfoObj.brand = monthObj.optString("brand");
                        passbookInfoObj.img_url = monthObj.optString("img_url");
                        passbookInfoObj.name = monthObj.optString("name");
                        passbookInfoObj.description = monthObj.optString("description");
                        passbookInfoObj.uearned = monthObj.optString("uearned");
                        passbookInfoObj.sign = monthObj.optString("sign");
                        passbookInfoObj.closing_balance = monthObj.optString("closing_balance");
                        passbookRangeInfoList.add(passbookInfoObj);
                    }
                    if(!passbookRangeInfoList.equals("null")) {
                        showPassBookList(passbookRangeInfoList);
                    }
                }

                if (jsonObject.has("summary")) {
                    JSONObject summaryObj = jsonObject.getJSONObject("summary");
                    summaryInfo = new UearnPassbookSummaryInfo();
                    summaryInfo.earnings = summaryObj.optString("earnings");
                    summaryInfo.opening_balance = summaryObj.optString("opening_balance");
                    summaryInfo.paid_amount = summaryObj.optString("paid_amount");
                    summaryInfo.total_balance = summaryObj.optString("total_balance");
                    summaryInfo.closing_balance = summaryObj.optString("closing_balance");
                    summaryInfo.bonus = summaryObj.optString("bonus");
                    showSummary();


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private void showPassBookList(List<UearnRangePassbookInfo> passbookRangeInfoList) {
        passbookRecyclerView.setVisibility(View.VISIBLE);
        noDataLayout.setVisibility(View.GONE);
        UearnPassbookAdapter uearnPassbookAdapter = new UearnPassbookAdapter(passbookRangeInfoList);
        passbookRecyclerView.setAdapter(uearnPassbookAdapter);
    }


    public void showSummary() {
        summaryLayout.setVisibility(View.VISIBLE);
        openingBalance.setText(summaryInfo.opening_balance);
        totalPaid.setText(summaryInfo.paid_amount);
        uearned.setText(summaryInfo.earnings);
        bonus.setText(summaryInfo.bonus);
        totalpaymentDone.setText(summaryInfo.paid_amount);
        closingBalance.setText(summaryInfo.closing_balance);
    }




    public static PassbookFragment newInstance(int sectionNumber) {
        PassbookFragment fragment = new PassbookFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}