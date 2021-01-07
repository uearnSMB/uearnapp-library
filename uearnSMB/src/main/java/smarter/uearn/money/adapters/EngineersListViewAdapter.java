package smarter.uearn.money.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.webservice.JsonParseAndOperate;
import smarter.uearn.money.views.events.OnEngineerClickedListener;
import smarter.uearn.money.views.events.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EngineersListViewAdapter extends BaseAdapter {

    private Context context;
    private TextView engineer_emailid, engineer_role, engineer_name;
    private Button pending_button, accept_button, accepted_button, delete_accept_engineer_button;
    private ArrayList<GroupsUserInfo> engineersList;
    private OnEngineerClickedListener onEngineerClickedListener;

    public EngineersListViewAdapter(Context context, ArrayList<GroupsUserInfo> engineersList, OnEngineerClickedListener onEngineerClickedListener) {
        this.context = context;
        this.engineersList = engineersList;
        this.onEngineerClickedListener = onEngineerClickedListener;
    }

    @Override
    public int getCount() {
        if (engineersList != null) {
            return engineersList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return engineersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GroupsUserInfo engineerDetails = engineersList.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.engineers_list_item, parent, false);

        engineer_name = convertView.findViewById(R.id.name);
        engineer_emailid = convertView.findViewById(R.id.email_id);
        engineer_role = convertView.findViewById(R.id.role);
        pending_button = convertView.findViewById(R.id.pending_engineer_button);
        accept_button = convertView.findViewById(R.id.accept_engineer_button);
        accepted_button = convertView.findViewById(R.id.accepted_engineer_button);
        delete_accept_engineer_button = convertView.findViewById(R.id.deleteAccept_engineer_button);
        convertView = setEmailIdView(engineerDetails, convertView);
        convertView = setButtonView(engineerDetails, convertView);
        convertView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onLongClick() {
                onEngineerClickedListener.onLongClick(engineerDetails);
            }
        });
        delete_accept_engineer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEngineerClickedListener.onDeleteAcceptClicked(engineerDetails);
            }
        });
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEngineerClickedListener.onButtonClicked(engineerDetails);
            }
        });

        return convertView;
    }

    public View setEmailIdView(GroupsUserInfo engineerDetails, View view) {

        if (engineerDetails != null && engineerDetails.email != null) {
            engineer_emailid.setText(engineerDetails.email);

            for (HashMap<String, String> member : JsonParseAndOperate.teamData) {
                if (engineerDetails.email.equalsIgnoreCase(member.get("email"))) {
                    if (member.containsKey("role")) {
                        engineer_role.setText("" + member.get("role"));
                    }
                }
            }
        }

        if (engineerDetails != null && engineerDetails.name != null) {
            engineer_name.setText(""+ engineerDetails.name);
        }

        return view;
    }

    public View setButtonView(GroupsUserInfo groupsUserInfo, View view) {
        if (groupsUserInfo.status.equals("deleted")) {

        }
        if (groupsUserInfo.status.equals("pending delete")) {
            pending_button.setVisibility(View.GONE);
            accept_button.setVisibility(View.GONE);
            delete_accept_engineer_button.setVisibility(View.VISIBLE);
        } else if (groupsUserInfo.request.equals("invite")) {
            pending_button.setVisibility(View.VISIBLE);
        } else if (groupsUserInfo.request.equals("invited")) {
            pending_button.setVisibility(View.GONE);
            accept_button.setVisibility(View.VISIBLE);
        } else if (groupsUserInfo.request.equals("accepted")) {
            pending_button.setVisibility(View.GONE);
            accepted_button.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public void notifyDataSetChanged(ArrayList<GroupsUserInfo> engineersList) {
        this.engineersList = engineersList;
        notifyDataSetChanged();
    }
}
