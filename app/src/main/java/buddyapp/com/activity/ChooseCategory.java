package buddyapp.com.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.activity.questions.Question1;
import buddyapp.com.activity.questions.Question4;
import buddyapp.com.adapter.CategoryAdapter;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class ChooseCategory extends AppCompatActivity {


    RelativeLayout root;
    DatabaseHandler db;
    GridView grid;
    CategoryAdapter categoryAdapter;
    TextView notext;
    public static ArrayList<String> cat_selectedID = new ArrayList<>();

    ImageView errorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("  Choose a Category");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        db = new DatabaseHandler(getApplicationContext());

        notext = (TextView) findViewById(R.id.nocat_text);

        root = (RelativeLayout) findViewById(R.id.root);
        grid = (GridView) findViewById(R.id.grid);

        errorImage = (ImageView) findViewById(R.id.errorImage);
        new getCategoryList().execute();
    }

    void loadData(JSONArray data) {

        categoryAdapter = new CategoryAdapter(getApplicationContext(), data);
        grid.setAdapter(categoryAdapter);

        if (categoryAdapter.getCount() == 0) {

            notext.setVisibility(View.VISIBLE);

        }

    }

    class getCategoryList extends AsyncTask<String, String, String> {

        JSONObject reqData = new JSONObject();


        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(ChooseCategory.this);


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
                    CommonCall.PrintLog("cat", db.getAllCAT().toString());


                    loadData(db.getAllCAT());

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
     * the menu layout has the 'add/new' menu item
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_category, menu);
        return true;
    }

    /**
     * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                // TODO put your code here to respond to the button tap


                if (cat_selectedID.size() > 0) {

                    /*
                    *
                    *
                    * check if this trainer has already completd the querstions by uploading the videos
                    *
                    * */
                    if (db.getcountSELECTEDCAT() > 0) {

                        startActivity(new Intent(getApplicationContext(), Question4.class));
                    } else
                        startActivity(new Intent(getApplicationContext(), Question1.class));


                } else {

                    Toast.makeText(this, "Please choose a category to continue.", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
