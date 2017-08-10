package buddyapp.com.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
;
import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.utils.AlertDialoge.Alertdialoge;
import buddyapp.com.utils.CircleImageView;

public class TrainerProfileView extends Activity {
    CircleImageView trainerImageView;
    TextView fullname, typeAge, height , weight, gymSubscription, trainingCategory, trainingHistory, coachingHistory, certifications, webUrl;
    ImageView faceBook, instagram, linkedIn, snapChat, twitter, youTube,back;
    String data = "", distance="", latitude="", longitude="",status="",userId, name, trainerId;
    Button booknow;
    Alertdialoge pd;
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
        booknow = (Button) findViewById(R.id.next);
        back = (ImageView) findViewById(R.id.back);
        pd = new Alertdialoge(TrainerProfileView.this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        data = intent.getStringExtra("TrainerData");
        if(data.length()>0)
        loadTrainerProfile();

        booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                pd.show();
                Intent intent= new Intent(getApplicationContext(), SessionReady.class);
                intent.putExtra(Constants.latitude,latitude);
                intent.putExtra(Constants.longitude,longitude);
                intent.putExtra("name",name);
                intent.putExtra("distance",distance);
                startActivity(intent);
                finish();

            }
        });

    }

    private void loadTrainerProfile() {
        try {
            JSONObject trainer_details= new JSONObject(data);
            JSONObject obj = trainer_details.getJSONObject("trainer_details");

            name = obj.getString("first_name") + " "+obj.getString("last_name");
            fullname.setText(obj.getString("first_name") + " "+obj.getString("last_name"));
            typeAge.setText("Trainer("+obj.getString("age")+")");
            height.setText(obj.getString("height"));
            weight.setText(obj.getString("weight"));
            latitude = obj.getString(Constants.latitude);
            longitude = obj.getString(Constants.longitude);
            distance = obj.getString("distance");
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

            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
