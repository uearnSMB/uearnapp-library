package smarter.uearn.money.fragments.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonOperations;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.CountryCodes;
import smarter.uearn.money.utils.webservice.ServiceUserProfile;

import java.util.ArrayList;
import java.util.List;


/**
 * Created on 21/02/2017
 *
 * @author Dilip
 * @version 1.0
 */
public class SettingsSalesFragment extends Fragment implements View.OnClickListener {

    private FirebaseAnalytics mFirebaseAnalytics;

    int selected = 0;

    TextView mLastSync;
    ImageView mSyncNow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_sales, container, false);
        CommonOperations.setFragmentTitle(getActivity(), "Sales");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {
        mLastSync = view.findViewById(R.id.tv_last_sync);
        mLastSync.setText(ApplicationSettings.getPref(AppConstants.LAST_SYNC_SETTINGS, ""));

        mSyncNow = view.findViewById(R.id.ivSyncData);
        mSyncNow.setOnClickListener(this);

        String salesStages = ApplicationSettings.getPref(AppConstants.SALES_STAGES, "");
        String salesCurrency = ApplicationSettings.getPref("salescurrency", "");
        int positionSpinner = 0;
        TextView tvSalesStage = view.findViewById(R.id.tvSalesStage);
        tvSalesStage.setText(salesStages);

        Spinner spinnerCountryCodes = view.findViewById(R.id.spinnerCountryCurrency);
        List<String> list = new ArrayList<>();

        CountryCodes currencyCodes = new CountryCodes();
        String countries[] = currencyCodes.getCountryForCurrencyList();
        final String currencies[] = currencyCodes.getCurrencyForCountryList();
        for (int i = 0; i < countries.length; i++) {
            list.add(countries[i] + " " + currencies[i]);
            if (salesCurrency.equalsIgnoreCase(currencies[i])) {
                positionSpinner = i;
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner_textview);
        spinnerCountryCodes.setAdapter(dataAdapter);
        spinnerCountryCodes.setSelection(positionSpinner);

        spinnerCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selected == 0) {
                    selected = 1;
                } else {
                    ApplicationSettings.putPref(AppConstants.TEAM_CURRENCY, currencies[i]);
                    ApplicationSettings.putPref(AppConstants.SAVE_SETTINGS, true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        /*if (!ApplicationSettings.getPref(AppConstants.SUPERVISOR_EMAIL, "").equalsIgnoreCase(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""))) {*/
        /*String role = CommonUtils.getRole(ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""), getContext());*/
        if(!ApplicationSettings.getPref(AppConstants.TEAM_ROLE,"").equalsIgnoreCase("superadmin")) {
            spinnerCountryCodes.setEnabled(false);
            TextView tvLocked = view.findViewById(R.id.tvLocked);
            String administrator = tvLocked.getText().toString() + " " + ApplicationSettings.getPref(AppConstants.SUPERVISOR_EMAIL, "");
            tvLocked.setText(administrator);
            tvLocked.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivSyncData) {
            logAnalytics("Sync_Data_Sales_Settings", "ImageView");
            ServiceUserProfile.getUserSettings();
            getFragmentManager().popBackStack();
        }
    }

    private void logAnalytics(String eventName, String eventType) {
        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_22);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
        mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_22, AnalyticsBundle);
    }
}
