package buddyapp.com.activity.Fragment;


import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.ForgotPassword;
import buddyapp.com.activity.SessionReady;
import buddyapp.com.adapter.StartStopAdapter;
import buddyapp.com.services.GPSTracker;
import buddyapp.com.services.LocationService;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.RippleMap.MapRipple;
import buddyapp.com.utils.Urls;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static buddyapp.com.Controller.mSocket;
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
    ToggleButton toggle;

    public HomeTrainerMap() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_trainer_map, container, false);
        toggle = (ToggleButton) view.findViewById(R.id.togglebutton);

        if (PreferencesUtils.getData(Constants.availStatus, getActivity(), "").equals("online")) {
            toggle.setChecked(true);
        }

//  checking online or offline
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (PreferencesUtils.getData(Constants.token, getActivity(), "").length() > 0 &&
                            PreferencesUtils.getData(Constants.user_type, getActivity(), "").equals(Constants.trainer) &&
                            PreferencesUtils.getData(Constants.availStatus, getActivity(), "").equals("online")) {
                        mSocket.connect();
                        getActivity().startService(new Intent(getActivity(), LocationService.class));
                        new updateStatus().execute();

                    }
                } else {
                    PreferencesUtils.saveData(Constants.availStatus, "offline", getActivity());
                    mSocket.disconnect();
                    getActivity().stopService(new Intent(getActivity(), LocationService.class));
                    Toast.makeText(getActivity(), "You are now Offline", Toast.LENGTH_SHORT).show();
                    // The toggle is disabled
                }
            }
        });
/****
 * Trainer location *************************************
 */
        if (PreferencesUtils.getData("Lat", getActivity(), "").length() > 0) {
            userlat = Double.valueOf(PreferencesUtils.getData("Lat", getActivity(), ""));
            userlng = Double.valueOf(PreferencesUtils.getData("Lng", getActivity(), ""));
            usercamera = new LatLng(userlat, userlng);

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


        DiscreteScrollView scrollView = (DiscreteScrollView) view.findViewById(R.id.picker);

        ArrayList<String> items = new ArrayList<>();
        items.add("start");
        items.add("stop");
        items.add("start");
        items.add("stop");
        items.add("start");
        items.add("stop");
        scrollView.setAdapter(new StartStopAdapter(items));
    }


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


    }

    /*********
     * update trainer status -------
     *********/
    class updateStatus extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(getActivity());
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getActivity(), ""));
                reqData.put(Constants.user_type, PreferencesUtils.getData(Constants.user_type, getActivity(), ""));
                reqData.put(Constants.availStatus, PreferencesUtils.getData(Constants.availStatus, getActivity(), ""));
                response = NetworkCalls.POST(Urls.getStatusURL(), reqData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                CommonCall.hideLoader();
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    JSONObject data = obj.getJSONObject("data");

                    Toast.makeText(getActivity(), "You are now Online", Toast.LENGTH_SHORT).show();
                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                }else if (obj.getInt("status") == 3) {
                    Toast.makeText(getActivity(), "Session out", Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getActivity());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}