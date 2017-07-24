package buddyapp.com.activity.questions;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.ProfileScreen;

public class Question1 extends AppCompatActivity {
    Button next;
    EditText zipcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question1);
        zipcode=(EditText)findViewById(R.id.zipcode);
        next=(Button)findViewById(R.id.next);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.what_zipcode));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (zipcode.getText().toString().trim().length()>4) {

                    PreferencesUtils.saveData(Constants.zipcode,zipcode.getText().toString(),getApplicationContext());
                    startActivity(new Intent(getApplicationContext(), Question2.class));

                }else{
                    zipcode.setError("Invalid Zipcode");
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
