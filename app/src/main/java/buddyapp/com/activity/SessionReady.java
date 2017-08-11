package buddyapp.com.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import buddyapp.com.Controller;
import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.adapter.StartStopAdapter;
import buddyapp.com.services.GPSTracker;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.RippleMap.MapRipple;

import static buddyapp.com.R.id.map;

public class SessionReady extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback, LocationSource.OnLocationChangedListener {
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
    private HashMap<Marker, String> hashMarker = new HashMap<Marker, String>();
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_ready);

// check if GPS enabled
        gps = new GPSTracker(SessionReady.this);
        if(gps.canGetLocation()){
            userlat = gps.getLatitude();
            userlng = gps.getLongitude();
            usercamera = new LatLng(userlat,userlng); // user current location
        }else {
            gps.showSettingsAlert();

        }

      /*  Intent intent = getIntent();
        lat = intent.getStringExtra(Constants.latitude);
        lng = intent.getStringExtra(Constants.longitude);
        name = intent.getStringExtra("name");
        disatance = intent.getStringExtra("distance");
*/

        try {
            JSONObject data = new JSONObject(PreferencesUtils.getData("trainerData",getApplicationContext(),""));
            JSONObject trainerDetail = data.getJSONObject("trainer_details");
            lat = trainerDetail.getString("trainer_latitude");
            lng = trainerDetail.getString("trainer_longitude");
            name = trainerDetail.getString("trainer_first_name") + " "+trainerDetail.getString("trainer_last_name");
            Controller.mSocket.connect();
            CommonCall.socketGetTrainerLocation();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
// trainer location
        latitude = Double.valueOf(lat);
        longitude = Double.valueOf(lng);
        camera = new LatLng(latitude, longitude);

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        origin = camera;
        dest = usercamera;

/****
 * get Trainer location
  ****/
        intstartStop();
        LoadmapTask();
    }


    void intstartStop(){



    }

    @Override
    protected void onStart() {
        super.onStart();
        CommonCall.showLoader(SessionReady.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        CommonCall.PrintLog("brodcastmsg","brodcastmsg");
                        lat = intent.getStringExtra("trainer_latitude");
                        lng = intent.getStringExtra("trainer_longitude");
                        // trainer location
                        latitude = Double.valueOf(lat);
                        longitude = Double.valueOf(lng);
                        camera = new LatLng(latitude, longitude);
                        origin = camera;
                        LoadmapTask();
                        animateMarker(pos_Marker,camera,false,0.0f);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 14));

                    }
                }, new IntentFilter("SOCKET_BUDDI_TRAINER_LOCATION")


        );

    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker, final Float bearing) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(bearing);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private void LoadmapTask() {
            if(googleMap!= null)
            googleMap.clear();

            String str_origin = "origin="+origin.latitude+","+origin.longitude;
            // Destination of route
            String str_dest = "destination="+dest.latitude+","+dest.longitude;
            // Sensor enabled
            String sensor = "sensor=false";
            String mode = "mode=driving";

            String parameters = str_origin+"&"+str_dest+"&"+sensor;

            // Building the parameters to the web service
//            String parameters = origin + "&" + dest + "&" + sensor + "&" + mode;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

            FetchUrl FetchUrl = new FetchUrl();
            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);

    }

    private void showMarker() {

try{
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12));
                pos_Marker =  googleMap.addMarker(new MarkerOptions().position(camera).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).title(name.toUpperCase()).draggable(false));
                pos_Marker.showInfoWindow();

                googleMap.setInfoWindowAdapter(SessionReady.this);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

        MapRipple mapRipple = new MapRipple(googleMap,usercamera, getApplicationContext());
        mapRipple.withNumberOfRipples(3);
        mapRipple.withFillColor(getResources().getColor(R.color.login_bgcolor));
        mapRipple.withStrokeColor(Color.BLACK);
        mapRipple.withStrokewidth(1);      // 10dp
        mapRipple.withDistance(1000);      // 2000 metres radius
        mapRipple.withRippleDuration(5000);    //12000ms
        mapRipple.withTransparency(0.5f);
        mapRipple.startRippleMapAnimation();

        showMarker();
        googleMap.setMyLocationEnabled(true);



        /*googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 13));
        Marker pos_Marker =  googleMap.addMarker(new MarkerOptions().position(camera).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).title("Starting Location").draggable(false));
        pos_Marker.showInfoWindow();*/

//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.my_map_style);
//        googleMap.setMapStyle(style);
        googleMap.setInfoWindowAdapter(SessionReady.this);
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

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
   /******* DISTANCE TIME DETAILS *************************************************************/

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.e("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.e("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.e("ParserTask","Executing routes");
                Log.e("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.e("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
CommonCall.hideLoader();
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(8);
                    lineOptions.color(Color.MAGENTA);

                    Log.d("onPostExecute","onPostExecute lineoptions decoded");

                }

                // Drawing polyline in the Google Map for the i-th route
                if(lineOptions != null) {
                    googleMap.addPolyline(lineOptions);
                }
                else {
                    Log.d("onPostExecute","without Polylines drawn");
                }
            }

        }




    public class DataParser {

        /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
        public List<List<HashMap<String,String>>> parse(JSONObject jObject){

            List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for(int i=0;i<jRoutes.length();i++){
                    jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<>();

                    /** Traversing all legs */
                    for(int j=0;j<jLegs.length();j++){
                        jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for(int k=0;k<jSteps.length();k++){
                            String polyline = "";
                            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for(int l=0;l<list.size();l++){
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude) );
                                hm.put("lng", Double.toString((list.get(l)).longitude) );
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
            }


            return routes;
        }



        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }

}
