package buddyapp.com.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.Fragment.BookingHistory;
import buddyapp.com.activity.Fragment.HomeCategory;
import buddyapp.com.activity.Fragment.Legal;
import buddyapp.com.activity.Payments.PaymentType;
import buddyapp.com.services.LocationService;
import buddyapp.com.utils.CircleImageView;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;


import static buddyapp.com.Settings.Constants.source_become_trainer;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    CircleImageView userImageView;
    LinearLayout root_profile;
    TextView name, email,rating;
    JSONObject data;
    Menu menu;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Buddi");

        Fragment fragment = new HomeCategory();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
// ********************* navigation view *****************8
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);

        menu =navigationView.getMenu();

        if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals("trainer"))
        menu.findItem(R.id.nav_trainer).setTitle("Add category");

        if(PreferencesUtils.getData(Constants.trainer_type,getApplicationContext(),"").equals("true")) {
         if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals("trainer"))
         {
             menu.findItem(R.id.nav_trainer).setVisible(true);
         }
         else
             menu.findItem(R.id.nav_trainer).setVisible(false);
        }
        userImageView = (CircleImageView) hView.findViewById(R.id.userImageView);
        name = (TextView) hView.findViewById(R.id.name);
        email = (TextView) hView.findViewById(R.id.email);
        rating = (TextView) hView.findViewById(R.id.rating);
        root_profile = (LinearLayout) hView.findViewById(R.id.root_profile);

        try {

            name.setText(PreferencesUtils.getData(Constants.fname,getApplicationContext(),"")+" "+PreferencesUtils.getData(Constants.lname,getApplicationContext(),""));
            CommonCall.LoadImage(getApplicationContext(),PreferencesUtils.getData(Constants.user_image,getApplicationContext(),""), userImageView,R.drawable.ic_no_image,R.drawable.ic_account);
            email.setText(PreferencesUtils.getData(Constants.email,getApplicationContext(),""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        root_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ProfileScreen.class);
                startActivity(intent);
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
                super.onBackPressed();
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
                    doubleBackToExitPressedOnce=false;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getSupportActionBar().setTitle("Buddi");
            Fragment fragment = new HomeCategory();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();


        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            Fragment fragment = new BookingHistory();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        } else if (id == R.id.nav_payment) {
            getSupportActionBar().setTitle("Payment");

            Intent payment = new Intent(getApplicationContext(), PaymentType.class);
startActivity(payment);
        } else if (id == R.id.nav_trainer) {
            source_become_trainer=true;
            Intent intent = new Intent(getApplicationContext(),ChooseCategory.class);
            startActivity(intent);
        } else if (id == R.id.nav_invite) {
            getSupportActionBar().setTitle("Invite Friends");
            Fragment fragment = new BookingHistory();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        } else if (id == R.id.nav_history) {
            getSupportActionBar().setTitle("Training History");
            Fragment fragment = new BookingHistory();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        }else if (id == R.id.nav_help) {
            getSupportActionBar().setTitle("Help");
            Fragment fragment = new BookingHistory();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        }else if (id == R.id.nav_legal) {
            Fragment fragment = new Legal();
            getSupportActionBar().setTitle("Legal");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        } else if (id == R.id.nav_logout) {

            new LogOutTask().execute();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
//
    //
        // ****** LOg Out *******
    class LogOutTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            String response = NetworkCalls.POST(Urls.getLogoutURL(),"");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    PreferencesUtils.cleardata(HomeActivity.this);
//
                    CommonCall.sessionout(HomeActivity.this);
                    finish();
                }else if(obj.getInt("status") == 2){
                    Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT);
                }
            }catch (JSONException e){
               e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonCall.LoadImage(getApplicationContext(),PreferencesUtils.getData(Constants.user_image,getApplicationContext(),""), userImageView,R.drawable.ic_no_image,R.drawable.ic_account);

        if (PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals(Constants.trainer)){

            mSocket.connect();
            startService(new Intent(this, LocationService.class));

        }
    }
}
