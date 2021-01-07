package smarter.uearn.money.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.webservice.JsonParseAndOperate;
import smarter.uearn.money.views.FiveLevelProgress;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 27-02-2017.
 */

public class Card extends BaseAdapter implements View.OnClickListener {

    private ArrayList<HashMap<String, String>> agentList;
    private ArrayList<HashMap<String, String>> agentList1 = UserActivities.getAgentList();
    private Context context;
    private int requestCode;
    private int cld = 0, nCld = 0, cldUp = 0, folup = 0, nfolup = 0, folupUp = 0,
            oPlaced = 0, pLine = 0, iLid = 0, lot = 0, notI = 0;
    private int hcld = 0, hnCld = 0, hcldUp = 0, hfolup = 0, hnfolup = 0, hfolupUp = 0,
            hoPlaced = 0, hpLine = 0, hiLid = 0, hlot = 0, hnotI = 0;
    private String agentMailId = "";
    private boolean parentMultiLevel = false;
    private int hValue;
    private String printUsername;
    private TextView userValue;
    boolean checkmate = false;

    public Card(ArrayList<HashMap<String, String>> data, Context context, int requestCode) {
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
        return null;
    }

    @Override
    public int getCount() {
        //Log.d("Count in Card", agentList.size() + "");
        return agentList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cardview_layout, parent, false);
        ProgressBar progressBar = view.findViewById(R.id.folloupsprogress);
        final TextView positive_percnt = view.findViewById(R.id.positive_percnt);
        final TextView negative_percnt = view.findViewById(R.id.negative_percent);
        final TextView tvUpcoming = view.findViewById(R.id.tv_upcoming);
        final TextView tv_todo = view.findViewById(R.id.tv_todo);

        final TextView tvTodo = view.findViewById(R.id.flp_todo);
        final TextView tvOverdue = view.findViewById(R.id.flp_overdue);
        final TextView tvDone = view.findViewById(R.id.flp_done);
        final TextView tvTotal = view.findViewById(R.id.total);

        ImageView phone = view.findViewById(R.id.phoneImg);
        TextView titletv = view.findViewById(R.id.title);
        TextView calledTv = view.findViewById(R.id.title1);
        TextView notCalledTv = view.findViewById(R.id.title2);
        userValue = view.findViewById(R.id.tv_user_value);
        TextView teamValue = view.findViewById(R.id.tv_team_value);
        TextView title3 = view.findViewById(R.id.title3);
        final LinearLayout linearLayout = view.findViewById(R.id.llSalesLayout);
        final LinearLayout followup_layout = view.findViewById(R.id.followup_layout);
        final LinearLayout overdue_layout = view.findViewById(R.id.overdue_layout);
        final LinearLayout done_layout = view.findViewById(R.id.done_layout);
        HashMap<String, String> agentmap1 = agentList.get(position);

        //Log.d("CARD", "card_AgentMap" + agentmap1);
        final String title = agentmap1.get("agent");
        agentMailId = agentmap1.get("agent");
        String userEmail = "";
        boolean check = true;
        ArrayList<String> cardTitles = new ArrayList<>();
        for (int i = 0; i < agentList.size(); i++) {
            HashMap<String, String> map = agentList.get(i);
            String agent = map.get("agent");
            cardTitles.add(agent);
        }

        if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
            userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        }

        if (ApplicationSettings.containsPref(AppConstants.AFTERCALLACTIVITY_SCREEN)) {
             String screen = ApplicationSettings.getPref(AppConstants.AFTERCALLACTIVITY_SCREEN, "");
            if ((screen!= null && ((screen.equalsIgnoreCase("Auto1AfterCallActivity")) || (screen.equalsIgnoreCase("Auto2AfterCallActivity"))))) {
                tv_todo.setText("SCHEDULED");
            }
        }

        if (title != null) {
            titletv.setText(title);
        }

        if (title != null && !title.equalsIgnoreCase("team")&& agentList.size() > 1) {
            followup_layout.setVisibility(View.GONE);
            overdue_layout.setVisibility(View.GONE);
            done_layout.setVisibility(View.GONE);
        } else {
            followup_layout.setVisibility(View.VISIBLE);
            overdue_layout.setVisibility(View.VISIBLE);
            done_layout.setVisibility(View.VISIBLE);
        }

        for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
            if (checkMember.get("email").equalsIgnoreCase(title)) {
                titletv.setText(checkMember.get("name"));
                tvTotal.setText(checkMember.get("name"));
                //Log.d("Card", "AgentId" + checkMember.get("name"));
            }
        }

        if (userEmail != null && !userEmail.isEmpty()) {
            if (title.equalsIgnoreCase(userEmail)) {
                check = false;
                titletv.append(" Admin");
            }
        }
        //Added By Srinath.k
        String id = "";
        String anotherId = "";
        for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
            if (checkMember.get("email").equalsIgnoreCase(title)) {
                id = checkMember.get("id");
                anotherId = checkMember.get("groupid");
                //Log.d("Card", "AgentId" + id);
            }
        }
        ArrayList<String> agentMails = new ArrayList<>();
        if (id != null && !(id.isEmpty())) {
            agentMails.add(title);
            for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
                if (checkMember.get("groupid").equalsIgnoreCase(id)) {

                    agentMails.add(checkMember.get("email"));
                    //Log.d("Card", "Agent Array" + agentMails);
                }
            }
        }
        /*int total = 0;
        if(agentMails != null && !agentMails.isEmpty()) {
            Log.d("Card","Agent mails not empty"+agentMails);
            total = getValues(agentMails);
        }*/

        for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
            if (checkMember.get("email").equalsIgnoreCase(title)) {

                if (checkMember.containsKey("multilevel")) {
                    if (checkMember.get("multilevel").equalsIgnoreCase("1")) {
                        if (!checkMember.get("id").equalsIgnoreCase(checkMember.get("groupid"))) {
                            parentMultiLevel = true;
                            if (title != null) {
                                //Log.d("AgentList", cardTitles.toString());
                                //Log.d("AgentMailList", agentMails.toString());

                                if (agentMails.size() == cardTitles.size()) {
                                    for (int k = 0; k < agentMails.size(); k++) {
                                        if (cardTitles.contains(agentMails.get(k))) {
                                            checkmate = true;
                                        } else {
                                            checkmate = false;
                                            break;
                                        }
                                    }
                                }
                                if (!checkmate) {
                                    titletv.append("*");
                                    tvTotal.append("*");
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
                        //String callerNumber = ApplicationSettings.getPref("Team:"+)
                        if (ApplicationSettings.getPref("Team:" + title, "") != null && (!ApplicationSettings.getPref("Team:" + title, "").isEmpty())) {
                            String num = "tel:" + ApplicationSettings.getPref("Team:" + title, "");
                            intent.setData(Uri.parse(num));
                        }
                        context.startActivity(intent);
                    }
                }
            });

            cld = 0;
            nCld = 0;
            cldUp = 0;
            folup = 0;
            nfolup = 0;
            folupUp = 0;
            oPlaced = 0;
            pLine = 0;
            iLid = 0;
            lot = 0;
            notI = 0;


            printUsername = agentmap1.get("agent");


            if (requestCode == 1) {
                cld = 0;
                nCld = 0;
                cldUp = 0;
                folup = 0;
                nfolup = 0;
                folupUp = 0;
                oPlaced = 0;
                pLine = 0;
                iLid = 0;
                lot = 0;
                notI = 0;

                int printFinal = findValues(agentmap1, 0, 1, 0);
                findValues(agentmap1, 0, 2, 0);

                int firstCallPrintValue = cld + nCld + cldUp;
                int flpdone = cld + folup;
                //Log.d("dashboard", "flpdone:cld" + cld);
                //Log.d("dashboard", "flpdone:flp" + folup);
                int flptodo = cldUp + folupUp;
                //Log.d("dashboard", "flpUpcmg:cld" + cldUp);
                //Log.d("dashboard", "flpUpcmg:flpup" + folupUp);
                int flpmissed = nCld + nfolup;
                //Log.d("dashboard", "flpmissed:cld" + nCld);
                //Log.d("dashboard", "flpmissed:flp" + nfolup);

                userValue.setText("" + firstCallPrintValue);
                tvTodo.setText("" + flptodo);
                tvOverdue.setText("" + flpmissed);
                tvDone.setText("" + flpdone);
                //Log.d("dashboard", "Print value:" + printFinal);
                //Log.d("dashboard", "first call value:" + firstCallPrintValue);

                if (title != null && (title.equalsIgnoreCase("Totals"))) {
                    titletv.setVisibility(View.GONE);
                    calledTv.setVisibility(View.GONE);
                    notCalledTv.setVisibility(View.GONE);
                    phone.setVisibility(View.GONE);
                } else {
                    titletv.setVisibility(View.VISIBLE);
                    calledTv.setVisibility(View.VISIBLE);
                    notCalledTv.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    calledTv.setText("DONE"); //Previously known as called
                    notCalledTv.setText("MISSED"); //Previously known as not called
                    title3.setText("TODO");
                }

                //int called = hcld, notCalled = hnCld, callUpcoming = hcldUp;

                /*if (parentMultiLevel) {
                    if ((called + notCalled + callUpcoming) != 0) {
                        final int finalCalled = called;
                        final int finalNotCalled = notCalled;
                        final int finalCallUpcoming = callUpcoming;

                        positive_percnt.setText("[" + String.valueOf(finalCalled) + "]");
                        negative_percnt.setText("[" + String.valueOf(finalNotCalled) + "]");
                        tvUpcoming.setText("[" + String.valueOf(finalCallUpcoming) + "]");
                    }
                }*/

                /*called = cld;
                notCalled = nCld;
                callUpcoming = cldUp;*/
                int called = cld, notCalled = nCld, callUpcoming = cldUp;
                if ((called + notCalled + callUpcoming) != 0) {
                    progressBar.setVisibility(View.GONE);

                    final int fprogress = (called * 100) / (called + notCalled + callUpcoming);
                    final int foprogress = (notCalled * 100) / (called + notCalled + callUpcoming);
                    final int fuprogress = (callUpcoming * 100) / (called + notCalled + callUpcoming);

                    final int finalCalled = called;
                    final int finalNotCalled = notCalled;
                    final int finalCallUpcoming = callUpcoming;

                    linearLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (context != null) {
                                FiveLevelProgress sProgress = new FiveLevelProgress(context);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);
                                sProgress.setLayoutParams(params);

                                sProgress.setFirstProgress(fprogress, linearLayout.getWidth());
                                sProgress.setSecondProgress(fprogress + foprogress, linearLayout.getWidth());
                                sProgress.setThirdProgress(fprogress + foprogress + fuprogress, linearLayout.getWidth());

                                positive_percnt.setText(String.valueOf(finalCalled) + " (" + String.valueOf(fprogress) + "%)");
                                negative_percnt.setText(String.valueOf(finalNotCalled) + " (" + String.valueOf(foprogress) + "%)");
                                tvUpcoming.setText(String.valueOf(finalCallUpcoming) + " (" + String.valueOf(fuprogress) + "%)");

                                linearLayout.addView(sProgress);
                            }
                        }
                    });
                }
            } else if (requestCode == 2) {
                findValues(agentmap1, 0, 2, 0);

                /*if (parentMultiLevel) {
                    userValue.setText("" + printFinal);
                    //teamValue.setText("[" + hValue + "]");
                } else {
                    userValue.setText("" + hValue);
                    teamValue.setVisibility(View.GONE);
                }*/

                if (title != null && (title.equalsIgnoreCase("Totals"))) {
                    titletv.setVisibility(View.GONE);
                    calledTv.setVisibility(View.GONE);
                    notCalledTv.setVisibility(View.GONE);
                    phone.setVisibility(View.GONE);
                } else {
                    titletv.setVisibility(View.VISIBLE);
                    calledTv.setVisibility(View.VISIBLE);
                    notCalledTv.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    calledTv.setText("DONE"); //Previously called as followed-up
                    notCalledTv.setText("MISSED"); //Previously called as To followed-up
                    title3.setText("TODO");
                }

                int followup = hfolup, notfollowup = hnfolup, fcg = hfolupUp;
                /*if (parentMultiLevel) {
                    if ((followup + notfollowup + fcg) != 0) {
                        final int finalFollowup = followup;
                        final int finalNotfollowup = notfollowup;
                        final int finalFcg = fcg;

                        positive_percnt.setText("[" + String.valueOf(finalFollowup) + "]");
                        negative_percnt.setText("[" + String.valueOf(finalNotfollowup) + "]");
                        tvUpcoming.setText("[" + String.valueOf(finalFcg) + "]");
                    }
                }*/

                followup = folup;
                notfollowup = nfolup;
                fcg = folupUp;
                if ((followup + notfollowup + fcg) != 0) {
                    int finalFollowPrintValue = followup + notfollowup + fcg;
                    userValue.setText("" + finalFollowPrintValue);
                    //Log.d("dashboard", "followup call value:" + finalFollowPrintValue);

                    progressBar.setVisibility(View.GONE);

                    // Load three level progress
                    final int fprogress = (followup * 100) / (followup + notfollowup + fcg);
                    final int foprogress = (notfollowup * 100) / (followup + notfollowup + fcg);
                    final int fuprogress = (fcg * 100) / (followup + notfollowup + fcg);

                    final int finalFollowup = followup;
                    final int finalNotfollowup = notfollowup;
                    final int finalFcg = fcg;

                    linearLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (context != null) {
                                FiveLevelProgress sProgress = new FiveLevelProgress(context);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);
                                sProgress.setLayoutParams(params);

                                sProgress.setFirstProgress(fprogress, linearLayout.getWidth());
                                sProgress.setSecondProgress(fprogress + foprogress, linearLayout.getWidth());
                                sProgress.setThirdProgress(fprogress + foprogress + fuprogress, linearLayout.getWidth());

                                positive_percnt.setText(String.valueOf(finalFollowup) + " (" + String.valueOf(fprogress) + "%)");
                                negative_percnt.setText(String.valueOf(finalNotfollowup) + " (" + String.valueOf(foprogress) + "%)");
                                tvUpcoming.setText(String.valueOf(finalFcg) + " (" + String.valueOf(fuprogress) + "%)");

                                linearLayout.addView(sProgress);
                            }
                        }
                    });
                }
            } else if (requestCode == 3) {
                findValues(agentmap1, 0, 3, 0);

                /*if (parentMultiLevel) {
                    userValue.setText("" + printFinal);
                    //teamValue.setText("[" + hValue + "]");
                } else {
                    userValue.setText("" + hValue);
                    teamValue.setVisibility(View.GONE);
                }*/

                if (title != null && (title.equalsIgnoreCase("Totals"))) {
                    titletv.setVisibility(View.GONE);
                    calledTv.setVisibility(View.GONE);
                    notCalledTv.setVisibility(View.GONE);
                    phone.setVisibility(View.GONE);
                } else {
                    titletv.setVisibility(View.VISIBLE);
                    calledTv.setVisibility(View.VISIBLE);
                    notCalledTv.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    calledTv.setText("Order placed");
                    notCalledTv.setText("Pipeline");
                }

                int orderplacedValue = hoPlaced, pipelinevalue = hpLine, invalidValue = hiLid, lostValue = hlot, notInterestValue = hnotI;
                /*if (parentMultiLevel) {
                    if ((orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue) != 0) {
                        positive_percnt.setText("[" + orderplacedValue + "]");
                        negative_percnt.setText("[" + pipelinevalue + "]");
                    }
                }*/

                orderplacedValue = oPlaced;
                pipelinevalue = pLine;
                invalidValue = iLid;
                lostValue = lot;
                notInterestValue = notI;
                if ((orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue) != 0) {
                    int printSalesValues = orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue;
                    userValue.setText("" + printSalesValues);
                    //Log.d("dashboard", "sales call value:" + printSalesValues);

                    //progressBar.setProgress((orderplacedValue * 100) / (orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue));
                    progressBar.setVisibility(View.GONE);
                    //Log.d("FolloupPercerntage", "" + (orderplacedValue * 100) / (orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue));
                    positive_percnt.setText(oPlaced + " ( " + ((orderplacedValue * 100) / (orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue)) + " % )");
                    negative_percnt.setText(pLine + " ( " + ((pipelinevalue * 100) / (orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue)) + " % )");
                    final int orderPercentage = (orderplacedValue * 100) / (orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue);
                    final int lostPercentage = (lostValue * 100) / (orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue);
                    final int invalidPercentage = (invalidValue * 100) / (orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue);
                    final int notInterestedPercentage = (notInterestValue * 100) / (orderplacedValue + pipelinevalue + invalidValue + lostValue + notInterestValue);

                    linearLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (context != null) {
                                FiveLevelProgress sProgress = new FiveLevelProgress(context);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);
                                sProgress.setLayoutParams(params);

                                sProgress.setFirstProgress(orderPercentage, linearLayout.getWidth());
                                sProgress.setSecondProgress(orderPercentage + lostPercentage, linearLayout.getWidth());
                                sProgress.setThirdProgress(orderPercentage + lostPercentage + invalidPercentage, linearLayout.getWidth());
                                sProgress.setFourthProgress(orderPercentage + lostPercentage + invalidPercentage + notInterestedPercentage, linearLayout.getWidth());

                                linearLayout.addView(sProgress);
                            }
                        }
                    });
                }
            }
        }

        return view;
    }

    private int getValues(ArrayList<String> agentEmails) {
        int total = 0;
        for (int m = 0; m < agentList1.size(); m++) {
            HashMap<String, String> agent = agentList1.get(m);
            //Log.d("Card", "AgentName" + agent.get("agent"));

            for (int i = 0; i < agentEmails.size(); i++) {
                //Log.d("Card", "AgentMailsName" + agentEmails.get(i));
                if (agentEmails.get(i).equalsIgnoreCase(agent.get("agent"))) {
                    int printFinal = findgroupValues(agent, 0, requestCode, 0);
                    total = total + printFinal;
                    //Log.d("Card", "Total" + printFinal);
                    //Log.d("Card", "AgentName" + agent.get("agent"));
                    break;
                }
            }
        }
        return total;
    }

    private int findgroupValues(HashMap<String, String> agentmap, int value, int requestCode, int memberItem) {
        switch (requestCode) {
            case 1:
                int ft1 = 0, ft2 = 0, ft3 = 0;
                String cS = agentmap.get("called");
                String nCS = agentmap.get("notCalled");
                String cU = agentmap.get("calledUpcoming");
                //Log.d("Card",cS+nCS+cU);

                if (cS != null) {
                    if (!cS.isEmpty()) {
                        cld += Integer.parseInt(cS);
                        ft1 = Integer.parseInt(cS);
                    }
                }

                if (nCS != null) {
                    if (!nCS.isEmpty()) {
                        nCld += Integer.parseInt(nCS);
                        ft2 = Integer.parseInt(nCS);
                    }
                }

                if (cU != null) {
                    if (!cU.isEmpty()) {
                        cldUp += Integer.parseInt(cU);
                        ft3 = Integer.parseInt(cU);
                    }
                }
                //value += cld + nCld + cldUp;
                value += ft1 + ft2 + ft3;
                //Log.d("Card","Value"+value);
                break;
            case 2:
                String fU = agentmap.get("followup");
                String nFU = agentmap.get("notFollowedup");
                String uP = agentmap.get("followedUpcoming");
                //Log.d("Card",fU+nFU+uP);
                if (fU != null) {
                    if (!fU.isEmpty()) {
                        folup += Integer.parseInt(fU);
                    }
                }

                if (nFU != null) {
                    if (!nFU.isEmpty()) {
                        nfolup += Integer.parseInt(nFU);
                    }
                }

                if (uP != null) {
                    if (!uP.isEmpty()) {
                        folupUp += Integer.parseInt(uP);
                    }
                }

                value += folup + nfolup + folupUp;
                //Log.d("Card","value"+value);
                return value;

            case 3:
                String oP = agentmap.get("orderPlaced");
                String pP = agentmap.get("rest");
                String iD = agentmap.get("invalid");
                String lst = agentmap.get("lost");
                String nI = agentmap.get("notInterested");

                if (oP != null) {
                    if (!oP.isEmpty()) {
                        oPlaced += Integer.parseInt(oP);
                    }
                }

                if (pP != null) {
                    if (!pP.isEmpty()) {
                        pLine += Integer.parseInt(pP);
                    }
                }

                if (iD != null) {
                    if (!iD.isEmpty()) {
                        iLid += Integer.parseInt(iD);
                    }
                }

                if (lst != null) {
                    if (!lst.isEmpty()) {
                        lot += Integer.parseInt(lst);
                    }
                }

                if (nI != null) {
                    if (!nI.isEmpty()) {
                        notI += Integer.parseInt(nI);
                    }
                }

                value += oPlaced + pLine + iLid + lot + notI;

                break;
        }
        return value;
    }

    private int findValues(HashMap<String, String> agentmap, int value, int requestCode, int memberItem) {
        String userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
        boolean check = false;
        for (int m = 0; m < agentList.size(); m++) {
            HashMap<String, String> map = agentList.get(m);
            if ((map.get("agent").equalsIgnoreCase(agentmap.get("agent")))) {
                check = true;
                break;
            }
        }

        if (!check) {
            return 0;
        }

        if (agentmap != null) {
            switch (requestCode) {
                case 1:
                    String agent = agentmap.get("agent");
                    //Log.d("Agent in Card", agent);
                    String cS = agentmap.get("called");
                    String nCS = agentmap.get("notCalled");
                    String cU = agentmap.get("calledUpcoming");

                    if (!checkmate) {
                        if (agentmap.containsKey("teamcalled")) {
                            cS = agentmap.get("teamcalled");
                        }

                        if (agentmap.containsKey("teamnotCalled")) {
                            nCS = agentmap.get("teamnotCalled");
                        }

                        if (agentmap.containsKey("teamcalledUpcoming")) {
                            cU = agentmap.get("teamcalledUpcoming");
                        }
                    } else {
                        cS = agentmap.get("called");
                        nCS = agentmap.get("notCalled");
                        cU = agentmap.get("calledUpcoming");
                    }

                    if (cS != null) {
                        if (!cS.isEmpty()) {
                            cld = Integer.parseInt(cS);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                cld = Integer.parseInt(cS);
                            }
                        }
                    }

                    if (nCS != null) {
                        if (!nCS.isEmpty()) {
                            nCld = Integer.parseInt(nCS);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                nCld = Integer.parseInt(nCS);
                            }
                        }
                    }

                    if (cU != null) {
                        if (!cU.isEmpty()) {
                            cldUp = Integer.parseInt(cU);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                cldUp = Integer.parseInt(cU);
                            }
                        }
                    }
                    value += cld + nCld + cldUp;
                    //Log.d("Agent in cld", ""+cld);
                    //Log.d("Agent in nCld", ""+nCld);
                    //Log.d("Agent in CldUp", ""+cldUp);

                    if (userEmail.equals(agentmap.get("agent"))) {
                        hValue = value;
                        return value;
                    }
                    break;
                case 2:
                    String fU = agentmap.get("followup");
                    String nFU = agentmap.get("notFollowedup");
                    String uP = agentmap.get("followedUpcoming");

                    if (!checkmate) {
                        if (agentmap.containsKey("teamfollowup")) {
                            fU = agentmap.get("teamfollowup");
                        }

                        if (agentmap.containsKey("teamnotFollowedup")) {
                            nFU = agentmap.get("teamnotFollowedup");
                        }

                        if (agentmap.containsKey("teamfollowedUpcoming")) {
                            uP = agentmap.get("teamfollowedUpcoming");
                        }
                    } else {
                        fU = agentmap.get("followup");
                        nFU = agentmap.get("notFollowedup");
                        uP = agentmap.get("followedUpcoming");
                    }

                    if (fU != null) {
                        if (!fU.isEmpty()) {
                            folup = Integer.parseInt(fU);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                folup = Integer.parseInt(fU);
                            }
                        }
                    }

                    if (nFU != null) {
                        if (!nFU.isEmpty()) {
                            nfolup = Integer.parseInt(nFU);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                nfolup = Integer.parseInt(nFU);
                            }
                        }
                    }

                    if (uP != null) {
                        if (!uP.isEmpty()) {
                            folupUp = Integer.parseInt(uP);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                folupUp = Integer.parseInt(uP);
                            }
                        }
                    }

                    value += folup + nfolup + folupUp;
                    //Log.d("Agent in flp", ""+folup);
                    //Log.d("Agent in nFlp", ""+nfolup);
                    //Log.d("Agent in flpUp", ""+folupUp);

                    if (userEmail.equals(agentmap.get("agent"))) {
                        hValue = value;
                        return value;
                    }
                    break;
                case 3:
                    String oP = agentmap.get("orderPlaced");
                    String pP = agentmap.get("rest");
                    String iD = agentmap.get("invalid");
                    String lst = agentmap.get("lost");
                    String nI = agentmap.get("notInterested");

                    if (oP != null) {
                        if (!oP.isEmpty()) {
                            oPlaced += Integer.parseInt(oP);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                oPlaced = Integer.parseInt(oP);
                            }
                        }
                    }

                    if (pP != null) {
                        if (!pP.isEmpty()) {
                            pLine += Integer.parseInt(pP);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                pLine = Integer.parseInt(pP);
                            }
                        }
                    }

                    if (iD != null) {
                        if (!iD.isEmpty()) {
                            iLid += Integer.parseInt(iD);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                iLid = Integer.parseInt(iD);
                            }
                        }
                    }

                    if (lst != null) {
                        if (!lst.isEmpty()) {
                            lot += Integer.parseInt(lst);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                lot = Integer.parseInt(lst);
                            }
                        }
                    }

                    if (nI != null) {
                        if (!nI.isEmpty()) {
                            notI += Integer.parseInt(nI);
                            if (userEmail.equalsIgnoreCase(agentmap.get("agent"))) {
                                notI = Integer.parseInt(nI);
                            }
                        }
                    }

                    value += oPlaced + pLine + iLid + lot + notI;

                    if (userEmail.equals(agentmap.get("agent"))) {
                        hValue = value;
                        return value;
                    }
                    break;
            }

            if (parentMultiLevel) {
                if (agentMailId.equalsIgnoreCase(agentmap.get("agent"))) {
                    hValue = value;

                    hcld = cld;
                    hnCld = nCld;
                    hcldUp = cldUp;
                    hfolup = folup;
                    hnfolup = nfolup;
                    hfolupUp = folupUp;
                    hoPlaced = oPlaced;
                    hpLine = pLine;
                    hiLid = iLid;
                    hlot = lot;
                    hnotI = notI;
                }
            }

            boolean multiLevel = false;
            String groupId = "";
            for (int ct = memberItem; ct < JsonParseAndOperate.teamData.size(); ct++) {
                HashMap<String, String> checkMember = JsonParseAndOperate.teamData.get(ct);
                if (checkMember.get("email").equalsIgnoreCase(agentmap.get("agent"))) {
                    if (checkMember.get("multilevel").equalsIgnoreCase("1")) {
                        //Log.d("dilip dashboard", "Group: " + checkMember.get("id"));
                        //if (!checkMember.get("id").equalsIgnoreCase(checkMember.get("groupid"))) {
                        multiLevel = true;
                        // groupId = checkMember.get("id");
                        // hisID = checkMember.get("groupid");

                        groupId = checkMember.get("id");
                        if (agentList.get(agentList.size() - 1).get("agent").equalsIgnoreCase(checkMember.get("email"))) {
                            multiLevel = false;
                        }
                        //}
                        break;
                    }
                }
            }

            /*Log.d("dilip dashboard", "Multilevel: " + printUsername + " " + multiLevel
                    + " " + agentList.size());*/

            //if (!hisID.equalsIgnoreCase(groupId)) {
            if (multiLevel && !groupId.isEmpty()) {
                //Log.d("dashboard", "multilevel:" + groupId + " hid:" + hisID);
                boolean foundData = false;
                String checkMail = "";
                for (HashMap<String, String> checkMember : JsonParseAndOperate.teamData) {
                    if (!checkMember.get("email").equalsIgnoreCase(agentmap.get("agent")) && (!userEmail.equalsIgnoreCase(checkMember.get("email")))) {
                        if (groupId.equalsIgnoreCase(checkMember.get("groupid"))) {
                            //Log.d("dilip dashboard", "Head: " + agentmap.get("agent") + " new guy:" + checkMember.get("email"));
                            memberItem++;
                            foundData = true;
                            checkMail = checkMember.get("email");
                        }
                    }
                }

                if (foundData) {
                    for (HashMap<String, String> item : UserActivities.getAgentList()) {
                        if (checkMail.equalsIgnoreCase(item.get("agent"))) {
                            //Log.d("dilip dashboard", "Next agent: " + item.get("agent"));
                            return findValues(item, value, requestCode, memberItem);
                        }
                    }
                }
            }
            //}
        } else {
            //Log.d("Agent Map", "NULL");
        }

        return value;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.phoneImg) {

        }
    }

    private void recursiveTeam() {

    }
}
