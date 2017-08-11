package buddyapp.com.activity.Fragment;


import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import buddyapp.com.R;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.SessionReady;
import buddyapp.com.services.GPSTracker;
import buddyapp.com.utils.RippleMap.MapRipple;

import static buddyapp.com.R.id.map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTrainerMap extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {
    Marker pos_Marker;
    GoogleMap googleMap;
    GPSTracker gps ;
    LatLng origin;
    LatLng dest;
    private LatLng camera,usercamera;
    Double latitude, longitude, userlat,userlng;
    LocationManager mLocationManager;
    Button select;
    String sgender,lat, lng, category,duration;
    String  disatance,name;
    boolean initalLocation = true;
    public HomeTrainerMap() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_trainer_map, container, false);
        ToggleButton toggle = (ToggleButton) view.findViewById(R.id.togglebutton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                }
            }
        });

        if(PreferencesUtils.getData("Lat",getActivity(),"").length()>0){
            userlat = Double.valueOf(PreferencesUtils.getData("Lat",getActivity(),""));
            userlng = Double.valueOf(PreferencesUtils.getData("Lng",getActivity(),""));
            usercamera = new LatLng(userlat, userlng);
        }else {
            // check if GPS enabled
            gps = new GPSTracker(getActivity());
            if (gps.canGetLocation()) {
                userlat = gps.getLatitude();
                userlng = gps.getLongitude();
                usercamera = new LatLng(userlat, userlng); // user current location
            } else {
                gps.showSettingsAlert();

            }
        }
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        LoadmapTask();

        return view;
    }

    private void LoadmapTask() {
        if(googleMap!= null)
            googleMap.clear();


    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        if(usercamera!=null) {
            PreferencesUtils.saveData("Lat", String.valueOf(usercamera.latitude),getActivity());
            PreferencesUtils.saveData("Lng", String.valueOf(usercamera.longitude),getActivity());
            setRippleView();

        }
    }

    private void setRippleView() {
        MapRipple mapRipple = new MapRipple(googleMap, usercamera, getActivity());
        mapRipple.withNumberOfRipples(3);
        mapRipple.withFillColor(getResources().getColor(R.color.login_bgcolor));
        mapRipple.withStrokeColor(Color.BLACK);
        mapRipple.withStrokewidth(1);      // 10dp
        mapRipple.withDistance(1000);      // 2000 metres radius
        mapRipple.withRippleDuration(5000);    //12000ms
        mapRipple.withTransparency(0.5f);
        mapRipple.startRippleMapAnimation();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usercamera, 12));
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        gps = new GPSTracker(getActivity());
        if(gps.canGetLocation()&& googleMap!=null){
            userlat = gps.getLatitude();
            userlng = gps.getLongitude();
            usercamera = new LatLng(userlat,userlng); // user current location
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usercamera, 12));
            setRippleView();
        }else {
//            gps.showSettingsAlert();

        }

    }
}
