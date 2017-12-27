package buddiapp.com.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.Common;

import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.database.DatabaseHandler;
import buddiapp.com.fcm.NotificationUtils;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;

import static buddiapp.com.Settings.Constants.start_session;
import static buddiapp.com.Settings.Constants.trainee_Data;

public class RequestActivity extends AppCompatActivity {
    JSONObject sessionData;
    TextView title;

    Button accept, reject;

    @Override
    protected void onResume() {
        super.onResume();

        Handler h = new Handler(Looper.getMainLooper());
        long delayInMilliseconds = 30000;
        h.postDelayed(new Runnable() {
            public void run() {

                finish();
            }
        }, delayInMilliseconds);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);


        title = (TextView) findViewById(R.id.title);
        title.setText(getIntent().getStringExtra("title"));
        accept = (Button) findViewById(R.id.accept);
        reject = (Button) findViewById(R.id.reject);


        try {
            sessionData = new JSONObject(getIntent().getStringExtra("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new acceptDecline("accept").execute();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new acceptDecline("reject").execute();
            }
        });

    }


    class acceptDecline extends AsyncTask<String, String, String> {

        String type;

        public acceptDecline(String type) {

            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(RequestActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                sessionData.put("trainer_id", PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String res;
            if (type.equals("accept"))
                res = NetworkCalls.POST(Urls.getAcceptSelectionURL(), sessionData.toString());
            else
                res = NetworkCalls.POST(Urls.getDeclineSelectionURL(), sessionData.toString());


            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

              NotificationUtils.clearNotifications(getApplicationContext());
            try {
                JSONObject obj = new JSONObject(res);
                if (obj.getInt("status") == 1) {


                    CommonCall.hideLoader();
//
                    if (type.equals("accept")) {
                        DatabaseHandler db;
                        db = new DatabaseHandler(getApplicationContext());

                        JSONObject jsonObject = obj.getJSONObject("data");
                        PreferencesUtils.saveData(Constants.trainee_id, jsonObject.getString("trainee_id"), getApplicationContext());
                        PreferencesUtils.saveData(trainee_Data, jsonObject.toString(), getApplicationContext());

                        PreferencesUtils.saveData(start_session, "true", getApplicationContext());

                        if(PreferencesUtils.getData(Constants.twitterShare, getApplicationContext(),"").equals("true")){
                            CommonCall.postTwitter("I have booked a "+jsonObject.getString("training_time")+" Minutes "
                                    + db.getCatName(jsonObject.getString("cat_id")) +" training session with "+jsonObject.getJSONObject("trainee_details").getString("trainee_first_name")+
                                    " "+jsonObject.getJSONObject("trainee_details").getString("trainee_last_name")+" at "+
                                    jsonObject.getString("pick_location"));
                            CommonCall.PrintLog("tweet","Success");
                        }
                        if(PreferencesUtils.getData(Constants.facebookShare, getApplicationContext(),"").equals("true")){
                            CommonCall.postFacebook("I have booked a "+jsonObject.getString("training_time")+" Minutes "
                                    + db.getCatName(jsonObject.getString("cat_id")) +" training session with "+jsonObject.getJSONObject("trainee_details").getString("trainee_first_name")+
                                    " "+jsonObject.getJSONObject("trainee_details").getString("trainee_last_name")+" at "+
                                    jsonObject.getString("pick_location"));
                            CommonCall.PrintLog("fbshare","Success");
                        }

                        Intent intent = new Intent(getApplicationContext(), SessionReady.class);

                        intent.putExtra("TrainerData", jsonObject.toString());
                        startActivity(intent);
                        finish();
                    } else {

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                } else if (obj.getInt("status") == 2) {
                    CommonCall.hideLoader();
                    Toast.makeText(RequestActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();


                } else {
                    CommonCall.hideLoader();


                }
            } catch (JSONException e) {
                e.printStackTrace();
                CommonCall.hideLoader();
                Toast.makeText(RequestActivity.this, Constants.server_error_message, Toast.LENGTH_SHORT).show();

            }

        }
    }
}
