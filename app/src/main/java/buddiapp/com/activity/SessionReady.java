package buddiapp.com.activity;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.android.gms.maps.OnMapReadyCallback;
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

import buddiapp.com.Controller;
import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.chat.ChatActivity;
import buddiapp.com.fcm.NotificationUtils;
import buddiapp.com.services.GPSTracker;
import buddiapp.com.services.LocationService;
import buddiapp.com.timmer.Timer_Service;
import buddiapp.com.utils.CircleImageView;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;

import static buddiapp.com.Controller.chatConnect;
import static buddiapp.com.Controller.mSocket;
import static buddiapp.com.Controller.updateSocket;
import static buddiapp.com.R.id.map;
import static buddiapp.com.Settings.Constants.start_session;
import static buddiapp.com.Settings.Constants.trainee_Data;
import static buddiapp.com.Settings.Constants.trainer_Data;

public class SessionReady extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback, LocationListener {
    Marker pos_Marker, user_Marker;
    GoogleMap googleMap;
    GPSTracker gps;
    LatLng origin;
    LatLng dest;
    private LatLng camera, usercamera;
    Double latitude, longitude, userlat, userlng;
    LocationManager mLocationManager;
    String lat = "0", lng = "0", traine_id, trainer_id, duration, book_id;
    int training_time;
    String name;

    LatLng trainerLocation;


    LinearLayout start, cancel, profile, message;
    CircleImageView profileactionIcon;
    ImageView startactionIcon, stopactionIcon, messageactionIcon, cancelactionIcon;
    TextView startactionTitle, stopactionTitle, profileactionTitle, messageactionTitle, sessionTimmer;

    Double pick_latitude, pick_longitude;
    String pick_location;


    public static boolean cancelFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_ready);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Training Session");

        intstartStop();
        NotificationUtils.clearNotifications(getApplicationContext());

// check if GPS enabled

        gps = new GPSTracker(SessionReady.this);

        if (gps.canGetLocation()) {
            userlat = gps.getLatitude();
            userlng = gps.getLongitude();
            usercamera = new LatLng(userlat, userlng);
            if (userlat == null)
                gps.showSettingsAlert();// user current location
        } else {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                buildAlertMessageNoGps();
//            }
            // gps.showSettingsAlert();
        }
        PreferencesUtils.saveData(Constants.current_page,"sessionready",getApplicationContext());
        try {


            if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {

                JSONObject data = new JSONObject(PreferencesUtils.getData(trainee_Data, getApplicationContext(), ""));
//                JSONObject trainerDetail = data.getJSONObject("trainer_details");
                book_id = data.getString("book_id");

                PreferencesUtils.saveData(Constants.bookid, book_id, getApplicationContext());
                traine_id = data.getString("trainee_id");
                training_time = data.getInt("training_time");
                name = data.getJSONObject("trainee_details").getString("trainee_first_name") + " " + data.getJSONObject("trainee_details").getString("trainee_last_name");
                lat = data.getJSONObject("trainee_details").getString("trainee_latitude");
                lng = data.getJSONObject("trainee_details").getString("trainee_longitude");

                pick_latitude = Double.valueOf(data.getString("pick_latitude"));
                pick_longitude = Double.valueOf(data.getString("pick_longitude"));
                pick_location = data.getString("pick_location");
                PreferencesUtils.saveData(Constants.pickup_location, pick_location, getApplicationContext());
                PreferencesUtils.saveData(Constants.trainee_name, name, getApplicationContext());
                if (data.getJSONObject("trainee_details").getString("trainee_user_image").length() > 1) {
                    PreferencesUtils.saveData(Constants.trainee_image, data.getJSONObject("trainee_details").getString("trainee_user_image"), getApplicationContext());
                }
                CommonCall.LoadImage(getApplicationContext(), data.getJSONObject("trainee_details").getString("trainee_user_image"), profileactionIcon, R.drawable.ic_man, R.drawable.ic_man);

                PreferencesUtils.saveData(Constants.trainee_id, traine_id, getApplicationContext());
                updateSocket();

                mSocket.connect();
                chatConnect();
//                startService(new Intent(getApplicationContext(), LocationService.class));
            } else {

                JSONObject data = new JSONObject(PreferencesUtils.getData(trainer_Data, getApplicationContext(), ""));
                JSONObject trainerDetail = data.getJSONObject("trainer_details");
                lat = trainerDetail.getString("trainer_latitude");
                lng = trainerDetail.getString("trainer_longitude");
                training_time = data.getInt("training_time");
                trainer_id = trainerDetail.getString("trainer_id");
                traine_id = PreferencesUtils.getData(Constants.user_id, getApplicationContext(), "");
                book_id = data.getString("book_id");
                PreferencesUtils.saveData(Constants.bookid, book_id, getApplicationContext());
                name = trainerDetail.getString("trainer_first_name") + " " + trainerDetail.getString("trainer_last_name");
                PreferencesUtils.saveData(Constants.trainee_id, traine_id, getApplicationContext());

                pick_latitude = Double.valueOf(data.getString("pick_latitude"));
                pick_longitude = Double.valueOf(data.getString("pick_longitude"));
                pick_location = data.getString("pick_location");
                PreferencesUtils.saveData(Constants.pickup_location, pick_location, getApplicationContext());
                CommonCall.LoadImage(getApplicationContext(), trainerDetail.getString("trainer_user_image"), profileactionIcon, R.drawable.ic_man, R.drawable.ic_man);
                PreferencesUtils.saveData(Constants.trainer_name, name, getApplicationContext());
                if (trainerDetail.getString("trainer_user_image").length() > 1) {
                    PreferencesUtils.saveData(Constants.trainer_image, trainerDetail.getString("trainer_user_image"), getApplicationContext());
                }else{
                    PreferencesUtils.saveData(Constants.trainer_image, "", getApplicationContext());
                }

                updateSocket();

                Controller.mSocket.connect();
                chatConnect();

                CommonCall.socketGetTrainerLocation();
            }

            sessionTimmer.setText(training_time + ":00");

//        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
//                .findFragmentById(map);
//        mapFragment.getMapAsync(this);
// trainging location location
            profileactionTitle.setText(name);
            latitude = Double.valueOf(pick_latitude);
            longitude = Double.valueOf(pick_longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(latitude == null || longitude == null){
            Toast.makeText(getApplicationContext(), "Please check your device Gps connection", Toast.LENGTH_SHORT).show();
        }else {
            camera = new LatLng(latitude, longitude);

            mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//            buildAlertMessageNoGps();
//
//        }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100, this);
            origin = usercamera;
            dest = camera;

        }

//        LoadmapTask();


    }

    HorizontalScrollView horizontalScrollView;

    void intstartStop() {

        start =  findViewById(R.id.start);
        cancel =  findViewById(R.id.cancel);
        profile =  findViewById(R.id.profile);
        message =  findViewById(R.id.message);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);


        startactionIcon =  findViewById(R.id.startactionIcon);
        stopactionIcon =  findViewById(R.id.stopactionIcon);
        profileactionIcon =  findViewById(R.id.profileactionIcon);
        messageactionIcon =  findViewById(R.id.messageactionIcon);
        cancelactionIcon =  findViewById(R.id.cancelactionIcon);

        startactionTitle =  findViewById(R.id.startactionTitle);
        stopactionTitle =  findViewById(R.id.stopactionTitle);
        profileactionTitle =  findViewById(R.id.profileactionTitle);
        messageactionTitle =  findViewById(R.id.messagectionTitle);


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
                if (CommonCall.isNetworkAvailable()) {

                    if (startactionTitle.getText().toString().equals("Start")) {


                        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee")
                                ) {


                            if (trainerLocation != null && checktrainerDistance()) {

                                Timer_Service.stopFlag = false;
                                new StartSession().execute();
                                profile.setEnabled(false);
                                message.setEnabled(false);
                            } else {

                                showAlert("It seems your trainer hasn't reached the designated location yet. Give it just a few more minutes, and we're sure he/she will be there!");
                            }
                        } else {

                            showAlert("Please ask the Trainee to start the session.");


                        }
                    } else {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        PreferencesUtils.saveData("stopped_s","true",getApplicationContext());
                                        PreferencesUtils.saveData(Constants.flag_rating, "true", getApplicationContext());
                                        stopAction();

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


                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

// Create custom dialog object
                cancelactionIcon.setImageResource(R.mipmap.cancel_blue);
                final Dialog dialog = new Dialog(SessionReady.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // Include dialog.xml file
                dialog.setContentView(R.layout.custom_dialog_yesno);
                // Set dialog title
                dialog.setTitle("Custom Dialog");
                dialog.setCancelable(false);
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
                            cancelFlag = true;
                            PreferencesUtils.saveData(Constants.flag_rating, "true", getApplicationContext());

                            new CommonCall.timerUpdate(SessionReady.this, "cancel", book_id, reason.getText().toString()).execute();


                        } else {


                            reason.setError("Please enter your reason to cancel.");

                        }


                    }
                });
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelactionIcon.setImageResource(R.mipmap.cross);
                        // Close dialog
                        dialog.dismiss();
                    }
                });


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
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferencesUtils.getData(Constants.timerstarted, getApplicationContext(), "false").equals("true")) {

                }else{
                if(CommonCall.isNetworkAvailable()){
                PreferencesUtils.saveData(Constants.from,"session ready",getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
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

            final SpannableStringBuilder sb = new SpannableStringBuilder(millisUntilFinished);


            final ForegroundColorSpan fcs = new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.black));


//            sb.setSpan(fcs, 3, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sessionTimmer.setText(millisUntilFinished);
            if (millisUntilFinished.equals("Session Completed")) {
                sessionTimmer.setText("00:00");
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


                Toast.makeText(getApplicationContext(), "Session Completed", Toast.LENGTH_SHORT).show();

//                Intent intenthome = new Intent(getApplicationContext(), HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intenthome);
//                finish();


            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//      CommonCall.showLoader(SessionReady.this);

        if (PreferencesUtils.getData(Constants.timerstarted, getApplicationContext(), "false").equals("true")) {


            cancel.setEnabled(false);
            startactionIcon.setImageResource(R.mipmap.stop_blue);
//            startService(new Intent(getApplicationContext(), LocationService.class));
            startService(new Intent(SessionReady.this, Timer_Service.class));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Controller.listenEvent();
        if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            if (origin.latitude != 0) {
                SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                        .findFragmentById(map);
                mapFragment.getMapAsync(this);
            }
        }


        if (PreferencesUtils.getData(Constants.timerstarted, getApplicationContext(), "false").equals("true")) {


            cancel.setEnabled(false);
            startactionIcon.setImageResource(R.mipmap.stop_blue);
            startService(new Intent(getApplicationContext(), LocationService.class));
            startService(new Intent(SessionReady.this, Timer_Service.class));


        }

/*
*
* br register
*
* */

        startauto();
        stopauto();
        registerReceiver(br, new IntentFilter(Timer_Service.str_receiver));
        resetTimmer();

        cancelAuto();

        if (getIntent().getStringExtra("push_session") != null) {
            Intent intent;
            switch (getIntent().getStringExtra("push_session")) {


                case "1":

                    break;
                case "2":
                    intent = new Intent("BUDDI_TRAINER_START");

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    break;
                case "3":

                    PreferencesUtils.saveData(start_session, "false", Controller.getAppContext());

                    intent = new Intent("BUDDI_TRAINER_CANCEL");

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


                    break;
                case "4":
                    intent = new Intent("BUDDI_TRAINER_STOP");

                    sendBroadcast(intent);
                    break;
                case "5":

                    break;
                case "6":
                    intent = new Intent("BUDDI_SESSION_EXTEND");
                    intent.putExtra("extend_time", getIntent().getStringExtra("extend_time"));
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    break;

            }

            getIntent().removeExtra("push_session");
        } else {


            if (PreferencesUtils.getData(Constants.startSessionPush, getApplicationContext(), "").equals("true")) {
                PreferencesUtils.saveData(Constants.startSessionPush, "false", getApplicationContext());

                Intent intent = new Intent("BUDDI_TRAINER_START");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }


//        LoadmapTask();
    }


    void cancelAuto() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        PreferencesUtils.saveData(Constants.flag_rating, "true", getApplicationContext());

                        stopSession();


                    }
                }, new IntentFilter("BUDDI_TRAINER_CANCEL")


        );
    }

    void resetTimmer() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {


                        CommonCall.hideLoader();

                        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {

                            if (Count != null)
                                Count.cancel();
                        }
                        startactionTitle.setText("Start");
                        duration = intent.getStringExtra("extend_time");

                        if (duration.equals("40")) {

                            PreferencesUtils.saveData(Constants.transactionId40, "", getApplicationContext());

                        } else {
                            PreferencesUtils.saveData(Constants.transactionId60, "", getApplicationContext());


                        }

                        startactionIcon.setImageResource(R.mipmap.play);


                        startclick();


                        Toast.makeText(context, "Your Session has been Extended.", Toast.LENGTH_SHORT).show();
                    }
                }, new IntentFilter("BUDDI_SESSION_EXTEND")


        );


    }


    void startclick() {

        /*
        *
        * for handling the start click even in other screen
        *
        * */
        PreferencesUtils.saveData(Constants.startSessionPush, "false", getApplicationContext());


        Timer_Service.stopFlag = false;
        new StartSession().execute();
        profile.setEnabled(false);
        message.setEnabled(false);

    }

    BroadcastReceiver startAuto = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            startclick();

            if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {
                Toast.makeText(getApplicationContext(), " " + name + " has started the session. ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), " " + name + " has started the session. ", Toast.LENGTH_SHORT).show();

            }

        }
    };

    BroadcastReceiver trainerLocationbr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CommonCall.PrintLog("brodcastmsg trainerLocationbr", "brodcastmsg trainerLocationbr");
            String slat = intent.getStringExtra("trainer_latitude");
            String slng = intent.getStringExtra("trainer_longitude");
            // trainer location

            trainerLocation = new LatLng(Double.parseDouble(slat), Double.parseDouble(slng));


        }
    };

    boolean checktrainerDistance() {

        Location locationA = new Location("point A");

        locationA.setLatitude(trainerLocation.latitude);
        locationA.setLongitude(trainerLocation.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(origin.latitude);
        locationB.setLongitude(origin.longitude);

        float distance = locationA.distanceTo(locationB);

        CommonCall.PrintLog("distance", distance + "");

        Toast.makeText(getApplicationContext(), "Distance :" + distance + "", Toast.LENGTH_SHORT).show();
        if (distance <= 500) {


            return true;

        } else {

            return false;
        }


    }


    void startauto() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
                startAuto, new IntentFilter("BUDDI_TRAINER_START")


        );

        LocalBroadcastManager.getInstance(this).registerReceiver(trainerLocationbr
                , new IntentFilter("SOCKET_BUDDI_TRAINER_LOCATION"));


    }


    void stopAction() {
        Timer_Service.stopFlag = true;
        PreferencesUtils.saveData(Constants.timerstarted, "false", getApplicationContext());
        PreferencesUtils.saveData(Constants.flag_rating, "true", getApplicationContext());

        startactionTitle.setText("Start");
        startactionIcon.setImageResource(R.mipmap.play);

        PreferencesUtils.saveData("data", "", getApplicationContext());
        PreferencesUtils.saveData("hours", "", getApplicationContext());

        stopService(new Intent(getApplicationContext(), Timer_Service.class));


        NotificationManager nManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
        nManager.cancelAll();


        new CommonCall.timerUpdate(SessionReady.this, "stop", book_id, "").execute();

    }

    void stopSession() {


        Timer_Service.stopFlag = true;
        PreferencesUtils.saveData(Constants.timerstarted, "false", getApplicationContext());


        PreferencesUtils.saveData(start_session, "false", Controller.getAppContext());

        startactionTitle.setText("Start");
        startactionIcon.setImageResource(R.mipmap.play);

        PreferencesUtils.saveData("data", "", getApplicationContext());
        PreferencesUtils.saveData("hours", "", getApplicationContext());

        stopService(new Intent(getApplicationContext(), Timer_Service.class));


        NotificationManager nManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
        nManager.cancelAll();


        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();


    }


    public CountDownTimer Count;
    BroadcastReceiver stopAutobr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PreferencesUtils.saveData(Constants.flag_rating, "true", getApplicationContext());

            Timer_Service.stopFlag = true;
            stopService(new Intent(getApplicationContext(), Timer_Service.class));


            if (Count == null) {
                CommonCall.showLoader(SessionReady.this, "Completing session Please wait.");
                if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {


                    Count = new android.os.CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
//                                textic.setText("Time Left: " + millisUntilFinished / 1000);


                            CommonCall.PrintLog("timmer ", "tick" + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
//                                textic.setText("OUT OF TIME!");
                            CommonCall.hideLoader();
                            CommonCall.PrintLog("timmer ", "tick onFinish");

                            PreferencesUtils.saveData(Constants.flag_rating, "true", getApplicationContext());

//                        PreferencesUtils.saveData(Constants.flag_rating, "true", getApplicationContext());
                            stopSession();

                        }
                    };
                    Count.start();
                } else


                {

                    CommonCall.PrintLog("NO TIMMER  ", "NO TIMMER ");

                    stopSession();
                }
            } else {

    /*
    *
    * if alredy showing the loader and get stop auto from push
    *
    * */
                Count.cancel();
                CommonCall.hideLoader();
                CommonCall.PrintLog("timmer ", "tick onFinish");

                PreferencesUtils.saveData(Constants.flag_rating, "true", getApplicationContext());

                stopSession();
            }
        }


    };

    void stopauto() {


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("BUDDI_TRAINER_STOP");

        registerReceiver(
                stopAutobr, intentFilter
        );


        LocalBroadcastManager.getInstance(this).registerReceiver(brfinsih
                , new IntentFilter("BUDDI_TRAINER_SESSION_FINISH")
        );
    }

    BroadcastReceiver brfinsih =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {


//                        if (!Timer_Service.stopFlag)

                    {

                        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee")

                                && !cancelFlag) {


                            CommonCall.showExtendBokingDialog(SessionReady.this);


                        } else {

//                                Intent intenthome = new Intent(getApplicationContext(), HomeActivity.class);
//                                intenthome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intenthome);
//                                finish();


                            CommonCall.showLoader(SessionReady.this, "Completing session Please wait.");
                            if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {
                                Count = new android.os.CountDownTimer(60000, 1000) {
                                    public void onTick(long millisUntilFinished) {
//                                textic.setText("Time Left: " + millisUntilFinished / 1000);


                                        CommonCall.PrintLog("timmer ", "tick" + millisUntilFinished / 1000);
                                    }

                                    public void onFinish() {
//                                textic.setText("OUT OF TIME!");
                                        CommonCall.hideLoader();
                                        CommonCall.PrintLog("timmer ", "tick onFinish");


//                                PreferencesUtils.saveData(Constants.flag_rating, "true", getApplicationContext());
                                        String bookid = PreferencesUtils.getData(Constants.bookid, getApplicationContext(), "");


                                        new CommonCall.timerUpdate(SessionReady.this, "complete", bookid, "").execute();

                                    }
                                };
                                Count.start();
                            } else {


                                String bookid = PreferencesUtils.getData(Constants.bookid, getApplicationContext(), "");


                                new CommonCall.timerUpdate(SessionReady.this, "complete", bookid, "").execute();
                            }
                        }

                    }


//


                }
            };


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);

        unregisterReceiver(stopAutobr);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brfinsih);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(startAuto);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(trainerLocationbr);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mSocket != null)
//            mSocket.disconnect();
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

        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&Key=" + getString(R.string.google_maps_key);

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

    /******
     * Showing Training location & Trainer||Trainee location on map
     ******/
    private void showMarker() {

/*      try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    try {

//                        user_Marker = googleMap.addMarker(new MarkerOptions().position(camera).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_man)).title(name.toUpperCase()).draggable(false));
//                        user_Marker.showInfoWindow();
                        googleMap.setInfoWindowAdapter(SessionReady.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancel() {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        pos_Marker = googleMap.addMarker(new MarkerOptions().position(camera).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).title(pick_location).draggable(false));
        pos_Marker.showInfoWindow();
    }

    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SessionReady.this, android.R.style.Theme_Material_Dialog_Alert);
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
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        if (!((Activity) this).isFinishing()) {
            alert.show();
        }
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
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;


        googleMap.setMyLocationEnabled(true);

      /*googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 13));
        Marker pos_Marker =  googleMap.addMarker(new MarkerOptions().position(camera).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).title("Starting Location").draggable(false));
        pos_Marker.showInfoWindow();*/

//        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.my_map_style);
//        googleMap.setMapStyle(style);
        googleMap.setInfoWindowAdapter(SessionReady.this);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 14), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                LoadmapTask();
                showMarker();
//                                animateMarker(pos_Marker, camera, false, 0.0f);
            }

            @Override
            public void onCancel() {

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        if (googleMap != null) {
            googleMap.clear();

            MarkerOptions mp = new MarkerOptions();
            mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

            googleMap.addMarker(mp);

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 16));
            origin = new LatLng(location.getLatitude(), location.getLongitude());


        }
        if (origin.latitude != 0)
            LoadmapTask();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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
            List<List<HashMap<String, String>>> routes = new ArrayList<>();

            try {
                jObject = new JSONObject(jsonData[0]);
                CommonCall.PrintLog("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                CommonCall.PrintLog("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                CommonCall.PrintLog("ParserTask", "Executing routes");
                CommonCall.PrintLog("ParserTask", routes.toString());

            } catch (Exception e) {
                CommonCall.PrintLog("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {

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
                    lineOptions.width(7);
                    lineOptions.color(getResources().getColor(R.color.marker_color));

                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    googleMap.addPolyline(lineOptions);
                    showMarker();
                } else {
                    Log.d("onPostExecute", "without Polylines drawn");
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            CommonCall.showLoader(SessionReady.this,"Session is going to start now...");
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

                    PreferencesUtils.saveData("stopped_s","true",getApplicationContext());

                    if (training_time == 40) {

                        PreferencesUtils.saveData(Constants.transactionId40, "", getApplicationContext());

                    } else {
                        PreferencesUtils.saveData(Constants.transactionId60, "", getApplicationContext());


                    }


                    startactionTitle.setText("Stop");
                    cancel.setEnabled(false);

                    startactionIcon.setImageResource(R.mipmap.stop_blue);


                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    String date_time = simpleDateFormat.format(calendar.getTime());


                    PreferencesUtils.saveData("data", date_time, getApplicationContext());
                    PreferencesUtils.saveData("hours", training_time + "", getApplicationContext());

//                    if (training_time == 40)
//                        PreferencesUtils.saveData("hours", "2", getApplicationContext());
//                    else
//                        PreferencesUtils.saveData("hours", "4", getApplicationContext());


                    startService(new Intent(SessionReady.this, Timer_Service.class));

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

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    void showAlert(String msg) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked


                        break;


                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(SessionReady.this);
        builder.setMessage(msg).setPositiveButton("OK", dialogClickListener)
                .show();

    }
}
