package buddyapp.com.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.api.Json;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.Payments.PaymentType;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.RippleMap.MapRipple;
import buddyapp.com.utils.Urls;

import static buddyapp.com.R.id.map;
import static buddyapp.com.Settings.Constants.start_session;
import static buddyapp.com.Settings.Constants.trainer_Data;

public class MapTrainee extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback, LocationSource.OnLocationChangedListener {
    GoogleMap googleMap;
    private LatLng camera;
    Double latitude, longitude;
    LocationManager mLocationManager;
    Button select;
    String sgender, lat, lng, category, duration, pick_latitude, pick_longitude, pick_location;
    private HashMap<Marker, String> hashMarker = new HashMap<Marker, String>();

    AVLoadingIndicatorView avi;

    int resultPayment = 403;

    @Override
    public void onBackPressed() {


        if (avi.getVisibility()==View.VISIBLE){

            return ;
        }else{

            super.onBackPressed();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_trainee);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        select = (Button) findViewById(R.id.select);

        Intent intent = getIntent();
        sgender = intent.getStringExtra(Constants.gender);
        category = intent.getStringExtra("category");
        lat = intent.getStringExtra(Constants.latitude);
        lng = intent.getStringExtra(Constants.longitude);
        duration = intent.getStringExtra(Constants.duration);
        pick_latitude = intent.getStringExtra("pick_latitude");
        pick_longitude = intent.getStringExtra("pick_longitude");
        pick_location = intent.getStringExtra("pick_location");
        avi = (AVLoadingIndicatorView) findViewById(R.id.aviloader);
        avi.setVisibility(View.VISIBLE);
        select.setClickable(false);
        if (PreferencesUtils.getData(Constants.instant_booking, getApplicationContext(), "false").equals("true")) {
            pick_location = PreferencesUtils.getData(Constants.settings_address_name, getApplicationContext(), "");
            pick_latitude = PreferencesUtils.getData(Constants.settings_latitude, getApplicationContext(), "");
            pick_longitude = PreferencesUtils.getData(Constants.settings_longitude, getApplicationContext(), "");
            PreferencesUtils.saveData(Constants.instant_booking, "false", getApplicationContext());
        }

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {

                    if (PreferencesUtils.getData(Constants.clientToken, getApplicationContext(), "").length() > 1) {

                        if (PreferencesUtils.getData(Constants.promo_code, getApplicationContext(), "").length() == 0) {

                            intPayment();
                        } else {

                            new RandomSelect().execute();
                        }
                    } else {

                        Intent payment = new Intent(getApplicationContext(), PaymentType.class);
                        payment.putExtra("result", true);
                        startActivityForResult(payment, resultPayment);
                    }
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        latitude = Double.valueOf(PreferencesUtils.getData(Constants.latitude, getApplicationContext(), ""));
        longitude = Double.valueOf(PreferencesUtils.getData(Constants.longitude, getApplicationContext(), ""));
        camera = new LatLng(latitude, longitude);

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

    }


    void intPayment() {
        avi.setVisibility(View.VISIBLE);
        select.setClickable(false);
        /*
*
*
* getting payment
*
* */
//        CommonCall.showLoader(MapTrainee.this);

        DropInResult.fetchDropInResult(MapTrainee.this, PreferencesUtils.getData(Constants.clientToken, getApplicationContext(), ""), new DropInResult.DropInResultListener() {
            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }

            @Override
            public void onResult(DropInResult result) {


                if (result.getPaymentMethodNonce() == null) {

                    Intent payment = new Intent(getApplicationContext(), PaymentType.class);
                    payment.putExtra("result", true);
                    startActivityForResult(payment, resultPayment);


                } else {
                    final String nounce = result.getPaymentMethodNonce().getNonce();

                    if (PreferencesUtils.getData(Constants.transactionId, getApplicationContext(), "").length() > 1) {


                        if (PreferencesUtils.getData(Constants.duration, getApplicationContext(), "").equals(duration))


                            new RandomSelect().execute();
                        else {

                            avi.setVisibility(View.GONE);


                            AlertDialog.Builder builder;


                            builder = new AlertDialog.Builder(MapTrainee.this);

                            builder.setCancelable(false);
                            if (PreferencesUtils.getData(Constants.duration, getApplicationContext(), "").equals("40") && duration.equals("60"))
                                builder.setMessage("You've already been paid for a 40 minutes session. If you proceed, 1 hour session amount will be deducted. Would you like to continue with 1 hour session ?");
                            else
                                builder.setMessage("You've already been paid for a 1 hour session. If you proceed, 40 minutes session amount will be deducted. Would you like to continue with 40 minute session ?");


                            builder.setTitle("Warning!")


                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue  with delete
                                            avi.setVisibility(View.VISIBLE);
PreferencesUtils.saveData(Constants.transactionId,"",getApplicationContext());
                                            intPayment();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                            avi.setVisibility(View.GONE);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();


                        }

                    } else {


                        new checkout(MapTrainee.this, nounce).execute();
                    }


                }

            }
        });
    }

    private void getTrainerMarker() {
        String array = PreferencesUtils.getData("searchArray", getApplicationContext(), "");
        try {
            JSONArray jsonarray = new JSONArray(array);
            showMarker(jsonarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void showMarker(final JSONArray places) {
        avi.setVisibility(View.GONE);
        select.setClickable(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 14), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                for (int k = 0; k < places.length(); k++) {
                    JSONObject place = null;
                    try {
                        place = places.getJSONObject(k);


//                Marker marker = googleMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(Double.parseDouble(place.getString("latitude")), Double.parseDouble(place.getString("longitude"))))
//                        .title(place.getString("name"))

//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.place_marker))

//                        .snippet(place.getString("availability_status")));

//                hashMarker.put(marker, place.getString("image"));


                        // mMap is GoogleMap object, latLng is the location on map from which ripple should start

                        Marker pos_Marker = null;

                        pos_Marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(place.getString("latitude")), Double.parseDouble(place.getString("longitude")))).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).title("Trainer").draggable(false));
                        pos_Marker.showInfoWindow();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    googleMap.setInfoWindowAdapter(MapTrainee.this);
                }
            }

            @Override
            public void onCancel() {

            }
        });
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

        MapRipple mapRipple = new MapRipple(googleMap, camera, getApplicationContext());
        mapRipple.withNumberOfRipples(1);
        mapRipple.withFillColor(getResources().getColor(R.color.login_bgcolor));
        mapRipple.withStrokeColor(Color.BLACK);
        mapRipple.withStrokewidth(1);      // 10dp
        mapRipple.withDistance(300);      // 2000 metres radius
        mapRipple.withRippleDuration(5000);    //12000ms
        mapRipple.withTransparency(0.9f);
        mapRipple.startRippleMapAnimation();

        getTrainerMarker();
        googleMap.setMyLocationEnabled(true);

        /*googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 13));
        Marker pos_Marker =  googleMap.addMarker(new MarkerOptions().position(camera).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).title("Starting Location").draggable(false));
        pos_Marker.showInfoWindow();*/

//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.my_map_style);
//        googleMap.setMapStyle(style);
        googleMap.setInfoWindowAdapter(MapTrainee.this);


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

    public class RandomSelect extends AsyncTask<String, String, String> {
        String response;
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            avi.setVisibility(View.VISIBLE);
            select.setClickable(false);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put("trainee_id", PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));
                reqData.put(Constants.gender, sgender);
                reqData.put("category", category);
                reqData.put(Constants.latitude, lat);
                reqData.put(Constants.longitude, lng);
                reqData.put(Constants.amount, PreferencesUtils.getData(Constants.amount, getApplicationContext(), ""));
                reqData.put(Constants.transaction_status, PreferencesUtils.getData(Constants.transaction_status, getApplicationContext(), ""));
                reqData.put("transaction_id", PreferencesUtils.getData(Constants.transactionId, getApplicationContext(), ""));
                reqData.put("training_time", duration);
                reqData.put("pick_latitude", pick_latitude);
                reqData.put("pick_longitude", pick_longitude);
                reqData.put("pick_location", pick_location);
                reqData.put("promocode", PreferencesUtils.getData(Constants.promo_code, getApplicationContext(), ""));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            response = NetworkCalls.POST(Urls.getRandomSelectURL(), reqData.toString());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {

//                    timeOut(obj.getInt("length"));
                    PreferencesUtils.saveData(Constants.promo_code, "", getApplicationContext());

//                    CommonCall.hideLoader();
//
//                    JSONObject jsonObject = obj.getJSONObject("data");
//                    PreferencesUtils.saveData(Constants.trainer_id,jsonObject.getString("trainer_id"),getApplicationContext());
//                    PreferencesUtils.saveData(trainer_Data,jsonObject.toString(),getApplicationContext());
//
//                    PreferencesUtils.saveData(start_session,"true",getApplicationContext());
//
//                    Intent intent = new Intent(getApplicationContext(),SessionReady.class);
//                    intent.putExtra("TrainerData",jsonObject.toString());
//                    startActivity(intent);


                } else if (obj.getInt("status") == 2) {
                    avi.setVisibility(View.GONE);
                    select.setClickable(true);
//                    CommonCall.hideLoader();
                    Toast.makeText(MapTrainee.this, obj.getString("message"), Toast.LENGTH_SHORT).show();


                } else {
                    avi.setVisibility(View.GONE);
                    select.setClickable(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                avi.setVisibility(View.GONE);
                select.setClickable(true);
                Toast.makeText(MapTrainee.this, Constants.server_error_message, Toast.LENGTH_SHORT).show();

            }

        }
    }

    public static CountDownTimer CountTimeout;

    void timeOut(int length) {
/*
*
* timeout to finding a trainer
*
* */

        CountTimeout = new android.os.CountDownTimer(60000 * length, 1000) {
            public void onTick(long millisUntilFinished) {
//                                textic.setText("Time Left: " + millisUntilFinished / 1000);


                CommonCall.PrintLog("timmer ", "tick" + millisUntilFinished / 1000);
            }

            public void onFinish() {

                avi.setVisibility(View.GONE);
                select.setClickable(true);

                Toast.makeText(MapTrainee.this, "Timeout for finding Trainer.", Toast.LENGTH_SHORT).show();
                finish();


            }
        };
        CountTimeout.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public class checkout extends AsyncTask<String, String, String> {

        Activity activity;

        public checkout(Activity act, String nounce) {
            this.activity = act;
            this.nounce = nounce;
        }

        String nounce;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            avi.setVisibility(View.VISIBLE);
            select.setClickable(false);
        }

        @Override
        protected String doInBackground(String... strings) {

            JSONObject req = new JSONObject();
            try {
                req.put("nonce", nounce);
                req.put("training_time", duration);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String res = NetworkCalls.POST(Urls.getcheckoutURL(), req.toString());
            return res;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {

                    JSONObject data = response.getJSONObject("data");
                    PreferencesUtils.saveData(Constants.transactionId, data.getString("transactionId"), getApplicationContext());
                    PreferencesUtils.saveData(Constants.amount, data.getString("amount"), getApplicationContext());
                    PreferencesUtils.saveData(Constants.transaction_status, data.getString("status"), getApplicationContext());
                    PreferencesUtils.saveData(Constants.duration, duration, getApplicationContext());

                    Toast.makeText(activity, "Payment  Successful!", Toast.LENGTH_SHORT).show();

                    new RandomSelect().execute();

                } else if (response.getInt(Constants.status) == 2) {
                    avi.setVisibility(View.GONE);
                    select.setClickable(true);
//                    CommonCall.hideLoader();
//                Snackbar snackbar = Snackbar
//                        .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
//                        .setAction("RETRY", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//
//                                Snackbar snackbar1 = null;
//
//                                snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);
//
//                                snackbar1.show();
//                                new PaymentType.applyPromo().execute();
//
//                            }
//                        });
//
//                snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {
                    avi.setVisibility(View.GONE);
                    select.setClickable(true);
// CommonCall.hideLoader();
                    CommonCall.sessionout(activity);
                }


            } catch (JSONException e) {
                avi.setVisibility(View.GONE);
                select.setClickable(true);
//                CommonCall.hideLoader();
                e.printStackTrace();
                CommonCall.hideLoader();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == resultPayment) {
            if (resultCode == Activity.RESULT_OK) {

                intPayment();


            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                avi.setVisibility(View.GONE);
                select.setClickable(true);
//                CommonCall.hideLoader();
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();

            } else {
                avi.setVisibility(View.GONE);
                select.setClickable(true);
//                CommonCall.hideLoader();
                CommonCall.PrintLog("mylog", "Error :403 ");
            }
        }


    }
}
