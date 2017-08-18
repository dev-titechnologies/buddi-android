package buddyapp.com.activity.questions;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.ProfileScreen;

public class Question1 extends Activity {
    Button next;
    EditText zipcode;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question1);
        zipcode=(EditText)findViewById(R.id.zipcode);
        next=(Button)findViewById(R.id.next);
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (zipcode.getText().toString().trim().length()>1 && zipcode.getText().toString().trim().length()<10) {

                    PreferencesUtils.saveData(Constants.zipcode,zipcode.getText().toString(),getApplicationContext());
                    startActivity(new Intent(getApplicationContext(), Question2.class));

                }else{
                    zipcode.setError("Invalid Zipcode");
                }
            }
        });

        zipcode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() > 4)
                   next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                else
                    next.setBackgroundColor(getResources().getColor(R.color.button_bgcolor));
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }

}
