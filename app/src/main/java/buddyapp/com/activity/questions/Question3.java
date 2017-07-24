package buddyapp.com.activity.questions;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

public class Question3 extends AppCompatActivity {
    Button next,yes_competed_category,no_competed_category,yes_coached_anybody,no_coached_anybody
       ,yes_certified_trainer,no_certified_trainer;

    String competed_category="",coached_anybody="",certified_trainer ="";
    EditText training_exp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question3);
        next=(Button)findViewById(R.id.next);
        training_exp=(EditText)findViewById(R.id.training_exp);

        yes_competed_category=(Button)findViewById(R.id.yes_competed_category);
        no_competed_category=(Button)findViewById(R.id.no_competed_category);
        yes_coached_anybody=(Button)findViewById(R.id.yes_coached_anybody);
        no_coached_anybody=(Button)findViewById(R.id.no_coached_anybody);
        yes_certified_trainer=(Button)findViewById(R.id.yes_certified_trainer);
        no_certified_trainer=(Button)findViewById(R.id.no_certified_trainer);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.text_training_history));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
if (training_exp.getText().toString().trim().length()==0){

    training_exp.setError("Please enter your experience.");

}else if (competed_category.length()==0){

    Toast.makeText(Question3.this, "Please Confirm your Competed Category field", Toast.LENGTH_SHORT).show();


}else if (coached_anybody.length()==0){


    Toast.makeText(Question3.this, "Please Confirm your Coached Anybody field", Toast.LENGTH_SHORT).show();

}else if (certified_trainer.length()==0){


    Toast.makeText(Question3.this, "Please Confirm your Certified Trainer field", Toast.LENGTH_SHORT).show();

}else {


    PreferencesUtils.saveData(Constants.training_exp,training_exp.getText().toString(),getApplicationContext());

    PreferencesUtils.saveData(Constants.competed_category,competed_category,getApplicationContext());
    PreferencesUtils.saveData(Constants.coached_anybody,coached_anybody,getApplicationContext());
    PreferencesUtils.saveData(Constants.certified_trainer,certified_trainer,getApplicationContext());

    startActivity(new Intent(getApplicationContext(), Question4.class));


}    }
        });

        yes_competed_category.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                competed_category ="yes";
                yes_competed_category.setPressed(true);
                no_competed_category.setPressed(false);
                return true;
            }
        });
        no_competed_category.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                competed_category ="no";
                no_competed_category.setPressed(true);
                yes_competed_category.setPressed(false);
                return true;
            }
        });
        yes_coached_anybody.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                coached_anybody ="yes";
                yes_coached_anybody.setPressed(true);
                no_coached_anybody.setPressed(false);
                return true;
            }
        });
        no_coached_anybody.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                coached_anybody ="no";
                no_coached_anybody.setPressed(true);
                yes_coached_anybody.setPressed(false);
                return true;
            }
        });
        yes_certified_trainer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                certified_trainer ="yes";
                yes_certified_trainer.setPressed(true);
                no_certified_trainer.setPressed(false);
                return true;
            }
        });
        no_certified_trainer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                certified_trainer ="no";
                no_certified_trainer.setPressed(true);
                yes_certified_trainer.setPressed(false);
                return true;
            }
        });

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
