package buddyapp.com.activity.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.RippleMap.MapRipple;
import buddyapp.com.utils.Urls;

import static android.content.Context.LOCATION_SERVICE;
import static buddyapp.com.R.id.map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Map_Trainee extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, LocationSource.OnLocationChangedListener {
    GoogleMap googleMap;
    private LatLng camera;
    Double latitude, longitude;
    LocationManager mLocationManager;
    Button select;
    String sgender,lat, lng, category,duration;
    private HashMap<Marker, String> hashMarker = new HashMap<Marker, String>();
    public Map_Trainee() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map__trainee, container, false);

        select = (Button) view.findViewById(R.id.select);

        sgender = getArguments().getString(Constants.gender);
        category = getArguments().getString("category");
        lat = getArguments().getString(Constants.latitude);
        lng = getArguments().getString(Constants.longitude);
        duration = getArguments().getString(Constants.duration);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RandomSelect().execute();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        latitude = Double.valueOf(PreferencesUtils.getData(Constants.latitude,getActivity(),""));
        longitude = Double.valueOf(PreferencesUtils.getData(Constants.longitude,getActivity(),""));
        camera = new LatLng(latitude, longitude);

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void getTrainerMarker() {
        String array = PreferencesUtils.getData("searchArray",getActivity(),"");
        try {
            JSONArray jsonarray = new JSONArray(array);
            showMarker(jsonarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void showMarker(JSONArray places) {

        try {
            for (int k = 0; k < places.length(); k++) {
                JSONObject place = places.getJSONObject(k);

//                Marker marker = googleMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(Double.parseDouble(place.getString("latitude")), Double.parseDouble(place.getString("longitude"))))
//                        .title(place.getString("name"))

//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.place_marker))

//                        .snippet(place.getString("availability_status")));

//                hashMarker.put(marker, place.getString("image"));


                // mMap is GoogleMap object, latLng is the location on map from which ripple should start

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(place.getString("latitude")), Double.parseDouble(place.getString("longitude"))), 13));
                Marker pos_Marker =  googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(place.getString("latitude")), Double.parseDouble(place.getString("longitude")))).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).title("Trainer").draggable(false));
                pos_Marker.showInfoWindow();

                googleMap.setInfoWindowAdapter(Map_Trainee.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

                MapRipple mapRipple = new MapRipple(googleMap,camera, getActivity());
                mapRipple.withNumberOfRipples(3);
                mapRipple.withFillColor(getResources().getColor(R.color.login_bgcolor));
                mapRipple.withStrokeColor(Color.BLACK);
                mapRipple.withStrokewidth(1);      // 10dp
                mapRipple.withDistance(1000);      // 2000 metres radius
                mapRipple.withRippleDuration(5000);    //12000ms
                mapRipple.withTransparency(0.5f);
                mapRipple.startRippleMapAnimation();

//************ Trainer *******
        getTrainerMarker();
        googleMap.setMyLocationEnabled(true);

        /*googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 13));
        Marker pos_Marker =  googleMap.addMarker(new MarkerOptions().position(camera).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).title("Starting Location").draggable(false));
        pos_Marker.showInfoWindow();*/

//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.my_map_style);
//        googleMap.setMapStyle(style);
        googleMap.setInfoWindowAdapter(Map_Trainee.this);
    }
    @Override
    public void onLocationChanged(Location location) {

        googleMap.clear();

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

        mp.title("my position");

        googleMap.addMarker(mp);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));

    }

    class RandomSelect extends AsyncTask<String,String,String>{
        String response;
        JSONObject reqData = new JSONObject();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(getActivity());

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.user_id,PreferencesUtils.getData(Constants.user_id,getActivity(),""));
                reqData.put(Constants.gender,sgender);
                reqData.put("category",category);
                reqData.put(Constants.latitude,lat);
                reqData.put(Constants.longitude,lng);
                reqData.put(Constants.duration,duration);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            response = NetworkCalls.POST(Urls.getRandomSelectURL(),reqData.toString());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    JSONObject jsonObject = obj.getJSONObject("data");
//                    PreferencesUtils.saveData("TrainerData",jsonObject.toString(),getActivity());
                    Fragment fragment = new TrainerProfileView();
                    Bundle args = new Bundle();
                    args.putString("TrainerData",jsonObject.toString());
                    fragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
