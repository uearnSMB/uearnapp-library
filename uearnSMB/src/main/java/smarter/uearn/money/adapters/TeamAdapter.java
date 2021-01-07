package smarter.uearn.money.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import smarter.uearn.money.R;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    Cursor mCursor;
    TeamClickedListener mTeamClickedListener;

    public TeamAdapter(Cursor cursor, TeamClickedListener teamClickedListener){
        this.mCursor = cursor;
        this.mTeamClickedListener = teamClickedListener;
    }

    @Override
    public TeamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_member, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamViewHolder holder, int position) {

        mCursor.moveToPosition(position);
        holder.onBind(mCursor.getString(mCursor.getColumnIndex("NAME")),
                mCursor.getString(mCursor.getColumnIndex("EMAILID")));

    }

    public interface TeamClickedListener{
        void onClick(int position);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class TeamViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mName,mEmail;

        TeamViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.tv_name);
            mEmail = itemView.findViewById(R.id.tv_email);
            itemView.setOnClickListener(this);
        }

        void onBind(String name,String email){
            mName.setText(name);
            mEmail.setText(email);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mTeamClickedListener.onClick(position);
        }
    }
}
