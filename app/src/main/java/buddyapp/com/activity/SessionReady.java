package buddyapp.com.activity;


import android.animation.ObjectAnimator;

import android.app.Dialog;
import android.app.NotificationManager;
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
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import buddyapp.com.Controller;
import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.services.GPSTracker;
import buddyapp.com.services.LocationService;
import buddyapp.com.timmer.Timer_Service;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.RippleMap.MapRipple;
import buddyapp.com.utils.Urls;


import static buddyapp.com.Controller.mSocket;
import static buddyapp.com.Controller.updateSocket;
import static buddyapp.com.R.id.map;
import static buddyapp.com.R.id.stop;
import static buddyapp.com.Settings.Constants.trainee_Data;
import static buddyapp.com.Settings.Constants.trainer_Data;

public class SessionReady extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback, LocationSource.OnLocationChangedListener {
    Marker pos_Marker;
    GoogleMap googleMap;
    GPSTracker gps;
    LatLng origin;
    LatLng dest;
    private LatLng camera, usercamera;
    Double latitude, longitude, userlat, userlng;
    LocationManager mLocationManager;
    Button select;
    String sgender, lat = "0", lng = "0", traine_id, trainer_id, duration, book_id;
    int training_time;
    String disatance, name;
    private HashMap<Marker, String> hashMarker = new HashMap<Marker, String>();
    private FusedLocationProviderClient mFusedLocationClient;


    LinearLayout start, cancel, profile, message;
    ImageView startactionIcon, stopactionIcon, profileactionIcon, messageactionIcon;
    TextView startactionTitle, stopactionTitle, profileactionTitle, messageactionTitle, sessionTimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_ready);

// check if GPS enabled
        gps = new GPSTracker(SessionReady.this);
        if (gps.canGetLocation()) {
            userlat = gps.getLatitude();
            userlng = gps.getLongitude();
            usercamera = new LatLng(userlat, userlng); // user current location
        } else {
            gps.showSettingsAlert();

        }

      /*  Intent intent = getIntent();
        lat = intent.getStringExtra(Constants.latitude);
        lng = intent.getStringExtra(Constants.longitude);
        name = intent.getStringExtra("name");
        disatance = intent.getStringExtra("distance");
*/

        try {


            if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {

                JSONObject data = new JSONObject(PreferencesUtils.getData(trainee_Data, getApplicationContext(), ""));
//                JSONObject trainerDetail = data.getJSONObject("trainer_details");
                lat = data.getString("trainee_latitude");
                lng = data.getString("trainee_longitude");
                book_id = data.getString("book_id");
                PreferencesUtils.saveData(Constants.bookid, book_id, getApplicationContext());
                traine_id = data.getString("trainee_id");
                training_time = data.getInt("training_time");
                name = data.getString("trainee_name");

                PreferencesUtils.saveData(Constants.trainee_id, traine_id, getApplicationContext());
                updateSocket();
                mSocket.connect();
                startService(new Intent(getApplicationContext(), LocationService.class));

            } else {

                JSONObject data = new JSONObject(PreferencesUtils.getData(trainer_Data, getApplicationContext(), ""));
                JSONObject trainerDetail = data.getJSONObject("trainer_details");
                lat = trainerDetail.getString("trainer_latitude");
                lng = trainerDetail.getString("trainer_longitude");
                training_time = data.getInt("training_time");
                trainer_id = data.getString("trainer_id");
                traine_id = data.getString("trainee_id");
                book_id = data.getString("book_id");
                PreferencesUtils.saveData(Constants.bookid, book_id, getApplicationContext());
                name = trainerDetail.getString("trainer_first_name") + " " + trainerDetail.getString("trainer_last_name");
                PreferencesUtils.saveData(Constants.trainee_id, traine_id, getApplicationContext());


                updateSocket();
                Controller.mSocket.connect();
                CommonCall.socketGetTrainerLocation();
            }


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

    HorizontalScrollView horizontalScrollView;

    void intstartStop() {

        start = (LinearLayout) findViewById(R.id.start);
        cancel = (LinearLayout) findViewById(R.id.cancel);
        profile = (LinearLayout) findViewById(R.id.profile);
        message = (LinearLayout) findViewById(R.id.message);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);


        startactionIcon = (ImageView) findViewById(R.id.startactionIcon);
        stopactionIcon = (ImageView) findViewById(R.id.stopactionIcon);
        profileactionIcon = (ImageView) findViewById(R.id.profileactionIcon);
        messageactionIcon = (ImageView) findViewById(R.id.messageactionIcon);


        startactionTitle = (TextView) findViewById(R.id.startactionTitle);
        stopactionTitle = (TextView) findViewById(R.id.stopactionTitle);
        profileactionTitle = (TextView) findViewById(R.id.profileactionTitle);
        messageactionTitle = (TextView) findViewById(R.id.messagectionTitle);


        sessionTimmer = (TextView) findViewById(R.id.sessionTimmer);
        ObjectAnimator animator = ObjectAnimator.ofInt(horizontalScrollView, "scrollX", 150);
        animator.setDuration(900);
        animator.start();

        sessionTimmer = (TextView) findViewById(R.id.sessionTimmer);
        if (PreferencesUtils.getData(Constants.timerstarted, getApplicationContext(), "false").equals("true")) {

            startactionTitle.setText("Stop");
        } else {
            startactionTitle.setText("Start");
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startactionTitle.getText().toString().equals("Start")) {

                    new StartSession().execute();
                    profile.setEnabled(false);
                    message.setEnabled(false);

                } else {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked

                                    Timer_Service.stopFlag = true;
                                    PreferencesUtils.saveData(Constants.timerstarted, "false", getApplicationContext());


                                    startactionTitle.setText("Start");
                                    startactionIcon.setImageResource(R.mipmap.play);

                                    PreferencesUtils.saveData("data", "", getApplicationContext());
                                    PreferencesUtils.saveData("hours", "", getApplicationContext());

                                    stopService(new Intent(getApplicationContext(), Timer_Service.class));


                                    NotificationManager nManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
                                    nManager.cancelAll();


                                    new CommonCall.timerUpdate(SessionReady.this,"complete",book_id).execute();


                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(SessionReady.this);
                    builder.setMessage("Are you sure you want to stop this session?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                }


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

// Create custom dialog object
                final Dialog dialog = new Dialog(SessionReady.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // Include dialog.xml file
                dialog.setContentView(R.layout.custom_dialog_yesno);
                // Set dialog title
                dialog.setTitle("Custom Dialog");

                // set values for custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.textDialog);
                final EditText reason = (EditText) dialog.findViewById(R.id.reason);
                text.setText("Do you want to cancel this session");


                dialog.show();

                Button declineButton = (Button) dialog.findViewById(R.id.no);
                Button yes = (Button) dialog.findViewById(R.id.yes);
                // if decline button is clicked, close the custom dialog
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (reason.getText().toString().length() > 1) {
                            dialog.dismiss();
                            new CommonCall.timerUpdate(SessionReady.this, "cancel", book_id).execute();


                        } else {


                            reason.setError("Please enter your reason to cancel.");

                        }


                    }
                });
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        dialog.dismiss();
                    }
                });


//                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case DialogInterface.BUTTON_POSITIVE:
//                                //Yes button clicked
//
//                                if (input.getText().toString().length()>2) {
//
//                                    new CommonCall.timerUpdate(SessionReady.this, "cancel", book_id).execute();
//                                    dialog.dismiss();
////                                    break;
//                                }else {
//
//                                    input.setError("Please enter your reason to cancel.");
//
//
//                                }
//
//                            case DialogInterface.BUTTON_NEGATIVE:
//                                //No button clicked
////                                break;
//                        }
//                    }
//                };
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(SessionReady.this);
//
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT);
//                input.setLayoutParams(lp);
//                builder.setView(input);
//                builder.setMessage("Do you want to cancel this session?").setPositiveButton("Yes", dialogClickListener)
//                        .setNegativeButton("No", dialogClickListener).show();


            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {
                    Intent intent = new Intent(getApplicationContext(), TraineeProfileView.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), TrainerProfileView.class);
                    startActivity(intent);
                }
            }
        });
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {

            String millisUntilFinished = intent.getStringExtra("time");
            CommonCall.PrintLog("service", "Countdown seconds remaining: " + millisUntilFinished);

            sessionTimmer.setText(millisUntilFinished);
            if (millisUntilFinished.equals("Session Completed")) {

                /*
                *
                * this will execute when timmer has complted
                *
                *
                *
                * */


                PreferencesUtils.saveData(Constants.timerstarted, "false", getApplicationContext());


                startactionTitle.setText("Start");
                startactionIcon.setImageResource(R.mipmap.play);

                PreferencesUtils.saveData("data", "", getApplicationContext());
                PreferencesUtils.saveData("hours", "", getApplicationContext());

                stopService(new Intent(getApplicationContext(), Timer_Service.class));


                NotificationManager nManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();

                //clearing last payment id to avoid multiple payments
                PreferencesUtils.saveData(Constants.transactionId,"",getApplicationContext());





                    Toast.makeText(getApplicationContext(), "Session Completed", Toast.LENGTH_SHORT).show();

                    Intent intenthome = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intenthome);
                    finish();


            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        CommonCall.showLoader(SessionReady.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        CommonCall.PrintLog("brodcastmsg", "brodcastmsg");
                        lat = intent.getStringExtra("trainer_latitude");
                        lng = intent.getStringExtra("trainer_longitude");
                        // trainer location
                        latitude = Double.valueOf(lat);
                        longitude = Double.valueOf(lng);
                        camera = new LatLng(latitude, longitude);
                        origin = camera;
                        LoadmapTask();
                        animateMarker(pos_Marker, camera, false, 0.0f);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 14));

                    }
                }, new IntentFilter("SOCKET_BUDDI_TRAINER_LOCATION")


        );
        registerReceiver(br, new IntentFilter(Timer_Service.str_receiver));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
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
        if (googleMap != null)
            googleMap.clear();

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String parameters = str_origin + "&" + str_dest + "&" + sensor;

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

        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));
            pos_Marker = googleMap.addMarker(new MarkerOptions().position(camera).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).title(name.toUpperCase()).draggable(false));
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

        MapRipple mapRipple = new MapRipple(googleMap, usercamera, getApplicationContext());
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
                Log.e("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.e("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.e("ParserTask", "Executing routes");
                Log.e("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.e("ParserTask", e.toString());
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

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                googleMap.addPolyline(lineOptions);
                showMarker();
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }

    }


    public class DataParser {

        /**
         * Receives a JSONObject and returns a list of lists containing latitude and longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<>();
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude));
                                hm.put("lng", Double.toString((list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
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

    /*********
     * Start Session
     *********/
    class StartSession extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(SessionReady.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put("book_id", book_id);
                reqData.put("trainee_id", PreferencesUtils.getData(Constants.trainee_id, getApplicationContext(), ""));

                if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer"))

                reqData.put("trainer_id", PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));
             else
                    reqData.put("trainer_id", trainer_id);


                reqData.put("user_type", PreferencesUtils.getData(Constants.user_type, getApplicationContext(), ""));
                response = NetworkCalls.POST(Urls.getStartSessionURL(), reqData.toString());
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


                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();


                    startactionTitle.setText("Stop");
                    cancel.setEnabled(false);

                    startactionIcon.setImageResource(R.mipmap.stop);


                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    String date_time = simpleDateFormat.format(calendar.getTime());


                    PreferencesUtils.saveData("data", date_time, getApplicationContext());
                    PreferencesUtils.saveData("hours", training_time + "", getApplicationContext());
//                    PreferencesUtils.saveData("hours",  "1", getApplicationContext());


                    startService(new Intent(getApplicationContext(), Timer_Service.class));


                    CommonCall.PrintLog("Service ", "Started service");
                    PreferencesUtils.saveData(Constants.timerstarted, "true", getApplicationContext());


                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                } else if (obj.getInt("status") == 3) {
                    Toast.makeText(getApplicationContext(), "Session out", Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getApplicationContext());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*********
     * --> Extend booking
     *********/
    class ExtendSession extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(SessionReady.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
//                reqData.put("book_id)", trainee_id);
//                reqData.put("extended_start_time", extended_start_time);
//                reqData.put("extended_end_time",extended_end_time);
                response = NetworkCalls.POST(Urls.getExtendSessionURL(), reqData.toString());
            } catch (Exception e) {
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
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                } else if (obj.getInt("status") == 3) {
                    Toast.makeText(getApplicationContext(), "Session out", Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getApplicationContext());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

return;

//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);
    }
}
