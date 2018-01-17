package buddiapp.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.utils.AlertDialoge.Alertdialoge;
import buddiapp.com.utils.CircleImageView;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;



public class TrainerProfileView extends Activity {
    CircleImageView trainerImageView;
    TextView fullname, typeAge, height, weight, gymSubscription, trainingCategory, trainingHistory, coachingHistory, certifications, webUrl;
    ImageView faceBook, instagram, twitter, youTube, back;
    String imageurl = "", distance = "", latitude = "", longitude = "", status = "", userId, name, trainerId;
    Alertdialoge pd;
    TextView desc;
    String facebookUrl="", instagramUrl="", twitterUrl="", youtubeUrl="";
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
        faceBook = (ImageView) findViewById(R.id.nav_facebook);
        instagram = (ImageView) findViewById(R.id.nav_instagram);
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

        faceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(facebookUrl.length()>5){
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(facebookUrl));
                startActivity(i);
                }else{
                    Toast.makeText(TrainerProfileView.this, "No facebook profile linked", Toast.LENGTH_SHORT).show();
                }
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(instagramUrl.length()>5){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(instagramUrl));
                startActivity(i);
            }else{
                Toast.makeText(TrainerProfileView.this, "No instagram profile linked", Toast.LENGTH_SHORT).show();
            }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(twitterUrl.length()>5){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(twitterUrl));
                startActivity(i);
                }else{
                    Toast.makeText(TrainerProfileView.this, "No twitter profile linked", Toast.LENGTH_SHORT).show();
                }
            }
        });
        youTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(youtubeUrl.length()>5){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(youtubeUrl));
                startActivity(i);
                }else{
                    Toast.makeText(TrainerProfileView.this, "No youtube profile linked", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                    {weight.setText(jsonObject.getString("weight"));}

                    if(!jsonObject.getString("social_media_links").equals("null") && jsonObject.getString("social_media_links").length()>0 )
                    { JSONArray jsonArray = new JSONArray(jsonObject.getString("social_media_links"));
                      JSONObject jsonObject1 = new JSONObject(String.valueOf(jsonArray.getJSONObject(0)));


                    if (jsonObject1.getJSONObject(Constants.social_media_links).getString("facebook").equals("null")||jsonObject1.getJSONObject(Constants.social_media_links).getString("facebook").length()==0) {
                        facebookUrl = "";
                    } else {
                        facebookUrl = jsonObject1.getJSONObject(Constants.social_media_links).getString("facebook");
                    }
                    if (jsonObject1.getJSONObject(Constants.social_media_links).getString("instagram").equals("null")||jsonObject1.getJSONObject(Constants.social_media_links).getString("instagram").length()==0) {
                        instagramUrl = "";
                    } else {
                        instagramUrl = jsonObject1.getJSONObject(Constants.social_media_links).getString("instagram");
                    }
                    if (jsonObject1.getJSONObject(Constants.social_media_links).getString("twitter").equals("null")||jsonObject1.getJSONObject(Constants.social_media_links).getString("twitter").length()==0) {
                        twitterUrl = "";
                    } else {
                        twitterUrl = jsonObject1.getJSONObject(Constants.social_media_links).getString("twitter");
                    }
                    if (jsonObject1.getJSONObject(Constants.social_media_links).getString("youtube").equals("null")||jsonObject1.getJSONObject(Constants.social_media_links).getString("youtube").length()==0) {
                        youtubeUrl = "";
                    } else {
                        youtubeUrl = jsonObject1.getJSONObject(Constants.social_media_links).getString("youtube");
                    }
                    }

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
