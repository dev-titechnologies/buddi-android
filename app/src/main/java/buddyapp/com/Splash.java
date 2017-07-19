package buddyapp.com;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

import buddyapp.com.activity.IntroScreen;
import buddyapp.com.activity.WelcomeActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        PreferencesUtils.saveData(Constants.device_id,android_id,getApplicationContext());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), IntroScreen.class));
                finish();
            }
        }, 3000);

    }
}
