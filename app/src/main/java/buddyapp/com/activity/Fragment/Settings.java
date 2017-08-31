package buddyapp.com.activity.Fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.ChooseSpecification;
import buddyapp.com.activity.SettingsCategory;
import it.sephiroth.android.library.easing.Linear;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    int sessionDuration = 0;
    String sgender="";
    LinearLayout duration, gender, location;
    TextView  fourty, hour, male, female, noPreference,maddress;
    TextView locationPref, categoryPref, genderPref, sessionPref;
    int id =0, ids=0, id1=0,id2=0;
    Animation a,b,c,d;
    Button next;
    Boolean check1=false, check2=false;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        locationPref = (TextView) view.findViewById(R.id.locaton_preference);
        categoryPref = (TextView) view.findViewById(R.id.category_preference);
        genderPref = (TextView) view.findViewById(R.id.gender_preference);
        sessionPref = (TextView) view.findViewById(R.id.session_preference);
        maddress = (TextView) view.findViewById(R.id.address);
        duration = (LinearLayout) view.findViewById(R.id.duration);
        gender = (LinearLayout) view.findViewById(R.id.gender);
        fourty = (TextView) view.findViewById(R.id.thirty);
        hour = (TextView) view.findViewById(R.id.hour);
        male = (TextView) view.findViewById(R.id.male);
        female = (TextView) view.findViewById(R.id.female);
        next= (Button) view.findViewById(R.id.next);
        noPreference = (TextView) view.findViewById(R.id.no_preference);
        location = (LinearLayout) view.findViewById(R.id.locationSettings);
        loadScreen();

        locationPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id1 == 0) {
                    id1 = 1;
                    c = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                    c.reset();
                    locationPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_sign_to_navigate, 0);
                    location.setVisibility(View.VISIBLE);
                    location.startAnimation(c);

                } else

                {
                    id1 =0;
                    c.reset();
                    locationPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                    location.setVisibility(View.GONE);
                }
                if(PreferencesUtils.getData(Constants.settings_address,getActivity(),"").length()==0){
                    placePicker();
                }else{
                    maddress.setText(PreferencesUtils.getData(Constants.settings_address,getActivity(),""));
                }
            }
        });

        categoryPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent i  = new Intent(getActivity(), SettingsCategory.class);
                    getActivity().startActivity(i);
               }
        });
        maddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placePicker();
            }
        });
        sessionPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == 0) {
                    id = 1;
                    b = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                    b.reset();
                    sessionPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_sign_to_navigate, 0);
                    duration.setVisibility(View.VISIBLE);
                    duration.startAnimation(b);

                } else

                {
                    id =0;
                    b.reset();
                    sessionPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                    duration.setVisibility(View.GONE);
                }
            }
        });
        genderPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ids == 0) {
                    ids = 1;
                    a = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
                    a.reset();
                    genderPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_sign_to_navigate, 0);
                    gender.setVisibility(View.VISIBLE);
                    gender.startAnimation(a);

                } else

                {
                    ids =0;
                    a.reset();
                    genderPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                    gender.setVisibility(View.GONE);
                }
            }
        });

        fourty.setOnClickListener(new View.OnClickListener() { @Override
        public void onClick(View view) {check1 = true;setFourty();}});
        hour.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            check1 = true;setHour();
        }
        });
        male.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {
            check2 = true;setMale();
        }
        });
        female.setOnClickListener(new View.OnClickListener() {                                           @Override
        public void onClick(View view) {check2 = true;setFemale();}});
        noPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check2 = true;setNoPreference();
            }
        });


        // Inflate the layout for this fragment
        return view;
    }
    private void placePicker(){
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(getActivity());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    private void loadScreen() {
//        if(PreferencesUtils.)
        if(PreferencesUtils.getData(Constants.training_duration,getActivity(),"").equals("40")){
            setFourty();
        }else if(PreferencesUtils.getData(Constants.training_duration,getActivity(),"").equals("60")){
            setHour();
        }
        if(PreferencesUtils.getData(Constants.trainer_gender,getActivity(),"").equals("male")){
            setMale();
        }else if(PreferencesUtils.getData(Constants.trainer_gender,getActivity(),"").equals("female")){
            setFemale();
        }else if(PreferencesUtils.getData(Constants.trainer_gender,getActivity(),"").equals("nopreference")){
            setNoPreference();
        }
    }

    void setFourty(){
        sessionDuration = 40;
        PreferencesUtils.saveData(Constants.training_duration, String.valueOf(sessionDuration),getActivity());
        fourty.setTextColor(getResources().getColor(R.color.white));
        hour.setTextColor(getResources().getColor(R.color.black));
        fourty.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        hour.setBackgroundColor(getResources().getColor(R.color.white));
    }
    void setHour(){
        sessionDuration = 60;
        PreferencesUtils.saveData(Constants.training_duration, String.valueOf(sessionDuration),getActivity());
        fourty.setTextColor(getResources().getColor(R.color.black));
        hour.setTextColor(getResources().getColor(R.color.white));
        fourty.setBackgroundColor(getResources().getColor(R.color.white));
        hour.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }
    void setMale(){
        sgender = "male";
        PreferencesUtils.saveData(Constants.trainer_gender,sgender,getActivity());
        male.setTextColor(getResources().getColor(R.color.white));
        female.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        female.setBackgroundColor(getResources().getColor(R.color.white));
        noPreference.setTextColor(getResources().getColor(R.color.black));
        noPreference.setBackgroundColor(getResources().getColor(R.color.white));
    }
    void setFemale(){
        sgender = "female";
        PreferencesUtils.saveData(Constants.trainer_gender,sgender,getActivity());
        female.setTextColor(getResources().getColor(R.color.white));
        male.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.white));
        female.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        noPreference.setTextColor(getResources().getColor(R.color.black));
        noPreference.setBackgroundColor(getResources().getColor(R.color.white));
    }
    private void setNoPreference() {
        sgender = "nopreference";
        PreferencesUtils.saveData(Constants.trainer_gender,sgender,getActivity());
        noPreference.setTextColor(getResources().getColor(R.color.white));
        noPreference.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        female.setTextColor(getResources().getColor(R.color.black));
        male.setTextColor(getResources().getColor(R.color.black));
        male.setBackgroundColor(getResources().getColor(R.color.white));
        female.setBackgroundColor(getResources().getColor(R.color.white));
    }
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
    try {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(getActivity(), data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();

            String prefAddress = (String) address;
            maddress.setText(address );
            PreferencesUtils.saveData(Constants.settings_address, prefAddress,getActivity());
            PreferencesUtils.saveData(Constants.settings_latitude, String.valueOf(place.getLatLng().latitude),getActivity());
            PreferencesUtils.saveData(Constants.settings_longitude, String.valueOf(place.getLatLng().longitude),getActivity());

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }catch (Exception e){
        e.printStackTrace();
    }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
