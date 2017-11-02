package buddyapp.com.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.Controller;
import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.Fragment.BookingHistory;
import buddyapp.com.activity.Fragment.Help;
import buddyapp.com.activity.Fragment.HomeCategory;
import buddyapp.com.activity.Fragment.InviteFriends;
import buddyapp.com.activity.Fragment.Legal;
import buddyapp.com.activity.Fragment.Settings;
import buddyapp.com.activity.Fragment.TrainerProfileFragment;
import buddyapp.com.activity.Payments.PaymentType;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.fcm.Config;
import buddyapp.com.fcm.NotificationUtils;
import buddyapp.com.utils.CircleImageView;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

import static buddyapp.com.Settings.Constants.source_become_trainer;
import static buddyapp.com.Settings.Constants.start_session;
import static buddyapp.com.Settings.Constants.trainer_Data;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer_layout;
    CircleImageView userImageView;
    LinearLayout root_profile;
    TextView name, email, rating;
    JSONObject data;
    Menu menu;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NotificationUtils.clearNotifications(getApplicationContext());
        Controller.getInstance().trackScreenView("Home Screen");

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();


                }
            }
        };


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Buddi");

        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals(Constants.trainer)) {
            Fragment fragment = new TrainerProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        } else {
            Fragment fragment = new HomeCategory();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        }
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
// ********************* navigation view *****************8
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

        menu = navigationView.getMenu();

        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer"))
            menu.findItem(R.id.nav_trainer).setTitle("Add Category");

        if (PreferencesUtils.getData(Constants.trainer_type, getApplicationContext(), "").equals("true")) {
            if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {
                menu.findItem(R.id.nav_trainer).setVisible(true);


            } else {
                menu.findItem(R.id.nav_trainer).setVisible(false);


            }
        }


        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {


            menu.findItem(R.id.nav_settings).setVisible(false);
        } else {


            menu.findItem(R.id.nav_settings).setVisible(true);

        }


        userImageView = (CircleImageView) hView.findViewById(R.id.userImageView);
        name = (TextView) hView.findViewById(R.id.name);
        email = (TextView) hView.findViewById(R.id.email);
        rating = (TextView) hView.findViewById(R.id.rating);
        root_profile = (LinearLayout) hView.findViewById(R.id.root_profile);


        root_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonCall.isNetworkAvailable()) {
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(getApplicationContext(), ProfileScreen.class);
                    startActivity(intent);


                } else {
                    Toast.makeText(getApplicationContext(), " Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {

//                startService(new Intent(getApplicationContext(), Timer_Service.class));
                Toast.makeText(this, "You are already in a session", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getApplicationContext(), SessionReady.class));
                finish();
            } else {
                getSupportActionBar().setTitle("Buddi");

                if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals(Constants.trainer)) {
                    Fragment fragment = new TrainerProfileFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

                } else {
                    Fragment fragment = new HomeCategory();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
                }


            }

        } else if (id == R.id.nav_settings) {

            getSupportActionBar().setTitle("Settings");
            Fragment fragment = new Settings();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        } else if (id == R.id.nav_payment) {
//            getSupportActionBar().setTitle("Payment");

            Intent payment = new Intent(getApplicationContext(), PaymentType.class);
            startActivity(payment);
        } else if (id == R.id.nav_trainer) {


            if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {
                Toast.makeText(this, "You are already in a session", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SessionReady.class));
                finish();
            } else {
                source_become_trainer = true;
                Intent intent = new Intent(getApplicationContext(), ChooseCategory.class);
                startActivity(intent);

            }

        } else if (id == R.id.nav_invite) {

            getSupportActionBar().setTitle("Invite Friends");
            Fragment fragment = new InviteFriends();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        } else if (id == R.id.nav_history) {

            getSupportActionBar().setTitle("Training History");
            Fragment fragment = new BookingHistory();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        } else if (id == R.id.nav_help) {

            getSupportActionBar().setTitle("Help");
            Fragment fragment = new Help();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        } else if (id == R.id.nav_legal) {

            Fragment fragment = new Legal();
            getSupportActionBar().setTitle("Legal");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        } else if (id == R.id.nav_logout) {


            if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {

//                startService(new Intent(getApplicationContext(), Timer_Service.class));
                Toast.makeText(this, "You are already in a session", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getApplicationContext(), SessionReady.class));
                finish();
            } else

            {
                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(HomeActivity.this);
                } else {
                    builder = new AlertDialog.Builder(HomeActivity.this);
                }
                builder.setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new LogOutTask().execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .show();

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //
    //
    // ****** LOg Out *******
    class LogOutTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String response = NetworkCalls.POST(Urls.getLogoutURL(), "");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    db = new DatabaseHandler(getApplicationContext());
                    db.deleteDB();
//
                    CommonCall.sessionout(HomeActivity.this);
                    finish();
                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        CommonCall.LoadImage(getApplicationContext(), PreferencesUtils.getData(Constants.user_image, getApplicationContext(), ""), userImageView, R.drawable.ic_no_image, R.drawable.ic_account);
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // clear the notification area when the app is opened

        try {

            name.setText(PreferencesUtils.getData(Constants.fname, getApplicationContext(), "") + " " + PreferencesUtils.getData(Constants.lname, getApplicationContext(), ""));
            email.setText(PreferencesUtils.getData(Constants.email, getApplicationContext(), ""));
            CommonCall.LoadImage(getApplicationContext(), PreferencesUtils.getData(Constants.user_image, getApplicationContext(), ""), userImageView, R.drawable.ic_no_image, R.drawable.ic_account);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee")
                & (PreferencesUtils.getData(Constants.trainer_Data, getApplicationContext(), "").equals("")))
            new getSessions().execute();
    }

    public void clearBackstack() {

        FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(
                0);
        getSupportFragmentManager().popBackStack(entry.getId(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().executePendingTransactions();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public class getSessions extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));
                reqData.put(Constants.user_type, PreferencesUtils.getData(Constants.user_type, getApplicationContext(), ""));

            } catch (Exception e) {
                e.printStackTrace();
            }
            String response = NetworkCalls.POST(Urls.getpendingBookingURL(), reqData.toString());
            return response;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {


                    CommonCall.PrintLog("response123", response.toString());

                    if (response.getJSONArray("data").length() > 0) {
                        JSONObject sessions = response.getJSONArray("data").getJSONObject(0);


                        PreferencesUtils.saveData(Constants.trainer_id, sessions.getJSONObject("trainer_details").getString("trainer_id"), getApplicationContext());
                        PreferencesUtils.saveData(trainer_Data, sessions.toString(), getApplicationContext());

                        PreferencesUtils.saveData(start_session, "true", getApplicationContext());

                        Intent resultIntent = new Intent(getApplicationContext(), SessionReady.class);
                        resultIntent.putExtra("message", sessions.toString());


                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(resultIntent);


                    }
                } else if (response.getInt(Constants.status) == 2) {


                    Snackbar snackbar = Snackbar
                            .make(drawer_layout, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(drawer_layout, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new getSessions().execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {

                    CommonCall.sessionout(HomeActivity.this);
                }


            } catch (JSONException e) {


                e.printStackTrace();
                CommonCall.hideLoader();
            }
        }
    }


}
