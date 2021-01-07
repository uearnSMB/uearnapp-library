package smarter.uearn.money.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.webservice.JsonParseAndOperate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Srinath on 28-07-2017.
 */

public class DashboardAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> agentList;
    private Context context;
    private int requestCode;
    private int cld = 0, nCld = 0, cldUp = 0, folup = 0, nfolup = 0, folupUp = 0,
            oPlaced = 0, pLine = 0, iLid = 0, lot = 0, notI = 0;

    private int firstcallMissedValue = 0, followupmissed = 0, missedTotal = 0;
    private int firstcallTodo = 0, followuptodo = 0, todoTotal = 0;
    private int firstcallDone = 0, followupdone = 0, doneTotal = 0;
    private int leadsInProgressValue = 0, leadsNotInterestValue = 0;
    private int firstTalkTimeValue = 0, flptalkTimeValue = 0, totalTalkTime = 0, avgTalkTime = 0;
    private int firstincomingValue = 0, flpIncomingValue = 0, firstoutgoingValue = 0, flpoutgoingValue = 0, firstmissedValue = 0, flpMissedValue = 0, firstrnrValue = 0, flprnrValur, callsTotal =0;

    private String agentMailId = "";
    private boolean parentMultiLevel = false;
    private int hValue;
    private String printUsername;
    private TextView userValue;
    boolean checkmate = false;
    TextView firstTitle, flpTitle;

    public DashboardAdapter(ArrayList<HashMap<String, String>> data, Context context, int requestCode) {
        this.agentList = data;
        this.context = context;
        this.requestCode = requestCode;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return agentList.size();
    }


    @Override
    public View getView(int position, View contentView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dashboard_adapterview, viewGroup, false);
        TextView firstCall = view.findViewById(R.id.firstcall);
        TextView followup = view.findViewById(R.id.followup);
        ImageView phone = view.findViewById(R.id.phone);

        LinearLayout linearLayout = view.findViewById(R.id.callslayout);
        LinearLayout first_followup_layout = view.findViewById(R.id.first_followup_layout);
        RelativeLayout gpstracker_layout = view.findViewById(R.id.gpstracker_layout);

        TextView incomingTv = view.findViewById(R.id.incoming_percent);
        TextView outgoingTv = view.findViewById(R.id.outgoing_percent);
        TextView missedtv = view.findViewById(R.id.missed_percent);
        TextView rnrTv = view.findViewById(R.id.rnr_percent);
        firstTitle = view.findViewById(R.id.firstTitle);
        flpTitle = view.findViewById(R.id.flpTitleView);

        TextView titleTview = view.findViewById(R.id.titleTview);
        userValue = view.findViewById(R.id.totalTv);
        HashMap<String, String> agentmap1 = agentList.get(position);

        //Log.d("Dashboard Adapter", "card" + agentmap1);
        final String title = agentmap1.get("agent");
        agentMailId = agentmap1.get("agent");
        String userEmail = "";
        boolean check = true;
        ArrayList<String> cardTitles = new ArrayList<>();
        for(int i =0; i< agentList.size(); i++) {
            HashMap<String, String> map = agentList.get(i);

            String agent = map.get("agent");
            cardTitles.add(agent);
        }

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }

        if (title != null) {
            titleTview.setText(""+title);
        }

        if (requestCode == 6) {
            linearLayout.setVisibility(View.VISIBLE);
            first_followup_layout.setVisibility(View.GONE);
        } else if(requestCode == 1  || requestCode == 2) {
            linearLayout.setVisibility(View.GONE);
            first_followup_layout.setVisibility(View.INVISIBLE);
        } else if(requestCode == 7 ) {
            linearLayout.setVisibility(View.GONE);
            first_followup_layout.setVisibility(View.GONE);
            gpstracker_layout.setVisibility(View.VISIBLE);
        } else if(requestCode == 3 ) {
            firstTitle.setVisibility(View.INVISIBLE);
            firstCall.setVisibility(View.INVISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
            first_followup_layout.setVisibility(View.VISIBLE);
        }

        for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
            if (checkMember.get("email").equalsIgnoreCase(title)) {
                titleTview.setText(checkMember.get("name"));
            }
        }

        if (userEmail != null && !userEmail.isEmpty()) {
            if (title.equalsIgnoreCase(userEmail)) {
                check = false;
                titleTview.append(" Admin");
            }
        }
        //Added By Srinath.k
        String id = "";
        for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
            if (checkMember.get("email").equalsIgnoreCase(title)) {
                id = checkMember.get("id");
            }
        }
        ArrayList<String> agentMails = new ArrayList<>();
        if(id != null && !(id.isEmpty())) {
            agentMails.add(title);
            for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
                if(checkMember.get("groupid").equalsIgnoreCase(id)) {

                    agentMails.add(checkMember.get("email"));
                    //Log.d("Card","Agent Array"+agentMails);
                }
            }
        }

        for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
            if (checkMember.get("email").equalsIgnoreCase(title)) {

                if (checkMember.containsKey("multilevel")) {
                    if (checkMember.get("multilevel").equalsIgnoreCase("1")) {
                        if (!checkMember.get("id").equalsIgnoreCase(checkMember.get("groupid"))) {
                            parentMultiLevel = true;
                            if (title != null) {
                                //Log.d("AgentList", cardTitles.toString());
                                //Log.d("AgentMailList", agentMails.toString());

                                if(agentMails.size() == cardTitles.size()) {
                                    for (int k = 0; k < agentMails.size(); k++) {
                                        if(cardTitles.contains(agentMails.get(k))) {
                                            checkmate = true;
                                        } else {
                                            checkmate = false;
                                            break;
                                        }
                                    }
                                }
                                if(!checkmate) {
                                     titleTview.append("*");
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        check = true;
        if (check) {
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (title != null && (!title.equalsIgnoreCase("Totals"))) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (ApplicationSettings.getPref("Team:" + title, "") != null && (!ApplicationSettings.getPref("Team:" + title, "").isEmpty())) {
                            String num = "tel:" + ApplicationSettings.getPref("Team:" + title, "");
                            intent.setData(Uri.parse(num));
                        }
                        context.startActivity(intent);
                    }
                }
            });
            printUsername = agentmap1.get("agent");

            if (requestCode == 1) {

                findValues(agentmap1, 0, 1, 0);
                userValue.setText("" + leadsInProgressValue);

            } else if (requestCode == 2) {
                findValues(agentmap1, 0, 2, 0);
                userValue.setText("" + leadsNotInterestValue);

            } else if (requestCode == 3) {
                findValues(agentmap1, 0, 3, 0);
                //missedTotal = missedTotal - firstcallMissedValue;
                userValue.setText("" + followupmissed);
                firstCall.setText("0" );
                followup.setText(""+ followupmissed);

            } else if(requestCode == 4) {
                findValues(agentmap1, 0, 4, 0);
                findValues(agentmap1, 0, 3, 0);
                int total = todoTotal + firstcallMissedValue;
                userValue.setText("" + total);
                int todo = firstcallTodo + firstcallMissedValue;
                firstCall.setText(""+ todo);
                followup.setText(""+ followuptodo);

            } else if(requestCode == 5) {
                findValues(agentmap1, 0, 5, 0);
                userValue.setText("" + doneTotal);
                firstCall.setText(""+firstcallDone);
                followup.setText(""+followupdone);

            } else if(requestCode == 6) {
                findValues(agentmap1, 0, 6, 0);
                userValue.setText("" +callsTotal);
                int incoming = firstincomingValue + flpIncomingValue;
                int outgoing = firstoutgoingValue + flpoutgoingValue;
                int missed = firstmissedValue + flpMissedValue;
                int rnr = firstrnrValue + flprnrValur;
                incomingTv.setText(""+incoming);
                outgoingTv.setText(""+outgoing);
                missedtv.setText(""+missed);
                rnrTv.setText(""+rnr);
            } else if(requestCode == 7) {

            } else if(requestCode == 8) {
                findValues(agentmap1, 0, 8, 0);
                findValues(agentmap1, 0, 6, 0);
                int incoming = firstincomingValue + flpIncomingValue;
                int outgoing = firstoutgoingValue + flpoutgoingValue;
                int total = incoming + outgoing;
                int hours = 0, mins  =0, sec = 0, avgCalls = 0, avgHours = 0, avgMins = 0, avgSec = 0;
                if(total > 0) {
                    avgCalls = totalTalkTime /total;
                    if(avgCalls > 0) {
                        avgHours = avgCalls / 3600;
                        avgMins = (avgCalls % 3600) / 60;
                        avgSec = avgCalls % 60;
                    }
                }
                if(totalTalkTime > 0) {
                    hours = totalTalkTime / 3600;
                    mins = (totalTalkTime % 3600) / 60;
                    sec = totalTalkTime%60;
                }
                firstTitle.setText("Avg TALKTIME");
                flpTitle.setText("TOTAL TALKTIME");

                firstCall.setText(avgHours +"h "+avgMins+"m "+ avgSec+"s");
                followup.setText(hours+"h "+mins+"m "+ sec+"s");
            }
        }
        return view;
    }

    private int findValues(HashMap<String, String> agentmap, int value, int requestCode, int memberItem) {
        String userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        boolean check = false;
        for(int m =0; m< agentList.size(); m++) {
            HashMap<String, String> map = agentList.get(m);
            if((map.get("agent").equalsIgnoreCase(agentmap.get("agent")))) {
                check = true;
                break;
            }
        }

        if(!check) {
            return 0;
        }

        if (agentmap != null) {
            switch (requestCode) {
                case 1:
                    leadsInProgressValue = 0;
                    //Log.d("Dashboarddata", agentmap.get("agent"));
                    String leadsInProgress = agentmap.get("leadsInProgress");

                    if (!checkmate) {
                        if (agentmap.containsKey("teamleadsInProgress")) {
                            leadsInProgress = agentmap.get("teamleadsInProgress");
                        }
                    } else {
                        leadsInProgress = agentmap.get("leadsInProgress");
                    }

                    if (leadsInProgress != null) {
                        if (!leadsInProgress.isEmpty()) {
                            leadsInProgressValue = Integer.parseInt(leadsInProgress);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                leadsInProgressValue = Integer.parseInt(leadsInProgress);
                            }
                        }
                    }

                    value = leadsInProgressValue;
                    break;
                case 2:
                    leadsNotInterestValue = 0;
                    String leadsNotInterest = agentmap.get("leadsNotInterested");

                    if (!checkmate) {
                        if (agentmap.containsKey("teamleadsNotInterested")) {
                            leadsNotInterest = agentmap.get("teamleadsNotInterested");
                        }
                    } else {
                        leadsNotInterest = agentmap.get("leadsNotInterested");

                    }

                    if (leadsNotInterest != null) {
                        if (!leadsNotInterest.isEmpty()) {
                            leadsNotInterestValue = Integer.parseInt(leadsNotInterest);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                leadsNotInterestValue = Integer.parseInt(leadsNotInterest);
                            }
                        }
                    }

                    value = leadsNotInterestValue;
                    break;
                case 3:
                    firstcallMissedValue = 0;
                    followupmissed = 0;

                    String firstCallMissed = agentmap.get("notCalled");
                    String followupMissed = agentmap.get("notFollowedup");

                    if (!checkmate) {
                        if (agentmap.containsKey("teamnotCalled")) {
                            firstCallMissed = agentmap.get("teamnotCalled");
                        }

                        if (agentmap.containsKey("teamnotFollowedup")) {
                            followupMissed = agentmap.get("teamnotFollowedup");
                        }

                    } else {
                        firstCallMissed = agentmap.get("notCalled");
                        followupMissed = agentmap.get("notFollowedup");
                    }

                    if (firstCallMissed != null) {
                        if (!firstCallMissed.isEmpty()) {
                            firstcallMissedValue = Integer.parseInt(firstCallMissed);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                firstcallMissedValue = Integer.parseInt(firstCallMissed);
                            }
                        }
                    }

                    if (followupMissed != null) {
                        if (!followupMissed.isEmpty()) {
                            followupmissed = Integer.parseInt(followupMissed);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                followupmissed = Integer.parseInt(followupMissed);
                            }
                        }
                    }

                    missedTotal = firstcallMissedValue + followupmissed;
                    break;

                case 4:
                    firstcallTodo = 0;
                    followuptodo = 0;
                    String firstCallTodo = agentmap.get("calledUpcoming");
                    String followupTodo = agentmap.get("followedUpcoming");

                    if (!checkmate) {
                        if (agentmap.containsKey("teamcalledUpcoming")) {
                            firstCallTodo = agentmap.get("teamcalledUpcoming");
                        }

                        if (agentmap.containsKey("teamfollowedUpcoming")) {
                            followupTodo = agentmap.get("teamfollowedUpcoming");
                        }

                    } else {
                        firstCallTodo = agentmap.get("calledUpcoming");
                        followupTodo = agentmap.get("followedUpcoming");
                    }

                    if (firstCallTodo != null) {
                        if (!firstCallTodo.isEmpty()) {
                            firstcallTodo = Integer.parseInt(firstCallTodo);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                firstcallTodo = Integer.parseInt(firstCallTodo);
                            }
                        }
                    }

                    if (followupTodo != null) {
                        if (!followupTodo.isEmpty()) {
                            followuptodo = Integer.parseInt(followupTodo);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                followuptodo = Integer.parseInt(followupTodo);
                            }
                        }
                    }

                    todoTotal = firstcallTodo + followuptodo;
                    break;

                case 5:
                    firstcallDone = 0;
                    followupdone = 0;
                    String firstCallDone = agentmap.get("called");
                    String followupDone = agentmap.get("followup");

                    if (!checkmate) {
                        if (agentmap.containsKey("teamcalled")) {
                            firstCallDone = agentmap.get("teamcalled");
                        }

                        if (agentmap.containsKey("teamfollowup")) {
                            followupDone = agentmap.get("teamfollowup");
                        }

                    } else {
                        firstCallDone = agentmap.get("called");
                        followupDone = agentmap.get("followup");
                    }

                    if (firstCallDone != null) {
                        if (!firstCallDone.isEmpty()) {
                            firstcallDone = Integer.parseInt(firstCallDone);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                firstcallDone = Integer.parseInt(firstCallDone);
                            }
                        }
                    }

                    if (followupDone != null) {
                        if (!followupDone.isEmpty()) {
                            followupdone = Integer.parseInt(followupDone);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                followupdone = Integer.parseInt(followupDone);
                            }
                        }
                    }

                    doneTotal = firstcallDone + followupdone;
                    break;


                case 6:
                    firstincomingValue = 0;
                    flpIncomingValue = 0;
                    firstoutgoingValue = 0;
                    flpoutgoingValue = 0;
                    firstmissedValue = 0;
                    flpMissedValue = 0;
                    firstrnrValue = 0;
                    flprnrValur = 0;
                    //Log.d("Dashboarddata", agentmap.get("agent"));
                    String firstcallincoming = agentmap.get("firstcallincoming");
                    String flpincoming = agentmap.get("flpincoming");
                    String firstoutgoing = agentmap.get("firstoutgoing");
                    String flpoutgoing = agentmap.get("flpoutgoing");
                    String firstmissed = agentmap.get("firstcallmissed");
                    String flpmissed = agentmap.get("flpmissed");
                    String firstcallrnr = agentmap.get("firstcallrnr");
                    String flprnr = agentmap.get("flprnr");

                    if (!checkmate) {
                        if (agentmap.containsKey("teamfirstcallmissed")) {
                            firstmissed = agentmap.get("teamfirstcallmissed");
                        }

                        if (agentmap.containsKey("teamflpmissed")) {
                            flpmissed = agentmap.get("teamflpmissed");
                        }

                        if (agentmap.containsKey("teamfirstoutgoing")) {
                            firstoutgoing = agentmap.get("teamfirstoutgoing");
                        }

                        if (agentmap.containsKey("teamflpoutgoing")) {
                            flpoutgoing = agentmap.get("teamflpoutgoing");
                        }

                        if (agentmap.containsKey("teamfirstcallincoming")) {
                            firstcallincoming = agentmap.get("teamfirstcallincoming");
                        }

                        if (agentmap.containsKey("teamflpincoming")) {
                            flpincoming = agentmap.get("teamflpincoming");
                        }

                        if (agentmap.containsKey("teamfirstcallrnr")) {
                            firstcallrnr = agentmap.get("teamfirstcallrnr");
                        }

                        if (agentmap.containsKey("teamflprnr")) {
                            flprnr = agentmap.get("teamflprnr");
                        }

                    } else {
                        firstcallincoming = agentmap.get("firstcallincoming");
                        flpincoming = agentmap.get("flpincoming");
                        firstoutgoing = agentmap.get("firstoutgoing");
                        flpoutgoing = agentmap.get("flpoutgoing");
                        firstmissed = agentmap.get("firstcallmissed");
                        flpmissed = agentmap.get("flpmissed");
                        firstcallrnr = agentmap.get("firstcallrnr");
                        flprnr = agentmap.get("flprnr");
                    }

                    if (firstcallincoming != null) {
                        if (!firstcallincoming.isEmpty()) {
                            firstincomingValue = Integer.parseInt(firstcallincoming);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                firstincomingValue = Integer.parseInt(firstcallincoming);
                            }
                        }
                    }

                    if (flpincoming != null) {
                        if (!flpincoming.isEmpty()) {
                            flpIncomingValue = Integer.parseInt(flpincoming);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                flpIncomingValue = Integer.parseInt(flpincoming);
                            }
                        }
                    }

                    if (firstoutgoing != null) {
                        if (!firstoutgoing.isEmpty()) {
                            firstoutgoingValue = Integer.parseInt(firstoutgoing);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                firstoutgoingValue = Integer.parseInt(firstoutgoing);
                            }
                        }
                    }

                    if (flpoutgoing != null) {
                        if (!flpoutgoing.isEmpty()) {
                            flpoutgoingValue = Integer.parseInt(flpoutgoing);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                flpoutgoingValue = Integer.parseInt(flpoutgoing);
                            }
                        }
                    }

                    if (firstmissed != null) {
                        if (!firstmissed.isEmpty()) {
                            firstmissedValue = Integer.parseInt(firstmissed);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                firstmissedValue = Integer.parseInt(firstmissed);
                            }
                        }
                    }

                    if (flpmissed != null) {
                        if (!flpmissed.isEmpty()) {
                            flpMissedValue = Integer.parseInt(flpmissed);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                flpMissedValue = Integer.parseInt(flpmissed);
                            }
                        }
                    }

                    if (firstcallrnr != null) {
                        if (!firstcallrnr.isEmpty()) {
                            firstrnrValue = Integer.parseInt(firstcallrnr);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                firstrnrValue = Integer.parseInt(firstcallrnr);
                            }
                        }
                    }

                    if (flprnr != null) {
                        if (!flprnr.isEmpty()) {
                            flprnrValur = Integer.parseInt(flprnr);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                flprnrValur = Integer.parseInt(flprnr);
                            }
                        }
                    }

                    callsTotal = firstincomingValue + flpIncomingValue + firstoutgoingValue + flpoutgoingValue + firstmissedValue + flpMissedValue + firstrnrValue + flprnrValur;
                    break;

                case 8:

                    String fisrtTalkTime = agentmap.get("firsttalktime");
                    String flptalkTime = agentmap.get("flptalktime");

                    if (!checkmate) {
                        if (agentmap.containsKey("teamfirsttalktime")) {
                            fisrtTalkTime = agentmap.get("teamfirsttalktime");
                        }
                        if (agentmap.containsKey("teamflptalktime")) {
                            flptalkTime = agentmap.get("teamflptalktime");
                        }
                    } else {
                        fisrtTalkTime = agentmap.get("firsttalktime");
                        flptalkTime = agentmap.get("firsttalktime");
                    }

                    if (fisrtTalkTime != null) {
                        if (!fisrtTalkTime.isEmpty()) {
                            firstTalkTimeValue = Integer.parseInt(fisrtTalkTime);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                firstTalkTimeValue = Integer.parseInt(fisrtTalkTime);
                            }
                        }
                    }

                    if (flptalkTime != null) {
                        if (!flptalkTime.isEmpty()) {
                            flptalkTimeValue = Integer.parseInt(flptalkTime);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                flptalkTimeValue = Integer.parseInt(flptalkTime);
                            }
                        }
                    }

                    totalTalkTime = firstTalkTimeValue+flptalkTimeValue;
                    break;


            }
        }
        return value;

    }

}
