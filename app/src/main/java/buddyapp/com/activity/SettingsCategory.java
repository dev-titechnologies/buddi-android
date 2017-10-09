package buddyapp.com.activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.adapter.SettingsCategoryAdapter;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class SettingsCategory extends AppCompatActivity {
    RelativeLayout root;
    DatabaseHandler db;
    GridView grid;
    SettingsCategoryAdapter settingsCategoryAdapter;
    public static ArrayList<String> settings_cat_selectedID = new ArrayList<>();
    public static Button done;
    ImageView errorImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category); getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("  Choose a Category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DatabaseHandler(getApplicationContext());
        root = (RelativeLayout) findViewById(R.id.root);
        grid = (GridView) findViewById(R.id.grid);
        done = (Button) findViewById(R.id.next);

        errorImage = (ImageView) findViewById(R.id.errorImage);

        if(db.getAllCATForTrainee().length()>0) {
            loadData(db.getAllCATForTrainee());
        }else {
            new getCategoryList().execute();
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                Intent intent = new Intent();
                intent.putExtra("name", PreferencesUtils.getData(Constants.settings_cat_name,getApplicationContext(),""));
                setResult(111,intent);
                finish();
            }
        });
    }

    void loadData(JSONArray data){

        settingsCategoryAdapter = new SettingsCategoryAdapter(getApplicationContext(),data);
        grid.setAdapter(settingsCategoryAdapter);

    }
    class getCategoryList extends AsyncTask<String, String, String> {

        JSONObject reqData = new JSONObject();


        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(SettingsCategory.this);


        }

        @Override
        protected String doInBackground(String... strings) {


            return NetworkCalls.POST(Urls.getCATEGORYURL(), reqData.toString());
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();

            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {
                    errorImage.setVisibility(View.GONE);
                    db.insertCategory(response.getJSONArray("data"));
                    CommonCall.PrintLog("cat",db.getAllCATForTrainee().toString());



                    loadData(db.getAllCATForTrainee());

                } else if (response.getInt(Constants.status) == 2) {
                    errorImage.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new getCategoryList().execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    /**
     * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}