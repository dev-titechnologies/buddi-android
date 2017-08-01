package buddyapp.com.activity.questions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.HomeActivity;

import static buddyapp.com.Settings.Constants.source_become_trainer;

public class DoneActivity extends AppCompatActivity {
Button exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        exit=(Button)findViewById(R.id.exit);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (source_become_trainer) {

                    PreferencesUtils.saveData(Constants.trainer_type,"true",getApplicationContext());
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        return;
    }
}
