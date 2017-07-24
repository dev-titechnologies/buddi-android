package buddyapp.com;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

import buddyapp.com.activity.ChooseCategory;
import buddyapp.com.activity.HomeActivity;
import buddyapp.com.activity.IntroScreen;
import buddyapp.com.activity.WelcomeActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        PreferencesUtils.saveData(Constants.device_id,android_id,getApplicationContext());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if (PreferencesUtils.getData(Constants.token,getApplicationContext(),"").length()>1) {

                    if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals(Constants.trainer)) {


                        if(PreferencesUtils.getData(Constants.trainer_status,getApplicationContext(),"").equals(Constants.approved)) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();


                        }else{

                            startActivity(new Intent(getApplicationContext(), ChooseCategory.class));
                            finish();

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
