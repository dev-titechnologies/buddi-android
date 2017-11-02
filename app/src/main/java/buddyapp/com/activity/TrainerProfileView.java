package buddyapp.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.AlertDialoge.Alertdialoge;
import buddyapp.com.utils.CircleImageView;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;



public class TrainerProfileView extends Activity {
    CircleImageView trainerImageView;
    TextView fullname, typeAge, height, weight, gymSubscription, trainingCategory, trainingHistory, coachingHistory, certifications, webUrl;
    ImageView faceBook, instagram, linkedIn, snapChat, twitter, youTube, back;
    String imageurl = "", distance = "", latitude = "", longitude = "", status = "", userId, name, trainerId;
    Alertdialoge pd;
    TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_profile_view);
//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.mipmap.profile_trainer));
        fullname = (TextView) findViewById(R.id.fullname);
        typeAge = (TextView) findViewById(R.id.type_age);
        height = (TextView) findViewById(R.id.height);
        weight = (TextView) findViewById(R.id.weight);
        gymSubscription = (TextView) findViewById(R.id.gym_subscription);
        trainingCategory = (TextView) findViewById(R.id.training_category);
        trainingHistory = (TextView) findViewById(R.id.training_history);
        coachingHistory = (TextView) findViewById(R.id.coaching_history);
        certifications = (TextView) findViewById(R.id.certifications);
        webUrl = (TextView) findViewById(R.id.web_url);
        faceBook = (ImageView) findViewById(R.id.nav_facebook);
        instagram = (ImageView) findViewById(R.id.nav_instagram);
        linkedIn = (ImageView) findViewById(R.id.nav_linkedin);
        snapChat = (ImageView) findViewById(R.id.nav_snapchat);
        twitter = (ImageView) findViewById(R.id.nav_twitter);
        youTube = (ImageView) findViewById(R.id.nav_youtube);
        back = (ImageView) findViewById(R.id.back);
        pd = new Alertdialoge(TrainerProfileView.this);
        trainerImageView = (CircleImageView) findViewById(R.id.trainerimageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        desc = (TextView) findViewById(R.id.description);
        trainingCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainerCategory.class);
                intent.putExtra("trainerId",PreferencesUtils.getData(Constants.trainer_id, getApplicationContext(), ""));
                intent.putExtra("usertype","trainer");
                startActivity(intent);
            }
        });
//        String text = "You guys are meeting at "+PreferencesUtils.getData(Constants.pickup_location,getApplicationContext(),"")+" to train "+
//        desc.setText();
        /*Intent intent = getIntent();
        data = intent.getStringExtra("TrainerData");
        if(data.length()>0)*/
        new getProfile().execute();

    }

    private void loadTrainerProfile() {
        try {

            JSONObject trainer_Data = new JSONObject(PreferencesUtils.getData(Constants.trainee_Profile_Data, getApplicationContext(), ""));
            JSONObject obj = trainer_Data.getJSONObject("trainer_details");

            name = obj.getString("trainer_first_name") + " " + obj.getString("trainer_last_name");
            fullname.setText(obj.getString("trainer_first_name") + " " + obj.getString("trainer_last_name"));
            typeAge.setText("Trainer(" + obj.getString("trainer_age") + ")");
            height.setText(obj.getString("trainer_height"));
            weight.setText(obj.getString("trainer_weight"));
            latitude = obj.getString("trainer_latitude");
            longitude = obj.getString("trainer_longitude");
            distance = obj.getString("trainer_distance");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

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

    class getProfile extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(TrainerProfileView.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.token, PreferencesUtils.getData(Constants.token, getApplicationContext(), ""));
                reqData.put(Constants.user_type, "trainer");
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.trainer_id, getApplicationContext(), ""));
                response = NetworkCalls.POST(Urls.getTraineeProfileURL(), reqData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt(Constants.status) == 1) {
                    JSONObject jsonObject = obj.getJSONObject("data");
                    PreferencesUtils.saveData(Constants.trainer_Profile_Data, obj.getJSONObject("data").toString(), getApplicationContext());

                    fullname.setText(jsonObject.getString(Constants.fname) + " " + jsonObject.getString(Constants.lname));
                    if (jsonObject.getString("age").equals("null")) {
                        typeAge.setText("Trainer");
                    } else
                        typeAge.setText("Trainer(" + jsonObject.getString("age") + ")");
                    if (jsonObject.getString("height").equals("null")) {
                        height.setVisibility(View.GONE);
                    } else
                        height.setText(jsonObject.getString("height"));
                    if (jsonObject.getString("weight").equals("null")) {
                        weight.setVisibility(View.GONE);
                    } else
                        weight.setText(jsonObject.getString("weight"));

                    imageurl = jsonObject.getString("user_image");

                    String gender = PreferencesUtils.getData(Constants.gender, getApplicationContext(), "");

                    CommonCall.LoadImage(getApplicationContext(), imageurl, trainerImageView, R.drawable.ic_account, R.drawable.ic_account);

                } else if (obj.getInt(Constants.status) == 2) {
                    Toast.makeText(TrainerProfileView.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    //session out
                    CommonCall.sessionout(TrainerProfileView.this);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
