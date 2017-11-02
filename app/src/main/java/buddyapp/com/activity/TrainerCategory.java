package buddyapp.com.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import buddyapp.com.adapter.TrainerCategoryAdapter;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class TrainerCategory extends AppCompatActivity {
    RelativeLayout root;
    DatabaseHandler db;
    GridView grid;
    TrainerCategoryAdapter trainerCategoryAdapter;
    public static ArrayList<String> settings_cat_selectedID = new ArrayList<>();
    public static Button done;
    ImageView errorImage;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
try{
        db.deleteCategory();}

        catch (Exception E){
    E.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_category);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Training categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DatabaseHandler(getApplicationContext());
        root = (RelativeLayout) findViewById(R.id.root);
        grid = (GridView) findViewById(R.id.grid);
        done = (Button) findViewById(R.id.next);

        errorImage = (ImageView) findViewById(R.id.errorImage);




        new checkStatus().execute();

    }

    void loadData(JSONArray data) {

        trainerCategoryAdapter = new TrainerCategoryAdapter(getApplicationContext(), data);
        grid.setAdapter(trainerCategoryAdapter);

    }

    class checkStatus extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(TrainerCategory.this);

        }

        @Override
        protected String doInBackground(String... strings) {

            JSONObject req = new JSONObject();
            try {

                if (getIntent().hasExtra("trainerId")){

                    req.put("user_id", getIntent().getStringExtra("trainerId"));
                    req.put("user_type", getIntent().getStringExtra("usertype"));



                }else {

                    req.put("user_id", PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));
                    req.put("user_type", PreferencesUtils.getData(Constants.user_type, getApplicationContext(), ""));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            String res = NetworkCalls.POST(Urls.getcategoryStatusURL(), req.toString());


            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);


            try {
                JSONObject obj = new JSONObject(res);
                CommonCall.PrintLog("result", obj.toString());
                if (obj.getInt("status") == 1) {
                    errorImage.setVisibility(View.GONE);
                    PreferencesUtils.saveData(Constants.approved, obj.getJSONObject("data").getJSONArray(Constants.approved).toString(), getApplicationContext());
                    PreferencesUtils.saveData(Constants.pending, obj.getJSONObject("data").getJSONArray(Constants.pending).toString(), getApplicationContext());
                    new getCategoryList().execute();

                } else if (obj.getInt("status") == 2) {
                    CommonCall.hideLoader();
                    errorImage.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar
                            .make(root, obj.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new checkStatus().execute();

                                }
                            });

                    snackbar.show();
                } else if (obj.getInt("status") == 3) {
                    CommonCall.hideLoader();
                    CommonCall.sessionout(TrainerCategory.this);
                    finish();
                }

            } catch (JSONException e) {
                CommonCall.hideLoader();
                e.printStackTrace();
            }

        }
    }

    class getCategoryList extends AsyncTask<String, String, String> {

        JSONObject reqData = new JSONObject();


        @Override
        protected void onPreExecute() {
//        CommonCall.showLoader(TrainerCategory.this);


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
                    CommonCall.PrintLog("cat", db.getCATForTrainer().toString());
                    loadData(db.getCATForTrainer());

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