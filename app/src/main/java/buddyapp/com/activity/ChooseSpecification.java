package buddyapp.com.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.Fragment.HomeCategory;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class ChooseSpecification extends AppCompatActivity {
    int sessionDuration = 0;
    String sgender="", prefAddress = "", pick_location;
    LinearLayout duration, gender, location;
    TextView session, trainerGender;
    TextView  locationPref, fourty, hour, male, female, noPreference, maddress;
    int id =0, ids=0, id1= 0;
    Animation a,b,c;
    Button next;
    private static final int PLACE_PICKER_REQUEST = 1;
    Boolean check1=false, check2=false, check3 =false;
    static LatLngBounds BOUNDS_MOUNTAIN_VIEW = null;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager mLocationManager;
    double longitude, latitude, mlongitude,mlatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_specification);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        duration = (LinearLayout) findViewById(R.id.duration);
        gender = (LinearLayout) findViewById(R.id.gender);
        session = (TextView) findViewById(R.id.session);
        trainerGender = (TextView) findViewById(R.id.trainer_gender);
        fourty = (TextView) findViewById(R.id.thirty);
        hour = (TextView) findViewById(R.id.hour);
        male = (TextView) findViewById(R.id.male);
        female = (TextView) findViewById(R.id.female);
        next= (Button) findViewById(R.id.next);
        noPreference = (TextView) findViewById(R.id.no_preference);
        locationPref = (TextView) findViewById(R.id.locaton_preference);
        location = (LinearLayout) findViewById(R.id.locationSettings);
        maddress = (TextView) findViewById(R.id.address);
// ************************Get Current location*********************
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(ChooseSpecification.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            PreferencesUtils.saveData(Constants.latitude, String.valueOf(latitude), getApplicationContext());
                            PreferencesUtils.saveData(Constants.longitude, String.valueOf(longitude),getApplicationContext());
                            BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                                    new LatLng(latitude, longitude), new LatLng(longitude, longitude));

                        }
                    }
                });
        session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == 0) {
                    session.setText("Choose Session Duration" + getTextSession());
                    id = 1;
                    b = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                    b.reset();
                    session.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_sign_to_navigate, 0);
                    duration.setVisibility(View.VISIBLE);
                    duration.startAnimation(b);

                } else

                {
                    id =0;
                    b.reset();
                    session.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                    duration.setVisibility(View.GONE);
                }
            }
        });

        trainerGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ids == 0) {
                    ids = 1;
                    a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                    a.reset();
                    trainerGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_sign_to_navigate, 0);
                    gender.setVisibility(View.VISIBLE);
                    gender.startAnimation(a);

                } else

                {
                    ids =0;
                    a.reset();
                    trainerGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                    gender.setVisibility(View.GONE);
                }
            }
        });

        locationPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id1 == 0) {
                    id1 = 1;
                    c = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                    c.reset();
                    locationPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_sign_to_navigate, 0);
                    location.setVisibility(View.VISIBLE);
                    location.startAnimation(c);
//                    if(maddress.getText().length()<1)
//                    maddress.performClick();

                } else

                {
                    id1 =0;
                    c.reset();
                    locationPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                    location.setVisibility(View.GONE);
                }

            }
        });

        maddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placePicker();
            }
        });

        fourty.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            check1 = true;
            setFourty();
            if(check2 && check3)
                next.setVisibility(View.VISIBLE);

            id =0;
            b.reset();
            session.setText("Choose Session Duration" +"\t\t\t\t\t\t\t\t\t40 Minutes");
            session.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
            duration.setVisibility(View.GONE);

        }
        });
        hour.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            check1 = true;setHour();
            if(check2 && check3)
                next.setVisibility(View.VISIBLE);

            id =0;
            b.reset();
            session.setText("Choose Session Duration" +"\t\t\t\t\t\t\t\t\t1 Hour");
            session.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
            duration.setVisibility(View.GONE);

        }
        });
        male.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            trainerGender.setText("Choose Trainer Gender \t\t\t\t\t\t\t\t\t\t"+ "Male");
            check2 = true;setMale();
            ids =0;
            a.reset();
            trainerGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
            gender.setVisibility(View.GONE);
            if(check1 && check3)
                next.setVisibility(View.VISIBLE);
        }
        });
        female.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            trainerGender.setText("Choose Trainer Gender \t\t\t\t\t\t\t\t\t\t"+ "Female");
            check2 = true;setFemale();
            ids =0;
            a.reset();
            trainerGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
            gender.setVisibility(View.GONE);

            if(check1 && check3)
                next.setVisibility(View.VISIBLE);
        }
        });
        noPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainerGender.setText("Choose Trainer Gender \t\t\t\t\t\t\t\t\t\t"+ "No Preference");
                check2 = true;setNoPreference();
                ids =0;
                a.reset();
                trainerGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                gender.setVisibility(View.GONE);

                if(check1 && check3)
                    next.setVisibility(View.VISIBLE);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {
                    Toast.makeText(ChooseSpecification.this, "You are already in a session", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), SessionReady.class));
                    finish();
                }     else {

                    mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                    if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        buildAlertMessageNoGps();
                    }

                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(ChooseSpecification.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        longitude = location.getLongitude();
                                        latitude = location.getLatitude();
                                        PreferencesUtils.saveData(Constants.latitude, String.valueOf(latitude), getApplicationContext());
                                        PreferencesUtils.saveData(Constants.longitude, String.valueOf(longitude), getApplicationContext());


                                        if (sessionDuration > 0) {
                                            if (latitude > 0.0) {
                                                new SearchTrainer().execute();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Please check your GPS connection", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Please select you choice", Toast.LENGTH_SHORT).show();
                                        }

                                    }else{

                                        Toast.makeText(ChooseSpecification.this, "Unable to fetch your current location!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                }

            }
        });
    }

    private String getTextSession() {
        if(sessionDuration == 40)
            return "\t40 Minutes";
        else if(sessionDuration == 60)
            return "\t\t\t\t\t\t\t1 Hour";
        else
            return "";
    }

    private void setNoPreference() {
        sgender = "nopreference";
        noPreference.setTextColor(getResources().getColor(R.color.white));
        noPreference.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        female.setTextColor(getResources().getColor(R.color.black));
        male.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.white));
        female.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ChooseSpecification.this);
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

    void setFourty(){
        sessionDuration = 40;

        fourty.setTextColor(getResources().getColor(R.color.white));
        hour.setTextColor(getResources().getColor(R.color.black));
        fourty.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        hour.setBackgroundColor(getResources().getColor(R.color.white));
    }
    void setHour(){
        sessionDuration = 60;

        fourty.setTextColor(getResources().getColor(R.color.black));
        hour.setTextColor(getResources().getColor(R.color.white));
        fourty.setBackgroundColor(getResources().getColor(R.color.white));
        hour.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }
    void setMale(){
        sgender = "male";
        male.setTextColor(getResources().getColor(R.color.white));
        female.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        female.setBackgroundColor(getResources().getColor(R.color.white));
        noPreference.setTextColor(getResources().getColor(R.color.black));
        noPreference.setBackgroundColor(getResources().getColor(R.color.white));
    }
    void setFemale(){
        sgender = "female";
        female.setTextColor(getResources().getColor(R.color.white));
        male.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.white));
        female.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        noPreference.setTextColor(getResources().getColor(R.color.black));
        noPreference.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void placePicker(){
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
//            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(ChooseSpecification.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        try {

            if (requestCode == PLACE_PICKER_REQUEST
                    && resultCode == Activity.RESULT_OK) {

                final Place place = PlacePicker.getPlace(getApplicationContext(), data);
                CommonCall.PrintLog("Place",place.toString());
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                check3 =true;
                pick_location = name.toString();
//                locationPref.setText("Choose Training Location \t\t\t\t\t"+name.toString() );
                prefAddress = name.toString()+" "+ address.toString();
                maddress.setText(prefAddress );
                mlatitude = place.getLatLng().latitude;
                mlongitude = place.getLatLng().longitude;
                if(check1 && check2)
                    next.setVisibility(View.VISIBLE);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    class SearchTrainer extends AsyncTask<String,String,String> {
        String response = "";
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(ChooseSpecification.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));
                reqData.put(Constants.gender, sgender);
                reqData.put("category", HomeCategory.cat_selectedID.get(0));
                reqData.put(Constants.latitude, latitude);
                reqData.put(Constants.longitude, longitude);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response = NetworkCalls.POST(Urls.getTrainerSearchURL(), reqData.toString());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    JSONArray jsonArray = obj.getJSONArray("data");
                    if (jsonArray.length() != 0) {
                        PreferencesUtils.saveData("searchArray",obj.getJSONArray("data").toString(),getApplicationContext());

                        Intent intent = new Intent(getApplicationContext(), MapTrainee.class);
                        intent.putExtra(Constants.gender,sgender);
                        intent.putExtra("category", HomeCategory.cat_selectedID.get(0));
                        intent.putExtra(Constants.latitude, String.valueOf(latitude));
                        intent.putExtra(Constants.longitude, String.valueOf(longitude));
                        intent.putExtra(Constants.duration, String.valueOf(sessionDuration));
                        intent.putExtra("pick_latitude",mlatitude+"");
                        intent.putExtra("pick_longitude",mlongitude+"");
                        intent.putExtra("pick_location",pick_location);
                        startActivity(intent);

                    }else
                    {
                        // No match found..........
                        Toast.makeText(getApplicationContext(), "No trainer found", Toast.LENGTH_SHORT).show();
                    }
                }else if (obj.getInt("status") == 2) {
                    Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                }else if (obj.getInt("status") == 3) {
                    CommonCall.sessionout(ChooseSpecification.this);
                }
            } catch (JSONException e) {

            }
        }
    }
    @Override
    public void onBackPressed() {
        finish();
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


}
