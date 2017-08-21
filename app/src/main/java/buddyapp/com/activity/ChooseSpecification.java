package buddyapp.com.activity;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    String sgender="";
    LinearLayout duration, gender;
    TextView session, trainerGender;
    TextView  thirty, hour, male, female, noPreference;
    int id =0, ids=0;
    Animation a,b;
    Button next;
    Boolean check1=false, check2=false;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager mLocationManager;
    double longitude, latitude;
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
        thirty = (TextView) findViewById(R.id.thirty);
        hour = (TextView) findViewById(R.id.hour);
        male = (TextView) findViewById(R.id.male);
        female = (TextView) findViewById(R.id.female);
        next= (Button) findViewById(R.id.next);
        noPreference = (TextView) findViewById(R.id.no_preference);

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
                        }
                    }
                });
        session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == 0) {
                    id = 1;
                    b = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                    b.reset();
                    duration.setVisibility(View.VISIBLE);
                    duration.startAnimation(b);

                } else

                {
                    id =0;
                    b.reset();
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
                    gender.setVisibility(View.VISIBLE);
                    gender.startAnimation(a);

                } else

                {
                    ids =0;
                    a.reset();
                    gender.setVisibility(View.GONE);
                }
            }
        });


        thirty.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            check1 = true;
            setFourty();
            if(check2)
                next.setVisibility(View.VISIBLE);
        }
        });
        hour.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            check1 = true;setHour();
            if(check2)
                next.setVisibility(View.VISIBLE);
        }
        });
        male.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            check2 = true;setMale();
            if(check1)
                next.setVisibility(View.VISIBLE);
        }
        });
        female.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            check2 = true;setFemale();
            if(check1)
                next.setVisibility(View.VISIBLE);
        }
        });
        noPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check2 = true;setNoPreference();
                if(check1)
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


                                        if (sessionDuration > 0 && sgender.length() > 1) {
                                            if (latitude > 0.0) {
                                                new SearchTrainer().execute();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Please check your GPS connection", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Please select you choice", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                            });


                }

            }
        });
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

        thirty.setTextColor(getResources().getColor(R.color.white));
        hour.setTextColor(getResources().getColor(R.color.black));
        thirty.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        hour.setBackgroundColor(getResources().getColor(R.color.white));
    }
    void setHour(){
        sessionDuration = 60;

        thirty.setTextColor(getResources().getColor(R.color.black));
        hour.setTextColor(getResources().getColor(R.color.white));
        thirty.setBackgroundColor(getResources().getColor(R.color.white));
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
