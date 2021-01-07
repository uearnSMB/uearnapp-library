package smarter.uearn.money.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.UserActivities;
import smarter.uearn.money.utils.webservice.JsonParseAndOperate;

import java.util.ArrayList;
import java.util.HashMap;

public class UserLocationReport extends BaseActivity implements OnMapReadyCallback{

    private UserActivities userActivities;
    GoogleMap mMap;
    ArrayList<HashMap<String, String>> locationlist;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommonUtils.allowScreenshot()){

        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.location_map);
        getUserLocation();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    private void getUserLocation() {
        try {
            String user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID,"");
            String requiredStartTime = getIntent().getStringExtra("start");
            String requiredEndTime = getIntent().getStringExtra("end");

            userActivities = new UserActivities(user_id, requiredStartTime, requiredEndTime);
            new APIProvider.GetUserLocation(userActivities, 1, new API_Response_Listener<ArrayList<HashMap<String, String>>>() {

                @Override
                public void onComplete(ArrayList<HashMap<String, String>> data, long request_code, int failure_code) {
                    if (data != null) {
                        locationlist = data;
                        mapFragment.getMapAsync(UserLocationReport.this);
                    }
                }
            }).call();
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(locationlist != null && !locationlist.isEmpty()) {
            for (int i =1; i< locationlist.size(); i++) {
                HashMap<String, String> map = locationlist.get(i);
                double userLat = 0, userLong = 0;
                if(map.get("userlat") != null && !(map.get("userlat").isEmpty())) {
                 userLat = Double.parseDouble(map.get("userlat"));
                }
                if(map.get("userlong") != null && !(map.get("userlong").isEmpty())) {
                    userLong = Double.parseDouble(map.get("userlong"));
                }
                LatLng latLng = new LatLng(userLat, userLong);
                String email = ""; float marker = 0f;
                if(map.get("usermail") != null) {
                    email = map.get("usermail");
                    marker = getColorCode(email);
                }

                mMap.addMarker(new MarkerOptions().position(latLng).title(email).icon(BitmapDescriptorFactory.defaultMarker(marker)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                builder.include(latLng);
            }
        }
    }

    private float getColorCode(String email) {

        ArrayList<HashMap<String, String>> list = JsonParseAndOperate.teamData;
        float value = 0f;
        for(int i =0; i< list.size();i++) {
            HashMap<String, String> checkMember = list.get(i);
            if(checkMember.get("email").equalsIgnoreCase(email)) {
                if (i <= 8) {
                    switch (i) {
                        case 0:
                            value = BitmapDescriptorFactory.HUE_ORANGE;
                        break;
                        case 1:
                            value = BitmapDescriptorFactory.HUE_YELLOW;
                        break;
                        case 2:
                            value = BitmapDescriptorFactory.HUE_MAGENTA;
                        break;
                        case 3:
                            value = BitmapDescriptorFactory.HUE_GREEN;
                        break;
                        case 4:
                           value = BitmapDescriptorFactory.HUE_CYAN;
                        break;
                        case 5:
                           value = BitmapDescriptorFactory.HUE_BLUE;
                        break;
                        case 6:
                            value = BitmapDescriptorFactory.HUE_AZURE;
                        break;
                        case 7:
                            value = BitmapDescriptorFactory.HUE_ROSE;
                        break;
                        case 8:
                            value = BitmapDescriptorFactory.HUE_VIOLET;
                        break;
                    }
                } else {
                    value = BitmapDescriptorFactory.HUE_RED;
                }
            }
        }
        return value;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        restoreActionBar("Gps Tracker");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
