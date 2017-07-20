package buddyapp.com.activity;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);
        db = new DatabaseHandler(getApplicationContext());
        root = (RelativeLayout) findViewById(R.id.root);
        grid = (GridView) findViewById(R.id.grid);


        new getCategoryList().execute();
    }

void loadData(JSONArray data){

    categoryAdapter = new CategoryAdapter(getApplicationContext(),data);
    grid.setAdapter(categoryAdapter);

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

                    db.insertCategory(response.getJSONArray("data"));
                    CommonCall.PrintLog("cat", db.getAllCAT().toString());



                    loadData(db.getAllCAT());

                } else if (response.getInt(Constants.status) == 2) {
                    Snackbar snackbar1 = null;

                    snackbar1 = Snackbar.make(root, response.getString(Constants.message), Snackbar.LENGTH_SHORT);

                    snackbar1.show();

                } else if (response.getInt(Constants.status) == 3) {


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
