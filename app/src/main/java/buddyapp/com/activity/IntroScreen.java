package buddyapp.com.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

public class IntroScreen extends AppCompatActivity {
TextView trainer, trainee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);
        trainee = (TextView) findViewById(R.id.trainee);
        trainer = (TextView) findViewById(R.id.trainer);

        trainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesUtils.saveData(Constants.user_type,"trainer",getApplicationContext());
                Intent i = new Intent(getApplicationContext(),WelcomeActivity.class);
                startActivity(i);
            }
        });
        trainee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesUtils.saveData(Constants.user_type,"trainee",getApplicationContext());
                Intent i = new Intent(getApplicationContext(),WelcomeActivity.class);
                startActivity(i);
            }
        });
    }
}
