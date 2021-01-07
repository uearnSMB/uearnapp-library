package smarter.uearn.money.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import smarter.uearn.money.R;

/**
 * Created by Srinath on 25-01-2017.
 */

public class LeadsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.leads, container, false);
        initialiseUi(view);
        return view;
    }

    private void initialiseUi(View view) {
        TextView accept_all = view.findViewById(R.id.acceptAllTv);
        accept_all.setText("Awesome!" +"\n" + "You have accepted all leads.");
    }
}
