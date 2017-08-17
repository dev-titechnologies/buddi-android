package buddyapp.com.activity.Fragment;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import buddyapp.com.R;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.services.GPSTracker;
import buddyapp.com.timmer.Timer_Service;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.RippleMap.MapRipple;

import static buddyapp.com.R.id.map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTrainerMap extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {
    Marker pos_Marker;
    GoogleMap googleMap;
    GPSTracker gps;
    LatLng origin;
    LatLng dest;
    private LatLng camera, usercamera;
    Double latitude, longitude, userlat, userlng;
    LocationManager mLocationManager;
    Button select;
    String sgender, lat, lng, category, duration;
    String disatance, name;
    boolean initalLocation = true;

    public HomeTrainerMap() {
        // Required empty public constructor

    }

    LinearLayout start, stop, profile, message;

    ImageView startactionIcon, stopactionIcon, profileactionIcon, messageactionIcon;

    TextView startactionTitle, stopactionTitle, profileactionTitle, messageactionTitle, sessionTimmer;

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

        if (PreferencesUtils.getData("Lat", getActivity(), "").length() > 0) {
            userlat = Double.valueOf(PreferencesUtils.getData("Lat", getActivity(), ""));
            userlng = Double.valueOf(PreferencesUtils.getData("Lng", getActivity(), ""));
            usercamera = new LatLng(userlat, userlng);
//            setRippleView();
        } else {
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
        intstartStop(view);
        return view;
    }

    void intstartStop(View view) {

        start = (LinearLayout) view.findViewById(R.id.start);
        stop = (LinearLayout) view.findViewById(R.id.stop);
        profile = (LinearLayout) view.findViewById(R.id.profile);
        message = (LinearLayout) view.findViewById(R.id.message);


        startactionIcon = (ImageView) view.findViewById(R.id.startactionIcon);
        stopactionIcon = (ImageView) view.findViewById(R.id.stopactionIcon);
        profileactionIcon = (ImageView) view.findViewById(R.id.profileactionIcon);
        messageactionIcon = (ImageView) view.findViewById(R.id.messageactionIcon);

        startactionTitle = (TextView) view.findViewById(R.id.startactionTitle);
        stopactionTitle = (TextView) view.findViewById(R.id.stopactionTitle);
        profileactionTitle = (TextView) view.findViewById(R.id.profileactionTitle);
        messageactionTitle = (TextView) view.findViewById(R.id.messagectionTitle);

        sessionTimmer = (TextView) view.findViewById(R.id.sessionTimmer);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startactionTitle.getText().toString().equals("Start")) {
                    startactionTitle.setText("Cancel");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    String date_time = simpleDateFormat.format(calendar.getTime());


                    PreferencesUtils.saveData("data", date_time, getActivity());
                    PreferencesUtils.saveData("hours", "1", getActivity());


                    getActivity().startService(new Intent(getActivity(), Timer_Service.class));
                    CommonCall.PrintLog("Service ", "Started service");

//                     timerService = new BroadcastService();


                } else
                    startactionTitle.setText("Cancel");


            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PreferencesUtils.saveData("data", "", getActivity());
                PreferencesUtils.saveData("hours", "", getActivity());

                getActivity().stopService(new Intent(getActivity(), Timer_Service.class));


                NotificationManager nManager = ((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();
            }
        });

    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };


    private void LoadmapTask() {
        if (googleMap != null)
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
        if (usercamera != null) {
            PreferencesUtils.saveData("Lat", String.valueOf(usercamera.latitude), getActivity());
            PreferencesUtils.saveData("Lng", String.valueOf(usercamera.longitude), getActivity());
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
        if (gps.canGetLocation() && googleMap != null) {
            userlat = gps.getLatitude();
            userlng = gps.getLongitude();
            usercamera = new LatLng(userlat, userlng); // user current location
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usercamera, 12));
            setRippleView();
        } else {
//            gps.showSettingsAlert();

        }

        CommonCall.PrintLog("service", "Registered broacast receiver");
        getActivity().registerReceiver(br, new IntentFilter(Timer_Service.str_receiver));


    }


    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {

            String millisUntilFinished = intent.getStringExtra("time");
            CommonCall.PrintLog("service", "Countdown seconds remaining: " + millisUntilFinished);

            sessionTimmer.setText(millisUntilFinished);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(br);
    }


}
