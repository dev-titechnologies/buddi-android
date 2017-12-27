package buddiapp.com.activity.questions;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;

public class Question3 extends Activity {
    Button next,yes_competed_category,no_competed_category,yes_coached_anybody,no_coached_anybody
       ,yes_certified_trainer,no_certified_trainer;
    ImageView back;
    String competed_category="",coached_anybody="",certified_trainer ="";
    Spinner year;
    Spinner month;
    String training_exp;
    private String[] yearSpinner;
    private String[] monthSpinner;
    String syear= "0",smonth="0", totalexp;
    boolean check1 = false, check2 = false, check3 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question3);
        next=(Button)findViewById(R.id.next);
        yes_competed_category=(Button)findViewById(R.id.yes_competed_category);
        no_competed_category=(Button)findViewById(R.id.no_competed_category);
        yes_coached_anybody=(Button)findViewById(R.id.yes_coached_anybody);
        no_coached_anybody=(Button)findViewById(R.id.no_coached_anybody);
        yes_certified_trainer=(Button)findViewById(R.id.yes_certified_trainer);
        no_certified_trainer=(Button)findViewById(R.id.no_certified_trainer);
        back = (ImageView) findViewById(R.id.back);

        this.yearSpinner = new String[] {
                "0","1", "2", "3", "4", "5","6","7","8","9","10+"
        };
        year = (Spinner) findViewById(R.id.year);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                R.layout.spinner_item, yearSpinner);
        year.setAdapter(adapter1);



        this.monthSpinner = new String[] {
                "0","1", "2", "3", "4", "5","6","7","8","9","10","11","12"
        };
        month = (Spinner) findViewById(R.id.month);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                R.layout.spinner_item,monthSpinner);
        month.setAdapter(adapter2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syear = year.getSelectedItem().toString();
                smonth = month.getSelectedItem().toString();
                training_exp = syear + " year" +" and "+ smonth + " months";
if (syear.equals("0")&&smonth.equals("0")){

   Toast.makeText(getApplicationContext(),"Please select your experience.", Toast.LENGTH_SHORT).show();

}else if (competed_category.length()==0){

    Toast.makeText(Question3.this, "Please Confirm your Competed Category field", Toast.LENGTH_SHORT).show();


}else if (coached_anybody.length()==0){


    Toast.makeText(Question3.this, "Please Confirm your Coached Anybody field", Toast.LENGTH_SHORT).show();

}else if (certified_trainer.length()==0){


    Toast.makeText(Question3.this, "Please Confirm your Certified Trainer field", Toast.LENGTH_SHORT).show();

}else {


    PreferencesUtils.saveData(Constants.training_exp,training_exp,getApplicationContext());

    PreferencesUtils.saveData(Constants.competed_category,competed_category,getApplicationContext());
    PreferencesUtils.saveData(Constants.coached_anybody,coached_anybody,getApplicationContext());
    PreferencesUtils.saveData(Constants.certified_trainer,certified_trainer,getApplicationContext());

    startActivity(new Intent(getApplicationContext(), Question4.class));


}    }
        });
        yes_competed_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check2 && check3){
                    next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                check1 = true;
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
                if(check2 && check3){
                    next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                check1 = true;
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
                if(check1 && check3){
                    next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                check2 = true;
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
                if(check1 && check3){
                    next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                check2 = true;
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
                if(check1 && check2){
                    next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                check3 = true;
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
                if(check1 && check2){
                    next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                check3 = true;
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
