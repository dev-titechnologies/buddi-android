package buddiapp.com.activity.questions;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.R;
import buddiapp.com.utils.CommonCall;

import static buddiapp.com.activity.questions.Question4.sub_cat_selectedID;


public class BeforeVideoActivity extends Activity {


    ImageView back;
Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_video);
        back = (ImageView) findViewById(R.id.back);

        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), VideoUploadActivity.class).putExtra("SUB_CAT",sub_cat_selectedID));

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        finish();
    }
}

