package buddyapp.com.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class MapTrainee extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback, LocationSource.OnLocationChangedListener {
    GoogleMap googleMap;
    private LatLng camera;
    Double latitude, longitude;
    LocationManager mLocationManager;
    Button select;
    String sgender,lat, lng, category,duration;
    private HashMap<Marker, String> hashMarker = new HashMap<Marker, String>();


    int resultPayment=403;
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

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



if (PreferencesUtils.getData(Constants.clientToken,getApplicationContext(),"").length()>1) {
    intPayment();

}else{

    Intent payment = new Intent(getApplicationContext(), PaymentType.class);
    payment.putExtra("result", true);
    startActivityForResult(payment,resultPayment);
}

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        latitude = Double.valueOf(PreferencesUtils.getData(Constants.latitude,getApplicationContext(),""));
        longitude = Double.valueOf(PreferencesUtils.getData(Constants.longitude,getApplicationContext(),""));
        camera = new LatLng(latitude, longitude);

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

    }


    void intPayment(){

        /*
*
*
* getting payment
*
* */
        CommonCall.showLoader(MapTrainee.this);

        DropInResult.fetchDropInResult(MapTrainee.this, PreferencesUtils.getData(Constants.clientToken,getApplicationContext(),""), new DropInResult.DropInResultListener() {
            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }

            @Override
            public void onResult(DropInResult result) {
                String  nounce=result.getPaymentMethodNonce().getNonce();


                new checkout(MapTrainee.this,nounce).execute();
            }
        });
    }
    private void getTrainerMarker() {
        String array = PreferencesUtils.getData("searchArray",getApplicationContext(),"");
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
                Marker pos_Marker =  googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(place.getString("latitude")), Double.parseDouble(place.getString("longitude")))).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).title("Trainer").draggable(false));
                pos_Marker.showInfoWindow();

                googleMap.setInfoWindowAdapter(MapTrainee.this);
            }
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

        MapRipple mapRipple = new MapRipple(googleMap,camera, getApplicationContext());
        mapRipple.withNumberOfRipples(3);
        mapRipple.withFillColor(getResources().getColor(R.color.login_bgcolor));
        mapRipple.withStrokeColor(Color.BLACK);
        mapRipple.withStrokewidth(1);      // 10dp
        mapRipple.withDistance(1000);      // 2000 metres radius
        mapRipple.withRippleDuration(5000);    //12000ms
        mapRipple.withTransparency(0.5f);
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

    public  class RandomSelect extends AsyncTask<String,String,String> {
        String response;
        JSONObject reqData = new JSONObject();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(MapTrainee.this);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.user_id,PreferencesUtils.getData(Constants.user_id,getApplicationContext(),""));
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
                    Intent intent = new Intent(getApplicationContext(),TrainerProfileView.class);
                    intent.putExtra("TrainerData",jsonObject.toString());
                    startActivity(intent);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }



    public  class checkout extends  AsyncTask<String,String,String>{

        Activity activity;
        public checkout(Activity act,String nounce){
            this.activity=act;
            this.nounce=nounce;
        }
        String nounce ;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();





        }

        @Override
        protected String doInBackground(String... strings) {

            JSONObject req= new JSONObject();
            try {
                req.put("nonce",nounce);
                req.put("amount","100");//for testing only
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String res = NetworkCalls.POST(Urls.getcheckoutURL(),req.toString());
            return res;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {


                    Toast.makeText(activity, "Payment  Successful!", Toast.LENGTH_SHORT).show();

                    new RandomSelect().execute();
                } else if (response.getInt(Constants.status) == 2) {

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

                    CommonCall.sessionout(activity);
                }


            } catch (JSONException e) {
                e.printStackTrace();
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


                Toast.makeText(this, "User Canceled", Toast.LENGTH_SHORT).show();

            } else {

                CommonCall.PrintLog("mylog", "Error :403 " );
            }
        }


    }
}
