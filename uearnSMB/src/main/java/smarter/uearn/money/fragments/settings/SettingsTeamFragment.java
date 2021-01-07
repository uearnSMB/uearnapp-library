package smarter.uearn.money.fragments.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;

/**
 * Created on 21/02/2017
 * @author Dilip
 * @Version 1.0
 */

public class SettingsTeamFragment extends Fragment {

    TextView tvSupervisor, tvMail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_team, container, false);
        CommonOperations.setFragmentTitle(getActivity(),"Team");

        initialize(view);

        return view;
    }

    private void initialize(View view){
        tvSupervisor = view.findViewById(R.id.tvSupervisor);
        tvMail = view.findViewById(R.id.tvMail);

        ServiceUserProfile.getUsersTeam(getActivity());
    }

}
