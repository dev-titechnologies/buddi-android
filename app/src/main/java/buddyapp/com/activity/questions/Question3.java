package buddyapp.com.activity.questions;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

public class Question3 extends Activity {
    Button next,yes_competed_category,no_competed_category,yes_coached_anybody,no_coached_anybody
       ,yes_certified_trainer,no_certified_trainer;
    ImageView back;
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
        yes_competed_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                competed_category ="yes";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    yes_competed_category.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    no_competed_category.setBackgroundColor(getResources().getColor(R.color.white));

                    no_competed_category.setTextColor(getResources().getColor(R.color.black));
                    yes_competed_category.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        no_competed_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                competed_category ="no";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    yes_competed_category.setBackgroundColor(getResources().getColor(R.color.white));
                    no_competed_category.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    yes_competed_category.setTextColor(getResources().getColor(R.color.black));

                    no_competed_category.setTextColor(getResources().getColor(R.color.white));

                }
            }
        });


        yes_coached_anybody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coached_anybody ="yes";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    yes_coached_anybody.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    no_coached_anybody.setBackgroundColor(getResources().getColor(R.color.white));

                    no_coached_anybody.setTextColor(getResources().getColor(R.color.black));
                    yes_coached_anybody.setTextColor(getResources().getColor(R.color.white));

                }
            }
        });
        no_coached_anybody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coached_anybody ="no";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    yes_coached_anybody.setBackgroundColor(getResources().getColor(R.color.white));
                    no_coached_anybody.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    yes_coached_anybody.setTextColor(getResources().getColor(R.color.black));

                    no_coached_anybody.setTextColor(getResources().getColor(R.color.white));

                }
            }
        });


        yes_certified_trainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                certified_trainer ="yes";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    yes_certified_trainer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    no_certified_trainer.setBackgroundColor(getResources().getColor(R.color.white));

                    no_certified_trainer.setTextColor(getResources().getColor(R.color.black));
                    yes_certified_trainer.setTextColor(getResources().getColor(R.color.white));

                }
            }
        });
        no_certified_trainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                certified_trainer ="no";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    yes_certified_trainer.setBackgroundColor(getResources().getColor(R.color.white));
                    no_certified_trainer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    yes_certified_trainer.setTextColor(getResources().getColor(R.color.black));

                    no_certified_trainer.setTextColor(getResources().getColor(R.color.white));

                }
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
