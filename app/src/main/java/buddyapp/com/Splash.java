package buddyapp.com;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

import buddyapp.com.activity.ChooseCategory;
import buddyapp.com.activity.HomeActivity;
import buddyapp.com.activity.IntroScreen;
import buddyapp.com.activity.WelcomeActivity;
import buddyapp.com.activity.questions.DoneActivity;
import buddyapp.com.utils.CommonCall;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if (PreferencesUtils.getData(Constants.token,getApplicationContext(),"").length()>1) {

                    if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals(Constants.trainer)) {
        try {

            if (new JSONArray(PreferencesUtils.getData(Constants.approved, getApplicationContext(), "[]")).length() == 0) {
                if (new JSONArray(PreferencesUtils.getData(Constants.pending, getApplicationContext(), "[]")).length() == 0) {
                    startActivity(new Intent(getApplicationContext(), ChooseCategory.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), DoneActivity.class));
                    finish();

                }
            }else {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();

            }
        }catch (JSONException e){
            e.printStackTrace();
        }

                        }else{
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();

                    }

                    }else
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                finish();

            }
        }, 3000);

    }
}
