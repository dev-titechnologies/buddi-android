package buddiapp.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.ChooseCategory;
import buddiapp.com.activity.HomeActivity;
import buddiapp.com.activity.RequestActivity;
import buddiapp.com.activity.SessionReady;
import buddiapp.com.activity.WelcomeActivity;
import buddiapp.com.activity.questions.DoneActivity;

import static buddiapp.com.Settings.Constants.start_session;
import static buddiapp.com.Settings.Constants.trainer_Data;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();


        Log.e("launcher activity", getIntent().getStringExtra("type") + "");
        if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("4")) {

            startActivity(new Intent(getApplicationContext(), SessionReady.class).putExtra("push_session", "4"));

            finish();
        } else if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("1")) {


            JSONObject data = null;
            try {
                data = new JSONObject(getIntent().getStringExtra("data"));


                PreferencesUtils.saveData(Constants.trainer_id, data.getJSONObject("trainer_details").getString("trainer_id"), getApplicationContext());
                PreferencesUtils.saveData(trainer_Data, data.toString(), getApplicationContext());

                PreferencesUtils.saveData(start_session, "true", getApplicationContext());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent resultIntent = new Intent(getApplicationContext(), SessionReady.class);
            resultIntent.putExtra("message", data.toString());


            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(resultIntent);

        } else if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("3")) {

            startActivity(new Intent(getApplicationContext(), SessionReady.class).putExtra("push_session", "3"));

            finish();
        } else if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("2")) {

            startActivity(new Intent(getApplicationContext(), SessionReady.class).putExtra("push_session", "2"));

            finish();
        } else if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("6")) {


            JSONObject data = null;
            try {
                data = new JSONObject(getIntent().getStringExtra("data"));


                startActivity(new Intent(getApplicationContext(), SessionReady.class)
                        .putExtra("push_session", "6").putExtra("extend_time", data.getString("extend_time")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            finish();
        } else if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equals("5")) {

            JSONObject data = null;
            try {
                data = new JSONObject(getIntent().getStringExtra("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Intent resultIntent = new Intent(getApplicationContext(), RequestActivity.class);
            resultIntent.putExtra("message", data.toString());
            try {
                resultIntent.putExtra("title", data.getString("noti_title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(resultIntent);
        } else
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (PreferencesUtils.getData(Constants.token, getApplicationContext(), "").length() > 1) {

                        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals(Constants.trainer)) {
                            try {

                                if (new JSONArray(PreferencesUtils.getData(Constants.approved, getApplicationContext(), "[]")).length() == 0) {
                                    if (new JSONArray(PreferencesUtils.getData(Constants.pending, getApplicationContext(), "[]")).length() == 0) {
                                        startActivity(new Intent(getApplicationContext(), ChooseCategory.class));
                                        finish();
                                    } else {
                                        startActivity(new Intent(getApplicationContext(), DoneActivity.class));
                                        finish();

                                    }
                                } else {


                                    if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {

//                                    startService(new Intent(getApplicationContext(), Timer_Service.class));

                                        startActivity(new Intent(getApplicationContext(), SessionReady.class));
                                        finish();
                                    } else

                                    {
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        finish();

                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {

/*
*
*
* trainee =>
*
* */
                            if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {

//                            startService(new Intent(getApplicationContext(), Timer_Service.class));

                                startActivity(new Intent(getApplicationContext(), SessionReady.class));
                                finish();
                            } else {
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();

                            }


                        }

                    } else
                        startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                    finish();

                }
            }, 3000);

    }
}
