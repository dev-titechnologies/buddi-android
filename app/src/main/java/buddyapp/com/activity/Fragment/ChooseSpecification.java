package buddyapp.com.activity.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseSpecification extends Fragment  {
    int sessionDuration = 0;
    String sgender="";
    LinearLayout duration, gender;
    TextView session, trainerGender;
    TextView fifteen, thirty, hour, male, female;
    int id =0, ids=0;
    Animation a,b;
    Button next;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager mLocationManager;
    double longitude, latitude;
    public ChooseSpecification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_choose_specification, container, false);
        duration = (LinearLayout) view.findViewById(R.id.duration);
        gender = (LinearLayout) view.findViewById(R.id.gender);
        session = (TextView) view.findViewById(R.id.session);
        trainerGender = (TextView) view.findViewById(R.id.trainer_gender);
        fifteen = (TextView) view.findViewById(R.id.fifteen);
        thirty = (TextView) view.findViewById(R.id.thirty);
        hour = (TextView) view.findViewById(R.id.hour);
        male = (TextView) view.findViewById(R.id.male);
        female = (TextView) view.findViewById(R.id.female);
        next= (Button) view.findViewById(R.id.next);

// ************************Get Current location*********************
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            PreferencesUtils.saveData(Constants.latitude, String.valueOf(latitude), getActivity());
                            PreferencesUtils.saveData(Constants.longitude, String.valueOf(longitude),getActivity());
                        }
                    }
                });
        session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == 0) {
                    id = 1;
                    b = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
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
                    a = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
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

        fifteen.setOnClickListener(new View.OnClickListener() {                                           @Override
                                               public void onClick(View view) {
                                                   setFifteen();
                                               }
                                           });
        thirty.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            setThirty();
        }
        });
        hour.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            setHour();
        }
        });
        male.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            setMale();
        }
        });
        female.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            setFemale();
        }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionDuration>0 && sgender.length()>1) {
                new SearchTrainer().execute();
                }else{
                    Toast.makeText(getActivity(), "Please select you choice", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
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
    public void setFifteen(){
        sessionDuration = 15;
        fifteen.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        thirty.setBackgroundColor(getResources().getColor(R.color.white));
        hour.setBackgroundColor(getResources().getColor(R.color.white));
        fifteen.setTextColor(getResources().getColor(R.color.white));
        thirty.setTextColor(getResources().getColor(R.color.black));
        hour.setTextColor(getResources().getColor(R.color.black));
    }
    void setThirty(){
        sessionDuration = 30;
        fifteen.setTextColor(getResources().getColor(R.color.black));
        thirty.setTextColor(getResources().getColor(R.color.white));
        hour.setTextColor(getResources().getColor(R.color.black));
        fifteen.setBackgroundColor(getResources().getColor(R.color.white));
        thirty.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        hour.setBackgroundColor(getResources().getColor(R.color.white));
    }
    void setHour(){
        sessionDuration = 60;
        fifteen.setTextColor(getResources().getColor(R.color.black));
        thirty.setTextColor(getResources().getColor(R.color.black));
        hour.setTextColor(getResources().getColor(R.color.white));
        fifteen.setBackgroundColor(getResources().getColor(R.color.white));
        thirty.setBackgroundColor(getResources().getColor(R.color.white));
        hour.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }
    void setMale(){
        sgender = "male";
        male.setTextColor(getResources().getColor(R.color.white));
        female.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        female.setBackgroundColor(getResources().getColor(R.color.white));
    }
    void setFemale(){
        sgender = "female";
        female.setTextColor(getResources().getColor(R.color.white));
        male.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.white));
        female.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }
    class SearchTrainer extends AsyncTask<String,String,String> {
        String response = "";
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(getActivity());
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getActivity(), ""));
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
                        PreferencesUtils.saveData("searchArray",obj.getJSONArray("data").toString(),getActivity());

                    Fragment fragment = new Map_Trainee();
                        Bundle args = new Bundle();
                        args.putString(Constants.gender,sgender);
                        args.putString("category", HomeCategory.cat_selectedID.get(0));
                        args.putString(Constants.latitude, String.valueOf(latitude));
                        args.putString(Constants.longitude, String.valueOf(longitude));
                        args.putString(Constants.duration, String.valueOf(sessionDuration));
                        fragment.setArguments(args);
                        getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

                    }else
                    {
                        // No match found..........
                    }
                }
            } catch (JSONException e) {

            }
        }
    }
}
