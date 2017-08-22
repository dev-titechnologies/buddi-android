package buddyapp.com.activity.questions;


import android.app.Activity;

import android.os.AsyncTask;

import android.os.Build;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.ChooseCategory;
import buddyapp.com.adapter.SubCategoryAdapter;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

import static buddyapp.com.Settings.Constants.questionData;


public class Question4 extends Activity {
    Button next, yes_pounds, no_pounds;
    Spinner spinner;
    ListView sub_list;
    DatabaseHandler db;
    SubCategoryAdapter subCategoryAdapter;

    public static ArrayList<String> sub_cat_selectedID;
    HashSet subCats;

    ImageView back;
    String pounds = "";

    private Integer[] hundredsSpinner;
    private String[] onesSpinner;
    int shundreds=0,sones=0, weight;
    Spinner hundreds, ones;
    private String[] spinnerArray;

    boolean outdoorFlag= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question4);


        sub_cat_selectedID = new ArrayList<>();

        spinner = (Spinner) findViewById(R.id.spinner);
        db = new DatabaseHandler(getApplicationContext());
//        yes_pounds = (Button) findViewById(R.id.yes_pounds);
//        no_pounds = (Button) findViewById(R.id.no_pounds);
        next = (Button) findViewById(R.id.next);

        sub_list = (ListView) findViewById(R.id.sub_list);
        back = (ImageView) findViewById(R.id.back);

        hundreds = (Spinner) findViewById(R.id.hundreds);
        this.hundredsSpinner = new Integer[] {
               100,200,300,400
        };
        ArrayAdapter<Integer> adapter1 = new ArrayAdapter<Integer>(this,
                R.layout.spinner_item, hundredsSpinner);
        hundreds.setAdapter(adapter1);


        ArrayList<Integer> numbers = new ArrayList<Integer>(100);
        for (int i = 1; i < 100; i++)
        {
            numbers.add(i);
        }
        ones = (Spinner) findViewById(R.id.ones);
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(this,
                R.layout.spinner_item, numbers);
        ones.setAdapter(adapter2);

        shundreds = Integer.parseInt(hundreds.getSelectedItem().toString());
        sones = Integer.parseInt(ones.getSelectedItem().toString());

        weight = shundreds+sones;
        this.spinnerArray = new String[] {
                "Not at all","Somewhat knowledgeable", "Extremely knowledgeable"
        };
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
                R.layout.spinner_item, spinnerArray);
        spinner.setAdapter(adapter3);
        pounds = String.valueOf(spinner.getSelectedItem());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pounds = "yes";

                finish();
            }
        });
        /*yes_pounds.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                pounds="yes";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    yes_pounds.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    no_pounds.setBackgroundColor(getResources().getColor(R.color.white));

                    no_pounds.setTextColor(getResources().getColor(R.color.black));
                    yes_pounds.setTextColor(getResources().getColor(R.color.white));
                }


            }
        });
        no_pounds.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                pounds="no";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    yes_pounds.setBackgroundColor(getResources().getColor(R.color.white));
                    no_pounds.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    yes_pounds.setTextColor(getResources().getColor(R.color.black));

                    no_pounds.setTextColor(getResources().getColor(R.color.white));

                }

            }
        });*/

        ArrayList<JSONObject> values = new ArrayList<JSONObject>();
        HashSet<JSONObject> hashSet = new HashSet<JSONObject>();


        subCats = db.getSubCat(ChooseCategory.cat_selectedID);


        subCategoryAdapter = new SubCategoryAdapter(getApplicationContext(), subCats, db);



        sub_list.setAdapter(subCategoryAdapter);

        if (subCats.size()==0){
            outdoorFlag = true;// no sub categroy for outdoor
            sub_list.setVisibility(View.GONE);
        }else{
            sub_list.setVisibility(View.VISIBLE);
        }


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sub_cat_selectedID.size() == 0 && !outdoorFlag ) {

                    Toast.makeText(Question4.this, "Please choose your category.", Toast.LENGTH_SHORT).show();
                } else if (weight == 0) {

                    Toast.makeText(Question4.this, "Please enter your weight.", Toast.LENGTH_SHORT).show();

                } else if (pounds.length() == 0) {

                    Toast.makeText(Question4.this, "Please answer the question.", Toast.LENGTH_SHORT).show();

                } else {


                    JSONObject question = new JSONObject();

                    try {


                        question.put("zipcode", PreferencesUtils.getData(Constants.zipcode, getApplicationContext(), ""));

                        question.put("military_installations", PreferencesUtils.getData(Constants.military_installations, getApplicationContext(), ""));

//                        List<String> myList = new ArrayList<String>(Arrays.asList(PreferencesUtils.getData(Constants.gym_subscriptions, getApplicationContext(), "").split(",")));

                        question.put("gym_subscriptions", (PreferencesUtils.getData(Constants.gym_subscriptions, getApplicationContext(), "")));

                        question.put("training_exp", PreferencesUtils.getData(Constants.training_exp, getApplicationContext(), ""));

                        question.put("competed_category", PreferencesUtils.getData(Constants.competed_category, getApplicationContext(), ""));


                        question.put("coached_anybody", PreferencesUtils.getData(Constants.coached_anybody, getApplicationContext(), ""));


                        question.put("certified_trainer", PreferencesUtils.getData(Constants.certified_trainer, getApplicationContext(), ""));

                        question.put("weight", weight+"");

                        question.put("pounds", pounds);
                        questionData.put("cat_ids",new JSONArray( ChooseCategory.cat_selectedID));

                        questionData.put("sub_cat",new JSONArray( sub_cat_selectedID));
                        questionData.put("user_id", PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));
                        questionData.put("user_type", PreferencesUtils.getData(Constants.user_type, getApplicationContext(), ""));


                        questionData.put("questions", question.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (CommonCall.isNetworkAvailable())
                        new  saveQuestionS().execute();
                    else {
                        Toast.makeText(getApplicationContext(), " Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });
    }


    class saveQuestionS extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            CommonCall.showLoader(Question4.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = NetworkCalls.POST(Urls.getADDTRAINECATEGORYURL(), Constants.questionData.toString());

            return response;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                CommonCall.hideLoader();
                JSONObject obj = new JSONObject(s);
                if (obj.getInt(Constants.status) == 1) {


                    Toast.makeText(Question4.this, "  Success.", Toast.LENGTH_SHORT).show();


                    if(!outdoorFlag)
                    startActivity(new Intent(getApplicationContext(), BeforeVideoActivity.class));
                else{

                        PreferencesUtils.saveData(Constants.pending, ChooseCategory.cat_selectedID.toString(),getApplicationContext());

                        startActivity(new Intent(getApplicationContext(), DoneActivity.class));
                    }






                } else if (obj.getInt(Constants.status) == 2) {


                    Toast.makeText(Question4.this, obj.getString("message"), Toast.LENGTH_SHORT).show();




                } else {


                    //session out



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
