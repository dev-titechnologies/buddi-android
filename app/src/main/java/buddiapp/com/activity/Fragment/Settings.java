package buddiapp.com.activity.Fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.SettingsCategory;
import buddiapp.com.utils.CommonCall;


/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    int sessionDuration = 0;
    String sgender="";
    LinearLayout duration, gender, location, category,social_root;
    TextView  fourty, hour, male, female, noPreference,maddress;
    TextView locationPref, categoryPref, genderPref, sessionPref,catname, socialMediaShare;
    int id =0, ids=0, id1=0,id2=0,id3=0;
    Animation a,b,c,d,e;
    Button next;
    Boolean check1=false, check2=false;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    TwitterLoginButton loginButton;

    public Settings() {
        // Required empty public constructor

    }

    Switch twitter_switch,facebook_switch;


    LoginButton loginButtonfb;
   public static  CallbackManager callbackManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        callbackManager = CallbackManager.Factory.create();
        loginButton = new TwitterLoginButton(getActivity());
//               loginButton = view.findViewById(R.id.twiter_button);

        loginButtonfb = (LoginButton)view.findViewById(R.id.facebook_button);
        loginButtonfb.setPublishPermissions("publish_actions");
        socialMediaShare = view.findViewById(R.id.socialMedia_preference);
        social_root = view.findViewById(R.id.social_root);
//        loginButtonfb.setReadPermissions("email");

        // If using in a fragment
//        loginButtonfb.setFragment(this);


                twitter_switch =(Switch)view.findViewById(R.id.twitter_switch);
        facebook_switch =(Switch)view.findViewById(R.id.facebook_switch);
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();


        if(twitterSession!=null)
        {

            Toast.makeText(getActivity(), "Welcome Back", Toast.LENGTH_LONG).show();
            PreferencesUtils.saveData(Constants.twitterShare,"true",getActivity());
            twitter_switch.setChecked(true);
//            CommonCall.postTwitter("test happy xhristmaz");
        }else{

            PreferencesUtils.saveData(Constants.twitterShare,"false",getActivity());
            twitter_switch.setChecked(false);



        }


        if (PreferencesUtils.getData(Constants.facebookShare,getActivity(),"false").equals("true")){

            facebook_switch.setChecked(true);
//            CommonCall.postFacebook("test happy xhristmaz");
            PreferencesUtils.saveData(Constants.facebookShare,"true",getActivity());
        }



        twitter_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                if (twitter_switch.isChecked()){
                    loginButton.performClick();

                }else{

                }

            }
        });



        loginButtonfb.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.e("result fb","successs fb");
                        PreferencesUtils.saveData(Constants.facebookShare,"true",getActivity());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e("result fb","onCancel");
                        facebook_switch.setChecked(false);
                        PreferencesUtils.saveData(Constants.facebookShare,"false",getActivity());
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e("result fb","onError");
                        facebook_switch.setChecked(false);
                        PreferencesUtils.saveData(Constants.facebookShare,"false",getActivity());
                    }
                });

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.e("result","successs");


                PreferencesUtils.saveData(Constants.twitterShare,"true",getActivity());
                TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
                if(twitterSession!=null)
                {

                    Toast.makeText(getActivity(), "Welcome Back", Toast.LENGTH_LONG).show();
                }



            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                PreferencesUtils.saveData(Constants.twitterShare,"false",getActivity());
                Log.e("error","errr");
                twitter_switch.setChecked(false);
            }
        });

        facebook_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (facebook_switch.isChecked()){
                    LoginManager.getInstance().logOut();
                    loginButtonfb.performClick();

                }else{

                }


            }
        });



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
        category = (LinearLayout) view.findViewById(R.id.categorySettings);
        catname = (TextView) view.findViewById(R.id.category_name);

        loadScreen();
        a = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        b = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        c = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        d = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        e = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);

        if(PreferencesUtils.getData(Constants.user_type,getActivity(),"").equals("trainer")){
            locationPref.setVisibility(View.GONE);
            categoryPref.setVisibility(View.GONE);
            genderPref.setVisibility(View.GONE);
            sessionPref.setVisibility(View.GONE);
            catname.setVisibility(View.GONE);
        }
        locationPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id1 == 0) {
                    id1 = 1;

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

                if (id2 == 0) {
                    id2 = 1;

                    d.reset();
                    categoryPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_sign_to_navigate, 0);
                    category.setVisibility(View.VISIBLE);
                    category.startAnimation(d);

                } else

                {
                    id2 =0;
                    d.reset();
                    categoryPref.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                    category.setVisibility(View.GONE);
                }
                if(PreferencesUtils.getData(Constants.settings_cat_name,getActivity(),"").length()==0){
                    Intent i  = new Intent(getActivity(), SettingsCategory.class);
                    getActivity().startActivityForResult(i,111);
                }else{
                    catname.setText(PreferencesUtils.getData(Constants.settings_cat_name,getActivity(),""));
                }
               }
        });
        catname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(getActivity(), SettingsCategory.class);
                getActivity().startActivityForResult(i,111);
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
        socialMediaShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id3 == 0) {
                    id3 = 1;

                    e.reset();
                    socialMediaShare.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_sign_to_navigate, 0);
                    social_root.setVisibility(View.VISIBLE);
                    social_root.startAnimation(e);

                } else

                {
                    id3 =0;
                    e.reset();
                    socialMediaShare.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0);
                    social_root.setVisibility(View.GONE);
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
            id = 1;
            duration.setVisibility(View.VISIBLE);
        }else if(PreferencesUtils.getData(Constants.training_duration,getActivity(),"").equals("60")){
            setHour();
            id = 1;
            duration.setVisibility(View.VISIBLE);
        }
        if(PreferencesUtils.getData(Constants.trainer_gender,getActivity(),"").equals("male")){
            setMale();
            ids = 1;
            gender.setVisibility(View.VISIBLE);
        }else if(PreferencesUtils.getData(Constants.trainer_gender,getActivity(),"").equals("female")){
            setFemale();
            ids = 1;
            gender.setVisibility(View.VISIBLE);
        }else if(PreferencesUtils.getData(Constants.trainer_gender,getActivity(),"").equals("nopreference")){
            setNoPreference();
            ids = 1;
            gender.setVisibility(View.VISIBLE);
        }
        if(PreferencesUtils.getData(Constants.settings_cat_name,getActivity(),"").length()>0){
            catname.setText(PreferencesUtils.getData(Constants.settings_cat_name,getActivity(),""));
            id2 = 1;
            category.setVisibility(View.VISIBLE);
        }
        if(PreferencesUtils.getData(Constants.settings_address,getActivity(),"").length()>0){
            maddress.setText(PreferencesUtils.getData(Constants.settings_address,getActivity(),""));
            id1 = 1;
            location.setVisibility(View.VISIBLE);
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

    public  void twitterCallback(int requestCode,int resultCode, Intent data){

        loginButton.onActivityResult(requestCode, resultCode, data);

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
            PreferencesUtils.saveData(Constants.settings_address_name,name.toString(),getActivity());
            PreferencesUtils.saveData(Constants.settings_latitude, String.valueOf(place.getLatLng().latitude),getActivity());
            PreferencesUtils.saveData(Constants.settings_longitude, String.valueOf(place.getLatLng().longitude),getActivity());

        }else if(requestCode == 111
                && resultCode == Activity.RESULT_OK){

        }
        else {
//            loginButton.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);


        }
    }catch (Exception e){
        e.printStackTrace();
    }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        super.onResume();
        catname.setText(PreferencesUtils.getData(Constants.settings_cat_name,getActivity(),""));

    }
}
