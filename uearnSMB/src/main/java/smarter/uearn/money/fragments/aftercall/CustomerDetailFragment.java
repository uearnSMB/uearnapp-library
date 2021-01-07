package smarter.uearn.money.fragments.aftercall;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.AfterCallActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.Validations;

public class CustomerDetailFragment extends Fragment {

    private static final String ARG_NUMBER = "number";
    private static final String ARG_ID = "customerid";
    private static final String AUTOCHECK = "autocheck";

    private String mNameOrNumber;
    private String mCustomerId;
    private boolean autoCheck = false;


    public CustomerDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 - Show number or name if record does not exist
     * @param param2 - Check local DB and load based on the data
     * @return A new instance of fragment CustomerDetailFragment.
     */
    public static CustomerDetailFragment newInstance(String param1, String param2) {
        CustomerDetailFragment fragment = new CustomerDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NUMBER, param1);
        args.putString(ARG_ID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static CustomerDetailFragment newInstance(String param1, String param2, boolean autocheck) {
        CustomerDetailFragment fragment = new CustomerDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NUMBER, param1);
        args.putString(ARG_ID, param2);
        args.putBoolean(AUTOCHECK, autocheck);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNameOrNumber = getArguments().getString(ARG_NUMBER);
            mCustomerId = getArguments().getString(ARG_ID);
            autoCheck = getArguments().getBoolean(AUTOCHECK);
        }
    }

    private WebView mMaps;
    private EditText mNumber, mEmail, mCompany, mSource, mAddress;
    private EditText mName, mDesignation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_detail, container, false);

        mMaps = view.findViewById(R.id.wv_location);

        mMaps.getSettings().setJavaScriptEnabled(true);
        mMaps.getSettings().setBuiltInZoomControls(true);
        mMaps.getSettings().setSupportZoom(true);
        mMaps.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

//        String iframe = "<iframe src='https://maps.google.com/maps?q=Bengaluru&output=embed&amp;z=12' width=300 height=200 frameborder=0 style=border:0></iframe>";
//        mMaps.loadData(iframe, "text/html", "utf-8");

        mName = view.findViewById(R.id.et_customer_name);
        mDesignation = view.findViewById(R.id.et_customer_designation);
        mNumber = view.findViewById(R.id.et_customer_phone);

        mNumber.setText(mNameOrNumber);

        mEmail = view.findViewById(R.id.et_customer_email);
        mCompany = view.findViewById(R.id.et_customer_company);
        mSource = view.findViewById(R.id.et_customer_source_of_lead);
        mAddress = view.findViewById(R.id.et_customer_address);
        if(autoCheck) {
            mSource.setVisibility(View.GONE);
            mCompany.setVisibility(View.GONE);
            mDesignation.setVisibility(View.GONE);
            mMaps.setVisibility(View.GONE);
            mAddress.setVisibility(View.GONE);
        }

        if (!ApplicationSettings.getPref(AfterCallActivity.AFTER_CALL_PHONE, "").isEmpty()) {
            setData();
        } else {
            getDetailsFromDb(mCustomerId);
        }

        return view;
    }

    /**
     * If data is already set in the application settings
     * Or if the user has edited in the same screen previously
     * Load the data from the application settings
     */
    private void setData() {
        mName.setHint("Add name");
        mDesignation.setHint("Add designation");
        mNumber.setHint("Add number");
        mEmail.setHint("Add email");
        mCompany.setHint("Add company");
        mSource.setHint("Add source of lead");
        mAddress.setHint("Add address");

        String name = ApplicationSettings.getPref(AfterCallActivity.AFTER_CALL_NAME, "");
        String number = ApplicationSettings.getPref(AfterCallActivity.AFTER_CALL_PHONE, "");
        String designation = ApplicationSettings.getPref(AfterCallActivity.AFTER_CALL_DESIGNATION, "");
        String email = ApplicationSettings.getPref(AfterCallActivity.AFTER_CALL_EMAIL, "");
        String company = ApplicationSettings.getPref(AfterCallActivity.AFTER_CALL_COMPANY, "");
        String source = ApplicationSettings.getPref(AfterCallActivity.AFTER_CALL_LEAD, "");
        String address = ApplicationSettings.getPref(AfterCallActivity.AFTER_CALL_ADDRESS, "");

        if (!name.isEmpty()) {
            mName.setText(name);
        }

        if (!number.isEmpty()) {
            mNumber.setText(number);
        }

        if (!designation.isEmpty()) {
            mDesignation.setText(designation);
        }

        if (!email.isEmpty()) {
            mEmail.setText(email);
        }

        if (!company.isEmpty()) {
            mCompany.setText(company);
        }

        if (!source.isEmpty()) {
            mSource.setText(source);
        }

        if (!address.isEmpty()) {
            mAddress.setText(address);

//            String urlAddress = address.replace(" ", "%20");
//            String iframe = "<iframe src='https://maps.google.com/maps?q=" + urlAddress + "&output=embed&amp;z=12' width=300 height=200 frameborder=0 style=border:0></iframe>";
//            mMaps.loadData(iframe, "text/html", "utf-8");
        }
    }

    /**
     * If data is being loaded for the first time
     * Check the appointment ID and load from database
     *
     * @param appointment_id - primary id for the remindertbNew
     */
    private void getDetailsFromDb(String appointment_id) {
        MySql dbHelper = MySql.getInstance(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM remindertbNew where _id=" + "'" + appointment_id + "'", null);
        int count = cursor.getCount();

        if (count > 0) {
            cursor.moveToFirst();
            String location = cursor.getString(cursor.getColumnIndex("LOCATION"));
            String callerName = cursor.getString(cursor.getColumnIndex("TONAME"));
            String designation = cursor.getString(cursor.getColumnIndex("DESIGNATION"));
            String companyName = cursor.getString(cursor.getColumnIndex("COMPANY_NAME"));
            String callerNumber = cursor.getString(cursor.getColumnIndex("TO1"));
            String emailIdString = cursor.getString(cursor.getColumnIndex("EMAILID"));
            String sourceOfLead = cursor.getString(cursor.getColumnIndex("LEAD_SOURCE"));

            mName.setHint("Add name");
            if (callerName != null) {
                if (!callerName.isEmpty()) {
                    mName.setText(callerName);
                }
            }

            mDesignation.setHint("Add designation");
            if (designation != null) {
                if (!designation.isEmpty()) {
                    mDesignation.setText(designation);
                }
            }

            mNumber.setHint("Add number");
            if (callerNumber != null) {
                if (!callerNumber.isEmpty()) {
                    mNumber.setText(callerNumber);
                }
            }

            mEmail.setHint("Add email");
            if (emailIdString != null) {
                if (!emailIdString.isEmpty()) {
                    mEmail.setText(emailIdString);
                }
            }

            mCompany.setHint("Add company");
            if (companyName != null) {
                if (!companyName.isEmpty()) {
                    mCompany.setText(companyName);
                }
            }

            mSource.setHint("Add source of lead");
            if (sourceOfLead != null) {
                if (!sourceOfLead.isEmpty()) {
                    mSource.setText(sourceOfLead);
                }
            }

            mAddress.setHint("Add address");
            if (location != null) {
                if (!location.isEmpty()) {
                    mAddress.setText(location);

//                    String urlAddress = location.replace(" ", "%20");
//                    String iframe = "<iframe src='https://maps.google.com/maps?q=" + urlAddress + "&output=embed&amp;z=12' width=300 height=200 frameborder=0 style=border:0></iframe>";
//                    mMaps.loadData(iframe, "text/html", "utf-8");
                }
            }
            //appointmentId = cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID"));
        }

        cursor.close();

        if (db.isOpen()) {
            db.close();
        }

        dbHelper.close();
    }

    /**
     * Method to store the changes made on the screen
     * in application settings preferences
     * Modification will be made on the final activity screen back
     */
    @Override
    public void onDetach() {
        super.onDetach();

        String mail = mEmail.getText().toString();
        if (Validations.isValidEmail(mail)) {
            ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_EMAIL, mEmail.getText().toString());
        } else {
            if (!mail.isEmpty()) {
                Toast.makeText(getActivity(), "Invalid email", Toast.LENGTH_SHORT).show();
            }
        }

        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NAME, mName.getText().toString());
        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_COMPANY, mCompany.getText().toString());
        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_DESIGNATION, mDesignation.getText().toString());
        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_LEAD, mSource.getText().toString());
        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_PHONE, mNumber.getText().toString());
        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_ADDRESS, mAddress.getText().toString());
    }
}
