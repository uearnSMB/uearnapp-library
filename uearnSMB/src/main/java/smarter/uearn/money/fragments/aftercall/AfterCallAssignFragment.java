package smarter.uearn.money.fragments.aftercall;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import smarter.uearn.money.activities.AfterCallActivity;
import smarter.uearn.money.adapters.TeamAdapter;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.MySql;

public class AfterCallAssignFragment extends Fragment implements TeamAdapter.TeamClickedListener {


    Cursor mCursor;
    RecyclerView mTeamList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_after_call_assign, container, false);
        mTeamList = view.findViewById(R.id.rv_team_members);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTeamList.setLayoutManager(linearLayoutManager);

        loadTeamMembers();

        return view;
    }

    private void loadTeamMembers() {
        int count = 0;
        MySql dbHelper = MySql.getInstance(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        mCursor = db.rawQuery("SELECT * FROM TeamMembers", null);
        count = mCursor.getCount();

        TeamAdapter teamAdapter = new TeamAdapter(mCursor, this);
        mTeamList.setAdapter(teamAdapter);

        if (db.isOpen()) {
            db.close();
        }

        dbHelper.close();

        if (count == 0) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onClick(int position) {
        mCursor.moveToPosition(position);
        String teamMemberEmail = mCursor.getString(mCursor.getColumnIndex("EMAILID"));
        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_ASSIGN, teamMemberEmail);
        getActivity().onBackPressed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCursor != null) {
            mCursor.close();
        }
    }
}
