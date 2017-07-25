package buddyapp.com.activity.questions;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;


import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.ChooseCategory;
import buddyapp.com.adapter.SubCategoryAdapter;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.utils.CommonCall;

public class Question4 extends AppCompatActivity {
    Button next, yes_pounds, no_pounds;

    ListView sub_list;
    DatabaseHandler db;
    SubCategoryAdapter subCategoryAdapter;

    public static ArrayList<String> sub_cat_selectedID;
    EditText weight;
    HashSet subCats;

    String pounds = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question4);


        sub_cat_selectedID = new ArrayList<>();
        db = new DatabaseHandler(getApplicationContext());
        yes_pounds = (Button) findViewById(R.id.yes_pounds);
        no_pounds = (Button) findViewById(R.id.no_pounds);
        next = (Button) findViewById(R.id.next);
        weight = (EditText) findViewById(R.id.weight);
        sub_list = (ListView) findViewById(R.id.sub_list);

        yes_pounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pounds = "yes";
            }
        });
        no_pounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pounds = "no";
            }
        });

        ArrayList<JSONObject> values = new ArrayList<JSONObject>();
        HashSet<JSONObject> hashSet = new HashSet<JSONObject>();


        subCats = db.getSubCat(ChooseCategory.cat_selectedID);


        subCategoryAdapter = new SubCategoryAdapter(getApplicationContext(), subCats, db);
        sub_list.setAdapter(subCategoryAdapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sub_cat_selectedID.size() == 0) {

                    Toast.makeText(Question4.this, "Please choose your category.", Toast.LENGTH_SHORT).show();
                } else if (weight.getText().toString().trim().length() == 0) {

                    Toast.makeText(Question4.this, "Please enter your weight.", Toast.LENGTH_SHORT).show();

                } else if (pounds.length() == 0) {

                    Toast.makeText(Question4.this, "Please answer the question.", Toast.LENGTH_SHORT).show();

                } else {


                    JSONObject questionData = new JSONObject();

                    try {
                        questionData.put("zipcode", PreferencesUtils.getData(Constants.zipcode, getApplicationContext(), ""));

                        questionData.put("military_installations", PreferencesUtils.getData(Constants.military_installations, getApplicationContext(), ""));
                        questionData.put("gym_subscriptions", PreferencesUtils.getData(Constants.gym_subscriptions, getApplicationContext(), ""));

                        questionData.put("training_exp", PreferencesUtils.getData(Constants.training_exp, getApplicationContext(), ""));

                        questionData.put("competed_category", PreferencesUtils.getData(Constants.competed_category, getApplicationContext(), ""));
                        questionData.put("certified_trainer", PreferencesUtils.getData(Constants.certified_trainer, getApplicationContext(), ""));

                        questionData.put("weight", weight.getText().toString());

                        questionData.put("pounds", pounds);
                        questionData.put("category", ChooseCategory.cat_selectedID);
                        questionData.put("sub_cat", sub_cat_selectedID.toString());


                        CommonCall.PrintLog("data", questionData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    startActivity(new Intent(getApplicationContext(), BeforeVideoActivity.class));

                }


            }
        });
    }
}
