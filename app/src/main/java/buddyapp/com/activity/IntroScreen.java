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
    boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);
        getSupportActionBar().hide();

        trainee = (TextView) findViewById(R.id.trainee);
        trainer = (TextView) findViewById(R.id.trainer);
        String login_type= getIntent().getStringExtra("login_type");
        if(login_type.equals("login")){
            flag = true;
            trainee.setText("LOGIN AS A TRAINEE");
            trainer.setText("LOGIN AS A TRAINER");
        }else{
            flag = false;
            trainee.setText("REGISTER AS A TRAINEE");
            trainer.setText("REGISTER AS A TRAINER");
        }
        trainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesUtils.saveData(Constants.user_type,"trainer",getApplicationContext());
                Intent intent;
                if(flag) {
                    intent = new Intent(getApplicationContext(), LoginScreen.class);
                }else{
                    intent = new Intent(getApplicationContext(), RegisterScreen.class);
                }
                startActivity(intent);
            }
        });
        trainee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesUtils.saveData(Constants.user_type,"trainee",getApplicationContext());
                Intent intent;
                if(flag) {
                    intent = new Intent(getApplicationContext(), LoginScreen.class);
                }else{
                    intent = new Intent(getApplicationContext(), RegisterScreen.class);
                }
                startActivity(intent);
            }
        });
    }
}
