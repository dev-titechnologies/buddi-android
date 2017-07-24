package buddyapp.com.activity.questions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

public class Question2 extends AppCompatActivity {
    Button next,yes_military_installations,no_military_installations;
    EditText gym_sub;

    String military_installations_selected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question2);

        next=(Button)findViewById(R.id.next);
        yes_military_installations=(Button)findViewById(R.id.yes_military_installations);
        no_military_installations=(Button)findViewById(R.id.no_military_installations);
        gym_sub=(EditText)findViewById(R.id.gym);

        yes_military_installations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                military_installations_selected ="yes";
            }
        });
        no_military_installations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                military_installations_selected ="no";
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (gym_sub.getText().toString().trim().length()<2) {

                    gym_sub.setError("Invalid Gym Subscription!");
                }else if (military_installations_selected.length()==0) {


                    Toast.makeText(Question2.this, "Please Select Military Instalations", Toast.LENGTH_SHORT).show();

                }  else{
                    PreferencesUtils.saveData(Constants.gym_subscriptions,gym_sub.getText().toString(),getApplicationContext());

                    PreferencesUtils.saveData(Constants.military_installations,military_installations_selected,getApplicationContext());
                    startActivity(new Intent(getApplicationContext(), Question3.class));
                }
            }
        });
    }
}
