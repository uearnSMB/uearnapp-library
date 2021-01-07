package smarter.uearn.money.activities.team;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIAdapter;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.Validation;

public class AddTeamMemberActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAnalytics mFirebaseAnalytics;

    Button btnContactPicker, btnCreateMember;
    EditText etEmailAddress, etName, etPhoneNumber;
    private static final int PICK_CONTACT = 112;
    TextView countryText;
    EditText etPincode, etCity, etRegion, etState;
    private TextInputLayout tipNameLayout, tipEmailLayout, tipPhoneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_add_team_member);
        changeStatusBarColor(this);
        restoreActionBar("Add Team Member");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        etEmailAddress = findViewById(R.id.et_email);
        etName = findViewById(R.id.et_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        countryText = findViewById(R.id.tv_country);

        tipNameLayout = findViewById(R.id.tip_name);
        tipPhoneLayout = findViewById(R.id.tip_phone);
        tipEmailLayout = findViewById(R.id.tip_mail);

        etPincode = findViewById(R.id.et_pincode);
        etPincode.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logAnalytics("Pincode", "EditText");
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);

                    String pincode = etPincode.getText().toString();
                    if (!pincode.isEmpty()) {
                        getLocationFromAddress(pincode);
                    }
                    return true;
                }
                return false;
            }
        });

        etCity = findViewById(R.id.et_city);
        etCity.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logAnalytics("City", "EditText");
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);
                    String city = etCity.getText().toString();
                    if (!city.isEmpty()) {
                        getLocationFromAddress(city);
                    }
                    return true;
                }
                return false;
            }
        });

        etRegion = findViewById(R.id.et_region);
        etRegion.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logAnalytics("region", "EditText");
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, 0);
                    String region = etRegion.getText().toString();
                    if (!region.isEmpty()) {
                        getLocationFromAddress(region);
                    }
                    return true;
                }
                return false;
            }
        });
        etState = findViewById(R.id.et_state);

        btnContactPicker = findViewById(R.id.btn_contact_picker);
        assert btnContactPicker != null;
        btnContactPicker.setOnClickListener(this);
        btnCreateMember = findViewById(R.id.btn_create_member);
        assert btnCreateMember != null;
        btnCreateMember.setOnClickListener(this);
    }

    public void getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            getAddressFromLocation(location.getLatitude(), location.getLongitude());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }


    public void getAddressFromLocation(final double latitude, final double longitude) {
        String pincode, country, city, region, state;

        Geocoder geocoder = new Geocoder(AddTeamMemberActivity.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                city = address.getLocality();
                pincode = address.getPostalCode();
                country = address.getCountryName();
                region = address.getSubLocality();
                state = address.getAdminArea();

                etState.setText(state);
                etCity.setText(city);
                etPincode.setText(pincode);
                etRegion.setText(region);
                countryText.setText(country);
            }
        } catch (IOException e) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        String userMail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");

        int id = v.getId();
        if (id == R.id.btn_contact_picker) {
            logAnalytics("pick_contacts", "Button");
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, PICK_CONTACT);
        } else if (id == R.id.btn_create_member) {
            logAnalytics("create_team_member", "Button");
            String country, pincode, city, region, state, name, email, phone;
            country = countryText.getText().toString();
            pincode = etPincode.getText().toString();
            city = etCity.getText().toString();
            region = etRegion.getText().toString();
            state = etState.getText().toString();

            name = etName.getText().toString();
            phone = etPhoneNumber.getText().toString();
            email = etEmailAddress.getText().toString();
            tipNameLayout.setError("");
            tipPhoneLayout.setError("");
            tipEmailLayout.setError("");

            if (name.isEmpty()) {
                tipNameLayout.setError("Please Enter valid name");
                return;
            } else if (!Validation.isEmailAddress(etEmailAddress, true, "Invalid email Address")) {
                tipEmailLayout.setError("Please Enter Valid Email Id");
                return;
            } else if (phone.length() < 8) {
                tipPhoneLayout.setError("Please Enter valid phone number");
                return;
            }

            if (CommonUtils.isNetworkAvailable(this)) {
                if (email.equalsIgnoreCase(userMail)) {
                    tipEmailLayout.setError("You cannot add your own email ID");
                } else {
                    doSave(name, "engineer", email, phone, pincode, region, city, state, country);
                }
            } else {
                Toast.makeText(this, "You have no Internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doSave(String name, String dependent_type, String email, String phone, String pincode, String region, String city, String state, String country) {
        ArrayList<GroupsUserInfo> groupsUserInfoArrayList = new ArrayList<>();
        GroupsUserInfo groupUser = new GroupsUserInfo(name, email, dependent_type, phone,"invite", "invitation sent", pincode, state, city, region, country);
        groupsUserInfoArrayList.add(groupUser);
        setGroupsOfUser(groupsUserInfoArrayList, "Sending Invitation");
    }

    private void setGroupsOfUser(ArrayList<GroupsUserInfo> groupsArrayList, String message) {
        new APIProvider.Set_GroupsOfUser(groupsArrayList, 1, this, message, new API_Response_Listener<String>() {
            @Override
            public void onComplete(String data, long request_code, int failure_code) {
                if (data != null) {
                    Toast.makeText(AddTeamMemberActivity.this, "Updated Changes", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else if (failure_code == APIProvider.EMAIL_ALREADY_EXISTS) {
                    tipEmailLayout.setError("Email has already been taken");
                } else if (failure_code == APIProvider.NUMBER_ALREADY_EXISTS) {
                    tipPhoneLayout.setError("Registration with this phonenumber already exists");
                } else if (failure_code != APIAdapter.CONNECTION_FAILED) {
                    Toast.makeText(AddTeamMemberActivity.this, "failed to assign new team member. \n Please check you mail id.", Toast.LENGTH_SHORT).show();
                }
            }
        }).call();
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (reqCode) {
                case PICK_CONTACT:

                    etName.setText("");
                    etEmailAddress.setText("");
                    etPhoneNumber.setText("");
                    Cursor cursor = null;
                    String email, name, phone;
                    try {
                        Uri result = data.getData();
                        String id = result.getLastPathSegment();

                        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",new String[]{id}, null);

                        if (phoneCursor != null) {
                            if (phoneCursor.moveToFirst()) {
                                phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if (phone != null) {
                                    if (!phone.isEmpty()) {
                                        phone = phone.replace(" ", "");
                                        etPhoneNumber.setText(phone);
                                    }
                                }
                            }
                        }

                        if (phoneCursor != null) {
                            phoneCursor.close();
                        }

                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",new String[]{id},null);
                        int nameId = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);


                        if (cursor.moveToFirst()) {
                            email = cursor.getString(emailIdx);
                            name = cursor.getString(nameId);

                            if (email != null) {
                                if (!email.isEmpty()) {
                                    etEmailAddress.setText(cursor.getString(emailIdx));
                                }
                            }

                            if (name != null) {
                                if (!name.isEmpty()) {
                                    etName.setText(name);
                                }
                            }
                        }
                    } catch (Exception e) {

                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    break;
            }
        }
    }

    private void logAnalytics(String eventName, String eventType) {
        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_16);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
        mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_16, AnalyticsBundle);
    }
}
