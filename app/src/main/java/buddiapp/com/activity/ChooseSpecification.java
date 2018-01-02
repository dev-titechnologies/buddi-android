package buddiapp.com.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.Fragment.HomeCategory;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;

public class ChooseSpecification extends AppCompatActivity {
    int sessionDuration = 0;
    String sgender = "", prefAddress = "", pick_location;
    LinearLayout duration, gender, location;
    TextView session, trainerGender;
    TextView locationPref, fourty, hour, male, female, noPreference, maddress;
    int id = 0, ids = 0, id1 = 0;
    Animation a, b, c;
    Button next;
    private static final int PLACE_PICKER_REQUEST = 1;
    Boolean check1 = false, check2 = false, check3 = false;
    static LatLngBounds BOUNDS_MOUNTAIN_VIEW = null;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager mLocationManager;
    double longitude, latitude, mlongitude, mlatitude;

    Boolean releaseForm = false;
    FrameLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_specification);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        root = (FrameLayout) findViewById(R.id.root);
        duration = (LinearLayout) findViewById(R.id.duration);
        gender = (LinearLayout) findViewById(R.id.gender);
        session = (TextView) findViewById(R.id.session);
        trainerGender = (TextView) findViewById(R.id.trainer_gender);
        fourty = (TextView) findViewById(R.id.thirty);
        hour = (TextView) findViewById(R.id.hour);
        male = (TextView) findViewById(R.id.male);
        female = (TextView) findViewById(R.id.female);
        next = (Button) findViewById(R.id.next);
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
                            PreferencesUtils.saveData(Constants.longitude, String.valueOf(longitude), getApplicationContext());
//                            BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
//                                    new LatLng(latitude, longitude), new LatLng(longitude, longitude));

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
                    id = 0;
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
                    ids = 0;
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
                    id1 = 0;
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

        fourty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check1 = true;
                setFourty();
                if (check2 && check3)
                    next.setVisibility(View.VISIBLE);

                id = 0;
                b.reset();
                session.setText("Choose Session Duration" + "\t\t\t\t\t\t40 Minutes");
                session.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                duration.setVisibility(View.GONE);

            }
        });
        hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check1 = true;
                setHour();
                if (check2 && check3)
                    next.setVisibility(View.VISIBLE);

                id = 0;
                b.reset();
                session.setText("Choose Session Duration" + "\t\t\t\t\t\t\t\t1 Hour");
                session.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                duration.setVisibility(View.GONE);

            }
        });
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainerGender.setText("Choose Trainer Gender \t\t\t\t\t\t\t\t\t\t" + "Male");
                check2 = true;
                setMale();
                ids = 0;
                a.reset();
                trainerGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                gender.setVisibility(View.GONE);
                if (check1 && check3)
                    next.setVisibility(View.VISIBLE);
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainerGender.setText("Choose Trainer Gender \t\t\t\t\t\t\t\t\t\t" + "Female");
                check2 = true;
                setFemale();
                ids = 0;
                a.reset();
                trainerGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                gender.setVisibility(View.GONE);

                if (check1 && check3)
                    next.setVisibility(View.VISIBLE);
            }
        });
        noPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainerGender.setText("Choose Trainer Gender \t\t\t\t\t\t\t\t\t\t" + "No Preference");
                check2 = true;
                setNoPreference();
                ids = 0;
                a.reset();
                trainerGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                gender.setVisibility(View.GONE);

                if (check1 && check3)
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
                } else {

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
                                            if(releaseForm){
                                                new getPendingPayments().execute();
                                            }else{
                                            showReleseFormAlert();      
                                            }

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Please check your GPS connection", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Please select you choice", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {

                                        Toast.makeText(ChooseSpecification.this, "Unable to fetch your current location!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                }

            }
        });
    }

    private String getTextSession() {
        if (sessionDuration == 40)
            return "\t\t\t\t\40 Minutes";
        else if (sessionDuration == 60)
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

    void setFourty() {
        sessionDuration = 40;

        fourty.setTextColor(getResources().getColor(R.color.white));
        hour.setTextColor(getResources().getColor(R.color.black));
        fourty.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        hour.setBackgroundColor(getResources().getColor(R.color.white));
    }

    void setHour() {
        sessionDuration = 60;

        fourty.setTextColor(getResources().getColor(R.color.black));
        hour.setTextColor(getResources().getColor(R.color.white));
        fourty.setBackgroundColor(getResources().getColor(R.color.white));
        hour.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    void setMale() {
        sgender = "male";
        male.setTextColor(getResources().getColor(R.color.white));
        female.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        female.setBackgroundColor(getResources().getColor(R.color.white));
        noPreference.setTextColor(getResources().getColor(R.color.black));
        noPreference.setBackgroundColor(getResources().getColor(R.color.white));
    }

    void setFemale() {
        sgender = "female";
        female.setTextColor(getResources().getColor(R.color.white));
        male.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.white));
        female.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        noPreference.setTextColor(getResources().getColor(R.color.black));
        noPreference.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void placePicker() {
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
                CommonCall.PrintLog("Place", place.toString());
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                check3 = true;
                pick_location = name.toString();
//                locationPref.setText("Choose Training Location \t\t\t\t\t"+name.toString() );
                prefAddress = name.toString() + " " + address.toString();
                maddress.setText(prefAddress);
                mlatitude = place.getLatLng().latitude;
                mlongitude = place.getLatLng().longitude;
                if (check1 && check2)
                    next.setVisibility(View.VISIBLE);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SearchTrainer extends AsyncTask<String, String, String> {
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
                        PreferencesUtils.saveData("searchArray", obj.getJSONArray("data").toString(), getApplicationContext());

                        Intent intent = new Intent(getApplicationContext(), MapTrainee.class);
                        intent.putExtra(Constants.gender, sgender);
                        intent.putExtra("category", HomeCategory.cat_selectedID.get(0));
                        intent.putExtra(Constants.latitude, String.valueOf(latitude));
                        intent.putExtra(Constants.longitude, String.valueOf(longitude));


                        intent.putExtra(Constants.duration, String.valueOf(sessionDuration));


                        intent.putExtra("pick_latitude", mlatitude + "");
                        intent.putExtra("pick_longitude", mlongitude + "");
                        intent.putExtra("pick_location", pick_location);
                        startActivity(intent);

                    } else {
                        // No match found..........
                        Toast.makeText(getApplicationContext(), "No trainer found", Toast.LENGTH_SHORT).show();
                    }
                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else if (obj.getInt("status") == 3) {
                    CommonCall.sessionout(ChooseSpecification.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public class getPendingPayments extends AsyncTask<String, String, String> {
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
                reqData.put(Constants.user_type, PreferencesUtils.getData(Constants.user_type, getApplicationContext(), ""));

            } catch (Exception e) {
                e.printStackTrace();
            }
            String response = NetworkCalls.POST(Urls.getpendingTransactionURL(), reqData.toString());
            return response;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {


                    JSONArray payment = response.getJSONArray("data");
                    if (payment.length() > 0)

                    {


                        for (int i = 0; i < payment.length(); i++) {
                            JSONObject transaction = payment.getJSONObject(i);

                            if (transaction.getString("session_duration").equals("40")) {

                                PreferencesUtils.saveData(Constants.transactionId40, transaction.getString("transaction_id"), getApplicationContext());
                                PreferencesUtils.saveData(Constants.transaction_status40, transaction.getString("transaction_status"), getApplicationContext());
                                PreferencesUtils.saveData(Constants.amount40, transaction.getString("transaction_amount"), getApplicationContext());

                            } else if (transaction.getString("session_duration").equals("60")) {
                                PreferencesUtils.saveData(Constants.transaction_status60, transaction.getString("transaction_status"), getApplicationContext());
                                PreferencesUtils.saveData(Constants.transactionId60, transaction.getString("transaction_id"), getApplicationContext());
                                PreferencesUtils.saveData(Constants.amount60, transaction.getString("transaction_amount"), getApplicationContext());


                            }

                        }


                    }


                    new SearchTrainer().execute();
                } else if (response.getInt(Constants.status) == 2) {


                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new getPendingPayments().execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {

                    CommonCall.sessionout(ChooseSpecification.this);
                }


            } catch (JSONException e) {


                e.printStackTrace();
                CommonCall.hideLoader();
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

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void showReleseFormAlert() {
        final Dialog dialog = new Dialog(ChooseSpecification.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Include dialog.xml file
        dialog.setContentView(R.layout.custom_dialog_releaseform);
        // Set dialog title
        dialog.setTitle("Custom Dialog");

        // set values for custom dialog components - text, image and button
        final EditText signature = (EditText) dialog.findViewById(R.id.signature);


        dialog.show();

        Button declineButton = (Button) dialog.findViewById(R.id.deny);
        final Button yes = (Button) dialog.findViewById(R.id.accept);
        final CheckBox checkBox = dialog.findViewById(R.id.check_box);
        // if decline button is clicked, close the custom dialog


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    signature.setVisibility(View.VISIBLE);
                    yes.setText("Accept as parent/Guardian");
                }else{
                    signature.setVisibility(View.GONE);
                }
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkBox.isChecked()) {
                   if(signature.getText().length()>0) {
                       dialog.dismiss();
                       releaseForm = true;
                       PreferencesUtils.saveData(Constants.signature,signature.getText().toString(),getApplicationContext());
                       next.performClick();
                   }else{
                   signature.setError("Please provide signature");
                   }
                } else {
                    dialog.dismiss();
                    releaseForm = true;
                    next.performClick();
                }


            }
        });
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                Toast.makeText(ChooseSpecification.this, "You must accept the Waiver Release Form to book a session!", Toast.LENGTH_SHORT).show();
                releaseForm = false;
                dialog.dismiss();

            }
        });
    }
}
