package smarter.uearn.money.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.Card;
import smarter.uearn.money.utils.UserActivities;
import smarter.uearn.money.utils.webservice.JsonParseAndOperate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created on 27-02-2017.
 */

public class SalesFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String ARG_USEREMAIL = "param1";
    private static final String ARG_GROUP_ID = "param2";

    private String usermail;
    private String groupId;
    private ArrayList<String> items = new ArrayList<>();

    public static SalesFragment newInstance(String param1, String param2) {
        SalesFragment fragment = new SalesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USEREMAIL, param1);
        args.putString(ARG_GROUP_ID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sales_reportsview, container, false);
        ListView listView = view.findViewById(R.id.card_listView);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        final ArrayList<HashMap<String, String>> agentList = UserActivities.getAgentList();
        final ArrayList<HashMap<String, String>> newList = new ArrayList<>();

        if (getArguments() != null) {
            usermail = getArguments().getString(ARG_USEREMAIL);
            groupId = getArguments().getString(ARG_GROUP_ID);

            if (agentList == null) {
                Toast.makeText(getActivity(), "No Agents data to show", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }/* else if (agentList.size() == 1) {
                Toast.makeText(getActivity(), "No Agents data to show", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
            }*/

            for (HashMap<String, String> item : agentList) {
                if (usermail.equalsIgnoreCase(item.get("agent"))) {
                    newList.add(item);
                    items.add(item.get("agent"));
                    break;
                }
            }

            for (int i = 0; i < agentList.size(); i++) {
                HashMap<String, String> item = agentList.get(i);
                for (int j = 0; j < JsonParseAndOperate.teamData.size(); j++) {
                    HashMap<String, String> member = JsonParseAndOperate.teamData.get(j);
                    if (item.get("agent").equalsIgnoreCase(member.get("email"))) {
                        if (groupId.equalsIgnoreCase(member.get("groupid"))) {
                            if (!items.contains(item.get("agent"))) {
                                newList.add(item);
                                items.add(item.get("agent"));
                            }
                        }
                    }
                }
            }

        }

        if (newList != null && (!newList.isEmpty()) && (newList.size() > 0)) {

            String mainEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
           /* if (items.contains(mainEmail)) {
                if (newList.size() == 1) {
                    Toast.makeText(getActivity(), "No Agents data to show", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }*/

            Collections.reverse(newList);
            Card adapter = new Card(newList, getContext(), 3);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    logAnalytics("Sales_Drill_Down", "CardView");

                    String agentMail = newList.get(position).get("agent");

                    boolean screenChange = false;
                    for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
                        if (checkMember.get("email").equalsIgnoreCase(agentMail)) {
                            if (checkMember.containsKey("multilevel")) {
                                if (checkMember.get("multilevel").equalsIgnoreCase("1")) {
                                    if (!groupId.equalsIgnoreCase(checkMember.get("id"))) {
                                        String agentGroupId = checkMember.get("id");
                                        SalesFragment newCalls = SalesFragment.newInstance(agentMail, agentGroupId);
                                        fragmentTransaction(newCalls);
                                        screenChange = true;
                                    }
                                }
                            }
                        }
                    }

                    if (!screenChange) {
                        String userId = "";
                        for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
                            if (checkMember.get("email").equalsIgnoreCase(agentMail)) {
                                userId = checkMember.get("id");
                                agentMail = checkMember.get("name"); // Remove this line of code if only email for title
                            }
                        }
/*
                        Bundle passData = new Bundle();
                        passData.putString("id", userId);
                        passData.putString("mail", agentMail);
                        Intent userLogs = new Intent(getActivity(),
                                DashboardUserActivitiesLog.class);
                        userLogs.putExtras(passData);
                        startActivity(userLogs);*/

//                        Intent folloaups = new Intent(getActivity(),
//                                DashboardTeamFollowups.class);
//                        folloaups.putExtra("UserId", userId);
//                        startActivity(folloaups);
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "No Agents data to show", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }
        return view;
    }

    private void fragmentTransaction(Fragment fragment) {
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.add(R.id.team_drill_down, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.detach(fragment);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logAnalytics(String eventName, String eventType) {
        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_19);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
        mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_19, AnalyticsBundle);
    }
}