package buddyapp.com;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

import buddyapp.com.activity.ChooseCategory;
import buddyapp.com.activity.HomeActivity;
import buddyapp.com.activity.IntroScreen;
import buddyapp.com.activity.RequestActivity;
import buddyapp.com.activity.Session;
import buddyapp.com.activity.SessionReady;
import buddyapp.com.activity.WelcomeActivity;
import buddyapp.com.activity.chat.ChatScreen;
import buddyapp.com.activity.questions.DoneActivity;
import buddyapp.com.timmer.Timer_Service;
import buddyapp.com.utils.CommonCall;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();


Log.e("launcher activity",getIntent().getStringExtra("type")+"");
        if (getIntent().getStringExtra("type")!=null && getIntent().getStringExtra("type").equals("4")){

            startActivity(new Intent(getApplicationContext(), SessionReady.class).putExtra("push_session","4"));

            finish();
        }else
        if (getIntent().getStringExtra("type")!=null && getIntent().getStringExtra("type").equals("3")){

            startActivity(new Intent(getApplicationContext(), SessionReady.class).putExtra("push_session","3"));

            finish();
        }else
        if (getIntent().getStringExtra("type")!=null && getIntent().getStringExtra("type").equals("2")){

            startActivity(new Intent(getApplicationContext(), SessionReady.class).putExtra("push_session","2"));

            finish();
        }
        else
        if (getIntent().getStringExtra("type")!=null && getIntent().getStringExtra("type").equals("6")){


            JSONObject data = null;
            try {
                data = new JSONObject(getIntent().getStringExtra("data"));



                startActivity(new Intent(getApplicationContext(), SessionReady.class)
                        .putExtra("push_session","6").putExtra("extend_time",data.getString("extend_time")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            finish();
        }
        else
        if (getIntent().getStringExtra("type")!=null && getIntent().getStringExtra("type").equals("5")){

            JSONObject data = null;
            try {
                data = new JSONObject(getIntent().getStringExtra("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Intent resultIntent = new Intent(getApplicationContext(), RequestActivity.class);
            resultIntent.putExtra("message",data.toString());
            try {
                resultIntent.putExtra("title",data.getString("noti_title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );

            startActivity(resultIntent);
        }
else
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
             if (PreferencesUtils.getData(Constants.token, getApplicationContext(), "").length() > 1) {

                    if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals(Constants.trainer)) {
                        try {

                            if (new JSONArray(PreferencesUtils.getData(Constants.approved, getApplicationContext(), "[]")).length() == 0) {
                                if (new JSONArray(PreferencesUtils.getData(Constants.pending, getApplicationContext(), "[]")).length() == 0) {
                                    startActivity(new Intent(getApplicationContext(), ChooseCategory.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), DoneActivity.class));
                                    finish();

                                }
                            } else {


                                if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {

                                    startService(new Intent(getApplicationContext(), Timer_Service.class));

                                    startActivity(new Intent(getApplicationContext(), SessionReady.class));
                                    finish();
                                } else

                                    {
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    finish();

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

/*
*
*
* trainee =>
*
* */
                        if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {

                            startService(new Intent(getApplicationContext(), Timer_Service.class));

                            startActivity(new Intent(getApplicationContext(), SessionReady.class));
                            finish();
                        } else {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();

                        }


                    }

                } else
                    startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                finish();

            }
        }, 3000);

    }
}
